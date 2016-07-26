/*  1:   */ package org.boon.datarepo;
/*  2:   */ 
/*  3:   */ public class DataRepoException
/*  4:   */   extends RuntimeException
/*  5:   */ {
/*  6:   */   public DataRepoException() {}
/*  7:   */   
/*  8:   */   public DataRepoException(String message)
/*  9:   */   {
/* 10:39 */     super(message);
/* 11:   */   }
/* 12:   */   
/* 13:   */   public DataRepoException(String message, Throwable cause)
/* 14:   */   {
/* 15:43 */     super(message, cause);
/* 16:   */   }
/* 17:   */   
/* 18:   */   public DataRepoException(Throwable cause)
/* 19:   */   {
/* 20:47 */     super(cause);
/* 21:   */   }
/* 22:   */   
/* 23:   */   protected DataRepoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
/* 24:   */   {
/* 25:51 */     super(message, cause, enableSuppression, writableStackTrace);
/* 26:   */   }
/* 27:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.DataRepoException
 * JD-Core Version:    0.7.0.1
 */