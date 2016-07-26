/*    1:     */ package weka.classifiers.functions;
/*    2:     */ 
/*    3:     */ import java.util.ArrayList;
/*    4:     */ import java.util.Collections;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.Random;
/*    7:     */ import java.util.Vector;
/*    8:     */ import weka.classifiers.RandomizableClassifier;
/*    9:     */ import weka.classifiers.UpdateableClassifier;
/*   10:     */ import weka.core.Aggregateable;
/*   11:     */ import weka.core.Attribute;
/*   12:     */ import weka.core.Capabilities;
/*   13:     */ import weka.core.Capabilities.Capability;
/*   14:     */ import weka.core.Instance;
/*   15:     */ import weka.core.Instances;
/*   16:     */ import weka.core.Option;
/*   17:     */ import weka.core.OptionHandler;
/*   18:     */ import weka.core.RevisionUtils;
/*   19:     */ import weka.core.SelectedTag;
/*   20:     */ import weka.core.Tag;
/*   21:     */ import weka.core.Utils;
/*   22:     */ import weka.filters.Filter;
/*   23:     */ import weka.filters.unsupervised.attribute.Normalize;
/*   24:     */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*   25:     */ 
/*   26:     */ public class SGD
/*   27:     */   extends RandomizableClassifier
/*   28:     */   implements UpdateableClassifier, OptionHandler, Aggregateable<SGD>
/*   29:     */ {
/*   30:     */   private static final long serialVersionUID = -3732968666673530290L;
/*   31:     */   protected ReplaceMissingValues m_replaceMissing;
/*   32:     */   protected Filter m_nominalToBinary;
/*   33:     */   protected Normalize m_normalize;
/*   34: 123 */   protected double m_lambda = 0.0001D;
/*   35: 126 */   protected double m_learningRate = 0.01D;
/*   36:     */   protected double[] m_weights;
/*   37: 132 */   protected double m_epsilon = 0.001D;
/*   38:     */   protected double m_t;
/*   39:     */   protected double m_numInstances;
/*   40: 144 */   protected int m_epochs = 500;
/*   41: 150 */   protected boolean m_dontNormalize = false;
/*   42: 156 */   protected boolean m_dontReplaceMissing = false;
/*   43:     */   protected Instances m_data;
/*   44:     */   public static final int HINGE = 0;
/*   45:     */   public static final int LOGLOSS = 1;
/*   46:     */   public static final int SQUAREDLOSS = 2;
/*   47:     */   public static final int EPSILON_INSENSITIVE = 3;
/*   48:     */   public static final int HUBER = 4;
/*   49:     */   
/*   50:     */   public Capabilities getCapabilities()
/*   51:     */   {
/*   52: 168 */     Capabilities result = super.getCapabilities();
/*   53: 169 */     result.disableAll();
/*   54:     */     
/*   55:     */ 
/*   56: 172 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*   57: 173 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*   58: 174 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*   59: 177 */     if ((this.m_loss == 2) || (this.m_loss == 3) || (this.m_loss == 4)) {
/*   60: 179 */       result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*   61:     */     } else {
/*   62: 181 */       result.enable(Capabilities.Capability.BINARY_CLASS);
/*   63:     */     }
/*   64: 182 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*   65:     */     
/*   66:     */ 
/*   67: 185 */     result.setMinimumNumberInstances(0);
/*   68:     */     
/*   69: 187 */     return result;
/*   70:     */   }
/*   71:     */   
/*   72:     */   public String epsilonTipText()
/*   73:     */   {
/*   74: 197 */     return "The epsilon threshold for epsilon insensitive and Huber loss. An error with absolute value less that this threshold has loss of 0 for epsilon insensitive loss. For Huber loss this is the boundary between the quadratic and linear parts of the loss function.";
/*   75:     */   }
/*   76:     */   
/*   77:     */   public void setEpsilon(double e)
/*   78:     */   {
/*   79: 211 */     this.m_epsilon = e;
/*   80:     */   }
/*   81:     */   
/*   82:     */   public double getEpsilon()
/*   83:     */   {
/*   84: 221 */     return this.m_epsilon;
/*   85:     */   }
/*   86:     */   
/*   87:     */   public String lambdaTipText()
/*   88:     */   {
/*   89: 231 */     return "The regularization constant. (default = 0.0001)";
/*   90:     */   }
/*   91:     */   
/*   92:     */   public void setLambda(double lambda)
/*   93:     */   {
/*   94: 240 */     this.m_lambda = lambda;
/*   95:     */   }
/*   96:     */   
/*   97:     */   public double getLambda()
/*   98:     */   {
/*   99: 249 */     return this.m_lambda;
/*  100:     */   }
/*  101:     */   
/*  102:     */   public void setLearningRate(double lr)
/*  103:     */   {
/*  104: 258 */     this.m_learningRate = lr;
/*  105:     */   }
/*  106:     */   
/*  107:     */   public double getLearningRate()
/*  108:     */   {
/*  109: 267 */     return this.m_learningRate;
/*  110:     */   }
/*  111:     */   
/*  112:     */   public String learningRateTipText()
/*  113:     */   {
/*  114: 277 */     return "The learning rate. If normalization is turned off (as it is automatically for streaming data), thenthe default learning rate will need to be reduced (try 0.0001).";
/*  115:     */   }
/*  116:     */   
/*  117:     */   public String epochsTipText()
/*  118:     */   {
/*  119: 290 */     return "The number of epochs to perform (batch learning). The total number of iterations is epochs * num instances.";
/*  120:     */   }
/*  121:     */   
/*  122:     */   public void setEpochs(int e)
/*  123:     */   {
/*  124: 300 */     this.m_epochs = e;
/*  125:     */   }
/*  126:     */   
/*  127:     */   public int getEpochs()
/*  128:     */   {
/*  129: 309 */     return this.m_epochs;
/*  130:     */   }
/*  131:     */   
/*  132:     */   public void setDontNormalize(boolean m)
/*  133:     */   {
/*  134: 318 */     this.m_dontNormalize = m;
/*  135:     */   }
/*  136:     */   
/*  137:     */   public boolean getDontNormalize()
/*  138:     */   {
/*  139: 327 */     return this.m_dontNormalize;
/*  140:     */   }
/*  141:     */   
/*  142:     */   public String dontNormalizeTipText()
/*  143:     */   {
/*  144: 337 */     return "Turn normalization off";
/*  145:     */   }
/*  146:     */   
/*  147:     */   public void setDontReplaceMissing(boolean m)
/*  148:     */   {
/*  149: 347 */     this.m_dontReplaceMissing = m;
/*  150:     */   }
/*  151:     */   
/*  152:     */   public boolean getDontReplaceMissing()
/*  153:     */   {
/*  154: 356 */     return this.m_dontReplaceMissing;
/*  155:     */   }
/*  156:     */   
/*  157:     */   public String dontReplaceMissingTipText()
/*  158:     */   {
/*  159: 366 */     return "Turn off global replacement of missing values";
/*  160:     */   }
/*  161:     */   
/*  162:     */   public void setLossFunction(SelectedTag function)
/*  163:     */   {
/*  164: 375 */     if (function.getTags() == TAGS_SELECTION) {
/*  165: 376 */       this.m_loss = function.getSelectedTag().getID();
/*  166:     */     }
/*  167:     */   }
/*  168:     */   
/*  169:     */   public SelectedTag getLossFunction()
/*  170:     */   {
/*  171: 386 */     return new SelectedTag(this.m_loss, TAGS_SELECTION);
/*  172:     */   }
/*  173:     */   
/*  174:     */   public String lossFunctionTipText()
/*  175:     */   {
/*  176: 396 */     return "The loss function to use. Hinge loss (SVM), log loss (logistic regression) or squared loss (regression).";
/*  177:     */   }
/*  178:     */   
/*  179:     */   public Enumeration<Option> listOptions()
/*  180:     */   {
/*  181: 408 */     Vector<Option> newVector = new Vector();
/*  182: 409 */     newVector.add(new Option("\tSet the loss function to minimize.\n\t0 = hinge loss (SVM), 1 = log loss (logistic regression),\n\t2 = squared loss (regression), 3 = epsilon insensitive loss (regression),\n\t4 = Huber loss (regression).\n\t(default = 0)", "F", 1, "-F"));
/*  183:     */     
/*  184:     */ 
/*  185:     */ 
/*  186: 413 */     newVector.add(new Option("\tThe learning rate. If normalization is\n\tturned off (as it is automatically for streaming data), then the\n\tdefault learning rate will need to be reduced (try 0.0001).\n\t(default = 0.01).", "L", 1, "-L"));
/*  187:     */     
/*  188:     */ 
/*  189:     */ 
/*  190:     */ 
/*  191:     */ 
/*  192: 419 */     newVector.add(new Option("\tThe lambda regularization constant (default = 0.0001)", "R", 1, "-R <double>"));
/*  193:     */     
/*  194: 421 */     newVector.add(new Option("\tThe number of epochs to perform (batch learning only, default = 500)", "E", 1, "-E <integer>"));
/*  195:     */     
/*  196: 423 */     newVector.add(new Option("\tThe epsilon threshold (epsilon-insenstive and Huber loss only, default = 1e-3)", "C", 1, "-C <double>"));
/*  197:     */     
/*  198:     */ 
/*  199: 426 */     newVector.add(new Option("\tDon't normalize the data", "N", 0, "-N"));
/*  200: 427 */     newVector.add(new Option("\tDon't replace missing values", "M", 0, "-M"));
/*  201:     */     
/*  202: 429 */     newVector.addAll(Collections.list(super.listOptions()));
/*  203:     */     
/*  204: 431 */     return newVector.elements();
/*  205:     */   }
/*  206:     */   
/*  207:     */   public void setOptions(String[] options)
/*  208:     */     throws Exception
/*  209:     */   {
/*  210: 489 */     reset();
/*  211:     */     
/*  212: 491 */     super.setOptions(options);
/*  213:     */     
/*  214: 493 */     String lossString = Utils.getOption('F', options);
/*  215: 494 */     if (lossString.length() != 0) {
/*  216: 495 */       setLossFunction(new SelectedTag(Integer.parseInt(lossString), TAGS_SELECTION));
/*  217:     */     }
/*  218: 499 */     String lambdaString = Utils.getOption('R', options);
/*  219: 500 */     if (lambdaString.length() > 0) {
/*  220: 501 */       setLambda(Double.parseDouble(lambdaString));
/*  221:     */     }
/*  222: 504 */     String learningRateString = Utils.getOption('L', options);
/*  223: 505 */     if (learningRateString.length() > 0) {
/*  224: 506 */       setLearningRate(Double.parseDouble(learningRateString));
/*  225:     */     }
/*  226: 509 */     String epochsString = Utils.getOption("E", options);
/*  227: 510 */     if (epochsString.length() > 0) {
/*  228: 511 */       setEpochs(Integer.parseInt(epochsString));
/*  229:     */     }
/*  230: 514 */     String epsilonString = Utils.getOption("C", options);
/*  231: 515 */     if (epsilonString.length() > 0) {
/*  232: 516 */       setEpsilon(Double.parseDouble(epsilonString));
/*  233:     */     }
/*  234: 519 */     setDontNormalize(Utils.getFlag("N", options));
/*  235: 520 */     setDontReplaceMissing(Utils.getFlag('M', options));
/*  236:     */     
/*  237: 522 */     Utils.checkForRemainingOptions(options);
/*  238:     */   }
/*  239:     */   
/*  240:     */   public String[] getOptions()
/*  241:     */   {
/*  242: 532 */     ArrayList<String> options = new ArrayList();
/*  243:     */     
/*  244: 534 */     options.add("-F");
/*  245: 535 */     options.add("" + getLossFunction().getSelectedTag().getID());
/*  246: 536 */     options.add("-L");
/*  247: 537 */     options.add("" + getLearningRate());
/*  248: 538 */     options.add("-R");
/*  249: 539 */     options.add("" + getLambda());
/*  250: 540 */     options.add("-E");
/*  251: 541 */     options.add("" + getEpochs());
/*  252: 542 */     options.add("-C");
/*  253: 543 */     options.add("" + getEpsilon());
/*  254: 544 */     if (getDontNormalize()) {
/*  255: 545 */       options.add("-N");
/*  256:     */     }
/*  257: 547 */     if (getDontReplaceMissing()) {
/*  258: 548 */       options.add("-M");
/*  259:     */     }
/*  260: 551 */     Collections.addAll(options, super.getOptions());
/*  261:     */     
/*  262: 553 */     return (String[])options.toArray(new String[1]);
/*  263:     */   }
/*  264:     */   
/*  265:     */   public String globalInfo()
/*  266:     */   {
/*  267: 563 */     return "Implements stochastic gradient descent for learning various linear models (binary class SVM, binary class logistic regression, squared loss, Huber loss and epsilon-insensitive loss linear regression). Globally replaces all missing values and transforms nominal attributes into binary ones. It also normalizes all attributes, so the coefficients in the output are based on the normalized data.\nFor numeric class attributes, the squared, Huber or epsilon-insensitve loss function must be used. Epsilon-insensitive and Huber loss may require a much higher learning rate.";
/*  268:     */   }
/*  269:     */   
/*  270:     */   public void reset()
/*  271:     */   {
/*  272: 579 */     this.m_t = 1.0D;
/*  273: 580 */     this.m_weights = null;
/*  274:     */   }
/*  275:     */   
/*  276:     */   public void buildClassifier(Instances data)
/*  277:     */     throws Exception
/*  278:     */   {
/*  279: 591 */     reset();
/*  280:     */     
/*  281:     */ 
/*  282: 594 */     getCapabilities().testWithFail(data);
/*  283:     */     
/*  284: 596 */     data = new Instances(data);
/*  285: 597 */     data.deleteWithMissingClass();
/*  286: 599 */     if ((data.numInstances() > 0) && (!this.m_dontReplaceMissing))
/*  287:     */     {
/*  288: 600 */       this.m_replaceMissing = new ReplaceMissingValues();
/*  289: 601 */       this.m_replaceMissing.setInputFormat(data);
/*  290: 602 */       data = Filter.useFilter(data, this.m_replaceMissing);
/*  291:     */     }
/*  292: 606 */     boolean onlyNumeric = true;
/*  293: 607 */     for (int i = 0; i < data.numAttributes(); i++) {
/*  294: 608 */       if ((i != data.classIndex()) && 
/*  295: 609 */         (!data.attribute(i).isNumeric()))
/*  296:     */       {
/*  297: 610 */         onlyNumeric = false;
/*  298: 611 */         break;
/*  299:     */       }
/*  300:     */     }
/*  301: 616 */     if (!onlyNumeric)
/*  302:     */     {
/*  303: 617 */       if (data.numInstances() > 0) {
/*  304: 618 */         this.m_nominalToBinary = new weka.filters.supervised.attribute.NominalToBinary();
/*  305:     */       } else {
/*  306: 620 */         this.m_nominalToBinary = new weka.filters.unsupervised.attribute.NominalToBinary();
/*  307:     */       }
/*  308: 622 */       this.m_nominalToBinary.setInputFormat(data);
/*  309: 623 */       data = Filter.useFilter(data, this.m_nominalToBinary);
/*  310:     */     }
/*  311: 626 */     if ((!this.m_dontNormalize) && (data.numInstances() > 0))
/*  312:     */     {
/*  313: 628 */       this.m_normalize = new Normalize();
/*  314: 629 */       this.m_normalize.setInputFormat(data);
/*  315: 630 */       data = Filter.useFilter(data, this.m_normalize);
/*  316:     */     }
/*  317: 633 */     this.m_numInstances = data.numInstances();
/*  318:     */     
/*  319: 635 */     this.m_weights = new double[data.numAttributes() + 1];
/*  320: 636 */     this.m_data = new Instances(data, 0);
/*  321: 638 */     if (data.numInstances() > 0)
/*  322:     */     {
/*  323: 639 */       data.randomize(new Random(getSeed()));
/*  324: 640 */       train(data);
/*  325:     */     }
/*  326:     */   }
/*  327:     */   
/*  328: 660 */   protected int m_loss = 0;
/*  329: 663 */   public static final Tag[] TAGS_SELECTION = { new Tag(0, "Hinge loss (SVM)"), new Tag(1, "Log loss (logistic regression)"), new Tag(2, "Squared loss (regression)"), new Tag(3, "Epsilon-insensitive loss (SVM regression)"), new Tag(4, "Huber loss (robust regression)") };
/*  330:     */   
/*  331:     */   protected double dloss(double z)
/*  332:     */   {
/*  333: 671 */     if (this.m_loss == 0) {
/*  334: 672 */       return z < 1.0D ? 1.0D : 0.0D;
/*  335:     */     }
/*  336: 675 */     if (this.m_loss == 1)
/*  337:     */     {
/*  338: 677 */       if (z < 0.0D) {
/*  339: 678 */         return 1.0D / (Math.exp(z) + 1.0D);
/*  340:     */       }
/*  341: 680 */       double t = Math.exp(-z);
/*  342: 681 */       return t / (t + 1.0D);
/*  343:     */     }
/*  344: 685 */     if (this.m_loss == 3)
/*  345:     */     {
/*  346: 686 */       if (z > this.m_epsilon) {
/*  347: 687 */         return 1.0D;
/*  348:     */       }
/*  349: 690 */       if (-z > this.m_epsilon) {
/*  350: 691 */         return -1.0D;
/*  351:     */       }
/*  352: 694 */       return 0.0D;
/*  353:     */     }
/*  354: 697 */     if (this.m_loss == 4)
/*  355:     */     {
/*  356: 698 */       if (Math.abs(z) <= this.m_epsilon) {
/*  357: 699 */         return z;
/*  358:     */       }
/*  359: 700 */       if (z > 0.0D) {
/*  360: 701 */         return this.m_epsilon;
/*  361:     */       }
/*  362: 703 */       return -this.m_epsilon;
/*  363:     */     }
/*  364: 708 */     return z;
/*  365:     */   }
/*  366:     */   
/*  367:     */   private void train(Instances data)
/*  368:     */     throws Exception
/*  369:     */   {
/*  370: 712 */     for (int e = 0; e < this.m_epochs; e++) {
/*  371: 713 */       for (int i = 0; i < data.numInstances(); i++) {
/*  372: 714 */         updateClassifier(data.instance(i), false);
/*  373:     */       }
/*  374:     */     }
/*  375:     */   }
/*  376:     */   
/*  377:     */   protected static double dotProd(Instance inst1, double[] weights, int classIndex)
/*  378:     */   {
/*  379: 721 */     double result = 0.0D;
/*  380:     */     
/*  381: 723 */     int n1 = inst1.numValues();
/*  382: 724 */     int n2 = weights.length - 1;
/*  383:     */     
/*  384: 726 */     int p1 = 0;
/*  385: 726 */     for (int p2 = 0; (p1 < n1) && (p2 < n2);)
/*  386:     */     {
/*  387: 727 */       int ind1 = inst1.index(p1);
/*  388: 728 */       int ind2 = p2;
/*  389: 729 */       if (ind1 == ind2)
/*  390:     */       {
/*  391: 730 */         if ((ind1 != classIndex) && (!inst1.isMissingSparse(p1))) {
/*  392: 731 */           result += inst1.valueSparse(p1) * weights[p2];
/*  393:     */         }
/*  394: 733 */         p1++;
/*  395: 734 */         p2++;
/*  396:     */       }
/*  397: 735 */       else if (ind1 > ind2)
/*  398:     */       {
/*  399: 736 */         p2++;
/*  400:     */       }
/*  401:     */       else
/*  402:     */       {
/*  403: 738 */         p1++;
/*  404:     */       }
/*  405:     */     }
/*  406: 741 */     return result;
/*  407:     */   }
/*  408:     */   
/*  409:     */   protected void updateClassifier(Instance instance, boolean filter)
/*  410:     */     throws Exception
/*  411:     */   {
/*  412: 758 */     if (!instance.classIsMissing())
/*  413:     */     {
/*  414: 759 */       if (filter)
/*  415:     */       {
/*  416: 760 */         if (this.m_replaceMissing != null)
/*  417:     */         {
/*  418: 761 */           this.m_replaceMissing.input(instance);
/*  419: 762 */           instance = this.m_replaceMissing.output();
/*  420:     */         }
/*  421: 765 */         if (this.m_nominalToBinary != null)
/*  422:     */         {
/*  423: 766 */           this.m_nominalToBinary.input(instance);
/*  424: 767 */           instance = this.m_nominalToBinary.output();
/*  425:     */         }
/*  426: 770 */         if (this.m_normalize != null)
/*  427:     */         {
/*  428: 771 */           this.m_normalize.input(instance);
/*  429: 772 */           instance = this.m_normalize.output();
/*  430:     */         }
/*  431:     */       }
/*  432: 776 */       double wx = dotProd(instance, this.m_weights, instance.classIndex());
/*  433:     */       double z;
/*  434:     */       double y;
/*  435:     */       double z;
/*  436: 780 */       if (instance.classAttribute().isNominal())
/*  437:     */       {
/*  438: 781 */         double y = instance.classValue() == 0.0D ? -1.0D : 1.0D;
/*  439: 782 */         z = y * (wx + this.m_weights[(this.m_weights.length - 1)]);
/*  440:     */       }
/*  441:     */       else
/*  442:     */       {
/*  443: 784 */         y = instance.classValue();
/*  444: 785 */         z = y - (wx + this.m_weights[(this.m_weights.length - 1)]);
/*  445: 786 */         y = 1.0D;
/*  446:     */       }
/*  447: 790 */       double multiplier = 1.0D;
/*  448: 791 */       if (this.m_numInstances == 0.0D) {
/*  449: 792 */         multiplier = 1.0D - this.m_learningRate * this.m_lambda / this.m_t;
/*  450:     */       } else {
/*  451: 794 */         multiplier = 1.0D - this.m_learningRate * this.m_lambda / this.m_numInstances;
/*  452:     */       }
/*  453: 796 */       for (int i = 0; i < this.m_weights.length - 1; i++) {
/*  454: 797 */         this.m_weights[i] *= multiplier;
/*  455:     */       }
/*  456: 802 */       if ((this.m_loss == 2) || (this.m_loss == 1) || (this.m_loss == 4) || ((this.m_loss == 0) && (z < 1.0D)) || ((this.m_loss == 3) && (Math.abs(z) > this.m_epsilon)))
/*  457:     */       {
/*  458: 807 */         double factor = this.m_learningRate * y * dloss(z);
/*  459:     */         
/*  460:     */ 
/*  461: 810 */         int n1 = instance.numValues();
/*  462: 811 */         for (int p1 = 0; p1 < n1; p1++)
/*  463:     */         {
/*  464: 812 */           int indS = instance.index(p1);
/*  465: 813 */           if ((indS != instance.classIndex()) && (!instance.isMissingSparse(p1))) {
/*  466: 814 */             this.m_weights[indS] += factor * instance.valueSparse(p1);
/*  467:     */           }
/*  468:     */         }
/*  469: 819 */         this.m_weights[(this.m_weights.length - 1)] += factor;
/*  470:     */       }
/*  471: 821 */       this.m_t += 1.0D;
/*  472:     */     }
/*  473:     */   }
/*  474:     */   
/*  475:     */   public void updateClassifier(Instance instance)
/*  476:     */     throws Exception
/*  477:     */   {
/*  478: 834 */     updateClassifier(instance, true);
/*  479:     */   }
/*  480:     */   
/*  481:     */   public double[] distributionForInstance(Instance inst)
/*  482:     */     throws Exception
/*  483:     */   {
/*  484: 846 */     double[] result = inst.classAttribute().isNominal() ? new double[2] : new double[1];
/*  485: 849 */     if (this.m_replaceMissing != null)
/*  486:     */     {
/*  487: 850 */       this.m_replaceMissing.input(inst);
/*  488: 851 */       inst = this.m_replaceMissing.output();
/*  489:     */     }
/*  490: 854 */     if (this.m_nominalToBinary != null)
/*  491:     */     {
/*  492: 855 */       this.m_nominalToBinary.input(inst);
/*  493: 856 */       inst = this.m_nominalToBinary.output();
/*  494:     */     }
/*  495: 859 */     if (this.m_normalize != null)
/*  496:     */     {
/*  497: 860 */       this.m_normalize.input(inst);
/*  498: 861 */       inst = this.m_normalize.output();
/*  499:     */     }
/*  500: 864 */     double wx = dotProd(inst, this.m_weights, inst.classIndex());
/*  501: 865 */     double z = wx + this.m_weights[(this.m_weights.length - 1)];
/*  502: 867 */     if (inst.classAttribute().isNumeric())
/*  503:     */     {
/*  504: 868 */       result[0] = z;
/*  505: 869 */       return result;
/*  506:     */     }
/*  507: 872 */     if (z <= 0.0D)
/*  508:     */     {
/*  509: 874 */       if (this.m_loss == 1)
/*  510:     */       {
/*  511: 875 */         result[0] = (1.0D / (1.0D + Math.exp(z)));
/*  512: 876 */         result[1] = (1.0D - result[0]);
/*  513:     */       }
/*  514:     */       else
/*  515:     */       {
/*  516: 878 */         result[0] = 1.0D;
/*  517:     */       }
/*  518:     */     }
/*  519: 881 */     else if (this.m_loss == 1)
/*  520:     */     {
/*  521: 882 */       result[1] = (1.0D / (1.0D + Math.exp(-z)));
/*  522: 883 */       result[0] = (1.0D - result[1]);
/*  523:     */     }
/*  524:     */     else
/*  525:     */     {
/*  526: 885 */       result[1] = 1.0D;
/*  527:     */     }
/*  528: 888 */     return result;
/*  529:     */   }
/*  530:     */   
/*  531:     */   public double[] getWeights()
/*  532:     */   {
/*  533: 892 */     return this.m_weights;
/*  534:     */   }
/*  535:     */   
/*  536:     */   public String toString()
/*  537:     */   {
/*  538: 902 */     if (this.m_weights == null) {
/*  539: 903 */       return "SGD: No model built yet.\n";
/*  540:     */     }
/*  541: 905 */     StringBuffer buff = new StringBuffer();
/*  542: 906 */     buff.append("Loss function: ");
/*  543: 907 */     if (this.m_loss == 0) {
/*  544: 908 */       buff.append("Hinge loss (SVM)\n\n");
/*  545: 909 */     } else if (this.m_loss == 1) {
/*  546: 910 */       buff.append("Log loss (logistic regression)\n\n");
/*  547:     */     }
/*  548: 911 */     if (this.m_loss == 3) {
/*  549: 912 */       buff.append("Epsilon insensitive loss (SVM regression)\n\n");
/*  550: 913 */     } else if (this.m_loss == 4) {
/*  551: 914 */       buff.append("Huber loss (robust regression)\n\n");
/*  552:     */     } else {
/*  553: 916 */       buff.append("Squared loss (linear regression)\n\n");
/*  554:     */     }
/*  555: 919 */     buff.append(this.m_data.classAttribute().name() + " = \n\n");
/*  556: 920 */     int printed = 0;
/*  557: 922 */     for (int i = 0; i < this.m_weights.length - 1; i++) {
/*  558: 923 */       if (i != this.m_data.classIndex())
/*  559:     */       {
/*  560: 924 */         if (printed > 0) {
/*  561: 925 */           buff.append(" + ");
/*  562:     */         } else {
/*  563: 927 */           buff.append("   ");
/*  564:     */         }
/*  565: 930 */         buff.append(Utils.doubleToString(this.m_weights[i], 12, 4) + " " + (this.m_normalize != null ? "(normalized) " : "") + this.m_data.attribute(i).name() + "\n");
/*  566:     */         
/*  567:     */ 
/*  568:     */ 
/*  569: 934 */         printed++;
/*  570:     */       }
/*  571:     */     }
/*  572: 938 */     if (this.m_weights[(this.m_weights.length - 1)] > 0.0D) {
/*  573: 939 */       buff.append(" + " + Utils.doubleToString(this.m_weights[(this.m_weights.length - 1)], 12, 4));
/*  574:     */     } else {
/*  575: 942 */       buff.append(" - " + Utils.doubleToString(-this.m_weights[(this.m_weights.length - 1)], 12, 4));
/*  576:     */     }
/*  577: 946 */     return buff.toString();
/*  578:     */   }
/*  579:     */   
/*  580:     */   public String getRevision()
/*  581:     */   {
/*  582: 956 */     return RevisionUtils.extract("$Revision: 11468 $");
/*  583:     */   }
/*  584:     */   
/*  585: 959 */   protected int m_numModels = 0;
/*  586:     */   
/*  587:     */   public SGD aggregate(SGD toAggregate)
/*  588:     */     throws Exception
/*  589:     */   {
/*  590: 972 */     if (this.m_weights == null) {
/*  591: 973 */       throw new Exception("No model built yet, can't aggregate");
/*  592:     */     }
/*  593: 976 */     if (!this.m_data.equalHeaders(toAggregate.m_data)) {
/*  594: 977 */       throw new Exception("Can't aggregate - data headers dont match: " + this.m_data.equalHeadersMsg(toAggregate.m_data));
/*  595:     */     }
/*  596: 981 */     if (this.m_weights.length != toAggregate.getWeights().length) {
/*  597: 982 */       throw new Exception("Can't aggregate - SDG to aggregate has weight vector that differs in length from ours.");
/*  598:     */     }
/*  599: 987 */     for (int i = 0; i < this.m_weights.length; i++) {
/*  600: 988 */       this.m_weights[i] += toAggregate.getWeights()[i];
/*  601:     */     }
/*  602: 991 */     this.m_numModels += 1;
/*  603:     */     
/*  604: 993 */     return this;
/*  605:     */   }
/*  606:     */   
/*  607:     */   public void finalizeAggregation()
/*  608:     */     throws Exception
/*  609:     */   {
/*  610:1004 */     if (this.m_numModels == 0) {
/*  611:1005 */       throw new Exception("Unable to finalize aggregation - haven't seen any models to aggregate");
/*  612:     */     }
/*  613:1009 */     for (int i = 0; i < this.m_weights.length; i++) {
/*  614:1010 */       this.m_weights[i] /= (this.m_numModels + 1);
/*  615:     */     }
/*  616:1014 */     this.m_numModels = 0;
/*  617:     */   }
/*  618:     */   
/*  619:     */   public static void main(String[] args)
/*  620:     */   {
/*  621:1021 */     runClassifier(new SGD(), args);
/*  622:     */   }
/*  623:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.SGD
 * JD-Core Version:    0.7.0.1
 */