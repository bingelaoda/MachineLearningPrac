/*  1:   */ package org.boon.validation;
/*  2:   */ 
/*  3:   */ public class ValidationException
/*  4:   */   extends Exception
/*  5:   */ {
/*  6:   */   private String field;
/*  7:   */   
/*  8:   */   public ValidationException(String message, String field)
/*  9:   */   {
/* 10:35 */     super(message);
/* 11:36 */     this.field = field;
/* 12:   */   }
/* 13:   */   
/* 14:   */   public String getField()
/* 15:   */   {
/* 16:40 */     return this.field;
/* 17:   */   }
/* 18:   */   
/* 19:   */   public void setField(String field)
/* 20:   */   {
/* 21:44 */     this.field = field;
/* 22:   */   }
/* 23:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.ValidationException
 * JD-Core Version:    0.7.0.1
 */