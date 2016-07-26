/*   1:    */ package weka.gui.sql;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Component;
/*   5:    */ import javax.swing.JTable;
/*   6:    */ import javax.swing.ListSelectionModel;
/*   7:    */ import javax.swing.UIManager;
/*   8:    */ import javax.swing.table.DefaultTableCellRenderer;
/*   9:    */ import javax.swing.table.TableColumnModel;
/*  10:    */ 
/*  11:    */ public class ResultSetTableCellRenderer
/*  12:    */   extends DefaultTableCellRenderer
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = -8106963669703497351L;
/*  15:    */   private final Color missingColor;
/*  16:    */   private final Color missingColorSelected;
/*  17:    */   
/*  18:    */   public ResultSetTableCellRenderer()
/*  19:    */   {
/*  20: 52 */     this(new Color(223, 223, 223), new Color(192, 192, 192));
/*  21:    */   }
/*  22:    */   
/*  23:    */   public ResultSetTableCellRenderer(Color missingColor, Color missingColorSelected)
/*  24:    */   {
/*  25: 63 */     this.missingColor = missingColor;
/*  26: 64 */     this.missingColorSelected = missingColorSelected;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*  30:    */   {
/*  31: 79 */     Component result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/*  32: 82 */     if ((table.getModel() instanceof ResultSetTableModel))
/*  33:    */     {
/*  34: 83 */       ResultSetTableModel model = (ResultSetTableModel)table.getModel();
/*  35: 85 */       if (row >= 0)
/*  36:    */       {
/*  37: 86 */         if (model.isNullAt(row, column))
/*  38:    */         {
/*  39: 87 */           setToolTipText("NULL");
/*  40: 88 */           if (isSelected) {
/*  41: 89 */             result.setBackground(this.missingColorSelected);
/*  42:    */           } else {
/*  43: 91 */             result.setBackground(this.missingColor);
/*  44:    */           }
/*  45:    */         }
/*  46:    */         else
/*  47:    */         {
/*  48: 94 */           setToolTipText(null);
/*  49: 95 */           if (isSelected) {
/*  50: 96 */             result.setBackground(table.getSelectionBackground());
/*  51:    */           } else {
/*  52: 98 */             result.setBackground(Color.WHITE);
/*  53:    */           }
/*  54:    */         }
/*  55:103 */         if (model.isNumericAt(column)) {
/*  56:104 */           setHorizontalAlignment(4);
/*  57:    */         } else {
/*  58:106 */           setHorizontalAlignment(2);
/*  59:    */         }
/*  60:    */       }
/*  61:    */       else
/*  62:    */       {
/*  63:111 */         setBorder(UIManager.getBorder("TableHeader.cellBorder"));
/*  64:112 */         setHorizontalAlignment(0);
/*  65:113 */         if (table.getColumnModel().getSelectionModel().isSelectedIndex(column)) {
/*  66:114 */           result.setBackground(UIManager.getColor("TableHeader.background").darker());
/*  67:    */         } else {
/*  68:117 */           result.setBackground(UIManager.getColor("TableHeader.background"));
/*  69:    */         }
/*  70:    */       }
/*  71:    */     }
/*  72:122 */     return result;
/*  73:    */   }
/*  74:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.ResultSetTableCellRenderer
 * JD-Core Version:    0.7.0.1
 */