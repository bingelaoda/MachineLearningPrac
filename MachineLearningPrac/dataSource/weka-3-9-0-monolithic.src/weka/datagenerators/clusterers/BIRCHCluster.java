/*    1:     */ package weka.datagenerators.clusterers;
/*    2:     */ 
/*    3:     */ import java.io.Serializable;
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
/*   14:     */ import weka.core.RevisionHandler;
/*   15:     */ import weka.core.RevisionUtils;
/*   16:     */ import weka.core.SelectedTag;
/*   17:     */ import weka.core.Tag;
/*   18:     */ import weka.core.TechnicalInformation;
/*   19:     */ import weka.core.TechnicalInformation.Field;
/*   20:     */ import weka.core.TechnicalInformation.Type;
/*   21:     */ import weka.core.TechnicalInformationHandler;
/*   22:     */ import weka.core.Utils;
/*   23:     */ import weka.core.WekaEnumeration;
/*   24:     */ import weka.datagenerators.ClusterGenerator;
/*   25:     */ 
/*   26:     */ public class BIRCHCluster
/*   27:     */   extends ClusterGenerator
/*   28:     */   implements TechnicalInformationHandler
/*   29:     */ {
/*   30:     */   static final long serialVersionUID = -334820527230755027L;
/*   31:     */   protected int m_NumClusters;
/*   32:     */   private int m_MinInstNum;
/*   33:     */   private int m_MaxInstNum;
/*   34:     */   private double m_MinRadius;
/*   35:     */   private double m_MaxRadius;
/*   36:     */   public static final int GRID = 0;
/*   37:     */   public static final int SINE = 1;
/*   38:     */   public static final int RANDOM = 2;
/*   39: 222 */   public static final Tag[] TAGS_PATTERN = { new Tag(0, "Grid"), new Tag(1, "Sine"), new Tag(2, "Random") };
/*   40:     */   private int m_Pattern;
/*   41:     */   private double m_DistMult;
/*   42:     */   private int m_NumCycles;
/*   43:     */   public static final int ORDERED = 0;
/*   44:     */   public static final int RANDOMIZED = 1;
/*   45: 239 */   public static final Tag[] TAGS_INPUTORDER = { new Tag(0, "ordered"), new Tag(1, "randomized") };
/*   46:     */   private int m_InputOrder;
/*   47:     */   private double m_NoiseRate;
/*   48:     */   private ArrayList<Cluster> m_ClusterList;
/*   49:     */   private int m_GridSize;
/*   50:     */   private double m_GridWidth;
/*   51:     */   
/*   52:     */   private class Cluster
/*   53:     */     implements Serializable, RevisionHandler
/*   54:     */   {
/*   55:     */     static final long serialVersionUID = -8336901069823498140L;
/*   56:     */     private final int m_InstNum;
/*   57:     */     private final double m_Radius;
/*   58:     */     private final double[] m_Center;
/*   59:     */     
/*   60:     */     private Cluster(int instNum, double radius, Random random)
/*   61:     */     {
/*   62: 285 */       this.m_InstNum = instNum;
/*   63: 286 */       this.m_Radius = radius;
/*   64: 287 */       this.m_Center = new double[BIRCHCluster.this.getNumAttributes()];
/*   65: 288 */       for (int i = 0; i < BIRCHCluster.this.getNumAttributes(); i++) {
/*   66: 289 */         this.m_Center[i] = (random.nextDouble() * BIRCHCluster.this.m_NumClusters);
/*   67:     */       }
/*   68:     */     }
/*   69:     */     
/*   70:     */     private Cluster(int instNum, double radius, int[] gridVector, double gridWidth)
/*   71:     */     {
/*   72: 304 */       this.m_InstNum = instNum;
/*   73: 305 */       this.m_Radius = radius;
/*   74: 306 */       this.m_Center = new double[BIRCHCluster.this.getNumAttributes()];
/*   75: 307 */       for (int i = 0; i < BIRCHCluster.this.getNumAttributes(); i++) {
/*   76: 308 */         this.m_Center[i] = ((gridVector[i] + 1.0D) * gridWidth);
/*   77:     */       }
/*   78:     */     }
/*   79:     */     
/*   80:     */     private int getInstNum()
/*   81:     */     {
/*   82: 319 */       return this.m_InstNum;
/*   83:     */     }
/*   84:     */     
/*   85:     */     private double getStdDev()
/*   86:     */     {
/*   87: 328 */       return this.m_Radius / Math.pow(2.0D, 0.5D);
/*   88:     */     }
/*   89:     */     
/*   90:     */     private double[] getCenter()
/*   91:     */     {
/*   92: 337 */       return this.m_Center;
/*   93:     */     }
/*   94:     */     
/*   95:     */     public String getRevision()
/*   96:     */     {
/*   97: 347 */       return RevisionUtils.extract("$Revision: 12471 $");
/*   98:     */     }
/*   99:     */   }
/*  100:     */   
/*  101:     */   private class GridVector
/*  102:     */     implements Serializable, RevisionHandler
/*  103:     */   {
/*  104:     */     static final long serialVersionUID = -1900309948991039522L;
/*  105:     */     private final int[] m_GridVector;
/*  106:     */     private final int m_Base;
/*  107:     */     private final int m_Size;
/*  108:     */     
/*  109:     */     private GridVector(int numDim, int base)
/*  110:     */     {
/*  111: 379 */       this.m_Size = numDim;
/*  112: 380 */       this.m_Base = base;
/*  113: 381 */       this.m_GridVector = new int[numDim];
/*  114: 382 */       for (int i = 0; i < numDim; i++) {
/*  115: 383 */         this.m_GridVector[i] = 0;
/*  116:     */       }
/*  117:     */     }
/*  118:     */     
/*  119:     */     private int[] getGridVector()
/*  120:     */     {
/*  121: 393 */       return this.m_GridVector;
/*  122:     */     }
/*  123:     */     
/*  124:     */     private boolean overflow(int digit)
/*  125:     */     {
/*  126: 403 */       return digit == 0;
/*  127:     */     }
/*  128:     */     
/*  129:     */     private int addOne(int digit)
/*  130:     */     {
/*  131: 413 */       int value = digit + 1;
/*  132: 414 */       if (value >= this.m_Base) {
/*  133: 415 */         value = 0;
/*  134:     */       }
/*  135: 417 */       return value;
/*  136:     */     }
/*  137:     */     
/*  138:     */     private void addOne()
/*  139:     */     {
/*  140: 424 */       this.m_GridVector[0] = addOne(this.m_GridVector[0]);
/*  141: 425 */       int i = 1;
/*  142: 426 */       while ((overflow(this.m_GridVector[(i - 1)])) && (i < this.m_Size))
/*  143:     */       {
/*  144: 427 */         this.m_GridVector[i] = addOne(this.m_GridVector[i]);
/*  145: 428 */         i++;
/*  146:     */       }
/*  147:     */     }
/*  148:     */     
/*  149:     */     public String getRevision()
/*  150:     */     {
/*  151: 440 */       return RevisionUtils.extract("$Revision: 12471 $");
/*  152:     */     }
/*  153:     */   }
/*  154:     */   
/*  155:     */   public BIRCHCluster()
/*  156:     */   {
/*  157: 450 */     setNumClusters(defaultNumClusters());
/*  158: 451 */     setMinInstNum(defaultMinInstNum());
/*  159: 452 */     setMaxInstNum(defaultMaxInstNum());
/*  160: 453 */     setMinRadius(defaultMinRadius());
/*  161: 454 */     setMaxRadius(defaultMaxRadius());
/*  162: 455 */     setPattern(defaultPattern());
/*  163: 456 */     setDistMult(defaultDistMult());
/*  164: 457 */     setNumCycles(defaultNumCycles());
/*  165: 458 */     setInputOrder(defaultInputOrder());
/*  166: 459 */     setNoiseRate(defaultNoiseRate());
/*  167:     */   }
/*  168:     */   
/*  169:     */   public String globalInfo()
/*  170:     */   {
/*  171: 469 */     return "Cluster data generator designed for the BIRCH System\n\nDataset is generated with instances in K clusters.\nInstances are 2-d data points.\nEach cluster is characterized by the number of data points in itits radius and its center. The location of the cluster centers isdetermined by the pattern parameter. Three patterns are currentlysupported grid, sine and random.\n\nFor more information refer to:\n\n" + getTechnicalInformation().toString();
/*  172:     */   }
/*  173:     */   
/*  174:     */   public TechnicalInformation getTechnicalInformation()
/*  175:     */   {
/*  176: 491 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  177: 492 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Tian Zhang and Raghu Ramakrishnan and Miron Livny");
/*  178:     */     
/*  179: 494 */     result.setValue(TechnicalInformation.Field.TITLE, "BIRCH: An Efficient Data Clustering Method for Very Large Databases");
/*  180:     */     
/*  181: 496 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "ACM SIGMOD International Conference on Management of Data");
/*  182:     */     
/*  183: 498 */     result.setValue(TechnicalInformation.Field.YEAR, "1996");
/*  184: 499 */     result.setValue(TechnicalInformation.Field.PAGES, "103-114");
/*  185: 500 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "ACM Press");
/*  186:     */     
/*  187: 502 */     return result;
/*  188:     */   }
/*  189:     */   
/*  190:     */   public Enumeration<Option> listOptions()
/*  191:     */   {
/*  192: 512 */     Vector<Option> result = enumToVector(super.listOptions());
/*  193:     */     
/*  194: 514 */     result.addElement(new Option("\tThe number of clusters (default " + defaultNumClusters() + ")", "k", 1, "-k <num>"));
/*  195:     */     
/*  196:     */ 
/*  197: 517 */     result.addElement(new Option("\tSet pattern to grid (default is random).\n\tThis flag cannot be used at the same time as flag I.\n\tThe pattern is random, if neither flag G nor flag I is set.", "G", 0, "-G"));
/*  198:     */     
/*  199:     */ 
/*  200:     */ 
/*  201:     */ 
/*  202: 522 */     result.addElement(new Option("\tSet pattern to sine (default is random).\n\tThis flag cannot be used at the same time as flag I.\n\tThe pattern is random, if neither flag G nor flag I is set.", "I", 0, "-I"));
/*  203:     */     
/*  204:     */ 
/*  205:     */ 
/*  206:     */ 
/*  207: 527 */     result.addElement(new Option("\tThe range of number of instances per cluster (default " + defaultMinInstNum() + ".." + defaultMaxInstNum() + ").\n" + "\tLower number must be between 0 and 2500,\n" + "\tupper number must be between 50 and 2500.", "N", 1, "-N <num>..<num>"));
/*  208:     */     
/*  209:     */ 
/*  210:     */ 
/*  211:     */ 
/*  212:     */ 
/*  213:     */ 
/*  214: 534 */     result.addElement(new Option("\tThe range of radius per cluster (default " + defaultMinRadius() + ".." + defaultMaxRadius() + ").\n" + "\tLower number must be between 0 and SQRT(2), \n" + "\tupper number must be between SQRT(2) and SQRT(32).", "R", 1, "-R <num>..<num>"));
/*  215:     */     
/*  216:     */ 
/*  217:     */ 
/*  218:     */ 
/*  219:     */ 
/*  220: 540 */     result.addElement(new Option("\tThe distance multiplier (default " + defaultDistMult() + ").", "M", 1, "-M <num>"));
/*  221:     */     
/*  222:     */ 
/*  223: 543 */     result.addElement(new Option("\tThe number of cycles (default " + defaultNumCycles() + ").", "C", 1, "-C <num>"));
/*  224:     */     
/*  225:     */ 
/*  226: 546 */     result.addElement(new Option("\tFlag for input order is ORDERED. If flag is not set then \n\tinput order is RANDOMIZED. RANDOMIZED is currently not \n\timplemented, therefore is the input order always ORDERED.", "O", 0, "-O"));
/*  227:     */     
/*  228:     */ 
/*  229:     */ 
/*  230:     */ 
/*  231:     */ 
/*  232: 552 */     result.addElement(new Option("\tThe noise rate in percent (default " + defaultNoiseRate() + ").\n" + "\tCan be between 0% and 30%. (Remark: The original \n" + "\talgorithm only allows noise up to 10%.)", "P", 1, "-P <num>"));
/*  233:     */     
/*  234:     */ 
/*  235:     */ 
/*  236:     */ 
/*  237: 557 */     return result.elements();
/*  238:     */   }
/*  239:     */   
/*  240:     */   public void setOptions(String[] options)
/*  241:     */     throws Exception
/*  242:     */   {
/*  243: 679 */     super.setOptions(options);
/*  244:     */     
/*  245: 681 */     String tmpStr = Utils.getOption('k', options);
/*  246: 682 */     if (tmpStr.length() != 0) {
/*  247: 683 */       setNumClusters(Integer.parseInt(tmpStr));
/*  248:     */     } else {
/*  249: 685 */       setNumClusters(defaultNumClusters());
/*  250:     */     }
/*  251: 688 */     tmpStr = Utils.getOption('N', options);
/*  252: 689 */     if (tmpStr.length() != 0) {
/*  253: 690 */       setInstNums(tmpStr);
/*  254:     */     } else {
/*  255: 692 */       setInstNums(defaultMinInstNum() + ".." + defaultMaxInstNum());
/*  256:     */     }
/*  257: 695 */     tmpStr = Utils.getOption('R', options);
/*  258: 696 */     if (tmpStr.length() != 0) {
/*  259: 697 */       setRadiuses(tmpStr);
/*  260:     */     } else {
/*  261: 699 */       setRadiuses(defaultMinRadius() + ".." + defaultMaxRadius());
/*  262:     */     }
/*  263: 702 */     boolean grid = Utils.getFlag('G', options);
/*  264: 703 */     boolean sine = Utils.getFlag('I', options);
/*  265: 705 */     if ((grid) && (sine)) {
/*  266: 706 */       throw new Exception("Flags -G and -I can only be set mutually exclusiv.");
/*  267:     */     }
/*  268: 709 */     setPattern(new SelectedTag(2, TAGS_PATTERN));
/*  269: 710 */     if (grid) {
/*  270: 711 */       setPattern(new SelectedTag(0, TAGS_PATTERN));
/*  271:     */     }
/*  272: 713 */     if (sine) {
/*  273: 714 */       setPattern(new SelectedTag(1, TAGS_PATTERN));
/*  274:     */     }
/*  275: 717 */     tmpStr = Utils.getOption('M', options);
/*  276: 718 */     if (tmpStr.length() != 0)
/*  277:     */     {
/*  278: 719 */       if (!grid) {
/*  279: 720 */         throw new Exception("Option M can only be used with GRID pattern.");
/*  280:     */       }
/*  281: 722 */       setDistMult(Double.parseDouble(tmpStr));
/*  282:     */     }
/*  283:     */     else
/*  284:     */     {
/*  285: 724 */       setDistMult(defaultDistMult());
/*  286:     */     }
/*  287: 727 */     tmpStr = Utils.getOption('C', options);
/*  288: 728 */     if (tmpStr.length() != 0)
/*  289:     */     {
/*  290: 729 */       if (!sine) {
/*  291: 730 */         throw new Exception("Option C can only be used with SINE pattern.");
/*  292:     */       }
/*  293: 732 */       setNumCycles(Integer.parseInt(tmpStr));
/*  294:     */     }
/*  295:     */     else
/*  296:     */     {
/*  297: 734 */       setNumCycles(defaultNumCycles());
/*  298:     */     }
/*  299: 737 */     if (Utils.getFlag('O', options)) {
/*  300: 738 */       setInputOrder(new SelectedTag(0, TAGS_INPUTORDER));
/*  301:     */     } else {
/*  302: 740 */       setInputOrder(defaultInputOrder());
/*  303:     */     }
/*  304: 743 */     tmpStr = Utils.getOption('P', options);
/*  305: 744 */     if (tmpStr.length() != 0) {
/*  306: 745 */       setNoiseRate(Double.parseDouble(tmpStr));
/*  307:     */     } else {
/*  308: 747 */       setNoiseRate(defaultNoiseRate());
/*  309:     */     }
/*  310:     */   }
/*  311:     */   
/*  312:     */   public String[] getOptions()
/*  313:     */   {
/*  314: 761 */     Vector<String> result = new Vector();
/*  315: 762 */     Collections.addAll(result, super.getOptions());
/*  316:     */     
/*  317: 764 */     result.add("-k");
/*  318: 765 */     result.add("" + getNumClusters());
/*  319:     */     
/*  320: 767 */     result.add("-N");
/*  321: 768 */     result.add("" + getInstNums());
/*  322:     */     
/*  323: 770 */     result.add("-R");
/*  324: 771 */     result.add("" + getRadiuses());
/*  325: 773 */     if (this.m_Pattern == 0)
/*  326:     */     {
/*  327: 774 */       result.add("-G");
/*  328:     */       
/*  329: 776 */       result.add("-M");
/*  330: 777 */       result.add("" + getDistMult());
/*  331:     */     }
/*  332: 780 */     if (this.m_Pattern == 1)
/*  333:     */     {
/*  334: 781 */       result.add("-I");
/*  335:     */       
/*  336: 783 */       result.add("-C");
/*  337: 784 */       result.add("" + getNumCycles());
/*  338:     */     }
/*  339: 787 */     if (getOrderedFlag()) {
/*  340: 788 */       result.add("-O");
/*  341:     */     }
/*  342: 791 */     result.add("-P");
/*  343: 792 */     result.add("" + getNoiseRate());
/*  344:     */     
/*  345: 794 */     return (String[])result.toArray(new String[result.size()]);
/*  346:     */   }
/*  347:     */   
/*  348:     */   protected int defaultNumClusters()
/*  349:     */   {
/*  350: 803 */     return 4;
/*  351:     */   }
/*  352:     */   
/*  353:     */   public void setNumClusters(int numClusters)
/*  354:     */   {
/*  355: 812 */     this.m_NumClusters = numClusters;
/*  356:     */   }
/*  357:     */   
/*  358:     */   public int getNumClusters()
/*  359:     */   {
/*  360: 821 */     return this.m_NumClusters;
/*  361:     */   }
/*  362:     */   
/*  363:     */   public String numClustersTipText()
/*  364:     */   {
/*  365: 831 */     return "The number of clusters to generate.";
/*  366:     */   }
/*  367:     */   
/*  368:     */   protected void setInstNums(String fromTo)
/*  369:     */   {
/*  370: 841 */     int i = fromTo.indexOf("..");
/*  371: 842 */     String from = fromTo.substring(0, i);
/*  372: 843 */     setMinInstNum(Integer.parseInt(from));
/*  373: 844 */     String to = fromTo.substring(i + 2, fromTo.length());
/*  374: 845 */     setMaxInstNum(Integer.parseInt(to));
/*  375:     */   }
/*  376:     */   
/*  377:     */   protected String getInstNums()
/*  378:     */   {
/*  379: 855 */     String fromTo = "" + getMinInstNum() + ".." + getMaxInstNum();
/*  380: 856 */     return fromTo;
/*  381:     */   }
/*  382:     */   
/*  383:     */   protected String instNumsTipText()
/*  384:     */   {
/*  385: 866 */     return "The upper and lowet boundary for instances per cluster.";
/*  386:     */   }
/*  387:     */   
/*  388:     */   protected int defaultMinInstNum()
/*  389:     */   {
/*  390: 875 */     return 1;
/*  391:     */   }
/*  392:     */   
/*  393:     */   public int getMinInstNum()
/*  394:     */   {
/*  395: 884 */     return this.m_MinInstNum;
/*  396:     */   }
/*  397:     */   
/*  398:     */   public void setMinInstNum(int newMinInstNum)
/*  399:     */   {
/*  400: 893 */     this.m_MinInstNum = newMinInstNum;
/*  401:     */   }
/*  402:     */   
/*  403:     */   public String minInstNumTipText()
/*  404:     */   {
/*  405: 903 */     return "The lower boundary for instances per cluster.";
/*  406:     */   }
/*  407:     */   
/*  408:     */   protected int defaultMaxInstNum()
/*  409:     */   {
/*  410: 912 */     return 50;
/*  411:     */   }
/*  412:     */   
/*  413:     */   public int getMaxInstNum()
/*  414:     */   {
/*  415: 921 */     return this.m_MaxInstNum;
/*  416:     */   }
/*  417:     */   
/*  418:     */   public void setMaxInstNum(int newMaxInstNum)
/*  419:     */   {
/*  420: 930 */     this.m_MaxInstNum = newMaxInstNum;
/*  421:     */   }
/*  422:     */   
/*  423:     */   public String maxInstNumTipText()
/*  424:     */   {
/*  425: 940 */     return "The upper boundary for instances per cluster.";
/*  426:     */   }
/*  427:     */   
/*  428:     */   protected void setRadiuses(String fromTo)
/*  429:     */   {
/*  430: 950 */     int i = fromTo.indexOf("..");
/*  431: 951 */     String from = fromTo.substring(0, i);
/*  432: 952 */     setMinRadius(Double.valueOf(from).doubleValue());
/*  433: 953 */     String to = fromTo.substring(i + 2, fromTo.length());
/*  434: 954 */     setMaxRadius(Double.valueOf(to).doubleValue());
/*  435:     */   }
/*  436:     */   
/*  437:     */   protected String getRadiuses()
/*  438:     */   {
/*  439: 964 */     String fromTo = "" + Utils.doubleToString(getMinRadius(), 2) + ".." + Utils.doubleToString(getMaxRadius(), 2);
/*  440:     */     
/*  441: 966 */     return fromTo;
/*  442:     */   }
/*  443:     */   
/*  444:     */   protected String radiusesTipText()
/*  445:     */   {
/*  446: 976 */     return "The upper and lower boundary for the radius of the clusters.";
/*  447:     */   }
/*  448:     */   
/*  449:     */   protected double defaultMinRadius()
/*  450:     */   {
/*  451: 985 */     return 0.1D;
/*  452:     */   }
/*  453:     */   
/*  454:     */   public double getMinRadius()
/*  455:     */   {
/*  456: 994 */     return this.m_MinRadius;
/*  457:     */   }
/*  458:     */   
/*  459:     */   public void setMinRadius(double newMinRadius)
/*  460:     */   {
/*  461:1003 */     this.m_MinRadius = newMinRadius;
/*  462:     */   }
/*  463:     */   
/*  464:     */   public String minRadiusTipText()
/*  465:     */   {
/*  466:1013 */     return "The lower boundary for the radius of the clusters.";
/*  467:     */   }
/*  468:     */   
/*  469:     */   protected double defaultMaxRadius()
/*  470:     */   {
/*  471:1022 */     return Math.sqrt(2.0D);
/*  472:     */   }
/*  473:     */   
/*  474:     */   public double getMaxRadius()
/*  475:     */   {
/*  476:1031 */     return this.m_MaxRadius;
/*  477:     */   }
/*  478:     */   
/*  479:     */   public void setMaxRadius(double newMaxRadius)
/*  480:     */   {
/*  481:1040 */     this.m_MaxRadius = newMaxRadius;
/*  482:     */   }
/*  483:     */   
/*  484:     */   public String maxRadiusTipText()
/*  485:     */   {
/*  486:1050 */     return "The upper boundary for the radius of the clusters.";
/*  487:     */   }
/*  488:     */   
/*  489:     */   protected SelectedTag defaultPattern()
/*  490:     */   {
/*  491:1059 */     return new SelectedTag(2, TAGS_PATTERN);
/*  492:     */   }
/*  493:     */   
/*  494:     */   public SelectedTag getPattern()
/*  495:     */   {
/*  496:1068 */     return new SelectedTag(this.m_Pattern, TAGS_PATTERN);
/*  497:     */   }
/*  498:     */   
/*  499:     */   public void setPattern(SelectedTag value)
/*  500:     */   {
/*  501:1077 */     if (value.getTags() == TAGS_PATTERN) {
/*  502:1078 */       this.m_Pattern = value.getSelectedTag().getID();
/*  503:     */     }
/*  504:     */   }
/*  505:     */   
/*  506:     */   public String patternTipText()
/*  507:     */   {
/*  508:1089 */     return "The pattern for generating the data.";
/*  509:     */   }
/*  510:     */   
/*  511:     */   protected double defaultDistMult()
/*  512:     */   {
/*  513:1098 */     return 4.0D;
/*  514:     */   }
/*  515:     */   
/*  516:     */   public double getDistMult()
/*  517:     */   {
/*  518:1107 */     return this.m_DistMult;
/*  519:     */   }
/*  520:     */   
/*  521:     */   public void setDistMult(double newDistMult)
/*  522:     */   {
/*  523:1116 */     this.m_DistMult = newDistMult;
/*  524:     */   }
/*  525:     */   
/*  526:     */   public String distMultTipText()
/*  527:     */   {
/*  528:1126 */     return "The distance multiplier (in combination with the 'Grid' pattern).";
/*  529:     */   }
/*  530:     */   
/*  531:     */   protected int defaultNumCycles()
/*  532:     */   {
/*  533:1135 */     return 4;
/*  534:     */   }
/*  535:     */   
/*  536:     */   public int getNumCycles()
/*  537:     */   {
/*  538:1144 */     return this.m_NumCycles;
/*  539:     */   }
/*  540:     */   
/*  541:     */   public void setNumCycles(int newNumCycles)
/*  542:     */   {
/*  543:1153 */     this.m_NumCycles = newNumCycles;
/*  544:     */   }
/*  545:     */   
/*  546:     */   public String numCyclesTipText()
/*  547:     */   {
/*  548:1163 */     return "The number of cycles to use (in combination with the 'Sine' pattern).";
/*  549:     */   }
/*  550:     */   
/*  551:     */   protected SelectedTag defaultInputOrder()
/*  552:     */   {
/*  553:1172 */     return new SelectedTag(0, TAGS_INPUTORDER);
/*  554:     */   }
/*  555:     */   
/*  556:     */   public SelectedTag getInputOrder()
/*  557:     */   {
/*  558:1184 */     return new SelectedTag(this.m_InputOrder, TAGS_INPUTORDER);
/*  559:     */   }
/*  560:     */   
/*  561:     */   public void setInputOrder(SelectedTag value)
/*  562:     */   {
/*  563:1193 */     if (value.getTags() == TAGS_INPUTORDER) {
/*  564:1194 */       this.m_InputOrder = value.getSelectedTag().getID();
/*  565:     */     }
/*  566:     */   }
/*  567:     */   
/*  568:     */   public String inputOrderTipText()
/*  569:     */   {
/*  570:1205 */     return "The input order to use.";
/*  571:     */   }
/*  572:     */   
/*  573:     */   public boolean getOrderedFlag()
/*  574:     */   {
/*  575:1214 */     return this.m_InputOrder == 0;
/*  576:     */   }
/*  577:     */   
/*  578:     */   protected double defaultNoiseRate()
/*  579:     */   {
/*  580:1223 */     return 0.0D;
/*  581:     */   }
/*  582:     */   
/*  583:     */   public double getNoiseRate()
/*  584:     */   {
/*  585:1232 */     return this.m_NoiseRate;
/*  586:     */   }
/*  587:     */   
/*  588:     */   public void setNoiseRate(double newNoiseRate)
/*  589:     */   {
/*  590:1241 */     this.m_NoiseRate = newNoiseRate;
/*  591:     */   }
/*  592:     */   
/*  593:     */   public String noiseRateTipText()
/*  594:     */   {
/*  595:1251 */     return "The noise rate to use.";
/*  596:     */   }
/*  597:     */   
/*  598:     */   public boolean getSingleModeFlag()
/*  599:     */   {
/*  600:1261 */     return false;
/*  601:     */   }
/*  602:     */   
/*  603:     */   public Instances defineDataFormat()
/*  604:     */     throws Exception
/*  605:     */   {
/*  606:1273 */     Random random = new Random(getSeed());
/*  607:1274 */     setRandom(random);
/*  608:     */     
/*  609:1276 */     ArrayList<Attribute> attributes = new ArrayList(3);
/*  610:     */     
/*  611:1278 */     boolean classFlag = getClassFlag();
/*  612:     */     
/*  613:1280 */     ArrayList<String> classValues = null;
/*  614:1281 */     if (classFlag) {
/*  615:1282 */       classValues = new ArrayList(this.m_NumClusters);
/*  616:     */     }
/*  617:1286 */     for (int i = 0; i < getNumAttributes(); i++)
/*  618:     */     {
/*  619:1287 */       Attribute attribute = new Attribute("X" + i);
/*  620:1288 */       attributes.add(attribute);
/*  621:     */     }
/*  622:1291 */     if (classFlag)
/*  623:     */     {
/*  624:1292 */       for (int i = 0; i < this.m_NumClusters; i++) {
/*  625:1293 */         classValues.add("c" + i);
/*  626:     */       }
/*  627:1295 */       Attribute attribute = new Attribute("class", classValues);
/*  628:1296 */       attributes.add(attribute);
/*  629:     */     }
/*  630:1299 */     Instances dataset = new Instances(getRelationNameToUse(), attributes, 0);
/*  631:1300 */     if (classFlag) {
/*  632:1301 */       dataset.setClassIndex(getNumAttributes());
/*  633:     */     }
/*  634:1305 */     Instances format = new Instances(dataset, 0);
/*  635:1306 */     setDatasetFormat(format);
/*  636:     */     
/*  637:1308 */     this.m_ClusterList = defineClusters(random);
/*  638:     */     
/*  639:     */ 
/*  640:1311 */     return dataset;
/*  641:     */   }
/*  642:     */   
/*  643:     */   public Instance generateExample()
/*  644:     */     throws Exception
/*  645:     */   {
/*  646:1324 */     throw new Exception("Examples cannot be generated one by one.");
/*  647:     */   }
/*  648:     */   
/*  649:     */   public Instances generateExamples()
/*  650:     */     throws Exception
/*  651:     */   {
/*  652:1336 */     Random random = getRandom();
/*  653:1337 */     Instances data = getDatasetFormat();
/*  654:1338 */     if (data == null) {
/*  655:1339 */       throw new Exception("Dataset format not defined.");
/*  656:     */     }
/*  657:1343 */     if (getOrderedFlag()) {
/*  658:1344 */       data = generateExamples(random, data);
/*  659:     */     } else {
/*  660:1346 */       throw new Exception("RANDOMIZED is not yet implemented.");
/*  661:     */     }
/*  662:1349 */     return data;
/*  663:     */   }
/*  664:     */   
/*  665:     */   public Instances generateExamples(Random random, Instances format)
/*  666:     */     throws Exception
/*  667:     */   {
/*  668:1362 */     Instance example = null;
/*  669:1364 */     if (format == null) {
/*  670:1365 */       throw new Exception("Dataset format not defined.");
/*  671:     */     }
/*  672:1369 */     int cNum = 0;
/*  673:1370 */     Enumeration<Cluster> enm = new WekaEnumeration(this.m_ClusterList);
/*  674:1371 */     for (; enm.hasMoreElements(); cNum++)
/*  675:     */     {
/*  676:1372 */       Cluster cl = (Cluster)enm.nextElement();
/*  677:1373 */       double stdDev = cl.getStdDev();
/*  678:1374 */       int instNum = cl.getInstNum();
/*  679:1375 */       double[] center = cl.getCenter();
/*  680:1376 */       String cName = "c" + cNum;
/*  681:1378 */       for (int i = 0; i < instNum; i++)
/*  682:     */       {
/*  683:1380 */         example = generateInstance(format, random, stdDev, center, cName);
/*  684:1382 */         if (example != null) {
/*  685:1383 */           example.setDataset(format);
/*  686:     */         }
/*  687:1385 */         format.add(example);
/*  688:     */       }
/*  689:     */     }
/*  690:1389 */     return format;
/*  691:     */   }
/*  692:     */   
/*  693:     */   private Instance generateInstance(Instances format, Random randomG, double stdDev, double[] center, String cName)
/*  694:     */   {
/*  695:1406 */     int numAtts = getNumAttributes();
/*  696:1407 */     if (getClassFlag()) {
/*  697:1408 */       numAtts++;
/*  698:     */     }
/*  699:1411 */     double[] data = new double[numAtts];
/*  700:1412 */     for (int i = 0; i < getNumAttributes(); i++) {
/*  701:1413 */       data[i] = (randomG.nextGaussian() * stdDev + center[i]);
/*  702:     */     }
/*  703:1416 */     if (getClassFlag()) {
/*  704:1417 */       data[format.classIndex()] = format.classAttribute().indexOfValue(cName);
/*  705:     */     }
/*  706:1420 */     Instance example = new DenseInstance(1.0D, data);
/*  707:1421 */     example.setDataset(format);
/*  708:     */     
/*  709:1423 */     return example;
/*  710:     */   }
/*  711:     */   
/*  712:     */   private ArrayList<Cluster> defineClusters(Random random)
/*  713:     */     throws Exception
/*  714:     */   {
/*  715:1435 */     if (this.m_Pattern == 0) {
/*  716:1436 */       return defineClustersGRID(random);
/*  717:     */     }
/*  718:1438 */     return defineClustersRANDOM(random);
/*  719:     */   }
/*  720:     */   
/*  721:     */   private ArrayList<Cluster> defineClustersGRID(Random random)
/*  722:     */     throws Exception
/*  723:     */   {
/*  724:1451 */     ArrayList<Cluster> clusters = new ArrayList(this.m_NumClusters);
/*  725:1452 */     double diffInstNum = this.m_MaxInstNum - this.m_MinInstNum;
/*  726:1453 */     double minInstNum = this.m_MinInstNum;
/*  727:1454 */     double diffRadius = this.m_MaxRadius - this.m_MinRadius;
/*  728:     */     
/*  729:     */ 
/*  730:     */ 
/*  731:1458 */     double gs = Math.pow(this.m_NumClusters, 1.0D / getNumAttributes());
/*  732:1460 */     if (gs - (int)gs > 0.0D) {
/*  733:1461 */       this.m_GridSize = ((int)(gs + 1.0D));
/*  734:     */     } else {
/*  735:1463 */       this.m_GridSize = ((int)gs);
/*  736:     */     }
/*  737:1467 */     this.m_GridWidth = ((this.m_MaxRadius + this.m_MinRadius) / 2.0D * this.m_DistMult);
/*  738:     */     
/*  739:     */ 
/*  740:     */ 
/*  741:     */ 
/*  742:     */ 
/*  743:1473 */     GridVector gv = new GridVector(getNumAttributes(), this.m_GridSize, null);
/*  744:1475 */     for (int i = 0; i < this.m_NumClusters; i++)
/*  745:     */     {
/*  746:1476 */       int instNum = (int)(random.nextDouble() * diffInstNum + minInstNum);
/*  747:1477 */       double radius = random.nextDouble() * diffRadius + this.m_MinRadius;
/*  748:     */       
/*  749:     */ 
/*  750:1480 */       Cluster cluster = new Cluster(instNum, radius, gv.getGridVector(), this.m_GridWidth, null);
/*  751:1481 */       clusters.add(cluster);
/*  752:1482 */       gv.addOne();
/*  753:     */     }
/*  754:1484 */     return clusters;
/*  755:     */   }
/*  756:     */   
/*  757:     */   private ArrayList<Cluster> defineClustersRANDOM(Random random)
/*  758:     */     throws Exception
/*  759:     */   {
/*  760:1497 */     ArrayList<Cluster> clusters = new ArrayList(this.m_NumClusters);
/*  761:1498 */     double diffInstNum = this.m_MaxInstNum - this.m_MinInstNum;
/*  762:1499 */     double minInstNum = this.m_MinInstNum;
/*  763:1500 */     double diffRadius = this.m_MaxRadius - this.m_MinRadius;
/*  764:1503 */     for (int i = 0; i < this.m_NumClusters; i++)
/*  765:     */     {
/*  766:1504 */       int instNum = (int)(random.nextDouble() * diffInstNum + minInstNum);
/*  767:1505 */       double radius = random.nextDouble() * diffRadius + this.m_MinRadius;
/*  768:     */       
/*  769:     */ 
/*  770:1508 */       Cluster cluster = new Cluster(instNum, radius, random, null);
/*  771:1509 */       clusters.add(cluster);
/*  772:     */     }
/*  773:1511 */     return clusters;
/*  774:     */   }
/*  775:     */   
/*  776:     */   public String generateFinished()
/*  777:     */     throws Exception
/*  778:     */   {
/*  779:1523 */     return "";
/*  780:     */   }
/*  781:     */   
/*  782:     */   public String generateStart()
/*  783:     */   {
/*  784:1534 */     StringBuffer docu = new StringBuffer();
/*  785:     */     
/*  786:1536 */     int sumInst = 0;
/*  787:1537 */     int cNum = 0;
/*  788:1538 */     Enumeration<Cluster> enm = new WekaEnumeration(this.m_ClusterList);
/*  789:1539 */     for (; enm.hasMoreElements(); cNum++)
/*  790:     */     {
/*  791:1540 */       Cluster cl = (Cluster)enm.nextElement();
/*  792:1541 */       docu.append("%\n");
/*  793:1542 */       docu.append("% Cluster: c" + cNum + "\n");
/*  794:1543 */       docu.append("% ----------------------------------------------\n");
/*  795:1544 */       docu.append("% StandardDeviation: " + Utils.doubleToString(cl.getStdDev(), 2) + "\n");
/*  796:     */       
/*  797:1546 */       docu.append("% Number of instances: " + cl.getInstNum() + "\n");
/*  798:1547 */       sumInst += cl.getInstNum();
/*  799:1548 */       double[] center = cl.getCenter();
/*  800:1549 */       docu.append("% ");
/*  801:1550 */       for (int i = 0; i < center.length - 1; i++) {
/*  802:1551 */         docu.append(Utils.doubleToString(center[i], 2) + ", ");
/*  803:     */       }
/*  804:1553 */       docu.append(Utils.doubleToString(center[(center.length - 1)], 2) + "\n");
/*  805:     */     }
/*  806:1555 */     docu.append("%\n% ----------------------------------------------\n");
/*  807:1556 */     docu.append("% Total number of instances: " + sumInst + "\n");
/*  808:1557 */     docu.append("%                            in " + cNum + " clusters\n");
/*  809:1558 */     docu.append("% Pattern chosen           : ");
/*  810:1559 */     if (this.m_Pattern == 0) {
/*  811:1560 */       docu.append("GRID, distance multiplier = " + Utils.doubleToString(this.m_DistMult, 2) + "\n");
/*  812:1562 */     } else if (this.m_Pattern == 1) {
/*  813:1563 */       docu.append("SINE\n");
/*  814:     */     } else {
/*  815:1565 */       docu.append("RANDOM\n");
/*  816:     */     }
/*  817:1568 */     return docu.toString();
/*  818:     */   }
/*  819:     */   
/*  820:     */   public String getRevision()
/*  821:     */   {
/*  822:1578 */     return RevisionUtils.extract("$Revision: 12471 $");
/*  823:     */   }
/*  824:     */   
/*  825:     */   public static void main(String[] args)
/*  826:     */   {
/*  827:1587 */     runDataGenerator(new BIRCHCluster(), args);
/*  828:     */   }
/*  829:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.datagenerators.clusterers.BIRCHCluster
 * JD-Core Version:    0.7.0.1
 */