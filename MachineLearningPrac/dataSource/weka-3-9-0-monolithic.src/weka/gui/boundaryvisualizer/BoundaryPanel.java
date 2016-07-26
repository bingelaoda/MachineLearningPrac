/*    1:     */ package weka.gui.boundaryvisualizer;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Color;
/*    5:     */ import java.awt.Container;
/*    6:     */ import java.awt.Dimension;
/*    7:     */ import java.awt.Graphics;
/*    8:     */ import java.awt.Graphics2D;
/*    9:     */ import java.awt.Image;
/*   10:     */ import java.awt.RenderingHints;
/*   11:     */ import java.awt.event.ActionEvent;
/*   12:     */ import java.awt.event.ActionListener;
/*   13:     */ import java.awt.event.MouseEvent;
/*   14:     */ import java.awt.event.MouseListener;
/*   15:     */ import java.awt.event.WindowAdapter;
/*   16:     */ import java.awt.event.WindowEvent;
/*   17:     */ import java.awt.image.BufferedImage;
/*   18:     */ import java.io.BufferedReader;
/*   19:     */ import java.io.File;
/*   20:     */ import java.io.FileInputStream;
/*   21:     */ import java.io.FileReader;
/*   22:     */ import java.io.ObjectInputStream;
/*   23:     */ import java.io.PrintStream;
/*   24:     */ import java.io.Reader;
/*   25:     */ import java.util.ArrayList;
/*   26:     */ import java.util.Iterator;
/*   27:     */ import java.util.Locale;
/*   28:     */ import java.util.Random;
/*   29:     */ import java.util.Vector;
/*   30:     */ import javax.imageio.IIOImage;
/*   31:     */ import javax.imageio.ImageIO;
/*   32:     */ import javax.imageio.ImageWriteParam;
/*   33:     */ import javax.imageio.ImageWriter;
/*   34:     */ import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
/*   35:     */ import javax.imageio.stream.ImageOutputStream;
/*   36:     */ import javax.swing.JFrame;
/*   37:     */ import javax.swing.JOptionPane;
/*   38:     */ import javax.swing.JPanel;
/*   39:     */ import javax.swing.ToolTipManager;
/*   40:     */ import weka.classifiers.AbstractClassifier;
/*   41:     */ import weka.classifiers.Classifier;
/*   42:     */ import weka.core.Attribute;
/*   43:     */ import weka.core.DenseInstance;
/*   44:     */ import weka.core.Instance;
/*   45:     */ import weka.core.Instances;
/*   46:     */ import weka.core.Utils;
/*   47:     */ 
/*   48:     */ public class BoundaryPanel
/*   49:     */   extends JPanel
/*   50:     */ {
/*   51:     */   private static final long serialVersionUID = -8499445518744770458L;
/*   52:  77 */   public static final Color[] DEFAULT_COLORS = { Color.red, Color.green, Color.blue, new Color(0, 255, 255), new Color(255, 0, 255), new Color(255, 255, 0), new Color(255, 255, 255), new Color(0, 0, 0) };
/*   53:     */   public static final double REMOVE_POINT_RADIUS = 7.0D;
/*   54:  89 */   protected ArrayList<Color> m_Colors = new ArrayList();
/*   55:     */   protected Instances m_trainingData;
/*   56:     */   protected Classifier m_classifier;
/*   57:     */   protected DataGenerator m_dataGenerator;
/*   58: 101 */   private int m_classIndex = -1;
/*   59:     */   protected int m_xAttribute;
/*   60:     */   protected int m_yAttribute;
/*   61:     */   protected double m_minX;
/*   62:     */   protected double m_minY;
/*   63:     */   protected double m_maxX;
/*   64:     */   protected double m_maxY;
/*   65:     */   private double m_rangeX;
/*   66:     */   private double m_rangeY;
/*   67:     */   protected double m_pixHeight;
/*   68:     */   protected double m_pixWidth;
/*   69: 120 */   protected Image m_osi = null;
/*   70:     */   protected int m_panelWidth;
/*   71:     */   protected int m_panelHeight;
/*   72: 127 */   protected int m_numOfSamplesPerRegion = 2;
/*   73:     */   protected int m_numOfSamplesPerGenerator;
/*   74: 131 */   protected double m_samplesBase = 2.0D;
/*   75: 134 */   private final Vector<ActionListener> m_listeners = new Vector();
/*   76:     */   
/*   77:     */   private class PlotPanel
/*   78:     */     extends JPanel
/*   79:     */   {
/*   80:     */     private static final long serialVersionUID = 743629498352235060L;
/*   81:     */     
/*   82:     */     public PlotPanel()
/*   83:     */     {
/*   84: 145 */       setToolTipText("");
/*   85:     */     }
/*   86:     */     
/*   87:     */     public void paintComponent(Graphics g)
/*   88:     */     {
/*   89: 150 */       super.paintComponent(g);
/*   90: 151 */       if (BoundaryPanel.this.m_osi != null) {
/*   91: 152 */         g.drawImage(BoundaryPanel.this.m_osi, 0, 0, this);
/*   92:     */       }
/*   93:     */     }
/*   94:     */     
/*   95:     */     public String getToolTipText(MouseEvent event)
/*   96:     */     {
/*   97: 158 */       if (BoundaryPanel.this.m_probabilityCache == null) {
/*   98: 159 */         return null;
/*   99:     */       }
/*  100: 162 */       if (BoundaryPanel.this.m_probabilityCache[event.getY()][event.getX()] == null) {
/*  101: 163 */         return null;
/*  102:     */       }
/*  103: 166 */       String pVec = "(X: " + Utils.doubleToString(BoundaryPanel.this.convertFromPanelX(event.getX()), 2) + " Y: " + Utils.doubleToString(BoundaryPanel.this.convertFromPanelY(event.getY()), 2) + ") ";
/*  104: 170 */       for (int i = 0; i < BoundaryPanel.this.m_trainingData.classAttribute().numValues(); i++) {
/*  105: 171 */         pVec = pVec + Utils.doubleToString(BoundaryPanel.this.m_probabilityCache[event.getY()][event.getX()][i], 3) + " ";
/*  106:     */       }
/*  107: 175 */       return pVec;
/*  108:     */     }
/*  109:     */   }
/*  110:     */   
/*  111: 180 */   private final PlotPanel m_plotPanel = new PlotPanel();
/*  112: 183 */   private Thread m_plotThread = null;
/*  113: 186 */   protected boolean m_stopPlotting = false;
/*  114: 189 */   protected boolean m_stopReplotting = false;
/*  115: 192 */   private final Double m_dummy = new Double(1.0D);
/*  116: 193 */   private boolean m_pausePlotting = false;
/*  117: 195 */   private int m_size = 1;
/*  118:     */   private boolean m_initialTiling;
/*  119: 200 */   private Random m_random = null;
/*  120:     */   protected double[][][] m_probabilityCache;
/*  121: 206 */   protected boolean m_plotTrainingData = true;
/*  122:     */   
/*  123:     */   public BoundaryPanel(int panelWidth, int panelHeight)
/*  124:     */   {
/*  125: 215 */     ToolTipManager.sharedInstance().setDismissDelay(2147483647);
/*  126: 216 */     this.m_panelWidth = panelWidth;
/*  127: 217 */     this.m_panelHeight = panelHeight;
/*  128: 218 */     setLayout(new BorderLayout());
/*  129: 219 */     this.m_plotPanel.setMinimumSize(new Dimension(this.m_panelWidth, this.m_panelHeight));
/*  130: 220 */     this.m_plotPanel.setPreferredSize(new Dimension(this.m_panelWidth, this.m_panelHeight));
/*  131: 221 */     this.m_plotPanel.setMaximumSize(new Dimension(this.m_panelWidth, this.m_panelHeight));
/*  132: 222 */     add(this.m_plotPanel, "Center");
/*  133: 223 */     setPreferredSize(this.m_plotPanel.getPreferredSize());
/*  134: 224 */     setMaximumSize(this.m_plotPanel.getMaximumSize());
/*  135: 225 */     setMinimumSize(this.m_plotPanel.getMinimumSize());
/*  136:     */     
/*  137: 227 */     this.m_random = new Random(1L);
/*  138: 228 */     for (Color element : DEFAULT_COLORS) {
/*  139: 229 */       this.m_Colors.add(new Color(element.getRed(), element.getGreen(), element.getBlue()));
/*  140:     */     }
/*  141: 232 */     this.m_probabilityCache = new double[this.m_panelHeight][this.m_panelWidth][];
/*  142:     */   }
/*  143:     */   
/*  144:     */   public void setNumSamplesPerRegion(int num)
/*  145:     */   {
/*  146: 243 */     this.m_numOfSamplesPerRegion = num;
/*  147:     */   }
/*  148:     */   
/*  149:     */   public int getNumSamplesPerRegion()
/*  150:     */   {
/*  151: 252 */     return this.m_numOfSamplesPerRegion;
/*  152:     */   }
/*  153:     */   
/*  154:     */   public void setGeneratorSamplesBase(double ksb)
/*  155:     */   {
/*  156: 262 */     this.m_samplesBase = ksb;
/*  157:     */   }
/*  158:     */   
/*  159:     */   public double getGeneratorSamplesBase()
/*  160:     */   {
/*  161: 272 */     return this.m_samplesBase;
/*  162:     */   }
/*  163:     */   
/*  164:     */   protected void initialize()
/*  165:     */   {
/*  166: 279 */     int iwidth = this.m_plotPanel.getWidth();
/*  167: 280 */     int iheight = this.m_plotPanel.getHeight();
/*  168:     */     
/*  169: 282 */     this.m_osi = this.m_plotPanel.createImage(iwidth, iheight);
/*  170: 283 */     Graphics m = this.m_osi.getGraphics();
/*  171: 284 */     m.fillRect(0, 0, iwidth, iheight);
/*  172:     */   }
/*  173:     */   
/*  174:     */   public void stopPlotting()
/*  175:     */   {
/*  176: 291 */     this.m_stopPlotting = true;
/*  177:     */     try
/*  178:     */     {
/*  179: 293 */       this.m_plotThread.join(100L);
/*  180:     */     }
/*  181:     */     catch (Exception e) {}
/*  182:     */   }
/*  183:     */   
/*  184:     */   public void computeMinMaxAtts()
/*  185:     */   {
/*  186: 304 */     this.m_minX = 1.7976931348623157E+308D;
/*  187: 305 */     this.m_minY = 1.7976931348623157E+308D;
/*  188: 306 */     this.m_maxX = 4.9E-324D;
/*  189: 307 */     this.m_maxY = 4.9E-324D;
/*  190:     */     
/*  191: 309 */     boolean allPointsLessThanOne = true;
/*  192: 311 */     if (this.m_trainingData.numInstances() == 0)
/*  193:     */     {
/*  194: 312 */       this.m_minX = (this.m_minY = 0.0D);
/*  195: 313 */       this.m_maxX = (this.m_maxY = 1.0D);
/*  196:     */     }
/*  197:     */     else
/*  198:     */     {
/*  199: 315 */       for (int i = 0; i < this.m_trainingData.numInstances(); i++)
/*  200:     */       {
/*  201: 316 */         Instance inst = this.m_trainingData.instance(i);
/*  202: 317 */         double x = inst.value(this.m_xAttribute);
/*  203: 318 */         double y = inst.value(this.m_yAttribute);
/*  204: 319 */         if ((!Utils.isMissingValue(x)) && (!Utils.isMissingValue(y)))
/*  205:     */         {
/*  206: 320 */           if (x < this.m_minX) {
/*  207: 321 */             this.m_minX = x;
/*  208:     */           }
/*  209: 323 */           if (x > this.m_maxX) {
/*  210: 324 */             this.m_maxX = x;
/*  211:     */           }
/*  212: 327 */           if (y < this.m_minY) {
/*  213: 328 */             this.m_minY = y;
/*  214:     */           }
/*  215: 330 */           if (y > this.m_maxY) {
/*  216: 331 */             this.m_maxY = y;
/*  217:     */           }
/*  218: 333 */           if ((x > 1.0D) || (y > 1.0D)) {
/*  219: 334 */             allPointsLessThanOne = false;
/*  220:     */           }
/*  221:     */         }
/*  222:     */       }
/*  223:     */     }
/*  224: 340 */     if (this.m_minX == this.m_maxX) {
/*  225: 341 */       this.m_minX = 0.0D;
/*  226:     */     }
/*  227: 343 */     if (this.m_minY == this.m_maxY) {
/*  228: 344 */       this.m_minY = 0.0D;
/*  229:     */     }
/*  230: 346 */     if (this.m_minX == 1.7976931348623157E+308D) {
/*  231: 347 */       this.m_minX = 0.0D;
/*  232:     */     }
/*  233: 349 */     if (this.m_minY == 1.7976931348623157E+308D) {
/*  234: 350 */       this.m_minY = 0.0D;
/*  235:     */     }
/*  236: 352 */     if (this.m_maxX == 4.9E-324D) {
/*  237: 353 */       this.m_maxX = 1.0D;
/*  238:     */     }
/*  239: 355 */     if (this.m_maxY == 4.9E-324D) {
/*  240: 356 */       this.m_maxY = 1.0D;
/*  241:     */     }
/*  242: 358 */     if (allPointsLessThanOne) {
/*  243: 360 */       this.m_maxX = (this.m_maxY = 1.0D);
/*  244:     */     }
/*  245: 363 */     this.m_rangeX = (this.m_maxX - this.m_minX);
/*  246: 364 */     this.m_rangeY = (this.m_maxY - this.m_minY);
/*  247:     */     
/*  248: 366 */     this.m_pixWidth = (this.m_rangeX / this.m_panelWidth);
/*  249: 367 */     this.m_pixHeight = (this.m_rangeY / this.m_panelHeight);
/*  250:     */   }
/*  251:     */   
/*  252:     */   private double getRandomX(int pix)
/*  253:     */   {
/*  254: 379 */     double minPix = this.m_minX + pix * this.m_pixWidth;
/*  255:     */     
/*  256: 381 */     return minPix + this.m_random.nextDouble() * this.m_pixWidth;
/*  257:     */   }
/*  258:     */   
/*  259:     */   private double getRandomY(int pix)
/*  260:     */   {
/*  261: 393 */     double minPix = this.m_minY + pix * this.m_pixHeight;
/*  262:     */     
/*  263: 395 */     return minPix + this.m_random.nextDouble() * this.m_pixHeight;
/*  264:     */   }
/*  265:     */   
/*  266:     */   public void start()
/*  267:     */     throws Exception
/*  268:     */   {
/*  269: 404 */     this.m_numOfSamplesPerGenerator = ((int)Math.pow(this.m_samplesBase, this.m_trainingData.numAttributes() - 3));
/*  270:     */     
/*  271:     */ 
/*  272: 407 */     this.m_stopReplotting = true;
/*  273: 408 */     if (this.m_trainingData == null) {
/*  274: 409 */       throw new Exception("No training data set (BoundaryPanel)");
/*  275:     */     }
/*  276: 411 */     if (this.m_classifier == null) {
/*  277: 412 */       throw new Exception("No classifier set (BoundaryPanel)");
/*  278:     */     }
/*  279: 414 */     if (this.m_dataGenerator == null) {
/*  280: 415 */       throw new Exception("No data generator set (BoundaryPanel)");
/*  281:     */     }
/*  282: 417 */     if ((this.m_trainingData.attribute(this.m_xAttribute).isNominal()) || (this.m_trainingData.attribute(this.m_yAttribute).isNominal())) {
/*  283: 419 */       throw new Exception("Visualization dimensions must be numeric (BoundaryPanel)");
/*  284:     */     }
/*  285: 423 */     computeMinMaxAtts();
/*  286:     */     
/*  287: 425 */     startPlotThread();
/*  288:     */   }
/*  289:     */   
/*  290:     */   protected class PlotThread
/*  291:     */     extends Thread
/*  292:     */   {
/*  293:     */     double[] m_weightingAttsValues;
/*  294:     */     boolean[] m_attsToWeightOn;
/*  295:     */     double[] m_vals;
/*  296:     */     double[] m_dist;
/*  297:     */     Instance m_predInst;
/*  298:     */     
/*  299:     */     protected PlotThread() {}
/*  300:     */     
/*  301:     */     public void run()
/*  302:     */     {
/*  303: 444 */       BoundaryPanel.this.m_stopPlotting = false;
/*  304:     */       try
/*  305:     */       {
/*  306: 446 */         BoundaryPanel.this.initialize();
/*  307: 447 */         BoundaryPanel.this.repaint();
/*  308:     */         
/*  309:     */ 
/*  310: 450 */         BoundaryPanel.this.m_probabilityCache = new double[BoundaryPanel.this.m_panelHeight][BoundaryPanel.this.m_panelWidth][];
/*  311: 451 */         BoundaryPanel.this.m_classifier.buildClassifier(BoundaryPanel.this.m_trainingData);
/*  312:     */         
/*  313:     */ 
/*  314: 454 */         this.m_attsToWeightOn = new boolean[BoundaryPanel.this.m_trainingData.numAttributes()];
/*  315: 455 */         this.m_attsToWeightOn[BoundaryPanel.this.m_xAttribute] = true;
/*  316: 456 */         this.m_attsToWeightOn[BoundaryPanel.this.m_yAttribute] = true;
/*  317:     */         
/*  318: 458 */         BoundaryPanel.this.m_dataGenerator.setWeightingDimensions(this.m_attsToWeightOn);
/*  319:     */         
/*  320: 460 */         BoundaryPanel.this.m_dataGenerator.buildGenerator(BoundaryPanel.this.m_trainingData);
/*  321:     */         
/*  322:     */ 
/*  323: 463 */         this.m_weightingAttsValues = new double[this.m_attsToWeightOn.length];
/*  324: 464 */         this.m_vals = new double[BoundaryPanel.this.m_trainingData.numAttributes()];
/*  325: 465 */         this.m_predInst = new DenseInstance(1.0D, this.m_vals);
/*  326: 466 */         this.m_predInst.setDataset(BoundaryPanel.this.m_trainingData);
/*  327:     */         
/*  328: 468 */         BoundaryPanel.this.m_size = 16;
/*  329:     */         
/*  330: 470 */         BoundaryPanel.this.m_initialTiling = true;
/*  331: 472 */         for (int i = 0; i <= BoundaryPanel.this.m_panelHeight; i += BoundaryPanel.this.m_size) {
/*  332: 473 */           for (int j = 0; j <= BoundaryPanel.this.m_panelWidth; j += BoundaryPanel.this.m_size)
/*  333:     */           {
/*  334: 474 */             if (BoundaryPanel.this.m_stopPlotting) {
/*  335:     */               break label380;
/*  336:     */             }
/*  337: 477 */             if (BoundaryPanel.this.m_pausePlotting) {
/*  338: 478 */               synchronized (BoundaryPanel.this.m_dummy)
/*  339:     */               {
/*  340:     */                 try
/*  341:     */                 {
/*  342: 480 */                   BoundaryPanel.this.m_dummy.wait();
/*  343:     */                 }
/*  344:     */                 catch (InterruptedException ex)
/*  345:     */                 {
/*  346: 482 */                   BoundaryPanel.this.m_pausePlotting = false;
/*  347:     */                 }
/*  348:     */               }
/*  349:     */             }
/*  350: 486 */             BoundaryPanel.this.plotPoint(j, i, BoundaryPanel.this.m_size, BoundaryPanel.this.m_size, calculateRegionProbs(j, i), j == 0);
/*  351:     */           }
/*  352:     */         }
/*  353:     */         label380:
/*  354: 490 */         if (!BoundaryPanel.this.m_stopPlotting) {
/*  355: 491 */           BoundaryPanel.this.m_initialTiling = false;
/*  356:     */         }
/*  357: 495 */         int size2 = BoundaryPanel.this.m_size / 2;
/*  358: 496 */         while (BoundaryPanel.this.m_size > 1)
/*  359:     */         {
/*  360: 497 */           for (int i = 0; i <= BoundaryPanel.this.m_panelHeight; i += BoundaryPanel.this.m_size) {
/*  361: 498 */             for (int j = 0; j <= BoundaryPanel.this.m_panelWidth; j += BoundaryPanel.this.m_size)
/*  362:     */             {
/*  363: 499 */               if (BoundaryPanel.this.m_stopPlotting) {
/*  364:     */                 break label650;
/*  365:     */               }
/*  366: 502 */               if (BoundaryPanel.this.m_pausePlotting) {
/*  367: 503 */                 synchronized (BoundaryPanel.this.m_dummy)
/*  368:     */                 {
/*  369:     */                   try
/*  370:     */                   {
/*  371: 505 */                     BoundaryPanel.this.m_dummy.wait();
/*  372:     */                   }
/*  373:     */                   catch (InterruptedException ex)
/*  374:     */                   {
/*  375: 507 */                     BoundaryPanel.this.m_pausePlotting = false;
/*  376:     */                   }
/*  377:     */                 }
/*  378:     */               }
/*  379: 511 */               boolean update = (j == 0) && (i % 2 == 0);
/*  380:     */               
/*  381: 513 */               BoundaryPanel.this.plotPoint(j, i + size2, size2, size2, calculateRegionProbs(j, i + size2), update);
/*  382:     */               
/*  383: 515 */               BoundaryPanel.this.plotPoint(j + size2, i + size2, size2, size2, calculateRegionProbs(j + size2, i + size2), update);
/*  384:     */               
/*  385: 517 */               BoundaryPanel.this.plotPoint(j + size2, i, size2, size2, calculateRegionProbs(j + size2, i), update);
/*  386:     */             }
/*  387:     */           }
/*  388: 522 */           BoundaryPanel.this.m_size = size2;
/*  389: 523 */           size2 /= 2;
/*  390:     */         }
/*  391:     */         label650:
/*  392: 525 */         BoundaryPanel.this.update();
/*  393: 534 */         if (BoundaryPanel.this.m_plotTrainingData) {
/*  394: 535 */           BoundaryPanel.this.plotTrainingData();
/*  395:     */         }
/*  396: 543 */         BoundaryPanel.this.m_plotThread = null;
/*  397:     */         
/*  398:     */ 
/*  399: 546 */         ActionEvent e = new ActionEvent(this, 0, "");
/*  400:     */         Vector<ActionListener> l;
/*  401: 547 */         synchronized (this)
/*  402:     */         {
/*  403: 548 */           l = (Vector)BoundaryPanel.this.m_listeners.clone();
/*  404:     */         }
/*  405: 550 */         for (int i = 0; i < l.size(); i++)
/*  406:     */         {
/*  407: 551 */           ActionListener al = (ActionListener)l.elementAt(i);
/*  408: 552 */           al.actionPerformed(e);
/*  409:     */         }
/*  410:     */       }
/*  411:     */       catch (Exception ex)
/*  412:     */       {
/*  413: 539 */         ex.printStackTrace();
/*  414: 540 */         JOptionPane.showMessageDialog(null, "Error while plotting: \"" + ex.getMessage() + "\"");
/*  415:     */         
/*  416:     */ 
/*  417: 543 */         BoundaryPanel.this.m_plotThread = null;
/*  418:     */         
/*  419:     */ 
/*  420: 546 */         ActionEvent e = new ActionEvent(this, 0, "");
/*  421:     */         Vector<ActionListener> l;
/*  422: 547 */         synchronized (this)
/*  423:     */         {
/*  424: 548 */           l = (Vector)BoundaryPanel.this.m_listeners.clone();
/*  425:     */         }
/*  426: 550 */         for (int i = 0; i < l.size(); i++)
/*  427:     */         {
/*  428: 551 */           ActionListener al = (ActionListener)l.elementAt(i);
/*  429: 552 */           al.actionPerformed(e);
/*  430:     */         }
/*  431:     */       }
/*  432:     */       finally
/*  433:     */       {
/*  434: 543 */         BoundaryPanel.this.m_plotThread = null;
/*  435:     */         
/*  436:     */ 
/*  437: 546 */         ActionEvent e = new ActionEvent(this, 0, "");
/*  438:     */         Vector<ActionListener> l;
/*  439: 547 */         synchronized (this)
/*  440:     */         {
/*  441: 548 */           l = (Vector)BoundaryPanel.this.m_listeners.clone();
/*  442:     */         }
/*  443: 550 */         for (int i = 0; i < l.size(); i++)
/*  444:     */         {
/*  445: 551 */           ActionListener al = (ActionListener)l.elementAt(i);
/*  446: 552 */           al.actionPerformed(e);
/*  447:     */         }
/*  448:     */       }
/*  449:     */     }
/*  450:     */     
/*  451:     */     private double[] calculateRegionProbs(int j, int i)
/*  452:     */       throws Exception
/*  453:     */     {
/*  454: 558 */       double[] sumOfProbsForRegion = new double[BoundaryPanel.this.m_trainingData.classAttribute().numValues()];
/*  455:     */       
/*  456:     */ 
/*  457: 561 */       double sumOfSums = 0.0D;
/*  458: 563 */       for (int u = 0; u < BoundaryPanel.this.m_numOfSamplesPerRegion; u++)
/*  459:     */       {
/*  460: 565 */         double[] sumOfProbsForLocation = new double[BoundaryPanel.this.m_trainingData.classAttribute().numValues()];
/*  461:     */         
/*  462:     */ 
/*  463: 568 */         this.m_weightingAttsValues[BoundaryPanel.this.m_xAttribute] = BoundaryPanel.this.getRandomX(j);
/*  464: 569 */         this.m_weightingAttsValues[BoundaryPanel.this.m_yAttribute] = BoundaryPanel.this.getRandomY(BoundaryPanel.this.m_panelHeight - i - 1);
/*  465:     */         
/*  466: 571 */         BoundaryPanel.this.m_dataGenerator.setWeightingValues(this.m_weightingAttsValues);
/*  467:     */         
/*  468: 573 */         double[] weights = BoundaryPanel.this.m_dataGenerator.getWeights();
/*  469: 574 */         double sumOfWeights = Utils.sum(weights);
/*  470: 575 */         sumOfSums += sumOfWeights;
/*  471: 576 */         int[] indices = Utils.sort(weights);
/*  472:     */         
/*  473:     */ 
/*  474: 579 */         int[] newIndices = new int[indices.length];
/*  475: 580 */         double sumSoFar = 0.0D;
/*  476: 581 */         double criticalMass = 0.99D * sumOfWeights;
/*  477: 582 */         int index = weights.length - 1;
/*  478: 583 */         int counter = 0;
/*  479: 584 */         for (int z = weights.length - 1; z >= 0; z--)
/*  480:     */         {
/*  481: 585 */           newIndices[(index--)] = indices[z];
/*  482: 586 */           sumSoFar += weights[indices[z]];
/*  483: 587 */           counter++;
/*  484: 588 */           if (sumSoFar > criticalMass) {
/*  485:     */             break;
/*  486:     */           }
/*  487:     */         }
/*  488: 592 */         indices = new int[counter];
/*  489: 593 */         System.arraycopy(newIndices, index + 1, indices, 0, counter);
/*  490: 595 */         for (int z = 0; z < BoundaryPanel.this.m_numOfSamplesPerGenerator; z++)
/*  491:     */         {
/*  492: 597 */           BoundaryPanel.this.m_dataGenerator.setWeightingValues(this.m_weightingAttsValues);
/*  493: 598 */           double[][] values = BoundaryPanel.this.m_dataGenerator.generateInstances(indices);
/*  494: 600 */           for (int q = 0; q < values.length; q++) {
/*  495: 601 */             if (values[q] != null)
/*  496:     */             {
/*  497: 602 */               System.arraycopy(values[q], 0, this.m_vals, 0, this.m_vals.length);
/*  498: 603 */               this.m_vals[BoundaryPanel.this.m_xAttribute] = this.m_weightingAttsValues[BoundaryPanel.this.m_xAttribute];
/*  499: 604 */               this.m_vals[BoundaryPanel.this.m_yAttribute] = this.m_weightingAttsValues[BoundaryPanel.this.m_yAttribute];
/*  500:     */               
/*  501:     */ 
/*  502: 607 */               this.m_dist = BoundaryPanel.this.m_classifier.distributionForInstance(this.m_predInst);
/*  503: 608 */               for (int k = 0; k < sumOfProbsForLocation.length; k++) {
/*  504: 609 */                 sumOfProbsForLocation[k] += this.m_dist[k] * weights[q];
/*  505:     */               }
/*  506:     */             }
/*  507:     */           }
/*  508:     */         }
/*  509: 615 */         for (int k = 0; k < sumOfProbsForRegion.length; k++) {
/*  510: 616 */           sumOfProbsForRegion[k] += sumOfProbsForLocation[k] / BoundaryPanel.this.m_numOfSamplesPerGenerator;
/*  511:     */         }
/*  512:     */       }
/*  513: 621 */       if (sumOfSums > 0.0D) {
/*  514: 622 */         Utils.normalize(sumOfProbsForRegion, sumOfSums);
/*  515:     */       } else {
/*  516: 624 */         throw new Exception("Arithmetic underflow. Please increase value of kernel bandwidth parameter (k).");
/*  517:     */       }
/*  518: 628 */       if ((i < BoundaryPanel.this.m_panelHeight) && (j < BoundaryPanel.this.m_panelWidth))
/*  519:     */       {
/*  520: 629 */         BoundaryPanel.this.m_probabilityCache[i][j] = new double[sumOfProbsForRegion.length];
/*  521: 630 */         System.arraycopy(sumOfProbsForRegion, 0, BoundaryPanel.this.m_probabilityCache[i][j], 0, sumOfProbsForRegion.length);
/*  522:     */       }
/*  523: 634 */       return sumOfProbsForRegion;
/*  524:     */     }
/*  525:     */   }
/*  526:     */   
/*  527:     */   public void plotTrainingData()
/*  528:     */   {
/*  529: 643 */     Graphics2D osg = (Graphics2D)this.m_osi.getGraphics();
/*  530: 644 */     Graphics g = this.m_plotPanel.getGraphics();
/*  531: 645 */     osg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  532:     */     
/*  533: 647 */     double xval = 0.0D;
/*  534: 648 */     double yval = 0.0D;
/*  535: 650 */     for (int i = 0; i < this.m_trainingData.numInstances(); i++) {
/*  536: 651 */       if ((!this.m_trainingData.instance(i).isMissing(this.m_xAttribute)) && (!this.m_trainingData.instance(i).isMissing(this.m_yAttribute))) {
/*  537: 654 */         if (!this.m_trainingData.instance(i).isMissing(this.m_classIndex))
/*  538:     */         {
/*  539: 659 */           xval = this.m_trainingData.instance(i).value(this.m_xAttribute);
/*  540: 660 */           yval = this.m_trainingData.instance(i).value(this.m_yAttribute);
/*  541:     */           
/*  542: 662 */           int panelX = convertToPanelX(xval);
/*  543: 663 */           int panelY = convertToPanelY(yval);
/*  544: 664 */           Color ColorToPlotWith = (Color)this.m_Colors.get((int)this.m_trainingData.instance(i).value(this.m_classIndex) % this.m_Colors.size());
/*  545: 667 */           if (ColorToPlotWith.equals(Color.white)) {
/*  546: 668 */             osg.setColor(Color.black);
/*  547:     */           } else {
/*  548: 670 */             osg.setColor(Color.white);
/*  549:     */           }
/*  550: 672 */           osg.fillOval(panelX - 3, panelY - 3, 7, 7);
/*  551: 673 */           osg.setColor(ColorToPlotWith);
/*  552: 674 */           osg.fillOval(panelX - 2, panelY - 2, 5, 5);
/*  553:     */         }
/*  554:     */       }
/*  555:     */     }
/*  556: 677 */     g.drawImage(this.m_osi, 0, 0, this.m_plotPanel);
/*  557:     */   }
/*  558:     */   
/*  559:     */   private int convertToPanelX(double xval)
/*  560:     */   {
/*  561: 684 */     double temp = (xval - this.m_minX) / this.m_rangeX;
/*  562: 685 */     temp *= this.m_panelWidth;
/*  563:     */     
/*  564: 687 */     return (int)temp;
/*  565:     */   }
/*  566:     */   
/*  567:     */   private int convertToPanelY(double yval)
/*  568:     */   {
/*  569: 694 */     double temp = (yval - this.m_minY) / this.m_rangeY;
/*  570: 695 */     temp *= this.m_panelHeight;
/*  571: 696 */     temp = this.m_panelHeight - temp;
/*  572:     */     
/*  573: 698 */     return (int)temp;
/*  574:     */   }
/*  575:     */   
/*  576:     */   private double convertFromPanelX(double pX)
/*  577:     */   {
/*  578: 705 */     pX /= this.m_panelWidth;
/*  579: 706 */     pX *= this.m_rangeX;
/*  580: 707 */     return pX + this.m_minX;
/*  581:     */   }
/*  582:     */   
/*  583:     */   private double convertFromPanelY(double pY)
/*  584:     */   {
/*  585: 714 */     pY = this.m_panelHeight - pY;
/*  586: 715 */     pY /= this.m_panelHeight;
/*  587: 716 */     pY *= this.m_rangeY;
/*  588:     */     
/*  589: 718 */     return pY + this.m_minY;
/*  590:     */   }
/*  591:     */   
/*  592:     */   protected void plotPoint(int x, int y, double[] probs, boolean update)
/*  593:     */   {
/*  594: 725 */     plotPoint(x, y, 1, 1, probs, update);
/*  595:     */   }
/*  596:     */   
/*  597:     */   private void plotPoint(int x, int y, int width, int height, double[] probs, boolean update)
/*  598:     */   {
/*  599: 735 */     Graphics osg = this.m_osi.getGraphics();
/*  600: 736 */     if (update)
/*  601:     */     {
/*  602: 737 */       osg.setXORMode(Color.white);
/*  603: 738 */       osg.drawLine(0, y, this.m_panelWidth - 1, y);
/*  604: 739 */       update();
/*  605: 740 */       osg.drawLine(0, y, this.m_panelWidth - 1, y);
/*  606:     */     }
/*  607: 744 */     osg.setPaintMode();
/*  608: 745 */     float[] colVal = new float[3];
/*  609:     */     
/*  610: 747 */     float[] tempCols = new float[3];
/*  611: 748 */     for (int k = 0; k < probs.length; k++)
/*  612:     */     {
/*  613: 749 */       Color curr = (Color)this.m_Colors.get(k % this.m_Colors.size());
/*  614:     */       
/*  615: 751 */       curr.getRGBColorComponents(tempCols);
/*  616: 752 */       for (int z = 0; z < 3; z++)
/*  617:     */       {
/*  618: 753 */         int tmp123_121 = z; float[] tmp123_119 = colVal;tmp123_119[tmp123_121] = ((float)(tmp123_119[tmp123_121] + probs[k] * tempCols[z]));
/*  619:     */       }
/*  620:     */     }
/*  621: 757 */     for (int z = 0; z < 3; z++) {
/*  622: 758 */       if (colVal[z] < 0.0F) {
/*  623: 759 */         colVal[z] = 0.0F;
/*  624: 760 */       } else if (colVal[z] > 1.0F) {
/*  625: 761 */         colVal[z] = 1.0F;
/*  626:     */       }
/*  627:     */     }
/*  628: 765 */     osg.setColor(new Color(colVal[0], colVal[1], colVal[2]));
/*  629: 766 */     osg.fillRect(x, y, width, height);
/*  630:     */   }
/*  631:     */   
/*  632:     */   private void update()
/*  633:     */   {
/*  634: 773 */     Graphics g = this.m_plotPanel.getGraphics();
/*  635: 774 */     g.drawImage(this.m_osi, 0, 0, this.m_plotPanel);
/*  636:     */   }
/*  637:     */   
/*  638:     */   public void setTrainingData(Instances trainingData)
/*  639:     */     throws Exception
/*  640:     */   {
/*  641: 785 */     this.m_trainingData = trainingData;
/*  642: 786 */     if (this.m_trainingData.classIndex() < 0) {
/*  643: 787 */       throw new Exception("No class attribute set (BoundaryPanel)");
/*  644:     */     }
/*  645: 789 */     this.m_classIndex = this.m_trainingData.classIndex();
/*  646:     */   }
/*  647:     */   
/*  648:     */   public void addTrainingInstance(Instance instance)
/*  649:     */   {
/*  650: 797 */     if (this.m_trainingData == null) {
/*  651: 799 */       System.err.println("Trying to add to a null training set (BoundaryPanel)");
/*  652:     */     } else {
/*  653: 802 */       this.m_trainingData.add(instance);
/*  654:     */     }
/*  655:     */   }
/*  656:     */   
/*  657:     */   public void addActionListener(ActionListener newListener)
/*  658:     */   {
/*  659: 812 */     this.m_listeners.add(newListener);
/*  660:     */   }
/*  661:     */   
/*  662:     */   public void removeActionListener(ActionListener removeListener)
/*  663:     */   {
/*  664: 821 */     this.m_listeners.removeElement(removeListener);
/*  665:     */   }
/*  666:     */   
/*  667:     */   public void setClassifier(Classifier classifier)
/*  668:     */   {
/*  669: 830 */     this.m_classifier = classifier;
/*  670:     */   }
/*  671:     */   
/*  672:     */   public void setDataGenerator(DataGenerator dataGenerator)
/*  673:     */   {
/*  674: 839 */     this.m_dataGenerator = dataGenerator;
/*  675:     */   }
/*  676:     */   
/*  677:     */   public void setXAttribute(int xatt)
/*  678:     */     throws Exception
/*  679:     */   {
/*  680: 849 */     if (this.m_trainingData == null) {
/*  681: 850 */       throw new Exception("No training data set (BoundaryPanel)");
/*  682:     */     }
/*  683: 852 */     if ((xatt < 0) || (xatt > this.m_trainingData.numAttributes())) {
/*  684: 853 */       throw new Exception("X attribute out of range (BoundaryPanel)");
/*  685:     */     }
/*  686: 855 */     if (this.m_trainingData.attribute(xatt).isNominal()) {
/*  687: 856 */       throw new Exception("Visualization dimensions must be numeric (BoundaryPanel)");
/*  688:     */     }
/*  689: 864 */     this.m_xAttribute = xatt;
/*  690:     */   }
/*  691:     */   
/*  692:     */   public void setYAttribute(int yatt)
/*  693:     */     throws Exception
/*  694:     */   {
/*  695: 874 */     if (this.m_trainingData == null) {
/*  696: 875 */       throw new Exception("No training data set (BoundaryPanel)");
/*  697:     */     }
/*  698: 877 */     if ((yatt < 0) || (yatt > this.m_trainingData.numAttributes())) {
/*  699: 878 */       throw new Exception("X attribute out of range (BoundaryPanel)");
/*  700:     */     }
/*  701: 880 */     if (this.m_trainingData.attribute(yatt).isNominal()) {
/*  702: 881 */       throw new Exception("Visualization dimensions must be numeric (BoundaryPanel)");
/*  703:     */     }
/*  704: 889 */     this.m_yAttribute = yatt;
/*  705:     */   }
/*  706:     */   
/*  707:     */   public void setColors(ArrayList<Color> colors)
/*  708:     */   {
/*  709: 898 */     synchronized (this.m_Colors)
/*  710:     */     {
/*  711: 899 */       this.m_Colors = colors;
/*  712:     */     }
/*  713: 902 */     update();
/*  714:     */   }
/*  715:     */   
/*  716:     */   public void setPlotTrainingData(boolean pg)
/*  717:     */   {
/*  718: 911 */     this.m_plotTrainingData = pg;
/*  719:     */   }
/*  720:     */   
/*  721:     */   public boolean getPlotTrainingData()
/*  722:     */   {
/*  723: 920 */     return this.m_plotTrainingData;
/*  724:     */   }
/*  725:     */   
/*  726:     */   public ArrayList<Color> getColors()
/*  727:     */   {
/*  728: 929 */     return this.m_Colors;
/*  729:     */   }
/*  730:     */   
/*  731:     */   public void replot()
/*  732:     */   {
/*  733: 936 */     if (this.m_probabilityCache[0][0] == null) {
/*  734: 937 */       return;
/*  735:     */     }
/*  736: 939 */     this.m_stopReplotting = true;
/*  737: 940 */     this.m_pausePlotting = true;
/*  738:     */     try
/*  739:     */     {
/*  740: 943 */       Thread.sleep(300L);
/*  741:     */     }
/*  742:     */     catch (Exception ex) {}
/*  743: 947 */     Thread replotThread = new Thread()
/*  744:     */     {
/*  745:     */       public void run()
/*  746:     */       {
/*  747: 950 */         BoundaryPanel.this.m_stopReplotting = false;
/*  748: 951 */         int size2 = BoundaryPanel.this.m_size / 2;
/*  749: 952 */         for (int i = 0; i < BoundaryPanel.this.m_panelHeight; i += BoundaryPanel.this.m_size) {
/*  750: 953 */           for (int j = 0; j < BoundaryPanel.this.m_panelWidth; j += BoundaryPanel.this.m_size)
/*  751:     */           {
/*  752: 954 */             if ((BoundaryPanel.this.m_probabilityCache[i][j] == null) || (BoundaryPanel.this.m_stopReplotting)) {
/*  753:     */               break label360;
/*  754:     */             }
/*  755: 958 */             boolean update = (j == 0) && (i % 2 == 0);
/*  756: 959 */             if ((i < BoundaryPanel.this.m_panelHeight) && (j < BoundaryPanel.this.m_panelWidth)) {
/*  757: 961 */               if ((BoundaryPanel.this.m_initialTiling) || (BoundaryPanel.this.m_size == 1))
/*  758:     */               {
/*  759: 962 */                 if (BoundaryPanel.this.m_probabilityCache[i][j] == null) {
/*  760:     */                   break label360;
/*  761:     */                 }
/*  762: 965 */                 BoundaryPanel.this.plotPoint(j, i, BoundaryPanel.this.m_size, BoundaryPanel.this.m_size, BoundaryPanel.this.m_probabilityCache[i][j], update);
/*  763:     */               }
/*  764:     */               else
/*  765:     */               {
/*  766: 968 */                 if (BoundaryPanel.this.m_probabilityCache[(i + size2)][j] == null) {
/*  767:     */                   break label360;
/*  768:     */                 }
/*  769: 971 */                 BoundaryPanel.this.plotPoint(j, i + size2, size2, size2, BoundaryPanel.this.m_probabilityCache[(i + size2)][j], update);
/*  770: 973 */                 if (BoundaryPanel.this.m_probabilityCache[(i + size2)][(j + size2)] == null) {
/*  771:     */                   break label360;
/*  772:     */                 }
/*  773: 976 */                 BoundaryPanel.this.plotPoint(j + size2, i + size2, size2, size2, BoundaryPanel.this.m_probabilityCache[(i + size2)][(j + size2)], update);
/*  774: 978 */                 if (BoundaryPanel.this.m_probabilityCache[i][(j + size2)] == null) {
/*  775:     */                   break label360;
/*  776:     */                 }
/*  777: 981 */                 BoundaryPanel.this.plotPoint(j + size2, i, size2, size2, BoundaryPanel.this.m_probabilityCache[(i + size2)][j], update);
/*  778:     */               }
/*  779:     */             }
/*  780:     */           }
/*  781:     */         }
/*  782:     */         label360:
/*  783: 987 */         BoundaryPanel.this.update();
/*  784: 988 */         if (BoundaryPanel.this.m_plotTrainingData) {
/*  785: 989 */           BoundaryPanel.this.plotTrainingData();
/*  786:     */         }
/*  787: 991 */         BoundaryPanel.this.m_pausePlotting = false;
/*  788: 992 */         if (!BoundaryPanel.this.m_stopPlotting) {
/*  789: 993 */           synchronized (BoundaryPanel.this.m_dummy)
/*  790:     */           {
/*  791: 994 */             BoundaryPanel.this.m_dummy.notifyAll();
/*  792:     */           }
/*  793:     */         }
/*  794:     */       }
/*  795: 999 */     };
/*  796:1000 */     replotThread.start();
/*  797:     */   }
/*  798:     */   
/*  799:     */   protected void saveImage(String fileName)
/*  800:     */   {
/*  801:     */     try
/*  802:     */     {
/*  803:1013 */       BufferedImage bi = new BufferedImage(this.m_panelWidth, this.m_panelHeight, 1);
/*  804:     */       
/*  805:1015 */       Graphics2D gr2 = bi.createGraphics();
/*  806:1016 */       gr2.drawImage(this.m_osi, 0, 0, this.m_panelWidth, this.m_panelHeight, null);
/*  807:     */       
/*  808:     */ 
/*  809:1019 */       ImageWriter writer = null;
/*  810:1020 */       Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpg");
/*  811:1021 */       if (iter.hasNext()) {
/*  812:1022 */         writer = (ImageWriter)iter.next();
/*  813:     */       } else {
/*  814:1024 */         throw new Exception("No JPEG writer available!");
/*  815:     */       }
/*  816:1028 */       ImageOutputStream ios = ImageIO.createImageOutputStream(new File(fileName));
/*  817:1029 */       writer.setOutput(ios);
/*  818:     */       
/*  819:     */ 
/*  820:1032 */       ImageWriteParam param = new JPEGImageWriteParam(Locale.getDefault());
/*  821:1033 */       param.setCompressionMode(2);
/*  822:1034 */       param.setCompressionQuality(1.0F);
/*  823:     */       
/*  824:     */ 
/*  825:1037 */       writer.write(null, new IIOImage(bi, null, null), param);
/*  826:     */       
/*  827:     */ 
/*  828:1040 */       ios.flush();
/*  829:1041 */       writer.dispose();
/*  830:1042 */       ios.close();
/*  831:     */     }
/*  832:     */     catch (Exception e)
/*  833:     */     {
/*  834:1044 */       e.printStackTrace();
/*  835:     */     }
/*  836:     */   }
/*  837:     */   
/*  838:     */   public void addTrainingInstanceFromMouseLocation(int mouseX, int mouseY, int classAttIndex, double classValue)
/*  839:     */   {
/*  840:1062 */     double x = convertFromPanelX(mouseX);
/*  841:1063 */     double y = convertFromPanelY(mouseY);
/*  842:     */     
/*  843:     */ 
/*  844:1066 */     Instance newInstance = new DenseInstance(this.m_trainingData.numAttributes());
/*  845:1067 */     for (int i = 0; i < newInstance.numAttributes(); i++) {
/*  846:1068 */       if (i == classAttIndex) {
/*  847:1069 */         newInstance.setValue(i, classValue);
/*  848:1070 */       } else if (i == this.m_xAttribute) {
/*  849:1071 */         newInstance.setValue(i, x);
/*  850:1072 */       } else if (i == this.m_yAttribute) {
/*  851:1073 */         newInstance.setValue(i, y);
/*  852:     */       } else {
/*  853:1075 */         newInstance.setMissing(i);
/*  854:     */       }
/*  855:     */     }
/*  856:1080 */     addTrainingInstance(newInstance);
/*  857:     */   }
/*  858:     */   
/*  859:     */   public void removeAllInstances()
/*  860:     */   {
/*  861:1087 */     if (this.m_trainingData != null)
/*  862:     */     {
/*  863:1088 */       this.m_trainingData.delete();
/*  864:     */       try
/*  865:     */       {
/*  866:1090 */         initialize();
/*  867:     */       }
/*  868:     */       catch (Exception e) {}
/*  869:     */     }
/*  870:     */   }
/*  871:     */   
/*  872:     */   public void removeTrainingInstanceFromMouseLocation(int mouseX, int mouseY)
/*  873:     */   {
/*  874:1105 */     double x = convertFromPanelX(mouseX);
/*  875:1106 */     double y = convertFromPanelY(mouseY);
/*  876:     */     
/*  877:1108 */     int bestIndex = -1;
/*  878:1109 */     double bestDistanceBetween = 2147483647.0D;
/*  879:1112 */     for (int i = 0; i < this.m_trainingData.numInstances(); i++)
/*  880:     */     {
/*  881:1113 */       Instance current = this.m_trainingData.instance(i);
/*  882:1114 */       double distanceBetween = (current.value(this.m_xAttribute) - x) * (current.value(this.m_xAttribute) - x) + (current.value(this.m_yAttribute) - y) * (current.value(this.m_yAttribute) - y);
/*  883:1119 */       if (distanceBetween < bestDistanceBetween)
/*  884:     */       {
/*  885:1120 */         bestIndex = i;
/*  886:1121 */         bestDistanceBetween = distanceBetween;
/*  887:     */       }
/*  888:     */     }
/*  889:1124 */     if (bestIndex == -1) {
/*  890:1125 */       return;
/*  891:     */     }
/*  892:1127 */     Instance best = this.m_trainingData.instance(bestIndex);
/*  893:1128 */     double panelDistance = (convertToPanelX(best.value(this.m_xAttribute)) - mouseX) * (convertToPanelX(best.value(this.m_xAttribute)) - mouseX) + (convertToPanelY(best.value(this.m_yAttribute)) - mouseY) * (convertToPanelY(best.value(this.m_yAttribute)) - mouseY);
/*  894:1132 */     if (panelDistance < 49.0D) {
/*  895:1139 */       this.m_trainingData.delete(bestIndex);
/*  896:     */     }
/*  897:     */   }
/*  898:     */   
/*  899:     */   public void startPlotThread()
/*  900:     */   {
/*  901:1147 */     if (this.m_plotThread == null)
/*  902:     */     {
/*  903:1148 */       this.m_plotThread = new PlotThread();
/*  904:1149 */       this.m_plotThread.setPriority(1);
/*  905:1150 */       this.m_plotThread.start();
/*  906:     */     }
/*  907:     */   }
/*  908:     */   
/*  909:     */   public void addMouseListener(MouseListener l)
/*  910:     */   {
/*  911:1159 */     this.m_plotPanel.addMouseListener(l);
/*  912:     */   }
/*  913:     */   
/*  914:     */   public double getMinXBound()
/*  915:     */   {
/*  916:1167 */     return this.m_minX;
/*  917:     */   }
/*  918:     */   
/*  919:     */   public double getMinYBound()
/*  920:     */   {
/*  921:1175 */     return this.m_minY;
/*  922:     */   }
/*  923:     */   
/*  924:     */   public double getMaxXBound()
/*  925:     */   {
/*  926:1183 */     return this.m_maxX;
/*  927:     */   }
/*  928:     */   
/*  929:     */   public double getMaxYBound()
/*  930:     */   {
/*  931:1191 */     return this.m_maxY;
/*  932:     */   }
/*  933:     */   
/*  934:     */   public static void main(String[] args)
/*  935:     */   {
/*  936:     */     try
/*  937:     */     {
/*  938:1201 */       if (args.length < 8)
/*  939:     */       {
/*  940:1202 */         System.err.println("Usage : BoundaryPanel <dataset> <class col> <xAtt> <yAtt> <base> <# loc/pixel> <kernel bandwidth> <display width> <display height> <classifier [classifier options]>");
/*  941:     */         
/*  942:     */ 
/*  943:     */ 
/*  944:1206 */         System.exit(1);
/*  945:     */       }
/*  946:1208 */       JFrame jf = new JFrame("Weka classification boundary visualizer");
/*  947:     */       
/*  948:1210 */       jf.getContentPane().setLayout(new BorderLayout());
/*  949:     */       
/*  950:1212 */       System.err.println("Loading instances from : " + args[0]);
/*  951:1213 */       Reader r = new BufferedReader(new FileReader(args[0]));
/*  952:     */       
/*  953:1215 */       final Instances i = new Instances(r);
/*  954:1216 */       i.setClassIndex(Integer.parseInt(args[1]));
/*  955:     */       
/*  956:     */ 
/*  957:1219 */       final int xatt = Integer.parseInt(args[2]);
/*  958:1220 */       final int yatt = Integer.parseInt(args[3]);
/*  959:1221 */       int base = Integer.parseInt(args[4]);
/*  960:1222 */       int loc = Integer.parseInt(args[5]);
/*  961:     */       
/*  962:1224 */       int bandWidth = Integer.parseInt(args[6]);
/*  963:1225 */       int panelWidth = Integer.parseInt(args[7]);
/*  964:1226 */       int panelHeight = Integer.parseInt(args[8]);
/*  965:     */       
/*  966:1228 */       String classifierName = args[9];
/*  967:1229 */       final BoundaryPanel bv = new BoundaryPanel(panelWidth, panelHeight);
/*  968:1230 */       bv.addActionListener(new ActionListener()
/*  969:     */       {
/*  970:     */         public void actionPerformed(ActionEvent e)
/*  971:     */         {
/*  972:1233 */           String classifierNameNew = this.val$classifierName.substring(this.val$classifierName.lastIndexOf('.') + 1, this.val$classifierName.length());
/*  973:     */           
/*  974:1235 */           bv.saveImage(classifierNameNew + "_" + i.relationName() + "_X" + xatt + "_Y" + yatt + ".jpg");
/*  975:     */         }
/*  976:1239 */       });
/*  977:1240 */       jf.getContentPane().add(bv, "Center");
/*  978:1241 */       jf.setSize(bv.getMinimumSize());
/*  979:     */       
/*  980:1243 */       jf.addWindowListener(new WindowAdapter()
/*  981:     */       {
/*  982:     */         public void windowClosing(WindowEvent e)
/*  983:     */         {
/*  984:1246 */           this.val$jf.dispose();
/*  985:1247 */           System.exit(0);
/*  986:     */         }
/*  987:1250 */       });
/*  988:1251 */       jf.pack();
/*  989:1252 */       jf.setVisible(true);
/*  990:     */       
/*  991:1254 */       bv.repaint();
/*  992:     */       
/*  993:1256 */       String[] argsR = null;
/*  994:1257 */       if (args.length > 10)
/*  995:     */       {
/*  996:1258 */         argsR = new String[args.length - 10];
/*  997:1259 */         for (int j = 10; j < args.length; j++) {
/*  998:1260 */           argsR[(j - 10)] = args[j];
/*  999:     */         }
/* 1000:     */       }
/* 1001:1263 */       Classifier c = AbstractClassifier.forName(args[9], argsR);
/* 1002:1264 */       KDDataGenerator dataGen = new KDDataGenerator();
/* 1003:1265 */       dataGen.setKernelBandwidth(bandWidth);
/* 1004:1266 */       bv.setDataGenerator(dataGen);
/* 1005:1267 */       bv.setNumSamplesPerRegion(loc);
/* 1006:1268 */       bv.setGeneratorSamplesBase(base);
/* 1007:1269 */       bv.setClassifier(c);
/* 1008:1270 */       bv.setTrainingData(i);
/* 1009:1271 */       bv.setXAttribute(xatt);
/* 1010:1272 */       bv.setYAttribute(yatt);
/* 1011:     */       try
/* 1012:     */       {
/* 1013:1276 */         FileInputStream fis = new FileInputStream("colors.ser");
/* 1014:1277 */         ObjectInputStream ois = new ObjectInputStream(fis);
/* 1015:     */         
/* 1016:1279 */         ArrayList<Color> colors = (ArrayList)ois.readObject();
/* 1017:1280 */         bv.setColors(colors);
/* 1018:1281 */         ois.close();
/* 1019:     */       }
/* 1020:     */       catch (Exception ex)
/* 1021:     */       {
/* 1022:1283 */         System.err.println("No color map file");
/* 1023:     */       }
/* 1024:1285 */       bv.start();
/* 1025:     */     }
/* 1026:     */     catch (Exception ex)
/* 1027:     */     {
/* 1028:1287 */       ex.printStackTrace();
/* 1029:     */     }
/* 1030:     */   }
/* 1031:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.boundaryvisualizer.BoundaryPanel
 * JD-Core Version:    0.7.0.1
 */