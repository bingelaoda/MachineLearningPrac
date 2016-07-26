/*  1:   */ package weka.core;
/*  2:   */ 
/*  3:   */ public class WekaException
/*  4:   */   extends Exception
/*  5:   */ {
/*  6:   */   private static final long serialVersionUID = 6428269989006208585L;
/*  7:   */   
/*  8:   */   public WekaException() {}
/*  9:   */   
/* 10:   */   public WekaException(String message)
/* 11:   */   {
/* 12:52 */     super(message);
/* 13:   */   }
/* 14:   */   
/* 15:   */   public WekaException(String message, Throwable cause)
/* 16:   */   {
/* 17:62 */     this(message);
/* 18:63 */     initCause(cause);
/* 19:64 */     fillInStackTrace();
/* 20:   */   }
/* 21:   */   
/* 22:   */   public WekaException(Throwable cause)
/* 23:   */   {
/* 24:73 */     this(cause.getMessage(), cause);
/* 25:   */   }
/* 26:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.WekaException
 * JD-Core Version:    0.7.0.1
 */