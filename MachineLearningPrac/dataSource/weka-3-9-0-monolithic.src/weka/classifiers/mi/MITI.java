/*    1:     */ package weka.classifiers.mi;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.ArrayList;
/*    6:     */ import java.util.Collections;
/*    7:     */ import java.util.Comparator;
/*    8:     */ import java.util.Enumeration;
/*    9:     */ import java.util.HashMap;
/*   10:     */ import java.util.Random;
/*   11:     */ import java.util.Vector;
/*   12:     */ import weka.classifiers.RandomizableClassifier;
/*   13:     */ import weka.classifiers.mi.miti.AlgorithmConfiguration;
/*   14:     */ import weka.classifiers.mi.miti.Bag;
/*   15:     */ import weka.classifiers.mi.miti.NextSplitHeuristic;
/*   16:     */ import weka.classifiers.mi.miti.Split;
/*   17:     */ import weka.classifiers.mi.miti.TreeNode;
/*   18:     */ import weka.core.AdditionalMeasureProducer;
/*   19:     */ import weka.core.Attribute;
/*   20:     */ import weka.core.Capabilities;
/*   21:     */ import weka.core.Capabilities.Capability;
/*   22:     */ import weka.core.Instance;
/*   23:     */ import weka.core.Instances;
/*   24:     */ import weka.core.MultiInstanceCapabilitiesHandler;
/*   25:     */ import weka.core.Option;
/*   26:     */ import weka.core.OptionHandler;
/*   27:     */ import weka.core.SelectedTag;
/*   28:     */ import weka.core.Tag;
/*   29:     */ import weka.core.TechnicalInformation;
/*   30:     */ import weka.core.TechnicalInformation.Field;
/*   31:     */ import weka.core.TechnicalInformation.Type;
/*   32:     */ import weka.core.TechnicalInformationHandler;
/*   33:     */ import weka.core.Utils;
/*   34:     */ 
/*   35:     */ public class MITI
/*   36:     */   extends RandomizableClassifier
/*   37:     */   implements OptionHandler, AdditionalMeasureProducer, TechnicalInformationHandler, MultiInstanceCapabilitiesHandler
/*   38:     */ {
/*   39:     */   static final long serialVersionUID = -217735168397644244L;
/*   40:     */   protected MultiInstanceDecisionTree tree;
/*   41:     */   public static final int SPLITMETHOD_GINI = 1;
/*   42:     */   public static final int SPLITMETHOD_MAXBEPP = 2;
/*   43:     */   public static final int SPLITMETHOD_SSBEPP = 3;
/*   44: 174 */   public static final Tag[] TAGS_SPLITMETHOD = { new Tag(1, "Gini: E * (1 - E)"), new Tag(2, "MaxBEPP: E"), new Tag(3, "Sum Squared BEPP: E * E") };
/*   45:     */   protected int m_SplitMethod;
/*   46:     */   protected boolean m_scaleK;
/*   47:     */   protected boolean m_useBagCount;
/*   48:     */   protected boolean m_unbiasedEstimate;
/*   49:     */   protected int m_kBEPPConstant;
/*   50:     */   protected int m_AttributesToSplit;
/*   51:     */   protected int m_AttributeSplitChoices;
/*   52:     */   protected double m_bagInstanceMultiplier;
/*   53:     */   
/*   54:     */   public MITI()
/*   55:     */   {
/*   56: 180 */     this.m_SplitMethod = 2;
/*   57:     */     
/*   58:     */ 
/*   59: 183 */     this.m_scaleK = false;
/*   60:     */     
/*   61:     */ 
/*   62: 186 */     this.m_useBagCount = false;
/*   63:     */     
/*   64:     */ 
/*   65: 189 */     this.m_unbiasedEstimate = false;
/*   66:     */     
/*   67:     */ 
/*   68: 192 */     this.m_kBEPPConstant = 5;
/*   69:     */     
/*   70:     */ 
/*   71: 195 */     this.m_AttributesToSplit = -1;
/*   72:     */     
/*   73:     */ 
/*   74: 198 */     this.m_AttributeSplitChoices = 1;
/*   75:     */     
/*   76:     */ 
/*   77:     */ 
/*   78: 202 */     this.m_bagInstanceMultiplier = 0.5D;
/*   79:     */   }
/*   80:     */   
/*   81:     */   public String globalInfo()
/*   82:     */   {
/*   83: 212 */     return "MITI (Multi Instance Tree Inducer): multi-instance classification  based a decision tree learned using Blockeel et al.'s algorithm. For more information, see\n\n" + getTechnicalInformation().toString();
/*   84:     */   }
/*   85:     */   
/*   86:     */   public TechnicalInformation getTechnicalInformation()
/*   87:     */   {
/*   88: 230 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*   89: 231 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Hendrik Blockeel and David Page and Ashwin Srinivasan");
/*   90:     */     
/*   91: 233 */     result.setValue(TechnicalInformation.Field.TITLE, "Multi-instance Tree Learning");
/*   92: 234 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Proceedings of the International Conference on Machine Learning");
/*   93:     */     
/*   94: 236 */     result.setValue(TechnicalInformation.Field.YEAR, "2005");
/*   95: 237 */     result.setValue(TechnicalInformation.Field.PAGES, "57-64");
/*   96: 238 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "ACM");
/*   97:     */     
/*   98: 240 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.INPROCEEDINGS);
/*   99: 241 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Luke Bjerring and Eibe Frank");
/*  100: 242 */     additional.setValue(TechnicalInformation.Field.TITLE, "Beyond Trees: Adopting MITI to Learn Rules and Ensemble Classifiers for Multi-instance Data");
/*  101:     */     
/*  102:     */ 
/*  103:     */ 
/*  104: 246 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "Proceedings of the Australasian Joint Conference on Artificial Intelligence");
/*  105:     */     
/*  106:     */ 
/*  107: 249 */     additional.setValue(TechnicalInformation.Field.YEAR, "2011");
/*  108: 250 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*  109:     */     
/*  110: 252 */     return result;
/*  111:     */   }
/*  112:     */   
/*  113:     */   public Capabilities getCapabilities()
/*  114:     */   {
/*  115: 260 */     Capabilities result = super.getCapabilities();
/*  116:     */     
/*  117:     */ 
/*  118: 263 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  119: 264 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/*  120: 265 */     result.disable(Capabilities.Capability.MISSING_VALUES);
/*  121:     */     
/*  122:     */ 
/*  123: 268 */     result.disableAllClasses();
/*  124: 269 */     result.disableAllClassDependencies();
/*  125: 270 */     result.enable(Capabilities.Capability.BINARY_CLASS);
/*  126:     */     
/*  127:     */ 
/*  128: 273 */     result.enable(Capabilities.Capability.ONLY_MULTIINSTANCE);
/*  129:     */     
/*  130: 275 */     return result;
/*  131:     */   }
/*  132:     */   
/*  133:     */   public Capabilities getMultiInstanceCapabilities()
/*  134:     */   {
/*  135: 287 */     Capabilities result = super.getCapabilities();
/*  136:     */     
/*  137:     */ 
/*  138: 290 */     result.disableAllClasses();
/*  139: 291 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  140:     */     
/*  141: 293 */     return result;
/*  142:     */   }
/*  143:     */   
/*  144:     */   public void buildClassifier(Instances trainingData)
/*  145:     */     throws Exception
/*  146:     */   {
/*  147: 302 */     getCapabilities().testWithFail(trainingData);
/*  148:     */     
/*  149: 304 */     this.tree = new MultiInstanceDecisionTree(trainingData);
/*  150:     */   }
/*  151:     */   
/*  152:     */   public Enumeration<String> enumerateMeasures()
/*  153:     */   {
/*  154: 315 */     Vector<String> newVector = new Vector(3);
/*  155: 316 */     newVector.addElement("measureNumRules");
/*  156: 317 */     newVector.addElement("measureNumPositiveRules");
/*  157: 318 */     newVector.addElement("measureNumConditionsInPositiveRules");
/*  158: 319 */     return newVector.elements();
/*  159:     */   }
/*  160:     */   
/*  161:     */   public double getMeasure(String additionalMeasureName)
/*  162:     */   {
/*  163: 332 */     if (additionalMeasureName.equalsIgnoreCase("measureNumRules")) {
/*  164: 333 */       return this.tree.getNumLeaves();
/*  165:     */     }
/*  166: 335 */     if (additionalMeasureName.equalsIgnoreCase("measureNumPositiveRules")) {
/*  167: 336 */       return this.tree.numPosRulesAndNumPosConditions()[0];
/*  168:     */     }
/*  169: 338 */     if (additionalMeasureName.equalsIgnoreCase("measureNumConditionsInPositiveRules")) {
/*  170: 340 */       return this.tree.numPosRulesAndNumPosConditions()[1];
/*  171:     */     }
/*  172: 342 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (MultiInstanceRuleLearner)");
/*  173:     */   }
/*  174:     */   
/*  175:     */   public double[] distributionForInstance(Instance newBag)
/*  176:     */     throws Exception
/*  177:     */   {
/*  178: 353 */     double[] distribution = new double[2];
/*  179: 354 */     Instances contents = newBag.relationalValue(1);
/*  180: 355 */     boolean positive = false;
/*  181: 356 */     for (Instance i : contents) {
/*  182: 357 */       if (this.tree.isPositive(i))
/*  183:     */       {
/*  184: 358 */         positive = true;
/*  185: 359 */         break;
/*  186:     */       }
/*  187:     */     }
/*  188: 363 */     distribution[1] = (positive ? 1.0D : 0.0D);
/*  189: 364 */     distribution[0] = (1.0D - distribution[1]);
/*  190:     */     
/*  191: 366 */     return distribution;
/*  192:     */   }
/*  193:     */   
/*  194:     */   protected class MultiInstanceDecisionTree
/*  195:     */     implements Serializable
/*  196:     */   {
/*  197:     */     private static final long serialVersionUID = 4037700809781784985L;
/*  198:     */     private TreeNode root;
/*  199:     */     private final HashMap<Instance, Bag> m_instanceBags;
/*  200: 384 */     private int numLeaves = 0;
/*  201:     */     
/*  202:     */     public int getNumLeaves()
/*  203:     */     {
/*  204: 388 */       return this.numLeaves;
/*  205:     */     }
/*  206:     */     
/*  207:     */     protected MultiInstanceDecisionTree(Instances instances)
/*  208:     */     {
/*  209: 396 */       this.m_instanceBags = new HashMap();
/*  210: 397 */       ArrayList<Instance> all = new ArrayList();
/*  211: 398 */       double totalInstances = 0.0D;
/*  212: 399 */       double totalBags = 0.0D;
/*  213: 400 */       for (Instance i : instances)
/*  214:     */       {
/*  215: 401 */         Bag bag = new Bag(i);
/*  216: 402 */         for (Instance bagged : bag.instances())
/*  217:     */         {
/*  218: 403 */           this.m_instanceBags.put(bagged, bag);
/*  219: 404 */           all.add(bagged);
/*  220:     */         }
/*  221: 406 */         totalBags += 1.0D;
/*  222: 407 */         totalInstances += bag.instances().numInstances();
/*  223:     */       }
/*  224: 410 */       double b_multiplier = totalInstances / totalBags;
/*  225: 411 */       if (MITI.this.m_scaleK) {
/*  226: 412 */         for (Bag bag : this.m_instanceBags.values()) {
/*  227: 413 */           bag.setBagWeightMultiplier(b_multiplier);
/*  228:     */         }
/*  229:     */       }
/*  230: 417 */       makeTree(this.m_instanceBags, all, false);
/*  231:     */     }
/*  232:     */     
/*  233:     */     public MultiInstanceDecisionTree(ArrayList<Instance> instanceBags, boolean all)
/*  234:     */     {
/*  235: 426 */       this.m_instanceBags = instanceBags;
/*  236: 427 */       makeTree(instanceBags, all, stopOnFirstPositiveLeaf);
/*  237:     */     }
/*  238:     */     
/*  239:     */     private void makeTree(HashMap<Instance, Bag> instanceBags, ArrayList<Instance> all, boolean stopOnFirstPositiveLeaf)
/*  240:     */     {
/*  241: 436 */       Random r = new Random(MITI.this.getSeed());
/*  242:     */       
/*  243: 438 */       AlgorithmConfiguration settings = MITI.this.getSettings();
/*  244:     */       
/*  245: 440 */       ArrayList<TreeNode> toSplit = new ArrayList();
/*  246:     */       
/*  247: 442 */       this.root = new TreeNode(null, all);
/*  248: 443 */       toSplit.add(this.root);
/*  249: 444 */       this.numLeaves = 0;
/*  250: 446 */       while (toSplit.size() > 0)
/*  251:     */       {
/*  252: 451 */         int nextIndex = Math.min(1, toSplit.size());
/*  253: 452 */         nextIndex = r.nextInt(nextIndex);
/*  254:     */         
/*  255: 454 */         TreeNode next = (TreeNode)toSplit.remove(nextIndex);
/*  256: 455 */         if (next != null)
/*  257:     */         {
/*  258: 459 */           if (next.isPurePositive(instanceBags))
/*  259:     */           {
/*  260: 460 */             next.makeLeafNode(true);
/*  261: 461 */             ArrayList<String> deactivated = new ArrayList();
/*  262: 462 */             next.deactivateRelatedInstances(instanceBags, deactivated);
/*  263: 464 */             if ((MITI.this.m_Debug) && (deactivated.size() > 0)) {
/*  264: 465 */               Bag.printDeactivatedInstances(deactivated);
/*  265:     */             }
/*  266: 470 */             for (TreeNode n : toSplit)
/*  267:     */             {
/*  268: 471 */               n.removeDeactivatedInstances(instanceBags);
/*  269: 472 */               n.calculateNodeScore(instanceBags, MITI.this.m_unbiasedEstimate, MITI.this.m_kBEPPConstant, MITI.this.m_useBagCount, MITI.this.m_bagInstanceMultiplier);
/*  270:     */             }
/*  271: 476 */             if ((stopOnFirstPositiveLeaf) && (deactivated.size() > 0)) {
/*  272: 477 */               return;
/*  273:     */             }
/*  274:     */           }
/*  275: 480 */           else if (next.isPureNegative(instanceBags))
/*  276:     */           {
/*  277: 481 */             next.makeLeafNode(false);
/*  278:     */           }
/*  279:     */           else
/*  280:     */           {
/*  281: 483 */             next.splitInstances(instanceBags, settings, r, MITI.this.m_Debug);
/*  282: 484 */             if (!next.isLeafNode())
/*  283:     */             {
/*  284: 485 */               if (next.split.isNominal)
/*  285:     */               {
/*  286: 486 */                 TreeNode[] nominals = next.nominals();
/*  287: 487 */                 for (TreeNode nominal : nominals)
/*  288:     */                 {
/*  289: 488 */                   nominal.calculateNodeScore(instanceBags, MITI.this.m_unbiasedEstimate, MITI.this.m_kBEPPConstant, MITI.this.m_useBagCount, MITI.this.m_bagInstanceMultiplier);
/*  290:     */                   
/*  291: 490 */                   toSplit.add(nominal);
/*  292:     */                 }
/*  293:     */               }
/*  294:     */               else
/*  295:     */               {
/*  296: 493 */                 next.left().calculateNodeScore(instanceBags, MITI.this.m_unbiasedEstimate, MITI.this.m_kBEPPConstant, MITI.this.m_useBagCount, MITI.this.m_bagInstanceMultiplier);
/*  297:     */                 
/*  298: 495 */                 toSplit.add(next.left());
/*  299: 496 */                 next.right().calculateNodeScore(instanceBags, MITI.this.m_unbiasedEstimate, MITI.this.m_kBEPPConstant, MITI.this.m_useBagCount, MITI.this.m_bagInstanceMultiplier);
/*  300:     */                 
/*  301: 498 */                 toSplit.add(next.right());
/*  302:     */               }
/*  303:     */             }
/*  304: 503 */             else if (next.isPositiveLeaf())
/*  305:     */             {
/*  306: 504 */               for (TreeNode n : toSplit)
/*  307:     */               {
/*  308: 505 */                 n.removeDeactivatedInstances(instanceBags);
/*  309: 506 */                 n.calculateNodeScore(instanceBags, MITI.this.m_unbiasedEstimate, MITI.this.m_kBEPPConstant, MITI.this.m_useBagCount, MITI.this.m_bagInstanceMultiplier);
/*  310:     */               }
/*  311: 510 */               if (stopOnFirstPositiveLeaf) {
/*  312: 511 */                 return;
/*  313:     */               }
/*  314:     */             }
/*  315:     */           }
/*  316: 518 */           if (next.isLeafNode()) {
/*  317: 519 */             this.numLeaves += 1;
/*  318:     */           }
/*  319: 524 */           Comparator<TreeNode> sh = Collections.reverseOrder(new NextSplitHeuristic());
/*  320:     */           
/*  321: 526 */           Collections.sort(toSplit, sh);
/*  322:     */         }
/*  323:     */       }
/*  324: 529 */       if (MITI.this.m_Debug) {
/*  325: 530 */         System.out.println(this.root.render(1, instanceBags));
/*  326:     */       }
/*  327:     */     }
/*  328:     */     
/*  329:     */     protected boolean isPositive(Instance i)
/*  330:     */     {
/*  331: 538 */       TreeNode leaf = traverseTree(i);
/*  332: 539 */       return (leaf != null) && (leaf.isPositiveLeaf());
/*  333:     */     }
/*  334:     */     
/*  335:     */     private TreeNode traverseTree(Instance i)
/*  336:     */     {
/*  337: 546 */       TreeNode next = this.root;
/*  338: 547 */       while ((next != null) && (!next.isLeafNode()))
/*  339:     */       {
/*  340: 548 */         Attribute a = next.split.attribute;
/*  341: 549 */         if (a.isNominal()) {
/*  342: 550 */           next = next.nominals()[((int)i.value(a))];
/*  343: 552 */         } else if (i.value(a) < next.split.splitPoint) {
/*  344: 553 */           next = next.left();
/*  345:     */         } else {
/*  346: 555 */           next = next.right();
/*  347:     */         }
/*  348:     */       }
/*  349: 559 */       return next;
/*  350:     */     }
/*  351:     */     
/*  352:     */     public String render()
/*  353:     */     {
/*  354: 566 */       return this.root.render(0, this.m_instanceBags);
/*  355:     */     }
/*  356:     */     
/*  357:     */     public boolean trimNegativeBranches()
/*  358:     */     {
/*  359: 573 */       return this.root.trimNegativeBranches();
/*  360:     */     }
/*  361:     */     
/*  362:     */     public int[] numPosRulesAndNumPosConditions()
/*  363:     */     {
/*  364: 582 */       return numPosRulesAndNumPosConditions(this.root);
/*  365:     */     }
/*  366:     */     
/*  367:     */     private int[] numPosRulesAndNumPosConditions(TreeNode next)
/*  368:     */     {
/*  369: 591 */       int[] numPosRulesAndNumPosConditions = new int[2];
/*  370: 592 */       if ((next != null) && (next.isLeafNode()))
/*  371:     */       {
/*  372: 595 */         if (next.isPositiveLeaf()) {
/*  373: 596 */           numPosRulesAndNumPosConditions[0] = 1;
/*  374:     */         }
/*  375:     */       }
/*  376: 598 */       else if (next != null)
/*  377:     */       {
/*  378: 601 */         Attribute a = next.split.attribute;
/*  379: 602 */         int[] fromBelow = null;
/*  380: 603 */         if (a.isNominal())
/*  381:     */         {
/*  382: 604 */           for (TreeNode child : next.nominals())
/*  383:     */           {
/*  384: 605 */             fromBelow = numPosRulesAndNumPosConditions(child);
/*  385:     */             
/*  386:     */ 
/*  387: 608 */             numPosRulesAndNumPosConditions[0] += fromBelow[0];
/*  388:     */             
/*  389:     */ 
/*  390: 611 */             numPosRulesAndNumPosConditions[1] += fromBelow[1] + fromBelow[0];
/*  391:     */           }
/*  392:     */         }
/*  393:     */         else
/*  394:     */         {
/*  395: 614 */           fromBelow = numPosRulesAndNumPosConditions(next.left());
/*  396:     */           
/*  397:     */ 
/*  398: 617 */           numPosRulesAndNumPosConditions[0] += fromBelow[0];
/*  399:     */           
/*  400:     */ 
/*  401: 620 */           numPosRulesAndNumPosConditions[1] += fromBelow[1] + fromBelow[0];
/*  402:     */           
/*  403: 622 */           fromBelow = numPosRulesAndNumPosConditions(next.right());
/*  404:     */           
/*  405:     */ 
/*  406: 625 */           numPosRulesAndNumPosConditions[0] += fromBelow[0];
/*  407:     */           
/*  408:     */ 
/*  409: 628 */           numPosRulesAndNumPosConditions[1] += fromBelow[1] + fromBelow[0];
/*  410:     */         }
/*  411:     */       }
/*  412: 631 */       return numPosRulesAndNumPosConditions;
/*  413:     */     }
/*  414:     */   }
/*  415:     */   
/*  416:     */   protected AlgorithmConfiguration getSettings()
/*  417:     */   {
/*  418: 639 */     return new AlgorithmConfiguration(this.m_SplitMethod, this.m_unbiasedEstimate, this.m_kBEPPConstant, this.m_useBagCount, this.m_bagInstanceMultiplier, this.m_AttributesToSplit, this.m_AttributeSplitChoices);
/*  419:     */   }
/*  420:     */   
/*  421:     */   public Enumeration<Option> listOptions()
/*  422:     */   {
/*  423: 713 */     Vector<Option> result = new Vector();
/*  424:     */     
/*  425: 715 */     result.addElement(new Option("\tThe method used to determine best split:\n\t1. Gini; 2. MaxBEPP; 3. SSBEPP", "M", 1, "-M [1|2|3]"));
/*  426:     */     
/*  427:     */ 
/*  428: 718 */     result.addElement(new Option("\tThe constant used in the tozero() hueristic", "K", 1, "-K [kBEPPConstant]"));
/*  429:     */     
/*  430:     */ 
/*  431:     */ 
/*  432: 722 */     result.addElement(new Option("\tScales the value of K to the size of the bags", "L", 0, "-L"));
/*  433:     */     
/*  434:     */ 
/*  435: 725 */     result.addElement(new Option("\tUse unbiased estimate rather than BEPP, i.e. UEPP.", "U", 0, "-U"));
/*  436:     */     
/*  437:     */ 
/*  438: 728 */     result.addElement(new Option("\tUses the instances present for the bag counts at each node when splitting,\n\tweighted according to 1 - Ba ^ n, where n is the number of instances\n\tpresent which belong to the bag, and Ba is another parameter (default 0.5)", "B", 0, "-B"));
/*  439:     */     
/*  440:     */ 
/*  441:     */ 
/*  442:     */ 
/*  443:     */ 
/*  444:     */ 
/*  445: 735 */     result.addElement(new Option("\tMultiplier for count influence of a bag based on the number of its instances", "Ba", 1, "-Ba [multiplier]"));
/*  446:     */     
/*  447:     */ 
/*  448:     */ 
/*  449:     */ 
/*  450: 740 */     result.addElement(new Option("\tThe number of randomly selected attributes to split\n\t-1: All attributes\n\t-2: square root of the total number of attributes", "A", 1, "-A [number of attributes]"));
/*  451:     */     
/*  452:     */ 
/*  453:     */ 
/*  454:     */ 
/*  455: 745 */     result.addElement(new Option("\tThe number of top scoring attribute splits to randomly pick from\n\t-1: All splits (completely random selection)\n\t-2: square root of the number of splits", "An", 1, "-An [number of splits]"));
/*  456:     */     
/*  457:     */ 
/*  458:     */ 
/*  459:     */ 
/*  460: 750 */     result.addAll(Collections.list(super.listOptions()));
/*  461:     */     
/*  462: 752 */     return result.elements();
/*  463:     */   }
/*  464:     */   
/*  465:     */   public void setOptions(String[] options)
/*  466:     */     throws Exception
/*  467:     */   {
/*  468: 763 */     String methodString = Utils.getOption('M', options);
/*  469: 764 */     if (methodString.length() != 0) {
/*  470: 765 */       setSplitMethod(new SelectedTag(Integer.parseInt(methodString), TAGS_SPLITMETHOD));
/*  471:     */     } else {
/*  472: 768 */       setSplitMethod(new SelectedTag(2, TAGS_SPLITMETHOD));
/*  473:     */     }
/*  474: 771 */     methodString = Utils.getOption('K', options);
/*  475: 772 */     if (methodString.length() != 0) {
/*  476: 773 */       setK(Integer.parseInt(methodString));
/*  477:     */     } else {
/*  478: 775 */       setK(5);
/*  479:     */     }
/*  480: 778 */     setL(Utils.getFlag('L', options));
/*  481:     */     
/*  482: 780 */     setUnbiasedEstimate(Utils.getFlag('U', options));
/*  483:     */     
/*  484: 782 */     methodString = Utils.getOption('A', options);
/*  485: 783 */     if (methodString.length() != 0) {
/*  486: 784 */       setAttributesToSplit(Integer.parseInt(methodString));
/*  487:     */     } else {
/*  488: 786 */       setAttributesToSplit(-1);
/*  489:     */     }
/*  490: 789 */     methodString = Utils.getOption("An", options);
/*  491: 790 */     if (methodString.length() != 0) {
/*  492: 791 */       setTopNAttributesToSplit(Integer.parseInt(methodString));
/*  493:     */     } else {
/*  494: 793 */       setTopNAttributesToSplit(1);
/*  495:     */     }
/*  496: 796 */     setB(Utils.getFlag('B', options));
/*  497:     */     
/*  498: 798 */     methodString = Utils.getOption("Ba", options);
/*  499: 799 */     if (methodString.length() != 0) {
/*  500: 800 */       setBa(Double.parseDouble(methodString));
/*  501:     */     } else {
/*  502: 802 */       setBa(0.5D);
/*  503:     */     }
/*  504: 805 */     super.setOptions(options);
/*  505:     */   }
/*  506:     */   
/*  507:     */   public String[] getOptions()
/*  508:     */   {
/*  509: 816 */     Vector<String> result = new Vector();
/*  510:     */     
/*  511: 818 */     result.add("-K");
/*  512: 819 */     result.add("" + this.m_kBEPPConstant);
/*  513: 821 */     if (getL()) {
/*  514: 822 */       result.add("-L");
/*  515:     */     }
/*  516: 825 */     if (getUnbiasedEstimate()) {
/*  517: 826 */       result.add("-U");
/*  518:     */     }
/*  519: 829 */     if (getB()) {
/*  520: 830 */       result.add("-B");
/*  521:     */     }
/*  522: 833 */     result.add("-Ba");
/*  523: 834 */     result.add("" + this.m_bagInstanceMultiplier);
/*  524:     */     
/*  525: 836 */     result.add("-M");
/*  526: 837 */     result.add("" + this.m_SplitMethod);
/*  527:     */     
/*  528: 839 */     result.add("-A");
/*  529: 840 */     result.add("" + this.m_AttributesToSplit);
/*  530:     */     
/*  531: 842 */     result.add("-An");
/*  532: 843 */     result.add("" + this.m_AttributeSplitChoices);
/*  533:     */     
/*  534: 845 */     Collections.addAll(result, super.getOptions());
/*  535:     */     
/*  536: 847 */     return (String[])result.toArray(new String[result.size()]);
/*  537:     */   }
/*  538:     */   
/*  539:     */   public String kTipText()
/*  540:     */   {
/*  541: 854 */     return "The value used in the tozero() method.";
/*  542:     */   }
/*  543:     */   
/*  544:     */   public int getK()
/*  545:     */   {
/*  546: 861 */     return this.m_kBEPPConstant;
/*  547:     */   }
/*  548:     */   
/*  549:     */   public void setK(int newValue)
/*  550:     */   {
/*  551: 868 */     this.m_kBEPPConstant = newValue;
/*  552:     */   }
/*  553:     */   
/*  554:     */   public String lTipText()
/*  555:     */   {
/*  556: 875 */     return "Whether to scale based on the number of instances.";
/*  557:     */   }
/*  558:     */   
/*  559:     */   public boolean getL()
/*  560:     */   {
/*  561: 882 */     return this.m_scaleK;
/*  562:     */   }
/*  563:     */   
/*  564:     */   public void setL(boolean newValue)
/*  565:     */   {
/*  566: 889 */     this.m_scaleK = newValue;
/*  567:     */   }
/*  568:     */   
/*  569:     */   public String unbiasedEstimateTipText()
/*  570:     */   {
/*  571: 896 */     return "Whether to used unbiased estimate (EPP instead of BEPP).";
/*  572:     */   }
/*  573:     */   
/*  574:     */   public boolean getUnbiasedEstimate()
/*  575:     */   {
/*  576: 903 */     return this.m_unbiasedEstimate;
/*  577:     */   }
/*  578:     */   
/*  579:     */   public void setUnbiasedEstimate(boolean newValue)
/*  580:     */   {
/*  581: 910 */     this.m_unbiasedEstimate = newValue;
/*  582:     */   }
/*  583:     */   
/*  584:     */   public String bTipText()
/*  585:     */   {
/*  586: 917 */     return "Whether to use bag-based statistics for estimates of proportion.";
/*  587:     */   }
/*  588:     */   
/*  589:     */   public boolean getB()
/*  590:     */   {
/*  591: 924 */     return this.m_useBagCount;
/*  592:     */   }
/*  593:     */   
/*  594:     */   public void setB(boolean newValue)
/*  595:     */   {
/*  596: 931 */     this.m_useBagCount = newValue;
/*  597:     */   }
/*  598:     */   
/*  599:     */   public String baTipText()
/*  600:     */   {
/*  601: 938 */     return "Multiplier for count influence of a bag based on the number of its instances.";
/*  602:     */   }
/*  603:     */   
/*  604:     */   public double getBa()
/*  605:     */   {
/*  606: 945 */     return this.m_bagInstanceMultiplier;
/*  607:     */   }
/*  608:     */   
/*  609:     */   public void setBa(double newValue)
/*  610:     */   {
/*  611: 952 */     this.m_bagInstanceMultiplier = newValue;
/*  612:     */   }
/*  613:     */   
/*  614:     */   public String attributesToSplitTipText()
/*  615:     */   {
/*  616: 959 */     return "The number of randomly chosen attributes to consider for splitting.";
/*  617:     */   }
/*  618:     */   
/*  619:     */   public int getAttributesToSplit()
/*  620:     */   {
/*  621: 966 */     return this.m_AttributesToSplit;
/*  622:     */   }
/*  623:     */   
/*  624:     */   public void setAttributesToSplit(int newValue)
/*  625:     */   {
/*  626: 973 */     this.m_AttributesToSplit = newValue;
/*  627:     */   }
/*  628:     */   
/*  629:     */   public String topNAttributesToSplitTipText()
/*  630:     */   {
/*  631: 980 */     return "Value of N to use for top-N attributes to choose randomly from.";
/*  632:     */   }
/*  633:     */   
/*  634:     */   public int getTopNAttributesToSplit()
/*  635:     */   {
/*  636: 987 */     return this.m_AttributeSplitChoices;
/*  637:     */   }
/*  638:     */   
/*  639:     */   public void setTopNAttributesToSplit(int newValue)
/*  640:     */   {
/*  641: 994 */     this.m_AttributeSplitChoices = newValue;
/*  642:     */   }
/*  643:     */   
/*  644:     */   public String splitMethodTipText()
/*  645:     */   {
/*  646:1001 */     return "The method used to determine best split: 1. Gini; 2. MaxBEPP; 3. SSBEPP";
/*  647:     */   }
/*  648:     */   
/*  649:     */   public void setSplitMethod(SelectedTag newMethod)
/*  650:     */   {
/*  651:1008 */     if (newMethod.getTags() == TAGS_SPLITMETHOD) {
/*  652:1009 */       this.m_SplitMethod = newMethod.getSelectedTag().getID();
/*  653:     */     }
/*  654:     */   }
/*  655:     */   
/*  656:     */   public SelectedTag getSplitMethod()
/*  657:     */   {
/*  658:1017 */     return new SelectedTag(this.m_SplitMethod, TAGS_SPLITMETHOD);
/*  659:     */   }
/*  660:     */   
/*  661:     */   public String toString()
/*  662:     */   {
/*  663:1025 */     if (this.tree != null)
/*  664:     */     {
/*  665:1027 */       String s = this.tree.render();
/*  666:     */       
/*  667:1029 */       s = s + "\n\nNumber of positive rules: " + getMeasure("measureNumPositiveRules") + "\n";
/*  668:     */       
/*  669:1031 */       s = s + "Number of conditions in positive rules: " + getMeasure("measureNumConditionsInPositiveRules") + "\n";
/*  670:     */       
/*  671:     */ 
/*  672:1034 */       return s;
/*  673:     */     }
/*  674:1036 */     return "No model built yet!";
/*  675:     */   }
/*  676:     */   
/*  677:     */   public static void main(String[] options)
/*  678:     */   {
/*  679:1044 */     runClassifier(new MITI(), options);
/*  680:     */   }
/*  681:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.mi.MITI
 * JD-Core Version:    0.7.0.1
 */