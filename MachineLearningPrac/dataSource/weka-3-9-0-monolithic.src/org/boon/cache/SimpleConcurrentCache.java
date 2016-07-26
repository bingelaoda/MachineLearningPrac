/*   1:    */ package org.boon.cache;
/*   2:    */ 
/*   3:    */ import java.util.Set;
/*   4:    */ import java.util.concurrent.locks.Lock;
/*   5:    */ import java.util.concurrent.locks.ReadWriteLock;
/*   6:    */ import java.util.concurrent.locks.ReentrantReadWriteLock;
/*   7:    */ import org.boon.core.reflection.ClassMeta;
/*   8:    */ import org.boon.core.reflection.MethodAccess;
/*   9:    */ import org.boon.primitive.Int;
/*  10:    */ 
/*  11:    */ public class SimpleConcurrentCache<K, V>
/*  12:    */   implements Cache<K, V>
/*  13:    */ {
/*  14:    */   final SimpleCache<K, V>[] cacheRegions;
/*  15:    */   private static final boolean useFastHash;
/*  16: 63 */   private final transient int hashSeed = randomHashSeed(this);
/*  17:    */   static final MethodAccess randomHashSeedMethod;
/*  18:    */   
/*  19:    */   private static class SimpleThreadSafeCache<K, V>
/*  20:    */     extends SimpleCache<K, V>
/*  21:    */   {
/*  22:    */     private final ReadWriteLock readWriteLock;
/*  23:    */     
/*  24:    */     SimpleThreadSafeCache(int limit, CacheType type, boolean fair)
/*  25:    */     {
/*  26: 77 */       super(type);
/*  27: 78 */       this.readWriteLock = new ReentrantReadWriteLock(fair);
/*  28:    */     }
/*  29:    */     
/*  30:    */     public void put(K key, V value)
/*  31:    */     {
/*  32: 84 */       this.readWriteLock.writeLock().lock();
/*  33:    */       try
/*  34:    */       {
/*  35: 87 */         super.put(key, value);
/*  36:    */       }
/*  37:    */       finally
/*  38:    */       {
/*  39: 89 */         this.readWriteLock.writeLock().unlock();
/*  40:    */       }
/*  41:    */     }
/*  42:    */     
/*  43:    */     public V get(K key)
/*  44:    */     {
/*  45: 96 */       this.readWriteLock.writeLock().lock();
/*  46:    */       V value;
/*  47:    */       try
/*  48:    */       {
/*  49:101 */         value = super.get(key);
/*  50:    */       }
/*  51:    */       finally
/*  52:    */       {
/*  53:103 */         this.readWriteLock.writeLock().unlock();
/*  54:    */       }
/*  55:105 */       return value;
/*  56:    */     }
/*  57:    */     
/*  58:    */     public void remove(K key)
/*  59:    */     {
/*  60:111 */       this.readWriteLock.writeLock().lock();
/*  61:    */       try
/*  62:    */       {
/*  63:115 */         super.remove(key);
/*  64:    */       }
/*  65:    */       finally
/*  66:    */       {
/*  67:117 */         this.readWriteLock.writeLock().unlock();
/*  68:    */       }
/*  69:    */     }
/*  70:    */     
/*  71:    */     public V getSilent(K key)
/*  72:    */     {
/*  73:123 */       this.readWriteLock.writeLock().lock();
/*  74:    */       V value;
/*  75:    */       try
/*  76:    */       {
/*  77:129 */         value = super.getSilent(key);
/*  78:    */       }
/*  79:    */       finally
/*  80:    */       {
/*  81:131 */         this.readWriteLock.writeLock().unlock();
/*  82:    */       }
/*  83:134 */       return value;
/*  84:    */     }
/*  85:    */     
/*  86:    */     public int size()
/*  87:    */     {
/*  88:139 */       this.readWriteLock.readLock().lock();
/*  89:140 */       int size = -1;
/*  90:    */       try
/*  91:    */       {
/*  92:142 */         size = super.size();
/*  93:    */       }
/*  94:    */       finally
/*  95:    */       {
/*  96:144 */         this.readWriteLock.readLock().unlock();
/*  97:    */       }
/*  98:146 */       return size;
/*  99:    */     }
/* 100:    */     
/* 101:    */     public String toString()
/* 102:    */     {
/* 103:150 */       this.readWriteLock.readLock().lock();
/* 104:    */       String str;
/* 105:    */       try
/* 106:    */       {
/* 107:153 */         str = super.toString();
/* 108:    */       }
/* 109:    */       finally
/* 110:    */       {
/* 111:155 */         this.readWriteLock.readLock().unlock();
/* 112:    */       }
/* 113:157 */       return str;
/* 114:    */     }
/* 115:    */   }
/* 116:    */   
/* 117:    */   public SimpleConcurrentCache(int limit)
/* 118:    */   {
/* 119:169 */     this(limit, false, CacheType.LRU);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public SimpleConcurrentCache(int limit, CacheType type)
/* 123:    */   {
/* 124:178 */     this(limit, false, type);
/* 125:    */   }
/* 126:    */   
/* 127:    */   public SimpleConcurrentCache(int limit, boolean fair, CacheType type)
/* 128:    */   {
/* 129:188 */     int cores = Runtime.getRuntime().availableProcessors();
/* 130:189 */     int stripeSize = cores < 2 ? 8 : cores * 4;
/* 131:190 */     stripeSize = Int.roundUpToPowerOf2(stripeSize);
/* 132:191 */     this.cacheRegions = new SimpleCache[stripeSize];
/* 133:192 */     for (int index = 0; index < this.cacheRegions.length; index++) {
/* 134:193 */       this.cacheRegions[index] = new SimpleThreadSafeCache(limit / this.cacheRegions.length, type, fair);
/* 135:    */     }
/* 136:    */   }
/* 137:    */   
/* 138:    */   public SimpleConcurrentCache(int concurrency, int limit, boolean fair, CacheType type)
/* 139:    */   {
/* 140:214 */     int stripeSize = Int.roundUpToPowerOf2(concurrency);
/* 141:215 */     this.cacheRegions = new SimpleCache[stripeSize];
/* 142:216 */     for (int index = 0; index < this.cacheRegions.length; index++) {
/* 143:217 */       this.cacheRegions[index] = new SimpleThreadSafeCache(limit / this.cacheRegions.length, type, fair);
/* 144:    */     }
/* 145:    */   }
/* 146:    */   
/* 147:    */   public SimpleConcurrentCache(int concurrency, int limit, boolean fair)
/* 148:    */   {
/* 149:232 */     int stripeSize = Int.roundUpToPowerOf2(concurrency);
/* 150:233 */     this.cacheRegions = new SimpleCache[stripeSize];
/* 151:234 */     for (int index = 0; index < this.cacheRegions.length; index++) {
/* 152:235 */       this.cacheRegions[index] = new SimpleThreadSafeCache(limit / this.cacheRegions.length, CacheType.LRU, fair);
/* 153:    */     }
/* 154:    */   }
/* 155:    */   
/* 156:    */   private SimpleCache<K, V> map(K key)
/* 157:    */   {
/* 158:241 */     return this.cacheRegions[stripeIndex(key)];
/* 159:    */   }
/* 160:    */   
/* 161:    */   public void put(K key, V value)
/* 162:    */   {
/* 163:252 */     map(key).put(key, value);
/* 164:    */   }
/* 165:    */   
/* 166:    */   public V get(K key)
/* 167:    */   {
/* 168:262 */     return map(key).get(key);
/* 169:    */   }
/* 170:    */   
/* 171:    */   public V getSilent(K key)
/* 172:    */   {
/* 173:273 */     return map(key).getSilent(key);
/* 174:    */   }
/* 175:    */   
/* 176:    */   public void remove(K key)
/* 177:    */   {
/* 178:283 */     map(key).remove(key);
/* 179:    */   }
/* 180:    */   
/* 181:    */   public int size()
/* 182:    */   {
/* 183:291 */     int size = 0;
/* 184:292 */     for (SimpleCache<K, V> cache : this.cacheRegions) {
/* 185:293 */       size += cache.size();
/* 186:    */     }
/* 187:295 */     return size;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public String toString()
/* 191:    */   {
/* 192:304 */     StringBuilder builder = new StringBuilder();
/* 193:305 */     for (SimpleCache<K, V> cache : this.cacheRegions) {
/* 194:306 */       builder.append(cache.toString()).append('\n');
/* 195:    */     }
/* 196:309 */     return builder.toString();
/* 197:    */   }
/* 198:    */   
/* 199:    */   static
/* 200:    */   {
/* 201:318 */     MethodAccess randomHashSeed = null;
/* 202:    */     boolean yes;
/* 203:    */     try
/* 204:    */     {
/* 205:320 */       Class cls = Class.forName("sun.misc.Hashing");
/* 206:    */       
/* 207:322 */       ClassMeta classMeta = ClassMeta.classMeta(cls);
/* 208:    */       
/* 209:324 */       yes = (classMeta.respondsTo("randomHashSeed", new Class[] { Object.class })) && (classMeta.classMethods().contains("randomHashSeed"));
/* 210:    */       
/* 211:    */ 
/* 212:327 */       randomHashSeed = classMeta.method("randomHashSeed");
/* 213:    */     }
/* 214:    */     catch (Exception ex)
/* 215:    */     {
/* 216:331 */       yes = false;
/* 217:    */     }
/* 218:334 */     useFastHash = yes;
/* 219:335 */     randomHashSeedMethod = randomHashSeed;
/* 220:    */   }
/* 221:    */   
/* 222:    */   private static int randomHashSeed(SimpleConcurrentCache instance)
/* 223:    */   {
/* 224:347 */     if (useFastHash) {
/* 225:349 */       return ((Integer)randomHashSeedMethod.invoke(instance, new Object[0])).intValue();
/* 226:    */     }
/* 227:352 */     return 0;
/* 228:    */   }
/* 229:    */   
/* 230:    */   private final int hash(Object k)
/* 231:    */   {
/* 232:362 */     int h = this.hashSeed;
/* 233:    */     
/* 234:364 */     h ^= k.hashCode();
/* 235:    */     
/* 236:366 */     h ^= h >>> 20 ^ h >>> 12;
/* 237:367 */     return h ^ h >>> 7 ^ h >>> 4;
/* 238:    */   }
/* 239:    */   
/* 240:    */   static int indexFor(int h, int length)
/* 241:    */   {
/* 242:375 */     return h & length - 1;
/* 243:    */   }
/* 244:    */   
/* 245:    */   private int stripeIndex(K key)
/* 246:    */   {
/* 247:385 */     return indexFor(hash(key), this.cacheRegions.length);
/* 248:    */   }
/* 249:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.cache.SimpleConcurrentCache
 * JD-Core Version:    0.7.0.1
 */