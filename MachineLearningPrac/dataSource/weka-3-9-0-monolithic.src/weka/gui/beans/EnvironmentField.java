/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.Container;
/*   6:    */ import java.awt.Dimension;
/*   7:    */ import java.awt.Graphics;
/*   8:    */ import java.awt.Rectangle;
/*   9:    */ import java.awt.event.ActionEvent;
/*  10:    */ import java.awt.event.ActionListener;
/*  11:    */ import java.awt.event.FocusAdapter;
/*  12:    */ import java.awt.event.FocusEvent;
/*  13:    */ import java.awt.event.KeyAdapter;
/*  14:    */ import java.awt.event.KeyEvent;
/*  15:    */ import java.awt.event.WindowAdapter;
/*  16:    */ import java.awt.event.WindowEvent;
/*  17:    */ import java.beans.PropertyChangeListener;
/*  18:    */ import java.beans.PropertyChangeSupport;
/*  19:    */ import java.beans.PropertyEditor;
/*  20:    */ import java.util.Vector;
/*  21:    */ import javax.swing.BorderFactory;
/*  22:    */ import javax.swing.ComboBoxEditor;
/*  23:    */ import javax.swing.ComboBoxModel;
/*  24:    */ import javax.swing.DefaultComboBoxModel;
/*  25:    */ import javax.swing.JComboBox;
/*  26:    */ import javax.swing.JFrame;
/*  27:    */ import javax.swing.JLabel;
/*  28:    */ import javax.swing.JPanel;
/*  29:    */ import javax.swing.JTextField;
/*  30:    */ import javax.swing.event.CaretEvent;
/*  31:    */ import javax.swing.event.CaretListener;
/*  32:    */ import weka.core.Environment;
/*  33:    */ import weka.core.EnvironmentHandler;
/*  34:    */ import weka.gui.CustomPanelSupplier;
/*  35:    */ 
/*  36:    */ @Deprecated
/*  37:    */ public class EnvironmentField
/*  38:    */   extends JPanel
/*  39:    */   implements EnvironmentHandler, PropertyEditor, CustomPanelSupplier
/*  40:    */ {
/*  41:    */   private static final long serialVersionUID = -3125404573324734121L;
/*  42:    */   protected JLabel m_label;
/*  43:    */   protected JComboBox m_combo;
/*  44:    */   protected Environment m_env;
/*  45: 81 */   protected String m_currentContents = "";
/*  46: 82 */   protected int m_firstCaretPos = 0;
/*  47: 83 */   protected int m_previousCaretPos = 0;
/*  48: 84 */   protected int m_currentCaretPos = 0;
/*  49: 86 */   protected PropertyChangeSupport m_support = new PropertyChangeSupport(this);
/*  50:    */   
/*  51:    */   public static class WideComboBox
/*  52:    */     extends JComboBox
/*  53:    */   {
/*  54:    */     private static final long serialVersionUID = -6512065375459733517L;
/*  55:    */     
/*  56:    */     public WideComboBox() {}
/*  57:    */     
/*  58:    */     public WideComboBox(Object[] items)
/*  59:    */     {
/*  60:105 */       super();
/*  61:    */     }
/*  62:    */     
/*  63:    */     public WideComboBox(Vector<Object> items)
/*  64:    */     {
/*  65:109 */       super();
/*  66:    */     }
/*  67:    */     
/*  68:    */     public WideComboBox(ComboBoxModel aModel)
/*  69:    */     {
/*  70:113 */       super();
/*  71:    */     }
/*  72:    */     
/*  73:116 */     private boolean m_layingOut = false;
/*  74:    */     
/*  75:    */     public void doLayout()
/*  76:    */     {
/*  77:    */       try
/*  78:    */       {
/*  79:121 */         this.m_layingOut = true;
/*  80:122 */         super.doLayout();
/*  81:    */       }
/*  82:    */       finally
/*  83:    */       {
/*  84:124 */         this.m_layingOut = false;
/*  85:    */       }
/*  86:    */     }
/*  87:    */     
/*  88:    */     public Dimension getSize()
/*  89:    */     {
/*  90:130 */       Dimension dim = super.getSize();
/*  91:131 */       if (!this.m_layingOut) {
/*  92:132 */         dim.width = Math.max(dim.width, getPreferredSize().width);
/*  93:    */       }
/*  94:134 */       return dim;
/*  95:    */     }
/*  96:    */   }
/*  97:    */   
/*  98:    */   public EnvironmentField()
/*  99:    */   {
/* 100:142 */     this("");
/* 101:143 */     setEnvironment(Environment.getSystemWide());
/* 102:    */   }
/* 103:    */   
/* 104:    */   public EnvironmentField(Environment env)
/* 105:    */   {
/* 106:152 */     this("");
/* 107:153 */     setEnvironment(env);
/* 108:    */   }
/* 109:    */   
/* 110:    */   public EnvironmentField(String label, Environment env)
/* 111:    */   {
/* 112:163 */     this(label);
/* 113:164 */     setEnvironment(env);
/* 114:    */   }
/* 115:    */   
/* 116:    */   public EnvironmentField(String label)
/* 117:    */   {
/* 118:173 */     setLayout(new BorderLayout());
/* 119:174 */     this.m_label = new JLabel(label);
/* 120:175 */     if (label.length() > 0) {
/* 121:176 */       this.m_label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
/* 122:    */     }
/* 123:178 */     add(this.m_label, "West");
/* 124:    */     
/* 125:180 */     this.m_combo = new WideComboBox();
/* 126:181 */     this.m_combo.setEditable(true);
/* 127:    */     
/* 128:    */ 
/* 129:184 */     Component theEditor = this.m_combo.getEditor().getEditorComponent();
/* 130:185 */     if ((theEditor instanceof JTextField))
/* 131:    */     {
/* 132:186 */       ((JTextField)this.m_combo.getEditor().getEditorComponent()).addCaretListener(new CaretListener()
/* 133:    */       {
/* 134:    */         public void caretUpdate(CaretEvent e)
/* 135:    */         {
/* 136:191 */           EnvironmentField.this.m_firstCaretPos = EnvironmentField.this.m_previousCaretPos;
/* 137:192 */           EnvironmentField.this.m_previousCaretPos = EnvironmentField.this.m_currentCaretPos;
/* 138:193 */           EnvironmentField.this.m_currentCaretPos = e.getDot();
/* 139:    */         }
/* 140:196 */       });
/* 141:197 */       this.m_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter()
/* 142:    */       {
/* 143:    */         public void keyReleased(KeyEvent e)
/* 144:    */         {
/* 145:200 */           EnvironmentField.this.m_support.firePropertyChange("", null, null);
/* 146:    */         }
/* 147:203 */       });
/* 148:204 */       ((JTextField)this.m_combo.getEditor().getEditorComponent()).addFocusListener(new FocusAdapter()
/* 149:    */       {
/* 150:    */         public void focusLost(FocusEvent e)
/* 151:    */         {
/* 152:208 */           EnvironmentField.this.m_support.firePropertyChange("", null, null);
/* 153:    */         }
/* 154:    */       });
/* 155:    */     }
/* 156:212 */     add(this.m_combo, "Center");
/* 157:    */     
/* 158:214 */     setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/* 159:    */     
/* 160:216 */     Dimension d = getPreferredSize();
/* 161:217 */     setPreferredSize(new Dimension(250, d.height));
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void setLabel(String label)
/* 165:    */   {
/* 166:226 */     this.m_label.setText(label);
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void setText(String text)
/* 170:    */   {
/* 171:235 */     this.m_currentContents = text;
/* 172:236 */     Component theEditor = this.m_combo.getEditor().getEditorComponent();
/* 173:237 */     if ((theEditor instanceof JTextField)) {
/* 174:238 */       ((JTextField)theEditor).setText(text);
/* 175:    */     } else {
/* 176:240 */       this.m_combo.setSelectedItem(this.m_currentContents);
/* 177:    */     }
/* 178:242 */     this.m_support.firePropertyChange("", null, null);
/* 179:    */   }
/* 180:    */   
/* 181:    */   public String getText()
/* 182:    */   {
/* 183:251 */     Component theEditor = this.m_combo.getEditor().getEditorComponent();
/* 184:252 */     String text = this.m_combo.getSelectedItem().toString();
/* 185:253 */     if ((theEditor instanceof JTextField)) {
/* 186:254 */       text = ((JTextField)theEditor).getText();
/* 187:    */     }
/* 188:256 */     return text;
/* 189:    */   }
/* 190:    */   
/* 191:    */   public void setAsText(String s)
/* 192:    */   {
/* 193:261 */     setText(s);
/* 194:    */   }
/* 195:    */   
/* 196:    */   public String getAsText()
/* 197:    */   {
/* 198:266 */     return getText();
/* 199:    */   }
/* 200:    */   
/* 201:    */   public void setValue(Object o)
/* 202:    */   {
/* 203:271 */     setAsText((String)o);
/* 204:    */   }
/* 205:    */   
/* 206:    */   public Object getValue()
/* 207:    */   {
/* 208:276 */     return getAsText();
/* 209:    */   }
/* 210:    */   
/* 211:    */   public String getJavaInitializationString()
/* 212:    */   {
/* 213:281 */     return null;
/* 214:    */   }
/* 215:    */   
/* 216:    */   public boolean isPaintable()
/* 217:    */   {
/* 218:286 */     return true;
/* 219:    */   }
/* 220:    */   
/* 221:    */   public String[] getTags()
/* 222:    */   {
/* 223:291 */     return null;
/* 224:    */   }
/* 225:    */   
/* 226:    */   public boolean supportsCustomEditor()
/* 227:    */   {
/* 228:296 */     return true;
/* 229:    */   }
/* 230:    */   
/* 231:    */   public Component getCustomEditor()
/* 232:    */   {
/* 233:301 */     return this;
/* 234:    */   }
/* 235:    */   
/* 236:    */   public JPanel getCustomPanel()
/* 237:    */   {
/* 238:306 */     return this;
/* 239:    */   }
/* 240:    */   
/* 241:    */   public void addPropertyChangeListener(PropertyChangeListener pcl)
/* 242:    */   {
/* 243:311 */     if ((this.m_support != null) && (pcl != null)) {
/* 244:312 */       this.m_support.addPropertyChangeListener(pcl);
/* 245:    */     }
/* 246:    */   }
/* 247:    */   
/* 248:    */   public void removePropertyChangeListener(PropertyChangeListener pcl)
/* 249:    */   {
/* 250:318 */     if ((this.m_support != null) && (pcl != null)) {
/* 251:319 */       this.m_support.removePropertyChangeListener(pcl);
/* 252:    */     }
/* 253:    */   }
/* 254:    */   
/* 255:    */   public void paintValue(Graphics gfx, Rectangle box) {}
/* 256:    */   
/* 257:    */   private String processSelected(String selected)
/* 258:    */   {
/* 259:330 */     if (selected.equals(this.m_currentContents)) {
/* 260:333 */       return selected;
/* 261:    */     }
/* 262:335 */     if (this.m_firstCaretPos == 0)
/* 263:    */     {
/* 264:336 */       this.m_currentContents = (selected + this.m_currentContents);
/* 265:    */     }
/* 266:337 */     else if (this.m_firstCaretPos >= this.m_currentContents.length())
/* 267:    */     {
/* 268:338 */       this.m_currentContents += selected;
/* 269:    */     }
/* 270:    */     else
/* 271:    */     {
/* 272:340 */       String left = this.m_currentContents.substring(0, this.m_firstCaretPos);
/* 273:341 */       String right = this.m_currentContents.substring(this.m_firstCaretPos, this.m_currentContents.length());
/* 274:    */       
/* 275:    */ 
/* 276:344 */       this.m_currentContents = (left + selected + right);
/* 277:    */     }
/* 278:353 */     this.m_combo.setSelectedItem(this.m_currentContents);
/* 279:354 */     this.m_support.firePropertyChange("", null, null);
/* 280:    */     
/* 281:356 */     return this.m_currentContents;
/* 282:    */   }
/* 283:    */   
/* 284:    */   public void setEnvironment(final Environment env)
/* 285:    */   {
/* 286:366 */     this.m_env = env;
/* 287:367 */     Vector<String> varKeys = new Vector(env.getVariableNames());
/* 288:    */     
/* 289:    */ 
/* 290:370 */     DefaultComboBoxModel dm = new DefaultComboBoxModel(varKeys)
/* 291:    */     {
/* 292:    */       public Object getSelectedItem()
/* 293:    */       {
/* 294:373 */         Object item = super.getSelectedItem();
/* 295:374 */         if (((item instanceof String)) && 
/* 296:375 */           (env.getVariableValue((String)item) != null))
/* 297:    */         {
/* 298:376 */           String newS = "${" + (String)item + "}";
/* 299:377 */           item = newS;
/* 300:    */         }
/* 301:380 */         return item;
/* 302:    */       }
/* 303:382 */     };
/* 304:383 */     this.m_combo.setModel(dm);
/* 305:384 */     this.m_combo.setSelectedItem("");
/* 306:385 */     this.m_combo.addActionListener(new ActionListener()
/* 307:    */     {
/* 308:    */       public void actionPerformed(ActionEvent e)
/* 309:    */       {
/* 310:388 */         String selected = (String)EnvironmentField.this.m_combo.getSelectedItem();
/* 311:    */         try
/* 312:    */         {
/* 313:390 */           selected = EnvironmentField.this.processSelected(selected);
/* 314:    */           
/* 315:392 */           selected = EnvironmentField.this.m_env.substitute(selected);
/* 316:    */         }
/* 317:    */         catch (Exception ex) {}
/* 318:396 */         EnvironmentField.this.m_combo.setToolTipText(selected);
/* 319:    */       }
/* 320:399 */     });
/* 321:400 */     this.m_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter()
/* 322:    */     {
/* 323:    */       public void keyReleased(KeyEvent e)
/* 324:    */       {
/* 325:403 */         Component theEditor = EnvironmentField.this.m_combo.getEditor().getEditorComponent();
/* 326:404 */         if ((theEditor instanceof JTextField))
/* 327:    */         {
/* 328:405 */           String selected = ((JTextField)theEditor).getText();
/* 329:406 */           EnvironmentField.this.m_currentContents = selected;
/* 330:407 */           if (EnvironmentField.this.m_env != null) {
/* 331:    */             try
/* 332:    */             {
/* 333:409 */               selected = EnvironmentField.this.m_env.substitute(selected);
/* 334:    */             }
/* 335:    */             catch (Exception ex) {}
/* 336:    */           }
/* 337:414 */           EnvironmentField.this.m_combo.setToolTipText(selected);
/* 338:    */         }
/* 339:    */       }
/* 340:    */     });
/* 341:    */   }
/* 342:    */   
/* 343:    */   public void setEnabled(boolean enabled)
/* 344:    */   {
/* 345:427 */     this.m_combo.setEnabled(enabled);
/* 346:    */   }
/* 347:    */   
/* 348:    */   public void setEditable(boolean editable)
/* 349:    */   {
/* 350:436 */     this.m_combo.setEditable(editable);
/* 351:    */   }
/* 352:    */   
/* 353:    */   public static void main(String[] args)
/* 354:    */   {
/* 355:    */     try
/* 356:    */     {
/* 357:446 */       JFrame jf = new JFrame("EnvironmentField");
/* 358:447 */       jf.getContentPane().setLayout(new BorderLayout());
/* 359:448 */       EnvironmentField f = new EnvironmentField("A label here");
/* 360:449 */       jf.getContentPane().add(f, "Center");
/* 361:450 */       Environment env = Environment.getSystemWide();
/* 362:451 */       f.setEnvironment(env);
/* 363:452 */       jf.addWindowListener(new WindowAdapter()
/* 364:    */       {
/* 365:    */         public void windowClosing(WindowEvent e)
/* 366:    */         {
/* 367:455 */           this.val$jf.dispose();
/* 368:456 */           System.exit(0);
/* 369:    */         }
/* 370:458 */       });
/* 371:459 */       jf.pack();
/* 372:460 */       jf.setVisible(true);
/* 373:    */     }
/* 374:    */     catch (Exception ex)
/* 375:    */     {
/* 376:462 */       ex.printStackTrace();
/* 377:    */     }
/* 378:    */   }
/* 379:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.EnvironmentField
 * JD-Core Version:    0.7.0.1
 */