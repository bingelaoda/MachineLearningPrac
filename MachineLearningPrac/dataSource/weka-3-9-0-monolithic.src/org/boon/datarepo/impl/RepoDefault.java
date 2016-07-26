/*   1:    */ package org.boon.datarepo.impl;
/*   2:    */ 
/*   3:    */ import java.util.Collection;
/*   4:    */ import java.util.Iterator;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.Map;
/*   7:    */ import org.boon.criteria.Selector;
/*   8:    */ import org.boon.criteria.Update;
/*   9:    */ import org.boon.criteria.internal.Criteria;
/*  10:    */ import org.boon.criteria.internal.Visitor;
/*  11:    */ import org.boon.datarepo.LookupIndex;
/*  12:    */ import org.boon.datarepo.ObjectEditor;
/*  13:    */ import org.boon.datarepo.Repo;
/*  14:    */ import org.boon.datarepo.ResultSet;
/*  15:    */ import org.boon.datarepo.SearchableCollection;
/*  16:    */ import org.boon.datarepo.spi.RepoComposer;
/*  17:    */ import org.boon.datarepo.spi.SearchIndex;
/*  18:    */ import org.boon.sort.Sort;
/*  19:    */ 
/*  20:    */ public class RepoDefault<KEY, ITEM>
/*  21:    */   implements Repo<KEY, ITEM>, RepoComposer<KEY, ITEM>
/*  22:    */ {
/*  23:    */   private ObjectEditor<KEY, ITEM> editor;
/*  24:    */   private SearchableCollection<KEY, ITEM> query;
/*  25:    */   
/*  26:    */   public Repo init(List<ITEM> items)
/*  27:    */   {
/*  28: 58 */     addAll(items);
/*  29: 59 */     return this;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void updateByFilter(String property, Object value, Criteria... expressions)
/*  33:    */   {
/*  34: 64 */     List<ITEM> items = this.query.query(expressions);
/*  35: 65 */     for (ITEM item : items) {
/*  36: 66 */       modify(item, property, value);
/*  37:    */     }
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void updateByFilterUsingValue(String property, String value, Criteria... expressions)
/*  41:    */   {
/*  42: 72 */     List<ITEM> items = this.query.query(expressions);
/*  43: 73 */     for (ITEM item : items) {
/*  44: 74 */       modifyByValue(item, property, value);
/*  45:    */     }
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void updateByFilter(String property, int value, Criteria... expressions)
/*  49:    */   {
/*  50: 80 */     List<ITEM> items = this.query.query(expressions);
/*  51: 81 */     for (ITEM item : items) {
/*  52: 82 */       modify(item, property, value);
/*  53:    */     }
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void updateByFilter(String property, long value, Criteria... expressions)
/*  57:    */   {
/*  58: 88 */     List<ITEM> items = this.query.query(expressions);
/*  59: 89 */     for (ITEM item : items) {
/*  60: 90 */       modify(item, property, value);
/*  61:    */     }
/*  62:    */   }
/*  63:    */   
/*  64:    */   public void updateByFilter(String property, char value, Criteria... expressions)
/*  65:    */   {
/*  66: 96 */     List<ITEM> items = this.query.query(expressions);
/*  67: 97 */     for (ITEM item : items) {
/*  68: 98 */       modify(item, property, value);
/*  69:    */     }
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void updateByFilter(String property, short value, Criteria... expressions)
/*  73:    */   {
/*  74:104 */     List<ITEM> items = this.query.query(expressions);
/*  75:105 */     for (ITEM item : items) {
/*  76:106 */       modify(item, property, value);
/*  77:    */     }
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void updateByFilter(String property, byte value, Criteria... expressions)
/*  81:    */   {
/*  82:112 */     List<ITEM> items = this.query.query(expressions);
/*  83:113 */     for (ITEM item : items) {
/*  84:114 */       modify(item, property, value);
/*  85:    */     }
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void updateByFilter(String property, float value, Criteria... expressions)
/*  89:    */   {
/*  90:120 */     List<ITEM> items = this.query.query(expressions);
/*  91:121 */     for (ITEM item : items) {
/*  92:122 */       modify(item, property, value);
/*  93:    */     }
/*  94:    */   }
/*  95:    */   
/*  96:    */   public void updateByFilter(String property, double value, Criteria... expressions)
/*  97:    */   {
/*  98:128 */     List<ITEM> items = this.query.query(expressions);
/*  99:129 */     for (ITEM item : items) {
/* 100:130 */       modify(item, property, value);
/* 101:    */     }
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void updateByFilter(List<Update> values, Criteria... expressions)
/* 105:    */   {
/* 106:136 */     List<ITEM> items = this.query.query(expressions);
/* 107:137 */     for (Iterator i$ = items.iterator(); i$.hasNext();)
/* 108:    */     {
/* 109:137 */       item = i$.next();
/* 110:139 */       for (Update value : values)
/* 111:    */       {
/* 112:140 */         this.query.invalidateIndex(value.getName(), item);
/* 113:141 */         value.doSet(this, item);
/* 114:142 */         this.query.validateIndex(value.getName(), item);
/* 115:    */       }
/* 116:    */     }
/* 117:    */     ITEM item;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public ITEM get(KEY key)
/* 121:    */   {
/* 122:150 */     return this.editor.get(key);
/* 123:    */   }
/* 124:    */   
/* 125:    */   public KEY getKey(ITEM item)
/* 126:    */   {
/* 127:155 */     return this.editor.getKey(item);
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void invalidateIndex(String property, ITEM item)
/* 131:    */   {
/* 132:160 */     this.query.invalidateIndex(property, item);
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void validateIndex(String property, ITEM item)
/* 136:    */   {
/* 137:165 */     this.query.validateIndex(property, item);
/* 138:    */   }
/* 139:    */   
/* 140:    */   public void validateIndexes(ITEM item)
/* 141:    */   {
/* 142:170 */     this.query.validateIndexes(item);
/* 143:    */   }
/* 144:    */   
/* 145:    */   public Object readObject(KEY key, String property)
/* 146:    */   {
/* 147:175 */     return this.editor.readObject(key, property);
/* 148:    */   }
/* 149:    */   
/* 150:    */   public <T> T readValue(KEY key, String property, Class<T> type)
/* 151:    */   {
/* 152:180 */     return this.editor.readValue(key, property, type);
/* 153:    */   }
/* 154:    */   
/* 155:    */   public int readInt(KEY key, String property)
/* 156:    */   {
/* 157:185 */     return this.editor.readInt(key, property);
/* 158:    */   }
/* 159:    */   
/* 160:    */   public long readLong(KEY key, String property)
/* 161:    */   {
/* 162:190 */     return this.editor.readLong(key, property);
/* 163:    */   }
/* 164:    */   
/* 165:    */   public char readChar(KEY key, String property)
/* 166:    */   {
/* 167:195 */     return this.editor.readChar(key, property);
/* 168:    */   }
/* 169:    */   
/* 170:    */   public short readShort(KEY key, String property)
/* 171:    */   {
/* 172:200 */     return this.editor.readShort(key, property);
/* 173:    */   }
/* 174:    */   
/* 175:    */   public byte readByte(KEY key, String property)
/* 176:    */   {
/* 177:205 */     return this.editor.readByte(key, property);
/* 178:    */   }
/* 179:    */   
/* 180:    */   public float readFloat(KEY key, String property)
/* 181:    */   {
/* 182:210 */     return this.editor.readFloat(key, property);
/* 183:    */   }
/* 184:    */   
/* 185:    */   public double readDouble(KEY key, String property)
/* 186:    */   {
/* 187:215 */     return this.editor.readDouble(key, property);
/* 188:    */   }
/* 189:    */   
/* 190:    */   public Object getObject(ITEM item, String property)
/* 191:    */   {
/* 192:220 */     return this.editor.getObject(item, property);
/* 193:    */   }
/* 194:    */   
/* 195:    */   public <T> T getValue(ITEM item, String property, Class<T> type)
/* 196:    */   {
/* 197:225 */     return this.editor.getValue(item, property, type);
/* 198:    */   }
/* 199:    */   
/* 200:    */   public int getInt(ITEM item, String property)
/* 201:    */   {
/* 202:230 */     return this.editor.getInt(item, property);
/* 203:    */   }
/* 204:    */   
/* 205:    */   public long getLong(ITEM item, String property)
/* 206:    */   {
/* 207:235 */     return this.editor.getLong(item, property);
/* 208:    */   }
/* 209:    */   
/* 210:    */   public char getChar(ITEM item, String property)
/* 211:    */   {
/* 212:241 */     return this.editor.getChar(item, property);
/* 213:    */   }
/* 214:    */   
/* 215:    */   public short getShort(ITEM item, String property)
/* 216:    */   {
/* 217:246 */     return this.editor.getShort(item, property);
/* 218:    */   }
/* 219:    */   
/* 220:    */   public byte getByte(ITEM item, String property)
/* 221:    */   {
/* 222:251 */     return this.editor.getByte(item, property);
/* 223:    */   }
/* 224:    */   
/* 225:    */   public float getFloat(ITEM item, String property)
/* 226:    */   {
/* 227:256 */     return this.editor.getFloat(item, property);
/* 228:    */   }
/* 229:    */   
/* 230:    */   public double getDouble(ITEM item, String property)
/* 231:    */   {
/* 232:261 */     return this.editor.getDouble(item, property);
/* 233:    */   }
/* 234:    */   
/* 235:    */   public int count(KEY key, String property, int value)
/* 236:    */   {
/* 237:266 */     return this.query.count(key, property, value);
/* 238:    */   }
/* 239:    */   
/* 240:    */   public int count(KEY key, String property, short value)
/* 241:    */   {
/* 242:271 */     return this.query.count(key, property, value);
/* 243:    */   }
/* 244:    */   
/* 245:    */   public int count(KEY key, String property, byte value)
/* 246:    */   {
/* 247:276 */     return this.query.count(key, property, value);
/* 248:    */   }
/* 249:    */   
/* 250:    */   public int count(KEY key, String property, long value)
/* 251:    */   {
/* 252:281 */     return this.query.count(key, property, value);
/* 253:    */   }
/* 254:    */   
/* 255:    */   public int count(KEY key, String property, char value)
/* 256:    */   {
/* 257:286 */     return this.query.count(key, property, value);
/* 258:    */   }
/* 259:    */   
/* 260:    */   public int count(KEY key, String property, float value)
/* 261:    */   {
/* 262:291 */     return this.query.count(key, property, value);
/* 263:    */   }
/* 264:    */   
/* 265:    */   public int count(KEY key, String property, double value)
/* 266:    */   {
/* 267:296 */     return this.query.count(key, property, value);
/* 268:    */   }
/* 269:    */   
/* 270:    */   public int count(KEY key, String property, Object value)
/* 271:    */   {
/* 272:301 */     return this.query.count(key, property, value);
/* 273:    */   }
/* 274:    */   
/* 275:    */   public <T> T max(KEY key, String property, Class<T> type)
/* 276:    */   {
/* 277:306 */     return this.query.max(key, property, type);
/* 278:    */   }
/* 279:    */   
/* 280:    */   public String maxString(KEY key, String property)
/* 281:    */   {
/* 282:311 */     return this.query.maxString(key, property);
/* 283:    */   }
/* 284:    */   
/* 285:    */   public Number maxNumber(KEY key, String property)
/* 286:    */   {
/* 287:316 */     return this.query.maxNumber(key, property);
/* 288:    */   }
/* 289:    */   
/* 290:    */   public int maxInt(KEY key, String property)
/* 291:    */   {
/* 292:321 */     return this.query.maxInt(key, property);
/* 293:    */   }
/* 294:    */   
/* 295:    */   public long maxLong(KEY key, String property)
/* 296:    */   {
/* 297:326 */     return this.query.maxLong(key, property);
/* 298:    */   }
/* 299:    */   
/* 300:    */   public double maxDouble(KEY key, String property)
/* 301:    */   {
/* 302:331 */     return this.query.maxDouble(key, property);
/* 303:    */   }
/* 304:    */   
/* 305:    */   public <T> T min(KEY key, String property, Class<T> type)
/* 306:    */   {
/* 307:336 */     return this.query.min(key, property, type);
/* 308:    */   }
/* 309:    */   
/* 310:    */   public String minString(KEY key, String property)
/* 311:    */   {
/* 312:341 */     return this.query.minString(key, property);
/* 313:    */   }
/* 314:    */   
/* 315:    */   public Number minNumber(KEY key, String property)
/* 316:    */   {
/* 317:346 */     return this.query.minNumber(key, property);
/* 318:    */   }
/* 319:    */   
/* 320:    */   public int minInt(KEY key, String property)
/* 321:    */   {
/* 322:351 */     return this.query.minInt(key, property);
/* 323:    */   }
/* 324:    */   
/* 325:    */   public long minLong(KEY key, String property)
/* 326:    */   {
/* 327:356 */     return this.query.minLong(key, property);
/* 328:    */   }
/* 329:    */   
/* 330:    */   public double minDouble(KEY key, String property)
/* 331:    */   {
/* 332:361 */     return this.query.minDouble(key, property);
/* 333:    */   }
/* 334:    */   
/* 335:    */   public ResultSet<ITEM> results(Criteria... expressions)
/* 336:    */   {
/* 337:366 */     return this.query.results(expressions);
/* 338:    */   }
/* 339:    */   
/* 340:    */   public List<ITEM> query(Criteria... expressions)
/* 341:    */   {
/* 342:371 */     return this.query.query(expressions);
/* 343:    */   }
/* 344:    */   
/* 345:    */   public List<ITEM> query(List<Criteria> expressions)
/* 346:    */   {
/* 347:376 */     return this.query.query(expressions);
/* 348:    */   }
/* 349:    */   
/* 350:    */   public List<ITEM> sortedQuery(String sortBy, Criteria... expressions)
/* 351:    */   {
/* 352:381 */     return this.query.sortedQuery(sortBy, expressions);
/* 353:    */   }
/* 354:    */   
/* 355:    */   public List<ITEM> sortedQuery(Sort sortBy, Criteria... expressions)
/* 356:    */   {
/* 357:386 */     return this.query.sortedQuery(sortBy, expressions);
/* 358:    */   }
/* 359:    */   
/* 360:    */   public List<Map<String, Object>> queryAsMaps(Criteria... expressions)
/* 361:    */   {
/* 362:391 */     return this.query.queryAsMaps(expressions);
/* 363:    */   }
/* 364:    */   
/* 365:    */   public List<Map<String, Object>> query(List<Selector> selectors, Criteria... expressions)
/* 366:    */   {
/* 367:396 */     return this.query.query(selectors, expressions);
/* 368:    */   }
/* 369:    */   
/* 370:    */   public List<Map<String, Object>> sortedQuery(String sortBy, List<Selector> selectors, Criteria... expressions)
/* 371:    */   {
/* 372:401 */     return this.query.sortedQuery(sortBy, selectors, expressions);
/* 373:    */   }
/* 374:    */   
/* 375:    */   public List<Map<String, Object>> sortedQuery(Sort sortBy, List<Selector> selectors, Criteria... expressions)
/* 376:    */   {
/* 377:406 */     return this.query.sortedQuery(sortBy, selectors, expressions);
/* 378:    */   }
/* 379:    */   
/* 380:    */   public void query(Visitor<KEY, ITEM> visitor, Criteria... expressions)
/* 381:    */   {
/* 382:411 */     this.query.query(visitor, expressions);
/* 383:    */   }
/* 384:    */   
/* 385:    */   public void sortedQuery(Visitor<KEY, ITEM> visitor, String sortBy, Criteria... expressions)
/* 386:    */   {
/* 387:416 */     this.query.query(visitor, expressions);
/* 388:    */   }
/* 389:    */   
/* 390:    */   public void sortedQuery(Visitor<KEY, ITEM> visitor, Sort sortBy, Criteria... expressions)
/* 391:    */   {
/* 392:421 */     this.query.sortedQuery(visitor, sortBy, expressions);
/* 393:    */   }
/* 394:    */   
/* 395:    */   public void put(ITEM item)
/* 396:    */   {
/* 397:426 */     this.editor.put(item);
/* 398:    */   }
/* 399:    */   
/* 400:    */   public void removeByKey(KEY key)
/* 401:    */   {
/* 402:431 */     this.editor.removeByKey(key);
/* 403:    */   }
/* 404:    */   
/* 405:    */   public void removeAll(ITEM... items)
/* 406:    */   {
/* 407:436 */     this.editor.removeAll(items);
/* 408:    */   }
/* 409:    */   
/* 410:    */   public void removeAllAsync(Collection<ITEM> items)
/* 411:    */   {
/* 412:441 */     this.editor.removeAllAsync(items);
/* 413:    */   }
/* 414:    */   
/* 415:    */   public void addAll(ITEM... items)
/* 416:    */   {
/* 417:446 */     this.editor.addAll(items);
/* 418:    */   }
/* 419:    */   
/* 420:    */   public void addAllAsync(Collection<ITEM> items)
/* 421:    */   {
/* 422:451 */     this.editor.addAllAsync(items);
/* 423:    */   }
/* 424:    */   
/* 425:    */   public void modifyAll(ITEM... items)
/* 426:    */   {
/* 427:456 */     this.editor.modifyAll(items);
/* 428:    */   }
/* 429:    */   
/* 430:    */   public void modifyAll(Collection<ITEM> items)
/* 431:    */   {
/* 432:461 */     this.editor.modifyAll(items);
/* 433:    */   }
/* 434:    */   
/* 435:    */   public void modify(ITEM item)
/* 436:    */   {
/* 437:466 */     this.editor.modify(item);
/* 438:    */   }
/* 439:    */   
/* 440:    */   public void update(ITEM item)
/* 441:    */   {
/* 442:471 */     this.editor.update(item);
/* 443:    */   }
/* 444:    */   
/* 445:    */   public void modifyByValue(ITEM item, String property, String value)
/* 446:    */   {
/* 447:476 */     this.editor.modifyByValue(item, property, value);
/* 448:    */   }
/* 449:    */   
/* 450:    */   public void modify(ITEM item, String property, Object value)
/* 451:    */   {
/* 452:481 */     this.editor.modify(item, property, value);
/* 453:    */   }
/* 454:    */   
/* 455:    */   public void modify(ITEM item, String property, int value)
/* 456:    */   {
/* 457:486 */     this.editor.modify(item, property, value);
/* 458:    */   }
/* 459:    */   
/* 460:    */   public void modify(ITEM item, String property, long value)
/* 461:    */   {
/* 462:491 */     this.editor.modify(item, property, value);
/* 463:    */   }
/* 464:    */   
/* 465:    */   public void modify(ITEM item, String property, char value)
/* 466:    */   {
/* 467:496 */     this.editor.modify(item, property, value);
/* 468:    */   }
/* 469:    */   
/* 470:    */   public void modify(ITEM item, String property, short value)
/* 471:    */   {
/* 472:501 */     this.editor.modify(item, property, value);
/* 473:    */   }
/* 474:    */   
/* 475:    */   public void modify(ITEM item, String property, byte value)
/* 476:    */   {
/* 477:506 */     this.editor.modify(item, property, value);
/* 478:    */   }
/* 479:    */   
/* 480:    */   public void modify(ITEM item, String property, float value)
/* 481:    */   {
/* 482:511 */     this.editor.modify(item, property, value);
/* 483:    */   }
/* 484:    */   
/* 485:    */   public void modify(ITEM item, String property, double value)
/* 486:    */   {
/* 487:516 */     this.editor.modify(item, property, value);
/* 488:    */   }
/* 489:    */   
/* 490:    */   public void modify(ITEM item, Update... values)
/* 491:    */   {
/* 492:521 */     this.editor.modify(item, values);
/* 493:    */   }
/* 494:    */   
/* 495:    */   public void updateByValue(KEY key, String property, String value)
/* 496:    */   {
/* 497:526 */     this.editor.updateByValue(key, property, value);
/* 498:    */   }
/* 499:    */   
/* 500:    */   public void update(KEY key, String property, Object value)
/* 501:    */   {
/* 502:531 */     this.editor.update(key, property, value);
/* 503:    */   }
/* 504:    */   
/* 505:    */   public void update(KEY key, String property, int value)
/* 506:    */   {
/* 507:536 */     this.editor.update(key, property, value);
/* 508:    */   }
/* 509:    */   
/* 510:    */   public void update(KEY key, String property, long value)
/* 511:    */   {
/* 512:541 */     this.editor.update(key, property, value);
/* 513:    */   }
/* 514:    */   
/* 515:    */   public void update(KEY key, String property, char value)
/* 516:    */   {
/* 517:546 */     this.editor.update(key, property, value);
/* 518:    */   }
/* 519:    */   
/* 520:    */   public void update(KEY key, String property, short value)
/* 521:    */   {
/* 522:551 */     this.editor.update(key, property, value);
/* 523:    */   }
/* 524:    */   
/* 525:    */   public void update(KEY key, String property, byte value)
/* 526:    */   {
/* 527:556 */     this.editor.update(key, property, value);
/* 528:    */   }
/* 529:    */   
/* 530:    */   public void update(KEY key, String property, float value)
/* 531:    */   {
/* 532:561 */     this.editor.update(key, property, value);
/* 533:    */   }
/* 534:    */   
/* 535:    */   public void update(KEY key, String property, double value)
/* 536:    */   {
/* 537:566 */     this.editor.update(key, property, value);
/* 538:    */   }
/* 539:    */   
/* 540:    */   public void update(KEY key, Update... values)
/* 541:    */   {
/* 542:571 */     this.editor.update(key, values);
/* 543:    */   }
/* 544:    */   
/* 545:    */   public boolean compareAndUpdate(KEY key, String property, Object compare, Object value)
/* 546:    */   {
/* 547:576 */     return this.editor.compareAndUpdate(key, property, compare, value);
/* 548:    */   }
/* 549:    */   
/* 550:    */   public boolean compareAndUpdate(KEY key, String property, int compare, int value)
/* 551:    */   {
/* 552:581 */     return this.editor.compareAndUpdate(key, property, compare, value);
/* 553:    */   }
/* 554:    */   
/* 555:    */   public boolean compareAndUpdate(KEY key, String property, long compare, long value)
/* 556:    */   {
/* 557:586 */     return this.editor.compareAndUpdate(key, property, compare, value);
/* 558:    */   }
/* 559:    */   
/* 560:    */   public boolean compareAndUpdate(KEY key, String property, char compare, char value)
/* 561:    */   {
/* 562:591 */     return this.editor.compareAndUpdate(key, property, compare, value);
/* 563:    */   }
/* 564:    */   
/* 565:    */   public boolean compareAndUpdate(KEY key, String property, short compare, short value)
/* 566:    */   {
/* 567:596 */     return this.editor.compareAndUpdate(key, property, compare, value);
/* 568:    */   }
/* 569:    */   
/* 570:    */   public boolean compareAndUpdate(KEY key, String property, byte compare, byte value)
/* 571:    */   {
/* 572:601 */     return this.editor.compareAndUpdate(key, property, compare, value);
/* 573:    */   }
/* 574:    */   
/* 575:    */   public boolean compareAndUpdate(KEY key, String property, float compare, float value)
/* 576:    */   {
/* 577:606 */     return this.editor.compareAndUpdate(key, property, compare, value);
/* 578:    */   }
/* 579:    */   
/* 580:    */   public boolean compareAndUpdate(KEY key, String property, double compare, double value)
/* 581:    */   {
/* 582:611 */     return this.editor.compareAndUpdate(key, property, compare, value);
/* 583:    */   }
/* 584:    */   
/* 585:    */   public boolean compareAndIncrement(KEY key, String property, int compare)
/* 586:    */   {
/* 587:616 */     return this.editor.compareAndIncrement(key, property, compare);
/* 588:    */   }
/* 589:    */   
/* 590:    */   public boolean compareAndIncrement(KEY key, String property, long compare)
/* 591:    */   {
/* 592:621 */     return this.editor.compareAndIncrement(key, property, compare);
/* 593:    */   }
/* 594:    */   
/* 595:    */   public boolean compareAndIncrement(KEY key, String property, short compare)
/* 596:    */   {
/* 597:626 */     return this.editor.compareAndIncrement(key, property, compare);
/* 598:    */   }
/* 599:    */   
/* 600:    */   public boolean compareAndIncrement(KEY key, String property, byte compare)
/* 601:    */   {
/* 602:631 */     return this.editor.compareAndIncrement(key, property, compare);
/* 603:    */   }
/* 604:    */   
/* 605:    */   public void addAll(List<ITEM> items)
/* 606:    */   {
/* 607:636 */     this.editor.addAll(items);
/* 608:    */   }
/* 609:    */   
/* 610:    */   public Object readNestedValue(KEY key, String... properties)
/* 611:    */   {
/* 612:641 */     return this.editor.readNestedValue(key, properties);
/* 613:    */   }
/* 614:    */   
/* 615:    */   public int readNestedInt(KEY key, String... properties)
/* 616:    */   {
/* 617:646 */     return this.editor.readNestedInt(key, properties);
/* 618:    */   }
/* 619:    */   
/* 620:    */   public short readNestedShort(KEY key, String... properties)
/* 621:    */   {
/* 622:651 */     return this.editor.readNestedShort(key, properties);
/* 623:    */   }
/* 624:    */   
/* 625:    */   public char readNestedChar(KEY key, String... properties)
/* 626:    */   {
/* 627:656 */     return this.editor.readNestedChar(key, properties);
/* 628:    */   }
/* 629:    */   
/* 630:    */   public byte readNestedByte(KEY key, String... properties)
/* 631:    */   {
/* 632:661 */     return this.editor.readNestedByte(key, properties);
/* 633:    */   }
/* 634:    */   
/* 635:    */   public double readNestedDouble(KEY key, String... properties)
/* 636:    */   {
/* 637:666 */     return this.editor.readNestedDouble(key, properties);
/* 638:    */   }
/* 639:    */   
/* 640:    */   public float readNestedFloat(KEY key, String... properties)
/* 641:    */   {
/* 642:671 */     return this.editor.readNestedFloat(key, properties);
/* 643:    */   }
/* 644:    */   
/* 645:    */   public long readNestedLong(KEY key, String... properties)
/* 646:    */   {
/* 647:676 */     return this.editor.readNestedLong(key, properties);
/* 648:    */   }
/* 649:    */   
/* 650:    */   public boolean add(ITEM item)
/* 651:    */   {
/* 652:681 */     return this.editor.add(item);
/* 653:    */   }
/* 654:    */   
/* 655:    */   public boolean remove(Object o)
/* 656:    */   {
/* 657:686 */     return this.query.remove(o);
/* 658:    */   }
/* 659:    */   
/* 660:    */   public boolean containsAll(Collection<?> c)
/* 661:    */   {
/* 662:691 */     return this.query.containsAll(c);
/* 663:    */   }
/* 664:    */   
/* 665:    */   public boolean addAll(Collection<? extends ITEM> c)
/* 666:    */   {
/* 667:696 */     return this.query.addAll(c);
/* 668:    */   }
/* 669:    */   
/* 670:    */   public boolean removeAll(Collection<?> c)
/* 671:    */   {
/* 672:701 */     return this.query.removeAll(c);
/* 673:    */   }
/* 674:    */   
/* 675:    */   public boolean retainAll(Collection<?> c)
/* 676:    */   {
/* 677:706 */     return this.query.retainAll(c);
/* 678:    */   }
/* 679:    */   
/* 680:    */   public boolean delete(ITEM item)
/* 681:    */   {
/* 682:711 */     return this.editor.delete(item);
/* 683:    */   }
/* 684:    */   
/* 685:    */   public void addSearchIndex(String name, SearchIndex<?, ?> si)
/* 686:    */   {
/* 687:716 */     this.query.addSearchIndex(name, si);
/* 688:    */   }
/* 689:    */   
/* 690:    */   public void addLookupIndex(String name, LookupIndex<?, ?> si)
/* 691:    */   {
/* 692:721 */     this.query.addLookupIndex(name, si);
/* 693:    */   }
/* 694:    */   
/* 695:    */   public List<ITEM> all()
/* 696:    */   {
/* 697:726 */     return this.editor.all();
/* 698:    */   }
/* 699:    */   
/* 700:    */   public int size()
/* 701:    */   {
/* 702:731 */     return this.editor.size();
/* 703:    */   }
/* 704:    */   
/* 705:    */   public boolean isEmpty()
/* 706:    */   {
/* 707:737 */     return this.query.isEmpty();
/* 708:    */   }
/* 709:    */   
/* 710:    */   public boolean contains(Object o)
/* 711:    */   {
/* 712:742 */     return this.query.contains(o);
/* 713:    */   }
/* 714:    */   
/* 715:    */   public Iterator<ITEM> iterator()
/* 716:    */   {
/* 717:747 */     return this.query.iterator();
/* 718:    */   }
/* 719:    */   
/* 720:    */   public Object[] toArray()
/* 721:    */   {
/* 722:753 */     return this.query.toArray();
/* 723:    */   }
/* 724:    */   
/* 725:    */   public <T> T[] toArray(T[] a)
/* 726:    */   {
/* 727:759 */     return this.query.toArray(a);
/* 728:    */   }
/* 729:    */   
/* 730:    */   public Collection<ITEM> toCollection()
/* 731:    */   {
/* 732:764 */     return this.editor.toCollection();
/* 733:    */   }
/* 734:    */   
/* 735:    */   public void clear()
/* 736:    */   {
/* 737:769 */     this.editor.clear();
/* 738:    */   }
/* 739:    */   
/* 740:    */   public void setSearchableCollection(SearchableCollection<KEY, ITEM> searchableCollection)
/* 741:    */   {
/* 742:775 */     this.query = searchableCollection;
/* 743:    */   }
/* 744:    */   
/* 745:    */   public void init() {}
/* 746:    */   
/* 747:    */   public void setObjectEditor(ObjectEditor editor)
/* 748:    */   {
/* 749:784 */     this.editor = editor;
/* 750:    */   }
/* 751:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.RepoDefault
 * JD-Core Version:    0.7.0.1
 */