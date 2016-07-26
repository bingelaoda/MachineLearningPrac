/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.sql.Connection;
/*   5:    */ import java.sql.DatabaseMetaData;
/*   6:    */ import java.sql.PreparedStatement;
/*   7:    */ import java.sql.SQLException;
/*   8:    */ import java.util.Properties;
/*   9:    */ import weka.core.RevisionUtils;
/*  10:    */ import weka.experiment.DatabaseUtils;
/*  11:    */ 
/*  12:    */ public class DatabaseConnection
/*  13:    */   extends DatabaseUtils
/*  14:    */ {
/*  15:    */   static final long serialVersionUID = 1673169848863178695L;
/*  16:    */   
/*  17:    */   public DatabaseConnection()
/*  18:    */     throws Exception
/*  19:    */   {}
/*  20:    */   
/*  21:    */   public DatabaseConnection(File propsFile)
/*  22:    */     throws Exception
/*  23:    */   {
/*  24: 62 */     super(propsFile);
/*  25:    */   }
/*  26:    */   
/*  27:    */   public DatabaseConnection(Properties props)
/*  28:    */     throws Exception
/*  29:    */   {
/*  30: 72 */     super(props);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public Properties getProperties()
/*  34:    */   {
/*  35: 81 */     return this.PROPERTIES;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public boolean getUpperCase()
/*  39:    */   {
/*  40: 92 */     return this.m_checkForUpperCaseNames;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public DatabaseMetaData getMetaData()
/*  44:    */     throws Exception
/*  45:    */   {
/*  46:102 */     if (!isConnected()) {
/*  47:103 */       throw new IllegalStateException("Not connected, please connect first!");
/*  48:    */     }
/*  49:105 */     return this.m_Connection.getMetaData();
/*  50:    */   }
/*  51:    */   
/*  52:    */   public int getUpdateCount()
/*  53:    */     throws SQLException
/*  54:    */   {
/*  55:115 */     if (!isConnected()) {
/*  56:116 */       throw new IllegalStateException("Not connected, please connect first!");
/*  57:    */     }
/*  58:118 */     return this.m_PreparedStatement.getUpdateCount();
/*  59:    */   }
/*  60:    */   
/*  61:    */   public String getRevision()
/*  62:    */   {
/*  63:127 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  64:    */   }
/*  65:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.DatabaseConnection
 * JD-Core Version:    0.7.0.1
 */