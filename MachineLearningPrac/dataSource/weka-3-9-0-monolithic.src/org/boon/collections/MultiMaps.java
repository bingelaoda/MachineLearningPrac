/*  1:   */ package org.boon.collections;
/*  2:   */ 
/*  3:   */ import java.util.ArrayList;
/*  4:   */ 
/*  5:   */ public class MultiMaps
/*  6:   */ {
/*  7:   */   public static <K, V> MultiMap<K, V> multiMap()
/*  8:   */   {
/*  9:11 */     return new MultiMapImpl(ArrayList.class);
/* 10:   */   }
/* 11:   */   
/* 12:   */   public static MultiMap safeMultiMap()
/* 13:   */   {
/* 14:15 */     return new MultiMapImpl(ConcurrentLinkedHashSet.class);
/* 15:   */   }
/* 16:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.collections.MultiMaps
 * JD-Core Version:    0.7.0.1
 */