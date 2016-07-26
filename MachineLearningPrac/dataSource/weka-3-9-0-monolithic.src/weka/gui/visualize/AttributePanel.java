/*   1:    */ package weka.gui.visualize;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Container;
/*   6:    */ import java.awt.Dimension;
/*   7:    */ import java.awt.Graphics;
/*   8:    */ import java.awt.GridBagConstraints;
/*   9:    */ import java.awt.GridBagLayout;
/*  10:    */ import java.awt.Insets;
/*  11:    */ import java.awt.event.MouseAdapter;
/*  12:    */ import java.awt.event.MouseEvent;
/*  13:    */ import java.awt.event.WindowAdapter;
/*  14:    */ import java.awt.event.WindowEvent;
/*  15:    */ import java.io.BufferedReader;
/*  16:    */ import java.io.FileReader;
/*  17:    */ import java.io.PrintStream;
/*  18:    */ import java.io.Reader;
/*  19:    */ import java.util.ArrayList;
/*  20:    */ import java.util.Properties;
/*  21:    */ import javax.swing.JFrame;
/*  22:    */ import javax.swing.JPanel;
/*  23:    */ import javax.swing.JScrollPane;
/*  24:    */ import weka.core.Attribute;
/*  25:    */ import weka.core.Environment;
/*  26:    */ import weka.core.Instance;
/*  27:    */ import weka.core.Instances;
/*  28:    */ import weka.core.Settings;
/*  29:    */ 
/*  30:    */ public class AttributePanel
/*  31:    */   extends JScrollPane
/*  32:    */ {
/*  33:    */   private static final long serialVersionUID = 3533330317806757814L;
/*  34: 58 */   protected Instances m_plotInstances = null;
/*  35:    */   protected double m_maxC;
/*  36:    */   protected double m_minC;
/*  37:    */   protected int m_cIndex;
/*  38:    */   protected int m_xIndex;
/*  39:    */   protected int m_yIndex;
/*  40:    */   protected ArrayList<Color> m_colorList;
/*  41: 71 */   protected Color[] m_DefaultColors = { Color.blue, Color.red, Color.green, Color.cyan, Color.pink, new Color(255, 0, 255), Color.orange, new Color(255, 0, 0), new Color(0, 255, 0), Color.white };
/*  42: 79 */   protected Color m_backgroundColor = null;
/*  43: 82 */   protected ArrayList<AttributePanelListener> m_Listeners = new ArrayList();
/*  44:    */   protected int[] m_heights;
/*  45: 93 */   protected JPanel m_span = null;
/*  46: 99 */   protected Color m_barColour = Color.black;
/*  47:    */   
/*  48:    */   protected class AttributeSpacing
/*  49:    */     extends JPanel
/*  50:    */   {
/*  51:    */     private static final long serialVersionUID = 7220615894321679898L;
/*  52:    */     protected double m_maxVal;
/*  53:    */     protected double m_minVal;
/*  54:    */     protected Attribute m_attrib;
/*  55:    */     protected int m_attribIndex;
/*  56:    */     protected int[] m_cached;
/*  57:    */     protected boolean[][] m_pointDrawn;
/*  58:134 */     protected int m_oldWidth = -9000;
/*  59:    */     
/*  60:    */     public AttributeSpacing(Attribute a, int aind)
/*  61:    */     {
/*  62:149 */       this.m_attrib = a;
/*  63:150 */       this.m_attribIndex = aind;
/*  64:151 */       setBackground(AttributePanel.this.m_barColour);
/*  65:152 */       setPreferredSize(new Dimension(0, 20));
/*  66:153 */       setMinimumSize(new Dimension(0, 20));
/*  67:154 */       this.m_cached = new int[AttributePanel.this.m_plotInstances.numInstances()];
/*  68:    */       
/*  69:    */ 
/*  70:    */ 
/*  71:158 */       double min = (1.0D / 0.0D);
/*  72:159 */       double max = (-1.0D / 0.0D);
/*  73:161 */       if (AttributePanel.this.m_plotInstances.attribute(this.m_attribIndex).isNominal())
/*  74:    */       {
/*  75:162 */         this.m_minVal = 0.0D;
/*  76:163 */         this.m_maxVal = (AttributePanel.this.m_plotInstances.attribute(this.m_attribIndex).numValues() - 1);
/*  77:    */       }
/*  78:    */       else
/*  79:    */       {
/*  80:165 */         for (int i = 0; i < AttributePanel.this.m_plotInstances.numInstances(); i++) {
/*  81:166 */           if (!AttributePanel.this.m_plotInstances.instance(i).isMissing(this.m_attribIndex))
/*  82:    */           {
/*  83:167 */             double value = AttributePanel.this.m_plotInstances.instance(i).value(this.m_attribIndex);
/*  84:168 */             if (value < min) {
/*  85:169 */               min = value;
/*  86:    */             }
/*  87:171 */             if (value > max) {
/*  88:172 */               max = value;
/*  89:    */             }
/*  90:    */           }
/*  91:    */         }
/*  92:176 */         this.m_minVal = min;
/*  93:177 */         this.m_maxVal = max;
/*  94:178 */         if (min == max)
/*  95:    */         {
/*  96:179 */           this.m_maxVal += 0.05D;
/*  97:180 */           this.m_minVal -= 0.05D;
/*  98:    */         }
/*  99:    */       }
/* 100:184 */       addMouseListener(new MouseAdapter()
/* 101:    */       {
/* 102:    */         public void mouseClicked(MouseEvent e)
/* 103:    */         {
/* 104:187 */           if ((e.getModifiers() & 0x10) == 16)
/* 105:    */           {
/* 106:188 */             AttributePanel.this.setX(AttributePanel.AttributeSpacing.this.m_attribIndex);
/* 107:189 */             if (AttributePanel.this.m_Listeners.size() > 0) {
/* 108:190 */               for (int i = 0; i < AttributePanel.this.m_Listeners.size(); i++)
/* 109:    */               {
/* 110:191 */                 AttributePanelListener l = (AttributePanelListener)AttributePanel.this.m_Listeners.get(i);
/* 111:192 */                 l.attributeSelectionChange(new AttributePanelEvent(true, false, AttributePanel.AttributeSpacing.this.m_attribIndex));
/* 112:    */               }
/* 113:    */             }
/* 114:    */           }
/* 115:    */           else
/* 116:    */           {
/* 117:198 */             AttributePanel.this.setY(AttributePanel.AttributeSpacing.this.m_attribIndex);
/* 118:199 */             if (AttributePanel.this.m_Listeners.size() > 0) {
/* 119:200 */               for (int i = 0; i < AttributePanel.this.m_Listeners.size(); i++)
/* 120:    */               {
/* 121:201 */                 AttributePanelListener l = (AttributePanelListener)AttributePanel.this.m_Listeners.get(i);
/* 122:202 */                 l.attributeSelectionChange(new AttributePanelEvent(false, true, AttributePanel.AttributeSpacing.this.m_attribIndex));
/* 123:    */               }
/* 124:    */             }
/* 125:    */           }
/* 126:    */         }
/* 127:    */       });
/* 128:    */     }
/* 129:    */     
/* 130:    */     private double convertToPanel(double val)
/* 131:    */     {
/* 132:218 */       double temp = (val - this.m_minVal) / (this.m_maxVal - this.m_minVal);
/* 133:219 */       double temp2 = temp * (getWidth() - 10);
/* 134:    */       
/* 135:221 */       return temp2 + 4.0D;
/* 136:    */     }
/* 137:    */     
/* 138:    */     public void paintComponent(Graphics gx)
/* 139:    */     {
/* 140:232 */       setBackground(AttributePanel.this.m_barColour);
/* 141:233 */       super.paintComponent(gx);
/* 142:    */       
/* 143:235 */       int h = getWidth();
/* 144:236 */       if ((AttributePanel.this.m_plotInstances != null) && (AttributePanel.this.m_plotInstances.numAttributes() > 0) && (AttributePanel.this.m_plotInstances.numInstances() > 0))
/* 145:    */       {
/* 146:239 */         if (this.m_oldWidth != h)
/* 147:    */         {
/* 148:240 */           this.m_pointDrawn = new boolean[h][20];
/* 149:241 */           for (int noa = 0; noa < AttributePanel.this.m_plotInstances.numInstances(); noa++) {
/* 150:242 */             if ((!AttributePanel.this.m_plotInstances.instance(noa).isMissing(this.m_attribIndex)) && (!AttributePanel.this.m_plotInstances.instance(noa).isMissing(AttributePanel.this.m_cIndex)))
/* 151:    */             {
/* 152:244 */               this.m_cached[noa] = ((int)convertToPanel(AttributePanel.this.m_plotInstances.instance(noa).value(this.m_attribIndex)));
/* 153:248 */               if (this.m_pointDrawn[(this.m_cached[noa] % h)][AttributePanel.this.m_heights[noa]] != 0) {
/* 154:249 */                 this.m_cached[noa] = -9000;
/* 155:    */               } else {
/* 156:251 */                 this.m_pointDrawn[(this.m_cached[noa] % h)][AttributePanel.this.m_heights[noa]] = 1;
/* 157:    */               }
/* 158:    */             }
/* 159:    */             else
/* 160:    */             {
/* 161:255 */               this.m_cached[noa] = -9000;
/* 162:    */             }
/* 163:    */           }
/* 164:259 */           this.m_oldWidth = h;
/* 165:    */         }
/* 166:262 */         if (AttributePanel.this.m_plotInstances.attribute(AttributePanel.this.m_cIndex).isNominal()) {
/* 167:263 */           for (int noa = 0; noa < AttributePanel.this.m_plotInstances.numInstances(); noa++) {
/* 168:265 */             if (this.m_cached[noa] != -9000)
/* 169:    */             {
/* 170:266 */               int xp = this.m_cached[noa];
/* 171:267 */               int yp = AttributePanel.this.m_heights[noa];
/* 172:268 */               if (AttributePanel.this.m_plotInstances.attribute(this.m_attribIndex).isNominal()) {
/* 173:269 */                 xp += (int)(Math.random() * 5.0D) - 2;
/* 174:    */               }
/* 175:271 */               int ci = (int)AttributePanel.this.m_plotInstances.instance(noa).value(AttributePanel.this.m_cIndex);
/* 176:    */               
/* 177:273 */               gx.setColor((Color)AttributePanel.this.m_colorList.get(ci % AttributePanel.this.m_colorList.size()));
/* 178:274 */               gx.drawRect(xp, yp, 1, 1);
/* 179:    */             }
/* 180:    */           }
/* 181:    */         } else {
/* 182:279 */           for (int noa = 0; noa < AttributePanel.this.m_plotInstances.numInstances(); noa++) {
/* 183:280 */             if (this.m_cached[noa] != -9000)
/* 184:    */             {
/* 185:282 */               double r = (AttributePanel.this.m_plotInstances.instance(noa).value(AttributePanel.this.m_cIndex) - AttributePanel.this.m_minC) / (AttributePanel.this.m_maxC - AttributePanel.this.m_minC);
/* 186:    */               
/* 187:    */ 
/* 188:    */ 
/* 189:286 */               r = r * 240.0D + 15.0D;
/* 190:    */               
/* 191:288 */               gx.setColor(new Color((int)r, 150, (int)(255.0D - r)));
/* 192:    */               
/* 193:290 */               int xp = this.m_cached[noa];
/* 194:291 */               int yp = AttributePanel.this.m_heights[noa];
/* 195:292 */               if (AttributePanel.this.m_plotInstances.attribute(this.m_attribIndex).isNominal()) {
/* 196:293 */                 xp += (int)(Math.random() * 5.0D) - 2;
/* 197:    */               }
/* 198:295 */               gx.drawRect(xp, yp, 1, 1);
/* 199:    */             }
/* 200:    */           }
/* 201:    */         }
/* 202:    */       }
/* 203:    */     }
/* 204:    */   }
/* 205:    */   
/* 206:    */   private void setProperties()
/* 207:    */   {
/* 208:307 */     if (VisualizeUtils.VISUALIZE_PROPERTIES != null)
/* 209:    */     {
/* 210:308 */       String thisClass = getClass().getName();
/* 211:309 */       String barKey = thisClass + ".barColour";
/* 212:    */       
/* 213:311 */       String barC = VisualizeUtils.VISUALIZE_PROPERTIES.getProperty(barKey);
/* 214:312 */       if (barC != null) {
/* 215:319 */         this.m_barColour = VisualizeUtils.processColour(barC, this.m_barColour);
/* 216:    */       }
/* 217:    */     }
/* 218:    */   }
/* 219:    */   
/* 220:    */   public void applySettings(Settings settings, String ownerID)
/* 221:    */   {
/* 222:332 */     this.m_barColour = ((Color)settings.getSetting(ownerID, VisualizeUtils.VisualizeDefaults.BAR_BACKGROUND_COLOUR_KEY, VisualizeUtils.VisualizeDefaults.BAR_BACKGROUND_COLOUR, Environment.getSystemWide()));
/* 223:    */     
/* 224:    */ 
/* 225:    */ 
/* 226:    */ 
/* 227:337 */     repaint();
/* 228:    */   }
/* 229:    */   
/* 230:    */   public AttributePanel()
/* 231:    */   {
/* 232:341 */     this(null);
/* 233:    */   }
/* 234:    */   
/* 235:    */   public AttributePanel(Color background)
/* 236:    */   {
/* 237:348 */     this.m_backgroundColor = background;
/* 238:    */     
/* 239:350 */     setProperties();
/* 240:351 */     setBackground(Color.blue);
/* 241:352 */     setVerticalScrollBarPolicy(22);
/* 242:353 */     this.m_colorList = new ArrayList(10);
/* 243:355 */     for (int noa = this.m_colorList.size(); noa < 10; noa++)
/* 244:    */     {
/* 245:356 */       Color pc = this.m_DefaultColors[(noa % 10)];
/* 246:357 */       int ija = noa / 10;
/* 247:358 */       ija *= 2;
/* 248:359 */       for (int j = 0; j < ija; j++) {
/* 249:360 */         pc = pc.darker();
/* 250:    */       }
/* 251:363 */       this.m_colorList.add(pc);
/* 252:    */     }
/* 253:    */   }
/* 254:    */   
/* 255:    */   public void addAttributePanelListener(AttributePanelListener a)
/* 256:    */   {
/* 257:373 */     this.m_Listeners.add(a);
/* 258:    */   }
/* 259:    */   
/* 260:    */   public void setCindex(int c, double h, double l)
/* 261:    */   {
/* 262:386 */     this.m_cIndex = c;
/* 263:387 */     this.m_maxC = h;
/* 264:388 */     this.m_minC = l;
/* 265:390 */     if (this.m_span != null)
/* 266:    */     {
/* 267:391 */       if ((this.m_plotInstances.numAttributes() > 0) && (this.m_cIndex < this.m_plotInstances.numAttributes())) {
/* 268:393 */         if ((this.m_plotInstances.attribute(this.m_cIndex).isNominal()) && 
/* 269:394 */           (this.m_plotInstances.attribute(this.m_cIndex).numValues() > this.m_colorList.size())) {
/* 270:396 */           extendColourMap();
/* 271:    */         }
/* 272:    */       }
/* 273:400 */       repaint();
/* 274:    */     }
/* 275:    */   }
/* 276:    */   
/* 277:    */   public void setCindex(int c)
/* 278:    */   {
/* 279:412 */     this.m_cIndex = c;
/* 280:417 */     if (this.m_span != null)
/* 281:    */     {
/* 282:418 */       if ((this.m_cIndex < this.m_plotInstances.numAttributes()) && (this.m_plotInstances.attribute(this.m_cIndex).isNumeric()))
/* 283:    */       {
/* 284:420 */         double min = (1.0D / 0.0D);
/* 285:421 */         double max = (-1.0D / 0.0D);
/* 286:424 */         for (int i = 0; i < this.m_plotInstances.numInstances(); i++) {
/* 287:425 */           if (!this.m_plotInstances.instance(i).isMissing(this.m_cIndex))
/* 288:    */           {
/* 289:426 */             double value = this.m_plotInstances.instance(i).value(this.m_cIndex);
/* 290:427 */             if (value < min) {
/* 291:428 */               min = value;
/* 292:    */             }
/* 293:430 */             if (value > max) {
/* 294:431 */               max = value;
/* 295:    */             }
/* 296:    */           }
/* 297:    */         }
/* 298:436 */         this.m_minC = min;
/* 299:437 */         this.m_maxC = max;
/* 300:    */       }
/* 301:439 */       else if (this.m_plotInstances.attribute(this.m_cIndex).numValues() > this.m_colorList.size())
/* 302:    */       {
/* 303:441 */         extendColourMap();
/* 304:    */       }
/* 305:445 */       repaint();
/* 306:    */     }
/* 307:    */   }
/* 308:    */   
/* 309:    */   private void extendColourMap()
/* 310:    */   {
/* 311:453 */     if (this.m_plotInstances.attribute(this.m_cIndex).isNominal()) {
/* 312:454 */       for (int i = this.m_colorList.size(); i < this.m_plotInstances.attribute(this.m_cIndex).numValues(); i++)
/* 313:    */       {
/* 314:456 */         Color pc = this.m_DefaultColors[(i % 10)];
/* 315:457 */         int ija = i / 10;
/* 316:458 */         ija *= 2;
/* 317:459 */         for (int j = 0; j < ija; j++) {
/* 318:460 */           pc = pc.brighter();
/* 319:    */         }
/* 320:463 */         if (this.m_backgroundColor != null) {
/* 321:464 */           pc = Plot2D.checkAgainstBackground(pc, this.m_backgroundColor);
/* 322:    */         }
/* 323:467 */         this.m_colorList.add(pc);
/* 324:    */       }
/* 325:    */     }
/* 326:    */   }
/* 327:    */   
/* 328:    */   public void setColours(ArrayList<Color> cols)
/* 329:    */   {
/* 330:478 */     this.m_colorList = cols;
/* 331:    */   }
/* 332:    */   
/* 333:    */   protected void setDefaultColourList(Color[] list)
/* 334:    */   {
/* 335:482 */     this.m_DefaultColors = list;
/* 336:    */   }
/* 337:    */   
/* 338:    */   public void setInstances(Instances ins)
/* 339:    */     throws Exception
/* 340:    */   {
/* 341:491 */     if (ins.numAttributes() > 512) {
/* 342:492 */       throw new Exception("Can't display more than 512 attributes!");
/* 343:    */     }
/* 344:495 */     if (this.m_span == null) {
/* 345:496 */       this.m_span = new JPanel()
/* 346:    */       {
/* 347:    */         private static final long serialVersionUID = 7107576557995451922L;
/* 348:    */         
/* 349:    */         public void paintComponent(Graphics gx)
/* 350:    */         {
/* 351:501 */           super.paintComponent(gx);
/* 352:502 */           gx.setColor(Color.red);
/* 353:503 */           if (AttributePanel.this.m_yIndex != AttributePanel.this.m_xIndex)
/* 354:    */           {
/* 355:504 */             gx.drawString("X", 5, AttributePanel.this.m_xIndex * 20 + 16);
/* 356:505 */             gx.drawString("Y", 5, AttributePanel.this.m_yIndex * 20 + 16);
/* 357:    */           }
/* 358:    */           else
/* 359:    */           {
/* 360:507 */             gx.drawString("B", 5, AttributePanel.this.m_xIndex * 20 + 16);
/* 361:    */           }
/* 362:    */         }
/* 363:    */       };
/* 364:    */     }
/* 365:513 */     this.m_span.removeAll();
/* 366:514 */     this.m_plotInstances = ins;
/* 367:515 */     if ((ins.numInstances() > 0) && (ins.numAttributes() > 0))
/* 368:    */     {
/* 369:516 */       JPanel padder = new JPanel();
/* 370:517 */       JPanel padd2 = new JPanel();
/* 371:    */       
/* 372:    */ 
/* 373:    */ 
/* 374:    */ 
/* 375:    */ 
/* 376:    */ 
/* 377:524 */       this.m_heights = new int[ins.numInstances()];
/* 378:    */       
/* 379:526 */       this.m_cIndex = (ins.numAttributes() - 1);
/* 380:527 */       for (int noa = 0; noa < ins.numInstances(); noa++) {
/* 381:528 */         this.m_heights[noa] = ((int)(Math.random() * 19.0D));
/* 382:    */       }
/* 383:530 */       this.m_span.setPreferredSize(new Dimension(this.m_span.getPreferredSize().width, (this.m_cIndex + 1) * 20));
/* 384:    */       
/* 385:532 */       this.m_span.setMaximumSize(new Dimension(this.m_span.getMaximumSize().width, (this.m_cIndex + 1) * 20));
/* 386:    */       
/* 387:    */ 
/* 388:    */ 
/* 389:536 */       GridBagLayout gb = new GridBagLayout();
/* 390:537 */       GridBagLayout gb2 = new GridBagLayout();
/* 391:538 */       GridBagConstraints constraints = new GridBagConstraints();
/* 392:    */       
/* 393:540 */       padder.setLayout(gb);
/* 394:541 */       this.m_span.setLayout(gb2);
/* 395:542 */       constraints.anchor = 10;
/* 396:543 */       constraints.gridx = 0;
/* 397:544 */       constraints.gridy = 0;
/* 398:545 */       constraints.weightx = 5.0D;
/* 399:546 */       constraints.fill = 2;
/* 400:547 */       constraints.gridwidth = 1;
/* 401:548 */       constraints.gridheight = 1;
/* 402:549 */       constraints.insets = new Insets(0, 0, 0, 0);
/* 403:550 */       padder.add(this.m_span, constraints);
/* 404:551 */       constraints.gridx = 0;
/* 405:552 */       constraints.gridy = 1;
/* 406:553 */       constraints.weightx = 5.0D;
/* 407:554 */       constraints.fill = 1;
/* 408:555 */       constraints.gridwidth = 1;
/* 409:556 */       constraints.gridheight = 1;
/* 410:557 */       constraints.weighty = 5.0D;
/* 411:558 */       constraints.insets = new Insets(0, 0, 0, 0);
/* 412:559 */       padder.add(padd2, constraints);
/* 413:560 */       constraints.weighty = 0.0D;
/* 414:561 */       setViewportView(padder);
/* 415:    */       
/* 416:    */ 
/* 417:    */ 
/* 418:565 */       constraints.anchor = 10;
/* 419:566 */       constraints.gridx = 0;
/* 420:567 */       constraints.gridy = 0;
/* 421:568 */       constraints.weightx = 5.0D;
/* 422:569 */       constraints.fill = 2;
/* 423:570 */       constraints.gridwidth = 1;
/* 424:571 */       constraints.gridheight = 1;
/* 425:572 */       constraints.weighty = 5.0D;
/* 426:573 */       constraints.insets = new Insets(2, 20, 2, 4);
/* 427:575 */       for (int noa = 0; noa < ins.numAttributes(); noa++)
/* 428:    */       {
/* 429:576 */         AttributeSpacing tmp = new AttributeSpacing(ins.attribute(noa), noa);
/* 430:    */         
/* 431:578 */         constraints.gridy = noa;
/* 432:579 */         this.m_span.add(tmp, constraints);
/* 433:    */       }
/* 434:    */     }
/* 435:    */   }
/* 436:    */   
/* 437:    */   public void setX(int x)
/* 438:    */   {
/* 439:590 */     if (this.m_span != null)
/* 440:    */     {
/* 441:591 */       this.m_xIndex = x;
/* 442:592 */       this.m_span.repaint();
/* 443:    */     }
/* 444:    */   }
/* 445:    */   
/* 446:    */   public void setY(int y)
/* 447:    */   {
/* 448:602 */     if (this.m_span != null)
/* 449:    */     {
/* 450:603 */       this.m_yIndex = y;
/* 451:604 */       this.m_span.repaint();
/* 452:    */     }
/* 453:    */   }
/* 454:    */   
/* 455:    */   public static void main(String[] args)
/* 456:    */   {
/* 457:    */     try
/* 458:    */     {
/* 459:616 */       if (args.length < 1)
/* 460:    */       {
/* 461:617 */         System.err.println("Usage : weka.gui.visualize.AttributePanel <dataset> [class col]");
/* 462:    */         
/* 463:619 */         System.exit(1);
/* 464:    */       }
/* 465:621 */       JFrame jf = new JFrame("Weka Explorer: Attribute");
/* 466:    */       
/* 467:623 */       jf.setSize(100, 100);
/* 468:624 */       jf.getContentPane().setLayout(new BorderLayout());
/* 469:625 */       AttributePanel p2 = new AttributePanel();
/* 470:626 */       p2.addAttributePanelListener(new AttributePanelListener()
/* 471:    */       {
/* 472:    */         public void attributeSelectionChange(AttributePanelEvent e)
/* 473:    */         {
/* 474:629 */           if (e.m_xChange) {
/* 475:630 */             System.err.println("X index changed to : " + e.m_indexVal);
/* 476:    */           } else {
/* 477:632 */             System.err.println("Y index changed to : " + e.m_indexVal);
/* 478:    */           }
/* 479:    */         }
/* 480:635 */       });
/* 481:636 */       jf.getContentPane().add(p2, "Center");
/* 482:637 */       jf.addWindowListener(new WindowAdapter()
/* 483:    */       {
/* 484:    */         public void windowClosing(WindowEvent e)
/* 485:    */         {
/* 486:640 */           this.val$jf.dispose();
/* 487:641 */           System.exit(0);
/* 488:    */         }
/* 489:    */       });
/* 490:644 */       if (args.length >= 1)
/* 491:    */       {
/* 492:645 */         System.err.println("Loading instances from " + args[0]);
/* 493:646 */         Reader r = new BufferedReader(new FileReader(args[0]));
/* 494:    */         
/* 495:648 */         Instances i = new Instances(r);
/* 496:649 */         i.setClassIndex(i.numAttributes() - 1);
/* 497:650 */         p2.setInstances(i);
/* 498:    */       }
/* 499:652 */       if (args.length > 1) {
/* 500:653 */         p2.setCindex(Integer.parseInt(args[1]) - 1);
/* 501:    */       } else {
/* 502:655 */         p2.setCindex(0);
/* 503:    */       }
/* 504:657 */       jf.setVisible(true);
/* 505:    */     }
/* 506:    */     catch (Exception ex)
/* 507:    */     {
/* 508:659 */       ex.printStackTrace();
/* 509:660 */       System.err.println(ex.getMessage());
/* 510:    */     }
/* 511:    */   }
/* 512:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.AttributePanel
 * JD-Core Version:    0.7.0.1
 */