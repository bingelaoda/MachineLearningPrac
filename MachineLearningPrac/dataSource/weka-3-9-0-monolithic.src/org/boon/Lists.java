/*   1:    */ package org.boon;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Array;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Collection;
/*   6:    */ import java.util.Collections;
/*   7:    */ import java.util.Enumeration;
/*   8:    */ import java.util.Iterator;
/*   9:    */ import java.util.LinkedList;
/*  10:    */ import java.util.List;
/*  11:    */ import java.util.Map;
/*  12:    */ import java.util.concurrent.CopyOnWriteArrayList;
/*  13:    */ import org.boon.collections.DoubleList;
/*  14:    */ import org.boon.collections.FloatList;
/*  15:    */ import org.boon.collections.IntList;
/*  16:    */ import org.boon.collections.LongList;
/*  17:    */ import org.boon.core.Conversions;
/*  18:    */ import org.boon.core.Function;
/*  19:    */ import org.boon.core.Predicate;
/*  20:    */ import org.boon.core.Reducer;
/*  21:    */ import org.boon.core.reflection.BeanUtils;
/*  22:    */ import org.boon.core.reflection.ClassMeta;
/*  23:    */ import org.boon.core.reflection.ConstructorAccess;
/*  24:    */ import org.boon.core.reflection.Invoker;
/*  25:    */ import org.boon.core.reflection.MapObjectConversion;
/*  26:    */ import org.boon.core.reflection.MethodAccess;
/*  27:    */ import org.boon.core.reflection.Reflection;
/*  28:    */ import org.boon.primitive.CharBuf;
/*  29:    */ 
/*  30:    */ public class Lists
/*  31:    */ {
/*  32:    */   public static <T> List<T> lazyAdd(List<T> list, T... items)
/*  33:    */   {
/*  34: 50 */     list = list == null ? new ArrayList() : list;
/*  35: 52 */     for (T item : items) {
/*  36: 53 */       list.add(item);
/*  37:    */     }
/*  38: 55 */     return list;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public static <T> List<T> lazyAdd(ArrayList<T> list, T... items)
/*  42:    */   {
/*  43: 60 */     list = list == null ? new ArrayList() : list;
/*  44: 62 */     for (T item : items) {
/*  45: 63 */       list.add(item);
/*  46:    */     }
/*  47: 65 */     return list;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public static <T> List<T> safeLazyAdd(CopyOnWriteArrayList<T> list, T... items)
/*  51:    */   {
/*  52: 68 */     list = list == null ? new CopyOnWriteArrayList() : list;
/*  53: 69 */     for (T item : items) {
/*  54: 70 */       list.add(item);
/*  55:    */     }
/*  56: 72 */     return list;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public static <T> List<T> lazyAdd(CopyOnWriteArrayList<T> list, T... items)
/*  60:    */   {
/*  61: 76 */     list = list == null ? new CopyOnWriteArrayList() : list;
/*  62: 77 */     for (T item : items) {
/*  63: 78 */       list.add(item);
/*  64:    */     }
/*  65: 80 */     return list;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public static <T> List<T> lazyCreate(List<T> list)
/*  69:    */   {
/*  70: 87 */     return list == null ? new ArrayList() : list;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public static <T> List<T> lazyCreate(ArrayList<T> list)
/*  74:    */   {
/*  75: 93 */     return list == null ? new ArrayList() : list;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public static <T> List<T> lazyCreate(CopyOnWriteArrayList<T> list)
/*  79:    */   {
/*  80: 98 */     return list == null ? new CopyOnWriteArrayList() : list;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public static <T> List<T> safeLazyCreate(CopyOnWriteArrayList<T> list)
/*  84:    */   {
/*  85:103 */     return list == null ? new CopyOnWriteArrayList() : list;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public static <T> T fromList(List<Object> list, Class<T> clazz)
/*  89:    */   {
/*  90:107 */     return MapObjectConversion.fromList(list, clazz);
/*  91:    */   }
/*  92:    */   
/*  93:    */   public static <V> List<V> list(Class<V> clazz)
/*  94:    */   {
/*  95:111 */     return new ArrayList();
/*  96:    */   }
/*  97:    */   
/*  98:    */   public static <V> List<V> copy(Collection<V> collection)
/*  99:    */   {
/* 100:116 */     return new ArrayList(collection);
/* 101:    */   }
/* 102:    */   
/* 103:    */   public static <V> List<V> deepCopy(Collection<V> collection)
/* 104:    */   {
/* 105:121 */     List<V> list = new ArrayList(collection.size());
/* 106:123 */     for (V v : collection) {
/* 107:124 */       list.add(BeanUtils.copy(v));
/* 108:    */     }
/* 109:126 */     return list;
/* 110:    */   }
/* 111:    */   
/* 112:    */   public static <V> List<V> deepCopyToList(Collection<V> src, List<V> dst)
/* 113:    */   {
/* 114:131 */     for (V v : src) {
/* 115:132 */       dst.add(BeanUtils.copy(v));
/* 116:    */     }
/* 117:134 */     return dst;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public static <V, T> List<T> deepCopy(Collection<V> src, Class<T> dest)
/* 121:    */   {
/* 122:139 */     List<T> list = new ArrayList(src.size());
/* 123:141 */     for (V v : src) {
/* 124:142 */       list.add(BeanUtils.createFromSrc(v, dest));
/* 125:    */     }
/* 126:144 */     return list;
/* 127:    */   }
/* 128:    */   
/* 129:    */   @Universal
/* 130:    */   public static <V> List<V> deepCopy(List<V> list)
/* 131:    */   {
/* 132:165 */     if ((list instanceof LinkedList)) {
/* 133:166 */       return deepCopyToList(list, new LinkedList());
/* 134:    */     }
/* 135:167 */     if ((list instanceof CopyOnWriteArrayList)) {
/* 136:168 */       return deepCopyToList(list, new CopyOnWriteArrayList());
/* 137:    */     }
/* 138:170 */     return deepCopy(list);
/* 139:    */   }
/* 140:    */   
/* 141:    */   public static <V> List<List<V>> lists(Collection<V>... collections)
/* 142:    */   {
/* 143:176 */     List<List<V>> lists = new ArrayList(collections.length);
/* 144:177 */     for (Collection<V> collection : collections) {
/* 145:178 */       lists.add(new ArrayList(collection));
/* 146:    */     }
/* 147:180 */     return lists;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public static <V> List<V> list(Iterable<V> iterable)
/* 151:    */   {
/* 152:186 */     List<V> list = new ArrayList();
/* 153:187 */     for (V o : iterable) {
/* 154:188 */       list.add(o);
/* 155:    */     }
/* 156:190 */     return list;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public static <V> List<V> list(Collection<V> collection)
/* 160:    */   {
/* 161:196 */     return new ArrayList(collection);
/* 162:    */   }
/* 163:    */   
/* 164:    */   public static <V> List<V> linkedList(Iterable<V> iterable)
/* 165:    */   {
/* 166:200 */     List<V> list = new LinkedList();
/* 167:201 */     for (V o : iterable) {
/* 168:202 */       list.add(o);
/* 169:    */     }
/* 170:204 */     return list;
/* 171:    */   }
/* 172:    */   
/* 173:    */   public static List<?> toListOrSingletonList(Object item)
/* 174:    */   {
/* 175:208 */     if (item == null) {
/* 176:209 */       return new ArrayList();
/* 177:    */     }
/* 178:210 */     if (item.getClass().isArray())
/* 179:    */     {
/* 180:211 */       int length = Array.getLength(item);
/* 181:212 */       List<Object> list = new ArrayList();
/* 182:213 */       for (int index = 0; index < length; index++) {
/* 183:214 */         list.add(Array.get(item, index));
/* 184:    */       }
/* 185:216 */       return list;
/* 186:    */     }
/* 187:217 */     if ((item instanceof Collection)) {
/* 188:218 */       return list((Collection)item);
/* 189:    */     }
/* 190:219 */     if ((item instanceof Iterator)) {
/* 191:220 */       return list((Iterator)item);
/* 192:    */     }
/* 193:221 */     if ((item instanceof Enumeration)) {
/* 194:222 */       return list((Enumeration)item);
/* 195:    */     }
/* 196:223 */     if ((item instanceof Iterable)) {
/* 197:224 */       return list((Iterable)item);
/* 198:    */     }
/* 199:226 */     List<Object> list = new ArrayList();
/* 200:227 */     list.add(item);
/* 201:228 */     return list;
/* 202:    */   }
/* 203:    */   
/* 204:    */   public static <PROP> List<PROP> toList(List<?> inputList, Class<PROP> cls, String propertyPath)
/* 205:    */   {
/* 206:234 */     List<PROP> outputList = new ArrayList();
/* 207:236 */     for (Object o : inputList) {
/* 208:237 */       outputList.add(BeanUtils.idx(o, propertyPath));
/* 209:    */     }
/* 210:240 */     return outputList;
/* 211:    */   }
/* 212:    */   
/* 213:    */   public static IntList toIntList(List<?> inputList, String propertyPath)
/* 214:    */   {
/* 215:245 */     return IntList.toIntList(inputList, propertyPath);
/* 216:    */   }
/* 217:    */   
/* 218:    */   public static FloatList toFloatList(List<?> inputList, String propertyPath)
/* 219:    */   {
/* 220:251 */     return FloatList.toFloatList(inputList, propertyPath);
/* 221:    */   }
/* 222:    */   
/* 223:    */   public static DoubleList toDoubleList(List<?> inputList, String propertyPath)
/* 224:    */   {
/* 225:257 */     return DoubleList.toDoubleList(inputList, propertyPath);
/* 226:    */   }
/* 227:    */   
/* 228:    */   public static LongList toLongList(List<?> inputList, String propertyPath)
/* 229:    */   {
/* 230:263 */     return LongList.toLongList(inputList, propertyPath);
/* 231:    */   }
/* 232:    */   
/* 233:    */   public static List<?> toList(List<?> inputList, String propertyPath)
/* 234:    */   {
/* 235:267 */     List<Object> outputList = new ArrayList();
/* 236:269 */     for (Object o : inputList) {
/* 237:270 */       outputList.add(BeanUtils.idx(o, propertyPath));
/* 238:    */     }
/* 239:273 */     return outputList;
/* 240:    */   }
/* 241:    */   
/* 242:    */   public static List<?> toList(Object item)
/* 243:    */   {
/* 244:277 */     if ((item != null) && (item.getClass().isArray()))
/* 245:    */     {
/* 246:278 */       int length = Array.getLength(item);
/* 247:279 */       List<Object> list = new ArrayList();
/* 248:280 */       for (int index = 0; index < length; index++) {
/* 249:281 */         list.add(Array.get(item, index));
/* 250:    */       }
/* 251:283 */       return list;
/* 252:    */     }
/* 253:284 */     if ((item instanceof Collection)) {
/* 254:285 */       return list((Collection)item);
/* 255:    */     }
/* 256:286 */     if ((item instanceof Iterator)) {
/* 257:287 */       return list((Iterator)item);
/* 258:    */     }
/* 259:288 */     if ((item instanceof Enumeration)) {
/* 260:289 */       return list((Enumeration)item);
/* 261:    */     }
/* 262:290 */     if ((item instanceof Iterable)) {
/* 263:291 */       return list((Iterable)item);
/* 264:    */     }
/* 265:293 */     return MapObjectConversion.toList(item);
/* 266:    */   }
/* 267:    */   
/* 268:    */   public static <V, WRAP> List<WRAP> convert(Class<WRAP> wrapper, Iterable<V> collection)
/* 269:    */   {
/* 270:300 */     List<WRAP> list = new ArrayList();
/* 271:302 */     for (V v : collection) {
/* 272:304 */       list.add(Conversions.coerce(wrapper, v));
/* 273:    */     }
/* 274:306 */     return list;
/* 275:    */   }
/* 276:    */   
/* 277:    */   public static <V, WRAP> List<WRAP> convert(Class<WRAP> wrapper, Collection<V> collection)
/* 278:    */   {
/* 279:310 */     List<WRAP> list = new ArrayList(collection.size());
/* 280:312 */     for (V v : collection) {
/* 281:314 */       list.add(Conversions.coerce(wrapper, v));
/* 282:    */     }
/* 283:316 */     return list;
/* 284:    */   }
/* 285:    */   
/* 286:    */   public static <V, WRAP> List<WRAP> convert(Class<WRAP> wrapper, V[] collection)
/* 287:    */   {
/* 288:321 */     List<WRAP> list = new ArrayList(collection.length);
/* 289:323 */     for (V v : collection) {
/* 290:325 */       list.add(Conversions.coerce(wrapper, v));
/* 291:    */     }
/* 292:327 */     return list;
/* 293:    */   }
/* 294:    */   
/* 295:    */   public static <V, WRAP> List<WRAP> wrap(Class<WRAP> wrapper, Iterable<V> collection)
/* 296:    */   {
/* 297:332 */     List<WRAP> list = new ArrayList();
/* 298:334 */     for (V v : collection)
/* 299:    */     {
/* 300:335 */       WRAP wrap = Reflection.newInstance(wrapper, v);
/* 301:336 */       list.add(wrap);
/* 302:    */     }
/* 303:338 */     return list;
/* 304:    */   }
/* 305:    */   
/* 306:    */   public static <V, WRAP> List<WRAP> wrap(Class<WRAP> wrapper, Collection<V> collection)
/* 307:    */   {
/* 308:343 */     if (collection.size() == 0) {
/* 309:344 */       return Collections.EMPTY_LIST;
/* 310:    */     }
/* 311:347 */     List<WRAP> list = new ArrayList(collection.size());
/* 312:    */     
/* 313:    */ 
/* 314:    */ 
/* 315:351 */     ClassMeta<WRAP> cls = ClassMeta.classMeta(wrapper);
/* 316:352 */     ConstructorAccess<WRAP> declaredConstructor = cls.declaredConstructor(collection.iterator().next().getClass());
/* 317:354 */     for (V v : collection)
/* 318:    */     {
/* 319:355 */       WRAP wrap = declaredConstructor.create(new Object[] { v });
/* 320:356 */       list.add(wrap);
/* 321:    */     }
/* 322:358 */     return list;
/* 323:    */   }
/* 324:    */   
/* 325:    */   public static <V, WRAP> List<WRAP> wrap(Class<WRAP> wrapper, V[] collection)
/* 326:    */   {
/* 327:363 */     List<WRAP> list = new ArrayList(collection.length);
/* 328:365 */     for (V v : collection)
/* 329:    */     {
/* 330:366 */       WRAP wrap = Reflection.newInstance(wrapper, v);
/* 331:367 */       list.add(wrap);
/* 332:    */     }
/* 333:369 */     return list;
/* 334:    */   }
/* 335:    */   
/* 336:    */   public static <V> List<V> list(Enumeration<V> enumeration)
/* 337:    */   {
/* 338:373 */     List<V> list = new ArrayList();
/* 339:374 */     while (enumeration.hasMoreElements()) {
/* 340:375 */       list.add(enumeration.nextElement());
/* 341:    */     }
/* 342:377 */     return list;
/* 343:    */   }
/* 344:    */   
/* 345:    */   public static <V> Enumeration<V> enumeration(List<V> list)
/* 346:    */   {
/* 347:382 */     Iterator<V> iter = list.iterator();
/* 348:383 */     new Enumeration()
/* 349:    */     {
/* 350:    */       public boolean hasMoreElements()
/* 351:    */       {
/* 352:386 */         return this.val$iter.hasNext();
/* 353:    */       }
/* 354:    */       
/* 355:    */       public V nextElement()
/* 356:    */       {
/* 357:391 */         return this.val$iter.next();
/* 358:    */       }
/* 359:    */     };
/* 360:    */   }
/* 361:    */   
/* 362:    */   public static <V> List<V> list(Iterator<V> iterator)
/* 363:    */   {
/* 364:399 */     List<V> list = new ArrayList();
/* 365:400 */     while (iterator.hasNext()) {
/* 366:401 */       list.add(iterator.next());
/* 367:    */     }
/* 368:403 */     return list;
/* 369:    */   }
/* 370:    */   
/* 371:    */   @SafeVarargs
/* 372:    */   public static <V> List<V> list(V... array)
/* 373:    */   {
/* 374:410 */     if (array == null) {
/* 375:411 */       return new ArrayList();
/* 376:    */     }
/* 377:413 */     List<V> list = new ArrayList(array.length);
/* 378:414 */     Collections.addAll(list, array);
/* 379:415 */     return list;
/* 380:    */   }
/* 381:    */   
/* 382:    */   public static <V> List<V> safeList(Class<V> cls)
/* 383:    */   {
/* 384:419 */     return new CopyOnWriteArrayList();
/* 385:    */   }
/* 386:    */   
/* 387:    */   @SafeVarargs
/* 388:    */   public static <V> List<V> safeList(V... array)
/* 389:    */   {
/* 390:424 */     return new CopyOnWriteArrayList(array);
/* 391:    */   }
/* 392:    */   
/* 393:    */   @SafeVarargs
/* 394:    */   public static <V> List<V> linkedList(V... array)
/* 395:    */   {
/* 396:429 */     if (array == null) {
/* 397:430 */       return new LinkedList();
/* 398:    */     }
/* 399:432 */     List<V> list = new LinkedList();
/* 400:433 */     Collections.addAll(list, array);
/* 401:434 */     return list;
/* 402:    */   }
/* 403:    */   
/* 404:    */   public static <V> List<V> safeList(Collection<V> collection)
/* 405:    */   {
/* 406:439 */     return new CopyOnWriteArrayList(collection);
/* 407:    */   }
/* 408:    */   
/* 409:    */   public static <V> List<V> linkedList(Collection<V> collection)
/* 410:    */   {
/* 411:443 */     return new LinkedList(collection);
/* 412:    */   }
/* 413:    */   
/* 414:    */   @Universal
/* 415:    */   public static int len(List<?> list)
/* 416:    */   {
/* 417:451 */     return list.size();
/* 418:    */   }
/* 419:    */   
/* 420:    */   @Universal
/* 421:    */   public static int lengthOf(List<?> list)
/* 422:    */   {
/* 423:457 */     return len(list);
/* 424:    */   }
/* 425:    */   
/* 426:    */   public static boolean isEmpty(List<?> list)
/* 427:    */   {
/* 428:461 */     return (list == null) || (list.size() == 0);
/* 429:    */   }
/* 430:    */   
/* 431:    */   @Universal
/* 432:    */   public static <V> boolean in(V value, List<?> list)
/* 433:    */   {
/* 434:466 */     return list.contains(value);
/* 435:    */   }
/* 436:    */   
/* 437:    */   @Universal
/* 438:    */   public static <V> void add(List<V> list, V value)
/* 439:    */   {
/* 440:471 */     list.add(value);
/* 441:    */   }
/* 442:    */   
/* 443:    */   @Universal
/* 444:    */   public static <V> void add(List<V> list, V... values)
/* 445:    */   {
/* 446:477 */     for (V v : values) {
/* 447:478 */       list.add(v);
/* 448:    */     }
/* 449:    */   }
/* 450:    */   
/* 451:    */   @Universal
/* 452:    */   public static <T> T atIndex(List<T> list, int index)
/* 453:    */   {
/* 454:485 */     return idx(list, index);
/* 455:    */   }
/* 456:    */   
/* 457:    */   @Universal
/* 458:    */   public static <T> T idx(List<T> list, int index)
/* 459:    */   {
/* 460:491 */     int i = calculateIndex(list, index);
/* 461:492 */     if (i > list.size() - 1) {
/* 462:493 */       i = list.size() - 1;
/* 463:    */     }
/* 464:495 */     return list.get(i);
/* 465:    */   }
/* 466:    */   
/* 467:    */   public static <T> List idxList(List<T> list, int index)
/* 468:    */   {
/* 469:500 */     return (List)idx(list, index);
/* 470:    */   }
/* 471:    */   
/* 472:    */   public static <T> Map idxMap(List<T> list, int index)
/* 473:    */   {
/* 474:505 */     return (Map)idx(list, index);
/* 475:    */   }
/* 476:    */   
/* 477:    */   @Universal
/* 478:    */   public static <V> void atIndex(List<V> list, int index, V v)
/* 479:    */   {
/* 480:511 */     idx(list, index, v);
/* 481:    */   }
/* 482:    */   
/* 483:    */   @Universal
/* 484:    */   public static <V> void idx(List<V> list, int index, V v)
/* 485:    */   {
/* 486:516 */     int i = calculateIndex(list, index);
/* 487:517 */     list.set(i, v);
/* 488:    */   }
/* 489:    */   
/* 490:    */   @Universal
/* 491:    */   public static <V> List<V> sliceOf(List<V> list, int startIndex, int endIndex)
/* 492:    */   {
/* 493:523 */     return slc(list, startIndex, endIndex);
/* 494:    */   }
/* 495:    */   
/* 496:    */   @Universal
/* 497:    */   public static <V> List<V> slc(List<V> list, int startIndex, int endIndex)
/* 498:    */   {
/* 499:528 */     int start = calculateIndex(list, startIndex);
/* 500:529 */     int end = calculateIndex(list, endIndex);
/* 501:530 */     return list.subList(start, end);
/* 502:    */   }
/* 503:    */   
/* 504:    */   @Universal
/* 505:    */   public static <V> List<V> sliceOf(List<V> list, int startIndex)
/* 506:    */   {
/* 507:536 */     return slc(list, startIndex);
/* 508:    */   }
/* 509:    */   
/* 510:    */   @Universal
/* 511:    */   public static <V> List<V> slc(List<V> list, int startIndex)
/* 512:    */   {
/* 513:541 */     return slc(list, startIndex, list.size());
/* 514:    */   }
/* 515:    */   
/* 516:    */   @Universal
/* 517:    */   public static <V> List<V> endSliceOf(List<V> list, int endIndex)
/* 518:    */   {
/* 519:546 */     return slcEnd(list, endIndex);
/* 520:    */   }
/* 521:    */   
/* 522:    */   @Universal
/* 523:    */   public static <V> List<V> slcEnd(List<V> list, int endIndex)
/* 524:    */   {
/* 525:552 */     return slc(list, 0, endIndex);
/* 526:    */   }
/* 527:    */   
/* 528:    */   @Universal
/* 529:    */   public static <V> List<V> copy(List<V> list)
/* 530:    */   {
/* 531:558 */     if ((list instanceof LinkedList)) {
/* 532:559 */       return new LinkedList(list);
/* 533:    */     }
/* 534:560 */     if ((list instanceof CopyOnWriteArrayList)) {
/* 535:561 */       return new CopyOnWriteArrayList(list);
/* 536:    */     }
/* 537:563 */     return new ArrayList(list);
/* 538:    */   }
/* 539:    */   
/* 540:    */   @Universal
/* 541:    */   public static <V> List<V> copy(CopyOnWriteArrayList<V> list)
/* 542:    */   {
/* 543:570 */     return new CopyOnWriteArrayList(list);
/* 544:    */   }
/* 545:    */   
/* 546:    */   @Universal
/* 547:    */   public static <V> List<V> copy(ArrayList<V> list)
/* 548:    */   {
/* 549:575 */     return new ArrayList(list);
/* 550:    */   }
/* 551:    */   
/* 552:    */   @Universal
/* 553:    */   public static <V> List<V> copy(LinkedList<V> list)
/* 554:    */   {
/* 555:580 */     return new LinkedList(list);
/* 556:    */   }
/* 557:    */   
/* 558:    */   @Universal
/* 559:    */   public static <V> void insert(List<V> list, int index, V v)
/* 560:    */   {
/* 561:586 */     int i = calculateIndex(list, index);
/* 562:587 */     list.add(i, v);
/* 563:    */   }
/* 564:    */   
/* 565:    */   private static <T> int calculateIndex(List<T> list, int originalIndex)
/* 566:    */   {
/* 567:593 */     int length = list.size();
/* 568:    */     
/* 569:595 */     int index = originalIndex;
/* 570:600 */     if (index < 0) {
/* 571:601 */       index = length + index;
/* 572:    */     }
/* 573:609 */     if (index < 0) {
/* 574:610 */       index = 0;
/* 575:    */     }
/* 576:612 */     if (index > length) {
/* 577:613 */       index = length;
/* 578:    */     }
/* 579:615 */     return index;
/* 580:    */   }
/* 581:    */   
/* 582:    */   public static <T> List<T> listFromProperty(Class<T> propertyType, String propertyPath, Collection<?> list)
/* 583:    */   {
/* 584:620 */     List<T> newList = new ArrayList(list.size());
/* 585:622 */     for (Object item : list)
/* 586:    */     {
/* 587:623 */       T newItem = BeanUtils.idx(item, propertyPath);
/* 588:624 */       newList.add(newItem);
/* 589:    */     }
/* 590:627 */     return newList;
/* 591:    */   }
/* 592:    */   
/* 593:    */   public static <T> List<T> listFromProperty(Class<T> propertyType, String propertyPath, Iterable<?> list)
/* 594:    */   {
/* 595:633 */     List<T> newList = new ArrayList();
/* 596:635 */     for (Object item : list)
/* 597:    */     {
/* 598:636 */       T newItem = BeanUtils.idx(item, propertyPath);
/* 599:637 */       newList.add(newItem);
/* 600:    */     }
/* 601:640 */     return newList;
/* 602:    */   }
/* 603:    */   
/* 604:    */   public static List<Map<String, Object>> toListOfMaps(List<?> list)
/* 605:    */   {
/* 606:644 */     return MapObjectConversion.toListOfMaps(list);
/* 607:    */   }
/* 608:    */   
/* 609:    */   public static void setListProperty(List<?> list, String propertyName, Object value)
/* 610:    */   {
/* 611:648 */     for (Object object : list) {
/* 612:649 */       BeanUtils.idx(object, propertyName, value);
/* 613:    */     }
/* 614:    */   }
/* 615:    */   
/* 616:    */   public static List<?> mapBy(Object[] objects, Object instance, String methodName)
/* 617:    */   {
/* 618:656 */     List list = new ArrayList(objects.length);
/* 619:657 */     for (Object o : objects) {
/* 620:658 */       list.add(Invoker.invoke(instance, methodName, new Object[] { o }));
/* 621:    */     }
/* 622:660 */     return list;
/* 623:    */   }
/* 624:    */   
/* 625:    */   public static List<?> mapBy(Object[] objects, Class<?> cls, String methodName)
/* 626:    */   {
/* 627:666 */     List list = new ArrayList(objects.length);
/* 628:667 */     for (Object o : objects) {
/* 629:668 */       list.add(Invoker.invoke(cls, methodName, new Object[] { o }));
/* 630:    */     }
/* 631:670 */     return list;
/* 632:    */   }
/* 633:    */   
/* 634:    */   public static List<?> mapBy(Iterable<?> objects, Class<?> cls, String methodName)
/* 635:    */   {
/* 636:677 */     List list = new ArrayList();
/* 637:678 */     for (Object o : objects) {
/* 638:679 */       list.add(Invoker.invoke(cls, methodName, new Object[] { o }));
/* 639:    */     }
/* 640:681 */     return list;
/* 641:    */   }
/* 642:    */   
/* 643:    */   public static List<?> mapBy(Iterable<?> objects, Object instance, String methodName)
/* 644:    */   {
/* 645:687 */     List list = new ArrayList();
/* 646:688 */     for (Object o : objects) {
/* 647:689 */       list.add(Invoker.invoke(instance, methodName, new Object[] { o }));
/* 648:    */     }
/* 649:691 */     return list;
/* 650:    */   }
/* 651:    */   
/* 652:    */   public static List<?> mapBy(Collection<?> objects, Class<?> cls, String methodName)
/* 653:    */   {
/* 654:697 */     List list = new ArrayList(objects.size());
/* 655:    */     
/* 656:699 */     MethodAccess methodAccess = Invoker.invokeMethodAccess(cls, methodName);
/* 657:701 */     for (Object o : objects) {
/* 658:702 */       list.add(methodAccess.invokeStatic(new Object[] { o }));
/* 659:    */     }
/* 660:704 */     return list;
/* 661:    */   }
/* 662:    */   
/* 663:    */   public static List<?> mapBy(Collection<?> objects, Object function)
/* 664:    */   {
/* 665:710 */     MethodAccess methodAccess = Invoker.invokeFunctionMethodAccess(function);
/* 666:    */     
/* 667:712 */     List list = new ArrayList();
/* 668:713 */     for (Object o : objects) {
/* 669:714 */       list.add(methodAccess.invoke(function, new Object[] { o }));
/* 670:    */     }
/* 671:717 */     return list;
/* 672:    */   }
/* 673:    */   
/* 674:    */   public static <T> List<T> mapBy(Class<T> cls, Collection<?> objects, Object function)
/* 675:    */   {
/* 676:722 */     return mapBy(objects, function);
/* 677:    */   }
/* 678:    */   
/* 679:    */   public static List<?> mapBy(Iterable<?> objects, Object function)
/* 680:    */   {
/* 681:728 */     MethodAccess methodAccess = Invoker.invokeFunctionMethodAccess(function);
/* 682:    */     
/* 683:730 */     List list = new ArrayList();
/* 684:731 */     for (Object o : objects) {
/* 685:732 */       list.add(methodAccess.invoke(function, new Object[] { o }));
/* 686:    */     }
/* 687:734 */     return list;
/* 688:    */   }
/* 689:    */   
/* 690:    */   public static List<?> mapBy(Object[] objects, Object function)
/* 691:    */   {
/* 692:740 */     MethodAccess methodAccess = Invoker.invokeFunctionMethodAccess(function);
/* 693:    */     
/* 694:742 */     List list = new ArrayList(objects.length);
/* 695:743 */     for (Object o : objects) {
/* 696:744 */       list.add(methodAccess.invoke(function, new Object[] { o }));
/* 697:    */     }
/* 698:746 */     return list;
/* 699:    */   }
/* 700:    */   
/* 701:    */   public static List<?> mapBy(Collection<?> objects, Object object, String methodName)
/* 702:    */   {
/* 703:754 */     MethodAccess methodAccess = Invoker.invokeMethodAccess(object.getClass(), methodName);
/* 704:    */     
/* 705:756 */     List list = new ArrayList(objects.size());
/* 706:757 */     for (Object o : objects) {
/* 707:758 */       list.add(methodAccess.invoke(object, new Object[] { o }));
/* 708:    */     }
/* 709:760 */     return list;
/* 710:    */   }
/* 711:    */   
/* 712:    */   public static <V, N> List<N> mapBy(V[] array, Function<V, N> function)
/* 713:    */   {
/* 714:765 */     List<N> list = new ArrayList(array.length);
/* 715:767 */     for (V v : array) {
/* 716:768 */       list.add(function.apply(v));
/* 717:    */     }
/* 718:770 */     return list;
/* 719:    */   }
/* 720:    */   
/* 721:    */   public static <V, N> List<N> mapBy(Collection<V> array, Function<V, N> function)
/* 722:    */   {
/* 723:774 */     List<N> list = new ArrayList(array.size());
/* 724:776 */     for (V v : array) {
/* 725:777 */       list.add(function.apply(v));
/* 726:    */     }
/* 727:779 */     return list;
/* 728:    */   }
/* 729:    */   
/* 730:    */   public static <V, N> List<N> mapBy(Iterable<V> array, Function<V, N> function)
/* 731:    */   {
/* 732:783 */     List<N> list = new ArrayList();
/* 733:785 */     for (V v : array) {
/* 734:786 */       list.add(function.apply(v));
/* 735:    */     }
/* 736:788 */     return list;
/* 737:    */   }
/* 738:    */   
/* 739:    */   public static <V, SUM> SUM reduceBy(Iterable<V> array, Reducer<V, SUM> function)
/* 740:    */   {
/* 741:794 */     SUM sum = null;
/* 742:795 */     for (V v : array) {
/* 743:796 */       sum = function.apply(sum, v);
/* 744:    */     }
/* 745:798 */     return sum;
/* 746:    */   }
/* 747:    */   
/* 748:    */   public static Object reduceBy(Iterable<?> array, Object object)
/* 749:    */   {
/* 750:804 */     Object sum = null;
/* 751:805 */     for (Object v : array) {
/* 752:806 */       sum = Invoker.invokeReducer(object, sum, v);
/* 753:    */     }
/* 754:808 */     return sum;
/* 755:    */   }
/* 756:    */   
/* 757:    */   public static <T> List<T> filterBy(Iterable<T> array, Predicate<T> predicate)
/* 758:    */   {
/* 759:814 */     List<T> list = new ArrayList();
/* 760:816 */     for (T v : array) {
/* 761:817 */       if (predicate.test(v)) {
/* 762:818 */         list.add(v);
/* 763:    */       }
/* 764:    */     }
/* 765:821 */     return list;
/* 766:    */   }
/* 767:    */   
/* 768:    */   public static <T> List<T> filterBy(Collection<T> array, Predicate<T> predicate)
/* 769:    */   {
/* 770:826 */     List<T> list = new ArrayList(array.size());
/* 771:828 */     for (T v : array) {
/* 772:829 */       if (predicate.test(v)) {
/* 773:830 */         list.add(v);
/* 774:    */       }
/* 775:    */     }
/* 776:833 */     return list;
/* 777:    */   }
/* 778:    */   
/* 779:    */   public static <T> List<T> filterBy(Predicate<T> predicate, T[] array)
/* 780:    */   {
/* 781:838 */     List<T> list = new ArrayList(array.length);
/* 782:840 */     for (T v : array) {
/* 783:841 */       if (predicate.test(v)) {
/* 784:842 */         list.add(v);
/* 785:    */       }
/* 786:    */     }
/* 787:845 */     return list;
/* 788:    */   }
/* 789:    */   
/* 790:    */   public static <T> List<T> filterBy(Iterable<T> array, Object object)
/* 791:    */   {
/* 792:851 */     List<T> list = new ArrayList();
/* 793:853 */     for (T v : array) {
/* 794:854 */       if (Invoker.invokeBooleanReturn(object, v)) {
/* 795:855 */         list.add(v);
/* 796:    */       }
/* 797:    */     }
/* 798:858 */     return list;
/* 799:    */   }
/* 800:    */   
/* 801:    */   public static <T> List<T> filterBy(Collection<T> array, Object object)
/* 802:    */   {
/* 803:863 */     List<T> list = new ArrayList(array.size());
/* 804:865 */     for (T v : array) {
/* 805:866 */       if (Invoker.invokeBooleanReturn(object, v)) {
/* 806:867 */         list.add(v);
/* 807:    */       }
/* 808:    */     }
/* 809:870 */     return list;
/* 810:    */   }
/* 811:    */   
/* 812:    */   public static <T> List<T> filterBy(T[] array, Object object)
/* 813:    */   {
/* 814:875 */     List<T> list = new ArrayList(array.length);
/* 815:877 */     for (T v : array) {
/* 816:878 */       if (Invoker.invokeBooleanReturn(object, v)) {
/* 817:879 */         list.add(v);
/* 818:    */       }
/* 819:    */     }
/* 820:882 */     return list;
/* 821:    */   }
/* 822:    */   
/* 823:    */   public static <T> List<T> filterBy(Iterable<T> array, Object object, String methodName)
/* 824:    */   {
/* 825:889 */     List<T> list = new ArrayList();
/* 826:891 */     for (T v : array) {
/* 827:892 */       if (((Boolean)Invoker.invokeEither(object, methodName, new Object[] { v })).booleanValue()) {
/* 828:893 */         list.add(v);
/* 829:    */       }
/* 830:    */     }
/* 831:896 */     return list;
/* 832:    */   }
/* 833:    */   
/* 834:    */   public static <T> List<T> filterBy(Collection<T> array, Object object, String methodName)
/* 835:    */   {
/* 836:901 */     List<T> list = new ArrayList(array.size());
/* 837:903 */     for (T v : array) {
/* 838:904 */       if (((Boolean)Invoker.invokeEither(object, methodName, new Object[] { v })).booleanValue()) {
/* 839:905 */         list.add(v);
/* 840:    */       }
/* 841:    */     }
/* 842:908 */     return list;
/* 843:    */   }
/* 844:    */   
/* 845:    */   public static <T> List<T> filterBy(T[] array, Object object, String methodName)
/* 846:    */   {
/* 847:913 */     List<T> list = new ArrayList(array.length);
/* 848:915 */     for (T v : array) {
/* 849:916 */       if (((Boolean)Invoker.invokeEither(object, methodName, new Object[] { v })).booleanValue()) {
/* 850:917 */         list.add(v);
/* 851:    */       }
/* 852:    */     }
/* 853:920 */     return list;
/* 854:    */   }
/* 855:    */   
/* 856:    */   public static String toPrettyJson(List list)
/* 857:    */   {
/* 858:926 */     CharBuf buf = CharBuf.createCharBuf();
/* 859:927 */     return buf.prettyPrintCollection(list, false, 0).toString();
/* 860:    */   }
/* 861:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.Lists
 * JD-Core Version:    0.7.0.1
 */