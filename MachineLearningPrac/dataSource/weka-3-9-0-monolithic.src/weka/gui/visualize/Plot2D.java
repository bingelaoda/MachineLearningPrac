/*    1:     */ package weka.gui.visualize;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Color;
/*    5:     */ import java.awt.Container;
/*    6:     */ import java.awt.Font;
/*    7:     */ import java.awt.FontMetrics;
/*    8:     */ import java.awt.Graphics;
/*    9:     */ import java.awt.event.MouseAdapter;
/*   10:     */ import java.awt.event.MouseEvent;
/*   11:     */ import java.awt.event.WindowAdapter;
/*   12:     */ import java.awt.event.WindowEvent;
/*   13:     */ import java.io.BufferedReader;
/*   14:     */ import java.io.FileReader;
/*   15:     */ import java.io.PrintStream;
/*   16:     */ import java.io.Reader;
/*   17:     */ import java.util.ArrayList;
/*   18:     */ import java.util.Properties;
/*   19:     */ import java.util.Random;
/*   20:     */ import java.util.Vector;
/*   21:     */ import javax.swing.JFrame;
/*   22:     */ import javax.swing.JPanel;
/*   23:     */ import weka.core.Attribute;
/*   24:     */ import weka.core.Environment;
/*   25:     */ import weka.core.Instance;
/*   26:     */ import weka.core.Instances;
/*   27:     */ import weka.core.Settings;
/*   28:     */ import weka.core.Utils;
/*   29:     */ 
/*   30:     */ public class Plot2D
/*   31:     */   extends JPanel
/*   32:     */ {
/*   33:     */   private static final long serialVersionUID = -1673162410856660442L;
/*   34:     */   public static final int MAX_SHAPES = 5;
/*   35:     */   public static final int ERROR_SHAPE = 1000;
/*   36:     */   public static final int MISSING_SHAPE = 2000;
/*   37:     */   public static final int CONST_AUTOMATIC_SHAPE = -1;
/*   38:     */   public static final int X_SHAPE = 0;
/*   39:     */   public static final int PLUS_SHAPE = 1;
/*   40:     */   public static final int DIAMOND_SHAPE = 2;
/*   41:     */   public static final int TRIANGLEUP_SHAPE = 3;
/*   42:     */   public static final int TRIANGLEDOWN_SHAPE = 4;
/*   43:     */   public static final int DEFAULT_SHAPE_SIZE = 2;
/*   44:  71 */   protected Color m_axisColour = Color.green;
/*   45:  74 */   protected Color m_backgroundColour = Color.black;
/*   46:  77 */   protected ArrayList<PlotData2D> m_plots = new ArrayList();
/*   47:  80 */   protected PlotData2D m_masterPlot = null;
/*   48:  83 */   protected String m_masterName = "master plot";
/*   49:  86 */   protected Instances m_plotInstances = null;
/*   50:  94 */   protected Plot2DCompanion m_plotCompanion = null;
/*   51:  97 */   protected Class<?> m_InstanceInfoFrameClass = null;
/*   52: 100 */   protected JFrame m_InstanceInfo = null;
/*   53:     */   protected ArrayList<Color> m_colorList;
/*   54: 106 */   protected Color[] m_DefaultColors = { Color.blue, Color.red, Color.green, Color.cyan, Color.pink, new Color(255, 0, 255), Color.orange, new Color(255, 0, 0), new Color(0, 255, 0), Color.white };
/*   55: 114 */   protected int m_xIndex = 0;
/*   56: 115 */   protected int m_yIndex = 0;
/*   57: 116 */   protected int m_cIndex = 0;
/*   58: 117 */   protected int m_sIndex = 0;
/*   59:     */   protected double m_maxX;
/*   60:     */   protected double m_minX;
/*   61:     */   protected double m_maxY;
/*   62:     */   protected double m_minY;
/*   63:     */   protected double m_maxC;
/*   64:     */   protected double m_minC;
/*   65: 131 */   protected final int m_axisPad = 5;
/*   66: 134 */   protected final int m_tickSize = 5;
/*   67: 137 */   protected int m_XaxisStart = 0;
/*   68: 138 */   protected int m_YaxisStart = 0;
/*   69: 139 */   protected int m_XaxisEnd = 0;
/*   70: 140 */   protected int m_YaxisEnd = 0;
/*   71: 147 */   protected boolean m_plotResize = true;
/*   72: 150 */   protected boolean m_axisChanged = false;
/*   73:     */   protected int[][] m_drawnPoints;
/*   74:     */   protected Font m_labelFont;
/*   75: 161 */   protected FontMetrics m_labelMetrics = null;
/*   76: 164 */   protected int m_JitterVal = 0;
/*   77: 167 */   protected Random m_JRand = new Random(0L);
/*   78:     */   
/*   79:     */   public Plot2D()
/*   80:     */   {
/*   81: 172 */     setProperties();
/*   82: 173 */     setBackground(this.m_backgroundColour);
/*   83:     */     
/*   84: 175 */     this.m_drawnPoints = new int[getWidth()][getHeight()];
/*   85:     */     
/*   86:     */ 
/*   87: 178 */     this.m_colorList = new ArrayList(10);
/*   88: 179 */     for (int noa = this.m_colorList.size(); noa < 10; noa++)
/*   89:     */     {
/*   90: 180 */       Color pc = this.m_DefaultColors[(noa % 10)];
/*   91: 181 */       int ija = noa / 10;
/*   92: 182 */       ija *= 2;
/*   93: 183 */       for (int j = 0; j < ija; j++) {
/*   94: 184 */         pc = pc.darker();
/*   95:     */       }
/*   96: 187 */       this.m_colorList.add(pc);
/*   97:     */     }
/*   98:     */   }
/*   99:     */   
/*  100:     */   private void setProperties()
/*  101:     */   {
/*  102: 195 */     if (VisualizeUtils.VISUALIZE_PROPERTIES != null)
/*  103:     */     {
/*  104: 196 */       String thisClass = getClass().getName();
/*  105: 197 */       String axisKey = thisClass + ".axisColour";
/*  106: 198 */       String backgroundKey = thisClass + ".backgroundColour";
/*  107:     */       
/*  108: 200 */       String axisColour = VisualizeUtils.VISUALIZE_PROPERTIES.getProperty(axisKey);
/*  109: 202 */       if (axisColour != null) {
/*  110: 209 */         this.m_axisColour = VisualizeUtils.processColour(axisColour, this.m_axisColour);
/*  111:     */       }
/*  112: 212 */       String backgroundColour = VisualizeUtils.VISUALIZE_PROPERTIES.getProperty(backgroundKey);
/*  113: 214 */       if (backgroundColour != null) {
/*  114: 221 */         this.m_backgroundColour = VisualizeUtils.processColour(backgroundColour, this.m_backgroundColour);
/*  115:     */       }
/*  116:     */       try
/*  117:     */       {
/*  118: 226 */         this.m_InstanceInfoFrameClass = Class.forName(VisualizeUtils.VISUALIZE_PROPERTIES.getProperty(thisClass + ".instanceInfoFrame", "weka.gui.visualize.InstanceInfoFrame"));
/*  119:     */       }
/*  120:     */       catch (Exception e)
/*  121:     */       {
/*  122: 231 */         e.printStackTrace();
/*  123: 232 */         this.m_InstanceInfoFrameClass = InstanceInfoFrame.class;
/*  124:     */       }
/*  125:     */     }
/*  126:     */   }
/*  127:     */   
/*  128:     */   public void applySettings(Settings settings, String ownerID)
/*  129:     */   {
/*  130: 245 */     this.m_axisColour = ((Color)settings.getSetting(ownerID, VisualizeUtils.VisualizeDefaults.AXIS_COLOUR_KEY, VisualizeUtils.VisualizeDefaults.AXIS_COLOR, Environment.getSystemWide()));
/*  131:     */     
/*  132:     */ 
/*  133:     */ 
/*  134:     */ 
/*  135: 250 */     this.m_backgroundColour = ((Color)settings.getSetting(ownerID, VisualizeUtils.VisualizeDefaults.BACKGROUND_COLOUR_KEY, VisualizeUtils.VisualizeDefaults.BACKGROUND_COLOR, Environment.getSystemWide()));
/*  136:     */     
/*  137:     */ 
/*  138:     */ 
/*  139:     */ 
/*  140: 255 */     setBackground(this.m_backgroundColour);
/*  141:     */     
/*  142: 257 */     repaint();
/*  143:     */   }
/*  144:     */   
/*  145:     */   public void setPlotCompanion(Plot2DCompanion p)
/*  146:     */   {
/*  147: 268 */     this.m_plotCompanion = p;
/*  148:     */   }
/*  149:     */   
/*  150:     */   public void setJitter(int j)
/*  151:     */   {
/*  152: 277 */     if ((this.m_plotInstances.numAttributes() > 0) && (this.m_plotInstances.numInstances() > 0)) {
/*  153: 279 */       if (j >= 0)
/*  154:     */       {
/*  155: 280 */         this.m_JitterVal = j;
/*  156: 281 */         this.m_JRand = new Random(this.m_JitterVal);
/*  157:     */         
/*  158: 283 */         this.m_drawnPoints = new int[this.m_XaxisEnd - this.m_XaxisStart + 1][this.m_YaxisEnd - this.m_YaxisStart + 1];
/*  159:     */         
/*  160: 285 */         updatePturb();
/*  161:     */         
/*  162: 287 */         repaint();
/*  163:     */       }
/*  164:     */     }
/*  165:     */   }
/*  166:     */   
/*  167:     */   public void setColours(ArrayList<Color> cols)
/*  168:     */   {
/*  169: 299 */     this.m_colorList = cols;
/*  170:     */   }
/*  171:     */   
/*  172:     */   public void setXindex(int x)
/*  173:     */   {
/*  174: 308 */     this.m_xIndex = x;
/*  175: 309 */     for (int i = 0; i < this.m_plots.size(); i++) {
/*  176: 310 */       ((PlotData2D)this.m_plots.get(i)).setXindex(this.m_xIndex);
/*  177:     */     }
/*  178: 312 */     determineBounds();
/*  179: 313 */     if (this.m_JitterVal != 0) {
/*  180: 314 */       updatePturb();
/*  181:     */     }
/*  182: 316 */     this.m_axisChanged = true;
/*  183: 317 */     repaint();
/*  184:     */   }
/*  185:     */   
/*  186:     */   public void setYindex(int y)
/*  187:     */   {
/*  188: 326 */     this.m_yIndex = y;
/*  189: 327 */     for (int i = 0; i < this.m_plots.size(); i++) {
/*  190: 328 */       ((PlotData2D)this.m_plots.get(i)).setYindex(this.m_yIndex);
/*  191:     */     }
/*  192: 330 */     determineBounds();
/*  193: 331 */     if (this.m_JitterVal != 0) {
/*  194: 332 */       updatePturb();
/*  195:     */     }
/*  196: 334 */     this.m_axisChanged = true;
/*  197: 335 */     repaint();
/*  198:     */   }
/*  199:     */   
/*  200:     */   public void setCindex(int c)
/*  201:     */   {
/*  202: 344 */     this.m_cIndex = c;
/*  203: 345 */     for (int i = 0; i < this.m_plots.size(); i++) {
/*  204: 346 */       ((PlotData2D)this.m_plots.get(i)).setCindex(this.m_cIndex);
/*  205:     */     }
/*  206: 348 */     determineBounds();
/*  207: 349 */     this.m_axisChanged = true;
/*  208: 350 */     repaint();
/*  209:     */   }
/*  210:     */   
/*  211:     */   public ArrayList<PlotData2D> getPlots()
/*  212:     */   {
/*  213: 359 */     return this.m_plots;
/*  214:     */   }
/*  215:     */   
/*  216:     */   public PlotData2D getMasterPlot()
/*  217:     */   {
/*  218: 368 */     return this.m_masterPlot;
/*  219:     */   }
/*  220:     */   
/*  221:     */   public double getMaxX()
/*  222:     */   {
/*  223: 377 */     return this.m_maxX;
/*  224:     */   }
/*  225:     */   
/*  226:     */   public double getMaxY()
/*  227:     */   {
/*  228: 386 */     return this.m_maxY;
/*  229:     */   }
/*  230:     */   
/*  231:     */   public double getMinX()
/*  232:     */   {
/*  233: 395 */     return this.m_minX;
/*  234:     */   }
/*  235:     */   
/*  236:     */   public double getMinY()
/*  237:     */   {
/*  238: 404 */     return this.m_minY;
/*  239:     */   }
/*  240:     */   
/*  241:     */   public double getMaxC()
/*  242:     */   {
/*  243: 413 */     return this.m_maxC;
/*  244:     */   }
/*  245:     */   
/*  246:     */   public double getMinC()
/*  247:     */   {
/*  248: 422 */     return this.m_minC;
/*  249:     */   }
/*  250:     */   
/*  251:     */   public void setInstances(Instances inst)
/*  252:     */     throws Exception
/*  253:     */   {
/*  254: 433 */     PlotData2D tempPlot = new PlotData2D(inst);
/*  255: 434 */     tempPlot.setPlotName("master plot");
/*  256: 435 */     setMasterPlot(tempPlot);
/*  257:     */   }
/*  258:     */   
/*  259:     */   public void setMasterPlot(PlotData2D master)
/*  260:     */     throws Exception
/*  261:     */   {
/*  262: 445 */     if (master.m_plotInstances == null) {
/*  263: 446 */       throw new Exception("No instances in plot data!");
/*  264:     */     }
/*  265: 448 */     removeAllPlots();
/*  266: 449 */     this.m_masterPlot = master;
/*  267: 450 */     this.m_plots.add(this.m_masterPlot);
/*  268: 451 */     this.m_plotInstances = this.m_masterPlot.m_plotInstances;
/*  269:     */     
/*  270: 453 */     this.m_xIndex = 0;
/*  271: 454 */     this.m_yIndex = 0;
/*  272: 455 */     this.m_cIndex = 0;
/*  273:     */     
/*  274: 457 */     determineBounds();
/*  275:     */   }
/*  276:     */   
/*  277:     */   public void removeAllPlots()
/*  278:     */   {
/*  279: 464 */     this.m_masterPlot = null;
/*  280: 465 */     this.m_plotInstances = null;
/*  281: 466 */     this.m_plots = new ArrayList();
/*  282: 467 */     this.m_xIndex = 0;
/*  283: 468 */     this.m_yIndex = 0;
/*  284: 469 */     this.m_cIndex = 0;
/*  285:     */   }
/*  286:     */   
/*  287:     */   public void addPlot(PlotData2D newPlot)
/*  288:     */     throws Exception
/*  289:     */   {
/*  290: 479 */     if (newPlot.m_plotInstances == null) {
/*  291: 480 */       throw new Exception("No instances in plot data!");
/*  292:     */     }
/*  293: 483 */     if (this.m_masterPlot != null)
/*  294:     */     {
/*  295: 484 */       if (!this.m_masterPlot.m_plotInstances.equalHeaders(newPlot.m_plotInstances)) {
/*  296: 485 */         throw new Exception("Plot2D :Plot data's instances are incompatable  with master plot");
/*  297:     */       }
/*  298:     */     }
/*  299:     */     else
/*  300:     */     {
/*  301: 489 */       this.m_masterPlot = newPlot;
/*  302: 490 */       this.m_plotInstances = this.m_masterPlot.m_plotInstances;
/*  303:     */     }
/*  304: 492 */     this.m_plots.add(newPlot);
/*  305: 493 */     setXindex(this.m_xIndex);
/*  306: 494 */     setYindex(this.m_yIndex);
/*  307: 495 */     setCindex(this.m_cIndex);
/*  308:     */   }
/*  309:     */   
/*  310:     */   private void setFonts(Graphics gx)
/*  311:     */   {
/*  312: 504 */     if (this.m_labelMetrics == null)
/*  313:     */     {
/*  314: 505 */       this.m_labelFont = new Font("Monospaced", 0, 12);
/*  315: 506 */       this.m_labelMetrics = gx.getFontMetrics(this.m_labelFont);
/*  316:     */     }
/*  317: 508 */     gx.setFont(this.m_labelFont);
/*  318:     */   }
/*  319:     */   
/*  320:     */   public void searchPoints(int x, int y, final boolean newFrame)
/*  321:     */   {
/*  322: 520 */     if (this.m_masterPlot.m_plotInstances != null)
/*  323:     */     {
/*  324: 521 */       int longest = 0;
/*  325: 522 */       for (int j = 0; j < this.m_masterPlot.m_plotInstances.numAttributes(); j++) {
/*  326: 523 */         if (this.m_masterPlot.m_plotInstances.attribute(j).name().length() > longest) {
/*  327: 524 */           longest = this.m_masterPlot.m_plotInstances.attribute(j).name().length();
/*  328:     */         }
/*  329:     */       }
/*  330: 528 */       StringBuffer insts = new StringBuffer();
/*  331: 529 */       Vector<Instances> data = new Vector();
/*  332: 530 */       for (int jj = 0; jj < this.m_plots.size(); jj++)
/*  333:     */       {
/*  334: 531 */         PlotData2D temp_plot = (PlotData2D)this.m_plots.get(jj);
/*  335: 532 */         data.add(new Instances(temp_plot.m_plotInstances, 0));
/*  336: 534 */         for (int i = 0; i < temp_plot.m_plotInstances.numInstances(); i++) {
/*  337: 535 */           if (temp_plot.m_pointLookup[i][0] != (-1.0D / 0.0D))
/*  338:     */           {
/*  339: 536 */             double px = temp_plot.m_pointLookup[i][0] + temp_plot.m_pointLookup[i][2];
/*  340:     */             
/*  341: 538 */             double py = temp_plot.m_pointLookup[i][1] + temp_plot.m_pointLookup[i][3];
/*  342:     */             
/*  343:     */ 
/*  344: 541 */             double size = temp_plot.m_shapeSize[i];
/*  345: 542 */             if ((x >= px - size) && (x <= px + size) && (y >= py - size) && (y <= py + size))
/*  346:     */             {
/*  347: 545 */               ((Instances)data.get(jj)).add((Instance)temp_plot.m_plotInstances.instance(i).copy());
/*  348:     */               
/*  349: 547 */               insts.append("\nPlot : " + temp_plot.m_plotName + "\nInstance: " + (i + 1) + "\n");
/*  350: 549 */               for (int j = 0; j < temp_plot.m_plotInstances.numAttributes(); j++)
/*  351:     */               {
/*  352: 550 */                 for (int k = 0; k < longest - temp_plot.m_plotInstances.attribute(j).name().length(); k++) {
/*  353: 552 */                   insts.append(" ");
/*  354:     */                 }
/*  355: 554 */                 insts.append(temp_plot.m_plotInstances.attribute(j).name());
/*  356: 555 */                 insts.append(" : ");
/*  357: 557 */                 if (temp_plot.m_plotInstances.instance(i).isMissing(j)) {
/*  358: 558 */                   insts.append("Missing");
/*  359: 559 */                 } else if ((temp_plot.m_plotInstances.attribute(j).isNominal()) || (temp_plot.m_plotInstances.attribute(j).isString())) {
/*  360: 561 */                   insts.append(temp_plot.m_plotInstances.attribute(j).value((int)temp_plot.m_plotInstances.instance(i).value(j)));
/*  361:     */                 } else {
/*  362: 564 */                   insts.append(temp_plot.m_plotInstances.instance(i).value(j));
/*  363:     */                 }
/*  364: 567 */                 insts.append("\n");
/*  365:     */               }
/*  366:     */             }
/*  367:     */           }
/*  368:     */         }
/*  369:     */       }
/*  370: 575 */       int i = 0;
/*  371: 576 */       while (data.size() > i) {
/*  372: 577 */         if (((Instances)data.get(i)).numInstances() == 0) {
/*  373: 578 */           data.remove(i);
/*  374:     */         } else {
/*  375: 580 */           i++;
/*  376:     */         }
/*  377:     */       }
/*  378: 584 */       if (insts.length() > 0) {
/*  379: 586 */         if ((newFrame) || (this.m_InstanceInfo == null))
/*  380:     */         {
/*  381:     */           try
/*  382:     */           {
/*  383: 588 */             final JFrame jf = (JFrame)this.m_InstanceInfoFrameClass.newInstance();
/*  384: 589 */             ((InstanceInfo)jf).setInfoText(insts.toString());
/*  385: 590 */             ((InstanceInfo)jf).setInfoData(data);
/*  386: 591 */             final JFrame testf = this.m_InstanceInfo;
/*  387: 592 */             jf.addWindowListener(new WindowAdapter()
/*  388:     */             {
/*  389:     */               public void windowClosing(WindowEvent e)
/*  390:     */               {
/*  391: 595 */                 if ((!newFrame) || (testf == null)) {
/*  392: 596 */                   Plot2D.this.m_InstanceInfo = null;
/*  393:     */                 }
/*  394: 598 */                 jf.dispose();
/*  395:     */               }
/*  396: 600 */             });
/*  397: 601 */             jf.setVisible(true);
/*  398: 602 */             if (this.m_InstanceInfo == null) {
/*  399: 603 */               this.m_InstanceInfo = jf;
/*  400:     */             }
/*  401:     */           }
/*  402:     */           catch (Exception e)
/*  403:     */           {
/*  404: 606 */             e.printStackTrace();
/*  405:     */           }
/*  406:     */         }
/*  407:     */         else
/*  408:     */         {
/*  409: 610 */           ((InstanceInfo)this.m_InstanceInfo).setInfoText(insts.toString());
/*  410: 611 */           ((InstanceInfo)this.m_InstanceInfo).setInfoData(data);
/*  411:     */         }
/*  412:     */       }
/*  413:     */     }
/*  414:     */   }
/*  415:     */   
/*  416:     */   public void determineBounds()
/*  417:     */   {
/*  418: 624 */     this.m_minX = ((PlotData2D)this.m_plots.get(0)).m_minX;
/*  419: 625 */     this.m_maxX = ((PlotData2D)this.m_plots.get(0)).m_maxX;
/*  420: 626 */     this.m_minY = ((PlotData2D)this.m_plots.get(0)).m_minY;
/*  421: 627 */     this.m_maxY = ((PlotData2D)this.m_plots.get(0)).m_maxY;
/*  422: 628 */     this.m_minC = ((PlotData2D)this.m_plots.get(0)).m_minC;
/*  423: 629 */     this.m_maxC = ((PlotData2D)this.m_plots.get(0)).m_maxC;
/*  424: 630 */     for (int i = 1; i < this.m_plots.size(); i++)
/*  425:     */     {
/*  426: 631 */       double value = ((PlotData2D)this.m_plots.get(i)).m_minX;
/*  427: 632 */       if (value < this.m_minX) {
/*  428: 633 */         this.m_minX = value;
/*  429:     */       }
/*  430: 635 */       value = ((PlotData2D)this.m_plots.get(i)).m_maxX;
/*  431: 636 */       if (value > this.m_maxX) {
/*  432: 637 */         this.m_maxX = value;
/*  433:     */       }
/*  434: 639 */       value = ((PlotData2D)this.m_plots.get(i)).m_minY;
/*  435: 640 */       if (value < this.m_minY) {
/*  436: 641 */         this.m_minY = value;
/*  437:     */       }
/*  438: 643 */       value = ((PlotData2D)this.m_plots.get(i)).m_maxY;
/*  439: 644 */       if (value > this.m_maxY) {
/*  440: 645 */         this.m_maxY = value;
/*  441:     */       }
/*  442: 647 */       value = ((PlotData2D)this.m_plots.get(i)).m_minC;
/*  443: 648 */       if (value < this.m_minC) {
/*  444: 649 */         this.m_minC = value;
/*  445:     */       }
/*  446: 651 */       value = ((PlotData2D)this.m_plots.get(i)).m_maxC;
/*  447: 652 */       if (value > this.m_maxC) {
/*  448: 653 */         this.m_maxC = value;
/*  449:     */       }
/*  450:     */     }
/*  451: 657 */     fillLookup();
/*  452: 658 */     repaint();
/*  453:     */   }
/*  454:     */   
/*  455:     */   public double convertToAttribX(double scx)
/*  456:     */   {
/*  457: 671 */     double temp = this.m_XaxisEnd - this.m_XaxisStart;
/*  458: 672 */     double temp2 = (scx - this.m_XaxisStart) * (this.m_maxX - this.m_minX) / temp;
/*  459:     */     
/*  460: 674 */     temp2 += this.m_minX;
/*  461:     */     
/*  462: 676 */     return temp2;
/*  463:     */   }
/*  464:     */   
/*  465:     */   public double convertToAttribY(double scy)
/*  466:     */   {
/*  467: 686 */     double temp = this.m_YaxisEnd - this.m_YaxisStart;
/*  468: 687 */     double temp2 = (scy - this.m_YaxisEnd) * (this.m_maxY - this.m_minY) / temp;
/*  469:     */     
/*  470: 689 */     temp2 = -(temp2 - this.m_minY);
/*  471:     */     
/*  472: 691 */     return temp2;
/*  473:     */   }
/*  474:     */   
/*  475:     */   int pturbX(double xvalP, double xj)
/*  476:     */   {
/*  477: 705 */     int xpturb = 0;
/*  478: 706 */     if (this.m_JitterVal > 0)
/*  479:     */     {
/*  480: 707 */       xpturb = (int)(this.m_JitterVal * (xj / 2.0D));
/*  481: 708 */       if ((xvalP + xpturb < this.m_XaxisStart) || (xvalP + xpturb > this.m_XaxisEnd)) {
/*  482: 709 */         xpturb *= -1;
/*  483:     */       }
/*  484:     */     }
/*  485: 712 */     return xpturb;
/*  486:     */   }
/*  487:     */   
/*  488:     */   public double convertToPanelX(double xval)
/*  489:     */   {
/*  490: 722 */     double temp = (xval - this.m_minX) / (this.m_maxX - this.m_minX);
/*  491: 723 */     double temp2 = temp * (this.m_XaxisEnd - this.m_XaxisStart);
/*  492:     */     
/*  493: 725 */     temp2 += this.m_XaxisStart;
/*  494:     */     
/*  495: 727 */     return temp2;
/*  496:     */   }
/*  497:     */   
/*  498:     */   int pturbY(double yvalP, double yj)
/*  499:     */   {
/*  500: 739 */     int ypturb = 0;
/*  501: 740 */     if (this.m_JitterVal > 0)
/*  502:     */     {
/*  503: 741 */       ypturb = (int)(this.m_JitterVal * (yj / 2.0D));
/*  504: 742 */       if ((yvalP + ypturb < this.m_YaxisStart) || (yvalP + ypturb > this.m_YaxisEnd)) {
/*  505: 743 */         ypturb *= -1;
/*  506:     */       }
/*  507:     */     }
/*  508: 746 */     return ypturb;
/*  509:     */   }
/*  510:     */   
/*  511:     */   public double convertToPanelY(double yval)
/*  512:     */   {
/*  513: 756 */     double temp = (yval - this.m_minY) / (this.m_maxY - this.m_minY);
/*  514: 757 */     double temp2 = temp * (this.m_YaxisEnd - this.m_YaxisStart);
/*  515:     */     
/*  516: 759 */     temp2 = this.m_YaxisEnd - temp2;
/*  517:     */     
/*  518: 761 */     return temp2;
/*  519:     */   }
/*  520:     */   
/*  521:     */   private static void drawX(Graphics gx, double x, double y, int size)
/*  522:     */   {
/*  523: 773 */     gx.drawLine((int)(x - size), (int)(y - size), (int)(x + size), (int)(y + size));
/*  524:     */     
/*  525:     */ 
/*  526: 776 */     gx.drawLine((int)(x + size), (int)(y - size), (int)(x - size), (int)(y + size));
/*  527:     */   }
/*  528:     */   
/*  529:     */   private static void drawPlus(Graphics gx, double x, double y, int size)
/*  530:     */   {
/*  531: 789 */     gx.drawLine((int)(x - size), (int)y, (int)(x + size), (int)y);
/*  532:     */     
/*  533: 791 */     gx.drawLine((int)x, (int)(y - size), (int)x, (int)(y + size));
/*  534:     */   }
/*  535:     */   
/*  536:     */   private static void drawDiamond(Graphics gx, double x, double y, int size)
/*  537:     */   {
/*  538: 803 */     gx.drawLine((int)(x - size), (int)y, (int)x, (int)(y - size));
/*  539:     */     
/*  540: 805 */     gx.drawLine((int)x, (int)(y - size), (int)(x + size), (int)y);
/*  541:     */     
/*  542: 807 */     gx.drawLine((int)(x + size), (int)y, (int)x, (int)(y + size));
/*  543:     */     
/*  544: 809 */     gx.drawLine((int)x, (int)(y + size), (int)(x - size), (int)y);
/*  545:     */   }
/*  546:     */   
/*  547:     */   private static void drawTriangleUp(Graphics gx, double x, double y, int size)
/*  548:     */   {
/*  549: 821 */     gx.drawLine((int)x, (int)(y - size), (int)(x - size), (int)(y + size));
/*  550:     */     
/*  551: 823 */     gx.drawLine((int)(x - size), (int)(y + size), (int)(x + size), (int)(y + size));
/*  552:     */     
/*  553:     */ 
/*  554: 826 */     gx.drawLine((int)(x + size), (int)(y + size), (int)x, (int)(y - size));
/*  555:     */   }
/*  556:     */   
/*  557:     */   private static void drawTriangleDown(Graphics gx, double x, double y, int size)
/*  558:     */   {
/*  559: 840 */     gx.drawLine((int)x, (int)(y + size), (int)(x - size), (int)(y - size));
/*  560:     */     
/*  561: 842 */     gx.drawLine((int)(x - size), (int)(y - size), (int)(x + size), (int)(y - size));
/*  562:     */     
/*  563:     */ 
/*  564: 845 */     gx.drawLine((int)(x + size), (int)(y - size), (int)x, (int)(y + size));
/*  565:     */   }
/*  566:     */   
/*  567:     */   protected static void drawDataPoint(double x, double y, double xprev, double yprev, int size, int shape, Graphics gx)
/*  568:     */   {
/*  569: 866 */     drawDataPoint(x, y, size, shape, gx);
/*  570:     */     
/*  571:     */ 
/*  572: 869 */     gx.drawLine((int)x, (int)y, (int)xprev, (int)yprev);
/*  573:     */   }
/*  574:     */   
/*  575:     */   protected static void drawDataPoint(double x, double y, int size, int shape, Graphics gx)
/*  576:     */   {
/*  577: 886 */     Font lf = new Font("Monospaced", 0, 12);
/*  578: 887 */     FontMetrics fm = gx.getFontMetrics(lf);
/*  579: 889 */     if (size == 0) {
/*  580: 890 */       size = 1;
/*  581:     */     }
/*  582: 893 */     if ((shape != 1000) && (shape != 2000)) {
/*  583: 894 */       shape %= 5;
/*  584:     */     }
/*  585: 897 */     switch (shape)
/*  586:     */     {
/*  587:     */     case 0: 
/*  588: 899 */       drawX(gx, x, y, size);
/*  589: 900 */       break;
/*  590:     */     case 1: 
/*  591: 902 */       drawPlus(gx, x, y, size);
/*  592: 903 */       break;
/*  593:     */     case 2: 
/*  594: 905 */       drawDiamond(gx, x, y, size);
/*  595: 906 */       break;
/*  596:     */     case 3: 
/*  597: 908 */       drawTriangleUp(gx, x, y, size);
/*  598: 909 */       break;
/*  599:     */     case 4: 
/*  600: 911 */       drawTriangleDown(gx, x, y, size);
/*  601: 912 */       break;
/*  602:     */     case 1000: 
/*  603: 914 */       gx.drawRect((int)(x - size), (int)(y - size), size * 2, size * 2);
/*  604: 915 */       break;
/*  605:     */     case 2000: 
/*  606: 917 */       int hf = fm.getAscent();
/*  607: 918 */       int width = fm.stringWidth("M");
/*  608: 919 */       gx.drawString("M", (int)(x - width / 2), (int)(y + hf / 2));
/*  609:     */     }
/*  610:     */   }
/*  611:     */   
/*  612:     */   private void updatePturb()
/*  613:     */   {
/*  614: 928 */     double xj = 0.0D;
/*  615: 929 */     double yj = 0.0D;
/*  616: 930 */     for (int j = 0; j < this.m_plots.size(); j++)
/*  617:     */     {
/*  618: 931 */       PlotData2D temp_plot = (PlotData2D)this.m_plots.get(j);
/*  619: 932 */       for (int i = 0; i < temp_plot.m_plotInstances.numInstances(); i++) {
/*  620: 933 */         if ((!temp_plot.m_plotInstances.instance(i).isMissing(this.m_xIndex)) && (!temp_plot.m_plotInstances.instance(i).isMissing(this.m_yIndex)))
/*  621:     */         {
/*  622: 936 */           if (this.m_JitterVal > 0)
/*  623:     */           {
/*  624: 937 */             xj = this.m_JRand.nextGaussian();
/*  625: 938 */             yj = this.m_JRand.nextGaussian();
/*  626:     */           }
/*  627: 940 */           temp_plot.m_pointLookup[i][2] = pturbX(temp_plot.m_pointLookup[i][0], xj);
/*  628:     */           
/*  629: 942 */           temp_plot.m_pointLookup[i][3] = pturbY(temp_plot.m_pointLookup[i][1], yj);
/*  630:     */         }
/*  631:     */       }
/*  632:     */     }
/*  633:     */   }
/*  634:     */   
/*  635:     */   private void fillLookup()
/*  636:     */   {
/*  637: 955 */     for (int j = 0; j < this.m_plots.size(); j++)
/*  638:     */     {
/*  639: 956 */       PlotData2D temp_plot = (PlotData2D)this.m_plots.get(j);
/*  640: 958 */       if ((temp_plot.m_plotInstances.numInstances() > 0) && (temp_plot.m_plotInstances.numAttributes() > 0)) {
/*  641: 960 */         for (int i = 0; i < temp_plot.m_plotInstances.numInstances(); i++) {
/*  642: 961 */           if ((temp_plot.m_plotInstances.instance(i).isMissing(this.m_xIndex)) || (temp_plot.m_plotInstances.instance(i).isMissing(this.m_yIndex)))
/*  643:     */           {
/*  644: 963 */             temp_plot.m_pointLookup[i][0] = (-1.0D / 0.0D);
/*  645: 964 */             temp_plot.m_pointLookup[i][1] = (-1.0D / 0.0D);
/*  646:     */           }
/*  647:     */           else
/*  648:     */           {
/*  649: 966 */             double x = convertToPanelX(temp_plot.m_plotInstances.instance(i).value(this.m_xIndex));
/*  650:     */             
/*  651:     */ 
/*  652: 969 */             double y = convertToPanelY(temp_plot.m_plotInstances.instance(i).value(this.m_yIndex));
/*  653:     */             
/*  654:     */ 
/*  655: 972 */             temp_plot.m_pointLookup[i][0] = x;
/*  656: 973 */             temp_plot.m_pointLookup[i][1] = y;
/*  657:     */           }
/*  658:     */         }
/*  659:     */       }
/*  660:     */     }
/*  661:     */   }
/*  662:     */   
/*  663:     */   private void paintData(Graphics gx)
/*  664:     */   {
/*  665: 987 */     for (int j = 0; j < this.m_plots.size(); j++)
/*  666:     */     {
/*  667: 988 */       PlotData2D temp_plot = (PlotData2D)this.m_plots.get(j);
/*  668: 990 */       for (int i = 0; i < temp_plot.m_plotInstances.numInstances(); i++) {
/*  669: 991 */         if ((!temp_plot.m_plotInstances.instance(i).isMissing(this.m_xIndex)) && (!temp_plot.m_plotInstances.instance(i).isMissing(this.m_yIndex)))
/*  670:     */         {
/*  671: 994 */           double x = temp_plot.m_pointLookup[i][0] + temp_plot.m_pointLookup[i][2];
/*  672:     */           
/*  673: 996 */           double y = temp_plot.m_pointLookup[i][1] + temp_plot.m_pointLookup[i][3];
/*  674:     */           
/*  675:     */ 
/*  676: 999 */           double prevx = 0.0D;
/*  677:1000 */           double prevy = 0.0D;
/*  678:1001 */           if (i > 0)
/*  679:     */           {
/*  680:1002 */             prevx = temp_plot.m_pointLookup[(i - 1)][0] + temp_plot.m_pointLookup[(i - 1)][2];
/*  681:     */             
/*  682:1004 */             prevy = temp_plot.m_pointLookup[(i - 1)][1] + temp_plot.m_pointLookup[(i - 1)][3];
/*  683:     */           }
/*  684:1008 */           int x_range = (int)x - this.m_XaxisStart;
/*  685:1009 */           int y_range = (int)y - this.m_YaxisStart;
/*  686:1011 */           if ((x_range >= 0) && (y_range >= 0) && (
/*  687:1012 */             (this.m_drawnPoints[x_range][y_range] == i) || (this.m_drawnPoints[x_range][y_range] == 0) || (temp_plot.m_shapeSize[i] == temp_plot.m_alwaysDisplayPointsOfThisSize) || (temp_plot.m_displayAllPoints == true)))
/*  688:     */           {
/*  689:1016 */             this.m_drawnPoints[x_range][y_range] = i;
/*  690:1017 */             if (temp_plot.m_plotInstances.attribute(this.m_cIndex).isNominal())
/*  691:     */             {
/*  692:1018 */               if ((temp_plot.m_plotInstances.attribute(this.m_cIndex).numValues() > this.m_colorList.size()) && (!temp_plot.m_useCustomColour)) {
/*  693:1020 */                 extendColourMap(temp_plot.m_plotInstances.attribute(this.m_cIndex).numValues());
/*  694:     */               }
/*  695:     */               Color ci;
/*  696:     */               Color ci;
/*  697:1025 */               if (temp_plot.m_plotInstances.instance(i).isMissing(this.m_cIndex))
/*  698:     */               {
/*  699:1026 */                 ci = Color.gray;
/*  700:     */               }
/*  701:     */               else
/*  702:     */               {
/*  703:1028 */                 int ind = (int)temp_plot.m_plotInstances.instance(i).value(this.m_cIndex);
/*  704:     */                 
/*  705:1030 */                 ci = (Color)this.m_colorList.get(ind);
/*  706:     */               }
/*  707:1033 */               if (!temp_plot.m_useCustomColour) {
/*  708:1034 */                 gx.setColor(ci);
/*  709:     */               } else {
/*  710:1036 */                 gx.setColor(temp_plot.m_customColour);
/*  711:     */               }
/*  712:1039 */               if (temp_plot.m_plotInstances.instance(i).isMissing(this.m_cIndex))
/*  713:     */               {
/*  714:1040 */                 if (temp_plot.m_connectPoints[i] == 1) {
/*  715:1041 */                   drawDataPoint(x, y, prevx, prevy, temp_plot.m_shapeSize[i], 2000, gx);
/*  716:     */                 } else {
/*  717:1044 */                   drawDataPoint(x, y, temp_plot.m_shapeSize[i], 2000, gx);
/*  718:     */                 }
/*  719:     */               }
/*  720:1048 */               else if (temp_plot.m_shapeType[i] == -1)
/*  721:     */               {
/*  722:1049 */                 if (temp_plot.m_connectPoints[i] == 1) {
/*  723:1050 */                   drawDataPoint(x, y, prevx, prevy, temp_plot.m_shapeSize[i], j, gx);
/*  724:     */                 } else {
/*  725:1053 */                   drawDataPoint(x, y, temp_plot.m_shapeSize[i], j, gx);
/*  726:     */                 }
/*  727:     */               }
/*  728:1056 */               else if (temp_plot.m_connectPoints[i] == 1) {
/*  729:1057 */                 drawDataPoint(x, y, prevx, prevy, temp_plot.m_shapeSize[i], temp_plot.m_shapeType[i], gx);
/*  730:     */               } else {
/*  731:1060 */                 drawDataPoint(x, y, temp_plot.m_shapeSize[i], temp_plot.m_shapeType[i], gx);
/*  732:     */               }
/*  733:     */             }
/*  734:     */             else
/*  735:     */             {
/*  736:1067 */               Color ci = null;
/*  737:1068 */               if (!temp_plot.m_plotInstances.instance(i).isMissing(this.m_cIndex))
/*  738:     */               {
/*  739:1069 */                 double r = (temp_plot.m_plotInstances.instance(i).value(this.m_cIndex) - this.m_minC) / (this.m_maxC - this.m_minC);
/*  740:     */                 
/*  741:     */ 
/*  742:1072 */                 r = r * 240.0D + 15.0D;
/*  743:1073 */                 ci = new Color((int)r, 150, (int)(255.0D - r));
/*  744:     */               }
/*  745:     */               else
/*  746:     */               {
/*  747:1075 */                 ci = Color.gray;
/*  748:     */               }
/*  749:1077 */               if (!temp_plot.m_useCustomColour) {
/*  750:1078 */                 gx.setColor(ci);
/*  751:     */               } else {
/*  752:1080 */                 gx.setColor(temp_plot.m_customColour);
/*  753:     */               }
/*  754:1082 */               if (temp_plot.m_plotInstances.instance(i).isMissing(this.m_cIndex))
/*  755:     */               {
/*  756:1083 */                 if (temp_plot.m_connectPoints[i] == 1) {
/*  757:1084 */                   drawDataPoint(x, y, prevx, prevy, temp_plot.m_shapeSize[i], 2000, gx);
/*  758:     */                 } else {
/*  759:1087 */                   drawDataPoint(x, y, temp_plot.m_shapeSize[i], 2000, gx);
/*  760:     */                 }
/*  761:     */               }
/*  762:1091 */               else if (temp_plot.m_shapeType[i] == -1)
/*  763:     */               {
/*  764:1092 */                 if (temp_plot.m_connectPoints[i] == 1) {
/*  765:1093 */                   drawDataPoint(x, y, prevx, prevy, temp_plot.m_shapeSize[i], j, gx);
/*  766:     */                 } else {
/*  767:1096 */                   drawDataPoint(x, y, temp_plot.m_shapeSize[i], j, gx);
/*  768:     */                 }
/*  769:     */               }
/*  770:1099 */               else if (temp_plot.m_connectPoints[i] == 1) {
/*  771:1100 */                 drawDataPoint(x, y, prevx, prevy, temp_plot.m_shapeSize[i], temp_plot.m_shapeType[i], gx);
/*  772:     */               } else {
/*  773:1103 */                 drawDataPoint(x, y, temp_plot.m_shapeSize[i], temp_plot.m_shapeType[i], gx);
/*  774:     */               }
/*  775:     */             }
/*  776:     */           }
/*  777:     */         }
/*  778:     */       }
/*  779:     */     }
/*  780:     */   }
/*  781:     */   
/*  782:     */   private void paintAxis(Graphics gx)
/*  783:     */   {
/*  784:1157 */     setFonts(gx);
/*  785:1158 */     int mxs = this.m_XaxisStart;
/*  786:1159 */     int mxe = this.m_XaxisEnd;
/*  787:1160 */     int mys = this.m_YaxisStart;
/*  788:1161 */     int mye = this.m_YaxisEnd;
/*  789:1162 */     this.m_plotResize = false;
/*  790:     */     
/*  791:1164 */     int h = getHeight();
/*  792:1165 */     int w = getWidth();
/*  793:1166 */     int hf = this.m_labelMetrics.getAscent();
/*  794:1167 */     int mswx = 0;
/*  795:1168 */     int mswy = 0;
/*  796:     */     
/*  797:     */ 
/*  798:1171 */     int precisionXmax = 1;
/*  799:1172 */     int precisionXmin = 1;
/*  800:1173 */     int precisionXmid = 1;
/*  801:     */     
/*  802:     */ 
/*  803:     */ 
/*  804:     */ 
/*  805:     */ 
/*  806:1179 */     int whole = (int)Math.abs(this.m_maxX);
/*  807:1180 */     double decimal = Math.abs(this.m_maxX) - whole;
/*  808:     */     
/*  809:1182 */     int nondecimal = whole > 0 ? (int)(Math.log(whole) / Math.log(10.0D)) : 1;
/*  810:     */     
/*  811:1184 */     precisionXmax = decimal > 0.0D ? (int)Math.abs(Math.log(Math.abs(this.m_maxX)) / Math.log(10.0D)) + 2 : 1;
/*  812:1187 */     if (precisionXmax > VisualizeUtils.MAX_PRECISION) {
/*  813:1188 */       precisionXmax = 1;
/*  814:     */     }
/*  815:1191 */     String maxStringX = Utils.doubleToString(this.m_maxX, nondecimal + 1 + precisionXmax, precisionXmax);
/*  816:     */     
/*  817:     */ 
/*  818:     */ 
/*  819:1195 */     whole = (int)Math.abs(this.m_minX);
/*  820:1196 */     decimal = Math.abs(this.m_minX) - whole;
/*  821:1197 */     nondecimal = whole > 0 ? (int)(Math.log(whole) / Math.log(10.0D)) : 1;
/*  822:1198 */     precisionXmin = decimal > 0.0D ? (int)Math.abs(Math.log(Math.abs(this.m_minX)) / Math.log(10.0D)) + 2 : 1;
/*  823:1201 */     if (precisionXmin > VisualizeUtils.MAX_PRECISION) {
/*  824:1202 */       precisionXmin = 1;
/*  825:     */     }
/*  826:1205 */     String minStringX = Utils.doubleToString(this.m_minX, nondecimal + 1 + precisionXmin, precisionXmin);
/*  827:     */     
/*  828:     */ 
/*  829:     */ 
/*  830:1209 */     mswx = this.m_labelMetrics.stringWidth(maxStringX);
/*  831:     */     
/*  832:1211 */     int precisionYmax = 1;
/*  833:1212 */     int precisionYmin = 1;
/*  834:1213 */     int precisionYmid = 1;
/*  835:1214 */     whole = (int)Math.abs(this.m_maxY);
/*  836:1215 */     decimal = Math.abs(this.m_maxY) - whole;
/*  837:1216 */     nondecimal = whole > 0 ? (int)(Math.log(whole) / Math.log(10.0D)) : 1;
/*  838:1217 */     precisionYmax = decimal > 0.0D ? (int)Math.abs(Math.log(Math.abs(this.m_maxY)) / Math.log(10.0D)) + 2 : 1;
/*  839:1220 */     if (precisionYmax > VisualizeUtils.MAX_PRECISION) {
/*  840:1221 */       precisionYmax = 1;
/*  841:     */     }
/*  842:1224 */     String maxStringY = Utils.doubleToString(this.m_maxY, nondecimal + 1 + precisionYmax, precisionYmax);
/*  843:     */     
/*  844:     */ 
/*  845:     */ 
/*  846:1228 */     whole = (int)Math.abs(this.m_minY);
/*  847:1229 */     decimal = Math.abs(this.m_minY) - whole;
/*  848:1230 */     nondecimal = whole > 0 ? (int)(Math.log(whole) / Math.log(10.0D)) : 1;
/*  849:1231 */     precisionYmin = decimal > 0.0D ? (int)Math.abs(Math.log(Math.abs(this.m_minY)) / Math.log(10.0D)) + 2 : 1;
/*  850:1234 */     if (precisionYmin > VisualizeUtils.MAX_PRECISION) {
/*  851:1235 */       precisionYmin = 1;
/*  852:     */     }
/*  853:1238 */     String minStringY = Utils.doubleToString(this.m_minY, nondecimal + 1 + precisionYmin, precisionYmin);
/*  854:1242 */     if (this.m_plotInstances.attribute(this.m_yIndex).isNumeric())
/*  855:     */     {
/*  856:1243 */       mswy = this.m_labelMetrics.stringWidth(maxStringY) > this.m_labelMetrics.stringWidth(minStringY) ? this.m_labelMetrics.stringWidth(maxStringY) : this.m_labelMetrics.stringWidth(minStringY);
/*  857:     */       
/*  858:     */ 
/*  859:     */ 
/*  860:1247 */       mswy += this.m_labelMetrics.stringWidth("M");
/*  861:     */     }
/*  862:     */     else
/*  863:     */     {
/*  864:1249 */       mswy = this.m_labelMetrics.stringWidth("MM");
/*  865:     */     }
/*  866:1252 */     this.m_YaxisStart = 5;
/*  867:1253 */     this.m_XaxisStart = (10 + mswy);
/*  868:     */     
/*  869:1255 */     this.m_XaxisEnd = (w - 5 - mswx / 2);
/*  870:     */     
/*  871:1257 */     this.m_YaxisEnd = (h - 5 - 2 * hf - 5);
/*  872:     */     
/*  873:     */ 
/*  874:1260 */     gx.setColor(this.m_axisColour);
/*  875:1261 */     if (this.m_plotInstances.attribute(this.m_xIndex).isNumeric())
/*  876:     */     {
/*  877:1262 */       if (w > 2 * mswx)
/*  878:     */       {
/*  879:1264 */         gx.drawString(maxStringX, this.m_XaxisEnd - mswx / 2, this.m_YaxisEnd + hf + 5);
/*  880:     */         
/*  881:     */ 
/*  882:1267 */         mswx = this.m_labelMetrics.stringWidth(minStringX);
/*  883:1268 */         gx.drawString(minStringX, this.m_XaxisStart - mswx / 2, this.m_YaxisEnd + hf + 5);
/*  884:1272 */         if ((w > 3 * mswx) && (this.m_plotInstances.attribute(this.m_xIndex).isNumeric()))
/*  885:     */         {
/*  886:1273 */           double mid = this.m_minX + (this.m_maxX - this.m_minX) / 2.0D;
/*  887:1274 */           whole = (int)Math.abs(mid);
/*  888:1275 */           decimal = Math.abs(mid) - whole;
/*  889:1276 */           nondecimal = whole > 0 ? (int)(Math.log(whole) / Math.log(10.0D)) : 1;
/*  890:1277 */           precisionXmid = decimal > 0.0D ? (int)Math.abs(Math.log(Math.abs(mid)) / Math.log(10.0D)) + 2 : 1;
/*  891:1280 */           if (precisionXmid > VisualizeUtils.MAX_PRECISION) {
/*  892:1281 */             precisionXmid = 1;
/*  893:     */           }
/*  894:1284 */           String maxString = Utils.doubleToString(mid, nondecimal + 1 + precisionXmid, precisionXmid);
/*  895:     */           
/*  896:     */ 
/*  897:1287 */           int sw = this.m_labelMetrics.stringWidth(maxString);
/*  898:1288 */           double mx = this.m_XaxisStart + (this.m_XaxisEnd - this.m_XaxisStart) / 2.0D;
/*  899:1289 */           gx.drawString(maxString, (int)(mx - sw / 2.0D), this.m_YaxisEnd + hf + 5);
/*  900:     */           
/*  901:1291 */           gx.drawLine((int)mx, this.m_YaxisEnd, (int)mx, this.m_YaxisEnd + 5);
/*  902:     */         }
/*  903:     */       }
/*  904:     */     }
/*  905:     */     else
/*  906:     */     {
/*  907:1295 */       int numValues = this.m_plotInstances.attribute(this.m_xIndex).numValues();
/*  908:1296 */       int maxXStringWidth = (this.m_XaxisEnd - this.m_XaxisStart) / numValues;
/*  909:1298 */       for (int i = 0; i < numValues; i++)
/*  910:     */       {
/*  911:1299 */         String val = this.m_plotInstances.attribute(this.m_xIndex).value(i);
/*  912:1300 */         int sw = this.m_labelMetrics.stringWidth(val);
/*  913:1303 */         if (sw > maxXStringWidth)
/*  914:     */         {
/*  915:1304 */           int incr = sw / val.length();
/*  916:1305 */           int rm = (sw - maxXStringWidth) / incr;
/*  917:1306 */           if (rm == 0) {
/*  918:1307 */             rm = 1;
/*  919:     */           }
/*  920:1309 */           val = val.substring(0, val.length() - rm);
/*  921:1310 */           sw = this.m_labelMetrics.stringWidth(val);
/*  922:     */         }
/*  923:1312 */         if (i == 0) {
/*  924:1313 */           gx.drawString(val, (int)convertToPanelX(i), this.m_YaxisEnd + hf + 5);
/*  925:1315 */         } else if (i == numValues - 1)
/*  926:     */         {
/*  927:1316 */           if (i % 2 == 0) {
/*  928:1317 */             gx.drawString(val, this.m_XaxisEnd - sw, this.m_YaxisEnd + hf + 5);
/*  929:     */           } else {
/*  930:1319 */             gx.drawString(val, this.m_XaxisEnd - sw, this.m_YaxisEnd + 2 * hf + 5);
/*  931:     */           }
/*  932:     */         }
/*  933:1323 */         else if (i % 2 == 0) {
/*  934:1324 */           gx.drawString(val, (int)convertToPanelX(i) - sw / 2, this.m_YaxisEnd + hf + 5);
/*  935:     */         } else {
/*  936:1327 */           gx.drawString(val, (int)convertToPanelX(i) - sw / 2, this.m_YaxisEnd + 2 * hf + 5);
/*  937:     */         }
/*  938:1331 */         gx.drawLine((int)convertToPanelX(i), this.m_YaxisEnd, (int)convertToPanelX(i), this.m_YaxisEnd + 5);
/*  939:     */       }
/*  940:     */     }
/*  941:1338 */     if (this.m_plotInstances.attribute(this.m_yIndex).isNumeric())
/*  942:     */     {
/*  943:1339 */       if (h > 2 * hf)
/*  944:     */       {
/*  945:1340 */         gx.drawString(maxStringY, this.m_XaxisStart - mswy - 5, this.m_YaxisStart + hf);
/*  946:     */         
/*  947:     */ 
/*  948:1343 */         gx.drawString(minStringY, this.m_XaxisStart - mswy - 5, this.m_YaxisEnd);
/*  949:1347 */         if ((w > 3 * hf) && (this.m_plotInstances.attribute(this.m_yIndex).isNumeric()))
/*  950:     */         {
/*  951:1348 */           double mid = this.m_minY + (this.m_maxY - this.m_minY) / 2.0D;
/*  952:1349 */           whole = (int)Math.abs(mid);
/*  953:1350 */           decimal = Math.abs(mid) - whole;
/*  954:1351 */           nondecimal = whole > 0 ? (int)(Math.log(whole) / Math.log(10.0D)) : 1;
/*  955:1352 */           precisionYmid = decimal > 0.0D ? (int)Math.abs(Math.log(Math.abs(mid)) / Math.log(10.0D)) + 2 : 1;
/*  956:1355 */           if (precisionYmid > VisualizeUtils.MAX_PRECISION) {
/*  957:1356 */             precisionYmid = 1;
/*  958:     */           }
/*  959:1359 */           String maxString = Utils.doubleToString(mid, nondecimal + 1 + precisionYmid, precisionYmid);
/*  960:     */           
/*  961:     */ 
/*  962:1362 */           int sw = this.m_labelMetrics.stringWidth(maxString);
/*  963:1363 */           double mx = this.m_YaxisStart + (this.m_YaxisEnd - this.m_YaxisStart) / 2.0D;
/*  964:1364 */           gx.drawString(maxString, this.m_XaxisStart - sw - 5 - 1, (int)(mx + hf / 2.0D));
/*  965:     */           
/*  966:1366 */           gx.drawLine(this.m_XaxisStart - 5, (int)mx, this.m_XaxisStart, (int)mx);
/*  967:     */         }
/*  968:     */       }
/*  969:     */     }
/*  970:     */     else
/*  971:     */     {
/*  972:1371 */       int numValues = this.m_plotInstances.attribute(this.m_yIndex).numValues();
/*  973:1372 */       int div = numValues % 2 == 0 ? numValues / 2 : numValues / 2 + 1;
/*  974:1373 */       int maxYStringHeight = (this.m_YaxisEnd - this.m_XaxisStart) / div;
/*  975:1374 */       int sw = this.m_labelMetrics.stringWidth("M");
/*  976:1375 */       for (int i = 0; i < numValues; i++)
/*  977:     */       {
/*  978:1377 */         if (maxYStringHeight >= 2 * hf)
/*  979:     */         {
/*  980:1378 */           String val = this.m_plotInstances.attribute(this.m_yIndex).value(i);
/*  981:1379 */           int numPrint = maxYStringHeight / hf > val.length() ? val.length() : maxYStringHeight / hf;
/*  982:1383 */           for (int j = 0; j < numPrint; j++)
/*  983:     */           {
/*  984:1384 */             String ll = val.substring(j, j + 1);
/*  985:1385 */             if ((val.charAt(j) == '_') || (val.charAt(j) == '-')) {
/*  986:1386 */               ll = "|";
/*  987:     */             }
/*  988:1388 */             if (i == 0) {
/*  989:1389 */               gx.drawString(ll, this.m_XaxisStart - sw - 5 - 1, (int)convertToPanelY(i) - (numPrint - 1) * hf + j * hf + hf / 2);
/*  990:1392 */             } else if (i == numValues - 1)
/*  991:     */             {
/*  992:1393 */               if (i % 2 == 0) {
/*  993:1394 */                 gx.drawString(ll, this.m_XaxisStart - sw - 5 - 1, (int)convertToPanelY(i) + j * hf + hf / 2);
/*  994:     */               } else {
/*  995:1397 */                 gx.drawString(ll, this.m_XaxisStart - 2 * sw - 5 - 1, (int)convertToPanelY(i) + j * hf + hf / 2);
/*  996:     */               }
/*  997:     */             }
/*  998:1401 */             else if (i % 2 == 0) {
/*  999:1402 */               gx.drawString(ll, this.m_XaxisStart - sw - 5 - 1, (int)convertToPanelY(i) - (numPrint - 1) * hf / 2 + j * hf + hf / 2);
/* 1000:     */             } else {
/* 1001:1406 */               gx.drawString(ll, this.m_XaxisStart - 2 * sw - 5 - 1, (int)convertToPanelY(i) - (numPrint - 1) * hf / 2 + j * hf + hf / 2);
/* 1002:     */             }
/* 1003:     */           }
/* 1004:     */         }
/* 1005:1413 */         gx.drawLine(this.m_XaxisStart - 5, (int)convertToPanelY(i), this.m_XaxisStart, (int)convertToPanelY(i));
/* 1006:     */       }
/* 1007:     */     }
/* 1008:1418 */     gx.drawLine(this.m_XaxisStart, this.m_YaxisStart, this.m_XaxisStart, this.m_YaxisEnd);
/* 1009:1419 */     gx.drawLine(this.m_XaxisStart, this.m_YaxisEnd, this.m_XaxisEnd, this.m_YaxisEnd);
/* 1010:1421 */     if ((this.m_XaxisStart != mxs) || (this.m_XaxisEnd != mxe) || (this.m_YaxisStart != mys) || (this.m_YaxisEnd != mye)) {
/* 1011:1423 */       this.m_plotResize = true;
/* 1012:     */     }
/* 1013:     */   }
/* 1014:     */   
/* 1015:     */   private void extendColourMap(int highest)
/* 1016:     */   {
/* 1017:1432 */     for (int i = this.m_colorList.size(); i < highest; i++)
/* 1018:     */     {
/* 1019:1433 */       Color pc = this.m_DefaultColors[(i % 10)];
/* 1020:1434 */       int ija = i / 10;
/* 1021:1435 */       ija *= 2;
/* 1022:1436 */       for (int j = 0; j < ija; j++) {
/* 1023:1437 */         pc = pc.brighter();
/* 1024:     */       }
/* 1025:1440 */       this.m_colorList.add(pc);
/* 1026:     */     }
/* 1027:     */   }
/* 1028:     */   
/* 1029:     */   public void paintComponent(Graphics gx)
/* 1030:     */   {
/* 1031:1451 */     super.paintComponent(gx);
/* 1032:1452 */     if ((this.m_plotInstances != null) && (this.m_plotInstances.numInstances() > 0) && (this.m_plotInstances.numAttributes() > 0))
/* 1033:     */     {
/* 1034:1454 */       if (this.m_plotCompanion != null) {
/* 1035:1455 */         this.m_plotCompanion.prePlot(gx);
/* 1036:     */       }
/* 1037:1458 */       this.m_JRand = new Random(this.m_JitterVal);
/* 1038:1459 */       paintAxis(gx);
/* 1039:1460 */       if ((this.m_axisChanged) || (this.m_plotResize))
/* 1040:     */       {
/* 1041:1461 */         int x_range = this.m_XaxisEnd - this.m_XaxisStart;
/* 1042:1462 */         int y_range = this.m_YaxisEnd - this.m_YaxisStart;
/* 1043:1463 */         if (x_range < 10) {
/* 1044:1464 */           x_range = 10;
/* 1045:     */         }
/* 1046:1466 */         if (y_range < 10) {
/* 1047:1467 */           y_range = 10;
/* 1048:     */         }
/* 1049:1470 */         this.m_drawnPoints = new int[x_range + 1][y_range + 1];
/* 1050:1471 */         fillLookup();
/* 1051:1472 */         this.m_plotResize = false;
/* 1052:1473 */         this.m_axisChanged = false;
/* 1053:     */       }
/* 1054:1475 */       paintData(gx);
/* 1055:     */     }
/* 1056:     */   }
/* 1057:     */   
/* 1058:     */   protected static Color checkAgainstBackground(Color c, Color background)
/* 1059:     */   {
/* 1060:1480 */     if (background == null) {
/* 1061:1481 */       return c;
/* 1062:     */     }
/* 1063:1484 */     if (c.equals(background))
/* 1064:     */     {
/* 1065:1485 */       int red = c.getRed();
/* 1066:1486 */       int blue = c.getBlue();
/* 1067:1487 */       int green = c.getGreen();
/* 1068:1488 */       red += (red < 128 ? (255 - red) / 2 : -(red / 2));
/* 1069:1489 */       blue += (blue < 128 ? (blue - red) / 2 : -(blue / 2));
/* 1070:1490 */       green += (green < 128 ? (255 - green) / 2 : -(green / 2));
/* 1071:1491 */       c = new Color(red, green, blue);
/* 1072:     */     }
/* 1073:1493 */     return c;
/* 1074:     */   }
/* 1075:     */   
/* 1076:     */   public static void main(String[] args)
/* 1077:     */   {
/* 1078:     */     try
/* 1079:     */     {
/* 1080:1503 */       if (args.length < 1)
/* 1081:     */       {
/* 1082:1504 */         System.err.println("Usage : weka.gui.visualize.Plot2D <dataset> [<dataset> <dataset>...]");
/* 1083:     */         
/* 1084:1506 */         System.exit(1);
/* 1085:     */       }
/* 1086:1509 */       JFrame jf = new JFrame("Weka Explorer: Visualize");
/* 1087:     */       
/* 1088:1511 */       jf.setSize(500, 400);
/* 1089:1512 */       jf.getContentPane().setLayout(new BorderLayout());
/* 1090:1513 */       Plot2D p2 = new Plot2D();
/* 1091:1514 */       jf.getContentPane().add(p2, "Center");
/* 1092:1515 */       jf.addWindowListener(new WindowAdapter()
/* 1093:     */       {
/* 1094:     */         public void windowClosing(WindowEvent e)
/* 1095:     */         {
/* 1096:1518 */           this.val$jf.dispose();
/* 1097:1519 */           System.exit(0);
/* 1098:     */         }
/* 1099:1522 */       });
/* 1100:1523 */       p2.addMouseListener(new MouseAdapter()
/* 1101:     */       {
/* 1102:     */         public void mouseClicked(MouseEvent e)
/* 1103:     */         {
/* 1104:1526 */           if ((e.getModifiers() & 0x10) == 16) {
/* 1105:1527 */             this.val$p2.searchPoints(e.getX(), e.getY(), false);
/* 1106:     */           } else {
/* 1107:1529 */             this.val$p2.searchPoints(e.getX(), e.getY(), true);
/* 1108:     */           }
/* 1109:     */         }
/* 1110:1533 */       });
/* 1111:1534 */       jf.setVisible(true);
/* 1112:1535 */       if (args.length >= 1) {
/* 1113:1536 */         for (int j = 0; j < args.length; j++)
/* 1114:     */         {
/* 1115:1537 */           System.err.println("Loading instances from " + args[j]);
/* 1116:1538 */           Reader r = new BufferedReader(new FileReader(args[j]));
/* 1117:     */           
/* 1118:1540 */           Instances i = new Instances(r);
/* 1119:1541 */           i.setClassIndex(i.numAttributes() - 1);
/* 1120:1542 */           PlotData2D pd1 = new PlotData2D(i);
/* 1121:1544 */           if (j == 0)
/* 1122:     */           {
/* 1123:1545 */             pd1.setPlotName("Master plot");
/* 1124:1546 */             p2.setMasterPlot(pd1);
/* 1125:1547 */             p2.setXindex(2);
/* 1126:1548 */             p2.setYindex(3);
/* 1127:1549 */             p2.setCindex(i.classIndex());
/* 1128:     */           }
/* 1129:     */           else
/* 1130:     */           {
/* 1131:1551 */             pd1.setPlotName("Plot " + (j + 1));
/* 1132:1552 */             pd1.m_useCustomColour = true;
/* 1133:1553 */             pd1.m_customColour = (j % 2 == 0 ? Color.red : Color.blue);
/* 1134:1554 */             p2.addPlot(pd1);
/* 1135:     */           }
/* 1136:     */         }
/* 1137:     */       }
/* 1138:     */     }
/* 1139:     */     catch (Exception ex)
/* 1140:     */     {
/* 1141:1559 */       ex.printStackTrace();
/* 1142:1560 */       System.err.println(ex.getMessage());
/* 1143:     */     }
/* 1144:     */   }
/* 1145:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.Plot2D
 * JD-Core Version:    0.7.0.1
 */