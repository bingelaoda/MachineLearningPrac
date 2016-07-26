/*    1:     */ package weka.classifiers.meta;
/*    2:     */ 
/*    3:     */ import java.io.Serializable;
/*    4:     */ import java.util.ArrayList;
/*    5:     */ import java.util.Arrays;
/*    6:     */ import java.util.Collections;
/*    7:     */ import java.util.Enumeration;
/*    8:     */ import java.util.Random;
/*    9:     */ import java.util.Vector;
/*   10:     */ import weka.classifiers.AbstractClassifier;
/*   11:     */ import weka.classifiers.Classifier;
/*   12:     */ import weka.classifiers.RandomizableSingleClassifierEnhancer;
/*   13:     */ import weka.classifiers.functions.Logistic;
/*   14:     */ import weka.classifiers.rules.ZeroR;
/*   15:     */ import weka.core.Attribute;
/*   16:     */ import weka.core.Capabilities;
/*   17:     */ import weka.core.Capabilities.Capability;
/*   18:     */ import weka.core.Instance;
/*   19:     */ import weka.core.Instances;
/*   20:     */ import weka.core.Option;
/*   21:     */ import weka.core.OptionHandler;
/*   22:     */ import weka.core.Range;
/*   23:     */ import weka.core.RevisionHandler;
/*   24:     */ import weka.core.RevisionUtils;
/*   25:     */ import weka.core.SelectedTag;
/*   26:     */ import weka.core.Tag;
/*   27:     */ import weka.core.Utils;
/*   28:     */ import weka.filters.Filter;
/*   29:     */ import weka.filters.unsupervised.attribute.MakeIndicator;
/*   30:     */ import weka.filters.unsupervised.instance.RemoveWithValues;
/*   31:     */ 
/*   32:     */ public class MultiClassClassifier
/*   33:     */   extends RandomizableSingleClassifierEnhancer
/*   34:     */   implements OptionHandler
/*   35:     */ {
/*   36:     */   static final long serialVersionUID = -3879602011542849141L;
/*   37:     */   protected Classifier[] m_Classifiers;
/*   38: 114 */   protected boolean m_pairwiseCoupling = false;
/*   39:     */   protected double[] m_SumOfWeights;
/*   40:     */   protected Filter[] m_ClassFilters;
/*   41:     */   private ZeroR m_ZeroR;
/*   42:     */   protected Attribute m_ClassAttribute;
/*   43:     */   protected Instances m_TwoClassDataset;
/*   44: 135 */   private double m_RandomWidthFactor = 2.0D;
/*   45: 138 */   protected boolean m_logLossDecoding = false;
/*   46: 141 */   protected int m_Method = 0;
/*   47:     */   public static final int METHOD_1_AGAINST_ALL = 0;
/*   48:     */   public static final int METHOD_ERROR_RANDOM = 1;
/*   49:     */   public static final int METHOD_ERROR_EXHAUSTIVE = 2;
/*   50:     */   public static final int METHOD_1_AGAINST_1 = 3;
/*   51: 152 */   public static final Tag[] TAGS_METHOD = { new Tag(0, "1-against-all"), new Tag(1, "Random correction code"), new Tag(2, "Exhaustive correction code"), new Tag(3, "1-against-1") };
/*   52:     */   
/*   53:     */   public MultiClassClassifier()
/*   54:     */   {
/*   55: 164 */     this.m_Classifier = new Logistic();
/*   56:     */   }
/*   57:     */   
/*   58:     */   protected String defaultClassifierString()
/*   59:     */   {
/*   60: 174 */     return "weka.classifiers.functions.Logistic";
/*   61:     */   }
/*   62:     */   
/*   63:     */   private abstract class Code
/*   64:     */     implements Serializable, RevisionHandler
/*   65:     */   {
/*   66:     */     static final long serialVersionUID = 418095077487120846L;
/*   67:     */     protected boolean[][] m_Codebits;
/*   68:     */     
/*   69:     */     private Code() {}
/*   70:     */     
/*   71:     */     public int size()
/*   72:     */     {
/*   73: 198 */       return this.m_Codebits.length;
/*   74:     */     }
/*   75:     */     
/*   76:     */     public String getIndices(int which)
/*   77:     */     {
/*   78: 209 */       StringBuffer sb = new StringBuffer();
/*   79: 210 */       for (int i = 0; i < this.m_Codebits[which].length; i++) {
/*   80: 211 */         if (this.m_Codebits[which][i] != 0)
/*   81:     */         {
/*   82: 212 */           if (sb.length() != 0) {
/*   83: 213 */             sb.append(',');
/*   84:     */           }
/*   85: 215 */           sb.append(i + 1);
/*   86:     */         }
/*   87:     */       }
/*   88: 218 */       return sb.toString();
/*   89:     */     }
/*   90:     */     
/*   91:     */     public String toString()
/*   92:     */     {
/*   93: 226 */       StringBuffer sb = new StringBuffer();
/*   94: 227 */       for (int i = 0; i < this.m_Codebits[0].length; i++)
/*   95:     */       {
/*   96: 228 */         for (int j = 0; j < this.m_Codebits.length; j++) {
/*   97: 229 */           sb.append(this.m_Codebits[j][i] != 0 ? " 1" : " 0");
/*   98:     */         }
/*   99: 231 */         sb.append('\n');
/*  100:     */       }
/*  101: 233 */       return sb.toString();
/*  102:     */     }
/*  103:     */     
/*  104:     */     public String getRevision()
/*  105:     */     {
/*  106: 242 */       return RevisionUtils.extract("$Revision: 11889 $");
/*  107:     */     }
/*  108:     */   }
/*  109:     */   
/*  110:     */   private class StandardCode
/*  111:     */     extends MultiClassClassifier.Code
/*  112:     */   {
/*  113:     */     static final long serialVersionUID = 3707829689461467358L;
/*  114:     */     
/*  115:     */     public StandardCode(int numClasses)
/*  116:     */     {
/*  117: 260 */       super(null);
/*  118: 261 */       this.m_Codebits = new boolean[numClasses][numClasses];
/*  119: 262 */       for (int i = 0; i < numClasses; i++) {
/*  120: 263 */         this.m_Codebits[i][i] = 1;
/*  121:     */       }
/*  122:     */     }
/*  123:     */     
/*  124:     */     public String getRevision()
/*  125:     */     {
/*  126: 274 */       return RevisionUtils.extract("$Revision: 11889 $");
/*  127:     */     }
/*  128:     */   }
/*  129:     */   
/*  130:     */   private class RandomCode
/*  131:     */     extends MultiClassClassifier.Code
/*  132:     */   {
/*  133:     */     static final long serialVersionUID = 4413410540703926563L;
/*  134: 288 */     Random r = null;
/*  135:     */     
/*  136:     */     public RandomCode(int numClasses, int numCodes, Instances data)
/*  137:     */     {
/*  138: 297 */       super(null);
/*  139: 298 */       this.r = data.getRandomNumberGenerator(MultiClassClassifier.this.m_Seed);
/*  140: 299 */       numCodes = Math.max(2, numCodes);
/*  141: 300 */       this.m_Codebits = new boolean[numCodes][numClasses];
/*  142: 301 */       int i = 0;
/*  143:     */       do
/*  144:     */       {
/*  145: 303 */         randomize();
/*  146: 305 */       } while ((!good()) && (i++ < 100));
/*  147:     */     }
/*  148:     */     
/*  149:     */     private boolean good()
/*  150:     */     {
/*  151: 310 */       boolean[] ninClass = new boolean[this.m_Codebits[0].length];
/*  152: 311 */       boolean[] ainClass = new boolean[this.m_Codebits[0].length];
/*  153: 312 */       for (int i = 0; i < ainClass.length; i++) {
/*  154: 313 */         ainClass[i] = true;
/*  155:     */       }
/*  156: 316 */       for (int i = 0; i < this.m_Codebits.length; i++)
/*  157:     */       {
/*  158: 317 */         boolean ninCode = false;
/*  159: 318 */         boolean ainCode = true;
/*  160: 319 */         for (int j = 0; j < this.m_Codebits[i].length; j++)
/*  161:     */         {
/*  162: 320 */           boolean current = this.m_Codebits[i][j];
/*  163: 321 */           ninCode = (ninCode) || (current);
/*  164: 322 */           ainCode = (ainCode) && (current);
/*  165: 323 */           ninClass[j] = ((ninClass[j] != 0) || (current) ? 1 : false);
/*  166: 324 */           ainClass[j] = ((ainClass[j] != 0) && (current) ? 1 : false);
/*  167:     */         }
/*  168: 326 */         if ((!ninCode) || (ainCode)) {
/*  169: 327 */           return false;
/*  170:     */         }
/*  171:     */       }
/*  172: 330 */       for (int j = 0; j < ninClass.length; j++) {
/*  173: 331 */         if ((ninClass[j] == 0) || (ainClass[j] != 0)) {
/*  174: 332 */           return false;
/*  175:     */         }
/*  176:     */       }
/*  177: 335 */       return true;
/*  178:     */     }
/*  179:     */     
/*  180:     */     private void randomize()
/*  181:     */     {
/*  182: 342 */       for (int i = 0; i < this.m_Codebits.length; i++) {
/*  183: 343 */         for (int j = 0; j < this.m_Codebits[i].length; j++)
/*  184:     */         {
/*  185: 344 */           double temp = this.r.nextDouble();
/*  186: 345 */           this.m_Codebits[i][j] = (temp < 0.5D ? 0 : 1);
/*  187:     */         }
/*  188:     */       }
/*  189:     */     }
/*  190:     */     
/*  191:     */     public String getRevision()
/*  192:     */     {
/*  193: 356 */       return RevisionUtils.extract("$Revision: 11889 $");
/*  194:     */     }
/*  195:     */   }
/*  196:     */   
/*  197:     */   private class ExhaustiveCode
/*  198:     */     extends MultiClassClassifier.Code
/*  199:     */   {
/*  200:     */     static final long serialVersionUID = 8090991039670804047L;
/*  201:     */     
/*  202:     */     public ExhaustiveCode(int numClasses)
/*  203:     */     {
/*  204: 380 */       super(null);
/*  205: 381 */       int width = (int)Math.pow(2.0D, numClasses - 1) - 1;
/*  206: 382 */       this.m_Codebits = new boolean[width][numClasses];
/*  207: 383 */       for (int j = 0; j < width; j++) {
/*  208: 384 */         this.m_Codebits[j][0] = 1;
/*  209:     */       }
/*  210: 386 */       for (int i = 1; i < numClasses; i++)
/*  211:     */       {
/*  212: 387 */         int skip = (int)Math.pow(2.0D, numClasses - (i + 1));
/*  213: 388 */         for (int j = 0; j < width; j++) {
/*  214: 389 */           this.m_Codebits[j][i] = (j / skip % 2 != 0 ? 1 : 0);
/*  215:     */         }
/*  216:     */       }
/*  217:     */     }
/*  218:     */     
/*  219:     */     public String getRevision()
/*  220:     */     {
/*  221: 401 */       return RevisionUtils.extract("$Revision: 11889 $");
/*  222:     */     }
/*  223:     */   }
/*  224:     */   
/*  225:     */   public Capabilities getCapabilities()
/*  226:     */   {
/*  227: 411 */     Capabilities result = super.getCapabilities();
/*  228:     */     
/*  229:     */ 
/*  230: 414 */     result.disableAllClasses();
/*  231: 415 */     result.disableAllClassDependencies();
/*  232: 416 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  233:     */     
/*  234: 418 */     return result;
/*  235:     */   }
/*  236:     */   
/*  237:     */   public void buildClassifier(Instances insts)
/*  238:     */     throws Exception
/*  239:     */   {
/*  240: 432 */     getCapabilities().testWithFail(insts);
/*  241:     */     
/*  242:     */ 
/*  243: 435 */     boolean zeroTrainingInstances = insts.numInstances() == 0;
/*  244:     */     
/*  245:     */ 
/*  246: 438 */     insts = new Instances(insts);
/*  247: 439 */     insts.deleteWithMissingClass();
/*  248: 441 */     if (this.m_Classifier == null) {
/*  249: 442 */       throw new Exception("No base classifier has been set!");
/*  250:     */     }
/*  251: 444 */     this.m_ZeroR = new ZeroR();
/*  252: 445 */     this.m_ZeroR.buildClassifier(insts);
/*  253:     */     
/*  254: 447 */     this.m_TwoClassDataset = null;
/*  255:     */     
/*  256: 449 */     int numClassifiers = insts.numClasses();
/*  257: 450 */     if (numClassifiers <= 2)
/*  258:     */     {
/*  259: 452 */       this.m_Classifiers = AbstractClassifier.makeCopies(this.m_Classifier, 1);
/*  260: 453 */       this.m_Classifiers[0].buildClassifier(insts);
/*  261:     */       
/*  262: 455 */       this.m_ClassFilters = null;
/*  263:     */     }
/*  264: 457 */     else if (this.m_Method == 3)
/*  265:     */     {
/*  266: 459 */       ArrayList<int[]> pairs = new ArrayList();
/*  267: 460 */       for (int i = 0; i < insts.numClasses(); i++) {
/*  268: 461 */         for (int j = 0; j < insts.numClasses(); j++) {
/*  269: 462 */           if (j > i)
/*  270:     */           {
/*  271: 463 */             int[] pair = new int[2];
/*  272: 464 */             pair[0] = i;pair[1] = j;
/*  273: 465 */             pairs.add(pair);
/*  274:     */           }
/*  275:     */         }
/*  276:     */       }
/*  277: 469 */       numClassifiers = pairs.size();
/*  278: 470 */       this.m_Classifiers = AbstractClassifier.makeCopies(this.m_Classifier, numClassifiers);
/*  279: 471 */       this.m_ClassFilters = new Filter[numClassifiers];
/*  280: 472 */       this.m_SumOfWeights = new double[numClassifiers];
/*  281: 475 */       for (int i = 0; i < numClassifiers; i++)
/*  282:     */       {
/*  283: 476 */         RemoveWithValues classFilter = new RemoveWithValues();
/*  284: 477 */         classFilter.setAttributeIndex("" + (insts.classIndex() + 1));
/*  285: 478 */         classFilter.setModifyHeader(true);
/*  286: 479 */         classFilter.setInvertSelection(true);
/*  287: 480 */         classFilter.setNominalIndicesArr((int[])pairs.get(i));
/*  288: 481 */         Instances tempInstances = new Instances(insts, 0);
/*  289: 482 */         tempInstances.setClassIndex(-1);
/*  290: 483 */         classFilter.setInputFormat(tempInstances);
/*  291: 484 */         Instances newInsts = Filter.useFilter(insts, classFilter);
/*  292: 485 */         if ((newInsts.numInstances() > 0) || (zeroTrainingInstances))
/*  293:     */         {
/*  294: 486 */           newInsts.setClassIndex(insts.classIndex());
/*  295: 487 */           this.m_Classifiers[i].buildClassifier(newInsts);
/*  296: 488 */           this.m_ClassFilters[i] = classFilter;
/*  297: 489 */           this.m_SumOfWeights[i] = newInsts.sumOfWeights();
/*  298:     */         }
/*  299:     */         else
/*  300:     */         {
/*  301: 491 */           this.m_Classifiers[i] = null;
/*  302: 492 */           this.m_ClassFilters[i] = null;
/*  303:     */         }
/*  304:     */       }
/*  305: 497 */       this.m_TwoClassDataset = new Instances(insts, 0);
/*  306: 498 */       int classIndex = this.m_TwoClassDataset.classIndex();
/*  307: 499 */       this.m_TwoClassDataset.setClassIndex(-1);
/*  308: 500 */       ArrayList<String> classLabels = new ArrayList();
/*  309: 501 */       classLabels.add("class0");
/*  310: 502 */       classLabels.add("class1");
/*  311: 503 */       this.m_TwoClassDataset.replaceAttributeAt(new Attribute("class", classLabels), classIndex);
/*  312:     */       
/*  313: 505 */       this.m_TwoClassDataset.setClassIndex(classIndex);
/*  314:     */     }
/*  315:     */     else
/*  316:     */     {
/*  317: 508 */       Code code = null;
/*  318: 509 */       switch (this.m_Method)
/*  319:     */       {
/*  320:     */       case 2: 
/*  321: 511 */         code = new ExhaustiveCode(numClassifiers);
/*  322: 512 */         break;
/*  323:     */       case 1: 
/*  324: 514 */         code = new RandomCode(numClassifiers, (int)(numClassifiers * this.m_RandomWidthFactor), insts);
/*  325:     */         
/*  326:     */ 
/*  327: 517 */         break;
/*  328:     */       case 0: 
/*  329: 519 */         code = new StandardCode(numClassifiers);
/*  330: 520 */         break;
/*  331:     */       default: 
/*  332: 522 */         throw new Exception("Unrecognized correction code type");
/*  333:     */       }
/*  334: 524 */       numClassifiers = code.size();
/*  335: 525 */       this.m_Classifiers = AbstractClassifier.makeCopies(this.m_Classifier, numClassifiers);
/*  336: 526 */       this.m_ClassFilters = new MakeIndicator[numClassifiers];
/*  337: 527 */       for (int i = 0; i < this.m_Classifiers.length; i++)
/*  338:     */       {
/*  339: 528 */         this.m_ClassFilters[i] = new MakeIndicator();
/*  340: 529 */         MakeIndicator classFilter = (MakeIndicator)this.m_ClassFilters[i];
/*  341: 530 */         classFilter.setAttributeIndex("" + (insts.classIndex() + 1));
/*  342: 531 */         classFilter.setValueIndices(code.getIndices(i));
/*  343: 532 */         classFilter.setNumeric(false);
/*  344: 533 */         classFilter.setInputFormat(insts);
/*  345: 534 */         Instances newInsts = Filter.useFilter(insts, this.m_ClassFilters[i]);
/*  346: 535 */         this.m_Classifiers[i].buildClassifier(newInsts);
/*  347:     */       }
/*  348:     */     }
/*  349: 538 */     this.m_ClassAttribute = insts.classAttribute();
/*  350:     */   }
/*  351:     */   
/*  352:     */   public double[] individualPredictions(Instance inst)
/*  353:     */     throws Exception
/*  354:     */   {
/*  355: 553 */     double[] result = null;
/*  356: 555 */     if (this.m_Classifiers.length == 1)
/*  357:     */     {
/*  358: 556 */       result = new double[1];
/*  359: 557 */       result[0] = this.m_Classifiers[0].distributionForInstance(inst)[1];
/*  360:     */     }
/*  361:     */     else
/*  362:     */     {
/*  363: 559 */       result = new double[this.m_ClassFilters.length];
/*  364: 560 */       for (int i = 0; i < this.m_ClassFilters.length; i++) {
/*  365: 561 */         if (this.m_Classifiers[i] != null) {
/*  366: 562 */           if (this.m_Method == 3)
/*  367:     */           {
/*  368: 563 */             Instance tempInst = (Instance)inst.copy();
/*  369: 564 */             tempInst.setDataset(this.m_TwoClassDataset);
/*  370: 565 */             result[i] = this.m_Classifiers[i].distributionForInstance(tempInst)[1];
/*  371:     */           }
/*  372:     */           else
/*  373:     */           {
/*  374: 567 */             this.m_ClassFilters[i].input(inst);
/*  375: 568 */             this.m_ClassFilters[i].batchFinished();
/*  376: 569 */             result[i] = this.m_Classifiers[i].distributionForInstance(this.m_ClassFilters[i].output())[1];
/*  377:     */           }
/*  378:     */         }
/*  379:     */       }
/*  380:     */     }
/*  381: 575 */     return result;
/*  382:     */   }
/*  383:     */   
/*  384:     */   public double[] distributionForInstance(Instance inst)
/*  385:     */     throws Exception
/*  386:     */   {
/*  387: 587 */     if (this.m_Classifiers.length == 1) {
/*  388: 588 */       return this.m_Classifiers[0].distributionForInstance(inst);
/*  389:     */     }
/*  390: 591 */     double[] probs = new double[inst.numClasses()];
/*  391: 593 */     if (this.m_Method == 3)
/*  392:     */     {
/*  393: 594 */       double[][] r = new double[inst.numClasses()][inst.numClasses()];
/*  394: 595 */       double[][] n = new double[inst.numClasses()][inst.numClasses()];
/*  395: 597 */       for (int i = 0; i < this.m_ClassFilters.length; i++) {
/*  396: 598 */         if (this.m_Classifiers[i] != null)
/*  397:     */         {
/*  398: 599 */           Instance tempInst = (Instance)inst.copy();
/*  399: 600 */           tempInst.setDataset(this.m_TwoClassDataset);
/*  400: 601 */           double[] current = this.m_Classifiers[i].distributionForInstance(tempInst);
/*  401: 602 */           Range range = new Range(((RemoveWithValues)this.m_ClassFilters[i]).getNominalIndices());
/*  402: 603 */           range.setUpper(this.m_ClassAttribute.numValues());
/*  403: 604 */           int[] pair = range.getSelection();
/*  404: 605 */           if ((this.m_pairwiseCoupling) && (inst.numClasses() > 2))
/*  405:     */           {
/*  406: 606 */             r[pair[0]][pair[1]] = current[0];
/*  407: 607 */             n[pair[0]][pair[1]] = this.m_SumOfWeights[i];
/*  408:     */           }
/*  409: 609 */           else if (current[0] > current[1])
/*  410:     */           {
/*  411: 610 */             probs[pair[0]] += 1.0D;
/*  412:     */           }
/*  413:     */           else
/*  414:     */           {
/*  415: 612 */             probs[pair[1]] += 1.0D;
/*  416:     */           }
/*  417:     */         }
/*  418:     */       }
/*  419: 617 */       if ((this.m_pairwiseCoupling) && (inst.numClasses() > 2)) {
/*  420: 618 */         return pairwiseCoupling(n, r);
/*  421:     */       }
/*  422:     */     }
/*  423: 620 */     else if (this.m_Method == 0)
/*  424:     */     {
/*  425: 621 */       for (int i = 0; i < this.m_ClassFilters.length; i++)
/*  426:     */       {
/*  427: 622 */         this.m_ClassFilters[i].input(inst);
/*  428: 623 */         this.m_ClassFilters[i].batchFinished();
/*  429: 624 */         probs[i] = this.m_Classifiers[i].distributionForInstance(this.m_ClassFilters[i].output())[1];
/*  430:     */       }
/*  431:     */     }
/*  432: 627 */     else if (getLogLossDecoding())
/*  433:     */     {
/*  434: 628 */       Arrays.fill(probs, 1.0D);
/*  435: 629 */       for (int i = 0; i < this.m_ClassFilters.length; i++)
/*  436:     */       {
/*  437: 630 */         this.m_ClassFilters[i].input(inst);
/*  438: 631 */         this.m_ClassFilters[i].batchFinished();
/*  439: 632 */         double[] current = this.m_Classifiers[i].distributionForInstance(this.m_ClassFilters[i].output());
/*  440: 633 */         for (int j = 0; j < this.m_ClassAttribute.numValues(); j++) {
/*  441: 634 */           if (((MakeIndicator)this.m_ClassFilters[i]).getValueRange().isInRange(j)) {
/*  442: 635 */             probs[j] += Math.log(Utils.SMALL + (1.0D - 2.0D * Utils.SMALL) * current[1]);
/*  443:     */           } else {
/*  444: 637 */             probs[j] += Math.log(Utils.SMALL + (1.0D - 2.0D * Utils.SMALL) * current[0]);
/*  445:     */           }
/*  446:     */         }
/*  447:     */       }
/*  448: 641 */       probs = Utils.logs2probs(probs);
/*  449:     */     }
/*  450:     */     else
/*  451:     */     {
/*  452: 645 */       for (int i = 0; i < this.m_ClassFilters.length; i++)
/*  453:     */       {
/*  454: 646 */         this.m_ClassFilters[i].input(inst);
/*  455: 647 */         this.m_ClassFilters[i].batchFinished();
/*  456: 648 */         double[] current = this.m_Classifiers[i].distributionForInstance(this.m_ClassFilters[i].output());
/*  457: 649 */         for (int j = 0; j < this.m_ClassAttribute.numValues(); j++) {
/*  458: 650 */           if (((MakeIndicator)this.m_ClassFilters[i]).getValueRange().isInRange(j)) {
/*  459: 651 */             probs[j] += current[1];
/*  460:     */           } else {
/*  461: 653 */             probs[j] += current[0];
/*  462:     */           }
/*  463:     */         }
/*  464:     */       }
/*  465:     */     }
/*  466: 660 */     if (Utils.gr(Utils.sum(probs), 0.0D))
/*  467:     */     {
/*  468: 661 */       Utils.normalize(probs);
/*  469: 662 */       return probs;
/*  470:     */     }
/*  471: 664 */     return this.m_ZeroR.distributionForInstance(inst);
/*  472:     */   }
/*  473:     */   
/*  474:     */   public String toString()
/*  475:     */   {
/*  476: 675 */     if (this.m_Classifiers == null) {
/*  477: 676 */       return "MultiClassClassifier: No model built yet.";
/*  478:     */     }
/*  479: 678 */     StringBuffer text = new StringBuffer();
/*  480: 679 */     text.append("MultiClassClassifier\n\n");
/*  481: 680 */     for (int i = 0; i < this.m_Classifiers.length; i++)
/*  482:     */     {
/*  483: 681 */       text.append("Classifier ").append(i + 1);
/*  484: 682 */       if (this.m_Classifiers[i] != null)
/*  485:     */       {
/*  486: 683 */         if ((this.m_ClassFilters != null) && (this.m_ClassFilters[i] != null)) {
/*  487: 684 */           if ((this.m_ClassFilters[i] instanceof RemoveWithValues))
/*  488:     */           {
/*  489: 685 */             Range range = new Range(((RemoveWithValues)this.m_ClassFilters[i]).getNominalIndices());
/*  490:     */             
/*  491: 687 */             range.setUpper(this.m_ClassAttribute.numValues());
/*  492: 688 */             int[] pair = range.getSelection();
/*  493: 689 */             text.append(", " + (pair[0] + 1) + " vs " + (pair[1] + 1));
/*  494:     */           }
/*  495: 690 */           else if ((this.m_ClassFilters[i] instanceof MakeIndicator))
/*  496:     */           {
/*  497: 691 */             text.append(", using indicator values: ");
/*  498: 692 */             text.append(((MakeIndicator)this.m_ClassFilters[i]).getValueRange());
/*  499:     */           }
/*  500:     */         }
/*  501: 695 */         text.append('\n');
/*  502: 696 */         text.append(this.m_Classifiers[i].toString() + "\n\n");
/*  503:     */       }
/*  504:     */       else
/*  505:     */       {
/*  506: 698 */         text.append(" Skipped (no training examples)\n");
/*  507:     */       }
/*  508:     */     }
/*  509: 702 */     return text.toString();
/*  510:     */   }
/*  511:     */   
/*  512:     */   public Enumeration<Option> listOptions()
/*  513:     */   {
/*  514: 712 */     Vector<Option> vec = new Vector(3);
/*  515:     */     
/*  516: 714 */     vec.addElement(new Option("\tSets the method to use. Valid values are 0 (1-against-all),\n\t1 (random codes), 2 (exhaustive code), and 3 (1-against-1). (default 0)\n", "M", 1, "-M <num>"));
/*  517:     */     
/*  518:     */ 
/*  519:     */ 
/*  520: 718 */     vec.addElement(new Option("\tSets the multiplier when using random codes. (default 2.0)", "R", 1, "-R <num>"));
/*  521:     */     
/*  522:     */ 
/*  523: 721 */     vec.addElement(new Option("\tUse pairwise coupling (only has an effect for 1-against1)", "P", 0, "-P"));
/*  524:     */     
/*  525:     */ 
/*  526: 724 */     vec.addElement(new Option("\tUse log loss decoding for random and exhaustive codes", "L", 0, "-L"));
/*  527:     */     
/*  528: 726 */     vec.addAll(Collections.list(super.listOptions()));
/*  529:     */     
/*  530: 728 */     return vec.elements();
/*  531:     */   }
/*  532:     */   
/*  533:     */   public void setOptions(String[] options)
/*  534:     */     throws Exception
/*  535:     */   {
/*  536: 783 */     String errorString = Utils.getOption('M', options);
/*  537: 784 */     if (errorString.length() != 0) {
/*  538: 785 */       setMethod(new SelectedTag(Integer.parseInt(errorString), TAGS_METHOD));
/*  539:     */     } else {
/*  540: 788 */       setMethod(new SelectedTag(0, TAGS_METHOD));
/*  541:     */     }
/*  542: 791 */     String rfactorString = Utils.getOption('R', options);
/*  543: 792 */     if (rfactorString.length() != 0) {
/*  544: 793 */       setRandomWidthFactor(new Double(rfactorString).doubleValue());
/*  545:     */     } else {
/*  546: 795 */       setRandomWidthFactor(2.0D);
/*  547:     */     }
/*  548: 798 */     setUsePairwiseCoupling(Utils.getFlag('P', options));
/*  549:     */     
/*  550: 800 */     setLogLossDecoding(Utils.getFlag('L', options));
/*  551:     */     
/*  552: 802 */     super.setOptions(options);
/*  553:     */     
/*  554: 804 */     Utils.checkForRemainingOptions(options);
/*  555:     */   }
/*  556:     */   
/*  557:     */   public String[] getOptions()
/*  558:     */   {
/*  559: 814 */     Vector<String> options = new Vector();
/*  560:     */     
/*  561: 816 */     options.add("-M");
/*  562: 817 */     options.add("" + this.m_Method);
/*  563: 819 */     if (getUsePairwiseCoupling()) {
/*  564: 820 */       options.add("-P");
/*  565:     */     }
/*  566: 823 */     if (getLogLossDecoding()) {
/*  567: 824 */       options.add("-L");
/*  568:     */     }
/*  569: 827 */     options.add("-R");
/*  570: 828 */     options.add("" + this.m_RandomWidthFactor);
/*  571:     */     
/*  572: 830 */     Collections.addAll(options, super.getOptions());
/*  573:     */     
/*  574: 832 */     return (String[])options.toArray(new String[0]);
/*  575:     */   }
/*  576:     */   
/*  577:     */   public String globalInfo()
/*  578:     */   {
/*  579: 841 */     return "A metaclassifier for handling multi-class datasets with 2-class classifiers. This classifier is also capable of applying error correcting output codes for increased accuracy.";
/*  580:     */   }
/*  581:     */   
/*  582:     */   public String logLossDecodingTipText()
/*  583:     */   {
/*  584: 852 */     return "Use log loss decoding for random or exhaustive codes.";
/*  585:     */   }
/*  586:     */   
/*  587:     */   public boolean getLogLossDecoding()
/*  588:     */   {
/*  589: 862 */     return this.m_logLossDecoding;
/*  590:     */   }
/*  591:     */   
/*  592:     */   public void setLogLossDecoding(boolean newlogLossDecoding)
/*  593:     */   {
/*  594: 872 */     this.m_logLossDecoding = newlogLossDecoding;
/*  595:     */   }
/*  596:     */   
/*  597:     */   public String randomWidthFactorTipText()
/*  598:     */   {
/*  599: 881 */     return "Sets the width multiplier when using random codes. The number of codes generated will be thus number multiplied by the number of classes.";
/*  600:     */   }
/*  601:     */   
/*  602:     */   public double getRandomWidthFactor()
/*  603:     */   {
/*  604: 894 */     return this.m_RandomWidthFactor;
/*  605:     */   }
/*  606:     */   
/*  607:     */   public void setRandomWidthFactor(double newRandomWidthFactor)
/*  608:     */   {
/*  609: 905 */     this.m_RandomWidthFactor = newRandomWidthFactor;
/*  610:     */   }
/*  611:     */   
/*  612:     */   public String methodTipText()
/*  613:     */   {
/*  614: 913 */     return "Sets the method to use for transforming the multi-class problem into several 2-class ones.";
/*  615:     */   }
/*  616:     */   
/*  617:     */   public SelectedTag getMethod()
/*  618:     */   {
/*  619: 925 */     return new SelectedTag(this.m_Method, TAGS_METHOD);
/*  620:     */   }
/*  621:     */   
/*  622:     */   public void setMethod(SelectedTag newMethod)
/*  623:     */   {
/*  624: 936 */     if (newMethod.getTags() == TAGS_METHOD) {
/*  625: 937 */       this.m_Method = newMethod.getSelectedTag().getID();
/*  626:     */     }
/*  627:     */   }
/*  628:     */   
/*  629:     */   public void setUsePairwiseCoupling(boolean p)
/*  630:     */   {
/*  631: 948 */     this.m_pairwiseCoupling = p;
/*  632:     */   }
/*  633:     */   
/*  634:     */   public boolean getUsePairwiseCoupling()
/*  635:     */   {
/*  636: 958 */     return this.m_pairwiseCoupling;
/*  637:     */   }
/*  638:     */   
/*  639:     */   public String usePairwiseCouplingTipText()
/*  640:     */   {
/*  641: 966 */     return "Use pairwise coupling (only has an effect for 1-against-1).";
/*  642:     */   }
/*  643:     */   
/*  644:     */   public static double[] pairwiseCoupling(double[][] n, double[][] r)
/*  645:     */   {
/*  646: 979 */     double[] p = new double[r.length];
/*  647: 980 */     for (int i = 0; i < p.length; i++) {
/*  648: 981 */       p[i] = (1.0D / p.length);
/*  649:     */     }
/*  650: 983 */     double[][] u = new double[r.length][r.length];
/*  651: 984 */     for (int i = 0; i < r.length; i++) {
/*  652: 985 */       for (int j = i + 1; j < r.length; j++) {
/*  653: 986 */         u[i][j] = 0.5D;
/*  654:     */       }
/*  655:     */     }
/*  656: 991 */     double[] firstSum = new double[p.length];
/*  657: 992 */     for (int i = 0; i < p.length; i++) {
/*  658: 993 */       for (int j = i + 1; j < p.length; j++)
/*  659:     */       {
/*  660: 994 */         firstSum[i] += n[i][j] * r[i][j];
/*  661: 995 */         firstSum[j] += n[i][j] * (1.0D - r[i][j]);
/*  662:     */       }
/*  663:     */     }
/*  664:     */     boolean changed;
/*  665:     */     do
/*  666:     */     {
/*  667:1002 */       changed = false;
/*  668:1003 */       double[] secondSum = new double[p.length];
/*  669:1004 */       for (int i = 0; i < p.length; i++) {
/*  670:1005 */         for (int j = i + 1; j < p.length; j++)
/*  671:     */         {
/*  672:1006 */           secondSum[i] += n[i][j] * u[i][j];
/*  673:1007 */           secondSum[j] += n[i][j] * (1.0D - u[i][j]);
/*  674:     */         }
/*  675:     */       }
/*  676:1010 */       for (int i = 0; i < p.length; i++) {
/*  677:1011 */         if ((firstSum[i] == 0.0D) || (secondSum[i] == 0.0D))
/*  678:     */         {
/*  679:1012 */           if (p[i] > 0.0D) {
/*  680:1013 */             changed = true;
/*  681:     */           }
/*  682:1015 */           p[i] = 0.0D;
/*  683:     */         }
/*  684:     */         else
/*  685:     */         {
/*  686:1017 */           double factor = firstSum[i] / secondSum[i];
/*  687:1018 */           double pOld = p[i];
/*  688:1019 */           p[i] *= factor;
/*  689:1020 */           if (Math.abs(pOld - p[i]) > 0.001D) {
/*  690:1021 */             changed = true;
/*  691:     */           }
/*  692:     */         }
/*  693:     */       }
/*  694:1025 */       Utils.normalize(p);
/*  695:1026 */       for (int i = 0; i < r.length; i++) {
/*  696:1027 */         for (int j = i + 1; j < r.length; j++) {
/*  697:1028 */           u[i][j] = (p[i] / (p[i] + p[j]));
/*  698:     */         }
/*  699:     */       }
/*  700:1031 */     } while (changed);
/*  701:1032 */     return p;
/*  702:     */   }
/*  703:     */   
/*  704:     */   public String getRevision()
/*  705:     */   {
/*  706:1041 */     return RevisionUtils.extract("$Revision: 11889 $");
/*  707:     */   }
/*  708:     */   
/*  709:     */   public static void main(String[] argv)
/*  710:     */   {
/*  711:1050 */     runClassifier(new MultiClassClassifier(), argv);
/*  712:     */   }
/*  713:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.MultiClassClassifier
 * JD-Core Version:    0.7.0.1
 */