/*    1:     */ package weka.attributeSelection;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.Enumeration;
/*    5:     */ import java.util.Random;
/*    6:     */ import java.util.Vector;
/*    7:     */ import weka.core.Attribute;
/*    8:     */ import weka.core.Capabilities;
/*    9:     */ import weka.core.Capabilities.Capability;
/*   10:     */ import weka.core.Instance;
/*   11:     */ import weka.core.Instances;
/*   12:     */ import weka.core.Option;
/*   13:     */ import weka.core.OptionHandler;
/*   14:     */ import weka.core.RevisionUtils;
/*   15:     */ import weka.core.TechnicalInformation;
/*   16:     */ import weka.core.TechnicalInformation.Field;
/*   17:     */ import weka.core.TechnicalInformation.Type;
/*   18:     */ import weka.core.TechnicalInformationHandler;
/*   19:     */ import weka.core.Utils;
/*   20:     */ 
/*   21:     */ public class ReliefFAttributeEval
/*   22:     */   extends ASEvaluation
/*   23:     */   implements AttributeEvaluator, OptionHandler, TechnicalInformationHandler
/*   24:     */ {
/*   25:     */   static final long serialVersionUID = -8422186665795839379L;
/*   26:     */   private Instances m_trainInstances;
/*   27:     */   private int m_classIndex;
/*   28:     */   private int m_numAttribs;
/*   29:     */   private int m_numInstances;
/*   30:     */   private boolean m_numericClass;
/*   31:     */   private int m_numClasses;
/*   32:     */   private double m_ndc;
/*   33:     */   private double[] m_nda;
/*   34:     */   private double[] m_ndcda;
/*   35:     */   private double[] m_weights;
/*   36:     */   private double[] m_classProbs;
/*   37:     */   private int m_sampleM;
/*   38:     */   private int m_Knn;
/*   39:     */   private double[][][] m_karray;
/*   40:     */   private double[] m_maxArray;
/*   41:     */   private double[] m_minArray;
/*   42:     */   private double[] m_worst;
/*   43:     */   private int[] m_index;
/*   44:     */   private int[] m_stored;
/*   45:     */   private int m_seed;
/*   46:     */   private double[] m_weightsByRank;
/*   47:     */   private int m_sigma;
/*   48:     */   private boolean m_weightByDistance;
/*   49:     */   
/*   50:     */   public ReliefFAttributeEval()
/*   51:     */   {
/*   52: 240 */     resetOptions();
/*   53:     */   }
/*   54:     */   
/*   55:     */   public String globalInfo()
/*   56:     */   {
/*   57: 250 */     return "ReliefFAttributeEval :\n\nEvaluates the worth of an attribute by repeatedly sampling an instance and considering the value of the given attribute for the nearest instance of the same and different class. Can operate on both discrete and continuous class data.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*   58:     */   }
/*   59:     */   
/*   60:     */   public TechnicalInformation getTechnicalInformation()
/*   61:     */   {
/*   62: 269 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*   63: 270 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Kenji Kira and Larry A. Rendell");
/*   64: 271 */     result.setValue(TechnicalInformation.Field.TITLE, "A Practical Approach to Feature Selection");
/*   65: 272 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Ninth International Workshop on Machine Learning");
/*   66:     */     
/*   67: 274 */     result.setValue(TechnicalInformation.Field.EDITOR, "Derek H. Sleeman and Peter Edwards");
/*   68: 275 */     result.setValue(TechnicalInformation.Field.YEAR, "1992");
/*   69: 276 */     result.setValue(TechnicalInformation.Field.PAGES, "249-256");
/*   70: 277 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann");
/*   71:     */     
/*   72: 279 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.INPROCEEDINGS);
/*   73: 280 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Igor Kononenko");
/*   74: 281 */     additional.setValue(TechnicalInformation.Field.TITLE, "Estimating Attributes: Analysis and Extensions of RELIEF");
/*   75:     */     
/*   76: 283 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "European Conference on Machine Learning");
/*   77:     */     
/*   78: 285 */     additional.setValue(TechnicalInformation.Field.EDITOR, "Francesco Bergadano and Luc De Raedt");
/*   79: 286 */     additional.setValue(TechnicalInformation.Field.YEAR, "1994");
/*   80: 287 */     additional.setValue(TechnicalInformation.Field.PAGES, "171-182");
/*   81: 288 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*   82:     */     
/*   83: 290 */     additional = result.add(TechnicalInformation.Type.INPROCEEDINGS);
/*   84: 291 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Marko Robnik-Sikonja and Igor Kononenko");
/*   85:     */     
/*   86: 293 */     additional.setValue(TechnicalInformation.Field.TITLE, "An adaptation of Relief for attribute estimation in regression");
/*   87:     */     
/*   88: 295 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "Fourteenth International Conference on Machine Learning");
/*   89:     */     
/*   90: 297 */     additional.setValue(TechnicalInformation.Field.EDITOR, "Douglas H. Fisher");
/*   91: 298 */     additional.setValue(TechnicalInformation.Field.YEAR, "1997");
/*   92: 299 */     additional.setValue(TechnicalInformation.Field.PAGES, "296-304");
/*   93: 300 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann");
/*   94:     */     
/*   95: 302 */     return result;
/*   96:     */   }
/*   97:     */   
/*   98:     */   public Enumeration<Option> listOptions()
/*   99:     */   {
/*  100: 312 */     Vector<Option> newVector = new Vector(4);
/*  101: 313 */     newVector.addElement(new Option("\tSpecify the number of instances to\n\tsample when estimating attributes.\n\tIf not specified, then all instances\n\twill be used.", "M", 1, "-M <num instances>"));
/*  102:     */     
/*  103:     */ 
/*  104:     */ 
/*  105: 317 */     newVector.addElement(new Option("\tSeed for randomly sampling instances.\n\t(Default = 1)", "D", 1, "-D <seed>"));
/*  106:     */     
/*  107: 319 */     newVector.addElement(new Option("\tNumber of nearest neighbours (k) used\n\tto estimate attribute relevances\n\t(Default = 10).", "K", 1, "-K <number of neighbours>"));
/*  108:     */     
/*  109:     */ 
/*  110: 322 */     newVector.addElement(new Option("\tWeight nearest neighbours by distance", "W", 0, "-W"));
/*  111:     */     
/*  112: 324 */     newVector.addElement(new Option("\tSpecify sigma value (used in an exp\n\tfunction to control how quickly\n\tweights for more distant instances\n\tdecrease. Use in conjunction with -W.\n\tSensible value=1/5 to 1/10 of the\n\tnumber of nearest neighbours.\n\t(Default = 2)", "A", 1, "-A <num>"));
/*  113:     */     
/*  114:     */ 
/*  115:     */ 
/*  116:     */ 
/*  117:     */ 
/*  118:     */ 
/*  119: 331 */     return newVector.elements();
/*  120:     */   }
/*  121:     */   
/*  122:     */   public void setOptions(String[] options)
/*  123:     */     throws Exception
/*  124:     */   {
/*  125: 386 */     resetOptions();
/*  126: 387 */     setWeightByDistance(Utils.getFlag('W', options));
/*  127: 388 */     String optionString = Utils.getOption('M', options);
/*  128: 390 */     if (optionString.length() != 0) {
/*  129: 391 */       setSampleSize(Integer.parseInt(optionString));
/*  130:     */     }
/*  131: 394 */     optionString = Utils.getOption('D', options);
/*  132: 396 */     if (optionString.length() != 0) {
/*  133: 397 */       setSeed(Integer.parseInt(optionString));
/*  134:     */     }
/*  135: 400 */     optionString = Utils.getOption('K', options);
/*  136: 402 */     if (optionString.length() != 0) {
/*  137: 403 */       setNumNeighbours(Integer.parseInt(optionString));
/*  138:     */     }
/*  139: 406 */     optionString = Utils.getOption('A', options);
/*  140: 408 */     if (optionString.length() != 0)
/*  141:     */     {
/*  142: 409 */       setWeightByDistance(true);
/*  143: 410 */       setSigma(Integer.parseInt(optionString));
/*  144:     */     }
/*  145:     */   }
/*  146:     */   
/*  147:     */   public String sigmaTipText()
/*  148:     */   {
/*  149: 421 */     return "Set influence of nearest neighbours. Used in an exp function to control how quickly weights decrease for more distant instances. Use in conjunction with weightByDistance. Sensible values = 1/5 to 1/10 the number of nearest neighbours.";
/*  150:     */   }
/*  151:     */   
/*  152:     */   public void setSigma(int s)
/*  153:     */     throws Exception
/*  154:     */   {
/*  155: 434 */     if (s <= 0) {
/*  156: 435 */       throw new Exception("value of sigma must be > 0!");
/*  157:     */     }
/*  158: 438 */     this.m_sigma = s;
/*  159:     */   }
/*  160:     */   
/*  161:     */   public int getSigma()
/*  162:     */   {
/*  163: 447 */     return this.m_sigma;
/*  164:     */   }
/*  165:     */   
/*  166:     */   public String numNeighboursTipText()
/*  167:     */   {
/*  168: 457 */     return "Number of nearest neighbours for attribute estimation.";
/*  169:     */   }
/*  170:     */   
/*  171:     */   public void setNumNeighbours(int n)
/*  172:     */   {
/*  173: 466 */     this.m_Knn = n;
/*  174:     */   }
/*  175:     */   
/*  176:     */   public int getNumNeighbours()
/*  177:     */   {
/*  178: 475 */     return this.m_Knn;
/*  179:     */   }
/*  180:     */   
/*  181:     */   public String seedTipText()
/*  182:     */   {
/*  183: 485 */     return "Random seed for sampling instances.";
/*  184:     */   }
/*  185:     */   
/*  186:     */   public void setSeed(int s)
/*  187:     */   {
/*  188: 494 */     this.m_seed = s;
/*  189:     */   }
/*  190:     */   
/*  191:     */   public int getSeed()
/*  192:     */   {
/*  193: 503 */     return this.m_seed;
/*  194:     */   }
/*  195:     */   
/*  196:     */   public String sampleSizeTipText()
/*  197:     */   {
/*  198: 513 */     return "Number of instances to sample. Default (-1) indicates that all instances will be used for attribute estimation.";
/*  199:     */   }
/*  200:     */   
/*  201:     */   public void setSampleSize(int s)
/*  202:     */   {
/*  203: 523 */     this.m_sampleM = s;
/*  204:     */   }
/*  205:     */   
/*  206:     */   public int getSampleSize()
/*  207:     */   {
/*  208: 532 */     return this.m_sampleM;
/*  209:     */   }
/*  210:     */   
/*  211:     */   public String weightByDistanceTipText()
/*  212:     */   {
/*  213: 542 */     return "Weight nearest neighbours by their distance.";
/*  214:     */   }
/*  215:     */   
/*  216:     */   public void setWeightByDistance(boolean b)
/*  217:     */   {
/*  218: 551 */     this.m_weightByDistance = b;
/*  219:     */   }
/*  220:     */   
/*  221:     */   public boolean getWeightByDistance()
/*  222:     */   {
/*  223: 560 */     return this.m_weightByDistance;
/*  224:     */   }
/*  225:     */   
/*  226:     */   public String[] getOptions()
/*  227:     */   {
/*  228: 571 */     Vector<String> options = new Vector();
/*  229: 573 */     if (getWeightByDistance()) {
/*  230: 574 */       options.add("-W");
/*  231:     */     }
/*  232: 577 */     options.add("-M");
/*  233: 578 */     options.add("" + getSampleSize());
/*  234: 579 */     options.add("-D");
/*  235: 580 */     options.add("" + getSeed());
/*  236: 581 */     options.add("-K");
/*  237: 582 */     options.add("" + getNumNeighbours());
/*  238: 584 */     if (getWeightByDistance())
/*  239:     */     {
/*  240: 585 */       options.add("-A");
/*  241: 586 */       options.add("" + getSigma());
/*  242:     */     }
/*  243: 589 */     return (String[])options.toArray(new String[0]);
/*  244:     */   }
/*  245:     */   
/*  246:     */   public String toString()
/*  247:     */   {
/*  248: 599 */     StringBuffer text = new StringBuffer();
/*  249: 601 */     if (this.m_trainInstances == null)
/*  250:     */     {
/*  251: 602 */       text.append("ReliefF feature evaluator has not been built yet\n");
/*  252:     */     }
/*  253:     */     else
/*  254:     */     {
/*  255: 604 */       text.append("\tReliefF Ranking Filter");
/*  256: 605 */       text.append("\n\tInstances sampled: ");
/*  257: 607 */       if (this.m_sampleM == -1) {
/*  258: 608 */         text.append("all\n");
/*  259:     */       } else {
/*  260: 610 */         text.append(this.m_sampleM + "\n");
/*  261:     */       }
/*  262: 613 */       text.append("\tNumber of nearest neighbours (k): " + this.m_Knn + "\n");
/*  263: 615 */       if (this.m_weightByDistance) {
/*  264: 616 */         text.append("\tExponentially decreasing (with distance) influence for\n\tnearest neighbours. Sigma: " + this.m_sigma + "\n");
/*  265:     */       } else {
/*  266: 620 */         text.append("\tEqual influence nearest neighbours\n");
/*  267:     */       }
/*  268:     */     }
/*  269: 624 */     return text.toString();
/*  270:     */   }
/*  271:     */   
/*  272:     */   public Capabilities getCapabilities()
/*  273:     */   {
/*  274: 635 */     Capabilities result = super.getCapabilities();
/*  275: 636 */     result.disableAll();
/*  276:     */     
/*  277:     */ 
/*  278: 639 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  279: 640 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  280: 641 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  281: 642 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  282:     */     
/*  283:     */ 
/*  284: 645 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  285: 646 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  286: 647 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  287: 648 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  288:     */     
/*  289: 650 */     return result;
/*  290:     */   }
/*  291:     */   
/*  292:     */   public void buildEvaluator(Instances data)
/*  293:     */     throws Exception
/*  294:     */   {
/*  295: 663 */     Random r = new Random(this.m_seed);
/*  296:     */     
/*  297:     */ 
/*  298: 666 */     getCapabilities().testWithFail(data);
/*  299:     */     
/*  300: 668 */     this.m_trainInstances = data;
/*  301: 669 */     this.m_classIndex = this.m_trainInstances.classIndex();
/*  302: 670 */     this.m_numAttribs = this.m_trainInstances.numAttributes();
/*  303: 671 */     this.m_numInstances = this.m_trainInstances.numInstances();
/*  304: 673 */     if (this.m_trainInstances.attribute(this.m_classIndex).isNumeric()) {
/*  305: 674 */       this.m_numericClass = true;
/*  306:     */     } else {
/*  307: 676 */       this.m_numericClass = false;
/*  308:     */     }
/*  309: 679 */     if (!this.m_numericClass)
/*  310:     */     {
/*  311: 680 */       this.m_numClasses = this.m_trainInstances.attribute(this.m_classIndex).numValues();
/*  312:     */     }
/*  313:     */     else
/*  314:     */     {
/*  315: 682 */       this.m_ndc = 0.0D;
/*  316: 683 */       this.m_numClasses = 1;
/*  317: 684 */       this.m_nda = new double[this.m_numAttribs];
/*  318: 685 */       this.m_ndcda = new double[this.m_numAttribs];
/*  319:     */     }
/*  320: 688 */     if (this.m_weightByDistance)
/*  321:     */     {
/*  322: 690 */       this.m_weightsByRank = new double[this.m_Knn];
/*  323: 692 */       for (int i = 0; i < this.m_Knn; i++) {
/*  324: 693 */         this.m_weightsByRank[i] = Math.exp(-(i / this.m_sigma * (i / this.m_sigma)));
/*  325:     */       }
/*  326:     */     }
/*  327: 699 */     this.m_weights = new double[this.m_numAttribs];
/*  328:     */     
/*  329:     */ 
/*  330: 702 */     this.m_karray = new double[this.m_numClasses][this.m_Knn][2];
/*  331: 704 */     if (!this.m_numericClass)
/*  332:     */     {
/*  333: 705 */       this.m_classProbs = new double[this.m_numClasses];
/*  334: 707 */       for (int i = 0; i < this.m_numInstances; i++) {
/*  335: 708 */         this.m_classProbs[((int)this.m_trainInstances.instance(i).value(this.m_classIndex))] += 1.0D;
/*  336:     */       }
/*  337: 711 */       for (int i = 0; i < this.m_numClasses; i++) {
/*  338: 712 */         this.m_classProbs[i] /= this.m_numInstances;
/*  339:     */       }
/*  340:     */     }
/*  341: 716 */     this.m_worst = new double[this.m_numClasses];
/*  342: 717 */     this.m_index = new int[this.m_numClasses];
/*  343: 718 */     this.m_stored = new int[this.m_numClasses];
/*  344: 719 */     this.m_minArray = new double[this.m_numAttribs];
/*  345: 720 */     this.m_maxArray = new double[this.m_numAttribs];
/*  346: 722 */     for (int i = 0; i < this.m_numAttribs; i++)
/*  347:     */     {
/*  348: 723 */       double tmp413_410 = (0.0D / 0.0D);this.m_maxArray[i] = tmp413_410;this.m_minArray[i] = tmp413_410;
/*  349:     */     }
/*  350: 726 */     for (int i = 0; i < this.m_numInstances; i++) {
/*  351: 727 */       updateMinMax(this.m_trainInstances.instance(i));
/*  352:     */     }
/*  353:     */     int totalInstances;
/*  354:     */     int totalInstances;
/*  355: 730 */     if ((this.m_sampleM > this.m_numInstances) || (this.m_sampleM < 0)) {
/*  356: 731 */       totalInstances = this.m_numInstances;
/*  357:     */     } else {
/*  358: 733 */       totalInstances = this.m_sampleM;
/*  359:     */     }
/*  360: 737 */     for (int i = 0; i < totalInstances; i++)
/*  361:     */     {
/*  362:     */       int z;
/*  363:     */       int z;
/*  364: 738 */       if (totalInstances == this.m_numInstances) {
/*  365: 739 */         z = i;
/*  366:     */       } else {
/*  367: 741 */         z = r.nextInt() % this.m_numInstances;
/*  368:     */       }
/*  369: 744 */       if (z < 0) {
/*  370: 745 */         z *= -1;
/*  371:     */       }
/*  372: 748 */       if (!this.m_trainInstances.instance(z).isMissing(this.m_classIndex))
/*  373:     */       {
/*  374: 750 */         for (int j = 0; j < this.m_numClasses; j++)
/*  375:     */         {
/*  376: 751 */           int tmp571_570 = 0;this.m_stored[j] = tmp571_570;this.m_index[j] = tmp571_570;
/*  377: 753 */           for (int k = 0; k < this.m_Knn; k++)
/*  378:     */           {
/*  379: 754 */             double tmp609_608 = 0.0D;this.m_karray[j][k][1] = tmp609_608;this.m_karray[j][k][0] = tmp609_608;
/*  380:     */           }
/*  381:     */         }
/*  382: 758 */         findKHitMiss(z);
/*  383: 760 */         if (this.m_numericClass) {
/*  384: 761 */           updateWeightsNumericClass(z);
/*  385:     */         } else {
/*  386: 763 */           updateWeightsDiscreteClass(z);
/*  387:     */         }
/*  388:     */       }
/*  389:     */     }
/*  390: 771 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  391: 772 */       if (i != this.m_classIndex) {
/*  392: 773 */         if (this.m_numericClass) {
/*  393: 774 */           this.m_weights[i] = (this.m_ndcda[i] / this.m_ndc - (this.m_nda[i] - this.m_ndcda[i]) / (totalInstances - this.m_ndc));
/*  394:     */         } else {
/*  395: 777 */           this.m_weights[i] *= 1.0D / totalInstances;
/*  396:     */         }
/*  397:     */       }
/*  398:     */     }
/*  399:     */   }
/*  400:     */   
/*  401:     */   public double evaluateAttribute(int attribute)
/*  402:     */     throws Exception
/*  403:     */   {
/*  404: 794 */     return this.m_weights[attribute];
/*  405:     */   }
/*  406:     */   
/*  407:     */   protected void resetOptions()
/*  408:     */   {
/*  409: 801 */     this.m_trainInstances = null;
/*  410: 802 */     this.m_sampleM = -1;
/*  411: 803 */     this.m_Knn = 10;
/*  412: 804 */     this.m_sigma = 2;
/*  413: 805 */     this.m_weightByDistance = false;
/*  414: 806 */     this.m_seed = 1;
/*  415:     */   }
/*  416:     */   
/*  417:     */   private double norm(double x, int i)
/*  418:     */   {
/*  419: 817 */     if ((Double.isNaN(this.m_minArray[i])) || (Utils.eq(this.m_maxArray[i], this.m_minArray[i]))) {
/*  420: 818 */       return 0.0D;
/*  421:     */     }
/*  422: 820 */     return (x - this.m_minArray[i]) / (this.m_maxArray[i] - this.m_minArray[i]);
/*  423:     */   }
/*  424:     */   
/*  425:     */   private void updateMinMax(Instance instance)
/*  426:     */   {
/*  427:     */     try
/*  428:     */     {
/*  429: 833 */       for (int j = 0; j < instance.numValues(); j++) {
/*  430: 834 */         if ((instance.attributeSparse(j).isNumeric()) && (!instance.isMissingSparse(j))) {
/*  431: 836 */           if (Double.isNaN(this.m_minArray[instance.index(j)]))
/*  432:     */           {
/*  433: 837 */             this.m_minArray[instance.index(j)] = instance.valueSparse(j);
/*  434: 838 */             this.m_maxArray[instance.index(j)] = instance.valueSparse(j);
/*  435:     */           }
/*  436: 840 */           else if (instance.valueSparse(j) < this.m_minArray[instance.index(j)])
/*  437:     */           {
/*  438: 841 */             this.m_minArray[instance.index(j)] = instance.valueSparse(j);
/*  439:     */           }
/*  440: 843 */           else if (instance.valueSparse(j) > this.m_maxArray[instance.index(j)])
/*  441:     */           {
/*  442: 844 */             this.m_maxArray[instance.index(j)] = instance.valueSparse(j);
/*  443:     */           }
/*  444:     */         }
/*  445:     */       }
/*  446:     */     }
/*  447:     */     catch (Exception ex)
/*  448:     */     {
/*  449: 851 */       System.err.println(ex);
/*  450: 852 */       ex.printStackTrace();
/*  451:     */     }
/*  452:     */   }
/*  453:     */   
/*  454:     */   private double difference(int index, double val1, double val2)
/*  455:     */   {
/*  456: 861 */     switch (this.m_trainInstances.attribute(index).type())
/*  457:     */     {
/*  458:     */     case 1: 
/*  459: 865 */       if ((Utils.isMissingValue(val1)) || (Utils.isMissingValue(val2))) {
/*  460: 866 */         return 1.0D - 1.0D / this.m_trainInstances.attribute(index).numValues();
/*  461:     */       }
/*  462: 867 */       if ((int)val1 != (int)val2) {
/*  463: 868 */         return 1.0D;
/*  464:     */       }
/*  465: 870 */       return 0.0D;
/*  466:     */     case 0: 
/*  467: 875 */       if ((Utils.isMissingValue(val1)) || (Utils.isMissingValue(val2)))
/*  468:     */       {
/*  469: 876 */         if ((Utils.isMissingValue(val1)) && (Utils.isMissingValue(val2))) {
/*  470: 877 */           return 1.0D;
/*  471:     */         }
/*  472:     */         double diff;
/*  473:     */         double diff;
/*  474: 880 */         if (Utils.isMissingValue(val2)) {
/*  475: 881 */           diff = norm(val1, index);
/*  476:     */         } else {
/*  477: 883 */           diff = norm(val2, index);
/*  478:     */         }
/*  479: 885 */         if (diff < 0.5D) {
/*  480: 886 */           diff = 1.0D - diff;
/*  481:     */         }
/*  482: 888 */         return diff;
/*  483:     */       }
/*  484: 891 */       return Math.abs(norm(val1, index) - norm(val2, index));
/*  485:     */     }
/*  486: 894 */     return 0.0D;
/*  487:     */   }
/*  488:     */   
/*  489:     */   private double distance(Instance first, Instance second)
/*  490:     */   {
/*  491: 907 */     double distance = 0.0D;
/*  492:     */     
/*  493:     */ 
/*  494: 910 */     int p1 = 0;
/*  495: 910 */     for (int p2 = 0; (p1 < first.numValues()) || (p2 < second.numValues());)
/*  496:     */     {
/*  497:     */       int firstI;
/*  498:     */       int firstI;
/*  499: 911 */       if (p1 >= first.numValues()) {
/*  500: 912 */         firstI = this.m_trainInstances.numAttributes();
/*  501:     */       } else {
/*  502: 914 */         firstI = first.index(p1);
/*  503:     */       }
/*  504:     */       int secondI;
/*  505:     */       int secondI;
/*  506: 916 */       if (p2 >= second.numValues()) {
/*  507: 917 */         secondI = this.m_trainInstances.numAttributes();
/*  508:     */       } else {
/*  509: 919 */         secondI = second.index(p2);
/*  510:     */       }
/*  511: 921 */       if (firstI == this.m_trainInstances.classIndex())
/*  512:     */       {
/*  513: 922 */         p1++;
/*  514:     */       }
/*  515: 925 */       else if (secondI == this.m_trainInstances.classIndex())
/*  516:     */       {
/*  517: 926 */         p2++;
/*  518:     */       }
/*  519:     */       else
/*  520:     */       {
/*  521:     */         double diff;
/*  522: 930 */         if (firstI == secondI)
/*  523:     */         {
/*  524: 931 */           double diff = difference(firstI, first.valueSparse(p1), second.valueSparse(p2));
/*  525: 932 */           p1++;
/*  526: 933 */           p2++;
/*  527:     */         }
/*  528: 934 */         else if (firstI > secondI)
/*  529:     */         {
/*  530: 935 */           double diff = difference(secondI, 0.0D, second.valueSparse(p2));
/*  531: 936 */           p2++;
/*  532:     */         }
/*  533:     */         else
/*  534:     */         {
/*  535: 938 */           diff = difference(firstI, first.valueSparse(p1), 0.0D);
/*  536: 939 */           p1++;
/*  537:     */         }
/*  538: 942 */         distance += diff;
/*  539:     */       }
/*  540:     */     }
/*  541: 946 */     return distance;
/*  542:     */   }
/*  543:     */   
/*  544:     */   private void updateWeightsNumericClass(int instNum)
/*  545:     */   {
/*  546: 957 */     int[] tempSorted = null;
/*  547: 958 */     double[] tempDist = null;
/*  548: 959 */     double distNorm = 1.0D;
/*  549:     */     
/*  550:     */ 
/*  551: 962 */     Instance inst = this.m_trainInstances.instance(instNum);
/*  552: 965 */     if (this.m_weightByDistance)
/*  553:     */     {
/*  554: 966 */       tempDist = new double[this.m_stored[0]];
/*  555:     */       
/*  556: 968 */       int j = 0;
/*  557: 968 */       for (distNorm = 0.0D; j < this.m_stored[0]; j++)
/*  558:     */       {
/*  559: 970 */         tempDist[j] = this.m_karray[0][j][0];
/*  560:     */         
/*  561: 972 */         distNorm += this.m_weightsByRank[j];
/*  562:     */       }
/*  563: 975 */       tempSorted = Utils.sort(tempDist);
/*  564:     */     }
/*  565:     */     double temp;
/*  566:     */     Instance cmp;
/*  567:     */     double temp_diffP_diffA_givNearest;
/*  568:     */     int p1;
/*  569:     */     int p2;
/*  570: 978 */     for (int i = 0; i < this.m_stored[0]; i++)
/*  571:     */     {
/*  572: 980 */       if (this.m_weightByDistance)
/*  573:     */       {
/*  574: 981 */         double temp = difference(this.m_classIndex, inst.value(this.m_classIndex), this.m_trainInstances.instance((int)this.m_karray[0][tempSorted[i]][1]).value(this.m_classIndex));
/*  575:     */         
/*  576:     */ 
/*  577:     */ 
/*  578:     */ 
/*  579: 986 */         temp *= this.m_weightsByRank[i] / distNorm;
/*  580:     */       }
/*  581:     */       else
/*  582:     */       {
/*  583: 988 */         temp = difference(this.m_classIndex, inst.value(this.m_classIndex), this.m_trainInstances.instance((int)this.m_karray[0][i][1]).value(this.m_classIndex));
/*  584:     */         
/*  585:     */ 
/*  586: 991 */         temp *= 1.0D / this.m_stored[0];
/*  587:     */       }
/*  588: 994 */       this.m_ndc += temp;
/*  589:     */       
/*  590:     */ 
/*  591: 997 */       cmp = this.m_weightByDistance ? this.m_trainInstances.instance((int)this.m_karray[0][tempSorted[i]][1]) : this.m_trainInstances.instance((int)this.m_karray[0][i][1]);
/*  592:     */       
/*  593:     */ 
/*  594:     */ 
/*  595:1001 */       temp_diffP_diffA_givNearest = difference(this.m_classIndex, inst.value(this.m_classIndex), cmp.value(this.m_classIndex));
/*  596:     */       
/*  597:     */ 
/*  598:1004 */       p1 = 0;
/*  599:1004 */       for (p2 = 0; (p1 < inst.numValues()) || (p2 < cmp.numValues());)
/*  600:     */       {
/*  601:     */         int firstI;
/*  602:     */         int firstI;
/*  603:1005 */         if (p1 >= inst.numValues()) {
/*  604:1006 */           firstI = this.m_trainInstances.numAttributes();
/*  605:     */         } else {
/*  606:1008 */           firstI = inst.index(p1);
/*  607:     */         }
/*  608:     */         int secondI;
/*  609:     */         int secondI;
/*  610:1010 */         if (p2 >= cmp.numValues()) {
/*  611:1011 */           secondI = this.m_trainInstances.numAttributes();
/*  612:     */         } else {
/*  613:1013 */           secondI = cmp.index(p2);
/*  614:     */         }
/*  615:1015 */         if (firstI == this.m_trainInstances.classIndex())
/*  616:     */         {
/*  617:1016 */           p1++;
/*  618:     */         }
/*  619:1019 */         else if (secondI == this.m_trainInstances.classIndex())
/*  620:     */         {
/*  621:1020 */           p2++;
/*  622:     */         }
/*  623:     */         else
/*  624:     */         {
/*  625:1023 */           temp = 0.0D;
/*  626:1024 */           double temp2 = 0.0D;
/*  627:     */           int j;
/*  628:1026 */           if (firstI == secondI)
/*  629:     */           {
/*  630:1027 */             int j = firstI;
/*  631:1028 */             temp = difference(j, inst.valueSparse(p1), cmp.valueSparse(p2));
/*  632:1029 */             p1++;
/*  633:1030 */             p2++;
/*  634:     */           }
/*  635:1031 */           else if (firstI > secondI)
/*  636:     */           {
/*  637:1032 */             int j = secondI;
/*  638:1033 */             temp = difference(j, 0.0D, cmp.valueSparse(p2));
/*  639:1034 */             p2++;
/*  640:     */           }
/*  641:     */           else
/*  642:     */           {
/*  643:1036 */             j = firstI;
/*  644:1037 */             temp = difference(j, inst.valueSparse(p1), 0.0D);
/*  645:1038 */             p1++;
/*  646:     */           }
/*  647:1041 */           temp2 = temp_diffP_diffA_givNearest * temp;
/*  648:1044 */           if (this.m_weightByDistance) {
/*  649:1045 */             temp2 *= this.m_weightsByRank[i] / distNorm;
/*  650:     */           } else {
/*  651:1047 */             temp2 *= 1.0D / this.m_stored[0];
/*  652:     */           }
/*  653:1050 */           this.m_ndcda[j] += temp2;
/*  654:1053 */           if (this.m_weightByDistance) {
/*  655:1054 */             temp *= this.m_weightsByRank[i] / distNorm;
/*  656:     */           } else {
/*  657:1056 */             temp *= 1.0D / this.m_stored[0];
/*  658:     */           }
/*  659:1059 */           this.m_nda[j] += temp;
/*  660:     */         }
/*  661:     */       }
/*  662:     */     }
/*  663:     */   }
/*  664:     */   
/*  665:     */   private void updateWeightsDiscreteClass(int instNum)
/*  666:     */   {
/*  667:1072 */     double w_norm = 1.0D;
/*  668:     */     
/*  669:1074 */     int[] tempSortedClass = null;
/*  670:1075 */     double distNormClass = 1.0D;
/*  671:     */     
/*  672:1077 */     int[][] tempSortedAtt = (int[][])null;
/*  673:1078 */     double[] distNormAtt = null;
/*  674:     */     
/*  675:     */ 
/*  676:     */ 
/*  677:1082 */     Instance inst = this.m_trainInstances.instance(instNum);
/*  678:     */     
/*  679:     */ 
/*  680:1085 */     int cl = (int)this.m_trainInstances.instance(instNum).value(this.m_classIndex);
/*  681:1088 */     if (this.m_weightByDistance)
/*  682:     */     {
/*  683:1091 */       double[] tempDistClass = new double[this.m_stored[cl]];
/*  684:     */       
/*  685:1093 */       int j = 0;
/*  686:1093 */       for (distNormClass = 0.0D; j < this.m_stored[cl]; j++)
/*  687:     */       {
/*  688:1095 */         tempDistClass[j] = this.m_karray[cl][j][0];
/*  689:     */         
/*  690:1097 */         distNormClass += this.m_weightsByRank[j];
/*  691:     */       }
/*  692:1100 */       tempSortedClass = Utils.sort(tempDistClass);
/*  693:     */       
/*  694:1102 */       tempSortedAtt = new int[this.m_numClasses][1];
/*  695:1103 */       distNormAtt = new double[this.m_numClasses];
/*  696:1105 */       for (int k = 0; k < this.m_numClasses; k++) {
/*  697:1106 */         if (k != cl)
/*  698:     */         {
/*  699:1109 */           double[] tempDistAtt = new double[this.m_stored[k]];
/*  700:     */           
/*  701:1111 */           j = 0;
/*  702:1111 */           for (distNormAtt[k] = 0.0D; j < this.m_stored[k]; j++)
/*  703:     */           {
/*  704:1113 */             tempDistAtt[j] = this.m_karray[k][j][0];
/*  705:     */             
/*  706:1115 */             distNormAtt[k] += this.m_weightsByRank[j];
/*  707:     */           }
/*  708:1118 */           tempSortedAtt[k] = Utils.sort(tempDistAtt);
/*  709:     */         }
/*  710:     */       }
/*  711:     */     }
/*  712:1123 */     if (this.m_numClasses > 2) {
/*  713:1126 */       w_norm = 1.0D - this.m_classProbs[cl];
/*  714:     */     }
/*  715:1130 */     int j = 0;
/*  716:     */     Instance cmp;
/*  717:     */     int p1;
/*  718:     */     int p2;
/*  719:1130 */     for (double temp_diff = 0.0D; j < this.m_stored[cl]; j++)
/*  720:     */     {
/*  721:1132 */       cmp = this.m_weightByDistance ? this.m_trainInstances.instance((int)this.m_karray[cl][tempSortedClass[j]][1]) : this.m_trainInstances.instance((int)this.m_karray[cl][j][1]);
/*  722:     */       
/*  723:     */ 
/*  724:     */ 
/*  725:1136 */       p1 = 0;
/*  726:1136 */       for (p2 = 0; (p1 < inst.numValues()) || (p2 < cmp.numValues());)
/*  727:     */       {
/*  728:     */         int firstI;
/*  729:     */         int firstI;
/*  730:1137 */         if (p1 >= inst.numValues()) {
/*  731:1138 */           firstI = this.m_trainInstances.numAttributes();
/*  732:     */         } else {
/*  733:1140 */           firstI = inst.index(p1);
/*  734:     */         }
/*  735:     */         int secondI;
/*  736:     */         int secondI;
/*  737:1142 */         if (p2 >= cmp.numValues()) {
/*  738:1143 */           secondI = this.m_trainInstances.numAttributes();
/*  739:     */         } else {
/*  740:1145 */           secondI = cmp.index(p2);
/*  741:     */         }
/*  742:1147 */         if (firstI == this.m_trainInstances.classIndex())
/*  743:     */         {
/*  744:1148 */           p1++;
/*  745:     */         }
/*  746:1151 */         else if (secondI == this.m_trainInstances.classIndex())
/*  747:     */         {
/*  748:1152 */           p2++;
/*  749:     */         }
/*  750:     */         else
/*  751:     */         {
/*  752:     */           int i;
/*  753:1155 */           if (firstI == secondI)
/*  754:     */           {
/*  755:1156 */             int i = firstI;
/*  756:1157 */             temp_diff = difference(i, inst.valueSparse(p1), cmp.valueSparse(p2));
/*  757:1158 */             p1++;
/*  758:1159 */             p2++;
/*  759:     */           }
/*  760:1160 */           else if (firstI > secondI)
/*  761:     */           {
/*  762:1161 */             int i = secondI;
/*  763:1162 */             temp_diff = difference(i, 0.0D, cmp.valueSparse(p2));
/*  764:1163 */             p2++;
/*  765:     */           }
/*  766:     */           else
/*  767:     */           {
/*  768:1165 */             i = firstI;
/*  769:1166 */             temp_diff = difference(i, inst.valueSparse(p1), 0.0D);
/*  770:1167 */             p1++;
/*  771:     */           }
/*  772:1170 */           if (this.m_weightByDistance) {
/*  773:1171 */             temp_diff *= this.m_weightsByRank[j] / distNormClass;
/*  774:1173 */           } else if (this.m_stored[cl] > 0) {
/*  775:1174 */             temp_diff /= this.m_stored[cl];
/*  776:     */           }
/*  777:1177 */           this.m_weights[i] -= temp_diff;
/*  778:     */         }
/*  779:     */       }
/*  780:     */     }
/*  781:1183 */     temp_diff = 0.0D;
/*  782:1185 */     for (int k = 0; k < this.m_numClasses; k++) {
/*  783:1186 */       if (k != cl) {
/*  784:1188 */         for (j = 0; j < this.m_stored[k]; j++)
/*  785:     */         {
/*  786:1190 */           Instance cmp = this.m_weightByDistance ? this.m_trainInstances.instance((int)this.m_karray[k][tempSortedAtt[k][j]][1]) : this.m_trainInstances.instance((int)this.m_karray[k][j][1]);
/*  787:     */           
/*  788:     */ 
/*  789:     */ 
/*  790:1194 */           int p1 = 0;int p2 = 0;
/*  791:1195 */           while ((p1 < inst.numValues()) || (p2 < cmp.numValues()))
/*  792:     */           {
/*  793:     */             int firstI;
/*  794:     */             int firstI;
/*  795:1196 */             if (p1 >= inst.numValues()) {
/*  796:1197 */               firstI = this.m_trainInstances.numAttributes();
/*  797:     */             } else {
/*  798:1199 */               firstI = inst.index(p1);
/*  799:     */             }
/*  800:     */             int secondI;
/*  801:     */             int secondI;
/*  802:1201 */             if (p2 >= cmp.numValues()) {
/*  803:1202 */               secondI = this.m_trainInstances.numAttributes();
/*  804:     */             } else {
/*  805:1204 */               secondI = cmp.index(p2);
/*  806:     */             }
/*  807:1206 */             if (firstI == this.m_trainInstances.classIndex())
/*  808:     */             {
/*  809:1207 */               p1++;
/*  810:     */             }
/*  811:1210 */             else if (secondI == this.m_trainInstances.classIndex())
/*  812:     */             {
/*  813:1211 */               p2++;
/*  814:     */             }
/*  815:     */             else
/*  816:     */             {
/*  817:     */               int i;
/*  818:1214 */               if (firstI == secondI)
/*  819:     */               {
/*  820:1215 */                 int i = firstI;
/*  821:1216 */                 temp_diff = difference(i, inst.valueSparse(p1), cmp.valueSparse(p2));
/*  822:     */                 
/*  823:1218 */                 p1++;
/*  824:1219 */                 p2++;
/*  825:     */               }
/*  826:1220 */               else if (firstI > secondI)
/*  827:     */               {
/*  828:1221 */                 int i = secondI;
/*  829:1222 */                 temp_diff = difference(i, 0.0D, cmp.valueSparse(p2));
/*  830:1223 */                 p2++;
/*  831:     */               }
/*  832:     */               else
/*  833:     */               {
/*  834:1225 */                 i = firstI;
/*  835:1226 */                 temp_diff = difference(i, inst.valueSparse(p1), 0.0D);
/*  836:1227 */                 p1++;
/*  837:     */               }
/*  838:1230 */               if (this.m_weightByDistance) {
/*  839:1231 */                 temp_diff *= this.m_weightsByRank[j] / distNormAtt[k];
/*  840:1233 */               } else if (this.m_stored[k] > 0) {
/*  841:1234 */                 temp_diff /= this.m_stored[k];
/*  842:     */               }
/*  843:1237 */               if (this.m_numClasses > 2) {
/*  844:1238 */                 this.m_weights[i] += this.m_classProbs[k] / w_norm * temp_diff;
/*  845:     */               } else {
/*  846:1240 */                 this.m_weights[i] += temp_diff;
/*  847:     */               }
/*  848:     */             }
/*  849:     */           }
/*  850:     */         }
/*  851:     */       }
/*  852:     */     }
/*  853:     */   }
/*  854:     */   
/*  855:     */   private void findKHitMiss(int instNum)
/*  856:     */   {
/*  857:1259 */     double temp_diff = 0.0D;
/*  858:1260 */     Instance thisInst = this.m_trainInstances.instance(instNum);
/*  859:1262 */     for (int i = 0; i < this.m_numInstances; i++) {
/*  860:1263 */       if (i != instNum)
/*  861:     */       {
/*  862:1264 */         Instance cmpInst = this.m_trainInstances.instance(i);
/*  863:1265 */         temp_diff = distance(cmpInst, thisInst);
/*  864:     */         int cl;
/*  865:     */         int cl;
/*  866:1268 */         if (this.m_numericClass) {
/*  867:1269 */           cl = 0;
/*  868:     */         } else {
/*  869:1271 */           cl = (int)this.m_trainInstances.instance(i).value(this.m_classIndex);
/*  870:     */         }
/*  871:1275 */         if (this.m_stored[cl] < this.m_Knn)
/*  872:     */         {
/*  873:1276 */           this.m_karray[cl][this.m_stored[cl]][0] = temp_diff;
/*  874:1277 */           this.m_karray[cl][this.m_stored[cl]][1] = i;
/*  875:1278 */           this.m_stored[cl] += 1;
/*  876:     */           
/*  877:     */ 
/*  878:1281 */           int j = 0;
/*  879:1281 */           for (double ww = -1.0D; j < this.m_stored[cl]; j++) {
/*  880:1282 */             if (this.m_karray[cl][j][0] > ww)
/*  881:     */             {
/*  882:1283 */               ww = this.m_karray[cl][j][0];
/*  883:1284 */               this.m_index[cl] = j;
/*  884:     */             }
/*  885:     */           }
/*  886:1288 */           this.m_worst[cl] = ww;
/*  887:     */         }
/*  888:1295 */         else if (temp_diff < this.m_karray[cl][this.m_index[cl]][0])
/*  889:     */         {
/*  890:1296 */           this.m_karray[cl][this.m_index[cl]][0] = temp_diff;
/*  891:1297 */           this.m_karray[cl][this.m_index[cl]][1] = i;
/*  892:     */           
/*  893:1299 */           int j = 0;
/*  894:1299 */           for (double ww = -1.0D; j < this.m_stored[cl]; j++) {
/*  895:1300 */             if (this.m_karray[cl][j][0] > ww)
/*  896:     */             {
/*  897:1301 */               ww = this.m_karray[cl][j][0];
/*  898:1302 */               this.m_index[cl] = j;
/*  899:     */             }
/*  900:     */           }
/*  901:1306 */           this.m_worst[cl] = ww;
/*  902:     */         }
/*  903:     */       }
/*  904:     */     }
/*  905:     */   }
/*  906:     */   
/*  907:     */   public String getRevision()
/*  908:     */   {
/*  909:1320 */     return RevisionUtils.extract("$Revision: 11217 $");
/*  910:     */   }
/*  911:     */   
/*  912:     */   public int[] postProcess(int[] attributeSet)
/*  913:     */   {
/*  914:1327 */     this.m_trainInstances = new Instances(this.m_trainInstances, 0);
/*  915:     */     
/*  916:1329 */     return attributeSet;
/*  917:     */   }
/*  918:     */   
/*  919:     */   public static void main(String[] args)
/*  920:     */   {
/*  921:1341 */     runEvaluator(new ReliefFAttributeEval(), args);
/*  922:     */   }
/*  923:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.ReliefFAttributeEval
 * JD-Core Version:    0.7.0.1
 */