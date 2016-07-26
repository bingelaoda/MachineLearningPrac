/*  1:   */ package weka.gui.sql;
/*  2:   */ 
/*  3:   */ import java.sql.Connection;
/*  4:   */ import weka.core.RevisionUtils;
/*  5:   */ import weka.experiment.DatabaseUtils;
/*  6:   */ 
/*  7:   */ public class DbUtils
/*  8:   */   extends DatabaseUtils
/*  9:   */ {
/* 10:   */   private static final long serialVersionUID = 103748569037426479L;
/* 11:   */   
/* 12:   */   public DbUtils()
/* 13:   */     throws Exception
/* 14:   */   {}
/* 15:   */   
/* 16:   */   public Connection getConnection()
/* 17:   */   {
/* 18:60 */     return this.m_Connection;
/* 19:   */   }
/* 20:   */   
/* 21:   */   public String getRevision()
/* 22:   */   {
/* 23:69 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 24:   */   }
/* 25:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.sql.DbUtils
 * JD-Core Version:    0.7.0.1
 */