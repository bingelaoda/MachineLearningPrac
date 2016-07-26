/*    1:     */ package weka.classifiers.bayes.net;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.io.StringReader;
/*    6:     */ import java.util.ArrayList;
/*    7:     */ import java.util.StringTokenizer;
/*    8:     */ import javax.xml.parsers.DocumentBuilder;
/*    9:     */ import javax.xml.parsers.DocumentBuilderFactory;
/*   10:     */ import org.w3c.dom.CharacterData;
/*   11:     */ import org.w3c.dom.Document;
/*   12:     */ import org.w3c.dom.Element;
/*   13:     */ import org.w3c.dom.Node;
/*   14:     */ import org.w3c.dom.NodeList;
/*   15:     */ import org.xml.sax.InputSource;
/*   16:     */ import weka.classifiers.bayes.BayesNet;
/*   17:     */ import weka.classifiers.bayes.net.estimate.DiscreteEstimatorBayes;
/*   18:     */ import weka.core.Attribute;
/*   19:     */ import weka.core.Instances;
/*   20:     */ import weka.core.RevisionUtils;
/*   21:     */ import weka.core.SerializedObject;
/*   22:     */ import weka.estimators.Estimator;
/*   23:     */ import weka.filters.Filter;
/*   24:     */ import weka.filters.unsupervised.attribute.Reorder;
/*   25:     */ 
/*   26:     */ public class EditableBayesNet
/*   27:     */   extends BayesNet
/*   28:     */ {
/*   29:     */   static final long serialVersionUID = 746037443258735954L;
/*   30:     */   protected ArrayList<Integer> m_nPositionX;
/*   31:     */   protected ArrayList<Integer> m_nPositionY;
/*   32:     */   protected ArrayList<double[]> m_fMarginP;
/*   33:     */   protected ArrayList<Integer> m_nEvidence;
/*   34:     */   static final int TEST = 0;
/*   35:     */   static final int EXECUTE = 1;
/*   36:     */   
/*   37:     */   public EditableBayesNet()
/*   38:     */   {
/*   39: 107 */     this.m_nEvidence = new ArrayList(0);
/*   40: 108 */     this.m_fMarginP = new ArrayList(0);
/*   41: 109 */     this.m_nPositionX = new ArrayList();
/*   42: 110 */     this.m_nPositionY = new ArrayList();
/*   43: 111 */     clearUndoStack();
/*   44:     */   }
/*   45:     */   
/*   46:     */   public EditableBayesNet(Instances instances)
/*   47:     */   {
/*   48:     */     try
/*   49:     */     {
/*   50: 120 */       if (instances.classIndex() < 0) {
/*   51: 121 */         instances.setClassIndex(instances.numAttributes() - 1);
/*   52:     */       }
/*   53: 123 */       this.m_Instances = normalizeDataSet(instances);
/*   54:     */     }
/*   55:     */     catch (Exception e)
/*   56:     */     {
/*   57: 125 */       e.printStackTrace();
/*   58:     */     }
/*   59: 128 */     int nNodes = getNrOfNodes();
/*   60: 129 */     this.m_ParentSets = new ParentSet[nNodes];
/*   61: 130 */     for (int i = 0; i < nNodes; i++) {
/*   62: 131 */       this.m_ParentSets[i] = new ParentSet();
/*   63:     */     }
/*   64: 133 */     this.m_Distributions = new Estimator[nNodes][];
/*   65: 134 */     for (int iNode = 0; iNode < nNodes; iNode++)
/*   66:     */     {
/*   67: 135 */       this.m_Distributions[iNode] = new Estimator[1];
/*   68: 136 */       this.m_Distributions[iNode][0] = new DiscreteEstimatorBayes(getCardinality(iNode), 0.5D);
/*   69:     */     }
/*   70: 140 */     this.m_nEvidence = new ArrayList(nNodes);
/*   71: 141 */     for (int i = 0; i < nNodes; i++) {
/*   72: 142 */       this.m_nEvidence.add(Integer.valueOf(-1));
/*   73:     */     }
/*   74: 144 */     this.m_fMarginP = new ArrayList(nNodes);
/*   75: 145 */     for (int i = 0; i < nNodes; i++)
/*   76:     */     {
/*   77: 146 */       double[] P = new double[getCardinality(i)];
/*   78: 147 */       this.m_fMarginP.add(P);
/*   79:     */     }
/*   80: 150 */     this.m_nPositionX = new ArrayList(nNodes);
/*   81: 151 */     this.m_nPositionY = new ArrayList(nNodes);
/*   82: 152 */     for (int iNode = 0; iNode < nNodes; iNode++)
/*   83:     */     {
/*   84: 153 */       this.m_nPositionX.add(Integer.valueOf(iNode % 10 * 50));
/*   85: 154 */       this.m_nPositionY.add(Integer.valueOf(iNode / 10 * 50));
/*   86:     */     }
/*   87:     */   }
/*   88:     */   
/*   89:     */   public EditableBayesNet(BIFReader other)
/*   90:     */   {
/*   91: 164 */     this.m_Instances = other.m_Instances;
/*   92: 165 */     this.m_ParentSets = other.getParentSets();
/*   93: 166 */     this.m_Distributions = other.getDistributions();
/*   94:     */     
/*   95: 168 */     int nNodes = getNrOfNodes();
/*   96: 169 */     this.m_nPositionX = new ArrayList(nNodes);
/*   97: 170 */     this.m_nPositionY = new ArrayList(nNodes);
/*   98: 171 */     for (int i = 0; i < nNodes; i++)
/*   99:     */     {
/*  100: 172 */       this.m_nPositionX.add(Integer.valueOf(other.m_nPositionX[i]));
/*  101: 173 */       this.m_nPositionY.add(Integer.valueOf(other.m_nPositionY[i]));
/*  102:     */     }
/*  103: 175 */     this.m_nEvidence = new ArrayList(nNodes);
/*  104: 176 */     for (int i = 0; i < nNodes; i++) {
/*  105: 177 */       this.m_nEvidence.add(Integer.valueOf(-1));
/*  106:     */     }
/*  107: 179 */     this.m_fMarginP = new ArrayList(nNodes);
/*  108: 180 */     for (int i = 0; i < nNodes; i++)
/*  109:     */     {
/*  110: 181 */       double[] P = new double[getCardinality(i)];
/*  111: 182 */       this.m_fMarginP.add(P);
/*  112:     */     }
/*  113: 184 */     clearUndoStack();
/*  114:     */   }
/*  115:     */   
/*  116:     */   public EditableBayesNet(boolean bSetInstances)
/*  117:     */   {
/*  118: 194 */     this.m_nEvidence = new ArrayList(0);
/*  119: 195 */     this.m_fMarginP = new ArrayList(0);
/*  120: 196 */     this.m_nPositionX = new ArrayList();
/*  121: 197 */     this.m_nPositionY = new ArrayList();
/*  122: 198 */     clearUndoStack();
/*  123: 199 */     if (bSetInstances) {
/*  124: 200 */       this.m_Instances = new Instances("New Network", new ArrayList(0), 0);
/*  125:     */     }
/*  126:     */   }
/*  127:     */   
/*  128:     */   public void setData(Instances instances)
/*  129:     */     throws Exception
/*  130:     */   {
/*  131: 215 */     int[] order = new int[getNrOfNodes()];
/*  132: 216 */     for (int iNode = 0; iNode < getNrOfNodes(); iNode++)
/*  133:     */     {
/*  134: 217 */       String sName = getNodeName(iNode);
/*  135: 218 */       int nNode = 0;
/*  136: 220 */       while ((nNode < getNrOfNodes()) && (!sName.equals(instances.attribute(nNode).name()))) {
/*  137: 221 */         nNode++;
/*  138:     */       }
/*  139: 223 */       if (nNode >= getNrOfNodes()) {
/*  140: 224 */         throw new Exception("Cannot find node named [[[" + sName + "]]] in the data");
/*  141:     */       }
/*  142: 227 */       order[iNode] = nNode;
/*  143:     */     }
/*  144: 229 */     Reorder reorderFilter = new Reorder();
/*  145: 230 */     reorderFilter.setAttributeIndicesArray(order);
/*  146: 231 */     reorderFilter.setInputFormat(instances);
/*  147: 232 */     instances = Filter.useFilter(instances, reorderFilter);
/*  148:     */     
/*  149: 234 */     Instances newInstances = new Instances(this.m_Instances, 0);
/*  150: 235 */     if ((this.m_DiscretizeFilter == null) && (this.m_MissingValuesFilter == null)) {
/*  151: 236 */       newInstances = normalizeDataSet(instances);
/*  152:     */     } else {
/*  153: 238 */       for (int iInstance = 0; iInstance < instances.numInstances(); iInstance++) {
/*  154: 239 */         newInstances.add(normalizeInstance(instances.instance(iInstance)));
/*  155:     */       }
/*  156:     */     }
/*  157: 243 */     for (int iNode = 0; iNode < getNrOfNodes(); iNode++) {
/*  158: 244 */       if (newInstances.attribute(iNode).numValues() != getCardinality(iNode)) {
/*  159: 245 */         throw new Exception("Number of values of node [[[" + getNodeName(iNode) + "]]] differs in (discretized) dataset.");
/*  160:     */       }
/*  161:     */     }
/*  162: 251 */     this.m_Instances = newInstances;
/*  163:     */   }
/*  164:     */   
/*  165:     */   public int getNode2(String sNodeName)
/*  166:     */   {
/*  167: 260 */     int iNode = 0;
/*  168: 261 */     while (iNode < this.m_Instances.numAttributes())
/*  169:     */     {
/*  170: 262 */       if (this.m_Instances.attribute(iNode).name().equals(sNodeName)) {
/*  171: 263 */         return iNode;
/*  172:     */       }
/*  173: 265 */       iNode++;
/*  174:     */     }
/*  175: 267 */     return -1;
/*  176:     */   }
/*  177:     */   
/*  178:     */   public int getNode(String sNodeName)
/*  179:     */     throws Exception
/*  180:     */   {
/*  181: 277 */     int iNode = getNode2(sNodeName);
/*  182: 278 */     if (iNode < 0) {
/*  183: 279 */       throw new Exception("Could not find node [[" + sNodeName + "]]");
/*  184:     */     }
/*  185: 281 */     return iNode;
/*  186:     */   }
/*  187:     */   
/*  188:     */   public void addNode(String sName, int nCardinality)
/*  189:     */     throws Exception
/*  190:     */   {
/*  191: 294 */     addNode(sName, nCardinality, 100 + getNrOfNodes() * 10, 100 + getNrOfNodes() * 10);
/*  192:     */   }
/*  193:     */   
/*  194:     */   public void addNode(String sName, int nCardinality, int nPosX, int nPosY)
/*  195:     */     throws Exception
/*  196:     */   {
/*  197: 312 */     if (getNode2(sName) >= 0)
/*  198:     */     {
/*  199: 313 */       addNode(sName + "x", nCardinality);
/*  200: 314 */       return;
/*  201:     */     }
/*  202: 317 */     ArrayList<String> values = new ArrayList(nCardinality);
/*  203: 318 */     for (int iValue = 0; iValue < nCardinality; iValue++) {
/*  204: 319 */       values.add("Value" + (iValue + 1));
/*  205:     */     }
/*  206: 321 */     Attribute att = new Attribute(sName, values);
/*  207: 322 */     this.m_Instances.insertAttributeAt(att, this.m_Instances.numAttributes());
/*  208: 323 */     int nAtts = this.m_Instances.numAttributes();
/*  209:     */     
/*  210: 325 */     ParentSet[] parentSets = new ParentSet[nAtts];
/*  211: 326 */     for (int iParentSet = 0; iParentSet < nAtts - 1; iParentSet++) {
/*  212: 327 */       parentSets[iParentSet] = this.m_ParentSets[iParentSet];
/*  213:     */     }
/*  214: 329 */     parentSets[(nAtts - 1)] = new ParentSet();
/*  215: 330 */     this.m_ParentSets = parentSets;
/*  216:     */     
/*  217: 332 */     Estimator[][] distributions = new Estimator[nAtts][];
/*  218: 333 */     for (int iNode = 0; iNode < nAtts - 1; iNode++) {
/*  219: 334 */       distributions[iNode] = this.m_Distributions[iNode];
/*  220:     */     }
/*  221: 336 */     distributions[(nAtts - 1)] = new Estimator[1];
/*  222: 337 */     distributions[(nAtts - 1)][0] = new DiscreteEstimatorBayes(nCardinality, 0.5D);
/*  223: 338 */     this.m_Distributions = distributions;
/*  224:     */     
/*  225: 340 */     this.m_nPositionX.add(Integer.valueOf(nPosX));
/*  226: 341 */     this.m_nPositionY.add(Integer.valueOf(nPosY));
/*  227:     */     
/*  228: 343 */     this.m_nEvidence.add(Integer.valueOf(-1));
/*  229: 344 */     double[] fMarginP = new double[nCardinality];
/*  230: 345 */     for (int iValue = 0; iValue < nCardinality; iValue++) {
/*  231: 346 */       fMarginP[iValue] = (1.0D / nCardinality);
/*  232:     */     }
/*  233: 348 */     this.m_fMarginP.add(fMarginP);
/*  234: 350 */     if (this.m_bNeedsUndoAction) {
/*  235: 351 */       addUndoAction(new AddNodeAction(sName, nCardinality, nPosX, nPosY));
/*  236:     */     }
/*  237:     */   }
/*  238:     */   
/*  239:     */   public void deleteNode(String sName)
/*  240:     */     throws Exception
/*  241:     */   {
/*  242: 366 */     int nTargetNode = getNode(sName);
/*  243: 367 */     deleteNode(nTargetNode);
/*  244:     */   }
/*  245:     */   
/*  246:     */   public void deleteNode(int nTargetNode)
/*  247:     */     throws Exception
/*  248:     */   {
/*  249: 381 */     if (this.m_bNeedsUndoAction) {
/*  250: 382 */       addUndoAction(new DeleteNodeAction(nTargetNode));
/*  251:     */     }
/*  252: 384 */     int nAtts = this.m_Instances.numAttributes() - 1;
/*  253: 385 */     int nTargetCard = this.m_Instances.attribute(nTargetNode).numValues();
/*  254:     */     
/*  255: 387 */     Estimator[][] distributions = new Estimator[nAtts][];
/*  256: 388 */     for (int iNode = 0; iNode < nAtts; iNode++)
/*  257:     */     {
/*  258: 389 */       int iNode2 = iNode;
/*  259: 390 */       if (iNode >= nTargetNode) {
/*  260: 391 */         iNode2++;
/*  261:     */       }
/*  262: 393 */       Estimator[] distribution = this.m_Distributions[iNode2];
/*  263: 394 */       if (this.m_ParentSets[iNode2].contains(nTargetNode))
/*  264:     */       {
/*  265: 396 */         int nParentCard = this.m_ParentSets[iNode2].getCardinalityOfParents();
/*  266: 397 */         nParentCard /= nTargetCard;
/*  267: 398 */         Estimator[] distribution2 = new Estimator[nParentCard];
/*  268: 399 */         for (int iParent = 0; iParent < nParentCard; iParent++) {
/*  269: 400 */           distribution2[iParent] = distribution[iParent];
/*  270:     */         }
/*  271: 402 */         distribution = distribution2;
/*  272:     */       }
/*  273: 404 */       distributions[iNode] = distribution;
/*  274:     */     }
/*  275: 406 */     this.m_Distributions = distributions;
/*  276:     */     
/*  277: 408 */     ParentSet[] parentSets = new ParentSet[nAtts];
/*  278: 409 */     for (int iParentSet = 0; iParentSet < nAtts; iParentSet++)
/*  279:     */     {
/*  280: 410 */       int iParentSet2 = iParentSet;
/*  281: 411 */       if (iParentSet >= nTargetNode) {
/*  282: 412 */         iParentSet2++;
/*  283:     */       }
/*  284: 414 */       ParentSet parentset = this.m_ParentSets[iParentSet2];
/*  285: 415 */       parentset.deleteParent(nTargetNode, this.m_Instances);
/*  286: 416 */       for (int iParent = 0; iParent < parentset.getNrOfParents(); iParent++)
/*  287:     */       {
/*  288: 417 */         int nParent = parentset.getParent(iParent);
/*  289: 418 */         if (nParent > nTargetNode) {
/*  290: 419 */           parentset.SetParent(iParent, nParent - 1);
/*  291:     */         }
/*  292:     */       }
/*  293: 422 */       parentSets[iParentSet] = parentset;
/*  294:     */     }
/*  295: 424 */     this.m_ParentSets = parentSets;
/*  296:     */     
/*  297: 426 */     this.m_Instances.setClassIndex(-1);
/*  298: 427 */     this.m_Instances.deleteAttributeAt(nTargetNode);
/*  299: 428 */     this.m_Instances.setClassIndex(nAtts - 1);
/*  300:     */     
/*  301:     */ 
/*  302: 431 */     this.m_nPositionX.remove(nTargetNode);
/*  303: 432 */     this.m_nPositionY.remove(nTargetNode);
/*  304:     */     
/*  305: 434 */     this.m_nEvidence.remove(nTargetNode);
/*  306: 435 */     this.m_fMarginP.remove(nTargetNode);
/*  307:     */   }
/*  308:     */   
/*  309:     */   public void deleteSelection(ArrayList<Integer> nodes)
/*  310:     */   {
/*  311: 449 */     for (int i = 0; i < nodes.size(); i++) {
/*  312: 450 */       for (int j = i + 1; j < nodes.size(); j++) {
/*  313: 451 */         if (((Integer)nodes.get(i)).intValue() > ((Integer)nodes.get(j)).intValue())
/*  314:     */         {
/*  315: 452 */           int h = ((Integer)nodes.get(i)).intValue();
/*  316: 453 */           nodes.set(i, nodes.get(j));
/*  317: 454 */           nodes.set(j, Integer.valueOf(h));
/*  318:     */         }
/*  319:     */       }
/*  320:     */     }
/*  321: 459 */     if (this.m_bNeedsUndoAction) {
/*  322: 460 */       addUndoAction(new DeleteSelectionAction(nodes));
/*  323:     */     }
/*  324: 462 */     boolean bNeedsUndoAction = this.m_bNeedsUndoAction;
/*  325: 463 */     this.m_bNeedsUndoAction = false;
/*  326:     */     try
/*  327:     */     {
/*  328: 465 */       for (int iNode = nodes.size() - 1; iNode >= 0; iNode--) {
/*  329: 466 */         deleteNode(((Integer)nodes.get(iNode)).intValue());
/*  330:     */       }
/*  331:     */     }
/*  332:     */     catch (Exception e)
/*  333:     */     {
/*  334: 469 */       e.printStackTrace();
/*  335:     */     }
/*  336: 471 */     this.m_bNeedsUndoAction = bNeedsUndoAction;
/*  337:     */   }
/*  338:     */   
/*  339:     */   ArrayList<Node> selectElements(Node item, String sElement)
/*  340:     */     throws Exception
/*  341:     */   {
/*  342: 481 */     NodeList children = item.getChildNodes();
/*  343: 482 */     ArrayList<Node> nodelist = new ArrayList();
/*  344: 483 */     for (int iNode = 0; iNode < children.getLength(); iNode++)
/*  345:     */     {
/*  346: 484 */       Node node = children.item(iNode);
/*  347: 485 */       if ((node.getNodeType() == 1) && (node.getNodeName().equals(sElement))) {
/*  348: 487 */         nodelist.add(node);
/*  349:     */       }
/*  350:     */     }
/*  351: 490 */     return nodelist;
/*  352:     */   }
/*  353:     */   
/*  354:     */   public String getContent(Element node)
/*  355:     */   {
/*  356: 506 */     String result = "";
/*  357: 507 */     NodeList list = node.getChildNodes();
/*  358: 509 */     for (int i = 0; i < list.getLength(); i++)
/*  359:     */     {
/*  360: 510 */       Node item = list.item(i);
/*  361: 511 */       if (item.getNodeType() == 3) {
/*  362: 512 */         result = result + "\n" + item.getNodeValue();
/*  363:     */       }
/*  364:     */     }
/*  365: 516 */     return result;
/*  366:     */   }
/*  367:     */   
/*  368:     */   Element getDefinition(Document doc, String sName)
/*  369:     */     throws Exception
/*  370:     */   {
/*  371: 527 */     NodeList nodelist = doc.getElementsByTagName("DEFINITION");
/*  372: 528 */     for (int iNode = 0; iNode < nodelist.getLength(); iNode++)
/*  373:     */     {
/*  374: 529 */       Node node = nodelist.item(iNode);
/*  375: 530 */       ArrayList<Node> list = selectElements(node, "FOR");
/*  376: 531 */       if (list.size() > 0)
/*  377:     */       {
/*  378: 532 */         Node forNode = (Node)list.get(0);
/*  379: 533 */         if (getContent((Element)forNode).trim().equals(sName)) {
/*  380: 534 */           return (Element)node;
/*  381:     */         }
/*  382:     */       }
/*  383:     */     }
/*  384: 538 */     throw new Exception("Could not find definition for ((" + sName + "))");
/*  385:     */   }
/*  386:     */   
/*  387:     */   public void paste(String sXML)
/*  388:     */     throws Exception
/*  389:     */   {
/*  390:     */     try
/*  391:     */     {
/*  392: 558 */       paste(sXML, 0);
/*  393:     */     }
/*  394:     */     catch (Exception e)
/*  395:     */     {
/*  396: 560 */       throw e;
/*  397:     */     }
/*  398: 562 */     paste(sXML, 1);
/*  399:     */   }
/*  400:     */   
/*  401:     */   void paste(String sXML, int mode)
/*  402:     */     throws Exception
/*  403:     */   {
/*  404: 574 */     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
/*  405: 575 */     factory.setValidating(true);
/*  406: 576 */     Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(sXML)));
/*  407:     */     
/*  408: 578 */     doc.normalize();
/*  409:     */     
/*  410:     */ 
/*  411: 581 */     NodeList nodelist = doc.getElementsByTagName("VARIABLE");
/*  412: 582 */     ArrayList<String> sBaseNames = new ArrayList();
/*  413: 583 */     Instances instances = new Instances(this.m_Instances, 0);
/*  414: 584 */     int nBase = instances.numAttributes();
/*  415: 585 */     for (int iNode = 0; iNode < nodelist.getLength(); iNode++)
/*  416:     */     {
/*  417: 589 */       ArrayList<Node> valueslist = selectElements(nodelist.item(iNode), "OUTCOME");
/*  418:     */       
/*  419: 591 */       int nValues = valueslist.size();
/*  420:     */       
/*  421: 593 */       ArrayList<String> nomStrings = new ArrayList(nValues + 1);
/*  422: 594 */       for (int iValue = 0; iValue < nValues; iValue++)
/*  423:     */       {
/*  424: 595 */         Node node = ((Node)valueslist.get(iValue)).getFirstChild();
/*  425: 596 */         String sValue = ((CharacterData)node).getData();
/*  426: 597 */         if (sValue == null) {
/*  427: 598 */           sValue = "Value" + (iValue + 1);
/*  428:     */         }
/*  429: 600 */         nomStrings.add(sValue);
/*  430:     */       }
/*  431: 604 */       ArrayList<Node> nodelist2 = selectElements(nodelist.item(iNode), "NAME");
/*  432: 605 */       if (nodelist2.size() == 0) {
/*  433: 606 */         throw new Exception("No name specified for variable");
/*  434:     */       }
/*  435: 608 */       String sBaseName = ((CharacterData)((Node)nodelist2.get(0)).getFirstChild()).getData();
/*  436:     */       
/*  437: 610 */       sBaseNames.add(sBaseName);
/*  438: 611 */       String sNodeName = sBaseName;
/*  439: 612 */       if (getNode2(sNodeName) >= 0) {
/*  440: 613 */         sNodeName = "Copy of " + sBaseName;
/*  441:     */       }
/*  442: 615 */       int iAttempt = 2;
/*  443: 616 */       while (getNode2(sNodeName) >= 0)
/*  444:     */       {
/*  445: 617 */         sNodeName = "Copy (" + iAttempt + ") of " + sBaseName;
/*  446: 618 */         iAttempt++;
/*  447:     */       }
/*  448: 621 */       Attribute att = new Attribute(sNodeName, nomStrings);
/*  449: 622 */       instances.insertAttributeAt(att, instances.numAttributes());
/*  450:     */       
/*  451: 624 */       valueslist = selectElements(nodelist.item(iNode), "PROPERTY");
/*  452: 625 */       nValues = valueslist.size();
/*  453:     */       
/*  454: 627 */       int nPosX = iAttempt * 10;
/*  455: 628 */       int nPosY = iAttempt * 10;
/*  456: 629 */       for (int iValue = 0; iValue < nValues; iValue++)
/*  457:     */       {
/*  458: 631 */         Node node = ((Node)valueslist.get(iValue)).getFirstChild();
/*  459: 632 */         String sValue = ((CharacterData)node).getData();
/*  460: 633 */         if (sValue.startsWith("position"))
/*  461:     */         {
/*  462: 634 */           int i0 = sValue.indexOf('(');
/*  463: 635 */           int i1 = sValue.indexOf(',');
/*  464: 636 */           int i2 = sValue.indexOf(')');
/*  465: 637 */           String sX = sValue.substring(i0 + 1, i1).trim();
/*  466: 638 */           String sY = sValue.substring(i1 + 1, i2).trim();
/*  467:     */           try
/*  468:     */           {
/*  469: 640 */             nPosX = Integer.parseInt(sX) + iAttempt * 10;
/*  470: 641 */             nPosY = Integer.parseInt(sY) + iAttempt * 10;
/*  471:     */           }
/*  472:     */           catch (NumberFormatException e)
/*  473:     */           {
/*  474: 643 */             System.err.println("Wrong number format in position :(" + sX + "," + sY + ")");
/*  475:     */           }
/*  476:     */         }
/*  477:     */       }
/*  478: 648 */       if (mode == 1)
/*  479:     */       {
/*  480: 649 */         this.m_nPositionX.add(Integer.valueOf(nPosX));
/*  481: 650 */         this.m_nPositionY.add(Integer.valueOf(nPosY));
/*  482:     */       }
/*  483:     */     }
/*  484: 656 */     Estimator[][] distributions = new Estimator[nBase + sBaseNames.size()][];
/*  485: 657 */     ParentSet[] parentsets = new ParentSet[nBase + sBaseNames.size()];
/*  486: 658 */     for (int iNode = 0; iNode < nBase; iNode++)
/*  487:     */     {
/*  488: 659 */       distributions[iNode] = this.m_Distributions[iNode];
/*  489: 660 */       parentsets[iNode] = this.m_ParentSets[iNode];
/*  490:     */     }
/*  491: 662 */     if (mode == 1) {
/*  492: 663 */       this.m_Instances = instances;
/*  493:     */     }
/*  494: 666 */     for (int iNode = 0; iNode < sBaseNames.size(); iNode++)
/*  495:     */     {
/*  496: 668 */       String sName = (String)sBaseNames.get(iNode);
/*  497: 669 */       Element definition = getDefinition(doc, sName);
/*  498: 670 */       parentsets[(nBase + iNode)] = new ParentSet();
/*  499:     */       
/*  500:     */ 
/*  501:     */ 
/*  502: 674 */       ArrayList<Node> nodelist2 = selectElements(definition, "GIVEN");
/*  503: 675 */       for (int iParent = 0; iParent < nodelist2.size(); iParent++)
/*  504:     */       {
/*  505: 676 */         Node parentName = ((Node)nodelist2.get(iParent)).getFirstChild();
/*  506: 677 */         String sParentName = ((CharacterData)parentName).getData();
/*  507: 678 */         int nParent = -1;
/*  508: 679 */         for (int iBase = 0; iBase < sBaseNames.size(); iBase++) {
/*  509: 680 */           if (sParentName.equals(sBaseNames.get(iBase))) {
/*  510: 681 */             nParent = nBase + iBase;
/*  511:     */           }
/*  512:     */         }
/*  513: 684 */         if (nParent < 0) {
/*  514: 685 */           nParent = getNode(sParentName);
/*  515:     */         }
/*  516: 687 */         parentsets[(nBase + iNode)].addParent(nParent, instances);
/*  517:     */       }
/*  518: 690 */       int nCardinality = parentsets[(nBase + iNode)].getCardinalityOfParents();
/*  519: 691 */       int nValues = instances.attribute(nBase + iNode).numValues();
/*  520: 692 */       distributions[(nBase + iNode)] = new Estimator[nCardinality];
/*  521: 693 */       for (int i = 0; i < nCardinality; i++) {
/*  522: 694 */         distributions[(nBase + iNode)][i] = new DiscreteEstimatorBayes(nValues, 0.0D);
/*  523:     */       }
/*  524: 698 */       String sTable = getContent((Element)selectElements(definition, "TABLE").get(0));
/*  525:     */       
/*  526: 700 */       sTable = sTable.replaceAll("\\n", " ");
/*  527: 701 */       StringTokenizer st = new StringTokenizer(sTable.toString());
/*  528: 703 */       for (int i = 0; i < nCardinality; i++)
/*  529:     */       {
/*  530: 704 */         DiscreteEstimatorBayes d = (DiscreteEstimatorBayes)distributions[(nBase + iNode)][i];
/*  531: 706 */         for (int iValue = 0; iValue < nValues; iValue++)
/*  532:     */         {
/*  533: 707 */           String sWeight = st.nextToken();
/*  534: 708 */           d.addValue(iValue, new Double(sWeight).doubleValue());
/*  535:     */         }
/*  536:     */       }
/*  537: 711 */       if (mode == 1)
/*  538:     */       {
/*  539: 712 */         this.m_nEvidence.add(nBase + iNode, Integer.valueOf(-1));
/*  540: 713 */         this.m_fMarginP.add(nBase + iNode, new double[getCardinality(nBase + iNode)]);
/*  541:     */       }
/*  542:     */     }
/*  543: 717 */     if (mode == 1)
/*  544:     */     {
/*  545: 718 */       this.m_Distributions = distributions;
/*  546: 719 */       this.m_ParentSets = parentsets;
/*  547:     */     }
/*  548: 722 */     if ((mode == 1) && (this.m_bNeedsUndoAction)) {
/*  549: 723 */       addUndoAction(new PasteAction(sXML, nBase));
/*  550:     */     }
/*  551:     */   }
/*  552:     */   
/*  553:     */   public void addArc(String sParent, String sChild)
/*  554:     */     throws Exception
/*  555:     */   {
/*  556: 736 */     int nParent = getNode(sParent);
/*  557: 737 */     int nChild = getNode(sChild);
/*  558: 738 */     addArc(nParent, nChild);
/*  559:     */   }
/*  560:     */   
/*  561:     */   public void addArc(int nParent, int nChild)
/*  562:     */     throws Exception
/*  563:     */   {
/*  564: 751 */     if (this.m_bNeedsUndoAction) {
/*  565: 752 */       addUndoAction(new AddArcAction(nParent, nChild));
/*  566:     */     }
/*  567: 754 */     int nOldCard = this.m_ParentSets[nChild].getCardinalityOfParents();
/*  568:     */     
/*  569: 756 */     this.m_ParentSets[nChild].addParent(nParent, this.m_Instances);
/*  570:     */     
/*  571: 758 */     int nNewCard = this.m_ParentSets[nChild].getCardinalityOfParents();
/*  572: 759 */     Estimator[] ds = new Estimator[nNewCard];
/*  573: 760 */     for (int iParent = 0; iParent < nNewCard; iParent++) {
/*  574: 761 */       ds[iParent] = Estimator.clone(this.m_Distributions[nChild][(iParent % nOldCard)]);
/*  575:     */     }
/*  576: 764 */     this.m_Distributions[nChild] = ds;
/*  577:     */   }
/*  578:     */   
/*  579:     */   public void addArc(String sParent, ArrayList<Integer> nodes)
/*  580:     */     throws Exception
/*  581:     */   {
/*  582: 776 */     int nParent = getNode(sParent);
/*  583: 778 */     if (this.m_bNeedsUndoAction) {
/*  584: 779 */       addUndoAction(new AddArcAction(nParent, nodes));
/*  585:     */     }
/*  586: 781 */     boolean bNeedsUndoAction = this.m_bNeedsUndoAction;
/*  587: 782 */     this.m_bNeedsUndoAction = false;
/*  588: 783 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/*  589:     */     {
/*  590: 784 */       int nNode = ((Integer)nodes.get(iNode)).intValue();
/*  591: 785 */       addArc(nParent, nNode);
/*  592:     */     }
/*  593: 787 */     this.m_bNeedsUndoAction = bNeedsUndoAction;
/*  594:     */   }
/*  595:     */   
/*  596:     */   public void deleteArc(String sParent, String sChild)
/*  597:     */     throws Exception
/*  598:     */   {
/*  599: 799 */     int nParent = getNode(sParent);
/*  600: 800 */     int nChild = getNode(sChild);
/*  601: 801 */     deleteArc(nParent, nChild);
/*  602:     */   }
/*  603:     */   
/*  604:     */   public void deleteArc(int nParent, int nChild)
/*  605:     */     throws Exception
/*  606:     */   {
/*  607: 814 */     if (this.m_bNeedsUndoAction) {
/*  608: 815 */       addUndoAction(new DeleteArcAction(nParent, nChild));
/*  609:     */     }
/*  610: 819 */     int nParentCard = this.m_ParentSets[nChild].getCardinalityOfParents();
/*  611: 820 */     int nTargetCard = this.m_Instances.attribute(nChild).numValues();
/*  612: 821 */     nParentCard /= nTargetCard;
/*  613: 822 */     Estimator[] distribution2 = new Estimator[nParentCard];
/*  614: 823 */     for (int iParent = 0; iParent < nParentCard; iParent++) {
/*  615: 824 */       distribution2[iParent] = this.m_Distributions[nChild][iParent];
/*  616:     */     }
/*  617: 826 */     this.m_Distributions[nChild] = distribution2;
/*  618:     */     
/*  619: 828 */     this.m_ParentSets[nChild].deleteParent(nParent, this.m_Instances);
/*  620:     */   }
/*  621:     */   
/*  622:     */   public void setDistribution(String sName, double[][] P)
/*  623:     */     throws Exception
/*  624:     */   {
/*  625: 840 */     int nTargetNode = getNode(sName);
/*  626: 841 */     setDistribution(nTargetNode, P);
/*  627:     */   }
/*  628:     */   
/*  629:     */   public void setDistribution(int nTargetNode, double[][] P)
/*  630:     */     throws Exception
/*  631:     */   {
/*  632: 854 */     if (this.m_bNeedsUndoAction) {
/*  633: 855 */       addUndoAction(new SetDistributionAction(nTargetNode, P));
/*  634:     */     }
/*  635: 857 */     Estimator[] distributions = this.m_Distributions[nTargetNode];
/*  636: 858 */     for (int iParent = 0; iParent < distributions.length; iParent++)
/*  637:     */     {
/*  638: 859 */       DiscreteEstimatorBayes distribution = new DiscreteEstimatorBayes(P[0].length, 0.0D);
/*  639: 861 */       for (int iValue = 0; iValue < distribution.getNumSymbols(); iValue++) {
/*  640: 862 */         distribution.addValue(iValue, P[iParent][iValue]);
/*  641:     */       }
/*  642: 864 */       distributions[iParent] = distribution;
/*  643:     */     }
/*  644:     */   }
/*  645:     */   
/*  646:     */   public double[][] getDistribution(String sName)
/*  647:     */   {
/*  648: 876 */     int nTargetNode = getNode2(sName);
/*  649: 877 */     return getDistribution(nTargetNode);
/*  650:     */   }
/*  651:     */   
/*  652:     */   public double[][] getDistribution(int nTargetNode)
/*  653:     */   {
/*  654: 887 */     int nParentCard = this.m_ParentSets[nTargetNode].getCardinalityOfParents();
/*  655: 888 */     int nCard = this.m_Instances.attribute(nTargetNode).numValues();
/*  656: 889 */     double[][] P = new double[nParentCard][nCard];
/*  657: 890 */     for (int iParent = 0; iParent < nParentCard; iParent++) {
/*  658: 891 */       for (int iValue = 0; iValue < nCard; iValue++) {
/*  659: 892 */         P[iParent][iValue] = this.m_Distributions[nTargetNode][iParent].getProbability(iValue);
/*  660:     */       }
/*  661:     */     }
/*  662: 896 */     return P;
/*  663:     */   }
/*  664:     */   
/*  665:     */   public String[] getValues(String sName)
/*  666:     */   {
/*  667: 905 */     int nTargetNode = getNode2(sName);
/*  668: 906 */     return getValues(nTargetNode);
/*  669:     */   }
/*  670:     */   
/*  671:     */   public String[] getValues(int nTargetNode)
/*  672:     */   {
/*  673: 915 */     String[] values = new String[getCardinality(nTargetNode)];
/*  674: 916 */     for (int iValue = 0; iValue < values.length; iValue++) {
/*  675: 917 */       values[iValue] = this.m_Instances.attribute(nTargetNode).value(iValue);
/*  676:     */     }
/*  677: 919 */     return values;
/*  678:     */   }
/*  679:     */   
/*  680:     */   public String getValueName(int nTargetNode, int iValue)
/*  681:     */   {
/*  682: 929 */     return this.m_Instances.attribute(nTargetNode).value(iValue);
/*  683:     */   }
/*  684:     */   
/*  685:     */   public void setNodeName(int nTargetNode, String sName)
/*  686:     */   {
/*  687: 940 */     if (this.m_bNeedsUndoAction) {
/*  688: 941 */       addUndoAction(new RenameAction(nTargetNode, getNodeName(nTargetNode), sName));
/*  689:     */     }
/*  690: 944 */     Attribute att = this.m_Instances.attribute(nTargetNode);
/*  691: 945 */     int nCardinality = att.numValues();
/*  692: 946 */     ArrayList<String> values = new ArrayList(nCardinality);
/*  693: 947 */     for (int iValue = 0; iValue < nCardinality; iValue++) {
/*  694: 948 */       values.add(att.value(iValue));
/*  695:     */     }
/*  696: 950 */     replaceAtt(nTargetNode, sName, values);
/*  697:     */   }
/*  698:     */   
/*  699:     */   public void renameNodeValue(int nTargetNode, String sValue, String sNewValue)
/*  700:     */   {
/*  701: 962 */     if (this.m_bNeedsUndoAction) {
/*  702: 963 */       addUndoAction(new RenameValueAction(nTargetNode, sValue, sNewValue));
/*  703:     */     }
/*  704: 965 */     Attribute att = this.m_Instances.attribute(nTargetNode);
/*  705: 966 */     int nCardinality = att.numValues();
/*  706: 967 */     ArrayList<String> values = new ArrayList(nCardinality);
/*  707: 968 */     for (int iValue = 0; iValue < nCardinality; iValue++) {
/*  708: 969 */       if (att.value(iValue).equals(sValue)) {
/*  709: 970 */         values.add(sNewValue);
/*  710:     */       } else {
/*  711: 972 */         values.add(att.value(iValue));
/*  712:     */       }
/*  713:     */     }
/*  714: 975 */     replaceAtt(nTargetNode, att.name(), values);
/*  715:     */   }
/*  716:     */   
/*  717:     */   public void addNodeValue(int nTargetNode, String sNewValue)
/*  718:     */   {
/*  719: 988 */     if (this.m_bNeedsUndoAction) {
/*  720: 989 */       addUndoAction(new AddValueAction(nTargetNode, sNewValue));
/*  721:     */     }
/*  722: 991 */     Attribute att = this.m_Instances.attribute(nTargetNode);
/*  723: 992 */     int nCardinality = att.numValues();
/*  724: 993 */     ArrayList<String> values = new ArrayList(nCardinality);
/*  725: 994 */     for (int iValue = 0; iValue < nCardinality; iValue++) {
/*  726: 995 */       values.add(att.value(iValue));
/*  727:     */     }
/*  728: 997 */     values.add(sNewValue);
/*  729: 998 */     replaceAtt(nTargetNode, att.name(), values);
/*  730:     */     
/*  731:     */ 
/*  732:1001 */     Estimator[] distributions = this.m_Distributions[nTargetNode];
/*  733:1002 */     int nNewCard = values.size();
/*  734:1003 */     for (int iParent = 0; iParent < distributions.length; iParent++)
/*  735:     */     {
/*  736:1004 */       DiscreteEstimatorBayes distribution = new DiscreteEstimatorBayes(nNewCard, 0.0D);
/*  737:1006 */       for (int iValue = 0; iValue < nNewCard - 1; iValue++) {
/*  738:1007 */         distribution.addValue(iValue, distributions[iParent].getProbability(iValue));
/*  739:     */       }
/*  740:1010 */       distributions[iParent] = distribution;
/*  741:     */     }
/*  742:1014 */     for (int iNode = 0; iNode < getNrOfNodes(); iNode++) {
/*  743:1015 */       if (this.m_ParentSets[iNode].contains(nTargetNode))
/*  744:     */       {
/*  745:1016 */         distributions = this.m_Distributions[iNode];
/*  746:1017 */         ParentSet parentSet = this.m_ParentSets[iNode];
/*  747:1018 */         int nParentCard = parentSet.getFreshCardinalityOfParents(this.m_Instances);
/*  748:1019 */         Estimator[] newDistributions = new Estimator[nParentCard];
/*  749:1020 */         int nCard = getCardinality(iNode);
/*  750:1021 */         int nParents = parentSet.getNrOfParents();
/*  751:1022 */         int[] values2 = new int[nParents];
/*  752:1023 */         int iOldPos = 0;
/*  753:1024 */         int iTargetNode = 0;
/*  754:1025 */         while (parentSet.getParent(iTargetNode) != nTargetNode) {
/*  755:1026 */           iTargetNode++;
/*  756:     */         }
/*  757:1028 */         for (int iPos = 0; iPos < nParentCard; iPos++)
/*  758:     */         {
/*  759:1029 */           DiscreteEstimatorBayes distribution = new DiscreteEstimatorBayes(nCard, 0.0D);
/*  760:1031 */           for (int iValue = 0; iValue < nCard; iValue++) {
/*  761:1032 */             distribution.addValue(iValue, distributions[iOldPos].getProbability(iValue));
/*  762:     */           }
/*  763:1035 */           newDistributions[iPos] = distribution;
/*  764:     */           
/*  765:1037 */           int i = 0;
/*  766:1038 */           values2[i] += 1;
/*  767:1040 */           while ((i < nParents) && (values2[i] == getCardinality(parentSet.getParent(i))))
/*  768:     */           {
/*  769:1041 */             values2[i] = 0;
/*  770:1042 */             i++;
/*  771:1043 */             if (i < nParents) {
/*  772:1044 */               values2[i] += 1;
/*  773:     */             }
/*  774:     */           }
/*  775:1047 */           if (values2[iTargetNode] != nNewCard - 1) {
/*  776:1048 */             iOldPos++;
/*  777:     */           }
/*  778:     */         }
/*  779:1051 */         this.m_Distributions[iNode] = newDistributions;
/*  780:     */       }
/*  781:     */     }
/*  782:     */   }
/*  783:     */   
/*  784:     */   public void delNodeValue(int nTargetNode, String sValue)
/*  785:     */     throws Exception
/*  786:     */   {
/*  787:1067 */     if (this.m_bNeedsUndoAction) {
/*  788:1068 */       addUndoAction(new DelValueAction(nTargetNode, sValue));
/*  789:     */     }
/*  790:1070 */     Attribute att = this.m_Instances.attribute(nTargetNode);
/*  791:1071 */     int nCardinality = att.numValues();
/*  792:1072 */     ArrayList<String> values = new ArrayList(nCardinality);
/*  793:1073 */     int nValue = -1;
/*  794:1074 */     for (int iValue = 0; iValue < nCardinality; iValue++) {
/*  795:1075 */       if (att.value(iValue).equals(sValue)) {
/*  796:1076 */         nValue = iValue;
/*  797:     */       } else {
/*  798:1078 */         values.add(att.value(iValue));
/*  799:     */       }
/*  800:     */     }
/*  801:1081 */     if (nValue < 0) {
/*  802:1083 */       throw new Exception("Node " + nTargetNode + " does not have value (" + sValue + ")");
/*  803:     */     }
/*  804:1086 */     replaceAtt(nTargetNode, att.name(), values);
/*  805:     */     
/*  806:     */ 
/*  807:1089 */     Estimator[] distributions = this.m_Distributions[nTargetNode];
/*  808:1090 */     int nCard = values.size();
/*  809:1091 */     for (int iParent = 0; iParent < distributions.length; iParent++)
/*  810:     */     {
/*  811:1092 */       DiscreteEstimatorBayes distribution = new DiscreteEstimatorBayes(nCard, 0.0D);
/*  812:1093 */       double sum = 0.0D;
/*  813:1094 */       for (int iValue = 0; iValue < nCard; iValue++) {
/*  814:1095 */         sum += distributions[iParent].getProbability(iValue);
/*  815:     */       }
/*  816:1097 */       if (sum > 0.0D) {
/*  817:1098 */         for (int iValue = 0; iValue < nCard; iValue++) {
/*  818:1099 */           distribution.addValue(iValue, distributions[iParent].getProbability(iValue) / sum);
/*  819:     */         }
/*  820:     */       } else {
/*  821:1103 */         for (int iValue = 0; iValue < nCard; iValue++) {
/*  822:1104 */           distribution.addValue(iValue, 1.0D / nCard);
/*  823:     */         }
/*  824:     */       }
/*  825:1107 */       distributions[iParent] = distribution;
/*  826:     */     }
/*  827:1111 */     for (int iNode = 0; iNode < getNrOfNodes(); iNode++) {
/*  828:1112 */       if (this.m_ParentSets[iNode].contains(nTargetNode))
/*  829:     */       {
/*  830:1113 */         ParentSet parentSet = this.m_ParentSets[iNode];
/*  831:1114 */         distributions = this.m_Distributions[iNode];
/*  832:1115 */         Estimator[] newDistributions = new Estimator[distributions.length * nCard / (nCard + 1)];
/*  833:     */         
/*  834:1117 */         int iCurrentDist = 0;
/*  835:     */         
/*  836:1119 */         int nParents = parentSet.getNrOfParents();
/*  837:1120 */         int[] values2 = new int[nParents];
/*  838:     */         
/*  839:1122 */         int nParentCard = parentSet.getFreshCardinalityOfParents(this.m_Instances) * (nCard + 1) / nCard;
/*  840:     */         
/*  841:1124 */         int iTargetNode = 0;
/*  842:1125 */         while (parentSet.getParent(iTargetNode) != nTargetNode) {
/*  843:1126 */           iTargetNode++;
/*  844:     */         }
/*  845:1128 */         int[] nCards = new int[nParents];
/*  846:1129 */         for (int iParent = 0; iParent < nParents; iParent++) {
/*  847:1130 */           nCards[iParent] = getCardinality(parentSet.getParent(iParent));
/*  848:     */         }
/*  849:1132 */         nCards[iTargetNode] += 1;
/*  850:1133 */         for (int iPos = 0; iPos < nParentCard; iPos++)
/*  851:     */         {
/*  852:1134 */           if (values2[iTargetNode] != nValue) {
/*  853:1135 */             newDistributions[(iCurrentDist++)] = distributions[iPos];
/*  854:     */           }
/*  855:1138 */           int i = 0;
/*  856:1139 */           values2[i] += 1;
/*  857:1140 */           while ((i < nParents) && (values2[i] == nCards[i]))
/*  858:     */           {
/*  859:1141 */             values2[i] = 0;
/*  860:1142 */             i++;
/*  861:1143 */             if (i < nParents) {
/*  862:1144 */               values2[i] += 1;
/*  863:     */             }
/*  864:     */           }
/*  865:     */         }
/*  866:1149 */         this.m_Distributions[iNode] = newDistributions;
/*  867:     */       }
/*  868:     */     }
/*  869:1153 */     if (getEvidence(nTargetNode) > nValue) {
/*  870:1154 */       setEvidence(nTargetNode, getEvidence(nTargetNode) - 1);
/*  871:     */     }
/*  872:     */   }
/*  873:     */   
/*  874:     */   public void setPosition(int iNode, int nX, int nY)
/*  875:     */   {
/*  876:1167 */     if (this.m_bNeedsUndoAction)
/*  877:     */     {
/*  878:1168 */       boolean isUpdate = false;
/*  879:1169 */       UndoAction undoAction = null;
/*  880:     */       try
/*  881:     */       {
/*  882:1171 */         if (this.m_undoStack.size() > 0)
/*  883:     */         {
/*  884:1172 */           undoAction = (UndoAction)this.m_undoStack.get(this.m_undoStack.size() - 1);
/*  885:1173 */           SetPositionAction posAction = (SetPositionAction)undoAction;
/*  886:1174 */           if (posAction.m_nTargetNode == iNode)
/*  887:     */           {
/*  888:1175 */             isUpdate = true;
/*  889:1176 */             posAction.setUndoPosition(nX, nY);
/*  890:     */           }
/*  891:     */         }
/*  892:     */       }
/*  893:     */       catch (Exception e) {}
/*  894:1182 */       if (!isUpdate) {
/*  895:1183 */         addUndoAction(new SetPositionAction(iNode, nX, nY));
/*  896:     */       }
/*  897:     */     }
/*  898:1186 */     this.m_nPositionX.add(iNode, Integer.valueOf(nX));
/*  899:1187 */     this.m_nPositionY.add(iNode, Integer.valueOf(nY));
/*  900:     */   }
/*  901:     */   
/*  902:     */   public void setPosition(int nNode, int nX, int nY, ArrayList<Integer> nodes)
/*  903:     */   {
/*  904:1200 */     int dX = nX - getPositionX(nNode);
/*  905:1201 */     int dY = nY - getPositionY(nNode);
/*  906:1203 */     if (this.m_bNeedsUndoAction)
/*  907:     */     {
/*  908:1204 */       boolean isUpdate = false;
/*  909:     */       try
/*  910:     */       {
/*  911:1206 */         UndoAction undoAction = null;
/*  912:1207 */         if (this.m_undoStack.size() > 0)
/*  913:     */         {
/*  914:1208 */           undoAction = (UndoAction)this.m_undoStack.get(this.m_undoStack.size() - 1);
/*  915:1209 */           SetGroupPositionAction posAction = (SetGroupPositionAction)undoAction;
/*  916:1210 */           isUpdate = true;
/*  917:1211 */           int iNode = 0;
/*  918:1212 */           while ((isUpdate) && (iNode < posAction.m_nodes.size()))
/*  919:     */           {
/*  920:1213 */             if (posAction.m_nodes.get(iNode) != nodes.get(iNode)) {
/*  921:1214 */               isUpdate = false;
/*  922:     */             }
/*  923:1216 */             iNode++;
/*  924:     */           }
/*  925:1218 */           if (isUpdate == true) {
/*  926:1219 */             posAction.setUndoPosition(dX, dY);
/*  927:     */           }
/*  928:     */         }
/*  929:     */       }
/*  930:     */       catch (Exception e) {}
/*  931:1225 */       if (!isUpdate) {
/*  932:1226 */         addUndoAction(new SetGroupPositionAction(nodes, dX, dY));
/*  933:     */       }
/*  934:     */     }
/*  935:1229 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/*  936:     */     {
/*  937:1230 */       nNode = ((Integer)nodes.get(iNode)).intValue();
/*  938:1231 */       this.m_nPositionX.set(nNode, Integer.valueOf(getPositionX(nNode) + dX));
/*  939:1232 */       this.m_nPositionY.set(nNode, Integer.valueOf(getPositionY(nNode) + dY));
/*  940:     */     }
/*  941:     */   }
/*  942:     */   
/*  943:     */   public void layoutGraph(ArrayList<Integer> nPosX, ArrayList<Integer> nPosY)
/*  944:     */   {
/*  945:1243 */     if (this.m_bNeedsUndoAction) {
/*  946:1244 */       addUndoAction(new LayoutGraphAction(nPosX, nPosY));
/*  947:     */     }
/*  948:1246 */     this.m_nPositionX = nPosX;
/*  949:1247 */     this.m_nPositionY = nPosY;
/*  950:     */   }
/*  951:     */   
/*  952:     */   public int getPositionX(int iNode)
/*  953:     */   {
/*  954:1256 */     return ((Integer)this.m_nPositionX.get(iNode)).intValue();
/*  955:     */   }
/*  956:     */   
/*  957:     */   public int getPositionY(int iNode)
/*  958:     */   {
/*  959:1265 */     return ((Integer)this.m_nPositionY.get(iNode)).intValue();
/*  960:     */   }
/*  961:     */   
/*  962:     */   public void alignLeft(ArrayList<Integer> nodes)
/*  963:     */   {
/*  964:1275 */     if (this.m_bNeedsUndoAction) {
/*  965:1276 */       addUndoAction(new alignLeftAction(nodes));
/*  966:     */     }
/*  967:1278 */     int nMinX = -1;
/*  968:1279 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/*  969:     */     {
/*  970:1280 */       int nX = getPositionX(((Integer)nodes.get(iNode)).intValue());
/*  971:1281 */       if ((nX < nMinX) || (iNode == 0)) {
/*  972:1282 */         nMinX = nX;
/*  973:     */       }
/*  974:     */     }
/*  975:1285 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/*  976:     */     {
/*  977:1286 */       int nNode = ((Integer)nodes.get(iNode)).intValue();
/*  978:1287 */       this.m_nPositionX.set(nNode, Integer.valueOf(nMinX));
/*  979:     */     }
/*  980:     */   }
/*  981:     */   
/*  982:     */   public void alignRight(ArrayList<Integer> nodes)
/*  983:     */   {
/*  984:1298 */     if (this.m_bNeedsUndoAction) {
/*  985:1299 */       addUndoAction(new alignRightAction(nodes));
/*  986:     */     }
/*  987:1301 */     int nMaxX = -1;
/*  988:1302 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/*  989:     */     {
/*  990:1303 */       int nX = getPositionX(((Integer)nodes.get(iNode)).intValue());
/*  991:1304 */       if ((nX > nMaxX) || (iNode == 0)) {
/*  992:1305 */         nMaxX = nX;
/*  993:     */       }
/*  994:     */     }
/*  995:1308 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/*  996:     */     {
/*  997:1309 */       int nNode = ((Integer)nodes.get(iNode)).intValue();
/*  998:1310 */       this.m_nPositionX.set(nNode, Integer.valueOf(nMaxX));
/*  999:     */     }
/* 1000:     */   }
/* 1001:     */   
/* 1002:     */   public void alignTop(ArrayList<Integer> nodes)
/* 1003:     */   {
/* 1004:1321 */     if (this.m_bNeedsUndoAction) {
/* 1005:1322 */       addUndoAction(new alignTopAction(nodes));
/* 1006:     */     }
/* 1007:1324 */     int nMinY = -1;
/* 1008:1325 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/* 1009:     */     {
/* 1010:1326 */       int nY = getPositionY(((Integer)nodes.get(iNode)).intValue());
/* 1011:1327 */       if ((nY < nMinY) || (iNode == 0)) {
/* 1012:1328 */         nMinY = nY;
/* 1013:     */       }
/* 1014:     */     }
/* 1015:1331 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/* 1016:     */     {
/* 1017:1332 */       int nNode = ((Integer)nodes.get(iNode)).intValue();
/* 1018:1333 */       this.m_nPositionY.set(nNode, Integer.valueOf(nMinY));
/* 1019:     */     }
/* 1020:     */   }
/* 1021:     */   
/* 1022:     */   public void alignBottom(ArrayList<Integer> nodes)
/* 1023:     */   {
/* 1024:1344 */     if (this.m_bNeedsUndoAction) {
/* 1025:1345 */       addUndoAction(new alignBottomAction(nodes));
/* 1026:     */     }
/* 1027:1347 */     int nMaxY = -1;
/* 1028:1348 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/* 1029:     */     {
/* 1030:1349 */       int nY = getPositionY(((Integer)nodes.get(iNode)).intValue());
/* 1031:1350 */       if ((nY > nMaxY) || (iNode == 0)) {
/* 1032:1351 */         nMaxY = nY;
/* 1033:     */       }
/* 1034:     */     }
/* 1035:1354 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/* 1036:     */     {
/* 1037:1355 */       int nNode = ((Integer)nodes.get(iNode)).intValue();
/* 1038:1356 */       this.m_nPositionY.set(nNode, Integer.valueOf(nMaxY));
/* 1039:     */     }
/* 1040:     */   }
/* 1041:     */   
/* 1042:     */   public void centerHorizontal(ArrayList<Integer> nodes)
/* 1043:     */   {
/* 1044:1367 */     if (this.m_bNeedsUndoAction) {
/* 1045:1368 */       addUndoAction(new centerHorizontalAction(nodes));
/* 1046:     */     }
/* 1047:1370 */     int nMinY = -1;
/* 1048:1371 */     int nMaxY = -1;
/* 1049:1372 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/* 1050:     */     {
/* 1051:1373 */       int nY = getPositionY(((Integer)nodes.get(iNode)).intValue());
/* 1052:1374 */       if ((nY < nMinY) || (iNode == 0)) {
/* 1053:1375 */         nMinY = nY;
/* 1054:     */       }
/* 1055:1377 */       if ((nY > nMaxY) || (iNode == 0)) {
/* 1056:1378 */         nMaxY = nY;
/* 1057:     */       }
/* 1058:     */     }
/* 1059:1381 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/* 1060:     */     {
/* 1061:1382 */       int nNode = ((Integer)nodes.get(iNode)).intValue();
/* 1062:1383 */       this.m_nPositionY.set(nNode, Integer.valueOf((nMinY + nMaxY) / 2));
/* 1063:     */     }
/* 1064:     */   }
/* 1065:     */   
/* 1066:     */   public void centerVertical(ArrayList<Integer> nodes)
/* 1067:     */   {
/* 1068:1394 */     if (this.m_bNeedsUndoAction) {
/* 1069:1395 */       addUndoAction(new centerVerticalAction(nodes));
/* 1070:     */     }
/* 1071:1397 */     int nMinX = -1;
/* 1072:1398 */     int nMaxX = -1;
/* 1073:1399 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/* 1074:     */     {
/* 1075:1400 */       int nX = getPositionX(((Integer)nodes.get(iNode)).intValue());
/* 1076:1401 */       if ((nX < nMinX) || (iNode == 0)) {
/* 1077:1402 */         nMinX = nX;
/* 1078:     */       }
/* 1079:1404 */       if ((nX > nMaxX) || (iNode == 0)) {
/* 1080:1405 */         nMaxX = nX;
/* 1081:     */       }
/* 1082:     */     }
/* 1083:1408 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/* 1084:     */     {
/* 1085:1409 */       int nNode = ((Integer)nodes.get(iNode)).intValue();
/* 1086:1410 */       this.m_nPositionX.set(nNode, Integer.valueOf((nMinX + nMaxX) / 2));
/* 1087:     */     }
/* 1088:     */   }
/* 1089:     */   
/* 1090:     */   public void spaceHorizontal(ArrayList<Integer> nodes)
/* 1091:     */   {
/* 1092:1421 */     if (this.m_bNeedsUndoAction) {
/* 1093:1422 */       addUndoAction(new spaceHorizontalAction(nodes));
/* 1094:     */     }
/* 1095:1424 */     int nMinX = -1;
/* 1096:1425 */     int nMaxX = -1;
/* 1097:1426 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/* 1098:     */     {
/* 1099:1427 */       int nX = getPositionX(((Integer)nodes.get(iNode)).intValue());
/* 1100:1428 */       if ((nX < nMinX) || (iNode == 0)) {
/* 1101:1429 */         nMinX = nX;
/* 1102:     */       }
/* 1103:1431 */       if ((nX > nMaxX) || (iNode == 0)) {
/* 1104:1432 */         nMaxX = nX;
/* 1105:     */       }
/* 1106:     */     }
/* 1107:1435 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/* 1108:     */     {
/* 1109:1436 */       int nNode = ((Integer)nodes.get(iNode)).intValue();
/* 1110:1437 */       this.m_nPositionX.set(nNode, Integer.valueOf((int)(nMinX + iNode * (nMaxX - nMinX) / (nodes.size() - 1.0D))));
/* 1111:     */     }
/* 1112:     */   }
/* 1113:     */   
/* 1114:     */   public void spaceVertical(ArrayList<Integer> nodes)
/* 1115:     */   {
/* 1116:1449 */     if (this.m_bNeedsUndoAction) {
/* 1117:1450 */       addUndoAction(new spaceVerticalAction(nodes));
/* 1118:     */     }
/* 1119:1452 */     int nMinY = -1;
/* 1120:1453 */     int nMaxY = -1;
/* 1121:1454 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/* 1122:     */     {
/* 1123:1455 */       int nY = getPositionY(((Integer)nodes.get(iNode)).intValue());
/* 1124:1456 */       if ((nY < nMinY) || (iNode == 0)) {
/* 1125:1457 */         nMinY = nY;
/* 1126:     */       }
/* 1127:1459 */       if ((nY > nMaxY) || (iNode == 0)) {
/* 1128:1460 */         nMaxY = nY;
/* 1129:     */       }
/* 1130:     */     }
/* 1131:1463 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/* 1132:     */     {
/* 1133:1464 */       int nNode = ((Integer)nodes.get(iNode)).intValue();
/* 1134:1465 */       this.m_nPositionY.set(nNode, Integer.valueOf((int)(nMinY + iNode * (nMaxY - nMinY) / (nodes.size() - 1.0D))));
/* 1135:     */     }
/* 1136:     */   }
/* 1137:     */   
/* 1138:     */   void replaceAtt(int nTargetNode, String sName, ArrayList<String> values)
/* 1139:     */   {
/* 1140:1478 */     Attribute newAtt = new Attribute(sName, values);
/* 1141:1479 */     if (this.m_Instances.classIndex() == nTargetNode)
/* 1142:     */     {
/* 1143:1480 */       this.m_Instances.setClassIndex(-1);
/* 1144:     */       
/* 1145:     */ 
/* 1146:     */ 
/* 1147:     */ 
/* 1148:     */ 
/* 1149:     */ 
/* 1150:1487 */       this.m_Instances.deleteAttributeAt(nTargetNode);
/* 1151:1488 */       this.m_Instances.insertAttributeAt(newAtt, nTargetNode);
/* 1152:1489 */       this.m_Instances.setClassIndex(nTargetNode);
/* 1153:     */     }
/* 1154:     */     else
/* 1155:     */     {
/* 1156:1495 */       this.m_Instances.deleteAttributeAt(nTargetNode);
/* 1157:1496 */       this.m_Instances.insertAttributeAt(newAtt, nTargetNode);
/* 1158:     */     }
/* 1159:     */   }
/* 1160:     */   
/* 1161:     */   public double[] getMargin(int iNode)
/* 1162:     */   {
/* 1163:1506 */     return (double[])this.m_fMarginP.get(iNode);
/* 1164:     */   }
/* 1165:     */   
/* 1166:     */   public void setMargin(int iNode, double[] fMarginP)
/* 1167:     */   {
/* 1168:1516 */     this.m_fMarginP.set(iNode, fMarginP);
/* 1169:     */   }
/* 1170:     */   
/* 1171:     */   public int getEvidence(int iNode)
/* 1172:     */   {
/* 1173:1526 */     return ((Integer)this.m_nEvidence.get(iNode)).intValue();
/* 1174:     */   }
/* 1175:     */   
/* 1176:     */   public void setEvidence(int iNode, int iValue)
/* 1177:     */   {
/* 1178:1537 */     this.m_nEvidence.set(iNode, Integer.valueOf(iValue));
/* 1179:     */   }
/* 1180:     */   
/* 1181:     */   public ArrayList<Integer> getChildren(int nTargetNode)
/* 1182:     */   {
/* 1183:1546 */     ArrayList<Integer> children = new ArrayList();
/* 1184:1547 */     for (int iNode = 0; iNode < getNrOfNodes(); iNode++) {
/* 1185:1548 */       if (this.m_ParentSets[iNode].contains(nTargetNode)) {
/* 1186:1549 */         children.add(Integer.valueOf(iNode));
/* 1187:     */       }
/* 1188:     */     }
/* 1189:1552 */     return children;
/* 1190:     */   }
/* 1191:     */   
/* 1192:     */   public String toXMLBIF03()
/* 1193:     */   {
/* 1194:1560 */     if (this.m_Instances == null) {
/* 1195:1561 */       return "<!--No model built yet-->";
/* 1196:     */     }
/* 1197:1564 */     StringBuffer text = new StringBuffer();
/* 1198:1565 */     text.append(getBIFHeader());
/* 1199:1566 */     text.append("\n");
/* 1200:1567 */     text.append("\n");
/* 1201:1568 */     text.append("<BIF VERSION=\"0.3\">\n");
/* 1202:1569 */     text.append("<NETWORK>\n");
/* 1203:1570 */     text.append("<NAME>" + XMLNormalize(this.m_Instances.relationName()) + "</NAME>\n");
/* 1204:1572 */     for (int iAttribute = 0; iAttribute < this.m_Instances.numAttributes(); iAttribute++)
/* 1205:     */     {
/* 1206:1573 */       text.append("<VARIABLE TYPE=\"nature\">\n");
/* 1207:1574 */       text.append("<NAME>" + XMLNormalize(this.m_Instances.attribute(iAttribute).name()) + "</NAME>\n");
/* 1208:1576 */       for (int iValue = 0; iValue < this.m_Instances.attribute(iAttribute).numValues(); iValue++) {
/* 1209:1578 */         text.append("<OUTCOME>" + XMLNormalize(this.m_Instances.attribute(iAttribute).value(iValue)) + "</OUTCOME>\n");
/* 1210:     */       }
/* 1211:1582 */       text.append("<PROPERTY>position = (" + getPositionX(iAttribute) + "," + getPositionY(iAttribute) + ")</PROPERTY>\n");
/* 1212:     */       
/* 1213:1584 */       text.append("</VARIABLE>\n");
/* 1214:     */     }
/* 1215:1587 */     for (int iAttribute = 0; iAttribute < this.m_Instances.numAttributes(); iAttribute++)
/* 1216:     */     {
/* 1217:1588 */       text.append("<DEFINITION>\n");
/* 1218:1589 */       text.append("<FOR>" + XMLNormalize(this.m_Instances.attribute(iAttribute).name()) + "</FOR>\n");
/* 1219:1591 */       for (int iParent = 0; iParent < this.m_ParentSets[iAttribute].getNrOfParents(); iParent++) {
/* 1220:1592 */         text.append("<GIVEN>" + XMLNormalize(this.m_Instances.attribute(this.m_ParentSets[iAttribute].getParent(iParent)).name()) + "</GIVEN>\n");
/* 1221:     */       }
/* 1222:1598 */       text.append("<TABLE>\n");
/* 1223:1599 */       for (int iParent = 0; iParent < this.m_ParentSets[iAttribute].getCardinalityOfParents(); iParent++)
/* 1224:     */       {
/* 1225:1601 */         for (int iValue = 0; iValue < this.m_Instances.attribute(iAttribute).numValues(); iValue++)
/* 1226:     */         {
/* 1227:1603 */           text.append(this.m_Distributions[iAttribute][iParent].getProbability(iValue));
/* 1228:     */           
/* 1229:1605 */           text.append(' ');
/* 1230:     */         }
/* 1231:1607 */         text.append('\n');
/* 1232:     */       }
/* 1233:1609 */       text.append("</TABLE>\n");
/* 1234:1610 */       text.append("</DEFINITION>\n");
/* 1235:     */     }
/* 1236:1612 */     text.append("</NETWORK>\n");
/* 1237:1613 */     text.append("</BIF>\n");
/* 1238:1614 */     return text.toString();
/* 1239:     */   }
/* 1240:     */   
/* 1241:     */   public String toXMLBIF03(ArrayList<Integer> nodes)
/* 1242:     */   {
/* 1243:1623 */     StringBuffer text = new StringBuffer();
/* 1244:1624 */     text.append(getBIFHeader());
/* 1245:1625 */     text.append("\n");
/* 1246:1626 */     text.append("\n");
/* 1247:1627 */     text.append("<BIF VERSION=\"0.3\">\n");
/* 1248:1628 */     text.append("<NETWORK>\n");
/* 1249:1629 */     text.append("<NAME>" + XMLNormalize(this.m_Instances.relationName()) + "</NAME>\n");
/* 1250:1631 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/* 1251:     */     {
/* 1252:1632 */       int nNode = ((Integer)nodes.get(iNode)).intValue();
/* 1253:1633 */       text.append("<VARIABLE TYPE=\"nature\">\n");
/* 1254:1634 */       text.append("<NAME>" + XMLNormalize(this.m_Instances.attribute(nNode).name()) + "</NAME>\n");
/* 1255:1636 */       for (int iValue = 0; iValue < this.m_Instances.attribute(nNode).numValues(); iValue++) {
/* 1256:1637 */         text.append("<OUTCOME>" + XMLNormalize(this.m_Instances.attribute(nNode).value(iValue)) + "</OUTCOME>\n");
/* 1257:     */       }
/* 1258:1641 */       text.append("<PROPERTY>position = (" + getPositionX(nNode) + "," + getPositionY(nNode) + ")</PROPERTY>\n");
/* 1259:     */       
/* 1260:1643 */       text.append("</VARIABLE>\n");
/* 1261:     */     }
/* 1262:1646 */     for (int iNode = 0; iNode < nodes.size(); iNode++)
/* 1263:     */     {
/* 1264:1647 */       int nNode = ((Integer)nodes.get(iNode)).intValue();
/* 1265:1648 */       text.append("<DEFINITION>\n");
/* 1266:1649 */       text.append("<FOR>" + XMLNormalize(this.m_Instances.attribute(nNode).name()) + "</FOR>\n");
/* 1267:1651 */       for (int iParent = 0; iParent < this.m_ParentSets[nNode].getNrOfParents(); iParent++) {
/* 1268:1652 */         text.append("<GIVEN>" + XMLNormalize(this.m_Instances.attribute(this.m_ParentSets[nNode].getParent(iParent)).name()) + "</GIVEN>\n");
/* 1269:     */       }
/* 1270:1656 */       text.append("<TABLE>\n");
/* 1271:1657 */       for (int iParent = 0; iParent < this.m_ParentSets[nNode].getCardinalityOfParents(); iParent++)
/* 1272:     */       {
/* 1273:1659 */         for (int iValue = 0; iValue < this.m_Instances.attribute(nNode).numValues(); iValue++)
/* 1274:     */         {
/* 1275:1660 */           text.append(this.m_Distributions[nNode][iParent].getProbability(iValue));
/* 1276:1661 */           text.append(' ');
/* 1277:     */         }
/* 1278:1663 */         text.append('\n');
/* 1279:     */       }
/* 1280:1665 */       text.append("</TABLE>\n");
/* 1281:1666 */       text.append("</DEFINITION>\n");
/* 1282:     */     }
/* 1283:1668 */     text.append("</NETWORK>\n");
/* 1284:1669 */     text.append("</BIF>\n");
/* 1285:1670 */     return text.toString();
/* 1286:     */   }
/* 1287:     */   
/* 1288:1674 */   ArrayList<UndoAction> m_undoStack = new ArrayList();
/* 1289:1677 */   int m_nCurrentEditAction = -1;
/* 1290:1680 */   int m_nSavedPointer = -1;
/* 1291:1686 */   boolean m_bNeedsUndoAction = true;
/* 1292:     */   
/* 1293:     */   public boolean canUndo()
/* 1294:     */   {
/* 1295:1690 */     return this.m_nCurrentEditAction >= 0;
/* 1296:     */   }
/* 1297:     */   
/* 1298:     */   public boolean canRedo()
/* 1299:     */   {
/* 1300:1695 */     return this.m_nCurrentEditAction < this.m_undoStack.size() - 1;
/* 1301:     */   }
/* 1302:     */   
/* 1303:     */   public boolean isChanged()
/* 1304:     */   {
/* 1305:1703 */     return this.m_nCurrentEditAction != this.m_nSavedPointer;
/* 1306:     */   }
/* 1307:     */   
/* 1308:     */   public void isSaved()
/* 1309:     */   {
/* 1310:1708 */     this.m_nSavedPointer = this.m_nCurrentEditAction;
/* 1311:     */   }
/* 1312:     */   
/* 1313:     */   public String lastActionMsg()
/* 1314:     */   {
/* 1315:1713 */     if (this.m_undoStack.size() == 0) {
/* 1316:1714 */       return "";
/* 1317:     */     }
/* 1318:1716 */     return ((UndoAction)this.m_undoStack.get(this.m_undoStack.size() - 1)).getRedoMsg();
/* 1319:     */   }
/* 1320:     */   
/* 1321:     */   public String undo()
/* 1322:     */   {
/* 1323:1724 */     if (!canUndo()) {
/* 1324:1725 */       return "";
/* 1325:     */     }
/* 1326:1727 */     UndoAction undoAction = (UndoAction)this.m_undoStack.get(this.m_nCurrentEditAction);
/* 1327:1728 */     this.m_bNeedsUndoAction = false;
/* 1328:1729 */     undoAction.undo();
/* 1329:1730 */     this.m_bNeedsUndoAction = true;
/* 1330:1731 */     this.m_nCurrentEditAction -= 1;
/* 1331:     */     
/* 1332:     */ 
/* 1333:     */ 
/* 1334:     */ 
/* 1335:     */ 
/* 1336:     */ 
/* 1337:     */ 
/* 1338:     */ 
/* 1339:     */ 
/* 1340:     */ 
/* 1341:     */ 
/* 1342:     */ 
/* 1343:     */ 
/* 1344:1745 */     return undoAction.getUndoMsg();
/* 1345:     */   }
/* 1346:     */   
/* 1347:     */   public String redo()
/* 1348:     */   {
/* 1349:1753 */     if (!canRedo()) {
/* 1350:1754 */       return "";
/* 1351:     */     }
/* 1352:1756 */     this.m_nCurrentEditAction += 1;
/* 1353:1757 */     UndoAction undoAction = (UndoAction)this.m_undoStack.get(this.m_nCurrentEditAction);
/* 1354:1758 */     this.m_bNeedsUndoAction = false;
/* 1355:1759 */     undoAction.redo();
/* 1356:1760 */     this.m_bNeedsUndoAction = true;
/* 1357:     */     
/* 1358:     */ 
/* 1359:     */ 
/* 1360:     */ 
/* 1361:     */ 
/* 1362:     */ 
/* 1363:     */ 
/* 1364:     */ 
/* 1365:     */ 
/* 1366:     */ 
/* 1367:     */ 
/* 1368:     */ 
/* 1369:     */ 
/* 1370:1774 */     return undoAction.getRedoMsg();
/* 1371:     */   }
/* 1372:     */   
/* 1373:     */   void addUndoAction(UndoAction action)
/* 1374:     */   {
/* 1375:1783 */     int iAction = this.m_undoStack.size() - 1;
/* 1376:1784 */     while (iAction > this.m_nCurrentEditAction) {
/* 1377:1785 */       this.m_undoStack.remove(iAction--);
/* 1378:     */     }
/* 1379:1787 */     if (this.m_nSavedPointer > this.m_nCurrentEditAction) {
/* 1380:1788 */       this.m_nSavedPointer = -2;
/* 1381:     */     }
/* 1382:1790 */     this.m_undoStack.add(action);
/* 1383:     */     
/* 1384:1792 */     this.m_nCurrentEditAction += 1;
/* 1385:     */   }
/* 1386:     */   
/* 1387:     */   public void clearUndoStack()
/* 1388:     */   {
/* 1389:1797 */     this.m_undoStack = new ArrayList();
/* 1390:     */     
/* 1391:1799 */     this.m_nCurrentEditAction = -1;
/* 1392:1800 */     this.m_nSavedPointer = -1;
/* 1393:     */   }
/* 1394:     */   
/* 1395:     */   class UndoAction
/* 1396:     */     implements Serializable
/* 1397:     */   {
/* 1398:     */     static final long serialVersionUID = 1L;
/* 1399:     */     
/* 1400:     */     UndoAction() {}
/* 1401:     */     
/* 1402:     */     public void undo() {}
/* 1403:     */     
/* 1404:     */     public void redo() {}
/* 1405:     */     
/* 1406:     */     public String getUndoMsg()
/* 1407:     */     {
/* 1408:1818 */       return getMsg();
/* 1409:     */     }
/* 1410:     */     
/* 1411:     */     public String getRedoMsg()
/* 1412:     */     {
/* 1413:1822 */       return getMsg();
/* 1414:     */     }
/* 1415:     */     
/* 1416:     */     String getMsg()
/* 1417:     */     {
/* 1418:1826 */       String sStr = toString();
/* 1419:1827 */       int iStart = sStr.indexOf('$');
/* 1420:1828 */       int iEnd = sStr.indexOf('@');
/* 1421:1829 */       StringBuffer sBuffer = new StringBuffer();
/* 1422:1830 */       for (int i = iStart + 1; i < iEnd; i++)
/* 1423:     */       {
/* 1424:1831 */         char c = sStr.charAt(i);
/* 1425:1832 */         if (Character.isUpperCase(c)) {
/* 1426:1833 */           sBuffer.append(' ');
/* 1427:     */         }
/* 1428:1835 */         sBuffer.append(sStr.charAt(i));
/* 1429:     */       }
/* 1430:1837 */       return sBuffer.toString();
/* 1431:     */     }
/* 1432:     */   }
/* 1433:     */   
/* 1434:     */   class AddNodeAction
/* 1435:     */     extends EditableBayesNet.UndoAction
/* 1436:     */   {
/* 1437:     */     static final long serialVersionUID = 1L;
/* 1438:     */     String m_sName;
/* 1439:     */     int m_nPosX;
/* 1440:     */     int m_nPosY;
/* 1441:     */     int m_nCardinality;
/* 1442:     */     
/* 1443:     */     AddNodeAction(String sName, int nCardinality, int nPosX, int nPosY)
/* 1444:     */     {
/* 1445:1850 */       super();
/* 1446:1851 */       this.m_sName = sName;
/* 1447:1852 */       this.m_nCardinality = nCardinality;
/* 1448:1853 */       this.m_nPosX = nPosX;
/* 1449:1854 */       this.m_nPosY = nPosY;
/* 1450:     */     }
/* 1451:     */     
/* 1452:     */     public void undo()
/* 1453:     */     {
/* 1454:     */       try
/* 1455:     */       {
/* 1456:1860 */         EditableBayesNet.this.deleteNode(EditableBayesNet.this.getNrOfNodes() - 1);
/* 1457:     */       }
/* 1458:     */       catch (Exception e)
/* 1459:     */       {
/* 1460:1862 */         e.printStackTrace();
/* 1461:     */       }
/* 1462:     */     }
/* 1463:     */     
/* 1464:     */     public void redo()
/* 1465:     */     {
/* 1466:     */       try
/* 1467:     */       {
/* 1468:1869 */         EditableBayesNet.this.addNode(this.m_sName, this.m_nCardinality, this.m_nPosX, this.m_nPosY);
/* 1469:     */       }
/* 1470:     */       catch (Exception e)
/* 1471:     */       {
/* 1472:1871 */         e.printStackTrace();
/* 1473:     */       }
/* 1474:     */     }
/* 1475:     */   }
/* 1476:     */   
/* 1477:     */   class DeleteNodeAction
/* 1478:     */     extends EditableBayesNet.UndoAction
/* 1479:     */   {
/* 1480:     */     static final long serialVersionUID = 1L;
/* 1481:     */     int m_nTargetNode;
/* 1482:     */     Attribute m_att;
/* 1483:     */     Estimator[] m_CPT;
/* 1484:     */     ParentSet m_ParentSet;
/* 1485:     */     ArrayList<EditableBayesNet.DeleteArcAction> m_deleteArcActions;
/* 1486:     */     int m_nPosX;
/* 1487:     */     int m_nPosY;
/* 1488:     */     
/* 1489:     */     DeleteNodeAction(int nTargetNode)
/* 1490:     */     {
/* 1491:1893 */       super();
/* 1492:1894 */       this.m_nTargetNode = nTargetNode;
/* 1493:1895 */       this.m_att = EditableBayesNet.this.m_Instances.attribute(nTargetNode);
/* 1494:     */       try
/* 1495:     */       {
/* 1496:1897 */         so = new SerializedObject(EditableBayesNet.this.m_Distributions[nTargetNode]);
/* 1497:1898 */         this.m_CPT = ((Estimator[])so.getObject());
/* 1498:     */         
/* 1499:1900 */         so = new SerializedObject(EditableBayesNet.this.m_ParentSets[nTargetNode]);
/* 1500:1901 */         this.m_ParentSet = ((ParentSet)so.getObject());
/* 1501:     */       }
/* 1502:     */       catch (Exception e)
/* 1503:     */       {
/* 1504:     */         SerializedObject so;
/* 1505:1903 */         e.printStackTrace();
/* 1506:     */       }
/* 1507:1905 */       this.m_deleteArcActions = new ArrayList();
/* 1508:1906 */       for (int iNode = 0; iNode < EditableBayesNet.this.getNrOfNodes(); iNode++) {
/* 1509:1907 */         if (EditableBayesNet.this.m_ParentSets[iNode].contains(nTargetNode)) {
/* 1510:1908 */           this.m_deleteArcActions.add(new EditableBayesNet.DeleteArcAction(EditableBayesNet.this, nTargetNode, iNode));
/* 1511:     */         }
/* 1512:     */       }
/* 1513:1911 */       this.m_nPosX = EditableBayesNet.this.getPositionX(this.m_nTargetNode);
/* 1514:1912 */       this.m_nPosY = EditableBayesNet.this.getPositionY(this.m_nTargetNode);
/* 1515:     */     }
/* 1516:     */     
/* 1517:     */     public void undo()
/* 1518:     */     {
/* 1519:     */       try
/* 1520:     */       {
/* 1521:1918 */         EditableBayesNet.this.m_Instances.insertAttributeAt(this.m_att, this.m_nTargetNode);
/* 1522:1919 */         int nAtts = EditableBayesNet.this.m_Instances.numAttributes();
/* 1523:     */         
/* 1524:1921 */         ParentSet[] parentSets = new ParentSet[nAtts];
/* 1525:1922 */         int nX = 0;
/* 1526:1923 */         for (int iParentSet = 0; iParentSet < nAtts; iParentSet++) {
/* 1527:1924 */           if (iParentSet == this.m_nTargetNode)
/* 1528:     */           {
/* 1529:1925 */             SerializedObject so = new SerializedObject(this.m_ParentSet);
/* 1530:1926 */             parentSets[iParentSet] = ((ParentSet)so.getObject());
/* 1531:1927 */             nX = 1;
/* 1532:     */           }
/* 1533:     */           else
/* 1534:     */           {
/* 1535:1929 */             parentSets[iParentSet] = EditableBayesNet.this.m_ParentSets[(iParentSet - nX)];
/* 1536:1930 */             for (int iParent = 0; iParent < parentSets[iParentSet].getNrOfParents(); iParent++)
/* 1537:     */             {
/* 1538:1932 */               int nParent = parentSets[iParentSet].getParent(iParent);
/* 1539:1933 */               if (nParent >= this.m_nTargetNode) {
/* 1540:1934 */                 parentSets[iParentSet].SetParent(iParent, nParent + 1);
/* 1541:     */               }
/* 1542:     */             }
/* 1543:     */           }
/* 1544:     */         }
/* 1545:1939 */         EditableBayesNet.this.m_ParentSets = parentSets;
/* 1546:     */         
/* 1547:1941 */         Estimator[][] distributions = new Estimator[nAtts][];
/* 1548:1942 */         nX = 0;
/* 1549:1943 */         for (int iNode = 0; iNode < nAtts; iNode++) {
/* 1550:1944 */           if (iNode == this.m_nTargetNode)
/* 1551:     */           {
/* 1552:1945 */             SerializedObject so = new SerializedObject(this.m_CPT);
/* 1553:1946 */             distributions[iNode] = ((Estimator[])(Estimator[])so.getObject());
/* 1554:1947 */             nX = 1;
/* 1555:     */           }
/* 1556:     */           else
/* 1557:     */           {
/* 1558:1949 */             distributions[iNode] = EditableBayesNet.this.m_Distributions[(iNode - nX)];
/* 1559:     */           }
/* 1560:     */         }
/* 1561:1952 */         EditableBayesNet.this.m_Distributions = distributions;
/* 1562:1954 */         for (int deletedArc = 0; deletedArc < this.m_deleteArcActions.size(); deletedArc++)
/* 1563:     */         {
/* 1564:1955 */           EditableBayesNet.DeleteArcAction action = (EditableBayesNet.DeleteArcAction)this.m_deleteArcActions.get(deletedArc);
/* 1565:1956 */           action.undo();
/* 1566:     */         }
/* 1567:1958 */         EditableBayesNet.this.m_nPositionX.add(this.m_nTargetNode, Integer.valueOf(this.m_nPosX));
/* 1568:1959 */         EditableBayesNet.this.m_nPositionY.add(this.m_nTargetNode, Integer.valueOf(this.m_nPosY));
/* 1569:1960 */         EditableBayesNet.this.m_nEvidence.add(this.m_nTargetNode, Integer.valueOf(-1));
/* 1570:1961 */         EditableBayesNet.this.m_fMarginP.add(this.m_nTargetNode, new double[EditableBayesNet.this.getCardinality(this.m_nTargetNode)]);
/* 1571:     */       }
/* 1572:     */       catch (Exception e)
/* 1573:     */       {
/* 1574:1964 */         e.printStackTrace();
/* 1575:     */       }
/* 1576:     */     }
/* 1577:     */     
/* 1578:     */     public void redo()
/* 1579:     */     {
/* 1580:     */       try
/* 1581:     */       {
/* 1582:1971 */         EditableBayesNet.this.deleteNode(this.m_nTargetNode);
/* 1583:     */       }
/* 1584:     */       catch (Exception e)
/* 1585:     */       {
/* 1586:1973 */         e.printStackTrace();
/* 1587:     */       }
/* 1588:     */     }
/* 1589:     */   }
/* 1590:     */   
/* 1591:     */   class DeleteSelectionAction
/* 1592:     */     extends EditableBayesNet.UndoAction
/* 1593:     */   {
/* 1594:     */     static final long serialVersionUID = 1L;
/* 1595:     */     ArrayList<Integer> m_nodes;
/* 1596:     */     Attribute[] m_att;
/* 1597:     */     Estimator[][] m_CPT;
/* 1598:     */     ParentSet[] m_ParentSet;
/* 1599:     */     ArrayList<EditableBayesNet.DeleteArcAction> m_deleteArcActions;
/* 1600:     */     int[] m_nPosX;
/* 1601:     */     int[] m_nPosY;
/* 1602:     */     
/* 1603:     */     public DeleteSelectionAction()
/* 1604:     */     {
/* 1605:1995 */       super();
/* 1606:1996 */       this.m_nodes = new ArrayList();
/* 1607:1997 */       int nNodes = nodes.size();
/* 1608:1998 */       this.m_att = new Attribute[nNodes];
/* 1609:1999 */       this.m_CPT = new Estimator[nNodes][];
/* 1610:2000 */       this.m_ParentSet = new ParentSet[nNodes];
/* 1611:2001 */       this.m_nPosX = new int[nNodes];
/* 1612:2002 */       this.m_nPosY = new int[nNodes];
/* 1613:2003 */       this.m_deleteArcActions = new ArrayList();
/* 1614:2004 */       for (int iNode = 0; iNode < nodes.size(); iNode++)
/* 1615:     */       {
/* 1616:2005 */         int nTargetNode = ((Integer)nodes.get(iNode)).intValue();
/* 1617:2006 */         this.m_nodes.add(Integer.valueOf(nTargetNode));
/* 1618:2007 */         this.m_att[iNode] = EditableBayesNet.this.m_Instances.attribute(nTargetNode);
/* 1619:     */         try
/* 1620:     */         {
/* 1621:2009 */           SerializedObject so = new SerializedObject(EditableBayesNet.this.m_Distributions[nTargetNode]);
/* 1622:     */           
/* 1623:2011 */           this.m_CPT[iNode] = ((Estimator[])(Estimator[])so.getObject());
/* 1624:     */           
/* 1625:2013 */           so = new SerializedObject(EditableBayesNet.this.m_ParentSets[nTargetNode]);
/* 1626:2014 */           this.m_ParentSet[iNode] = ((ParentSet)so.getObject());
/* 1627:     */         }
/* 1628:     */         catch (Exception e)
/* 1629:     */         {
/* 1630:2016 */           e.printStackTrace();
/* 1631:     */         }
/* 1632:2018 */         this.m_nPosX[iNode] = EditableBayesNet.this.getPositionX(nTargetNode);
/* 1633:2019 */         this.m_nPosY[iNode] = EditableBayesNet.this.getPositionY(nTargetNode);
/* 1634:2020 */         for (int iNode2 = 0; iNode2 < EditableBayesNet.this.getNrOfNodes(); iNode2++) {
/* 1635:2021 */           if ((!nodes.contains(Integer.valueOf(iNode2))) && (EditableBayesNet.this.m_ParentSets[iNode2].contains(nTargetNode))) {
/* 1636:2023 */             this.m_deleteArcActions.add(new EditableBayesNet.DeleteArcAction(EditableBayesNet.this, nTargetNode, iNode2));
/* 1637:     */           }
/* 1638:     */         }
/* 1639:     */       }
/* 1640:     */     }
/* 1641:     */     
/* 1642:     */     public void undo()
/* 1643:     */     {
/* 1644:     */       try
/* 1645:     */       {
/* 1646:2032 */         for (int iNode = 0; iNode < this.m_nodes.size(); iNode++)
/* 1647:     */         {
/* 1648:2033 */           int nTargetNode = ((Integer)this.m_nodes.get(iNode)).intValue();
/* 1649:2034 */           EditableBayesNet.this.m_Instances.insertAttributeAt(this.m_att[iNode], nTargetNode);
/* 1650:     */         }
/* 1651:2036 */         int nAtts = EditableBayesNet.this.m_Instances.numAttributes();
/* 1652:     */         
/* 1653:2038 */         ParentSet[] parentSets = new ParentSet[nAtts];
/* 1654:2039 */         int[] offset = new int[nAtts];
/* 1655:2040 */         for (int iNode = 0; iNode < nAtts; iNode++) {
/* 1656:2041 */           offset[iNode] = iNode;
/* 1657:     */         }
/* 1658:2043 */         for (int iNode = this.m_nodes.size() - 1; iNode >= 0; iNode--)
/* 1659:     */         {
/* 1660:2044 */           int nTargetNode = ((Integer)this.m_nodes.get(iNode)).intValue();
/* 1661:2045 */           for (int i = nTargetNode; i < nAtts - 1; i++) {
/* 1662:2046 */             offset[i] = offset[(i + 1)];
/* 1663:     */           }
/* 1664:     */         }
/* 1665:2050 */         int iTargetNode = 0;
/* 1666:2051 */         for (int iParentSet = 0; iParentSet < nAtts; iParentSet++) {
/* 1667:2052 */           if ((iTargetNode < this.m_nodes.size()) && (((Integer)this.m_nodes.get(iTargetNode)).intValue() == iParentSet))
/* 1668:     */           {
/* 1669:2054 */             SerializedObject so = new SerializedObject(this.m_ParentSet[iTargetNode]);
/* 1670:2055 */             parentSets[iParentSet] = ((ParentSet)so.getObject());
/* 1671:2056 */             iTargetNode++;
/* 1672:     */           }
/* 1673:     */           else
/* 1674:     */           {
/* 1675:2058 */             parentSets[iParentSet] = EditableBayesNet.this.m_ParentSets[(iParentSet - iTargetNode)];
/* 1676:2059 */             for (int iParent = 0; iParent < parentSets[iParentSet].getNrOfParents(); iParent++)
/* 1677:     */             {
/* 1678:2061 */               int nParent = parentSets[iParentSet].getParent(iParent);
/* 1679:2062 */               parentSets[iParentSet].SetParent(iParent, offset[nParent]);
/* 1680:     */             }
/* 1681:     */           }
/* 1682:     */         }
/* 1683:2066 */         EditableBayesNet.this.m_ParentSets = parentSets;
/* 1684:     */         
/* 1685:2068 */         Estimator[][] distributions = new Estimator[nAtts][];
/* 1686:2069 */         iTargetNode = 0;
/* 1687:2070 */         for (int iNode = 0; iNode < nAtts; iNode++) {
/* 1688:2071 */           if ((iTargetNode < this.m_nodes.size()) && (((Integer)this.m_nodes.get(iTargetNode)).intValue() == iNode))
/* 1689:     */           {
/* 1690:2072 */             SerializedObject so = new SerializedObject(this.m_CPT[iTargetNode]);
/* 1691:2073 */             distributions[iNode] = ((Estimator[])(Estimator[])so.getObject());
/* 1692:2074 */             iTargetNode++;
/* 1693:     */           }
/* 1694:     */           else
/* 1695:     */           {
/* 1696:2076 */             distributions[iNode] = EditableBayesNet.this.m_Distributions[(iNode - iTargetNode)];
/* 1697:     */           }
/* 1698:     */         }
/* 1699:2079 */         EditableBayesNet.this.m_Distributions = distributions;
/* 1700:2081 */         for (int iNode = 0; iNode < this.m_nodes.size(); iNode++)
/* 1701:     */         {
/* 1702:2082 */           int nTargetNode = ((Integer)this.m_nodes.get(iNode)).intValue();
/* 1703:2083 */           EditableBayesNet.this.m_nPositionX.add(nTargetNode, Integer.valueOf(this.m_nPosX[iNode]));
/* 1704:2084 */           EditableBayesNet.this.m_nPositionY.add(nTargetNode, Integer.valueOf(this.m_nPosY[iNode]));
/* 1705:2085 */           EditableBayesNet.this.m_nEvidence.add(nTargetNode, Integer.valueOf(-1));
/* 1706:2086 */           EditableBayesNet.this.m_fMarginP.add(nTargetNode, new double[EditableBayesNet.this.getCardinality(nTargetNode)]);
/* 1707:     */         }
/* 1708:2088 */         for (int deletedArc = 0; deletedArc < this.m_deleteArcActions.size(); deletedArc++)
/* 1709:     */         {
/* 1710:2089 */           EditableBayesNet.DeleteArcAction action = (EditableBayesNet.DeleteArcAction)this.m_deleteArcActions.get(deletedArc);
/* 1711:2090 */           action.undo();
/* 1712:     */         }
/* 1713:     */       }
/* 1714:     */       catch (Exception e)
/* 1715:     */       {
/* 1716:2093 */         e.printStackTrace();
/* 1717:     */       }
/* 1718:     */     }
/* 1719:     */     
/* 1720:     */     public void redo()
/* 1721:     */     {
/* 1722:     */       try
/* 1723:     */       {
/* 1724:2100 */         for (int iNode = this.m_nodes.size() - 1; iNode >= 0; iNode--)
/* 1725:     */         {
/* 1726:2101 */           int nNode = ((Integer)this.m_nodes.get(iNode)).intValue();
/* 1727:2102 */           EditableBayesNet.this.deleteNode(nNode);
/* 1728:     */         }
/* 1729:     */       }
/* 1730:     */       catch (Exception e)
/* 1731:     */       {
/* 1732:2105 */         e.printStackTrace();
/* 1733:     */       }
/* 1734:     */     }
/* 1735:     */   }
/* 1736:     */   
/* 1737:     */   class AddArcAction
/* 1738:     */     extends EditableBayesNet.UndoAction
/* 1739:     */   {
/* 1740:     */     static final long serialVersionUID = 1L;
/* 1741:     */     ArrayList<Integer> m_children;
/* 1742:     */     int m_nParent;
/* 1743:     */     Estimator[][] m_CPT;
/* 1744:     */     
/* 1745:     */     AddArcAction(int nParent, int nChild)
/* 1746:     */     {
/* 1747:2120 */       super();
/* 1748:     */       try
/* 1749:     */       {
/* 1750:2122 */         this.m_nParent = nParent;
/* 1751:2123 */         this.m_children = new ArrayList();
/* 1752:2124 */         this.m_children.add(Integer.valueOf(nChild));
/* 1753:     */         
/* 1754:2126 */         SerializedObject so = new SerializedObject(EditableBayesNet.this.m_Distributions[nChild]);
/* 1755:2127 */         this.m_CPT = new Estimator[1][];
/* 1756:2128 */         this.m_CPT[0] = ((Estimator[])(Estimator[])so.getObject());
/* 1757:     */       }
/* 1758:     */       catch (Exception e)
/* 1759:     */       {
/* 1760:2131 */         e.printStackTrace();
/* 1761:     */       }
/* 1762:     */     }
/* 1763:     */     
/* 1764:     */     AddArcAction(ArrayList<Integer> nParent)
/* 1765:     */     {
/* 1766:2135 */       super();
/* 1767:     */       try
/* 1768:     */       {
/* 1769:2137 */         this.m_nParent = nParent;
/* 1770:2138 */         this.m_children = new ArrayList();
/* 1771:2139 */         this.m_CPT = new Estimator[children.size()][];
/* 1772:2140 */         for (int iChild = 0; iChild < children.size(); iChild++)
/* 1773:     */         {
/* 1774:2141 */           int nChild = ((Integer)children.get(iChild)).intValue();
/* 1775:2142 */           this.m_children.add(Integer.valueOf(nChild));
/* 1776:2143 */           SerializedObject so = new SerializedObject(EditableBayesNet.this.m_Distributions[nChild]);
/* 1777:2144 */           this.m_CPT[iChild] = ((Estimator[])(Estimator[])so.getObject());
/* 1778:     */         }
/* 1779:     */       }
/* 1780:     */       catch (Exception e)
/* 1781:     */       {
/* 1782:2147 */         e.printStackTrace();
/* 1783:     */       }
/* 1784:     */     }
/* 1785:     */     
/* 1786:     */     public void undo()
/* 1787:     */     {
/* 1788:     */       try
/* 1789:     */       {
/* 1790:2154 */         for (int iChild = 0; iChild < this.m_children.size(); iChild++)
/* 1791:     */         {
/* 1792:2155 */           int nChild = ((Integer)this.m_children.get(iChild)).intValue();
/* 1793:2156 */           EditableBayesNet.this.deleteArc(this.m_nParent, nChild);
/* 1794:2157 */           SerializedObject so = new SerializedObject(this.m_CPT[iChild]);
/* 1795:2158 */           EditableBayesNet.this.m_Distributions[nChild] = ((Estimator[])(Estimator[])so.getObject());
/* 1796:     */         }
/* 1797:     */       }
/* 1798:     */       catch (Exception e)
/* 1799:     */       {
/* 1800:2161 */         e.printStackTrace();
/* 1801:     */       }
/* 1802:     */     }
/* 1803:     */     
/* 1804:     */     public void redo()
/* 1805:     */     {
/* 1806:     */       try
/* 1807:     */       {
/* 1808:2168 */         for (int iChild = 0; iChild < this.m_children.size(); iChild++)
/* 1809:     */         {
/* 1810:2169 */           int nChild = ((Integer)this.m_children.get(iChild)).intValue();
/* 1811:2170 */           EditableBayesNet.this.addArc(this.m_nParent, nChild);
/* 1812:     */         }
/* 1813:     */       }
/* 1814:     */       catch (Exception e)
/* 1815:     */       {
/* 1816:2173 */         e.printStackTrace();
/* 1817:     */       }
/* 1818:     */     }
/* 1819:     */   }
/* 1820:     */   
/* 1821:     */   class DeleteArcAction
/* 1822:     */     extends EditableBayesNet.UndoAction
/* 1823:     */   {
/* 1824:     */     static final long serialVersionUID = 1L;
/* 1825:     */     int[] m_nParents;
/* 1826:     */     int m_nChild;
/* 1827:     */     int m_nParent;
/* 1828:     */     Estimator[] m_CPT;
/* 1829:     */     
/* 1830:     */     DeleteArcAction(int nParent, int nChild)
/* 1831:     */     {
/* 1832:2186 */       super();
/* 1833:     */       try
/* 1834:     */       {
/* 1835:2188 */         this.m_nChild = nChild;
/* 1836:2189 */         this.m_nParent = nParent;
/* 1837:2190 */         this.m_nParents = new int[EditableBayesNet.this.getNrOfParents(nChild)];
/* 1838:2191 */         for (int iParent = 0; iParent < this.m_nParents.length; iParent++) {
/* 1839:2192 */           this.m_nParents[iParent] = EditableBayesNet.this.getParent(nChild, iParent);
/* 1840:     */         }
/* 1841:2194 */         SerializedObject so = new SerializedObject(EditableBayesNet.this.m_Distributions[nChild]);
/* 1842:2195 */         this.m_CPT = ((Estimator[])so.getObject());
/* 1843:     */       }
/* 1844:     */       catch (Exception e)
/* 1845:     */       {
/* 1846:2197 */         e.printStackTrace();
/* 1847:     */       }
/* 1848:     */     }
/* 1849:     */     
/* 1850:     */     public void undo()
/* 1851:     */     {
/* 1852:     */       try
/* 1853:     */       {
/* 1854:2204 */         SerializedObject so = new SerializedObject(this.m_CPT);
/* 1855:2205 */         EditableBayesNet.this.m_Distributions[this.m_nChild] = ((Estimator[])(Estimator[])so.getObject());
/* 1856:2206 */         ParentSet parentSet = new ParentSet();
/* 1857:2207 */         for (int m_nParent2 : this.m_nParents) {
/* 1858:2208 */           parentSet.addParent(m_nParent2, EditableBayesNet.this.m_Instances);
/* 1859:     */         }
/* 1860:2210 */         EditableBayesNet.this.m_ParentSets[this.m_nChild] = parentSet;
/* 1861:     */       }
/* 1862:     */       catch (Exception e)
/* 1863:     */       {
/* 1864:2212 */         e.printStackTrace();
/* 1865:     */       }
/* 1866:     */     }
/* 1867:     */     
/* 1868:     */     public void redo()
/* 1869:     */     {
/* 1870:     */       try
/* 1871:     */       {
/* 1872:2219 */         EditableBayesNet.this.deleteArc(this.m_nParent, this.m_nChild);
/* 1873:     */       }
/* 1874:     */       catch (Exception e)
/* 1875:     */       {
/* 1876:2221 */         e.printStackTrace();
/* 1877:     */       }
/* 1878:     */     }
/* 1879:     */   }
/* 1880:     */   
/* 1881:     */   class SetDistributionAction
/* 1882:     */     extends EditableBayesNet.UndoAction
/* 1883:     */   {
/* 1884:     */     static final long serialVersionUID = 1L;
/* 1885:     */     int m_nTargetNode;
/* 1886:     */     Estimator[] m_CPT;
/* 1887:     */     double[][] m_P;
/* 1888:     */     
/* 1889:     */     SetDistributionAction(int nTargetNode, double[][] P)
/* 1890:     */     {
/* 1891:2235 */       super();
/* 1892:     */       try
/* 1893:     */       {
/* 1894:2237 */         this.m_nTargetNode = nTargetNode;
/* 1895:2238 */         so = new SerializedObject(EditableBayesNet.this.m_Distributions[nTargetNode]);
/* 1896:2239 */         this.m_CPT = ((Estimator[])so.getObject());
/* 1897:     */         
/* 1898:2241 */         this.m_P = P;
/* 1899:     */       }
/* 1900:     */       catch (Exception e)
/* 1901:     */       {
/* 1902:     */         SerializedObject so;
/* 1903:2243 */         e.printStackTrace();
/* 1904:     */       }
/* 1905:     */     }
/* 1906:     */     
/* 1907:     */     public void undo()
/* 1908:     */     {
/* 1909:     */       try
/* 1910:     */       {
/* 1911:2250 */         SerializedObject so = new SerializedObject(this.m_CPT);
/* 1912:2251 */         EditableBayesNet.this.m_Distributions[this.m_nTargetNode] = ((Estimator[])(Estimator[])so.getObject());
/* 1913:     */       }
/* 1914:     */       catch (Exception e)
/* 1915:     */       {
/* 1916:2253 */         e.printStackTrace();
/* 1917:     */       }
/* 1918:     */     }
/* 1919:     */     
/* 1920:     */     public void redo()
/* 1921:     */     {
/* 1922:     */       try
/* 1923:     */       {
/* 1924:2260 */         EditableBayesNet.this.setDistribution(this.m_nTargetNode, this.m_P);
/* 1925:     */       }
/* 1926:     */       catch (Exception e)
/* 1927:     */       {
/* 1928:2262 */         e.printStackTrace();
/* 1929:     */       }
/* 1930:     */     }
/* 1931:     */     
/* 1932:     */     public String getUndoMsg()
/* 1933:     */     {
/* 1934:2268 */       return "Distribution of node " + EditableBayesNet.this.getNodeName(this.m_nTargetNode) + " changed";
/* 1935:     */     }
/* 1936:     */     
/* 1937:     */     public String getRedoMsg()
/* 1938:     */     {
/* 1939:2273 */       return "Distribution of node " + EditableBayesNet.this.getNodeName(this.m_nTargetNode) + " changed";
/* 1940:     */     }
/* 1941:     */   }
/* 1942:     */   
/* 1943:     */   class RenameAction
/* 1944:     */     extends EditableBayesNet.UndoAction
/* 1945:     */   {
/* 1946:     */     static final long serialVersionUID = 1L;
/* 1947:     */     int m_nTargetNode;
/* 1948:     */     String m_sNewName;
/* 1949:     */     String m_sOldName;
/* 1950:     */     
/* 1951:     */     RenameAction(int nTargetNode, String sOldName, String sNewName)
/* 1952:     */     {
/* 1953:2286 */       super();
/* 1954:2287 */       this.m_nTargetNode = nTargetNode;
/* 1955:2288 */       this.m_sNewName = sNewName;
/* 1956:2289 */       this.m_sOldName = sOldName;
/* 1957:     */     }
/* 1958:     */     
/* 1959:     */     public void undo()
/* 1960:     */     {
/* 1961:2294 */       EditableBayesNet.this.setNodeName(this.m_nTargetNode, this.m_sOldName);
/* 1962:     */     }
/* 1963:     */     
/* 1964:     */     public void redo()
/* 1965:     */     {
/* 1966:2299 */       EditableBayesNet.this.setNodeName(this.m_nTargetNode, this.m_sNewName);
/* 1967:     */     }
/* 1968:     */   }
/* 1969:     */   
/* 1970:     */   class RenameValueAction
/* 1971:     */     extends EditableBayesNet.RenameAction
/* 1972:     */   {
/* 1973:     */     static final long serialVersionUID = 1L;
/* 1974:     */     
/* 1975:     */     RenameValueAction(int nTargetNode, String sOldName, String sNewName)
/* 1976:     */     {
/* 1977:2308 */       super(nTargetNode, sOldName, sNewName);
/* 1978:     */     }
/* 1979:     */     
/* 1980:     */     public void undo()
/* 1981:     */     {
/* 1982:2313 */       EditableBayesNet.this.renameNodeValue(this.m_nTargetNode, this.m_sNewName, this.m_sOldName);
/* 1983:     */     }
/* 1984:     */     
/* 1985:     */     public void redo()
/* 1986:     */     {
/* 1987:2318 */       EditableBayesNet.this.renameNodeValue(this.m_nTargetNode, this.m_sOldName, this.m_sNewName);
/* 1988:     */     }
/* 1989:     */     
/* 1990:     */     public String getUndoMsg()
/* 1991:     */     {
/* 1992:2323 */       return "Value of node " + EditableBayesNet.this.getNodeName(this.m_nTargetNode) + " changed from " + this.m_sNewName + " to " + this.m_sOldName;
/* 1993:     */     }
/* 1994:     */     
/* 1995:     */     public String getRedoMsg()
/* 1996:     */     {
/* 1997:2329 */       return "Value of node " + EditableBayesNet.this.getNodeName(this.m_nTargetNode) + " changed from " + this.m_sOldName + " to " + this.m_sNewName;
/* 1998:     */     }
/* 1999:     */   }
/* 2000:     */   
/* 2001:     */   class AddValueAction
/* 2002:     */     extends EditableBayesNet.UndoAction
/* 2003:     */   {
/* 2004:     */     static final long serialVersionUID = 1L;
/* 2005:     */     int m_nTargetNode;
/* 2006:     */     String m_sValue;
/* 2007:     */     
/* 2008:     */     AddValueAction(int nTargetNode, String sValue)
/* 2009:     */     {
/* 2010:2341 */       super();
/* 2011:2342 */       this.m_nTargetNode = nTargetNode;
/* 2012:2343 */       this.m_sValue = sValue;
/* 2013:     */     }
/* 2014:     */     
/* 2015:     */     public void undo()
/* 2016:     */     {
/* 2017:     */       try
/* 2018:     */       {
/* 2019:2349 */         EditableBayesNet.this.delNodeValue(this.m_nTargetNode, this.m_sValue);
/* 2020:     */       }
/* 2021:     */       catch (Exception e)
/* 2022:     */       {
/* 2023:2351 */         e.printStackTrace();
/* 2024:     */       }
/* 2025:     */     }
/* 2026:     */     
/* 2027:     */     public void redo()
/* 2028:     */     {
/* 2029:2357 */       EditableBayesNet.this.addNodeValue(this.m_nTargetNode, this.m_sValue);
/* 2030:     */     }
/* 2031:     */     
/* 2032:     */     public String getUndoMsg()
/* 2033:     */     {
/* 2034:2362 */       return "Value " + this.m_sValue + " removed from node " + EditableBayesNet.this.getNodeName(this.m_nTargetNode);
/* 2035:     */     }
/* 2036:     */     
/* 2037:     */     public String getRedoMsg()
/* 2038:     */     {
/* 2039:2368 */       return "Value " + this.m_sValue + " added to node " + EditableBayesNet.this.getNodeName(this.m_nTargetNode);
/* 2040:     */     }
/* 2041:     */   }
/* 2042:     */   
/* 2043:     */   class DelValueAction
/* 2044:     */     extends EditableBayesNet.UndoAction
/* 2045:     */   {
/* 2046:     */     static final long serialVersionUID = 1L;
/* 2047:     */     int m_nTargetNode;
/* 2048:     */     String m_sValue;
/* 2049:     */     Estimator[] m_CPT;
/* 2050:     */     ArrayList<Integer> m_children;
/* 2051:     */     Estimator[][] m_childAtts;
/* 2052:     */     Attribute m_att;
/* 2053:     */     
/* 2054:     */     DelValueAction(int nTargetNode, String sValue)
/* 2055:     */     {
/* 2056:2388 */       super();
/* 2057:     */       try
/* 2058:     */       {
/* 2059:2390 */         this.m_nTargetNode = nTargetNode;
/* 2060:2391 */         this.m_sValue = sValue;
/* 2061:2392 */         this.m_att = EditableBayesNet.this.m_Instances.attribute(nTargetNode);
/* 2062:2393 */         so = new SerializedObject(EditableBayesNet.this.m_Distributions[nTargetNode]);
/* 2063:2394 */         this.m_CPT = ((Estimator[])so.getObject());
/* 2064:     */         
/* 2065:2396 */         this.m_children = new ArrayList();
/* 2066:2397 */         for (int iNode = 0; iNode < EditableBayesNet.this.getNrOfNodes(); iNode++) {
/* 2067:2398 */           if (EditableBayesNet.this.m_ParentSets[iNode].contains(nTargetNode)) {
/* 2068:2399 */             this.m_children.add(Integer.valueOf(iNode));
/* 2069:     */           }
/* 2070:     */         }
/* 2071:2402 */         this.m_childAtts = new Estimator[this.m_children.size()][];
/* 2072:2403 */         for (int iChild = 0; iChild < this.m_children.size(); iChild++)
/* 2073:     */         {
/* 2074:2404 */           int nChild = ((Integer)this.m_children.get(iChild)).intValue();
/* 2075:2405 */           this.m_childAtts[iChild] = EditableBayesNet.this.m_Distributions[nChild];
/* 2076:     */         }
/* 2077:     */       }
/* 2078:     */       catch (Exception e)
/* 2079:     */       {
/* 2080:     */         SerializedObject so;
/* 2081:2408 */         e.printStackTrace();
/* 2082:     */       }
/* 2083:     */     }
/* 2084:     */     
/* 2085:     */     public void undo()
/* 2086:     */     {
/* 2087:     */       try
/* 2088:     */       {
/* 2089:2415 */         EditableBayesNet.this.m_Instances.insertAttributeAt(this.m_att, this.m_nTargetNode);
/* 2090:2416 */         SerializedObject so = new SerializedObject(this.m_CPT);
/* 2091:2417 */         EditableBayesNet.this.m_Distributions[this.m_nTargetNode] = ((Estimator[])(Estimator[])so.getObject());
/* 2092:2418 */         for (int iChild = 0; iChild < this.m_children.size(); iChild++)
/* 2093:     */         {
/* 2094:2419 */           int nChild = ((Integer)this.m_children.get(iChild)).intValue();
/* 2095:2420 */           EditableBayesNet.this.m_Instances.insertAttributeAt(this.m_att, this.m_nTargetNode);
/* 2096:2421 */           so = new SerializedObject(this.m_childAtts[iChild]);
/* 2097:2422 */           EditableBayesNet.this.m_Distributions[nChild] = ((Estimator[])(Estimator[])so.getObject());
/* 2098:     */         }
/* 2099:     */       }
/* 2100:     */       catch (Exception e)
/* 2101:     */       {
/* 2102:2425 */         e.printStackTrace();
/* 2103:     */       }
/* 2104:     */     }
/* 2105:     */     
/* 2106:     */     public void redo()
/* 2107:     */     {
/* 2108:     */       try
/* 2109:     */       {
/* 2110:2432 */         EditableBayesNet.this.delNodeValue(this.m_nTargetNode, this.m_sValue);
/* 2111:     */       }
/* 2112:     */       catch (Exception e)
/* 2113:     */       {
/* 2114:2434 */         e.printStackTrace();
/* 2115:     */       }
/* 2116:     */     }
/* 2117:     */     
/* 2118:     */     public String getUndoMsg()
/* 2119:     */     {
/* 2120:2440 */       return "Value " + this.m_sValue + " added to node " + EditableBayesNet.this.getNodeName(this.m_nTargetNode);
/* 2121:     */     }
/* 2122:     */     
/* 2123:     */     public String getRedoMsg()
/* 2124:     */     {
/* 2125:2446 */       return "Value " + this.m_sValue + " removed from node " + EditableBayesNet.this.getNodeName(this.m_nTargetNode);
/* 2126:     */     }
/* 2127:     */   }
/* 2128:     */   
/* 2129:     */   class alignAction
/* 2130:     */     extends EditableBayesNet.UndoAction
/* 2131:     */   {
/* 2132:     */     static final long serialVersionUID = 1L;
/* 2133:     */     ArrayList<Integer> m_nodes;
/* 2134:     */     ArrayList<Integer> m_posX;
/* 2135:     */     ArrayList<Integer> m_posY;
/* 2136:     */     
/* 2137:     */     alignAction()
/* 2138:     */     {
/* 2139:2460 */       super();
/* 2140:2461 */       this.m_nodes = new ArrayList(nodes.size());
/* 2141:2462 */       this.m_posX = new ArrayList(nodes.size());
/* 2142:2463 */       this.m_posY = new ArrayList(nodes.size());
/* 2143:2464 */       for (int iNode = 0; iNode < nodes.size(); iNode++)
/* 2144:     */       {
/* 2145:2465 */         int nNode = ((Integer)nodes.get(iNode)).intValue();
/* 2146:2466 */         this.m_nodes.add(Integer.valueOf(nNode));
/* 2147:2467 */         this.m_posX.add(Integer.valueOf(EditableBayesNet.this.getPositionX(nNode)));
/* 2148:2468 */         this.m_posY.add(Integer.valueOf(EditableBayesNet.this.getPositionY(nNode)));
/* 2149:     */       }
/* 2150:     */     }
/* 2151:     */     
/* 2152:     */     public void undo()
/* 2153:     */     {
/* 2154:     */       try
/* 2155:     */       {
/* 2156:2475 */         for (int iNode = 0; iNode < this.m_nodes.size(); iNode++)
/* 2157:     */         {
/* 2158:2476 */           int nNode = ((Integer)this.m_nodes.get(iNode)).intValue();
/* 2159:2477 */           EditableBayesNet.this.setPosition(nNode, ((Integer)this.m_posX.get(iNode)).intValue(), ((Integer)this.m_posY.get(iNode)).intValue());
/* 2160:     */         }
/* 2161:     */       }
/* 2162:     */       catch (Exception e)
/* 2163:     */       {
/* 2164:2480 */         e.printStackTrace();
/* 2165:     */       }
/* 2166:     */     }
/* 2167:     */   }
/* 2168:     */   
/* 2169:     */   class alignLeftAction
/* 2170:     */     extends EditableBayesNet.alignAction
/* 2171:     */   {
/* 2172:     */     static final long serialVersionUID = 1L;
/* 2173:     */     
/* 2174:     */     public alignLeftAction()
/* 2175:     */     {
/* 2176:2490 */       super(nodes);
/* 2177:     */     }
/* 2178:     */     
/* 2179:     */     public void redo()
/* 2180:     */     {
/* 2181:     */       try
/* 2182:     */       {
/* 2183:2496 */         EditableBayesNet.this.alignLeft(this.m_nodes);
/* 2184:     */       }
/* 2185:     */       catch (Exception e)
/* 2186:     */       {
/* 2187:2498 */         e.printStackTrace();
/* 2188:     */       }
/* 2189:     */     }
/* 2190:     */     
/* 2191:     */     public String getUndoMsg()
/* 2192:     */     {
/* 2193:2504 */       return "Returning " + this.m_nodes.size() + " from aliging nodes to the left.";
/* 2194:     */     }
/* 2195:     */     
/* 2196:     */     public String getRedoMsg()
/* 2197:     */     {
/* 2198:2509 */       return "Aligning " + this.m_nodes.size() + " nodes to the left.";
/* 2199:     */     }
/* 2200:     */   }
/* 2201:     */   
/* 2202:     */   class alignRightAction
/* 2203:     */     extends EditableBayesNet.alignAction
/* 2204:     */   {
/* 2205:     */     static final long serialVersionUID = 1L;
/* 2206:     */     
/* 2207:     */     public alignRightAction()
/* 2208:     */     {
/* 2209:2518 */       super(nodes);
/* 2210:     */     }
/* 2211:     */     
/* 2212:     */     public void redo()
/* 2213:     */     {
/* 2214:     */       try
/* 2215:     */       {
/* 2216:2524 */         EditableBayesNet.this.alignRight(this.m_nodes);
/* 2217:     */       }
/* 2218:     */       catch (Exception e)
/* 2219:     */       {
/* 2220:2526 */         e.printStackTrace();
/* 2221:     */       }
/* 2222:     */     }
/* 2223:     */     
/* 2224:     */     public String getUndoMsg()
/* 2225:     */     {
/* 2226:2532 */       return "Returning " + this.m_nodes.size() + " from aliging nodes to the right.";
/* 2227:     */     }
/* 2228:     */     
/* 2229:     */     public String getRedoMsg()
/* 2230:     */     {
/* 2231:2538 */       return "Aligning " + this.m_nodes.size() + " nodes to the right.";
/* 2232:     */     }
/* 2233:     */   }
/* 2234:     */   
/* 2235:     */   class alignTopAction
/* 2236:     */     extends EditableBayesNet.alignAction
/* 2237:     */   {
/* 2238:     */     static final long serialVersionUID = 1L;
/* 2239:     */     
/* 2240:     */     public alignTopAction()
/* 2241:     */     {
/* 2242:2547 */       super(nodes);
/* 2243:     */     }
/* 2244:     */     
/* 2245:     */     public void redo()
/* 2246:     */     {
/* 2247:     */       try
/* 2248:     */       {
/* 2249:2553 */         EditableBayesNet.this.alignTop(this.m_nodes);
/* 2250:     */       }
/* 2251:     */       catch (Exception e)
/* 2252:     */       {
/* 2253:2555 */         e.printStackTrace();
/* 2254:     */       }
/* 2255:     */     }
/* 2256:     */     
/* 2257:     */     public String getUndoMsg()
/* 2258:     */     {
/* 2259:2561 */       return "Returning " + this.m_nodes.size() + " from aliging nodes to the top.";
/* 2260:     */     }
/* 2261:     */     
/* 2262:     */     public String getRedoMsg()
/* 2263:     */     {
/* 2264:2566 */       return "Aligning " + this.m_nodes.size() + " nodes to the top.";
/* 2265:     */     }
/* 2266:     */   }
/* 2267:     */   
/* 2268:     */   class alignBottomAction
/* 2269:     */     extends EditableBayesNet.alignAction
/* 2270:     */   {
/* 2271:     */     static final long serialVersionUID = 1L;
/* 2272:     */     
/* 2273:     */     public alignBottomAction()
/* 2274:     */     {
/* 2275:2575 */       super(nodes);
/* 2276:     */     }
/* 2277:     */     
/* 2278:     */     public void redo()
/* 2279:     */     {
/* 2280:     */       try
/* 2281:     */       {
/* 2282:2581 */         EditableBayesNet.this.alignBottom(this.m_nodes);
/* 2283:     */       }
/* 2284:     */       catch (Exception e)
/* 2285:     */       {
/* 2286:2583 */         e.printStackTrace();
/* 2287:     */       }
/* 2288:     */     }
/* 2289:     */     
/* 2290:     */     public String getUndoMsg()
/* 2291:     */     {
/* 2292:2589 */       return "Returning " + this.m_nodes.size() + " from aliging nodes to the bottom.";
/* 2293:     */     }
/* 2294:     */     
/* 2295:     */     public String getRedoMsg()
/* 2296:     */     {
/* 2297:2595 */       return "Aligning " + this.m_nodes.size() + " nodes to the bottom.";
/* 2298:     */     }
/* 2299:     */   }
/* 2300:     */   
/* 2301:     */   class centerHorizontalAction
/* 2302:     */     extends EditableBayesNet.alignAction
/* 2303:     */   {
/* 2304:     */     static final long serialVersionUID = 1L;
/* 2305:     */     
/* 2306:     */     public centerHorizontalAction()
/* 2307:     */     {
/* 2308:2604 */       super(nodes);
/* 2309:     */     }
/* 2310:     */     
/* 2311:     */     public void redo()
/* 2312:     */     {
/* 2313:     */       try
/* 2314:     */       {
/* 2315:2610 */         EditableBayesNet.this.centerHorizontal(this.m_nodes);
/* 2316:     */       }
/* 2317:     */       catch (Exception e)
/* 2318:     */       {
/* 2319:2612 */         e.printStackTrace();
/* 2320:     */       }
/* 2321:     */     }
/* 2322:     */     
/* 2323:     */     public String getUndoMsg()
/* 2324:     */     {
/* 2325:2618 */       return "Returning " + this.m_nodes.size() + " from centering horizontally.";
/* 2326:     */     }
/* 2327:     */     
/* 2328:     */     public String getRedoMsg()
/* 2329:     */     {
/* 2330:2623 */       return "Centering " + this.m_nodes.size() + " nodes horizontally.";
/* 2331:     */     }
/* 2332:     */   }
/* 2333:     */   
/* 2334:     */   class centerVerticalAction
/* 2335:     */     extends EditableBayesNet.alignAction
/* 2336:     */   {
/* 2337:     */     static final long serialVersionUID = 1L;
/* 2338:     */     
/* 2339:     */     public centerVerticalAction()
/* 2340:     */     {
/* 2341:2632 */       super(nodes);
/* 2342:     */     }
/* 2343:     */     
/* 2344:     */     public void redo()
/* 2345:     */     {
/* 2346:     */       try
/* 2347:     */       {
/* 2348:2638 */         EditableBayesNet.this.centerVertical(this.m_nodes);
/* 2349:     */       }
/* 2350:     */       catch (Exception e)
/* 2351:     */       {
/* 2352:2640 */         e.printStackTrace();
/* 2353:     */       }
/* 2354:     */     }
/* 2355:     */     
/* 2356:     */     public String getUndoMsg()
/* 2357:     */     {
/* 2358:2646 */       return "Returning " + this.m_nodes.size() + " from centering vertically.";
/* 2359:     */     }
/* 2360:     */     
/* 2361:     */     public String getRedoMsg()
/* 2362:     */     {
/* 2363:2651 */       return "Centering " + this.m_nodes.size() + " nodes vertically.";
/* 2364:     */     }
/* 2365:     */   }
/* 2366:     */   
/* 2367:     */   class spaceHorizontalAction
/* 2368:     */     extends EditableBayesNet.alignAction
/* 2369:     */   {
/* 2370:     */     static final long serialVersionUID = 1L;
/* 2371:     */     
/* 2372:     */     public spaceHorizontalAction()
/* 2373:     */     {
/* 2374:2660 */       super(nodes);
/* 2375:     */     }
/* 2376:     */     
/* 2377:     */     public void redo()
/* 2378:     */     {
/* 2379:     */       try
/* 2380:     */       {
/* 2381:2666 */         EditableBayesNet.this.spaceHorizontal(this.m_nodes);
/* 2382:     */       }
/* 2383:     */       catch (Exception e)
/* 2384:     */       {
/* 2385:2668 */         e.printStackTrace();
/* 2386:     */       }
/* 2387:     */     }
/* 2388:     */     
/* 2389:     */     public String getUndoMsg()
/* 2390:     */     {
/* 2391:2674 */       return "Returning " + this.m_nodes.size() + " from spaceing horizontally.";
/* 2392:     */     }
/* 2393:     */     
/* 2394:     */     public String getRedoMsg()
/* 2395:     */     {
/* 2396:2679 */       return "spaceing " + this.m_nodes.size() + " nodes horizontally.";
/* 2397:     */     }
/* 2398:     */   }
/* 2399:     */   
/* 2400:     */   class spaceVerticalAction
/* 2401:     */     extends EditableBayesNet.alignAction
/* 2402:     */   {
/* 2403:     */     static final long serialVersionUID = 1L;
/* 2404:     */     
/* 2405:     */     public spaceVerticalAction()
/* 2406:     */     {
/* 2407:2688 */       super(nodes);
/* 2408:     */     }
/* 2409:     */     
/* 2410:     */     public void redo()
/* 2411:     */     {
/* 2412:     */       try
/* 2413:     */       {
/* 2414:2694 */         EditableBayesNet.this.spaceVertical(this.m_nodes);
/* 2415:     */       }
/* 2416:     */       catch (Exception e)
/* 2417:     */       {
/* 2418:2696 */         e.printStackTrace();
/* 2419:     */       }
/* 2420:     */     }
/* 2421:     */     
/* 2422:     */     public String getUndoMsg()
/* 2423:     */     {
/* 2424:2702 */       return "Returning " + this.m_nodes.size() + " from spaceng vertically.";
/* 2425:     */     }
/* 2426:     */     
/* 2427:     */     public String getRedoMsg()
/* 2428:     */     {
/* 2429:2707 */       return "Spaceng " + this.m_nodes.size() + " nodes vertically.";
/* 2430:     */     }
/* 2431:     */   }
/* 2432:     */   
/* 2433:     */   class SetPositionAction
/* 2434:     */     extends EditableBayesNet.UndoAction
/* 2435:     */   {
/* 2436:     */     static final long serialVersionUID = 1L;
/* 2437:     */     int m_nTargetNode;
/* 2438:     */     int m_nX;
/* 2439:     */     int m_nY;
/* 2440:     */     int m_nX2;
/* 2441:     */     int m_nY2;
/* 2442:     */     
/* 2443:     */     SetPositionAction(int nTargetNode, int nX, int nY)
/* 2444:     */     {
/* 2445:2724 */       super();
/* 2446:2725 */       this.m_nTargetNode = nTargetNode;
/* 2447:2726 */       this.m_nX2 = nX;
/* 2448:2727 */       this.m_nY2 = nY;
/* 2449:2728 */       this.m_nX = EditableBayesNet.this.getPositionX(nTargetNode);
/* 2450:2729 */       this.m_nY = EditableBayesNet.this.getPositionY(nTargetNode);
/* 2451:     */     }
/* 2452:     */     
/* 2453:     */     public void undo()
/* 2454:     */     {
/* 2455:2734 */       EditableBayesNet.this.setPosition(this.m_nTargetNode, this.m_nX, this.m_nY);
/* 2456:     */     }
/* 2457:     */     
/* 2458:     */     public void redo()
/* 2459:     */     {
/* 2460:2739 */       EditableBayesNet.this.setPosition(this.m_nTargetNode, this.m_nX2, this.m_nY2);
/* 2461:     */     }
/* 2462:     */     
/* 2463:     */     public void setUndoPosition(int nX, int nY)
/* 2464:     */     {
/* 2465:2743 */       this.m_nX2 = nX;
/* 2466:2744 */       this.m_nY2 = nY;
/* 2467:     */     }
/* 2468:     */   }
/* 2469:     */   
/* 2470:     */   class SetGroupPositionAction
/* 2471:     */     extends EditableBayesNet.UndoAction
/* 2472:     */   {
/* 2473:     */     static final long serialVersionUID = 1L;
/* 2474:     */     ArrayList<Integer> m_nodes;
/* 2475:     */     int m_dX;
/* 2476:     */     int m_dY;
/* 2477:     */     
/* 2478:     */     SetGroupPositionAction(int nodes, int dX)
/* 2479:     */     {
/* 2480:2755 */       super();
/* 2481:2756 */       this.m_nodes = new ArrayList(nodes.size());
/* 2482:2757 */       for (int iNode = 0; iNode < nodes.size(); iNode++) {
/* 2483:2758 */         this.m_nodes.add(nodes.get(iNode));
/* 2484:     */       }
/* 2485:2760 */       this.m_dX = dX;
/* 2486:2761 */       this.m_dY = dY;
/* 2487:     */     }
/* 2488:     */     
/* 2489:     */     public void undo()
/* 2490:     */     {
/* 2491:2766 */       for (int iNode = 0; iNode < this.m_nodes.size(); iNode++)
/* 2492:     */       {
/* 2493:2767 */         int nNode = ((Integer)this.m_nodes.get(iNode)).intValue();
/* 2494:2768 */         EditableBayesNet.this.setPosition(nNode, EditableBayesNet.this.getPositionX(nNode) - this.m_dX, EditableBayesNet.this.getPositionY(nNode) - this.m_dY);
/* 2495:     */       }
/* 2496:     */     }
/* 2497:     */     
/* 2498:     */     public void redo()
/* 2499:     */     {
/* 2500:2775 */       for (int iNode = 0; iNode < this.m_nodes.size(); iNode++)
/* 2501:     */       {
/* 2502:2776 */         int nNode = ((Integer)this.m_nodes.get(iNode)).intValue();
/* 2503:2777 */         EditableBayesNet.this.setPosition(nNode, EditableBayesNet.this.getPositionX(nNode) + this.m_dX, EditableBayesNet.this.getPositionY(nNode) + this.m_dY);
/* 2504:     */       }
/* 2505:     */     }
/* 2506:     */     
/* 2507:     */     public void setUndoPosition(int dX, int dY)
/* 2508:     */     {
/* 2509:2783 */       this.m_dX += dX;
/* 2510:2784 */       this.m_dY += dY;
/* 2511:     */     }
/* 2512:     */   }
/* 2513:     */   
/* 2514:     */   class LayoutGraphAction
/* 2515:     */     extends EditableBayesNet.UndoAction
/* 2516:     */   {
/* 2517:     */     static final long serialVersionUID = 1L;
/* 2518:     */     ArrayList<Integer> m_nPosX;
/* 2519:     */     ArrayList<Integer> m_nPosY;
/* 2520:     */     ArrayList<Integer> m_nPosX2;
/* 2521:     */     ArrayList<Integer> m_nPosY2;
/* 2522:     */     
/* 2523:     */     LayoutGraphAction(ArrayList<Integer> nPosX)
/* 2524:     */     {
/* 2525:2796 */       super();
/* 2526:2797 */       this.m_nPosX = new ArrayList(nPosX.size());
/* 2527:2798 */       this.m_nPosY = new ArrayList(nPosX.size());
/* 2528:2799 */       this.m_nPosX2 = new ArrayList(nPosX.size());
/* 2529:2800 */       this.m_nPosY2 = new ArrayList(nPosX.size());
/* 2530:2801 */       for (int iNode = 0; iNode < nPosX.size(); iNode++)
/* 2531:     */       {
/* 2532:2802 */         this.m_nPosX.add(EditableBayesNet.this.m_nPositionX.get(iNode));
/* 2533:2803 */         this.m_nPosY.add(EditableBayesNet.this.m_nPositionY.get(iNode));
/* 2534:2804 */         this.m_nPosX2.add(nPosX.get(iNode));
/* 2535:2805 */         this.m_nPosY2.add(nPosY.get(iNode));
/* 2536:     */       }
/* 2537:     */     }
/* 2538:     */     
/* 2539:     */     public void undo()
/* 2540:     */     {
/* 2541:2811 */       for (int iNode = 0; iNode < this.m_nPosX.size(); iNode++) {
/* 2542:2812 */         EditableBayesNet.this.setPosition(iNode, ((Integer)this.m_nPosX.get(iNode)).intValue(), ((Integer)this.m_nPosY.get(iNode)).intValue());
/* 2543:     */       }
/* 2544:     */     }
/* 2545:     */     
/* 2546:     */     public void redo()
/* 2547:     */     {
/* 2548:2818 */       for (int iNode = 0; iNode < this.m_nPosX.size(); iNode++) {
/* 2549:2819 */         EditableBayesNet.this.setPosition(iNode, ((Integer)this.m_nPosX2.get(iNode)).intValue(), ((Integer)this.m_nPosY2.get(iNode)).intValue());
/* 2550:     */       }
/* 2551:     */     }
/* 2552:     */   }
/* 2553:     */   
/* 2554:     */   class PasteAction
/* 2555:     */     extends EditableBayesNet.UndoAction
/* 2556:     */   {
/* 2557:     */     static final long serialVersionUID = 1L;
/* 2558:     */     int m_nBase;
/* 2559:     */     String m_sXML;
/* 2560:     */     
/* 2561:     */     PasteAction(String sXML, int nBase)
/* 2562:     */     {
/* 2563:2831 */       super();
/* 2564:2832 */       this.m_sXML = sXML;
/* 2565:2833 */       this.m_nBase = nBase;
/* 2566:     */     }
/* 2567:     */     
/* 2568:     */     public void undo()
/* 2569:     */     {
/* 2570:     */       try
/* 2571:     */       {
/* 2572:2839 */         int iNode = EditableBayesNet.this.getNrOfNodes() - 1;
/* 2573:2840 */         while (iNode >= this.m_nBase)
/* 2574:     */         {
/* 2575:2841 */           EditableBayesNet.this.deleteNode(iNode);
/* 2576:2842 */           iNode--;
/* 2577:     */         }
/* 2578:     */       }
/* 2579:     */       catch (Exception e)
/* 2580:     */       {
/* 2581:2845 */         e.printStackTrace();
/* 2582:     */       }
/* 2583:     */     }
/* 2584:     */     
/* 2585:     */     public void redo()
/* 2586:     */     {
/* 2587:     */       try
/* 2588:     */       {
/* 2589:2852 */         EditableBayesNet.this.paste(this.m_sXML, 1);
/* 2590:     */       }
/* 2591:     */       catch (Exception e)
/* 2592:     */       {
/* 2593:2854 */         e.printStackTrace();
/* 2594:     */       }
/* 2595:     */     }
/* 2596:     */   }
/* 2597:     */   
/* 2598:     */   public String getRevision()
/* 2599:     */   {
/* 2600:2866 */     return RevisionUtils.extract("$Revision: 10153 $");
/* 2601:     */   }
/* 2602:     */   
/* 2603:     */   public static void main(String[] args) {}
/* 2604:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.net.EditableBayesNet
 * JD-Core Version:    0.7.0.1
 */