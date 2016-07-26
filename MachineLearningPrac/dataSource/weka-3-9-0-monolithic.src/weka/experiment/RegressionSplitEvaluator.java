/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.ByteArrayOutputStream;
/*   4:    */ import java.io.ObjectOutputStream;
/*   5:    */ import java.io.ObjectStreamClass;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.io.Serializable;
/*   8:    */ import java.lang.management.ManagementFactory;
/*   9:    */ import java.lang.management.ThreadMXBean;
/*  10:    */ import java.util.ArrayList;
/*  11:    */ import java.util.Arrays;
/*  12:    */ import java.util.Collections;
/*  13:    */ import java.util.Enumeration;
/*  14:    */ import java.util.Iterator;
/*  15:    */ import java.util.List;
/*  16:    */ import java.util.Vector;
/*  17:    */ import weka.classifiers.AbstractClassifier;
/*  18:    */ import weka.classifiers.Classifier;
/*  19:    */ import weka.classifiers.Evaluation;
/*  20:    */ import weka.classifiers.evaluation.AbstractEvaluationMetric;
/*  21:    */ import weka.classifiers.rules.ZeroR;
/*  22:    */ import weka.core.AdditionalMeasureProducer;
/*  23:    */ import weka.core.Attribute;
/*  24:    */ import weka.core.Instances;
/*  25:    */ import weka.core.Option;
/*  26:    */ import weka.core.OptionHandler;
/*  27:    */ import weka.core.RevisionHandler;
/*  28:    */ import weka.core.RevisionUtils;
/*  29:    */ import weka.core.Summarizable;
/*  30:    */ import weka.core.Utils;
/*  31:    */ 
/*  32:    */ public class RegressionSplitEvaluator
/*  33:    */   implements SplitEvaluator, OptionHandler, AdditionalMeasureProducer, RevisionHandler
/*  34:    */ {
/*  35:    */   static final long serialVersionUID = -328181640503349202L;
/*  36: 97 */   protected Classifier m_Template = new ZeroR();
/*  37:    */   protected Classifier m_Classifier;
/*  38:    */   protected Evaluation m_Evaluation;
/*  39:106 */   protected String[] m_AdditionalMeasures = null;
/*  40:113 */   protected boolean[] m_doesProduce = null;
/*  41:116 */   protected String m_result = null;
/*  42:119 */   protected String m_ClassifierOptions = "";
/*  43:122 */   protected String m_ClassifierVersion = "";
/*  44:    */   private boolean m_NoSizeDetermination;
/*  45:    */   private static final int KEY_SIZE = 3;
/*  46:    */   private static final int RESULT_SIZE = 27;
/*  47:133 */   protected final List<AbstractEvaluationMetric> m_pluginMetrics = new ArrayList();
/*  48:135 */   protected int m_numPluginStatistics = 0;
/*  49:    */   
/*  50:    */   public RegressionSplitEvaluator()
/*  51:    */   {
/*  52:142 */     updateOptions();
/*  53:    */     
/*  54:144 */     List<AbstractEvaluationMetric> pluginMetrics = AbstractEvaluationMetric.getPluginMetrics();
/*  55:146 */     if (pluginMetrics != null) {
/*  56:147 */       for (AbstractEvaluationMetric m : pluginMetrics) {
/*  57:148 */         if (m.appliesToNumericClass())
/*  58:    */         {
/*  59:149 */           this.m_pluginMetrics.add(m);
/*  60:150 */           this.m_numPluginStatistics += m.getStatisticNames().size();
/*  61:    */         }
/*  62:    */       }
/*  63:    */     }
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String globalInfo()
/*  67:    */   {
/*  68:163 */     return "A SplitEvaluator that produces results for a classification scheme on a numeric class attribute.";
/*  69:    */   }
/*  70:    */   
/*  71:    */   public Enumeration<Option> listOptions()
/*  72:    */   {
/*  73:175 */     Vector<Option> newVector = new Vector(2);
/*  74:    */     
/*  75:177 */     newVector.addElement(new Option("\tSkips the determination of sizes (train/test/classifier)\n\t(default: sizes are determined)", "no-size", 0, "-no-size"));
/*  76:    */     
/*  77:    */ 
/*  78:180 */     newVector.addElement(new Option("\tThe full class name of the classifier.\n\teg: weka.classifiers.bayes.NaiveBayes", "W", 1, "-W <class name>"));
/*  79:184 */     if ((this.m_Template != null) && ((this.m_Template instanceof OptionHandler)))
/*  80:    */     {
/*  81:185 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to classifier " + this.m_Template.getClass().getName() + ":"));
/*  82:    */       
/*  83:    */ 
/*  84:188 */       newVector.addAll(Collections.list(((OptionHandler)this.m_Template).listOptions()));
/*  85:    */     }
/*  86:191 */     return newVector.elements();
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setOptions(String[] options)
/*  90:    */     throws Exception
/*  91:    */   {
/*  92:232 */     this.m_NoSizeDetermination = Utils.getFlag("no-size", options);
/*  93:    */     
/*  94:234 */     String cName = Utils.getOption('W', options);
/*  95:235 */     if (cName.length() == 0) {
/*  96:236 */       throw new Exception("A classifier must be specified with the -W option.");
/*  97:    */     }
/*  98:242 */     setClassifier(AbstractClassifier.forName(cName, null));
/*  99:243 */     if ((getClassifier() instanceof OptionHandler))
/* 100:    */     {
/* 101:244 */       ((OptionHandler)getClassifier()).setOptions(Utils.partitionOptions(options));
/* 102:    */       
/* 103:246 */       updateOptions();
/* 104:    */     }
/* 105:    */   }
/* 106:    */   
/* 107:    */   public String[] getOptions()
/* 108:    */   {
/* 109:260 */     Vector<String> result = new Vector();
/* 110:    */     
/* 111:262 */     String[] classifierOptions = new String[0];
/* 112:263 */     if ((this.m_Template != null) && ((this.m_Template instanceof OptionHandler))) {
/* 113:264 */       classifierOptions = ((OptionHandler)this.m_Template).getOptions();
/* 114:    */     }
/* 115:267 */     if (getNoSizeDetermination()) {
/* 116:268 */       result.add("-no-size");
/* 117:    */     }
/* 118:271 */     if (getClassifier() != null)
/* 119:    */     {
/* 120:272 */       result.add("-W");
/* 121:273 */       result.add(getClassifier().getClass().getName());
/* 122:    */     }
/* 123:275 */     result.add("--");
/* 124:276 */     result.addAll(Arrays.asList(classifierOptions));
/* 125:    */     
/* 126:278 */     return (String[])result.toArray(new String[result.size()]);
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void setAdditionalMeasures(String[] additionalMeasures)
/* 130:    */   {
/* 131:291 */     this.m_AdditionalMeasures = additionalMeasures;
/* 132:295 */     if ((this.m_AdditionalMeasures != null) && (this.m_AdditionalMeasures.length > 0))
/* 133:    */     {
/* 134:296 */       this.m_doesProduce = new boolean[this.m_AdditionalMeasures.length];
/* 135:298 */       if ((this.m_Template instanceof AdditionalMeasureProducer))
/* 136:    */       {
/* 137:299 */         Enumeration<String> en = ((AdditionalMeasureProducer)this.m_Template).enumerateMeasures();
/* 138:301 */         while (en.hasMoreElements())
/* 139:    */         {
/* 140:302 */           String mname = (String)en.nextElement();
/* 141:303 */           for (int j = 0; j < this.m_AdditionalMeasures.length; j++) {
/* 142:304 */             if (mname.compareToIgnoreCase(this.m_AdditionalMeasures[j]) == 0) {
/* 143:305 */               this.m_doesProduce[j] = true;
/* 144:    */             }
/* 145:    */           }
/* 146:    */         }
/* 147:    */       }
/* 148:    */     }
/* 149:    */     else
/* 150:    */     {
/* 151:311 */       this.m_doesProduce = null;
/* 152:    */     }
/* 153:    */   }
/* 154:    */   
/* 155:    */   public Enumeration<String> enumerateMeasures()
/* 156:    */   {
/* 157:323 */     Vector<String> newVector = new Vector();
/* 158:324 */     if ((this.m_Template instanceof AdditionalMeasureProducer))
/* 159:    */     {
/* 160:325 */       Enumeration<String> en = ((AdditionalMeasureProducer)this.m_Template).enumerateMeasures();
/* 161:327 */       while (en.hasMoreElements())
/* 162:    */       {
/* 163:328 */         String mname = (String)en.nextElement();
/* 164:329 */         newVector.add(mname);
/* 165:    */       }
/* 166:    */     }
/* 167:332 */     return newVector.elements();
/* 168:    */   }
/* 169:    */   
/* 170:    */   public double getMeasure(String additionalMeasureName)
/* 171:    */   {
/* 172:344 */     if ((this.m_Template instanceof AdditionalMeasureProducer))
/* 173:    */     {
/* 174:345 */       if (this.m_Classifier == null) {
/* 175:346 */         throw new IllegalArgumentException("ClassifierSplitEvaluator: Can't return result for measure, classifier has not been built yet.");
/* 176:    */       }
/* 177:350 */       return ((AdditionalMeasureProducer)this.m_Classifier).getMeasure(additionalMeasureName);
/* 178:    */     }
/* 179:353 */     throw new IllegalArgumentException("ClassifierSplitEvaluator: Can't return value for : " + additionalMeasureName + ". " + this.m_Template.getClass().getName() + " " + "is not an AdditionalMeasureProducer");
/* 180:    */   }
/* 181:    */   
/* 182:    */   public Object[] getKeyTypes()
/* 183:    */   {
/* 184:370 */     Object[] keyTypes = new Object[3];
/* 185:371 */     keyTypes[0] = "";
/* 186:372 */     keyTypes[1] = "";
/* 187:373 */     keyTypes[2] = "";
/* 188:374 */     return keyTypes;
/* 189:    */   }
/* 190:    */   
/* 191:    */   public String[] getKeyNames()
/* 192:    */   {
/* 193:386 */     String[] keyNames = new String[3];
/* 194:387 */     keyNames[0] = "Scheme";
/* 195:388 */     keyNames[1] = "Scheme_options";
/* 196:389 */     keyNames[2] = "Scheme_version_ID";
/* 197:390 */     return keyNames;
/* 198:    */   }
/* 199:    */   
/* 200:    */   public Object[] getKey()
/* 201:    */   {
/* 202:404 */     Object[] key = new Object[3];
/* 203:405 */     key[0] = this.m_Template.getClass().getName();
/* 204:406 */     key[1] = this.m_ClassifierOptions;
/* 205:407 */     key[2] = this.m_ClassifierVersion;
/* 206:408 */     return key;
/* 207:    */   }
/* 208:    */   
/* 209:    */   public Object[] getResultTypes()
/* 210:    */   {
/* 211:421 */     int addm = this.m_AdditionalMeasures != null ? this.m_AdditionalMeasures.length : 0;
/* 212:422 */     Object[] resultTypes = new Object[27 + addm + this.m_numPluginStatistics];
/* 213:    */     
/* 214:424 */     Double doub = new Double(0.0D);
/* 215:425 */     int current = 0;
/* 216:426 */     resultTypes[(current++)] = doub;
/* 217:427 */     resultTypes[(current++)] = doub;
/* 218:    */     
/* 219:429 */     resultTypes[(current++)] = doub;
/* 220:430 */     resultTypes[(current++)] = doub;
/* 221:431 */     resultTypes[(current++)] = doub;
/* 222:432 */     resultTypes[(current++)] = doub;
/* 223:433 */     resultTypes[(current++)] = doub;
/* 224:434 */     resultTypes[(current++)] = doub;
/* 225:435 */     resultTypes[(current++)] = doub;
/* 226:    */     
/* 227:437 */     resultTypes[(current++)] = doub;
/* 228:438 */     resultTypes[(current++)] = doub;
/* 229:439 */     resultTypes[(current++)] = doub;
/* 230:440 */     resultTypes[(current++)] = doub;
/* 231:441 */     resultTypes[(current++)] = doub;
/* 232:442 */     resultTypes[(current++)] = doub;
/* 233:    */     
/* 234:    */ 
/* 235:445 */     resultTypes[(current++)] = doub;
/* 236:446 */     resultTypes[(current++)] = doub;
/* 237:447 */     resultTypes[(current++)] = doub;
/* 238:448 */     resultTypes[(current++)] = doub;
/* 239:449 */     resultTypes[(current++)] = doub;
/* 240:450 */     resultTypes[(current++)] = doub;
/* 241:    */     
/* 242:    */ 
/* 243:453 */     resultTypes[(current++)] = doub;
/* 244:454 */     resultTypes[(current++)] = doub;
/* 245:455 */     resultTypes[(current++)] = doub;
/* 246:    */     
/* 247:    */ 
/* 248:458 */     resultTypes[(current++)] = doub;
/* 249:459 */     resultTypes[(current++)] = doub;
/* 250:    */     
/* 251:461 */     resultTypes[(current++)] = "";
/* 252:464 */     for (int i = 0; i < addm; i++) {
/* 253:465 */       resultTypes[(current++)] = doub;
/* 254:    */     }
/* 255:469 */     for (int i = 0; i < this.m_numPluginStatistics; i++) {
/* 256:470 */       resultTypes[(current++)] = doub;
/* 257:    */     }
/* 258:473 */     if (current != 27 + addm + this.m_numPluginStatistics) {
/* 259:474 */       throw new Error("ResultTypes didn't fit RESULT_SIZE");
/* 260:    */     }
/* 261:476 */     return resultTypes;
/* 262:    */   }
/* 263:    */   
/* 264:    */   public String[] getResultNames()
/* 265:    */   {
/* 266:487 */     int addm = this.m_AdditionalMeasures != null ? this.m_AdditionalMeasures.length : 0;
/* 267:488 */     String[] resultNames = new String[27 + addm + this.m_numPluginStatistics];
/* 268:    */     
/* 269:490 */     int current = 0;
/* 270:491 */     resultNames[(current++)] = "Number_of_training_instances";
/* 271:492 */     resultNames[(current++)] = "Number_of_testing_instances";
/* 272:    */     
/* 273:    */ 
/* 274:495 */     resultNames[(current++)] = "Mean_absolute_error";
/* 275:496 */     resultNames[(current++)] = "Root_mean_squared_error";
/* 276:497 */     resultNames[(current++)] = "Relative_absolute_error";
/* 277:498 */     resultNames[(current++)] = "Root_relative_squared_error";
/* 278:499 */     resultNames[(current++)] = "Correlation_coefficient";
/* 279:500 */     resultNames[(current++)] = "Number_unclassified";
/* 280:501 */     resultNames[(current++)] = "Percent_unclassified";
/* 281:    */     
/* 282:    */ 
/* 283:504 */     resultNames[(current++)] = "SF_prior_entropy";
/* 284:505 */     resultNames[(current++)] = "SF_scheme_entropy";
/* 285:506 */     resultNames[(current++)] = "SF_entropy_gain";
/* 286:507 */     resultNames[(current++)] = "SF_mean_prior_entropy";
/* 287:508 */     resultNames[(current++)] = "SF_mean_scheme_entropy";
/* 288:509 */     resultNames[(current++)] = "SF_mean_entropy_gain";
/* 289:    */     
/* 290:    */ 
/* 291:512 */     resultNames[(current++)] = "Elapsed_Time_training";
/* 292:513 */     resultNames[(current++)] = "Elapsed_Time_testing";
/* 293:514 */     resultNames[(current++)] = "UserCPU_Time_training";
/* 294:515 */     resultNames[(current++)] = "UserCPU_Time_testing";
/* 295:516 */     resultNames[(current++)] = "UserCPU_Time_millis_training";
/* 296:517 */     resultNames[(current++)] = "UserCPU_Time_millis_testing";
/* 297:    */     
/* 298:    */ 
/* 299:520 */     resultNames[(current++)] = "Serialized_Model_Size";
/* 300:521 */     resultNames[(current++)] = "Serialized_Train_Set_Size";
/* 301:522 */     resultNames[(current++)] = "Serialized_Test_Set_Size";
/* 302:    */     
/* 303:    */ 
/* 304:525 */     resultNames[(current++)] = "Coverage_of_Test_Cases_By_Regions";
/* 305:526 */     resultNames[(current++)] = "Size_of_Predicted_Regions";
/* 306:    */     
/* 307:    */ 
/* 308:529 */     resultNames[(current++)] = "Summary";
/* 309:531 */     for (int i = 0; i < addm; i++) {
/* 310:532 */       resultNames[(current++)] = this.m_AdditionalMeasures[i];
/* 311:    */     }
/* 312:535 */     for (AbstractEvaluationMetric m : this.m_pluginMetrics)
/* 313:    */     {
/* 314:536 */       List<String> statNames = m.getStatisticNames();
/* 315:537 */       for (String s : statNames) {
/* 316:538 */         resultNames[(current++)] = s;
/* 317:    */       }
/* 318:    */     }
/* 319:542 */     if (current != 27 + addm + this.m_numPluginStatistics) {
/* 320:543 */       throw new Error("ResultNames didn't fit RESULT_SIZE");
/* 321:    */     }
/* 322:545 */     return resultNames;
/* 323:    */   }
/* 324:    */   
/* 325:    */   public Object[] getResult(Instances train, Instances test)
/* 326:    */     throws Exception
/* 327:    */   {
/* 328:562 */     if (train.classAttribute().type() != 0) {
/* 329:563 */       throw new Exception("Class attribute is not numeric!");
/* 330:    */     }
/* 331:565 */     if (this.m_Template == null) {
/* 332:566 */       throw new Exception("No classifier has been specified");
/* 333:    */     }
/* 334:568 */     ThreadMXBean thMonitor = ManagementFactory.getThreadMXBean();
/* 335:569 */     boolean canMeasureCPUTime = thMonitor.isThreadCpuTimeSupported();
/* 336:570 */     if ((canMeasureCPUTime) && (!thMonitor.isThreadCpuTimeEnabled())) {
/* 337:571 */       thMonitor.setThreadCpuTimeEnabled(true);
/* 338:    */     }
/* 339:574 */     int addm = this.m_AdditionalMeasures != null ? this.m_AdditionalMeasures.length : 0;
/* 340:575 */     Object[] result = new Object[27 + addm + this.m_numPluginStatistics];
/* 341:576 */     long thID = Thread.currentThread().getId();
/* 342:577 */     long CPUStartTime = -1L;long trainCPUTimeElapsed = -1L;long testCPUTimeElapsed = -1L;
/* 343:578 */     Evaluation eval = new Evaluation(train);
/* 344:579 */     this.m_Classifier = AbstractClassifier.makeCopy(this.m_Template);
/* 345:    */     
/* 346:581 */     long trainTimeStart = System.currentTimeMillis();
/* 347:582 */     if (canMeasureCPUTime) {
/* 348:583 */       CPUStartTime = thMonitor.getThreadUserTime(thID);
/* 349:    */     }
/* 350:585 */     this.m_Classifier.buildClassifier(train);
/* 351:586 */     if (canMeasureCPUTime) {
/* 352:587 */       trainCPUTimeElapsed = thMonitor.getThreadUserTime(thID) - CPUStartTime;
/* 353:    */     }
/* 354:589 */     long trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;
/* 355:590 */     long testTimeStart = System.currentTimeMillis();
/* 356:591 */     if (canMeasureCPUTime) {
/* 357:592 */       CPUStartTime = thMonitor.getThreadUserTime(thID);
/* 358:    */     }
/* 359:594 */     eval.evaluateModel(this.m_Classifier, test, new Object[0]);
/* 360:595 */     if (canMeasureCPUTime) {
/* 361:596 */       testCPUTimeElapsed = thMonitor.getThreadUserTime(thID) - CPUStartTime;
/* 362:    */     }
/* 363:598 */     long testTimeElapsed = System.currentTimeMillis() - testTimeStart;
/* 364:599 */     thMonitor = null;
/* 365:    */     
/* 366:601 */     this.m_result = eval.toSummaryString();
/* 367:    */     
/* 368:    */ 
/* 369:604 */     int current = 0;
/* 370:605 */     result[(current++)] = new Double(train.numInstances());
/* 371:606 */     result[(current++)] = new Double(eval.numInstances());
/* 372:    */     
/* 373:608 */     result[(current++)] = new Double(eval.meanAbsoluteError());
/* 374:609 */     result[(current++)] = new Double(eval.rootMeanSquaredError());
/* 375:610 */     result[(current++)] = new Double(eval.relativeAbsoluteError());
/* 376:611 */     result[(current++)] = new Double(eval.rootRelativeSquaredError());
/* 377:612 */     result[(current++)] = new Double(eval.correlationCoefficient());
/* 378:613 */     result[(current++)] = new Double(eval.unclassified());
/* 379:614 */     result[(current++)] = new Double(eval.pctUnclassified());
/* 380:    */     
/* 381:616 */     result[(current++)] = new Double(eval.SFPriorEntropy());
/* 382:617 */     result[(current++)] = new Double(eval.SFSchemeEntropy());
/* 383:618 */     result[(current++)] = new Double(eval.SFEntropyGain());
/* 384:619 */     result[(current++)] = new Double(eval.SFMeanPriorEntropy());
/* 385:620 */     result[(current++)] = new Double(eval.SFMeanSchemeEntropy());
/* 386:621 */     result[(current++)] = new Double(eval.SFMeanEntropyGain());
/* 387:    */     
/* 388:    */ 
/* 389:624 */     result[(current++)] = new Double(trainTimeElapsed / 1000.0D);
/* 390:625 */     result[(current++)] = new Double(testTimeElapsed / 1000.0D);
/* 391:626 */     if (canMeasureCPUTime)
/* 392:    */     {
/* 393:627 */       result[(current++)] = new Double(trainCPUTimeElapsed / 1000000.0D / 1000.0D);
/* 394:    */       
/* 395:629 */       result[(current++)] = new Double(testCPUTimeElapsed / 1000000.0D / 1000.0D);
/* 396:    */       
/* 397:631 */       result[(current++)] = new Double(trainCPUTimeElapsed / 1000000.0D);
/* 398:    */       
/* 399:633 */       result[(current++)] = new Double(testCPUTimeElapsed / 1000000.0D);
/* 400:    */     }
/* 401:    */     else
/* 402:    */     {
/* 403:635 */       result[(current++)] = new Double(Utils.missingValue());
/* 404:636 */       result[(current++)] = new Double(Utils.missingValue());
/* 405:637 */       result[(current++)] = new Double(Utils.missingValue());
/* 406:638 */       result[(current++)] = new Double(Utils.missingValue());
/* 407:    */     }
/* 408:642 */     if (this.m_NoSizeDetermination)
/* 409:    */     {
/* 410:643 */       result[(current++)] = Double.valueOf(-1.0D);
/* 411:644 */       result[(current++)] = Double.valueOf(-1.0D);
/* 412:645 */       result[(current++)] = Double.valueOf(-1.0D);
/* 413:    */     }
/* 414:    */     else
/* 415:    */     {
/* 416:647 */       ByteArrayOutputStream bastream = new ByteArrayOutputStream();
/* 417:648 */       ObjectOutputStream oostream = new ObjectOutputStream(bastream);
/* 418:649 */       oostream.writeObject(this.m_Classifier);
/* 419:650 */       result[(current++)] = new Double(bastream.size());
/* 420:651 */       bastream = new ByteArrayOutputStream();
/* 421:652 */       oostream = new ObjectOutputStream(bastream);
/* 422:653 */       oostream.writeObject(train);
/* 423:654 */       result[(current++)] = new Double(bastream.size());
/* 424:655 */       bastream = new ByteArrayOutputStream();
/* 425:656 */       oostream = new ObjectOutputStream(bastream);
/* 426:657 */       oostream.writeObject(test);
/* 427:658 */       result[(current++)] = new Double(bastream.size());
/* 428:    */     }
/* 429:662 */     result[(current++)] = new Double(eval.coverageOfTestCasesByPredictedRegions());
/* 430:    */     
/* 431:664 */     result[(current++)] = new Double(eval.sizeOfPredictedRegions());
/* 432:666 */     if ((this.m_Classifier instanceof Summarizable)) {
/* 433:667 */       result[(current++)] = ((Summarizable)this.m_Classifier).toSummaryString();
/* 434:    */     } else {
/* 435:669 */       result[(current++)] = null;
/* 436:    */     }
/* 437:672 */     for (int i = 0; i < addm; i++) {
/* 438:673 */       if (this.m_doesProduce[i] != 0) {
/* 439:    */         try
/* 440:    */         {
/* 441:675 */           double dv = ((AdditionalMeasureProducer)this.m_Classifier).getMeasure(this.m_AdditionalMeasures[i]);
/* 442:677 */           if (!Utils.isMissingValue(dv))
/* 443:    */           {
/* 444:678 */             Double value = new Double(dv);
/* 445:679 */             result[(current++)] = value;
/* 446:    */           }
/* 447:    */           else
/* 448:    */           {
/* 449:681 */             result[(current++)] = null;
/* 450:    */           }
/* 451:    */         }
/* 452:    */         catch (Exception ex)
/* 453:    */         {
/* 454:684 */           System.err.println(ex);
/* 455:    */         }
/* 456:    */       } else {
/* 457:687 */         result[(current++)] = null;
/* 458:    */       }
/* 459:    */     }
/* 460:692 */     List<AbstractEvaluationMetric> metrics = eval.getPluginMetrics();
/* 461:    */     Iterator i$;
/* 462:693 */     if (metrics != null) {
/* 463:694 */       for (i$ = metrics.iterator(); i$.hasNext();)
/* 464:    */       {
/* 465:694 */         m = (AbstractEvaluationMetric)i$.next();
/* 466:695 */         if (m.appliesToNumericClass())
/* 467:    */         {
/* 468:696 */           List<String> statNames = m.getStatisticNames();
/* 469:697 */           for (String s : statNames) {
/* 470:698 */             result[(current++)] = new Double(m.getStatistic(s));
/* 471:    */           }
/* 472:    */         }
/* 473:    */       }
/* 474:    */     }
/* 475:    */     AbstractEvaluationMetric m;
/* 476:704 */     if (current != 27 + addm + this.m_numPluginStatistics) {
/* 477:705 */       throw new Error("Results didn't fit RESULT_SIZE");
/* 478:    */     }
/* 479:708 */     this.m_Evaluation = eval;
/* 480:    */     
/* 481:710 */     return result;
/* 482:    */   }
/* 483:    */   
/* 484:    */   public String classifierTipText()
/* 485:    */   {
/* 486:720 */     return "The classifier to use.";
/* 487:    */   }
/* 488:    */   
/* 489:    */   public Classifier getClassifier()
/* 490:    */   {
/* 491:730 */     return this.m_Template;
/* 492:    */   }
/* 493:    */   
/* 494:    */   public void setClassifier(Classifier newClassifier)
/* 495:    */   {
/* 496:740 */     this.m_Template = newClassifier;
/* 497:741 */     updateOptions();
/* 498:    */     
/* 499:743 */     System.err.println("RegressionSplitEvaluator: In set classifier");
/* 500:    */   }
/* 501:    */   
/* 502:    */   public boolean getNoSizeDetermination()
/* 503:    */   {
/* 504:752 */     return this.m_NoSizeDetermination;
/* 505:    */   }
/* 506:    */   
/* 507:    */   public void setNoSizeDetermination(boolean value)
/* 508:    */   {
/* 509:761 */     this.m_NoSizeDetermination = value;
/* 510:    */   }
/* 511:    */   
/* 512:    */   public String noSizeDeterminationTipText()
/* 513:    */   {
/* 514:771 */     return "If enabled, the size determination for train/test/classifier is skipped.";
/* 515:    */   }
/* 516:    */   
/* 517:    */   protected void updateOptions()
/* 518:    */   {
/* 519:779 */     if ((this.m_Template instanceof OptionHandler)) {
/* 520:780 */       this.m_ClassifierOptions = Utils.joinOptions(((OptionHandler)this.m_Template).getOptions());
/* 521:    */     } else {
/* 522:783 */       this.m_ClassifierOptions = "";
/* 523:    */     }
/* 524:785 */     if ((this.m_Template instanceof Serializable))
/* 525:    */     {
/* 526:786 */       ObjectStreamClass obs = ObjectStreamClass.lookup(this.m_Template.getClass());
/* 527:787 */       this.m_ClassifierVersion = ("" + obs.getSerialVersionUID());
/* 528:    */     }
/* 529:    */     else
/* 530:    */     {
/* 531:789 */       this.m_ClassifierVersion = "";
/* 532:    */     }
/* 533:    */   }
/* 534:    */   
/* 535:    */   public void setClassifierName(String newClassifierName)
/* 536:    */     throws Exception
/* 537:    */   {
/* 538:    */     try
/* 539:    */     {
/* 540:803 */       setClassifier((Classifier)Class.forName(newClassifierName).newInstance());
/* 541:    */     }
/* 542:    */     catch (Exception ex)
/* 543:    */     {
/* 544:805 */       throw new Exception("Can't find Classifier with class name: " + newClassifierName);
/* 545:    */     }
/* 546:    */   }
/* 547:    */   
/* 548:    */   public String getRawResultOutput()
/* 549:    */   {
/* 550:817 */     StringBuffer result = new StringBuffer();
/* 551:819 */     if (this.m_Classifier == null) {
/* 552:820 */       return "<null> classifier";
/* 553:    */     }
/* 554:822 */     result.append(toString());
/* 555:823 */     result.append("Classifier model: \n" + this.m_Classifier.toString() + '\n');
/* 556:826 */     if (this.m_result != null)
/* 557:    */     {
/* 558:827 */       result.append(this.m_result);
/* 559:829 */       if (this.m_doesProduce != null) {
/* 560:830 */         for (int i = 0; i < this.m_doesProduce.length; i++) {
/* 561:831 */           if (this.m_doesProduce[i] != 0) {
/* 562:    */             try
/* 563:    */             {
/* 564:833 */               double dv = ((AdditionalMeasureProducer)this.m_Classifier).getMeasure(this.m_AdditionalMeasures[i]);
/* 565:835 */               if (!Utils.isMissingValue(dv))
/* 566:    */               {
/* 567:836 */                 Double value = new Double(dv);
/* 568:837 */                 result.append(this.m_AdditionalMeasures[i] + " : " + value + '\n');
/* 569:    */               }
/* 570:    */               else
/* 571:    */               {
/* 572:839 */                 result.append(this.m_AdditionalMeasures[i] + " : " + '?' + '\n');
/* 573:    */               }
/* 574:    */             }
/* 575:    */             catch (Exception ex)
/* 576:    */             {
/* 577:842 */               System.err.println(ex);
/* 578:    */             }
/* 579:    */           }
/* 580:    */         }
/* 581:    */       }
/* 582:    */     }
/* 583:848 */     return result.toString();
/* 584:    */   }
/* 585:    */   
/* 586:    */   public String toString()
/* 587:    */   {
/* 588:859 */     String result = "RegressionSplitEvaluator: ";
/* 589:860 */     if (this.m_Template == null) {
/* 590:861 */       return result + "<null> classifier";
/* 591:    */     }
/* 592:863 */     return result + this.m_Template.getClass().getName() + " " + this.m_ClassifierOptions + "(version " + this.m_ClassifierVersion + ")";
/* 593:    */   }
/* 594:    */   
/* 595:    */   public String getRevision()
/* 596:    */   {
/* 597:874 */     return RevisionUtils.extract("$Revision: 11323 $");
/* 598:    */   }
/* 599:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.RegressionSplitEvaluator
 * JD-Core Version:    0.7.0.1
 */