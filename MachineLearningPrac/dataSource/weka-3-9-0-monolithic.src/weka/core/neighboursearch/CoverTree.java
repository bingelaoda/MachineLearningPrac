/*    1:     */ package weka.core.neighboursearch;
/*    2:     */ 
/*    3:     */ import java.io.BufferedReader;
/*    4:     */ import java.io.File;
/*    5:     */ import java.io.FileReader;
/*    6:     */ import java.io.PrintStream;
/*    7:     */ import java.io.Serializable;
/*    8:     */ import java.util.Collections;
/*    9:     */ import java.util.Enumeration;
/*   10:     */ import java.util.LinkedList;
/*   11:     */ import java.util.List;
/*   12:     */ import java.util.Vector;
/*   13:     */ import weka.core.DistanceFunction;
/*   14:     */ import weka.core.EuclideanDistance;
/*   15:     */ import weka.core.Instance;
/*   16:     */ import weka.core.Instances;
/*   17:     */ import weka.core.Option;
/*   18:     */ import weka.core.RevisionHandler;
/*   19:     */ import weka.core.RevisionUtils;
/*   20:     */ import weka.core.TechnicalInformation;
/*   21:     */ import weka.core.TechnicalInformation.Field;
/*   22:     */ import weka.core.TechnicalInformation.Type;
/*   23:     */ import weka.core.TechnicalInformationHandler;
/*   24:     */ import weka.core.Utils;
/*   25:     */ import weka.core.converters.CSVLoader;
/*   26:     */ import weka.core.neighboursearch.covertrees.Stack;
/*   27:     */ 
/*   28:     */ public class CoverTree
/*   29:     */   extends NearestNeighbourSearch
/*   30:     */   implements TechnicalInformationHandler
/*   31:     */ {
/*   32:     */   private static final long serialVersionUID = 7617412821497807586L;
/*   33:     */   protected EuclideanDistance m_EuclideanDistance;
/*   34:     */   protected CoverTreeNode m_Root;
/*   35:     */   protected double[] m_DistanceList;
/*   36:     */   protected int m_NumNodes;
/*   37:     */   protected int m_NumLeaves;
/*   38:     */   protected int m_MaxDepth;
/*   39:     */   protected TreePerformanceStats m_TreeStats;
/*   40:     */   protected double m_Base;
/*   41:     */   protected double il2;
/*   42:     */   
/*   43:     */   public class CoverTreeNode
/*   44:     */     implements Serializable, RevisionHandler
/*   45:     */   {
/*   46:     */     private static final long serialVersionUID = 1808760031169036512L;
/*   47:     */     private Integer idx;
/*   48:     */     private double max_dist;
/*   49:     */     private double parent_dist;
/*   50:     */     private Stack<CoverTreeNode> children;
/*   51:     */     private int num_children;
/*   52:     */     private int scale;
/*   53:     */     
/*   54:     */     public CoverTreeNode() {}
/*   55:     */     
/*   56:     */     public CoverTreeNode(double i, double arg4, Stack<CoverTreeNode> arg6, int childs, int numchilds)
/*   57:     */     {
/*   58: 148 */       this.idx = i;
/*   59: 149 */       this.max_dist = md;
/*   60: 150 */       this.parent_dist = pd;
/*   61: 151 */       this.children = childs;
/*   62: 152 */       this.num_children = numchilds;
/*   63: 153 */       this.scale = s;
/*   64:     */     }
/*   65:     */     
/*   66:     */     public Instance p()
/*   67:     */     {
/*   68: 162 */       return CoverTree.this.m_Instances.instance(this.idx.intValue());
/*   69:     */     }
/*   70:     */     
/*   71:     */     public boolean isALeaf()
/*   72:     */     {
/*   73: 171 */       return this.num_children == 0;
/*   74:     */     }
/*   75:     */     
/*   76:     */     public String getRevision()
/*   77:     */     {
/*   78: 181 */       return RevisionUtils.extract("$Revision: 10203 $");
/*   79:     */     }
/*   80:     */   }
/*   81:     */   
/*   82:     */   private class DistanceNode
/*   83:     */     implements RevisionHandler
/*   84:     */   {
/*   85:     */     Stack<Double> dist;
/*   86:     */     Integer idx;
/*   87:     */     
/*   88:     */     private DistanceNode() {}
/*   89:     */     
/*   90:     */     public Instance q()
/*   91:     */     {
/*   92: 209 */       return CoverTree.this.m_Instances.instance(this.idx.intValue());
/*   93:     */     }
/*   94:     */     
/*   95:     */     public String getRevision()
/*   96:     */     {
/*   97: 219 */       return RevisionUtils.extract("$Revision: 10203 $");
/*   98:     */     }
/*   99:     */   }
/*  100:     */   
/*  101:     */   public CoverTree()
/*  102:     */   {
/*  103: 226 */     if ((this.m_DistanceFunction instanceof EuclideanDistance)) {
/*  104: 227 */       this.m_EuclideanDistance = ((EuclideanDistance)this.m_DistanceFunction);
/*  105:     */     } else {
/*  106: 229 */       this.m_DistanceFunction = (this.m_EuclideanDistance = new EuclideanDistance());
/*  107:     */     }
/*  108: 246 */     this.m_TreeStats = null;
/*  109:     */     
/*  110:     */ 
/*  111:     */ 
/*  112:     */ 
/*  113:     */ 
/*  114:     */ 
/*  115: 253 */     this.m_Base = 1.3D;
/*  116:     */     
/*  117:     */ 
/*  118:     */ 
/*  119:     */ 
/*  120:     */ 
/*  121:     */ 
/*  122: 260 */     this.il2 = (1.0D / Math.log(this.m_Base));
/*  123: 267 */     if (getMeasurePerformance()) {
/*  124: 268 */       this.m_Stats = (this.m_TreeStats = new TreePerformanceStats());
/*  125:     */     }
/*  126:     */   }
/*  127:     */   
/*  128:     */   public String globalInfo()
/*  129:     */   {
/*  130: 280 */     return "Class implementing the CoverTree datastructure.\nThe class is very much a translation of the c source code made available by the authors.\n\nFor more information and original source code see:\n\n" + getTechnicalInformation().toString();
/*  131:     */   }
/*  132:     */   
/*  133:     */   public TechnicalInformation getTechnicalInformation()
/*  134:     */   {
/*  135: 298 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  136: 299 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Alina Beygelzimer and Sham Kakade and John Langford");
/*  137:     */     
/*  138: 301 */     result.setValue(TechnicalInformation.Field.TITLE, "Cover trees for nearest neighbor");
/*  139: 302 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "ICML'06: Proceedings of the 23rd international conference on Machine learning");
/*  140:     */     
/*  141:     */ 
/*  142: 305 */     result.setValue(TechnicalInformation.Field.PAGES, "97-104");
/*  143: 306 */     result.setValue(TechnicalInformation.Field.YEAR, "2006");
/*  144: 307 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "ACM Press");
/*  145: 308 */     result.setValue(TechnicalInformation.Field.ADDRESS, "New York, NY, USA");
/*  146: 309 */     result.setValue(TechnicalInformation.Field.LOCATION, "Pittsburgh, Pennsylvania");
/*  147: 310 */     result.setValue(TechnicalInformation.Field.HTTP, "http://hunch.net/~jl/projects/cover_tree/cover_tree.html");
/*  148:     */     
/*  149:     */ 
/*  150: 313 */     return result;
/*  151:     */   }
/*  152:     */   
/*  153:     */   public Enumeration<Option> listOptions()
/*  154:     */   {
/*  155: 323 */     Vector<Option> newVector = new Vector();
/*  156:     */     
/*  157: 325 */     newVector.addElement(new Option("\tSet base of the expansion constant\n\t(default = 1.3).", "B", 1, "-B <value>"));
/*  158:     */     
/*  159:     */ 
/*  160: 328 */     newVector.addAll(Collections.list(super.listOptions()));
/*  161:     */     
/*  162: 330 */     return newVector.elements();
/*  163:     */   }
/*  164:     */   
/*  165:     */   public void setOptions(String[] options)
/*  166:     */     throws Exception
/*  167:     */   {
/*  168: 354 */     super.setOptions(options);
/*  169:     */     
/*  170: 356 */     String optionString = Utils.getOption('B', options);
/*  171: 357 */     if (optionString.length() != 0) {
/*  172: 358 */       setBase(Double.parseDouble(optionString));
/*  173:     */     } else {
/*  174: 360 */       setBase(1.3D);
/*  175:     */     }
/*  176: 363 */     Utils.checkForRemainingOptions(options);
/*  177:     */   }
/*  178:     */   
/*  179:     */   public String[] getOptions()
/*  180:     */   {
/*  181: 373 */     Vector<String> result = new Vector();
/*  182:     */     
/*  183: 375 */     Collections.addAll(result, super.getOptions());
/*  184:     */     
/*  185: 377 */     result.add("-B");
/*  186: 378 */     result.add("" + getBase());
/*  187:     */     
/*  188: 380 */     return (String[])result.toArray(new String[result.size()]);
/*  189:     */   }
/*  190:     */   
/*  191:     */   protected double dist_of_scale(int s)
/*  192:     */   {
/*  193: 391 */     return Math.pow(this.m_Base, s);
/*  194:     */   }
/*  195:     */   
/*  196:     */   protected int get_scale(double d)
/*  197:     */   {
/*  198: 401 */     return (int)Math.ceil(this.il2 * Math.log(d));
/*  199:     */   }
/*  200:     */   
/*  201:     */   protected CoverTreeNode new_node(Integer idx)
/*  202:     */   {
/*  203: 411 */     CoverTreeNode new_node = new CoverTreeNode();
/*  204: 412 */     new_node.idx = idx;
/*  205: 413 */     return new_node;
/*  206:     */   }
/*  207:     */   
/*  208:     */   protected CoverTreeNode new_leaf(Integer idx)
/*  209:     */   {
/*  210: 423 */     CoverTreeNode new_leaf = new CoverTreeNode(idx, 0.0D, 0.0D, null, 0, 100);
/*  211: 424 */     return new_leaf;
/*  212:     */   }
/*  213:     */   
/*  214:     */   protected double max_set(Stack<DistanceNode> v)
/*  215:     */   {
/*  216: 436 */     double max = 0.0D;
/*  217: 437 */     for (int i = 0; i < v.length; i++)
/*  218:     */     {
/*  219: 438 */       DistanceNode n = (DistanceNode)v.element(i);
/*  220: 439 */       if (max < ((Double)n.dist.element(n.dist.length - 1)).floatValue()) {
/*  221: 440 */         max = ((Double)n.dist.element(n.dist.length - 1)).floatValue();
/*  222:     */       }
/*  223:     */     }
/*  224: 443 */     return max;
/*  225:     */   }
/*  226:     */   
/*  227:     */   protected void split(Stack<DistanceNode> point_set, Stack<DistanceNode> far_set, int max_scale)
/*  228:     */   {
/*  229: 463 */     int new_index = 0;
/*  230: 464 */     double fmax = dist_of_scale(max_scale);
/*  231: 465 */     for (int i = 0; i < point_set.length; i++)
/*  232:     */     {
/*  233: 466 */       DistanceNode n = (DistanceNode)point_set.element(i);
/*  234: 467 */       if (((Double)n.dist.element(n.dist.length - 1)).doubleValue() <= fmax) {
/*  235: 468 */         point_set.set(new_index++, point_set.element(i));
/*  236:     */       } else {
/*  237: 470 */         far_set.push(point_set.element(i));
/*  238:     */       }
/*  239:     */     }
/*  240: 473 */     List<DistanceNode> l = new LinkedList();
/*  241: 474 */     for (int i = 0; i < new_index; i++) {
/*  242: 475 */       l.add(point_set.element(i));
/*  243:     */     }
/*  244: 478 */     point_set.clear();
/*  245: 479 */     point_set.addAll(l);
/*  246:     */   }
/*  247:     */   
/*  248:     */   protected void dist_split(Stack<DistanceNode> point_set, Stack<DistanceNode> new_point_set, DistanceNode new_point, int max_scale)
/*  249:     */   {
/*  250: 496 */     int new_index = 0;
/*  251: 497 */     double fmax = dist_of_scale(max_scale);
/*  252: 498 */     for (int i = 0; i < point_set.length; i++)
/*  253:     */     {
/*  254: 499 */       double new_d = Math.sqrt(this.m_DistanceFunction.distance(new_point.q(), ((DistanceNode)point_set.element(i)).q(), fmax * fmax));
/*  255: 501 */       if (new_d <= fmax)
/*  256:     */       {
/*  257: 502 */         ((DistanceNode)point_set.element(i)).dist.push(Double.valueOf(new_d));
/*  258: 503 */         new_point_set.push(point_set.element(i));
/*  259:     */       }
/*  260:     */       else
/*  261:     */       {
/*  262: 505 */         point_set.set(new_index++, point_set.element(i));
/*  263:     */       }
/*  264:     */     }
/*  265: 508 */     List<DistanceNode> l = new LinkedList();
/*  266: 509 */     for (int i = 0; i < new_index; i++) {
/*  267: 510 */       l.add(point_set.element(i));
/*  268:     */     }
/*  269: 512 */     point_set.clear();
/*  270: 513 */     point_set.addAll(l);
/*  271:     */   }
/*  272:     */   
/*  273:     */   protected CoverTreeNode batch_insert(Integer p, int max_scale, int top_scale, Stack<DistanceNode> point_set, Stack<DistanceNode> consumed_set)
/*  274:     */   {
/*  275: 543 */     if (point_set.length == 0)
/*  276:     */     {
/*  277: 544 */       CoverTreeNode leaf = new_leaf(p);
/*  278: 545 */       this.m_NumNodes += 1;
/*  279: 546 */       this.m_NumLeaves += 1;
/*  280: 547 */       return leaf;
/*  281:     */     }
/*  282: 549 */     double max_dist = max_set(point_set);
/*  283:     */     
/*  284: 551 */     int next_scale = Math.min(max_scale - 1, get_scale(max_dist));
/*  285: 552 */     if (next_scale == -2147483648)
/*  286:     */     {
/*  287: 554 */       Stack<CoverTreeNode> children = new Stack();
/*  288: 555 */       CoverTreeNode leaf = new_leaf(p);
/*  289: 556 */       children.push(leaf);
/*  290: 557 */       this.m_NumLeaves += 1;
/*  291: 558 */       this.m_NumNodes += 1;
/*  292: 559 */       while (point_set.length > 0)
/*  293:     */       {
/*  294: 560 */         DistanceNode tmpnode = (DistanceNode)point_set.pop();
/*  295: 561 */         leaf = new_leaf(tmpnode.idx);
/*  296: 562 */         children.push(leaf);
/*  297: 563 */         this.m_NumLeaves += 1;
/*  298: 564 */         this.m_NumNodes += 1;
/*  299: 565 */         consumed_set.push(tmpnode);
/*  300:     */       }
/*  301: 567 */       CoverTreeNode n = new_node(p);
/*  302: 568 */       this.m_NumNodes += 1;
/*  303: 569 */       n.scale = 100;
/*  304: 570 */       n.max_dist = 0.0D;
/*  305: 571 */       n.num_children = children.length;
/*  306: 572 */       n.children = children;
/*  307: 573 */       return n;
/*  308:     */     }
/*  309: 575 */     Stack<DistanceNode> far = new Stack();
/*  310: 576 */     split(point_set, far, max_scale);
/*  311:     */     
/*  312: 578 */     CoverTreeNode child = batch_insert(p, next_scale, top_scale, point_set, consumed_set);
/*  313: 581 */     if (point_set.length == 0)
/*  314:     */     {
/*  315: 584 */       point_set.replaceAllBy(far);
/*  316: 585 */       return child;
/*  317:     */     }
/*  318: 587 */     CoverTreeNode n = new_node(p);
/*  319: 588 */     this.m_NumNodes += 1;
/*  320: 589 */     Stack<CoverTreeNode> children = new Stack();
/*  321: 590 */     children.push(child);
/*  322: 592 */     while (point_set.length != 0)
/*  323:     */     {
/*  324: 593 */       Stack<DistanceNode> new_point_set = new Stack();
/*  325: 594 */       Stack<DistanceNode> new_consumed_set = new Stack();
/*  326: 595 */       DistanceNode tmpnode = (DistanceNode)point_set.pop();
/*  327: 596 */       double new_dist = ((Double)tmpnode.dist.last()).doubleValue();
/*  328: 597 */       consumed_set.push(tmpnode);
/*  329:     */       
/*  330:     */ 
/*  331:     */ 
/*  332: 601 */       dist_split(point_set, new_point_set, tmpnode, max_scale);
/*  333:     */       
/*  334:     */ 
/*  335: 604 */       dist_split(far, new_point_set, tmpnode, max_scale);
/*  336:     */       
/*  337: 606 */       CoverTreeNode new_child = batch_insert(tmpnode.idx, next_scale, top_scale, new_point_set, new_consumed_set);
/*  338:     */       
/*  339: 608 */       new_child.parent_dist = new_dist;
/*  340:     */       
/*  341: 610 */       children.push(new_child);
/*  342:     */       
/*  343:     */ 
/*  344:     */ 
/*  345: 614 */       double fmax = dist_of_scale(max_scale);
/*  346: 615 */       tmpnode = null;
/*  347: 616 */       for (int i = 0; i < new_point_set.length; i++)
/*  348:     */       {
/*  349: 617 */         tmpnode = (DistanceNode)new_point_set.element(i);
/*  350: 618 */         tmpnode.dist.pop();
/*  351: 619 */         if (((Double)tmpnode.dist.last()).doubleValue() <= fmax) {
/*  352: 620 */           point_set.push(tmpnode);
/*  353:     */         } else {
/*  354: 622 */           far.push(tmpnode);
/*  355:     */         }
/*  356:     */       }
/*  357: 627 */       tmpnode = null;
/*  358: 628 */       for (int i = 0; i < new_consumed_set.length; i++)
/*  359:     */       {
/*  360: 629 */         tmpnode = (DistanceNode)new_consumed_set.element(i);
/*  361: 630 */         tmpnode.dist.pop();
/*  362: 631 */         consumed_set.push(tmpnode);
/*  363:     */       }
/*  364:     */     }
/*  365: 634 */     point_set.replaceAllBy(far);
/*  366: 635 */     n.scale = (top_scale - max_scale);
/*  367: 636 */     n.max_dist = max_set(consumed_set);
/*  368: 637 */     n.num_children = children.length;
/*  369: 638 */     n.children = children;
/*  370: 639 */     return n;
/*  371:     */   }
/*  372:     */   
/*  373:     */   protected void buildCoverTree(Instances insts)
/*  374:     */     throws Exception
/*  375:     */   {
/*  376: 654 */     if (insts.numInstances() == 0) {
/*  377: 655 */       throw new Exception("CoverTree: Empty set of instances. Cannot build tree.");
/*  378:     */     }
/*  379: 658 */     checkMissing(insts);
/*  380: 659 */     if (this.m_EuclideanDistance == null) {
/*  381: 660 */       this.m_DistanceFunction = (this.m_EuclideanDistance = new EuclideanDistance(insts));
/*  382:     */     } else {
/*  383: 662 */       this.m_EuclideanDistance.setInstances(insts);
/*  384:     */     }
/*  385: 665 */     Stack<DistanceNode> point_set = new Stack();
/*  386: 666 */     Stack<DistanceNode> consumed_set = new Stack();
/*  387:     */     
/*  388: 668 */     Instance point_p = insts.instance(0);
/*  389: 669 */     int p_idx = 0;
/*  390: 670 */     double max_dist = -1.0D;double dist = 0.0D;
/*  391: 672 */     for (int i = 1; i < insts.numInstances(); i++)
/*  392:     */     {
/*  393: 673 */       DistanceNode temp = new DistanceNode(null);
/*  394: 674 */       temp.dist = new Stack();
/*  395: 675 */       dist = Math.sqrt(this.m_DistanceFunction.distance(point_p, insts.instance(i), (1.0D / 0.0D)));
/*  396: 677 */       if (dist > max_dist)
/*  397:     */       {
/*  398: 678 */         max_dist = dist;
/*  399: 679 */         insts.instance(i);
/*  400:     */       }
/*  401: 681 */       temp.dist.push(Double.valueOf(dist));
/*  402: 682 */       temp.idx = Integer.valueOf(i);
/*  403: 683 */       point_set.push(temp);
/*  404:     */     }
/*  405: 686 */     max_dist = max_set(point_set);
/*  406: 687 */     this.m_Root = batch_insert(Integer.valueOf(p_idx), get_scale(max_dist), get_scale(max_dist), point_set, consumed_set);
/*  407:     */   }
/*  408:     */   
/*  409:     */   protected class MyHeap
/*  410:     */     implements RevisionHandler
/*  411:     */   {
/*  412: 704 */     CoverTree.MyHeapElement[] m_heap = null;
/*  413:     */     
/*  414:     */     public MyHeap(int maxSize)
/*  415:     */     {
/*  416: 712 */       if (maxSize % 2 == 0) {
/*  417: 713 */         maxSize++;
/*  418:     */       }
/*  419: 716 */       this.m_heap = new CoverTree.MyHeapElement[maxSize + 1];
/*  420: 717 */       this.m_heap[0] = new CoverTree.MyHeapElement(CoverTree.this, -1.0D);
/*  421:     */     }
/*  422:     */     
/*  423:     */     public int size()
/*  424:     */     {
/*  425: 726 */       return this.m_heap[0].index;
/*  426:     */     }
/*  427:     */     
/*  428:     */     public CoverTree.MyHeapElement peek()
/*  429:     */     {
/*  430: 735 */       return this.m_heap[1];
/*  431:     */     }
/*  432:     */     
/*  433:     */     public CoverTree.MyHeapElement get()
/*  434:     */       throws Exception
/*  435:     */     {
/*  436: 745 */       if (this.m_heap[0].index == 0) {
/*  437: 746 */         throw new Exception("No elements present in the heap");
/*  438:     */       }
/*  439: 748 */       CoverTree.MyHeapElement r = this.m_heap[1];
/*  440: 749 */       this.m_heap[1] = this.m_heap[this.m_heap[0].index];
/*  441: 750 */       this.m_heap[0].index -= 1;
/*  442: 751 */       downheap();
/*  443: 752 */       return r;
/*  444:     */     }
/*  445:     */     
/*  446:     */     public void put(double d)
/*  447:     */       throws Exception
/*  448:     */     {
/*  449: 762 */       if (this.m_heap[0].index + 1 > this.m_heap.length - 1) {
/*  450: 763 */         throw new Exception("the number of elements cannot exceed the initially set maximum limit");
/*  451:     */       }
/*  452: 766 */       this.m_heap[0].index += 1;
/*  453: 767 */       this.m_heap[this.m_heap[0].index] = new CoverTree.MyHeapElement(CoverTree.this, d);
/*  454: 768 */       upheap();
/*  455:     */     }
/*  456:     */     
/*  457:     */     public void putBySubstitute(double d)
/*  458:     */       throws Exception
/*  459:     */     {
/*  460: 778 */       CoverTree.MyHeapElement head = get();
/*  461: 779 */       put(d);
/*  462: 780 */       if (head.distance == this.m_heap[1].distance)
/*  463:     */       {
/*  464: 781 */         putKthNearest(head.distance);
/*  465:     */       }
/*  466: 782 */       else if (head.distance > this.m_heap[1].distance)
/*  467:     */       {
/*  468: 783 */         this.m_KthNearest = null;
/*  469: 784 */         this.m_KthNearestSize = 0;
/*  470: 785 */         this.initSize = 10;
/*  471:     */       }
/*  472: 786 */       else if (head.distance < this.m_heap[1].distance)
/*  473:     */       {
/*  474: 787 */         throw new Exception("The substituted element is greater than the head element. put() should have been called in place of putBySubstitute()");
/*  475:     */       }
/*  476:     */     }
/*  477:     */     
/*  478: 794 */     CoverTree.MyHeapElement[] m_KthNearest = null;
/*  479: 797 */     int m_KthNearestSize = 0;
/*  480: 800 */     int initSize = 10;
/*  481:     */     
/*  482:     */     public int noOfKthNearest()
/*  483:     */     {
/*  484: 809 */       return this.m_KthNearestSize;
/*  485:     */     }
/*  486:     */     
/*  487:     */     public void putKthNearest(double d)
/*  488:     */     {
/*  489: 818 */       if (this.m_KthNearest == null) {
/*  490: 819 */         this.m_KthNearest = new CoverTree.MyHeapElement[this.initSize];
/*  491:     */       }
/*  492: 821 */       if (this.m_KthNearestSize >= this.m_KthNearest.length)
/*  493:     */       {
/*  494: 822 */         this.initSize += this.initSize;
/*  495: 823 */         CoverTree.MyHeapElement[] temp = new CoverTree.MyHeapElement[this.initSize];
/*  496: 824 */         System.arraycopy(this.m_KthNearest, 0, temp, 0, this.m_KthNearest.length);
/*  497: 825 */         this.m_KthNearest = temp;
/*  498:     */       }
/*  499: 827 */       this.m_KthNearest[(this.m_KthNearestSize++)] = new CoverTree.MyHeapElement(CoverTree.this, d);
/*  500:     */     }
/*  501:     */     
/*  502:     */     public CoverTree.MyHeapElement getKthNearest()
/*  503:     */     {
/*  504: 836 */       if (this.m_KthNearestSize == 0) {
/*  505: 837 */         return null;
/*  506:     */       }
/*  507: 839 */       this.m_KthNearestSize -= 1;
/*  508: 840 */       return this.m_KthNearest[this.m_KthNearestSize];
/*  509:     */     }
/*  510:     */     
/*  511:     */     protected void upheap()
/*  512:     */     {
/*  513: 847 */       int i = this.m_heap[0].index;
/*  514: 849 */       while ((i > 1) && (this.m_heap[i].distance > this.m_heap[(i / 2)].distance))
/*  515:     */       {
/*  516: 850 */         CoverTree.MyHeapElement temp = this.m_heap[i];
/*  517: 851 */         this.m_heap[i] = this.m_heap[(i / 2)];
/*  518: 852 */         i /= 2;
/*  519: 853 */         this.m_heap[i] = temp;
/*  520:     */       }
/*  521:     */     }
/*  522:     */     
/*  523:     */     protected void downheap()
/*  524:     */     {
/*  525: 861 */       int i = 1;
/*  526: 864 */       while (((2 * i <= this.m_heap[0].index) && (this.m_heap[i].distance < this.m_heap[(2 * i)].distance)) || ((2 * i + 1 <= this.m_heap[0].index) && (this.m_heap[i].distance < this.m_heap[(2 * i + 1)].distance))) {
/*  527: 865 */         if (2 * i + 1 <= this.m_heap[0].index)
/*  528:     */         {
/*  529: 866 */           if (this.m_heap[(2 * i)].distance > this.m_heap[(2 * i + 1)].distance)
/*  530:     */           {
/*  531: 867 */             CoverTree.MyHeapElement temp = this.m_heap[i];
/*  532: 868 */             this.m_heap[i] = this.m_heap[(2 * i)];
/*  533: 869 */             i = 2 * i;
/*  534: 870 */             this.m_heap[i] = temp;
/*  535:     */           }
/*  536:     */           else
/*  537:     */           {
/*  538: 872 */             CoverTree.MyHeapElement temp = this.m_heap[i];
/*  539: 873 */             this.m_heap[i] = this.m_heap[(2 * i + 1)];
/*  540: 874 */             i = 2 * i + 1;
/*  541: 875 */             this.m_heap[i] = temp;
/*  542:     */           }
/*  543:     */         }
/*  544:     */         else
/*  545:     */         {
/*  546: 878 */           CoverTree.MyHeapElement temp = this.m_heap[i];
/*  547: 879 */           this.m_heap[i] = this.m_heap[(2 * i)];
/*  548: 880 */           i = 2 * i;
/*  549: 881 */           this.m_heap[i] = temp;
/*  550:     */         }
/*  551:     */       }
/*  552:     */     }
/*  553:     */     
/*  554:     */     public int totalSize()
/*  555:     */     {
/*  556: 892 */       return size() + noOfKthNearest();
/*  557:     */     }
/*  558:     */     
/*  559:     */     public String getRevision()
/*  560:     */     {
/*  561: 902 */       return RevisionUtils.extract("$Revision: 10203 $");
/*  562:     */     }
/*  563:     */   }
/*  564:     */   
/*  565:     */   protected class MyHeapElement
/*  566:     */     implements RevisionHandler
/*  567:     */   {
/*  568:     */     public double distance;
/*  569: 921 */     int index = 0;
/*  570:     */     
/*  571:     */     public MyHeapElement(double d)
/*  572:     */     {
/*  573: 929 */       this.distance = d;
/*  574:     */     }
/*  575:     */     
/*  576:     */     public String getRevision()
/*  577:     */     {
/*  578: 939 */       return RevisionUtils.extract("$Revision: 10203 $");
/*  579:     */     }
/*  580:     */   }
/*  581:     */   
/*  582:     */   private class d_node
/*  583:     */     implements RevisionHandler
/*  584:     */   {
/*  585:     */     double dist;
/*  586:     */     CoverTree.CoverTreeNode n;
/*  587:     */     
/*  588:     */     public d_node(double d, CoverTree.CoverTreeNode node)
/*  589:     */     {
/*  590: 964 */       this.dist = d;
/*  591: 965 */       this.n = node;
/*  592:     */     }
/*  593:     */     
/*  594:     */     public String getRevision()
/*  595:     */     {
/*  596: 975 */       return RevisionUtils.extract("$Revision: 10203 $");
/*  597:     */     }
/*  598:     */   }
/*  599:     */   
/*  600:     */   protected void setter(MyHeap heap, double upper_bound, int k)
/*  601:     */     throws Exception
/*  602:     */   {
/*  603: 992 */     if (heap.size() > 0) {
/*  604: 993 */       heap.m_heap[0].index = 0;
/*  605:     */     }
/*  606: 996 */     while (heap.size() < k) {
/*  607: 997 */       heap.put(upper_bound);
/*  608:     */     }
/*  609:     */   }
/*  610:     */   
/*  611:     */   protected void update(MyHeap upper_bound, double new_bound)
/*  612:     */     throws Exception
/*  613:     */   {
/*  614:1010 */     upper_bound.putBySubstitute(new_bound);
/*  615:     */   }
/*  616:     */   
/*  617:     */   protected Stack<d_node> getCoverSet(int idx, Stack<Stack<d_node>> cover_sets)
/*  618:     */   {
/*  619:1025 */     if (cover_sets.length <= idx)
/*  620:     */     {
/*  621:1026 */       int i = cover_sets.length - 1;
/*  622:1027 */       while (i < idx)
/*  623:     */       {
/*  624:1028 */         i++;
/*  625:1029 */         Stack<d_node> new_cover_set = new Stack();
/*  626:1030 */         cover_sets.push(new_cover_set);
/*  627:     */       }
/*  628:     */     }
/*  629:1033 */     return (Stack)cover_sets.element(idx);
/*  630:     */   }
/*  631:     */   
/*  632:     */   protected void copy_zero_set(CoverTreeNode query_chi, MyHeap new_upper_k, Stack<d_node> zero_set, Stack<d_node> new_zero_set)
/*  633:     */     throws Exception
/*  634:     */   {
/*  635:1054 */     new_zero_set.clear();
/*  636:1056 */     for (int i = 0; i < zero_set.length; i++)
/*  637:     */     {
/*  638:1057 */       d_node ele = (d_node)zero_set.element(i);
/*  639:1058 */       double upper_dist = new_upper_k.peek().distance + query_chi.max_dist;
/*  640:1059 */       if (shell(ele.dist, query_chi.parent_dist, upper_dist))
/*  641:     */       {
/*  642:1060 */         double d = Math.sqrt(this.m_DistanceFunction.distance(query_chi.p(), ele.n.p(), upper_dist * upper_dist));
/*  643:1062 */         if (this.m_TreeStats != null) {
/*  644:1063 */           this.m_TreeStats.incrPointCount();
/*  645:     */         }
/*  646:1065 */         if (d <= upper_dist)
/*  647:     */         {
/*  648:1066 */           if (d < new_upper_k.peek().distance) {
/*  649:1067 */             update(new_upper_k, d);
/*  650:     */           }
/*  651:1069 */           d_node temp = new d_node(d, ele.n);
/*  652:1070 */           new_zero_set.push(temp);
/*  653:1071 */           if (this.m_TreeStats != null) {
/*  654:1072 */             this.m_TreeStats.incrLeafCount();
/*  655:     */           }
/*  656:     */         }
/*  657:     */       }
/*  658:     */     }
/*  659:     */   }
/*  660:     */   
/*  661:     */   protected void copy_cover_sets(CoverTreeNode query_chi, MyHeap new_upper_k, Stack<Stack<d_node>> cover_sets, Stack<Stack<d_node>> new_cover_sets, int current_scale, int max_scale)
/*  662:     */     throws Exception
/*  663:     */   {
/*  664:1103 */     new_cover_sets.clear();
/*  665:1104 */     for (; current_scale <= max_scale; current_scale++)
/*  666:     */     {
/*  667:1106 */       Stack<d_node> cover_set_currentscale = getCoverSet(current_scale, cover_sets);
/*  668:1108 */       for (int i = 0; i < cover_set_currentscale.length; i++)
/*  669:     */       {
/*  670:1110 */         d_node ele = (d_node)cover_set_currentscale.element(i);
/*  671:1111 */         double upper_dist = new_upper_k.peek().distance + query_chi.max_dist + ele.n.max_dist;
/*  672:1113 */         if (shell(ele.dist, query_chi.parent_dist, upper_dist))
/*  673:     */         {
/*  674:1114 */           double d = Math.sqrt(this.m_DistanceFunction.distance(query_chi.p(), ele.n.p(), upper_dist * upper_dist));
/*  675:1116 */           if (this.m_TreeStats != null) {
/*  676:1117 */             this.m_TreeStats.incrPointCount();
/*  677:     */           }
/*  678:1119 */           if (d <= upper_dist)
/*  679:     */           {
/*  680:1120 */             if (d < new_upper_k.peek().distance) {
/*  681:1121 */               update(new_upper_k, d);
/*  682:     */             }
/*  683:1123 */             d_node temp = new d_node(d, ele.n);
/*  684:1124 */             ((Stack)new_cover_sets.element(current_scale)).push(temp);
/*  685:1125 */             if (this.m_TreeStats != null) {
/*  686:1126 */               this.m_TreeStats.incrIntNodeCount();
/*  687:     */             }
/*  688:     */           }
/*  689:     */         }
/*  690:     */       }
/*  691:     */     }
/*  692:     */   }
/*  693:     */   
/*  694:     */   void print_cover_sets(Stack<Stack<d_node>> cover_sets, Stack<d_node> zero_set, int current_scale, int max_scale)
/*  695:     */   {
/*  696:1145 */     println("cover set = ");
/*  697:1146 */     for (; current_scale <= max_scale; current_scale++)
/*  698:     */     {
/*  699:1147 */       println("" + current_scale);
/*  700:1148 */       for (int i = 0; i < ((Stack)cover_sets.element(current_scale)).length; i++)
/*  701:     */       {
/*  702:1149 */         d_node ele = (d_node)((Stack)cover_sets.element(current_scale)).element(i);
/*  703:1150 */         CoverTreeNode n = ele.n;
/*  704:1151 */         println(n.p());
/*  705:     */       }
/*  706:     */     }
/*  707:1154 */     println("infinity");
/*  708:1155 */     for (int i = 0; i < zero_set.length; i++)
/*  709:     */     {
/*  710:1156 */       d_node ele = (d_node)zero_set.element(i);
/*  711:1157 */       CoverTreeNode n = ele.n;
/*  712:1158 */       println(n.p());
/*  713:     */     }
/*  714:     */   }
/*  715:     */   
/*  716:     */   protected void SWAP(int a, int b, Stack<d_node> cover_set)
/*  717:     */   {
/*  718:1171 */     d_node tmp = (d_node)cover_set.element(a);
/*  719:1172 */     cover_set.set(a, cover_set.element(b));
/*  720:1173 */     cover_set.set(b, tmp);
/*  721:     */   }
/*  722:     */   
/*  723:     */   protected double compare(int p1, int p2, Stack<d_node> cover_set)
/*  724:     */   {
/*  725:1187 */     return ((d_node)cover_set.element(p1)).dist - ((d_node)cover_set.element(p2)).dist;
/*  726:     */   }
/*  727:     */   
/*  728:     */   protected void halfsort(Stack<d_node> cover_set)
/*  729:     */   {
/*  730:1197 */     if (cover_set.length <= 1) {
/*  731:1198 */       return;
/*  732:     */     }
/*  733:1200 */     int start = 0;
/*  734:1201 */     int hi = cover_set.length - 1;
/*  735:1202 */     int right = hi;
/*  736:1205 */     while (right > start)
/*  737:     */     {
/*  738:1206 */       int mid = start + (hi - start >> 1);
/*  739:     */       
/*  740:1208 */       boolean jumpover = false;
/*  741:1209 */       if (compare(mid, start, cover_set) < 0.0D) {
/*  742:1210 */         SWAP(mid, start, cover_set);
/*  743:     */       }
/*  744:1212 */       if (compare(hi, mid, cover_set) < 0.0D) {
/*  745:1213 */         SWAP(mid, hi, cover_set);
/*  746:     */       } else {
/*  747:1215 */         jumpover = true;
/*  748:     */       }
/*  749:1217 */       if ((!jumpover) && (compare(mid, start, cover_set) < 0.0D)) {
/*  750:1218 */         SWAP(mid, start, cover_set);
/*  751:     */       }
/*  752:1223 */       int left = start + 1;
/*  753:1224 */       right = hi - 1;
/*  754:     */       do
/*  755:     */       {
/*  756:1227 */         while (compare(left, mid, cover_set) < 0.0D) {
/*  757:1228 */           left++;
/*  758:     */         }
/*  759:1231 */         while (compare(mid, right, cover_set) < 0.0D) {
/*  760:1232 */           right--;
/*  761:     */         }
/*  762:1235 */         if (left < right)
/*  763:     */         {
/*  764:1236 */           SWAP(left, right, cover_set);
/*  765:1237 */           if (mid == left) {
/*  766:1238 */             mid = right;
/*  767:1239 */           } else if (mid == right) {
/*  768:1240 */             mid = left;
/*  769:     */           }
/*  770:1242 */           left++;
/*  771:1243 */           right--;
/*  772:     */         }
/*  773:1244 */         else if (left == right)
/*  774:     */         {
/*  775:1245 */           left++;
/*  776:1246 */           right--;
/*  777:1247 */           break;
/*  778:     */         }
/*  779:1249 */       } while (left <= right);
/*  780:1250 */       hi = right;
/*  781:     */     }
/*  782:     */   }
/*  783:     */   
/*  784:     */   protected boolean shell(double parent_query_dist, double child_parent_dist, double upper_bound)
/*  785:     */   {
/*  786:1267 */     return parent_query_dist - child_parent_dist <= upper_bound;
/*  787:     */   }
/*  788:     */   
/*  789:     */   protected int descend(CoverTreeNode query, MyHeap upper_k, int current_scale, int max_scale, Stack<Stack<d_node>> cover_sets, Stack<d_node> zero_set)
/*  790:     */     throws Exception
/*  791:     */   {
/*  792:1305 */     Stack<d_node> cover_set_currentscale = getCoverSet(current_scale, cover_sets);
/*  793:1307 */     for (int i = 0; i < cover_set_currentscale.length; i++)
/*  794:     */     {
/*  795:1308 */       d_node parent = (d_node)cover_set_currentscale.element(i);
/*  796:1309 */       CoverTreeNode par = parent.n;
/*  797:1310 */       double upper_dist = upper_k.peek().distance + query.max_dist + query.max_dist;
/*  798:1312 */       if (parent.dist <= upper_dist + par.max_dist)
/*  799:     */       {
/*  800:     */         CoverTreeNode chi;
/*  801:     */         CoverTreeNode chi;
/*  802:1314 */         if ((par == this.m_Root) && (par.num_children == 0)) {
/*  803:1317 */           chi = par;
/*  804:     */         } else {
/*  805:1319 */           chi = (CoverTreeNode)par.children.element(0);
/*  806:     */         }
/*  807:1321 */         if (parent.dist <= upper_dist + chi.max_dist) {
/*  808:1324 */           if (chi.num_children > 0)
/*  809:     */           {
/*  810:1325 */             if (max_scale < chi.scale) {
/*  811:1326 */               max_scale = chi.scale;
/*  812:     */             }
/*  813:1328 */             d_node temp = new d_node(parent.dist, chi);
/*  814:1329 */             getCoverSet(chi.scale, cover_sets).push(temp);
/*  815:1330 */             if (this.m_TreeStats != null) {
/*  816:1331 */               this.m_TreeStats.incrIntNodeCount();
/*  817:     */             }
/*  818:     */           }
/*  819:1333 */           else if (parent.dist <= upper_dist)
/*  820:     */           {
/*  821:1334 */             d_node temp = new d_node(parent.dist, chi);
/*  822:1335 */             zero_set.push(temp);
/*  823:1336 */             if (this.m_TreeStats != null) {
/*  824:1337 */               this.m_TreeStats.incrLeafCount();
/*  825:     */             }
/*  826:     */           }
/*  827:     */         }
/*  828:1341 */         for (int c = 1; c < par.num_children; c++)
/*  829:     */         {
/*  830:1342 */           chi = (CoverTreeNode)par.children.element(c);
/*  831:1343 */           double upper_chi = upper_k.peek().distance + chi.max_dist + query.max_dist + query.max_dist;
/*  832:1347 */           if (shell(parent.dist, chi.parent_dist, upper_chi))
/*  833:     */           {
/*  834:1357 */             double d = Math.sqrt(this.m_DistanceFunction.distance(query.p(), chi.p(), upper_chi * upper_chi, this.m_TreeStats));
/*  835:1359 */             if (this.m_TreeStats != null) {
/*  836:1360 */               this.m_TreeStats.incrPointCount();
/*  837:     */             }
/*  838:1362 */             if (d <= upper_chi)
/*  839:     */             {
/*  840:1363 */               if (d < upper_k.peek().distance) {
/*  841:1364 */                 update(upper_k, d);
/*  842:     */               }
/*  843:1366 */               if (chi.num_children > 0)
/*  844:     */               {
/*  845:1367 */                 if (max_scale < chi.scale) {
/*  846:1368 */                   max_scale = chi.scale;
/*  847:     */                 }
/*  848:1370 */                 d_node temp = new d_node(d, chi);
/*  849:1371 */                 getCoverSet(chi.scale, cover_sets).push(temp);
/*  850:1372 */                 if (this.m_TreeStats != null) {
/*  851:1373 */                   this.m_TreeStats.incrIntNodeCount();
/*  852:     */                 }
/*  853:     */               }
/*  854:1375 */               else if (d <= upper_chi - chi.max_dist)
/*  855:     */               {
/*  856:1376 */                 d_node temp = new d_node(d, chi);
/*  857:1377 */                 zero_set.push(temp);
/*  858:1378 */                 if (this.m_TreeStats != null) {
/*  859:1379 */                   this.m_TreeStats.incrLeafCount();
/*  860:     */                 }
/*  861:     */               }
/*  862:     */             }
/*  863:     */           }
/*  864:     */         }
/*  865:     */       }
/*  866:     */     }
/*  867:1387 */     return max_scale;
/*  868:     */   }
/*  869:     */   
/*  870:     */   protected void brute_nearest(int k, CoverTreeNode query, Stack<d_node> zero_set, MyHeap upper_k, Stack<NearestNeighbourSearch.NeighborList> results)
/*  871:     */     throws Exception
/*  872:     */   {
/*  873:1408 */     if (query.num_children > 0)
/*  874:     */     {
/*  875:1409 */       Stack<d_node> new_zero_set = new Stack();
/*  876:1410 */       CoverTreeNode query_chi = (CoverTreeNode)query.children.element(0);
/*  877:1411 */       brute_nearest(k, query_chi, zero_set, upper_k, results);
/*  878:1412 */       MyHeap new_upper_k = new MyHeap(k);
/*  879:1414 */       for (int i = 1; i < query.children.length; i++)
/*  880:     */       {
/*  881:1415 */         query_chi = (CoverTreeNode)query.children.element(i);
/*  882:1416 */         setter(new_upper_k, upper_k.peek().distance + query_chi.parent_dist, k);
/*  883:1417 */         copy_zero_set(query_chi, new_upper_k, zero_set, new_zero_set);
/*  884:1418 */         brute_nearest(k, query_chi, new_zero_set, new_upper_k, results);
/*  885:     */       }
/*  886:     */     }
/*  887:     */     else
/*  888:     */     {
/*  889:1421 */       NearestNeighbourSearch.NeighborList temp = new NearestNeighbourSearch.NeighborList(this, k);
/*  890:1423 */       for (int i = 0; i < zero_set.length; i++)
/*  891:     */       {
/*  892:1424 */         d_node ele = (d_node)zero_set.element(i);
/*  893:1425 */         if (ele.dist <= upper_k.peek().distance) {
/*  894:1426 */           temp.insertSorted(ele.dist, ele.n.p());
/*  895:     */         }
/*  896:     */       }
/*  897:1429 */       results.push(temp);
/*  898:     */     }
/*  899:     */   }
/*  900:     */   
/*  901:     */   protected void internal_batch_nearest_neighbor(int k, CoverTreeNode query_node, Stack<Stack<d_node>> cover_sets, Stack<d_node> zero_set, int current_scale, int max_scale, MyHeap upper_k, Stack<NearestNeighbourSearch.NeighborList> results)
/*  902:     */     throws Exception
/*  903:     */   {
/*  904:1458 */     if (current_scale > max_scale)
/*  905:     */     {
/*  906:1460 */       brute_nearest(k, query_node, zero_set, upper_k, results);
/*  907:     */     }
/*  908:1463 */     else if ((query_node.scale <= current_scale) && (query_node.scale != 100))
/*  909:     */     {
/*  910:1468 */       Stack<d_node> new_zero_set = new Stack();
/*  911:1469 */       Stack<Stack<d_node>> new_cover_sets = new Stack();
/*  912:1470 */       MyHeap new_upper_k = new MyHeap(k);
/*  913:1472 */       for (int i = 1; i < query_node.num_children; i++)
/*  914:     */       {
/*  915:1475 */         CoverTreeNode query_chi = (CoverTreeNode)query_node.children.element(i);
/*  916:1476 */         setter(new_upper_k, upper_k.peek().distance + query_chi.parent_dist, k);
/*  917:     */         
/*  918:     */ 
/*  919:1479 */         copy_zero_set(query_chi, new_upper_k, zero_set, new_zero_set);
/*  920:     */         
/*  921:     */ 
/*  922:1482 */         copy_cover_sets(query_chi, new_upper_k, cover_sets, new_cover_sets, current_scale, max_scale);
/*  923:     */         
/*  924:     */ 
/*  925:1485 */         internal_batch_nearest_neighbor(k, query_chi, new_cover_sets, new_zero_set, current_scale, max_scale, new_upper_k, results);
/*  926:     */       }
/*  927:1488 */       new_cover_sets = null;
/*  928:1489 */       new_zero_set = null;
/*  929:1490 */       new_upper_k = null;
/*  930:     */       
/*  931:     */ 
/*  932:1493 */       internal_batch_nearest_neighbor(k, (CoverTreeNode)query_node.children.element(0), cover_sets, zero_set, current_scale, max_scale, upper_k, results);
/*  933:     */     }
/*  934:     */     else
/*  935:     */     {
/*  936:1496 */       Stack<d_node> cover_set_i = getCoverSet(current_scale, cover_sets);
/*  937:     */       
/*  938:1498 */       halfsort(cover_set_i);
/*  939:1499 */       max_scale = descend(query_node, upper_k, current_scale, max_scale, cover_sets, zero_set);
/*  940:     */       
/*  941:1501 */       cover_set_i.clear();
/*  942:1502 */       current_scale++;
/*  943:1503 */       internal_batch_nearest_neighbor(k, query_node, cover_sets, zero_set, current_scale, max_scale, upper_k, results);
/*  944:     */     }
/*  945:     */   }
/*  946:     */   
/*  947:     */   protected void batch_nearest_neighbor(int k, CoverTreeNode tree_root, CoverTreeNode query_root, Stack<NearestNeighbourSearch.NeighborList> results)
/*  948:     */     throws Exception
/*  949:     */   {
/*  950:1523 */     Stack<Stack<d_node>> cover_sets = new Stack(100);
/*  951:     */     
/*  952:     */ 
/*  953:1526 */     Stack<d_node> zero_set = new Stack();
/*  954:1527 */     MyHeap upper_k = new MyHeap(k);
/*  955:     */     
/*  956:1529 */     setter(upper_k, (1.0D / 0.0D), k);
/*  957:     */     
/*  958:     */ 
/*  959:1532 */     double treeroot_to_query_dist = Math.sqrt(this.m_DistanceFunction.distance(query_root.p(), tree_root.p(), (1.0D / 0.0D)));
/*  960:     */     
/*  961:     */ 
/*  962:     */ 
/*  963:1536 */     update(upper_k, treeroot_to_query_dist);
/*  964:     */     
/*  965:1538 */     d_node temp = new d_node(treeroot_to_query_dist, tree_root);
/*  966:1539 */     getCoverSet(0, cover_sets).push(temp);
/*  967:1542 */     if (this.m_TreeStats != null)
/*  968:     */     {
/*  969:1543 */       this.m_TreeStats.incrPointCount();
/*  970:1544 */       if (tree_root.num_children > 0) {
/*  971:1545 */         this.m_TreeStats.incrIntNodeCount();
/*  972:     */       } else {
/*  973:1547 */         this.m_TreeStats.incrLeafCount();
/*  974:     */       }
/*  975:     */     }
/*  976:1551 */     internal_batch_nearest_neighbor(k, query_root, cover_sets, zero_set, 0, 0, upper_k, results);
/*  977:     */   }
/*  978:     */   
/*  979:     */   protected NearestNeighbourSearch.NeighborList findKNearest(Instance target, int k)
/*  980:     */     throws Exception
/*  981:     */   {
/*  982:1565 */     Stack<d_node> cover_set_current = new Stack();Stack<d_node> zero_set = new Stack();
/*  983:     */     
/*  984:     */ 
/*  985:1568 */     MyHeap upper_k = new MyHeap(k);
/*  986:1569 */     double d = Math.sqrt(this.m_DistanceFunction.distance(this.m_Root.p(), target, (1.0D / 0.0D), this.m_TreeStats));
/*  987:     */     
/*  988:1571 */     cover_set_current.push(new d_node(d, this.m_Root));
/*  989:1572 */     setter(upper_k, (1.0D / 0.0D), k);
/*  990:1573 */     update(upper_k, d);
/*  991:1575 */     if (this.m_TreeStats != null)
/*  992:     */     {
/*  993:1576 */       if (this.m_Root.num_children > 0) {
/*  994:1577 */         this.m_TreeStats.incrIntNodeCount();
/*  995:     */       } else {
/*  996:1579 */         this.m_TreeStats.incrLeafCount();
/*  997:     */       }
/*  998:1581 */       this.m_TreeStats.incrPointCount();
/*  999:     */     }
/* 1000:1585 */     if (this.m_Root.num_children == 0)
/* 1001:     */     {
/* 1002:1586 */       NearestNeighbourSearch.NeighborList list = new NearestNeighbourSearch.NeighborList(this, k);
/* 1003:1587 */       list.insertSorted(d, this.m_Root.p());
/* 1004:1588 */       return list;
/* 1005:     */     }
/* 1006:1591 */     while (cover_set_current.length > 0)
/* 1007:     */     {
/* 1008:1592 */       Stack<d_node> cover_set_next = new Stack();
/* 1009:1593 */       for (int i = 0; i < cover_set_current.length; i++)
/* 1010:     */       {
/* 1011:1594 */         d_node par = (d_node)cover_set_current.element(i);
/* 1012:1595 */         CoverTreeNode parent = par.n;
/* 1013:1596 */         for (int c = 0; c < parent.num_children; c++)
/* 1014:     */         {
/* 1015:1597 */           CoverTreeNode child = (CoverTreeNode)parent.children.element(c);
/* 1016:1598 */           double upper_bound = upper_k.peek().distance;
/* 1017:1599 */           if (c == 0)
/* 1018:     */           {
/* 1019:1600 */             d = par.dist;
/* 1020:     */           }
/* 1021:     */           else
/* 1022:     */           {
/* 1023:1602 */             d = upper_bound + child.max_dist;
/* 1024:1603 */             d = Math.sqrt(this.m_DistanceFunction.distance(child.p(), target, d * d, this.m_TreeStats));
/* 1025:1605 */             if (this.m_TreeStats != null) {
/* 1026:1606 */               this.m_TreeStats.incrPointCount();
/* 1027:     */             }
/* 1028:     */           }
/* 1029:1609 */           if (d <= upper_bound + child.max_dist)
/* 1030:     */           {
/* 1031:1610 */             if ((c > 0) && (d < upper_bound)) {
/* 1032:1611 */               update(upper_k, d);
/* 1033:     */             }
/* 1034:1613 */             if (child.num_children > 0)
/* 1035:     */             {
/* 1036:1614 */               cover_set_next.push(new d_node(d, child));
/* 1037:1615 */               if (this.m_TreeStats != null) {
/* 1038:1616 */                 this.m_TreeStats.incrIntNodeCount();
/* 1039:     */               }
/* 1040:     */             }
/* 1041:1618 */             else if (d <= upper_bound)
/* 1042:     */             {
/* 1043:1619 */               zero_set.push(new d_node(d, child));
/* 1044:1620 */               if (this.m_TreeStats != null) {
/* 1045:1621 */                 this.m_TreeStats.incrLeafCount();
/* 1046:     */               }
/* 1047:     */             }
/* 1048:     */           }
/* 1049:     */         }
/* 1050:     */       }
/* 1051:1627 */       cover_set_current = cover_set_next;
/* 1052:     */     }
/* 1053:1630 */     NearestNeighbourSearch.NeighborList list = new NearestNeighbourSearch.NeighborList(this, k);
/* 1054:     */     
/* 1055:1632 */     double upper_bound = upper_k.peek().distance;
/* 1056:1633 */     for (int i = 0; i < zero_set.length; i++)
/* 1057:     */     {
/* 1058:1634 */       d_node tmpnode = (d_node)zero_set.element(i);
/* 1059:1635 */       if (tmpnode.dist <= upper_bound) {
/* 1060:1636 */         list.insertSorted(tmpnode.dist, tmpnode.n.p());
/* 1061:     */       }
/* 1062:     */     }
/* 1063:1640 */     if (list.currentLength() <= 0) {
/* 1064:1641 */       throw new Exception("Error: No neighbour found. This cannot happen");
/* 1065:     */     }
/* 1066:1644 */     return list;
/* 1067:     */   }
/* 1068:     */   
/* 1069:     */   public Instances kNearestNeighbours(Instance target, int k)
/* 1070:     */     throws Exception
/* 1071:     */   {
/* 1072:1662 */     if (this.m_Stats != null) {
/* 1073:1663 */       this.m_Stats.searchStart();
/* 1074:     */     }
/* 1075:1665 */     CoverTree querytree = new CoverTree();
/* 1076:1666 */     Instances insts = new Instances(this.m_Instances, 0);
/* 1077:1667 */     insts.add(target);
/* 1078:1668 */     querytree.setInstances(insts);
/* 1079:1669 */     Stack<NearestNeighbourSearch.NeighborList> result = new Stack();
/* 1080:1670 */     batch_nearest_neighbor(k, this.m_Root, querytree.m_Root, result);
/* 1081:1671 */     if (this.m_Stats != null) {
/* 1082:1672 */       this.m_Stats.searchFinish();
/* 1083:     */     }
/* 1084:1675 */     insts = new Instances(this.m_Instances, 0);
/* 1085:1676 */     NearestNeighbourSearch.NeighborNode node = ((NearestNeighbourSearch.NeighborList)result.element(0)).getFirst();
/* 1086:1677 */     this.m_DistanceList = new double[((NearestNeighbourSearch.NeighborList)result.element(0)).currentLength()];
/* 1087:1678 */     int i = 0;
/* 1088:1679 */     while (node != null)
/* 1089:     */     {
/* 1090:1680 */       insts.add(node.m_Instance);
/* 1091:1681 */       this.m_DistanceList[i] = node.m_Distance;
/* 1092:1682 */       i++;
/* 1093:1683 */       node = node.m_Next;
/* 1094:     */     }
/* 1095:1685 */     return insts;
/* 1096:     */   }
/* 1097:     */   
/* 1098:     */   public Instance nearestNeighbour(Instance target)
/* 1099:     */     throws Exception
/* 1100:     */   {
/* 1101:1698 */     return kNearestNeighbours(target, 1).instance(0);
/* 1102:     */   }
/* 1103:     */   
/* 1104:     */   public double[] getDistances()
/* 1105:     */     throws Exception
/* 1106:     */   {
/* 1107:1712 */     if ((this.m_Instances == null) || (this.m_DistanceList == null)) {
/* 1108:1713 */       throw new Exception("The tree has not been supplied with a set of instances or getDistances() has been called before calling kNearestNeighbours().");
/* 1109:     */     }
/* 1110:1717 */     return this.m_DistanceList;
/* 1111:     */   }
/* 1112:     */   
/* 1113:     */   protected void checkMissing(Instances instances)
/* 1114:     */     throws Exception
/* 1115:     */   {
/* 1116:1728 */     for (int i = 0; i < instances.numInstances(); i++)
/* 1117:     */     {
/* 1118:1729 */       Instance ins = instances.instance(i);
/* 1119:1730 */       for (int j = 0; j < ins.numValues(); j++) {
/* 1120:1731 */         if ((ins.index(j) != ins.classIndex()) && 
/* 1121:1732 */           (ins.isMissingSparse(j))) {
/* 1122:1733 */           throw new Exception("ERROR: KDTree can not deal with missing values. Please run ReplaceMissingValues filter on the dataset before passing it on to the KDTree.");
/* 1123:     */         }
/* 1124:     */       }
/* 1125:     */     }
/* 1126:     */   }
/* 1127:     */   
/* 1128:     */   public void setInstances(Instances instances)
/* 1129:     */     throws Exception
/* 1130:     */   {
/* 1131:1750 */     super.setInstances(instances);
/* 1132:1751 */     buildCoverTree(instances);
/* 1133:     */   }
/* 1134:     */   
/* 1135:     */   public void update(Instance ins)
/* 1136:     */     throws Exception
/* 1137:     */   {
/* 1138:1764 */     throw new Exception("BottomUpConstruction method does not allow addition of new Instances.");
/* 1139:     */   }
/* 1140:     */   
/* 1141:     */   public void addInstanceInfo(Instance ins)
/* 1142:     */   {
/* 1143:1779 */     if (this.m_Instances != null) {
/* 1144:     */       try
/* 1145:     */       {
/* 1146:1781 */         this.m_DistanceFunction.update(ins);
/* 1147:     */       }
/* 1148:     */       catch (Exception ex)
/* 1149:     */       {
/* 1150:1783 */         ex.printStackTrace();
/* 1151:     */       }
/* 1152:1785 */     } else if (this.m_Instances == null) {
/* 1153:1786 */       throw new IllegalStateException("No instances supplied yet. Cannot update withoutsupplying a set of instances first.");
/* 1154:     */     }
/* 1155:     */   }
/* 1156:     */   
/* 1157:     */   public void setDistanceFunction(DistanceFunction df)
/* 1158:     */     throws Exception
/* 1159:     */   {
/* 1160:1801 */     if (!(df instanceof EuclideanDistance)) {
/* 1161:1802 */       throw new Exception("CoverTree currently only works with EuclideanDistanceFunction.");
/* 1162:     */     }
/* 1163:1805 */     this.m_DistanceFunction = (this.m_EuclideanDistance = (EuclideanDistance)df);
/* 1164:     */   }
/* 1165:     */   
/* 1166:     */   public String baseTipText()
/* 1167:     */   {
/* 1168:1815 */     return "The base for the expansion constant.";
/* 1169:     */   }
/* 1170:     */   
/* 1171:     */   public double getBase()
/* 1172:     */   {
/* 1173:1824 */     return this.m_Base;
/* 1174:     */   }
/* 1175:     */   
/* 1176:     */   public void setBase(double b)
/* 1177:     */   {
/* 1178:1833 */     this.m_Base = b;
/* 1179:     */   }
/* 1180:     */   
/* 1181:     */   public double measureTreeSize()
/* 1182:     */   {
/* 1183:1842 */     return this.m_NumNodes;
/* 1184:     */   }
/* 1185:     */   
/* 1186:     */   public double measureNumLeaves()
/* 1187:     */   {
/* 1188:1851 */     return this.m_NumLeaves;
/* 1189:     */   }
/* 1190:     */   
/* 1191:     */   public double measureMaxDepth()
/* 1192:     */   {
/* 1193:1860 */     return this.m_MaxDepth;
/* 1194:     */   }
/* 1195:     */   
/* 1196:     */   public Enumeration<String> enumerateMeasures()
/* 1197:     */   {
/* 1198:1870 */     Vector<String> newVector = new Vector();
/* 1199:1871 */     newVector.addElement("measureTreeSize");
/* 1200:1872 */     newVector.addElement("measureNumLeaves");
/* 1201:1873 */     newVector.addElement("measureMaxDepth");
/* 1202:1874 */     if (this.m_Stats != null) {
/* 1203:1875 */       newVector.addAll(Collections.list(this.m_Stats.enumerateMeasures()));
/* 1204:     */     }
/* 1205:1877 */     return newVector.elements();
/* 1206:     */   }
/* 1207:     */   
/* 1208:     */   public double getMeasure(String additionalMeasureName)
/* 1209:     */   {
/* 1210:1889 */     if (additionalMeasureName.compareToIgnoreCase("measureMaxDepth") == 0) {
/* 1211:1890 */       return measureMaxDepth();
/* 1212:     */     }
/* 1213:1891 */     if (additionalMeasureName.compareToIgnoreCase("measureTreeSize") == 0) {
/* 1214:1892 */       return measureTreeSize();
/* 1215:     */     }
/* 1216:1893 */     if (additionalMeasureName.compareToIgnoreCase("measureNumLeaves") == 0) {
/* 1217:1894 */       return measureNumLeaves();
/* 1218:     */     }
/* 1219:1895 */     if (this.m_Stats != null) {
/* 1220:1896 */       return this.m_Stats.getMeasure(additionalMeasureName);
/* 1221:     */     }
/* 1222:1898 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (KDTree)");
/* 1223:     */   }
/* 1224:     */   
/* 1225:     */   protected static void print(String s)
/* 1226:     */   {
/* 1227:1910 */     System.out.print(s);
/* 1228:     */   }
/* 1229:     */   
/* 1230:     */   protected static void println(String s)
/* 1231:     */   {
/* 1232:1919 */     System.out.println(s);
/* 1233:     */   }
/* 1234:     */   
/* 1235:     */   protected static void print(Object o)
/* 1236:     */   {
/* 1237:1928 */     System.out.print(o);
/* 1238:     */   }
/* 1239:     */   
/* 1240:     */   protected static void println(Object o)
/* 1241:     */   {
/* 1242:1937 */     System.out.println(o);
/* 1243:     */   }
/* 1244:     */   
/* 1245:     */   protected static void print_space(int s)
/* 1246:     */   {
/* 1247:1946 */     for (int i = 0; i < s; i++) {
/* 1248:1947 */       System.out.print(" ");
/* 1249:     */     }
/* 1250:     */   }
/* 1251:     */   
/* 1252:     */   protected static void print(int depth, CoverTreeNode top_node)
/* 1253:     */   {
/* 1254:1958 */     print_space(depth);
/* 1255:1959 */     println(top_node.p());
/* 1256:1960 */     if (top_node.num_children > 0)
/* 1257:     */     {
/* 1258:1961 */       print_space(depth);
/* 1259:1962 */       print("scale = " + top_node.scale + "\n");
/* 1260:1963 */       print_space(depth);
/* 1261:1964 */       print("num children = " + top_node.num_children + "\n");
/* 1262:1965 */       System.out.flush();
/* 1263:1966 */       for (int i = 0; i < top_node.num_children; i++) {
/* 1264:1967 */         print(depth + 1, (CoverTreeNode)top_node.children.element(i));
/* 1265:     */       }
/* 1266:     */     }
/* 1267:     */   }
/* 1268:     */   
/* 1269:     */   public String getRevision()
/* 1270:     */   {
/* 1271:1979 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 1272:     */   }
/* 1273:     */   
/* 1274:     */   public static void main(String[] args)
/* 1275:     */   {
/* 1276:1988 */     if (args.length != 1)
/* 1277:     */     {
/* 1278:1989 */       System.err.println("Usage: CoverTree <ARFF file>");
/* 1279:1990 */       System.exit(-1);
/* 1280:     */     }
/* 1281:     */     try
/* 1282:     */     {
/* 1283:1993 */       Instances insts = null;
/* 1284:1994 */       if (args[0].endsWith(".csv"))
/* 1285:     */       {
/* 1286:1995 */         CSVLoader csv = new CSVLoader();
/* 1287:1996 */         csv.setFile(new File(args[0]));
/* 1288:1997 */         insts = csv.getDataSet();
/* 1289:     */       }
/* 1290:     */       else
/* 1291:     */       {
/* 1292:1999 */         insts = new Instances(new BufferedReader(new FileReader(args[0])));
/* 1293:     */       }
/* 1294:2002 */       CoverTree tree = new CoverTree();
/* 1295:2003 */       tree.setInstances(insts);
/* 1296:2004 */       print("Created data tree:\n");
/* 1297:2005 */       print(0, tree.m_Root);
/* 1298:2006 */       println("");
/* 1299:     */     }
/* 1300:     */     catch (Exception ex)
/* 1301:     */     {
/* 1302:2008 */       ex.printStackTrace();
/* 1303:     */     }
/* 1304:     */   }
/* 1305:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.CoverTree
 * JD-Core Version:    0.7.0.1
 */