/*   1:    */ package weka.filters.unsupervised.instance;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.classifiers.AbstractClassifier;
/*   6:    */ import weka.classifiers.Classifier;
/*   7:    */ import weka.classifiers.rules.ZeroR;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.Utils;
/*  16:    */ import weka.filters.Filter;
/*  17:    */ import weka.filters.UnsupervisedFilter;
/*  18:    */ 
/*  19:    */ public class RemoveMisclassified
/*  20:    */   extends Filter
/*  21:    */   implements UnsupervisedFilter, OptionHandler
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = 5469157004717663171L;
/*  24: 98 */   protected Classifier m_cleansingClassifier = new ZeroR();
/*  25:101 */   protected int m_classIndex = -1;
/*  26:107 */   protected int m_numOfCrossValidationFolds = 0;
/*  27:113 */   protected int m_numOfCleansingIterations = 0;
/*  28:116 */   protected double m_numericClassifyThreshold = 0.1D;
/*  29:122 */   protected boolean m_invertMatching = false;
/*  30:125 */   protected boolean m_firstBatchFinished = false;
/*  31:    */   
/*  32:    */   public Capabilities getCapabilities()
/*  33:    */   {
/*  34:    */     Capabilities result;
/*  35:137 */     if (getClassifier() == null)
/*  36:    */     {
/*  37:138 */       Capabilities result = super.getCapabilities();
/*  38:139 */       result.disableAll();
/*  39:    */     }
/*  40:    */     else
/*  41:    */     {
/*  42:141 */       result = getClassifier().getCapabilities();
/*  43:    */     }
/*  44:144 */     result.setMinimumNumberInstances(0);
/*  45:    */     
/*  46:146 */     return result;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public boolean setInputFormat(Instances instanceInfo)
/*  50:    */     throws Exception
/*  51:    */   {
/*  52:161 */     super.setInputFormat(instanceInfo);
/*  53:162 */     setOutputFormat(instanceInfo);
/*  54:163 */     this.m_firstBatchFinished = false;
/*  55:164 */     return true;
/*  56:    */   }
/*  57:    */   
/*  58:    */   private Instances cleanseTrain(Instances data)
/*  59:    */     throws Exception
/*  60:    */   {
/*  61:177 */     Instances buildSet = new Instances(data);
/*  62:178 */     Instances temp = new Instances(data, data.numInstances());
/*  63:179 */     Instances inverseSet = new Instances(data, data.numInstances());
/*  64:180 */     int count = 0;
/*  65:    */     
/*  66:182 */     int iterations = 0;
/*  67:183 */     int classIndex = this.m_classIndex;
/*  68:184 */     if (classIndex < 0) {
/*  69:185 */       classIndex = data.classIndex();
/*  70:    */     }
/*  71:187 */     if (classIndex < 0) {
/*  72:188 */       classIndex = data.numAttributes() - 1;
/*  73:    */     }
/*  74:192 */     while (count != buildSet.numInstances())
/*  75:    */     {
/*  76:195 */       iterations++;
/*  77:196 */       if ((this.m_numOfCleansingIterations > 0) && (iterations > this.m_numOfCleansingIterations)) {
/*  78:    */         break;
/*  79:    */       }
/*  80:202 */       count = buildSet.numInstances();
/*  81:203 */       buildSet.setClassIndex(classIndex);
/*  82:204 */       this.m_cleansingClassifier.buildClassifier(buildSet);
/*  83:    */       
/*  84:206 */       temp = new Instances(buildSet, buildSet.numInstances());
/*  85:209 */       for (int i = 0; i < buildSet.numInstances(); i++)
/*  86:    */       {
/*  87:210 */         Instance inst = buildSet.instance(i);
/*  88:211 */         double ans = this.m_cleansingClassifier.classifyInstance(inst);
/*  89:212 */         if (buildSet.classAttribute().isNumeric())
/*  90:    */         {
/*  91:213 */           if ((ans >= inst.classValue() - this.m_numericClassifyThreshold) && (ans <= inst.classValue() + this.m_numericClassifyThreshold)) {
/*  92:215 */             temp.add(inst);
/*  93:216 */           } else if (this.m_invertMatching) {
/*  94:217 */             inverseSet.add(inst);
/*  95:    */           }
/*  96:    */         }
/*  97:220 */         else if (ans == inst.classValue()) {
/*  98:221 */           temp.add(inst);
/*  99:222 */         } else if (this.m_invertMatching) {
/* 100:223 */           inverseSet.add(inst);
/* 101:    */         }
/* 102:    */       }
/* 103:227 */       buildSet = temp;
/* 104:    */     }
/* 105:230 */     if (this.m_invertMatching)
/* 106:    */     {
/* 107:231 */       inverseSet.setClassIndex(data.classIndex());
/* 108:232 */       return inverseSet;
/* 109:    */     }
/* 110:234 */     buildSet.setClassIndex(data.classIndex());
/* 111:235 */     return buildSet;
/* 112:    */   }
/* 113:    */   
/* 114:    */   private Instances cleanseCross(Instances data)
/* 115:    */     throws Exception
/* 116:    */   {
/* 117:250 */     Instances crossSet = new Instances(data);
/* 118:251 */     Instances temp = new Instances(data, data.numInstances());
/* 119:252 */     Instances inverseSet = new Instances(data, data.numInstances());
/* 120:253 */     int count = 0;
/* 121:    */     
/* 122:255 */     int iterations = 0;
/* 123:256 */     int classIndex = this.m_classIndex;
/* 124:257 */     if (classIndex < 0) {
/* 125:258 */       classIndex = data.classIndex();
/* 126:    */     }
/* 127:260 */     if (classIndex < 0) {
/* 128:261 */       classIndex = data.numAttributes() - 1;
/* 129:    */     }
/* 130:266 */     while ((count != crossSet.numInstances()) && (crossSet.numInstances() >= this.m_numOfCrossValidationFolds))
/* 131:    */     {
/* 132:268 */       count = crossSet.numInstances();
/* 133:    */       
/* 134:    */ 
/* 135:271 */       iterations++;
/* 136:272 */       if ((this.m_numOfCleansingIterations > 0) && (iterations > this.m_numOfCleansingIterations)) {
/* 137:    */         break;
/* 138:    */       }
/* 139:277 */       crossSet.setClassIndex(classIndex);
/* 140:279 */       if (crossSet.classAttribute().isNominal()) {
/* 141:280 */         crossSet.stratify(this.m_numOfCrossValidationFolds);
/* 142:    */       }
/* 143:283 */       temp = new Instances(crossSet, crossSet.numInstances());
/* 144:285 */       for (int fold = 0; fold < this.m_numOfCrossValidationFolds; fold++)
/* 145:    */       {
/* 146:286 */         Instances train = crossSet.trainCV(this.m_numOfCrossValidationFolds, fold);
/* 147:287 */         this.m_cleansingClassifier.buildClassifier(train);
/* 148:288 */         Instances test = crossSet.testCV(this.m_numOfCrossValidationFolds, fold);
/* 149:290 */         for (int i = 0; i < test.numInstances(); i++)
/* 150:    */         {
/* 151:291 */           Instance inst = test.instance(i);
/* 152:292 */           double ans = this.m_cleansingClassifier.classifyInstance(inst);
/* 153:293 */           if (crossSet.classAttribute().isNumeric())
/* 154:    */           {
/* 155:294 */             if ((ans >= inst.classValue() - this.m_numericClassifyThreshold) && (ans <= inst.classValue() + this.m_numericClassifyThreshold)) {
/* 156:296 */               temp.add(inst);
/* 157:297 */             } else if (this.m_invertMatching) {
/* 158:298 */               inverseSet.add(inst);
/* 159:    */             }
/* 160:    */           }
/* 161:301 */           else if (ans == inst.classValue()) {
/* 162:302 */             temp.add(inst);
/* 163:303 */           } else if (this.m_invertMatching) {
/* 164:304 */             inverseSet.add(inst);
/* 165:    */           }
/* 166:    */         }
/* 167:    */       }
/* 168:309 */       crossSet = temp;
/* 169:    */     }
/* 170:312 */     if (this.m_invertMatching)
/* 171:    */     {
/* 172:313 */       inverseSet.setClassIndex(data.classIndex());
/* 173:314 */       return inverseSet;
/* 174:    */     }
/* 175:316 */     crossSet.setClassIndex(data.classIndex());
/* 176:317 */     return crossSet;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public boolean input(Instance instance)
/* 180:    */     throws Exception
/* 181:    */   {
/* 182:334 */     if (inputFormatPeek() == null) {
/* 183:335 */       throw new NullPointerException("No input instance format defined");
/* 184:    */     }
/* 185:338 */     if (this.m_NewBatch)
/* 186:    */     {
/* 187:339 */       resetQueue();
/* 188:340 */       this.m_NewBatch = false;
/* 189:    */     }
/* 190:342 */     if (this.m_firstBatchFinished)
/* 191:    */     {
/* 192:343 */       push(instance);
/* 193:344 */       return true;
/* 194:    */     }
/* 195:346 */     bufferInput(instance);
/* 196:347 */     return false;
/* 197:    */   }
/* 198:    */   
/* 199:    */   public boolean batchFinished()
/* 200:    */     throws Exception
/* 201:    */   {
/* 202:360 */     if (getInputFormat() == null) {
/* 203:361 */       throw new IllegalStateException("No input instance format defined");
/* 204:    */     }
/* 205:364 */     if (!this.m_firstBatchFinished)
/* 206:    */     {
/* 207:    */       Instances filtered;
/* 208:    */       Instances filtered;
/* 209:367 */       if (this.m_numOfCrossValidationFolds < 2) {
/* 210:368 */         filtered = cleanseTrain(getInputFormat());
/* 211:    */       } else {
/* 212:370 */         filtered = cleanseCross(getInputFormat());
/* 213:    */       }
/* 214:373 */       for (int i = 0; i < filtered.numInstances(); i++) {
/* 215:374 */         push(filtered.instance(i), false);
/* 216:    */       }
/* 217:377 */       this.m_firstBatchFinished = true;
/* 218:378 */       flushInput();
/* 219:    */     }
/* 220:380 */     this.m_NewBatch = true;
/* 221:381 */     return numPendingOutput() != 0;
/* 222:    */   }
/* 223:    */   
/* 224:    */   public Enumeration<Option> listOptions()
/* 225:    */   {
/* 226:392 */     Vector<Option> newVector = new Vector(6);
/* 227:    */     
/* 228:394 */     newVector.addElement(new Option("\tFull class name of classifier to use, followed\n\tby scheme options. eg:\n\t\t\"weka.classifiers.bayes.NaiveBayes -D\"\n\t(default: weka.classifiers.rules.ZeroR)", "W", 1, "-W <classifier specification>"));
/* 229:    */     
/* 230:    */ 
/* 231:    */ 
/* 232:    */ 
/* 233:    */ 
/* 234:400 */     newVector.addElement(new Option("\tAttribute on which misclassifications are based.\n\tIf < 0 will use any current set class or default to the last attribute.", "C", 1, "-C <class index>"));
/* 235:    */     
/* 236:    */ 
/* 237:    */ 
/* 238:    */ 
/* 239:405 */     newVector.addElement(new Option("\tThe number of folds to use for cross-validation cleansing.\n\t(<2 = no cross-validation - default).", "F", 1, "-F <number of folds>"));
/* 240:    */     
/* 241:    */ 
/* 242:    */ 
/* 243:409 */     newVector.addElement(new Option("\tThreshold for the max error when predicting numeric class.\n\t(Value should be >= 0, default = 0.1).", "T", 1, "-T <threshold>"));
/* 244:    */     
/* 245:    */ 
/* 246:    */ 
/* 247:    */ 
/* 248:414 */     newVector.addElement(new Option("\tThe maximum number of cleansing iterations to perform.\n\t(<1 = until fully cleansed - default)", "I", 1, "-I"));
/* 249:    */     
/* 250:    */ 
/* 251:417 */     newVector.addElement(new Option("\tInvert the match so that correctly classified instances are discarded.\n", "V", 0, "-V"));
/* 252:    */     
/* 253:    */ 
/* 254:    */ 
/* 255:    */ 
/* 256:422 */     return newVector.elements();
/* 257:    */   }
/* 258:    */   
/* 259:    */   public void setOptions(String[] options)
/* 260:    */     throws Exception
/* 261:    */   {
/* 262:477 */     String classifierString = Utils.getOption('W', options);
/* 263:478 */     if (classifierString.length() == 0) {
/* 264:479 */       classifierString = ZeroR.class.getName();
/* 265:    */     }
/* 266:481 */     String[] classifierSpec = Utils.splitOptions(classifierString);
/* 267:482 */     if (classifierSpec.length == 0) {
/* 268:483 */       throw new Exception("Invalid classifier specification string");
/* 269:    */     }
/* 270:485 */     String classifierName = classifierSpec[0];
/* 271:486 */     classifierSpec[0] = "";
/* 272:487 */     setClassifier(AbstractClassifier.forName(classifierName, classifierSpec));
/* 273:    */     
/* 274:489 */     String cString = Utils.getOption('C', options);
/* 275:490 */     if (cString.length() != 0) {
/* 276:491 */       setClassIndex(new Double(cString).intValue());
/* 277:    */     } else {
/* 278:493 */       setClassIndex(-1);
/* 279:    */     }
/* 280:496 */     String fString = Utils.getOption('F', options);
/* 281:497 */     if (fString.length() != 0) {
/* 282:498 */       setNumFolds(new Double(fString).intValue());
/* 283:    */     } else {
/* 284:500 */       setNumFolds(0);
/* 285:    */     }
/* 286:503 */     String tString = Utils.getOption('T', options);
/* 287:504 */     if (tString.length() != 0) {
/* 288:505 */       setThreshold(new Double(tString).doubleValue());
/* 289:    */     } else {
/* 290:507 */       setThreshold(0.1D);
/* 291:    */     }
/* 292:510 */     String iString = Utils.getOption('I', options);
/* 293:511 */     if (iString.length() != 0) {
/* 294:512 */       setMaxIterations(new Double(iString).intValue());
/* 295:    */     } else {
/* 296:514 */       setMaxIterations(0);
/* 297:    */     }
/* 298:517 */     if (Utils.getFlag('V', options)) {
/* 299:518 */       setInvert(true);
/* 300:    */     } else {
/* 301:520 */       setInvert(false);
/* 302:    */     }
/* 303:523 */     Utils.checkForRemainingOptions(options);
/* 304:    */   }
/* 305:    */   
/* 306:    */   public String[] getOptions()
/* 307:    */   {
/* 308:535 */     Vector<String> options = new Vector();
/* 309:    */     
/* 310:537 */     options.add("-W");
/* 311:538 */     options.add("" + getClassifierSpec());
/* 312:539 */     options.add("-C");
/* 313:540 */     options.add("" + getClassIndex());
/* 314:541 */     options.add("-F");
/* 315:542 */     options.add("" + getNumFolds());
/* 316:543 */     options.add("-T");
/* 317:544 */     options.add("" + getThreshold());
/* 318:545 */     options.add("-I");
/* 319:546 */     options.add("" + getMaxIterations());
/* 320:547 */     if (getInvert()) {
/* 321:548 */       options.add("-V");
/* 322:    */     }
/* 323:551 */     return (String[])options.toArray(new String[0]);
/* 324:    */   }
/* 325:    */   
/* 326:    */   public String globalInfo()
/* 327:    */   {
/* 328:561 */     return "A filter that removes instances which are incorrectly classified. Useful for removing outliers.";
/* 329:    */   }
/* 330:    */   
/* 331:    */   public String classifierTipText()
/* 332:    */   {
/* 333:573 */     return "The classifier upon which to base the misclassifications.";
/* 334:    */   }
/* 335:    */   
/* 336:    */   public void setClassifier(Classifier classifier)
/* 337:    */   {
/* 338:583 */     this.m_cleansingClassifier = classifier;
/* 339:    */   }
/* 340:    */   
/* 341:    */   public Classifier getClassifier()
/* 342:    */   {
/* 343:593 */     return this.m_cleansingClassifier;
/* 344:    */   }
/* 345:    */   
/* 346:    */   protected String getClassifierSpec()
/* 347:    */   {
/* 348:604 */     Classifier c = getClassifier();
/* 349:605 */     if ((c instanceof OptionHandler)) {
/* 350:606 */       return c.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)c).getOptions());
/* 351:    */     }
/* 352:609 */     return c.getClass().getName();
/* 353:    */   }
/* 354:    */   
/* 355:    */   public String classIndexTipText()
/* 356:    */   {
/* 357:620 */     return "Index of the class upon which to base the misclassifications. If < 0 will use any current set class or default to the last attribute.";
/* 358:    */   }
/* 359:    */   
/* 360:    */   public void setClassIndex(int classIndex)
/* 361:    */   {
/* 362:632 */     this.m_classIndex = classIndex;
/* 363:    */   }
/* 364:    */   
/* 365:    */   public int getClassIndex()
/* 366:    */   {
/* 367:642 */     return this.m_classIndex;
/* 368:    */   }
/* 369:    */   
/* 370:    */   public String numFoldsTipText()
/* 371:    */   {
/* 372:653 */     return "The number of cross-validation folds to use. If < 2 then no cross-validation will be performed.";
/* 373:    */   }
/* 374:    */   
/* 375:    */   public void setNumFolds(int numOfFolds)
/* 376:    */   {
/* 377:664 */     this.m_numOfCrossValidationFolds = numOfFolds;
/* 378:    */   }
/* 379:    */   
/* 380:    */   public int getNumFolds()
/* 381:    */   {
/* 382:674 */     return this.m_numOfCrossValidationFolds;
/* 383:    */   }
/* 384:    */   
/* 385:    */   public String thresholdTipText()
/* 386:    */   {
/* 387:685 */     return "Threshold for the max allowable error when predicting a numeric class. Should be >= 0.";
/* 388:    */   }
/* 389:    */   
/* 390:    */   public void setThreshold(double threshold)
/* 391:    */   {
/* 392:696 */     this.m_numericClassifyThreshold = threshold;
/* 393:    */   }
/* 394:    */   
/* 395:    */   public double getThreshold()
/* 396:    */   {
/* 397:706 */     return this.m_numericClassifyThreshold;
/* 398:    */   }
/* 399:    */   
/* 400:    */   public String maxIterationsTipText()
/* 401:    */   {
/* 402:717 */     return "The maximum number of iterations to perform. < 1 means filter will go until fully cleansed.";
/* 403:    */   }
/* 404:    */   
/* 405:    */   public void setMaxIterations(int iterations)
/* 406:    */   {
/* 407:728 */     this.m_numOfCleansingIterations = iterations;
/* 408:    */   }
/* 409:    */   
/* 410:    */   public int getMaxIterations()
/* 411:    */   {
/* 412:738 */     return this.m_numOfCleansingIterations;
/* 413:    */   }
/* 414:    */   
/* 415:    */   public String invertTipText()
/* 416:    */   {
/* 417:749 */     return "Whether or not to invert the selection. If true, correctly classified instances will be discarded.";
/* 418:    */   }
/* 419:    */   
/* 420:    */   public void setInvert(boolean invert)
/* 421:    */   {
/* 422:759 */     this.m_invertMatching = invert;
/* 423:    */   }
/* 424:    */   
/* 425:    */   public boolean getInvert()
/* 426:    */   {
/* 427:769 */     return this.m_invertMatching;
/* 428:    */   }
/* 429:    */   
/* 430:    */   public String getRevision()
/* 431:    */   {
/* 432:779 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 433:    */   }
/* 434:    */   
/* 435:    */   public static void main(String[] argv)
/* 436:    */   {
/* 437:788 */     runFilter(new RemoveMisclassified(), argv);
/* 438:    */   }
/* 439:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.instance.RemoveMisclassified
 * JD-Core Version:    0.7.0.1
 */