/*  1:   */ package org.boon.datarepo.impl.decorators;
/*  2:   */ 
/*  3:   */ import org.boon.cache.Cache;
/*  4:   */ import org.boon.cache.CacheType;
/*  5:   */ import org.boon.cache.SimpleConcurrentCache;
/*  6:   */ import org.boon.criteria.ObjectFilter;
/*  7:   */ import org.boon.criteria.internal.Criteria;
/*  8:   */ import org.boon.criteria.internal.Group;
/*  9:   */ import org.boon.datarepo.Filter;
/* 10:   */ import org.boon.datarepo.ResultSet;
/* 11:   */ 
/* 12:   */ public class FilterWithSimpleCache
/* 13:   */   extends FilterDecoratorBase
/* 14:   */ {
/* 15:43 */   private Cache<Criteria, ResultSet> fifoCache = new SimpleConcurrentCache(50, false, CacheType.FIFO);
/* 16:44 */   private Cache<Criteria, ResultSet> lruCache = new SimpleConcurrentCache(1000, false, CacheType.LRU);
/* 17:   */   
/* 18:   */   public ResultSet filter(Criteria... expressions)
/* 19:   */   {
/* 20:49 */     Group and = ObjectFilter.and(expressions);
/* 21:   */     
/* 22:51 */     ResultSet results = (ResultSet)this.fifoCache.get(and);
/* 23:54 */     if (results == null)
/* 24:   */     {
/* 25:55 */       results = (ResultSet)this.lruCache.get(and);
/* 26:56 */       if (results != null)
/* 27:   */       {
/* 28:57 */         this.fifoCache.put(and, results);
/* 29:58 */         return results;
/* 30:   */       }
/* 31:   */     }
/* 32:63 */     results = super.filter(expressions);
/* 33:   */     
/* 34:65 */     this.fifoCache.put(and, results);
/* 35:66 */     this.lruCache.put(and, results);
/* 36:   */     
/* 37:68 */     return results;
/* 38:   */   }
/* 39:   */   
/* 40:   */   public void invalidate()
/* 41:   */   {
/* 42:75 */     this.fifoCache = new SimpleConcurrentCache(50, false, CacheType.FIFO);
/* 43:76 */     this.lruCache = new SimpleConcurrentCache(1000, false, CacheType.LRU);
/* 44:77 */     super.invalidate();
/* 45:   */   }
/* 46:   */   
/* 47:   */   public FilterWithSimpleCache(Filter delegate)
/* 48:   */   {
/* 49:81 */     super(delegate);
/* 50:   */   }
/* 51:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.decorators.FilterWithSimpleCache
 * JD-Core Version:    0.7.0.1
 */