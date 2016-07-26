/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Component;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.Point;
/*   6:    */ import java.awt.Rectangle;
/*   7:    */ import java.io.PrintStream;
/*   8:    */ import javax.swing.JTable;
/*   9:    */ import javax.swing.JViewport;
/*  10:    */ import javax.swing.table.JTableHeader;
/*  11:    */ import javax.swing.table.TableCellRenderer;
/*  12:    */ import javax.swing.table.TableColumn;
/*  13:    */ import javax.swing.table.TableColumnModel;
/*  14:    */ import javax.swing.table.TableModel;
/*  15:    */ 
/*  16:    */ public class JTableHelper
/*  17:    */ {
/*  18:    */   private final JTable jtable;
/*  19:    */   
/*  20:    */   public JTableHelper(JTable jtable)
/*  21:    */   {
/*  22: 52 */     this.jtable = jtable;
/*  23:    */   }
/*  24:    */   
/*  25:    */   public JTable getJTable()
/*  26:    */   {
/*  27: 59 */     return this.jtable;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public int calcColumnWidth(int col)
/*  31:    */   {
/*  32: 66 */     return calcColumnWidth(getJTable(), col);
/*  33:    */   }
/*  34:    */   
/*  35:    */   public static int calcColumnWidth(JTable table, int col)
/*  36:    */   {
/*  37: 81 */     int width = calcHeaderWidth(table, col);
/*  38: 82 */     if (width == -1) {
/*  39: 83 */       return width;
/*  40:    */     }
/*  41: 86 */     TableColumnModel columns = table.getColumnModel();
/*  42: 87 */     TableModel data = table.getModel();
/*  43: 88 */     int rowCount = data.getRowCount();
/*  44: 89 */     columns.getColumn(col);
/*  45:    */     try
/*  46:    */     {
/*  47: 91 */       for (int row = rowCount - 1; row >= 0; row--)
/*  48:    */       {
/*  49: 92 */         Component c = table.prepareRenderer(table.getCellRenderer(row, col), row, col);
/*  50:    */         
/*  51: 94 */         width = Math.max(width, c.getPreferredSize().width + 10);
/*  52:    */       }
/*  53:    */     }
/*  54:    */     catch (Exception e)
/*  55:    */     {
/*  56: 97 */       e.printStackTrace();
/*  57:    */     }
/*  58:100 */     return width;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public int calcHeaderWidth(int col)
/*  62:    */   {
/*  63:107 */     return calcHeaderWidth(getJTable(), col);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public static int calcHeaderWidth(JTable table, int col)
/*  67:    */   {
/*  68:119 */     if (table == null) {
/*  69:120 */       return -1;
/*  70:    */     }
/*  71:123 */     if ((col < 0) || (col > table.getColumnCount()))
/*  72:    */     {
/*  73:124 */       System.out.println("invalid col " + col);
/*  74:125 */       return -1;
/*  75:    */     }
/*  76:128 */     JTableHeader header = table.getTableHeader();
/*  77:129 */     TableCellRenderer defaultHeaderRenderer = null;
/*  78:130 */     if (header != null) {
/*  79:131 */       defaultHeaderRenderer = header.getDefaultRenderer();
/*  80:    */     }
/*  81:133 */     TableColumnModel columns = table.getColumnModel();
/*  82:134 */     table.getModel();
/*  83:135 */     TableColumn column = columns.getColumn(col);
/*  84:136 */     int width = -1;
/*  85:137 */     TableCellRenderer h = column.getHeaderRenderer();
/*  86:138 */     if (h == null) {
/*  87:139 */       h = defaultHeaderRenderer;
/*  88:    */     }
/*  89:141 */     if (h != null)
/*  90:    */     {
/*  91:143 */       Component c = h.getTableCellRendererComponent(table, column.getHeaderValue(), false, false, -1, col);
/*  92:    */       
/*  93:145 */       width = c.getPreferredSize().width + 5;
/*  94:    */     }
/*  95:148 */     return width;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void setOptimalColumnWidth(int col)
/*  99:    */   {
/* 100:155 */     setOptimalColumnWidth(getJTable(), col);
/* 101:    */   }
/* 102:    */   
/* 103:    */   public static void setOptimalColumnWidth(JTable jtable, int col)
/* 104:    */   {
/* 105:166 */     if ((col >= 0) && (col < jtable.getColumnModel().getColumnCount()))
/* 106:    */     {
/* 107:167 */       int width = calcColumnWidth(jtable, col);
/* 108:169 */       if (width >= 0)
/* 109:    */       {
/* 110:170 */         JTableHeader header = jtable.getTableHeader();
/* 111:171 */         TableColumn column = jtable.getColumnModel().getColumn(col);
/* 112:172 */         column.setPreferredWidth(width);
/* 113:173 */         jtable.sizeColumnsToFit(-1);
/* 114:174 */         header.repaint();
/* 115:    */       }
/* 116:    */     }
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void setOptimalColumnWidth()
/* 120:    */   {
/* 121:183 */     setOptimalColumnWidth(getJTable());
/* 122:    */   }
/* 123:    */   
/* 124:    */   public static void setOptimalColumnWidth(JTable jtable)
/* 125:    */   {
/* 126:192 */     for (int i = 0; i < jtable.getColumnModel().getColumnCount(); i++) {
/* 127:193 */       setOptimalColumnWidth(jtable, i);
/* 128:    */     }
/* 129:    */   }
/* 130:    */   
/* 131:    */   public void setOptimalHeaderWidth(int col)
/* 132:    */   {
/* 133:201 */     setOptimalHeaderWidth(getJTable(), col);
/* 134:    */   }
/* 135:    */   
/* 136:    */   public static void setOptimalHeaderWidth(JTable jtable, int col)
/* 137:    */   {
/* 138:212 */     if ((col >= 0) && (col < jtable.getColumnModel().getColumnCount()))
/* 139:    */     {
/* 140:213 */       int width = calcHeaderWidth(jtable, col);
/* 141:215 */       if (width >= 0)
/* 142:    */       {
/* 143:216 */         JTableHeader header = jtable.getTableHeader();
/* 144:217 */         TableColumn column = jtable.getColumnModel().getColumn(col);
/* 145:218 */         column.setPreferredWidth(width);
/* 146:219 */         jtable.sizeColumnsToFit(-1);
/* 147:220 */         header.repaint();
/* 148:    */       }
/* 149:    */     }
/* 150:    */   }
/* 151:    */   
/* 152:    */   public void setOptimalHeaderWidth()
/* 153:    */   {
/* 154:229 */     setOptimalHeaderWidth(getJTable());
/* 155:    */   }
/* 156:    */   
/* 157:    */   public static void setOptimalHeaderWidth(JTable jtable)
/* 158:    */   {
/* 159:238 */     for (int i = 0; i < jtable.getColumnModel().getColumnCount(); i++) {
/* 160:239 */       setOptimalHeaderWidth(jtable, i);
/* 161:    */     }
/* 162:    */   }
/* 163:    */   
/* 164:    */   public void scrollToVisible(int row, int col)
/* 165:    */   {
/* 166:248 */     scrollToVisible(getJTable(), row, col);
/* 167:    */   }
/* 168:    */   
/* 169:    */   public static void scrollToVisible(JTable table, int row, int col)
/* 170:    */   {
/* 171:256 */     if (!(table.getParent() instanceof JViewport)) {
/* 172:257 */       return;
/* 173:    */     }
/* 174:260 */     JViewport viewport = (JViewport)table.getParent();
/* 175:    */     
/* 176:    */ 
/* 177:    */ 
/* 178:264 */     Rectangle rect = table.getCellRect(row, col, true);
/* 179:    */     
/* 180:    */ 
/* 181:267 */     Point pt = viewport.getViewPosition();
/* 182:    */     
/* 183:    */ 
/* 184:    */ 
/* 185:    */ 
/* 186:272 */     rect.setLocation(rect.x - pt.x, rect.y - pt.y);
/* 187:    */     
/* 188:    */ 
/* 189:275 */     viewport.scrollRectToVisible(rect);
/* 190:    */   }
/* 191:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.JTableHelper
 * JD-Core Version:    0.7.0.1
 */