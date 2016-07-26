/*    1:     */ package org.boon;
/*    2:     */ 
/*    3:     */ import java.util.ArrayList;
/*    4:     */ import java.util.Collection;
/*    5:     */ import java.util.Comparator;
/*    6:     */ import java.util.HashMap;
/*    7:     */ import java.util.Iterator;
/*    8:     */ import java.util.LinkedHashMap;
/*    9:     */ import java.util.LinkedHashSet;
/*   10:     */ import java.util.List;
/*   11:     */ import java.util.Map;
/*   12:     */ import java.util.Map.Entry;
/*   13:     */ import java.util.NavigableMap;
/*   14:     */ import java.util.SortedMap;
/*   15:     */ import java.util.TreeMap;
/*   16:     */ import java.util.concurrent.ConcurrentHashMap;
/*   17:     */ import java.util.concurrent.ConcurrentSkipListMap;
/*   18:     */ import org.boon.core.Conversions;
/*   19:     */ import org.boon.core.Typ;
/*   20:     */ import org.boon.core.reflection.BeanUtils;
/*   21:     */ import org.boon.core.reflection.MapObjectConversion;
/*   22:     */ import org.boon.primitive.CharBuf;
/*   23:     */ 
/*   24:     */ public class Maps
/*   25:     */ {
/*   26:     */   public static <V> List<V> lazyCreate(List<V> lazy)
/*   27:     */   {
/*   28:  50 */     if (lazy == null) {
/*   29:  52 */       lazy = new ArrayList();
/*   30:     */     }
/*   31:  54 */     return lazy;
/*   32:     */   }
/*   33:     */   
/*   34:     */   public static <K, V> Map<K, V> lazyCreate(Map<K, V> lazy)
/*   35:     */   {
/*   36:  58 */     if (lazy == null) {
/*   37:  60 */       lazy = new LinkedHashMap();
/*   38:     */     }
/*   39:  62 */     return lazy;
/*   40:     */   }
/*   41:     */   
/*   42:     */   public static <K, V> Map<K, V> lazyCreate(HashMap<K, V> lazy)
/*   43:     */   {
/*   44:  67 */     if (lazy == null) {
/*   45:  69 */       lazy = new HashMap();
/*   46:     */     }
/*   47:  71 */     return lazy;
/*   48:     */   }
/*   49:     */   
/*   50:     */   public static <K, V> Map<K, V> lazyCreate(LinkedHashMap<K, V> lazy)
/*   51:     */   {
/*   52:  76 */     if (lazy == null) {
/*   53:  78 */       lazy = new LinkedHashMap();
/*   54:     */     }
/*   55:  80 */     return lazy;
/*   56:     */   }
/*   57:     */   
/*   58:     */   public static <K, V> Map<K, V> lazyCreateLinked(Map<K, V> lazy)
/*   59:     */   {
/*   60:  85 */     if (lazy == null) {
/*   61:  87 */       lazy = new LinkedHashMap();
/*   62:     */     }
/*   63:  89 */     return lazy;
/*   64:     */   }
/*   65:     */   
/*   66:     */   public static <K, V> Map<K, V> lazyCreate(ConcurrentHashMap<K, V> lazy)
/*   67:     */   {
/*   68:  95 */     if (lazy == null) {
/*   69:  97 */       lazy = new ConcurrentHashMap();
/*   70:     */     }
/*   71:  99 */     return lazy;
/*   72:     */   }
/*   73:     */   
/*   74:     */   public static <K, V> Map<K, V> lazyCreateSafe(Map<K, V> lazy)
/*   75:     */   {
/*   76: 103 */     if (lazy == null) {
/*   77: 105 */       lazy = new ConcurrentHashMap();
/*   78:     */     }
/*   79: 107 */     return lazy;
/*   80:     */   }
/*   81:     */   
/*   82:     */   @Universal
/*   83:     */   public static int lengthOf(Map<?, ?> map)
/*   84:     */   {
/*   85: 113 */     return len(map);
/*   86:     */   }
/*   87:     */   
/*   88:     */   @Universal
/*   89:     */   public static <K, V> V atIndex(Map<K, V> map, K k)
/*   90:     */   {
/*   91: 118 */     return idx(map, k);
/*   92:     */   }
/*   93:     */   
/*   94:     */   @Universal
/*   95:     */   public static <K, V> SortedMap<K, V> sliceOf(NavigableMap<K, V> map, K startIndex, K endIndex)
/*   96:     */   {
/*   97: 123 */     return slc(map, startIndex, endIndex);
/*   98:     */   }
/*   99:     */   
/*  100:     */   @Universal
/*  101:     */   public static <K, V> SortedMap<K, V> endSliceOf(NavigableMap<K, V> map, K fromKey)
/*  102:     */   {
/*  103: 129 */     return slcEnd(map, fromKey);
/*  104:     */   }
/*  105:     */   
/*  106:     */   @Universal
/*  107:     */   public static int len(Map<?, ?> map)
/*  108:     */   {
/*  109: 137 */     return map.size();
/*  110:     */   }
/*  111:     */   
/*  112:     */   @Universal
/*  113:     */   public static <K, V> boolean in(K key, Map<K, V> map)
/*  114:     */   {
/*  115: 143 */     return map.containsKey(key);
/*  116:     */   }
/*  117:     */   
/*  118:     */   @Universal
/*  119:     */   public static <K, V> void add(Map<K, V> map, Entry<K, V> entry)
/*  120:     */   {
/*  121: 148 */     map.put(entry.key(), entry.value());
/*  122:     */   }
/*  123:     */   
/*  124:     */   @Universal
/*  125:     */   public static <K, V> V idx(Map<K, V> map, K k)
/*  126:     */   {
/*  127: 153 */     return map.get(k);
/*  128:     */   }
/*  129:     */   
/*  130:     */   @Universal
/*  131:     */   public static <K, V> void idx(Map<K, V> map, K k, V v)
/*  132:     */   {
/*  133: 158 */     map.put(k, v);
/*  134:     */   }
/*  135:     */   
/*  136:     */   public static <K, V> String idxStr(Map<K, V> map, K k)
/*  137:     */   {
/*  138: 164 */     return Str.toString(map.get(k));
/*  139:     */   }
/*  140:     */   
/*  141:     */   public static <K, V> Integer idxInt(Map<K, V> map, K k)
/*  142:     */   {
/*  143: 169 */     return (Integer)map.get(k);
/*  144:     */   }
/*  145:     */   
/*  146:     */   public static <K, V> Long idxLong(Map<K, V> map, K k)
/*  147:     */   {
/*  148: 175 */     return (Long)map.get(k);
/*  149:     */   }
/*  150:     */   
/*  151:     */   public static <K, V> Map idxMap(Map<K, V> map, K k)
/*  152:     */   {
/*  153: 180 */     return (Map)map.get(k);
/*  154:     */   }
/*  155:     */   
/*  156:     */   public static <K, V> List idxList(Map<K, V> map, K k)
/*  157:     */   {
/*  158: 185 */     return (List)map.get(k);
/*  159:     */   }
/*  160:     */   
/*  161:     */   public static <K, V> long toLong(Map<K, V> map, K key)
/*  162:     */   {
/*  163: 191 */     V value = map.get(key);
/*  164: 192 */     long l = Conversions.toLong(value, -9223372036854775808L);
/*  165: 193 */     if (l == -9223372036854775808L) {
/*  166: 194 */       Exceptions.die(new Object[] { "Cannot convert", key, "into long value", value });
/*  167:     */     }
/*  168: 196 */     return l;
/*  169:     */   }
/*  170:     */   
/*  171:     */   public static <K, V> int toInt(Map<K, V> map, K key)
/*  172:     */   {
/*  173: 201 */     V value = map.get(key);
/*  174: 202 */     int v = Conversions.toInt(value, -2147483648);
/*  175: 203 */     if (v == -2147483648) {
/*  176: 204 */       Exceptions.die(new Object[] { "Cannot convert", key, "into int value", value });
/*  177:     */     }
/*  178: 206 */     return v;
/*  179:     */   }
/*  180:     */   
/*  181:     */   @Universal
/*  182:     */   public static <K, V> SortedMap<K, V> copy(SortedMap<K, V> map)
/*  183:     */   {
/*  184: 211 */     if ((map instanceof TreeMap)) {
/*  185: 212 */       return new TreeMap(map);
/*  186:     */     }
/*  187: 213 */     if ((map instanceof ConcurrentSkipListMap)) {
/*  188: 214 */       return new ConcurrentSkipListMap(map);
/*  189:     */     }
/*  190: 216 */     return new TreeMap(map);
/*  191:     */   }
/*  192:     */   
/*  193:     */   @Universal
/*  194:     */   public static <K, V> Map<K, V> copy(Map<K, V> map)
/*  195:     */   {
/*  196: 222 */     if ((map instanceof LinkedHashMap)) {
/*  197: 223 */       return new LinkedHashMap(map);
/*  198:     */     }
/*  199: 224 */     if ((map instanceof ConcurrentHashMap)) {
/*  200: 225 */       return new ConcurrentHashMap(map);
/*  201:     */     }
/*  202: 227 */     return new LinkedHashMap(map);
/*  203:     */   }
/*  204:     */   
/*  205:     */   @Universal
/*  206:     */   public static <K, V> V first(NavigableMap<K, V> map)
/*  207:     */   {
/*  208: 235 */     return map.firstEntry().getValue();
/*  209:     */   }
/*  210:     */   
/*  211:     */   @Universal
/*  212:     */   public static <K, V> V last(NavigableMap<K, V> map)
/*  213:     */   {
/*  214: 243 */     return map.lastEntry().getValue();
/*  215:     */   }
/*  216:     */   
/*  217:     */   @Universal
/*  218:     */   public static <K, V> V after(NavigableMap<K, V> map, K index)
/*  219:     */   {
/*  220: 251 */     return map.get(map.higherKey(index));
/*  221:     */   }
/*  222:     */   
/*  223:     */   @Universal
/*  224:     */   public static <K, V> V before(NavigableMap<K, V> map, K index)
/*  225:     */   {
/*  226: 257 */     return map.get(map.lowerKey(index));
/*  227:     */   }
/*  228:     */   
/*  229:     */   @Universal
/*  230:     */   public static <K, V> SortedMap<K, V> slc(NavigableMap<K, V> map, K startIndex, K endIndex)
/*  231:     */   {
/*  232: 263 */     return map.subMap(startIndex, endIndex);
/*  233:     */   }
/*  234:     */   
/*  235:     */   @Universal
/*  236:     */   public static <K, V> SortedMap<K, V> slcEnd(NavigableMap<K, V> map, K fromKey)
/*  237:     */   {
/*  238: 269 */     return map.tailMap(fromKey);
/*  239:     */   }
/*  240:     */   
/*  241:     */   @Universal
/*  242:     */   public static <K, V> SortedMap<K, V> slc(NavigableMap<K, V> map, K toKey)
/*  243:     */   {
/*  244: 274 */     return map.headMap(toKey);
/*  245:     */   }
/*  246:     */   
/*  247:     */   public static <K, V> boolean valueIn(V value, Map<K, V> map)
/*  248:     */   {
/*  249: 282 */     return map.containsValue(value);
/*  250:     */   }
/*  251:     */   
/*  252:     */   public static <K, V> Entry<K, V> entry(K k, V v)
/*  253:     */   {
/*  254: 287 */     return new Pair(k, v);
/*  255:     */   }
/*  256:     */   
/*  257:     */   public static <K, V> Entry<K, V> entry(Entry<K, V> entry)
/*  258:     */   {
/*  259: 291 */     return new Pair(entry);
/*  260:     */   }
/*  261:     */   
/*  262:     */   public static Map<?, ?> mapFromArray(Object... args)
/*  263:     */   {
/*  264: 295 */     Map<Object, Object> map = map(Object.class, Object.class);
/*  265: 297 */     if (args.length % 2 != 0) {
/*  266: 298 */       return (Map)Exceptions.die(Map.class, "mapFromArray arguments must be equal");
/*  267:     */     }
/*  268: 301 */     Object lastKey = null;
/*  269: 302 */     for (int index = 0; index < args.length; index++) {
/*  270: 304 */       if (index % 2 == 0) {
/*  271: 305 */         lastKey = args[index];
/*  272:     */       } else {
/*  273: 307 */         map.put(lastKey, args[index]);
/*  274:     */       }
/*  275:     */     }
/*  276: 311 */     return map;
/*  277:     */   }
/*  278:     */   
/*  279:     */   public static <K, V> Map<K, V> map(Class<K> keyClass, Class<V> valueClass)
/*  280:     */   {
/*  281: 317 */     return new LinkedHashMap(10);
/*  282:     */   }
/*  283:     */   
/*  284:     */   public static <K, V> Map<K, V> safeMap(Class<K> keyClass, Class<V> valueClass)
/*  285:     */   {
/*  286: 321 */     return new ConcurrentHashMap(10);
/*  287:     */   }
/*  288:     */   
/*  289:     */   public static <K, V> Map<K, V> map(K k0, V v0)
/*  290:     */   {
/*  291: 325 */     Map<K, V> map = new LinkedHashMap(10);
/*  292: 326 */     map.put(k0, v0);
/*  293: 327 */     return map;
/*  294:     */   }
/*  295:     */   
/*  296:     */   public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1)
/*  297:     */   {
/*  298: 331 */     Map<K, V> map = new LinkedHashMap(10);
/*  299: 332 */     map.put(k0, v0);
/*  300: 333 */     map.put(k1, v1);
/*  301: 334 */     return map;
/*  302:     */   }
/*  303:     */   
/*  304:     */   public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2)
/*  305:     */   {
/*  306: 339 */     Map<K, V> map = new LinkedHashMap(10);
/*  307: 340 */     map.put(k0, v0);
/*  308: 341 */     map.put(k1, v1);
/*  309: 342 */     map.put(k2, v2);
/*  310: 343 */     return map;
/*  311:     */   }
/*  312:     */   
/*  313:     */   public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3)
/*  314:     */   {
/*  315: 348 */     Map<K, V> map = new LinkedHashMap(10);
/*  316: 349 */     map.put(k0, v0);
/*  317: 350 */     map.put(k1, v1);
/*  318: 351 */     map.put(k2, v2);
/*  319: 352 */     map.put(k3, v3);
/*  320: 353 */     return map;
/*  321:     */   }
/*  322:     */   
/*  323:     */   public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4)
/*  324:     */   {
/*  325: 358 */     Map<K, V> map = new LinkedHashMap(10);
/*  326: 359 */     map.put(k0, v0);
/*  327: 360 */     map.put(k1, v1);
/*  328: 361 */     map.put(k2, v2);
/*  329: 362 */     map.put(k3, v3);
/*  330: 363 */     map.put(k4, v4);
/*  331: 364 */     return map;
/*  332:     */   }
/*  333:     */   
/*  334:     */   public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5)
/*  335:     */   {
/*  336: 369 */     Map<K, V> map = new LinkedHashMap(10);
/*  337: 370 */     map.put(k0, v0);
/*  338: 371 */     map.put(k1, v1);
/*  339: 372 */     map.put(k2, v2);
/*  340: 373 */     map.put(k3, v3);
/*  341: 374 */     map.put(k4, v4);
/*  342: 375 */     map.put(k5, v5);
/*  343: 376 */     return map;
/*  344:     */   }
/*  345:     */   
/*  346:     */   public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6)
/*  347:     */   {
/*  348: 381 */     Map<K, V> map = new LinkedHashMap(10);
/*  349: 382 */     map.put(k0, v0);
/*  350: 383 */     map.put(k1, v1);
/*  351: 384 */     map.put(k2, v2);
/*  352: 385 */     map.put(k3, v3);
/*  353: 386 */     map.put(k4, v4);
/*  354: 387 */     map.put(k5, v5);
/*  355: 388 */     map.put(k6, v6);
/*  356: 389 */     return map;
/*  357:     */   }
/*  358:     */   
/*  359:     */   public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7)
/*  360:     */   {
/*  361: 394 */     Map<K, V> map = new LinkedHashMap(10);
/*  362: 395 */     map.put(k0, v0);
/*  363: 396 */     map.put(k1, v1);
/*  364: 397 */     map.put(k2, v2);
/*  365: 398 */     map.put(k3, v3);
/*  366: 399 */     map.put(k4, v4);
/*  367: 400 */     map.put(k5, v5);
/*  368: 401 */     map.put(k6, v6);
/*  369: 402 */     map.put(k7, v7);
/*  370: 403 */     return map;
/*  371:     */   }
/*  372:     */   
/*  373:     */   public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8)
/*  374:     */   {
/*  375: 408 */     Map<K, V> map = new LinkedHashMap(10);
/*  376: 409 */     map.put(k0, v0);
/*  377: 410 */     map.put(k1, v1);
/*  378: 411 */     map.put(k2, v2);
/*  379: 412 */     map.put(k3, v3);
/*  380: 413 */     map.put(k4, v4);
/*  381: 414 */     map.put(k5, v5);
/*  382: 415 */     map.put(k6, v6);
/*  383: 416 */     map.put(k7, v7);
/*  384: 417 */     map.put(k8, v8);
/*  385: 418 */     return map;
/*  386:     */   }
/*  387:     */   
/*  388:     */   public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9)
/*  389:     */   {
/*  390: 424 */     Map<K, V> map = new LinkedHashMap(10);
/*  391: 425 */     map.put(k0, v0);
/*  392: 426 */     map.put(k1, v1);
/*  393: 427 */     map.put(k2, v2);
/*  394: 428 */     map.put(k3, v3);
/*  395: 429 */     map.put(k4, v4);
/*  396: 430 */     map.put(k5, v5);
/*  397: 431 */     map.put(k6, v6);
/*  398: 432 */     map.put(k7, v7);
/*  399: 433 */     map.put(k8, v8);
/*  400: 434 */     map.put(k9, v9);
/*  401: 435 */     return map;
/*  402:     */   }
/*  403:     */   
/*  404:     */   public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10)
/*  405:     */   {
/*  406: 442 */     Map<K, V> map = new LinkedHashMap(11);
/*  407: 443 */     map.put(k0, v0);
/*  408: 444 */     map.put(k1, v1);
/*  409: 445 */     map.put(k2, v2);
/*  410: 446 */     map.put(k3, v3);
/*  411: 447 */     map.put(k4, v4);
/*  412: 448 */     map.put(k5, v5);
/*  413: 449 */     map.put(k6, v6);
/*  414: 450 */     map.put(k7, v7);
/*  415: 451 */     map.put(k8, v8);
/*  416: 452 */     map.put(k9, v9);
/*  417: 453 */     map.put(k10, v10);
/*  418:     */     
/*  419: 455 */     return map;
/*  420:     */   }
/*  421:     */   
/*  422:     */   public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10, K k11, V v11)
/*  423:     */   {
/*  424: 461 */     Map<K, V> map = new LinkedHashMap(12);
/*  425: 462 */     map.put(k0, v0);
/*  426: 463 */     map.put(k1, v1);
/*  427: 464 */     map.put(k2, v2);
/*  428: 465 */     map.put(k3, v3);
/*  429: 466 */     map.put(k4, v4);
/*  430: 467 */     map.put(k5, v5);
/*  431: 468 */     map.put(k6, v6);
/*  432: 469 */     map.put(k7, v7);
/*  433: 470 */     map.put(k8, v8);
/*  434: 471 */     map.put(k9, v9);
/*  435: 472 */     map.put(k10, v10);
/*  436: 473 */     map.put(k11, v11);
/*  437:     */     
/*  438: 475 */     return map;
/*  439:     */   }
/*  440:     */   
/*  441:     */   public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10, K k11, V v11, K k12, V v12)
/*  442:     */   {
/*  443: 481 */     Map<K, V> map = new LinkedHashMap(13);
/*  444: 482 */     map.put(k0, v0);
/*  445: 483 */     map.put(k1, v1);
/*  446: 484 */     map.put(k2, v2);
/*  447: 485 */     map.put(k3, v3);
/*  448: 486 */     map.put(k4, v4);
/*  449: 487 */     map.put(k5, v5);
/*  450: 488 */     map.put(k6, v6);
/*  451: 489 */     map.put(k7, v7);
/*  452: 490 */     map.put(k8, v8);
/*  453: 491 */     map.put(k9, v9);
/*  454: 492 */     map.put(k10, v10);
/*  455: 493 */     map.put(k11, v11);
/*  456: 494 */     map.put(k12, v12);
/*  457:     */     
/*  458: 496 */     return map;
/*  459:     */   }
/*  460:     */   
/*  461:     */   public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10, K k11, V v11, K k12, V v12, K k13, V v13)
/*  462:     */   {
/*  463: 504 */     Map<K, V> map = new LinkedHashMap(14);
/*  464: 505 */     map.put(k0, v0);
/*  465: 506 */     map.put(k1, v1);
/*  466: 507 */     map.put(k2, v2);
/*  467: 508 */     map.put(k3, v3);
/*  468: 509 */     map.put(k4, v4);
/*  469: 510 */     map.put(k5, v5);
/*  470: 511 */     map.put(k6, v6);
/*  471: 512 */     map.put(k7, v7);
/*  472: 513 */     map.put(k8, v8);
/*  473: 514 */     map.put(k9, v9);
/*  474: 515 */     map.put(k10, v10);
/*  475: 516 */     map.put(k11, v11);
/*  476: 517 */     map.put(k12, v12);
/*  477: 518 */     map.put(k13, v13);
/*  478:     */     
/*  479: 520 */     return map;
/*  480:     */   }
/*  481:     */   
/*  482:     */   public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10, K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14)
/*  483:     */   {
/*  484: 528 */     Map<K, V> map = new LinkedHashMap(15);
/*  485: 529 */     map.put(k0, v0);
/*  486: 530 */     map.put(k1, v1);
/*  487: 531 */     map.put(k2, v2);
/*  488: 532 */     map.put(k3, v3);
/*  489: 533 */     map.put(k4, v4);
/*  490: 534 */     map.put(k5, v5);
/*  491: 535 */     map.put(k6, v6);
/*  492: 536 */     map.put(k7, v7);
/*  493: 537 */     map.put(k8, v8);
/*  494: 538 */     map.put(k9, v9);
/*  495: 539 */     map.put(k10, v10);
/*  496: 540 */     map.put(k11, v11);
/*  497: 541 */     map.put(k12, v12);
/*  498: 542 */     map.put(k13, v13);
/*  499: 543 */     map.put(k14, v14);
/*  500:     */     
/*  501: 545 */     return map;
/*  502:     */   }
/*  503:     */   
/*  504:     */   public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10, K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14, K k15, V v15)
/*  505:     */   {
/*  506: 552 */     Map<K, V> map = new LinkedHashMap(16);
/*  507: 553 */     map.put(k0, v0);
/*  508: 554 */     map.put(k1, v1);
/*  509: 555 */     map.put(k2, v2);
/*  510: 556 */     map.put(k3, v3);
/*  511: 557 */     map.put(k4, v4);
/*  512: 558 */     map.put(k5, v5);
/*  513: 559 */     map.put(k6, v6);
/*  514: 560 */     map.put(k7, v7);
/*  515: 561 */     map.put(k8, v8);
/*  516: 562 */     map.put(k9, v9);
/*  517: 563 */     map.put(k10, v10);
/*  518: 564 */     map.put(k11, v11);
/*  519: 565 */     map.put(k12, v12);
/*  520: 566 */     map.put(k13, v13);
/*  521: 567 */     map.put(k14, v14);
/*  522: 568 */     map.put(k15, v15);
/*  523:     */     
/*  524: 570 */     return map;
/*  525:     */   }
/*  526:     */   
/*  527:     */   public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10, K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14, K k15, V v15, K k16, V v16)
/*  528:     */   {
/*  529: 578 */     Map<K, V> map = new LinkedHashMap(17);
/*  530: 579 */     map.put(k0, v0);
/*  531: 580 */     map.put(k1, v1);
/*  532: 581 */     map.put(k2, v2);
/*  533: 582 */     map.put(k3, v3);
/*  534: 583 */     map.put(k4, v4);
/*  535: 584 */     map.put(k5, v5);
/*  536: 585 */     map.put(k6, v6);
/*  537: 586 */     map.put(k7, v7);
/*  538: 587 */     map.put(k8, v8);
/*  539: 588 */     map.put(k9, v9);
/*  540: 589 */     map.put(k10, v10);
/*  541: 590 */     map.put(k11, v11);
/*  542: 591 */     map.put(k12, v12);
/*  543: 592 */     map.put(k13, v13);
/*  544: 593 */     map.put(k14, v14);
/*  545: 594 */     map.put(k15, v15);
/*  546: 595 */     map.put(k16, v16);
/*  547:     */     
/*  548: 597 */     return map;
/*  549:     */   }
/*  550:     */   
/*  551:     */   public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10, K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14, K k15, V v15, K k16, V v16, K k17, V v17)
/*  552:     */   {
/*  553: 605 */     Map<K, V> map = new LinkedHashMap(18);
/*  554: 606 */     map.put(k0, v0);
/*  555: 607 */     map.put(k1, v1);
/*  556: 608 */     map.put(k2, v2);
/*  557: 609 */     map.put(k3, v3);
/*  558: 610 */     map.put(k4, v4);
/*  559: 611 */     map.put(k5, v5);
/*  560: 612 */     map.put(k6, v6);
/*  561: 613 */     map.put(k7, v7);
/*  562: 614 */     map.put(k8, v8);
/*  563: 615 */     map.put(k9, v9);
/*  564: 616 */     map.put(k10, v10);
/*  565: 617 */     map.put(k11, v11);
/*  566: 618 */     map.put(k12, v12);
/*  567: 619 */     map.put(k13, v13);
/*  568: 620 */     map.put(k14, v14);
/*  569: 621 */     map.put(k15, v15);
/*  570: 622 */     map.put(k16, v16);
/*  571: 623 */     map.put(k17, v17);
/*  572:     */     
/*  573:     */ 
/*  574: 626 */     return map;
/*  575:     */   }
/*  576:     */   
/*  577:     */   public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10, K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14, K k15, V v15, K k16, V v16, K k17, V v17, K k18, V v18)
/*  578:     */   {
/*  579: 634 */     Map<K, V> map = new LinkedHashMap(19);
/*  580: 635 */     map.put(k0, v0);
/*  581: 636 */     map.put(k1, v1);
/*  582: 637 */     map.put(k2, v2);
/*  583: 638 */     map.put(k3, v3);
/*  584: 639 */     map.put(k4, v4);
/*  585: 640 */     map.put(k5, v5);
/*  586: 641 */     map.put(k6, v6);
/*  587: 642 */     map.put(k7, v7);
/*  588: 643 */     map.put(k8, v8);
/*  589: 644 */     map.put(k9, v9);
/*  590: 645 */     map.put(k10, v10);
/*  591: 646 */     map.put(k11, v11);
/*  592: 647 */     map.put(k12, v12);
/*  593: 648 */     map.put(k13, v13);
/*  594: 649 */     map.put(k14, v14);
/*  595: 650 */     map.put(k15, v15);
/*  596: 651 */     map.put(k16, v16);
/*  597: 652 */     map.put(k17, v17);
/*  598: 653 */     map.put(k18, v18);
/*  599:     */     
/*  600: 655 */     return map;
/*  601:     */   }
/*  602:     */   
/*  603:     */   public static <K, V> Map<K, V> map(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10, K k11, V v11, K k12, V v12, K k13, V v13, K k14, V v14, K k15, V v15, K k16, V v16, K k17, V v17, K k18, V v18, K k19, V v19)
/*  604:     */   {
/*  605: 664 */     Map<K, V> map = new LinkedHashMap(20);
/*  606: 665 */     map.put(k0, v0);
/*  607: 666 */     map.put(k1, v1);
/*  608: 667 */     map.put(k2, v2);
/*  609: 668 */     map.put(k3, v3);
/*  610: 669 */     map.put(k4, v4);
/*  611: 670 */     map.put(k5, v5);
/*  612: 671 */     map.put(k6, v6);
/*  613: 672 */     map.put(k7, v7);
/*  614: 673 */     map.put(k8, v8);
/*  615: 674 */     map.put(k9, v9);
/*  616: 675 */     map.put(k10, v10);
/*  617: 676 */     map.put(k11, v11);
/*  618: 677 */     map.put(k12, v12);
/*  619: 678 */     map.put(k13, v13);
/*  620: 679 */     map.put(k14, v14);
/*  621: 680 */     map.put(k15, v15);
/*  622: 681 */     map.put(k16, v16);
/*  623: 682 */     map.put(k17, v17);
/*  624: 683 */     map.put(k18, v18);
/*  625: 684 */     map.put(k19, v19);
/*  626:     */     
/*  627: 686 */     return map;
/*  628:     */   }
/*  629:     */   
/*  630:     */   public static <K, V> Map<K, V> map(List<K> keys, List<V> values)
/*  631:     */   {
/*  632: 694 */     Map<K, V> map = new LinkedHashMap(10 + keys.size());
/*  633: 695 */     Iterator<V> iterator = values.iterator();
/*  634: 696 */     for (K k : keys) {
/*  635: 697 */       if (iterator.hasNext())
/*  636:     */       {
/*  637: 698 */         V v = iterator.next();
/*  638: 699 */         map.put(k, v);
/*  639:     */       }
/*  640:     */       else
/*  641:     */       {
/*  642: 701 */         map.put(k, null);
/*  643:     */       }
/*  644:     */     }
/*  645: 704 */     return map;
/*  646:     */   }
/*  647:     */   
/*  648:     */   public static <K, V> Map<K, V> map(LinkedHashSet<K> keys, LinkedHashSet<V> values)
/*  649:     */   {
/*  650: 709 */     Map<K, V> map = new LinkedHashMap(10 + keys.size());
/*  651: 710 */     Iterator<V> iterator = values.iterator();
/*  652: 711 */     for (K k : keys) {
/*  653: 712 */       if (iterator.hasNext())
/*  654:     */       {
/*  655: 713 */         V v = iterator.next();
/*  656: 714 */         map.put(k, v);
/*  657:     */       }
/*  658:     */       else
/*  659:     */       {
/*  660: 716 */         map.put(k, null);
/*  661:     */       }
/*  662:     */     }
/*  663: 719 */     return map;
/*  664:     */   }
/*  665:     */   
/*  666:     */   public static <K, V> Map<K, V> map(Iterable<K> keys, Iterable<V> values)
/*  667:     */   {
/*  668: 731 */     Map<K, V> map = new LinkedHashMap();
/*  669: 732 */     Iterator<V> iterator = values.iterator();
/*  670: 733 */     for (K k : keys) {
/*  671: 734 */       if (iterator.hasNext())
/*  672:     */       {
/*  673: 735 */         V v = iterator.next();
/*  674: 736 */         map.put(k, v);
/*  675:     */       }
/*  676:     */       else
/*  677:     */       {
/*  678: 738 */         map.put(k, null);
/*  679:     */       }
/*  680:     */     }
/*  681: 741 */     return map;
/*  682:     */   }
/*  683:     */   
/*  684:     */   public static <K, V> Map<K, V> map(K[] keys, V[] values)
/*  685:     */   {
/*  686: 746 */     Map<K, V> map = new LinkedHashMap(10 + keys.length);
/*  687: 747 */     int index = 0;
/*  688: 748 */     for (K k : keys)
/*  689:     */     {
/*  690: 749 */       if (index < keys.length)
/*  691:     */       {
/*  692: 750 */         V v = values[index];
/*  693: 751 */         map.put(k, v);
/*  694:     */       }
/*  695:     */       else
/*  696:     */       {
/*  697: 753 */         map.put(k, null);
/*  698:     */       }
/*  699: 755 */       index++;
/*  700:     */     }
/*  701: 757 */     return map;
/*  702:     */   }
/*  703:     */   
/*  704:     */   @SafeVarargs
/*  705:     */   public static <K, V> Map<K, V> map(Entry<K, V>... entries)
/*  706:     */   {
/*  707: 763 */     Map<K, V> map = new LinkedHashMap(entries.length);
/*  708: 764 */     for (Entry<K, V> entry : entries) {
/*  709: 765 */       map.put(entry.key(), entry.value());
/*  710:     */     }
/*  711: 767 */     return map;
/*  712:     */   }
/*  713:     */   
/*  714:     */   @SafeVarargs
/*  715:     */   public static <K, V> Map<K, V> mapByEntries(Entry<K, V>... entries)
/*  716:     */   {
/*  717: 773 */     Map<K, V> map = new LinkedHashMap(entries.length);
/*  718: 774 */     for (Entry<K, V> entry : entries) {
/*  719: 775 */       map.put(entry.key(), entry.value());
/*  720:     */     }
/*  721: 777 */     return map;
/*  722:     */   }
/*  723:     */   
/*  724:     */   public static <K, V> NavigableMap<K, V> sortedMap(K k0, V v0)
/*  725:     */   {
/*  726: 782 */     NavigableMap<K, V> map = new TreeMap();
/*  727: 783 */     map.put(k0, v0);
/*  728: 784 */     return map;
/*  729:     */   }
/*  730:     */   
/*  731:     */   public static <K, V> NavigableMap<K, V> sortedMap(K k0, V v0, K k1, V v1)
/*  732:     */   {
/*  733: 788 */     NavigableMap<K, V> map = new TreeMap();
/*  734: 789 */     map.put(k0, v0);
/*  735: 790 */     map.put(k1, v1);
/*  736: 791 */     return map;
/*  737:     */   }
/*  738:     */   
/*  739:     */   public static <K, V> NavigableMap<K, V> sortedMap(K k0, V v0, K k1, V v1, K k2, V v2)
/*  740:     */   {
/*  741: 795 */     NavigableMap<K, V> map = new TreeMap();
/*  742: 796 */     map.put(k0, v0);
/*  743: 797 */     map.put(k1, v1);
/*  744: 798 */     map.put(k2, v2);
/*  745: 799 */     return map;
/*  746:     */   }
/*  747:     */   
/*  748:     */   public static <K, V> NavigableMap<K, V> sortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3)
/*  749:     */   {
/*  750: 804 */     NavigableMap<K, V> map = new TreeMap();
/*  751: 805 */     map.put(k0, v0);
/*  752: 806 */     map.put(k1, v1);
/*  753: 807 */     map.put(k2, v2);
/*  754: 808 */     map.put(k3, v3);
/*  755: 809 */     return map;
/*  756:     */   }
/*  757:     */   
/*  758:     */   public static <K, V> NavigableMap<K, V> sortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4)
/*  759:     */   {
/*  760: 814 */     NavigableMap<K, V> map = new TreeMap();
/*  761: 815 */     map.put(k0, v0);
/*  762: 816 */     map.put(k1, v1);
/*  763: 817 */     map.put(k2, v2);
/*  764: 818 */     map.put(k3, v3);
/*  765: 819 */     map.put(k4, v4);
/*  766: 820 */     return map;
/*  767:     */   }
/*  768:     */   
/*  769:     */   public static <K, V> NavigableMap<K, V> sortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5)
/*  770:     */   {
/*  771: 825 */     NavigableMap<K, V> map = new TreeMap();
/*  772: 826 */     map.put(k0, v0);
/*  773: 827 */     map.put(k1, v1);
/*  774: 828 */     map.put(k2, v2);
/*  775: 829 */     map.put(k3, v3);
/*  776: 830 */     map.put(k4, v4);
/*  777: 831 */     map.put(k5, v5);
/*  778: 832 */     return map;
/*  779:     */   }
/*  780:     */   
/*  781:     */   public static <K, V> NavigableMap<K, V> sortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6)
/*  782:     */   {
/*  783: 837 */     NavigableMap<K, V> map = new TreeMap();
/*  784: 838 */     map.put(k0, v0);
/*  785: 839 */     map.put(k1, v1);
/*  786: 840 */     map.put(k2, v2);
/*  787: 841 */     map.put(k3, v3);
/*  788: 842 */     map.put(k4, v4);
/*  789: 843 */     map.put(k5, v5);
/*  790: 844 */     map.put(k6, v6);
/*  791: 845 */     return map;
/*  792:     */   }
/*  793:     */   
/*  794:     */   public static <K, V> NavigableMap<K, V> sortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7)
/*  795:     */   {
/*  796: 850 */     NavigableMap<K, V> map = new TreeMap();
/*  797: 851 */     map.put(k0, v0);
/*  798: 852 */     map.put(k1, v1);
/*  799: 853 */     map.put(k2, v2);
/*  800: 854 */     map.put(k3, v3);
/*  801: 855 */     map.put(k4, v4);
/*  802: 856 */     map.put(k5, v5);
/*  803: 857 */     map.put(k6, v6);
/*  804: 858 */     map.put(k7, v7);
/*  805: 859 */     return map;
/*  806:     */   }
/*  807:     */   
/*  808:     */   public static <K, V> NavigableMap<K, V> sortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8)
/*  809:     */   {
/*  810: 864 */     NavigableMap<K, V> map = new TreeMap();
/*  811: 865 */     map.put(k0, v0);
/*  812: 866 */     map.put(k1, v1);
/*  813: 867 */     map.put(k2, v2);
/*  814: 868 */     map.put(k3, v3);
/*  815: 869 */     map.put(k4, v4);
/*  816: 870 */     map.put(k5, v5);
/*  817: 871 */     map.put(k6, v6);
/*  818: 872 */     map.put(k7, v7);
/*  819: 873 */     map.put(k8, v8);
/*  820: 874 */     return map;
/*  821:     */   }
/*  822:     */   
/*  823:     */   public static <K, V> NavigableMap<K, V> sortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9)
/*  824:     */   {
/*  825: 880 */     NavigableMap<K, V> map = new TreeMap();
/*  826: 881 */     map.put(k0, v0);
/*  827: 882 */     map.put(k1, v1);
/*  828: 883 */     map.put(k2, v2);
/*  829: 884 */     map.put(k3, v3);
/*  830: 885 */     map.put(k4, v4);
/*  831: 886 */     map.put(k5, v5);
/*  832: 887 */     map.put(k6, v6);
/*  833: 888 */     map.put(k7, v7);
/*  834: 889 */     map.put(k8, v8);
/*  835: 890 */     map.put(k9, v9);
/*  836: 891 */     return map;
/*  837:     */   }
/*  838:     */   
/*  839:     */   public static <K, V> NavigableMap<K, V> sortedMap(Collection<K> keys, Collection<V> values)
/*  840:     */   {
/*  841: 895 */     NavigableMap<K, V> map = new TreeMap();
/*  842: 896 */     Iterator<V> iterator = values.iterator();
/*  843: 897 */     for (K k : keys) {
/*  844: 898 */       if (iterator.hasNext())
/*  845:     */       {
/*  846: 899 */         V v = iterator.next();
/*  847: 900 */         map.put(k, v);
/*  848:     */       }
/*  849:     */       else
/*  850:     */       {
/*  851: 902 */         map.put(k, null);
/*  852:     */       }
/*  853:     */     }
/*  854: 905 */     return map;
/*  855:     */   }
/*  856:     */   
/*  857:     */   public static <K, V> NavigableMap<K, V> sortedMap(Iterable<K> keys, Iterable<V> values)
/*  858:     */   {
/*  859: 910 */     NavigableMap<K, V> map = new TreeMap();
/*  860: 911 */     Iterator<V> iterator = values.iterator();
/*  861: 912 */     for (K k : keys) {
/*  862: 913 */       if (iterator.hasNext())
/*  863:     */       {
/*  864: 914 */         V v = iterator.next();
/*  865: 915 */         map.put(k, v);
/*  866:     */       }
/*  867:     */       else
/*  868:     */       {
/*  869: 917 */         map.put(k, null);
/*  870:     */       }
/*  871:     */     }
/*  872: 920 */     return map;
/*  873:     */   }
/*  874:     */   
/*  875:     */   public static <K, V> NavigableMap<K, V> sortedMap(K[] keys, V[] values)
/*  876:     */   {
/*  877: 926 */     NavigableMap<K, V> map = new TreeMap();
/*  878: 927 */     int index = 0;
/*  879: 928 */     for (K k : keys)
/*  880:     */     {
/*  881: 929 */       if (index < keys.length)
/*  882:     */       {
/*  883: 930 */         V v = values[index];
/*  884: 931 */         map.put(k, v);
/*  885:     */       }
/*  886:     */       else
/*  887:     */       {
/*  888: 933 */         map.put(k, null);
/*  889:     */       }
/*  890: 935 */       index++;
/*  891:     */     }
/*  892: 937 */     return map;
/*  893:     */   }
/*  894:     */   
/*  895:     */   public static <K, V> NavigableMap<K, V> sortedMap(List<Entry<K, V>> entries)
/*  896:     */   {
/*  897: 942 */     NavigableMap<K, V> map = new TreeMap();
/*  898: 943 */     for (Entry<K, V> entry : entries) {
/*  899: 944 */       map.put(entry.key(), entry.value());
/*  900:     */     }
/*  901: 946 */     return map;
/*  902:     */   }
/*  903:     */   
/*  904:     */   public static <K, V> NavigableMap<K, V> sortedMap(Comparator<K> comparator, K k0, V v0)
/*  905:     */   {
/*  906: 953 */     NavigableMap<K, V> map = new TreeMap(comparator);
/*  907: 954 */     map.put(k0, v0);
/*  908: 955 */     return map;
/*  909:     */   }
/*  910:     */   
/*  911:     */   public static <K, V> NavigableMap<K, V> sortedMap(Comparator<K> comparator, K k0, V v0, K k1, V v1)
/*  912:     */   {
/*  913: 959 */     NavigableMap<K, V> map = new TreeMap(comparator);
/*  914: 960 */     map.put(k0, v0);
/*  915: 961 */     map.put(k1, v1);
/*  916: 962 */     return map;
/*  917:     */   }
/*  918:     */   
/*  919:     */   public static <K, V> NavigableMap<K, V> sortedMap(Comparator<K> comparator, K k0, V v0, K k1, V v1, K k2, V v2)
/*  920:     */   {
/*  921: 966 */     NavigableMap<K, V> map = new TreeMap(comparator);
/*  922: 967 */     map.put(k0, v0);
/*  923: 968 */     map.put(k1, v1);
/*  924: 969 */     map.put(k2, v2);
/*  925: 970 */     return map;
/*  926:     */   }
/*  927:     */   
/*  928:     */   public static <K, V> NavigableMap<K, V> sortedMap(Comparator<K> comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3)
/*  929:     */   {
/*  930: 975 */     NavigableMap<K, V> map = new TreeMap(comparator);
/*  931: 976 */     map.put(k0, v0);
/*  932: 977 */     map.put(k1, v1);
/*  933: 978 */     map.put(k2, v2);
/*  934: 979 */     map.put(k3, v3);
/*  935: 980 */     return map;
/*  936:     */   }
/*  937:     */   
/*  938:     */   public static <K, V> NavigableMap<K, V> sortedMap(Comparator<K> comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4)
/*  939:     */   {
/*  940: 985 */     NavigableMap<K, V> map = new TreeMap(comparator);
/*  941: 986 */     map.put(k0, v0);
/*  942: 987 */     map.put(k1, v1);
/*  943: 988 */     map.put(k2, v2);
/*  944: 989 */     map.put(k3, v3);
/*  945: 990 */     map.put(k4, v4);
/*  946: 991 */     return map;
/*  947:     */   }
/*  948:     */   
/*  949:     */   public static <K, V> NavigableMap<K, V> sortedMap(Comparator<K> comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5)
/*  950:     */   {
/*  951: 996 */     NavigableMap<K, V> map = new TreeMap(comparator);
/*  952: 997 */     map.put(k0, v0);
/*  953: 998 */     map.put(k1, v1);
/*  954: 999 */     map.put(k2, v2);
/*  955:1000 */     map.put(k3, v3);
/*  956:1001 */     map.put(k4, v4);
/*  957:1002 */     map.put(k5, v5);
/*  958:1003 */     return map;
/*  959:     */   }
/*  960:     */   
/*  961:     */   public static <K, V> NavigableMap<K, V> sortedMap(Comparator<K> comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6)
/*  962:     */   {
/*  963:1008 */     NavigableMap<K, V> map = new TreeMap(comparator);
/*  964:1009 */     map.put(k0, v0);
/*  965:1010 */     map.put(k1, v1);
/*  966:1011 */     map.put(k2, v2);
/*  967:1012 */     map.put(k3, v3);
/*  968:1013 */     map.put(k4, v4);
/*  969:1014 */     map.put(k5, v5);
/*  970:1015 */     map.put(k6, v6);
/*  971:1016 */     return map;
/*  972:     */   }
/*  973:     */   
/*  974:     */   public static <K, V> NavigableMap<K, V> sortedMap(Comparator<K> comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7)
/*  975:     */   {
/*  976:1021 */     NavigableMap<K, V> map = new TreeMap(comparator);
/*  977:1022 */     map.put(k0, v0);
/*  978:1023 */     map.put(k1, v1);
/*  979:1024 */     map.put(k2, v2);
/*  980:1025 */     map.put(k3, v3);
/*  981:1026 */     map.put(k4, v4);
/*  982:1027 */     map.put(k5, v5);
/*  983:1028 */     map.put(k6, v6);
/*  984:1029 */     map.put(k7, v7);
/*  985:1030 */     return map;
/*  986:     */   }
/*  987:     */   
/*  988:     */   public static <K, V> NavigableMap<K, V> sortedMap(Comparator<K> comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8)
/*  989:     */   {
/*  990:1035 */     NavigableMap<K, V> map = new TreeMap(comparator);
/*  991:1036 */     map.put(k0, v0);
/*  992:1037 */     map.put(k1, v1);
/*  993:1038 */     map.put(k2, v2);
/*  994:1039 */     map.put(k3, v3);
/*  995:1040 */     map.put(k4, v4);
/*  996:1041 */     map.put(k5, v5);
/*  997:1042 */     map.put(k6, v6);
/*  998:1043 */     map.put(k7, v7);
/*  999:1044 */     map.put(k8, v8);
/* 1000:1045 */     return map;
/* 1001:     */   }
/* 1002:     */   
/* 1003:     */   public static <K, V> NavigableMap<K, V> sortedMap(Comparator<K> comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9)
/* 1004:     */   {
/* 1005:1051 */     NavigableMap<K, V> map = new TreeMap(comparator);
/* 1006:1052 */     map.put(k0, v0);
/* 1007:1053 */     map.put(k1, v1);
/* 1008:1054 */     map.put(k2, v2);
/* 1009:1055 */     map.put(k3, v3);
/* 1010:1056 */     map.put(k4, v4);
/* 1011:1057 */     map.put(k5, v5);
/* 1012:1058 */     map.put(k6, v6);
/* 1013:1059 */     map.put(k7, v7);
/* 1014:1060 */     map.put(k8, v8);
/* 1015:1061 */     map.put(k9, v9);
/* 1016:1062 */     return map;
/* 1017:     */   }
/* 1018:     */   
/* 1019:     */   public static <K, V> NavigableMap<K, V> sortedMap(Comparator<K> comparator, Collection<K> keys, Collection<V> values)
/* 1020:     */   {
/* 1021:1066 */     NavigableMap<K, V> map = new TreeMap(comparator);
/* 1022:1067 */     Iterator<V> iterator = values.iterator();
/* 1023:1068 */     for (K k : keys) {
/* 1024:1069 */       if (iterator.hasNext())
/* 1025:     */       {
/* 1026:1070 */         V v = iterator.next();
/* 1027:1071 */         map.put(k, v);
/* 1028:     */       }
/* 1029:     */       else
/* 1030:     */       {
/* 1031:1073 */         map.put(k, null);
/* 1032:     */       }
/* 1033:     */     }
/* 1034:1076 */     return map;
/* 1035:     */   }
/* 1036:     */   
/* 1037:     */   public static <K, V> NavigableMap<K, V> sortedMap(Comparator<K> comparator, K[] keys, V[] values)
/* 1038:     */   {
/* 1039:1081 */     NavigableMap<K, V> map = new TreeMap(comparator);
/* 1040:1082 */     int index = 0;
/* 1041:1083 */     for (K k : keys)
/* 1042:     */     {
/* 1043:1084 */       if (index < keys.length)
/* 1044:     */       {
/* 1045:1085 */         V v = values[index];
/* 1046:1086 */         map.put(k, v);
/* 1047:     */       }
/* 1048:     */       else
/* 1049:     */       {
/* 1050:1088 */         map.put(k, null);
/* 1051:     */       }
/* 1052:1090 */       index++;
/* 1053:     */     }
/* 1054:1092 */     return map;
/* 1055:     */   }
/* 1056:     */   
/* 1057:     */   public static <K, V> NavigableMap<K, V> sortedMap(Comparator<K> comparator, List<Entry<K, V>> entries)
/* 1058:     */   {
/* 1059:1097 */     NavigableMap<K, V> map = new TreeMap(comparator);
/* 1060:1098 */     for (Entry<K, V> entry : entries) {
/* 1061:1099 */       map.put(entry.key(), entry.value());
/* 1062:     */     }
/* 1063:1101 */     return map;
/* 1064:     */   }
/* 1065:     */   
/* 1066:     */   public static <K, V> Map<K, V> safeMap(Map<K, V> map)
/* 1067:     */   {
/* 1068:1106 */     return new ConcurrentHashMap(map);
/* 1069:     */   }
/* 1070:     */   
/* 1071:     */   public static <K, V> Map<K, V> safeMap(K k0, V v0)
/* 1072:     */   {
/* 1073:1110 */     Map<K, V> map = new ConcurrentHashMap(10);
/* 1074:1111 */     map.put(k0, v0);
/* 1075:1112 */     return map;
/* 1076:     */   }
/* 1077:     */   
/* 1078:     */   public static <K, V> Map<K, V> safeMap(K k0, V v0, K k1, V v1)
/* 1079:     */   {
/* 1080:1116 */     Map<K, V> map = new ConcurrentHashMap(10);
/* 1081:1117 */     map.put(k0, v0);
/* 1082:1118 */     map.put(k1, v1);
/* 1083:1119 */     return map;
/* 1084:     */   }
/* 1085:     */   
/* 1086:     */   public static <K, V> Map<K, V> safeMap(K k0, V v0, K k1, V v1, K k2, V v2)
/* 1087:     */   {
/* 1088:1124 */     Map<K, V> map = new ConcurrentHashMap(10);
/* 1089:1125 */     map.put(k0, v0);
/* 1090:1126 */     map.put(k1, v1);
/* 1091:1127 */     map.put(k2, v2);
/* 1092:1128 */     return map;
/* 1093:     */   }
/* 1094:     */   
/* 1095:     */   public static <K, V> Map<K, V> safeMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3)
/* 1096:     */   {
/* 1097:1133 */     Map<K, V> map = new ConcurrentHashMap(10);
/* 1098:1134 */     map.put(k0, v0);
/* 1099:1135 */     map.put(k1, v1);
/* 1100:1136 */     map.put(k2, v2);
/* 1101:1137 */     map.put(k3, v3);
/* 1102:1138 */     return map;
/* 1103:     */   }
/* 1104:     */   
/* 1105:     */   public static <K, V> Map<K, V> safeMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4)
/* 1106:     */   {
/* 1107:1143 */     Map<K, V> map = new ConcurrentHashMap(10);
/* 1108:1144 */     map.put(k0, v0);
/* 1109:1145 */     map.put(k1, v1);
/* 1110:1146 */     map.put(k2, v2);
/* 1111:1147 */     map.put(k3, v3);
/* 1112:1148 */     map.put(k4, v4);
/* 1113:1149 */     return map;
/* 1114:     */   }
/* 1115:     */   
/* 1116:     */   public static <K, V> Map<K, V> safeMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5)
/* 1117:     */   {
/* 1118:1154 */     Map<K, V> map = new ConcurrentHashMap(10);
/* 1119:1155 */     map.put(k0, v0);
/* 1120:1156 */     map.put(k1, v1);
/* 1121:1157 */     map.put(k2, v2);
/* 1122:1158 */     map.put(k3, v3);
/* 1123:1159 */     map.put(k4, v4);
/* 1124:1160 */     map.put(k5, v5);
/* 1125:1161 */     return map;
/* 1126:     */   }
/* 1127:     */   
/* 1128:     */   public static <K, V> Map<K, V> safeMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6)
/* 1129:     */   {
/* 1130:1166 */     Map<K, V> map = new ConcurrentHashMap(10);
/* 1131:1167 */     map.put(k0, v0);
/* 1132:1168 */     map.put(k1, v1);
/* 1133:1169 */     map.put(k2, v2);
/* 1134:1170 */     map.put(k3, v3);
/* 1135:1171 */     map.put(k4, v4);
/* 1136:1172 */     map.put(k5, v5);
/* 1137:1173 */     map.put(k6, v6);
/* 1138:1174 */     return map;
/* 1139:     */   }
/* 1140:     */   
/* 1141:     */   public static <K, V> Map<K, V> safeMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7)
/* 1142:     */   {
/* 1143:1179 */     Map<K, V> map = new ConcurrentHashMap(10);
/* 1144:1180 */     map.put(k0, v0);
/* 1145:1181 */     map.put(k1, v1);
/* 1146:1182 */     map.put(k2, v2);
/* 1147:1183 */     map.put(k3, v3);
/* 1148:1184 */     map.put(k4, v4);
/* 1149:1185 */     map.put(k5, v5);
/* 1150:1186 */     map.put(k6, v6);
/* 1151:1187 */     map.put(k7, v7);
/* 1152:1188 */     return map;
/* 1153:     */   }
/* 1154:     */   
/* 1155:     */   public static <K, V> Map<K, V> safeMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8)
/* 1156:     */   {
/* 1157:1193 */     Map<K, V> map = new ConcurrentHashMap(10);
/* 1158:1194 */     map.put(k0, v0);
/* 1159:1195 */     map.put(k1, v1);
/* 1160:1196 */     map.put(k2, v2);
/* 1161:1197 */     map.put(k3, v3);
/* 1162:1198 */     map.put(k4, v4);
/* 1163:1199 */     map.put(k5, v5);
/* 1164:1200 */     map.put(k6, v6);
/* 1165:1201 */     map.put(k7, v7);
/* 1166:1202 */     map.put(k8, v8);
/* 1167:1203 */     return map;
/* 1168:     */   }
/* 1169:     */   
/* 1170:     */   public static <K, V> Map<K, V> safeMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9)
/* 1171:     */   {
/* 1172:1209 */     Map<K, V> map = new ConcurrentHashMap(10);
/* 1173:1210 */     map.put(k0, v0);
/* 1174:1211 */     map.put(k1, v1);
/* 1175:1212 */     map.put(k2, v2);
/* 1176:1213 */     map.put(k3, v3);
/* 1177:1214 */     map.put(k4, v4);
/* 1178:1215 */     map.put(k5, v5);
/* 1179:1216 */     map.put(k6, v6);
/* 1180:1217 */     map.put(k7, v7);
/* 1181:1218 */     map.put(k8, v8);
/* 1182:1219 */     map.put(k9, v9);
/* 1183:1220 */     return map;
/* 1184:     */   }
/* 1185:     */   
/* 1186:     */   public static <K, V> Map<K, V> safeMap(Collection<K> keys, Collection<V> values)
/* 1187:     */   {
/* 1188:1224 */     Map<K, V> map = new ConcurrentHashMap(10 + keys.size());
/* 1189:1225 */     Iterator<V> iterator = values.iterator();
/* 1190:1226 */     for (K k : keys) {
/* 1191:1227 */       if (iterator.hasNext())
/* 1192:     */       {
/* 1193:1228 */         V v = iterator.next();
/* 1194:1229 */         map.put(k, v);
/* 1195:     */       }
/* 1196:     */       else
/* 1197:     */       {
/* 1198:1231 */         map.put(k, null);
/* 1199:     */       }
/* 1200:     */     }
/* 1201:1234 */     return map;
/* 1202:     */   }
/* 1203:     */   
/* 1204:     */   public static <K, V> Map<K, V> safeMap(Iterable<K> keys, Iterable<V> values)
/* 1205:     */   {
/* 1206:1238 */     Map<K, V> map = new ConcurrentHashMap();
/* 1207:1239 */     Iterator<V> iterator = values.iterator();
/* 1208:1240 */     for (K k : keys) {
/* 1209:1241 */       if (iterator.hasNext())
/* 1210:     */       {
/* 1211:1242 */         V v = iterator.next();
/* 1212:1243 */         map.put(k, v);
/* 1213:     */       }
/* 1214:     */       else
/* 1215:     */       {
/* 1216:1245 */         map.put(k, null);
/* 1217:     */       }
/* 1218:     */     }
/* 1219:1248 */     return map;
/* 1220:     */   }
/* 1221:     */   
/* 1222:     */   public static <K, V> Map<K, V> safeMap(K[] keys, V[] values)
/* 1223:     */   {
/* 1224:1253 */     Map<K, V> map = new ConcurrentHashMap(10 + keys.length);
/* 1225:1254 */     int index = 0;
/* 1226:1255 */     for (K k : keys)
/* 1227:     */     {
/* 1228:1256 */       if (index < keys.length)
/* 1229:     */       {
/* 1230:1257 */         V v = values[index];
/* 1231:1258 */         map.put(k, v);
/* 1232:     */       }
/* 1233:     */       else
/* 1234:     */       {
/* 1235:1260 */         map.put(k, null);
/* 1236:     */       }
/* 1237:1262 */       index++;
/* 1238:     */     }
/* 1239:1264 */     return map;
/* 1240:     */   }
/* 1241:     */   
/* 1242:     */   @SafeVarargs
/* 1243:     */   public static <K, V> Map<K, V> safeMap(Entry<K, V>... entries)
/* 1244:     */   {
/* 1245:1270 */     Map<K, V> map = new ConcurrentHashMap(entries.length);
/* 1246:1271 */     for (Entry<K, V> entry : entries) {
/* 1247:1272 */       map.put(entry.key(), entry.value());
/* 1248:     */     }
/* 1249:1274 */     return map;
/* 1250:     */   }
/* 1251:     */   
/* 1252:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(K k0, V v0)
/* 1253:     */   {
/* 1254:1279 */     NavigableMap<K, V> map = new ConcurrentSkipListMap();
/* 1255:1280 */     map.put(k0, v0);
/* 1256:1281 */     return map;
/* 1257:     */   }
/* 1258:     */   
/* 1259:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(K k0, V v0, K k1, V v1)
/* 1260:     */   {
/* 1261:1285 */     NavigableMap<K, V> map = new ConcurrentSkipListMap();
/* 1262:1286 */     map.put(k0, v0);
/* 1263:1287 */     map.put(k1, v1);
/* 1264:1288 */     return map;
/* 1265:     */   }
/* 1266:     */   
/* 1267:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(K k0, V v0, K k1, V v1, K k2, V v2)
/* 1268:     */   {
/* 1269:1293 */     NavigableMap<K, V> map = new ConcurrentSkipListMap();
/* 1270:1294 */     map.put(k0, v0);
/* 1271:1295 */     map.put(k1, v1);
/* 1272:1296 */     map.put(k2, v2);
/* 1273:1297 */     return map;
/* 1274:     */   }
/* 1275:     */   
/* 1276:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3)
/* 1277:     */   {
/* 1278:1302 */     NavigableMap<K, V> map = new ConcurrentSkipListMap();
/* 1279:1303 */     map.put(k0, v0);
/* 1280:1304 */     map.put(k1, v1);
/* 1281:1305 */     map.put(k2, v2);
/* 1282:1306 */     map.put(k3, v3);
/* 1283:1307 */     return map;
/* 1284:     */   }
/* 1285:     */   
/* 1286:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4)
/* 1287:     */   {
/* 1288:1312 */     NavigableMap<K, V> map = new ConcurrentSkipListMap();
/* 1289:1313 */     map.put(k0, v0);
/* 1290:1314 */     map.put(k1, v1);
/* 1291:1315 */     map.put(k2, v2);
/* 1292:1316 */     map.put(k3, v3);
/* 1293:1317 */     map.put(k4, v4);
/* 1294:1318 */     return map;
/* 1295:     */   }
/* 1296:     */   
/* 1297:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5)
/* 1298:     */   {
/* 1299:1323 */     NavigableMap<K, V> map = new ConcurrentSkipListMap();
/* 1300:1324 */     map.put(k0, v0);
/* 1301:1325 */     map.put(k1, v1);
/* 1302:1326 */     map.put(k2, v2);
/* 1303:1327 */     map.put(k3, v3);
/* 1304:1328 */     map.put(k4, v4);
/* 1305:1329 */     map.put(k5, v5);
/* 1306:1330 */     return map;
/* 1307:     */   }
/* 1308:     */   
/* 1309:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6)
/* 1310:     */   {
/* 1311:1335 */     NavigableMap<K, V> map = new ConcurrentSkipListMap();
/* 1312:1336 */     map.put(k0, v0);
/* 1313:1337 */     map.put(k1, v1);
/* 1314:1338 */     map.put(k2, v2);
/* 1315:1339 */     map.put(k3, v3);
/* 1316:1340 */     map.put(k4, v4);
/* 1317:1341 */     map.put(k5, v5);
/* 1318:1342 */     map.put(k6, v6);
/* 1319:1343 */     return map;
/* 1320:     */   }
/* 1321:     */   
/* 1322:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7)
/* 1323:     */   {
/* 1324:1348 */     NavigableMap<K, V> map = new ConcurrentSkipListMap();
/* 1325:1349 */     map.put(k0, v0);
/* 1326:1350 */     map.put(k1, v1);
/* 1327:1351 */     map.put(k2, v2);
/* 1328:1352 */     map.put(k3, v3);
/* 1329:1353 */     map.put(k4, v4);
/* 1330:1354 */     map.put(k5, v5);
/* 1331:1355 */     map.put(k6, v6);
/* 1332:1356 */     map.put(k7, v7);
/* 1333:1357 */     return map;
/* 1334:     */   }
/* 1335:     */   
/* 1336:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8)
/* 1337:     */   {
/* 1338:1362 */     NavigableMap<K, V> map = new ConcurrentSkipListMap();
/* 1339:1363 */     map.put(k0, v0);
/* 1340:1364 */     map.put(k1, v1);
/* 1341:1365 */     map.put(k2, v2);
/* 1342:1366 */     map.put(k3, v3);
/* 1343:1367 */     map.put(k4, v4);
/* 1344:1368 */     map.put(k5, v5);
/* 1345:1369 */     map.put(k6, v6);
/* 1346:1370 */     map.put(k7, v7);
/* 1347:1371 */     map.put(k8, v8);
/* 1348:1372 */     return map;
/* 1349:     */   }
/* 1350:     */   
/* 1351:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9)
/* 1352:     */   {
/* 1353:1378 */     NavigableMap<K, V> map = new ConcurrentSkipListMap();
/* 1354:1379 */     map.put(k0, v0);
/* 1355:1380 */     map.put(k1, v1);
/* 1356:1381 */     map.put(k2, v2);
/* 1357:1382 */     map.put(k3, v3);
/* 1358:1383 */     map.put(k4, v4);
/* 1359:1384 */     map.put(k5, v5);
/* 1360:1385 */     map.put(k6, v6);
/* 1361:1386 */     map.put(k7, v7);
/* 1362:1387 */     map.put(k8, v8);
/* 1363:1388 */     map.put(k9, v9);
/* 1364:1389 */     return map;
/* 1365:     */   }
/* 1366:     */   
/* 1367:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(Collection<K> keys, Collection<V> values)
/* 1368:     */   {
/* 1369:1393 */     NavigableMap<K, V> map = new ConcurrentSkipListMap();
/* 1370:1394 */     Iterator<V> iterator = values.iterator();
/* 1371:1395 */     for (K k : keys) {
/* 1372:1396 */       if (iterator.hasNext())
/* 1373:     */       {
/* 1374:1397 */         V v = iterator.next();
/* 1375:1398 */         map.put(k, v);
/* 1376:     */       }
/* 1377:     */       else
/* 1378:     */       {
/* 1379:1400 */         map.put(k, null);
/* 1380:     */       }
/* 1381:     */     }
/* 1382:1403 */     return map;
/* 1383:     */   }
/* 1384:     */   
/* 1385:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(Iterable<K> keys, Iterable<V> values)
/* 1386:     */   {
/* 1387:1407 */     NavigableMap<K, V> map = new ConcurrentSkipListMap();
/* 1388:1408 */     Iterator<V> iterator = values.iterator();
/* 1389:1409 */     for (K k : keys) {
/* 1390:1410 */       if (iterator.hasNext())
/* 1391:     */       {
/* 1392:1411 */         V v = iterator.next();
/* 1393:1412 */         map.put(k, v);
/* 1394:     */       }
/* 1395:     */       else
/* 1396:     */       {
/* 1397:1414 */         map.put(k, null);
/* 1398:     */       }
/* 1399:     */     }
/* 1400:1417 */     return map;
/* 1401:     */   }
/* 1402:     */   
/* 1403:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(K[] keys, V[] values)
/* 1404:     */   {
/* 1405:1422 */     NavigableMap<K, V> map = new ConcurrentSkipListMap();
/* 1406:1423 */     int index = 0;
/* 1407:1424 */     for (K k : keys)
/* 1408:     */     {
/* 1409:1425 */       if (index < keys.length)
/* 1410:     */       {
/* 1411:1426 */         V v = values[index];
/* 1412:1427 */         map.put(k, v);
/* 1413:     */       }
/* 1414:     */       else
/* 1415:     */       {
/* 1416:1429 */         map.put(k, null);
/* 1417:     */       }
/* 1418:1431 */       index++;
/* 1419:     */     }
/* 1420:1433 */     return map;
/* 1421:     */   }
/* 1422:     */   
/* 1423:     */   @SafeVarargs
/* 1424:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(Entry<K, V>... entries)
/* 1425:     */   {
/* 1426:1439 */     NavigableMap<K, V> map = new ConcurrentSkipListMap();
/* 1427:1440 */     for (Entry<K, V> entry : entries) {
/* 1428:1441 */       map.put(entry.key(), entry.value());
/* 1429:     */     }
/* 1430:1443 */     return map;
/* 1431:     */   }
/* 1432:     */   
/* 1433:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(Comparator<K> comparator, K k0, V v0)
/* 1434:     */   {
/* 1435:1448 */     NavigableMap<K, V> map = new ConcurrentSkipListMap(comparator);
/* 1436:1449 */     map.put(k0, v0);
/* 1437:1450 */     return map;
/* 1438:     */   }
/* 1439:     */   
/* 1440:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(Comparator<K> comparator, K k0, V v0, K k1, V v1)
/* 1441:     */   {
/* 1442:1454 */     NavigableMap<K, V> map = new ConcurrentSkipListMap(comparator);
/* 1443:1455 */     map.put(k0, v0);
/* 1444:1456 */     map.put(k1, v1);
/* 1445:1457 */     return map;
/* 1446:     */   }
/* 1447:     */   
/* 1448:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(Comparator<K> comparator, K k0, V v0, K k1, V v1, K k2, V v2)
/* 1449:     */   {
/* 1450:1461 */     NavigableMap<K, V> map = new ConcurrentSkipListMap(comparator);
/* 1451:1462 */     map.put(k0, v0);
/* 1452:1463 */     map.put(k1, v1);
/* 1453:1464 */     map.put(k2, v2);
/* 1454:1465 */     return map;
/* 1455:     */   }
/* 1456:     */   
/* 1457:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(Comparator<K> comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3)
/* 1458:     */   {
/* 1459:1470 */     NavigableMap<K, V> map = new ConcurrentSkipListMap(comparator);
/* 1460:1471 */     map.put(k0, v0);
/* 1461:1472 */     map.put(k1, v1);
/* 1462:1473 */     map.put(k2, v2);
/* 1463:1474 */     map.put(k3, v3);
/* 1464:1475 */     return map;
/* 1465:     */   }
/* 1466:     */   
/* 1467:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(Comparator<K> comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4)
/* 1468:     */   {
/* 1469:1480 */     NavigableMap<K, V> map = new ConcurrentSkipListMap(comparator);
/* 1470:1481 */     map.put(k0, v0);
/* 1471:1482 */     map.put(k1, v1);
/* 1472:1483 */     map.put(k2, v2);
/* 1473:1484 */     map.put(k3, v3);
/* 1474:1485 */     map.put(k4, v4);
/* 1475:1486 */     return map;
/* 1476:     */   }
/* 1477:     */   
/* 1478:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(Comparator<K> comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5)
/* 1479:     */   {
/* 1480:1491 */     NavigableMap<K, V> map = new ConcurrentSkipListMap(comparator);
/* 1481:1492 */     map.put(k0, v0);
/* 1482:1493 */     map.put(k1, v1);
/* 1483:1494 */     map.put(k2, v2);
/* 1484:1495 */     map.put(k3, v3);
/* 1485:1496 */     map.put(k4, v4);
/* 1486:1497 */     map.put(k5, v5);
/* 1487:1498 */     return map;
/* 1488:     */   }
/* 1489:     */   
/* 1490:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(Comparator<K> comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6)
/* 1491:     */   {
/* 1492:1503 */     NavigableMap<K, V> map = new ConcurrentSkipListMap(comparator);
/* 1493:1504 */     map.put(k0, v0);
/* 1494:1505 */     map.put(k1, v1);
/* 1495:1506 */     map.put(k2, v2);
/* 1496:1507 */     map.put(k3, v3);
/* 1497:1508 */     map.put(k4, v4);
/* 1498:1509 */     map.put(k5, v5);
/* 1499:1510 */     map.put(k6, v6);
/* 1500:1511 */     return map;
/* 1501:     */   }
/* 1502:     */   
/* 1503:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(Comparator<K> comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7)
/* 1504:     */   {
/* 1505:1516 */     NavigableMap<K, V> map = new ConcurrentSkipListMap(comparator);
/* 1506:1517 */     map.put(k0, v0);
/* 1507:1518 */     map.put(k1, v1);
/* 1508:1519 */     map.put(k2, v2);
/* 1509:1520 */     map.put(k3, v3);
/* 1510:1521 */     map.put(k4, v4);
/* 1511:1522 */     map.put(k5, v5);
/* 1512:1523 */     map.put(k6, v6);
/* 1513:1524 */     map.put(k7, v7);
/* 1514:1525 */     return map;
/* 1515:     */   }
/* 1516:     */   
/* 1517:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(Comparator<K> comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8)
/* 1518:     */   {
/* 1519:1530 */     NavigableMap<K, V> map = new ConcurrentSkipListMap(comparator);
/* 1520:1531 */     map.put(k0, v0);
/* 1521:1532 */     map.put(k1, v1);
/* 1522:1533 */     map.put(k2, v2);
/* 1523:1534 */     map.put(k3, v3);
/* 1524:1535 */     map.put(k4, v4);
/* 1525:1536 */     map.put(k5, v5);
/* 1526:1537 */     map.put(k6, v6);
/* 1527:1538 */     map.put(k7, v7);
/* 1528:1539 */     map.put(k8, v8);
/* 1529:1540 */     return map;
/* 1530:     */   }
/* 1531:     */   
/* 1532:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(Comparator<K> comparator, K k0, V v0, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9)
/* 1533:     */   {
/* 1534:1546 */     NavigableMap<K, V> map = new ConcurrentSkipListMap(comparator);
/* 1535:1547 */     map.put(k0, v0);
/* 1536:1548 */     map.put(k1, v1);
/* 1537:1549 */     map.put(k2, v2);
/* 1538:1550 */     map.put(k3, v3);
/* 1539:1551 */     map.put(k4, v4);
/* 1540:1552 */     map.put(k5, v5);
/* 1541:1553 */     map.put(k6, v6);
/* 1542:1554 */     map.put(k7, v7);
/* 1543:1555 */     map.put(k8, v8);
/* 1544:1556 */     map.put(k9, v9);
/* 1545:1557 */     return map;
/* 1546:     */   }
/* 1547:     */   
/* 1548:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(Comparator<K> comparator, Collection<K> keys, Collection<V> values)
/* 1549:     */   {
/* 1550:1561 */     NavigableMap<K, V> map = new ConcurrentSkipListMap(comparator);
/* 1551:1562 */     Iterator<V> iterator = values.iterator();
/* 1552:1563 */     for (K k : keys) {
/* 1553:1564 */       if (iterator.hasNext())
/* 1554:     */       {
/* 1555:1565 */         V v = iterator.next();
/* 1556:1566 */         map.put(k, v);
/* 1557:     */       }
/* 1558:     */       else
/* 1559:     */       {
/* 1560:1568 */         map.put(k, null);
/* 1561:     */       }
/* 1562:     */     }
/* 1563:1571 */     return map;
/* 1564:     */   }
/* 1565:     */   
/* 1566:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(Comparator<K> comparator, K[] keys, V[] values)
/* 1567:     */   {
/* 1568:1576 */     NavigableMap<K, V> map = new ConcurrentSkipListMap(comparator);
/* 1569:1577 */     int index = 0;
/* 1570:1578 */     for (K k : keys)
/* 1571:     */     {
/* 1572:1579 */       if (index < keys.length)
/* 1573:     */       {
/* 1574:1580 */         V v = values[index];
/* 1575:1581 */         map.put(k, v);
/* 1576:     */       }
/* 1577:     */       else
/* 1578:     */       {
/* 1579:1583 */         map.put(k, null);
/* 1580:     */       }
/* 1581:1585 */       index++;
/* 1582:     */     }
/* 1583:1587 */     return map;
/* 1584:     */   }
/* 1585:     */   
/* 1586:     */   public static <K, V> NavigableMap<K, V> safeSortedMap(Comparator<K> comparator, List<Entry<K, V>> entries)
/* 1587:     */   {
/* 1588:1592 */     NavigableMap<K, V> map = new ConcurrentSkipListMap(comparator);
/* 1589:1593 */     for (Entry<K, V> entry : entries) {
/* 1590:1594 */       map.put(entry.key(), entry.value());
/* 1591:     */     }
/* 1592:1596 */     return map;
/* 1593:     */   }
/* 1594:     */   
/* 1595:     */   public static <T> T idx(Class<T> clz, Map map, Object key)
/* 1596:     */   {
/* 1597:1602 */     Object value = map.get(key.toString());
/* 1598:1603 */     if (value == null) {
/* 1599:1604 */       return value;
/* 1600:     */     }
/* 1601:1606 */     if (value.getClass() != clz)
/* 1602:     */     {
/* 1603:1607 */       T t = Conversions.coerce(clz, value);
/* 1604:1608 */       return t;
/* 1605:     */     }
/* 1606:1610 */     return value;
/* 1607:     */   }
/* 1608:     */   
/* 1609:     */   public static <T> T fromMap(Map<String, Object> map, Class<T> clazz)
/* 1610:     */   {
/* 1611:1616 */     return MapObjectConversion.fromMap(map, clazz);
/* 1612:     */   }
/* 1613:     */   
/* 1614:     */   public static Object fromMap(Map<String, Object> map)
/* 1615:     */   {
/* 1616:1620 */     return MapObjectConversion.fromMap(map);
/* 1617:     */   }
/* 1618:     */   
/* 1619:     */   public static Map<String, Object> toMap(Object object)
/* 1620:     */   {
/* 1621:1625 */     return MapObjectConversion.toMap(object);
/* 1622:     */   }
/* 1623:     */   
/* 1624:     */   public static <T> Map<String, List<T>> toMultiValueMap(String propertyPath, Collection<T> collection)
/* 1625:     */   {
/* 1626:1630 */     LinkedHashMap<String, List<T>> map = new LinkedHashMap(collection.size());
/* 1627:1632 */     for (T item : collection)
/* 1628:     */     {
/* 1629:1633 */       Object oKey = BeanUtils.idx(item, propertyPath);
/* 1630:1634 */       if (oKey != null)
/* 1631:     */       {
/* 1632:1637 */         String key = (String)Conversions.coerce(Typ.string, oKey);
/* 1633:     */         
/* 1634:1639 */         List<T> list = (List)map.get(key);
/* 1635:1640 */         if (list == null)
/* 1636:     */         {
/* 1637:1641 */           list = new ArrayList();
/* 1638:1642 */           map.put(key, list);
/* 1639:     */         }
/* 1640:1644 */         list.add(item);
/* 1641:     */       }
/* 1642:     */     }
/* 1643:1647 */     return map;
/* 1644:     */   }
/* 1645:     */   
/* 1646:     */   public static <T> Map<String, T> toMap(String propertyPath, Collection<T> collection)
/* 1647:     */   {
/* 1648:1653 */     return toMap(Typ.string, propertyPath, collection);
/* 1649:     */   }
/* 1650:     */   
/* 1651:     */   public static <T> NavigableMap<String, T> toSortedMap(String propertyPath, Collection<T> collection)
/* 1652:     */   {
/* 1653:1657 */     return toSortedMap(Typ.string, propertyPath, collection);
/* 1654:     */   }
/* 1655:     */   
/* 1656:     */   public static <T> NavigableMap<String, T> toSafeSortedMap(String propertyPath, Collection<T> collection)
/* 1657:     */   {
/* 1658:1661 */     return toSafeSortedMap(Typ.string, propertyPath, collection);
/* 1659:     */   }
/* 1660:     */   
/* 1661:     */   public static <T> Map<String, T> toSafeMap(String propertyPath, Collection<T> collection)
/* 1662:     */   {
/* 1663:1665 */     return toSafeMap(Typ.string, propertyPath, collection);
/* 1664:     */   }
/* 1665:     */   
/* 1666:     */   public static <K, T> Map<K, T> toMap(Class<K> keyType, String propertyPath, Collection<T> collection)
/* 1667:     */   {
/* 1668:1670 */     LinkedHashMap<K, T> map = new LinkedHashMap(collection.size());
/* 1669:1671 */     doPopulateMapWithCollectionAndPropPath(keyType, propertyPath, collection, map);
/* 1670:1672 */     return map;
/* 1671:     */   }
/* 1672:     */   
/* 1673:     */   public static <K, T> NavigableMap<K, T> toSortedMap(Class<K> keyType, String propertyPath, Collection<T> collection)
/* 1674:     */   {
/* 1675:1676 */     TreeMap<K, T> map = new TreeMap();
/* 1676:1677 */     doPopulateMapWithCollectionAndPropPath(keyType, propertyPath, collection, map);
/* 1677:1678 */     return map;
/* 1678:     */   }
/* 1679:     */   
/* 1680:     */   public static <K, T> NavigableMap<K, T> toSafeSortedMap(Class<K> keyType, String propertyPath, Collection<T> collection)
/* 1681:     */   {
/* 1682:1682 */     ConcurrentSkipListMap<K, T> map = new ConcurrentSkipListMap();
/* 1683:1683 */     doPopulateMapWithCollectionAndPropPath(keyType, propertyPath, collection, map);
/* 1684:1684 */     return map;
/* 1685:     */   }
/* 1686:     */   
/* 1687:     */   public static <K, T> Map<K, T> toSafeMap(Class<K> keyType, String propertyPath, Collection<T> collection)
/* 1688:     */   {
/* 1689:1688 */     ConcurrentHashMap<K, T> map = new ConcurrentHashMap();
/* 1690:1689 */     doPopulateMapWithCollectionAndPropPath(keyType, propertyPath, collection, map);
/* 1691:1690 */     return map;
/* 1692:     */   }
/* 1693:     */   
/* 1694:     */   private static <K, T> void doPopulateMapWithCollectionAndPropPath(Class<K> keyType, String propertyPath, Collection<T> collection, Map<K, T> map)
/* 1695:     */   {
/* 1696:1695 */     for (T item : collection)
/* 1697:     */     {
/* 1698:1696 */       Object oKey = BeanUtils.idx(item, propertyPath);
/* 1699:1697 */       if (oKey != null)
/* 1700:     */       {
/* 1701:1700 */         K key = Conversions.coerce(keyType, oKey);
/* 1702:1701 */         map.put(key, item);
/* 1703:     */       }
/* 1704:     */     }
/* 1705:     */   }
/* 1706:     */   
/* 1707:     */   public static <K, V> void copyKeys(Collection<K> keys, Map<K, V> sourceMap, Map<K, V> destinationMap)
/* 1708:     */   {
/* 1709:1708 */     for (K key : keys)
/* 1710:     */     {
/* 1711:1709 */       V value = sourceMap.get(key);
/* 1712:1710 */       if (value != null) {
/* 1713:1711 */         destinationMap.put(key, value);
/* 1714:     */       }
/* 1715:     */     }
/* 1716:     */   }
/* 1717:     */   
/* 1718:     */   public static <K, V> Map<K, V> copyKeys(Collection<K> keys, Map<K, V> sourceMap)
/* 1719:     */   {
/* 1720:1717 */     Map<K, V> destinationMap = new ConcurrentHashMap();
/* 1721:1718 */     for (K key : keys)
/* 1722:     */     {
/* 1723:1719 */       V value = sourceMap.get(key);
/* 1724:1720 */       if (value != null) {
/* 1725:1721 */         destinationMap.put(key, value);
/* 1726:     */       }
/* 1727:     */     }
/* 1728:1724 */     return destinationMap;
/* 1729:     */   }
/* 1730:     */   
/* 1731:     */   public static String asPrettyJsonString(Map<String, Object> map)
/* 1732:     */   {
/* 1733:1729 */     CharBuf buf = CharBuf.createCharBuf();
/* 1734:1730 */     return buf.prettyPrintMap(map).toString();
/* 1735:     */   }
/* 1736:     */   
/* 1737:     */   public static Map<String, Object> toPrettyMap(Object object)
/* 1738:     */   {
/* 1739:1734 */     return MapObjectConversion.toPrettyMap(object);
/* 1740:     */   }
/* 1741:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.Maps
 * JD-Core Version:    0.7.0.1
 */