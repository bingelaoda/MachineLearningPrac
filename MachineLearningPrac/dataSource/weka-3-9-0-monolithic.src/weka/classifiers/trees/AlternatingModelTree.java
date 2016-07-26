/*    1:     */ package weka.classifiers.trees;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.ArrayList;
/*    6:     */ import java.util.Collections;
/*    7:     */ import java.util.Enumeration;
/*    8:     */ import java.util.LinkedList;
/*    9:     */ import java.util.List;
/*   10:     */ import java.util.Queue;
/*   11:     */ import java.util.Vector;
/*   12:     */ import weka.classifiers.AbstractClassifier;
/*   13:     */ import weka.classifiers.Classifier;
/*   14:     */ import weka.classifiers.IterativeClassifier;
/*   15:     */ import weka.classifiers.functions.SimpleLinearRegression;
/*   16:     */ import weka.classifiers.rules.ZeroR;
/*   17:     */ import weka.core.Attribute;
/*   18:     */ import weka.core.Capabilities;
/*   19:     */ import weka.core.Capabilities.Capability;
/*   20:     */ import weka.core.DenseInstance;
/*   21:     */ import weka.core.Drawable;
/*   22:     */ import weka.core.Instance;
/*   23:     */ import weka.core.Instances;
/*   24:     */ import weka.core.Option;
/*   25:     */ import weka.core.RevisionHandler;
/*   26:     */ import weka.core.RevisionUtils;
/*   27:     */ import weka.core.TechnicalInformation;
/*   28:     */ import weka.core.TechnicalInformation.Field;
/*   29:     */ import weka.core.TechnicalInformation.Type;
/*   30:     */ import weka.core.TechnicalInformationHandler;
/*   31:     */ import weka.core.Utils;
/*   32:     */ import weka.core.WeightedInstancesHandler;
/*   33:     */ import weka.filters.Filter;
/*   34:     */ import weka.filters.supervised.attribute.NominalToBinary;
/*   35:     */ import weka.filters.unsupervised.attribute.RemoveUseless;
/*   36:     */ 
/*   37:     */ public class AlternatingModelTree
/*   38:     */   extends AbstractClassifier
/*   39:     */   implements WeightedInstancesHandler, IterativeClassifier, Drawable, TechnicalInformationHandler, RevisionHandler
/*   40:     */ {
/*   41:     */   static final long serialVersionUID = -7716785668198681288L;
/*   42:     */   protected Instances m_Data;
/*   43:     */   protected int m_NumberOfIterations;
/*   44:     */   protected double m_Shrinkage;
/*   45:     */   protected boolean m_BuildDecisionTree;
/*   46:     */   protected ArrayList<PredictionNode> m_PredictionNodes;
/*   47:     */   private NominalToBinary m_nominalToBinary;
/*   48:     */   private RemoveUseless m_removeUseless;
/*   49:     */   
/*   50:     */   public AlternatingModelTree()
/*   51:     */   {
/*   52:  91 */     this.m_NumberOfIterations = 10;
/*   53:     */     
/*   54:     */ 
/*   55:  94 */     this.m_Shrinkage = 1.0D;
/*   56:     */   }
/*   57:     */   
/*   58:     */   public String globalInfo()
/*   59:     */   {
/*   60: 115 */     return "Grows an alternating model tree by minimising squared error. Nominal attributes are converted to binary numeric ones before the tree is built, using the supervised version of NominalToBinary.\n\nFor more information see\n\n" + getTechnicalInformation();
/*   61:     */   }
/*   62:     */   
/*   63:     */   public TechnicalInformation getTechnicalInformation()
/*   64:     */   {
/*   65: 133 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*   66: 134 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Eibe Frank, Michael Mayo and Stefan Kramer");
/*   67: 135 */     result.setValue(TechnicalInformation.Field.TITLE, "Alternating Model Trees");
/*   68: 136 */     result.setValue(TechnicalInformation.Field.YEAR, "2015");
/*   69: 137 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "Proceedings of the ACM Symposium on Applied Computing, Data Mining Track");
/*   70: 138 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "ACM Press");
/*   71:     */     
/*   72: 140 */     return result;
/*   73:     */   }
/*   74:     */   
/*   75:     */   public Enumeration<Option> listOptions()
/*   76:     */   {
/*   77: 150 */     Vector<Option> newVector = new Vector(3);
/*   78:     */     
/*   79: 152 */     newVector.addElement(new Option("\tSet the number of iterations to perform. (default 10).", "I", 1, "-I <number of iterations>"));
/*   80:     */     
/*   81:     */ 
/*   82:     */ 
/*   83:     */ 
/*   84: 157 */     newVector.addElement(new Option("\tSet shrinkage parameter (default 1.0).", "H", 1, "-H <double>"));
/*   85:     */     
/*   86:     */ 
/*   87:     */ 
/*   88:     */ 
/*   89: 162 */     newVector.addElement(new Option("\tBuild a decision tree instead of an alternating tree.", "B", 0, "-B"));
/*   90:     */     
/*   91:     */ 
/*   92:     */ 
/*   93:     */ 
/*   94: 167 */     newVector.addAll(Collections.list(super.listOptions()));
/*   95:     */     
/*   96: 169 */     return newVector.elements();
/*   97:     */   }
/*   98:     */   
/*   99:     */   public String[] getOptions()
/*  100:     */   {
/*  101: 179 */     Vector<String> options = new Vector();
/*  102:     */     
/*  103: 181 */     Collections.addAll(options, super.getOptions());
/*  104:     */     
/*  105: 183 */     options.add("-I");
/*  106: 184 */     options.add("" + getNumberOfIterations());
/*  107:     */     
/*  108: 186 */     options.add("-H");
/*  109: 187 */     options.add("" + getShrinkage());
/*  110: 189 */     if (getBuildDecisionTree()) {
/*  111: 190 */       options.add("-B");
/*  112:     */     }
/*  113: 193 */     return (String[])options.toArray(new String[0]);
/*  114:     */   }
/*  115:     */   
/*  116:     */   public void setOptions(String[] options)
/*  117:     */     throws Exception
/*  118:     */   {
/*  119: 228 */     super.setOptions(options);
/*  120: 229 */     String numIterationsString = Utils.getOption('I', options);
/*  121: 230 */     if (numIterationsString.length() != 0) {
/*  122: 231 */       this.m_NumberOfIterations = Integer.parseInt(numIterationsString);
/*  123:     */     } else {
/*  124: 233 */       this.m_NumberOfIterations = 10;
/*  125:     */     }
/*  126: 235 */     String shrinkageString = Utils.getOption('H', options);
/*  127: 236 */     if (shrinkageString.length() != 0) {
/*  128: 237 */       setShrinkage(new Double(shrinkageString).doubleValue());
/*  129:     */     } else {
/*  130: 239 */       setShrinkage(1.0D);
/*  131:     */     }
/*  132: 241 */     setBuildDecisionTree(Utils.getFlag('B', options));
/*  133:     */     
/*  134: 243 */     Utils.checkForRemainingOptions(options);
/*  135:     */   }
/*  136:     */   
/*  137:     */   public String numberOfIterationsTipText()
/*  138:     */   {
/*  139: 253 */     return "Sets the number of iterations to perform.";
/*  140:     */   }
/*  141:     */   
/*  142:     */   public int getNumberOfIterations()
/*  143:     */   {
/*  144: 263 */     return this.m_NumberOfIterations;
/*  145:     */   }
/*  146:     */   
/*  147:     */   public void setNumberOfIterations(int newNumberOfIterations)
/*  148:     */   {
/*  149: 273 */     this.m_NumberOfIterations = newNumberOfIterations;
/*  150:     */   }
/*  151:     */   
/*  152:     */   public String shrinkageTipText()
/*  153:     */   {
/*  154: 282 */     return "The value of the shrinkage parameter.";
/*  155:     */   }
/*  156:     */   
/*  157:     */   public double getShrinkage()
/*  158:     */   {
/*  159: 292 */     return this.m_Shrinkage;
/*  160:     */   }
/*  161:     */   
/*  162:     */   public void setShrinkage(double newShrinkage)
/*  163:     */   {
/*  164: 302 */     this.m_Shrinkage = newShrinkage;
/*  165:     */   }
/*  166:     */   
/*  167:     */   public String buildDecisionTreeTipText()
/*  168:     */   {
/*  169: 311 */     return "Set to true if a decision tree is to be built.";
/*  170:     */   }
/*  171:     */   
/*  172:     */   public boolean getBuildDecisionTree()
/*  173:     */   {
/*  174: 321 */     return this.m_BuildDecisionTree;
/*  175:     */   }
/*  176:     */   
/*  177:     */   public void setBuildDecisionTree(boolean newBuildDecisionTree)
/*  178:     */   {
/*  179: 331 */     this.m_BuildDecisionTree = newBuildDecisionTree;
/*  180:     */   }
/*  181:     */   
/*  182:     */   public Capabilities getCapabilities()
/*  183:     */   {
/*  184: 340 */     Capabilities result = super.getCapabilities();
/*  185: 341 */     result.disableAll();
/*  186:     */     
/*  187:     */ 
/*  188: 344 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  189: 345 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  190: 346 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  191: 347 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  192:     */     
/*  193:     */ 
/*  194: 350 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  195: 351 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  196: 352 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  197:     */     
/*  198: 354 */     return result;
/*  199:     */   }
/*  200:     */   
/*  201:     */   protected class SplitInfo
/*  202:     */     implements Serializable
/*  203:     */   {
/*  204: 363 */     protected int m_AttributeIndex = -1;
/*  205: 366 */     protected double m_Split = -1.797693134862316E+308D;
/*  206: 369 */     protected double m_Worth = -1.797693134862316E+308D;
/*  207:     */     
/*  208:     */     protected SplitInfo() {}
/*  209:     */     
/*  210:     */     public String toString()
/*  211:     */     {
/*  212: 376 */       return AlternatingModelTree.this.m_Data.attribute(this.m_AttributeIndex).name() + " " + this.m_Split + " " + this.m_Worth;
/*  213:     */     }
/*  214:     */   }
/*  215:     */   
/*  216:     */   protected class SplitterNode
/*  217:     */     implements Serializable
/*  218:     */   {
/*  219:     */     protected int m_AttributeIndex;
/*  220:     */     protected double m_Split;
/*  221:     */     protected AlternatingModelTree.PredictionNode m_Left;
/*  222:     */     protected AlternatingModelTree.PredictionNode m_Right;
/*  223:     */     protected AlternatingModelTree.PredictionNode m_Missing;
/*  224:     */     
/*  225:     */     protected SplitterNode() {}
/*  226:     */     
/*  227:     */     protected String toString(String prefix)
/*  228:     */     {
/*  229: 402 */       StringBuilder sb = new StringBuilder();
/*  230:     */       
/*  231: 404 */       sb.append(prefix + AlternatingModelTree.this.m_Data.attribute(this.m_AttributeIndex).name() + " <= " + this.m_Split + "\n");
/*  232:     */       
/*  233: 406 */       sb.append(this.m_Left.toString(prefix + "  | "));
/*  234: 407 */       sb.append(prefix + AlternatingModelTree.this.m_Data.attribute(this.m_AttributeIndex).name() + " > " + this.m_Split + "\n");
/*  235:     */       
/*  236: 409 */       sb.append(this.m_Right.toString(prefix + "  | "));
/*  237: 410 */       sb.append(prefix + AlternatingModelTree.this.m_Data.attribute(this.m_AttributeIndex).name() + " = ?\n");
/*  238: 411 */       sb.append(this.m_Missing.toString(prefix + "  | "));
/*  239: 412 */       return sb.toString();
/*  240:     */     }
/*  241:     */   }
/*  242:     */   
/*  243:     */   protected void calculateResiduals(Classifier c, int[] indices, double shrinkage)
/*  244:     */     throws Exception
/*  245:     */   {
/*  246: 423 */     for (int i = 0; i < indices.length; i++)
/*  247:     */     {
/*  248: 424 */       Instance inst = this.m_Data.instance(indices[i]);
/*  249: 425 */       inst.setClassValue(inst.classValue() - shrinkage * c.classifyInstance(this.m_Data.instance(indices[i])));
/*  250:     */     }
/*  251:     */   }
/*  252:     */   
/*  253:     */   protected class PredictionNode
/*  254:     */     implements Serializable
/*  255:     */   {
/*  256: 436 */     protected Classifier m_Model = null;
/*  257:     */     protected List<AlternatingModelTree.SplitterNode> m_Successors;
/*  258:     */     protected int[] m_Indices;
/*  259:     */     protected int m_Size;
/*  260:     */     
/*  261:     */     protected String toString(String prefix)
/*  262:     */     {
/*  263: 452 */       StringBuilder sb = new StringBuilder();
/*  264: 453 */       sb.append(prefix);
/*  265: 454 */       if ((this.m_Model instanceof ZeroR)) {
/*  266: 455 */         sb.append("Pred = " + this.m_Model.toString().replaceAll("ZeroR predicts class value: ", "") + " (" + this.m_Size + ")\n");
/*  267:     */       } else {
/*  268: 458 */         sb.append("Pred = " + this.m_Model.toString().replaceAll("Linear(.*\\n)", "").replaceAll("Predicting", " :").replaceAll("if attribute value is missing.", "for ?").replaceAll("(\\r|\\n)", "") + " (" + this.m_Size + ")\n");
/*  269:     */       }
/*  270: 463 */       for (AlternatingModelTree.SplitterNode splitter : this.m_Successors) {
/*  271: 464 */         sb.append(splitter.toString(prefix));
/*  272:     */       }
/*  273: 466 */       return sb.toString();
/*  274:     */     }
/*  275:     */     
/*  276:     */     protected PredictionNode(Instances data)
/*  277:     */       throws Exception
/*  278:     */     {
/*  279: 475 */       int[] indices = new int[data.numInstances()];
/*  280: 476 */       for (int i = 0; i < indices.length; i++) {
/*  281: 477 */         indices[i] = i;
/*  282:     */       }
/*  283: 479 */       this.m_Indices = indices;
/*  284:     */       
/*  285:     */ 
/*  286: 482 */       this.m_Model = new ZeroR();
/*  287: 483 */       this.m_Model.buildClassifier(data);
/*  288:     */       
/*  289:     */ 
/*  290: 486 */       AlternatingModelTree.this.calculateResiduals(this.m_Model, indices, 1.0D);
/*  291:     */       
/*  292:     */ 
/*  293: 489 */       this.m_Size = this.m_Indices.length;
/*  294:     */       
/*  295:     */ 
/*  296: 492 */       this.m_Successors = new LinkedList();
/*  297:     */     }
/*  298:     */     
/*  299:     */     protected PredictionNode(int[] indices)
/*  300:     */       throws Exception
/*  301:     */     {
/*  302: 501 */       this.m_Indices = indices;
/*  303:     */       
/*  304:     */ 
/*  305: 504 */       this.m_Model = buildModel(getData(indices));
/*  306:     */       
/*  307:     */ 
/*  308: 507 */       AlternatingModelTree.this.calculateResiduals(this.m_Model, indices, AlternatingModelTree.this.m_Shrinkage);
/*  309:     */       
/*  310:     */ 
/*  311: 510 */       this.m_Size = this.m_Indices.length;
/*  312:     */       
/*  313:     */ 
/*  314: 513 */       this.m_Successors = new LinkedList();
/*  315:     */     }
/*  316:     */     
/*  317:     */     protected Instances getData(int[] indices)
/*  318:     */     {
/*  319: 522 */       Instances data = new Instances(AlternatingModelTree.this.m_Data, 0);
/*  320: 523 */       for (int i = 0; i < indices.length; i++) {
/*  321: 524 */         data.add(AlternatingModelTree.this.m_Data.instance(indices[i]));
/*  322:     */       }
/*  323: 526 */       return data;
/*  324:     */     }
/*  325:     */     
/*  326:     */     protected Classifier buildModel(Instances data)
/*  327:     */       throws Exception
/*  328:     */     {
/*  329: 535 */       if (data.numInstances() == 0)
/*  330:     */       {
/*  331: 536 */         ZeroR c = new ZeroR();
/*  332: 537 */         c.buildClassifier(data);
/*  333: 538 */         return c;
/*  334:     */       }
/*  335: 540 */       SimpleLinearRegression c = new SimpleLinearRegression();
/*  336: 541 */       c.setSuppressErrorMessage(true);
/*  337: 542 */       c.setDoNotCheckCapabilities(true);
/*  338: 543 */       c.buildClassifier(data);
/*  339: 544 */       return c;
/*  340:     */     }
/*  341:     */     
/*  342:     */     protected double evaluateModel(Instances data)
/*  343:     */       throws Exception
/*  344:     */     {
/*  345: 554 */       Classifier c = buildModel(data);
/*  346: 555 */       double SSE = 0.0D;
/*  347: 556 */       for (Instance inst : data)
/*  348:     */       {
/*  349: 557 */         double diff = inst.classValue() - AlternatingModelTree.this.m_Shrinkage * c.classifyInstance(inst);
/*  350: 558 */         SSE += inst.weight() * diff * diff;
/*  351:     */       }
/*  352: 560 */       return SSE;
/*  353:     */     }
/*  354:     */     
/*  355:     */     protected AlternatingModelTree.SplitInfo evaluateNodeExpansion()
/*  356:     */       throws Exception
/*  357:     */     {
/*  358: 568 */       if (AlternatingModelTree.this.m_Debug) {
/*  359: 569 */         System.out.println(toString(""));
/*  360:     */       }
/*  361: 573 */       if (this.m_Indices.length == 0) {
/*  362: 574 */         return null;
/*  363:     */       }
/*  364: 578 */       if ((AlternatingModelTree.this.m_BuildDecisionTree) && (this.m_Successors.size() >= 1)) {
/*  365: 579 */         return null;
/*  366:     */       }
/*  367: 583 */       AlternatingModelTree.SplitInfo split = new AlternatingModelTree.SplitInfo(AlternatingModelTree.this);
/*  368:     */       
/*  369:     */ 
/*  370: 586 */       double currentSSE = 0.0D;
/*  371: 587 */       for (int j = 0; j < this.m_Indices.length; j++)
/*  372:     */       {
/*  373: 588 */         Instance inst = AlternatingModelTree.this.m_Data.instance(this.m_Indices[j]);
/*  374: 589 */         currentSSE += inst.weight() * inst.classValue() * inst.classValue();
/*  375:     */       }
/*  376: 593 */       if (AlternatingModelTree.this.m_Debug) {
/*  377: 594 */         System.err.println("Current SSE: " + currentSSE);
/*  378:     */       }
/*  379: 598 */       for (int attIndex = 0; attIndex < AlternatingModelTree.this.m_Data.numAttributes(); attIndex++) {
/*  380: 601 */         if (attIndex != AlternatingModelTree.this.m_Data.classIndex())
/*  381:     */         {
/*  382: 603 */           if (AlternatingModelTree.this.m_Debug) {
/*  383: 604 */             System.err.println(AlternatingModelTree.this.m_Data.attribute(attIndex));
/*  384:     */           }
/*  385: 608 */           double[] vals = new double[this.m_Indices.length];
/*  386: 609 */           for (int j = 0; j < this.m_Indices.length; j++) {
/*  387: 610 */             vals[j] = AlternatingModelTree.this.m_Data.instance(this.m_Indices[j]).value(attIndex);
/*  388:     */           }
/*  389: 614 */           double median = Utils.kthSmallestValue(vals, vals.length / 2);
/*  390: 616 */           if (AlternatingModelTree.this.m_Debug) {
/*  391: 617 */             System.err.println("median: " + median);
/*  392:     */           }
/*  393: 621 */           ArrayList<Integer> firstSubset = new ArrayList(vals.length);
/*  394: 622 */           ArrayList<Integer> secondSubset = new ArrayList(vals.length);
/*  395: 623 */           ArrayList<Integer> missingSubset = new ArrayList(vals.length);
/*  396: 624 */           for (int j = 0; j < vals.length; j++) {
/*  397: 625 */             if (Utils.isMissingValue(vals[j])) {
/*  398: 626 */               missingSubset.add(Integer.valueOf(this.m_Indices[j]));
/*  399: 627 */             } else if (vals[j] <= median) {
/*  400: 628 */               firstSubset.add(Integer.valueOf(this.m_Indices[j]));
/*  401:     */             } else {
/*  402: 630 */               secondSubset.add(Integer.valueOf(this.m_Indices[j]));
/*  403:     */             }
/*  404:     */           }
/*  405: 633 */           if (((firstSubset.size() != 0) || (missingSubset.size() != 0)) && ((secondSubset.size() != 0) || (missingSubset.size() != 0)) && ((secondSubset.size() != 0) || (firstSubset.size() != 0)))
/*  406:     */           {
/*  407: 640 */             double firstSSE = evaluateModel(getData(toIntArray(firstSubset)));
/*  408: 641 */             double secondSSE = evaluateModel(getData(toIntArray(secondSubset)));
/*  409: 642 */             double missingSSE = evaluateModel(getData(toIntArray(missingSubset)));
/*  410: 643 */             double errorReduction = currentSSE - (firstSSE + secondSSE + missingSSE);
/*  411: 644 */             if (AlternatingModelTree.this.m_Debug)
/*  412:     */             {
/*  413: 645 */               System.err.println("firstSSE " + firstSSE);
/*  414: 646 */               System.err.println("secondSSE " + secondSSE);
/*  415: 647 */               System.err.println("missingSSE " + missingSSE);
/*  416: 648 */               System.err.println("errorReduction " + errorReduction);
/*  417:     */             }
/*  418: 652 */             if (errorReduction > split.m_Worth)
/*  419:     */             {
/*  420: 653 */               split.m_Worth = errorReduction;
/*  421: 654 */               split.m_AttributeIndex = attIndex;
/*  422: 655 */               split.m_Split = median;
/*  423:     */             }
/*  424:     */           }
/*  425:     */         }
/*  426:     */       }
/*  427: 661 */       if (split.m_AttributeIndex < 0) {
/*  428: 662 */         return null;
/*  429:     */       }
/*  430: 665 */       if (AlternatingModelTree.this.m_Debug) {
/*  431: 666 */         System.err.println(split);
/*  432:     */       }
/*  433: 669 */       return split;
/*  434:     */     }
/*  435:     */     
/*  436:     */     protected AlternatingModelTree.SplitterNode expandNode(AlternatingModelTree.SplitInfo split)
/*  437:     */       throws Exception
/*  438:     */     {
/*  439: 678 */       AlternatingModelTree.SplitterNode splitterNode = new AlternatingModelTree.SplitterNode(AlternatingModelTree.this);
/*  440: 679 */       splitterNode.m_Split = split.m_Split;
/*  441: 680 */       splitterNode.m_AttributeIndex = split.m_AttributeIndex;
/*  442:     */       
/*  443:     */ 
/*  444: 683 */       ArrayList<Integer> leftIndices = new ArrayList();
/*  445: 684 */       ArrayList<Integer> rightIndices = new ArrayList();
/*  446: 685 */       ArrayList<Integer> missingIndices = new ArrayList();
/*  447: 688 */       for (int i = 0; i < this.m_Indices.length; i++) {
/*  448: 689 */         if (AlternatingModelTree.this.m_Data.instance(this.m_Indices[i]).isMissing(split.m_AttributeIndex)) {
/*  449: 690 */           missingIndices.add(Integer.valueOf(this.m_Indices[i]));
/*  450: 692 */         } else if (AlternatingModelTree.this.m_Data.instance(this.m_Indices[i]).value(split.m_AttributeIndex) <= split.m_Split) {
/*  451: 693 */           leftIndices.add(Integer.valueOf(this.m_Indices[i]));
/*  452:     */         } else {
/*  453: 695 */           rightIndices.add(Integer.valueOf(this.m_Indices[i]));
/*  454:     */         }
/*  455:     */       }
/*  456: 701 */       splitterNode.m_Left = new PredictionNode(AlternatingModelTree.this, toIntArray(leftIndices));
/*  457: 702 */       splitterNode.m_Right = new PredictionNode(AlternatingModelTree.this, toIntArray(rightIndices));
/*  458: 703 */       splitterNode.m_Missing = new PredictionNode(AlternatingModelTree.this, toIntArray(missingIndices));
/*  459:     */       
/*  460:     */ 
/*  461: 706 */       this.m_Successors.add(splitterNode);
/*  462:     */       
/*  463:     */ 
/*  464: 709 */       return splitterNode;
/*  465:     */     }
/*  466:     */     
/*  467:     */     protected int[] toIntArray(ArrayList<Integer> al)
/*  468:     */     {
/*  469: 717 */       int[] arr = new int[al.size()];
/*  470: 718 */       int index = 0;
/*  471: 719 */       for (Integer i : al) {
/*  472: 720 */         arr[(index++)] = i.intValue();
/*  473:     */       }
/*  474: 722 */       return arr;
/*  475:     */     }
/*  476:     */   }
/*  477:     */   
/*  478:     */   public int graphType()
/*  479:     */   {
/*  480: 733 */     return 1;
/*  481:     */   }
/*  482:     */   
/*  483:     */   public String graph()
/*  484:     */     throws Exception
/*  485:     */   {
/*  486: 745 */     StringBuffer text = new StringBuffer();
/*  487: 746 */     text.append("digraph AMTree {\n");
/*  488: 747 */     graphTraverse((PredictionNode)this.m_PredictionNodes.get(0), text, "P");
/*  489: 748 */     return text.toString() + "}\n";
/*  490:     */   }
/*  491:     */   
/*  492:     */   protected void graphTraverse(PredictionNode currentNode, StringBuffer text, String id)
/*  493:     */     throws Exception
/*  494:     */   {
/*  495: 761 */     text.append(id + " [label=\"");
/*  496: 762 */     if ((currentNode.m_Model instanceof ZeroR)) {
/*  497: 763 */       text.append(currentNode.m_Model.toString().replaceAll("ZeroR predicts class value: ", "") + " (" + currentNode.m_Size + ")");
/*  498:     */     } else {
/*  499: 766 */       text.append(currentNode.m_Model.toString().replaceAll("Linear(.*\\n)", "").replaceAll("Predicting", " :").replaceAll("if attribute value is missing.", "for ?").replaceAll("(\\r|\\n)", "") + " (" + currentNode.m_Size + ")");
/*  500:     */     }
/*  501: 771 */     text.append("\" shape=box style=filled");
/*  502: 772 */     text.append("]\n");
/*  503: 773 */     int splitIndex = 0;
/*  504: 774 */     for (SplitterNode split : currentNode.m_Successors)
/*  505:     */     {
/*  506: 775 */       String nId = id + "S" + splitIndex++;
/*  507: 776 */       text.append(id + "->" + nId + " [style=dotted]\n");
/*  508: 777 */       text.append(nId + " [label=\"" + Utils.backQuoteChars(this.m_Data.attribute(split.m_AttributeIndex).name()) + "\"]\n");
/*  509: 781 */       if (split.m_Left != null)
/*  510:     */       {
/*  511: 782 */         text.append(nId + "->" + nId + "1P" + " [label=\"" + " <= " + split.m_Split + "\"]\n");
/*  512:     */         
/*  513:     */ 
/*  514: 785 */         graphTraverse(split.m_Left, text, nId + "1P");
/*  515:     */       }
/*  516: 787 */       if (split.m_Right != null)
/*  517:     */       {
/*  518: 788 */         text.append(nId + "->" + nId + "2P" + " [label=\"" + " > " + split.m_Split + "\"]\n");
/*  519:     */         
/*  520:     */ 
/*  521: 791 */         graphTraverse(split.m_Right, text, nId + "2P");
/*  522:     */       }
/*  523: 793 */       if (split.m_Missing != null)
/*  524:     */       {
/*  525: 794 */         text.append(nId + "->" + nId + "3P" + " [label=\"" + " == ? " + "\"]\n");
/*  526:     */         
/*  527:     */ 
/*  528: 797 */         graphTraverse(split.m_Missing, text, nId + "3P");
/*  529:     */       }
/*  530:     */     }
/*  531:     */   }
/*  532:     */   
/*  533:     */   public String toString()
/*  534:     */   {
/*  535: 807 */     if (this.m_PredictionNodes == null) {
/*  536: 808 */       return "No model built yet.";
/*  537:     */     }
/*  538: 811 */     return ((PredictionNode)this.m_PredictionNodes.get(0)).toString("");
/*  539:     */   }
/*  540:     */   
/*  541:     */   public double classifyInstance(Instance inst)
/*  542:     */     throws Exception
/*  543:     */   {
/*  544: 819 */     this.m_nominalToBinary.input(inst);
/*  545: 820 */     inst = this.m_nominalToBinary.output();
/*  546: 821 */     this.m_removeUseless.input(inst);
/*  547: 822 */     inst = this.m_removeUseless.output();
/*  548:     */     
/*  549:     */ 
/*  550: 825 */     Queue<PredictionNode> queue = new LinkedList();
/*  551: 826 */     queue.add(this.m_PredictionNodes.get(0));
/*  552:     */     
/*  553:     */ 
/*  554:     */ 
/*  555: 830 */     double pred = 0.0D;
/*  556: 831 */     double shrinkage = 1.0D;
/*  557:     */     PredictionNode predNode;
/*  558: 832 */     while ((predNode = (PredictionNode)queue.poll()) != null)
/*  559:     */     {
/*  560: 835 */       pred += shrinkage * predNode.m_Model.classifyInstance(inst);
/*  561: 838 */       for (SplitterNode splitterNode : predNode.m_Successors) {
/*  562: 841 */         if (inst.isMissing(splitterNode.m_AttributeIndex)) {
/*  563: 842 */           queue.add(splitterNode.m_Missing);
/*  564: 844 */         } else if (inst.value(splitterNode.m_AttributeIndex) <= splitterNode.m_Split) {
/*  565: 845 */           queue.add(splitterNode.m_Left);
/*  566:     */         } else {
/*  567: 847 */           queue.add(splitterNode.m_Right);
/*  568:     */         }
/*  569:     */       }
/*  570: 851 */       shrinkage = this.m_Shrinkage;
/*  571:     */     }
/*  572: 853 */     return pred;
/*  573:     */   }
/*  574:     */   
/*  575:     */   public void initializeClassifier(Instances data)
/*  576:     */     throws Exception
/*  577:     */   {
/*  578: 862 */     getCapabilities().testWithFail(data);
/*  579:     */     
/*  580:     */ 
/*  581: 865 */     data = new Instances(data);
/*  582: 866 */     data.deleteWithMissingClass();
/*  583:     */     
/*  584: 868 */     this.m_nominalToBinary = new NominalToBinary();
/*  585: 869 */     this.m_nominalToBinary.setInputFormat(data);
/*  586: 870 */     data = Filter.useFilter(data, this.m_nominalToBinary);
/*  587:     */     
/*  588: 872 */     this.m_removeUseless = new RemoveUseless();
/*  589: 873 */     this.m_removeUseless.setInputFormat(data);
/*  590: 874 */     data = Filter.useFilter(data, this.m_removeUseless);
/*  591:     */     
/*  592:     */ 
/*  593: 877 */     this.m_Data = new Instances(data, data.numInstances());
/*  594: 878 */     for (Instance inst : data) {
/*  595: 879 */       this.m_Data.add(new UnsafeInstance(inst));
/*  596:     */     }
/*  597: 883 */     this.m_PredictionNodes = new ArrayList();
/*  598:     */     
/*  599:     */ 
/*  600: 886 */     this.m_PredictionNodes.add(new PredictionNode(this.m_Data));
/*  601:     */   }
/*  602:     */   
/*  603:     */   public boolean next()
/*  604:     */     throws Exception
/*  605:     */   {
/*  606: 895 */     SplitInfo bestSplit = null;
/*  607: 896 */     PredictionNode bestNode = null;
/*  608: 897 */     for (PredictionNode predNode : this.m_PredictionNodes)
/*  609:     */     {
/*  610: 900 */       SplitInfo split = predNode.evaluateNodeExpansion();
/*  611: 901 */       if (split != null) {
/*  612: 906 */         if ((bestSplit == null) || (split.m_Worth > bestSplit.m_Worth))
/*  613:     */         {
/*  614: 907 */           bestSplit = split;
/*  615: 908 */           bestNode = predNode;
/*  616:     */         }
/*  617:     */       }
/*  618:     */     }
/*  619: 913 */     if (bestSplit == null) {
/*  620: 914 */       return false;
/*  621:     */     }
/*  622: 918 */     SplitterNode splitterNode = bestNode.expandNode(bestSplit);
/*  623:     */     
/*  624:     */ 
/*  625: 921 */     this.m_PredictionNodes.add(splitterNode.m_Left);
/*  626: 922 */     this.m_PredictionNodes.add(splitterNode.m_Right);
/*  627: 923 */     this.m_PredictionNodes.add(splitterNode.m_Missing);
/*  628:     */     
/*  629:     */ 
/*  630: 926 */     return true;
/*  631:     */   }
/*  632:     */   
/*  633:     */   public void done()
/*  634:     */     throws Exception
/*  635:     */   {
/*  636: 935 */     this.m_Data = new Instances(this.m_Data, 0);
/*  637: 938 */     for (PredictionNode predNode : this.m_PredictionNodes) {
/*  638: 939 */       predNode.m_Indices = null;
/*  639:     */     }
/*  640:     */   }
/*  641:     */   
/*  642:     */   public void buildClassifier(Instances data)
/*  643:     */     throws Exception
/*  644:     */   {
/*  645: 949 */     initializeClassifier(data);
/*  646: 952 */     if (data.numAttributes() == 1)
/*  647:     */     {
/*  648: 953 */       done();
/*  649: 954 */       return;
/*  650:     */     }
/*  651: 958 */     for (int i = 0; i < this.m_NumberOfIterations; i++) {
/*  652: 959 */       next();
/*  653:     */     }
/*  654: 963 */     done();
/*  655:     */   }
/*  656:     */   
/*  657:     */   private class UnsafeInstance
/*  658:     */     extends DenseInstance
/*  659:     */   {
/*  660:     */     private static final long serialVersionUID = 3210674215118962869L;
/*  661:     */     
/*  662:     */     public UnsafeInstance(Instance vals)
/*  663:     */     {
/*  664: 984 */       super();
/*  665: 985 */       for (int i = 0; i < vals.numAttributes(); i++) {
/*  666: 986 */         this.m_AttValues[i] = vals.value(i);
/*  667:     */       }
/*  668: 988 */       this.m_Weight = vals.weight();
/*  669:     */     }
/*  670:     */     
/*  671:     */     public void setValue(int attIndex, double value)
/*  672:     */     {
/*  673: 997 */       this.m_AttValues[attIndex] = value;
/*  674:     */     }
/*  675:     */     
/*  676:     */     public Object copy()
/*  677:     */     {
/*  678:1006 */       return this;
/*  679:     */     }
/*  680:     */   }
/*  681:     */   
/*  682:     */   public String getRevision()
/*  683:     */   {
/*  684:1017 */     return RevisionUtils.extract("$Revision: $");
/*  685:     */   }
/*  686:     */   
/*  687:     */   public static void main(String[] args)
/*  688:     */   {
/*  689:1025 */     runClassifier(new AlternatingModelTree(), args);
/*  690:     */   }
/*  691:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.AlternatingModelTree
 * JD-Core Version:    0.7.0.1
 */