/*    1:     */ package weka.filters.unsupervised.attribute;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.Vector;
/*    8:     */ import weka.core.Attribute;
/*    9:     */ import weka.core.Capabilities;
/*   10:     */ import weka.core.Capabilities.Capability;
/*   11:     */ import weka.core.DenseInstance;
/*   12:     */ import weka.core.Instance;
/*   13:     */ import weka.core.Instances;
/*   14:     */ import weka.core.Option;
/*   15:     */ import weka.core.Range;
/*   16:     */ import weka.core.RevisionUtils;
/*   17:     */ import weka.core.Utils;
/*   18:     */ import weka.filters.SimpleBatchFilter;
/*   19:     */ 
/*   20:     */ public class InterquartileRange
/*   21:     */   extends SimpleBatchFilter
/*   22:     */ {
/*   23:     */   private static final long serialVersionUID = -227879653639723030L;
/*   24:     */   public static final int NON_NUMERIC = -1;
/*   25:     */   
/*   26:     */   public static enum ValueType
/*   27:     */   {
/*   28: 131 */     UPPER_EXTREME_VALUES,  UPPER_OUTLIER_VALUES,  LOWER_OUTLIER_VALUES,  LOWER_EXTREME_VALUES,  MEDIAN,  IQR;
/*   29:     */     
/*   30:     */     private ValueType() {}
/*   31:     */   }
/*   32:     */   
/*   33: 135 */   protected Range m_Attributes = new Range("first-last");
/*   34: 138 */   protected int[] m_AttributeIndices = null;
/*   35: 141 */   protected double m_OutlierFactor = 3.0D;
/*   36: 144 */   protected double m_ExtremeValuesFactor = 2.0D * this.m_OutlierFactor;
/*   37: 147 */   protected boolean m_ExtremeValuesAsOutliers = false;
/*   38: 150 */   protected double[] m_UpperExtremeValue = null;
/*   39: 153 */   protected double[] m_UpperOutlier = null;
/*   40: 156 */   protected double[] m_LowerOutlier = null;
/*   41: 159 */   protected double[] m_IQR = null;
/*   42: 162 */   protected double[] m_Median = null;
/*   43: 165 */   protected double[] m_LowerExtremeValue = null;
/*   44: 171 */   protected boolean m_DetectionPerAttribute = false;
/*   45: 174 */   protected int[] m_OutlierAttributePosition = null;
/*   46: 182 */   protected boolean m_OutputOffsetMultiplier = false;
/*   47:     */   
/*   48:     */   public String globalInfo()
/*   49:     */   {
/*   50: 192 */     return "A filter for detecting outliers and extreme values based on interquartile ranges. The filter skips the class attribute.\n\nOutliers:\n  Q3 + OF*IQR < x <= Q3 + EVF*IQR\n  or\n  Q1 - EVF*IQR <= x < Q1 - OF*IQR\n\nExtreme values:\n  x > Q3 + EVF*IQR\n  or\n  x < Q1 - EVF*IQR\n\nKey:\n  Q1  = 25% quartile\n  Q3  = 75% quartile\n  IQR = Interquartile Range, difference between Q1 and Q3\n  OF  = Outlier Factor\n  EVF = Extreme Value Factor";
/*   51:     */   }
/*   52:     */   
/*   53:     */   public Enumeration<Option> listOptions()
/*   54:     */   {
/*   55: 210 */     Vector<Option> result = new Vector();
/*   56:     */     
/*   57: 212 */     result.addElement(new Option("\tSpecifies list of columns to base outlier/extreme value detection\n\ton. If an instance is considered in at least one of those\n\tattributes an outlier/extreme value, it is tagged accordingly.\n 'first' and 'last' are valid indexes.\n\t(default none)", "R", 1, "-R <col1,col2-col4,...>"));
/*   58:     */     
/*   59:     */ 
/*   60:     */ 
/*   61:     */ 
/*   62:     */ 
/*   63:     */ 
/*   64: 219 */     result.addElement(new Option("\tThe factor for outlier detection.\n\t(default: 3)", "O", 1, "-O <num>"));
/*   65:     */     
/*   66:     */ 
/*   67: 222 */     result.addElement(new Option("\tThe factor for extreme values detection.\n\t(default: 2*Outlier Factor)", "E", 1, "-E <num>"));
/*   68:     */     
/*   69:     */ 
/*   70: 225 */     result.addElement(new Option("\tTags extreme values also as outliers.\n\t(default: off)", "E-as-O", 0, "-E-as-O"));
/*   71:     */     
/*   72:     */ 
/*   73: 228 */     result.addElement(new Option("\tGenerates Outlier/ExtremeValue pair for each numeric attribute in\n\tthe range, not just a single indicator pair for all the attributes.\n\t(default: off)", "P", 0, "-P"));
/*   74:     */     
/*   75:     */ 
/*   76:     */ 
/*   77:     */ 
/*   78:     */ 
/*   79: 234 */     result.addElement(new Option("\tGenerates an additional attribute 'Offset' per Outlier/ExtremeValue\n\tpair that contains the multiplier that the value is off the median.\n\t   value = median + 'multiplier' * IQR\nNote: implicitely sets '-P'.\t(default: off)", "M", 0, "-M"));
/*   80:     */     
/*   81:     */ 
/*   82:     */ 
/*   83:     */ 
/*   84:     */ 
/*   85:     */ 
/*   86: 241 */     result.addAll(Collections.list(super.listOptions()));
/*   87:     */     
/*   88: 243 */     return result.elements();
/*   89:     */   }
/*   90:     */   
/*   91:     */   public void setOptions(String[] options)
/*   92:     */     throws Exception
/*   93:     */   {
/*   94: 308 */     String tmpStr = Utils.getOption("R", options);
/*   95: 309 */     if (tmpStr.length() != 0) {
/*   96: 310 */       setAttributeIndices(tmpStr);
/*   97:     */     } else {
/*   98: 312 */       setAttributeIndices("first-last");
/*   99:     */     }
/*  100: 315 */     tmpStr = Utils.getOption("O", options);
/*  101: 316 */     if (tmpStr.length() != 0) {
/*  102: 317 */       setOutlierFactor(Double.parseDouble(tmpStr));
/*  103:     */     } else {
/*  104: 319 */       setOutlierFactor(3.0D);
/*  105:     */     }
/*  106: 322 */     tmpStr = Utils.getOption("E", options);
/*  107: 323 */     if (tmpStr.length() != 0) {
/*  108: 324 */       setExtremeValuesFactor(Double.parseDouble(tmpStr));
/*  109:     */     } else {
/*  110: 326 */       setExtremeValuesFactor(2.0D * getOutlierFactor());
/*  111:     */     }
/*  112: 329 */     setExtremeValuesAsOutliers(Utils.getFlag("E-as-O", options));
/*  113:     */     
/*  114: 331 */     setDetectionPerAttribute(Utils.getFlag("P", options));
/*  115:     */     
/*  116: 333 */     setOutputOffsetMultiplier(Utils.getFlag("M", options));
/*  117:     */     
/*  118: 335 */     super.setOptions(options);
/*  119:     */     
/*  120: 337 */     Utils.checkForRemainingOptions(options);
/*  121:     */   }
/*  122:     */   
/*  123:     */   public String[] getOptions()
/*  124:     */   {
/*  125: 348 */     Vector<String> result = new Vector();
/*  126:     */     
/*  127: 350 */     result.add("-R");
/*  128: 351 */     if (!getAttributeIndices().equals("")) {
/*  129: 352 */       result.add(getAttributeIndices());
/*  130:     */     } else {
/*  131: 354 */       result.add("first-last");
/*  132:     */     }
/*  133: 357 */     result.add("-O");
/*  134: 358 */     result.add("" + getOutlierFactor());
/*  135:     */     
/*  136: 360 */     result.add("-E");
/*  137: 361 */     result.add("" + getExtremeValuesFactor());
/*  138: 363 */     if (getExtremeValuesAsOutliers()) {
/*  139: 364 */       result.add("-E-as-O");
/*  140:     */     }
/*  141: 367 */     if (getDetectionPerAttribute()) {
/*  142: 368 */       result.add("-P");
/*  143:     */     }
/*  144: 371 */     if (getOutputOffsetMultiplier()) {
/*  145: 372 */       result.add("-M");
/*  146:     */     }
/*  147: 375 */     Collections.addAll(result, super.getOptions());
/*  148:     */     
/*  149: 377 */     return (String[])result.toArray(new String[result.size()]);
/*  150:     */   }
/*  151:     */   
/*  152:     */   public String attributeIndicesTipText()
/*  153:     */   {
/*  154: 387 */     return "Specify range of attributes to act on;  this is a comma separated list of attribute indices, with \"first\" and \"last\" valid values; specify an inclusive range with \"-\", eg: \"first-3,5,6-10,last\".";
/*  155:     */   }
/*  156:     */   
/*  157:     */   public String getAttributeIndices()
/*  158:     */   {
/*  159: 399 */     return this.m_Attributes.getRanges();
/*  160:     */   }
/*  161:     */   
/*  162:     */   public void setAttributeIndices(String value)
/*  163:     */   {
/*  164: 413 */     this.m_Attributes.setRanges(value);
/*  165:     */   }
/*  166:     */   
/*  167:     */   public void setAttributeIndicesArray(int[] value)
/*  168:     */   {
/*  169: 427 */     setAttributeIndices(Range.indicesToRangeList(value));
/*  170:     */   }
/*  171:     */   
/*  172:     */   public String outlierFactorTipText()
/*  173:     */   {
/*  174: 437 */     return "The factor for determining the thresholds for outliers.";
/*  175:     */   }
/*  176:     */   
/*  177:     */   public void setOutlierFactor(double value)
/*  178:     */   {
/*  179: 446 */     if (value >= getExtremeValuesFactor()) {
/*  180: 447 */       System.err.println("OutlierFactor must be smaller than ExtremeValueFactor");
/*  181:     */     } else {
/*  182: 450 */       this.m_OutlierFactor = value;
/*  183:     */     }
/*  184:     */   }
/*  185:     */   
/*  186:     */   public double getOutlierFactor()
/*  187:     */   {
/*  188: 460 */     return this.m_OutlierFactor;
/*  189:     */   }
/*  190:     */   
/*  191:     */   public String extremeValuesFactorTipText()
/*  192:     */   {
/*  193: 470 */     return "The factor for determining the thresholds for extreme values.";
/*  194:     */   }
/*  195:     */   
/*  196:     */   public void setExtremeValuesFactor(double value)
/*  197:     */   {
/*  198: 479 */     if (value <= getOutlierFactor()) {
/*  199: 480 */       System.err.println("ExtremeValuesFactor must be greater than OutlierFactor!");
/*  200:     */     } else {
/*  201: 483 */       this.m_ExtremeValuesFactor = value;
/*  202:     */     }
/*  203:     */   }
/*  204:     */   
/*  205:     */   public double getExtremeValuesFactor()
/*  206:     */   {
/*  207: 493 */     return this.m_ExtremeValuesFactor;
/*  208:     */   }
/*  209:     */   
/*  210:     */   public String extremeValuesAsOutliersTipText()
/*  211:     */   {
/*  212: 503 */     return "Whether to tag extreme values also as outliers.";
/*  213:     */   }
/*  214:     */   
/*  215:     */   public void setExtremeValuesAsOutliers(boolean value)
/*  216:     */   {
/*  217: 512 */     this.m_ExtremeValuesAsOutliers = value;
/*  218:     */   }
/*  219:     */   
/*  220:     */   public boolean getExtremeValuesAsOutliers()
/*  221:     */   {
/*  222: 521 */     return this.m_ExtremeValuesAsOutliers;
/*  223:     */   }
/*  224:     */   
/*  225:     */   public String detectionPerAttributeTipText()
/*  226:     */   {
/*  227: 531 */     return "Generates Outlier/ExtremeValue attribute pair for each numeric attribute, not just a single pair for all numeric attributes together.";
/*  228:     */   }
/*  229:     */   
/*  230:     */   public void setDetectionPerAttribute(boolean value)
/*  231:     */   {
/*  232: 544 */     this.m_DetectionPerAttribute = value;
/*  233: 545 */     if (!this.m_DetectionPerAttribute) {
/*  234: 546 */       this.m_OutputOffsetMultiplier = false;
/*  235:     */     }
/*  236:     */   }
/*  237:     */   
/*  238:     */   public boolean getDetectionPerAttribute()
/*  239:     */   {
/*  240: 559 */     return this.m_DetectionPerAttribute;
/*  241:     */   }
/*  242:     */   
/*  243:     */   public String outputOffsetMultiplierTipText()
/*  244:     */   {
/*  245: 569 */     return "Generates an additional attribute 'Offset' that contains the multiplier the value is off the median: value = median + 'multiplier' * IQR";
/*  246:     */   }
/*  247:     */   
/*  248:     */   public void setOutputOffsetMultiplier(boolean value)
/*  249:     */   {
/*  250: 582 */     this.m_OutputOffsetMultiplier = value;
/*  251: 583 */     if (this.m_OutputOffsetMultiplier) {
/*  252: 584 */       this.m_DetectionPerAttribute = true;
/*  253:     */     }
/*  254:     */   }
/*  255:     */   
/*  256:     */   public boolean getOutputOffsetMultiplier()
/*  257:     */   {
/*  258: 596 */     return this.m_OutputOffsetMultiplier;
/*  259:     */   }
/*  260:     */   
/*  261:     */   public Capabilities getCapabilities()
/*  262:     */   {
/*  263: 607 */     Capabilities result = super.getCapabilities();
/*  264: 608 */     result.disableAll();
/*  265:     */     
/*  266:     */ 
/*  267: 611 */     result.enableAllAttributes();
/*  268:     */     
/*  269:     */ 
/*  270: 614 */     result.enableAllClasses();
/*  271: 615 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  272: 616 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  273:     */     
/*  274: 618 */     return result;
/*  275:     */   }
/*  276:     */   
/*  277:     */   protected Instances determineOutputFormat(Instances inputFormat)
/*  278:     */     throws Exception
/*  279:     */   {
/*  280: 643 */     this.m_Attributes.setUpper(inputFormat.numAttributes() - 1);
/*  281: 644 */     this.m_AttributeIndices = this.m_Attributes.getSelection();
/*  282: 645 */     for (int i = 0; i < this.m_AttributeIndices.length; i++) {
/*  283: 647 */       if (this.m_AttributeIndices[i] == inputFormat.classIndex()) {
/*  284: 648 */         this.m_AttributeIndices[i] = -1;
/*  285: 652 */       } else if (!inputFormat.attribute(this.m_AttributeIndices[i]).isNumeric()) {
/*  286: 653 */         this.m_AttributeIndices[i] = -1;
/*  287:     */       }
/*  288:     */     }
/*  289: 658 */     ArrayList<Attribute> atts = new ArrayList();
/*  290: 659 */     for (i = 0; i < inputFormat.numAttributes(); i++) {
/*  291: 660 */       atts.add(inputFormat.attribute(i));
/*  292:     */     }
/*  293: 663 */     if (!getDetectionPerAttribute())
/*  294:     */     {
/*  295: 664 */       this.m_OutlierAttributePosition = new int[1];
/*  296: 665 */       this.m_OutlierAttributePosition[0] = atts.size();
/*  297:     */       
/*  298:     */ 
/*  299: 668 */       ArrayList<String> values = new ArrayList();
/*  300: 669 */       values.add("no");
/*  301: 670 */       values.add("yes");
/*  302: 671 */       atts.add(new Attribute("Outlier", values));
/*  303:     */       
/*  304: 673 */       values = new ArrayList();
/*  305: 674 */       values.add("no");
/*  306: 675 */       values.add("yes");
/*  307: 676 */       atts.add(new Attribute("ExtremeValue", values));
/*  308:     */     }
/*  309:     */     else
/*  310:     */     {
/*  311: 678 */       this.m_OutlierAttributePosition = new int[this.m_AttributeIndices.length];
/*  312: 680 */       for (i = 0; i < this.m_AttributeIndices.length; i++) {
/*  313: 681 */         if (this.m_AttributeIndices[i] != -1)
/*  314:     */         {
/*  315: 685 */           this.m_OutlierAttributePosition[i] = atts.size();
/*  316:     */           
/*  317:     */ 
/*  318: 688 */           ArrayList<String> values = new ArrayList();
/*  319: 689 */           values.add("no");
/*  320: 690 */           values.add("yes");
/*  321: 691 */           atts.add(new Attribute(inputFormat.attribute(this.m_AttributeIndices[i]).name() + "_Outlier", values));
/*  322:     */           
/*  323:     */ 
/*  324: 694 */           values = new ArrayList();
/*  325: 695 */           values.add("no");
/*  326: 696 */           values.add("yes");
/*  327: 697 */           atts.add(new Attribute(inputFormat.attribute(this.m_AttributeIndices[i]).name() + "_ExtremeValue", values));
/*  328: 700 */           if (getOutputOffsetMultiplier()) {
/*  329: 701 */             atts.add(new Attribute(inputFormat.attribute(this.m_AttributeIndices[i]).name() + "_Offset"));
/*  330:     */           }
/*  331:     */         }
/*  332:     */       }
/*  333:     */     }
/*  334: 708 */     Instances result = new Instances(inputFormat.relationName(), atts, 0);
/*  335: 709 */     result.setClassIndex(inputFormat.classIndex());
/*  336:     */     
/*  337: 711 */     return result;
/*  338:     */   }
/*  339:     */   
/*  340:     */   protected void computeThresholds(Instances instances)
/*  341:     */   {
/*  342: 729 */     this.m_UpperExtremeValue = new double[this.m_AttributeIndices.length];
/*  343: 730 */     this.m_UpperOutlier = new double[this.m_AttributeIndices.length];
/*  344: 731 */     this.m_LowerOutlier = new double[this.m_AttributeIndices.length];
/*  345: 732 */     this.m_LowerExtremeValue = new double[this.m_AttributeIndices.length];
/*  346: 733 */     this.m_Median = new double[this.m_AttributeIndices.length];
/*  347: 734 */     this.m_IQR = new double[this.m_AttributeIndices.length];
/*  348: 736 */     for (int i = 0; i < this.m_AttributeIndices.length; i++) {
/*  349: 738 */       if (this.m_AttributeIndices[i] != -1)
/*  350:     */       {
/*  351: 743 */         double[] values = instances.attributeToDoubleArray(this.m_AttributeIndices[i]);
/*  352: 744 */         int[] sortedIndices = Utils.sort(values);
/*  353:     */         
/*  354:     */ 
/*  355: 747 */         int half = sortedIndices.length / 2;
/*  356: 748 */         int quarter = half / 2;
/*  357:     */         double q2;
/*  358:     */         double q2;
/*  359: 750 */         if (sortedIndices.length % 2 == 1) {
/*  360: 751 */           q2 = values[sortedIndices[half]];
/*  361:     */         } else {
/*  362: 753 */           q2 = (values[sortedIndices[half]] + values[sortedIndices[(half + 1)]]) / 2.0D;
/*  363:     */         }
/*  364:     */         double q3;
/*  365:     */         double q1;
/*  366:     */         double q3;
/*  367: 756 */         if (half % 2 == 1)
/*  368:     */         {
/*  369: 757 */           double q1 = values[sortedIndices[quarter]];
/*  370: 758 */           q3 = values[sortedIndices[(sortedIndices.length - quarter - 1)]];
/*  371:     */         }
/*  372:     */         else
/*  373:     */         {
/*  374: 760 */           q1 = (values[sortedIndices[quarter]] + values[sortedIndices[(quarter + 1)]]) / 2.0D;
/*  375: 761 */           q3 = (values[sortedIndices[(sortedIndices.length - quarter - 1)]] + values[sortedIndices[(sortedIndices.length - quarter)]]) / 2.0D;
/*  376:     */         }
/*  377: 766 */         this.m_Median[i] = q2;
/*  378: 767 */         this.m_IQR[i] = (q3 - q1);
/*  379: 768 */         this.m_UpperExtremeValue[i] = (q3 + getExtremeValuesFactor() * this.m_IQR[i]);
/*  380: 769 */         this.m_UpperOutlier[i] = (q3 + getOutlierFactor() * this.m_IQR[i]);
/*  381: 770 */         this.m_LowerOutlier[i] = (q1 - getOutlierFactor() * this.m_IQR[i]);
/*  382: 771 */         this.m_LowerExtremeValue[i] = (q1 - getExtremeValuesFactor() * this.m_IQR[i]);
/*  383:     */       }
/*  384:     */     }
/*  385:     */   }
/*  386:     */   
/*  387:     */   public double[] getValues(ValueType type)
/*  388:     */   {
/*  389: 782 */     switch (1.$SwitchMap$weka$filters$unsupervised$attribute$InterquartileRange$ValueType[type.ordinal()])
/*  390:     */     {
/*  391:     */     case 1: 
/*  392: 784 */       return this.m_UpperExtremeValue;
/*  393:     */     case 2: 
/*  394: 786 */       return this.m_UpperOutlier;
/*  395:     */     case 3: 
/*  396: 788 */       return this.m_LowerOutlier;
/*  397:     */     case 4: 
/*  398: 790 */       return this.m_LowerExtremeValue;
/*  399:     */     case 5: 
/*  400: 792 */       return this.m_Median;
/*  401:     */     case 6: 
/*  402: 794 */       return this.m_IQR;
/*  403:     */     }
/*  404: 796 */     throw new IllegalArgumentException("Unhandled value type: " + type);
/*  405:     */   }
/*  406:     */   
/*  407:     */   protected boolean isOutlier(Instance inst, int index)
/*  408:     */   {
/*  409: 812 */     double value = inst.value(this.m_AttributeIndices[index]);
/*  410: 813 */     boolean result = ((this.m_UpperOutlier[index] < value) && (value <= this.m_UpperExtremeValue[index])) || ((this.m_LowerExtremeValue[index] <= value) && (value < this.m_LowerOutlier[index]));
/*  411:     */     
/*  412:     */ 
/*  413: 816 */     return result;
/*  414:     */   }
/*  415:     */   
/*  416:     */   protected boolean isOutlier(Instance inst)
/*  417:     */   {
/*  418: 829 */     boolean result = false;
/*  419: 831 */     for (int i = 0; i < this.m_AttributeIndices.length; i++) {
/*  420: 833 */       if (this.m_AttributeIndices[i] != -1)
/*  421:     */       {
/*  422: 837 */         result = isOutlier(inst, i);
/*  423: 839 */         if (result) {
/*  424:     */           break;
/*  425:     */         }
/*  426:     */       }
/*  427:     */     }
/*  428: 844 */     return result;
/*  429:     */   }
/*  430:     */   
/*  431:     */   protected boolean isExtremeValue(Instance inst, int index)
/*  432:     */   {
/*  433: 859 */     double value = inst.value(this.m_AttributeIndices[index]);
/*  434: 860 */     boolean result = (value > this.m_UpperExtremeValue[index]) || (value < this.m_LowerExtremeValue[index]);
/*  435:     */     
/*  436:     */ 
/*  437: 863 */     return result;
/*  438:     */   }
/*  439:     */   
/*  440:     */   protected boolean isExtremeValue(Instance inst)
/*  441:     */   {
/*  442: 876 */     boolean result = false;
/*  443: 878 */     for (int i = 0; i < this.m_AttributeIndices.length; i++) {
/*  444: 880 */       if (this.m_AttributeIndices[i] != -1)
/*  445:     */       {
/*  446: 884 */         result = isExtremeValue(inst, i);
/*  447: 886 */         if (result) {
/*  448:     */           break;
/*  449:     */         }
/*  450:     */       }
/*  451:     */     }
/*  452: 891 */     return result;
/*  453:     */   }
/*  454:     */   
/*  455:     */   protected double calculateMultiplier(Instance inst, int index)
/*  456:     */   {
/*  457: 906 */     double value = inst.value(this.m_AttributeIndices[index]);
/*  458: 907 */     double result = (value - this.m_Median[index]) / this.m_IQR[index];
/*  459:     */     
/*  460: 909 */     return result;
/*  461:     */   }
/*  462:     */   
/*  463:     */   protected Instances process(Instances instances)
/*  464:     */     throws Exception
/*  465:     */   {
/*  466: 934 */     if (!isFirstBatchDone()) {
/*  467: 935 */       computeThresholds(instances);
/*  468:     */     }
/*  469: 938 */     Instances result = getOutputFormat();
/*  470: 939 */     int numAttOld = instances.numAttributes();
/*  471: 940 */     int numAttNew = result.numAttributes();
/*  472: 942 */     for (int n = 0; n < instances.numInstances(); n++)
/*  473:     */     {
/*  474: 943 */       Instance instOld = instances.instance(n);
/*  475: 944 */       double[] values = new double[numAttNew];
/*  476: 945 */       System.arraycopy(instOld.toDoubleArray(), 0, values, 0, numAttOld);
/*  477: 948 */       if (!getDetectionPerAttribute())
/*  478:     */       {
/*  479: 950 */         if (isOutlier(instOld)) {
/*  480: 951 */           values[this.m_OutlierAttributePosition[0]] = 1.0D;
/*  481:     */         }
/*  482: 954 */         if (isExtremeValue(instOld))
/*  483:     */         {
/*  484: 955 */           values[(this.m_OutlierAttributePosition[0] + 1)] = 1.0D;
/*  485: 957 */           if (getExtremeValuesAsOutliers()) {
/*  486: 958 */             values[this.m_OutlierAttributePosition[0]] = 1.0D;
/*  487:     */           }
/*  488:     */         }
/*  489:     */       }
/*  490:     */       else
/*  491:     */       {
/*  492: 962 */         for (int i = 0; i < this.m_AttributeIndices.length; i++) {
/*  493: 964 */           if (this.m_AttributeIndices[i] != -1)
/*  494:     */           {
/*  495: 969 */             if (isOutlier(instOld, this.m_AttributeIndices[i])) {
/*  496: 970 */               values[this.m_OutlierAttributePosition[i]] = 1.0D;
/*  497:     */             }
/*  498: 973 */             if (isExtremeValue(instOld, this.m_AttributeIndices[i]))
/*  499:     */             {
/*  500: 974 */               values[(this.m_OutlierAttributePosition[i] + 1)] = 1.0D;
/*  501: 976 */               if (getExtremeValuesAsOutliers()) {
/*  502: 977 */                 values[this.m_OutlierAttributePosition[i]] = 1.0D;
/*  503:     */               }
/*  504:     */             }
/*  505: 981 */             if (getOutputOffsetMultiplier()) {
/*  506: 982 */               values[(this.m_OutlierAttributePosition[i] + 2)] = calculateMultiplier(instOld, this.m_AttributeIndices[i]);
/*  507:     */             }
/*  508:     */           }
/*  509:     */         }
/*  510:     */       }
/*  511: 989 */       Instance instNew = new DenseInstance(1.0D, values);
/*  512: 990 */       instNew.setDataset(result);
/*  513:     */       
/*  514:     */ 
/*  515: 993 */       copyValues(instNew, false, instOld.dataset(), outputFormatPeek());
/*  516:     */       
/*  517:     */ 
/*  518: 996 */       result.add(instNew);
/*  519:     */     }
/*  520: 999 */     return result;
/*  521:     */   }
/*  522:     */   
/*  523:     */   public String getRevision()
/*  524:     */   {
/*  525:1009 */     return RevisionUtils.extract("$Revision: 12476 $");
/*  526:     */   }
/*  527:     */   
/*  528:     */   public static void main(String[] args)
/*  529:     */   {
/*  530:1018 */     runFilter(new InterquartileRange(), args);
/*  531:     */   }
/*  532:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.InterquartileRange
 * JD-Core Version:    0.7.0.1
 */