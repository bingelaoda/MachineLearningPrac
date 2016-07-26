/*    1:     */ package weka.classifiers.rules;
/*    2:     */ 
/*    3:     */ import java.io.Serializable;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Collections;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.Random;
/*    8:     */ import java.util.Vector;
/*    9:     */ import weka.classifiers.AbstractClassifier;
/*   10:     */ import weka.core.AdditionalMeasureProducer;
/*   11:     */ import weka.core.Attribute;
/*   12:     */ import weka.core.Capabilities;
/*   13:     */ import weka.core.Capabilities.Capability;
/*   14:     */ import weka.core.Instance;
/*   15:     */ import weka.core.Instances;
/*   16:     */ import weka.core.Option;
/*   17:     */ import weka.core.RevisionHandler;
/*   18:     */ import weka.core.RevisionUtils;
/*   19:     */ import weka.core.UnsupportedClassTypeException;
/*   20:     */ import weka.core.Utils;
/*   21:     */ import weka.core.WeightedInstancesHandler;
/*   22:     */ 
/*   23:     */ public class Ridor
/*   24:     */   extends AbstractClassifier
/*   25:     */   implements AdditionalMeasureProducer, WeightedInstancesHandler
/*   26:     */ {
/*   27:     */   static final long serialVersionUID = -7261533075088314436L;
/*   28:     */   private int m_Folds;
/*   29:     */   private int m_Shuffle;
/*   30:     */   private Random m_Random;
/*   31:     */   private int m_Seed;
/*   32:     */   private boolean m_IsAllErr;
/*   33:     */   private boolean m_IsMajority;
/*   34:     */   private Ridor_node m_Root;
/*   35:     */   private Attribute m_Class;
/*   36:     */   private double m_Cover;
/*   37:     */   private double m_Err;
/*   38:     */   private double m_MinNo;
/*   39:     */   
/*   40:     */   public Ridor()
/*   41:     */   {
/*   42: 128 */     this.m_Folds = 3;
/*   43:     */     
/*   44:     */ 
/*   45: 131 */     this.m_Shuffle = 1;
/*   46:     */     
/*   47:     */ 
/*   48: 134 */     this.m_Random = null;
/*   49:     */     
/*   50:     */ 
/*   51: 137 */     this.m_Seed = 1;
/*   52:     */     
/*   53:     */ 
/*   54: 140 */     this.m_IsAllErr = false;
/*   55:     */     
/*   56:     */ 
/*   57: 143 */     this.m_IsMajority = false;
/*   58:     */     
/*   59:     */ 
/*   60: 146 */     this.m_Root = null;
/*   61:     */     
/*   62:     */ 
/*   63:     */ 
/*   64:     */ 
/*   65:     */ 
/*   66:     */ 
/*   67:     */ 
/*   68:     */ 
/*   69: 155 */     this.m_MinNo = 2.0D;
/*   70:     */   }
/*   71:     */   
/*   72:     */   public String globalInfo()
/*   73:     */   {
/*   74: 164 */     return "An implementation of a RIpple-DOwn Rule learner.\n\nIt generates a default rule first and then the exceptions for the default rule with the least (weighted) error rate.  Then it generates the \"best\" exceptions for each exception and iterates until pure.  Thus it performs a tree-like expansion of exceptions.The exceptions are a set of rules that predict classes other than the default. IREP is used to generate the exceptions.\n\nFor more information about Ripple-Down Rules, see:\n\n";
/*   75:     */   }
/*   76:     */   
/*   77:     */   private class Ridor_node
/*   78:     */     implements Serializable, RevisionHandler
/*   79:     */   {
/*   80:     */     static final long serialVersionUID = -581370560157467677L;
/*   81: 185 */     private double defClass = (0.0D / 0.0D);
/*   82: 192 */     private Ridor.RidorRule[] rules = null;
/*   83: 195 */     private Ridor_node[] excepts = null;
/*   84:     */     private int level;
/*   85:     */     
/*   86:     */     private Ridor_node() {}
/*   87:     */     
/*   88:     */     public double getDefClass()
/*   89:     */     {
/*   90: 206 */       return this.defClass;
/*   91:     */     }
/*   92:     */     
/*   93:     */     public Ridor.RidorRule[] getRules()
/*   94:     */     {
/*   95: 215 */       return this.rules;
/*   96:     */     }
/*   97:     */     
/*   98:     */     public Ridor_node[] getExcepts()
/*   99:     */     {
/*  100: 224 */       return this.excepts;
/*  101:     */     }
/*  102:     */     
/*  103:     */     public void findRules(Instances[] dataByClass, int lvl)
/*  104:     */       throws Exception
/*  105:     */     {
/*  106: 236 */       Vector<Ridor.RidorRule> finalRules = null;
/*  107: 237 */       int clas = -1;
/*  108: 238 */       double[] isPure = new double[dataByClass.length];
/*  109: 239 */       int numMajority = 0;
/*  110:     */       
/*  111: 241 */       this.level = (lvl + 1);
/*  112: 243 */       for (int h = 0; h < dataByClass.length; h++)
/*  113:     */       {
/*  114: 244 */         isPure[h] = dataByClass[h].sumOfWeights();
/*  115: 245 */         if (Utils.grOrEq(isPure[h], Ridor.this.m_Folds)) {
/*  116: 246 */           numMajority++;
/*  117:     */         }
/*  118:     */       }
/*  119: 250 */       if (numMajority <= 1)
/*  120:     */       {
/*  121: 251 */         this.defClass = Utils.maxIndex(isPure);
/*  122: 252 */         return;
/*  123:     */       }
/*  124: 254 */       double total = Utils.sum(isPure);
/*  125: 256 */       if (Ridor.this.m_IsMajority)
/*  126:     */       {
/*  127: 257 */         this.defClass = Utils.maxIndex(isPure);
/*  128: 258 */         Instances data = new Instances(dataByClass[((int)this.defClass)]);
/*  129: 259 */         int index = data.classIndex();
/*  130: 261 */         for (int j = 0; j < data.numInstances(); j++) {
/*  131: 262 */           data.instance(j).setClassValue(1.0D);
/*  132:     */         }
/*  133: 265 */         for (int k = 0; k < dataByClass.length; k++) {
/*  134: 266 */           if (k != (int)this.defClass) {
/*  135: 267 */             if (data.numInstances() >= dataByClass[k].numInstances()) {
/*  136: 268 */               data = append(data, dataByClass[k]);
/*  137:     */             } else {
/*  138: 270 */               data = append(dataByClass[k], data);
/*  139:     */             }
/*  140:     */           }
/*  141:     */         }
/*  142: 275 */         data.setClassIndex(index);
/*  143:     */         
/*  144: 277 */         double classCount = total - isPure[((int)this.defClass)];
/*  145: 278 */         finalRules = new Vector();
/*  146: 279 */         buildRuleset(data, classCount, finalRules);
/*  147: 280 */         if (finalRules.size() == 0) {
/*  148: 281 */           return;
/*  149:     */         }
/*  150:     */       }
/*  151:     */       else
/*  152:     */       {
/*  153: 284 */         double maxAcRt = isPure[Utils.maxIndex(isPure)] / total;
/*  154: 287 */         for (int i = 0; i < dataByClass.length; i++) {
/*  155: 288 */           if (isPure[i] >= Ridor.this.m_Folds)
/*  156:     */           {
/*  157: 289 */             Instances data = new Instances(dataByClass[i]);
/*  158: 290 */             int index = data.classIndex();
/*  159: 292 */             for (int j = 0; j < data.numInstances(); j++) {
/*  160: 293 */               data.instance(j).setClassValue(1.0D);
/*  161:     */             }
/*  162: 296 */             for (int k = 0; k < dataByClass.length; k++) {
/*  163: 297 */               if (k != i) {
/*  164: 298 */                 if (data.numInstances() >= dataByClass[k].numInstances()) {
/*  165: 299 */                   data = append(data, dataByClass[k]);
/*  166:     */                 } else {
/*  167: 301 */                   data = append(dataByClass[k], data);
/*  168:     */                 }
/*  169:     */               }
/*  170:     */             }
/*  171: 306 */             data.setClassIndex(index);
/*  172:     */             
/*  173:     */ 
/*  174: 309 */             double classCount = data.sumOfWeights() - isPure[i];
/*  175: 310 */             Vector<Ridor.RidorRule> ruleset = new Vector();
/*  176: 311 */             double wAcRt = buildRuleset(data, classCount, ruleset);
/*  177: 313 */             if (Utils.gr(wAcRt, maxAcRt))
/*  178:     */             {
/*  179: 314 */               finalRules = ruleset;
/*  180: 315 */               maxAcRt = wAcRt;
/*  181: 316 */               clas = i;
/*  182:     */             }
/*  183:     */           }
/*  184:     */         }
/*  185: 321 */         if (finalRules == null)
/*  186:     */         {
/*  187: 323 */           this.defClass = Utils.maxIndex(isPure);
/*  188: 324 */           return;
/*  189:     */         }
/*  190: 327 */         this.defClass = clas;
/*  191:     */       }
/*  192: 331 */       int size = finalRules.size();
/*  193: 332 */       this.rules = new Ridor.RidorRule[size];
/*  194: 333 */       this.excepts = new Ridor_node[size];
/*  195: 334 */       for (int l = 0; l < size; l++) {
/*  196: 335 */         this.rules[l] = ((Ridor.RidorRule)finalRules.elementAt(l));
/*  197:     */       }
/*  198: 339 */       Instances[] uncovered = dataByClass;
/*  199: 340 */       if (this.level == 1) {
/*  200: 341 */         Ridor.this.m_Err = (total - uncovered[((int)this.defClass)].sumOfWeights());
/*  201:     */       }
/*  202: 344 */       uncovered[((int)this.defClass)] = new Instances(uncovered[((int)this.defClass)], 0);
/*  203: 346 */       for (int m = 0; m < size; m++)
/*  204:     */       {
/*  205: 351 */         Instances[][] dvdData = divide(this.rules[m], uncovered);
/*  206: 352 */         Instances[] covered = dvdData[0];
/*  207:     */         
/*  208: 354 */         this.excepts[m] = new Ridor_node(Ridor.this);
/*  209: 355 */         this.excepts[m].findRules(covered, this.level);
/*  210:     */       }
/*  211:     */     }
/*  212:     */     
/*  213:     */     private double buildRuleset(Instances insts, double classCount, Vector<Ridor.RidorRule> ruleset)
/*  214:     */       throws Exception
/*  215:     */     {
/*  216: 373 */       Instances data = new Instances(insts);
/*  217: 374 */       double wAcRt = 0.0D;
/*  218: 375 */       double total = data.sumOfWeights();
/*  219: 377 */       while (classCount >= Ridor.this.m_Folds)
/*  220:     */       {
/*  221: 378 */         Ridor.RidorRule bestRule = null;
/*  222: 379 */         double bestWorthRate = -1.0D;
/*  223: 380 */         double bestWorth = -1.0D;
/*  224:     */         
/*  225: 382 */         Ridor.RidorRule rule = new Ridor.RidorRule(Ridor.this, null);
/*  226: 383 */         rule.setPredictedClass(0.0D);
/*  227: 385 */         for (int j = 0; j < Ridor.this.m_Shuffle; j++)
/*  228:     */         {
/*  229: 386 */           if (Ridor.this.m_Shuffle > 1) {
/*  230: 387 */             data.randomize(Ridor.this.m_Random);
/*  231:     */           }
/*  232: 390 */           rule.buildClassifier(data);
/*  233:     */           double w;
/*  234:     */           double wr;
/*  235:     */           double w;
/*  236: 393 */           if (Ridor.this.m_IsAllErr)
/*  237:     */           {
/*  238: 394 */             double wr = (rule.getWorth() + rule.getAccuG()) / (rule.getCoverP() + rule.getCoverG());
/*  239:     */             
/*  240: 396 */             w = rule.getWorth() + rule.getAccuG();
/*  241:     */           }
/*  242:     */           else
/*  243:     */           {
/*  244: 398 */             wr = rule.getWorthRate();
/*  245: 399 */             w = rule.getWorth();
/*  246:     */           }
/*  247: 402 */           if ((Utils.gr(wr, bestWorthRate)) || ((Utils.eq(wr, bestWorthRate)) && (Utils.gr(w, bestWorth))))
/*  248:     */           {
/*  249: 404 */             bestRule = rule;
/*  250: 405 */             bestWorthRate = wr;
/*  251: 406 */             bestWorth = w;
/*  252:     */           }
/*  253:     */         }
/*  254: 410 */         if (bestRule == null) {
/*  255: 411 */           throw new Exception("Something wrong here inside findRule()!");
/*  256:     */         }
/*  257: 414 */         if ((Utils.sm(bestWorthRate, 0.5D)) || (!bestRule.hasAntds())) {
/*  258:     */           break;
/*  259:     */         }
/*  260: 418 */         Instances newData = new Instances(data);
/*  261: 419 */         data = new Instances(newData, 0);
/*  262: 420 */         classCount = 0.0D;
/*  263: 421 */         double cover = 0.0D;
/*  264: 423 */         for (int l = 0; l < newData.numInstances(); l++)
/*  265:     */         {
/*  266: 424 */           Instance datum = newData.instance(l);
/*  267: 425 */           if (!bestRule.isCover(datum))
/*  268:     */           {
/*  269: 427 */             data.add(datum);
/*  270: 428 */             if (Utils.eq(datum.classValue(), 0.0D)) {
/*  271: 429 */               classCount += datum.weight();
/*  272:     */             }
/*  273:     */           }
/*  274:     */           else
/*  275:     */           {
/*  276: 432 */             cover += datum.weight();
/*  277:     */           }
/*  278:     */         }
/*  279: 436 */         wAcRt += computeWeightedAcRt(bestWorthRate, cover, total);
/*  280: 437 */         ruleset.addElement(bestRule);
/*  281:     */       }
/*  282: 441 */       double wDefAcRt = (data.sumOfWeights() - classCount) / total;
/*  283: 442 */       wAcRt += wDefAcRt;
/*  284:     */       
/*  285: 444 */       return wAcRt;
/*  286:     */     }
/*  287:     */     
/*  288:     */     private Instances append(Instances data1, Instances data2)
/*  289:     */     {
/*  290: 455 */       Instances data = new Instances(data1);
/*  291: 456 */       for (int i = 0; i < data2.numInstances(); i++) {
/*  292: 457 */         data.add(data2.instance(i));
/*  293:     */       }
/*  294: 460 */       return data;
/*  295:     */     }
/*  296:     */     
/*  297:     */     private double computeWeightedAcRt(double worthRt, double cover, double total)
/*  298:     */     {
/*  299: 482 */       return worthRt * (cover / total);
/*  300:     */     }
/*  301:     */     
/*  302:     */     private Instances[][] divide(Ridor.RidorRule rule, Instances[] dataByClass)
/*  303:     */     {
/*  304: 496 */       int len = dataByClass.length;
/*  305: 497 */       Instances[][] dataBags = new Instances[2][len];
/*  306: 499 */       for (int i = 0; i < len; i++)
/*  307:     */       {
/*  308: 500 */         Instances[] dvdData = rule.coveredByRule(dataByClass[i]);
/*  309: 501 */         dataBags[0][i] = dvdData[0];
/*  310: 502 */         dataBags[1][i] = dvdData[1];
/*  311:     */       }
/*  312: 505 */       return dataBags;
/*  313:     */     }
/*  314:     */     
/*  315:     */     public int size()
/*  316:     */     {
/*  317: 515 */       int size = 0;
/*  318: 516 */       if (this.rules != null)
/*  319:     */       {
/*  320: 517 */         for (int i = 0; i < this.rules.length; i++) {
/*  321: 518 */           size += this.excepts[i].size();
/*  322:     */         }
/*  323: 520 */         size += this.rules.length;
/*  324:     */       }
/*  325: 522 */       return size;
/*  326:     */     }
/*  327:     */     
/*  328:     */     public String toString()
/*  329:     */     {
/*  330: 532 */       StringBuffer text = new StringBuffer();
/*  331: 534 */       if (this.level == 1) {
/*  332: 535 */         text.append(Ridor.this.m_Class.name() + " = " + Ridor.this.m_Class.value((int)getDefClass()) + "  (" + Ridor.this.m_Cover + "/" + Ridor.this.m_Err + ")\n");
/*  333:     */       }
/*  334: 538 */       if (this.rules != null) {
/*  335: 539 */         for (int i = 0; i < this.rules.length; i++)
/*  336:     */         {
/*  337: 540 */           for (int j = 0; j < this.level; j++) {
/*  338: 541 */             text.append("         ");
/*  339:     */           }
/*  340: 543 */           String cl = Ridor.this.m_Class.value((int)this.excepts[i].getDefClass());
/*  341: 544 */           text.append("  Except " + this.rules[i].toString(Ridor.this.m_Class.name(), cl) + "\n" + this.excepts[i].toString());
/*  342:     */         }
/*  343:     */       }
/*  344: 549 */       return text.toString();
/*  345:     */     }
/*  346:     */     
/*  347:     */     public String getRevision()
/*  348:     */     {
/*  349: 559 */       return RevisionUtils.extract("$Revision: 10375 $");
/*  350:     */     }
/*  351:     */   }
/*  352:     */   
/*  353:     */   private class RidorRule
/*  354:     */     implements WeightedInstancesHandler, Serializable, RevisionHandler
/*  355:     */   {
/*  356:     */     static final long serialVersionUID = 4375199423973848157L;
/*  357: 581 */     private double m_Class = -1.0D;
/*  358:     */     private Attribute m_ClassAttribute;
/*  359: 587 */     protected ArrayList<Ridor.Antd> m_Antds = null;
/*  360: 593 */     private double m_WorthRate = 0.0D;
/*  361: 596 */     private double m_Worth = 0.0D;
/*  362: 599 */     private double m_CoverP = 0.0D;
/*  363: 602 */     private double m_CoverG = 0.0D;
/*  364: 602 */     private double m_AccuG = 0.0D;
/*  365:     */     
/*  366:     */     private RidorRule() {}
/*  367:     */     
/*  368:     */     public void setPredictedClass(double cl)
/*  369:     */     {
/*  370: 606 */       this.m_Class = cl;
/*  371:     */     }
/*  372:     */     
/*  373:     */     public void buildClassifier(Instances instances)
/*  374:     */       throws Exception
/*  375:     */     {
/*  376: 617 */       this.m_ClassAttribute = instances.classAttribute();
/*  377: 618 */       if (!this.m_ClassAttribute.isNominal()) {
/*  378: 619 */         throw new UnsupportedClassTypeException(" Only nominal class, please.");
/*  379:     */       }
/*  380: 621 */       if (instances.numClasses() != 2) {
/*  381: 622 */         throw new Exception(" Only 2 classes, please.");
/*  382:     */       }
/*  383: 625 */       Instances data = new Instances(instances);
/*  384: 626 */       if (Utils.eq(data.sumOfWeights(), 0.0D)) {
/*  385: 627 */         throw new Exception(" No training data.");
/*  386:     */       }
/*  387: 630 */       data.deleteWithMissingClass();
/*  388: 631 */       if (Utils.eq(data.sumOfWeights(), 0.0D)) {
/*  389: 632 */         throw new Exception(" The class labels of all the training data are missing.");
/*  390:     */       }
/*  391: 636 */       if (data.numInstances() < Ridor.this.m_Folds) {
/*  392: 637 */         throw new Exception(" Not enough data for REP.");
/*  393:     */       }
/*  394: 640 */       this.m_Antds = new ArrayList();
/*  395:     */       
/*  396:     */ 
/*  397: 643 */       Ridor.this.m_Random = new Random(Ridor.this.m_Seed);
/*  398: 644 */       data.randomize(Ridor.this.m_Random);
/*  399: 645 */       data.stratify(Ridor.this.m_Folds);
/*  400: 646 */       Instances growData = data.trainCV(Ridor.this.m_Folds, Ridor.this.m_Folds - 1, Ridor.this.m_Random);
/*  401: 647 */       Instances pruneData = data.testCV(Ridor.this.m_Folds, Ridor.this.m_Folds - 1);
/*  402:     */       
/*  403: 649 */       grow(growData);
/*  404:     */       
/*  405: 651 */       prune(pruneData);
/*  406:     */     }
/*  407:     */     
/*  408:     */     public Instances[] coveredByRule(Instances insts)
/*  409:     */     {
/*  410: 663 */       Instances[] data = new Instances[2];
/*  411: 664 */       data[0] = new Instances(insts, insts.numInstances());
/*  412: 665 */       data[1] = new Instances(insts, insts.numInstances());
/*  413: 667 */       for (int i = 0; i < insts.numInstances(); i++)
/*  414:     */       {
/*  415: 668 */         Instance datum = insts.instance(i);
/*  416: 669 */         if (isCover(datum)) {
/*  417: 670 */           data[0].add(datum);
/*  418:     */         } else {
/*  419: 672 */           data[1].add(datum);
/*  420:     */         }
/*  421:     */       }
/*  422: 676 */       return data;
/*  423:     */     }
/*  424:     */     
/*  425:     */     public boolean isCover(Instance datum)
/*  426:     */     {
/*  427: 687 */       boolean isCover = true;
/*  428: 689 */       for (int i = 0; i < this.m_Antds.size(); i++)
/*  429:     */       {
/*  430: 690 */         Ridor.Antd antd = (Ridor.Antd)this.m_Antds.get(i);
/*  431: 691 */         if (!antd.isCover(datum))
/*  432:     */         {
/*  433: 692 */           isCover = false;
/*  434: 693 */           break;
/*  435:     */         }
/*  436:     */       }
/*  437: 697 */       return isCover;
/*  438:     */     }
/*  439:     */     
/*  440:     */     public boolean hasAntds()
/*  441:     */     {
/*  442: 706 */       if (this.m_Antds == null) {
/*  443: 707 */         return false;
/*  444:     */       }
/*  445: 709 */       return this.m_Antds.size() > 0;
/*  446:     */     }
/*  447:     */     
/*  448:     */     private void grow(Instances data)
/*  449:     */     {
/*  450: 719 */       Instances growData = new Instances(data);
/*  451:     */       
/*  452: 721 */       this.m_AccuG = computeDefAccu(growData);
/*  453: 722 */       this.m_CoverG = growData.sumOfWeights();
/*  454:     */       
/*  455: 724 */       double defAcRt = this.m_AccuG / this.m_CoverG;
/*  456:     */       
/*  457:     */ 
/*  458: 727 */       boolean[] used = new boolean[growData.numAttributes()];
/*  459: 728 */       for (int k = 0; k < used.length; k++) {
/*  460: 729 */         used[k] = false;
/*  461:     */       }
/*  462: 731 */       int numUnused = used.length;
/*  463:     */       
/*  464:     */ 
/*  465: 734 */       boolean isContinue = true;
/*  466: 736 */       while (isContinue)
/*  467:     */       {
/*  468: 737 */         double maxInfoGain = 0.0D;
/*  469:     */         
/*  470:     */ 
/*  471: 740 */         Ridor.Antd oneAntd = null;
/*  472: 741 */         Instances coverData = null;
/*  473: 742 */         Enumeration<Attribute> enumAttr = growData.enumerateAttributes();
/*  474: 743 */         int index = -1;
/*  475: 746 */         while (enumAttr.hasMoreElements())
/*  476:     */         {
/*  477: 747 */           Attribute att = (Attribute)enumAttr.nextElement();
/*  478: 748 */           index++;
/*  479:     */           
/*  480: 750 */           Ridor.Antd antd = null;
/*  481: 751 */           if (att.isNumeric()) {
/*  482: 752 */             antd = new Ridor.NumericAntd(Ridor.this, att);
/*  483:     */           } else {
/*  484: 754 */             antd = new Ridor.NominalAntd(Ridor.this, att);
/*  485:     */           }
/*  486: 757 */           if (used[index] == 0)
/*  487:     */           {
/*  488: 763 */             Instances coveredData = computeInfoGain(growData, defAcRt, antd);
/*  489: 764 */             if (coveredData != null)
/*  490:     */             {
/*  491: 765 */               double infoGain = antd.getMaxInfoGain();
/*  492: 766 */               if (Utils.gr(infoGain, maxInfoGain))
/*  493:     */               {
/*  494: 767 */                 oneAntd = antd;
/*  495: 768 */                 coverData = coveredData;
/*  496: 769 */                 maxInfoGain = infoGain;
/*  497:     */               }
/*  498:     */             }
/*  499:     */           }
/*  500:     */         }
/*  501: 775 */         if (oneAntd == null) {
/*  502: 776 */           return;
/*  503:     */         }
/*  504: 780 */         if (!oneAntd.getAttr().isNumeric())
/*  505:     */         {
/*  506: 781 */           used[oneAntd.getAttr().index()] = true;
/*  507: 782 */           numUnused--;
/*  508:     */         }
/*  509: 785 */         this.m_Antds.add(oneAntd);
/*  510: 786 */         growData = coverData;
/*  511:     */         
/*  512: 788 */         defAcRt = oneAntd.getAccuRate();
/*  513: 791 */         if ((Utils.eq(growData.sumOfWeights(), 0.0D)) || (Utils.eq(defAcRt, 1.0D)) || (numUnused == 0)) {
/*  514: 793 */           isContinue = false;
/*  515:     */         }
/*  516:     */       }
/*  517:     */     }
/*  518:     */     
/*  519:     */     private Instances computeInfoGain(Instances instances, double defAcRt, Ridor.Antd antd)
/*  520:     */     {
/*  521: 808 */       Instances data = new Instances(instances);
/*  522:     */       
/*  523:     */ 
/*  524:     */ 
/*  525:     */ 
/*  526:     */ 
/*  527: 814 */       Instances[] splitData = antd.splitData(data, defAcRt, this.m_Class);
/*  528: 817 */       if (splitData != null) {
/*  529: 818 */         return splitData[((int)antd.getAttrValue())];
/*  530:     */       }
/*  531: 820 */       return null;
/*  532:     */     }
/*  533:     */     
/*  534:     */     private void prune(Instances pruneData)
/*  535:     */     {
/*  536: 831 */       Instances data = new Instances(pruneData);
/*  537:     */       
/*  538: 833 */       double total = data.sumOfWeights();
/*  539:     */       
/*  540:     */ 
/*  541: 836 */       double defAccu = 0.0D;double defAccuRate = 0.0D;
/*  542:     */       
/*  543: 838 */       int size = this.m_Antds.size();
/*  544: 839 */       if (size == 0) {
/*  545: 840 */         return;
/*  546:     */       }
/*  547: 843 */       double[] worthRt = new double[size];
/*  548: 844 */       double[] coverage = new double[size];
/*  549: 845 */       double[] worthValue = new double[size];
/*  550: 846 */       for (int w = 0; w < size; w++)
/*  551:     */       {
/*  552: 847 */         double tmp78_77 = (worthValue[w] = 0.0D);coverage[w] = tmp78_77;worthRt[w] = tmp78_77;
/*  553:     */       }
/*  554: 851 */       for (int x = 0; x < size; x++)
/*  555:     */       {
/*  556: 852 */         Ridor.Antd antd = (Ridor.Antd)this.m_Antds.get(x);
/*  557: 853 */         Attribute attr = antd.getAttr();
/*  558: 854 */         Instances newData = new Instances(data);
/*  559: 855 */         data = new Instances(newData, newData.numInstances());
/*  560: 858 */         for (int y = 0; y < newData.numInstances(); y++)
/*  561:     */         {
/*  562: 859 */           Instance ins = newData.instance(y);
/*  563: 860 */           if ((!ins.isMissing(attr)) && 
/*  564: 861 */             (antd.isCover(ins)))
/*  565:     */           {
/*  566: 862 */             coverage[x] += ins.weight();
/*  567: 863 */             data.add(ins);
/*  568: 864 */             if (Utils.eq(ins.classValue(), this.m_Class)) {
/*  569: 865 */               worthValue[x] += ins.weight();
/*  570:     */             }
/*  571:     */           }
/*  572:     */         }
/*  573: 871 */         if (coverage[x] != 0.0D) {
/*  574: 872 */           worthValue[x] /= coverage[x];
/*  575:     */         }
/*  576:     */       }
/*  577: 877 */       for (int z = size - 1; z > 0; z--)
/*  578:     */       {
/*  579: 878 */         if (!Utils.sm(worthRt[z], worthRt[(z - 1)])) {
/*  580:     */           break;
/*  581:     */         }
/*  582: 879 */         this.m_Antds.remove(z);
/*  583:     */       }
/*  584: 886 */       if (this.m_Antds.size() == 1)
/*  585:     */       {
/*  586: 887 */         defAccu = computeDefAccu(pruneData);
/*  587: 888 */         defAccuRate = defAccu / total;
/*  588: 889 */         if (Utils.sm(worthRt[0], defAccuRate)) {
/*  589: 890 */           this.m_Antds.clear();
/*  590:     */         }
/*  591:     */       }
/*  592: 895 */       int antdsSize = this.m_Antds.size();
/*  593: 896 */       if (antdsSize != 0)
/*  594:     */       {
/*  595: 897 */         this.m_Worth = worthValue[(antdsSize - 1)];
/*  596:     */         
/*  597: 899 */         this.m_WorthRate = worthRt[(antdsSize - 1)];
/*  598: 900 */         this.m_CoverP = coverage[(antdsSize - 1)];
/*  599: 901 */         Ridor.Antd last = (Ridor.Antd)this.m_Antds.get(this.m_Antds.size() - 1);
/*  600: 902 */         this.m_CoverG = last.getCover();
/*  601: 903 */         this.m_AccuG = last.getAccu();
/*  602:     */       }
/*  603:     */       else
/*  604:     */       {
/*  605: 905 */         this.m_Worth = defAccu;
/*  606: 906 */         this.m_WorthRate = defAccuRate;
/*  607: 907 */         this.m_CoverP = total;
/*  608:     */       }
/*  609:     */     }
/*  610:     */     
/*  611:     */     private double computeDefAccu(Instances data)
/*  612:     */     {
/*  613: 919 */       double defAccu = 0.0D;
/*  614: 920 */       for (int i = 0; i < data.numInstances(); i++)
/*  615:     */       {
/*  616: 921 */         Instance inst = data.instance(i);
/*  617: 922 */         if (Utils.eq(inst.classValue(), this.m_Class)) {
/*  618: 923 */           defAccu += inst.weight();
/*  619:     */         }
/*  620:     */       }
/*  621: 926 */       return defAccu;
/*  622:     */     }
/*  623:     */     
/*  624:     */     public double getWorthRate()
/*  625:     */     {
/*  626: 934 */       return this.m_WorthRate;
/*  627:     */     }
/*  628:     */     
/*  629:     */     public double getWorth()
/*  630:     */     {
/*  631: 938 */       return this.m_Worth;
/*  632:     */     }
/*  633:     */     
/*  634:     */     public double getCoverP()
/*  635:     */     {
/*  636: 942 */       return this.m_CoverP;
/*  637:     */     }
/*  638:     */     
/*  639:     */     public double getCoverG()
/*  640:     */     {
/*  641: 946 */       return this.m_CoverG;
/*  642:     */     }
/*  643:     */     
/*  644:     */     public double getAccuG()
/*  645:     */     {
/*  646: 950 */       return this.m_AccuG;
/*  647:     */     }
/*  648:     */     
/*  649:     */     public String toString(String att, String cl)
/*  650:     */     {
/*  651: 962 */       StringBuffer text = new StringBuffer();
/*  652: 963 */       if (this.m_Antds.size() > 0)
/*  653:     */       {
/*  654: 964 */         for (int j = 0; j < this.m_Antds.size() - 1; j++) {
/*  655: 965 */           text.append("(" + ((Ridor.Antd)this.m_Antds.get(j)).toString() + ") and ");
/*  656:     */         }
/*  657: 967 */         text.append("(" + ((Ridor.Antd)this.m_Antds.get(this.m_Antds.size() - 1)).toString() + ")");
/*  658:     */       }
/*  659: 969 */       text.append(" => " + att + " = " + cl);
/*  660: 970 */       text.append("  (" + this.m_CoverG + "/" + (this.m_CoverG - this.m_AccuG) + ") [" + this.m_CoverP + "/" + (this.m_CoverP - this.m_Worth) + "]");
/*  661:     */       
/*  662: 972 */       return text.toString();
/*  663:     */     }
/*  664:     */     
/*  665:     */     public String toString()
/*  666:     */     {
/*  667: 982 */       return toString(this.m_ClassAttribute.name(), this.m_ClassAttribute.value((int)this.m_Class));
/*  668:     */     }
/*  669:     */     
/*  670:     */     public String getRevision()
/*  671:     */     {
/*  672: 993 */       return RevisionUtils.extract("$Revision: 10375 $");
/*  673:     */     }
/*  674:     */   }
/*  675:     */   
/*  676:     */   private abstract class Antd
/*  677:     */     implements Serializable, RevisionHandler
/*  678:     */   {
/*  679:     */     private static final long serialVersionUID = 5317379013858933369L;
/*  680:     */     protected Attribute att;
/*  681:     */     protected double value;
/*  682:     */     protected double maxInfoGain;
/*  683:     */     protected double accuRate;
/*  684:     */     protected double cover;
/*  685:     */     protected double accu;
/*  686:     */     
/*  687:     */     public Antd(Attribute a)
/*  688:     */     {
/*  689:1031 */       this.att = a;
/*  690:1032 */       this.value = (0.0D / 0.0D);
/*  691:1033 */       this.maxInfoGain = 0.0D;
/*  692:1034 */       this.accuRate = (0.0D / 0.0D);
/*  693:1035 */       this.cover = (0.0D / 0.0D);
/*  694:1036 */       this.accu = (0.0D / 0.0D);
/*  695:     */     }
/*  696:     */     
/*  697:     */     public abstract Instances[] splitData(Instances paramInstances, double paramDouble1, double paramDouble2);
/*  698:     */     
/*  699:     */     public abstract boolean isCover(Instance paramInstance);
/*  700:     */     
/*  701:     */     public abstract String toString();
/*  702:     */     
/*  703:     */     public Attribute getAttr()
/*  704:     */     {
/*  705:1050 */       return this.att;
/*  706:     */     }
/*  707:     */     
/*  708:     */     public double getAttrValue()
/*  709:     */     {
/*  710:1054 */       return this.value;
/*  711:     */     }
/*  712:     */     
/*  713:     */     public double getMaxInfoGain()
/*  714:     */     {
/*  715:1058 */       return this.maxInfoGain;
/*  716:     */     }
/*  717:     */     
/*  718:     */     public double getAccuRate()
/*  719:     */     {
/*  720:1062 */       return this.accuRate;
/*  721:     */     }
/*  722:     */     
/*  723:     */     public double getAccu()
/*  724:     */     {
/*  725:1066 */       return this.accu;
/*  726:     */     }
/*  727:     */     
/*  728:     */     public double getCover()
/*  729:     */     {
/*  730:1070 */       return this.cover;
/*  731:     */     }
/*  732:     */     
/*  733:     */     public String getRevision()
/*  734:     */     {
/*  735:1080 */       return RevisionUtils.extract("$Revision: 10375 $");
/*  736:     */     }
/*  737:     */   }
/*  738:     */   
/*  739:     */   private class NumericAntd
/*  740:     */     extends Ridor.Antd
/*  741:     */   {
/*  742:     */     static final long serialVersionUID = 1968761518014492214L;
/*  743:     */     private double splitPoint;
/*  744:     */     
/*  745:     */     public NumericAntd(Attribute a)
/*  746:     */     {
/*  747:1097 */       super(a);
/*  748:1098 */       this.splitPoint = (0.0D / 0.0D);
/*  749:     */     }
/*  750:     */     
/*  751:     */     public Instances[] splitData(Instances insts, double defAcRt, double cl)
/*  752:     */     {
/*  753:1113 */       Instances data = new Instances(insts);
/*  754:1114 */       data.sort(this.att);
/*  755:1115 */       int total = data.numInstances();
/*  756:     */       
/*  757:     */ 
/*  758:1118 */       int split = 1;
/*  759:1119 */       int prev = 0;
/*  760:1120 */       int finalSplit = split;
/*  761:1121 */       this.maxInfoGain = 0.0D;
/*  762:1122 */       this.value = 0.0D;
/*  763:     */       
/*  764:     */ 
/*  765:1125 */       double minSplit = 0.1D * data.sumOfWeights() / 2.0D;
/*  766:1126 */       if (Utils.smOrEq(minSplit, Ridor.this.m_MinNo)) {
/*  767:1127 */         minSplit = Ridor.this.m_MinNo;
/*  768:1128 */       } else if (Utils.gr(minSplit, 25.0D)) {
/*  769:1129 */         minSplit = 25.0D;
/*  770:     */       }
/*  771:1132 */       double fstCover = 0.0D;double sndCover = 0.0D;double fstAccu = 0.0D;double sndAccu = 0.0D;
/*  772:1134 */       for (int x = 0; x < data.numInstances(); x++)
/*  773:     */       {
/*  774:1135 */         Instance inst = data.instance(x);
/*  775:1136 */         if (inst.isMissing(this.att))
/*  776:     */         {
/*  777:1137 */           total = x;
/*  778:1138 */           break;
/*  779:     */         }
/*  780:1141 */         sndCover += inst.weight();
/*  781:1142 */         if (Utils.eq(inst.classValue(), cl)) {
/*  782:1143 */           sndAccu += inst.weight();
/*  783:     */         }
/*  784:     */       }
/*  785:1148 */       if (Utils.sm(sndCover, 2.0D * minSplit)) {
/*  786:1149 */         return null;
/*  787:     */       }
/*  788:1152 */       if (total == 0) {
/*  789:1153 */         return null;
/*  790:     */       }
/*  791:1155 */       this.splitPoint = data.instance(total - 1).value(this.att);
/*  792:1157 */       for (; split < total; split++) {
/*  793:1158 */         if (!Utils.eq(data.instance(split).value(this.att), data.instance(prev).value(this.att)))
/*  794:     */         {
/*  795:1161 */           for (int y = prev; y < split; y++)
/*  796:     */           {
/*  797:1162 */             Instance inst = data.instance(y);
/*  798:1163 */             fstCover += inst.weight();
/*  799:1164 */             sndCover -= inst.weight();
/*  800:1165 */             if (Utils.eq(data.instance(y).classValue(), cl))
/*  801:     */             {
/*  802:1166 */               fstAccu += inst.weight();
/*  803:1167 */               sndAccu -= inst.weight();
/*  804:     */             }
/*  805:     */           }
/*  806:1171 */           if ((Utils.sm(fstCover, minSplit)) || (Utils.sm(sndCover, minSplit)))
/*  807:     */           {
/*  808:1172 */             prev = split;
/*  809:     */           }
/*  810:     */           else
/*  811:     */           {
/*  812:1176 */             double fstAccuRate = 0.0D;double sndAccuRate = 0.0D;
/*  813:1177 */             if (!Utils.eq(fstCover, 0.0D)) {
/*  814:1178 */               fstAccuRate = fstAccu / fstCover;
/*  815:     */             }
/*  816:1180 */             if (!Utils.eq(sndCover, 0.0D)) {
/*  817:1181 */               sndAccuRate = sndAccu / sndCover;
/*  818:     */             }
/*  819:1189 */             double fstInfoGain = Utils.eq(fstAccuRate, 0.0D) ? 0.0D : fstAccu * (Utils.log2(fstAccuRate) - Utils.log2(defAcRt));
/*  820:     */             
/*  821:1191 */             double sndInfoGain = Utils.eq(sndAccuRate, 0.0D) ? 0.0D : sndAccu * (Utils.log2(sndAccuRate) - Utils.log2(defAcRt));
/*  822:     */             double coverage;
/*  823:     */             boolean isFirst;
/*  824:     */             double infoGain;
/*  825:     */             double accRate;
/*  826:     */             double accurate;
/*  827:     */             double coverage;
/*  828:1193 */             if ((Utils.gr(fstInfoGain, sndInfoGain)) || ((Utils.eq(fstInfoGain, sndInfoGain)) && (Utils.grOrEq(fstAccuRate, sndAccuRate))))
/*  829:     */             {
/*  830:1196 */               boolean isFirst = true;
/*  831:1197 */               double infoGain = fstInfoGain;
/*  832:1198 */               double accRate = fstAccuRate;
/*  833:1199 */               double accurate = fstAccu;
/*  834:1200 */               coverage = fstCover;
/*  835:     */             }
/*  836:     */             else
/*  837:     */             {
/*  838:1202 */               isFirst = false;
/*  839:1203 */               infoGain = sndInfoGain;
/*  840:1204 */               accRate = sndAccuRate;
/*  841:1205 */               accurate = sndAccu;
/*  842:1206 */               coverage = sndCover;
/*  843:     */             }
/*  844:1209 */             boolean isUpdate = Utils.gr(infoGain, this.maxInfoGain);
/*  845:1212 */             if (isUpdate)
/*  846:     */             {
/*  847:1213 */               this.splitPoint = ((data.instance(split).value(this.att) + data.instance(prev).value(this.att)) / 2.0D);
/*  848:     */               
/*  849:1215 */               this.value = (isFirst ? 0 : 1);
/*  850:1216 */               this.accuRate = accRate;
/*  851:1217 */               this.accu = accurate;
/*  852:1218 */               this.cover = coverage;
/*  853:1219 */               this.maxInfoGain = infoGain;
/*  854:1220 */               finalSplit = split;
/*  855:     */             }
/*  856:1222 */             prev = split;
/*  857:     */           }
/*  858:     */         }
/*  859:     */       }
/*  860:1227 */       Instances[] splitData = new Instances[2];
/*  861:1228 */       splitData[0] = new Instances(data, 0, finalSplit);
/*  862:1229 */       splitData[1] = new Instances(data, finalSplit, total - finalSplit);
/*  863:     */       
/*  864:1231 */       return splitData;
/*  865:     */     }
/*  866:     */     
/*  867:     */     public boolean isCover(Instance inst)
/*  868:     */     {
/*  869:1243 */       boolean isCover = false;
/*  870:1244 */       if (!inst.isMissing(this.att)) {
/*  871:1245 */         if (Utils.eq(this.value, 0.0D))
/*  872:     */         {
/*  873:1246 */           if (Utils.smOrEq(inst.value(this.att), this.splitPoint)) {
/*  874:1247 */             isCover = true;
/*  875:     */           }
/*  876:     */         }
/*  877:1249 */         else if (Utils.gr(inst.value(this.att), this.splitPoint)) {
/*  878:1250 */           isCover = true;
/*  879:     */         }
/*  880:     */       }
/*  881:1253 */       return isCover;
/*  882:     */     }
/*  883:     */     
/*  884:     */     public String toString()
/*  885:     */     {
/*  886:1263 */       String symbol = Utils.eq(this.value, 0.0D) ? " <= " : " > ";
/*  887:1264 */       return this.att.name() + symbol + Utils.doubleToString(this.splitPoint, 6);
/*  888:     */     }
/*  889:     */     
/*  890:     */     public String getRevision()
/*  891:     */     {
/*  892:1274 */       return RevisionUtils.extract("$Revision: 10375 $");
/*  893:     */     }
/*  894:     */   }
/*  895:     */   
/*  896:     */   private class NominalAntd
/*  897:     */     extends Ridor.Antd
/*  898:     */   {
/*  899:     */     static final long serialVersionUID = -256386137196078004L;
/*  900:     */     private final double[] accurate;
/*  901:     */     private final double[] coverage;
/*  902:     */     private final double[] infoGain;
/*  903:     */     
/*  904:     */     public NominalAntd(Attribute a)
/*  905:     */     {
/*  906:1293 */       super(a);
/*  907:1294 */       int bag = this.att.numValues();
/*  908:1295 */       this.accurate = new double[bag];
/*  909:1296 */       this.coverage = new double[bag];
/*  910:1297 */       this.infoGain = new double[bag];
/*  911:     */     }
/*  912:     */     
/*  913:     */     public Instances[] splitData(Instances data, double defAcRt, double cl)
/*  914:     */     {
/*  915:1312 */       int bag = this.att.numValues();
/*  916:1313 */       Instances[] splitData = new Instances[bag];
/*  917:1315 */       for (int x = 0; x < bag; x++)
/*  918:     */       {
/*  919:1316 */         double tmp47_46 = (this.infoGain[x] = 0.0D);this.coverage[x] = tmp47_46;this.accurate[x] = tmp47_46;
/*  920:1317 */         splitData[x] = new Instances(data, data.numInstances());
/*  921:     */       }
/*  922:1320 */       for (int x = 0; x < data.numInstances(); x++)
/*  923:     */       {
/*  924:1321 */         Instance inst = data.instance(x);
/*  925:1322 */         if (!inst.isMissing(this.att))
/*  926:     */         {
/*  927:1323 */           int v = (int)inst.value(this.att);
/*  928:1324 */           splitData[v].add(inst);
/*  929:1325 */           this.coverage[v] += inst.weight();
/*  930:1326 */           if (Utils.eq(inst.classValue(), cl)) {
/*  931:1327 */             this.accurate[v] += inst.weight();
/*  932:     */           }
/*  933:     */         }
/*  934:     */       }
/*  935:1333 */       int count = 0;
/*  936:1334 */       for (int x = 0; x < bag; x++)
/*  937:     */       {
/*  938:1335 */         double t = this.coverage[x];
/*  939:1336 */         if (Utils.grOrEq(t, Ridor.this.m_MinNo))
/*  940:     */         {
/*  941:1337 */           double p = this.accurate[x];
/*  942:1339 */           if (!Utils.eq(t, 0.0D)) {
/*  943:1340 */             this.infoGain[x] = (p * (Utils.log2(p / t) - Utils.log2(defAcRt)));
/*  944:     */           }
/*  945:1342 */           count++;
/*  946:     */         }
/*  947:     */       }
/*  948:1346 */       if (count < 2) {
/*  949:1347 */         return null;
/*  950:     */       }
/*  951:1350 */       this.value = Utils.maxIndex(this.infoGain);
/*  952:     */       
/*  953:1352 */       this.cover = this.coverage[((int)this.value)];
/*  954:1353 */       this.accu = this.accurate[((int)this.value)];
/*  955:1355 */       if (!Utils.eq(this.cover, 0.0D)) {
/*  956:1356 */         this.accuRate = (this.accu / this.cover);
/*  957:     */       } else {
/*  958:1358 */         this.accuRate = 0.0D;
/*  959:     */       }
/*  960:1361 */       this.maxInfoGain = this.infoGain[((int)this.value)];
/*  961:     */       
/*  962:1363 */       return splitData;
/*  963:     */     }
/*  964:     */     
/*  965:     */     public boolean isCover(Instance inst)
/*  966:     */     {
/*  967:1375 */       boolean isCover = false;
/*  968:1376 */       if ((!inst.isMissing(this.att)) && 
/*  969:1377 */         (Utils.eq(inst.value(this.att), this.value))) {
/*  970:1378 */         isCover = true;
/*  971:     */       }
/*  972:1381 */       return isCover;
/*  973:     */     }
/*  974:     */     
/*  975:     */     public String toString()
/*  976:     */     {
/*  977:1391 */       return this.att.name() + " = " + this.att.value((int)this.value);
/*  978:     */     }
/*  979:     */     
/*  980:     */     public String getRevision()
/*  981:     */     {
/*  982:1401 */       return RevisionUtils.extract("$Revision: 10375 $");
/*  983:     */     }
/*  984:     */   }
/*  985:     */   
/*  986:     */   public Capabilities getCapabilities()
/*  987:     */   {
/*  988:1412 */     Capabilities result = super.getCapabilities();
/*  989:1413 */     result.disableAll();
/*  990:     */     
/*  991:     */ 
/*  992:1416 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  993:1417 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  994:1418 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  995:1419 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  996:     */     
/*  997:     */ 
/*  998:1422 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  999:1423 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 1000:     */     
/* 1001:1425 */     return result;
/* 1002:     */   }
/* 1003:     */   
/* 1004:     */   public void buildClassifier(Instances instances)
/* 1005:     */     throws Exception
/* 1006:     */   {
/* 1007:1438 */     getCapabilities().testWithFail(instances);
/* 1008:     */     
/* 1009:     */ 
/* 1010:1441 */     Instances data = new Instances(instances);
/* 1011:1442 */     data.deleteWithMissingClass();
/* 1012:     */     
/* 1013:1444 */     int numCl = data.numClasses();
/* 1014:1445 */     this.m_Root = new Ridor_node(null);
/* 1015:1446 */     this.m_Class = instances.classAttribute();
/* 1016:     */     
/* 1017:1448 */     int index = data.classIndex();
/* 1018:1449 */     this.m_Cover = data.sumOfWeights();
/* 1019:     */     
/* 1020:1451 */     this.m_Random = new Random(this.m_Seed);
/* 1021:     */     
/* 1022:     */ 
/* 1023:1454 */     ArrayList<String> binary_values = new ArrayList(2);
/* 1024:1455 */     binary_values.add("otherClasses");
/* 1025:1456 */     binary_values.add("defClass");
/* 1026:1457 */     Attribute attr = new Attribute("newClass", binary_values);
/* 1027:1458 */     data.insertAttributeAt(attr, index);
/* 1028:1459 */     data.setClassIndex(index);
/* 1029:     */     
/* 1030:     */ 
/* 1031:1462 */     Instances[] dataByClass = new Instances[numCl];
/* 1032:1463 */     for (int i = 0; i < numCl; i++) {
/* 1033:1464 */       dataByClass[i] = new Instances(data, data.numInstances());
/* 1034:     */     }
/* 1035:1466 */     for (int i = 0; i < data.numInstances(); i++)
/* 1036:     */     {
/* 1037:1467 */       Instance inst = data.instance(i);
/* 1038:1468 */       inst.setClassValue(0.0D);
/* 1039:1469 */       dataByClass[((int)inst.value(index + 1))].add(inst);
/* 1040:     */     }
/* 1041:1472 */     for (int i = 0; i < numCl; i++) {
/* 1042:1473 */       dataByClass[i].deleteAttributeAt(index + 1);
/* 1043:     */     }
/* 1044:1476 */     this.m_Root.findRules(dataByClass, 0);
/* 1045:     */   }
/* 1046:     */   
/* 1047:     */   public double classifyInstance(Instance datum)
/* 1048:     */   {
/* 1049:1488 */     return classify(this.m_Root, datum);
/* 1050:     */   }
/* 1051:     */   
/* 1052:     */   private double classify(Ridor_node node, Instance datum)
/* 1053:     */   {
/* 1054:1499 */     double classValue = node.getDefClass();
/* 1055:1500 */     RidorRule[] rules = node.getRules();
/* 1056:1502 */     if (rules != null)
/* 1057:     */     {
/* 1058:1503 */       Ridor_node[] excepts = node.getExcepts();
/* 1059:1504 */       for (int i = 0; i < excepts.length; i++) {
/* 1060:1505 */         if (rules[i].isCover(datum))
/* 1061:     */         {
/* 1062:1506 */           classValue = classify(excepts[i], datum);
/* 1063:1507 */           break;
/* 1064:     */         }
/* 1065:     */       }
/* 1066:     */     }
/* 1067:1512 */     return classValue;
/* 1068:     */   }
/* 1069:     */   
/* 1070:     */   public Enumeration<Option> listOptions()
/* 1071:     */   {
/* 1072:1549 */     Vector<Option> newVector = new Vector(5);
/* 1073:     */     
/* 1074:1551 */     newVector.addElement(new Option("\tSet number of folds for IREP\n\tOne fold is used as pruning set.\n\t(default 3)", "F", 1, "-F <number of folds>"));
/* 1075:     */     
/* 1076:     */ 
/* 1077:1554 */     newVector.addElement(new Option("\tSet number of shuffles to randomize\n\tthe data in order to get better rule.\n\t(default 1)", "S", 1, "-S <number of shuffles>"));
/* 1078:     */     
/* 1079:     */ 
/* 1080:1557 */     newVector.addElement(new Option("\tSet flag of whether use the error rate \n\tof all the data to select the default class\n\tin each step. If not set, the learner will only use\tthe error rate in the pruning data", "A", 0, "-A"));
/* 1081:     */     
/* 1082:     */ 
/* 1083:     */ 
/* 1084:     */ 
/* 1085:1562 */     newVector.addElement(new Option("\t Set flag of whether use the majority class as\n\tthe default class in each step instead of \n\tchoosing default class based on the error rate\n\t(if the flag is not set)", "M", 0, "-M"));
/* 1086:     */     
/* 1087:     */ 
/* 1088:     */ 
/* 1089:     */ 
/* 1090:1567 */     newVector.addElement(new Option("\tSet the minimal weights of instances\n\twithin a split.\n\t(default 2.0)", "N", 1, "-N <min. weights>"));
/* 1091:     */     
/* 1092:     */ 
/* 1093:     */ 
/* 1094:     */ 
/* 1095:1572 */     newVector.addAll(Collections.list(super.listOptions()));
/* 1096:     */     
/* 1097:1574 */     return newVector.elements();
/* 1098:     */   }
/* 1099:     */   
/* 1100:     */   public void setOptions(String[] options)
/* 1101:     */     throws Exception
/* 1102:     */   {
/* 1103:1628 */     String numFoldsString = Utils.getOption('F', options);
/* 1104:1629 */     if (numFoldsString.length() != 0) {
/* 1105:1630 */       this.m_Folds = Integer.parseInt(numFoldsString);
/* 1106:     */     } else {
/* 1107:1632 */       this.m_Folds = 3;
/* 1108:     */     }
/* 1109:1635 */     String numShuffleString = Utils.getOption('S', options);
/* 1110:1636 */     if (numShuffleString.length() != 0) {
/* 1111:1637 */       this.m_Shuffle = Integer.parseInt(numShuffleString);
/* 1112:     */     } else {
/* 1113:1639 */       this.m_Shuffle = 1;
/* 1114:     */     }
/* 1115:1642 */     String seedString = Utils.getOption('s', options);
/* 1116:1643 */     if (seedString.length() != 0) {
/* 1117:1644 */       this.m_Seed = Integer.parseInt(seedString);
/* 1118:     */     } else {
/* 1119:1646 */       this.m_Seed = 1;
/* 1120:     */     }
/* 1121:1649 */     String minNoString = Utils.getOption('N', options);
/* 1122:1650 */     if (minNoString.length() != 0) {
/* 1123:1651 */       this.m_MinNo = Double.parseDouble(minNoString);
/* 1124:     */     } else {
/* 1125:1653 */       this.m_MinNo = 2.0D;
/* 1126:     */     }
/* 1127:1656 */     this.m_IsAllErr = Utils.getFlag('A', options);
/* 1128:1657 */     this.m_IsMajority = Utils.getFlag('M', options);
/* 1129:     */     
/* 1130:1659 */     super.setOptions(options);
/* 1131:     */     
/* 1132:1661 */     Utils.checkForRemainingOptions(options);
/* 1133:     */   }
/* 1134:     */   
/* 1135:     */   public String[] getOptions()
/* 1136:     */   {
/* 1137:1672 */     String[] options = new String[8];
/* 1138:1673 */     int current = 0;
/* 1139:1674 */     options[(current++)] = "-F";
/* 1140:1675 */     options[(current++)] = ("" + this.m_Folds);
/* 1141:1676 */     options[(current++)] = "-S";
/* 1142:1677 */     options[(current++)] = ("" + this.m_Shuffle);
/* 1143:1678 */     options[(current++)] = "-N";
/* 1144:1679 */     options[(current++)] = ("" + this.m_MinNo);
/* 1145:1681 */     if (this.m_IsAllErr) {
/* 1146:1682 */       options[(current++)] = "-A";
/* 1147:     */     }
/* 1148:1684 */     if (this.m_IsMajority) {
/* 1149:1685 */       options[(current++)] = "-M";
/* 1150:     */     }
/* 1151:1687 */     while (current < options.length) {
/* 1152:1688 */       options[(current++)] = "";
/* 1153:     */     }
/* 1154:1690 */     return options;
/* 1155:     */   }
/* 1156:     */   
/* 1157:     */   public String foldsTipText()
/* 1158:     */   {
/* 1159:1702 */     return "Determines the amount of data used for pruning. One fold is used for pruning, the rest for growing the rules.";
/* 1160:     */   }
/* 1161:     */   
/* 1162:     */   public void setFolds(int fold)
/* 1163:     */   {
/* 1164:1707 */     this.m_Folds = fold;
/* 1165:     */   }
/* 1166:     */   
/* 1167:     */   public int getFolds()
/* 1168:     */   {
/* 1169:1711 */     return this.m_Folds;
/* 1170:     */   }
/* 1171:     */   
/* 1172:     */   public String shuffleTipText()
/* 1173:     */   {
/* 1174:1721 */     return "Determines how often the data is shuffled before a rule is chosen. If > 1, a rule is learned multiple times and the most accurate rule is chosen.";
/* 1175:     */   }
/* 1176:     */   
/* 1177:     */   public void setShuffle(int sh)
/* 1178:     */   {
/* 1179:1727 */     this.m_Shuffle = sh;
/* 1180:     */   }
/* 1181:     */   
/* 1182:     */   public int getShuffle()
/* 1183:     */   {
/* 1184:1731 */     return this.m_Shuffle;
/* 1185:     */   }
/* 1186:     */   
/* 1187:     */   public String seedTipText()
/* 1188:     */   {
/* 1189:1741 */     return "The seed used for randomizing the data.";
/* 1190:     */   }
/* 1191:     */   
/* 1192:     */   public void setSeed(int s)
/* 1193:     */   {
/* 1194:1745 */     this.m_Seed = s;
/* 1195:     */   }
/* 1196:     */   
/* 1197:     */   public int getSeed()
/* 1198:     */   {
/* 1199:1749 */     return this.m_Seed;
/* 1200:     */   }
/* 1201:     */   
/* 1202:     */   public String wholeDataErrTipText()
/* 1203:     */   {
/* 1204:1759 */     return "Whether worth of rule is computed based on all the data or just based on data covered by rule.";
/* 1205:     */   }
/* 1206:     */   
/* 1207:     */   public void setWholeDataErr(boolean a)
/* 1208:     */   {
/* 1209:1764 */     this.m_IsAllErr = a;
/* 1210:     */   }
/* 1211:     */   
/* 1212:     */   public boolean getWholeDataErr()
/* 1213:     */   {
/* 1214:1768 */     return this.m_IsAllErr;
/* 1215:     */   }
/* 1216:     */   
/* 1217:     */   public String majorityClassTipText()
/* 1218:     */   {
/* 1219:1778 */     return "Whether the majority class is used as default.";
/* 1220:     */   }
/* 1221:     */   
/* 1222:     */   public void setMajorityClass(boolean m)
/* 1223:     */   {
/* 1224:1782 */     this.m_IsMajority = m;
/* 1225:     */   }
/* 1226:     */   
/* 1227:     */   public boolean getMajorityClass()
/* 1228:     */   {
/* 1229:1786 */     return this.m_IsMajority;
/* 1230:     */   }
/* 1231:     */   
/* 1232:     */   public String minNoTipText()
/* 1233:     */   {
/* 1234:1796 */     return "The minimum total weight of the instances in a rule.";
/* 1235:     */   }
/* 1236:     */   
/* 1237:     */   public void setMinNo(double m)
/* 1238:     */   {
/* 1239:1800 */     this.m_MinNo = m;
/* 1240:     */   }
/* 1241:     */   
/* 1242:     */   public double getMinNo()
/* 1243:     */   {
/* 1244:1804 */     return this.m_MinNo;
/* 1245:     */   }
/* 1246:     */   
/* 1247:     */   public Enumeration<String> enumerateMeasures()
/* 1248:     */   {
/* 1249:1814 */     Vector<String> newVector = new Vector(1);
/* 1250:1815 */     newVector.addElement("measureNumRules");
/* 1251:1816 */     return newVector.elements();
/* 1252:     */   }
/* 1253:     */   
/* 1254:     */   public double getMeasure(String additionalMeasureName)
/* 1255:     */   {
/* 1256:1828 */     if (additionalMeasureName.compareToIgnoreCase("measureNumRules") == 0) {
/* 1257:1829 */       return numRules();
/* 1258:     */     }
/* 1259:1831 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (Ripple down rule learner)");
/* 1260:     */   }
/* 1261:     */   
/* 1262:     */   private double numRules()
/* 1263:     */   {
/* 1264:1842 */     int size = 0;
/* 1265:1843 */     if (this.m_Root != null) {
/* 1266:1844 */       size = this.m_Root.size();
/* 1267:     */     }
/* 1268:1847 */     return size + 1;
/* 1269:     */   }
/* 1270:     */   
/* 1271:     */   public String toString()
/* 1272:     */   {
/* 1273:1857 */     if (this.m_Root == null) {
/* 1274:1858 */       return "RIpple DOwn Rule Learner(Ridor): No model built yet.";
/* 1275:     */     }
/* 1276:1861 */     return "RIpple DOwn Rule Learner(Ridor) rules\n--------------------------------------\n\n" + this.m_Root.toString() + "\nTotal number of rules (incl. the default rule): " + (int)numRules();
/* 1277:     */   }
/* 1278:     */   
/* 1279:     */   public String getRevision()
/* 1280:     */   {
/* 1281:1873 */     return RevisionUtils.extract("$Revision: 10375 $");
/* 1282:     */   }
/* 1283:     */   
/* 1284:     */   public static void main(String[] args)
/* 1285:     */   {
/* 1286:1882 */     runClassifier(new Ridor(), args);
/* 1287:     */   }
/* 1288:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.rules.Ridor
 * JD-Core Version:    0.7.0.1
 */