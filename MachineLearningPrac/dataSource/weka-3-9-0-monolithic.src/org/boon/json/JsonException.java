/*  1:   */ package org.boon.json;
/*  2:   */ 
/*  3:   */ public class JsonException
/*  4:   */   extends RuntimeException
/*  5:   */ {
/*  6:   */   static void handleException(Exception ex)
/*  7:   */   {
/*  8:36 */     throw new JsonException(ex);
/*  9:   */   }
/* 10:   */   
/* 11:   */   public JsonException(String message, Throwable cause)
/* 12:   */   {
/* 13:41 */     super(message, cause);
/* 14:   */   }
/* 15:   */   
/* 16:   */   public JsonException(String message)
/* 17:   */   {
/* 18:45 */     super(message);
/* 19:   */   }
/* 20:   */   
/* 21:   */   public JsonException(Throwable cause)
/* 22:   */   {
/* 23:49 */     super(cause);
/* 24:   */   }
/* 25:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.JsonException
 * JD-Core Version:    0.7.0.1
 */