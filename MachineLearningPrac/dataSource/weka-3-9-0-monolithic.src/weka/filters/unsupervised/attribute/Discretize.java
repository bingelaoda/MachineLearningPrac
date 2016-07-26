/*    1:     */ package weka.filters.unsupervised.attribute;
/*    2:     */ 
/*    3:     */ import java.util.ArrayList;
/*    4:     */ import java.util.Collections;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.Vector;
/*    7:     */ import weka.core.Attribute;
/*    8:     */ import weka.core.Capabilities;
/*    9:     */ import weka.core.Capabilities.Capability;
/*   10:     */ import weka.core.DenseInstance;
/*   11:     */ import weka.core.Instance;
/*   12:     */ import weka.core.Instances;
/*   13:     */ import weka.core.Option;
/*   14:     */ import weka.core.Range;
/*   15:     */ import weka.core.RevisionUtils;
/*   16:     */ import weka.core.SparseInstance;
/*   17:     */ import weka.core.Utils;
/*   18:     */ import weka.core.WeightedInstancesHandler;
/*   19:     */ import weka.filters.UnsupervisedFilter;
/*   20:     */ 
/*   21:     */ public class Discretize
/*   22:     */   extends PotentialClassIgnorer
/*   23:     */   implements UnsupervisedFilter, WeightedInstancesHandler
/*   24:     */ {
/*   25:     */   static final long serialVersionUID = -1358531742174527279L;
/*   26: 120 */   protected Range m_DiscretizeCols = new Range();
/*   27: 123 */   protected int m_NumBins = 10;
/*   28: 126 */   protected double m_DesiredWeightOfInstancesPerInterval = -1.0D;
/*   29: 129 */   protected double[][] m_CutPoints = (double[][])null;
/*   30: 132 */   protected boolean m_MakeBinary = false;
/*   31: 135 */   protected boolean m_UseBinNumbers = false;
/*   32: 138 */   protected boolean m_FindNumBins = false;
/*   33: 141 */   protected boolean m_UseEqualFrequency = false;
/*   34:     */   protected String m_DefaultCols;
/*   35:     */   
/*   36:     */   public Discretize()
/*   37:     */   {
/*   38: 149 */     this.m_DefaultCols = "first-last";
/*   39: 150 */     setAttributeIndices("first-last");
/*   40:     */   }
/*   41:     */   
/*   42:     */   public Discretize(String cols)
/*   43:     */   {
/*   44: 160 */     this.m_DefaultCols = cols;
/*   45: 161 */     setAttributeIndices(cols);
/*   46:     */   }
/*   47:     */   
/*   48:     */   public Enumeration<Option> listOptions()
/*   49:     */   {
/*   50: 172 */     Vector<Option> result = new Vector();
/*   51:     */     
/*   52: 174 */     result.addElement(new Option("\tSpecifies the (maximum) number of bins to divide numeric attributes into.\n\t(default = 10)", "B", 1, "-B <num>"));
/*   53:     */     
/*   54:     */ 
/*   55:     */ 
/*   56: 178 */     result.addElement(new Option("\tSpecifies the desired weight of instances per bin for\n\tequal-frequency binning. If this is set to a positive\n\tnumber then the -B option will be ignored.\n\t(default = -1)", "M", 1, "-M <num>"));
/*   57:     */     
/*   58:     */ 
/*   59:     */ 
/*   60:     */ 
/*   61:     */ 
/*   62:     */ 
/*   63: 185 */     result.addElement(new Option("\tUse equal-frequency instead of equal-width discretization.", "F", 0, "-F"));
/*   64:     */     
/*   65:     */ 
/*   66:     */ 
/*   67: 189 */     result.addElement(new Option("\tOptimize number of bins using leave-one-out estimate\n\tof estimated entropy (for equal-width discretization).\n\tIf this is set then the -B option will be ignored.", "O", 0, "-O"));
/*   68:     */     
/*   69:     */ 
/*   70:     */ 
/*   71:     */ 
/*   72:     */ 
/*   73:     */ 
/*   74: 196 */     result.addElement(new Option("\tSpecifies list of columns to Discretize. First and last are valid indexes.\n\t(default: first-last)", "R", 1, "-R <col1,col2-col4,...>"));
/*   75:     */     
/*   76:     */ 
/*   77:     */ 
/*   78:     */ 
/*   79: 201 */     result.addElement(new Option("\tInvert matching sense of column indexes.", "V", 0, "-V"));
/*   80:     */     
/*   81:     */ 
/*   82: 204 */     result.addElement(new Option("\tOutput binary attributes for discretized attributes.", "D", 0, "-D"));
/*   83:     */     
/*   84:     */ 
/*   85: 207 */     result.addElement(new Option("\tUse bin numbers rather than ranges for discretized attributes.", "Y", 0, "-Y"));
/*   86:     */     
/*   87:     */ 
/*   88:     */ 
/*   89: 211 */     result.addAll(Collections.list(super.listOptions()));
/*   90:     */     
/*   91: 213 */     return result.elements();
/*   92:     */   }
/*   93:     */   
/*   94:     */   public void setOptions(String[] options)
/*   95:     */     throws Exception
/*   96:     */   {
/*   97: 285 */     setMakeBinary(Utils.getFlag('D', options));
/*   98: 286 */     setUseBinNumbers(Utils.getFlag('Y', options));
/*   99: 287 */     setUseEqualFrequency(Utils.getFlag('F', options));
/*  100: 288 */     setFindNumBins(Utils.getFlag('O', options));
/*  101: 289 */     setInvertSelection(Utils.getFlag('V', options));
/*  102:     */     
/*  103: 291 */     String weight = Utils.getOption('M', options);
/*  104: 292 */     if (weight.length() != 0) {
/*  105: 293 */       setDesiredWeightOfInstancesPerInterval(new Double(weight).doubleValue());
/*  106:     */     } else {
/*  107: 295 */       setDesiredWeightOfInstancesPerInterval(-1.0D);
/*  108:     */     }
/*  109: 298 */     String numBins = Utils.getOption('B', options);
/*  110: 299 */     if (numBins.length() != 0) {
/*  111: 300 */       setBins(Integer.parseInt(numBins));
/*  112:     */     } else {
/*  113: 302 */       setBins(10);
/*  114:     */     }
/*  115: 305 */     String convertList = Utils.getOption('R', options);
/*  116: 306 */     if (convertList.length() != 0) {
/*  117: 307 */       setAttributeIndices(convertList);
/*  118:     */     } else {
/*  119: 309 */       setAttributeIndices(this.m_DefaultCols);
/*  120:     */     }
/*  121: 312 */     if (getInputFormat() != null) {
/*  122: 313 */       setInputFormat(getInputFormat());
/*  123:     */     }
/*  124: 316 */     super.setOptions(options);
/*  125:     */     
/*  126: 318 */     Utils.checkForRemainingOptions(options);
/*  127:     */   }
/*  128:     */   
/*  129:     */   public String[] getOptions()
/*  130:     */   {
/*  131: 329 */     Vector<String> result = new Vector();
/*  132: 331 */     if (getMakeBinary()) {
/*  133: 332 */       result.add("-D");
/*  134:     */     }
/*  135: 335 */     if (getUseBinNumbers()) {
/*  136: 336 */       result.add("-Y");
/*  137:     */     }
/*  138: 339 */     if (getUseEqualFrequency()) {
/*  139: 340 */       result.add("-F");
/*  140:     */     }
/*  141: 343 */     if (getFindNumBins()) {
/*  142: 344 */       result.add("-O");
/*  143:     */     }
/*  144: 347 */     if (getInvertSelection()) {
/*  145: 348 */       result.add("-V");
/*  146:     */     }
/*  147: 351 */     result.add("-B");
/*  148: 352 */     result.add("" + getBins());
/*  149:     */     
/*  150: 354 */     result.add("-M");
/*  151: 355 */     result.add("" + getDesiredWeightOfInstancesPerInterval());
/*  152: 357 */     if (!getAttributeIndices().equals(""))
/*  153:     */     {
/*  154: 358 */       result.add("-R");
/*  155: 359 */       result.add(getAttributeIndices());
/*  156:     */     }
/*  157: 362 */     Collections.addAll(result, super.getOptions());
/*  158:     */     
/*  159: 364 */     return (String[])result.toArray(new String[result.size()]);
/*  160:     */   }
/*  161:     */   
/*  162:     */   public Capabilities getCapabilities()
/*  163:     */   {
/*  164: 375 */     Capabilities result = super.getCapabilities();
/*  165: 376 */     result.disableAll();
/*  166:     */     
/*  167:     */ 
/*  168: 379 */     result.enableAllAttributes();
/*  169: 380 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  170:     */     
/*  171:     */ 
/*  172: 383 */     result.enableAllClasses();
/*  173: 384 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  174: 385 */     if (!getMakeBinary()) {
/*  175: 386 */       result.enable(Capabilities.Capability.NO_CLASS);
/*  176:     */     }
/*  177: 389 */     return result;
/*  178:     */   }
/*  179:     */   
/*  180:     */   public boolean setInputFormat(Instances instanceInfo)
/*  181:     */     throws Exception
/*  182:     */   {
/*  183: 404 */     if ((this.m_MakeBinary) && (this.m_IgnoreClass)) {
/*  184: 405 */       throw new IllegalArgumentException("Can't ignore class when changing the number of attributes!");
/*  185:     */     }
/*  186: 409 */     super.setInputFormat(instanceInfo);
/*  187:     */     
/*  188: 411 */     this.m_DiscretizeCols.setUpper(instanceInfo.numAttributes() - 1);
/*  189: 412 */     this.m_CutPoints = ((double[][])null);
/*  190: 414 */     if ((getFindNumBins()) && (getUseEqualFrequency())) {
/*  191: 415 */       throw new IllegalArgumentException("Bin number optimization in conjunction with equal-frequency binning not implemented.");
/*  192:     */     }
/*  193: 422 */     return false;
/*  194:     */   }
/*  195:     */   
/*  196:     */   public boolean input(Instance instance)
/*  197:     */   {
/*  198: 437 */     if (getInputFormat() == null) {
/*  199: 438 */       throw new IllegalStateException("No input instance format defined");
/*  200:     */     }
/*  201: 440 */     if (this.m_NewBatch)
/*  202:     */     {
/*  203: 441 */       resetQueue();
/*  204: 442 */       this.m_NewBatch = false;
/*  205:     */     }
/*  206: 445 */     if (this.m_CutPoints != null)
/*  207:     */     {
/*  208: 446 */       convertInstance(instance);
/*  209: 447 */       return true;
/*  210:     */     }
/*  211: 450 */     bufferInput(instance);
/*  212: 451 */     return false;
/*  213:     */   }
/*  214:     */   
/*  215:     */   public boolean batchFinished()
/*  216:     */   {
/*  217: 465 */     if (getInputFormat() == null) {
/*  218: 466 */       throw new IllegalStateException("No input instance format defined");
/*  219:     */     }
/*  220: 468 */     if (this.m_CutPoints == null)
/*  221:     */     {
/*  222: 469 */       calculateCutPoints();
/*  223:     */       
/*  224: 471 */       setOutputFormat();
/*  225: 476 */       for (int i = 0; i < getInputFormat().numInstances(); i++) {
/*  226: 477 */         convertInstance(getInputFormat().instance(i));
/*  227:     */       }
/*  228:     */     }
/*  229: 480 */     flushInput();
/*  230:     */     
/*  231: 482 */     this.m_NewBatch = true;
/*  232: 483 */     return numPendingOutput() != 0;
/*  233:     */   }
/*  234:     */   
/*  235:     */   public String globalInfo()
/*  236:     */   {
/*  237: 494 */     return "An instance filter that discretizes a range of numeric attributes in the dataset into nominal attributes. Discretization is by simple binning. Skips the class attribute if set.";
/*  238:     */   }
/*  239:     */   
/*  240:     */   public String findNumBinsTipText()
/*  241:     */   {
/*  242: 508 */     return "Optimize number of equal-width bins using leave-one-out. Doesn't work for equal-frequency binning";
/*  243:     */   }
/*  244:     */   
/*  245:     */   public boolean getFindNumBins()
/*  246:     */   {
/*  247: 519 */     return this.m_FindNumBins;
/*  248:     */   }
/*  249:     */   
/*  250:     */   public void setFindNumBins(boolean newFindNumBins)
/*  251:     */   {
/*  252: 529 */     this.m_FindNumBins = newFindNumBins;
/*  253:     */   }
/*  254:     */   
/*  255:     */   public String makeBinaryTipText()
/*  256:     */   {
/*  257: 540 */     return "Make resulting attributes binary.";
/*  258:     */   }
/*  259:     */   
/*  260:     */   public boolean getMakeBinary()
/*  261:     */   {
/*  262: 550 */     return this.m_MakeBinary;
/*  263:     */   }
/*  264:     */   
/*  265:     */   public void setMakeBinary(boolean makeBinary)
/*  266:     */   {
/*  267: 560 */     this.m_MakeBinary = makeBinary;
/*  268:     */   }
/*  269:     */   
/*  270:     */   public String useBinNumbersTipText()
/*  271:     */   {
/*  272: 570 */     return "Use bin numbers (eg BXofY) rather than ranges for for discretized attributes";
/*  273:     */   }
/*  274:     */   
/*  275:     */   public boolean getUseBinNumbers()
/*  276:     */   {
/*  277: 581 */     return this.m_UseBinNumbers;
/*  278:     */   }
/*  279:     */   
/*  280:     */   public void setUseBinNumbers(boolean useBinNumbers)
/*  281:     */   {
/*  282: 592 */     this.m_UseBinNumbers = useBinNumbers;
/*  283:     */   }
/*  284:     */   
/*  285:     */   public String desiredWeightOfInstancesPerIntervalTipText()
/*  286:     */   {
/*  287: 603 */     return "Sets the desired weight of instances per interval for equal-frequency binning.";
/*  288:     */   }
/*  289:     */   
/*  290:     */   public double getDesiredWeightOfInstancesPerInterval()
/*  291:     */   {
/*  292: 614 */     return this.m_DesiredWeightOfInstancesPerInterval;
/*  293:     */   }
/*  294:     */   
/*  295:     */   public void setDesiredWeightOfInstancesPerInterval(double newDesiredNumber)
/*  296:     */   {
/*  297: 624 */     this.m_DesiredWeightOfInstancesPerInterval = newDesiredNumber;
/*  298:     */   }
/*  299:     */   
/*  300:     */   public String useEqualFrequencyTipText()
/*  301:     */   {
/*  302: 635 */     return "If set to true, equal-frequency binning will be used instead of equal-width binning.";
/*  303:     */   }
/*  304:     */   
/*  305:     */   public boolean getUseEqualFrequency()
/*  306:     */   {
/*  307: 646 */     return this.m_UseEqualFrequency;
/*  308:     */   }
/*  309:     */   
/*  310:     */   public void setUseEqualFrequency(boolean newUseEqualFrequency)
/*  311:     */   {
/*  312: 656 */     this.m_UseEqualFrequency = newUseEqualFrequency;
/*  313:     */   }
/*  314:     */   
/*  315:     */   public String binsTipText()
/*  316:     */   {
/*  317: 667 */     return "Number of bins.";
/*  318:     */   }
/*  319:     */   
/*  320:     */   public int getBins()
/*  321:     */   {
/*  322: 677 */     return this.m_NumBins;
/*  323:     */   }
/*  324:     */   
/*  325:     */   public void setBins(int numBins)
/*  326:     */   {
/*  327: 687 */     this.m_NumBins = numBins;
/*  328:     */   }
/*  329:     */   
/*  330:     */   public String invertSelectionTipText()
/*  331:     */   {
/*  332: 698 */     return "Set attribute selection mode. If false, only selected (numeric) attributes in the range will be discretized; if true, only non-selected attributes will be discretized.";
/*  333:     */   }
/*  334:     */   
/*  335:     */   public boolean getInvertSelection()
/*  336:     */   {
/*  337: 710 */     return this.m_DiscretizeCols.getInvert();
/*  338:     */   }
/*  339:     */   
/*  340:     */   public void setInvertSelection(boolean invert)
/*  341:     */   {
/*  342: 722 */     this.m_DiscretizeCols.setInvert(invert);
/*  343:     */   }
/*  344:     */   
/*  345:     */   public String attributeIndicesTipText()
/*  346:     */   {
/*  347: 732 */     return "Specify range of attributes to act on. This is a comma separated list of attribute indices, with \"first\" and \"last\" valid values. Specify an inclusive range with \"-\". E.g: \"first-3,5,6-10,last\".";
/*  348:     */   }
/*  349:     */   
/*  350:     */   public String getAttributeIndices()
/*  351:     */   {
/*  352: 745 */     return this.m_DiscretizeCols.getRanges();
/*  353:     */   }
/*  354:     */   
/*  355:     */   public void setAttributeIndices(String rangeList)
/*  356:     */   {
/*  357: 760 */     this.m_DiscretizeCols.setRanges(rangeList);
/*  358:     */   }
/*  359:     */   
/*  360:     */   public void setAttributeIndicesArray(int[] attributes)
/*  361:     */   {
/*  362: 774 */     setAttributeIndices(Range.indicesToRangeList(attributes));
/*  363:     */   }
/*  364:     */   
/*  365:     */   public double[] getCutPoints(int attributeIndex)
/*  366:     */   {
/*  367: 787 */     if (this.m_CutPoints == null) {
/*  368: 788 */       return null;
/*  369:     */     }
/*  370: 790 */     return this.m_CutPoints[attributeIndex];
/*  371:     */   }
/*  372:     */   
/*  373:     */   public String getBinRangesString(int attributeIndex)
/*  374:     */   {
/*  375: 803 */     if (this.m_CutPoints == null) {
/*  376: 804 */       return null;
/*  377:     */     }
/*  378: 807 */     double[] cutPoints = this.m_CutPoints[attributeIndex];
/*  379: 809 */     if (cutPoints == null) {
/*  380: 810 */       return "All";
/*  381:     */     }
/*  382: 813 */     StringBuilder sb = new StringBuilder();
/*  383: 814 */     boolean first = true;
/*  384:     */     
/*  385: 816 */     int j = 0;
/*  386: 816 */     for (int n = cutPoints.length; j <= n; j++)
/*  387:     */     {
/*  388: 817 */       if (first) {
/*  389: 818 */         first = false;
/*  390:     */       } else {
/*  391: 820 */         sb.append(',');
/*  392:     */       }
/*  393: 823 */       sb.append(binRangeString(cutPoints, j));
/*  394:     */     }
/*  395: 826 */     return sb.toString();
/*  396:     */   }
/*  397:     */   
/*  398:     */   private static String binRangeString(double[] cutPoints, int j)
/*  399:     */   {
/*  400: 838 */     assert (cutPoints != null);
/*  401:     */     
/*  402: 840 */     int n = cutPoints.length;
/*  403: 841 */     assert ((0 <= j) && (j <= n));
/*  404:     */     
/*  405: 843 */     return "(" + Utils.doubleToString(cutPoints[(j - 1)], 6) + "-" + Utils.doubleToString(cutPoints[j], 6) + "]";
/*  406:     */   }
/*  407:     */   
/*  408:     */   protected void calculateCutPoints()
/*  409:     */   {
/*  410: 853 */     this.m_CutPoints = new double[getInputFormat().numAttributes()][];
/*  411: 854 */     for (int i = getInputFormat().numAttributes() - 1; i >= 0; i--) {
/*  412: 855 */       if ((this.m_DiscretizeCols.isInRange(i)) && (getInputFormat().attribute(i).isNumeric()) && (getInputFormat().classIndex() != i)) {
/*  413: 858 */         if (this.m_FindNumBins) {
/*  414: 859 */           findNumBins(i);
/*  415: 860 */         } else if (!this.m_UseEqualFrequency) {
/*  416: 861 */           calculateCutPointsByEqualWidthBinning(i);
/*  417:     */         } else {
/*  418: 863 */           calculateCutPointsByEqualFrequencyBinning(i);
/*  419:     */         }
/*  420:     */       }
/*  421:     */     }
/*  422:     */   }
/*  423:     */   
/*  424:     */   protected void calculateCutPointsByEqualWidthBinning(int index)
/*  425:     */   {
/*  426: 877 */     double max = 0.0D;double min = 1.0D;
/*  427: 879 */     for (int i = 0; i < getInputFormat().numInstances(); i++)
/*  428:     */     {
/*  429: 880 */       Instance currentInstance = getInputFormat().instance(i);
/*  430: 881 */       if (!currentInstance.isMissing(index))
/*  431:     */       {
/*  432: 882 */         double currentVal = currentInstance.value(index);
/*  433: 883 */         if (max < min) {
/*  434: 884 */           max = min = currentVal;
/*  435:     */         }
/*  436: 886 */         if (currentVal > max) {
/*  437: 887 */           max = currentVal;
/*  438:     */         }
/*  439: 889 */         if (currentVal < min) {
/*  440: 890 */           min = currentVal;
/*  441:     */         }
/*  442:     */       }
/*  443:     */     }
/*  444: 894 */     double binWidth = (max - min) / this.m_NumBins;
/*  445: 895 */     double[] cutPoints = null;
/*  446: 896 */     if ((this.m_NumBins > 1) && (binWidth > 0.0D))
/*  447:     */     {
/*  448: 897 */       cutPoints = new double[this.m_NumBins - 1];
/*  449: 898 */       for (int i = 1; i < this.m_NumBins; i++) {
/*  450: 899 */         cutPoints[(i - 1)] = (min + binWidth * i);
/*  451:     */       }
/*  452:     */     }
/*  453: 902 */     this.m_CutPoints[index] = cutPoints;
/*  454:     */   }
/*  455:     */   
/*  456:     */   protected void calculateCutPointsByEqualFrequencyBinning(int index)
/*  457:     */   {
/*  458: 913 */     Instances data = new Instances(getInputFormat());
/*  459:     */     
/*  460:     */ 
/*  461: 916 */     data.sort(index);
/*  462:     */     
/*  463:     */ 
/*  464: 919 */     double sumOfWeights = 0.0D;
/*  465: 920 */     for (int i = 0; i < data.numInstances(); i++)
/*  466:     */     {
/*  467: 921 */       if (data.instance(i).isMissing(index)) {
/*  468:     */         break;
/*  469:     */       }
/*  470: 924 */       sumOfWeights += data.instance(i).weight();
/*  471:     */     }
/*  472: 928 */     double[] cutPoints = new double[this.m_NumBins - 1];
/*  473:     */     double freq;
/*  474: 929 */     if (getDesiredWeightOfInstancesPerInterval() > 0.0D)
/*  475:     */     {
/*  476: 930 */       double freq = getDesiredWeightOfInstancesPerInterval();
/*  477: 931 */       cutPoints = new double[(int)(sumOfWeights / freq)];
/*  478:     */     }
/*  479:     */     else
/*  480:     */     {
/*  481: 933 */       freq = sumOfWeights / this.m_NumBins;
/*  482: 934 */       cutPoints = new double[this.m_NumBins - 1];
/*  483:     */     }
/*  484: 938 */     double counter = 0.0D;double last = 0.0D;
/*  485: 939 */     int cpindex = 0;int lastIndex = -1;
/*  486: 940 */     for (int i = 0; i < data.numInstances() - 1; i++)
/*  487:     */     {
/*  488: 943 */       if (data.instance(i).isMissing(index)) {
/*  489:     */         break;
/*  490:     */       }
/*  491: 946 */       counter += data.instance(i).weight();
/*  492: 947 */       sumOfWeights -= data.instance(i).weight();
/*  493: 950 */       if (data.instance(i).value(index) < data.instance(i + 1).value(index)) {
/*  494: 953 */         if (counter >= freq)
/*  495:     */         {
/*  496: 956 */           if ((freq - last < counter - freq) && (lastIndex != -1))
/*  497:     */           {
/*  498: 957 */             cutPoints[cpindex] = ((data.instance(lastIndex).value(index) + data.instance(lastIndex + 1).value(index)) / 2.0D);
/*  499:     */             
/*  500: 959 */             counter -= last;
/*  501: 960 */             last = counter;
/*  502: 961 */             lastIndex = i;
/*  503:     */           }
/*  504:     */           else
/*  505:     */           {
/*  506: 963 */             cutPoints[cpindex] = ((data.instance(i).value(index) + data.instance(i + 1).value(index)) / 2.0D);
/*  507:     */             
/*  508: 965 */             counter = 0.0D;
/*  509: 966 */             last = 0.0D;
/*  510: 967 */             lastIndex = -1;
/*  511:     */           }
/*  512: 969 */           cpindex++;
/*  513: 970 */           freq = (sumOfWeights + counter) / (cutPoints.length + 1 - cpindex);
/*  514:     */         }
/*  515:     */         else
/*  516:     */         {
/*  517: 972 */           lastIndex = i;
/*  518: 973 */           last = counter;
/*  519:     */         }
/*  520:     */       }
/*  521:     */     }
/*  522: 979 */     if ((cpindex < cutPoints.length) && (lastIndex != -1))
/*  523:     */     {
/*  524: 980 */       cutPoints[cpindex] = ((data.instance(lastIndex).value(index) + data.instance(lastIndex + 1).value(index)) / 2.0D);
/*  525:     */       
/*  526: 982 */       cpindex++;
/*  527:     */     }
/*  528: 986 */     if (cpindex == 0)
/*  529:     */     {
/*  530: 987 */       this.m_CutPoints[index] = null;
/*  531:     */     }
/*  532:     */     else
/*  533:     */     {
/*  534: 989 */       double[] cp = new double[cpindex];
/*  535: 990 */       for (int i = 0; i < cpindex; i++) {
/*  536: 991 */         cp[i] = cutPoints[i];
/*  537:     */       }
/*  538: 993 */       this.m_CutPoints[index] = cp;
/*  539:     */     }
/*  540:     */   }
/*  541:     */   
/*  542:     */   protected void findNumBins(int index)
/*  543:     */   {
/*  544:1004 */     double min = 1.7976931348623157E+308D;double max = -1.797693134862316E+308D;double binWidth = 0.0D;double bestEntropy = 1.7976931348623157E+308D;
/*  545:     */     
/*  546:1006 */     int bestNumBins = 1;
/*  547:1010 */     for (int i = 0; i < getInputFormat().numInstances(); i++)
/*  548:     */     {
/*  549:1011 */       Instance currentInstance = getInputFormat().instance(i);
/*  550:1012 */       if (!currentInstance.isMissing(index))
/*  551:     */       {
/*  552:1013 */         double currentVal = currentInstance.value(index);
/*  553:1014 */         if (currentVal > max) {
/*  554:1015 */           max = currentVal;
/*  555:     */         }
/*  556:1017 */         if (currentVal < min) {
/*  557:1018 */           min = currentVal;
/*  558:     */         }
/*  559:     */       }
/*  560:     */     }
/*  561:1024 */     for (int i = 0; i < this.m_NumBins; i++)
/*  562:     */     {
/*  563:1025 */       double[] distribution = new double[i + 1];
/*  564:1026 */       binWidth = (max - min) / (i + 1);
/*  565:1029 */       for (int j = 0; j < getInputFormat().numInstances(); j++)
/*  566:     */       {
/*  567:1030 */         Instance currentInstance = getInputFormat().instance(j);
/*  568:1031 */         if (!currentInstance.isMissing(index)) {
/*  569:1032 */           for (int k = 0; k < i + 1; k++) {
/*  570:1033 */             if (currentInstance.value(index) <= min + (k + 1.0D) * binWidth)
/*  571:     */             {
/*  572:1034 */               distribution[k] += currentInstance.weight();
/*  573:1035 */               break;
/*  574:     */             }
/*  575:     */           }
/*  576:     */         }
/*  577:     */       }
/*  578:1042 */       double entropy = 0.0D;
/*  579:1043 */       for (int k = 0; k < i + 1; k++)
/*  580:     */       {
/*  581:1044 */         if (distribution[k] < 2.0D)
/*  582:     */         {
/*  583:1045 */           entropy = 1.7976931348623157E+308D;
/*  584:1046 */           break;
/*  585:     */         }
/*  586:1048 */         entropy -= distribution[k] * Math.log((distribution[k] - 1.0D) / binWidth);
/*  587:     */       }
/*  588:1052 */       if (entropy < bestEntropy)
/*  589:     */       {
/*  590:1053 */         bestEntropy = entropy;
/*  591:1054 */         bestNumBins = i + 1;
/*  592:     */       }
/*  593:     */     }
/*  594:1059 */     double[] cutPoints = null;
/*  595:1060 */     if ((bestNumBins > 1) && (binWidth > 0.0D))
/*  596:     */     {
/*  597:1061 */       cutPoints = new double[bestNumBins - 1];
/*  598:1062 */       for (int i = 1; i < bestNumBins; i++) {
/*  599:1063 */         cutPoints[(i - 1)] = (min + binWidth * i);
/*  600:     */       }
/*  601:     */     }
/*  602:1066 */     this.m_CutPoints[index] = cutPoints;
/*  603:     */   }
/*  604:     */   
/*  605:     */   protected void setOutputFormat()
/*  606:     */   {
/*  607:1075 */     if (this.m_CutPoints == null)
/*  608:     */     {
/*  609:1076 */       setOutputFormat(null);
/*  610:1077 */       return;
/*  611:     */     }
/*  612:1079 */     ArrayList<Attribute> attributes = new ArrayList(getInputFormat().numAttributes());
/*  613:     */     
/*  614:1081 */     int classIndex = getInputFormat().classIndex();
/*  615:1082 */     int i = 0;
/*  616:1082 */     for (int m = getInputFormat().numAttributes(); i < m; i++) {
/*  617:1083 */       if ((this.m_DiscretizeCols.isInRange(i)) && (getInputFormat().attribute(i).isNumeric()) && (getInputFormat().classIndex() != i))
/*  618:     */       {
/*  619:1086 */         double[] cutPoints = this.m_CutPoints[i];
/*  620:1087 */         if (!this.m_MakeBinary)
/*  621:     */         {
/*  622:     */           ArrayList<String> attribValues;
/*  623:1089 */           if (cutPoints == null)
/*  624:     */           {
/*  625:1090 */             ArrayList<String> attribValues = new ArrayList(1);
/*  626:1091 */             attribValues.add("'All'");
/*  627:     */           }
/*  628:     */           else
/*  629:     */           {
/*  630:1093 */             attribValues = new ArrayList(cutPoints.length + 1);
/*  631:1094 */             if (this.m_UseBinNumbers)
/*  632:     */             {
/*  633:1095 */               int j = 0;
/*  634:1095 */               for (int n = cutPoints.length; j <= n; j++) {
/*  635:1096 */                 attribValues.add("'B" + (j + 1) + "of" + (n + 1) + "'");
/*  636:     */               }
/*  637:     */             }
/*  638:     */             else
/*  639:     */             {
/*  640:1099 */               int j = 0;
/*  641:1099 */               for (int n = cutPoints.length; j <= n; j++) {
/*  642:1100 */                 attribValues.add("'" + binRangeString(cutPoints, j) + "'");
/*  643:     */               }
/*  644:     */             }
/*  645:     */           }
/*  646:1104 */           Attribute newAtt = new Attribute(getInputFormat().attribute(i).name(), attribValues);
/*  647:     */           
/*  648:1106 */           newAtt.setWeight(getInputFormat().attribute(i).weight());
/*  649:1107 */           attributes.add(newAtt);
/*  650:     */         }
/*  651:1109 */         else if (cutPoints == null)
/*  652:     */         {
/*  653:1110 */           ArrayList<String> attribValues = new ArrayList(1);
/*  654:1111 */           attribValues.add("'All'");
/*  655:1112 */           Attribute newAtt = new Attribute(getInputFormat().attribute(i).name(), attribValues);
/*  656:     */           
/*  657:1114 */           newAtt.setWeight(getInputFormat().attribute(i).weight());
/*  658:1115 */           attributes.add(newAtt);
/*  659:     */         }
/*  660:     */         else
/*  661:     */         {
/*  662:1117 */           if (i < getInputFormat().classIndex()) {
/*  663:1118 */             classIndex += cutPoints.length - 1;
/*  664:     */           }
/*  665:1120 */           int j = 0;
/*  666:1120 */           for (int n = cutPoints.length; j < n; j++)
/*  667:     */           {
/*  668:1121 */             ArrayList<String> attribValues = new ArrayList(2);
/*  669:1122 */             if (this.m_UseBinNumbers)
/*  670:     */             {
/*  671:1123 */               attribValues.add("'B1of2'");
/*  672:1124 */               attribValues.add("'B2of2'");
/*  673:     */             }
/*  674:     */             else
/*  675:     */             {
/*  676:1126 */               double[] binaryCutPoint = { cutPoints[j] };
/*  677:1127 */               attribValues.add("'" + binRangeString(binaryCutPoint, 0) + "'");
/*  678:1128 */               attribValues.add("'" + binRangeString(binaryCutPoint, 1) + "'");
/*  679:     */             }
/*  680:1130 */             Attribute newAtt = new Attribute(getInputFormat().attribute(i).name() + "_" + (j + 1), attribValues);
/*  681:     */             
/*  682:1132 */             newAtt.setWeight(getInputFormat().attribute(i).weight());
/*  683:1133 */             attributes.add(newAtt);
/*  684:     */           }
/*  685:     */         }
/*  686:     */       }
/*  687:     */       else
/*  688:     */       {
/*  689:1138 */         attributes.add((Attribute)getInputFormat().attribute(i).copy());
/*  690:     */       }
/*  691:     */     }
/*  692:1141 */     Instances outputFormat = new Instances(getInputFormat().relationName(), attributes, 0);
/*  693:     */     
/*  694:1143 */     outputFormat.setClassIndex(classIndex);
/*  695:1144 */     setOutputFormat(outputFormat);
/*  696:     */   }
/*  697:     */   
/*  698:     */   protected void convertInstance(Instance instance)
/*  699:     */   {
/*  700:1155 */     int index = 0;
/*  701:1156 */     double[] vals = new double[outputFormatPeek().numAttributes()];
/*  702:1158 */     for (int i = 0; i < getInputFormat().numAttributes(); i++) {
/*  703:1159 */       if ((this.m_DiscretizeCols.isInRange(i)) && (getInputFormat().attribute(i).isNumeric()) && (getInputFormat().classIndex() != i))
/*  704:     */       {
/*  705:1163 */         double currentVal = instance.value(i);
/*  706:1164 */         if (this.m_CutPoints[i] == null)
/*  707:     */         {
/*  708:1165 */           if (instance.isMissing(i)) {
/*  709:1166 */             vals[index] = Utils.missingValue();
/*  710:     */           } else {
/*  711:1168 */             vals[index] = 0.0D;
/*  712:     */           }
/*  713:1170 */           index++;
/*  714:     */         }
/*  715:1172 */         else if (!this.m_MakeBinary)
/*  716:     */         {
/*  717:1173 */           if (instance.isMissing(i))
/*  718:     */           {
/*  719:1174 */             vals[index] = Utils.missingValue();
/*  720:     */           }
/*  721:     */           else
/*  722:     */           {
/*  723:1176 */             for (int j = 0; j < this.m_CutPoints[i].length; j++) {
/*  724:1177 */               if (currentVal <= this.m_CutPoints[i][j]) {
/*  725:     */                 break;
/*  726:     */               }
/*  727:     */             }
/*  728:1181 */             vals[index] = j;
/*  729:     */           }
/*  730:1183 */           index++;
/*  731:     */         }
/*  732:     */         else
/*  733:     */         {
/*  734:1185 */           for (int j = 0; j < this.m_CutPoints[i].length; j++)
/*  735:     */           {
/*  736:1186 */             if (instance.isMissing(i)) {
/*  737:1187 */               vals[index] = Utils.missingValue();
/*  738:1188 */             } else if (currentVal <= this.m_CutPoints[i][j]) {
/*  739:1189 */               vals[index] = 0.0D;
/*  740:     */             } else {
/*  741:1191 */               vals[index] = 1.0D;
/*  742:     */             }
/*  743:1193 */             index++;
/*  744:     */           }
/*  745:     */         }
/*  746:     */       }
/*  747:     */       else
/*  748:     */       {
/*  749:1198 */         vals[index] = instance.value(i);
/*  750:1199 */         index++;
/*  751:     */       }
/*  752:     */     }
/*  753:1203 */     Instance inst = null;
/*  754:1204 */     if ((instance instanceof SparseInstance)) {
/*  755:1205 */       inst = new SparseInstance(instance.weight(), vals);
/*  756:     */     } else {
/*  757:1207 */       inst = new DenseInstance(instance.weight(), vals);
/*  758:     */     }
/*  759:1210 */     copyValues(inst, false, instance.dataset(), outputFormatPeek());
/*  760:     */     
/*  761:1212 */     push(inst);
/*  762:     */   }
/*  763:     */   
/*  764:     */   public String getRevision()
/*  765:     */   {
/*  766:1222 */     return RevisionUtils.extract("$Revision: 12037 $");
/*  767:     */   }
/*  768:     */   
/*  769:     */   public static void main(String[] argv)
/*  770:     */   {
/*  771:1231 */     runFilter(new Discretize(), argv);
/*  772:     */   }
/*  773:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.Discretize
 * JD-Core Version:    0.7.0.1
 */