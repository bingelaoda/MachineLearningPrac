/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Component;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import javax.swing.JPanel;
/*   7:    */ import javax.swing.JScrollPane;
/*   8:    */ import javax.swing.JTable;
/*   9:    */ import javax.swing.event.TableModelEvent;
/*  10:    */ import javax.swing.event.TableModelListener;
/*  11:    */ import javax.swing.table.DefaultTableCellRenderer;
/*  12:    */ import javax.swing.table.TableColumn;
/*  13:    */ import javax.swing.table.TableColumnModel;
/*  14:    */ 
/*  15:    */ public class InteractiveTablePanel
/*  16:    */   extends JPanel
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = 4495705463732140410L;
/*  19:    */   protected String[] m_columnNames;
/*  20:    */   protected JTable m_table;
/*  21:    */   protected JScrollPane m_scroller;
/*  22:    */   protected InteractiveTableModel m_tableModel;
/*  23:    */   
/*  24:    */   public InteractiveTablePanel(String[] colNames)
/*  25:    */   {
/*  26: 62 */     this.m_columnNames = colNames;
/*  27: 63 */     initComponent();
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void initComponent()
/*  31:    */   {
/*  32: 70 */     this.m_tableModel = new InteractiveTableModel(this.m_columnNames);
/*  33: 71 */     this.m_tableModel.addTableModelListener(new InteractiveTableModelListener());
/*  34:    */     
/*  35: 73 */     this.m_table = new JTable();
/*  36: 74 */     this.m_table.setModel(this.m_tableModel);
/*  37: 75 */     this.m_table.setSurrendersFocusOnKeystroke(true);
/*  38: 76 */     if (!this.m_tableModel.hasEmptyRow()) {
/*  39: 77 */       this.m_tableModel.addEmptyRow();
/*  40:    */     }
/*  41: 80 */     InteractiveTableModel model = (InteractiveTableModel)this.m_table.getModel();
/*  42: 81 */     this.m_scroller = new JScrollPane(this.m_table);
/*  43: 82 */     this.m_table.setPreferredScrollableViewportSize(new Dimension(500, 80));
/*  44: 83 */     TableColumn hidden = this.m_table.getColumnModel().getColumn(model.m_hidden_index);
/*  45:    */     
/*  46: 85 */     hidden.setMinWidth(2);
/*  47: 86 */     hidden.setPreferredWidth(2);
/*  48: 87 */     hidden.setMaxWidth(2);
/*  49: 88 */     hidden.setCellRenderer(new InteractiveRenderer(model.m_hidden_index));
/*  50:    */     
/*  51: 90 */     setLayout(new BorderLayout());
/*  52: 91 */     add(this.m_scroller, "Center");
/*  53:    */   }
/*  54:    */   
/*  55:    */   public JTable getTable()
/*  56:    */   {
/*  57:100 */     return this.m_table;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void highlightLastRow(int row)
/*  61:    */   {
/*  62:109 */     int lastrow = this.m_tableModel.getRowCount();
/*  63:110 */     if (row == lastrow - 1) {
/*  64:111 */       this.m_table.setRowSelectionInterval(lastrow - 1, lastrow - 1);
/*  65:    */     } else {
/*  66:113 */       this.m_table.setRowSelectionInterval(row + 1, row + 1);
/*  67:    */     }
/*  68:116 */     this.m_table.setColumnSelectionInterval(0, 0);
/*  69:    */   }
/*  70:    */   
/*  71:    */   class InteractiveRenderer
/*  72:    */     extends DefaultTableCellRenderer
/*  73:    */   {
/*  74:    */     private static final long serialVersionUID = 6186813827783402502L;
/*  75:    */     protected int m_interactiveColumn;
/*  76:    */     
/*  77:    */     public InteractiveRenderer(int interactiveColumn)
/*  78:    */     {
/*  79:136 */       this.m_interactiveColumn = interactiveColumn;
/*  80:    */     }
/*  81:    */     
/*  82:    */     public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
/*  83:    */     {
/*  84:142 */       Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
/*  85:145 */       if ((column == this.m_interactiveColumn) && (hasFocus))
/*  86:    */       {
/*  87:146 */         if ((InteractiveTablePanel.this.m_tableModel.getRowCount() - 1 == row) && (!InteractiveTablePanel.this.m_tableModel.hasEmptyRow())) {
/*  88:148 */           InteractiveTablePanel.this.m_tableModel.addEmptyRow();
/*  89:    */         }
/*  90:151 */         InteractiveTablePanel.this.highlightLastRow(row);
/*  91:    */       }
/*  92:154 */       return c;
/*  93:    */     }
/*  94:    */   }
/*  95:    */   
/*  96:    */   public class InteractiveTableModelListener
/*  97:    */     implements TableModelListener
/*  98:    */   {
/*  99:    */     public InteractiveTableModelListener() {}
/* 100:    */     
/* 101:    */     public void tableChanged(TableModelEvent evt)
/* 102:    */     {
/* 103:164 */       if (evt.getType() == 0)
/* 104:    */       {
/* 105:165 */         int column = evt.getColumn();
/* 106:166 */         int row = evt.getFirstRow();
/* 107:167 */         InteractiveTablePanel.this.m_table.setColumnSelectionInterval(column + 1, column + 1);
/* 108:168 */         InteractiveTablePanel.this.m_table.setRowSelectionInterval(row, row);
/* 109:    */       }
/* 110:    */     }
/* 111:    */   }
/* 112:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.InteractiveTablePanel
 * JD-Core Version:    0.7.0.1
 */