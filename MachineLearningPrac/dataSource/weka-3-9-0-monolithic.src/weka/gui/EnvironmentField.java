/*   1:    */ package weka.gui;
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
/*  34:    */ 
/*  35:    */ public class EnvironmentField
/*  36:    */   extends JPanel
/*  37:    */   implements EnvironmentHandler, PropertyEditor, CustomPanelSupplier
/*  38:    */ {
/*  39:    */   private static final long serialVersionUID = -3125404573324734121L;
/*  40:    */   protected JLabel m_label;
/*  41:    */   protected JComboBox m_combo;
/*  42:    */   protected Environment m_env;
/*  43: 78 */   protected String m_currentContents = "";
/*  44: 79 */   protected int m_firstCaretPos = 0;
/*  45: 80 */   protected int m_previousCaretPos = 0;
/*  46: 81 */   protected int m_currentCaretPos = 0;
/*  47: 83 */   protected PropertyChangeSupport m_support = new PropertyChangeSupport(this);
/*  48:    */   
/*  49:    */   public static class WideComboBox
/*  50:    */     extends JComboBox
/*  51:    */   {
/*  52:    */     private static final long serialVersionUID = -6512065375459733517L;
/*  53:    */     
/*  54:    */     public WideComboBox() {}
/*  55:    */     
/*  56:    */     public WideComboBox(Object[] items)
/*  57:    */     {
/*  58:102 */       super();
/*  59:    */     }
/*  60:    */     
/*  61:    */     public WideComboBox(Vector<Object> items)
/*  62:    */     {
/*  63:106 */       super();
/*  64:    */     }
/*  65:    */     
/*  66:    */     public WideComboBox(ComboBoxModel aModel)
/*  67:    */     {
/*  68:110 */       super();
/*  69:    */     }
/*  70:    */     
/*  71:113 */     private boolean m_layingOut = false;
/*  72:    */     
/*  73:    */     public void doLayout()
/*  74:    */     {
/*  75:    */       try
/*  76:    */       {
/*  77:118 */         this.m_layingOut = true;
/*  78:119 */         super.doLayout();
/*  79:    */       }
/*  80:    */       finally
/*  81:    */       {
/*  82:121 */         this.m_layingOut = false;
/*  83:    */       }
/*  84:    */     }
/*  85:    */     
/*  86:    */     public Dimension getSize()
/*  87:    */     {
/*  88:127 */       Dimension dim = super.getSize();
/*  89:128 */       if (!this.m_layingOut) {
/*  90:129 */         dim.width = Math.max(dim.width, getPreferredSize().width);
/*  91:    */       }
/*  92:131 */       return dim;
/*  93:    */     }
/*  94:    */   }
/*  95:    */   
/*  96:    */   public EnvironmentField()
/*  97:    */   {
/*  98:139 */     this("");
/*  99:140 */     setEnvironment(Environment.getSystemWide());
/* 100:    */   }
/* 101:    */   
/* 102:    */   public EnvironmentField(Environment env)
/* 103:    */   {
/* 104:149 */     this("");
/* 105:150 */     setEnvironment(env);
/* 106:    */   }
/* 107:    */   
/* 108:    */   public EnvironmentField(String label, Environment env)
/* 109:    */   {
/* 110:160 */     this(label);
/* 111:161 */     setEnvironment(env);
/* 112:    */   }
/* 113:    */   
/* 114:    */   public EnvironmentField(String label)
/* 115:    */   {
/* 116:170 */     setLayout(new BorderLayout());
/* 117:171 */     this.m_label = new JLabel(label);
/* 118:172 */     if (label.length() > 0) {
/* 119:173 */       this.m_label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
/* 120:    */     }
/* 121:175 */     add(this.m_label, "West");
/* 122:    */     
/* 123:177 */     this.m_combo = new WideComboBox();
/* 124:178 */     this.m_combo.setEditable(true);
/* 125:    */     
/* 126:    */ 
/* 127:181 */     Component theEditor = this.m_combo.getEditor().getEditorComponent();
/* 128:182 */     if ((theEditor instanceof JTextField))
/* 129:    */     {
/* 130:183 */       ((JTextField)this.m_combo.getEditor().getEditorComponent()).addCaretListener(new CaretListener()
/* 131:    */       {
/* 132:    */         public void caretUpdate(CaretEvent e)
/* 133:    */         {
/* 134:188 */           EnvironmentField.this.m_firstCaretPos = EnvironmentField.this.m_previousCaretPos;
/* 135:189 */           EnvironmentField.this.m_previousCaretPos = EnvironmentField.this.m_currentCaretPos;
/* 136:190 */           EnvironmentField.this.m_currentCaretPos = e.getDot();
/* 137:    */         }
/* 138:193 */       });
/* 139:194 */       this.m_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter()
/* 140:    */       {
/* 141:    */         public void keyReleased(KeyEvent e)
/* 142:    */         {
/* 143:197 */           EnvironmentField.this.m_support.firePropertyChange("", null, null);
/* 144:    */         }
/* 145:200 */       });
/* 146:201 */       ((JTextField)this.m_combo.getEditor().getEditorComponent()).addFocusListener(new FocusAdapter()
/* 147:    */       {
/* 148:    */         public void focusLost(FocusEvent e)
/* 149:    */         {
/* 150:205 */           EnvironmentField.this.m_support.firePropertyChange("", null, null);
/* 151:    */         }
/* 152:    */       });
/* 153:    */     }
/* 154:209 */     add(this.m_combo, "Center");
/* 155:    */     
/* 156:    */ 
/* 157:    */ 
/* 158:213 */     Dimension d = getPreferredSize();
/* 159:214 */     setPreferredSize(new Dimension(250, d.height));
/* 160:    */   }
/* 161:    */   
/* 162:    */   public void setLabel(String label)
/* 163:    */   {
/* 164:223 */     this.m_label.setText(label);
/* 165:    */   }
/* 166:    */   
/* 167:    */   public void setText(String text)
/* 168:    */   {
/* 169:232 */     this.m_currentContents = text;
/* 170:233 */     Component theEditor = this.m_combo.getEditor().getEditorComponent();
/* 171:234 */     if ((theEditor instanceof JTextField)) {
/* 172:235 */       ((JTextField)theEditor).setText(text);
/* 173:    */     } else {
/* 174:237 */       this.m_combo.setSelectedItem(this.m_currentContents);
/* 175:    */     }
/* 176:239 */     this.m_support.firePropertyChange("", null, null);
/* 177:    */   }
/* 178:    */   
/* 179:    */   public String getText()
/* 180:    */   {
/* 181:248 */     Component theEditor = this.m_combo.getEditor().getEditorComponent();
/* 182:249 */     String text = this.m_combo.getSelectedItem().toString();
/* 183:250 */     if ((theEditor instanceof JTextField)) {
/* 184:251 */       text = ((JTextField)theEditor).getText();
/* 185:    */     }
/* 186:253 */     return text;
/* 187:    */   }
/* 188:    */   
/* 189:    */   public void setAsText(String s)
/* 190:    */   {
/* 191:258 */     setText(s);
/* 192:    */   }
/* 193:    */   
/* 194:    */   public String getAsText()
/* 195:    */   {
/* 196:263 */     return getText();
/* 197:    */   }
/* 198:    */   
/* 199:    */   public void setValue(Object o)
/* 200:    */   {
/* 201:268 */     setAsText((String)o);
/* 202:    */   }
/* 203:    */   
/* 204:    */   public Object getValue()
/* 205:    */   {
/* 206:273 */     return getAsText();
/* 207:    */   }
/* 208:    */   
/* 209:    */   public String getJavaInitializationString()
/* 210:    */   {
/* 211:278 */     return null;
/* 212:    */   }
/* 213:    */   
/* 214:    */   public boolean isPaintable()
/* 215:    */   {
/* 216:283 */     return true;
/* 217:    */   }
/* 218:    */   
/* 219:    */   public String[] getTags()
/* 220:    */   {
/* 221:288 */     return null;
/* 222:    */   }
/* 223:    */   
/* 224:    */   public boolean supportsCustomEditor()
/* 225:    */   {
/* 226:293 */     return true;
/* 227:    */   }
/* 228:    */   
/* 229:    */   public Component getCustomEditor()
/* 230:    */   {
/* 231:298 */     return this;
/* 232:    */   }
/* 233:    */   
/* 234:    */   public JPanel getCustomPanel()
/* 235:    */   {
/* 236:303 */     return this;
/* 237:    */   }
/* 238:    */   
/* 239:    */   public void addPropertyChangeListener(PropertyChangeListener pcl)
/* 240:    */   {
/* 241:308 */     if ((this.m_support != null) && (pcl != null)) {
/* 242:309 */       this.m_support.addPropertyChangeListener(pcl);
/* 243:    */     }
/* 244:    */   }
/* 245:    */   
/* 246:    */   public void removePropertyChangeListener(PropertyChangeListener pcl)
/* 247:    */   {
/* 248:315 */     if ((this.m_support != null) && (pcl != null)) {
/* 249:316 */       this.m_support.removePropertyChangeListener(pcl);
/* 250:    */     }
/* 251:    */   }
/* 252:    */   
/* 253:    */   public void paintValue(Graphics gfx, Rectangle box) {}
/* 254:    */   
/* 255:    */   private String processSelected(String selected)
/* 256:    */   {
/* 257:327 */     if (selected.equals(this.m_currentContents)) {
/* 258:330 */       return selected;
/* 259:    */     }
/* 260:332 */     if (this.m_firstCaretPos == 0)
/* 261:    */     {
/* 262:333 */       this.m_currentContents = (selected + this.m_currentContents);
/* 263:    */     }
/* 264:334 */     else if (this.m_firstCaretPos >= this.m_currentContents.length())
/* 265:    */     {
/* 266:335 */       this.m_currentContents += selected;
/* 267:    */     }
/* 268:    */     else
/* 269:    */     {
/* 270:337 */       String left = this.m_currentContents.substring(0, this.m_firstCaretPos);
/* 271:338 */       String right = this.m_currentContents.substring(this.m_firstCaretPos, this.m_currentContents.length());
/* 272:    */       
/* 273:    */ 
/* 274:341 */       this.m_currentContents = (left + selected + right);
/* 275:    */     }
/* 276:350 */     this.m_combo.setSelectedItem(this.m_currentContents);
/* 277:351 */     this.m_support.firePropertyChange("", null, null);
/* 278:    */     
/* 279:353 */     return this.m_currentContents;
/* 280:    */   }
/* 281:    */   
/* 282:    */   public void setEnvironment(final Environment env)
/* 283:    */   {
/* 284:363 */     this.m_env = env;
/* 285:364 */     Vector<String> varKeys = new Vector(env.getVariableNames());
/* 286:    */     
/* 287:    */ 
/* 288:367 */     DefaultComboBoxModel dm = new DefaultComboBoxModel(varKeys)
/* 289:    */     {
/* 290:    */       public Object getSelectedItem()
/* 291:    */       {
/* 292:370 */         Object item = super.getSelectedItem();
/* 293:371 */         if (((item instanceof String)) && 
/* 294:372 */           (env.getVariableValue((String)item) != null))
/* 295:    */         {
/* 296:373 */           String newS = "${" + (String)item + "}";
/* 297:374 */           item = newS;
/* 298:    */         }
/* 299:377 */         return item;
/* 300:    */       }
/* 301:379 */     };
/* 302:380 */     this.m_combo.setModel(dm);
/* 303:381 */     this.m_combo.setSelectedItem("");
/* 304:382 */     this.m_combo.addActionListener(new ActionListener()
/* 305:    */     {
/* 306:    */       public void actionPerformed(ActionEvent e)
/* 307:    */       {
/* 308:385 */         String selected = (String)EnvironmentField.this.m_combo.getSelectedItem();
/* 309:    */         try
/* 310:    */         {
/* 311:387 */           selected = EnvironmentField.this.processSelected(selected);
/* 312:    */           
/* 313:389 */           selected = EnvironmentField.this.m_env.substitute(selected);
/* 314:    */         }
/* 315:    */         catch (Exception ex) {}
/* 316:393 */         EnvironmentField.this.m_combo.setToolTipText(selected);
/* 317:    */       }
/* 318:396 */     });
/* 319:397 */     this.m_combo.getEditor().getEditorComponent().addKeyListener(new KeyAdapter()
/* 320:    */     {
/* 321:    */       public void keyReleased(KeyEvent e)
/* 322:    */       {
/* 323:400 */         Component theEditor = EnvironmentField.this.m_combo.getEditor().getEditorComponent();
/* 324:401 */         if ((theEditor instanceof JTextField))
/* 325:    */         {
/* 326:402 */           String selected = ((JTextField)theEditor).getText();
/* 327:403 */           EnvironmentField.this.m_currentContents = selected;
/* 328:404 */           if (EnvironmentField.this.m_env != null) {
/* 329:    */             try
/* 330:    */             {
/* 331:406 */               selected = EnvironmentField.this.m_env.substitute(selected);
/* 332:    */             }
/* 333:    */             catch (Exception ex) {}
/* 334:    */           }
/* 335:411 */           EnvironmentField.this.m_combo.setToolTipText(selected);
/* 336:    */         }
/* 337:    */       }
/* 338:    */     });
/* 339:    */   }
/* 340:    */   
/* 341:    */   public void setEnabled(boolean enabled)
/* 342:    */   {
/* 343:424 */     this.m_combo.setEnabled(enabled);
/* 344:    */   }
/* 345:    */   
/* 346:    */   public void setEditable(boolean editable)
/* 347:    */   {
/* 348:433 */     this.m_combo.setEditable(editable);
/* 349:    */   }
/* 350:    */   
/* 351:    */   public static void main(String[] args)
/* 352:    */   {
/* 353:    */     try
/* 354:    */     {
/* 355:443 */       JFrame jf = new JFrame("EnvironmentField");
/* 356:444 */       jf.getContentPane().setLayout(new BorderLayout());
/* 357:445 */       EnvironmentField f = new EnvironmentField("A label here");
/* 358:446 */       jf.getContentPane().add(f, "Center");
/* 359:447 */       Environment env = Environment.getSystemWide();
/* 360:448 */       f.setEnvironment(env);
/* 361:449 */       jf.addWindowListener(new WindowAdapter()
/* 362:    */       {
/* 363:    */         public void windowClosing(WindowEvent e)
/* 364:    */         {
/* 365:452 */           this.val$jf.dispose();
/* 366:453 */           System.exit(0);
/* 367:    */         }
/* 368:455 */       });
/* 369:456 */       jf.pack();
/* 370:457 */       jf.setVisible(true);
/* 371:    */     }
/* 372:    */     catch (Exception ex)
/* 373:    */     {
/* 374:459 */       ex.printStackTrace();
/* 375:    */     }
/* 376:    */   }
/* 377:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.EnvironmentField
 * JD-Core Version:    0.7.0.1
 */