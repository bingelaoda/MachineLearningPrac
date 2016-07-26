/*    1:     */ package weka.classifiers.trees;
/*    2:     */ 
/*    3:     */ import java.io.Serializable;
/*    4:     */ import java.util.Collections;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.LinkedList;
/*    7:     */ import java.util.Queue;
/*    8:     */ import java.util.Random;
/*    9:     */ import java.util.Vector;
/*   10:     */ import weka.classifiers.AbstractClassifier;
/*   11:     */ import weka.classifiers.Sourcable;
/*   12:     */ import weka.classifiers.rules.ZeroR;
/*   13:     */ import weka.core.AdditionalMeasureProducer;
/*   14:     */ import weka.core.Attribute;
/*   15:     */ import weka.core.Capabilities;
/*   16:     */ import weka.core.Capabilities.Capability;
/*   17:     */ import weka.core.ContingencyTables;
/*   18:     */ import weka.core.Drawable;
/*   19:     */ import weka.core.Instance;
/*   20:     */ import weka.core.Instances;
/*   21:     */ import weka.core.Option;
/*   22:     */ import weka.core.OptionHandler;
/*   23:     */ import weka.core.PartitionGenerator;
/*   24:     */ import weka.core.Randomizable;
/*   25:     */ import weka.core.RevisionHandler;
/*   26:     */ import weka.core.RevisionUtils;
/*   27:     */ import weka.core.Utils;
/*   28:     */ import weka.core.WeightedInstancesHandler;
/*   29:     */ 
/*   30:     */ public class REPTree
/*   31:     */   extends AbstractClassifier
/*   32:     */   implements OptionHandler, WeightedInstancesHandler, Drawable, AdditionalMeasureProducer, Sourcable, PartitionGenerator, Randomizable
/*   33:     */ {
/*   34:     */   static final long serialVersionUID = -9216785998198681299L;
/*   35:     */   protected ZeroR m_zeroR;
/*   36:     */   
/*   37:     */   public String globalInfo()
/*   38:     */   {
/*   39: 118 */     return "Fast decision tree learner. Builds a decision/regression tree using information gain/variance and prunes it using reduced-error pruning (with backfitting).  Only sorts values for numeric attributes once. Missing values are dealt with by splitting the corresponding instances into pieces (i.e. as in C4.5).";
/*   40:     */   }
/*   41:     */   
/*   42:     */   protected class Tree
/*   43:     */     implements Serializable, RevisionHandler
/*   44:     */   {
/*   45:     */     static final long serialVersionUID = -1635481717888437935L;
/*   46: 132 */     protected Instances m_Info = null;
/*   47:     */     protected Tree[] m_Successors;
/*   48: 138 */     protected int m_Attribute = -1;
/*   49: 141 */     protected double m_SplitPoint = (0.0D / 0.0D);
/*   50: 144 */     protected double[] m_Prop = null;
/*   51: 150 */     protected double[] m_ClassProbs = null;
/*   52: 156 */     protected double[] m_Distribution = null;
/*   53: 163 */     protected double[] m_HoldOutDist = null;
/*   54: 169 */     protected double m_HoldOutError = 0.0D;
/*   55:     */     
/*   56:     */     protected Tree() {}
/*   57:     */     
/*   58:     */     protected double[] distributionForInstance(Instance instance)
/*   59:     */       throws Exception
/*   60:     */     {
/*   61: 181 */       double[] returnedDist = null;
/*   62: 183 */       if (this.m_Attribute > -1) {
/*   63: 186 */         if (instance.isMissing(this.m_Attribute))
/*   64:     */         {
/*   65: 189 */           returnedDist = new double[this.m_Info.numClasses()];
/*   66: 192 */           for (int i = 0; i < this.m_Successors.length; i++)
/*   67:     */           {
/*   68: 193 */             double[] help = this.m_Successors[i].distributionForInstance(instance);
/*   69: 194 */             if (help != null) {
/*   70: 195 */               for (int j = 0; j < help.length; j++) {
/*   71: 196 */                 returnedDist[j] += this.m_Prop[i] * help[j];
/*   72:     */               }
/*   73:     */             }
/*   74:     */           }
/*   75:     */         }
/*   76: 200 */         else if (this.m_Info.attribute(this.m_Attribute).isNominal())
/*   77:     */         {
/*   78: 203 */           returnedDist = this.m_Successors[((int)instance.value(this.m_Attribute))].distributionForInstance(instance);
/*   79:     */         }
/*   80: 208 */         else if (instance.value(this.m_Attribute) < this.m_SplitPoint)
/*   81:     */         {
/*   82: 209 */           returnedDist = this.m_Successors[0].distributionForInstance(instance);
/*   83:     */         }
/*   84:     */         else
/*   85:     */         {
/*   86: 211 */           returnedDist = this.m_Successors[1].distributionForInstance(instance);
/*   87:     */         }
/*   88:     */       }
/*   89: 215 */       if ((this.m_Attribute == -1) || (returnedDist == null))
/*   90:     */       {
/*   91: 218 */         if (this.m_ClassProbs == null) {
/*   92: 219 */           return this.m_ClassProbs;
/*   93:     */         }
/*   94: 221 */         return (double[])this.m_ClassProbs.clone();
/*   95:     */       }
/*   96: 223 */       return returnedDist;
/*   97:     */     }
/*   98:     */     
/*   99:     */     public final String sourceExpression(int index)
/*  100:     */     {
/*  101: 240 */       StringBuffer expr = null;
/*  102: 241 */       if (index < 0) {
/*  103: 242 */         return "i[" + this.m_Attribute + "] == null";
/*  104:     */       }
/*  105: 244 */       if (this.m_Info.attribute(this.m_Attribute).isNominal())
/*  106:     */       {
/*  107: 245 */         expr = new StringBuffer("i[");
/*  108: 246 */         expr.append(this.m_Attribute).append("]");
/*  109: 247 */         expr.append(".equals(\"").append(this.m_Info.attribute(this.m_Attribute).value(index)).append("\")");
/*  110:     */       }
/*  111:     */       else
/*  112:     */       {
/*  113: 250 */         expr = new StringBuffer("");
/*  114: 251 */         if (index == 0) {
/*  115: 252 */           expr.append("((Double)i[").append(this.m_Attribute).append("]).doubleValue() < ").append(this.m_SplitPoint);
/*  116:     */         } else {
/*  117: 255 */           expr.append("true");
/*  118:     */         }
/*  119:     */       }
/*  120: 258 */       return expr.toString();
/*  121:     */     }
/*  122:     */     
/*  123:     */     public StringBuffer[] toSource(String className, Tree parent)
/*  124:     */       throws Exception
/*  125:     */     {
/*  126: 283 */       StringBuffer[] result = new StringBuffer[2];
/*  127:     */       double[] currentProbs;
/*  128:     */       double[] currentProbs;
/*  129: 286 */       if (this.m_ClassProbs == null) {
/*  130: 287 */         currentProbs = parent.m_ClassProbs;
/*  131:     */       } else {
/*  132: 289 */         currentProbs = this.m_ClassProbs;
/*  133:     */       }
/*  134: 292 */       long printID = REPTree.nextID();
/*  135: 295 */       if (this.m_Attribute == -1)
/*  136:     */       {
/*  137: 296 */         result[0] = new StringBuffer("\tp = ");
/*  138: 297 */         if (this.m_Info.classAttribute().isNumeric()) {
/*  139: 298 */           result[0].append(currentProbs[0]);
/*  140:     */         } else {
/*  141: 300 */           result[0].append(Utils.maxIndex(currentProbs));
/*  142:     */         }
/*  143: 302 */         result[0].append(";\n");
/*  144: 303 */         result[1] = new StringBuffer("");
/*  145:     */       }
/*  146:     */       else
/*  147:     */       {
/*  148: 305 */         StringBuffer text = new StringBuffer("");
/*  149: 306 */         StringBuffer atEnd = new StringBuffer("");
/*  150:     */         
/*  151: 308 */         text.append("  static double N").append(Integer.toHexString(hashCode()) + printID).append("(Object []i) {\n").append("    double p = Double.NaN;\n");
/*  152:     */         
/*  153:     */ 
/*  154:     */ 
/*  155: 312 */         text.append("    /* " + this.m_Info.attribute(this.m_Attribute).name() + " */\n");
/*  156:     */         
/*  157: 314 */         text.append("    if (" + sourceExpression(-1) + ") {\n").append("      p = ");
/*  158: 316 */         if (this.m_Info.classAttribute().isNumeric()) {
/*  159: 317 */           text.append(currentProbs[0] + ";\n");
/*  160:     */         } else {
/*  161: 319 */           text.append(Utils.maxIndex(currentProbs) + ";\n");
/*  162:     */         }
/*  163: 321 */         text.append("    } ");
/*  164: 324 */         for (int i = 0; i < this.m_Successors.length; i++)
/*  165:     */         {
/*  166: 325 */           text.append("else if (" + sourceExpression(i) + ") {\n");
/*  167: 327 */           if (this.m_Successors[i].m_Attribute == -1)
/*  168:     */           {
/*  169: 328 */             double[] successorProbs = this.m_Successors[i].m_ClassProbs;
/*  170: 329 */             if (successorProbs == null) {
/*  171: 330 */               successorProbs = this.m_ClassProbs;
/*  172:     */             }
/*  173: 332 */             text.append("      p = ");
/*  174: 333 */             if (this.m_Info.classAttribute().isNumeric()) {
/*  175: 334 */               text.append(successorProbs[0] + ";\n");
/*  176:     */             } else {
/*  177: 336 */               text.append(Utils.maxIndex(successorProbs) + ";\n");
/*  178:     */             }
/*  179:     */           }
/*  180:     */           else
/*  181:     */           {
/*  182: 339 */             StringBuffer[] sub = this.m_Successors[i].toSource(className, this);
/*  183: 340 */             text.append("" + sub[0]);
/*  184: 341 */             atEnd.append("" + sub[1]);
/*  185:     */           }
/*  186: 343 */           text.append("    } ");
/*  187: 344 */           if (i == this.m_Successors.length - 1) {
/*  188: 345 */             text.append("\n");
/*  189:     */           }
/*  190:     */         }
/*  191: 349 */         text.append("    return p;\n  }\n");
/*  192:     */         
/*  193: 351 */         result[0] = new StringBuffer("    p = " + className + ".N");
/*  194: 352 */         result[0].append(Integer.toHexString(hashCode()) + printID).append("(i);\n");
/*  195:     */         
/*  196: 354 */         result[1] = text.append("" + atEnd);
/*  197:     */       }
/*  198: 356 */       return result;
/*  199:     */     }
/*  200:     */     
/*  201:     */     protected int toGraph(StringBuffer text, int num, Tree parent)
/*  202:     */       throws Exception
/*  203:     */     {
/*  204:     */       
/*  205: 372 */       if (this.m_Attribute == -1)
/*  206:     */       {
/*  207: 373 */         text.append("N" + Integer.toHexString(hashCode()) + " [label=\"" + num + Utils.backQuoteChars(leafString(parent)) + "\"" + "shape=box]\n");
/*  208:     */       }
/*  209:     */       else
/*  210:     */       {
/*  211: 377 */         text.append("N" + Integer.toHexString(hashCode()) + " [label=\"" + num + ": " + Utils.backQuoteChars(this.m_Info.attribute(this.m_Attribute).name()) + "\"]\n");
/*  212: 381 */         for (int i = 0; i < this.m_Successors.length; i++)
/*  213:     */         {
/*  214: 382 */           text.append("N" + Integer.toHexString(hashCode()) + "->" + "N" + Integer.toHexString(this.m_Successors[i].hashCode()) + " [label=\"");
/*  215: 385 */           if (this.m_Info.attribute(this.m_Attribute).isNumeric())
/*  216:     */           {
/*  217: 386 */             if (i == 0) {
/*  218: 387 */               text.append(" < " + Utils.doubleToString(this.m_SplitPoint, 2));
/*  219:     */             } else {
/*  220: 389 */               text.append(" >= " + Utils.doubleToString(this.m_SplitPoint, 2));
/*  221:     */             }
/*  222:     */           }
/*  223:     */           else {
/*  224: 392 */             text.append(" = " + Utils.backQuoteChars(this.m_Info.attribute(this.m_Attribute).value(i)));
/*  225:     */           }
/*  226: 395 */           text.append("\"]\n");
/*  227: 396 */           num = this.m_Successors[i].toGraph(text, num, this);
/*  228:     */         }
/*  229:     */       }
/*  230: 400 */       return num;
/*  231:     */     }
/*  232:     */     
/*  233:     */     protected String leafString(Tree parent)
/*  234:     */       throws Exception
/*  235:     */     {
/*  236: 412 */       if (this.m_Info.classAttribute().isNumeric())
/*  237:     */       {
/*  238:     */         double classMean;
/*  239:     */         double classMean;
/*  240: 414 */         if (this.m_ClassProbs == null) {
/*  241: 415 */           classMean = parent.m_ClassProbs[0];
/*  242:     */         } else {
/*  243: 417 */           classMean = this.m_ClassProbs[0];
/*  244:     */         }
/*  245: 419 */         StringBuffer buffer = new StringBuffer();
/*  246: 420 */         buffer.append(" : " + Utils.doubleToString(classMean, 2));
/*  247: 421 */         double avgError = 0.0D;
/*  248: 422 */         if (this.m_Distribution[1] > 0.0D) {
/*  249: 423 */           avgError = this.m_Distribution[0] / this.m_Distribution[1];
/*  250:     */         }
/*  251: 425 */         buffer.append(" (" + Utils.doubleToString(this.m_Distribution[1], 2) + "/" + Utils.doubleToString(avgError, 2) + ")");
/*  252:     */         
/*  253: 427 */         avgError = 0.0D;
/*  254: 428 */         if (this.m_HoldOutDist[0] > 0.0D) {
/*  255: 429 */           avgError = this.m_HoldOutError / this.m_HoldOutDist[0];
/*  256:     */         }
/*  257: 431 */         buffer.append(" [" + Utils.doubleToString(this.m_HoldOutDist[0], 2) + "/" + Utils.doubleToString(avgError, 2) + "]");
/*  258:     */         
/*  259: 433 */         return buffer.toString();
/*  260:     */       }
/*  261:     */       int maxIndex;
/*  262:     */       int maxIndex;
/*  263: 436 */       if (this.m_ClassProbs == null) {
/*  264: 437 */         maxIndex = Utils.maxIndex(parent.m_ClassProbs);
/*  265:     */       } else {
/*  266: 439 */         maxIndex = Utils.maxIndex(this.m_ClassProbs);
/*  267:     */       }
/*  268: 441 */       return " : " + this.m_Info.classAttribute().value(maxIndex) + " (" + Utils.doubleToString(Utils.sum(this.m_Distribution), 2) + "/" + Utils.doubleToString(Utils.sum(this.m_Distribution) - this.m_Distribution[maxIndex], 2) + ")" + " [" + Utils.doubleToString(Utils.sum(this.m_HoldOutDist), 2) + "/" + Utils.doubleToString(Utils.sum(this.m_HoldOutDist) - this.m_HoldOutDist[maxIndex], 2) + "]";
/*  269:     */     }
/*  270:     */     
/*  271:     */     protected String toString(int level, Tree parent)
/*  272:     */     {
/*  273:     */       try
/*  274:     */       {
/*  275: 467 */         StringBuffer text = new StringBuffer();
/*  276: 469 */         if (this.m_Attribute == -1) {
/*  277: 472 */           return leafString(parent);
/*  278:     */         }
/*  279: 473 */         if (this.m_Info.attribute(this.m_Attribute).isNominal())
/*  280:     */         {
/*  281: 476 */           for (int i = 0; i < this.m_Successors.length; i++)
/*  282:     */           {
/*  283: 477 */             text.append("\n");
/*  284: 478 */             for (int j = 0; j < level; j++) {
/*  285: 479 */               text.append("|   ");
/*  286:     */             }
/*  287: 481 */             text.append(this.m_Info.attribute(this.m_Attribute).name() + " = " + this.m_Info.attribute(this.m_Attribute).value(i));
/*  288:     */             
/*  289: 483 */             text.append(this.m_Successors[i].toString(level + 1, this));
/*  290:     */           }
/*  291:     */         }
/*  292:     */         else
/*  293:     */         {
/*  294: 488 */           text.append("\n");
/*  295: 489 */           for (int j = 0; j < level; j++) {
/*  296: 490 */             text.append("|   ");
/*  297:     */           }
/*  298: 492 */           text.append(this.m_Info.attribute(this.m_Attribute).name() + " < " + Utils.doubleToString(this.m_SplitPoint, 2));
/*  299:     */           
/*  300: 494 */           text.append(this.m_Successors[0].toString(level + 1, this));
/*  301: 495 */           text.append("\n");
/*  302: 496 */           for (int j = 0; j < level; j++) {
/*  303: 497 */             text.append("|   ");
/*  304:     */           }
/*  305: 499 */           text.append(this.m_Info.attribute(this.m_Attribute).name() + " >= " + Utils.doubleToString(this.m_SplitPoint, 2));
/*  306:     */           
/*  307: 501 */           text.append(this.m_Successors[1].toString(level + 1, this));
/*  308:     */         }
/*  309: 504 */         return text.toString();
/*  310:     */       }
/*  311:     */       catch (Exception e)
/*  312:     */       {
/*  313: 506 */         e.printStackTrace();
/*  314:     */       }
/*  315: 507 */       return "Decision tree: tree can't be printed";
/*  316:     */     }
/*  317:     */     
/*  318:     */     protected void buildTree(int[][][] sortedIndices, double[][][] weights, Instances data, double totalWeight, double[] classProbs, Instances header, double minNum, double minVariance, int depth, int maxDepth)
/*  319:     */       throws Exception
/*  320:     */     {
/*  321: 533 */       this.m_Info = header;
/*  322: 534 */       if (data.classAttribute().isNumeric()) {
/*  323: 535 */         this.m_HoldOutDist = new double[2];
/*  324:     */       } else {
/*  325: 537 */         this.m_HoldOutDist = new double[data.numClasses()];
/*  326:     */       }
/*  327: 541 */       int helpIndex = 0;
/*  328: 542 */       if (data.classIndex() == 0) {
/*  329: 543 */         helpIndex = 1;
/*  330:     */       }
/*  331: 545 */       if (sortedIndices[0][helpIndex].length == 0)
/*  332:     */       {
/*  333: 546 */         if (data.classAttribute().isNumeric()) {
/*  334: 547 */           this.m_Distribution = new double[2];
/*  335:     */         } else {
/*  336: 549 */           this.m_Distribution = new double[data.numClasses()];
/*  337:     */         }
/*  338: 551 */         this.m_ClassProbs = null;
/*  339: 552 */         sortedIndices[0] = ((int[][])null);
/*  340: 553 */         weights[0] = ((double[][])null);
/*  341: 554 */         return;
/*  342:     */       }
/*  343: 557 */       double priorVar = 0.0D;
/*  344: 558 */       if (data.classAttribute().isNumeric())
/*  345:     */       {
/*  346: 561 */         double totalSum = 0.0D;double totalSumSquared = 0.0D;double totalSumOfWeights = 0.0D;
/*  347: 562 */         for (int i = 0; i < sortedIndices[0][helpIndex].length; i++)
/*  348:     */         {
/*  349: 563 */           Instance inst = data.instance(sortedIndices[0][helpIndex][i]);
/*  350: 564 */           totalSum += inst.classValue() * weights[0][helpIndex][i];
/*  351: 565 */           totalSumSquared += inst.classValue() * inst.classValue() * weights[0][helpIndex][i];
/*  352:     */           
/*  353: 567 */           totalSumOfWeights += weights[0][helpIndex][i];
/*  354:     */         }
/*  355: 569 */         priorVar = singleVariance(totalSum, totalSumSquared, totalSumOfWeights);
/*  356:     */       }
/*  357: 574 */       this.m_ClassProbs = new double[classProbs.length];
/*  358: 575 */       System.arraycopy(classProbs, 0, this.m_ClassProbs, 0, classProbs.length);
/*  359: 576 */       if ((totalWeight < 2.0D * minNum) || ((data.classAttribute().isNominal()) && (Utils.eq(this.m_ClassProbs[Utils.maxIndex(this.m_ClassProbs)], Utils.sum(this.m_ClassProbs)))) || ((data.classAttribute().isNumeric()) && (priorVar / totalWeight < minVariance)) || ((REPTree.this.m_MaxDepth >= 0) && (depth >= maxDepth)))
/*  360:     */       {
/*  361: 592 */         this.m_Attribute = -1;
/*  362: 593 */         if (data.classAttribute().isNominal())
/*  363:     */         {
/*  364: 596 */           this.m_Distribution = new double[this.m_ClassProbs.length];
/*  365: 597 */           for (int i = 0; i < this.m_ClassProbs.length; i++) {
/*  366: 598 */             this.m_Distribution[i] = this.m_ClassProbs[i];
/*  367:     */           }
/*  368: 600 */           doSmoothing();
/*  369: 601 */           Utils.normalize(this.m_ClassProbs);
/*  370:     */         }
/*  371:     */         else
/*  372:     */         {
/*  373: 605 */           this.m_Distribution = new double[2];
/*  374: 606 */           this.m_Distribution[0] = priorVar;
/*  375: 607 */           this.m_Distribution[1] = totalWeight;
/*  376:     */         }
/*  377: 609 */         sortedIndices[0] = ((int[][])null);
/*  378: 610 */         weights[0] = ((double[][])null);
/*  379: 611 */         return;
/*  380:     */       }
/*  381: 616 */       double[] vals = new double[data.numAttributes()];
/*  382: 617 */       double[][][] dists = new double[data.numAttributes()][0][0];
/*  383: 618 */       double[][] props = new double[data.numAttributes()][0];
/*  384: 619 */       double[][] totalSubsetWeights = new double[data.numAttributes()][0];
/*  385: 620 */       double[] splits = new double[data.numAttributes()];
/*  386: 621 */       if (data.classAttribute().isNominal()) {
/*  387: 624 */         for (int i = 0; i < data.numAttributes(); i++) {
/*  388: 625 */           if (i != data.classIndex())
/*  389:     */           {
/*  390: 626 */             splits[i] = distribution(props, dists, i, sortedIndices[0][i], weights[0][i], totalSubsetWeights, data);
/*  391:     */             
/*  392: 628 */             vals[i] = gain(dists[i], priorVal(dists[i]));
/*  393:     */           }
/*  394:     */         }
/*  395:     */       } else {
/*  396: 634 */         for (int i = 0; i < data.numAttributes(); i++) {
/*  397: 635 */           if (i != data.classIndex()) {
/*  398: 636 */             splits[i] = numericDistribution(props, dists, i, sortedIndices[0][i], weights[0][i], totalSubsetWeights, data, vals);
/*  399:     */           }
/*  400:     */         }
/*  401:     */       }
/*  402: 644 */       this.m_Attribute = Utils.maxIndex(vals);
/*  403: 645 */       int numAttVals = dists[this.m_Attribute].length;
/*  404:     */       
/*  405:     */ 
/*  406:     */ 
/*  407: 649 */       int count = 0;
/*  408: 650 */       for (int i = 0; i < numAttVals; i++)
/*  409:     */       {
/*  410: 651 */         if (totalSubsetWeights[this.m_Attribute][i] >= minNum) {
/*  411: 652 */           count++;
/*  412:     */         }
/*  413: 654 */         if (count > 1) {
/*  414:     */           break;
/*  415:     */         }
/*  416:     */       }
/*  417: 660 */       if ((Utils.gr(vals[this.m_Attribute], 0.0D)) && (count > 1))
/*  418:     */       {
/*  419: 663 */         this.m_SplitPoint = splits[this.m_Attribute];
/*  420: 664 */         this.m_Prop = props[this.m_Attribute];
/*  421: 665 */         double[][] attSubsetDists = dists[this.m_Attribute];
/*  422: 666 */         double[] attTotalSubsetWeights = totalSubsetWeights[this.m_Attribute];
/*  423:     */         
/*  424:     */ 
/*  425: 669 */         vals = null;
/*  426: 670 */         dists = (double[][][])null;
/*  427: 671 */         props = (double[][])null;
/*  428: 672 */         totalSubsetWeights = (double[][])null;
/*  429: 673 */         splits = null;
/*  430:     */         
/*  431:     */ 
/*  432: 676 */         int[][][][] subsetIndices = new int[numAttVals][1][data.numAttributes()][0];
/*  433: 677 */         double[][][][] subsetWeights = new double[numAttVals][1][data.numAttributes()][0];
/*  434:     */         
/*  435: 679 */         splitData(subsetIndices, subsetWeights, this.m_Attribute, this.m_SplitPoint, sortedIndices[0], weights[0], data);
/*  436:     */         
/*  437:     */ 
/*  438:     */ 
/*  439: 683 */         sortedIndices[0] = ((int[][])null);
/*  440: 684 */         weights[0] = ((double[][])null);
/*  441:     */         
/*  442:     */ 
/*  443: 687 */         this.m_Successors = new Tree[numAttVals];
/*  444: 688 */         for (int i = 0; i < numAttVals; i++)
/*  445:     */         {
/*  446: 689 */           this.m_Successors[i] = new Tree(REPTree.this);
/*  447: 690 */           this.m_Successors[i].buildTree(subsetIndices[i], subsetWeights[i], data, attTotalSubsetWeights[i], attSubsetDists[i], header, minNum, minVariance, depth + 1, maxDepth);
/*  448:     */           
/*  449:     */ 
/*  450:     */ 
/*  451:     */ 
/*  452: 695 */           attSubsetDists[i] = null;
/*  453:     */         }
/*  454:     */       }
/*  455:     */       else
/*  456:     */       {
/*  457: 700 */         this.m_Attribute = -1;
/*  458: 701 */         sortedIndices[0] = ((int[][])null);
/*  459: 702 */         weights[0] = ((double[][])null);
/*  460:     */       }
/*  461: 706 */       if (data.classAttribute().isNominal())
/*  462:     */       {
/*  463: 707 */         this.m_Distribution = new double[this.m_ClassProbs.length];
/*  464: 708 */         for (int i = 0; i < this.m_ClassProbs.length; i++) {
/*  465: 709 */           this.m_Distribution[i] = this.m_ClassProbs[i];
/*  466:     */         }
/*  467: 711 */         doSmoothing();
/*  468: 712 */         Utils.normalize(this.m_ClassProbs);
/*  469:     */       }
/*  470:     */       else
/*  471:     */       {
/*  472: 714 */         this.m_Distribution = new double[2];
/*  473: 715 */         this.m_Distribution[0] = priorVar;
/*  474: 716 */         this.m_Distribution[1] = totalWeight;
/*  475:     */       }
/*  476:     */     }
/*  477:     */     
/*  478:     */     protected void doSmoothing()
/*  479:     */     {
/*  480: 725 */       double val = REPTree.this.m_InitialCount;
/*  481: 726 */       if (REPTree.this.m_SpreadInitialCount) {
/*  482: 727 */         val /= this.m_ClassProbs.length;
/*  483:     */       }
/*  484: 729 */       for (int i = 0; i < this.m_ClassProbs.length; i++) {
/*  485: 730 */         this.m_ClassProbs[i] += val;
/*  486:     */       }
/*  487:     */     }
/*  488:     */     
/*  489:     */     protected int numNodes()
/*  490:     */     {
/*  491: 741 */       if (this.m_Attribute == -1) {
/*  492: 742 */         return 1;
/*  493:     */       }
/*  494: 744 */       int size = 1;
/*  495: 745 */       for (Tree m_Successor : this.m_Successors) {
/*  496: 746 */         size += m_Successor.numNodes();
/*  497:     */       }
/*  498: 748 */       return size;
/*  499:     */     }
/*  500:     */     
/*  501:     */     protected void splitData(int[][][][] subsetIndices, double[][][][] subsetWeights, int att, double splitPoint, int[][] sortedIndices, double[][] weights, Instances data)
/*  502:     */       throws Exception
/*  503:     */     {
/*  504: 773 */       for (int i = 0; i < data.numAttributes(); i++) {
/*  505: 774 */         if (i != data.classIndex())
/*  506:     */         {
/*  507: 775 */           if (data.attribute(att).isNominal())
/*  508:     */           {
/*  509: 778 */             int[] num = new int[data.attribute(att).numValues()];
/*  510: 779 */             for (int k = 0; k < num.length; k++)
/*  511:     */             {
/*  512: 780 */               subsetIndices[k][0][i] = new int[sortedIndices[i].length];
/*  513: 781 */               subsetWeights[k][0][i] = new double[sortedIndices[i].length];
/*  514:     */             }
/*  515: 783 */             for (int j = 0; j < sortedIndices[i].length; j++)
/*  516:     */             {
/*  517: 784 */               Instance inst = data.instance(sortedIndices[i][j]);
/*  518: 785 */               if (inst.isMissing(att))
/*  519:     */               {
/*  520: 788 */                 for (int k = 0; k < num.length; k++) {
/*  521: 789 */                   if (this.m_Prop[k] > 0.0D)
/*  522:     */                   {
/*  523: 790 */                     subsetIndices[k][0][i][num[k]] = sortedIndices[i][j];
/*  524: 791 */                     subsetWeights[k][0][i][num[k]] = (this.m_Prop[k] * weights[i][j]);
/*  525: 792 */                     num[k] += 1;
/*  526:     */                   }
/*  527:     */                 }
/*  528:     */               }
/*  529:     */               else
/*  530:     */               {
/*  531: 796 */                 int subset = (int)inst.value(att);
/*  532: 797 */                 subsetIndices[subset][0][i][num[subset]] = sortedIndices[i][j];
/*  533: 798 */                 subsetWeights[subset][0][i][num[subset]] = weights[i][j];
/*  534: 799 */                 num[subset] += 1;
/*  535:     */               }
/*  536:     */             }
/*  537:     */           }
/*  538: 805 */           int[] num = new int[2];
/*  539: 806 */           for (int k = 0; k < 2; k++)
/*  540:     */           {
/*  541: 807 */             subsetIndices[k][0][i] = new int[sortedIndices[i].length];
/*  542: 808 */             subsetWeights[k][0][i] = new double[weights[i].length];
/*  543:     */           }
/*  544: 810 */           for (int j = 0; j < sortedIndices[i].length; j++)
/*  545:     */           {
/*  546: 811 */             Instance inst = data.instance(sortedIndices[i][j]);
/*  547: 812 */             if (inst.isMissing(att))
/*  548:     */             {
/*  549: 815 */               for (int k = 0; k < num.length; k++) {
/*  550: 816 */                 if (this.m_Prop[k] > 0.0D)
/*  551:     */                 {
/*  552: 817 */                   subsetIndices[k][0][i][num[k]] = sortedIndices[i][j];
/*  553: 818 */                   subsetWeights[k][0][i][num[k]] = (this.m_Prop[k] * weights[i][j]);
/*  554: 819 */                   num[k] += 1;
/*  555:     */                 }
/*  556:     */               }
/*  557:     */             }
/*  558:     */             else
/*  559:     */             {
/*  560: 823 */               int subset = inst.value(att) < splitPoint ? 0 : 1;
/*  561: 824 */               subsetIndices[subset][0][i][num[subset]] = sortedIndices[i][j];
/*  562: 825 */               subsetWeights[subset][0][i][num[subset]] = weights[i][j];
/*  563: 826 */               num[subset] += 1;
/*  564:     */             }
/*  565:     */           }
/*  566: 832 */           for (int k = 0; k < num.length; k++)
/*  567:     */           {
/*  568: 833 */             int[] copy = new int[num[k]];
/*  569: 834 */             System.arraycopy(subsetIndices[k][0][i], 0, copy, 0, num[k]);
/*  570: 835 */             subsetIndices[k][0][i] = copy;
/*  571: 836 */             double[] copyWeights = new double[num[k]];
/*  572: 837 */             System.arraycopy(subsetWeights[k][0][i], 0, copyWeights, 0, num[k]);
/*  573: 838 */             subsetWeights[k][0][i] = copyWeights;
/*  574:     */           }
/*  575:     */         }
/*  576:     */       }
/*  577:     */     }
/*  578:     */     
/*  579:     */     protected double distribution(double[][] props, double[][][] dists, int att, int[] sortedIndices, double[] weights, double[][] subsetWeights, Instances data)
/*  580:     */       throws Exception
/*  581:     */     {
/*  582: 861 */       double splitPoint = (0.0D / 0.0D);
/*  583: 862 */       Attribute attribute = data.attribute(att);
/*  584: 863 */       double[][] dist = (double[][])null;
/*  585: 866 */       if (attribute.isNominal())
/*  586:     */       {
/*  587: 869 */         dist = new double[attribute.numValues()][data.numClasses()];
/*  588: 870 */         for (int i = 0; i < sortedIndices.length; i++)
/*  589:     */         {
/*  590: 871 */           Instance inst = data.instance(sortedIndices[i]);
/*  591: 872 */           if (inst.isMissing(att)) {
/*  592:     */             break;
/*  593:     */           }
/*  594: 875 */           dist[((int)inst.value(att))][((int)inst.classValue())] += weights[i];
/*  595:     */         }
/*  596:     */       }
/*  597: 880 */       double[][] currDist = new double[2][data.numClasses()];
/*  598: 881 */       dist = new double[2][data.numClasses()];
/*  599: 884 */       for (int j = 0; j < sortedIndices.length; j++)
/*  600:     */       {
/*  601: 885 */         Instance inst = data.instance(sortedIndices[j]);
/*  602: 886 */         if (inst.isMissing(att)) {
/*  603:     */           break;
/*  604:     */         }
/*  605: 889 */         currDist[1][((int)inst.classValue())] += weights[j];
/*  606:     */       }
/*  607: 891 */       double priorVal = priorVal(currDist);
/*  608: 892 */       System.arraycopy(currDist[1], 0, dist[1], 0, dist[1].length);
/*  609:     */       
/*  610:     */ 
/*  611: 895 */       double currSplit = data.instance(sortedIndices[0]).value(att);
/*  612: 896 */       double bestVal = -1.797693134862316E+308D;
/*  613: 897 */       for (int i = 0; i < sortedIndices.length; i++)
/*  614:     */       {
/*  615: 898 */         Instance inst = data.instance(sortedIndices[i]);
/*  616: 899 */         if (inst.isMissing(att)) {
/*  617:     */           break;
/*  618:     */         }
/*  619: 902 */         if (inst.value(att) > currSplit)
/*  620:     */         {
/*  621: 903 */           double currVal = gain(currDist, priorVal);
/*  622: 904 */           if (currVal > bestVal)
/*  623:     */           {
/*  624: 905 */             bestVal = currVal;
/*  625: 906 */             splitPoint = (inst.value(att) + currSplit) / 2.0D;
/*  626: 909 */             if (splitPoint <= currSplit) {
/*  627: 910 */               splitPoint = inst.value(att);
/*  628:     */             }
/*  629: 913 */             for (int j = 0; j < currDist.length; j++) {
/*  630: 914 */               System.arraycopy(currDist[j], 0, dist[j], 0, dist[j].length);
/*  631:     */             }
/*  632:     */           }
/*  633:     */         }
/*  634: 918 */         currSplit = inst.value(att);
/*  635: 919 */         currDist[0][((int)inst.classValue())] += weights[i];
/*  636: 920 */         currDist[1][((int)inst.classValue())] -= weights[i];
/*  637:     */       }
/*  638: 925 */       props[att] = new double[dist.length];
/*  639: 926 */       for (int k = 0; k < props[att].length; k++) {
/*  640: 927 */         props[att][k] = Utils.sum(dist[k]);
/*  641:     */       }
/*  642: 929 */       if (Utils.sum(props[att]) <= 0.0D) {
/*  643: 930 */         for (int k = 0; k < props[att].length; k++) {
/*  644: 931 */           props[att][k] = (1.0D / props[att].length);
/*  645:     */         }
/*  646:     */       } else {
/*  647: 934 */         Utils.normalize(props[att]);
/*  648:     */       }
/*  649: 938 */       while (i < sortedIndices.length)
/*  650:     */       {
/*  651: 939 */         Instance inst = data.instance(sortedIndices[i]);
/*  652: 940 */         for (int j = 0; j < dist.length; j++) {
/*  653: 941 */           dist[j][((int)inst.classValue())] += props[att][j] * weights[i];
/*  654:     */         }
/*  655: 943 */         i++;
/*  656:     */       }
/*  657: 947 */       subsetWeights[att] = new double[dist.length];
/*  658: 948 */       for (int j = 0; j < dist.length; j++) {
/*  659: 949 */         subsetWeights[att][j] += Utils.sum(dist[j]);
/*  660:     */       }
/*  661: 953 */       dists[att] = dist;
/*  662: 954 */       return splitPoint;
/*  663:     */     }
/*  664:     */     
/*  665:     */     protected double numericDistribution(double[][] props, double[][][] dists, int att, int[] sortedIndices, double[] weights, double[][] subsetWeights, Instances data, double[] vals)
/*  666:     */       throws Exception
/*  667:     */     {
/*  668: 975 */       double splitPoint = (0.0D / 0.0D);
/*  669: 976 */       Attribute attribute = data.attribute(att);
/*  670: 977 */       double[][] dist = (double[][])null;
/*  671: 978 */       double[] sums = null;
/*  672: 979 */       double[] sumSquared = null;
/*  673: 980 */       double[] sumOfWeights = null;
/*  674: 981 */       double totalSum = 0.0D;double totalSumSquared = 0.0D;double totalSumOfWeights = 0.0D;
/*  675:     */       int i;
/*  676: 985 */       if (attribute.isNominal())
/*  677:     */       {
/*  678: 988 */         sums = new double[attribute.numValues()];
/*  679: 989 */         sumSquared = new double[attribute.numValues()];
/*  680: 990 */         sumOfWeights = new double[attribute.numValues()];
/*  681: 992 */         for (int i = 0; i < sortedIndices.length; i++)
/*  682:     */         {
/*  683: 993 */           Instance inst = data.instance(sortedIndices[i]);
/*  684: 994 */           if (inst.isMissing(att)) {
/*  685:     */             break;
/*  686:     */           }
/*  687: 997 */           int attVal = (int)inst.value(att);
/*  688: 998 */           sums[attVal] += inst.classValue() * weights[i];
/*  689: 999 */           sumSquared[attVal] += inst.classValue() * inst.classValue() * weights[i];
/*  690:     */           
/*  691:1001 */           sumOfWeights[attVal] += weights[i];
/*  692:     */         }
/*  693:1003 */         totalSum = Utils.sum(sums);
/*  694:1004 */         totalSumSquared = Utils.sum(sumSquared);
/*  695:1005 */         totalSumOfWeights = Utils.sum(sumOfWeights);
/*  696:     */       }
/*  697:     */       else
/*  698:     */       {
/*  699:1009 */         sums = new double[2];
/*  700:1010 */         sumSquared = new double[2];
/*  701:1011 */         sumOfWeights = new double[2];
/*  702:1012 */         double[] currSums = new double[2];
/*  703:1013 */         double[] currSumSquared = new double[2];
/*  704:1014 */         double[] currSumOfWeights = new double[2];
/*  705:1017 */         for (int j = 0; j < sortedIndices.length; j++)
/*  706:     */         {
/*  707:1018 */           Instance inst = data.instance(sortedIndices[j]);
/*  708:1019 */           if (inst.isMissing(att)) {
/*  709:     */             break;
/*  710:     */           }
/*  711:1022 */           currSums[1] += inst.classValue() * weights[j];
/*  712:1023 */           currSumSquared[1] += inst.classValue() * inst.classValue() * weights[j];
/*  713:     */           
/*  714:1025 */           currSumOfWeights[1] += weights[j];
/*  715:     */         }
/*  716:1028 */         totalSum = currSums[1];
/*  717:1029 */         totalSumSquared = currSumSquared[1];
/*  718:1030 */         totalSumOfWeights = currSumOfWeights[1];
/*  719:     */         
/*  720:1032 */         sums[1] = currSums[1];
/*  721:1033 */         sumSquared[1] = currSumSquared[1];
/*  722:1034 */         sumOfWeights[1] = currSumOfWeights[1];
/*  723:     */         
/*  724:     */ 
/*  725:1037 */         double currSplit = data.instance(sortedIndices[0]).value(att);
/*  726:1038 */         double bestVal = 1.7976931348623157E+308D;
/*  727:1039 */         for (i = 0; i < sortedIndices.length; i++)
/*  728:     */         {
/*  729:1040 */           Instance inst = data.instance(sortedIndices[i]);
/*  730:1041 */           if (inst.isMissing(att)) {
/*  731:     */             break;
/*  732:     */           }
/*  733:1044 */           if (inst.value(att) > currSplit)
/*  734:     */           {
/*  735:1045 */             double currVal = variance(currSums, currSumSquared, currSumOfWeights);
/*  736:1046 */             if (currVal < bestVal)
/*  737:     */             {
/*  738:1047 */               bestVal = currVal;
/*  739:1048 */               splitPoint = (inst.value(att) + currSplit) / 2.0D;
/*  740:1051 */               if (splitPoint <= currSplit) {
/*  741:1052 */                 splitPoint = inst.value(att);
/*  742:     */               }
/*  743:1055 */               for (int j = 0; j < 2; j++)
/*  744:     */               {
/*  745:1056 */                 sums[j] = currSums[j];
/*  746:1057 */                 sumSquared[j] = currSumSquared[j];
/*  747:1058 */                 sumOfWeights[j] = currSumOfWeights[j];
/*  748:     */               }
/*  749:     */             }
/*  750:     */           }
/*  751:1063 */           currSplit = inst.value(att);
/*  752:     */           
/*  753:1065 */           double classVal = inst.classValue() * weights[i];
/*  754:1066 */           double classValSquared = inst.classValue() * classVal;
/*  755:     */           
/*  756:1068 */           currSums[0] += classVal;
/*  757:1069 */           currSumSquared[0] += classValSquared;
/*  758:1070 */           currSumOfWeights[0] += weights[i];
/*  759:     */           
/*  760:1072 */           currSums[1] -= classVal;
/*  761:1073 */           currSumSquared[1] -= classValSquared;
/*  762:1074 */           currSumOfWeights[1] -= weights[i];
/*  763:     */         }
/*  764:     */       }
/*  765:1079 */       props[att] = new double[sums.length];
/*  766:1080 */       for (int k = 0; k < props[att].length; k++) {
/*  767:1081 */         props[att][k] = sumOfWeights[k];
/*  768:     */       }
/*  769:1083 */       if (Utils.sum(props[att]) <= 0.0D) {
/*  770:1084 */         for (int k = 0; k < props[att].length; k++) {
/*  771:1085 */           props[att][k] = (1.0D / props[att].length);
/*  772:     */         }
/*  773:     */       } else {
/*  774:1088 */         Utils.normalize(props[att]);
/*  775:     */       }
/*  776:1092 */       while (i < sortedIndices.length)
/*  777:     */       {
/*  778:1093 */         Instance inst = data.instance(sortedIndices[i]);
/*  779:1094 */         for (int j = 0; j < sums.length; j++)
/*  780:     */         {
/*  781:1095 */           sums[j] += props[att][j] * inst.classValue() * weights[i];
/*  782:1096 */           sumSquared[j] += props[att][j] * inst.classValue() * inst.classValue() * weights[i];
/*  783:     */           
/*  784:1098 */           sumOfWeights[j] += props[att][j] * weights[i];
/*  785:     */         }
/*  786:1100 */         totalSum += inst.classValue() * weights[i];
/*  787:1101 */         totalSumSquared += inst.classValue() * inst.classValue() * weights[i];
/*  788:1102 */         totalSumOfWeights += weights[i];
/*  789:1103 */         i++;
/*  790:     */       }
/*  791:1107 */       dist = new double[sums.length][data.numClasses()];
/*  792:1108 */       for (int j = 0; j < sums.length; j++) {
/*  793:1109 */         if (sumOfWeights[j] > 0.0D) {
/*  794:1110 */           dist[j][0] = (sums[j] / sumOfWeights[j]);
/*  795:     */         } else {
/*  796:1112 */           dist[j][0] = (totalSum / totalSumOfWeights);
/*  797:     */         }
/*  798:     */       }
/*  799:1117 */       double priorVar = singleVariance(totalSum, totalSumSquared, totalSumOfWeights);
/*  800:     */       
/*  801:1119 */       double var = variance(sums, sumSquared, sumOfWeights);
/*  802:1120 */       double gain = priorVar - var;
/*  803:     */       
/*  804:     */ 
/*  805:1123 */       subsetWeights[att] = sumOfWeights;
/*  806:1124 */       dists[att] = dist;
/*  807:1125 */       vals[att] = gain;
/*  808:1126 */       return splitPoint;
/*  809:     */     }
/*  810:     */     
/*  811:     */     protected double variance(double[] s, double[] sS, double[] sumOfWeights)
/*  812:     */     {
/*  813:1139 */       double var = 0.0D;
/*  814:1141 */       for (int i = 0; i < s.length; i++) {
/*  815:1142 */         if (sumOfWeights[i] > 0.0D) {
/*  816:1143 */           var += singleVariance(s[i], sS[i], sumOfWeights[i]);
/*  817:     */         }
/*  818:     */       }
/*  819:1147 */       return var;
/*  820:     */     }
/*  821:     */     
/*  822:     */     protected double singleVariance(double s, double sS, double weight)
/*  823:     */     {
/*  824:1160 */       return sS - s * s / weight;
/*  825:     */     }
/*  826:     */     
/*  827:     */     protected double priorVal(double[][] dist)
/*  828:     */     {
/*  829:1171 */       return ContingencyTables.entropyOverColumns(dist);
/*  830:     */     }
/*  831:     */     
/*  832:     */     protected double gain(double[][] dist, double priorVal)
/*  833:     */     {
/*  834:1183 */       return priorVal - ContingencyTables.entropyConditionedOnRows(dist);
/*  835:     */     }
/*  836:     */     
/*  837:     */     protected double reducedErrorPrune()
/*  838:     */       throws Exception
/*  839:     */     {
/*  840:1195 */       if (this.m_Attribute == -1) {
/*  841:1196 */         return this.m_HoldOutError;
/*  842:     */       }
/*  843:1200 */       double errorTree = 0.0D;
/*  844:1201 */       for (Tree m_Successor : this.m_Successors) {
/*  845:1202 */         errorTree += m_Successor.reducedErrorPrune();
/*  846:     */       }
/*  847:1206 */       if (errorTree >= this.m_HoldOutError)
/*  848:     */       {
/*  849:1207 */         this.m_Attribute = -1;
/*  850:1208 */         this.m_Successors = null;
/*  851:1209 */         return this.m_HoldOutError;
/*  852:     */       }
/*  853:1211 */       return errorTree;
/*  854:     */     }
/*  855:     */     
/*  856:     */     protected void insertHoldOutSet(Instances data)
/*  857:     */       throws Exception
/*  858:     */     {
/*  859:1223 */       for (int i = 0; i < data.numInstances(); i++) {
/*  860:1224 */         insertHoldOutInstance(data.instance(i), data.instance(i).weight(), this);
/*  861:     */       }
/*  862:     */     }
/*  863:     */     
/*  864:     */     protected void insertHoldOutInstance(Instance inst, double weight, Tree parent)
/*  865:     */       throws Exception
/*  866:     */     {
/*  867:1240 */       if (inst.classAttribute().isNominal())
/*  868:     */       {
/*  869:1243 */         this.m_HoldOutDist[((int)inst.classValue())] += weight;
/*  870:1244 */         int predictedClass = 0;
/*  871:1245 */         if (this.m_ClassProbs == null) {
/*  872:1246 */           predictedClass = Utils.maxIndex(parent.m_ClassProbs);
/*  873:     */         } else {
/*  874:1248 */           predictedClass = Utils.maxIndex(this.m_ClassProbs);
/*  875:     */         }
/*  876:1250 */         if (predictedClass != (int)inst.classValue()) {
/*  877:1251 */           this.m_HoldOutError += weight;
/*  878:     */         }
/*  879:     */       }
/*  880:     */       else
/*  881:     */       {
/*  882:1256 */         this.m_HoldOutDist[0] += weight;
/*  883:1257 */         this.m_HoldOutDist[1] += weight * inst.classValue();
/*  884:1258 */         double diff = 0.0D;
/*  885:1259 */         if (this.m_ClassProbs == null) {
/*  886:1260 */           diff = parent.m_ClassProbs[0] - inst.classValue();
/*  887:     */         } else {
/*  888:1262 */           diff = this.m_ClassProbs[0] - inst.classValue();
/*  889:     */         }
/*  890:1264 */         this.m_HoldOutError += diff * diff * weight;
/*  891:     */       }
/*  892:1268 */       if (this.m_Attribute != -1) {
/*  893:1271 */         if (inst.isMissing(this.m_Attribute)) {
/*  894:1274 */           for (int i = 0; i < this.m_Successors.length; i++) {
/*  895:1275 */             if (this.m_Prop[i] > 0.0D) {
/*  896:1276 */               this.m_Successors[i].insertHoldOutInstance(inst, weight * this.m_Prop[i], this);
/*  897:     */             }
/*  898:     */           }
/*  899:1282 */         } else if (this.m_Info.attribute(this.m_Attribute).isNominal()) {
/*  900:1285 */           this.m_Successors[((int)inst.value(this.m_Attribute))].insertHoldOutInstance(inst, weight, this);
/*  901:1290 */         } else if (inst.value(this.m_Attribute) < this.m_SplitPoint) {
/*  902:1291 */           this.m_Successors[0].insertHoldOutInstance(inst, weight, this);
/*  903:     */         } else {
/*  904:1293 */           this.m_Successors[1].insertHoldOutInstance(inst, weight, this);
/*  905:     */         }
/*  906:     */       }
/*  907:     */     }
/*  908:     */     
/*  909:     */     protected void backfitHoldOutSet()
/*  910:     */       throws Exception
/*  911:     */     {
/*  912:1308 */       if (this.m_Info.classAttribute().isNominal())
/*  913:     */       {
/*  914:1311 */         if (this.m_ClassProbs == null) {
/*  915:1312 */           this.m_ClassProbs = new double[this.m_Info.numClasses()];
/*  916:     */         }
/*  917:1314 */         System.arraycopy(this.m_Distribution, 0, this.m_ClassProbs, 0, this.m_Info.numClasses());
/*  918:1316 */         for (int i = 0; i < this.m_HoldOutDist.length; i++) {
/*  919:1317 */           this.m_ClassProbs[i] += this.m_HoldOutDist[i];
/*  920:     */         }
/*  921:1319 */         if (Utils.sum(this.m_ClassProbs) > 0.0D)
/*  922:     */         {
/*  923:1320 */           doSmoothing();
/*  924:1321 */           Utils.normalize(this.m_ClassProbs);
/*  925:     */         }
/*  926:     */         else
/*  927:     */         {
/*  928:1323 */           this.m_ClassProbs = null;
/*  929:     */         }
/*  930:     */       }
/*  931:     */       else
/*  932:     */       {
/*  933:1328 */         double sumOfWeightsTrainAndHoldout = this.m_Distribution[1] + this.m_HoldOutDist[0];
/*  934:1330 */         if (sumOfWeightsTrainAndHoldout <= 0.0D) {
/*  935:1331 */           return;
/*  936:     */         }
/*  937:1333 */         if (this.m_ClassProbs == null) {
/*  938:1334 */           this.m_ClassProbs = new double[1];
/*  939:     */         } else {
/*  940:1336 */           this.m_ClassProbs[0] *= this.m_Distribution[1];
/*  941:     */         }
/*  942:1338 */         this.m_ClassProbs[0] += this.m_HoldOutDist[1];
/*  943:1339 */         this.m_ClassProbs[0] /= sumOfWeightsTrainAndHoldout;
/*  944:     */       }
/*  945:1343 */       if (this.m_Attribute != -1) {
/*  946:1344 */         for (Tree m_Successor : this.m_Successors) {
/*  947:1345 */           m_Successor.backfitHoldOutSet();
/*  948:     */         }
/*  949:     */       }
/*  950:     */     }
/*  951:     */     
/*  952:     */     public String getRevision()
/*  953:     */     {
/*  954:1357 */       return RevisionUtils.extract("$Revision: 11615 $");
/*  955:     */     }
/*  956:     */   }
/*  957:     */   
/*  958:1362 */   protected Tree m_Tree = null;
/*  959:1365 */   protected int m_NumFolds = 3;
/*  960:1368 */   protected int m_Seed = 1;
/*  961:1371 */   protected boolean m_NoPruning = false;
/*  962:1374 */   protected double m_MinNum = 2.0D;
/*  963:1380 */   protected double m_MinVarianceProp = 0.001D;
/*  964:1383 */   protected int m_MaxDepth = -1;
/*  965:1386 */   protected double m_InitialCount = 0.0D;
/*  966:1389 */   protected boolean m_SpreadInitialCount = false;
/*  967:     */   
/*  968:     */   public String noPruningTipText()
/*  969:     */   {
/*  970:1398 */     return "Whether pruning is performed.";
/*  971:     */   }
/*  972:     */   
/*  973:     */   public boolean getNoPruning()
/*  974:     */   {
/*  975:1408 */     return this.m_NoPruning;
/*  976:     */   }
/*  977:     */   
/*  978:     */   public void setNoPruning(boolean newNoPruning)
/*  979:     */   {
/*  980:1418 */     this.m_NoPruning = newNoPruning;
/*  981:     */   }
/*  982:     */   
/*  983:     */   public String minNumTipText()
/*  984:     */   {
/*  985:1428 */     return "The minimum total weight of the instances in a leaf.";
/*  986:     */   }
/*  987:     */   
/*  988:     */   public double getMinNum()
/*  989:     */   {
/*  990:1438 */     return this.m_MinNum;
/*  991:     */   }
/*  992:     */   
/*  993:     */   public void setMinNum(double newMinNum)
/*  994:     */   {
/*  995:1448 */     this.m_MinNum = newMinNum;
/*  996:     */   }
/*  997:     */   
/*  998:     */   public String minVariancePropTipText()
/*  999:     */   {
/* 1000:1458 */     return "The minimum proportion of the variance on all the data that needs to be present at a node in order for splitting to be performed in regression trees.";
/* 1001:     */   }
/* 1002:     */   
/* 1003:     */   public double getMinVarianceProp()
/* 1004:     */   {
/* 1005:1470 */     return this.m_MinVarianceProp;
/* 1006:     */   }
/* 1007:     */   
/* 1008:     */   public void setMinVarianceProp(double newMinVarianceProp)
/* 1009:     */   {
/* 1010:1480 */     this.m_MinVarianceProp = newMinVarianceProp;
/* 1011:     */   }
/* 1012:     */   
/* 1013:     */   public String seedTipText()
/* 1014:     */   {
/* 1015:1490 */     return "The seed used for randomizing the data.";
/* 1016:     */   }
/* 1017:     */   
/* 1018:     */   public int getSeed()
/* 1019:     */   {
/* 1020:1501 */     return this.m_Seed;
/* 1021:     */   }
/* 1022:     */   
/* 1023:     */   public void setSeed(int newSeed)
/* 1024:     */   {
/* 1025:1512 */     this.m_Seed = newSeed;
/* 1026:     */   }
/* 1027:     */   
/* 1028:     */   public String numFoldsTipText()
/* 1029:     */   {
/* 1030:1522 */     return "Determines the amount of data used for pruning. One fold is used for pruning, the rest for growing the rules.";
/* 1031:     */   }
/* 1032:     */   
/* 1033:     */   public int getNumFolds()
/* 1034:     */   {
/* 1035:1533 */     return this.m_NumFolds;
/* 1036:     */   }
/* 1037:     */   
/* 1038:     */   public void setNumFolds(int newNumFolds)
/* 1039:     */   {
/* 1040:1543 */     this.m_NumFolds = newNumFolds;
/* 1041:     */   }
/* 1042:     */   
/* 1043:     */   public String maxDepthTipText()
/* 1044:     */   {
/* 1045:1553 */     return "The maximum tree depth (-1 for no restriction).";
/* 1046:     */   }
/* 1047:     */   
/* 1048:     */   public int getMaxDepth()
/* 1049:     */   {
/* 1050:1563 */     return this.m_MaxDepth;
/* 1051:     */   }
/* 1052:     */   
/* 1053:     */   public void setMaxDepth(int newMaxDepth)
/* 1054:     */   {
/* 1055:1573 */     this.m_MaxDepth = newMaxDepth;
/* 1056:     */   }
/* 1057:     */   
/* 1058:     */   public String initialCountTipText()
/* 1059:     */   {
/* 1060:1583 */     return "Initial class value count.";
/* 1061:     */   }
/* 1062:     */   
/* 1063:     */   public double getInitialCount()
/* 1064:     */   {
/* 1065:1593 */     return this.m_InitialCount;
/* 1066:     */   }
/* 1067:     */   
/* 1068:     */   public void setInitialCount(double newInitialCount)
/* 1069:     */   {
/* 1070:1603 */     this.m_InitialCount = newInitialCount;
/* 1071:     */   }
/* 1072:     */   
/* 1073:     */   public String spreadInitialCountTipText()
/* 1074:     */   {
/* 1075:1613 */     return "Spread initial count across all values instead of using the count per value.";
/* 1076:     */   }
/* 1077:     */   
/* 1078:     */   public boolean getSpreadInitialCount()
/* 1079:     */   {
/* 1080:1623 */     return this.m_SpreadInitialCount;
/* 1081:     */   }
/* 1082:     */   
/* 1083:     */   public void setSpreadInitialCount(boolean newSpreadInitialCount)
/* 1084:     */   {
/* 1085:1633 */     this.m_SpreadInitialCount = newSpreadInitialCount;
/* 1086:     */   }
/* 1087:     */   
/* 1088:     */   public Enumeration<Option> listOptions()
/* 1089:     */   {
/* 1090:1644 */     Vector<Option> newVector = new Vector(8);
/* 1091:     */     
/* 1092:1646 */     newVector.addElement(new Option("\tSet minimum number of instances per leaf (default 2).", "M", 1, "-M <minimum number of instances>"));
/* 1093:     */     
/* 1094:     */ 
/* 1095:1649 */     newVector.addElement(new Option("\tSet minimum numeric class variance proportion\n\tof train variance for split (default 1e-3).", "V", 1, "-V <minimum variance for split>"));
/* 1096:     */     
/* 1097:     */ 
/* 1098:     */ 
/* 1099:1653 */     newVector.addElement(new Option("\tNumber of folds for reduced error pruning (default 3).", "N", 1, "-N <number of folds>"));
/* 1100:     */     
/* 1101:     */ 
/* 1102:1656 */     newVector.addElement(new Option("\tSeed for random data shuffling (default 1).", "S", 1, "-S <seed>"));
/* 1103:     */     
/* 1104:1658 */     newVector.addElement(new Option("\tNo pruning.", "P", 0, "-P"));
/* 1105:1659 */     newVector.addElement(new Option("\tMaximum tree depth (default -1, no maximum)", "L", 1, "-L"));
/* 1106:     */     
/* 1107:1661 */     newVector.addElement(new Option("\tInitial class value count (default 0)", "I", 1, "-I"));
/* 1108:     */     
/* 1109:1663 */     newVector.addElement(new Option("\tSpread initial count over all class values (i.e. don't use 1 per value)", "R", 0, "-R"));
/* 1110:     */     
/* 1111:     */ 
/* 1112:     */ 
/* 1113:1667 */     newVector.addAll(Collections.list(super.listOptions()));
/* 1114:     */     
/* 1115:1669 */     return newVector.elements();
/* 1116:     */   }
/* 1117:     */   
/* 1118:     */   public String[] getOptions()
/* 1119:     */   {
/* 1120:1680 */     Vector<String> options = new Vector();
/* 1121:1681 */     options.add("-M");
/* 1122:1682 */     options.add("" + (int)getMinNum());
/* 1123:1683 */     options.add("-V");
/* 1124:1684 */     options.add("" + getMinVarianceProp());
/* 1125:1685 */     options.add("-N");
/* 1126:1686 */     options.add("" + getNumFolds());
/* 1127:1687 */     options.add("-S");
/* 1128:1688 */     options.add("" + getSeed());
/* 1129:1689 */     options.add("-L");
/* 1130:1690 */     options.add("" + getMaxDepth());
/* 1131:1691 */     if (getNoPruning()) {
/* 1132:1692 */       options.add("-P");
/* 1133:     */     }
/* 1134:1694 */     options.add("-I");
/* 1135:1695 */     options.add("" + getInitialCount());
/* 1136:1696 */     if (getSpreadInitialCount()) {
/* 1137:1697 */       options.add("-R");
/* 1138:     */     }
/* 1139:1700 */     Collections.addAll(options, super.getOptions());
/* 1140:     */     
/* 1141:1702 */     return (String[])options.toArray(new String[0]);
/* 1142:     */   }
/* 1143:     */   
/* 1144:     */   public void setOptions(String[] options)
/* 1145:     */     throws Exception
/* 1146:     */   {
/* 1147:1751 */     String minNumString = Utils.getOption('M', options);
/* 1148:1752 */     if (minNumString.length() != 0) {
/* 1149:1753 */       this.m_MinNum = Integer.parseInt(minNumString);
/* 1150:     */     } else {
/* 1151:1755 */       this.m_MinNum = 2.0D;
/* 1152:     */     }
/* 1153:1757 */     String minVarString = Utils.getOption('V', options);
/* 1154:1758 */     if (minVarString.length() != 0) {
/* 1155:1759 */       this.m_MinVarianceProp = Double.parseDouble(minVarString);
/* 1156:     */     } else {
/* 1157:1761 */       this.m_MinVarianceProp = 0.001D;
/* 1158:     */     }
/* 1159:1763 */     String numFoldsString = Utils.getOption('N', options);
/* 1160:1764 */     if (numFoldsString.length() != 0) {
/* 1161:1765 */       this.m_NumFolds = Integer.parseInt(numFoldsString);
/* 1162:     */     } else {
/* 1163:1767 */       this.m_NumFolds = 3;
/* 1164:     */     }
/* 1165:1769 */     String seedString = Utils.getOption('S', options);
/* 1166:1770 */     if (seedString.length() != 0) {
/* 1167:1771 */       this.m_Seed = Integer.parseInt(seedString);
/* 1168:     */     } else {
/* 1169:1773 */       this.m_Seed = 1;
/* 1170:     */     }
/* 1171:1775 */     this.m_NoPruning = Utils.getFlag('P', options);
/* 1172:1776 */     String depthString = Utils.getOption('L', options);
/* 1173:1777 */     if (depthString.length() != 0) {
/* 1174:1778 */       this.m_MaxDepth = Integer.parseInt(depthString);
/* 1175:     */     } else {
/* 1176:1780 */       this.m_MaxDepth = -1;
/* 1177:     */     }
/* 1178:1782 */     String initialCountString = Utils.getOption('I', options);
/* 1179:1783 */     if (initialCountString.length() != 0) {
/* 1180:1784 */       this.m_InitialCount = Double.parseDouble(initialCountString);
/* 1181:     */     } else {
/* 1182:1786 */       this.m_InitialCount = 0.0D;
/* 1183:     */     }
/* 1184:1788 */     this.m_SpreadInitialCount = Utils.getFlag('R', options);
/* 1185:     */     
/* 1186:1790 */     super.setOptions(options);
/* 1187:1791 */     Utils.checkForRemainingOptions(options);
/* 1188:     */   }
/* 1189:     */   
/* 1190:     */   public int numNodes()
/* 1191:     */   {
/* 1192:1801 */     return this.m_Tree.numNodes();
/* 1193:     */   }
/* 1194:     */   
/* 1195:     */   public Enumeration<String> enumerateMeasures()
/* 1196:     */   {
/* 1197:1812 */     Vector<String> newVector = new Vector(1);
/* 1198:1813 */     newVector.addElement("measureTreeSize");
/* 1199:1814 */     return newVector.elements();
/* 1200:     */   }
/* 1201:     */   
/* 1202:     */   public double getMeasure(String additionalMeasureName)
/* 1203:     */   {
/* 1204:1827 */     if (additionalMeasureName.equalsIgnoreCase("measureTreeSize")) {
/* 1205:1828 */       return numNodes();
/* 1206:     */     }
/* 1207:1830 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (REPTree)");
/* 1208:     */   }
/* 1209:     */   
/* 1210:     */   public Capabilities getCapabilities()
/* 1211:     */   {
/* 1212:1842 */     Capabilities result = super.getCapabilities();
/* 1213:1843 */     result.disableAll();
/* 1214:     */     
/* 1215:     */ 
/* 1216:1846 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 1217:1847 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 1218:1848 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 1219:1849 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 1220:     */     
/* 1221:     */ 
/* 1222:1852 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 1223:1853 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/* 1224:1854 */     result.enable(Capabilities.Capability.DATE_CLASS);
/* 1225:1855 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 1226:     */     
/* 1227:1857 */     return result;
/* 1228:     */   }
/* 1229:     */   
/* 1230:     */   public void buildClassifier(Instances data)
/* 1231:     */     throws Exception
/* 1232:     */   {
/* 1233:1870 */     getCapabilities().testWithFail(data);
/* 1234:     */     
/* 1235:     */ 
/* 1236:1873 */     data = new Instances(data);
/* 1237:1874 */     data.deleteWithMissingClass();
/* 1238:     */     
/* 1239:1876 */     Random random = new Random(this.m_Seed);
/* 1240:     */     
/* 1241:1878 */     this.m_zeroR = null;
/* 1242:1879 */     if (data.numAttributes() == 1)
/* 1243:     */     {
/* 1244:1880 */       this.m_zeroR = new ZeroR();
/* 1245:1881 */       this.m_zeroR.buildClassifier(data);
/* 1246:1882 */       return;
/* 1247:     */     }
/* 1248:1886 */     data.randomize(random);
/* 1249:1887 */     if (data.classAttribute().isNominal()) {
/* 1250:1888 */       data.stratify(this.m_NumFolds);
/* 1251:     */     }
/* 1252:1892 */     Instances train = null;
/* 1253:1893 */     Instances prune = null;
/* 1254:1894 */     if (!this.m_NoPruning)
/* 1255:     */     {
/* 1256:1895 */       train = data.trainCV(this.m_NumFolds, 0, random);
/* 1257:1896 */       prune = data.testCV(this.m_NumFolds, 0);
/* 1258:     */     }
/* 1259:     */     else
/* 1260:     */     {
/* 1261:1898 */       train = data;
/* 1262:     */     }
/* 1263:1902 */     int[][][] sortedIndices = new int[1][train.numAttributes()][0];
/* 1264:1903 */     double[][][] weights = new double[1][train.numAttributes()][0];
/* 1265:1904 */     double[] vals = new double[train.numInstances()];
/* 1266:1905 */     for (int j = 0; j < train.numAttributes(); j++) {
/* 1267:1906 */       if (j != train.classIndex())
/* 1268:     */       {
/* 1269:1907 */         weights[0][j] = new double[train.numInstances()];
/* 1270:1908 */         if (train.attribute(j).isNominal())
/* 1271:     */         {
/* 1272:1912 */           sortedIndices[0][j] = new int[train.numInstances()];
/* 1273:1913 */           int count = 0;
/* 1274:1914 */           for (int i = 0; i < train.numInstances(); i++)
/* 1275:     */           {
/* 1276:1915 */             Instance inst = train.instance(i);
/* 1277:1916 */             if (!inst.isMissing(j))
/* 1278:     */             {
/* 1279:1917 */               sortedIndices[0][j][count] = i;
/* 1280:1918 */               weights[0][j][count] = inst.weight();
/* 1281:1919 */               count++;
/* 1282:     */             }
/* 1283:     */           }
/* 1284:1922 */           for (int i = 0; i < train.numInstances(); i++)
/* 1285:     */           {
/* 1286:1923 */             Instance inst = train.instance(i);
/* 1287:1924 */             if (inst.isMissing(j))
/* 1288:     */             {
/* 1289:1925 */               sortedIndices[0][j][count] = i;
/* 1290:1926 */               weights[0][j][count] = inst.weight();
/* 1291:1927 */               count++;
/* 1292:     */             }
/* 1293:     */           }
/* 1294:     */         }
/* 1295:     */         else
/* 1296:     */         {
/* 1297:1933 */           for (int i = 0; i < train.numInstances(); i++)
/* 1298:     */           {
/* 1299:1934 */             Instance inst = train.instance(i);
/* 1300:1935 */             vals[i] = inst.value(j);
/* 1301:     */           }
/* 1302:1937 */           sortedIndices[0][j] = Utils.sort(vals);
/* 1303:1938 */           for (int i = 0; i < train.numInstances(); i++) {
/* 1304:1939 */             weights[0][j][i] = train.instance(sortedIndices[0][j][i]).weight();
/* 1305:     */           }
/* 1306:     */         }
/* 1307:     */       }
/* 1308:     */     }
/* 1309:1946 */     double[] classProbs = new double[train.numClasses()];
/* 1310:1947 */     double totalWeight = 0.0D;double totalSumSquared = 0.0D;
/* 1311:1948 */     for (int i = 0; i < train.numInstances(); i++)
/* 1312:     */     {
/* 1313:1949 */       Instance inst = train.instance(i);
/* 1314:1950 */       if (data.classAttribute().isNominal())
/* 1315:     */       {
/* 1316:1951 */         classProbs[((int)inst.classValue())] += inst.weight();
/* 1317:1952 */         totalWeight += inst.weight();
/* 1318:     */       }
/* 1319:     */       else
/* 1320:     */       {
/* 1321:1954 */         classProbs[0] += inst.classValue() * inst.weight();
/* 1322:1955 */         totalSumSquared += inst.classValue() * inst.classValue() * inst.weight();
/* 1323:     */         
/* 1324:1957 */         totalWeight += inst.weight();
/* 1325:     */       }
/* 1326:     */     }
/* 1327:1960 */     this.m_Tree = new Tree();
/* 1328:1961 */     double trainVariance = 0.0D;
/* 1329:1962 */     if (data.classAttribute().isNumeric())
/* 1330:     */     {
/* 1331:1963 */       trainVariance = this.m_Tree.singleVariance(classProbs[0], totalSumSquared, totalWeight) / totalWeight;
/* 1332:     */       
/* 1333:1965 */       classProbs[0] /= totalWeight;
/* 1334:     */     }
/* 1335:1969 */     this.m_Tree.buildTree(sortedIndices, weights, train, totalWeight, classProbs, new Instances(train, 0), this.m_MinNum, this.m_MinVarianceProp * trainVariance, 0, this.m_MaxDepth);
/* 1336:1974 */     if (!this.m_NoPruning)
/* 1337:     */     {
/* 1338:1975 */       this.m_Tree.insertHoldOutSet(prune);
/* 1339:1976 */       this.m_Tree.reducedErrorPrune();
/* 1340:1977 */       this.m_Tree.backfitHoldOutSet();
/* 1341:     */     }
/* 1342:     */   }
/* 1343:     */   
/* 1344:     */   public double[] distributionForInstance(Instance instance)
/* 1345:     */     throws Exception
/* 1346:     */   {
/* 1347:1991 */     if (this.m_zeroR != null) {
/* 1348:1992 */       return this.m_zeroR.distributionForInstance(instance);
/* 1349:     */     }
/* 1350:1994 */     return this.m_Tree.distributionForInstance(instance);
/* 1351:     */   }
/* 1352:     */   
/* 1353:2002 */   private static long PRINTED_NODES = 0L;
/* 1354:     */   
/* 1355:     */   protected static long nextID()
/* 1356:     */   {
/* 1357:2011 */     return PRINTED_NODES++;
/* 1358:     */   }
/* 1359:     */   
/* 1360:     */   protected static void resetID()
/* 1361:     */   {
/* 1362:2018 */     PRINTED_NODES = 0L;
/* 1363:     */   }
/* 1364:     */   
/* 1365:     */   public String toSource(String className)
/* 1366:     */     throws Exception
/* 1367:     */   {
/* 1368:2031 */     if (this.m_Tree == null) {
/* 1369:2032 */       throw new Exception("REPTree: No model built yet.");
/* 1370:     */     }
/* 1371:2034 */     StringBuffer[] source = this.m_Tree.toSource(className, this.m_Tree);
/* 1372:2035 */     return "class " + className + " {\n\n" + "  public static double classify(Object [] i)\n" + "    throws Exception {\n\n" + "    double p = Double.NaN;\n" + source[0] + "    return p;\n" + "  }\n" + source[1] + "}\n";
/* 1373:     */   }
/* 1374:     */   
/* 1375:     */   public int graphType()
/* 1376:     */   {
/* 1377:2050 */     return 1;
/* 1378:     */   }
/* 1379:     */   
/* 1380:     */   public String graph()
/* 1381:     */     throws Exception
/* 1382:     */   {
/* 1383:2062 */     if (this.m_Tree == null) {
/* 1384:2063 */       throw new Exception("REPTree: No model built yet.");
/* 1385:     */     }
/* 1386:2065 */     StringBuffer resultBuff = new StringBuffer();
/* 1387:2066 */     this.m_Tree.toGraph(resultBuff, 0, null);
/* 1388:2067 */     String result = "digraph Tree {\nedge [style=bold]\n" + resultBuff.toString() + "\n}\n";
/* 1389:     */     
/* 1390:2069 */     return result;
/* 1391:     */   }
/* 1392:     */   
/* 1393:     */   public String toString()
/* 1394:     */   {
/* 1395:2080 */     if (this.m_zeroR != null) {
/* 1396:2081 */       return "No attributes other than class. Using ZeroR.\n\n" + this.m_zeroR.toString();
/* 1397:     */     }
/* 1398:2084 */     if (this.m_Tree == null) {
/* 1399:2085 */       return "REPTree: No model built yet.";
/* 1400:     */     }
/* 1401:2087 */     return "\nREPTree\n============\n" + this.m_Tree.toString(0, null) + "\n" + "\nSize of the tree : " + numNodes();
/* 1402:     */   }
/* 1403:     */   
/* 1404:     */   public void generatePartition(Instances data)
/* 1405:     */     throws Exception
/* 1406:     */   {
/* 1407:2097 */     buildClassifier(data);
/* 1408:     */   }
/* 1409:     */   
/* 1410:     */   public double[] getMembershipValues(Instance instance)
/* 1411:     */     throws Exception
/* 1412:     */   {
/* 1413:2107 */     if (this.m_zeroR != null)
/* 1414:     */     {
/* 1415:2108 */       double[] m = new double[1];
/* 1416:2109 */       m[0] = instance.weight();
/* 1417:2110 */       return m;
/* 1418:     */     }
/* 1419:2114 */     double[] a = new double[numElements()];
/* 1420:     */     
/* 1421:     */ 
/* 1422:2117 */     Queue<Double> queueOfWeights = new LinkedList();
/* 1423:2118 */     Queue<Tree> queueOfNodes = new LinkedList();
/* 1424:2119 */     queueOfWeights.add(Double.valueOf(instance.weight()));
/* 1425:2120 */     queueOfNodes.add(this.m_Tree);
/* 1426:2121 */     int index = 0;
/* 1427:2124 */     while (!queueOfNodes.isEmpty())
/* 1428:     */     {
/* 1429:2126 */       a[(index++)] = ((Double)queueOfWeights.poll()).doubleValue();
/* 1430:2127 */       Tree node = (Tree)queueOfNodes.poll();
/* 1431:2130 */       if (node.m_Attribute > -1)
/* 1432:     */       {
/* 1433:2135 */         double[] weights = new double[node.m_Successors.length];
/* 1434:2136 */         if (instance.isMissing(node.m_Attribute)) {
/* 1435:2137 */           System.arraycopy(node.m_Prop, 0, weights, 0, node.m_Prop.length);
/* 1436:2138 */         } else if (node.m_Info.attribute(node.m_Attribute).isNominal()) {
/* 1437:2139 */           weights[((int)instance.value(node.m_Attribute))] = 1.0D;
/* 1438:2141 */         } else if (instance.value(node.m_Attribute) < node.m_SplitPoint) {
/* 1439:2142 */           weights[0] = 1.0D;
/* 1440:     */         } else {
/* 1441:2144 */           weights[1] = 1.0D;
/* 1442:     */         }
/* 1443:2147 */         for (int i = 0; i < node.m_Successors.length; i++)
/* 1444:     */         {
/* 1445:2148 */           queueOfNodes.add(node.m_Successors[i]);
/* 1446:2149 */           queueOfWeights.add(Double.valueOf(a[(index - 1)] * weights[i]));
/* 1447:     */         }
/* 1448:     */       }
/* 1449:     */     }
/* 1450:2152 */     return a;
/* 1451:     */   }
/* 1452:     */   
/* 1453:     */   public int numElements()
/* 1454:     */     throws Exception
/* 1455:     */   {
/* 1456:2162 */     if (this.m_zeroR != null) {
/* 1457:2163 */       return 1;
/* 1458:     */     }
/* 1459:2165 */     return numNodes();
/* 1460:     */   }
/* 1461:     */   
/* 1462:     */   public String getRevision()
/* 1463:     */   {
/* 1464:2175 */     return RevisionUtils.extract("$Revision: 11615 $");
/* 1465:     */   }
/* 1466:     */   
/* 1467:     */   public static void main(String[] argv)
/* 1468:     */   {
/* 1469:2184 */     runClassifier(new REPTree(), argv);
/* 1470:     */   }
/* 1471:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.REPTree
 * JD-Core Version:    0.7.0.1
 */