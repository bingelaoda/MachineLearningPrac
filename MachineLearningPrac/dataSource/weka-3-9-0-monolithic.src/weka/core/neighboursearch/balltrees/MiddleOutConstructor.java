/*    1:     */ package weka.core.neighboursearch.balltrees;
/*    2:     */ 
/*    3:     */ import java.io.Serializable;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.Iterator;
/*    8:     */ import java.util.Random;
/*    9:     */ import java.util.Vector;
/*   10:     */ import weka.core.DenseInstance;
/*   11:     */ import weka.core.DistanceFunction;
/*   12:     */ import weka.core.Instance;
/*   13:     */ import weka.core.Instances;
/*   14:     */ import weka.core.Option;
/*   15:     */ import weka.core.Randomizable;
/*   16:     */ import weka.core.RevisionHandler;
/*   17:     */ import weka.core.RevisionUtils;
/*   18:     */ import weka.core.TechnicalInformation;
/*   19:     */ import weka.core.TechnicalInformation.Field;
/*   20:     */ import weka.core.TechnicalInformation.Type;
/*   21:     */ import weka.core.TechnicalInformationHandler;
/*   22:     */ import weka.core.Utils;
/*   23:     */ 
/*   24:     */ public class MiddleOutConstructor
/*   25:     */   extends BallTreeConstructor
/*   26:     */   implements Randomizable, TechnicalInformationHandler
/*   27:     */ {
/*   28:     */   private static final long serialVersionUID = -8523314263062524462L;
/*   29: 110 */   protected int m_RSeed = 1;
/*   30: 116 */   protected Random rand = new Random(this.m_RSeed);
/*   31: 121 */   private double rootRadius = -1.0D;
/*   32: 127 */   protected boolean m_RandomInitialAnchor = true;
/*   33:     */   
/*   34:     */   public String globalInfo()
/*   35:     */   {
/*   36: 142 */     return "The class that builds a BallTree middle out.\n\nFor more information see also:\n\n" + getTechnicalInformation().toString();
/*   37:     */   }
/*   38:     */   
/*   39:     */   public TechnicalInformation getTechnicalInformation()
/*   40:     */   {
/*   41: 159 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*   42: 160 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Andrew W. Moore");
/*   43: 161 */     result.setValue(TechnicalInformation.Field.TITLE, "The Anchors Hierarchy: Using the Triangle Inequality to Survive High Dimensional Data");
/*   44:     */     
/*   45:     */ 
/*   46:     */ 
/*   47: 165 */     result.setValue(TechnicalInformation.Field.YEAR, "2000");
/*   48: 166 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "UAI '00: Proceedings of the 16th Conference on Uncertainty in Artificial Intelligence");
/*   49:     */     
/*   50:     */ 
/*   51:     */ 
/*   52: 170 */     result.setValue(TechnicalInformation.Field.PAGES, "397-405");
/*   53: 171 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann Publishers Inc.");
/*   54: 172 */     result.setValue(TechnicalInformation.Field.ADDRESS, "San Francisco, CA, USA");
/*   55:     */     
/*   56: 174 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.MASTERSTHESIS);
/*   57: 175 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Ashraf Masood Kibriya");
/*   58: 176 */     additional.setValue(TechnicalInformation.Field.TITLE, "Fast Algorithms for Nearest Neighbour Search");
/*   59:     */     
/*   60: 178 */     additional.setValue(TechnicalInformation.Field.YEAR, "2007");
/*   61: 179 */     additional.setValue(TechnicalInformation.Field.SCHOOL, "Department of Computer Science, School of Computing and Mathematical Sciences, University of Waikato");
/*   62:     */     
/*   63:     */ 
/*   64:     */ 
/*   65: 183 */     additional.setValue(TechnicalInformation.Field.ADDRESS, "Hamilton, New Zealand");
/*   66:     */     
/*   67: 185 */     return result;
/*   68:     */   }
/*   69:     */   
/*   70:     */   public BallNode buildTree()
/*   71:     */     throws Exception
/*   72:     */   {
/*   73: 196 */     this.m_NumNodes = (this.m_MaxDepth = this.m_NumLeaves = 0);
/*   74: 197 */     if (this.rootRadius == -1.0D) {
/*   75: 198 */       this.rootRadius = BallNode.calcRadius(this.m_InstList, this.m_Instances, BallNode.calcCentroidPivot(this.m_InstList, this.m_Instances), this.m_DistanceFunction);
/*   76:     */     }
/*   77: 203 */     BallNode root = buildTreeMiddleOut(0, this.m_Instances.numInstances() - 1);
/*   78: 204 */     return root;
/*   79:     */   }
/*   80:     */   
/*   81:     */   protected BallNode buildTreeMiddleOut(int startIdx, int endIdx)
/*   82:     */     throws Exception
/*   83:     */   {
/*   84: 222 */     int numInsts = endIdx - startIdx + 1;
/*   85: 223 */     int numAnchors = (int)Math.round(Math.sqrt(numInsts));
/*   86: 226 */     if (numAnchors > 1)
/*   87:     */     {
/*   88: 227 */       Instance pivot = BallNode.calcCentroidPivot(startIdx, endIdx, this.m_InstList, this.m_Instances);
/*   89:     */       
/*   90: 229 */       double radius = BallNode.calcRadius(startIdx, endIdx, this.m_InstList, this.m_Instances, pivot, this.m_DistanceFunction);
/*   91: 231 */       if ((numInsts <= this.m_MaxInstancesInLeaf) || (this.rootRadius == 0.0D) || (radius / this.rootRadius < this.m_MaxRelLeafRadius))
/*   92:     */       {
/*   93: 240 */         BallNode node = new BallNode(startIdx, endIdx, this.m_NumNodes, pivot, radius);
/*   94:     */         
/*   95: 242 */         return node;
/*   96:     */       }
/*   97: 244 */       Vector<TempNode> anchors = new Vector(numAnchors);
/*   98: 245 */       createAnchorsHierarchy(anchors, numAnchors, startIdx, endIdx);
/*   99:     */       
/*  100: 247 */       BallNode node = mergeNodes(anchors, startIdx, endIdx);
/*  101:     */       
/*  102: 249 */       buildLeavesMiddleOut(node);
/*  103:     */       
/*  104: 251 */       return node;
/*  105:     */     }
/*  106:     */     Instance pivot;
/*  107: 254 */     BallNode node = new BallNode(startIdx, endIdx, this.m_NumNodes, pivot = BallNode.calcCentroidPivot(startIdx, endIdx, this.m_InstList, this.m_Instances), BallNode.calcRadius(startIdx, endIdx, this.m_InstList, this.m_Instances, pivot, this.m_DistanceFunction));
/*  108:     */     
/*  109:     */ 
/*  110:     */ 
/*  111: 258 */     return node;
/*  112:     */   }
/*  113:     */   
/*  114:     */   protected void createAnchorsHierarchy(Vector<TempNode> anchors, int numAnchors, int startIdx, int endIdx)
/*  115:     */     throws Exception
/*  116:     */   {
/*  117: 275 */     TempNode anchr1 = this.m_RandomInitialAnchor ? getRandomAnchor(startIdx, endIdx) : getFurthestFromMeanAnchor(startIdx, endIdx);
/*  118:     */     
/*  119:     */ 
/*  120: 278 */     TempNode amax = anchr1;
/*  121:     */     
/*  122: 280 */     Vector<double[]> anchorDistances = new Vector(numAnchors - 1);
/*  123: 281 */     anchors.add(anchr1);
/*  124: 284 */     while (anchors.size() < numAnchors)
/*  125:     */     {
/*  126: 286 */       TempNode newAnchor = new TempNode();
/*  127: 287 */       newAnchor.points = new MyIdxList();
/*  128: 288 */       Instance newpivot = this.m_Instances.instance(amax.points.getFirst().idx);
/*  129: 289 */       newAnchor.anchor = newpivot;
/*  130: 290 */       newAnchor.idx = amax.points.getFirst().idx;
/*  131:     */       
/*  132: 292 */       setInterAnchorDistances(anchors, newAnchor, anchorDistances);
/*  133: 293 */       if (stealPoints(newAnchor, anchors, anchorDistances)) {
/*  134: 294 */         newAnchor.radius = newAnchor.points.getFirst().distance;
/*  135:     */       } else {
/*  136: 296 */         newAnchor.radius = 0.0D;
/*  137:     */       }
/*  138: 298 */       anchors.add(newAnchor);
/*  139:     */       
/*  140:     */ 
/*  141: 301 */       amax = (TempNode)anchors.elementAt(0);
/*  142: 302 */       for (int i = 1; i < anchors.size(); i++)
/*  143:     */       {
/*  144: 303 */         newAnchor = (TempNode)anchors.elementAt(i);
/*  145: 304 */         if (newAnchor.radius > amax.radius) {
/*  146: 305 */           amax = newAnchor;
/*  147:     */         }
/*  148:     */       }
/*  149:     */     }
/*  150:     */   }
/*  151:     */   
/*  152:     */   protected void buildLeavesMiddleOut(BallNode node)
/*  153:     */     throws Exception
/*  154:     */   {
/*  155: 322 */     if ((node.m_Left != null) && (node.m_Right != null))
/*  156:     */     {
/*  157: 323 */       buildLeavesMiddleOut(node.m_Left);
/*  158: 324 */       buildLeavesMiddleOut(node.m_Right);
/*  159:     */     }
/*  160:     */     else
/*  161:     */     {
/*  162: 325 */       if ((node.m_Left != null) || (node.m_Right != null)) {
/*  163: 326 */         throw new Exception("Invalid leaf assignment. Please check code");
/*  164:     */       }
/*  165: 328 */       BallNode n2 = buildTreeMiddleOut(node.m_Start, node.m_End);
/*  166: 329 */       if ((n2.m_Left != null) && (n2.m_Right != null))
/*  167:     */       {
/*  168: 330 */         node.m_Left = n2.m_Left;
/*  169: 331 */         node.m_Right = n2.m_Right;
/*  170: 332 */         buildLeavesMiddleOut(node);
/*  171:     */       }
/*  172: 335 */       else if ((n2.m_Left != null) || (n2.m_Right != null))
/*  173:     */       {
/*  174: 336 */         throw new Exception("Invalid leaf assignment. Please check code");
/*  175:     */       }
/*  176:     */     }
/*  177:     */   }
/*  178:     */   
/*  179:     */   protected BallNode mergeNodes(Vector<TempNode> list, int startIdx, int endIdx)
/*  180:     */     throws Exception
/*  181:     */   {
/*  182: 355 */     for (int i = 0; i < list.size(); i++)
/*  183:     */     {
/*  184: 356 */       TempNode n = (TempNode)list.get(i);
/*  185: 357 */       n.anchor = calcPivot(n.points, new MyIdxList(), this.m_Instances);
/*  186: 358 */       n.radius = calcRadius(n.points, new MyIdxList(), n.anchor, this.m_Instances);
/*  187:     */     }
/*  188: 361 */     Instance minPivot = null;
/*  189:     */     
/*  190: 363 */     int min1 = -1;int min2 = -1;
/*  191: 365 */     while (list.size() > 1)
/*  192:     */     {
/*  193: 366 */       double minRadius = (1.0D / 0.0D);
/*  194: 368 */       for (int i = 0; i < list.size(); i++)
/*  195:     */       {
/*  196: 369 */         TempNode first = (TempNode)list.get(i);
/*  197: 370 */         for (int j = i + 1; j < list.size(); j++)
/*  198:     */         {
/*  199: 371 */           TempNode second = (TempNode)list.get(j);
/*  200: 372 */           Instance pivot = calcPivot(first, second, this.m_Instances);
/*  201: 373 */           double tmpRadius = calcRadius(first, second);
/*  202: 376 */           if (tmpRadius < minRadius)
/*  203:     */           {
/*  204: 377 */             minRadius = tmpRadius;
/*  205: 378 */             minPivot = pivot;
/*  206: 379 */             min1 = i;
/*  207: 380 */             min2 = j;
/*  208:     */           }
/*  209:     */         }
/*  210:     */       }
/*  211: 385 */       TempNode parent = new TempNode();
/*  212: 386 */       parent.left = ((TempNode)list.get(min1));
/*  213: 387 */       parent.right = ((TempNode)list.get(min2));
/*  214: 388 */       parent.anchor = minPivot;
/*  215: 389 */       parent.radius = calcRadius(parent.left.points, parent.right.points, minPivot, this.m_Instances);
/*  216:     */       
/*  217: 391 */       parent.points = parent.left.points.append(parent.left.points, parent.right.points);
/*  218:     */       
/*  219: 393 */       list.remove(min1);
/*  220: 394 */       list.remove(min2 - 1);
/*  221: 395 */       list.add(parent);
/*  222:     */     }
/*  223: 397 */     TempNode tmpRoot = (TempNode)list.get(list.size() - 1);
/*  224: 399 */     if (endIdx - startIdx + 1 != tmpRoot.points.length()) {
/*  225: 400 */       throw new Exception("Root nodes instance list is of irregular length. Please check code. Length should be: " + (endIdx - startIdx + 1) + " whereas it is found to be: " + tmpRoot.points.length());
/*  226:     */     }
/*  227: 405 */     for (int i = 0; i < tmpRoot.points.length(); i++) {
/*  228: 406 */       this.m_InstList[(startIdx + i)] = tmpRoot.points.get(i).idx;
/*  229:     */     }
/*  230: 409 */     BallNode node = makeBallTreeNodes(tmpRoot, startIdx, endIdx, 0);
/*  231:     */     
/*  232: 411 */     return node;
/*  233:     */   }
/*  234:     */   
/*  235:     */   protected BallNode makeBallTreeNodes(TempNode node, int startidx, int endidx, int depth)
/*  236:     */   {
/*  237: 428 */     BallNode ball = null;
/*  238: 430 */     if ((node.left != null) && (node.right != null))
/*  239:     */     {
/*  240: 431 */       ball = new BallNode(startidx, endidx, this.m_NumNodes, node.anchor, node.radius);
/*  241:     */       
/*  242: 433 */       this.m_NumNodes += 1;
/*  243: 434 */       ball.m_Left = makeBallTreeNodes(node.left, startidx, startidx + node.left.points.length() - 1, depth + 1);
/*  244:     */       
/*  245: 436 */       ball.m_Right = makeBallTreeNodes(node.right, startidx + node.left.points.length(), endidx, depth + 1);
/*  246:     */       
/*  247: 438 */       this.m_MaxDepth += 1;
/*  248:     */     }
/*  249:     */     else
/*  250:     */     {
/*  251: 440 */       ball = new BallNode(startidx, endidx, this.m_NumNodes, node.anchor, node.radius);
/*  252:     */       
/*  253: 442 */       this.m_NumNodes += 1;
/*  254: 443 */       this.m_NumLeaves += 1;
/*  255:     */     }
/*  256: 445 */     return ball;
/*  257:     */   }
/*  258:     */   
/*  259:     */   protected TempNode getFurthestFromMeanAnchor(int startIdx, int endIdx)
/*  260:     */   {
/*  261: 460 */     TempNode anchor = new TempNode();
/*  262: 461 */     Instance centroid = BallNode.calcCentroidPivot(startIdx, endIdx, this.m_InstList, this.m_Instances);
/*  263:     */     
/*  264:     */ 
/*  265:     */ 
/*  266: 465 */     anchor.radius = (-1.0D / 0.0D);
/*  267: 466 */     for (int i = startIdx; i <= endIdx; i++)
/*  268:     */     {
/*  269: 467 */       Instance temp = this.m_Instances.instance(this.m_InstList[i]);
/*  270: 468 */       double tmpr = this.m_DistanceFunction.distance(centroid, temp);
/*  271: 469 */       if (tmpr > anchor.radius)
/*  272:     */       {
/*  273: 470 */         anchor.idx = this.m_InstList[i];
/*  274: 471 */         anchor.anchor = temp;
/*  275: 472 */         anchor.radius = tmpr;
/*  276:     */       }
/*  277:     */     }
/*  278: 476 */     setPoints(anchor, startIdx, endIdx, this.m_InstList);
/*  279: 477 */     return anchor;
/*  280:     */   }
/*  281:     */   
/*  282:     */   protected TempNode getRandomAnchor(int startIdx, int endIdx)
/*  283:     */   {
/*  284: 489 */     TempNode anchr1 = new TempNode();
/*  285: 490 */     anchr1.idx = this.m_InstList[(startIdx + this.rand.nextInt(endIdx - startIdx + 1))];
/*  286: 491 */     anchr1.anchor = this.m_Instances.instance(anchr1.idx);
/*  287: 492 */     setPoints(anchr1, startIdx, endIdx, this.m_InstList);
/*  288: 493 */     anchr1.radius = anchr1.points.getFirst().distance;
/*  289:     */     
/*  290: 495 */     return anchr1;
/*  291:     */   }
/*  292:     */   
/*  293:     */   public void setPoints(TempNode node, int startIdx, int endIdx, int[] indices)
/*  294:     */   {
/*  295: 511 */     node.points = new MyIdxList();
/*  296: 514 */     for (int i = startIdx; i <= endIdx; i++)
/*  297:     */     {
/*  298: 515 */       Instance temp = this.m_Instances.instance(indices[i]);
/*  299: 516 */       double dist = this.m_DistanceFunction.distance(node.anchor, temp);
/*  300: 517 */       node.points.insertReverseSorted(indices[i], dist);
/*  301:     */     }
/*  302:     */   }
/*  303:     */   
/*  304:     */   public void setInterAnchorDistances(Vector<TempNode> anchors, TempNode newAnchor, Vector<double[]> anchorDistances)
/*  305:     */     throws Exception
/*  306:     */   {
/*  307: 533 */     double[] distArray = new double[anchors.size()];
/*  308: 535 */     for (int i = 0; i < anchors.size(); i++)
/*  309:     */     {
/*  310: 536 */       Instance anchr = ((TempNode)anchors.elementAt(i)).anchor;
/*  311: 537 */       distArray[i] = this.m_DistanceFunction.distance(anchr, newAnchor.anchor);
/*  312:     */     }
/*  313: 539 */     anchorDistances.add(distArray);
/*  314:     */   }
/*  315:     */   
/*  316:     */   public boolean stealPoints(TempNode newAnchor, Vector<TempNode> anchors, Vector<double[]> anchorDistances)
/*  317:     */   {
/*  318: 555 */     double maxDist = (-1.0D / 0.0D);
/*  319: 556 */     double[] distArray = (double[])anchorDistances.lastElement();
/*  320: 558 */     for (double element : distArray) {
/*  321: 559 */       if (maxDist < element) {
/*  322: 560 */         maxDist = element;
/*  323:     */       }
/*  324:     */     }
/*  325: 564 */     boolean anyPointsStolen = false;boolean pointsStolen = false;
/*  326:     */     
/*  327:     */ 
/*  328: 567 */     Instance newAnchInst = newAnchor.anchor;
/*  329: 568 */     for (int i = 0; i < anchors.size(); i++)
/*  330:     */     {
/*  331: 569 */       TempNode anchorI = (TempNode)anchors.elementAt(i);
/*  332: 570 */       Instance anchIInst = anchorI.anchor;
/*  333:     */       
/*  334: 572 */       pointsStolen = false;
/*  335: 573 */       double interAnchMidDist = this.m_DistanceFunction.distance(newAnchInst, anchIInst) / 2.0D;
/*  336: 574 */       for (int j = 0; j < anchorI.points.length(); j++)
/*  337:     */       {
/*  338: 575 */         ListNode tmp = anchorI.points.get(j);
/*  339: 578 */         if (tmp.distance < interAnchMidDist) {
/*  340:     */           break;
/*  341:     */         }
/*  342: 582 */         double newDist = this.m_DistanceFunction.distance(newAnchInst, this.m_Instances.instance(tmp.idx));
/*  343:     */         
/*  344: 584 */         double distI = tmp.distance;
/*  345: 585 */         if (newDist < distI)
/*  346:     */         {
/*  347: 586 */           newAnchor.points.insertReverseSorted(tmp.idx, newDist);
/*  348: 587 */           anchorI.points.remove(j);
/*  349: 588 */           anyPointsStolen = pointsStolen = 1;
/*  350:     */         }
/*  351:     */       }
/*  352: 591 */       if (pointsStolen) {
/*  353: 592 */         anchorI.radius = anchorI.points.getFirst().distance;
/*  354:     */       }
/*  355:     */     }
/*  356: 595 */     return anyPointsStolen;
/*  357:     */   }
/*  358:     */   
/*  359:     */   public Instance calcPivot(TempNode node1, TempNode node2, Instances insts)
/*  360:     */   {
/*  361: 609 */     int classIdx = this.m_Instances.classIndex();
/*  362: 610 */     double[] attrVals = new double[insts.numAttributes()];
/*  363:     */     
/*  364: 612 */     double anchr1Ratio = node1.points.length() / (node1.points.length() + node2.points.length());
/*  365: 613 */     double anchr2Ratio = node2.points.length() / (node1.points.length() + node2.points.length());
/*  366: 616 */     for (int k = 0; k < node1.anchor.numValues(); k++) {
/*  367: 617 */       if (node1.anchor.index(k) != classIdx) {
/*  368: 620 */         attrVals[k] += node1.anchor.valueSparse(k) * anchr1Ratio;
/*  369:     */       }
/*  370:     */     }
/*  371: 622 */     for (int k = 0; k < node2.anchor.numValues(); k++) {
/*  372: 623 */       if (node2.anchor.index(k) != classIdx) {
/*  373: 626 */         attrVals[k] += node2.anchor.valueSparse(k) * anchr2Ratio;
/*  374:     */       }
/*  375:     */     }
/*  376: 628 */     Instance temp = new DenseInstance(1.0D, attrVals);
/*  377: 629 */     return temp;
/*  378:     */   }
/*  379:     */   
/*  380:     */   public Instance calcPivot(MyIdxList list1, MyIdxList list2, Instances insts)
/*  381:     */   {
/*  382: 643 */     int classIdx = this.m_Instances.classIndex();
/*  383: 644 */     double[] attrVals = new double[insts.numAttributes()];
/*  384: 647 */     for (int i = 0; i < list1.length(); i++)
/*  385:     */     {
/*  386: 648 */       Instance temp = insts.instance(list1.get(i).idx);
/*  387: 649 */       for (int k = 0; k < temp.numValues(); k++) {
/*  388: 650 */         if (temp.index(k) != classIdx) {
/*  389: 653 */           attrVals[k] += temp.valueSparse(k);
/*  390:     */         }
/*  391:     */       }
/*  392:     */     }
/*  393: 656 */     for (int j = 0; j < list2.length(); j++)
/*  394:     */     {
/*  395: 657 */       Instance temp = insts.instance(list2.get(j).idx);
/*  396: 658 */       for (int k = 0; k < temp.numValues(); k++) {
/*  397: 659 */         if (temp.index(k) != classIdx) {
/*  398: 662 */           attrVals[k] += temp.valueSparse(k);
/*  399:     */         }
/*  400:     */       }
/*  401:     */     }
/*  402: 665 */     int j = 0;
/*  403: 665 */     for (int numInsts = list1.length() + list2.length(); j < attrVals.length; j++) {
/*  404: 666 */       attrVals[j] /= numInsts;
/*  405:     */     }
/*  406: 668 */     Instance temp = new DenseInstance(1.0D, attrVals);
/*  407: 669 */     return temp;
/*  408:     */   }
/*  409:     */   
/*  410:     */   public double calcRadius(TempNode n1, TempNode n2)
/*  411:     */   {
/*  412: 682 */     Instance p1 = n1.anchor;Instance p2 = n2.anchor;
/*  413: 683 */     double radius = n1.radius + this.m_DistanceFunction.distance(p1, p2) + n2.radius;
/*  414: 684 */     return radius / 2.0D;
/*  415:     */   }
/*  416:     */   
/*  417:     */   public double calcRadius(MyIdxList list1, MyIdxList list2, Instance pivot, Instances insts)
/*  418:     */   {
/*  419: 700 */     double radius = (-1.0D / 0.0D);
/*  420: 702 */     for (int i = 0; i < list1.length(); i++)
/*  421:     */     {
/*  422: 703 */       double dist = this.m_DistanceFunction.distance(pivot, insts.instance(list1.get(i).idx));
/*  423: 705 */       if (dist > radius) {
/*  424: 706 */         radius = dist;
/*  425:     */       }
/*  426:     */     }
/*  427: 709 */     for (int j = 0; j < list2.length(); j++)
/*  428:     */     {
/*  429: 710 */       double dist = this.m_DistanceFunction.distance(pivot, insts.instance(list2.get(j).idx));
/*  430: 712 */       if (dist > radius) {
/*  431: 713 */         radius = dist;
/*  432:     */       }
/*  433:     */     }
/*  434: 716 */     return radius;
/*  435:     */   }
/*  436:     */   
/*  437:     */   public int[] addInstance(BallNode node, Instance inst)
/*  438:     */     throws Exception
/*  439:     */   {
/*  440: 733 */     throw new Exception("Addition of instances after the tree is built, not possible with MiddleOutConstructor.");
/*  441:     */   }
/*  442:     */   
/*  443:     */   public void setMaxInstancesInLeaf(int num)
/*  444:     */     throws Exception
/*  445:     */   {
/*  446: 746 */     if (num < 2) {
/*  447: 747 */       throw new Exception("The maximum number of instances in a leaf for using MiddleOutConstructor must be >=2.");
/*  448:     */     }
/*  449: 750 */     super.setMaxInstancesInLeaf(num);
/*  450:     */   }
/*  451:     */   
/*  452:     */   public void setInstances(Instances insts)
/*  453:     */   {
/*  454: 760 */     super.setInstances(insts);
/*  455: 761 */     this.rootRadius = -1.0D;
/*  456:     */   }
/*  457:     */   
/*  458:     */   public void setInstanceList(int[] instList)
/*  459:     */   {
/*  460: 772 */     super.setInstanceList(instList);
/*  461: 773 */     this.rootRadius = -1.0D;
/*  462:     */   }
/*  463:     */   
/*  464:     */   public String initialAnchorRandomTipText()
/*  465:     */   {
/*  466: 783 */     return "Whether the initial anchor is chosen randomly.";
/*  467:     */   }
/*  468:     */   
/*  469:     */   public boolean isInitialAnchorRandom()
/*  470:     */   {
/*  471: 792 */     return this.m_RandomInitialAnchor;
/*  472:     */   }
/*  473:     */   
/*  474:     */   public void setInitialAnchorRandom(boolean randomInitialAnchor)
/*  475:     */   {
/*  476: 803 */     this.m_RandomInitialAnchor = randomInitialAnchor;
/*  477:     */   }
/*  478:     */   
/*  479:     */   public String seedTipText()
/*  480:     */   {
/*  481: 813 */     return "The seed value for the random number generator.";
/*  482:     */   }
/*  483:     */   
/*  484:     */   public int getSeed()
/*  485:     */   {
/*  486: 823 */     return this.m_RSeed;
/*  487:     */   }
/*  488:     */   
/*  489:     */   public void setSeed(int seed)
/*  490:     */   {
/*  491: 834 */     this.m_RSeed = seed;
/*  492:     */   }
/*  493:     */   
/*  494:     */   public Enumeration<Option> listOptions()
/*  495:     */   {
/*  496: 844 */     Vector<Option> newVector = new Vector();
/*  497:     */     
/*  498: 846 */     newVector.addElement(new Option("\tThe seed for the random number generator used\n\tin selecting random anchor.\n(default: 1)", "S", 1, "-S <num>"));
/*  499:     */     
/*  500:     */ 
/*  501:     */ 
/*  502:     */ 
/*  503: 851 */     newVector.addElement(new Option("\tUse randomly chosen initial anchors.", "R", 0, "-R"));
/*  504:     */     
/*  505:     */ 
/*  506: 854 */     newVector.addAll(Collections.list(super.listOptions()));
/*  507:     */     
/*  508: 856 */     return newVector.elements();
/*  509:     */   }
/*  510:     */   
/*  511:     */   public void setOptions(String[] options)
/*  512:     */     throws Exception
/*  513:     */   {
/*  514: 885 */     String temp = Utils.getOption('S', options);
/*  515: 886 */     if (temp.length() > 0) {
/*  516: 887 */       setSeed(Integer.parseInt(temp));
/*  517:     */     } else {
/*  518: 889 */       setSeed(1);
/*  519:     */     }
/*  520: 892 */     super.setOptions(options);
/*  521:     */     
/*  522: 894 */     setInitialAnchorRandom(Utils.getFlag('R', options));
/*  523:     */   }
/*  524:     */   
/*  525:     */   public String[] getOptions()
/*  526:     */   {
/*  527: 904 */     Vector<String> result = new Vector();
/*  528:     */     
/*  529: 906 */     result.add("-S");
/*  530: 907 */     result.add("" + getSeed());
/*  531: 909 */     if (isInitialAnchorRandom()) {
/*  532: 910 */       result.add("-R");
/*  533:     */     }
/*  534: 913 */     Collections.addAll(result, super.getOptions());
/*  535:     */     
/*  536: 915 */     return (String[])result.toArray(new String[result.size()]);
/*  537:     */   }
/*  538:     */   
/*  539:     */   public void checkIndicesList(MyIdxList list, int startidx, int endidx)
/*  540:     */     throws Exception
/*  541:     */   {
/*  542: 933 */     for (int i = 0; i < list.size(); i++)
/*  543:     */     {
/*  544: 934 */       ListNode node = list.get(i);
/*  545: 935 */       boolean found = false;
/*  546: 936 */       for (int j = startidx; j <= endidx; j++) {
/*  547: 937 */         if (node.idx == this.m_InstList[j])
/*  548:     */         {
/*  549: 938 */           found = true;
/*  550: 939 */           break;
/*  551:     */         }
/*  552:     */       }
/*  553: 942 */       if (!found) {
/*  554: 943 */         throw new Exception("Error: Element " + node.idx + " of the list not in " + "the array." + "\nArray: " + printInsts(startidx, endidx) + "\nList: " + printList(list));
/*  555:     */       }
/*  556:     */     }
/*  557:     */   }
/*  558:     */   
/*  559:     */   public String printInsts(int startIdx, int endIdx)
/*  560:     */   {
/*  561: 959 */     StringBuffer bf = new StringBuffer();
/*  562:     */     try
/*  563:     */     {
/*  564: 961 */       bf.append("i: ");
/*  565: 962 */       for (int i = startIdx; i <= endIdx; i++) {
/*  566: 963 */         if (i == startIdx) {
/*  567: 964 */           bf.append("" + this.m_InstList[i]);
/*  568:     */         } else {
/*  569: 966 */           bf.append(", " + this.m_InstList[i]);
/*  570:     */         }
/*  571:     */       }
/*  572:     */     }
/*  573:     */     catch (Exception ex)
/*  574:     */     {
/*  575: 970 */       ex.printStackTrace();
/*  576:     */     }
/*  577: 972 */     return bf.toString();
/*  578:     */   }
/*  579:     */   
/*  580:     */   public String printList(MyIdxList points)
/*  581:     */   {
/*  582: 982 */     if ((points == null) || (points.length() == 0)) {
/*  583: 983 */       return "";
/*  584:     */     }
/*  585: 985 */     StringBuffer bf = new StringBuffer();
/*  586:     */     try
/*  587:     */     {
/*  588: 988 */       for (int i = 0; i < points.size(); i++)
/*  589:     */       {
/*  590: 989 */         ListNode temp = points.get(i);
/*  591: 990 */         if (i == 0) {
/*  592: 991 */           bf.append("" + temp.idx);
/*  593:     */         } else {
/*  594: 993 */           bf.append(", " + temp.idx);
/*  595:     */         }
/*  596:     */       }
/*  597:     */     }
/*  598:     */     catch (Exception ex)
/*  599:     */     {
/*  600: 997 */       ex.printStackTrace();
/*  601:     */     }
/*  602: 999 */     return bf.toString();
/*  603:     */   }
/*  604:     */   
/*  605:     */   public String getRevision()
/*  606:     */   {
/*  607:1009 */     return RevisionUtils.extract("$Revision: 11269 $");
/*  608:     */   }
/*  609:     */   
/*  610:     */   protected class TempNode
/*  611:     */     implements RevisionHandler
/*  612:     */   {
/*  613:     */     Instance anchor;
/*  614:     */     int idx;
/*  615:     */     double radius;
/*  616:     */     MiddleOutConstructor.MyIdxList points;
/*  617:     */     TempNode left;
/*  618:     */     TempNode right;
/*  619:     */     
/*  620:     */     protected TempNode() {}
/*  621:     */     
/*  622:     */     public String toString()
/*  623:     */     {
/*  624:1049 */       if ((this.points == null) || (this.points.length() == 0)) {
/*  625:1050 */         return this.idx + "";
/*  626:     */       }
/*  627:1052 */       StringBuffer bf = new StringBuffer();
/*  628:     */       try
/*  629:     */       {
/*  630:1054 */         bf.append(this.idx + " p: ");
/*  631:1056 */         for (int i = 0; i < this.points.size(); i++)
/*  632:     */         {
/*  633:1057 */           MiddleOutConstructor.ListNode temp = this.points.get(i);
/*  634:1058 */           if (i == 0) {
/*  635:1059 */             bf.append("" + temp.idx);
/*  636:     */           } else {
/*  637:1061 */             bf.append(", " + temp.idx);
/*  638:     */           }
/*  639:     */         }
/*  640:     */       }
/*  641:     */       catch (Exception ex)
/*  642:     */       {
/*  643:1065 */         ex.printStackTrace();
/*  644:     */       }
/*  645:1067 */       return bf.toString();
/*  646:     */     }
/*  647:     */     
/*  648:     */     public String getRevision()
/*  649:     */     {
/*  650:1077 */       return RevisionUtils.extract("$Revision: 11269 $");
/*  651:     */     }
/*  652:     */   }
/*  653:     */   
/*  654:     */   protected class ListNode
/*  655:     */     implements RevisionHandler, Serializable
/*  656:     */   {
/*  657:1091 */     int idx = -1;
/*  658:1094 */     double distance = (-1.0D / 0.0D);
/*  659:     */     
/*  660:     */     public ListNode(int i, double d)
/*  661:     */     {
/*  662:1103 */       this.idx = i;
/*  663:1104 */       this.distance = d;
/*  664:     */     }
/*  665:     */     
/*  666:     */     public String getRevision()
/*  667:     */     {
/*  668:1114 */       return RevisionUtils.extract("$Revision: 11269 $");
/*  669:     */     }
/*  670:     */   }
/*  671:     */   
/*  672:     */   protected class MyIdxList
/*  673:     */     implements Serializable, RevisionHandler
/*  674:     */   {
/*  675:     */     private static final long serialVersionUID = -2283869109722934927L;
/*  676:     */     protected ArrayList<MiddleOutConstructor.ListNode> m_List;
/*  677:     */     
/*  678:     */     public MyIdxList()
/*  679:     */     {
/*  680:1138 */       this.m_List = new ArrayList();
/*  681:     */     }
/*  682:     */     
/*  683:     */     public MyIdxList(int capacity)
/*  684:     */     {
/*  685:1145 */       this.m_List = new ArrayList(capacity);
/*  686:     */     }
/*  687:     */     
/*  688:     */     public MiddleOutConstructor.ListNode getFirst()
/*  689:     */     {
/*  690:1154 */       return (MiddleOutConstructor.ListNode)this.m_List.get(0);
/*  691:     */     }
/*  692:     */     
/*  693:     */     public void insertReverseSorted(int idx, double distance)
/*  694:     */     {
/*  695:1166 */       int i = 0;
/*  696:1167 */       for (MiddleOutConstructor.ListNode temp : this.m_List)
/*  697:     */       {
/*  698:1168 */         if (temp.distance < distance) {
/*  699:     */           break;
/*  700:     */         }
/*  701:1171 */         i++;
/*  702:     */       }
/*  703:1173 */       this.m_List.add(i, new MiddleOutConstructor.ListNode(MiddleOutConstructor.this, idx, distance));
/*  704:     */     }
/*  705:     */     
/*  706:     */     public MiddleOutConstructor.ListNode get(int index)
/*  707:     */     {
/*  708:1183 */       return (MiddleOutConstructor.ListNode)this.m_List.get(index);
/*  709:     */     }
/*  710:     */     
/*  711:     */     public void remove(int index)
/*  712:     */     {
/*  713:1192 */       this.m_List.remove(index);
/*  714:     */     }
/*  715:     */     
/*  716:     */     public int length()
/*  717:     */     {
/*  718:1201 */       return this.m_List.size();
/*  719:     */     }
/*  720:     */     
/*  721:     */     public int size()
/*  722:     */     {
/*  723:1210 */       return this.m_List.size();
/*  724:     */     }
/*  725:     */     
/*  726:     */     public MyIdxList append(MyIdxList list1, MyIdxList list2)
/*  727:     */     {
/*  728:1221 */       MyIdxList temp = new MyIdxList(MiddleOutConstructor.this, list1.size() + list2.size());
/*  729:1222 */       temp.m_List.addAll(list1.m_List);
/*  730:1223 */       temp.m_List.addAll(list2.m_List);
/*  731:1224 */       return temp;
/*  732:     */     }
/*  733:     */     
/*  734:     */     public void checkSorting(MyIdxList list)
/*  735:     */       throws Exception
/*  736:     */     {
/*  737:1234 */       Iterator<MiddleOutConstructor.ListNode> en = this.m_List.iterator();
/*  738:1235 */       MiddleOutConstructor.ListNode first = null;MiddleOutConstructor.ListNode second = null;
/*  739:1236 */       while (en.hasNext()) {
/*  740:1237 */         if (first == null)
/*  741:     */         {
/*  742:1238 */           first = (MiddleOutConstructor.ListNode)en.next();
/*  743:     */         }
/*  744:     */         else
/*  745:     */         {
/*  746:1240 */           second = (MiddleOutConstructor.ListNode)en.next();
/*  747:1241 */           if (first.distance < second.distance) {
/*  748:1242 */             throw new Exception("List not sorted correctly. first.distance: " + first.distance + " second.distance: " + second.distance + " Please check code.");
/*  749:     */           }
/*  750:     */         }
/*  751:     */       }
/*  752:     */     }
/*  753:     */     
/*  754:     */     public String getRevision()
/*  755:     */     {
/*  756:1257 */       return RevisionUtils.extract("$Revision: 11269 $");
/*  757:     */     }
/*  758:     */   }
/*  759:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.balltrees.MiddleOutConstructor
 * JD-Core Version:    0.7.0.1
 */