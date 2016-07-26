/*    1:     */ package weka.classifiers.trees;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.ArrayList;
/*    6:     */ import java.util.Collections;
/*    7:     */ import java.util.Enumeration;
/*    8:     */ import java.util.Vector;
/*    9:     */ import weka.classifiers.AbstractClassifier;
/*   10:     */ import weka.classifiers.IterativeClassifier;
/*   11:     */ import weka.classifiers.trees.adtree.ReferenceInstances;
/*   12:     */ import weka.core.AdditionalMeasureProducer;
/*   13:     */ import weka.core.Attribute;
/*   14:     */ import weka.core.Capabilities;
/*   15:     */ import weka.core.Capabilities.Capability;
/*   16:     */ import weka.core.DenseInstance;
/*   17:     */ import weka.core.Drawable;
/*   18:     */ import weka.core.Instance;
/*   19:     */ import weka.core.Instances;
/*   20:     */ import weka.core.Option;
/*   21:     */ import weka.core.RevisionUtils;
/*   22:     */ import weka.core.TechnicalInformation;
/*   23:     */ import weka.core.TechnicalInformation.Field;
/*   24:     */ import weka.core.TechnicalInformation.Type;
/*   25:     */ import weka.core.TechnicalInformationHandler;
/*   26:     */ import weka.core.Utils;
/*   27:     */ import weka.core.WekaEnumeration;
/*   28:     */ 
/*   29:     */ public class LADTree
/*   30:     */   extends AbstractClassifier
/*   31:     */   implements Drawable, IterativeClassifier, AdditionalMeasureProducer, TechnicalInformationHandler
/*   32:     */ {
/*   33:     */   private static final long serialVersionUID = -4940716114518300302L;
/*   34:     */   protected double Z_MAX;
/*   35:     */   protected int m_numOfClasses;
/*   36:     */   protected ReferenceInstances m_trainInstances;
/*   37:     */   protected PredictionNode m_root;
/*   38:     */   protected int m_lastAddedSplitNum;
/*   39:     */   protected int[] m_numericAttIndices;
/*   40:     */   protected double m_search_smallestLeastSquares;
/*   41:     */   protected PredictionNode m_search_bestInsertionNode;
/*   42:     */   protected Splitter m_search_bestSplitter;
/*   43:     */   protected Instances m_search_bestPathInstances;
/*   44:     */   protected ArrayList<Splitter> m_staticPotentialSplitters2way;
/*   45:     */   protected int m_nodesExpanded;
/*   46:     */   protected int m_examplesCounted;
/*   47:     */   protected int m_boostingIterations;
/*   48:     */   
/*   49:     */   public LADTree()
/*   50:     */   {
/*   51: 106 */     this.Z_MAX = 4.0D;
/*   52:     */     
/*   53:     */ 
/*   54:     */ 
/*   55:     */ 
/*   56:     */ 
/*   57:     */ 
/*   58:     */ 
/*   59:     */ 
/*   60: 115 */     this.m_root = null;
/*   61:     */     
/*   62:     */ 
/*   63: 118 */     this.m_lastAddedSplitNum = 0;
/*   64:     */     
/*   65:     */ 
/*   66:     */ 
/*   67:     */ 
/*   68:     */ 
/*   69:     */ 
/*   70:     */ 
/*   71:     */ 
/*   72:     */ 
/*   73:     */ 
/*   74:     */ 
/*   75:     */ 
/*   76:     */ 
/*   77:     */ 
/*   78: 133 */     this.m_nodesExpanded = 0;
/*   79: 134 */     this.m_examplesCounted = 0;
/*   80:     */     
/*   81:     */ 
/*   82: 137 */     this.m_boostingIterations = 10;
/*   83:     */   }
/*   84:     */   
/*   85:     */   public String globalInfo()
/*   86:     */   {
/*   87: 147 */     return "Class for generating a multi-class alternating decision tree using the LogitBoost strategy. For more info, see\n\n" + getTechnicalInformation().toString();
/*   88:     */   }
/*   89:     */   
/*   90:     */   public TechnicalInformation getTechnicalInformation()
/*   91:     */   {
/*   92: 163 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*   93: 164 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Geoffrey Holmes and Bernhard Pfahringer and Richard Kirkby and Eibe Frank and Mark Hall");
/*   94:     */     
/*   95:     */ 
/*   96:     */ 
/*   97: 168 */     result.setValue(TechnicalInformation.Field.TITLE, "Multiclass alternating decision trees");
/*   98: 169 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "ECML");
/*   99: 170 */     result.setValue(TechnicalInformation.Field.YEAR, "2001");
/*  100: 171 */     result.setValue(TechnicalInformation.Field.PAGES, "161-172");
/*  101: 172 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Springer");
/*  102:     */     
/*  103: 174 */     return result;
/*  104:     */   }
/*  105:     */   
/*  106:     */   protected class LADInstance
/*  107:     */     extends DenseInstance
/*  108:     */   {
/*  109:     */     private static final long serialVersionUID = -9005560077243466915L;
/*  110:     */     public double[] fVector;
/*  111:     */     public double[] wVector;
/*  112:     */     public double[] pVector;
/*  113:     */     public double[] zVector;
/*  114:     */     
/*  115:     */     public LADInstance(Instance instance)
/*  116:     */     {
/*  117: 191 */       super();
/*  118:     */       
/*  119: 193 */       setDataset(instance.dataset());
/*  120:     */       
/*  121:     */ 
/*  122: 196 */       this.fVector = new double[LADTree.this.m_numOfClasses];
/*  123: 197 */       this.wVector = new double[LADTree.this.m_numOfClasses];
/*  124: 198 */       this.pVector = new double[LADTree.this.m_numOfClasses];
/*  125: 199 */       this.zVector = new double[LADTree.this.m_numOfClasses];
/*  126:     */       
/*  127:     */ 
/*  128: 202 */       double initProb = 1.0D / LADTree.this.m_numOfClasses;
/*  129: 203 */       for (int i = 0; i < LADTree.this.m_numOfClasses; i++) {
/*  130: 204 */         this.pVector[i] = initProb;
/*  131:     */       }
/*  132: 206 */       updateZVector();
/*  133: 207 */       updateWVector();
/*  134:     */     }
/*  135:     */     
/*  136:     */     public void updateWeights(double[] fVectorIncrement)
/*  137:     */     {
/*  138: 211 */       for (int i = 0; i < this.fVector.length; i++) {
/*  139: 212 */         this.fVector[i] += fVectorIncrement[i];
/*  140:     */       }
/*  141: 214 */       updateVectors(this.fVector);
/*  142:     */     }
/*  143:     */     
/*  144:     */     public void updateVectors(double[] newFVector)
/*  145:     */     {
/*  146: 218 */       updatePVector(newFVector);
/*  147: 219 */       updateZVector();
/*  148: 220 */       updateWVector();
/*  149:     */     }
/*  150:     */     
/*  151:     */     public void updatePVector(double[] newFVector)
/*  152:     */     {
/*  153: 224 */       double max = newFVector[Utils.maxIndex(newFVector)];
/*  154: 225 */       for (int i = 0; i < this.pVector.length; i++) {
/*  155: 226 */         this.pVector[i] = Math.exp(newFVector[i] - max);
/*  156:     */       }
/*  157: 228 */       Utils.normalize(this.pVector);
/*  158:     */     }
/*  159:     */     
/*  160:     */     public void updateWVector()
/*  161:     */     {
/*  162: 232 */       for (int i = 0; i < this.wVector.length; i++) {
/*  163: 233 */         this.wVector[i] = ((yVector(i) - this.pVector[i]) / this.zVector[i]);
/*  164:     */       }
/*  165:     */     }
/*  166:     */     
/*  167:     */     public void updateZVector()
/*  168:     */     {
/*  169: 239 */       for (int i = 0; i < this.zVector.length; i++) {
/*  170: 240 */         if (yVector(i) == 1.0D)
/*  171:     */         {
/*  172: 241 */           this.zVector[i] = (1.0D / this.pVector[i]);
/*  173: 242 */           if (this.zVector[i] > LADTree.this.Z_MAX) {
/*  174: 243 */             this.zVector[i] = LADTree.this.Z_MAX;
/*  175:     */           }
/*  176:     */         }
/*  177:     */         else
/*  178:     */         {
/*  179: 246 */           this.zVector[i] = (-1.0D / (1.0D - this.pVector[i]));
/*  180: 247 */           if (this.zVector[i] < -LADTree.this.Z_MAX) {
/*  181: 248 */             this.zVector[i] = (-LADTree.this.Z_MAX);
/*  182:     */           }
/*  183:     */         }
/*  184:     */       }
/*  185:     */     }
/*  186:     */     
/*  187:     */     public double yVector(int index)
/*  188:     */     {
/*  189: 255 */       return index == (int)classValue() ? 1.0D : 0.0D;
/*  190:     */     }
/*  191:     */     
/*  192:     */     public Object copy()
/*  193:     */     {
/*  194: 260 */       LADInstance copy = new LADInstance(LADTree.this, (Instance)super.copy());
/*  195: 261 */       System.arraycopy(this.fVector, 0, copy.fVector, 0, this.fVector.length);
/*  196: 262 */       System.arraycopy(this.wVector, 0, copy.wVector, 0, this.wVector.length);
/*  197: 263 */       System.arraycopy(this.pVector, 0, copy.pVector, 0, this.pVector.length);
/*  198: 264 */       System.arraycopy(this.zVector, 0, copy.zVector, 0, this.zVector.length);
/*  199: 265 */       return copy;
/*  200:     */     }
/*  201:     */     
/*  202:     */     public String toString()
/*  203:     */     {
/*  204: 271 */       StringBuffer text = new StringBuffer();
/*  205: 272 */       text.append(" * F(");
/*  206: 273 */       for (int i = 0; i < this.fVector.length; i++)
/*  207:     */       {
/*  208: 274 */         text.append(Utils.doubleToString(this.fVector[i], 3));
/*  209: 275 */         if (i < this.fVector.length - 1) {
/*  210: 276 */           text.append(",");
/*  211:     */         }
/*  212:     */       }
/*  213: 279 */       text.append(") P(");
/*  214: 280 */       for (int i = 0; i < this.pVector.length; i++)
/*  215:     */       {
/*  216: 281 */         text.append(Utils.doubleToString(this.pVector[i], 3));
/*  217: 282 */         if (i < this.pVector.length - 1) {
/*  218: 283 */           text.append(",");
/*  219:     */         }
/*  220:     */       }
/*  221: 286 */       text.append(") W(");
/*  222: 287 */       for (int i = 0; i < this.wVector.length; i++)
/*  223:     */       {
/*  224: 288 */         text.append(Utils.doubleToString(this.wVector[i], 3));
/*  225: 289 */         if (i < this.wVector.length - 1) {
/*  226: 290 */           text.append(",");
/*  227:     */         }
/*  228:     */       }
/*  229: 293 */       text.append(")");
/*  230: 294 */       return super.toString() + text.toString();
/*  231:     */     }
/*  232:     */   }
/*  233:     */   
/*  234:     */   protected class PredictionNode
/*  235:     */     implements Serializable, Cloneable
/*  236:     */   {
/*  237:     */     private static final long serialVersionUID = -8286364217836877790L;
/*  238:     */     private final double[] values;
/*  239:     */     private final ArrayList<LADTree.Splitter> children;
/*  240:     */     
/*  241:     */     public PredictionNode(double[] newValues)
/*  242:     */     {
/*  243: 308 */       this.values = new double[LADTree.this.m_numOfClasses];
/*  244: 309 */       setValues(newValues);
/*  245: 310 */       this.children = new ArrayList();
/*  246:     */     }
/*  247:     */     
/*  248:     */     public void setValues(double[] newValues)
/*  249:     */     {
/*  250: 314 */       System.arraycopy(newValues, 0, this.values, 0, LADTree.this.m_numOfClasses);
/*  251:     */     }
/*  252:     */     
/*  253:     */     public double[] getValues()
/*  254:     */     {
/*  255: 318 */       return this.values;
/*  256:     */     }
/*  257:     */     
/*  258:     */     public ArrayList<LADTree.Splitter> getChildren()
/*  259:     */     {
/*  260: 322 */       return this.children;
/*  261:     */     }
/*  262:     */     
/*  263:     */     public Enumeration<LADTree.Splitter> children()
/*  264:     */     {
/*  265: 326 */       return new WekaEnumeration(this.children);
/*  266:     */     }
/*  267:     */     
/*  268:     */     public void addChild(LADTree.Splitter newChild)
/*  269:     */     {
/*  270: 331 */       LADTree.Splitter oldEqual = null;
/*  271: 332 */       for (Enumeration<LADTree.Splitter> e = children(); e.hasMoreElements();)
/*  272:     */       {
/*  273: 333 */         LADTree.Splitter split = (LADTree.Splitter)e.nextElement();
/*  274: 334 */         if (newChild.equalTo(split))
/*  275:     */         {
/*  276: 335 */           oldEqual = split;
/*  277: 336 */           break;
/*  278:     */         }
/*  279:     */       }
/*  280: 339 */       if (oldEqual == null)
/*  281:     */       {
/*  282: 340 */         LADTree.Splitter addChild = (LADTree.Splitter)newChild.clone();
/*  283: 341 */         addChild.orderAdded = (++LADTree.this.m_lastAddedSplitNum);
/*  284: 342 */         this.children.add(addChild);
/*  285:     */       }
/*  286:     */       else
/*  287:     */       {
/*  288: 344 */         for (int i = 0; i < newChild.getNumOfBranches(); i++)
/*  289:     */         {
/*  290: 345 */           PredictionNode oldPred = oldEqual.getChildForBranch(i);
/*  291: 346 */           PredictionNode newPred = newChild.getChildForBranch(i);
/*  292: 347 */           if ((oldPred != null) && (newPred != null)) {
/*  293: 348 */             oldPred.merge(newPred);
/*  294:     */           }
/*  295:     */         }
/*  296:     */       }
/*  297:     */     }
/*  298:     */     
/*  299:     */     public Object clone()
/*  300:     */     {
/*  301: 356 */       PredictionNode clone = new PredictionNode(LADTree.this, this.values);
/*  302:     */       
/*  303: 358 */       Enumeration<LADTree.Splitter> e = new WekaEnumeration(this.children);
/*  304: 359 */       while (e.hasMoreElements()) {
/*  305: 360 */         clone.children.add((LADTree.Splitter)((LADTree.Splitter)e.nextElement()).clone());
/*  306:     */       }
/*  307: 362 */       return clone;
/*  308:     */     }
/*  309:     */     
/*  310:     */     public void merge(PredictionNode merger)
/*  311:     */     {
/*  312: 367 */       for (int i = 0; i < LADTree.this.m_numOfClasses; i++) {
/*  313: 368 */         this.values[i] += merger.values[i];
/*  314:     */       }
/*  315: 370 */       for (Enumeration<LADTree.Splitter> e = merger.children(); e.hasMoreElements();) {
/*  316: 371 */         addChild((LADTree.Splitter)e.nextElement());
/*  317:     */       }
/*  318:     */     }
/*  319:     */   }
/*  320:     */   
/*  321:     */   protected abstract class Splitter
/*  322:     */     implements Serializable, Cloneable
/*  323:     */   {
/*  324:     */     private static final long serialVersionUID = -3647262875989478674L;
/*  325:     */     protected int attIndex;
/*  326:     */     public int orderAdded;
/*  327:     */     
/*  328:     */     protected Splitter() {}
/*  329:     */     
/*  330:     */     public abstract int getNumOfBranches();
/*  331:     */     
/*  332:     */     public abstract int branchInstanceGoesDown(Instance paramInstance);
/*  333:     */     
/*  334:     */     public abstract Instances instancesDownBranch(int paramInt, Instances paramInstances);
/*  335:     */     
/*  336:     */     public abstract String attributeString();
/*  337:     */     
/*  338:     */     public abstract String comparisonString(int paramInt);
/*  339:     */     
/*  340:     */     public abstract boolean equalTo(Splitter paramSplitter);
/*  341:     */     
/*  342:     */     public abstract void setChildForBranch(int paramInt, LADTree.PredictionNode paramPredictionNode);
/*  343:     */     
/*  344:     */     public abstract LADTree.PredictionNode getChildForBranch(int paramInt);
/*  345:     */     
/*  346:     */     public abstract Object clone();
/*  347:     */   }
/*  348:     */   
/*  349:     */   protected class TwoWayNominalSplit
/*  350:     */     extends LADTree.Splitter
/*  351:     */   {
/*  352:     */     private static final long serialVersionUID = 8710802611812576635L;
/*  353:     */     private final int trueSplitValue;
/*  354:     */     private final LADTree.PredictionNode[] children;
/*  355:     */     
/*  356:     */     public TwoWayNominalSplit(int _attIndex, int _trueSplitValue)
/*  357:     */     {
/*  358: 417 */       super();
/*  359: 418 */       this.attIndex = _attIndex;
/*  360: 419 */       this.trueSplitValue = _trueSplitValue;
/*  361: 420 */       this.children = new LADTree.PredictionNode[2];
/*  362:     */     }
/*  363:     */     
/*  364:     */     public int getNumOfBranches()
/*  365:     */     {
/*  366: 425 */       return 2;
/*  367:     */     }
/*  368:     */     
/*  369:     */     public int branchInstanceGoesDown(Instance inst)
/*  370:     */     {
/*  371: 430 */       if (inst.isMissing(this.attIndex)) {
/*  372: 431 */         return -1;
/*  373:     */       }
/*  374: 432 */       if (inst.value(this.attIndex) == this.trueSplitValue) {
/*  375: 433 */         return 0;
/*  376:     */       }
/*  377: 435 */       return 1;
/*  378:     */     }
/*  379:     */     
/*  380:     */     public Instances instancesDownBranch(int branch, Instances instances)
/*  381:     */     {
/*  382: 441 */       ReferenceInstances filteredInstances = new ReferenceInstances(instances, 1);
/*  383: 443 */       if (branch == -1) {
/*  384: 444 */         for (Instance instance : instances)
/*  385:     */         {
/*  386: 445 */           Instance inst = instance;
/*  387: 446 */           if (inst.isMissing(this.attIndex)) {
/*  388: 447 */             filteredInstances.addReference(inst);
/*  389:     */           }
/*  390:     */         }
/*  391: 450 */       } else if (branch == 0) {
/*  392: 451 */         for (Instance instance : instances)
/*  393:     */         {
/*  394: 452 */           Instance inst = instance;
/*  395: 453 */           if ((!inst.isMissing(this.attIndex)) && (inst.value(this.attIndex) == this.trueSplitValue)) {
/*  396: 455 */             filteredInstances.addReference(inst);
/*  397:     */           }
/*  398:     */         }
/*  399:     */       } else {
/*  400: 459 */         for (Instance instance : instances)
/*  401:     */         {
/*  402: 460 */           Instance inst = instance;
/*  403: 461 */           if ((!inst.isMissing(this.attIndex)) && (inst.value(this.attIndex) != this.trueSplitValue)) {
/*  404: 463 */             filteredInstances.addReference(inst);
/*  405:     */           }
/*  406:     */         }
/*  407:     */       }
/*  408: 467 */       return filteredInstances;
/*  409:     */     }
/*  410:     */     
/*  411:     */     public String attributeString()
/*  412:     */     {
/*  413: 472 */       return LADTree.this.m_trainInstances.attribute(this.attIndex).name();
/*  414:     */     }
/*  415:     */     
/*  416:     */     public String comparisonString(int branchNum)
/*  417:     */     {
/*  418: 477 */       Attribute att = LADTree.this.m_trainInstances.attribute(this.attIndex);
/*  419: 478 */       if (att.numValues() != 2) {
/*  420: 479 */         return (branchNum == 0 ? "= " : "!= ") + att.value(this.trueSplitValue);
/*  421:     */       }
/*  422: 481 */       return "= " + (branchNum == 0 ? att.value(this.trueSplitValue) : att.value(this.trueSplitValue == 0 ? 1 : 0));
/*  423:     */     }
/*  424:     */     
/*  425:     */     public boolean equalTo(LADTree.Splitter compare)
/*  426:     */     {
/*  427: 488 */       if ((compare instanceof TwoWayNominalSplit))
/*  428:     */       {
/*  429: 489 */         TwoWayNominalSplit compareSame = (TwoWayNominalSplit)compare;
/*  430: 490 */         return (this.attIndex == compareSame.attIndex) && (this.trueSplitValue == compareSame.trueSplitValue);
/*  431:     */       }
/*  432: 492 */       return false;
/*  433:     */     }
/*  434:     */     
/*  435:     */     public void setChildForBranch(int branchNum, LADTree.PredictionNode childPredictor)
/*  436:     */     {
/*  437: 498 */       this.children[branchNum] = childPredictor;
/*  438:     */     }
/*  439:     */     
/*  440:     */     public LADTree.PredictionNode getChildForBranch(int branchNum)
/*  441:     */     {
/*  442: 503 */       return this.children[branchNum];
/*  443:     */     }
/*  444:     */     
/*  445:     */     public Object clone()
/*  446:     */     {
/*  447: 508 */       TwoWayNominalSplit clone = new TwoWayNominalSplit(LADTree.this, this.attIndex, this.trueSplitValue);
/*  448: 510 */       if (this.children[0] != null) {
/*  449: 511 */         clone.setChildForBranch(0, (LADTree.PredictionNode)this.children[0].clone());
/*  450:     */       }
/*  451: 513 */       if (this.children[1] != null) {
/*  452: 514 */         clone.setChildForBranch(1, (LADTree.PredictionNode)this.children[1].clone());
/*  453:     */       }
/*  454: 516 */       return clone;
/*  455:     */     }
/*  456:     */   }
/*  457:     */   
/*  458:     */   protected class TwoWayNumericSplit
/*  459:     */     extends LADTree.Splitter
/*  460:     */     implements Cloneable
/*  461:     */   {
/*  462:     */     private static final long serialVersionUID = 3552224905046975032L;
/*  463:     */     private final double splitPoint;
/*  464:     */     private final LADTree.PredictionNode[] children;
/*  465:     */     
/*  466:     */     public TwoWayNumericSplit(int _attIndex, double _splitPoint)
/*  467:     */     {
/*  468: 529 */       super();
/*  469: 530 */       this.attIndex = _attIndex;
/*  470: 531 */       this.splitPoint = _splitPoint;
/*  471: 532 */       this.children = new LADTree.PredictionNode[2];
/*  472:     */     }
/*  473:     */     
/*  474:     */     public int getNumOfBranches()
/*  475:     */     {
/*  476: 537 */       return 2;
/*  477:     */     }
/*  478:     */     
/*  479:     */     public int branchInstanceGoesDown(Instance inst)
/*  480:     */     {
/*  481: 542 */       if (inst.isMissing(this.attIndex)) {
/*  482: 543 */         return -1;
/*  483:     */       }
/*  484: 544 */       if (inst.value(this.attIndex) < this.splitPoint) {
/*  485: 545 */         return 0;
/*  486:     */       }
/*  487: 547 */       return 1;
/*  488:     */     }
/*  489:     */     
/*  490:     */     public Instances instancesDownBranch(int branch, Instances instances)
/*  491:     */     {
/*  492: 553 */       ReferenceInstances filteredInstances = new ReferenceInstances(instances, 1);
/*  493: 555 */       if (branch == -1) {
/*  494: 556 */         for (Instance instance : instances)
/*  495:     */         {
/*  496: 557 */           Instance inst = instance;
/*  497: 558 */           if (inst.isMissing(this.attIndex)) {
/*  498: 559 */             filteredInstances.addReference(inst);
/*  499:     */           }
/*  500:     */         }
/*  501: 562 */       } else if (branch == 0) {
/*  502: 563 */         for (Instance instance : instances)
/*  503:     */         {
/*  504: 564 */           Instance inst = instance;
/*  505: 565 */           if ((!inst.isMissing(this.attIndex)) && (inst.value(this.attIndex) < this.splitPoint)) {
/*  506: 566 */             filteredInstances.addReference(inst);
/*  507:     */           }
/*  508:     */         }
/*  509:     */       } else {
/*  510: 570 */         for (Instance instance : instances)
/*  511:     */         {
/*  512: 571 */           Instance inst = instance;
/*  513: 572 */           if ((!inst.isMissing(this.attIndex)) && (inst.value(this.attIndex) >= this.splitPoint)) {
/*  514: 573 */             filteredInstances.addReference(inst);
/*  515:     */           }
/*  516:     */         }
/*  517:     */       }
/*  518: 577 */       return filteredInstances;
/*  519:     */     }
/*  520:     */     
/*  521:     */     public String attributeString()
/*  522:     */     {
/*  523: 582 */       return LADTree.this.m_trainInstances.attribute(this.attIndex).name();
/*  524:     */     }
/*  525:     */     
/*  526:     */     public String comparisonString(int branchNum)
/*  527:     */     {
/*  528: 587 */       return (branchNum == 0 ? "< " : ">= ") + Utils.doubleToString(this.splitPoint, 3);
/*  529:     */     }
/*  530:     */     
/*  531:     */     public boolean equalTo(LADTree.Splitter compare)
/*  532:     */     {
/*  533: 593 */       if ((compare instanceof TwoWayNumericSplit))
/*  534:     */       {
/*  535: 594 */         TwoWayNumericSplit compareSame = (TwoWayNumericSplit)compare;
/*  536: 595 */         return (this.attIndex == compareSame.attIndex) && (this.splitPoint == compareSame.splitPoint);
/*  537:     */       }
/*  538: 597 */       return false;
/*  539:     */     }
/*  540:     */     
/*  541:     */     public void setChildForBranch(int branchNum, LADTree.PredictionNode childPredictor)
/*  542:     */     {
/*  543: 603 */       this.children[branchNum] = childPredictor;
/*  544:     */     }
/*  545:     */     
/*  546:     */     public LADTree.PredictionNode getChildForBranch(int branchNum)
/*  547:     */     {
/*  548: 608 */       return this.children[branchNum];
/*  549:     */     }
/*  550:     */     
/*  551:     */     public Object clone()
/*  552:     */     {
/*  553: 613 */       TwoWayNumericSplit clone = new TwoWayNumericSplit(LADTree.this, this.attIndex, this.splitPoint);
/*  554: 614 */       if (this.children[0] != null) {
/*  555: 615 */         clone.setChildForBranch(0, (LADTree.PredictionNode)this.children[0].clone());
/*  556:     */       }
/*  557: 617 */       if (this.children[1] != null) {
/*  558: 618 */         clone.setChildForBranch(1, (LADTree.PredictionNode)this.children[1].clone());
/*  559:     */       }
/*  560: 620 */       return clone;
/*  561:     */     }
/*  562:     */   }
/*  563:     */   
/*  564:     */   public void initializeClassifier(Instances instances)
/*  565:     */     throws Exception
/*  566:     */   {
/*  567: 633 */     this.m_nodesExpanded = 0;
/*  568: 634 */     this.m_examplesCounted = 0;
/*  569: 635 */     this.m_lastAddedSplitNum = 0;
/*  570:     */     
/*  571: 637 */     this.m_numOfClasses = instances.numClasses();
/*  572: 640 */     if (instances.checkForStringAttributes()) {
/*  573: 641 */       throw new Exception("Can't handle string attributes!");
/*  574:     */     }
/*  575: 643 */     if (!instances.classAttribute().isNominal()) {
/*  576: 644 */       throw new Exception("Class must be nominal!");
/*  577:     */     }
/*  578: 648 */     this.m_trainInstances = new ReferenceInstances(instances, instances.numInstances());
/*  579: 650 */     for (Instance instance : instances)
/*  580:     */     {
/*  581: 651 */       Instance inst = instance;
/*  582: 652 */       if (!inst.classIsMissing())
/*  583:     */       {
/*  584: 653 */         LADInstance adtInst = new LADInstance(inst);
/*  585: 654 */         this.m_trainInstances.addReference(adtInst);
/*  586: 655 */         adtInst.setDataset(this.m_trainInstances);
/*  587:     */       }
/*  588:     */     }
/*  589: 660 */     this.m_root = new PredictionNode(new double[this.m_numOfClasses]);
/*  590:     */     
/*  591:     */ 
/*  592: 663 */     generateStaticPotentialSplittersAndNumericIndices();
/*  593:     */   }
/*  594:     */   
/*  595:     */   public boolean next()
/*  596:     */     throws Exception
/*  597:     */   {
/*  598: 667 */     boost();
/*  599: 668 */     return true;
/*  600:     */   }
/*  601:     */   
/*  602:     */   public void done()
/*  603:     */     throws Exception
/*  604:     */   {
/*  605: 672 */     this.m_staticPotentialSplitters2way = null;
/*  606: 673 */     this.m_numericAttIndices = null;
/*  607:     */   }
/*  608:     */   
/*  609:     */   private void boost()
/*  610:     */     throws Exception
/*  611:     */   {
/*  612: 684 */     if (this.m_trainInstances == null) {
/*  613: 685 */       throw new Exception("Trying to boost with no training data");
/*  614:     */     }
/*  615: 689 */     searchForBestTest();
/*  616: 691 */     if (this.m_Debug) {
/*  617: 692 */       System.out.println("Best split found: " + this.m_search_bestSplitter.getNumOfBranches() + "-way split on " + this.m_search_bestSplitter.attributeString() + "\nBestGain = " + this.m_search_smallestLeastSquares);
/*  618:     */     }
/*  619: 699 */     if (this.m_search_bestSplitter == null) {
/*  620: 700 */       return;
/*  621:     */     }
/*  622: 704 */     for (int i = 0; i < this.m_search_bestSplitter.getNumOfBranches(); i++)
/*  623:     */     {
/*  624: 705 */       Instances applicableInstances = this.m_search_bestSplitter.instancesDownBranch(i, this.m_search_bestPathInstances);
/*  625:     */       
/*  626: 707 */       double[] predictionValues = calcPredictionValues(applicableInstances);
/*  627: 708 */       PredictionNode newPredictor = new PredictionNode(predictionValues);
/*  628: 709 */       updateWeights(applicableInstances, predictionValues);
/*  629: 710 */       this.m_search_bestSplitter.setChildForBranch(i, newPredictor);
/*  630:     */     }
/*  631: 714 */     this.m_search_bestInsertionNode.addChild(this.m_search_bestSplitter);
/*  632: 716 */     if (this.m_Debug) {
/*  633: 717 */       System.out.println("Tree is now:\n" + toString(this.m_root, 1) + "\n");
/*  634:     */     }
/*  635: 722 */     this.m_search_bestPathInstances = null;
/*  636:     */   }
/*  637:     */   
/*  638:     */   private void updateWeights(Instances instances, double[] newPredictionValues)
/*  639:     */   {
/*  640: 727 */     for (int i = 0; i < instances.numInstances(); i++) {
/*  641: 728 */       ((LADInstance)instances.instance(i)).updateWeights(newPredictionValues);
/*  642:     */     }
/*  643:     */   }
/*  644:     */   
/*  645:     */   private void generateStaticPotentialSplittersAndNumericIndices()
/*  646:     */   {
/*  647: 740 */     this.m_staticPotentialSplitters2way = new ArrayList();
/*  648: 741 */     ArrayList<Integer> numericIndices = new ArrayList();
/*  649: 743 */     for (int i = 0; i < this.m_trainInstances.numAttributes(); i++) {
/*  650: 744 */       if (i != this.m_trainInstances.classIndex()) {
/*  651: 747 */         if (this.m_trainInstances.attribute(i).isNumeric())
/*  652:     */         {
/*  653: 748 */           numericIndices.add(new Integer(i));
/*  654:     */         }
/*  655:     */         else
/*  656:     */         {
/*  657: 750 */           int numValues = this.m_trainInstances.attribute(i).numValues();
/*  658: 751 */           if (numValues == 2) {
/*  659: 752 */             this.m_staticPotentialSplitters2way.add(new TwoWayNominalSplit(i, 0));
/*  660:     */           } else {
/*  661: 754 */             for (int j = 0; j < numValues; j++) {
/*  662: 755 */               this.m_staticPotentialSplitters2way.add(new TwoWayNominalSplit(i, j));
/*  663:     */             }
/*  664:     */           }
/*  665:     */         }
/*  666:     */       }
/*  667:     */     }
/*  668: 761 */     this.m_numericAttIndices = new int[numericIndices.size()];
/*  669: 762 */     for (int i = 0; i < numericIndices.size(); i++) {
/*  670: 763 */       this.m_numericAttIndices[i] = ((Integer)numericIndices.get(i)).intValue();
/*  671:     */     }
/*  672:     */   }
/*  673:     */   
/*  674:     */   private void searchForBestTest()
/*  675:     */     throws Exception
/*  676:     */   {
/*  677: 775 */     if (this.m_Debug) {
/*  678: 776 */       System.out.println("Searching for best split...");
/*  679:     */     }
/*  680: 779 */     this.m_search_smallestLeastSquares = 0.0D;
/*  681: 780 */     searchForBestTest(this.m_root, this.m_trainInstances);
/*  682:     */   }
/*  683:     */   
/*  684:     */   private void searchForBestTest(PredictionNode currentNode, Instances instances)
/*  685:     */     throws Exception
/*  686:     */   {
/*  687: 797 */     this.m_nodesExpanded += 1;
/*  688: 798 */     this.m_examplesCounted += instances.numInstances();
/*  689:     */     
/*  690:     */ 
/*  691: 801 */     Enumeration<Splitter> e = new WekaEnumeration(this.m_staticPotentialSplitters2way);
/*  692: 802 */     while (e.hasMoreElements()) {
/*  693: 803 */       evaluateSplitter((Splitter)e.nextElement(), currentNode, instances);
/*  694:     */     }
/*  695: 806 */     if (this.m_Debug) {}
/*  696: 811 */     for (int m_numericAttIndice : this.m_numericAttIndices) {
/*  697: 812 */       evaluateNumericSplit(currentNode, instances, m_numericAttIndice);
/*  698:     */     }
/*  699: 815 */     if (currentNode.getChildren().size() == 0) {
/*  700: 816 */       return;
/*  701:     */     }
/*  702: 820 */     goDownAllPaths(currentNode, instances);
/*  703:     */   }
/*  704:     */   
/*  705:     */   private void goDownAllPaths(PredictionNode currentNode, Instances instances)
/*  706:     */     throws Exception
/*  707:     */   {
/*  708: 834 */     for (Enumeration<Splitter> e = currentNode.children(); e.hasMoreElements();)
/*  709:     */     {
/*  710: 835 */       Splitter split = (Splitter)e.nextElement();
/*  711: 836 */       for (int i = 0; i < split.getNumOfBranches(); i++) {
/*  712: 837 */         searchForBestTest(split.getChildForBranch(i), split.instancesDownBranch(i, instances));
/*  713:     */       }
/*  714:     */     }
/*  715:     */   }
/*  716:     */   
/*  717:     */   private void evaluateSplitter(Splitter split, PredictionNode currentNode, Instances instances)
/*  718:     */     throws Exception
/*  719:     */   {
/*  720: 857 */     double leastSquares = leastSquaresNonMissing(instances, split.attIndex);
/*  721: 859 */     for (int i = 0; i < split.getNumOfBranches(); i++) {
/*  722: 860 */       leastSquares -= leastSquares(split.instancesDownBranch(i, instances));
/*  723:     */     }
/*  724: 863 */     if (this.m_Debug) {
/*  725: 865 */       System.out.print(split.getNumOfBranches() + "-way split on " + split.attributeString() + " has leastSquares value of " + Utils.doubleToString(leastSquares, 3));
/*  726:     */     }
/*  727: 870 */     if (leastSquares > this.m_search_smallestLeastSquares)
/*  728:     */     {
/*  729: 871 */       if (this.m_Debug) {
/*  730: 872 */         System.out.print(" (best so far)");
/*  731:     */       }
/*  732: 874 */       this.m_search_smallestLeastSquares = leastSquares;
/*  733: 875 */       this.m_search_bestInsertionNode = currentNode;
/*  734: 876 */       this.m_search_bestSplitter = split;
/*  735: 877 */       this.m_search_bestPathInstances = instances;
/*  736:     */     }
/*  737: 879 */     if (this.m_Debug) {
/*  738: 880 */       System.out.print("\n");
/*  739:     */     }
/*  740:     */   }
/*  741:     */   
/*  742:     */   private void evaluateNumericSplit(PredictionNode currentNode, Instances instances, int attIndex)
/*  743:     */   {
/*  744: 887 */     double[] splitAndLS = findNumericSplitpointAndLS(instances, attIndex);
/*  745: 888 */     double gain = leastSquaresNonMissing(instances, attIndex) - splitAndLS[1];
/*  746: 890 */     if (this.m_Debug) {
/*  747: 892 */       System.out.print("Numeric split on " + instances.attribute(attIndex).name() + " has leastSquares value of " + Utils.doubleToString(gain, 3));
/*  748:     */     }
/*  749: 898 */     if (gain > this.m_search_smallestLeastSquares)
/*  750:     */     {
/*  751: 899 */       if (this.m_Debug) {
/*  752: 900 */         System.out.print(" (best so far)");
/*  753:     */       }
/*  754: 902 */       this.m_search_smallestLeastSquares = gain;
/*  755: 903 */       this.m_search_bestInsertionNode = currentNode;
/*  756: 904 */       this.m_search_bestSplitter = new TwoWayNumericSplit(attIndex, splitAndLS[0]);
/*  757:     */       
/*  758: 906 */       this.m_search_bestPathInstances = instances;
/*  759:     */     }
/*  760: 908 */     if (this.m_Debug) {
/*  761: 909 */       System.out.print("\n");
/*  762:     */     }
/*  763:     */   }
/*  764:     */   
/*  765:     */   private double[] findNumericSplitpointAndLS(Instances instances, int attIndex)
/*  766:     */   {
/*  767: 915 */     double allLS = leastSquares(instances);
/*  768:     */     
/*  769:     */ 
/*  770: 918 */     double[] term1L = new double[this.m_numOfClasses];
/*  771: 919 */     double[] term2L = new double[this.m_numOfClasses];
/*  772: 920 */     double[] term3L = new double[this.m_numOfClasses];
/*  773: 921 */     double[] meanNumL = new double[this.m_numOfClasses];
/*  774: 922 */     double[] term1R = new double[this.m_numOfClasses];
/*  775: 923 */     double[] term2R = new double[this.m_numOfClasses];
/*  776: 924 */     double[] term3R = new double[this.m_numOfClasses];
/*  777: 925 */     double[] meanNumR = new double[this.m_numOfClasses];
/*  778: 929 */     for (int j = 0; j < this.m_numOfClasses; j++) {
/*  779: 930 */       for (int i = 0; i < instances.numInstances(); i++)
/*  780:     */       {
/*  781: 931 */         LADInstance inst = (LADInstance)instances.instance(i);
/*  782: 932 */         double temp1 = inst.wVector[j] * inst.zVector[j];
/*  783: 933 */         term1R[j] += temp1 * inst.zVector[j];
/*  784: 934 */         term2R[j] += temp1;
/*  785: 935 */         term3R[j] += inst.wVector[j];
/*  786: 936 */         meanNumR[j] += inst.wVector[j] * inst.zVector[j];
/*  787:     */       }
/*  788:     */     }
/*  789: 944 */     double smallestLeastSquares = (1.0D / 0.0D);
/*  790: 945 */     double bestSplit = 0.0D;
/*  791:     */     
/*  792:     */ 
/*  793: 948 */     instances.sort(attIndex);
/*  794: 950 */     for (int i = 0; i < instances.numInstances() - 1; i++)
/*  795:     */     {
/*  796: 952 */       if (instances.instance(i + 1).isMissing(attIndex)) {
/*  797:     */         break;
/*  798:     */       }
/*  799:     */       boolean newSplit;
/*  800:     */       boolean newSplit;
/*  801: 955 */       if (instances.instance(i + 1).value(attIndex) > instances.instance(i).value(attIndex)) {
/*  802: 957 */         newSplit = true;
/*  803:     */       } else {
/*  804: 959 */         newSplit = false;
/*  805:     */       }
/*  806: 961 */       LADInstance inst = (LADInstance)instances.instance(i);
/*  807: 962 */       double leastSquares = 0.0D;
/*  808: 963 */       for (int j = 0; j < this.m_numOfClasses; j++)
/*  809:     */       {
/*  810: 964 */         double temp1 = inst.wVector[j] * inst.zVector[j];
/*  811: 965 */         double temp2 = temp1 * inst.zVector[j];
/*  812: 966 */         double temp3 = inst.wVector[j] * inst.zVector[j];
/*  813: 967 */         term1L[j] += temp2;
/*  814: 968 */         term2L[j] += temp1;
/*  815: 969 */         term3L[j] += inst.wVector[j];
/*  816: 970 */         term1R[j] -= temp2;
/*  817: 971 */         term2R[j] -= temp1;
/*  818: 972 */         term3R[j] -= inst.wVector[j];
/*  819: 973 */         meanNumL[j] += temp3;
/*  820: 974 */         meanNumR[j] -= temp3;
/*  821: 975 */         if (newSplit)
/*  822:     */         {
/*  823: 976 */           double meanL = meanNumL[j] / term3L[j];
/*  824: 977 */           double meanR = meanNumR[j] / term3R[j];
/*  825: 978 */           leastSquares += term1L[j] - 2.0D * meanL * term2L[j] + meanL * meanL * term3L[j];
/*  826:     */           
/*  827: 980 */           leastSquares += term1R[j] - 2.0D * meanR * term2R[j] + meanR * meanR * term3R[j];
/*  828:     */         }
/*  829:     */       }
/*  830: 984 */       if ((this.m_Debug) && (newSplit)) {
/*  831: 985 */         System.out.println(attIndex + "/" + (instances.instance(i).value(attIndex) + instances.instance(i + 1).value(attIndex)) / 2.0D + " = " + (allLS - leastSquares));
/*  832:     */       }
/*  833: 991 */       if ((newSplit) && (leastSquares < smallestLeastSquares))
/*  834:     */       {
/*  835: 992 */         bestSplit = (instances.instance(i).value(attIndex) + instances.instance(i + 1).value(attIndex)) / 2.0D;
/*  836:     */         
/*  837: 994 */         smallestLeastSquares = leastSquares;
/*  838:     */       }
/*  839:     */     }
/*  840: 997 */     double[] result = new double[2];
/*  841: 998 */     result[0] = bestSplit;
/*  842: 999 */     result[1] = (smallestLeastSquares > 0.0D ? smallestLeastSquares : 0.0D);
/*  843:1000 */     return result;
/*  844:     */   }
/*  845:     */   
/*  846:     */   private double leastSquares(Instances instances)
/*  847:     */   {
/*  848:1005 */     double numerator = 0.0D;
/*  849:1006 */     double[] classMeans = new double[this.m_numOfClasses];
/*  850:1007 */     double[] classTotals = new double[this.m_numOfClasses];
/*  851:1009 */     for (int i = 0; i < instances.numInstances(); i++)
/*  852:     */     {
/*  853:1010 */       LADInstance inst = (LADInstance)instances.instance(i);
/*  854:1011 */       for (int j = 0; j < this.m_numOfClasses; j++)
/*  855:     */       {
/*  856:1012 */         classMeans[j] += inst.zVector[j] * inst.wVector[j];
/*  857:1013 */         classTotals[j] += inst.wVector[j];
/*  858:     */       }
/*  859:     */     }
/*  860:1017 */     instances.numInstances();
/*  861:1018 */     for (int j = 0; j < this.m_numOfClasses; j++) {
/*  862:1019 */       if (classTotals[j] != 0.0D) {
/*  863:1020 */         classMeans[j] /= classTotals[j];
/*  864:     */       }
/*  865:     */     }
/*  866:1024 */     for (int i = 0; i < instances.numInstances(); i++) {
/*  867:1025 */       for (int j = 0; j < this.m_numOfClasses; j++)
/*  868:     */       {
/*  869:1026 */         LADInstance inst = (LADInstance)instances.instance(i);
/*  870:1027 */         double w = inst.wVector[j];
/*  871:1028 */         double t = inst.zVector[j] - classMeans[j];
/*  872:1029 */         numerator += w * (t * t);
/*  873:     */       }
/*  874:     */     }
/*  875:1033 */     return numerator > 0.0D ? numerator : 0.0D;
/*  876:     */   }
/*  877:     */   
/*  878:     */   private double leastSquaresNonMissing(Instances instances, int attIndex)
/*  879:     */   {
/*  880:1038 */     double numerator = 0.0D;
/*  881:1039 */     double[] classMeans = new double[this.m_numOfClasses];
/*  882:1040 */     double[] classTotals = new double[this.m_numOfClasses];
/*  883:1042 */     for (int i = 0; i < instances.numInstances(); i++)
/*  884:     */     {
/*  885:1043 */       LADInstance inst = (LADInstance)instances.instance(i);
/*  886:1044 */       for (int j = 0; j < this.m_numOfClasses; j++)
/*  887:     */       {
/*  888:1045 */         classMeans[j] += inst.zVector[j] * inst.wVector[j];
/*  889:1046 */         classTotals[j] += inst.wVector[j];
/*  890:     */       }
/*  891:     */     }
/*  892:1050 */     instances.numInstances();
/*  893:1051 */     for (int j = 0; j < this.m_numOfClasses; j++) {
/*  894:1052 */       if (classTotals[j] != 0.0D) {
/*  895:1053 */         classMeans[j] /= classTotals[j];
/*  896:     */       }
/*  897:     */     }
/*  898:1057 */     for (int i = 0; i < instances.numInstances(); i++) {
/*  899:1058 */       for (int j = 0; j < this.m_numOfClasses; j++)
/*  900:     */       {
/*  901:1059 */         LADInstance inst = (LADInstance)instances.instance(i);
/*  902:1060 */         if (!inst.isMissing(attIndex))
/*  903:     */         {
/*  904:1061 */           double w = inst.wVector[j];
/*  905:1062 */           double t = inst.zVector[j] - classMeans[j];
/*  906:1063 */           numerator += w * (t * t);
/*  907:     */         }
/*  908:     */       }
/*  909:     */     }
/*  910:1068 */     return numerator > 0.0D ? numerator : 0.0D;
/*  911:     */   }
/*  912:     */   
/*  913:     */   private double[] calcPredictionValues(Instances instances)
/*  914:     */   {
/*  915:1073 */     double[] classMeans = new double[this.m_numOfClasses];
/*  916:1074 */     double meansSum = 0.0D;
/*  917:1075 */     double multiplier = (this.m_numOfClasses - 1) / this.m_numOfClasses;
/*  918:     */     
/*  919:     */ 
/*  920:1078 */     double[] classTotals = new double[this.m_numOfClasses];
/*  921:1080 */     for (int i = 0; i < instances.numInstances(); i++)
/*  922:     */     {
/*  923:1081 */       LADInstance inst = (LADInstance)instances.instance(i);
/*  924:1082 */       for (int j = 0; j < this.m_numOfClasses; j++)
/*  925:     */       {
/*  926:1083 */         classMeans[j] += inst.zVector[j] * inst.wVector[j];
/*  927:1084 */         classTotals[j] += inst.wVector[j];
/*  928:     */       }
/*  929:     */     }
/*  930:1087 */     instances.numInstances();
/*  931:1088 */     for (int j = 0; j < this.m_numOfClasses; j++)
/*  932:     */     {
/*  933:1089 */       if (classTotals[j] != 0.0D) {
/*  934:1090 */         classMeans[j] /= classTotals[j];
/*  935:     */       }
/*  936:1092 */       meansSum += classMeans[j];
/*  937:     */     }
/*  938:1094 */     meansSum /= this.m_numOfClasses;
/*  939:1096 */     for (int j = 0; j < this.m_numOfClasses; j++) {
/*  940:1097 */       classMeans[j] = (multiplier * (classMeans[j] - meansSum));
/*  941:     */     }
/*  942:1099 */     return classMeans;
/*  943:     */   }
/*  944:     */   
/*  945:     */   public double[] distributionForInstance(Instance instance)
/*  946:     */   {
/*  947:1111 */     double[] predValues = new double[this.m_numOfClasses];
/*  948:1112 */     for (int i = 0; i < this.m_numOfClasses; i++) {
/*  949:1113 */       predValues[i] = 0.0D;
/*  950:     */     }
/*  951:1115 */     double[] distribution = predictionValuesForInstance(instance, this.m_root, predValues);
/*  952:     */     
/*  953:1117 */     double max = distribution[Utils.maxIndex(distribution)];
/*  954:1118 */     for (int i = 0; i < this.m_numOfClasses; i++) {
/*  955:1119 */       distribution[i] = Math.exp(distribution[i] - max);
/*  956:     */     }
/*  957:1121 */     double sum = Utils.sum(distribution);
/*  958:1122 */     if (sum > 0.0D) {
/*  959:1123 */       Utils.normalize(distribution, sum);
/*  960:     */     }
/*  961:1125 */     return distribution;
/*  962:     */   }
/*  963:     */   
/*  964:     */   private double[] predictionValuesForInstance(Instance inst, PredictionNode currentNode, double[] currentValues)
/*  965:     */   {
/*  966:1140 */     double[] predValues = currentNode.getValues();
/*  967:1141 */     for (int i = 0; i < this.m_numOfClasses; i++) {
/*  968:1142 */       currentValues[i] += predValues[i];
/*  969:     */     }
/*  970:1145 */     for (Enumeration<Splitter> e = currentNode.children(); e.hasMoreElements();)
/*  971:     */     {
/*  972:1146 */       Splitter split = (Splitter)e.nextElement();
/*  973:1147 */       int branch = split.branchInstanceGoesDown(inst);
/*  974:1148 */       if (branch >= 0) {
/*  975:1149 */         currentValues = predictionValuesForInstance(inst, split.getChildForBranch(branch), currentValues);
/*  976:     */       }
/*  977:     */     }
/*  978:1153 */     return currentValues;
/*  979:     */   }
/*  980:     */   
/*  981:     */   public String toString()
/*  982:     */   {
/*  983:1166 */     String className = getClass().getName();
/*  984:1167 */     if (this.m_root == null) {
/*  985:1168 */       return className + " not built yet";
/*  986:     */     }
/*  987:1170 */     return className + ":\n\n" + toString(this.m_root, 1) + "\nLegend: " + legend() + "\n#Tree size (total): " + numOfAllNodes(this.m_root) + "\n#Tree size (number of predictor nodes): " + numOfPredictionNodes(this.m_root) + "\n#Leaves (number of predictor nodes): " + numOfLeafNodes(this.m_root) + "\n#Expanded nodes: " + this.m_nodesExpanded + "\n#Processed examples: " + this.m_examplesCounted + "\n#Ratio e/n: " + this.m_examplesCounted / this.m_nodesExpanded;
/*  988:     */   }
/*  989:     */   
/*  990:     */   private String toString(PredictionNode currentNode, int level)
/*  991:     */   {
/*  992:1189 */     StringBuffer text = new StringBuffer();
/*  993:     */     
/*  994:1191 */     text.append(": ");
/*  995:1192 */     double[] predValues = currentNode.getValues();
/*  996:1193 */     for (int i = 0; i < this.m_numOfClasses; i++)
/*  997:     */     {
/*  998:1194 */       text.append(Utils.doubleToString(predValues[i], 3));
/*  999:1195 */       if (i < this.m_numOfClasses - 1) {
/* 1000:1196 */         text.append(",");
/* 1001:     */       }
/* 1002:     */     }
/* 1003:1199 */     for (Enumeration<Splitter> e = currentNode.children(); e.hasMoreElements();)
/* 1004:     */     {
/* 1005:1200 */       Splitter split = (Splitter)e.nextElement();
/* 1006:1202 */       for (int j = 0; j < split.getNumOfBranches(); j++)
/* 1007:     */       {
/* 1008:1203 */         PredictionNode child = split.getChildForBranch(j);
/* 1009:1204 */         if (child != null)
/* 1010:     */         {
/* 1011:1205 */           text.append("\n");
/* 1012:1206 */           for (int k = 0; k < level; k++) {
/* 1013:1207 */             text.append("|  ");
/* 1014:     */           }
/* 1015:1209 */           text.append("(" + split.orderAdded + ")");
/* 1016:1210 */           text.append(split.attributeString() + " " + split.comparisonString(j));
/* 1017:     */           
/* 1018:1212 */           text.append(toString(child, level + 1));
/* 1019:     */         }
/* 1020:     */       }
/* 1021:     */     }
/* 1022:1216 */     return text.toString();
/* 1023:     */   }
/* 1024:     */   
/* 1025:     */   public String graph()
/* 1026:     */     throws Exception
/* 1027:     */   {
/* 1028:1228 */     StringBuffer text = new StringBuffer();
/* 1029:1229 */     text.append("digraph ADTree {\n");
/* 1030:     */     
/* 1031:1231 */     graphTraverse(this.m_root, text, 0, 0);
/* 1032:1232 */     return text.toString() + "}\n";
/* 1033:     */   }
/* 1034:     */   
/* 1035:     */   protected void graphTraverse(PredictionNode currentNode, StringBuffer text, int splitOrder, int predOrder)
/* 1036:     */     throws Exception
/* 1037:     */   {
/* 1038:1247 */     text.append("S" + splitOrder + "P" + predOrder + " [label=\"");
/* 1039:1248 */     double[] predValues = currentNode.getValues();
/* 1040:1249 */     for (int i = 0; i < this.m_numOfClasses; i++)
/* 1041:     */     {
/* 1042:1250 */       text.append(Utils.doubleToString(predValues[i], 3));
/* 1043:1251 */       if (i < this.m_numOfClasses - 1) {
/* 1044:1252 */         text.append(",");
/* 1045:     */       }
/* 1046:     */     }
/* 1047:1255 */     if (splitOrder == 0) {
/* 1048:1256 */       text.append(" (" + legend() + ")");
/* 1049:     */     }
/* 1050:1258 */     text.append("\" shape=box style=filled]\n");
/* 1051:1259 */     for (Enumeration<Splitter> e = currentNode.children(); e.hasMoreElements();)
/* 1052:     */     {
/* 1053:1260 */       Splitter split = (Splitter)e.nextElement();
/* 1054:1261 */       text.append("S" + splitOrder + "P" + predOrder + "->" + "S" + split.orderAdded + " [style=dotted]\n");
/* 1055:     */       
/* 1056:1263 */       text.append("S" + split.orderAdded + " [label=\"" + split.orderAdded + ": " + Utils.backQuoteChars(split.attributeString()) + "\"]\n");
/* 1057:1266 */       for (int i = 0; i < split.getNumOfBranches(); i++)
/* 1058:     */       {
/* 1059:1267 */         PredictionNode child = split.getChildForBranch(i);
/* 1060:1268 */         if (child != null)
/* 1061:     */         {
/* 1062:1269 */           text.append("S" + split.orderAdded + "->" + "S" + split.orderAdded + "P" + i + " [label=\"" + Utils.backQuoteChars(split.comparisonString(i)) + "\"]\n");
/* 1063:     */           
/* 1064:     */ 
/* 1065:1272 */           graphTraverse(child, text, split.orderAdded, i);
/* 1066:     */         }
/* 1067:     */       }
/* 1068:     */     }
/* 1069:     */   }
/* 1070:     */   
/* 1071:     */   public String legend()
/* 1072:     */   {
/* 1073:1286 */     Attribute classAttribute = null;
/* 1074:1287 */     if (this.m_trainInstances == null) {
/* 1075:1288 */       return "";
/* 1076:     */     }
/* 1077:     */     try
/* 1078:     */     {
/* 1079:1291 */       classAttribute = this.m_trainInstances.classAttribute();
/* 1080:     */     }
/* 1081:     */     catch (Exception x) {}
/* 1082:1295 */     if (this.m_numOfClasses == 1) {
/* 1083:1296 */       return "-ve = " + classAttribute.value(0) + ", +ve = " + classAttribute.value(1);
/* 1084:     */     }
/* 1085:1299 */     StringBuffer text = new StringBuffer();
/* 1086:1300 */     for (int i = 0; i < this.m_numOfClasses; i++)
/* 1087:     */     {
/* 1088:1301 */       if (i > 0) {
/* 1089:1302 */         text.append(", ");
/* 1090:     */       }
/* 1091:1304 */       text.append(classAttribute.value(i));
/* 1092:     */     }
/* 1093:1306 */     return text.toString();
/* 1094:     */   }
/* 1095:     */   
/* 1096:     */   public String numOfBoostingIterationsTipText()
/* 1097:     */   {
/* 1098:1318 */     return "The number of boosting iterations to use, which determines the size of the tree.";
/* 1099:     */   }
/* 1100:     */   
/* 1101:     */   public int getNumOfBoostingIterations()
/* 1102:     */   {
/* 1103:1328 */     return this.m_boostingIterations;
/* 1104:     */   }
/* 1105:     */   
/* 1106:     */   public void setNumOfBoostingIterations(int b)
/* 1107:     */   {
/* 1108:1338 */     this.m_boostingIterations = b;
/* 1109:     */   }
/* 1110:     */   
/* 1111:     */   public Enumeration<Option> listOptions()
/* 1112:     */   {
/* 1113:1349 */     Vector<Option> newVector = new Vector(1);
/* 1114:1350 */     newVector.addElement(new Option("\tNumber of boosting iterations.\n\t(Default = 10)", "B", 1, "-B <number of boosting iterations>"));
/* 1115:     */     
/* 1116:     */ 
/* 1117:1353 */     newVector.addAll(Collections.list(super.listOptions()));
/* 1118:     */     
/* 1119:1355 */     return newVector.elements();
/* 1120:     */   }
/* 1121:     */   
/* 1122:     */   public void setOptions(String[] options)
/* 1123:     */     throws Exception
/* 1124:     */   {
/* 1125:1372 */     String bString = Utils.getOption('B', options);
/* 1126:1373 */     if (bString.length() != 0) {
/* 1127:1374 */       setNumOfBoostingIterations(Integer.parseInt(bString));
/* 1128:     */     }
/* 1129:1377 */     super.setOptions(options);
/* 1130:     */     
/* 1131:1379 */     Utils.checkForRemainingOptions(options);
/* 1132:     */   }
/* 1133:     */   
/* 1134:     */   public String[] getOptions()
/* 1135:     */   {
/* 1136:1390 */     ArrayList<String> options = new ArrayList();
/* 1137:     */     
/* 1138:1392 */     options.add("-B");
/* 1139:1393 */     options.add("" + getNumOfBoostingIterations());
/* 1140:     */     
/* 1141:1395 */     Collections.addAll(options, super.getOptions());
/* 1142:     */     
/* 1143:1397 */     return (String[])options.toArray(new String[0]);
/* 1144:     */   }
/* 1145:     */   
/* 1146:     */   public double measureTreeSize()
/* 1147:     */   {
/* 1148:1409 */     return numOfAllNodes(this.m_root);
/* 1149:     */   }
/* 1150:     */   
/* 1151:     */   public double measureNumLeaves()
/* 1152:     */   {
/* 1153:1419 */     return numOfPredictionNodes(this.m_root);
/* 1154:     */   }
/* 1155:     */   
/* 1156:     */   public double measureNumPredictionLeaves()
/* 1157:     */   {
/* 1158:1429 */     return numOfLeafNodes(this.m_root);
/* 1159:     */   }
/* 1160:     */   
/* 1161:     */   public double measureNodesExpanded()
/* 1162:     */   {
/* 1163:1439 */     return this.m_nodesExpanded;
/* 1164:     */   }
/* 1165:     */   
/* 1166:     */   public double measureExamplesCounted()
/* 1167:     */   {
/* 1168:1449 */     return this.m_examplesCounted;
/* 1169:     */   }
/* 1170:     */   
/* 1171:     */   public Enumeration<String> enumerateMeasures()
/* 1172:     */   {
/* 1173:1460 */     Vector<String> newVector = new Vector(5);
/* 1174:1461 */     newVector.addElement("measureTreeSize");
/* 1175:1462 */     newVector.addElement("measureNumLeaves");
/* 1176:1463 */     newVector.addElement("measureNumPredictionLeaves");
/* 1177:1464 */     newVector.addElement("measureNodesExpanded");
/* 1178:1465 */     newVector.addElement("measureExamplesCounted");
/* 1179:1466 */     return newVector.elements();
/* 1180:     */   }
/* 1181:     */   
/* 1182:     */   public double getMeasure(String additionalMeasureName)
/* 1183:     */   {
/* 1184:1479 */     if (additionalMeasureName.equalsIgnoreCase("measureTreeSize")) {
/* 1185:1480 */       return measureTreeSize();
/* 1186:     */     }
/* 1187:1481 */     if (additionalMeasureName.equalsIgnoreCase("measureNodesExpanded")) {
/* 1188:1482 */       return measureNodesExpanded();
/* 1189:     */     }
/* 1190:1483 */     if (additionalMeasureName.equalsIgnoreCase("measureNumLeaves")) {
/* 1191:1484 */       return measureNumLeaves();
/* 1192:     */     }
/* 1193:1485 */     if (additionalMeasureName.equalsIgnoreCase("measureNumPredictionLeaves")) {
/* 1194:1487 */       return measureNumPredictionLeaves();
/* 1195:     */     }
/* 1196:1488 */     if (additionalMeasureName.equalsIgnoreCase("measureExamplesCounted")) {
/* 1197:1489 */       return measureExamplesCounted();
/* 1198:     */     }
/* 1199:1491 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (ADTree)");
/* 1200:     */   }
/* 1201:     */   
/* 1202:     */   protected int numOfPredictionNodes(PredictionNode root)
/* 1203:     */   {
/* 1204:1504 */     int numSoFar = 0;
/* 1205:     */     Enumeration<Splitter> e;
/* 1206:1505 */     if (root != null)
/* 1207:     */     {
/* 1208:1506 */       numSoFar++;
/* 1209:1507 */       for (e = root.children(); e.hasMoreElements();)
/* 1210:     */       {
/* 1211:1508 */         Splitter split = (Splitter)e.nextElement();
/* 1212:1509 */         for (int i = 0; i < split.getNumOfBranches(); i++) {
/* 1213:1510 */           numSoFar += numOfPredictionNodes(split.getChildForBranch(i));
/* 1214:     */         }
/* 1215:     */       }
/* 1216:     */     }
/* 1217:1514 */     return numSoFar;
/* 1218:     */   }
/* 1219:     */   
/* 1220:     */   protected int numOfLeafNodes(PredictionNode root)
/* 1221:     */   {
/* 1222:1525 */     int numSoFar = 0;
/* 1223:     */     Enumeration<Splitter> e;
/* 1224:1526 */     if (root.getChildren().size() > 0) {
/* 1225:1527 */       for (e = root.children(); e.hasMoreElements();)
/* 1226:     */       {
/* 1227:1528 */         Splitter split = (Splitter)e.nextElement();
/* 1228:1529 */         for (int i = 0; i < split.getNumOfBranches(); i++) {
/* 1229:1530 */           numSoFar += numOfLeafNodes(split.getChildForBranch(i));
/* 1230:     */         }
/* 1231:     */       }
/* 1232:     */     } else {
/* 1233:1534 */       numSoFar = 1;
/* 1234:     */     }
/* 1235:1536 */     return numSoFar;
/* 1236:     */   }
/* 1237:     */   
/* 1238:     */   protected int numOfAllNodes(PredictionNode root)
/* 1239:     */   {
/* 1240:1547 */     int numSoFar = 0;
/* 1241:     */     Enumeration<Splitter> e;
/* 1242:1548 */     if (root != null)
/* 1243:     */     {
/* 1244:1549 */       numSoFar++;
/* 1245:1550 */       for (e = root.children(); e.hasMoreElements();)
/* 1246:     */       {
/* 1247:1551 */         numSoFar++;
/* 1248:1552 */         Splitter split = (Splitter)e.nextElement();
/* 1249:1553 */         for (int i = 0; i < split.getNumOfBranches(); i++) {
/* 1250:1554 */           numSoFar += numOfAllNodes(split.getChildForBranch(i));
/* 1251:     */         }
/* 1252:     */       }
/* 1253:     */     }
/* 1254:1558 */     return numSoFar;
/* 1255:     */   }
/* 1256:     */   
/* 1257:     */   public void buildClassifier(Instances instances)
/* 1258:     */     throws Exception
/* 1259:     */   {
/* 1260:1573 */     initializeClassifier(instances);
/* 1261:1576 */     for (int T = 0; T < this.m_boostingIterations; T++) {
/* 1262:1577 */       boost();
/* 1263:     */     }
/* 1264:     */   }
/* 1265:     */   
/* 1266:     */   public int predictiveError(Instances test)
/* 1267:     */   {
/* 1268:1582 */     int error = 0;
/* 1269:1583 */     for (int i = test.numInstances() - 1; i >= 0; i--)
/* 1270:     */     {
/* 1271:1584 */       Instance inst = test.instance(i);
/* 1272:     */       try
/* 1273:     */       {
/* 1274:1586 */         if (classifyInstance(inst) != inst.classValue()) {
/* 1275:1587 */           error++;
/* 1276:     */         }
/* 1277:     */       }
/* 1278:     */       catch (Exception e)
/* 1279:     */       {
/* 1280:1590 */         error++;
/* 1281:     */       }
/* 1282:     */     }
/* 1283:1593 */     return error;
/* 1284:     */   }
/* 1285:     */   
/* 1286:     */   public void merge(LADTree mergeWith)
/* 1287:     */     throws Exception
/* 1288:     */   {
/* 1289:1607 */     if ((this.m_root == null) || (mergeWith.m_root == null)) {
/* 1290:1608 */       throw new Exception("Trying to merge an uninitialized tree");
/* 1291:     */     }
/* 1292:1610 */     if (this.m_numOfClasses != mergeWith.m_numOfClasses) {
/* 1293:1611 */       throw new Exception("Trees not suitable for merge - different sized prediction nodes");
/* 1294:     */     }
/* 1295:1614 */     this.m_root.merge(mergeWith.m_root);
/* 1296:     */   }
/* 1297:     */   
/* 1298:     */   public int graphType()
/* 1299:     */   {
/* 1300:1624 */     return 1;
/* 1301:     */   }
/* 1302:     */   
/* 1303:     */   public String getRevision()
/* 1304:     */   {
/* 1305:1634 */     return RevisionUtils.extract("$Revision: 10887 $");
/* 1306:     */   }
/* 1307:     */   
/* 1308:     */   public Capabilities getCapabilities()
/* 1309:     */   {
/* 1310:1644 */     Capabilities result = super.getCapabilities();
/* 1311:1645 */     result.disableAll();
/* 1312:     */     
/* 1313:     */ 
/* 1314:1648 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 1315:1649 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 1316:1650 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 1317:1651 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 1318:     */     
/* 1319:     */ 
/* 1320:1654 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 1321:1655 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 1322:     */     
/* 1323:1657 */     return result;
/* 1324:     */   }
/* 1325:     */   
/* 1326:     */   public static void main(String[] argv)
/* 1327:     */   {
/* 1328:1666 */     runClassifier(new LADTree(), argv);
/* 1329:     */   }
/* 1330:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.LADTree
 * JD-Core Version:    0.7.0.1
 */