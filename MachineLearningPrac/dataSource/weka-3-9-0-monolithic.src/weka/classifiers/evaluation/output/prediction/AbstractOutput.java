/*   1:    */ package weka.classifiers.evaluation.output.prediction;
/*   2:    */ 
/*   3:    */ import java.io.BufferedWriter;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileWriter;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import java.io.Serializable;
/*   8:    */ import java.util.Enumeration;
/*   9:    */ import java.util.Vector;
/*  10:    */ import weka.classifiers.Classifier;
/*  11:    */ import weka.classifiers.misc.InputMappedClassifier;
/*  12:    */ import weka.core.BatchPredictor;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.Option;
/*  16:    */ import weka.core.OptionHandler;
/*  17:    */ import weka.core.Range;
/*  18:    */ import weka.core.Utils;
/*  19:    */ import weka.core.WekaException;
/*  20:    */ import weka.core.converters.ConverterUtils.DataSource;
/*  21:    */ 
/*  22:    */ public abstract class AbstractOutput
/*  23:    */   implements Serializable, OptionHandler
/*  24:    */ {
/*  25:    */   private static final long serialVersionUID = 752696986017306241L;
/*  26:    */   protected Instances m_Header;
/*  27:    */   protected StringBuffer m_Buffer;
/*  28:    */   protected StringBuffer m_FileBuffer;
/*  29:    */   protected boolean m_OutputDistribution;
/*  30:    */   protected Range m_Attributes;
/*  31:    */   protected int m_NumDecimals;
/*  32:    */   protected File m_OutputFile;
/*  33:    */   protected boolean m_SuppressOutput;
/*  34:    */   
/*  35:    */   public AbstractOutput()
/*  36:    */   {
/*  37:124 */     this.m_Header = null;
/*  38:125 */     this.m_OutputDistribution = false;
/*  39:126 */     this.m_Attributes = null;
/*  40:127 */     this.m_Buffer = null;
/*  41:128 */     this.m_NumDecimals = 3;
/*  42:129 */     this.m_OutputFile = new File(".");
/*  43:130 */     this.m_FileBuffer = new StringBuffer();
/*  44:131 */     this.m_SuppressOutput = false;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public abstract String globalInfo();
/*  48:    */   
/*  49:    */   public abstract String getDisplay();
/*  50:    */   
/*  51:    */   public Enumeration<Option> listOptions()
/*  52:    */   {
/*  53:157 */     Vector<Option> result = new Vector();
/*  54:    */     
/*  55:159 */     result.addElement(new Option("\tThe range of attributes to print in addition to the classification.\n\t(default: none)", "p", 1, "-p <range>"));
/*  56:    */     
/*  57:    */ 
/*  58:    */ 
/*  59:163 */     result.addElement(new Option("\tWhether to turn on the output of the class distribution.\n\tOnly for nominal class attributes.\n\t(default: off)", "distribution", 0, "-distribution"));
/*  60:    */     
/*  61:    */ 
/*  62:    */ 
/*  63:    */ 
/*  64:168 */     result.addElement(new Option("\tThe number of digits after the decimal point.\n\t(default: " + getDefaultNumDecimals() + ")", "decimals", 1, "-decimals <num>"));
/*  65:    */     
/*  66:    */ 
/*  67:    */ 
/*  68:172 */     result.addElement(new Option("\tThe file to store the output in, instead of outputting it on stdout.\n\tGets ignored if the supplied path is a directory.\n\t(default: .)", "file", 1, "-file <path>"));
/*  69:    */     
/*  70:    */ 
/*  71:    */ 
/*  72:    */ 
/*  73:177 */     result.addElement(new Option("\tIn case the data gets stored in a file, then this flag can be used\n\tto suppress the regular output.\n\t(default: not suppressed)", "suppress", 0, "-suppress"));
/*  74:    */     
/*  75:    */ 
/*  76:    */ 
/*  77:    */ 
/*  78:    */ 
/*  79:183 */     return result.elements();
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void setOptions(String[] options)
/*  83:    */     throws Exception
/*  84:    */   {
/*  85:198 */     setAttributes(Utils.getOption("p", options));
/*  86:199 */     setOutputDistribution(Utils.getFlag("distribution", options));
/*  87:    */     
/*  88:201 */     String tmpStr = Utils.getOption("decimals", options);
/*  89:202 */     if (tmpStr.length() > 0) {
/*  90:203 */       setNumDecimals(Integer.parseInt(tmpStr));
/*  91:    */     } else {
/*  92:205 */       setNumDecimals(getDefaultNumDecimals());
/*  93:    */     }
/*  94:208 */     tmpStr = Utils.getOption("file", options);
/*  95:209 */     if (tmpStr.length() > 0) {
/*  96:210 */       setOutputFile(new File(tmpStr));
/*  97:    */     } else {
/*  98:212 */       setOutputFile(new File("."));
/*  99:    */     }
/* 100:215 */     setSuppressOutput(Utils.getFlag("suppress", options));
/* 101:    */   }
/* 102:    */   
/* 103:    */   public String[] getOptions()
/* 104:    */   {
/* 105:227 */     Vector<String> result = new Vector();
/* 106:229 */     if (getAttributes().length() > 0)
/* 107:    */     {
/* 108:230 */       result.add("-p");
/* 109:231 */       result.add(getAttributes());
/* 110:    */     }
/* 111:234 */     if (getOutputDistribution()) {
/* 112:235 */       result.add("-distribution");
/* 113:    */     }
/* 114:238 */     if (getNumDecimals() != getDefaultNumDecimals())
/* 115:    */     {
/* 116:239 */       result.add("-decimals");
/* 117:240 */       result.add("" + getNumDecimals());
/* 118:    */     }
/* 119:243 */     if (!getOutputFile().isDirectory())
/* 120:    */     {
/* 121:244 */       result.add("-file");
/* 122:245 */       result.add(getOutputFile().getAbsolutePath());
/* 123:246 */       if (getSuppressOutput()) {
/* 124:247 */         result.add("-suppress");
/* 125:    */       }
/* 126:    */     }
/* 127:251 */     return (String[])result.toArray(new String[result.size()]);
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void setHeader(Instances value)
/* 131:    */   {
/* 132:260 */     if (value != null) {
/* 133:261 */       this.m_Header = new Instances(value, 0);
/* 134:    */     }
/* 135:    */   }
/* 136:    */   
/* 137:    */   public Instances getHeader()
/* 138:    */   {
/* 139:271 */     return this.m_Header;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public void setBuffer(StringBuffer value)
/* 143:    */   {
/* 144:280 */     this.m_Buffer = value;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public StringBuffer getBuffer()
/* 148:    */   {
/* 149:289 */     return this.m_Buffer;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public void setAttributes(String value)
/* 153:    */   {
/* 154:298 */     if (value.length() == 0) {
/* 155:299 */       this.m_Attributes = null;
/* 156:    */     } else {
/* 157:301 */       this.m_Attributes = new Range(value);
/* 158:    */     }
/* 159:    */   }
/* 160:    */   
/* 161:    */   public String getAttributes()
/* 162:    */   {
/* 163:311 */     if (this.m_Attributes == null) {
/* 164:312 */       return "";
/* 165:    */     }
/* 166:314 */     return this.m_Attributes.getRanges();
/* 167:    */   }
/* 168:    */   
/* 169:    */   public String attributesTipText()
/* 170:    */   {
/* 171:324 */     return "The indices of the attributes to print in addition.";
/* 172:    */   }
/* 173:    */   
/* 174:    */   public void setOutputDistribution(boolean value)
/* 175:    */   {
/* 176:333 */     this.m_OutputDistribution = value;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public boolean getOutputDistribution()
/* 180:    */   {
/* 181:342 */     return this.m_OutputDistribution;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public String outputDistributionTipText()
/* 185:    */   {
/* 186:351 */     return "Whether to ouput the class distribution as well (only nominal class attributes).";
/* 187:    */   }
/* 188:    */   
/* 189:    */   public int getDefaultNumDecimals()
/* 190:    */   {
/* 191:360 */     return 3;
/* 192:    */   }
/* 193:    */   
/* 194:    */   public void setNumDecimals(int value)
/* 195:    */   {
/* 196:369 */     if (value >= 0) {
/* 197:370 */       this.m_NumDecimals = value;
/* 198:    */     } else {
/* 199:372 */       System.err.println("Number of decimals cannot be negative (provided: " + value + ")!");
/* 200:    */     }
/* 201:    */   }
/* 202:    */   
/* 203:    */   public int getNumDecimals()
/* 204:    */   {
/* 205:383 */     return this.m_NumDecimals;
/* 206:    */   }
/* 207:    */   
/* 208:    */   public String numDecimalsTipText()
/* 209:    */   {
/* 210:392 */     return "The number of digits to output after the decimal point.";
/* 211:    */   }
/* 212:    */   
/* 213:    */   public void setOutputFile(File value)
/* 214:    */   {
/* 215:401 */     this.m_OutputFile = value;
/* 216:    */   }
/* 217:    */   
/* 218:    */   public File getOutputFile()
/* 219:    */   {
/* 220:410 */     return this.m_OutputFile;
/* 221:    */   }
/* 222:    */   
/* 223:    */   public String outputFileTipText()
/* 224:    */   {
/* 225:419 */     return "The file to write the generated output to (disabled if path is a directory).";
/* 226:    */   }
/* 227:    */   
/* 228:    */   public void setSuppressOutput(boolean value)
/* 229:    */   {
/* 230:429 */     this.m_SuppressOutput = value;
/* 231:    */   }
/* 232:    */   
/* 233:    */   public boolean getSuppressOutput()
/* 234:    */   {
/* 235:439 */     return this.m_SuppressOutput;
/* 236:    */   }
/* 237:    */   
/* 238:    */   public String suppressOutputTipText()
/* 239:    */   {
/* 240:448 */     return "Whether to suppress the regular output when storing the output in a file.";
/* 241:    */   }
/* 242:    */   
/* 243:    */   protected String checkBasic()
/* 244:    */   {
/* 245:457 */     if (this.m_Buffer == null) {
/* 246:458 */       return "Buffer is null!";
/* 247:    */     }
/* 248:461 */     if (this.m_Header == null) {
/* 249:462 */       return "No dataset structure provided!";
/* 250:    */     }
/* 251:465 */     if (this.m_Attributes != null) {
/* 252:466 */       this.m_Attributes.setUpper(this.m_Header.numAttributes() - 1);
/* 253:    */     }
/* 254:469 */     return null;
/* 255:    */   }
/* 256:    */   
/* 257:    */   public boolean generatesOutput()
/* 258:    */   {
/* 259:478 */     return (this.m_OutputFile.isDirectory()) || ((!this.m_OutputFile.isDirectory()) && (!this.m_SuppressOutput));
/* 260:    */   }
/* 261:    */   
/* 262:    */   protected void append(String s)
/* 263:    */   {
/* 264:491 */     if (generatesOutput()) {
/* 265:492 */       this.m_Buffer.append(s);
/* 266:    */     }
/* 267:494 */     if (!this.m_OutputFile.isDirectory()) {
/* 268:495 */       this.m_FileBuffer.append(s);
/* 269:    */     }
/* 270:    */   }
/* 271:    */   
/* 272:    */   protected String checkHeader()
/* 273:    */   {
/* 274:505 */     return checkBasic();
/* 275:    */   }
/* 276:    */   
/* 277:    */   protected abstract void doPrintHeader();
/* 278:    */   
/* 279:    */   public void printHeader()
/* 280:    */   {
/* 281:    */     String error;
/* 282:519 */     if ((error = checkHeader()) != null) {
/* 283:520 */       throw new IllegalStateException(error);
/* 284:    */     }
/* 285:523 */     doPrintHeader();
/* 286:    */   }
/* 287:    */   
/* 288:    */   protected abstract void doPrintClassification(Classifier paramClassifier, Instance paramInstance, int paramInt)
/* 289:    */     throws Exception;
/* 290:    */   
/* 291:    */   protected abstract void doPrintClassification(double[] paramArrayOfDouble, Instance paramInstance, int paramInt)
/* 292:    */     throws Exception;
/* 293:    */   
/* 294:    */   protected Instance preProcessInstance(Instance inst, Instance withMissing, Classifier classifier)
/* 295:    */     throws Exception
/* 296:    */   {
/* 297:564 */     if ((classifier instanceof InputMappedClassifier))
/* 298:    */     {
/* 299:565 */       inst = (Instance)inst.copy();
/* 300:566 */       inst = ((InputMappedClassifier)classifier).constructMappedInstance(inst);
/* 301:    */       
/* 302:    */ 
/* 303:569 */       int mappedClass = ((InputMappedClassifier)classifier).getMappedClassIndex();
/* 304:    */       
/* 305:    */ 
/* 306:572 */       withMissing.setMissing(mappedClass);
/* 307:    */     }
/* 308:    */     else
/* 309:    */     {
/* 310:574 */       withMissing.setMissing(withMissing.classIndex());
/* 311:    */     }
/* 312:577 */     return inst;
/* 313:    */   }
/* 314:    */   
/* 315:    */   public void printClassification(Classifier classifier, Instance inst, int index)
/* 316:    */     throws Exception
/* 317:    */   {
/* 318:    */     String error;
/* 319:593 */     if ((error = checkBasic()) != null) {
/* 320:594 */       throw new WekaException(error);
/* 321:    */     }
/* 322:597 */     doPrintClassification(classifier, inst, index);
/* 323:    */   }
/* 324:    */   
/* 325:    */   public void printClassification(double[] dist, Instance inst, int index)
/* 326:    */     throws Exception
/* 327:    */   {
/* 328:    */     String error;
/* 329:613 */     if ((error = checkBasic()) != null) {
/* 330:614 */       throw new WekaException(error);
/* 331:    */     }
/* 332:617 */     doPrintClassification(dist, inst, index);
/* 333:    */   }
/* 334:    */   
/* 335:    */   public void printClassifications(Classifier classifier, ConverterUtils.DataSource testset)
/* 336:    */     throws Exception
/* 337:    */   {
/* 338:634 */     int i = 0;
/* 339:635 */     testset.reset();
/* 340:637 */     if (((classifier instanceof BatchPredictor)) && (((BatchPredictor)classifier).implementsMoreEfficientBatchPrediction()))
/* 341:    */     {
/* 342:639 */       Instances test = testset.getDataSet(this.m_Header.classIndex());
/* 343:640 */       double[][] predictions = ((BatchPredictor)classifier).distributionsForInstances(test);
/* 344:642 */       for (i = 0; i < test.numInstances(); i++) {
/* 345:643 */         printClassification(predictions[i], test.instance(i), i);
/* 346:    */       }
/* 347:    */     }
/* 348:    */     else
/* 349:    */     {
/* 350:646 */       Instances test = testset.getStructure(this.m_Header.classIndex());
/* 351:647 */       while (testset.hasMoreElements(test))
/* 352:    */       {
/* 353:648 */         Instance inst = testset.nextElement(test);
/* 354:649 */         doPrintClassification(classifier, inst, i);
/* 355:650 */         i++;
/* 356:    */       }
/* 357:    */     }
/* 358:    */   }
/* 359:    */   
/* 360:    */   public void printClassifications(Classifier classifier, Instances testset)
/* 361:    */     throws Exception
/* 362:    */   {
/* 363:667 */     if (((classifier instanceof BatchPredictor)) && (((BatchPredictor)classifier).implementsMoreEfficientBatchPrediction()))
/* 364:    */     {
/* 365:669 */       double[][] predictions = ((BatchPredictor)classifier).distributionsForInstances(testset);
/* 366:671 */       for (int i = 0; i < testset.numInstances(); i++) {
/* 367:672 */         printClassification(predictions[i], testset.instance(i), i);
/* 368:    */       }
/* 369:    */     }
/* 370:    */     else
/* 371:    */     {
/* 372:675 */       for (int i = 0; i < testset.numInstances(); i++) {
/* 373:676 */         doPrintClassification(classifier, testset.instance(i), i);
/* 374:    */       }
/* 375:    */     }
/* 376:    */   }
/* 377:    */   
/* 378:    */   protected abstract void doPrintFooter();
/* 379:    */   
/* 380:    */   public void printFooter()
/* 381:    */     throws Exception
/* 382:    */   {
/* 383:    */     String error;
/* 384:696 */     if ((error = checkBasic()) != null) {
/* 385:697 */       throw new WekaException(error);
/* 386:    */     }
/* 387:700 */     doPrintFooter();
/* 388:703 */     if (!this.m_OutputFile.isDirectory()) {
/* 389:    */       try
/* 390:    */       {
/* 391:705 */         BufferedWriter writer = new BufferedWriter(new FileWriter(this.m_OutputFile));
/* 392:706 */         writer.write(this.m_FileBuffer.toString());
/* 393:707 */         writer.newLine();
/* 394:708 */         writer.flush();
/* 395:709 */         writer.close();
/* 396:    */       }
/* 397:    */       catch (Exception e)
/* 398:    */       {
/* 399:711 */         e.printStackTrace();
/* 400:    */       }
/* 401:    */     }
/* 402:    */   }
/* 403:    */   
/* 404:    */   public void print(Classifier classifier, ConverterUtils.DataSource testset)
/* 405:    */     throws Exception
/* 406:    */   {
/* 407:725 */     printHeader();
/* 408:726 */     printClassifications(classifier, testset);
/* 409:727 */     printFooter();
/* 410:    */   }
/* 411:    */   
/* 412:    */   public void print(Classifier classifier, Instances testset)
/* 413:    */     throws Exception
/* 414:    */   {
/* 415:739 */     printHeader();
/* 416:740 */     printClassifications(classifier, testset);
/* 417:741 */     printFooter();
/* 418:    */   }
/* 419:    */   
/* 420:    */   public static AbstractOutput fromCommandline(String cmdline)
/* 421:    */   {
/* 422:    */     AbstractOutput result;
/* 423:    */     try
/* 424:    */     {
/* 425:756 */       String[] options = Utils.splitOptions(cmdline);
/* 426:757 */       String classname = options[0];
/* 427:758 */       options[0] = "";
/* 428:759 */       result = (AbstractOutput)Utils.forName(AbstractOutput.class, classname, options);
/* 429:    */     }
/* 430:    */     catch (Exception e)
/* 431:    */     {
/* 432:763 */       result = null;
/* 433:    */     }
/* 434:766 */     return result;
/* 435:    */   }
/* 436:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.output.prediction.AbstractOutput
 * JD-Core Version:    0.7.0.1
 */