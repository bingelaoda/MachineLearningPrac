/*   1:    */ package weka.gui.sql.event;
/*   2:    */ 
/*   3:    */ import java.util.EventObject;
/*   4:    */ import weka.gui.sql.DbUtils;
/*   5:    */ 
/*   6:    */ public class ConnectionEvent
/*   7:    */   extends EventObject
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = 5420308930427835037L;
/*  10:    */   public static final int CONNECT = 0;
/*  11:    */   public static final int DISCONNECT = 1;
/*  12:    */   protected int m_Type;
/*  13:    */   protected DbUtils m_DbUtils;
/*  14:    */   protected Exception m_Exception;
/*  15:    */   
/*  16:    */   public ConnectionEvent(Object source, int type, DbUtils utils)
/*  17:    */   {
/*  18: 64 */     this(source, type, utils, null);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public ConnectionEvent(Object source, int type, DbUtils utils, Exception ex)
/*  22:    */   {
/*  23: 76 */     super(source);
/*  24:    */     
/*  25: 78 */     this.m_Type = type;
/*  26: 79 */     this.m_DbUtils = utils;
/*  27: 80 */     this.m_Exception = ex;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public int getType()
/*  31:    */   {
/*  32: 90 */     return this.m_Type;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public boolean failed()
/*  36:    */   {
/*  37: 98 */     return getException() != null;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public boolean isConnected()
/*  41:    */   {
/*  42:106 */     return this.m_DbUtils.isConnected();
/*  43:    */   }
/*  44:    */   
/*  45:    */   public Exception getException()
/*  46:    */   {
/*  47:113 */     return this.m_Exception;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public DbUtils getDbUtils()
/*  51:    */   {
/*  52:122 */     return this.m_DbUtils;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public String toString()
/*  56:    */   {
/*  57:132 */     String result = super.toString();
/*  58:133 */     result = result.substring(0, result.length() - 1);
/*  59:134 */     result = result + ",url=" + this.m_DbUtils.getDatabaseURL() + ",user=" + this.m_DbUtils.getUsername() + ",password=" + this.m_DbUtils.getPassword().replaceAll(".", "*") + ",connected=" + isConnected() + ",exception=" + getException() + "]";
/*  60:    */     
/*  61:    */ 
/*  62:    */ 
/*  63:    */ 
/*  64:    */ 
/*  65:    */ 
/*  66:141 */     return result;
/*  67:    */   }
/*  68:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.event.ConnectionEvent
 * JD-Core Version:    0.7.0.1
 */