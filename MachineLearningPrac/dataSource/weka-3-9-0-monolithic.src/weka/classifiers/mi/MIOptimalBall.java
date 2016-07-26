/*   1:    */ package weka.classifiers.mi;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.AbstractClassifier;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.MultiInstanceCapabilitiesHandler;
/*  14:    */ import weka.core.Option;
/*  15:    */ import weka.core.OptionHandler;
/*  16:    */ import weka.core.RevisionUtils;
/*  17:    */ import weka.core.SelectedTag;
/*  18:    */ import weka.core.Tag;
/*  19:    */ import weka.core.TechnicalInformation;
/*  20:    */ import weka.core.TechnicalInformation.Field;
/*  21:    */ import weka.core.TechnicalInformation.Type;
/*  22:    */ import weka.core.TechnicalInformationHandler;
/*  23:    */ import weka.core.Utils;
/*  24:    */ import weka.core.WeightedInstancesHandler;
/*  25:    */ import weka.core.matrix.DoubleVector;
/*  26:    */ import weka.filters.Filter;
/*  27:    */ import weka.filters.unsupervised.attribute.MultiInstanceToPropositional;
/*  28:    */ import weka.filters.unsupervised.attribute.Normalize;
/*  29:    */ import weka.filters.unsupervised.attribute.PropositionalToMultiInstance;
/*  30:    */ import weka.filters.unsupervised.attribute.Standardize;
/*  31:    */ 
/*  32:    */ public class MIOptimalBall
/*  33:    */   extends AbstractClassifier
/*  34:    */   implements OptionHandler, WeightedInstancesHandler, MultiInstanceCapabilitiesHandler, TechnicalInformationHandler
/*  35:    */ {
/*  36:    */   static final long serialVersionUID = -6465750129576777254L;
/*  37:    */   protected double[] m_Center;
/*  38:    */   protected double m_Radius;
/*  39:    */   protected double[][][] m_Distance;
/*  40:114 */   protected Filter m_Filter = null;
/*  41:117 */   protected int m_filterType = 0;
/*  42:    */   public static final int FILTER_NORMALIZE = 0;
/*  43:    */   public static final int FILTER_STANDARDIZE = 1;
/*  44:    */   public static final int FILTER_NONE = 2;
/*  45:126 */   public static final Tag[] TAGS_FILTER = { new Tag(0, "Normalize training data"), new Tag(1, "Standardize training data"), new Tag(2, "No normalization/standardization") };
/*  46:132 */   protected MultiInstanceToPropositional m_ConvertToSI = new MultiInstanceToPropositional();
/*  47:135 */   protected PropositionalToMultiInstance m_ConvertToMI = new PropositionalToMultiInstance();
/*  48:    */   
/*  49:    */   public String globalInfo()
/*  50:    */   {
/*  51:144 */     return "This classifier tries to find a suitable ball in the multiple-instance space, with a certain data point in the instance space as a ball center. The possible ball center is a certain instance in a positive bag. The possible radiuses are those which can achieve the highest classification accuracy. The model selects the maximum radius as the radius of the optimal ball.\n\nFor more information about this algorithm, see:\n\n" + getTechnicalInformation().toString();
/*  52:    */   }
/*  53:    */   
/*  54:    */   public TechnicalInformation getTechnicalInformation()
/*  55:    */   {
/*  56:165 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  57:166 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Peter Auer and Ronald Ortner");
/*  58:167 */     result.setValue(TechnicalInformation.Field.TITLE, "A Boosting Approach to Multiple Instance Learning");
/*  59:    */     
/*  60:169 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "15th European Conference on Machine Learning");
/*  61:    */     
/*  62:171 */     result.setValue(TechnicalInformation.Field.YEAR, "2004");
/*  63:172 */     result.setValue(TechnicalInformation.Field.PAGES, "63-74");
/*  64:173 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*  65:174 */     result.setValue(TechnicalInformation.Field.NOTE, "LNAI 3201");
/*  66:    */     
/*  67:176 */     return result;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public Capabilities getCapabilities()
/*  71:    */   {
/*  72:186 */     Capabilities result = super.getCapabilities();
/*  73:187 */     result.disableAll();
/*  74:    */     
/*  75:    */ 
/*  76:190 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  77:191 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/*  78:    */     
/*  79:    */ 
/*  80:194 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/*  81:195 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  82:    */     
/*  83:    */ 
/*  84:198 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/*  85:    */     
/*  86:200 */     return result;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public Capabilities getMultiInstanceCapabilities()
/*  90:    */   {
/*  91:212 */     Capabilities result = super.getCapabilities();
/*  92:213 */     result.disableAll();
/*  93:    */     
/*  94:    */ 
/*  95:216 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  96:217 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  97:218 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  98:219 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  99:    */     
/* 100:    */ 
/* 101:222 */     result.disableAllClasses();
/* 102:223 */     result.enable(Capabilities.Capability.NO_CLASS);
/* 103:    */     
/* 104:225 */     return result;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void buildClassifier(Instances data)
/* 108:    */     throws Exception
/* 109:    */   {
/* 110:238 */     getCapabilities().testWithFail(data);
/* 111:    */     
/* 112:    */ 
/* 113:241 */     Instances train = new Instances(data);
/* 114:242 */     train.deleteWithMissingClass();
/* 115:    */     
/* 116:244 */     int numAttributes = train.attribute(1).relation().numAttributes();
/* 117:245 */     this.m_Center = new double[numAttributes];
/* 118:247 */     if (getDebug()) {
/* 119:248 */       System.out.println("Start training ...");
/* 120:    */     }
/* 121:252 */     this.m_ConvertToSI.setInputFormat(train);
/* 122:253 */     train = Filter.useFilter(train, this.m_ConvertToSI);
/* 123:255 */     if (this.m_filterType == 1) {
/* 124:256 */       this.m_Filter = new Standardize();
/* 125:257 */     } else if (this.m_filterType == 0) {
/* 126:258 */       this.m_Filter = new Normalize();
/* 127:    */     } else {
/* 128:260 */       this.m_Filter = null;
/* 129:    */     }
/* 130:263 */     if (this.m_Filter != null)
/* 131:    */     {
/* 132:265 */       this.m_Filter.setInputFormat(train);
/* 133:266 */       train = Filter.useFilter(train, this.m_Filter);
/* 134:    */     }
/* 135:270 */     this.m_ConvertToMI.setInputFormat(train);
/* 136:271 */     train = Filter.useFilter(train, this.m_ConvertToMI);
/* 137:    */     
/* 138:    */ 
/* 139:    */ 
/* 140:    */ 
/* 141:    */ 
/* 142:277 */     calculateDistance(train);
/* 143:    */     
/* 144:    */ 
/* 145:    */ 
/* 146:    */ 
/* 147:    */ 
/* 148:283 */     findRadius(train);
/* 149:285 */     if (getDebug()) {
/* 150:286 */       System.out.println("Finish building optimal ball model");
/* 151:    */     }
/* 152:    */   }
/* 153:    */   
/* 154:    */   public void calculateDistance(Instances train)
/* 155:    */   {
/* 156:299 */     int numBags = train.numInstances();
/* 157:    */     
/* 158:    */ 
/* 159:    */ 
/* 160:303 */     this.m_Distance = new double[numBags][][];
/* 161:304 */     for (int i = 0; i < numBags; i++) {
/* 162:305 */       if (train.instance(i).classValue() == 1.0D)
/* 163:    */       {
/* 164:306 */         int numInstances = train.instance(i).relationalValue(1).numInstances();
/* 165:307 */         this.m_Distance[i] = new double[numInstances][];
/* 166:308 */         for (int j = 0; j < numInstances; j++)
/* 167:    */         {
/* 168:309 */           Instance tempCenter = train.instance(i).relationalValue(1).instance(j);
/* 169:310 */           this.m_Distance[i][j] = new double[numBags];
/* 170:312 */           for (int k = 0; k < numBags; k++) {
/* 171:313 */             if (i == k) {
/* 172:314 */               this.m_Distance[i][j][k] = 0.0D;
/* 173:    */             } else {
/* 174:316 */               this.m_Distance[i][j][k] = minBagDistance(tempCenter, train.instance(k));
/* 175:    */             }
/* 176:    */           }
/* 177:    */         }
/* 178:    */       }
/* 179:    */     }
/* 180:    */   }
/* 181:    */   
/* 182:    */   public double minBagDistance(Instance center, Instance bag)
/* 183:    */   {
/* 184:334 */     double minDistance = 1.7976931348623157E+308D;
/* 185:335 */     Instances temp = bag.relationalValue(1);
/* 186:338 */     for (int i = 0; i < temp.numInstances(); i++)
/* 187:    */     {
/* 188:339 */       double distance = 0.0D;
/* 189:340 */       for (int j = 0; j < center.numAttributes(); j++) {
/* 190:341 */         distance += (center.value(j) - temp.instance(i).value(j)) * (center.value(j) - temp.instance(i).value(j));
/* 191:    */       }
/* 192:345 */       if (minDistance > distance) {
/* 193:346 */         minDistance = distance;
/* 194:    */       }
/* 195:    */     }
/* 196:349 */     return Math.sqrt(minDistance);
/* 197:    */   }
/* 198:    */   
/* 199:    */   public void findRadius(Instances train)
/* 200:    */   {
/* 201:360 */     int highestCount = 0;
/* 202:    */     
/* 203:362 */     int numBags = train.numInstances();
/* 204:364 */     for (int i = 0; i < numBags; i++) {
/* 205:365 */       if (train.instance(i).classValue() == 1.0D)
/* 206:    */       {
/* 207:366 */         int numInstances = train.instance(i).relationalValue(1).numInstances();
/* 208:367 */         for (int j = 0; j < numInstances; j++)
/* 209:    */         {
/* 210:368 */           Instance tempCenter = train.instance(i).relationalValue(1).instance(j);
/* 211:    */           
/* 212:    */ 
/* 213:    */ 
/* 214:    */ 
/* 215:373 */           double[] sortedDistance = sortArray(this.m_Distance[i][j]);
/* 216:376 */           for (int k = 1; k < sortedDistance.length; k++)
/* 217:    */           {
/* 218:377 */             double radius = sortedDistance[k] - (sortedDistance[k] - sortedDistance[(k - 1)]) / 2.0D;
/* 219:    */             
/* 220:    */ 
/* 221:    */ 
/* 222:    */ 
/* 223:382 */             int correctCount = 0;
/* 224:383 */             for (int n = 0; n < numBags; n++)
/* 225:    */             {
/* 226:384 */               double bagDistance = this.m_Distance[i][j][n];
/* 227:385 */               if (((bagDistance <= radius) && (train.instance(n).classValue() == 1.0D)) || ((bagDistance > radius) && (train.instance(n).classValue() == 0.0D))) {
/* 228:387 */                 correctCount = (int)(correctCount + train.instance(n).weight());
/* 229:    */               }
/* 230:    */             }
/* 231:394 */             if ((correctCount > highestCount) || ((correctCount == highestCount) && (radius > this.m_Radius)))
/* 232:    */             {
/* 233:396 */               highestCount = correctCount;
/* 234:397 */               this.m_Radius = radius;
/* 235:398 */               for (int p = 0; p < tempCenter.numAttributes(); p++) {
/* 236:399 */                 this.m_Center[p] = tempCenter.value(p);
/* 237:    */               }
/* 238:    */             }
/* 239:    */           }
/* 240:    */         }
/* 241:    */       }
/* 242:    */     }
/* 243:    */   }
/* 244:    */   
/* 245:    */   public double[] sortArray(double[] distance)
/* 246:    */   {
/* 247:415 */     double[] sorted = new double[distance.length];
/* 248:    */     
/* 249:    */ 
/* 250:418 */     double[] disCopy = new double[distance.length];
/* 251:419 */     for (int i = 0; i < distance.length; i++) {
/* 252:420 */       disCopy[i] = distance[i];
/* 253:    */     }
/* 254:423 */     DoubleVector sortVector = new DoubleVector(disCopy);
/* 255:424 */     sortVector.sort();
/* 256:425 */     sorted = sortVector.getArrayCopy();
/* 257:426 */     return sorted;
/* 258:    */   }
/* 259:    */   
/* 260:    */   public double[] distributionForInstance(Instance newBag)
/* 261:    */     throws Exception
/* 262:    */   {
/* 263:439 */     double[] distribution = new double[2];
/* 264:    */     
/* 265:441 */     distribution[0] = 0.0D;
/* 266:442 */     distribution[1] = 0.0D;
/* 267:    */     
/* 268:444 */     Instances insts = new Instances(newBag.dataset(), 0);
/* 269:445 */     insts.add(newBag);
/* 270:    */     
/* 271:    */ 
/* 272:448 */     insts = Filter.useFilter(insts, this.m_ConvertToSI);
/* 273:449 */     if (this.m_Filter != null) {
/* 274:450 */       insts = Filter.useFilter(insts, this.m_Filter);
/* 275:    */     }
/* 276:454 */     int numInsts = insts.numInstances();
/* 277:455 */     insts.deleteAttributeAt(0);
/* 278:458 */     for (int i = 0; i < numInsts; i++)
/* 279:    */     {
/* 280:459 */       double distance = 0.0D;
/* 281:460 */       for (int j = 0; j < insts.numAttributes() - 1; j++) {
/* 282:461 */         distance += (insts.instance(i).value(j) - this.m_Center[j]) * (insts.instance(i).value(j) - this.m_Center[j]);
/* 283:    */       }
/* 284:465 */       if (distance <= this.m_Radius * this.m_Radius)
/* 285:    */       {
/* 286:467 */         distribution[1] = 1.0D;
/* 287:468 */         break;
/* 288:    */       }
/* 289:    */     }
/* 290:472 */     distribution[0] = (1.0D - distribution[1]);
/* 291:    */     
/* 292:474 */     return distribution;
/* 293:    */   }
/* 294:    */   
/* 295:    */   public Enumeration<Option> listOptions()
/* 296:    */   {
/* 297:485 */     Vector<Option> result = new Vector();
/* 298:    */     
/* 299:487 */     result.addElement(new Option("\tWhether to 0=normalize/1=standardize/2=neither. \n\t(default 0=normalize)", "N", 1, "-N <num>"));
/* 300:    */     
/* 301:    */ 
/* 302:    */ 
/* 303:491 */     result.addAll(Collections.list(super.listOptions()));
/* 304:    */     
/* 305:493 */     return result.elements();
/* 306:    */   }
/* 307:    */   
/* 308:    */   public String[] getOptions()
/* 309:    */   {
/* 310:504 */     Vector<String> result = new Vector();
/* 311:    */     
/* 312:506 */     result.add("-N");
/* 313:507 */     result.add("" + this.m_filterType);
/* 314:    */     
/* 315:509 */     Collections.addAll(result, super.getOptions());
/* 316:    */     
/* 317:511 */     return (String[])result.toArray(new String[result.size()]);
/* 318:    */   }
/* 319:    */   
/* 320:    */   public void setOptions(String[] options)
/* 321:    */     throws Exception
/* 322:    */   {
/* 323:535 */     String nString = Utils.getOption('N', options);
/* 324:536 */     if (nString.length() != 0) {
/* 325:537 */       setFilterType(new SelectedTag(Integer.parseInt(nString), TAGS_FILTER));
/* 326:    */     } else {
/* 327:539 */       setFilterType(new SelectedTag(0, TAGS_FILTER));
/* 328:    */     }
/* 329:542 */     super.setOptions(options);
/* 330:    */     
/* 331:544 */     Utils.checkForRemainingOptions(options);
/* 332:    */   }
/* 333:    */   
/* 334:    */   public String filterTypeTipText()
/* 335:    */   {
/* 336:554 */     return "The filter type for transforming the training data.";
/* 337:    */   }
/* 338:    */   
/* 339:    */   public void setFilterType(SelectedTag newType)
/* 340:    */   {
/* 341:565 */     if (newType.getTags() == TAGS_FILTER) {
/* 342:566 */       this.m_filterType = newType.getSelectedTag().getID();
/* 343:    */     }
/* 344:    */   }
/* 345:    */   
/* 346:    */   public SelectedTag getFilterType()
/* 347:    */   {
/* 348:578 */     return new SelectedTag(this.m_filterType, TAGS_FILTER);
/* 349:    */   }
/* 350:    */   
/* 351:    */   public String getRevision()
/* 352:    */   {
/* 353:588 */     return RevisionUtils.extract("$Revision: 10369 $");
/* 354:    */   }
/* 355:    */   
/* 356:    */   public static void main(String[] argv)
/* 357:    */   {
/* 358:598 */     runClassifier(new MIOptimalBall(), argv);
/* 359:    */   }
/* 360:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.MIOptimalBall
 * JD-Core Version:    0.7.0.1
 */