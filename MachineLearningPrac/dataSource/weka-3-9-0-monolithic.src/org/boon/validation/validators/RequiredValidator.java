/*  1:   */ package org.boon.validation.validators;
/*  2:   */ 
/*  3:   */ import org.boon.Str;
/*  4:   */ import org.boon.validation.ValidatorMessage;
/*  5:   */ import org.boon.validation.ValidatorMessageHolder;
/*  6:   */ 
/*  7:   */ public class RequiredValidator
/*  8:   */   extends BaseValidator
/*  9:   */ {
/* 10:   */   public void init()
/* 11:   */   {
/* 12:48 */     if (Str.isEmpty(getDetailMessage())) {
/* 13:49 */       setDetailMessage("{validator.required.detail}");
/* 14:   */     }
/* 15:52 */     if ((!isNoSummary()) && 
/* 16:53 */       (Str.isEmpty(getSummaryMessage()))) {
/* 17:54 */       setSummaryMessage("{validator.required.summary}");
/* 18:   */     }
/* 19:   */   }
/* 20:   */   
/* 21:   */   public ValidatorMessageHolder validate(Object object, String fieldLabel)
/* 22:   */   {
/* 23:60 */     ValidatorMessage message = new ValidatorMessage();
/* 24:62 */     if ((object instanceof String))
/* 25:   */     {
/* 26:63 */       String string = (String)object;
/* 27:64 */       boolean valid = (string != null) && (!string.trim().equals(""));
/* 28:65 */       if (!valid) {
/* 29:66 */         populateMessage(message, fieldLabel, new Object[0]);
/* 30:   */       }
/* 31:   */     }
/* 32:70 */     else if (object == null)
/* 33:   */     {
/* 34:71 */       populateMessage(message, fieldLabel, new Object[0]);
/* 35:   */     }
/* 36:75 */     return message;
/* 37:   */   }
/* 38:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.validators.RequiredValidator
 * JD-Core Version:    0.7.0.1
 */