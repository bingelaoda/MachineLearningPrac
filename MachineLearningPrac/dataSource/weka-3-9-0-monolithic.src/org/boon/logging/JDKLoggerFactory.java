/*  1:   */ package org.boon.logging;
/*  2:   */ 
/*  3:   */ public class JDKLoggerFactory
/*  4:   */   implements LoggerFactory
/*  5:   */ {
/*  6:   */   public LoggerDelegate logger(String name)
/*  7:   */   {
/*  8:34 */     return new JDKLogger(name);
/*  9:   */   }
/* 10:   */   
/* 11:   */   public LoggerDelegate apply(String logName)
/* 12:   */   {
/* 13:39 */     return logger(logName);
/* 14:   */   }
/* 15:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.logging.JDKLoggerFactory
 * JD-Core Version:    0.7.0.1
 */