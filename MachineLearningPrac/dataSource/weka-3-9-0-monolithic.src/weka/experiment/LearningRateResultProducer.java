/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Enumeration;
/*   6:    */ import java.util.Random;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.core.AdditionalMeasureProducer;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.OptionHandler;
/*  12:    */ import weka.core.RevisionHandler;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.Utils;
/*  15:    */ 
/*  16:    */ public class LearningRateResultProducer
/*  17:    */   implements ResultListener, ResultProducer, OptionHandler, AdditionalMeasureProducer, RevisionHandler
/*  18:    */ {
/*  19:    */   static final long serialVersionUID = -3841159673490861331L;
/*  20:    */   protected Instances m_Instances;
/*  21:176 */   protected ResultListener m_ResultListener = new CSVResultListener();
/*  22:179 */   protected ResultProducer m_ResultProducer = new AveragingResultProducer();
/*  23:182 */   protected String[] m_AdditionalMeasures = null;
/*  24:188 */   protected int m_LowerSize = 0;
/*  25:194 */   protected int m_UpperSize = -1;
/*  26:197 */   protected int m_StepSize = 10;
/*  27:200 */   protected int m_CurrentSize = 0;
/*  28:203 */   public static String STEP_FIELD_NAME = "Total_instances";
/*  29:    */   
/*  30:    */   public String globalInfo()
/*  31:    */   {
/*  32:212 */     return "Tells a sub-ResultProducer to reproduce the current run for varying sized subsamples of the dataset. Normally used with an AveragingResultProducer and CrossValidationResultProducer combo to generate learning curve results. For non-numeric result fields, the first value is used.";
/*  33:    */   }
/*  34:    */   
/*  35:    */   public String[] determineColumnConstraints(ResultProducer rp)
/*  36:    */     throws Exception
/*  37:    */   {
/*  38:233 */     return null;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void doRunKeys(int run)
/*  42:    */     throws Exception
/*  43:    */   {
/*  44:247 */     if (this.m_ResultProducer == null) {
/*  45:248 */       throw new Exception("No ResultProducer set");
/*  46:    */     }
/*  47:250 */     if (this.m_ResultListener == null) {
/*  48:251 */       throw new Exception("No ResultListener set");
/*  49:    */     }
/*  50:253 */     if (this.m_Instances == null) {
/*  51:254 */       throw new Exception("No Instances set");
/*  52:    */     }
/*  53:258 */     this.m_ResultProducer.setResultListener(this);
/*  54:259 */     this.m_ResultProducer.setInstances(this.m_Instances);
/*  55:262 */     if (this.m_LowerSize == 0) {
/*  56:263 */       this.m_CurrentSize = this.m_StepSize;
/*  57:    */     } else {
/*  58:265 */       this.m_CurrentSize = this.m_LowerSize;
/*  59:    */     }
/*  60:268 */     while ((this.m_CurrentSize <= this.m_Instances.numInstances()) && ((this.m_UpperSize == -1) || (this.m_CurrentSize <= this.m_UpperSize)))
/*  61:    */     {
/*  62:269 */       this.m_ResultProducer.doRunKeys(run);
/*  63:270 */       this.m_CurrentSize += this.m_StepSize;
/*  64:    */     }
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void doRun(int run)
/*  68:    */     throws Exception
/*  69:    */   {
/*  70:285 */     if (this.m_ResultProducer == null) {
/*  71:286 */       throw new Exception("No ResultProducer set");
/*  72:    */     }
/*  73:288 */     if (this.m_ResultListener == null) {
/*  74:289 */       throw new Exception("No ResultListener set");
/*  75:    */     }
/*  76:291 */     if (this.m_Instances == null) {
/*  77:292 */       throw new Exception("No Instances set");
/*  78:    */     }
/*  79:296 */     Instances runInstances = new Instances(this.m_Instances);
/*  80:297 */     runInstances.randomize(new Random(run));
/*  81:    */     
/*  82:    */ 
/*  83:    */ 
/*  84:    */ 
/*  85:    */ 
/*  86:    */ 
/*  87:    */ 
/*  88:    */ 
/*  89:306 */     this.m_ResultProducer.setResultListener(this);
/*  90:309 */     if (this.m_LowerSize == 0) {
/*  91:310 */       this.m_CurrentSize = this.m_StepSize;
/*  92:    */     } else {
/*  93:312 */       this.m_CurrentSize = this.m_LowerSize;
/*  94:    */     }
/*  95:315 */     while ((this.m_CurrentSize <= this.m_Instances.numInstances()) && ((this.m_UpperSize == -1) || (this.m_CurrentSize <= this.m_UpperSize)))
/*  96:    */     {
/*  97:316 */       this.m_ResultProducer.setInstances(new Instances(runInstances, 0, this.m_CurrentSize));
/*  98:    */       
/*  99:318 */       this.m_ResultProducer.doRun(run);
/* 100:319 */       this.m_CurrentSize += this.m_StepSize;
/* 101:    */     }
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void preProcess(ResultProducer rp)
/* 105:    */     throws Exception
/* 106:    */   {
/* 107:332 */     if (this.m_ResultListener == null) {
/* 108:333 */       throw new Exception("No ResultListener set");
/* 109:    */     }
/* 110:335 */     this.m_ResultListener.preProcess(this);
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void preProcess()
/* 114:    */     throws Exception
/* 115:    */   {
/* 116:347 */     if (this.m_ResultProducer == null) {
/* 117:348 */       throw new Exception("No ResultProducer set");
/* 118:    */     }
/* 119:351 */     this.m_ResultProducer.setResultListener(this);
/* 120:352 */     this.m_ResultProducer.preProcess();
/* 121:    */   }
/* 122:    */   
/* 123:    */   public void postProcess(ResultProducer rp)
/* 124:    */     throws Exception
/* 125:    */   {
/* 126:365 */     this.m_ResultListener.postProcess(this);
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void postProcess()
/* 130:    */     throws Exception
/* 131:    */   {
/* 132:378 */     this.m_ResultProducer.postProcess();
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void acceptResult(ResultProducer rp, Object[] key, Object[] result)
/* 136:    */     throws Exception
/* 137:    */   {
/* 138:395 */     if (this.m_ResultProducer != rp) {
/* 139:396 */       throw new Error("Unrecognized ResultProducer sending results!!");
/* 140:    */     }
/* 141:399 */     Object[] newKey = new Object[key.length + 1];
/* 142:400 */     System.arraycopy(key, 0, newKey, 0, key.length);
/* 143:401 */     newKey[key.length] = new String("" + this.m_CurrentSize);
/* 144:    */     
/* 145:403 */     this.m_ResultListener.acceptResult(this, newKey, result);
/* 146:    */   }
/* 147:    */   
/* 148:    */   public boolean isResultRequired(ResultProducer rp, Object[] key)
/* 149:    */     throws Exception
/* 150:    */   {
/* 151:419 */     if (this.m_ResultProducer != rp) {
/* 152:420 */       throw new Error("Unrecognized ResultProducer sending results!!");
/* 153:    */     }
/* 154:423 */     Object[] newKey = new Object[key.length + 1];
/* 155:424 */     System.arraycopy(key, 0, newKey, 0, key.length);
/* 156:425 */     newKey[key.length] = new String("" + this.m_CurrentSize);
/* 157:    */     
/* 158:427 */     return this.m_ResultListener.isResultRequired(this, newKey);
/* 159:    */   }
/* 160:    */   
/* 161:    */   public String[] getKeyNames()
/* 162:    */     throws Exception
/* 163:    */   {
/* 164:439 */     String[] keyNames = this.m_ResultProducer.getKeyNames();
/* 165:440 */     String[] newKeyNames = new String[keyNames.length + 1];
/* 166:441 */     System.arraycopy(keyNames, 0, newKeyNames, 0, keyNames.length);
/* 167:    */     
/* 168:443 */     newKeyNames[keyNames.length] = STEP_FIELD_NAME;
/* 169:444 */     return newKeyNames;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public Object[] getKeyTypes()
/* 173:    */     throws Exception
/* 174:    */   {
/* 175:459 */     Object[] keyTypes = this.m_ResultProducer.getKeyTypes();
/* 176:460 */     Object[] newKeyTypes = new Object[keyTypes.length + 1];
/* 177:461 */     System.arraycopy(keyTypes, 0, newKeyTypes, 0, keyTypes.length);
/* 178:462 */     newKeyTypes[keyTypes.length] = "";
/* 179:463 */     return newKeyTypes;
/* 180:    */   }
/* 181:    */   
/* 182:    */   public String[] getResultNames()
/* 183:    */     throws Exception
/* 184:    */   {
/* 185:480 */     return this.m_ResultProducer.getResultNames();
/* 186:    */   }
/* 187:    */   
/* 188:    */   public Object[] getResultTypes()
/* 189:    */     throws Exception
/* 190:    */   {
/* 191:494 */     return this.m_ResultProducer.getResultTypes();
/* 192:    */   }
/* 193:    */   
/* 194:    */   public String getCompatibilityState()
/* 195:    */   {
/* 196:513 */     String result = " ";
/* 197:516 */     if (this.m_ResultProducer == null)
/* 198:    */     {
/* 199:517 */       result = result + "<null ResultProducer>";
/* 200:    */     }
/* 201:    */     else
/* 202:    */     {
/* 203:519 */       result = result + "-W " + this.m_ResultProducer.getClass().getName();
/* 204:520 */       result = result + " -- " + this.m_ResultProducer.getCompatibilityState();
/* 205:    */     }
/* 206:523 */     return result.trim();
/* 207:    */   }
/* 208:    */   
/* 209:    */   public Enumeration<Option> listOptions()
/* 210:    */   {
/* 211:534 */     Vector<Option> newVector = new Vector(2);
/* 212:    */     
/* 213:536 */     newVector.addElement(new Option("\tThe number of steps in the learning rate curve.\n\t(default 10)", "X", 1, "-X <num steps>"));
/* 214:    */     
/* 215:    */ 
/* 216:539 */     newVector.addElement(new Option("\tThe full class name of a ResultProducer.\n\teg: weka.experiment.CrossValidationResultProducer", "W", 1, "-W <class name>"));
/* 217:544 */     if ((this.m_ResultProducer != null) && ((this.m_ResultProducer instanceof OptionHandler)))
/* 218:    */     {
/* 219:546 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to result producer " + this.m_ResultProducer.getClass().getName() + ":"));
/* 220:    */       
/* 221:    */ 
/* 222:549 */       newVector.addAll(Collections.list(((OptionHandler)this.m_ResultProducer).listOptions()));
/* 223:    */     }
/* 224:552 */     return newVector.elements();
/* 225:    */   }
/* 226:    */   
/* 227:    */   public void setOptions(String[] options)
/* 228:    */     throws Exception
/* 229:    */   {
/* 230:682 */     String stepSize = Utils.getOption('S', options);
/* 231:683 */     if (stepSize.length() != 0) {
/* 232:684 */       setStepSize(Integer.parseInt(stepSize));
/* 233:    */     } else {
/* 234:686 */       setStepSize(10);
/* 235:    */     }
/* 236:689 */     String lowerSize = Utils.getOption('L', options);
/* 237:690 */     if (lowerSize.length() != 0) {
/* 238:691 */       setLowerSize(Integer.parseInt(lowerSize));
/* 239:    */     } else {
/* 240:693 */       setLowerSize(0);
/* 241:    */     }
/* 242:696 */     String upperSize = Utils.getOption('U', options);
/* 243:697 */     if (upperSize.length() != 0) {
/* 244:698 */       setUpperSize(Integer.parseInt(upperSize));
/* 245:    */     } else {
/* 246:700 */       setUpperSize(-1);
/* 247:    */     }
/* 248:703 */     String rpName = Utils.getOption('W', options);
/* 249:704 */     if (rpName.length() == 0) {
/* 250:705 */       throw new Exception("A ResultProducer must be specified with the -W option.");
/* 251:    */     }
/* 252:711 */     setResultProducer((ResultProducer)Utils.forName(ResultProducer.class, rpName, null));
/* 253:713 */     if ((getResultProducer() instanceof OptionHandler)) {
/* 254:714 */       ((OptionHandler)getResultProducer()).setOptions(Utils.partitionOptions(options));
/* 255:    */     }
/* 256:    */   }
/* 257:    */   
/* 258:    */   public String[] getOptions()
/* 259:    */   {
/* 260:727 */     String[] seOptions = new String[0];
/* 261:728 */     if ((this.m_ResultProducer != null) && ((this.m_ResultProducer instanceof OptionHandler))) {
/* 262:730 */       seOptions = ((OptionHandler)this.m_ResultProducer).getOptions();
/* 263:    */     }
/* 264:733 */     String[] options = new String[seOptions.length + 9];
/* 265:734 */     int current = 0;
/* 266:    */     
/* 267:736 */     options[(current++)] = "-S";
/* 268:737 */     options[(current++)] = ("" + getStepSize());
/* 269:738 */     options[(current++)] = "-L";
/* 270:739 */     options[(current++)] = ("" + getLowerSize());
/* 271:740 */     options[(current++)] = "-U";
/* 272:741 */     options[(current++)] = ("" + getUpperSize());
/* 273:742 */     if (getResultProducer() != null)
/* 274:    */     {
/* 275:743 */       options[(current++)] = "-W";
/* 276:744 */       options[(current++)] = getResultProducer().getClass().getName();
/* 277:    */     }
/* 278:746 */     options[(current++)] = "--";
/* 279:    */     
/* 280:748 */     System.arraycopy(seOptions, 0, options, current, seOptions.length);
/* 281:749 */     current += seOptions.length;
/* 282:750 */     while (current < options.length) {
/* 283:751 */       options[(current++)] = "";
/* 284:    */     }
/* 285:753 */     return options;
/* 286:    */   }
/* 287:    */   
/* 288:    */   public void setAdditionalMeasures(String[] additionalMeasures)
/* 289:    */   {
/* 290:766 */     this.m_AdditionalMeasures = additionalMeasures;
/* 291:768 */     if (this.m_ResultProducer != null)
/* 292:    */     {
/* 293:769 */       System.err.println("LearningRateResultProducer: setting additional measures for ResultProducer");
/* 294:    */       
/* 295:771 */       this.m_ResultProducer.setAdditionalMeasures(this.m_AdditionalMeasures);
/* 296:    */     }
/* 297:    */   }
/* 298:    */   
/* 299:    */   public Enumeration<String> enumerateMeasures()
/* 300:    */   {
/* 301:783 */     Vector<String> newVector = new Vector();
/* 302:784 */     if ((this.m_ResultProducer instanceof AdditionalMeasureProducer))
/* 303:    */     {
/* 304:785 */       Enumeration<String> en = ((AdditionalMeasureProducer)this.m_ResultProducer).enumerateMeasures();
/* 305:787 */       while (en.hasMoreElements())
/* 306:    */       {
/* 307:788 */         String mname = (String)en.nextElement();
/* 308:789 */         newVector.add(mname);
/* 309:    */       }
/* 310:    */     }
/* 311:792 */     return newVector.elements();
/* 312:    */   }
/* 313:    */   
/* 314:    */   public double getMeasure(String additionalMeasureName)
/* 315:    */   {
/* 316:804 */     if ((this.m_ResultProducer instanceof AdditionalMeasureProducer)) {
/* 317:805 */       return ((AdditionalMeasureProducer)this.m_ResultProducer).getMeasure(additionalMeasureName);
/* 318:    */     }
/* 319:808 */     throw new IllegalArgumentException("LearningRateResultProducer: Can't return value for : " + additionalMeasureName + ". " + this.m_ResultProducer.getClass().getName() + " " + "is not an AdditionalMeasureProducer");
/* 320:    */   }
/* 321:    */   
/* 322:    */   public void setInstances(Instances instances)
/* 323:    */   {
/* 324:823 */     this.m_Instances = instances;
/* 325:    */   }
/* 326:    */   
/* 327:    */   public String lowerSizeTipText()
/* 328:    */   {
/* 329:833 */     return "Set the minmum number of instances in a dataset. Setting zero here will actually use <stepSize> number of instances at the first step (since it makes no sense to use zero instances :-))";
/* 330:    */   }
/* 331:    */   
/* 332:    */   public int getLowerSize()
/* 333:    */   {
/* 334:845 */     return this.m_LowerSize;
/* 335:    */   }
/* 336:    */   
/* 337:    */   public void setLowerSize(int newLowerSize)
/* 338:    */   {
/* 339:855 */     this.m_LowerSize = newLowerSize;
/* 340:    */   }
/* 341:    */   
/* 342:    */   public String upperSizeTipText()
/* 343:    */   {
/* 344:865 */     return "Set the maximum number of instances in a dataset. Setting -1 sets no upper limit (other than the total number of instances in the full dataset)";
/* 345:    */   }
/* 346:    */   
/* 347:    */   public int getUpperSize()
/* 348:    */   {
/* 349:877 */     return this.m_UpperSize;
/* 350:    */   }
/* 351:    */   
/* 352:    */   public void setUpperSize(int newUpperSize)
/* 353:    */   {
/* 354:887 */     this.m_UpperSize = newUpperSize;
/* 355:    */   }
/* 356:    */   
/* 357:    */   public String stepSizeTipText()
/* 358:    */   {
/* 359:897 */     return "Set the number of instances to add at each step.";
/* 360:    */   }
/* 361:    */   
/* 362:    */   public int getStepSize()
/* 363:    */   {
/* 364:907 */     return this.m_StepSize;
/* 365:    */   }
/* 366:    */   
/* 367:    */   public void setStepSize(int newStepSize)
/* 368:    */   {
/* 369:917 */     this.m_StepSize = newStepSize;
/* 370:    */   }
/* 371:    */   
/* 372:    */   public void setResultListener(ResultListener listener)
/* 373:    */   {
/* 374:928 */     this.m_ResultListener = listener;
/* 375:    */   }
/* 376:    */   
/* 377:    */   public String resultProducerTipText()
/* 378:    */   {
/* 379:938 */     return "Set the resultProducer for which learning rate results should be generated.";
/* 380:    */   }
/* 381:    */   
/* 382:    */   public ResultProducer getResultProducer()
/* 383:    */   {
/* 384:949 */     return this.m_ResultProducer;
/* 385:    */   }
/* 386:    */   
/* 387:    */   public void setResultProducer(ResultProducer newResultProducer)
/* 388:    */   {
/* 389:959 */     this.m_ResultProducer = newResultProducer;
/* 390:960 */     this.m_ResultProducer.setResultListener(this);
/* 391:    */   }
/* 392:    */   
/* 393:    */   public String toString()
/* 394:    */   {
/* 395:971 */     String result = "LearningRateResultProducer: ";
/* 396:972 */     result = result + getCompatibilityState();
/* 397:973 */     if (this.m_Instances == null) {
/* 398:974 */       result = result + ": <null Instances>";
/* 399:    */     } else {
/* 400:976 */       result = result + ": " + Utils.backQuoteChars(this.m_Instances.relationName());
/* 401:    */     }
/* 402:978 */     return result;
/* 403:    */   }
/* 404:    */   
/* 405:    */   public String getRevision()
/* 406:    */   {
/* 407:988 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 408:    */   }
/* 409:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.LearningRateResultProducer
 * JD-Core Version:    0.7.0.1
 */