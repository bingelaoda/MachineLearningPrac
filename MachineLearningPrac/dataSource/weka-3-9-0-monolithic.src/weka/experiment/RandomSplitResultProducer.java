/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.PrintStream;
/*   5:    */ import java.util.Calendar;
/*   6:    */ import java.util.Collections;
/*   7:    */ import java.util.Enumeration;
/*   8:    */ import java.util.Random;
/*   9:    */ import java.util.TimeZone;
/*  10:    */ import java.util.Vector;
/*  11:    */ import weka.core.AdditionalMeasureProducer;
/*  12:    */ import weka.core.Attribute;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.OptionHandler;
/*  17:    */ import weka.core.RevisionHandler;
/*  18:    */ import weka.core.RevisionUtils;
/*  19:    */ import weka.core.Utils;
/*  20:    */ 
/*  21:    */ public class RandomSplitResultProducer
/*  22:    */   implements ResultProducer, OptionHandler, AdditionalMeasureProducer, RevisionHandler
/*  23:    */ {
/*  24:    */   static final long serialVersionUID = 1403798165056795073L;
/*  25:    */   protected Instances m_Instances;
/*  26:139 */   protected ResultListener m_ResultListener = new CSVResultListener();
/*  27:142 */   protected double m_TrainPercent = 66.0D;
/*  28:145 */   protected boolean m_randomize = true;
/*  29:148 */   protected SplitEvaluator m_SplitEvaluator = new ClassifierSplitEvaluator();
/*  30:151 */   protected String[] m_AdditionalMeasures = null;
/*  31:154 */   protected boolean m_debugOutput = false;
/*  32:157 */   protected OutputZipper m_ZipDest = null;
/*  33:160 */   protected File m_OutputFile = new File(new File(System.getProperty("user.dir")), "splitEvalutorOut.zip");
/*  34:164 */   public static String DATASET_FIELD_NAME = "Dataset";
/*  35:167 */   public static String RUN_FIELD_NAME = "Run";
/*  36:170 */   public static String TIMESTAMP_FIELD_NAME = "Date_time";
/*  37:    */   
/*  38:    */   public String globalInfo()
/*  39:    */   {
/*  40:179 */     return "Generates a single train/test split and calls the appropriate SplitEvaluator to generate some results.";
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void setInstances(Instances instances)
/*  44:    */   {
/*  45:191 */     this.m_Instances = instances;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setAdditionalMeasures(String[] additionalMeasures)
/*  49:    */   {
/*  50:204 */     this.m_AdditionalMeasures = additionalMeasures;
/*  51:206 */     if (this.m_SplitEvaluator != null)
/*  52:    */     {
/*  53:207 */       System.err.println("RandomSplitResultProducer: setting additional measures for split evaluator");
/*  54:    */       
/*  55:209 */       this.m_SplitEvaluator.setAdditionalMeasures(this.m_AdditionalMeasures);
/*  56:    */     }
/*  57:    */   }
/*  58:    */   
/*  59:    */   public Enumeration<String> enumerateMeasures()
/*  60:    */   {
/*  61:221 */     Vector<String> newVector = new Vector();
/*  62:222 */     if ((this.m_SplitEvaluator instanceof AdditionalMeasureProducer))
/*  63:    */     {
/*  64:223 */       Enumeration<String> en = ((AdditionalMeasureProducer)this.m_SplitEvaluator).enumerateMeasures();
/*  65:225 */       while (en.hasMoreElements())
/*  66:    */       {
/*  67:226 */         String mname = (String)en.nextElement();
/*  68:227 */         newVector.add(mname);
/*  69:    */       }
/*  70:    */     }
/*  71:230 */     return newVector.elements();
/*  72:    */   }
/*  73:    */   
/*  74:    */   public double getMeasure(String additionalMeasureName)
/*  75:    */   {
/*  76:242 */     if ((this.m_SplitEvaluator instanceof AdditionalMeasureProducer)) {
/*  77:243 */       return ((AdditionalMeasureProducer)this.m_SplitEvaluator).getMeasure(additionalMeasureName);
/*  78:    */     }
/*  79:246 */     throw new IllegalArgumentException("RandomSplitResultProducer: Can't return value for : " + additionalMeasureName + ". " + this.m_SplitEvaluator.getClass().getName() + " " + "is not an AdditionalMeasureProducer");
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void setResultListener(ResultListener listener)
/*  83:    */   {
/*  84:261 */     this.m_ResultListener = listener;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public static Double getTimestamp()
/*  88:    */   {
/*  89:272 */     Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
/*  90:273 */     double timestamp = now.get(1) * 10000 + (now.get(2) + 1) * 100 + now.get(5) + now.get(11) / 100.0D + now.get(12) / 10000.0D;
/*  91:    */     
/*  92:    */ 
/*  93:    */ 
/*  94:277 */     return new Double(timestamp);
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void preProcess()
/*  98:    */     throws Exception
/*  99:    */   {
/* 100:288 */     if (this.m_SplitEvaluator == null) {
/* 101:289 */       throw new Exception("No SplitEvalutor set");
/* 102:    */     }
/* 103:291 */     if (this.m_ResultListener == null) {
/* 104:292 */       throw new Exception("No ResultListener set");
/* 105:    */     }
/* 106:294 */     this.m_ResultListener.preProcess(this);
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void postProcess()
/* 110:    */     throws Exception
/* 111:    */   {
/* 112:307 */     this.m_ResultListener.postProcess(this);
/* 113:308 */     if ((this.m_debugOutput) && 
/* 114:309 */       (this.m_ZipDest != null))
/* 115:    */     {
/* 116:310 */       this.m_ZipDest.finished();
/* 117:311 */       this.m_ZipDest = null;
/* 118:    */     }
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void doRunKeys(int run)
/* 122:    */     throws Exception
/* 123:    */   {
/* 124:326 */     if (this.m_Instances == null) {
/* 125:327 */       throw new Exception("No Instances set");
/* 126:    */     }
/* 127:330 */     Object[] seKey = this.m_SplitEvaluator.getKey();
/* 128:331 */     Object[] key = new Object[seKey.length + 2];
/* 129:332 */     key[0] = Utils.backQuoteChars(this.m_Instances.relationName());
/* 130:333 */     key[1] = ("" + run);
/* 131:334 */     System.arraycopy(seKey, 0, key, 2, seKey.length);
/* 132:335 */     if (this.m_ResultListener.isResultRequired(this, key)) {
/* 133:    */       try
/* 134:    */       {
/* 135:337 */         this.m_ResultListener.acceptResult(this, key, null);
/* 136:    */       }
/* 137:    */       catch (Exception ex)
/* 138:    */       {
/* 139:340 */         throw ex;
/* 140:    */       }
/* 141:    */     }
/* 142:    */   }
/* 143:    */   
/* 144:    */   public void doRun(int run)
/* 145:    */     throws Exception
/* 146:    */   {
/* 147:356 */     if ((getRawOutput()) && 
/* 148:357 */       (this.m_ZipDest == null)) {
/* 149:358 */       this.m_ZipDest = new OutputZipper(this.m_OutputFile);
/* 150:    */     }
/* 151:362 */     if (this.m_Instances == null) {
/* 152:363 */       throw new Exception("No Instances set");
/* 153:    */     }
/* 154:366 */     Object[] seKey = this.m_SplitEvaluator.getKey();
/* 155:367 */     Object[] key = new Object[seKey.length + 2];
/* 156:368 */     key[0] = Utils.backQuoteChars(this.m_Instances.relationName());
/* 157:369 */     key[1] = ("" + run);
/* 158:370 */     System.arraycopy(seKey, 0, key, 2, seKey.length);
/* 159:371 */     if (this.m_ResultListener.isResultRequired(this, key))
/* 160:    */     {
/* 161:374 */       Instances runInstances = new Instances(this.m_Instances);
/* 162:    */       Instances test;
/* 163:    */       Instances train;
/* 164:    */       Instances test;
/* 165:379 */       if (!this.m_randomize)
/* 166:    */       {
/* 167:382 */         int trainSize = Utils.round(runInstances.numInstances() * this.m_TrainPercent / 100.0D);
/* 168:    */         
/* 169:384 */         int testSize = runInstances.numInstances() - trainSize;
/* 170:385 */         Instances train = new Instances(runInstances, 0, trainSize);
/* 171:386 */         test = new Instances(runInstances, trainSize, testSize);
/* 172:    */       }
/* 173:    */       else
/* 174:    */       {
/* 175:388 */         Random rand = new Random(run);
/* 176:389 */         runInstances.randomize(rand);
/* 177:392 */         if (runInstances.classAttribute().isNominal())
/* 178:    */         {
/* 179:395 */           int numClasses = runInstances.numClasses();
/* 180:396 */           Instances[] subsets = new Instances[numClasses + 1];
/* 181:397 */           for (int i = 0; i < numClasses + 1; i++) {
/* 182:398 */             subsets[i] = new Instances(runInstances, 10);
/* 183:    */           }
/* 184:402 */           Enumeration<Instance> e = runInstances.enumerateInstances();
/* 185:403 */           while (e.hasMoreElements())
/* 186:    */           {
/* 187:404 */             Instance inst = (Instance)e.nextElement();
/* 188:405 */             if (inst.classIsMissing()) {
/* 189:406 */               subsets[numClasses].add(inst);
/* 190:    */             } else {
/* 191:408 */               subsets[((int)inst.classValue())].add(inst);
/* 192:    */             }
/* 193:    */           }
/* 194:413 */           for (int i = 0; i < numClasses + 1; i++) {
/* 195:414 */             subsets[i].compactify();
/* 196:    */           }
/* 197:418 */           Instances train = new Instances(runInstances, runInstances.numInstances());
/* 198:419 */           Instances test = new Instances(runInstances, runInstances.numInstances());
/* 199:420 */           for (int i = 0; i < numClasses + 1; i++)
/* 200:    */           {
/* 201:421 */             int trainSize = Utils.probRound(subsets[i].numInstances() * this.m_TrainPercent / 100.0D, rand);
/* 202:423 */             for (int j = 0; j < trainSize; j++) {
/* 203:424 */               train.add(subsets[i].instance(j));
/* 204:    */             }
/* 205:426 */             for (int j = trainSize; j < subsets[i].numInstances(); j++) {
/* 206:427 */               test.add(subsets[i].instance(j));
/* 207:    */             }
/* 208:430 */             subsets[i] = null;
/* 209:    */           }
/* 210:432 */           train.compactify();
/* 211:433 */           test.compactify();
/* 212:    */           
/* 213:    */ 
/* 214:436 */           train.randomize(rand);
/* 215:437 */           test.randomize(rand);
/* 216:    */         }
/* 217:    */         else
/* 218:    */         {
/* 219:441 */           int trainSize = Utils.probRound(runInstances.numInstances() * this.m_TrainPercent / 100.0D, rand);
/* 220:    */           
/* 221:443 */           int testSize = runInstances.numInstances() - trainSize;
/* 222:444 */           train = new Instances(runInstances, 0, trainSize);
/* 223:445 */           test = new Instances(runInstances, trainSize, testSize);
/* 224:    */         }
/* 225:    */       }
/* 226:    */       try
/* 227:    */       {
/* 228:449 */         Object[] seResults = this.m_SplitEvaluator.getResult(train, test);
/* 229:450 */         Object[] results = new Object[seResults.length + 1];
/* 230:451 */         results[0] = getTimestamp();
/* 231:452 */         System.arraycopy(seResults, 0, results, 1, seResults.length);
/* 232:453 */         if (this.m_debugOutput)
/* 233:    */         {
/* 234:454 */           String resultName = ("" + run + "." + Utils.backQuoteChars(runInstances.relationName()) + "." + this.m_SplitEvaluator.toString()).replace(' ', '_');
/* 235:    */           
/* 236:    */ 
/* 237:457 */           resultName = Utils.removeSubstring(resultName, "weka.classifiers.");
/* 238:458 */           resultName = Utils.removeSubstring(resultName, "weka.filters.");
/* 239:459 */           resultName = Utils.removeSubstring(resultName, "weka.attributeSelection.");
/* 240:    */           
/* 241:461 */           this.m_ZipDest.zipit(this.m_SplitEvaluator.getRawResultOutput(), resultName);
/* 242:    */         }
/* 243:463 */         this.m_ResultListener.acceptResult(this, key, results);
/* 244:    */       }
/* 245:    */       catch (Exception ex)
/* 246:    */       {
/* 247:466 */         throw ex;
/* 248:    */       }
/* 249:    */     }
/* 250:    */   }
/* 251:    */   
/* 252:    */   public String[] getKeyNames()
/* 253:    */   {
/* 254:480 */     String[] keyNames = this.m_SplitEvaluator.getKeyNames();
/* 255:    */     
/* 256:482 */     String[] newKeyNames = new String[keyNames.length + 2];
/* 257:483 */     newKeyNames[0] = DATASET_FIELD_NAME;
/* 258:484 */     newKeyNames[1] = RUN_FIELD_NAME;
/* 259:485 */     System.arraycopy(keyNames, 0, newKeyNames, 2, keyNames.length);
/* 260:486 */     return newKeyNames;
/* 261:    */   }
/* 262:    */   
/* 263:    */   public Object[] getKeyTypes()
/* 264:    */   {
/* 265:499 */     Object[] keyTypes = this.m_SplitEvaluator.getKeyTypes();
/* 266:    */     
/* 267:501 */     Object[] newKeyTypes = new String[keyTypes.length + 2];
/* 268:502 */     newKeyTypes[0] = new String();
/* 269:503 */     newKeyTypes[1] = new String();
/* 270:504 */     System.arraycopy(keyTypes, 0, newKeyTypes, 2, keyTypes.length);
/* 271:505 */     return newKeyTypes;
/* 272:    */   }
/* 273:    */   
/* 274:    */   public String[] getResultNames()
/* 275:    */   {
/* 276:517 */     String[] resultNames = this.m_SplitEvaluator.getResultNames();
/* 277:    */     
/* 278:519 */     String[] newResultNames = new String[resultNames.length + 1];
/* 279:520 */     newResultNames[0] = TIMESTAMP_FIELD_NAME;
/* 280:521 */     System.arraycopy(resultNames, 0, newResultNames, 1, resultNames.length);
/* 281:522 */     return newResultNames;
/* 282:    */   }
/* 283:    */   
/* 284:    */   public Object[] getResultTypes()
/* 285:    */   {
/* 286:535 */     Object[] resultTypes = this.m_SplitEvaluator.getResultTypes();
/* 287:    */     
/* 288:537 */     Object[] newResultTypes = new Object[resultTypes.length + 1];
/* 289:538 */     newResultTypes[0] = new Double(0.0D);
/* 290:539 */     System.arraycopy(resultTypes, 0, newResultTypes, 1, resultTypes.length);
/* 291:540 */     return newResultTypes;
/* 292:    */   }
/* 293:    */   
/* 294:    */   public String getCompatibilityState()
/* 295:    */   {
/* 296:559 */     String result = "-P " + this.m_TrainPercent;
/* 297:560 */     if (!getRandomizeData()) {
/* 298:561 */       result = result + " -R";
/* 299:    */     }
/* 300:563 */     if (this.m_SplitEvaluator == null) {
/* 301:564 */       result = result + " <null SplitEvaluator>";
/* 302:    */     } else {
/* 303:566 */       result = result + " -W " + this.m_SplitEvaluator.getClass().getName();
/* 304:    */     }
/* 305:568 */     return result + " --";
/* 306:    */   }
/* 307:    */   
/* 308:    */   public String outputFileTipText()
/* 309:    */   {
/* 310:578 */     return "Set the destination for saving raw output. If the rawOutput option is selected, then output from the splitEvaluator for individual train-test splits is saved. If the destination is a directory, then each output is saved to an individual gzip file; if the destination is a file, then each output is saved as an entry in a zip file.";
/* 311:    */   }
/* 312:    */   
/* 313:    */   public File getOutputFile()
/* 314:    */   {
/* 315:594 */     return this.m_OutputFile;
/* 316:    */   }
/* 317:    */   
/* 318:    */   public void setOutputFile(File newOutputFile)
/* 319:    */   {
/* 320:604 */     this.m_OutputFile = newOutputFile;
/* 321:    */   }
/* 322:    */   
/* 323:    */   public String randomizeDataTipText()
/* 324:    */   {
/* 325:614 */     return "Do not randomize dataset and do not perform probabilistic rounding if false";
/* 326:    */   }
/* 327:    */   
/* 328:    */   public boolean getRandomizeData()
/* 329:    */   {
/* 330:624 */     return this.m_randomize;
/* 331:    */   }
/* 332:    */   
/* 333:    */   public void setRandomizeData(boolean d)
/* 334:    */   {
/* 335:633 */     this.m_randomize = d;
/* 336:    */   }
/* 337:    */   
/* 338:    */   public String rawOutputTipText()
/* 339:    */   {
/* 340:643 */     return "Save raw output (useful for debugging). If set, then output is sent to the destination specified by outputFile";
/* 341:    */   }
/* 342:    */   
/* 343:    */   public boolean getRawOutput()
/* 344:    */   {
/* 345:653 */     return this.m_debugOutput;
/* 346:    */   }
/* 347:    */   
/* 348:    */   public void setRawOutput(boolean d)
/* 349:    */   {
/* 350:662 */     this.m_debugOutput = d;
/* 351:    */   }
/* 352:    */   
/* 353:    */   public String trainPercentTipText()
/* 354:    */   {
/* 355:672 */     return "Set the percentage of data to use for training.";
/* 356:    */   }
/* 357:    */   
/* 358:    */   public double getTrainPercent()
/* 359:    */   {
/* 360:682 */     return this.m_TrainPercent;
/* 361:    */   }
/* 362:    */   
/* 363:    */   public void setTrainPercent(double newTrainPercent)
/* 364:    */   {
/* 365:692 */     this.m_TrainPercent = newTrainPercent;
/* 366:    */   }
/* 367:    */   
/* 368:    */   public String splitEvaluatorTipText()
/* 369:    */   {
/* 370:702 */     return "The evaluator to apply to the test data. This may be a classifier, regression scheme etc.";
/* 371:    */   }
/* 372:    */   
/* 373:    */   public SplitEvaluator getSplitEvaluator()
/* 374:    */   {
/* 375:713 */     return this.m_SplitEvaluator;
/* 376:    */   }
/* 377:    */   
/* 378:    */   public void setSplitEvaluator(SplitEvaluator newSplitEvaluator)
/* 379:    */   {
/* 380:723 */     this.m_SplitEvaluator = newSplitEvaluator;
/* 381:724 */     this.m_SplitEvaluator.setAdditionalMeasures(this.m_AdditionalMeasures);
/* 382:    */   }
/* 383:    */   
/* 384:    */   public Enumeration<Option> listOptions()
/* 385:    */   {
/* 386:735 */     Vector<Option> newVector = new Vector(5);
/* 387:    */     
/* 388:737 */     newVector.addElement(new Option("\tThe percentage of instances to use for training.\n\t(default 66)", "P", 1, "-P <percent>"));
/* 389:    */     
/* 390:    */ 
/* 391:    */ 
/* 392:    */ 
/* 393:742 */     newVector.addElement(new Option("Save raw split evaluator output.", "D", 0, "-D"));
/* 394:    */     
/* 395:    */ 
/* 396:745 */     newVector.addElement(new Option("\tThe filename where raw output will be stored.\n\tIf a directory name is specified then then individual\n\toutputs will be gzipped, otherwise all output will be\n\tzipped to the named file. Use in conjuction with -D.\t(default splitEvalutorOut.zip)", "O", 1, "-O <file/directory name/path>"));
/* 397:    */     
/* 398:    */ 
/* 399:    */ 
/* 400:    */ 
/* 401:    */ 
/* 402:    */ 
/* 403:    */ 
/* 404:753 */     newVector.addElement(new Option("\tThe full class name of a SplitEvaluator.\n\teg: weka.experiment.ClassifierSplitEvaluator", "W", 1, "-W <class name>"));
/* 405:    */     
/* 406:    */ 
/* 407:    */ 
/* 408:    */ 
/* 409:758 */     newVector.addElement(new Option("\tSet when data is not to be randomized and the data sets' size.\n\tIs not to be determined via probabilistic rounding.", "R", 0, "-R"));
/* 410:764 */     if ((this.m_SplitEvaluator != null) && ((this.m_SplitEvaluator instanceof OptionHandler)))
/* 411:    */     {
/* 412:766 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to split evaluator " + this.m_SplitEvaluator.getClass().getName() + ":"));
/* 413:    */       
/* 414:    */ 
/* 415:769 */       newVector.addAll(Collections.list(((OptionHandler)this.m_SplitEvaluator).listOptions()));
/* 416:    */     }
/* 417:772 */     return newVector.elements();
/* 418:    */   }
/* 419:    */   
/* 420:    */   public void setOptions(String[] options)
/* 421:    */     throws Exception
/* 422:    */   {
/* 423:864 */     setRawOutput(Utils.getFlag('D', options));
/* 424:865 */     setRandomizeData(!Utils.getFlag('R', options));
/* 425:    */     
/* 426:867 */     String fName = Utils.getOption('O', options);
/* 427:868 */     if (fName.length() != 0) {
/* 428:869 */       setOutputFile(new File(fName));
/* 429:    */     }
/* 430:872 */     String trainPct = Utils.getOption('P', options);
/* 431:873 */     if (trainPct.length() != 0) {
/* 432:874 */       setTrainPercent(new Double(trainPct).doubleValue());
/* 433:    */     } else {
/* 434:876 */       setTrainPercent(66.0D);
/* 435:    */     }
/* 436:879 */     String seName = Utils.getOption('W', options);
/* 437:880 */     if (seName.length() == 0) {
/* 438:881 */       throw new Exception("A SplitEvaluator must be specified with the -W option.");
/* 439:    */     }
/* 440:887 */     setSplitEvaluator((SplitEvaluator)Utils.forName(SplitEvaluator.class, seName, null));
/* 441:889 */     if ((getSplitEvaluator() instanceof OptionHandler)) {
/* 442:890 */       ((OptionHandler)getSplitEvaluator()).setOptions(Utils.partitionOptions(options));
/* 443:    */     }
/* 444:    */   }
/* 445:    */   
/* 446:    */   public String[] getOptions()
/* 447:    */   {
/* 448:903 */     String[] seOptions = new String[0];
/* 449:904 */     if ((this.m_SplitEvaluator != null) && ((this.m_SplitEvaluator instanceof OptionHandler))) {
/* 450:906 */       seOptions = ((OptionHandler)this.m_SplitEvaluator).getOptions();
/* 451:    */     }
/* 452:909 */     String[] options = new String[seOptions.length + 9];
/* 453:910 */     int current = 0;
/* 454:    */     
/* 455:912 */     options[(current++)] = "-P";
/* 456:913 */     options[(current++)] = ("" + getTrainPercent());
/* 457:915 */     if (getRawOutput()) {
/* 458:916 */       options[(current++)] = "-D";
/* 459:    */     }
/* 460:919 */     if (!getRandomizeData()) {
/* 461:920 */       options[(current++)] = "-R";
/* 462:    */     }
/* 463:923 */     options[(current++)] = "-O";
/* 464:924 */     options[(current++)] = getOutputFile().getName();
/* 465:926 */     if (getSplitEvaluator() != null)
/* 466:    */     {
/* 467:927 */       options[(current++)] = "-W";
/* 468:928 */       options[(current++)] = getSplitEvaluator().getClass().getName();
/* 469:    */     }
/* 470:930 */     options[(current++)] = "--";
/* 471:    */     
/* 472:932 */     System.arraycopy(seOptions, 0, options, current, seOptions.length);
/* 473:933 */     current += seOptions.length;
/* 474:934 */     while (current < options.length) {
/* 475:935 */       options[(current++)] = "";
/* 476:    */     }
/* 477:937 */     return options;
/* 478:    */   }
/* 479:    */   
/* 480:    */   public String toString()
/* 481:    */   {
/* 482:948 */     String result = "RandomSplitResultProducer: ";
/* 483:949 */     result = result + getCompatibilityState();
/* 484:950 */     if (this.m_Instances == null) {
/* 485:951 */       result = result + ": <null Instances>";
/* 486:    */     } else {
/* 487:953 */       result = result + ": " + Utils.backQuoteChars(this.m_Instances.relationName());
/* 488:    */     }
/* 489:955 */     return result;
/* 490:    */   }
/* 491:    */   
/* 492:    */   public String getRevision()
/* 493:    */   {
/* 494:965 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 495:    */   }
/* 496:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.RandomSplitResultProducer
 * JD-Core Version:    0.7.0.1
 */