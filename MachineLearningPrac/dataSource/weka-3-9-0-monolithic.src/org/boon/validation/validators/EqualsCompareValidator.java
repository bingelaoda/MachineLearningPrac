/*  1:   */ package org.boon.validation.validators;
/*  2:   */ 
/*  3:   */ public class EqualsCompareValidator
/*  4:   */   extends AbstractCompareValidator
/*  5:   */ {
/*  6:   */   protected boolean checkValidity(Object object, Object compareToPropertyValue)
/*  7:   */   {
/*  8:38 */     boolean valid = object.equals(compareToPropertyValue);
/*  9:39 */     return valid;
/* 10:   */   }
/* 11:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.validators.EqualsCompareValidator
 * JD-Core Version:    0.7.0.1
 */