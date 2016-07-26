/*  1:   */ package org.boon.validation.validators;
/*  2:   */ 
/*  3:   */ public abstract class AbstractRangeValidator
/*  4:   */   extends BaseValidator
/*  5:   */ {
/*  6:   */   private Comparable realMin;
/*  7:   */   private Comparable realMax;
/*  8:   */   private boolean isInitialized;
/*  9:   */   
/* 10:   */   protected void init(Comparable min, Comparable max)
/* 11:   */   {
/* 12:47 */     this.realMin = min;
/* 13:48 */     this.realMax = max;
/* 14:49 */     assert (min.compareTo(max) < 0);
/* 15:50 */     this.isInitialized = true;
/* 16:   */   }
/* 17:   */   
/* 18:   */   protected boolean isValueGreaterThanMin(Comparable value)
/* 19:   */   {
/* 20:55 */     if (this.realMin == null) {
/* 21:56 */       return true;
/* 22:   */     }
/* 23:58 */     return value.compareTo(this.realMin) >= 0;
/* 24:   */   }
/* 25:   */   
/* 26:   */   protected boolean isValueLessThanMax(Comparable value)
/* 27:   */   {
/* 28:63 */     if (this.realMax == null) {
/* 29:64 */       return true;
/* 30:   */     }
/* 31:66 */     return value.compareTo(this.realMax) <= 0;
/* 32:   */   }
/* 33:   */   
/* 34:   */   public boolean isInitialized()
/* 35:   */   {
/* 36:70 */     return this.isInitialized;
/* 37:   */   }
/* 38:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.validation.validators.AbstractRangeValidator
 * JD-Core Version:    0.7.0.1
 */