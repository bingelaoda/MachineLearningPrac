/*    1:     */ package weka.filters.supervised.attribute;
/*    2:     */ 
/*    3:     */ import java.util.ArrayList;
/*    4:     */ import java.util.Enumeration;
/*    5:     */ import java.util.HashSet;
/*    6:     */ import java.util.List;
/*    7:     */ import java.util.Set;
/*    8:     */ import java.util.Vector;
/*    9:     */ import weka.core.Attribute;
/*   10:     */ import weka.core.Capabilities;
/*   11:     */ import weka.core.Capabilities.Capability;
/*   12:     */ import weka.core.ContingencyTables;
/*   13:     */ import weka.core.DenseInstance;
/*   14:     */ import weka.core.Instance;
/*   15:     */ import weka.core.Instances;
/*   16:     */ import weka.core.Option;
/*   17:     */ import weka.core.OptionHandler;
/*   18:     */ import weka.core.Range;
/*   19:     */ import weka.core.RevisionUtils;
/*   20:     */ import weka.core.SparseInstance;
/*   21:     */ import weka.core.SpecialFunctions;
/*   22:     */ import weka.core.TechnicalInformation;
/*   23:     */ import weka.core.TechnicalInformation.Field;
/*   24:     */ import weka.core.TechnicalInformation.Type;
/*   25:     */ import weka.core.TechnicalInformationHandler;
/*   26:     */ import weka.core.Utils;
/*   27:     */ import weka.core.WeightedInstancesHandler;
/*   28:     */ import weka.filters.Filter;
/*   29:     */ import weka.filters.SupervisedFilter;
/*   30:     */ 
/*   31:     */ public class Discretize
/*   32:     */   extends Filter
/*   33:     */   implements SupervisedFilter, OptionHandler, WeightedInstancesHandler, TechnicalInformationHandler
/*   34:     */ {
/*   35:     */   static final long serialVersionUID = -3141006402280129097L;
/*   36: 129 */   protected Range m_DiscretizeCols = new Range();
/*   37: 132 */   protected double[][] m_CutPoints = (double[][])null;
/*   38: 135 */   protected boolean m_MakeBinary = false;
/*   39: 138 */   protected boolean m_UseBinNumbers = false;
/*   40: 141 */   protected boolean m_UseBetterEncoding = false;
/*   41: 144 */   protected boolean m_UseKononenko = false;
/*   42: 147 */   protected int m_BinRangePrecision = 6;
/*   43:     */   
/*   44:     */   public Discretize()
/*   45:     */   {
/*   46: 152 */     setAttributeIndices("first-last");
/*   47:     */   }
/*   48:     */   
/*   49:     */   public Enumeration<Option> listOptions()
/*   50:     */   {
/*   51: 163 */     Vector<Option> newVector = new Vector(6);
/*   52:     */     
/*   53: 165 */     newVector.addElement(new Option("\tSpecifies list of columns to Discretize. First and last are valid indexes.\n\t(default none)", "R", 1, "-R <col1,col2-col4,...>"));
/*   54:     */     
/*   55:     */ 
/*   56:     */ 
/*   57:     */ 
/*   58: 170 */     newVector.addElement(new Option("\tInvert matching sense of column indexes.", "V", 0, "-V"));
/*   59:     */     
/*   60:     */ 
/*   61: 173 */     newVector.addElement(new Option("\tOutput binary attributes for discretized attributes.", "D", 0, "-D"));
/*   62:     */     
/*   63:     */ 
/*   64: 176 */     newVector.addElement(new Option("\tUse bin numbers rather than ranges for discretized attributes.", "Y", 0, "-Y"));
/*   65:     */     
/*   66:     */ 
/*   67:     */ 
/*   68: 180 */     newVector.addElement(new Option("\tUse better encoding of split point for MDL.", "E", 0, "-E"));
/*   69:     */     
/*   70:     */ 
/*   71: 183 */     newVector.addElement(new Option("\tUse Kononenko's MDL criterion.", "K", 0, "-K"));
/*   72:     */     
/*   73:     */ 
/*   74: 186 */     newVector.addElement(new Option("\tPrecision for bin boundary labels.\n\t(default = 6 decimal places).", "precision", 1, "-precision <integer>"));
/*   75:     */     
/*   76:     */ 
/*   77:     */ 
/*   78:     */ 
/*   79: 191 */     return newVector.elements();
/*   80:     */   }
/*   81:     */   
/*   82:     */   public void setOptions(String[] options)
/*   83:     */     throws Exception
/*   84:     */   {
/*   85: 232 */     setMakeBinary(Utils.getFlag('D', options));
/*   86: 233 */     setUseBinNumbers(Utils.getFlag('Y', options));
/*   87: 234 */     setUseBetterEncoding(Utils.getFlag('E', options));
/*   88: 235 */     setUseKononenko(Utils.getFlag('K', options));
/*   89: 236 */     setInvertSelection(Utils.getFlag('V', options));
/*   90:     */     
/*   91: 238 */     String convertList = Utils.getOption('R', options);
/*   92: 239 */     if (convertList.length() != 0) {
/*   93: 240 */       setAttributeIndices(convertList);
/*   94:     */     } else {
/*   95: 242 */       setAttributeIndices("first-last");
/*   96:     */     }
/*   97: 245 */     String precisionS = Utils.getOption("precision", options);
/*   98: 246 */     if (precisionS.length() > 0) {
/*   99: 247 */       setBinRangePrecision(Integer.parseInt(precisionS));
/*  100:     */     }
/*  101: 250 */     if (getInputFormat() != null) {
/*  102: 251 */       setInputFormat(getInputFormat());
/*  103:     */     }
/*  104: 254 */     Utils.checkForRemainingOptions(options);
/*  105:     */   }
/*  106:     */   
/*  107:     */   public String[] getOptions()
/*  108:     */   {
/*  109: 265 */     List<String> options = new ArrayList();
/*  110: 267 */     if (getMakeBinary()) {
/*  111: 268 */       options.add("-D");
/*  112:     */     }
/*  113: 270 */     if (getUseBinNumbers()) {
/*  114: 271 */       options.add("-Y");
/*  115:     */     }
/*  116: 273 */     if (getUseBetterEncoding()) {
/*  117: 274 */       options.add("-E");
/*  118:     */     }
/*  119: 276 */     if (getUseKononenko()) {
/*  120: 277 */       options.add("-K");
/*  121:     */     }
/*  122: 279 */     if (getInvertSelection()) {
/*  123: 280 */       options.add("-V");
/*  124:     */     }
/*  125: 282 */     if (!getAttributeIndices().equals(""))
/*  126:     */     {
/*  127: 283 */       options.add("-R");
/*  128: 284 */       options.add(getAttributeIndices());
/*  129:     */     }
/*  130: 287 */     options.add("-precision");
/*  131: 288 */     options.add("" + getBinRangePrecision());
/*  132:     */     
/*  133: 290 */     return (String[])options.toArray(new String[options.size()]);
/*  134:     */   }
/*  135:     */   
/*  136:     */   public Capabilities getCapabilities()
/*  137:     */   {
/*  138: 301 */     Capabilities result = super.getCapabilities();
/*  139: 302 */     result.disableAll();
/*  140:     */     
/*  141:     */ 
/*  142: 305 */     result.enableAllAttributes();
/*  143: 306 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  144:     */     
/*  145:     */ 
/*  146: 309 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  147:     */     
/*  148: 311 */     return result;
/*  149:     */   }
/*  150:     */   
/*  151:     */   public boolean setInputFormat(Instances instanceInfo)
/*  152:     */     throws Exception
/*  153:     */   {
/*  154: 326 */     super.setInputFormat(instanceInfo);
/*  155:     */     
/*  156: 328 */     this.m_DiscretizeCols.setUpper(instanceInfo.numAttributes() - 1);
/*  157: 329 */     this.m_CutPoints = ((double[][])null);
/*  158:     */     
/*  159:     */ 
/*  160:     */ 
/*  161: 333 */     return false;
/*  162:     */   }
/*  163:     */   
/*  164:     */   public boolean input(Instance instance)
/*  165:     */   {
/*  166: 348 */     if (getInputFormat() == null) {
/*  167: 349 */       throw new IllegalStateException("No input instance format defined");
/*  168:     */     }
/*  169: 351 */     if (this.m_NewBatch)
/*  170:     */     {
/*  171: 352 */       resetQueue();
/*  172: 353 */       this.m_NewBatch = false;
/*  173:     */     }
/*  174: 356 */     if (this.m_CutPoints != null)
/*  175:     */     {
/*  176: 357 */       convertInstance(instance);
/*  177: 358 */       return true;
/*  178:     */     }
/*  179: 361 */     bufferInput(instance);
/*  180: 362 */     return false;
/*  181:     */   }
/*  182:     */   
/*  183:     */   public boolean batchFinished()
/*  184:     */   {
/*  185: 376 */     if (getInputFormat() == null) {
/*  186: 377 */       throw new IllegalStateException("No input instance format defined");
/*  187:     */     }
/*  188: 379 */     if (this.m_CutPoints == null)
/*  189:     */     {
/*  190: 380 */       calculateCutPoints();
/*  191:     */       
/*  192: 382 */       setOutputFormat();
/*  193: 387 */       for (int i = 0; i < getInputFormat().numInstances(); i++) {
/*  194: 388 */         convertInstance(getInputFormat().instance(i));
/*  195:     */       }
/*  196:     */     }
/*  197: 391 */     flushInput();
/*  198:     */     
/*  199: 393 */     this.m_NewBatch = true;
/*  200: 394 */     return numPendingOutput() != 0;
/*  201:     */   }
/*  202:     */   
/*  203:     */   public String globalInfo()
/*  204:     */   {
/*  205: 405 */     return "An instance filter that discretizes a range of numeric attributes in the dataset into nominal attributes. Discretization is by Fayyad & Irani's MDL method (the default).\n\nFor more information, see:\n\n" + getTechnicalInformation().toString();
/*  206:     */   }
/*  207:     */   
/*  208:     */   public TechnicalInformation getTechnicalInformation()
/*  209:     */   {
/*  210: 423 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  211: 424 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Usama M. Fayyad and Keki B. Irani");
/*  212: 425 */     result.setValue(TechnicalInformation.Field.TITLE, "Multi-interval discretization of continuousvalued attributes for classification learning");
/*  213:     */     
/*  214:     */ 
/*  215:     */ 
/*  216: 429 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Thirteenth International Joint Conference on Articial Intelligence");
/*  217:     */     
/*  218: 431 */     result.setValue(TechnicalInformation.Field.YEAR, "1993");
/*  219: 432 */     result.setValue(TechnicalInformation.Field.VOLUME, "2");
/*  220: 433 */     result.setValue(TechnicalInformation.Field.PAGES, "1022-1027");
/*  221: 434 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann Publishers");
/*  222:     */     
/*  223: 436 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.INPROCEEDINGS);
/*  224: 437 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Igor Kononenko");
/*  225: 438 */     additional.setValue(TechnicalInformation.Field.TITLE, "On Biases in Estimating Multi-Valued Attributes");
/*  226:     */     
/*  227: 440 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "14th International Joint Conference on Articial Intelligence");
/*  228:     */     
/*  229: 442 */     additional.setValue(TechnicalInformation.Field.YEAR, "1995");
/*  230: 443 */     additional.setValue(TechnicalInformation.Field.PAGES, "1034-1040");
/*  231: 444 */     additional.setValue(TechnicalInformation.Field.PS, "http://ai.fri.uni-lj.si/papers/kononenko95-ijcai.ps.gz");
/*  232:     */     
/*  233:     */ 
/*  234: 447 */     return result;
/*  235:     */   }
/*  236:     */   
/*  237:     */   public String binRangePrecisionTipText()
/*  238:     */   {
/*  239: 457 */     return "The number of decimal places for cut points to use when generating bin labels";
/*  240:     */   }
/*  241:     */   
/*  242:     */   public void setBinRangePrecision(int p)
/*  243:     */   {
/*  244: 468 */     this.m_BinRangePrecision = p;
/*  245:     */   }
/*  246:     */   
/*  247:     */   public int getBinRangePrecision()
/*  248:     */   {
/*  249: 479 */     return this.m_BinRangePrecision;
/*  250:     */   }
/*  251:     */   
/*  252:     */   public String makeBinaryTipText()
/*  253:     */   {
/*  254: 490 */     return "Make resulting attributes binary.";
/*  255:     */   }
/*  256:     */   
/*  257:     */   public boolean getMakeBinary()
/*  258:     */   {
/*  259: 500 */     return this.m_MakeBinary;
/*  260:     */   }
/*  261:     */   
/*  262:     */   public void setMakeBinary(boolean makeBinary)
/*  263:     */   {
/*  264: 510 */     this.m_MakeBinary = makeBinary;
/*  265:     */   }
/*  266:     */   
/*  267:     */   public String useBinNumbersTipText()
/*  268:     */   {
/*  269: 520 */     return "Use bin numbers (eg BXofY) rather than ranges for for discretized attributes";
/*  270:     */   }
/*  271:     */   
/*  272:     */   public boolean getUseBinNumbers()
/*  273:     */   {
/*  274: 531 */     return this.m_UseBinNumbers;
/*  275:     */   }
/*  276:     */   
/*  277:     */   public void setUseBinNumbers(boolean useBinNumbers)
/*  278:     */   {
/*  279: 542 */     this.m_UseBinNumbers = useBinNumbers;
/*  280:     */   }
/*  281:     */   
/*  282:     */   public String useKononenkoTipText()
/*  283:     */   {
/*  284: 553 */     return "Use Kononenko's MDL criterion. If set to false uses the Fayyad & Irani criterion.";
/*  285:     */   }
/*  286:     */   
/*  287:     */   public boolean getUseKononenko()
/*  288:     */   {
/*  289: 564 */     return this.m_UseKononenko;
/*  290:     */   }
/*  291:     */   
/*  292:     */   public void setUseKononenko(boolean useKon)
/*  293:     */   {
/*  294: 574 */     this.m_UseKononenko = useKon;
/*  295:     */   }
/*  296:     */   
/*  297:     */   public String useBetterEncodingTipText()
/*  298:     */   {
/*  299: 585 */     return "Uses a more efficient split point encoding.";
/*  300:     */   }
/*  301:     */   
/*  302:     */   public boolean getUseBetterEncoding()
/*  303:     */   {
/*  304: 595 */     return this.m_UseBetterEncoding;
/*  305:     */   }
/*  306:     */   
/*  307:     */   public void setUseBetterEncoding(boolean useBetterEncoding)
/*  308:     */   {
/*  309: 605 */     this.m_UseBetterEncoding = useBetterEncoding;
/*  310:     */   }
/*  311:     */   
/*  312:     */   public String invertSelectionTipText()
/*  313:     */   {
/*  314: 616 */     return "Set attribute selection mode. If false, only selected (numeric) attributes in the range will be discretized; if true, only non-selected attributes will be discretized.";
/*  315:     */   }
/*  316:     */   
/*  317:     */   public boolean getInvertSelection()
/*  318:     */   {
/*  319: 628 */     return this.m_DiscretizeCols.getInvert();
/*  320:     */   }
/*  321:     */   
/*  322:     */   public void setInvertSelection(boolean invert)
/*  323:     */   {
/*  324: 640 */     this.m_DiscretizeCols.setInvert(invert);
/*  325:     */   }
/*  326:     */   
/*  327:     */   public String attributeIndicesTipText()
/*  328:     */   {
/*  329: 650 */     return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
/*  330:     */   }
/*  331:     */   
/*  332:     */   public String getAttributeIndices()
/*  333:     */   {
/*  334: 663 */     return this.m_DiscretizeCols.getRanges();
/*  335:     */   }
/*  336:     */   
/*  337:     */   public void setAttributeIndices(String rangeList)
/*  338:     */   {
/*  339: 678 */     this.m_DiscretizeCols.setRanges(rangeList);
/*  340:     */   }
/*  341:     */   
/*  342:     */   public void setAttributeIndicesArray(int[] attributes)
/*  343:     */   {
/*  344: 692 */     setAttributeIndices(Range.indicesToRangeList(attributes));
/*  345:     */   }
/*  346:     */   
/*  347:     */   public double[] getCutPoints(int attributeIndex)
/*  348:     */   {
/*  349: 705 */     if (this.m_CutPoints == null) {
/*  350: 706 */       return null;
/*  351:     */     }
/*  352: 708 */     return this.m_CutPoints[attributeIndex];
/*  353:     */   }
/*  354:     */   
/*  355:     */   public String getBinRangesString(int attributeIndex)
/*  356:     */   {
/*  357: 721 */     if (this.m_CutPoints == null) {
/*  358: 722 */       return null;
/*  359:     */     }
/*  360: 725 */     double[] cutPoints = this.m_CutPoints[attributeIndex];
/*  361: 727 */     if (cutPoints == null) {
/*  362: 728 */       return "All";
/*  363:     */     }
/*  364: 731 */     StringBuilder sb = new StringBuilder();
/*  365: 732 */     boolean first = true;
/*  366:     */     
/*  367: 734 */     int j = 0;
/*  368: 734 */     for (int n = cutPoints.length; j <= n; j++)
/*  369:     */     {
/*  370: 735 */       if (first) {
/*  371: 736 */         first = false;
/*  372:     */       } else {
/*  373: 738 */         sb.append(',');
/*  374:     */       }
/*  375: 741 */       sb.append(binRangeString(cutPoints, j, this.m_BinRangePrecision));
/*  376:     */     }
/*  377: 744 */     return sb.toString();
/*  378:     */   }
/*  379:     */   
/*  380:     */   private static String binRangeString(double[] cutPoints, int j, int precision)
/*  381:     */   {
/*  382: 757 */     assert (cutPoints != null);
/*  383:     */     
/*  384: 759 */     int n = cutPoints.length;
/*  385: 760 */     assert ((0 <= j) && (j <= n));
/*  386:     */     
/*  387: 762 */     return "(" + Utils.doubleToString(cutPoints[(j - 1)], precision) + "-" + Utils.doubleToString(cutPoints[j], precision) + "]";
/*  388:     */   }
/*  389:     */   
/*  390:     */   protected void calculateCutPoints()
/*  391:     */   {
/*  392: 772 */     Instances copy = null;
/*  393:     */     
/*  394: 774 */     this.m_CutPoints = new double[getInputFormat().numAttributes()][];
/*  395: 775 */     for (int i = getInputFormat().numAttributes() - 1; i >= 0; i--) {
/*  396: 776 */       if ((this.m_DiscretizeCols.isInRange(i)) && (getInputFormat().attribute(i).isNumeric()))
/*  397:     */       {
/*  398: 780 */         if (copy == null) {
/*  399: 781 */           copy = new Instances(getInputFormat());
/*  400:     */         }
/*  401: 783 */         calculateCutPointsByMDL(i, copy);
/*  402:     */       }
/*  403:     */     }
/*  404:     */   }
/*  405:     */   
/*  406:     */   protected void calculateCutPointsByMDL(int index, Instances data)
/*  407:     */   {
/*  408: 797 */     data.sort(data.attribute(index));
/*  409:     */     
/*  410:     */ 
/*  411: 800 */     int firstMissing = data.numInstances();
/*  412: 801 */     for (int i = 0; i < data.numInstances(); i++) {
/*  413: 802 */       if (data.instance(i).isMissing(index))
/*  414:     */       {
/*  415: 803 */         firstMissing = i;
/*  416: 804 */         break;
/*  417:     */       }
/*  418:     */     }
/*  419: 807 */     this.m_CutPoints[index] = cutPointsForSubset(data, index, 0, firstMissing);
/*  420:     */   }
/*  421:     */   
/*  422:     */   private boolean KononenkosMDL(double[] priorCounts, double[][] bestCounts, double numInstances, int numCutPoints)
/*  423:     */   {
/*  424: 822 */     double distAfter = 0.0D;double instAfter = 0.0D;
/*  425:     */     
/*  426:     */ 
/*  427:     */ 
/*  428:     */ 
/*  429: 827 */     int numClassesTotal = 0;
/*  430: 828 */     for (double priorCount : priorCounts) {
/*  431: 829 */       if (priorCount > 0.0D) {
/*  432: 830 */         numClassesTotal++;
/*  433:     */       }
/*  434:     */     }
/*  435: 835 */     double distPrior = SpecialFunctions.log2Binomial(numInstances + numClassesTotal - 1.0D, numClassesTotal - 1);
/*  436:     */     
/*  437:     */ 
/*  438:     */ 
/*  439: 839 */     double instPrior = SpecialFunctions.log2Multinomial(numInstances, priorCounts);
/*  440:     */     
/*  441: 841 */     double before = instPrior + distPrior;
/*  442: 844 */     for (double[] bestCount : bestCounts)
/*  443:     */     {
/*  444: 845 */       double sum = Utils.sum(bestCount);
/*  445: 846 */       distAfter += SpecialFunctions.log2Binomial(sum + numClassesTotal - 1.0D, numClassesTotal - 1);
/*  446:     */       
/*  447: 848 */       instAfter += SpecialFunctions.log2Multinomial(sum, bestCount);
/*  448:     */     }
/*  449: 852 */     double after = Utils.log2(numCutPoints) + distAfter + instAfter;
/*  450:     */     
/*  451:     */ 
/*  452: 855 */     return before > after;
/*  453:     */   }
/*  454:     */   
/*  455:     */   private boolean FayyadAndIranisMDL(double[] priorCounts, double[][] bestCounts, double numInstances, int numCutPoints)
/*  456:     */   {
/*  457: 875 */     double priorEntropy = ContingencyTables.entropy(priorCounts);
/*  458:     */     
/*  459:     */ 
/*  460: 878 */     double entropy = ContingencyTables.entropyConditionedOnRows(bestCounts);
/*  461:     */     
/*  462:     */ 
/*  463: 881 */     double gain = priorEntropy - entropy;
/*  464:     */     
/*  465:     */ 
/*  466: 884 */     int numClassesTotal = 0;
/*  467: 885 */     for (double priorCount : priorCounts) {
/*  468: 886 */       if (priorCount > 0.0D) {
/*  469: 887 */         numClassesTotal++;
/*  470:     */       }
/*  471:     */     }
/*  472: 892 */     int numClassesLeft = 0;
/*  473: 893 */     for (int i = 0; i < bestCounts[0].length; i++) {
/*  474: 894 */       if (bestCounts[0][i] > 0.0D) {
/*  475: 895 */         numClassesLeft++;
/*  476:     */       }
/*  477:     */     }
/*  478: 900 */     int numClassesRight = 0;
/*  479: 901 */     for (int i = 0; i < bestCounts[1].length; i++) {
/*  480: 902 */       if (bestCounts[1][i] > 0.0D) {
/*  481: 903 */         numClassesRight++;
/*  482:     */       }
/*  483:     */     }
/*  484: 908 */     double entropyLeft = ContingencyTables.entropy(bestCounts[0]);
/*  485: 909 */     double entropyRight = ContingencyTables.entropy(bestCounts[1]);
/*  486:     */     
/*  487:     */ 
/*  488: 912 */     double delta = Utils.log2(Math.pow(3.0D, numClassesTotal) - 2.0D) - (numClassesTotal * priorEntropy - numClassesRight * entropyRight - numClassesLeft * entropyLeft);
/*  489:     */     
/*  490:     */ 
/*  491:     */ 
/*  492: 916 */     return gain > (Utils.log2(numCutPoints) + delta) / numInstances;
/*  493:     */   }
/*  494:     */   
/*  495:     */   private double[] cutPointsForSubset(Instances instances, int attIndex, int first, int lastPlusOne)
/*  496:     */   {
/*  497: 933 */     double currentCutPoint = -1.797693134862316E+308D;double bestCutPoint = -1.0D;
/*  498: 934 */     int bestIndex = -1;int numCutPoints = 0;
/*  499: 935 */     double numInstances = 0.0D;
/*  500: 938 */     if (lastPlusOne - first < 2) {
/*  501: 939 */       return null;
/*  502:     */     }
/*  503: 943 */     double[][] counts = new double[2][instances.numClasses()];
/*  504: 944 */     for (int i = first; i < lastPlusOne; i++)
/*  505:     */     {
/*  506: 945 */       numInstances += instances.instance(i).weight();
/*  507: 946 */       counts[1][((int)instances.instance(i).classValue())] += instances.instance(i).weight();
/*  508:     */     }
/*  509: 951 */     double[] priorCounts = new double[instances.numClasses()];
/*  510: 952 */     System.arraycopy(counts[1], 0, priorCounts, 0, instances.numClasses());
/*  511:     */     
/*  512:     */ 
/*  513: 955 */     double priorEntropy = ContingencyTables.entropy(priorCounts);
/*  514: 956 */     double bestEntropy = priorEntropy;
/*  515:     */     
/*  516:     */ 
/*  517: 959 */     double[][] bestCounts = new double[2][instances.numClasses()];
/*  518: 960 */     for (int i = first; i < lastPlusOne - 1; i++)
/*  519:     */     {
/*  520: 961 */       counts[0][((int)instances.instance(i).classValue())] += instances.instance(i).weight();
/*  521:     */       
/*  522: 963 */       counts[1][((int)instances.instance(i).classValue())] -= instances.instance(i).weight();
/*  523: 965 */       if (instances.instance(i).value(attIndex) < instances.instance(i + 1).value(attIndex))
/*  524:     */       {
/*  525: 967 */         currentCutPoint = (instances.instance(i).value(attIndex) + instances.instance(i + 1).value(attIndex)) / 2.0D;
/*  526:     */         
/*  527: 969 */         double currentEntropy = ContingencyTables.entropyConditionedOnRows(counts);
/*  528: 970 */         if (currentEntropy < bestEntropy)
/*  529:     */         {
/*  530: 971 */           bestCutPoint = currentCutPoint;
/*  531: 972 */           bestEntropy = currentEntropy;
/*  532: 973 */           bestIndex = i;
/*  533: 974 */           System.arraycopy(counts[0], 0, bestCounts[0], 0, instances.numClasses());
/*  534:     */           
/*  535: 976 */           System.arraycopy(counts[1], 0, bestCounts[1], 0, instances.numClasses());
/*  536:     */         }
/*  537: 979 */         numCutPoints++;
/*  538:     */       }
/*  539:     */     }
/*  540: 984 */     if (!this.m_UseBetterEncoding) {
/*  541: 985 */       numCutPoints = lastPlusOne - first - 1;
/*  542:     */     }
/*  543: 989 */     double gain = priorEntropy - bestEntropy;
/*  544: 990 */     if (gain <= 0.0D) {
/*  545: 991 */       return null;
/*  546:     */     }
/*  547: 995 */     if (((this.m_UseKononenko) && (KononenkosMDL(priorCounts, bestCounts, numInstances, numCutPoints))) || ((!this.m_UseKononenko) && (FayyadAndIranisMDL(priorCounts, bestCounts, numInstances, numCutPoints))))
/*  548:     */     {
/*  549:1001 */       double[] left = cutPointsForSubset(instances, attIndex, first, bestIndex + 1);
/*  550:1002 */       double[] right = cutPointsForSubset(instances, attIndex, bestIndex + 1, lastPlusOne);
/*  551:     */       double[] cutPoints;
/*  552:1006 */       if ((left == null) && (right == null))
/*  553:     */       {
/*  554:1007 */         double[] cutPoints = new double[1];
/*  555:1008 */         cutPoints[0] = bestCutPoint;
/*  556:     */       }
/*  557:1009 */       else if (right == null)
/*  558:     */       {
/*  559:1010 */         double[] cutPoints = new double[left.length + 1];
/*  560:1011 */         System.arraycopy(left, 0, cutPoints, 0, left.length);
/*  561:1012 */         cutPoints[left.length] = bestCutPoint;
/*  562:     */       }
/*  563:1013 */       else if (left == null)
/*  564:     */       {
/*  565:1014 */         double[] cutPoints = new double[1 + right.length];
/*  566:1015 */         cutPoints[0] = bestCutPoint;
/*  567:1016 */         System.arraycopy(right, 0, cutPoints, 1, right.length);
/*  568:     */       }
/*  569:     */       else
/*  570:     */       {
/*  571:1018 */         cutPoints = new double[left.length + right.length + 1];
/*  572:1019 */         System.arraycopy(left, 0, cutPoints, 0, left.length);
/*  573:1020 */         cutPoints[left.length] = bestCutPoint;
/*  574:1021 */         System.arraycopy(right, 0, cutPoints, left.length + 1, right.length);
/*  575:     */       }
/*  576:1024 */       return cutPoints;
/*  577:     */     }
/*  578:1026 */     return null;
/*  579:     */   }
/*  580:     */   
/*  581:     */   protected void setOutputFormat()
/*  582:     */   {
/*  583:1036 */     if (this.m_CutPoints == null)
/*  584:     */     {
/*  585:1037 */       setOutputFormat(null);
/*  586:1038 */       return;
/*  587:     */     }
/*  588:1040 */     ArrayList<Attribute> attributes = new ArrayList(getInputFormat().numAttributes());
/*  589:     */     
/*  590:1042 */     int classIndex = getInputFormat().classIndex();
/*  591:1043 */     int i = 0;
/*  592:1043 */     for (int m = getInputFormat().numAttributes(); i < m; i++) {
/*  593:1044 */       if ((this.m_DiscretizeCols.isInRange(i)) && (getInputFormat().attribute(i).isNumeric()))
/*  594:     */       {
/*  595:1047 */         Set<String> cutPointsCheck = new HashSet();
/*  596:1048 */         double[] cutPoints = this.m_CutPoints[i];
/*  597:1049 */         if (!this.m_MakeBinary)
/*  598:     */         {
/*  599:     */           ArrayList<String> attribValues;
/*  600:1051 */           if (cutPoints == null)
/*  601:     */           {
/*  602:1052 */             ArrayList<String> attribValues = new ArrayList(1);
/*  603:1053 */             attribValues.add("'All'");
/*  604:     */           }
/*  605:     */           else
/*  606:     */           {
/*  607:1055 */             attribValues = new ArrayList(cutPoints.length + 1);
/*  608:1056 */             if (this.m_UseBinNumbers)
/*  609:     */             {
/*  610:1057 */               int j = 0;
/*  611:1057 */               for (int n = cutPoints.length; j <= n; j++) {
/*  612:1058 */                 attribValues.add("'B" + (j + 1) + "of" + (n + 1) + "'");
/*  613:     */               }
/*  614:     */             }
/*  615:     */             else
/*  616:     */             {
/*  617:1061 */               int j = 0;
/*  618:1061 */               for (int n = cutPoints.length; j <= n; j++)
/*  619:     */               {
/*  620:1062 */                 String newBinRangeString = binRangeString(cutPoints, j, this.m_BinRangePrecision);
/*  621:1064 */                 if (cutPointsCheck.contains(newBinRangeString)) {
/*  622:1065 */                   throw new IllegalArgumentException("A duplicate bin range was detected. Try increasing the bin range precision.");
/*  623:     */                 }
/*  624:1069 */                 attribValues.add("'" + newBinRangeString + "'");
/*  625:     */               }
/*  626:     */             }
/*  627:     */           }
/*  628:1073 */           Attribute newAtt = new Attribute(getInputFormat().attribute(i).name(), attribValues);
/*  629:     */           
/*  630:1075 */           newAtt.setWeight(getInputFormat().attribute(i).weight());
/*  631:1076 */           attributes.add(newAtt);
/*  632:     */         }
/*  633:1078 */         else if (cutPoints == null)
/*  634:     */         {
/*  635:1079 */           ArrayList<String> attribValues = new ArrayList(1);
/*  636:1080 */           attribValues.add("'All'");
/*  637:1081 */           Attribute newAtt = new Attribute(getInputFormat().attribute(i).name(), attribValues);
/*  638:     */           
/*  639:1083 */           newAtt.setWeight(getInputFormat().attribute(i).weight());
/*  640:1084 */           attributes.add(newAtt);
/*  641:     */         }
/*  642:     */         else
/*  643:     */         {
/*  644:1086 */           if (i < getInputFormat().classIndex()) {
/*  645:1087 */             classIndex += cutPoints.length - 1;
/*  646:     */           }
/*  647:1089 */           int j = 0;
/*  648:1089 */           for (int n = cutPoints.length; j < n; j++)
/*  649:     */           {
/*  650:1090 */             ArrayList<String> attribValues = new ArrayList(2);
/*  651:1091 */             if (this.m_UseBinNumbers)
/*  652:     */             {
/*  653:1092 */               attribValues.add("'B1of2'");
/*  654:1093 */               attribValues.add("'B2of2'");
/*  655:     */             }
/*  656:     */             else
/*  657:     */             {
/*  658:1095 */               double[] binaryCutPoint = { cutPoints[j] };
/*  659:1096 */               String newBinRangeString1 = binRangeString(binaryCutPoint, 0, this.m_BinRangePrecision);
/*  660:     */               
/*  661:1098 */               String newBinRangeString2 = binRangeString(binaryCutPoint, 1, this.m_BinRangePrecision);
/*  662:1100 */               if (newBinRangeString1.equals(newBinRangeString2)) {
/*  663:1101 */                 throw new IllegalArgumentException("A duplicate bin range was detected. Try increasing the bin range precision.");
/*  664:     */               }
/*  665:1105 */               attribValues.add("'" + newBinRangeString1 + "'");
/*  666:1106 */               attribValues.add("'" + newBinRangeString2 + "'");
/*  667:     */             }
/*  668:1108 */             Attribute newAtt = new Attribute(getInputFormat().attribute(i).name() + "_" + (j + 1), attribValues);
/*  669:     */             
/*  670:1110 */             newAtt.setWeight(getInputFormat().attribute(i).weight());
/*  671:1111 */             attributes.add(newAtt);
/*  672:     */           }
/*  673:     */         }
/*  674:     */       }
/*  675:     */       else
/*  676:     */       {
/*  677:1116 */         attributes.add((Attribute)getInputFormat().attribute(i).copy());
/*  678:     */       }
/*  679:     */     }
/*  680:1119 */     Instances outputFormat = new Instances(getInputFormat().relationName(), attributes, 0);
/*  681:     */     
/*  682:1121 */     outputFormat.setClassIndex(classIndex);
/*  683:1122 */     setOutputFormat(outputFormat);
/*  684:     */   }
/*  685:     */   
/*  686:     */   protected void convertInstance(Instance instance)
/*  687:     */   {
/*  688:1133 */     int index = 0;
/*  689:1134 */     double[] vals = new double[outputFormatPeek().numAttributes()];
/*  690:1136 */     for (int i = 0; i < getInputFormat().numAttributes(); i++) {
/*  691:1137 */       if ((this.m_DiscretizeCols.isInRange(i)) && (getInputFormat().attribute(i).isNumeric()))
/*  692:     */       {
/*  693:1140 */         double currentVal = instance.value(i);
/*  694:1141 */         if (this.m_CutPoints[i] == null)
/*  695:     */         {
/*  696:1142 */           if (instance.isMissing(i)) {
/*  697:1143 */             vals[index] = Utils.missingValue();
/*  698:     */           } else {
/*  699:1145 */             vals[index] = 0.0D;
/*  700:     */           }
/*  701:1147 */           index++;
/*  702:     */         }
/*  703:1149 */         else if (!this.m_MakeBinary)
/*  704:     */         {
/*  705:1150 */           if (instance.isMissing(i))
/*  706:     */           {
/*  707:1151 */             vals[index] = Utils.missingValue();
/*  708:     */           }
/*  709:     */           else
/*  710:     */           {
/*  711:1153 */             for (int j = 0; j < this.m_CutPoints[i].length; j++) {
/*  712:1154 */               if (currentVal <= this.m_CutPoints[i][j]) {
/*  713:     */                 break;
/*  714:     */               }
/*  715:     */             }
/*  716:1158 */             vals[index] = j;
/*  717:     */           }
/*  718:1160 */           index++;
/*  719:     */         }
/*  720:     */         else
/*  721:     */         {
/*  722:1162 */           for (int j = 0; j < this.m_CutPoints[i].length; j++)
/*  723:     */           {
/*  724:1163 */             if (instance.isMissing(i)) {
/*  725:1164 */               vals[index] = Utils.missingValue();
/*  726:1165 */             } else if (currentVal <= this.m_CutPoints[i][j]) {
/*  727:1166 */               vals[index] = 0.0D;
/*  728:     */             } else {
/*  729:1168 */               vals[index] = 1.0D;
/*  730:     */             }
/*  731:1170 */             index++;
/*  732:     */           }
/*  733:     */         }
/*  734:     */       }
/*  735:     */       else
/*  736:     */       {
/*  737:1175 */         vals[index] = instance.value(i);
/*  738:1176 */         index++;
/*  739:     */       }
/*  740:     */     }
/*  741:1180 */     Instance inst = null;
/*  742:1181 */     if ((instance instanceof SparseInstance)) {
/*  743:1182 */       inst = new SparseInstance(instance.weight(), vals);
/*  744:     */     } else {
/*  745:1184 */       inst = new DenseInstance(instance.weight(), vals);
/*  746:     */     }
/*  747:1187 */     copyValues(inst, false, instance.dataset(), outputFormatPeek());
/*  748:     */     
/*  749:1189 */     push(inst);
/*  750:     */   }
/*  751:     */   
/*  752:     */   public String getRevision()
/*  753:     */   {
/*  754:1199 */     return RevisionUtils.extract("$Revision: 12037 $");
/*  755:     */   }
/*  756:     */   
/*  757:     */   public static void main(String[] argv)
/*  758:     */   {
/*  759:1208 */     runFilter(new Discretize(), argv);
/*  760:     */   }
/*  761:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.supervised.attribute.Discretize
 * JD-Core Version:    0.7.0.1
 */