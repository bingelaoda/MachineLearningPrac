/*    1:     */ package weka.classifiers.bayes.net;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.HashSet;
/*    6:     */ import java.util.Iterator;
/*    7:     */ import java.util.Set;
/*    8:     */ import java.util.Vector;
/*    9:     */ import weka.classifiers.bayes.BayesNet;
/*   10:     */ import weka.core.Attribute;
/*   11:     */ import weka.core.Instances;
/*   12:     */ import weka.core.RevisionHandler;
/*   13:     */ import weka.core.RevisionUtils;
/*   14:     */ import weka.estimators.Estimator;
/*   15:     */ 
/*   16:     */ public class MarginCalculator
/*   17:     */   implements Serializable, RevisionHandler
/*   18:     */ {
/*   19:     */   private static final long serialVersionUID = 650278019241175534L;
/*   20:     */   boolean m_debug;
/*   21:     */   public JunctionTreeNode m_root;
/*   22:     */   JunctionTreeNode[] jtNodes;
/*   23:     */   double[][] m_Margins;
/*   24:     */   
/*   25:     */   public MarginCalculator()
/*   26:     */   {
/*   27:  38 */     this.m_debug = false;
/*   28:  39 */     this.m_root = null;
/*   29:     */   }
/*   30:     */   
/*   31:     */   public int getNode(String sNodeName)
/*   32:     */   {
/*   33:  43 */     int iNode = 0;
/*   34:  44 */     while (iNode < this.m_root.m_bayesNet.m_Instances.numAttributes())
/*   35:     */     {
/*   36:  45 */       if (this.m_root.m_bayesNet.m_Instances.attribute(iNode).name().equals(sNodeName)) {
/*   37:  47 */         return iNode;
/*   38:     */       }
/*   39:  49 */       iNode++;
/*   40:     */     }
/*   41:  52 */     return -1;
/*   42:     */   }
/*   43:     */   
/*   44:     */   public String toXMLBIF03()
/*   45:     */   {
/*   46:  56 */     return this.m_root.m_bayesNet.toXMLBIF03();
/*   47:     */   }
/*   48:     */   
/*   49:     */   public void calcMargins(BayesNet bayesNet)
/*   50:     */     throws Exception
/*   51:     */   {
/*   52:  68 */     boolean[][] bAdjacencyMatrix = moralize(bayesNet);
/*   53:  69 */     process(bAdjacencyMatrix, bayesNet);
/*   54:     */   }
/*   55:     */   
/*   56:     */   public void calcFullMargins(BayesNet bayesNet)
/*   57:     */     throws Exception
/*   58:     */   {
/*   59:  74 */     int nNodes = bayesNet.getNrOfNodes();
/*   60:  75 */     boolean[][] bAdjacencyMatrix = new boolean[nNodes][nNodes];
/*   61:  76 */     for (int iNode = 0; iNode < nNodes; iNode++) {
/*   62:  77 */       for (int iNode2 = 0; iNode2 < nNodes; iNode2++) {
/*   63:  78 */         bAdjacencyMatrix[iNode][iNode2] = 1;
/*   64:     */       }
/*   65:     */     }
/*   66:  81 */     process(bAdjacencyMatrix, bayesNet);
/*   67:     */   }
/*   68:     */   
/*   69:     */   public void process(boolean[][] bAdjacencyMatrix, BayesNet bayesNet)
/*   70:     */     throws Exception
/*   71:     */   {
/*   72:  86 */     int[] order = getMaxCardOrder(bAdjacencyMatrix);
/*   73:  87 */     bAdjacencyMatrix = fillIn(order, bAdjacencyMatrix);
/*   74:  88 */     order = getMaxCardOrder(bAdjacencyMatrix);
/*   75:  89 */     Set<Integer>[] cliques = getCliques(order, bAdjacencyMatrix);
/*   76:  90 */     Set<Integer>[] separators = getSeparators(order, cliques);
/*   77:  91 */     int[] parentCliques = getCliqueTree(order, cliques, separators);
/*   78:     */     
/*   79:  93 */     int nNodes = bAdjacencyMatrix.length;
/*   80:  94 */     if (this.m_debug) {
/*   81:  95 */       for (int i = 0; i < nNodes; i++)
/*   82:     */       {
/*   83:  96 */         int iNode = order[i];
/*   84:  97 */         if (cliques[iNode] != null)
/*   85:     */         {
/*   86:  98 */           System.out.print("Clique " + iNode + " (");
/*   87:  99 */           Iterator<Integer> nodes = cliques[iNode].iterator();
/*   88: 100 */           while (nodes.hasNext())
/*   89:     */           {
/*   90: 101 */             int iNode2 = ((Integer)nodes.next()).intValue();
/*   91: 102 */             System.out.print(iNode2 + " " + bayesNet.getNodeName(iNode2));
/*   92: 103 */             if (nodes.hasNext()) {
/*   93: 104 */               System.out.print(",");
/*   94:     */             }
/*   95:     */           }
/*   96: 107 */           System.out.print(") S(");
/*   97: 108 */           nodes = separators[iNode].iterator();
/*   98: 109 */           while (nodes.hasNext())
/*   99:     */           {
/*  100: 110 */             int iNode2 = ((Integer)nodes.next()).intValue();
/*  101: 111 */             System.out.print(iNode2 + " " + bayesNet.getNodeName(iNode2));
/*  102: 112 */             if (nodes.hasNext()) {
/*  103: 113 */               System.out.print(",");
/*  104:     */             }
/*  105:     */           }
/*  106: 116 */           System.out.println(") parent clique " + parentCliques[iNode]);
/*  107:     */         }
/*  108:     */       }
/*  109:     */     }
/*  110: 121 */     this.jtNodes = getJunctionTree(cliques, separators, parentCliques, order, bayesNet);
/*  111:     */     
/*  112: 123 */     this.m_root = null;
/*  113: 124 */     for (int iNode = 0; iNode < nNodes; iNode++) {
/*  114: 125 */       if ((parentCliques[iNode] < 0) && (this.jtNodes[iNode] != null))
/*  115:     */       {
/*  116: 126 */         this.m_root = this.jtNodes[iNode];
/*  117: 127 */         break;
/*  118:     */       }
/*  119:     */     }
/*  120: 130 */     this.m_Margins = new double[nNodes][];
/*  121: 131 */     initialize(this.jtNodes, order, cliques, separators, parentCliques);
/*  122: 134 */     for (int i = 0; i < nNodes; i++)
/*  123:     */     {
/*  124: 135 */       int iNode = order[i];
/*  125: 136 */       if ((cliques[iNode] != null) && 
/*  126: 137 */         (parentCliques[iNode] == -1) && (separators[iNode].size() > 0)) {
/*  127: 138 */         throw new Exception("Something wrong in clique tree");
/*  128:     */       }
/*  129:     */     }
/*  130: 142 */     if (this.m_debug) {}
/*  131:     */   }
/*  132:     */   
/*  133:     */   void initialize(JunctionTreeNode[] jtNodes, int[] order, Set<Integer>[] cliques, Set<Integer>[] separators, int[] parentCliques)
/*  134:     */   {
/*  135: 149 */     int nNodes = order.length;
/*  136: 150 */     for (int i = nNodes - 1; i >= 0; i--)
/*  137:     */     {
/*  138: 151 */       int iNode = order[i];
/*  139: 152 */       if (jtNodes[iNode] != null) {
/*  140: 153 */         jtNodes[iNode].initializeUp();
/*  141:     */       }
/*  142:     */     }
/*  143: 156 */     for (int i = 0; i < nNodes; i++)
/*  144:     */     {
/*  145: 157 */       int iNode = order[i];
/*  146: 158 */       if (jtNodes[iNode] != null) {
/*  147: 159 */         jtNodes[iNode].initializeDown(false);
/*  148:     */       }
/*  149:     */     }
/*  150:     */   }
/*  151:     */   
/*  152:     */   JunctionTreeNode[] getJunctionTree(Set<Integer>[] cliques, Set<Integer>[] separators, int[] parentCliques, int[] order, BayesNet bayesNet)
/*  153:     */   {
/*  154: 167 */     int nNodes = order.length;
/*  155: 168 */     JunctionTreeNode[] jtns = new JunctionTreeNode[nNodes];
/*  156: 169 */     boolean[] bDone = new boolean[nNodes];
/*  157: 171 */     for (int i = 0; i < nNodes; i++)
/*  158:     */     {
/*  159: 172 */       int iNode = order[i];
/*  160: 173 */       if (cliques[iNode] != null) {
/*  161: 174 */         jtns[iNode] = new JunctionTreeNode(cliques[iNode], bayesNet, bDone);
/*  162:     */       }
/*  163:     */     }
/*  164: 178 */     for (int i = 0; i < nNodes; i++)
/*  165:     */     {
/*  166: 179 */       int iNode = order[i];
/*  167: 180 */       if (cliques[iNode] != null)
/*  168:     */       {
/*  169: 181 */         JunctionTreeNode parent = null;
/*  170: 182 */         if (parentCliques[iNode] > 0)
/*  171:     */         {
/*  172: 183 */           parent = jtns[parentCliques[iNode]];
/*  173: 184 */           JunctionTreeSeparator jts = new JunctionTreeSeparator(separators[iNode], bayesNet, jtns[iNode], parent);
/*  174:     */           
/*  175: 186 */           jtns[iNode].setParentSeparator(jts);
/*  176: 187 */           jtns[parentCliques[iNode]].addChildClique(jtns[iNode]);
/*  177:     */         }
/*  178:     */       }
/*  179:     */     }
/*  180: 192 */     return jtns;
/*  181:     */   }
/*  182:     */   
/*  183:     */   public class JunctionTreeSeparator
/*  184:     */     implements Serializable, RevisionHandler
/*  185:     */   {
/*  186:     */     private static final long serialVersionUID = 6502780192411755343L;
/*  187:     */     int[] m_nNodes;
/*  188:     */     int m_nCardinality;
/*  189:     */     double[] m_fiParent;
/*  190:     */     double[] m_fiChild;
/*  191:     */     MarginCalculator.JunctionTreeNode m_parentNode;
/*  192:     */     MarginCalculator.JunctionTreeNode m_childNode;
/*  193:     */     BayesNet m_bayesNet;
/*  194:     */     
/*  195:     */     JunctionTreeSeparator(BayesNet separator, MarginCalculator.JunctionTreeNode bayesNet, MarginCalculator.JunctionTreeNode childNode)
/*  196:     */     {
/*  197: 210 */       this.m_nNodes = new int[separator.size()];
/*  198: 211 */       int iPos = 0;
/*  199: 212 */       this.m_nCardinality = 1;
/*  200: 213 */       for (Integer element : separator)
/*  201:     */       {
/*  202: 214 */         int iNode = element.intValue();
/*  203: 215 */         this.m_nNodes[(iPos++)] = iNode;
/*  204: 216 */         this.m_nCardinality *= bayesNet.getCardinality(iNode);
/*  205:     */       }
/*  206: 218 */       this.m_parentNode = parentNode;
/*  207: 219 */       this.m_childNode = childNode;
/*  208: 220 */       this.m_bayesNet = bayesNet;
/*  209:     */     }
/*  210:     */     
/*  211:     */     public void updateFromParent()
/*  212:     */     {
/*  213: 229 */       double[] fis = update(this.m_parentNode);
/*  214: 230 */       if (fis == null)
/*  215:     */       {
/*  216: 231 */         this.m_fiParent = null;
/*  217:     */       }
/*  218:     */       else
/*  219:     */       {
/*  220: 233 */         this.m_fiParent = fis;
/*  221:     */         
/*  222: 235 */         double sum = 0.0D;
/*  223: 236 */         for (int iPos = 0; iPos < this.m_nCardinality; iPos++) {
/*  224: 237 */           sum += this.m_fiParent[iPos];
/*  225:     */         }
/*  226: 239 */         for (int iPos = 0; iPos < this.m_nCardinality; iPos++) {
/*  227: 240 */           this.m_fiParent[iPos] /= sum;
/*  228:     */         }
/*  229:     */       }
/*  230:     */     }
/*  231:     */     
/*  232:     */     public void updateFromChild()
/*  233:     */     {
/*  234: 251 */       double[] fis = update(this.m_childNode);
/*  235: 252 */       if (fis == null)
/*  236:     */       {
/*  237: 253 */         this.m_fiChild = null;
/*  238:     */       }
/*  239:     */       else
/*  240:     */       {
/*  241: 255 */         this.m_fiChild = fis;
/*  242:     */         
/*  243: 257 */         double sum = 0.0D;
/*  244: 258 */         for (int iPos = 0; iPos < this.m_nCardinality; iPos++) {
/*  245: 259 */           sum += this.m_fiChild[iPos];
/*  246:     */         }
/*  247: 261 */         for (int iPos = 0; iPos < this.m_nCardinality; iPos++) {
/*  248: 262 */           this.m_fiChild[iPos] /= sum;
/*  249:     */         }
/*  250:     */       }
/*  251:     */     }
/*  252:     */     
/*  253:     */     public double[] update(MarginCalculator.JunctionTreeNode node)
/*  254:     */     {
/*  255: 274 */       if (node.m_P == null) {
/*  256: 275 */         return null;
/*  257:     */       }
/*  258: 277 */       double[] fi = new double[this.m_nCardinality];
/*  259:     */       
/*  260: 279 */       int[] values = new int[node.m_nNodes.length];
/*  261: 280 */       int[] order = new int[this.m_bayesNet.getNrOfNodes()];
/*  262: 281 */       for (int iNode = 0; iNode < node.m_nNodes.length; iNode++) {
/*  263: 282 */         order[node.m_nNodes[iNode]] = iNode;
/*  264:     */       }
/*  265: 285 */       for (int iPos = 0; iPos < node.m_nCardinality; iPos++)
/*  266:     */       {
/*  267: 286 */         int iNodeCPT = MarginCalculator.this.getCPT(node.m_nNodes, node.m_nNodes.length, values, order, this.m_bayesNet);
/*  268:     */         
/*  269: 288 */         int iSepCPT = MarginCalculator.this.getCPT(this.m_nNodes, this.m_nNodes.length, values, order, this.m_bayesNet);
/*  270:     */         
/*  271: 290 */         fi[iSepCPT] += node.m_P[iNodeCPT];
/*  272:     */         
/*  273: 292 */         int i = 0;
/*  274: 293 */         values[i] += 1;
/*  275: 295 */         while ((i < node.m_nNodes.length) && (values[i] == this.m_bayesNet.getCardinality(node.m_nNodes[i])))
/*  276:     */         {
/*  277: 296 */           values[i] = 0;
/*  278: 297 */           i++;
/*  279: 298 */           if (i < node.m_nNodes.length) {
/*  280: 299 */             values[i] += 1;
/*  281:     */           }
/*  282:     */         }
/*  283:     */       }
/*  284: 303 */       return fi;
/*  285:     */     }
/*  286:     */     
/*  287:     */     public String getRevision()
/*  288:     */     {
/*  289: 313 */       return RevisionUtils.extract("$Revision: 10154 $");
/*  290:     */     }
/*  291:     */   }
/*  292:     */   
/*  293:     */   public class JunctionTreeNode
/*  294:     */     implements Serializable, RevisionHandler
/*  295:     */   {
/*  296:     */     private static final long serialVersionUID = 650278019241175536L;
/*  297:     */     BayesNet m_bayesNet;
/*  298:     */     public int[] m_nNodes;
/*  299:     */     int m_nCardinality;
/*  300:     */     double[] m_fi;
/*  301:     */     double[] m_P;
/*  302:     */     double[][] m_MarginalP;
/*  303:     */     MarginCalculator.JunctionTreeSeparator m_parentSeparator;
/*  304:     */     public Vector<JunctionTreeNode> m_children;
/*  305:     */     
/*  306:     */     public void setParentSeparator(MarginCalculator.JunctionTreeSeparator parentSeparator)
/*  307:     */     {
/*  308: 341 */       this.m_parentSeparator = parentSeparator;
/*  309:     */     }
/*  310:     */     
/*  311:     */     public void addChildClique(JunctionTreeNode child)
/*  312:     */     {
/*  313: 347 */       this.m_children.add(child);
/*  314:     */     }
/*  315:     */     
/*  316:     */     public void initializeUp()
/*  317:     */     {
/*  318: 351 */       this.m_P = new double[this.m_nCardinality];
/*  319: 352 */       for (int iPos = 0; iPos < this.m_nCardinality; iPos++) {
/*  320: 353 */         this.m_P[iPos] = this.m_fi[iPos];
/*  321:     */       }
/*  322: 355 */       int[] values = new int[this.m_nNodes.length];
/*  323: 356 */       int[] order = new int[this.m_bayesNet.getNrOfNodes()];
/*  324: 357 */       for (int iNode = 0; iNode < this.m_nNodes.length; iNode++) {
/*  325: 358 */         order[this.m_nNodes[iNode]] = iNode;
/*  326:     */       }
/*  327: 360 */       for (JunctionTreeNode element : this.m_children)
/*  328:     */       {
/*  329: 361 */         JunctionTreeNode childNode = element;
/*  330: 362 */         MarginCalculator.JunctionTreeSeparator separator = childNode.m_parentSeparator;
/*  331: 364 */         for (int iPos = 0; iPos < this.m_nCardinality; iPos++)
/*  332:     */         {
/*  333: 365 */           int iSepCPT = MarginCalculator.this.getCPT(separator.m_nNodes, separator.m_nNodes.length, values, order, this.m_bayesNet);
/*  334:     */           
/*  335: 367 */           int iNodeCPT = MarginCalculator.this.getCPT(this.m_nNodes, this.m_nNodes.length, values, order, this.m_bayesNet);
/*  336:     */           
/*  337: 369 */           this.m_P[iNodeCPT] *= separator.m_fiChild[iSepCPT];
/*  338:     */           
/*  339: 371 */           int i = 0;
/*  340: 372 */           values[i] += 1;
/*  341: 374 */           while ((i < this.m_nNodes.length) && (values[i] == this.m_bayesNet.getCardinality(this.m_nNodes[i])))
/*  342:     */           {
/*  343: 375 */             values[i] = 0;
/*  344: 376 */             i++;
/*  345: 377 */             if (i < this.m_nNodes.length) {
/*  346: 378 */               values[i] += 1;
/*  347:     */             }
/*  348:     */           }
/*  349:     */         }
/*  350:     */       }
/*  351: 384 */       double sum = 0.0D;
/*  352: 385 */       for (int iPos = 0; iPos < this.m_nCardinality; iPos++) {
/*  353: 386 */         sum += this.m_P[iPos];
/*  354:     */       }
/*  355: 388 */       for (int iPos = 0; iPos < this.m_nCardinality; iPos++) {
/*  356: 389 */         this.m_P[iPos] /= sum;
/*  357:     */       }
/*  358: 392 */       if (this.m_parentSeparator != null) {
/*  359: 393 */         this.m_parentSeparator.updateFromChild();
/*  360:     */       }
/*  361:     */     }
/*  362:     */     
/*  363:     */     public void initializeDown(boolean recursively)
/*  364:     */     {
/*  365: 398 */       if (this.m_parentSeparator == null)
/*  366:     */       {
/*  367: 399 */         calcMarginalProbabilities();
/*  368:     */       }
/*  369:     */       else
/*  370:     */       {
/*  371: 401 */         this.m_parentSeparator.updateFromParent();
/*  372: 402 */         int[] values = new int[this.m_nNodes.length];
/*  373: 403 */         int[] order = new int[this.m_bayesNet.getNrOfNodes()];
/*  374: 404 */         for (int iNode = 0; iNode < this.m_nNodes.length; iNode++) {
/*  375: 405 */           order[this.m_nNodes[iNode]] = iNode;
/*  376:     */         }
/*  377: 409 */         for (int iPos = 0; iPos < this.m_nCardinality; iPos++)
/*  378:     */         {
/*  379: 410 */           int iSepCPT = MarginCalculator.this.getCPT(this.m_parentSeparator.m_nNodes, this.m_parentSeparator.m_nNodes.length, values, order, this.m_bayesNet);
/*  380:     */           
/*  381: 412 */           int iNodeCPT = MarginCalculator.this.getCPT(this.m_nNodes, this.m_nNodes.length, values, order, this.m_bayesNet);
/*  382: 414 */           if (this.m_parentSeparator.m_fiChild[iSepCPT] > 0.0D) {
/*  383: 415 */             this.m_P[iNodeCPT] *= this.m_parentSeparator.m_fiParent[iSepCPT] / this.m_parentSeparator.m_fiChild[iSepCPT];
/*  384:     */           } else {
/*  385: 418 */             this.m_P[iNodeCPT] = 0.0D;
/*  386:     */           }
/*  387: 421 */           int i = 0;
/*  388: 422 */           values[i] += 1;
/*  389: 424 */           while ((i < this.m_nNodes.length) && (values[i] == this.m_bayesNet.getCardinality(this.m_nNodes[i])))
/*  390:     */           {
/*  391: 425 */             values[i] = 0;
/*  392: 426 */             i++;
/*  393: 427 */             if (i < this.m_nNodes.length) {
/*  394: 428 */               values[i] += 1;
/*  395:     */             }
/*  396:     */           }
/*  397:     */         }
/*  398: 433 */         double sum = 0.0D;
/*  399: 434 */         for (int iPos = 0; iPos < this.m_nCardinality; iPos++) {
/*  400: 435 */           sum += this.m_P[iPos];
/*  401:     */         }
/*  402: 437 */         for (int iPos = 0; iPos < this.m_nCardinality; iPos++) {
/*  403: 438 */           this.m_P[iPos] /= sum;
/*  404:     */         }
/*  405: 440 */         this.m_parentSeparator.updateFromChild();
/*  406: 441 */         calcMarginalProbabilities();
/*  407:     */       }
/*  408: 443 */       if (recursively) {
/*  409: 444 */         for (Object element : this.m_children)
/*  410:     */         {
/*  411: 445 */           JunctionTreeNode childNode = (JunctionTreeNode)element;
/*  412: 446 */           childNode.initializeDown(true);
/*  413:     */         }
/*  414:     */       }
/*  415:     */     }
/*  416:     */     
/*  417:     */     void calcMarginalProbabilities()
/*  418:     */     {
/*  419: 457 */       int[] values = new int[this.m_nNodes.length];
/*  420: 458 */       int[] order = new int[this.m_bayesNet.getNrOfNodes()];
/*  421: 459 */       this.m_MarginalP = new double[this.m_nNodes.length][];
/*  422: 460 */       for (int iNode = 0; iNode < this.m_nNodes.length; iNode++)
/*  423:     */       {
/*  424: 461 */         order[this.m_nNodes[iNode]] = iNode;
/*  425: 462 */         this.m_MarginalP[iNode] = new double[this.m_bayesNet.getCardinality(this.m_nNodes[iNode])];
/*  426:     */       }
/*  427: 465 */       for (int iPos = 0; iPos < this.m_nCardinality; iPos++)
/*  428:     */       {
/*  429: 466 */         int iNodeCPT = MarginCalculator.this.getCPT(this.m_nNodes, this.m_nNodes.length, values, order, this.m_bayesNet);
/*  430: 468 */         for (int iNode = 0; iNode < this.m_nNodes.length; iNode++) {
/*  431: 469 */           this.m_MarginalP[iNode][values[iNode]] += this.m_P[iNodeCPT];
/*  432:     */         }
/*  433: 472 */         int i = 0;
/*  434: 473 */         values[i] += 1;
/*  435: 475 */         while ((i < this.m_nNodes.length) && (values[i] == this.m_bayesNet.getCardinality(this.m_nNodes[i])))
/*  436:     */         {
/*  437: 476 */           values[i] = 0;
/*  438: 477 */           i++;
/*  439: 478 */           if (i < this.m_nNodes.length) {
/*  440: 479 */             values[i] += 1;
/*  441:     */           }
/*  442:     */         }
/*  443:     */       }
/*  444: 484 */       for (int iNode = 0; iNode < this.m_nNodes.length; iNode++) {
/*  445: 485 */         MarginCalculator.this.m_Margins[this.m_nNodes[iNode]] = this.m_MarginalP[iNode];
/*  446:     */       }
/*  447:     */     }
/*  448:     */     
/*  449:     */     public String toString()
/*  450:     */     {
/*  451: 491 */       StringBuffer buf = new StringBuffer();
/*  452: 492 */       for (int iNode = 0; iNode < this.m_nNodes.length; iNode++)
/*  453:     */       {
/*  454: 493 */         buf.append(this.m_bayesNet.getNodeName(this.m_nNodes[iNode]) + ": ");
/*  455: 494 */         for (int iValue = 0; iValue < this.m_MarginalP[iNode].length; iValue++) {
/*  456: 495 */           buf.append(this.m_MarginalP[iNode][iValue] + " ");
/*  457:     */         }
/*  458: 497 */         buf.append('\n');
/*  459:     */       }
/*  460: 499 */       for (Object element : this.m_children)
/*  461:     */       {
/*  462: 500 */         JunctionTreeNode childNode = (JunctionTreeNode)element;
/*  463: 501 */         buf.append("----------------\n");
/*  464: 502 */         buf.append(childNode.toString());
/*  465:     */       }
/*  466: 504 */       return buf.toString();
/*  467:     */     }
/*  468:     */     
/*  469:     */     void calculatePotentials(BayesNet bayesNet, Set<Integer> clique, boolean[] bDone)
/*  470:     */     {
/*  471: 509 */       this.m_fi = new double[this.m_nCardinality];
/*  472:     */       
/*  473: 511 */       int[] values = new int[this.m_nNodes.length];
/*  474: 512 */       int[] order = new int[bayesNet.getNrOfNodes()];
/*  475: 513 */       for (int iNode = 0; iNode < this.m_nNodes.length; iNode++) {
/*  476: 514 */         order[this.m_nNodes[iNode]] = iNode;
/*  477:     */       }
/*  478: 517 */       boolean[] bIsContained = new boolean[this.m_nNodes.length];
/*  479: 518 */       for (int iNode = 0; iNode < this.m_nNodes.length; iNode++)
/*  480:     */       {
/*  481: 519 */         int nNode = this.m_nNodes[iNode];
/*  482: 520 */         bIsContained[iNode] = (bDone[nNode] == 0 ? 1 : false);
/*  483: 521 */         for (int iParent = 0; iParent < bayesNet.getNrOfParents(nNode); iParent++)
/*  484:     */         {
/*  485: 522 */           int nParent = bayesNet.getParent(nNode, iParent);
/*  486: 523 */           if (!clique.contains(Integer.valueOf(nParent))) {
/*  487: 524 */             bIsContained[iNode] = false;
/*  488:     */           }
/*  489:     */         }
/*  490: 527 */         if (bIsContained[iNode] != 0)
/*  491:     */         {
/*  492: 528 */           bDone[nNode] = true;
/*  493: 529 */           if (MarginCalculator.this.m_debug) {
/*  494: 530 */             System.out.println("adding node " + nNode);
/*  495:     */           }
/*  496:     */         }
/*  497:     */       }
/*  498: 536 */       for (int iPos = 0; iPos < this.m_nCardinality; iPos++)
/*  499:     */       {
/*  500: 537 */         int iCPT = MarginCalculator.this.getCPT(this.m_nNodes, this.m_nNodes.length, values, order, bayesNet);
/*  501: 538 */         this.m_fi[iCPT] = 1.0D;
/*  502: 539 */         for (int iNode = 0; iNode < this.m_nNodes.length; iNode++) {
/*  503: 540 */           if (bIsContained[iNode] != 0)
/*  504:     */           {
/*  505: 541 */             int nNode = this.m_nNodes[iNode];
/*  506: 542 */             int[] nNodes = bayesNet.getParentSet(nNode).getParents();
/*  507: 543 */             int iCPT2 = MarginCalculator.this.getCPT(nNodes, bayesNet.getNrOfParents(nNode), values, order, bayesNet);
/*  508:     */             
/*  509: 545 */             double f = bayesNet.getDistributions()[nNode][iCPT2].getProbability(values[iNode]);
/*  510:     */             
/*  511: 547 */             this.m_fi[iCPT] *= f;
/*  512:     */           }
/*  513:     */         }
/*  514: 552 */         int i = 0;
/*  515: 553 */         values[i] += 1;
/*  516: 555 */         while ((i < this.m_nNodes.length) && (values[i] == bayesNet.getCardinality(this.m_nNodes[i])))
/*  517:     */         {
/*  518: 556 */           values[i] = 0;
/*  519: 557 */           i++;
/*  520: 558 */           if (i < this.m_nNodes.length) {
/*  521: 559 */             values[i] += 1;
/*  522:     */           }
/*  523:     */         }
/*  524:     */       }
/*  525:     */     }
/*  526:     */     
/*  527:     */     JunctionTreeNode(BayesNet clique, boolean[] bayesNet)
/*  528:     */     {
/*  529: 566 */       this.m_bayesNet = bayesNet;
/*  530: 567 */       this.m_children = new Vector();
/*  531:     */       
/*  532:     */ 
/*  533: 570 */       this.m_nNodes = new int[clique.size()];
/*  534: 571 */       int iPos = 0;
/*  535: 572 */       this.m_nCardinality = 1;
/*  536: 573 */       for (Integer integer : clique)
/*  537:     */       {
/*  538: 574 */         int iNode = integer.intValue();
/*  539: 575 */         this.m_nNodes[(iPos++)] = iNode;
/*  540: 576 */         this.m_nCardinality *= bayesNet.getCardinality(iNode);
/*  541:     */       }
/*  542: 580 */       calculatePotentials(bayesNet, clique, bDone);
/*  543:     */     }
/*  544:     */     
/*  545:     */     boolean contains(int nNode)
/*  546:     */     {
/*  547: 587 */       for (int m_nNode : this.m_nNodes) {
/*  548: 588 */         if (m_nNode == nNode) {
/*  549: 589 */           return true;
/*  550:     */         }
/*  551:     */       }
/*  552: 592 */       return false;
/*  553:     */     }
/*  554:     */     
/*  555:     */     public void setEvidence(int nNode, int iValue)
/*  556:     */       throws Exception
/*  557:     */     {
/*  558: 596 */       int[] values = new int[this.m_nNodes.length];
/*  559: 597 */       int[] order = new int[this.m_bayesNet.getNrOfNodes()];
/*  560:     */       
/*  561: 599 */       int nNodeIdx = -1;
/*  562: 600 */       for (int iNode = 0; iNode < this.m_nNodes.length; iNode++)
/*  563:     */       {
/*  564: 601 */         order[this.m_nNodes[iNode]] = iNode;
/*  565: 602 */         if (this.m_nNodes[iNode] == nNode) {
/*  566: 603 */           nNodeIdx = iNode;
/*  567:     */         }
/*  568:     */       }
/*  569: 606 */       if (nNodeIdx < 0) {
/*  570: 607 */         throw new Exception("setEvidence: Node " + nNode + " not found in this clique");
/*  571:     */       }
/*  572: 610 */       for (int iPos = 0; iPos < this.m_nCardinality; iPos++)
/*  573:     */       {
/*  574: 611 */         if (values[nNodeIdx] != iValue)
/*  575:     */         {
/*  576: 612 */           int iNodeCPT = MarginCalculator.this.getCPT(this.m_nNodes, this.m_nNodes.length, values, order, this.m_bayesNet);
/*  577:     */           
/*  578: 614 */           this.m_P[iNodeCPT] = 0.0D;
/*  579:     */         }
/*  580: 617 */         int i = 0;
/*  581: 618 */         values[i] += 1;
/*  582: 620 */         while ((i < this.m_nNodes.length) && (values[i] == this.m_bayesNet.getCardinality(this.m_nNodes[i])))
/*  583:     */         {
/*  584: 621 */           values[i] = 0;
/*  585: 622 */           i++;
/*  586: 623 */           if (i < this.m_nNodes.length) {
/*  587: 624 */             values[i] += 1;
/*  588:     */           }
/*  589:     */         }
/*  590:     */       }
/*  591: 629 */       double sum = 0.0D;
/*  592: 630 */       for (int iPos = 0; iPos < this.m_nCardinality; iPos++) {
/*  593: 631 */         sum += this.m_P[iPos];
/*  594:     */       }
/*  595: 633 */       for (int iPos = 0; iPos < this.m_nCardinality; iPos++) {
/*  596: 634 */         this.m_P[iPos] /= sum;
/*  597:     */       }
/*  598: 636 */       calcMarginalProbabilities();
/*  599: 637 */       updateEvidence(this);
/*  600:     */     }
/*  601:     */     
/*  602:     */     void updateEvidence(JunctionTreeNode source)
/*  603:     */     {
/*  604: 641 */       if (source != this)
/*  605:     */       {
/*  606: 642 */         int[] values = new int[this.m_nNodes.length];
/*  607: 643 */         int[] order = new int[this.m_bayesNet.getNrOfNodes()];
/*  608: 644 */         for (int iNode = 0; iNode < this.m_nNodes.length; iNode++) {
/*  609: 645 */           order[this.m_nNodes[iNode]] = iNode;
/*  610:     */         }
/*  611: 647 */         int[] nChildNodes = source.m_parentSeparator.m_nNodes;
/*  612: 648 */         int nNumChildNodes = nChildNodes.length;
/*  613: 649 */         for (int iPos = 0; iPos < this.m_nCardinality; iPos++)
/*  614:     */         {
/*  615: 650 */           int iNodeCPT = MarginCalculator.this.getCPT(this.m_nNodes, this.m_nNodes.length, values, order, this.m_bayesNet);
/*  616:     */           
/*  617: 652 */           int iChildCPT = MarginCalculator.this.getCPT(nChildNodes, nNumChildNodes, values, order, this.m_bayesNet);
/*  618: 654 */           if (source.m_parentSeparator.m_fiParent[iChildCPT] != 0.0D) {
/*  619: 655 */             this.m_P[iNodeCPT] *= source.m_parentSeparator.m_fiChild[iChildCPT] / source.m_parentSeparator.m_fiParent[iChildCPT];
/*  620:     */           } else {
/*  621: 658 */             this.m_P[iNodeCPT] = 0.0D;
/*  622:     */           }
/*  623: 661 */           int i = 0;
/*  624: 662 */           values[i] += 1;
/*  625: 664 */           while ((i < this.m_nNodes.length) && (values[i] == this.m_bayesNet.getCardinality(this.m_nNodes[i])))
/*  626:     */           {
/*  627: 665 */             values[i] = 0;
/*  628: 666 */             i++;
/*  629: 667 */             if (i < this.m_nNodes.length) {
/*  630: 668 */               values[i] += 1;
/*  631:     */             }
/*  632:     */           }
/*  633:     */         }
/*  634: 673 */         double sum = 0.0D;
/*  635: 674 */         for (int iPos = 0; iPos < this.m_nCardinality; iPos++) {
/*  636: 675 */           sum += this.m_P[iPos];
/*  637:     */         }
/*  638: 677 */         for (int iPos = 0; iPos < this.m_nCardinality; iPos++) {
/*  639: 678 */           this.m_P[iPos] /= sum;
/*  640:     */         }
/*  641: 680 */         calcMarginalProbabilities();
/*  642:     */       }
/*  643: 682 */       for (Object element : this.m_children)
/*  644:     */       {
/*  645: 683 */         JunctionTreeNode childNode = (JunctionTreeNode)element;
/*  646: 684 */         if (childNode != source) {
/*  647: 685 */           childNode.initializeDown(true);
/*  648:     */         }
/*  649:     */       }
/*  650: 688 */       if (this.m_parentSeparator != null)
/*  651:     */       {
/*  652: 689 */         this.m_parentSeparator.updateFromChild();
/*  653: 690 */         this.m_parentSeparator.m_parentNode.updateEvidence(this);
/*  654: 691 */         this.m_parentSeparator.updateFromParent();
/*  655:     */       }
/*  656:     */     }
/*  657:     */     
/*  658:     */     public String getRevision()
/*  659:     */     {
/*  660: 702 */       return RevisionUtils.extract("$Revision: 10154 $");
/*  661:     */     }
/*  662:     */   }
/*  663:     */   
/*  664:     */   int getCPT(int[] nodeSet, int nNodes, int[] values, int[] order, BayesNet bayesNet)
/*  665:     */   {
/*  666: 709 */     int iCPTnew = 0;
/*  667: 710 */     for (int iNode = 0; iNode < nNodes; iNode++)
/*  668:     */     {
/*  669: 711 */       int nNode = nodeSet[iNode];
/*  670: 712 */       iCPTnew *= bayesNet.getCardinality(nNode);
/*  671: 713 */       iCPTnew += values[order[nNode]];
/*  672:     */     }
/*  673: 715 */     return iCPTnew;
/*  674:     */   }
/*  675:     */   
/*  676:     */   int[] getCliqueTree(int[] order, Set<Integer>[] cliques, Set<Integer>[] separators)
/*  677:     */   {
/*  678: 720 */     int nNodes = order.length;
/*  679: 721 */     int[] parentCliques = new int[nNodes];
/*  680: 723 */     for (int i = 0; i < nNodes; i++)
/*  681:     */     {
/*  682: 724 */       int iNode = order[i];
/*  683: 725 */       parentCliques[iNode] = -1;
/*  684: 726 */       if ((cliques[iNode] != null) && (separators[iNode].size() > 0)) {
/*  685: 728 */         for (int j = 0; j < nNodes; j++)
/*  686:     */         {
/*  687: 729 */           int iNode2 = order[j];
/*  688: 730 */           if ((iNode != iNode2) && (cliques[iNode2] != null) && (cliques[iNode2].containsAll(separators[iNode])))
/*  689:     */           {
/*  690: 732 */             parentCliques[iNode] = iNode2;
/*  691: 733 */             j = i;
/*  692: 734 */             j = 0;
/*  693: 735 */             j = nNodes;
/*  694:     */           }
/*  695:     */         }
/*  696:     */       }
/*  697:     */     }
/*  698: 741 */     return parentCliques;
/*  699:     */   }
/*  700:     */   
/*  701:     */   Set<Integer>[] getSeparators(int[] order, Set<Integer>[] cliques)
/*  702:     */   {
/*  703: 752 */     int nNodes = order.length;
/*  704:     */     
/*  705: 754 */     Set<Integer>[] separators = new HashSet[nNodes];
/*  706: 755 */     Set<Integer> processedNodes = new HashSet();
/*  707: 757 */     for (int i = 0; i < nNodes; i++)
/*  708:     */     {
/*  709: 758 */       int iNode = order[i];
/*  710: 759 */       if (cliques[iNode] != null)
/*  711:     */       {
/*  712: 760 */         Set<Integer> separator = new HashSet();
/*  713: 761 */         separator.addAll(cliques[iNode]);
/*  714: 762 */         separator.retainAll(processedNodes);
/*  715: 763 */         separators[iNode] = separator;
/*  716: 764 */         processedNodes.addAll(cliques[iNode]);
/*  717:     */       }
/*  718:     */     }
/*  719: 767 */     return separators;
/*  720:     */   }
/*  721:     */   
/*  722:     */   Set<Integer>[] getCliques(int[] order, boolean[][] bAdjacencyMatrix)
/*  723:     */     throws Exception
/*  724:     */   {
/*  725: 779 */     int nNodes = bAdjacencyMatrix.length;
/*  726:     */     
/*  727: 781 */     Set<Integer>[] cliques = new HashSet[nNodes];
/*  728: 787 */     for (int i = nNodes - 1; i >= 0; i--)
/*  729:     */     {
/*  730: 788 */       int iNode = order[i];
/*  731: 789 */       if (iNode == 22) {}
/*  732: 791 */       Set<Integer> clique = new HashSet();
/*  733: 792 */       clique.add(Integer.valueOf(iNode));
/*  734: 793 */       for (int j = 0; j < i; j++)
/*  735:     */       {
/*  736: 794 */         int iNode2 = order[j];
/*  737: 795 */         if (bAdjacencyMatrix[iNode][iNode2] != 0) {
/*  738: 796 */           clique.add(Integer.valueOf(iNode2));
/*  739:     */         }
/*  740:     */       }
/*  741: 806 */       cliques[iNode] = clique;
/*  742:     */     }
/*  743: 808 */     for (int iNode = 0; iNode < nNodes; iNode++) {
/*  744: 809 */       for (int iNode2 = 0; iNode2 < nNodes; iNode2++) {
/*  745: 810 */         if ((iNode != iNode2) && (cliques[iNode] != null) && (cliques[iNode2] != null) && (cliques[iNode].containsAll(cliques[iNode2]))) {
/*  746: 813 */           cliques[iNode2] = null;
/*  747:     */         }
/*  748:     */       }
/*  749:     */     }
/*  750: 818 */     if (this.m_debug)
/*  751:     */     {
/*  752: 819 */       int[] nNodeSet = new int[nNodes];
/*  753: 820 */       for (int iNode = 0; iNode < nNodes; iNode++) {
/*  754: 821 */         if (cliques[iNode] != null)
/*  755:     */         {
/*  756: 822 */           Iterator<Integer> it = cliques[iNode].iterator();
/*  757: 823 */           int k = 0;
/*  758: 824 */           while (it.hasNext()) {
/*  759: 825 */             nNodeSet[(k++)] = ((Integer)it.next()).intValue();
/*  760:     */           }
/*  761: 827 */           for (int i = 0; i < cliques[iNode].size(); i++) {
/*  762: 828 */             for (int j = 0; j < cliques[iNode].size(); j++) {
/*  763: 829 */               if ((i != j) && (bAdjacencyMatrix[nNodeSet[i]][nNodeSet[j]] == 0)) {
/*  764: 830 */                 throw new Exception("Non clique" + i + " " + j);
/*  765:     */               }
/*  766:     */             }
/*  767:     */           }
/*  768:     */         }
/*  769:     */       }
/*  770:     */     }
/*  771: 837 */     return cliques;
/*  772:     */   }
/*  773:     */   
/*  774:     */   public boolean[][] moralize(BayesNet bayesNet)
/*  775:     */   {
/*  776: 849 */     int nNodes = bayesNet.getNrOfNodes();
/*  777: 850 */     boolean[][] bAdjacencyMatrix = new boolean[nNodes][nNodes];
/*  778: 851 */     for (int iNode = 0; iNode < nNodes; iNode++)
/*  779:     */     {
/*  780: 852 */       ParentSet parents = bayesNet.getParentSets()[iNode];
/*  781: 853 */       moralizeNode(parents, iNode, bAdjacencyMatrix);
/*  782:     */     }
/*  783: 855 */     return bAdjacencyMatrix;
/*  784:     */   }
/*  785:     */   
/*  786:     */   private void moralizeNode(ParentSet parents, int iNode, boolean[][] bAdjacencyMatrix)
/*  787:     */   {
/*  788: 860 */     for (int iParent = 0; iParent < parents.getNrOfParents(); iParent++)
/*  789:     */     {
/*  790: 861 */       int nParent = parents.getParent(iParent);
/*  791: 862 */       if ((this.m_debug) && (bAdjacencyMatrix[iNode][nParent] == 0)) {
/*  792: 863 */         System.out.println("Insert " + iNode + "--" + nParent);
/*  793:     */       }
/*  794: 865 */       bAdjacencyMatrix[iNode][nParent] = 1;
/*  795: 866 */       bAdjacencyMatrix[nParent][iNode] = 1;
/*  796: 867 */       for (int iParent2 = iParent + 1; iParent2 < parents.getNrOfParents(); iParent2++)
/*  797:     */       {
/*  798: 868 */         int nParent2 = parents.getParent(iParent2);
/*  799: 869 */         if ((this.m_debug) && (bAdjacencyMatrix[nParent2][nParent] == 0)) {
/*  800: 870 */           System.out.println("Mary " + nParent + "--" + nParent2);
/*  801:     */         }
/*  802: 872 */         bAdjacencyMatrix[nParent2][nParent] = 1;
/*  803: 873 */         bAdjacencyMatrix[nParent][nParent2] = 1;
/*  804:     */       }
/*  805:     */     }
/*  806:     */   }
/*  807:     */   
/*  808:     */   public boolean[][] fillIn(int[] order, boolean[][] bAdjacencyMatrix)
/*  809:     */   {
/*  810: 890 */     int nNodes = bAdjacencyMatrix.length;
/*  811: 891 */     int[] inverseOrder = new int[nNodes];
/*  812: 892 */     for (int iNode = 0; iNode < nNodes; iNode++) {
/*  813: 893 */       inverseOrder[order[iNode]] = iNode;
/*  814:     */     }
/*  815: 896 */     for (int i = nNodes - 1; i >= 0; i--)
/*  816:     */     {
/*  817: 897 */       int iNode = order[i];
/*  818: 899 */       for (int j = 0; j < i; j++)
/*  819:     */       {
/*  820: 900 */         int iNode2 = order[j];
/*  821: 901 */         if (bAdjacencyMatrix[iNode][iNode2] != 0) {
/*  822: 902 */           for (int k = j + 1; k < i; k++)
/*  823:     */           {
/*  824: 903 */             int iNode3 = order[k];
/*  825: 904 */             if (bAdjacencyMatrix[iNode][iNode3] != 0)
/*  826:     */             {
/*  827: 906 */               if ((this.m_debug) && ((bAdjacencyMatrix[iNode2][iNode3] == 0) || (bAdjacencyMatrix[iNode3][iNode2] == 0))) {
/*  828: 908 */                 System.out.println("Fill in " + iNode2 + "--" + iNode3);
/*  829:     */               }
/*  830: 910 */               bAdjacencyMatrix[iNode2][iNode3] = 1;
/*  831: 911 */               bAdjacencyMatrix[iNode3][iNode2] = 1;
/*  832:     */             }
/*  833:     */           }
/*  834:     */         }
/*  835:     */       }
/*  836:     */     }
/*  837: 917 */     return bAdjacencyMatrix;
/*  838:     */   }
/*  839:     */   
/*  840:     */   int[] getMaxCardOrder(boolean[][] bAdjacencyMatrix)
/*  841:     */   {
/*  842: 930 */     int nNodes = bAdjacencyMatrix.length;
/*  843: 931 */     int[] order = new int[nNodes];
/*  844: 932 */     if (nNodes == 0) {
/*  845: 933 */       return order;
/*  846:     */     }
/*  847: 935 */     boolean[] bDone = new boolean[nNodes];
/*  848:     */     
/*  849: 937 */     order[0] = 0;
/*  850: 938 */     bDone[0] = true;
/*  851: 940 */     for (int iNode = 1; iNode < nNodes; iNode++)
/*  852:     */     {
/*  853: 941 */       int nMaxCard = -1;
/*  854: 942 */       int iBestNode = -1;
/*  855: 944 */       for (int iNode2 = 0; iNode2 < nNodes; iNode2++) {
/*  856: 945 */         if (bDone[iNode2] == 0)
/*  857:     */         {
/*  858: 946 */           int nCard = 0;
/*  859: 948 */           for (int iNode3 = 0; iNode3 < nNodes; iNode3++) {
/*  860: 949 */             if ((bAdjacencyMatrix[iNode2][iNode3] != 0) && (bDone[iNode3] != 0)) {
/*  861: 950 */               nCard++;
/*  862:     */             }
/*  863:     */           }
/*  864: 953 */           if (nCard > nMaxCard)
/*  865:     */           {
/*  866: 954 */             nMaxCard = nCard;
/*  867: 955 */             iBestNode = iNode2;
/*  868:     */           }
/*  869:     */         }
/*  870:     */       }
/*  871: 959 */       order[iNode] = iBestNode;
/*  872: 960 */       bDone[iBestNode] = true;
/*  873:     */     }
/*  874: 962 */     return order;
/*  875:     */   }
/*  876:     */   
/*  877:     */   public void setEvidence(int nNode, int iValue)
/*  878:     */     throws Exception
/*  879:     */   {
/*  880: 966 */     if (this.m_root == null) {
/*  881: 967 */       throw new Exception("Junction tree not initialize yet");
/*  882:     */     }
/*  883: 969 */     int iJtNode = 0;
/*  884: 971 */     while ((iJtNode < this.jtNodes.length) && ((this.jtNodes[iJtNode] == null) || (!this.jtNodes[iJtNode].contains(nNode)))) {
/*  885: 972 */       iJtNode++;
/*  886:     */     }
/*  887: 974 */     if (this.jtNodes.length == iJtNode) {
/*  888: 975 */       throw new Exception("Could not find node " + nNode + " in junction tree");
/*  889:     */     }
/*  890: 977 */     this.jtNodes[iJtNode].setEvidence(nNode, iValue);
/*  891:     */   }
/*  892:     */   
/*  893:     */   public String toString()
/*  894:     */   {
/*  895: 982 */     return this.m_root.toString();
/*  896:     */   }
/*  897:     */   
/*  898:     */   public double[] getMargin(int iNode)
/*  899:     */   {
/*  900: 988 */     return this.m_Margins[iNode];
/*  901:     */   }
/*  902:     */   
/*  903:     */   public String getRevision()
/*  904:     */   {
/*  905: 998 */     return RevisionUtils.extract("$Revision: 10154 $");
/*  906:     */   }
/*  907:     */   
/*  908:     */   public static void main(String[] args)
/*  909:     */   {
/*  910:     */     try
/*  911:     */     {
/*  912:1003 */       BIFReader bayesNet = new BIFReader();
/*  913:1004 */       bayesNet.processFile(args[0]);
/*  914:     */       
/*  915:1006 */       MarginCalculator dc = new MarginCalculator();
/*  916:1007 */       dc.calcMargins(bayesNet);
/*  917:1008 */       int iNode = 2;
/*  918:1009 */       int iValue = 0;
/*  919:1010 */       int iNode2 = 4;
/*  920:1011 */       int iValue2 = 0;
/*  921:1012 */       dc.setEvidence(iNode, iValue);
/*  922:1013 */       dc.setEvidence(iNode2, iValue2);
/*  923:1014 */       System.out.print(dc.toString());
/*  924:     */       
/*  925:1016 */       dc.calcFullMargins(bayesNet);
/*  926:1017 */       dc.setEvidence(iNode, iValue);
/*  927:1018 */       dc.setEvidence(iNode2, iValue2);
/*  928:1019 */       System.out.println("==============");
/*  929:1020 */       System.out.print(dc.toString());
/*  930:     */     }
/*  931:     */     catch (Exception e)
/*  932:     */     {
/*  933:1023 */       e.printStackTrace();
/*  934:     */     }
/*  935:     */   }
/*  936:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.MarginCalculator
 * JD-Core Version:    0.7.0.1
 */