/*    1:     */ package weka.associations;
/*    2:     */ 
/*    3:     */ import java.io.File;
/*    4:     */ import java.io.PrintStream;
/*    5:     */ import java.io.Serializable;
/*    6:     */ import java.util.ArrayList;
/*    7:     */ import java.util.Collection;
/*    8:     */ import java.util.Collections;
/*    9:     */ import java.util.Comparator;
/*   10:     */ import java.util.Enumeration;
/*   11:     */ import java.util.HashMap;
/*   12:     */ import java.util.Iterator;
/*   13:     */ import java.util.LinkedList;
/*   14:     */ import java.util.List;
/*   15:     */ import java.util.Map;
/*   16:     */ import java.util.Set;
/*   17:     */ import java.util.Vector;
/*   18:     */ import weka.core.Attribute;
/*   19:     */ import weka.core.Capabilities;
/*   20:     */ import weka.core.Capabilities.Capability;
/*   21:     */ import weka.core.Instance;
/*   22:     */ import weka.core.Instances;
/*   23:     */ import weka.core.Option;
/*   24:     */ import weka.core.OptionHandler;
/*   25:     */ import weka.core.RevisionUtils;
/*   26:     */ import weka.core.SelectedTag;
/*   27:     */ import weka.core.SparseInstance;
/*   28:     */ import weka.core.Tag;
/*   29:     */ import weka.core.TechnicalInformation;
/*   30:     */ import weka.core.TechnicalInformation.Field;
/*   31:     */ import weka.core.TechnicalInformation.Type;
/*   32:     */ import weka.core.TechnicalInformationHandler;
/*   33:     */ import weka.core.Utils;
/*   34:     */ import weka.core.converters.ArffLoader;
/*   35:     */ 
/*   36:     */ public class FPGrowth
/*   37:     */   extends AbstractAssociator
/*   38:     */   implements AssociationRulesProducer, OptionHandler, TechnicalInformationHandler
/*   39:     */ {
/*   40:     */   private static final long serialVersionUID = 3620717108603442911L;
/*   41:     */   
/*   42:     */   protected static class FrequentBinaryItemSet
/*   43:     */     implements Serializable, Cloneable
/*   44:     */   {
/*   45:     */     private static final long serialVersionUID = -6543815873565829448L;
/*   46: 171 */     protected ArrayList<BinaryItem> m_items = new ArrayList();
/*   47:     */     protected int m_support;
/*   48:     */     
/*   49:     */     public FrequentBinaryItemSet(ArrayList<BinaryItem> items, int support)
/*   50:     */     {
/*   51: 183 */       this.m_items = items;
/*   52: 184 */       this.m_support = support;
/*   53: 185 */       Collections.sort(this.m_items);
/*   54:     */     }
/*   55:     */     
/*   56:     */     public void addItem(BinaryItem i)
/*   57:     */     {
/*   58: 194 */       this.m_items.add(i);
/*   59: 195 */       Collections.sort(this.m_items);
/*   60:     */     }
/*   61:     */     
/*   62:     */     public void setSupport(int support)
/*   63:     */     {
/*   64: 204 */       this.m_support = support;
/*   65:     */     }
/*   66:     */     
/*   67:     */     public int getSupport()
/*   68:     */     {
/*   69: 213 */       return this.m_support;
/*   70:     */     }
/*   71:     */     
/*   72:     */     public Collection<BinaryItem> getItems()
/*   73:     */     {
/*   74: 222 */       return this.m_items;
/*   75:     */     }
/*   76:     */     
/*   77:     */     public BinaryItem getItem(int index)
/*   78:     */     {
/*   79: 232 */       return (BinaryItem)this.m_items.get(index);
/*   80:     */     }
/*   81:     */     
/*   82:     */     public int numberOfItems()
/*   83:     */     {
/*   84: 241 */       return this.m_items.size();
/*   85:     */     }
/*   86:     */     
/*   87:     */     public String toString()
/*   88:     */     {
/*   89: 251 */       StringBuffer buff = new StringBuffer();
/*   90: 252 */       Iterator<BinaryItem> i = this.m_items.iterator();
/*   91: 254 */       while (i.hasNext()) {
/*   92: 255 */         buff.append(((BinaryItem)i.next()).toString() + " ");
/*   93:     */       }
/*   94: 257 */       buff.append(": " + this.m_support);
/*   95: 258 */       return buff.toString();
/*   96:     */     }
/*   97:     */     
/*   98:     */     public Object clone()
/*   99:     */     {
/*  100: 268 */       ArrayList<BinaryItem> items = new ArrayList(this.m_items);
/*  101: 269 */       return new FrequentBinaryItemSet(items, this.m_support);
/*  102:     */     }
/*  103:     */   }
/*  104:     */   
/*  105:     */   protected static class FrequentItemSets
/*  106:     */     implements Serializable
/*  107:     */   {
/*  108:     */     private static final long serialVersionUID = 4173606872363973588L;
/*  109: 282 */     protected ArrayList<FPGrowth.FrequentBinaryItemSet> m_sets = new ArrayList();
/*  110:     */     protected int m_numberOfTransactions;
/*  111:     */     
/*  112:     */     public FrequentItemSets(int numTransactions)
/*  113:     */     {
/*  114: 293 */       this.m_numberOfTransactions = numTransactions;
/*  115:     */     }
/*  116:     */     
/*  117:     */     public FPGrowth.FrequentBinaryItemSet getItemSet(int index)
/*  118:     */     {
/*  119: 303 */       return (FPGrowth.FrequentBinaryItemSet)this.m_sets.get(index);
/*  120:     */     }
/*  121:     */     
/*  122:     */     public Iterator<FPGrowth.FrequentBinaryItemSet> iterator()
/*  123:     */     {
/*  124: 312 */       return this.m_sets.iterator();
/*  125:     */     }
/*  126:     */     
/*  127:     */     public int getNumberOfTransactions()
/*  128:     */     {
/*  129: 322 */       return this.m_numberOfTransactions;
/*  130:     */     }
/*  131:     */     
/*  132:     */     public void addItemSet(FPGrowth.FrequentBinaryItemSet setToAdd)
/*  133:     */     {
/*  134: 331 */       this.m_sets.add(setToAdd);
/*  135:     */     }
/*  136:     */     
/*  137:     */     public void sort(Comparator<FPGrowth.FrequentBinaryItemSet> comp)
/*  138:     */     {
/*  139: 340 */       Collections.sort(this.m_sets, comp);
/*  140:     */     }
/*  141:     */     
/*  142:     */     public int size()
/*  143:     */     {
/*  144: 349 */       return this.m_sets.size();
/*  145:     */     }
/*  146:     */     
/*  147:     */     public void sort()
/*  148:     */     {
/*  149: 357 */       Comparator<FPGrowth.FrequentBinaryItemSet> compF = new Comparator()
/*  150:     */       {
/*  151:     */         public int compare(FPGrowth.FrequentBinaryItemSet one, FPGrowth.FrequentBinaryItemSet two)
/*  152:     */         {
/*  153: 360 */           Collection<BinaryItem> compOne = one.getItems();
/*  154: 361 */           Collection<BinaryItem> compTwo = two.getItems();
/*  155: 366 */           if (compOne.size() < compTwo.size()) {
/*  156: 367 */             return -1;
/*  157:     */           }
/*  158: 368 */           if (compOne.size() > compTwo.size()) {
/*  159: 369 */             return 1;
/*  160:     */           }
/*  161: 372 */           Iterator<BinaryItem> twoIterator = compTwo.iterator();
/*  162: 373 */           for (BinaryItem oneI : compOne)
/*  163:     */           {
/*  164: 374 */             BinaryItem twoI = (BinaryItem)twoIterator.next();
/*  165: 375 */             int result = oneI.compareTo(twoI);
/*  166: 376 */             if (result != 0) {
/*  167: 377 */               return result;
/*  168:     */             }
/*  169:     */           }
/*  170: 380 */           return 0;
/*  171:     */         }
/*  172: 392 */       };
/*  173: 393 */       sort(compF);
/*  174:     */     }
/*  175:     */     
/*  176:     */     public String toString(int numSets)
/*  177:     */     {
/*  178: 403 */       if (this.m_sets.size() == 0) {
/*  179: 404 */         return "No frequent items sets found!";
/*  180:     */       }
/*  181: 407 */       StringBuffer result = new StringBuffer();
/*  182: 408 */       result.append("" + this.m_sets.size() + " frequent item sets found");
/*  183: 409 */       if (numSets > 0) {
/*  184: 410 */         result.append(" , displaying " + numSets);
/*  185:     */       }
/*  186: 412 */       result.append(":\n\n");
/*  187:     */       
/*  188: 414 */       int count = 0;
/*  189: 415 */       for (FPGrowth.FrequentBinaryItemSet i : this.m_sets)
/*  190:     */       {
/*  191: 416 */         if ((numSets > 0) && (count > numSets)) {
/*  192:     */           break;
/*  193:     */         }
/*  194: 419 */         result.append(i.toString() + "\n");
/*  195: 420 */         count++;
/*  196:     */       }
/*  197: 423 */       return result.toString();
/*  198:     */     }
/*  199:     */   }
/*  200:     */   
/*  201:     */   protected static class ShadowCounts
/*  202:     */     implements Serializable
/*  203:     */   {
/*  204:     */     private static final long serialVersionUID = 4435433714185969155L;
/*  205: 436 */     private final ArrayList<Integer> m_counts = new ArrayList();
/*  206:     */     
/*  207:     */     public int getCount(int recursionLevel)
/*  208:     */     {
/*  209: 445 */       if (recursionLevel >= this.m_counts.size()) {
/*  210: 446 */         return 0;
/*  211:     */       }
/*  212: 448 */       return ((Integer)this.m_counts.get(recursionLevel)).intValue();
/*  213:     */     }
/*  214:     */     
/*  215:     */     public void increaseCount(int recursionLevel, int incr)
/*  216:     */     {
/*  217: 463 */       if (recursionLevel == this.m_counts.size())
/*  218:     */       {
/*  219: 465 */         this.m_counts.add(Integer.valueOf(incr));
/*  220:     */       }
/*  221: 466 */       else if (recursionLevel == this.m_counts.size() - 1)
/*  222:     */       {
/*  223: 468 */         int n = ((Integer)this.m_counts.get(recursionLevel)).intValue();
/*  224: 469 */         this.m_counts.set(recursionLevel, Integer.valueOf(n + incr));
/*  225:     */       }
/*  226:     */     }
/*  227:     */     
/*  228:     */     public void removeCount(int recursionLevel)
/*  229:     */     {
/*  230: 479 */       if (recursionLevel < this.m_counts.size()) {
/*  231: 480 */         this.m_counts.remove(recursionLevel);
/*  232:     */       }
/*  233:     */     }
/*  234:     */   }
/*  235:     */   
/*  236:     */   protected static class FPTreeNode
/*  237:     */     implements Serializable
/*  238:     */   {
/*  239:     */     private static final long serialVersionUID = 4396315323673737660L;
/*  240:     */     protected FPTreeNode m_levelSibling;
/*  241:     */     protected FPTreeNode m_parent;
/*  242:     */     protected BinaryItem m_item;
/*  243:     */     protected int m_ID;
/*  244: 506 */     protected Map<BinaryItem, FPTreeNode> m_children = new HashMap();
/*  245: 509 */     protected FPGrowth.ShadowCounts m_projectedCounts = new FPGrowth.ShadowCounts();
/*  246:     */     
/*  247:     */     public FPTreeNode(FPTreeNode parent, BinaryItem item)
/*  248:     */     {
/*  249: 518 */       this.m_parent = parent;
/*  250: 519 */       this.m_item = item;
/*  251:     */     }
/*  252:     */     
/*  253:     */     public void addItemSet(Collection<BinaryItem> itemSet, Map<BinaryItem, FPGrowth.FPTreeRoot.Header> headerTable, int incr)
/*  254:     */     {
/*  255: 534 */       Iterator<BinaryItem> i = itemSet.iterator();
/*  256: 536 */       if (i.hasNext())
/*  257:     */       {
/*  258: 537 */         BinaryItem first = (BinaryItem)i.next();
/*  259:     */         FPTreeNode aChild;
/*  260: 540 */         if (!this.m_children.containsKey(first))
/*  261:     */         {
/*  262: 542 */           FPTreeNode aChild = new FPTreeNode(this, first);
/*  263: 543 */           this.m_children.put(first, aChild);
/*  264: 546 */           if (!headerTable.containsKey(first)) {
/*  265: 547 */             headerTable.put(first, new FPGrowth.FPTreeRoot.Header());
/*  266:     */           }
/*  267: 551 */           ((FPGrowth.FPTreeRoot.Header)headerTable.get(first)).addToList(aChild);
/*  268:     */         }
/*  269:     */         else
/*  270:     */         {
/*  271: 554 */           aChild = (FPTreeNode)this.m_children.get(first);
/*  272:     */         }
/*  273: 558 */         ((FPGrowth.FPTreeRoot.Header)headerTable.get(first)).getProjectedCounts().increaseCount(0, incr);
/*  274:     */         
/*  275:     */ 
/*  276: 561 */         aChild.increaseProjectedCount(0, incr);
/*  277:     */         
/*  278:     */ 
/*  279: 564 */         itemSet.remove(first);
/*  280: 565 */         aChild.addItemSet(itemSet, headerTable, incr);
/*  281:     */       }
/*  282:     */     }
/*  283:     */     
/*  284:     */     public void increaseProjectedCount(int recursionLevel, int incr)
/*  285:     */     {
/*  286: 576 */       this.m_projectedCounts.increaseCount(recursionLevel, incr);
/*  287:     */     }
/*  288:     */     
/*  289:     */     public void removeProjectedCount(int recursionLevel)
/*  290:     */     {
/*  291: 585 */       this.m_projectedCounts.removeCount(recursionLevel);
/*  292:     */     }
/*  293:     */     
/*  294:     */     public int getProjectedCount(int recursionLevel)
/*  295:     */     {
/*  296: 595 */       return this.m_projectedCounts.getCount(recursionLevel);
/*  297:     */     }
/*  298:     */     
/*  299:     */     public FPTreeNode getParent()
/*  300:     */     {
/*  301: 604 */       return this.m_parent;
/*  302:     */     }
/*  303:     */     
/*  304:     */     public BinaryItem getItem()
/*  305:     */     {
/*  306: 613 */       return this.m_item;
/*  307:     */     }
/*  308:     */     
/*  309:     */     public String toString(int recursionLevel)
/*  310:     */     {
/*  311: 623 */       return toString("", recursionLevel);
/*  312:     */     }
/*  313:     */     
/*  314:     */     public String toString(String prefix, int recursionLevel)
/*  315:     */     {
/*  316: 634 */       StringBuffer buffer = new StringBuffer();
/*  317: 635 */       buffer.append(prefix);
/*  318: 636 */       buffer.append("|  ");
/*  319: 637 */       buffer.append(this.m_item.toString());
/*  320: 638 */       buffer.append(" (");
/*  321: 639 */       buffer.append(this.m_projectedCounts.getCount(recursionLevel));
/*  322: 640 */       buffer.append(")\n");
/*  323: 642 */       for (FPTreeNode node : this.m_children.values()) {
/*  324: 643 */         buffer.append(node.toString(prefix + "|  ", recursionLevel));
/*  325:     */       }
/*  326: 645 */       return buffer.toString();
/*  327:     */     }
/*  328:     */     
/*  329:     */     protected int assignIDs(int lastID)
/*  330:     */     {
/*  331: 649 */       int currentLastID = lastID + 1;
/*  332: 650 */       this.m_ID = currentLastID;
/*  333: 651 */       if (this.m_children != null)
/*  334:     */       {
/*  335: 652 */         Collection<FPTreeNode> kids = this.m_children.values();
/*  336: 653 */         for (FPTreeNode n : kids) {
/*  337: 654 */           currentLastID = n.assignIDs(currentLastID);
/*  338:     */         }
/*  339:     */       }
/*  340: 657 */       return currentLastID;
/*  341:     */     }
/*  342:     */     
/*  343:     */     public void graphFPTree(StringBuffer text)
/*  344:     */     {
/*  345: 666 */       if (this.m_children != null)
/*  346:     */       {
/*  347: 667 */         Collection<FPTreeNode> kids = this.m_children.values();
/*  348: 668 */         for (FPTreeNode n : kids)
/*  349:     */         {
/*  350: 669 */           text.append("N" + n.m_ID);
/*  351: 670 */           text.append(" [label=\"");
/*  352: 671 */           text.append(n.getItem().toString() + " (" + n.getProjectedCount(0) + ")\\n");
/*  353:     */           
/*  354: 673 */           text.append("\"]\n");
/*  355: 674 */           n.graphFPTree(text);
/*  356: 675 */           text.append("N" + this.m_ID + "->" + "N" + n.m_ID + "\n");
/*  357:     */         }
/*  358:     */       }
/*  359:     */     }
/*  360:     */   }
/*  361:     */   
/*  362:     */   private static class FPTreeRoot
/*  363:     */     extends FPGrowth.FPTreeNode
/*  364:     */   {
/*  365:     */     private static final long serialVersionUID = 632150939785333297L;
/*  366:     */     
/*  367:     */     protected static class Header
/*  368:     */       implements Serializable
/*  369:     */     {
/*  370:     */       private static final long serialVersionUID = -6583156284891368909L;
/*  371: 698 */       protected List<FPGrowth.FPTreeNode> m_headerList = new LinkedList();
/*  372: 701 */       protected FPGrowth.ShadowCounts m_projectedHeaderCounts = new FPGrowth.ShadowCounts();
/*  373:     */       
/*  374:     */       public void addToList(FPGrowth.FPTreeNode toAdd)
/*  375:     */       {
/*  376: 709 */         this.m_headerList.add(toAdd);
/*  377:     */       }
/*  378:     */       
/*  379:     */       public List<FPGrowth.FPTreeNode> getHeaderList()
/*  380:     */       {
/*  381: 718 */         return this.m_headerList;
/*  382:     */       }
/*  383:     */       
/*  384:     */       public FPGrowth.ShadowCounts getProjectedCounts()
/*  385:     */       {
/*  386: 727 */         return this.m_projectedHeaderCounts;
/*  387:     */       }
/*  388:     */     }
/*  389:     */     
/*  390: 732 */     protected Map<BinaryItem, Header> m_headerTable = new HashMap();
/*  391:     */     
/*  392:     */     public FPTreeRoot()
/*  393:     */     {
/*  394: 738 */       super(null);
/*  395:     */     }
/*  396:     */     
/*  397:     */     public void addItemSet(Collection<BinaryItem> itemSet, int incr)
/*  398:     */     {
/*  399: 748 */       super.addItemSet(itemSet, this.m_headerTable, incr);
/*  400:     */     }
/*  401:     */     
/*  402:     */     public Map<BinaryItem, Header> getHeaderTable()
/*  403:     */     {
/*  404: 757 */       return this.m_headerTable;
/*  405:     */     }
/*  406:     */     
/*  407:     */     public boolean isEmpty(int recursionLevel)
/*  408:     */     {
/*  409: 761 */       for (FPGrowth.FPTreeNode c : this.m_children.values()) {
/*  410: 762 */         if (c.getProjectedCount(recursionLevel) > 0) {
/*  411: 763 */           return false;
/*  412:     */         }
/*  413:     */       }
/*  414: 766 */       return true;
/*  415:     */     }
/*  416:     */     
/*  417:     */     public String toString(String pad, int recursionLevel)
/*  418:     */     {
/*  419: 779 */       StringBuffer result = new StringBuffer();
/*  420: 780 */       result.append(pad);
/*  421: 781 */       result.append("+ ROOT\n");
/*  422: 783 */       for (FPGrowth.FPTreeNode node : this.m_children.values()) {
/*  423: 784 */         result.append(node.toString(pad + "|  ", recursionLevel));
/*  424:     */       }
/*  425: 786 */       return result.toString();
/*  426:     */     }
/*  427:     */   }
/*  428:     */   
/*  429:     */   private static void nextSubset(boolean[] subset)
/*  430:     */   {
/*  431: 791 */     for (int i = 0; i < subset.length; i++)
/*  432:     */     {
/*  433: 792 */       if (subset[i] == 0)
/*  434:     */       {
/*  435: 793 */         subset[i] = true;
/*  436: 794 */         break;
/*  437:     */       }
/*  438: 796 */       subset[i] = false;
/*  439:     */     }
/*  440:     */   }
/*  441:     */   
/*  442:     */   private static Collection<Item> getPremise(FrequentBinaryItemSet fis, boolean[] subset)
/*  443:     */   {
/*  444: 803 */     boolean ok = false;
/*  445: 804 */     for (int i = 0; i < subset.length; i++) {
/*  446: 805 */       if (subset[i] == 0)
/*  447:     */       {
/*  448: 806 */         ok = true;
/*  449: 807 */         break;
/*  450:     */       }
/*  451:     */     }
/*  452: 811 */     if (!ok) {
/*  453: 812 */       return null;
/*  454:     */     }
/*  455: 815 */     List<Item> premise = new ArrayList();
/*  456: 816 */     ArrayList<Item> items = new ArrayList(fis.getItems());
/*  457: 818 */     for (int i = 0; i < subset.length; i++) {
/*  458: 819 */       if (subset[i] != 0) {
/*  459: 820 */         premise.add(items.get(i));
/*  460:     */       }
/*  461:     */     }
/*  462: 823 */     return premise;
/*  463:     */   }
/*  464:     */   
/*  465:     */   private static Collection<Item> getConsequence(FrequentBinaryItemSet fis, boolean[] subset)
/*  466:     */   {
/*  467: 828 */     List<Item> consequence = new ArrayList();
/*  468: 829 */     ArrayList<Item> items = new ArrayList(fis.getItems());
/*  469: 831 */     for (int i = 0; i < subset.length; i++) {
/*  470: 832 */       if (subset[i] == 0) {
/*  471: 833 */         consequence.add(items.get(i));
/*  472:     */       }
/*  473:     */     }
/*  474: 836 */     return consequence;
/*  475:     */   }
/*  476:     */   
/*  477:     */   public static List<AssociationRule> generateRulesBruteForce(FrequentItemSets largeItemSets, DefaultAssociationRule.METRIC_TYPE metricToUse, double metricThreshold, int upperBoundMinSuppAsInstances, int lowerBoundMinSuppAsInstances, int totalTransactions)
/*  478:     */   {
/*  479: 859 */     List<AssociationRule> rules = new ArrayList();
/*  480: 860 */     largeItemSets.sort();
/*  481: 861 */     Map<Collection<BinaryItem>, Integer> frequencyLookup = new HashMap();
/*  482:     */     
/*  483: 863 */     Iterator<FrequentBinaryItemSet> setI = largeItemSets.iterator();
/*  484: 865 */     while (setI.hasNext())
/*  485:     */     {
/*  486: 866 */       FrequentBinaryItemSet fis = (FrequentBinaryItemSet)setI.next();
/*  487: 867 */       frequencyLookup.put(fis.getItems(), Integer.valueOf(fis.getSupport()));
/*  488: 868 */       if (fis.getItems().size() > 1)
/*  489:     */       {
/*  490: 870 */         boolean[] subset = new boolean[fis.getItems().size()];
/*  491: 871 */         Collection<Item> premise = null;
/*  492: 872 */         Collection<Item> consequence = null;
/*  493: 873 */         while ((premise = getPremise(fis, subset)) != null)
/*  494:     */         {
/*  495: 874 */           if ((premise.size() > 0) && (premise.size() < fis.getItems().size()))
/*  496:     */           {
/*  497: 875 */             consequence = getConsequence(fis, subset);
/*  498: 876 */             int totalSupport = fis.getSupport();
/*  499: 877 */             int supportPremise = ((Integer)frequencyLookup.get(premise)).intValue();
/*  500: 878 */             int supportConsequence = ((Integer)frequencyLookup.get(consequence)).intValue();
/*  501:     */             
/*  502:     */ 
/*  503:     */ 
/*  504: 882 */             DefaultAssociationRule candidate = new DefaultAssociationRule(premise, consequence, metricToUse, supportPremise, supportConsequence, totalSupport, totalTransactions);
/*  505: 885 */             if ((candidate.getPrimaryMetricValue() > metricThreshold) && (candidate.getTotalSupport() >= lowerBoundMinSuppAsInstances) && (candidate.getTotalSupport() <= upperBoundMinSuppAsInstances)) {
/*  506: 889 */               rules.add(candidate);
/*  507:     */             }
/*  508:     */           }
/*  509: 892 */           nextSubset(subset);
/*  510:     */         }
/*  511:     */       }
/*  512:     */     }
/*  513: 896 */     return rules;
/*  514:     */   }
/*  515:     */   
/*  516:     */   public static List<AssociationRule> pruneRules(List<AssociationRule> rulesToPrune, ArrayList<Item> itemsToConsider, boolean useOr)
/*  517:     */   {
/*  518: 902 */     ArrayList<AssociationRule> result = new ArrayList();
/*  519: 904 */     for (AssociationRule r : rulesToPrune) {
/*  520: 905 */       if (r.containsItems(itemsToConsider, useOr)) {
/*  521: 906 */         result.add(r);
/*  522:     */       }
/*  523:     */     }
/*  524: 910 */     return result;
/*  525:     */   }
/*  526:     */   
/*  527: 914 */   protected int m_numRulesToFind = 10;
/*  528: 918 */   protected double m_upperBoundMinSupport = 1.0D;
/*  529: 921 */   protected double m_lowerBoundMinSupport = 0.1D;
/*  530: 924 */   protected double m_delta = 0.05D;
/*  531:     */   protected int m_numInstances;
/*  532: 933 */   protected int m_offDiskReportingFrequency = 10000;
/*  533: 940 */   protected boolean m_findAllRulesForSupportLevel = false;
/*  534: 945 */   protected int m_positiveIndex = 2;
/*  535: 947 */   protected DefaultAssociationRule.METRIC_TYPE m_metric = DefaultAssociationRule.METRIC_TYPE.CONFIDENCE;
/*  536: 949 */   protected double m_metricThreshold = 0.9D;
/*  537:     */   protected FrequentItemSets m_largeItemSets;
/*  538:     */   protected List<AssociationRule> m_rules;
/*  539: 958 */   protected int m_maxItems = -1;
/*  540: 964 */   protected String m_transactionsMustContain = "";
/*  541: 967 */   protected boolean m_mustContainOR = false;
/*  542: 970 */   protected String m_rulesMustContain = "";
/*  543:     */   
/*  544:     */   public Capabilities getCapabilities()
/*  545:     */   {
/*  546: 979 */     Capabilities result = super.getCapabilities();
/*  547: 980 */     result.disableAll();
/*  548:     */     
/*  549:     */ 
/*  550:     */ 
/*  551:     */ 
/*  552: 985 */     result.enable(Capabilities.Capability.UNARY_ATTRIBUTES);
/*  553: 986 */     result.enable(Capabilities.Capability.BINARY_ATTRIBUTES);
/*  554: 987 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  555:     */     
/*  556: 989 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  557:     */     
/*  558: 991 */     return result;
/*  559:     */   }
/*  560:     */   
/*  561:     */   public String globalInfo()
/*  562:     */   {
/*  563:1001 */     return "Class implementing the FP-growth algorithm for finding large item sets without candidate generation. Iteratively reduces the minimum support until it finds the required number of rules with the given minimum metric. For more information see:\n\n" + getTechnicalInformation().toString();
/*  564:     */   }
/*  565:     */   
/*  566:     */   public TechnicalInformation getTechnicalInformation()
/*  567:     */   {
/*  568:1019 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  569:1020 */     result.setValue(TechnicalInformation.Field.AUTHOR, "J. Han and J.Pei and Y. Yin");
/*  570:1021 */     result.setValue(TechnicalInformation.Field.TITLE, "Mining frequent patterns without candidate generation");
/*  571:     */     
/*  572:1023 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Proceedings of the 2000 ACM-SIGMID International Conference on Management of Data");
/*  573:     */     
/*  574:     */ 
/*  575:1026 */     result.setValue(TechnicalInformation.Field.YEAR, "2000");
/*  576:1027 */     result.setValue(TechnicalInformation.Field.PAGES, "1-12");
/*  577:     */     
/*  578:1029 */     return result;
/*  579:     */   }
/*  580:     */   
/*  581:     */   private boolean passesMustContain(Instance inst, boolean[] transactionsMustContainIndexes, int numInTransactionsMustContainList)
/*  582:     */   {
/*  583:1036 */     boolean result = false;
/*  584:1038 */     if ((inst instanceof SparseInstance))
/*  585:     */     {
/*  586:1039 */       int containsCount = 0;
/*  587:1040 */       for (int i = 0; i < inst.numValues(); i++)
/*  588:     */       {
/*  589:1041 */         int attIndex = inst.index(i);
/*  590:1043 */         if (this.m_mustContainOR)
/*  591:     */         {
/*  592:1044 */           if (transactionsMustContainIndexes[attIndex] != 0) {
/*  593:1047 */             return true;
/*  594:     */           }
/*  595:     */         }
/*  596:1050 */         else if (transactionsMustContainIndexes[attIndex] != 0) {
/*  597:1051 */           containsCount++;
/*  598:     */         }
/*  599:     */       }
/*  600:1056 */       if ((!this.m_mustContainOR) && 
/*  601:1057 */         (containsCount == numInTransactionsMustContainList)) {
/*  602:1058 */         return true;
/*  603:     */       }
/*  604:     */     }
/*  605:     */     else
/*  606:     */     {
/*  607:1062 */       int containsCount = 0;
/*  608:1063 */       for (int i = 0; i < transactionsMustContainIndexes.length; i++) {
/*  609:1064 */         if ((transactionsMustContainIndexes[i] != 0) && 
/*  610:1065 */           ((int)inst.value(i) == this.m_positiveIndex - 1))
/*  611:     */         {
/*  612:1066 */           if (this.m_mustContainOR) {
/*  613:1070 */             return true;
/*  614:     */           }
/*  615:1072 */           containsCount++;
/*  616:     */         }
/*  617:     */       }
/*  618:1078 */       if ((!this.m_mustContainOR) && 
/*  619:1079 */         (containsCount == numInTransactionsMustContainList)) {
/*  620:1080 */         return true;
/*  621:     */       }
/*  622:     */     }
/*  623:1085 */     return result;
/*  624:     */   }
/*  625:     */   
/*  626:     */   private void processSingleton(Instance current, ArrayList<BinaryItem> singletons)
/*  627:     */     throws Exception
/*  628:     */   {
/*  629:1091 */     if ((current instanceof SparseInstance)) {
/*  630:1092 */       for (int j = 0; j < current.numValues(); j++)
/*  631:     */       {
/*  632:1093 */         int attIndex = current.index(j);
/*  633:1094 */         ((BinaryItem)singletons.get(attIndex)).increaseFrequency();
/*  634:     */       }
/*  635:     */     } else {
/*  636:1097 */       for (int j = 0; j < current.numAttributes(); j++) {
/*  637:1098 */         if ((!current.isMissing(j)) && (
/*  638:1099 */           (current.attribute(j).numValues() == 1) || (current.value(j) == this.m_positiveIndex - 1))) {
/*  639:1101 */           ((BinaryItem)singletons.get(j)).increaseFrequency();
/*  640:     */         }
/*  641:     */       }
/*  642:     */     }
/*  643:     */   }
/*  644:     */   
/*  645:     */   protected ArrayList<BinaryItem> getSingletons(Object source)
/*  646:     */     throws Exception
/*  647:     */   {
/*  648:1116 */     ArrayList<BinaryItem> singletons = new ArrayList();
/*  649:1117 */     Instances data = null;
/*  650:1119 */     if ((source instanceof Instances)) {
/*  651:1120 */       data = (Instances)source;
/*  652:1121 */     } else if ((source instanceof ArffLoader)) {
/*  653:1122 */       data = ((ArffLoader)source).getStructure();
/*  654:     */     }
/*  655:1125 */     for (int i = 0; i < data.numAttributes(); i++) {
/*  656:1126 */       singletons.add(new BinaryItem(data.attribute(i), this.m_positiveIndex - 1));
/*  657:     */     }
/*  658:1129 */     if ((source instanceof Instances))
/*  659:     */     {
/*  660:1131 */       this.m_numInstances = data.numInstances();
/*  661:1133 */       for (int i = 0; i < data.numInstances(); i++)
/*  662:     */       {
/*  663:1134 */         Instance current = data.instance(i);
/*  664:1135 */         processSingleton(current, singletons);
/*  665:     */       }
/*  666:     */     }
/*  667:1137 */     else if ((source instanceof ArffLoader))
/*  668:     */     {
/*  669:1138 */       ArffLoader loader = (ArffLoader)source;
/*  670:1139 */       Instance current = null;
/*  671:1140 */       int count = 0;
/*  672:1141 */       while ((current = loader.getNextInstance(data)) != null)
/*  673:     */       {
/*  674:1142 */         processSingleton(current, singletons);
/*  675:1143 */         count++;
/*  676:1144 */         if (count % this.m_offDiskReportingFrequency == 0) {
/*  677:1145 */           System.err.println("Singletons: done " + count);
/*  678:     */         }
/*  679:     */       }
/*  680:1150 */       this.m_numInstances = count;
/*  681:     */       
/*  682:1152 */       loader.reset();
/*  683:     */     }
/*  684:1155 */     return singletons;
/*  685:     */   }
/*  686:     */   
/*  687:     */   protected ArrayList<BinaryItem> getSingletons(Instances data)
/*  688:     */     throws Exception
/*  689:     */   {
/*  690:1167 */     return getSingletons(data);
/*  691:     */   }
/*  692:     */   
/*  693:     */   private void insertInstance(Instance current, ArrayList<BinaryItem> singletons, FPTreeRoot tree, int minSupport)
/*  694:     */   {
/*  695:1206 */     ArrayList<BinaryItem> transaction = new ArrayList();
/*  696:1207 */     if ((current instanceof SparseInstance))
/*  697:     */     {
/*  698:1208 */       for (int j = 0; j < current.numValues(); j++)
/*  699:     */       {
/*  700:1209 */         int attIndex = current.index(j);
/*  701:1210 */         if (((BinaryItem)singletons.get(attIndex)).getFrequency() >= minSupport) {
/*  702:1211 */           transaction.add(singletons.get(attIndex));
/*  703:     */         }
/*  704:     */       }
/*  705:1214 */       Collections.sort(transaction);
/*  706:1215 */       tree.addItemSet(transaction, 1);
/*  707:     */     }
/*  708:     */     else
/*  709:     */     {
/*  710:1217 */       for (int j = 0; j < current.numAttributes(); j++) {
/*  711:1218 */         if ((!current.isMissing(j)) && (
/*  712:1219 */           (current.attribute(j).numValues() == 1) || (current.value(j) == this.m_positiveIndex - 1))) {
/*  713:1221 */           if (((BinaryItem)singletons.get(j)).getFrequency() >= minSupport) {
/*  714:1222 */             transaction.add(singletons.get(j));
/*  715:     */           }
/*  716:     */         }
/*  717:     */       }
/*  718:1227 */       Collections.sort(transaction);
/*  719:1228 */       tree.addItemSet(transaction, 1);
/*  720:     */     }
/*  721:     */   }
/*  722:     */   
/*  723:     */   protected FPTreeRoot buildFPTree(ArrayList<BinaryItem> singletons, Object dataSource, int minSupport)
/*  724:     */     throws Exception
/*  725:     */   {
/*  726:1245 */     FPTreeRoot tree = new FPTreeRoot();
/*  727:1246 */     Instances data = null;
/*  728:1247 */     if ((dataSource instanceof Instances)) {
/*  729:1248 */       data = (Instances)dataSource;
/*  730:1249 */     } else if ((dataSource instanceof ArffLoader)) {
/*  731:1250 */       data = ((ArffLoader)dataSource).getStructure();
/*  732:     */     }
/*  733:1253 */     if ((dataSource instanceof Instances))
/*  734:     */     {
/*  735:1254 */       for (int i = 0; i < data.numInstances(); i++) {
/*  736:1255 */         insertInstance(data.instance(i), singletons, tree, minSupport);
/*  737:     */       }
/*  738:     */     }
/*  739:1257 */     else if ((dataSource instanceof ArffLoader))
/*  740:     */     {
/*  741:1258 */       ArffLoader loader = (ArffLoader)dataSource;
/*  742:1259 */       Instance current = null;
/*  743:1260 */       int count = 0;
/*  744:1261 */       while ((current = loader.getNextInstance(data)) != null)
/*  745:     */       {
/*  746:1262 */         insertInstance(current, singletons, tree, minSupport);
/*  747:1263 */         count++;
/*  748:1264 */         if (count % this.m_offDiskReportingFrequency == 0) {
/*  749:1265 */           System.err.println("build tree done: " + count);
/*  750:     */         }
/*  751:     */       }
/*  752:     */     }
/*  753:1270 */     return tree;
/*  754:     */   }
/*  755:     */   
/*  756:     */   protected void mineTree(FPTreeRoot tree, FrequentItemSets largeItemSets, int recursionLevel, FrequentBinaryItemSet conditionalItems, int minSupport)
/*  757:     */   {
/*  758:1318 */     if (!tree.isEmpty(recursionLevel))
/*  759:     */     {
/*  760:1319 */       if ((this.m_maxItems > 0) && (recursionLevel >= this.m_maxItems)) {
/*  761:1321 */         return;
/*  762:     */       }
/*  763:1324 */       Map<BinaryItem, FPGrowth.FPTreeRoot.Header> headerTable = tree.getHeaderTable();
/*  764:1325 */       Set<BinaryItem> keys = headerTable.keySet();
/*  765:     */       
/*  766:     */ 
/*  767:1328 */       Iterator<BinaryItem> i = keys.iterator();
/*  768:1329 */       while (i.hasNext())
/*  769:     */       {
/*  770:1330 */         BinaryItem item = (BinaryItem)i.next();
/*  771:1331 */         FPGrowth.FPTreeRoot.Header itemHeader = (FPGrowth.FPTreeRoot.Header)headerTable.get(item);
/*  772:     */         
/*  773:     */ 
/*  774:1334 */         int support = itemHeader.getProjectedCounts().getCount(recursionLevel);
/*  775:1335 */         if (support >= minSupport)
/*  776:     */         {
/*  777:1337 */           for (FPTreeNode n : itemHeader.getHeaderList())
/*  778:     */           {
/*  779:1339 */             int currentCount = n.getProjectedCount(recursionLevel);
/*  780:1340 */             if (currentCount > 0)
/*  781:     */             {
/*  782:1341 */               FPTreeNode temp = n.getParent();
/*  783:1342 */               while (temp != tree)
/*  784:     */               {
/*  785:1344 */                 temp.increaseProjectedCount(recursionLevel + 1, currentCount);
/*  786:     */                 
/*  787:     */ 
/*  788:1347 */                 ((FPGrowth.FPTreeRoot.Header)headerTable.get(temp.getItem())).getProjectedCounts().increaseCount(recursionLevel + 1, currentCount);
/*  789:     */                 
/*  790:     */ 
/*  791:1350 */                 temp = temp.getParent();
/*  792:     */               }
/*  793:     */             }
/*  794:     */           }
/*  795:1355 */           FrequentBinaryItemSet newConditional = (FrequentBinaryItemSet)conditionalItems.clone();
/*  796:     */           
/*  797:     */ 
/*  798:     */ 
/*  799:1359 */           newConditional.addItem(item);
/*  800:1360 */           newConditional.setSupport(support);
/*  801:     */           
/*  802:     */ 
/*  803:1363 */           largeItemSets.addItemSet(newConditional);
/*  804:     */           
/*  805:     */ 
/*  806:1366 */           mineTree(tree, largeItemSets, recursionLevel + 1, newConditional, minSupport);
/*  807:1370 */           for (FPTreeNode n : itemHeader.getHeaderList())
/*  808:     */           {
/*  809:1371 */             FPTreeNode temp = n.getParent();
/*  810:1372 */             while (temp != tree)
/*  811:     */             {
/*  812:1373 */               temp.removeProjectedCount(recursionLevel + 1);
/*  813:1374 */               temp = temp.getParent();
/*  814:     */             }
/*  815:     */           }
/*  816:1380 */           for (FPGrowth.FPTreeRoot.Header h : headerTable.values()) {
/*  817:1381 */             h.getProjectedCounts().removeCount(recursionLevel + 1);
/*  818:     */           }
/*  819:     */         }
/*  820:     */       }
/*  821:     */     }
/*  822:     */   }
/*  823:     */   
/*  824:     */   public FPGrowth()
/*  825:     */   {
/*  826:1392 */     resetOptions();
/*  827:     */   }
/*  828:     */   
/*  829:     */   public void resetOptions()
/*  830:     */   {
/*  831:1399 */     this.m_delta = 0.05D;
/*  832:1400 */     this.m_metricThreshold = 0.9D;
/*  833:1401 */     this.m_numRulesToFind = 10;
/*  834:1402 */     this.m_lowerBoundMinSupport = 0.1D;
/*  835:1403 */     this.m_upperBoundMinSupport = 1.0D;
/*  836:     */     
/*  837:1405 */     this.m_positiveIndex = 2;
/*  838:1406 */     this.m_transactionsMustContain = "";
/*  839:1407 */     this.m_rulesMustContain = "";
/*  840:1408 */     this.m_mustContainOR = false;
/*  841:     */   }
/*  842:     */   
/*  843:     */   public String positiveIndexTipText()
/*  844:     */   {
/*  845:1417 */     return "Set the index of binary valued attributes that is to be considered the positive index. Has no effect for sparse data (in this case the first index (i.e. non-zero values) is always treated as  positive. Also has no effect for unary valued attributes (i.e. when using the Weka Apriori-style format for market basket data, which uses missing value \"?\" to indicate absence of an item.";
/*  846:     */   }
/*  847:     */   
/*  848:     */   public void setPositiveIndex(int index)
/*  849:     */   {
/*  850:1433 */     this.m_positiveIndex = index;
/*  851:     */   }
/*  852:     */   
/*  853:     */   public int getPositiveIndex()
/*  854:     */   {
/*  855:1444 */     return this.m_positiveIndex;
/*  856:     */   }
/*  857:     */   
/*  858:     */   public void setNumRulesToFind(int numR)
/*  859:     */   {
/*  860:1453 */     this.m_numRulesToFind = numR;
/*  861:     */   }
/*  862:     */   
/*  863:     */   public int getNumRulesToFind()
/*  864:     */   {
/*  865:1462 */     return this.m_numRulesToFind;
/*  866:     */   }
/*  867:     */   
/*  868:     */   public String numRulesToFindTipText()
/*  869:     */   {
/*  870:1471 */     return "The number of rules to output";
/*  871:     */   }
/*  872:     */   
/*  873:     */   public void setMetricType(SelectedTag d)
/*  874:     */   {
/*  875:1480 */     int ordinal = d.getSelectedTag().getID();
/*  876:1481 */     for (DefaultAssociationRule.METRIC_TYPE m : DefaultAssociationRule.METRIC_TYPE.values()) {
/*  877:1483 */       if (m.ordinal() == ordinal)
/*  878:     */       {
/*  879:1484 */         this.m_metric = m;
/*  880:1485 */         break;
/*  881:     */       }
/*  882:     */     }
/*  883:     */   }
/*  884:     */   
/*  885:     */   public void setMaxNumberOfItems(int max)
/*  886:     */   {
/*  887:1496 */     this.m_maxItems = max;
/*  888:     */   }
/*  889:     */   
/*  890:     */   public int getMaxNumberOfItems()
/*  891:     */   {
/*  892:1505 */     return this.m_maxItems;
/*  893:     */   }
/*  894:     */   
/*  895:     */   public String maxNumberOfItemsTipText()
/*  896:     */   {
/*  897:1514 */     return "The maximum number of items to include in frequent item sets. -1 means no limit.";
/*  898:     */   }
/*  899:     */   
/*  900:     */   public SelectedTag getMetricType()
/*  901:     */   {
/*  902:1524 */     return new SelectedTag(this.m_metric.ordinal(), DefaultAssociationRule.TAGS_SELECTION);
/*  903:     */   }
/*  904:     */   
/*  905:     */   public String metricTypeTipText()
/*  906:     */   {
/*  907:1534 */     return "Set the type of metric by which to rank rules. Confidence is the proportion of the examples covered by the premise that are also covered by the consequence(Class association rules can only be mined using confidence). Lift is confidence divided by the proportion of all examples that are covered by the consequence. This is a measure of the importance of the association that is independent of support. Leverage is the proportion of additional examples covered by both the premise and consequence above those expected if the premise and consequence were independent of each other. The total number of examples that this represents is presented in brackets following the leverage. Conviction is another measure of departure from independence.";
/*  908:     */   }
/*  909:     */   
/*  910:     */   public String minMetricTipText()
/*  911:     */   {
/*  912:1554 */     return "Minimum metric score. Consider only rules with scores higher than this value.";
/*  913:     */   }
/*  914:     */   
/*  915:     */   public double getMinMetric()
/*  916:     */   {
/*  917:1565 */     return this.m_metricThreshold;
/*  918:     */   }
/*  919:     */   
/*  920:     */   public void setMinMetric(double v)
/*  921:     */   {
/*  922:1575 */     this.m_metricThreshold = v;
/*  923:     */   }
/*  924:     */   
/*  925:     */   public String transactionsMustContainTipText()
/*  926:     */   {
/*  927:1585 */     return "Limit input to FPGrowth to those transactions (instances) that contain these items. Provide a comma separated list of attribute names.";
/*  928:     */   }
/*  929:     */   
/*  930:     */   public void setTransactionsMustContain(String list)
/*  931:     */   {
/*  932:1598 */     this.m_transactionsMustContain = list;
/*  933:     */   }
/*  934:     */   
/*  935:     */   public String getTransactionsMustContain()
/*  936:     */   {
/*  937:1609 */     return this.m_transactionsMustContain;
/*  938:     */   }
/*  939:     */   
/*  940:     */   public String rulesMustContainTipText()
/*  941:     */   {
/*  942:1619 */     return "Only print rules that contain these items. Provide a comma separated list of attribute names.";
/*  943:     */   }
/*  944:     */   
/*  945:     */   public void setRulesMustContain(String list)
/*  946:     */   {
/*  947:1631 */     this.m_rulesMustContain = list;
/*  948:     */   }
/*  949:     */   
/*  950:     */   public String getRulesMustContain()
/*  951:     */   {
/*  952:1642 */     return this.m_rulesMustContain;
/*  953:     */   }
/*  954:     */   
/*  955:     */   public String useORForMustContainListTipText()
/*  956:     */   {
/*  957:1652 */     return "Use OR instead of AND for transactions/rules must contain lists.";
/*  958:     */   }
/*  959:     */   
/*  960:     */   public void setUseORForMustContainList(boolean b)
/*  961:     */   {
/*  962:1662 */     this.m_mustContainOR = b;
/*  963:     */   }
/*  964:     */   
/*  965:     */   public boolean getUseORForMustContainList()
/*  966:     */   {
/*  967:1672 */     return this.m_mustContainOR;
/*  968:     */   }
/*  969:     */   
/*  970:     */   public String deltaTipText()
/*  971:     */   {
/*  972:1682 */     return "Iteratively decrease support by this factor. Reduces support until min support is reached or required number of rules has been generated.";
/*  973:     */   }
/*  974:     */   
/*  975:     */   public double getDelta()
/*  976:     */   {
/*  977:1694 */     return this.m_delta;
/*  978:     */   }
/*  979:     */   
/*  980:     */   public void setDelta(double v)
/*  981:     */   {
/*  982:1704 */     this.m_delta = v;
/*  983:     */   }
/*  984:     */   
/*  985:     */   public String lowerBoundMinSupportTipText()
/*  986:     */   {
/*  987:1714 */     return "Lower bound for minimum support as a fraction or number of instances.";
/*  988:     */   }
/*  989:     */   
/*  990:     */   public double getLowerBoundMinSupport()
/*  991:     */   {
/*  992:1724 */     return this.m_lowerBoundMinSupport;
/*  993:     */   }
/*  994:     */   
/*  995:     */   public void setLowerBoundMinSupport(double v)
/*  996:     */   {
/*  997:1734 */     this.m_lowerBoundMinSupport = v;
/*  998:     */   }
/*  999:     */   
/* 1000:     */   public String upperBoundMinSupportTipText()
/* 1001:     */   {
/* 1002:1744 */     return "Upper bound for minimum support as a fraction or number of instances. Start iteratively decreasing minimum support from this value.";
/* 1003:     */   }
/* 1004:     */   
/* 1005:     */   public double getUpperBoundMinSupport()
/* 1006:     */   {
/* 1007:1755 */     return this.m_upperBoundMinSupport;
/* 1008:     */   }
/* 1009:     */   
/* 1010:     */   public void setUpperBoundMinSupport(double v)
/* 1011:     */   {
/* 1012:1765 */     this.m_upperBoundMinSupport = v;
/* 1013:     */   }
/* 1014:     */   
/* 1015:     */   public String findAllRulesForSupportLevelTipText()
/* 1016:     */   {
/* 1017:1774 */     return "Find all rules that meet the lower bound on minimum support and the minimum metric constraint. Turning this mode on will disable the iterative support reduction procedure to find the specified number of rules.";
/* 1018:     */   }
/* 1019:     */   
/* 1020:     */   public void setFindAllRulesForSupportLevel(boolean s)
/* 1021:     */   {
/* 1022:1790 */     this.m_findAllRulesForSupportLevel = s;
/* 1023:     */   }
/* 1024:     */   
/* 1025:     */   public boolean getFindAllRulesForSupportLevel()
/* 1026:     */   {
/* 1027:1801 */     return this.m_findAllRulesForSupportLevel;
/* 1028:     */   }
/* 1029:     */   
/* 1030:     */   public void setOffDiskReportingFrequency(int freq)
/* 1031:     */   {
/* 1032:1811 */     this.m_offDiskReportingFrequency = freq;
/* 1033:     */   }
/* 1034:     */   
/* 1035:     */   public AssociationRules getAssociationRules()
/* 1036:     */   {
/* 1037:1828 */     List<AssociationRule> rulesToReturn = new ArrayList();
/* 1038:     */     
/* 1039:1830 */     int count = 0;
/* 1040:1831 */     for (AssociationRule r : this.m_rules)
/* 1041:     */     {
/* 1042:1832 */       rulesToReturn.add(r);
/* 1043:1833 */       count++;
/* 1044:1834 */       if ((!this.m_findAllRulesForSupportLevel) && (count == this.m_numRulesToFind)) {
/* 1045:     */         break;
/* 1046:     */       }
/* 1047:     */     }
/* 1048:1839 */     return new AssociationRules(rulesToReturn, this);
/* 1049:     */   }
/* 1050:     */   
/* 1051:     */   public String[] getRuleMetricNames()
/* 1052:     */   {
/* 1053:1852 */     String[] metricNames = new String[DefaultAssociationRule.TAGS_SELECTION.length];
/* 1054:1854 */     for (int i = 0; i < DefaultAssociationRule.TAGS_SELECTION.length; i++) {
/* 1055:1855 */       metricNames[i] = DefaultAssociationRule.TAGS_SELECTION[i].getReadable();
/* 1056:     */     }
/* 1057:1858 */     return metricNames;
/* 1058:     */   }
/* 1059:     */   
/* 1060:     */   public boolean canProduceRules()
/* 1061:     */   {
/* 1062:1874 */     return true;
/* 1063:     */   }
/* 1064:     */   
/* 1065:     */   public Enumeration<Option> listOptions()
/* 1066:     */   {
/* 1067:1884 */     Vector<Option> newVector = new Vector();
/* 1068:     */     
/* 1069:1886 */     String string00 = "\tSet the index of the attribute value to consider as 'positive'\n\tfor binary attributes in normal dense instances. Index 2 is always\n\tused for sparse instances. (default = 2)";
/* 1070:     */     
/* 1071:     */ 
/* 1072:1889 */     String string0 = "\tThe maximum number of items to include in large items sets (and rules). (default = -1, i.e. no limit.)";
/* 1073:     */     
/* 1074:     */ 
/* 1075:1892 */     String string1 = "\tThe required number of rules. (default = " + this.m_numRulesToFind + ")";
/* 1076:     */     
/* 1077:1894 */     String string2 = "\tThe minimum metric score of a rule. (default = " + this.m_metricThreshold + ")";
/* 1078:     */     
/* 1079:1896 */     String string3 = "\tThe metric by which to rank rules. (default = confidence)";
/* 1080:     */     
/* 1081:1898 */     String string4 = "\tThe lower bound for the minimum support as a fraction or number of instances. (default = " + this.m_lowerBoundMinSupport + ")";
/* 1082:     */     
/* 1083:1900 */     String string5 = "\tUpper bound for minimum support as a fraction or number of instances. (default = 1.0)";
/* 1084:     */     
/* 1085:1902 */     String string6 = "\tThe delta by which the minimum support is decreased in\n\teach iteration as a fraction or number of instances. (default = " + this.m_delta + ")";
/* 1086:     */     
/* 1087:     */ 
/* 1088:1905 */     String string7 = "\tFind all rules that meet the lower bound on\n\tminimum support and the minimum metric constraint.\n\tTurning this mode on will disable the iterative support reduction\n\tprocedure to find the specified number of rules.";
/* 1089:     */     
/* 1090:     */ 
/* 1091:     */ 
/* 1092:1909 */     String string8 = "\tOnly consider transactions that contain these items (default = no restriction)";
/* 1093:1910 */     String string9 = "\tOnly print rules that contain these items. (default = no restriction)";
/* 1094:1911 */     String string10 = "\tUse OR instead of AND for must contain list(s). Use in conjunction\n\twith -transactions and/or -rules";
/* 1095:     */     
/* 1096:     */ 
/* 1097:1914 */     newVector.add(new Option(string00, "P", 1, "-P <attribute index of positive value>"));
/* 1098:     */     
/* 1099:1916 */     newVector.add(new Option(string0, "I", 1, "-I <max items>"));
/* 1100:1917 */     newVector.add(new Option(string1, "N", 1, "-N <require number of rules>"));
/* 1101:1918 */     newVector.add(new Option(string3, "T", 1, "-T <0=confidence | 1=lift | 2=leverage | 3=Conviction>"));
/* 1102:     */     
/* 1103:1920 */     newVector.add(new Option(string2, "C", 1, "-C <minimum metric score of a rule>"));
/* 1104:     */     
/* 1105:1922 */     newVector.add(new Option(string5, "U", 1, "-U <upper bound for minimum support>"));
/* 1106:     */     
/* 1107:1924 */     newVector.add(new Option(string4, "M", 1, "-M <lower bound for minimum support>"));
/* 1108:     */     
/* 1109:1926 */     newVector.add(new Option(string6, "D", 1, "-D <delta for minimum support>"));
/* 1110:     */     
/* 1111:1928 */     newVector.add(new Option(string7, "S", 0, "-S"));
/* 1112:1929 */     newVector.add(new Option(string8, "transactions", 1, "-transactions <comma separated list of attribute names>"));
/* 1113:     */     
/* 1114:1931 */     newVector.add(new Option(string9, "rules", 1, "-rules <comma separated list of attribute names>"));
/* 1115:     */     
/* 1116:1933 */     newVector.add(new Option(string10, "use-or", 0, "-use-or"));
/* 1117:     */     
/* 1118:1935 */     return newVector.elements();
/* 1119:     */   }
/* 1120:     */   
/* 1121:     */   public void setOptions(String[] options)
/* 1122:     */     throws Exception
/* 1123:     */   {
/* 1124:2020 */     resetOptions();
/* 1125:2021 */     String positiveIndexString = Utils.getOption('P', options);
/* 1126:2022 */     String maxItemsString = Utils.getOption('I', options);
/* 1127:2023 */     String numRulesString = Utils.getOption('N', options);
/* 1128:2024 */     String minMetricString = Utils.getOption('C', options);
/* 1129:2025 */     String metricTypeString = Utils.getOption("T", options);
/* 1130:2026 */     String lowerBoundSupportString = Utils.getOption("M", options);
/* 1131:2027 */     String upperBoundSupportString = Utils.getOption("U", options);
/* 1132:2028 */     String deltaString = Utils.getOption("D", options);
/* 1133:2029 */     String transactionsString = Utils.getOption("transactions", options);
/* 1134:2030 */     String rulesString = Utils.getOption("rules", options);
/* 1135:2032 */     if (positiveIndexString.length() != 0) {
/* 1136:2033 */       setPositiveIndex(Integer.parseInt(positiveIndexString));
/* 1137:     */     }
/* 1138:2036 */     if (maxItemsString.length() != 0) {
/* 1139:2037 */       setMaxNumberOfItems(Integer.parseInt(maxItemsString));
/* 1140:     */     }
/* 1141:2040 */     if (metricTypeString.length() != 0) {
/* 1142:2041 */       setMetricType(new SelectedTag(Integer.parseInt(metricTypeString), DefaultAssociationRule.TAGS_SELECTION));
/* 1143:     */     }
/* 1144:2045 */     if (numRulesString.length() != 0) {
/* 1145:2046 */       setNumRulesToFind(Integer.parseInt(numRulesString));
/* 1146:     */     }
/* 1147:2049 */     if (minMetricString.length() != 0) {
/* 1148:2050 */       setMinMetric(Double.parseDouble(minMetricString));
/* 1149:     */     }
/* 1150:2053 */     if (deltaString.length() != 0) {
/* 1151:2054 */       setDelta(Double.parseDouble(deltaString));
/* 1152:     */     }
/* 1153:2057 */     if (lowerBoundSupportString.length() != 0) {
/* 1154:2058 */       setLowerBoundMinSupport(Double.parseDouble(lowerBoundSupportString));
/* 1155:     */     }
/* 1156:2061 */     if (upperBoundSupportString.length() != 0) {
/* 1157:2062 */       setUpperBoundMinSupport(Double.parseDouble(upperBoundSupportString));
/* 1158:     */     }
/* 1159:2065 */     if (transactionsString.length() != 0) {
/* 1160:2066 */       setTransactionsMustContain(transactionsString);
/* 1161:     */     }
/* 1162:2069 */     if (rulesString.length() > 0) {
/* 1163:2070 */       setRulesMustContain(rulesString);
/* 1164:     */     }
/* 1165:2073 */     setUseORForMustContainList(Utils.getFlag("use-or", options));
/* 1166:     */     
/* 1167:2075 */     setFindAllRulesForSupportLevel(Utils.getFlag('S', options));
/* 1168:     */   }
/* 1169:     */   
/* 1170:     */   public String[] getOptions()
/* 1171:     */   {
/* 1172:2085 */     ArrayList<String> options = new ArrayList();
/* 1173:     */     
/* 1174:2087 */     options.add("-P");
/* 1175:2088 */     options.add("" + getPositiveIndex());
/* 1176:2089 */     options.add("-I");
/* 1177:2090 */     options.add("" + getMaxNumberOfItems());
/* 1178:2091 */     options.add("-N");
/* 1179:2092 */     options.add("" + getNumRulesToFind());
/* 1180:2093 */     options.add("-T");
/* 1181:2094 */     options.add("" + getMetricType().getSelectedTag().getID());
/* 1182:2095 */     options.add("-C");
/* 1183:2096 */     options.add("" + getMinMetric());
/* 1184:2097 */     options.add("-D");
/* 1185:2098 */     options.add("" + getDelta());
/* 1186:2099 */     options.add("-U");
/* 1187:2100 */     options.add("" + getUpperBoundMinSupport());
/* 1188:2101 */     options.add("-M");
/* 1189:2102 */     options.add("" + getLowerBoundMinSupport());
/* 1190:2103 */     if (getFindAllRulesForSupportLevel()) {
/* 1191:2104 */       options.add("-S");
/* 1192:     */     }
/* 1193:2107 */     if (getTransactionsMustContain().length() > 0)
/* 1194:     */     {
/* 1195:2108 */       options.add("-transactions");
/* 1196:2109 */       options.add(getTransactionsMustContain());
/* 1197:     */     }
/* 1198:2112 */     if (getRulesMustContain().length() > 0)
/* 1199:     */     {
/* 1200:2113 */       options.add("-rules");
/* 1201:2114 */       options.add(getRulesMustContain());
/* 1202:     */     }
/* 1203:2117 */     if (getUseORForMustContainList()) {
/* 1204:2118 */       options.add("-use-or");
/* 1205:     */     }
/* 1206:2121 */     return (String[])options.toArray(new String[1]);
/* 1207:     */   }
/* 1208:     */   
/* 1209:     */   private Instances parseTransactionsMustContain(Instances data)
/* 1210:     */   {
/* 1211:2125 */     String[] split = this.m_transactionsMustContain.trim().split(",");
/* 1212:2126 */     boolean[] transactionsMustContainIndexes = new boolean[data.numAttributes()];
/* 1213:2127 */     int numInTransactionsMustContainList = split.length;
/* 1214:2129 */     for (String element : split)
/* 1215:     */     {
/* 1216:2130 */       String attName = element.trim();
/* 1217:2131 */       Attribute att = data.attribute(attName);
/* 1218:2132 */       if (att == null)
/* 1219:     */       {
/* 1220:2133 */         System.err.println("[FPGrowth] : WARNING - can't find attribute " + attName + " in the data.");
/* 1221:     */         
/* 1222:2135 */         numInTransactionsMustContainList--;
/* 1223:     */       }
/* 1224:     */       else
/* 1225:     */       {
/* 1226:2137 */         transactionsMustContainIndexes[att.index()] = true;
/* 1227:     */       }
/* 1228:     */     }
/* 1229:2141 */     if (numInTransactionsMustContainList == 0) {
/* 1230:2142 */       return data;
/* 1231:     */     }
/* 1232:2144 */     Instances newInsts = new Instances(data, 0);
/* 1233:2145 */     for (int i = 0; i < data.numInstances(); i++) {
/* 1234:2146 */       if (passesMustContain(data.instance(i), transactionsMustContainIndexes, numInTransactionsMustContainList)) {
/* 1235:2148 */         newInsts.add(data.instance(i));
/* 1236:     */       }
/* 1237:     */     }
/* 1238:2151 */     newInsts.compactify();
/* 1239:2152 */     return newInsts;
/* 1240:     */   }
/* 1241:     */   
/* 1242:     */   private ArrayList<Item> parseRulesMustContain(Instances data)
/* 1243:     */   {
/* 1244:2157 */     ArrayList<Item> result = new ArrayList();
/* 1245:     */     
/* 1246:2159 */     String[] split = this.m_rulesMustContain.trim().split(",");
/* 1247:2161 */     for (String element : split)
/* 1248:     */     {
/* 1249:2162 */       String attName = element.trim();
/* 1250:2163 */       Attribute att = data.attribute(attName);
/* 1251:2164 */       if (att == null)
/* 1252:     */       {
/* 1253:2165 */         System.err.println("[FPGrowth] : WARNING - can't find attribute " + attName + " in the data.");
/* 1254:     */       }
/* 1255:     */       else
/* 1256:     */       {
/* 1257:2168 */         BinaryItem tempI = null;
/* 1258:     */         try
/* 1259:     */         {
/* 1260:2170 */           tempI = new BinaryItem(att, this.m_positiveIndex - 1);
/* 1261:     */         }
/* 1262:     */         catch (Exception e)
/* 1263:     */         {
/* 1264:2173 */           e.printStackTrace();
/* 1265:     */         }
/* 1266:2175 */         result.add(tempI);
/* 1267:     */       }
/* 1268:     */     }
/* 1269:2179 */     return result;
/* 1270:     */   }
/* 1271:     */   
/* 1272:     */   private void buildAssociations(Object source)
/* 1273:     */     throws Exception
/* 1274:     */   {
/* 1275:2194 */     Instances data = null;
/* 1276:2195 */     Capabilities capabilities = getCapabilities();
/* 1277:2196 */     boolean arffLoader = false;
/* 1278:2197 */     boolean breakOnNext = false;
/* 1279:2199 */     if ((source instanceof ArffLoader))
/* 1280:     */     {
/* 1281:2200 */       data = ((ArffLoader)source).getStructure();
/* 1282:2201 */       capabilities.setMinimumNumberInstances(0);
/* 1283:2202 */       arffLoader = true;
/* 1284:     */     }
/* 1285:     */     else
/* 1286:     */     {
/* 1287:2204 */       data = (Instances)source;
/* 1288:     */     }
/* 1289:2208 */     capabilities.testWithFail(data);
/* 1290:2212 */     if ((this.m_transactionsMustContain.length() > 0) && ((source instanceof Instances)))
/* 1291:     */     {
/* 1292:2213 */       data = parseTransactionsMustContain(data);
/* 1293:2214 */       getCapabilities().testWithFail(data);
/* 1294:     */     }
/* 1295:2217 */     ArrayList<Item> rulesMustContain = null;
/* 1296:2218 */     if (this.m_rulesMustContain.length() > 0) {
/* 1297:2219 */       rulesMustContain = parseRulesMustContain(data);
/* 1298:     */     }
/* 1299:2222 */     ArrayList<BinaryItem> singletons = getSingletons(source);
/* 1300:     */     
/* 1301:2224 */     int upperBoundMinSuppAsInstances = this.m_upperBoundMinSupport > 1.0D ? (int)this.m_upperBoundMinSupport : (int)Math.ceil(this.m_upperBoundMinSupport * this.m_numInstances);
/* 1302:     */     
/* 1303:     */ 
/* 1304:2227 */     int lowerBoundMinSuppAsInstances = this.m_lowerBoundMinSupport > 1.0D ? (int)this.m_lowerBoundMinSupport : (int)Math.ceil(this.m_lowerBoundMinSupport * this.m_numInstances);
/* 1305:     */     
/* 1306:     */ 
/* 1307:2230 */     double lowerBoundMinSuppAsFraction = this.m_lowerBoundMinSupport > 1.0D ? this.m_lowerBoundMinSupport / this.m_numInstances : this.m_lowerBoundMinSupport;
/* 1308:     */     
/* 1309:     */ 
/* 1310:     */ 
/* 1311:2234 */     double deltaAsFraction = this.m_delta > 1.0D ? this.m_delta / this.m_numInstances : this.m_delta;
/* 1312:     */     
/* 1313:     */ 
/* 1314:2237 */     double currentSupport = 1.0D;
/* 1315:2239 */     if (this.m_findAllRulesForSupportLevel) {
/* 1316:2240 */       currentSupport = lowerBoundMinSuppAsFraction;
/* 1317:     */     }
/* 1318:     */     do
/* 1319:     */     {
/* 1320:2244 */       if (arffLoader) {
/* 1321:2245 */         ((ArffLoader)source).reset();
/* 1322:     */       }
/* 1323:2248 */       int currentSupportAsInstances = currentSupport > 1.0D ? (int)currentSupport : (int)Math.ceil(currentSupport * this.m_numInstances);
/* 1324:2252 */       if (arffLoader) {
/* 1325:2253 */         System.err.println("Building FP-tree...");
/* 1326:     */       }
/* 1327:2255 */       FPTreeRoot tree = buildFPTree(singletons, source, currentSupportAsInstances);
/* 1328:     */       
/* 1329:     */ 
/* 1330:2258 */       FrequentItemSets largeItemSets = new FrequentItemSets(this.m_numInstances);
/* 1331:2260 */       if (arffLoader) {
/* 1332:2261 */         System.err.println("Mining tree for min supp " + currentSupport);
/* 1333:     */       }
/* 1334:2265 */       FrequentBinaryItemSet conditionalItems = new FrequentBinaryItemSet(new ArrayList(), 0);
/* 1335:     */       
/* 1336:2267 */       mineTree(tree, largeItemSets, 0, conditionalItems, currentSupportAsInstances);
/* 1337:     */       
/* 1338:     */ 
/* 1339:2270 */       this.m_largeItemSets = largeItemSets;
/* 1340:2272 */       if (arffLoader) {
/* 1341:2273 */         System.err.println("Number of large item sets: " + this.m_largeItemSets.size());
/* 1342:     */       }
/* 1343:2278 */       tree = null;
/* 1344:     */       
/* 1345:2280 */       this.m_rules = generateRulesBruteForce(this.m_largeItemSets, this.m_metric, this.m_metricThreshold, upperBoundMinSuppAsInstances, lowerBoundMinSuppAsInstances, this.m_numInstances);
/* 1346:2284 */       if (arffLoader) {
/* 1347:2285 */         System.err.println("Number of rules found " + this.m_rules.size());
/* 1348:     */       }
/* 1349:2288 */       if ((rulesMustContain != null) && (rulesMustContain.size() > 0)) {
/* 1350:2289 */         this.m_rules = pruneRules(this.m_rules, rulesMustContain, this.m_mustContainOR);
/* 1351:     */       }
/* 1352:2292 */       if ((this.m_findAllRulesForSupportLevel) || 
/* 1353:2293 */         (breakOnNext)) {
/* 1354:     */         break;
/* 1355:     */       }
/* 1356:2296 */       currentSupport -= deltaAsFraction;
/* 1357:2299 */       if (currentSupport < lowerBoundMinSuppAsFraction)
/* 1358:     */       {
/* 1359:2300 */         if (currentSupport + deltaAsFraction <= lowerBoundMinSuppAsFraction) {
/* 1360:     */           break;
/* 1361:     */         }
/* 1362:2302 */         currentSupport = lowerBoundMinSuppAsFraction;
/* 1363:2303 */         breakOnNext = true;
/* 1364:     */       }
/* 1365:2313 */     } while (this.m_rules.size() < this.m_numRulesToFind);
/* 1366:2315 */     Collections.sort(this.m_rules);
/* 1367:     */   }
/* 1368:     */   
/* 1369:     */   public void buildAssociations(Instances data)
/* 1370:     */     throws Exception
/* 1371:     */   {
/* 1372:2329 */     buildAssociations(data);
/* 1373:     */   }
/* 1374:     */   
/* 1375:     */   public String toString()
/* 1376:     */   {
/* 1377:2341 */     if (this.m_rules == null) {
/* 1378:2342 */       return "FPGrowth hasn't been trained yet!";
/* 1379:     */     }
/* 1380:2345 */     StringBuffer result = new StringBuffer();
/* 1381:2346 */     int numRules = this.m_rules.size() < this.m_numRulesToFind ? this.m_rules.size() : this.m_numRulesToFind;
/* 1382:2349 */     if (this.m_rules.size() == 0) {
/* 1383:2350 */       return "No rules found!";
/* 1384:     */     }
/* 1385:2352 */     result.append("FPGrowth found " + this.m_rules.size() + " rules");
/* 1386:2353 */     if (!this.m_findAllRulesForSupportLevel) {
/* 1387:2354 */       result.append(" (displaying top " + numRules + ")");
/* 1388:     */     }
/* 1389:2357 */     if ((this.m_transactionsMustContain.length() > 0) || (this.m_rulesMustContain.length() > 0))
/* 1390:     */     {
/* 1391:2359 */       result.append("\n");
/* 1392:2360 */       if (this.m_transactionsMustContain.length() > 0) {
/* 1393:2361 */         result.append("\nUsing only transactions that contain: " + this.m_transactionsMustContain);
/* 1394:     */       }
/* 1395:2364 */       if (this.m_rulesMustContain.length() > 0) {
/* 1396:2365 */         result.append("\nShowing only rules that contain: " + this.m_rulesMustContain);
/* 1397:     */       }
/* 1398:     */     }
/* 1399:2370 */     result.append("\n\n");
/* 1400:     */     
/* 1401:     */ 
/* 1402:2373 */     int count = 0;
/* 1403:2374 */     for (AssociationRule r : this.m_rules)
/* 1404:     */     {
/* 1405:2375 */       result.append(Utils.doubleToString(count + 1.0D, (int)(Math.log(numRules) / Math.log(10.0D) + 1.0D), 0) + ". ");
/* 1406:     */       
/* 1407:     */ 
/* 1408:2378 */       result.append(r + "\n");
/* 1409:2379 */       count++;
/* 1410:2380 */       if ((!this.m_findAllRulesForSupportLevel) && (count == this.m_numRulesToFind)) {
/* 1411:     */         break;
/* 1412:     */       }
/* 1413:     */     }
/* 1414:2384 */     return result.toString();
/* 1415:     */   }
/* 1416:     */   
/* 1417:     */   public String graph(FPTreeRoot tree)
/* 1418:     */   {
/* 1419:2396 */     StringBuffer text = new StringBuffer();
/* 1420:2397 */     text.append("digraph FPTree {\n");
/* 1421:2398 */     text.append("N0 [label=\"ROOT\"]\n");
/* 1422:2399 */     tree.graphFPTree(text);
/* 1423:     */     
/* 1424:     */ 
/* 1425:2402 */     text.append("}\n");
/* 1426:     */     
/* 1427:2404 */     return text.toString();
/* 1428:     */   }
/* 1429:     */   
/* 1430:     */   public String getRevision()
/* 1431:     */   {
/* 1432:2414 */     return RevisionUtils.extract("$Revision: 10172 $");
/* 1433:     */   }
/* 1434:     */   
/* 1435:     */   public static void main(String[] args)
/* 1436:     */   {
/* 1437:     */     try
/* 1438:     */     {
/* 1439:2424 */       String[] argsCopy = (String[])args.clone();
/* 1440:2425 */       if ((Utils.getFlag('h', argsCopy)) || (Utils.getFlag("help", argsCopy)))
/* 1441:     */       {
/* 1442:2426 */         runAssociator(new FPGrowth(), args);
/* 1443:2427 */         System.out.println("-disk\n\tProcess data off of disk instead of loading\n\tinto main memory. This is a command line only option.");
/* 1444:     */         
/* 1445:     */ 
/* 1446:2430 */         return;
/* 1447:     */       }
/* 1448:2433 */       if (!Utils.getFlag("disk", args))
/* 1449:     */       {
/* 1450:2434 */         runAssociator(new FPGrowth(), args);
/* 1451:     */       }
/* 1452:     */       else
/* 1453:     */       {
/* 1454:2437 */         String filename = Utils.getOption('t', args);
/* 1455:2438 */         ArffLoader loader = null;
/* 1456:2439 */         if (filename.length() != 0)
/* 1457:     */         {
/* 1458:2440 */           loader = new ArffLoader();
/* 1459:2441 */           loader.setFile(new File(filename));
/* 1460:     */         }
/* 1461:     */         else
/* 1462:     */         {
/* 1463:2443 */           throw new Exception("No training file specified!");
/* 1464:     */         }
/* 1465:2445 */         FPGrowth fpGrowth = new FPGrowth();
/* 1466:2446 */         fpGrowth.setOptions(args);
/* 1467:2447 */         Utils.checkForRemainingOptions(args);
/* 1468:2448 */         fpGrowth.buildAssociations(loader);
/* 1469:2449 */         System.out.print(fpGrowth.toString());
/* 1470:     */       }
/* 1471:     */     }
/* 1472:     */     catch (Exception ex)
/* 1473:     */     {
/* 1474:2452 */       ex.printStackTrace();
/* 1475:     */     }
/* 1476:     */   }
/* 1477:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.associations.FPGrowth
 * JD-Core Version:    0.7.0.1
 */