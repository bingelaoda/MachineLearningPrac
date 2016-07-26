/*   1:    */ package weka.gui.sql.event;
/*   2:    */ 
/*   3:    */ import java.sql.ResultSet;
/*   4:    */ import java.util.EventObject;
/*   5:    */ import weka.gui.sql.DbUtils;
/*   6:    */ 
/*   7:    */ public class QueryExecuteEvent
/*   8:    */   extends EventObject
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -5556385019954730740L;
/*  11:    */   protected DbUtils m_DbUtils;
/*  12:    */   protected String m_Query;
/*  13:    */   protected ResultSet m_ResultSet;
/*  14:    */   protected Exception m_Exception;
/*  15:    */   protected int m_MaxRows;
/*  16:    */   
/*  17:    */   public QueryExecuteEvent(Object source, DbUtils utils, String query, int rows, ResultSet rs, Exception ex)
/*  18:    */   {
/*  19: 73 */     super(source);
/*  20:    */     
/*  21: 75 */     this.m_DbUtils = utils;
/*  22: 76 */     this.m_Query = query;
/*  23: 77 */     this.m_MaxRows = rows;
/*  24: 78 */     this.m_ResultSet = rs;
/*  25: 79 */     this.m_Exception = ex;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public DbUtils getDbUtils()
/*  29:    */   {
/*  30: 86 */     return this.m_DbUtils;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public String getQuery()
/*  34:    */   {
/*  35: 93 */     return this.m_Query;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public int getMaxRows()
/*  39:    */   {
/*  40:100 */     return this.m_MaxRows;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public boolean failed()
/*  44:    */   {
/*  45:107 */     return this.m_Exception != null;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public boolean hasResult()
/*  49:    */   {
/*  50:115 */     return this.m_ResultSet != null;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public ResultSet getResultSet()
/*  54:    */   {
/*  55:123 */     return this.m_ResultSet;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public Exception getException()
/*  59:    */   {
/*  60:130 */     return this.m_Exception;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public String toString()
/*  64:    */   {
/*  65:140 */     String result = super.toString();
/*  66:141 */     result = result.substring(0, result.length() - 1);
/*  67:142 */     result = result + ",query=" + getQuery() + ",maxrows=" + getMaxRows() + ",failed=" + failed() + ",exception=" + getException() + "]";
/*  68:    */     
/*  69:    */ 
/*  70:    */ 
/*  71:    */ 
/*  72:147 */     return result;
/*  73:    */   }
/*  74:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.event.QueryExecuteEvent
 * JD-Core Version:    0.7.0.1
 */