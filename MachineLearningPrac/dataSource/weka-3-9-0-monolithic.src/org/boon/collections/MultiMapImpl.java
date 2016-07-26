/*   1:    */ package org.boon.collections;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collection;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Iterator;
/*   7:    */ import java.util.List;
/*   8:    */ import java.util.Map;
/*   9:    */ import java.util.Map.Entry;
/*  10:    */ import java.util.Set;
/*  11:    */ import java.util.concurrent.ConcurrentHashMap;
/*  12:    */ import org.boon.Exceptions;
/*  13:    */ import org.boon.core.Conversions;
/*  14:    */ 
/*  15:    */ public class MultiMapImpl<K, V>
/*  16:    */   implements MultiMap<K, V>
/*  17:    */ {
/*  18: 40 */   private int initialSize = 10;
/*  19: 41 */   private Map<K, Collection<V>> map = new ConcurrentHashMap();
/*  20: 42 */   private Class<? extends Collection> collectionClass = ArrayList.class;
/*  21:    */   
/*  22:    */   public MultiMapImpl(Class<? extends Collection> collectionClass, int initialSize)
/*  23:    */   {
/*  24: 46 */     this.collectionClass = collectionClass;
/*  25: 47 */     this.initialSize = initialSize;
/*  26:    */   }
/*  27:    */   
/*  28:    */   public MultiMapImpl(Class<? extends Collection> collectionClass)
/*  29:    */   {
/*  30: 51 */     this.collectionClass = collectionClass;
/*  31:    */   }
/*  32:    */   
/*  33:    */   public MultiMapImpl() {}
/*  34:    */   
/*  35:    */   public Iterator<Map.Entry<K, Collection<V>>> iterator()
/*  36:    */   {
/*  37: 60 */     return this.map.entrySet().iterator();
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void add(K key, V v)
/*  41:    */   {
/*  42: 66 */     Collection<V> collection = (Collection)this.map.get(key);
/*  43: 67 */     if (collection == null) {
/*  44: 68 */       collection = createCollection(key);
/*  45:    */     }
/*  46: 70 */     collection.add(v);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public V put(K key, V value)
/*  50:    */   {
/*  51: 76 */     Collection<V> collection = (Collection)this.map.get(key);
/*  52: 77 */     if (collection == null) {
/*  53: 78 */       collection = createCollection(key);
/*  54:    */     }
/*  55: 80 */     collection.add(value);
/*  56: 81 */     return null;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public V remove(Object key)
/*  60:    */   {
/*  61: 86 */     this.map.remove(key);
/*  62: 87 */     return null;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void putAll(Map<? extends K, ? extends V> m)
/*  66:    */   {
/*  67: 93 */     Set<? extends Map.Entry<? extends K, ? extends V>> entries = m.entrySet();
/*  68: 94 */     for (Map.Entry<? extends K, ? extends V> entry : entries) {
/*  69: 95 */       add(entry.getKey(), entry.getValue());
/*  70:    */     }
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void clear()
/*  74:    */   {
/*  75:101 */     this.map.clear();
/*  76:    */   }
/*  77:    */   
/*  78:    */   public Set<K> keySet()
/*  79:    */   {
/*  80:106 */     return this.map.keySet();
/*  81:    */   }
/*  82:    */   
/*  83:    */   public V get(Object key)
/*  84:    */   {
/*  85:112 */     return getFirst(key);
/*  86:    */   }
/*  87:    */   
/*  88:    */   public V getFirst(K key)
/*  89:    */   {
/*  90:119 */     Collection<V> collection = (Collection)this.map.get(key);
/*  91:120 */     if ((collection == null) || (collection.size() == 0)) {
/*  92:121 */       return null;
/*  93:    */     }
/*  94:123 */     return collection.iterator().next();
/*  95:    */   }
/*  96:    */   
/*  97:    */   public Iterable<V> getAll(K key)
/*  98:    */   {
/*  99:130 */     Collection<V> collection = (Collection)this.map.get(key);
/* 100:131 */     if (collection == null) {
/* 101:132 */       return Collections.emptyList();
/* 102:    */     }
/* 103:134 */     return collection;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public boolean removeValueFrom(K key, V v)
/* 107:    */   {
/* 108:141 */     Collection<V> collection = (Collection)this.map.get(key);
/* 109:142 */     if (collection == null) {
/* 110:143 */       return false;
/* 111:    */     }
/* 112:145 */     return collection.remove(v);
/* 113:    */   }
/* 114:    */   
/* 115:    */   public boolean removeMulti(K key)
/* 116:    */   {
/* 117:153 */     return this.map.remove(key) != null;
/* 118:    */   }
/* 119:    */   
/* 120:    */   private Collection<V> createCollection(K key)
/* 121:    */   {
/* 122:157 */     Collection<V> collection = Conversions.createCollection(this.collectionClass, this.initialSize);
/* 123:158 */     this.map.put(key, collection);
/* 124:159 */     return collection;
/* 125:    */   }
/* 126:    */   
/* 127:    */   public Iterable<K> keySetMulti()
/* 128:    */   {
/* 129:164 */     return this.map.keySet();
/* 130:    */   }
/* 131:    */   
/* 132:    */   public Iterable<V> valueMulti()
/* 133:    */   {
/* 134:170 */     List list = new ArrayList();
/* 135:171 */     Collection<Collection<V>> values = this.map.values();
/* 136:173 */     for (Collection c : values) {
/* 137:174 */       for (Object o : c) {
/* 138:175 */         list.add(o);
/* 139:    */       }
/* 140:    */     }
/* 141:178 */     return list;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public Collection<V> values()
/* 145:    */   {
/* 146:185 */     List list = new ArrayList();
/* 147:186 */     Collection<Collection<V>> values = this.map.values();
/* 148:188 */     for (Collection c : values) {
/* 149:189 */       for (Object o : c) {
/* 150:190 */         list.add(o);
/* 151:    */       }
/* 152:    */     }
/* 153:193 */     return list;
/* 154:    */   }
/* 155:    */   
/* 156:    */   public Set<Map.Entry<K, V>> entrySet()
/* 157:    */   {
/* 158:202 */     final Set<Map.Entry<K, Collection<V>>> entries = this.map.entrySet();
/* 159:    */     
/* 160:204 */     new Set()
/* 161:    */     {
/* 162:    */       public int size()
/* 163:    */       {
/* 164:208 */         return entries.size();
/* 165:    */       }
/* 166:    */       
/* 167:    */       public boolean isEmpty()
/* 168:    */       {
/* 169:213 */         return entries.isEmpty();
/* 170:    */       }
/* 171:    */       
/* 172:    */       public boolean contains(Object o)
/* 173:    */       {
/* 174:218 */         return entries.contains(o);
/* 175:    */       }
/* 176:    */       
/* 177:    */       public Iterator<Map.Entry<K, V>> iterator()
/* 178:    */       {
/* 179:223 */         final Iterator<Map.Entry<K, Collection<V>>> iterator = entries.iterator();
/* 180:224 */         new Iterator()
/* 181:    */         {
/* 182:    */           public boolean hasNext()
/* 183:    */           {
/* 184:228 */             return iterator.hasNext();
/* 185:    */           }
/* 186:    */           
/* 187:    */           public Map.Entry<K, V> next()
/* 188:    */           {
/* 189:233 */             final Map.Entry<K, Collection<V>> next = (Map.Entry)iterator.next();
/* 190:234 */             Collection<V> value = (Collection)next.getValue();
/* 191:235 */             V theValue = null;
/* 192:236 */             if ((value instanceof List)) {
/* 193:237 */               theValue = ((List)value).get(0);
/* 194:    */             }
/* 195:240 */             final V item = theValue;
/* 196:    */             
/* 197:242 */             new Map.Entry()
/* 198:    */             {
/* 199:    */               public K getKey()
/* 200:    */               {
/* 201:245 */                 return next.getKey();
/* 202:    */               }
/* 203:    */               
/* 204:    */               public V getValue()
/* 205:    */               {
/* 206:250 */                 return item;
/* 207:    */               }
/* 208:    */               
/* 209:    */               public V setValue(V value)
/* 210:    */               {
/* 211:255 */                 return null;
/* 212:    */               }
/* 213:    */             };
/* 214:    */           }
/* 215:    */           
/* 216:    */           public void remove() {}
/* 217:    */         };
/* 218:    */       }
/* 219:    */       
/* 220:    */       public Object[] toArray()
/* 221:    */       {
/* 222:271 */         Exceptions.die("Not supported");
/* 223:272 */         return null;
/* 224:    */       }
/* 225:    */       
/* 226:    */       public <T> T[] toArray(T[] a)
/* 227:    */       {
/* 228:277 */         Exceptions.die("Not supported");
/* 229:278 */         return null;
/* 230:    */       }
/* 231:    */       
/* 232:    */       public boolean add(Map.Entry<K, V> kvEntry)
/* 233:    */       {
/* 234:283 */         Exceptions.die("Not supported");
/* 235:    */         
/* 236:285 */         return false;
/* 237:    */       }
/* 238:    */       
/* 239:    */       public boolean remove(Object o)
/* 240:    */       {
/* 241:290 */         return false;
/* 242:    */       }
/* 243:    */       
/* 244:    */       public boolean containsAll(Collection<?> c)
/* 245:    */       {
/* 246:295 */         Exceptions.die("Not supported");
/* 247:    */         
/* 248:297 */         return false;
/* 249:    */       }
/* 250:    */       
/* 251:    */       public boolean addAll(Collection<? extends Map.Entry<K, V>> c)
/* 252:    */       {
/* 253:302 */         Exceptions.die("Not supported");
/* 254:    */         
/* 255:304 */         return false;
/* 256:    */       }
/* 257:    */       
/* 258:    */       public boolean retainAll(Collection<?> c)
/* 259:    */       {
/* 260:309 */         Exceptions.die("Not supported");
/* 261:    */         
/* 262:311 */         return false;
/* 263:    */       }
/* 264:    */       
/* 265:    */       public boolean removeAll(Collection<?> c)
/* 266:    */       {
/* 267:316 */         Exceptions.die("Not supported");
/* 268:    */         
/* 269:318 */         return false;
/* 270:    */       }
/* 271:    */       
/* 272:    */       public void clear()
/* 273:    */       {
/* 274:323 */         Exceptions.die("Not supported");
/* 275:    */       }
/* 276:    */     };
/* 277:    */   }
/* 278:    */   
/* 279:    */   public int size()
/* 280:    */   {
/* 281:330 */     return this.map.size();
/* 282:    */   }
/* 283:    */   
/* 284:    */   public boolean isEmpty()
/* 285:    */   {
/* 286:335 */     return this.map.size() == 0;
/* 287:    */   }
/* 288:    */   
/* 289:    */   public boolean containsKey(Object key)
/* 290:    */   {
/* 291:340 */     if (!this.map.containsKey(key)) {
/* 292:341 */       return false;
/* 293:    */     }
/* 294:343 */     Collection<V> collection = (Collection)this.map.get(key);
/* 295:344 */     if ((collection == null) || (collection.size() == 0)) {
/* 296:345 */       return false;
/* 297:    */     }
/* 298:347 */     return true;
/* 299:    */   }
/* 300:    */   
/* 301:    */   public boolean containsValue(Object value)
/* 302:    */   {
/* 303:355 */     Exceptions.die("Not supported by MultiMap");
/* 304:356 */     return false;
/* 305:    */   }
/* 306:    */   
/* 307:    */   public void putAll(MultiMap<K, V> params)
/* 308:    */   {
/* 309:363 */     this.map.putAll(params.baseMap());
/* 310:    */   }
/* 311:    */   
/* 312:    */   public Map<? extends K, ? extends Collection<V>> baseMap()
/* 313:    */   {
/* 314:369 */     return this.map;
/* 315:    */   }
/* 316:    */   
/* 317:    */   public V getSingleObject(K name)
/* 318:    */   {
/* 319:374 */     Collection<V> vs = (Collection)this.map.get(name);
/* 320:375 */     if ((vs == null) || (vs.size() == 0)) {
/* 321:376 */       return null;
/* 322:    */     }
/* 323:378 */     if (vs.size() == 1)
/* 324:    */     {
/* 325:379 */       vs.iterator().hasNext();
/* 326:380 */       return vs.iterator().next();
/* 327:    */     }
/* 328:382 */     return null;
/* 329:    */   }
/* 330:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.collections.MultiMapImpl
 * JD-Core Version:    0.7.0.1
 */