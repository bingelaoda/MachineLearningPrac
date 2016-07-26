/*   1:    */ package weka.classifiers.functions;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import no.uib.cipr.matrix.DenseCholesky;
/*   6:    */ import no.uib.cipr.matrix.DenseVector;
/*   7:    */ import no.uib.cipr.matrix.Matrices;
/*   8:    */ import no.uib.cipr.matrix.Matrix;
/*   9:    */ import no.uib.cipr.matrix.UpperSPDDenseMatrix;
/*  10:    */ import weka.classifiers.ConditionalDensityEstimator;
/*  11:    */ import weka.classifiers.IntervalEstimator;
/*  12:    */ import weka.classifiers.RandomizableClassifier;
/*  13:    */ import weka.classifiers.functions.supportVector.CachedKernel;
/*  14:    */ import weka.classifiers.functions.supportVector.Kernel;
/*  15:    */ import weka.classifiers.functions.supportVector.PolyKernel;
/*  16:    */ import weka.core.Attribute;
/*  17:    */ import weka.core.Capabilities;
/*  18:    */ import weka.core.Capabilities.Capability;
/*  19:    */ import weka.core.Instance;
/*  20:    */ import weka.core.Instances;
/*  21:    */ import weka.core.Option;
/*  22:    */ import weka.core.OptionHandler;
/*  23:    */ import weka.core.SelectedTag;
/*  24:    */ import weka.core.Statistics;
/*  25:    */ import weka.core.Tag;
/*  26:    */ import weka.core.TechnicalInformation;
/*  27:    */ import weka.core.TechnicalInformation.Field;
/*  28:    */ import weka.core.TechnicalInformation.Type;
/*  29:    */ import weka.core.TechnicalInformationHandler;
/*  30:    */ import weka.core.Utils;
/*  31:    */ import weka.core.WeightedInstancesHandler;
/*  32:    */ import weka.filters.Filter;
/*  33:    */ import weka.filters.unsupervised.attribute.NominalToBinary;
/*  34:    */ import weka.filters.unsupervised.attribute.Normalize;
/*  35:    */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*  36:    */ import weka.filters.unsupervised.attribute.Standardize;
/*  37:    */ 
/*  38:    */ public class GaussianProcesses
/*  39:    */   extends RandomizableClassifier
/*  40:    */   implements IntervalEstimator, ConditionalDensityEstimator, TechnicalInformationHandler, WeightedInstancesHandler
/*  41:    */ {
/*  42:    */   static final long serialVersionUID = -8620066949967678545L;
/*  43:    */   protected NominalToBinary m_NominalToBinary;
/*  44:    */   public static final int FILTER_NORMALIZE = 0;
/*  45:    */   public static final int FILTER_STANDARDIZE = 1;
/*  46:    */   public static final int FILTER_NONE = 2;
/*  47:156 */   public static final Tag[] TAGS_FILTER = { new Tag(0, "Normalize training data"), new Tag(1, "Standardize training data"), new Tag(2, "No normalization/standardization") };
/*  48:162 */   protected Filter m_Filter = null;
/*  49:165 */   protected int m_filterType = 0;
/*  50:    */   protected ReplaceMissingValues m_Missing;
/*  51:175 */   protected boolean m_checksTurnedOff = false;
/*  52:178 */   protected double m_delta = 1.0D;
/*  53:181 */   protected double m_deltaSquared = 1.0D;
/*  54:    */   protected double m_Alin;
/*  55:    */   protected double m_Blin;
/*  56:191 */   protected Kernel m_kernel = new PolyKernel();
/*  57:    */   protected Kernel m_actualKernel;
/*  58:197 */   protected int m_NumTrain = 0;
/*  59:    */   protected double m_avg_target;
/*  60:    */   public Matrix m_L;
/*  61:    */   protected no.uib.cipr.matrix.Vector m_t;
/*  62:    */   protected double[] m_weights;
/*  63:    */   
/*  64:    */   public String globalInfo()
/*  65:    */   {
/*  66:219 */     return " Implements Gaussian processes for regression without hyperparameter-tuning. To make choosing an appropriate noise level easier, this implementation applies normalization/standardization to the target attribute as well as the other attributes (if  normalization/standardizaton is turned on). Missing values are replaced by the global mean/mode. Nominal attributes are converted to binary ones. Note that kernel caching is turned off if the kernel used implements CachedKernel.";
/*  67:    */   }
/*  68:    */   
/*  69:    */   public TechnicalInformation getTechnicalInformation()
/*  70:    */   {
/*  71:241 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.MISC);
/*  72:242 */     result.setValue(TechnicalInformation.Field.AUTHOR, "David J.C. Mackay");
/*  73:243 */     result.setValue(TechnicalInformation.Field.YEAR, "1998");
/*  74:244 */     result.setValue(TechnicalInformation.Field.TITLE, "Introduction to Gaussian Processes");
/*  75:245 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Dept. of Physics, Cambridge University, UK");
/*  76:    */     
/*  77:247 */     result.setValue(TechnicalInformation.Field.PS, "http://wol.ra.phy.cam.ac.uk/mackay/gpB.ps.gz");
/*  78:    */     
/*  79:249 */     return result;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public Capabilities getCapabilities()
/*  83:    */   {
/*  84:259 */     Capabilities result = getKernel().getCapabilities();
/*  85:260 */     result.setOwner(this);
/*  86:    */     
/*  87:    */ 
/*  88:263 */     result.enableAllAttributeDependencies();
/*  89:266 */     if (result.handles(Capabilities.Capability.NUMERIC_ATTRIBUTES)) {
/*  90:267 */       result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  91:    */     }
/*  92:269 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  93:    */     
/*  94:    */ 
/*  95:272 */     result.disableAllClasses();
/*  96:273 */     result.disableAllClassDependencies();
/*  97:274 */     result.disable(Capabilities.Capability.NO_CLASS);
/*  98:275 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  99:276 */     result.enable(Capabilities.Capability.DATE_CLASS);
/* 100:277 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 101:    */     
/* 102:279 */     return result;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public void buildClassifier(Instances insts)
/* 106:    */     throws Exception
/* 107:    */   {
/* 108:292 */     if (!this.m_checksTurnedOff)
/* 109:    */     {
/* 110:294 */       getCapabilities().testWithFail(insts);
/* 111:    */       
/* 112:    */ 
/* 113:297 */       insts = new Instances(insts);
/* 114:298 */       insts.deleteWithMissingClass();
/* 115:299 */       this.m_Missing = new ReplaceMissingValues();
/* 116:300 */       this.m_Missing.setInputFormat(insts);
/* 117:301 */       insts = Filter.useFilter(insts, this.m_Missing);
/* 118:    */     }
/* 119:    */     else
/* 120:    */     {
/* 121:303 */       this.m_Missing = null;
/* 122:    */     }
/* 123:306 */     if (getCapabilities().handles(Capabilities.Capability.NUMERIC_ATTRIBUTES))
/* 124:    */     {
/* 125:307 */       boolean onlyNumeric = true;
/* 126:308 */       if (!this.m_checksTurnedOff) {
/* 127:309 */         for (int i = 0; i < insts.numAttributes(); i++) {
/* 128:310 */           if ((i != insts.classIndex()) && 
/* 129:311 */             (!insts.attribute(i).isNumeric()))
/* 130:    */           {
/* 131:312 */             onlyNumeric = false;
/* 132:313 */             break;
/* 133:    */           }
/* 134:    */         }
/* 135:    */       }
/* 136:319 */       if (!onlyNumeric)
/* 137:    */       {
/* 138:320 */         this.m_NominalToBinary = new NominalToBinary();
/* 139:321 */         this.m_NominalToBinary.setInputFormat(insts);
/* 140:322 */         insts = Filter.useFilter(insts, this.m_NominalToBinary);
/* 141:    */       }
/* 142:    */       else
/* 143:    */       {
/* 144:324 */         this.m_NominalToBinary = null;
/* 145:    */       }
/* 146:    */     }
/* 147:    */     else
/* 148:    */     {
/* 149:327 */       this.m_NominalToBinary = null;
/* 150:    */     }
/* 151:330 */     if (this.m_filterType == 1)
/* 152:    */     {
/* 153:331 */       this.m_Filter = new Standardize();
/* 154:332 */       ((Standardize)this.m_Filter).setIgnoreClass(true);
/* 155:333 */       this.m_Filter.setInputFormat(insts);
/* 156:334 */       insts = Filter.useFilter(insts, this.m_Filter);
/* 157:    */     }
/* 158:335 */     else if (this.m_filterType == 0)
/* 159:    */     {
/* 160:336 */       this.m_Filter = new Normalize();
/* 161:337 */       ((Normalize)this.m_Filter).setIgnoreClass(true);
/* 162:338 */       this.m_Filter.setInputFormat(insts);
/* 163:339 */       insts = Filter.useFilter(insts, this.m_Filter);
/* 164:    */     }
/* 165:    */     else
/* 166:    */     {
/* 167:341 */       this.m_Filter = null;
/* 168:    */     }
/* 169:344 */     this.m_NumTrain = insts.numInstances();
/* 170:348 */     if (this.m_Filter != null)
/* 171:    */     {
/* 172:349 */       Instance witness = (Instance)insts.instance(0).copy();
/* 173:350 */       witness.setValue(insts.classIndex(), 0.0D);
/* 174:351 */       this.m_Filter.input(witness);
/* 175:352 */       this.m_Filter.batchFinished();
/* 176:353 */       Instance res = this.m_Filter.output();
/* 177:354 */       this.m_Blin = res.value(insts.classIndex());
/* 178:355 */       witness.setValue(insts.classIndex(), 1.0D);
/* 179:356 */       this.m_Filter.input(witness);
/* 180:357 */       this.m_Filter.batchFinished();
/* 181:358 */       res = this.m_Filter.output();
/* 182:359 */       this.m_Alin = (res.value(insts.classIndex()) - this.m_Blin);
/* 183:    */     }
/* 184:    */     else
/* 185:    */     {
/* 186:361 */       this.m_Alin = 1.0D;
/* 187:362 */       this.m_Blin = 0.0D;
/* 188:    */     }
/* 189:366 */     this.m_actualKernel = Kernel.makeCopy(this.m_kernel);
/* 190:367 */     if ((this.m_kernel instanceof CachedKernel)) {
/* 191:368 */       ((CachedKernel)this.m_actualKernel).setCacheSize(-1);
/* 192:    */     }
/* 193:370 */     this.m_actualKernel.buildKernel(insts);
/* 194:    */     
/* 195:    */ 
/* 196:373 */     double sum = 0.0D;
/* 197:374 */     for (int i = 0; i < insts.numInstances(); i++) {
/* 198:375 */       sum += insts.instance(i).weight() * insts.instance(i).classValue();
/* 199:    */     }
/* 200:377 */     this.m_avg_target = (sum / insts.sumOfWeights());
/* 201:    */     
/* 202:    */ 
/* 203:380 */     this.m_deltaSquared = (this.m_delta * this.m_delta);
/* 204:    */     
/* 205:    */ 
/* 206:383 */     this.m_weights = new double[insts.numInstances()];
/* 207:384 */     for (int i = 0; i < insts.numInstances(); i++) {
/* 208:385 */       this.m_weights[i] = Math.sqrt(insts.instance(i).weight());
/* 209:    */     }
/* 210:389 */     int n = insts.numInstances();
/* 211:390 */     this.m_L = new UpperSPDDenseMatrix(n);
/* 212:391 */     for (int i = 0; i < n; i++)
/* 213:    */     {
/* 214:392 */       for (int j = i + 1; j < n; j++) {
/* 215:393 */         this.m_L.set(i, j, this.m_weights[i] * this.m_weights[j] * this.m_actualKernel.eval(i, j, insts.instance(i)));
/* 216:    */       }
/* 217:395 */       this.m_L.set(i, i, this.m_weights[i] * this.m_weights[i] * this.m_actualKernel.eval(i, i, insts.instance(i)) + this.m_deltaSquared);
/* 218:    */     }
/* 219:399 */     this.m_L = new DenseCholesky(n, true).factor((UpperSPDDenseMatrix)this.m_L).solve(Matrices.identity(n));
/* 220:400 */     this.m_L = new UpperSPDDenseMatrix(this.m_L);
/* 221:    */     
/* 222:    */ 
/* 223:403 */     no.uib.cipr.matrix.Vector tt = new DenseVector(n);
/* 224:404 */     for (int i = 0; i < n; i++) {
/* 225:405 */       tt.set(i, this.m_weights[i] * (insts.instance(i).classValue() - this.m_avg_target));
/* 226:    */     }
/* 227:407 */     this.m_t = this.m_L.mult(tt, new DenseVector(insts.numInstances()));
/* 228:    */   }
/* 229:    */   
/* 230:    */   public double classifyInstance(Instance inst)
/* 231:    */     throws Exception
/* 232:    */   {
/* 233:422 */     inst = filterInstance(inst);
/* 234:    */     
/* 235:    */ 
/* 236:425 */     no.uib.cipr.matrix.Vector k = new DenseVector(this.m_NumTrain);
/* 237:426 */     for (int i = 0; i < this.m_NumTrain; i++) {
/* 238:427 */       k.set(i, this.m_weights[i] * this.m_actualKernel.eval(-1, i, inst));
/* 239:    */     }
/* 240:430 */     double result = (k.dot(this.m_t) + this.m_avg_target - this.m_Blin) / this.m_Alin;
/* 241:    */     
/* 242:432 */     return result;
/* 243:    */   }
/* 244:    */   
/* 245:    */   protected Instance filterInstance(Instance inst)
/* 246:    */     throws Exception
/* 247:    */   {
/* 248:441 */     if (!this.m_checksTurnedOff)
/* 249:    */     {
/* 250:442 */       this.m_Missing.input(inst);
/* 251:443 */       this.m_Missing.batchFinished();
/* 252:444 */       inst = this.m_Missing.output();
/* 253:    */     }
/* 254:447 */     if (this.m_NominalToBinary != null)
/* 255:    */     {
/* 256:448 */       this.m_NominalToBinary.input(inst);
/* 257:449 */       this.m_NominalToBinary.batchFinished();
/* 258:450 */       inst = this.m_NominalToBinary.output();
/* 259:    */     }
/* 260:453 */     if (this.m_Filter != null)
/* 261:    */     {
/* 262:454 */       this.m_Filter.input(inst);
/* 263:455 */       this.m_Filter.batchFinished();
/* 264:456 */       inst = this.m_Filter.output();
/* 265:    */     }
/* 266:458 */     return inst;
/* 267:    */   }
/* 268:    */   
/* 269:    */   protected double computeStdDev(Instance inst, no.uib.cipr.matrix.Vector k)
/* 270:    */     throws Exception
/* 271:    */   {
/* 272:467 */     double kappa = this.m_actualKernel.eval(-1, -1, inst) + this.m_deltaSquared;
/* 273:    */     
/* 274:469 */     double s = this.m_L.mult(k, new DenseVector(k.size())).dot(k);
/* 275:    */     
/* 276:471 */     double sigma = this.m_delta;
/* 277:472 */     if (kappa > s) {
/* 278:473 */       sigma = Math.sqrt(kappa - s);
/* 279:    */     }
/* 280:476 */     return sigma;
/* 281:    */   }
/* 282:    */   
/* 283:    */   public double[][] predictIntervals(Instance inst, double confidenceLevel)
/* 284:    */     throws Exception
/* 285:    */   {
/* 286:491 */     inst = filterInstance(inst);
/* 287:    */     
/* 288:    */ 
/* 289:494 */     no.uib.cipr.matrix.Vector k = new DenseVector(this.m_NumTrain);
/* 290:495 */     for (int i = 0; i < this.m_NumTrain; i++) {
/* 291:496 */       k.set(i, this.m_weights[i] * this.m_actualKernel.eval(-1, i, inst));
/* 292:    */     }
/* 293:499 */     double estimate = k.dot(this.m_t) + this.m_avg_target;
/* 294:    */     
/* 295:501 */     double sigma = computeStdDev(inst, k);
/* 296:    */     
/* 297:503 */     confidenceLevel = 1.0D - (1.0D - confidenceLevel) / 2.0D;
/* 298:    */     
/* 299:505 */     double z = Statistics.normalInverse(confidenceLevel);
/* 300:    */     
/* 301:507 */     double[][] interval = new double[1][2];
/* 302:    */     
/* 303:509 */     interval[0][0] = (estimate - z * sigma);
/* 304:510 */     interval[0][1] = (estimate + z * sigma);
/* 305:    */     
/* 306:512 */     interval[0][0] = ((interval[0][0] - this.m_Blin) / this.m_Alin);
/* 307:513 */     interval[0][1] = ((interval[0][1] - this.m_Blin) / this.m_Alin);
/* 308:    */     
/* 309:515 */     return interval;
/* 310:    */   }
/* 311:    */   
/* 312:    */   public double getStandardDeviation(Instance inst)
/* 313:    */     throws Exception
/* 314:    */   {
/* 315:528 */     inst = filterInstance(inst);
/* 316:    */     
/* 317:    */ 
/* 318:531 */     no.uib.cipr.matrix.Vector k = new DenseVector(this.m_NumTrain);
/* 319:532 */     for (int i = 0; i < this.m_NumTrain; i++) {
/* 320:533 */       k.set(i, this.m_weights[i] * this.m_actualKernel.eval(-1, i, inst));
/* 321:    */     }
/* 322:536 */     return computeStdDev(inst, k) / this.m_Alin;
/* 323:    */   }
/* 324:    */   
/* 325:    */   public double logDensity(Instance inst, double value)
/* 326:    */     throws Exception
/* 327:    */   {
/* 328:551 */     inst = filterInstance(inst);
/* 329:    */     
/* 330:    */ 
/* 331:554 */     no.uib.cipr.matrix.Vector k = new DenseVector(this.m_NumTrain);
/* 332:555 */     for (int i = 0; i < this.m_NumTrain; i++) {
/* 333:556 */       k.set(i, this.m_weights[i] * this.m_actualKernel.eval(-1, i, inst));
/* 334:    */     }
/* 335:559 */     double estimate = k.dot(this.m_t) + this.m_avg_target;
/* 336:    */     
/* 337:561 */     double sigma = computeStdDev(inst, k);
/* 338:    */     
/* 339:    */ 
/* 340:564 */     value = value * this.m_Alin + this.m_Blin;
/* 341:    */     
/* 342:566 */     value -= estimate;
/* 343:567 */     double z = -Math.log(sigma * Math.sqrt(6.283185307179586D)) - value * value / (2.0D * sigma * sigma);
/* 344:    */     
/* 345:    */ 
/* 346:570 */     return z + Math.log(this.m_Alin);
/* 347:    */   }
/* 348:    */   
/* 349:    */   public Enumeration<Option> listOptions()
/* 350:    */   {
/* 351:581 */     java.util.Vector<Option> result = new java.util.Vector();
/* 352:    */     
/* 353:583 */     result.addElement(new Option("\tLevel of Gaussian Noise wrt transformed target. (default 1)", "L", 1, "-L <double>"));
/* 354:    */     
/* 355:    */ 
/* 356:    */ 
/* 357:587 */     result.addElement(new Option("\tWhether to 0=normalize/1=standardize/2=neither. (default 0=normalize)", "N", 1, "-N"));
/* 358:    */     
/* 359:    */ 
/* 360:    */ 
/* 361:591 */     result.addElement(new Option("\tThe Kernel to use.\n\t(default: weka.classifiers.functions.supportVector.PolyKernel)", "K", 1, "-K <classname and parameters>"));
/* 362:    */     
/* 363:    */ 
/* 364:    */ 
/* 365:595 */     result.addAll(Collections.list(super.listOptions()));
/* 366:    */     
/* 367:597 */     result.addElement(new Option("", "", 0, "\nOptions specific to kernel " + getKernel().getClass().getName() + ":"));
/* 368:    */     
/* 369:    */ 
/* 370:600 */     result.addAll(Collections.list(getKernel().listOptions()));
/* 371:    */     
/* 372:    */ 
/* 373:603 */     return result.elements();
/* 374:    */   }
/* 375:    */   
/* 376:    */   public void setOptions(String[] options)
/* 377:    */     throws Exception
/* 378:    */   {
/* 379:673 */     String tmpStr = Utils.getOption('L', options);
/* 380:674 */     if (tmpStr.length() != 0) {
/* 381:675 */       setNoise(Double.parseDouble(tmpStr));
/* 382:    */     } else {
/* 383:677 */       setNoise(1.0D);
/* 384:    */     }
/* 385:680 */     tmpStr = Utils.getOption('N', options);
/* 386:681 */     if (tmpStr.length() != 0) {
/* 387:682 */       setFilterType(new SelectedTag(Integer.parseInt(tmpStr), TAGS_FILTER));
/* 388:    */     } else {
/* 389:684 */       setFilterType(new SelectedTag(0, TAGS_FILTER));
/* 390:    */     }
/* 391:687 */     tmpStr = Utils.getOption('K', options);
/* 392:688 */     String[] tmpOptions = Utils.splitOptions(tmpStr);
/* 393:689 */     if (tmpOptions.length != 0)
/* 394:    */     {
/* 395:690 */       tmpStr = tmpOptions[0];
/* 396:691 */       tmpOptions[0] = "";
/* 397:692 */       setKernel(Kernel.forName(tmpStr, tmpOptions));
/* 398:    */     }
/* 399:695 */     super.setOptions(options);
/* 400:    */     
/* 401:697 */     Utils.checkForRemainingOptions(options);
/* 402:    */   }
/* 403:    */   
/* 404:    */   public String[] getOptions()
/* 405:    */   {
/* 406:708 */     java.util.Vector<String> result = new java.util.Vector();
/* 407:    */     
/* 408:710 */     result.addElement("-L");
/* 409:711 */     result.addElement("" + getNoise());
/* 410:    */     
/* 411:713 */     result.addElement("-N");
/* 412:714 */     result.addElement("" + this.m_filterType);
/* 413:    */     
/* 414:716 */     result.addElement("-K");
/* 415:717 */     result.addElement("" + this.m_kernel.getClass().getName() + " " + Utils.joinOptions(this.m_kernel.getOptions()));
/* 416:    */     
/* 417:    */ 
/* 418:720 */     Collections.addAll(result, super.getOptions());
/* 419:    */     
/* 420:722 */     return (String[])result.toArray(new String[result.size()]);
/* 421:    */   }
/* 422:    */   
/* 423:    */   public String kernelTipText()
/* 424:    */   {
/* 425:732 */     return "The kernel to use.";
/* 426:    */   }
/* 427:    */   
/* 428:    */   public Kernel getKernel()
/* 429:    */   {
/* 430:741 */     return this.m_kernel;
/* 431:    */   }
/* 432:    */   
/* 433:    */   public void setKernel(Kernel value)
/* 434:    */   {
/* 435:750 */     this.m_kernel = value;
/* 436:    */   }
/* 437:    */   
/* 438:    */   public String filterTypeTipText()
/* 439:    */   {
/* 440:760 */     return "Determines how/if the data will be transformed.";
/* 441:    */   }
/* 442:    */   
/* 443:    */   public SelectedTag getFilterType()
/* 444:    */   {
/* 445:771 */     return new SelectedTag(this.m_filterType, TAGS_FILTER);
/* 446:    */   }
/* 447:    */   
/* 448:    */   public void setFilterType(SelectedTag newType)
/* 449:    */   {
/* 450:782 */     if (newType.getTags() == TAGS_FILTER) {
/* 451:783 */       this.m_filterType = newType.getSelectedTag().getID();
/* 452:    */     }
/* 453:    */   }
/* 454:    */   
/* 455:    */   public String noiseTipText()
/* 456:    */   {
/* 457:794 */     return "The level of Gaussian Noise (added to the diagonal of the Covariance Matrix), after the target has been normalized/standardized/left unchanged).";
/* 458:    */   }
/* 459:    */   
/* 460:    */   public double getNoise()
/* 461:    */   {
/* 462:804 */     return this.m_delta;
/* 463:    */   }
/* 464:    */   
/* 465:    */   public void setNoise(double v)
/* 466:    */   {
/* 467:813 */     this.m_delta = v;
/* 468:    */   }
/* 469:    */   
/* 470:    */   public String toString()
/* 471:    */   {
/* 472:824 */     StringBuffer text = new StringBuffer();
/* 473:826 */     if (this.m_t == null) {
/* 474:827 */       return "Gaussian Processes: No model built yet.";
/* 475:    */     }
/* 476:    */     try
/* 477:    */     {
/* 478:832 */       text.append("Gaussian Processes\n\n");
/* 479:833 */       text.append("Kernel used:\n  " + this.m_kernel.toString() + "\n\n");
/* 480:    */       
/* 481:835 */       text.append("All values shown based on: " + TAGS_FILTER[this.m_filterType].getReadable() + "\n\n");
/* 482:    */       
/* 483:    */ 
/* 484:838 */       text.append("Average Target Value : " + this.m_avg_target + "\n");
/* 485:    */       
/* 486:840 */       text.append("Inverted Covariance Matrix:\n");
/* 487:841 */       double min = this.m_L.get(0, 0);
/* 488:842 */       double max = this.m_L.get(0, 0);
/* 489:843 */       for (int i = 0; i < this.m_NumTrain; i++) {
/* 490:844 */         for (int j = 0; j <= i; j++) {
/* 491:845 */           if (this.m_L.get(i, j) < min) {
/* 492:846 */             min = this.m_L.get(i, j);
/* 493:847 */           } else if (this.m_L.get(i, j) > max) {
/* 494:848 */             max = this.m_L.get(i, j);
/* 495:    */           }
/* 496:    */         }
/* 497:    */       }
/* 498:852 */       text.append("    Lowest Value = " + min + "\n");
/* 499:853 */       text.append("    Highest Value = " + max + "\n");
/* 500:854 */       text.append("Inverted Covariance Matrix * Target-value Vector:\n");
/* 501:855 */       min = this.m_t.get(0);
/* 502:856 */       max = this.m_t.get(0);
/* 503:857 */       for (int i = 0; i < this.m_NumTrain; i++) {
/* 504:858 */         if (this.m_t.get(i) < min) {
/* 505:859 */           min = this.m_t.get(i);
/* 506:860 */         } else if (this.m_t.get(i) > max) {
/* 507:861 */           max = this.m_t.get(i);
/* 508:    */         }
/* 509:    */       }
/* 510:864 */       text.append("    Lowest Value = " + min + "\n");
/* 511:865 */       text.append("    Highest Value = " + max + "\n \n");
/* 512:    */     }
/* 513:    */     catch (Exception e)
/* 514:    */     {
/* 515:868 */       return "Can't print the classifier.";
/* 516:    */     }
/* 517:871 */     return text.toString();
/* 518:    */   }
/* 519:    */   
/* 520:    */   public static void main(String[] argv)
/* 521:    */   {
/* 522:881 */     runClassifier(new GaussianProcesses(), argv);
/* 523:    */   }
/* 524:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.GaussianProcesses
 * JD-Core Version:    0.7.0.1
 */