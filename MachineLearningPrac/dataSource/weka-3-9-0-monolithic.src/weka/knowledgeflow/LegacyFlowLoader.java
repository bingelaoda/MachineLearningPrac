/*   1:    */ package weka.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.FileReader;
/*   6:    */ import java.io.InputStream;
/*   7:    */ import java.io.InputStreamReader;
/*   8:    */ import java.io.Reader;
/*   9:    */ import java.lang.reflect.Method;
/*  10:    */ import java.util.Set;
/*  11:    */ import java.util.Vector;
/*  12:    */ import weka.core.PluginManager;
/*  13:    */ import weka.core.WekaException;
/*  14:    */ import weka.gui.Logger;
/*  15:    */ import weka.gui.beans.BeanCommon;
/*  16:    */ import weka.gui.beans.BeanConnection;
/*  17:    */ import weka.gui.beans.BeanInstance;
/*  18:    */ import weka.gui.beans.WekaWrapper;
/*  19:    */ import weka.gui.beans.xml.XMLBeans;
/*  20:    */ import weka.knowledgeflow.steps.Step;
/*  21:    */ import weka.knowledgeflow.steps.WekaAlgorithmWrapper;
/*  22:    */ 
/*  23:    */ public class LegacyFlowLoader
/*  24:    */   implements FlowLoader
/*  25:    */ {
/*  26:    */   public static final String EXTENSION = "kfml";
/*  27:    */   protected static final String STEP_LIST_PROPS = "weka/knowledgeflow/steps/steps.props";
/*  28:    */   protected Vector<Object> m_beans;
/*  29:    */   protected Vector<BeanConnection> m_connections;
/*  30:    */   protected LogManager m_log;
/*  31:    */   
/*  32:    */   static
/*  33:    */   {
/*  34:    */     try
/*  35:    */     {
/*  36: 85 */       PluginManager.addFromProperties(LegacyFlowLoader.class.getClassLoader().getResourceAsStream("weka/knowledgeflow/steps/steps.props"), true);
/*  37:    */     }
/*  38:    */     catch (Exception e)
/*  39:    */     {
/*  40: 88 */       e.printStackTrace();
/*  41:    */     }
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void setLog(Logger log)
/*  45:    */   {
/*  46:114 */     this.m_log = new LogManager(log, false);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public String getFlowFileExtension()
/*  50:    */   {
/*  51:124 */     return "kfml";
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String getFlowFileExtensionDescription()
/*  55:    */   {
/*  56:134 */     return "Legacy XML-based Knowledge Flow configuration files";
/*  57:    */   }
/*  58:    */   
/*  59:    */   public Flow readFlow(File flowFile)
/*  60:    */     throws WekaException
/*  61:    */   {
/*  62:    */     try
/*  63:    */     {
/*  64:147 */       loadLegacy(new BufferedReader(new FileReader(flowFile)));
/*  65:    */     }
/*  66:    */     catch (Exception ex)
/*  67:    */     {
/*  68:149 */       throw new WekaException(ex);
/*  69:    */     }
/*  70:152 */     String name = flowFile.getName();
/*  71:153 */     name = name.substring(0, name.lastIndexOf('.'));
/*  72:154 */     return makeFlow(name);
/*  73:    */   }
/*  74:    */   
/*  75:    */   public Flow readFlow(InputStream is)
/*  76:    */     throws WekaException
/*  77:    */   {
/*  78:166 */     loadLegacy(new InputStreamReader(is));
/*  79:    */     
/*  80:168 */     return makeFlow("Untitled");
/*  81:    */   }
/*  82:    */   
/*  83:    */   public Flow readFlow(Reader r)
/*  84:    */     throws WekaException
/*  85:    */   {
/*  86:180 */     loadLegacy(r);
/*  87:    */     
/*  88:182 */     return makeFlow("Untitled");
/*  89:    */   }
/*  90:    */   
/*  91:    */   protected Flow makeFlow(String name)
/*  92:    */     throws WekaException
/*  93:    */   {
/*  94:194 */     Flow flow = new Flow();
/*  95:195 */     flow.setFlowName(name != null ? name : "Untitled");
/*  96:198 */     for (Object o : this.m_beans)
/*  97:    */     {
/*  98:199 */       BeanInstance bean = (BeanInstance)o;
/*  99:200 */       StepManagerImpl newManager = handleStep(bean);
/* 100:201 */       if (newManager != null) {
/* 101:202 */         flow.addStep(newManager);
/* 102:    */       }
/* 103:    */     }
/* 104:207 */     for (BeanConnection conn : this.m_connections) {
/* 105:208 */       handleConnection(flow, conn);
/* 106:    */     }
/* 107:211 */     return flow;
/* 108:    */   }
/* 109:    */   
/* 110:    */   protected void handleConnection(Flow flow, BeanConnection conn)
/* 111:    */   {
/* 112:222 */     BeanInstance source = conn.getSource();
/* 113:223 */     BeanInstance target = conn.getTarget();
/* 114:224 */     if ((!(source.getBean() instanceof BeanCommon)) && (!(target.getBean() instanceof BeanCommon))) {
/* 115:226 */       return;
/* 116:    */     }
/* 117:228 */     BeanCommon sourceC = (BeanCommon)source.getBean();
/* 118:229 */     BeanCommon targetC = (BeanCommon)target.getBean();
/* 119:    */     
/* 120:231 */     StepManagerImpl sourceNew = flow.findStep(sourceC.getCustomName());
/* 121:232 */     StepManagerImpl targetNew = flow.findStep(targetC.getCustomName());
/* 122:233 */     if ((sourceNew == null) || (targetNew == null))
/* 123:    */     {
/* 124:234 */       this.m_log.logWarning("Unable to make connection in new flow between legacy steps " + sourceC.getCustomName() + " and " + targetC.getCustomName() + " for connection '" + conn.getEventName());
/* 125:    */       
/* 126:    */ 
/* 127:    */ 
/* 128:238 */       return;
/* 129:    */     }
/* 130:241 */     String evntName = conn.getEventName();
/* 131:    */     
/* 132:243 */     flow.connectSteps(sourceNew, targetNew, evntName, true);
/* 133:    */   }
/* 134:    */   
/* 135:    */   protected StepManagerImpl handleStep(BeanInstance bean)
/* 136:    */     throws WekaException
/* 137:    */   {
/* 138:256 */     Object comp = bean.getBean();
/* 139:257 */     String name = "";
/* 140:258 */     if ((comp instanceof BeanCommon))
/* 141:    */     {
/* 142:259 */       BeanCommon beanCommon = (BeanCommon)comp;
/* 143:260 */       name = beanCommon.getCustomName();
/* 144:    */     }
/* 145:262 */     int x = bean.getX();
/* 146:263 */     int y = bean.getY();
/* 147:    */     
/* 148:265 */     Step match = findStepMatch(comp.getClass().getCanonicalName());
/* 149:266 */     if (match != null)
/* 150:    */     {
/* 151:267 */       StepManagerImpl manager = new StepManagerImpl(match);
/* 152:268 */       manager.m_x = x;
/* 153:269 */       manager.m_y = y;
/* 154:272 */       if (!(comp instanceof WekaWrapper)) {
/* 155:273 */         copySettingsNonWrapper(comp, match);
/* 156:    */       } else {
/* 157:275 */         copySettingsWrapper((WekaWrapper)comp, (WekaAlgorithmWrapper)match);
/* 158:    */       }
/* 159:278 */       if (!(match instanceof weka.knowledgeflow.steps.Note)) {
/* 160:279 */         match.setName(name);
/* 161:    */       }
/* 162:282 */       return manager;
/* 163:    */     }
/* 164:284 */     if (this.m_log != null) {
/* 165:285 */       this.m_log.logWarning("Unable to find an equivalent for legacy step: " + comp.getClass().getCanonicalName());
/* 166:    */     }
/* 167:290 */     return null;
/* 168:    */   }
/* 169:    */   
/* 170:    */   protected void copySettingsWrapper(WekaWrapper legacy, WekaAlgorithmWrapper current)
/* 171:    */     throws WekaException
/* 172:    */   {
/* 173:304 */     Object wrappedAlgo = legacy.getWrappedAlgorithm();
/* 174:305 */     current.setWrappedAlgorithm(wrappedAlgo);
/* 175:307 */     if (((legacy instanceof weka.gui.beans.Classifier)) && ((current instanceof weka.knowledgeflow.steps.Classifier)))
/* 176:    */     {
/* 177:309 */       ((weka.knowledgeflow.steps.Classifier)current).setLoadClassifierFileName(new File(((weka.gui.beans.Classifier)legacy).getLoadClassifierFileName()));
/* 178:    */       
/* 179:311 */       ((weka.knowledgeflow.steps.Classifier)current).setUpdateIncrementalClassifier(((weka.gui.beans.Classifier)legacy).getUpdateIncrementalClassifier());
/* 180:    */       
/* 181:    */ 
/* 182:314 */       ((weka.knowledgeflow.steps.Classifier)current).setResetIncrementalClassifier(((weka.gui.beans.Classifier)legacy).getResetIncrementalClassifier());
/* 183:    */     }
/* 184:317 */     else if (((legacy instanceof weka.gui.beans.Saver)) && ((current instanceof weka.knowledgeflow.steps.Saver)))
/* 185:    */     {
/* 186:319 */       ((weka.knowledgeflow.steps.Saver)current).setRelationNameForFilename(((weka.gui.beans.Saver)legacy).getRelationNameForFilename());
/* 187:    */     }
/* 188:    */   }
/* 189:    */   
/* 190:    */   protected void copySettingsNonWrapper(Object legacy, Step current)
/* 191:    */     throws WekaException
/* 192:    */   {
/* 193:336 */     if (((current instanceof weka.knowledgeflow.steps.Note)) && ((legacy instanceof weka.gui.beans.Note)))
/* 194:    */     {
/* 195:337 */       ((weka.knowledgeflow.steps.Note)current).setNoteText(((weka.gui.beans.Note)legacy).getNoteText());
/* 196:    */     }
/* 197:339 */     else if (((current instanceof weka.knowledgeflow.steps.TrainTestSplitMaker)) && ((legacy instanceof weka.gui.beans.TrainTestSplitMaker)))
/* 198:    */     {
/* 199:341 */       ((weka.knowledgeflow.steps.TrainTestSplitMaker)current).setSeed("" + ((weka.gui.beans.TrainTestSplitMaker)legacy).getSeed());
/* 200:    */       
/* 201:343 */       ((weka.knowledgeflow.steps.TrainTestSplitMaker)current).setTrainPercent("" + ((weka.gui.beans.TrainTestSplitMaker)legacy).getTrainPercent());
/* 202:    */     }
/* 203:345 */     else if (((current instanceof weka.knowledgeflow.steps.CrossValidationFoldMaker)) && ((legacy instanceof weka.gui.beans.CrossValidationFoldMaker)))
/* 204:    */     {
/* 205:347 */       ((weka.knowledgeflow.steps.CrossValidationFoldMaker)current).setSeed("" + ((weka.gui.beans.CrossValidationFoldMaker)legacy).getSeed());
/* 206:    */       
/* 207:349 */       ((weka.knowledgeflow.steps.CrossValidationFoldMaker)current).setNumFolds("" + ((weka.gui.beans.CrossValidationFoldMaker)legacy).getFolds());
/* 208:    */       
/* 209:351 */       ((weka.knowledgeflow.steps.CrossValidationFoldMaker)current).setPreserveOrder(((weka.gui.beans.CrossValidationFoldMaker)legacy).getPreserveOrder());
/* 210:    */     }
/* 211:354 */     else if (((current instanceof weka.knowledgeflow.steps.ClassAssigner)) && ((legacy instanceof weka.gui.beans.ClassAssigner)))
/* 212:    */     {
/* 213:356 */       ((weka.knowledgeflow.steps.ClassAssigner)current).setClassColumn(((weka.gui.beans.ClassAssigner)legacy).getClassColumn());
/* 214:    */     }
/* 215:359 */     else if (((current instanceof weka.knowledgeflow.steps.ClassValuePicker)) && ((legacy instanceof weka.gui.beans.ClassValuePicker)))
/* 216:    */     {
/* 217:361 */       ((weka.knowledgeflow.steps.ClassValuePicker)current).setClassValue(((weka.gui.beans.ClassValuePicker)legacy).getClassValue());
/* 218:    */     }
/* 219:364 */     else if (((current instanceof weka.knowledgeflow.steps.ClassifierPerformanceEvaluator)) && ((legacy instanceof weka.gui.beans.ClassifierPerformanceEvaluator)))
/* 220:    */     {
/* 221:366 */       ((weka.knowledgeflow.steps.ClassifierPerformanceEvaluator)current).setEvaluationMetricsToOutput(((weka.gui.beans.ClassifierPerformanceEvaluator)legacy).getEvaluationMetricsToOutput());
/* 222:    */       
/* 223:    */ 
/* 224:369 */       ((weka.knowledgeflow.steps.ClassifierPerformanceEvaluator)current).setErrorPlotPointSizeProportionalToMargin(((weka.gui.beans.ClassifierPerformanceEvaluator)legacy).getErrorPlotPointSizeProportionalToMargin());
/* 225:    */     }
/* 226:372 */     else if (((current instanceof weka.knowledgeflow.steps.IncrementalClassifierEvaluator)) && ((legacy instanceof weka.gui.beans.IncrementalClassifierEvaluator)))
/* 227:    */     {
/* 228:374 */       ((weka.knowledgeflow.steps.IncrementalClassifierEvaluator)current).setChartingEvalWindowSize(((weka.gui.beans.IncrementalClassifierEvaluator)legacy).getChartingEvalWindowSize());
/* 229:    */       
/* 230:    */ 
/* 231:377 */       ((weka.knowledgeflow.steps.IncrementalClassifierEvaluator)current).setOutputPerClassInfoRetrievalStats(((weka.gui.beans.IncrementalClassifierEvaluator)legacy).getOutputPerClassInfoRetrievalStats());
/* 232:    */       
/* 233:    */ 
/* 234:380 */       ((weka.knowledgeflow.steps.IncrementalClassifierEvaluator)current).setStatusFrequency(((weka.gui.beans.IncrementalClassifierEvaluator)legacy).getStatusFrequency());
/* 235:    */     }
/* 236:383 */     else if (((current instanceof weka.knowledgeflow.steps.PredictionAppender)) && ((legacy instanceof weka.gui.beans.PredictionAppender)))
/* 237:    */     {
/* 238:385 */       ((weka.knowledgeflow.steps.PredictionAppender)current).setAppendProbabilities(((weka.gui.beans.PredictionAppender)legacy).getAppendPredictedProbabilities());
/* 239:    */     }
/* 240:388 */     else if (((current instanceof weka.knowledgeflow.steps.SerializedModelSaver)) && ((legacy instanceof weka.gui.beans.SerializedModelSaver)))
/* 241:    */     {
/* 242:390 */       ((weka.knowledgeflow.steps.SerializedModelSaver)current).setFilenamePrefix(((weka.gui.beans.SerializedModelSaver)legacy).getPrefix());
/* 243:    */       
/* 244:    */ 
/* 245:393 */       ((weka.knowledgeflow.steps.SerializedModelSaver)current).setIncludeRelationNameInFilename(((weka.gui.beans.SerializedModelSaver)legacy).getIncludeRelationName());
/* 246:    */       
/* 247:    */ 
/* 248:396 */       ((weka.knowledgeflow.steps.SerializedModelSaver)current).setOutputDirectory(((weka.gui.beans.SerializedModelSaver)legacy).getDirectory());
/* 249:    */       
/* 250:    */ 
/* 251:399 */       ((weka.knowledgeflow.steps.SerializedModelSaver)current).setIncrementalSaveSchedule(((weka.gui.beans.SerializedModelSaver)legacy).getIncrementalSaveSchedule());
/* 252:    */     }
/* 253:402 */     else if (((current instanceof weka.knowledgeflow.steps.ImageSaver)) && ((legacy instanceof weka.gui.beans.ImageSaver)))
/* 254:    */     {
/* 255:404 */       ((weka.knowledgeflow.steps.ImageSaver)current).setFile(new File(((weka.gui.beans.ImageSaver)legacy).getFilename()));
/* 256:    */     }
/* 257:406 */     else if (((current instanceof weka.knowledgeflow.steps.TextSaver)) && ((legacy instanceof weka.gui.beans.TextSaver)))
/* 258:    */     {
/* 259:408 */       ((weka.knowledgeflow.steps.TextSaver)current).setFile(new File(((weka.gui.beans.TextSaver)legacy).getFilename()));
/* 260:    */       
/* 261:410 */       ((weka.knowledgeflow.steps.TextSaver)current).setAppend(((weka.gui.beans.TextSaver)legacy).getAppend());
/* 262:    */     }
/* 263:412 */     else if (((current instanceof weka.knowledgeflow.steps.StripChart)) && ((legacy instanceof weka.gui.beans.StripChart)))
/* 264:    */     {
/* 265:414 */       ((weka.knowledgeflow.steps.StripChart)current).setRefreshFreq(((weka.gui.beans.StripChart)legacy).getRefreshFreq());
/* 266:    */       
/* 267:416 */       ((weka.knowledgeflow.steps.StripChart)current).setRefreshWidth(((weka.gui.beans.StripChart)legacy).getRefreshWidth());
/* 268:    */       
/* 269:418 */       ((weka.knowledgeflow.steps.StripChart)current).setXLabelFreq(((weka.gui.beans.StripChart)legacy).getXLabelFreq());
/* 270:    */     }
/* 271:420 */     else if (((current instanceof weka.knowledgeflow.steps.ModelPerformanceChart)) && ((legacy instanceof weka.gui.beans.ModelPerformanceChart)))
/* 272:    */     {
/* 273:422 */       ((weka.knowledgeflow.steps.ModelPerformanceChart)current).setOffscreenAdditionalOpts(((weka.gui.beans.ModelPerformanceChart)legacy).getOffscreenAdditionalOpts());
/* 274:    */       
/* 275:    */ 
/* 276:425 */       ((weka.knowledgeflow.steps.ModelPerformanceChart)current).setOffscreenRendererName(((weka.gui.beans.ModelPerformanceChart)legacy).getOffscreenRendererName());
/* 277:    */       
/* 278:    */ 
/* 279:428 */       ((weka.knowledgeflow.steps.ModelPerformanceChart)current).setOffscreenHeight(((weka.gui.beans.ModelPerformanceChart)legacy).getOffscreenHeight());
/* 280:    */       
/* 281:    */ 
/* 282:431 */       ((weka.knowledgeflow.steps.ModelPerformanceChart)current).setOffscreenWidth(((weka.gui.beans.ModelPerformanceChart)legacy).getOffscreenWidth());
/* 283:    */       
/* 284:    */ 
/* 285:434 */       ((weka.knowledgeflow.steps.ModelPerformanceChart)current).setOffscreenXAxis(((weka.gui.beans.ModelPerformanceChart)legacy).getOffscreenXAxis());
/* 286:    */       
/* 287:    */ 
/* 288:437 */       ((weka.knowledgeflow.steps.ModelPerformanceChart)current).setOffscreenYAxis(((weka.gui.beans.ModelPerformanceChart)legacy).getOffscreenYAxis());
/* 289:    */     }
/* 290:440 */     else if (((current instanceof weka.knowledgeflow.steps.DataVisualizer)) && ((legacy instanceof weka.gui.beans.DataVisualizer)))
/* 291:    */     {
/* 292:442 */       ((weka.knowledgeflow.steps.DataVisualizer)current).setOffscreenHeight(((weka.gui.beans.DataVisualizer)legacy).getOffscreenHeight());
/* 293:    */       
/* 294:    */ 
/* 295:445 */       ((weka.knowledgeflow.steps.DataVisualizer)current).setOffscreenWidth(((weka.gui.beans.DataVisualizer)legacy).getOffscreenWidth());
/* 296:    */       
/* 297:    */ 
/* 298:448 */       ((weka.knowledgeflow.steps.DataVisualizer)current).setOffscreenXAxis(((weka.gui.beans.DataVisualizer)legacy).getOffscreenXAxis());
/* 299:    */       
/* 300:    */ 
/* 301:451 */       ((weka.knowledgeflow.steps.DataVisualizer)current).setOffscreenRendererName(((weka.gui.beans.DataVisualizer)legacy).getOffscreenRendererName());
/* 302:    */       
/* 303:    */ 
/* 304:454 */       ((weka.knowledgeflow.steps.DataVisualizer)current).setOffscreenAdditionalOpts(((weka.gui.beans.DataVisualizer)legacy).getOffscreenAdditionalOpts());
/* 305:    */     }
/* 306:457 */     else if (((current instanceof weka.knowledgeflow.steps.FlowByExpression)) && ((legacy instanceof weka.gui.beans.FlowByExpression)))
/* 307:    */     {
/* 308:459 */       ((weka.knowledgeflow.steps.FlowByExpression)current).setExpressionString(((weka.gui.beans.FlowByExpression)legacy).getExpressionString());
/* 309:    */       
/* 310:    */ 
/* 311:462 */       ((weka.knowledgeflow.steps.FlowByExpression)current).setTrueStepName(((weka.gui.beans.FlowByExpression)legacy).getTrueStepName());
/* 312:    */       
/* 313:    */ 
/* 314:465 */       ((weka.knowledgeflow.steps.FlowByExpression)current).setFalseStepName(((weka.gui.beans.FlowByExpression)legacy).getFalseStepName());
/* 315:    */     }
/* 316:468 */     else if (((current instanceof weka.knowledgeflow.steps.Join)) && ((legacy instanceof weka.gui.beans.Join)))
/* 317:    */     {
/* 318:469 */       ((weka.knowledgeflow.steps.Join)current).setKeySpec(((weka.gui.beans.Join)legacy).getKeySpec());
/* 319:    */     }
/* 320:470 */     else if (((current instanceof weka.knowledgeflow.steps.Sorter)) && ((legacy instanceof weka.gui.beans.Sorter)))
/* 321:    */     {
/* 322:472 */       ((weka.knowledgeflow.steps.Sorter)current).setSortDetails(((weka.gui.beans.Sorter)legacy).getSortDetails());
/* 323:    */       
/* 324:474 */       ((weka.knowledgeflow.steps.Sorter)current).setBufferSize(((weka.gui.beans.Sorter)legacy).getBufferSize());
/* 325:    */       
/* 326:476 */       ((weka.knowledgeflow.steps.Sorter)current).setTempDirectory(new File(((weka.gui.beans.Sorter)legacy).getTempDirectory()));
/* 327:    */     }
/* 328:478 */     else if (((current instanceof weka.knowledgeflow.steps.SubstringReplacer)) && ((legacy instanceof weka.gui.beans.SubstringReplacer)))
/* 329:    */     {
/* 330:480 */       ((weka.knowledgeflow.steps.SubstringReplacer)current).setMatchReplaceDetails(((weka.gui.beans.SubstringReplacer)legacy).getMatchReplaceDetails());
/* 331:    */     }
/* 332:483 */     else if (((current instanceof weka.knowledgeflow.steps.SubstringLabeler)) && ((legacy instanceof weka.gui.beans.SubstringLabeler)))
/* 333:    */     {
/* 334:485 */       ((weka.knowledgeflow.steps.SubstringLabeler)current).setMatchDetails(((weka.gui.beans.SubstringLabeler)legacy).getMatchDetails());
/* 335:    */       
/* 336:    */ 
/* 337:488 */       ((weka.knowledgeflow.steps.SubstringLabeler)current).setConsumeNonMatching(((weka.gui.beans.SubstringLabeler)legacy).getConsumeNonMatching());
/* 338:    */       
/* 339:    */ 
/* 340:491 */       ((weka.knowledgeflow.steps.SubstringLabeler)current).setMatchAttributeName(((weka.gui.beans.SubstringLabeler)legacy).getMatchAttributeName());
/* 341:    */       
/* 342:    */ 
/* 343:494 */       ((weka.knowledgeflow.steps.SubstringLabeler)current).setNominalBinary(((weka.gui.beans.SubstringLabeler)legacy).getNominalBinary());
/* 344:    */     }
/* 345:    */     else
/* 346:    */     {
/* 347:499 */       configurePluginStep(legacy, current);
/* 348:    */     }
/* 349:    */   }
/* 350:    */   
/* 351:    */   protected void transferSetting(Object legacy, Step current, String propName, Class propType)
/* 352:    */     throws WekaException
/* 353:    */   {
/* 354:    */     try
/* 355:    */     {
/* 356:515 */       Method getM = legacy.getClass().getMethod("get" + propName, new Class[0]);
/* 357:    */       
/* 358:517 */       Object value = getM.invoke(legacy, new Object[0]);
/* 359:518 */       Method setM = current.getClass().getMethod("set" + propName, new Class[] { propType });
/* 360:    */       
/* 361:    */ 
/* 362:521 */       setM.invoke(current, new Object[] { value });
/* 363:    */     }
/* 364:    */     catch (Exception ex)
/* 365:    */     {
/* 366:523 */       throw new WekaException(ex);
/* 367:    */     }
/* 368:    */   }
/* 369:    */   
/* 370:    */   protected void configurePluginStep(Object legacy, Step current)
/* 371:    */     throws WekaException
/* 372:    */   {
/* 373:536 */     if ((legacy.getClass().toString().endsWith("PythonScriptExecutor")) && (current.getClass().toString().endsWith("PythonScriptExecutor"))) {
/* 374:    */       try
/* 375:    */       {
/* 376:540 */         transferSetting(legacy, current, "Debug", Boolean.TYPE);
/* 377:541 */         transferSetting(legacy, current, "PythonScript", String.class);
/* 378:    */         
/* 379:543 */         Method getM = legacy.getClass().getDeclaredMethod("getScriptFile", new Class[0]);
/* 380:    */         
/* 381:545 */         Object value = getM.invoke(legacy, new Object[0]);
/* 382:546 */         Method setM = current.getClass().getDeclaredMethod("setScriptFile", new Class[] { File.class });
/* 383:    */         
/* 384:    */ 
/* 385:549 */         setM.invoke(current, new Object[] { new File(value.toString()) });
/* 386:    */         
/* 387:551 */         transferSetting(legacy, current, "VariablesToGetFromPython", String.class);
/* 388:    */       }
/* 389:    */       catch (Exception ex)
/* 390:    */       {
/* 391:554 */         throw new WekaException(ex);
/* 392:    */       }
/* 393:556 */     } else if ((legacy.getClass().toString().endsWith("RScriptExecutor")) && (current.getClass().toString().endsWith("RScriptExecutor"))) {
/* 394:    */       try
/* 395:    */       {
/* 396:560 */         transferSetting(legacy, current, "RScript", String.class);
/* 397:    */         
/* 398:562 */         Method getM = legacy.getClass().getDeclaredMethod("getScriptFile", new Class[0]);
/* 399:    */         
/* 400:564 */         Object value = getM.invoke(legacy, new Object[0]);
/* 401:565 */         Method setM = current.getClass().getDeclaredMethod("setScriptFile", new Class[] { File.class });
/* 402:    */         
/* 403:    */ 
/* 404:568 */         setM.invoke(current, new Object[] { new File(value.toString()) });
/* 405:    */       }
/* 406:    */       catch (Exception ex)
/* 407:    */       {
/* 408:570 */         throw new WekaException(ex);
/* 409:    */       }
/* 410:572 */     } else if ((legacy.getClass().toString().endsWith("JsonFieldExtractor")) && (current.getClass().toString().endsWith("JsonFieldExtractor"))) {
/* 411:    */       try
/* 412:    */       {
/* 413:575 */         transferSetting(legacy, current, "PathDetails", String.class);
/* 414:    */       }
/* 415:    */       catch (Exception ex)
/* 416:    */       {
/* 417:577 */         throw new WekaException(ex);
/* 418:    */       }
/* 419:579 */     } else if ((legacy.getClass().toString().endsWith("TimeSeriesForecasting")) && (current.getClass().toString().endsWith("TimeSeriesForecasting"))) {
/* 420:    */       try
/* 421:    */       {
/* 422:582 */         transferSetting(legacy, current, "EncodedForecaster", String.class);
/* 423:583 */         transferSetting(legacy, current, "NumStepsToForecast", String.class);
/* 424:584 */         transferSetting(legacy, current, "ArtificialTimeStartOffset", String.class);
/* 425:    */         
/* 426:586 */         transferSetting(legacy, current, "RebuildForecaster", Boolean.TYPE);
/* 427:    */         
/* 428:588 */         Method getM = legacy.getClass().getDeclaredMethod("getFilename", new Class[0]);
/* 429:    */         
/* 430:590 */         Object value = getM.invoke(legacy, new Object[0]);
/* 431:591 */         Method setM = current.getClass().getDeclaredMethod("setFilename", new Class[] { File.class });
/* 432:    */         
/* 433:    */ 
/* 434:594 */         setM.invoke(current, new Object[] { new File(value.toString()) });
/* 435:    */         
/* 436:596 */         getM = legacy.getClass().getDeclaredMethod("getSaveFilename", new Class[0]);
/* 437:    */         
/* 438:    */ 
/* 439:599 */         value = getM.invoke(legacy, new Object[0]);
/* 440:600 */         setM = current.getClass().getDeclaredMethod("setSaveFilename", new Class[] { File.class });
/* 441:    */         
/* 442:    */ 
/* 443:603 */         setM.invoke(current, new Object[] { new File(value.toString()) });
/* 444:    */       }
/* 445:    */       catch (Exception ex)
/* 446:    */       {
/* 447:605 */         throw new WekaException(ex);
/* 448:    */       }
/* 449:607 */     } else if ((legacy.getClass().toString().endsWith("GroovyComponent")) && (current.getClass().toString().endsWith("GroovyStep"))) {
/* 450:609 */       transferSetting(legacy, current, "Script", String.class);
/* 451:610 */     } else if ((legacy.getClass().getSuperclass().toString().endsWith("AbstractSparkJob")) && (current.getClass().getSuperclass().toString().endsWith("AbstractSparkJob"))) {
/* 452:614 */       transferSetting(legacy, current, "JobOptions", String.class);
/* 453:615 */     } else if (legacy.getClass().getSuperclass().toString().endsWith("AbstractHadoopJob")) {
/* 454:617 */       transferSetting(legacy, current, "JobOptions", String.class);
/* 455:    */     }
/* 456:    */   }
/* 457:    */   
/* 458:    */   protected Step findStepMatch(String legacyFullyQualified)
/* 459:    */     throws WekaException
/* 460:    */   {
/* 461:632 */     String clazzNameOnly = legacyFullyQualified.substring(legacyFullyQualified.lastIndexOf('.') + 1, legacyFullyQualified.length());
/* 462:637 */     if (clazzNameOnly.equals("Note")) {
/* 463:638 */       return new weka.knowledgeflow.steps.Note();
/* 464:    */     }
/* 465:641 */     Set<String> steps = PluginManager.getPluginNamesOfType(Step.class.getCanonicalName());
/* 466:    */     
/* 467:    */ 
/* 468:    */ 
/* 469:645 */     Step result = null;
/* 470:646 */     if (steps != null) {
/* 471:647 */       for (String s : steps)
/* 472:    */       {
/* 473:648 */         String sClazzNameOnly = s.substring(s.lastIndexOf(".") + 1);
/* 474:649 */         if (sClazzNameOnly.equals(clazzNameOnly)) {
/* 475:    */           try
/* 476:    */           {
/* 477:651 */             result = (Step)PluginManager.getPluginInstance(Step.class.getCanonicalName(), s);
/* 478:    */           }
/* 479:    */           catch (Exception ex)
/* 480:    */           {
/* 481:656 */             throw new WekaException(ex);
/* 482:    */           }
/* 483:    */         }
/* 484:    */       }
/* 485:    */     }
/* 486:662 */     return result;
/* 487:    */   }
/* 488:    */   
/* 489:    */   protected void loadLegacy(Reader r)
/* 490:    */     throws WekaException
/* 491:    */   {
/* 492:673 */     BeanConnection.init();
/* 493:674 */     BeanInstance.init();
/* 494:    */     try
/* 495:    */     {
/* 496:676 */       XMLBeans xml = new XMLBeans(null, null, 0);
/* 497:677 */       Vector<?> v = (Vector)xml.read(r);
/* 498:    */       
/* 499:679 */       this.m_beans = ((Vector)v.get(0));
/* 500:680 */       this.m_connections = ((Vector)v.get(1));
/* 501:    */     }
/* 502:    */     catch (Exception ex)
/* 503:    */     {
/* 504:683 */       throw new WekaException(ex);
/* 505:    */     }
/* 506:    */   }
/* 507:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.LegacyFlowLoader
 * JD-Core Version:    0.7.0.1
 */