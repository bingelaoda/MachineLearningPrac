/*    1:     */ package weka.classifiers.functions;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.Collections;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.Vector;
/*    7:     */ import weka.classifiers.AbstractClassifier;
/*    8:     */ import weka.classifiers.pmml.producer.LogisticProducerHelper;
/*    9:     */ import weka.core.Aggregateable;
/*   10:     */ import weka.core.Attribute;
/*   11:     */ import weka.core.Capabilities;
/*   12:     */ import weka.core.Capabilities.Capability;
/*   13:     */ import weka.core.ConjugateGradientOptimization;
/*   14:     */ import weka.core.Instance;
/*   15:     */ import weka.core.Instances;
/*   16:     */ import weka.core.Optimization;
/*   17:     */ import weka.core.Option;
/*   18:     */ import weka.core.OptionHandler;
/*   19:     */ import weka.core.RevisionUtils;
/*   20:     */ import weka.core.TechnicalInformation;
/*   21:     */ import weka.core.TechnicalInformation.Field;
/*   22:     */ import weka.core.TechnicalInformation.Type;
/*   23:     */ import weka.core.TechnicalInformationHandler;
/*   24:     */ import weka.core.Utils;
/*   25:     */ import weka.core.WeightedInstancesHandler;
/*   26:     */ import weka.core.pmml.PMMLProducer;
/*   27:     */ import weka.filters.Filter;
/*   28:     */ import weka.filters.unsupervised.attribute.NominalToBinary;
/*   29:     */ import weka.filters.unsupervised.attribute.RemoveUseless;
/*   30:     */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*   31:     */ 
/*   32:     */ public class Logistic
/*   33:     */   extends AbstractClassifier
/*   34:     */   implements OptionHandler, WeightedInstancesHandler, TechnicalInformationHandler, PMMLProducer, Aggregateable<Logistic>
/*   35:     */ {
/*   36:     */   static final long serialVersionUID = 3932117032546553727L;
/*   37:     */   protected double[][] m_Par;
/*   38:     */   protected double[][] m_Data;
/*   39:     */   protected int m_NumPredictors;
/*   40:     */   protected int m_ClassIndex;
/*   41:     */   protected int m_NumClasses;
/*   42: 161 */   protected double m_Ridge = 1.0E-008D;
/*   43:     */   private RemoveUseless m_AttFilter;
/*   44:     */   private NominalToBinary m_NominalToBinary;
/*   45:     */   private ReplaceMissingValues m_ReplaceMissingValues;
/*   46:     */   protected double m_LL;
/*   47: 176 */   private int m_MaxIts = -1;
/*   48: 179 */   private boolean m_useConjugateGradientDescent = false;
/*   49:     */   private Instances m_structure;
/*   50:     */   
/*   51:     */   public Logistic()
/*   52:     */   {
/*   53: 188 */     setNumDecimalPlaces(4);
/*   54:     */   }
/*   55:     */   
/*   56:     */   public String globalInfo()
/*   57:     */   {
/*   58: 198 */     return "Class for building and using a multinomial logistic regression model with a ridge estimator.\n\nThere are some modifications, however, compared to the paper of leCessie and van Houwelingen(1992): \n\nIf there are k classes for n instances with m attributes, the parameter matrix B to be calculated will be an m*(k-1) matrix.\n\nThe probability for class j with the exception of the last class is\n\nPj(Xi) = exp(XiBj)/((sum[j=1..(k-1)]exp(Xi*Bj))+1) \n\nThe last class has probability\n\n1-(sum[j=1..(k-1)]Pj(Xi)) \n\t= 1/((sum[j=1..(k-1)]exp(Xi*Bj))+1)\n\nThe (negative) multinomial log-likelihood is thus: \n\nL = -sum[i=1..n]{\n\tsum[j=1..(k-1)](Yij * ln(Pj(Xi)))\n\t+(1 - (sum[j=1..(k-1)]Yij)) \n\t* ln(1 - sum[j=1..(k-1)]Pj(Xi))\n\t} + ridge * (B^2)\n\nIn order to find the matrix B for which L is minimised, a Quasi-Newton Method is used to search for the optimized values of the m*(k-1) variables.  Note that before we use the optimization procedure, we 'squeeze' the matrix B into a m*(k-1) vector.  For details of the optimization procedure, please check weka.core.Optimization class.\n\nAlthough original Logistic Regression does not deal with instance weights, we modify the algorithm a little bit to handle the instance weights.\n\nFor more information see:\n\n" + getTechnicalInformation().toString() + "\n\n" + "Note: Missing values are replaced using a ReplaceMissingValuesFilter, and " + "nominal attributes are transformed into numeric attributes using a " + "NominalToBinaryFilter.";
/*   59:     */   }
/*   60:     */   
/*   61:     */   public TechnicalInformation getTechnicalInformation()
/*   62:     */   {
/*   63: 240 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*   64: 241 */     result.setValue(TechnicalInformation.Field.AUTHOR, "le Cessie, S. and van Houwelingen, J.C.");
/*   65: 242 */     result.setValue(TechnicalInformation.Field.YEAR, "1992");
/*   66: 243 */     result.setValue(TechnicalInformation.Field.TITLE, "Ridge Estimators in Logistic Regression");
/*   67: 244 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Applied Statistics");
/*   68: 245 */     result.setValue(TechnicalInformation.Field.VOLUME, "41");
/*   69: 246 */     result.setValue(TechnicalInformation.Field.NUMBER, "1");
/*   70: 247 */     result.setValue(TechnicalInformation.Field.PAGES, "191-201");
/*   71:     */     
/*   72: 249 */     return result;
/*   73:     */   }
/*   74:     */   
/*   75:     */   public Enumeration<Option> listOptions()
/*   76:     */   {
/*   77: 259 */     Vector<Option> newVector = new Vector(4);
/*   78:     */     
/*   79: 261 */     newVector.addElement(new Option("\tUse conjugate gradient descent rather than BFGS updates.", "C", 0, "-C"));
/*   80:     */     
/*   81:     */ 
/*   82: 264 */     newVector.addElement(new Option("\tSet the ridge in the log-likelihood.", "R", 1, "-R <ridge>"));
/*   83:     */     
/*   84: 266 */     newVector.addElement(new Option("\tSet the maximum number of iterations (default -1, until convergence).", "M", 1, "-M <number>"));
/*   85:     */     
/*   86:     */ 
/*   87: 269 */     newVector.addAll(Collections.list(super.listOptions()));
/*   88:     */     
/*   89: 271 */     return newVector.elements();
/*   90:     */   }
/*   91:     */   
/*   92:     */   public void setOptions(String[] options)
/*   93:     */     throws Exception
/*   94:     */   {
/*   95: 304 */     setUseConjugateGradientDescent(Utils.getFlag('C', options));
/*   96:     */     
/*   97: 306 */     String ridgeString = Utils.getOption('R', options);
/*   98: 307 */     if (ridgeString.length() != 0) {
/*   99: 308 */       this.m_Ridge = Double.parseDouble(ridgeString);
/*  100:     */     } else {
/*  101: 310 */       this.m_Ridge = 1.0E-008D;
/*  102:     */     }
/*  103: 313 */     String maxItsString = Utils.getOption('M', options);
/*  104: 314 */     if (maxItsString.length() != 0) {
/*  105: 315 */       this.m_MaxIts = Integer.parseInt(maxItsString);
/*  106:     */     } else {
/*  107: 317 */       this.m_MaxIts = -1;
/*  108:     */     }
/*  109: 320 */     super.setOptions(options);
/*  110:     */     
/*  111: 322 */     Utils.checkForRemainingOptions(options);
/*  112:     */   }
/*  113:     */   
/*  114:     */   public String[] getOptions()
/*  115:     */   {
/*  116: 333 */     Vector<String> options = new Vector();
/*  117: 335 */     if (getUseConjugateGradientDescent()) {
/*  118: 336 */       options.add("-C");
/*  119:     */     }
/*  120: 338 */     options.add("-R");
/*  121: 339 */     options.add("" + this.m_Ridge);
/*  122: 340 */     options.add("-M");
/*  123: 341 */     options.add("" + this.m_MaxIts);
/*  124:     */     
/*  125: 343 */     Collections.addAll(options, super.getOptions());
/*  126:     */     
/*  127: 345 */     return (String[])options.toArray(new String[0]);
/*  128:     */   }
/*  129:     */   
/*  130:     */   public String debugTipText()
/*  131:     */   {
/*  132: 356 */     return "Output debug information to the console.";
/*  133:     */   }
/*  134:     */   
/*  135:     */   public void setDebug(boolean debug)
/*  136:     */   {
/*  137: 366 */     this.m_Debug = debug;
/*  138:     */   }
/*  139:     */   
/*  140:     */   public boolean getDebug()
/*  141:     */   {
/*  142: 376 */     return this.m_Debug;
/*  143:     */   }
/*  144:     */   
/*  145:     */   public String useConjugateGradientDescentTipText()
/*  146:     */   {
/*  147: 386 */     return "Use conjugate gradient descent rather than BFGS updates; faster for problems with many parameters.";
/*  148:     */   }
/*  149:     */   
/*  150:     */   public void setUseConjugateGradientDescent(boolean useConjugateGradientDescent)
/*  151:     */   {
/*  152: 395 */     this.m_useConjugateGradientDescent = useConjugateGradientDescent;
/*  153:     */   }
/*  154:     */   
/*  155:     */   public boolean getUseConjugateGradientDescent()
/*  156:     */   {
/*  157: 404 */     return this.m_useConjugateGradientDescent;
/*  158:     */   }
/*  159:     */   
/*  160:     */   public String ridgeTipText()
/*  161:     */   {
/*  162: 414 */     return "Set the Ridge value in the log-likelihood.";
/*  163:     */   }
/*  164:     */   
/*  165:     */   public void setRidge(double ridge)
/*  166:     */   {
/*  167: 423 */     this.m_Ridge = ridge;
/*  168:     */   }
/*  169:     */   
/*  170:     */   public double getRidge()
/*  171:     */   {
/*  172: 432 */     return this.m_Ridge;
/*  173:     */   }
/*  174:     */   
/*  175:     */   public String maxItsTipText()
/*  176:     */   {
/*  177: 442 */     return "Maximum number of iterations to perform.";
/*  178:     */   }
/*  179:     */   
/*  180:     */   public int getMaxIts()
/*  181:     */   {
/*  182: 452 */     return this.m_MaxIts;
/*  183:     */   }
/*  184:     */   
/*  185:     */   public void setMaxIts(int newMaxIts)
/*  186:     */   {
/*  187: 462 */     this.m_MaxIts = newMaxIts;
/*  188:     */   }
/*  189:     */   
/*  190:     */   private class OptEng
/*  191:     */     extends Optimization
/*  192:     */   {
/*  193: 467 */     Logistic.OptObject m_oO = null;
/*  194:     */     
/*  195:     */     private OptEng(Logistic.OptObject oO)
/*  196:     */     {
/*  197: 470 */       this.m_oO = oO;
/*  198:     */     }
/*  199:     */     
/*  200:     */     protected double objectiveFunction(double[] x)
/*  201:     */     {
/*  202: 475 */       return this.m_oO.objectiveFunction(x);
/*  203:     */     }
/*  204:     */     
/*  205:     */     protected double[] evaluateGradient(double[] x)
/*  206:     */     {
/*  207: 480 */       return this.m_oO.evaluateGradient(x);
/*  208:     */     }
/*  209:     */     
/*  210:     */     public String getRevision()
/*  211:     */     {
/*  212: 485 */       return RevisionUtils.extract("$Revision: 12617 $");
/*  213:     */     }
/*  214:     */   }
/*  215:     */   
/*  216:     */   private class OptEngCG
/*  217:     */     extends ConjugateGradientOptimization
/*  218:     */   {
/*  219: 491 */     Logistic.OptObject m_oO = null;
/*  220:     */     
/*  221:     */     private OptEngCG(Logistic.OptObject oO)
/*  222:     */     {
/*  223: 494 */       this.m_oO = oO;
/*  224:     */     }
/*  225:     */     
/*  226:     */     protected double objectiveFunction(double[] x)
/*  227:     */     {
/*  228: 499 */       return this.m_oO.objectiveFunction(x);
/*  229:     */     }
/*  230:     */     
/*  231:     */     protected double[] evaluateGradient(double[] x)
/*  232:     */     {
/*  233: 504 */       return this.m_oO.evaluateGradient(x);
/*  234:     */     }
/*  235:     */     
/*  236:     */     public String getRevision()
/*  237:     */     {
/*  238: 509 */       return RevisionUtils.extract("$Revision: 12617 $");
/*  239:     */     }
/*  240:     */   }
/*  241:     */   
/*  242:     */   private class OptObject
/*  243:     */   {
/*  244:     */     private double[] weights;
/*  245:     */     private int[] cls;
/*  246:     */     
/*  247:     */     private OptObject() {}
/*  248:     */     
/*  249:     */     public void setWeights(double[] w)
/*  250:     */     {
/*  251: 527 */       this.weights = w;
/*  252:     */     }
/*  253:     */     
/*  254:     */     public void setClassLabels(int[] c)
/*  255:     */     {
/*  256: 536 */       this.cls = c;
/*  257:     */     }
/*  258:     */     
/*  259:     */     protected double objectiveFunction(double[] x)
/*  260:     */     {
/*  261: 546 */       double nll = 0.0D;
/*  262: 547 */       int dim = Logistic.this.m_NumPredictors + 1;
/*  263: 549 */       for (int i = 0; i < this.cls.length; i++)
/*  264:     */       {
/*  265: 551 */         double[] exp = new double[Logistic.this.m_NumClasses - 1];
/*  266: 553 */         for (int offset = 0; offset < Logistic.this.m_NumClasses - 1; offset++)
/*  267:     */         {
/*  268: 554 */           int index = offset * dim;
/*  269: 555 */           for (int j = 0; j < dim; j++) {
/*  270: 556 */             exp[offset] += Logistic.this.m_Data[i][j] * x[(index + j)];
/*  271:     */           }
/*  272:     */         }
/*  273: 559 */         double max = exp[Utils.maxIndex(exp)];
/*  274: 560 */         double denom = Math.exp(-max);
/*  275:     */         double num;
/*  276:     */         double num;
/*  277: 562 */         if (this.cls[i] == Logistic.this.m_NumClasses - 1) {
/*  278: 563 */           num = -max;
/*  279:     */         } else {
/*  280: 565 */           num = exp[this.cls[i]] - max;
/*  281:     */         }
/*  282: 567 */         for (int offset = 0; offset < Logistic.this.m_NumClasses - 1; offset++) {
/*  283: 568 */           denom += Math.exp(exp[offset] - max);
/*  284:     */         }
/*  285: 571 */         nll -= this.weights[i] * (num - Math.log(denom));
/*  286:     */       }
/*  287: 575 */       for (int offset = 0; offset < Logistic.this.m_NumClasses - 1; offset++) {
/*  288: 576 */         for (int r = 1; r < dim; r++) {
/*  289: 577 */           nll += Logistic.this.m_Ridge * x[(offset * dim + r)] * x[(offset * dim + r)];
/*  290:     */         }
/*  291:     */       }
/*  292: 581 */       return nll;
/*  293:     */     }
/*  294:     */     
/*  295:     */     protected double[] evaluateGradient(double[] x)
/*  296:     */     {
/*  297: 591 */       double[] grad = new double[x.length];
/*  298: 592 */       int dim = Logistic.this.m_NumPredictors + 1;
/*  299: 594 */       for (int i = 0; i < this.cls.length; i++)
/*  300:     */       {
/*  301: 595 */         double[] num = new double[Logistic.this.m_NumClasses - 1];
/*  302: 598 */         for (int offset = 0; offset < Logistic.this.m_NumClasses - 1; offset++)
/*  303:     */         {
/*  304: 600 */           double exp = 0.0D;
/*  305: 601 */           int index = offset * dim;
/*  306: 602 */           for (int j = 0; j < dim; j++) {
/*  307: 603 */             exp += Logistic.this.m_Data[i][j] * x[(index + j)];
/*  308:     */           }
/*  309: 605 */           num[offset] = exp;
/*  310:     */         }
/*  311: 608 */         double max = num[Utils.maxIndex(num)];
/*  312: 609 */         double denom = Math.exp(-max);
/*  313: 610 */         for (int offset = 0; offset < Logistic.this.m_NumClasses - 1; offset++)
/*  314:     */         {
/*  315: 611 */           num[offset] = Math.exp(num[offset] - max);
/*  316: 612 */           denom += num[offset];
/*  317:     */         }
/*  318: 614 */         Utils.normalize(num, denom);
/*  319: 618 */         for (int offset = 0; offset < Logistic.this.m_NumClasses - 1; offset++)
/*  320:     */         {
/*  321: 620 */           int index = offset * dim;
/*  322: 621 */           double firstTerm = this.weights[i] * num[offset];
/*  323: 622 */           for (int q = 0; q < dim; q++) {
/*  324: 623 */             grad[(index + q)] += firstTerm * Logistic.this.m_Data[i][q];
/*  325:     */           }
/*  326:     */         }
/*  327: 627 */         if (this.cls[i] != Logistic.this.m_NumClasses - 1) {
/*  328: 628 */           for (int p = 0; p < dim; p++) {
/*  329: 629 */             grad[(this.cls[i] * dim + p)] -= this.weights[i] * Logistic.this.m_Data[i][p];
/*  330:     */           }
/*  331:     */         }
/*  332:     */       }
/*  333: 635 */       for (int offset = 0; offset < Logistic.this.m_NumClasses - 1; offset++) {
/*  334: 636 */         for (int r = 1; r < dim; r++) {
/*  335: 637 */           grad[(offset * dim + r)] += 2.0D * Logistic.this.m_Ridge * x[(offset * dim + r)];
/*  336:     */         }
/*  337:     */       }
/*  338: 641 */       return grad;
/*  339:     */     }
/*  340:     */   }
/*  341:     */   
/*  342:     */   public Capabilities getCapabilities()
/*  343:     */   {
/*  344: 652 */     Capabilities result = super.getCapabilities();
/*  345: 653 */     result.disableAll();
/*  346:     */     
/*  347:     */ 
/*  348: 656 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  349: 657 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  350: 658 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  351: 659 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  352:     */     
/*  353:     */ 
/*  354: 662 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  355: 663 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  356:     */     
/*  357: 665 */     return result;
/*  358:     */   }
/*  359:     */   
/*  360:     */   public void buildClassifier(Instances train)
/*  361:     */     throws Exception
/*  362:     */   {
/*  363: 678 */     getCapabilities().testWithFail(train);
/*  364:     */     
/*  365:     */ 
/*  366: 681 */     train = new Instances(train);
/*  367: 682 */     train.deleteWithMissingClass();
/*  368:     */     
/*  369:     */ 
/*  370: 685 */     this.m_ReplaceMissingValues = new ReplaceMissingValues();
/*  371: 686 */     this.m_ReplaceMissingValues.setInputFormat(train);
/*  372: 687 */     train = Filter.useFilter(train, this.m_ReplaceMissingValues);
/*  373:     */     
/*  374:     */ 
/*  375: 690 */     this.m_AttFilter = new RemoveUseless();
/*  376: 691 */     this.m_AttFilter.setInputFormat(train);
/*  377: 692 */     train = Filter.useFilter(train, this.m_AttFilter);
/*  378:     */     
/*  379:     */ 
/*  380: 695 */     this.m_NominalToBinary = new NominalToBinary();
/*  381: 696 */     this.m_NominalToBinary.setInputFormat(train);
/*  382: 697 */     train = Filter.useFilter(train, this.m_NominalToBinary);
/*  383:     */     
/*  384:     */ 
/*  385: 700 */     this.m_structure = new Instances(train, 0);
/*  386:     */     
/*  387:     */ 
/*  388: 703 */     this.m_ClassIndex = train.classIndex();
/*  389: 704 */     this.m_NumClasses = train.numClasses();
/*  390:     */     
/*  391: 706 */     int nK = this.m_NumClasses - 1;
/*  392: 707 */     int nR = this.m_NumPredictors = train.numAttributes() - 1;
/*  393: 708 */     int nC = train.numInstances();
/*  394:     */     
/*  395: 710 */     this.m_Data = new double[nC][nR + 1];
/*  396: 711 */     int[] Y = new int[nC];
/*  397: 712 */     double[] xMean = new double[nR + 1];
/*  398: 713 */     double[] xSD = new double[nR + 1];
/*  399: 714 */     double[] sY = new double[nK + 1];
/*  400: 715 */     double[] weights = new double[nC];
/*  401: 716 */     double totWeights = 0.0D;
/*  402: 717 */     this.m_Par = new double[nR + 1][nK];
/*  403: 719 */     if (this.m_Debug) {
/*  404: 720 */       System.out.println("Extracting data...");
/*  405:     */     }
/*  406: 723 */     for (int i = 0; i < nC; i++)
/*  407:     */     {
/*  408: 725 */       Instance current = train.instance(i);
/*  409: 726 */       Y[i] = ((int)current.classValue());
/*  410: 727 */       weights[i] = current.weight();
/*  411: 728 */       totWeights += weights[i];
/*  412:     */       
/*  413: 730 */       this.m_Data[i][0] = 1.0D;
/*  414: 731 */       int j = 1;
/*  415: 732 */       for (int k = 0; k <= nR; k++) {
/*  416: 733 */         if (k != this.m_ClassIndex)
/*  417:     */         {
/*  418: 734 */           double x = current.value(k);
/*  419: 735 */           this.m_Data[i][j] = x;
/*  420: 736 */           xMean[j] += weights[i] * x;
/*  421: 737 */           xSD[j] += weights[i] * x * x;
/*  422: 738 */           j++;
/*  423:     */         }
/*  424:     */       }
/*  425: 743 */       sY[Y[i]] += 1.0D;
/*  426:     */     }
/*  427: 746 */     if ((totWeights <= 1.0D) && (nC > 1)) {
/*  428: 747 */       throw new Exception("Sum of weights of instances less than 1, please reweight!");
/*  429:     */     }
/*  430: 751 */     xMean[0] = 0.0D;
/*  431: 752 */     xSD[0] = 1.0D;
/*  432: 753 */     for (int j = 1; j <= nR; j++)
/*  433:     */     {
/*  434: 754 */       xMean[j] /= totWeights;
/*  435: 755 */       if (totWeights > 1.0D) {
/*  436: 756 */         xSD[j] = Math.sqrt(Math.abs(xSD[j] - totWeights * xMean[j] * xMean[j]) / (totWeights - 1.0D));
/*  437:     */       } else {
/*  438: 759 */         xSD[j] = 0.0D;
/*  439:     */       }
/*  440:     */     }
/*  441: 763 */     if (this.m_Debug)
/*  442:     */     {
/*  443: 765 */       System.out.println("Descriptives...");
/*  444: 766 */       for (int m = 0; m <= nK; m++) {
/*  445: 767 */         System.out.println(sY[m] + " cases have class " + m);
/*  446:     */       }
/*  447: 769 */       System.out.println("\n Variable     Avg       SD    ");
/*  448: 770 */       for (int j = 1; j <= nR; j++) {
/*  449: 771 */         System.out.println(Utils.doubleToString(j, 8, 4) + Utils.doubleToString(xMean[j], 10, 4) + Utils.doubleToString(xSD[j], 10, 4));
/*  450:     */       }
/*  451:     */     }
/*  452: 778 */     for (int i = 0; i < nC; i++) {
/*  453: 779 */       for (int j = 0; j <= nR; j++) {
/*  454: 780 */         if (xSD[j] != 0.0D) {
/*  455: 781 */           this.m_Data[i][j] = ((this.m_Data[i][j] - xMean[j]) / xSD[j]);
/*  456:     */         }
/*  457:     */       }
/*  458:     */     }
/*  459: 786 */     if (this.m_Debug) {
/*  460: 787 */       System.out.println("\nIteration History...");
/*  461:     */     }
/*  462: 790 */     double[] x = new double[(nR + 1) * nK];
/*  463: 791 */     double[][] b = new double[2][x.length];
/*  464: 794 */     for (int p = 0; p < nK; p++)
/*  465:     */     {
/*  466: 795 */       int offset = p * (nR + 1);
/*  467: 796 */       x[offset] = (Math.log(sY[p] + 1.0D) - Math.log(sY[nK] + 1.0D));
/*  468: 797 */       b[0][offset] = (0.0D / 0.0D);
/*  469: 798 */       b[1][offset] = (0.0D / 0.0D);
/*  470: 799 */       for (int q = 1; q <= nR; q++)
/*  471:     */       {
/*  472: 800 */         x[(offset + q)] = 0.0D;
/*  473: 801 */         b[0][(offset + q)] = (0.0D / 0.0D);
/*  474: 802 */         b[1][(offset + q)] = (0.0D / 0.0D);
/*  475:     */       }
/*  476:     */     }
/*  477: 806 */     OptObject oO = new OptObject(null);
/*  478: 807 */     oO.setWeights(weights);
/*  479: 808 */     oO.setClassLabels(Y);
/*  480:     */     
/*  481: 810 */     Optimization opt = null;
/*  482: 811 */     if (this.m_useConjugateGradientDescent) {
/*  483: 812 */       opt = new OptEngCG(oO, null);
/*  484:     */     } else {
/*  485: 814 */       opt = new OptEng(oO, null);
/*  486:     */     }
/*  487: 816 */     opt.setDebug(this.m_Debug);
/*  488: 818 */     if (this.m_MaxIts == -1)
/*  489:     */     {
/*  490: 819 */       x = opt.findArgmin(x, b);
/*  491: 820 */       while (x == null)
/*  492:     */       {
/*  493: 821 */         x = opt.getVarbValues();
/*  494: 822 */         if (this.m_Debug) {
/*  495: 823 */           System.out.println("First set of iterations finished, not enough!");
/*  496:     */         }
/*  497: 825 */         x = opt.findArgmin(x, b);
/*  498:     */       }
/*  499: 827 */       if (this.m_Debug) {
/*  500: 828 */         System.out.println(" -------------<Converged>--------------");
/*  501:     */       }
/*  502:     */     }
/*  503:     */     else
/*  504:     */     {
/*  505: 831 */       opt.setMaxIteration(this.m_MaxIts);
/*  506: 832 */       x = opt.findArgmin(x, b);
/*  507: 833 */       if (x == null) {
/*  508: 834 */         x = opt.getVarbValues();
/*  509:     */       }
/*  510:     */     }
/*  511: 838 */     this.m_LL = (-opt.getMinFunction());
/*  512:     */     
/*  513:     */ 
/*  514: 841 */     this.m_Data = ((double[][])null);
/*  515: 844 */     for (int i = 0; i < nK; i++)
/*  516:     */     {
/*  517: 845 */       this.m_Par[0][i] = x[(i * (nR + 1))];
/*  518: 846 */       for (int j = 1; j <= nR; j++)
/*  519:     */       {
/*  520: 847 */         this.m_Par[j][i] = x[(i * (nR + 1) + j)];
/*  521: 848 */         if (xSD[j] != 0.0D)
/*  522:     */         {
/*  523: 849 */           this.m_Par[j][i] /= xSD[j];
/*  524: 850 */           this.m_Par[0][i] -= this.m_Par[j][i] * xMean[j];
/*  525:     */         }
/*  526:     */       }
/*  527:     */     }
/*  528:     */   }
/*  529:     */   
/*  530:     */   public double[] distributionForInstance(Instance instance)
/*  531:     */     throws Exception
/*  532:     */   {
/*  533: 866 */     this.m_ReplaceMissingValues.input(instance);
/*  534: 867 */     instance = this.m_ReplaceMissingValues.output();
/*  535: 868 */     this.m_AttFilter.input(instance);
/*  536: 869 */     instance = this.m_AttFilter.output();
/*  537: 870 */     this.m_NominalToBinary.input(instance);
/*  538: 871 */     instance = this.m_NominalToBinary.output();
/*  539:     */     
/*  540:     */ 
/*  541: 874 */     double[] instDat = new double[this.m_NumPredictors + 1];
/*  542: 875 */     int j = 1;
/*  543: 876 */     instDat[0] = 1.0D;
/*  544: 877 */     for (int k = 0; k <= this.m_NumPredictors; k++) {
/*  545: 878 */       if (k != this.m_ClassIndex) {
/*  546: 879 */         instDat[(j++)] = instance.value(k);
/*  547:     */       }
/*  548:     */     }
/*  549: 883 */     double[] distribution = evaluateProbability(instDat);
/*  550: 884 */     return distribution;
/*  551:     */   }
/*  552:     */   
/*  553:     */   private double[] evaluateProbability(double[] data)
/*  554:     */   {
/*  555: 895 */     double[] prob = new double[this.m_NumClasses];double[] v = new double[this.m_NumClasses];
/*  556: 898 */     for (int j = 0; j < this.m_NumClasses - 1; j++) {
/*  557: 899 */       for (int k = 0; k <= this.m_NumPredictors; k++) {
/*  558: 900 */         v[j] += this.m_Par[k][j] * data[k];
/*  559:     */       }
/*  560:     */     }
/*  561: 903 */     v[(this.m_NumClasses - 1)] = 0.0D;
/*  562: 906 */     for (int m = 0; m < this.m_NumClasses; m++)
/*  563:     */     {
/*  564: 907 */       double sum = 0.0D;
/*  565: 908 */       for (int n = 0; n < this.m_NumClasses - 1; n++) {
/*  566: 909 */         sum += Math.exp(v[n] - v[m]);
/*  567:     */       }
/*  568: 911 */       prob[m] = (1.0D / (sum + Math.exp(-v[m])));
/*  569:     */     }
/*  570: 914 */     return prob;
/*  571:     */   }
/*  572:     */   
/*  573:     */   public double[][] coefficients()
/*  574:     */   {
/*  575: 924 */     return this.m_Par;
/*  576:     */   }
/*  577:     */   
/*  578:     */   public String toString()
/*  579:     */   {
/*  580: 934 */     StringBuffer temp = new StringBuffer();
/*  581:     */     
/*  582: 936 */     String result = "";
/*  583: 937 */     temp.append("Logistic Regression with ridge parameter of " + this.m_Ridge);
/*  584: 938 */     if (this.m_Par == null) {
/*  585: 939 */       return result + ": No model built yet.";
/*  586:     */     }
/*  587: 943 */     int attLength = 0;
/*  588: 944 */     for (int i = 0; i < this.m_structure.numAttributes(); i++) {
/*  589: 945 */       if ((i != this.m_structure.classIndex()) && (this.m_structure.attribute(i).name().length() > attLength)) {
/*  590: 947 */         attLength = this.m_structure.attribute(i).name().length();
/*  591:     */       }
/*  592:     */     }
/*  593: 951 */     if ("Intercept".length() > attLength) {
/*  594: 952 */       attLength = "Intercept".length();
/*  595:     */     }
/*  596: 955 */     if ("Variable".length() > attLength) {
/*  597: 956 */       attLength = "Variable".length();
/*  598:     */     }
/*  599: 958 */     attLength += 2;
/*  600:     */     
/*  601: 960 */     int colWidth = 0;
/*  602: 962 */     for (int i = 0; i < this.m_structure.classAttribute().numValues() - 1; i++) {
/*  603: 963 */       if (this.m_structure.classAttribute().value(i).length() > colWidth) {
/*  604: 964 */         colWidth = this.m_structure.classAttribute().value(i).length();
/*  605:     */       }
/*  606:     */     }
/*  607: 969 */     for (int j = 1; j <= this.m_NumPredictors; j++) {
/*  608: 970 */       for (int k = 0; k < this.m_NumClasses - 1; k++)
/*  609:     */       {
/*  610: 971 */         if (Utils.doubleToString(this.m_Par[j][k], 8 + getNumDecimalPlaces(), getNumDecimalPlaces()).trim().length() > colWidth) {
/*  611: 972 */           colWidth = Utils.doubleToString(this.m_Par[j][k], 8 + getNumDecimalPlaces(), getNumDecimalPlaces()).trim().length();
/*  612:     */         }
/*  613: 974 */         double ORc = Math.exp(this.m_Par[j][k]);
/*  614: 975 */         String t = " " + (ORc > 10000000000.0D ? "" + ORc : Utils.doubleToString(ORc, 8 + getNumDecimalPlaces(), getNumDecimalPlaces()));
/*  615: 977 */         if (t.trim().length() > colWidth) {
/*  616: 978 */           colWidth = t.trim().length();
/*  617:     */         }
/*  618:     */       }
/*  619:     */     }
/*  620: 983 */     if ("Class".length() > colWidth) {
/*  621: 984 */       colWidth = "Class".length();
/*  622:     */     }
/*  623: 986 */     colWidth += 2;
/*  624:     */     
/*  625: 988 */     temp.append("\nCoefficients...\n");
/*  626: 989 */     temp.append(Utils.padLeft(" ", attLength) + Utils.padLeft("Class", colWidth) + "\n");
/*  627:     */     
/*  628: 991 */     temp.append(Utils.padRight("Variable", attLength));
/*  629: 993 */     for (int i = 0; i < this.m_NumClasses - 1; i++)
/*  630:     */     {
/*  631: 994 */       String className = this.m_structure.classAttribute().value(i);
/*  632: 995 */       temp.append(Utils.padLeft(className, colWidth));
/*  633:     */     }
/*  634: 997 */     temp.append("\n");
/*  635: 998 */     int separatorL = attLength + (this.m_NumClasses - 1) * colWidth;
/*  636: 999 */     for (int i = 0; i < separatorL; i++) {
/*  637:1000 */       temp.append("=");
/*  638:     */     }
/*  639:1002 */     temp.append("\n");
/*  640:     */     
/*  641:1004 */     int j = 1;
/*  642:1005 */     for (int i = 0; i < this.m_structure.numAttributes(); i++) {
/*  643:1006 */       if (i != this.m_structure.classIndex())
/*  644:     */       {
/*  645:1007 */         temp.append(Utils.padRight(this.m_structure.attribute(i).name(), attLength));
/*  646:1008 */         for (int k = 0; k < this.m_NumClasses - 1; k++) {
/*  647:1009 */           temp.append(Utils.padLeft(Utils.doubleToString(this.m_Par[j][k], 8 + getNumDecimalPlaces(), getNumDecimalPlaces()).trim(), colWidth));
/*  648:     */         }
/*  649:1012 */         temp.append("\n");
/*  650:1013 */         j++;
/*  651:     */       }
/*  652:     */     }
/*  653:1017 */     temp.append(Utils.padRight("Intercept", attLength));
/*  654:1018 */     for (int k = 0; k < this.m_NumClasses - 1; k++) {
/*  655:1019 */       temp.append(Utils.padLeft(Utils.doubleToString(this.m_Par[0][k], 6 + getNumDecimalPlaces(), getNumDecimalPlaces()).trim(), colWidth));
/*  656:     */     }
/*  657:1022 */     temp.append("\n");
/*  658:     */     
/*  659:1024 */     temp.append("\n\nOdds Ratios...\n");
/*  660:1025 */     temp.append(Utils.padLeft(" ", attLength) + Utils.padLeft("Class", colWidth) + "\n");
/*  661:     */     
/*  662:1027 */     temp.append(Utils.padRight("Variable", attLength));
/*  663:1029 */     for (int i = 0; i < this.m_NumClasses - 1; i++)
/*  664:     */     {
/*  665:1030 */       String className = this.m_structure.classAttribute().value(i);
/*  666:1031 */       temp.append(Utils.padLeft(className, colWidth));
/*  667:     */     }
/*  668:1033 */     temp.append("\n");
/*  669:1034 */     for (int i = 0; i < separatorL; i++) {
/*  670:1035 */       temp.append("=");
/*  671:     */     }
/*  672:1037 */     temp.append("\n");
/*  673:     */     
/*  674:1039 */     j = 1;
/*  675:1040 */     for (int i = 0; i < this.m_structure.numAttributes(); i++) {
/*  676:1041 */       if (i != this.m_structure.classIndex())
/*  677:     */       {
/*  678:1042 */         temp.append(Utils.padRight(this.m_structure.attribute(i).name(), attLength));
/*  679:1043 */         for (int k = 0; k < this.m_NumClasses - 1; k++)
/*  680:     */         {
/*  681:1044 */           double ORc = Math.exp(this.m_Par[j][k]);
/*  682:1045 */           String ORs = " " + (ORc > 10000000000.0D ? "" + ORc : Utils.doubleToString(ORc, 8 + getNumDecimalPlaces(), getNumDecimalPlaces()));
/*  683:     */           
/*  684:1047 */           temp.append(Utils.padLeft(ORs.trim(), colWidth));
/*  685:     */         }
/*  686:1049 */         temp.append("\n");
/*  687:1050 */         j++;
/*  688:     */       }
/*  689:     */     }
/*  690:1054 */     return temp.toString();
/*  691:     */   }
/*  692:     */   
/*  693:     */   public String getRevision()
/*  694:     */   {
/*  695:1064 */     return RevisionUtils.extract("$Revision: 12617 $");
/*  696:     */   }
/*  697:     */   
/*  698:1067 */   protected int m_numModels = 0;
/*  699:     */   
/*  700:     */   public Logistic aggregate(Logistic toAggregate)
/*  701:     */     throws Exception
/*  702:     */   {
/*  703:1079 */     if (this.m_numModels == -2147483648) {
/*  704:1080 */       throw new Exception("Can't aggregate further - model has already been aggregated and finalized");
/*  705:     */     }
/*  706:1084 */     if (this.m_Par == null) {
/*  707:1085 */       throw new Exception("No model built yet, can't aggregate");
/*  708:     */     }
/*  709:1088 */     if (!this.m_structure.equalHeaders(toAggregate.m_structure)) {
/*  710:1089 */       throw new Exception("Can't aggregate - data headers dont match: " + this.m_structure.equalHeadersMsg(toAggregate.m_structure));
/*  711:     */     }
/*  712:1093 */     for (int i = 0; i < this.m_Par.length; i++) {
/*  713:1094 */       for (int j = 0; j < this.m_Par[i].length; j++) {
/*  714:1095 */         this.m_Par[i][j] += toAggregate.m_Par[i][j];
/*  715:     */       }
/*  716:     */     }
/*  717:1099 */     this.m_numModels += 1;
/*  718:     */     
/*  719:1101 */     return this;
/*  720:     */   }
/*  721:     */   
/*  722:     */   public void finalizeAggregation()
/*  723:     */     throws Exception
/*  724:     */   {
/*  725:1113 */     if (this.m_numModels == -2147483648) {
/*  726:1114 */       throw new Exception("Aggregation has already been finalized");
/*  727:     */     }
/*  728:1117 */     if (this.m_numModels == 0) {
/*  729:1118 */       throw new Exception("Unable to finalize aggregation - haven't seen any models to aggregate");
/*  730:     */     }
/*  731:1122 */     for (int i = 0; i < this.m_Par.length; i++) {
/*  732:1123 */       for (int j = 0; j < this.m_Par[i].length; j++) {
/*  733:1124 */         this.m_Par[i][j] /= (this.m_numModels + 1);
/*  734:     */       }
/*  735:     */     }
/*  736:1129 */     this.m_numModels = -2147483648;
/*  737:     */   }
/*  738:     */   
/*  739:     */   public static void main(String[] argv)
/*  740:     */   {
/*  741:1139 */     runClassifier(new Logistic(), argv);
/*  742:     */   }
/*  743:     */   
/*  744:     */   public String toPMML(Instances train)
/*  745:     */   {
/*  746:1151 */     return LogisticProducerHelper.toPMML(train, this.m_structure, this.m_Par, this.m_NumClasses);
/*  747:     */   }
/*  748:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.Logistic
 * JD-Core Version:    0.7.0.1
 */