/*    1:     */ package weka.gui.beans;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.ArrayList;
/*    6:     */ import java.util.Enumeration;
/*    7:     */ import java.util.List;
/*    8:     */ import java.util.Vector;
/*    9:     */ import java.util.concurrent.BlockingQueue;
/*   10:     */ import java.util.concurrent.LinkedBlockingQueue;
/*   11:     */ import java.util.concurrent.ThreadPoolExecutor;
/*   12:     */ import java.util.concurrent.TimeUnit;
/*   13:     */ import weka.classifiers.AggregateableEvaluation;
/*   14:     */ import weka.classifiers.Classifier;
/*   15:     */ import weka.classifiers.Evaluation;
/*   16:     */ import weka.classifiers.evaluation.ThresholdCurve;
/*   17:     */ import weka.classifiers.misc.InputMappedClassifier;
/*   18:     */ import weka.core.Attribute;
/*   19:     */ import weka.core.BatchPredictor;
/*   20:     */ import weka.core.Instance;
/*   21:     */ import weka.core.Instances;
/*   22:     */ import weka.core.OptionHandler;
/*   23:     */ import weka.core.Utils;
/*   24:     */ import weka.experiment.Task;
/*   25:     */ import weka.experiment.TaskStatusInfo;
/*   26:     */ import weka.gui.Logger;
/*   27:     */ import weka.gui.explorer.ClassifierErrorsPlotInstances;
/*   28:     */ import weka.gui.explorer.ExplorerDefaults;
/*   29:     */ import weka.gui.visualize.PlotData2D;
/*   30:     */ 
/*   31:     */ public class ClassifierPerformanceEvaluator
/*   32:     */   extends AbstractEvaluator
/*   33:     */   implements BatchClassifierListener, Serializable, UserRequestAcceptor, EventConstraints
/*   34:     */ {
/*   35:     */   private static final long serialVersionUID = -3511801418192148690L;
/*   36:     */   private transient AggregateableEvaluation m_eval;
/*   37:  64 */   private transient Instances m_aggregatedPlotInstances = null;
/*   38:  65 */   private transient ArrayList<Object> m_aggregatedPlotSizes = null;
/*   39:  66 */   private transient ArrayList<Integer> m_aggregatedPlotShapes = null;
/*   40:     */   private transient long m_currentBatchIdentifier;
/*   41:     */   private transient int m_setsComplete;
/*   42:  73 */   private final Vector<TextListener> m_textListeners = new Vector();
/*   43:  75 */   private final Vector<ThresholdDataListener> m_thresholdListeners = new Vector();
/*   44:  77 */   private final Vector<VisualizableErrorListener> m_visualizableErrorListeners = new Vector();
/*   45:     */   protected transient ThreadPoolExecutor m_executorPool;
/*   46:     */   protected transient List<EvaluationTask> m_tasks;
/*   47:     */   protected boolean m_errorPlotPointSizeProportionalToMargin;
/*   48:  88 */   protected int m_executionSlots = 2;
/*   49:  91 */   protected String m_selectedEvalMetrics = "";
/*   50:  92 */   protected List<String> m_metricsList = new ArrayList();
/*   51:     */   
/*   52:     */   public ClassifierPerformanceEvaluator()
/*   53:     */   {
/*   54:  95 */     this.m_visual.loadIcons("weka/gui/beans/icons/ClassifierPerformanceEvaluator.gif", "weka/gui/beans/icons/ClassifierPerformanceEvaluator_animated.gif");
/*   55:     */     
/*   56:     */ 
/*   57:  98 */     this.m_visual.setText("ClassifierPerformanceEvaluator");
/*   58:     */     
/*   59: 100 */     this.m_metricsList = Evaluation.getAllEvaluationMetricNames();
/*   60: 101 */     this.m_metricsList.remove("Coverage");
/*   61: 102 */     this.m_metricsList.remove("Region size");
/*   62: 103 */     StringBuilder b = new StringBuilder();
/*   63: 104 */     for (String s : this.m_metricsList) {
/*   64: 105 */       b.append(s).append(",");
/*   65:     */     }
/*   66: 107 */     this.m_selectedEvalMetrics = b.substring(0, b.length() - 1);
/*   67:     */   }
/*   68:     */   
/*   69:     */   protected void stringToList(String l)
/*   70:     */   {
/*   71: 111 */     if ((l != null) && (l.length() > 0))
/*   72:     */     {
/*   73: 112 */       String[] parts = l.split(",");
/*   74: 113 */       this.m_metricsList.clear();
/*   75: 114 */       for (String s : parts) {
/*   76: 115 */         this.m_metricsList.add(s.trim());
/*   77:     */       }
/*   78:     */     }
/*   79:     */   }
/*   80:     */   
/*   81:     */   public void setEvaluationMetricsToOutput(String m)
/*   82:     */   {
/*   83: 126 */     this.m_selectedEvalMetrics = m;
/*   84: 127 */     stringToList(m);
/*   85:     */   }
/*   86:     */   
/*   87:     */   public String getEvaluationMetricsToOutput()
/*   88:     */   {
/*   89: 136 */     return this.m_selectedEvalMetrics;
/*   90:     */   }
/*   91:     */   
/*   92:     */   public String evaluationMetricsToOutputTipText()
/*   93:     */   {
/*   94: 145 */     return "A comma-separated list of evaluation metrics to output";
/*   95:     */   }
/*   96:     */   
/*   97:     */   public void setErrorPlotPointSizeProportionalToMargin(boolean e)
/*   98:     */   {
/*   99: 155 */     this.m_errorPlotPointSizeProportionalToMargin = e;
/*  100:     */   }
/*  101:     */   
/*  102:     */   public boolean getErrorPlotPointSizeProportionalToMargin()
/*  103:     */   {
/*  104: 165 */     return this.m_errorPlotPointSizeProportionalToMargin;
/*  105:     */   }
/*  106:     */   
/*  107:     */   public String errorPlotPointSizeProportionalToMarginTipText()
/*  108:     */   {
/*  109: 174 */     return "Set the point size proportional to the prediction margin for classification error plots";
/*  110:     */   }
/*  111:     */   
/*  112:     */   public int getExecutionSlots()
/*  113:     */   {
/*  114: 184 */     return this.m_executionSlots;
/*  115:     */   }
/*  116:     */   
/*  117:     */   public void setExecutionSlots(int slots)
/*  118:     */   {
/*  119: 193 */     this.m_executionSlots = slots;
/*  120:     */   }
/*  121:     */   
/*  122:     */   public String executionSlotsTipText()
/*  123:     */   {
/*  124: 202 */     return "Set the number of evaluation tasks to run in parallel.";
/*  125:     */   }
/*  126:     */   
/*  127:     */   private void startExecutorPool()
/*  128:     */   {
/*  129: 207 */     if (this.m_executorPool != null) {
/*  130: 208 */       this.m_executorPool.shutdownNow();
/*  131:     */     }
/*  132: 211 */     this.m_executorPool = new ThreadPoolExecutor(this.m_executionSlots, this.m_executionSlots, 120L, TimeUnit.SECONDS, new LinkedBlockingQueue());
/*  133:     */   }
/*  134:     */   
/*  135:     */   public void setCustomName(String name)
/*  136:     */   {
/*  137: 223 */     this.m_visual.setText(name);
/*  138:     */   }
/*  139:     */   
/*  140:     */   public String getCustomName()
/*  141:     */   {
/*  142: 233 */     return this.m_visual.getText();
/*  143:     */   }
/*  144:     */   
/*  145:     */   public String globalInfo()
/*  146:     */   {
/*  147: 242 */     return "Evaluate the performance of batch trained classifiers.";
/*  148:     */   }
/*  149:     */   
/*  150: 246 */   private transient ClassifierErrorsPlotInstances m_PlotInstances = null;
/*  151:     */   
/*  152:     */   protected static Evaluation adjustForInputMappedClassifier(Evaluation eval, Classifier classifier, Instances inst, ClassifierErrorsPlotInstances plotInstances)
/*  153:     */     throws Exception
/*  154:     */   {
/*  155: 252 */     if ((classifier instanceof InputMappedClassifier))
/*  156:     */     {
/*  157: 253 */       Instances mappedClassifierHeader = ((InputMappedClassifier)classifier).getModelHeader(new Instances(inst, 0));
/*  158:     */       
/*  159:     */ 
/*  160:     */ 
/*  161: 257 */       eval = new Evaluation(new Instances(mappedClassifierHeader, 0));
/*  162: 259 */       if (!eval.getHeader().equalHeaders(inst))
/*  163:     */       {
/*  164: 265 */         Instances mappedClassifierDataset = ((InputMappedClassifier)classifier).getModelHeader(new Instances(mappedClassifierHeader, 0));
/*  165: 268 */         for (int zz = 0; zz < inst.numInstances(); zz++)
/*  166:     */         {
/*  167: 269 */           Instance mapped = ((InputMappedClassifier)classifier).constructMappedInstance(inst.instance(zz));
/*  168:     */           
/*  169:     */ 
/*  170: 272 */           mappedClassifierDataset.add(mapped);
/*  171:     */         }
/*  172: 275 */         eval.setPriors(mappedClassifierDataset);
/*  173: 276 */         plotInstances.setInstances(mappedClassifierDataset);
/*  174: 277 */         plotInstances.setClassifier(classifier);
/*  175: 278 */         plotInstances.setClassIndex(mappedClassifierDataset.classIndex());
/*  176: 279 */         plotInstances.setEvaluation(eval);
/*  177:     */       }
/*  178:     */     }
/*  179: 283 */     return eval;
/*  180:     */   }
/*  181:     */   
/*  182:     */   protected class EvaluationTask
/*  183:     */     implements Runnable, Task
/*  184:     */   {
/*  185:     */     private static final long serialVersionUID = -8939077467030259059L;
/*  186:     */     protected Instances m_testData;
/*  187:     */     protected Instances m_trainData;
/*  188:     */     protected int m_setNum;
/*  189:     */     protected int m_maxSetNum;
/*  190:     */     protected Classifier m_classifier;
/*  191:     */     protected boolean m_stopped;
/*  192: 301 */     protected String m_evalLabel = "";
/*  193:     */     
/*  194:     */     public EvaluationTask(Classifier classifier, Instances trainData, Instances testData, int setNum, int maxSetNum, String evalLabel)
/*  195:     */     {
/*  196: 316 */       this.m_classifier = classifier;
/*  197: 317 */       this.m_setNum = setNum;
/*  198: 318 */       this.m_maxSetNum = maxSetNum;
/*  199: 319 */       this.m_testData = testData;
/*  200: 320 */       this.m_trainData = trainData;
/*  201: 321 */       if (evalLabel != null) {
/*  202: 322 */         this.m_evalLabel = evalLabel;
/*  203:     */       }
/*  204:     */     }
/*  205:     */     
/*  206:     */     public void setStopped()
/*  207:     */     {
/*  208: 327 */       this.m_stopped = true;
/*  209:     */     }
/*  210:     */     
/*  211:     */     public void run()
/*  212:     */     {
/*  213: 332 */       execute();
/*  214:     */     }
/*  215:     */     
/*  216:     */     public void execute()
/*  217:     */     {
/*  218: 337 */       if (this.m_stopped) {
/*  219: 338 */         return;
/*  220:     */       }
/*  221: 341 */       if (ClassifierPerformanceEvaluator.this.m_logger != null) {
/*  222: 342 */         ClassifierPerformanceEvaluator.this.m_logger.statusMessage(ClassifierPerformanceEvaluator.this.statusMessagePrefix() + "Evaluating (" + this.m_setNum + ")...");
/*  223:     */       }
/*  224:     */       try
/*  225:     */       {
/*  226: 347 */         ClassifierErrorsPlotInstances plotInstances = ExplorerDefaults.getClassifierErrorsPlotInstances();
/*  227:     */         
/*  228: 349 */         Evaluation eval = null;
/*  229: 351 */         if ((this.m_trainData == null) || (this.m_trainData.numInstances() == 0))
/*  230:     */         {
/*  231: 352 */           eval = new Evaluation(this.m_testData);
/*  232: 353 */           plotInstances.setInstances(this.m_testData);
/*  233: 354 */           plotInstances.setClassifier(this.m_classifier);
/*  234: 355 */           plotInstances.setClassIndex(this.m_testData.classIndex());
/*  235: 356 */           plotInstances.setEvaluation(eval);
/*  236: 357 */           plotInstances.setPointSizeProportionalToMargin(ClassifierPerformanceEvaluator.this.m_errorPlotPointSizeProportionalToMargin);
/*  237:     */           
/*  238: 359 */           eval = ClassifierPerformanceEvaluator.adjustForInputMappedClassifier(eval, this.m_classifier, this.m_testData, plotInstances);
/*  239:     */           
/*  240:     */ 
/*  241:     */ 
/*  242: 363 */           eval.useNoPriors();
/*  243: 364 */           eval.setMetricsToDisplay(ClassifierPerformanceEvaluator.this.m_metricsList);
/*  244:     */         }
/*  245:     */         else
/*  246:     */         {
/*  247: 366 */           eval = new Evaluation(this.m_trainData);
/*  248: 367 */           plotInstances.setInstances(this.m_trainData);
/*  249: 368 */           plotInstances.setClassifier(this.m_classifier);
/*  250: 369 */           plotInstances.setClassIndex(this.m_trainData.classIndex());
/*  251: 370 */           plotInstances.setEvaluation(eval);
/*  252: 371 */           plotInstances.setPointSizeProportionalToMargin(ClassifierPerformanceEvaluator.this.m_errorPlotPointSizeProportionalToMargin);
/*  253:     */           
/*  254: 373 */           eval = ClassifierPerformanceEvaluator.adjustForInputMappedClassifier(eval, this.m_classifier, this.m_trainData, plotInstances);
/*  255:     */           
/*  256:     */ 
/*  257: 376 */           eval.setMetricsToDisplay(ClassifierPerformanceEvaluator.this.m_metricsList);
/*  258:     */         }
/*  259: 379 */         plotInstances.setUp();
/*  260: 381 */         if (((this.m_classifier instanceof BatchPredictor)) && (((BatchPredictor)this.m_classifier).implementsMoreEfficientBatchPrediction()))
/*  261:     */         {
/*  262: 384 */           double[][] predictions = ((BatchPredictor)this.m_classifier).distributionsForInstances(this.m_testData);
/*  263:     */           
/*  264:     */ 
/*  265: 387 */           plotInstances.process(this.m_testData, predictions, eval);
/*  266:     */         }
/*  267:     */         else
/*  268:     */         {
/*  269: 390 */           for (int i = 0; i < this.m_testData.numInstances(); i++)
/*  270:     */           {
/*  271: 391 */             if (this.m_stopped) {
/*  272:     */               break;
/*  273:     */             }
/*  274: 394 */             Instance temp = this.m_testData.instance(i);
/*  275: 395 */             plotInstances.process(temp, this.m_classifier, eval);
/*  276:     */           }
/*  277:     */         }
/*  278: 399 */         if (this.m_stopped) {
/*  279: 400 */           return;
/*  280:     */         }
/*  281: 403 */         ClassifierPerformanceEvaluator.this.aggregateEvalTask(eval, this.m_classifier, this.m_testData, plotInstances, this.m_setNum, this.m_maxSetNum, this.m_evalLabel);
/*  282:     */       }
/*  283:     */       catch (Exception ex)
/*  284:     */       {
/*  285: 407 */         ClassifierPerformanceEvaluator.this.stop();
/*  286: 408 */         if (ClassifierPerformanceEvaluator.this.m_logger != null) {
/*  287: 409 */           ClassifierPerformanceEvaluator.this.m_logger.logMessage("[ClassifierPerformanceEvaluator] " + ClassifierPerformanceEvaluator.this.statusMessagePrefix() + " problem evaluating classifier. " + ex.getMessage());
/*  288:     */         }
/*  289: 413 */         ex.printStackTrace();
/*  290:     */       }
/*  291:     */     }
/*  292:     */     
/*  293:     */     public TaskStatusInfo getTaskStatus()
/*  294:     */     {
/*  295: 420 */       return null;
/*  296:     */     }
/*  297:     */   }
/*  298:     */   
/*  299:     */   protected static class AggregateableClassifierErrorsPlotInstances
/*  300:     */     extends ClassifierErrorsPlotInstances
/*  301:     */   {
/*  302:     */     private static final long serialVersionUID = 2012744784036684168L;
/*  303:     */     
/*  304:     */     public void setPlotShapes(ArrayList<Integer> plotShapes)
/*  305:     */     {
/*  306: 445 */       this.m_PlotShapes = plotShapes;
/*  307:     */     }
/*  308:     */     
/*  309:     */     public void setPlotSizes(ArrayList<Object> plotSizes)
/*  310:     */     {
/*  311: 455 */       this.m_PlotSizes = plotSizes;
/*  312:     */     }
/*  313:     */     
/*  314:     */     public void setPlotInstances(Instances inst)
/*  315:     */     {
/*  316: 459 */       this.m_PlotInstances = inst;
/*  317:     */     }
/*  318:     */     
/*  319:     */     protected void finishUp()
/*  320:     */     {
/*  321: 464 */       this.m_FinishUpCalled = true;
/*  322: 466 */       if (!this.m_SaveForVisualization) {
/*  323: 467 */         return;
/*  324:     */       }
/*  325: 470 */       if ((this.m_Instances.classAttribute().isNumeric()) || (this.m_pointSizeProportionalToMargin)) {
/*  326: 472 */         scaleNumericPredictions();
/*  327:     */       }
/*  328:     */     }
/*  329:     */   }
/*  330:     */   
/*  331:     */   protected synchronized void aggregateEvalTask(Evaluation eval, Classifier classifier, Instances testData, ClassifierErrorsPlotInstances plotInstances, int setNum, int maxSetNum, String evalLabel)
/*  332:     */   {
/*  333: 495 */     this.m_eval.aggregate(eval);
/*  334: 497 */     if (this.m_aggregatedPlotInstances == null)
/*  335:     */     {
/*  336: 499 */       this.m_aggregatedPlotShapes = ((ArrayList)plotInstances.getPlotShapes().clone());
/*  337:     */       
/*  338: 501 */       this.m_aggregatedPlotSizes = ((ArrayList)plotInstances.getPlotSizes().clone());
/*  339:     */       
/*  340:     */ 
/*  341:     */ 
/*  342: 505 */       this.m_aggregatedPlotInstances = new Instances(plotInstances.getPlotInstances());
/*  343:     */     }
/*  344:     */     else
/*  345:     */     {
/*  346: 509 */       ArrayList<Object> tmpSizes = (ArrayList)plotInstances.getPlotSizes().clone();
/*  347:     */       
/*  348: 511 */       ArrayList<Integer> tmpShapes = (ArrayList)plotInstances.getPlotShapes().clone();
/*  349:     */       
/*  350:     */ 
/*  351: 514 */       Instances temp = plotInstances.getPlotInstances();
/*  352: 515 */       for (int i = 0; i < temp.numInstances(); i++)
/*  353:     */       {
/*  354: 516 */         this.m_aggregatedPlotInstances.add(temp.get(i));
/*  355: 517 */         this.m_aggregatedPlotShapes.add(tmpShapes.get(i));
/*  356: 518 */         this.m_aggregatedPlotSizes.add(tmpSizes.get(i));
/*  357:     */       }
/*  358:     */     }
/*  359: 521 */     this.m_setsComplete += 1;
/*  360: 523 */     if ((this.m_logger != null) && 
/*  361: 524 */       (this.m_setsComplete < maxSetNum)) {
/*  362: 525 */       this.m_logger.statusMessage(statusMessagePrefix() + "Completed (" + this.m_setsComplete + ").");
/*  363:     */     }
/*  364: 531 */     if (this.m_setsComplete == maxSetNum) {
/*  365:     */       try
/*  366:     */       {
/*  367: 533 */         AggregateableClassifierErrorsPlotInstances aggPlot = new AggregateableClassifierErrorsPlotInstances();
/*  368:     */         
/*  369: 535 */         aggPlot.setInstances(testData);
/*  370: 536 */         aggPlot.setPlotInstances(this.m_aggregatedPlotInstances);
/*  371: 537 */         aggPlot.setPlotShapes(this.m_aggregatedPlotShapes);
/*  372: 538 */         aggPlot.setPlotSizes(this.m_aggregatedPlotSizes);
/*  373: 539 */         aggPlot.setPointSizeProportionalToMargin(this.m_errorPlotPointSizeProportionalToMargin);
/*  374:     */         
/*  375:     */ 
/*  376:     */ 
/*  377: 543 */         aggPlot.getPlotInstances();
/*  378:     */         
/*  379: 545 */         String textTitle = "";
/*  380: 546 */         textTitle = textTitle + classifier.getClass().getName();
/*  381: 547 */         String textOptions = "";
/*  382: 548 */         if ((classifier instanceof OptionHandler)) {
/*  383: 549 */           textOptions = Utils.joinOptions(((OptionHandler)classifier).getOptions());
/*  384:     */         }
/*  385: 552 */         textTitle = textTitle.substring(textTitle.lastIndexOf('.') + 1, textTitle.length());
/*  386: 555 */         if ((evalLabel != null) && (evalLabel.length() > 0) && 
/*  387: 556 */           (!textTitle.toLowerCase().startsWith(evalLabel.toLowerCase()))) {
/*  388: 557 */           textTitle = evalLabel + " : " + textTitle;
/*  389:     */         }
/*  390: 560 */         String resultT = "=== Evaluation result ===\n\nScheme: " + textTitle + "\n" + (textOptions.length() > 0 ? "Options: " + textOptions + "\n" : "") + "Relation: " + testData.relationName() + "\n\n" + this.m_eval.toSummaryString();
/*  391: 569 */         if (testData.classAttribute().isNominal()) {
/*  392: 570 */           resultT = resultT + "\n" + this.m_eval.toClassDetailsString() + "\n" + this.m_eval.toMatrixString();
/*  393:     */         }
/*  394: 575 */         TextEvent te = new TextEvent(this, resultT, textTitle);
/*  395:     */         
/*  396: 577 */         notifyTextListeners(te);
/*  397: 580 */         if (this.m_visualizableErrorListeners.size() > 0)
/*  398:     */         {
/*  399: 581 */           PlotData2D errorD = new PlotData2D(this.m_aggregatedPlotInstances);
/*  400: 582 */           errorD.setShapeSize(this.m_aggregatedPlotSizes);
/*  401: 583 */           errorD.setShapeType(this.m_aggregatedPlotShapes);
/*  402: 584 */           errorD.setPlotName(textTitle + " " + textOptions);
/*  403:     */           
/*  404:     */ 
/*  405:     */ 
/*  406:     */ 
/*  407:     */ 
/*  408: 590 */           VisualizableErrorEvent vel = new VisualizableErrorEvent(this, errorD);
/*  409:     */           
/*  410:     */ 
/*  411: 593 */           notifyVisualizableErrorListeners(vel);
/*  412: 594 */           this.m_PlotInstances.cleanUp();
/*  413:     */         }
/*  414: 597 */         if ((testData.classAttribute().isNominal()) && (this.m_thresholdListeners.size() > 0))
/*  415:     */         {
/*  416: 599 */           ThresholdCurve tc = new ThresholdCurve();
/*  417: 600 */           Instances result = tc.getCurve(this.m_eval.predictions(), 0);
/*  418: 601 */           result.setRelationName(testData.relationName());
/*  419: 602 */           PlotData2D pd = new PlotData2D(result);
/*  420: 603 */           String htmlTitle = "<html><font size=-2>" + textTitle;
/*  421: 604 */           String newOptions = "";
/*  422: 605 */           if ((classifier instanceof OptionHandler))
/*  423:     */           {
/*  424: 606 */             String[] options = ((OptionHandler)classifier).getOptions();
/*  425: 607 */             if (options.length > 0) {
/*  426: 608 */               for (int ii = 0; ii < options.length; ii++) {
/*  427: 609 */                 if (options[ii].length() != 0)
/*  428:     */                 {
/*  429: 612 */                   if ((options[ii].charAt(0) == '-') && ((options[ii].charAt(1) < '0') || (options[ii].charAt(1) > '9'))) {
/*  430: 614 */                     newOptions = newOptions + "<br>";
/*  431:     */                   }
/*  432: 616 */                   newOptions = newOptions + options[ii];
/*  433:     */                 }
/*  434:     */               }
/*  435:     */             }
/*  436:     */           }
/*  437: 621 */           htmlTitle = htmlTitle + " " + newOptions + "<br>" + " (class: " + testData.classAttribute().value(0) + ")" + "</font></html>";
/*  438:     */           
/*  439:     */ 
/*  440: 624 */           pd.setPlotName(textTitle + " (class: " + testData.classAttribute().value(0) + ")");
/*  441:     */           
/*  442: 626 */           pd.setPlotNameHTML(htmlTitle);
/*  443: 627 */           boolean[] connectPoints = new boolean[result.numInstances()];
/*  444: 628 */           for (int jj = 1; jj < connectPoints.length; jj++) {
/*  445: 629 */             connectPoints[jj] = true;
/*  446:     */           }
/*  447: 632 */           pd.setConnectPoints(connectPoints);
/*  448:     */           
/*  449: 634 */           ThresholdDataEvent rde = new ThresholdDataEvent(this, pd, testData.classAttribute());
/*  450:     */           
/*  451:     */ 
/*  452: 637 */           notifyThresholdListeners(rde);
/*  453:     */         }
/*  454: 639 */         if (this.m_logger != null) {
/*  455: 640 */           this.m_logger.statusMessage(statusMessagePrefix() + "Finished.");
/*  456:     */         }
/*  457:     */       }
/*  458:     */       catch (Exception ex)
/*  459:     */       {
/*  460: 644 */         if (this.m_logger != null) {
/*  461: 645 */           this.m_logger.logMessage("[ClassifierPerformanceEvaluator] " + statusMessagePrefix() + " problem constructing evaluation results. " + ex.getMessage());
/*  462:     */         }
/*  463: 649 */         ex.printStackTrace();
/*  464:     */       }
/*  465:     */       finally
/*  466:     */       {
/*  467: 651 */         this.m_visual.setStatic();
/*  468:     */         
/*  469: 653 */         this.m_PlotInstances = null;
/*  470: 654 */         this.m_setsComplete = 0;
/*  471: 655 */         this.m_tasks = null;
/*  472: 656 */         this.m_aggregatedPlotInstances = null;
/*  473:     */       }
/*  474:     */     }
/*  475:     */   }
/*  476:     */   
/*  477:     */   public void acceptClassifier(BatchClassifierEvent ce)
/*  478:     */   {
/*  479: 668 */     if ((ce.getTestSet() == null) || (ce.getTestSet().isStructureOnly())) {
/*  480: 669 */       return;
/*  481:     */     }
/*  482: 672 */     Classifier classifier = ce.getClassifier();
/*  483:     */     try
/*  484:     */     {
/*  485: 675 */       if (ce.getGroupIdentifier() != this.m_currentBatchIdentifier)
/*  486:     */       {
/*  487: 676 */         if (this.m_setsComplete > 0)
/*  488:     */         {
/*  489: 677 */           if (this.m_logger != null)
/*  490:     */           {
/*  491: 678 */             this.m_logger.statusMessage(statusMessagePrefix() + "BUSY. Can't accept data " + "at this time.");
/*  492:     */             
/*  493: 680 */             this.m_logger.logMessage("[ClassifierPerformanceEvaluator] " + statusMessagePrefix() + " BUSY. Can't accept data at this time.");
/*  494:     */           }
/*  495: 684 */           return;
/*  496:     */         }
/*  497: 686 */         if ((ce.getTrainSet().getDataSet() == null) || (ce.getTrainSet().getDataSet().numInstances() == 0))
/*  498:     */         {
/*  499: 690 */           Evaluation eval = new Evaluation(ce.getTestSet().getDataSet());
/*  500: 691 */           this.m_PlotInstances = ExplorerDefaults.getClassifierErrorsPlotInstances();
/*  501: 692 */           this.m_PlotInstances.setInstances(ce.getTestSet().getDataSet());
/*  502: 693 */           this.m_PlotInstances.setClassifier(ce.getClassifier());
/*  503: 694 */           this.m_PlotInstances.setClassIndex(ce.getTestSet().getDataSet().classIndex());
/*  504:     */           
/*  505: 696 */           this.m_PlotInstances.setEvaluation(eval);
/*  506:     */           
/*  507: 698 */           eval = adjustForInputMappedClassifier(eval, ce.getClassifier(), ce.getTestSet().getDataSet(), this.m_PlotInstances);
/*  508:     */           
/*  509:     */ 
/*  510: 701 */           eval.useNoPriors();
/*  511: 702 */           this.m_eval = new AggregateableEvaluation(eval);
/*  512: 703 */           this.m_eval.setMetricsToDisplay(this.m_metricsList);
/*  513:     */         }
/*  514:     */         else
/*  515:     */         {
/*  516: 706 */           Evaluation eval = new Evaluation(ce.getTrainSet().getDataSet());
/*  517: 707 */           this.m_PlotInstances = ExplorerDefaults.getClassifierErrorsPlotInstances();
/*  518: 708 */           this.m_PlotInstances.setInstances(ce.getTrainSet().getDataSet());
/*  519: 709 */           this.m_PlotInstances.setClassifier(ce.getClassifier());
/*  520: 710 */           this.m_PlotInstances.setClassIndex(ce.getTestSet().getDataSet().classIndex());
/*  521:     */           
/*  522: 712 */           this.m_PlotInstances.setEvaluation(eval);
/*  523:     */           
/*  524: 714 */           eval = adjustForInputMappedClassifier(eval, ce.getClassifier(), ce.getTrainSet().getDataSet(), this.m_PlotInstances);
/*  525:     */           
/*  526:     */ 
/*  527: 717 */           this.m_eval = new AggregateableEvaluation(eval);
/*  528: 718 */           this.m_eval.setMetricsToDisplay(this.m_metricsList);
/*  529:     */         }
/*  530: 721 */         this.m_PlotInstances.setUp();
/*  531:     */         
/*  532: 723 */         this.m_currentBatchIdentifier = ce.getGroupIdentifier();
/*  533: 724 */         this.m_setsComplete = 0;
/*  534:     */         
/*  535: 726 */         this.m_aggregatedPlotInstances = null;
/*  536:     */         
/*  537: 728 */         String msg = "[ClassifierPerformanceEvaluator] " + statusMessagePrefix() + " starting executor pool (" + getExecutionSlots() + " slots)...";
/*  538:     */         
/*  539:     */ 
/*  540:     */ 
/*  541:     */ 
/*  542: 733 */         startExecutorPool();
/*  543:     */         
/*  544: 735 */         this.m_tasks = new ArrayList();
/*  545: 737 */         if (this.m_logger != null) {
/*  546: 738 */           this.m_logger.logMessage(msg);
/*  547:     */         } else {
/*  548: 740 */           System.out.println(msg);
/*  549:     */         }
/*  550:     */       }
/*  551: 745 */       if ((this.m_setsComplete < ce.getMaxSetNumber()) && (this.m_tasks != null))
/*  552:     */       {
/*  553: 746 */         EvaluationTask newTask = new EvaluationTask(classifier, ce.getTrainSet().getDataSet(), ce.getTestSet().getDataSet(), ce.getSetNumber(), ce.getMaxSetNumber(), ce.getLabel());
/*  554:     */         
/*  555:     */ 
/*  556:     */ 
/*  557: 750 */         String msg = "[ClassifierPerformanceEvaluator] " + statusMessagePrefix() + " scheduling " + " evaluation of fold " + ce.getSetNumber() + " for execution...";
/*  558: 754 */         if (this.m_logger != null) {
/*  559: 755 */           this.m_logger.logMessage(msg);
/*  560:     */         } else {
/*  561: 757 */           System.out.println(msg);
/*  562:     */         }
/*  563: 760 */         this.m_tasks.add(newTask);
/*  564: 761 */         this.m_executorPool.execute(newTask);
/*  565:     */       }
/*  566:     */     }
/*  567:     */     catch (Exception ex)
/*  568:     */     {
/*  569: 764 */       ex.printStackTrace();
/*  570:     */       
/*  571: 766 */       stop();
/*  572:     */     }
/*  573:     */   }
/*  574:     */   
/*  575:     */   public boolean isBusy()
/*  576:     */   {
/*  577: 779 */     if ((this.m_executorPool == null) || ((this.m_executorPool.getQueue().size() == 0) && (this.m_executorPool.getActiveCount() == 0) && (this.m_setsComplete == 0))) {
/*  578: 782 */       return false;
/*  579:     */     }
/*  580: 785 */     return true;
/*  581:     */   }
/*  582:     */   
/*  583:     */   public void stop()
/*  584:     */   {
/*  585: 795 */     if ((this.m_listenee instanceof BeanCommon)) {
/*  586: 797 */       ((BeanCommon)this.m_listenee).stop();
/*  587:     */     }
/*  588: 800 */     if (this.m_tasks != null) {
/*  589: 801 */       for (EvaluationTask t : this.m_tasks) {
/*  590: 802 */         t.setStopped();
/*  591:     */       }
/*  592:     */     }
/*  593: 805 */     this.m_tasks = null;
/*  594: 806 */     this.m_visual.setStatic();
/*  595: 807 */     this.m_setsComplete = 0;
/*  596: 810 */     if (this.m_executorPool != null)
/*  597:     */     {
/*  598: 811 */       this.m_executorPool.shutdownNow();
/*  599: 812 */       this.m_executorPool.purge();
/*  600: 813 */       this.m_executorPool = null;
/*  601:     */     }
/*  602:     */   }
/*  603:     */   
/*  604:     */   public Enumeration<String> enumerateRequests()
/*  605:     */   {
/*  606: 842 */     Vector<String> newVector = new Vector(0);
/*  607: 846 */     if ((this.m_executorPool != null) && ((this.m_executorPool.getQueue().size() > 0) || (this.m_executorPool.getActiveCount() > 0))) {
/*  608: 849 */       newVector.addElement("Stop");
/*  609:     */     }
/*  610: 852 */     return newVector.elements();
/*  611:     */   }
/*  612:     */   
/*  613:     */   public void performRequest(String request)
/*  614:     */   {
/*  615: 863 */     if (request.compareTo("Stop") == 0) {
/*  616: 864 */       stop();
/*  617:     */     } else {
/*  618: 866 */       throw new IllegalArgumentException(request + " not supported (ClassifierPerformanceEvaluator)");
/*  619:     */     }
/*  620:     */   }
/*  621:     */   
/*  622:     */   public synchronized void addTextListener(TextListener cl)
/*  623:     */   {
/*  624: 878 */     this.m_textListeners.addElement(cl);
/*  625:     */   }
/*  626:     */   
/*  627:     */   public synchronized void removeTextListener(TextListener cl)
/*  628:     */   {
/*  629: 887 */     this.m_textListeners.remove(cl);
/*  630:     */   }
/*  631:     */   
/*  632:     */   public synchronized void addThresholdDataListener(ThresholdDataListener cl)
/*  633:     */   {
/*  634: 896 */     this.m_thresholdListeners.addElement(cl);
/*  635:     */   }
/*  636:     */   
/*  637:     */   public synchronized void removeThresholdDataListener(ThresholdDataListener cl)
/*  638:     */   {
/*  639: 906 */     this.m_thresholdListeners.remove(cl);
/*  640:     */   }
/*  641:     */   
/*  642:     */   public synchronized void addVisualizableErrorListener(VisualizableErrorListener vel)
/*  643:     */   {
/*  644: 916 */     this.m_visualizableErrorListeners.add(vel);
/*  645:     */   }
/*  646:     */   
/*  647:     */   public synchronized void removeVisualizableErrorListener(VisualizableErrorListener vel)
/*  648:     */   {
/*  649: 926 */     this.m_visualizableErrorListeners.remove(vel);
/*  650:     */   }
/*  651:     */   
/*  652:     */   private void notifyTextListeners(TextEvent te)
/*  653:     */   {
/*  654:     */     Vector<TextListener> l;
/*  655: 937 */     synchronized (this)
/*  656:     */     {
/*  657: 938 */       l = (Vector)this.m_textListeners.clone();
/*  658:     */     }
/*  659: 940 */     if (l.size() > 0) {
/*  660: 941 */       for (int i = 0; i < l.size(); i++) {
/*  661: 944 */         ((TextListener)l.elementAt(i)).acceptText(te);
/*  662:     */       }
/*  663:     */     }
/*  664:     */   }
/*  665:     */   
/*  666:     */   private void notifyThresholdListeners(ThresholdDataEvent re)
/*  667:     */   {
/*  668:     */     Vector<ThresholdDataListener> l;
/*  669: 957 */     synchronized (this)
/*  670:     */     {
/*  671: 958 */       l = (Vector)this.m_thresholdListeners.clone();
/*  672:     */     }
/*  673: 960 */     if (l.size() > 0) {
/*  674: 961 */       for (int i = 0; i < l.size(); i++) {
/*  675: 964 */         ((ThresholdDataListener)l.elementAt(i)).acceptDataSet(re);
/*  676:     */       }
/*  677:     */     }
/*  678:     */   }
/*  679:     */   
/*  680:     */   private void notifyVisualizableErrorListeners(VisualizableErrorEvent re)
/*  681:     */   {
/*  682:     */     Vector<VisualizableErrorListener> l;
/*  683: 977 */     synchronized (this)
/*  684:     */     {
/*  685: 978 */       l = (Vector)this.m_visualizableErrorListeners.clone();
/*  686:     */     }
/*  687: 982 */     if (l.size() > 0) {
/*  688: 983 */       for (int i = 0; i < l.size(); i++) {
/*  689: 986 */         ((VisualizableErrorListener)l.elementAt(i)).acceptDataSet(re);
/*  690:     */       }
/*  691:     */     }
/*  692:     */   }
/*  693:     */   
/*  694:     */   public boolean eventGeneratable(String eventName)
/*  695:     */   {
/*  696:1001 */     if (this.m_listenee == null) {
/*  697:1002 */       return false;
/*  698:     */     }
/*  699:1005 */     if (((this.m_listenee instanceof EventConstraints)) && 
/*  700:1006 */       (!((EventConstraints)this.m_listenee).eventGeneratable("batchClassifier"))) {
/*  701:1007 */       return false;
/*  702:     */     }
/*  703:1010 */     return true;
/*  704:     */   }
/*  705:     */   
/*  706:     */   private String statusMessagePrefix()
/*  707:     */   {
/*  708:1014 */     return getCustomName() + "$" + hashCode() + "|";
/*  709:     */   }
/*  710:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ClassifierPerformanceEvaluator
 * JD-Core Version:    0.7.0.1
 */