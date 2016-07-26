/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.event.MouseAdapter;
/*   4:    */ import java.awt.event.MouseEvent;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.Collections;
/*   8:    */ import javax.swing.JTable;
/*   9:    */ import javax.swing.event.TableModelEvent;
/*  10:    */ import javax.swing.event.TableModelListener;
/*  11:    */ import javax.swing.table.AbstractTableModel;
/*  12:    */ import javax.swing.table.JTableHeader;
/*  13:    */ import javax.swing.table.TableColumnModel;
/*  14:    */ import javax.swing.table.TableModel;
/*  15:    */ import weka.core.ClassDiscovery;
/*  16:    */ 
/*  17:    */ public class SortedTableModel
/*  18:    */   extends AbstractTableModel
/*  19:    */   implements TableModelListener
/*  20:    */ {
/*  21:    */   static final long serialVersionUID = 4030907921461127548L;
/*  22:    */   protected TableModel mModel;
/*  23:    */   protected int[] mIndices;
/*  24:    */   protected int mSortColumn;
/*  25:    */   protected boolean mAscending;
/*  26:    */   
/*  27:    */   public static class SortContainer
/*  28:    */     implements Comparable<SortContainer>
/*  29:    */   {
/*  30:    */     protected Comparable<?> m_Value;
/*  31:    */     protected int m_Index;
/*  32:    */     
/*  33:    */     public SortContainer(Comparable<?> value, int index)
/*  34:    */     {
/*  35: 73 */       this.m_Value = value;
/*  36: 74 */       this.m_Index = index;
/*  37:    */     }
/*  38:    */     
/*  39:    */     public Comparable<?> getValue()
/*  40:    */     {
/*  41: 83 */       return this.m_Value;
/*  42:    */     }
/*  43:    */     
/*  44:    */     public int getIndex()
/*  45:    */     {
/*  46: 92 */       return this.m_Index;
/*  47:    */     }
/*  48:    */     
/*  49:    */     public int compareTo(SortContainer o)
/*  50:    */     {
/*  51:110 */       if ((this.m_Value == null) || (o.getValue() == null))
/*  52:    */       {
/*  53:111 */         if (this.m_Value == o.getValue()) {
/*  54:112 */           return 0;
/*  55:    */         }
/*  56:114 */         if (this.m_Value == null) {
/*  57:115 */           return -1;
/*  58:    */         }
/*  59:117 */         return 1;
/*  60:    */       }
/*  61:120 */       return this.m_Value.compareTo(o.getValue());
/*  62:    */     }
/*  63:    */     
/*  64:    */     public boolean equals(Object obj)
/*  65:    */     {
/*  66:135 */       return compareTo((SortContainer)obj) == 0;
/*  67:    */     }
/*  68:    */     
/*  69:    */     public String toString()
/*  70:    */     {
/*  71:145 */       return "value=" + this.m_Value + ", index=" + this.m_Index;
/*  72:    */     }
/*  73:    */   }
/*  74:    */   
/*  75:    */   public SortedTableModel()
/*  76:    */   {
/*  77:165 */     this(null);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public SortedTableModel(TableModel model)
/*  81:    */   {
/*  82:174 */     setModel(model);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setModel(TableModel value)
/*  86:    */   {
/*  87:183 */     this.mModel = value;
/*  88:186 */     if (this.mModel == null)
/*  89:    */     {
/*  90:187 */       this.mIndices = null;
/*  91:    */     }
/*  92:    */     else
/*  93:    */     {
/*  94:189 */       initializeIndices();
/*  95:190 */       this.mSortColumn = -1;
/*  96:191 */       this.mAscending = true;
/*  97:192 */       this.mModel.addTableModelListener(this);
/*  98:    */     }
/*  99:    */   }
/* 100:    */   
/* 101:    */   protected void initializeIndices()
/* 102:    */   {
/* 103:202 */     this.mIndices = new int[this.mModel.getRowCount()];
/* 104:203 */     for (int i = 0; i < this.mIndices.length; i++) {
/* 105:204 */       this.mIndices[i] = i;
/* 106:    */     }
/* 107:    */   }
/* 108:    */   
/* 109:    */   public TableModel getModel()
/* 110:    */   {
/* 111:214 */     return this.mModel;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public boolean isSorted()
/* 115:    */   {
/* 116:223 */     return this.mSortColumn > -1;
/* 117:    */   }
/* 118:    */   
/* 119:    */   protected boolean isInitialized()
/* 120:    */   {
/* 121:233 */     return getModel() != null;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public int getActualRow(int visibleRow)
/* 125:    */   {
/* 126:244 */     if (!isInitialized()) {
/* 127:245 */       return -1;
/* 128:    */     }
/* 129:247 */     return this.mIndices[visibleRow];
/* 130:    */   }
/* 131:    */   
/* 132:    */   public Class<?> getColumnClass(int columnIndex)
/* 133:    */   {
/* 134:259 */     if (!isInitialized()) {
/* 135:260 */       return null;
/* 136:    */     }
/* 137:262 */     return getModel().getColumnClass(columnIndex);
/* 138:    */   }
/* 139:    */   
/* 140:    */   public int getColumnCount()
/* 141:    */   {
/* 142:273 */     if (!isInitialized()) {
/* 143:274 */       return 0;
/* 144:    */     }
/* 145:276 */     return getModel().getColumnCount();
/* 146:    */   }
/* 147:    */   
/* 148:    */   public String getColumnName(int columnIndex)
/* 149:    */   {
/* 150:288 */     if (!isInitialized()) {
/* 151:289 */       return null;
/* 152:    */     }
/* 153:291 */     return getModel().getColumnName(columnIndex);
/* 154:    */   }
/* 155:    */   
/* 156:    */   public int getRowCount()
/* 157:    */   {
/* 158:302 */     if (!isInitialized()) {
/* 159:303 */       return 0;
/* 160:    */     }
/* 161:305 */     return getModel().getRowCount();
/* 162:    */   }
/* 163:    */   
/* 164:    */   public Object getValueAt(int rowIndex, int columnIndex)
/* 165:    */   {
/* 166:318 */     if (!isInitialized()) {
/* 167:319 */       return null;
/* 168:    */     }
/* 169:321 */     return getModel().getValueAt(this.mIndices[rowIndex], columnIndex);
/* 170:    */   }
/* 171:    */   
/* 172:    */   public boolean isCellEditable(int rowIndex, int columnIndex)
/* 173:    */   {
/* 174:334 */     if (!isInitialized()) {
/* 175:335 */       return false;
/* 176:    */     }
/* 177:337 */     return getModel().isCellEditable(this.mIndices[rowIndex], columnIndex);
/* 178:    */   }
/* 179:    */   
/* 180:    */   public void setValueAt(Object aValue, int rowIndex, int columnIndex)
/* 181:    */   {
/* 182:350 */     if (isInitialized()) {
/* 183:351 */       getModel().setValueAt(aValue, this.mIndices[rowIndex], columnIndex);
/* 184:    */     }
/* 185:    */   }
/* 186:    */   
/* 187:    */   public void sort(int columnIndex)
/* 188:    */   {
/* 189:361 */     sort(columnIndex, true);
/* 190:    */   }
/* 191:    */   
/* 192:    */   public void sort(int columnIndex, boolean ascending)
/* 193:    */   {
/* 194:378 */     if ((!isInitialized()) || (getModel().getRowCount() != this.mIndices.length))
/* 195:    */     {
/* 196:380 */       System.out.println(getClass().getName() + ": Table model not initialized!");
/* 197:    */       
/* 198:    */ 
/* 199:383 */       return;
/* 200:    */     }
/* 201:387 */     this.mSortColumn = columnIndex;
/* 202:388 */     this.mAscending = ascending;
/* 203:389 */     initializeIndices();
/* 204:    */     int columnType;
/* 205:    */     int columnType;
/* 206:392 */     if (ClassDiscovery.hasInterface(Comparable.class, getColumnClass(this.mSortColumn))) {
/* 207:394 */       columnType = 1;
/* 208:    */     } else {
/* 209:396 */       columnType = 0;
/* 210:    */     }
/* 211:400 */     ArrayList<SortContainer> sorted = new ArrayList();
/* 212:401 */     for (int i = 0; i < getRowCount(); i++)
/* 213:    */     {
/* 214:402 */       Object value = this.mModel.getValueAt(this.mIndices[i], this.mSortColumn);
/* 215:    */       SortContainer cont;
/* 216:    */       SortContainer cont;
/* 217:403 */       if (columnType == 0) {
/* 218:404 */         cont = new SortContainer(value == null ? null : value.toString(), this.mIndices[i]);
/* 219:    */       } else {
/* 220:407 */         cont = new SortContainer((Comparable)value, this.mIndices[i]);
/* 221:    */       }
/* 222:409 */       sorted.add(cont);
/* 223:    */     }
/* 224:411 */     Collections.sort(sorted);
/* 225:413 */     for (i = 0; i < sorted.size(); i++) {
/* 226:414 */       if (this.mAscending) {
/* 227:415 */         this.mIndices[i] = ((SortContainer)sorted.get(i)).getIndex();
/* 228:    */       } else {
/* 229:417 */         this.mIndices[i] = ((SortContainer)sorted.get(sorted.size() - 1 - i)).getIndex();
/* 230:    */       }
/* 231:    */     }
/* 232:421 */     sorted.clear();
/* 233:422 */     sorted = null;
/* 234:    */   }
/* 235:    */   
/* 236:    */   public void tableChanged(TableModelEvent e)
/* 237:    */   {
/* 238:433 */     initializeIndices();
/* 239:434 */     if (isSorted()) {
/* 240:435 */       sort(this.mSortColumn, this.mAscending);
/* 241:    */     }
/* 242:438 */     fireTableChanged(e);
/* 243:    */   }
/* 244:    */   
/* 245:    */   public void addMouseListenerToHeader(JTable table)
/* 246:    */   {
/* 247:448 */     final SortedTableModel modelFinal = this;
/* 248:449 */     final JTable tableFinal = table;
/* 249:450 */     tableFinal.setColumnSelectionAllowed(false);
/* 250:451 */     JTableHeader header = tableFinal.getTableHeader();
/* 251:453 */     if (header != null)
/* 252:    */     {
/* 253:454 */       MouseAdapter listMouseListener = new MouseAdapter()
/* 254:    */       {
/* 255:    */         public void mouseClicked(MouseEvent e)
/* 256:    */         {
/* 257:457 */           TableColumnModel columnModel = tableFinal.getColumnModel();
/* 258:458 */           int viewColumn = columnModel.getColumnIndexAtX(e.getX());
/* 259:459 */           int column = tableFinal.convertColumnIndexToModel(viewColumn);
/* 260:460 */           if ((e.getButton() == 1) && (e.getClickCount() == 1) && (!e.isAltDown()) && (column != -1))
/* 261:    */           {
/* 262:462 */             int shiftPressed = e.getModifiers() & 0x1;
/* 263:463 */             boolean ascending = shiftPressed == 0;
/* 264:464 */             modelFinal.sort(column, ascending);
/* 265:    */           }
/* 266:    */         }
/* 267:468 */       };
/* 268:469 */       header.addMouseListener(listMouseListener);
/* 269:    */     }
/* 270:    */   }
/* 271:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.SortedTableModel
 * JD-Core Version:    0.7.0.1
 */