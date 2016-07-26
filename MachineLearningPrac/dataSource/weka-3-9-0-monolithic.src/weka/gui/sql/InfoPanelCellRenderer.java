/*  1:   */ package weka.gui.sql;
/*  2:   */ 
/*  3:   */ import java.awt.Component;
/*  4:   */ import javax.swing.JLabel;
/*  5:   */ import javax.swing.JList;
/*  6:   */ import javax.swing.ListCellRenderer;
/*  7:   */ 
/*  8:   */ public class InfoPanelCellRenderer
/*  9:   */   extends JLabel
/* 10:   */   implements ListCellRenderer
/* 11:   */ {
/* 12:   */   private static final long serialVersionUID = -533380118807178531L;
/* 13:   */   
/* 14:   */   public InfoPanelCellRenderer()
/* 15:   */   {
/* 16:49 */     setOpaque(true);
/* 17:   */   }
/* 18:   */   
/* 19:   */   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
/* 20:   */   {
/* 21:64 */     if ((value instanceof JLabel))
/* 22:   */     {
/* 23:65 */       setIcon(((JLabel)value).getIcon());
/* 24:66 */       setText(((JLabel)value).getText());
/* 25:   */     }
/* 26:   */     else
/* 27:   */     {
/* 28:69 */       setIcon(null);
/* 29:70 */       setText(value.toString());
/* 30:   */     }
/* 31:73 */     if (isSelected)
/* 32:   */     {
/* 33:74 */       setBackground(list.getSelectionBackground());
/* 34:75 */       setForeground(list.getSelectionForeground());
/* 35:   */     }
/* 36:   */     else
/* 37:   */     {
/* 38:78 */       setBackground(list.getBackground());
/* 39:79 */       setForeground(list.getForeground());
/* 40:   */     }
/* 41:81 */     setEnabled(list.isEnabled());
/* 42:82 */     setFont(list.getFont());
/* 43:   */     
/* 44:84 */     return this;
/* 45:   */   }
/* 46:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.InfoPanelCellRenderer
 * JD-Core Version:    0.7.0.1
 */