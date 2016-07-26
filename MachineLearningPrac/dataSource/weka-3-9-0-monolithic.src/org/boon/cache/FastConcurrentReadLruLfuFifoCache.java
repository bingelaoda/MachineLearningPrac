/*   1:    */ package org.boon.cache;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.LinkedList;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.concurrent.ConcurrentHashMap;
/*   7:    */ import java.util.concurrent.atomic.AtomicInteger;
/*   8:    */ import org.boon.collections.SortableConcurrentList;
/*   9:    */ import org.boon.core.timer.TimeKeeper;
/*  10:    */ import org.boon.core.timer.TimeKeeperBasic;
/*  11:    */ 
/*  12:    */ public class FastConcurrentReadLruLfuFifoCache<KEY, VALUE>
/*  13:    */   implements Cache<KEY, VALUE>
/*  14:    */ {
/*  15: 59 */   private final ConcurrentHashMap<KEY, CacheEntry<KEY, VALUE>> map = new ConcurrentHashMap();
/*  16:    */   private final SortableConcurrentList<CacheEntry<KEY, VALUE>> list;
/*  17:    */   private final int evictSize;
/*  18: 74 */   private final AtomicInteger count = new AtomicInteger();
/*  19:    */   private final CacheType type;
/*  20:    */   private final TimeKeeper timeKeeper;
/*  21:    */   
/*  22:    */   public FastConcurrentReadLruLfuFifoCache(int evictSize)
/*  23:    */   {
/*  24: 91 */     this.evictSize = ((int)(evictSize + evictSize * 0.2F));
/*  25: 92 */     this.list = new SortableConcurrentList();
/*  26: 93 */     this.type = CacheType.LFU;
/*  27: 94 */     this.timeKeeper = new TimeKeeperBasic();
/*  28:    */   }
/*  29:    */   
/*  30:    */   public FastConcurrentReadLruLfuFifoCache(int evictSize, Tradeoffs tradeoffs, CacheType type)
/*  31:    */   {
/*  32:106 */     this.evictSize = ((int)(evictSize + evictSize * 0.2F));
/*  33:    */     
/*  34:108 */     this.type = type;
/*  35:110 */     if (tradeoffs == Tradeoffs.FAST_REMOVE) {
/*  36:111 */       this.list = new SortableConcurrentList(new LinkedList());
/*  37:112 */     } else if (tradeoffs == Tradeoffs.FAST_SORT) {
/*  38:113 */       this.list = new SortableConcurrentList(new ArrayList());
/*  39:    */     } else {
/*  40:115 */       this.list = new SortableConcurrentList();
/*  41:    */     }
/*  42:119 */     this.timeKeeper = new TimeKeeperBasic();
/*  43:    */   }
/*  44:    */   
/*  45:    */   FastConcurrentReadLruLfuFifoCache(boolean test, int evictSize, CacheType type)
/*  46:    */   {
/*  47:130 */     this.evictSize = ((int)(evictSize + evictSize * 0.2F));
/*  48:131 */     this.list = new SortableConcurrentList();
/*  49:132 */     this.type = type;
/*  50:    */     
/*  51:134 */     this.timeKeeper = new TimeKeeper()
/*  52:    */     {
/*  53:    */       int i;
/*  54:    */       
/*  55:    */       public long time()
/*  56:    */       {
/*  57:139 */         return System.currentTimeMillis() + this.i++;
/*  58:    */       }
/*  59:    */     };
/*  60:    */   }
/*  61:    */   
/*  62:    */   public VALUE get(KEY key)
/*  63:    */   {
/*  64:152 */     CacheEntry<KEY, VALUE> cacheEntry = (CacheEntry)this.map.get(key);
/*  65:153 */     if (cacheEntry != null)
/*  66:    */     {
/*  67:154 */       cacheEntry.readCount.incrementAndGet();
/*  68:155 */       return cacheEntry.value;
/*  69:    */     }
/*  70:157 */     return null;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public VALUE getSilent(KEY key)
/*  74:    */   {
/*  75:168 */     CacheEntry<KEY, VALUE> cacheEntry = (CacheEntry)this.map.get(key);
/*  76:169 */     if (cacheEntry != null) {
/*  77:170 */       return cacheEntry.value;
/*  78:    */     }
/*  79:172 */     return null;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void remove(KEY key)
/*  83:    */   {
/*  84:184 */     CacheEntry<KEY, VALUE> entry = (CacheEntry)this.map.remove(key);
/*  85:185 */     if (entry != null) {
/*  86:186 */       this.list.remove(entry);
/*  87:    */     }
/*  88:    */   }
/*  89:    */   
/*  90:    */   public void put(KEY key, VALUE value)
/*  91:    */   {
/*  92:196 */     CacheEntry<KEY, VALUE> entry = (CacheEntry)this.map.get(key);
/*  93:199 */     if (entry == null)
/*  94:    */     {
/*  95:200 */       entry = new CacheEntry(key, value, order(), this.type, time());
/*  96:201 */       this.list.add(entry);
/*  97:202 */       this.map.put(key, entry);
/*  98:    */     }
/*  99:    */     else
/* 100:    */     {
/* 101:204 */       entry.readCount.incrementAndGet();
/* 102:205 */       entry.value = value;
/* 103:    */     }
/* 104:207 */     evictIfNeeded();
/* 105:    */   }
/* 106:    */   
/* 107:    */   private long time()
/* 108:    */   {
/* 109:212 */     return this.timeKeeper.time();
/* 110:    */   }
/* 111:    */   
/* 112:    */   private final int order()
/* 113:    */   {
/* 114:218 */     int order = this.count.incrementAndGet();
/* 115:219 */     if (order > 2147483547) {
/* 116:220 */       this.count.set(0);
/* 117:    */     }
/* 118:222 */     return order;
/* 119:    */   }
/* 120:    */   
/* 121:    */   private final void evictIfNeeded()
/* 122:    */   {
/* 123:227 */     if (this.list.size() > this.evictSize)
/* 124:    */     {
/* 125:229 */       List<CacheEntry<KEY, VALUE>> killList = this.list.sortAndReturnPurgeList(0.1F);
/* 126:231 */       for (CacheEntry<KEY, VALUE> cacheEntry : killList) {
/* 127:232 */         this.map.remove(cacheEntry.key);
/* 128:    */       }
/* 129:    */     }
/* 130:    */   }
/* 131:    */   
/* 132:    */   public String toString()
/* 133:    */   {
/* 134:239 */     return this.map.toString();
/* 135:    */   }
/* 136:    */   
/* 137:    */   public int size()
/* 138:    */   {
/* 139:248 */     return this.map.size();
/* 140:    */   }
/* 141:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.cache.FastConcurrentReadLruLfuFifoCache
 * JD-Core Version:    0.7.0.1
 */