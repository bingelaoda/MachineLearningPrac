/*   1:    */ package org.boon.criteria;
/*   2:    */ 
/*   3:    */ import java.util.Collection;
/*   4:    */ import java.util.List;
/*   5:    */ import java.util.Map;
/*   6:    */ import org.boon.Lists;
/*   7:    */ import org.boon.Str;
/*   8:    */ import org.boon.core.Conversions;
/*   9:    */ import org.boon.core.Typ;
/*  10:    */ import org.boon.core.reflection.fields.FieldAccess;
/*  11:    */ 
/*  12:    */ public abstract class ProjectedSelector
/*  13:    */   extends Selector
/*  14:    */ {
/*  15:    */   public static List<ProjectedSelector> projections(ProjectedSelector... projections)
/*  16:    */   {
/*  17: 46 */     return Lists.list(projections);
/*  18:    */   }
/*  19:    */   
/*  20:    */   public static Selector max(String fieldName)
/*  21:    */   {
/*  22: 51 */     new Selector(fieldName, Str.join('.', new String[] { "max", fieldName }))
/*  23:    */     {
/*  24:    */       Comparable max;
/*  25:    */       
/*  26:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/*  27:    */       {
/*  28: 56 */         Comparable value = (Comparable)getPropertyValue(item, fields);
/*  29: 57 */         if (this.max == null) {
/*  30: 58 */           this.max = value;
/*  31:    */         }
/*  32: 61 */         if (value.compareTo(this.max) > 0) {
/*  33: 62 */           this.max = value;
/*  34:    */         }
/*  35:    */       }
/*  36:    */       
/*  37:    */       public void handleStart(Collection<?> results)
/*  38:    */       {
/*  39: 68 */         this.max = null;
/*  40:    */       }
/*  41:    */       
/*  42:    */       public void handleComplete(List<Map<String, Object>> rows)
/*  43:    */       {
/*  44: 73 */         if (rows.size() > 0) {
/*  45: 74 */           ((Map)rows.get(0)).put(this.alias, this.max);
/*  46:    */         }
/*  47:    */       }
/*  48:    */     };
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static Selector min(String fieldName)
/*  52:    */   {
/*  53: 82 */     new Selector(fieldName, Str.join('.', new String[] { "min", fieldName }))
/*  54:    */     {
/*  55:    */       Comparable min;
/*  56:    */       
/*  57:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/*  58:    */       {
/*  59: 87 */         Comparable value = (Comparable)getPropertyValue(item, fields);
/*  60: 89 */         if (this.min == null) {
/*  61: 90 */           this.min = value;
/*  62:    */         }
/*  63: 93 */         if (value.compareTo(this.min) < 0) {
/*  64: 94 */           this.min = value;
/*  65:    */         }
/*  66:    */       }
/*  67:    */       
/*  68:    */       public void handleStart(Collection<?> results)
/*  69:    */       {
/*  70:100 */         this.min = null;
/*  71:    */       }
/*  72:    */       
/*  73:    */       public void handleComplete(List<Map<String, Object>> rows)
/*  74:    */       {
/*  75:105 */         if (rows.size() > 0) {
/*  76:106 */           ((Map)rows.get(0)).put(this.alias, this.min);
/*  77:    */         }
/*  78:    */       }
/*  79:    */     };
/*  80:    */   }
/*  81:    */   
/*  82:    */   public static Selector sum(final String fieldName)
/*  83:    */   {
/*  84:114 */     new Selector(fieldName, Str.join('.', new String[] { "sum", fieldName }))
/*  85:    */     {
/*  86:115 */       long sum = 0L;
/*  87:    */       
/*  88:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/*  89:    */       {
/*  90:120 */         FieldAccess field = (FieldAccess)fields.get(fieldName);
/*  91:121 */         if (field.type() == Typ.intgr)
/*  92:    */         {
/*  93:122 */           int value = field.getInt(item);
/*  94:123 */           this.sum += value;
/*  95:    */         }
/*  96:    */         else
/*  97:    */         {
/*  98:126 */           Comparable value = (Comparable)getPropertyValue(item, fields);
/*  99:    */           
/* 100:128 */           Integer ovalue = Integer.valueOf(Conversions.toInt(value));
/* 101:129 */           this.sum += ovalue.intValue();
/* 102:    */         }
/* 103:    */       }
/* 104:    */       
/* 105:    */       public void handleStart(Collection<?> results)
/* 106:    */       {
/* 107:137 */         this.sum = -2147483648L;
/* 108:    */       }
/* 109:    */       
/* 110:    */       public void handleComplete(List<Map<String, Object>> rows)
/* 111:    */       {
/* 112:142 */         if (rows.size() > 0) {
/* 113:143 */           ((Map)rows.get(0)).put(this.alias, Long.valueOf(this.sum));
/* 114:    */         }
/* 115:    */       }
/* 116:    */     };
/* 117:    */   }
/* 118:    */   
/* 119:    */   public static Selector sumFloat(final String fieldName)
/* 120:    */   {
/* 121:150 */     new Selector(fieldName, Str.join('.', new String[] { "sum", fieldName }))
/* 122:    */     {
/* 123:151 */       double sum = 0.0D;
/* 124:    */       
/* 125:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 126:    */       {
/* 127:156 */         FieldAccess field = (FieldAccess)fields.get(fieldName);
/* 128:157 */         if (field.type() == Typ.flt)
/* 129:    */         {
/* 130:158 */           float value = field.getFloat(item);
/* 131:159 */           this.sum += value;
/* 132:    */         }
/* 133:    */         else
/* 134:    */         {
/* 135:161 */           Comparable value = (Comparable)getPropertyValue(item, fields);
/* 136:    */           
/* 137:163 */           Float ovalue = Float.valueOf(Conversions.toFloat(value));
/* 138:164 */           this.sum += ovalue.floatValue();
/* 139:    */         }
/* 140:    */       }
/* 141:    */       
/* 142:    */       public void handleStart(Collection<?> results)
/* 143:    */       {
/* 144:172 */         this.sum = -2147483648.0D;
/* 145:    */       }
/* 146:    */       
/* 147:    */       public void handleComplete(List<Map<String, Object>> rows)
/* 148:    */       {
/* 149:177 */         if (rows.size() > 0) {
/* 150:178 */           ((Map)rows.get(0)).put(this.alias, Double.valueOf(this.sum));
/* 151:    */         }
/* 152:    */       }
/* 153:    */     };
/* 154:    */   }
/* 155:    */   
/* 156:    */   public static Selector maxInt(final String fieldName)
/* 157:    */   {
/* 158:187 */     new Selector(fieldName, Str.join('.', new String[] { "max", fieldName }))
/* 159:    */     {
/* 160:188 */       int max = -2147483648;
/* 161:    */       
/* 162:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 163:    */       {
/* 164:192 */         FieldAccess field = (FieldAccess)fields.get(fieldName);
/* 165:193 */         if (field.type() == Typ.intgr)
/* 166:    */         {
/* 167:194 */           int value = field.getInt(item);
/* 168:195 */           if (value > this.max) {
/* 169:196 */             this.max = value;
/* 170:    */           }
/* 171:    */         }
/* 172:    */         else
/* 173:    */         {
/* 174:199 */           Integer ovalue = Integer.valueOf(Conversions.toInt(field.getValue(item)));
/* 175:200 */           if (ovalue.intValue() > this.max) {
/* 176:201 */             this.max = ovalue.intValue();
/* 177:    */           }
/* 178:    */         }
/* 179:    */       }
/* 180:    */       
/* 181:    */       public void handleStart(Collection<?> results)
/* 182:    */       {
/* 183:211 */         this.max = -2147483648;
/* 184:    */       }
/* 185:    */       
/* 186:    */       public void handleComplete(List<Map<String, Object>> rows)
/* 187:    */       {
/* 188:216 */         if (rows.size() > 0) {
/* 189:217 */           ((Map)rows.get(0)).put(this.alias, Integer.valueOf(this.max));
/* 190:    */         }
/* 191:    */       }
/* 192:    */     };
/* 193:    */   }
/* 194:    */   
/* 195:    */   public static Selector maxLong(final String fieldName)
/* 196:    */   {
/* 197:224 */     new Selector(fieldName, Str.join('.', new String[] { "max", fieldName }))
/* 198:    */     {
/* 199:225 */       long max = -9223372036854775808L;
/* 200:    */       
/* 201:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 202:    */       {
/* 203:229 */         FieldAccess field = (FieldAccess)fields.get(fieldName);
/* 204:230 */         if (field.type() == Typ.lng)
/* 205:    */         {
/* 206:231 */           long value = field.getLong(item);
/* 207:232 */           if (value > this.max) {
/* 208:233 */             this.max = value;
/* 209:    */           }
/* 210:    */         }
/* 211:    */         else
/* 212:    */         {
/* 213:236 */           Long ovalue = Long.valueOf(Conversions.toLong(field.getValue(item)));
/* 214:237 */           if (ovalue.longValue() > this.max) {
/* 215:238 */             this.max = ovalue.longValue();
/* 216:    */           }
/* 217:    */         }
/* 218:    */       }
/* 219:    */       
/* 220:    */       public void handleStart(Collection<?> results)
/* 221:    */       {
/* 222:248 */         this.max = -9223372036854775808L;
/* 223:    */       }
/* 224:    */       
/* 225:    */       public void handleComplete(List<Map<String, Object>> rows)
/* 226:    */       {
/* 227:253 */         if (rows.size() > 0) {
/* 228:254 */           ((Map)rows.get(0)).put(this.alias, Long.valueOf(this.max));
/* 229:    */         }
/* 230:    */       }
/* 231:    */     };
/* 232:    */   }
/* 233:    */   
/* 234:    */   public static Selector minInt(final String fieldName)
/* 235:    */   {
/* 236:262 */     new Selector(fieldName, Str.join('.', new String[] { "min", fieldName }))
/* 237:    */     {
/* 238:263 */       int min = 2147483647;
/* 239:    */       
/* 240:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 241:    */       {
/* 242:267 */         FieldAccess field = (FieldAccess)fields.get(fieldName);
/* 243:268 */         if (field.type() == Typ.intgr)
/* 244:    */         {
/* 245:269 */           int value = field.getInt(item);
/* 246:270 */           if (value < this.min) {
/* 247:271 */             this.min = value;
/* 248:    */           }
/* 249:    */         }
/* 250:    */         else
/* 251:    */         {
/* 252:274 */           Integer ovalue = Integer.valueOf(Conversions.toInt(field.getValue(item)));
/* 253:275 */           if (ovalue.intValue() < this.min) {
/* 254:276 */             this.min = ovalue.intValue();
/* 255:    */           }
/* 256:    */         }
/* 257:    */       }
/* 258:    */       
/* 259:    */       public void handleStart(Collection<?> results)
/* 260:    */       {
/* 261:284 */         this.min = 2147483647;
/* 262:    */       }
/* 263:    */       
/* 264:    */       public void handleComplete(List<Map<String, Object>> rows)
/* 265:    */       {
/* 266:289 */         if (rows.size() > 0) {
/* 267:290 */           ((Map)rows.get(0)).put(this.alias, Integer.valueOf(this.min));
/* 268:    */         }
/* 269:    */       }
/* 270:    */     };
/* 271:    */   }
/* 272:    */   
/* 273:    */   public static Selector minLong(final String fieldName)
/* 274:    */   {
/* 275:298 */     new Selector(fieldName, Str.join('.', new String[] { "min", fieldName }))
/* 276:    */     {
/* 277:299 */       long min = 9223372036854775807L;
/* 278:    */       
/* 279:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 280:    */       {
/* 281:303 */         FieldAccess field = (FieldAccess)fields.get(fieldName);
/* 282:304 */         if (field.type() == Typ.lng)
/* 283:    */         {
/* 284:305 */           long value = field.getLong(item);
/* 285:306 */           if (value < this.min) {
/* 286:307 */             this.min = value;
/* 287:    */           }
/* 288:    */         }
/* 289:    */         else
/* 290:    */         {
/* 291:310 */           Long ovalue = Long.valueOf(Conversions.toLong(field.getValue(item)));
/* 292:311 */           if (ovalue.longValue() < this.min) {
/* 293:312 */             this.min = ovalue.longValue();
/* 294:    */           }
/* 295:    */         }
/* 296:    */       }
/* 297:    */       
/* 298:    */       public void handleStart(Collection<?> results)
/* 299:    */       {
/* 300:320 */         this.min = 9223372036854775807L;
/* 301:    */       }
/* 302:    */       
/* 303:    */       public void handleComplete(List<Map<String, Object>> rows)
/* 304:    */       {
/* 305:325 */         if (rows.size() > 0) {
/* 306:326 */           ((Map)rows.get(0)).put(this.alias, Long.valueOf(this.min));
/* 307:    */         }
/* 308:    */       }
/* 309:    */     };
/* 310:    */   }
/* 311:    */   
/* 312:    */   public static Selector maxFloat(final String fieldName)
/* 313:    */   {
/* 314:334 */     new Selector(fieldName, Str.join('.', new String[] { "max", fieldName }))
/* 315:    */     {
/* 316:335 */       float max = 1.4E-45F;
/* 317:    */       
/* 318:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 319:    */       {
/* 320:339 */         FieldAccess field = (FieldAccess)fields.get(fieldName);
/* 321:340 */         if (field.type() == Typ.flt)
/* 322:    */         {
/* 323:341 */           float value = field.getFloat(item);
/* 324:342 */           if (value > this.max) {
/* 325:343 */             this.max = value;
/* 326:    */           }
/* 327:    */         }
/* 328:    */         else
/* 329:    */         {
/* 330:346 */           Float ovalue = Float.valueOf(Conversions.toFloat(field.getValue(item)));
/* 331:347 */           if (ovalue.floatValue() > this.max) {
/* 332:348 */             this.max = ovalue.floatValue();
/* 333:    */           }
/* 334:    */         }
/* 335:    */       }
/* 336:    */       
/* 337:    */       public void handleStart(Collection<?> results)
/* 338:    */       {
/* 339:356 */         this.max = 1.4E-45F;
/* 340:    */       }
/* 341:    */       
/* 342:    */       public void handleComplete(List<Map<String, Object>> rows)
/* 343:    */       {
/* 344:361 */         if (rows.size() > 0) {
/* 345:362 */           ((Map)rows.get(0)).put(this.alias, Float.valueOf(this.max));
/* 346:    */         }
/* 347:    */       }
/* 348:    */     };
/* 349:    */   }
/* 350:    */   
/* 351:    */   public static Selector minFloat(final String fieldName)
/* 352:    */   {
/* 353:369 */     new Selector(fieldName, Str.join('.', new String[] { "min", fieldName }))
/* 354:    */     {
/* 355:371 */       float min = 3.4028235E+38F;
/* 356:    */       
/* 357:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 358:    */       {
/* 359:375 */         FieldAccess field = (FieldAccess)fields.get(fieldName);
/* 360:377 */         if (field.type() == Typ.flt)
/* 361:    */         {
/* 362:378 */           float value = field.getFloat(item);
/* 363:379 */           if (value > this.min) {
/* 364:380 */             this.min = value;
/* 365:    */           }
/* 366:    */         }
/* 367:    */         else
/* 368:    */         {
/* 369:383 */           Float ovalue = Float.valueOf(Conversions.toFloat(field.getValue(item)));
/* 370:384 */           if (ovalue.floatValue() > this.min) {
/* 371:385 */             this.min = ovalue.floatValue();
/* 372:    */           }
/* 373:    */         }
/* 374:    */       }
/* 375:    */       
/* 376:    */       public void handleStart(Collection<?> results)
/* 377:    */       {
/* 378:393 */         this.min = 3.4028235E+38F;
/* 379:    */       }
/* 380:    */       
/* 381:    */       public void handleComplete(List<Map<String, Object>> rows)
/* 382:    */       {
/* 383:398 */         if (rows.size() > 0) {
/* 384:399 */           ((Map)rows.get(0)).put(this.alias, Float.valueOf(this.min));
/* 385:    */         }
/* 386:    */       }
/* 387:    */     };
/* 388:    */   }
/* 389:    */   
/* 390:    */   public static Selector maxDouble(final String fieldName)
/* 391:    */   {
/* 392:407 */     new Selector(fieldName, Str.join('.', new String[] { "max", fieldName }))
/* 393:    */     {
/* 394:408 */       double max = 4.9E-324D;
/* 395:    */       
/* 396:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 397:    */       {
/* 398:412 */         double value = ((FieldAccess)fields.get(fieldName)).getDouble(item);
/* 399:413 */         if (value > this.max) {
/* 400:414 */           this.max = value;
/* 401:    */         }
/* 402:    */       }
/* 403:    */       
/* 404:    */       public void handleStart(Collection<?> results)
/* 405:    */       {
/* 406:420 */         this.max = 4.9E-324D;
/* 407:    */       }
/* 408:    */       
/* 409:    */       public void handleComplete(List<Map<String, Object>> rows)
/* 410:    */       {
/* 411:425 */         if (rows.size() > 0) {
/* 412:426 */           ((Map)rows.get(0)).put(this.alias, Double.valueOf(this.max));
/* 413:    */         }
/* 414:    */       }
/* 415:    */     };
/* 416:    */   }
/* 417:    */   
/* 418:    */   public static Selector minDouble(final String fieldName)
/* 419:    */   {
/* 420:433 */     new Selector(fieldName, Str.join('.', new String[] { "min", fieldName }))
/* 421:    */     {
/* 422:434 */       double min = 1.7976931348623157E+308D;
/* 423:    */       
/* 424:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 425:    */       {
/* 426:438 */         double value = ((FieldAccess)fields.get(fieldName)).getDouble(item);
/* 427:439 */         if (value < this.min) {
/* 428:440 */           this.min = value;
/* 429:    */         }
/* 430:    */       }
/* 431:    */       
/* 432:    */       public void handleStart(Collection<?> results)
/* 433:    */       {
/* 434:446 */         this.min = 4.9E-324D;
/* 435:    */       }
/* 436:    */       
/* 437:    */       public void handleComplete(List<Map<String, Object>> rows)
/* 438:    */       {
/* 439:451 */         if (rows.size() > 0) {
/* 440:452 */           ((Map)rows.get(0)).put(this.alias, Double.valueOf(this.min));
/* 441:    */         }
/* 442:    */       }
/* 443:    */     };
/* 444:    */   }
/* 445:    */   
/* 446:    */   public static Selector minShort(final String fieldName)
/* 447:    */   {
/* 448:459 */     new Selector(fieldName, Str.join('.', new String[] { "min", fieldName }))
/* 449:    */     {
/* 450:460 */       short min = 32767;
/* 451:    */       
/* 452:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 453:    */       {
/* 454:464 */         short value = ((FieldAccess)fields.get(fieldName)).getShort(item);
/* 455:465 */         if (value < this.min) {
/* 456:466 */           this.min = value;
/* 457:    */         }
/* 458:    */       }
/* 459:    */       
/* 460:    */       public void handleStart(Collection<?> results)
/* 461:    */       {
/* 462:472 */         this.min = 32767;
/* 463:    */       }
/* 464:    */       
/* 465:    */       public void handleComplete(List<Map<String, Object>> rows)
/* 466:    */       {
/* 467:477 */         if (rows.size() > 0) {
/* 468:478 */           ((Map)rows.get(0)).put(this.alias, Short.valueOf(this.min));
/* 469:    */         }
/* 470:    */       }
/* 471:    */     };
/* 472:    */   }
/* 473:    */   
/* 474:    */   public static Selector maxShort(final String fieldName)
/* 475:    */   {
/* 476:485 */     new Selector(fieldName, Str.join('.', new String[] { "max", fieldName }))
/* 477:    */     {
/* 478:486 */       short max = -32768;
/* 479:    */       
/* 480:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 481:    */       {
/* 482:490 */         short value = ((FieldAccess)fields.get(fieldName)).getShort(item);
/* 483:491 */         if (value > this.max) {
/* 484:492 */           this.max = value;
/* 485:    */         }
/* 486:    */       }
/* 487:    */       
/* 488:    */       public void handleStart(Collection<?> results)
/* 489:    */       {
/* 490:498 */         this.max = -32768;
/* 491:    */       }
/* 492:    */       
/* 493:    */       public void handleComplete(List<Map<String, Object>> rows)
/* 494:    */       {
/* 495:503 */         if (rows.size() > 0) {
/* 496:504 */           ((Map)rows.get(0)).put(this.alias, Short.valueOf(this.max));
/* 497:    */         }
/* 498:    */       }
/* 499:    */     };
/* 500:    */   }
/* 501:    */   
/* 502:    */   public static Selector maxByte(final String fieldName)
/* 503:    */   {
/* 504:511 */     new Selector(fieldName, Str.join('.', new String[] { "max", fieldName }))
/* 505:    */     {
/* 506:512 */       byte max = -128;
/* 507:    */       
/* 508:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 509:    */       {
/* 510:516 */         byte value = ((FieldAccess)fields.get(fieldName)).getByte(item);
/* 511:517 */         if (value > this.max) {
/* 512:518 */           this.max = value;
/* 513:    */         }
/* 514:    */       }
/* 515:    */       
/* 516:    */       public void handleStart(Collection<?> results)
/* 517:    */       {
/* 518:524 */         this.max = -128;
/* 519:    */       }
/* 520:    */       
/* 521:    */       public void handleComplete(List<Map<String, Object>> rows)
/* 522:    */       {
/* 523:529 */         if (rows.size() > 0) {
/* 524:530 */           ((Map)rows.get(0)).put(this.alias, Byte.valueOf(this.max));
/* 525:    */         }
/* 526:    */       }
/* 527:    */     };
/* 528:    */   }
/* 529:    */   
/* 530:    */   public static Selector minByte(final String fieldName)
/* 531:    */   {
/* 532:538 */     new Selector(fieldName, Str.join('.', new String[] { "min", fieldName }))
/* 533:    */     {
/* 534:539 */       byte min = 127;
/* 535:    */       
/* 536:    */       public void handleRow(int index, Map<String, Object> row, Object item, Map<String, FieldAccess> fields)
/* 537:    */       {
/* 538:543 */         byte value = ((FieldAccess)fields.get(fieldName)).getByte(item);
/* 539:544 */         if (value < this.min) {
/* 540:545 */           this.min = value;
/* 541:    */         }
/* 542:    */       }
/* 543:    */       
/* 544:    */       public void handleStart(Collection<?> results)
/* 545:    */       {
/* 546:551 */         this.min = -128;
/* 547:    */       }
/* 548:    */       
/* 549:    */       public void handleComplete(List<Map<String, Object>> rows)
/* 550:    */       {
/* 551:556 */         if (rows.size() > 0) {
/* 552:557 */           ((Map)rows.get(0)).put(this.alias, Byte.valueOf(this.min));
/* 553:    */         }
/* 554:    */       }
/* 555:    */     };
/* 556:    */   }
/* 557:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.criteria.ProjectedSelector
 * JD-Core Version:    0.7.0.1
 */