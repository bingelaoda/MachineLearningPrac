/*    1:     */ package weka.classifiers.rules;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.ArrayList;
/*    6:     */ import java.util.Collections;
/*    7:     */ import java.util.Enumeration;
/*    8:     */ import java.util.Random;
/*    9:     */ import java.util.Vector;
/*   10:     */ import weka.classifiers.AbstractClassifier;
/*   11:     */ import weka.core.Attribute;
/*   12:     */ import weka.core.Capabilities;
/*   13:     */ import weka.core.Capabilities.Capability;
/*   14:     */ import weka.core.ContingencyTables;
/*   15:     */ import weka.core.Instance;
/*   16:     */ import weka.core.Instances;
/*   17:     */ import weka.core.Option;
/*   18:     */ import weka.core.OptionHandler;
/*   19:     */ import weka.core.RevisionHandler;
/*   20:     */ import weka.core.RevisionUtils;
/*   21:     */ import weka.core.Utils;
/*   22:     */ import weka.core.WeightedInstancesHandler;
/*   23:     */ 
/*   24:     */ public class ConjunctiveRule
/*   25:     */   extends AbstractClassifier
/*   26:     */   implements OptionHandler, WeightedInstancesHandler
/*   27:     */ {
/*   28:     */   static final long serialVersionUID = -5938309903225087198L;
/*   29:     */   private int m_Folds;
/*   30:     */   private Attribute m_ClassAttribute;
/*   31:     */   protected ArrayList<Antd> m_Antds;
/*   32:     */   protected double[] m_DefDstr;
/*   33:     */   protected double[] m_Cnsqt;
/*   34:     */   private int m_NumClasses;
/*   35:     */   private long m_Seed;
/*   36:     */   private Random m_Random;
/*   37:     */   private ArrayList<double[][]> m_Targets;
/*   38:     */   private boolean m_IsExclude;
/*   39:     */   private double m_MinNo;
/*   40:     */   private int m_NumAntds;
/*   41:     */   
/*   42:     */   public ConjunctiveRule()
/*   43:     */   {
/*   44: 126 */     this.m_Folds = 3;
/*   45:     */     
/*   46:     */ 
/*   47:     */ 
/*   48:     */ 
/*   49:     */ 
/*   50: 132 */     this.m_Antds = null;
/*   51:     */     
/*   52:     */ 
/*   53: 135 */     this.m_DefDstr = null;
/*   54:     */     
/*   55:     */ 
/*   56: 138 */     this.m_Cnsqt = null;
/*   57:     */     
/*   58:     */ 
/*   59: 141 */     this.m_NumClasses = 0;
/*   60:     */     
/*   61:     */ 
/*   62: 144 */     this.m_Seed = 1L;
/*   63:     */     
/*   64:     */ 
/*   65: 147 */     this.m_Random = null;
/*   66:     */     
/*   67:     */ 
/*   68:     */ 
/*   69:     */ 
/*   70:     */ 
/*   71: 153 */     this.m_IsExclude = false;
/*   72:     */     
/*   73:     */ 
/*   74: 156 */     this.m_MinNo = 2.0D;
/*   75:     */     
/*   76:     */ 
/*   77: 159 */     this.m_NumAntds = -1;
/*   78:     */   }
/*   79:     */   
/*   80:     */   public String globalInfo()
/*   81:     */   {
/*   82: 169 */     return "This class implements a single conjunctive rule learner that can predict for numeric and nominal class labels.\n\nA rule consists of antecedents \"AND\"ed together and the consequent (class value) for the classification/regression.  In this case, the consequent is the distribution of the available classes (or mean for a numeric value) in the dataset. If the test instance is not covered by this rule, then it's predicted using the default class distributions/value of the data not covered by the rule in the training data.This learner selects an antecedent by computing the Information Gain of each antecendent and prunes the generated rule using Reduced Error Prunning (REP) or simple pre-pruning based on the number of antecedents.\n\nFor classification, the Information of one antecedent is the weighted average of the entropies of both the data covered and not covered by the rule.\nFor regression, the Information is the weighted average of the mean-squared errors of both the data covered and not covered by the rule.\n\nIn pruning, weighted average of the accuracy rates on the pruning data is used for classification while the weighted average of the mean-squared errors on the pruning data is used for regression.\n\n";
/*   83:     */   }
/*   84:     */   
/*   85:     */   private abstract class Antd
/*   86:     */     implements Serializable, RevisionHandler
/*   87:     */   {
/*   88:     */     private static final long serialVersionUID = -8729076306737827571L;
/*   89:     */     protected Attribute att;
/*   90:     */     protected double value;
/*   91:     */     protected double maxInfoGain;
/*   92:     */     protected double inform;
/*   93:     */     protected double uncoverWtSq;
/*   94:     */     protected double uncoverWtVl;
/*   95:     */     protected double uncoverSum;
/*   96:     */     protected double[] uncover;
/*   97:     */     
/*   98:     */     public Antd(Attribute a, double[] unc)
/*   99:     */     {
/*  100: 229 */       this.att = a;
/*  101: 230 */       this.value = (0.0D / 0.0D);
/*  102: 231 */       this.maxInfoGain = 0.0D;
/*  103: 232 */       this.inform = (0.0D / 0.0D);
/*  104: 233 */       this.uncover = unc;
/*  105:     */     }
/*  106:     */     
/*  107:     */     public Antd(Attribute a, double uncoveredWtSq, double uncoveredWtVl, double uncoveredWts)
/*  108:     */     {
/*  109: 241 */       this.att = a;
/*  110: 242 */       this.value = (0.0D / 0.0D);
/*  111: 243 */       this.maxInfoGain = 0.0D;
/*  112: 244 */       this.inform = (0.0D / 0.0D);
/*  113: 245 */       this.uncoverWtSq = uncoveredWtSq;
/*  114: 246 */       this.uncoverWtVl = uncoveredWtVl;
/*  115: 247 */       this.uncoverSum = uncoveredWts;
/*  116:     */     }
/*  117:     */     
/*  118:     */     public abstract Instances[] splitData(Instances paramInstances, double paramDouble);
/*  119:     */     
/*  120:     */     public abstract boolean isCover(Instance paramInstance);
/*  121:     */     
/*  122:     */     public abstract String toString();
/*  123:     */     
/*  124:     */     public Attribute getAttr()
/*  125:     */     {
/*  126: 260 */       return this.att;
/*  127:     */     }
/*  128:     */     
/*  129:     */     public double getAttrValue()
/*  130:     */     {
/*  131: 264 */       return this.value;
/*  132:     */     }
/*  133:     */     
/*  134:     */     public double getMaxInfoGain()
/*  135:     */     {
/*  136: 268 */       return this.maxInfoGain;
/*  137:     */     }
/*  138:     */     
/*  139:     */     public double getInfo()
/*  140:     */     {
/*  141: 272 */       return this.inform;
/*  142:     */     }
/*  143:     */     
/*  144:     */     protected double wtMeanSqErr(double weightedSq, double weightedValue, double sum)
/*  145:     */     {
/*  146: 287 */       if (Utils.smOrEq(sum, 1.0E-006D)) {
/*  147: 288 */         return 0.0D;
/*  148:     */       }
/*  149: 290 */       return weightedSq - weightedValue * weightedValue / sum;
/*  150:     */     }
/*  151:     */     
/*  152:     */     protected double entropy(double[] value, double sum)
/*  153:     */     {
/*  154: 304 */       if (Utils.smOrEq(sum, 1.0E-006D)) {
/*  155: 305 */         return 0.0D;
/*  156:     */       }
/*  157: 308 */       double entropy = 0.0D;
/*  158: 309 */       for (int i = 0; i < value.length; i++) {
/*  159: 310 */         if (!Utils.eq(value[i], 0.0D)) {
/*  160: 311 */           entropy -= value[i] * Utils.log2(value[i]);
/*  161:     */         }
/*  162:     */       }
/*  163: 314 */       entropy += sum * Utils.log2(sum);
/*  164: 315 */       entropy /= sum;
/*  165: 316 */       return entropy;
/*  166:     */     }
/*  167:     */     
/*  168:     */     public String getRevision()
/*  169:     */     {
/*  170: 326 */       return RevisionUtils.extract("$Revision: 10335 $");
/*  171:     */     }
/*  172:     */   }
/*  173:     */   
/*  174:     */   private class NumericAntd
/*  175:     */     extends ConjunctiveRule.Antd
/*  176:     */   {
/*  177:     */     static final long serialVersionUID = -7957266498918210436L;
/*  178:     */     private double splitPoint;
/*  179:     */     
/*  180:     */     public NumericAntd(Attribute a, double[] unc)
/*  181:     */     {
/*  182: 345 */       super(a, unc);
/*  183: 346 */       this.splitPoint = (0.0D / 0.0D);
/*  184:     */     }
/*  185:     */     
/*  186:     */     public NumericAntd(Attribute a, double sq, double vl, double wts)
/*  187:     */     {
/*  188: 353 */       super(a, sq, vl, wts);
/*  189: 354 */       this.splitPoint = (0.0D / 0.0D);
/*  190:     */     }
/*  191:     */     
/*  192:     */     public Instances[] splitData(Instances insts, double defInfo)
/*  193:     */     {
/*  194: 369 */       Instances data = new Instances(insts);
/*  195: 370 */       data.sort(this.att);
/*  196: 371 */       int total = data.numInstances();
/*  197:     */       
/*  198: 373 */       this.maxInfoGain = 0.0D;
/*  199: 374 */       this.value = 0.0D;
/*  200:     */       double minSplit;
/*  201: 378 */       if (ConjunctiveRule.this.m_ClassAttribute.isNominal())
/*  202:     */       {
/*  203: 379 */         double minSplit = 0.1D * data.sumOfWeights() / ConjunctiveRule.this.m_ClassAttribute.numValues();
/*  204: 380 */         if (Utils.smOrEq(minSplit, ConjunctiveRule.this.m_MinNo)) {
/*  205: 381 */           minSplit = ConjunctiveRule.this.m_MinNo;
/*  206: 382 */         } else if (Utils.gr(minSplit, 25.0D)) {
/*  207: 383 */           minSplit = 25.0D;
/*  208:     */         }
/*  209:     */       }
/*  210:     */       else
/*  211:     */       {
/*  212: 386 */         minSplit = ConjunctiveRule.this.m_MinNo;
/*  213:     */       }
/*  214: 389 */       double[] fst = null;double[] snd = null;double[] missing = null;
/*  215: 390 */       if (ConjunctiveRule.this.m_ClassAttribute.isNominal())
/*  216:     */       {
/*  217: 391 */         fst = new double[ConjunctiveRule.this.m_NumClasses];
/*  218: 392 */         snd = new double[ConjunctiveRule.this.m_NumClasses];
/*  219: 393 */         missing = new double[ConjunctiveRule.this.m_NumClasses];
/*  220: 395 */         for (int v = 0; v < ConjunctiveRule.this.m_NumClasses; v++)
/*  221:     */         {
/*  222: 396 */           double tmp212_211 = (missing[v] = 0.0D);snd[v] = tmp212_211;fst[v] = tmp212_211;
/*  223:     */         }
/*  224:     */       }
/*  225: 399 */       double fstCover = 0.0D;double sndCover = 0.0D;double fstWtSq = 0.0D;double sndWtSq = 0.0D;double fstWtVl = 0.0D;double sndWtVl = 0.0D;
/*  226:     */       
/*  227: 401 */       int split = 1;
/*  228: 402 */       int prev = 0;
/*  229: 403 */       int finalSplit = split;
/*  230: 405 */       for (int x = 0; x < data.numInstances(); x++)
/*  231:     */       {
/*  232: 406 */         Instance inst = data.instance(x);
/*  233: 407 */         if (inst.isMissing(this.att))
/*  234:     */         {
/*  235: 408 */           total = x;
/*  236: 409 */           break;
/*  237:     */         }
/*  238: 412 */         sndCover += inst.weight();
/*  239: 413 */         if (ConjunctiveRule.this.m_ClassAttribute.isNominal())
/*  240:     */         {
/*  241: 414 */           snd[((int)inst.classValue())] += inst.weight();
/*  242:     */         }
/*  243:     */         else
/*  244:     */         {
/*  245: 416 */           sndWtSq += inst.weight() * inst.classValue() * inst.classValue();
/*  246: 417 */           sndWtVl += inst.weight() * inst.classValue();
/*  247:     */         }
/*  248:     */       }
/*  249: 422 */       if (Utils.sm(sndCover, 2.0D * minSplit)) {
/*  250: 423 */         return null;
/*  251:     */       }
/*  252: 426 */       double msingWtSq = 0.0D;double msingWtVl = 0.0D;
/*  253: 427 */       Instances missingData = new Instances(data, 0);
/*  254: 428 */       for (int y = total; y < data.numInstances(); y++)
/*  255:     */       {
/*  256: 429 */         Instance inst = data.instance(y);
/*  257: 430 */         missingData.add(inst);
/*  258: 431 */         if (ConjunctiveRule.this.m_ClassAttribute.isNominal())
/*  259:     */         {
/*  260: 432 */           missing[((int)inst.classValue())] += inst.weight();
/*  261:     */         }
/*  262:     */         else
/*  263:     */         {
/*  264: 434 */           msingWtSq += inst.weight() * inst.classValue() * inst.classValue();
/*  265: 435 */           msingWtVl += inst.weight() * inst.classValue();
/*  266:     */         }
/*  267:     */       }
/*  268: 439 */       if (total == 0) {
/*  269: 440 */         return null;
/*  270:     */       }
/*  271: 443 */       this.splitPoint = data.instance(total - 1).value(this.att);
/*  272: 445 */       for (; split < total; split++) {
/*  273: 446 */         if (!Utils.eq(data.instance(split).value(this.att), data.instance(prev).value(this.att)))
/*  274:     */         {
/*  275: 450 */           for (int y = prev; y < split; y++)
/*  276:     */           {
/*  277: 451 */             Instance inst = data.instance(y);
/*  278: 452 */             fstCover += inst.weight();
/*  279: 453 */             sndCover -= inst.weight();
/*  280: 454 */             if (ConjunctiveRule.this.m_ClassAttribute.isNominal())
/*  281:     */             {
/*  282: 455 */               fst[((int)inst.classValue())] += inst.weight();
/*  283: 456 */               snd[((int)inst.classValue())] -= inst.weight();
/*  284:     */             }
/*  285:     */             else
/*  286:     */             {
/*  287: 458 */               fstWtSq += inst.weight() * inst.classValue() * inst.classValue();
/*  288: 459 */               fstWtVl += inst.weight() * inst.classValue();
/*  289: 460 */               sndWtSq -= inst.weight() * inst.classValue() * inst.classValue();
/*  290: 461 */               sndWtVl -= inst.weight() * inst.classValue();
/*  291:     */             }
/*  292:     */           }
/*  293: 465 */           if ((Utils.sm(fstCover, minSplit)) || (Utils.sm(sndCover, minSplit)))
/*  294:     */           {
/*  295: 466 */             prev = split;
/*  296:     */           }
/*  297:     */           else
/*  298:     */           {
/*  299: 470 */             double fstEntp = 0.0D;double sndEntp = 0.0D;
/*  300: 472 */             if (ConjunctiveRule.this.m_ClassAttribute.isNominal())
/*  301:     */             {
/*  302: 473 */               fstEntp = entropy(fst, fstCover);
/*  303: 474 */               sndEntp = entropy(snd, sndCover);
/*  304:     */             }
/*  305:     */             else
/*  306:     */             {
/*  307: 476 */               fstEntp = wtMeanSqErr(fstWtSq, fstWtVl, fstCover) / fstCover;
/*  308: 477 */               sndEntp = wtMeanSqErr(sndWtSq, sndWtVl, sndCover) / sndCover;
/*  309:     */             }
/*  310:     */             double sndInfoGain;
/*  311:     */             double fstInfo;
/*  312:     */             double fstInfoGain;
/*  313:     */             double sndInfo;
/*  314:     */             double sndInfoGain;
/*  315: 484 */             if (ConjunctiveRule.this.m_ClassAttribute.isNominal())
/*  316:     */             {
/*  317: 485 */               double sum = data.sumOfWeights();
/*  318: 486 */               double whole = sum + Utils.sum(this.uncover);
/*  319: 487 */               double[] other = null;
/*  320:     */               
/*  321:     */ 
/*  322: 490 */               other = new double[ConjunctiveRule.this.m_NumClasses];
/*  323: 491 */               for (int z = 0; z < ConjunctiveRule.this.m_NumClasses; z++) {
/*  324: 492 */                 other[z] = (this.uncover[z] + snd[z] + missing[z]);
/*  325:     */               }
/*  326: 494 */               double otherCover = whole - fstCover;
/*  327: 495 */               double otherEntropy = entropy(other, otherCover);
/*  328:     */               
/*  329: 497 */               double fstInfo = (fstEntp * fstCover + otherEntropy * otherCover) / whole;
/*  330: 498 */               double fstInfoGain = defInfo - fstInfo;
/*  331:     */               
/*  332:     */ 
/*  333: 501 */               other = new double[ConjunctiveRule.this.m_NumClasses];
/*  334: 502 */               for (int z = 0; z < ConjunctiveRule.this.m_NumClasses; z++) {
/*  335: 503 */                 other[z] = (this.uncover[z] + fst[z] + missing[z]);
/*  336:     */               }
/*  337: 505 */               otherCover = whole - sndCover;
/*  338: 506 */               otherEntropy = entropy(other, otherCover);
/*  339:     */               
/*  340: 508 */               double sndInfo = (sndEntp * sndCover + otherEntropy * otherCover) / whole;
/*  341: 509 */               sndInfoGain = defInfo - sndInfo;
/*  342:     */             }
/*  343:     */             else
/*  344:     */             {
/*  345: 511 */               double sum = data.sumOfWeights();
/*  346: 512 */               double otherWtSq = sndWtSq + msingWtSq + this.uncoverWtSq;double otherWtVl = sndWtVl + msingWtVl + this.uncoverWtVl;
/*  347: 513 */               double otherCover = sum - fstCover + this.uncoverSum;
/*  348:     */               
/*  349: 515 */               fstInfo = Utils.eq(fstCover, 0.0D) ? 0.0D : fstEntp * fstCover;
/*  350: 516 */               fstInfo += wtMeanSqErr(otherWtSq, otherWtVl, otherCover);
/*  351: 517 */               fstInfoGain = defInfo - fstInfo;
/*  352:     */               
/*  353: 519 */               otherWtSq = fstWtSq + msingWtSq + this.uncoverWtSq;
/*  354: 520 */               otherWtVl = fstWtVl + msingWtVl + this.uncoverWtVl;
/*  355: 521 */               otherCover = sum - sndCover + this.uncoverSum;
/*  356: 522 */               sndInfo = Utils.eq(sndCover, 0.0D) ? 0.0D : sndEntp * sndCover;
/*  357: 523 */               sndInfo += wtMeanSqErr(otherWtSq, otherWtVl, otherCover);
/*  358: 524 */               sndInfoGain = defInfo - sndInfo;
/*  359:     */             }
/*  360:     */             double info;
/*  361:     */             boolean isFirst;
/*  362:     */             double infoGain;
/*  363:     */             double info;
/*  364: 527 */             if ((Utils.gr(fstInfoGain, sndInfoGain)) || ((Utils.eq(fstInfoGain, sndInfoGain)) && (Utils.sm(fstEntp, sndEntp))))
/*  365:     */             {
/*  366: 530 */               boolean isFirst = true;
/*  367: 531 */               double infoGain = fstInfoGain;
/*  368: 532 */               info = fstInfo;
/*  369:     */             }
/*  370:     */             else
/*  371:     */             {
/*  372: 534 */               isFirst = false;
/*  373: 535 */               infoGain = sndInfoGain;
/*  374: 536 */               info = sndInfo;
/*  375:     */             }
/*  376: 539 */             boolean isUpdate = Utils.gr(infoGain, this.maxInfoGain);
/*  377: 542 */             if (isUpdate)
/*  378:     */             {
/*  379: 543 */               this.splitPoint = ((data.instance(split).value(this.att) + data.instance(prev).value(this.att)) / 2.0D);
/*  380:     */               
/*  381: 545 */               this.value = (isFirst ? 0 : 1);
/*  382: 546 */               this.inform = info;
/*  383: 547 */               this.maxInfoGain = infoGain;
/*  384: 548 */               finalSplit = split;
/*  385:     */             }
/*  386: 550 */             prev = split;
/*  387:     */           }
/*  388:     */         }
/*  389:     */       }
/*  390: 555 */       Instances[] splitData = new Instances[3];
/*  391: 556 */       splitData[0] = new Instances(data, 0, finalSplit);
/*  392: 557 */       splitData[1] = new Instances(data, finalSplit, total - finalSplit);
/*  393: 558 */       splitData[2] = new Instances(missingData);
/*  394:     */       
/*  395: 560 */       return splitData;
/*  396:     */     }
/*  397:     */     
/*  398:     */     public boolean isCover(Instance inst)
/*  399:     */     {
/*  400: 572 */       boolean isCover = false;
/*  401: 573 */       if (!inst.isMissing(this.att)) {
/*  402: 574 */         if (Utils.eq(this.value, 0.0D))
/*  403:     */         {
/*  404: 575 */           if (Utils.smOrEq(inst.value(this.att), this.splitPoint)) {
/*  405: 576 */             isCover = true;
/*  406:     */           }
/*  407:     */         }
/*  408: 578 */         else if (Utils.gr(inst.value(this.att), this.splitPoint)) {
/*  409: 579 */           isCover = true;
/*  410:     */         }
/*  411:     */       }
/*  412: 582 */       return isCover;
/*  413:     */     }
/*  414:     */     
/*  415:     */     public String toString()
/*  416:     */     {
/*  417: 592 */       String symbol = Utils.eq(this.value, 0.0D) ? " <= " : " > ";
/*  418: 593 */       return this.att.name() + symbol + Utils.doubleToString(this.splitPoint, 6);
/*  419:     */     }
/*  420:     */     
/*  421:     */     public String getRevision()
/*  422:     */     {
/*  423: 603 */       return RevisionUtils.extract("$Revision: 10335 $");
/*  424:     */     }
/*  425:     */   }
/*  426:     */   
/*  427:     */   class NominalAntd
/*  428:     */     extends ConjunctiveRule.Antd
/*  429:     */   {
/*  430:     */     static final long serialVersionUID = -5949864163376447424L;
/*  431:     */     private final double[][] stats;
/*  432:     */     private final double[] coverage;
/*  433:     */     private boolean isIn;
/*  434:     */     
/*  435:     */     public NominalAntd(Attribute a, double[] unc)
/*  436:     */     {
/*  437: 624 */       super(a, unc);
/*  438: 625 */       int bag = this.att.numValues();
/*  439: 626 */       this.stats = new double[bag][ConjunctiveRule.this.m_NumClasses];
/*  440: 627 */       this.coverage = new double[bag];
/*  441: 628 */       this.isIn = true;
/*  442:     */     }
/*  443:     */     
/*  444:     */     public NominalAntd(Attribute a, double sq, double vl, double wts)
/*  445:     */     {
/*  446: 635 */       super(a, sq, vl, wts);
/*  447: 636 */       int bag = this.att.numValues();
/*  448: 637 */       this.stats = ((double[][])null);
/*  449: 638 */       this.coverage = new double[bag];
/*  450: 639 */       this.isIn = true;
/*  451:     */     }
/*  452:     */     
/*  453:     */     public Instances[] splitData(Instances data, double defInfo)
/*  454:     */     {
/*  455: 654 */       int bag = this.att.numValues();
/*  456: 655 */       Instances[] splitData = new Instances[bag + 1];
/*  457: 656 */       double[] wSq = new double[bag];
/*  458: 657 */       double[] wVl = new double[bag];
/*  459: 658 */       double totalWS = 0.0D;double totalWV = 0.0D;double msingWS = 0.0D;double msingWV = 0.0D;double sum = data.sumOfWeights();
/*  460:     */       
/*  461: 660 */       double[] all = new double[ConjunctiveRule.this.m_NumClasses];
/*  462: 661 */       double[] missing = new double[ConjunctiveRule.this.m_NumClasses];
/*  463: 663 */       for (int w = 0; w < ConjunctiveRule.this.m_NumClasses; w++)
/*  464:     */       {
/*  465: 664 */         double tmp94_93 = 0.0D;missing[w] = tmp94_93;all[w] = tmp94_93;
/*  466:     */       }
/*  467: 667 */       for (int x = 0; x < bag; x++)
/*  468:     */       {
/*  469: 668 */         double tmp130_129 = (wVl[x] = 0.0D);wSq[x] = tmp130_129;this.coverage[x] = tmp130_129;
/*  470: 669 */         if (this.stats != null) {
/*  471: 670 */           for (int y = 0; y < ConjunctiveRule.this.m_NumClasses; y++) {
/*  472: 671 */             this.stats[x][y] = 0.0D;
/*  473:     */           }
/*  474:     */         }
/*  475: 674 */         splitData[x] = new Instances(data, data.numInstances());
/*  476:     */       }
/*  477: 676 */       splitData[bag] = new Instances(data, data.numInstances());
/*  478: 679 */       for (int x = 0; x < data.numInstances(); x++)
/*  479:     */       {
/*  480: 680 */         Instance inst = data.instance(x);
/*  481: 681 */         if (!inst.isMissing(this.att))
/*  482:     */         {
/*  483: 682 */           int v = (int)inst.value(this.att);
/*  484: 683 */           splitData[v].add(inst);
/*  485: 684 */           this.coverage[v] += inst.weight();
/*  486: 685 */           if (ConjunctiveRule.this.m_ClassAttribute.isNominal())
/*  487:     */           {
/*  488: 686 */             this.stats[v][((int)inst.classValue())] += inst.weight();
/*  489: 687 */             all[((int)inst.classValue())] += inst.weight();
/*  490:     */           }
/*  491:     */           else
/*  492:     */           {
/*  493: 689 */             wSq[v] += inst.weight() * inst.classValue() * inst.classValue();
/*  494: 690 */             wVl[v] += inst.weight() * inst.classValue();
/*  495: 691 */             totalWS += inst.weight() * inst.classValue() * inst.classValue();
/*  496: 692 */             totalWV += inst.weight() * inst.classValue();
/*  497:     */           }
/*  498:     */         }
/*  499:     */         else
/*  500:     */         {
/*  501: 695 */           splitData[bag].add(inst);
/*  502: 696 */           if (ConjunctiveRule.this.m_ClassAttribute.isNominal())
/*  503:     */           {
/*  504: 697 */             all[((int)inst.classValue())] += inst.weight();
/*  505: 698 */             missing[((int)inst.classValue())] += inst.weight();
/*  506:     */           }
/*  507:     */           else
/*  508:     */           {
/*  509: 700 */             totalWS += inst.weight() * inst.classValue() * inst.classValue();
/*  510: 701 */             totalWV += inst.weight() * inst.classValue();
/*  511: 702 */             msingWS += inst.weight() * inst.classValue() * inst.classValue();
/*  512: 703 */             msingWV += inst.weight() * inst.classValue();
/*  513:     */           }
/*  514:     */         }
/*  515:     */       }
/*  516:     */       double whole;
/*  517:     */       double whole;
/*  518: 710 */       if (ConjunctiveRule.this.m_ClassAttribute.isNominal()) {
/*  519: 711 */         whole = sum + Utils.sum(this.uncover);
/*  520:     */       } else {
/*  521: 713 */         whole = sum + this.uncoverSum;
/*  522:     */       }
/*  523: 717 */       double minEntrp = 1.7976931348623157E+308D;
/*  524: 718 */       this.maxInfoGain = 0.0D;
/*  525:     */       
/*  526:     */ 
/*  527: 721 */       int count = 0;
/*  528: 722 */       for (int x = 0; x < bag; x++) {
/*  529: 723 */         if (Utils.grOrEq(this.coverage[x], ConjunctiveRule.this.m_MinNo)) {
/*  530: 724 */           count++;
/*  531:     */         }
/*  532:     */       }
/*  533: 728 */       if (count < 2)
/*  534:     */       {
/*  535: 729 */         this.maxInfoGain = 0.0D;
/*  536: 730 */         this.inform = defInfo;
/*  537: 731 */         this.value = (0.0D / 0.0D);
/*  538: 732 */         return null;
/*  539:     */       }
/*  540: 735 */       for (int x = 0; x < bag; x++)
/*  541:     */       {
/*  542: 736 */         double t = this.coverage[x];
/*  543: 738 */         if (!Utils.sm(t, ConjunctiveRule.this.m_MinNo))
/*  544:     */         {
/*  545:     */           double infoGain;
/*  546:     */           double entrp;
/*  547:     */           double infoGain;
/*  548: 742 */           if (ConjunctiveRule.this.m_ClassAttribute.isNominal())
/*  549:     */           {
/*  550: 743 */             double[] other = new double[ConjunctiveRule.this.m_NumClasses];
/*  551: 744 */             for (int y = 0; y < ConjunctiveRule.this.m_NumClasses; y++) {
/*  552: 745 */               other[y] = (all[y] - this.stats[x][y] + this.uncover[y]);
/*  553:     */             }
/*  554: 747 */             double otherCover = whole - t;
/*  555:     */             
/*  556:     */ 
/*  557: 750 */             double entrp = entropy(this.stats[x], t);
/*  558: 751 */             double uncEntp = entropy(other, otherCover);
/*  559: 753 */             if (ConjunctiveRule.this.m_Debug) {
/*  560: 754 */               System.err.println(defInfo + " " + entrp + " " + t + " " + uncEntp + " " + otherCover + " " + whole);
/*  561:     */             }
/*  562: 759 */             infoGain = defInfo - (entrp * t + uncEntp * otherCover) / whole;
/*  563:     */           }
/*  564:     */           else
/*  565:     */           {
/*  566: 761 */             double weight = whole - t;
/*  567: 762 */             entrp = wtMeanSqErr(wSq[x], wVl[x], t) / t;
/*  568: 763 */             infoGain = defInfo - entrp * t - wtMeanSqErr(totalWS - wSq[x] + this.uncoverWtSq, totalWV - wVl[x] + this.uncoverWtVl, weight);
/*  569:     */           }
/*  570: 770 */           boolean isWithin = true;
/*  571: 771 */           if (ConjunctiveRule.this.m_IsExclude)
/*  572:     */           {
/*  573:     */             double infoGain2;
/*  574:     */             double entrp2;
/*  575:     */             double infoGain2;
/*  576: 773 */             if (ConjunctiveRule.this.m_ClassAttribute.isNominal())
/*  577:     */             {
/*  578: 774 */               double[] other2 = new double[ConjunctiveRule.this.m_NumClasses];
/*  579: 775 */               double[] notIn = new double[ConjunctiveRule.this.m_NumClasses];
/*  580: 776 */               for (int y = 0; y < ConjunctiveRule.this.m_NumClasses; y++)
/*  581:     */               {
/*  582: 777 */                 other2[y] = (this.stats[x][y] + missing[y] + this.uncover[y]);
/*  583: 778 */                 notIn[y] = (all[y] - this.stats[x][y] - missing[y]);
/*  584:     */               }
/*  585: 781 */               double msSum = Utils.sum(missing);
/*  586: 782 */               double otherCover2 = t + msSum + Utils.sum(this.uncover);
/*  587:     */               
/*  588: 784 */               double entrp2 = entropy(notIn, sum - t - msSum);
/*  589: 785 */               double uncEntp2 = entropy(other2, otherCover2);
/*  590: 786 */               infoGain2 = defInfo - (entrp2 * (sum - t - msSum) + uncEntp2 * otherCover2) / whole;
/*  591:     */             }
/*  592:     */             else
/*  593:     */             {
/*  594: 789 */               double msWts = splitData[bag].sumOfWeights();
/*  595: 790 */               double weight2 = t + this.uncoverSum + msWts;
/*  596:     */               
/*  597: 792 */               entrp2 = wtMeanSqErr(totalWS - wSq[x] - msingWS, totalWV - wVl[x] - msingWV, sum - t - msWts) / (sum - t - msWts);
/*  598:     */               
/*  599:     */ 
/*  600: 795 */               infoGain2 = defInfo - entrp2 * (sum - t - msWts) - wtMeanSqErr(wSq[x] + this.uncoverWtSq + msingWS, wVl[x] + this.uncoverWtVl + msingWV, weight2);
/*  601:     */             }
/*  602: 803 */             if ((Utils.gr(infoGain2, infoGain)) || ((Utils.eq(infoGain2, infoGain)) && (Utils.sm(entrp2, entrp))))
/*  603:     */             {
/*  604: 805 */               infoGain = infoGain2;
/*  605: 806 */               entrp = entrp2;
/*  606: 807 */               isWithin = false;
/*  607:     */             }
/*  608:     */           }
/*  609: 812 */           if ((Utils.gr(infoGain, this.maxInfoGain)) || ((Utils.eq(infoGain, this.maxInfoGain)) && (Utils.sm(entrp, minEntrp))))
/*  610:     */           {
/*  611: 814 */             this.value = x;
/*  612: 815 */             this.maxInfoGain = infoGain;
/*  613: 816 */             this.inform = (-(this.maxInfoGain - defInfo));
/*  614: 817 */             minEntrp = entrp;
/*  615: 818 */             this.isIn = isWithin;
/*  616:     */           }
/*  617:     */         }
/*  618:     */       }
/*  619: 822 */       return splitData;
/*  620:     */     }
/*  621:     */     
/*  622:     */     public boolean isCover(Instance inst)
/*  623:     */     {
/*  624: 834 */       boolean isCover = false;
/*  625: 835 */       if (!inst.isMissing(this.att)) {
/*  626: 836 */         if (this.isIn)
/*  627:     */         {
/*  628: 837 */           if (Utils.eq(inst.value(this.att), this.value)) {
/*  629: 838 */             isCover = true;
/*  630:     */           }
/*  631:     */         }
/*  632: 840 */         else if (!Utils.eq(inst.value(this.att), this.value)) {
/*  633: 841 */           isCover = true;
/*  634:     */         }
/*  635:     */       }
/*  636: 844 */       return isCover;
/*  637:     */     }
/*  638:     */     
/*  639:     */     public boolean isIn()
/*  640:     */     {
/*  641: 854 */       return this.isIn;
/*  642:     */     }
/*  643:     */     
/*  644:     */     public String toString()
/*  645:     */     {
/*  646: 864 */       String symbol = this.isIn ? " = " : " != ";
/*  647: 865 */       return this.att.name() + symbol + this.att.value((int)this.value);
/*  648:     */     }
/*  649:     */     
/*  650:     */     public String getRevision()
/*  651:     */     {
/*  652: 875 */       return RevisionUtils.extract("$Revision: 10335 $");
/*  653:     */     }
/*  654:     */   }
/*  655:     */   
/*  656:     */   public Enumeration<Option> listOptions()
/*  657:     */   {
/*  658: 918 */     Vector<Option> newVector = new Vector(6);
/*  659:     */     
/*  660: 920 */     newVector.addElement(new Option("\tSet number of folds for REP\n\tOne fold is used as pruning set.\n\t(default 3)", "N", 1, "-N <number of folds>"));
/*  661:     */     
/*  662:     */ 
/*  663:     */ 
/*  664: 924 */     newVector.addElement(new Option("\tSet if NOT uses randomization\n\t(default:use randomization)", "R", 0, "-R"));
/*  665:     */     
/*  666:     */ 
/*  667: 927 */     newVector.addElement(new Option("\tSet whether consider the exclusive\n\texpressions for nominal attributes\n\t(default false)", "E", 0, "-E"));
/*  668:     */     
/*  669:     */ 
/*  670:     */ 
/*  671: 931 */     newVector.addElement(new Option("\tSet the minimal weights of instances\n\twithin a split.\n\t(default 2.0)", "M", 1, "-M <min. weights>"));
/*  672:     */     
/*  673:     */ 
/*  674:     */ 
/*  675:     */ 
/*  676: 936 */     newVector.addElement(new Option("\tSet number of antecedents for pre-pruning\n\tif -1, then REP is used\n\t(default -1)", "P", 1, "-P <number of antecedents>"));
/*  677:     */     
/*  678:     */ 
/*  679:     */ 
/*  680:     */ 
/*  681: 941 */     newVector.addElement(new Option("\tSet the seed of randomization\n\t(default 1)", "S", 1, "-S <seed>"));
/*  682:     */     
/*  683:     */ 
/*  684: 944 */     newVector.addAll(Collections.list(super.listOptions()));
/*  685:     */     
/*  686: 946 */     return newVector.elements();
/*  687:     */   }
/*  688:     */   
/*  689:     */   public void setOptions(String[] options)
/*  690:     */     throws Exception
/*  691:     */   {
/*  692:1004 */     String numFoldsString = Utils.getOption('N', options);
/*  693:1005 */     if (numFoldsString.length() != 0) {
/*  694:1006 */       this.m_Folds = Integer.parseInt(numFoldsString);
/*  695:     */     } else {
/*  696:1008 */       this.m_Folds = 3;
/*  697:     */     }
/*  698:1011 */     String minNoString = Utils.getOption('M', options);
/*  699:1012 */     if (minNoString.length() != 0) {
/*  700:1013 */       this.m_MinNo = Double.parseDouble(minNoString);
/*  701:     */     } else {
/*  702:1015 */       this.m_MinNo = 2.0D;
/*  703:     */     }
/*  704:1018 */     String seedString = Utils.getOption('S', options);
/*  705:1019 */     if (seedString.length() != 0) {
/*  706:1020 */       this.m_Seed = Integer.parseInt(seedString);
/*  707:     */     } else {
/*  708:1022 */       this.m_Seed = 1L;
/*  709:     */     }
/*  710:1025 */     String numAntdsString = Utils.getOption('P', options);
/*  711:1026 */     if (numAntdsString.length() != 0) {
/*  712:1027 */       this.m_NumAntds = Integer.parseInt(numAntdsString);
/*  713:     */     } else {
/*  714:1029 */       this.m_NumAntds = -1;
/*  715:     */     }
/*  716:1032 */     this.m_IsExclude = Utils.getFlag('E', options);
/*  717:     */     
/*  718:1034 */     super.setOptions(options);
/*  719:     */     
/*  720:1036 */     Utils.checkForRemainingOptions(options);
/*  721:     */   }
/*  722:     */   
/*  723:     */   public String[] getOptions()
/*  724:     */   {
/*  725:1047 */     Vector<String> options = new Vector();
/*  726:     */     
/*  727:1049 */     options.add("-N");
/*  728:1050 */     options.add("" + this.m_Folds);
/*  729:1051 */     options.add("-M");
/*  730:1052 */     options.add("" + this.m_MinNo);
/*  731:1053 */     options.add("-P");
/*  732:1054 */     options.add("" + this.m_NumAntds);
/*  733:1055 */     options.add("-S");
/*  734:1056 */     options.add("" + this.m_Seed);
/*  735:1058 */     if (this.m_IsExclude) {
/*  736:1059 */       options.add("-E");
/*  737:     */     }
/*  738:1062 */     return (String[])options.toArray(new String[0]);
/*  739:     */   }
/*  740:     */   
/*  741:     */   public String foldsTipText()
/*  742:     */   {
/*  743:1074 */     return "Determines the amount of data used for pruning. One fold is used for pruning, the rest for growing the rules.";
/*  744:     */   }
/*  745:     */   
/*  746:     */   public void setFolds(int folds)
/*  747:     */   {
/*  748:1084 */     this.m_Folds = folds;
/*  749:     */   }
/*  750:     */   
/*  751:     */   public int getFolds()
/*  752:     */   {
/*  753:1093 */     return this.m_Folds;
/*  754:     */   }
/*  755:     */   
/*  756:     */   public String seedTipText()
/*  757:     */   {
/*  758:1103 */     return "The seed used for randomizing the data.";
/*  759:     */   }
/*  760:     */   
/*  761:     */   public void setSeed(long s)
/*  762:     */   {
/*  763:1112 */     this.m_Seed = s;
/*  764:     */   }
/*  765:     */   
/*  766:     */   public long getSeed()
/*  767:     */   {
/*  768:1121 */     return this.m_Seed;
/*  769:     */   }
/*  770:     */   
/*  771:     */   public String exclusiveTipText()
/*  772:     */   {
/*  773:1131 */     return "Set whether to consider exclusive expressions for nominal attribute splits.";
/*  774:     */   }
/*  775:     */   
/*  776:     */   public boolean getExclusive()
/*  777:     */   {
/*  778:1143 */     return this.m_IsExclude;
/*  779:     */   }
/*  780:     */   
/*  781:     */   public void setExclusive(boolean e)
/*  782:     */   {
/*  783:1154 */     this.m_IsExclude = e;
/*  784:     */   }
/*  785:     */   
/*  786:     */   public String minNoTipText()
/*  787:     */   {
/*  788:1164 */     return "The minimum total weight of the instances in a rule.";
/*  789:     */   }
/*  790:     */   
/*  791:     */   public void setMinNo(double m)
/*  792:     */   {
/*  793:1173 */     this.m_MinNo = m;
/*  794:     */   }
/*  795:     */   
/*  796:     */   public double getMinNo()
/*  797:     */   {
/*  798:1182 */     return this.m_MinNo;
/*  799:     */   }
/*  800:     */   
/*  801:     */   public String numAntdsTipText()
/*  802:     */   {
/*  803:1192 */     return "Set the number of antecedents allowed in the rule if pre-pruning is used.  If this value is other than -1, then pre-pruning will be used, otherwise the rule uses reduced-error pruning.";
/*  804:     */   }
/*  805:     */   
/*  806:     */   public void setNumAntds(int n)
/*  807:     */   {
/*  808:1204 */     this.m_NumAntds = n;
/*  809:     */   }
/*  810:     */   
/*  811:     */   public int getNumAntds()
/*  812:     */   {
/*  813:1213 */     return this.m_NumAntds;
/*  814:     */   }
/*  815:     */   
/*  816:     */   public Capabilities getCapabilities()
/*  817:     */   {
/*  818:1223 */     Capabilities result = super.getCapabilities();
/*  819:1224 */     result.disableAll();
/*  820:     */     
/*  821:     */ 
/*  822:1227 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  823:1228 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  824:1229 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  825:1230 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  826:     */     
/*  827:     */ 
/*  828:1233 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  829:1234 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  830:1235 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  831:1236 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  832:     */     
/*  833:1238 */     return result;
/*  834:     */   }
/*  835:     */   
/*  836:     */   public void buildClassifier(Instances instances)
/*  837:     */     throws Exception
/*  838:     */   {
/*  839:1253 */     getCapabilities().testWithFail(instances);
/*  840:     */     
/*  841:     */ 
/*  842:1256 */     Instances data = new Instances(instances);
/*  843:1257 */     data.deleteWithMissingClass();
/*  844:1259 */     if (data.numInstances() < this.m_Folds) {
/*  845:1260 */       throw new Exception("Not enough data for REP.");
/*  846:     */     }
/*  847:1263 */     this.m_ClassAttribute = data.classAttribute();
/*  848:1264 */     if (this.m_ClassAttribute.isNominal()) {
/*  849:1265 */       this.m_NumClasses = this.m_ClassAttribute.numValues();
/*  850:     */     } else {
/*  851:1267 */       this.m_NumClasses = 1;
/*  852:     */     }
/*  853:1270 */     this.m_Antds = new ArrayList();
/*  854:1271 */     this.m_DefDstr = new double[this.m_NumClasses];
/*  855:1272 */     this.m_Cnsqt = new double[this.m_NumClasses];
/*  856:1273 */     this.m_Targets = new ArrayList();
/*  857:1274 */     this.m_Random = new Random(this.m_Seed);
/*  858:1276 */     if (this.m_NumAntds != -1)
/*  859:     */     {
/*  860:1277 */       grow(data);
/*  861:     */     }
/*  862:     */     else
/*  863:     */     {
/*  864:1280 */       data.randomize(this.m_Random);
/*  865:     */       
/*  866:     */ 
/*  867:1283 */       data.stratify(this.m_Folds);
/*  868:     */       
/*  869:1285 */       Instances growData = data.trainCV(this.m_Folds, this.m_Folds - 1, this.m_Random);
/*  870:1286 */       Instances pruneData = data.testCV(this.m_Folds, this.m_Folds - 1);
/*  871:     */       
/*  872:1288 */       grow(growData);
/*  873:1289 */       prune(pruneData);
/*  874:     */     }
/*  875:1292 */     if (this.m_ClassAttribute.isNominal())
/*  876:     */     {
/*  877:1293 */       Utils.normalize(this.m_Cnsqt);
/*  878:1294 */       if (Utils.gr(Utils.sum(this.m_DefDstr), 0.0D)) {
/*  879:1295 */         Utils.normalize(this.m_DefDstr);
/*  880:     */       }
/*  881:     */     }
/*  882:     */   }
/*  883:     */   
/*  884:     */   public double[] distributionForInstance(Instance instance)
/*  885:     */     throws Exception
/*  886:     */   {
/*  887:1309 */     if (instance == null) {
/*  888:1310 */       throw new Exception("Testing instance is NULL!");
/*  889:     */     }
/*  890:1313 */     if (isCover(instance)) {
/*  891:1314 */       return this.m_Cnsqt;
/*  892:     */     }
/*  893:1316 */     return this.m_DefDstr;
/*  894:     */   }
/*  895:     */   
/*  896:     */   public boolean isCover(Instance datum)
/*  897:     */   {
/*  898:1328 */     boolean isCover = true;
/*  899:1330 */     for (int i = 0; i < this.m_Antds.size(); i++)
/*  900:     */     {
/*  901:1331 */       Antd antd = (Antd)this.m_Antds.get(i);
/*  902:1332 */       if (!antd.isCover(datum))
/*  903:     */       {
/*  904:1333 */         isCover = false;
/*  905:1334 */         break;
/*  906:     */       }
/*  907:     */     }
/*  908:1338 */     return isCover;
/*  909:     */   }
/*  910:     */   
/*  911:     */   public boolean hasAntds()
/*  912:     */   {
/*  913:1347 */     if (this.m_Antds == null) {
/*  914:1348 */       return false;
/*  915:     */     }
/*  916:1350 */     return this.m_Antds.size() > 0;
/*  917:     */   }
/*  918:     */   
/*  919:     */   private void grow(Instances data)
/*  920:     */   {
/*  921:1360 */     Instances growData = new Instances(data);
/*  922:     */     
/*  923:1362 */     double whole = data.sumOfWeights();
/*  924:1364 */     if (this.m_NumAntds != 0)
/*  925:     */     {
/*  926:1370 */       double[][] classDstr = new double[2][this.m_NumClasses];
/*  927:1373 */       for (int j = 0; j < this.m_NumClasses; j++)
/*  928:     */       {
/*  929:1374 */         classDstr[0][j] = 0.0D;
/*  930:1375 */         classDstr[1][j] = 0.0D;
/*  931:     */       }
/*  932:     */       double defInfo;
/*  933:     */       double defInfo;
/*  934:1377 */       if (this.m_ClassAttribute.isNominal())
/*  935:     */       {
/*  936:1378 */         for (int i = 0; i < growData.numInstances(); i++)
/*  937:     */         {
/*  938:1379 */           Instance datum = growData.instance(i);
/*  939:1380 */           classDstr[0][((int)datum.classValue())] += datum.weight();
/*  940:     */         }
/*  941:1382 */         defInfo = ContingencyTables.entropy(classDstr[0]);
/*  942:     */       }
/*  943:     */       else
/*  944:     */       {
/*  945:1384 */         for (int i = 0; i < growData.numInstances(); i++)
/*  946:     */         {
/*  947:1385 */           Instance datum = growData.instance(i);
/*  948:1386 */           classDstr[0][0] += datum.weight() * datum.classValue();
/*  949:     */         }
/*  950:1391 */         double defMean = classDstr[0][0] / whole;
/*  951:1392 */         defInfo = meanSquaredError(growData, defMean) * growData.sumOfWeights();
/*  952:     */       }
/*  953:1396 */       double[][] tmp = new double[2][this.m_NumClasses];
/*  954:1397 */       for (int y = 0; y < this.m_NumClasses; y++) {
/*  955:1398 */         if (this.m_ClassAttribute.isNominal())
/*  956:     */         {
/*  957:1399 */           tmp[0][y] = classDstr[0][y];
/*  958:1400 */           tmp[1][y] = classDstr[1][y];
/*  959:     */         }
/*  960:     */         else
/*  961:     */         {
/*  962:1402 */           classDstr[0][y] /= whole;
/*  963:1403 */           tmp[1][y] = classDstr[1][y];
/*  964:     */         }
/*  965:     */       }
/*  966:1406 */       this.m_Targets.add(tmp);
/*  967:     */       
/*  968:     */ 
/*  969:1409 */       boolean[] used = new boolean[growData.numAttributes()];
/*  970:1410 */       for (int k = 0; k < used.length; k++) {
/*  971:1411 */         used[k] = false;
/*  972:     */       }
/*  973:1413 */       int numUnused = used.length;
/*  974:1414 */       double uncoveredWtSq = 0.0D;double uncoveredWtVl = 0.0D;double uncoveredWts = 0.0D;
/*  975:1415 */       boolean isContinue = true;
/*  976:1417 */       while (isContinue)
/*  977:     */       {
/*  978:1418 */         double maxInfoGain = 0.0D;
/*  979:     */         
/*  980:     */ 
/*  981:1421 */         Antd oneAntd = null;
/*  982:1422 */         Instances coverData = null;Instances uncoverData = null;
/*  983:1423 */         Enumeration<Attribute> enumAttr = growData.enumerateAttributes();
/*  984:1424 */         int index = -1;
/*  985:1426 */         if (this.m_Debug) {
/*  986:1427 */           System.out.println("Growing data: " + growData);
/*  987:     */         }
/*  988:1431 */         while (enumAttr.hasMoreElements())
/*  989:     */         {
/*  990:1432 */           Attribute att = (Attribute)enumAttr.nextElement();
/*  991:1433 */           index++;
/*  992:     */           
/*  993:1435 */           Antd antd = null;
/*  994:1436 */           if (this.m_ClassAttribute.isNominal())
/*  995:     */           {
/*  996:1437 */             if (att.isNumeric()) {
/*  997:1438 */               antd = new NumericAntd(att, classDstr[1]);
/*  998:     */             } else {
/*  999:1440 */               antd = new NominalAntd(att, classDstr[1]);
/* 1000:     */             }
/* 1001:     */           }
/* 1002:1442 */           else if (att.isNumeric()) {
/* 1003:1443 */             antd = new NumericAntd(att, uncoveredWtSq, uncoveredWtVl, uncoveredWts);
/* 1004:     */           } else {
/* 1005:1446 */             antd = new NominalAntd(att, uncoveredWtSq, uncoveredWtVl, uncoveredWts);
/* 1006:     */           }
/* 1007:1450 */           if (used[index] == 0)
/* 1008:     */           {
/* 1009:1457 */             Instances[] coveredData = computeInfoGain(growData, defInfo, antd);
/* 1010:1459 */             if (coveredData != null)
/* 1011:     */             {
/* 1012:1460 */               double infoGain = antd.getMaxInfoGain();
/* 1013:1461 */               boolean isUpdate = Utils.gr(infoGain, maxInfoGain);
/* 1014:1463 */               if (this.m_Debug)
/* 1015:     */               {
/* 1016:1464 */                 System.err.println(antd);
/* 1017:1465 */                 System.err.println("Info gain: " + infoGain);
/* 1018:1466 */                 System.err.println("Max info gain: " + maxInfoGain);
/* 1019:     */               }
/* 1020:1469 */               if (isUpdate)
/* 1021:     */               {
/* 1022:1470 */                 oneAntd = antd;
/* 1023:1471 */                 coverData = coveredData[0];
/* 1024:1472 */                 uncoverData = coveredData[1];
/* 1025:1473 */                 maxInfoGain = infoGain;
/* 1026:     */               }
/* 1027:     */             }
/* 1028:     */           }
/* 1029:     */         }
/* 1030:1479 */         if (oneAntd == null) {
/* 1031:     */           break;
/* 1032:     */         }
/* 1033:1483 */         if (this.m_Debug)
/* 1034:     */         {
/* 1035:1484 */           System.err.println("Adding antecedent: ");
/* 1036:1485 */           System.err.println(oneAntd);
/* 1037:1486 */           System.err.println("Covered data: ");
/* 1038:1487 */           System.err.println(coverData);
/* 1039:1488 */           System.err.println("Uncovered data: ");
/* 1040:1489 */           System.err.println(uncoverData);
/* 1041:     */         }
/* 1042:1493 */         if (!oneAntd.getAttr().isNumeric())
/* 1043:     */         {
/* 1044:1494 */           used[oneAntd.getAttr().index()] = true;
/* 1045:1495 */           numUnused--;
/* 1046:     */         }
/* 1047:1498 */         this.m_Antds.add(oneAntd);
/* 1048:1499 */         growData = coverData;
/* 1049:1501 */         for (int x = 0; x < uncoverData.numInstances(); x++)
/* 1050:     */         {
/* 1051:1502 */           Instance datum = uncoverData.instance(x);
/* 1052:1503 */           if (this.m_ClassAttribute.isNumeric())
/* 1053:     */           {
/* 1054:1504 */             uncoveredWtSq += datum.weight() * datum.classValue() * datum.classValue();
/* 1055:     */             
/* 1056:1506 */             uncoveredWtVl += datum.weight() * datum.classValue();
/* 1057:1507 */             uncoveredWts += datum.weight();
/* 1058:1508 */             classDstr[0][0] -= datum.weight() * datum.classValue();
/* 1059:1509 */             classDstr[1][0] += datum.weight() * datum.classValue();
/* 1060:     */           }
/* 1061:     */           else
/* 1062:     */           {
/* 1063:1511 */             classDstr[0][((int)datum.classValue())] -= datum.weight();
/* 1064:1512 */             classDstr[1][((int)datum.classValue())] += datum.weight();
/* 1065:     */           }
/* 1066:     */         }
/* 1067:1517 */         tmp = new double[2][this.m_NumClasses];
/* 1068:1518 */         for (int y = 0; y < this.m_NumClasses; y++) {
/* 1069:1519 */           if (this.m_ClassAttribute.isNominal())
/* 1070:     */           {
/* 1071:1520 */             tmp[0][y] = classDstr[0][y];
/* 1072:1521 */             tmp[1][y] = classDstr[1][y];
/* 1073:     */           }
/* 1074:     */           else
/* 1075:     */           {
/* 1076:1523 */             classDstr[0][y] /= (whole - uncoveredWts);
/* 1077:1524 */             classDstr[1][y] /= uncoveredWts;
/* 1078:     */           }
/* 1079:     */         }
/* 1080:1527 */         this.m_Targets.add(tmp);
/* 1081:     */         
/* 1082:1529 */         defInfo = oneAntd.getInfo();
/* 1083:1530 */         if (this.m_Debug) {
/* 1084:1531 */           System.err.println("Default info: " + defInfo);
/* 1085:     */         }
/* 1086:1533 */         int numAntdsThreshold = this.m_NumAntds == -1 ? 2147483647 : this.m_NumAntds;
/* 1087:1536 */         if ((Utils.eq(growData.sumOfWeights(), 0.0D)) || (numUnused == 0) || (this.m_Antds.size() >= numAntdsThreshold)) {
/* 1088:1538 */           isContinue = false;
/* 1089:     */         }
/* 1090:     */       }
/* 1091:     */     }
/* 1092:1543 */     this.m_Cnsqt = ((double[][])this.m_Targets.get(this.m_Targets.size() - 1))[0];
/* 1093:1544 */     this.m_DefDstr = ((double[][])this.m_Targets.get(this.m_Targets.size() - 1))[1];
/* 1094:     */   }
/* 1095:     */   
/* 1096:     */   private Instances[] computeInfoGain(Instances instances, double defInfo, Antd antd)
/* 1097:     */   {
/* 1098:1557 */     Instances data = new Instances(instances);
/* 1099:     */     
/* 1100:     */ 
/* 1101:     */ 
/* 1102:     */ 
/* 1103:     */ 
/* 1104:1563 */     Instances[] splitData = antd.splitData(data, defInfo);
/* 1105:1564 */     Instances[] coveredData = new Instances[2];
/* 1106:     */     
/* 1107:     */ 
/* 1108:1567 */     Instances tmp1 = new Instances(data, 0);
/* 1109:1568 */     Instances tmp2 = new Instances(data, 0);
/* 1110:1570 */     if (splitData == null) {
/* 1111:1571 */       return null;
/* 1112:     */     }
/* 1113:1574 */     for (int x = 0; x < splitData.length - 1; x++) {
/* 1114:1575 */       if (x == (int)antd.getAttrValue()) {
/* 1115:1576 */         tmp1 = splitData[x];
/* 1116:     */       } else {
/* 1117:1578 */         for (int y = 0; y < splitData[x].numInstances(); y++) {
/* 1118:1579 */           tmp2.add(splitData[x].instance(y));
/* 1119:     */         }
/* 1120:     */       }
/* 1121:     */     }
/* 1122:1584 */     if (antd.getAttr().isNominal())
/* 1123:     */     {
/* 1124:1585 */       if (((NominalAntd)antd).isIn())
/* 1125:     */       {
/* 1126:1586 */         coveredData[0] = new Instances(tmp1);
/* 1127:1587 */         coveredData[1] = new Instances(tmp2);
/* 1128:     */       }
/* 1129:     */       else
/* 1130:     */       {
/* 1131:1589 */         coveredData[0] = new Instances(tmp2);
/* 1132:1590 */         coveredData[1] = new Instances(tmp1);
/* 1133:     */       }
/* 1134:     */     }
/* 1135:     */     else
/* 1136:     */     {
/* 1137:1593 */       coveredData[0] = new Instances(tmp1);
/* 1138:1594 */       coveredData[1] = new Instances(tmp2);
/* 1139:     */     }
/* 1140:1598 */     for (int z = 0; z < splitData[(splitData.length - 1)].numInstances(); z++) {
/* 1141:1599 */       coveredData[1].add(splitData[(splitData.length - 1)].instance(z));
/* 1142:     */     }
/* 1143:1602 */     return coveredData;
/* 1144:     */   }
/* 1145:     */   
/* 1146:     */   private void prune(Instances pruneData)
/* 1147:     */   {
/* 1148:1612 */     Instances data = new Instances(pruneData);
/* 1149:1613 */     Instances otherData = new Instances(data, 0);
/* 1150:1614 */     double total = data.sumOfWeights();
/* 1151:     */     double defAccu;
/* 1152:     */     double defAccu;
/* 1153:1618 */     if (this.m_ClassAttribute.isNumeric())
/* 1154:     */     {
/* 1155:1619 */       defAccu = meanSquaredError(pruneData, ((double[][])this.m_Targets.get(0))[0][0]);
/* 1156:     */     }
/* 1157:     */     else
/* 1158:     */     {
/* 1159:1621 */       int predict = Utils.maxIndex(((double[][])this.m_Targets.get(0))[0]);
/* 1160:1622 */       defAccu = computeAccu(pruneData, predict) / total;
/* 1161:     */     }
/* 1162:1625 */     int size = this.m_Antds.size();
/* 1163:1626 */     if (size == 0)
/* 1164:     */     {
/* 1165:1627 */       this.m_Cnsqt = ((double[][])this.m_Targets.get(this.m_Targets.size() - 1))[0];
/* 1166:1628 */       this.m_DefDstr = ((double[][])this.m_Targets.get(this.m_Targets.size() - 1))[1];
/* 1167:1629 */       return;
/* 1168:     */     }
/* 1169:1632 */     double[] worthValue = new double[size];
/* 1170:1635 */     for (int x = 0; x < size; x++)
/* 1171:     */     {
/* 1172:1636 */       Antd antd = (Antd)this.m_Antds.get(x);
/* 1173:1637 */       Instances newData = new Instances(data);
/* 1174:1638 */       if (Utils.eq(newData.sumOfWeights(), 0.0D)) {
/* 1175:     */         break;
/* 1176:     */       }
/* 1177:1642 */       data = new Instances(newData, newData.numInstances());
/* 1178:1644 */       for (int y = 0; y < newData.numInstances(); y++)
/* 1179:     */       {
/* 1180:1645 */         Instance ins = newData.instance(y);
/* 1181:1646 */         if (antd.isCover(ins)) {
/* 1182:1647 */           data.add(ins);
/* 1183:     */         } else {
/* 1184:1649 */           otherData.add(ins);
/* 1185:     */         }
/* 1186:     */       }
/* 1187:1654 */       double[][] classes = (double[][])this.m_Targets.get(x + 1);
/* 1188:     */       double other;
/* 1189:     */       double covered;
/* 1190:     */       double other;
/* 1191:1656 */       if (this.m_ClassAttribute.isNominal())
/* 1192:     */       {
/* 1193:1657 */         int coverClass = Utils.maxIndex(classes[0]);int otherClass = Utils.maxIndex(classes[1]);
/* 1194:     */         
/* 1195:     */ 
/* 1196:1660 */         double covered = computeAccu(data, coverClass);
/* 1197:1661 */         other = computeAccu(otherData, otherClass);
/* 1198:     */       }
/* 1199:     */       else
/* 1200:     */       {
/* 1201:1663 */         double coverClass = classes[0][0];double otherClass = classes[1][0];
/* 1202:1664 */         covered = data.sumOfWeights() * meanSquaredError(data, coverClass);
/* 1203:1665 */         other = otherData.sumOfWeights() * meanSquaredError(otherData, otherClass);
/* 1204:     */       }
/* 1205:1669 */       worthValue[x] = ((covered + other) / total);
/* 1206:     */     }
/* 1207:1673 */     for (int z = size - 1; z > 0; z--)
/* 1208:     */     {
/* 1209:     */       double valueDelta;
/* 1210:     */       double valueDelta;
/* 1211:1676 */       if (this.m_ClassAttribute.isNominal())
/* 1212:     */       {
/* 1213:     */         double valueDelta;
/* 1214:1677 */         if (Utils.sm(worthValue[z], 1.0D)) {
/* 1215:1678 */           valueDelta = (worthValue[z] - worthValue[(z - 1)]) / worthValue[z];
/* 1216:     */         } else {
/* 1217:1680 */           valueDelta = worthValue[z] - worthValue[(z - 1)];
/* 1218:     */         }
/* 1219:     */       }
/* 1220:     */       else
/* 1221:     */       {
/* 1222:     */         double valueDelta;
/* 1223:1683 */         if (Utils.sm(worthValue[z], 1.0D)) {
/* 1224:1684 */           valueDelta = (worthValue[(z - 1)] - worthValue[z]) / worthValue[z];
/* 1225:     */         } else {
/* 1226:1686 */           valueDelta = worthValue[(z - 1)] - worthValue[z];
/* 1227:     */         }
/* 1228:     */       }
/* 1229:1690 */       if (!Utils.smOrEq(valueDelta, 0.0D)) {
/* 1230:     */         break;
/* 1231:     */       }
/* 1232:1691 */       this.m_Antds.remove(z);
/* 1233:1692 */       this.m_Targets.remove(z + 1);
/* 1234:     */     }
/* 1235:1699 */     if (this.m_Antds.size() == 1)
/* 1236:     */     {
/* 1237:     */       double valueDelta;
/* 1238:     */       double valueDelta;
/* 1239:1701 */       if (this.m_ClassAttribute.isNominal())
/* 1240:     */       {
/* 1241:     */         double valueDelta;
/* 1242:1702 */         if (Utils.sm(worthValue[0], 1.0D)) {
/* 1243:1703 */           valueDelta = (worthValue[0] - defAccu) / worthValue[0];
/* 1244:     */         } else {
/* 1245:1705 */           valueDelta = worthValue[0] - defAccu;
/* 1246:     */         }
/* 1247:     */       }
/* 1248:     */       else
/* 1249:     */       {
/* 1250:     */         double valueDelta;
/* 1251:1708 */         if (Utils.sm(worthValue[0], 1.0D)) {
/* 1252:1709 */           valueDelta = (defAccu - worthValue[0]) / worthValue[0];
/* 1253:     */         } else {
/* 1254:1711 */           valueDelta = defAccu - worthValue[0];
/* 1255:     */         }
/* 1256:     */       }
/* 1257:1715 */       if (Utils.smOrEq(valueDelta, 0.0D))
/* 1258:     */       {
/* 1259:1716 */         this.m_Antds.clear();
/* 1260:1717 */         this.m_Targets.remove(1);
/* 1261:     */       }
/* 1262:     */     }
/* 1263:1721 */     this.m_Cnsqt = ((double[][])this.m_Targets.get(this.m_Targets.size() - 1))[0];
/* 1264:1722 */     this.m_DefDstr = ((double[][])this.m_Targets.get(this.m_Targets.size() - 1))[1];
/* 1265:     */   }
/* 1266:     */   
/* 1267:     */   private double computeAccu(Instances data, int clas)
/* 1268:     */   {
/* 1269:1734 */     double accu = 0.0D;
/* 1270:1735 */     for (int i = 0; i < data.numInstances(); i++)
/* 1271:     */     {
/* 1272:1736 */       Instance inst = data.instance(i);
/* 1273:1737 */       if ((int)inst.classValue() == clas) {
/* 1274:1738 */         accu += inst.weight();
/* 1275:     */       }
/* 1276:     */     }
/* 1277:1741 */     return accu;
/* 1278:     */   }
/* 1279:     */   
/* 1280:     */   private double meanSquaredError(Instances data, double mean)
/* 1281:     */   {
/* 1282:1753 */     if (Utils.eq(data.sumOfWeights(), 0.0D)) {
/* 1283:1754 */       return 0.0D;
/* 1284:     */     }
/* 1285:1757 */     double mSqErr = 0.0D;double sum = data.sumOfWeights();
/* 1286:1758 */     for (int i = 0; i < data.numInstances(); i++)
/* 1287:     */     {
/* 1288:1759 */       Instance datum = data.instance(i);
/* 1289:1760 */       mSqErr += datum.weight() * (datum.classValue() - mean) * (datum.classValue() - mean);
/* 1290:     */     }
/* 1291:1764 */     return mSqErr / sum;
/* 1292:     */   }
/* 1293:     */   
/* 1294:     */   public String toString(String att, String cl)
/* 1295:     */   {
/* 1296:1775 */     StringBuffer text = new StringBuffer();
/* 1297:1776 */     if (this.m_Antds.size() > 0)
/* 1298:     */     {
/* 1299:1777 */       for (int j = 0; j < this.m_Antds.size() - 1; j++) {
/* 1300:1778 */         text.append("(" + ((Antd)this.m_Antds.get(j)).toString() + ") and ");
/* 1301:     */       }
/* 1302:1780 */       text.append("(" + ((Antd)this.m_Antds.get(this.m_Antds.size() - 1)).toString() + ")");
/* 1303:     */     }
/* 1304:1782 */     text.append(" => " + att + " = " + cl);
/* 1305:     */     
/* 1306:1784 */     return text.toString();
/* 1307:     */   }
/* 1308:     */   
/* 1309:     */   public String toString()
/* 1310:     */   {
/* 1311:1794 */     String title = "\n\nSingle conjunctive rule learner:\n--------------------------------\n";
/* 1312:1795 */     String body = null;
/* 1313:1796 */     StringBuffer text = new StringBuffer();
/* 1314:1797 */     if (this.m_ClassAttribute != null) {
/* 1315:1798 */       if (this.m_ClassAttribute.isNominal())
/* 1316:     */       {
/* 1317:1799 */         body = toString(this.m_ClassAttribute.name(), this.m_ClassAttribute.value(Utils.maxIndex(this.m_Cnsqt)));
/* 1318:     */         
/* 1319:     */ 
/* 1320:1802 */         text.append("\n\nClass distributions:\nCovered by the rule:\n");
/* 1321:1803 */         for (int k = 0; k < this.m_Cnsqt.length; k++) {
/* 1322:1804 */           text.append(this.m_ClassAttribute.value(k) + "\t");
/* 1323:     */         }
/* 1324:1806 */         text.append('\n');
/* 1325:1807 */         for (double element : this.m_Cnsqt) {
/* 1326:1808 */           text.append(Utils.doubleToString(element, 6) + "\t");
/* 1327:     */         }
/* 1328:1811 */         text.append("\n\nNot covered by the rule:\n");
/* 1329:1812 */         for (int k = 0; k < this.m_DefDstr.length; k++) {
/* 1330:1813 */           text.append(this.m_ClassAttribute.value(k) + "\t");
/* 1331:     */         }
/* 1332:1815 */         text.append('\n');
/* 1333:1816 */         for (double element : this.m_DefDstr) {
/* 1334:1817 */           text.append(Utils.doubleToString(element, 6) + "\t");
/* 1335:     */         }
/* 1336:     */       }
/* 1337:     */       else
/* 1338:     */       {
/* 1339:1820 */         body = toString(this.m_ClassAttribute.name(), Utils.doubleToString(this.m_Cnsqt[0], 6));
/* 1340:     */       }
/* 1341:     */     }
/* 1342:1824 */     return title + body + text.toString();
/* 1343:     */   }
/* 1344:     */   
/* 1345:     */   public String getRevision()
/* 1346:     */   {
/* 1347:1834 */     return RevisionUtils.extract("$Revision: 10335 $");
/* 1348:     */   }
/* 1349:     */   
/* 1350:     */   public static void main(String[] args)
/* 1351:     */   {
/* 1352:1843 */     runClassifier(new ConjunctiveRule(), args);
/* 1353:     */   }
/* 1354:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.rules.ConjunctiveRule
 * JD-Core Version:    0.7.0.1
 */