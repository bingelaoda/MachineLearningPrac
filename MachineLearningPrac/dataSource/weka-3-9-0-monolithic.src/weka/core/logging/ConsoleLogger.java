/*  1:   */ package weka.core.logging;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import java.text.SimpleDateFormat;
/*  5:   */ import java.util.Date;
/*  6:   */ import weka.core.RevisionUtils;
/*  7:   */ 
/*  8:   */ public class ConsoleLogger
/*  9:   */   extends Logger
/* 10:   */ {
/* 11:   */   protected void doLog(Logger.Level level, String msg, String cls, String method, int lineno)
/* 12:   */   {
/* 13:46 */     System.err.println(m_DateFormat.format(new Date()) + " " + cls + " " + method + "\n" + level + ": " + msg);
/* 14:   */   }
/* 15:   */   
/* 16:   */   public String getRevision()
/* 17:   */   {
/* 18:57 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 19:   */   }
/* 20:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.logging.ConsoleLogger
 * JD-Core Version:    0.7.0.1
 */