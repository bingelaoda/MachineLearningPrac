/*   1:    */ package org.boon.cache;
/*   2:    */ 
/*   3:    */ import java.util.Deque;
/*   4:    */ import java.util.Map;
/*   5:    */ import java.util.concurrent.ConcurrentHashMap;
/*   6:    */ import java.util.concurrent.ConcurrentLinkedDeque;
/*   7:    */ 
/*   8:    */ public class ConcurrentLruCache<KEY, VALUE>
/*   9:    */   implements Cache<KEY, VALUE>
/*  10:    */ {
/*  11: 52 */   private final Map<KEY, VALUE> map = new ConcurrentHashMap();
/*  12: 56 */   private final Deque<KEY> queue = new ConcurrentLinkedDeque();
/*  13:    */   private final int limit;
/*  14:    */   
/*  15:    */   public ConcurrentLruCache(int limit)
/*  16:    */   {
/*  17: 64 */     this.limit = limit;
/*  18:    */   }
/*  19:    */   
/*  20:    */   public void put(KEY key, VALUE value)
/*  21:    */   {
/*  22: 74 */     VALUE oldValue = this.map.put(key, value);
/*  23: 75 */     if (oldValue != null) {
/*  24: 76 */       removeThenAddKey(key);
/*  25:    */     } else {
/*  26: 78 */       addKey(key);
/*  27:    */     }
/*  28: 80 */     if (this.map.size() > this.limit) {
/*  29: 81 */       this.map.remove(removeLast());
/*  30:    */     }
/*  31:    */   }
/*  32:    */   
/*  33:    */   public VALUE get(KEY key)
/*  34:    */   {
/*  35: 93 */     removeThenAddKey(key);
/*  36: 94 */     return this.map.get(key);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public VALUE getSilent(KEY key)
/*  40:    */   {
/*  41:105 */     return this.map.get(key);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void remove(KEY key)
/*  45:    */   {
/*  46:114 */     removeFirstOccurrence(key);
/*  47:115 */     this.map.remove(key);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public int size()
/*  51:    */   {
/*  52:124 */     return this.map.size();
/*  53:    */   }
/*  54:    */   
/*  55:    */   private void addKey(KEY key)
/*  56:    */   {
/*  57:130 */     this.queue.addFirst(key);
/*  58:    */   }
/*  59:    */   
/*  60:    */   private KEY removeLast()
/*  61:    */   {
/*  62:135 */     KEY removedKey = this.queue.removeLast();
/*  63:136 */     return removedKey;
/*  64:    */   }
/*  65:    */   
/*  66:    */   private void removeThenAddKey(KEY key)
/*  67:    */   {
/*  68:146 */     this.queue.removeFirstOccurrence(key);
/*  69:147 */     this.queue.addFirst(key);
/*  70:    */   }
/*  71:    */   
/*  72:    */   private void removeFirstOccurrence(KEY key)
/*  73:    */   {
/*  74:156 */     this.queue.removeFirstOccurrence(key);
/*  75:    */   }
/*  76:    */   
/*  77:    */   public String toString()
/*  78:    */   {
/*  79:163 */     return this.map.toString();
/*  80:    */   }
/*  81:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.cache.ConcurrentLruCache
 * JD-Core Version:    0.7.0.1
 */