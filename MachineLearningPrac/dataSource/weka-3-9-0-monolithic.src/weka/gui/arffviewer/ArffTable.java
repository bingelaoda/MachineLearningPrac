/*   1:    */ package weka.gui.arffviewer;
/*   2:    */ 
/*   3:    */ import java.awt.Component;
/*   4:    */ import java.awt.datatransfer.StringSelection;
/*   5:    */ import java.awt.event.ActionEvent;
/*   6:    */ import java.awt.event.ActionListener;
/*   7:    */ import java.util.Enumeration;
/*   8:    */ import java.util.HashSet;
/*   9:    */ import java.util.Iterator;
/*  10:    */ import javax.swing.AbstractCellEditor;
/*  11:    */ import javax.swing.DefaultCellEditor;
/*  12:    */ import javax.swing.JButton;
/*  13:    */ import javax.swing.JComboBox;
/*  14:    */ import javax.swing.JTable;
/*  15:    */ import javax.swing.ListSelectionModel;
/*  16:    */ import javax.swing.event.ChangeEvent;
/*  17:    */ import javax.swing.event.ChangeListener;
/*  18:    */ import javax.swing.event.TableModelEvent;
/*  19:    */ import javax.swing.table.JTableHeader;
/*  20:    */ import javax.swing.table.TableCellEditor;
/*  21:    */ import javax.swing.table.TableColumn;
/*  22:    */ import javax.swing.table.TableColumnModel;
/*  23:    */ import javax.swing.table.TableModel;
/*  24:    */ import weka.core.Attribute;
/*  25:    */ import weka.core.Instances;
/*  26:    */ import weka.core.SerializedObject;
/*  27:    */ import weka.core.converters.AbstractFileLoader;
/*  28:    */ import weka.gui.ComponentHelper;
/*  29:    */ import weka.gui.JTableHelper;
/*  30:    */ import weka.gui.ViewerDialog;
/*  31:    */ 
/*  32:    */ public class ArffTable
/*  33:    */   extends JTable
/*  34:    */ {
/*  35:    */   static final long serialVersionUID = -2016200506908637967L;
/*  36:    */   private String m_SearchString;
/*  37:    */   private HashSet<ChangeListener> m_ChangeListeners;
/*  38:    */   
/*  39:    */   protected class RelationalCellEditor
/*  40:    */     extends AbstractCellEditor
/*  41:    */     implements TableCellEditor
/*  42:    */   {
/*  43:    */     private static final long serialVersionUID = 657969163293205963L;
/*  44:    */     protected JButton m_Button;
/*  45:    */     protected Instances m_CurrentInst;
/*  46:    */     protected int m_RowIndex;
/*  47:    */     protected int m_ColumnIndex;
/*  48:    */     
/*  49:    */     public RelationalCellEditor(int rowIndex, int columnIndex)
/*  50:    */     {
/*  51: 93 */       this.m_CurrentInst = getInstancesAt(rowIndex, columnIndex);
/*  52: 94 */       this.m_RowIndex = rowIndex;
/*  53: 95 */       this.m_ColumnIndex = columnIndex;
/*  54:    */       
/*  55: 97 */       this.m_Button = new JButton("...");
/*  56: 98 */       this.m_Button.addActionListener(new ActionListener()
/*  57:    */       {
/*  58:    */         public void actionPerformed(ActionEvent evt)
/*  59:    */         {
/*  60:104 */           ViewerDialog dialog = new ViewerDialog(null);
/*  61:105 */           dialog.setTitle("Relational attribute Viewer - " + ((ArffSortedTableModel)ArffTable.this.getModel()).getInstances().attribute(ArffTable.RelationalCellEditor.this.m_ColumnIndex - 1).name());
/*  62:    */           
/*  63:    */ 
/*  64:108 */           int result = dialog.showDialog(ArffTable.RelationalCellEditor.this.m_CurrentInst);
/*  65:109 */           if (result == 0)
/*  66:    */           {
/*  67:110 */             ArffTable.RelationalCellEditor.this.m_CurrentInst = dialog.getInstances();
/*  68:111 */             ArffTable.RelationalCellEditor.this.fireEditingStopped();
/*  69:    */           }
/*  70:    */           else
/*  71:    */           {
/*  72:113 */             ArffTable.RelationalCellEditor.this.fireEditingCanceled();
/*  73:    */           }
/*  74:    */         }
/*  75:    */       });
/*  76:    */     }
/*  77:    */     
/*  78:    */     protected Instances getInstancesAt(int rowIndex, int columnIndex)
/*  79:    */     {
/*  80:131 */       ArffSortedTableModel model = (ArffSortedTableModel)ArffTable.this.getModel();
/*  81:132 */       double value = model.getInstancesValueAt(rowIndex, columnIndex);
/*  82:133 */       Instances result = model.getInstances().attribute(columnIndex - 1).relation((int)value);
/*  83:    */       
/*  84:    */ 
/*  85:136 */       return result;
/*  86:    */     }
/*  87:    */     
/*  88:    */     public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
/*  89:    */     {
/*  90:154 */       return this.m_Button;
/*  91:    */     }
/*  92:    */     
/*  93:    */     public Object getCellEditorValue()
/*  94:    */     {
/*  95:164 */       return this.m_CurrentInst;
/*  96:    */     }
/*  97:    */   }
/*  98:    */   
/*  99:    */   public ArffTable()
/* 100:    */   {
/* 101:177 */     this(new ArffSortedTableModel("", new AbstractFileLoader[0]));
/* 102:    */   }
/* 103:    */   
/* 104:    */   public ArffTable(TableModel model)
/* 105:    */   {
/* 106:186 */     super(model);
/* 107:    */     
/* 108:188 */     setAutoResizeMode(0);
/* 109:    */   }
/* 110:    */   
/* 111:    */   public void setModel(TableModel model)
/* 112:    */   {
/* 113:201 */     this.m_SearchString = null;
/* 114:204 */     if (this.m_ChangeListeners == null) {
/* 115:205 */       this.m_ChangeListeners = new HashSet();
/* 116:    */     }
/* 117:208 */     super.setModel(model);
/* 118:210 */     if (model == null) {
/* 119:211 */       return;
/* 120:    */     }
/* 121:214 */     if (!(model instanceof ArffSortedTableModel)) {
/* 122:215 */       return;
/* 123:    */     }
/* 124:218 */     ArffSortedTableModel arffModel = (ArffSortedTableModel)model;
/* 125:219 */     arffModel.addMouseListenerToHeader(this);
/* 126:220 */     arffModel.addTableModelListener(this);
/* 127:221 */     arffModel.sort(0);
/* 128:222 */     setLayout();
/* 129:223 */     setSelectedColumn(0);
/* 130:226 */     if (getTableHeader() != null) {
/* 131:227 */       getTableHeader().setReorderingAllowed(false);
/* 132:    */     }
/* 133:    */   }
/* 134:    */   
/* 135:    */   public TableCellEditor getCellEditor(int row, int column)
/* 136:    */   {
/* 137:    */     TableCellEditor result;
/* 138:    */     TableCellEditor result;
/* 139:243 */     if (((getModel() instanceof ArffSortedTableModel)) && (((ArffSortedTableModel)getModel()).getType(column) == 4)) {
/* 140:245 */       result = new RelationalCellEditor(row, column);
/* 141:    */     } else {
/* 142:248 */       result = super.getCellEditor(row, column);
/* 143:    */     }
/* 144:251 */     return result;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public boolean isReadOnly()
/* 148:    */   {
/* 149:260 */     return ((ArffSortedTableModel)getModel()).isReadOnly();
/* 150:    */   }
/* 151:    */   
/* 152:    */   public void setReadOnly(boolean value)
/* 153:    */   {
/* 154:269 */     ((ArffSortedTableModel)getModel()).setReadOnly(value);
/* 155:    */   }
/* 156:    */   
/* 157:    */   private void setLayout()
/* 158:    */   {
/* 159:281 */     ArffSortedTableModel arffModel = (ArffSortedTableModel)getModel();
/* 160:283 */     for (int i = 0; i < getColumnCount(); i++)
/* 161:    */     {
/* 162:285 */       JTableHelper.setOptimalHeaderWidth(this, i);
/* 163:    */       
/* 164:    */ 
/* 165:288 */       getColumnModel().getColumn(i).setCellRenderer(new ArffTableCellRenderer());
/* 166:292 */       if (i > 0) {
/* 167:293 */         if (arffModel.getType(i) == 1)
/* 168:    */         {
/* 169:294 */           JComboBox combo = new JComboBox();
/* 170:295 */           combo.addItem(null);
/* 171:296 */           Enumeration<Object> enm = arffModel.getInstances().attribute(i - 1).enumerateValues();
/* 172:297 */           while (enm.hasMoreElements())
/* 173:    */           {
/* 174:298 */             Object o = enm.nextElement();
/* 175:299 */             if ((o instanceof SerializedObject)) {
/* 176:300 */               ((SerializedObject)o).getObject();
/* 177:    */             }
/* 178:302 */             combo.addItem(o);
/* 179:    */           }
/* 180:304 */           getColumnModel().getColumn(i).setCellEditor(new DefaultCellEditor(combo));
/* 181:    */         }
/* 182:    */         else
/* 183:    */         {
/* 184:307 */           getColumnModel().getColumn(i).setCellEditor(null);
/* 185:    */         }
/* 186:    */       }
/* 187:    */     }
/* 188:    */   }
/* 189:    */   
/* 190:    */   public String getPlainColumnName(int columnIndex)
/* 191:    */   {
/* 192:324 */     String result = "";
/* 193:326 */     if (getModel() == null) {
/* 194:327 */       return result;
/* 195:    */     }
/* 196:329 */     if (!(getModel() instanceof ArffSortedTableModel)) {
/* 197:330 */       return result;
/* 198:    */     }
/* 199:333 */     ArffSortedTableModel arffModel = (ArffSortedTableModel)getModel();
/* 200:335 */     if ((columnIndex >= 0) && (columnIndex < getColumnCount())) {
/* 201:336 */       if (columnIndex == 0) {
/* 202:337 */         result = "No.";
/* 203:    */       } else {
/* 204:339 */         result = arffModel.getAttributeAt(columnIndex).name();
/* 205:    */       }
/* 206:    */     }
/* 207:343 */     return result;
/* 208:    */   }
/* 209:    */   
/* 210:    */   public StringSelection getStringSelection()
/* 211:    */   {
/* 212:360 */     StringSelection result = null;
/* 213:363 */     if (getSelectedRow() == -1)
/* 214:    */     {
/* 215:365 */       if (ComponentHelper.showMessageBox(getParent(), "Question...", "Do you really want to copy the whole table?", 0, 3) != 0) {
/* 216:368 */         return result;
/* 217:    */       }
/* 218:371 */       int[] indices = new int[getRowCount()];
/* 219:372 */       for (int i = 0; i < indices.length; i++) {
/* 220:373 */         indices[i] = i;
/* 221:    */       }
/* 222:    */     }
/* 223:376 */     int[] indices = getSelectedRows();
/* 224:    */     
/* 225:    */ 
/* 226:    */ 
/* 227:380 */     StringBuffer tmp = new StringBuffer();
/* 228:381 */     for (int i = 0; i < getColumnCount(); i++)
/* 229:    */     {
/* 230:382 */       if (i > 0) {
/* 231:383 */         tmp.append("\t");
/* 232:    */       }
/* 233:385 */       tmp.append(getPlainColumnName(i));
/* 234:    */     }
/* 235:387 */     tmp.append("\n");
/* 236:390 */     for (i = 0; i < indices.length; i++)
/* 237:    */     {
/* 238:391 */       for (int n = 0; n < getColumnCount(); n++)
/* 239:    */       {
/* 240:392 */         if (n > 0) {
/* 241:393 */           tmp.append("\t");
/* 242:    */         }
/* 243:395 */         tmp.append(getValueAt(indices[i], n).toString());
/* 244:    */       }
/* 245:397 */       tmp.append("\n");
/* 246:    */     }
/* 247:400 */     result = new StringSelection(tmp.toString());
/* 248:    */     
/* 249:402 */     return result;
/* 250:    */   }
/* 251:    */   
/* 252:    */   public void setSearchString(String searchString)
/* 253:    */   {
/* 254:412 */     this.m_SearchString = searchString;
/* 255:413 */     repaint();
/* 256:    */   }
/* 257:    */   
/* 258:    */   public String getSearchString()
/* 259:    */   {
/* 260:422 */     return this.m_SearchString;
/* 261:    */   }
/* 262:    */   
/* 263:    */   public void setSelectedColumn(int index)
/* 264:    */   {
/* 265:431 */     getColumnModel().getSelectionModel().clearSelection();
/* 266:432 */     getColumnModel().getSelectionModel().setSelectionInterval(index, index);
/* 267:433 */     resizeAndRepaint();
/* 268:434 */     if (getTableHeader() != null) {
/* 269:435 */       getTableHeader().resizeAndRepaint();
/* 270:    */     }
/* 271:    */   }
/* 272:    */   
/* 273:    */   public void tableChanged(TableModelEvent e)
/* 274:    */   {
/* 275:447 */     super.tableChanged(e);
/* 276:    */     
/* 277:449 */     setLayout();
/* 278:450 */     notifyListener();
/* 279:    */   }
/* 280:    */   
/* 281:    */   private void notifyListener()
/* 282:    */   {
/* 283:459 */     Iterator<ChangeListener> iter = this.m_ChangeListeners.iterator();
/* 284:460 */     while (iter.hasNext()) {
/* 285:461 */       ((ChangeListener)iter.next()).stateChanged(new ChangeEvent(this));
/* 286:    */     }
/* 287:    */   }
/* 288:    */   
/* 289:    */   public void addChangeListener(ChangeListener l)
/* 290:    */   {
/* 291:471 */     this.m_ChangeListeners.add(l);
/* 292:    */   }
/* 293:    */   
/* 294:    */   public void removeChangeListener(ChangeListener l)
/* 295:    */   {
/* 296:480 */     this.m_ChangeListeners.remove(l);
/* 297:    */   }
/* 298:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.arffviewer.ArffTable
 * JD-Core Version:    0.7.0.1
 */