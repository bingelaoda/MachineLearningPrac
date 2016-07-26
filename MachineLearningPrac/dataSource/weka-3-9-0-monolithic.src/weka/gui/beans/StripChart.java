/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Container;
/*   6:    */ import java.awt.Dimension;
/*   7:    */ import java.awt.Font;
/*   8:    */ import java.awt.FontMetrics;
/*   9:    */ import java.awt.Graphics;
/*  10:    */ import java.awt.Image;
/*  11:    */ import java.awt.event.WindowAdapter;
/*  12:    */ import java.awt.event.WindowEvent;
/*  13:    */ import java.beans.EventSetDescriptor;
/*  14:    */ import java.io.IOException;
/*  15:    */ import java.io.ObjectInputStream;
/*  16:    */ import java.io.PrintStream;
/*  17:    */ import java.util.Enumeration;
/*  18:    */ import java.util.LinkedList;
/*  19:    */ import java.util.Properties;
/*  20:    */ import java.util.Random;
/*  21:    */ import java.util.Vector;
/*  22:    */ import javax.swing.BorderFactory;
/*  23:    */ import javax.swing.JFrame;
/*  24:    */ import javax.swing.JPanel;
/*  25:    */ import weka.core.Attribute;
/*  26:    */ import weka.core.Instance;
/*  27:    */ import weka.core.Instances;
/*  28:    */ import weka.core.Utils;
/*  29:    */ import weka.gui.Logger;
/*  30:    */ import weka.gui.visualize.PrintableComponent;
/*  31:    */ import weka.gui.visualize.VisualizeUtils;
/*  32:    */ 
/*  33:    */ public class StripChart
/*  34:    */   extends JPanel
/*  35:    */   implements ChartListener, InstanceListener, Visible, BeanCommon, UserRequestAcceptor
/*  36:    */ {
/*  37:    */   private static final long serialVersionUID = 1483649041577695019L;
/*  38: 64 */   protected Color[] m_colorList = { Color.green, Color.red, Color.blue, Color.cyan, Color.pink, new Color(255, 0, 255), Color.orange, new Color(255, 0, 0), new Color(0, 255, 0), Color.white };
/*  39:    */   protected Color m_BackgroundColor;
/*  40:    */   protected Color m_LegendPanelBorderColor;
/*  41:    */   
/*  42:    */   private class StripPlotter
/*  43:    */     extends JPanel
/*  44:    */   {
/*  45:    */     private static final long serialVersionUID = -7056271598761675879L;
/*  46:    */     
/*  47:    */     private StripPlotter() {}
/*  48:    */     
/*  49:    */     public void paintComponent(Graphics g)
/*  50:    */     {
/*  51: 84 */       super.paintComponent(g);
/*  52: 85 */       if (StripChart.this.m_osi != null) {
/*  53: 86 */         g.drawImage(StripChart.this.m_osi, 0, 0, this);
/*  54:    */       }
/*  55:    */     }
/*  56:    */   }
/*  57:    */   
/*  58: 91 */   private transient JFrame m_outputFrame = null;
/*  59: 92 */   private transient StripPlotter m_plotPanel = null;
/*  60: 97 */   private transient Image m_osi = null;
/*  61:    */   private int m_iheight;
/*  62:    */   private int m_iwidth;
/*  63:108 */   private double m_max = 1.0D;
/*  64:113 */   private double m_min = 0.0D;
/*  65:118 */   private boolean m_yScaleUpdate = false;
/*  66:    */   private double m_oldMax;
/*  67:    */   private double m_oldMin;
/*  68:122 */   private final Font m_labelFont = new Font("Monospaced", 0, 10);
/*  69:    */   private FontMetrics m_labelMetrics;
/*  70:127 */   private Vector<String> m_legendText = new Vector();
/*  71:    */   
/*  72:    */   private class ScalePanel
/*  73:    */     extends JPanel
/*  74:    */   {
/*  75:    */     private static final long serialVersionUID = 6416998474984829434L;
/*  76:    */     
/*  77:    */     private ScalePanel() {}
/*  78:    */     
/*  79:    */     public void paintComponent(Graphics gx)
/*  80:    */     {
/*  81:139 */       super.paintComponent(gx);
/*  82:140 */       if (StripChart.this.m_labelMetrics == null) {
/*  83:141 */         StripChart.this.m_labelMetrics = gx.getFontMetrics(StripChart.this.m_labelFont);
/*  84:    */       }
/*  85:143 */       gx.setFont(StripChart.this.m_labelFont);
/*  86:144 */       int hf = StripChart.this.m_labelMetrics.getAscent();
/*  87:145 */       String temp = "" + StripChart.this.m_max;
/*  88:146 */       gx.setColor(StripChart.this.m_colorList[(StripChart.this.m_colorList.length - 1)]);
/*  89:147 */       gx.drawString(temp, 1, hf - 2);
/*  90:148 */       temp = "" + (StripChart.this.m_min + (StripChart.this.m_max - StripChart.this.m_min) / 2.0D);
/*  91:149 */       gx.drawString(temp, 1, getHeight() / 2 + hf / 2);
/*  92:150 */       temp = "" + StripChart.this.m_min;
/*  93:151 */       gx.drawString(temp, 1, getHeight() - 1);
/*  94:    */     }
/*  95:    */   }
/*  96:    */   
/*  97:156 */   private final ScalePanel m_scalePanel = new ScalePanel(null);
/*  98:    */   
/*  99:    */   private class LegendPanel
/* 100:    */     extends JPanel
/* 101:    */   {
/* 102:    */     private static final long serialVersionUID = 7713986576833797583L;
/* 103:    */     
/* 104:    */     private LegendPanel() {}
/* 105:    */     
/* 106:    */     public void paintComponent(Graphics gx)
/* 107:    */     {
/* 108:168 */       super.paintComponent(gx);
/* 109:170 */       if (StripChart.this.m_labelMetrics == null) {
/* 110:171 */         StripChart.this.m_labelMetrics = gx.getFontMetrics(StripChart.this.m_labelFont);
/* 111:    */       }
/* 112:173 */       int hf = StripChart.this.m_labelMetrics.getAscent();
/* 113:174 */       int x = 10;
/* 114:175 */       int y = hf + 15;
/* 115:176 */       gx.setFont(StripChart.this.m_labelFont);
/* 116:177 */       for (int i = 0; i < StripChart.this.m_legendText.size(); i++)
/* 117:    */       {
/* 118:178 */         String temp = (String)StripChart.this.m_legendText.elementAt(i);
/* 119:179 */         gx.setColor(StripChart.this.m_colorList[(i % StripChart.this.m_colorList.length)]);
/* 120:180 */         gx.drawString(temp, x, y);
/* 121:181 */         y += hf;
/* 122:    */       }
/* 123:183 */       StripChart.this.revalidate();
/* 124:    */     }
/* 125:    */   }
/* 126:    */   
/* 127:188 */   private final LegendPanel m_legendPanel = new LegendPanel(null);
/* 128:193 */   private LinkedList<double[]> m_dataList = new LinkedList();
/* 129:195 */   private double[] m_previousY = new double[1];
/* 130:    */   private transient Thread m_updateHandler;
/* 131:199 */   protected BeanVisual m_visual = new BeanVisual("StripChart", "weka/gui/beans/icons/StripChart.gif", "weka/gui/beans/icons/StripChart_animated.gif");
/* 132:203 */   private Object m_listenee = null;
/* 133:207 */   private int m_xValFreq = 500;
/* 134:208 */   private int m_xCount = 0;
/* 135:213 */   private int m_refreshWidth = 1;
/* 136:215 */   private int m_userRefreshWidth = 1;
/* 137:220 */   private int m_refreshFrequency = 5;
/* 138:223 */   protected PrintableComponent m_Printer = null;
/* 139:    */   
/* 140:    */   public StripChart()
/* 141:    */   {
/* 142:229 */     setLayout(new BorderLayout());
/* 143:230 */     add(this.m_visual, "Center");
/* 144:    */     
/* 145:    */ 
/* 146:233 */     initPlot();
/* 147:    */   }
/* 148:    */   
/* 149:    */   public void setCustomName(String name)
/* 150:    */   {
/* 151:243 */     this.m_visual.setText(name);
/* 152:    */   }
/* 153:    */   
/* 154:    */   public String getCustomName()
/* 155:    */   {
/* 156:253 */     return this.m_visual.getText();
/* 157:    */   }
/* 158:    */   
/* 159:    */   public String globalInfo()
/* 160:    */   {
/* 161:262 */     return "Visualize incremental classifier performance as a scrolling plot.";
/* 162:    */   }
/* 163:    */   
/* 164:    */   public String xLabelFreqTipText()
/* 165:    */   {
/* 166:271 */     return "Show x axis labels this often";
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void setXLabelFreq(int freq)
/* 170:    */   {
/* 171:280 */     this.m_xValFreq = freq;
/* 172:281 */     if (getGraphics() != null) {
/* 173:282 */       setRefreshGap();
/* 174:    */     }
/* 175:    */   }
/* 176:    */   
/* 177:    */   public int getXLabelFreq()
/* 178:    */   {
/* 179:292 */     return this.m_xValFreq;
/* 180:    */   }
/* 181:    */   
/* 182:    */   public String refreshFreqTipText()
/* 183:    */   {
/* 184:301 */     return "Plot every x'th data point";
/* 185:    */   }
/* 186:    */   
/* 187:    */   public void setRefreshFreq(int freq)
/* 188:    */   {
/* 189:310 */     this.m_refreshFrequency = freq;
/* 190:311 */     if (getGraphics() != null) {
/* 191:312 */       setRefreshGap();
/* 192:    */     }
/* 193:    */   }
/* 194:    */   
/* 195:    */   public int getRefreshFreq()
/* 196:    */   {
/* 197:322 */     return this.m_refreshFrequency;
/* 198:    */   }
/* 199:    */   
/* 200:    */   public String refreshWidthTipText()
/* 201:    */   {
/* 202:331 */     return "The number of pixels to shift the plot by every time a point is plotted.";
/* 203:    */   }
/* 204:    */   
/* 205:    */   public void setRefreshWidth(int width)
/* 206:    */   {
/* 207:341 */     if (width > 0) {
/* 208:342 */       this.m_userRefreshWidth = width;
/* 209:    */     }
/* 210:    */   }
/* 211:    */   
/* 212:    */   public int getRefreshWidth()
/* 213:    */   {
/* 214:352 */     return this.m_userRefreshWidth;
/* 215:    */   }
/* 216:    */   
/* 217:    */   private void setRefreshGap()
/* 218:    */   {
/* 219:356 */     this.m_refreshWidth = this.m_userRefreshWidth;
/* 220:357 */     if (this.m_labelMetrics == null)
/* 221:    */     {
/* 222:358 */       getGraphics().setFont(this.m_labelFont);
/* 223:359 */       this.m_labelMetrics = getGraphics().getFontMetrics(this.m_labelFont);
/* 224:    */     }
/* 225:362 */     int refWidth = this.m_labelMetrics.stringWidth("99000");
/* 226:    */     
/* 227:364 */     int z = getXLabelFreq() / getRefreshFreq();
/* 228:365 */     if (z < 1) {
/* 229:366 */       z = 1;
/* 230:    */     }
/* 231:369 */     if (z * this.m_refreshWidth < refWidth + 5) {
/* 232:370 */       this.m_refreshWidth *= ((refWidth + 5) / z + 1);
/* 233:    */     }
/* 234:    */   }
/* 235:    */   
/* 236:    */   private void readObject(ObjectInputStream ois)
/* 237:    */     throws IOException, ClassNotFoundException
/* 238:    */   {
/* 239:    */     try
/* 240:    */     {
/* 241:384 */       ois.defaultReadObject();
/* 242:385 */       initPlot();
/* 243:    */     }
/* 244:    */     catch (Exception ex)
/* 245:    */     {
/* 246:388 */       ex.printStackTrace();
/* 247:    */     }
/* 248:    */   }
/* 249:    */   
/* 250:    */   private void setProperties()
/* 251:    */   {
/* 252:402 */     String key = getClass().getName() + ".backgroundColour";
/* 253:403 */     String color = BeansProperties.BEAN_PROPERTIES.getProperty(key);
/* 254:404 */     this.m_BackgroundColor = Color.BLACK;
/* 255:405 */     if (color != null) {
/* 256:406 */       this.m_BackgroundColor = VisualizeUtils.processColour(color, this.m_BackgroundColor);
/* 257:    */     }
/* 258:411 */     key = this.m_legendPanel.getClass().getName() + ".borderColour";
/* 259:412 */     color = BeansProperties.BEAN_PROPERTIES.getProperty(key);
/* 260:413 */     this.m_LegendPanelBorderColor = Color.BLUE;
/* 261:414 */     if (color != null) {
/* 262:415 */       this.m_LegendPanelBorderColor = VisualizeUtils.processColour(color, this.m_LegendPanelBorderColor);
/* 263:    */     }
/* 264:    */   }
/* 265:    */   
/* 266:    */   private void initPlot()
/* 267:    */   {
/* 268:421 */     setProperties();
/* 269:422 */     this.m_plotPanel = new StripPlotter(null);
/* 270:423 */     this.m_plotPanel.setBackground(this.m_BackgroundColor);
/* 271:424 */     this.m_scalePanel.setBackground(this.m_BackgroundColor);
/* 272:425 */     this.m_legendPanel.setBackground(this.m_BackgroundColor);
/* 273:426 */     this.m_xCount = 0;
/* 274:    */   }
/* 275:    */   
/* 276:    */   private void startHandler()
/* 277:    */   {
/* 278:430 */     if (this.m_updateHandler == null)
/* 279:    */     {
/* 280:431 */       this.m_updateHandler = new Thread()
/* 281:    */       {
/* 282:    */         private double[] dataPoint;
/* 283:    */         
/* 284:    */         public void run()
/* 285:    */         {
/* 286:    */           for (;;)
/* 287:    */           {
/* 288:437 */             if (StripChart.this.m_outputFrame != null)
/* 289:    */             {
/* 290:438 */               synchronized (StripChart.this.m_dataList)
/* 291:    */               {
/* 292:439 */                 while (StripChart.this.m_dataList.isEmpty()) {
/* 293:    */                   try
/* 294:    */                   {
/* 295:442 */                     StripChart.this.m_dataList.wait();
/* 296:    */                   }
/* 297:    */                   catch (InterruptedException ex)
/* 298:    */                   {
/* 299:444 */                     return;
/* 300:    */                   }
/* 301:    */                 }
/* 302:447 */                 this.dataPoint = ((double[])StripChart.this.m_dataList.remove(0));
/* 303:    */               }
/* 304:450 */               if (StripChart.this.m_outputFrame != null) {
/* 305:451 */                 StripChart.this.updateChart(this.dataPoint);
/* 306:    */               }
/* 307:    */             }
/* 308:    */           }
/* 309:    */         }
/* 310:457 */       };
/* 311:458 */       this.m_updateHandler.start();
/* 312:    */     }
/* 313:    */   }
/* 314:    */   
/* 315:    */   public void showChart()
/* 316:    */   {
/* 317:466 */     if (this.m_outputFrame == null)
/* 318:    */     {
/* 319:467 */       this.m_outputFrame = new JFrame("Strip Chart");
/* 320:468 */       this.m_outputFrame.getContentPane().setLayout(new BorderLayout());
/* 321:469 */       JPanel panel = new JPanel(new BorderLayout());
/* 322:470 */       new PrintableComponent(panel);
/* 323:471 */       this.m_outputFrame.getContentPane().add(panel, "Center");
/* 324:472 */       panel.add(this.m_legendPanel, "West");
/* 325:473 */       panel.add(this.m_plotPanel, "Center");
/* 326:474 */       panel.add(this.m_scalePanel, "East");
/* 327:475 */       this.m_legendPanel.setMinimumSize(new Dimension(100, getHeight()));
/* 328:476 */       this.m_legendPanel.setPreferredSize(new Dimension(100, getHeight()));
/* 329:477 */       this.m_scalePanel.setMinimumSize(new Dimension(30, getHeight()));
/* 330:478 */       this.m_scalePanel.setPreferredSize(new Dimension(30, getHeight()));
/* 331:479 */       Font lf = new Font("Monospaced", 0, 12);
/* 332:480 */       this.m_legendPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.gray, Color.darkGray), "Legend", 2, 0, lf, this.m_LegendPanelBorderColor));
/* 333:    */       
/* 334:    */ 
/* 335:    */ 
/* 336:484 */       this.m_outputFrame.addWindowListener(new WindowAdapter()
/* 337:    */       {
/* 338:    */         public void windowClosing(WindowEvent e)
/* 339:    */         {
/* 340:487 */           if (StripChart.this.m_updateHandler != null)
/* 341:    */           {
/* 342:488 */             System.err.println("Interrupting");
/* 343:489 */             StripChart.this.m_updateHandler.interrupt();
/* 344:490 */             StripChart.this.m_updateHandler = null;
/* 345:    */           }
/* 346:492 */           synchronized (StripChart.this.m_dataList)
/* 347:    */           {
/* 348:493 */             StripChart.this.m_dataList = new LinkedList();
/* 349:    */           }
/* 350:495 */           StripChart.this.m_outputFrame.dispose();
/* 351:496 */           StripChart.this.m_outputFrame = null;
/* 352:    */         }
/* 353:498 */       });
/* 354:499 */       this.m_outputFrame.pack();
/* 355:500 */       this.m_outputFrame.setSize(600, 150);
/* 356:501 */       this.m_outputFrame.setResizable(false);
/* 357:502 */       this.m_outputFrame.setVisible(true);
/* 358:503 */       this.m_outputFrame.setAlwaysOnTop(true);
/* 359:    */       
/* 360:505 */       int iwidth = this.m_plotPanel.getWidth();
/* 361:506 */       int iheight = this.m_plotPanel.getHeight();
/* 362:507 */       this.m_osi = this.m_plotPanel.createImage(iwidth, iheight);
/* 363:508 */       Graphics m = this.m_osi.getGraphics();
/* 364:509 */       m.setColor(this.m_BackgroundColor);
/* 365:510 */       m.fillRect(0, 0, iwidth, iheight);
/* 366:511 */       this.m_previousY[0] = -1.0D;
/* 367:512 */       setRefreshGap();
/* 368:513 */       if (this.m_updateHandler == null)
/* 369:    */       {
/* 370:514 */         System.err.println("Starting handler");
/* 371:515 */         startHandler();
/* 372:    */       }
/* 373:    */     }
/* 374:    */     else
/* 375:    */     {
/* 376:518 */       this.m_outputFrame.toFront();
/* 377:    */     }
/* 378:    */   }
/* 379:    */   
/* 380:    */   private int convertToPanelY(double yval)
/* 381:    */   {
/* 382:523 */     int height = this.m_plotPanel.getHeight();
/* 383:524 */     double temp = (yval - this.m_min) / (this.m_max - this.m_min);
/* 384:525 */     temp *= height;
/* 385:526 */     temp = height - temp;
/* 386:527 */     return (int)temp;
/* 387:    */   }
/* 388:    */   
/* 389:    */   protected void updateChart(double[] dataPoint)
/* 390:    */   {
/* 391:536 */     if (this.m_previousY[0] == -1.0D)
/* 392:    */     {
/* 393:537 */       int iw = this.m_plotPanel.getWidth();
/* 394:538 */       int ih = this.m_plotPanel.getHeight();
/* 395:539 */       this.m_osi = this.m_plotPanel.createImage(iw, ih);
/* 396:540 */       Graphics m = this.m_osi.getGraphics();
/* 397:541 */       m.setColor(this.m_BackgroundColor);
/* 398:542 */       m.fillRect(0, 0, iw, ih);
/* 399:543 */       this.m_previousY[0] = convertToPanelY(0.0D);
/* 400:544 */       this.m_iheight = ih;
/* 401:545 */       this.m_iwidth = iw;
/* 402:    */     }
/* 403:548 */     if (dataPoint.length - 1 != this.m_previousY.length)
/* 404:    */     {
/* 405:549 */       this.m_previousY = new double[dataPoint.length - 1];
/* 406:551 */       for (int i = 0; i < dataPoint.length - 1; i++) {
/* 407:552 */         this.m_previousY[i] = convertToPanelY(0.0D);
/* 408:    */       }
/* 409:    */     }
/* 410:556 */     Graphics osg = this.m_osi.getGraphics();
/* 411:557 */     Graphics g = this.m_plotPanel.getGraphics();
/* 412:    */     
/* 413:559 */     osg.copyArea(this.m_refreshWidth, 0, this.m_iwidth - this.m_refreshWidth, this.m_iheight, -this.m_refreshWidth, 0);
/* 414:    */     
/* 415:561 */     osg.setColor(this.m_BackgroundColor);
/* 416:562 */     osg.fillRect(this.m_iwidth - this.m_refreshWidth, 0, this.m_iwidth, this.m_iheight);
/* 417:565 */     if (this.m_yScaleUpdate)
/* 418:    */     {
/* 419:566 */       String maxVal = numToString(this.m_oldMax);
/* 420:567 */       String minVal = numToString(this.m_oldMin);
/* 421:568 */       String midVal = numToString((this.m_oldMax - this.m_oldMin) / 2.0D);
/* 422:569 */       if (this.m_labelMetrics == null) {
/* 423:570 */         this.m_labelMetrics = g.getFontMetrics(this.m_labelFont);
/* 424:    */       }
/* 425:572 */       osg.setFont(this.m_labelFont);
/* 426:573 */       int wmx = this.m_labelMetrics.stringWidth(maxVal);
/* 427:574 */       int wmn = this.m_labelMetrics.stringWidth(minVal);
/* 428:575 */       int wmd = this.m_labelMetrics.stringWidth(midVal);
/* 429:    */       
/* 430:577 */       int hf = this.m_labelMetrics.getAscent();
/* 431:578 */       osg.setColor(this.m_colorList[(this.m_colorList.length - 1)]);
/* 432:579 */       osg.drawString(maxVal, this.m_iwidth - wmx, hf - 2);
/* 433:580 */       osg.drawString(midVal, this.m_iwidth - wmd, this.m_iheight / 2 + hf / 2);
/* 434:581 */       osg.drawString(minVal, this.m_iwidth - wmn, this.m_iheight - 1);
/* 435:582 */       this.m_yScaleUpdate = false;
/* 436:    */     }
/* 437:586 */     for (int i = 0; i < dataPoint.length - 1; i++) {
/* 438:587 */       if (!Utils.isMissingValue(dataPoint[i]))
/* 439:    */       {
/* 440:590 */         osg.setColor(this.m_colorList[(i % this.m_colorList.length)]);
/* 441:591 */         double pos = convertToPanelY(dataPoint[i]);
/* 442:592 */         osg.drawLine(this.m_iwidth - this.m_refreshWidth, (int)this.m_previousY[i], this.m_iwidth - 1, (int)pos);
/* 443:    */         
/* 444:594 */         this.m_previousY[i] = pos;
/* 445:595 */         if (dataPoint[(dataPoint.length - 1)] % this.m_xValFreq == 0.0D)
/* 446:    */         {
/* 447:597 */           String val = numToString(dataPoint[i]);
/* 448:598 */           if (this.m_labelMetrics == null) {
/* 449:599 */             this.m_labelMetrics = g.getFontMetrics(this.m_labelFont);
/* 450:    */           }
/* 451:601 */           int hf = this.m_labelMetrics.getAscent();
/* 452:602 */           if (pos - hf < 0.0D) {
/* 453:603 */             pos += hf;
/* 454:    */           }
/* 455:605 */           int w = this.m_labelMetrics.stringWidth(val);
/* 456:606 */           osg.setFont(this.m_labelFont);
/* 457:607 */           osg.drawString(val, this.m_iwidth - w, (int)pos);
/* 458:    */         }
/* 459:    */       }
/* 460:    */     }
/* 461:612 */     if (dataPoint[(dataPoint.length - 1)] % this.m_xValFreq == 0.0D)
/* 462:    */     {
/* 463:614 */       String xVal = "" + (int)dataPoint[(dataPoint.length - 1)];
/* 464:615 */       osg.setColor(this.m_colorList[(this.m_colorList.length - 1)]);
/* 465:616 */       int w = this.m_labelMetrics.stringWidth(xVal);
/* 466:617 */       osg.setFont(this.m_labelFont);
/* 467:618 */       osg.drawString(xVal, this.m_iwidth - w, this.m_iheight - 1);
/* 468:    */     }
/* 469:620 */     g.drawImage(this.m_osi, 0, 0, this.m_plotPanel);
/* 470:    */   }
/* 471:    */   
/* 472:    */   private static String numToString(double num)
/* 473:    */   {
/* 474:626 */     int precision = 1;
/* 475:627 */     int whole = (int)Math.abs(num);
/* 476:628 */     double decimal = Math.abs(num) - whole;
/* 477:    */     
/* 478:630 */     int nondecimal = whole > 0 ? (int)(Math.log(whole) / Math.log(10.0D)) : 1;
/* 479:    */     
/* 480:632 */     precision = decimal > 0.0D ? (int)Math.abs(Math.log(Math.abs(num)) / Math.log(10.0D)) + 2 : 1;
/* 481:634 */     if (precision > 5) {
/* 482:635 */       precision = 1;
/* 483:    */     }
/* 484:638 */     String numString = Utils.doubleToString(num, nondecimal + 1 + precision, precision);
/* 485:    */     
/* 486:    */ 
/* 487:641 */     return numString;
/* 488:    */   }
/* 489:    */   
/* 490:644 */   ChartEvent m_ce = new ChartEvent(this);
/* 491:645 */   double[] m_dataPoint = null;
/* 492:    */   
/* 493:    */   public void acceptInstance(InstanceEvent e)
/* 494:    */   {
/* 495:649 */     if (e.getStatus() == 0)
/* 496:    */     {
/* 497:650 */       Instances structure = e.getStructure();
/* 498:651 */       this.m_legendText = new Vector();
/* 499:652 */       this.m_max = 1.0D;
/* 500:653 */       this.m_min = 0.0D;
/* 501:654 */       int i = 0;
/* 502:655 */       for (i = 0; i < structure.numAttributes(); i++)
/* 503:    */       {
/* 504:656 */         if (i > 10)
/* 505:    */         {
/* 506:657 */           i--;
/* 507:658 */           break;
/* 508:    */         }
/* 509:660 */         this.m_legendText.addElement(structure.attribute(i).name());
/* 510:661 */         this.m_legendPanel.repaint();
/* 511:662 */         this.m_scalePanel.repaint();
/* 512:    */       }
/* 513:664 */       this.m_dataPoint = new double[i];
/* 514:665 */       this.m_xCount = 0;
/* 515:666 */       return;
/* 516:    */     }
/* 517:670 */     Instance inst = e.getInstance();
/* 518:671 */     for (int i = 0; i < this.m_dataPoint.length; i++) {
/* 519:672 */       if (!inst.isMissing(i)) {
/* 520:673 */         this.m_dataPoint[i] = inst.value(i);
/* 521:    */       }
/* 522:    */     }
/* 523:676 */     acceptDataPoint(this.m_dataPoint);
/* 524:677 */     this.m_xCount += 1;
/* 525:    */   }
/* 526:    */   
/* 527:    */   public void acceptDataPoint(ChartEvent e)
/* 528:    */   {
/* 529:687 */     if (e.getReset())
/* 530:    */     {
/* 531:688 */       this.m_xCount = 0;
/* 532:689 */       this.m_max = 1.0D;
/* 533:690 */       this.m_min = 0.0D;
/* 534:    */     }
/* 535:692 */     if (this.m_outputFrame != null)
/* 536:    */     {
/* 537:693 */       boolean refresh = false;
/* 538:694 */       if (((e.getLegendText() != null ? 1 : 0) & (e.getLegendText() != this.m_legendText ? 1 : 0)) != 0)
/* 539:    */       {
/* 540:695 */         this.m_legendText = e.getLegendText();
/* 541:696 */         refresh = true;
/* 542:    */       }
/* 543:699 */       if ((e.getMin() != this.m_min) || (e.getMax() != this.m_max))
/* 544:    */       {
/* 545:700 */         this.m_oldMax = this.m_max;
/* 546:701 */         this.m_oldMin = this.m_min;
/* 547:702 */         this.m_max = e.getMax();
/* 548:703 */         this.m_min = e.getMin();
/* 549:704 */         refresh = true;
/* 550:705 */         this.m_yScaleUpdate = true;
/* 551:    */       }
/* 552:708 */       if (refresh)
/* 553:    */       {
/* 554:709 */         this.m_legendPanel.repaint();
/* 555:710 */         this.m_scalePanel.repaint();
/* 556:    */       }
/* 557:713 */       acceptDataPoint(e.getDataPoint());
/* 558:    */     }
/* 559:715 */     this.m_xCount += 1;
/* 560:    */   }
/* 561:    */   
/* 562:    */   public void acceptDataPoint(double[] dataPoint)
/* 563:    */   {
/* 564:725 */     if ((this.m_outputFrame != null) && (this.m_xCount % this.m_refreshFrequency == 0))
/* 565:    */     {
/* 566:726 */       double[] dp = new double[dataPoint.length + 1];
/* 567:727 */       dp[(dp.length - 1)] = this.m_xCount;
/* 568:728 */       System.arraycopy(dataPoint, 0, dp, 0, dataPoint.length);
/* 569:730 */       for (double element : dataPoint)
/* 570:    */       {
/* 571:731 */         if (element < this.m_min)
/* 572:    */         {
/* 573:732 */           this.m_oldMin = this.m_min;
/* 574:733 */           this.m_min = element;
/* 575:734 */           this.m_yScaleUpdate = true;
/* 576:    */         }
/* 577:737 */         if (element > this.m_max)
/* 578:    */         {
/* 579:738 */           this.m_oldMax = this.m_max;
/* 580:739 */           this.m_max = element;
/* 581:740 */           this.m_yScaleUpdate = true;
/* 582:    */         }
/* 583:    */       }
/* 584:743 */       if (this.m_yScaleUpdate)
/* 585:    */       {
/* 586:744 */         this.m_scalePanel.repaint();
/* 587:745 */         this.m_yScaleUpdate = false;
/* 588:    */       }
/* 589:747 */       synchronized (this.m_dataList)
/* 590:    */       {
/* 591:748 */         this.m_dataList.add(this.m_dataList.size(), dp);
/* 592:    */         
/* 593:750 */         this.m_dataList.notifyAll();
/* 594:    */       }
/* 595:    */     }
/* 596:    */   }
/* 597:    */   
/* 598:    */   public void setVisual(BeanVisual newVisual)
/* 599:    */   {
/* 600:768 */     this.m_visual = newVisual;
/* 601:    */   }
/* 602:    */   
/* 603:    */   public BeanVisual getVisual()
/* 604:    */   {
/* 605:776 */     return this.m_visual;
/* 606:    */   }
/* 607:    */   
/* 608:    */   public void useDefaultVisual()
/* 609:    */   {
/* 610:784 */     this.m_visual.loadIcons("weka/gui/beans/icons/StripChart.gif", "weka/gui/beans/icons/StripChart_animated.gif");
/* 611:    */   }
/* 612:    */   
/* 613:    */   public void stop()
/* 614:    */   {
/* 615:794 */     if ((this.m_listenee instanceof BeanCommon)) {
/* 616:795 */       ((BeanCommon)this.m_listenee).stop();
/* 617:    */     }
/* 618:    */   }
/* 619:    */   
/* 620:    */   public boolean isBusy()
/* 621:    */   {
/* 622:808 */     return false;
/* 623:    */   }
/* 624:    */   
/* 625:    */   public void setLog(Logger logger) {}
/* 626:    */   
/* 627:    */   public boolean connectionAllowed(String eventName)
/* 628:    */   {
/* 629:829 */     if (this.m_listenee == null) {
/* 630:830 */       return true;
/* 631:    */     }
/* 632:832 */     return false;
/* 633:    */   }
/* 634:    */   
/* 635:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/* 636:    */   {
/* 637:844 */     return connectionAllowed(esd.getName());
/* 638:    */   }
/* 639:    */   
/* 640:    */   public void connectionNotification(String eventName, Object source)
/* 641:    */   {
/* 642:858 */     if (connectionAllowed(eventName)) {
/* 643:859 */       this.m_listenee = source;
/* 644:    */     }
/* 645:    */   }
/* 646:    */   
/* 647:    */   public void disconnectionNotification(String eventName, Object source)
/* 648:    */   {
/* 649:873 */     this.m_listenee = null;
/* 650:    */   }
/* 651:    */   
/* 652:    */   public Enumeration<String> enumerateRequests()
/* 653:    */   {
/* 654:883 */     Vector<String> newVector = new Vector(0);
/* 655:884 */     newVector.addElement("Show chart");
/* 656:885 */     return newVector.elements();
/* 657:    */   }
/* 658:    */   
/* 659:    */   public void performRequest(String request)
/* 660:    */   {
/* 661:896 */     if (request.compareTo("Show chart") == 0) {
/* 662:897 */       showChart();
/* 663:    */     } else {
/* 664:899 */       throw new IllegalArgumentException(request + " not supported (StripChart)");
/* 665:    */     }
/* 666:    */   }
/* 667:    */   
/* 668:    */   public static void main(String[] args)
/* 669:    */   {
/* 670:    */     try
/* 671:    */     {
/* 672:912 */       JFrame jf = new JFrame("Weka Knowledge Flow : StipChart");
/* 673:    */       
/* 674:914 */       jf.getContentPane().setLayout(new BorderLayout());
/* 675:915 */       StripChart jd = new StripChart();
/* 676:916 */       jf.getContentPane().add(jd, "Center");
/* 677:917 */       jf.addWindowListener(new WindowAdapter()
/* 678:    */       {
/* 679:    */         public void windowClosing(WindowEvent e)
/* 680:    */         {
/* 681:920 */           this.val$jf.dispose();
/* 682:921 */           System.exit(0);
/* 683:    */         }
/* 684:923 */       });
/* 685:924 */       jf.pack();
/* 686:925 */       jf.setVisible(true);
/* 687:926 */       jd.showChart();
/* 688:927 */       Random r = new Random(1L);
/* 689:928 */       for (int i = 0; i < 1020; i++)
/* 690:    */       {
/* 691:929 */         double[] pos = new double[1];
/* 692:930 */         pos[0] = r.nextDouble();
/* 693:931 */         jd.acceptDataPoint(pos);
/* 694:    */       }
/* 695:933 */       System.err.println("Done sending data");
/* 696:    */     }
/* 697:    */     catch (Exception ex)
/* 698:    */     {
/* 699:935 */       ex.printStackTrace();
/* 700:936 */       System.err.println(ex.getMessage());
/* 701:    */     }
/* 702:    */   }
/* 703:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.StripChart
 * JD-Core Version:    0.7.0.1
 */