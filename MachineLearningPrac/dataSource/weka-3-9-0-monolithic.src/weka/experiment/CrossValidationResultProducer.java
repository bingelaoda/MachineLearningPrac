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
/*  13:    */ import weka.core.Instances;
/*  14:    */ import weka.core.Option;
/*  15:    */ import weka.core.OptionHandler;
/*  16:    */ import weka.core.RevisionHandler;
/*  17:    */ import weka.core.RevisionUtils;
/*  18:    */ import weka.core.Utils;
/*  19:    */ 
/*  20:    */ public class CrossValidationResultProducer
/*  21:    */   implements ResultProducer, OptionHandler, AdditionalMeasureProducer, RevisionHandler
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = -1580053925080091917L;
/*  24:    */   protected Instances m_Instances;
/*  25:135 */   protected ResultListener m_ResultListener = new CSVResultListener();
/*  26:138 */   protected int m_NumFolds = 10;
/*  27:141 */   protected boolean m_debugOutput = false;
/*  28:144 */   protected OutputZipper m_ZipDest = null;
/*  29:147 */   protected File m_OutputFile = new File(new File(System.getProperty("user.dir")), "splitEvalutorOut.zip");
/*  30:151 */   protected SplitEvaluator m_SplitEvaluator = new ClassifierSplitEvaluator();
/*  31:154 */   protected String[] m_AdditionalMeasures = null;
/*  32:157 */   public static String DATASET_FIELD_NAME = "Dataset";
/*  33:160 */   public static String RUN_FIELD_NAME = "Run";
/*  34:163 */   public static String FOLD_FIELD_NAME = "Fold";
/*  35:166 */   public static String TIMESTAMP_FIELD_NAME = "Date_time";
/*  36:    */   
/*  37:    */   public String globalInfo()
/*  38:    */   {
/*  39:175 */     return "Generates for each run, carries out an n-fold cross-validation, using the set SplitEvaluator to generate some results. If the class attribute is nominal, the dataset is stratified. Results for each fold are generated, so you may wish to use this in addition with an AveragingResultProducer to obtain averages for each run.";
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void setInstances(Instances instances)
/*  43:    */   {
/*  44:190 */     this.m_Instances = instances;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setResultListener(ResultListener listener)
/*  48:    */   {
/*  49:201 */     this.m_ResultListener = listener;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setAdditionalMeasures(String[] additionalMeasures)
/*  53:    */   {
/*  54:214 */     this.m_AdditionalMeasures = additionalMeasures;
/*  55:216 */     if (this.m_SplitEvaluator != null)
/*  56:    */     {
/*  57:217 */       System.err.println("CrossValidationResultProducer: setting additional measures for split evaluator");
/*  58:    */       
/*  59:219 */       this.m_SplitEvaluator.setAdditionalMeasures(this.m_AdditionalMeasures);
/*  60:    */     }
/*  61:    */   }
/*  62:    */   
/*  63:    */   public Enumeration<String> enumerateMeasures()
/*  64:    */   {
/*  65:231 */     Vector<String> newVector = new Vector();
/*  66:232 */     if ((this.m_SplitEvaluator instanceof AdditionalMeasureProducer))
/*  67:    */     {
/*  68:233 */       Enumeration<String> en = ((AdditionalMeasureProducer)this.m_SplitEvaluator).enumerateMeasures();
/*  69:235 */       while (en.hasMoreElements())
/*  70:    */       {
/*  71:236 */         String mname = (String)en.nextElement();
/*  72:237 */         newVector.addElement(mname);
/*  73:    */       }
/*  74:    */     }
/*  75:240 */     return newVector.elements();
/*  76:    */   }
/*  77:    */   
/*  78:    */   public double getMeasure(String additionalMeasureName)
/*  79:    */   {
/*  80:252 */     if ((this.m_SplitEvaluator instanceof AdditionalMeasureProducer)) {
/*  81:253 */       return ((AdditionalMeasureProducer)this.m_SplitEvaluator).getMeasure(additionalMeasureName);
/*  82:    */     }
/*  83:256 */     throw new IllegalArgumentException("CrossValidationResultProducer: Can't return value for : " + additionalMeasureName + ". " + this.m_SplitEvaluator.getClass().getName() + " " + "is not an AdditionalMeasureProducer");
/*  84:    */   }
/*  85:    */   
/*  86:    */   public static Double getTimestamp()
/*  87:    */   {
/*  88:271 */     Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
/*  89:272 */     double timestamp = now.get(1) * 10000 + (now.get(2) + 1) * 100 + now.get(5) + now.get(11) / 100.0D + now.get(12) / 10000.0D;
/*  90:    */     
/*  91:    */ 
/*  92:    */ 
/*  93:276 */     return new Double(timestamp);
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void preProcess()
/*  97:    */     throws Exception
/*  98:    */   {
/*  99:287 */     if (this.m_SplitEvaluator == null) {
/* 100:288 */       throw new Exception("No SplitEvalutor set");
/* 101:    */     }
/* 102:290 */     if (this.m_ResultListener == null) {
/* 103:291 */       throw new Exception("No ResultListener set");
/* 104:    */     }
/* 105:293 */     this.m_ResultListener.preProcess(this);
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void postProcess()
/* 109:    */     throws Exception
/* 110:    */   {
/* 111:306 */     this.m_ResultListener.postProcess(this);
/* 112:308 */     if ((this.m_debugOutput) && 
/* 113:309 */       (this.m_ZipDest != null))
/* 114:    */     {
/* 115:310 */       this.m_ZipDest.finished();
/* 116:311 */       this.m_ZipDest = null;
/* 117:    */     }
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void doRunKeys(int run)
/* 121:    */     throws Exception
/* 122:    */   {
/* 123:326 */     if (this.m_Instances == null) {
/* 124:327 */       throw new Exception("No Instances set");
/* 125:    */     }
/* 126:335 */     for (int fold = 0; fold < this.m_NumFolds; fold++)
/* 127:    */     {
/* 128:337 */       Object[] seKey = this.m_SplitEvaluator.getKey();
/* 129:338 */       Object[] key = new Object[seKey.length + 3];
/* 130:339 */       key[0] = Utils.backQuoteChars(this.m_Instances.relationName());
/* 131:340 */       key[1] = ("" + run);
/* 132:341 */       key[2] = ("" + (fold + 1));
/* 133:342 */       System.arraycopy(seKey, 0, key, 3, seKey.length);
/* 134:343 */       if (this.m_ResultListener.isResultRequired(this, key)) {
/* 135:    */         try
/* 136:    */         {
/* 137:345 */           this.m_ResultListener.acceptResult(this, key, null);
/* 138:    */         }
/* 139:    */         catch (Exception ex)
/* 140:    */         {
/* 141:348 */           throw ex;
/* 142:    */         }
/* 143:    */       }
/* 144:    */     }
/* 145:    */   }
/* 146:    */   
/* 147:    */   public void doRun(int run)
/* 148:    */     throws Exception
/* 149:    */   {
/* 150:365 */     if ((getRawOutput()) && 
/* 151:366 */       (this.m_ZipDest == null)) {
/* 152:367 */       this.m_ZipDest = new OutputZipper(this.m_OutputFile);
/* 153:    */     }
/* 154:371 */     if (this.m_Instances == null) {
/* 155:372 */       throw new Exception("No Instances set");
/* 156:    */     }
/* 157:375 */     Instances runInstances = new Instances(this.m_Instances);
/* 158:376 */     Random random = new Random(run);
/* 159:377 */     runInstances.randomize(random);
/* 160:378 */     if (runInstances.classAttribute().isNominal()) {
/* 161:379 */       runInstances.stratify(this.m_NumFolds);
/* 162:    */     }
/* 163:381 */     for (int fold = 0; fold < this.m_NumFolds; fold++)
/* 164:    */     {
/* 165:383 */       Object[] seKey = this.m_SplitEvaluator.getKey();
/* 166:384 */       Object[] key = new Object[seKey.length + 3];
/* 167:385 */       key[0] = Utils.backQuoteChars(this.m_Instances.relationName());
/* 168:386 */       key[1] = ("" + run);
/* 169:387 */       key[2] = ("" + (fold + 1));
/* 170:388 */       System.arraycopy(seKey, 0, key, 3, seKey.length);
/* 171:389 */       if (this.m_ResultListener.isResultRequired(this, key))
/* 172:    */       {
/* 173:390 */         Instances train = runInstances.trainCV(this.m_NumFolds, fold, random);
/* 174:391 */         Instances test = runInstances.testCV(this.m_NumFolds, fold);
/* 175:    */         try
/* 176:    */         {
/* 177:393 */           Object[] seResults = this.m_SplitEvaluator.getResult(train, test);
/* 178:394 */           Object[] results = new Object[seResults.length + 1];
/* 179:395 */           results[0] = getTimestamp();
/* 180:396 */           System.arraycopy(seResults, 0, results, 1, seResults.length);
/* 181:397 */           if (this.m_debugOutput)
/* 182:    */           {
/* 183:398 */             String resultName = ("" + run + "." + (fold + 1) + "." + Utils.backQuoteChars(runInstances.relationName()) + "." + this.m_SplitEvaluator.toString()).replace(' ', '_');
/* 184:    */             
/* 185:    */ 
/* 186:401 */             resultName = Utils.removeSubstring(resultName, "weka.classifiers.");
/* 187:402 */             resultName = Utils.removeSubstring(resultName, "weka.filters.");
/* 188:403 */             resultName = Utils.removeSubstring(resultName, "weka.attributeSelection.");
/* 189:    */             
/* 190:405 */             this.m_ZipDest.zipit(this.m_SplitEvaluator.getRawResultOutput(), resultName);
/* 191:    */           }
/* 192:407 */           this.m_ResultListener.acceptResult(this, key, results);
/* 193:    */         }
/* 194:    */         catch (Exception ex)
/* 195:    */         {
/* 196:410 */           throw ex;
/* 197:    */         }
/* 198:    */       }
/* 199:    */     }
/* 200:    */   }
/* 201:    */   
/* 202:    */   public String[] getKeyNames()
/* 203:    */   {
/* 204:425 */     String[] keyNames = this.m_SplitEvaluator.getKeyNames();
/* 205:    */     
/* 206:427 */     String[] newKeyNames = new String[keyNames.length + 3];
/* 207:428 */     newKeyNames[0] = DATASET_FIELD_NAME;
/* 208:429 */     newKeyNames[1] = RUN_FIELD_NAME;
/* 209:430 */     newKeyNames[2] = FOLD_FIELD_NAME;
/* 210:431 */     System.arraycopy(keyNames, 0, newKeyNames, 3, keyNames.length);
/* 211:432 */     return newKeyNames;
/* 212:    */   }
/* 213:    */   
/* 214:    */   public Object[] getKeyTypes()
/* 215:    */   {
/* 216:445 */     Object[] keyTypes = this.m_SplitEvaluator.getKeyTypes();
/* 217:    */     
/* 218:447 */     Object[] newKeyTypes = new String[keyTypes.length + 3];
/* 219:448 */     newKeyTypes[0] = new String();
/* 220:449 */     newKeyTypes[1] = new String();
/* 221:450 */     newKeyTypes[2] = new String();
/* 222:451 */     System.arraycopy(keyTypes, 0, newKeyTypes, 3, keyTypes.length);
/* 223:452 */     return newKeyTypes;
/* 224:    */   }
/* 225:    */   
/* 226:    */   public String[] getResultNames()
/* 227:    */   {
/* 228:464 */     String[] resultNames = this.m_SplitEvaluator.getResultNames();
/* 229:    */     
/* 230:466 */     String[] newResultNames = new String[resultNames.length + 1];
/* 231:467 */     newResultNames[0] = TIMESTAMP_FIELD_NAME;
/* 232:468 */     System.arraycopy(resultNames, 0, newResultNames, 1, resultNames.length);
/* 233:469 */     return newResultNames;
/* 234:    */   }
/* 235:    */   
/* 236:    */   public Object[] getResultTypes()
/* 237:    */   {
/* 238:482 */     Object[] resultTypes = this.m_SplitEvaluator.getResultTypes();
/* 239:    */     
/* 240:484 */     Object[] newResultTypes = new Object[resultTypes.length + 1];
/* 241:485 */     newResultTypes[0] = new Double(0.0D);
/* 242:486 */     System.arraycopy(resultTypes, 0, newResultTypes, 1, resultTypes.length);
/* 243:487 */     return newResultTypes;
/* 244:    */   }
/* 245:    */   
/* 246:    */   public String getCompatibilityState()
/* 247:    */   {
/* 248:506 */     String result = "-X " + this.m_NumFolds + " ";
/* 249:507 */     if (this.m_SplitEvaluator == null) {
/* 250:508 */       result = result + "<null SplitEvaluator>";
/* 251:    */     } else {
/* 252:510 */       result = result + "-W " + this.m_SplitEvaluator.getClass().getName();
/* 253:    */     }
/* 254:512 */     return result + " --";
/* 255:    */   }
/* 256:    */   
/* 257:    */   public String outputFileTipText()
/* 258:    */   {
/* 259:522 */     return "Set the destination for saving raw output. If the rawOutput option is selected, then output from the splitEvaluator for individual folds is saved. If the destination is a directory, then each output is saved to an individual gzip file; if the destination is a file, then each output is saved as an entry in a zip file.";
/* 260:    */   }
/* 261:    */   
/* 262:    */   public File getOutputFile()
/* 263:    */   {
/* 264:537 */     return this.m_OutputFile;
/* 265:    */   }
/* 266:    */   
/* 267:    */   public void setOutputFile(File newOutputFile)
/* 268:    */   {
/* 269:547 */     this.m_OutputFile = newOutputFile;
/* 270:    */   }
/* 271:    */   
/* 272:    */   public String numFoldsTipText()
/* 273:    */   {
/* 274:557 */     return "Number of folds to use in cross validation.";
/* 275:    */   }
/* 276:    */   
/* 277:    */   public int getNumFolds()
/* 278:    */   {
/* 279:567 */     return this.m_NumFolds;
/* 280:    */   }
/* 281:    */   
/* 282:    */   public void setNumFolds(int newNumFolds)
/* 283:    */   {
/* 284:577 */     this.m_NumFolds = newNumFolds;
/* 285:    */   }
/* 286:    */   
/* 287:    */   public String rawOutputTipText()
/* 288:    */   {
/* 289:587 */     return "Save raw output (useful for debugging). If set, then output is sent to the destination specified by outputFile";
/* 290:    */   }
/* 291:    */   
/* 292:    */   public boolean getRawOutput()
/* 293:    */   {
/* 294:597 */     return this.m_debugOutput;
/* 295:    */   }
/* 296:    */   
/* 297:    */   public void setRawOutput(boolean d)
/* 298:    */   {
/* 299:606 */     this.m_debugOutput = d;
/* 300:    */   }
/* 301:    */   
/* 302:    */   public String splitEvaluatorTipText()
/* 303:    */   {
/* 304:616 */     return "The evaluator to apply to the cross validation folds. This may be a classifier, regression scheme etc.";
/* 305:    */   }
/* 306:    */   
/* 307:    */   public SplitEvaluator getSplitEvaluator()
/* 308:    */   {
/* 309:627 */     return this.m_SplitEvaluator;
/* 310:    */   }
/* 311:    */   
/* 312:    */   public void setSplitEvaluator(SplitEvaluator newSplitEvaluator)
/* 313:    */   {
/* 314:637 */     this.m_SplitEvaluator = newSplitEvaluator;
/* 315:638 */     this.m_SplitEvaluator.setAdditionalMeasures(this.m_AdditionalMeasures);
/* 316:    */   }
/* 317:    */   
/* 318:    */   public Enumeration<Option> listOptions()
/* 319:    */   {
/* 320:649 */     Vector<Option> newVector = new Vector(4);
/* 321:    */     
/* 322:651 */     newVector.addElement(new Option("\tThe number of folds to use for the cross-validation.\n\t(default 10)", "X", 1, "-X <number of folds>"));
/* 323:    */     
/* 324:    */ 
/* 325:    */ 
/* 326:655 */     newVector.addElement(new Option("Save raw split evaluator output.", "D", 0, "-D"));
/* 327:    */     
/* 328:    */ 
/* 329:658 */     newVector.addElement(new Option("\tThe filename where raw output will be stored.\n\tIf a directory name is specified then then individual\n\toutputs will be gzipped, otherwise all output will be\n\tzipped to the named file. Use in conjuction with -D.\t(default splitEvalutorOut.zip)", "O", 1, "-O <file/directory name/path>"));
/* 330:    */     
/* 331:    */ 
/* 332:    */ 
/* 333:    */ 
/* 334:    */ 
/* 335:    */ 
/* 336:    */ 
/* 337:666 */     newVector.addElement(new Option("\tThe full class name of a SplitEvaluator.\n\teg: weka.experiment.ClassifierSplitEvaluator", "W", 1, "-W <class name>"));
/* 338:671 */     if ((this.m_SplitEvaluator != null) && ((this.m_SplitEvaluator instanceof OptionHandler)))
/* 339:    */     {
/* 340:673 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to split evaluator " + this.m_SplitEvaluator.getClass().getName() + ":"));
/* 341:    */       
/* 342:    */ 
/* 343:676 */       newVector.addAll(Collections.list(((OptionHandler)this.m_SplitEvaluator).listOptions()));
/* 344:    */     }
/* 345:679 */     return newVector.elements();
/* 346:    */   }
/* 347:    */   
/* 348:    */   public void setOptions(String[] options)
/* 349:    */     throws Exception
/* 350:    */   {
/* 351:765 */     setRawOutput(Utils.getFlag('D', options));
/* 352:    */     
/* 353:767 */     String fName = Utils.getOption('O', options);
/* 354:768 */     if (fName.length() != 0) {
/* 355:769 */       setOutputFile(new File(fName));
/* 356:    */     }
/* 357:772 */     String numFolds = Utils.getOption('X', options);
/* 358:773 */     if (numFolds.length() != 0) {
/* 359:774 */       setNumFolds(Integer.parseInt(numFolds));
/* 360:    */     } else {
/* 361:776 */       setNumFolds(10);
/* 362:    */     }
/* 363:779 */     String seName = Utils.getOption('W', options);
/* 364:780 */     if (seName.length() == 0) {
/* 365:781 */       throw new Exception("A SplitEvaluator must be specified with the -W option.");
/* 366:    */     }
/* 367:787 */     setSplitEvaluator((SplitEvaluator)Utils.forName(SplitEvaluator.class, seName, null));
/* 368:789 */     if ((getSplitEvaluator() instanceof OptionHandler)) {
/* 369:790 */       ((OptionHandler)getSplitEvaluator()).setOptions(Utils.partitionOptions(options));
/* 370:    */     }
/* 371:    */   }
/* 372:    */   
/* 373:    */   public String[] getOptions()
/* 374:    */   {
/* 375:803 */     Vector<String> options = new Vector();
/* 376:    */     
/* 377:805 */     options.add("-X");
/* 378:806 */     options.add("" + getNumFolds());
/* 379:808 */     if (getRawOutput()) {
/* 380:809 */       options.add("-D");
/* 381:    */     }
/* 382:812 */     options.add("-O");
/* 383:813 */     options.add(getOutputFile().getName());
/* 384:815 */     if (getSplitEvaluator() != null)
/* 385:    */     {
/* 386:816 */       options.add("-W");
/* 387:817 */       options.add(getSplitEvaluator().getClass().getName());
/* 388:    */     }
/* 389:819 */     if ((this.m_SplitEvaluator != null) && ((this.m_SplitEvaluator instanceof OptionHandler)))
/* 390:    */     {
/* 391:821 */       String[] opts = ((OptionHandler)this.m_SplitEvaluator).getOptions();
/* 392:822 */       if (opts.length > 0)
/* 393:    */       {
/* 394:823 */         options.add("--");
/* 395:824 */         Collections.addAll(options, opts);
/* 396:    */       }
/* 397:    */     }
/* 398:828 */     return (String[])options.toArray(new String[0]);
/* 399:    */   }
/* 400:    */   
/* 401:    */   public String toString()
/* 402:    */   {
/* 403:839 */     String result = "CrossValidationResultProducer: ";
/* 404:840 */     result = result + getCompatibilityState();
/* 405:841 */     if (this.m_Instances == null) {
/* 406:842 */       result = result + ": <null Instances>";
/* 407:    */     } else {
/* 408:844 */       result = result + ": " + Utils.backQuoteChars(this.m_Instances.relationName());
/* 409:    */     }
/* 410:846 */     return result;
/* 411:    */   }
/* 412:    */   
/* 413:    */   public String getRevision()
/* 414:    */   {
/* 415:856 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 416:    */   }
/* 417:    */   
/* 418:    */   public static void main(String[] args)
/* 419:    */   {
/* 420:866 */     System.err.println(Utils.doubleToString(getTimestamp().doubleValue(), 4));
/* 421:    */   }
/* 422:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.CrossValidationResultProducer
 * JD-Core Version:    0.7.0.1
 */