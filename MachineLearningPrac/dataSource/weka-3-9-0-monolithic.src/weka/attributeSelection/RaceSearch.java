/*    1:     */ package weka.attributeSelection;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.BitSet;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.Random;
/*    8:     */ import java.util.Vector;
/*    9:     */ import weka.core.Attribute;
/*   10:     */ import weka.core.Instance;
/*   11:     */ import weka.core.Instances;
/*   12:     */ import weka.core.Option;
/*   13:     */ import weka.core.OptionHandler;
/*   14:     */ import weka.core.RevisionUtils;
/*   15:     */ import weka.core.SelectedTag;
/*   16:     */ import weka.core.Statistics;
/*   17:     */ import weka.core.Tag;
/*   18:     */ import weka.core.TechnicalInformation;
/*   19:     */ import weka.core.TechnicalInformation.Field;
/*   20:     */ import weka.core.TechnicalInformation.Type;
/*   21:     */ import weka.core.TechnicalInformationHandler;
/*   22:     */ import weka.core.Utils;
/*   23:     */ import weka.experiment.PairedStats;
/*   24:     */ import weka.experiment.Stats;
/*   25:     */ 
/*   26:     */ public class RaceSearch
/*   27:     */   extends ASSearch
/*   28:     */   implements RankedOutputSearch, OptionHandler, TechnicalInformationHandler
/*   29:     */ {
/*   30:     */   static final long serialVersionUID = 4015453851212985720L;
/*   31: 191 */   private Instances m_Instances = null;
/*   32:     */   private static final int FORWARD_RACE = 0;
/*   33:     */   private static final int BACKWARD_RACE = 1;
/*   34:     */   private static final int SCHEMATA_RACE = 2;
/*   35:     */   private static final int RANK_RACE = 3;
/*   36: 198 */   public static final Tag[] TAGS_SELECTION = { new Tag(0, "Forward selection race"), new Tag(1, "Backward elimination race"), new Tag(2, "Schemata race"), new Tag(3, "Rank race") };
/*   37: 204 */   private int m_raceType = 0;
/*   38:     */   private static final int TEN_FOLD = 0;
/*   39:     */   private static final int LEAVE_ONE_OUT = 1;
/*   40: 209 */   public static final Tag[] XVALTAGS_SELECTION = { new Tag(0, "10 Fold"), new Tag(1, "Leave-one-out") };
/*   41: 213 */   private int m_xvalType = 0;
/*   42:     */   private int m_classIndex;
/*   43:     */   private int m_numAttribs;
/*   44: 225 */   private double m_bestMerit = -1.797693134862316E+308D;
/*   45: 228 */   private HoldOutSubsetEvaluator m_theEvaluator = null;
/*   46: 231 */   private double m_sigLevel = 0.001D;
/*   47: 234 */   private double m_delta = 0.001D;
/*   48: 240 */   private final int m_samples = 20;
/*   49: 246 */   private int m_numFolds = 10;
/*   50: 252 */   private ASEvaluation m_ASEval = new GainRatioAttributeEval();
/*   51:     */   private int[] m_Ranking;
/*   52: 261 */   private boolean m_debug = false;
/*   53: 267 */   private boolean m_rankingRequested = false;
/*   54:     */   private double[][] m_rankedAtts;
/*   55:     */   private int m_rankedSoFar;
/*   56: 279 */   private int m_numToSelect = -1;
/*   57: 281 */   private int m_calculatedNumToSelect = -1;
/*   58: 284 */   private double m_threshold = -1.797693134862316E+308D;
/*   59:     */   
/*   60:     */   public String globalInfo()
/*   61:     */   {
/*   62: 293 */     return "Races the cross validation error of competing attribute subsets. Use in conjuction with a ClassifierSubsetEval. RaceSearch has four modes:\n\nforward selection races all single attribute additions to a base set (initially  no attributes), selects the winner to become the new base set and then iterates until there is no improvement over the base set. \n\nBackward elimination is similar but the initial base set has all attributes included and races all single attribute deletions. \n\nSchemata search is a bit different. Each iteration a series of races are run in parallel. Each race in a set determines whether a particular attribute should be included or not---ie the race is between the attribute being \"in\" or \"out\". The other attributes for this race are included or excluded randomly at each point in the evaluation. As soon as one race has a clear winner (ie it has been decided whether a particular attribute should be inor not) then the next set of races begins, using the result of the winning race from the previous iteration as new base set.\n\nRank race first ranks the attributes using an attribute evaluator and then races the ranking. The race includes no attributes, the top ranked attribute, the top two attributes, the top three attributes, etc.\n\nIt is also possible to generate a raked list of attributes through the forward racing process. If generateRanking is set to true then a complete forward race will be run---that is, racing continues until all attributes have been selected. The order that they are added in determines a complete ranking of all the attributes.\n\nRacing uses paired and unpaired t-tests on cross-validation errors of competing subsets. When there is a significant difference between the means of the errors of two competing subsets then the poorer of the two can be eliminated from the race. Similarly, if there is no significant difference between the mean errors of two competing subsets and they are within some threshold of each other, then one can be eliminated from the race.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*   63:     */   }
/*   64:     */   
/*   65:     */   public TechnicalInformation getTechnicalInformation()
/*   66:     */   {
/*   67: 339 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*   68: 340 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Andrew W. Moore and Mary S. Lee");
/*   69: 341 */     result.setValue(TechnicalInformation.Field.TITLE, "Efficient Algorithms for Minimizing Cross Validation Error");
/*   70:     */     
/*   71: 343 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Eleventh International Conference on Machine Learning");
/*   72:     */     
/*   73: 345 */     result.setValue(TechnicalInformation.Field.YEAR, "1994");
/*   74: 346 */     result.setValue(TechnicalInformation.Field.PAGES, "190-198");
/*   75: 347 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann");
/*   76:     */     
/*   77: 349 */     return result;
/*   78:     */   }
/*   79:     */   
/*   80:     */   public String raceTypeTipText()
/*   81:     */   {
/*   82: 359 */     return "Set the type of search.";
/*   83:     */   }
/*   84:     */   
/*   85:     */   public void setRaceType(SelectedTag d)
/*   86:     */   {
/*   87: 369 */     if (d.getTags() == TAGS_SELECTION) {
/*   88: 370 */       this.m_raceType = d.getSelectedTag().getID();
/*   89:     */     }
/*   90: 372 */     if ((this.m_raceType == 2) && (!this.m_rankingRequested)) {
/*   91:     */       try
/*   92:     */       {
/*   93: 374 */         setFoldsType(new SelectedTag(1, XVALTAGS_SELECTION));
/*   94: 375 */         setSignificanceLevel(0.01D);
/*   95:     */       }
/*   96:     */       catch (Exception ex) {}
/*   97:     */     } else {
/*   98:     */       try
/*   99:     */       {
/*  100: 380 */         setFoldsType(new SelectedTag(0, XVALTAGS_SELECTION));
/*  101: 381 */         setSignificanceLevel(0.001D);
/*  102:     */       }
/*  103:     */       catch (Exception ex) {}
/*  104:     */     }
/*  105:     */   }
/*  106:     */   
/*  107:     */   public SelectedTag getRaceType()
/*  108:     */   {
/*  109: 393 */     return new SelectedTag(this.m_raceType, TAGS_SELECTION);
/*  110:     */   }
/*  111:     */   
/*  112:     */   public String significanceLevelTipText()
/*  113:     */   {
/*  114: 403 */     return "Set the significance level to use for t-test comparisons.";
/*  115:     */   }
/*  116:     */   
/*  117:     */   public void setSignificanceLevel(double sig)
/*  118:     */   {
/*  119: 412 */     this.m_sigLevel = sig;
/*  120:     */   }
/*  121:     */   
/*  122:     */   public double getSignificanceLevel()
/*  123:     */   {
/*  124: 421 */     return this.m_sigLevel;
/*  125:     */   }
/*  126:     */   
/*  127:     */   public String thresholdTipText()
/*  128:     */   {
/*  129: 431 */     return "Set the error threshold by which to consider two subsets equivalent.";
/*  130:     */   }
/*  131:     */   
/*  132:     */   public void setThreshold(double t)
/*  133:     */   {
/*  134: 442 */     this.m_delta = t;
/*  135:     */   }
/*  136:     */   
/*  137:     */   public double getThreshold()
/*  138:     */   {
/*  139: 452 */     return this.m_delta;
/*  140:     */   }
/*  141:     */   
/*  142:     */   public String foldsTypeTipText()
/*  143:     */   {
/*  144: 462 */     return "Set the number of folds to use for x-val error estimation; leave-one-out is selected automatically for schemata search.";
/*  145:     */   }
/*  146:     */   
/*  147:     */   public void setFoldsType(SelectedTag d)
/*  148:     */   {
/*  149: 473 */     if (d.getTags() == XVALTAGS_SELECTION) {
/*  150: 474 */       this.m_xvalType = d.getSelectedTag().getID();
/*  151:     */     }
/*  152:     */   }
/*  153:     */   
/*  154:     */   public SelectedTag getFoldsType()
/*  155:     */   {
/*  156: 484 */     return new SelectedTag(this.m_xvalType, XVALTAGS_SELECTION);
/*  157:     */   }
/*  158:     */   
/*  159:     */   public String debugTipText()
/*  160:     */   {
/*  161: 494 */     return "Turn on verbose output for monitoring the search's progress.";
/*  162:     */   }
/*  163:     */   
/*  164:     */   public void setDebug(boolean d)
/*  165:     */   {
/*  166: 503 */     this.m_debug = d;
/*  167:     */   }
/*  168:     */   
/*  169:     */   public boolean getDebug()
/*  170:     */   {
/*  171: 512 */     return this.m_debug;
/*  172:     */   }
/*  173:     */   
/*  174:     */   public String attributeEvaluatorTipText()
/*  175:     */   {
/*  176: 522 */     return "Attribute evaluator to use for generating an initial ranking. Use in conjunction with a rank race";
/*  177:     */   }
/*  178:     */   
/*  179:     */   public void setAttributeEvaluator(ASEvaluation newEvaluator)
/*  180:     */   {
/*  181: 532 */     this.m_ASEval = newEvaluator;
/*  182:     */   }
/*  183:     */   
/*  184:     */   public ASEvaluation getAttributeEvaluator()
/*  185:     */   {
/*  186: 541 */     return this.m_ASEval;
/*  187:     */   }
/*  188:     */   
/*  189:     */   public String generateRankingTipText()
/*  190:     */   {
/*  191: 551 */     return "Use the racing process to generate a ranked list of attributes. Using this mode forces the race to be a forward type and then races until all attributes have been added, thus giving a ranked list";
/*  192:     */   }
/*  193:     */   
/*  194:     */   public void setGenerateRanking(boolean doRank)
/*  195:     */   {
/*  196: 563 */     this.m_rankingRequested = doRank;
/*  197: 564 */     if (this.m_rankingRequested) {
/*  198:     */       try
/*  199:     */       {
/*  200: 566 */         setRaceType(new SelectedTag(0, TAGS_SELECTION));
/*  201:     */       }
/*  202:     */       catch (Exception ex) {}
/*  203:     */     }
/*  204:     */   }
/*  205:     */   
/*  206:     */   public boolean getGenerateRanking()
/*  207:     */   {
/*  208: 581 */     return this.m_rankingRequested;
/*  209:     */   }
/*  210:     */   
/*  211:     */   public String numToSelectTipText()
/*  212:     */   {
/*  213: 591 */     return "Specify the number of attributes to retain. Use in conjunction with generateRanking. The default value (-1) indicates that all attributes are to be retained. Use either this option or a threshold to reduce the attribute set.";
/*  214:     */   }
/*  215:     */   
/*  216:     */   public void setNumToSelect(int n)
/*  217:     */   {
/*  218: 605 */     this.m_numToSelect = n;
/*  219:     */   }
/*  220:     */   
/*  221:     */   public int getNumToSelect()
/*  222:     */   {
/*  223: 615 */     return this.m_numToSelect;
/*  224:     */   }
/*  225:     */   
/*  226:     */   public int getCalculatedNumToSelect()
/*  227:     */   {
/*  228: 626 */     if (this.m_numToSelect >= 0) {
/*  229: 627 */       this.m_calculatedNumToSelect = this.m_numToSelect;
/*  230:     */     }
/*  231: 629 */     return this.m_calculatedNumToSelect;
/*  232:     */   }
/*  233:     */   
/*  234:     */   public String selectionThresholdTipText()
/*  235:     */   {
/*  236: 639 */     return "Set threshold by which attributes can be discarded. Default value results in no attributes being discarded. Use in conjunction with generateRanking";
/*  237:     */   }
/*  238:     */   
/*  239:     */   public void setSelectionThreshold(double threshold)
/*  240:     */   {
/*  241: 651 */     this.m_threshold = threshold;
/*  242:     */   }
/*  243:     */   
/*  244:     */   public double getSelectionThreshold()
/*  245:     */   {
/*  246: 659 */     return this.m_threshold;
/*  247:     */   }
/*  248:     */   
/*  249:     */   public Enumeration<Option> listOptions()
/*  250:     */   {
/*  251: 669 */     Vector<Option> newVector = new Vector();
/*  252:     */     
/*  253: 671 */     newVector.addElement(new Option("\tType of race to perform.\n\t(default = 0).", "R", 1, "-R <0 = forward | 1 = backward race | 2 = schemata | 3 = rank>"));
/*  254:     */     
/*  255:     */ 
/*  256:     */ 
/*  257: 675 */     newVector.addElement(new Option("\tSignificance level for comaparisons\n\t(default = 0.001(forward/backward/rank)/0.01(schemata)).", "L", 1, "-L <significance>"));
/*  258:     */     
/*  259:     */ 
/*  260:     */ 
/*  261: 679 */     newVector.addElement(new Option("\tThreshold for error comparison.\n\t(default = 0.001).", "T", 1, "-T <threshold>"));
/*  262:     */     
/*  263:     */ 
/*  264: 682 */     newVector.addElement(new Option("\tAttribute ranker to use if doing a \n\trank search. Place any\n\tevaluator options LAST on \n\tthe command line following a \"--\".\n\teg. -A weka.attributeSelection.GainRatioAttributeEval ... -- -M.\n\t(default = GainRatioAttributeEval)", "A", 1, "-A <attribute evaluator>"));
/*  265:     */     
/*  266:     */ 
/*  267:     */ 
/*  268:     */ 
/*  269:     */ 
/*  270:     */ 
/*  271: 689 */     newVector.addElement(new Option("\tFolds for cross validation\n\t(default = 0 (1 if schemata race)", "F", 1, "-F <0 = 10 fold | 1 = leave-one-out>"));
/*  272:     */     
/*  273:     */ 
/*  274:     */ 
/*  275: 693 */     newVector.addElement(new Option("\tGenerate a ranked list of attributes.\n\tForces the search to be forward\n\tand races until all attributes have\n\tselected, thus producing a ranking.", "Q", 0, "-Q"));
/*  276:     */     
/*  277:     */ 
/*  278:     */ 
/*  279:     */ 
/*  280: 698 */     newVector.addElement(new Option("\tSpecify number of attributes to retain from \n\tthe ranking. Overides -T. Use in conjunction with -Q", "N", 1, "-N <num to select>"));
/*  281:     */     
/*  282:     */ 
/*  283:     */ 
/*  284:     */ 
/*  285: 703 */     newVector.addElement(new Option("\tSpecify a theshold by which attributes\n\tmay be discarded from the ranking.\n\tUse in conjuction with -Q", "J", 1, "-J <threshold>"));
/*  286:     */     
/*  287:     */ 
/*  288:     */ 
/*  289:     */ 
/*  290: 708 */     newVector.addElement(new Option("\tVerbose output for monitoring the search.", "Z", 0, "-Z"));
/*  291: 711 */     if ((this.m_ASEval != null) && ((this.m_ASEval instanceof OptionHandler)))
/*  292:     */     {
/*  293: 712 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to evaluator " + this.m_ASEval.getClass().getName() + ":"));
/*  294:     */       
/*  295:     */ 
/*  296:     */ 
/*  297: 716 */       newVector.addAll(Collections.list(((OptionHandler)this.m_ASEval).listOptions()));
/*  298:     */     }
/*  299: 720 */     return newVector.elements();
/*  300:     */   }
/*  301:     */   
/*  302:     */   public void setOptions(String[] options)
/*  303:     */     throws Exception
/*  304:     */   {
/*  305: 807 */     resetOptions();
/*  306:     */     
/*  307: 809 */     String optionString = Utils.getOption('R', options);
/*  308: 810 */     if (optionString.length() != 0) {
/*  309: 811 */       setRaceType(new SelectedTag(Integer.parseInt(optionString), TAGS_SELECTION));
/*  310:     */     }
/*  311: 815 */     optionString = Utils.getOption('F', options);
/*  312: 816 */     if (optionString.length() != 0) {
/*  313: 817 */       setFoldsType(new SelectedTag(Integer.parseInt(optionString), XVALTAGS_SELECTION));
/*  314:     */     }
/*  315: 821 */     optionString = Utils.getOption('L', options);
/*  316: 822 */     if (optionString.length() != 0) {
/*  317: 823 */       setSignificanceLevel(Double.parseDouble(optionString));
/*  318:     */     }
/*  319: 826 */     optionString = Utils.getOption('T', options);
/*  320: 827 */     if (optionString.length() != 0) {
/*  321: 828 */       setThreshold(Double.parseDouble(optionString));
/*  322:     */     }
/*  323: 831 */     optionString = Utils.getOption('A', options);
/*  324: 832 */     if (optionString.length() != 0) {
/*  325: 833 */       setAttributeEvaluator(ASEvaluation.forName(optionString, Utils.partitionOptions(options)));
/*  326:     */     }
/*  327: 837 */     setGenerateRanking(Utils.getFlag('Q', options));
/*  328:     */     
/*  329: 839 */     optionString = Utils.getOption('J', options);
/*  330: 840 */     if (optionString.length() != 0) {
/*  331: 841 */       setSelectionThreshold(Double.parseDouble(optionString));
/*  332:     */     }
/*  333: 844 */     optionString = Utils.getOption('N', options);
/*  334: 845 */     if (optionString.length() != 0) {
/*  335: 846 */       setNumToSelect(Integer.parseInt(optionString));
/*  336:     */     }
/*  337: 849 */     setDebug(Utils.getFlag('Z', options));
/*  338:     */   }
/*  339:     */   
/*  340:     */   public String[] getOptions()
/*  341:     */   {
/*  342: 860 */     Vector<String> options = new Vector();
/*  343:     */     
/*  344: 862 */     options.add("-R");
/*  345: 863 */     options.add("" + this.m_raceType);
/*  346: 864 */     options.add("-L");
/*  347: 865 */     options.add("" + getSignificanceLevel());
/*  348: 866 */     options.add("-T");
/*  349: 867 */     options.add("" + getThreshold());
/*  350: 868 */     options.add("-F");
/*  351: 869 */     options.add("" + this.m_xvalType);
/*  352: 870 */     if (getGenerateRanking()) {
/*  353: 871 */       options.add("-Q");
/*  354:     */     }
/*  355: 873 */     options.add("-N");
/*  356: 874 */     options.add("" + getNumToSelect());
/*  357: 875 */     options.add("-J");
/*  358: 876 */     options.add("" + getSelectionThreshold());
/*  359: 877 */     if (getDebug()) {
/*  360: 878 */       options.add("-Z");
/*  361:     */     }
/*  362: 881 */     if ((this.m_ASEval != null) && ((this.m_ASEval instanceof OptionHandler)))
/*  363:     */     {
/*  364: 882 */       String[] evaluatorOptions = ((OptionHandler)this.m_ASEval).getOptions();
/*  365: 883 */       options.add("-A");
/*  366: 884 */       options.add(getAttributeEvaluator().getClass().getName());
/*  367: 885 */       options.add("--");
/*  368: 886 */       Collections.addAll(options, evaluatorOptions);
/*  369:     */     }
/*  370: 889 */     return (String[])options.toArray(new String[0]);
/*  371:     */   }
/*  372:     */   
/*  373:     */   public int[] search(ASEvaluation ASEval, Instances data)
/*  374:     */     throws Exception
/*  375:     */   {
/*  376: 903 */     if (!(ASEval instanceof SubsetEvaluator)) {
/*  377: 904 */       throw new Exception(ASEval.getClass().getName() + " is not a " + "Subset evaluator! (RaceSearch)");
/*  378:     */     }
/*  379: 908 */     if ((ASEval instanceof UnsupervisedSubsetEvaluator)) {
/*  380: 909 */       throw new Exception("Can't use an unsupervised subset evaluator (RaceSearch).");
/*  381:     */     }
/*  382: 913 */     if (!(ASEval instanceof HoldOutSubsetEvaluator)) {
/*  383: 914 */       throw new Exception("Must use a HoldOutSubsetEvaluator, eg. weka.attributeSelection.ClassifierSubsetEval (RaceSearch)");
/*  384:     */     }
/*  385: 918 */     if (!(ASEval instanceof ErrorBasedMeritEvaluator)) {
/*  386: 919 */       throw new Exception("Only error based subset evaluators can be used, eg. weka.attributeSelection.ClassifierSubsetEval (RaceSearch)");
/*  387:     */     }
/*  388: 923 */     this.m_Instances = new Instances(data);
/*  389: 924 */     this.m_Instances.deleteWithMissingClass();
/*  390: 925 */     if (this.m_Instances.numInstances() == 0) {
/*  391: 926 */       throw new Exception("All train instances have missing class! (RaceSearch)");
/*  392:     */     }
/*  393: 929 */     if ((this.m_rankingRequested) && (this.m_numToSelect > this.m_Instances.numAttributes() - 1)) {
/*  394: 930 */       throw new Exception("More attributes requested than exist in the data (RaceSearch).");
/*  395:     */     }
/*  396: 933 */     this.m_theEvaluator = ((HoldOutSubsetEvaluator)ASEval);
/*  397: 934 */     this.m_numAttribs = this.m_Instances.numAttributes();
/*  398: 935 */     this.m_classIndex = this.m_Instances.classIndex();
/*  399: 937 */     if (this.m_rankingRequested)
/*  400:     */     {
/*  401: 938 */       this.m_rankedAtts = new double[this.m_numAttribs - 1][2];
/*  402: 939 */       this.m_rankedSoFar = 0;
/*  403:     */     }
/*  404: 942 */     if (this.m_xvalType == 1) {
/*  405: 943 */       this.m_numFolds = this.m_Instances.numInstances();
/*  406:     */     } else {
/*  407: 945 */       this.m_numFolds = 10;
/*  408:     */     }
/*  409: 948 */     Random random = new Random(1L);
/*  410: 949 */     this.m_Instances.randomize(random);
/*  411: 950 */     int[] bestSubset = null;
/*  412: 952 */     switch (this.m_raceType)
/*  413:     */     {
/*  414:     */     case 0: 
/*  415:     */     case 1: 
/*  416: 955 */       bestSubset = hillclimbRace(this.m_Instances, random);
/*  417: 956 */       break;
/*  418:     */     case 2: 
/*  419: 958 */       bestSubset = schemataRace(this.m_Instances, random);
/*  420: 959 */       break;
/*  421:     */     case 3: 
/*  422: 961 */       bestSubset = rankRace(this.m_Instances, random);
/*  423:     */     }
/*  424: 965 */     return bestSubset;
/*  425:     */   }
/*  426:     */   
/*  427:     */   public double[][] rankedAttributes()
/*  428:     */     throws Exception
/*  429:     */   {
/*  430: 970 */     if (!this.m_rankingRequested) {
/*  431: 971 */       throw new Exception("Need to request a ranked list of attributes before attributes can be ranked (RaceSearch).");
/*  432:     */     }
/*  433: 974 */     if (this.m_rankedAtts == null) {
/*  434: 975 */       throw new Exception("Search must be performed before attributes can be ranked (RaceSearch).");
/*  435:     */     }
/*  436: 979 */     double[][] final_rank = new double[this.m_rankedSoFar][2];
/*  437: 980 */     for (int i = 0; i < this.m_rankedSoFar; i++)
/*  438:     */     {
/*  439: 981 */       final_rank[i][0] = this.m_rankedAtts[i][0];
/*  440: 982 */       final_rank[i][1] = this.m_rankedAtts[i][1];
/*  441:     */     }
/*  442: 985 */     if (this.m_numToSelect <= 0) {
/*  443: 986 */       if (this.m_threshold == -1.797693134862316E+308D) {
/*  444: 987 */         this.m_calculatedNumToSelect = final_rank.length;
/*  445:     */       } else {
/*  446: 989 */         determineNumToSelectFromThreshold(final_rank);
/*  447:     */       }
/*  448:     */     }
/*  449: 993 */     return final_rank;
/*  450:     */   }
/*  451:     */   
/*  452:     */   private void determineNumToSelectFromThreshold(double[][] ranking)
/*  453:     */   {
/*  454: 997 */     int count = 0;
/*  455: 998 */     for (double[] element : ranking) {
/*  456: 999 */       if (element[1] > this.m_threshold) {
/*  457:1000 */         count++;
/*  458:     */       }
/*  459:     */     }
/*  460:1003 */     this.m_calculatedNumToSelect = count;
/*  461:     */   }
/*  462:     */   
/*  463:     */   private String printSets(char[][] raceSets)
/*  464:     */   {
/*  465:1010 */     StringBuffer temp = new StringBuffer();
/*  466:1011 */     for (char[] raceSet : raceSets)
/*  467:     */     {
/*  468:1012 */       for (int j = 0; j < this.m_numAttribs; j++) {
/*  469:1013 */         temp.append(raceSet[j]);
/*  470:     */       }
/*  471:1015 */       temp.append('\n');
/*  472:     */     }
/*  473:1017 */     return temp.toString();
/*  474:     */   }
/*  475:     */   
/*  476:     */   private int[] schemataRace(Instances data, Random random)
/*  477:     */     throws Exception
/*  478:     */   {
/*  479:1030 */     int numRaces = this.m_numAttribs - 1;
/*  480:1031 */     Random r = new Random(42L);
/*  481:1032 */     int numInstances = data.numInstances();
/*  482:     */     
/*  483:     */ 
/*  484:     */ 
/*  485:     */ 
/*  486:     */ 
/*  487:1038 */     Stats[][] raceStats = new Stats[numRaces][2];
/*  488:     */     
/*  489:1040 */     char[][][] parallelRaces = new char[numRaces][2][this.m_numAttribs - 1];
/*  490:1041 */     char[] base = new char[this.m_numAttribs];
/*  491:1042 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  492:1043 */       base[i] = '*';
/*  493:     */     }
/*  494:1046 */     int count = 0;
/*  495:1048 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  496:1049 */       if (i != this.m_classIndex)
/*  497:     */       {
/*  498:1050 */         parallelRaces[count][0] = ((char[])base.clone());
/*  499:1051 */         parallelRaces[count][1] = ((char[])base.clone());
/*  500:1052 */         parallelRaces[count][0][i] = 49;
/*  501:1053 */         parallelRaces[(count++)][1][i] = 48;
/*  502:     */       }
/*  503:     */     }
/*  504:1057 */     if (this.m_debug)
/*  505:     */     {
/*  506:1058 */       System.err.println("Initial sets:\n");
/*  507:1059 */       for (int i = 0; i < numRaces; i++) {
/*  508:1060 */         System.err.print(printSets(parallelRaces[i]) + "--------------\n");
/*  509:     */       }
/*  510:     */     }
/*  511:1064 */     BitSet randomB = new BitSet(this.m_numAttribs);
/*  512:1065 */     char[] randomBC = new char[this.m_numAttribs];
/*  513:     */     
/*  514:     */ 
/*  515:1068 */     boolean[] attributeConstraints = new boolean[this.m_numAttribs];
/*  516:     */     
/*  517:1070 */     int evaluationCount = 0;
/*  518:1071 */     while (numRaces > 0)
/*  519:     */     {
/*  520:1072 */       boolean won = false;
/*  521:1073 */       for (int i = 0; i < numRaces; i++)
/*  522:     */       {
/*  523:1074 */         raceStats[i][0] = new Stats();
/*  524:1075 */         raceStats[i][1] = new Stats();
/*  525:     */       }
/*  526:     */       label1017:
/*  527:1079 */       while (!won)
/*  528:     */       {
/*  529:1081 */         for (int i = 0; i < this.m_numAttribs; i++) {
/*  530:1082 */           if (i != this.m_classIndex) {
/*  531:1083 */             if (attributeConstraints[i] == 0)
/*  532:     */             {
/*  533:1084 */               if (r.nextDouble() < 0.5D) {
/*  534:1085 */                 randomB.set(i);
/*  535:     */               } else {
/*  536:1087 */                 randomB.clear(i);
/*  537:     */               }
/*  538:     */             }
/*  539:1090 */             else if (base[i] == '1') {
/*  540:1091 */               randomB.set(i);
/*  541:     */             } else {
/*  542:1093 */               randomB.clear(i);
/*  543:     */             }
/*  544:     */           }
/*  545:     */         }
/*  546:1100 */         int testIndex = Math.abs(r.nextInt() % numInstances);
/*  547:     */         
/*  548:     */ 
/*  549:     */ 
/*  550:1104 */         Instances trainCV = data.trainCV(numInstances, testIndex, new Random(1L));
/*  551:1105 */         Instances testCV = data.testCV(numInstances, testIndex);
/*  552:1106 */         Instance testInstance = testCV.instance(0);
/*  553:1107 */         this.m_theEvaluator.buildEvaluator(trainCV);
/*  554:     */         
/*  555:     */ 
/*  556:1110 */         double error = -this.m_theEvaluator.evaluateSubset(randomB, testInstance, true);
/*  557:1111 */         evaluationCount++;
/*  558:1114 */         for (int i = 0; i < this.m_numAttribs; i++) {
/*  559:1115 */           if (randomB.get(i)) {
/*  560:1116 */             randomBC[i] = '1';
/*  561:     */           } else {
/*  562:1118 */             randomBC[i] = '0';
/*  563:     */           }
/*  564:     */         }
/*  565:1123 */         for (int i = 0; i < numRaces; i++)
/*  566:     */         {
/*  567:1127 */           if ((raceStats[i][0].count + raceStats[i][1].count) / 2.0D > numInstances) {
/*  568:     */             break label1296;
/*  569:     */           }
/*  570:1130 */           for (int j = 0; j < 2; j++)
/*  571:     */           {
/*  572:1131 */             boolean matched = true;
/*  573:1132 */             for (int k = 0; k < this.m_numAttribs; k++) {
/*  574:1133 */               if ((parallelRaces[i][j][k] != '*') && 
/*  575:1134 */                 (parallelRaces[i][j][k] != randomBC[k]))
/*  576:     */               {
/*  577:1135 */                 matched = false;
/*  578:1136 */                 break;
/*  579:     */               }
/*  580:     */             }
/*  581:1140 */             if (matched)
/*  582:     */             {
/*  583:1142 */               raceStats[i][j].add(error);
/*  584:1146 */               if ((raceStats[i][0].count > 20.0D) && (raceStats[i][1].count > 20.0D))
/*  585:     */               {
/*  586:1148 */                 raceStats[i][0].calculateDerived();
/*  587:1149 */                 raceStats[i][1].calculateDerived();
/*  588:     */                 
/*  589:     */ 
/*  590:     */ 
/*  591:     */ 
/*  592:     */ 
/*  593:1155 */                 double prob = ttest(raceStats[i][0], raceStats[i][1]);
/*  594:1157 */                 if (prob < this.m_sigLevel)
/*  595:     */                 {
/*  596:1158 */                   if (raceStats[i][0].mean < raceStats[i][1].mean)
/*  597:     */                   {
/*  598:1159 */                     base = (char[])parallelRaces[i][0].clone();
/*  599:1160 */                     this.m_bestMerit = raceStats[i][0].mean;
/*  600:1161 */                     if (this.m_debug) {
/*  601:1162 */                       System.err.println("contender 0 won ");
/*  602:     */                     }
/*  603:     */                   }
/*  604:     */                   else
/*  605:     */                   {
/*  606:1165 */                     base = (char[])parallelRaces[i][1].clone();
/*  607:1166 */                     this.m_bestMerit = raceStats[i][1].mean;
/*  608:1167 */                     if (this.m_debug) {
/*  609:1168 */                       System.err.println("contender 1 won");
/*  610:     */                     }
/*  611:     */                   }
/*  612:1171 */                   if (this.m_debug)
/*  613:     */                   {
/*  614:1172 */                     System.err.println(new String(parallelRaces[i][0]) + " " + new String(parallelRaces[i][1]));
/*  615:     */                     
/*  616:1174 */                     System.err.println("Means : " + raceStats[i][0].mean + " vs" + raceStats[i][1].mean);
/*  617:     */                     
/*  618:1176 */                     System.err.println("Evaluations so far : " + evaluationCount);
/*  619:     */                   }
/*  620:1179 */                   won = true;
/*  621:     */                   break label1017;
/*  622:     */                 }
/*  623:     */               }
/*  624:     */             }
/*  625:     */           }
/*  626:     */         }
/*  627:     */       }
/*  628:1189 */       numRaces--;
/*  629:1191 */       if ((numRaces > 0) && (won))
/*  630:     */       {
/*  631:1192 */         parallelRaces = new char[numRaces][2][this.m_numAttribs - 1];
/*  632:1193 */         raceStats = new Stats[numRaces][2];
/*  633:1195 */         for (int i = 0; i < this.m_numAttribs; i++) {
/*  634:1196 */           if ((i != this.m_classIndex) && (attributeConstraints[i] == 0) && (base[i] != '*'))
/*  635:     */           {
/*  636:1197 */             attributeConstraints[i] = true;
/*  637:1198 */             break;
/*  638:     */           }
/*  639:     */         }
/*  640:1201 */         count = 0;
/*  641:1202 */         for (int i = 0; i < numRaces; i++)
/*  642:     */         {
/*  643:1203 */           parallelRaces[i][0] = ((char[])base.clone());
/*  644:1204 */           parallelRaces[i][1] = ((char[])base.clone());
/*  645:1205 */           for (int j = count; j < this.m_numAttribs; j++) {
/*  646:1206 */             if ((j != this.m_classIndex) && (parallelRaces[i][0][j] == '*'))
/*  647:     */             {
/*  648:1207 */               parallelRaces[i][0][j] = 49;
/*  649:1208 */               parallelRaces[i][1][j] = 48;
/*  650:1209 */               count = j + 1;
/*  651:1210 */               break;
/*  652:     */             }
/*  653:     */           }
/*  654:     */         }
/*  655:1215 */         if (this.m_debug)
/*  656:     */         {
/*  657:1216 */           System.err.println("Next sets:\n");
/*  658:1217 */           for (int i = 0; i < numRaces; i++) {
/*  659:1218 */             System.err.print(printSets(parallelRaces[i]) + "--------------\n");
/*  660:     */           }
/*  661:     */         }
/*  662:     */       }
/*  663:     */     }
/*  664:     */     label1296:
/*  665:1224 */     if (this.m_debug) {
/*  666:1225 */       System.err.println("Total evaluations : " + evaluationCount);
/*  667:     */     }
/*  668:1227 */     return attributeList(base);
/*  669:     */   }
/*  670:     */   
/*  671:     */   private double ttest(Stats c1, Stats c2)
/*  672:     */     throws Exception
/*  673:     */   {
/*  674:1235 */     double n1 = c1.count;
/*  675:1236 */     double n2 = c2.count;
/*  676:1237 */     double v1 = c1.stdDev * c1.stdDev;
/*  677:1238 */     double v2 = c2.stdDev * c2.stdDev;
/*  678:1239 */     double av1 = c1.mean;
/*  679:1240 */     double av2 = c2.mean;
/*  680:     */     
/*  681:1242 */     double df = n1 + n2 - 2.0D;
/*  682:1243 */     double cv = ((n1 - 1.0D) * v1 + (n2 - 1.0D) * v2) / df;
/*  683:1244 */     double t = (av1 - av2) / Math.sqrt(cv * (1.0D / n1 + 1.0D / n2));
/*  684:     */     
/*  685:1246 */     return Statistics.incompleteBeta(df / 2.0D, 0.5D, df / (df + t * t));
/*  686:     */   }
/*  687:     */   
/*  688:     */   private int[] rankRace(Instances data, Random random)
/*  689:     */     throws Exception
/*  690:     */   {
/*  691:1259 */     char[] baseSet = new char[this.m_numAttribs];
/*  692:1262 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  693:1263 */       if (i == this.m_classIndex) {
/*  694:1264 */         baseSet[i] = '-';
/*  695:     */       } else {
/*  696:1266 */         baseSet[i] = '0';
/*  697:     */       }
/*  698:     */     }
/*  699:1270 */     int numCompetitors = this.m_numAttribs - 1;
/*  700:1271 */     char[][] raceSets = new char[numCompetitors + 1][this.m_numAttribs];
/*  701:1273 */     if ((this.m_ASEval instanceof AttributeEvaluator))
/*  702:     */     {
/*  703:1275 */       Ranker ranker = new Ranker();
/*  704:1276 */       this.m_ASEval.buildEvaluator(data);
/*  705:1277 */       this.m_Ranking = ranker.search(this.m_ASEval, data);
/*  706:     */     }
/*  707:     */     else
/*  708:     */     {
/*  709:1279 */       GreedyStepwise fs = new GreedyStepwise();
/*  710:     */       
/*  711:1281 */       fs.setGenerateRanking(true);
/*  712:1282 */       this.m_ASEval.buildEvaluator(data);
/*  713:1283 */       fs.search(this.m_ASEval, data);
/*  714:1284 */       double[][] rankres = fs.rankedAttributes();
/*  715:1285 */       this.m_Ranking = new int[rankres.length];
/*  716:1286 */       for (int i = 0; i < rankres.length; i++) {
/*  717:1287 */         this.m_Ranking[i] = ((int)rankres[i][0]);
/*  718:     */       }
/*  719:     */     }
/*  720:1292 */     raceSets[0] = ((char[])baseSet.clone());
/*  721:1293 */     for (int i = 0; i < this.m_Ranking.length; i++)
/*  722:     */     {
/*  723:1294 */       raceSets[(i + 1)] = ((char[])raceSets[i].clone());
/*  724:1295 */       raceSets[(i + 1)][this.m_Ranking[i]] = 49;
/*  725:     */     }
/*  726:1298 */     if (this.m_debug) {
/*  727:1299 */       System.err.println("Initial sets:\n" + printSets(raceSets));
/*  728:     */     }
/*  729:1303 */     double[] winnerInfo = raceSubsets(raceSets, data, true, random);
/*  730:1304 */     double bestSetError = winnerInfo[1];
/*  731:1305 */     char[] bestSet = (char[])raceSets[((int)winnerInfo[0])].clone();
/*  732:1306 */     this.m_bestMerit = bestSetError;
/*  733:1307 */     return attributeList(bestSet);
/*  734:     */   }
/*  735:     */   
/*  736:     */   private int[] hillclimbRace(Instances data, Random random)
/*  737:     */     throws Exception
/*  738:     */   {
/*  739:1323 */     char[] baseSet = new char[this.m_numAttribs];
/*  740:1325 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  741:1326 */       if (i != this.m_classIndex)
/*  742:     */       {
/*  743:1327 */         if (this.m_raceType == 0) {
/*  744:1328 */           baseSet[i] = '0';
/*  745:     */         } else {
/*  746:1330 */           baseSet[i] = '1';
/*  747:     */         }
/*  748:     */       }
/*  749:     */       else {
/*  750:1333 */         baseSet[i] = '-';
/*  751:     */       }
/*  752:     */     }
/*  753:1337 */     int numCompetitors = this.m_numAttribs - 1;
/*  754:1338 */     char[][] raceSets = new char[numCompetitors + 1][this.m_numAttribs];
/*  755:     */     
/*  756:1340 */     raceSets[0] = ((char[])baseSet.clone());
/*  757:1341 */     int count = 1;
/*  758:1343 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  759:1344 */       if (i != this.m_classIndex)
/*  760:     */       {
/*  761:1345 */         raceSets[count] = ((char[])baseSet.clone());
/*  762:1346 */         if (this.m_raceType == 1) {
/*  763:1347 */           raceSets[(count++)][i] = 48;
/*  764:     */         } else {
/*  765:1349 */           raceSets[(count++)][i] = 49;
/*  766:     */         }
/*  767:     */       }
/*  768:     */     }
/*  769:1354 */     if (this.m_debug) {
/*  770:1355 */       System.err.println("Initial sets:\n" + printSets(raceSets));
/*  771:     */     }
/*  772:1359 */     double[] winnerInfo = raceSubsets(raceSets, data, true, random);
/*  773:1360 */     double baseSetError = winnerInfo[1];
/*  774:1361 */     this.m_bestMerit = baseSetError;
/*  775:1362 */     baseSet = (char[])raceSets[((int)winnerInfo[0])].clone();
/*  776:1363 */     if (this.m_rankingRequested)
/*  777:     */     {
/*  778:1364 */       this.m_rankedAtts[this.m_rankedSoFar][0] = ((int)(winnerInfo[0] - 1.0D));
/*  779:1365 */       this.m_rankedAtts[this.m_rankedSoFar][1] = winnerInfo[1];
/*  780:1366 */       this.m_rankedSoFar += 1;
/*  781:     */     }
/*  782:1369 */     boolean improved = true;
/*  783:1373 */     while (improved)
/*  784:     */     {
/*  785:1375 */       numCompetitors--;
/*  786:1376 */       if (numCompetitors == 0) {
/*  787:     */         break;
/*  788:     */       }
/*  789:1379 */       int j = 0;
/*  790:     */       
/*  791:     */ 
/*  792:     */ 
/*  793:1383 */       raceSets = new char[numCompetitors + 1][this.m_numAttribs];
/*  794:1384 */       for (int i = 0; i < numCompetitors + 1; i++)
/*  795:     */       {
/*  796:1385 */         raceSets[i] = ((char[])baseSet.clone());
/*  797:1386 */         if (i > 0) {
/*  798:1387 */           for (int k = j; k < this.m_numAttribs; k++) {
/*  799:1388 */             if (this.m_raceType == 1)
/*  800:     */             {
/*  801:1389 */               if ((k != this.m_classIndex) && (raceSets[i][k] != '0'))
/*  802:     */               {
/*  803:1390 */                 raceSets[i][k] = 48;
/*  804:1391 */                 j = k + 1;
/*  805:1392 */                 break;
/*  806:     */               }
/*  807:     */             }
/*  808:1395 */             else if ((k != this.m_classIndex) && (raceSets[i][k] != '1'))
/*  809:     */             {
/*  810:1396 */               raceSets[i][k] = 49;
/*  811:1397 */               j = k + 1;
/*  812:1398 */               break;
/*  813:     */             }
/*  814:     */           }
/*  815:     */         }
/*  816:     */       }
/*  817:1405 */       if (this.m_debug) {
/*  818:1406 */         System.err.println("Next set : \n" + printSets(raceSets));
/*  819:     */       }
/*  820:1408 */       improved = false;
/*  821:1409 */       winnerInfo = raceSubsets(raceSets, data, true, random);
/*  822:1410 */       String bs = new String(baseSet);
/*  823:1411 */       String win = new String(raceSets[((int)winnerInfo[0])]);
/*  824:1412 */       if (bs.compareTo(win) != 0) {
/*  825:1415 */         if ((winnerInfo[1] < baseSetError) || (this.m_rankingRequested))
/*  826:     */         {
/*  827:1416 */           improved = true;
/*  828:1417 */           baseSetError = winnerInfo[1];
/*  829:1418 */           this.m_bestMerit = baseSetError;
/*  830:1420 */           if (this.m_rankingRequested) {
/*  831:1421 */             for (int i = 0; i < baseSet.length; i++) {
/*  832:1422 */               if (win.charAt(i) != bs.charAt(i))
/*  833:     */               {
/*  834:1423 */                 this.m_rankedAtts[this.m_rankedSoFar][0] = i;
/*  835:1424 */                 this.m_rankedAtts[this.m_rankedSoFar][1] = winnerInfo[1];
/*  836:1425 */                 this.m_rankedSoFar += 1;
/*  837:     */               }
/*  838:     */             }
/*  839:     */           }
/*  840:1429 */           baseSet = (char[])raceSets[((int)winnerInfo[0])].clone();
/*  841:     */         }
/*  842:     */       }
/*  843:     */     }
/*  844:1438 */     return attributeList(baseSet);
/*  845:     */   }
/*  846:     */   
/*  847:     */   private int[] attributeList(char[] list)
/*  848:     */   {
/*  849:1445 */     int count = 0;
/*  850:1447 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  851:1448 */       if (list[i] == '1') {
/*  852:1449 */         count++;
/*  853:     */       }
/*  854:     */     }
/*  855:1453 */     int[] rlist = new int[count];
/*  856:1454 */     count = 0;
/*  857:1455 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  858:1456 */       if (list[i] == '1') {
/*  859:1457 */         rlist[(count++)] = i;
/*  860:     */       }
/*  861:     */     }
/*  862:1461 */     return rlist;
/*  863:     */   }
/*  864:     */   
/*  865:     */   private double[] raceSubsets(char[][] raceSets, Instances data, boolean baseSetIncluded, Random random)
/*  866:     */     throws Exception
/*  867:     */   {
/*  868:1479 */     ASEvaluation[] evaluators = ASEvaluation.makeCopies(this.m_theEvaluator, raceSets.length);
/*  869:     */     
/*  870:     */ 
/*  871:     */ 
/*  872:1483 */     boolean[] eliminated = new boolean[raceSets.length];
/*  873:     */     
/*  874:     */ 
/*  875:1486 */     Stats[] individualStats = new Stats[raceSets.length];
/*  876:     */     
/*  877:     */ 
/*  878:1489 */     PairedStats[][] testers = new PairedStats[raceSets.length][raceSets.length];
/*  879:     */     
/*  880:     */ 
/*  881:1492 */     int startPt = this.m_rankingRequested ? 1 : 0;
/*  882:1494 */     for (int i = 0; i < raceSets.length; i++)
/*  883:     */     {
/*  884:1495 */       individualStats[i] = new Stats();
/*  885:1496 */       for (int j = i + 1; j < raceSets.length; j++) {
/*  886:1497 */         testers[i][j] = new PairedStats(this.m_sigLevel);
/*  887:     */       }
/*  888:     */     }
/*  889:1501 */     BitSet[] raceBitSets = new BitSet[raceSets.length];
/*  890:1502 */     for (int i = 0; i < raceSets.length; i++)
/*  891:     */     {
/*  892:1503 */       raceBitSets[i] = new BitSet(this.m_numAttribs);
/*  893:1504 */       for (int j = 0; j < this.m_numAttribs; j++) {
/*  894:1505 */         if (raceSets[i][j] == '1') {
/*  895:1506 */           raceBitSets[i].set(j);
/*  896:     */         }
/*  897:     */       }
/*  898:     */     }
/*  899:1516 */     double[] errors = new double[raceSets.length];
/*  900:1517 */     int eliminatedCount = 0;
/*  901:1518 */     int processedCount = 0;
/*  902:     */     
/*  903:     */ 
/*  904:     */ 
/*  905:1522 */     processedCount = 0;
/*  906:1523 */     for (int i = 0; i < this.m_numFolds; i++)
/*  907:     */     {
/*  908:1527 */       Instances trainCV = data.trainCV(this.m_numFolds, i, new Random(1L));
/*  909:1528 */       Instances testCV = data.testCV(this.m_numFolds, i);
/*  910:1532 */       for (int j = startPt; j < raceSets.length; j++) {
/*  911:1533 */         if (eliminated[j] == 0) {
/*  912:1534 */           evaluators[j].buildEvaluator(trainCV);
/*  913:     */         }
/*  914:     */       }
/*  915:1538 */       for (int z = 0; z < testCV.numInstances(); z++)
/*  916:     */       {
/*  917:1539 */         Instance testInst = testCV.instance(z);
/*  918:1540 */         processedCount++;
/*  919:1544 */         for (int zz = startPt; zz < raceSets.length; zz++) {
/*  920:1545 */           if (eliminated[zz] == 0) {
/*  921:1546 */             if (z == 0) {
/*  922:1547 */               errors[zz] = (-((HoldOutSubsetEvaluator)evaluators[zz]).evaluateSubset(raceBitSets[zz], testInst, true));
/*  923:     */             } else {
/*  924:1550 */               errors[zz] = (-((HoldOutSubsetEvaluator)evaluators[zz]).evaluateSubset(raceBitSets[zz], testInst, false));
/*  925:     */             }
/*  926:     */           }
/*  927:     */         }
/*  928:1557 */         for (int j = startPt; j < raceSets.length; j++) {
/*  929:1558 */           if (eliminated[j] == 0)
/*  930:     */           {
/*  931:1559 */             individualStats[j].add(errors[j]);
/*  932:1560 */             for (int k = j + 1; k < raceSets.length; k++) {
/*  933:1561 */               if (eliminated[k] == 0) {
/*  934:1562 */                 testers[j][k].add(errors[j], errors[k]);
/*  935:     */               }
/*  936:     */             }
/*  937:     */           }
/*  938:     */         }
/*  939:1570 */         if ((processedCount > 19) && (eliminatedCount < raceSets.length - 1)) {
/*  940:1572 */           for (int j = 0; j < raceSets.length; j++) {
/*  941:1573 */             if (eliminated[j] == 0) {
/*  942:1574 */               for (int k = j + 1; k < raceSets.length; k++) {
/*  943:1575 */                 if (eliminated[k] == 0)
/*  944:     */                 {
/*  945:1576 */                   testers[j][k].calculateDerived();
/*  946:1578 */                   if ((testers[j][k].differencesSignificance == 0) && ((Utils.eq(testers[j][k].differencesStats.mean, 0.0D)) || (Utils.gr(this.m_delta, Math.abs(testers[j][k].differencesStats.mean)))))
/*  947:     */                   {
/*  948:1585 */                     if (Utils.eq(testers[j][k].differencesStats.mean, 0.0D))
/*  949:     */                     {
/*  950:1587 */                       if (baseSetIncluded)
/*  951:     */                       {
/*  952:1588 */                         if (j != 0) {
/*  953:1589 */                           eliminated[j] = true;
/*  954:     */                         } else {
/*  955:1591 */                           eliminated[k] = true;
/*  956:     */                         }
/*  957:1593 */                         eliminatedCount++;
/*  958:     */                       }
/*  959:     */                       else
/*  960:     */                       {
/*  961:1595 */                         eliminated[j] = true;
/*  962:     */                       }
/*  963:1597 */                       if (this.m_debug) {
/*  964:1598 */                         System.err.println("Eliminating (identical) " + j + " " + raceBitSets[j].toString() + " vs " + k + " " + raceBitSets[k].toString() + " after " + processedCount + " evaluations\n" + "\nerror " + j + " : " + testers[j][k].xStats.mean + " vs " + k + " : " + testers[j][k].yStats.mean + " diff : " + testers[j][k].differencesStats.mean);
/*  965:     */                       }
/*  966:     */                     }
/*  967:     */                     else
/*  968:     */                     {
/*  969:1608 */                       if (testers[j][k].xStats.mean > testers[j][k].yStats.mean)
/*  970:     */                       {
/*  971:1609 */                         eliminated[j] = true;
/*  972:1610 */                         eliminatedCount++;
/*  973:1611 */                         if (!this.m_debug) {
/*  974:     */                           break;
/*  975:     */                         }
/*  976:1612 */                         System.err.println("Eliminating (near identical) " + j + " " + raceBitSets[j].toString() + " vs " + k + " " + raceBitSets[k].toString() + " after " + processedCount + " evaluations\n" + "\nerror " + j + " : " + testers[j][k].xStats.mean + " vs " + k + " : " + testers[j][k].yStats.mean + " diff : " + testers[j][k].differencesStats.mean); break;
/*  977:     */                       }
/*  978:1622 */                       eliminated[k] = true;
/*  979:1623 */                       eliminatedCount++;
/*  980:1624 */                       if (this.m_debug) {
/*  981:1625 */                         System.err.println("Eliminating (near identical) " + k + " " + raceBitSets[k].toString() + " vs " + j + " " + raceBitSets[j].toString() + " after " + processedCount + " evaluations\n" + "\nerror " + k + " : " + testers[j][k].yStats.mean + " vs " + j + " : " + testers[j][k].xStats.mean + " diff : " + testers[j][k].differencesStats.mean);
/*  982:     */                       }
/*  983:     */                     }
/*  984:     */                   }
/*  985:1637 */                   else if (testers[j][k].differencesSignificance != 0)
/*  986:     */                   {
/*  987:1638 */                     if (testers[j][k].differencesSignificance > 0)
/*  988:     */                     {
/*  989:1639 */                       eliminated[j] = true;
/*  990:1640 */                       eliminatedCount++;
/*  991:1641 */                       if (!this.m_debug) {
/*  992:     */                         break;
/*  993:     */                       }
/*  994:1642 */                       System.err.println("Eliminating (-worse) " + j + " " + raceBitSets[j].toString() + " vs " + k + " " + raceBitSets[k].toString() + " after " + processedCount + " evaluations" + "\nerror " + j + " : " + testers[j][k].xStats.mean + " vs " + k + " : " + testers[j][k].yStats.mean); break;
/*  995:     */                     }
/*  996:1651 */                     eliminated[k] = true;
/*  997:1652 */                     eliminatedCount++;
/*  998:1653 */                     if (this.m_debug) {
/*  999:1654 */                       System.err.println("Eliminating (worse) " + k + " " + raceBitSets[k].toString() + " vs " + j + " " + raceBitSets[j].toString() + " after " + processedCount + " evaluations" + "\nerror " + k + " : " + testers[j][k].yStats.mean + " vs " + j + " : " + testers[j][k].xStats.mean);
/* 1000:     */                     }
/* 1001:     */                   }
/* 1002:     */                 }
/* 1003:     */               }
/* 1004:     */             }
/* 1005:     */           }
/* 1006:     */         }
/* 1007:1671 */         if ((eliminatedCount == raceSets.length - 1) && (baseSetIncluded) && (eliminated[0] == 0) && (!this.m_rankingRequested)) {
/* 1008:     */           break label1707;
/* 1009:     */         }
/* 1010:     */       }
/* 1011:     */     }
/* 1012:     */     label1707:
/* 1013:1678 */     if (this.m_debug) {
/* 1014:1679 */       System.err.println("*****eliminated count: " + eliminatedCount);
/* 1015:     */     }
/* 1016:1681 */     double bestError = 1.7976931348623157E+308D;
/* 1017:1682 */     int bestIndex = 0;
/* 1018:1684 */     for (int i = startPt; i < raceSets.length; i++) {
/* 1019:1685 */       if (eliminated[i] == 0)
/* 1020:     */       {
/* 1021:1686 */         individualStats[i].calculateDerived();
/* 1022:1687 */         if (this.m_debug) {
/* 1023:1688 */           System.err.println("Remaining error: " + raceBitSets[i].toString() + " " + individualStats[i].mean);
/* 1024:     */         }
/* 1025:1691 */         if (individualStats[i].mean < bestError)
/* 1026:     */         {
/* 1027:1692 */           bestError = individualStats[i].mean;
/* 1028:1693 */           bestIndex = i;
/* 1029:     */         }
/* 1030:     */       }
/* 1031:     */     }
/* 1032:1698 */     double[] retInfo = new double[2];
/* 1033:1699 */     retInfo[0] = bestIndex;
/* 1034:1700 */     retInfo[1] = bestError;
/* 1035:1702 */     if (this.m_debug)
/* 1036:     */     {
/* 1037:1703 */       System.err.print("Best set from race : ");
/* 1038:1705 */       for (int i = 0; i < this.m_numAttribs; i++) {
/* 1039:1706 */         if (raceSets[bestIndex][i] == '1') {
/* 1040:1707 */           System.err.print('1');
/* 1041:     */         } else {
/* 1042:1709 */           System.err.print('0');
/* 1043:     */         }
/* 1044:     */       }
/* 1045:1712 */       System.err.println(" :" + bestError + " Processed : " + processedCount + "\n" + individualStats[bestIndex].toString());
/* 1046:     */     }
/* 1047:1715 */     return retInfo;
/* 1048:     */   }
/* 1049:     */   
/* 1050:     */   public String toString()
/* 1051:     */   {
/* 1052:1725 */     StringBuffer text = new StringBuffer();
/* 1053:     */     
/* 1054:1727 */     text.append("\tRaceSearch.\n\tRace type : ");
/* 1055:1728 */     switch (this.m_raceType)
/* 1056:     */     {
/* 1057:     */     case 0: 
/* 1058:1730 */       text.append("forward selection race\n\tBase set : no attributes");
/* 1059:1731 */       break;
/* 1060:     */     case 1: 
/* 1061:1733 */       text.append("backward elimination race\n\tBase set : all attributes");
/* 1062:1734 */       break;
/* 1063:     */     case 2: 
/* 1064:1736 */       text.append("schemata race\n\tBase set : no attributes");
/* 1065:1737 */       break;
/* 1066:     */     case 3: 
/* 1067:1739 */       text.append("rank race\n\tBase set : no attributes\n\t");
/* 1068:1740 */       text.append("Attribute evaluator : " + getAttributeEvaluator().getClass().getName() + " ");
/* 1069:1742 */       if ((this.m_ASEval instanceof OptionHandler))
/* 1070:     */       {
/* 1071:1743 */         String[] evaluatorOptions = new String[0];
/* 1072:1744 */         evaluatorOptions = ((OptionHandler)this.m_ASEval).getOptions();
/* 1073:1745 */         for (String evaluatorOption : evaluatorOptions) {
/* 1074:1746 */           text.append(evaluatorOption + ' ');
/* 1075:     */         }
/* 1076:     */       }
/* 1077:1749 */       text.append("\n");
/* 1078:1750 */       text.append("\tAttribute ranking : \n");
/* 1079:1751 */       int rlength = (int)(Math.log(this.m_Ranking.length) / Math.log(10.0D) + 1.0D);
/* 1080:1752 */       for (int element : this.m_Ranking) {
/* 1081:1753 */         text.append("\t " + Utils.doubleToString(element + 1, rlength, 0) + " " + this.m_Instances.attribute(element).name() + '\n');
/* 1082:     */       }
/* 1083:     */     }
/* 1084:1758 */     text.append("\n\tCross validation mode : ");
/* 1085:1759 */     if (this.m_xvalType == 0) {
/* 1086:1760 */       text.append("10 fold");
/* 1087:     */     } else {
/* 1088:1762 */       text.append("Leave-one-out");
/* 1089:     */     }
/* 1090:1765 */     text.append("\n\tMerit of best subset found : ");
/* 1091:1766 */     int fieldwidth = 3;
/* 1092:1767 */     double precision = this.m_bestMerit - (int)this.m_bestMerit;
/* 1093:1768 */     if (Math.abs(this.m_bestMerit) > 0.0D) {
/* 1094:1769 */       fieldwidth = (int)Math.abs(Math.log(Math.abs(this.m_bestMerit)) / Math.log(10.0D)) + 2;
/* 1095:     */     }
/* 1096:1772 */     if (Math.abs(precision) > 0.0D) {
/* 1097:1773 */       precision = Math.abs(Math.log(Math.abs(precision)) / Math.log(10.0D)) + 3.0D;
/* 1098:     */     } else {
/* 1099:1775 */       precision = 2.0D;
/* 1100:     */     }
/* 1101:1778 */     text.append(Utils.doubleToString(Math.abs(this.m_bestMerit), fieldwidth + (int)precision, (int)precision) + "\n");
/* 1102:     */     
/* 1103:     */ 
/* 1104:1781 */     return text.toString();
/* 1105:     */   }
/* 1106:     */   
/* 1107:     */   protected void resetOptions()
/* 1108:     */   {
/* 1109:1789 */     this.m_sigLevel = 0.001D;
/* 1110:1790 */     this.m_delta = 0.001D;
/* 1111:1791 */     this.m_ASEval = new GainRatioAttributeEval();
/* 1112:1792 */     this.m_Ranking = null;
/* 1113:1793 */     this.m_raceType = 0;
/* 1114:1794 */     this.m_debug = false;
/* 1115:1795 */     this.m_theEvaluator = null;
/* 1116:1796 */     this.m_bestMerit = -1.797693134862316E+308D;
/* 1117:1797 */     this.m_numFolds = 10;
/* 1118:     */   }
/* 1119:     */   
/* 1120:     */   public String getRevision()
/* 1121:     */   {
/* 1122:1807 */     return RevisionUtils.extract("$Revision: 10374 $");
/* 1123:     */   }
/* 1124:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.RaceSearch
 * JD-Core Version:    0.7.0.1
 */