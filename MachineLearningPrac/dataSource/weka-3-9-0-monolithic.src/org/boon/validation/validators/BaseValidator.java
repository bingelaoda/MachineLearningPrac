/*  1:   */ package org.boon.validation.validators;
/*  2:   */ 
/*  3:   */ import org.boon.core.NameAware;
/*  4:   */ import org.boon.messages.MessageSpecification;
/*  5:   */ import org.boon.validation.FieldValidator;
/*  6:   */ import org.boon.validation.ValidatorMessage;
/*  7:   */ 
/*  8:   */ public abstract class BaseValidator
/*  9:   */   extends MessageSpecification
/* 10:   */   implements NameAware, FieldValidator
/* 11:   */ {
/* 12:50 */   public boolean noMessages = false;
/* 13:   */   
/* 14:   */   public boolean isNoMessages()
/* 15:   */   {
/* 16:53 */     return this.noMessages;
/* 17:   */   }
/* 18:   */   
/* 19:   */   public void setNoMessages(boolean noMessages)
/* 20:   */   {
/* 21:57 */     this.noMessages = noMessages;
/* 22:   */   }
/* 23:   */   
/* 24:   */   protected void populateMessage(ValidatorMessage message, String fieldLabel, Object... args)
/* 25:   */   {
/* 26:61 */     populateMessage(null, message, fieldLabel, args);
/* 27:   */   }
/* 28:   */   
/* 29:   */   protected void populateMessage(MessageSpecification ms, ValidatorMessage message, String fieldLabel, Object... args)
/* 30:   */   {
/* 31:65 */     if (ms == null) {
/* 32:66 */       ms = this;
/* 33:   */     }
/* 34:69 */     ms.setCurrentSubject(fieldLabel);
/* 35:70 */     if (!this.noMessages)
/* 36:   */     {
/* 37:71 */       message.setSummary(ms.createSummaryMessage(args));
/* 38:72 */       message.setDetail(ms.createDetailMessage(args));
/* 39:   */     }
/* 40:74 */     ms.setCurrentSubject(null);
/* 41:75 */     message.setHasError(true);
/* 42:   */   }
/* 43:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.validators.BaseValidator
 * JD-Core Version:    0.7.0.1
 */