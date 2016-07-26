/*    1:     */ package weka.gui.beans;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Container;
/*    5:     */ import java.awt.GraphicsEnvironment;
/*    6:     */ import java.awt.event.WindowAdapter;
/*    7:     */ import java.awt.event.WindowEvent;
/*    8:     */ import java.awt.image.BufferedImage;
/*    9:     */ import java.beans.EventSetDescriptor;
/*   10:     */ import java.beans.PropertyChangeListener;
/*   11:     */ import java.beans.VetoableChangeListener;
/*   12:     */ import java.beans.beancontext.BeanContext;
/*   13:     */ import java.beans.beancontext.BeanContextChild;
/*   14:     */ import java.beans.beancontext.BeanContextChildSupport;
/*   15:     */ import java.io.BufferedReader;
/*   16:     */ import java.io.FileReader;
/*   17:     */ import java.io.PrintStream;
/*   18:     */ import java.io.Reader;
/*   19:     */ import java.io.Serializable;
/*   20:     */ import java.util.ArrayList;
/*   21:     */ import java.util.Enumeration;
/*   22:     */ import java.util.EventObject;
/*   23:     */ import java.util.List;
/*   24:     */ import java.util.Vector;
/*   25:     */ import javax.swing.JFrame;
/*   26:     */ import javax.swing.JPanel;
/*   27:     */ import weka.core.Attribute;
/*   28:     */ import weka.core.DenseInstance;
/*   29:     */ import weka.core.Environment;
/*   30:     */ import weka.core.EnvironmentHandler;
/*   31:     */ import weka.core.Instance;
/*   32:     */ import weka.core.Instances;
/*   33:     */ import weka.core.PluginManager;
/*   34:     */ import weka.gui.Logger;
/*   35:     */ import weka.gui.visualize.PlotData2D;
/*   36:     */ import weka.gui.visualize.VisualizePanel;
/*   37:     */ 
/*   38:     */ public class ModelPerformanceChart
/*   39:     */   extends JPanel
/*   40:     */   implements ThresholdDataListener, VisualizableErrorListener, Visible, UserRequestAcceptor, EventConstraints, Serializable, BeanContextChild, HeadlessEventCollector, BeanCommon, EnvironmentHandler
/*   41:     */ {
/*   42:     */   private static final long serialVersionUID = -4602034200071195924L;
/*   43:  68 */   protected BeanVisual m_visual = new BeanVisual("ModelPerformanceChart", "weka/gui/beans/icons/ModelPerformanceChart.gif", "weka/gui/beans/icons/ModelPerformanceChart_animated.gif");
/*   44:     */   protected transient PlotData2D m_masterPlot;
/*   45:     */   protected transient List<Instances> m_offscreenPlotData;
/*   46:     */   protected transient List<String> m_thresholdSeriesTitles;
/*   47:     */   protected transient OffscreenChartRenderer m_offscreenRenderer;
/*   48:  80 */   protected String m_offscreenRendererName = "Weka Chart Renderer";
/*   49:  86 */   protected String m_xAxis = "";
/*   50:  92 */   protected String m_yAxis = "";
/*   51:  97 */   protected String m_additionalOptions = "";
/*   52: 100 */   protected String m_width = "500";
/*   53: 103 */   protected String m_height = "400";
/*   54:     */   protected transient JFrame m_popupFrame;
/*   55: 107 */   protected boolean m_framePoppedUp = false;
/*   56:     */   protected List<EventObject> m_headlessEvents;
/*   57: 117 */   protected transient boolean m_processingHeadlessEvents = false;
/*   58: 119 */   protected ArrayList<ImageListener> m_imageListeners = new ArrayList();
/*   59: 121 */   protected List<Object> m_listenees = new ArrayList();
/*   60:     */   protected boolean m_design;
/*   61: 131 */   protected transient BeanContext m_beanContext = null;
/*   62:     */   private transient VisualizePanel m_visPanel;
/*   63:     */   protected transient Environment m_env;
/*   64: 143 */   protected BeanContextChildSupport m_bcSupport = new BeanContextChildSupport(this);
/*   65:     */   
/*   66:     */   public ModelPerformanceChart()
/*   67:     */   {
/*   68: 147 */     useDefaultVisual();
/*   69: 149 */     if (!GraphicsEnvironment.isHeadless()) {
/*   70: 150 */       appearanceFinal();
/*   71:     */     } else {
/*   72: 152 */       this.m_headlessEvents = new ArrayList();
/*   73:     */     }
/*   74:     */   }
/*   75:     */   
/*   76:     */   public String globalInfo()
/*   77:     */   {
/*   78: 162 */     return "Visualize performance charts (such as ROC).";
/*   79:     */   }
/*   80:     */   
/*   81:     */   protected void appearanceDesign()
/*   82:     */   {
/*   83: 166 */     removeAll();
/*   84:     */     
/*   85: 168 */     setLayout(new BorderLayout());
/*   86: 169 */     add(this.m_visual, "Center");
/*   87:     */   }
/*   88:     */   
/*   89:     */   protected void appearanceFinal()
/*   90:     */   {
/*   91: 173 */     removeAll();
/*   92: 174 */     setLayout(new BorderLayout());
/*   93: 175 */     setUpFinal();
/*   94:     */   }
/*   95:     */   
/*   96:     */   protected void setUpFinal()
/*   97:     */   {
/*   98: 179 */     if (this.m_visPanel == null) {
/*   99: 180 */       this.m_visPanel = new VisualizePanel();
/*  100:     */     }
/*  101: 182 */     add(this.m_visPanel, "Center");
/*  102:     */   }
/*  103:     */   
/*  104:     */   protected void setupOffscreenRenderer()
/*  105:     */   {
/*  106: 186 */     if (this.m_offscreenRenderer == null)
/*  107:     */     {
/*  108: 187 */       if ((this.m_offscreenRendererName == null) || (this.m_offscreenRendererName.length() == 0))
/*  109:     */       {
/*  110: 189 */         this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/*  111: 190 */         return;
/*  112:     */       }
/*  113: 193 */       if (this.m_offscreenRendererName.equalsIgnoreCase("weka chart renderer")) {
/*  114: 194 */         this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/*  115:     */       } else {
/*  116:     */         try
/*  117:     */         {
/*  118: 197 */           Object r = PluginManager.getPluginInstance("weka.gui.beans.OffscreenChartRenderer", this.m_offscreenRendererName);
/*  119: 199 */           if ((r != null) && ((r instanceof OffscreenChartRenderer))) {
/*  120: 200 */             this.m_offscreenRenderer = ((OffscreenChartRenderer)r);
/*  121:     */           } else {
/*  122: 203 */             this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/*  123:     */           }
/*  124:     */         }
/*  125:     */         catch (Exception ex)
/*  126:     */         {
/*  127: 207 */           this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/*  128:     */         }
/*  129:     */       }
/*  130:     */     }
/*  131:     */   }
/*  132:     */   
/*  133:     */   public synchronized void acceptDataSet(ThresholdDataEvent e)
/*  134:     */   {
/*  135: 220 */     if (this.m_env == null) {
/*  136: 221 */       this.m_env = Environment.getSystemWide();
/*  137:     */     }
/*  138: 224 */     if (!GraphicsEnvironment.isHeadless())
/*  139:     */     {
/*  140: 225 */       if (this.m_visPanel == null) {
/*  141: 226 */         this.m_visPanel = new VisualizePanel();
/*  142:     */       }
/*  143: 228 */       if (this.m_masterPlot == null) {
/*  144: 229 */         this.m_masterPlot = e.getDataSet();
/*  145:     */       }
/*  146:     */       try
/*  147:     */       {
/*  148: 233 */         if (!this.m_masterPlot.getPlotInstances().relationName().equals(e.getDataSet().getPlotInstances().relationName()))
/*  149:     */         {
/*  150: 237 */           this.m_masterPlot = e.getDataSet();
/*  151: 238 */           this.m_visPanel.setMasterPlot(this.m_masterPlot);
/*  152: 239 */           this.m_visPanel.validate();
/*  153: 240 */           this.m_visPanel.repaint();
/*  154:     */         }
/*  155:     */         else
/*  156:     */         {
/*  157: 243 */           this.m_visPanel.addPlot(e.getDataSet());
/*  158: 244 */           this.m_visPanel.validate();
/*  159: 245 */           this.m_visPanel.repaint();
/*  160:     */         }
/*  161: 247 */         this.m_visPanel.setXIndex(4);
/*  162: 248 */         this.m_visPanel.setYIndex(5);
/*  163:     */       }
/*  164:     */       catch (Exception ex)
/*  165:     */       {
/*  166: 250 */         System.err.println("Problem setting up visualization (ModelPerformanceChart)");
/*  167:     */         
/*  168: 252 */         ex.printStackTrace();
/*  169:     */       }
/*  170:     */     }
/*  171:     */     else
/*  172:     */     {
/*  173: 255 */       this.m_headlessEvents.add(e);
/*  174:     */     }
/*  175: 258 */     if ((this.m_imageListeners.size() > 0) && (!this.m_processingHeadlessEvents))
/*  176:     */     {
/*  177: 260 */       setupOffscreenRenderer();
/*  178: 262 */       if ((this.m_offscreenPlotData == null) || (!((Instances)this.m_offscreenPlotData.get(0)).relationName().equals(e.getDataSet().getPlotInstances().relationName())))
/*  179:     */       {
/*  180: 265 */         this.m_offscreenPlotData = new ArrayList();
/*  181: 266 */         this.m_thresholdSeriesTitles = new ArrayList();
/*  182:     */       }
/*  183: 268 */       this.m_offscreenPlotData.add(e.getDataSet().getPlotInstances());
/*  184: 269 */       this.m_thresholdSeriesTitles.add(e.getDataSet().getPlotName());
/*  185: 270 */       List<String> options = new ArrayList();
/*  186:     */       
/*  187: 272 */       String additional = "-color=/last";
/*  188: 273 */       if ((this.m_additionalOptions != null) && (this.m_additionalOptions.length() > 0))
/*  189:     */       {
/*  190: 274 */         additional = this.m_additionalOptions;
/*  191:     */         try
/*  192:     */         {
/*  193: 276 */           additional = this.m_env.substitute(additional);
/*  194:     */         }
/*  195:     */         catch (Exception ex) {}
/*  196:     */       }
/*  197: 280 */       String[] optsParts = additional.split(",");
/*  198: 281 */       for (String p : optsParts) {
/*  199: 282 */         options.add(p.trim());
/*  200:     */       }
/*  201: 285 */       String xAxis = "False Positive Rate";
/*  202: 286 */       if ((this.m_xAxis != null) && (this.m_xAxis.length() > 0))
/*  203:     */       {
/*  204: 287 */         xAxis = this.m_xAxis;
/*  205:     */         try
/*  206:     */         {
/*  207: 289 */           xAxis = this.m_env.substitute(xAxis);
/*  208:     */         }
/*  209:     */         catch (Exception ex) {}
/*  210:     */       }
/*  211: 293 */       String yAxis = "True Positive Rate";
/*  212: 294 */       if ((this.m_yAxis != null) && (this.m_yAxis.length() > 0))
/*  213:     */       {
/*  214: 295 */         yAxis = this.m_yAxis;
/*  215:     */         try
/*  216:     */         {
/*  217: 297 */           yAxis = this.m_env.substitute(yAxis);
/*  218:     */         }
/*  219:     */         catch (Exception ex) {}
/*  220:     */       }
/*  221: 302 */       String width = this.m_width;
/*  222: 303 */       String height = this.m_height;
/*  223: 304 */       int defWidth = 500;
/*  224: 305 */       int defHeight = 400;
/*  225:     */       try
/*  226:     */       {
/*  227: 307 */         width = this.m_env.substitute(width);
/*  228: 308 */         height = this.m_env.substitute(height);
/*  229:     */         
/*  230: 310 */         defWidth = Integer.parseInt(width);
/*  231: 311 */         defHeight = Integer.parseInt(height);
/*  232:     */       }
/*  233:     */       catch (Exception ex) {}
/*  234:     */       try
/*  235:     */       {
/*  236: 316 */         List<Instances> series = new ArrayList();
/*  237: 317 */         for (int i = 0; i < this.m_offscreenPlotData.size(); i++)
/*  238:     */         {
/*  239: 318 */           Instances temp = new Instances((Instances)this.m_offscreenPlotData.get(i));
/*  240:     */           
/*  241:     */ 
/*  242: 321 */           temp.setRelationName((String)this.m_thresholdSeriesTitles.get(i));
/*  243: 322 */           series.add(temp);
/*  244:     */         }
/*  245: 324 */         BufferedImage osi = this.m_offscreenRenderer.renderXYLineChart(defWidth, defHeight, series, xAxis, yAxis, options);
/*  246:     */         
/*  247:     */ 
/*  248: 327 */         ImageEvent ie = new ImageEvent(this, osi);
/*  249: 328 */         notifyImageListeners(ie);
/*  250:     */       }
/*  251:     */       catch (Exception e1)
/*  252:     */       {
/*  253: 330 */         e1.printStackTrace();
/*  254:     */       }
/*  255:     */     }
/*  256:     */   }
/*  257:     */   
/*  258:     */   public synchronized void acceptDataSet(VisualizableErrorEvent e)
/*  259:     */   {
/*  260: 342 */     if (this.m_env == null) {
/*  261: 343 */       this.m_env = Environment.getSystemWide();
/*  262:     */     }
/*  263: 346 */     if (!GraphicsEnvironment.isHeadless())
/*  264:     */     {
/*  265: 347 */       if (this.m_visPanel == null) {
/*  266: 348 */         this.m_visPanel = new VisualizePanel();
/*  267:     */       }
/*  268: 351 */       this.m_masterPlot = e.getDataSet();
/*  269:     */       try
/*  270:     */       {
/*  271: 354 */         this.m_visPanel.setMasterPlot(this.m_masterPlot);
/*  272:     */       }
/*  273:     */       catch (Exception ex)
/*  274:     */       {
/*  275: 356 */         System.err.println("Problem setting up visualization (ModelPerformanceChart)");
/*  276:     */         
/*  277: 358 */         ex.printStackTrace();
/*  278:     */       }
/*  279: 360 */       this.m_visPanel.validate();
/*  280: 361 */       this.m_visPanel.repaint();
/*  281:     */     }
/*  282:     */     else
/*  283:     */     {
/*  284: 363 */       this.m_headlessEvents = new ArrayList();
/*  285: 364 */       this.m_headlessEvents.add(e);
/*  286:     */     }
/*  287: 367 */     if ((this.m_imageListeners.size() > 0) && (!this.m_processingHeadlessEvents))
/*  288:     */     {
/*  289: 369 */       setupOffscreenRenderer();
/*  290:     */       
/*  291: 371 */       this.m_offscreenPlotData = new ArrayList();
/*  292: 372 */       Instances predictedI = e.getDataSet().getPlotInstances();
/*  293: 373 */       if (predictedI.classAttribute().isNominal())
/*  294:     */       {
/*  295: 382 */         ArrayList<Attribute> atts = new ArrayList();
/*  296: 383 */         for (int i = 0; i < predictedI.numAttributes(); i++) {
/*  297: 384 */           atts.add((Attribute)predictedI.attribute(i).copy());
/*  298:     */         }
/*  299: 386 */         atts.add(new Attribute("@@size@@"));
/*  300: 387 */         Instances newInsts = new Instances(predictedI.relationName(), atts, predictedI.numInstances());
/*  301:     */         
/*  302: 389 */         newInsts.setClassIndex(predictedI.classIndex());
/*  303: 391 */         for (int i = 0; i < predictedI.numInstances(); i++)
/*  304:     */         {
/*  305: 392 */           double[] vals = new double[newInsts.numAttributes()];
/*  306: 393 */           for (int j = 0; j < predictedI.numAttributes(); j++) {
/*  307: 394 */             vals[j] = predictedI.instance(i).value(j);
/*  308:     */           }
/*  309: 396 */           vals[(vals.length - 1)] = 2.0D;
/*  310: 397 */           Instance ni = new DenseInstance(1.0D, vals);
/*  311: 398 */           newInsts.add(ni);
/*  312:     */         }
/*  313: 402 */         Instances[] classes = new Instances[newInsts.numClasses()];
/*  314: 403 */         for (int i = 0; i < newInsts.numClasses(); i++)
/*  315:     */         {
/*  316: 404 */           classes[i] = new Instances(newInsts, 0);
/*  317: 405 */           classes[i].setRelationName(newInsts.classAttribute().value(i));
/*  318:     */         }
/*  319: 407 */         Instances errors = new Instances(newInsts, 0);
/*  320: 408 */         int actualClass = newInsts.classIndex();
/*  321: 409 */         for (int i = 0; i < newInsts.numInstances(); i++)
/*  322:     */         {
/*  323: 410 */           Instance current = newInsts.instance(i);
/*  324: 411 */           classes[((int)current.classValue())].add((Instance)current.copy());
/*  325: 413 */           if (current.value(actualClass) != current.value(actualClass - 1))
/*  326:     */           {
/*  327: 414 */             Instance toAdd = (Instance)current.copy();
/*  328:     */             
/*  329:     */ 
/*  330: 417 */             toAdd.setValue(toAdd.numAttributes() - 1, 5.0D);
/*  331:     */             
/*  332:     */ 
/*  333:     */ 
/*  334:     */ 
/*  335: 422 */             double actualClassV = toAdd.value(actualClass);
/*  336: 423 */             double predictedClassV = toAdd.value(actualClass - 1);
/*  337: 424 */             toAdd.setValue(actualClass, predictedClassV);
/*  338: 425 */             toAdd.setValue(actualClass - 1, actualClassV);
/*  339:     */             
/*  340: 427 */             errors.add(toAdd);
/*  341:     */           }
/*  342:     */         }
/*  343: 431 */         errors.setRelationName("Errors");
/*  344: 432 */         this.m_offscreenPlotData.add(errors);
/*  345: 434 */         for (Instances classe : classes) {
/*  346: 435 */           this.m_offscreenPlotData.add(classe);
/*  347:     */         }
/*  348:     */       }
/*  349:     */       else
/*  350:     */       {
/*  351: 441 */         ArrayList<Attribute> atts = new ArrayList();
/*  352: 442 */         for (int i = 0; i < predictedI.numAttributes(); i++) {
/*  353: 443 */           atts.add((Attribute)predictedI.attribute(i).copy());
/*  354:     */         }
/*  355: 445 */         atts.add(new Attribute("@@size@@"));
/*  356: 446 */         Instances newInsts = new Instances(predictedI.relationName(), atts, predictedI.numInstances());
/*  357:     */         
/*  358:     */ 
/*  359: 449 */         int[] shapeSizes = e.getDataSet().getShapeSize();
/*  360: 451 */         for (int i = 0; i < predictedI.numInstances(); i++)
/*  361:     */         {
/*  362: 452 */           double[] vals = new double[newInsts.numAttributes()];
/*  363: 453 */           for (int j = 0; j < predictedI.numAttributes(); j++) {
/*  364: 454 */             vals[j] = predictedI.instance(i).value(j);
/*  365:     */           }
/*  366: 456 */           vals[(vals.length - 1)] = shapeSizes[i];
/*  367: 457 */           Instance ni = new DenseInstance(1.0D, vals);
/*  368: 458 */           newInsts.add(ni);
/*  369:     */         }
/*  370: 460 */         newInsts.setRelationName(predictedI.classAttribute().name());
/*  371: 461 */         this.m_offscreenPlotData.add(newInsts);
/*  372:     */       }
/*  373: 464 */       List<String> options = new ArrayList();
/*  374:     */       
/*  375: 466 */       String additional = "-color=" + predictedI.classAttribute().name() + ",-hasErrors";
/*  376: 468 */       if ((this.m_additionalOptions != null) && (this.m_additionalOptions.length() > 0))
/*  377:     */       {
/*  378: 469 */         additional = additional + "," + this.m_additionalOptions;
/*  379:     */         try
/*  380:     */         {
/*  381: 471 */           additional = this.m_env.substitute(additional);
/*  382:     */         }
/*  383:     */         catch (Exception ex) {}
/*  384:     */       }
/*  385: 475 */       String[] optionsParts = additional.split(",");
/*  386: 476 */       for (String p : optionsParts) {
/*  387: 477 */         options.add(p.trim());
/*  388:     */       }
/*  389: 481 */       options.add("-shapeSize=@@size@@");
/*  390:     */       
/*  391:     */ 
/*  392: 484 */       String xAxis = this.m_xAxis;
/*  393:     */       try
/*  394:     */       {
/*  395: 486 */         xAxis = this.m_env.substitute(xAxis);
/*  396:     */       }
/*  397:     */       catch (Exception ex) {}
/*  398: 490 */       String yAxis = this.m_yAxis;
/*  399:     */       try
/*  400:     */       {
/*  401: 492 */         yAxis = this.m_env.substitute(yAxis);
/*  402:     */       }
/*  403:     */       catch (Exception ex) {}
/*  404: 496 */       String width = this.m_width;
/*  405: 497 */       String height = this.m_height;
/*  406: 498 */       int defWidth = 500;
/*  407: 499 */       int defHeight = 400;
/*  408:     */       try
/*  409:     */       {
/*  410: 501 */         width = this.m_env.substitute(width);
/*  411: 502 */         height = this.m_env.substitute(height);
/*  412:     */         
/*  413: 504 */         defWidth = Integer.parseInt(width);
/*  414: 505 */         defHeight = Integer.parseInt(height);
/*  415:     */       }
/*  416:     */       catch (Exception ex) {}
/*  417:     */       try
/*  418:     */       {
/*  419: 510 */         BufferedImage osi = this.m_offscreenRenderer.renderXYScatterPlot(defWidth, defHeight, this.m_offscreenPlotData, xAxis, yAxis, options);
/*  420:     */         
/*  421:     */ 
/*  422: 513 */         ImageEvent ie = new ImageEvent(this, osi);
/*  423: 514 */         notifyImageListeners(ie);
/*  424:     */       }
/*  425:     */       catch (Exception e1)
/*  426:     */       {
/*  427: 516 */         e1.printStackTrace();
/*  428:     */       }
/*  429:     */     }
/*  430:     */   }
/*  431:     */   
/*  432:     */   private void notifyImageListeners(ImageEvent te)
/*  433:     */   {
/*  434:     */     ArrayList<ImageListener> l;
/*  435: 529 */     synchronized (this)
/*  436:     */     {
/*  437: 530 */       l = (ArrayList)this.m_imageListeners.clone();
/*  438:     */     }
/*  439: 532 */     if (l.size() > 0) {
/*  440: 533 */       for (int i = 0; i < l.size(); i++) {
/*  441: 534 */         ((ImageListener)l.get(i)).acceptImage(te);
/*  442:     */       }
/*  443:     */     }
/*  444:     */   }
/*  445:     */   
/*  446:     */   public List<EventObject> retrieveHeadlessEvents()
/*  447:     */   {
/*  448: 547 */     return this.m_headlessEvents;
/*  449:     */   }
/*  450:     */   
/*  451:     */   public void processHeadlessEvents(List<EventObject> headless)
/*  452:     */   {
/*  453: 560 */     if (!GraphicsEnvironment.isHeadless())
/*  454:     */     {
/*  455: 561 */       this.m_processingHeadlessEvents = true;
/*  456: 562 */       for (EventObject e : headless) {
/*  457: 563 */         if ((e instanceof ThresholdDataEvent)) {
/*  458: 564 */           acceptDataSet((ThresholdDataEvent)e);
/*  459: 565 */         } else if ((e instanceof VisualizableErrorEvent)) {
/*  460: 566 */           acceptDataSet((VisualizableErrorEvent)e);
/*  461:     */         }
/*  462:     */       }
/*  463:     */     }
/*  464: 570 */     this.m_processingHeadlessEvents = false;
/*  465:     */   }
/*  466:     */   
/*  467:     */   public void setVisual(BeanVisual newVisual)
/*  468:     */   {
/*  469: 580 */     this.m_visual = newVisual;
/*  470:     */   }
/*  471:     */   
/*  472:     */   public BeanVisual getVisual()
/*  473:     */   {
/*  474: 588 */     return this.m_visual;
/*  475:     */   }
/*  476:     */   
/*  477:     */   public void useDefaultVisual()
/*  478:     */   {
/*  479: 596 */     this.m_visual.loadIcons("weka/gui/beans/icons/ModelPerformanceChart.gif", "weka/gui/beans/icons/ModelPerformanceChart_animated.gif");
/*  480:     */   }
/*  481:     */   
/*  482:     */   public Enumeration<String> enumerateRequests()
/*  483:     */   {
/*  484: 607 */     Vector<String> newVector = new Vector(0);
/*  485: 608 */     if (this.m_masterPlot != null)
/*  486:     */     {
/*  487: 609 */       newVector.addElement("Show chart");
/*  488: 610 */       newVector.addElement("?Clear all plots");
/*  489:     */     }
/*  490: 612 */     return newVector.elements();
/*  491:     */   }
/*  492:     */   
/*  493:     */   public void addPropertyChangeListener(String name, PropertyChangeListener pcl)
/*  494:     */   {
/*  495: 623 */     this.m_bcSupport.addPropertyChangeListener(name, pcl);
/*  496:     */   }
/*  497:     */   
/*  498:     */   public void removePropertyChangeListener(String name, PropertyChangeListener pcl)
/*  499:     */   {
/*  500: 635 */     this.m_bcSupport.removePropertyChangeListener(name, pcl);
/*  501:     */   }
/*  502:     */   
/*  503:     */   public void addVetoableChangeListener(String name, VetoableChangeListener vcl)
/*  504:     */   {
/*  505: 646 */     this.m_bcSupport.addVetoableChangeListener(name, vcl);
/*  506:     */   }
/*  507:     */   
/*  508:     */   public void removeVetoableChangeListener(String name, VetoableChangeListener vcl)
/*  509:     */   {
/*  510: 658 */     this.m_bcSupport.removeVetoableChangeListener(name, vcl);
/*  511:     */   }
/*  512:     */   
/*  513:     */   public void setBeanContext(BeanContext bc)
/*  514:     */   {
/*  515: 668 */     this.m_beanContext = bc;
/*  516: 669 */     this.m_design = this.m_beanContext.isDesignTime();
/*  517: 670 */     if (this.m_design) {
/*  518: 671 */       appearanceDesign();
/*  519: 673 */     } else if (!GraphicsEnvironment.isHeadless()) {
/*  520: 674 */       appearanceFinal();
/*  521:     */     }
/*  522:     */   }
/*  523:     */   
/*  524:     */   public BeanContext getBeanContext()
/*  525:     */   {
/*  526: 686 */     return this.m_beanContext;
/*  527:     */   }
/*  528:     */   
/*  529:     */   public void performRequest(String request)
/*  530:     */   {
/*  531: 697 */     if (request.compareTo("Show chart") == 0)
/*  532:     */     {
/*  533:     */       try
/*  534:     */       {
/*  535: 700 */         if (!this.m_framePoppedUp)
/*  536:     */         {
/*  537: 701 */           this.m_framePoppedUp = true;
/*  538:     */           
/*  539: 703 */           final JFrame jf = new JFrame("Model Performance Chart");
/*  540:     */           
/*  541: 705 */           jf.setSize(800, 600);
/*  542: 706 */           jf.getContentPane().setLayout(new BorderLayout());
/*  543: 707 */           jf.getContentPane().add(this.m_visPanel, "Center");
/*  544: 708 */           jf.addWindowListener(new WindowAdapter()
/*  545:     */           {
/*  546:     */             public void windowClosing(WindowEvent e)
/*  547:     */             {
/*  548: 711 */               jf.dispose();
/*  549: 712 */               ModelPerformanceChart.this.m_framePoppedUp = false;
/*  550:     */             }
/*  551: 714 */           });
/*  552: 715 */           jf.setVisible(true);
/*  553: 716 */           this.m_popupFrame = jf;
/*  554:     */         }
/*  555:     */         else
/*  556:     */         {
/*  557: 718 */           this.m_popupFrame.toFront();
/*  558:     */         }
/*  559:     */       }
/*  560:     */       catch (Exception ex)
/*  561:     */       {
/*  562: 721 */         ex.printStackTrace();
/*  563: 722 */         this.m_framePoppedUp = false;
/*  564:     */       }
/*  565:     */     }
/*  566: 724 */     else if (request.equals("Clear all plots"))
/*  567:     */     {
/*  568: 725 */       this.m_visPanel.removeAllPlots();
/*  569: 726 */       this.m_visPanel.validate();
/*  570: 727 */       this.m_visPanel.repaint();
/*  571: 728 */       this.m_visPanel = null;
/*  572: 729 */       this.m_masterPlot = null;
/*  573: 730 */       this.m_offscreenPlotData = null;
/*  574:     */     }
/*  575:     */     else
/*  576:     */     {
/*  577: 732 */       throw new IllegalArgumentException(request + " not supported (Model Performance Chart)");
/*  578:     */     }
/*  579:     */   }
/*  580:     */   
/*  581:     */   public static void main(String[] args)
/*  582:     */   {
/*  583:     */     try
/*  584:     */     {
/*  585: 739 */       if (args.length != 1)
/*  586:     */       {
/*  587: 740 */         System.err.println("Usage: ModelPerformanceChart <dataset>");
/*  588: 741 */         System.exit(1);
/*  589:     */       }
/*  590: 743 */       Reader r = new BufferedReader(new FileReader(args[0]));
/*  591:     */       
/*  592: 745 */       Instances inst = new Instances(r);
/*  593: 746 */       JFrame jf = new JFrame();
/*  594: 747 */       jf.getContentPane().setLayout(new BorderLayout());
/*  595: 748 */       ModelPerformanceChart as = new ModelPerformanceChart();
/*  596: 749 */       PlotData2D pd = new PlotData2D(inst);
/*  597: 750 */       pd.setPlotName(inst.relationName());
/*  598: 751 */       ThresholdDataEvent roc = new ThresholdDataEvent(as, pd);
/*  599: 752 */       as.acceptDataSet(roc);
/*  600:     */       
/*  601: 754 */       jf.getContentPane().add(as, "Center");
/*  602: 755 */       jf.addWindowListener(new WindowAdapter()
/*  603:     */       {
/*  604:     */         public void windowClosing(WindowEvent e)
/*  605:     */         {
/*  606: 758 */           this.val$jf.dispose();
/*  607: 759 */           System.exit(0);
/*  608:     */         }
/*  609: 761 */       });
/*  610: 762 */       jf.setSize(800, 600);
/*  611: 763 */       jf.setVisible(true);
/*  612:     */     }
/*  613:     */     catch (Exception ex)
/*  614:     */     {
/*  615: 765 */       ex.printStackTrace();
/*  616: 766 */       System.err.println(ex.getMessage());
/*  617:     */     }
/*  618:     */   }
/*  619:     */   
/*  620:     */   public void setCustomName(String name)
/*  621:     */   {
/*  622: 777 */     this.m_visual.setText(name);
/*  623:     */   }
/*  624:     */   
/*  625:     */   public String getCustomName()
/*  626:     */   {
/*  627: 787 */     return this.m_visual.getText();
/*  628:     */   }
/*  629:     */   
/*  630:     */   public void stop() {}
/*  631:     */   
/*  632:     */   public boolean isBusy()
/*  633:     */   {
/*  634: 805 */     return false;
/*  635:     */   }
/*  636:     */   
/*  637:     */   public synchronized void addImageListener(ImageListener cl)
/*  638:     */   {
/*  639: 814 */     this.m_imageListeners.add(cl);
/*  640:     */   }
/*  641:     */   
/*  642:     */   public synchronized void removeImageListener(ImageListener cl)
/*  643:     */   {
/*  644: 823 */     this.m_imageListeners.remove(cl);
/*  645:     */   }
/*  646:     */   
/*  647:     */   public void setLog(Logger logger) {}
/*  648:     */   
/*  649:     */   public boolean connectionAllowed(EventSetDescriptor esd)
/*  650:     */   {
/*  651: 844 */     return connectionAllowed(esd.getName());
/*  652:     */   }
/*  653:     */   
/*  654:     */   public boolean connectionAllowed(String eventName)
/*  655:     */   {
/*  656: 856 */     return (eventName.equals("thresholdData")) || (eventName.equals("visualizableError"));
/*  657:     */   }
/*  658:     */   
/*  659:     */   public void connectionNotification(String eventName, Object source)
/*  660:     */   {
/*  661: 871 */     if (connectionAllowed(eventName)) {
/*  662: 872 */       this.m_listenees.add(source);
/*  663:     */     }
/*  664:     */   }
/*  665:     */   
/*  666:     */   public void disconnectionNotification(String eventName, Object source)
/*  667:     */   {
/*  668: 886 */     this.m_listenees.remove(source);
/*  669:     */   }
/*  670:     */   
/*  671:     */   public boolean eventGeneratable(String eventName)
/*  672:     */   {
/*  673: 899 */     if (this.m_listenees.size() == 0) {
/*  674: 900 */       return false;
/*  675:     */     }
/*  676: 903 */     boolean ok = false;
/*  677: 904 */     for (Object o : this.m_listenees) {
/*  678: 905 */       if (((o instanceof EventConstraints)) && (
/*  679: 906 */         (((EventConstraints)o).eventGeneratable("thresholdData")) || (((EventConstraints)o).eventGeneratable("visualizableError"))))
/*  680:     */       {
/*  681: 908 */         ok = true;
/*  682: 909 */         break;
/*  683:     */       }
/*  684:     */     }
/*  685: 914 */     return ok;
/*  686:     */   }
/*  687:     */   
/*  688:     */   public void setEnvironment(Environment env)
/*  689:     */   {
/*  690: 919 */     this.m_env = env;
/*  691:     */   }
/*  692:     */   
/*  693:     */   public void setOffscreenXAxis(String xAxis)
/*  694:     */   {
/*  695: 929 */     this.m_xAxis = xAxis;
/*  696:     */   }
/*  697:     */   
/*  698:     */   public String getOffscreenXAxis()
/*  699:     */   {
/*  700: 938 */     return this.m_xAxis;
/*  701:     */   }
/*  702:     */   
/*  703:     */   public void setOffscreenYAxis(String yAxis)
/*  704:     */   {
/*  705: 948 */     this.m_yAxis = yAxis;
/*  706:     */   }
/*  707:     */   
/*  708:     */   public String getOffscreenYAxis()
/*  709:     */   {
/*  710: 957 */     return this.m_yAxis;
/*  711:     */   }
/*  712:     */   
/*  713:     */   public void setOffscreenWidth(String width)
/*  714:     */   {
/*  715: 966 */     this.m_width = width;
/*  716:     */   }
/*  717:     */   
/*  718:     */   public String getOffscreenWidth()
/*  719:     */   {
/*  720: 975 */     return this.m_width;
/*  721:     */   }
/*  722:     */   
/*  723:     */   public void setOffscreenHeight(String height)
/*  724:     */   {
/*  725: 984 */     this.m_height = height;
/*  726:     */   }
/*  727:     */   
/*  728:     */   public String getOffscreenHeight()
/*  729:     */   {
/*  730: 993 */     return this.m_height;
/*  731:     */   }
/*  732:     */   
/*  733:     */   public void setOffscreenRendererName(String rendererName)
/*  734:     */   {
/*  735:1003 */     this.m_offscreenRendererName = rendererName;
/*  736:1004 */     this.m_offscreenRenderer = null;
/*  737:     */   }
/*  738:     */   
/*  739:     */   public String getOffscreenRendererName()
/*  740:     */   {
/*  741:1014 */     return this.m_offscreenRendererName;
/*  742:     */   }
/*  743:     */   
/*  744:     */   public void setOffscreenAdditionalOpts(String additional)
/*  745:     */   {
/*  746:1023 */     this.m_additionalOptions = additional;
/*  747:     */   }
/*  748:     */   
/*  749:     */   public String getOffscreenAdditionalOpts()
/*  750:     */   {
/*  751:1032 */     return this.m_additionalOptions;
/*  752:     */   }
/*  753:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ModelPerformanceChart
 * JD-Core Version:    0.7.0.1
 */