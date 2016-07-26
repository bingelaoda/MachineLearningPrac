/*   1:    */ package weka.gui.sql;
/*   2:    */ 
/*   3:    */ import java.sql.ResultSet;
/*   4:    */ import java.sql.Statement;
/*   5:    */ import java.util.HashSet;
/*   6:    */ import javax.swing.event.TableModelListener;
/*   7:    */ import javax.swing.table.TableModel;
/*   8:    */ 
/*   9:    */ public class ResultSetTableModel
/*  10:    */   implements TableModel
/*  11:    */ {
/*  12:    */   protected HashSet<TableModelListener> m_Listeners;
/*  13:    */   protected Object[][] m_Data;
/*  14:    */   protected ResultSetHelper m_Helper;
/*  15:    */   
/*  16:    */   public ResultSetTableModel(ResultSet rs)
/*  17:    */   {
/*  18: 53 */     this(rs, 0);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public ResultSetTableModel(ResultSet rs, int rows)
/*  22:    */   {
/*  23: 66 */     this.m_Listeners = new HashSet();
/*  24: 67 */     this.m_Helper = new ResultSetHelper(rs, rows);
/*  25: 68 */     this.m_Data = this.m_Helper.getCells();
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void addTableModelListener(TableModelListener l)
/*  29:    */   {
/*  30: 79 */     this.m_Listeners.add(l);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public Class<?> getColumnClass(int columnIndex)
/*  34:    */   {
/*  35: 93 */     Class<?> result = null;
/*  36: 95 */     if ((this.m_Helper.getColumnClasses() != null) && (columnIndex >= 0) && (columnIndex < getColumnCount())) {
/*  37: 97 */       if (columnIndex == 0) {
/*  38: 98 */         result = Integer.class;
/*  39:    */       } else {
/*  40:100 */         result = this.m_Helper.getColumnClasses()[(columnIndex - 1)];
/*  41:    */       }
/*  42:    */     }
/*  43:104 */     return result;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public int getColumnCount()
/*  47:    */   {
/*  48:114 */     return this.m_Helper.getColumnCount() + 1;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public String getColumnName(int columnIndex)
/*  52:    */   {
/*  53:127 */     String result = "";
/*  54:129 */     if ((this.m_Helper.getColumnNames() != null) && (columnIndex >= 0) && (columnIndex < getColumnCount())) {
/*  55:131 */       if (columnIndex == 0) {
/*  56:132 */         result = "Row";
/*  57:    */       } else {
/*  58:134 */         result = this.m_Helper.getColumnNames()[(columnIndex - 1)];
/*  59:    */       }
/*  60:    */     }
/*  61:138 */     return result;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public int getRowCount()
/*  65:    */   {
/*  66:148 */     return this.m_Data.length;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public Object getValueAt(int rowIndex, int columnIndex)
/*  70:    */   {
/*  71:162 */     Object result = null;
/*  72:164 */     if ((rowIndex >= 0) && (rowIndex < getRowCount()) && (columnIndex >= 0) && (columnIndex < getColumnCount())) {
/*  73:166 */       if (columnIndex == 0) {
/*  74:167 */         result = new Integer(rowIndex + 1);
/*  75:    */       } else {
/*  76:169 */         result = this.m_Data[rowIndex][(columnIndex - 1)];
/*  77:    */       }
/*  78:    */     }
/*  79:173 */     return result;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public boolean isNullAt(int rowIndex, int columnIndex)
/*  83:    */   {
/*  84:184 */     return getValueAt(rowIndex, columnIndex) == null;
/*  85:    */   }
/*  86:    */   
/*  87:    */   public boolean isNumericAt(int columnIndex)
/*  88:    */   {
/*  89:196 */     boolean result = false;
/*  90:198 */     if ((columnIndex >= 0) && (columnIndex < getColumnCount())) {
/*  91:199 */       if (columnIndex == 0) {
/*  92:200 */         result = true;
/*  93:202 */       } else if (this.m_Helper.getNumericColumns() == null) {
/*  94:203 */         result = false;
/*  95:    */       } else {
/*  96:205 */         result = this.m_Helper.getNumericColumns()[(columnIndex - 1)];
/*  97:    */       }
/*  98:    */     }
/*  99:210 */     return result;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public boolean isCellEditable(int rowIndex, int columnIndex)
/* 103:    */   {
/* 104:222 */     return false;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void removeTableModelListener(TableModelListener l)
/* 108:    */   {
/* 109:233 */     this.m_Listeners.remove(l);
/* 110:    */   }
/* 111:    */   
/* 112:    */   public void setValueAt(Object aValue, int rowIndex, int columnIndex) {}
/* 113:    */   
/* 114:    */   public void finalize()
/* 115:    */     throws Throwable
/* 116:    */   {
/* 117:    */     try
/* 118:    */     {
/* 119:256 */       this.m_Helper.getResultSet().close();
/* 120:257 */       this.m_Helper.getResultSet().getStatement().close();
/* 121:258 */       this.m_Helper = null;
/* 122:    */     }
/* 123:    */     catch (Exception e) {}
/* 124:263 */     this.m_Data = ((Object[][])null);
/* 125:    */     
/* 126:265 */     super.finalize();
/* 127:    */   }
/* 128:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.ResultSetTableModel
 * JD-Core Version:    0.7.0.1
 */