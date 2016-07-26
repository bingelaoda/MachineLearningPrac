/*   1:    */ package org.boon.datarepo.impl;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collection;
/*   5:    */ import java.util.Iterator;
/*   6:    */ import java.util.LinkedHashMap;
/*   7:    */ import java.util.LinkedHashSet;
/*   8:    */ import java.util.List;
/*   9:    */ import java.util.Map;
/*  10:    */ import java.util.Set;
/*  11:    */ import java.util.logging.Logger;
/*  12:    */ import org.boon.Boon;
/*  13:    */ import org.boon.Exceptions;
/*  14:    */ import org.boon.Lists;
/*  15:    */ import org.boon.core.Conversions;
/*  16:    */ import org.boon.core.Function;
/*  17:    */ import org.boon.core.reflection.MapObjectConversion;
/*  18:    */ import org.boon.core.reflection.Reflection;
/*  19:    */ import org.boon.core.reflection.fields.FieldAccess;
/*  20:    */ import org.boon.criteria.Selector;
/*  21:    */ import org.boon.criteria.internal.Criteria;
/*  22:    */ import org.boon.criteria.internal.Visitor;
/*  23:    */ import org.boon.datarepo.Filter;
/*  24:    */ import org.boon.datarepo.LookupIndex;
/*  25:    */ import org.boon.datarepo.ResultSet;
/*  26:    */ import org.boon.datarepo.SearchableCollection;
/*  27:    */ import org.boon.datarepo.impl.indexes.UniqueLookupIndex;
/*  28:    */ import org.boon.datarepo.spi.FilterComposer;
/*  29:    */ import org.boon.datarepo.spi.SearchIndex;
/*  30:    */ import org.boon.datarepo.spi.SearchableCollectionComposer;
/*  31:    */ import org.boon.sort.Sort;
/*  32:    */ 
/*  33:    */ public class SearchableCollectionDefault<KEY, ITEM>
/*  34:    */   implements SearchableCollection<KEY, ITEM>, SearchableCollectionComposer
/*  35:    */ {
/*  36: 60 */   private Logger log = Logger.getLogger(RepoDefault.class.getName());
/*  37: 62 */   protected Map<String, LookupIndex> lookupIndexMap = new LinkedHashMap();
/*  38: 63 */   protected Map<String, SearchIndex> searchIndexMap = new LinkedHashMap();
/*  39: 64 */   protected Set<LookupIndex> indexes = new LinkedHashSet();
/*  40:    */   protected Filter filter;
/*  41: 67 */   protected Map<String, FieldAccess> fields = new LinkedHashMap();
/*  42:    */   protected UniqueLookupIndex<KEY, ITEM> primaryIndex;
/*  43:    */   protected Function<ITEM, KEY> primaryKeyGetter;
/*  44:    */   protected String primaryKeyName;
/*  45: 74 */   protected boolean removeDuplication = true;
/*  46:    */   
/*  47:    */   public boolean delete(ITEM item)
/*  48:    */   {
/*  49: 79 */     for (LookupIndex index : this.indexes) {
/*  50: 80 */       index.delete(item);
/*  51:    */     }
/*  52: 82 */     return true;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public boolean add(ITEM item)
/*  56:    */   {
/*  57: 87 */     Exceptions.requireNonNull(item, "No nulls allowed in repo");
/*  58:    */     
/*  59: 89 */     KEY key = getKey(item);
/*  60: 90 */     if (this.primaryIndex.has(key)) {
/*  61: 91 */       return false;
/*  62:    */     }
/*  63: 94 */     validateIndexes(item);
/*  64: 95 */     return true;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void validateIndexes(ITEM item)
/*  68:    */   {
/*  69: 99 */     for (LookupIndex index : this.indexes) {
/*  70:100 */       index.add(item);
/*  71:    */     }
/*  72:    */   }
/*  73:    */   
/*  74:    */   public ITEM get(KEY key)
/*  75:    */   {
/*  76:106 */     LookupIndex lookupIndex = this.primaryIndex;
/*  77:107 */     return lookupIndex.get(key);
/*  78:    */   }
/*  79:    */   
/*  80:    */   public KEY getKey(ITEM item)
/*  81:    */   {
/*  82:111 */     return this.primaryKeyGetter.apply(item);
/*  83:    */   }
/*  84:    */   
/*  85:    */   public void setRemoveDuplication(boolean removeDuplication)
/*  86:    */   {
/*  87:116 */     this.removeDuplication = removeDuplication;
/*  88:    */   }
/*  89:    */   
/*  90:    */   public int count(KEY key, String property, int value)
/*  91:    */   {
/*  92:122 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/*  93:124 */     if (index == null) {
/*  94:125 */       throw new IllegalStateException(String.format("No searchIndex was found so you can't do a count for \n key %s \t property %s \t set %s", new Object[] { key, property, Integer.valueOf(value) }));
/*  95:    */     }
/*  96:131 */     return index.count(key);
/*  97:    */   }
/*  98:    */   
/*  99:    */   public int count(KEY key, String property, short value)
/* 100:    */   {
/* 101:137 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/* 102:139 */     if (index == null) {
/* 103:140 */       throw new IllegalStateException(String.format("No searchIndex was found so you can't do a count for \n key %s \t property %s \t set %s", new Object[] { key, property, Short.valueOf(value) }));
/* 104:    */     }
/* 105:144 */     return index.count(key);
/* 106:    */   }
/* 107:    */   
/* 108:    */   public int count(KEY key, String property, byte value)
/* 109:    */   {
/* 110:149 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/* 111:151 */     if (index == null) {
/* 112:152 */       throw new IllegalStateException(String.format("No searchIndex was found so you can't do a count for \n key %s \t property %s \t set %s", new Object[] { key, property, Byte.valueOf(value) }));
/* 113:    */     }
/* 114:156 */     return index.count(key);
/* 115:    */   }
/* 116:    */   
/* 117:    */   public int count(KEY key, String property, long value)
/* 118:    */   {
/* 119:161 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/* 120:163 */     if (index == null) {
/* 121:164 */       throw new IllegalStateException(String.format("No searchIndex was found so you can't do a count for \n key %s \t property %s \t set %s", new Object[] { key, property, Long.valueOf(value) }));
/* 122:    */     }
/* 123:169 */     return index.count(key);
/* 124:    */   }
/* 125:    */   
/* 126:    */   public int count(KEY key, String property, char value)
/* 127:    */   {
/* 128:174 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/* 129:176 */     if (index == null) {
/* 130:177 */       throw new IllegalStateException(String.format("No searchIndex was found so you can't do a count for \n key %s \t property %s \t set %s", new Object[] { key, property, Character.valueOf(value) }));
/* 131:    */     }
/* 132:182 */     return index.count(key);
/* 133:    */   }
/* 134:    */   
/* 135:    */   public int count(KEY key, String property, float value)
/* 136:    */   {
/* 137:187 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/* 138:189 */     if (index == null) {
/* 139:190 */       throw new IllegalStateException(String.format("No searchIndex was found so you can't do a count for \n key %s \t property %s \t set %s", new Object[] { key, property, Float.valueOf(value) }));
/* 140:    */     }
/* 141:195 */     return index.count(key);
/* 142:    */   }
/* 143:    */   
/* 144:    */   public int count(KEY key, String property, double value)
/* 145:    */   {
/* 146:200 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/* 147:202 */     if (index == null) {
/* 148:203 */       throw new IllegalStateException(String.format("No searchIndex was found so you can't do a count for \n key %s \t property %s \t set %s", new Object[] { key, property, Double.valueOf(value) }));
/* 149:    */     }
/* 150:208 */     return index.count(key);
/* 151:    */   }
/* 152:    */   
/* 153:    */   public int count(KEY key, String property, Object value)
/* 154:    */   {
/* 155:213 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/* 156:215 */     if (index == null) {
/* 157:216 */       throw new IllegalStateException(String.format("No searchIndex was found so you can't do a count for \n key %s \t property %s \t set %s", new Object[] { key, property, value }));
/* 158:    */     }
/* 159:221 */     return index.count(key);
/* 160:    */   }
/* 161:    */   
/* 162:    */   public <T> T max(KEY key, String property, Class<T> type)
/* 163:    */   {
/* 164:226 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/* 165:227 */     if (index != null)
/* 166:    */     {
/* 167:228 */       ITEM item = index.max();
/* 168:229 */       if (item != null) {
/* 169:230 */         return ((FieldAccess)this.fields.get(property)).getValue(item);
/* 170:    */       }
/* 171:    */     }
/* 172:233 */     return null;
/* 173:    */   }
/* 174:    */   
/* 175:    */   public String maxString(KEY key, String property)
/* 176:    */   {
/* 177:238 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/* 178:239 */     if (index != null)
/* 179:    */     {
/* 180:240 */       ITEM item = index.max();
/* 181:241 */       if (item != null) {
/* 182:242 */         return (String)((FieldAccess)this.fields.get(property)).getObject(item);
/* 183:    */       }
/* 184:    */     }
/* 185:245 */     return null;
/* 186:    */   }
/* 187:    */   
/* 188:    */   public Number maxNumber(KEY key, String property)
/* 189:    */   {
/* 190:250 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/* 191:251 */     if (index != null)
/* 192:    */     {
/* 193:252 */       ITEM item = index.max();
/* 194:253 */       if (item != null) {
/* 195:254 */         return (Number)((FieldAccess)this.fields.get(property)).getValue(item);
/* 196:    */       }
/* 197:    */     }
/* 198:257 */     return Double.valueOf((0.0D / 0.0D));
/* 199:    */   }
/* 200:    */   
/* 201:    */   public int maxInt(KEY key, String property)
/* 202:    */   {
/* 203:262 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/* 204:263 */     if (index != null)
/* 205:    */     {
/* 206:264 */       ITEM item = index.max();
/* 207:265 */       if (item != null) {
/* 208:266 */         return ((FieldAccess)this.fields.get(property)).getInt(item);
/* 209:    */       }
/* 210:    */     }
/* 211:269 */     return -2147483648;
/* 212:    */   }
/* 213:    */   
/* 214:    */   public long maxLong(KEY key, String property)
/* 215:    */   {
/* 216:274 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/* 217:275 */     if (index != null)
/* 218:    */     {
/* 219:276 */       ITEM item = index.max();
/* 220:277 */       if (item != null)
/* 221:    */       {
/* 222:278 */         FieldAccess field = (FieldAccess)this.fields.get(property);
/* 223:280 */         if (field.type() == Long.TYPE) {
/* 224:281 */           return field.getLong(item);
/* 225:    */         }
/* 226:    */       }
/* 227:    */     }
/* 228:285 */     return -9223372036854775808L;
/* 229:    */   }
/* 230:    */   
/* 231:    */   public double maxDouble(KEY key, String property)
/* 232:    */   {
/* 233:290 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/* 234:291 */     if (index != null)
/* 235:    */     {
/* 236:292 */       ITEM item = index.max();
/* 237:293 */       if (item != null) {
/* 238:294 */         return ((FieldAccess)this.fields.get(property)).getDouble(item);
/* 239:    */       }
/* 240:    */     }
/* 241:297 */     return -2147483648.0D;
/* 242:    */   }
/* 243:    */   
/* 244:    */   public <T> T min(KEY key, String property, Class<T> type)
/* 245:    */   {
/* 246:302 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/* 247:303 */     if (index != null)
/* 248:    */     {
/* 249:304 */       ITEM item = index.min();
/* 250:305 */       if (item != null) {
/* 251:306 */         return ((FieldAccess)this.fields.get(property)).getValue(item);
/* 252:    */       }
/* 253:    */     }
/* 254:309 */     return null;
/* 255:    */   }
/* 256:    */   
/* 257:    */   public String minString(KEY key, String property)
/* 258:    */   {
/* 259:314 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/* 260:315 */     if (index != null)
/* 261:    */     {
/* 262:316 */       ITEM item = index.min();
/* 263:317 */       if (item != null) {
/* 264:318 */         return (String)((FieldAccess)this.fields.get(property)).getObject(item);
/* 265:    */       }
/* 266:    */     }
/* 267:321 */     return "";
/* 268:    */   }
/* 269:    */   
/* 270:    */   public Number minNumber(KEY key, String property)
/* 271:    */   {
/* 272:326 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/* 273:327 */     if (index != null)
/* 274:    */     {
/* 275:328 */       ITEM item = index.min();
/* 276:329 */       if (item != null) {
/* 277:330 */         return (Number)((FieldAccess)this.fields.get(property)).getValue(item);
/* 278:    */       }
/* 279:    */     }
/* 280:333 */     return Double.valueOf((0.0D / 0.0D));
/* 281:    */   }
/* 282:    */   
/* 283:    */   public int minInt(KEY key, String property)
/* 284:    */   {
/* 285:338 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/* 286:339 */     if (index != null)
/* 287:    */     {
/* 288:340 */       ITEM item = index.min();
/* 289:341 */       if (item != null) {
/* 290:342 */         return ((FieldAccess)this.fields.get(property)).getInt(item);
/* 291:    */       }
/* 292:    */     }
/* 293:345 */     return 2147483647;
/* 294:    */   }
/* 295:    */   
/* 296:    */   public long minLong(KEY key, String property)
/* 297:    */   {
/* 298:350 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/* 299:351 */     if (index != null)
/* 300:    */     {
/* 301:352 */       ITEM item = index.min();
/* 302:353 */       if (item != null)
/* 303:    */       {
/* 304:354 */         FieldAccess field = (FieldAccess)this.fields.get(property);
/* 305:356 */         if (field.type() == Long.TYPE) {
/* 306:357 */           return field.getLong(item);
/* 307:    */         }
/* 308:    */       }
/* 309:    */     }
/* 310:361 */     return 9223372036854775807L;
/* 311:    */   }
/* 312:    */   
/* 313:    */   public double minDouble(KEY key, String property)
/* 314:    */   {
/* 315:366 */     SearchIndex index = (SearchIndex)this.searchIndexMap.get(property);
/* 316:367 */     if (index != null)
/* 317:    */     {
/* 318:368 */       ITEM item = index.min();
/* 319:369 */       if (item != null) {
/* 320:370 */         return ((FieldAccess)this.fields.get(property)).getDouble(item);
/* 321:    */       }
/* 322:    */     }
/* 323:373 */     return 1.7976931348623157E+308D;
/* 324:    */   }
/* 325:    */   
/* 326:    */   public ResultSet<ITEM> results(Criteria... expressions)
/* 327:    */   {
/* 328:378 */     return this.filter.filter(expressions);
/* 329:    */   }
/* 330:    */   
/* 331:    */   public List<ITEM> query(Criteria... expressions)
/* 332:    */   {
/* 333:383 */     if ((expressions == null) || (expressions.length == 0)) {
/* 334:384 */       return all();
/* 335:    */     }
/* 336:386 */     if (this.removeDuplication) {
/* 337:387 */       return this.filter.filter(expressions).removeDuplication().asList();
/* 338:    */     }
/* 339:389 */     return this.filter.filter(expressions).asList();
/* 340:    */   }
/* 341:    */   
/* 342:    */   public List<ITEM> query(List<Criteria> expressions)
/* 343:    */   {
/* 344:396 */     return query((Criteria[])Conversions.toArray(Criteria.class, expressions));
/* 345:    */   }
/* 346:    */   
/* 347:    */   public List<ITEM> sortedQuery(String sortBy, Criteria... expressions)
/* 348:    */   {
/* 349:401 */     Sort asc = Sort.asc(sortBy);
/* 350:402 */     return sortedQuery(asc, expressions);
/* 351:    */   }
/* 352:    */   
/* 353:    */   public List<ITEM> sortedQuery(Sort sortBy, Criteria... expressions)
/* 354:    */   {
/* 355:407 */     List<ITEM> results = query(expressions);
/* 356:408 */     sortBy.sort(results);
/* 357:409 */     return results;
/* 358:    */   }
/* 359:    */   
/* 360:    */   public List<Map<String, Object>> queryAsMaps(Criteria... expressions)
/* 361:    */   {
/* 362:414 */     List<ITEM> items = query(expressions);
/* 363:415 */     List<Map<String, Object>> results = new ArrayList(items.size());
/* 364:416 */     for (ITEM item : items) {
/* 365:417 */       results.add(MapObjectConversion.toMap(item));
/* 366:    */     }
/* 367:419 */     return results;
/* 368:    */   }
/* 369:    */   
/* 370:    */   public List<Map<String, Object>> sortedQuery(String sortBy, List<Selector> selectors, Criteria... expressions)
/* 371:    */   {
/* 372:425 */     Sort asc = Sort.asc(sortBy);
/* 373:426 */     return sortedQuery(asc, selectors, expressions);
/* 374:    */   }
/* 375:    */   
/* 376:    */   public List<Map<String, Object>> sortedQuery(Sort sortBy, List<Selector> selectors, Criteria... expressions)
/* 377:    */   {
/* 378:431 */     List<Map<String, Object>> results = query(selectors, expressions);
/* 379:432 */     sortBy.sort(results);
/* 380:433 */     return results;
/* 381:    */   }
/* 382:    */   
/* 383:    */   private void visit(KEY key, ITEM item, Visitor<KEY, ITEM> visitor, Object o, List<String> path, int levels)
/* 384:    */   {
/* 385:437 */     if (o == null) {
/* 386:438 */       return;
/* 387:    */     }
/* 388:440 */     levels++;
/* 389:441 */     if (levels > 20) {
/* 390:442 */       return;
/* 391:    */     }
/* 392:444 */     visitor.visit(key, item, o, path);
/* 393:447 */     if (o.getClass().isPrimitive()) {
/* 394:448 */       return;
/* 395:    */     }
/* 396:452 */     if (o.getClass().getName().startsWith("java")) {
/* 397:453 */       return;
/* 398:    */     }
/* 399:457 */     if ((Boon.isArray(o)) || ((o instanceof Collection)))
/* 400:    */     {
/* 401:459 */       int index = 0;
/* 402:460 */       Iterator iterator = Boon.iterator(o);
/* 403:461 */       while (iterator.hasNext())
/* 404:    */       {
/* 405:462 */         path.add(String.format("[%s]", new Object[] { Integer.valueOf(index) }));
/* 406:463 */         Object objectItem = iterator.next();
/* 407:464 */         visit(key, item, visitor, objectItem, path, levels);
/* 408:465 */         path.remove(path.size() - 1);
/* 409:466 */         index++;
/* 410:    */       }
/* 411:    */     }
/* 412:472 */     Map<String, FieldAccess> accessorFields = Reflection.getAllAccessorFields(o.getClass());
/* 413:473 */     for (FieldAccess field : accessorFields.values()) {
/* 414:474 */       if (!field.isStatic())
/* 415:    */       {
/* 416:477 */         path.add(field.name());
/* 417:478 */         visit(key, item, visitor, field.getValue(o), path, levels);
/* 418:479 */         path.remove(path.size() - 1);
/* 419:    */       }
/* 420:    */     }
/* 421:    */   }
/* 422:    */   
/* 423:    */   public void query(Visitor<KEY, ITEM> visitor, Criteria... expressions)
/* 424:    */   {
/* 425:488 */     List<ITEM> items = query(expressions);
/* 426:489 */     for (ITEM item : items)
/* 427:    */     {
/* 428:490 */       KEY key = this.primaryKeyGetter.apply(item);
/* 429:491 */       int levels = 0;
/* 430:492 */       visit(key, item, visitor, item, Lists.list(new String[] { "root" }), levels);
/* 431:    */     }
/* 432:    */   }
/* 433:    */   
/* 434:    */   public void sortedQuery(Visitor<KEY, ITEM> visitor, String sortBy, Criteria... expressions)
/* 435:    */   {
/* 436:499 */     Sort asc = Sort.asc(sortBy);
/* 437:500 */     sortedQuery(visitor, asc, expressions);
/* 438:    */   }
/* 439:    */   
/* 440:    */   public void sortedQuery(Visitor<KEY, ITEM> visitor, Sort sortBy, Criteria... expressions)
/* 441:    */   {
/* 442:505 */     List<ITEM> items = sortedQuery(sortBy, expressions);
/* 443:506 */     for (ITEM item : items)
/* 444:    */     {
/* 445:507 */       KEY key = this.primaryKeyGetter.apply(item);
/* 446:508 */       int levels = 0;
/* 447:509 */       visit(key, item, visitor, item, Lists.list(new String[] { "root" }), levels);
/* 448:    */     }
/* 449:    */   }
/* 450:    */   
/* 451:    */   public List<Map<String, Object>> query(List<Selector> selectors, Criteria... expressions)
/* 452:    */   {
/* 453:516 */     List<ITEM> results = query(expressions);
/* 454:    */     
/* 455:518 */     return Selector.selectFrom(selectors, results, this.fields);
/* 456:    */   }
/* 457:    */   
/* 458:    */   public void invalidateIndex(String property, ITEM item)
/* 459:    */   {
/* 460:525 */     LookupIndex index = (LookupIndex)this.searchIndexMap.get(property);
/* 461:526 */     if (index != null) {
/* 462:527 */       index.delete(item);
/* 463:    */     }
/* 464:530 */     index = (LookupIndex)this.lookupIndexMap.get(property);
/* 465:531 */     if (index != null) {
/* 466:532 */       index.delete(item);
/* 467:    */     }
/* 468:534 */     this.filter.invalidate();
/* 469:    */   }
/* 470:    */   
/* 471:    */   public void validateIndex(String property, ITEM item)
/* 472:    */   {
/* 473:539 */     LookupIndex index = (LookupIndex)this.searchIndexMap.get(property);
/* 474:540 */     if (index != null) {
/* 475:541 */       index.add(item);
/* 476:    */     }
/* 477:544 */     index = (LookupIndex)this.lookupIndexMap.get(property);
/* 478:545 */     if (index != null) {
/* 479:546 */       index.add(item);
/* 480:    */     }
/* 481:    */   }
/* 482:    */   
/* 483:    */   public void clear()
/* 484:    */   {
/* 485:554 */     for (LookupIndex index : this.indexes) {
/* 486:555 */       index.clear();
/* 487:    */     }
/* 488:    */   }
/* 489:    */   
/* 490:    */   public void setFilter(Filter filter)
/* 491:    */   {
/* 492:562 */     this.filter = filter;
/* 493:    */   }
/* 494:    */   
/* 495:    */   public void addSearchIndex(String name, SearchIndex si)
/* 496:    */   {
/* 497:567 */     this.log.config(String.format("search index added name %s", new Object[] { name }));
/* 498:568 */     this.searchIndexMap.put(name, si);
/* 499:569 */     this.indexes.add(si);
/* 500:    */   }
/* 501:    */   
/* 502:    */   public void addLookupIndex(String name, LookupIndex si)
/* 503:    */   {
/* 504:573 */     this.log.config(String.format("lookupWithDefault index added name %s", new Object[] { name }));
/* 505:    */     
/* 506:575 */     this.lookupIndexMap.put(name, si);
/* 507:576 */     this.indexes.add(si);
/* 508:    */   }
/* 509:    */   
/* 510:    */   public List<ITEM> all()
/* 511:    */   {
/* 512:580 */     return this.primaryIndex.all();
/* 513:    */   }
/* 514:    */   
/* 515:    */   public void setPrimaryKeyName(String primaryKey)
/* 516:    */   {
/* 517:585 */     this.primaryKeyName = primaryKey;
/* 518:    */   }
/* 519:    */   
/* 520:    */   public Collection<ITEM> toCollection()
/* 521:    */   {
/* 522:589 */     return this.primaryIndex.toCollection();
/* 523:    */   }
/* 524:    */   
/* 525:    */   public boolean isEmpty()
/* 526:    */   {
/* 527:593 */     return this.primaryIndex.toCollection().isEmpty();
/* 528:    */   }
/* 529:    */   
/* 530:    */   public Iterator<ITEM> iterator()
/* 531:    */   {
/* 532:598 */     return this.primaryIndex.toCollection().iterator();
/* 533:    */   }
/* 534:    */   
/* 535:    */   public Object[] toArray()
/* 536:    */   {
/* 537:603 */     return this.primaryIndex.toCollection().toArray();
/* 538:    */   }
/* 539:    */   
/* 540:    */   public <T> T[] toArray(T[] a)
/* 541:    */   {
/* 542:607 */     return this.primaryIndex.toCollection().toArray(a);
/* 543:    */   }
/* 544:    */   
/* 545:    */   public void setPrimaryKeyGetter(Function getter)
/* 546:    */   {
/* 547:612 */     this.log.config(String.format("primary key getter set %s", new Object[] { getter }));
/* 548:    */     
/* 549:614 */     this.primaryKeyGetter = getter;
/* 550:    */   }
/* 551:    */   
/* 552:    */   public void init()
/* 553:    */   {
/* 554:619 */     this.primaryIndex = ((UniqueLookupIndex)this.lookupIndexMap.get(this.primaryKeyName));
/* 555:620 */     if ((this.filter instanceof FilterComposer))
/* 556:    */     {
/* 557:621 */       FilterComposer fc = (FilterComposer)this.filter;
/* 558:622 */       fc.setFields(this.fields);
/* 559:623 */       fc.setLookupIndexMap(this.lookupIndexMap);
/* 560:624 */       fc.setSearchIndexMap(this.searchIndexMap);
/* 561:625 */       fc.setSearchableCollection(this);
/* 562:626 */       fc.init();
/* 563:    */     }
/* 564:628 */     this.indexes.add(this.primaryIndex);
/* 565:    */   }
/* 566:    */   
/* 567:    */   public void setFields(Map<String, FieldAccess> fields)
/* 568:    */   {
/* 569:633 */     this.fields = fields;
/* 570:    */   }
/* 571:    */   
/* 572:    */   public int size()
/* 573:    */   {
/* 574:638 */     return this.primaryIndex.size();
/* 575:    */   }
/* 576:    */   
/* 577:    */   public boolean addAll(Collection<? extends ITEM> items)
/* 578:    */   {
/* 579:642 */     for (ITEM item : items) {
/* 580:643 */       add(item);
/* 581:    */     }
/* 582:645 */     return true;
/* 583:    */   }
/* 584:    */   
/* 585:    */   public boolean remove(Object o)
/* 586:    */   {
/* 587:649 */     KEY key = null;
/* 588:650 */     ITEM item = null;
/* 589:651 */     item = o;
/* 590:652 */     delete(item);
/* 591:653 */     return true;
/* 592:    */   }
/* 593:    */   
/* 594:    */   public void removeByKey(KEY key)
/* 595:    */   {
/* 596:657 */     ITEM item = get(key);
/* 597:658 */     delete(item);
/* 598:    */   }
/* 599:    */   
/* 600:    */   public boolean containsAll(Collection<?> c)
/* 601:    */   {
/* 602:664 */     for (Object object : c)
/* 603:    */     {
/* 604:665 */       KEY key = null;
/* 605:666 */       ITEM item = null;
/* 606:    */       try
/* 607:    */       {
/* 608:668 */         key = object;
/* 609:669 */         item = get(key);
/* 610:    */       }
/* 611:    */       catch (ClassCastException ex)
/* 612:    */       {
/* 613:671 */         ITEM itemArg = object;
/* 614:672 */         key = getKey(itemArg);
/* 615:673 */         item = get(key);
/* 616:    */       }
/* 617:675 */       if (item == null) {
/* 618:676 */         return true;
/* 619:    */       }
/* 620:    */     }
/* 621:679 */     return false;
/* 622:    */   }
/* 623:    */   
/* 624:    */   public boolean removeAll(Collection<?> items)
/* 625:    */   {
/* 626:684 */     for (Object o : items) {
/* 627:685 */       remove(o);
/* 628:    */     }
/* 629:687 */     return true;
/* 630:    */   }
/* 631:    */   
/* 632:    */   public boolean retainAll(Collection<?> c)
/* 633:    */   {
/* 634:692 */     for (Object object : c)
/* 635:    */     {
/* 636:693 */       KEY key = null;
/* 637:694 */       ITEM item = null;
/* 638:    */       try
/* 639:    */       {
/* 640:696 */         key = object;
/* 641:697 */         item = get(key);
/* 642:    */       }
/* 643:    */       catch (ClassCastException ex)
/* 644:    */       {
/* 645:699 */         item = object;
/* 646:    */       }
/* 647:701 */       if (item == null) {
/* 648:702 */         return true;
/* 649:    */       }
/* 650:    */     }
/* 651:705 */     return false;
/* 652:    */   }
/* 653:    */   
/* 654:    */   public boolean contains(Object o)
/* 655:    */   {
/* 656:710 */     KEY key = null;
/* 657:711 */     ITEM item = null;
/* 658:    */     try
/* 659:    */     {
/* 660:713 */       key = o;
/* 661:714 */       item = get(key);
/* 662:    */     }
/* 663:    */     catch (ClassCastException ex)
/* 664:    */     {
/* 665:716 */       ITEM itemArg = o;
/* 666:717 */       key = getKey(itemArg);
/* 667:718 */       item = get(key);
/* 668:    */     }
/* 669:720 */     if (item == null) {
/* 670:721 */       return true;
/* 671:    */     }
/* 672:723 */     return false;
/* 673:    */   }
/* 674:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.SearchableCollectionDefault
 * JD-Core Version:    0.7.0.1
 */