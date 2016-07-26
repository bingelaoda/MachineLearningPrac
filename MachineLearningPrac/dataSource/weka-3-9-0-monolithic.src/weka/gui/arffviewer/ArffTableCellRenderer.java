/*   1:    */ package weka.gui.arffviewer;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Component;
/*   5:    */ import javax.swing.JTable;
/*   6:    */ import javax.swing.ListSelectionModel;
/*   7:    */ import javax.swing.UIManager;
/*   8:    */ import javax.swing.table.DefaultTableCellRenderer;
/*   9:    */ import javax.swing.table.TableColumnModel;
/*  10:    */ 
/*  11:    */ public class ArffTableCellRenderer
/*  12:    */   extends DefaultTableCellRenderer
/*  13:    */ {
/*  14:    */   static final long serialVersionUID = 9195794493301191171L;
/*  15:    */   private Color missingColor;
/*  16:    */   private Color missingColorSelected;
/*  17:    */   private Color highlightColor;
/*  18:    */   private Color highlightColorSelected;
/*  19:    */   
/*  20:    */   public ArffTableCellRenderer()
/*  21:    */   {
/*  22: 62 */     this(new Color(223, 223, 223), new Color(192, 192, 192));
/*  23:    */   }
/*  24:    */   
/*  25:    */   public ArffTableCellRenderer(Color missingColor, Color missingColorSelected)
/*  26:    */   {
/*  27: 74 */     this(missingColor, missingColorSelected, Color.RED, Color.RED.darker());
/*  28:    */   }
/*  29:    */   
/*  30:    */   public ArffTableCellRenderer(Color missingColor, Color missingColorSelected, Color highlightColor, Color highlightColorSelected)
/*  31:    */   {
/*  32: 94 */     this.missingColor = missingColor;
/*  33: 95 */     this.missingColorSelected = missingColorSelected;
/*  34: 96 */     this.highlightColor = highlightColor;
/*  35: 97 */     this.highlightColorSelected = highlightColorSelected;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*  39:    */   {
/*  40:120 */     Component result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/*  41:    */     String searchString;
/*  42:    */     String searchString;
/*  43:124 */     if ((table instanceof ArffTable)) {
/*  44:125 */       searchString = ((ArffTable)table).getSearchString();
/*  45:    */     } else {
/*  46:127 */       searchString = null;
/*  47:    */     }
/*  48:    */     boolean found;
/*  49:    */     boolean found;
/*  50:128 */     if ((searchString != null) && (!searchString.equals(""))) {
/*  51:129 */       found = searchString.equals(value.toString());
/*  52:    */     } else {
/*  53:131 */       found = false;
/*  54:    */     }
/*  55:133 */     if ((table.getModel() instanceof ArffSortedTableModel))
/*  56:    */     {
/*  57:134 */       ArffSortedTableModel model = (ArffSortedTableModel)table.getModel();
/*  58:136 */       if (row >= 0)
/*  59:    */       {
/*  60:137 */         if (model.isMissingAt(row, column))
/*  61:    */         {
/*  62:138 */           setToolTipText("missing");
/*  63:139 */           if (found)
/*  64:    */           {
/*  65:140 */             if (isSelected) {
/*  66:141 */               result.setBackground(this.highlightColorSelected);
/*  67:    */             } else {
/*  68:143 */               result.setBackground(this.highlightColor);
/*  69:    */             }
/*  70:    */           }
/*  71:146 */           else if (isSelected) {
/*  72:147 */             result.setBackground(this.missingColorSelected);
/*  73:    */           } else {
/*  74:149 */             result.setBackground(this.missingColor);
/*  75:    */           }
/*  76:    */         }
/*  77:    */         else
/*  78:    */         {
/*  79:153 */           setToolTipText(null);
/*  80:154 */           if (found)
/*  81:    */           {
/*  82:155 */             if (isSelected) {
/*  83:156 */               result.setBackground(this.highlightColorSelected);
/*  84:    */             } else {
/*  85:158 */               result.setBackground(this.highlightColor);
/*  86:    */             }
/*  87:    */           }
/*  88:161 */           else if (isSelected) {
/*  89:162 */             result.setBackground(table.getSelectionBackground());
/*  90:    */           } else {
/*  91:164 */             result.setBackground(Color.WHITE);
/*  92:    */           }
/*  93:    */         }
/*  94:169 */         if (model.getType(row, column) == 0) {
/*  95:170 */           setHorizontalAlignment(4);
/*  96:    */         } else {
/*  97:172 */           setHorizontalAlignment(2);
/*  98:    */         }
/*  99:    */       }
/* 100:    */       else
/* 101:    */       {
/* 102:176 */         setBorder(UIManager.getBorder("TableHeader.cellBorder"));
/* 103:177 */         setHorizontalAlignment(0);
/* 104:178 */         if (table.getColumnModel().getSelectionModel().isSelectedIndex(column)) {
/* 105:179 */           result.setBackground(UIManager.getColor("TableHeader.background").darker());
/* 106:    */         } else {
/* 107:181 */           result.setBackground(UIManager.getColor("TableHeader.background"));
/* 108:    */         }
/* 109:    */       }
/* 110:    */     }
/* 111:185 */     return result;
/* 112:    */   }
/* 113:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.arffviewer.ArffTableCellRenderer
 * JD-Core Version:    0.7.0.1
 */