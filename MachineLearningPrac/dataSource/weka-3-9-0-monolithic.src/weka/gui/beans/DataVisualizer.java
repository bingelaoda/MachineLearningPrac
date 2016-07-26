/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.GraphicsEnvironment;
/*   6:    */ import java.awt.event.WindowAdapter;
/*   7:    */ import java.awt.event.WindowEvent;
/*   8:    */ import java.awt.image.BufferedImage;
/*   9:    */ import java.beans.EventSetDescriptor;
/*  10:    */ import java.beans.PropertyChangeListener;
/*  11:    */ import java.beans.VetoableChangeListener;
/*  12:    */ import java.beans.beancontext.BeanContext;
/*  13:    */ import java.beans.beancontext.BeanContextChild;
/*  14:    */ import java.beans.beancontext.BeanContextChildSupport;
/*  15:    */ import java.io.BufferedReader;
/*  16:    */ import java.io.FileReader;
/*  17:    */ import java.io.PrintStream;
/*  18:    */ import java.io.Reader;
/*  19:    */ import java.io.Serializable;
/*  20:    */ import java.util.ArrayList;
/*  21:    */ import java.util.Enumeration;
/*  22:    */ import java.util.EventObject;
/*  23:    */ import java.util.List;
/*  24:    */ import java.util.Vector;
/*  25:    */ import javax.swing.JFrame;
/*  26:    */ import javax.swing.JPanel;
/*  27:    */ import weka.core.Attribute;
/*  28:    */ import weka.core.Environment;
/*  29:    */ import weka.core.EnvironmentHandler;
/*  30:    */ import weka.core.Instance;
/*  31:    */ import weka.core.Instances;
/*  32:    */ import weka.core.PluginManager;
/*  33:    */ import weka.gui.Logger;
/*  34:    */ import weka.gui.visualize.PlotData2D;
/*  35:    */ import weka.gui.visualize.VisualizePanel;
/*  36:    */ 
/*  37:    */ public class DataVisualizer
/*  38:    */   extends JPanel
/*  39:    */   implements DataSourceListener, TrainingSetListener, TestSetListener, Visible, UserRequestAcceptor, Serializable, BeanContextChild, HeadlessEventCollector, EnvironmentHandler, BeanCommon, EventConstraints
/*  40:    */ {
/*  41:    */   private static final long serialVersionUID = 1949062132560159028L;
/*  42: 65 */   protected BeanVisual m_visual = new BeanVisual("DataVisualizer", "weka/gui/beans/icons/DefaultDataVisualizer.gif", "weka/gui/beans/icons/DefaultDataVisualizer_animated.gif");
/*  43:    */   protected transient Instances m_visualizeDataSet;
/*  44:    */   protected transient JFrame m_popupFrame;
/*  45: 73 */   protected boolean m_framePoppedUp = false;
/*  46:    */   protected boolean m_design;
/*  47: 83 */   protected transient BeanContext m_beanContext = null;
/*  48:    */   private VisualizePanel m_visPanel;
/*  49:    */   protected List<EventObject> m_headlessEvents;
/*  50: 95 */   protected transient boolean m_processingHeadlessEvents = false;
/*  51: 97 */   protected ArrayList<ImageListener> m_imageListeners = new ArrayList();
/*  52: 99 */   protected List<Object> m_listenees = new ArrayList();
/*  53:104 */   private final Vector<DataSourceListener> m_dataSetListeners = new Vector();
/*  54:    */   protected transient List<Instances> m_offscreenPlotData;
/*  55:    */   protected transient OffscreenChartRenderer m_offscreenRenderer;
/*  56:111 */   protected String m_offscreenRendererName = "Weka Chart Renderer";
/*  57:117 */   protected String m_xAxis = "";
/*  58:123 */   protected String m_yAxis = "";
/*  59:128 */   protected String m_additionalOptions = "";
/*  60:131 */   protected String m_width = "500";
/*  61:134 */   protected String m_height = "400";
/*  62:    */   protected transient Environment m_env;
/*  63:144 */   protected BeanContextChildSupport m_bcSupport = new BeanContextChildSupport(this);
/*  64:    */   
/*  65:    */   public DataVisualizer()
/*  66:    */   {
/*  67:148 */     GraphicsEnvironment.getLocalGraphicsEnvironment();
/*  68:149 */     if (!GraphicsEnvironment.isHeadless()) {
/*  69:150 */       appearanceFinal();
/*  70:    */     } else {
/*  71:152 */       this.m_headlessEvents = new ArrayList();
/*  72:    */     }
/*  73:    */   }
/*  74:    */   
/*  75:    */   public String globalInfo()
/*  76:    */   {
/*  77:162 */     return "Visualize incoming data/training/test sets in a 2D scatter plot.";
/*  78:    */   }
/*  79:    */   
/*  80:    */   protected void appearanceDesign()
/*  81:    */   {
/*  82:166 */     this.m_visPanel = null;
/*  83:167 */     removeAll();
/*  84:168 */     useDefaultVisual();
/*  85:169 */     setLayout(new BorderLayout());
/*  86:170 */     add(this.m_visual, "Center");
/*  87:    */   }
/*  88:    */   
/*  89:    */   protected void appearanceFinal()
/*  90:    */   {
/*  91:174 */     GraphicsEnvironment.getLocalGraphicsEnvironment();
/*  92:    */     
/*  93:176 */     removeAll();
/*  94:177 */     if (!GraphicsEnvironment.isHeadless())
/*  95:    */     {
/*  96:178 */       setLayout(new BorderLayout());
/*  97:179 */       setUpFinal();
/*  98:    */     }
/*  99:    */   }
/* 100:    */   
/* 101:    */   protected void setUpFinal()
/* 102:    */   {
/* 103:184 */     if (this.m_visPanel == null) {
/* 104:185 */       this.m_visPanel = new VisualizePanel();
/* 105:    */     }
/* 106:187 */     add(this.m_visPanel, "Center");
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void acceptTrainingSet(TrainingSetEvent e)
/* 110:    */   {
/* 111:197 */     Instances trainingSet = e.getTrainingSet();
/* 112:198 */     DataSetEvent dse = new DataSetEvent(this, trainingSet);
/* 113:199 */     acceptDataSet(dse);
/* 114:    */   }
/* 115:    */   
/* 116:    */   public void acceptTestSet(TestSetEvent e)
/* 117:    */   {
/* 118:209 */     Instances testSet = e.getTestSet();
/* 119:210 */     DataSetEvent dse = new DataSetEvent(this, testSet);
/* 120:211 */     acceptDataSet(dse);
/* 121:    */   }
/* 122:    */   
/* 123:    */   public synchronized void acceptDataSet(DataSetEvent e)
/* 124:    */   {
/* 125:222 */     if (e.isStructureOnly()) {
/* 126:223 */       return;
/* 127:    */     }
/* 128:225 */     this.m_visualizeDataSet = new Instances(e.getDataSet());
/* 129:226 */     if (this.m_visualizeDataSet.classIndex() < 0) {
/* 130:227 */       this.m_visualizeDataSet.setClassIndex(this.m_visualizeDataSet.numAttributes() - 1);
/* 131:    */     }
/* 132:229 */     if (!this.m_design)
/* 133:    */     {
/* 134:    */       try
/* 135:    */       {
/* 136:231 */         setInstances(this.m_visualizeDataSet);
/* 137:    */       }
/* 138:    */       catch (Exception ex)
/* 139:    */       {
/* 140:233 */         ex.printStackTrace();
/* 141:    */       }
/* 142:    */     }
/* 143:236 */     else if (this.m_headlessEvents != null)
/* 144:    */     {
/* 145:237 */       this.m_headlessEvents = new ArrayList();
/* 146:238 */       this.m_headlessEvents.add(e);
/* 147:    */     }
/* 148:243 */     notifyDataSetListeners(e);
/* 149:    */     
/* 150:245 */     renderOffscreenImage(e);
/* 151:    */   }
/* 152:    */   
/* 153:    */   protected void renderOffscreenImage(DataSetEvent e)
/* 154:    */   {
/* 155:249 */     if (this.m_env == null) {
/* 156:250 */       this.m_env = Environment.getSystemWide();
/* 157:    */     }
/* 158:253 */     if ((this.m_imageListeners.size() > 0) && (!this.m_processingHeadlessEvents))
/* 159:    */     {
/* 160:255 */       setupOffscreenRenderer();
/* 161:    */       
/* 162:257 */       this.m_offscreenPlotData = new ArrayList();
/* 163:258 */       Instances predictedI = e.getDataSet();
/* 164:259 */       if ((predictedI.classIndex() >= 0) && (predictedI.classAttribute().isNominal()))
/* 165:    */       {
/* 166:262 */         Instances[] classes = new Instances[predictedI.numClasses()];
/* 167:263 */         for (int i = 0; i < predictedI.numClasses(); i++)
/* 168:    */         {
/* 169:264 */           classes[i] = new Instances(predictedI, 0);
/* 170:265 */           classes[i].setRelationName(predictedI.classAttribute().value(i));
/* 171:    */         }
/* 172:267 */         for (int i = 0; i < predictedI.numInstances(); i++)
/* 173:    */         {
/* 174:268 */           Instance current = predictedI.instance(i);
/* 175:269 */           classes[((int)current.classValue())].add((Instance)current.copy());
/* 176:    */         }
/* 177:271 */         for (Instances classe : classes) {
/* 178:272 */           this.m_offscreenPlotData.add(classe);
/* 179:    */         }
/* 180:    */       }
/* 181:    */       else
/* 182:    */       {
/* 183:275 */         this.m_offscreenPlotData.add(new Instances(predictedI));
/* 184:    */       }
/* 185:278 */       List<String> options = new ArrayList();
/* 186:279 */       String additional = this.m_additionalOptions;
/* 187:280 */       if ((this.m_additionalOptions != null) && (this.m_additionalOptions.length() > 0)) {
/* 188:    */         try
/* 189:    */         {
/* 190:282 */           additional = this.m_env.substitute(additional);
/* 191:    */         }
/* 192:    */         catch (Exception ex) {}
/* 193:    */       }
/* 194:286 */       if ((additional != null) && (additional.indexOf("-color") < 0))
/* 195:    */       {
/* 196:288 */         if (additional.length() > 0) {
/* 197:289 */           additional = additional + ",";
/* 198:    */         }
/* 199:291 */         if (predictedI.classIndex() >= 0) {
/* 200:292 */           additional = additional + "-color=" + predictedI.classAttribute().name();
/* 201:    */         } else {
/* 202:294 */           additional = additional + "-color=/last";
/* 203:    */         }
/* 204:    */       }
/* 205:297 */       String[] optionsParts = additional.split(",");
/* 206:298 */       for (String p : optionsParts) {
/* 207:299 */         options.add(p.trim());
/* 208:    */       }
/* 209:302 */       String xAxis = this.m_xAxis;
/* 210:    */       try
/* 211:    */       {
/* 212:304 */         xAxis = this.m_env.substitute(xAxis);
/* 213:    */       }
/* 214:    */       catch (Exception ex) {}
/* 215:308 */       String yAxis = this.m_yAxis;
/* 216:    */       try
/* 217:    */       {
/* 218:310 */         yAxis = this.m_env.substitute(yAxis);
/* 219:    */       }
/* 220:    */       catch (Exception ex) {}
/* 221:314 */       String width = this.m_width;
/* 222:315 */       String height = this.m_height;
/* 223:316 */       int defWidth = 500;
/* 224:317 */       int defHeight = 400;
/* 225:    */       try
/* 226:    */       {
/* 227:319 */         width = this.m_env.substitute(width);
/* 228:320 */         height = this.m_env.substitute(height);
/* 229:    */         
/* 230:322 */         defWidth = Integer.parseInt(width);
/* 231:323 */         defHeight = Integer.parseInt(height);
/* 232:    */       }
/* 233:    */       catch (Exception ex) {}
/* 234:    */       try
/* 235:    */       {
/* 236:328 */         BufferedImage osi = this.m_offscreenRenderer.renderXYScatterPlot(defWidth, defHeight, this.m_offscreenPlotData, xAxis, yAxis, options);
/* 237:    */         
/* 238:    */ 
/* 239:331 */         ImageEvent ie = new ImageEvent(this, osi);
/* 240:332 */         notifyImageListeners(ie);
/* 241:    */       }
/* 242:    */       catch (Exception e1)
/* 243:    */       {
/* 244:334 */         e1.printStackTrace();
/* 245:    */       }
/* 246:    */     }
/* 247:    */   }
/* 248:    */   
/* 249:    */   protected void notifyImageListeners(ImageEvent te)
/* 250:    */   {
/* 251:    */     ArrayList<ImageListener> l;
/* 252:347 */     synchronized (this)
/* 253:    */     {
/* 254:348 */       l = (ArrayList)this.m_imageListeners.clone();
/* 255:    */     }
/* 256:350 */     if (l.size() > 0) {
/* 257:351 */       for (int i = 0; i < l.size(); i++) {
/* 258:352 */         ((ImageListener)l.get(i)).acceptImage(te);
/* 259:    */       }
/* 260:    */     }
/* 261:    */   }
/* 262:    */   
/* 263:    */   public List<EventObject> retrieveHeadlessEvents()
/* 264:    */   {
/* 265:365 */     return this.m_headlessEvents;
/* 266:    */   }
/* 267:    */   
/* 268:    */   public void processHeadlessEvents(List<EventObject> headless)
/* 269:    */   {
/* 270:377 */     if (!GraphicsEnvironment.isHeadless())
/* 271:    */     {
/* 272:378 */       this.m_processingHeadlessEvents = true;
/* 273:379 */       for (EventObject e : headless) {
/* 274:380 */         if ((e instanceof DataSetEvent)) {
/* 275:381 */           acceptDataSet((DataSetEvent)e);
/* 276:    */         }
/* 277:    */       }
/* 278:    */     }
/* 279:385 */     this.m_processingHeadlessEvents = false;
/* 280:    */   }
/* 281:    */   
/* 282:    */   public void setVisual(BeanVisual newVisual)
/* 283:    */   {
/* 284:395 */     this.m_visual = newVisual;
/* 285:    */   }
/* 286:    */   
/* 287:    */   public BeanVisual getVisual()
/* 288:    */   {
/* 289:403 */     return this.m_visual;
/* 290:    */   }
/* 291:    */   
/* 292:    */   public void useDefaultVisual()
/* 293:    */   {
/* 294:411 */     this.m_visual.loadIcons("weka/gui/beans/icons/DefaultDataVisualizer.gif", "weka/gui/beans/icons/DefaultDataVisualizer_animated.gif");
/* 295:    */   }
/* 296:    */   
/* 297:    */   public Enumeration<String> enumerateRequests()
/* 298:    */   {
/* 299:422 */     Vector<String> newVector = new Vector(0);
/* 300:423 */     if (this.m_visualizeDataSet != null) {
/* 301:424 */       newVector.addElement("Show plot");
/* 302:    */     }
/* 303:426 */     return newVector.elements();
/* 304:    */   }
/* 305:    */   
/* 306:    */   public void addPropertyChangeListener(String name, PropertyChangeListener pcl)
/* 307:    */   {
/* 308:437 */     this.m_bcSupport.addPropertyChangeListener(name, pcl);
/* 309:    */   }
/* 310:    */   
/* 311:    */   public void removePropertyChangeListener(String name, PropertyChangeListener pcl)
/* 312:    */   {
/* 313:449 */     this.m_bcSupport.removePropertyChangeListener(name, pcl);
/* 314:    */   }
/* 315:    */   
/* 316:    */   public void addVetoableChangeListener(String name, VetoableChangeListener vcl)
/* 317:    */   {
/* 318:460 */     this.m_bcSupport.addVetoableChangeListener(name, vcl);
/* 319:    */   }
/* 320:    */   
/* 321:    */   public void removeVetoableChangeListener(String name, VetoableChangeListener vcl)
/* 322:    */   {
/* 323:472 */     this.m_bcSupport.removeVetoableChangeListener(name, vcl);
/* 324:    */   }
/* 325:    */   
/* 326:    */   public void setBeanContext(BeanContext bc)
/* 327:    */   {
/* 328:482 */     this.m_beanContext = bc;
/* 329:483 */     this.m_design = this.m_beanContext.isDesignTime();
/* 330:484 */     if (this.m_design)
/* 331:    */     {
/* 332:485 */       appearanceDesign();
/* 333:    */     }
/* 334:    */     else
/* 335:    */     {
/* 336:487 */       GraphicsEnvironment.getLocalGraphicsEnvironment();
/* 337:488 */       if (!GraphicsEnvironment.isHeadless()) {
/* 338:489 */         appearanceFinal();
/* 339:    */       }
/* 340:    */     }
/* 341:    */   }
/* 342:    */   
/* 343:    */   public BeanContext getBeanContext()
/* 344:    */   {
/* 345:501 */     return this.m_beanContext;
/* 346:    */   }
/* 347:    */   
/* 348:    */   public void setInstances(Instances inst)
/* 349:    */     throws Exception
/* 350:    */   {
/* 351:512 */     if (this.m_design) {
/* 352:513 */       throw new Exception("This method is not to be used during design time. It is meant to be used if this bean is being used programatically as as stand alone component.");
/* 353:    */     }
/* 354:518 */     this.m_visualizeDataSet = inst;
/* 355:519 */     PlotData2D pd1 = new PlotData2D(this.m_visualizeDataSet);
/* 356:520 */     String relationName = this.m_visualizeDataSet.relationName();
/* 357:521 */     pd1.setPlotName(relationName);
/* 358:    */     try
/* 359:    */     {
/* 360:523 */       this.m_visPanel.setMasterPlot(pd1);
/* 361:    */     }
/* 362:    */     catch (Exception ex)
/* 363:    */     {
/* 364:525 */       System.err.println("Problem setting up visualization (DataVisualizer)");
/* 365:    */       
/* 366:527 */       ex.printStackTrace();
/* 367:    */     }
/* 368:    */   }
/* 369:    */   
/* 370:    */   private void notifyDataSetListeners(DataSetEvent ge)
/* 371:    */   {
/* 372:    */     Vector<DataSourceListener> l;
/* 373:539 */     synchronized (this)
/* 374:    */     {
/* 375:540 */       l = (Vector)this.m_dataSetListeners.clone();
/* 376:    */     }
/* 377:542 */     if (l.size() > 0) {
/* 378:543 */       for (int i = 0; i < l.size(); i++) {
/* 379:544 */         ((DataSourceListener)l.elementAt(i)).acceptDataSet(ge);
/* 380:    */       }
/* 381:    */     }
/* 382:    */   }
/* 383:    */   
/* 384:    */   public void performRequest(String request)
/* 385:    */   {
/* 386:557 */     if (request.compareTo("Show plot") == 0) {
/* 387:    */       try
/* 388:    */       {
/* 389:560 */         if (!this.m_framePoppedUp)
/* 390:    */         {
/* 391:561 */           this.m_framePoppedUp = true;
/* 392:562 */           VisualizePanel vis = new VisualizePanel();
/* 393:563 */           PlotData2D pd1 = new PlotData2D(this.m_visualizeDataSet);
/* 394:    */           
/* 395:565 */           String relationName = this.m_visualizeDataSet.relationName();
/* 396:569 */           if (relationName.startsWith("__"))
/* 397:    */           {
/* 398:570 */             boolean[] connect = new boolean[this.m_visualizeDataSet.numInstances()];
/* 399:571 */             for (int i = 1; i < connect.length; i++) {
/* 400:572 */               connect[i] = true;
/* 401:    */             }
/* 402:574 */             pd1.setConnectPoints(connect);
/* 403:575 */             relationName = relationName.substring(2);
/* 404:    */           }
/* 405:577 */           pd1.setPlotName(relationName);
/* 406:    */           try
/* 407:    */           {
/* 408:579 */             vis.setMasterPlot(pd1);
/* 409:    */           }
/* 410:    */           catch (Exception ex)
/* 411:    */           {
/* 412:581 */             System.err.println("Problem setting up visualization (DataVisualizer)");
/* 413:    */             
/* 414:583 */             ex.printStackTrace();
/* 415:    */           }
/* 416:585 */           final JFrame jf = new JFrame("Visualize");
/* 417:586 */           jf.setSize(800, 600);
/* 418:587 */           jf.getContentPane().setLayout(new BorderLayout());
/* 419:588 */           jf.getContentPane().add(vis, "Center");
/* 420:589 */           jf.addWindowListener(new WindowAdapter()
/* 421:    */           {
/* 422:    */             public void windowClosing(WindowEvent e)
/* 423:    */             {
/* 424:592 */               jf.dispose();
/* 425:593 */               DataVisualizer.this.m_framePoppedUp = false;
/* 426:    */             }
/* 427:595 */           });
/* 428:596 */           jf.setVisible(true);
/* 429:597 */           this.m_popupFrame = jf;
/* 430:    */         }
/* 431:    */         else
/* 432:    */         {
/* 433:599 */           this.m_popupFrame.toFront();
/* 434:    */         }
/* 435:    */       }
/* 436:    */       catch (Exception ex)
/* 437:    */       {
/* 438:602 */         ex.printStackTrace();
/* 439:603 */         this.m_framePoppedUp = false;
/* 440:    */       }
/* 441:    */     } else {
/* 442:606 */       throw new IllegalArgumentException(request + " not supported (DataVisualizer)");
/* 443:    */     }
/* 444:    */   }
/* 445:    */   
/* 446:    */   protected void setupOffscreenRenderer()
/* 447:    */   {
/* 448:612 */     if (this.m_offscreenRenderer == null)
/* 449:    */     {
/* 450:613 */       if ((this.m_offscreenRendererName == null) || (this.m_offscreenRendererName.length() == 0))
/* 451:    */       {
/* 452:615 */         this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/* 453:616 */         return;
/* 454:    */       }
/* 455:619 */       if (this.m_offscreenRendererName.equalsIgnoreCase("weka chart renderer")) {
/* 456:620 */         this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/* 457:    */       } else {
/* 458:    */         try
/* 459:    */         {
/* 460:623 */           Object r = PluginManager.getPluginInstance("weka.gui.beans.OffscreenChartRenderer", this.m_offscreenRendererName);
/* 461:625 */           if ((r != null) && ((r instanceof OffscreenChartRenderer))) {
/* 462:626 */             this.m_offscreenRenderer = ((OffscreenChartRenderer)r);
/* 463:    */           } else {
/* 464:629 */             this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/* 465:    */           }
/* 466:    */         }
/* 467:    */         catch (Exception ex)
/* 468:    */         {
/* 469:633 */           this.m_offscreenRenderer = new WekaOffscreenChartRenderer();
/* 470:    */         }
/* 471:    */       }
/* 472:    */     }
/* 473:    */   }
/* 474:    */   
/* 475:    */   public synchronized void addDataSourceListener(DataSourceListener dsl)
/* 476:    */   {
/* 477:645 */     this.m_dataSetListeners.addElement(dsl);
/* 478:    */   }
/* 479:    */   
/* 480:    */   public synchronized void removeDataSourceListener(DataSourceListener dsl)
/* 481:    */   {
/* 482:654 */     this.m_dataSetListeners.remove(dsl);
/* 483:    */   }
/* 484:    */   
/* 485:    */   public static void main(String[] args)
/* 486:    */   {
/* 487:    */     try
/* 488:    */     {
/* 489:659 */       if (args.length != 1)
/* 490:    */       {
/* 491:660 */         System.err.println("Usage: DataVisualizer <dataset>");
/* 492:661 */         System.exit(1);
/* 493:    */       }
/* 494:663 */       Reader r = new BufferedReader(new FileReader(args[0]));
/* 495:    */       
/* 496:665 */       Instances inst = new Instances(r);
/* 497:666 */       JFrame jf = new JFrame();
/* 498:667 */       jf.getContentPane().setLayout(new BorderLayout());
/* 499:668 */       DataVisualizer as = new DataVisualizer();
/* 500:669 */       as.setInstances(inst);
/* 501:    */       
/* 502:671 */       jf.getContentPane().add(as, "Center");
/* 503:672 */       jf.addWindowListener(new WindowAdapter()
/* 504:    */       {
/* 505:    */         public void windowClosing(WindowEvent e)
/* 506:    */         {
/* 507:675 */           this.val$jf.dispose();
/* 508:676 */           System.exit(0);
/* 509:    */         }
/* 510:678 */       });
/* 511:679 */       jf.setSize(800, 600);
/* 512:680 */       jf.setVisible(true);
/* 513:    */     }
/* 514:    */     catch (Exception ex)
/* 515:    */     {
/* 516:682 */       ex.printStackTrace();
/* 517:683 */       System.err.println(ex.getMessage());
/* 518:    */     }
/* 519:    */   }
/* 520:    */   
/* 521:    */   public void setEnvironment(Environment env)
/* 522:    */   {
/* 523:689 */     this.m_env = env;
/* 524:    */   }
/* 525:    */   
/* 526:    */   public void setOffscreenXAxis(String xAxis)
/* 527:    */   {
/* 528:699 */     this.m_xAxis = xAxis;
/* 529:    */   }
/* 530:    */   
/* 531:    */   public String getOffscreenXAxis()
/* 532:    */   {
/* 533:708 */     return this.m_xAxis;
/* 534:    */   }
/* 535:    */   
/* 536:    */   public void setOffscreenYAxis(String yAxis)
/* 537:    */   {
/* 538:718 */     this.m_yAxis = yAxis;
/* 539:    */   }
/* 540:    */   
/* 541:    */   public String getOffscreenYAxis()
/* 542:    */   {
/* 543:727 */     return this.m_yAxis;
/* 544:    */   }
/* 545:    */   
/* 546:    */   public void setOffscreenWidth(String width)
/* 547:    */   {
/* 548:736 */     this.m_width = width;
/* 549:    */   }
/* 550:    */   
/* 551:    */   public String getOffscreenWidth()
/* 552:    */   {
/* 553:745 */     return this.m_width;
/* 554:    */   }
/* 555:    */   
/* 556:    */   public void setOffscreenHeight(String height)
/* 557:    */   {
/* 558:754 */     this.m_height = height;
/* 559:    */   }
/* 560:    */   
/* 561:    */   public String getOffscreenHeight()
/* 562:    */   {
/* 563:763 */     return this.m_height;
/* 564:    */   }
/* 565:    */   
/* 566:    */   public void setOffscreenRendererName(String rendererName)
/* 567:    */   {
/* 568:773 */     this.m_offscreenRendererName = rendererName;
/* 569:774 */     this.m_offscreenRenderer = null;
/* 570:    */   }
/* 571:    */   
/* 572:    */   public String getOffscreenRendererName()
/* 573:    */   {
/* 574:784 */     return this.m_offscreenRendererName;
/* 575:    */   }
/* 576:    */   
/* 577:    */   public void setOffscreenAdditionalOpts(String additional)
/* 578:    */   {
/* 579:793 */     this.m_additionalOptions = additional;
/* 580:    */   }
/* 581:    */   
/* 582:    */   public String getOffscreenAdditionalOpts()
/* 583:    */   {
/* 584:802 */     return this.m_additionalOptions;
/* 585:    */   }
/* 586:    */   
/* 587:    */   public synchronized void addImageListener(ImageListener cl)
/* 588:    */   {
/* 589:811 */     this.m_imageListeners.add(cl);
/* 590:    */   }
/* 591:    */   
/* 592:    */   public synchronized void removeImageListener(ImageListener cl)
/* 593:    */   {
/* 594:820 */     this.m_imageListeners.remove(cl);
/* 595:    */   }
/* 596:    */   
/* 597:    */   public void setCustomName(String name)
/* 598:    */   {
/* 599:830 */     this.m_visual.setText(name);
/* 600:    */   }
/* 601:    */   
/* 602:    */   public String getCustomName()
/* 603:    */   {
/* 604:841 */     return this.m_visual.getText();
/* 605:    */   }
/* 606:    */   
/* 607:    */   public void stop() {}
/* 608:    */   
/* 609:    */   public boolean isBusy()
/* 610:    */   {
/* 611:859 */     return false;
/* 612:    */   }
/* 613:    */   
/* 614:    */   public void setLog(Logger logger) {}
/* 615:    */   
/* 616:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/* 617:    */   {
/* 618:880 */     return connectionAllowed(esd.getName());
/* 619:    */   }
/* 620:    */   
/* 621:    */   public boolean connectionAllowed(String eventName)
/* 622:    */   {
/* 623:892 */     return (eventName.equals("dataSet")) || (eventName.equals("trainingSet")) || (eventName.equals("testSet"));
/* 624:    */   }
/* 625:    */   
/* 626:    */   public void connectionNotification(String eventName, Object source)
/* 627:    */   {
/* 628:907 */     if (connectionAllowed(eventName)) {
/* 629:908 */       this.m_listenees.add(source);
/* 630:    */     }
/* 631:    */   }
/* 632:    */   
/* 633:    */   public void disconnectionNotification(String eventName, Object source)
/* 634:    */   {
/* 635:922 */     this.m_listenees.remove(source);
/* 636:    */   }
/* 637:    */   
/* 638:    */   public boolean eventGeneratable(String eventName)
/* 639:    */   {
/* 640:935 */     if (this.m_listenees.size() == 0) {
/* 641:936 */       return false;
/* 642:    */     }
/* 643:939 */     boolean ok = false;
/* 644:940 */     for (Object o : this.m_listenees) {
/* 645:941 */       if (((o instanceof EventConstraints)) && (
/* 646:942 */         (((EventConstraints)o).eventGeneratable("dataSet")) || (((EventConstraints)o).eventGeneratable("trainingSet")) || (((EventConstraints)o).eventGeneratable("testSet"))))
/* 647:    */       {
/* 648:945 */         ok = true;
/* 649:946 */         break;
/* 650:    */       }
/* 651:    */     }
/* 652:950 */     return ok;
/* 653:    */   }
/* 654:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.DataVisualizer
 * JD-Core Version:    0.7.0.1
 */