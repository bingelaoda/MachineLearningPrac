/*   1:    */ package org.boon;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collection;
/*   5:    */ import java.util.Collections;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import java.util.HashSet;
/*   8:    */ import java.util.Iterator;
/*   9:    */ import java.util.LinkedHashSet;
/*  10:    */ import java.util.List;
/*  11:    */ import java.util.Map;
/*  12:    */ import java.util.NavigableSet;
/*  13:    */ import java.util.Set;
/*  14:    */ import java.util.SortedSet;
/*  15:    */ import java.util.TreeSet;
/*  16:    */ import java.util.concurrent.ConcurrentSkipListSet;
/*  17:    */ import java.util.concurrent.CopyOnWriteArraySet;
/*  18:    */ import org.boon.core.reflection.BeanUtils;
/*  19:    */ import org.boon.core.reflection.MapObjectConversion;
/*  20:    */ import org.boon.primitive.CharBuf;
/*  21:    */ 
/*  22:    */ public class Sets
/*  23:    */ {
/*  24:    */   public static <V> Set<V> set(Collection<V> collection)
/*  25:    */   {
/*  26: 44 */     if ((collection instanceof Set)) {
/*  27: 45 */       return (Set)collection;
/*  28:    */     }
/*  29: 47 */     if (collection == null) {
/*  30: 48 */       return Collections.EMPTY_SET;
/*  31:    */     }
/*  32: 50 */     return new LinkedHashSet(collection);
/*  33:    */   }
/*  34:    */   
/*  35:    */   public static <V> Enumeration<V> enumeration(Set<V> set)
/*  36:    */   {
/*  37: 55 */     Iterator<V> iter = set.iterator();
/*  38: 56 */     new Enumeration()
/*  39:    */     {
/*  40:    */       public boolean hasMoreElements()
/*  41:    */       {
/*  42: 59 */         return this.val$iter.hasNext();
/*  43:    */       }
/*  44:    */       
/*  45:    */       public V nextElement()
/*  46:    */       {
/*  47: 64 */         return this.val$iter.next();
/*  48:    */       }
/*  49:    */     };
/*  50:    */   }
/*  51:    */   
/*  52:    */   public static <V> Set<V> set(Class<V> clazz)
/*  53:    */   {
/*  54: 72 */     return new LinkedHashSet();
/*  55:    */   }
/*  56:    */   
/*  57:    */   public static <V> Set<V> set(Iterable<V> iterable)
/*  58:    */   {
/*  59: 76 */     Set<V> set = new LinkedHashSet();
/*  60: 77 */     for (V o : iterable) {
/*  61: 78 */       set.add(o);
/*  62:    */     }
/*  63: 80 */     return set;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public static <V> Set<V> set(Enumeration<V> enumeration)
/*  67:    */   {
/*  68: 84 */     Set<V> set = new LinkedHashSet();
/*  69: 85 */     while (enumeration.hasMoreElements()) {
/*  70: 86 */       set.add(enumeration.nextElement());
/*  71:    */     }
/*  72: 88 */     return set;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public static <V> Set<V> set(Iterator<V> iterator)
/*  76:    */   {
/*  77: 93 */     Set<V> set = new LinkedHashSet();
/*  78: 94 */     while (iterator.hasNext()) {
/*  79: 95 */       set.add(iterator.next());
/*  80:    */     }
/*  81: 97 */     return set;
/*  82:    */   }
/*  83:    */   
/*  84:    */   @SafeVarargs
/*  85:    */   public static <V> Set<V> set(V... array)
/*  86:    */   {
/*  87:105 */     Set<V> set = new LinkedHashSet();
/*  88:107 */     for (V v : array) {
/*  89:108 */       set.add(v);
/*  90:    */     }
/*  91:110 */     return set;
/*  92:    */   }
/*  93:    */   
/*  94:    */   @SafeVarargs
/*  95:    */   public static <V> Set<V> set(int size, V... array)
/*  96:    */   {
/*  97:116 */     int index = 0;
/*  98:117 */     Set<V> set = new LinkedHashSet();
/*  99:119 */     for (V v : array)
/* 100:    */     {
/* 101:120 */       set.add(v);
/* 102:121 */       index++;
/* 103:122 */       if (index == size) {
/* 104:    */         break;
/* 105:    */       }
/* 106:    */     }
/* 107:126 */     return set;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public static <V> NavigableSet<V> sortedSet(Iterator<V> iterator)
/* 111:    */   {
/* 112:131 */     NavigableSet<V> set = new TreeSet();
/* 113:132 */     while (iterator.hasNext()) {
/* 114:133 */       set.add(iterator.next());
/* 115:    */     }
/* 116:135 */     return set;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public static <V> NavigableSet<V> sortedSet(Class<V> clazz)
/* 120:    */   {
/* 121:139 */     return new TreeSet();
/* 122:    */   }
/* 123:    */   
/* 124:    */   public static <V> NavigableSet<V> sortedSet(Iterable<V> iterable)
/* 125:    */   {
/* 126:143 */     NavigableSet<V> set = new TreeSet();
/* 127:144 */     for (V o : iterable) {
/* 128:145 */       set.add(o);
/* 129:    */     }
/* 130:147 */     return set;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public static <V> NavigableSet<V> sortedSet(Enumeration<V> enumeration)
/* 134:    */   {
/* 135:151 */     NavigableSet<V> set = new TreeSet();
/* 136:152 */     while (enumeration.hasMoreElements()) {
/* 137:153 */       set.add(enumeration.nextElement());
/* 138:    */     }
/* 139:155 */     return set;
/* 140:    */   }
/* 141:    */   
/* 142:    */   @SafeVarargs
/* 143:    */   public static <V> NavigableSet<V> sortedSet(V... array)
/* 144:    */   {
/* 145:160 */     NavigableSet<V> set = new TreeSet();
/* 146:162 */     for (V v : array) {
/* 147:163 */       set.add(v);
/* 148:    */     }
/* 149:165 */     return set;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public static <V> NavigableSet<V> sortedSet(Collection<V> collection)
/* 153:    */   {
/* 154:169 */     return new TreeSet(collection);
/* 155:    */   }
/* 156:    */   
/* 157:    */   public static <V> NavigableSet<V> safeSortedSet(Iterator<V> iterator)
/* 158:    */   {
/* 159:174 */     NavigableSet<V> set = new ConcurrentSkipListSet();
/* 160:175 */     while (iterator.hasNext()) {
/* 161:176 */       set.add(iterator.next());
/* 162:    */     }
/* 163:178 */     return set;
/* 164:    */   }
/* 165:    */   
/* 166:    */   public static <V> NavigableSet<V> safeSortedSet(Class<V> clazz)
/* 167:    */   {
/* 168:182 */     return new ConcurrentSkipListSet();
/* 169:    */   }
/* 170:    */   
/* 171:    */   public static <V> NavigableSet<V> safeSortedSet(Iterable<V> iterable)
/* 172:    */   {
/* 173:186 */     NavigableSet<V> set = new ConcurrentSkipListSet();
/* 174:187 */     for (V o : iterable) {
/* 175:188 */       set.add(o);
/* 176:    */     }
/* 177:190 */     return set;
/* 178:    */   }
/* 179:    */   
/* 180:    */   public static <V> NavigableSet<V> safeSortedSet(Enumeration<V> enumeration)
/* 181:    */   {
/* 182:194 */     NavigableSet<V> set = new ConcurrentSkipListSet();
/* 183:195 */     while (enumeration.hasMoreElements()) {
/* 184:196 */       set.add(enumeration.nextElement());
/* 185:    */     }
/* 186:198 */     return set;
/* 187:    */   }
/* 188:    */   
/* 189:    */   @SafeVarargs
/* 190:    */   public static <V> NavigableSet<V> safeSortedSet(V... array)
/* 191:    */   {
/* 192:204 */     NavigableSet<V> set = new ConcurrentSkipListSet();
/* 193:206 */     for (V v : array) {
/* 194:207 */       set.add(v);
/* 195:    */     }
/* 196:209 */     return set;
/* 197:    */   }
/* 198:    */   
/* 199:    */   public static <V> NavigableSet<V> safeSortedSet(Collection<V> collection)
/* 200:    */   {
/* 201:215 */     return new ConcurrentSkipListSet(collection);
/* 202:    */   }
/* 203:    */   
/* 204:    */   public static <V> Set<V> safeSet(Class<V> clazz)
/* 205:    */   {
/* 206:219 */     return new CopyOnWriteArraySet();
/* 207:    */   }
/* 208:    */   
/* 209:    */   public static <V> Set<V> safeSet(Iterable<V> iterable)
/* 210:    */   {
/* 211:223 */     Set<V> set = new CopyOnWriteArraySet();
/* 212:224 */     for (V o : iterable) {
/* 213:225 */       set.add(o);
/* 214:    */     }
/* 215:227 */     return set;
/* 216:    */   }
/* 217:    */   
/* 218:    */   public static <V> Set<V> safeSet(Enumeration<V> enumeration)
/* 219:    */   {
/* 220:231 */     Set<V> set = new CopyOnWriteArraySet();
/* 221:232 */     while (enumeration.hasMoreElements()) {
/* 222:233 */       set.add(enumeration.nextElement());
/* 223:    */     }
/* 224:235 */     return set;
/* 225:    */   }
/* 226:    */   
/* 227:    */   public static <V> Set<V> safeSet(Iterator<V> iterator)
/* 228:    */   {
/* 229:240 */     Set<V> set = new CopyOnWriteArraySet();
/* 230:241 */     while (iterator.hasNext()) {
/* 231:242 */       set.add(iterator.next());
/* 232:    */     }
/* 233:244 */     return set;
/* 234:    */   }
/* 235:    */   
/* 236:    */   @SafeVarargs
/* 237:    */   public static <V> Set<V> safeSet(V... array)
/* 238:    */   {
/* 239:251 */     List<V> list = Lists.list(array);
/* 240:252 */     Set<V> set = new CopyOnWriteArraySet(list);
/* 241:    */     
/* 242:254 */     return set;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public static <V> Set<V> safeSet(Collection<V> collection)
/* 246:    */   {
/* 247:258 */     return new CopyOnWriteArraySet(collection);
/* 248:    */   }
/* 249:    */   
/* 250:    */   @Universal
/* 251:    */   public static int len(Set<?> set)
/* 252:    */   {
/* 253:264 */     return set.size();
/* 254:    */   }
/* 255:    */   
/* 256:    */   @Universal
/* 257:    */   public static <V> boolean in(V value, Set<?> set)
/* 258:    */   {
/* 259:269 */     return set.contains(value);
/* 260:    */   }
/* 261:    */   
/* 262:    */   @Universal
/* 263:    */   public static <V> void add(Set<V> set, V value)
/* 264:    */   {
/* 265:274 */     set.add(value);
/* 266:    */   }
/* 267:    */   
/* 268:    */   @Universal
/* 269:    */   public static <T> T idx(NavigableSet<T> set, T index)
/* 270:    */   {
/* 271:280 */     return set.higher(index);
/* 272:    */   }
/* 273:    */   
/* 274:    */   @Universal
/* 275:    */   public static <T> T idx(Set<T> set, T index)
/* 276:    */   {
/* 277:286 */     if ((set instanceof NavigableSet)) {
/* 278:287 */       return idx((NavigableSet)set, index);
/* 279:    */     }
/* 280:289 */     throw new IllegalArgumentException("Set must be a NavigableSet for idx operation to work");
/* 281:    */   }
/* 282:    */   
/* 283:    */   public static <T> T after(NavigableSet<T> set, T index)
/* 284:    */   {
/* 285:295 */     return set.higher(index);
/* 286:    */   }
/* 287:    */   
/* 288:    */   public static <T> T before(NavigableSet<T> set, T index)
/* 289:    */   {
/* 290:300 */     return set.lower(index);
/* 291:    */   }
/* 292:    */   
/* 293:    */   @Universal
/* 294:    */   public static <V> SortedSet<V> slc(NavigableSet<V> set, V startIndex, V endIndex)
/* 295:    */   {
/* 296:305 */     return set.subSet(startIndex, endIndex);
/* 297:    */   }
/* 298:    */   
/* 299:    */   @Universal
/* 300:    */   public static <V> SortedSet<V> slcEnd(NavigableSet<V> set, V fromIndex)
/* 301:    */   {
/* 302:311 */     return set.tailSet(fromIndex);
/* 303:    */   }
/* 304:    */   
/* 305:    */   @Universal
/* 306:    */   public static <V> SortedSet<V> slc(NavigableSet<V> set, V toIndex)
/* 307:    */   {
/* 308:317 */     return set.headSet(toIndex);
/* 309:    */   }
/* 310:    */   
/* 311:    */   @Universal
/* 312:    */   public static <V> Set<V> copy(HashSet<V> collection)
/* 313:    */   {
/* 314:322 */     return new LinkedHashSet(collection);
/* 315:    */   }
/* 316:    */   
/* 317:    */   @Universal
/* 318:    */   public static <V> NavigableSet<V> copy(TreeSet<V> collection)
/* 319:    */   {
/* 320:327 */     return new TreeSet(collection);
/* 321:    */   }
/* 322:    */   
/* 323:    */   @Universal
/* 324:    */   public static <V> Set<V> copy(CopyOnWriteArraySet<V> collection)
/* 325:    */   {
/* 326:332 */     return new CopyOnWriteArraySet(collection);
/* 327:    */   }
/* 328:    */   
/* 329:    */   @Universal
/* 330:    */   public static <V> NavigableSet<V> copy(ConcurrentSkipListSet<V> collection)
/* 331:    */   {
/* 332:337 */     return new ConcurrentSkipListSet(collection);
/* 333:    */   }
/* 334:    */   
/* 335:    */   @Universal
/* 336:    */   public static <V> NavigableSet<V> copy(NavigableSet<V> collection)
/* 337:    */   {
/* 338:343 */     if ((collection instanceof ConcurrentSkipListSet)) {
/* 339:344 */       return copy((ConcurrentSkipListSet)collection);
/* 340:    */     }
/* 341:346 */     return copy((TreeSet)collection);
/* 342:    */   }
/* 343:    */   
/* 344:    */   @Universal
/* 345:    */   public static <V> Set<V> copy(Set<V> collection)
/* 346:    */   {
/* 347:353 */     if ((collection instanceof NavigableSet)) {
/* 348:355 */       return copy((NavigableSet)collection);
/* 349:    */     }
/* 350:358 */     if ((collection instanceof CopyOnWriteArraySet)) {
/* 351:360 */       return copy((CopyOnWriteArraySet)collection);
/* 352:    */     }
/* 353:364 */     return copy((LinkedHashSet)collection);
/* 354:    */   }
/* 355:    */   
/* 356:    */   public static <V> Set<V> deepCopy(Collection<V> collection)
/* 357:    */   {
/* 358:372 */     Set<V> newSet = new LinkedHashSet(collection.size());
/* 359:374 */     for (V v : collection) {
/* 360:375 */       newSet.add(BeanUtils.copy(v));
/* 361:    */     }
/* 362:377 */     return newSet;
/* 363:    */   }
/* 364:    */   
/* 365:    */   public static <V> Set<V> deepCopyToSet(Collection<V> src, Set<V> dst)
/* 366:    */   {
/* 367:382 */     for (V v : src) {
/* 368:383 */       dst.add(BeanUtils.copy(v));
/* 369:    */     }
/* 370:385 */     return dst;
/* 371:    */   }
/* 372:    */   
/* 373:    */   public static <V, T> List<T> deepCopy(Collection<V> src, Class<T> dest)
/* 374:    */   {
/* 375:390 */     List<T> list = new ArrayList(src.size());
/* 376:392 */     for (V v : src) {
/* 377:393 */       list.add(BeanUtils.createFromSrc(v, dest));
/* 378:    */     }
/* 379:395 */     return list;
/* 380:    */   }
/* 381:    */   
/* 382:    */   @Universal
/* 383:    */   public static <V> Set<V> deepCopy(Set<V> set)
/* 384:    */   {
/* 385:400 */     if ((set instanceof LinkedHashSet)) {
/* 386:401 */       return deepCopyToSet(set, new LinkedHashSet());
/* 387:    */     }
/* 388:402 */     if ((set instanceof CopyOnWriteArraySet)) {
/* 389:403 */       return deepCopyToSet(set, new CopyOnWriteArraySet());
/* 390:    */     }
/* 391:404 */     if ((set instanceof HashSet)) {
/* 392:405 */       return deepCopyToSet(set, new HashSet());
/* 393:    */     }
/* 394:407 */     return deepCopy(set);
/* 395:    */   }
/* 396:    */   
/* 397:    */   public static List<Map<String, Object>> toListOfMaps(Set<?> set)
/* 398:    */   {
/* 399:414 */     return MapObjectConversion.toListOfMaps(set);
/* 400:    */   }
/* 401:    */   
/* 402:    */   public static <T> Set<T> setFromProperty(Class<T> propertyType, String propertyPath, Collection<?> list)
/* 403:    */   {
/* 404:420 */     Set<T> newSet = new LinkedHashSet(list.size());
/* 405:422 */     for (Object item : list)
/* 406:    */     {
/* 407:423 */       T newItem = BeanUtils.idx(item, propertyPath);
/* 408:424 */       newSet.add(newItem);
/* 409:    */     }
/* 410:427 */     return newSet;
/* 411:    */   }
/* 412:    */   
/* 413:    */   public static <T> Set<T> setFromProperty(Class<T> propertyType, String propertyPath, Iterable<?> list)
/* 414:    */   {
/* 415:433 */     Set<T> newSet = new LinkedHashSet();
/* 416:435 */     for (Object item : list)
/* 417:    */     {
/* 418:436 */       T newItem = BeanUtils.idx(item, propertyPath);
/* 419:437 */       newSet.add(newItem);
/* 420:    */     }
/* 421:440 */     return newSet;
/* 422:    */   }
/* 423:    */   
/* 424:    */   public static String toPrettyJson(Set set)
/* 425:    */   {
/* 426:446 */     CharBuf buf = CharBuf.createCharBuf();
/* 427:447 */     return buf.prettyPrintCollection(set, false, 0).toString();
/* 428:    */   }
/* 429:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.Sets
 * JD-Core Version:    0.7.0.1
 */