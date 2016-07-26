/*  1:   */ package org.boon.criteria.internal;
/*  2:   */ 
/*  3:   */ import java.util.ArrayList;
/*  4:   */ import java.util.Collection;
/*  5:   */ import java.util.Collections;
/*  6:   */ import java.util.List;
/*  7:   */ import org.boon.criteria.ObjectFilter;
/*  8:   */ 
/*  9:   */ public class QueryFactory
/* 10:   */ {
/* 11:   */   public static boolean test(Object obj, Criteria exp)
/* 12:   */   {
/* 13:43 */     return exp.test(obj);
/* 14:   */   }
/* 15:   */   
/* 16:   */   public static boolean andTest(Object obj, Criteria... exp)
/* 17:   */   {
/* 18:47 */     return ObjectFilter.and(exp).test(obj);
/* 19:   */   }
/* 20:   */   
/* 21:   */   public static boolean orTest(Object obj, Criteria... exp)
/* 22:   */   {
/* 23:51 */     return ObjectFilter.or(exp).test(obj);
/* 24:   */   }
/* 25:   */   
/* 26:   */   public static <T> List<T> filter(Collection<T> items, Criteria exp)
/* 27:   */   {
/* 28:56 */     if (items.size() == 0) {
/* 29:57 */       return Collections.EMPTY_LIST;
/* 30:   */     }
/* 31:60 */     List<T> results = new ArrayList();
/* 32:61 */     for (T item : items) {
/* 33:62 */       if (exp.test(item)) {
/* 34:63 */         results.add(item);
/* 35:   */       }
/* 36:   */     }
/* 37:66 */     return results;
/* 38:   */   }
/* 39:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.criteria.internal.QueryFactory
 * JD-Core Version:    0.7.0.1
 */