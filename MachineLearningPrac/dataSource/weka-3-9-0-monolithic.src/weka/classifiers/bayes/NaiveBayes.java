/*    1:     */ package weka.classifiers.bayes;
/*    2:     */ 
/*    3:     */ import java.util.Collections;
/*    4:     */ import java.util.Enumeration;
/*    5:     */ import java.util.Vector;
/*    6:     */ import weka.classifiers.AbstractClassifier;
/*    7:     */ import weka.core.Aggregateable;
/*    8:     */ import weka.core.Attribute;
/*    9:     */ import weka.core.Capabilities;
/*   10:     */ import weka.core.Capabilities.Capability;
/*   11:     */ import weka.core.Instance;
/*   12:     */ import weka.core.Instances;
/*   13:     */ import weka.core.Option;
/*   14:     */ import weka.core.OptionHandler;
/*   15:     */ import weka.core.RevisionUtils;
/*   16:     */ import weka.core.TechnicalInformation;
/*   17:     */ import weka.core.TechnicalInformation.Field;
/*   18:     */ import weka.core.TechnicalInformation.Type;
/*   19:     */ import weka.core.TechnicalInformationHandler;
/*   20:     */ import weka.core.Utils;
/*   21:     */ import weka.core.WeightedInstancesHandler;
/*   22:     */ import weka.estimators.DiscreteEstimator;
/*   23:     */ import weka.estimators.Estimator;
/*   24:     */ import weka.estimators.KernelEstimator;
/*   25:     */ import weka.estimators.NormalEstimator;
/*   26:     */ import weka.filters.Filter;
/*   27:     */ import weka.filters.supervised.attribute.Discretize;
/*   28:     */ 
/*   29:     */ public class NaiveBayes
/*   30:     */   extends AbstractClassifier
/*   31:     */   implements OptionHandler, WeightedInstancesHandler, TechnicalInformationHandler, Aggregateable<NaiveBayes>
/*   32:     */ {
/*   33:     */   static final long serialVersionUID = 5995231201785697655L;
/*   34:     */   protected Estimator[][] m_Distributions;
/*   35:     */   protected Estimator m_ClassDistribution;
/*   36: 125 */   protected boolean m_UseKernelEstimator = false;
/*   37: 131 */   protected boolean m_UseDiscretization = false;
/*   38:     */   protected int m_NumClasses;
/*   39:     */   protected Instances m_Instances;
/*   40:     */   protected static final double DEFAULT_NUM_PRECISION = 0.01D;
/*   41: 148 */   protected Discretize m_Disc = null;
/*   42: 150 */   protected boolean m_displayModelInOldFormat = false;
/*   43:     */   
/*   44:     */   public String globalInfo()
/*   45:     */   {
/*   46: 159 */     return "Class for a Naive Bayes classifier using estimator classes. Numeric estimator precision values are chosen based on analysis of the  training data. For this reason, the classifier is not an UpdateableClassifier (which in typical usage are initialized with zero training instances) -- if you need the UpdateableClassifier functionality, use the NaiveBayesUpdateable classifier. The NaiveBayesUpdateable classifier will  use a default precision of 0.1 for numeric attributes when buildClassifier is called with zero training instances.\n\nFor more information on Naive Bayes classifiers, see\n\n" + getTechnicalInformation().toString();
/*   47:     */   }
/*   48:     */   
/*   49:     */   public TechnicalInformation getTechnicalInformation()
/*   50:     */   {
/*   51: 182 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*   52: 183 */     result.setValue(TechnicalInformation.Field.AUTHOR, "George H. John and Pat Langley");
/*   53: 184 */     result.setValue(TechnicalInformation.Field.TITLE, "Estimating Continuous Distributions in Bayesian Classifiers");
/*   54:     */     
/*   55: 186 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Eleventh Conference on Uncertainty in Artificial Intelligence");
/*   56:     */     
/*   57: 188 */     result.setValue(TechnicalInformation.Field.YEAR, "1995");
/*   58: 189 */     result.setValue(TechnicalInformation.Field.PAGES, "338-345");
/*   59: 190 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann");
/*   60: 191 */     result.setValue(TechnicalInformation.Field.ADDRESS, "San Mateo");
/*   61:     */     
/*   62: 193 */     return result;
/*   63:     */   }
/*   64:     */   
/*   65:     */   public Capabilities getCapabilities()
/*   66:     */   {
/*   67: 203 */     Capabilities result = super.getCapabilities();
/*   68: 204 */     result.disableAll();
/*   69:     */     
/*   70:     */ 
/*   71: 207 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*   72: 208 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*   73: 209 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*   74:     */     
/*   75:     */ 
/*   76: 212 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*   77: 213 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*   78:     */     
/*   79:     */ 
/*   80: 216 */     result.setMinimumNumberInstances(0);
/*   81:     */     
/*   82: 218 */     return result;
/*   83:     */   }
/*   84:     */   
/*   85:     */   public void buildClassifier(Instances instances)
/*   86:     */     throws Exception
/*   87:     */   {
/*   88: 231 */     getCapabilities().testWithFail(instances);
/*   89:     */     
/*   90:     */ 
/*   91: 234 */     instances = new Instances(instances);
/*   92: 235 */     instances.deleteWithMissingClass();
/*   93:     */     
/*   94: 237 */     this.m_NumClasses = instances.numClasses();
/*   95:     */     
/*   96:     */ 
/*   97: 240 */     this.m_Instances = new Instances(instances);
/*   98: 243 */     if (this.m_UseDiscretization)
/*   99:     */     {
/*  100: 244 */       this.m_Disc = new Discretize();
/*  101: 245 */       this.m_Disc.setInputFormat(this.m_Instances);
/*  102: 246 */       this.m_Instances = Filter.useFilter(this.m_Instances, this.m_Disc);
/*  103:     */     }
/*  104:     */     else
/*  105:     */     {
/*  106: 248 */       this.m_Disc = null;
/*  107:     */     }
/*  108: 252 */     this.m_Distributions = new Estimator[this.m_Instances.numAttributes() - 1][this.m_Instances.numClasses()];
/*  109:     */     
/*  110: 254 */     this.m_ClassDistribution = new DiscreteEstimator(this.m_Instances.numClasses(), true);
/*  111: 255 */     int attIndex = 0;
/*  112: 256 */     Enumeration<Attribute> enu = this.m_Instances.enumerateAttributes();
/*  113: 257 */     while (enu.hasMoreElements())
/*  114:     */     {
/*  115: 258 */       Attribute attribute = (Attribute)enu.nextElement();
/*  116:     */       
/*  117:     */ 
/*  118:     */ 
/*  119: 262 */       double numPrecision = 0.01D;
/*  120: 263 */       if (attribute.type() == 0)
/*  121:     */       {
/*  122: 264 */         this.m_Instances.sort(attribute);
/*  123: 265 */         if ((this.m_Instances.numInstances() > 0) && (!this.m_Instances.instance(0).isMissing(attribute)))
/*  124:     */         {
/*  125: 267 */           double lastVal = this.m_Instances.instance(0).value(attribute);
/*  126: 268 */           double deltaSum = 0.0D;
/*  127: 269 */           int distinct = 0;
/*  128: 270 */           for (int i = 1; i < this.m_Instances.numInstances(); i++)
/*  129:     */           {
/*  130: 271 */             Instance currentInst = this.m_Instances.instance(i);
/*  131: 272 */             if (currentInst.isMissing(attribute)) {
/*  132:     */               break;
/*  133:     */             }
/*  134: 275 */             double currentVal = currentInst.value(attribute);
/*  135: 276 */             if (currentVal != lastVal)
/*  136:     */             {
/*  137: 277 */               deltaSum += currentVal - lastVal;
/*  138: 278 */               lastVal = currentVal;
/*  139: 279 */               distinct++;
/*  140:     */             }
/*  141:     */           }
/*  142: 282 */           if (distinct > 0) {
/*  143: 283 */             numPrecision = deltaSum / distinct;
/*  144:     */           }
/*  145:     */         }
/*  146:     */       }
/*  147: 288 */       for (int j = 0; j < this.m_Instances.numClasses(); j++) {
/*  148: 289 */         switch (attribute.type())
/*  149:     */         {
/*  150:     */         case 0: 
/*  151: 291 */           if (this.m_UseKernelEstimator) {
/*  152: 292 */             this.m_Distributions[attIndex][j] = new KernelEstimator(numPrecision);
/*  153:     */           } else {
/*  154: 294 */             this.m_Distributions[attIndex][j] = new NormalEstimator(numPrecision);
/*  155:     */           }
/*  156: 296 */           break;
/*  157:     */         case 1: 
/*  158: 298 */           this.m_Distributions[attIndex][j] = new DiscreteEstimator(attribute.numValues(), true);
/*  159:     */           
/*  160: 300 */           break;
/*  161:     */         default: 
/*  162: 302 */           throw new Exception("Attribute type unknown to NaiveBayes");
/*  163:     */         }
/*  164:     */       }
/*  165: 305 */       attIndex++;
/*  166:     */     }
/*  167: 309 */     Enumeration<Instance> enumInsts = this.m_Instances.enumerateInstances();
/*  168: 310 */     while (enumInsts.hasMoreElements())
/*  169:     */     {
/*  170: 311 */       Instance instance = (Instance)enumInsts.nextElement();
/*  171: 312 */       updateClassifier(instance);
/*  172:     */     }
/*  173: 316 */     this.m_Instances = new Instances(this.m_Instances, 0);
/*  174:     */   }
/*  175:     */   
/*  176:     */   public void updateClassifier(Instance instance)
/*  177:     */     throws Exception
/*  178:     */   {
/*  179: 328 */     if (!instance.classIsMissing())
/*  180:     */     {
/*  181: 329 */       Enumeration<Attribute> enumAtts = this.m_Instances.enumerateAttributes();
/*  182: 330 */       int attIndex = 0;
/*  183: 331 */       while (enumAtts.hasMoreElements())
/*  184:     */       {
/*  185: 332 */         Attribute attribute = (Attribute)enumAtts.nextElement();
/*  186: 333 */         if (!instance.isMissing(attribute)) {
/*  187: 334 */           this.m_Distributions[attIndex][((int)instance.classValue())].addValue(instance.value(attribute), instance.weight());
/*  188:     */         }
/*  189: 337 */         attIndex++;
/*  190:     */       }
/*  191: 339 */       this.m_ClassDistribution.addValue(instance.classValue(), instance.weight());
/*  192:     */     }
/*  193:     */   }
/*  194:     */   
/*  195:     */   public double[] distributionForInstance(Instance instance)
/*  196:     */     throws Exception
/*  197:     */   {
/*  198: 353 */     if (this.m_UseDiscretization)
/*  199:     */     {
/*  200: 354 */       this.m_Disc.input(instance);
/*  201: 355 */       instance = this.m_Disc.output();
/*  202:     */     }
/*  203: 357 */     double[] probs = new double[this.m_NumClasses];
/*  204: 358 */     for (int j = 0; j < this.m_NumClasses; j++) {
/*  205: 359 */       probs[j] = this.m_ClassDistribution.getProbability(j);
/*  206:     */     }
/*  207: 361 */     Enumeration<Attribute> enumAtts = instance.enumerateAttributes();
/*  208: 362 */     int attIndex = 0;
/*  209: 363 */     while (enumAtts.hasMoreElements())
/*  210:     */     {
/*  211: 364 */       Attribute attribute = (Attribute)enumAtts.nextElement();
/*  212: 365 */       if (!instance.isMissing(attribute))
/*  213:     */       {
/*  214: 366 */         double max = 0.0D;
/*  215: 367 */         for (int j = 0; j < this.m_NumClasses; j++)
/*  216:     */         {
/*  217: 368 */           double temp = Math.max(1.0E-075D, Math.pow(this.m_Distributions[attIndex][j].getProbability(instance.value(attribute)), this.m_Instances.attribute(attIndex).weight()));
/*  218:     */           
/*  219:     */ 
/*  220: 371 */           probs[j] *= temp;
/*  221: 372 */           if (probs[j] > max) {
/*  222: 373 */             max = probs[j];
/*  223:     */           }
/*  224: 375 */           if (Double.isNaN(probs[j])) {
/*  225: 376 */             throw new Exception("NaN returned from estimator for attribute " + attribute.name() + ":\n" + this.m_Distributions[attIndex][j].toString());
/*  226:     */           }
/*  227:     */         }
/*  228: 381 */         if ((max > 0.0D) && (max < 1.0E-075D)) {
/*  229: 382 */           for (int j = 0; j < this.m_NumClasses; j++) {
/*  230: 383 */             probs[j] *= 9.999999999999999E+074D;
/*  231:     */           }
/*  232:     */         }
/*  233:     */       }
/*  234: 387 */       attIndex++;
/*  235:     */     }
/*  236: 391 */     Utils.normalize(probs);
/*  237: 392 */     return probs;
/*  238:     */   }
/*  239:     */   
/*  240:     */   public Enumeration<Option> listOptions()
/*  241:     */   {
/*  242: 403 */     Vector<Option> newVector = new Vector(3);
/*  243:     */     
/*  244: 405 */     newVector.addElement(new Option("\tUse kernel density estimator rather than normal\n\tdistribution for numeric attributes", "K", 0, "-K"));
/*  245:     */     
/*  246:     */ 
/*  247: 408 */     newVector.addElement(new Option("\tUse supervised discretization to process numeric attributes\n", "D", 0, "-D"));
/*  248:     */     
/*  249:     */ 
/*  250:     */ 
/*  251: 412 */     newVector.addElement(new Option("\tDisplay model in old format (good when there are many classes)\n", "O", 0, "-O"));
/*  252:     */     
/*  253:     */ 
/*  254:     */ 
/*  255:     */ 
/*  256: 417 */     newVector.addAll(Collections.list(super.listOptions()));
/*  257:     */     
/*  258: 419 */     return newVector.elements();
/*  259:     */   }
/*  260:     */   
/*  261:     */   public void setOptions(String[] options)
/*  262:     */     throws Exception
/*  263:     */   {
/*  264: 453 */     super.setOptions(options);
/*  265: 454 */     boolean k = Utils.getFlag('K', options);
/*  266: 455 */     boolean d = Utils.getFlag('D', options);
/*  267: 456 */     if ((k) && (d)) {
/*  268: 457 */       throw new IllegalArgumentException("Can't use both kernel density estimation and discretization!");
/*  269:     */     }
/*  270: 460 */     setUseSupervisedDiscretization(d);
/*  271: 461 */     setUseKernelEstimator(k);
/*  272: 462 */     setDisplayModelInOldFormat(Utils.getFlag('O', options));
/*  273: 463 */     Utils.checkForRemainingOptions(options);
/*  274:     */   }
/*  275:     */   
/*  276:     */   public String[] getOptions()
/*  277:     */   {
/*  278: 474 */     Vector<String> options = new Vector();
/*  279:     */     
/*  280: 476 */     Collections.addAll(options, super.getOptions());
/*  281: 478 */     if (this.m_UseKernelEstimator) {
/*  282: 479 */       options.add("-K");
/*  283:     */     }
/*  284: 482 */     if (this.m_UseDiscretization) {
/*  285: 483 */       options.add("-D");
/*  286:     */     }
/*  287: 486 */     if (this.m_displayModelInOldFormat) {
/*  288: 487 */       options.add("-O");
/*  289:     */     }
/*  290: 490 */     return (String[])options.toArray(new String[0]);
/*  291:     */   }
/*  292:     */   
/*  293:     */   public String toString()
/*  294:     */   {
/*  295: 500 */     if (this.m_displayModelInOldFormat) {
/*  296: 501 */       return toStringOriginal();
/*  297:     */     }
/*  298: 504 */     StringBuffer temp = new StringBuffer();
/*  299: 505 */     temp.append("Naive Bayes Classifier");
/*  300: 506 */     if (this.m_Instances == null)
/*  301:     */     {
/*  302: 507 */       temp.append(": No model built yet.");
/*  303:     */     }
/*  304:     */     else
/*  305:     */     {
/*  306: 510 */       int maxWidth = 0;
/*  307: 511 */       int maxAttWidth = 0;
/*  308: 512 */       boolean containsKernel = false;
/*  309: 516 */       for (int i = 0; i < this.m_Instances.numClasses(); i++) {
/*  310: 517 */         if (this.m_Instances.classAttribute().value(i).length() > maxWidth) {
/*  311: 518 */           maxWidth = this.m_Instances.classAttribute().value(i).length();
/*  312:     */         }
/*  313:     */       }
/*  314: 522 */       for (int i = 0; i < this.m_Instances.numAttributes(); i++) {
/*  315: 523 */         if (i != this.m_Instances.classIndex())
/*  316:     */         {
/*  317: 524 */           Attribute a = this.m_Instances.attribute(i);
/*  318: 525 */           if (a.name().length() > maxAttWidth) {
/*  319: 526 */             maxAttWidth = this.m_Instances.attribute(i).name().length();
/*  320:     */           }
/*  321: 528 */           if (a.isNominal()) {
/*  322: 530 */             for (int j = 0; j < a.numValues(); j++)
/*  323:     */             {
/*  324: 531 */               String val = a.value(j) + "  ";
/*  325: 532 */               if (val.length() > maxAttWidth) {
/*  326: 533 */                 maxAttWidth = val.length();
/*  327:     */               }
/*  328:     */             }
/*  329:     */           }
/*  330:     */         }
/*  331:     */       }
/*  332: 540 */       for (Estimator[] m_Distribution : this.m_Distributions) {
/*  333: 541 */         for (int j = 0; j < this.m_Instances.numClasses(); j++) {
/*  334: 542 */           if ((m_Distribution[0] instanceof NormalEstimator))
/*  335:     */           {
/*  336: 544 */             NormalEstimator n = (NormalEstimator)m_Distribution[j];
/*  337: 545 */             double mean = Math.log(Math.abs(n.getMean())) / Math.log(10.0D);
/*  338: 546 */             double precision = Math.log(Math.abs(n.getPrecision())) / Math.log(10.0D);
/*  339:     */             
/*  340: 548 */             double width = mean > precision ? mean : precision;
/*  341: 549 */             if (width < 0.0D) {
/*  342: 550 */               width = 1.0D;
/*  343:     */             }
/*  344: 553 */             width += 6.0D;
/*  345: 554 */             if ((int)width > maxWidth) {
/*  346: 555 */               maxWidth = (int)width;
/*  347:     */             }
/*  348:     */           }
/*  349: 557 */           else if ((m_Distribution[0] instanceof KernelEstimator))
/*  350:     */           {
/*  351: 558 */             containsKernel = true;
/*  352: 559 */             KernelEstimator ke = (KernelEstimator)m_Distribution[j];
/*  353: 560 */             int numK = ke.getNumKernels();
/*  354: 561 */             String temps = "K" + numK + ": mean (weight)";
/*  355: 562 */             if (maxAttWidth < temps.length()) {
/*  356: 563 */               maxAttWidth = temps.length();
/*  357:     */             }
/*  358: 566 */             if (ke.getNumKernels() > 0)
/*  359:     */             {
/*  360: 567 */               double[] means = ke.getMeans();
/*  361: 568 */               double[] weights = ke.getWeights();
/*  362: 569 */               for (int k = 0; k < ke.getNumKernels(); k++)
/*  363:     */               {
/*  364: 570 */                 String m = Utils.doubleToString(means[k], maxWidth, 4).trim();
/*  365: 571 */                 m = m + " (" + Utils.doubleToString(weights[k], maxWidth, 1).trim() + ")";
/*  366: 573 */                 if (maxWidth < m.length()) {
/*  367: 574 */                   maxWidth = m.length();
/*  368:     */                 }
/*  369:     */               }
/*  370:     */             }
/*  371:     */           }
/*  372: 578 */           else if ((m_Distribution[0] instanceof DiscreteEstimator))
/*  373:     */           {
/*  374: 579 */             DiscreteEstimator d = (DiscreteEstimator)m_Distribution[j];
/*  375: 580 */             for (int k = 0; k < d.getNumSymbols(); k++)
/*  376:     */             {
/*  377: 581 */               String size = "" + d.getCount(k);
/*  378: 582 */               if (size.length() > maxWidth) {
/*  379: 583 */                 maxWidth = size.length();
/*  380:     */               }
/*  381:     */             }
/*  382: 586 */             int sum = ("" + d.getSumOfCounts()).length();
/*  383: 587 */             if (sum > maxWidth) {
/*  384: 588 */               maxWidth = sum;
/*  385:     */             }
/*  386:     */           }
/*  387:     */         }
/*  388:     */       }
/*  389: 595 */       for (int i = 0; i < this.m_Instances.numClasses(); i++)
/*  390:     */       {
/*  391: 596 */         String cSize = this.m_Instances.classAttribute().value(i);
/*  392: 597 */         if (cSize.length() > maxWidth) {
/*  393: 598 */           maxWidth = cSize.length();
/*  394:     */         }
/*  395:     */       }
/*  396: 603 */       for (int i = 0; i < this.m_Instances.numClasses(); i++)
/*  397:     */       {
/*  398: 604 */         String priorP = Utils.doubleToString(((DiscreteEstimator)this.m_ClassDistribution).getProbability(i), maxWidth, 2).trim();
/*  399:     */         
/*  400:     */ 
/*  401: 607 */         priorP = "(" + priorP + ")";
/*  402: 608 */         if (priorP.length() > maxWidth) {
/*  403: 609 */           maxWidth = priorP.length();
/*  404:     */         }
/*  405:     */       }
/*  406: 613 */       if (maxAttWidth < "Attribute".length()) {
/*  407: 614 */         maxAttWidth = "Attribute".length();
/*  408:     */       }
/*  409: 617 */       if (maxAttWidth < "  weight sum".length()) {
/*  410: 618 */         maxAttWidth = "  weight sum".length();
/*  411:     */       }
/*  412: 621 */       if ((containsKernel) && 
/*  413: 622 */         (maxAttWidth < "  [precision]".length())) {
/*  414: 623 */         maxAttWidth = "  [precision]".length();
/*  415:     */       }
/*  416: 627 */       maxAttWidth += 2;
/*  417:     */       
/*  418: 629 */       temp.append("\n\n");
/*  419: 630 */       temp.append(pad("Class", " ", maxAttWidth + maxWidth + 1 - "Class".length(), true));
/*  420:     */       
/*  421:     */ 
/*  422: 633 */       temp.append("\n");
/*  423: 634 */       temp.append(pad("Attribute", " ", maxAttWidth - "Attribute".length(), false));
/*  424: 637 */       for (int i = 0; i < this.m_Instances.numClasses(); i++)
/*  425:     */       {
/*  426: 638 */         String classL = this.m_Instances.classAttribute().value(i);
/*  427: 639 */         temp.append(pad(classL, " ", maxWidth + 1 - classL.length(), true));
/*  428:     */       }
/*  429: 641 */       temp.append("\n");
/*  430:     */       
/*  431: 643 */       temp.append(pad("", " ", maxAttWidth, true));
/*  432: 644 */       for (int i = 0; i < this.m_Instances.numClasses(); i++)
/*  433:     */       {
/*  434: 645 */         String priorP = Utils.doubleToString(((DiscreteEstimator)this.m_ClassDistribution).getProbability(i), maxWidth, 2).trim();
/*  435:     */         
/*  436:     */ 
/*  437: 648 */         priorP = "(" + priorP + ")";
/*  438: 649 */         temp.append(pad(priorP, " ", maxWidth + 1 - priorP.length(), true));
/*  439:     */       }
/*  440: 651 */       temp.append("\n");
/*  441: 652 */       temp.append(pad("", "=", maxAttWidth + maxWidth * this.m_Instances.numClasses() + this.m_Instances.numClasses() + 1, true));
/*  442:     */       
/*  443:     */ 
/*  444:     */ 
/*  445:     */ 
/*  446: 657 */       temp.append("\n");
/*  447:     */       
/*  448:     */ 
/*  449: 660 */       int counter = 0;
/*  450: 661 */       for (int i = 0; i < this.m_Instances.numAttributes(); i++) {
/*  451: 662 */         if (i != this.m_Instances.classIndex())
/*  452:     */         {
/*  453: 665 */           String attName = this.m_Instances.attribute(i).name();
/*  454: 666 */           temp.append(attName + "\n");
/*  455: 668 */           if ((this.m_Distributions[counter][0] instanceof NormalEstimator))
/*  456:     */           {
/*  457: 669 */             String meanL = "  mean";
/*  458: 670 */             temp.append(pad(meanL, " ", maxAttWidth + 1 - meanL.length(), false));
/*  459: 671 */             for (int j = 0; j < this.m_Instances.numClasses(); j++)
/*  460:     */             {
/*  461: 673 */               NormalEstimator n = (NormalEstimator)this.m_Distributions[counter][j];
/*  462: 674 */               String mean = Utils.doubleToString(n.getMean(), maxWidth, 4).trim();
/*  463: 675 */               temp.append(pad(mean, " ", maxWidth + 1 - mean.length(), true));
/*  464:     */             }
/*  465: 677 */             temp.append("\n");
/*  466:     */             
/*  467: 679 */             String stdDevL = "  std. dev.";
/*  468: 680 */             temp.append(pad(stdDevL, " ", maxAttWidth + 1 - stdDevL.length(), false));
/*  469: 682 */             for (int j = 0; j < this.m_Instances.numClasses(); j++)
/*  470:     */             {
/*  471: 683 */               NormalEstimator n = (NormalEstimator)this.m_Distributions[counter][j];
/*  472: 684 */               String stdDev = Utils.doubleToString(n.getStdDev(), maxWidth, 4).trim();
/*  473:     */               
/*  474: 686 */               temp.append(pad(stdDev, " ", maxWidth + 1 - stdDev.length(), true));
/*  475:     */             }
/*  476: 688 */             temp.append("\n");
/*  477:     */             
/*  478: 690 */             String weightL = "  weight sum";
/*  479: 691 */             temp.append(pad(weightL, " ", maxAttWidth + 1 - weightL.length(), false));
/*  480: 693 */             for (int j = 0; j < this.m_Instances.numClasses(); j++)
/*  481:     */             {
/*  482: 694 */               NormalEstimator n = (NormalEstimator)this.m_Distributions[counter][j];
/*  483: 695 */               String weight = Utils.doubleToString(n.getSumOfWeights(), maxWidth, 4).trim();
/*  484:     */               
/*  485: 697 */               temp.append(pad(weight, " ", maxWidth + 1 - weight.length(), true));
/*  486:     */             }
/*  487: 699 */             temp.append("\n");
/*  488:     */             
/*  489: 701 */             String precisionL = "  precision";
/*  490: 702 */             temp.append(pad(precisionL, " ", maxAttWidth + 1 - precisionL.length(), false));
/*  491: 704 */             for (int j = 0; j < this.m_Instances.numClasses(); j++)
/*  492:     */             {
/*  493: 705 */               NormalEstimator n = (NormalEstimator)this.m_Distributions[counter][j];
/*  494: 706 */               String precision = Utils.doubleToString(n.getPrecision(), maxWidth, 4).trim();
/*  495:     */               
/*  496: 708 */               temp.append(pad(precision, " ", maxWidth + 1 - precision.length(), true));
/*  497:     */             }
/*  498: 711 */             temp.append("\n\n");
/*  499:     */           }
/*  500: 713 */           else if ((this.m_Distributions[counter][0] instanceof DiscreteEstimator))
/*  501:     */           {
/*  502: 714 */             Attribute a = this.m_Instances.attribute(i);
/*  503: 715 */             for (int j = 0; j < a.numValues(); j++)
/*  504:     */             {
/*  505: 716 */               String val = "  " + a.value(j);
/*  506: 717 */               temp.append(pad(val, " ", maxAttWidth + 1 - val.length(), false));
/*  507: 718 */               for (int k = 0; k < this.m_Instances.numClasses(); k++)
/*  508:     */               {
/*  509: 719 */                 DiscreteEstimator d = (DiscreteEstimator)this.m_Distributions[counter][k];
/*  510: 720 */                 String count = "" + d.getCount(j);
/*  511: 721 */                 temp.append(pad(count, " ", maxWidth + 1 - count.length(), true));
/*  512:     */               }
/*  513: 723 */               temp.append("\n");
/*  514:     */             }
/*  515: 726 */             String total = "  [total]";
/*  516: 727 */             temp.append(pad(total, " ", maxAttWidth + 1 - total.length(), false));
/*  517: 728 */             for (int k = 0; k < this.m_Instances.numClasses(); k++)
/*  518:     */             {
/*  519: 729 */               DiscreteEstimator d = (DiscreteEstimator)this.m_Distributions[counter][k];
/*  520: 730 */               String count = "" + d.getSumOfCounts();
/*  521: 731 */               temp.append(pad(count, " ", maxWidth + 1 - count.length(), true));
/*  522:     */             }
/*  523: 733 */             temp.append("\n\n");
/*  524:     */           }
/*  525: 734 */           else if ((this.m_Distributions[counter][0] instanceof KernelEstimator))
/*  526:     */           {
/*  527: 735 */             String kL = "  [# kernels]";
/*  528: 736 */             temp.append(pad(kL, " ", maxAttWidth + 1 - kL.length(), false));
/*  529: 737 */             for (int k = 0; k < this.m_Instances.numClasses(); k++)
/*  530:     */             {
/*  531: 738 */               KernelEstimator ke = (KernelEstimator)this.m_Distributions[counter][k];
/*  532: 739 */               String nk = "" + ke.getNumKernels();
/*  533: 740 */               temp.append(pad(nk, " ", maxWidth + 1 - nk.length(), true));
/*  534:     */             }
/*  535: 742 */             temp.append("\n");
/*  536:     */             
/*  537: 744 */             String stdDevL = "  [std. dev]";
/*  538: 745 */             temp.append(pad(stdDevL, " ", maxAttWidth + 1 - stdDevL.length(), false));
/*  539: 747 */             for (int k = 0; k < this.m_Instances.numClasses(); k++)
/*  540:     */             {
/*  541: 748 */               KernelEstimator ke = (KernelEstimator)this.m_Distributions[counter][k];
/*  542: 749 */               String stdD = Utils.doubleToString(ke.getStdDev(), maxWidth, 4).trim();
/*  543:     */               
/*  544: 751 */               temp.append(pad(stdD, " ", maxWidth + 1 - stdD.length(), true));
/*  545:     */             }
/*  546: 753 */             temp.append("\n");
/*  547: 754 */             String precL = "  [precision]";
/*  548: 755 */             temp.append(pad(precL, " ", maxAttWidth + 1 - precL.length(), false));
/*  549: 756 */             for (int k = 0; k < this.m_Instances.numClasses(); k++)
/*  550:     */             {
/*  551: 757 */               KernelEstimator ke = (KernelEstimator)this.m_Distributions[counter][k];
/*  552: 758 */               String prec = Utils.doubleToString(ke.getPrecision(), maxWidth, 4).trim();
/*  553:     */               
/*  554: 760 */               temp.append(pad(prec, " ", maxWidth + 1 - prec.length(), true));
/*  555:     */             }
/*  556: 762 */             temp.append("\n");
/*  557:     */             
/*  558: 764 */             int maxK = 0;
/*  559: 765 */             for (int k = 0; k < this.m_Instances.numClasses(); k++)
/*  560:     */             {
/*  561: 766 */               KernelEstimator ke = (KernelEstimator)this.m_Distributions[counter][k];
/*  562: 767 */               if (ke.getNumKernels() > maxK) {
/*  563: 768 */                 maxK = ke.getNumKernels();
/*  564:     */               }
/*  565:     */             }
/*  566: 771 */             for (int j = 0; j < maxK; j++)
/*  567:     */             {
/*  568: 773 */               String meanL = "  K" + (j + 1) + ": mean (weight)";
/*  569: 774 */               temp.append(pad(meanL, " ", maxAttWidth + 1 - meanL.length(), false));
/*  570: 776 */               for (int k = 0; k < this.m_Instances.numClasses(); k++)
/*  571:     */               {
/*  572: 777 */                 KernelEstimator ke = (KernelEstimator)this.m_Distributions[counter][k];
/*  573: 778 */                 double[] means = ke.getMeans();
/*  574: 779 */                 double[] weights = ke.getWeights();
/*  575: 780 */                 String m = "--";
/*  576: 781 */                 if (ke.getNumKernels() == 0)
/*  577:     */                 {
/*  578: 782 */                   m = "0";
/*  579:     */                 }
/*  580: 783 */                 else if (j < ke.getNumKernels())
/*  581:     */                 {
/*  582: 784 */                   m = Utils.doubleToString(means[j], maxWidth, 4).trim();
/*  583: 785 */                   m = m + " (" + Utils.doubleToString(weights[j], maxWidth, 1).trim() + ")";
/*  584:     */                 }
/*  585: 788 */                 temp.append(pad(m, " ", maxWidth + 1 - m.length(), true));
/*  586:     */               }
/*  587: 790 */               temp.append("\n");
/*  588:     */             }
/*  589: 792 */             temp.append("\n");
/*  590:     */           }
/*  591: 795 */           counter++;
/*  592:     */         }
/*  593:     */       }
/*  594:     */     }
/*  595: 799 */     return temp.toString();
/*  596:     */   }
/*  597:     */   
/*  598:     */   protected String toStringOriginal()
/*  599:     */   {
/*  600: 809 */     StringBuffer text = new StringBuffer();
/*  601:     */     
/*  602: 811 */     text.append("Naive Bayes Classifier");
/*  603: 812 */     if (this.m_Instances == null) {
/*  604: 813 */       text.append(": No model built yet.");
/*  605:     */     } else {
/*  606:     */       try
/*  607:     */       {
/*  608: 816 */         for (int i = 0; i < this.m_Distributions[0].length; i++)
/*  609:     */         {
/*  610: 817 */           text.append("\n\nClass " + this.m_Instances.classAttribute().value(i) + ": Prior probability = " + Utils.doubleToString(this.m_ClassDistribution.getProbability(i), 4, 2) + "\n\n");
/*  611:     */           
/*  612:     */ 
/*  613:     */ 
/*  614: 821 */           Enumeration<Attribute> enumAtts = this.m_Instances.enumerateAttributes();
/*  615: 822 */           int attIndex = 0;
/*  616: 823 */           while (enumAtts.hasMoreElements())
/*  617:     */           {
/*  618: 824 */             Attribute attribute = (Attribute)enumAtts.nextElement();
/*  619: 825 */             if (attribute.weight() > 0.0D) {
/*  620: 826 */               text.append(attribute.name() + ":  " + this.m_Distributions[attIndex][i]);
/*  621:     */             }
/*  622: 829 */             attIndex++;
/*  623:     */           }
/*  624:     */         }
/*  625:     */       }
/*  626:     */       catch (Exception ex)
/*  627:     */       {
/*  628: 833 */         text.append(ex.getMessage());
/*  629:     */       }
/*  630:     */     }
/*  631: 837 */     return text.toString();
/*  632:     */   }
/*  633:     */   
/*  634:     */   private String pad(String source, String padChar, int length, boolean leftPad)
/*  635:     */   {
/*  636: 841 */     StringBuffer temp = new StringBuffer();
/*  637: 843 */     if (leftPad)
/*  638:     */     {
/*  639: 844 */       for (int i = 0; i < length; i++) {
/*  640: 845 */         temp.append(padChar);
/*  641:     */       }
/*  642: 847 */       temp.append(source);
/*  643:     */     }
/*  644:     */     else
/*  645:     */     {
/*  646: 849 */       temp.append(source);
/*  647: 850 */       for (int i = 0; i < length; i++) {
/*  648: 851 */         temp.append(padChar);
/*  649:     */       }
/*  650:     */     }
/*  651: 854 */     return temp.toString();
/*  652:     */   }
/*  653:     */   
/*  654:     */   public String useKernelEstimatorTipText()
/*  655:     */   {
/*  656: 864 */     return "Use a kernel estimator for numeric attributes rather than a normal distribution.";
/*  657:     */   }
/*  658:     */   
/*  659:     */   public boolean getUseKernelEstimator()
/*  660:     */   {
/*  661: 875 */     return this.m_UseKernelEstimator;
/*  662:     */   }
/*  663:     */   
/*  664:     */   public void setUseKernelEstimator(boolean v)
/*  665:     */   {
/*  666: 885 */     this.m_UseKernelEstimator = v;
/*  667: 886 */     if (v) {
/*  668: 887 */       setUseSupervisedDiscretization(false);
/*  669:     */     }
/*  670:     */   }
/*  671:     */   
/*  672:     */   public String useSupervisedDiscretizationTipText()
/*  673:     */   {
/*  674: 898 */     return "Use supervised discretization to convert numeric attributes to nominal ones.";
/*  675:     */   }
/*  676:     */   
/*  677:     */   public boolean getUseSupervisedDiscretization()
/*  678:     */   {
/*  679: 909 */     return this.m_UseDiscretization;
/*  680:     */   }
/*  681:     */   
/*  682:     */   public void setUseSupervisedDiscretization(boolean newblah)
/*  683:     */   {
/*  684: 919 */     this.m_UseDiscretization = newblah;
/*  685: 920 */     if (newblah) {
/*  686: 921 */       setUseKernelEstimator(false);
/*  687:     */     }
/*  688:     */   }
/*  689:     */   
/*  690:     */   public String displayModelInOldFormatTipText()
/*  691:     */   {
/*  692: 932 */     return "Use old format for model output. The old format is better when there are many class values. The new format is better when there are fewer classes and many attributes.";
/*  693:     */   }
/*  694:     */   
/*  695:     */   public void setDisplayModelInOldFormat(boolean d)
/*  696:     */   {
/*  697: 943 */     this.m_displayModelInOldFormat = d;
/*  698:     */   }
/*  699:     */   
/*  700:     */   public boolean getDisplayModelInOldFormat()
/*  701:     */   {
/*  702: 952 */     return this.m_displayModelInOldFormat;
/*  703:     */   }
/*  704:     */   
/*  705:     */   public Instances getHeader()
/*  706:     */   {
/*  707: 961 */     return this.m_Instances;
/*  708:     */   }
/*  709:     */   
/*  710:     */   public Estimator[][] getConditionalEstimators()
/*  711:     */   {
/*  712: 970 */     return this.m_Distributions;
/*  713:     */   }
/*  714:     */   
/*  715:     */   public Estimator getClassEstimator()
/*  716:     */   {
/*  717: 979 */     return this.m_ClassDistribution;
/*  718:     */   }
/*  719:     */   
/*  720:     */   public String getRevision()
/*  721:     */   {
/*  722: 989 */     return RevisionUtils.extract("$Revision: 11741 $");
/*  723:     */   }
/*  724:     */   
/*  725:     */   public NaiveBayes aggregate(NaiveBayes toAggregate)
/*  726:     */     throws Exception
/*  727:     */   {
/*  728: 998 */     if ((this.m_UseDiscretization) || (toAggregate.getUseSupervisedDiscretization())) {
/*  729: 999 */       throw new Exception("Unable to aggregate when supervised discretization has been turned on");
/*  730:     */     }
/*  731:1003 */     if (!this.m_Instances.equalHeaders(toAggregate.m_Instances)) {
/*  732:1004 */       throw new Exception("Can't aggregate - data headers don't match: " + this.m_Instances.equalHeadersMsg(toAggregate.m_Instances));
/*  733:     */     }
/*  734:1008 */     ((Aggregateable)this.m_ClassDistribution).aggregate(toAggregate.m_ClassDistribution);
/*  735:1012 */     for (int i = 0; i < this.m_Distributions.length; i++) {
/*  736:1013 */       for (int j = 0; j < this.m_Distributions[i].length; j++) {
/*  737:1014 */         ((Aggregateable)this.m_Distributions[i][j]).aggregate(toAggregate.m_Distributions[i][j]);
/*  738:     */       }
/*  739:     */     }
/*  740:1019 */     return this;
/*  741:     */   }
/*  742:     */   
/*  743:     */   public void finalizeAggregation()
/*  744:     */     throws Exception
/*  745:     */   {}
/*  746:     */   
/*  747:     */   public static void main(String[] argv)
/*  748:     */   {
/*  749:1033 */     runClassifier(new NaiveBayes(), argv);
/*  750:     */   }
/*  751:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.NaiveBayes
 * JD-Core Version:    0.7.0.1
 */