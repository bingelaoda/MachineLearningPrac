/*   1:    */ package org.boon.datarepo.impl;
/*   2:    */ 
/*   3:    */ import java.lang.reflect.Array;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.Arrays;
/*   6:    */ import java.util.Collection;
/*   7:    */ import java.util.Collections;
/*   8:    */ import java.util.HashSet;
/*   9:    */ import java.util.Iterator;
/*  10:    */ import java.util.List;
/*  11:    */ import java.util.Map;
/*  12:    */ import java.util.Set;
/*  13:    */ import org.boon.Boon;
/*  14:    */ import org.boon.Lists;
/*  15:    */ import org.boon.core.Conversions;
/*  16:    */ import org.boon.core.reflection.BeanUtils;
/*  17:    */ import org.boon.core.reflection.MapObjectConversion;
/*  18:    */ import org.boon.core.reflection.fields.FieldAccess;
/*  19:    */ import org.boon.criteria.Selector;
/*  20:    */ import org.boon.criteria.internal.Criteria;
/*  21:    */ import org.boon.criteria.internal.QueryFactory;
/*  22:    */ import org.boon.datarepo.DataRepoException;
/*  23:    */ import org.boon.datarepo.PlanStep;
/*  24:    */ import org.boon.datarepo.ResultSet;
/*  25:    */ import org.boon.datarepo.spi.ResultSetInternal;
/*  26:    */ import org.boon.sort.Sort;
/*  27:    */ 
/*  28:    */ public class ResultSetImpl<T>
/*  29:    */   implements ResultSetInternal<T>
/*  30:    */ {
/*  31:    */   private List<T> results;
/*  32:    */   private List<List<T>> allResults;
/*  33:    */   private int totalSize;
/*  34:    */   private List<T> lastList;
/*  35:    */   private Map<String, FieldAccess> fields;
/*  36:    */   
/*  37:    */   public ResultSetImpl(Map<String, FieldAccess> fields)
/*  38:    */   {
/*  39: 62 */     this.fields = fields;
/*  40: 63 */     this.allResults = new ArrayList();
/*  41:    */   }
/*  42:    */   
/*  43:    */   public ResultSetImpl(List<T> results, Map<String, FieldAccess> fields)
/*  44:    */   {
/*  45: 68 */     this.fields = fields;
/*  46: 69 */     this.allResults = new ArrayList();
/*  47: 70 */     addResults(results);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public ResultSetImpl(List<T> results)
/*  51:    */   {
/*  52: 74 */     if (results.size() > 0) {
/*  53: 75 */       this.fields = BeanUtils.getFieldsFromObject(results.get(0));
/*  54:    */     } else {
/*  55: 77 */       this.fields = Collections.EMPTY_MAP;
/*  56:    */     }
/*  57: 79 */     this.allResults = new ArrayList();
/*  58: 80 */     addResults(results);
/*  59:    */   }
/*  60:    */   
/*  61:    */   private void prepareResults()
/*  62:    */   {
/*  63: 84 */     if ((this.results == null) && (this.allResults.size() == 1))
/*  64:    */     {
/*  65: 85 */       this.results = ((List)this.allResults.get(0));
/*  66:    */     }
/*  67: 86 */     else if (this.results == null)
/*  68:    */     {
/*  69: 88 */       this.results = new ArrayList(this.totalSize);
/*  70: 90 */       for (List<T> list : this.allResults) {
/*  71: 91 */         for (T item : list) {
/*  72: 92 */           this.results.add(item);
/*  73:    */         }
/*  74:    */       }
/*  75:    */     }
/*  76: 96 */     this.allResults.clear();
/*  77: 97 */     this.totalSize = 0;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void addResults(List<T> results)
/*  81:    */   {
/*  82:102 */     this.lastList = results;
/*  83:103 */     this.totalSize += results.size();
/*  84:104 */     this.allResults.add(results);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public ResultSet expectOne()
/*  88:    */   {
/*  89:109 */     prepareResults();
/*  90:110 */     if (this.results.size() == 0) {
/*  91:111 */       throw new DataRepoException("Expected one result, no results");
/*  92:    */     }
/*  93:112 */     if (this.results.size() > 1) {
/*  94:113 */       throw new DataRepoException("Expected one result, but have many");
/*  95:    */     }
/*  96:115 */     return this;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public <EXPECT> ResultSet<EXPECT> expectOne(Class<EXPECT> clz)
/* 100:    */   {
/* 101:120 */     return expectOne();
/* 102:    */   }
/* 103:    */   
/* 104:    */   public ResultSet expectMany()
/* 105:    */   {
/* 106:125 */     prepareResults();
/* 107:127 */     if (this.results.size() <= 1) {
/* 108:128 */       throw new DataRepoException("Expected many");
/* 109:    */     }
/* 110:130 */     return this;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public ResultSet expectNone()
/* 114:    */   {
/* 115:135 */     prepareResults();
/* 116:137 */     if (this.results.size() != 0) {
/* 117:138 */       throw new DataRepoException("Expected none");
/* 118:    */     }
/* 119:140 */     return this;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public ResultSet expectOneOrMany()
/* 123:    */   {
/* 124:145 */     prepareResults();
/* 125:147 */     if (this.results.size() >= 1) {
/* 126:148 */       throw new DataRepoException("Expected one or many");
/* 127:    */     }
/* 128:150 */     return this;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public ResultSet removeDuplication()
/* 132:    */   {
/* 133:155 */     prepareResults();
/* 134:156 */     this.results = new ArrayList(asSet());
/* 135:157 */     return this;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public ResultSet sort(Sort sort)
/* 139:    */   {
/* 140:162 */     prepareResults();
/* 141:163 */     sort.sort(this.results);
/* 142:164 */     return this;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public Collection<T> filter(Criteria criteria)
/* 146:    */   {
/* 147:169 */     prepareResults();
/* 148:170 */     return QueryFactory.filter(this.results, criteria);
/* 149:    */   }
/* 150:    */   
/* 151:    */   public void filterAndPrune(Criteria criteria)
/* 152:    */   {
/* 153:175 */     prepareResults();
/* 154:176 */     this.results = QueryFactory.filter(this.results, criteria);
/* 155:    */   }
/* 156:    */   
/* 157:    */   public ResultSet<List<Map<String, Object>>> select(Selector... selectors)
/* 158:    */   {
/* 159:182 */     prepareResults();
/* 160:183 */     return new ResultSetImpl(Selector.selectFrom(Arrays.asList(selectors), this.results, this.fields), this.fields);
/* 161:    */   }
/* 162:    */   
/* 163:    */   public int[] selectInts(Selector selector)
/* 164:    */   {
/* 165:192 */     prepareResults();
/* 166:    */     
/* 167:194 */     int[] values = new int[this.results.size()];
/* 168:    */     
/* 169:196 */     List<Map<String, Object>> maps = Selector.selectFrom(Lists.list(new Selector[] { selector }), this.results, this.fields);
/* 170:    */     
/* 171:198 */     String keyName = selector.getName();
/* 172:199 */     for (int index = 0; index < values.length; index++)
/* 173:    */     {
/* 174:200 */       Map<String, Object> map = (Map)maps.get(index);
/* 175:201 */       values[index] = Conversions.toInt(map.get(keyName));
/* 176:    */     }
/* 177:203 */     return values;
/* 178:    */   }
/* 179:    */   
/* 180:    */   public float[] selectFloats(Selector selector)
/* 181:    */   {
/* 182:208 */     prepareResults();
/* 183:    */     
/* 184:210 */     float[] values = new float[this.results.size()];
/* 185:    */     
/* 186:212 */     List<Map<String, Object>> maps = Selector.selectFrom(Lists.list(new Selector[] { selector }), this.results, this.fields);
/* 187:    */     
/* 188:214 */     String keyName = selector.getName();
/* 189:215 */     for (int index = 0; index < values.length; index++)
/* 190:    */     {
/* 191:216 */       Map<String, Object> map = (Map)maps.get(index);
/* 192:217 */       values[index] = Conversions.toFloat(map.get(keyName));
/* 193:    */     }
/* 194:219 */     return values;
/* 195:    */   }
/* 196:    */   
/* 197:    */   public short[] selectShorts(Selector selector)
/* 198:    */   {
/* 199:224 */     prepareResults();
/* 200:    */     
/* 201:226 */     short[] values = new short[this.results.size()];
/* 202:    */     
/* 203:228 */     List<Map<String, Object>> maps = Selector.selectFrom(Lists.list(new Selector[] { selector }), this.results, this.fields);
/* 204:    */     
/* 205:230 */     String keyName = selector.getName();
/* 206:231 */     for (int index = 0; index < values.length; index++)
/* 207:    */     {
/* 208:232 */       Map<String, Object> map = (Map)maps.get(index);
/* 209:233 */       values[index] = Conversions.toShort(map.get(keyName));
/* 210:    */     }
/* 211:235 */     return values;
/* 212:    */   }
/* 213:    */   
/* 214:    */   public double[] selectDoubles(Selector selector)
/* 215:    */   {
/* 216:240 */     prepareResults();
/* 217:    */     
/* 218:242 */     double[] values = new double[this.results.size()];
/* 219:    */     
/* 220:244 */     List<Map<String, Object>> maps = Selector.selectFrom(Lists.list(new Selector[] { selector }), this.results, this.fields);
/* 221:    */     
/* 222:246 */     String keyName = selector.getName();
/* 223:247 */     for (int index = 0; index < values.length; index++)
/* 224:    */     {
/* 225:248 */       Map<String, Object> map = (Map)maps.get(index);
/* 226:249 */       values[index] = Conversions.toDouble(map.get(keyName));
/* 227:    */     }
/* 228:251 */     return values;
/* 229:    */   }
/* 230:    */   
/* 231:    */   public byte[] selectBytes(Selector selector)
/* 232:    */   {
/* 233:256 */     prepareResults();
/* 234:    */     
/* 235:258 */     byte[] values = new byte[this.results.size()];
/* 236:    */     
/* 237:260 */     List<Map<String, Object>> maps = Selector.selectFrom(Lists.list(new Selector[] { selector }), this.results, this.fields);
/* 238:    */     
/* 239:262 */     String keyName = selector.getName();
/* 240:263 */     for (int index = 0; index < values.length; index++)
/* 241:    */     {
/* 242:264 */       Map<String, Object> map = (Map)maps.get(index);
/* 243:265 */       values[index] = Conversions.toByte(map.get(keyName));
/* 244:    */     }
/* 245:267 */     return values;
/* 246:    */   }
/* 247:    */   
/* 248:    */   public char[] selectChars(Selector selector)
/* 249:    */   {
/* 250:272 */     prepareResults();
/* 251:    */     
/* 252:274 */     char[] values = new char[this.results.size()];
/* 253:    */     
/* 254:276 */     List<Map<String, Object>> maps = Selector.selectFrom(Lists.list(new Selector[] { selector }), this.results, this.fields);
/* 255:    */     
/* 256:278 */     String keyName = selector.getName();
/* 257:279 */     for (int index = 0; index < values.length; index++)
/* 258:    */     {
/* 259:280 */       Map<String, Object> map = (Map)maps.get(index);
/* 260:281 */       values[index] = Conversions.toChar(map.get(keyName));
/* 261:    */     }
/* 262:283 */     return values;
/* 263:    */   }
/* 264:    */   
/* 265:    */   public Object[] selectObjects(Selector selector)
/* 266:    */   {
/* 267:288 */     prepareResults();
/* 268:    */     
/* 269:290 */     Object[] values = new Object[this.results.size()];
/* 270:    */     
/* 271:292 */     List<Map<String, Object>> maps = Selector.selectFrom(Lists.list(new Selector[] { selector }), this.results, this.fields);
/* 272:    */     
/* 273:294 */     String keyName = selector.getName();
/* 274:295 */     for (int index = 0; index < values.length; index++)
/* 275:    */     {
/* 276:296 */       Map<String, Object> map = (Map)maps.get(index);
/* 277:297 */       values[index] = map.get(keyName);
/* 278:    */     }
/* 279:299 */     return values;
/* 280:    */   }
/* 281:    */   
/* 282:    */   public <OBJ> OBJ[] selectObjects(Class<OBJ> cls, Selector selector)
/* 283:    */   {
/* 284:304 */     prepareResults();
/* 285:    */     
/* 286:306 */     Object values = Array.newInstance(cls, this.results.size());
/* 287:    */     
/* 288:308 */     List<Map<String, Object>> maps = Selector.selectFrom(Lists.list(new Selector[] { selector }), this.results, this.fields);
/* 289:    */     
/* 290:310 */     String keyName = selector.getName();
/* 291:311 */     for (int index = 0; index < this.results.size(); index++)
/* 292:    */     {
/* 293:312 */       Map<String, Object> map = (Map)maps.get(index);
/* 294:313 */       BeanUtils.idx(values, index, map.get(keyName));
/* 295:    */     }
/* 296:315 */     return (Object[])values;
/* 297:    */   }
/* 298:    */   
/* 299:    */   public <OBJ> ResultSet<OBJ> selectObjectsAsResultSet(Class<OBJ> cls, Selector selector)
/* 300:    */   {
/* 301:321 */     prepareResults();
/* 302:    */     
/* 303:323 */     Object values = Array.newInstance(cls, this.results.size());
/* 304:    */     
/* 305:325 */     List<Map<String, Object>> maps = Selector.selectFrom(Lists.list(new Selector[] { selector }), this.results, this.fields);
/* 306:    */     
/* 307:327 */     String keyName = selector.getName();
/* 308:328 */     for (int index = 0; index < this.results.size(); index++)
/* 309:    */     {
/* 310:329 */       Map<String, Object> map = (Map)maps.get(index);
/* 311:330 */       BeanUtils.idx(values, index, map.get(keyName));
/* 312:    */     }
/* 313:332 */     OBJ[] array = (Object[])values;
/* 314:333 */     List list = new ArrayList(Arrays.asList(array));
/* 315:334 */     return new ResultSetImpl(list);
/* 316:    */   }
/* 317:    */   
/* 318:    */   public Collection<T> asCollection()
/* 319:    */   {
/* 320:339 */     prepareResults();
/* 321:    */     
/* 322:341 */     return this.results;
/* 323:    */   }
/* 324:    */   
/* 325:    */   public String asJSONString()
/* 326:    */   {
/* 327:346 */     prepareResults();
/* 328:    */     
/* 329:348 */     throw new RuntimeException("NOT IMPLEMENTED");
/* 330:    */   }
/* 331:    */   
/* 332:    */   public List<Map<String, Object>> asListOfMaps()
/* 333:    */   {
/* 334:353 */     prepareResults();
/* 335:    */     
/* 336:    */ 
/* 337:356 */     List<Map<String, Object>> items = new ArrayList(this.results.size());
/* 338:357 */     for (T item : this.results) {
/* 339:358 */       items.add(MapObjectConversion.toMap(item));
/* 340:    */     }
/* 341:360 */     return items;
/* 342:    */   }
/* 343:    */   
/* 344:    */   public List<T> asList()
/* 345:    */   {
/* 346:366 */     prepareResults();
/* 347:    */     
/* 348:368 */     return this.results;
/* 349:    */   }
/* 350:    */   
/* 351:    */   public <G> List<G> asList(Class<G> cls)
/* 352:    */   {
/* 353:373 */     return asList();
/* 354:    */   }
/* 355:    */   
/* 356:    */   public Set<T> asSet()
/* 357:    */   {
/* 358:378 */     prepareResults();
/* 359:    */     
/* 360:380 */     return new HashSet(this.results);
/* 361:    */   }
/* 362:    */   
/* 363:    */   public List<PlanStep> queryPlan()
/* 364:    */   {
/* 365:385 */     throw new RuntimeException("NOT IMPLEMENTED");
/* 366:    */   }
/* 367:    */   
/* 368:    */   public T firstItem()
/* 369:    */   {
/* 370:390 */     prepareResults();
/* 371:    */     
/* 372:392 */     return this.results.get(0);
/* 373:    */   }
/* 374:    */   
/* 375:    */   public Map<String, Object> firstMap()
/* 376:    */   {
/* 377:397 */     prepareResults();
/* 378:398 */     return MapObjectConversion.toMap(firstItem());
/* 379:    */   }
/* 380:    */   
/* 381:    */   public String firstJSON()
/* 382:    */   {
/* 383:403 */     return Boon.toJson(firstItem());
/* 384:    */   }
/* 385:    */   
/* 386:    */   public int firstInt(Selector selector)
/* 387:    */   {
/* 388:408 */     prepareResults();
/* 389:    */     
/* 390:410 */     int[] values = new int[1];
/* 391:    */     
/* 392:412 */     List<Map<String, Object>> maps = Selector.selectFrom(Lists.list(new Selector[] { selector }), this.results, this.fields);
/* 393:    */     
/* 394:414 */     String keyName = selector.getAlias();
/* 395:415 */     for (int index = 0; (index < values.length) && (index < maps.size()); index++)
/* 396:    */     {
/* 397:416 */       Map<String, Object> map = (Map)maps.get(index);
/* 398:417 */       values[index] = Conversions.toInt(map.get(keyName));
/* 399:418 */       if (index == 1) {
/* 400:    */         break;
/* 401:    */       }
/* 402:    */     }
/* 403:422 */     return values[0];
/* 404:    */   }
/* 405:    */   
/* 406:    */   public float firstFloat(Selector selector)
/* 407:    */   {
/* 408:429 */     prepareResults();
/* 409:    */     
/* 410:431 */     float[] values = new float[1];
/* 411:    */     
/* 412:433 */     List<Map<String, Object>> maps = Selector.selectFrom(Lists.list(new Selector[] { selector }), this.results, this.fields);
/* 413:    */     
/* 414:435 */     String keyName = selector.getName();
/* 415:436 */     for (int index = 0; index < values.length; index++)
/* 416:    */     {
/* 417:437 */       Map<String, Object> map = (Map)maps.get(index);
/* 418:438 */       values[index] = Conversions.toFloat(map.get(keyName));
/* 419:439 */       if (index == 1) {
/* 420:    */         break;
/* 421:    */       }
/* 422:    */     }
/* 423:443 */     return values[1];
/* 424:    */   }
/* 425:    */   
/* 426:    */   public short firstShort(Selector selector)
/* 427:    */   {
/* 428:448 */     prepareResults();
/* 429:    */     
/* 430:450 */     short[] values = new short[1];
/* 431:    */     
/* 432:452 */     List<Map<String, Object>> maps = Selector.selectFrom(Lists.list(new Selector[] { selector }), this.results, this.fields);
/* 433:    */     
/* 434:454 */     String keyName = selector.getName();
/* 435:455 */     for (int index = 0; index < values.length; index++)
/* 436:    */     {
/* 437:456 */       Map<String, Object> map = (Map)maps.get(index);
/* 438:457 */       values[index] = Conversions.toShort(map.get(keyName));
/* 439:458 */       if (index == 1) {
/* 440:    */         break;
/* 441:    */       }
/* 442:    */     }
/* 443:462 */     return values[1];
/* 444:    */   }
/* 445:    */   
/* 446:    */   public double firstDouble(Selector selector)
/* 447:    */   {
/* 448:467 */     prepareResults();
/* 449:    */     
/* 450:469 */     double[] values = new double[1];
/* 451:    */     
/* 452:471 */     List<Map<String, Object>> maps = Selector.selectFrom(Lists.list(new Selector[] { selector }), this.results, this.fields);
/* 453:    */     
/* 454:473 */     String keyName = selector.getName();
/* 455:474 */     for (int index = 0; index < values.length; index++)
/* 456:    */     {
/* 457:475 */       Map<String, Object> map = (Map)maps.get(index);
/* 458:476 */       values[index] = Conversions.toDouble(map.get(keyName));
/* 459:477 */       if (index == 1) {
/* 460:    */         break;
/* 461:    */       }
/* 462:    */     }
/* 463:481 */     return values[1];
/* 464:    */   }
/* 465:    */   
/* 466:    */   public byte firstByte(Selector selector)
/* 467:    */   {
/* 468:486 */     prepareResults();
/* 469:    */     
/* 470:488 */     byte[] values = new byte[1];
/* 471:    */     
/* 472:490 */     List<Map<String, Object>> maps = Selector.selectFrom(Lists.list(new Selector[] { selector }), this.results, this.fields);
/* 473:    */     
/* 474:492 */     String keyName = selector.getName();
/* 475:493 */     for (int index = 0; index < values.length; index++)
/* 476:    */     {
/* 477:494 */       Map<String, Object> map = (Map)maps.get(index);
/* 478:495 */       values[index] = Conversions.toByte(map.get(keyName));
/* 479:496 */       if (index == 1) {
/* 480:    */         break;
/* 481:    */       }
/* 482:    */     }
/* 483:500 */     return values[1];
/* 484:    */   }
/* 485:    */   
/* 486:    */   public char firstChar(Selector selector)
/* 487:    */   {
/* 488:505 */     prepareResults();
/* 489:    */     
/* 490:507 */     char[] values = new char[1];
/* 491:    */     
/* 492:509 */     List<Map<String, Object>> maps = Selector.selectFrom(Lists.list(new Selector[] { selector }), this.results, this.fields);
/* 493:    */     
/* 494:511 */     String keyName = selector.getName();
/* 495:512 */     for (int index = 0; index < values.length; index++)
/* 496:    */     {
/* 497:513 */       Map<String, Object> map = (Map)maps.get(index);
/* 498:514 */       values[index] = Conversions.toChar(map.get(keyName));
/* 499:515 */       if (index == 1) {
/* 500:    */         break;
/* 501:    */       }
/* 502:    */     }
/* 503:519 */     return values[1];
/* 504:    */   }
/* 505:    */   
/* 506:    */   public Object firstObject(Selector selector)
/* 507:    */   {
/* 508:524 */     prepareResults();
/* 509:    */     
/* 510:526 */     Object[] values = new Object[1];
/* 511:    */     
/* 512:528 */     List<Map<String, Object>> maps = Selector.selectFrom(Lists.list(new Selector[] { selector }), this.results, this.fields);
/* 513:    */     
/* 514:530 */     String keyName = selector.getName();
/* 515:531 */     for (int index = 0; index < values.length; index++)
/* 516:    */     {
/* 517:532 */       Map<String, Object> map = (Map)maps.get(index);
/* 518:533 */       values[index] = map.get(keyName);
/* 519:534 */       if (index == 1) {
/* 520:    */         break;
/* 521:    */       }
/* 522:    */     }
/* 523:538 */     return values[1];
/* 524:    */   }
/* 525:    */   
/* 526:    */   public <OBJ> OBJ firstObject(Class<OBJ> cls, Selector selector)
/* 527:    */   {
/* 528:543 */     prepareResults();
/* 529:    */     
/* 530:545 */     Object[] values = new Object[1];
/* 531:    */     
/* 532:547 */     List<Map<String, Object>> maps = Selector.selectFrom(Lists.list(new Selector[] { selector }), this.results, this.fields);
/* 533:    */     
/* 534:549 */     String keyName = selector.getName();
/* 535:550 */     for (int index = 0; index < values.length; index++)
/* 536:    */     {
/* 537:551 */       Map<String, Object> map = (Map)maps.get(index);
/* 538:552 */       values[index] = map.get(keyName);
/* 539:553 */       if (index == 1) {
/* 540:    */         break;
/* 541:    */       }
/* 542:    */     }
/* 543:557 */     return values[1];
/* 544:    */   }
/* 545:    */   
/* 546:    */   public List<T> paginate(int start, int size)
/* 547:    */   {
/* 548:562 */     prepareResults();
/* 549:    */     
/* 550:564 */     return this.results.subList(start, start + size);
/* 551:    */   }
/* 552:    */   
/* 553:    */   public List<Map<String, Object>> paginateMaps(int start, int size)
/* 554:    */   {
/* 555:569 */     prepareResults();
/* 556:    */     
/* 557:571 */     List<Map<String, Object>> mapResults = new ArrayList();
/* 558:572 */     List<T> list = paginate(start, size);
/* 559:574 */     for (T item : list) {
/* 560:575 */       mapResults.add(MapObjectConversion.toMap(item));
/* 561:    */     }
/* 562:578 */     return mapResults;
/* 563:    */   }
/* 564:    */   
/* 565:    */   public String paginateJSON(int start, int size)
/* 566:    */   {
/* 567:583 */     prepareResults();
/* 568:    */     
/* 569:585 */     throw new RuntimeException("NOT IMPLEMENTED");
/* 570:    */   }
/* 571:    */   
/* 572:    */   public int size()
/* 573:    */   {
/* 574:590 */     if (this.results != null) {
/* 575:591 */       return this.results.size();
/* 576:    */     }
/* 577:593 */     return this.totalSize;
/* 578:    */   }
/* 579:    */   
/* 580:    */   public Iterator<T> iterator()
/* 581:    */   {
/* 582:599 */     prepareResults();
/* 583:600 */     return this.results.iterator();
/* 584:    */   }
/* 585:    */   
/* 586:    */   public int lastSize()
/* 587:    */   {
/* 588:605 */     if (this.lastList == null) {
/* 589:606 */       return 0;
/* 590:    */     }
/* 591:608 */     return this.lastList.size();
/* 592:    */   }
/* 593:    */   
/* 594:    */   public void andResults()
/* 595:    */   {
/* 596:615 */     int size = 2147483647;
/* 597:616 */     List<T> finalResult = Collections.emptyList();
/* 598:618 */     for (List<T> result : this.allResults)
/* 599:    */     {
/* 600:620 */       if (result.size() == 0)
/* 601:    */       {
/* 602:621 */         finalResult = Collections.emptyList();
/* 603:622 */         size = 0;
/* 604:623 */         break;
/* 605:    */       }
/* 606:625 */       if (result.size() < size) {
/* 607:626 */         size = result.size();
/* 608:    */       }
/* 609:    */     }
/* 610:631 */     for (List<T> result : this.allResults) {
/* 611:633 */       if (result.size() == size)
/* 612:    */       {
/* 613:634 */         finalResult = result;
/* 614:635 */         break;
/* 615:    */       }
/* 616:    */     }
/* 617:639 */     this.allResults.clear();
/* 618:640 */     this.allResults.add(finalResult);
/* 619:    */   }
/* 620:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.ResultSetImpl
 * JD-Core Version:    0.7.0.1
 */