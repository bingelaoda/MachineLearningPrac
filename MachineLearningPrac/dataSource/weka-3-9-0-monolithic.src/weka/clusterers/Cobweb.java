/*    1:     */ package weka.clusterers;
/*    2:     */ 
/*    3:     */ import java.io.Serializable;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.Random;
/*    8:     */ import java.util.Vector;
/*    9:     */ import weka.core.Attribute;
/*   10:     */ import weka.core.AttributeStats;
/*   11:     */ import weka.core.Capabilities;
/*   12:     */ import weka.core.Capabilities.Capability;
/*   13:     */ import weka.core.Drawable;
/*   14:     */ import weka.core.Instance;
/*   15:     */ import weka.core.Instances;
/*   16:     */ import weka.core.Option;
/*   17:     */ import weka.core.RevisionHandler;
/*   18:     */ import weka.core.RevisionUtils;
/*   19:     */ import weka.core.TechnicalInformation;
/*   20:     */ import weka.core.TechnicalInformation.Field;
/*   21:     */ import weka.core.TechnicalInformation.Type;
/*   22:     */ import weka.core.TechnicalInformationHandler;
/*   23:     */ import weka.core.Utils;
/*   24:     */ import weka.experiment.Stats;
/*   25:     */ import weka.filters.Filter;
/*   26:     */ import weka.filters.unsupervised.attribute.Add;
/*   27:     */ 
/*   28:     */ public class Cobweb
/*   29:     */   extends RandomizableClusterer
/*   30:     */   implements Drawable, TechnicalInformationHandler, UpdateableClusterer
/*   31:     */ {
/*   32:     */   static final long serialVersionUID = 928406656495092318L;
/*   33:     */   
/*   34:     */   public class CNode
/*   35:     */     implements Serializable, RevisionHandler
/*   36:     */   {
/*   37:     */     static final long serialVersionUID = 3452097436933325631L;
/*   38:     */     private AttributeStats[] m_attStats;
/*   39:     */     private final int m_numAttributes;
/*   40: 149 */     protected Instances m_clusterInstances = null;
/*   41: 154 */     private ArrayList<CNode> m_children = null;
/*   42: 159 */     private double m_totalInstances = 0.0D;
/*   43: 164 */     private int m_clusterNum = -1;
/*   44:     */     
/*   45:     */     public CNode(int numAttributes)
/*   46:     */     {
/*   47: 172 */       this.m_numAttributes = numAttributes;
/*   48:     */     }
/*   49:     */     
/*   50:     */     public CNode(int numAttributes, Instance leafInstance)
/*   51:     */     {
/*   52: 182 */       this(numAttributes);
/*   53: 183 */       if (this.m_clusterInstances == null) {
/*   54: 184 */         this.m_clusterInstances = new Instances(leafInstance.dataset(), 1);
/*   55:     */       }
/*   56: 186 */       this.m_clusterInstances.add(leafInstance);
/*   57: 187 */       updateStats(leafInstance, false);
/*   58:     */     }
/*   59:     */     
/*   60:     */     protected void addInstance(Instance newInstance)
/*   61:     */       throws Exception
/*   62:     */     {
/*   63: 199 */       if (this.m_clusterInstances == null)
/*   64:     */       {
/*   65: 200 */         this.m_clusterInstances = new Instances(newInstance.dataset(), 1);
/*   66: 201 */         this.m_clusterInstances.add(newInstance);
/*   67: 202 */         updateStats(newInstance, false);
/*   68: 203 */         return;
/*   69:     */       }
/*   70: 204 */       if (this.m_children == null)
/*   71:     */       {
/*   72: 209 */         this.m_children = new ArrayList();
/*   73: 210 */         CNode tempSubCluster = new CNode(Cobweb.this, this.m_numAttributes, this.m_clusterInstances.instance(0));
/*   74: 214 */         for (int i = 1; i < this.m_clusterInstances.numInstances(); i++)
/*   75:     */         {
/*   76: 215 */           tempSubCluster.m_clusterInstances.add(this.m_clusterInstances.instance(i));
/*   77: 216 */           tempSubCluster.updateStats(this.m_clusterInstances.instance(i), false);
/*   78:     */         }
/*   79: 218 */         this.m_children = new ArrayList();
/*   80: 219 */         this.m_children.add(tempSubCluster);
/*   81: 220 */         this.m_children.add(new CNode(Cobweb.this, this.m_numAttributes, newInstance));
/*   82:     */         
/*   83: 222 */         this.m_clusterInstances.add(newInstance);
/*   84: 223 */         updateStats(newInstance, false);
/*   85: 227 */         if (categoryUtility() < Cobweb.this.m_cutoff) {
/*   86: 229 */           this.m_children = null;
/*   87:     */         }
/*   88: 231 */         return;
/*   89:     */       }
/*   90: 235 */       CNode bestHost = findHost(newInstance, false);
/*   91: 236 */       if (bestHost != null) {
/*   92: 238 */         bestHost.addInstance(newInstance);
/*   93:     */       }
/*   94:     */     }
/*   95:     */     
/*   96:     */     private double[] cuScoresForChildren(Instance newInstance)
/*   97:     */       throws Exception
/*   98:     */     {
/*   99: 253 */       double[] categoryUtils = new double[this.m_children.size()];
/*  100: 256 */       for (int i = 0; i < this.m_children.size(); i++)
/*  101:     */       {
/*  102: 257 */         CNode temp = (CNode)this.m_children.get(i);
/*  103:     */         
/*  104: 259 */         temp.updateStats(newInstance, false);
/*  105: 260 */         categoryUtils[i] = categoryUtility();
/*  106:     */         
/*  107:     */ 
/*  108: 263 */         temp.updateStats(newInstance, true);
/*  109:     */       }
/*  110: 265 */       return categoryUtils;
/*  111:     */     }
/*  112:     */     
/*  113:     */     private double cuScoreForBestTwoMerged(CNode merged, CNode a, CNode b, Instance newInstance)
/*  114:     */       throws Exception
/*  115:     */     {
/*  116: 271 */       double mergedCU = -1.797693134862316E+308D;
/*  117:     */       
/*  118:     */ 
/*  119: 274 */       merged.m_clusterInstances = new Instances(this.m_clusterInstances, 1);
/*  120:     */       
/*  121: 276 */       merged.addChildNode(a);
/*  122: 277 */       merged.addChildNode(b);
/*  123: 278 */       merged.updateStats(newInstance, false);
/*  124:     */       
/*  125: 280 */       this.m_children.remove(this.m_children.indexOf(a));
/*  126: 281 */       this.m_children.remove(this.m_children.indexOf(b));
/*  127: 282 */       this.m_children.add(merged);
/*  128: 283 */       mergedCU = categoryUtility();
/*  129:     */       
/*  130: 285 */       merged.updateStats(newInstance, true);
/*  131: 286 */       this.m_children.remove(this.m_children.indexOf(merged));
/*  132: 287 */       this.m_children.add(a);
/*  133: 288 */       this.m_children.add(b);
/*  134: 289 */       return mergedCU;
/*  135:     */     }
/*  136:     */     
/*  137:     */     private CNode findHost(Instance newInstance, boolean structureFrozen)
/*  138:     */       throws Exception
/*  139:     */     {
/*  140: 305 */       if (!structureFrozen) {
/*  141: 306 */         updateStats(newInstance, false);
/*  142:     */       }
/*  143: 310 */       double[] categoryUtils = cuScoresForChildren(newInstance);
/*  144:     */       
/*  145:     */ 
/*  146: 313 */       CNode newLeaf = new CNode(Cobweb.this, this.m_numAttributes, newInstance);
/*  147: 314 */       this.m_children.add(newLeaf);
/*  148: 315 */       double bestHostCU = categoryUtility();
/*  149: 316 */       CNode finalBestHost = newLeaf;
/*  150:     */       
/*  151:     */ 
/*  152:     */ 
/*  153: 320 */       this.m_children.remove(this.m_children.size() - 1);
/*  154:     */       
/*  155:     */ 
/*  156: 323 */       int best = 0;
/*  157: 324 */       int secondBest = 0;
/*  158: 325 */       for (int i = 0; i < categoryUtils.length; i++) {
/*  159: 326 */         if (categoryUtils[i] > categoryUtils[secondBest]) {
/*  160: 327 */           if (categoryUtils[i] > categoryUtils[best])
/*  161:     */           {
/*  162: 328 */             secondBest = best;
/*  163: 329 */             best = i;
/*  164:     */           }
/*  165:     */           else
/*  166:     */           {
/*  167: 331 */             secondBest = i;
/*  168:     */           }
/*  169:     */         }
/*  170:     */       }
/*  171: 336 */       CNode a = (CNode)this.m_children.get(best);
/*  172: 337 */       CNode b = (CNode)this.m_children.get(secondBest);
/*  173: 338 */       if (categoryUtils[best] > bestHostCU)
/*  174:     */       {
/*  175: 339 */         bestHostCU = categoryUtils[best];
/*  176: 340 */         finalBestHost = a;
/*  177:     */       }
/*  178: 344 */       if (structureFrozen)
/*  179:     */       {
/*  180: 345 */         if (finalBestHost == newLeaf) {
/*  181: 346 */           return null;
/*  182:     */         }
/*  183: 348 */         return finalBestHost;
/*  184:     */       }
/*  185: 352 */       double mergedCU = -1.797693134862316E+308D;
/*  186: 353 */       CNode merged = new CNode(Cobweb.this, this.m_numAttributes);
/*  187: 354 */       if (a != b)
/*  188:     */       {
/*  189: 355 */         mergedCU = cuScoreForBestTwoMerged(merged, a, b, newInstance);
/*  190: 357 */         if (mergedCU > bestHostCU)
/*  191:     */         {
/*  192: 358 */           bestHostCU = mergedCU;
/*  193: 359 */           finalBestHost = merged;
/*  194:     */         }
/*  195:     */       }
/*  196: 364 */       double splitCU = -1.797693134862316E+308D;
/*  197: 365 */       double splitBestChildCU = -1.797693134862316E+308D;
/*  198: 366 */       double splitPlusNewLeafCU = -1.797693134862316E+308D;
/*  199: 367 */       double splitPlusMergeBestTwoCU = -1.797693134862316E+308D;
/*  200: 368 */       if (a.m_children != null)
/*  201:     */       {
/*  202: 369 */         ArrayList<CNode> tempChildren = new ArrayList();
/*  203: 371 */         for (int i = 0; i < this.m_children.size(); i++)
/*  204:     */         {
/*  205: 372 */           CNode existingChild = (CNode)this.m_children.get(i);
/*  206: 373 */           if (existingChild != a) {
/*  207: 374 */             tempChildren.add(existingChild);
/*  208:     */           }
/*  209:     */         }
/*  210: 377 */         for (int i = 0; i < a.m_children.size(); i++)
/*  211:     */         {
/*  212: 378 */           CNode promotedChild = (CNode)a.m_children.get(i);
/*  213: 379 */           tempChildren.add(promotedChild);
/*  214:     */         }
/*  215: 382 */         tempChildren.add(newLeaf);
/*  216:     */         
/*  217: 384 */         ArrayList<CNode> saveStatusQuo = this.m_children;
/*  218: 385 */         this.m_children = tempChildren;
/*  219: 386 */         splitPlusNewLeafCU = categoryUtility();
/*  220:     */         
/*  221: 388 */         tempChildren.remove(tempChildren.size() - 1);
/*  222:     */         
/*  223: 390 */         categoryUtils = cuScoresForChildren(newInstance);
/*  224:     */         
/*  225:     */ 
/*  226: 393 */         best = 0;
/*  227: 394 */         secondBest = 0;
/*  228: 395 */         for (int i = 0; i < categoryUtils.length; i++) {
/*  229: 396 */           if (categoryUtils[i] > categoryUtils[secondBest]) {
/*  230: 397 */             if (categoryUtils[i] > categoryUtils[best])
/*  231:     */             {
/*  232: 398 */               secondBest = best;
/*  233: 399 */               best = i;
/*  234:     */             }
/*  235:     */             else
/*  236:     */             {
/*  237: 401 */               secondBest = i;
/*  238:     */             }
/*  239:     */           }
/*  240:     */         }
/*  241: 405 */         CNode sa = (CNode)this.m_children.get(best);
/*  242: 406 */         CNode sb = (CNode)this.m_children.get(secondBest);
/*  243: 407 */         splitBestChildCU = categoryUtils[best];
/*  244:     */         
/*  245:     */ 
/*  246: 410 */         CNode mergedSplitChildren = new CNode(Cobweb.this, this.m_numAttributes);
/*  247: 411 */         if (sa != sb) {
/*  248: 412 */           splitPlusMergeBestTwoCU = cuScoreForBestTwoMerged(mergedSplitChildren, sa, sb, newInstance);
/*  249:     */         }
/*  250: 415 */         splitCU = splitBestChildCU > splitPlusNewLeafCU ? splitBestChildCU : splitPlusNewLeafCU;
/*  251:     */         
/*  252: 417 */         splitCU = splitCU > splitPlusMergeBestTwoCU ? splitCU : splitPlusMergeBestTwoCU;
/*  253: 420 */         if (splitCU > bestHostCU)
/*  254:     */         {
/*  255: 421 */           bestHostCU = splitCU;
/*  256: 422 */           finalBestHost = this;
/*  257:     */         }
/*  258:     */         else
/*  259:     */         {
/*  260: 426 */           this.m_children = saveStatusQuo;
/*  261:     */         }
/*  262:     */       }
/*  263: 430 */       if (finalBestHost != this) {
/*  264: 432 */         this.m_clusterInstances.add(newInstance);
/*  265:     */       } else {
/*  266: 434 */         Cobweb.this.m_numberSplits += 1;
/*  267:     */       }
/*  268: 437 */       if (finalBestHost == merged)
/*  269:     */       {
/*  270: 438 */         Cobweb.this.m_numberMerges += 1;
/*  271: 439 */         this.m_children.remove(this.m_children.indexOf(a));
/*  272: 440 */         this.m_children.remove(this.m_children.indexOf(b));
/*  273: 441 */         this.m_children.add(merged);
/*  274:     */       }
/*  275: 444 */       if (finalBestHost == newLeaf)
/*  276:     */       {
/*  277: 445 */         finalBestHost = new CNode(Cobweb.this, this.m_numAttributes);
/*  278: 446 */         this.m_children.add(finalBestHost);
/*  279:     */       }
/*  280: 449 */       if (bestHostCU < Cobweb.this.m_cutoff)
/*  281:     */       {
/*  282: 450 */         if (finalBestHost == this) {
/*  283: 454 */           this.m_clusterInstances.add(newInstance);
/*  284:     */         }
/*  285: 456 */         this.m_children = null;
/*  286: 457 */         finalBestHost = null;
/*  287:     */       }
/*  288: 460 */       if (finalBestHost == this) {
/*  289: 463 */         updateStats(newInstance, true);
/*  290:     */       }
/*  291: 466 */       return finalBestHost;
/*  292:     */     }
/*  293:     */     
/*  294:     */     protected void addChildNode(CNode child)
/*  295:     */     {
/*  296: 476 */       for (int i = 0; i < child.m_clusterInstances.numInstances(); i++)
/*  297:     */       {
/*  298: 477 */         Instance temp = child.m_clusterInstances.instance(i);
/*  299: 478 */         this.m_clusterInstances.add(temp);
/*  300: 479 */         updateStats(temp, false);
/*  301:     */       }
/*  302: 482 */       if (this.m_children == null) {
/*  303: 483 */         this.m_children = new ArrayList();
/*  304:     */       }
/*  305: 485 */       this.m_children.add(child);
/*  306:     */     }
/*  307:     */     
/*  308:     */     protected double categoryUtility()
/*  309:     */       throws Exception
/*  310:     */     {
/*  311: 496 */       if (this.m_children == null) {
/*  312: 497 */         throw new Exception("categoryUtility: No children!");
/*  313:     */       }
/*  314: 500 */       double totalCU = 0.0D;
/*  315: 502 */       for (int i = 0; i < this.m_children.size(); i++)
/*  316:     */       {
/*  317: 503 */         CNode child = (CNode)this.m_children.get(i);
/*  318: 504 */         totalCU += categoryUtilityChild(child);
/*  319:     */       }
/*  320: 507 */       totalCU /= this.m_children.size();
/*  321: 508 */       return totalCU;
/*  322:     */     }
/*  323:     */     
/*  324:     */     protected double categoryUtilityChild(CNode child)
/*  325:     */       throws Exception
/*  326:     */     {
/*  327: 520 */       double sum = 0.0D;
/*  328: 521 */       for (int i = 0; i < this.m_numAttributes; i++) {
/*  329: 522 */         if (this.m_clusterInstances.attribute(i).isNominal()) {
/*  330: 523 */           for (int j = 0; j < this.m_clusterInstances.attribute(i).numValues(); j++)
/*  331:     */           {
/*  332: 524 */             double x = child.getProbability(i, j);
/*  333: 525 */             double y = getProbability(i, j);
/*  334: 526 */             sum += (x - y) * (x + y);
/*  335:     */           }
/*  336:     */         } else {
/*  337: 530 */           sum += Cobweb.m_normal / child.getStandardDev(i) - Cobweb.m_normal / getStandardDev(i);
/*  338:     */         }
/*  339:     */       }
/*  340: 534 */       return child.m_totalInstances / this.m_totalInstances * sum;
/*  341:     */     }
/*  342:     */     
/*  343:     */     protected double getProbability(int attIndex, int valueIndex)
/*  344:     */       throws Exception
/*  345:     */     {
/*  346: 548 */       if (!this.m_clusterInstances.attribute(attIndex).isNominal()) {
/*  347: 549 */         throw new Exception("getProbability: attribute is not nominal");
/*  348:     */       }
/*  349: 552 */       if (this.m_attStats[attIndex].totalCount <= 0) {
/*  350: 553 */         return 0.0D;
/*  351:     */       }
/*  352: 556 */       return this.m_attStats[attIndex].nominalCounts[valueIndex] / this.m_attStats[attIndex].totalCount;
/*  353:     */     }
/*  354:     */     
/*  355:     */     protected double getStandardDev(int attIndex)
/*  356:     */       throws Exception
/*  357:     */     {
/*  358: 568 */       if (!this.m_clusterInstances.attribute(attIndex).isNumeric()) {
/*  359: 569 */         throw new Exception("getStandardDev: attribute is not numeric");
/*  360:     */       }
/*  361: 572 */       this.m_attStats[attIndex].numericStats.calculateDerived();
/*  362: 573 */       double stdDev = this.m_attStats[attIndex].numericStats.stdDev;
/*  363: 574 */       if ((Double.isNaN(stdDev)) || (Double.isInfinite(stdDev))) {
/*  364: 575 */         return Cobweb.this.m_acuity;
/*  365:     */       }
/*  366: 578 */       return Math.max(Cobweb.this.m_acuity, stdDev);
/*  367:     */     }
/*  368:     */     
/*  369:     */     protected void updateStats(Instance updateInstance, boolean delete)
/*  370:     */     {
/*  371: 590 */       if (this.m_attStats == null)
/*  372:     */       {
/*  373: 591 */         this.m_attStats = new AttributeStats[this.m_numAttributes];
/*  374: 592 */         for (int i = 0; i < this.m_numAttributes; i++)
/*  375:     */         {
/*  376: 593 */           this.m_attStats[i] = new AttributeStats();
/*  377: 594 */           if (this.m_clusterInstances.attribute(i).isNominal()) {
/*  378: 595 */             this.m_attStats[i].nominalCounts = new int[this.m_clusterInstances.attribute(i).numValues()];
/*  379:     */           } else {
/*  380: 598 */             this.m_attStats[i].numericStats = new Stats();
/*  381:     */           }
/*  382:     */         }
/*  383:     */       }
/*  384: 602 */       for (int i = 0; i < this.m_numAttributes; i++) {
/*  385: 603 */         if (!updateInstance.isMissing(i))
/*  386:     */         {
/*  387: 604 */           double value = updateInstance.value(i);
/*  388: 605 */           if (this.m_clusterInstances.attribute(i).isNominal())
/*  389:     */           {
/*  390: 606 */             int tmp157_156 = ((int)value); int[] tmp157_151 = this.m_attStats[i].nominalCounts;tmp157_151[tmp157_156] = ((int)(tmp157_151[tmp157_156] + (delete ? -1.0D * updateInstance.weight() : updateInstance.weight()))); AttributeStats 
/*  391:     */             
/*  392: 608 */               tmp192_191 = this.m_attStats[i];tmp192_191.totalCount = ((int)(tmp192_191.totalCount + (delete ? -1.0D * updateInstance.weight() : updateInstance.weight())));
/*  393:     */           }
/*  394: 611 */           else if (delete)
/*  395:     */           {
/*  396: 612 */             this.m_attStats[i].numericStats.subtract(value, updateInstance.weight());
/*  397:     */           }
/*  398:     */           else
/*  399:     */           {
/*  400: 615 */             this.m_attStats[i].numericStats.add(value, updateInstance.weight());
/*  401:     */           }
/*  402:     */         }
/*  403:     */       }
/*  404: 620 */       this.m_totalInstances += (delete ? -1.0D * updateInstance.weight() : updateInstance.weight());
/*  405:     */     }
/*  406:     */     
/*  407:     */     private void assignClusterNums(int[] cl_num)
/*  408:     */       throws Exception
/*  409:     */     {
/*  410: 631 */       if ((this.m_children != null) && (this.m_children.size() < 2)) {
/*  411: 632 */         throw new Exception("assignClusterNums: tree not built correctly!");
/*  412:     */       }
/*  413: 635 */       this.m_clusterNum = cl_num[0];
/*  414: 636 */       cl_num[0] += 1;
/*  415: 637 */       if (this.m_children != null) {
/*  416: 638 */         for (int i = 0; i < this.m_children.size(); i++)
/*  417:     */         {
/*  418: 639 */           CNode child = (CNode)this.m_children.get(i);
/*  419: 640 */           child.assignClusterNums(cl_num);
/*  420:     */         }
/*  421:     */       }
/*  422:     */     }
/*  423:     */     
/*  424:     */     protected void dumpTree(int depth, StringBuffer text)
/*  425:     */     {
/*  426: 653 */       if (depth == 0) {
/*  427: 654 */         Cobweb.this.determineNumberOfClusters();
/*  428:     */       }
/*  429: 657 */       if (this.m_children == null)
/*  430:     */       {
/*  431: 658 */         text.append("\n");
/*  432: 659 */         for (int j = 0; j < depth; j++) {
/*  433: 660 */           text.append("|   ");
/*  434:     */         }
/*  435: 662 */         text.append("leaf " + this.m_clusterNum + " [" + this.m_clusterInstances.numInstances() + "]");
/*  436:     */       }
/*  437:     */       else
/*  438:     */       {
/*  439: 665 */         for (int i = 0; i < this.m_children.size(); i++)
/*  440:     */         {
/*  441: 666 */           text.append("\n");
/*  442: 667 */           for (int j = 0; j < depth; j++) {
/*  443: 668 */             text.append("|   ");
/*  444:     */           }
/*  445: 670 */           text.append("node " + this.m_clusterNum + " [" + this.m_clusterInstances.numInstances() + "]");
/*  446:     */           
/*  447: 672 */           ((CNode)this.m_children.get(i)).dumpTree(depth + 1, text);
/*  448:     */         }
/*  449:     */       }
/*  450:     */     }
/*  451:     */     
/*  452:     */     protected String dumpData()
/*  453:     */       throws Exception
/*  454:     */     {
/*  455: 685 */       if (this.m_children == null) {
/*  456: 686 */         return this.m_clusterInstances.toString();
/*  457:     */       }
/*  458: 690 */       CNode tempNode = new CNode(Cobweb.this, this.m_numAttributes);
/*  459: 691 */       tempNode.m_clusterInstances = new Instances(this.m_clusterInstances, 1);
/*  460: 692 */       for (int i = 0; i < this.m_children.size(); i++) {
/*  461: 693 */         tempNode.addChildNode((CNode)this.m_children.get(i));
/*  462:     */       }
/*  463: 695 */       Instances tempInst = tempNode.m_clusterInstances;
/*  464: 696 */       tempNode = null;
/*  465:     */       
/*  466: 698 */       Add af = new Add();
/*  467: 699 */       af.setAttributeName("Cluster");
/*  468: 700 */       String labels = "";
/*  469: 701 */       for (int i = 0; i < this.m_children.size(); i++)
/*  470:     */       {
/*  471: 702 */         CNode temp = (CNode)this.m_children.get(i);
/*  472: 703 */         labels = labels + "C" + temp.m_clusterNum;
/*  473: 704 */         if (i < this.m_children.size() - 1) {
/*  474: 705 */           labels = labels + ",";
/*  475:     */         }
/*  476:     */       }
/*  477: 708 */       af.setNominalLabels(labels);
/*  478: 709 */       af.setInputFormat(tempInst);
/*  479: 710 */       tempInst = Filter.useFilter(tempInst, af);
/*  480: 711 */       tempInst.setRelationName("Cluster " + this.m_clusterNum);
/*  481:     */       
/*  482: 713 */       int z = 0;
/*  483: 714 */       for (int i = 0; i < this.m_children.size(); i++)
/*  484:     */       {
/*  485: 715 */         CNode temp = (CNode)this.m_children.get(i);
/*  486: 716 */         for (int j = 0; j < temp.m_clusterInstances.numInstances(); j++)
/*  487:     */         {
/*  488: 717 */           tempInst.instance(z).setValue(this.m_numAttributes, i);
/*  489: 718 */           z++;
/*  490:     */         }
/*  491:     */       }
/*  492: 721 */       return tempInst.toString();
/*  493:     */     }
/*  494:     */     
/*  495:     */     protected void graphTree(StringBuffer text)
/*  496:     */       throws Exception
/*  497:     */     {
/*  498: 732 */       text.append("N" + this.m_clusterNum + " [label=\"" + (this.m_children == null ? "leaf " : "node ") + this.m_clusterNum + " " + " (" + this.m_clusterInstances.numInstances() + ")\" " + (this.m_children == null ? "shape=box style=filled " : "") + (Cobweb.this.m_saveInstances ? "data =\n" + dumpData() + "\n,\n" : "") + "]\n");
/*  499: 737 */       if (this.m_children != null)
/*  500:     */       {
/*  501: 738 */         for (int i = 0; i < this.m_children.size(); i++)
/*  502:     */         {
/*  503: 739 */           CNode temp = (CNode)this.m_children.get(i);
/*  504: 740 */           text.append("N" + this.m_clusterNum + "->" + "N" + temp.m_clusterNum + "\n");
/*  505:     */         }
/*  506: 744 */         for (int i = 0; i < this.m_children.size(); i++)
/*  507:     */         {
/*  508: 745 */           CNode temp = (CNode)this.m_children.get(i);
/*  509: 746 */           temp.graphTree(text);
/*  510:     */         }
/*  511:     */       }
/*  512:     */     }
/*  513:     */     
/*  514:     */     public String getRevision()
/*  515:     */     {
/*  516: 758 */       return RevisionUtils.extract("$Revision: 11556 $");
/*  517:     */     }
/*  518:     */   }
/*  519:     */   
/*  520: 765 */   protected static final double m_normal = 1.0D / (2.0D * Math.sqrt(3.141592653589793D));
/*  521: 770 */   protected double m_acuity = 1.0D;
/*  522: 775 */   protected double m_cutoff = 0.01D * m_normal;
/*  523: 780 */   protected CNode m_cobwebTree = null;
/*  524: 790 */   protected int m_numberOfClusters = -1;
/*  525: 793 */   protected boolean m_numberOfClustersDetermined = false;
/*  526:     */   protected int m_numberSplits;
/*  527:     */   protected int m_numberMerges;
/*  528: 805 */   protected boolean m_saveInstances = false;
/*  529:     */   
/*  530:     */   public Cobweb()
/*  531:     */   {
/*  532: 813 */     this.m_SeedDefault = 42;
/*  533: 814 */     setSeed(this.m_SeedDefault);
/*  534:     */   }
/*  535:     */   
/*  536:     */   public String globalInfo()
/*  537:     */   {
/*  538: 824 */     return "Class implementing the Cobweb and Classit clustering algorithms.\n\nNote: the application of node operators (merging, splitting etc.) in terms of ordering and priority differs (and is somewhat ambiguous) between the original Cobweb and Classit papers. This algorithm always compares the best host, adding a new leaf, merging the two best hosts, and splitting the best host when considering where to place a new instance.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  539:     */   }
/*  540:     */   
/*  541:     */   public TechnicalInformation getTechnicalInformation()
/*  542:     */   {
/*  543: 847 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  544: 848 */     result.setValue(TechnicalInformation.Field.AUTHOR, "D. Fisher");
/*  545: 849 */     result.setValue(TechnicalInformation.Field.YEAR, "1987");
/*  546: 850 */     result.setValue(TechnicalInformation.Field.TITLE, "Knowledge acquisition via incremental conceptual clustering");
/*  547:     */     
/*  548: 852 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Machine Learning");
/*  549: 853 */     result.setValue(TechnicalInformation.Field.VOLUME, "2");
/*  550: 854 */     result.setValue(TechnicalInformation.Field.NUMBER, "2");
/*  551: 855 */     result.setValue(TechnicalInformation.Field.PAGES, "139-172");
/*  552:     */     
/*  553: 857 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.ARTICLE);
/*  554: 858 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "J. H. Gennari and P. Langley and D. Fisher");
/*  555:     */     
/*  556: 860 */     additional.setValue(TechnicalInformation.Field.YEAR, "1990");
/*  557: 861 */     additional.setValue(TechnicalInformation.Field.TITLE, "Models of incremental concept formation");
/*  558: 862 */     additional.setValue(TechnicalInformation.Field.JOURNAL, "Artificial Intelligence");
/*  559: 863 */     additional.setValue(TechnicalInformation.Field.VOLUME, "40");
/*  560: 864 */     additional.setValue(TechnicalInformation.Field.PAGES, "11-61");
/*  561:     */     
/*  562: 866 */     return result;
/*  563:     */   }
/*  564:     */   
/*  565:     */   public Capabilities getCapabilities()
/*  566:     */   {
/*  567: 876 */     Capabilities result = super.getCapabilities();
/*  568: 877 */     result.disableAll();
/*  569: 878 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  570:     */     
/*  571:     */ 
/*  572: 881 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  573: 882 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  574: 883 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  575: 884 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  576:     */     
/*  577:     */ 
/*  578: 887 */     result.setMinimumNumberInstances(0);
/*  579:     */     
/*  580: 889 */     return result;
/*  581:     */   }
/*  582:     */   
/*  583:     */   public void buildClusterer(Instances data)
/*  584:     */     throws Exception
/*  585:     */   {
/*  586: 900 */     this.m_numberOfClusters = -1;
/*  587: 901 */     this.m_cobwebTree = null;
/*  588: 902 */     this.m_numberSplits = 0;
/*  589: 903 */     this.m_numberMerges = 0;
/*  590:     */     
/*  591:     */ 
/*  592: 906 */     getCapabilities().testWithFail(data);
/*  593:     */     
/*  594:     */ 
/*  595: 909 */     data = new Instances(data);
/*  596: 911 */     if (getSeed() >= 0) {
/*  597: 912 */       data.randomize(new Random(getSeed()));
/*  598:     */     }
/*  599: 915 */     for (int i = 0; i < data.numInstances(); i++) {
/*  600: 916 */       updateClusterer(data.instance(i));
/*  601:     */     }
/*  602: 919 */     updateFinished();
/*  603:     */   }
/*  604:     */   
/*  605:     */   public void updateFinished()
/*  606:     */   {
/*  607: 927 */     determineNumberOfClusters();
/*  608:     */   }
/*  609:     */   
/*  610:     */   public int clusterInstance(Instance instance)
/*  611:     */     throws Exception
/*  612:     */   {
/*  613: 940 */     CNode host = this.m_cobwebTree;
/*  614: 941 */     CNode temp = null;
/*  615:     */     
/*  616: 943 */     determineNumberOfClusters();
/*  617:     */     do
/*  618:     */     {
/*  619: 946 */       if (host.m_children == null)
/*  620:     */       {
/*  621: 947 */         temp = null;
/*  622: 948 */         break;
/*  623:     */       }
/*  624: 952 */       temp = host.findHost(instance, true);
/*  625: 955 */       if (temp != null) {
/*  626: 956 */         host = temp;
/*  627:     */       }
/*  628: 958 */     } while (temp != null);
/*  629: 960 */     return host.m_clusterNum;
/*  630:     */   }
/*  631:     */   
/*  632:     */   protected void determineNumberOfClusters()
/*  633:     */   {
/*  634: 970 */     if ((!this.m_numberOfClustersDetermined) && (this.m_cobwebTree != null))
/*  635:     */     {
/*  636: 971 */       int[] numClusts = new int[1];
/*  637: 972 */       numClusts[0] = 0;
/*  638:     */       try
/*  639:     */       {
/*  640: 974 */         this.m_cobwebTree.assignClusterNums(numClusts);
/*  641:     */       }
/*  642:     */       catch (Exception e)
/*  643:     */       {
/*  644: 976 */         e.printStackTrace();
/*  645: 977 */         numClusts[0] = 0;
/*  646:     */       }
/*  647: 979 */       this.m_numberOfClusters = numClusts[0];
/*  648:     */       
/*  649: 981 */       this.m_numberOfClustersDetermined = true;
/*  650:     */     }
/*  651:     */   }
/*  652:     */   
/*  653:     */   public int numberOfClusters()
/*  654:     */   {
/*  655: 992 */     determineNumberOfClusters();
/*  656: 993 */     return this.m_numberOfClusters;
/*  657:     */   }
/*  658:     */   
/*  659:     */   public CNode getTreeRoot()
/*  660:     */   {
/*  661:1002 */     return this.m_cobwebTree;
/*  662:     */   }
/*  663:     */   
/*  664:     */   public void updateClusterer(Instance newInstance)
/*  665:     */     throws Exception
/*  666:     */   {
/*  667:1013 */     this.m_numberOfClustersDetermined = false;
/*  668:1015 */     if (this.m_cobwebTree == null) {
/*  669:1016 */       this.m_cobwebTree = new CNode(newInstance.numAttributes(), newInstance);
/*  670:     */     } else {
/*  671:1018 */       this.m_cobwebTree.addInstance(newInstance);
/*  672:     */     }
/*  673:     */   }
/*  674:     */   
/*  675:     */   @Deprecated
/*  676:     */   public void addInstance(Instance newInstance)
/*  677:     */     throws Exception
/*  678:     */   {
/*  679:1032 */     updateClusterer(newInstance);
/*  680:     */   }
/*  681:     */   
/*  682:     */   public Enumeration<Option> listOptions()
/*  683:     */   {
/*  684:1042 */     Vector<Option> result = new Vector();
/*  685:     */     
/*  686:1044 */     result.addElement(new Option("\tAcuity.\n\t(default=1.0)", "A", 1, "-A <acuity>"));
/*  687:     */     
/*  688:     */ 
/*  689:1047 */     result.addElement(new Option("\tCutoff.\n\t(default=0.002)", "C", 1, "-C <cutoff>"));
/*  690:     */     
/*  691:     */ 
/*  692:1050 */     result.addElement(new Option("\tSave instance data.", "save-data", 0, "-save-data"));
/*  693:     */     
/*  694:     */ 
/*  695:1053 */     result.addAll(Collections.list(super.listOptions()));
/*  696:     */     
/*  697:1055 */     return result.elements();
/*  698:     */   }
/*  699:     */   
/*  700:     */   public void setOptions(String[] options)
/*  701:     */     throws Exception
/*  702:     */   {
/*  703:1097 */     String optionString = Utils.getOption('A', options);
/*  704:1098 */     if (optionString.length() != 0)
/*  705:     */     {
/*  706:1099 */       Double temp = new Double(optionString);
/*  707:1100 */       setAcuity(temp.doubleValue());
/*  708:     */     }
/*  709:     */     else
/*  710:     */     {
/*  711:1102 */       this.m_acuity = 1.0D;
/*  712:     */     }
/*  713:1104 */     optionString = Utils.getOption('C', options);
/*  714:1105 */     if (optionString.length() != 0)
/*  715:     */     {
/*  716:1106 */       Double temp = new Double(optionString);
/*  717:1107 */       setCutoff(temp.doubleValue());
/*  718:     */     }
/*  719:     */     else
/*  720:     */     {
/*  721:1109 */       this.m_cutoff = (0.01D * m_normal);
/*  722:     */     }
/*  723:1112 */     setSaveInstanceData(Utils.getFlag("save-data", options));
/*  724:     */     
/*  725:1114 */     super.setOptions(options);
/*  726:     */     
/*  727:1116 */     Utils.checkForRemainingOptions(options);
/*  728:     */   }
/*  729:     */   
/*  730:     */   public String acuityTipText()
/*  731:     */   {
/*  732:1126 */     return "set the minimum standard deviation for numeric attributes";
/*  733:     */   }
/*  734:     */   
/*  735:     */   public void setAcuity(double a)
/*  736:     */   {
/*  737:1135 */     this.m_acuity = a;
/*  738:     */   }
/*  739:     */   
/*  740:     */   public double getAcuity()
/*  741:     */   {
/*  742:1144 */     return this.m_acuity;
/*  743:     */   }
/*  744:     */   
/*  745:     */   public String cutoffTipText()
/*  746:     */   {
/*  747:1154 */     return "set the category utility threshold by which to prune nodes";
/*  748:     */   }
/*  749:     */   
/*  750:     */   public void setCutoff(double c)
/*  751:     */   {
/*  752:1163 */     this.m_cutoff = c;
/*  753:     */   }
/*  754:     */   
/*  755:     */   public double getCutoff()
/*  756:     */   {
/*  757:1172 */     return this.m_cutoff;
/*  758:     */   }
/*  759:     */   
/*  760:     */   public String saveInstanceDataTipText()
/*  761:     */   {
/*  762:1182 */     return "save instance information for visualization purposes";
/*  763:     */   }
/*  764:     */   
/*  765:     */   public boolean getSaveInstanceData()
/*  766:     */   {
/*  767:1192 */     return this.m_saveInstances;
/*  768:     */   }
/*  769:     */   
/*  770:     */   public void setSaveInstanceData(boolean newsaveInstances)
/*  771:     */   {
/*  772:1202 */     this.m_saveInstances = newsaveInstances;
/*  773:     */   }
/*  774:     */   
/*  775:     */   public String[] getOptions()
/*  776:     */   {
/*  777:1213 */     Vector<String> result = new Vector();
/*  778:     */     
/*  779:1215 */     result.add("-A");
/*  780:1216 */     result.add("" + this.m_acuity);
/*  781:1217 */     result.add("-C");
/*  782:1218 */     result.add("" + this.m_cutoff);
/*  783:1220 */     if (getSaveInstanceData()) {
/*  784:1221 */       result.add("-save-data");
/*  785:     */     }
/*  786:1224 */     Collections.addAll(result, super.getOptions());
/*  787:     */     
/*  788:1226 */     return (String[])result.toArray(new String[result.size()]);
/*  789:     */   }
/*  790:     */   
/*  791:     */   public String toString()
/*  792:     */   {
/*  793:1236 */     StringBuffer text = new StringBuffer();
/*  794:1237 */     if (this.m_cobwebTree == null) {
/*  795:1238 */       return "Cobweb hasn't been built yet!";
/*  796:     */     }
/*  797:1240 */     this.m_cobwebTree.dumpTree(0, text);
/*  798:1241 */     return "Number of merges: " + this.m_numberMerges + "\nNumber of splits: " + this.m_numberSplits + "\nNumber of clusters: " + numberOfClusters() + "\n" + text.toString() + "\n\n";
/*  799:     */   }
/*  800:     */   
/*  801:     */   public int graphType()
/*  802:     */   {
/*  803:1255 */     return 1;
/*  804:     */   }
/*  805:     */   
/*  806:     */   public String graph()
/*  807:     */     throws Exception
/*  808:     */   {
/*  809:1266 */     StringBuffer text = new StringBuffer();
/*  810:     */     
/*  811:1268 */     text.append("digraph CobwebTree {\n");
/*  812:1269 */     this.m_cobwebTree.graphTree(text);
/*  813:1270 */     text.append("}\n");
/*  814:1271 */     return text.toString();
/*  815:     */   }
/*  816:     */   
/*  817:     */   public String getRevision()
/*  818:     */   {
/*  819:1281 */     return RevisionUtils.extract("$Revision: 11556 $");
/*  820:     */   }
/*  821:     */   
/*  822:     */   public String seedTipText()
/*  823:     */   {
/*  824:1292 */     String result = super.seedTipText() + " Use -1 for no randomization.";
/*  825:     */     
/*  826:1294 */     return result;
/*  827:     */   }
/*  828:     */   
/*  829:     */   public static void main(String[] argv)
/*  830:     */   {
/*  831:1303 */     runClusterer(new Cobweb(), argv);
/*  832:     */   }
/*  833:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.clusterers.Cobweb
 * JD-Core Version:    0.7.0.1
 */