/*    1:     */ package weka.classifiers.meta;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.Collections;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.HashSet;
/*    7:     */ import java.util.List;
/*    8:     */ import java.util.Random;
/*    9:     */ import java.util.Set;
/*   10:     */ import java.util.Vector;
/*   11:     */ import java.util.concurrent.Callable;
/*   12:     */ import java.util.concurrent.ExecutorService;
/*   13:     */ import java.util.concurrent.Executors;
/*   14:     */ import java.util.concurrent.Future;
/*   15:     */ import weka.classifiers.AbstractClassifier;
/*   16:     */ import weka.classifiers.Classifier;
/*   17:     */ import weka.classifiers.IterativeClassifier;
/*   18:     */ import weka.classifiers.RandomizableClassifier;
/*   19:     */ import weka.classifiers.evaluation.Evaluation;
/*   20:     */ import weka.classifiers.evaluation.EvaluationMetricHelper;
/*   21:     */ import weka.core.AdditionalMeasureProducer;
/*   22:     */ import weka.core.Attribute;
/*   23:     */ import weka.core.Capabilities;
/*   24:     */ import weka.core.Capabilities.Capability;
/*   25:     */ import weka.core.Instance;
/*   26:     */ import weka.core.Instances;
/*   27:     */ import weka.core.Option;
/*   28:     */ import weka.core.OptionHandler;
/*   29:     */ import weka.core.RevisionUtils;
/*   30:     */ import weka.core.SelectedTag;
/*   31:     */ import weka.core.Tag;
/*   32:     */ import weka.core.Utils;
/*   33:     */ 
/*   34:     */ public class IterativeClassifierOptimizer
/*   35:     */   extends RandomizableClassifier
/*   36:     */   implements AdditionalMeasureProducer
/*   37:     */ {
/*   38:     */   private static final long serialVersionUID = -3665485256313525864L;
/*   39: 197 */   protected IterativeClassifier m_IterativeClassifier = new LogitBoost();
/*   40: 200 */   protected int m_NumFolds = 10;
/*   41: 203 */   protected int m_NumRuns = 1;
/*   42: 206 */   protected int m_StepSize = 1;
/*   43: 209 */   protected boolean m_UseAverage = false;
/*   44: 212 */   protected int m_lookAheadIterations = 50;
/*   45:     */   public static Tag[] TAGS_EVAL;
/*   46:     */   
/*   47:     */   static
/*   48:     */   {
/*   49: 217 */     List<String> evalNames = EvaluationMetricHelper.getAllMetricNames();
/*   50: 218 */     TAGS_EVAL = new Tag[evalNames.size()];
/*   51: 219 */     for (int i = 0; i < evalNames.size(); i++) {
/*   52: 220 */       TAGS_EVAL[i] = new Tag(i, (String)evalNames.get(i), (String)evalNames.get(i), false);
/*   53:     */     }
/*   54:     */   }
/*   55:     */   
/*   56: 225 */   protected String m_evalMetric = "rmse";
/*   57: 231 */   protected int m_classValueIndex = -1;
/*   58: 237 */   protected double[] m_thresholds = null;
/*   59: 240 */   protected double m_bestResult = 1.7976931348623157E+308D;
/*   60:     */   protected int m_bestNumIts;
/*   61: 246 */   protected int m_numThreads = 1;
/*   62: 249 */   protected int m_poolSize = 1;
/*   63:     */   
/*   64:     */   public String globalInfo()
/*   65:     */   {
/*   66: 259 */     return "Optimizes the number of iterations of the given iterative classifier using cross-validation.";
/*   67:     */   }
/*   68:     */   
/*   69:     */   protected String defaultIterativeClassifierString()
/*   70:     */   {
/*   71: 268 */     return "weka.classifiers.meta.LogitBoost";
/*   72:     */   }
/*   73:     */   
/*   74:     */   public String useAverageTipText()
/*   75:     */   {
/*   76: 278 */     return "If true, average estimates are used instead of one estimate from pooled predictions.";
/*   77:     */   }
/*   78:     */   
/*   79:     */   public boolean getUseAverage()
/*   80:     */   {
/*   81: 288 */     return this.m_UseAverage;
/*   82:     */   }
/*   83:     */   
/*   84:     */   public void setUseAverage(boolean newUseAverage)
/*   85:     */   {
/*   86: 298 */     this.m_UseAverage = newUseAverage;
/*   87:     */   }
/*   88:     */   
/*   89:     */   public String numThreadsTipText()
/*   90:     */   {
/*   91: 306 */     return "The number of threads to use, which should be >= size of thread pool.";
/*   92:     */   }
/*   93:     */   
/*   94:     */   public int getNumThreads()
/*   95:     */   {
/*   96: 314 */     return this.m_numThreads;
/*   97:     */   }
/*   98:     */   
/*   99:     */   public void setNumThreads(int nT)
/*  100:     */   {
/*  101: 322 */     this.m_numThreads = nT;
/*  102:     */   }
/*  103:     */   
/*  104:     */   public String poolSizeTipText()
/*  105:     */   {
/*  106: 330 */     return "The size of the thread pool, for example, the number of cores in the CPU.";
/*  107:     */   }
/*  108:     */   
/*  109:     */   public int getPoolSize()
/*  110:     */   {
/*  111: 338 */     return this.m_poolSize;
/*  112:     */   }
/*  113:     */   
/*  114:     */   public void setPoolSize(int nT)
/*  115:     */   {
/*  116: 346 */     this.m_poolSize = nT;
/*  117:     */   }
/*  118:     */   
/*  119:     */   public String stepSizeTipText()
/*  120:     */   {
/*  121: 356 */     return "Step size for the evaluation, if evaluation is time consuming.";
/*  122:     */   }
/*  123:     */   
/*  124:     */   public int getStepSize()
/*  125:     */   {
/*  126: 366 */     return this.m_StepSize;
/*  127:     */   }
/*  128:     */   
/*  129:     */   public void setStepSize(int newStepSize)
/*  130:     */   {
/*  131: 376 */     this.m_StepSize = newStepSize;
/*  132:     */   }
/*  133:     */   
/*  134:     */   public String numRunsTipText()
/*  135:     */   {
/*  136: 386 */     return "Number of runs for cross-validation.";
/*  137:     */   }
/*  138:     */   
/*  139:     */   public int getNumRuns()
/*  140:     */   {
/*  141: 396 */     return this.m_NumRuns;
/*  142:     */   }
/*  143:     */   
/*  144:     */   public void setNumRuns(int newNumRuns)
/*  145:     */   {
/*  146: 406 */     this.m_NumRuns = newNumRuns;
/*  147:     */   }
/*  148:     */   
/*  149:     */   public String numFoldsTipText()
/*  150:     */   {
/*  151: 416 */     return "Number of folds for cross-validation.";
/*  152:     */   }
/*  153:     */   
/*  154:     */   public int getNumFolds()
/*  155:     */   {
/*  156: 426 */     return this.m_NumFolds;
/*  157:     */   }
/*  158:     */   
/*  159:     */   public void setNumFolds(int newNumFolds)
/*  160:     */   {
/*  161: 436 */     this.m_NumFolds = newNumFolds;
/*  162:     */   }
/*  163:     */   
/*  164:     */   public String lookAheadIterationsTipText()
/*  165:     */   {
/*  166: 446 */     return "The number of iterations to look ahead for to find a better optimum.";
/*  167:     */   }
/*  168:     */   
/*  169:     */   public int getLookAheadIterations()
/*  170:     */   {
/*  171: 456 */     return this.m_lookAheadIterations;
/*  172:     */   }
/*  173:     */   
/*  174:     */   public void setLookAheadIterations(int newLookAheadIterations)
/*  175:     */   {
/*  176: 466 */     this.m_lookAheadIterations = newLookAheadIterations;
/*  177:     */   }
/*  178:     */   
/*  179:     */   public void buildClassifier(Instances data)
/*  180:     */     throws Exception
/*  181:     */   {
/*  182: 475 */     if (this.m_IterativeClassifier == null) {
/*  183: 476 */       throw new Exception("A base classifier has not been specified!");
/*  184:     */     }
/*  185: 480 */     getCapabilities().testWithFail(data);
/*  186:     */     
/*  187:     */ 
/*  188: 483 */     Random randomInstance = new Random(this.m_Seed);
/*  189:     */     
/*  190:     */ 
/*  191: 486 */     Instances origData = data;
/*  192:     */     
/*  193:     */ 
/*  194: 489 */     data = new Instances(data);
/*  195: 490 */     data.deleteWithMissingClass();
/*  196: 492 */     if (data.numInstances() < this.m_NumFolds)
/*  197:     */     {
/*  198: 493 */       System.err.println("WARNING: reducing number of folds to number of instances in IterativeClassifierOptimizer");
/*  199:     */       
/*  200: 495 */       this.m_NumFolds = data.numInstances();
/*  201:     */     }
/*  202: 499 */     Instances[][] trainingSets = new Instances[this.m_NumRuns][this.m_NumFolds];
/*  203: 500 */     Instances[][] testSets = new Instances[this.m_NumRuns][this.m_NumFolds];
/*  204: 501 */     final IterativeClassifier[][] classifiers = new IterativeClassifier[this.m_NumRuns][this.m_NumFolds];
/*  205: 502 */     for (int j = 0; j < this.m_NumRuns; j++)
/*  206:     */     {
/*  207: 503 */       data.randomize(randomInstance);
/*  208: 504 */       if (data.classAttribute().isNominal()) {
/*  209: 505 */         data.stratify(this.m_NumFolds);
/*  210:     */       }
/*  211: 507 */       for (int i = 0; i < this.m_NumFolds; i++)
/*  212:     */       {
/*  213: 508 */         trainingSets[j][i] = data.trainCV(this.m_NumFolds, i, randomInstance);
/*  214: 509 */         testSets[j][i] = data.testCV(this.m_NumFolds, i);
/*  215: 510 */         classifiers[j][i] = ((IterativeClassifier)AbstractClassifier.makeCopy(this.m_IterativeClassifier));
/*  216:     */         
/*  217: 512 */         classifiers[j][i].initializeClassifier(trainingSets[j][i]);
/*  218:     */       }
/*  219:     */     }
/*  220: 517 */     ExecutorService pool = Executors.newFixedThreadPool(this.m_poolSize);
/*  221:     */     
/*  222:     */ 
/*  223: 520 */     Evaluation eval = new Evaluation(data);
/*  224: 521 */     EvaluationMetricHelper helper = new EvaluationMetricHelper(eval);
/*  225: 522 */     boolean maximise = helper.metricIsMaximisable(this.m_evalMetric);
/*  226: 523 */     if (maximise) {
/*  227: 524 */       this.m_bestResult = 4.9E-324D;
/*  228:     */     } else {
/*  229: 526 */       this.m_bestResult = 1.7976931348623157E+308D;
/*  230:     */     }
/*  231: 528 */     this.m_thresholds = null;
/*  232: 529 */     int numIts = 0;
/*  233: 530 */     this.m_bestNumIts = 0;
/*  234: 531 */     int numberOfIterationsSinceMinimum = -1;
/*  235:     */     for (;;)
/*  236:     */     {
/*  237: 535 */       if (numIts % this.m_StepSize == 0)
/*  238:     */       {
/*  239: 537 */         double result = 0.0D;
/*  240: 538 */         double[] tempThresholds = null;
/*  241: 541 */         if (!this.m_UseAverage)
/*  242:     */         {
/*  243: 542 */           eval = new Evaluation(data);
/*  244: 543 */           helper.setEvaluation(eval);
/*  245: 544 */           for (int r = 0; r < this.m_NumRuns; r++) {
/*  246: 545 */             for (int i = 0; i < this.m_NumFolds; i++) {
/*  247: 546 */               eval.evaluateModel(classifiers[r][i], testSets[r][i], new Object[0]);
/*  248:     */             }
/*  249:     */           }
/*  250: 549 */           result = getClassValueIndex() >= 0 ? helper.getNamedMetric(this.m_evalMetric, new int[] { getClassValueIndex() }) : helper.getNamedMetric(this.m_evalMetric, new int[0]);
/*  251:     */           
/*  252:     */ 
/*  253:     */ 
/*  254: 553 */           tempThresholds = helper.getNamedMetricThresholds(this.m_evalMetric);
/*  255:     */         }
/*  256:     */         else
/*  257:     */         {
/*  258: 557 */           for (int r = 0; r < this.m_NumRuns; r++) {
/*  259: 558 */             for (int i = 0; i < this.m_NumFolds; i++)
/*  260:     */             {
/*  261: 559 */               eval = new Evaluation(trainingSets[r][i]);
/*  262: 560 */               helper.setEvaluation(eval);
/*  263: 561 */               eval.evaluateModel(classifiers[r][i], testSets[r][i], new Object[0]);
/*  264: 562 */               result += (getClassValueIndex() >= 0 ? helper.getNamedMetric(this.m_evalMetric, new int[] { getClassValueIndex() }) : helper.getNamedMetric(this.m_evalMetric, new int[0]));
/*  265:     */               
/*  266:     */ 
/*  267:     */ 
/*  268: 566 */               double[] thresholds = helper.getNamedMetricThresholds(this.m_evalMetric);
/*  269: 569 */               if (thresholds != null)
/*  270:     */               {
/*  271: 570 */                 if (tempThresholds == null) {
/*  272: 571 */                   tempThresholds = new double[data.numClasses()];
/*  273:     */                 }
/*  274: 573 */                 for (int j = 0; j < thresholds.length; j++) {
/*  275: 574 */                   tempThresholds[j] += thresholds[j];
/*  276:     */                 }
/*  277:     */               }
/*  278:     */             }
/*  279:     */           }
/*  280: 579 */           result /= this.m_NumFolds * this.m_NumRuns;
/*  281: 582 */           if (tempThresholds != null) {
/*  282: 583 */             for (int j = 0; j < tempThresholds.length; j++) {
/*  283: 584 */               tempThresholds[j] /= this.m_NumRuns * this.m_NumFolds;
/*  284:     */             }
/*  285:     */           }
/*  286:     */         }
/*  287: 589 */         if (this.m_Debug)
/*  288:     */         {
/*  289: 590 */           System.err.println("Iteration: " + numIts + " " + "Measure: " + result);
/*  290: 591 */           if (tempThresholds != null)
/*  291:     */           {
/*  292: 592 */             System.err.print("Thresholds:");
/*  293: 593 */             for (int j = 0; j < tempThresholds.length; j++) {
/*  294: 594 */               System.err.print(" " + tempThresholds[j]);
/*  295:     */             }
/*  296: 596 */             System.err.println();
/*  297:     */           }
/*  298:     */         }
/*  299: 600 */         double delta = maximise ? this.m_bestResult - result : result - this.m_bestResult;
/*  300: 603 */         if (delta < 0.0D)
/*  301:     */         {
/*  302: 604 */           this.m_bestResult = result;
/*  303: 605 */           this.m_bestNumIts = numIts;
/*  304: 606 */           this.m_thresholds = tempThresholds;
/*  305: 607 */           numberOfIterationsSinceMinimum = -1;
/*  306:     */         }
/*  307:     */       }
/*  308: 610 */       numberOfIterationsSinceMinimum++;
/*  309: 611 */       numIts++;
/*  310: 613 */       if (numberOfIterationsSinceMinimum >= this.m_lookAheadIterations) {
/*  311:     */         break;
/*  312:     */       }
/*  313: 618 */       int numRuns = this.m_NumRuns * this.m_NumFolds;
/*  314: 619 */       final int N = this.m_NumFolds;
/*  315: 620 */       int chunksize = numRuns / this.m_numThreads;
/*  316: 621 */       Set<Future<Boolean>> results = new HashSet();
/*  317: 624 */       for (int j = 0; j < this.m_numThreads; j++)
/*  318:     */       {
/*  319: 627 */         final int lo = j * chunksize;
/*  320: 628 */         final int hi = j < this.m_numThreads - 1 ? lo + chunksize : numRuns;
/*  321:     */         
/*  322:     */ 
/*  323: 631 */         Future<Boolean> futureT = pool.submit(new Callable()
/*  324:     */         {
/*  325:     */           public Boolean call()
/*  326:     */             throws Exception
/*  327:     */           {
/*  328: 634 */             for (int k = lo; k < hi; k++) {
/*  329: 635 */               if (!classifiers[(k / N)][(k % N)].next())
/*  330:     */               {
/*  331: 636 */                 if (IterativeClassifierOptimizer.this.m_Debug) {
/*  332: 637 */                   System.err.println("Classifier failed to iterate in cross-validation.");
/*  333:     */                 }
/*  334: 639 */                 return Boolean.valueOf(false);
/*  335:     */               }
/*  336:     */             }
/*  337: 642 */             return Boolean.valueOf(true);
/*  338:     */           }
/*  339: 644 */         });
/*  340: 645 */         results.add(futureT);
/*  341:     */       }
/*  342:     */       try
/*  343:     */       {
/*  344: 650 */         boolean failure = false;
/*  345: 651 */         for (Future<Boolean> futureT : results) {
/*  346: 652 */           if (!((Boolean)futureT.get()).booleanValue())
/*  347:     */           {
/*  348: 653 */             failure = true;
/*  349: 654 */             break;
/*  350:     */           }
/*  351:     */         }
/*  352: 657 */         if (failure) {
/*  353:     */           break;
/*  354:     */         }
/*  355:     */       }
/*  356:     */       catch (Exception e)
/*  357:     */       {
/*  358: 661 */         System.out.println("Classifiers could not be generated.");
/*  359: 662 */         e.printStackTrace();
/*  360:     */       }
/*  361:     */     }
/*  362: 665 */     trainingSets = (Instances[][])null;
/*  363: 666 */     testSets = (Instances[][])null;
/*  364: 667 */     data = null;
/*  365:     */     
/*  366:     */ 
/*  367: 670 */     this.m_IterativeClassifier.initializeClassifier(origData);
/*  368: 671 */     int i = 0;
/*  369: 672 */     while ((i++ < this.m_bestNumIts) && (this.m_IterativeClassifier.next())) {}
/*  370: 675 */     this.m_IterativeClassifier.done();
/*  371:     */     
/*  372:     */ 
/*  373: 678 */     pool.shutdown();
/*  374:     */   }
/*  375:     */   
/*  376:     */   public double[] distributionForInstance(Instance inst)
/*  377:     */     throws Exception
/*  378:     */   {
/*  379: 688 */     if (this.m_thresholds != null)
/*  380:     */     {
/*  381: 689 */       double[] dist = this.m_IterativeClassifier.distributionForInstance(inst);
/*  382: 690 */       double[] newDist = new double[dist.length];
/*  383: 691 */       for (int i = 0; i < dist.length; i++) {
/*  384: 692 */         if (dist[i] >= this.m_thresholds[i]) {
/*  385: 693 */           newDist[i] = 1.0D;
/*  386:     */         }
/*  387:     */       }
/*  388: 696 */       Utils.normalize(newDist);
/*  389: 697 */       return newDist;
/*  390:     */     }
/*  391: 699 */     return this.m_IterativeClassifier.distributionForInstance(inst);
/*  392:     */   }
/*  393:     */   
/*  394:     */   public String toString()
/*  395:     */   {
/*  396: 709 */     if (this.m_IterativeClassifier == null) {
/*  397: 710 */       return "No classifier built yet.";
/*  398:     */     }
/*  399: 712 */     StringBuffer sb = new StringBuffer();
/*  400: 713 */     sb.append("Best value found: " + this.m_bestResult + "\n");
/*  401: 714 */     sb.append("Best number of iterations found: " + this.m_bestNumIts + "\n\n");
/*  402: 715 */     if (this.m_thresholds != null)
/*  403:     */     {
/*  404: 716 */       sb.append("Thresholds found: ");
/*  405: 717 */       for (int i = 0; i < this.m_thresholds.length; i++) {
/*  406: 718 */         sb.append(this.m_thresholds[i] + " ");
/*  407:     */       }
/*  408:     */     }
/*  409: 721 */     sb.append("\n\n");
/*  410: 722 */     sb.append(this.m_IterativeClassifier.toString());
/*  411: 723 */     return sb.toString();
/*  412:     */   }
/*  413:     */   
/*  414:     */   public Enumeration<Option> listOptions()
/*  415:     */   {
/*  416: 735 */     Vector<Option> newVector = new Vector(7);
/*  417:     */     
/*  418: 737 */     newVector.addElement(new Option("\tIf set, average estimate is used rather than one estimate from pooled predictions.\n", "A", 0, "-A"));
/*  419:     */     
/*  420: 739 */     newVector.addElement(new Option("\t" + lookAheadIterationsTipText() + "\n" + "\t(default 50)", "L", 1, "-L <num>"));
/*  421:     */     
/*  422: 741 */     newVector.addElement(new Option("\t" + poolSizeTipText() + "\n\t(default 1)", "P", 1, "-P <int>"));
/*  423:     */     
/*  424: 743 */     newVector.addElement(new Option("\t" + numThreadsTipText() + "\n" + "\t(default 1)", "E", 1, "-E <int>"));
/*  425:     */     
/*  426: 745 */     newVector.addElement(new Option("\t" + stepSizeTipText() + "\n" + "\t(default 1)", "I", 1, "-I <num>"));
/*  427:     */     
/*  428: 747 */     newVector.addElement(new Option("\tNumber of folds for cross-validation.\n\t(default 10)", "F", 1, "-F <num>"));
/*  429:     */     
/*  430: 749 */     newVector.addElement(new Option("\tNumber of runs for cross-validation.\n\t(default 1)", "R", 1, "-R <num>"));
/*  431:     */     
/*  432:     */ 
/*  433: 752 */     newVector.addElement(new Option("\tFull name of base classifier.\n\t(default: " + defaultIterativeClassifierString() + ")", "W", 1, "-W"));
/*  434:     */     
/*  435:     */ 
/*  436:     */ 
/*  437:     */ 
/*  438: 757 */     List<String> metrics = EvaluationMetricHelper.getAllMetricNames();
/*  439: 758 */     StringBuilder b = new StringBuilder();
/*  440: 759 */     int length = 0;
/*  441: 760 */     for (String m : metrics)
/*  442:     */     {
/*  443: 761 */       b.append(m.toLowerCase()).append(",");
/*  444:     */       
/*  445: 763 */       length += m.length();
/*  446: 764 */       if (length >= 60)
/*  447:     */       {
/*  448: 765 */         b.append("\n\t");
/*  449: 766 */         length = 0;
/*  450:     */       }
/*  451:     */     }
/*  452: 769 */     newVector.addElement(new Option("\tEvaluation metric to optimise (default rmse). Available metrics:\n\t" + b.substring(0, b.length() - 1), "metric", 1, "-metric <name>"));
/*  453:     */     
/*  454:     */ 
/*  455:     */ 
/*  456: 773 */     newVector.addElement(new Option("\tClass value index to optimise. Ignored for all but information-retrieval\n\ttype metrics (such as roc area). If unspecified (or a negative value is supplied),\n\tand an information-retrieval metric is specified, then the class-weighted average\n\tmetric used. (default -1)", "class-value-index", 1, "-class-value-index <0-based index>"));
/*  457:     */     
/*  458:     */ 
/*  459:     */ 
/*  460:     */ 
/*  461:     */ 
/*  462:     */ 
/*  463:     */ 
/*  464: 781 */     newVector.addAll(Collections.list(super.listOptions()));
/*  465:     */     
/*  466: 783 */     newVector.addElement(new Option("", "", 0, "\nOptions specific to classifier " + this.m_IterativeClassifier.getClass().getName() + ":"));
/*  467:     */     
/*  468:     */ 
/*  469: 786 */     newVector.addAll(Collections.list(((OptionHandler)this.m_IterativeClassifier).listOptions()));
/*  470:     */     
/*  471:     */ 
/*  472: 789 */     return newVector.elements();
/*  473:     */   }
/*  474:     */   
/*  475:     */   public void setOptions(String[] options)
/*  476:     */     throws Exception
/*  477:     */   {
/*  478: 809 */     super.setOptions(options);
/*  479:     */     
/*  480: 811 */     setUseAverage(Utils.getFlag('A', options));
/*  481:     */     
/*  482: 813 */     String lookAheadIterations = Utils.getOption('L', options);
/*  483: 814 */     if (lookAheadIterations.length() != 0) {
/*  484: 815 */       setLookAheadIterations(Integer.parseInt(lookAheadIterations));
/*  485:     */     } else {
/*  486: 817 */       setLookAheadIterations(50);
/*  487:     */     }
/*  488: 819 */     String PoolSize = Utils.getOption('P', options);
/*  489: 820 */     if (PoolSize.length() != 0) {
/*  490: 821 */       setPoolSize(Integer.parseInt(PoolSize));
/*  491:     */     } else {
/*  492: 823 */       setPoolSize(1);
/*  493:     */     }
/*  494: 825 */     String NumThreads = Utils.getOption('E', options);
/*  495: 826 */     if (NumThreads.length() != 0) {
/*  496: 827 */       setNumThreads(Integer.parseInt(NumThreads));
/*  497:     */     } else {
/*  498: 829 */       setNumThreads(1);
/*  499:     */     }
/*  500: 832 */     String stepSize = Utils.getOption('I', options);
/*  501: 833 */     if (stepSize.length() != 0) {
/*  502: 834 */       setStepSize(Integer.parseInt(stepSize));
/*  503:     */     } else {
/*  504: 836 */       setStepSize(1);
/*  505:     */     }
/*  506: 839 */     String numFolds = Utils.getOption('F', options);
/*  507: 840 */     if (numFolds.length() != 0) {
/*  508: 841 */       setNumFolds(Integer.parseInt(numFolds));
/*  509:     */     } else {
/*  510: 843 */       setNumFolds(10);
/*  511:     */     }
/*  512: 846 */     String numRuns = Utils.getOption('R', options);
/*  513: 847 */     if (numRuns.length() != 0) {
/*  514: 848 */       setNumRuns(Integer.parseInt(numRuns));
/*  515:     */     } else {
/*  516: 850 */       setNumRuns(1);
/*  517:     */     }
/*  518: 853 */     String evalMetric = Utils.getOption("metric", options);
/*  519: 854 */     if (evalMetric.length() > 0)
/*  520:     */     {
/*  521: 855 */       boolean found = false;
/*  522: 856 */       for (int i = 0; i < TAGS_EVAL.length; i++) {
/*  523: 857 */         if (TAGS_EVAL[i].getIDStr().equalsIgnoreCase(evalMetric))
/*  524:     */         {
/*  525: 858 */           setEvaluationMetric(new SelectedTag(i, TAGS_EVAL));
/*  526: 859 */           found = true;
/*  527: 860 */           break;
/*  528:     */         }
/*  529:     */       }
/*  530: 864 */       if (!found) {
/*  531: 865 */         throw new Exception("Unknown evaluation metric: " + evalMetric);
/*  532:     */       }
/*  533:     */     }
/*  534: 869 */     String classValIndex = Utils.getOption("class-value-index", options);
/*  535: 870 */     if (classValIndex.length() > 0) {
/*  536: 871 */       setClassValueIndex(Integer.parseInt(classValIndex));
/*  537:     */     } else {
/*  538: 873 */       setClassValueIndex(-1);
/*  539:     */     }
/*  540: 876 */     String classifierName = Utils.getOption('W', options);
/*  541: 878 */     if (classifierName.length() > 0) {
/*  542: 879 */       setIterativeClassifier(getIterativeClassifier(classifierName, Utils.partitionOptions(options)));
/*  543:     */     } else {
/*  544: 882 */       setIterativeClassifier(getIterativeClassifier(defaultIterativeClassifierString(), Utils.partitionOptions(options)));
/*  545:     */     }
/*  546:     */   }
/*  547:     */   
/*  548:     */   protected IterativeClassifier getIterativeClassifier(String name, String[] options)
/*  549:     */     throws Exception
/*  550:     */   {
/*  551: 896 */     Classifier c = AbstractClassifier.forName(name, options);
/*  552: 897 */     if ((c instanceof IterativeClassifier)) {
/*  553: 898 */       return (IterativeClassifier)c;
/*  554:     */     }
/*  555: 900 */     throw new IllegalArgumentException(name + " is not an IterativeClassifier.");
/*  556:     */   }
/*  557:     */   
/*  558:     */   public String[] getOptions()
/*  559:     */   {
/*  560: 913 */     Vector<String> options = new Vector();
/*  561: 915 */     if (getUseAverage()) {
/*  562: 916 */       options.add("-A");
/*  563:     */     }
/*  564: 919 */     options.add("-W");
/*  565: 920 */     options.add(getIterativeClassifier().getClass().getName());
/*  566:     */     
/*  567: 922 */     options.add("-L");
/*  568: 923 */     options.add("" + getLookAheadIterations());
/*  569:     */     
/*  570: 925 */     options.add("-P");
/*  571: 926 */     options.add("" + getPoolSize());
/*  572:     */     
/*  573: 928 */     options.add("-E");
/*  574: 929 */     options.add("" + getNumThreads());
/*  575:     */     
/*  576: 931 */     options.add("-I");
/*  577: 932 */     options.add("" + getStepSize());
/*  578:     */     
/*  579: 934 */     options.add("-F");
/*  580: 935 */     options.add("" + getNumFolds());
/*  581: 936 */     options.add("-R");
/*  582: 937 */     options.add("" + getNumRuns());
/*  583:     */     
/*  584: 939 */     options.add("-metric");
/*  585: 940 */     options.add(getEvaluationMetric().getSelectedTag().getIDStr());
/*  586: 942 */     if (getClassValueIndex() >= 0)
/*  587:     */     {
/*  588: 943 */       options.add("-class-value-index");
/*  589: 944 */       options.add("" + getClassValueIndex());
/*  590:     */     }
/*  591: 947 */     Collections.addAll(options, super.getOptions());
/*  592:     */     
/*  593: 949 */     String[] classifierOptions = ((OptionHandler)this.m_IterativeClassifier).getOptions();
/*  594: 951 */     if (classifierOptions.length > 0)
/*  595:     */     {
/*  596: 952 */       options.add("--");
/*  597: 953 */       Collections.addAll(options, classifierOptions);
/*  598:     */     }
/*  599: 956 */     return (String[])options.toArray(new String[0]);
/*  600:     */   }
/*  601:     */   
/*  602:     */   public String evaluationMetricTipText()
/*  603:     */   {
/*  604: 966 */     return "The evaluation metric to use";
/*  605:     */   }
/*  606:     */   
/*  607:     */   public void setEvaluationMetric(SelectedTag metric)
/*  608:     */   {
/*  609: 975 */     if (metric.getTags() == TAGS_EVAL) {
/*  610: 976 */       this.m_evalMetric = metric.getSelectedTag().getIDStr();
/*  611:     */     }
/*  612:     */   }
/*  613:     */   
/*  614:     */   public SelectedTag getEvaluationMetric()
/*  615:     */   {
/*  616: 986 */     for (int i = 0; i < TAGS_EVAL.length; i++) {
/*  617: 987 */       if (TAGS_EVAL[i].getIDStr().equalsIgnoreCase(this.m_evalMetric)) {
/*  618: 988 */         return new SelectedTag(i, TAGS_EVAL);
/*  619:     */       }
/*  620:     */     }
/*  621: 994 */     return new SelectedTag(12, TAGS_EVAL);
/*  622:     */   }
/*  623:     */   
/*  624:     */   public String classValueIndexTipText()
/*  625:     */   {
/*  626:1004 */     return "The class value index to use with information retrieval type metrics. A value < 0 indicates to use the class weighted average version of the metric.";
/*  627:     */   }
/*  628:     */   
/*  629:     */   public void setClassValueIndex(int i)
/*  630:     */   {
/*  631:1014 */     this.m_classValueIndex = i;
/*  632:     */   }
/*  633:     */   
/*  634:     */   public int getClassValueIndex()
/*  635:     */   {
/*  636:1023 */     return this.m_classValueIndex;
/*  637:     */   }
/*  638:     */   
/*  639:     */   public String iterativeClassifierTipText()
/*  640:     */   {
/*  641:1033 */     return "The iterative classifier to be optimized.";
/*  642:     */   }
/*  643:     */   
/*  644:     */   public Capabilities getCapabilities()
/*  645:     */   {
/*  646:     */     Capabilities result;
/*  647:     */     Capabilities result;
/*  648:1045 */     if (getIterativeClassifier() != null)
/*  649:     */     {
/*  650:1046 */       result = getIterativeClassifier().getCapabilities();
/*  651:     */     }
/*  652:     */     else
/*  653:     */     {
/*  654:1048 */       result = new Capabilities(this);
/*  655:1049 */       result.disableAll();
/*  656:     */     }
/*  657:1053 */     for (Capabilities.Capability cap : Capabilities.Capability.values()) {
/*  658:1054 */       result.enableDependency(cap);
/*  659:     */     }
/*  660:1057 */     result.setOwner(this);
/*  661:     */     
/*  662:1059 */     return result;
/*  663:     */   }
/*  664:     */   
/*  665:     */   public void setIterativeClassifier(IterativeClassifier newIterativeClassifier)
/*  666:     */   {
/*  667:1070 */     this.m_IterativeClassifier = newIterativeClassifier;
/*  668:     */   }
/*  669:     */   
/*  670:     */   public IterativeClassifier getIterativeClassifier()
/*  671:     */   {
/*  672:1080 */     return this.m_IterativeClassifier;
/*  673:     */   }
/*  674:     */   
/*  675:     */   protected String getIterativeClassifierSpec()
/*  676:     */   {
/*  677:1091 */     IterativeClassifier c = getIterativeClassifier();
/*  678:1092 */     return c.getClass().getName() + " " + Utils.joinOptions(((OptionHandler)c).getOptions());
/*  679:     */   }
/*  680:     */   
/*  681:     */   public String getRevision()
/*  682:     */   {
/*  683:1103 */     return RevisionUtils.extract("$Revision: 10649 $");
/*  684:     */   }
/*  685:     */   
/*  686:     */   public double measureBestNumIts()
/*  687:     */   {
/*  688:1112 */     return this.m_bestNumIts;
/*  689:     */   }
/*  690:     */   
/*  691:     */   public double measureBestVal()
/*  692:     */   {
/*  693:1121 */     return this.m_bestResult;
/*  694:     */   }
/*  695:     */   
/*  696:     */   public Enumeration<String> enumerateMeasures()
/*  697:     */   {
/*  698:1131 */     Vector<String> newVector = new Vector(2);
/*  699:1132 */     newVector.addElement("measureBestNumIts");
/*  700:1133 */     newVector.addElement("measureBestVal");
/*  701:1134 */     return newVector.elements();
/*  702:     */   }
/*  703:     */   
/*  704:     */   public double getMeasure(String additionalMeasureName)
/*  705:     */   {
/*  706:1146 */     if (additionalMeasureName.compareToIgnoreCase("measureBestNumIts") == 0) {
/*  707:1147 */       return measureBestNumIts();
/*  708:     */     }
/*  709:1148 */     if (additionalMeasureName.compareToIgnoreCase("measureBestVal") == 0) {
/*  710:1149 */       return measureBestVal();
/*  711:     */     }
/*  712:1151 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (IterativeClassifierOptimizer)");
/*  713:     */   }
/*  714:     */   
/*  715:     */   public static void main(String[] argv)
/*  716:     */   {
/*  717:1162 */     runClassifier(new IterativeClassifierOptimizer(), argv);
/*  718:     */   }
/*  719:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.IterativeClassifierOptimizer
 * JD-Core Version:    0.7.0.1
 */