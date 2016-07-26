/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.ByteArrayOutputStream;
/*   5:    */ import java.io.File;
/*   6:    */ import java.io.FileReader;
/*   7:    */ import java.io.ObjectOutputStream;
/*   8:    */ import java.io.PrintStream;
/*   9:    */ import java.lang.management.ManagementFactory;
/*  10:    */ import java.lang.management.ThreadMXBean;
/*  11:    */ import java.util.Collections;
/*  12:    */ import java.util.Enumeration;
/*  13:    */ import java.util.Vector;
/*  14:    */ import weka.classifiers.AbstractClassifier;
/*  15:    */ import weka.classifiers.Classifier;
/*  16:    */ import weka.classifiers.CostMatrix;
/*  17:    */ import weka.classifiers.Evaluation;
/*  18:    */ import weka.core.AdditionalMeasureProducer;
/*  19:    */ import weka.core.Attribute;
/*  20:    */ import weka.core.Instances;
/*  21:    */ import weka.core.Option;
/*  22:    */ import weka.core.RevisionUtils;
/*  23:    */ import weka.core.Summarizable;
/*  24:    */ import weka.core.Utils;
/*  25:    */ 
/*  26:    */ public class CostSensitiveClassifierSplitEvaluator
/*  27:    */   extends ClassifierSplitEvaluator
/*  28:    */ {
/*  29:    */   static final long serialVersionUID = -8069566663019501276L;
/*  30:116 */   protected File m_OnDemandDirectory = new File(System.getProperty("user.dir"));
/*  31:    */   private static final int RESULT_SIZE = 33;
/*  32:    */   
/*  33:    */   public String globalInfo()
/*  34:    */   {
/*  35:129 */     return " SplitEvaluator that produces results for a classification scheme on a nominal class attribute, including weighted misclassification costs.";
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Enumeration<Option> listOptions()
/*  39:    */   {
/*  40:142 */     Vector<Option> newVector = new Vector(1);
/*  41:    */     
/*  42:144 */     newVector.addAll(Collections.list(super.listOptions()));
/*  43:    */     
/*  44:146 */     newVector.addElement(new Option("\tName of a directory to search for cost files when loading\n\tcosts on demand (default current directory).", "D", 1, "-D <directory>"));
/*  45:    */     
/*  46:    */ 
/*  47:    */ 
/*  48:    */ 
/*  49:151 */     return newVector.elements();
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setOptions(String[] options)
/*  53:    */     throws Exception
/*  54:    */   {
/*  55:214 */     String demandDir = Utils.getOption('D', options);
/*  56:215 */     if (demandDir.length() != 0) {
/*  57:216 */       setOnDemandDirectory(new File(demandDir));
/*  58:    */     }
/*  59:219 */     super.setOptions(options);
/*  60:    */     
/*  61:221 */     Utils.checkForRemainingOptions(options);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public String[] getOptions()
/*  65:    */   {
/*  66:232 */     Vector<String> options = new Vector();
/*  67:    */     
/*  68:234 */     options.add("-D");
/*  69:235 */     options.add("" + getOnDemandDirectory());
/*  70:    */     
/*  71:237 */     Collections.addAll(options, super.getOptions());
/*  72:    */     
/*  73:239 */     return (String[])options.toArray(new String[0]);
/*  74:    */   }
/*  75:    */   
/*  76:    */   public String onDemandDirectoryTipText()
/*  77:    */   {
/*  78:249 */     return "The directory to look in for cost files. This directory will be searched for cost files when loading on demand.";
/*  79:    */   }
/*  80:    */   
/*  81:    */   public File getOnDemandDirectory()
/*  82:    */   {
/*  83:261 */     return this.m_OnDemandDirectory;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public void setOnDemandDirectory(File newDir)
/*  87:    */   {
/*  88:272 */     if (newDir.isDirectory()) {
/*  89:273 */       this.m_OnDemandDirectory = newDir;
/*  90:    */     } else {
/*  91:275 */       this.m_OnDemandDirectory = new File(newDir.getParent());
/*  92:    */     }
/*  93:    */   }
/*  94:    */   
/*  95:    */   public Object[] getResultTypes()
/*  96:    */   {
/*  97:289 */     int addm = this.m_AdditionalMeasures != null ? this.m_AdditionalMeasures.length : 0;
/*  98:290 */     Object[] resultTypes = new Object[33 + addm];
/*  99:291 */     Double doub = new Double(0.0D);
/* 100:292 */     int current = 0;
/* 101:293 */     resultTypes[(current++)] = doub;
/* 102:294 */     resultTypes[(current++)] = doub;
/* 103:    */     
/* 104:296 */     resultTypes[(current++)] = doub;
/* 105:297 */     resultTypes[(current++)] = doub;
/* 106:298 */     resultTypes[(current++)] = doub;
/* 107:299 */     resultTypes[(current++)] = doub;
/* 108:300 */     resultTypes[(current++)] = doub;
/* 109:301 */     resultTypes[(current++)] = doub;
/* 110:302 */     resultTypes[(current++)] = doub;
/* 111:303 */     resultTypes[(current++)] = doub;
/* 112:    */     
/* 113:305 */     resultTypes[(current++)] = doub;
/* 114:306 */     resultTypes[(current++)] = doub;
/* 115:307 */     resultTypes[(current++)] = doub;
/* 116:308 */     resultTypes[(current++)] = doub;
/* 117:    */     
/* 118:310 */     resultTypes[(current++)] = doub;
/* 119:311 */     resultTypes[(current++)] = doub;
/* 120:312 */     resultTypes[(current++)] = doub;
/* 121:313 */     resultTypes[(current++)] = doub;
/* 122:314 */     resultTypes[(current++)] = doub;
/* 123:315 */     resultTypes[(current++)] = doub;
/* 124:    */     
/* 125:317 */     resultTypes[(current++)] = doub;
/* 126:318 */     resultTypes[(current++)] = doub;
/* 127:319 */     resultTypes[(current++)] = doub;
/* 128:    */     
/* 129:    */ 
/* 130:322 */     resultTypes[(current++)] = doub;
/* 131:323 */     resultTypes[(current++)] = doub;
/* 132:324 */     resultTypes[(current++)] = doub;
/* 133:325 */     resultTypes[(current++)] = doub;
/* 134:326 */     resultTypes[(current++)] = doub;
/* 135:327 */     resultTypes[(current++)] = doub;
/* 136:    */     
/* 137:    */ 
/* 138:330 */     resultTypes[(current++)] = doub;
/* 139:331 */     resultTypes[(current++)] = doub;
/* 140:332 */     resultTypes[(current++)] = doub;
/* 141:    */     
/* 142:334 */     resultTypes[(current++)] = "";
/* 143:337 */     for (int i = 0; i < addm; i++) {
/* 144:338 */       resultTypes[(current++)] = doub;
/* 145:    */     }
/* 146:340 */     if (current != 33 + addm) {
/* 147:341 */       throw new Error("ResultTypes didn't fit RESULT_SIZE");
/* 148:    */     }
/* 149:343 */     return resultTypes;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public String[] getResultNames()
/* 153:    */   {
/* 154:354 */     int addm = this.m_AdditionalMeasures != null ? this.m_AdditionalMeasures.length : 0;
/* 155:355 */     String[] resultNames = new String[33 + addm];
/* 156:356 */     int current = 0;
/* 157:357 */     resultNames[(current++)] = "Number_of_training_instances";
/* 158:358 */     resultNames[(current++)] = "Number_of_testing_instances";
/* 159:    */     
/* 160:    */ 
/* 161:361 */     resultNames[(current++)] = "Number_correct";
/* 162:362 */     resultNames[(current++)] = "Number_incorrect";
/* 163:363 */     resultNames[(current++)] = "Number_unclassified";
/* 164:364 */     resultNames[(current++)] = "Percent_correct";
/* 165:365 */     resultNames[(current++)] = "Percent_incorrect";
/* 166:366 */     resultNames[(current++)] = "Percent_unclassified";
/* 167:367 */     resultNames[(current++)] = "Total_cost";
/* 168:368 */     resultNames[(current++)] = "Average_cost";
/* 169:    */     
/* 170:    */ 
/* 171:371 */     resultNames[(current++)] = "Mean_absolute_error";
/* 172:372 */     resultNames[(current++)] = "Root_mean_squared_error";
/* 173:373 */     resultNames[(current++)] = "Relative_absolute_error";
/* 174:374 */     resultNames[(current++)] = "Root_relative_squared_error";
/* 175:    */     
/* 176:    */ 
/* 177:377 */     resultNames[(current++)] = "SF_prior_entropy";
/* 178:378 */     resultNames[(current++)] = "SF_scheme_entropy";
/* 179:379 */     resultNames[(current++)] = "SF_entropy_gain";
/* 180:380 */     resultNames[(current++)] = "SF_mean_prior_entropy";
/* 181:381 */     resultNames[(current++)] = "SF_mean_scheme_entropy";
/* 182:382 */     resultNames[(current++)] = "SF_mean_entropy_gain";
/* 183:    */     
/* 184:    */ 
/* 185:385 */     resultNames[(current++)] = "KB_information";
/* 186:386 */     resultNames[(current++)] = "KB_mean_information";
/* 187:387 */     resultNames[(current++)] = "KB_relative_information";
/* 188:    */     
/* 189:    */ 
/* 190:390 */     resultNames[(current++)] = "Elapsed_Time_training";
/* 191:391 */     resultNames[(current++)] = "Elapsed_Time_testing";
/* 192:392 */     resultNames[(current++)] = "UserCPU_Time_training";
/* 193:393 */     resultNames[(current++)] = "UserCPU_Time_testing";
/* 194:394 */     resultNames[(current++)] = "UserCPU_Time_millis_training";
/* 195:395 */     resultNames[(current++)] = "UserCPU_Time_millis_testing";
/* 196:    */     
/* 197:    */ 
/* 198:398 */     resultNames[(current++)] = "Serialized_Model_Size";
/* 199:399 */     resultNames[(current++)] = "Serialized_Train_Set_Size";
/* 200:400 */     resultNames[(current++)] = "Serialized_Test_Set_Size";
/* 201:    */     
/* 202:    */ 
/* 203:403 */     resultNames[(current++)] = "Summary";
/* 204:405 */     for (int i = 0; i < addm; i++) {
/* 205:406 */       resultNames[(current++)] = this.m_AdditionalMeasures[i];
/* 206:    */     }
/* 207:408 */     if (current != 33 + addm) {
/* 208:409 */       throw new Error("ResultNames didn't fit RESULT_SIZE");
/* 209:    */     }
/* 210:411 */     return resultNames;
/* 211:    */   }
/* 212:    */   
/* 213:    */   public Object[] getResult(Instances train, Instances test)
/* 214:    */     throws Exception
/* 215:    */   {
/* 216:428 */     if (train.classAttribute().type() != 1) {
/* 217:429 */       throw new Exception("Class attribute is not nominal!");
/* 218:    */     }
/* 219:431 */     if (this.m_Template == null) {
/* 220:432 */       throw new Exception("No classifier has been specified");
/* 221:    */     }
/* 222:434 */     ThreadMXBean thMonitor = ManagementFactory.getThreadMXBean();
/* 223:435 */     boolean canMeasureCPUTime = thMonitor.isThreadCpuTimeSupported();
/* 224:436 */     if ((canMeasureCPUTime) && (!thMonitor.isThreadCpuTimeEnabled())) {
/* 225:437 */       thMonitor.setThreadCpuTimeEnabled(true);
/* 226:    */     }
/* 227:440 */     int addm = this.m_AdditionalMeasures != null ? this.m_AdditionalMeasures.length : 0;
/* 228:441 */     Object[] result = new Object[33 + addm];
/* 229:442 */     long thID = Thread.currentThread().getId();
/* 230:443 */     long CPUStartTime = -1L;long trainCPUTimeElapsed = -1L;long testCPUTimeElapsed = -1L;
/* 231:    */     
/* 232:445 */     String costName = train.relationName() + CostMatrix.FILE_EXTENSION;
/* 233:446 */     File costFile = new File(getOnDemandDirectory(), costName);
/* 234:447 */     if (!costFile.exists()) {
/* 235:448 */       throw new Exception("On-demand cost file doesn't exist: " + costFile);
/* 236:    */     }
/* 237:450 */     CostMatrix costMatrix = new CostMatrix(new BufferedReader(new FileReader(costFile)));
/* 238:    */     
/* 239:    */ 
/* 240:453 */     Evaluation eval = new Evaluation(train, costMatrix);
/* 241:454 */     this.m_Classifier = AbstractClassifier.makeCopy(this.m_Template);
/* 242:    */     
/* 243:456 */     long trainTimeStart = System.currentTimeMillis();
/* 244:457 */     if (canMeasureCPUTime) {
/* 245:458 */       CPUStartTime = thMonitor.getThreadUserTime(thID);
/* 246:    */     }
/* 247:460 */     this.m_Classifier.buildClassifier(train);
/* 248:461 */     if (canMeasureCPUTime) {
/* 249:462 */       trainCPUTimeElapsed = thMonitor.getThreadUserTime(thID) - CPUStartTime;
/* 250:    */     }
/* 251:464 */     long trainTimeElapsed = System.currentTimeMillis() - trainTimeStart;
/* 252:465 */     long testTimeStart = System.currentTimeMillis();
/* 253:466 */     if (canMeasureCPUTime) {
/* 254:467 */       CPUStartTime = thMonitor.getThreadUserTime(thID);
/* 255:    */     }
/* 256:469 */     eval.evaluateModel(this.m_Classifier, test, new Object[0]);
/* 257:470 */     if (canMeasureCPUTime) {
/* 258:471 */       testCPUTimeElapsed = thMonitor.getThreadUserTime(thID) - CPUStartTime;
/* 259:    */     }
/* 260:473 */     long testTimeElapsed = System.currentTimeMillis() - testTimeStart;
/* 261:474 */     thMonitor = null;
/* 262:    */     
/* 263:476 */     this.m_result = eval.toSummaryString();
/* 264:    */     
/* 265:    */ 
/* 266:479 */     int current = 0;
/* 267:480 */     result[(current++)] = new Double(train.numInstances());
/* 268:481 */     result[(current++)] = new Double(eval.numInstances());
/* 269:    */     
/* 270:483 */     result[(current++)] = new Double(eval.correct());
/* 271:484 */     result[(current++)] = new Double(eval.incorrect());
/* 272:485 */     result[(current++)] = new Double(eval.unclassified());
/* 273:486 */     result[(current++)] = new Double(eval.pctCorrect());
/* 274:487 */     result[(current++)] = new Double(eval.pctIncorrect());
/* 275:488 */     result[(current++)] = new Double(eval.pctUnclassified());
/* 276:489 */     result[(current++)] = new Double(eval.totalCost());
/* 277:490 */     result[(current++)] = new Double(eval.avgCost());
/* 278:    */     
/* 279:492 */     result[(current++)] = new Double(eval.meanAbsoluteError());
/* 280:493 */     result[(current++)] = new Double(eval.rootMeanSquaredError());
/* 281:494 */     result[(current++)] = new Double(eval.relativeAbsoluteError());
/* 282:495 */     result[(current++)] = new Double(eval.rootRelativeSquaredError());
/* 283:    */     
/* 284:497 */     result[(current++)] = new Double(eval.SFPriorEntropy());
/* 285:498 */     result[(current++)] = new Double(eval.SFSchemeEntropy());
/* 286:499 */     result[(current++)] = new Double(eval.SFEntropyGain());
/* 287:500 */     result[(current++)] = new Double(eval.SFMeanPriorEntropy());
/* 288:501 */     result[(current++)] = new Double(eval.SFMeanSchemeEntropy());
/* 289:502 */     result[(current++)] = new Double(eval.SFMeanEntropyGain());
/* 290:    */     
/* 291:    */ 
/* 292:505 */     result[(current++)] = new Double(eval.KBInformation());
/* 293:506 */     result[(current++)] = new Double(eval.KBMeanInformation());
/* 294:507 */     result[(current++)] = new Double(eval.KBRelativeInformation());
/* 295:    */     
/* 296:    */ 
/* 297:510 */     result[(current++)] = new Double(trainTimeElapsed / 1000.0D);
/* 298:511 */     result[(current++)] = new Double(testTimeElapsed / 1000.0D);
/* 299:512 */     if (canMeasureCPUTime)
/* 300:    */     {
/* 301:513 */       result[(current++)] = new Double(trainCPUTimeElapsed / 1000000.0D / 1000.0D);
/* 302:    */       
/* 303:515 */       result[(current++)] = new Double(testCPUTimeElapsed / 1000000.0D / 1000.0D);
/* 304:    */       
/* 305:517 */       result[(current++)] = new Double(trainCPUTimeElapsed / 1000000.0D);
/* 306:    */       
/* 307:519 */       result[(current++)] = new Double(testCPUTimeElapsed / 1000000.0D);
/* 308:    */     }
/* 309:    */     else
/* 310:    */     {
/* 311:521 */       result[(current++)] = new Double(Utils.missingValue());
/* 312:522 */       result[(current++)] = new Double(Utils.missingValue());
/* 313:523 */       result[(current++)] = new Double(Utils.missingValue());
/* 314:524 */       result[(current++)] = new Double(Utils.missingValue());
/* 315:    */     }
/* 316:528 */     ByteArrayOutputStream bastream = new ByteArrayOutputStream();
/* 317:529 */     ObjectOutputStream oostream = new ObjectOutputStream(bastream);
/* 318:530 */     oostream.writeObject(this.m_Classifier);
/* 319:531 */     result[(current++)] = new Double(bastream.size());
/* 320:532 */     bastream = new ByteArrayOutputStream();
/* 321:533 */     oostream = new ObjectOutputStream(bastream);
/* 322:534 */     oostream.writeObject(train);
/* 323:535 */     result[(current++)] = new Double(bastream.size());
/* 324:536 */     bastream = new ByteArrayOutputStream();
/* 325:537 */     oostream = new ObjectOutputStream(bastream);
/* 326:538 */     oostream.writeObject(test);
/* 327:539 */     result[(current++)] = new Double(bastream.size());
/* 328:541 */     if ((this.m_Classifier instanceof Summarizable)) {
/* 329:542 */       result[(current++)] = ((Summarizable)this.m_Classifier).toSummaryString();
/* 330:    */     } else {
/* 331:544 */       result[(current++)] = null;
/* 332:    */     }
/* 333:547 */     for (int i = 0; i < addm; i++) {
/* 334:548 */       if (this.m_doesProduce[i] != 0) {
/* 335:    */         try
/* 336:    */         {
/* 337:550 */           double dv = ((AdditionalMeasureProducer)this.m_Classifier).getMeasure(this.m_AdditionalMeasures[i]);
/* 338:552 */           if (!Utils.isMissingValue(dv))
/* 339:    */           {
/* 340:553 */             Double value = new Double(dv);
/* 341:554 */             result[(current++)] = value;
/* 342:    */           }
/* 343:    */           else
/* 344:    */           {
/* 345:556 */             result[(current++)] = null;
/* 346:    */           }
/* 347:    */         }
/* 348:    */         catch (Exception ex)
/* 349:    */         {
/* 350:559 */           System.err.println(ex);
/* 351:    */         }
/* 352:    */       } else {
/* 353:562 */         result[(current++)] = null;
/* 354:    */       }
/* 355:    */     }
/* 356:566 */     if (current != 33 + addm) {
/* 357:567 */       throw new Error("Results didn't fit RESULT_SIZE");
/* 358:    */     }
/* 359:570 */     this.m_Evaluation = eval;
/* 360:    */     
/* 361:572 */     return result;
/* 362:    */   }
/* 363:    */   
/* 364:    */   public String toString()
/* 365:    */   {
/* 366:583 */     String result = "CostSensitiveClassifierSplitEvaluator: ";
/* 367:584 */     if (this.m_Template == null) {
/* 368:585 */       return result + "<null> classifier";
/* 369:    */     }
/* 370:587 */     return result + this.m_Template.getClass().getName() + " " + this.m_ClassifierOptions + "(version " + this.m_ClassifierVersion + ")";
/* 371:    */   }
/* 372:    */   
/* 373:    */   public String getRevision()
/* 374:    */   {
/* 375:598 */     return RevisionUtils.extract("$Revision: 11323 $");
/* 376:    */   }
/* 377:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.CostSensitiveClassifierSplitEvaluator
 * JD-Core Version:    0.7.0.1
 */