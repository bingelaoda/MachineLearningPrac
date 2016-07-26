/*   1:    */ package weka.gui.arffviewer;
/*   2:    */ 
/*   3:    */ import javax.swing.event.TableModelEvent;
/*   4:    */ import javax.swing.event.TableModelListener;
/*   5:    */ import javax.swing.table.TableModel;
/*   6:    */ import weka.core.Attribute;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.Undoable;
/*   9:    */ import weka.core.converters.AbstractFileLoader;
/*  10:    */ import weka.gui.SortedTableModel;
/*  11:    */ 
/*  12:    */ public class ArffSortedTableModel
/*  13:    */   extends SortedTableModel
/*  14:    */   implements Undoable
/*  15:    */ {
/*  16:    */   static final long serialVersionUID = -5733148376354254030L;
/*  17:    */   
/*  18:    */   public ArffSortedTableModel(String filename, AbstractFileLoader... loaders)
/*  19:    */   {
/*  20: 56 */     this(new ArffTableModel(filename, loaders));
/*  21:    */   }
/*  22:    */   
/*  23:    */   public ArffSortedTableModel(Instances data)
/*  24:    */   {
/*  25: 66 */     this(new ArffTableModel(data));
/*  26:    */   }
/*  27:    */   
/*  28:    */   public ArffSortedTableModel(TableModel model)
/*  29:    */   {
/*  30: 75 */     super(model);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public boolean isNotificationEnabled()
/*  34:    */   {
/*  35: 84 */     return ((ArffTableModel)getModel()).isNotificationEnabled();
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void setNotificationEnabled(boolean enabled)
/*  39:    */   {
/*  40: 93 */     ((ArffTableModel)getModel()).setNotificationEnabled(enabled);
/*  41:    */   }
/*  42:    */   
/*  43:    */   public boolean isUndoEnabled()
/*  44:    */   {
/*  45:102 */     return ((ArffTableModel)getModel()).isUndoEnabled();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setUndoEnabled(boolean enabled)
/*  49:    */   {
/*  50:111 */     ((ArffTableModel)getModel()).setUndoEnabled(enabled);
/*  51:    */   }
/*  52:    */   
/*  53:    */   public boolean isReadOnly()
/*  54:    */   {
/*  55:120 */     return ((ArffTableModel)getModel()).isReadOnly();
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void setReadOnly(boolean value)
/*  59:    */   {
/*  60:129 */     ((ArffTableModel)getModel()).setReadOnly(value);
/*  61:    */   }
/*  62:    */   
/*  63:    */   public double getInstancesValueAt(int rowIndex, int columnIndex)
/*  64:    */   {
/*  65:141 */     return ((ArffTableModel)getModel()).getInstancesValueAt(this.mIndices[rowIndex], columnIndex);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public Object getModelValueAt(int rowIndex, int columnIndex)
/*  69:    */   {
/*  70:154 */     Object result = super.getModel().getValueAt(rowIndex, columnIndex);
/*  71:157 */     if (((ArffTableModel)getModel()).isMissingAt(rowIndex, columnIndex)) {
/*  72:158 */       result = null;
/*  73:    */     }
/*  74:160 */     return result;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public int getType(int columnIndex)
/*  78:    */   {
/*  79:170 */     return ((ArffTableModel)getModel()).getType(this.mIndices.length > 0 ? this.mIndices[0] : -1, columnIndex);
/*  80:    */   }
/*  81:    */   
/*  82:    */   public int getType(int rowIndex, int columnIndex)
/*  83:    */   {
/*  84:181 */     return ((ArffTableModel)getModel()).getType(this.mIndices[rowIndex], columnIndex);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public void deleteAttributeAt(int columnIndex)
/*  88:    */   {
/*  89:190 */     ((ArffTableModel)getModel()).deleteAttributeAt(columnIndex);
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void deleteAttributes(int[] columnIndices)
/*  93:    */   {
/*  94:199 */     ((ArffTableModel)getModel()).deleteAttributes(columnIndices);
/*  95:    */   }
/*  96:    */   
/*  97:    */   public void renameAttributeAt(int columnIndex, String newName)
/*  98:    */   {
/*  99:209 */     ((ArffTableModel)getModel()).renameAttributeAt(columnIndex, newName);
/* 100:    */   }
/* 101:    */   
/* 102:    */   public void attributeAsClassAt(int columnIndex)
/* 103:    */   {
/* 104:218 */     ((ArffTableModel)getModel()).attributeAsClassAt(columnIndex);
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void deleteInstanceAt(int rowIndex)
/* 108:    */   {
/* 109:227 */     ((ArffTableModel)getModel()).deleteInstanceAt(this.mIndices[rowIndex]);
/* 110:    */   }
/* 111:    */   
/* 112:    */   public void insertInstance(int index)
/* 113:    */   {
/* 114:237 */     ((ArffTableModel)getModel()).insertInstance(index);
/* 115:    */   }
/* 116:    */   
/* 117:    */   public void deleteInstances(int[] rowIndices)
/* 118:    */   {
/* 119:249 */     int[] realIndices = new int[rowIndices.length];
/* 120:250 */     for (int i = 0; i < rowIndices.length; i++) {
/* 121:251 */       realIndices[i] = this.mIndices[rowIndices[i]];
/* 122:    */     }
/* 123:253 */     ((ArffTableModel)getModel()).deleteInstances(realIndices);
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void sortInstances(int columnIndex)
/* 127:    */   {
/* 128:262 */     ((ArffTableModel)getModel()).sortInstances(columnIndex);
/* 129:    */   }
/* 130:    */   
/* 131:    */   public void sortInstances(int columnIndex, boolean ascending)
/* 132:    */   {
/* 133:272 */     ((ArffTableModel)getModel()).sortInstances(columnIndex, ascending);
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void sort(int columnIndex, boolean ascending)
/* 137:    */   {
/* 138:282 */     sortInstances(columnIndex, ascending);
/* 139:    */   }
/* 140:    */   
/* 141:    */   public int getAttributeColumn(String name)
/* 142:    */   {
/* 143:292 */     return ((ArffTableModel)getModel()).getAttributeColumn(name);
/* 144:    */   }
/* 145:    */   
/* 146:    */   public boolean isMissingAt(int rowIndex, int columnIndex)
/* 147:    */   {
/* 148:303 */     return ((ArffTableModel)getModel()).isMissingAt(this.mIndices[rowIndex], columnIndex);
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void setInstances(Instances data)
/* 152:    */   {
/* 153:312 */     ((ArffTableModel)getModel()).setInstances(data);
/* 154:    */   }
/* 155:    */   
/* 156:    */   public Instances getInstances()
/* 157:    */   {
/* 158:321 */     return ((ArffTableModel)getModel()).getInstances();
/* 159:    */   }
/* 160:    */   
/* 161:    */   public Attribute getAttributeAt(int columnIndex)
/* 162:    */   {
/* 163:332 */     return ((ArffTableModel)getModel()).getAttributeAt(columnIndex);
/* 164:    */   }
/* 165:    */   
/* 166:    */   public void addTableModelListener(TableModelListener l)
/* 167:    */   {
/* 168:342 */     if (getModel() != null) {
/* 169:343 */       ((ArffTableModel)getModel()).addTableModelListener(l);
/* 170:    */     }
/* 171:    */   }
/* 172:    */   
/* 173:    */   public void removeTableModelListener(TableModelListener l)
/* 174:    */   {
/* 175:353 */     if (getModel() != null) {
/* 176:354 */       ((ArffTableModel)getModel()).removeTableModelListener(l);
/* 177:    */     }
/* 178:    */   }
/* 179:    */   
/* 180:    */   public void notifyListener(TableModelEvent e)
/* 181:    */   {
/* 182:363 */     ((ArffTableModel)getModel()).notifyListener(e);
/* 183:    */   }
/* 184:    */   
/* 185:    */   public void clearUndo()
/* 186:    */   {
/* 187:370 */     ((ArffTableModel)getModel()).clearUndo();
/* 188:    */   }
/* 189:    */   
/* 190:    */   public boolean canUndo()
/* 191:    */   {
/* 192:380 */     return ((ArffTableModel)getModel()).canUndo();
/* 193:    */   }
/* 194:    */   
/* 195:    */   public void undo()
/* 196:    */   {
/* 197:387 */     ((ArffTableModel)getModel()).undo();
/* 198:    */   }
/* 199:    */   
/* 200:    */   public void addUndoPoint()
/* 201:    */   {
/* 202:394 */     ((ArffTableModel)getModel()).addUndoPoint();
/* 203:    */   }
/* 204:    */   
/* 205:    */   public void setShowAttributeIndex(boolean value)
/* 206:    */   {
/* 207:404 */     ((ArffTableModel)getModel()).setShowAttributeIndex(value);
/* 208:    */   }
/* 209:    */   
/* 210:    */   public boolean getShowAttributeIndex()
/* 211:    */   {
/* 212:414 */     return ((ArffTableModel)getModel()).getShowAttributeIndex();
/* 213:    */   }
/* 214:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.arffviewer.ArffSortedTableModel
 * JD-Core Version:    0.7.0.1
 */