/*   1:    */ package org.boon.cache;
/*   2:    */ 
/*   3:    */ import java.util.concurrent.atomic.AtomicInteger;
/*   4:    */ import org.boon.Exceptions;
/*   5:    */ 
/*   6:    */ class CacheEntry<KEY, VALUE>
/*   7:    */   implements Comparable<CacheEntry>
/*   8:    */ {
/*   9: 43 */   final AtomicInteger readCount = new AtomicInteger();
/*  10:    */   final int order;
/*  11:    */   VALUE value;
/*  12:    */   final KEY key;
/*  13:    */   final CacheType type;
/*  14:    */   final long time;
/*  15:    */   
/*  16:    */   CacheEntry(KEY key, VALUE value, int order, CacheType type, long time)
/*  17:    */   {
/*  18: 70 */     this.order = order;
/*  19: 71 */     this.value = value;
/*  20: 72 */     this.key = key;
/*  21: 73 */     this.time = time;
/*  22: 74 */     this.type = type;
/*  23:    */   }
/*  24:    */   
/*  25:    */   public final int compareTo(CacheEntry other)
/*  26:    */   {
/*  27: 86 */     switch (1.$SwitchMap$org$boon$cache$CacheType[this.type.ordinal()])
/*  28:    */     {
/*  29:    */     case 1: 
/*  30: 89 */       return compareToLFU(other);
/*  31:    */     case 2: 
/*  32: 91 */       return compareToLRU(other);
/*  33:    */     case 3: 
/*  34: 93 */       return compareToFIFO(other);
/*  35:    */     }
/*  36: 95 */     Exceptions.die();
/*  37: 96 */     return 0;
/*  38:    */   }
/*  39:    */   
/*  40:    */   private final int compareReadCount(CacheEntry other)
/*  41:    */   {
/*  42:109 */     if (this.readCount.get() > other.readCount.get()) {
/*  43:110 */       return 1;
/*  44:    */     }
/*  45:111 */     if (this.readCount.get() < other.readCount.get()) {
/*  46:112 */       return -1;
/*  47:    */     }
/*  48:113 */     if (this.readCount.get() == other.readCount.get()) {
/*  49:114 */       return 0;
/*  50:    */     }
/*  51:116 */     Exceptions.die();
/*  52:117 */     return 0;
/*  53:    */   }
/*  54:    */   
/*  55:    */   private final int compareTime(CacheEntry other)
/*  56:    */   {
/*  57:127 */     if (this.time > other.time) {
/*  58:128 */       return 1;
/*  59:    */     }
/*  60:129 */     if (this.time < other.time) {
/*  61:130 */       return -1;
/*  62:    */     }
/*  63:131 */     if (this.time == other.time) {
/*  64:132 */       return 0;
/*  65:    */     }
/*  66:134 */     Exceptions.die();
/*  67:135 */     return 0;
/*  68:    */   }
/*  69:    */   
/*  70:    */   private final int compareOrder(CacheEntry other)
/*  71:    */   {
/*  72:145 */     if (this.order > other.order) {
/*  73:146 */       return 1;
/*  74:    */     }
/*  75:147 */     if (this.order < other.order) {
/*  76:148 */       return -1;
/*  77:    */     }
/*  78:149 */     if (this.order == other.order) {
/*  79:150 */       return 0;
/*  80:    */     }
/*  81:152 */     Exceptions.die();
/*  82:153 */     return 0;
/*  83:    */   }
/*  84:    */   
/*  85:    */   private final int compareToLFU(CacheEntry other)
/*  86:    */   {
/*  87:163 */     int cmp = compareReadCount(other);
/*  88:164 */     if (cmp != 0) {
/*  89:165 */       return cmp;
/*  90:    */     }
/*  91:168 */     cmp = compareTime(other);
/*  92:169 */     if (cmp != 0) {
/*  93:170 */       return cmp;
/*  94:    */     }
/*  95:173 */     return compareOrder(other);
/*  96:    */   }
/*  97:    */   
/*  98:    */   private final int compareToLRU(CacheEntry other)
/*  99:    */   {
/* 100:184 */     int cmp = compareTime(other);
/* 101:185 */     if (cmp != 0) {
/* 102:186 */       return cmp;
/* 103:    */     }
/* 104:190 */     cmp = compareOrder(other);
/* 105:191 */     if (cmp != 0) {
/* 106:192 */       return cmp;
/* 107:    */     }
/* 108:196 */     return compareReadCount(other);
/* 109:    */   }
/* 110:    */   
/* 111:    */   private final int compareToFIFO(CacheEntry other)
/* 112:    */   {
/* 113:206 */     int cmp = compareOrder(other);
/* 114:207 */     if (cmp != 0) {
/* 115:208 */       return cmp;
/* 116:    */     }
/* 117:212 */     cmp = compareTime(other);
/* 118:213 */     if (cmp != 0) {
/* 119:214 */       return cmp;
/* 120:    */     }
/* 121:218 */     return cmp = compareReadCount(other);
/* 122:    */   }
/* 123:    */   
/* 124:    */   public String toString()
/* 125:    */   {
/* 126:223 */     return "CE{c=" + this.readCount + ", ord=" + this.order + ", val=" + this.value + ", ky=" + this.key + ", typ=" + this.type + ", t=" + this.time + '}';
/* 127:    */   }
/* 128:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.cache.CacheEntry
 * JD-Core Version:    0.7.0.1
 */