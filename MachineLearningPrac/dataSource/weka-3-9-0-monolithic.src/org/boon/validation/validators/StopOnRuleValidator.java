/*  1:   */ package org.boon.validation.validators;
/*  2:   */ 
/*  3:   */ import org.boon.validation.FieldValidator;
/*  4:   */ import org.boon.validation.ValidatorMessage;
/*  5:   */ import org.boon.validation.ValidatorMessageHolder;
/*  6:   */ 
/*  7:   */ public class StopOnRuleValidator
/*  8:   */   implements FieldValidator
/*  9:   */ {
/* 10:   */   private String ruleName;
/* 11:   */   
/* 12:   */   public String getRuleName()
/* 13:   */   {
/* 14:49 */     return this.ruleName;
/* 15:   */   }
/* 16:   */   
/* 17:   */   public void setRuleName(String ruleName)
/* 18:   */   {
/* 19:53 */     this.ruleName = ruleName;
/* 20:   */   }
/* 21:   */   
/* 22:   */   public ValidatorMessageHolder validate(Object fieldValue, String fieldLabel)
/* 23:   */   {
/* 24:57 */     return new ValidatorMessage();
/* 25:   */   }
/* 26:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.validators.StopOnRuleValidator
 * JD-Core Version:    0.7.0.1
 */