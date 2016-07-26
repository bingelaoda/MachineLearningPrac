/*    1:     */ package weka.associations;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.Hashtable;
/*    7:     */ import java.util.List;
/*    8:     */ import java.util.Vector;
/*    9:     */ import weka.core.AttributeStats;
/*   10:     */ import weka.core.Capabilities;
/*   11:     */ import weka.core.Capabilities.Capability;
/*   12:     */ import weka.core.Instances;
/*   13:     */ import weka.core.Option;
/*   14:     */ import weka.core.OptionHandler;
/*   15:     */ import weka.core.RevisionUtils;
/*   16:     */ import weka.core.SelectedTag;
/*   17:     */ import weka.core.Tag;
/*   18:     */ import weka.core.TechnicalInformation;
/*   19:     */ import weka.core.TechnicalInformation.Field;
/*   20:     */ import weka.core.TechnicalInformation.Type;
/*   21:     */ import weka.core.TechnicalInformationHandler;
/*   22:     */ import weka.core.Utils;
/*   23:     */ import weka.core.WekaEnumeration;
/*   24:     */ import weka.filters.Filter;
/*   25:     */ import weka.filters.unsupervised.attribute.Remove;
/*   26:     */ 
/*   27:     */ public class Apriori
/*   28:     */   extends AbstractAssociator
/*   29:     */   implements OptionHandler, AssociationRulesProducer, CARuleMiner, TechnicalInformationHandler
/*   30:     */ {
/*   31:     */   static final long serialVersionUID = 3277498842319212687L;
/*   32:     */   protected double m_minSupport;
/*   33:     */   protected double m_upperBoundMinSupport;
/*   34:     */   protected double m_lowerBoundMinSupport;
/*   35:     */   protected static final int CONFIDENCE = 0;
/*   36:     */   protected static final int LIFT = 1;
/*   37:     */   protected static final int LEVERAGE = 2;
/*   38:     */   protected static final int CONVICTION = 3;
/*   39: 200 */   public static final Tag[] TAGS_SELECTION = { new Tag(0, "Confidence"), new Tag(1, "Lift"), new Tag(2, "Leverage"), new Tag(3, "Conviction") };
/*   40: 205 */   protected int m_metricType = 0;
/*   41:     */   protected double m_minMetric;
/*   42:     */   protected int m_numRules;
/*   43:     */   protected double m_delta;
/*   44:     */   protected double m_significanceLevel;
/*   45:     */   protected int m_cycles;
/*   46:     */   protected ArrayList<ArrayList<Object>> m_Ls;
/*   47:     */   protected ArrayList<Hashtable<ItemSet, Integer>> m_hashtables;
/*   48:     */   protected ArrayList<Object>[] m_allTheRules;
/*   49:     */   protected Instances m_instances;
/*   50:     */   protected boolean m_outputItemSets;
/*   51:     */   protected boolean m_removeMissingCols;
/*   52:     */   protected boolean m_verbose;
/*   53:     */   protected Instances m_onlyClass;
/*   54:     */   protected int m_classIndex;
/*   55:     */   protected boolean m_car;
/*   56: 258 */   protected boolean m_treatZeroAsMissing = false;
/*   57: 263 */   protected String m_toStringDelimiters = null;
/*   58:     */   
/*   59:     */   public String globalInfo()
/*   60:     */   {
/*   61: 272 */     return "Class implementing an Apriori-type algorithm. Iteratively reduces the minimum support until it finds the required number of rules with the given minimum confidence.\nThe algorithm has an option to mine class association rules. It is adapted as explained in the second reference.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*   62:     */   }
/*   63:     */   
/*   64:     */   public TechnicalInformation getTechnicalInformation()
/*   65:     */   {
/*   66: 292 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*   67: 293 */     result.setValue(TechnicalInformation.Field.AUTHOR, "R. Agrawal and R. Srikant");
/*   68: 294 */     result.setValue(TechnicalInformation.Field.TITLE, "Fast Algorithms for Mining Association Rules in Large Databases");
/*   69:     */     
/*   70: 296 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "20th International Conference on Very Large Data Bases");
/*   71:     */     
/*   72: 298 */     result.setValue(TechnicalInformation.Field.YEAR, "1994");
/*   73: 299 */     result.setValue(TechnicalInformation.Field.PAGES, "478-499");
/*   74: 300 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann, Los Altos, CA");
/*   75:     */     
/*   76: 302 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.INPROCEEDINGS);
/*   77: 303 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Bing Liu and Wynne Hsu and Yiming Ma");
/*   78: 304 */     additional.setValue(TechnicalInformation.Field.TITLE, "Integrating Classification and Association Rule Mining");
/*   79:     */     
/*   80: 306 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "Fourth International Conference on Knowledge Discovery and Data Mining");
/*   81:     */     
/*   82: 308 */     additional.setValue(TechnicalInformation.Field.YEAR, "1998");
/*   83: 309 */     additional.setValue(TechnicalInformation.Field.PAGES, "80-86");
/*   84: 310 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "AAAI Press");
/*   85:     */     
/*   86: 312 */     return result;
/*   87:     */   }
/*   88:     */   
/*   89:     */   public Apriori()
/*   90:     */   {
/*   91: 321 */     resetOptions();
/*   92:     */   }
/*   93:     */   
/*   94:     */   public void resetOptions()
/*   95:     */   {
/*   96: 329 */     this.m_removeMissingCols = false;
/*   97: 330 */     this.m_verbose = false;
/*   98: 331 */     this.m_delta = 0.05D;
/*   99: 332 */     this.m_minMetric = 0.9D;
/*  100: 333 */     this.m_numRules = 10;
/*  101: 334 */     this.m_lowerBoundMinSupport = 0.1D;
/*  102: 335 */     this.m_upperBoundMinSupport = 1.0D;
/*  103: 336 */     this.m_significanceLevel = -1.0D;
/*  104: 337 */     this.m_outputItemSets = false;
/*  105: 338 */     this.m_car = false;
/*  106: 339 */     this.m_classIndex = -1;
/*  107: 340 */     this.m_treatZeroAsMissing = false;
/*  108: 341 */     this.m_metricType = 0;
/*  109:     */   }
/*  110:     */   
/*  111:     */   protected Instances removeMissingColumns(Instances instances)
/*  112:     */     throws Exception
/*  113:     */   {
/*  114: 354 */     int numInstances = instances.numInstances();
/*  115: 355 */     StringBuffer deleteString = new StringBuffer();
/*  116: 356 */     int removeCount = 0;
/*  117: 357 */     boolean first = true;
/*  118: 358 */     int maxCount = 0;
/*  119: 360 */     for (int i = 0; i < instances.numAttributes(); i++)
/*  120:     */     {
/*  121: 361 */       AttributeStats as = instances.attributeStats(i);
/*  122: 362 */       if ((this.m_upperBoundMinSupport == 1.0D) && (maxCount != numInstances))
/*  123:     */       {
/*  124: 364 */         int[] counts = as.nominalCounts;
/*  125: 365 */         if (counts[Utils.maxIndex(counts)] > maxCount) {
/*  126: 366 */           maxCount = counts[Utils.maxIndex(counts)];
/*  127:     */         }
/*  128:     */       }
/*  129: 369 */       if (as.missingCount == numInstances)
/*  130:     */       {
/*  131: 370 */         if (first)
/*  132:     */         {
/*  133: 371 */           deleteString.append(i + 1);
/*  134: 372 */           first = false;
/*  135:     */         }
/*  136:     */         else
/*  137:     */         {
/*  138: 374 */           deleteString.append("," + (i + 1));
/*  139:     */         }
/*  140: 376 */         removeCount++;
/*  141:     */       }
/*  142:     */     }
/*  143: 379 */     if (this.m_verbose) {
/*  144: 380 */       System.err.println("Removed : " + removeCount + " columns with all missing " + "values.");
/*  145:     */     }
/*  146: 383 */     if ((this.m_upperBoundMinSupport == 1.0D) && (maxCount != numInstances))
/*  147:     */     {
/*  148: 384 */       this.m_upperBoundMinSupport = (maxCount / numInstances);
/*  149: 385 */       if (this.m_verbose) {
/*  150: 386 */         System.err.println("Setting upper bound min support to : " + this.m_upperBoundMinSupport);
/*  151:     */       }
/*  152:     */     }
/*  153: 391 */     if (deleteString.toString().length() > 0)
/*  154:     */     {
/*  155: 392 */       Remove af = new Remove();
/*  156: 393 */       af.setAttributeIndices(deleteString.toString());
/*  157: 394 */       af.setInvertSelection(false);
/*  158: 395 */       af.setInputFormat(instances);
/*  159: 396 */       Instances newInst = Filter.useFilter(instances, af);
/*  160:     */       
/*  161: 398 */       return newInst;
/*  162:     */     }
/*  163: 400 */     return instances;
/*  164:     */   }
/*  165:     */   
/*  166:     */   public Capabilities getCapabilities()
/*  167:     */   {
/*  168: 410 */     Capabilities result = super.getCapabilities();
/*  169: 411 */     result.disableAll();
/*  170:     */     
/*  171:     */ 
/*  172:     */ 
/*  173:     */ 
/*  174: 416 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  175: 417 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  176:     */     
/*  177:     */ 
/*  178: 420 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  179: 421 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  180: 422 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  181:     */     
/*  182: 424 */     return result;
/*  183:     */   }
/*  184:     */   
/*  185:     */   public void buildAssociations(Instances instances)
/*  186:     */     throws Exception
/*  187:     */   {
/*  188: 441 */     double necSupport = 0.0D;
/*  189:     */     
/*  190: 443 */     instances = new Instances(instances);
/*  191: 445 */     if (this.m_removeMissingCols) {
/*  192: 446 */       instances = removeMissingColumns(instances);
/*  193:     */     }
/*  194: 448 */     if ((this.m_car) && (this.m_metricType != 0)) {
/*  195: 449 */       throw new Exception("For CAR-Mining metric type has to be confidence!");
/*  196:     */     }
/*  197: 453 */     if (this.m_car) {
/*  198: 454 */       if (this.m_classIndex == -1) {
/*  199: 455 */         instances.setClassIndex(instances.numAttributes() - 1);
/*  200: 456 */       } else if ((this.m_classIndex <= instances.numAttributes()) && (this.m_classIndex > 0)) {
/*  201: 457 */         instances.setClassIndex(this.m_classIndex - 1);
/*  202:     */       } else {
/*  203: 459 */         throw new Exception("Invalid class index.");
/*  204:     */       }
/*  205:     */     }
/*  206: 464 */     getCapabilities().testWithFail(instances);
/*  207:     */     
/*  208: 466 */     this.m_cycles = 0;
/*  209:     */     
/*  210:     */ 
/*  211: 469 */     double lowerBoundMinSupportToUse = this.m_lowerBoundMinSupport * instances.numInstances() < 1.0D ? 1.0D / instances.numInstances() : this.m_lowerBoundMinSupport;
/*  212: 473 */     if (this.m_car)
/*  213:     */     {
/*  214: 475 */       this.m_instances = LabeledItemSet.divide(instances, false);
/*  215:     */       
/*  216:     */ 
/*  217: 478 */       this.m_onlyClass = LabeledItemSet.divide(instances, true);
/*  218:     */     }
/*  219:     */     else
/*  220:     */     {
/*  221: 480 */       this.m_instances = instances;
/*  222:     */     }
/*  223: 483 */     if ((this.m_car) && (this.m_numRules == 2147483647))
/*  224:     */     {
/*  225: 485 */       this.m_minSupport = lowerBoundMinSupportToUse;
/*  226:     */     }
/*  227:     */     else
/*  228:     */     {
/*  229: 489 */       this.m_minSupport = (1.0D - this.m_delta);
/*  230: 490 */       this.m_minSupport = (this.m_minSupport < lowerBoundMinSupportToUse ? lowerBoundMinSupportToUse : this.m_minSupport);
/*  231:     */     }
/*  232:     */     do
/*  233:     */     {
/*  234: 496 */       this.m_Ls = new ArrayList();
/*  235: 497 */       this.m_hashtables = new ArrayList();
/*  236: 498 */       this.m_allTheRules = new ArrayList[6];
/*  237: 499 */       this.m_allTheRules[0] = new ArrayList();
/*  238: 500 */       this.m_allTheRules[1] = new ArrayList();
/*  239: 501 */       this.m_allTheRules[2] = new ArrayList();
/*  240:     */       
/*  241: 503 */       this.m_allTheRules[3] = new ArrayList();
/*  242: 504 */       this.m_allTheRules[4] = new ArrayList();
/*  243: 505 */       this.m_allTheRules[5] = new ArrayList();
/*  244:     */       
/*  245: 507 */       ArrayList<Object>[] sortedRuleSet = new ArrayList[6];
/*  246: 508 */       sortedRuleSet[0] = new ArrayList();
/*  247: 509 */       sortedRuleSet[1] = new ArrayList();
/*  248: 510 */       sortedRuleSet[2] = new ArrayList();
/*  249:     */       
/*  250: 512 */       sortedRuleSet[3] = new ArrayList();
/*  251: 513 */       sortedRuleSet[4] = new ArrayList();
/*  252: 514 */       sortedRuleSet[5] = new ArrayList();
/*  253: 516 */       if (!this.m_car)
/*  254:     */       {
/*  255: 518 */         findLargeItemSets();
/*  256: 519 */         if ((this.m_significanceLevel != -1.0D) || (this.m_metricType != 0)) {
/*  257: 520 */           findRulesBruteForce();
/*  258:     */         } else {
/*  259: 522 */           findRulesQuickly();
/*  260:     */         }
/*  261:     */       }
/*  262:     */       else
/*  263:     */       {
/*  264: 525 */         findLargeCarItemSets();
/*  265: 526 */         findCarRulesQuickly();
/*  266:     */       }
/*  267: 530 */       if (this.m_upperBoundMinSupport < 1.0D) {
/*  268: 531 */         pruneRulesForUpperBoundSupport();
/*  269:     */       }
/*  270: 549 */       int j = this.m_allTheRules[2].size() - 1;
/*  271: 550 */       double[] supports = new double[this.m_allTheRules[2].size()];
/*  272: 551 */       for (int i = 0; i < j + 1; i++) {
/*  273: 552 */         supports[(j - i)] = (((ItemSet)this.m_allTheRules[1].get(j - i)).support() * -1.0D);
/*  274:     */       }
/*  275: 555 */       int[] indices = Utils.stableSort(supports);
/*  276: 556 */       for (int i = 0; i < j + 1; i++)
/*  277:     */       {
/*  278: 557 */         sortedRuleSet[0].add(this.m_allTheRules[0].get(indices[(j - i)]));
/*  279: 558 */         sortedRuleSet[1].add(this.m_allTheRules[1].get(indices[(j - i)]));
/*  280: 559 */         sortedRuleSet[2].add(this.m_allTheRules[2].get(indices[(j - i)]));
/*  281: 560 */         if (!this.m_car)
/*  282:     */         {
/*  283: 562 */           sortedRuleSet[3].add(this.m_allTheRules[3].get(indices[(j - i)]));
/*  284: 563 */           sortedRuleSet[4].add(this.m_allTheRules[4].get(indices[(j - i)]));
/*  285: 564 */           sortedRuleSet[5].add(this.m_allTheRules[5].get(indices[(j - i)]));
/*  286:     */         }
/*  287:     */       }
/*  288: 570 */       this.m_allTheRules[0].clear();
/*  289: 571 */       this.m_allTheRules[1].clear();
/*  290: 572 */       this.m_allTheRules[2].clear();
/*  291:     */       
/*  292: 574 */       this.m_allTheRules[3].clear();
/*  293: 575 */       this.m_allTheRules[4].clear();
/*  294: 576 */       this.m_allTheRules[5].clear();
/*  295:     */       
/*  296: 578 */       double[] confidences = new double[sortedRuleSet[2].size()];
/*  297: 579 */       int sortType = 2 + this.m_metricType;
/*  298: 581 */       for (int i = 0; i < sortedRuleSet[2].size(); i++) {
/*  299: 582 */         confidences[i] = ((Double)sortedRuleSet[sortType].get(i)).doubleValue();
/*  300:     */       }
/*  301: 585 */       indices = Utils.stableSort(confidences);
/*  302: 586 */       for (int i = sortedRuleSet[0].size() - 1; (i >= sortedRuleSet[0].size() - this.m_numRules) && (i >= 0); i--)
/*  303:     */       {
/*  304: 588 */         this.m_allTheRules[0].add(sortedRuleSet[0].get(indices[i]));
/*  305: 589 */         this.m_allTheRules[1].add(sortedRuleSet[1].get(indices[i]));
/*  306: 590 */         this.m_allTheRules[2].add(sortedRuleSet[2].get(indices[i]));
/*  307: 592 */         if (!this.m_car)
/*  308:     */         {
/*  309: 593 */           this.m_allTheRules[3].add(sortedRuleSet[3].get(indices[i]));
/*  310: 594 */           this.m_allTheRules[4].add(sortedRuleSet[4].get(indices[i]));
/*  311: 595 */           this.m_allTheRules[5].add(sortedRuleSet[5].get(indices[i]));
/*  312:     */         }
/*  313:     */       }
/*  314: 600 */       if ((this.m_verbose) && 
/*  315: 601 */         (this.m_Ls.size() > 1)) {
/*  316: 602 */         System.out.println(toString());
/*  317:     */       }
/*  318: 606 */       if ((this.m_minSupport == lowerBoundMinSupportToUse) || (this.m_minSupport - this.m_delta > lowerBoundMinSupportToUse)) {
/*  319: 608 */         this.m_minSupport -= this.m_delta;
/*  320:     */       } else {
/*  321: 610 */         this.m_minSupport = lowerBoundMinSupportToUse;
/*  322:     */       }
/*  323: 613 */       necSupport = Math.rint(this.m_minSupport * this.m_instances.numInstances());
/*  324:     */       
/*  325: 615 */       this.m_cycles += 1;
/*  326: 619 */     } while ((this.m_allTheRules[0].size() < this.m_numRules) && (Utils.grOrEq(this.m_minSupport, lowerBoundMinSupportToUse)) && (necSupport >= 1.0D));
/*  327: 620 */     this.m_minSupport += this.m_delta;
/*  328:     */   }
/*  329:     */   
/*  330:     */   private void pruneRulesForUpperBoundSupport()
/*  331:     */   {
/*  332: 624 */     int necMaxSupport = (int)(this.m_upperBoundMinSupport * this.m_instances.numInstances() + 0.5D);
/*  333:     */     
/*  334:     */ 
/*  335:     */ 
/*  336: 628 */     ArrayList<Object>[] prunedRules = new ArrayList[6];
/*  337: 629 */     for (int i = 0; i < 6; i++) {
/*  338: 630 */       prunedRules[i] = new ArrayList();
/*  339:     */     }
/*  340: 633 */     for (int i = 0; i < this.m_allTheRules[0].size(); i++) {
/*  341: 634 */       if (((ItemSet)this.m_allTheRules[1].get(i)).support() <= necMaxSupport)
/*  342:     */       {
/*  343: 635 */         prunedRules[0].add(this.m_allTheRules[0].get(i));
/*  344: 636 */         prunedRules[1].add(this.m_allTheRules[1].get(i));
/*  345: 637 */         prunedRules[2].add(this.m_allTheRules[2].get(i));
/*  346: 639 */         if (!this.m_car)
/*  347:     */         {
/*  348: 640 */           prunedRules[3].add(this.m_allTheRules[3].get(i));
/*  349: 641 */           prunedRules[4].add(this.m_allTheRules[4].get(i));
/*  350: 642 */           prunedRules[5].add(this.m_allTheRules[5].get(i));
/*  351:     */         }
/*  352:     */       }
/*  353:     */     }
/*  354: 646 */     this.m_allTheRules[0] = prunedRules[0];
/*  355: 647 */     this.m_allTheRules[1] = prunedRules[1];
/*  356: 648 */     this.m_allTheRules[2] = prunedRules[2];
/*  357: 649 */     this.m_allTheRules[3] = prunedRules[3];
/*  358: 650 */     this.m_allTheRules[4] = prunedRules[4];
/*  359: 651 */     this.m_allTheRules[5] = prunedRules[5];
/*  360:     */   }
/*  361:     */   
/*  362:     */   public ArrayList<Object>[] mineCARs(Instances data)
/*  363:     */     throws Exception
/*  364:     */   {
/*  365: 667 */     this.m_car = true;
/*  366: 668 */     buildAssociations(data);
/*  367: 669 */     return this.m_allTheRules;
/*  368:     */   }
/*  369:     */   
/*  370:     */   public Instances getInstancesNoClass()
/*  371:     */   {
/*  372: 680 */     return this.m_instances;
/*  373:     */   }
/*  374:     */   
/*  375:     */   public Instances getInstancesOnlyClass()
/*  376:     */   {
/*  377: 691 */     return this.m_onlyClass;
/*  378:     */   }
/*  379:     */   
/*  380:     */   public Enumeration<Option> listOptions()
/*  381:     */   {
/*  382: 702 */     String string1 = "\tThe required number of rules. (default = " + this.m_numRules + ")";
/*  383: 703 */     String string2 = "\tThe minimum confidence of a rule. (default = " + this.m_minMetric + ")";
/*  384: 704 */     String string3 = "\tThe delta by which the minimum support is decreased in\n";String string4 = "\teach iteration. (default = " + this.m_delta + ")";
/*  385: 705 */     String string5 = "\tThe lower bound for the minimum support. (default = " + this.m_lowerBoundMinSupport + ")";
/*  386: 706 */     String string6 = "\tIf used, rules are tested for significance at\n";String string7 = "\tthe given level. Slower. (default = no significance testing)";String string8 = "\tIf set the itemsets found are also output. (default = no)";String string9 = "\tIf set class association rules are mined. (default = no)";String string10 = "\tThe class index. (default = last)";String stringType = "\tThe metric type by which to rank rules. (default = confidence)";
/*  387: 707 */     String stringZeroAsMissing = "\tTreat zero (i.e. first value of nominal attributes) as missing";
/*  388: 708 */     String stringToStringDelimiters = "\tIf used, two characters to use as rule delimiters\n\tin the result of toString: the first to delimit fields,\n\tthe second to delimit items within fields.\n\t(default = traditional toString result)";
/*  389:     */     
/*  390:     */ 
/*  391:     */ 
/*  392:     */ 
/*  393: 713 */     Vector<Option> newVector = new Vector(14);
/*  394:     */     
/*  395: 715 */     newVector.add(new Option(string1, "N", 1, "-N <required number of rules output>"));
/*  396:     */     
/*  397: 717 */     newVector.add(new Option(stringType, "T", 1, "-T <0=confidence | 1=lift | 2=leverage | 3=Conviction>"));
/*  398:     */     
/*  399: 719 */     newVector.add(new Option(string2, "C", 1, "-C <minimum metric score of a rule>"));
/*  400:     */     
/*  401: 721 */     newVector.add(new Option(string3 + string4, "D", 1, "-D <delta for minimum support>"));
/*  402:     */     
/*  403: 723 */     newVector.add(new Option("\tUpper bound for minimum support. (default = 1.0)", "U", 1, "-U <upper bound for minimum support>"));
/*  404:     */     
/*  405: 725 */     newVector.add(new Option(string5, "M", 1, "-M <lower bound for minimum support>"));
/*  406:     */     
/*  407: 727 */     newVector.add(new Option(string6 + string7, "S", 1, "-S <significance level>"));
/*  408:     */     
/*  409: 729 */     newVector.add(new Option(string8, "I", 0, "-I"));
/*  410: 730 */     newVector.add(new Option("\tRemove columns that contain all missing values (default = no)", "R", 0, "-R"));
/*  411:     */     
/*  412: 732 */     newVector.add(new Option("\tReport progress iteratively. (default = no)", "V", 0, "-V"));
/*  413:     */     
/*  414: 734 */     newVector.add(new Option(string9, "A", 0, "-A"));
/*  415: 735 */     newVector.add(new Option(stringZeroAsMissing, "Z", 0, "-Z"));
/*  416: 736 */     newVector.add(new Option(stringToStringDelimiters, "B", 1, "-B <toString delimiters>"));
/*  417:     */     
/*  418: 738 */     newVector.add(new Option(string10, "c", 1, "-c <the class index>"));
/*  419:     */     
/*  420: 740 */     return newVector.elements();
/*  421:     */   }
/*  422:     */   
/*  423:     */   public void setOptions(String[] options)
/*  424:     */     throws Exception
/*  425:     */   {
/*  426: 833 */     resetOptions();
/*  427: 834 */     String numRulesString = Utils.getOption('N', options);String minConfidenceString = Utils.getOption('C', options);
/*  428: 835 */     String deltaString = Utils.getOption('D', options);String maxSupportString = Utils.getOption('U', options);
/*  429: 836 */     String minSupportString = Utils.getOption('M', options);
/*  430: 837 */     String significanceLevelString = Utils.getOption('S', options);
/*  431: 838 */     String classIndexString = Utils.getOption('c', options);String toStringDelimitersString = Utils.getOption('B', options);
/*  432:     */     
/*  433:     */ 
/*  434: 841 */     String metricTypeString = Utils.getOption('T', options);
/*  435: 842 */     if (metricTypeString.length() != 0) {
/*  436: 843 */       setMetricType(new SelectedTag(Integer.parseInt(metricTypeString), TAGS_SELECTION));
/*  437:     */     }
/*  438: 847 */     if (numRulesString.length() != 0) {
/*  439: 848 */       this.m_numRules = Integer.parseInt(numRulesString);
/*  440:     */     }
/*  441: 850 */     if (classIndexString.length() != 0) {
/*  442: 851 */       if (classIndexString.equalsIgnoreCase("last")) {
/*  443: 852 */         this.m_classIndex = -1;
/*  444: 853 */       } else if (classIndexString.equalsIgnoreCase("first")) {
/*  445: 854 */         this.m_classIndex = 0;
/*  446:     */       } else {
/*  447: 856 */         this.m_classIndex = Integer.parseInt(classIndexString);
/*  448:     */       }
/*  449:     */     }
/*  450: 859 */     if (minConfidenceString.length() != 0) {
/*  451: 860 */       this.m_minMetric = new Double(minConfidenceString).doubleValue();
/*  452:     */     }
/*  453: 862 */     if (deltaString.length() != 0) {
/*  454: 863 */       this.m_delta = new Double(deltaString).doubleValue();
/*  455:     */     }
/*  456: 865 */     if (maxSupportString.length() != 0) {
/*  457: 866 */       setUpperBoundMinSupport(new Double(maxSupportString).doubleValue());
/*  458:     */     }
/*  459: 868 */     if (minSupportString.length() != 0) {
/*  460: 869 */       this.m_lowerBoundMinSupport = new Double(minSupportString).doubleValue();
/*  461:     */     }
/*  462: 871 */     if (significanceLevelString.length() != 0) {
/*  463: 872 */       this.m_significanceLevel = new Double(significanceLevelString).doubleValue();
/*  464:     */     }
/*  465: 874 */     this.m_outputItemSets = Utils.getFlag('I', options);
/*  466: 875 */     this.m_car = Utils.getFlag('A', options);
/*  467: 876 */     this.m_verbose = Utils.getFlag('V', options);
/*  468: 877 */     this.m_treatZeroAsMissing = Utils.getFlag('Z', options);
/*  469:     */     
/*  470: 879 */     setRemoveAllMissingCols(Utils.getFlag('R', options));
/*  471: 881 */     if (toStringDelimitersString.length() == 2) {
/*  472: 882 */       this.m_toStringDelimiters = toStringDelimitersString;
/*  473:     */     }
/*  474:     */   }
/*  475:     */   
/*  476:     */   public String[] getOptions()
/*  477:     */   {
/*  478: 894 */     String[] options = new String[23];
/*  479: 895 */     int current = 0;
/*  480: 897 */     if (this.m_outputItemSets) {
/*  481: 898 */       options[(current++)] = "-I";
/*  482:     */     }
/*  483: 901 */     if (getRemoveAllMissingCols()) {
/*  484: 902 */       options[(current++)] = "-R";
/*  485:     */     }
/*  486: 905 */     options[(current++)] = "-N";
/*  487: 906 */     options[(current++)] = ("" + this.m_numRules);
/*  488: 907 */     options[(current++)] = "-T";
/*  489: 908 */     options[(current++)] = ("" + this.m_metricType);
/*  490: 909 */     options[(current++)] = "-C";
/*  491: 910 */     options[(current++)] = ("" + this.m_minMetric);
/*  492: 911 */     options[(current++)] = "-D";
/*  493: 912 */     options[(current++)] = ("" + this.m_delta);
/*  494: 913 */     options[(current++)] = "-U";
/*  495: 914 */     options[(current++)] = ("" + this.m_upperBoundMinSupport);
/*  496: 915 */     options[(current++)] = "-M";
/*  497: 916 */     options[(current++)] = ("" + this.m_lowerBoundMinSupport);
/*  498: 917 */     options[(current++)] = "-S";
/*  499: 918 */     options[(current++)] = ("" + this.m_significanceLevel);
/*  500: 919 */     if (this.m_car) {
/*  501: 920 */       options[(current++)] = "-A";
/*  502:     */     }
/*  503: 922 */     if (this.m_verbose) {
/*  504: 923 */       options[(current++)] = "-V";
/*  505:     */     }
/*  506: 926 */     if (this.m_treatZeroAsMissing) {
/*  507: 927 */       options[(current++)] = "-Z";
/*  508:     */     }
/*  509: 929 */     options[(current++)] = "-c";
/*  510: 930 */     options[(current++)] = ("" + this.m_classIndex);
/*  511: 932 */     if (this.m_toStringDelimiters != null)
/*  512:     */     {
/*  513: 933 */       options[(current++)] = "-B";
/*  514: 934 */       options[(current++)] = this.m_toStringDelimiters;
/*  515:     */     }
/*  516: 937 */     while (current < options.length) {
/*  517: 938 */       options[(current++)] = "";
/*  518:     */     }
/*  519: 940 */     return options;
/*  520:     */   }
/*  521:     */   
/*  522:     */   public String toString()
/*  523:     */   {
/*  524: 951 */     StringBuffer text = new StringBuffer();
/*  525: 953 */     if (this.m_Ls.size() <= 1) {
/*  526: 954 */       return "\nNo large itemsets and rules found!\n";
/*  527:     */     }
/*  528: 956 */     text.append("\nApriori\n=======\n\n");
/*  529: 957 */     text.append("Minimum support: " + Utils.doubleToString(this.m_minSupport, 2) + " (" + (int)(this.m_minSupport * this.m_instances.numInstances() + 0.5D) + " instances)" + '\n');
/*  530:     */     
/*  531:     */ 
/*  532: 960 */     text.append("Minimum metric <");
/*  533: 961 */     switch (this.m_metricType)
/*  534:     */     {
/*  535:     */     case 0: 
/*  536: 963 */       text.append("confidence>: ");
/*  537: 964 */       break;
/*  538:     */     case 1: 
/*  539: 966 */       text.append("lift>: ");
/*  540: 967 */       break;
/*  541:     */     case 2: 
/*  542: 969 */       text.append("leverage>: ");
/*  543: 970 */       break;
/*  544:     */     case 3: 
/*  545: 972 */       text.append("conviction>: ");
/*  546:     */     }
/*  547: 975 */     text.append(Utils.doubleToString(this.m_minMetric, 2) + '\n');
/*  548: 977 */     if (this.m_significanceLevel != -1.0D) {
/*  549: 978 */       text.append("Significance level: " + Utils.doubleToString(this.m_significanceLevel, 2) + '\n');
/*  550:     */     }
/*  551: 981 */     text.append("Number of cycles performed: " + this.m_cycles + '\n');
/*  552: 982 */     text.append("\nGenerated sets of large itemsets:\n");
/*  553: 983 */     if (!this.m_car)
/*  554:     */     {
/*  555: 984 */       for (int i = 0; i < this.m_Ls.size(); i++)
/*  556:     */       {
/*  557: 985 */         text.append("\nSize of set of large itemsets L(" + (i + 1) + "): " + ((ArrayList)this.m_Ls.get(i)).size() + '\n');
/*  558: 987 */         if (this.m_outputItemSets)
/*  559:     */         {
/*  560: 988 */           text.append("\nLarge Itemsets L(" + (i + 1) + "):\n");
/*  561: 989 */           for (int j = 0; j < ((ArrayList)this.m_Ls.get(i)).size(); j++) {
/*  562: 990 */             text.append(((AprioriItemSet)((ArrayList)this.m_Ls.get(i)).get(j)).toString(this.m_instances) + "\n");
/*  563:     */           }
/*  564:     */         }
/*  565:     */       }
/*  566: 996 */       text.append("\nBest rules found:\n\n");
/*  567: 998 */       if (this.m_toStringDelimiters != null) {
/*  568: 999 */         text.append("Number,Premise,Premise Support,Consequence,Consequence Support,Confidence,Lift,Leverage,LeverageT,Conviction\n");
/*  569:     */       }
/*  570:1003 */       for (int i = 0; i < this.m_allTheRules[0].size(); i++)
/*  571:     */       {
/*  572:     */         String convClose;
/*  573:     */         String outerDelim;
/*  574:     */         String innerDelim;
/*  575:     */         String stop;
/*  576:     */         String implies;
/*  577:     */         String confOpen;
/*  578:     */         String confClose;
/*  579:     */         String liftOpen;
/*  580:     */         String liftClose;
/*  581:     */         String levOpen;
/*  582:     */         String levInner;
/*  583:     */         String levClose;
/*  584:     */         String convOpen;
/*  585:     */         String convClose;
/*  586:1034 */         if (this.m_toStringDelimiters != null)
/*  587:     */         {
/*  588:1035 */           String outerDelim = this.m_toStringDelimiters.substring(0, 1);
/*  589:1036 */           String innerDelim = this.m_toStringDelimiters.substring(1, 2);
/*  590:     */           
/*  591:1038 */           String stop = outerDelim;
/*  592:1039 */           String implies = outerDelim;
/*  593:     */           
/*  594:1041 */           String confOpen = outerDelim;
/*  595:1042 */           String confClose = "";
/*  596:     */           
/*  597:1044 */           String liftOpen = outerDelim;
/*  598:1045 */           String liftClose = "";
/*  599:     */           
/*  600:1047 */           String levOpen = outerDelim;
/*  601:1048 */           String levInner = outerDelim;
/*  602:1049 */           String levClose = "";
/*  603:     */           
/*  604:1051 */           String convOpen = outerDelim;
/*  605:1052 */           convClose = "";
/*  606:     */         }
/*  607:     */         else
/*  608:     */         {
/*  609:1054 */           outerDelim = " ";
/*  610:1055 */           innerDelim = " ";
/*  611:     */           
/*  612:1057 */           stop = ". ";
/*  613:1058 */           implies = " ==> ";
/*  614:     */           
/*  615:1060 */           confOpen = "    " + (this.m_metricType == 0 ? "<" : "") + "conf:(";
/*  616:     */           
/*  617:1062 */           confClose = ")" + (this.m_metricType == 0 ? ">" : "");
/*  618:     */           
/*  619:1064 */           liftOpen = (this.m_metricType == 1 ? " <" : "") + " lift:(";
/*  620:1065 */           liftClose = ")" + (this.m_metricType == 1 ? ">" : "");
/*  621:     */           
/*  622:1067 */           levOpen = (this.m_metricType == 2 ? " <" : "") + " lev:(";
/*  623:1068 */           levInner = ") [";
/*  624:1069 */           levClose = "]" + (this.m_metricType == 2 ? ">" : "");
/*  625:     */           
/*  626:1071 */           convOpen = (this.m_metricType == 3 ? " <" : "") + " conv:(";
/*  627:1072 */           convClose = ")" + (this.m_metricType == 3 ? ">" : "");
/*  628:     */         }
/*  629:1075 */         char odc = outerDelim.charAt(0);
/*  630:1076 */         char idc = innerDelim.charAt(0);
/*  631:     */         
/*  632:1078 */         String n = Utils.doubleToString(i + 1.0D, (int)(Math.log(this.m_numRules) / Math.log(10.0D) + 1.0D), 0);
/*  633:     */         
/*  634:     */ 
/*  635:1081 */         String premise = ((AprioriItemSet)this.m_allTheRules[0].get(i)).toString(this.m_instances, odc, idc);
/*  636:     */         
/*  637:1083 */         String consequence = ((AprioriItemSet)this.m_allTheRules[1].get(i)).toString(this.m_instances, odc, idc);
/*  638:     */         
/*  639:     */ 
/*  640:1086 */         String confidence = Utils.doubleToString(((Double)this.m_allTheRules[2].get(i)).doubleValue(), 2);
/*  641:     */         
/*  642:1088 */         String lift = Utils.doubleToString(((Double)this.m_allTheRules[3].get(i)).doubleValue(), 2);
/*  643:     */         
/*  644:1090 */         String leverage = Utils.doubleToString(((Double)this.m_allTheRules[4].get(i)).doubleValue(), 2);
/*  645:     */         
/*  646:1092 */         String conviction = Utils.doubleToString(((Double)this.m_allTheRules[5].get(i)).doubleValue(), 2);
/*  647:     */         
/*  648:     */ 
/*  649:1095 */         int leverageT = (int)(((Double)this.m_allTheRules[4].get(i)).doubleValue() * this.m_instances.numInstances());
/*  650:     */         
/*  651:     */ 
/*  652:1098 */         text.append(n).append(stop);
/*  653:1099 */         text.append(premise).append(implies).append(consequence);
/*  654:1100 */         text.append(confOpen).append(confidence).append(confClose);
/*  655:     */         
/*  656:     */ 
/*  657:1103 */         text.append(liftOpen).append(lift).append(liftClose);
/*  658:1104 */         text.append(levOpen).append(leverage).append(levInner).append(leverageT).append(levClose);
/*  659:     */         
/*  660:1106 */         text.append(convOpen).append(conviction).append(convClose);
/*  661:     */         
/*  662:     */ 
/*  663:     */ 
/*  664:     */ 
/*  665:     */ 
/*  666:     */ 
/*  667:     */ 
/*  668:     */ 
/*  669:     */ 
/*  670:     */ 
/*  671:     */ 
/*  672:     */ 
/*  673:     */ 
/*  674:     */ 
/*  675:     */ 
/*  676:     */ 
/*  677:     */ 
/*  678:1124 */         text.append('\n');
/*  679:     */       }
/*  680:     */     }
/*  681:     */     else
/*  682:     */     {
/*  683:1127 */       for (int i = 0; i < this.m_Ls.size(); i++)
/*  684:     */       {
/*  685:1128 */         text.append("\nSize of set of large itemsets L(" + (i + 1) + "): " + ((ArrayList)this.m_Ls.get(i)).size() + '\n');
/*  686:1130 */         if (this.m_outputItemSets)
/*  687:     */         {
/*  688:1131 */           text.append("\nLarge Itemsets L(" + (i + 1) + "):\n");
/*  689:1132 */           for (int j = 0; j < ((ArrayList)this.m_Ls.get(i)).size(); j++)
/*  690:     */           {
/*  691:1133 */             text.append(((ItemSet)((ArrayList)this.m_Ls.get(i)).get(j)).toString(this.m_instances) + "\n");
/*  692:     */             
/*  693:1135 */             text.append(((LabeledItemSet)((ArrayList)this.m_Ls.get(i)).get(j)).m_classLabel + "  ");
/*  694:     */             
/*  695:1137 */             text.append(((LabeledItemSet)((ArrayList)this.m_Ls.get(i)).get(j)).support() + "\n");
/*  696:     */           }
/*  697:     */         }
/*  698:     */       }
/*  699:1142 */       text.append("\nBest rules found:\n\n");
/*  700:1144 */       if (this.m_toStringDelimiters != null) {
/*  701:1145 */         text.append("Number,Premise,Premise Support,Consequence,Consequence Support,Confidence\n");
/*  702:     */       }
/*  703:1149 */       for (int i = 0; i < this.m_allTheRules[0].size(); i++)
/*  704:     */       {
/*  705:     */         String confClose;
/*  706:     */         String outerDelim;
/*  707:     */         String innerDelim;
/*  708:     */         String stop;
/*  709:     */         String implies;
/*  710:     */         String confOpen;
/*  711:     */         String confClose;
/*  712:1168 */         if (this.m_toStringDelimiters != null)
/*  713:     */         {
/*  714:1169 */           String outerDelim = this.m_toStringDelimiters.substring(0, 1);
/*  715:1170 */           String innerDelim = this.m_toStringDelimiters.substring(1, 2);
/*  716:     */           
/*  717:1172 */           String stop = outerDelim;
/*  718:1173 */           String implies = outerDelim;
/*  719:     */           
/*  720:1175 */           String confOpen = outerDelim;
/*  721:1176 */           confClose = "";
/*  722:     */         }
/*  723:     */         else
/*  724:     */         {
/*  725:1178 */           outerDelim = " ";
/*  726:1179 */           innerDelim = " ";
/*  727:     */           
/*  728:1181 */           stop = ". ";
/*  729:1182 */           implies = " ==> ";
/*  730:     */           
/*  731:1184 */           confOpen = "    conf:(";
/*  732:1185 */           confClose = ")";
/*  733:     */         }
/*  734:1188 */         char odc = outerDelim.charAt(0);
/*  735:1189 */         char idc = innerDelim.charAt(0);
/*  736:     */         
/*  737:1191 */         String n = Utils.doubleToString(i + 1.0D, (int)(Math.log(this.m_numRules) / Math.log(10.0D) + 1.0D), 0);
/*  738:     */         
/*  739:     */ 
/*  740:1194 */         String premise = ((ItemSet)this.m_allTheRules[0].get(i)).toString(this.m_instances, odc, idc);
/*  741:     */         
/*  742:1196 */         String consequence = ((ItemSet)this.m_allTheRules[1].get(i)).toString(this.m_onlyClass, odc, idc);
/*  743:     */         
/*  744:     */ 
/*  745:1199 */         String confidence = Utils.doubleToString(((Double)this.m_allTheRules[2].get(i)).doubleValue(), 2);
/*  746:     */         
/*  747:     */ 
/*  748:1202 */         text.append(n).append(stop).append(premise).append(implies).append(consequence).append(confOpen).append(confidence).append(confClose);
/*  749:     */         
/*  750:     */ 
/*  751:     */ 
/*  752:1206 */         text.append('\n');
/*  753:     */       }
/*  754:     */     }
/*  755:1210 */     return text.toString();
/*  756:     */   }
/*  757:     */   
/*  758:     */   public String metricString()
/*  759:     */   {
/*  760:1222 */     switch (this.m_metricType)
/*  761:     */     {
/*  762:     */     case 1: 
/*  763:1224 */       return "lif";
/*  764:     */     case 2: 
/*  765:1226 */       return "leverage";
/*  766:     */     case 3: 
/*  767:1228 */       return "conviction";
/*  768:     */     }
/*  769:1230 */     return "conf";
/*  770:     */   }
/*  771:     */   
/*  772:     */   public String removeAllMissingColsTipText()
/*  773:     */   {
/*  774:1241 */     return "Remove columns with all missing values.";
/*  775:     */   }
/*  776:     */   
/*  777:     */   public void setRemoveAllMissingCols(boolean r)
/*  778:     */   {
/*  779:1250 */     this.m_removeMissingCols = r;
/*  780:     */   }
/*  781:     */   
/*  782:     */   public boolean getRemoveAllMissingCols()
/*  783:     */   {
/*  784:1259 */     return this.m_removeMissingCols;
/*  785:     */   }
/*  786:     */   
/*  787:     */   public String upperBoundMinSupportTipText()
/*  788:     */   {
/*  789:1269 */     return "Upper bound for minimum support. Start iteratively decreasing minimum support from this value.";
/*  790:     */   }
/*  791:     */   
/*  792:     */   public double getUpperBoundMinSupport()
/*  793:     */   {
/*  794:1280 */     return this.m_upperBoundMinSupport;
/*  795:     */   }
/*  796:     */   
/*  797:     */   public void setUpperBoundMinSupport(double v)
/*  798:     */   {
/*  799:1290 */     this.m_upperBoundMinSupport = v;
/*  800:     */   }
/*  801:     */   
/*  802:     */   public void setClassIndex(int index)
/*  803:     */   {
/*  804:1301 */     this.m_classIndex = index;
/*  805:     */   }
/*  806:     */   
/*  807:     */   public int getClassIndex()
/*  808:     */   {
/*  809:1311 */     return this.m_classIndex;
/*  810:     */   }
/*  811:     */   
/*  812:     */   public String classIndexTipText()
/*  813:     */   {
/*  814:1321 */     return "Index of the class attribute. If set to -1, the last attribute is taken as class attribute.";
/*  815:     */   }
/*  816:     */   
/*  817:     */   public void setCar(boolean flag)
/*  818:     */   {
/*  819:1331 */     this.m_car = flag;
/*  820:     */   }
/*  821:     */   
/*  822:     */   public boolean getCar()
/*  823:     */   {
/*  824:1340 */     return this.m_car;
/*  825:     */   }
/*  826:     */   
/*  827:     */   public String carTipText()
/*  828:     */   {
/*  829:1350 */     return "If enabled class association rules are mined instead of (general) association rules.";
/*  830:     */   }
/*  831:     */   
/*  832:     */   public String lowerBoundMinSupportTipText()
/*  833:     */   {
/*  834:1360 */     return "Lower bound for minimum support.";
/*  835:     */   }
/*  836:     */   
/*  837:     */   public double getLowerBoundMinSupport()
/*  838:     */   {
/*  839:1370 */     return this.m_lowerBoundMinSupport;
/*  840:     */   }
/*  841:     */   
/*  842:     */   public void setLowerBoundMinSupport(double v)
/*  843:     */   {
/*  844:1380 */     this.m_lowerBoundMinSupport = v;
/*  845:     */   }
/*  846:     */   
/*  847:     */   public SelectedTag getMetricType()
/*  848:     */   {
/*  849:1389 */     return new SelectedTag(this.m_metricType, TAGS_SELECTION);
/*  850:     */   }
/*  851:     */   
/*  852:     */   public String metricTypeTipText()
/*  853:     */   {
/*  854:1399 */     return "Set the type of metric by which to rank rules. Confidence is the proportion of the examples covered by the premise that are also covered by the consequence (Class association rules can only be mined using confidence). Lift is confidence divided by the proportion of all examples that are covered by the consequence. This is a measure of the importance of the association that is independent of support. Leverage is the proportion of additional examples covered by both the premise and consequence above those expected if the premise and consequence were independent of each other. The total number of examples that this represents is presented in brackets following the leverage. Conviction is another measure of departure from independence. Conviction is given by P(premise)P(!consequence) / P(premise, !consequence).";
/*  855:     */   }
/*  856:     */   
/*  857:     */   public void setMetricType(SelectedTag d)
/*  858:     */   {
/*  859:1420 */     if (d.getTags() == TAGS_SELECTION) {
/*  860:1421 */       this.m_metricType = d.getSelectedTag().getID();
/*  861:     */     }
/*  862:1424 */     if (this.m_metricType == 0) {
/*  863:1425 */       setMinMetric(0.9D);
/*  864:     */     }
/*  865:1428 */     if ((this.m_metricType == 1) || (this.m_metricType == 3)) {
/*  866:1429 */       setMinMetric(1.1D);
/*  867:     */     }
/*  868:1432 */     if (this.m_metricType == 2) {
/*  869:1433 */       setMinMetric(0.1D);
/*  870:     */     }
/*  871:     */   }
/*  872:     */   
/*  873:     */   public String minMetricTipText()
/*  874:     */   {
/*  875:1444 */     return "Minimum metric score. Consider only rules with scores higher than this value.";
/*  876:     */   }
/*  877:     */   
/*  878:     */   public double getMinMetric()
/*  879:     */   {
/*  880:1455 */     return this.m_minMetric;
/*  881:     */   }
/*  882:     */   
/*  883:     */   public void setMinMetric(double v)
/*  884:     */   {
/*  885:1465 */     this.m_minMetric = v;
/*  886:     */   }
/*  887:     */   
/*  888:     */   public String numRulesTipText()
/*  889:     */   {
/*  890:1475 */     return "Number of rules to find.";
/*  891:     */   }
/*  892:     */   
/*  893:     */   public int getNumRules()
/*  894:     */   {
/*  895:1485 */     return this.m_numRules;
/*  896:     */   }
/*  897:     */   
/*  898:     */   public void setNumRules(int v)
/*  899:     */   {
/*  900:1495 */     this.m_numRules = v;
/*  901:     */   }
/*  902:     */   
/*  903:     */   public String deltaTipText()
/*  904:     */   {
/*  905:1505 */     return "Iteratively decrease support by this factor. Reduces support until min support is reached or required number of rules has been generated.";
/*  906:     */   }
/*  907:     */   
/*  908:     */   public double getDelta()
/*  909:     */   {
/*  910:1517 */     return this.m_delta;
/*  911:     */   }
/*  912:     */   
/*  913:     */   public void setDelta(double v)
/*  914:     */   {
/*  915:1527 */     this.m_delta = v;
/*  916:     */   }
/*  917:     */   
/*  918:     */   public String significanceLevelTipText()
/*  919:     */   {
/*  920:1537 */     return "Significance level. Significance test (confidence metric only).";
/*  921:     */   }
/*  922:     */   
/*  923:     */   public double getSignificanceLevel()
/*  924:     */   {
/*  925:1547 */     return this.m_significanceLevel;
/*  926:     */   }
/*  927:     */   
/*  928:     */   public void setSignificanceLevel(double v)
/*  929:     */   {
/*  930:1557 */     this.m_significanceLevel = v;
/*  931:     */   }
/*  932:     */   
/*  933:     */   public void setOutputItemSets(boolean flag)
/*  934:     */   {
/*  935:1566 */     this.m_outputItemSets = flag;
/*  936:     */   }
/*  937:     */   
/*  938:     */   public boolean getOutputItemSets()
/*  939:     */   {
/*  940:1575 */     return this.m_outputItemSets;
/*  941:     */   }
/*  942:     */   
/*  943:     */   public String outputItemSetsTipText()
/*  944:     */   {
/*  945:1585 */     return "If enabled the itemsets are output as well.";
/*  946:     */   }
/*  947:     */   
/*  948:     */   public void setVerbose(boolean flag)
/*  949:     */   {
/*  950:1594 */     this.m_verbose = flag;
/*  951:     */   }
/*  952:     */   
/*  953:     */   public boolean getVerbose()
/*  954:     */   {
/*  955:1603 */     return this.m_verbose;
/*  956:     */   }
/*  957:     */   
/*  958:     */   public String verboseTipText()
/*  959:     */   {
/*  960:1613 */     return "If enabled the algorithm will be run in verbose mode.";
/*  961:     */   }
/*  962:     */   
/*  963:     */   public String treatZeroAsMissingTipText()
/*  964:     */   {
/*  965:1623 */     return "If enabled, zero (that is, the first value of a nominal) is treated in the same way as a missing value.";
/*  966:     */   }
/*  967:     */   
/*  968:     */   public void setTreatZeroAsMissing(boolean z)
/*  969:     */   {
/*  970:1634 */     this.m_treatZeroAsMissing = z;
/*  971:     */   }
/*  972:     */   
/*  973:     */   public boolean getTreatZeroAsMissing()
/*  974:     */   {
/*  975:1644 */     return this.m_treatZeroAsMissing;
/*  976:     */   }
/*  977:     */   
/*  978:     */   private void findLargeItemSets()
/*  979:     */     throws Exception
/*  980:     */   {
/*  981:1656 */     int i = 0;
/*  982:     */     
/*  983:     */ 
/*  984:     */ 
/*  985:     */ 
/*  986:1661 */     int necSupport = (int)(this.m_minSupport * this.m_instances.numInstances() + 0.5D);
/*  987:     */     
/*  988:1663 */     ArrayList<Object> kSets = AprioriItemSet.singletons(this.m_instances, this.m_treatZeroAsMissing);
/*  989:1664 */     if (this.m_treatZeroAsMissing) {
/*  990:1665 */       AprioriItemSet.upDateCountersTreatZeroAsMissing(kSets, this.m_instances);
/*  991:     */     } else {
/*  992:1667 */       AprioriItemSet.upDateCounters(kSets, this.m_instances);
/*  993:     */     }
/*  994:1669 */     kSets = AprioriItemSet.deleteItemSets(kSets, necSupport, this.m_instances.numInstances());
/*  995:1671 */     if (kSets.size() == 0) {
/*  996:     */       return;
/*  997:     */     }
/*  998:     */     do
/*  999:     */     {
/* 1000:1675 */       this.m_Ls.add(kSets);
/* 1001:1676 */       ArrayList<Object> kMinusOneSets = kSets;
/* 1002:1677 */       kSets = AprioriItemSet.mergeAllItemSets(kMinusOneSets, i, this.m_instances.numInstances());
/* 1003:     */       
/* 1004:1679 */       Hashtable<ItemSet, Integer> hashtable = AprioriItemSet.getHashtable(kMinusOneSets, kMinusOneSets.size());
/* 1005:     */       
/* 1006:1681 */       this.m_hashtables.add(hashtable);
/* 1007:1682 */       kSets = AprioriItemSet.pruneItemSets(kSets, hashtable);
/* 1008:1683 */       if (this.m_treatZeroAsMissing) {
/* 1009:1684 */         AprioriItemSet.upDateCountersTreatZeroAsMissing(kSets, this.m_instances);
/* 1010:     */       } else {
/* 1011:1686 */         AprioriItemSet.upDateCounters(kSets, this.m_instances);
/* 1012:     */       }
/* 1013:1688 */       kSets = AprioriItemSet.deleteItemSets(kSets, necSupport, this.m_instances.numInstances());
/* 1014:     */       
/* 1015:1690 */       i++;
/* 1016:1691 */     } while (kSets.size() > 0);
/* 1017:     */   }
/* 1018:     */   
/* 1019:     */   private void findRulesBruteForce()
/* 1020:     */     throws Exception
/* 1021:     */   {
/* 1022:1704 */     for (int j = 1; j < this.m_Ls.size(); j++)
/* 1023:     */     {
/* 1024:1705 */       ArrayList<Object> currentItemSets = (ArrayList)this.m_Ls.get(j);
/* 1025:1706 */       Enumeration<Object> enumItemSets = new WekaEnumeration(currentItemSets);
/* 1026:1708 */       while (enumItemSets.hasMoreElements())
/* 1027:     */       {
/* 1028:1709 */         AprioriItemSet currentItemSet = (AprioriItemSet)enumItemSets.nextElement();
/* 1029:     */         
/* 1030:     */ 
/* 1031:     */ 
/* 1032:1713 */         ArrayList<Object>[] rules = currentItemSet.generateRulesBruteForce(this.m_minMetric, this.m_metricType, this.m_hashtables, j + 1, this.m_instances.numInstances(), this.m_significanceLevel);
/* 1033:1716 */         for (int k = 0; k < rules[0].size(); k++)
/* 1034:     */         {
/* 1035:1717 */           this.m_allTheRules[0].add(rules[0].get(k));
/* 1036:1718 */           this.m_allTheRules[1].add(rules[1].get(k));
/* 1037:1719 */           this.m_allTheRules[2].add(rules[2].get(k));
/* 1038:     */           
/* 1039:1721 */           this.m_allTheRules[3].add(rules[3].get(k));
/* 1040:1722 */           this.m_allTheRules[4].add(rules[4].get(k));
/* 1041:1723 */           this.m_allTheRules[5].add(rules[5].get(k));
/* 1042:     */         }
/* 1043:     */       }
/* 1044:     */     }
/* 1045:     */   }
/* 1046:     */   
/* 1047:     */   private void findRulesQuickly()
/* 1048:     */     throws Exception
/* 1049:     */   {
/* 1050:1738 */     for (int j = 1; j < this.m_Ls.size(); j++)
/* 1051:     */     {
/* 1052:1739 */       ArrayList<Object> currentItemSets = (ArrayList)this.m_Ls.get(j);
/* 1053:1740 */       Enumeration<Object> enumItemSets = new WekaEnumeration(currentItemSets);
/* 1054:1742 */       while (enumItemSets.hasMoreElements())
/* 1055:     */       {
/* 1056:1743 */         AprioriItemSet currentItemSet = (AprioriItemSet)enumItemSets.nextElement();
/* 1057:     */         
/* 1058:     */ 
/* 1059:     */ 
/* 1060:1747 */         ArrayList<Object>[] rules = currentItemSet.generateRules(this.m_minMetric, this.m_hashtables, j + 1);
/* 1061:1748 */         for (int k = 0; k < rules[0].size(); k++)
/* 1062:     */         {
/* 1063:1749 */           this.m_allTheRules[0].add(rules[0].get(k));
/* 1064:1750 */           this.m_allTheRules[1].add(rules[1].get(k));
/* 1065:1751 */           this.m_allTheRules[2].add(rules[2].get(k));
/* 1066:1753 */           if (rules.length > 3)
/* 1067:     */           {
/* 1068:1754 */             this.m_allTheRules[3].add(rules[3].get(k));
/* 1069:1755 */             this.m_allTheRules[4].add(rules[4].get(k));
/* 1070:1756 */             this.m_allTheRules[5].add(rules[5].get(k));
/* 1071:     */           }
/* 1072:     */         }
/* 1073:     */       }
/* 1074:     */     }
/* 1075:     */   }
/* 1076:     */   
/* 1077:     */   private void findLargeCarItemSets()
/* 1078:     */     throws Exception
/* 1079:     */   {
/* 1080:1774 */     int i = 0;
/* 1081:     */     
/* 1082:     */ 
/* 1083:     */ 
/* 1084:     */ 
/* 1085:1779 */     double nextMinSupport = this.m_minSupport * this.m_instances.numInstances();
/* 1086:1780 */     double nextMaxSupport = this.m_upperBoundMinSupport * this.m_instances.numInstances();
/* 1087:     */     int necSupport;
/* 1088:     */     int necSupport;
/* 1089:1781 */     if (Math.rint(nextMinSupport) == nextMinSupport) {
/* 1090:1782 */       necSupport = (int)nextMinSupport;
/* 1091:     */     } else {
/* 1092:1784 */       necSupport = Math.round((float)(nextMinSupport + 0.5D));
/* 1093:     */     }
/* 1094:1786 */     if (Math.rint(nextMaxSupport) != nextMaxSupport) {
/* 1095:1788 */       Math.round((float)(nextMaxSupport + 0.5D));
/* 1096:     */     }
/* 1097:1792 */     ArrayList<Object> kSets = LabeledItemSet.singletons(this.m_instances, this.m_onlyClass);
/* 1098:1793 */     LabeledItemSet.upDateCounters(kSets, this.m_instances, this.m_onlyClass);
/* 1099:     */     
/* 1100:     */ 
/* 1101:1796 */     kSets = LabeledItemSet.deleteItemSets(kSets, necSupport, this.m_instances.numInstances());
/* 1102:1798 */     if (kSets.size() == 0) {
/* 1103:     */       return;
/* 1104:     */     }
/* 1105:     */     do
/* 1106:     */     {
/* 1107:1802 */       this.m_Ls.add(kSets);
/* 1108:1803 */       ArrayList<Object> kMinusOneSets = kSets;
/* 1109:1804 */       kSets = LabeledItemSet.mergeAllItemSets(kMinusOneSets, i, this.m_instances.numInstances());
/* 1110:     */       
/* 1111:1806 */       Hashtable<ItemSet, Integer> hashtable = LabeledItemSet.getHashtable(kMinusOneSets, kMinusOneSets.size());
/* 1112:     */       
/* 1113:1808 */       kSets = LabeledItemSet.pruneItemSets(kSets, hashtable);
/* 1114:1809 */       LabeledItemSet.upDateCounters(kSets, this.m_instances, this.m_onlyClass);
/* 1115:1810 */       kSets = LabeledItemSet.deleteItemSets(kSets, necSupport, this.m_instances.numInstances());
/* 1116:     */       
/* 1117:1812 */       i++;
/* 1118:1813 */     } while (kSets.size() > 0);
/* 1119:     */   }
/* 1120:     */   
/* 1121:     */   private void findCarRulesQuickly()
/* 1122:     */     throws Exception
/* 1123:     */   {
/* 1124:1826 */     for (int j = 0; j < this.m_Ls.size(); j++)
/* 1125:     */     {
/* 1126:1827 */       ArrayList<Object> currentLabeledItemSets = (ArrayList)this.m_Ls.get(j);
/* 1127:1828 */       Enumeration<Object> enumLabeledItemSets = new WekaEnumeration(currentLabeledItemSets);
/* 1128:1830 */       while (enumLabeledItemSets.hasMoreElements())
/* 1129:     */       {
/* 1130:1831 */         LabeledItemSet currentLabeledItemSet = (LabeledItemSet)enumLabeledItemSets.nextElement();
/* 1131:     */         
/* 1132:1833 */         ArrayList<Object>[] rules = currentLabeledItemSet.generateRules(this.m_minMetric, false);
/* 1133:1834 */         for (int k = 0; k < rules[0].size(); k++)
/* 1134:     */         {
/* 1135:1835 */           this.m_allTheRules[0].add(rules[0].get(k));
/* 1136:1836 */           this.m_allTheRules[1].add(rules[1].get(k));
/* 1137:1837 */           this.m_allTheRules[2].add(rules[2].get(k));
/* 1138:     */         }
/* 1139:     */       }
/* 1140:     */     }
/* 1141:     */   }
/* 1142:     */   
/* 1143:     */   public ArrayList<Object>[] getAllTheRules()
/* 1144:     */   {
/* 1145:1850 */     return this.m_allTheRules;
/* 1146:     */   }
/* 1147:     */   
/* 1148:     */   public AssociationRules getAssociationRules()
/* 1149:     */   {
/* 1150:1855 */     List<AssociationRule> rules = new ArrayList();
/* 1151:1857 */     if ((this.m_allTheRules != null) && (this.m_allTheRules.length > 3)) {
/* 1152:1858 */       for (int i = 0; i < this.m_allTheRules[0].size(); i++)
/* 1153:     */       {
/* 1154:1860 */         List<Item> premise = new ArrayList();
/* 1155:1861 */         List<Item> consequence = new ArrayList();
/* 1156:     */         
/* 1157:1863 */         AprioriItemSet premiseSet = (AprioriItemSet)this.m_allTheRules[0].get(i);
/* 1158:1864 */         AprioriItemSet consequenceSet = (AprioriItemSet)this.m_allTheRules[1].get(i);
/* 1159:1866 */         for (int j = 0; j < this.m_instances.numAttributes(); j++)
/* 1160:     */         {
/* 1161:1867 */           if (premiseSet.m_items[j] != -1) {
/* 1162:     */             try
/* 1163:     */             {
/* 1164:1869 */               Item newItem = new NominalItem(this.m_instances.attribute(j), premiseSet.m_items[j]);
/* 1165:     */               
/* 1166:1871 */               premise.add(newItem);
/* 1167:     */             }
/* 1168:     */             catch (Exception ex)
/* 1169:     */             {
/* 1170:1873 */               ex.printStackTrace();
/* 1171:     */             }
/* 1172:     */           }
/* 1173:1877 */           if (consequenceSet.m_items[j] != -1) {
/* 1174:     */             try
/* 1175:     */             {
/* 1176:1879 */               Item newItem = new NominalItem(this.m_instances.attribute(j), consequenceSet.m_items[j]);
/* 1177:     */               
/* 1178:1881 */               consequence.add(newItem);
/* 1179:     */             }
/* 1180:     */             catch (Exception ex)
/* 1181:     */             {
/* 1182:1883 */               ex.printStackTrace();
/* 1183:     */             }
/* 1184:     */           }
/* 1185:     */         }
/* 1186:1889 */         int totalTrans = premiseSet.m_totalTransactions;
/* 1187:1890 */         int totalSupport = consequenceSet.m_counter;
/* 1188:1891 */         int premiseSupport = premiseSet.m_counter;
/* 1189:1892 */         int consequenceSupport = consequenceSet.m_secondaryCounter;
/* 1190:     */         
/* 1191:     */ 
/* 1192:1895 */         DefaultAssociationRule.METRIC_TYPE metric = null;
/* 1193:1896 */         switch (this.m_metricType)
/* 1194:     */         {
/* 1195:     */         case 0: 
/* 1196:1898 */           metric = DefaultAssociationRule.METRIC_TYPE.CONFIDENCE;
/* 1197:1899 */           break;
/* 1198:     */         case 1: 
/* 1199:1901 */           metric = DefaultAssociationRule.METRIC_TYPE.LIFT;
/* 1200:1902 */           break;
/* 1201:     */         case 2: 
/* 1202:1904 */           metric = DefaultAssociationRule.METRIC_TYPE.LEVERAGE;
/* 1203:1905 */           break;
/* 1204:     */         case 3: 
/* 1205:1907 */           metric = DefaultAssociationRule.METRIC_TYPE.CONVICTION;
/* 1206:     */         }
/* 1207:1911 */         DefaultAssociationRule newRule = new DefaultAssociationRule(premise, consequence, metric, premiseSupport, consequenceSupport, totalSupport, totalTrans);
/* 1208:     */         
/* 1209:     */ 
/* 1210:     */ 
/* 1211:1915 */         rules.add(newRule);
/* 1212:     */       }
/* 1213:     */     }
/* 1214:1919 */     return new AssociationRules(rules, this);
/* 1215:     */   }
/* 1216:     */   
/* 1217:     */   public String[] getRuleMetricNames()
/* 1218:     */   {
/* 1219:1932 */     String[] metricNames = new String[DefaultAssociationRule.TAGS_SELECTION.length];
/* 1220:1934 */     for (int i = 0; i < DefaultAssociationRule.TAGS_SELECTION.length; i++) {
/* 1221:1935 */       metricNames[i] = DefaultAssociationRule.TAGS_SELECTION[i].getReadable();
/* 1222:     */     }
/* 1223:1938 */     return metricNames;
/* 1224:     */   }
/* 1225:     */   
/* 1226:     */   public boolean canProduceRules()
/* 1227:     */   {
/* 1228:1954 */     return true;
/* 1229:     */   }
/* 1230:     */   
/* 1231:     */   public String getRevision()
/* 1232:     */   {
/* 1233:1964 */     return RevisionUtils.extract("$Revision: 12014 $");
/* 1234:     */   }
/* 1235:     */   
/* 1236:     */   public static void main(String[] args)
/* 1237:     */   {
/* 1238:1973 */     runAssociator(new Apriori(), args);
/* 1239:     */   }
/* 1240:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.Apriori
 * JD-Core Version:    0.7.0.1
 */