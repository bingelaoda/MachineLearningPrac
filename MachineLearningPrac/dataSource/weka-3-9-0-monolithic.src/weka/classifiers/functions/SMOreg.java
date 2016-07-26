/*   1:    */ package weka.classifiers.functions;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.classifiers.AbstractClassifier;
/*   7:    */ import weka.classifiers.functions.supportVector.Kernel;
/*   8:    */ import weka.classifiers.functions.supportVector.PolyKernel;
/*   9:    */ import weka.classifiers.functions.supportVector.RegOptimizer;
/*  10:    */ import weka.classifiers.functions.supportVector.RegSMOImproved;
/*  11:    */ import weka.core.AdditionalMeasureProducer;
/*  12:    */ import weka.core.Attribute;
/*  13:    */ import weka.core.Capabilities;
/*  14:    */ import weka.core.Capabilities.Capability;
/*  15:    */ import weka.core.Instance;
/*  16:    */ import weka.core.Instances;
/*  17:    */ import weka.core.Option;
/*  18:    */ import weka.core.OptionHandler;
/*  19:    */ import weka.core.RevisionUtils;
/*  20:    */ import weka.core.SelectedTag;
/*  21:    */ import weka.core.Tag;
/*  22:    */ import weka.core.TechnicalInformation;
/*  23:    */ import weka.core.TechnicalInformation.Field;
/*  24:    */ import weka.core.TechnicalInformation.Type;
/*  25:    */ import weka.core.TechnicalInformationHandler;
/*  26:    */ import weka.core.Utils;
/*  27:    */ import weka.core.WeightedInstancesHandler;
/*  28:    */ import weka.filters.Filter;
/*  29:    */ import weka.filters.unsupervised.attribute.NominalToBinary;
/*  30:    */ import weka.filters.unsupervised.attribute.Normalize;
/*  31:    */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*  32:    */ import weka.filters.unsupervised.attribute.Standardize;
/*  33:    */ 
/*  34:    */ public class SMOreg
/*  35:    */   extends AbstractClassifier
/*  36:    */   implements WeightedInstancesHandler, AdditionalMeasureProducer, TechnicalInformationHandler
/*  37:    */ {
/*  38:    */   private static final long serialVersionUID = -7149606251113102827L;
/*  39:    */   public static final int FILTER_NORMALIZE = 0;
/*  40:    */   public static final int FILTER_STANDARDIZE = 1;
/*  41:    */   public static final int FILTER_NONE = 2;
/*  42:176 */   public static final Tag[] TAGS_FILTER = { new Tag(0, "Normalize training data"), new Tag(1, "Standardize training data"), new Tag(2, "No normalization/standardization") };
/*  43:184 */   protected int m_filterType = 0;
/*  44:    */   protected NominalToBinary m_NominalToBinary;
/*  45:190 */   protected Filter m_Filter = null;
/*  46:    */   protected ReplaceMissingValues m_Missing;
/*  47:    */   protected boolean m_onlyNumeric;
/*  48:199 */   protected double m_C = 1.0D;
/*  49:203 */   protected double m_x1 = 1.0D;
/*  50:204 */   protected double m_x0 = 0.0D;
/*  51:207 */   protected RegOptimizer m_optimizer = new RegSMOImproved();
/*  52:210 */   protected Kernel m_kernel = new PolyKernel();
/*  53:    */   
/*  54:    */   public String globalInfo()
/*  55:    */   {
/*  56:219 */     return "SMOreg implements the support vector machine for regression. The parameters can be learned using various algorithms. The algorithm is selected by setting the RegOptimizer. The most popular algorithm (" + RegSMOImproved.class.getName().replaceAll(".*\\.", "") + ") is due to Shevade, Keerthi " + "et al and this is the default RegOptimizer.\n\n" + "For more information see:\n\n" + getTechnicalInformation().toString();
/*  57:    */   }
/*  58:    */   
/*  59:    */   public TechnicalInformation getTechnicalInformation()
/*  60:    */   {
/*  61:242 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  62:243 */     result.setValue(TechnicalInformation.Field.AUTHOR, "S.K. Shevade and S.S. Keerthi and C. Bhattacharyya and K.R.K. Murthy");
/*  63:244 */     result.setValue(TechnicalInformation.Field.TITLE, "Improvements to the SMO Algorithm for SVM Regression");
/*  64:245 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "IEEE Transactions on Neural Networks");
/*  65:246 */     result.setValue(TechnicalInformation.Field.YEAR, "1999");
/*  66:247 */     result.setValue(TechnicalInformation.Field.PS, "http://guppy.mpe.nus.edu.sg/~mpessk/svm/ieee_smo_reg.ps.gz");
/*  67:    */     
/*  68:249 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.TECHREPORT);
/*  69:250 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "A.J. Smola and B. Schoelkopf");
/*  70:251 */     additional.setValue(TechnicalInformation.Field.TITLE, "A tutorial on support vector regression");
/*  71:252 */     additional.setValue(TechnicalInformation.Field.NOTE, "NeuroCOLT2 Technical Report NC2-TR-1998-030");
/*  72:253 */     additional.setValue(TechnicalInformation.Field.YEAR, "1998");
/*  73:    */     
/*  74:255 */     return result;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public Enumeration<Option> listOptions()
/*  78:    */   {
/*  79:265 */     Vector<Option> result = new Vector();
/*  80:    */     
/*  81:267 */     result.addElement(new Option("\tThe complexity constant C.\n\t(default 1)", "C", 1, "-C <double>"));
/*  82:    */     
/*  83:    */ 
/*  84:    */ 
/*  85:    */ 
/*  86:272 */     result.addElement(new Option("\tWhether to 0=normalize/1=standardize/2=neither.\n\t(default 0=normalize)", "N", 1, "-N"));
/*  87:    */     
/*  88:    */ 
/*  89:    */ 
/*  90:    */ 
/*  91:277 */     result.addElement(new Option("\tOptimizer class used for solving quadratic optimization problem\n\t(default " + RegSMOImproved.class.getName() + ")", "I", 1, "-I <classname and parameters>"));
/*  92:    */     
/*  93:    */ 
/*  94:    */ 
/*  95:    */ 
/*  96:282 */     result.addElement(new Option("\tThe Kernel to use.\n\t(default: weka.classifiers.functions.supportVector.PolyKernel)", "K", 1, "-K <classname and parameters>"));
/*  97:    */     
/*  98:    */ 
/*  99:    */ 
/* 100:    */ 
/* 101:287 */     result.addAll(Collections.list(super.listOptions()));
/* 102:    */     
/* 103:289 */     result.addElement(new Option("", "", 0, "\nOptions specific to optimizer ('-I') " + getRegOptimizer().getClass().getName() + ":"));
/* 104:    */     
/* 105:    */ 
/* 106:    */ 
/* 107:    */ 
/* 108:294 */     result.addAll(Collections.list(getRegOptimizer().listOptions()));
/* 109:    */     
/* 110:296 */     result.addElement(new Option("", "", 0, "\nOptions specific to kernel ('-K') " + getKernel().getClass().getName() + ":"));
/* 111:    */     
/* 112:    */ 
/* 113:    */ 
/* 114:    */ 
/* 115:301 */     result.addAll(Collections.list(getKernel().listOptions()));
/* 116:    */     
/* 117:303 */     return result.elements();
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void setOptions(String[] options)
/* 121:    */     throws Exception
/* 122:    */   {
/* 123:386 */     String tmpStr = Utils.getOption('C', options);
/* 124:387 */     if (tmpStr.length() != 0) {
/* 125:388 */       setC(Double.parseDouble(tmpStr));
/* 126:    */     } else {
/* 127:390 */       setC(1.0D);
/* 128:    */     }
/* 129:393 */     String nString = Utils.getOption('N', options);
/* 130:394 */     if (nString.length() != 0) {
/* 131:395 */       setFilterType(new SelectedTag(Integer.parseInt(nString), TAGS_FILTER));
/* 132:    */     } else {
/* 133:397 */       setFilterType(new SelectedTag(0, TAGS_FILTER));
/* 134:    */     }
/* 135:400 */     tmpStr = Utils.getOption('I', options);
/* 136:401 */     String[] tmpOptions = Utils.splitOptions(tmpStr);
/* 137:402 */     if (tmpOptions.length != 0)
/* 138:    */     {
/* 139:403 */       tmpStr = tmpOptions[0];
/* 140:404 */       tmpOptions[0] = "";
/* 141:405 */       setRegOptimizer((RegOptimizer)Utils.forName(RegOptimizer.class, tmpStr, tmpOptions));
/* 142:    */     }
/* 143:    */     else
/* 144:    */     {
/* 145:409 */       setRegOptimizer(new RegSMOImproved());
/* 146:    */     }
/* 147:412 */     tmpStr = Utils.getOption('K', options);
/* 148:413 */     tmpOptions = Utils.splitOptions(tmpStr);
/* 149:414 */     if (tmpOptions.length != 0)
/* 150:    */     {
/* 151:415 */       tmpStr = tmpOptions[0];
/* 152:416 */       tmpOptions[0] = "";
/* 153:417 */       setKernel(Kernel.forName(tmpStr, tmpOptions));
/* 154:    */     }
/* 155:    */     else
/* 156:    */     {
/* 157:420 */       setKernel(new PolyKernel());
/* 158:    */     }
/* 159:423 */     super.setOptions(options);
/* 160:    */     
/* 161:425 */     Utils.checkForRemainingOptions(options);
/* 162:    */   }
/* 163:    */   
/* 164:    */   public String[] getOptions()
/* 165:    */   {
/* 166:435 */     Vector<String> result = new Vector();
/* 167:    */     
/* 168:437 */     result.add("-C");
/* 169:438 */     result.add("" + getC());
/* 170:    */     
/* 171:440 */     result.add("-N");
/* 172:441 */     result.add("" + this.m_filterType);
/* 173:    */     
/* 174:443 */     result.add("-I");
/* 175:444 */     result.add("" + getRegOptimizer().getClass().getName() + " " + Utils.joinOptions(getRegOptimizer().getOptions()));
/* 176:    */     
/* 177:446 */     result.add("-K");
/* 178:447 */     result.add("" + getKernel().getClass().getName() + " " + Utils.joinOptions(getKernel().getOptions()));
/* 179:    */     
/* 180:449 */     Collections.addAll(result, super.getOptions());
/* 181:    */     
/* 182:451 */     return (String[])result.toArray(new String[result.size()]);
/* 183:    */   }
/* 184:    */   
/* 185:    */   public Capabilities getCapabilities()
/* 186:    */   {
/* 187:460 */     Capabilities result = getKernel().getCapabilities();
/* 188:461 */     result.setOwner(this);
/* 189:    */     
/* 190:    */ 
/* 191:464 */     result.enableAllAttributeDependencies();
/* 192:467 */     if (result.handles(Capabilities.Capability.NUMERIC_ATTRIBUTES)) {
/* 193:468 */       result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 194:    */     }
/* 195:469 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 196:    */     
/* 197:    */ 
/* 198:472 */     result.disableAllClasses();
/* 199:473 */     result.disableAllClassDependencies();
/* 200:474 */     result.disable(Capabilities.Capability.NO_CLASS);
/* 201:475 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/* 202:476 */     result.enable(Capabilities.Capability.DATE_CLASS);
/* 203:477 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 204:    */     
/* 205:479 */     return result;
/* 206:    */   }
/* 207:    */   
/* 208:    */   public void buildClassifier(Instances instances)
/* 209:    */     throws Exception
/* 210:    */   {
/* 211:490 */     getCapabilities().testWithFail(instances);
/* 212:    */     
/* 213:    */ 
/* 214:493 */     instances = new Instances(instances);
/* 215:494 */     instances.deleteWithMissingClass();
/* 216:    */     
/* 217:    */ 
/* 218:    */ 
/* 219:    */ 
/* 220:499 */     Instances data = new Instances(instances, 0);
/* 221:500 */     for (int i = 0; i < instances.numInstances(); i++) {
/* 222:501 */       if (instances.instance(i).weight() > 0.0D) {
/* 223:502 */         data.add(instances.instance(i));
/* 224:    */       }
/* 225:    */     }
/* 226:506 */     if (data.numInstances() == 0) {
/* 227:507 */       throw new Exception("No training instances left after removing instance with either a weight null or a missing class!");
/* 228:    */     }
/* 229:509 */     instances = data;
/* 230:    */     
/* 231:511 */     this.m_onlyNumeric = true;
/* 232:512 */     for (int i = 0; i < instances.numAttributes(); i++) {
/* 233:513 */       if ((i != instances.classIndex()) && 
/* 234:514 */         (!instances.attribute(i).isNumeric()))
/* 235:    */       {
/* 236:515 */         this.m_onlyNumeric = false;
/* 237:516 */         break;
/* 238:    */       }
/* 239:    */     }
/* 240:520 */     this.m_Missing = new ReplaceMissingValues();
/* 241:521 */     this.m_Missing.setInputFormat(instances);
/* 242:522 */     instances = Filter.useFilter(instances, this.m_Missing);
/* 243:524 */     if (getCapabilities().handles(Capabilities.Capability.NUMERIC_ATTRIBUTES))
/* 244:    */     {
/* 245:525 */       if (!this.m_onlyNumeric)
/* 246:    */       {
/* 247:526 */         this.m_NominalToBinary = new NominalToBinary();
/* 248:527 */         this.m_NominalToBinary.setInputFormat(instances);
/* 249:528 */         instances = Filter.useFilter(instances, this.m_NominalToBinary);
/* 250:    */       }
/* 251:    */       else
/* 252:    */       {
/* 253:530 */         this.m_NominalToBinary = null;
/* 254:    */       }
/* 255:    */     }
/* 256:    */     else {
/* 257:533 */       this.m_NominalToBinary = null;
/* 258:    */     }
/* 259:537 */     double y0 = instances.instance(0).classValue();
/* 260:538 */     int index = 1;
/* 261:539 */     while ((index < instances.numInstances()) && (instances.instance(index).classValue() == y0)) {
/* 262:540 */       index++;
/* 263:    */     }
/* 264:542 */     if (index == instances.numInstances()) {
/* 265:545 */       throw new Exception("All class values are the same. At least two class values should be different");
/* 266:    */     }
/* 267:547 */     double y1 = instances.instance(index).classValue();
/* 268:550 */     if (this.m_filterType == 1)
/* 269:    */     {
/* 270:551 */       this.m_Filter = new Standardize();
/* 271:552 */       ((Standardize)this.m_Filter).setIgnoreClass(true);
/* 272:553 */       this.m_Filter.setInputFormat(instances);
/* 273:554 */       instances = Filter.useFilter(instances, this.m_Filter);
/* 274:    */     }
/* 275:555 */     else if (this.m_filterType == 0)
/* 276:    */     {
/* 277:556 */       this.m_Filter = new Normalize();
/* 278:557 */       ((Normalize)this.m_Filter).setIgnoreClass(true);
/* 279:558 */       this.m_Filter.setInputFormat(instances);
/* 280:559 */       instances = Filter.useFilter(instances, this.m_Filter);
/* 281:    */     }
/* 282:    */     else
/* 283:    */     {
/* 284:561 */       this.m_Filter = null;
/* 285:    */     }
/* 286:563 */     if (this.m_Filter != null)
/* 287:    */     {
/* 288:564 */       double z0 = instances.instance(0).classValue();
/* 289:565 */       double z1 = instances.instance(index).classValue();
/* 290:566 */       this.m_x1 = ((y0 - y1) / (z0 - z1));
/* 291:567 */       this.m_x0 = (y0 - this.m_x1 * z0);
/* 292:    */     }
/* 293:    */     else
/* 294:    */     {
/* 295:569 */       this.m_x1 = 1.0D;
/* 296:570 */       this.m_x0 = 0.0D;
/* 297:    */     }
/* 298:573 */     this.m_optimizer.setSMOReg(this);
/* 299:574 */     this.m_optimizer.buildClassifier(instances);
/* 300:    */   }
/* 301:    */   
/* 302:    */   public double classifyInstance(Instance instance)
/* 303:    */     throws Exception
/* 304:    */   {
/* 305:586 */     this.m_Missing.input(instance);
/* 306:587 */     this.m_Missing.batchFinished();
/* 307:588 */     instance = this.m_Missing.output();
/* 308:590 */     if ((!this.m_onlyNumeric) && (this.m_NominalToBinary != null))
/* 309:    */     {
/* 310:591 */       this.m_NominalToBinary.input(instance);
/* 311:592 */       this.m_NominalToBinary.batchFinished();
/* 312:593 */       instance = this.m_NominalToBinary.output();
/* 313:    */     }
/* 314:596 */     if (this.m_Filter != null)
/* 315:    */     {
/* 316:597 */       this.m_Filter.input(instance);
/* 317:598 */       this.m_Filter.batchFinished();
/* 318:599 */       instance = this.m_Filter.output();
/* 319:    */     }
/* 320:602 */     double result = this.m_optimizer.SVMOutput(instance);
/* 321:603 */     return result * this.m_x1 + this.m_x0;
/* 322:    */   }
/* 323:    */   
/* 324:    */   public String regOptimizerTipText()
/* 325:    */   {
/* 326:613 */     return "The learning algorithm.";
/* 327:    */   }
/* 328:    */   
/* 329:    */   public void setRegOptimizer(RegOptimizer regOptimizer)
/* 330:    */   {
/* 331:622 */     this.m_optimizer = regOptimizer;
/* 332:    */   }
/* 333:    */   
/* 334:    */   public RegOptimizer getRegOptimizer()
/* 335:    */   {
/* 336:631 */     return this.m_optimizer;
/* 337:    */   }
/* 338:    */   
/* 339:    */   public String kernelTipText()
/* 340:    */   {
/* 341:641 */     return "The kernel to use.";
/* 342:    */   }
/* 343:    */   
/* 344:    */   public void setKernel(Kernel value)
/* 345:    */   {
/* 346:650 */     this.m_kernel = value;
/* 347:    */   }
/* 348:    */   
/* 349:    */   public Kernel getKernel()
/* 350:    */   {
/* 351:659 */     return this.m_kernel;
/* 352:    */   }
/* 353:    */   
/* 354:    */   public String cTipText()
/* 355:    */   {
/* 356:669 */     return "The complexity parameter C.";
/* 357:    */   }
/* 358:    */   
/* 359:    */   public double getC()
/* 360:    */   {
/* 361:678 */     return this.m_C;
/* 362:    */   }
/* 363:    */   
/* 364:    */   public void setC(double v)
/* 365:    */   {
/* 366:687 */     this.m_C = v;
/* 367:    */   }
/* 368:    */   
/* 369:    */   public String filterTypeTipText()
/* 370:    */   {
/* 371:697 */     return "Determines how/if the data will be transformed.";
/* 372:    */   }
/* 373:    */   
/* 374:    */   public SelectedTag getFilterType()
/* 375:    */   {
/* 376:707 */     return new SelectedTag(this.m_filterType, TAGS_FILTER);
/* 377:    */   }
/* 378:    */   
/* 379:    */   public void setFilterType(SelectedTag newType)
/* 380:    */   {
/* 381:717 */     if (newType.getTags() == TAGS_FILTER) {
/* 382:718 */       this.m_filterType = newType.getSelectedTag().getID();
/* 383:    */     }
/* 384:    */   }
/* 385:    */   
/* 386:    */   public String toString()
/* 387:    */   {
/* 388:728 */     StringBuffer text = new StringBuffer();
/* 389:730 */     if ((this.m_optimizer == null) || (!this.m_optimizer.modelBuilt())) {
/* 390:731 */       return "SMOreg: No model built yet.";
/* 391:    */     }
/* 392:    */     try
/* 393:    */     {
/* 394:735 */       text.append(this.m_optimizer.toString());
/* 395:    */     }
/* 396:    */     catch (Exception e)
/* 397:    */     {
/* 398:738 */       return "Can't print SMVreg classifier.";
/* 399:    */     }
/* 400:741 */     return text.toString();
/* 401:    */   }
/* 402:    */   
/* 403:    */   public Enumeration<String> enumerateMeasures()
/* 404:    */   {
/* 405:752 */     Vector<String> result = new Vector();
/* 406:    */     
/* 407:754 */     result.addElement("measureKernelEvaluations");
/* 408:755 */     result.addElement("measureCacheHits");
/* 409:    */     
/* 410:757 */     return result.elements();
/* 411:    */   }
/* 412:    */   
/* 413:    */   public double getMeasure(String measureName)
/* 414:    */   {
/* 415:767 */     if (measureName.equalsIgnoreCase("measureKernelEvaluations")) {
/* 416:768 */       return measureKernelEvaluations();
/* 417:    */     }
/* 418:769 */     if (measureName.equalsIgnoreCase("measureCacheHits")) {
/* 419:770 */       return measureCacheHits();
/* 420:    */     }
/* 421:772 */     throw new IllegalArgumentException("Measure '" + measureName + "' is not supported!");
/* 422:    */   }
/* 423:    */   
/* 424:    */   protected double measureKernelEvaluations()
/* 425:    */   {
/* 426:781 */     if (this.m_optimizer != null) {
/* 427:782 */       return this.m_optimizer.getKernelEvaluations();
/* 428:    */     }
/* 429:784 */     return 0.0D;
/* 430:    */   }
/* 431:    */   
/* 432:    */   protected double measureCacheHits()
/* 433:    */   {
/* 434:794 */     if (this.m_optimizer != null) {
/* 435:795 */       return this.m_optimizer.getCacheHits();
/* 436:    */     }
/* 437:797 */     return 0.0D;
/* 438:    */   }
/* 439:    */   
/* 440:    */   public String getRevision()
/* 441:    */   {
/* 442:807 */     return RevisionUtils.extract("$Revision: 12558 $");
/* 443:    */   }
/* 444:    */   
/* 445:    */   public static void main(String[] args)
/* 446:    */   {
/* 447:816 */     runClassifier(new SMOreg(), args);
/* 448:    */   }
/* 449:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.SMOreg
 * JD-Core Version:    0.7.0.1
 */