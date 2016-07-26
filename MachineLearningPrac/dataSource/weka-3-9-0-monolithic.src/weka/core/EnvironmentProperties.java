/*  1:   */ package weka.core;
/*  2:   */ 
/*  3:   */ import java.util.Properties;
/*  4:   */ 
/*  5:   */ public class EnvironmentProperties
/*  6:   */   extends Properties
/*  7:   */ {
/*  8:37 */   protected transient Environment m_env = Environment.getSystemWide();
/*  9:   */   
/* 10:   */   public EnvironmentProperties() {}
/* 11:   */   
/* 12:   */   public EnvironmentProperties(Properties props)
/* 13:   */   {
/* 14:44 */     super(props);
/* 15:   */   }
/* 16:   */   
/* 17:   */   public String getProperty(String key)
/* 18:   */   {
/* 19:50 */     if (this.m_env == null) {
/* 20:51 */       this.m_env = Environment.getSystemWide();
/* 21:   */     }
/* 22:53 */     String result = this.m_env.getVariableValue(key);
/* 23:55 */     if (result == null) {
/* 24:56 */       result = super.getProperty(key);
/* 25:   */     }
/* 26:59 */     return result;
/* 27:   */   }
/* 28:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.EnvironmentProperties
 * JD-Core Version:    0.7.0.1
 */