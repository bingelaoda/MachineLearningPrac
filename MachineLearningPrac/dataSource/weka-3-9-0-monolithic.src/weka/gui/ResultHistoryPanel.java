/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Container;
/*   6:    */ import java.awt.Dimension;
/*   7:    */ import java.awt.Font;
/*   8:    */ import java.awt.Point;
/*   9:    */ import java.awt.event.KeyAdapter;
/*  10:    */ import java.awt.event.KeyEvent;
/*  11:    */ import java.awt.event.MouseAdapter;
/*  12:    */ import java.awt.event.MouseEvent;
/*  13:    */ import java.awt.event.WindowAdapter;
/*  14:    */ import java.awt.event.WindowEvent;
/*  15:    */ import java.io.PrintStream;
/*  16:    */ import java.io.Serializable;
/*  17:    */ import java.util.Hashtable;
/*  18:    */ import javax.swing.BorderFactory;
/*  19:    */ import javax.swing.DefaultListModel;
/*  20:    */ import javax.swing.JFrame;
/*  21:    */ import javax.swing.JList;
/*  22:    */ import javax.swing.JPanel;
/*  23:    */ import javax.swing.JScrollPane;
/*  24:    */ import javax.swing.JTextArea;
/*  25:    */ import javax.swing.JViewport;
/*  26:    */ import javax.swing.ListSelectionModel;
/*  27:    */ import javax.swing.event.ChangeEvent;
/*  28:    */ import javax.swing.event.ChangeListener;
/*  29:    */ import javax.swing.event.ListSelectionEvent;
/*  30:    */ import javax.swing.event.ListSelectionListener;
/*  31:    */ import javax.swing.text.JTextComponent;
/*  32:    */ import weka.gui.visualize.PrintableComponent;
/*  33:    */ 
/*  34:    */ public class ResultHistoryPanel
/*  35:    */   extends JPanel
/*  36:    */ {
/*  37:    */   static final long serialVersionUID = 4297069440135326829L;
/*  38:    */   protected JTextComponent m_SingleText;
/*  39:    */   protected String m_SingleName;
/*  40: 76 */   protected DefaultListModel m_Model = new DefaultListModel();
/*  41: 79 */   protected JList m_List = new JList(this.m_Model);
/*  42: 82 */   protected Hashtable<String, StringBuffer> m_Results = new Hashtable();
/*  43: 86 */   protected Hashtable<String, JTextArea> m_FramedOutput = new Hashtable();
/*  44: 90 */   protected Hashtable<String, Object> m_Objs = new Hashtable();
/*  45: 96 */   protected boolean m_HandleRightClicks = true;
/*  46: 99 */   protected PrintableComponent m_Printer = null;
/*  47:    */   
/*  48:    */   public ResultHistoryPanel(JTextComponent text)
/*  49:    */   {
/*  50:126 */     this.m_SingleText = text;
/*  51:127 */     if (text != null) {
/*  52:128 */       this.m_Printer = new PrintableComponent(this.m_SingleText);
/*  53:    */     }
/*  54:130 */     this.m_List.setSelectionMode(0);
/*  55:131 */     this.m_List.addMouseListener(new RMouseAdapter()
/*  56:    */     {
/*  57:    */       private static final long serialVersionUID = -9015397020486290479L;
/*  58:    */       
/*  59:    */       public void mouseClicked(MouseEvent e)
/*  60:    */       {
/*  61:136 */         if ((e.getModifiers() & 0x10) == 16)
/*  62:    */         {
/*  63:137 */           if (((e.getModifiers() & 0x40) == 0) && ((e.getModifiers() & 0x80) == 0))
/*  64:    */           {
/*  65:139 */             int index = ResultHistoryPanel.this.m_List.locationToIndex(e.getPoint());
/*  66:140 */             if ((index != -1) && (ResultHistoryPanel.this.m_SingleText != null)) {
/*  67:141 */               ResultHistoryPanel.this.setSingle((String)ResultHistoryPanel.this.m_Model.elementAt(index));
/*  68:    */             }
/*  69:    */           }
/*  70:    */         }
/*  71:147 */         else if (ResultHistoryPanel.this.m_HandleRightClicks)
/*  72:    */         {
/*  73:148 */           int index = ResultHistoryPanel.this.m_List.locationToIndex(e.getPoint());
/*  74:149 */           if (index != -1)
/*  75:    */           {
/*  76:150 */             String name = (String)ResultHistoryPanel.this.m_Model.elementAt(index);
/*  77:151 */             ResultHistoryPanel.this.openFrame(name);
/*  78:    */           }
/*  79:    */         }
/*  80:    */       }
/*  81:157 */     });
/*  82:158 */     this.m_List.addKeyListener(new RKeyAdapter()
/*  83:    */     {
/*  84:    */       private static final long serialVersionUID = 7910681776999302344L;
/*  85:    */       
/*  86:    */       public void keyReleased(KeyEvent e)
/*  87:    */       {
/*  88:163 */         if (e.getKeyCode() == 127)
/*  89:    */         {
/*  90:164 */           int selected = ResultHistoryPanel.this.m_List.getSelectedIndex();
/*  91:165 */           if (selected != -1) {
/*  92:166 */             ResultHistoryPanel.this.removeResult((String)ResultHistoryPanel.this.m_Model.elementAt(selected));
/*  93:    */           }
/*  94:    */         }
/*  95:    */       }
/*  96:170 */     });
/*  97:171 */     this.m_List.getSelectionModel().addListSelectionListener(new ListSelectionListener()
/*  98:    */     {
/*  99:    */       public void valueChanged(ListSelectionEvent e)
/* 100:    */       {
/* 101:175 */         if (!e.getValueIsAdjusting())
/* 102:    */         {
/* 103:176 */           ListSelectionModel lm = (ListSelectionModel)e.getSource();
/* 104:177 */           for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
/* 105:178 */             if (lm.isSelectedIndex(i))
/* 106:    */             {
/* 107:180 */               if ((i == -1) || (ResultHistoryPanel.this.m_SingleText == null)) {
/* 108:    */                 break;
/* 109:    */               }
/* 110:181 */               ResultHistoryPanel.this.setSingle((String)ResultHistoryPanel.this.m_Model.elementAt(i)); break;
/* 111:    */             }
/* 112:    */           }
/* 113:    */         }
/* 114:    */       }
/* 115:189 */     });
/* 116:190 */     setLayout(new BorderLayout());
/* 117:    */     
/* 118:192 */     JScrollPane js = new JScrollPane(this.m_List);
/* 119:193 */     js.getViewport().addChangeListener(new ChangeListener()
/* 120:    */     {
/* 121:    */       private int lastHeight;
/* 122:    */       
/* 123:    */       public void stateChanged(ChangeEvent e)
/* 124:    */       {
/* 125:198 */         JViewport vp = (JViewport)e.getSource();
/* 126:199 */         int h = vp.getViewSize().height;
/* 127:200 */         if (h != this.lastHeight)
/* 128:    */         {
/* 129:201 */           this.lastHeight = h;
/* 130:202 */           int x = h - vp.getExtentSize().height;
/* 131:203 */           vp.setViewPosition(new Point(0, x));
/* 132:    */         }
/* 133:    */       }
/* 134:206 */     });
/* 135:207 */     add(js, "Center");
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void addResult(String name, StringBuffer result)
/* 139:    */   {
/* 140:218 */     this.m_Model.addElement(name);
/* 141:219 */     this.m_Results.put(name, result);
/* 142:    */   }
/* 143:    */   
/* 144:    */   public void removeResult(String name)
/* 145:    */   {
/* 146:230 */     StringBuffer buff = (StringBuffer)this.m_Results.get(name);
/* 147:231 */     if (buff != null)
/* 148:    */     {
/* 149:232 */       this.m_Results.remove(name);
/* 150:233 */       this.m_Model.removeElement(name);
/* 151:234 */       this.m_Objs.remove(name);
/* 152:235 */       System.gc();
/* 153:    */     }
/* 154:    */   }
/* 155:    */   
/* 156:    */   public void clearResults()
/* 157:    */   {
/* 158:244 */     this.m_Results.clear();
/* 159:245 */     this.m_Model.clear();
/* 160:246 */     this.m_Objs.clear();
/* 161:247 */     System.gc();
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void addObject(String name, Object o)
/* 165:    */   {
/* 166:257 */     this.m_Objs.put(name, o);
/* 167:    */   }
/* 168:    */   
/* 169:    */   public Object getNamedObject(String name)
/* 170:    */   {
/* 171:267 */     Object v = null;
/* 172:268 */     v = this.m_Objs.get(name);
/* 173:269 */     return v;
/* 174:    */   }
/* 175:    */   
/* 176:    */   public Object getSelectedObject()
/* 177:    */   {
/* 178:279 */     Object v = null;
/* 179:280 */     int index = this.m_List.getSelectedIndex();
/* 180:281 */     if (index != -1)
/* 181:    */     {
/* 182:282 */       String name = (String)this.m_Model.elementAt(index);
/* 183:283 */       v = this.m_Objs.get(name);
/* 184:    */     }
/* 185:286 */     return v;
/* 186:    */   }
/* 187:    */   
/* 188:    */   public StringBuffer getNamedBuffer(String name)
/* 189:    */   {
/* 190:295 */     StringBuffer b = null;
/* 191:296 */     b = (StringBuffer)this.m_Results.get(name);
/* 192:297 */     return b;
/* 193:    */   }
/* 194:    */   
/* 195:    */   public StringBuffer getSelectedBuffer()
/* 196:    */   {
/* 197:306 */     StringBuffer b = null;
/* 198:307 */     int index = this.m_List.getSelectedIndex();
/* 199:308 */     if (index != -1)
/* 200:    */     {
/* 201:309 */       String name = (String)this.m_Model.elementAt(index);
/* 202:310 */       b = (StringBuffer)this.m_Results.get(name);
/* 203:    */     }
/* 204:312 */     return b;
/* 205:    */   }
/* 206:    */   
/* 207:    */   public String getSelectedName()
/* 208:    */   {
/* 209:321 */     int index = this.m_List.getSelectedIndex();
/* 210:322 */     if (index != -1) {
/* 211:323 */       return (String)this.m_Model.elementAt(index);
/* 212:    */     }
/* 213:325 */     return null;
/* 214:    */   }
/* 215:    */   
/* 216:    */   public String getNameAtIndex(int index)
/* 217:    */   {
/* 218:334 */     if (index != -1) {
/* 219:335 */       return (String)this.m_Model.elementAt(index);
/* 220:    */     }
/* 221:337 */     return null;
/* 222:    */   }
/* 223:    */   
/* 224:    */   public void setSingle(String name)
/* 225:    */   {
/* 226:347 */     StringBuffer buff = (StringBuffer)this.m_Results.get(name);
/* 227:348 */     if (buff != null)
/* 228:    */     {
/* 229:349 */       this.m_SingleName = name;
/* 230:350 */       this.m_SingleText.setText(buff.toString());
/* 231:351 */       this.m_List.setSelectedValue(name, true);
/* 232:    */     }
/* 233:    */   }
/* 234:    */   
/* 235:    */   public void setSelectedListValue(String name)
/* 236:    */   {
/* 237:363 */     this.m_List.setSelectedValue(name, true);
/* 238:    */   }
/* 239:    */   
/* 240:    */   public void openFrame(String name)
/* 241:    */   {
/* 242:373 */     StringBuffer buff = (StringBuffer)this.m_Results.get(name);
/* 243:374 */     JTextComponent currentText = (JTextComponent)this.m_FramedOutput.get(name);
/* 244:375 */     if ((buff != null) && (currentText == null))
/* 245:    */     {
/* 246:377 */       JTextArea ta = new JTextArea();
/* 247:378 */       ta.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/* 248:379 */       ta.setFont(new Font("Monospaced", 0, 12));
/* 249:380 */       ta.setEditable(false);
/* 250:381 */       ta.setText(buff.toString());
/* 251:382 */       this.m_FramedOutput.put(name, ta);
/* 252:383 */       final JFrame jf = new JFrame(name);
/* 253:384 */       jf.addWindowListener(new WindowAdapter()
/* 254:    */       {
/* 255:    */         public void windowClosing(WindowEvent e)
/* 256:    */         {
/* 257:387 */           ResultHistoryPanel.this.m_FramedOutput.remove(jf.getTitle());
/* 258:388 */           jf.dispose();
/* 259:    */         }
/* 260:390 */       });
/* 261:391 */       jf.getContentPane().setLayout(new BorderLayout());
/* 262:392 */       jf.getContentPane().add(new JScrollPane(ta), "Center");
/* 263:393 */       jf.pack();
/* 264:394 */       jf.setSize(450, 350);
/* 265:395 */       jf.setVisible(true);
/* 266:    */     }
/* 267:    */   }
/* 268:    */   
/* 269:    */   public void updateResult(String name)
/* 270:    */   {
/* 271:407 */     StringBuffer buff = (StringBuffer)this.m_Results.get(name);
/* 272:408 */     if (buff == null) {
/* 273:409 */       return;
/* 274:    */     }
/* 275:411 */     if (this.m_SingleName == name) {
/* 276:412 */       this.m_SingleText.setText(buff.toString());
/* 277:    */     }
/* 278:414 */     JTextComponent currentText = (JTextComponent)this.m_FramedOutput.get(name);
/* 279:415 */     if (currentText != null) {
/* 280:416 */       currentText.setText(buff.toString());
/* 281:    */     }
/* 282:    */   }
/* 283:    */   
/* 284:    */   public ListSelectionModel getSelectionModel()
/* 285:    */   {
/* 286:427 */     return this.m_List.getSelectionModel();
/* 287:    */   }
/* 288:    */   
/* 289:    */   public JList getList()
/* 290:    */   {
/* 291:436 */     return this.m_List;
/* 292:    */   }
/* 293:    */   
/* 294:    */   public void setHandleRightClicks(boolean tf)
/* 295:    */   {
/* 296:446 */     this.m_HandleRightClicks = tf;
/* 297:    */   }
/* 298:    */   
/* 299:    */   public void setBackground(Color c)
/* 300:    */   {
/* 301:456 */     super.setBackground(c);
/* 302:457 */     if (this.m_List != null) {
/* 303:458 */       this.m_List.setBackground(c);
/* 304:    */     }
/* 305:    */   }
/* 306:    */   
/* 307:    */   public void setFont(Font f)
/* 308:    */   {
/* 309:469 */     super.setFont(f);
/* 310:470 */     if (this.m_List != null) {
/* 311:471 */       this.m_List.setFont(f);
/* 312:    */     }
/* 313:    */   }
/* 314:    */   
/* 315:    */   public static void main(String[] args)
/* 316:    */   {
/* 317:    */     try
/* 318:    */     {
/* 319:483 */       JFrame jf = new JFrame("Weka Explorer: Classifier");
/* 320:    */       
/* 321:485 */       jf.getContentPane().setLayout(new BorderLayout());
/* 322:486 */       ResultHistoryPanel jd = new ResultHistoryPanel(null);
/* 323:487 */       jd.addResult("blah", new StringBuffer("Nothing to see here"));
/* 324:488 */       jd.addResult("blah1", new StringBuffer("Nothing to see here1"));
/* 325:489 */       jd.addResult("blah2", new StringBuffer("Nothing to see here2"));
/* 326:490 */       jd.addResult("blah3", new StringBuffer("Nothing to see here3"));
/* 327:491 */       jf.getContentPane().add(jd, "Center");
/* 328:492 */       jf.addWindowListener(new WindowAdapter()
/* 329:    */       {
/* 330:    */         public void windowClosing(WindowEvent e)
/* 331:    */         {
/* 332:495 */           this.val$jf.dispose();
/* 333:496 */           System.exit(0);
/* 334:    */         }
/* 335:498 */       });
/* 336:499 */       jf.pack();
/* 337:500 */       jf.setVisible(true);
/* 338:    */     }
/* 339:    */     catch (Exception ex)
/* 340:    */     {
/* 341:502 */       ex.printStackTrace();
/* 342:503 */       System.err.println(ex.getMessage());
/* 343:    */     }
/* 344:    */   }
/* 345:    */   
/* 346:    */   public static class RKeyAdapter
/* 347:    */     extends KeyAdapter
/* 348:    */     implements Serializable
/* 349:    */   {
/* 350:    */     static final long serialVersionUID = -8675332541861828079L;
/* 351:    */   }
/* 352:    */   
/* 353:    */   public static class RMouseAdapter
/* 354:    */     extends MouseAdapter
/* 355:    */     implements Serializable
/* 356:    */   {
/* 357:    */     static final long serialVersionUID = -8991922650552358669L;
/* 358:    */   }
/* 359:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.ResultHistoryPanel
 * JD-Core Version:    0.7.0.1
 */