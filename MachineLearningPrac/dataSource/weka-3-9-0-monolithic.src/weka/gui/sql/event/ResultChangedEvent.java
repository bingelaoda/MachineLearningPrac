/*   1:    */ package weka.gui.sql.event;
/*   2:    */ 
/*   3:    */ import java.util.EventObject;
/*   4:    */ 
/*   5:    */ public class ResultChangedEvent
/*   6:    */   extends EventObject
/*   7:    */ {
/*   8:    */   private static final long serialVersionUID = 36042516077236111L;
/*   9:    */   protected String m_Query;
/*  10:    */   protected String m_URL;
/*  11:    */   protected String m_User;
/*  12:    */   protected String m_Password;
/*  13:    */   
/*  14:    */   public ResultChangedEvent(Object source, String url, String user, String pw, String query)
/*  15:    */   {
/*  16: 65 */     super(source);
/*  17:    */     
/*  18: 67 */     this.m_URL = url;
/*  19: 68 */     this.m_User = user;
/*  20: 69 */     this.m_Password = pw;
/*  21: 70 */     this.m_Query = query;
/*  22:    */   }
/*  23:    */   
/*  24:    */   public String getURL()
/*  25:    */   {
/*  26: 77 */     return this.m_URL;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public String getUser()
/*  30:    */   {
/*  31: 84 */     return this.m_User;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String getPassword()
/*  35:    */   {
/*  36: 91 */     return this.m_Password;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public String getQuery()
/*  40:    */   {
/*  41: 98 */     return this.m_Query;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String toString()
/*  45:    */   {
/*  46:108 */     String result = super.toString();
/*  47:109 */     result = result.substring(0, result.length() - 1);
/*  48:110 */     result = result + ",url=" + getURL() + ",user=" + getUser() + ",password=" + getPassword().replaceAll(".", "*") + ",query=" + getQuery() + "]";
/*  49:    */     
/*  50:    */ 
/*  51:    */ 
/*  52:    */ 
/*  53:    */ 
/*  54:116 */     return result;
/*  55:    */   }
/*  56:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.event.ResultChangedEvent
 * JD-Core Version:    0.7.0.1
 */