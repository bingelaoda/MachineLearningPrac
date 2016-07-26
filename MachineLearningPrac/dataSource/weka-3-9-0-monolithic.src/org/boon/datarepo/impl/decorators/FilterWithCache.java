/*  1:   */ package org.boon.datarepo.impl.decorators;
/*  2:   */ 
/*  3:   */ import org.boon.cache.Cache;
/*  4:   */ import org.boon.core.Supplier;
/*  5:   */ import org.boon.criteria.ObjectFilter;
/*  6:   */ import org.boon.criteria.internal.Criteria;
/*  7:   */ import org.boon.criteria.internal.Group;
/*  8:   */ import org.boon.datarepo.Filter;
/*  9:   */ import org.boon.datarepo.ResultSet;
/* 10:   */ 
/* 11:   */ public class FilterWithCache
/* 12:   */   extends FilterDecoratorBase
/* 13:   */ {
/* 14:   */   private final Supplier<Cache<Group, ResultSet>> cacheFactory;
/* 15:   */   private Cache<Group, ResultSet> cache;
/* 16:   */   
/* 17:   */   public FilterWithCache(Filter delegate, Supplier<Cache<Group, ResultSet>> cacheFactory)
/* 18:   */   {
/* 19:46 */     super(delegate);
/* 20:47 */     this.cacheFactory = cacheFactory;
/* 21:48 */     this.cache = ((Cache)cacheFactory.get());
/* 22:   */   }
/* 23:   */   
/* 24:   */   public ResultSet filter(Criteria... expressions)
/* 25:   */   {
/* 26:53 */     Group and = ObjectFilter.and(expressions);
/* 27:   */     
/* 28:55 */     ResultSet results = (ResultSet)this.cache.get(and);
/* 29:58 */     if (results != null)
/* 30:   */     {
/* 31:59 */       this.cache.put(and, results);
/* 32:60 */       return results;
/* 33:   */     }
/* 34:64 */     results = super.filter(expressions);
/* 35:   */     
/* 36:66 */     this.cache.put(and, results);
/* 37:   */     
/* 38:68 */     return results;
/* 39:   */   }
/* 40:   */   
/* 41:   */   public void invalidate()
/* 42:   */   {
/* 43:74 */     this.cache = ((Cache)this.cacheFactory.get());
/* 44:75 */     super.invalidate();
/* 45:   */   }
/* 46:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.decorators.FilterWithCache
 * JD-Core Version:    0.7.0.1
 */