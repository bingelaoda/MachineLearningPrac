/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.image.BufferedImage;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.LinkedHashMap;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Map;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.DenseInstance;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.OptionMetadata;
/*  13:    */ import weka.core.PluginManager;
/*  14:    */ import weka.core.WekaException;
/*  15:    */ import weka.gui.ProgrammaticProperty;
/*  16:    */ import weka.gui.beans.OffscreenChartRenderer;
/*  17:    */ import weka.gui.beans.WekaOffscreenChartRenderer;
/*  18:    */ import weka.gui.visualize.PlotData2D;
/*  19:    */ import weka.knowledgeflow.Data;
/*  20:    */ import weka.knowledgeflow.StepManager;
/*  21:    */ 
/*  22:    */ @KFStep(name="ModelPerformanceChart", category="Visualization", toolTipText="Visualize performance charts (such as ROC).", iconPath="weka/gui/knowledgeflow/icons/ModelPerformanceChart.gif")
/*  23:    */ public class ModelPerformanceChart
/*  24:    */   extends BaseStep
/*  25:    */   implements DataCollector
/*  26:    */ {
/*  27:    */   private static final long serialVersionUID = 6166590810777938147L;
/*  28: 60 */   protected List<PlotData2D> m_plots = new ArrayList();
/*  29:    */   protected transient List<Instances> m_offscreenPlotData;
/*  30:    */   protected transient List<String> m_thresholdSeriesTitles;
/*  31:    */   protected transient OffscreenChartRenderer m_offscreenRenderer;
/*  32: 68 */   protected String m_offscreenRendererName = "Weka Chart Renderer";
/*  33: 74 */   protected String m_xAxis = "";
/*  34: 80 */   protected String m_yAxis = "";
/*  35: 85 */   protected String m_additionalOptions = "";
/*  36: 88 */   protected String m_width = "500";
/*  37: 91 */   protected String m_height = "400";
/*  38:    */   protected boolean m_dataIsThresholdData;
/*  39:    */   
/*  40:    */   @OptionMetadata(displayName="X-axis attribute", description="Attribute name or /first, /last or /<index>", displayOrder=1)
/*  41:    */   public void setOffscreenXAxis(String xAxis)
/*  42:    */   {
/*  43:106 */     this.m_xAxis = xAxis;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public String getOffscreenXAxis()
/*  47:    */   {
/*  48:115 */     return this.m_xAxis;
/*  49:    */   }
/*  50:    */   
/*  51:    */   @OptionMetadata(displayName="Y-axis attribute", description="Attribute name or /first, /last or /<index>", displayOrder=2)
/*  52:    */   public void setOffscreenYAxis(String yAxis)
/*  53:    */   {
/*  54:128 */     this.m_yAxis = yAxis;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public String getOffscreenYAxis()
/*  58:    */   {
/*  59:137 */     return this.m_yAxis;
/*  60:    */   }
/*  61:    */   
/*  62:    */   @OptionMetadata(displayName="Chart width (pixels)", description="Width of the rendered chart", displayOrder=3)
/*  63:    */   public void setOffscreenWidth(String width)
/*  64:    */   {
/*  65:148 */     this.m_width = width;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public String getOffscreenWidth()
/*  69:    */   {
/*  70:157 */     return this.m_width;
/*  71:    */   }
/*  72:    */   
/*  73:    */   @OptionMetadata(displayName="Chart height (pixels)", description="Height of the rendered chart", displayOrder=4)
/*  74:    */   public void setOffscreenHeight(String height)
/*  75:    */   {
/*  76:168 */     this.m_height = height;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public String getOffscreenHeight()
/*  80:    */   {
/*  81:177 */     return this.m_height;
/*  82:    */   }
/*  83:    */   
/*  84:    */   @ProgrammaticProperty
/*  85:    */   public void setOffscreenRendererName(String rendererName)
/*  86:    */   {
/*  87:188 */     this.m_offscreenRendererName = rendererName;
/*  88:189 */     this.m_offscreenRenderer = null;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public String getOffscreenRendererName()
/*  92:    */   {
/*  93:199 */     return this.m_offscreenRendererName;
/*  94:    */   }
/*  95:    */   
/*  96:    */   @ProgrammaticProperty
/*  97:    */   public void setOffscreenAdditionalOpts(String additional)
/*  98:    */   {
/*  99:209 */     this.m_additionalOptions = additional;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public String getOffscreenAdditionalOpts()
/* 103:    */   {
/* 104:218 */     return this.m_additionalOptions;
/* 105:    */   }
/* 106:    */   
/* 107:    */   protected void setupOffscreenRenderer()
/* 108:    */   {
/* 109:225 */     getStepManager().logDetailed("Initializing offscreen renderer: " + getOffscreenRendererName());
/* 110:227 */     if (this.m_offscreenRenderer == null)
/* 111:    */     {
/* 112:228 */       if ((this.m_offscreenRendererName == null) || (this.m_offscreenRendererName.length() == 0))
/* 113:    */       {
/* 114:230 */         this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/* 115:231 */         return;
/* 116:    */       }
/* 117:234 */       if (this.m_offscreenRendererName.equalsIgnoreCase("weka chart renderer")) {
/* 118:235 */         this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/* 119:    */       } else {
/* 120:    */         try
/* 121:    */         {
/* 122:238 */           Object r = PluginManager.getPluginInstance("weka.gui.beans.OffscreenChartRenderer", this.m_offscreenRendererName);
/* 123:241 */           if ((r != null) && ((r instanceof OffscreenChartRenderer)))
/* 124:    */           {
/* 125:242 */             this.m_offscreenRenderer = ((OffscreenChartRenderer)r);
/* 126:    */           }
/* 127:    */           else
/* 128:    */           {
/* 129:245 */             getStepManager().logWarning("Offscreen renderer '" + getOffscreenRendererName() + "' is not available, using default weka chart renderer " + "instead");
/* 130:    */             
/* 131:    */ 
/* 132:    */ 
/* 133:249 */             this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/* 134:    */           }
/* 135:    */         }
/* 136:    */         catch (Exception ex)
/* 137:    */         {
/* 138:253 */           getStepManager().logWarning("Offscreen renderer '" + getOffscreenRendererName() + "' is not available, using default weka chart renderer " + "instead");
/* 139:    */           
/* 140:    */ 
/* 141:    */ 
/* 142:257 */           this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/* 143:    */         }
/* 144:    */       }
/* 145:    */     }
/* 146:    */   }
/* 147:    */   
/* 148:    */   public List<String> getIncomingConnectionTypes()
/* 149:    */   {
/* 150:274 */     List<String> result = new ArrayList();
/* 151:276 */     if (getStepManager().numIncomingConnections() == 0)
/* 152:    */     {
/* 153:277 */       result.add("thresholdData");
/* 154:278 */       result.add("visualizableError");
/* 155:    */     }
/* 156:282 */     else if (getStepManager().numIncomingConnectionsOfType("thresholdData") > 0)
/* 157:    */     {
/* 158:284 */       result.add("thresholdData");
/* 159:    */     }
/* 160:288 */     return result;
/* 161:    */   }
/* 162:    */   
/* 163:    */   public List<String> getOutgoingConnectionTypes()
/* 164:    */   {
/* 165:302 */     List<String> result = new ArrayList();
/* 166:303 */     if (getStepManager().numIncomingConnections() > 0) {
/* 167:304 */       result.add("image");
/* 168:    */     }
/* 169:306 */     return result;
/* 170:    */   }
/* 171:    */   
/* 172:    */   protected BufferedImage addOffscreenThresholdPlot(PlotData2D thresholdD)
/* 173:    */     throws WekaException
/* 174:    */   {
/* 175:318 */     this.m_offscreenPlotData.add(thresholdD.getPlotInstances());
/* 176:319 */     this.m_thresholdSeriesTitles.add(thresholdD.getPlotName());
/* 177:320 */     List<String> options = new ArrayList();
/* 178:321 */     String additional = "-color=/last";
/* 179:322 */     if ((this.m_additionalOptions != null) && (this.m_additionalOptions.length() > 0))
/* 180:    */     {
/* 181:323 */       additional = this.m_additionalOptions;
/* 182:324 */       additional = getStepManager().environmentSubstitute(additional);
/* 183:    */     }
/* 184:326 */     String[] optsParts = additional.split(",");
/* 185:327 */     for (String p : optsParts) {
/* 186:328 */       options.add(p.trim());
/* 187:    */     }
/* 188:331 */     String xAxis = "False Positive Rate";
/* 189:332 */     if ((this.m_xAxis != null) && (this.m_xAxis.length() > 0))
/* 190:    */     {
/* 191:333 */       xAxis = this.m_xAxis;
/* 192:334 */       xAxis = getStepManager().environmentSubstitute(xAxis);
/* 193:    */     }
/* 194:336 */     String yAxis = "True Positive Rate";
/* 195:337 */     if ((this.m_yAxis != null) && (this.m_yAxis.length() > 0))
/* 196:    */     {
/* 197:338 */       yAxis = this.m_yAxis;
/* 198:339 */       yAxis = getStepManager().environmentSubstitute(yAxis);
/* 199:    */     }
/* 200:342 */     String width = this.m_width;
/* 201:343 */     String height = this.m_height;
/* 202:344 */     int defWidth = 500;
/* 203:345 */     int defHeight = 400;
/* 204:346 */     width = getStepManager().environmentSubstitute(width);
/* 205:347 */     height = getStepManager().environmentSubstitute(height);
/* 206:348 */     defWidth = Integer.parseInt(width);
/* 207:349 */     defHeight = Integer.parseInt(height);
/* 208:350 */     List<Instances> series = new ArrayList();
/* 209:351 */     for (int i = 0; i < this.m_offscreenPlotData.size(); i++)
/* 210:    */     {
/* 211:352 */       Instances temp = new Instances((Instances)this.m_offscreenPlotData.get(i));
/* 212:353 */       temp.setRelationName((String)this.m_thresholdSeriesTitles.get(i));
/* 213:354 */       series.add(temp);
/* 214:    */     }
/* 215:    */     try
/* 216:    */     {
/* 217:357 */       return this.m_offscreenRenderer.renderXYLineChart(defWidth, defHeight, series, xAxis, yAxis, options);
/* 218:    */     }
/* 219:    */     catch (Exception ex)
/* 220:    */     {
/* 221:360 */       throw new WekaException(ex);
/* 222:    */     }
/* 223:    */   }
/* 224:    */   
/* 225:    */   protected BufferedImage addOffscreenErrorPlot(PlotData2D plotData)
/* 226:    */     throws WekaException
/* 227:    */   {
/* 228:373 */     Instances predictedI = plotData.getPlotInstances();
/* 229:374 */     if (predictedI.classAttribute().isNominal())
/* 230:    */     {
/* 231:383 */       ArrayList<Attribute> atts = new ArrayList();
/* 232:384 */       for (int i = 0; i < predictedI.numAttributes(); i++) {
/* 233:385 */         atts.add((Attribute)predictedI.attribute(i).copy());
/* 234:    */       }
/* 235:387 */       atts.add(new Attribute("@@size@@"));
/* 236:388 */       Instances newInsts = new Instances(predictedI.relationName(), atts, predictedI.numInstances());
/* 237:    */       
/* 238:    */ 
/* 239:391 */       newInsts.setClassIndex(predictedI.classIndex());
/* 240:393 */       for (int i = 0; i < predictedI.numInstances(); i++)
/* 241:    */       {
/* 242:394 */         double[] vals = new double[newInsts.numAttributes()];
/* 243:395 */         for (int j = 0; j < predictedI.numAttributes(); j++) {
/* 244:396 */           vals[j] = predictedI.instance(i).value(j);
/* 245:    */         }
/* 246:398 */         vals[(vals.length - 1)] = 2.0D;
/* 247:399 */         Instance ni = new DenseInstance(1.0D, vals);
/* 248:400 */         newInsts.add(ni);
/* 249:    */       }
/* 250:404 */       Instances[] classes = new Instances[newInsts.numClasses()];
/* 251:405 */       for (int i = 0; i < newInsts.numClasses(); i++)
/* 252:    */       {
/* 253:406 */         classes[i] = new Instances(newInsts, 0);
/* 254:407 */         classes[i].setRelationName(newInsts.classAttribute().value(i));
/* 255:    */       }
/* 256:409 */       Instances errors = new Instances(newInsts, 0);
/* 257:410 */       int actualClass = newInsts.classIndex();
/* 258:411 */       for (int i = 0; i < newInsts.numInstances(); i++)
/* 259:    */       {
/* 260:412 */         Instance current = newInsts.instance(i);
/* 261:413 */         classes[((int)current.classValue())].add((Instance)current.copy());
/* 262:415 */         if (current.value(actualClass) != current.value(actualClass - 1))
/* 263:    */         {
/* 264:416 */           Instance toAdd = (Instance)current.copy();
/* 265:    */           
/* 266:    */ 
/* 267:419 */           toAdd.setValue(toAdd.numAttributes() - 1, 5.0D);
/* 268:    */           
/* 269:    */ 
/* 270:    */ 
/* 271:    */ 
/* 272:424 */           double actualClassV = toAdd.value(actualClass);
/* 273:425 */           double predictedClassV = toAdd.value(actualClass - 1);
/* 274:426 */           toAdd.setValue(actualClass, predictedClassV);
/* 275:427 */           toAdd.setValue(actualClass - 1, actualClassV);
/* 276:    */           
/* 277:429 */           errors.add(toAdd);
/* 278:    */         }
/* 279:    */       }
/* 280:433 */       errors.setRelationName("Errors");
/* 281:434 */       this.m_offscreenPlotData.add(errors);
/* 282:436 */       for (Instances classe : classes) {
/* 283:437 */         this.m_offscreenPlotData.add(classe);
/* 284:    */       }
/* 285:    */     }
/* 286:    */     else
/* 287:    */     {
/* 288:443 */       ArrayList<Attribute> atts = new ArrayList();
/* 289:444 */       for (int i = 0; i < predictedI.numAttributes(); i++) {
/* 290:445 */         atts.add((Attribute)predictedI.attribute(i).copy());
/* 291:    */       }
/* 292:447 */       atts.add(new Attribute("@@size@@"));
/* 293:448 */       Instances newInsts = new Instances(predictedI.relationName(), atts, predictedI.numInstances());
/* 294:    */       
/* 295:    */ 
/* 296:    */ 
/* 297:452 */       int[] shapeSizes = plotData.getShapeSize();
/* 298:454 */       for (int i = 0; i < predictedI.numInstances(); i++)
/* 299:    */       {
/* 300:455 */         double[] vals = new double[newInsts.numAttributes()];
/* 301:456 */         for (int j = 0; j < predictedI.numAttributes(); j++) {
/* 302:457 */           vals[j] = predictedI.instance(i).value(j);
/* 303:    */         }
/* 304:459 */         vals[(vals.length - 1)] = shapeSizes[i];
/* 305:460 */         Instance ni = new DenseInstance(1.0D, vals);
/* 306:461 */         newInsts.add(ni);
/* 307:    */       }
/* 308:463 */       newInsts.setRelationName(predictedI.classAttribute().name());
/* 309:464 */       this.m_offscreenPlotData.add(newInsts);
/* 310:    */     }
/* 311:467 */     List<String> options = new ArrayList();
/* 312:    */     
/* 313:469 */     String additional = "-color=" + predictedI.classAttribute().name() + ",-hasErrors";
/* 314:471 */     if ((this.m_additionalOptions != null) && (this.m_additionalOptions.length() > 0))
/* 315:    */     {
/* 316:472 */       additional = additional + "," + this.m_additionalOptions;
/* 317:473 */       additional = environmentSubstitute(additional);
/* 318:    */     }
/* 319:475 */     String[] optionsParts = additional.split(",");
/* 320:476 */     for (String p : optionsParts) {
/* 321:477 */       options.add(p.trim());
/* 322:    */     }
/* 323:481 */     options.add("-shapeSize=@@size@@");
/* 324:    */     
/* 325:    */ 
/* 326:484 */     String xAxis = this.m_xAxis;
/* 327:485 */     xAxis = environmentSubstitute(xAxis);
/* 328:    */     
/* 329:487 */     String yAxis = this.m_yAxis;
/* 330:488 */     yAxis = environmentSubstitute(yAxis);
/* 331:    */     
/* 332:490 */     String width = this.m_width;
/* 333:491 */     String height = this.m_height;
/* 334:492 */     int defWidth = 500;
/* 335:493 */     int defHeight = 400;
/* 336:494 */     width = environmentSubstitute(width);
/* 337:495 */     height = environmentSubstitute(height);
/* 338:    */     
/* 339:497 */     defWidth = Integer.parseInt(width);
/* 340:498 */     defHeight = Integer.parseInt(height);
/* 341:    */     try
/* 342:    */     {
/* 343:501 */       return this.m_offscreenRenderer.renderXYScatterPlot(defWidth, defHeight, this.m_offscreenPlotData, xAxis, yAxis, options);
/* 344:    */     }
/* 345:    */     catch (Exception e1)
/* 346:    */     {
/* 347:504 */       throw new WekaException(e1);
/* 348:    */     }
/* 349:    */   }
/* 350:    */   
/* 351:    */   public synchronized void processIncoming(Data data)
/* 352:    */     throws WekaException
/* 353:    */   {
/* 354:516 */     getStepManager().processing();
/* 355:517 */     PlotData2D errorD = (PlotData2D)data.getPayloadElement("visualizableError");
/* 356:    */     
/* 357:519 */     PlotData2D thresholdD = (PlotData2D)data.getPayloadElement("thresholdData");
/* 358:    */     
/* 359:    */ 
/* 360:522 */     getStepManager().logDetailed("Processing " + (errorD != null ? " error data " + errorD.getPlotName() : new StringBuilder().append(" threshold data ").append(thresholdD.getPlotName()).toString()));
/* 361:527 */     if (data.getConnectionName().equals("visualizableError"))
/* 362:    */     {
/* 363:528 */       this.m_plots.clear();
/* 364:529 */       this.m_plots.add(errorD);
/* 365:530 */       this.m_dataIsThresholdData = false;
/* 366:532 */       if (getStepManager().numOutgoingConnectionsOfType("image") > 0)
/* 367:    */       {
/* 368:534 */         setupOffscreenRenderer();
/* 369:535 */         this.m_offscreenPlotData = new ArrayList();
/* 370:536 */         BufferedImage bi = addOffscreenErrorPlot(errorD);
/* 371:537 */         Data imageD = new Data("image");
/* 372:538 */         imageD.setPayloadElement("image", bi);
/* 373:539 */         getStepManager().outputData("image", imageD);
/* 374:    */       }
/* 375:    */     }
/* 376:541 */     else if (data.getConnectionName().equals("thresholdData"))
/* 377:    */     {
/* 378:542 */       if (this.m_plots.size() == 0)
/* 379:    */       {
/* 380:543 */         this.m_plots.add(thresholdD);
/* 381:    */       }
/* 382:    */       else
/* 383:    */       {
/* 384:545 */         if (!((PlotData2D)this.m_plots.get(0)).getPlotInstances().relationName().equals(thresholdD.getPlotInstances().relationName())) {
/* 385:547 */           this.m_plots.clear();
/* 386:    */         }
/* 387:549 */         this.m_plots.add(thresholdD);
/* 388:    */       }
/* 389:551 */       this.m_dataIsThresholdData = true;
/* 390:553 */       if (getStepManager().numOutgoingConnectionsOfType("image") > 0)
/* 391:    */       {
/* 392:555 */         setupOffscreenRenderer();
/* 393:556 */         if ((this.m_offscreenPlotData == null) || (this.m_offscreenPlotData.size() == 0) || (!((Instances)this.m_offscreenPlotData.get(0)).relationName().equals(thresholdD.getPlotInstances().relationName())))
/* 394:    */         {
/* 395:559 */           this.m_offscreenPlotData = new ArrayList();
/* 396:560 */           this.m_thresholdSeriesTitles = new ArrayList();
/* 397:    */         }
/* 398:562 */         BufferedImage bi = addOffscreenThresholdPlot(thresholdD);
/* 399:563 */         Data imageD = new Data("image");
/* 400:564 */         imageD.setPayloadElement("image", bi);
/* 401:565 */         imageD.setPayloadElement("aux_textTitle", thresholdD.getPlotName());
/* 402:    */         
/* 403:567 */         getStepManager().outputData("image", imageD);
/* 404:    */       }
/* 405:    */     }
/* 406:571 */     getStepManager().finished();
/* 407:    */   }
/* 408:    */   
/* 409:    */   public Map<String, String> getInteractiveViewers()
/* 410:    */   {
/* 411:596 */     Map<String, String> views = new LinkedHashMap();
/* 412:598 */     if (this.m_plots.size() > 0) {
/* 413:599 */       views.put("Show chart", "weka.gui.knowledgeflow.steps.ModelPerformanceChartInteractiveView");
/* 414:    */     }
/* 415:603 */     return views;
/* 416:    */   }
/* 417:    */   
/* 418:    */   public List<PlotData2D> getPlots()
/* 419:    */   {
/* 420:612 */     return this.m_plots;
/* 421:    */   }
/* 422:    */   
/* 423:    */   public boolean isDataIsThresholdData()
/* 424:    */   {
/* 425:621 */     return this.m_dataIsThresholdData;
/* 426:    */   }
/* 427:    */   
/* 428:    */   public void clearPlotData()
/* 429:    */   {
/* 430:628 */     this.m_plots.clear();
/* 431:630 */     if (this.m_offscreenPlotData != null) {
/* 432:631 */       this.m_offscreenPlotData.clear();
/* 433:    */     }
/* 434:    */   }
/* 435:    */   
/* 436:    */   public Object retrieveData()
/* 437:    */   {
/* 438:642 */     Object[] onAndOffScreen = new Object[2];
/* 439:643 */     onAndOffScreen[0] = this.m_plots;
/* 440:    */     
/* 441:645 */     onAndOffScreen[1] = Boolean.valueOf(this.m_dataIsThresholdData);
/* 442:    */     
/* 443:647 */     return onAndOffScreen;
/* 444:    */   }
/* 445:    */   
/* 446:    */   public void restoreData(Object data)
/* 447:    */     throws WekaException
/* 448:    */   {
/* 449:659 */     if (!(data instanceof Object[])) {
/* 450:660 */       throw new WekaException("Argument must be a three element array, where the first element holds a list of Plot2D objects, the second a list of Instances objects and the third a boolean - true if the data is threshold data");
/* 451:    */     }
/* 452:665 */     this.m_plots = ((List)((Object[])(Object[])data)[0]);
/* 453:    */     
/* 454:667 */     this.m_dataIsThresholdData = ((Boolean)((Object[])(Object[])data)[1]).booleanValue();
/* 455:668 */     this.m_offscreenPlotData = new ArrayList();
/* 456:    */   }
/* 457:    */   
/* 458:    */   public void stepInit()
/* 459:    */     throws WekaException
/* 460:    */   {}
/* 461:    */   
/* 462:    */   public String getCustomEditorForStep()
/* 463:    */   {
/* 464:691 */     return "weka.gui.knowledgeflow.steps.ModelPerformanceChartStepEditorDialog";
/* 465:    */   }
/* 466:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.ModelPerformanceChart
 * JD-Core Version:    0.7.0.1
 */