/*  1:   */ package weka.knowledgeflow;
/*  2:   */ 
/*  3:   */ public enum LoggingLevel
/*  4:   */ {
/*  5:31 */   NONE("None"),  LOW("Low"),  BASIC("Basic"),  DETAILED("Detailed"),  DEBUGGING("Debugging"),  WARNING("WARNING"),  ERROR("ERROR");
/*  6:   */   
/*  7:   */   private final String m_name;
/*  8:   */   
/*  9:   */   private LoggingLevel(String name)
/* 10:   */   {
/* 11:43 */     this.m_name = name;
/* 12:   */   }
/* 13:   */   
/* 14:   */   public static LoggingLevel stringToLevel(String s)
/* 15:   */   {
/* 16:54 */     LoggingLevel ret = BASIC;
/* 17:55 */     for (LoggingLevel l : values()) {
/* 18:56 */       if (l.toString().equals(s)) {
/* 19:57 */         ret = l;
/* 20:   */       }
/* 21:   */     }
/* 22:60 */     return ret;
/* 23:   */   }
/* 24:   */   
/* 25:   */   public String toString()
/* 26:   */   {
/* 27:70 */     return this.m_name;
/* 28:   */   }
/* 29:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.LoggingLevel
 * JD-Core Version:    0.7.0.1
 */