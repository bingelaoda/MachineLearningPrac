/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.Font;
/*   7:    */ import java.awt.FontMetrics;
/*   8:    */ import java.awt.Graphics;
/*   9:    */ import java.awt.Image;
/*  10:    */ import java.awt.Window;
/*  11:    */ import java.awt.event.WindowAdapter;
/*  12:    */ import java.awt.event.WindowEvent;
/*  13:    */ import java.util.ArrayList;
/*  14:    */ import java.util.List;
/*  15:    */ import java.util.Map;
/*  16:    */ import javax.swing.BorderFactory;
/*  17:    */ import javax.swing.JFrame;
/*  18:    */ import javax.swing.JPanel;
/*  19:    */ import weka.core.Defaults;
/*  20:    */ import weka.core.Environment;
/*  21:    */ import weka.core.Settings;
/*  22:    */ import weka.core.Settings.SettingKey;
/*  23:    */ import weka.core.Utils;
/*  24:    */ import weka.gui.knowledgeflow.BaseInteractiveViewer;
/*  25:    */ import weka.gui.visualize.PrintableComponent;
/*  26:    */ import weka.knowledgeflow.steps.StripChart;
/*  27:    */ import weka.knowledgeflow.steps.StripChart.PlotNotificationListener;
/*  28:    */ 
/*  29:    */ public class StripChartInteractiveView
/*  30:    */   extends BaseInteractiveViewer
/*  31:    */   implements StripChart.PlotNotificationListener
/*  32:    */ {
/*  33:    */   private static final long serialVersionUID = 7697752421621805402L;
/*  34:    */   protected Color[] m_colorList;
/*  35:    */   protected Color m_BackgroundColor;
/*  36:    */   protected Color m_LegendPanelBorderColor;
/*  37:    */   protected StripPlotter m_plotPanel;
/*  38:    */   protected final ScalePanel m_scalePanel;
/*  39:    */   protected transient Image m_osi;
/*  40:    */   protected int m_iheight;
/*  41:    */   protected int m_iwidth;
/*  42:    */   protected double m_max;
/*  43:    */   protected double m_min;
/*  44:    */   protected boolean m_yScaleUpdate;
/*  45:    */   protected double m_oldMax;
/*  46:    */   protected double m_oldMin;
/*  47:    */   protected int m_xCount;
/*  48:    */   private int m_refreshWidth;
/*  49:    */   protected final Font m_labelFont;
/*  50:    */   protected FontMetrics m_labelMetrics;
/*  51:    */   protected final LegendPanel m_legendPanel;
/*  52:    */   protected List<String> m_legendText;
/*  53:    */   private double[] m_previousY;
/*  54:    */   
/*  55:    */   public StripChartInteractiveView()
/*  56:    */   {
/*  57: 52 */     this.m_colorList = new Color[] { Color.green, Color.red, new Color(6, 80, 255), Color.cyan, Color.pink, new Color(255, 0, 255), Color.orange, new Color(255, 0, 0), new Color(0, 255, 0), Color.white };
/*  58:    */     
/*  59:    */ 
/*  60:    */ 
/*  61:    */ 
/*  62: 57 */     this.m_BackgroundColor = Color.BLACK;
/*  63:    */     
/*  64:    */ 
/*  65: 60 */     this.m_LegendPanelBorderColor = new Color(253, 255, 61);
/*  66:    */     
/*  67:    */ 
/*  68:    */ 
/*  69:    */ 
/*  70: 65 */     this.m_scalePanel = new ScalePanel(null);
/*  71:    */     
/*  72:    */ 
/*  73:    */ 
/*  74:    */ 
/*  75: 70 */     this.m_osi = null;
/*  76:    */     
/*  77:    */ 
/*  78:    */ 
/*  79:    */ 
/*  80:    */ 
/*  81:    */ 
/*  82:    */ 
/*  83:    */ 
/*  84:    */ 
/*  85:    */ 
/*  86: 81 */     this.m_max = 1.0D;
/*  87:    */     
/*  88:    */ 
/*  89:    */ 
/*  90:    */ 
/*  91: 86 */     this.m_min = 0.0D;
/*  92:    */     
/*  93:    */ 
/*  94:    */ 
/*  95:    */ 
/*  96: 91 */     this.m_yScaleUpdate = false;
/*  97:    */     
/*  98:    */ 
/*  99:    */ 
/* 100:    */ 
/* 101: 96 */     this.m_xCount = 0;
/* 102:    */     
/* 103:    */ 
/* 104:    */ 
/* 105:    */ 
/* 106:101 */     this.m_refreshWidth = 1;
/* 107:    */     
/* 108:    */ 
/* 109:104 */     this.m_labelFont = new Font("Monospaced", 0, 10);
/* 110:    */     
/* 111:    */ 
/* 112:    */ 
/* 113:    */ 
/* 114:    */ 
/* 115:110 */     this.m_legendPanel = new LegendPanel();
/* 116:    */     
/* 117:    */ 
/* 118:113 */     this.m_legendText = new ArrayList();
/* 119:    */     
/* 120:    */ 
/* 121:116 */     this.m_previousY = new double[1];
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void init()
/* 125:    */   {
/* 126:123 */     this.m_plotPanel = new StripPlotter(null);
/* 127:124 */     this.m_plotPanel.setBackground(this.m_BackgroundColor);
/* 128:125 */     this.m_scalePanel.setBackground(this.m_BackgroundColor);
/* 129:126 */     this.m_legendPanel.setBackground(this.m_BackgroundColor);
/* 130:127 */     this.m_xCount = 0;
/* 131:    */     
/* 132:129 */     JPanel panel = new JPanel(new BorderLayout());
/* 133:130 */     new PrintableComponent(panel);
/* 134:131 */     add(panel, "Center");
/* 135:132 */     panel.add(this.m_legendPanel, "West");
/* 136:133 */     panel.add(this.m_plotPanel, "Center");
/* 137:134 */     panel.add(this.m_scalePanel, "East");
/* 138:135 */     this.m_legendPanel.setMinimumSize(new Dimension(100, getHeight()));
/* 139:136 */     this.m_legendPanel.setPreferredSize(new Dimension(100, getHeight()));
/* 140:137 */     this.m_scalePanel.setMinimumSize(new Dimension(30, getHeight()));
/* 141:138 */     this.m_scalePanel.setPreferredSize(new Dimension(30, getHeight()));
/* 142:    */     
/* 143:    */ 
/* 144:    */ 
/* 145:142 */     this.m_parent.addWindowListener(new WindowAdapter()
/* 146:    */     {
/* 147:    */       public void windowClosing(WindowEvent e)
/* 148:    */       {
/* 149:145 */         super.windowClosing(e);
/* 150:146 */         ((StripChart)StripChartInteractiveView.this.getStep()).removePlotNotificationListener(StripChartInteractiveView.this);
/* 151:    */       }
/* 152:149 */     });
/* 153:150 */     ((StripChart)getStep()).addPlotNotificationListener(this);
/* 154:151 */     applySettings(getSettings());
/* 155:    */   }
/* 156:    */   
/* 157:    */   public void closePressed()
/* 158:    */   {
/* 159:159 */     ((StripChart)getStep()).removePlotNotificationListener(this);
/* 160:    */   }
/* 161:    */   
/* 162:    */   public void nowVisible()
/* 163:    */   {
/* 164:169 */     this.m_parent.setSize(600, 180);
/* 165:170 */     ((JFrame)this.m_parent).setResizable(false);
/* 166:171 */     this.m_parent.setAlwaysOnTop(true);
/* 167:172 */     this.m_parent.validate();
/* 168:    */     
/* 169:174 */     int iwidth = this.m_plotPanel.getWidth();
/* 170:175 */     int iheight = this.m_plotPanel.getHeight();
/* 171:176 */     this.m_osi = this.m_plotPanel.createImage(iwidth, iheight);
/* 172:177 */     Graphics m = this.m_osi.getGraphics();
/* 173:178 */     m.setColor(this.m_BackgroundColor);
/* 174:179 */     m.fillRect(0, 0, iwidth, iheight);
/* 175:180 */     this.m_previousY[0] = -1.0D;
/* 176:181 */     setRefreshWidth();
/* 177:    */   }
/* 178:    */   
/* 179:    */   private int convertToPanelY(double yval)
/* 180:    */   {
/* 181:185 */     int height = this.m_plotPanel.getHeight();
/* 182:186 */     double temp = (yval - this.m_min) / (this.m_max - this.m_min);
/* 183:187 */     temp *= height;
/* 184:188 */     temp = height - temp;
/* 185:189 */     return (int)temp;
/* 186:    */   }
/* 187:    */   
/* 188:    */   public String getViewerName()
/* 189:    */   {
/* 190:199 */     return "Strip Chart";
/* 191:    */   }
/* 192:    */   
/* 193:    */   public void setLegend(List<String> legendEntries, double min, double max)
/* 194:    */   {
/* 195:211 */     this.m_legendText = legendEntries;
/* 196:212 */     this.m_max = max;
/* 197:213 */     this.m_min = min;
/* 198:214 */     this.m_xCount = 0;
/* 199:215 */     this.m_legendPanel.repaint();
/* 200:    */   }
/* 201:    */   
/* 202:    */   protected double[] preProcessDataPoint(double[] dataPoint)
/* 203:    */   {
/* 204:226 */     for (double element : dataPoint)
/* 205:    */     {
/* 206:227 */       if (element < this.m_min)
/* 207:    */       {
/* 208:228 */         this.m_oldMin = this.m_min;
/* 209:229 */         this.m_min = element;
/* 210:230 */         this.m_yScaleUpdate = true;
/* 211:    */       }
/* 212:233 */       if (element > this.m_max)
/* 213:    */       {
/* 214:234 */         this.m_oldMax = this.m_max;
/* 215:235 */         this.m_max = element;
/* 216:236 */         this.m_yScaleUpdate = true;
/* 217:    */       }
/* 218:    */     }
/* 219:240 */     if (this.m_yScaleUpdate)
/* 220:    */     {
/* 221:241 */       this.m_scalePanel.repaint();
/* 222:242 */       this.m_yScaleUpdate = false;
/* 223:    */     }
/* 224:246 */     return dataPoint;
/* 225:    */   }
/* 226:    */   
/* 227:    */   public void acceptDataPoint(double[] dataPoint)
/* 228:    */   {
/* 229:256 */     if (this.m_xCount % ((StripChart)getStep()).getRefreshFreq() != 0)
/* 230:    */     {
/* 231:257 */       this.m_xCount += 1;
/* 232:258 */       return;
/* 233:    */     }
/* 234:260 */     dataPoint = preProcessDataPoint(dataPoint);
/* 235:262 */     if (this.m_previousY[0] == -1.0D)
/* 236:    */     {
/* 237:263 */       int iw = this.m_plotPanel.getWidth();
/* 238:264 */       int ih = this.m_plotPanel.getHeight();
/* 239:265 */       this.m_osi = this.m_plotPanel.createImage(iw, ih);
/* 240:266 */       Graphics m = this.m_osi.getGraphics();
/* 241:267 */       m.setColor(this.m_BackgroundColor);
/* 242:268 */       m.fillRect(0, 0, iw, ih);
/* 243:269 */       this.m_previousY[0] = convertToPanelY(0.0D);
/* 244:270 */       this.m_iheight = ih;
/* 245:271 */       this.m_iwidth = iw;
/* 246:    */     }
/* 247:274 */     if (dataPoint.length != this.m_previousY.length)
/* 248:    */     {
/* 249:275 */       this.m_previousY = new double[dataPoint.length];
/* 250:276 */       for (int i = 0; i < dataPoint.length; i++) {
/* 251:277 */         this.m_previousY[i] = convertToPanelY(0.0D);
/* 252:    */       }
/* 253:    */     }
/* 254:281 */     Graphics osg = this.m_osi.getGraphics();
/* 255:282 */     Graphics g = this.m_plotPanel.getGraphics();
/* 256:    */     
/* 257:284 */     osg.copyArea(this.m_refreshWidth, 0, this.m_iwidth - this.m_refreshWidth, this.m_iheight, -this.m_refreshWidth, 0);
/* 258:    */     
/* 259:286 */     osg.setColor(this.m_BackgroundColor);
/* 260:287 */     osg.fillRect(this.m_iwidth - this.m_refreshWidth, 0, this.m_iwidth, this.m_iheight);
/* 261:290 */     if (this.m_yScaleUpdate)
/* 262:    */     {
/* 263:291 */       String maxVal = numToString(this.m_oldMax);
/* 264:292 */       String minVal = numToString(this.m_oldMin);
/* 265:293 */       String midVal = numToString((this.m_oldMax - this.m_oldMin) / 2.0D);
/* 266:294 */       if (this.m_labelMetrics == null) {
/* 267:295 */         this.m_labelMetrics = g.getFontMetrics(this.m_labelFont);
/* 268:    */       }
/* 269:297 */       osg.setFont(this.m_labelFont);
/* 270:298 */       int wmx = this.m_labelMetrics.stringWidth(maxVal);
/* 271:299 */       int wmn = this.m_labelMetrics.stringWidth(minVal);
/* 272:300 */       int wmd = this.m_labelMetrics.stringWidth(midVal);
/* 273:    */       
/* 274:302 */       int hf = this.m_labelMetrics.getAscent();
/* 275:303 */       osg.setColor(this.m_colorList[(this.m_colorList.length - 1)]);
/* 276:304 */       osg.drawString(maxVal, this.m_iwidth - wmx, hf - 2);
/* 277:305 */       osg.drawString(midVal, this.m_iwidth - wmd, this.m_iheight / 2 + hf / 2);
/* 278:306 */       osg.drawString(minVal, this.m_iwidth - wmn, this.m_iheight - 1);
/* 279:307 */       this.m_yScaleUpdate = false;
/* 280:    */     }
/* 281:311 */     for (int i = 0; i < dataPoint.length; i++) {
/* 282:312 */       if (!Utils.isMissingValue(dataPoint[i]))
/* 283:    */       {
/* 284:315 */         osg.setColor(this.m_colorList[(i % this.m_colorList.length)]);
/* 285:316 */         double pos = convertToPanelY(dataPoint[i]);
/* 286:317 */         osg.drawLine(this.m_iwidth - this.m_refreshWidth, (int)this.m_previousY[i], this.m_iwidth - 1, (int)pos);
/* 287:    */         
/* 288:319 */         this.m_previousY[i] = pos;
/* 289:320 */         if (this.m_xCount % ((StripChart)getStep()).getXLabelFreq() == 0)
/* 290:    */         {
/* 291:322 */           String val = numToString(dataPoint[i]);
/* 292:323 */           if (this.m_labelMetrics == null) {
/* 293:324 */             this.m_labelMetrics = g.getFontMetrics(this.m_labelFont);
/* 294:    */           }
/* 295:326 */           int hf = this.m_labelMetrics.getAscent();
/* 296:327 */           if (pos - hf < 0.0D) {
/* 297:328 */             pos += hf;
/* 298:    */           }
/* 299:330 */           int w = this.m_labelMetrics.stringWidth(val);
/* 300:331 */           osg.setFont(this.m_labelFont);
/* 301:332 */           osg.drawString(val, this.m_iwidth - w, (int)pos);
/* 302:    */         }
/* 303:    */       }
/* 304:    */     }
/* 305:336 */     if (this.m_xCount % ((StripChart)getStep()).getXLabelFreq() == 0)
/* 306:    */     {
/* 307:338 */       String xVal = "" + this.m_xCount;
/* 308:339 */       osg.setColor(this.m_colorList[(this.m_colorList.length - 1)]);
/* 309:340 */       int w = this.m_labelMetrics.stringWidth(xVal);
/* 310:341 */       osg.setFont(this.m_labelFont);
/* 311:342 */       osg.drawString(xVal, this.m_iwidth - w, this.m_iheight - 1);
/* 312:    */     }
/* 313:344 */     g.drawImage(this.m_osi, 0, 0, this.m_plotPanel);
/* 314:345 */     this.m_xCount += 1;
/* 315:    */   }
/* 316:    */   
/* 317:    */   private void setRefreshWidth()
/* 318:    */   {
/* 319:349 */     this.m_refreshWidth = ((StripChart)getStep()).getRefreshWidth();
/* 320:350 */     if (this.m_labelMetrics == null)
/* 321:    */     {
/* 322:351 */       getGraphics().setFont(this.m_labelFont);
/* 323:352 */       this.m_labelMetrics = getGraphics().getFontMetrics(this.m_labelFont);
/* 324:    */     }
/* 325:355 */     int refWidth = this.m_labelMetrics.stringWidth("99000");
/* 326:    */     
/* 327:357 */     int z = ((StripChart)getStep()).getXLabelFreq() / ((StripChart)getStep()).getRefreshFreq();
/* 328:360 */     if (z < 1) {
/* 329:361 */       z = 1;
/* 330:    */     }
/* 331:364 */     if (z * this.m_refreshWidth < refWidth + 5) {
/* 332:365 */       this.m_refreshWidth *= ((refWidth + 5) / z + 1);
/* 333:    */     }
/* 334:    */   }
/* 335:    */   
/* 336:    */   private class StripPlotter
/* 337:    */     extends JPanel
/* 338:    */   {
/* 339:    */     private static final long serialVersionUID = -7056271598761675879L;
/* 340:    */     
/* 341:    */     private StripPlotter() {}
/* 342:    */     
/* 343:    */     public void paintComponent(Graphics g)
/* 344:    */     {
/* 345:379 */       super.paintComponent(g);
/* 346:380 */       if (StripChartInteractiveView.this.m_osi != null) {
/* 347:381 */         g.drawImage(StripChartInteractiveView.this.m_osi, 0, 0, this);
/* 348:    */       }
/* 349:    */     }
/* 350:    */   }
/* 351:    */   
/* 352:    */   private class ScalePanel
/* 353:    */     extends JPanel
/* 354:    */   {
/* 355:    */     private static final long serialVersionUID = 6416998474984829434L;
/* 356:    */     
/* 357:    */     private ScalePanel() {}
/* 358:    */     
/* 359:    */     public void paintComponent(Graphics gx)
/* 360:    */     {
/* 361:396 */       super.paintComponent(gx);
/* 362:397 */       if (StripChartInteractiveView.this.m_labelMetrics == null) {
/* 363:398 */         StripChartInteractiveView.this.m_labelMetrics = gx.getFontMetrics(StripChartInteractiveView.this.m_labelFont);
/* 364:    */       }
/* 365:400 */       gx.setFont(StripChartInteractiveView.this.m_labelFont);
/* 366:401 */       int hf = StripChartInteractiveView.this.m_labelMetrics.getAscent();
/* 367:402 */       String temp = "" + StripChartInteractiveView.this.m_max;
/* 368:403 */       gx.setColor(StripChartInteractiveView.this.m_colorList[(StripChartInteractiveView.this.m_colorList.length - 1)]);
/* 369:404 */       gx.drawString(temp, 1, hf - 2);
/* 370:405 */       temp = "" + (StripChartInteractiveView.this.m_min + (StripChartInteractiveView.this.m_max - StripChartInteractiveView.this.m_min) / 2.0D);
/* 371:406 */       gx.drawString(temp, 1, getHeight() / 2 + hf / 2);
/* 372:407 */       temp = "" + StripChartInteractiveView.this.m_min;
/* 373:408 */       gx.drawString(temp, 1, getHeight() - 1);
/* 374:    */     }
/* 375:    */   }
/* 376:    */   
/* 377:    */   protected class LegendPanel
/* 378:    */     extends JPanel
/* 379:    */   {
/* 380:    */     private static final long serialVersionUID = 7713986576833797583L;
/* 381:    */     
/* 382:    */     protected LegendPanel() {}
/* 383:    */     
/* 384:    */     public void paintComponent(Graphics gx)
/* 385:    */     {
/* 386:422 */       super.paintComponent(gx);
/* 387:424 */       if (StripChartInteractiveView.this.m_labelMetrics == null) {
/* 388:425 */         StripChartInteractiveView.this.m_labelMetrics = gx.getFontMetrics(StripChartInteractiveView.this.m_labelFont);
/* 389:    */       }
/* 390:427 */       int hf = StripChartInteractiveView.this.m_labelMetrics.getAscent();
/* 391:428 */       int x = 10;
/* 392:429 */       int y = hf + 15;
/* 393:430 */       gx.setFont(StripChartInteractiveView.this.m_labelFont);
/* 394:431 */       for (int i = 0; i < StripChartInteractiveView.this.m_legendText.size(); i++)
/* 395:    */       {
/* 396:432 */         String temp = (String)StripChartInteractiveView.this.m_legendText.get(i);
/* 397:433 */         gx.setColor(StripChartInteractiveView.this.m_colorList[(i % StripChartInteractiveView.this.m_colorList.length)]);
/* 398:434 */         gx.drawString(temp, x, y);
/* 399:435 */         y += hf;
/* 400:    */       }
/* 401:437 */       StripChartInteractiveView.this.revalidate();
/* 402:    */     }
/* 403:    */   }
/* 404:    */   
/* 405:    */   private static String numToString(double num)
/* 406:    */   {
/* 407:442 */     int precision = 1;
/* 408:443 */     int whole = (int)Math.abs(num);
/* 409:444 */     double decimal = Math.abs(num) - whole;
/* 410:    */     
/* 411:446 */     int nondecimal = whole > 0 ? (int)(Math.log(whole) / Math.log(10.0D)) : 1;
/* 412:    */     
/* 413:448 */     precision = decimal > 0.0D ? (int)Math.abs(Math.log(Math.abs(num)) / Math.log(10.0D)) + 2 : 1;
/* 414:451 */     if (precision > 5) {
/* 415:452 */       precision = 1;
/* 416:    */     }
/* 417:455 */     String numString = Utils.doubleToString(num, nondecimal + 1 + precision, precision);
/* 418:    */     
/* 419:    */ 
/* 420:    */ 
/* 421:459 */     return numString;
/* 422:    */   }
/* 423:    */   
/* 424:    */   public Defaults getDefaultSettings()
/* 425:    */   {
/* 426:469 */     return new StripChartInteractiveViewDefaults();
/* 427:    */   }
/* 428:    */   
/* 429:    */   public void applySettings(Settings settings)
/* 430:    */   {
/* 431:479 */     this.m_BackgroundColor = ((Color)settings.getSetting("weka.gui.knowledgeflow.steps.stripchart", StripChartInteractiveViewDefaults.BACKGROUND_COLOR_KEY, StripChartInteractiveViewDefaults.BACKGROUND_COLOR, Environment.getSystemWide()));
/* 432:    */     
/* 433:    */ 
/* 434:    */ 
/* 435:    */ 
/* 436:484 */     this.m_plotPanel.setBackground(this.m_BackgroundColor);
/* 437:485 */     this.m_scalePanel.setBackground(this.m_BackgroundColor);
/* 438:486 */     this.m_legendPanel.setBackground(this.m_BackgroundColor);
/* 439:    */     
/* 440:488 */     this.m_LegendPanelBorderColor = ((Color)settings.getSetting("weka.gui.knowledgeflow.steps.stripchart", StripChartInteractiveViewDefaults.LEGEND_BORDER_COLOR_KEY, StripChartInteractiveViewDefaults.LEGEND_BORDER_COLOR, Environment.getSystemWide()));
/* 441:    */     
/* 442:    */ 
/* 443:    */ 
/* 444:    */ 
/* 445:    */ 
/* 446:494 */     Font lf = new Font("Monospaced", 0, 12);
/* 447:495 */     this.m_legendPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.gray, Color.darkGray), "Legend", 2, 0, lf, this.m_LegendPanelBorderColor));
/* 448:    */     
/* 449:    */ 
/* 450:    */ 
/* 451:    */ 
/* 452:500 */     this.m_colorList[(this.m_colorList.length - 1)] = ((Color)settings.getSetting("weka.gui.knowledgeflow.steps.stripchart", StripChartInteractiveViewDefaults.X_LABEL_COLOR_KEY, StripChartInteractiveViewDefaults.X_LABEL_COLOR, Environment.getSystemWide()));
/* 453:    */   }
/* 454:    */   
/* 455:    */   protected static final class StripChartInteractiveViewDefaults
/* 456:    */     extends Defaults
/* 457:    */   {
/* 458:    */     public static final String ID = "weka.gui.knowledgeflow.steps.stripchart";
/* 459:515 */     protected static final Settings.SettingKey BACKGROUND_COLOR_KEY = new Settings.SettingKey("weka.gui.knowledgeflow.steps.stripchart.outputBackgroundColor", "Output background color", "Output background color");
/* 460:518 */     protected static final Color BACKGROUND_COLOR = Color.black;
/* 461:520 */     protected static final Settings.SettingKey LEGEND_BORDER_COLOR_KEY = new Settings.SettingKey("weka.gui.knowledgeflow.steps.stripchart.legendBorderColor", "Legend border color", "Legend border color");
/* 462:523 */     protected static final Color LEGEND_BORDER_COLOR = new Color(253, 255, 61);
/* 463:525 */     protected static final Settings.SettingKey X_LABEL_COLOR_KEY = new Settings.SettingKey("weka.gui.knowledgeflow.steps.stripchart.xLabelColor", "Color for x label text", "Color for x label text");
/* 464:528 */     protected static final Color X_LABEL_COLOR = Color.white;
/* 465:    */     private static final long serialVersionUID = 2247370679260844812L;
/* 466:    */     
/* 467:    */     public StripChartInteractiveViewDefaults()
/* 468:    */     {
/* 469:533 */       super();
/* 470:534 */       this.m_defaults.put(BACKGROUND_COLOR_KEY, BACKGROUND_COLOR);
/* 471:535 */       this.m_defaults.put(LEGEND_BORDER_COLOR_KEY, LEGEND_BORDER_COLOR);
/* 472:536 */       this.m_defaults.put(X_LABEL_COLOR_KEY, X_LABEL_COLOR);
/* 473:    */     }
/* 474:    */   }
/* 475:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.StripChartInteractiveView
 * JD-Core Version:    0.7.0.1
 */