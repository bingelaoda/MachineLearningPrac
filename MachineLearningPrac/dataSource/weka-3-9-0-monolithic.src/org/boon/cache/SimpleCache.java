/*   1:    */ package org.boon.cache;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collection;
/*   5:    */ import java.util.LinkedHashMap;
/*   6:    */ import java.util.Map;
/*   7:    */ import java.util.Map.Entry;
/*   8:    */ 
/*   9:    */ public class SimpleCache<K, V>
/*  10:    */   implements Cache<K, V>
/*  11:    */ {
/*  12: 43 */   Map<K, V> map = new LinkedHashMap();
/*  13:    */   
/*  14:    */   private static class InternalCacheLinkedList<K, V>
/*  15:    */     extends LinkedHashMap<K, V>
/*  16:    */   {
/*  17:    */     final int limit;
/*  18:    */     
/*  19:    */     InternalCacheLinkedList(int limit, boolean lru)
/*  20:    */     {
/*  21: 50 */       super(0.75F, lru);
/*  22: 51 */       this.limit = limit;
/*  23:    */     }
/*  24:    */     
/*  25:    */     protected final boolean removeEldestEntry(Map.Entry<K, V> eldest)
/*  26:    */     {
/*  27: 55 */       return super.size() > this.limit;
/*  28:    */     }
/*  29:    */   }
/*  30:    */   
/*  31:    */   public SimpleCache(int limit, CacheType type)
/*  32:    */   {
/*  33: 62 */     if (type.equals(CacheType.LRU)) {
/*  34: 63 */       this.map = new InternalCacheLinkedList(limit, true);
/*  35:    */     } else {
/*  36: 65 */       this.map = new InternalCacheLinkedList(limit, false);
/*  37:    */     }
/*  38:    */   }
/*  39:    */   
/*  40:    */   public SimpleCache(int limit)
/*  41:    */   {
/*  42: 71 */     this.map = new InternalCacheLinkedList(limit, true);
/*  43:    */   }
/*  44:    */   
/*  45:    */   public void put(K key, V value)
/*  46:    */   {
/*  47: 77 */     this.map.put(key, value);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public V get(K key)
/*  51:    */   {
/*  52: 82 */     return this.map.get(key);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public V getSilent(K key)
/*  56:    */   {
/*  57: 88 */     V value = this.map.get(key);
/*  58: 89 */     if (value != null)
/*  59:    */     {
/*  60: 90 */       this.map.remove(key);
/*  61: 91 */       this.map.put(key, value);
/*  62:    */     }
/*  63: 93 */     return value;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void remove(K key)
/*  67:    */   {
/*  68: 98 */     this.map.remove(key);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public int size()
/*  72:    */   {
/*  73:103 */     return this.map.size();
/*  74:    */   }
/*  75:    */   
/*  76:    */   public String toString()
/*  77:    */   {
/*  78:107 */     return this.map.toString();
/*  79:    */   }
/*  80:    */   
/*  81:    */   public Collection<V> values()
/*  82:    */   {
/*  83:111 */     return new ArrayList(this.map.values());
/*  84:    */   }
/*  85:    */   
/*  86:    */   public Collection<K> keys()
/*  87:    */   {
/*  88:115 */     return new ArrayList(this.map.keySet());
/*  89:    */   }
/*  90:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.cache.SimpleCache
 * JD-Core Version:    0.7.0.1
 */