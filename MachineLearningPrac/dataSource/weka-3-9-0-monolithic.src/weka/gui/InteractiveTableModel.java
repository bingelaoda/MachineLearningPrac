/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.List;
/*   6:    */ import javax.swing.table.AbstractTableModel;
/*   7:    */ 
/*   8:    */ public class InteractiveTableModel
/*   9:    */   extends AbstractTableModel
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = -5113873323690309667L;
/*  12:    */   public int m_hidden_index;
/*  13:    */   protected String[] m_columnNames;
/*  14:    */   protected List<List<String>> m_dataVector;
/*  15:    */   
/*  16:    */   public InteractiveTableModel(String[] columnNames)
/*  17:    */   {
/*  18: 56 */     this.m_columnNames = columnNames;
/*  19: 57 */     this.m_dataVector = new ArrayList();
/*  20: 58 */     this.m_hidden_index = (columnNames.length - 1);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public String getColumnName(int column)
/*  24:    */   {
/*  25: 63 */     return this.m_columnNames[column];
/*  26:    */   }
/*  27:    */   
/*  28:    */   public boolean isCellEditable(int row, int column)
/*  29:    */   {
/*  30: 68 */     if (column == this.m_hidden_index) {
/*  31: 69 */       return false;
/*  32:    */     }
/*  33: 71 */     return true;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public Class<?> getColumnClass(int column)
/*  37:    */   {
/*  38: 76 */     return String.class;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public Object getValueAt(int row, int column)
/*  42:    */   {
/*  43: 81 */     if (column >= this.m_columnNames.length) {
/*  44: 82 */       return new Object();
/*  45:    */     }
/*  46: 85 */     List<String> rowData = (List)this.m_dataVector.get(row);
/*  47: 86 */     return rowData.get(column);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setValueAt(Object value, int row, int column)
/*  51:    */   {
/*  52: 91 */     if (column >= this.m_columnNames.length) {
/*  53: 92 */       System.err.println("Invalid index");
/*  54:    */     }
/*  55: 95 */     List<String> rowData = (List)this.m_dataVector.get(row);
/*  56: 96 */     rowData.set(column, value.toString());
/*  57: 97 */     fireTableCellUpdated(row, column);
/*  58:    */   }
/*  59:    */   
/*  60:    */   public int getRowCount()
/*  61:    */   {
/*  62:102 */     return this.m_dataVector.size();
/*  63:    */   }
/*  64:    */   
/*  65:    */   public int getColumnCount()
/*  66:    */   {
/*  67:107 */     return this.m_columnNames.length;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public boolean hasEmptyRow()
/*  71:    */   {
/*  72:116 */     if (this.m_dataVector.size() == 0) {
/*  73:117 */       return false;
/*  74:    */     }
/*  75:120 */     List<String> dataRow = (List)this.m_dataVector.get(this.m_dataVector.size() - 1);
/*  76:121 */     for (String s : dataRow) {
/*  77:122 */       if (s.length() != 0) {
/*  78:123 */         return false;
/*  79:    */       }
/*  80:    */     }
/*  81:127 */     return true;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void addEmptyRow()
/*  85:    */   {
/*  86:134 */     ArrayList<String> empty = new ArrayList();
/*  87:135 */     for (int i = 0; i < this.m_columnNames.length; i++) {
/*  88:136 */       empty.add("");
/*  89:    */     }
/*  90:138 */     this.m_dataVector.add(empty);
/*  91:139 */     fireTableRowsInserted(this.m_dataVector.size() - 1, this.m_dataVector.size() - 1);
/*  92:    */   }
/*  93:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.InteractiveTableModel
 * JD-Core Version:    0.7.0.1
 */