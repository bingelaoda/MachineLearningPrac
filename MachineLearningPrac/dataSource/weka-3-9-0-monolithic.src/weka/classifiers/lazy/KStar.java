/*   1:    */ package weka.classifiers.lazy;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.AbstractClassifier;
/*   8:    */ import weka.classifiers.UpdateableClassifier;
/*   9:    */ import weka.classifiers.lazy.kstar.KStarCache;
/*  10:    */ import weka.classifiers.lazy.kstar.KStarConstants;
/*  11:    */ import weka.classifiers.lazy.kstar.KStarNominalAttribute;
/*  12:    */ import weka.classifiers.lazy.kstar.KStarNumericAttribute;
/*  13:    */ import weka.core.Attribute;
/*  14:    */ import weka.core.Capabilities;
/*  15:    */ import weka.core.Capabilities.Capability;
/*  16:    */ import weka.core.Instance;
/*  17:    */ import weka.core.Instances;
/*  18:    */ import weka.core.Option;
/*  19:    */ import weka.core.RevisionUtils;
/*  20:    */ import weka.core.SelectedTag;
/*  21:    */ import weka.core.Tag;
/*  22:    */ import weka.core.TechnicalInformation;
/*  23:    */ import weka.core.TechnicalInformation.Field;
/*  24:    */ import weka.core.TechnicalInformation.Type;
/*  25:    */ import weka.core.TechnicalInformationHandler;
/*  26:    */ import weka.core.Utils;
/*  27:    */ 
/*  28:    */ public class KStar
/*  29:    */   extends AbstractClassifier
/*  30:    */   implements KStarConstants, UpdateableClassifier, TechnicalInformationHandler
/*  31:    */ {
/*  32:    */   static final long serialVersionUID = 332458330800479083L;
/*  33:    */   protected Instances m_Train;
/*  34:    */   protected int m_NumInstances;
/*  35:    */   protected int m_NumClasses;
/*  36:    */   protected int m_NumAttributes;
/*  37:    */   protected int m_ClassType;
/*  38:    */   protected int[][] m_RandClassCols;
/*  39:122 */   protected int m_ComputeRandomCols = 1;
/*  40:125 */   protected int m_InitFlag = 1;
/*  41:    */   protected KStarCache[] m_Cache;
/*  42:134 */   protected int m_MissingMode = 4;
/*  43:137 */   protected int m_BlendMethod = 1;
/*  44:140 */   protected int m_GlobalBlend = 20;
/*  45:143 */   public static final Tag[] TAGS_MISSING = { new Tag(1, "Ignore the instances with missing values"), new Tag(2, "Treat missing values as maximally different"), new Tag(3, "Normalize over the attributes"), new Tag(4, "Average column entropy curves") };
/*  46:    */   
/*  47:    */   public String globalInfo()
/*  48:    */   {
/*  49:157 */     return "K* is an instance-based classifier, that is the class of a test instance is based upon the class of those training instances similar to it, as determined by some similarity function.  It differs from other instance-based learners in that it uses an entropy-based distance function.\n\nFor more information on K*, see\n\n" + getTechnicalInformation().toString();
/*  50:    */   }
/*  51:    */   
/*  52:    */   public TechnicalInformation getTechnicalInformation()
/*  53:    */   {
/*  54:176 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  55:177 */     result.setValue(TechnicalInformation.Field.AUTHOR, "John G. Cleary and Leonard E. Trigg");
/*  56:178 */     result.setValue(TechnicalInformation.Field.TITLE, "K*: An Instance-based Learner Using an Entropic Distance Measure");
/*  57:179 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "12th International Conference on Machine Learning");
/*  58:180 */     result.setValue(TechnicalInformation.Field.YEAR, "1995");
/*  59:181 */     result.setValue(TechnicalInformation.Field.PAGES, "108-114");
/*  60:    */     
/*  61:183 */     return result;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public Capabilities getCapabilities()
/*  65:    */   {
/*  66:192 */     Capabilities result = super.getCapabilities();
/*  67:193 */     result.disableAll();
/*  68:    */     
/*  69:    */ 
/*  70:196 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  71:197 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  72:198 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  73:199 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  74:    */     
/*  75:    */ 
/*  76:202 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  77:203 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  78:204 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  79:205 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  80:    */     
/*  81:    */ 
/*  82:208 */     result.setMinimumNumberInstances(0);
/*  83:    */     
/*  84:210 */     return result;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void buildClassifier(Instances instances)
/*  88:    */     throws Exception
/*  89:    */   {
/*  90:222 */     getCapabilities().testWithFail(instances);
/*  91:    */     
/*  92:    */ 
/*  93:225 */     instances = new Instances(instances);
/*  94:226 */     instances.deleteWithMissingClass();
/*  95:    */     
/*  96:228 */     this.m_Train = new Instances(instances, 0, instances.numInstances());
/*  97:    */     
/*  98:    */ 
/*  99:231 */     init_m_Attributes();
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void updateClassifier(Instance instance)
/* 103:    */     throws Exception
/* 104:    */   {
/* 105:242 */     if (!this.m_Train.equalHeaders(instance.dataset())) {
/* 106:243 */       throw new Exception("Incompatible instance types\n" + this.m_Train.equalHeadersMsg(instance.dataset()));
/* 107:    */     }
/* 108:244 */     if (instance.classIsMissing()) {
/* 109:245 */       return;
/* 110:    */     }
/* 111:246 */     this.m_Train.add(instance);
/* 112:    */     
/* 113:248 */     update_m_Attributes();
/* 114:    */   }
/* 115:    */   
/* 116:    */   public double[] distributionForInstance(Instance instance)
/* 117:    */     throws Exception
/* 118:    */   {
/* 119:260 */     double transProb = 0.0D;double temp = 0.0D;
/* 120:261 */     double[] classProbability = new double[this.m_NumClasses];
/* 121:262 */     double[] predictedValue = new double[1];
/* 122:265 */     for (int i = 0; i < classProbability.length; i++) {
/* 123:266 */       classProbability[i] = 0.0D;
/* 124:    */     }
/* 125:268 */     predictedValue[0] = 0.0D;
/* 126:269 */     if (this.m_InitFlag == 1)
/* 127:    */     {
/* 128:272 */       if (this.m_BlendMethod == 2) {
/* 129:273 */         generateRandomClassColomns();
/* 130:    */       }
/* 131:275 */       this.m_Cache = new KStarCache[this.m_NumAttributes];
/* 132:276 */       for (int i = 0; i < this.m_NumAttributes; i++) {
/* 133:277 */         this.m_Cache[i] = new KStarCache();
/* 134:    */       }
/* 135:279 */       this.m_InitFlag = 0;
/* 136:    */     }
/* 137:284 */     Enumeration<Instance> enu = this.m_Train.enumerateInstances();
/* 138:285 */     while (enu.hasMoreElements())
/* 139:    */     {
/* 140:286 */       Instance trainInstance = (Instance)enu.nextElement();
/* 141:287 */       transProb = instanceTransformationProbability(instance, trainInstance);
/* 142:288 */       switch (this.m_ClassType)
/* 143:    */       {
/* 144:    */       case 1: 
/* 145:291 */         classProbability[((int)trainInstance.classValue())] += transProb;
/* 146:292 */         break;
/* 147:    */       case 0: 
/* 148:294 */         predictedValue[0] += transProb * trainInstance.classValue();
/* 149:295 */         temp += transProb;
/* 150:    */       }
/* 151:    */     }
/* 152:299 */     if (this.m_ClassType == 1)
/* 153:    */     {
/* 154:300 */       double sum = Utils.sum(classProbability);
/* 155:301 */       if (sum <= 0.0D) {
/* 156:302 */         for (int i = 0; i < classProbability.length; i++) {
/* 157:303 */           classProbability[i] = (1.0D / this.m_NumClasses);
/* 158:    */         }
/* 159:    */       } else {
/* 160:304 */         Utils.normalize(classProbability, sum);
/* 161:    */       }
/* 162:305 */       return classProbability;
/* 163:    */     }
/* 164:308 */     predictedValue[0] = (temp != 0.0D ? predictedValue[0] / temp : 0.0D);
/* 165:309 */     return predictedValue;
/* 166:    */   }
/* 167:    */   
/* 168:    */   private double instanceTransformationProbability(Instance first, Instance second)
/* 169:    */   {
/* 170:325 */     double transProb = 1.0D;
/* 171:326 */     int numMissAttr = 0;
/* 172:327 */     for (int i = 0; i < this.m_NumAttributes; i++) {
/* 173:328 */       if (i != this.m_Train.classIndex()) {
/* 174:331 */         if (first.isMissing(i))
/* 175:    */         {
/* 176:332 */           numMissAttr++;
/* 177:    */         }
/* 178:    */         else
/* 179:    */         {
/* 180:335 */           transProb *= attrTransProb(first, second, i);
/* 181:337 */           if (numMissAttr != this.m_NumAttributes) {
/* 182:338 */             transProb = Math.pow(transProb, this.m_NumAttributes / (this.m_NumAttributes - numMissAttr));
/* 183:    */           } else {
/* 184:342 */             transProb = 0.0D;
/* 185:    */           }
/* 186:    */         }
/* 187:    */       }
/* 188:    */     }
/* 189:346 */     return transProb / this.m_NumInstances;
/* 190:    */   }
/* 191:    */   
/* 192:    */   private double attrTransProb(Instance first, Instance second, int col)
/* 193:    */   {
/* 194:360 */     double transProb = 0.0D;
/* 195:363 */     switch (this.m_Train.attribute(col).type())
/* 196:    */     {
/* 197:    */     case 1: 
/* 198:366 */       KStarNominalAttribute ksNominalAttr = new KStarNominalAttribute(first, second, col, this.m_Train, this.m_RandClassCols, this.m_Cache[col]);
/* 199:    */       
/* 200:    */ 
/* 201:369 */       ksNominalAttr.setOptions(this.m_MissingMode, this.m_BlendMethod, this.m_GlobalBlend);
/* 202:370 */       transProb = ksNominalAttr.transProb();
/* 203:371 */       ksNominalAttr = null;
/* 204:372 */       break;
/* 205:    */     case 0: 
/* 206:375 */       KStarNumericAttribute ksNumericAttr = new KStarNumericAttribute(first, second, col, this.m_Train, this.m_RandClassCols, this.m_Cache[col]);
/* 207:    */       
/* 208:    */ 
/* 209:378 */       ksNumericAttr.setOptions(this.m_MissingMode, this.m_BlendMethod, this.m_GlobalBlend);
/* 210:379 */       transProb = ksNumericAttr.transProb();
/* 211:380 */       ksNumericAttr = null;
/* 212:    */     }
/* 213:383 */     return transProb;
/* 214:    */   }
/* 215:    */   
/* 216:    */   public String missingModeTipText()
/* 217:    */   {
/* 218:392 */     return "Determines how missing attribute values are treated.";
/* 219:    */   }
/* 220:    */   
/* 221:    */   public SelectedTag getMissingMode()
/* 222:    */   {
/* 223:403 */     return new SelectedTag(this.m_MissingMode, TAGS_MISSING);
/* 224:    */   }
/* 225:    */   
/* 226:    */   public void setMissingMode(SelectedTag newMode)
/* 227:    */   {
/* 228:414 */     if (newMode.getTags() == TAGS_MISSING) {
/* 229:415 */       this.m_MissingMode = newMode.getSelectedTag().getID();
/* 230:    */     }
/* 231:    */   }
/* 232:    */   
/* 233:    */   public Enumeration<Option> listOptions()
/* 234:    */   {
/* 235:426 */     Vector<Option> optVector = new Vector(3);
/* 236:427 */     optVector.addElement(new Option("\tManual blend setting (default 20%)\n", "B", 1, "-B <num>"));
/* 237:    */     
/* 238:    */ 
/* 239:430 */     optVector.addElement(new Option("\tEnable entropic auto-blend setting (symbolic class only)\n", "E", 0, "-E"));
/* 240:    */     
/* 241:    */ 
/* 242:433 */     optVector.addElement(new Option("\tSpecify the missing value treatment mode (default a)\n\tValid options are: a(verage), d(elete), m(axdiff), n(ormal)\n", "M", 1, "-M <char>"));
/* 243:    */     
/* 244:    */ 
/* 245:    */ 
/* 246:    */ 
/* 247:438 */     optVector.addAll(Collections.list(super.listOptions()));
/* 248:    */     
/* 249:440 */     return optVector.elements();
/* 250:    */   }
/* 251:    */   
/* 252:    */   public String globalBlendTipText()
/* 253:    */   {
/* 254:449 */     return "The parameter for global blending. Values are restricted to [0,100].";
/* 255:    */   }
/* 256:    */   
/* 257:    */   public void setGlobalBlend(int b)
/* 258:    */   {
/* 259:457 */     this.m_GlobalBlend = b;
/* 260:458 */     if (this.m_GlobalBlend > 100) {
/* 261:459 */       this.m_GlobalBlend = 100;
/* 262:    */     }
/* 263:461 */     if (this.m_GlobalBlend < 0) {
/* 264:462 */       this.m_GlobalBlend = 0;
/* 265:    */     }
/* 266:    */   }
/* 267:    */   
/* 268:    */   public int getGlobalBlend()
/* 269:    */   {
/* 270:471 */     return this.m_GlobalBlend;
/* 271:    */   }
/* 272:    */   
/* 273:    */   public String entropicAutoBlendTipText()
/* 274:    */   {
/* 275:480 */     return "Whether entropy-based blending is to be used.";
/* 276:    */   }
/* 277:    */   
/* 278:    */   public void setEntropicAutoBlend(boolean e)
/* 279:    */   {
/* 280:488 */     if (e) {
/* 281:489 */       this.m_BlendMethod = 2;
/* 282:    */     } else {
/* 283:491 */       this.m_BlendMethod = 1;
/* 284:    */     }
/* 285:    */   }
/* 286:    */   
/* 287:    */   public boolean getEntropicAutoBlend()
/* 288:    */   {
/* 289:500 */     if (this.m_BlendMethod == 2) {
/* 290:501 */       return true;
/* 291:    */     }
/* 292:504 */     return false;
/* 293:    */   }
/* 294:    */   
/* 295:    */   public void setOptions(String[] options)
/* 296:    */     throws Exception
/* 297:    */   {
/* 298:533 */     String blendStr = Utils.getOption('B', options);
/* 299:534 */     if (blendStr.length() != 0) {
/* 300:535 */       setGlobalBlend(Integer.parseInt(blendStr));
/* 301:    */     }
/* 302:538 */     setEntropicAutoBlend(Utils.getFlag('E', options));
/* 303:    */     
/* 304:540 */     String missingModeStr = Utils.getOption('M', options);
/* 305:541 */     if (missingModeStr.length() != 0) {
/* 306:542 */       switch (missingModeStr.charAt(0))
/* 307:    */       {
/* 308:    */       case 'a': 
/* 309:544 */         setMissingMode(new SelectedTag(4, TAGS_MISSING));
/* 310:545 */         break;
/* 311:    */       case 'd': 
/* 312:547 */         setMissingMode(new SelectedTag(1, TAGS_MISSING));
/* 313:548 */         break;
/* 314:    */       case 'm': 
/* 315:550 */         setMissingMode(new SelectedTag(2, TAGS_MISSING));
/* 316:551 */         break;
/* 317:    */       case 'n': 
/* 318:553 */         setMissingMode(new SelectedTag(3, TAGS_MISSING));
/* 319:554 */         break;
/* 320:    */       default: 
/* 321:556 */         setMissingMode(new SelectedTag(4, TAGS_MISSING));
/* 322:    */       }
/* 323:    */     }
/* 324:560 */     super.setOptions(options);
/* 325:    */     
/* 326:562 */     Utils.checkForRemainingOptions(options);
/* 327:    */   }
/* 328:    */   
/* 329:    */   public String[] getOptions()
/* 330:    */   {
/* 331:573 */     Vector<String> options = new Vector();
/* 332:    */     
/* 333:575 */     options.add("-B");
/* 334:576 */     options.add("" + this.m_GlobalBlend);
/* 335:578 */     if (getEntropicAutoBlend()) {
/* 336:579 */       options.add("-E");
/* 337:    */     }
/* 338:582 */     options.add("-M");
/* 339:583 */     if (this.m_MissingMode == 4) {
/* 340:584 */       options.add("a");
/* 341:586 */     } else if (this.m_MissingMode == 1) {
/* 342:587 */       options.add("d");
/* 343:589 */     } else if (this.m_MissingMode == 2) {
/* 344:590 */       options.add("m");
/* 345:592 */     } else if (this.m_MissingMode == 3) {
/* 346:593 */       options.add("n");
/* 347:    */     }
/* 348:596 */     Collections.addAll(options, super.getOptions());
/* 349:    */     
/* 350:598 */     return (String[])options.toArray(new String[0]);
/* 351:    */   }
/* 352:    */   
/* 353:    */   public String toString()
/* 354:    */   {
/* 355:607 */     StringBuffer st = new StringBuffer();
/* 356:608 */     st.append("KStar Beta Verion (0.1b).\nCopyright (c) 1995-97 by Len Trigg (trigg@cs.waikato.ac.nz).\nJava port to Weka by Abdelaziz Mahoui (am14@cs.waikato.ac.nz).\n\nKStar options : ");
/* 357:    */     
/* 358:    */ 
/* 359:    */ 
/* 360:612 */     String[] ops = getOptions();
/* 361:613 */     for (int i = 0; i < ops.length; i++) {
/* 362:614 */       st.append(ops[i] + ' ');
/* 363:    */     }
/* 364:616 */     return st.toString();
/* 365:    */   }
/* 366:    */   
/* 367:    */   public static void main(String[] argv)
/* 368:    */   {
/* 369:625 */     runClassifier(new KStar(), argv);
/* 370:    */   }
/* 371:    */   
/* 372:    */   private void init_m_Attributes()
/* 373:    */   {
/* 374:    */     try
/* 375:    */     {
/* 376:633 */       this.m_NumInstances = this.m_Train.numInstances();
/* 377:634 */       this.m_NumClasses = this.m_Train.numClasses();
/* 378:635 */       this.m_NumAttributes = this.m_Train.numAttributes();
/* 379:636 */       this.m_ClassType = this.m_Train.classAttribute().type();
/* 380:637 */       this.m_InitFlag = 1;
/* 381:    */     }
/* 382:    */     catch (Exception e)
/* 383:    */     {
/* 384:639 */       e.printStackTrace();
/* 385:    */     }
/* 386:    */   }
/* 387:    */   
/* 388:    */   private void update_m_Attributes()
/* 389:    */   {
/* 390:647 */     this.m_NumInstances = this.m_Train.numInstances();
/* 391:648 */     this.m_InitFlag = 1;
/* 392:    */   }
/* 393:    */   
/* 394:    */   private void generateRandomClassColomns()
/* 395:    */   {
/* 396:657 */     Random generator = new Random(42L);
/* 397:    */     
/* 398:659 */     this.m_RandClassCols = new int[6][];
/* 399:660 */     int[] classvals = classValues();
/* 400:661 */     for (int i = 0; i < 5; i++) {
/* 401:663 */       this.m_RandClassCols[i] = randomize(classvals, generator);
/* 402:    */     }
/* 403:666 */     this.m_RandClassCols[5] = classvals;
/* 404:    */   }
/* 405:    */   
/* 406:    */   private int[] classValues()
/* 407:    */   {
/* 408:677 */     int[] classval = new int[this.m_NumInstances];
/* 409:678 */     for (int i = 0; i < this.m_NumInstances; i++) {
/* 410:    */       try
/* 411:    */       {
/* 412:680 */         classval[i] = ((int)this.m_Train.instance(i).classValue());
/* 413:    */       }
/* 414:    */       catch (Exception ex)
/* 415:    */       {
/* 416:682 */         ex.printStackTrace();
/* 417:    */       }
/* 418:    */     }
/* 419:685 */     return classval;
/* 420:    */   }
/* 421:    */   
/* 422:    */   private int[] randomize(int[] array, Random generator)
/* 423:    */   {
/* 424:699 */     int[] newArray = new int[array.length];
/* 425:700 */     System.arraycopy(array, 0, newArray, 0, array.length);
/* 426:701 */     for (int j = newArray.length - 1; j > 0; j--)
/* 427:    */     {
/* 428:702 */       int index = (int)(generator.nextDouble() * j);
/* 429:703 */       int temp = newArray[j];
/* 430:704 */       newArray[j] = newArray[index];
/* 431:705 */       newArray[index] = temp;
/* 432:    */     }
/* 433:707 */     return newArray;
/* 434:    */   }
/* 435:    */   
/* 436:    */   public String getRevision()
/* 437:    */   {
/* 438:716 */     return RevisionUtils.extract("$Revision: 10141 $");
/* 439:    */   }
/* 440:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.lazy.KStar
 * JD-Core Version:    0.7.0.1
 */