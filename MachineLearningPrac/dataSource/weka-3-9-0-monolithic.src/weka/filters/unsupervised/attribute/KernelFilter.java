/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.Vector;
/*   8:    */ import weka.classifiers.functions.supportVector.Kernel;
/*   9:    */ import weka.classifiers.functions.supportVector.PolyKernel;
/*  10:    */ import weka.classifiers.functions.supportVector.RBFKernel;
/*  11:    */ import weka.core.Attribute;
/*  12:    */ import weka.core.Capabilities;
/*  13:    */ import weka.core.Capabilities.Capability;
/*  14:    */ import weka.core.DenseInstance;
/*  15:    */ import weka.core.Instance;
/*  16:    */ import weka.core.Instances;
/*  17:    */ import weka.core.Option;
/*  18:    */ import weka.core.OptionHandler;
/*  19:    */ import weka.core.RevisionUtils;
/*  20:    */ import weka.core.SingleIndex;
/*  21:    */ import weka.core.TechnicalInformation;
/*  22:    */ import weka.core.TechnicalInformation.Field;
/*  23:    */ import weka.core.TechnicalInformation.Type;
/*  24:    */ import weka.core.TechnicalInformationHandler;
/*  25:    */ import weka.core.Utils;
/*  26:    */ import weka.core.converters.ConverterUtils.DataSource;
/*  27:    */ import weka.core.expressionlanguage.common.IfElseMacro;
/*  28:    */ import weka.core.expressionlanguage.common.JavaMacro;
/*  29:    */ import weka.core.expressionlanguage.common.MacroDeclarationsCompositor;
/*  30:    */ import weka.core.expressionlanguage.common.MathFunctions;
/*  31:    */ import weka.core.expressionlanguage.common.Primitives.DoubleExpression;
/*  32:    */ import weka.core.expressionlanguage.common.SimpleVariableDeclarations;
/*  33:    */ import weka.core.expressionlanguage.common.SimpleVariableDeclarations.VariableInitializer;
/*  34:    */ import weka.core.expressionlanguage.core.MacroDeclarations;
/*  35:    */ import weka.core.expressionlanguage.core.Node;
/*  36:    */ import weka.core.expressionlanguage.parser.Parser;
/*  37:    */ import weka.filters.AllFilter;
/*  38:    */ import weka.filters.Filter;
/*  39:    */ import weka.filters.SimpleBatchFilter;
/*  40:    */ import weka.filters.UnsupervisedFilter;
/*  41:    */ 
/*  42:    */ public class KernelFilter
/*  43:    */   extends SimpleBatchFilter
/*  44:    */   implements UnsupervisedFilter, TechnicalInformationHandler
/*  45:    */ {
/*  46:    */   static final long serialVersionUID = 213800899640387499L;
/*  47:    */   protected int m_NumTrainInstances;
/*  48:213 */   protected Kernel m_Kernel = new PolyKernel();
/*  49:216 */   protected Kernel m_ActualKernel = null;
/*  50:    */   protected boolean m_checksTurnedOff;
/*  51:    */   protected NominalToBinary m_NominalToBinary;
/*  52:    */   protected ReplaceMissingValues m_Missing;
/*  53:234 */   protected File m_InitFile = new File(System.getProperty("user.dir"));
/*  54:241 */   protected SingleIndex m_InitFileClassIndex = new SingleIndex("last");
/*  55:244 */   protected boolean m_Initialized = false;
/*  56:250 */   protected String m_KernelFactorExpression = "1";
/*  57:257 */   protected double m_KernelFactor = 1.0D;
/*  58:260 */   protected Filter m_Filter = new Center();
/*  59:263 */   protected Filter m_ActualFilter = null;
/*  60:    */   
/*  61:    */   public String globalInfo()
/*  62:    */   {
/*  63:273 */     return "Converts the given set of predictor variables into a kernel matrix. The class value remains unchangedm, as long as the preprocessing filter doesn't change it.\nBy default, the data is preprocessed with the Center filter, but the user can choose any filter (NB: one must be careful that the filter does not alter the class attribute unintentionally). With weka.filters.AllFilter the preprocessing gets disabled.\n\nFor more information regarding preprocessing the data, see:\n\n" + getTechnicalInformation().toString();
/*  64:    */   }
/*  65:    */   
/*  66:    */   public TechnicalInformation getTechnicalInformation()
/*  67:    */   {
/*  68:295 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  69:296 */     result.setValue(TechnicalInformation.Field.AUTHOR, "K.P. Bennett and M.J. Embrechts");
/*  70:297 */     result.setValue(TechnicalInformation.Field.TITLE, "An Optimization Perspective on Kernel Partial Least Squares Regression");
/*  71:    */     
/*  72:299 */     result.setValue(TechnicalInformation.Field.YEAR, "2003");
/*  73:300 */     result.setValue(TechnicalInformation.Field.EDITOR, "J. Suykens et al.");
/*  74:301 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Advances in Learning Theory: Methods, Models and Applications");
/*  75:    */     
/*  76:303 */     result.setValue(TechnicalInformation.Field.PAGES, "227-249");
/*  77:304 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "IOS Press, Amsterdam, The Netherlands");
/*  78:305 */     result.setValue(TechnicalInformation.Field.SERIES, "NATO Science Series, Series III: Computer and System Sciences");
/*  79:    */     
/*  80:307 */     result.setValue(TechnicalInformation.Field.VOLUME, "190");
/*  81:    */     
/*  82:309 */     return result;
/*  83:    */   }
/*  84:    */   
/*  85:    */   public Enumeration<Option> listOptions()
/*  86:    */   {
/*  87:320 */     Vector<Option> result = new Vector();
/*  88:    */     
/*  89:322 */     result.addElement(new Option("\tTurns off all checks - use with caution!\n\tTurning them off assumes that data is purely numeric, doesn't\n\tcontain any missing values, and has a nominal class. Turning them\n\toff also means that no header information will be stored if the\n\tmachine is linear. Finally, it also assumes that no instance has\n\ta weight equal to 0.\n\t(default: checks on)", "no-checks", 0, "-no-checks"));
/*  90:    */     
/*  91:    */ 
/*  92:    */ 
/*  93:    */ 
/*  94:    */ 
/*  95:    */ 
/*  96:    */ 
/*  97:330 */     result.addElement(new Option("\tThe file to initialize the filter with (optional).", "F", 1, "-F <filename>"));
/*  98:    */     
/*  99:    */ 
/* 100:    */ 
/* 101:334 */     result.addElement(new Option("\tThe class index for the file to initialize with,\n\tFirst and last are valid (optional, default: last).", "C", 1, "-C <num>"));
/* 102:    */     
/* 103:    */ 
/* 104:    */ 
/* 105:    */ 
/* 106:339 */     result.addElement(new Option("\tThe Kernel to use.\n\t(default: weka.classifiers.functions.supportVector.PolyKernel)", "K", 1, "-K <classname and parameters>"));
/* 107:    */     
/* 108:    */ 
/* 109:    */ 
/* 110:343 */     result.addElement(new Option("\tDefines a factor for the kernel.\n\t\t- RBFKernel: a factor for gamma\n\t\t\tStandardize: 1/(2*N)\n\t\t\tNormalize..: 6/N\n\tAvailable parameters are:\n\t\tN for # of instances, A for # of attributes\n\t(default: 1)", "kernel-factor", 0, "-kernel-factor"));
/* 111:    */     
/* 112:    */ 
/* 113:    */ 
/* 114:    */ 
/* 115:    */ 
/* 116:    */ 
/* 117:350 */     result.addElement(new Option("\tThe Filter used for preprocessing (use weka.filters.AllFilter\n\tto disable preprocessing).\n\t(default: " + Center.class.getName() + ")", "P", 1, "-P <classname and parameters>"));
/* 118:    */     
/* 119:    */ 
/* 120:    */ 
/* 121:    */ 
/* 122:    */ 
/* 123:    */ 
/* 124:357 */     result.addAll(Collections.list(super.listOptions()));
/* 125:    */     
/* 126:    */ 
/* 127:360 */     result.addElement(new Option("", "", 0, "\nOptions specific to kernel " + getKernel().getClass().getName() + ":"));
/* 128:    */     
/* 129:    */ 
/* 130:363 */     result.addAll(Collections.list(getKernel().listOptions()));
/* 131:367 */     if ((getPreprocessing() instanceof OptionHandler))
/* 132:    */     {
/* 133:368 */       result.addElement(new Option("", "", 0, "\nOptions specific to preprocessing filter " + getPreprocessing().getClass().getName() + ":"));
/* 134:    */       
/* 135:    */ 
/* 136:    */ 
/* 137:372 */       result.addAll(Collections.list(getPreprocessing().listOptions()));
/* 138:    */     }
/* 139:376 */     return result.elements();
/* 140:    */   }
/* 141:    */   
/* 142:    */   public String[] getOptions()
/* 143:    */   {
/* 144:387 */     Vector<String> result = new Vector();
/* 145:389 */     if (getChecksTurnedOff()) {
/* 146:390 */       result.add("-no-checks");
/* 147:    */     }
/* 148:393 */     if ((getInitFile() != null) && (getInitFile().isFile()))
/* 149:    */     {
/* 150:394 */       result.add("-F");
/* 151:395 */       result.add("" + getInitFile().getAbsolutePath());
/* 152:    */       
/* 153:397 */       result.add("-C");
/* 154:398 */       result.add("" + getInitFileClassIndex());
/* 155:    */     }
/* 156:401 */     result.add("-K");
/* 157:402 */     result.add("" + getKernel().getClass().getName() + " " + Utils.joinOptions(getKernel().getOptions()));
/* 158:    */     
/* 159:    */ 
/* 160:405 */     result.add("-kernel-factor");
/* 161:406 */     result.add("" + getKernelFactorExpression());
/* 162:    */     
/* 163:408 */     result.add("-P");
/* 164:409 */     String tmpStr = getPreprocessing().getClass().getName();
/* 165:410 */     if ((getPreprocessing() instanceof OptionHandler)) {
/* 166:411 */       tmpStr = tmpStr + " " + Utils.joinOptions(getPreprocessing().getOptions());
/* 167:    */     }
/* 168:414 */     result.add("" + tmpStr);
/* 169:    */     
/* 170:416 */     Collections.addAll(result, super.getOptions());
/* 171:    */     
/* 172:418 */     return (String[])result.toArray(new String[result.size()]);
/* 173:    */   }
/* 174:    */   
/* 175:    */   public void setOptions(String[] options)
/* 176:    */     throws Exception
/* 177:    */   {
/* 178:535 */     setChecksTurnedOff(Utils.getFlag("no-checks", options));
/* 179:    */     
/* 180:537 */     String tmpStr = Utils.getOption('F', options);
/* 181:538 */     if (tmpStr.length() != 0) {
/* 182:539 */       setInitFile(new File(tmpStr));
/* 183:    */     } else {
/* 184:541 */       setInitFile(null);
/* 185:    */     }
/* 186:544 */     tmpStr = Utils.getOption('C', options);
/* 187:545 */     if (tmpStr.length() != 0) {
/* 188:546 */       setInitFileClassIndex(tmpStr);
/* 189:    */     } else {
/* 190:548 */       setInitFileClassIndex("last");
/* 191:    */     }
/* 192:551 */     tmpStr = Utils.getOption('K', options);
/* 193:552 */     String[] tmpOptions = Utils.splitOptions(tmpStr);
/* 194:553 */     if (tmpOptions.length != 0)
/* 195:    */     {
/* 196:554 */       tmpStr = tmpOptions[0];
/* 197:555 */       tmpOptions[0] = "";
/* 198:556 */       setKernel(Kernel.forName(tmpStr, tmpOptions));
/* 199:    */     }
/* 200:559 */     tmpStr = Utils.getOption("kernel-factor", options);
/* 201:560 */     if (tmpStr.length() != 0) {
/* 202:561 */       setKernelFactorExpression(tmpStr);
/* 203:    */     } else {
/* 204:563 */       setKernelFactorExpression("1");
/* 205:    */     }
/* 206:566 */     tmpStr = Utils.getOption("P", options);
/* 207:567 */     tmpOptions = Utils.splitOptions(tmpStr);
/* 208:568 */     if (tmpOptions.length != 0)
/* 209:    */     {
/* 210:569 */       tmpStr = tmpOptions[0];
/* 211:570 */       tmpOptions[0] = "";
/* 212:571 */       setPreprocessing((Filter)Utils.forName(Filter.class, tmpStr, tmpOptions));
/* 213:    */     }
/* 214:    */     else
/* 215:    */     {
/* 216:573 */       setPreprocessing(new Center());
/* 217:    */     }
/* 218:576 */     super.setOptions(options);
/* 219:    */     
/* 220:578 */     Utils.checkForRemainingOptions(tmpOptions);
/* 221:    */   }
/* 222:    */   
/* 223:    */   public String initFileTipText()
/* 224:    */   {
/* 225:588 */     return "The dataset to initialize the filter with.";
/* 226:    */   }
/* 227:    */   
/* 228:    */   public File getInitFile()
/* 229:    */   {
/* 230:597 */     return this.m_InitFile;
/* 231:    */   }
/* 232:    */   
/* 233:    */   public void setInitFile(File value)
/* 234:    */   {
/* 235:606 */     this.m_InitFile = value;
/* 236:    */   }
/* 237:    */   
/* 238:    */   public String initFileClassIndexTipText()
/* 239:    */   {
/* 240:616 */     return "The class index of the dataset to initialize the filter with (first and last are valid).";
/* 241:    */   }
/* 242:    */   
/* 243:    */   public String getInitFileClassIndex()
/* 244:    */   {
/* 245:625 */     return this.m_InitFileClassIndex.getSingleIndex();
/* 246:    */   }
/* 247:    */   
/* 248:    */   public void setInitFileClassIndex(String value)
/* 249:    */   {
/* 250:634 */     this.m_InitFileClassIndex.setSingleIndex(value);
/* 251:    */   }
/* 252:    */   
/* 253:    */   public String kernelTipText()
/* 254:    */   {
/* 255:644 */     return "The kernel to use.";
/* 256:    */   }
/* 257:    */   
/* 258:    */   public Kernel getKernel()
/* 259:    */   {
/* 260:653 */     return this.m_Kernel;
/* 261:    */   }
/* 262:    */   
/* 263:    */   public void setKernel(Kernel value)
/* 264:    */   {
/* 265:662 */     this.m_Kernel = value;
/* 266:    */   }
/* 267:    */   
/* 268:    */   public void setChecksTurnedOff(boolean value)
/* 269:    */   {
/* 270:672 */     this.m_checksTurnedOff = value;
/* 271:    */   }
/* 272:    */   
/* 273:    */   public boolean getChecksTurnedOff()
/* 274:    */   {
/* 275:681 */     return this.m_checksTurnedOff;
/* 276:    */   }
/* 277:    */   
/* 278:    */   public String checksTurnedOffTipText()
/* 279:    */   {
/* 280:691 */     return "Turns time-consuming checks off - use with caution.";
/* 281:    */   }
/* 282:    */   
/* 283:    */   public String kernelFactorExpressionTipText()
/* 284:    */   {
/* 285:701 */     return "The factor for the kernel, with A = # of attributes and N = # of instances.";
/* 286:    */   }
/* 287:    */   
/* 288:    */   public String getKernelFactorExpression()
/* 289:    */   {
/* 290:710 */     return this.m_KernelFactorExpression;
/* 291:    */   }
/* 292:    */   
/* 293:    */   public void setKernelFactorExpression(String value)
/* 294:    */   {
/* 295:719 */     this.m_KernelFactorExpression = value;
/* 296:    */   }
/* 297:    */   
/* 298:    */   public String preprocessingTipText()
/* 299:    */   {
/* 300:729 */     return "Sets the filter to use for preprocessing (use the AllFilter for no preprocessing).";
/* 301:    */   }
/* 302:    */   
/* 303:    */   public void setPreprocessing(Filter value)
/* 304:    */   {
/* 305:739 */     this.m_Filter = value;
/* 306:740 */     this.m_ActualFilter = null;
/* 307:    */   }
/* 308:    */   
/* 309:    */   public Filter getPreprocessing()
/* 310:    */   {
/* 311:749 */     return this.m_Filter;
/* 312:    */   }
/* 313:    */   
/* 314:    */   protected void reset()
/* 315:    */   {
/* 316:757 */     super.reset();
/* 317:    */     
/* 318:759 */     this.m_Initialized = false;
/* 319:    */   }
/* 320:    */   
/* 321:    */   protected Instances determineOutputFormat(Instances inputFormat)
/* 322:    */     throws Exception
/* 323:    */   {
/* 324:777 */     return new Instances(inputFormat);
/* 325:    */   }
/* 326:    */   
/* 327:    */   public void initFilter(Instances instances)
/* 328:    */     throws Exception
/* 329:    */   {
/* 330:791 */     SimpleVariableDeclarations variables = new SimpleVariableDeclarations();
/* 331:792 */     variables.addDouble("A");
/* 332:793 */     variables.addDouble("N");
/* 333:    */     
/* 334:795 */     Node root = Parser.parse(getKernelFactorExpression(), variables, new MacroDeclarationsCompositor(new MacroDeclarations[] { new MathFunctions(), new IfElseMacro(), new JavaMacro() }));
/* 335:808 */     if (!(root instanceof Primitives.DoubleExpression)) {
/* 336:809 */       throw new Exception("Kernel factor expression must be of double type!");
/* 337:    */     }
/* 338:811 */     if (variables.getInitializer().hasVariable("A")) {
/* 339:812 */       variables.getInitializer().setDouble("A", instances.numAttributes());
/* 340:    */     }
/* 341:813 */     if (variables.getInitializer().hasVariable("N")) {
/* 342:814 */       variables.getInitializer().setDouble("N", instances.numInstances());
/* 343:    */     }
/* 344:816 */     this.m_KernelFactor = ((Primitives.DoubleExpression)root).evaluate();
/* 345:819 */     if (!this.m_checksTurnedOff)
/* 346:    */     {
/* 347:820 */       this.m_Missing = new ReplaceMissingValues();
/* 348:821 */       this.m_Missing.setInputFormat(instances);
/* 349:822 */       instances = Filter.useFilter(instances, this.m_Missing);
/* 350:    */     }
/* 351:    */     else
/* 352:    */     {
/* 353:824 */       this.m_Missing = null;
/* 354:    */     }
/* 355:827 */     if (getKernel().getCapabilities().handles(Capabilities.Capability.NUMERIC_ATTRIBUTES))
/* 356:    */     {
/* 357:828 */       boolean onlyNumeric = true;
/* 358:829 */       if (!this.m_checksTurnedOff) {
/* 359:830 */         for (int i = 0; i < instances.numAttributes(); i++) {
/* 360:831 */           if ((i != instances.classIndex()) && 
/* 361:832 */             (!instances.attribute(i).isNumeric()))
/* 362:    */           {
/* 363:833 */             onlyNumeric = false;
/* 364:834 */             break;
/* 365:    */           }
/* 366:    */         }
/* 367:    */       }
/* 368:840 */       if (!onlyNumeric)
/* 369:    */       {
/* 370:841 */         this.m_NominalToBinary = new NominalToBinary();
/* 371:842 */         this.m_NominalToBinary.setInputFormat(instances);
/* 372:843 */         instances = Filter.useFilter(instances, this.m_NominalToBinary);
/* 373:    */       }
/* 374:    */       else
/* 375:    */       {
/* 376:845 */         this.m_NominalToBinary = null;
/* 377:    */       }
/* 378:    */     }
/* 379:    */     else
/* 380:    */     {
/* 381:848 */       this.m_NominalToBinary = null;
/* 382:    */     }
/* 383:851 */     if ((this.m_Filter != null) && (this.m_Filter.getClass() != AllFilter.class))
/* 384:    */     {
/* 385:852 */       this.m_ActualFilter = Filter.makeCopy(this.m_Filter);
/* 386:853 */       this.m_ActualFilter.setInputFormat(instances);
/* 387:854 */       instances = Filter.useFilter(instances, this.m_ActualFilter);
/* 388:    */     }
/* 389:    */     else
/* 390:    */     {
/* 391:856 */       this.m_ActualFilter = null;
/* 392:    */     }
/* 393:859 */     this.m_NumTrainInstances = instances.numInstances();
/* 394:    */     
/* 395:    */ 
/* 396:862 */     this.m_ActualKernel = Kernel.makeCopy(this.m_Kernel);
/* 397:863 */     if ((this.m_ActualKernel instanceof RBFKernel)) {
/* 398:864 */       ((RBFKernel)this.m_ActualKernel).setGamma(this.m_KernelFactor * ((RBFKernel)this.m_ActualKernel).getGamma());
/* 399:    */     }
/* 400:868 */     this.m_ActualKernel.buildKernel(instances);
/* 401:    */     
/* 402:870 */     this.m_Initialized = true;
/* 403:    */   }
/* 404:    */   
/* 405:    */   public Capabilities getCapabilities()
/* 406:    */   {
/* 407:    */     Capabilities result;
/* 408:883 */     if (getKernel() == null)
/* 409:    */     {
/* 410:884 */       Capabilities result = super.getCapabilities();
/* 411:885 */       result.disableAll();
/* 412:    */     }
/* 413:    */     else
/* 414:    */     {
/* 415:887 */       result = getKernel().getCapabilities();
/* 416:    */     }
/* 417:890 */     result.setMinimumNumberInstances(0);
/* 418:    */     
/* 419:892 */     return result;
/* 420:    */   }
/* 421:    */   
/* 422:    */   protected Instances process(Instances instances)
/* 423:    */     throws Exception
/* 424:    */   {
/* 425:907 */     if (!this.m_Initialized) {
/* 426:909 */       if ((getInitFile() != null) && (getInitFile().isFile()))
/* 427:    */       {
/* 428:910 */         ConverterUtils.DataSource source = new ConverterUtils.DataSource(getInitFile().getAbsolutePath());
/* 429:911 */         Instances data = source.getDataSet();
/* 430:912 */         this.m_InitFileClassIndex.setUpper(data.numAttributes() - 1);
/* 431:913 */         data.setClassIndex(this.m_InitFileClassIndex.getIndex());
/* 432:914 */         initFilter(data);
/* 433:    */       }
/* 434:    */       else
/* 435:    */       {
/* 436:916 */         initFilter(instances);
/* 437:    */       }
/* 438:    */     }
/* 439:921 */     if (this.m_Missing != null) {
/* 440:922 */       instances = Filter.useFilter(instances, this.m_Missing);
/* 441:    */     }
/* 442:924 */     if (this.m_NominalToBinary != null) {
/* 443:925 */       instances = Filter.useFilter(instances, this.m_NominalToBinary);
/* 444:    */     }
/* 445:927 */     if (this.m_ActualFilter != null) {
/* 446:928 */       instances = Filter.useFilter(instances, this.m_ActualFilter);
/* 447:    */     }
/* 448:932 */     int classIndex = instances.classIndex();
/* 449:933 */     double[] classes = null;
/* 450:934 */     Attribute classAttribute = null;
/* 451:935 */     if (classIndex >= 0)
/* 452:    */     {
/* 453:936 */       classes = instances.attributeToDoubleArray(instances.classIndex());
/* 454:937 */       classAttribute = (Attribute)instances.classAttribute().copy();
/* 455:938 */       instances.setClassIndex(-1);
/* 456:939 */       instances.deleteAttributeAt(classIndex);
/* 457:    */     }
/* 458:943 */     ArrayList<Attribute> atts = new ArrayList();
/* 459:944 */     for (int j = 0; j < this.m_NumTrainInstances; j++) {
/* 460:945 */       atts.add(new Attribute("Kernel " + j));
/* 461:    */     }
/* 462:947 */     if (classIndex >= 0) {
/* 463:948 */       atts.add(classAttribute);
/* 464:    */     }
/* 465:950 */     Instances result = new Instances("Kernel", atts, 0);
/* 466:951 */     if (classIndex >= 0) {
/* 467:952 */       result.setClassIndex(result.numAttributes() - 1);
/* 468:    */     }
/* 469:956 */     for (int i = 0; i < instances.numInstances(); i++)
/* 470:    */     {
/* 471:957 */       double[] k = new double[this.m_NumTrainInstances + (classIndex >= 0 ? 1 : 0)];
/* 472:959 */       for (int j = 0; j < this.m_NumTrainInstances; j++)
/* 473:    */       {
/* 474:960 */         double v = this.m_ActualKernel.eval(-1, j, instances.instance(i));
/* 475:961 */         k[j] = v;
/* 476:    */       }
/* 477:963 */       if (classIndex >= 0) {
/* 478:964 */         k[(k.length - 1)] = classes[i];
/* 479:    */       }
/* 480:968 */       Instance in = new DenseInstance(1.0D, k);
/* 481:969 */       result.add(in);
/* 482:    */     }
/* 483:972 */     if (!isFirstBatchDone()) {
/* 484:973 */       setOutputFormat(result);
/* 485:    */     }
/* 486:976 */     return result;
/* 487:    */   }
/* 488:    */   
/* 489:    */   public String getRevision()
/* 490:    */   {
/* 491:986 */     return RevisionUtils.extract("$Revision: 12612 $");
/* 492:    */   }
/* 493:    */   
/* 494:    */   public static void main(String[] args)
/* 495:    */   {
/* 496:995 */     runFilter(new KernelFilter(), args);
/* 497:    */   }
/* 498:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.KernelFilter
 * JD-Core Version:    0.7.0.1
 */