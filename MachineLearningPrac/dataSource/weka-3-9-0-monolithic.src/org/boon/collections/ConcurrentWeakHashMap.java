/*   1:    */ package org.boon.collections;
/*   2:    */ 
/*   3:    */ import java.lang.ref.ReferenceQueue;
/*   4:    */ import java.lang.ref.WeakReference;
/*   5:    */ import java.util.AbstractMap;
/*   6:    */ import java.util.AbstractSet;
/*   7:    */ import java.util.Collection;
/*   8:    */ import java.util.Iterator;
/*   9:    */ import java.util.Map;
/*  10:    */ import java.util.Map.Entry;
/*  11:    */ import java.util.Set;
/*  12:    */ import java.util.concurrent.ConcurrentHashMap;
/*  13:    */ import java.util.concurrent.ConcurrentMap;
/*  14:    */ import org.boon.Exceptions;
/*  15:    */ import org.boon.Pair;
/*  16:    */ 
/*  17:    */ public class ConcurrentWeakHashMap<K, V>
/*  18:    */   extends AbstractMap<K, V>
/*  19:    */   implements ConcurrentMap<K, V>
/*  20:    */ {
/*  21:    */   static final int DEFAULT_CONCURRENCY_LEVEL = 16;
/*  22:    */   static final float DEFAULT_LOAD_FACTOR = 0.75F;
/*  23:    */   static final int DEFAULT_INITIAL_CAPACITY = 16;
/*  24: 55 */   private static final ThreadLocal<HardRefKeyValue> HARD_REF = new ThreadLocal()
/*  25:    */   {
/*  26:    */     protected ConcurrentWeakHashMap.HardRefKeyValue initialValue()
/*  27:    */     {
/*  28: 58 */       return new ConcurrentWeakHashMap.HardRefKeyValue(null);
/*  29:    */     }
/*  30:    */   };
/*  31:    */   private final ConcurrentHashMap<KeyValue<K, V>, V> map;
/*  32: 62 */   private final ReferenceQueue<K> referenceQueue = new ReferenceQueue();
/*  33:    */   private ConcurrentWeakHashMap<K, V>.EntrySet entrySet;
/*  34:    */   
/*  35:    */   public ConcurrentWeakHashMap(int initialCapacity, float loadFactor, int concurrencyLevel)
/*  36:    */   {
/*  37: 69 */     this.map = new ConcurrentHashMap(initialCapacity, loadFactor, concurrencyLevel);
/*  38:    */   }
/*  39:    */   
/*  40:    */   public ConcurrentWeakHashMap(int initialCapacity, float loadFactor)
/*  41:    */   {
/*  42: 73 */     this(initialCapacity, loadFactor, 16);
/*  43:    */   }
/*  44:    */   
/*  45:    */   public ConcurrentWeakHashMap(int initialCapacity)
/*  46:    */   {
/*  47: 78 */     this(initialCapacity, 0.75F, 16);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public ConcurrentWeakHashMap()
/*  51:    */   {
/*  52: 83 */     this(16, 0.75F, 16);
/*  53:    */   }
/*  54:    */   
/*  55:    */   private HardRefKeyValue<K, V> createHardRef(K k)
/*  56:    */   {
/*  57: 87 */     HardRefKeyValue hardKey = (HardRefKeyValue)HARD_REF.get();
/*  58: 88 */     hardKey.set(k, null, k.hashCode());
/*  59: 89 */     return hardKey;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public V get(Object key)
/*  63:    */   {
/*  64: 96 */     if (key == null) {
/*  65: 97 */       Exceptions.die("Null keys not allowed");
/*  66:    */     }
/*  67: 99 */     HardRefKeyValue<K, V> hardRef = createHardRef(key);
/*  68:    */     
/*  69:101 */     V result = this.map.get(hardRef);
/*  70:102 */     hardRef.clear();
/*  71:103 */     return result;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public V put(K key, V value)
/*  75:    */   {
/*  76:108 */     evictEntires();
/*  77:110 */     if (key == null) {
/*  78:111 */       Exceptions.die("No null keys");
/*  79:    */     }
/*  80:113 */     KeyValue<K, V> weakKey = createWeakKey(key, value);
/*  81:114 */     return this.map.put(weakKey, value);
/*  82:    */   }
/*  83:    */   
/*  84:    */   public V remove(Object key)
/*  85:    */   {
/*  86:120 */     evictEntires();
/*  87:123 */     if (key == null) {
/*  88:124 */       Exceptions.die("Null keys not allowed");
/*  89:    */     }
/*  90:126 */     HardRefKeyValue<K, V> hardRef = createHardRef(key);
/*  91:    */     
/*  92:128 */     V removedValue = this.map.remove(hardRef);
/*  93:    */     
/*  94:130 */     hardRef.clear();
/*  95:    */     
/*  96:132 */     return removedValue;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public void putAll(Map<? extends K, ? extends V> map)
/* 100:    */   {
/* 101:138 */     Set<? extends Map.Entry<? extends K, ? extends V>> entries = map.entrySet();
/* 102:140 */     for (Map.Entry<? extends K, ? extends V> entry : entries) {
/* 103:141 */       put(entry.getKey(), entry.getValue());
/* 104:    */     }
/* 105:    */   }
/* 106:    */   
/* 107:    */   public int size()
/* 108:    */   {
/* 109:149 */     return entrySet().size();
/* 110:    */   }
/* 111:    */   
/* 112:    */   public void clear()
/* 113:    */   {
/* 114:154 */     evictEntires();
/* 115:155 */     this.map.clear();
/* 116:    */   }
/* 117:    */   
/* 118:    */   public Collection<V> values()
/* 119:    */   {
/* 120:161 */     evictEntires();
/* 121:162 */     return this.map.values();
/* 122:    */   }
/* 123:    */   
/* 124:    */   public boolean isEmpty()
/* 125:    */   {
/* 126:167 */     return entrySet().isEmpty();
/* 127:    */   }
/* 128:    */   
/* 129:    */   public boolean containsKey(Object key)
/* 130:    */   {
/* 131:174 */     if (key == null) {
/* 132:175 */       Exceptions.die("Null keys not allowed");
/* 133:    */     }
/* 134:177 */     HardRefKeyValue<K, V> hardRef = createHardRef(key);
/* 135:    */     
/* 136:179 */     boolean containsKey = this.map.containsKey(hardRef);
/* 137:    */     
/* 138:181 */     hardRef.clear();
/* 139:182 */     return containsKey;
/* 140:    */   }
/* 141:    */   
/* 142:    */   public boolean containsValue(Object value)
/* 143:    */   {
/* 144:189 */     return this.map.containsKey(value);
/* 145:    */   }
/* 146:    */   
/* 147:    */   private boolean evictEntires()
/* 148:    */   {
/* 149:194 */     WeakRefKeyValue<K, V> weakKeyValue = (WeakRefKeyValue)this.referenceQueue.poll();
/* 150:    */     
/* 151:196 */     boolean processed = false;
/* 152:198 */     while (weakKeyValue != null)
/* 153:    */     {
/* 154:199 */       V value = weakKeyValue.getValue();
/* 155:200 */       this.map.remove(weakKeyValue, value);
/* 156:201 */       processed = true;
/* 157:    */     }
/* 158:203 */     return processed;
/* 159:    */   }
/* 160:    */   
/* 161:    */   public Set<Map.Entry<K, V>> entrySet()
/* 162:    */   {
/* 163:208 */     if (this.entrySet == null) {
/* 164:208 */       this.entrySet = new EntrySet(null);
/* 165:    */     }
/* 166:209 */     return this.entrySet;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public V putIfAbsent(K key, V value)
/* 170:    */   {
/* 171:214 */     evictEntires();
/* 172:215 */     return this.map.putIfAbsent(createWeakKey(key, value), value);
/* 173:    */   }
/* 174:    */   
/* 175:    */   private KeyValue<K, V> createWeakKey(K key, V value)
/* 176:    */   {
/* 177:219 */     return new WeakRefKeyValue(key, value, this.referenceQueue, null);
/* 178:    */   }
/* 179:    */   
/* 180:    */   public boolean remove(Object key, Object value)
/* 181:    */   {
/* 182:224 */     evictEntires();
/* 183:225 */     return this.map.remove(createWeakKey(key, value), value);
/* 184:    */   }
/* 185:    */   
/* 186:    */   public boolean replace(K key, V oldValue, V newValue)
/* 187:    */   {
/* 188:230 */     evictEntires();
/* 189:231 */     return this.map.replace(createWeakKey(key, oldValue), oldValue, newValue);
/* 190:    */   }
/* 191:    */   
/* 192:    */   public V replace(K key, V value)
/* 193:    */   {
/* 194:236 */     evictEntires();
/* 195:237 */     return this.map.replace(createWeakKey(key, value), value);
/* 196:    */   }
/* 197:    */   
/* 198:    */   private static abstract interface KeyValue<K, V>
/* 199:    */   {
/* 200:    */     public abstract K getKey();
/* 201:    */     
/* 202:    */     public abstract V getValue();
/* 203:    */     
/* 204:    */     public abstract int hashCode();
/* 205:    */   }
/* 206:    */   
/* 207:    */   private static class HardRefKeyValue<K, V>
/* 208:    */     implements ConcurrentWeakHashMap.KeyValue<K, V>
/* 209:    */   {
/* 210:    */     K key;
/* 211:    */     V value;
/* 212:    */     int hashCode;
/* 213:    */     
/* 214:    */     void set(K key, V value, int hashCode)
/* 215:    */     {
/* 216:260 */       this.key = key;
/* 217:261 */       this.value = value;
/* 218:262 */       this.hashCode = hashCode;
/* 219:    */     }
/* 220:    */     
/* 221:    */     public K getKey()
/* 222:    */     {
/* 223:267 */       return this.key;
/* 224:    */     }
/* 225:    */     
/* 226:    */     public V getValue()
/* 227:    */     {
/* 228:272 */       return this.value;
/* 229:    */     }
/* 230:    */     
/* 231:    */     public void clear()
/* 232:    */     {
/* 233:276 */       this.value = null;
/* 234:277 */       this.hashCode = 0;
/* 235:278 */       this.key = null;
/* 236:    */     }
/* 237:    */     
/* 238:    */     public boolean equals(Object o)
/* 239:    */     {
/* 240:283 */       if (this == o) {
/* 241:284 */         return true;
/* 242:    */       }
/* 243:286 */       if (!(o instanceof ConcurrentWeakHashMap.KeyValue)) {
/* 244:287 */         return false;
/* 245:    */       }
/* 246:289 */       Object ours = getKey();
/* 247:    */       
/* 248:291 */       Object theirs = ((ConcurrentWeakHashMap.KeyValue)o).getKey();
/* 249:292 */       if ((theirs == null) || (ours == null)) {
/* 250:293 */         return false;
/* 251:    */       }
/* 252:296 */       if (ours == theirs) {
/* 253:297 */         return true;
/* 254:    */       }
/* 255:299 */       return ours.equals(theirs);
/* 256:    */     }
/* 257:    */     
/* 258:    */     public int hashCode()
/* 259:    */     {
/* 260:303 */       return this.hashCode;
/* 261:    */     }
/* 262:    */   }
/* 263:    */   
/* 264:    */   private static class WeakRefKeyValue<K, V>
/* 265:    */     extends WeakReference<K>
/* 266:    */     implements ConcurrentWeakHashMap.KeyValue
/* 267:    */   {
/* 268:    */     protected final ReferenceQueue<K> referenceQueue;
/* 269:    */     private final int hashCode;
/* 270:    */     private final V value;
/* 271:    */     
/* 272:    */     private WeakRefKeyValue(K referent, V v, ReferenceQueue<K> q)
/* 273:    */     {
/* 274:313 */       super(q);
/* 275:314 */       this.value = v;
/* 276:315 */       this.hashCode = referent.hashCode();
/* 277:316 */       this.referenceQueue = q;
/* 278:    */     }
/* 279:    */     
/* 280:    */     public K getKey()
/* 281:    */     {
/* 282:320 */       return get();
/* 283:    */     }
/* 284:    */     
/* 285:    */     public V getValue()
/* 286:    */     {
/* 287:324 */       return this.value;
/* 288:    */     }
/* 289:    */     
/* 290:    */     public boolean equals(Object o)
/* 291:    */     {
/* 292:329 */       if (this == o) {
/* 293:330 */         return true;
/* 294:    */       }
/* 295:332 */       if (!(o instanceof ConcurrentWeakHashMap.KeyValue)) {
/* 296:333 */         return false;
/* 297:    */       }
/* 298:335 */       Object ours = get();
/* 299:    */       
/* 300:337 */       Object theirs = ((ConcurrentWeakHashMap.KeyValue)o).getKey();
/* 301:338 */       if ((theirs == null) || (ours == null)) {
/* 302:339 */         return false;
/* 303:    */       }
/* 304:342 */       if (ours == theirs) {
/* 305:343 */         return true;
/* 306:    */       }
/* 307:345 */       return ours.equals(theirs);
/* 308:    */     }
/* 309:    */     
/* 310:    */     public int hashCode()
/* 311:    */     {
/* 312:349 */       return this.hashCode;
/* 313:    */     }
/* 314:    */   }
/* 315:    */   
/* 316:    */   private class EntrySet
/* 317:    */     extends AbstractSet<Map.Entry<K, V>>
/* 318:    */   {
/* 319:356 */     final Iterator<Map.Entry<ConcurrentWeakHashMap.KeyValue<K, V>, V>> iterator = ConcurrentWeakHashMap.this.map.entrySet().iterator();
/* 320:    */     
/* 321:    */     private EntrySet() {}
/* 322:    */     
/* 323:    */     public Iterator<Map.Entry<K, V>> iterator()
/* 324:    */     {
/* 325:360 */       new Iterator()
/* 326:    */       {
/* 327:361 */         Pair<K, V> next = null;
/* 328:    */         
/* 329:    */         public boolean hasNext()
/* 330:    */         {
/* 331:365 */           this.next = null;
/* 332:367 */           while (ConcurrentWeakHashMap.EntrySet.this.iterator.hasNext())
/* 333:    */           {
/* 334:368 */             Map.Entry<ConcurrentWeakHashMap.KeyValue<K, V>, V> entry = (Map.Entry)ConcurrentWeakHashMap.EntrySet.this.iterator.next();
/* 335:369 */             ConcurrentWeakHashMap.KeyValue<K, V> kv = (ConcurrentWeakHashMap.KeyValue)entry.getKey();
/* 336:370 */             K key = kv != null ? kv.getKey() : null;
/* 337:371 */             if (key != null) {
/* 338:374 */               this.next = new Pair(key, kv.getValue());
/* 339:    */             }
/* 340:    */           }
/* 341:377 */           return this.next != null;
/* 342:    */         }
/* 343:    */         
/* 344:    */         public Map.Entry<K, V> next()
/* 345:    */         {
/* 346:382 */           return this.next;
/* 347:    */         }
/* 348:    */         
/* 349:    */         public void remove()
/* 350:    */         {
/* 351:387 */           ConcurrentWeakHashMap.EntrySet.this.iterator.remove();
/* 352:    */         }
/* 353:    */       };
/* 354:    */     }
/* 355:    */     
/* 356:    */     public boolean isEmpty()
/* 357:    */     {
/* 358:394 */       return !iterator().hasNext();
/* 359:    */     }
/* 360:    */     
/* 361:    */     public int size()
/* 362:    */     {
/* 363:399 */       int count = 0;
/* 364:400 */       for (Iterator i = iterator(); i.hasNext(); i.next()) {
/* 365:401 */         count++;
/* 366:    */       }
/* 367:403 */       return count;
/* 368:    */     }
/* 369:    */     
/* 370:    */     public boolean remove(Object o)
/* 371:    */     {
/* 372:408 */       ConcurrentWeakHashMap.this.evictEntires();
/* 373:    */       
/* 374:    */ 
/* 375:411 */       ConcurrentWeakHashMap.HardRefKeyValue<K, V> key = ConcurrentWeakHashMap.this.createHardRef(o);
/* 376:    */       
/* 377:    */ 
/* 378:414 */       boolean removed = ConcurrentWeakHashMap.this.map.remove(key) != null;
/* 379:    */       
/* 380:416 */       key.clear();
/* 381:417 */       return removed;
/* 382:    */     }
/* 383:    */   }
/* 384:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.collections.ConcurrentWeakHashMap
 * JD-Core Version:    0.7.0.1
 */