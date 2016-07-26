/*  1:   */ package org.boon.cache;
/*  2:   */ 
/*  3:   */ import java.util.Deque;
/*  4:   */ import java.util.HashMap;
/*  5:   */ import java.util.LinkedList;
/*  6:   */ import java.util.Map;
/*  7:   */ 
/*  8:   */ public class FastReaderSingleThreadedCache<KEY, VALUE>
/*  9:   */   implements Cache<KEY, VALUE>
/* 10:   */ {
/* 11:39 */   private final Map<KEY, VALUE> map = new HashMap();
/* 12:40 */   private final Deque<KEY> queue = new LinkedList();
/* 13:   */   private final int limit;
/* 14:   */   
/* 15:   */   public FastReaderSingleThreadedCache(int limit)
/* 16:   */   {
/* 17:45 */     this.limit = limit;
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void put(KEY key, VALUE value)
/* 21:   */   {
/* 22:49 */     VALUE oldValue = this.map.put(key, value);
/* 23:55 */     if (oldValue != null) {
/* 24:56 */       this.queue.removeFirstOccurrence(key);
/* 25:   */     }
/* 26:58 */     this.queue.addFirst(key);
/* 27:60 */     if (this.map.size() > this.limit)
/* 28:   */     {
/* 29:61 */       KEY removedKey = this.queue.removeLast();
/* 30:62 */       this.map.remove(removedKey);
/* 31:   */     }
/* 32:   */   }
/* 33:   */   
/* 34:   */   public VALUE get(KEY key)
/* 35:   */   {
/* 36:71 */     this.queue.removeFirstOccurrence(key);
/* 37:72 */     this.queue.addFirst(key);
/* 38:73 */     return this.map.get(key);
/* 39:   */   }
/* 40:   */   
/* 41:   */   public VALUE getSilent(KEY key)
/* 42:   */   {
/* 43:79 */     return this.map.get(key);
/* 44:   */   }
/* 45:   */   
/* 46:   */   public void remove(KEY key)
/* 47:   */   {
/* 48:85 */     this.queue.removeFirstOccurrence(key);
/* 49:86 */     this.map.remove(key);
/* 50:   */   }
/* 51:   */   
/* 52:   */   public int size()
/* 53:   */   {
/* 54:90 */     return this.map.size();
/* 55:   */   }
/* 56:   */   
/* 57:   */   public String toString()
/* 58:   */   {
/* 59:94 */     return this.map.toString();
/* 60:   */   }
/* 61:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.cache.FastReaderSingleThreadedCache
 * JD-Core Version:    0.7.0.1
 */