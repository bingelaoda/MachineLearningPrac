/*  1:   */ package org.boon.validation.validators;
/*  2:   */ 
/*  3:   */ import org.boon.Boon;
/*  4:   */ import org.boon.validation.ValidatorMessage;
/*  5:   */ import org.boon.validation.ValidatorMessageHolder;
/*  6:   */ 
/*  7:   */ public class LengthValidator
/*  8:   */   extends BaseValidator
/*  9:   */ {
/* 10:45 */   private int min = 0;
/* 11:49 */   private int max = 2147483647;
/* 12:   */   
/* 13:   */   public ValidatorMessageHolder validate(Object fieldValue, String fieldLabel)
/* 14:   */   {
/* 15:58 */     ValidatorMessage validatorMessage = new ValidatorMessage();
/* 16:59 */     if (fieldValue == null) {
/* 17:60 */       return validatorMessage;
/* 18:   */     }
/* 19:63 */     int len = Boon.len(fieldValue);
/* 20:65 */     if ((len < this.min) || (len > this.max)) {
/* 21:66 */       populateMessage(validatorMessage, fieldLabel, new Object[] { Integer.valueOf(this.min), Integer.valueOf(this.max) });
/* 22:   */     }
/* 23:70 */     return validatorMessage;
/* 24:   */   }
/* 25:   */   
/* 26:   */   public void setMax(int max)
/* 27:   */   {
/* 28:75 */     this.max = max;
/* 29:   */   }
/* 30:   */   
/* 31:   */   public void setMin(int min)
/* 32:   */   {
/* 33:79 */     this.min = min;
/* 34:   */   }
/* 35:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.validators.LengthValidator
 * JD-Core Version:    0.7.0.1
 */