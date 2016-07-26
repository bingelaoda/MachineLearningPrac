/*    1:     */ package weka.datagenerators.clusterers;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.Random;
/*    8:     */ import java.util.Vector;
/*    9:     */ import weka.core.Attribute;
/*   10:     */ import weka.core.DenseInstance;
/*   11:     */ import weka.core.Instance;
/*   12:     */ import weka.core.Instances;
/*   13:     */ import weka.core.Option;
/*   14:     */ import weka.core.Range;
/*   15:     */ import weka.core.RevisionUtils;
/*   16:     */ import weka.core.SelectedTag;
/*   17:     */ import weka.core.Tag;
/*   18:     */ import weka.core.Utils;
/*   19:     */ import weka.datagenerators.ClusterDefinition;
/*   20:     */ import weka.datagenerators.ClusterGenerator;
/*   21:     */ 
/*   22:     */ public class SubspaceCluster
/*   23:     */   extends ClusterGenerator
/*   24:     */ {
/*   25:     */   static final long serialVersionUID = -3454999858505621128L;
/*   26:     */   protected double m_NoiseRate;
/*   27:     */   protected ClusterDefinition[] m_Clusters;
/*   28:     */   protected int[] m_numValues;
/*   29:     */   public static final int UNIFORM_RANDOM = 0;
/*   30:     */   public static final int TOTAL_UNIFORM = 1;
/*   31:     */   public static final int GAUSSIAN = 2;
/*   32: 173 */   public static final Tag[] TAGS_CLUSTERTYPE = { new Tag(0, "uniform/random"), new Tag(1, "total uniform"), new Tag(2, "gaussian") };
/*   33:     */   public static final int CONTINUOUS = 0;
/*   34:     */   public static final int INTEGER = 1;
/*   35: 182 */   public static final Tag[] TAGS_CLUSTERSUBTYPE = { new Tag(0, "continuous"), new Tag(1, "integer") };
/*   36:     */   
/*   37:     */   public SubspaceCluster()
/*   38:     */   {
/*   39: 192 */     setNoiseRate(defaultNoiseRate());
/*   40:     */   }
/*   41:     */   
/*   42:     */   public String globalInfo()
/*   43:     */   {
/*   44: 202 */     return "A data generator that produces data points in hyperrectangular subspace clusters.";
/*   45:     */   }
/*   46:     */   
/*   47:     */   public Enumeration<Option> listOptions()
/*   48:     */   {
/*   49: 213 */     Vector<Option> result = enumToVector(super.listOptions());
/*   50:     */     
/*   51: 215 */     result.addElement(new Option("\tThe noise rate in percent (default " + defaultNoiseRate() + ").\n" + "\tCan be between 0% and 30%. (Remark: The original \n" + "\talgorithm only allows noise up to 10%.)", "P", 1, "-P <num>"));
/*   52:     */     
/*   53:     */ 
/*   54:     */ 
/*   55:     */ 
/*   56: 220 */     result.addElement(new Option("\tA cluster definition of class '" + SubspaceClusterDefinition.class.getName().replaceAll(".*\\.", "") + "'\n" + "\t(definition needs to be quoted to be recognized as \n" + "\ta single argument).", "C", 1, "-C <cluster-definition>"));
/*   57:     */     
/*   58:     */ 
/*   59:     */ 
/*   60:     */ 
/*   61: 225 */     result.addElement(new Option("", "", 0, "\nOptions specific to " + SubspaceClusterDefinition.class.getName() + ":"));
/*   62:     */     
/*   63:     */ 
/*   64: 228 */     result.addAll(enumToVector(new SubspaceClusterDefinition(this).listOptions()));
/*   65:     */     
/*   66:     */ 
/*   67: 231 */     return result.elements();
/*   68:     */   }
/*   69:     */   
/*   70:     */   public void setOptions(String[] options)
/*   71:     */     throws Exception
/*   72:     */   {
/*   73: 346 */     super.setOptions(options);
/*   74:     */     
/*   75: 348 */     this.m_numValues = new int[getNumAttributes()];
/*   76: 351 */     for (int i = 0; i < getNumAttributes(); i++) {
/*   77: 352 */       this.m_numValues[i] = 1;
/*   78:     */     }
/*   79: 355 */     String tmpStr = Utils.getOption('P', options);
/*   80: 356 */     if (tmpStr.length() != 0) {
/*   81: 357 */       setNoiseRate(Double.parseDouble(tmpStr));
/*   82:     */     } else {
/*   83: 359 */       setNoiseRate(defaultNoiseRate());
/*   84:     */     }
/*   85: 363 */     Vector<SubspaceClusterDefinition> list = new Vector();
/*   86:     */     do
/*   87:     */     {
/*   88: 366 */       tmpStr = Utils.getOption('C', options);
/*   89: 367 */       if (tmpStr.length() != 0)
/*   90:     */       {
/*   91: 368 */         SubspaceClusterDefinition cl = new SubspaceClusterDefinition(this);
/*   92: 369 */         cl.setOptions(Utils.splitOptions(tmpStr));
/*   93: 370 */         list.add(cl);
/*   94:     */       }
/*   95: 372 */     } while (tmpStr.length() != 0);
/*   96: 374 */     this.m_Clusters = ((ClusterDefinition[])list.toArray(new ClusterDefinition[list.size()]));
/*   97:     */     
/*   98:     */ 
/*   99: 377 */     getClusters();
/*  100:     */   }
/*  101:     */   
/*  102:     */   public String[] getOptions()
/*  103:     */   {
/*  104: 388 */     Vector<String> result = new Vector();
/*  105:     */     
/*  106: 390 */     Collections.addAll(result, super.getOptions());
/*  107:     */     
/*  108: 392 */     result.add("-P");
/*  109: 393 */     result.add("" + getNoiseRate());
/*  110: 395 */     for (int i = 0; i < getClusters().length; i++)
/*  111:     */     {
/*  112: 396 */       result.add("-C");
/*  113: 397 */       result.add(Utils.joinOptions(getClusters()[i].getOptions()));
/*  114:     */     }
/*  115: 400 */     return (String[])result.toArray(new String[result.size()]);
/*  116:     */   }
/*  117:     */   
/*  118:     */   protected ClusterDefinition[] getClusters()
/*  119:     */   {
/*  120: 409 */     if ((this.m_Clusters == null) || (this.m_Clusters.length == 0))
/*  121:     */     {
/*  122: 410 */       if (this.m_Clusters != null) {
/*  123: 411 */         System.out.println("NOTE: at least 1 cluster definition is necessary, created default one.");
/*  124:     */       }
/*  125: 414 */       this.m_Clusters = new ClusterDefinition[] { new SubspaceClusterDefinition(this) };
/*  126:     */     }
/*  127: 417 */     return this.m_Clusters;
/*  128:     */   }
/*  129:     */   
/*  130:     */   protected int defaultNumAttributes()
/*  131:     */   {
/*  132: 427 */     return 1;
/*  133:     */   }
/*  134:     */   
/*  135:     */   public void setNumAttributes(int numAttributes)
/*  136:     */   {
/*  137: 437 */     super.setNumAttributes(numAttributes);
/*  138: 438 */     this.m_numValues = new int[getNumAttributes()];
/*  139:     */   }
/*  140:     */   
/*  141:     */   public String numAttributesTipText()
/*  142:     */   {
/*  143: 449 */     return "The number of attributes the generated data will contain (Note: they must be covered by the cluster definitions!)";
/*  144:     */   }
/*  145:     */   
/*  146:     */   protected double defaultNoiseRate()
/*  147:     */   {
/*  148: 458 */     return 0.0D;
/*  149:     */   }
/*  150:     */   
/*  151:     */   public double getNoiseRate()
/*  152:     */   {
/*  153: 467 */     return this.m_NoiseRate;
/*  154:     */   }
/*  155:     */   
/*  156:     */   public void setNoiseRate(double newNoiseRate)
/*  157:     */   {
/*  158: 476 */     this.m_NoiseRate = newNoiseRate;
/*  159:     */   }
/*  160:     */   
/*  161:     */   public String noiseRateTipText()
/*  162:     */   {
/*  163: 486 */     return "The noise rate to use.";
/*  164:     */   }
/*  165:     */   
/*  166:     */   public ClusterDefinition[] getClusterDefinitions()
/*  167:     */   {
/*  168: 495 */     return getClusters();
/*  169:     */   }
/*  170:     */   
/*  171:     */   public void setClusterDefinitions(ClusterDefinition[] value)
/*  172:     */     throws Exception
/*  173:     */   {
/*  174: 508 */     String indexStr = "";
/*  175: 509 */     this.m_Clusters = value;
/*  176: 510 */     for (int i = 0; i < getClusters().length; i++)
/*  177:     */     {
/*  178: 511 */       if (!(getClusters()[i] instanceof SubspaceClusterDefinition))
/*  179:     */       {
/*  180: 512 */         if (indexStr.length() != 0) {
/*  181: 513 */           indexStr = indexStr + ",";
/*  182:     */         }
/*  183: 515 */         indexStr = indexStr + "" + (i + 1);
/*  184:     */       }
/*  185: 517 */       getClusters()[i].setParent(this);
/*  186: 518 */       getClusters()[i].setOptions(getClusters()[i].getOptions());
/*  187:     */     }
/*  188: 523 */     if (indexStr.length() != 0) {
/*  189: 524 */       throw new Exception("These cluster definitions are not '" + SubspaceClusterDefinition.class.getName() + "': " + indexStr);
/*  190:     */     }
/*  191:     */   }
/*  192:     */   
/*  193:     */   public String clusterDefinitionsTipText()
/*  194:     */   {
/*  195: 536 */     return "The clusters to use.";
/*  196:     */   }
/*  197:     */   
/*  198:     */   protected boolean checkCoverage()
/*  199:     */   {
/*  200: 554 */     int[] count = new int[getNumAttributes()];
/*  201: 555 */     for (int i = 0; i < getNumAttributes(); i++)
/*  202:     */     {
/*  203: 556 */       if (this.m_nominalCols.isInRange(i)) {
/*  204: 557 */         count[i] += 1;
/*  205:     */       }
/*  206: 559 */       if (this.m_booleanCols.isInRange(i)) {
/*  207: 560 */         count[i] += 1;
/*  208:     */       }
/*  209: 562 */       for (int n = 0; n < getClusters().length; n++)
/*  210:     */       {
/*  211: 563 */         SubspaceClusterDefinition cl = (SubspaceClusterDefinition)getClusters()[n];
/*  212: 564 */         Range r = new Range(cl.getAttrIndexRange());
/*  213: 565 */         r.setUpper(getNumAttributes());
/*  214: 566 */         if (r.isInRange(i)) {
/*  215: 567 */           count[i] += 1;
/*  216:     */         }
/*  217:     */       }
/*  218:     */     }
/*  219: 573 */     String attrIndex = "";
/*  220: 574 */     for (i = 0; i < count.length; i++) {
/*  221: 575 */       if (count[i] == 0)
/*  222:     */       {
/*  223: 576 */         if (attrIndex.length() != 0) {
/*  224: 577 */           attrIndex = attrIndex + ",";
/*  225:     */         }
/*  226: 579 */         attrIndex = attrIndex + (i + 1);
/*  227:     */       }
/*  228:     */     }
/*  229: 583 */     if (attrIndex.length() != 0) {
/*  230: 584 */       throw new IllegalArgumentException("The following attributes are not covered by a cluster definition: " + attrIndex + "\n");
/*  231:     */     }
/*  232: 589 */     return true;
/*  233:     */   }
/*  234:     */   
/*  235:     */   public boolean getSingleModeFlag()
/*  236:     */   {
/*  237: 599 */     return false;
/*  238:     */   }
/*  239:     */   
/*  240:     */   public Instances defineDataFormat()
/*  241:     */     throws Exception
/*  242:     */   {
/*  243: 613 */     setOptions(getOptions());
/*  244:     */     
/*  245: 615 */     checkCoverage();
/*  246:     */     
/*  247: 617 */     Random random = new Random(getSeed());
/*  248: 618 */     setRandom(random);
/*  249:     */     
/*  250: 620 */     ArrayList<Attribute> attributes = new ArrayList(3);
/*  251:     */     
/*  252: 622 */     boolean classFlag = getClassFlag();
/*  253:     */     
/*  254: 624 */     ArrayList<String> classValues = null;
/*  255: 625 */     if (classFlag) {
/*  256: 626 */       classValues = new ArrayList(getClusters().length);
/*  257:     */     }
/*  258: 628 */     ArrayList<String> boolValues = new ArrayList(2);
/*  259: 629 */     boolValues.add("false");
/*  260: 630 */     boolValues.add("true");
/*  261: 631 */     ArrayList<String> nomValues = null;
/*  262: 634 */     for (int i = 0; i < getNumAttributes(); i++)
/*  263:     */     {
/*  264:     */       Attribute attribute;
/*  265:     */       Attribute attribute;
/*  266: 636 */       if (this.m_booleanCols.isInRange(i))
/*  267:     */       {
/*  268: 637 */         attribute = new Attribute("B" + i, boolValues);
/*  269:     */       }
/*  270:     */       else
/*  271:     */       {
/*  272:     */         Attribute attribute;
/*  273: 638 */         if (this.m_nominalCols.isInRange(i))
/*  274:     */         {
/*  275: 640 */           nomValues = new ArrayList(this.m_numValues[i]);
/*  276: 641 */           for (int j = 0; j < this.m_numValues[i]; j++) {
/*  277: 642 */             nomValues.add("value-" + j);
/*  278:     */           }
/*  279: 644 */           attribute = new Attribute("N" + i, nomValues);
/*  280:     */         }
/*  281:     */         else
/*  282:     */         {
/*  283: 647 */           attribute = new Attribute("X" + i);
/*  284:     */         }
/*  285:     */       }
/*  286: 649 */       attributes.add(attribute);
/*  287:     */     }
/*  288: 652 */     if (classFlag)
/*  289:     */     {
/*  290: 653 */       for (int i = 0; i < getClusters().length; i++) {
/*  291: 654 */         classValues.add("c" + i);
/*  292:     */       }
/*  293: 656 */       Attribute attribute = new Attribute("class", classValues);
/*  294: 657 */       attributes.add(attribute);
/*  295:     */     }
/*  296: 660 */     Instances dataset = new Instances(getRelationNameToUse(), attributes, 0);
/*  297: 661 */     if (classFlag) {
/*  298: 662 */       dataset.setClassIndex(this.m_NumAttributes);
/*  299:     */     }
/*  300: 666 */     Instances format = new Instances(dataset, 0);
/*  301: 667 */     setDatasetFormat(format);
/*  302: 669 */     for (int i = 0; i < getClusters().length; i++)
/*  303:     */     {
/*  304: 670 */       SubspaceClusterDefinition cl = (SubspaceClusterDefinition)getClusters()[i];
/*  305: 671 */       cl.setNumInstances(random);
/*  306: 672 */       cl.setParent(this);
/*  307:     */     }
/*  308: 675 */     return dataset;
/*  309:     */   }
/*  310:     */   
/*  311:     */   public boolean isBoolean(int index)
/*  312:     */   {
/*  313: 685 */     return this.m_booleanCols.isInRange(index);
/*  314:     */   }
/*  315:     */   
/*  316:     */   public boolean isNominal(int index)
/*  317:     */   {
/*  318: 695 */     return this.m_nominalCols.isInRange(index);
/*  319:     */   }
/*  320:     */   
/*  321:     */   public int[] getNumValues()
/*  322:     */   {
/*  323: 704 */     return this.m_numValues;
/*  324:     */   }
/*  325:     */   
/*  326:     */   public Instance generateExample()
/*  327:     */     throws Exception
/*  328:     */   {
/*  329: 717 */     throw new Exception("Examples cannot be generated one by one.");
/*  330:     */   }
/*  331:     */   
/*  332:     */   public Instances generateExamples()
/*  333:     */     throws Exception
/*  334:     */   {
/*  335: 729 */     Instances format = getDatasetFormat();
/*  336: 730 */     Instance example = null;
/*  337: 732 */     if (format == null) {
/*  338: 733 */       throw new Exception("Dataset format not defined.");
/*  339:     */     }
/*  340: 737 */     for (int cNum = 0; cNum < getClusters().length; cNum++)
/*  341:     */     {
/*  342: 738 */       SubspaceClusterDefinition cl = (SubspaceClusterDefinition)getClusters()[cNum];
/*  343:     */       
/*  344:     */ 
/*  345: 741 */       int instNum = cl.getNumInstances();
/*  346:     */       
/*  347:     */ 
/*  348: 744 */       String cName = "c" + cNum;
/*  349: 746 */       switch (cl.getClusterType().getSelectedTag().getID())
/*  350:     */       {
/*  351:     */       case 0: 
/*  352: 748 */         for (int i = 0; i < instNum; i++)
/*  353:     */         {
/*  354: 750 */           example = generateExample(format, getRandom(), cl, cName);
/*  355: 751 */           if (example != null) {
/*  356: 752 */             format.add(example);
/*  357:     */           }
/*  358:     */         }
/*  359: 755 */         break;
/*  360:     */       case 1: 
/*  361: 758 */         if (!cl.isInteger()) {
/*  362: 759 */           generateUniformExamples(format, instNum, cl, cName);
/*  363:     */         } else {
/*  364: 761 */           generateUniformIntegerExamples(format, instNum, cl, cName);
/*  365:     */         }
/*  366: 763 */         break;
/*  367:     */       case 2: 
/*  368: 766 */         generateGaussianExamples(format, instNum, getRandom(), cl, cName);
/*  369:     */       }
/*  370:     */     }
/*  371: 771 */     return format;
/*  372:     */   }
/*  373:     */   
/*  374:     */   private Instance generateExample(Instances format, Random randomG, SubspaceClusterDefinition cl, String cName)
/*  375:     */   {
/*  376: 786 */     boolean makeInteger = cl.isInteger();
/*  377: 787 */     int num = -1;
/*  378: 788 */     int numAtts = this.m_NumAttributes;
/*  379: 789 */     if (getClassFlag()) {
/*  380: 790 */       numAtts++;
/*  381:     */     }
/*  382: 793 */     double[] values = new double[numAtts];
/*  383: 794 */     boolean[] attributes = cl.getAttributes();
/*  384: 795 */     double[] minValue = cl.getMinValue();
/*  385: 796 */     double[] maxValue = cl.getMaxValue();
/*  386:     */     
/*  387:     */ 
/*  388: 799 */     int clusterI = -1;
/*  389: 800 */     for (int i = 0; i < this.m_NumAttributes; i++) {
/*  390: 801 */       if (attributes[i] != 0)
/*  391:     */       {
/*  392: 802 */         clusterI++;
/*  393: 803 */         num++;
/*  394:     */         double value;
/*  395: 805 */         if ((isBoolean(i)) || (isNominal(i)))
/*  396:     */         {
/*  397:     */           double value;
/*  398: 807 */           if (minValue[clusterI] == maxValue[clusterI])
/*  399:     */           {
/*  400: 808 */             value = minValue[clusterI];
/*  401:     */           }
/*  402:     */           else
/*  403:     */           {
/*  404: 810 */             int numValues = (int)(maxValue[clusterI] - minValue[clusterI] + 1.0D);
/*  405: 811 */             double value = randomG.nextInt(numValues);
/*  406: 812 */             value += minValue[clusterI];
/*  407:     */           }
/*  408:     */         }
/*  409:     */         else
/*  410:     */         {
/*  411: 816 */           value = randomG.nextDouble() * (maxValue[num] - minValue[num]) + minValue[num];
/*  412: 818 */           if (makeInteger) {
/*  413: 819 */             value = Math.round(value);
/*  414:     */           }
/*  415:     */         }
/*  416: 822 */         values[i] = value;
/*  417:     */       }
/*  418:     */       else
/*  419:     */       {
/*  420: 824 */         values[i] = Utils.missingValue();
/*  421:     */       }
/*  422:     */     }
/*  423: 828 */     if (getClassFlag()) {
/*  424: 829 */       values[format.classIndex()] = format.classAttribute().indexOfValue(cName);
/*  425:     */     }
/*  426: 832 */     DenseInstance example = new DenseInstance(1.0D, values);
/*  427: 833 */     example.setDataset(format);
/*  428:     */     
/*  429: 835 */     return example;
/*  430:     */   }
/*  431:     */   
/*  432:     */   private void generateUniformExamples(Instances format, int numInstances, SubspaceClusterDefinition cl, String cName)
/*  433:     */   {
/*  434: 849 */     int numAtts = this.m_NumAttributes;
/*  435: 850 */     if (getClassFlag()) {
/*  436: 851 */       numAtts++;
/*  437:     */     }
/*  438: 853 */     boolean[] attributes = cl.getAttributes();
/*  439: 854 */     double[] minValue = cl.getMinValue();
/*  440: 855 */     double[] maxValue = cl.getMaxValue();
/*  441: 856 */     double[] diff = new double[minValue.length];
/*  442: 858 */     for (int i = 0; i < minValue.length; i++) {
/*  443: 859 */       maxValue[i] -= minValue[i];
/*  444:     */     }
/*  445: 862 */     for (int j = 0; j < numInstances; j++)
/*  446:     */     {
/*  447: 863 */       double[] values = new double[numAtts];
/*  448: 864 */       int num = -1;
/*  449: 865 */       for (int i = 0; i < this.m_NumAttributes; i++) {
/*  450: 866 */         if (attributes[i] != 0)
/*  451:     */         {
/*  452: 867 */           num++;
/*  453: 868 */           double value = minValue[num] + diff[num] * (j / (numInstances - 1));
/*  454:     */           
/*  455: 870 */           values[i] = value;
/*  456:     */         }
/*  457:     */         else
/*  458:     */         {
/*  459: 872 */           values[i] = Utils.missingValue();
/*  460:     */         }
/*  461:     */       }
/*  462: 875 */       if (getClassFlag()) {
/*  463: 876 */         values[format.classIndex()] = format.classAttribute().indexOfValue(cName);
/*  464:     */       }
/*  465: 879 */       DenseInstance example = new DenseInstance(1.0D, values);
/*  466: 880 */       example.setDataset(format);
/*  467: 881 */       format.add(example);
/*  468:     */     }
/*  469:     */   }
/*  470:     */   
/*  471:     */   private void generateUniformIntegerExamples(Instances format, int numInstances, SubspaceClusterDefinition cl, String cName)
/*  472:     */   {
/*  473: 896 */     int numAtts = this.m_NumAttributes;
/*  474: 897 */     if (getClassFlag()) {
/*  475: 898 */       numAtts++;
/*  476:     */     }
/*  477: 901 */     double[] values = new double[numAtts];
/*  478: 902 */     boolean[] attributes = cl.getAttributes();
/*  479: 903 */     double[] minValue = cl.getMinValue();
/*  480: 904 */     double[] maxValue = cl.getMaxValue();
/*  481: 905 */     int[] minInt = new int[minValue.length];
/*  482: 906 */     int[] maxInt = new int[maxValue.length];
/*  483: 907 */     int[] intValue = new int[maxValue.length];
/*  484: 908 */     int[] numInt = new int[minValue.length];
/*  485:     */     
/*  486: 910 */     int num = 1;
/*  487: 911 */     for (int i = 0; i < minValue.length; i++)
/*  488:     */     {
/*  489: 912 */       minInt[i] = ((int)Math.ceil(minValue[i]));
/*  490: 913 */       maxInt[i] = ((int)Math.floor(maxValue[i]));
/*  491: 914 */       numInt[i] = (maxInt[i] - minInt[i] + 1);
/*  492: 915 */       num *= numInt[i];
/*  493:     */     }
/*  494: 917 */     int numEach = numInstances / num;
/*  495: 918 */     int rest = numInstances - numEach * num;
/*  496: 921 */     for (int i = 0; i < this.m_NumAttributes; i++) {
/*  497: 922 */       if (attributes[i] != 0)
/*  498:     */       {
/*  499: 923 */         values[i] = minInt[i];
/*  500: 924 */         intValue[i] = minInt[i];
/*  501:     */       }
/*  502:     */       else
/*  503:     */       {
/*  504: 926 */         values[i] = Utils.missingValue();
/*  505:     */       }
/*  506:     */     }
/*  507: 929 */     if (getClassFlag()) {
/*  508: 930 */       values[format.classIndex()] = format.classAttribute().indexOfValue(cName);
/*  509:     */     }
/*  510: 933 */     DenseInstance example = new DenseInstance(1.0D, values);
/*  511: 934 */     example.setDataset(format);
/*  512:     */     
/*  513: 936 */     int added = 0;
/*  514: 937 */     int attr = 0;
/*  515:     */     do
/*  516:     */     {
/*  517: 941 */       for (int k = 0; k < numEach; k++)
/*  518:     */       {
/*  519: 942 */         format.add(example);
/*  520: 943 */         added++;
/*  521:     */       }
/*  522: 945 */       if (rest > 0)
/*  523:     */       {
/*  524: 946 */         format.add(example);
/*  525: 947 */         added++;
/*  526: 948 */         rest--;
/*  527:     */       }
/*  528: 951 */       if (added >= numInstances) {
/*  529:     */         break;
/*  530:     */       }
/*  531: 955 */       boolean done = false;
/*  532:     */       do
/*  533:     */       {
/*  534: 957 */         if ((attributes[attr] != 0) && (intValue[attr] + 1 <= maxInt[attr]))
/*  535:     */         {
/*  536: 958 */           intValue[attr] += 1;
/*  537: 959 */           done = true;
/*  538:     */         }
/*  539:     */         else
/*  540:     */         {
/*  541: 961 */           attr++;
/*  542:     */         }
/*  543: 963 */       } while (!done);
/*  544: 965 */       example.setValue(attr, intValue[attr]);
/*  545: 966 */     } while (added < numInstances);
/*  546:     */   }
/*  547:     */   
/*  548:     */   private void generateGaussianExamples(Instances format, int numInstances, Random random, SubspaceClusterDefinition cl, String cName)
/*  549:     */   {
/*  550: 981 */     boolean makeInteger = cl.isInteger();
/*  551: 982 */     int numAtts = this.m_NumAttributes;
/*  552: 983 */     if (getClassFlag()) {
/*  553: 984 */       numAtts++;
/*  554:     */     }
/*  555: 987 */     boolean[] attributes = cl.getAttributes();
/*  556: 988 */     double[] meanValue = cl.getMeanValue();
/*  557: 989 */     double[] stddevValue = cl.getStddevValue();
/*  558: 991 */     for (int j = 0; j < numInstances; j++)
/*  559:     */     {
/*  560: 992 */       double[] values = new double[numAtts];
/*  561: 993 */       int num = -1;
/*  562: 994 */       for (int i = 0; i < this.m_NumAttributes; i++) {
/*  563: 995 */         if (attributes[i] != 0)
/*  564:     */         {
/*  565: 996 */           num++;
/*  566: 997 */           double value = meanValue[num] + random.nextGaussian() * stddevValue[num];
/*  567: 999 */           if (makeInteger) {
/*  568:1000 */             value = Math.round(value);
/*  569:     */           }
/*  570:1002 */           values[i] = value;
/*  571:     */         }
/*  572:     */         else
/*  573:     */         {
/*  574:1004 */           values[i] = Utils.missingValue();
/*  575:     */         }
/*  576:     */       }
/*  577:1007 */       if (getClassFlag()) {
/*  578:1008 */         values[format.classIndex()] = format.classAttribute().indexOfValue(cName);
/*  579:     */       }
/*  580:1011 */       DenseInstance example = new DenseInstance(1.0D, values);
/*  581:1012 */       example.setDataset(format);
/*  582:1013 */       format.add(example);
/*  583:     */     }
/*  584:     */   }
/*  585:     */   
/*  586:     */   public String generateFinished()
/*  587:     */     throws Exception
/*  588:     */   {
/*  589:1026 */     return "";
/*  590:     */   }
/*  591:     */   
/*  592:     */   public String generateStart()
/*  593:     */   {
/*  594:1037 */     StringBuffer docu = new StringBuffer();
/*  595:     */     
/*  596:1039 */     int sumInst = 0;
/*  597:1040 */     for (int cNum = 0; cNum < getClusters().length; cNum++)
/*  598:     */     {
/*  599:1041 */       SubspaceClusterDefinition cl = (SubspaceClusterDefinition)getClusters()[cNum];
/*  600:1042 */       docu.append("%\n");
/*  601:1043 */       docu.append("% Cluster: c" + cNum + "   ");
/*  602:1044 */       switch (cl.getClusterType().getSelectedTag().getID())
/*  603:     */       {
/*  604:     */       case 0: 
/*  605:1046 */         docu.append("Uniform Random");
/*  606:1047 */         break;
/*  607:     */       case 1: 
/*  608:1049 */         docu.append("Total Random");
/*  609:1050 */         break;
/*  610:     */       case 2: 
/*  611:1052 */         docu.append("Gaussian");
/*  612:     */       }
/*  613:1055 */       if (cl.isInteger()) {
/*  614:1056 */         docu.append(" / INTEGER");
/*  615:     */       }
/*  616:1059 */       docu.append("\n% ----------------------------------------------\n");
/*  617:1060 */       docu.append("%" + cl.attributesToString());
/*  618:     */       
/*  619:1062 */       docu.append("\n% Number of Instances:            " + cl.getInstNums() + "\n");
/*  620:     */       
/*  621:1064 */       docu.append("% Generated Number of Instances:  " + cl.getNumInstances() + "\n");
/*  622:     */       
/*  623:1066 */       sumInst += cl.getNumInstances();
/*  624:     */     }
/*  625:1068 */     docu.append("%\n% ----------------------------------------------\n");
/*  626:1069 */     docu.append("% Total Number of Instances: " + sumInst + "\n");
/*  627:1070 */     docu.append("%                            in " + getClusters().length + " Cluster(s)\n%");
/*  628:     */     
/*  629:     */ 
/*  630:1073 */     return docu.toString();
/*  631:     */   }
/*  632:     */   
/*  633:     */   public String getRevision()
/*  634:     */   {
/*  635:1083 */     return RevisionUtils.extract("$Revision: 12478 $");
/*  636:     */   }
/*  637:     */   
/*  638:     */   public static void main(String[] args)
/*  639:     */   {
/*  640:1092 */     runDataGenerator(new SubspaceCluster(), args);
/*  641:     */   }
/*  642:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.datagenerators.clusterers.SubspaceCluster
 * JD-Core Version:    0.7.0.1
 */