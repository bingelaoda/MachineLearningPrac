/*    1:     */ package weka.attributeSelection;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.BitSet;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.Hashtable;
/*    8:     */ import java.util.Random;
/*    9:     */ import java.util.Vector;
/*   10:     */ import weka.core.Instances;
/*   11:     */ import weka.core.Option;
/*   12:     */ import weka.core.OptionHandler;
/*   13:     */ import weka.core.Range;
/*   14:     */ import weka.core.RevisionHandler;
/*   15:     */ import weka.core.RevisionUtils;
/*   16:     */ import weka.core.TechnicalInformation;
/*   17:     */ import weka.core.TechnicalInformation.Field;
/*   18:     */ import weka.core.TechnicalInformation.Type;
/*   19:     */ import weka.core.TechnicalInformationHandler;
/*   20:     */ import weka.core.Utils;
/*   21:     */ 
/*   22:     */ public class GeneticSearch
/*   23:     */   extends ASSearch
/*   24:     */   implements StartSetHandler, OptionHandler, TechnicalInformationHandler
/*   25:     */ {
/*   26:     */   static final long serialVersionUID = -1618264232838472679L;
/*   27:     */   private int[] m_starting;
/*   28:     */   private Range m_startRange;
/*   29:     */   private boolean m_hasClass;
/*   30:     */   private int m_classIndex;
/*   31:     */   private int m_numAttribs;
/*   32:     */   private GABitSet[] m_population;
/*   33:     */   private int m_popSize;
/*   34:     */   private GABitSet m_best;
/*   35:     */   private int m_bestFeatureCount;
/*   36:     */   private int m_lookupTableSize;
/*   37:     */   private Hashtable<BitSet, GABitSet> m_lookupTable;
/*   38:     */   private Random m_random;
/*   39:     */   private int m_seed;
/*   40:     */   private double m_pCrossover;
/*   41:     */   private double m_pMutation;
/*   42:     */   private double m_sumFitness;
/*   43:     */   private double m_maxFitness;
/*   44:     */   private double m_minFitness;
/*   45:     */   private double m_avgFitness;
/*   46:     */   private int m_maxGenerations;
/*   47:     */   private int m_reportFrequency;
/*   48:     */   private StringBuffer m_generationReports;
/*   49:     */   
/*   50:     */   protected class GABitSet
/*   51:     */     implements Cloneable, Serializable, RevisionHandler
/*   52:     */   {
/*   53:     */     static final long serialVersionUID = -2930607837482622224L;
/*   54:     */     private BitSet m_chromosome;
/*   55: 207 */     private double m_objective = -1.797693134862316E+308D;
/*   56:     */     private double m_fitness;
/*   57:     */     
/*   58:     */     public GABitSet()
/*   59:     */     {
/*   60: 216 */       this.m_chromosome = new BitSet();
/*   61:     */     }
/*   62:     */     
/*   63:     */     public Object clone()
/*   64:     */       throws CloneNotSupportedException
/*   65:     */     {
/*   66: 227 */       GABitSet temp = new GABitSet(GeneticSearch.this);
/*   67:     */       
/*   68: 229 */       temp.setObjective(getObjective());
/*   69: 230 */       temp.setFitness(getFitness());
/*   70: 231 */       temp.setChromosome((BitSet)this.m_chromosome.clone());
/*   71: 232 */       return temp;
/*   72:     */     }
/*   73:     */     
/*   74:     */     public void setObjective(double objective)
/*   75:     */     {
/*   76: 242 */       this.m_objective = objective;
/*   77:     */     }
/*   78:     */     
/*   79:     */     public double getObjective()
/*   80:     */     {
/*   81: 251 */       return this.m_objective;
/*   82:     */     }
/*   83:     */     
/*   84:     */     public void setFitness(double fitness)
/*   85:     */     {
/*   86: 260 */       this.m_fitness = fitness;
/*   87:     */     }
/*   88:     */     
/*   89:     */     public double getFitness()
/*   90:     */     {
/*   91: 269 */       return this.m_fitness;
/*   92:     */     }
/*   93:     */     
/*   94:     */     public BitSet getChromosome()
/*   95:     */     {
/*   96: 278 */       return this.m_chromosome;
/*   97:     */     }
/*   98:     */     
/*   99:     */     public void setChromosome(BitSet c)
/*  100:     */     {
/*  101: 287 */       this.m_chromosome = c;
/*  102:     */     }
/*  103:     */     
/*  104:     */     public void clear(int bit)
/*  105:     */     {
/*  106: 296 */       this.m_chromosome.clear(bit);
/*  107:     */     }
/*  108:     */     
/*  109:     */     public void set(int bit)
/*  110:     */     {
/*  111: 305 */       this.m_chromosome.set(bit);
/*  112:     */     }
/*  113:     */     
/*  114:     */     public boolean get(int bit)
/*  115:     */     {
/*  116: 315 */       return this.m_chromosome.get(bit);
/*  117:     */     }
/*  118:     */     
/*  119:     */     public String getRevision()
/*  120:     */     {
/*  121: 325 */       return RevisionUtils.extract("$Revision: 10325 $");
/*  122:     */     }
/*  123:     */   }
/*  124:     */   
/*  125:     */   public Enumeration<Option> listOptions()
/*  126:     */   {
/*  127: 336 */     Vector<Option> newVector = new Vector(7);
/*  128:     */     
/*  129: 338 */     newVector.addElement(new Option("\tSpecify a starting set of attributes.\n\tEg. 1,3,5-7.If supplied, the starting set becomes\n\tone member of the initial random\n\tpopulation.", "P", 1, "-P <start set>"));
/*  130:     */     
/*  131:     */ 
/*  132:     */ 
/*  133: 342 */     newVector.addElement(new Option("\tSet the size of the population (even number).\n\t(default = 20).", "Z", 1, "-Z <population size>"));
/*  134:     */     
/*  135:     */ 
/*  136: 345 */     newVector.addElement(new Option("\tSet the number of generations.\n\t(default = 20)", "G", 1, "-G <number of generations>"));
/*  137:     */     
/*  138: 347 */     newVector.addElement(new Option("\tSet the probability of crossover.\n\t(default = 0.6)", "C", 1, "-C <probability of crossover>"));
/*  139:     */     
/*  140: 349 */     newVector.addElement(new Option("\tSet the probability of mutation.\n\t(default = 0.033)", "M", 1, "-M <probability of mutation>"));
/*  141:     */     
/*  142:     */ 
/*  143: 352 */     newVector.addElement(new Option("\tSet frequency of generation reports.\n\te.g, setting the value to 5 will \n\treport every 5th generation\n\t(default = number of generations)", "R", 1, "-R <report frequency>"));
/*  144:     */     
/*  145:     */ 
/*  146:     */ 
/*  147:     */ 
/*  148: 357 */     newVector.addElement(new Option("\tSet the random number seed.\n\t(default = 1)", "S", 1, "-S <seed>"));
/*  149:     */     
/*  150: 359 */     return newVector.elements();
/*  151:     */   }
/*  152:     */   
/*  153:     */   public void setOptions(String[] options)
/*  154:     */     throws Exception
/*  155:     */   {
/*  156: 424 */     resetOptions();
/*  157:     */     
/*  158: 426 */     String optionString = Utils.getOption('P', options);
/*  159: 427 */     if (optionString.length() != 0) {
/*  160: 428 */       setStartSet(optionString);
/*  161:     */     }
/*  162: 431 */     optionString = Utils.getOption('Z', options);
/*  163: 432 */     if (optionString.length() != 0) {
/*  164: 433 */       setPopulationSize(Integer.parseInt(optionString));
/*  165:     */     }
/*  166: 436 */     optionString = Utils.getOption('G', options);
/*  167: 437 */     if (optionString.length() != 0)
/*  168:     */     {
/*  169: 438 */       setMaxGenerations(Integer.parseInt(optionString));
/*  170: 439 */       setReportFrequency(Integer.parseInt(optionString));
/*  171:     */     }
/*  172: 442 */     optionString = Utils.getOption('C', options);
/*  173: 443 */     if (optionString.length() != 0) {
/*  174: 444 */       setCrossoverProb(new Double(optionString).doubleValue());
/*  175:     */     }
/*  176: 447 */     optionString = Utils.getOption('M', options);
/*  177: 448 */     if (optionString.length() != 0) {
/*  178: 449 */       setMutationProb(new Double(optionString).doubleValue());
/*  179:     */     }
/*  180: 452 */     optionString = Utils.getOption('R', options);
/*  181: 453 */     if (optionString.length() != 0) {
/*  182: 454 */       setReportFrequency(Integer.parseInt(optionString));
/*  183:     */     }
/*  184: 457 */     optionString = Utils.getOption('S', options);
/*  185: 458 */     if (optionString.length() != 0) {
/*  186: 459 */       setSeed(Integer.parseInt(optionString));
/*  187:     */     }
/*  188: 462 */     Utils.checkForRemainingOptions(options);
/*  189:     */   }
/*  190:     */   
/*  191:     */   public String[] getOptions()
/*  192:     */   {
/*  193: 473 */     Vector<String> options = new Vector();
/*  194: 475 */     if (!getStartSet().equals(""))
/*  195:     */     {
/*  196: 476 */       options.add("-P");
/*  197: 477 */       options.add("" + startSetToString());
/*  198:     */     }
/*  199: 479 */     options.add("-Z");
/*  200: 480 */     options.add("" + getPopulationSize());
/*  201: 481 */     options.add("-G");
/*  202: 482 */     options.add("" + getMaxGenerations());
/*  203: 483 */     options.add("-C");
/*  204: 484 */     options.add("" + getCrossoverProb());
/*  205: 485 */     options.add("-M");
/*  206: 486 */     options.add("" + getMutationProb());
/*  207: 487 */     options.add("-R");
/*  208: 488 */     options.add("" + getReportFrequency());
/*  209: 489 */     options.add("-S");
/*  210: 490 */     options.add("" + getSeed());
/*  211:     */     
/*  212: 492 */     return (String[])options.toArray(new String[0]);
/*  213:     */   }
/*  214:     */   
/*  215:     */   public String startSetTipText()
/*  216:     */   {
/*  217: 502 */     return "Set a start point for the search. This is specified as a comma seperated list off attribute indexes starting at 1. It can include ranges. Eg. 1,2,5-9,17. The start set becomes one of the population members of the initial population.";
/*  218:     */   }
/*  219:     */   
/*  220:     */   public void setStartSet(String startSet)
/*  221:     */     throws Exception
/*  222:     */   {
/*  223: 518 */     this.m_startRange.setRanges(startSet);
/*  224:     */   }
/*  225:     */   
/*  226:     */   public String getStartSet()
/*  227:     */   {
/*  228: 528 */     return this.m_startRange.getRanges();
/*  229:     */   }
/*  230:     */   
/*  231:     */   public String seedTipText()
/*  232:     */   {
/*  233: 538 */     return "Set the random seed.";
/*  234:     */   }
/*  235:     */   
/*  236:     */   public void setSeed(int s)
/*  237:     */   {
/*  238: 547 */     this.m_seed = s;
/*  239:     */   }
/*  240:     */   
/*  241:     */   public int getSeed()
/*  242:     */   {
/*  243: 556 */     return this.m_seed;
/*  244:     */   }
/*  245:     */   
/*  246:     */   public String reportFrequencyTipText()
/*  247:     */   {
/*  248: 566 */     return "Set how frequently reports are generated. Default is equal to the number of generations meaning that a report will be printed for initial and final generations. Setting the value to 5 will result in a report being printed every 5 generations.";
/*  249:     */   }
/*  250:     */   
/*  251:     */   public void setReportFrequency(int f)
/*  252:     */   {
/*  253: 578 */     this.m_reportFrequency = f;
/*  254:     */   }
/*  255:     */   
/*  256:     */   public int getReportFrequency()
/*  257:     */   {
/*  258: 587 */     return this.m_reportFrequency;
/*  259:     */   }
/*  260:     */   
/*  261:     */   public String mutationProbTipText()
/*  262:     */   {
/*  263: 597 */     return "Set the probability of mutation occuring.";
/*  264:     */   }
/*  265:     */   
/*  266:     */   public void setMutationProb(double m)
/*  267:     */   {
/*  268: 606 */     this.m_pMutation = m;
/*  269:     */   }
/*  270:     */   
/*  271:     */   public double getMutationProb()
/*  272:     */   {
/*  273: 615 */     return this.m_pMutation;
/*  274:     */   }
/*  275:     */   
/*  276:     */   public String crossoverProbTipText()
/*  277:     */   {
/*  278: 625 */     return "Set the probability of crossover. This is the probability that two population members will exchange genetic material.";
/*  279:     */   }
/*  280:     */   
/*  281:     */   public void setCrossoverProb(double c)
/*  282:     */   {
/*  283: 636 */     this.m_pCrossover = c;
/*  284:     */   }
/*  285:     */   
/*  286:     */   public double getCrossoverProb()
/*  287:     */   {
/*  288: 645 */     return this.m_pCrossover;
/*  289:     */   }
/*  290:     */   
/*  291:     */   public String maxGenerationsTipText()
/*  292:     */   {
/*  293: 655 */     return "Set the number of generations to evaluate.";
/*  294:     */   }
/*  295:     */   
/*  296:     */   public void setMaxGenerations(int m)
/*  297:     */   {
/*  298: 664 */     this.m_maxGenerations = m;
/*  299:     */   }
/*  300:     */   
/*  301:     */   public int getMaxGenerations()
/*  302:     */   {
/*  303: 673 */     return this.m_maxGenerations;
/*  304:     */   }
/*  305:     */   
/*  306:     */   public String populationSizeTipText()
/*  307:     */   {
/*  308: 683 */     return "Set the population size (even number), this is the number of individuals (attribute sets) in the population.";
/*  309:     */   }
/*  310:     */   
/*  311:     */   public void setPopulationSize(int p)
/*  312:     */   {
/*  313: 693 */     if (p % 2 == 0) {
/*  314: 694 */       this.m_popSize = p;
/*  315:     */     } else {
/*  316: 696 */       System.err.println("Population size needs to be an even number!");
/*  317:     */     }
/*  318:     */   }
/*  319:     */   
/*  320:     */   public int getPopulationSize()
/*  321:     */   {
/*  322: 706 */     return this.m_popSize;
/*  323:     */   }
/*  324:     */   
/*  325:     */   public String globalInfo()
/*  326:     */   {
/*  327: 716 */     return "GeneticSearch:\n\nPerforms a search using the simple genetic algorithm described in Goldberg (1989).\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  328:     */   }
/*  329:     */   
/*  330:     */   public TechnicalInformation getTechnicalInformation()
/*  331:     */   {
/*  332: 732 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.BOOK);
/*  333: 733 */     result.setValue(TechnicalInformation.Field.AUTHOR, "David E. Goldberg");
/*  334: 734 */     result.setValue(TechnicalInformation.Field.YEAR, "1989");
/*  335: 735 */     result.setValue(TechnicalInformation.Field.TITLE, "Genetic algorithms in search, optimization and machine learning");
/*  336:     */     
/*  337: 737 */     result.setValue(TechnicalInformation.Field.ISBN, "0201157675");
/*  338: 738 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Addison-Wesley");
/*  339:     */     
/*  340: 740 */     return result;
/*  341:     */   }
/*  342:     */   
/*  343:     */   public GeneticSearch()
/*  344:     */   {
/*  345: 747 */     resetOptions();
/*  346:     */   }
/*  347:     */   
/*  348:     */   private String startSetToString()
/*  349:     */   {
/*  350: 760 */     StringBuffer FString = new StringBuffer();
/*  351: 763 */     if (this.m_starting == null) {
/*  352: 764 */       return getStartSet();
/*  353:     */     }
/*  354: 767 */     for (int i = 0; i < this.m_starting.length; i++)
/*  355:     */     {
/*  356: 768 */       boolean didPrint = false;
/*  357: 770 */       if ((!this.m_hasClass) || ((this.m_hasClass == true) && (i != this.m_classIndex)))
/*  358:     */       {
/*  359: 771 */         FString.append(this.m_starting[i] + 1);
/*  360: 772 */         didPrint = true;
/*  361:     */       }
/*  362: 775 */       if (i == this.m_starting.length - 1) {
/*  363: 776 */         FString.append("");
/*  364: 778 */       } else if (didPrint) {
/*  365: 779 */         FString.append(",");
/*  366:     */       }
/*  367:     */     }
/*  368: 784 */     return FString.toString();
/*  369:     */   }
/*  370:     */   
/*  371:     */   public String toString()
/*  372:     */   {
/*  373: 794 */     StringBuffer GAString = new StringBuffer();
/*  374: 795 */     GAString.append("\tGenetic search.\n\tStart set: ");
/*  375: 797 */     if (this.m_starting == null) {
/*  376: 798 */       GAString.append("no attributes\n");
/*  377:     */     } else {
/*  378: 800 */       GAString.append(startSetToString() + "\n");
/*  379:     */     }
/*  380: 802 */     GAString.append("\tPopulation size: " + this.m_popSize);
/*  381: 803 */     GAString.append("\n\tNumber of generations: " + this.m_maxGenerations);
/*  382: 804 */     GAString.append("\n\tProbability of crossover: " + Utils.doubleToString(this.m_pCrossover, 6, 3));
/*  383:     */     
/*  384: 806 */     GAString.append("\n\tProbability of mutation: " + Utils.doubleToString(this.m_pMutation, 6, 3));
/*  385:     */     
/*  386: 808 */     GAString.append("\n\tReport frequency: " + this.m_reportFrequency);
/*  387: 809 */     GAString.append("\n\tRandom number seed: " + this.m_seed + "\n");
/*  388: 810 */     GAString.append(this.m_generationReports.toString());
/*  389: 811 */     return GAString.toString();
/*  390:     */   }
/*  391:     */   
/*  392:     */   public int[] search(ASEvaluation ASEval, Instances data)
/*  393:     */     throws Exception
/*  394:     */   {
/*  395: 825 */     this.m_best = null;
/*  396: 826 */     this.m_generationReports = new StringBuffer();
/*  397: 828 */     if (!(ASEval instanceof SubsetEvaluator)) {
/*  398: 829 */       throw new Exception(ASEval.getClass().getName() + " is not a " + "Subset evaluator!");
/*  399:     */     }
/*  400: 833 */     if ((ASEval instanceof UnsupervisedSubsetEvaluator))
/*  401:     */     {
/*  402: 834 */       this.m_hasClass = false;
/*  403:     */     }
/*  404:     */     else
/*  405:     */     {
/*  406: 836 */       this.m_hasClass = true;
/*  407: 837 */       this.m_classIndex = data.classIndex();
/*  408:     */     }
/*  409: 840 */     SubsetEvaluator ASEvaluator = (SubsetEvaluator)ASEval;
/*  410: 841 */     this.m_numAttribs = data.numAttributes();
/*  411:     */     
/*  412: 843 */     this.m_startRange.setUpper(this.m_numAttribs - 1);
/*  413: 844 */     if (!getStartSet().equals("")) {
/*  414: 845 */       this.m_starting = this.m_startRange.getSelection();
/*  415:     */     }
/*  416: 849 */     this.m_lookupTable = new Hashtable(this.m_lookupTableSize);
/*  417: 850 */     this.m_random = new Random(this.m_seed);
/*  418: 851 */     this.m_population = new GABitSet[this.m_popSize];
/*  419:     */     
/*  420:     */ 
/*  421: 854 */     initPopulation();
/*  422: 855 */     evaluatePopulation(ASEvaluator);
/*  423: 856 */     populationStatistics();
/*  424: 857 */     scalePopulation();
/*  425: 858 */     checkBest();
/*  426: 859 */     this.m_generationReports.append(populationReport(0));
/*  427: 862 */     for (int i = 1; i <= this.m_maxGenerations; i++)
/*  428:     */     {
/*  429: 863 */       generation();
/*  430: 864 */       evaluatePopulation(ASEvaluator);
/*  431: 865 */       populationStatistics();
/*  432: 866 */       scalePopulation();
/*  433:     */       
/*  434: 868 */       boolean converged = checkBest();
/*  435: 870 */       if ((i == this.m_maxGenerations) || (i % this.m_reportFrequency == 0) || (converged == true))
/*  436:     */       {
/*  437: 872 */         this.m_generationReports.append(populationReport(i));
/*  438: 873 */         if (converged == true) {
/*  439:     */           break;
/*  440:     */         }
/*  441:     */       }
/*  442:     */     }
/*  443: 878 */     return attributeList(this.m_best.getChromosome());
/*  444:     */   }
/*  445:     */   
/*  446:     */   private int[] attributeList(BitSet group)
/*  447:     */   {
/*  448: 888 */     int count = 0;
/*  449: 891 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  450: 892 */       if (group.get(i)) {
/*  451: 893 */         count++;
/*  452:     */       }
/*  453:     */     }
/*  454: 897 */     int[] list = new int[count];
/*  455: 898 */     count = 0;
/*  456: 900 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  457: 901 */       if (group.get(i)) {
/*  458: 902 */         list[(count++)] = i;
/*  459:     */       }
/*  460:     */     }
/*  461: 906 */     return list;
/*  462:     */   }
/*  463:     */   
/*  464:     */   private boolean checkBest()
/*  465:     */     throws Exception
/*  466:     */   {
/*  467: 919 */     int lowestCount = this.m_numAttribs;
/*  468: 920 */     double b = -1.797693134862316E+308D;
/*  469: 921 */     GABitSet localbest = null;
/*  470:     */     
/*  471: 923 */     boolean converged = false;
/*  472: 924 */     int oldcount = 2147483647;
/*  473: 926 */     if (this.m_maxFitness - this.m_minFitness > 0.0D) {
/*  474: 928 */       for (int i = 0; i < this.m_popSize; i++) {
/*  475: 929 */         if (this.m_population[i].getObjective() > b)
/*  476:     */         {
/*  477: 930 */           b = this.m_population[i].getObjective();
/*  478: 931 */           localbest = this.m_population[i];
/*  479: 932 */           oldcount = countFeatures(localbest.getChromosome());
/*  480:     */         }
/*  481: 933 */         else if (Utils.eq(this.m_population[i].getObjective(), b))
/*  482:     */         {
/*  483: 935 */           int count = countFeatures(this.m_population[i].getChromosome());
/*  484: 936 */           if (count < oldcount)
/*  485:     */           {
/*  486: 937 */             b = this.m_population[i].getObjective();
/*  487: 938 */             localbest = this.m_population[i];
/*  488: 939 */             oldcount = count;
/*  489:     */           }
/*  490:     */         }
/*  491:     */       }
/*  492:     */     }
/*  493: 945 */     for (int i = 0; i < this.m_popSize; i++)
/*  494:     */     {
/*  495: 946 */       BitSet temp = this.m_population[i].getChromosome();
/*  496: 947 */       int count = countFeatures(temp);
/*  497: 950 */       if (count < lowestCount)
/*  498:     */       {
/*  499: 951 */         lowestCount = count;
/*  500: 952 */         localbest = this.m_population[i];
/*  501: 953 */         b = localbest.getObjective();
/*  502:     */       }
/*  503:     */     }
/*  504: 956 */     converged = true;
/*  505:     */     
/*  506:     */ 
/*  507:     */ 
/*  508: 960 */     int count = 0;
/*  509: 961 */     BitSet temp = localbest.getChromosome();
/*  510: 962 */     count = countFeatures(temp);
/*  511: 965 */     if (this.m_best == null)
/*  512:     */     {
/*  513: 966 */       this.m_best = ((GABitSet)localbest.clone());
/*  514: 967 */       this.m_bestFeatureCount = count;
/*  515:     */     }
/*  516: 968 */     else if (b > this.m_best.getObjective())
/*  517:     */     {
/*  518: 969 */       this.m_best = ((GABitSet)localbest.clone());
/*  519: 970 */       this.m_bestFeatureCount = count;
/*  520:     */     }
/*  521: 971 */     else if (Utils.eq(this.m_best.getObjective(), b))
/*  522:     */     {
/*  523: 973 */       if (count < this.m_bestFeatureCount)
/*  524:     */       {
/*  525: 974 */         this.m_best = ((GABitSet)localbest.clone());
/*  526: 975 */         this.m_bestFeatureCount = count;
/*  527:     */       }
/*  528:     */     }
/*  529: 978 */     return converged;
/*  530:     */   }
/*  531:     */   
/*  532:     */   private int countFeatures(BitSet featureSet)
/*  533:     */   {
/*  534: 988 */     int count = 0;
/*  535: 989 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  536: 990 */       if (featureSet.get(i)) {
/*  537: 991 */         count++;
/*  538:     */       }
/*  539:     */     }
/*  540: 994 */     return count;
/*  541:     */   }
/*  542:     */   
/*  543:     */   private void generation()
/*  544:     */     throws Exception
/*  545:     */   {
/*  546:1003 */     int j = 0;
/*  547:1004 */     double best_fit = -1.797693134862316E+308D;
/*  548:1005 */     int old_count = 0;
/*  549:     */     
/*  550:1007 */     GABitSet[] newPop = new GABitSet[this.m_popSize];
/*  551:1014 */     for (int i = 0; i < this.m_popSize; i++) {
/*  552:1015 */       if (this.m_population[i].getFitness() > best_fit)
/*  553:     */       {
/*  554:1016 */         j = i;
/*  555:1017 */         best_fit = this.m_population[i].getFitness();
/*  556:1018 */         old_count = countFeatures(this.m_population[i].getChromosome());
/*  557:     */       }
/*  558:1019 */       else if (Utils.eq(this.m_population[i].getFitness(), best_fit))
/*  559:     */       {
/*  560:1020 */         int count = countFeatures(this.m_population[i].getChromosome());
/*  561:1021 */         if (count < old_count)
/*  562:     */         {
/*  563:1022 */           j = i;
/*  564:1023 */           best_fit = this.m_population[i].getFitness();
/*  565:1024 */           old_count = count;
/*  566:     */         }
/*  567:     */       }
/*  568:     */     }
/*  569:1028 */     newPop[0] = ((GABitSet)(GABitSet)this.m_population[j].clone());
/*  570:1029 */     newPop[1] = newPop[0];
/*  571:1031 */     for (j = 2; j < this.m_popSize; j += 2)
/*  572:     */     {
/*  573:1032 */       int parent1 = select();
/*  574:1033 */       int parent2 = select();
/*  575:1034 */       newPop[j] = ((GABitSet)(GABitSet)this.m_population[parent1].clone());
/*  576:1035 */       newPop[(j + 1)] = ((GABitSet)(GABitSet)this.m_population[parent2].clone());
/*  577:1037 */       if (parent1 == parent2)
/*  578:     */       {
/*  579:1039 */         if (this.m_hasClass)
/*  580:     */         {
/*  581:     */           int r;
/*  582:1040 */           while ((r = this.m_random.nextInt(this.m_numAttribs)) == this.m_classIndex) {}
/*  583:     */         }
/*  584:1044 */         int r = this.m_random.nextInt(this.m_numAttribs);
/*  585:1047 */         if (newPop[j].get(r)) {
/*  586:1048 */           newPop[j].clear(r);
/*  587:     */         } else {
/*  588:1050 */           newPop[j].set(r);
/*  589:     */         }
/*  590:     */       }
/*  591:     */       else
/*  592:     */       {
/*  593:1054 */         double r = this.m_random.nextDouble();
/*  594:1055 */         if ((this.m_numAttribs >= 3) && 
/*  595:1056 */           (r < this.m_pCrossover))
/*  596:     */         {
/*  597:1058 */           int cp = Math.abs(this.m_random.nextInt());
/*  598:     */           
/*  599:1060 */           cp %= (this.m_numAttribs - 2);
/*  600:1061 */           cp++;
/*  601:1063 */           for (i = 0; i < cp; i++)
/*  602:     */           {
/*  603:1064 */             if (this.m_population[parent1].get(i)) {
/*  604:1065 */               newPop[(j + 1)].set(i);
/*  605:     */             } else {
/*  606:1067 */               newPop[(j + 1)].clear(i);
/*  607:     */             }
/*  608:1069 */             if (this.m_population[parent2].get(i)) {
/*  609:1070 */               newPop[j].set(i);
/*  610:     */             } else {
/*  611:1072 */               newPop[j].clear(i);
/*  612:     */             }
/*  613:     */           }
/*  614:     */         }
/*  615:1079 */         for (int k = 0; k < 2; k++) {
/*  616:1080 */           for (i = 0; i < this.m_numAttribs; i++)
/*  617:     */           {
/*  618:1081 */             r = this.m_random.nextDouble();
/*  619:1082 */             if ((r < this.m_pMutation) && (
/*  620:1083 */               (!this.m_hasClass) || (i != this.m_classIndex))) {
/*  621:1086 */               if (newPop[(j + k)].get(i)) {
/*  622:1087 */                 newPop[(j + k)].clear(i);
/*  623:     */               } else {
/*  624:1089 */                 newPop[(j + k)].set(i);
/*  625:     */               }
/*  626:     */             }
/*  627:     */           }
/*  628:     */         }
/*  629:     */       }
/*  630:     */     }
/*  631:1099 */     this.m_population = newPop;
/*  632:     */   }
/*  633:     */   
/*  634:     */   private int select()
/*  635:     */   {
/*  636:1111 */     double partsum = 0.0D;
/*  637:1112 */     double r = this.m_random.nextDouble() * this.m_sumFitness;
/*  638:1113 */     for (int i = 0; i < this.m_popSize; i++)
/*  639:     */     {
/*  640:1114 */       partsum += this.m_population[i].getFitness();
/*  641:1115 */       if ((partsum >= r) || (i == this.m_popSize - 1)) {
/*  642:     */         break;
/*  643:     */       }
/*  644:     */     }
/*  645:1121 */     if (i == this.m_popSize) {
/*  646:1122 */       i = 0;
/*  647:     */     }
/*  648:1125 */     return i;
/*  649:     */   }
/*  650:     */   
/*  651:     */   private void evaluatePopulation(SubsetEvaluator ASEvaluator)
/*  652:     */     throws Exception
/*  653:     */   {
/*  654:1140 */     for (int i = 0; i < this.m_popSize; i++) {
/*  655:1142 */       if (!this.m_lookupTable.containsKey(this.m_population[i].getChromosome()))
/*  656:     */       {
/*  657:1143 */         double merit = ASEvaluator.evaluateSubset(this.m_population[i].getChromosome());
/*  658:1144 */         this.m_population[i].setObjective(merit);
/*  659:1145 */         this.m_lookupTable.put(this.m_population[i].getChromosome(), this.m_population[i]);
/*  660:     */       }
/*  661:     */       else
/*  662:     */       {
/*  663:1147 */         GABitSet temp = (GABitSet)this.m_lookupTable.get(this.m_population[i].getChromosome());
/*  664:1148 */         this.m_population[i].setObjective(temp.getObjective());
/*  665:     */       }
/*  666:     */     }
/*  667:     */   }
/*  668:     */   
/*  669:     */   private void initPopulation()
/*  670:     */     throws Exception
/*  671:     */   {
/*  672:1163 */     int start = 0;
/*  673:1166 */     if (this.m_starting != null)
/*  674:     */     {
/*  675:1167 */       this.m_population[0] = new GABitSet();
/*  676:1168 */       for (int i = 0; i < this.m_starting.length; i++) {
/*  677:1169 */         if (this.m_starting[i] != this.m_classIndex) {
/*  678:1170 */           this.m_population[0].set(this.m_starting[i]);
/*  679:     */         }
/*  680:     */       }
/*  681:1173 */       start = 1;
/*  682:     */     }
/*  683:1176 */     for (int i = start; i < this.m_popSize; i++)
/*  684:     */     {
/*  685:1177 */       this.m_population[i] = new GABitSet();
/*  686:     */       
/*  687:1179 */       int num_bits = this.m_random.nextInt();
/*  688:1180 */       num_bits = num_bits % this.m_numAttribs - 1;
/*  689:1181 */       if (num_bits < 0) {
/*  690:1182 */         num_bits *= -1;
/*  691:     */       }
/*  692:1184 */       if (num_bits == 0) {
/*  693:1185 */         num_bits = 1;
/*  694:     */       }
/*  695:1188 */       for (int j = 0; j < num_bits; j++)
/*  696:     */       {
/*  697:1189 */         boolean ok = false;
/*  698:     */         int bit;
/*  699:     */         do
/*  700:     */         {
/*  701:1191 */           bit = this.m_random.nextInt();
/*  702:1192 */           if (bit < 0) {
/*  703:1193 */             bit *= -1;
/*  704:     */           }
/*  705:1195 */           bit %= this.m_numAttribs;
/*  706:1196 */           if (this.m_hasClass)
/*  707:     */           {
/*  708:1197 */             if (bit != this.m_classIndex) {
/*  709:1198 */               ok = true;
/*  710:     */             }
/*  711:     */           }
/*  712:     */           else {
/*  713:1201 */             ok = true;
/*  714:     */           }
/*  715:1203 */         } while (!ok);
/*  716:1205 */         if (bit > this.m_numAttribs) {
/*  717:1206 */           throw new Exception("Problem in population init");
/*  718:     */         }
/*  719:1208 */         this.m_population[i].set(bit);
/*  720:     */       }
/*  721:     */     }
/*  722:     */   }
/*  723:     */   
/*  724:     */   private void populationStatistics()
/*  725:     */   {
/*  726:1219 */     this.m_sumFitness = (this.m_minFitness = this.m_maxFitness = this.m_population[0].getObjective());
/*  727:1221 */     for (int i = 1; i < this.m_popSize; i++)
/*  728:     */     {
/*  729:1222 */       this.m_sumFitness += this.m_population[i].getObjective();
/*  730:1223 */       if (this.m_population[i].getObjective() > this.m_maxFitness) {
/*  731:1224 */         this.m_maxFitness = this.m_population[i].getObjective();
/*  732:1225 */       } else if (this.m_population[i].getObjective() < this.m_minFitness) {
/*  733:1226 */         this.m_minFitness = this.m_population[i].getObjective();
/*  734:     */       }
/*  735:     */     }
/*  736:1229 */     this.m_avgFitness = (this.m_sumFitness / this.m_popSize);
/*  737:     */   }
/*  738:     */   
/*  739:     */   private void scalePopulation()
/*  740:     */   {
/*  741:1237 */     double a = 0.0D;
/*  742:1238 */     double b = 0.0D;
/*  743:1239 */     double fmultiple = 2.0D;
/*  744:1243 */     if (this.m_minFitness > (fmultiple * this.m_avgFitness - this.m_maxFitness) / (fmultiple - 1.0D))
/*  745:     */     {
/*  746:1244 */       double delta = this.m_maxFitness - this.m_avgFitness;
/*  747:1245 */       a = (fmultiple - 1.0D) * this.m_avgFitness / delta;
/*  748:1246 */       b = this.m_avgFitness * (this.m_maxFitness - fmultiple * this.m_avgFitness) / delta;
/*  749:     */     }
/*  750:     */     else
/*  751:     */     {
/*  752:1248 */       double delta = this.m_avgFitness - this.m_minFitness;
/*  753:1249 */       a = this.m_avgFitness / delta;
/*  754:1250 */       b = -this.m_minFitness * this.m_avgFitness / delta;
/*  755:     */     }
/*  756:1254 */     this.m_sumFitness = 0.0D;
/*  757:1255 */     for (int j = 0; j < this.m_popSize; j++)
/*  758:     */     {
/*  759:1256 */       if ((a == (1.0D / 0.0D)) || (a == (-1.0D / 0.0D)) || (b == (1.0D / 0.0D)) || (b == (-1.0D / 0.0D))) {
/*  760:1258 */         this.m_population[j].setFitness(this.m_population[j].getObjective());
/*  761:     */       } else {
/*  762:1260 */         this.m_population[j].setFitness(Math.abs(a * this.m_population[j].getObjective() + b));
/*  763:     */       }
/*  764:1263 */       this.m_sumFitness += this.m_population[j].getFitness();
/*  765:     */     }
/*  766:     */   }
/*  767:     */   
/*  768:     */   private String populationReport(int genNum)
/*  769:     */   {
/*  770:1274 */     StringBuffer temp = new StringBuffer();
/*  771:1276 */     if (genNum == 0) {
/*  772:1277 */       temp.append("\nInitial population\n");
/*  773:     */     } else {
/*  774:1279 */       temp.append("\nGeneration: " + genNum + "\n");
/*  775:     */     }
/*  776:1281 */     temp.append("merit   \tscaled  \tsubset\n");
/*  777:1283 */     for (int i = 0; i < this.m_popSize; i++)
/*  778:     */     {
/*  779:1284 */       temp.append(Utils.doubleToString(Math.abs(this.m_population[i].getObjective()), 8, 5) + "\t" + Utils.doubleToString(this.m_population[i].getFitness(), 8, 5) + "\t");
/*  780:     */       
/*  781:     */ 
/*  782:     */ 
/*  783:     */ 
/*  784:1289 */       temp.append(printPopMember(this.m_population[i].getChromosome()) + "\n");
/*  785:     */     }
/*  786:1291 */     return temp.toString();
/*  787:     */   }
/*  788:     */   
/*  789:     */   private String printPopMember(BitSet temp)
/*  790:     */   {
/*  791:1301 */     StringBuffer text = new StringBuffer();
/*  792:1303 */     for (int j = 0; j < this.m_numAttribs; j++) {
/*  793:1304 */       if (temp.get(j)) {
/*  794:1305 */         text.append(j + 1 + " ");
/*  795:     */       }
/*  796:     */     }
/*  797:1308 */     return text.toString();
/*  798:     */   }
/*  799:     */   
/*  800:     */   private void resetOptions()
/*  801:     */   {
/*  802:1315 */     this.m_population = null;
/*  803:1316 */     this.m_popSize = 20;
/*  804:1317 */     this.m_lookupTableSize = 1001;
/*  805:1318 */     this.m_pCrossover = 0.6D;
/*  806:1319 */     this.m_pMutation = 0.033D;
/*  807:1320 */     this.m_maxGenerations = 20;
/*  808:1321 */     this.m_reportFrequency = this.m_maxGenerations;
/*  809:1322 */     this.m_starting = null;
/*  810:1323 */     this.m_startRange = new Range();
/*  811:1324 */     this.m_seed = 1;
/*  812:     */   }
/*  813:     */   
/*  814:     */   public String getRevision()
/*  815:     */   {
/*  816:1334 */     return RevisionUtils.extract("$Revision: 10325 $");
/*  817:     */   }
/*  818:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.GeneticSearch
 * JD-Core Version:    0.7.0.1
 */