/*    1:     */ package weka.classifiers;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.ArrayList;
/*    6:     */ import java.util.List;
/*    7:     */ import java.util.Random;
/*    8:     */ import weka.classifiers.evaluation.AbstractEvaluationMetric;
/*    9:     */ import weka.classifiers.evaluation.Prediction;
/*   10:     */ import weka.core.Instance;
/*   11:     */ import weka.core.Instances;
/*   12:     */ import weka.core.RevisionHandler;
/*   13:     */ import weka.core.Summarizable;
/*   14:     */ 
/*   15:     */ public class Evaluation
/*   16:     */   implements Serializable, Summarizable, RevisionHandler
/*   17:     */ {
/*   18:     */   private static final long serialVersionUID = -170766452472965668L;
/*   19: 195 */   public static final String[] BUILT_IN_EVAL_METRICS = weka.classifiers.evaluation.Evaluation.BUILT_IN_EVAL_METRICS;
/*   20:     */   protected weka.classifiers.evaluation.Evaluation m_delegate;
/*   21:     */   
/*   22:     */   public static List<String> getAllEvaluationMetricNames()
/*   23:     */   {
/*   24: 207 */     return weka.classifiers.evaluation.Evaluation.getAllEvaluationMetricNames();
/*   25:     */   }
/*   26:     */   
/*   27:     */   public Evaluation(Instances data)
/*   28:     */     throws Exception
/*   29:     */   {
/*   30: 211 */     this.m_delegate = new weka.classifiers.evaluation.Evaluation(data);
/*   31:     */   }
/*   32:     */   
/*   33:     */   public Evaluation(Instances data, CostMatrix costMatrix)
/*   34:     */     throws Exception
/*   35:     */   {
/*   36: 215 */     this.m_delegate = new weka.classifiers.evaluation.Evaluation(data, costMatrix);
/*   37:     */   }
/*   38:     */   
/*   39:     */   public Instances getHeader()
/*   40:     */   {
/*   41: 224 */     return this.m_delegate.getHeader();
/*   42:     */   }
/*   43:     */   
/*   44:     */   public List<AbstractEvaluationMetric> getPluginMetrics()
/*   45:     */   {
/*   46: 233 */     return this.m_delegate.getPluginMetrics();
/*   47:     */   }
/*   48:     */   
/*   49:     */   public AbstractEvaluationMetric getPluginMetric(String name)
/*   50:     */   {
/*   51: 247 */     return this.m_delegate.getPluginMetric(name);
/*   52:     */   }
/*   53:     */   
/*   54:     */   public void setMetricsToDisplay(List<String> display)
/*   55:     */   {
/*   56: 258 */     this.m_delegate.setMetricsToDisplay(display);
/*   57:     */   }
/*   58:     */   
/*   59:     */   public List<String> getMetricsToDisplay()
/*   60:     */   {
/*   61: 269 */     return this.m_delegate.getMetricsToDisplay();
/*   62:     */   }
/*   63:     */   
/*   64:     */   public void toggleEvalMetrics(List<String> metricsToToggle)
/*   65:     */   {
/*   66: 278 */     this.m_delegate.toggleEvalMetrics(metricsToToggle);
/*   67:     */   }
/*   68:     */   
/*   69:     */   public void setDiscardPredictions(boolean value)
/*   70:     */   {
/*   71: 289 */     this.m_delegate.setDiscardPredictions(value);
/*   72:     */   }
/*   73:     */   
/*   74:     */   public boolean getDiscardPredictions()
/*   75:     */   {
/*   76: 300 */     return this.m_delegate.getDiscardPredictions();
/*   77:     */   }
/*   78:     */   
/*   79:     */   public double areaUnderROC(int classIndex)
/*   80:     */   {
/*   81: 312 */     return this.m_delegate.areaUnderROC(classIndex);
/*   82:     */   }
/*   83:     */   
/*   84:     */   public double weightedAreaUnderROC()
/*   85:     */   {
/*   86: 321 */     return this.m_delegate.weightedAreaUnderROC();
/*   87:     */   }
/*   88:     */   
/*   89:     */   public double areaUnderPRC(int classIndex)
/*   90:     */   {
/*   91: 333 */     return this.m_delegate.areaUnderPRC(classIndex);
/*   92:     */   }
/*   93:     */   
/*   94:     */   public double weightedAreaUnderPRC()
/*   95:     */   {
/*   96: 342 */     return this.m_delegate.weightedAreaUnderPRC();
/*   97:     */   }
/*   98:     */   
/*   99:     */   public double[][] confusionMatrix()
/*  100:     */   {
/*  101: 351 */     return this.m_delegate.confusionMatrix();
/*  102:     */   }
/*  103:     */   
/*  104:     */   public void crossValidateModel(Classifier classifier, Instances data, int numFolds, Random random, Object... forPredictionsPrinting)
/*  105:     */     throws Exception
/*  106:     */   {
/*  107: 374 */     this.m_delegate.crossValidateModel(classifier, data, numFolds, random, forPredictionsPrinting);
/*  108:     */   }
/*  109:     */   
/*  110:     */   public void crossValidateModel(String classifierString, Instances data, int numFolds, String[] options, Random random)
/*  111:     */     throws Exception
/*  112:     */   {
/*  113: 393 */     this.m_delegate.crossValidateModel(classifierString, data, numFolds, options, random);
/*  114:     */   }
/*  115:     */   
/*  116:     */   public static String evaluateModel(String classifierString, String[] options)
/*  117:     */     throws Exception
/*  118:     */   {
/*  119: 527 */     return weka.classifiers.evaluation.Evaluation.evaluateModel(classifierString, options);
/*  120:     */   }
/*  121:     */   
/*  122:     */   public static String evaluateModel(Classifier classifier, String[] options)
/*  123:     */     throws Exception
/*  124:     */   {
/*  125: 650 */     return weka.classifiers.evaluation.Evaluation.evaluateModel(classifier, options);
/*  126:     */   }
/*  127:     */   
/*  128:     */   public double[] evaluateModel(Classifier classifier, Instances data, Object... forPredictionsPrinting)
/*  129:     */     throws Exception
/*  130:     */   {
/*  131: 671 */     return this.m_delegate.evaluateModel(classifier, data, forPredictionsPrinting);
/*  132:     */   }
/*  133:     */   
/*  134:     */   public double evaluationForSingleInstance(double[] dist, Instance instance, boolean storePredictions)
/*  135:     */     throws Exception
/*  136:     */   {
/*  137: 685 */     return this.m_delegate.evaluationForSingleInstance(dist, instance, storePredictions);
/*  138:     */   }
/*  139:     */   
/*  140:     */   public double evaluateModelOnceAndRecordPrediction(Classifier classifier, Instance instance)
/*  141:     */     throws Exception
/*  142:     */   {
/*  143: 700 */     return this.m_delegate.evaluateModelOnceAndRecordPrediction(classifier, instance);
/*  144:     */   }
/*  145:     */   
/*  146:     */   public double evaluateModelOnce(Classifier classifier, Instance instance)
/*  147:     */     throws Exception
/*  148:     */   {
/*  149: 715 */     return this.m_delegate.evaluateModelOnce(classifier, instance);
/*  150:     */   }
/*  151:     */   
/*  152:     */   public double evaluateModelOnce(double[] dist, Instance instance)
/*  153:     */     throws Exception
/*  154:     */   {
/*  155: 728 */     return this.m_delegate.evaluateModelOnce(dist, instance);
/*  156:     */   }
/*  157:     */   
/*  158:     */   public double evaluateModelOnceAndRecordPrediction(double[] dist, Instance instance)
/*  159:     */     throws Exception
/*  160:     */   {
/*  161: 741 */     return this.m_delegate.evaluateModelOnceAndRecordPrediction(dist, instance);
/*  162:     */   }
/*  163:     */   
/*  164:     */   public void evaluateModelOnce(double prediction, Instance instance)
/*  165:     */     throws Exception
/*  166:     */   {
/*  167: 753 */     this.m_delegate.evaluateModelOnce(prediction, instance);
/*  168:     */   }
/*  169:     */   
/*  170:     */   public ArrayList<Prediction> predictions()
/*  171:     */   {
/*  172: 764 */     return this.m_delegate.predictions();
/*  173:     */   }
/*  174:     */   
/*  175:     */   public static String wekaStaticWrapper(Sourcable classifier, String className)
/*  176:     */     throws Exception
/*  177:     */   {
/*  178: 779 */     return weka.classifiers.evaluation.Evaluation.wekaStaticWrapper(classifier, className);
/*  179:     */   }
/*  180:     */   
/*  181:     */   public final double numInstances()
/*  182:     */   {
/*  183: 790 */     return this.m_delegate.numInstances();
/*  184:     */   }
/*  185:     */   
/*  186:     */   public final double coverageOfTestCasesByPredictedRegions()
/*  187:     */   {
/*  188: 800 */     return this.m_delegate.coverageOfTestCasesByPredictedRegions();
/*  189:     */   }
/*  190:     */   
/*  191:     */   public final double sizeOfPredictedRegions()
/*  192:     */   {
/*  193: 811 */     return this.m_delegate.sizeOfPredictedRegions();
/*  194:     */   }
/*  195:     */   
/*  196:     */   public final double incorrect()
/*  197:     */   {
/*  198: 822 */     return this.m_delegate.incorrect();
/*  199:     */   }
/*  200:     */   
/*  201:     */   public final double pctIncorrect()
/*  202:     */   {
/*  203: 832 */     return this.m_delegate.pctIncorrect();
/*  204:     */   }
/*  205:     */   
/*  206:     */   public final double totalCost()
/*  207:     */   {
/*  208: 842 */     return this.m_delegate.totalCost();
/*  209:     */   }
/*  210:     */   
/*  211:     */   public final double avgCost()
/*  212:     */   {
/*  213: 852 */     return this.m_delegate.avgCost();
/*  214:     */   }
/*  215:     */   
/*  216:     */   public final double correct()
/*  217:     */   {
/*  218: 863 */     return this.m_delegate.correct();
/*  219:     */   }
/*  220:     */   
/*  221:     */   public final double pctCorrect()
/*  222:     */   {
/*  223: 873 */     return this.m_delegate.pctCorrect();
/*  224:     */   }
/*  225:     */   
/*  226:     */   public final double unclassified()
/*  227:     */   {
/*  228: 884 */     return this.m_delegate.unclassified();
/*  229:     */   }
/*  230:     */   
/*  231:     */   public final double pctUnclassified()
/*  232:     */   {
/*  233: 894 */     return this.m_delegate.pctUnclassified();
/*  234:     */   }
/*  235:     */   
/*  236:     */   public final double errorRate()
/*  237:     */   {
/*  238: 906 */     return this.m_delegate.errorRate();
/*  239:     */   }
/*  240:     */   
/*  241:     */   public final double kappa()
/*  242:     */   {
/*  243: 915 */     return this.m_delegate.kappa();
/*  244:     */   }
/*  245:     */   
/*  246:     */   public String getRevision()
/*  247:     */   {
/*  248: 920 */     return this.m_delegate.getRevision();
/*  249:     */   }
/*  250:     */   
/*  251:     */   public final double correlationCoefficient()
/*  252:     */     throws Exception
/*  253:     */   {
/*  254: 930 */     return this.m_delegate.correlationCoefficient();
/*  255:     */   }
/*  256:     */   
/*  257:     */   public final double meanAbsoluteError()
/*  258:     */   {
/*  259: 941 */     return this.m_delegate.meanAbsoluteError();
/*  260:     */   }
/*  261:     */   
/*  262:     */   public final double meanPriorAbsoluteError()
/*  263:     */   {
/*  264: 950 */     return this.m_delegate.meanPriorAbsoluteError();
/*  265:     */   }
/*  266:     */   
/*  267:     */   public final double relativeAbsoluteError()
/*  268:     */     throws Exception
/*  269:     */   {
/*  270: 960 */     return this.m_delegate.relativeAbsoluteError();
/*  271:     */   }
/*  272:     */   
/*  273:     */   public final double rootMeanSquaredError()
/*  274:     */   {
/*  275: 969 */     return this.m_delegate.rootMeanSquaredError();
/*  276:     */   }
/*  277:     */   
/*  278:     */   public final double rootMeanPriorSquaredError()
/*  279:     */   {
/*  280: 978 */     return this.m_delegate.rootMeanPriorSquaredError();
/*  281:     */   }
/*  282:     */   
/*  283:     */   public final double rootRelativeSquaredError()
/*  284:     */   {
/*  285: 987 */     return this.m_delegate.rootRelativeSquaredError();
/*  286:     */   }
/*  287:     */   
/*  288:     */   public final double priorEntropy()
/*  289:     */     throws Exception
/*  290:     */   {
/*  291: 997 */     return this.m_delegate.priorEntropy();
/*  292:     */   }
/*  293:     */   
/*  294:     */   public final double KBInformation()
/*  295:     */     throws Exception
/*  296:     */   {
/*  297:1007 */     return this.m_delegate.KBInformation();
/*  298:     */   }
/*  299:     */   
/*  300:     */   public final double KBMeanInformation()
/*  301:     */     throws Exception
/*  302:     */   {
/*  303:1017 */     return this.m_delegate.KBMeanInformation();
/*  304:     */   }
/*  305:     */   
/*  306:     */   public final double KBRelativeInformation()
/*  307:     */     throws Exception
/*  308:     */   {
/*  309:1027 */     return this.m_delegate.KBRelativeInformation();
/*  310:     */   }
/*  311:     */   
/*  312:     */   public final double SFPriorEntropy()
/*  313:     */   {
/*  314:1036 */     return this.m_delegate.SFPriorEntropy();
/*  315:     */   }
/*  316:     */   
/*  317:     */   public final double SFMeanPriorEntropy()
/*  318:     */   {
/*  319:1045 */     return this.m_delegate.SFMeanPriorEntropy();
/*  320:     */   }
/*  321:     */   
/*  322:     */   public final double SFSchemeEntropy()
/*  323:     */   {
/*  324:1054 */     return this.m_delegate.SFSchemeEntropy();
/*  325:     */   }
/*  326:     */   
/*  327:     */   public final double SFMeanSchemeEntropy()
/*  328:     */   {
/*  329:1063 */     return this.m_delegate.SFMeanSchemeEntropy();
/*  330:     */   }
/*  331:     */   
/*  332:     */   public final double SFEntropyGain()
/*  333:     */   {
/*  334:1073 */     return this.m_delegate.SFEntropyGain();
/*  335:     */   }
/*  336:     */   
/*  337:     */   public final double SFMeanEntropyGain()
/*  338:     */   {
/*  339:1083 */     return this.m_delegate.SFMeanEntropyGain();
/*  340:     */   }
/*  341:     */   
/*  342:     */   public String toCumulativeMarginDistributionString()
/*  343:     */     throws Exception
/*  344:     */   {
/*  345:1094 */     return this.m_delegate.toCumulativeMarginDistributionString();
/*  346:     */   }
/*  347:     */   
/*  348:     */   public String toSummaryString()
/*  349:     */   {
/*  350:1104 */     return this.m_delegate.toSummaryString();
/*  351:     */   }
/*  352:     */   
/*  353:     */   public String toSummaryString(boolean printComplexityStatistics)
/*  354:     */   {
/*  355:1115 */     return this.m_delegate.toSummaryString(printComplexityStatistics);
/*  356:     */   }
/*  357:     */   
/*  358:     */   public String toSummaryString(String title, boolean printComplexityStatistics)
/*  359:     */   {
/*  360:1130 */     return this.m_delegate.toSummaryString(title, printComplexityStatistics);
/*  361:     */   }
/*  362:     */   
/*  363:     */   public String toMatrixString()
/*  364:     */     throws Exception
/*  365:     */   {
/*  366:1140 */     return this.m_delegate.toMatrixString();
/*  367:     */   }
/*  368:     */   
/*  369:     */   public String toMatrixString(String title)
/*  370:     */     throws Exception
/*  371:     */   {
/*  372:1152 */     return this.m_delegate.toMatrixString(title);
/*  373:     */   }
/*  374:     */   
/*  375:     */   public String toClassDetailsString()
/*  376:     */     throws Exception
/*  377:     */   {
/*  378:1165 */     return this.m_delegate.toClassDetailsString();
/*  379:     */   }
/*  380:     */   
/*  381:     */   public String toClassDetailsString(String title)
/*  382:     */     throws Exception
/*  383:     */   {
/*  384:1179 */     return this.m_delegate.toClassDetailsString(title);
/*  385:     */   }
/*  386:     */   
/*  387:     */   public double numTruePositives(int classIndex)
/*  388:     */   {
/*  389:1195 */     return this.m_delegate.numTruePositives(classIndex);
/*  390:     */   }
/*  391:     */   
/*  392:     */   public double truePositiveRate(int classIndex)
/*  393:     */   {
/*  394:1213 */     return this.m_delegate.truePositiveRate(classIndex);
/*  395:     */   }
/*  396:     */   
/*  397:     */   public double weightedTruePositiveRate()
/*  398:     */   {
/*  399:1222 */     return this.m_delegate.weightedTruePositiveRate();
/*  400:     */   }
/*  401:     */   
/*  402:     */   public double numTrueNegatives(int classIndex)
/*  403:     */   {
/*  404:1238 */     return this.m_delegate.numTrueNegatives(classIndex);
/*  405:     */   }
/*  406:     */   
/*  407:     */   public double trueNegativeRate(int classIndex)
/*  408:     */   {
/*  409:1256 */     return this.m_delegate.trueNegativeRate(classIndex);
/*  410:     */   }
/*  411:     */   
/*  412:     */   public double weightedTrueNegativeRate()
/*  413:     */   {
/*  414:1265 */     return this.m_delegate.weightedTrueNegativeRate();
/*  415:     */   }
/*  416:     */   
/*  417:     */   public double numFalsePositives(int classIndex)
/*  418:     */   {
/*  419:1281 */     return this.m_delegate.numFalsePositives(classIndex);
/*  420:     */   }
/*  421:     */   
/*  422:     */   public double falsePositiveRate(int classIndex)
/*  423:     */   {
/*  424:1299 */     return this.m_delegate.falsePositiveRate(classIndex);
/*  425:     */   }
/*  426:     */   
/*  427:     */   public double weightedFalsePositiveRate()
/*  428:     */   {
/*  429:1308 */     return this.m_delegate.weightedFalsePositiveRate();
/*  430:     */   }
/*  431:     */   
/*  432:     */   public double numFalseNegatives(int classIndex)
/*  433:     */   {
/*  434:1324 */     return this.m_delegate.numFalseNegatives(classIndex);
/*  435:     */   }
/*  436:     */   
/*  437:     */   public double falseNegativeRate(int classIndex)
/*  438:     */   {
/*  439:1342 */     return this.m_delegate.falseNegativeRate(classIndex);
/*  440:     */   }
/*  441:     */   
/*  442:     */   public double weightedFalseNegativeRate()
/*  443:     */   {
/*  444:1351 */     return this.m_delegate.weightedFalseNegativeRate();
/*  445:     */   }
/*  446:     */   
/*  447:     */   public double matthewsCorrelationCoefficient(int classIndex)
/*  448:     */   {
/*  449:1364 */     return this.m_delegate.matthewsCorrelationCoefficient(classIndex);
/*  450:     */   }
/*  451:     */   
/*  452:     */   public double weightedMatthewsCorrelation()
/*  453:     */   {
/*  454:1373 */     return this.m_delegate.weightedMatthewsCorrelation();
/*  455:     */   }
/*  456:     */   
/*  457:     */   public double recall(int classIndex)
/*  458:     */   {
/*  459:1392 */     return this.m_delegate.recall(classIndex);
/*  460:     */   }
/*  461:     */   
/*  462:     */   public double weightedRecall()
/*  463:     */   {
/*  464:1401 */     return this.m_delegate.weightedRecall();
/*  465:     */   }
/*  466:     */   
/*  467:     */   public double precision(int classIndex)
/*  468:     */   {
/*  469:1419 */     return this.m_delegate.precision(classIndex);
/*  470:     */   }
/*  471:     */   
/*  472:     */   public double weightedPrecision()
/*  473:     */   {
/*  474:1428 */     return this.m_delegate.weightedPrecision();
/*  475:     */   }
/*  476:     */   
/*  477:     */   public double fMeasure(int classIndex)
/*  478:     */   {
/*  479:1446 */     return this.m_delegate.fMeasure(classIndex);
/*  480:     */   }
/*  481:     */   
/*  482:     */   public double weightedFMeasure()
/*  483:     */   {
/*  484:1455 */     return this.m_delegate.weightedFMeasure();
/*  485:     */   }
/*  486:     */   
/*  487:     */   public double unweightedMacroFmeasure()
/*  488:     */   {
/*  489:1465 */     return this.m_delegate.unweightedMacroFmeasure();
/*  490:     */   }
/*  491:     */   
/*  492:     */   public double unweightedMicroFmeasure()
/*  493:     */   {
/*  494:1477 */     return this.m_delegate.unweightedMicroFmeasure();
/*  495:     */   }
/*  496:     */   
/*  497:     */   public void setPriors(Instances train)
/*  498:     */     throws Exception
/*  499:     */   {
/*  500:1488 */     this.m_delegate.setPriors(train);
/*  501:     */   }
/*  502:     */   
/*  503:     */   public double[] getClassPriors()
/*  504:     */   {
/*  505:1497 */     return this.m_delegate.getClassPriors();
/*  506:     */   }
/*  507:     */   
/*  508:     */   public void updatePriors(Instance instance)
/*  509:     */     throws Exception
/*  510:     */   {
/*  511:1508 */     this.m_delegate.updatePriors(instance);
/*  512:     */   }
/*  513:     */   
/*  514:     */   public void useNoPriors()
/*  515:     */   {
/*  516:1517 */     this.m_delegate.useNoPriors();
/*  517:     */   }
/*  518:     */   
/*  519:     */   public boolean equals(Object obj)
/*  520:     */   {
/*  521:1529 */     if ((obj instanceof Evaluation)) {
/*  522:1530 */       obj = ((Evaluation)obj).m_delegate;
/*  523:     */     }
/*  524:1532 */     return this.m_delegate.equals(obj);
/*  525:     */   }
/*  526:     */   
/*  527:     */   public static void main(String[] args)
/*  528:     */   {
/*  529:     */     try
/*  530:     */     {
/*  531:1545 */       if (args.length == 0) {
/*  532:1546 */         throw new Exception("The first argument must be the class name of a classifier");
/*  533:     */       }
/*  534:1549 */       String classifier = args[0];
/*  535:1550 */       args[0] = "";
/*  536:1551 */       System.out.println(evaluateModel(classifier, args));
/*  537:     */     }
/*  538:     */     catch (Exception ex)
/*  539:     */     {
/*  540:1553 */       ex.printStackTrace();
/*  541:1554 */       System.err.println(ex.getMessage());
/*  542:     */     }
/*  543:     */   }
/*  544:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.Evaluation
 * JD-Core Version:    0.7.0.1
 */