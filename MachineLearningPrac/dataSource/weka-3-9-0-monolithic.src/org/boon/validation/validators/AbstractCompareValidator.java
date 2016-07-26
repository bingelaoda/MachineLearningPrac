/*   1:    */ package org.boon.validation.validators;
/*   2:    */ 
/*   3:    */ import org.boon.validation.ValidationContext;
/*   4:    */ import org.boon.validation.ValidatorMessage;
/*   5:    */ import org.boon.validation.ValidatorMessageHolder;
/*   6:    */ 
/*   7:    */ public abstract class AbstractCompareValidator
/*   8:    */   extends BaseValidator
/*   9:    */ {
/*  10:    */   private String compareToProperty;
/*  11:    */   
/*  12:    */   public ValidatorMessageHolder validate(Object value, String fieldLabel)
/*  13:    */   {
/*  14: 65 */     ValidatorMessage message = new ValidatorMessage();
/*  15: 67 */     if (value == null) {
/*  16: 68 */       return message;
/*  17:    */     }
/*  18: 72 */     Object compareToPropertyValue = lookupCompareToPropertyValue();
/*  19:    */     
/*  20:    */ 
/*  21: 75 */     boolean valid = checkValidity(value, compareToPropertyValue);
/*  22: 80 */     if (!valid) {
/*  23: 81 */       populateMessage(message, fieldLabel, new Object[0]);
/*  24:    */     }
/*  25: 84 */     return message;
/*  26:    */   }
/*  27:    */   
/*  28:    */   protected abstract boolean checkValidity(Object paramObject1, Object paramObject2);
/*  29:    */   
/*  30:    */   protected Object lookupCompareToPropertyValue()
/*  31:    */   {
/*  32:109 */     return ValidationContext.getCurrentInstance().getProposedPropertyValue(this.compareToProperty);
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void setCompareToProperty(String compareToProperty)
/*  36:    */   {
/*  37:118 */     this.compareToProperty = compareToProperty;
/*  38:    */   }
/*  39:    */   
/*  40:    */   protected String getCompareToProperty()
/*  41:    */   {
/*  42:123 */     return this.compareToProperty;
/*  43:    */   }
/*  44:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.validators.AbstractCompareValidator
 * JD-Core Version:    0.7.0.1
 */