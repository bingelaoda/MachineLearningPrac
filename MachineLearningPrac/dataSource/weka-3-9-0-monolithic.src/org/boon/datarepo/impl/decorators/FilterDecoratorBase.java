/*  1:   */ package org.boon.datarepo.impl.decorators;
/*  2:   */ 
/*  3:   */ import org.boon.criteria.internal.Criteria;
/*  4:   */ import org.boon.datarepo.Filter;
/*  5:   */ import org.boon.datarepo.ResultSet;
/*  6:   */ 
/*  7:   */ public class FilterDecoratorBase
/*  8:   */   implements Filter
/*  9:   */ {
/* 10:   */   Filter delegate;
/* 11:   */   
/* 12:   */   FilterDecoratorBase(Filter delegate)
/* 13:   */   {
/* 14:43 */     this.delegate = delegate;
/* 15:   */   }
/* 16:   */   
/* 17:   */   public ResultSet filter(Criteria... expressions)
/* 18:   */   {
/* 19:48 */     return this.delegate.filter(expressions);
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void invalidate()
/* 23:   */   {
/* 24:53 */     this.delegate.invalidate();
/* 25:   */   }
/* 26:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.decorators.FilterDecoratorBase
 * JD-Core Version:    0.7.0.1
 */