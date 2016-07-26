/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Container;
/*   6:    */ import java.awt.Dimension;
/*   7:    */ import java.awt.FlowLayout;
/*   8:    */ import java.awt.GraphicsConfiguration;
/*   9:    */ import java.awt.GridLayout;
/*  10:    */ import java.awt.Point;
/*  11:    */ import java.awt.Rectangle;
/*  12:    */ import java.awt.event.ActionEvent;
/*  13:    */ import java.awt.event.ActionListener;
/*  14:    */ import java.awt.event.ItemEvent;
/*  15:    */ import java.awt.event.ItemListener;
/*  16:    */ import java.io.PrintStream;
/*  17:    */ import java.util.Date;
/*  18:    */ import javax.swing.JButton;
/*  19:    */ import javax.swing.JCheckBox;
/*  20:    */ import javax.swing.JFrame;
/*  21:    */ import javax.swing.JLabel;
/*  22:    */ import javax.swing.JPanel;
/*  23:    */ import javax.swing.JScrollPane;
/*  24:    */ import javax.swing.JSpinner;
/*  25:    */ import javax.swing.JTextPane;
/*  26:    */ import javax.swing.SpinnerNumberModel;
/*  27:    */ import javax.swing.event.CaretEvent;
/*  28:    */ import javax.swing.event.CaretListener;
/*  29:    */ import javax.swing.event.ChangeEvent;
/*  30:    */ import javax.swing.event.ChangeListener;
/*  31:    */ import javax.swing.text.Document;
/*  32:    */ import javax.swing.text.Style;
/*  33:    */ import javax.swing.text.StyleConstants;
/*  34:    */ import javax.swing.text.StyleContext;
/*  35:    */ import javax.swing.text.StyledDocument;
/*  36:    */ import weka.core.Tee;
/*  37:    */ import weka.core.Utils;
/*  38:    */ 
/*  39:    */ public class LogWindow
/*  40:    */   extends JFrame
/*  41:    */   implements CaretListener, ChangeListener
/*  42:    */ {
/*  43:    */   private static final long serialVersionUID = 5650947361381061112L;
/*  44:    */   public static final String STYLE_STDOUT = "stdout";
/*  45:    */   public static final String STYLE_STDERR = "stderr";
/*  46: 75 */   public static final Color COLOR_STDOUT = Color.BLACK;
/*  47: 78 */   public static final Color COLOR_STDERR = Color.RED;
/*  48:    */   public static final boolean DEBUG = false;
/*  49: 84 */   public boolean m_UseWordwrap = true;
/*  50: 87 */   protected JTextPane m_Output = new JTextPane();
/*  51: 90 */   protected JButton m_ButtonClear = new JButton("Clear");
/*  52: 93 */   protected JButton m_ButtonClose = new JButton("Close");
/*  53: 96 */   protected JLabel m_LabelCurrentSize = new JLabel("currently: 0");
/*  54: 99 */   protected JSpinner m_SpinnerMaxSize = new JSpinner();
/*  55:102 */   protected JCheckBox m_CheckBoxWordwrap = new JCheckBox("Use wordwrap");
/*  56:105 */   protected static Tee m_TeeOut = null;
/*  57:108 */   protected static Tee m_TeeErr = null;
/*  58:    */   
/*  59:    */   protected class LogWindowPrintStream
/*  60:    */     extends PrintStream
/*  61:    */   {
/*  62:116 */     protected LogWindow m_Parent = null;
/*  63:119 */     protected String m_Style = null;
/*  64:    */     
/*  65:    */     public LogWindowPrintStream(LogWindow parent, PrintStream stream, String style)
/*  66:    */     {
/*  67:130 */       super();
/*  68:    */       
/*  69:132 */       this.m_Parent = parent;
/*  70:133 */       this.m_Style = style;
/*  71:    */     }
/*  72:    */     
/*  73:    */     public synchronized void flush() {}
/*  74:    */     
/*  75:    */     public synchronized void print(int x)
/*  76:    */     {
/*  77:149 */       print(new Integer(x).toString());
/*  78:    */     }
/*  79:    */     
/*  80:    */     public synchronized void print(boolean x)
/*  81:    */     {
/*  82:157 */       print(new Boolean(x).toString());
/*  83:    */     }
/*  84:    */     
/*  85:    */     public synchronized void print(String x)
/*  86:    */     {
/*  87:166 */       StyledDocument doc = this.m_Parent.m_Output.getStyledDocument();
/*  88:    */       try
/*  89:    */       {
/*  90:170 */         doc.insertString(doc.getLength(), x, doc.getStyle(this.m_Style));
/*  91:    */         
/*  92:    */ 
/*  93:173 */         this.m_Parent.m_Output.setCaretPosition(doc.getLength());
/*  94:    */         
/*  95:    */ 
/*  96:176 */         this.m_Parent.trim();
/*  97:    */       }
/*  98:    */       catch (Exception e)
/*  99:    */       {
/* 100:178 */         e.printStackTrace();
/* 101:    */       }
/* 102:    */     }
/* 103:    */     
/* 104:    */     public synchronized void print(Object x)
/* 105:    */     {
/* 106:192 */       if ((x instanceof Throwable))
/* 107:    */       {
/* 108:193 */         Throwable t = (Throwable)x;
/* 109:194 */         StackTraceElement[] trace = t.getStackTrace();
/* 110:195 */         String line = t.getMessage() + "\n";
/* 111:196 */         for (int i = 0; i < trace.length; i++) {
/* 112:197 */           line = line + "\t" + trace[i].toString() + "\n";
/* 113:    */         }
/* 114:199 */         x = line;
/* 115:    */       }
/* 116:202 */       if (x == null) {
/* 117:203 */         print("null");
/* 118:    */       } else {
/* 119:205 */         print(x.toString());
/* 120:    */       }
/* 121:    */     }
/* 122:    */     
/* 123:    */     public synchronized void println()
/* 124:    */     {
/* 125:214 */       print("\n");
/* 126:    */     }
/* 127:    */     
/* 128:    */     public synchronized void println(int x)
/* 129:    */     {
/* 130:222 */       print(x);
/* 131:223 */       println();
/* 132:    */     }
/* 133:    */     
/* 134:    */     public synchronized void println(boolean x)
/* 135:    */     {
/* 136:231 */       print(x);
/* 137:232 */       println();
/* 138:    */     }
/* 139:    */     
/* 140:    */     public synchronized void println(String x)
/* 141:    */     {
/* 142:240 */       print(x);
/* 143:241 */       println();
/* 144:    */     }
/* 145:    */     
/* 146:    */     public synchronized void println(Object x)
/* 147:    */     {
/* 148:249 */       print(x);
/* 149:250 */       println();
/* 150:    */     }
/* 151:    */   }
/* 152:    */   
/* 153:    */   public LogWindow()
/* 154:    */   {
/* 155:258 */     super("Weka - Log");
/* 156:    */     
/* 157:260 */     createFrame();
/* 158:    */     
/* 159:    */ 
/* 160:    */ 
/* 161:    */ 
/* 162:    */ 
/* 163:    */ 
/* 164:267 */     StyledDocument doc = this.m_Output.getStyledDocument();
/* 165:268 */     Style style = StyleContext.getDefaultStyleContext().getStyle("default");
/* 166:    */     
/* 167:270 */     style = doc.addStyle("stdout", style);
/* 168:271 */     StyleConstants.setFontFamily(style, "monospaced");
/* 169:272 */     StyleConstants.setForeground(style, COLOR_STDOUT);
/* 170:    */     
/* 171:274 */     style = StyleContext.getDefaultStyleContext().getStyle("default");
/* 172:    */     
/* 173:276 */     style = doc.addStyle("stderr", style);
/* 174:277 */     StyleConstants.setFontFamily(style, "monospaced");
/* 175:278 */     StyleConstants.setForeground(style, COLOR_STDERR);
/* 176:    */     
/* 177:    */ 
/* 178:281 */     boolean teeDone = (m_TeeOut != null) || (m_TeeErr != null);
/* 179:283 */     if (!teeDone)
/* 180:    */     {
/* 181:284 */       m_TeeOut = new Tee(System.out);
/* 182:285 */       System.setOut(m_TeeOut);
/* 183:    */     }
/* 184:287 */     m_TeeOut.add(new LogWindowPrintStream(this, m_TeeOut.getDefault(), "stdout"));
/* 185:291 */     if (!teeDone)
/* 186:    */     {
/* 187:292 */       m_TeeErr = new Tee(System.err);
/* 188:293 */       System.setErr(m_TeeErr);
/* 189:    */     }
/* 190:295 */     m_TeeErr.add(new LogWindowPrintStream(this, m_TeeErr.getDefault(), "stderr"));
/* 191:    */   }
/* 192:    */   
/* 193:    */   protected void createFrame()
/* 194:    */   {
/* 195:312 */     setSize(600, 400);
/* 196:313 */     int width = getBounds().width;
/* 197:314 */     setLocation(getGraphicsConfiguration().getBounds().width - width, getLocation().y);
/* 198:    */     
/* 199:316 */     getContentPane().setLayout(new BorderLayout());
/* 200:    */     
/* 201:    */ 
/* 202:319 */     getContentPane().add(new JScrollPane(this.m_Output), "Center");
/* 203:320 */     setWordwrap(this.m_UseWordwrap);
/* 204:    */     
/* 205:    */ 
/* 206:323 */     JPanel panel = new JPanel(new BorderLayout());
/* 207:324 */     getContentPane().add(panel, "South");
/* 208:325 */     JPanel panel3 = new JPanel(new BorderLayout());
/* 209:326 */     panel.add(panel3, "South");
/* 210:327 */     JPanel panel2 = new JPanel(new FlowLayout(2));
/* 211:328 */     panel3.add(panel2, "East");
/* 212:    */     
/* 213:330 */     this.m_ButtonClear.setMnemonic('C');
/* 214:331 */     this.m_ButtonClear.addActionListener(new ActionListener()
/* 215:    */     {
/* 216:    */       public void actionPerformed(ActionEvent e)
/* 217:    */       {
/* 218:334 */         LogWindow.this.clear();
/* 219:    */       }
/* 220:336 */     });
/* 221:337 */     panel2.add(this.m_ButtonClear);
/* 222:    */     
/* 223:339 */     this.m_ButtonClose.setMnemonic('l');
/* 224:340 */     this.m_ButtonClose.addActionListener(new ActionListener()
/* 225:    */     {
/* 226:    */       public void actionPerformed(ActionEvent e)
/* 227:    */       {
/* 228:343 */         LogWindow.this.close();
/* 229:    */       }
/* 230:345 */     });
/* 231:346 */     panel2.add(this.m_ButtonClose);
/* 232:    */     
/* 233:    */ 
/* 234:349 */     panel2 = new JPanel(new GridLayout(1, 3));
/* 235:350 */     panel3.add(panel2, "West");
/* 236:    */     
/* 237:    */ 
/* 238:353 */     JPanel panel4 = new JPanel(new FlowLayout());
/* 239:354 */     panel2.add(panel4);
/* 240:355 */     SpinnerNumberModel model = (SpinnerNumberModel)this.m_SpinnerMaxSize.getModel();
/* 241:356 */     model.setMinimum(new Integer(1));
/* 242:357 */     model.setStepSize(new Integer(1000));
/* 243:358 */     model.setValue(new Integer(100000));
/* 244:359 */     model.addChangeListener(this);
/* 245:    */     
/* 246:361 */     JLabel label = new JLabel("max. Size");
/* 247:362 */     label.setDisplayedMnemonic('m');
/* 248:363 */     label.setLabelFor(this.m_SpinnerMaxSize);
/* 249:    */     
/* 250:365 */     panel4.add(label);
/* 251:366 */     panel4.add(this.m_SpinnerMaxSize);
/* 252:    */     
/* 253:    */ 
/* 254:369 */     panel4 = new JPanel(new FlowLayout());
/* 255:370 */     panel2.add(panel4);
/* 256:371 */     panel4.add(this.m_LabelCurrentSize);
/* 257:    */     
/* 258:    */ 
/* 259:374 */     panel4 = new JPanel(new FlowLayout());
/* 260:375 */     panel2.add(panel4);
/* 261:376 */     this.m_CheckBoxWordwrap.setSelected(this.m_UseWordwrap);
/* 262:377 */     this.m_CheckBoxWordwrap.addItemListener(new ItemListener()
/* 263:    */     {
/* 264:    */       public void itemStateChanged(ItemEvent e)
/* 265:    */       {
/* 266:380 */         LogWindow.this.setWordwrap(LogWindow.this.m_CheckBoxWordwrap.isSelected());
/* 267:    */       }
/* 268:382 */     });
/* 269:383 */     panel4.add(this.m_CheckBoxWordwrap);
/* 270:    */   }
/* 271:    */   
/* 272:    */   public void clear()
/* 273:    */   {
/* 274:390 */     this.m_Output.setText("");
/* 275:    */   }
/* 276:    */   
/* 277:    */   public void close()
/* 278:    */   {
/* 279:397 */     setVisible(false);
/* 280:    */   }
/* 281:    */   
/* 282:    */   public void trim()
/* 283:    */   {
/* 284:409 */     StyledDocument doc = this.m_Output.getStyledDocument();
/* 285:    */     
/* 286:    */ 
/* 287:412 */     int size = doc.getLength();
/* 288:413 */     int maxSize = ((Integer)this.m_SpinnerMaxSize.getValue()).intValue();
/* 289:414 */     if (size > maxSize) {
/* 290:    */       try
/* 291:    */       {
/* 292:417 */         int pos = size - maxSize;
/* 293:418 */         while (!doc.getText(pos, 1).equals("\n")) {
/* 294:419 */           pos++;
/* 295:    */         }
/* 296:421 */         while (doc.getText(pos, 1).equals("\n")) {
/* 297:422 */           pos++;
/* 298:    */         }
/* 299:425 */         doc.remove(0, pos);
/* 300:    */       }
/* 301:    */       catch (Exception ex) {}
/* 302:    */     }
/* 303:435 */     this.m_Output.setCaretPosition(doc.getLength());
/* 304:    */   }
/* 305:    */   
/* 306:    */   protected String colorToString(Color c)
/* 307:    */   {
/* 308:444 */     String result = "#" + Utils.padLeft(Integer.toHexString(c.getRed()), 2) + Utils.padLeft(Integer.toHexString(c.getGreen()), 2) + Utils.padLeft(Integer.toHexString(c.getBlue()), 2);
/* 309:    */     
/* 310:    */ 
/* 311:    */ 
/* 312:448 */     result = result.replaceAll("\\ ", "0").toUpperCase();
/* 313:    */     
/* 314:450 */     return result;
/* 315:    */   }
/* 316:    */   
/* 317:    */   public void setWordwrap(boolean wrap)
/* 318:    */   {
/* 319:462 */     this.m_UseWordwrap = wrap;
/* 320:463 */     if (this.m_CheckBoxWordwrap.isSelected() != this.m_UseWordwrap) {
/* 321:464 */       this.m_CheckBoxWordwrap.setSelected(this.m_UseWordwrap);
/* 322:    */     }
/* 323:468 */     Container parent = this.m_Output.getParent();
/* 324:469 */     JTextPane outputOld = this.m_Output;
/* 325:470 */     if (this.m_UseWordwrap) {
/* 326:471 */       this.m_Output = new JTextPane();
/* 327:    */     } else {
/* 328:473 */       this.m_Output = new JTextPane()
/* 329:    */       {
/* 330:    */         private static final long serialVersionUID = -8275856175921425981L;
/* 331:    */         
/* 332:    */         public void setSize(Dimension d)
/* 333:    */         {
/* 334:478 */           if (d.width < getGraphicsConfiguration().getBounds().width) {
/* 335:479 */             d.width = getGraphicsConfiguration().getBounds().width;
/* 336:    */           }
/* 337:481 */           super.setSize(d);
/* 338:    */         }
/* 339:    */         
/* 340:    */         public boolean getScrollableTracksViewportWidth()
/* 341:    */         {
/* 342:486 */           return false;
/* 343:    */         }
/* 344:    */       };
/* 345:    */     }
/* 346:490 */     this.m_Output.setEditable(false);
/* 347:491 */     this.m_Output.addCaretListener(this);
/* 348:492 */     this.m_Output.setDocument(outputOld.getDocument());
/* 349:493 */     this.m_Output.setCaretPosition(this.m_Output.getDocument().getLength());
/* 350:    */     
/* 351:    */ 
/* 352:    */ 
/* 353:497 */     parent.add(this.m_Output);
/* 354:498 */     parent.remove(outputOld);
/* 355:    */   }
/* 356:    */   
/* 357:    */   public void caretUpdate(CaretEvent e)
/* 358:    */   {
/* 359:506 */     this.m_LabelCurrentSize.setText("currently: " + this.m_Output.getStyledDocument().getLength());
/* 360:    */   }
/* 361:    */   
/* 362:    */   public void stateChanged(ChangeEvent e)
/* 363:    */   {
/* 364:520 */     if (e.getSource() == this.m_SpinnerMaxSize.getModel())
/* 365:    */     {
/* 366:521 */       trim();
/* 367:522 */       validate();
/* 368:523 */       caretUpdate(null);
/* 369:    */     }
/* 370:    */   }
/* 371:    */   
/* 372:    */   public static void main(String[] args)
/* 373:    */   {
/* 374:533 */     LookAndFeel.setLookAndFeel();
/* 375:    */     
/* 376:535 */     LogWindow log = new LogWindow();
/* 377:536 */     log.setVisible(true);
/* 378:537 */     log.setDefaultCloseOperation(2);
/* 379:    */     
/* 380:    */ 
/* 381:540 */     System.out.print("a");
/* 382:541 */     System.err.print("a");
/* 383:542 */     System.out.print("a");
/* 384:543 */     System.out.println();
/* 385:544 */     System.err.println(new Date());
/* 386:    */   }
/* 387:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.LogWindow
 * JD-Core Version:    0.7.0.1
 */