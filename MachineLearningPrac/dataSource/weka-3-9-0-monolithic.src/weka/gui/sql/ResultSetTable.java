/*   1:    */ package weka.gui.sql;
/*   2:    */ 
/*   3:    */ import java.awt.event.MouseAdapter;
/*   4:    */ import java.awt.event.MouseEvent;
/*   5:    */ import javax.swing.JTable;
/*   6:    */ import javax.swing.table.JTableHeader;
/*   7:    */ import javax.swing.table.TableColumn;
/*   8:    */ import javax.swing.table.TableColumnModel;
/*   9:    */ import weka.gui.JTableHelper;
/*  10:    */ 
/*  11:    */ public class ResultSetTable
/*  12:    */   extends JTable
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = -3391076671854464137L;
/*  15:    */   protected String m_Query;
/*  16:    */   protected String m_URL;
/*  17:    */   protected String m_User;
/*  18:    */   protected String m_Password;
/*  19:    */   
/*  20:    */   public ResultSetTable(String url, String user, String pw, String query, ResultSetTableModel model)
/*  21:    */   {
/*  22: 66 */     super(model);
/*  23:    */     
/*  24: 68 */     this.m_URL = url;
/*  25: 69 */     this.m_User = user;
/*  26: 70 */     this.m_Password = pw;
/*  27: 71 */     this.m_Query = query;
/*  28:    */     
/*  29: 73 */     setAutoResizeMode(0);
/*  30: 76 */     for (int i = 0; i < getColumnCount(); i++)
/*  31:    */     {
/*  32: 77 */       JTableHelper.setOptimalHeaderWidth(this, i);
/*  33: 78 */       getColumnModel().getColumn(i).setCellRenderer(new ResultSetTableCellRenderer());
/*  34:    */     }
/*  35: 83 */     final JTable table = this;
/*  36: 84 */     getTableHeader().addMouseListener(new MouseAdapter()
/*  37:    */     {
/*  38:    */       public void mouseClicked(MouseEvent e)
/*  39:    */       {
/*  40: 86 */         TableColumnModel columnModel = ResultSetTable.this.getColumnModel();
/*  41: 87 */         int viewColumn = columnModel.getColumnIndexAtX(e.getX());
/*  42: 88 */         int column = ResultSetTable.this.convertColumnIndexToModel(viewColumn);
/*  43: 89 */         if ((e.getButton() == 1) && (e.getClickCount() == 2) && (column != -1)) {
/*  44: 92 */           JTableHelper.setOptimalColumnWidth(table, column);
/*  45:    */         }
/*  46:    */       }
/*  47: 94 */     });
/*  48: 95 */     getTableHeader().setToolTipText("double left click on column displays the column with optimal width");
/*  49:    */   }
/*  50:    */   
/*  51:    */   public String getURL()
/*  52:    */   {
/*  53:102 */     return this.m_URL;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public String getUser()
/*  57:    */   {
/*  58:109 */     return this.m_User;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public String getPassword()
/*  62:    */   {
/*  63:116 */     return this.m_Password;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String getQuery()
/*  67:    */   {
/*  68:123 */     return this.m_Query;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void finalize()
/*  72:    */     throws Throwable
/*  73:    */   {
/*  74:130 */     if (getModel() != null) {
/*  75:131 */       ((ResultSetTableModel)getModel()).finalize();
/*  76:    */     }
/*  77:133 */     super.finalize();
/*  78:    */     
/*  79:135 */     System.gc();
/*  80:    */   }
/*  81:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.ResultSetTable
 * JD-Core Version:    0.7.0.1
 */