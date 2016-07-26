/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.GridLayout;
/*   7:    */ import java.awt.event.ActionEvent;
/*   8:    */ import java.awt.event.ActionListener;
/*   9:    */ import java.awt.event.WindowAdapter;
/*  10:    */ import java.awt.event.WindowEvent;
/*  11:    */ import java.io.BufferedReader;
/*  12:    */ import java.io.FileReader;
/*  13:    */ import java.io.PrintStream;
/*  14:    */ import java.util.regex.Pattern;
/*  15:    */ import javax.swing.BorderFactory;
/*  16:    */ import javax.swing.JButton;
/*  17:    */ import javax.swing.JFrame;
/*  18:    */ import javax.swing.JOptionPane;
/*  19:    */ import javax.swing.JPanel;
/*  20:    */ import javax.swing.JScrollPane;
/*  21:    */ import javax.swing.JTable;
/*  22:    */ import javax.swing.ListSelectionModel;
/*  23:    */ import javax.swing.table.AbstractTableModel;
/*  24:    */ import javax.swing.table.TableColumn;
/*  25:    */ import javax.swing.table.TableColumnModel;
/*  26:    */ import javax.swing.table.TableModel;
/*  27:    */ import weka.core.Attribute;
/*  28:    */ import weka.core.Instances;
/*  29:    */ 
/*  30:    */ public class AttributeSelectionPanel
/*  31:    */   extends JPanel
/*  32:    */ {
/*  33:    */   private static final long serialVersionUID = 627131485290359194L;
/*  34:    */   
/*  35:    */   class AttributeTableModel
/*  36:    */     extends AbstractTableModel
/*  37:    */   {
/*  38:    */     private static final long serialVersionUID = -4152987434024338064L;
/*  39:    */     protected Instances m_Instances;
/*  40:    */     protected boolean[] m_Selected;
/*  41:    */     
/*  42:    */     public AttributeTableModel(Instances instances)
/*  43:    */     {
/*  44: 91 */       setInstances(instances);
/*  45:    */     }
/*  46:    */     
/*  47:    */     public void setInstances(Instances instances)
/*  48:    */     {
/*  49:101 */       this.m_Instances = instances;
/*  50:102 */       this.m_Selected = new boolean[this.m_Instances.numAttributes()];
/*  51:    */     }
/*  52:    */     
/*  53:    */     public int getRowCount()
/*  54:    */     {
/*  55:113 */       return this.m_Selected.length;
/*  56:    */     }
/*  57:    */     
/*  58:    */     public int getColumnCount()
/*  59:    */     {
/*  60:124 */       return 3;
/*  61:    */     }
/*  62:    */     
/*  63:    */     public Object getValueAt(int row, int column)
/*  64:    */     {
/*  65:137 */       switch (column)
/*  66:    */       {
/*  67:    */       case 0: 
/*  68:139 */         return new Integer(row + 1);
/*  69:    */       case 1: 
/*  70:141 */         return new Boolean(this.m_Selected[row]);
/*  71:    */       case 2: 
/*  72:143 */         return this.m_Instances.attribute(row).name();
/*  73:    */       }
/*  74:145 */       return null;
/*  75:    */     }
/*  76:    */     
/*  77:    */     public String getColumnName(int column)
/*  78:    */     {
/*  79:158 */       switch (column)
/*  80:    */       {
/*  81:    */       case 0: 
/*  82:160 */         return new String("No.");
/*  83:    */       case 1: 
/*  84:162 */         return new String("");
/*  85:    */       case 2: 
/*  86:164 */         return new String("Name");
/*  87:    */       }
/*  88:166 */       return null;
/*  89:    */     }
/*  90:    */     
/*  91:    */     public void setValueAt(Object value, int row, int col)
/*  92:    */     {
/*  93:180 */       if (col == 1)
/*  94:    */       {
/*  95:181 */         this.m_Selected[row] = ((Boolean)value).booleanValue();
/*  96:182 */         fireTableRowsUpdated(0, this.m_Selected.length);
/*  97:    */       }
/*  98:    */     }
/*  99:    */     
/* 100:    */     public Class<?> getColumnClass(int col)
/* 101:    */     {
/* 102:194 */       return getValueAt(0, col).getClass();
/* 103:    */     }
/* 104:    */     
/* 105:    */     public boolean isCellEditable(int row, int col)
/* 106:    */     {
/* 107:207 */       if (col == 1) {
/* 108:208 */         return true;
/* 109:    */       }
/* 110:210 */       return false;
/* 111:    */     }
/* 112:    */     
/* 113:    */     public int[] getSelectedAttributes()
/* 114:    */     {
/* 115:220 */       int[] r1 = new int[getRowCount()];
/* 116:221 */       int selCount = 0;
/* 117:222 */       for (int i = 0; i < getRowCount(); i++) {
/* 118:223 */         if (this.m_Selected[i] != 0) {
/* 119:224 */           r1[(selCount++)] = i;
/* 120:    */         }
/* 121:    */       }
/* 122:227 */       int[] result = new int[selCount];
/* 123:228 */       System.arraycopy(r1, 0, result, 0, selCount);
/* 124:229 */       return result;
/* 125:    */     }
/* 126:    */     
/* 127:    */     public void includeAll()
/* 128:    */     {
/* 129:237 */       for (int i = 0; i < this.m_Selected.length; i++) {
/* 130:238 */         this.m_Selected[i] = true;
/* 131:    */       }
/* 132:240 */       fireTableRowsUpdated(0, this.m_Selected.length);
/* 133:    */     }
/* 134:    */     
/* 135:    */     public void removeAll()
/* 136:    */     {
/* 137:248 */       for (int i = 0; i < this.m_Selected.length; i++) {
/* 138:249 */         this.m_Selected[i] = false;
/* 139:    */       }
/* 140:251 */       fireTableRowsUpdated(0, this.m_Selected.length);
/* 141:    */     }
/* 142:    */     
/* 143:    */     public void invert()
/* 144:    */     {
/* 145:259 */       for (int i = 0; i < this.m_Selected.length; i++) {
/* 146:260 */         this.m_Selected[i] = (this.m_Selected[i] == 0 ? 1 : false);
/* 147:    */       }
/* 148:262 */       fireTableRowsUpdated(0, this.m_Selected.length);
/* 149:    */     }
/* 150:    */     
/* 151:    */     public void pattern(String pattern)
/* 152:    */     {
/* 153:272 */       for (int i = 0; i < this.m_Selected.length; i++) {
/* 154:273 */         this.m_Selected[i] = Pattern.matches(pattern, this.m_Instances.attribute(i).name());
/* 155:    */       }
/* 156:276 */       fireTableRowsUpdated(0, this.m_Selected.length);
/* 157:    */     }
/* 158:    */     
/* 159:    */     public void setSelectedAttributes(boolean[] selected)
/* 160:    */       throws Exception
/* 161:    */     {
/* 162:280 */       if (selected.length != this.m_Selected.length) {
/* 163:281 */         throw new Exception("Supplied array does not have the same number of elements as there are attributes!");
/* 164:    */       }
/* 165:285 */       for (int i = 0; i < selected.length; i++) {
/* 166:286 */         this.m_Selected[i] = selected[i];
/* 167:    */       }
/* 168:288 */       fireTableRowsUpdated(0, this.m_Selected.length);
/* 169:    */     }
/* 170:    */   }
/* 171:    */   
/* 172:293 */   protected JButton m_IncludeAll = new JButton("All");
/* 173:296 */   protected JButton m_RemoveAll = new JButton("None");
/* 174:299 */   protected JButton m_Invert = new JButton("Invert");
/* 175:302 */   protected JButton m_Pattern = new JButton("Pattern");
/* 176:305 */   protected JTable m_Table = new JTable();
/* 177:    */   protected AttributeTableModel m_Model;
/* 178:311 */   protected String m_PatternRegEx = "";
/* 179:    */   
/* 180:    */   public AttributeSelectionPanel()
/* 181:    */   {
/* 182:317 */     this(true, true, true, true);
/* 183:    */   }
/* 184:    */   
/* 185:    */   public AttributeSelectionPanel(boolean include, boolean remove, boolean invert, boolean pattern)
/* 186:    */   {
/* 187:331 */     this.m_IncludeAll.setToolTipText("Selects all attributes");
/* 188:332 */     this.m_IncludeAll.setEnabled(false);
/* 189:333 */     this.m_IncludeAll.addActionListener(new ActionListener()
/* 190:    */     {
/* 191:    */       public void actionPerformed(ActionEvent e)
/* 192:    */       {
/* 193:336 */         AttributeSelectionPanel.this.m_Model.includeAll();
/* 194:    */       }
/* 195:338 */     });
/* 196:339 */     this.m_RemoveAll.setToolTipText("Unselects all attributes");
/* 197:340 */     this.m_RemoveAll.setEnabled(false);
/* 198:341 */     this.m_RemoveAll.addActionListener(new ActionListener()
/* 199:    */     {
/* 200:    */       public void actionPerformed(ActionEvent e)
/* 201:    */       {
/* 202:344 */         AttributeSelectionPanel.this.m_Model.removeAll();
/* 203:    */       }
/* 204:346 */     });
/* 205:347 */     this.m_Invert.setToolTipText("Inverts the current attribute selection");
/* 206:348 */     this.m_Invert.setEnabled(false);
/* 207:349 */     this.m_Invert.addActionListener(new ActionListener()
/* 208:    */     {
/* 209:    */       public void actionPerformed(ActionEvent e)
/* 210:    */       {
/* 211:352 */         AttributeSelectionPanel.this.m_Model.invert();
/* 212:    */       }
/* 213:354 */     });
/* 214:355 */     this.m_Pattern.setToolTipText("Selects all attributes that match a reg. expression");
/* 215:    */     
/* 216:357 */     this.m_Pattern.setEnabled(false);
/* 217:358 */     this.m_Pattern.addActionListener(new ActionListener()
/* 218:    */     {
/* 219:    */       public void actionPerformed(ActionEvent e)
/* 220:    */       {
/* 221:361 */         String pattern = JOptionPane.showInputDialog(AttributeSelectionPanel.this.m_Pattern.getParent(), "Enter a Perl regular expression", AttributeSelectionPanel.this.m_PatternRegEx);
/* 222:363 */         if (pattern != null) {
/* 223:    */           try
/* 224:    */           {
/* 225:365 */             Pattern.compile(pattern);
/* 226:366 */             AttributeSelectionPanel.this.m_PatternRegEx = pattern;
/* 227:367 */             AttributeSelectionPanel.this.m_Model.pattern(pattern);
/* 228:    */           }
/* 229:    */           catch (Exception ex)
/* 230:    */           {
/* 231:369 */             JOptionPane.showMessageDialog(AttributeSelectionPanel.this.m_Pattern.getParent(), "'" + pattern + "' is not a valid Perl regular expression!\n" + "Error: " + ex, "Error in Pattern...", 0);
/* 232:    */           }
/* 233:    */         }
/* 234:    */       }
/* 235:375 */     });
/* 236:376 */     this.m_Table.setSelectionMode(0);
/* 237:377 */     this.m_Table.setColumnSelectionAllowed(false);
/* 238:378 */     this.m_Table.setPreferredScrollableViewportSize(new Dimension(250, 150));
/* 239:    */     
/* 240:    */ 
/* 241:381 */     JPanel p1 = new JPanel();
/* 242:382 */     p1.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
/* 243:383 */     p1.setLayout(new GridLayout(1, 4, 5, 5));
/* 244:384 */     if (include) {
/* 245:385 */       p1.add(this.m_IncludeAll);
/* 246:    */     }
/* 247:387 */     if (remove) {
/* 248:388 */       p1.add(this.m_RemoveAll);
/* 249:    */     }
/* 250:390 */     if (invert) {
/* 251:391 */       p1.add(this.m_Invert);
/* 252:    */     }
/* 253:393 */     if (pattern) {
/* 254:394 */       p1.add(this.m_Pattern);
/* 255:    */     }
/* 256:397 */     setLayout(new BorderLayout());
/* 257:398 */     if ((include) || (remove) || (invert) || (pattern)) {
/* 258:399 */       add(p1, "North");
/* 259:    */     }
/* 260:401 */     add(new JScrollPane(this.m_Table), "Center");
/* 261:    */   }
/* 262:    */   
/* 263:    */   public Dimension getPreferredScrollableViewportSize()
/* 264:    */   {
/* 265:405 */     return this.m_Table.getPreferredScrollableViewportSize();
/* 266:    */   }
/* 267:    */   
/* 268:    */   public void setPreferredScrollableViewportSize(Dimension d)
/* 269:    */   {
/* 270:409 */     this.m_Table.setPreferredScrollableViewportSize(d);
/* 271:    */   }
/* 272:    */   
/* 273:    */   public void setInstances(Instances newInstances)
/* 274:    */   {
/* 275:419 */     if (this.m_Model == null)
/* 276:    */     {
/* 277:420 */       this.m_Model = new AttributeTableModel(newInstances);
/* 278:421 */       this.m_Table.setModel(this.m_Model);
/* 279:422 */       TableColumnModel tcm = this.m_Table.getColumnModel();
/* 280:423 */       tcm.getColumn(0).setMaxWidth(60);
/* 281:424 */       tcm.getColumn(1).setMaxWidth(tcm.getColumn(1).getMinWidth());
/* 282:425 */       tcm.getColumn(2).setMinWidth(100);
/* 283:    */     }
/* 284:    */     else
/* 285:    */     {
/* 286:427 */       this.m_Model.setInstances(newInstances);
/* 287:428 */       this.m_Table.clearSelection();
/* 288:    */     }
/* 289:430 */     this.m_IncludeAll.setEnabled(true);
/* 290:431 */     this.m_RemoveAll.setEnabled(true);
/* 291:432 */     this.m_Invert.setEnabled(true);
/* 292:433 */     this.m_Pattern.setEnabled(true);
/* 293:434 */     this.m_Table.sizeColumnsToFit(2);
/* 294:435 */     this.m_Table.revalidate();
/* 295:436 */     this.m_Table.repaint();
/* 296:    */   }
/* 297:    */   
/* 298:    */   public int[] getSelectedAttributes()
/* 299:    */   {
/* 300:446 */     return this.m_Model == null ? null : this.m_Model.getSelectedAttributes();
/* 301:    */   }
/* 302:    */   
/* 303:    */   public void setSelectedAttributes(boolean[] selected)
/* 304:    */     throws Exception
/* 305:    */   {
/* 306:459 */     if (this.m_Model != null) {
/* 307:460 */       this.m_Model.setSelectedAttributes(selected);
/* 308:    */     }
/* 309:    */   }
/* 310:    */   
/* 311:    */   public TableModel getTableModel()
/* 312:    */   {
/* 313:470 */     return this.m_Model;
/* 314:    */   }
/* 315:    */   
/* 316:    */   public ListSelectionModel getSelectionModel()
/* 317:    */   {
/* 318:480 */     return this.m_Table.getSelectionModel();
/* 319:    */   }
/* 320:    */   
/* 321:    */   public static void main(String[] args)
/* 322:    */   {
/* 323:    */     try
/* 324:    */     {
/* 325:491 */       if (args.length == 0) {
/* 326:492 */         throw new Exception("supply the name of an arff file");
/* 327:    */       }
/* 328:494 */       Instances i = new Instances(new BufferedReader(new FileReader(args[0])));
/* 329:    */       
/* 330:496 */       AttributeSelectionPanel asp = new AttributeSelectionPanel();
/* 331:497 */       JFrame jf = new JFrame("Attribute Selection Panel");
/* 332:    */       
/* 333:499 */       jf.getContentPane().setLayout(new BorderLayout());
/* 334:500 */       jf.getContentPane().add(asp, "Center");
/* 335:501 */       jf.addWindowListener(new WindowAdapter()
/* 336:    */       {
/* 337:    */         public void windowClosing(WindowEvent e)
/* 338:    */         {
/* 339:504 */           this.val$jf.dispose();
/* 340:505 */           System.exit(0);
/* 341:    */         }
/* 342:507 */       });
/* 343:508 */       jf.pack();
/* 344:509 */       jf.setVisible(true);
/* 345:510 */       asp.setInstances(i);
/* 346:    */     }
/* 347:    */     catch (Exception ex)
/* 348:    */     {
/* 349:512 */       ex.printStackTrace();
/* 350:513 */       System.err.println(ex.getMessage());
/* 351:    */     }
/* 352:    */   }
/* 353:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.AttributeSelectionPanel
 * JD-Core Version:    0.7.0.1
 */