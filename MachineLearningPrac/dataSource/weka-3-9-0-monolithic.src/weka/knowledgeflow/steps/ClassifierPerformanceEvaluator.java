/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import java.util.concurrent.atomic.AtomicInteger;
/*   6:    */ import weka.classifiers.AggregateableEvaluation;
/*   7:    */ import weka.classifiers.Classifier;
/*   8:    */ import weka.classifiers.Evaluation;
/*   9:    */ import weka.classifiers.evaluation.ThresholdCurve;
/*  10:    */ import weka.classifiers.misc.InputMappedClassifier;
/*  11:    */ import weka.core.Attribute;
/*  12:    */ import weka.core.BatchPredictor;
/*  13:    */ import weka.core.Instance;
/*  14:    */ import weka.core.Instances;
/*  15:    */ import weka.core.OptionHandler;
/*  16:    */ import weka.core.OptionMetadata;
/*  17:    */ import weka.core.Utils;
/*  18:    */ import weka.core.WekaException;
/*  19:    */ import weka.gui.ProgrammaticProperty;
/*  20:    */ import weka.gui.explorer.ClassifierErrorsPlotInstances;
/*  21:    */ import weka.gui.explorer.ExplorerDefaults;
/*  22:    */ import weka.gui.visualize.PlotData2D;
/*  23:    */ import weka.knowledgeflow.Data;
/*  24:    */ import weka.knowledgeflow.ExecutionEnvironment;
/*  25:    */ import weka.knowledgeflow.ExecutionResult;
/*  26:    */ import weka.knowledgeflow.LogManager;
/*  27:    */ import weka.knowledgeflow.StepManager;
/*  28:    */ import weka.knowledgeflow.StepTask;
/*  29:    */ import weka.knowledgeflow.StepTaskCallback;
/*  30:    */ 
/*  31:    */ @KFStep(name="ClassifierPerformanceEvaluator", category="Evaluation", toolTipText="Evaluates batch classifiers", iconPath="weka/gui/knowledgeflow/icons/ClassifierPerformanceEvaluator.gif")
/*  32:    */ public class ClassifierPerformanceEvaluator
/*  33:    */   extends BaseStep
/*  34:    */ {
/*  35:    */   private static final long serialVersionUID = -2679292079974676672L;
/*  36:    */   private transient AggregateableEvaluation m_eval;
/*  37: 68 */   private transient Instances m_aggregatedPlotInstances = null;
/*  38: 71 */   private transient ArrayList<Object> m_aggregatedPlotSizes = null;
/*  39: 74 */   private transient ArrayList<Integer> m_aggregatedPlotShapes = null;
/*  40:    */   protected boolean m_errorPlotPointSizeProportionalToMargin;
/*  41: 83 */   protected String m_selectedEvalMetrics = "";
/*  42: 86 */   protected List<String> m_metricsList = new ArrayList();
/*  43:    */   protected boolean m_isReset;
/*  44:    */   protected AtomicInteger m_setsToGo;
/*  45:    */   protected int m_maxSetNum;
/*  46:    */   protected AtomicInteger m_taskCount;
/*  47:    */   
/*  48:    */   protected void stringToList(String l)
/*  49:    */   {
/*  50:100 */     if ((l != null) && (l.length() > 0))
/*  51:    */     {
/*  52:101 */       String[] parts = l.split(",");
/*  53:102 */       this.m_metricsList.clear();
/*  54:103 */       for (String s : parts) {
/*  55:104 */         this.m_metricsList.add(s.trim());
/*  56:    */       }
/*  57:    */     }
/*  58:    */   }
/*  59:    */   
/*  60:    */   @OptionMetadata(displayName="Error plot point size proportional to margin", description="Set the point size proportional to the prediction margin for classification error plots")
/*  61:    */   public boolean getErrorPlotPointSizeProportionalToMargin()
/*  62:    */   {
/*  63:120 */     return this.m_errorPlotPointSizeProportionalToMargin;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void setErrorPlotPointSizeProportionalToMargin(boolean e)
/*  67:    */   {
/*  68:131 */     this.m_errorPlotPointSizeProportionalToMargin = e;
/*  69:    */   }
/*  70:    */   
/*  71:    */   @ProgrammaticProperty
/*  72:    */   public String getEvaluationMetricsToOutput()
/*  73:    */   {
/*  74:141 */     return this.m_selectedEvalMetrics;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setEvaluationMetricsToOutput(String m)
/*  78:    */   {
/*  79:150 */     this.m_selectedEvalMetrics = m;
/*  80:151 */     stringToList(m);
/*  81:    */   }
/*  82:    */   
/*  83:    */   public List<String> getIncomingConnectionTypes()
/*  84:    */   {
/*  85:165 */     List<String> result = new ArrayList();
/*  86:167 */     if (getStepManager().numIncomingConnectionsOfType("batchClassifier") == 0) {
/*  87:169 */       result.add("batchClassifier");
/*  88:    */     }
/*  89:171 */     return result;
/*  90:    */   }
/*  91:    */   
/*  92:    */   public List<String> getOutgoingConnectionTypes()
/*  93:    */   {
/*  94:185 */     List<String> result = new ArrayList();
/*  95:186 */     if (getStepManager().numIncomingConnections() > 0)
/*  96:    */     {
/*  97:187 */       result.add("text");
/*  98:188 */       result.add("thresholdData");
/*  99:189 */       result.add("visualizableError");
/* 100:    */     }
/* 101:192 */     return result;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public ClassifierPerformanceEvaluator()
/* 105:    */   {
/* 106:200 */     this.m_metricsList = Evaluation.getAllEvaluationMetricNames();
/* 107:201 */     this.m_metricsList.remove("Coverage");
/* 108:202 */     this.m_metricsList.remove("Region size");
/* 109:203 */     StringBuilder b = new StringBuilder();
/* 110:204 */     for (String s : this.m_metricsList) {
/* 111:205 */       b.append(s).append(",");
/* 112:    */     }
/* 113:207 */     this.m_selectedEvalMetrics = b.substring(0, b.length() - 1);
/* 114:    */   }
/* 115:    */   
/* 116:    */   public void stepInit()
/* 117:    */     throws WekaException
/* 118:    */   {
/* 119:212 */     this.m_isReset = true;
/* 120:213 */     this.m_PlotInstances = null;
/* 121:214 */     this.m_aggregatedPlotInstances = null;
/* 122:215 */     this.m_taskCount = new AtomicInteger(0);
/* 123:    */   }
/* 124:    */   
/* 125:    */   public void stop()
/* 126:    */   {
/* 127:220 */     super.stop();
/* 128:222 */     if ((this.m_taskCount.get() == 0) && (isStopRequested())) {
/* 129:223 */       getStepManager().interrupted();
/* 130:    */     }
/* 131:    */   }
/* 132:    */   
/* 133:228 */   private transient ClassifierErrorsPlotInstances m_PlotInstances = null;
/* 134:    */   
/* 135:    */   public synchronized void processIncoming(Data data)
/* 136:    */     throws WekaException
/* 137:    */   {
/* 138:    */     try
/* 139:    */     {
/* 140:239 */       int setNum = ((Integer)data.getPayloadElement("aux_set_num")).intValue();
/* 141:    */       
/* 142:241 */       Instances trainingData = (Instances)data.getPayloadElement("aux_trainingSet");
/* 143:    */       
/* 144:    */ 
/* 145:244 */       Instances testData = (Instances)data.getPayloadElement("aux_testsSet");
/* 146:247 */       if ((testData == null) || (testData.numInstances() == 0))
/* 147:    */       {
/* 148:249 */         getStepManager().logDetailed("No test set available - unable to evaluate");
/* 149:    */         
/* 150:251 */         return;
/* 151:    */       }
/* 152:254 */       Classifier classifier = (Classifier)data.getPayloadElement("batchClassifier");
/* 153:    */       
/* 154:    */ 
/* 155:257 */       String evalLabel = data.getPayloadElement("aux_label").toString();
/* 156:260 */       if (this.m_isReset)
/* 157:    */       {
/* 158:261 */         this.m_isReset = false;
/* 159:262 */         getStepManager().processing();
/* 160:    */         
/* 161:264 */         this.m_maxSetNum = ((Integer)data.getPayloadElement("aux_max_set_num")).intValue();
/* 162:    */         
/* 163:    */ 
/* 164:267 */         this.m_setsToGo = new AtomicInteger(0);
/* 165:268 */         if (trainingData == null)
/* 166:    */         {
/* 167:270 */           Evaluation eval = new Evaluation(testData);
/* 168:271 */           this.m_PlotInstances = ExplorerDefaults.getClassifierErrorsPlotInstances();
/* 169:272 */           this.m_PlotInstances.setInstances(testData);
/* 170:273 */           this.m_PlotInstances.setClassifier(classifier);
/* 171:274 */           this.m_PlotInstances.setClassIndex(testData.classIndex());
/* 172:275 */           this.m_PlotInstances.setEvaluation(eval);
/* 173:    */           
/* 174:277 */           eval = adjustForInputMappedClassifier(eval, classifier, testData, this.m_PlotInstances);
/* 175:    */           
/* 176:    */ 
/* 177:280 */           eval.useNoPriors();
/* 178:281 */           this.m_eval = new AggregateableEvaluation(eval);
/* 179:282 */           this.m_eval.setMetricsToDisplay(this.m_metricsList);
/* 180:    */         }
/* 181:    */         else
/* 182:    */         {
/* 183:284 */           Evaluation eval = new Evaluation(trainingData);
/* 184:285 */           this.m_PlotInstances = ExplorerDefaults.getClassifierErrorsPlotInstances();
/* 185:286 */           this.m_PlotInstances.setInstances(trainingData);
/* 186:287 */           this.m_PlotInstances.setClassifier(classifier);
/* 187:288 */           this.m_PlotInstances.setClassIndex(trainingData.classIndex());
/* 188:289 */           this.m_PlotInstances.setEvaluation(eval);
/* 189:    */           
/* 190:291 */           eval = adjustForInputMappedClassifier(eval, classifier, trainingData, this.m_PlotInstances);
/* 191:    */           
/* 192:    */ 
/* 193:294 */           this.m_eval = new AggregateableEvaluation(eval);
/* 194:295 */           this.m_eval.setMetricsToDisplay(this.m_metricsList);
/* 195:    */         }
/* 196:297 */         this.m_PlotInstances.setUp();
/* 197:298 */         this.m_aggregatedPlotInstances = null;
/* 198:    */       }
/* 199:301 */       if (!isStopRequested())
/* 200:    */       {
/* 201:302 */         getStepManager().logBasic("Scheduling evaluation of fold/set " + setNum + " for execution");
/* 202:    */         
/* 203:    */ 
/* 204:    */ 
/* 205:306 */         EvaluationTask evalTask = new EvaluationTask(this, classifier, trainingData, testData, setNum, this.m_metricsList, getErrorPlotPointSizeProportionalToMargin(), evalLabel, new EvaluationCallback());
/* 206:    */         
/* 207:    */ 
/* 208:    */ 
/* 209:310 */         getStepManager().getExecutionEnvironment().submitTask(evalTask);
/* 210:311 */         this.m_taskCount.incrementAndGet();
/* 211:    */       }
/* 212:    */       else
/* 213:    */       {
/* 214:313 */         getStepManager().interrupted();
/* 215:    */       }
/* 216:    */     }
/* 217:    */     catch (Exception ex)
/* 218:    */     {
/* 219:316 */       throw new WekaException(ex);
/* 220:    */     }
/* 221:    */   }
/* 222:    */   
/* 223:    */   protected synchronized void aggregateEvalTask(Evaluation eval, Classifier classifier, Instances testData, ClassifierErrorsPlotInstances plotInstances, int setNum, String evalLabel)
/* 224:    */     throws Exception
/* 225:    */   {
/* 226:336 */     this.m_eval.aggregate(eval);
/* 227:338 */     if (this.m_aggregatedPlotInstances == null)
/* 228:    */     {
/* 229:340 */       this.m_aggregatedPlotShapes = ((ArrayList)plotInstances.getPlotShapes().clone());
/* 230:    */       
/* 231:342 */       this.m_aggregatedPlotSizes = ((ArrayList)plotInstances.getPlotSizes().clone());
/* 232:    */       
/* 233:    */ 
/* 234:    */ 
/* 235:346 */       this.m_aggregatedPlotInstances = new Instances(plotInstances.getPlotInstances());
/* 236:    */     }
/* 237:    */     else
/* 238:    */     {
/* 239:350 */       ArrayList<Object> tmpSizes = (ArrayList)plotInstances.getPlotSizes().clone();
/* 240:    */       
/* 241:352 */       ArrayList<Integer> tmpShapes = (ArrayList)plotInstances.getPlotShapes().clone();
/* 242:    */       
/* 243:    */ 
/* 244:355 */       Instances temp = plotInstances.getPlotInstances();
/* 245:356 */       for (int i = 0; i < temp.numInstances(); i++)
/* 246:    */       {
/* 247:357 */         this.m_aggregatedPlotInstances.add(temp.get(i));
/* 248:358 */         this.m_aggregatedPlotShapes.add(tmpShapes.get(i));
/* 249:359 */         this.m_aggregatedPlotSizes.add(tmpSizes.get(i));
/* 250:    */       }
/* 251:    */     }
/* 252:363 */     getStepManager().statusMessage("Completed folds/sets " + this.m_setsToGo.incrementAndGet());
/* 253:366 */     if (this.m_setsToGo.get() == this.m_maxSetNum)
/* 254:    */     {
/* 255:367 */       AggregateableClassifierErrorsPlotInstances aggPlot = new AggregateableClassifierErrorsPlotInstances();
/* 256:    */       
/* 257:369 */       aggPlot.setInstances(testData);
/* 258:370 */       aggPlot.setPlotInstances(this.m_aggregatedPlotInstances);
/* 259:371 */       aggPlot.setPlotShapes(this.m_aggregatedPlotShapes);
/* 260:372 */       aggPlot.setPlotSizes(this.m_aggregatedPlotSizes);
/* 261:373 */       aggPlot.setPointSizeProportionalToMargin(this.m_errorPlotPointSizeProportionalToMargin);
/* 262:    */       
/* 263:    */ 
/* 264:    */ 
/* 265:377 */       aggPlot.getPlotInstances();
/* 266:    */       
/* 267:379 */       String textTitle = "";
/* 268:380 */       textTitle = textTitle + classifier.getClass().getName();
/* 269:381 */       String textOptions = "";
/* 270:382 */       if ((classifier instanceof OptionHandler)) {
/* 271:383 */         textOptions = Utils.joinOptions(((OptionHandler)classifier).getOptions());
/* 272:    */       }
/* 273:386 */       textTitle = textTitle.substring(textTitle.lastIndexOf('.') + 1, textTitle.length());
/* 274:388 */       if ((evalLabel != null) && (evalLabel.length() > 0) && 
/* 275:389 */         (!textTitle.toLowerCase().startsWith(evalLabel.toLowerCase()))) {
/* 276:390 */         textTitle = evalLabel + " : " + textTitle;
/* 277:    */       }
/* 278:393 */       String resultT = "=== Evaluation result ===\n\nScheme: " + textTitle + "\n" + (textOptions.length() > 0 ? "Options: " + textOptions + "\n" : "") + "Relation: " + testData.relationName() + "\n\n" + this.m_eval.toSummaryString();
/* 279:402 */       if (testData.classAttribute().isNominal()) {
/* 280:403 */         resultT = resultT + "\n" + this.m_eval.toClassDetailsString() + "\n" + this.m_eval.toMatrixString();
/* 281:    */       }
/* 282:407 */       Data text = new Data("text");
/* 283:408 */       text.setPayloadElement("text", resultT);
/* 284:409 */       text.setPayloadElement("aux_textTitle", textTitle);
/* 285:410 */       getStepManager().outputData(new Data[] { text });
/* 286:413 */       if (getStepManager().numOutgoingConnectionsOfType("visualizableError") > 0)
/* 287:    */       {
/* 288:415 */         PlotData2D errorD = new PlotData2D(this.m_aggregatedPlotInstances);
/* 289:416 */         errorD.setShapeSize(this.m_aggregatedPlotSizes);
/* 290:417 */         errorD.setShapeType(this.m_aggregatedPlotShapes);
/* 291:418 */         errorD.setPlotName(textTitle + " " + textOptions);
/* 292:    */         
/* 293:420 */         Data visErr = new Data("visualizableError");
/* 294:421 */         visErr.setPayloadElement("visualizableError", errorD);
/* 295:422 */         getStepManager().outputData(new Data[] { visErr });
/* 296:    */       }
/* 297:426 */       if ((testData.classAttribute().isNominal()) && (getStepManager().numOutgoingConnectionsOfType("thresholdData") > 0))
/* 298:    */       {
/* 299:429 */         ThresholdCurve tc = new ThresholdCurve();
/* 300:430 */         Instances result = tc.getCurve(this.m_eval.predictions(), 0);
/* 301:431 */         result.setRelationName(testData.relationName());
/* 302:432 */         PlotData2D pd = new PlotData2D(result);
/* 303:433 */         String htmlTitle = "<html><font size=-2>" + textTitle;
/* 304:434 */         String newOptions = "";
/* 305:435 */         if ((classifier instanceof OptionHandler))
/* 306:    */         {
/* 307:436 */           String[] options = ((OptionHandler)classifier).getOptions();
/* 308:437 */           if (options.length > 0) {
/* 309:438 */             for (int ii = 0; ii < options.length; ii++) {
/* 310:439 */               if (options[ii].length() != 0)
/* 311:    */               {
/* 312:442 */                 if ((options[ii].charAt(0) == '-') && ((options[ii].charAt(1) < '0') || (options[ii].charAt(1) > '9'))) {
/* 313:444 */                   newOptions = newOptions + "<br>";
/* 314:    */                 }
/* 315:446 */                 newOptions = newOptions + options[ii];
/* 316:    */               }
/* 317:    */             }
/* 318:    */           }
/* 319:    */         }
/* 320:450 */         htmlTitle = htmlTitle + " " + newOptions + "<br>" + " (class: " + testData.classAttribute().value(0) + ")" + "</font></html>";
/* 321:    */         
/* 322:    */ 
/* 323:453 */         pd.setPlotName(textTitle + " (class: " + testData.classAttribute().value(0) + ")");
/* 324:    */         
/* 325:455 */         pd.setPlotNameHTML(htmlTitle);
/* 326:456 */         boolean[] connectPoints = new boolean[result.numInstances()];
/* 327:457 */         for (int jj = 1; jj < connectPoints.length; jj++) {
/* 328:458 */           connectPoints[jj] = true;
/* 329:    */         }
/* 330:461 */         pd.setConnectPoints(connectPoints);
/* 331:462 */         Data threshData = new Data("thresholdData");
/* 332:463 */         threshData.setPayloadElement("thresholdData", pd);
/* 333:464 */         threshData.setPayloadElement("class_attribute", testData.classAttribute());
/* 334:    */         
/* 335:466 */         getStepManager().outputData(new Data[] { threshData });
/* 336:    */       }
/* 337:468 */       getStepManager().finished();
/* 338:    */     }
/* 339:470 */     if (isStopRequested()) {
/* 340:471 */       getStepManager().interrupted();
/* 341:    */     }
/* 342:    */   }
/* 343:    */   
/* 344:    */   public String getCustomEditorForStep()
/* 345:    */   {
/* 346:485 */     return "weka.gui.knowledgeflow.steps.ClassifierPerformanceEvaluatorStepEditorDialog";
/* 347:    */   }
/* 348:    */   
/* 349:    */   protected static Evaluation adjustForInputMappedClassifier(Evaluation eval, Classifier classifier, Instances inst, ClassifierErrorsPlotInstances plotInstances)
/* 350:    */     throws Exception
/* 351:    */   {
/* 352:503 */     if ((classifier instanceof InputMappedClassifier))
/* 353:    */     {
/* 354:504 */       Instances mappedClassifierHeader = ((InputMappedClassifier)classifier).getModelHeader(new Instances(inst, 0));
/* 355:    */       
/* 356:    */ 
/* 357:    */ 
/* 358:508 */       eval = new Evaluation(new Instances(mappedClassifierHeader, 0));
/* 359:510 */       if (!eval.getHeader().equalHeaders(inst))
/* 360:    */       {
/* 361:516 */         Instances mappedClassifierDataset = ((InputMappedClassifier)classifier).getModelHeader(new Instances(mappedClassifierHeader, 0));
/* 362:519 */         for (int zz = 0; zz < inst.numInstances(); zz++)
/* 363:    */         {
/* 364:520 */           Instance mapped = ((InputMappedClassifier)classifier).constructMappedInstance(inst.instance(zz));
/* 365:    */           
/* 366:    */ 
/* 367:523 */           mappedClassifierDataset.add(mapped);
/* 368:    */         }
/* 369:526 */         eval.setPriors(mappedClassifierDataset);
/* 370:527 */         plotInstances.setInstances(mappedClassifierDataset);
/* 371:528 */         plotInstances.setClassifier(classifier);
/* 372:529 */         plotInstances.setClassIndex(mappedClassifierDataset.classIndex());
/* 373:530 */         plotInstances.setEvaluation(eval);
/* 374:    */       }
/* 375:    */     }
/* 376:534 */     return eval;
/* 377:    */   }
/* 378:    */   
/* 379:    */   protected static class AggregateableClassifierErrorsPlotInstances
/* 380:    */     extends ClassifierErrorsPlotInstances
/* 381:    */   {
/* 382:    */     private static final long serialVersionUID = 2012744784036684168L;
/* 383:    */     
/* 384:    */     public void setPlotShapes(ArrayList<Integer> plotShapes)
/* 385:    */     {
/* 386:558 */       this.m_PlotShapes = plotShapes;
/* 387:    */     }
/* 388:    */     
/* 389:    */     public void setPlotSizes(ArrayList<Object> plotSizes)
/* 390:    */     {
/* 391:568 */       this.m_PlotSizes = plotSizes;
/* 392:    */     }
/* 393:    */     
/* 394:    */     public void setPlotInstances(Instances inst)
/* 395:    */     {
/* 396:572 */       this.m_PlotInstances = inst;
/* 397:    */     }
/* 398:    */     
/* 399:    */     protected void finishUp()
/* 400:    */     {
/* 401:577 */       this.m_FinishUpCalled = true;
/* 402:579 */       if (!this.m_SaveForVisualization) {
/* 403:580 */         return;
/* 404:    */       }
/* 405:583 */       if ((this.m_Instances.classAttribute().isNumeric()) || (this.m_pointSizeProportionalToMargin)) {
/* 406:585 */         scaleNumericPredictions();
/* 407:    */       }
/* 408:    */     }
/* 409:    */   }
/* 410:    */   
/* 411:    */   protected static class EvaluationTask
/* 412:    */     extends StepTask<Object[]>
/* 413:    */   {
/* 414:    */     private static final long serialVersionUID = -686972773536075889L;
/* 415:    */     protected Classifier m_classifier;
/* 416:    */     protected Instances m_trainData;
/* 417:    */     protected Instances m_testData;
/* 418:    */     protected int m_setNum;
/* 419:    */     protected List<String> m_metricsList;
/* 420:    */     protected boolean m_errPlotPtSizePropToMarg;
/* 421:    */     protected String m_evalLabel;
/* 422:604 */     protected String m_classifierDesc = "";
/* 423:    */     
/* 424:    */     public EvaluationTask(Step source, Classifier classifier, Instances trainData, Instances testData, int setNum, List<String> metricsList, boolean errPlotPtSizePropToMarg, String evalLabel, ClassifierPerformanceEvaluator.EvaluationCallback callback)
/* 425:    */     {
/* 426:611 */       super(callback);
/* 427:612 */       this.m_classifier = classifier;
/* 428:613 */       this.m_trainData = trainData;
/* 429:614 */       this.m_testData = testData;
/* 430:615 */       this.m_setNum = setNum;
/* 431:616 */       this.m_metricsList = metricsList;
/* 432:617 */       this.m_errPlotPtSizePropToMarg = errPlotPtSizePropToMarg;
/* 433:618 */       this.m_evalLabel = evalLabel;
/* 434:    */       
/* 435:620 */       this.m_classifierDesc = this.m_classifier.getClass().getCanonicalName();
/* 436:621 */       this.m_classifierDesc = this.m_classifierDesc.substring(this.m_classifierDesc.lastIndexOf(".") + 1);
/* 437:623 */       if ((this.m_classifier instanceof OptionHandler))
/* 438:    */       {
/* 439:624 */         String optsString = Utils.joinOptions(((OptionHandler)this.m_classifier).getOptions());
/* 440:    */         
/* 441:626 */         this.m_classifierDesc = (this.m_classifierDesc + " " + optsString);
/* 442:    */       }
/* 443:    */     }
/* 444:    */     
/* 445:    */     public void process()
/* 446:    */       throws Exception
/* 447:    */     {
/* 448:632 */       Object[] r = new Object[6];
/* 449:633 */       r[4] = Integer.valueOf(this.m_setNum);
/* 450:634 */       getExecutionResult().setResult(r);
/* 451:    */       
/* 452:636 */       getLogHandler().statusMessage("Evaluating " + this.m_classifierDesc + " on fold/set " + this.m_setNum);
/* 453:    */       
/* 454:638 */       getLogHandler().logDetailed("Evaluating " + this.m_classifierDesc + " on " + this.m_testData.relationName() + " fold/set " + this.m_setNum);
/* 455:    */       
/* 456:    */ 
/* 457:641 */       ClassifierErrorsPlotInstances plotInstances = ExplorerDefaults.getClassifierErrorsPlotInstances();
/* 458:    */       
/* 459:643 */       Evaluation eval = null;
/* 460:645 */       if (this.m_trainData == null)
/* 461:    */       {
/* 462:646 */         eval = new Evaluation(this.m_testData);
/* 463:647 */         plotInstances.setInstances(this.m_testData);
/* 464:648 */         plotInstances.setClassifier(this.m_classifier);
/* 465:649 */         plotInstances.setClassIndex(this.m_testData.classIndex());
/* 466:650 */         plotInstances.setEvaluation(eval);
/* 467:651 */         plotInstances.setPointSizeProportionalToMargin(this.m_errPlotPtSizePropToMarg);
/* 468:    */         
/* 469:653 */         eval = ClassifierPerformanceEvaluator.adjustForInputMappedClassifier(eval, this.m_classifier, this.m_testData, plotInstances);
/* 470:    */         
/* 471:    */ 
/* 472:    */ 
/* 473:657 */         eval.useNoPriors();
/* 474:658 */         eval.setMetricsToDisplay(this.m_metricsList);
/* 475:    */       }
/* 476:    */       else
/* 477:    */       {
/* 478:660 */         eval = new Evaluation(this.m_trainData);
/* 479:661 */         plotInstances.setInstances(this.m_trainData);
/* 480:662 */         plotInstances.setClassifier(this.m_classifier);
/* 481:663 */         plotInstances.setClassIndex(this.m_trainData.classIndex());
/* 482:664 */         plotInstances.setEvaluation(eval);
/* 483:665 */         plotInstances.setPointSizeProportionalToMargin(this.m_errPlotPtSizePropToMarg);
/* 484:    */         
/* 485:667 */         eval = ClassifierPerformanceEvaluator.adjustForInputMappedClassifier(eval, this.m_classifier, this.m_trainData, plotInstances);
/* 486:    */         
/* 487:    */ 
/* 488:670 */         eval.setMetricsToDisplay(this.m_metricsList);
/* 489:    */       }
/* 490:673 */       plotInstances.setUp();
/* 491:674 */       if ((this.m_classifier instanceof BatchPredictor))
/* 492:    */       {
/* 493:675 */         double[][] predictions = ((BatchPredictor)this.m_classifier).distributionsForInstances(this.m_testData);
/* 494:    */         
/* 495:677 */         plotInstances.process(this.m_testData, predictions, eval);
/* 496:    */       }
/* 497:    */       else
/* 498:    */       {
/* 499:680 */         for (int i = 0; i < this.m_testData.numInstances(); i++)
/* 500:    */         {
/* 501:681 */           Instance temp = this.m_testData.instance(i);
/* 502:682 */           plotInstances.process(temp, this.m_classifier, eval);
/* 503:    */         }
/* 504:    */       }
/* 505:686 */       r[0] = eval;
/* 506:687 */       r[1] = this.m_classifier;
/* 507:688 */       r[2] = this.m_testData;
/* 508:689 */       r[3] = plotInstances;
/* 509:690 */       r[5] = this.m_evalLabel;
/* 510:    */     }
/* 511:    */   }
/* 512:    */   
/* 513:    */   protected class EvaluationCallback
/* 514:    */     implements StepTaskCallback<Object[]>
/* 515:    */   {
/* 516:    */     protected EvaluationCallback() {}
/* 517:    */     
/* 518:    */     public void taskFinished(ExecutionResult<Object[]> result)
/* 519:    */       throws Exception
/* 520:    */     {
/* 521:703 */       if (!ClassifierPerformanceEvaluator.this.isStopRequested())
/* 522:    */       {
/* 523:704 */         Evaluation eval = (Evaluation)((Object[])result.getResult())[0];
/* 524:705 */         Classifier classifier = (Classifier)((Object[])result.getResult())[1];
/* 525:    */         
/* 526:707 */         Instances testData = (Instances)((Object[])result.getResult())[2];
/* 527:708 */         ClassifierErrorsPlotInstances plotInstances = (ClassifierErrorsPlotInstances)((Object[])result.getResult())[3];
/* 528:    */         
/* 529:710 */         int setNum = ((Integer)((Object[])result.getResult())[4]).intValue();
/* 530:711 */         String evalLabel = ((Object[])result.getResult())[5].toString();
/* 531:    */         
/* 532:713 */         ClassifierPerformanceEvaluator.this.aggregateEvalTask(eval, classifier, testData, plotInstances, setNum, evalLabel);
/* 533:    */       }
/* 534:    */       else
/* 535:    */       {
/* 536:716 */         ClassifierPerformanceEvaluator.this.getStepManager().interrupted();
/* 537:    */       }
/* 538:718 */       ClassifierPerformanceEvaluator.this.m_taskCount.decrementAndGet();
/* 539:    */     }
/* 540:    */     
/* 541:    */     public void taskFailed(StepTask<Object[]> failedTask, ExecutionResult<Object[]> failedResult)
/* 542:    */       throws Exception
/* 543:    */     {
/* 544:724 */       Integer setNum = (Integer)((Object[])failedResult.getResult())[4];
/* 545:725 */       ClassifierPerformanceEvaluator.this.getStepManager().logError("Evaluation for fold " + setNum + " failed", failedResult.getError());
/* 546:    */       
/* 547:727 */       ClassifierPerformanceEvaluator.this.m_taskCount.decrementAndGet();
/* 548:    */     }
/* 549:    */   }
/* 550:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.ClassifierPerformanceEvaluator
 * JD-Core Version:    0.7.0.1
 */