/*  1:   */ package org.boon.criteria.internal;
/*  2:   */ 
/*  3:   */ public class Not
/*  4:   */   extends Criteria
/*  5:   */ {
/*  6:   */   private final Criteria expression;
/*  7:   */   
/*  8:   */   public Not(Criteria expression)
/*  9:   */   {
/* 10:37 */     this.expression = expression;
/* 11:   */   }
/* 12:   */   
/* 13:   */   public boolean test(Object owner)
/* 14:   */   {
/* 15:43 */     return !this.expression.test(owner);
/* 16:   */   }
/* 17:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.criteria.internal.Not
 * JD-Core Version:    0.7.0.1
 */