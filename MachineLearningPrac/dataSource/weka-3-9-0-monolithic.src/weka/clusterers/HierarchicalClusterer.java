/*    1:     */ package weka.clusterers;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.text.DecimalFormat;
/*    6:     */ import java.text.NumberFormat;
/*    7:     */ import java.util.Collections;
/*    8:     */ import java.util.Comparator;
/*    9:     */ import java.util.Enumeration;
/*   10:     */ import java.util.Locale;
/*   11:     */ import java.util.PriorityQueue;
/*   12:     */ import java.util.Vector;
/*   13:     */ import weka.core.Attribute;
/*   14:     */ import weka.core.Capabilities;
/*   15:     */ import weka.core.Capabilities.Capability;
/*   16:     */ import weka.core.DistanceFunction;
/*   17:     */ import weka.core.Drawable;
/*   18:     */ import weka.core.EuclideanDistance;
/*   19:     */ import weka.core.Instance;
/*   20:     */ import weka.core.Instances;
/*   21:     */ import weka.core.Option;
/*   22:     */ import weka.core.OptionHandler;
/*   23:     */ import weka.core.RevisionUtils;
/*   24:     */ import weka.core.SelectedTag;
/*   25:     */ import weka.core.Tag;
/*   26:     */ import weka.core.Utils;
/*   27:     */ 
/*   28:     */ public class HierarchicalClusterer
/*   29:     */   extends AbstractClusterer
/*   30:     */   implements OptionHandler, Drawable
/*   31:     */ {
/*   32:     */   private static final long serialVersionUID = 1L;
/*   33:     */   protected boolean m_bDistanceIsBranchLength;
/*   34:     */   Instances m_instances;
/*   35:     */   int m_nNumClusters;
/*   36:     */   protected DistanceFunction m_DistanceFunction;
/*   37:     */   static final int SINGLE = 0;
/*   38:     */   static final int COMPLETE = 1;
/*   39:     */   static final int AVERAGE = 2;
/*   40:     */   static final int MEAN = 3;
/*   41:     */   static final int CENTROID = 4;
/*   42:     */   static final int WARD = 5;
/*   43:     */   static final int ADJCOMPLETE = 6;
/*   44:     */   static final int NEIGHBOR_JOINING = 7;
/*   45:     */   
/*   46:     */   public void setNumClusters(int nClusters)
/*   47:     */   {
/*   48: 110 */     this.m_nNumClusters = Math.max(1, nClusters);
/*   49:     */   }
/*   50:     */   
/*   51:     */   public int getNumClusters()
/*   52:     */   {
/*   53: 114 */     return this.m_nNumClusters;
/*   54:     */   }
/*   55:     */   
/*   56:     */   public DistanceFunction getDistanceFunction()
/*   57:     */   {
/*   58: 121 */     return this.m_DistanceFunction;
/*   59:     */   }
/*   60:     */   
/*   61:     */   public void setDistanceFunction(DistanceFunction distanceFunction)
/*   62:     */   {
/*   63: 125 */     this.m_DistanceFunction = distanceFunction;
/*   64:     */   }
/*   65:     */   
/*   66:     */   class Tuple
/*   67:     */   {
/*   68:     */     double m_fDist;
/*   69:     */     int m_iCluster1;
/*   70:     */     int m_iCluster2;
/*   71:     */     int m_nClusterSize1;
/*   72:     */     int m_nClusterSize2;
/*   73:     */     
/*   74:     */     public Tuple(double d, int i, int j, int nSize1, int nSize2)
/*   75:     */     {
/*   76: 134 */       this.m_fDist = d;
/*   77: 135 */       this.m_iCluster1 = i;
/*   78: 136 */       this.m_iCluster2 = j;
/*   79: 137 */       this.m_nClusterSize1 = nSize1;
/*   80: 138 */       this.m_nClusterSize2 = nSize2;
/*   81:     */     }
/*   82:     */   }
/*   83:     */   
/*   84:     */   class TupleComparator
/*   85:     */     implements Comparator<HierarchicalClusterer.Tuple>
/*   86:     */   {
/*   87:     */     TupleComparator() {}
/*   88:     */     
/*   89:     */     public int compare(HierarchicalClusterer.Tuple o1, HierarchicalClusterer.Tuple o2)
/*   90:     */     {
/*   91: 152 */       if (o1.m_fDist < o2.m_fDist) {
/*   92: 153 */         return -1;
/*   93:     */       }
/*   94: 154 */       if (o1.m_fDist == o2.m_fDist) {
/*   95: 155 */         return 0;
/*   96:     */       }
/*   97: 157 */       return 1;
/*   98:     */     }
/*   99:     */   }
/*  100:     */   
/*  101: 170 */   public static final Tag[] TAGS_LINK_TYPE = { new Tag(0, "SINGLE"), new Tag(1, "COMPLETE"), new Tag(2, "AVERAGE"), new Tag(3, "MEAN"), new Tag(4, "CENTROID"), new Tag(5, "WARD"), new Tag(6, "ADJCOMPLETE"), new Tag(7, "NEIGHBOR_JOINING") };
/*  102:     */   int m_nLinkType;
/*  103:     */   boolean m_bPrintNewick;
/*  104:     */   protected Node[] m_clusters;
/*  105:     */   int[] m_nClusterNr;
/*  106:     */   
/*  107:     */   public HierarchicalClusterer()
/*  108:     */   {
/*  109: 101 */     this.m_bDistanceIsBranchLength = false;
/*  110:     */     
/*  111:     */ 
/*  112:     */ 
/*  113:     */ 
/*  114:     */ 
/*  115: 107 */     this.m_nNumClusters = 2;
/*  116:     */     
/*  117:     */ 
/*  118:     */ 
/*  119:     */ 
/*  120:     */ 
/*  121:     */ 
/*  122:     */ 
/*  123:     */ 
/*  124:     */ 
/*  125:     */ 
/*  126: 118 */     this.m_DistanceFunction = new EuclideanDistance();
/*  127:     */     
/*  128:     */ 
/*  129:     */ 
/*  130:     */ 
/*  131:     */ 
/*  132:     */ 
/*  133:     */ 
/*  134:     */ 
/*  135:     */ 
/*  136:     */ 
/*  137:     */ 
/*  138:     */ 
/*  139:     */ 
/*  140:     */ 
/*  141:     */ 
/*  142:     */ 
/*  143:     */ 
/*  144:     */ 
/*  145:     */ 
/*  146:     */ 
/*  147:     */ 
/*  148:     */ 
/*  149:     */ 
/*  150:     */ 
/*  151:     */ 
/*  152:     */ 
/*  153:     */ 
/*  154:     */ 
/*  155:     */ 
/*  156:     */ 
/*  157:     */ 
/*  158:     */ 
/*  159:     */ 
/*  160:     */ 
/*  161:     */ 
/*  162:     */ 
/*  163:     */ 
/*  164:     */ 
/*  165:     */ 
/*  166:     */ 
/*  167:     */ 
/*  168:     */ 
/*  169:     */ 
/*  170:     */ 
/*  171:     */ 
/*  172:     */ 
/*  173:     */ 
/*  174:     */ 
/*  175:     */ 
/*  176:     */ 
/*  177:     */ 
/*  178:     */ 
/*  179:     */ 
/*  180:     */ 
/*  181:     */ 
/*  182:     */ 
/*  183:     */ 
/*  184:     */ 
/*  185:     */ 
/*  186:     */ 
/*  187: 179 */     this.m_nLinkType = 0;
/*  188:     */     
/*  189: 181 */     this.m_bPrintNewick = true;
/*  190:     */   }
/*  191:     */   
/*  192:     */   public boolean getPrintNewick()
/*  193:     */   {
/*  194: 184 */     return this.m_bPrintNewick;
/*  195:     */   }
/*  196:     */   
/*  197:     */   public void setPrintNewick(boolean bPrintNewick)
/*  198:     */   {
/*  199: 188 */     this.m_bPrintNewick = bPrintNewick;
/*  200:     */   }
/*  201:     */   
/*  202:     */   public void setLinkType(SelectedTag newLinkType)
/*  203:     */   {
/*  204: 192 */     if (newLinkType.getTags() == TAGS_LINK_TYPE) {
/*  205: 193 */       this.m_nLinkType = newLinkType.getSelectedTag().getID();
/*  206:     */     }
/*  207:     */   }
/*  208:     */   
/*  209:     */   public SelectedTag getLinkType()
/*  210:     */   {
/*  211: 198 */     return new SelectedTag(this.m_nLinkType, TAGS_LINK_TYPE);
/*  212:     */   }
/*  213:     */   
/*  214:     */   class Node
/*  215:     */     implements Serializable
/*  216:     */   {
/*  217:     */     private static final long serialVersionUID = 7639483515789717908L;
/*  218:     */     Node m_left;
/*  219:     */     Node m_right;
/*  220:     */     Node m_parent;
/*  221:     */     int m_iLeftInstance;
/*  222:     */     int m_iRightInstance;
/*  223: 212 */     double m_fLeftLength = 0.0D;
/*  224: 213 */     double m_fRightLength = 0.0D;
/*  225: 214 */     double m_fHeight = 0.0D;
/*  226:     */     
/*  227:     */     Node() {}
/*  228:     */     
/*  229:     */     public String toString(int attIndex)
/*  230:     */     {
/*  231: 217 */       NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "US"));
/*  232: 218 */       DecimalFormat myFormatter = (DecimalFormat)nf;
/*  233: 219 */       myFormatter.applyPattern("#.#####");
/*  234: 221 */       if (this.m_left == null)
/*  235:     */       {
/*  236: 222 */         if (this.m_right == null) {
/*  237: 223 */           return "(" + HierarchicalClusterer.this.m_instances.instance(this.m_iLeftInstance).stringValue(attIndex) + ":" + myFormatter.format(this.m_fLeftLength) + "," + HierarchicalClusterer.this.m_instances.instance(this.m_iRightInstance).stringValue(attIndex) + ":" + myFormatter.format(this.m_fRightLength) + ")";
/*  238:     */         }
/*  239: 229 */         return "(" + HierarchicalClusterer.this.m_instances.instance(this.m_iLeftInstance).stringValue(attIndex) + ":" + myFormatter.format(this.m_fLeftLength) + "," + this.m_right.toString(attIndex) + ":" + myFormatter.format(this.m_fRightLength) + ")";
/*  240:     */       }
/*  241: 236 */       if (this.m_right == null) {
/*  242: 237 */         return "(" + this.m_left.toString(attIndex) + ":" + myFormatter.format(this.m_fLeftLength) + "," + HierarchicalClusterer.this.m_instances.instance(this.m_iRightInstance).stringValue(attIndex) + ":" + myFormatter.format(this.m_fRightLength) + ")";
/*  243:     */       }
/*  244: 242 */       return "(" + this.m_left.toString(attIndex) + ":" + myFormatter.format(this.m_fLeftLength) + "," + this.m_right.toString(attIndex) + ":" + myFormatter.format(this.m_fRightLength) + ")";
/*  245:     */     }
/*  246:     */     
/*  247:     */     public String toString2(int attIndex)
/*  248:     */     {
/*  249: 251 */       NumberFormat nf = NumberFormat.getNumberInstance(new Locale("en", "US"));
/*  250: 252 */       DecimalFormat myFormatter = (DecimalFormat)nf;
/*  251: 253 */       myFormatter.applyPattern("#.#####");
/*  252: 255 */       if (this.m_left == null)
/*  253:     */       {
/*  254: 256 */         if (this.m_right == null) {
/*  255: 257 */           return "(" + HierarchicalClusterer.this.m_instances.instance(this.m_iLeftInstance).value(attIndex) + ":" + myFormatter.format(this.m_fLeftLength) + "," + HierarchicalClusterer.this.m_instances.instance(this.m_iRightInstance).value(attIndex) + ":" + myFormatter.format(this.m_fRightLength) + ")";
/*  256:     */         }
/*  257: 262 */         return "(" + HierarchicalClusterer.this.m_instances.instance(this.m_iLeftInstance).value(attIndex) + ":" + myFormatter.format(this.m_fLeftLength) + "," + this.m_right.toString2(attIndex) + ":" + myFormatter.format(this.m_fRightLength) + ")";
/*  258:     */       }
/*  259: 268 */       if (this.m_right == null) {
/*  260: 269 */         return "(" + this.m_left.toString2(attIndex) + ":" + myFormatter.format(this.m_fLeftLength) + "," + HierarchicalClusterer.this.m_instances.instance(this.m_iRightInstance).value(attIndex) + ":" + myFormatter.format(this.m_fRightLength) + ")";
/*  261:     */       }
/*  262: 274 */       return "(" + this.m_left.toString2(attIndex) + ":" + myFormatter.format(this.m_fLeftLength) + "," + this.m_right.toString2(attIndex) + ":" + myFormatter.format(this.m_fRightLength) + ")";
/*  263:     */     }
/*  264:     */     
/*  265:     */     void setHeight(double fHeight1, double fHeight2)
/*  266:     */     {
/*  267: 283 */       this.m_fHeight = fHeight1;
/*  268: 284 */       if (this.m_left == null) {
/*  269: 285 */         this.m_fLeftLength = fHeight1;
/*  270:     */       } else {
/*  271: 287 */         this.m_fLeftLength = (fHeight1 - this.m_left.m_fHeight);
/*  272:     */       }
/*  273: 289 */       if (this.m_right == null) {
/*  274: 290 */         this.m_fRightLength = fHeight2;
/*  275:     */       } else {
/*  276: 292 */         this.m_fRightLength = (fHeight2 - this.m_right.m_fHeight);
/*  277:     */       }
/*  278:     */     }
/*  279:     */     
/*  280:     */     void setLength(double fLength1, double fLength2)
/*  281:     */     {
/*  282: 297 */       this.m_fLeftLength = fLength1;
/*  283: 298 */       this.m_fRightLength = fLength2;
/*  284: 299 */       this.m_fHeight = fLength1;
/*  285: 300 */       if (this.m_left != null) {
/*  286: 301 */         this.m_fHeight += this.m_left.m_fHeight;
/*  287:     */       }
/*  288:     */     }
/*  289:     */   }
/*  290:     */   
/*  291:     */   public void buildClusterer(Instances data)
/*  292:     */     throws Exception
/*  293:     */   {
/*  294: 312 */     this.m_instances = data;
/*  295: 313 */     int nInstances = this.m_instances.numInstances();
/*  296: 314 */     if (nInstances == 0) {
/*  297: 315 */       return;
/*  298:     */     }
/*  299: 317 */     this.m_DistanceFunction.setInstances(this.m_instances);
/*  300:     */     
/*  301:     */ 
/*  302:     */ 
/*  303: 321 */     Vector<Integer>[] nClusterID = new Vector[data.numInstances()];
/*  304: 322 */     for (int i = 0; i < data.numInstances(); i++)
/*  305:     */     {
/*  306: 323 */       nClusterID[i] = new Vector();
/*  307: 324 */       nClusterID[i].add(Integer.valueOf(i));
/*  308:     */     }
/*  309: 327 */     int nClusters = data.numInstances();
/*  310:     */     
/*  311:     */ 
/*  312: 330 */     Node[] clusterNodes = new Node[nInstances];
/*  313: 331 */     if (this.m_nLinkType == 7) {
/*  314: 332 */       neighborJoining(nClusters, nClusterID, clusterNodes);
/*  315:     */     } else {
/*  316: 334 */       doLinkClustering(nClusters, nClusterID, clusterNodes);
/*  317:     */     }
/*  318: 339 */     int iCurrent = 0;
/*  319: 340 */     this.m_clusters = new Node[this.m_nNumClusters];
/*  320: 341 */     this.m_nClusterNr = new int[nInstances];
/*  321: 342 */     for (int i = 0; i < nInstances; i++) {
/*  322: 343 */       if (nClusterID[i].size() > 0)
/*  323:     */       {
/*  324: 344 */         for (int j = 0; j < nClusterID[i].size(); j++) {
/*  325: 345 */           this.m_nClusterNr[((Integer)nClusterID[i].elementAt(j)).intValue()] = iCurrent;
/*  326:     */         }
/*  327: 347 */         this.m_clusters[iCurrent] = clusterNodes[i];
/*  328: 348 */         iCurrent++;
/*  329:     */       }
/*  330:     */     }
/*  331:     */   }
/*  332:     */   
/*  333:     */   void neighborJoining(int nClusters, Vector<Integer>[] nClusterID, Node[] clusterNodes)
/*  334:     */   {
/*  335: 365 */     int n = this.m_instances.numInstances();
/*  336:     */     
/*  337: 367 */     double[][] fDist = new double[nClusters][nClusters];
/*  338: 368 */     for (int i = 0; i < nClusters; i++)
/*  339:     */     {
/*  340: 369 */       fDist[i][i] = 0.0D;
/*  341: 370 */       for (int j = i + 1; j < nClusters; j++)
/*  342:     */       {
/*  343: 371 */         fDist[i][j] = getDistance0(nClusterID[i], nClusterID[j]);
/*  344: 372 */         fDist[j][i] = fDist[i][j];
/*  345:     */       }
/*  346:     */     }
/*  347: 376 */     double[] fSeparationSums = new double[n];
/*  348: 377 */     double[] fSeparations = new double[n];
/*  349: 378 */     int[] nNextActive = new int[n];
/*  350: 381 */     for (int i = 0; i < n; i++)
/*  351:     */     {
/*  352: 382 */       double fSum = 0.0D;
/*  353: 383 */       for (int j = 0; j < n; j++) {
/*  354: 384 */         fSum += fDist[i][j];
/*  355:     */       }
/*  356: 386 */       fSeparationSums[i] = fSum;
/*  357: 387 */       fSeparations[i] = (fSum / (nClusters - 2));
/*  358: 388 */       nNextActive[i] = (i + 1);
/*  359:     */     }
/*  360: 391 */     while (nClusters > 2)
/*  361:     */     {
/*  362: 393 */       int iMin1 = -1;
/*  363: 394 */       int iMin2 = -1;
/*  364: 395 */       double fMin = 1.7976931348623157E+308D;
/*  365: 396 */       if (this.m_Debug)
/*  366:     */       {
/*  367: 397 */         for (int i = 0; i < n; i++) {
/*  368: 398 */           if (nClusterID[i].size() > 0)
/*  369:     */           {
/*  370: 399 */             double[] fRow = fDist[i];
/*  371: 400 */             double fSep1 = fSeparations[i];
/*  372: 401 */             for (int j = 0; j < n; j++) {
/*  373: 402 */               if ((nClusterID[j].size() > 0) && (i != j))
/*  374:     */               {
/*  375: 403 */                 double fSep2 = fSeparations[j];
/*  376: 404 */                 double fVal = fRow[j] - fSep1 - fSep2;
/*  377: 406 */                 if (fVal < fMin)
/*  378:     */                 {
/*  379: 408 */                   iMin1 = i;
/*  380: 409 */                   iMin2 = j;
/*  381: 410 */                   fMin = fVal;
/*  382:     */                 }
/*  383:     */               }
/*  384:     */             }
/*  385:     */           }
/*  386:     */         }
/*  387:     */       }
/*  388:     */       else
/*  389:     */       {
/*  390: 417 */         int i = 0;
/*  391: 418 */         while (i < n)
/*  392:     */         {
/*  393: 419 */           double fSep1 = fSeparations[i];
/*  394: 420 */           double[] fRow = fDist[i];
/*  395: 421 */           int j = nNextActive[i];
/*  396: 422 */           while (j < n)
/*  397:     */           {
/*  398: 423 */             double fSep2 = fSeparations[j];
/*  399: 424 */             double fVal = fRow[j] - fSep1 - fSep2;
/*  400: 425 */             if (fVal < fMin)
/*  401:     */             {
/*  402: 427 */               iMin1 = i;
/*  403: 428 */               iMin2 = j;
/*  404: 429 */               fMin = fVal;
/*  405:     */             }
/*  406: 431 */             j = nNextActive[j];
/*  407:     */           }
/*  408: 433 */           i = nNextActive[i];
/*  409:     */         }
/*  410:     */       }
/*  411: 437 */       double fMinDistance = fDist[iMin1][iMin2];
/*  412: 438 */       nClusters--;
/*  413: 439 */       double fSep1 = fSeparations[iMin1];
/*  414: 440 */       double fSep2 = fSeparations[iMin2];
/*  415: 441 */       double fDist1 = 0.5D * fMinDistance + 0.5D * (fSep1 - fSep2);
/*  416: 442 */       double fDist2 = 0.5D * fMinDistance + 0.5D * (fSep2 - fSep1);
/*  417: 443 */       if (nClusters > 2)
/*  418:     */       {
/*  419: 445 */         double fNewSeparationSum = 0.0D;
/*  420: 446 */         double fMutualDistance = fDist[iMin1][iMin2];
/*  421: 447 */         double[] fRow1 = fDist[iMin1];
/*  422: 448 */         double[] fRow2 = fDist[iMin2];
/*  423: 449 */         for (int i = 0; i < n; i++) {
/*  424: 450 */           if ((i == iMin1) || (i == iMin2) || (nClusterID[i].size() == 0))
/*  425:     */           {
/*  426: 451 */             fRow1[i] = 0.0D;
/*  427:     */           }
/*  428:     */           else
/*  429:     */           {
/*  430: 453 */             double fVal1 = fRow1[i];
/*  431: 454 */             double fVal2 = fRow2[i];
/*  432: 455 */             double fDistance = (fVal1 + fVal2 - fMutualDistance) / 2.0D;
/*  433: 456 */             fNewSeparationSum += fDistance;
/*  434:     */             
/*  435: 458 */             fSeparationSums[i] += fDistance - fVal1 - fVal2;
/*  436: 459 */             fSeparationSums[i] /= (nClusters - 2);
/*  437: 460 */             fRow1[i] = fDistance;
/*  438: 461 */             fDist[i][iMin1] = fDistance;
/*  439:     */           }
/*  440:     */         }
/*  441: 464 */         fSeparationSums[iMin1] = fNewSeparationSum;
/*  442: 465 */         fSeparations[iMin1] = (fNewSeparationSum / (nClusters - 2));
/*  443: 466 */         fSeparationSums[iMin2] = 0.0D;
/*  444: 467 */         merge(iMin1, iMin2, fDist1, fDist2, nClusterID, clusterNodes);
/*  445: 468 */         int iPrev = iMin2;
/*  446: 471 */         while (nClusterID[iPrev].size() == 0) {
/*  447: 472 */           iPrev--;
/*  448:     */         }
/*  449: 474 */         nNextActive[iPrev] = nNextActive[iMin2];
/*  450:     */       }
/*  451:     */       else
/*  452:     */       {
/*  453: 476 */         merge(iMin1, iMin2, fDist1, fDist2, nClusterID, clusterNodes);
/*  454: 477 */         break;
/*  455:     */       }
/*  456:     */     }
/*  457: 481 */     for (int i = 0; i < n; i++) {
/*  458: 482 */       if (nClusterID[i].size() > 0) {
/*  459: 483 */         for (int j = i + 1; j < n; j++) {
/*  460: 484 */           if (nClusterID[j].size() > 0)
/*  461:     */           {
/*  462: 485 */             double fDist1 = fDist[i][j];
/*  463: 486 */             if (nClusterID[i].size() == 1)
/*  464:     */             {
/*  465: 487 */               merge(i, j, fDist1, 0.0D, nClusterID, clusterNodes); break;
/*  466:     */             }
/*  467: 488 */             if (nClusterID[j].size() == 1)
/*  468:     */             {
/*  469: 489 */               merge(i, j, 0.0D, fDist1, nClusterID, clusterNodes); break;
/*  470:     */             }
/*  471: 491 */             merge(i, j, fDist1 / 2.0D, fDist1 / 2.0D, nClusterID, clusterNodes);
/*  472:     */             
/*  473: 493 */             break;
/*  474:     */           }
/*  475:     */         }
/*  476:     */       }
/*  477:     */     }
/*  478:     */   }
/*  479:     */   
/*  480:     */   void doLinkClustering(int nClusters, Vector<Integer>[] nClusterID, Node[] clusterNodes)
/*  481:     */   {
/*  482: 510 */     int nInstances = this.m_instances.numInstances();
/*  483: 511 */     PriorityQueue<Tuple> queue = new PriorityQueue(nClusters * nClusters / 2, new TupleComparator());
/*  484:     */     
/*  485: 513 */     double[][] fDistance0 = new double[nClusters][nClusters];
/*  486: 514 */     double[][] fClusterDistance = (double[][])null;
/*  487: 515 */     if (this.m_Debug) {
/*  488: 516 */       fClusterDistance = new double[nClusters][nClusters];
/*  489:     */     }
/*  490: 518 */     for (int i = 0; i < nClusters; i++)
/*  491:     */     {
/*  492: 519 */       fDistance0[i][i] = 0.0D;
/*  493: 520 */       for (int j = i + 1; j < nClusters; j++)
/*  494:     */       {
/*  495: 521 */         fDistance0[i][j] = getDistance0(nClusterID[i], nClusterID[j]);
/*  496: 522 */         fDistance0[j][i] = fDistance0[i][j];
/*  497: 523 */         queue.add(new Tuple(fDistance0[i][j], i, j, 1, 1));
/*  498: 524 */         if (this.m_Debug)
/*  499:     */         {
/*  500: 525 */           fClusterDistance[i][j] = fDistance0[i][j];
/*  501: 526 */           fClusterDistance[j][i] = fDistance0[i][j];
/*  502:     */         }
/*  503:     */       }
/*  504:     */     }
/*  505: 530 */     while (nClusters > this.m_nNumClusters)
/*  506:     */     {
/*  507: 531 */       int iMin1 = -1;
/*  508: 532 */       int iMin2 = -1;
/*  509: 534 */       if (this.m_Debug)
/*  510:     */       {
/*  511: 536 */         double fMinDistance = 1.7976931348623157E+308D;
/*  512: 537 */         for (int i = 0; i < nInstances; i++) {
/*  513: 538 */           if (nClusterID[i].size() > 0) {
/*  514: 539 */             for (int j = i + 1; j < nInstances; j++) {
/*  515: 540 */               if (nClusterID[j].size() > 0)
/*  516:     */               {
/*  517: 541 */                 double fDist = fClusterDistance[i][j];
/*  518: 542 */                 if (fDist < fMinDistance)
/*  519:     */                 {
/*  520: 543 */                   fMinDistance = fDist;
/*  521: 544 */                   iMin1 = i;
/*  522: 545 */                   iMin2 = j;
/*  523:     */                 }
/*  524:     */               }
/*  525:     */             }
/*  526:     */           }
/*  527:     */         }
/*  528: 551 */         merge(iMin1, iMin2, fMinDistance, fMinDistance, nClusterID, clusterNodes);
/*  529:     */       }
/*  530:     */       else
/*  531:     */       {
/*  532:     */         Tuple t;
/*  533:     */         do
/*  534:     */         {
/*  535: 557 */           t = (Tuple)queue.poll();
/*  536: 559 */         } while ((t != null) && ((nClusterID[t.m_iCluster1].size() != t.m_nClusterSize1) || (nClusterID[t.m_iCluster2].size() != t.m_nClusterSize2)));
/*  537: 561 */         iMin1 = t.m_iCluster1;
/*  538: 562 */         iMin2 = t.m_iCluster2;
/*  539: 563 */         merge(iMin1, iMin2, t.m_fDist, t.m_fDist, nClusterID, clusterNodes);
/*  540:     */       }
/*  541: 568 */       for (int i = 0; i < nInstances; i++) {
/*  542: 569 */         if ((i != iMin1) && (nClusterID[i].size() != 0))
/*  543:     */         {
/*  544: 570 */           int i1 = Math.min(iMin1, i);
/*  545: 571 */           int i2 = Math.max(iMin1, i);
/*  546: 572 */           double fDistance = getDistance(fDistance0, nClusterID[i1], nClusterID[i2]);
/*  547: 574 */           if (this.m_Debug)
/*  548:     */           {
/*  549: 575 */             fClusterDistance[i1][i2] = fDistance;
/*  550: 576 */             fClusterDistance[i2][i1] = fDistance;
/*  551:     */           }
/*  552: 578 */           queue.add(new Tuple(fDistance, i1, i2, nClusterID[i1].size(), nClusterID[i2].size()));
/*  553:     */         }
/*  554:     */       }
/*  555: 583 */       nClusters--;
/*  556:     */     }
/*  557:     */   }
/*  558:     */   
/*  559:     */   void merge(int iMin1, int iMin2, double fDist1, double fDist2, Vector<Integer>[] nClusterID, Node[] clusterNodes)
/*  560:     */   {
/*  561: 589 */     if (this.m_Debug) {
/*  562: 590 */       System.err.println("Merging " + iMin1 + " " + iMin2 + " " + fDist1 + " " + fDist2);
/*  563:     */     }
/*  564: 593 */     if (iMin1 > iMin2)
/*  565:     */     {
/*  566: 594 */       int h = iMin1;
/*  567: 595 */       iMin1 = iMin2;
/*  568: 596 */       iMin2 = h;
/*  569: 597 */       double f = fDist1;
/*  570: 598 */       fDist1 = fDist2;
/*  571: 599 */       fDist2 = f;
/*  572:     */     }
/*  573: 601 */     nClusterID[iMin1].addAll(nClusterID[iMin2]);
/*  574: 602 */     nClusterID[iMin2].removeAllElements();
/*  575:     */     
/*  576:     */ 
/*  577: 605 */     Node node = new Node();
/*  578: 606 */     if (clusterNodes[iMin1] == null)
/*  579:     */     {
/*  580: 607 */       node.m_iLeftInstance = iMin1;
/*  581:     */     }
/*  582:     */     else
/*  583:     */     {
/*  584: 609 */       node.m_left = clusterNodes[iMin1];
/*  585: 610 */       clusterNodes[iMin1].m_parent = node;
/*  586:     */     }
/*  587: 612 */     if (clusterNodes[iMin2] == null)
/*  588:     */     {
/*  589: 613 */       node.m_iRightInstance = iMin2;
/*  590:     */     }
/*  591:     */     else
/*  592:     */     {
/*  593: 615 */       node.m_right = clusterNodes[iMin2];
/*  594: 616 */       clusterNodes[iMin2].m_parent = node;
/*  595:     */     }
/*  596: 618 */     if (this.m_bDistanceIsBranchLength) {
/*  597: 619 */       node.setLength(fDist1, fDist2);
/*  598:     */     } else {
/*  599: 621 */       node.setHeight(fDist1, fDist2);
/*  600:     */     }
/*  601: 623 */     clusterNodes[iMin1] = node;
/*  602:     */   }
/*  603:     */   
/*  604:     */   double getDistance0(Vector<Integer> cluster1, Vector<Integer> cluster2)
/*  605:     */   {
/*  606: 628 */     double fBestDist = 1.7976931348623157E+308D;
/*  607: 629 */     switch (this.m_nLinkType)
/*  608:     */     {
/*  609:     */     case 0: 
/*  610:     */     case 1: 
/*  611:     */     case 2: 
/*  612:     */     case 3: 
/*  613:     */     case 4: 
/*  614:     */     case 6: 
/*  615:     */     case 7: 
/*  616: 638 */       Instance instance1 = (Instance)this.m_instances.instance(((Integer)cluster1.elementAt(0)).intValue()).copy();
/*  617:     */       
/*  618: 640 */       Instance instance2 = (Instance)this.m_instances.instance(((Integer)cluster2.elementAt(0)).intValue()).copy();
/*  619:     */       
/*  620: 642 */       fBestDist = this.m_DistanceFunction.distance(instance1, instance2);
/*  621: 643 */       break;
/*  622:     */     case 5: 
/*  623: 649 */       double ESS1 = calcESS(cluster1);
/*  624: 650 */       double ESS2 = calcESS(cluster2);
/*  625: 651 */       Vector<Integer> merged = new Vector();
/*  626: 652 */       merged.addAll(cluster1);
/*  627: 653 */       merged.addAll(cluster2);
/*  628: 654 */       double ESS = calcESS(merged);
/*  629: 655 */       fBestDist = ESS * merged.size() - ESS1 * cluster1.size() - ESS2 * cluster2.size();
/*  630:     */     }
/*  631: 660 */     return fBestDist;
/*  632:     */   }
/*  633:     */   
/*  634:     */   double getDistance(double[][] fDistance, Vector<Integer> cluster1, Vector<Integer> cluster2)
/*  635:     */   {
/*  636: 672 */     double fBestDist = 1.7976931348623157E+308D;
/*  637: 673 */     switch (this.m_nLinkType)
/*  638:     */     {
/*  639:     */     case 0: 
/*  640: 678 */       fBestDist = 1.7976931348623157E+308D;
/*  641: 679 */       for (int i = 0; i < cluster1.size(); i++)
/*  642:     */       {
/*  643: 680 */         int i1 = ((Integer)cluster1.elementAt(i)).intValue();
/*  644: 681 */         for (int j = 0; j < cluster2.size(); j++)
/*  645:     */         {
/*  646: 682 */           int i2 = ((Integer)cluster2.elementAt(j)).intValue();
/*  647: 683 */           double fDist = fDistance[i1][i2];
/*  648: 684 */           if (fBestDist > fDist) {
/*  649: 685 */             fBestDist = fDist;
/*  650:     */           }
/*  651:     */         }
/*  652:     */       }
/*  653: 689 */       break;
/*  654:     */     case 1: 
/*  655:     */     case 6: 
/*  656: 695 */       fBestDist = 0.0D;
/*  657: 696 */       for (int i = 0; i < cluster1.size(); i++)
/*  658:     */       {
/*  659: 697 */         int i1 = ((Integer)cluster1.elementAt(i)).intValue();
/*  660: 698 */         for (int j = 0; j < cluster2.size(); j++)
/*  661:     */         {
/*  662: 699 */           int i2 = ((Integer)cluster2.elementAt(j)).intValue();
/*  663: 700 */           double fDist = fDistance[i1][i2];
/*  664: 701 */           if (fBestDist < fDist) {
/*  665: 702 */             fBestDist = fDist;
/*  666:     */           }
/*  667:     */         }
/*  668:     */       }
/*  669: 706 */       if (this.m_nLinkType != 1)
/*  670:     */       {
/*  671: 710 */         double fMaxDist = 0.0D;
/*  672: 711 */         for (int i = 0; i < cluster1.size(); i++)
/*  673:     */         {
/*  674: 712 */           int i1 = ((Integer)cluster1.elementAt(i)).intValue();
/*  675: 713 */           for (int j = i + 1; j < cluster1.size(); j++)
/*  676:     */           {
/*  677: 714 */             int i2 = ((Integer)cluster1.elementAt(j)).intValue();
/*  678: 715 */             double fDist = fDistance[i1][i2];
/*  679: 716 */             if (fMaxDist < fDist) {
/*  680: 717 */               fMaxDist = fDist;
/*  681:     */             }
/*  682:     */           }
/*  683:     */         }
/*  684: 721 */         for (int i = 0; i < cluster2.size(); i++)
/*  685:     */         {
/*  686: 722 */           int i1 = ((Integer)cluster2.elementAt(i)).intValue();
/*  687: 723 */           for (int j = i + 1; j < cluster2.size(); j++)
/*  688:     */           {
/*  689: 724 */             int i2 = ((Integer)cluster2.elementAt(j)).intValue();
/*  690: 725 */             double fDist = fDistance[i1][i2];
/*  691: 726 */             if (fMaxDist < fDist) {
/*  692: 727 */               fMaxDist = fDist;
/*  693:     */             }
/*  694:     */           }
/*  695:     */         }
/*  696: 731 */         fBestDist -= fMaxDist;
/*  697:     */       }
/*  698: 732 */       break;
/*  699:     */     case 2: 
/*  700: 735 */       fBestDist = 0.0D;
/*  701: 736 */       for (int i = 0; i < cluster1.size(); i++)
/*  702:     */       {
/*  703: 737 */         int i1 = ((Integer)cluster1.elementAt(i)).intValue();
/*  704: 738 */         for (int j = 0; j < cluster2.size(); j++)
/*  705:     */         {
/*  706: 739 */           int i2 = ((Integer)cluster2.elementAt(j)).intValue();
/*  707: 740 */           fBestDist += fDistance[i1][i2];
/*  708:     */         }
/*  709:     */       }
/*  710: 743 */       fBestDist /= cluster1.size() * cluster2.size();
/*  711: 744 */       break;
/*  712:     */     case 3: 
/*  713: 748 */       Vector<Integer> merged = new Vector();
/*  714: 749 */       merged.addAll(cluster1);
/*  715: 750 */       merged.addAll(cluster2);
/*  716: 751 */       fBestDist = 0.0D;
/*  717: 752 */       for (int i = 0; i < merged.size(); i++)
/*  718:     */       {
/*  719: 753 */         int i1 = ((Integer)merged.elementAt(i)).intValue();
/*  720: 754 */         for (int j = i + 1; j < merged.size(); j++)
/*  721:     */         {
/*  722: 755 */           int i2 = ((Integer)merged.elementAt(j)).intValue();
/*  723: 756 */           fBestDist += fDistance[i1][i2];
/*  724:     */         }
/*  725:     */       }
/*  726: 759 */       int n = merged.size();
/*  727: 760 */       fBestDist /= n * (n - 1.0D) / 2.0D;
/*  728:     */       
/*  729: 762 */       break;
/*  730:     */     case 4: 
/*  731: 765 */       double[] fValues1 = new double[this.m_instances.numAttributes()];
/*  732: 766 */       for (int i = 0; i < cluster1.size(); i++)
/*  733:     */       {
/*  734: 767 */         Instance instance = this.m_instances.instance(((Integer)cluster1.elementAt(i)).intValue());
/*  735: 768 */         for (int j = 0; j < this.m_instances.numAttributes(); j++) {
/*  736: 769 */           fValues1[j] += instance.value(j);
/*  737:     */         }
/*  738:     */       }
/*  739: 772 */       double[] fValues2 = new double[this.m_instances.numAttributes()];
/*  740: 773 */       for (int i = 0; i < cluster2.size(); i++)
/*  741:     */       {
/*  742: 774 */         Instance instance = this.m_instances.instance(((Integer)cluster2.elementAt(i)).intValue());
/*  743: 775 */         for (int j = 0; j < this.m_instances.numAttributes(); j++) {
/*  744: 776 */           fValues2[j] += instance.value(j);
/*  745:     */         }
/*  746:     */       }
/*  747: 779 */       for (int j = 0; j < this.m_instances.numAttributes(); j++)
/*  748:     */       {
/*  749: 780 */         fValues1[j] /= cluster1.size();
/*  750: 781 */         fValues2[j] /= cluster2.size();
/*  751:     */       }
/*  752: 783 */       fBestDist = this.m_DistanceFunction.distance(this.m_instances.instance(0).copy(fValues1), this.m_instances.instance(0).copy(fValues2));
/*  753:     */       
/*  754: 785 */       break;
/*  755:     */     case 5: 
/*  756: 791 */       double ESS1 = calcESS(cluster1);
/*  757: 792 */       double ESS2 = calcESS(cluster2);
/*  758: 793 */       Vector<Integer> merged = new Vector();
/*  759: 794 */       merged.addAll(cluster1);
/*  760: 795 */       merged.addAll(cluster2);
/*  761: 796 */       double ESS = calcESS(merged);
/*  762: 797 */       fBestDist = ESS * merged.size() - ESS1 * cluster1.size() - ESS2 * cluster2.size();
/*  763:     */     }
/*  764: 802 */     return fBestDist;
/*  765:     */   }
/*  766:     */   
/*  767:     */   double calcESS(Vector<Integer> cluster)
/*  768:     */   {
/*  769: 807 */     double[] fValues1 = new double[this.m_instances.numAttributes()];
/*  770: 808 */     for (int i = 0; i < cluster.size(); i++)
/*  771:     */     {
/*  772: 809 */       Instance instance = this.m_instances.instance(((Integer)cluster.elementAt(i)).intValue());
/*  773: 810 */       for (int j = 0; j < this.m_instances.numAttributes(); j++) {
/*  774: 811 */         fValues1[j] += instance.value(j);
/*  775:     */       }
/*  776:     */     }
/*  777: 814 */     for (int j = 0; j < this.m_instances.numAttributes(); j++) {
/*  778: 815 */       fValues1[j] /= cluster.size();
/*  779:     */     }
/*  780: 818 */     Instance centroid = this.m_instances.instance(((Integer)cluster.elementAt(0)).intValue()).copy(fValues1);
/*  781: 819 */     double fESS = 0.0D;
/*  782: 820 */     for (int i = 0; i < cluster.size(); i++)
/*  783:     */     {
/*  784: 821 */       Instance instance = this.m_instances.instance(((Integer)cluster.elementAt(i)).intValue());
/*  785: 822 */       fESS += this.m_DistanceFunction.distance(centroid, instance);
/*  786:     */     }
/*  787: 824 */     return fESS / cluster.size();
/*  788:     */   }
/*  789:     */   
/*  790:     */   public int clusterInstance(Instance instance)
/*  791:     */     throws Exception
/*  792:     */   {
/*  793: 833 */     if (this.m_instances.numInstances() == 0) {
/*  794: 834 */       return 0;
/*  795:     */     }
/*  796: 836 */     double fBestDist = 1.7976931348623157E+308D;
/*  797: 837 */     int iBestInstance = -1;
/*  798: 838 */     for (int i = 0; i < this.m_instances.numInstances(); i++)
/*  799:     */     {
/*  800: 839 */       double fDist = this.m_DistanceFunction.distance(instance, this.m_instances.instance(i));
/*  801: 841 */       if (fDist < fBestDist)
/*  802:     */       {
/*  803: 842 */         fBestDist = fDist;
/*  804: 843 */         iBestInstance = i;
/*  805:     */       }
/*  806:     */     }
/*  807: 846 */     return this.m_nClusterNr[iBestInstance];
/*  808:     */   }
/*  809:     */   
/*  810:     */   public double[] distributionForInstance(Instance instance)
/*  811:     */     throws Exception
/*  812:     */   {
/*  813: 854 */     if (numberOfClusters() == 0)
/*  814:     */     {
/*  815: 855 */       double[] p = new double[1];
/*  816: 856 */       p[0] = 1.0D;
/*  817: 857 */       return p;
/*  818:     */     }
/*  819: 859 */     double[] p = new double[numberOfClusters()];
/*  820: 860 */     p[clusterInstance(instance)] = 1.0D;
/*  821: 861 */     return p;
/*  822:     */   }
/*  823:     */   
/*  824:     */   public Capabilities getCapabilities()
/*  825:     */   {
/*  826: 866 */     Capabilities result = new Capabilities(this);
/*  827: 867 */     result.disableAll();
/*  828: 868 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  829:     */     
/*  830:     */ 
/*  831: 871 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  832: 872 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  833: 873 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  834: 874 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  835: 875 */     result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
/*  836:     */     
/*  837:     */ 
/*  838: 878 */     result.setMinimumNumberInstances(0);
/*  839: 879 */     return result;
/*  840:     */   }
/*  841:     */   
/*  842:     */   public int numberOfClusters()
/*  843:     */     throws Exception
/*  844:     */   {
/*  845: 884 */     return Math.min(this.m_nNumClusters, this.m_instances.numInstances());
/*  846:     */   }
/*  847:     */   
/*  848:     */   public Enumeration<Option> listOptions()
/*  849:     */   {
/*  850: 895 */     Vector<Option> newVector = new Vector(5);
/*  851:     */     
/*  852: 897 */     newVector.addElement(new Option("\tIf set, distance is interpreted as branch length\n\totherwise it is node height.", "B", 0, "-B"));
/*  853:     */     
/*  854:     */ 
/*  855:     */ 
/*  856: 901 */     newVector.addElement(new Option("\tnumber of clusters", "N", 1, "-N <Nr Of Clusters>"));
/*  857:     */     
/*  858: 903 */     newVector.addElement(new Option("\tFlag to indicate the cluster should be printed in Newick format.", "P", 0, "-P"));
/*  859:     */     
/*  860:     */ 
/*  861: 906 */     newVector.addElement(new Option("Link type (Single, Complete, Average, Mean, Centroid, Ward, Adjusted complete, Neighbor joining)", "L", 1, "-L [SINGLE|COMPLETE|AVERAGE|MEAN|CENTROID|WARD|ADJCOMPLETE|NEIGHBOR_JOINING]"));
/*  862:     */     
/*  863:     */ 
/*  864:     */ 
/*  865:     */ 
/*  866: 911 */     newVector.add(new Option("\tDistance function to use.\n\t(default: weka.core.EuclideanDistance)", "A", 1, "-A <classname and options>"));
/*  867:     */     
/*  868:     */ 
/*  869:     */ 
/*  870: 915 */     newVector.addAll(Collections.list(super.listOptions()));
/*  871:     */     
/*  872: 917 */     return newVector.elements();
/*  873:     */   }
/*  874:     */   
/*  875:     */   public void setOptions(String[] options)
/*  876:     */     throws Exception
/*  877:     */   {
/*  878: 934 */     this.m_bPrintNewick = Utils.getFlag('P', options);
/*  879:     */     
/*  880: 936 */     String optionString = Utils.getOption('N', options);
/*  881: 937 */     if (optionString.length() != 0)
/*  882:     */     {
/*  883: 938 */       Integer temp = new Integer(optionString);
/*  884: 939 */       setNumClusters(temp.intValue());
/*  885:     */     }
/*  886:     */     else
/*  887:     */     {
/*  888: 941 */       setNumClusters(2);
/*  889:     */     }
/*  890: 944 */     setDistanceIsBranchLength(Utils.getFlag('B', options));
/*  891:     */     
/*  892: 946 */     String sLinkType = Utils.getOption('L', options);
/*  893: 948 */     if (sLinkType.compareTo("SINGLE") == 0) {
/*  894: 949 */       setLinkType(new SelectedTag(0, TAGS_LINK_TYPE));
/*  895:     */     }
/*  896: 951 */     if (sLinkType.compareTo("COMPLETE") == 0) {
/*  897: 952 */       setLinkType(new SelectedTag(1, TAGS_LINK_TYPE));
/*  898:     */     }
/*  899: 954 */     if (sLinkType.compareTo("AVERAGE") == 0) {
/*  900: 955 */       setLinkType(new SelectedTag(2, TAGS_LINK_TYPE));
/*  901:     */     }
/*  902: 957 */     if (sLinkType.compareTo("MEAN") == 0) {
/*  903: 958 */       setLinkType(new SelectedTag(3, TAGS_LINK_TYPE));
/*  904:     */     }
/*  905: 960 */     if (sLinkType.compareTo("CENTROID") == 0) {
/*  906: 961 */       setLinkType(new SelectedTag(4, TAGS_LINK_TYPE));
/*  907:     */     }
/*  908: 963 */     if (sLinkType.compareTo("WARD") == 0) {
/*  909: 964 */       setLinkType(new SelectedTag(5, TAGS_LINK_TYPE));
/*  910:     */     }
/*  911: 966 */     if (sLinkType.compareTo("ADJCOMPLETE") == 0) {
/*  912: 967 */       setLinkType(new SelectedTag(6, TAGS_LINK_TYPE));
/*  913:     */     }
/*  914: 969 */     if (sLinkType.compareTo("NEIGHBOR_JOINING") == 0) {
/*  915: 970 */       setLinkType(new SelectedTag(7, TAGS_LINK_TYPE));
/*  916:     */     }
/*  917: 973 */     String nnSearchClass = Utils.getOption('A', options);
/*  918: 974 */     if (nnSearchClass.length() != 0)
/*  919:     */     {
/*  920: 975 */       String[] nnSearchClassSpec = Utils.splitOptions(nnSearchClass);
/*  921: 976 */       if (nnSearchClassSpec.length == 0) {
/*  922: 977 */         throw new Exception("Invalid DistanceFunction specification string.");
/*  923:     */       }
/*  924: 979 */       String className = nnSearchClassSpec[0];
/*  925: 980 */       nnSearchClassSpec[0] = "";
/*  926:     */       
/*  927: 982 */       setDistanceFunction((DistanceFunction)Utils.forName(DistanceFunction.class, className, nnSearchClassSpec));
/*  928:     */     }
/*  929:     */     else
/*  930:     */     {
/*  931: 985 */       setDistanceFunction(new EuclideanDistance());
/*  932:     */     }
/*  933: 988 */     super.setOptions(options);
/*  934:     */     
/*  935: 990 */     Utils.checkForRemainingOptions(options);
/*  936:     */   }
/*  937:     */   
/*  938:     */   public String[] getOptions()
/*  939:     */   {
/*  940:1001 */     Vector<String> options = new Vector();
/*  941:     */     
/*  942:1003 */     options.add("-N");
/*  943:1004 */     options.add("" + getNumClusters());
/*  944:     */     
/*  945:1006 */     options.add("-L");
/*  946:1007 */     switch (this.m_nLinkType)
/*  947:     */     {
/*  948:     */     case 0: 
/*  949:1009 */       options.add("SINGLE");
/*  950:1010 */       break;
/*  951:     */     case 1: 
/*  952:1012 */       options.add("COMPLETE");
/*  953:1013 */       break;
/*  954:     */     case 2: 
/*  955:1015 */       options.add("AVERAGE");
/*  956:1016 */       break;
/*  957:     */     case 3: 
/*  958:1018 */       options.add("MEAN");
/*  959:1019 */       break;
/*  960:     */     case 4: 
/*  961:1021 */       options.add("CENTROID");
/*  962:1022 */       break;
/*  963:     */     case 5: 
/*  964:1024 */       options.add("WARD");
/*  965:1025 */       break;
/*  966:     */     case 6: 
/*  967:1027 */       options.add("ADJCOMPLETE");
/*  968:1028 */       break;
/*  969:     */     case 7: 
/*  970:1030 */       options.add("NEIGHBOR_JOINING");
/*  971:     */     }
/*  972:1033 */     if (this.m_bPrintNewick) {
/*  973:1034 */       options.add("-P");
/*  974:     */     }
/*  975:1036 */     if (getDistanceIsBranchLength()) {
/*  976:1037 */       options.add("-B");
/*  977:     */     }
/*  978:1040 */     options.add("-A");
/*  979:1041 */     options.add((this.m_DistanceFunction.getClass().getName() + " " + Utils.joinOptions(this.m_DistanceFunction.getOptions())).trim());
/*  980:     */     
/*  981:     */ 
/*  982:1044 */     Collections.addAll(options, super.getOptions());
/*  983:     */     
/*  984:1046 */     return (String[])options.toArray(new String[0]);
/*  985:     */   }
/*  986:     */   
/*  987:     */   public String toString()
/*  988:     */   {
/*  989:1051 */     StringBuffer buf = new StringBuffer();
/*  990:1052 */     int attIndex = this.m_instances.classIndex();
/*  991:1053 */     if (attIndex < 0)
/*  992:     */     {
/*  993:1055 */       attIndex = 0;
/*  994:1056 */       while ((attIndex < this.m_instances.numAttributes() - 1) && 
/*  995:1057 */         (!this.m_instances.attribute(attIndex).isString())) {
/*  996:1060 */         attIndex++;
/*  997:     */       }
/*  998:     */     }
/*  999:     */     try
/* 1000:     */     {
/* 1001:1064 */       if ((this.m_bPrintNewick) && (numberOfClusters() > 0)) {
/* 1002:1065 */         for (int i = 0; i < this.m_clusters.length; i++) {
/* 1003:1066 */           if (this.m_clusters[i] != null)
/* 1004:     */           {
/* 1005:1067 */             buf.append("Cluster " + i + "\n");
/* 1006:1068 */             if (this.m_instances.attribute(attIndex).isString()) {
/* 1007:1069 */               buf.append(this.m_clusters[i].toString(attIndex));
/* 1008:     */             } else {
/* 1009:1071 */               buf.append(this.m_clusters[i].toString2(attIndex));
/* 1010:     */             }
/* 1011:1073 */             buf.append("\n\n");
/* 1012:     */           }
/* 1013:     */         }
/* 1014:     */       }
/* 1015:     */     }
/* 1016:     */     catch (Exception e)
/* 1017:     */     {
/* 1018:1078 */       e.printStackTrace();
/* 1019:     */     }
/* 1020:1080 */     return buf.toString();
/* 1021:     */   }
/* 1022:     */   
/* 1023:     */   public boolean getDistanceIsBranchLength()
/* 1024:     */   {
/* 1025:1084 */     return this.m_bDistanceIsBranchLength;
/* 1026:     */   }
/* 1027:     */   
/* 1028:     */   public void setDistanceIsBranchLength(boolean bDistanceIsHeight)
/* 1029:     */   {
/* 1030:1088 */     this.m_bDistanceIsBranchLength = bDistanceIsHeight;
/* 1031:     */   }
/* 1032:     */   
/* 1033:     */   public String distanceIsBranchLengthTipText()
/* 1034:     */   {
/* 1035:1092 */     return "If set to false, the distance between clusters is interpreted as the height of the node linking the clusters. This is appropriate for example for single link clustering. However, for neighbor joining, the distance is better interpreted as branch length. Set this flag to get the latter interpretation.";
/* 1036:     */   }
/* 1037:     */   
/* 1038:     */   public String numClustersTipText()
/* 1039:     */   {
/* 1040:1103 */     return "Sets the number of clusters. If a single hierarchy is desired, set this to 1.";
/* 1041:     */   }
/* 1042:     */   
/* 1043:     */   public String printNewickTipText()
/* 1044:     */   {
/* 1045:1111 */     return "Flag to indicate whether the cluster should be print in Newick format. This can be useful for display in other programs. However, for large datasets a lot of text may be produced, which may not be a nuisance when the Newick format is not required";
/* 1046:     */   }
/* 1047:     */   
/* 1048:     */   public String distanceFunctionTipText()
/* 1049:     */   {
/* 1050:1121 */     return "Sets the distance function, which measures the distance between two individual. instances (or possibly the distance between an instance and the centroid of a clusterdepending on the Link type).";
/* 1051:     */   }
/* 1052:     */   
/* 1053:     */   public String linkTypeTipText()
/* 1054:     */   {
/* 1055:1130 */     return "Sets the method used to measure the distance between two clusters.\nSINGLE:\n find single link distance aka minimum link, which is the closest distance between any item in cluster1 and any item in cluster2\nCOMPLETE:\n find complete link distance aka maximum link, which is the largest distance between any item in cluster1 and any item in cluster2\nADJCOMPLETE:\n as COMPLETE, but with adjustment, which is the largest within cluster distance\nAVERAGE:\n finds average distance between the elements of the two clusters\nMEAN: \n calculates the mean distance of a merged cluster (akak Group-average agglomerative clustering)\nCENTROID:\n finds the distance of the centroids of the clusters\nWARD:\n finds the distance of the change in caused by merging the cluster. The information of a cluster is calculated as the error sum of squares of the centroids of the cluster and its members.\nNEIGHBOR_JOINING\n use neighbor joining algorithm.";
/* 1056:     */   }
/* 1057:     */   
/* 1058:     */   public String globalInfo()
/* 1059:     */   {
/* 1060:1159 */     return "Hierarchical clustering class.\nImplements a number of classic agglomorative (i.e. bottom up) hierarchical clustering methodsbased on .";
/* 1061:     */   }
/* 1062:     */   
/* 1063:     */   public static void main(String[] argv)
/* 1064:     */   {
/* 1065:1165 */     runClusterer(new HierarchicalClusterer(), argv);
/* 1066:     */   }
/* 1067:     */   
/* 1068:     */   public String graph()
/* 1069:     */     throws Exception
/* 1070:     */   {
/* 1071:1170 */     if (numberOfClusters() == 0) {
/* 1072:1171 */       return "Newick:(no,clusters)";
/* 1073:     */     }
/* 1074:1173 */     int attIndex = this.m_instances.classIndex();
/* 1075:1174 */     if (attIndex < 0)
/* 1076:     */     {
/* 1077:1176 */       attIndex = 0;
/* 1078:1177 */       while ((attIndex < this.m_instances.numAttributes() - 1) && 
/* 1079:1178 */         (!this.m_instances.attribute(attIndex).isString())) {
/* 1080:1181 */         attIndex++;
/* 1081:     */       }
/* 1082:     */     }
/* 1083:1184 */     String sNewick = null;
/* 1084:1185 */     if (this.m_instances.attribute(attIndex).isString()) {
/* 1085:1186 */       sNewick = this.m_clusters[0].toString(attIndex);
/* 1086:     */     } else {
/* 1087:1188 */       sNewick = this.m_clusters[0].toString2(attIndex);
/* 1088:     */     }
/* 1089:1190 */     return "Newick:" + sNewick;
/* 1090:     */   }
/* 1091:     */   
/* 1092:     */   public int graphType()
/* 1093:     */   {
/* 1094:1195 */     return 3;
/* 1095:     */   }
/* 1096:     */   
/* 1097:     */   public String getRevision()
/* 1098:     */   {
/* 1099:1205 */     return RevisionUtils.extract("$Revision: 12475 $");
/* 1100:     */   }
/* 1101:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.clusterers.HierarchicalClusterer
 * JD-Core Version:    0.7.0.1
 */