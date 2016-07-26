/*    1:     */ package weka.attributeSelection;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.BitSet;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.List;
/*    8:     */ import java.util.Vector;
/*    9:     */ import java.util.concurrent.Callable;
/*   10:     */ import java.util.concurrent.ExecutorService;
/*   11:     */ import java.util.concurrent.Executors;
/*   12:     */ import java.util.concurrent.Future;
/*   13:     */ import weka.core.Instances;
/*   14:     */ import weka.core.Option;
/*   15:     */ import weka.core.OptionHandler;
/*   16:     */ import weka.core.Range;
/*   17:     */ import weka.core.RevisionUtils;
/*   18:     */ import weka.core.ThreadSafe;
/*   19:     */ import weka.core.Utils;
/*   20:     */ 
/*   21:     */ public class GreedyStepwise
/*   22:     */   extends ASSearch
/*   23:     */   implements RankedOutputSearch, StartSetHandler, OptionHandler
/*   24:     */ {
/*   25:     */   static final long serialVersionUID = -6312951970168325471L;
/*   26:     */   protected boolean m_hasClass;
/*   27:     */   protected int m_classIndex;
/*   28:     */   protected int m_numAttribs;
/*   29:     */   protected boolean m_rankingRequested;
/*   30:     */   protected boolean m_doRank;
/*   31:     */   protected boolean m_doneRanking;
/*   32:     */   protected double m_threshold;
/*   33: 142 */   protected int m_numToSelect = -1;
/*   34:     */   protected int m_calculatedNumToSelect;
/*   35:     */   protected double m_bestMerit;
/*   36:     */   protected double[][] m_rankedAtts;
/*   37:     */   protected int m_rankedSoFar;
/*   38:     */   protected BitSet m_best_group;
/*   39:     */   protected ASEvaluation m_ASEval;
/*   40:     */   protected Instances m_Instances;
/*   41:     */   protected Range m_startRange;
/*   42:     */   protected int[] m_starting;
/*   43: 166 */   protected boolean m_backward = false;
/*   44: 172 */   protected boolean m_conservativeSelection = false;
/*   45: 175 */   protected boolean m_debug = false;
/*   46: 177 */   protected int m_poolSize = 1;
/*   47: 180 */   protected transient ExecutorService m_pool = null;
/*   48:     */   
/*   49:     */   public GreedyStepwise()
/*   50:     */   {
/*   51: 186 */     this.m_threshold = -1.797693134862316E+308D;
/*   52: 187 */     this.m_doneRanking = false;
/*   53: 188 */     this.m_startRange = new Range();
/*   54: 189 */     this.m_starting = null;
/*   55: 190 */     resetOptions();
/*   56:     */   }
/*   57:     */   
/*   58:     */   public String globalInfo()
/*   59:     */   {
/*   60: 200 */     return "GreedyStepwise :\n\nPerforms a greedy forward or backward search through the space of attribute subsets. May start with no/all attributes or from an arbitrary point in the space. Stops when the addition/deletion of any remaining attributes results in a decrease in evaluation. Can also produce a ranked list of attributes by traversing the space from one side to the other and recording the order that attributes are selected.\n";
/*   61:     */   }
/*   62:     */   
/*   63:     */   public String searchBackwardsTipText()
/*   64:     */   {
/*   65: 217 */     return "Search backwards rather than forwards.";
/*   66:     */   }
/*   67:     */   
/*   68:     */   public void setSearchBackwards(boolean back)
/*   69:     */   {
/*   70: 226 */     this.m_backward = back;
/*   71: 227 */     if (this.m_backward) {
/*   72: 228 */       setGenerateRanking(false);
/*   73:     */     }
/*   74:     */   }
/*   75:     */   
/*   76:     */   public boolean getSearchBackwards()
/*   77:     */   {
/*   78: 238 */     return this.m_backward;
/*   79:     */   }
/*   80:     */   
/*   81:     */   public String thresholdTipText()
/*   82:     */   {
/*   83: 248 */     return "Set threshold by which attributes can be discarded. Default value results in no attributes being discarded. Use in conjunction with generateRanking";
/*   84:     */   }
/*   85:     */   
/*   86:     */   public void setThreshold(double threshold)
/*   87:     */   {
/*   88: 261 */     this.m_threshold = threshold;
/*   89:     */   }
/*   90:     */   
/*   91:     */   public double getThreshold()
/*   92:     */   {
/*   93: 270 */     return this.m_threshold;
/*   94:     */   }
/*   95:     */   
/*   96:     */   public String numToSelectTipText()
/*   97:     */   {
/*   98: 280 */     return "Specify the number of attributes to retain. The default value (-1) indicates that all attributes are to be retained. Use either this option or a threshold to reduce the attribute set.";
/*   99:     */   }
/*  100:     */   
/*  101:     */   public void setNumToSelect(int n)
/*  102:     */   {
/*  103: 293 */     this.m_numToSelect = n;
/*  104:     */   }
/*  105:     */   
/*  106:     */   public int getNumToSelect()
/*  107:     */   {
/*  108: 303 */     return this.m_numToSelect;
/*  109:     */   }
/*  110:     */   
/*  111:     */   public int getCalculatedNumToSelect()
/*  112:     */   {
/*  113: 314 */     if (this.m_numToSelect >= 0) {
/*  114: 315 */       this.m_calculatedNumToSelect = this.m_numToSelect;
/*  115:     */     }
/*  116: 317 */     return this.m_calculatedNumToSelect;
/*  117:     */   }
/*  118:     */   
/*  119:     */   public String generateRankingTipText()
/*  120:     */   {
/*  121: 327 */     return "Set to true if a ranked list is required.";
/*  122:     */   }
/*  123:     */   
/*  124:     */   public void setGenerateRanking(boolean doRank)
/*  125:     */   {
/*  126: 337 */     this.m_rankingRequested = doRank;
/*  127:     */   }
/*  128:     */   
/*  129:     */   public boolean getGenerateRanking()
/*  130:     */   {
/*  131: 349 */     return this.m_rankingRequested;
/*  132:     */   }
/*  133:     */   
/*  134:     */   public String startSetTipText()
/*  135:     */   {
/*  136: 359 */     return "Set the start point for the search. This is specified as a comma seperated list off attribute indexes starting at 1. It can include ranges. Eg. 1,2,5-9,17.";
/*  137:     */   }
/*  138:     */   
/*  139:     */   public void setStartSet(String startSet)
/*  140:     */     throws Exception
/*  141:     */   {
/*  142: 374 */     this.m_startRange.setRanges(startSet);
/*  143:     */   }
/*  144:     */   
/*  145:     */   public String getStartSet()
/*  146:     */   {
/*  147: 384 */     return this.m_startRange.getRanges();
/*  148:     */   }
/*  149:     */   
/*  150:     */   public String conservativeForwardSelectionTipText()
/*  151:     */   {
/*  152: 394 */     return "If true (and forward search is selected) then attributes will continue to be added to the best subset as long as merit does not degrade.";
/*  153:     */   }
/*  154:     */   
/*  155:     */   public void setConservativeForwardSelection(boolean c)
/*  156:     */   {
/*  157: 406 */     this.m_conservativeSelection = c;
/*  158:     */   }
/*  159:     */   
/*  160:     */   public boolean getConservativeForwardSelection()
/*  161:     */   {
/*  162: 415 */     return this.m_conservativeSelection;
/*  163:     */   }
/*  164:     */   
/*  165:     */   public String debuggingOutputTipText()
/*  166:     */   {
/*  167: 425 */     return "Output debugging information to the console";
/*  168:     */   }
/*  169:     */   
/*  170:     */   public void setDebuggingOutput(boolean d)
/*  171:     */   {
/*  172: 434 */     this.m_debug = d;
/*  173:     */   }
/*  174:     */   
/*  175:     */   public boolean getDebuggingOutput()
/*  176:     */   {
/*  177: 443 */     return this.m_debug;
/*  178:     */   }
/*  179:     */   
/*  180:     */   public String numExecutionSlotsTipText()
/*  181:     */   {
/*  182: 451 */     return "The number of execution slots, for example, the number of cores in the CPU.";
/*  183:     */   }
/*  184:     */   
/*  185:     */   public int getNumExecutionSlots()
/*  186:     */   {
/*  187: 459 */     return this.m_poolSize;
/*  188:     */   }
/*  189:     */   
/*  190:     */   public void setNumExecutionSlots(int nT)
/*  191:     */   {
/*  192: 467 */     this.m_poolSize = nT;
/*  193:     */   }
/*  194:     */   
/*  195:     */   public Enumeration<Option> listOptions()
/*  196:     */   {
/*  197: 477 */     Vector<Option> newVector = new Vector(8);
/*  198:     */     
/*  199: 479 */     newVector.addElement(new Option("\tUse conservative forward search", "-C", 0, "-C"));
/*  200:     */     
/*  201:     */ 
/*  202: 482 */     newVector.addElement(new Option("\tUse a backward search instead of a\n\tforward one.", "-B", 0, "-B"));
/*  203:     */     
/*  204: 484 */     newVector.addElement(new Option("\tSpecify a starting set of attributes.\n\tEg. 1,3,5-7.", "P", 1, "-P <start set>"));
/*  205:     */     
/*  206:     */ 
/*  207: 487 */     newVector.addElement(new Option("\tProduce a ranked list of attributes.", "R", 0, "-R"));
/*  208:     */     
/*  209: 489 */     newVector.addElement(new Option("\tSpecify a theshold by which attributes\n\tmay be discarded from the ranking.\n\tUse in conjuction with -R", "T", 1, "-T <threshold>"));
/*  210:     */     
/*  211:     */ 
/*  212:     */ 
/*  213: 493 */     newVector.addElement(new Option("\tSpecify number of attributes to select", "N", 1, "-N <num to select>"));
/*  214:     */     
/*  215:     */ 
/*  216: 496 */     newVector.addElement(new Option("\t" + numExecutionSlotsTipText() + " (default 1)\n", "-num-slots", 1, "-num-slots <int>"));
/*  217:     */     
/*  218:     */ 
/*  219: 499 */     newVector.addElement(new Option("\tPrint debugging output", "D", 0, "-D"));
/*  220:     */     
/*  221: 501 */     return newVector.elements();
/*  222:     */   }
/*  223:     */   
/*  224:     */   public void setOptions(String[] options)
/*  225:     */     throws Exception
/*  226:     */   {
/*  227: 564 */     resetOptions();
/*  228:     */     
/*  229: 566 */     setSearchBackwards(Utils.getFlag('B', options));
/*  230:     */     
/*  231: 568 */     setConservativeForwardSelection(Utils.getFlag('C', options));
/*  232:     */     
/*  233: 570 */     String optionString = Utils.getOption('P', options);
/*  234: 571 */     if (optionString.length() != 0) {
/*  235: 572 */       setStartSet(optionString);
/*  236:     */     }
/*  237: 575 */     setGenerateRanking(Utils.getFlag('R', options));
/*  238:     */     
/*  239: 577 */     optionString = Utils.getOption('T', options);
/*  240: 578 */     if (optionString.length() != 0)
/*  241:     */     {
/*  242: 580 */       Double temp = Double.valueOf(optionString);
/*  243: 581 */       setThreshold(temp.doubleValue());
/*  244:     */     }
/*  245: 584 */     optionString = Utils.getOption('N', options);
/*  246: 585 */     if (optionString.length() != 0) {
/*  247: 586 */       setNumToSelect(Integer.parseInt(optionString));
/*  248:     */     }
/*  249: 589 */     optionString = Utils.getOption("num-slots", options);
/*  250: 590 */     if (optionString.length() > 0) {
/*  251: 591 */       setNumExecutionSlots(Integer.parseInt(optionString));
/*  252:     */     }
/*  253: 594 */     setDebuggingOutput(Utils.getFlag('D', options));
/*  254:     */   }
/*  255:     */   
/*  256:     */   public String[] getOptions()
/*  257:     */   {
/*  258: 605 */     Vector<String> options = new Vector();
/*  259: 607 */     if (getSearchBackwards()) {
/*  260: 608 */       options.add("-B");
/*  261:     */     }
/*  262: 611 */     if (getConservativeForwardSelection()) {
/*  263: 612 */       options.add("-C");
/*  264:     */     }
/*  265: 615 */     if (!getStartSet().equals(""))
/*  266:     */     {
/*  267: 616 */       options.add("-P");
/*  268: 617 */       options.add("" + startSetToString());
/*  269:     */     }
/*  270: 620 */     if (getGenerateRanking()) {
/*  271: 621 */       options.add("-R");
/*  272:     */     }
/*  273: 623 */     options.add("-T");
/*  274: 624 */     options.add("" + getThreshold());
/*  275:     */     
/*  276: 626 */     options.add("-N");
/*  277: 627 */     options.add("" + getNumToSelect());
/*  278:     */     
/*  279: 629 */     options.add("-num-slots");
/*  280: 630 */     options.add("" + getNumExecutionSlots());
/*  281: 632 */     if (getDebuggingOutput()) {
/*  282: 633 */       options.add("-D");
/*  283:     */     }
/*  284: 636 */     return (String[])options.toArray(new String[0]);
/*  285:     */   }
/*  286:     */   
/*  287:     */   protected String startSetToString()
/*  288:     */   {
/*  289: 649 */     StringBuffer FString = new StringBuffer();
/*  290: 652 */     if (this.m_starting == null) {
/*  291: 653 */       return getStartSet();
/*  292:     */     }
/*  293: 655 */     for (int i = 0; i < this.m_starting.length; i++)
/*  294:     */     {
/*  295: 656 */       boolean didPrint = false;
/*  296: 658 */       if ((!this.m_hasClass) || ((this.m_hasClass == true) && (i != this.m_classIndex)))
/*  297:     */       {
/*  298: 659 */         FString.append(this.m_starting[i] + 1);
/*  299: 660 */         didPrint = true;
/*  300:     */       }
/*  301: 663 */       if (i == this.m_starting.length - 1) {
/*  302: 664 */         FString.append("");
/*  303: 666 */       } else if (didPrint) {
/*  304: 667 */         FString.append(",");
/*  305:     */       }
/*  306:     */     }
/*  307: 672 */     return FString.toString();
/*  308:     */   }
/*  309:     */   
/*  310:     */   public String toString()
/*  311:     */   {
/*  312: 682 */     StringBuffer FString = new StringBuffer();
/*  313: 683 */     FString.append("\tGreedy Stepwise (" + (this.m_backward ? "backwards)" : "forwards)") + ".\n\tStart set: ");
/*  314: 686 */     if (this.m_starting == null)
/*  315:     */     {
/*  316: 687 */       if (this.m_backward) {
/*  317: 688 */         FString.append("all attributes\n");
/*  318:     */       } else {
/*  319: 690 */         FString.append("no attributes\n");
/*  320:     */       }
/*  321:     */     }
/*  322:     */     else {
/*  323: 693 */       FString.append(startSetToString() + "\n");
/*  324:     */     }
/*  325: 695 */     if (!this.m_doneRanking) {
/*  326: 696 */       FString.append("\tMerit of best subset found: " + Utils.doubleToString(Math.abs(this.m_bestMerit), 8, 3) + "\n");
/*  327: 699 */     } else if (this.m_backward) {
/*  328: 700 */       FString.append("\n\tRanking is the order that attributes were removed, starting \n\twith all attributes. The merit scores in the left\n\tcolumn are the goodness of the remaining attributes in the\n\tsubset after removing the corresponding in the right column\n\tattribute from the subset.\n");
/*  329:     */     } else {
/*  330: 707 */       FString.append("\n\tRanking is the order that attributes were added, starting \n\twith no attributes. The merit scores in the left column\n\tare the goodness of the subset after the adding the\n\tcorresponding attribute in the right column to the subset.\n");
/*  331:     */     }
/*  332: 715 */     if ((this.m_threshold != -1.797693134862316E+308D) && (this.m_doneRanking)) {
/*  333: 716 */       FString.append("\tThreshold for discarding attributes: " + Utils.doubleToString(this.m_threshold, 8, 4) + "\n");
/*  334:     */     }
/*  335: 720 */     return FString.toString();
/*  336:     */   }
/*  337:     */   
/*  338:     */   public int[] search(ASEvaluation ASEval, Instances data)
/*  339:     */     throws Exception
/*  340:     */   {
/*  341: 735 */     double best_merit = -1.797693134862316E+308D;
/*  342:     */     
/*  343: 737 */     int temp_index = 0;
/*  344:     */     
/*  345: 739 */     boolean parallel = this.m_poolSize > 1;
/*  346: 740 */     if (parallel) {
/*  347: 741 */       this.m_pool = Executors.newFixedThreadPool(this.m_poolSize);
/*  348:     */     }
/*  349: 744 */     if (data != null)
/*  350:     */     {
/*  351: 745 */       resetOptions();
/*  352: 746 */       this.m_Instances = new Instances(data, 0);
/*  353:     */     }
/*  354: 748 */     this.m_ASEval = ASEval;
/*  355:     */     
/*  356: 750 */     this.m_numAttribs = this.m_Instances.numAttributes();
/*  357: 752 */     if (this.m_best_group == null) {
/*  358: 753 */       this.m_best_group = new BitSet(this.m_numAttribs);
/*  359:     */     }
/*  360: 756 */     if (!(this.m_ASEval instanceof SubsetEvaluator)) {
/*  361: 757 */       throw new Exception(this.m_ASEval.getClass().getName() + " is not a " + "Subset evaluator!");
/*  362:     */     }
/*  363: 761 */     this.m_startRange.setUpper(this.m_numAttribs - 1);
/*  364: 762 */     if (!getStartSet().equals("")) {
/*  365: 763 */       this.m_starting = this.m_startRange.getSelection();
/*  366:     */     }
/*  367: 766 */     if ((this.m_ASEval instanceof UnsupervisedSubsetEvaluator))
/*  368:     */     {
/*  369: 767 */       this.m_hasClass = false;
/*  370: 768 */       this.m_classIndex = -1;
/*  371:     */     }
/*  372:     */     else
/*  373:     */     {
/*  374: 770 */       this.m_hasClass = true;
/*  375: 771 */       this.m_classIndex = this.m_Instances.classIndex();
/*  376:     */     }
/*  377: 774 */     SubsetEvaluator ASEvaluator = (SubsetEvaluator)this.m_ASEval;
/*  378: 776 */     if (this.m_rankedAtts == null)
/*  379:     */     {
/*  380: 777 */       this.m_rankedAtts = new double[this.m_numAttribs][2];
/*  381: 778 */       this.m_rankedSoFar = 0;
/*  382:     */     }
/*  383:     */     int i;
/*  384: 782 */     if ((this.m_starting != null) && (this.m_rankedSoFar <= 0)) {
/*  385: 783 */       for (i = 0; i < this.m_starting.length;)
/*  386:     */       {
/*  387: 784 */         if (this.m_starting[i] != this.m_classIndex) {
/*  388: 785 */           this.m_best_group.set(this.m_starting[i]);
/*  389:     */         }
/*  390: 783 */         i++; continue;
/*  391: 789 */         if ((this.m_backward) && (this.m_rankedSoFar <= 0)) {
/*  392: 790 */           for (int i = 0; i < this.m_numAttribs; i++) {
/*  393: 791 */             if (i != this.m_classIndex) {
/*  394: 792 */               this.m_best_group.set(i);
/*  395:     */             }
/*  396:     */           }
/*  397:     */         }
/*  398:     */       }
/*  399:     */     }
/*  400: 799 */     best_merit = ASEvaluator.evaluateSubset(this.m_best_group);
/*  401:     */     
/*  402:     */ 
/*  403: 802 */     boolean done = false;
/*  404: 803 */     boolean addone = false;
/*  405: 806 */     if ((this.m_debug) && (parallel)) {
/*  406: 807 */       System.err.println("Evaluating subsets in parallel...");
/*  407:     */     }
/*  408: 809 */     while (!done)
/*  409:     */     {
/*  410: 810 */       List<Future<Double[]>> results = new ArrayList();
/*  411: 811 */       BitSet temp_group = (BitSet)this.m_best_group.clone();
/*  412: 812 */       double temp_best = best_merit;
/*  413: 813 */       if (this.m_doRank) {
/*  414: 814 */         temp_best = -1.797693134862316E+308D;
/*  415:     */       }
/*  416: 816 */       done = true;
/*  417: 817 */       addone = false;
/*  418: 818 */       for (int i = 0; i < this.m_numAttribs; i++)
/*  419:     */       {
/*  420:     */         boolean z;
/*  421:     */         boolean z;
/*  422: 819 */         if (this.m_backward) {
/*  423: 820 */           z = (i != this.m_classIndex) && (temp_group.get(i));
/*  424:     */         } else {
/*  425: 822 */           z = (i != this.m_classIndex) && (!temp_group.get(i));
/*  426:     */         }
/*  427: 824 */         if (z)
/*  428:     */         {
/*  429: 826 */           if (this.m_backward) {
/*  430: 827 */             temp_group.clear(i);
/*  431:     */           } else {
/*  432: 829 */             temp_group.set(i);
/*  433:     */           }
/*  434: 832 */           if (parallel)
/*  435:     */           {
/*  436: 833 */             final BitSet tempCopy = (BitSet)temp_group.clone();
/*  437: 834 */             final int attBeingEvaluated = i;
/*  438:     */             
/*  439:     */ 
/*  440: 837 */             final SubsetEvaluator theEvaluator = (ASEvaluator instanceof ThreadSafe) ? ASEvaluator : (SubsetEvaluator)ASEvaluation.makeCopies(this.m_ASEval, 1)[0];
/*  441:     */             
/*  442:     */ 
/*  443:     */ 
/*  444: 841 */             Future<Double[]> future = this.m_pool.submit(new Callable()
/*  445:     */             {
/*  446:     */               public Double[] call()
/*  447:     */                 throws Exception
/*  448:     */               {
/*  449: 844 */                 Double[] r = new Double[2];
/*  450: 845 */                 double e = theEvaluator.evaluateSubset(tempCopy);
/*  451: 846 */                 r[0] = new Double(attBeingEvaluated);
/*  452: 847 */                 r[1] = Double.valueOf(e);
/*  453: 848 */                 return r;
/*  454:     */               }
/*  455: 851 */             });
/*  456: 852 */             results.add(future);
/*  457:     */           }
/*  458:     */           else
/*  459:     */           {
/*  460: 854 */             double temp_merit = ASEvaluator.evaluateSubset(temp_group);
/*  461: 855 */             if (this.m_backward) {
/*  462: 856 */               z = temp_merit >= temp_best;
/*  463: 858 */             } else if (this.m_conservativeSelection) {
/*  464: 859 */               z = temp_merit >= temp_best;
/*  465:     */             } else {
/*  466: 861 */               z = temp_merit > temp_best;
/*  467:     */             }
/*  468: 865 */             if (z)
/*  469:     */             {
/*  470: 866 */               temp_best = temp_merit;
/*  471: 867 */               temp_index = i;
/*  472: 868 */               addone = true;
/*  473: 869 */               done = false;
/*  474:     */             }
/*  475:     */           }
/*  476: 874 */           if (this.m_backward) {
/*  477: 875 */             temp_group.set(i);
/*  478:     */           } else {
/*  479: 877 */             temp_group.clear(i);
/*  480:     */           }
/*  481: 879 */           if (this.m_doRank) {
/*  482: 880 */             done = false;
/*  483:     */           }
/*  484:     */         }
/*  485:     */       }
/*  486: 885 */       if (parallel) {
/*  487: 886 */         for (int j = 0; j < results.size(); j++)
/*  488:     */         {
/*  489: 887 */           Future<Double[]> f = (Future)results.get(j);
/*  490:     */           
/*  491: 889 */           int index = ((Double[])f.get())[0].intValue();
/*  492: 890 */           double temp_merit = ((Double[])f.get())[1].doubleValue();
/*  493:     */           boolean z;
/*  494:     */           boolean z;
/*  495: 892 */           if (this.m_backward)
/*  496:     */           {
/*  497: 893 */             z = temp_merit >= temp_best;
/*  498:     */           }
/*  499:     */           else
/*  500:     */           {
/*  501:     */             boolean z;
/*  502: 895 */             if (this.m_conservativeSelection) {
/*  503: 896 */               z = temp_merit >= temp_best;
/*  504:     */             } else {
/*  505: 898 */               z = temp_merit > temp_best;
/*  506:     */             }
/*  507:     */           }
/*  508: 902 */           if (z)
/*  509:     */           {
/*  510: 903 */             temp_best = temp_merit;
/*  511: 904 */             temp_index = index;
/*  512: 905 */             addone = true;
/*  513: 906 */             done = false;
/*  514:     */           }
/*  515:     */         }
/*  516:     */       }
/*  517: 911 */       if (addone)
/*  518:     */       {
/*  519: 912 */         if (this.m_backward) {
/*  520: 913 */           this.m_best_group.clear(temp_index);
/*  521:     */         } else {
/*  522: 915 */           this.m_best_group.set(temp_index);
/*  523:     */         }
/*  524: 917 */         best_merit = temp_best;
/*  525: 918 */         if (this.m_debug)
/*  526:     */         {
/*  527: 919 */           System.err.print("Best subset found so far: ");
/*  528: 920 */           int[] atts = attributeList(this.m_best_group);
/*  529: 921 */           for (int a : atts) {
/*  530: 922 */             System.err.print("" + (a + 1) + " ");
/*  531:     */           }
/*  532: 924 */           System.err.println("\nMerit: " + best_merit);
/*  533:     */         }
/*  534: 926 */         this.m_rankedAtts[this.m_rankedSoFar][0] = temp_index;
/*  535: 927 */         this.m_rankedAtts[this.m_rankedSoFar][1] = best_merit;
/*  536: 928 */         this.m_rankedSoFar += 1;
/*  537:     */       }
/*  538:     */     }
/*  539: 932 */     if (parallel) {
/*  540: 933 */       this.m_pool.shutdown();
/*  541:     */     }
/*  542: 936 */     this.m_bestMerit = best_merit;
/*  543: 937 */     return attributeList(this.m_best_group);
/*  544:     */   }
/*  545:     */   
/*  546:     */   public double[][] rankedAttributes()
/*  547:     */     throws Exception
/*  548:     */   {
/*  549: 957 */     if ((this.m_rankedAtts == null) || (this.m_rankedSoFar == -1)) {
/*  550: 958 */       throw new Exception("Search must be performed before attributes can be ranked.");
/*  551:     */     }
/*  552: 962 */     this.m_doRank = true;
/*  553: 963 */     search(this.m_ASEval, null);
/*  554:     */     
/*  555: 965 */     double[][] final_rank = new double[this.m_rankedSoFar][2];
/*  556: 966 */     for (int i = 0; i < this.m_rankedSoFar; i++)
/*  557:     */     {
/*  558: 967 */       final_rank[i][0] = this.m_rankedAtts[i][0];
/*  559: 968 */       final_rank[i][1] = this.m_rankedAtts[i][1];
/*  560:     */     }
/*  561: 971 */     resetOptions();
/*  562: 972 */     this.m_doneRanking = true;
/*  563: 974 */     if (this.m_numToSelect > final_rank.length) {
/*  564: 975 */       throw new Exception("More attributes requested than exist in the data");
/*  565:     */     }
/*  566: 978 */     if (this.m_numToSelect <= 0) {
/*  567: 979 */       if (this.m_threshold == -1.797693134862316E+308D) {
/*  568: 980 */         this.m_calculatedNumToSelect = final_rank.length;
/*  569:     */       } else {
/*  570: 982 */         determineNumToSelectFromThreshold(final_rank);
/*  571:     */       }
/*  572:     */     }
/*  573: 986 */     return final_rank;
/*  574:     */   }
/*  575:     */   
/*  576:     */   private void determineNumToSelectFromThreshold(double[][] ranking)
/*  577:     */   {
/*  578: 990 */     int count = 0;
/*  579: 991 */     for (double[] element : ranking) {
/*  580: 992 */       if (element[1] > this.m_threshold) {
/*  581: 993 */         count++;
/*  582:     */       }
/*  583:     */     }
/*  584: 996 */     this.m_calculatedNumToSelect = count;
/*  585:     */   }
/*  586:     */   
/*  587:     */   protected int[] attributeList(BitSet group)
/*  588:     */   {
/*  589:1006 */     int count = 0;
/*  590:1009 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  591:1010 */       if (group.get(i)) {
/*  592:1011 */         count++;
/*  593:     */       }
/*  594:     */     }
/*  595:1015 */     int[] list = new int[count];
/*  596:1016 */     count = 0;
/*  597:1018 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  598:1019 */       if (group.get(i)) {
/*  599:1020 */         list[(count++)] = i;
/*  600:     */       }
/*  601:     */     }
/*  602:1024 */     return list;
/*  603:     */   }
/*  604:     */   
/*  605:     */   protected void resetOptions()
/*  606:     */   {
/*  607:1031 */     this.m_doRank = false;
/*  608:1032 */     this.m_best_group = null;
/*  609:1033 */     this.m_ASEval = null;
/*  610:1034 */     this.m_Instances = null;
/*  611:1035 */     this.m_rankedSoFar = -1;
/*  612:1036 */     this.m_rankedAtts = ((double[][])null);
/*  613:     */   }
/*  614:     */   
/*  615:     */   public String getRevision()
/*  616:     */   {
/*  617:1046 */     return RevisionUtils.extract("$Revision: 11227 $");
/*  618:     */   }
/*  619:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.GreedyStepwise
 * JD-Core Version:    0.7.0.1
 */