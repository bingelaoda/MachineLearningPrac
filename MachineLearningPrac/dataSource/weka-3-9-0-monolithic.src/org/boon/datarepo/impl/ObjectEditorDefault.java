/*   1:    */ package org.boon.datarepo.impl;
/*   2:    */ 
/*   3:    */ import java.util.Collection;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.LinkedHashMap;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.Map;
/*   8:    */ import java.util.logging.Level;
/*   9:    */ import java.util.logging.Logger;
/*  10:    */ import org.boon.core.reflection.BeanUtils;
/*  11:    */ import org.boon.core.reflection.fields.FieldAccess;
/*  12:    */ import org.boon.criteria.Update;
/*  13:    */ import org.boon.datarepo.DataRepoException;
/*  14:    */ import org.boon.datarepo.ObjectEditor;
/*  15:    */ import org.boon.datarepo.SearchableCollection;
/*  16:    */ import org.boon.datarepo.spi.ObjectEditorComposer;
/*  17:    */ 
/*  18:    */ public class ObjectEditorDefault<KEY, ITEM>
/*  19:    */   implements ObjectEditorComposer<KEY, ITEM>, ObjectEditor<KEY, ITEM>
/*  20:    */ {
/*  21: 46 */   private Logger log = Logger.getLogger(ObjectEditorDefault.class.getName());
/*  22:    */   protected SearchableCollection<KEY, ITEM> query;
/*  23: 49 */   protected Map<String, FieldAccess> fields = new LinkedHashMap();
/*  24:    */   private boolean hashCodeOptimization;
/*  25:    */   private boolean lookupAndExcept;
/*  26:    */   
/*  27:    */   public void put(ITEM item)
/*  28:    */   {
/*  29: 57 */     if (!add(item)) {
/*  30: 58 */       throw new DataRepoException("Unable to addObject item " + item);
/*  31:    */     }
/*  32:    */   }
/*  33:    */   
/*  34:    */   public void removeByKey(KEY key)
/*  35:    */   {
/*  36: 63 */     this.query.removeByKey(key);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void removeAll(ITEM... items)
/*  40:    */   {
/*  41: 67 */     for (ITEM item : items) {
/*  42: 68 */       delete(item);
/*  43:    */     }
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void removeAllAsync(Collection<ITEM> items)
/*  47:    */   {
/*  48: 73 */     for (ITEM item : items) {
/*  49: 74 */       delete(item);
/*  50:    */     }
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void addAll(ITEM... items)
/*  54:    */   {
/*  55: 79 */     for (ITEM item : items) {
/*  56: 80 */       add(item);
/*  57:    */     }
/*  58:    */   }
/*  59:    */   
/*  60:    */   public void addAllAsync(Collection<ITEM> items)
/*  61:    */   {
/*  62: 86 */     this.query.addAll(items);
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void modifyAll(ITEM... items)
/*  66:    */   {
/*  67: 90 */     for (ITEM item : items) {
/*  68: 91 */       modify(item);
/*  69:    */     }
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void modifyAll(Collection<ITEM> items)
/*  73:    */   {
/*  74: 96 */     for (ITEM item : items) {
/*  75: 97 */       modify(item);
/*  76:    */     }
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void modify(ITEM item)
/*  80:    */   {
/*  81:104 */     KEY key = this.query.getKey(item);
/*  82:105 */     ITEM oldItem = doGet(key);
/*  83:107 */     if (oldItem != null) {
/*  84:108 */       delete(oldItem);
/*  85:    */     } else {
/*  86:110 */       this.log.warning(String.format("An original item was not in the repo %s", new Object[] { item }));
/*  87:    */     }
/*  88:113 */     this.query.validateIndexes(item);
/*  89:115 */     if (this.log.isLoggable(Level.FINE)) {
/*  90:116 */       this.log.fine(String.format("This item %s was modified like this %s", new Object[] { oldItem, item }));
/*  91:    */     }
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void update(ITEM item)
/*  95:    */   {
/*  96:124 */     KEY key = this.query.getKey(item);
/*  97:125 */     ITEM oldItem = doGet(key);
/*  98:127 */     if (oldItem == null) {
/*  99:128 */       throw new DataRepoException("Unable to perform update, the object does not exist");
/* 100:    */     }
/* 101:130 */     this.query.delete(oldItem);
/* 102:131 */     this.query.validateIndexes(item);
/* 103:133 */     if (this.log.isLoggable(Level.FINE)) {
/* 104:134 */       this.log.fine(String.format("This item %s was modified like this %s", new Object[] { oldItem, item }));
/* 105:    */     }
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void modify(ITEM item, String property, Object value)
/* 109:    */   {
/* 110:140 */     item = lookupAndExpect(item);
/* 111:141 */     this.query.invalidateIndex(property, item);
/* 112:142 */     ((FieldAccess)this.fields.get(property)).setObject(item, value);
/* 113:143 */     optimizeHash(item);
/* 114:144 */     this.query.validateIndex(property, item);
/* 115:    */   }
/* 116:    */   
/* 117:    */   private void optimizeHash(ITEM item)
/* 118:    */   {
/* 119:148 */     if (!this.hashCodeOptimization) {
/* 120:149 */       return;
/* 121:    */     }
/* 122:151 */     FieldAccess hashCode = (FieldAccess)this.fields.get("_hashCode");
/* 123:152 */     if (hashCode == null) {
/* 124:153 */       return;
/* 125:    */     }
/* 126:155 */     hashCode.setInt(item, -1);
/* 127:156 */     hashCode.setInt(item, item.hashCode());
/* 128:    */   }
/* 129:    */   
/* 130:    */   public void modifyByValue(ITEM item, String property, String value)
/* 131:    */   {
/* 132:161 */     item = lookupAndExpect(item);
/* 133:162 */     this.query.invalidateIndex(property, item);
/* 134:163 */     ((FieldAccess)this.fields.get(property)).setValue(item, value);
/* 135:164 */     optimizeHash(item);
/* 136:165 */     this.query.validateIndex(property, item);
/* 137:    */   }
/* 138:    */   
/* 139:    */   public void modify(ITEM item, String property, int value)
/* 140:    */   {
/* 141:170 */     item = lookupAndExpect(item);
/* 142:171 */     this.query.invalidateIndex(property, item);
/* 143:172 */     ((FieldAccess)this.fields.get(property)).setInt(item, value);
/* 144:173 */     optimizeHash(item);
/* 145:174 */     this.query.validateIndex(property, item);
/* 146:    */   }
/* 147:    */   
/* 148:    */   public void modify(ITEM item, String property, long value)
/* 149:    */   {
/* 150:178 */     item = lookupAndExpect(item);
/* 151:179 */     this.query.invalidateIndex(property, item);
/* 152:180 */     ((FieldAccess)this.fields.get(property)).setLong(item, value);
/* 153:181 */     optimizeHash(item);
/* 154:182 */     this.query.validateIndex(property, item);
/* 155:    */   }
/* 156:    */   
/* 157:    */   public void modify(ITEM item, String property, char value)
/* 158:    */   {
/* 159:186 */     item = lookupAndExpect(item);
/* 160:187 */     this.query.invalidateIndex(property, item);
/* 161:188 */     ((FieldAccess)this.fields.get(property)).setChar(item, value);
/* 162:189 */     optimizeHash(item);
/* 163:190 */     this.query.validateIndex(property, item);
/* 164:    */   }
/* 165:    */   
/* 166:    */   public void modify(ITEM item, String property, short value)
/* 167:    */   {
/* 168:194 */     item = lookupAndExpect(item);
/* 169:195 */     this.query.invalidateIndex(property, item);
/* 170:196 */     ((FieldAccess)this.fields.get(property)).setShort(item, value);
/* 171:197 */     optimizeHash(item);
/* 172:198 */     this.query.validateIndex(property, item);
/* 173:    */   }
/* 174:    */   
/* 175:    */   public void modify(ITEM item, String property, byte value)
/* 176:    */   {
/* 177:202 */     item = lookupAndExpect(item);
/* 178:203 */     this.query.invalidateIndex(property, item);
/* 179:204 */     ((FieldAccess)this.fields.get(property)).setByte(item, value);
/* 180:205 */     optimizeHash(item);
/* 181:206 */     this.query.validateIndex(property, item);
/* 182:    */   }
/* 183:    */   
/* 184:    */   public void modify(ITEM item, String property, float value)
/* 185:    */   {
/* 186:210 */     item = lookupAndExpect(item);
/* 187:211 */     this.query.invalidateIndex(property, item);
/* 188:212 */     ((FieldAccess)this.fields.get(property)).setFloat(item, value);
/* 189:213 */     optimizeHash(item);
/* 190:214 */     this.query.validateIndex(property, item);
/* 191:    */   }
/* 192:    */   
/* 193:    */   public void modify(ITEM item, String property, double value)
/* 194:    */   {
/* 195:218 */     item = lookupAndExpect(item);
/* 196:219 */     this.query.invalidateIndex(property, item);
/* 197:220 */     ((FieldAccess)this.fields.get(property)).setDouble(item, value);
/* 198:221 */     optimizeHash(item);
/* 199:222 */     this.query.validateIndex(property, item);
/* 200:    */   }
/* 201:    */   
/* 202:    */   public void modify(ITEM item, Update... values)
/* 203:    */   {
/* 204:226 */     item = lookupAndExpect(item);
/* 205:227 */     for (Update value : values)
/* 206:    */     {
/* 207:228 */       this.query.invalidateIndex(value.getName(), item);
/* 208:229 */       value.doSet(this, item);
/* 209:230 */       optimizeHash(item);
/* 210:231 */       this.query.validateIndex(value.getName(), item);
/* 211:    */     }
/* 212:    */   }
/* 213:    */   
/* 214:    */   public void update(KEY key, String property, Object value)
/* 215:    */   {
/* 216:237 */     ITEM item = lookupAndExpectByKey(key);
/* 217:238 */     this.query.invalidateIndex(property, item);
/* 218:239 */     FieldAccess fieldAccess = (FieldAccess)this.fields.get(property);
/* 219:240 */     if (fieldAccess == null) {
/* 220:241 */       BeanUtils.idx(item, property, value);
/* 221:    */     } else {
/* 222:243 */       fieldAccess.setObject(item, value);
/* 223:    */     }
/* 224:245 */     optimizeHash(item);
/* 225:246 */     this.query.validateIndex(property, item);
/* 226:    */   }
/* 227:    */   
/* 228:    */   public void updateByValue(KEY key, String property, String value)
/* 229:    */   {
/* 230:250 */     ITEM item = lookupAndExpectByKey(key);
/* 231:251 */     this.query.invalidateIndex(property, item);
/* 232:252 */     FieldAccess fieldAccess = (FieldAccess)this.fields.get(property);
/* 233:253 */     if (fieldAccess == null) {
/* 234:254 */       BeanUtils.idx(item, property, value);
/* 235:    */     } else {
/* 236:256 */       fieldAccess.setValue(item, value);
/* 237:    */     }
/* 238:259 */     optimizeHash(item);
/* 239:260 */     this.query.validateIndex(property, item);
/* 240:    */   }
/* 241:    */   
/* 242:    */   public void update(KEY key, String property, int value)
/* 243:    */   {
/* 244:264 */     ITEM item = lookupAndExpectByKey(key);
/* 245:265 */     this.query.invalidateIndex(property, item);
/* 246:    */     
/* 247:267 */     FieldAccess fieldAccess = (FieldAccess)this.fields.get(property);
/* 248:268 */     if (fieldAccess == null) {
/* 249:269 */       BeanUtils.idx(item, property, Integer.valueOf(value));
/* 250:    */     } else {
/* 251:271 */       fieldAccess.setInt(item, value);
/* 252:    */     }
/* 253:274 */     optimizeHash(item);
/* 254:275 */     this.query.validateIndex(property, item);
/* 255:    */   }
/* 256:    */   
/* 257:    */   public void update(KEY key, String property, long value)
/* 258:    */   {
/* 259:279 */     ITEM item = lookupAndExpectByKey(key);
/* 260:280 */     this.query.invalidateIndex(property, item);
/* 261:    */     
/* 262:    */ 
/* 263:283 */     FieldAccess fieldAccess = (FieldAccess)this.fields.get(property);
/* 264:284 */     if (fieldAccess == null) {
/* 265:285 */       BeanUtils.idx(item, property, Long.valueOf(value));
/* 266:    */     } else {
/* 267:287 */       fieldAccess.setLong(item, value);
/* 268:    */     }
/* 269:289 */     optimizeHash(item);
/* 270:290 */     this.query.validateIndex(property, item);
/* 271:    */   }
/* 272:    */   
/* 273:    */   public void update(KEY key, String property, char value)
/* 274:    */   {
/* 275:294 */     ITEM item = lookupAndExpectByKey(key);
/* 276:295 */     this.query.invalidateIndex(property, item);
/* 277:    */     
/* 278:    */ 
/* 279:298 */     FieldAccess fieldAccess = (FieldAccess)this.fields.get(property);
/* 280:299 */     if (fieldAccess == null) {
/* 281:300 */       BeanUtils.idx(item, property, Character.valueOf(value));
/* 282:    */     } else {
/* 283:302 */       fieldAccess.setChar(item, value);
/* 284:    */     }
/* 285:304 */     optimizeHash(item);
/* 286:305 */     this.query.validateIndex(property, item);
/* 287:    */   }
/* 288:    */   
/* 289:    */   public void update(KEY key, String property, short value)
/* 290:    */   {
/* 291:309 */     ITEM item = lookupAndExpectByKey(key);
/* 292:310 */     this.query.invalidateIndex(property, item);
/* 293:    */     
/* 294:    */ 
/* 295:313 */     FieldAccess fieldAccess = (FieldAccess)this.fields.get(property);
/* 296:314 */     if (fieldAccess == null) {
/* 297:315 */       BeanUtils.idx(item, property, Short.valueOf(value));
/* 298:    */     } else {
/* 299:317 */       fieldAccess.setShort(item, value);
/* 300:    */     }
/* 301:320 */     optimizeHash(item);
/* 302:321 */     this.query.validateIndex(property, item);
/* 303:    */   }
/* 304:    */   
/* 305:    */   public void update(KEY key, String property, byte value)
/* 306:    */   {
/* 307:325 */     ITEM item = lookupAndExpectByKey(key);
/* 308:326 */     this.query.invalidateIndex(property, item);
/* 309:    */     
/* 310:    */ 
/* 311:329 */     FieldAccess fieldAccess = (FieldAccess)this.fields.get(property);
/* 312:330 */     if (fieldAccess == null) {
/* 313:331 */       BeanUtils.idx(item, property, Byte.valueOf(value));
/* 314:    */     } else {
/* 315:333 */       fieldAccess.setByte(item, value);
/* 316:    */     }
/* 317:336 */     optimizeHash(item);
/* 318:337 */     this.query.validateIndex(property, item);
/* 319:    */   }
/* 320:    */   
/* 321:    */   public void update(KEY key, String property, float value)
/* 322:    */   {
/* 323:341 */     ITEM item = lookupAndExpectByKey(key);
/* 324:342 */     this.query.invalidateIndex(property, item);
/* 325:    */     
/* 326:    */ 
/* 327:345 */     FieldAccess fieldAccess = (FieldAccess)this.fields.get(property);
/* 328:346 */     if (fieldAccess == null) {
/* 329:347 */       BeanUtils.idx(item, property, Float.valueOf(value));
/* 330:    */     } else {
/* 331:349 */       fieldAccess.setFloat(item, value);
/* 332:    */     }
/* 333:352 */     optimizeHash(item);
/* 334:353 */     this.query.validateIndex(property, item);
/* 335:    */   }
/* 336:    */   
/* 337:    */   public void update(KEY key, String property, double value)
/* 338:    */   {
/* 339:357 */     ITEM item = lookupAndExpectByKey(key);
/* 340:358 */     this.query.invalidateIndex(property, item);
/* 341:    */     
/* 342:    */ 
/* 343:361 */     FieldAccess fieldAccess = (FieldAccess)this.fields.get(property);
/* 344:362 */     if (fieldAccess == null) {
/* 345:363 */       BeanUtils.idx(item, property, Double.valueOf(value));
/* 346:    */     } else {
/* 347:365 */       fieldAccess.setDouble(item, value);
/* 348:    */     }
/* 349:368 */     optimizeHash(item);
/* 350:369 */     this.query.validateIndex(property, item);
/* 351:    */   }
/* 352:    */   
/* 353:    */   public void update(KEY key, Update... values)
/* 354:    */   {
/* 355:373 */     ITEM item = lookupAndExpectByKey(key);
/* 356:375 */     for (Update value : values)
/* 357:    */     {
/* 358:376 */       this.query.invalidateIndex(value.getName(), item);
/* 359:377 */       value.doSet(this, item);
/* 360:378 */       optimizeHash(item);
/* 361:379 */       this.query.validateIndex(value.getName(), item);
/* 362:    */     }
/* 363:    */   }
/* 364:    */   
/* 365:    */   public boolean compareAndUpdate(KEY key, String property, Object compare, Object value)
/* 366:    */   {
/* 367:384 */     ITEM item = lookupAndExpectByKey(key);
/* 368:385 */     FieldAccess field = (FieldAccess)this.fields.get(property);
/* 369:386 */     boolean set = false;
/* 370:387 */     if (field.getObject(item).equals(compare))
/* 371:    */     {
/* 372:388 */       this.query.invalidateIndex(property, item);
/* 373:389 */       field.setObject(item, value);
/* 374:390 */       set = true;
/* 375:391 */       optimizeHash(item);
/* 376:392 */       this.query.validateIndex(property, item);
/* 377:    */     }
/* 378:394 */     return set;
/* 379:    */   }
/* 380:    */   
/* 381:    */   public boolean compareAndUpdate(KEY key, String property, int compare, int value)
/* 382:    */   {
/* 383:398 */     ITEM item = lookupAndExpectByKey(key);
/* 384:399 */     FieldAccess field = (FieldAccess)this.fields.get(property);
/* 385:400 */     boolean set = false;
/* 386:401 */     if (field.getInt(item) == compare)
/* 387:    */     {
/* 388:402 */       this.query.invalidateIndex(property, item);
/* 389:403 */       field.setInt(item, value);
/* 390:404 */       set = true;
/* 391:405 */       optimizeHash(item);
/* 392:406 */       this.query.validateIndex(property, item);
/* 393:    */     }
/* 394:408 */     return set;
/* 395:    */   }
/* 396:    */   
/* 397:    */   public boolean compareAndUpdate(KEY key, String property, long compare, long value)
/* 398:    */   {
/* 399:412 */     ITEM item = lookupAndExpectByKey(key);
/* 400:413 */     FieldAccess field = (FieldAccess)this.fields.get(property);
/* 401:414 */     boolean set = false;
/* 402:415 */     if (field.getLong(item) == compare)
/* 403:    */     {
/* 404:416 */       this.query.invalidateIndex(property, item);
/* 405:417 */       field.setLong(item, value);
/* 406:418 */       set = true;
/* 407:419 */       optimizeHash(item);
/* 408:420 */       this.query.validateIndex(property, item);
/* 409:    */     }
/* 410:422 */     return set;
/* 411:    */   }
/* 412:    */   
/* 413:    */   public boolean compareAndUpdate(KEY key, String property, char compare, char value)
/* 414:    */   {
/* 415:426 */     ITEM item = lookupAndExpectByKey(key);
/* 416:427 */     FieldAccess field = (FieldAccess)this.fields.get(property);
/* 417:428 */     boolean set = false;
/* 418:429 */     if (field.getChar(item) == compare)
/* 419:    */     {
/* 420:430 */       this.query.invalidateIndex(property, item);
/* 421:431 */       field.setChar(item, value);
/* 422:432 */       set = true;
/* 423:433 */       optimizeHash(item);
/* 424:434 */       this.query.validateIndex(property, item);
/* 425:    */     }
/* 426:436 */     return set;
/* 427:    */   }
/* 428:    */   
/* 429:    */   public boolean compareAndUpdate(KEY key, String property, short compare, short value)
/* 430:    */   {
/* 431:440 */     ITEM item = lookupAndExpectByKey(key);
/* 432:441 */     FieldAccess field = (FieldAccess)this.fields.get(property);
/* 433:442 */     boolean set = false;
/* 434:443 */     if (field.getShort(item) == compare)
/* 435:    */     {
/* 436:444 */       this.query.invalidateIndex(property, item);
/* 437:445 */       field.setShort(item, value);
/* 438:446 */       set = true;
/* 439:447 */       optimizeHash(item);
/* 440:448 */       this.query.validateIndex(property, item);
/* 441:    */     }
/* 442:450 */     return set;
/* 443:    */   }
/* 444:    */   
/* 445:    */   public boolean compareAndUpdate(KEY key, String property, byte compare, byte value)
/* 446:    */   {
/* 447:455 */     ITEM item = lookupAndExpectByKey(key);
/* 448:456 */     FieldAccess field = (FieldAccess)this.fields.get(property);
/* 449:457 */     boolean set = false;
/* 450:458 */     if (field.getByte(item) == compare)
/* 451:    */     {
/* 452:459 */       this.query.invalidateIndex(property, item);
/* 453:460 */       field.setByte(item, value);
/* 454:461 */       set = true;
/* 455:462 */       optimizeHash(item);
/* 456:463 */       this.query.validateIndex(property, item);
/* 457:    */     }
/* 458:465 */     return set;
/* 459:    */   }
/* 460:    */   
/* 461:    */   public boolean compareAndUpdate(KEY key, String property, float compare, float value)
/* 462:    */   {
/* 463:469 */     ITEM item = lookupAndExpectByKey(key);
/* 464:470 */     FieldAccess field = (FieldAccess)this.fields.get(property);
/* 465:471 */     boolean set = false;
/* 466:472 */     if (field.getFloat(item) == compare)
/* 467:    */     {
/* 468:473 */       this.query.invalidateIndex(property, item);
/* 469:474 */       field.setFloat(item, value);
/* 470:475 */       set = true;
/* 471:476 */       optimizeHash(item);
/* 472:477 */       this.query.validateIndex(property, item);
/* 473:    */     }
/* 474:479 */     return set;
/* 475:    */   }
/* 476:    */   
/* 477:    */   public boolean compareAndUpdate(KEY key, String property, double compare, double value)
/* 478:    */   {
/* 479:483 */     ITEM item = lookupAndExpectByKey(key);
/* 480:484 */     FieldAccess field = (FieldAccess)this.fields.get(property);
/* 481:485 */     boolean set = false;
/* 482:486 */     if (field.getDouble(item) == compare)
/* 483:    */     {
/* 484:487 */       this.query.invalidateIndex(property, item);
/* 485:488 */       field.setDouble(item, value);
/* 486:489 */       set = true;
/* 487:490 */       optimizeHash(item);
/* 488:491 */       this.query.validateIndex(property, item);
/* 489:    */     }
/* 490:493 */     return set;
/* 491:    */   }
/* 492:    */   
/* 493:    */   public boolean compareAndIncrement(KEY key, String property, int compare)
/* 494:    */   {
/* 495:497 */     ITEM item = lookupAndExpectByKey(key);
/* 496:498 */     FieldAccess field = (FieldAccess)this.fields.get(property);
/* 497:499 */     boolean set = false;
/* 498:500 */     if (field.getInt(item) == compare)
/* 499:    */     {
/* 500:501 */       this.query.invalidateIndex(property, item);
/* 501:502 */       field.setInt(item, compare + 1);
/* 502:503 */       set = true;
/* 503:504 */       optimizeHash(item);
/* 504:505 */       this.query.validateIndex(property, item);
/* 505:    */     }
/* 506:507 */     return set;
/* 507:    */   }
/* 508:    */   
/* 509:    */   public boolean compareAndIncrement(KEY key, String property, long compare)
/* 510:    */   {
/* 511:512 */     ITEM item = lookupAndExpectByKey(key);
/* 512:513 */     FieldAccess field = (FieldAccess)this.fields.get(property);
/* 513:514 */     boolean set = false;
/* 514:515 */     if (field.getLong(item) == compare)
/* 515:    */     {
/* 516:516 */       this.query.invalidateIndex(property, item);
/* 517:517 */       field.setLong(item, compare + 1L);
/* 518:518 */       set = true;
/* 519:519 */       optimizeHash(item);
/* 520:520 */       this.query.validateIndex(property, item);
/* 521:    */     }
/* 522:522 */     return set;
/* 523:    */   }
/* 524:    */   
/* 525:    */   public boolean compareAndIncrement(KEY key, String property, short compare)
/* 526:    */   {
/* 527:526 */     ITEM item = lookupAndExpectByKey(key);
/* 528:527 */     FieldAccess field = (FieldAccess)this.fields.get(property);
/* 529:528 */     boolean set = false;
/* 530:529 */     if (field.getShort(item) == compare)
/* 531:    */     {
/* 532:530 */       this.query.invalidateIndex(property, item);
/* 533:531 */       field.setShort(item, (short)(compare + 1));
/* 534:532 */       set = true;
/* 535:533 */       optimizeHash(item);
/* 536:534 */       this.query.validateIndex(property, item);
/* 537:    */     }
/* 538:536 */     return set;
/* 539:    */   }
/* 540:    */   
/* 541:    */   public boolean compareAndIncrement(KEY key, String property, byte compare)
/* 542:    */   {
/* 543:540 */     ITEM item = lookupAndExpectByKey(key);
/* 544:541 */     FieldAccess field = (FieldAccess)this.fields.get(property);
/* 545:542 */     boolean set = false;
/* 546:543 */     if (field.getByte(item) == compare)
/* 547:    */     {
/* 548:544 */       this.query.invalidateIndex(property, item);
/* 549:545 */       field.setByte(item, (byte)(compare + 1));
/* 550:546 */       set = true;
/* 551:547 */       optimizeHash(item);
/* 552:548 */       this.query.validateIndex(property, item);
/* 553:    */     }
/* 554:550 */     return set;
/* 555:    */   }
/* 556:    */   
/* 557:    */   public void addAll(List<ITEM> items)
/* 558:    */   {
/* 559:555 */     this.query.addAll(items);
/* 560:    */   }
/* 561:    */   
/* 562:    */   public boolean add(ITEM item)
/* 563:    */   {
/* 564:560 */     return this.query.add(item);
/* 565:    */   }
/* 566:    */   
/* 567:    */   public void setFields(Map<String, FieldAccess> fields)
/* 568:    */   {
/* 569:566 */     if (fields != null) {
/* 570:567 */       this.fields = fields;
/* 571:    */     } else {
/* 572:569 */       fields = Collections.EMPTY_MAP;
/* 573:    */     }
/* 574:    */   }
/* 575:    */   
/* 576:    */   public void setSearchableCollection(SearchableCollection searchableCollection)
/* 577:    */   {
/* 578:576 */     this.query = searchableCollection;
/* 579:    */   }
/* 580:    */   
/* 581:    */   public void init() {}
/* 582:    */   
/* 583:    */   public void hashCodeOptimizationOn()
/* 584:    */   {
/* 585:585 */     this.hashCodeOptimization = true;
/* 586:    */   }
/* 587:    */   
/* 588:    */   public ITEM get(KEY key)
/* 589:    */   {
/* 590:590 */     return this.query.get(key);
/* 591:    */   }
/* 592:    */   
/* 593:    */   private ITEM doGet(KEY key)
/* 594:    */   {
/* 595:594 */     return this.query.get(key);
/* 596:    */   }
/* 597:    */   
/* 598:    */   public KEY getKey(ITEM item)
/* 599:    */   {
/* 600:599 */     return this.query.getKey(item);
/* 601:    */   }
/* 602:    */   
/* 603:    */   private ITEM lookupAndExpect(ITEM item)
/* 604:    */   {
/* 605:603 */     if (!this.lookupAndExcept) {
/* 606:604 */       return item;
/* 607:    */     }
/* 608:607 */     KEY key = getKey(item);
/* 609:608 */     ITEM oldItem = doGet(key);
/* 610:611 */     if (oldItem == null) {
/* 611:612 */       throw new IllegalStateException(String.format("An original item was not in the repo %s", new Object[] { item }));
/* 612:    */     }
/* 613:616 */     return oldItem;
/* 614:    */   }
/* 615:    */   
/* 616:    */   private ITEM lookupAndExpectByKey(KEY key)
/* 617:    */   {
/* 618:620 */     ITEM oldItem = doGet(key);
/* 619:622 */     if (oldItem == null) {
/* 620:624 */       throw new IllegalStateException(String.format("An original item was not in the repo at this key %s", new Object[] { key }));
/* 621:    */     }
/* 622:630 */     return oldItem;
/* 623:    */   }
/* 624:    */   
/* 625:    */   public void clear()
/* 626:    */   {
/* 627:636 */     this.query.clear();
/* 628:    */   }
/* 629:    */   
/* 630:    */   public boolean delete(ITEM item)
/* 631:    */   {
/* 632:641 */     return this.query.delete(item);
/* 633:    */   }
/* 634:    */   
/* 635:    */   public List<ITEM> all()
/* 636:    */   {
/* 637:647 */     return this.query.all();
/* 638:    */   }
/* 639:    */   
/* 640:    */   public int size()
/* 641:    */   {
/* 642:652 */     return this.query.size();
/* 643:    */   }
/* 644:    */   
/* 645:    */   public Collection<ITEM> toCollection()
/* 646:    */   {
/* 647:658 */     return this.query;
/* 648:    */   }
/* 649:    */   
/* 650:    */   public SearchableCollection<KEY, ITEM> query()
/* 651:    */   {
/* 652:663 */     return this.query;
/* 653:    */   }
/* 654:    */   
/* 655:    */   public Object readNestedValue(KEY key, String... properties)
/* 656:    */   {
/* 657:669 */     ITEM item = get(key);
/* 658:670 */     return BeanUtils.getPropertyValue(item, properties);
/* 659:    */   }
/* 660:    */   
/* 661:    */   public int readNestedInt(KEY key, String... properties)
/* 662:    */   {
/* 663:676 */     ITEM item = get(key);
/* 664:677 */     return BeanUtils.getPropertyInt(item, properties);
/* 665:    */   }
/* 666:    */   
/* 667:    */   public short readNestedShort(KEY key, String... properties)
/* 668:    */   {
/* 669:682 */     ITEM item = get(key);
/* 670:683 */     return BeanUtils.getPropertyShort(item, properties);
/* 671:    */   }
/* 672:    */   
/* 673:    */   public char readNestedChar(KEY key, String... properties)
/* 674:    */   {
/* 675:688 */     ITEM item = get(key);
/* 676:689 */     return BeanUtils.getPropertyChar(item, properties);
/* 677:    */   }
/* 678:    */   
/* 679:    */   public byte readNestedByte(KEY key, String... properties)
/* 680:    */   {
/* 681:694 */     ITEM item = get(key);
/* 682:695 */     return BeanUtils.getPropertyByte(item, properties);
/* 683:    */   }
/* 684:    */   
/* 685:    */   public double readNestedDouble(KEY key, String... properties)
/* 686:    */   {
/* 687:700 */     ITEM item = get(key);
/* 688:701 */     return BeanUtils.getPropertyDouble(item, properties);
/* 689:    */   }
/* 690:    */   
/* 691:    */   public float readNestedFloat(KEY key, String... properties)
/* 692:    */   {
/* 693:706 */     ITEM item = get(key);
/* 694:707 */     return BeanUtils.getPropertyFloat(item, properties);
/* 695:    */   }
/* 696:    */   
/* 697:    */   public long readNestedLong(KEY key, String... properties)
/* 698:    */   {
/* 699:712 */     ITEM item = get(key);
/* 700:713 */     return BeanUtils.getPropertyLong(item, properties);
/* 701:    */   }
/* 702:    */   
/* 703:    */   public Object readObject(KEY key, String property)
/* 704:    */   {
/* 705:719 */     ITEM item = get(key);
/* 706:720 */     return ((FieldAccess)this.fields.get(property)).getObject(item);
/* 707:    */   }
/* 708:    */   
/* 709:    */   public <T> T readValue(KEY key, String property, Class<T> type)
/* 710:    */   {
/* 711:726 */     ITEM item = get(key);
/* 712:727 */     return ((FieldAccess)this.fields.get(property)).getValue(item);
/* 713:    */   }
/* 714:    */   
/* 715:    */   public int readInt(KEY key, String property)
/* 716:    */   {
/* 717:732 */     ITEM item = get(key);
/* 718:733 */     return ((FieldAccess)this.fields.get(property)).getInt(item);
/* 719:    */   }
/* 720:    */   
/* 721:    */   public long readLong(KEY key, String property)
/* 722:    */   {
/* 723:738 */     ITEM item = get(key);
/* 724:739 */     return ((FieldAccess)this.fields.get(property)).getLong(item);
/* 725:    */   }
/* 726:    */   
/* 727:    */   public char readChar(KEY key, String property)
/* 728:    */   {
/* 729:744 */     ITEM item = get(key);
/* 730:745 */     return ((FieldAccess)this.fields.get(property)).getChar(item);
/* 731:    */   }
/* 732:    */   
/* 733:    */   public short readShort(KEY key, String property)
/* 734:    */   {
/* 735:750 */     ITEM item = get(key);
/* 736:751 */     return ((FieldAccess)this.fields.get(property)).getShort(item);
/* 737:    */   }
/* 738:    */   
/* 739:    */   public byte readByte(KEY key, String property)
/* 740:    */   {
/* 741:756 */     ITEM item = get(key);
/* 742:757 */     return ((FieldAccess)this.fields.get(property)).getByte(item);
/* 743:    */   }
/* 744:    */   
/* 745:    */   public float readFloat(KEY key, String property)
/* 746:    */   {
/* 747:762 */     ITEM item = get(key);
/* 748:763 */     return ((FieldAccess)this.fields.get(property)).getFloat(item);
/* 749:    */   }
/* 750:    */   
/* 751:    */   public double readDouble(KEY key, String property)
/* 752:    */   {
/* 753:769 */     ITEM item = get(key);
/* 754:770 */     return ((FieldAccess)this.fields.get(property)).getDouble(item);
/* 755:    */   }
/* 756:    */   
/* 757:    */   public Object getObject(ITEM item, String property)
/* 758:    */   {
/* 759:776 */     return ((FieldAccess)this.fields.get(property)).getObject(item);
/* 760:    */   }
/* 761:    */   
/* 762:    */   public <T> T getValue(ITEM item, String property, Class<T> type)
/* 763:    */   {
/* 764:781 */     return ((FieldAccess)this.fields.get(property)).getValue(item);
/* 765:    */   }
/* 766:    */   
/* 767:    */   public int getInt(ITEM item, String property)
/* 768:    */   {
/* 769:786 */     return ((FieldAccess)this.fields.get(property)).getInt(item);
/* 770:    */   }
/* 771:    */   
/* 772:    */   public long getLong(ITEM item, String property)
/* 773:    */   {
/* 774:791 */     return ((FieldAccess)this.fields.get(property)).getLong(item);
/* 775:    */   }
/* 776:    */   
/* 777:    */   public char getChar(ITEM item, String property)
/* 778:    */   {
/* 779:796 */     return ((FieldAccess)this.fields.get(property)).getChar(item);
/* 780:    */   }
/* 781:    */   
/* 782:    */   public short getShort(ITEM item, String property)
/* 783:    */   {
/* 784:801 */     return ((FieldAccess)this.fields.get(property)).getShort(item);
/* 785:    */   }
/* 786:    */   
/* 787:    */   public byte getByte(ITEM item, String property)
/* 788:    */   {
/* 789:806 */     return ((FieldAccess)this.fields.get(property)).getByte(item);
/* 790:    */   }
/* 791:    */   
/* 792:    */   public float getFloat(ITEM item, String property)
/* 793:    */   {
/* 794:811 */     return ((FieldAccess)this.fields.get(property)).getFloat(item);
/* 795:    */   }
/* 796:    */   
/* 797:    */   public double getDouble(ITEM item, String property)
/* 798:    */   {
/* 799:816 */     return ((FieldAccess)this.fields.get(property)).getDouble(item);
/* 800:    */   }
/* 801:    */   
/* 802:    */   public void setLookupAndExcept(boolean lookupAndExcept)
/* 803:    */   {
/* 804:821 */     this.lookupAndExcept = lookupAndExcept;
/* 805:    */   }
/* 806:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.datarepo.impl.ObjectEditorDefault
 * JD-Core Version:    0.7.0.1
 */