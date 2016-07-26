/*   1:    */ package weka.gui.visualize;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Component;
/*   6:    */ import java.awt.Container;
/*   7:    */ import java.awt.Font;
/*   8:    */ import java.awt.FontMetrics;
/*   9:    */ import java.awt.Graphics;
/*  10:    */ import java.awt.event.ActionEvent;
/*  11:    */ import java.awt.event.ActionListener;
/*  12:    */ import java.awt.event.MouseAdapter;
/*  13:    */ import java.awt.event.MouseEvent;
/*  14:    */ import java.awt.event.WindowAdapter;
/*  15:    */ import java.awt.event.WindowEvent;
/*  16:    */ import java.io.BufferedReader;
/*  17:    */ import java.io.FileReader;
/*  18:    */ import java.io.PrintStream;
/*  19:    */ import java.io.Reader;
/*  20:    */ import java.util.ArrayList;
/*  21:    */ import javax.swing.JColorChooser;
/*  22:    */ import javax.swing.JFrame;
/*  23:    */ import javax.swing.JLabel;
/*  24:    */ import javax.swing.JPanel;
/*  25:    */ import weka.core.Attribute;
/*  26:    */ import weka.core.Instance;
/*  27:    */ import weka.core.Instances;
/*  28:    */ import weka.core.Utils;
/*  29:    */ 
/*  30:    */ public class ClassPanel
/*  31:    */   extends JPanel
/*  32:    */ {
/*  33:    */   private static final long serialVersionUID = -7969401840501661430L;
/*  34: 63 */   private boolean m_isEnabled = false;
/*  35: 66 */   private boolean m_isNumeric = false;
/*  36: 69 */   private final int m_spectrumHeight = 5;
/*  37:    */   private double m_maxC;
/*  38:    */   private double m_minC;
/*  39: 78 */   private final int m_tickSize = 5;
/*  40: 81 */   private FontMetrics m_labelMetrics = null;
/*  41: 84 */   private Font m_labelFont = null;
/*  42: 87 */   private int m_HorizontalPad = 0;
/*  43:    */   private int m_precisionC;
/*  44: 96 */   private int m_oldWidth = -9000;
/*  45: 99 */   private Instances m_Instances = null;
/*  46:    */   private int m_cIndex;
/*  47:    */   private ArrayList<Color> m_colorList;
/*  48:112 */   private final ArrayList<Component> m_Repainters = new ArrayList();
/*  49:118 */   private final ArrayList<ActionListener> m_ColourChangeListeners = new ArrayList();
/*  50:121 */   protected Color[] m_DefaultColors = { Color.blue, Color.red, Color.green, Color.cyan, Color.pink, new Color(255, 0, 255), Color.orange, new Color(255, 0, 0), new Color(0, 255, 0), Color.white };
/*  51:129 */   protected Color m_backgroundColor = null;
/*  52:    */   
/*  53:    */   private class NomLabel
/*  54:    */     extends JLabel
/*  55:    */   {
/*  56:    */     private static final long serialVersionUID = -4686613106474820655L;
/*  57:140 */     private int m_index = 0;
/*  58:    */     
/*  59:    */     public NomLabel(String name, int id)
/*  60:    */     {
/*  61:149 */       super();
/*  62:150 */       this.m_index = id;
/*  63:    */       
/*  64:152 */       addMouseListener(new MouseAdapter()
/*  65:    */       {
/*  66:    */         public void mouseClicked(MouseEvent e)
/*  67:    */         {
/*  68:156 */           if ((e.getModifiers() & 0x10) == 16)
/*  69:    */           {
/*  70:157 */             Color tmp = JColorChooser.showDialog(ClassPanel.this, "Select new Color", (Color)ClassPanel.this.m_colorList.get(ClassPanel.NomLabel.this.m_index));
/*  71:160 */             if (tmp != null)
/*  72:    */             {
/*  73:161 */               ClassPanel.this.m_colorList.set(ClassPanel.NomLabel.this.m_index, tmp);
/*  74:162 */               ClassPanel.this.m_oldWidth = -9000;
/*  75:163 */               ClassPanel.this.repaint();
/*  76:164 */               if (ClassPanel.this.m_Repainters.size() > 0) {
/*  77:165 */                 for (int i = 0; i < ClassPanel.this.m_Repainters.size(); i++) {
/*  78:166 */                   ((Component)ClassPanel.this.m_Repainters.get(i)).repaint();
/*  79:    */                 }
/*  80:    */               }
/*  81:170 */               if (ClassPanel.this.m_ColourChangeListeners.size() > 0) {
/*  82:171 */                 for (int i = 0; i < ClassPanel.this.m_ColourChangeListeners.size(); i++) {
/*  83:172 */                   ((ActionListener)ClassPanel.this.m_ColourChangeListeners.get(i)).actionPerformed(new ActionEvent(this, 0, ""));
/*  84:    */                 }
/*  85:    */               }
/*  86:    */             }
/*  87:    */           }
/*  88:    */         }
/*  89:    */       });
/*  90:    */     }
/*  91:    */   }
/*  92:    */   
/*  93:    */   public ClassPanel()
/*  94:    */   {
/*  95:184 */     this(null);
/*  96:    */   }
/*  97:    */   
/*  98:    */   public ClassPanel(Color background)
/*  99:    */   {
/* 100:188 */     this.m_backgroundColor = background;
/* 101:    */     
/* 102:    */ 
/* 103:191 */     this.m_colorList = new ArrayList(10);
/* 104:192 */     for (int noa = this.m_colorList.size(); noa < 10; noa++)
/* 105:    */     {
/* 106:193 */       Color pc = this.m_DefaultColors[(noa % 10)];
/* 107:194 */       int ija = noa / 10;
/* 108:195 */       ija *= 2;
/* 109:196 */       for (int j = 0; j < ija; j++) {
/* 110:197 */         pc = pc.darker();
/* 111:    */       }
/* 112:200 */       this.m_colorList.add(pc);
/* 113:    */     }
/* 114:    */   }
/* 115:    */   
/* 116:    */   public void addRepaintNotify(Component c)
/* 117:    */   {
/* 118:211 */     this.m_Repainters.add(c);
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void addActionListener(ActionListener a)
/* 122:    */   {
/* 123:221 */     this.m_ColourChangeListeners.add(a);
/* 124:    */   }
/* 125:    */   
/* 126:    */   private void setFonts(Graphics gx)
/* 127:    */   {
/* 128:230 */     if (this.m_labelMetrics == null)
/* 129:    */     {
/* 130:231 */       this.m_labelFont = new Font("Monospaced", 0, 12);
/* 131:232 */       this.m_labelMetrics = gx.getFontMetrics(this.m_labelFont);
/* 132:233 */       int hf = this.m_labelMetrics.getAscent();
/* 133:234 */       if (getHeight() < 3 * hf)
/* 134:    */       {
/* 135:235 */         this.m_labelFont = new Font("Monospaced", 0, 11);
/* 136:236 */         this.m_labelMetrics = gx.getFontMetrics(this.m_labelFont);
/* 137:    */       }
/* 138:    */     }
/* 139:239 */     gx.setFont(this.m_labelFont);
/* 140:    */   }
/* 141:    */   
/* 142:    */   public void setOn(boolean e)
/* 143:    */   {
/* 144:248 */     this.m_isEnabled = e;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public void setInstances(Instances insts)
/* 148:    */   {
/* 149:257 */     this.m_Instances = insts;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public void setCindex(int cIndex)
/* 153:    */   {
/* 154:266 */     if (this.m_Instances.numAttributes() > 0)
/* 155:    */     {
/* 156:267 */       this.m_cIndex = cIndex;
/* 157:268 */       if (this.m_Instances.attribute(this.m_cIndex).isNumeric())
/* 158:    */       {
/* 159:269 */         setNumeric();
/* 160:    */       }
/* 161:    */       else
/* 162:    */       {
/* 163:271 */         if (this.m_Instances.attribute(this.m_cIndex).numValues() > this.m_colorList.size()) {
/* 164:272 */           extendColourMap();
/* 165:    */         }
/* 166:274 */         setNominal();
/* 167:    */       }
/* 168:    */     }
/* 169:    */   }
/* 170:    */   
/* 171:    */   private void extendColourMap()
/* 172:    */   {
/* 173:284 */     if (this.m_Instances.attribute(this.m_cIndex).isNominal()) {
/* 174:285 */       for (int i = this.m_colorList.size(); i < this.m_Instances.attribute(this.m_cIndex).numValues(); i++)
/* 175:    */       {
/* 176:287 */         Color pc = this.m_DefaultColors[(i % 10)];
/* 177:288 */         int ija = i / 10;
/* 178:289 */         ija *= 2;
/* 179:290 */         for (int j = 0; j < ija; j++) {
/* 180:291 */           pc = pc.brighter();
/* 181:    */         }
/* 182:293 */         if (this.m_backgroundColor != null) {
/* 183:294 */           pc = Plot2D.checkAgainstBackground(pc, this.m_backgroundColor);
/* 184:    */         }
/* 185:297 */         this.m_colorList.add(pc);
/* 186:    */       }
/* 187:    */     }
/* 188:    */   }
/* 189:    */   
/* 190:    */   protected void setDefaultColourList(Color[] list)
/* 191:    */   {
/* 192:303 */     this.m_DefaultColors = list;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public void setColours(ArrayList<Color> cols)
/* 196:    */   {
/* 197:312 */     this.m_colorList = cols;
/* 198:    */   }
/* 199:    */   
/* 200:    */   protected void setNominal()
/* 201:    */   {
/* 202:319 */     this.m_isNumeric = false;
/* 203:320 */     this.m_HorizontalPad = 0;
/* 204:321 */     setOn(true);
/* 205:322 */     this.m_oldWidth = -9000;
/* 206:    */     
/* 207:324 */     repaint();
/* 208:    */   }
/* 209:    */   
/* 210:    */   protected void setNumeric()
/* 211:    */   {
/* 212:331 */     this.m_isNumeric = true;
/* 213:    */     
/* 214:    */ 
/* 215:    */ 
/* 216:    */ 
/* 217:336 */     double min = (1.0D / 0.0D);
/* 218:337 */     double max = (-1.0D / 0.0D);
/* 219:340 */     for (int i = 0; i < this.m_Instances.numInstances(); i++) {
/* 220:341 */       if (!this.m_Instances.instance(i).isMissing(this.m_cIndex))
/* 221:    */       {
/* 222:342 */         double value = this.m_Instances.instance(i).value(this.m_cIndex);
/* 223:343 */         if (value < min) {
/* 224:344 */           min = value;
/* 225:    */         }
/* 226:346 */         if (value > max) {
/* 227:347 */           max = value;
/* 228:    */         }
/* 229:    */       }
/* 230:    */     }
/* 231:353 */     if (min == (1.0D / 0.0D)) {
/* 232:354 */       min = max = 0.0D;
/* 233:    */     }
/* 234:357 */     this.m_minC = min;
/* 235:358 */     this.m_maxC = max;
/* 236:    */     
/* 237:360 */     int whole = (int)Math.abs(this.m_maxC);
/* 238:361 */     double decimal = Math.abs(this.m_maxC) - whole;
/* 239:    */     
/* 240:363 */     int nondecimal = whole > 0 ? (int)(Math.log(whole) / Math.log(10.0D)) : 1;
/* 241:    */     
/* 242:365 */     this.m_precisionC = (decimal > 0.0D ? (int)Math.abs(Math.log(Math.abs(this.m_maxC)) / Math.log(10.0D)) + 2 : 1);
/* 243:367 */     if (this.m_precisionC > VisualizeUtils.MAX_PRECISION) {
/* 244:368 */       this.m_precisionC = 1;
/* 245:    */     }
/* 246:371 */     String maxStringC = Utils.doubleToString(this.m_maxC, nondecimal + 1 + this.m_precisionC, this.m_precisionC);
/* 247:373 */     if (this.m_labelMetrics != null) {
/* 248:374 */       this.m_HorizontalPad = this.m_labelMetrics.stringWidth(maxStringC);
/* 249:    */     }
/* 250:377 */     whole = (int)Math.abs(this.m_minC);
/* 251:378 */     decimal = Math.abs(this.m_minC) - whole;
/* 252:379 */     nondecimal = whole > 0 ? (int)(Math.log(whole) / Math.log(10.0D)) : 1;
/* 253:    */     
/* 254:381 */     this.m_precisionC = (decimal > 0.0D ? (int)Math.abs(Math.log(Math.abs(this.m_minC)) / Math.log(10.0D)) + 2 : 1);
/* 255:383 */     if (this.m_precisionC > VisualizeUtils.MAX_PRECISION) {
/* 256:384 */       this.m_precisionC = 1;
/* 257:    */     }
/* 258:387 */     maxStringC = Utils.doubleToString(this.m_minC, nondecimal + 1 + this.m_precisionC, this.m_precisionC);
/* 259:389 */     if ((this.m_labelMetrics != null) && 
/* 260:390 */       (this.m_labelMetrics.stringWidth(maxStringC) > this.m_HorizontalPad)) {
/* 261:391 */       this.m_HorizontalPad = this.m_labelMetrics.stringWidth(maxStringC);
/* 262:    */     }
/* 263:395 */     setOn(true);
/* 264:396 */     repaint();
/* 265:    */   }
/* 266:    */   
/* 267:    */   protected void paintNominal(Graphics gx)
/* 268:    */   {
/* 269:405 */     setFonts(gx);
/* 270:    */     
/* 271:    */ 
/* 272:    */ 
/* 273:409 */     int numClasses = this.m_Instances.attribute(this.m_cIndex).numValues();
/* 274:    */     
/* 275:411 */     int maxLabelLen = 0;
/* 276:412 */     int idx = 0;
/* 277:    */     
/* 278:414 */     int w = getWidth();
/* 279:415 */     int hf = this.m_labelMetrics.getAscent();
/* 280:417 */     for (int i = 0; i < numClasses; i++) {
/* 281:418 */       if (this.m_Instances.attribute(this.m_cIndex).value(i).length() > maxLabelLen)
/* 282:    */       {
/* 283:419 */         maxLabelLen = this.m_Instances.attribute(this.m_cIndex).value(i).length();
/* 284:420 */         idx = i;
/* 285:    */       }
/* 286:    */     }
/* 287:424 */     maxLabelLen = this.m_labelMetrics.stringWidth(this.m_Instances.attribute(this.m_cIndex).value(idx));
/* 288:    */     int legendHeight;
/* 289:    */     int legendHeight;
/* 290:427 */     if ((w - 2 * this.m_HorizontalPad) / (maxLabelLen + 5) >= numClasses) {
/* 291:428 */       legendHeight = 1;
/* 292:    */     } else {
/* 293:430 */       legendHeight = 2;
/* 294:    */     }
/* 295:433 */     int x = this.m_HorizontalPad;
/* 296:434 */     int y = 1 + hf;
/* 297:    */     
/* 298:436 */     int numToDo = legendHeight == 1 ? numClasses : numClasses / 2;
/* 299:437 */     for (int i = 0; i < numToDo; i++)
/* 300:    */     {
/* 301:439 */       gx.setColor((Color)this.m_colorList.get(i));
/* 302:441 */       if (numToDo * maxLabelLen > w - this.m_HorizontalPad * 2)
/* 303:    */       {
/* 304:443 */         String val = this.m_Instances.attribute(this.m_cIndex).value(i);
/* 305:    */         
/* 306:445 */         int sw = this.m_labelMetrics.stringWidth(val);
/* 307:446 */         int rm = 0;
/* 308:448 */         if (sw > (w - this.m_HorizontalPad * 2) / numToDo)
/* 309:    */         {
/* 310:449 */           int incr = sw / val.length();
/* 311:450 */           rm = (sw - (w - this.m_HorizontalPad * 2) / numToDo) / incr;
/* 312:451 */           if (rm <= 0) {
/* 313:452 */             rm = 0;
/* 314:    */           }
/* 315:454 */           if (rm >= val.length()) {
/* 316:455 */             rm = val.length() - 1;
/* 317:    */           }
/* 318:457 */           val = val.substring(0, val.length() - rm);
/* 319:458 */           sw = this.m_labelMetrics.stringWidth(val);
/* 320:    */         }
/* 321:460 */         NomLabel jj = new NomLabel(val, i);
/* 322:461 */         jj.setFont(gx.getFont());
/* 323:    */         
/* 324:463 */         jj.setSize(this.m_labelMetrics.stringWidth(jj.getText()), this.m_labelMetrics.getAscent() + 4);
/* 325:    */         
/* 326:465 */         add(jj);
/* 327:466 */         jj.setLocation(x, y);
/* 328:467 */         jj.setForeground((Color)this.m_colorList.get(i % this.m_colorList.size()));
/* 329:    */         
/* 330:469 */         x += sw + 2;
/* 331:    */       }
/* 332:    */       else
/* 333:    */       {
/* 334:473 */         NomLabel jj = new NomLabel(this.m_Instances.attribute(this.m_cIndex).value(i), i);
/* 335:    */         
/* 336:475 */         jj.setFont(gx.getFont());
/* 337:    */         
/* 338:477 */         jj.setSize(this.m_labelMetrics.stringWidth(jj.getText()), this.m_labelMetrics.getAscent() + 4);
/* 339:    */         
/* 340:479 */         add(jj);
/* 341:480 */         jj.setLocation(x, y);
/* 342:481 */         jj.setForeground((Color)this.m_colorList.get(i % this.m_colorList.size()));
/* 343:    */         
/* 344:483 */         x += (w - this.m_HorizontalPad * 2) / numToDo;
/* 345:    */       }
/* 346:    */     }
/* 347:487 */     x = this.m_HorizontalPad;
/* 348:488 */     y = 1 + hf + 5 + hf;
/* 349:489 */     for (int i = numToDo; i < numClasses; i++)
/* 350:    */     {
/* 351:491 */       gx.setColor((Color)this.m_colorList.get(i));
/* 352:492 */       if ((numClasses - numToDo + 1) * maxLabelLen > w - this.m_HorizontalPad * 2)
/* 353:    */       {
/* 354:494 */         String val = this.m_Instances.attribute(this.m_cIndex).value(i);
/* 355:    */         
/* 356:496 */         int sw = this.m_labelMetrics.stringWidth(val);
/* 357:497 */         int rm = 0;
/* 358:499 */         if (sw > (w - this.m_HorizontalPad * 2) / (numClasses - numToDo + 1))
/* 359:    */         {
/* 360:500 */           int incr = sw / val.length();
/* 361:501 */           rm = (sw - (w - this.m_HorizontalPad * 2) / (numClasses - numToDo)) / incr;
/* 362:503 */           if (rm <= 0) {
/* 363:504 */             rm = 0;
/* 364:    */           }
/* 365:506 */           if (rm >= val.length()) {
/* 366:507 */             rm = val.length() - 1;
/* 367:    */           }
/* 368:509 */           val = val.substring(0, val.length() - rm);
/* 369:510 */           sw = this.m_labelMetrics.stringWidth(val);
/* 370:    */         }
/* 371:513 */         NomLabel jj = new NomLabel(val, i);
/* 372:514 */         jj.setFont(gx.getFont());
/* 373:    */         
/* 374:516 */         jj.setSize(this.m_labelMetrics.stringWidth(jj.getText()), this.m_labelMetrics.getAscent() + 4);
/* 375:    */         
/* 376:    */ 
/* 377:519 */         add(jj);
/* 378:520 */         jj.setLocation(x, y);
/* 379:521 */         jj.setForeground((Color)this.m_colorList.get(i % this.m_colorList.size()));
/* 380:    */         
/* 381:523 */         x += sw + 2;
/* 382:    */       }
/* 383:    */       else
/* 384:    */       {
/* 385:527 */         NomLabel jj = new NomLabel(this.m_Instances.attribute(this.m_cIndex).value(i), i);
/* 386:    */         
/* 387:529 */         jj.setFont(gx.getFont());
/* 388:    */         
/* 389:531 */         jj.setSize(this.m_labelMetrics.stringWidth(jj.getText()), this.m_labelMetrics.getAscent() + 4);
/* 390:    */         
/* 391:533 */         add(jj);
/* 392:534 */         jj.setLocation(x, y);
/* 393:535 */         jj.setForeground((Color)this.m_colorList.get(i % this.m_colorList.size()));
/* 394:    */         
/* 395:537 */         x += (w - this.m_HorizontalPad * 2) / (numClasses - numToDo);
/* 396:    */       }
/* 397:    */     }
/* 398:    */   }
/* 399:    */   
/* 400:    */   protected void paintNumeric(Graphics gx)
/* 401:    */   {
/* 402:550 */     setFonts(gx);
/* 403:551 */     if (this.m_HorizontalPad == 0) {
/* 404:552 */       setCindex(this.m_cIndex);
/* 405:    */     }
/* 406:555 */     int w = getWidth();
/* 407:556 */     double rs = 15.0D;
/* 408:557 */     double incr = 240.0D / (w - this.m_HorizontalPad * 2);
/* 409:558 */     int hf = this.m_labelMetrics.getAscent();
/* 410:560 */     for (int i = this.m_HorizontalPad; i < w - this.m_HorizontalPad; i++)
/* 411:    */     {
/* 412:561 */       Color c = new Color((int)rs, 150, (int)(255.0D - rs));
/* 413:562 */       gx.setColor(c);
/* 414:563 */       gx.drawLine(i, 0, i, 5);
/* 415:564 */       rs += incr;
/* 416:    */     }
/* 417:567 */     int whole = (int)Math.abs(this.m_maxC);
/* 418:568 */     double decimal = Math.abs(this.m_maxC) - whole;
/* 419:    */     
/* 420:570 */     int nondecimal = whole > 0 ? (int)(Math.log(whole) / Math.log(10.0D)) : 1;
/* 421:    */     
/* 422:572 */     this.m_precisionC = (decimal > 0.0D ? (int)Math.abs(Math.log(Math.abs(this.m_maxC)) / Math.log(10.0D)) + 2 : 1);
/* 423:574 */     if (this.m_precisionC > VisualizeUtils.MAX_PRECISION) {
/* 424:575 */       this.m_precisionC = 1;
/* 425:    */     }
/* 426:578 */     String maxStringC = Utils.doubleToString(this.m_maxC, nondecimal + 1 + this.m_precisionC, this.m_precisionC);
/* 427:    */     
/* 428:    */ 
/* 429:581 */     int mswc = this.m_labelMetrics.stringWidth(maxStringC);
/* 430:582 */     int tmsc = mswc;
/* 431:583 */     if (w > 2 * tmsc)
/* 432:    */     {
/* 433:584 */       gx.setColor(Color.black);
/* 434:585 */       gx.drawLine(this.m_HorizontalPad, 10, w - this.m_HorizontalPad, 10);
/* 435:    */       
/* 436:    */ 
/* 437:588 */       gx.drawLine(w - this.m_HorizontalPad, 10, w - this.m_HorizontalPad, 15);
/* 438:    */       
/* 439:    */ 
/* 440:591 */       gx.drawString(maxStringC, w - this.m_HorizontalPad - mswc / 2, 15 + hf);
/* 441:    */       
/* 442:    */ 
/* 443:594 */       gx.drawLine(this.m_HorizontalPad, 10, this.m_HorizontalPad, 15);
/* 444:    */       
/* 445:    */ 
/* 446:597 */       whole = (int)Math.abs(this.m_minC);
/* 447:598 */       decimal = Math.abs(this.m_minC) - whole;
/* 448:599 */       nondecimal = whole > 0 ? (int)(Math.log(whole) / Math.log(10.0D)) : 1;
/* 449:    */       
/* 450:601 */       this.m_precisionC = (decimal > 0.0D ? (int)Math.abs(Math.log(Math.abs(this.m_minC)) / Math.log(10.0D)) + 2 : 1);
/* 451:604 */       if (this.m_precisionC > VisualizeUtils.MAX_PRECISION) {
/* 452:605 */         this.m_precisionC = 1;
/* 453:    */       }
/* 454:608 */       maxStringC = Utils.doubleToString(this.m_minC, nondecimal + 1 + this.m_precisionC, this.m_precisionC);
/* 455:    */       
/* 456:    */ 
/* 457:611 */       mswc = this.m_labelMetrics.stringWidth(maxStringC);
/* 458:612 */       gx.drawString(maxStringC, this.m_HorizontalPad - mswc / 2, 15 + hf);
/* 459:616 */       if (w > 3 * tmsc)
/* 460:    */       {
/* 461:617 */         double mid = this.m_minC + (this.m_maxC - this.m_minC) / 2.0D;
/* 462:618 */         gx.drawLine(this.m_HorizontalPad + (w - 2 * this.m_HorizontalPad) / 2, 10, this.m_HorizontalPad + (w - 2 * this.m_HorizontalPad) / 2, 15);
/* 463:    */         
/* 464:    */ 
/* 465:    */ 
/* 466:    */ 
/* 467:623 */         whole = (int)Math.abs(mid);
/* 468:624 */         decimal = Math.abs(mid) - whole;
/* 469:625 */         nondecimal = whole > 0 ? (int)(Math.log(whole) / Math.log(10.0D)) : 1;
/* 470:    */         
/* 471:627 */         this.m_precisionC = (decimal > 0.0D ? (int)Math.abs(Math.log(Math.abs(mid)) / Math.log(10.0D)) + 2 : 1);
/* 472:629 */         if (this.m_precisionC > VisualizeUtils.MAX_PRECISION) {
/* 473:630 */           this.m_precisionC = 1;
/* 474:    */         }
/* 475:633 */         maxStringC = Utils.doubleToString(mid, nondecimal + 1 + this.m_precisionC, this.m_precisionC);
/* 476:    */         
/* 477:    */ 
/* 478:636 */         mswc = this.m_labelMetrics.stringWidth(maxStringC);
/* 479:637 */         gx.drawString(maxStringC, this.m_HorizontalPad + (w - 2 * this.m_HorizontalPad) / 2 - mswc / 2, 15 + hf);
/* 480:    */       }
/* 481:    */     }
/* 482:    */   }
/* 483:    */   
/* 484:    */   public void paintComponent(Graphics gx)
/* 485:    */   {
/* 486:651 */     super.paintComponent(gx);
/* 487:652 */     if (this.m_isEnabled) {
/* 488:653 */       if (this.m_isNumeric)
/* 489:    */       {
/* 490:654 */         this.m_oldWidth = -9000;
/* 491:    */         
/* 492:656 */         removeAll();
/* 493:657 */         paintNumeric(gx);
/* 494:    */       }
/* 495:659 */       else if ((this.m_Instances != null) && (this.m_Instances.numInstances() > 0) && (this.m_Instances.numAttributes() > 0))
/* 496:    */       {
/* 497:661 */         if (this.m_oldWidth != getWidth())
/* 498:    */         {
/* 499:662 */           removeAll();
/* 500:663 */           this.m_oldWidth = getWidth();
/* 501:664 */           paintNominal(gx);
/* 502:    */         }
/* 503:    */       }
/* 504:    */     }
/* 505:    */   }
/* 506:    */   
/* 507:    */   public static void main(String[] args)
/* 508:    */   {
/* 509:    */     try
/* 510:    */     {
/* 511:679 */       if (args.length < 1)
/* 512:    */       {
/* 513:680 */         System.err.println("Usage : weka.gui.visualize.ClassPanel <dataset> [class col]");
/* 514:    */         
/* 515:682 */         System.exit(1);
/* 516:    */       }
/* 517:684 */       JFrame jf = new JFrame("Weka Explorer: Class");
/* 518:    */       
/* 519:686 */       jf.setSize(500, 100);
/* 520:687 */       jf.getContentPane().setLayout(new BorderLayout());
/* 521:688 */       ClassPanel p2 = new ClassPanel();
/* 522:689 */       jf.getContentPane().add(p2, "Center");
/* 523:690 */       jf.addWindowListener(new WindowAdapter()
/* 524:    */       {
/* 525:    */         public void windowClosing(WindowEvent e)
/* 526:    */         {
/* 527:693 */           this.val$jf.dispose();
/* 528:694 */           System.exit(0);
/* 529:    */         }
/* 530:    */       });
/* 531:698 */       if (args.length >= 1)
/* 532:    */       {
/* 533:699 */         System.err.println("Loading instances from " + args[0]);
/* 534:700 */         Reader r = new BufferedReader(new FileReader(args[0]));
/* 535:    */         
/* 536:702 */         Instances i = new Instances(r);
/* 537:703 */         i.setClassIndex(i.numAttributes() - 1);
/* 538:704 */         p2.setInstances(i);
/* 539:    */       }
/* 540:706 */       if (args.length > 1) {
/* 541:707 */         p2.setCindex(Integer.parseInt(args[1]) - 1);
/* 542:    */       } else {
/* 543:709 */         p2.setCindex(0);
/* 544:    */       }
/* 545:711 */       jf.setVisible(true);
/* 546:    */     }
/* 547:    */     catch (Exception ex)
/* 548:    */     {
/* 549:713 */       ex.printStackTrace();
/* 550:714 */       System.err.println(ex.getMessage());
/* 551:    */     }
/* 552:    */   }
/* 553:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.ClassPanel
 * JD-Core Version:    0.7.0.1
 */