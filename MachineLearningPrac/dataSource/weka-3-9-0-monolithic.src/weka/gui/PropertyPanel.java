/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.FontMetrics;
/*   7:    */ import java.awt.Graphics;
/*   8:    */ import java.awt.Insets;
/*   9:    */ import java.awt.Point;
/*  10:    */ import java.awt.Rectangle;
/*  11:    */ import java.awt.Toolkit;
/*  12:    */ import java.awt.datatransfer.Clipboard;
/*  13:    */ import java.awt.datatransfer.StringSelection;
/*  14:    */ import java.awt.event.ActionEvent;
/*  15:    */ import java.awt.event.ActionListener;
/*  16:    */ import java.awt.event.MouseAdapter;
/*  17:    */ import java.awt.event.MouseEvent;
/*  18:    */ import java.beans.PropertyChangeEvent;
/*  19:    */ import java.beans.PropertyChangeListener;
/*  20:    */ import java.beans.PropertyEditor;
/*  21:    */ import javax.swing.BorderFactory;
/*  22:    */ import javax.swing.JMenuItem;
/*  23:    */ import javax.swing.JOptionPane;
/*  24:    */ import javax.swing.JPanel;
/*  25:    */ import javax.swing.JPopupMenu;
/*  26:    */ import weka.core.OptionHandler;
/*  27:    */ import weka.core.Utils;
/*  28:    */ 
/*  29:    */ public class PropertyPanel
/*  30:    */   extends JPanel
/*  31:    */ {
/*  32:    */   static final long serialVersionUID = 5370025273466728904L;
/*  33:    */   private final PropertyEditor m_Editor;
/*  34:    */   private PropertyDialog m_PD;
/*  35: 71 */   private boolean m_HasCustomPanel = false;
/*  36:    */   private JPanel m_CustomPanel;
/*  37:    */   
/*  38:    */   public PropertyPanel(PropertyEditor pe)
/*  39:    */   {
/*  40: 83 */     this(pe, false);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public PropertyPanel(PropertyEditor pe, boolean ignoreCustomPanel)
/*  44:    */   {
/*  45: 95 */     this.m_Editor = pe;
/*  46: 97 */     if ((!ignoreCustomPanel) && ((this.m_Editor instanceof CustomPanelSupplier)))
/*  47:    */     {
/*  48: 98 */       setLayout(new BorderLayout());
/*  49: 99 */       this.m_CustomPanel = ((CustomPanelSupplier)this.m_Editor).getCustomPanel();
/*  50:100 */       add(this.m_CustomPanel, "Center");
/*  51:101 */       this.m_HasCustomPanel = true;
/*  52:    */     }
/*  53:    */     else
/*  54:    */     {
/*  55:103 */       createDefaultPanel();
/*  56:    */     }
/*  57:    */   }
/*  58:    */   
/*  59:    */   protected void createDefaultPanel()
/*  60:    */   {
/*  61:113 */     setBorder(BorderFactory.createEtchedBorder());
/*  62:114 */     setToolTipText("Left-click to edit properties for this object, right-click/Alt+Shift+left-click for menu");
/*  63:115 */     setOpaque(true);
/*  64:116 */     final Component comp = this;
/*  65:117 */     addMouseListener(new MouseAdapter()
/*  66:    */     {
/*  67:    */       public void mouseClicked(MouseEvent evt)
/*  68:    */       {
/*  69:120 */         if (evt.getClickCount() == 1) {
/*  70:121 */           if ((evt.getButton() == 1) && (!evt.isAltDown()) && (!evt.isShiftDown()))
/*  71:    */           {
/*  72:123 */             PropertyPanel.this.showPropertyDialog();
/*  73:    */           }
/*  74:124 */           else if ((evt.getButton() == 3) || ((evt.getButton() == 1) && (evt.isAltDown()) && (evt.isShiftDown())))
/*  75:    */           {
/*  76:127 */             JPopupMenu menu = new JPopupMenu();
/*  77:130 */             if (PropertyPanel.this.m_Editor.getValue() != null)
/*  78:    */             {
/*  79:131 */               JMenuItem item = new JMenuItem("Show properties...");
/*  80:132 */               item.addActionListener(new ActionListener()
/*  81:    */               {
/*  82:    */                 public void actionPerformed(ActionEvent e)
/*  83:    */                 {
/*  84:135 */                   PropertyPanel.this.showPropertyDialog();
/*  85:    */                 }
/*  86:137 */               });
/*  87:138 */               menu.add(item);
/*  88:    */               
/*  89:140 */               item = new JMenuItem("Copy configuration to clipboard");
/*  90:141 */               item.addActionListener(new ActionListener()
/*  91:    */               {
/*  92:    */                 public void actionPerformed(ActionEvent e)
/*  93:    */                 {
/*  94:144 */                   String str = PropertyPanel.this.m_Editor.getValue().getClass().getName();
/*  95:145 */                   if ((PropertyPanel.this.m_Editor.getValue() instanceof OptionHandler)) {
/*  96:146 */                     str = str + " " + Utils.joinOptions(((OptionHandler)PropertyPanel.this.m_Editor.getValue()).getOptions());
/*  97:    */                   }
/*  98:149 */                   StringSelection selection = new StringSelection(str.trim());
/*  99:150 */                   Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
/* 100:    */                   
/* 101:152 */                   clipboard.setContents(selection, selection);
/* 102:    */                 }
/* 103:154 */               });
/* 104:155 */               menu.add(item);
/* 105:    */             }
/* 106:158 */             JMenuItem item = new JMenuItem("Enter configuration...");
/* 107:159 */             item.addActionListener(new ActionListener()
/* 108:    */             {
/* 109:    */               public void actionPerformed(ActionEvent e)
/* 110:    */               {
/* 111:162 */                 String str = JOptionPane.showInputDialog(PropertyPanel.1.this.val$comp, "Configuration (<classname> [<options>])");
/* 112:164 */                 if (str != null) {
/* 113:    */                   try
/* 114:    */                   {
/* 115:166 */                     String[] options = Utils.splitOptions(str);
/* 116:167 */                     String classname = options[0];
/* 117:168 */                     options[0] = "";
/* 118:169 */                     PropertyPanel.this.m_Editor.setValue(Utils.forName(Object.class, classname, options));
/* 119:    */                   }
/* 120:    */                   catch (Exception ex)
/* 121:    */                   {
/* 122:172 */                     JOptionPane.showMessageDialog(PropertyPanel.1.this.val$comp, "Error parsing commandline:\n" + ex, "Error...", 0);
/* 123:    */                   }
/* 124:    */                 }
/* 125:    */               }
/* 126:178 */             });
/* 127:179 */             menu.add(item);
/* 128:181 */             if ((PropertyPanel.this.m_Editor.getValue() instanceof OptionHandler))
/* 129:    */             {
/* 130:182 */               item = new JMenuItem("Edit configuration...");
/* 131:183 */               item.addActionListener(new ActionListener()
/* 132:    */               {
/* 133:    */                 public void actionPerformed(ActionEvent e)
/* 134:    */                 {
/* 135:186 */                   String str = PropertyPanel.this.m_Editor.getValue().getClass().getName();
/* 136:187 */                   str = str + " " + Utils.joinOptions(((OptionHandler)PropertyPanel.this.m_Editor.getValue()).getOptions());
/* 137:    */                   
/* 138:    */ 
/* 139:190 */                   str = JOptionPane.showInputDialog(PropertyPanel.1.this.val$comp, "Configuration", str);
/* 140:191 */                   if (str != null) {
/* 141:    */                     try
/* 142:    */                     {
/* 143:193 */                       String[] options = Utils.splitOptions(str);
/* 144:194 */                       String classname = options[0];
/* 145:195 */                       options[0] = "";
/* 146:196 */                       PropertyPanel.this.m_Editor.setValue(Utils.forName(Object.class, classname, options));
/* 147:    */                     }
/* 148:    */                     catch (Exception ex)
/* 149:    */                     {
/* 150:199 */                       JOptionPane.showMessageDialog(PropertyPanel.1.this.val$comp, "Error parsing commandline:\n" + ex, "Error...", 0);
/* 151:    */                     }
/* 152:    */                   }
/* 153:    */                 }
/* 154:205 */               });
/* 155:206 */               menu.add(item);
/* 156:    */             }
/* 157:209 */             if ((PropertyPanel.this.m_Editor instanceof GenericObjectEditor)) {
/* 158:210 */               ((GenericObjectEditor)PropertyPanel.this.m_Editor).getHistory().customizePopupMenu(menu, PropertyPanel.this.m_Editor.getValue(), new GenericObjectEditorHistory.HistorySelectionListener()
/* 159:    */               {
/* 160:    */                 public void historySelected(GenericObjectEditorHistory.HistorySelectionEvent e)
/* 161:    */                 {
/* 162:214 */                   PropertyPanel.this.m_Editor.setValue(e.getHistoryItem());
/* 163:    */                 }
/* 164:    */               });
/* 165:    */             }
/* 166:219 */             menu.show(comp, evt.getX(), evt.getY());
/* 167:    */           }
/* 168:    */         }
/* 169:    */       }
/* 170:223 */     });
/* 171:224 */     Dimension newPref = getPreferredSize();
/* 172:225 */     newPref.height = (getFontMetrics(getFont()).getHeight() * 5 / 4);
/* 173:226 */     newPref.width = (newPref.height * 5);
/* 174:227 */     setPreferredSize(newPref);
/* 175:    */     
/* 176:229 */     this.m_Editor.addPropertyChangeListener(new PropertyChangeListener()
/* 177:    */     {
/* 178:    */       public void propertyChange(PropertyChangeEvent evt)
/* 179:    */       {
/* 180:232 */         PropertyPanel.this.repaint();
/* 181:    */       }
/* 182:    */     });
/* 183:    */   }
/* 184:    */   
/* 185:    */   public void showPropertyDialog()
/* 186:    */   {
/* 187:242 */     if (this.m_Editor.getValue() != null)
/* 188:    */     {
/* 189:243 */       if (this.m_PD == null)
/* 190:    */       {
/* 191:244 */         int x = getLocationOnScreen().x;
/* 192:245 */         int y = getLocationOnScreen().y;
/* 193:246 */         if (PropertyDialog.getParentDialog(this) != null) {
/* 194:247 */           this.m_PD = new PropertyDialog(PropertyDialog.getParentDialog(this), this.m_Editor, x, y);
/* 195:    */         } else {
/* 196:250 */           this.m_PD = new PropertyDialog(PropertyDialog.getParentFrame(this), this.m_Editor, x, y);
/* 197:    */         }
/* 198:252 */         this.m_PD.setVisible(true);
/* 199:    */       }
/* 200:    */       else
/* 201:    */       {
/* 202:254 */         this.m_PD.setVisible(true);
/* 203:    */       }
/* 204:257 */       this.m_Editor.setValue(this.m_Editor.getValue());
/* 205:    */     }
/* 206:    */   }
/* 207:    */   
/* 208:    */   public void removeNotify()
/* 209:    */   {
/* 210:267 */     super.removeNotify();
/* 211:268 */     if (this.m_PD != null)
/* 212:    */     {
/* 213:269 */       this.m_PD.dispose();
/* 214:270 */       this.m_PD = null;
/* 215:    */     }
/* 216:    */   }
/* 217:    */   
/* 218:    */   public void setEnabled(boolean enabled)
/* 219:    */   {
/* 220:281 */     super.setEnabled(enabled);
/* 221:282 */     if (this.m_HasCustomPanel) {
/* 222:283 */       this.m_CustomPanel.setEnabled(enabled);
/* 223:    */     }
/* 224:    */   }
/* 225:    */   
/* 226:    */   public void paintComponent(Graphics g)
/* 227:    */   {
/* 228:296 */     if (!this.m_HasCustomPanel)
/* 229:    */     {
/* 230:297 */       Insets i = getInsets();
/* 231:298 */       Rectangle box = new Rectangle(i.left, i.top, getSize().width - i.left - i.right - 1, getSize().height - i.top - i.bottom - 1);
/* 232:    */       
/* 233:    */ 
/* 234:301 */       g.clearRect(i.left, i.top, getSize().width - i.right - i.left, getSize().height - i.bottom - i.top);
/* 235:    */       
/* 236:303 */       this.m_Editor.paintValue(g, box);
/* 237:    */     }
/* 238:    */   }
/* 239:    */   
/* 240:    */   public boolean addToHistory()
/* 241:    */   {
/* 242:313 */     return addToHistory(this.m_Editor.getValue());
/* 243:    */   }
/* 244:    */   
/* 245:    */   public boolean addToHistory(Object obj)
/* 246:    */   {
/* 247:323 */     if (((this.m_Editor instanceof GenericObjectEditor)) && (obj != null))
/* 248:    */     {
/* 249:324 */       ((GenericObjectEditor)this.m_Editor).getHistory().add(obj);
/* 250:325 */       return true;
/* 251:    */     }
/* 252:328 */     return false;
/* 253:    */   }
/* 254:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.PropertyPanel
 * JD-Core Version:    0.7.0.1
 */