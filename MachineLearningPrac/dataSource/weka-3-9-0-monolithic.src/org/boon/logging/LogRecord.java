/*  1:   */ package org.boon.logging;
/*  2:   */ 
/*  3:   */ import java.util.Arrays;
/*  4:   */ 
/*  5:   */ public class LogRecord
/*  6:   */ {
/*  7:   */   public final Object[] messages;
/*  8:   */   public final Throwable throwable;
/*  9:   */   public final LogLevel level;
/* 10:   */   public final boolean before;
/* 11:   */   
/* 12:   */   public static LogRecord before(Object[] messages, Throwable throwable, LogLevel level)
/* 13:   */   {
/* 14:46 */     return new LogRecord(messages, throwable, level, true);
/* 15:   */   }
/* 16:   */   
/* 17:   */   public static LogRecord after(Object[] messages, Throwable throwable, LogLevel level)
/* 18:   */   {
/* 19:51 */     return new LogRecord(messages, throwable, level, false);
/* 20:   */   }
/* 21:   */   
/* 22:   */   public static LogRecord before(Object[] messages, LogLevel level)
/* 23:   */   {
/* 24:57 */     return new LogRecord(messages, null, level, true);
/* 25:   */   }
/* 26:   */   
/* 27:   */   public static LogRecord after(Object[] messages, LogLevel level)
/* 28:   */   {
/* 29:63 */     return new LogRecord(messages, null, level, false);
/* 30:   */   }
/* 31:   */   
/* 32:   */   public LogRecord(Object[] messages, Throwable throwable, LogLevel level, boolean before)
/* 33:   */   {
/* 34:69 */     this.messages = messages;
/* 35:70 */     this.throwable = throwable;
/* 36:71 */     this.level = level;
/* 37:72 */     this.before = before;
/* 38:   */   }
/* 39:   */   
/* 40:   */   public LogRecord()
/* 41:   */   {
/* 42:76 */     this.messages = null;
/* 43:77 */     this.throwable = null;
/* 44:78 */     this.level = null;
/* 45:79 */     this.before = false;
/* 46:   */   }
/* 47:   */   
/* 48:   */   public Object[] getMessages()
/* 49:   */   {
/* 50:83 */     return this.messages;
/* 51:   */   }
/* 52:   */   
/* 53:   */   public Throwable getThrowable()
/* 54:   */   {
/* 55:87 */     return this.throwable;
/* 56:   */   }
/* 57:   */   
/* 58:   */   public LogLevel getLevel()
/* 59:   */   {
/* 60:91 */     return this.level;
/* 61:   */   }
/* 62:   */   
/* 63:   */   public String toString()
/* 64:   */   {
/* 65:96 */     return "LogRecord{messages=" + Arrays.toString(this.messages) + ", throwable=" + this.throwable + ", level=" + this.level + '}';
/* 66:   */   }
/* 67:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.logging.LogRecord
 * JD-Core Version:    0.7.0.1
 */