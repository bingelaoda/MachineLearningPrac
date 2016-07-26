/*   1:    */ package org.boon.core;
/*   2:    */ 
/*   3:    */ import java.text.ParseException;
/*   4:    */ import java.text.SimpleDateFormat;
/*   5:    */ import java.util.Calendar;
/*   6:    */ import java.util.Date;
/*   7:    */ import java.util.TimeZone;
/*   8:    */ import org.boon.Exceptions;
/*   9:    */ import org.boon.Str;
/*  10:    */ import org.boon.core.reflection.FastStringUtils;
/*  11:    */ import org.boon.json.JsonException;
/*  12:    */ import org.boon.primitive.CharBuf;
/*  13:    */ import org.boon.primitive.CharScanner;
/*  14:    */ 
/*  15:    */ public class Dates
/*  16:    */ {
/*  17: 46 */   public static final TimeZone GMT = TimeZone.getTimeZone("GMT");
/*  18: 47 */   private static final TimeZone UTC_TIME_ZONE = TimeZone.getTimeZone("UTC");
/*  19:    */   private static volatile long lastNow;
/*  20: 49 */   private static long MILLI_SECOND = 1L;
/*  21: 50 */   private static long SECOND = MILLI_SECOND * 1000L;
/*  22: 51 */   private static long MINUTE = 60L * SECOND;
/*  23: 52 */   private static long HOUR = 60L * MINUTE;
/*  24: 53 */   private static long DAY = 24L * HOUR;
/*  25: 54 */   private static long WEEK = 7L * DAY;
/*  26: 55 */   private static long MONTH = (30.416699999999999D * DAY);
/*  27: 56 */   private static long YEAR = (365.24250000000001D * DAY);
/*  28:    */   
/*  29:    */   public static long utcNow()
/*  30:    */   {
/*  31: 60 */     long now = System.currentTimeMillis();
/*  32: 61 */     Calendar calendar = Calendar.getInstance();
/*  33: 62 */     calendar.setTimeInMillis(now);
/*  34: 63 */     calendar.setTimeZone(UTC_TIME_ZONE);
/*  35: 64 */     long utcNow = calendar.getTime().getTime();
/*  36: 65 */     lastNow = now;
/*  37: 66 */     return utcNow;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public static long now()
/*  41:    */   {
/*  42: 70 */     return System.currentTimeMillis();
/*  43:    */   }
/*  44:    */   
/*  45:    */   public static long timeZoneNow(TimeZone timeZone)
/*  46:    */   {
/*  47: 75 */     Calendar calendar = Calendar.getInstance();
/*  48: 76 */     return timeZoneNow(timeZone);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static long timeZoneNow(TimeZone timeZone, Calendar calendar)
/*  52:    */   {
/*  53: 81 */     long now = System.currentTimeMillis();
/*  54: 82 */     calendar.setTimeInMillis(now);
/*  55: 83 */     calendar.setTimeZone(timeZone);
/*  56: 84 */     long timeZoneNow = calendar.getTime().getTime();
/*  57: 85 */     return timeZoneNow;
/*  58:    */   }
/*  59:    */   
/*  60:    */   public static Calendar utcCalendar()
/*  61:    */   {
/*  62: 91 */     Calendar calendar = Calendar.getInstance();
/*  63: 92 */     calendar.setTimeZone(UTC_TIME_ZONE);
/*  64: 93 */     return calendar;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public static int durationInHours(long to, long from)
/*  68:    */   {
/*  69: 97 */     long duration = Math.abs(to - from);
/*  70:    */     
/*  71: 99 */     return (int)(duration / HOUR);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public static int durationInMinutes(long to, long from)
/*  75:    */   {
/*  76:105 */     long duration = Math.abs(to - from);
/*  77:    */     
/*  78:107 */     return (int)(duration / MINUTE);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public static long durationInSeconds(long to, long from)
/*  82:    */   {
/*  83:113 */     long duration = Math.abs(to - from);
/*  84:    */     
/*  85:115 */     return (int)(duration / SECOND);
/*  86:    */   }
/*  87:    */   
/*  88:    */   public static long durationInMilis(long to, long from)
/*  89:    */   {
/*  90:121 */     long duration = Math.abs(to - from);
/*  91:    */     
/*  92:123 */     return (int)(duration / MILLI_SECOND);
/*  93:    */   }
/*  94:    */   
/*  95:    */   public static long utcNowFast(Calendar utcCalendar)
/*  96:    */   {
/*  97:129 */     long now = Sys.time();
/*  98:130 */     long utcNow = utcCalendar.getTime().getTime();
/*  99:131 */     lastNow = now;
/* 100:132 */     return utcNow;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public static long utc(long time)
/* 104:    */   {
/* 105:136 */     Calendar calendar = Calendar.getInstance();
/* 106:137 */     calendar.setTimeInMillis(time);
/* 107:138 */     calendar.setTimeZone(UTC_TIME_ZONE);
/* 108:139 */     long utcNow = calendar.getTime().getTime();
/* 109:140 */     lastNow = time;
/* 110:141 */     return utcNow;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public static long utcFast(long time, Calendar utcCalendar)
/* 114:    */   {
/* 115:147 */     long utcNow = utcCalendar.getTime().getTime();
/* 116:148 */     lastNow = time;
/* 117:149 */     return utcNow;
/* 118:    */   }
/* 119:    */   
/* 120:    */   static long lastNow()
/* 121:    */   {
/* 122:156 */     return lastNow;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public static long fromUtcTimeToTimeZone(long utcTime, TimeZone timeZone)
/* 126:    */   {
/* 127:162 */     Calendar calendar = Calendar.getInstance(UTC_TIME_ZONE);
/* 128:163 */     calendar.setTimeInMillis(utcTime);
/* 129:164 */     calendar.setTimeZone(timeZone);
/* 130:165 */     return calendar.getTime().getTime();
/* 131:    */   }
/* 132:    */   
/* 133:    */   public static boolean before(long isThis, long beforeThis)
/* 134:    */   {
/* 135:170 */     return isThis < beforeThis;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public static boolean after(long isThis, long afterThis)
/* 139:    */   {
/* 140:175 */     return isThis > afterThis;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public static long hourDuration(int count)
/* 144:    */   {
/* 145:179 */     return count * HOUR;
/* 146:    */   }
/* 147:    */   
/* 148:    */   public static long minuteDuration(int count)
/* 149:    */   {
/* 150:184 */     return count * MINUTE;
/* 151:    */   }
/* 152:    */   
/* 153:    */   public static long secondDuration(int count)
/* 154:    */   {
/* 155:189 */     return count * SECOND;
/* 156:    */   }
/* 157:    */   
/* 158:    */   public static long dayDuration(int count)
/* 159:    */   {
/* 160:194 */     return count * DAY;
/* 161:    */   }
/* 162:    */   
/* 163:    */   public static long secondsFrom(long time, int seconds)
/* 164:    */   {
/* 165:198 */     return time + seconds * SECOND;
/* 166:    */   }
/* 167:    */   
/* 168:    */   public static long minutesFrom(long time, int minutes)
/* 169:    */   {
/* 170:202 */     return time + minutes * MINUTE;
/* 171:    */   }
/* 172:    */   
/* 173:    */   public static long hoursFrom(long time, int hours)
/* 174:    */   {
/* 175:206 */     return time + hours * HOUR;
/* 176:    */   }
/* 177:    */   
/* 178:    */   public static long daysFrom(long time, int days)
/* 179:    */   {
/* 180:210 */     return time + days * DAY;
/* 181:    */   }
/* 182:    */   
/* 183:    */   public static long weeksFrom(long time, int weeks)
/* 184:    */   {
/* 185:214 */     return time + weeks * WEEK;
/* 186:    */   }
/* 187:    */   
/* 188:    */   public static long monthsFrom(long time, int months)
/* 189:    */   {
/* 190:218 */     return time + months * MONTH;
/* 191:    */   }
/* 192:    */   
/* 193:    */   public static long yearsFrom(long time, int years)
/* 194:    */   {
/* 195:222 */     return time + years * YEAR;
/* 196:    */   }
/* 197:    */   
/* 198:    */   public static long utcDate(int year, int month, int day)
/* 199:    */   {
/* 200:226 */     Calendar calendar = Calendar.getInstance();
/* 201:    */     
/* 202:    */ 
/* 203:229 */     midnight(calendar);
/* 204:    */     
/* 205:    */ 
/* 206:    */ 
/* 207:    */ 
/* 208:234 */     calendar.setTimeZone(UTC_TIME_ZONE);
/* 209:    */     
/* 210:    */ 
/* 211:237 */     return internalDate(year, month, day, calendar);
/* 212:    */   }
/* 213:    */   
/* 214:    */   public static long utcDate(int year, int month, int day, int hour, int minute)
/* 215:    */   {
/* 216:242 */     Calendar calendar = Calendar.getInstance();
/* 217:243 */     midnight(calendar);
/* 218:    */     
/* 219:    */ 
/* 220:    */ 
/* 221:    */ 
/* 222:248 */     calendar.setTimeZone(UTC_TIME_ZONE);
/* 223:    */     
/* 224:250 */     return internalDateLong(year, month, day, hour, minute, calendar);
/* 225:    */   }
/* 226:    */   
/* 227:    */   private static long internalDateLong(int year, int month, int day, int hour, int minute, Calendar calendar)
/* 228:    */   {
/* 229:255 */     return internalDate(year, month, day, hour, minute, calendar).getTime();
/* 230:    */   }
/* 231:    */   
/* 232:    */   private static Date internalDate(int year, int month, int day, int hour, int minute, Calendar calendar)
/* 233:    */   {
/* 234:261 */     calendar.set(1, year);
/* 235:262 */     calendar.set(2, month);
/* 236:263 */     calendar.set(5, day);
/* 237:264 */     calendar.set(11, hour);
/* 238:265 */     calendar.set(12, minute);
/* 239:266 */     calendar.set(13, 0);
/* 240:267 */     calendar.set(14, 0);
/* 241:    */     
/* 242:    */ 
/* 243:270 */     return calendar.getTime();
/* 244:    */   }
/* 245:    */   
/* 246:    */   private static Date internalDate(TimeZone tz, int year, int month, int day, int hour, int minute, int second)
/* 247:    */   {
/* 248:276 */     Calendar calendar = Calendar.getInstance();
/* 249:    */     
/* 250:278 */     calendar.set(1, year);
/* 251:279 */     calendar.set(2, month - 1);
/* 252:280 */     calendar.set(5, day);
/* 253:281 */     calendar.set(11, hour);
/* 254:282 */     calendar.set(12, minute);
/* 255:283 */     calendar.set(13, second);
/* 256:284 */     calendar.set(14, 0);
/* 257:    */     
/* 258:286 */     calendar.setTimeZone(tz);
/* 259:    */     
/* 260:288 */     return calendar.getTime();
/* 261:    */   }
/* 262:    */   
/* 263:    */   private static Date internalDate(TimeZone tz, int year, int month, int day, int hour, int minute, int second, int miliseconds)
/* 264:    */   {
/* 265:295 */     Calendar calendar = Calendar.getInstance();
/* 266:    */     
/* 267:297 */     calendar.set(1, year);
/* 268:298 */     calendar.set(2, month - 1);
/* 269:299 */     calendar.set(5, day);
/* 270:300 */     calendar.set(11, hour);
/* 271:301 */     calendar.set(12, minute);
/* 272:302 */     calendar.set(13, second);
/* 273:303 */     calendar.set(14, miliseconds);
/* 274:    */     
/* 275:305 */     calendar.setTimeZone(tz);
/* 276:    */     
/* 277:307 */     return calendar.getTime();
/* 278:    */   }
/* 279:    */   
/* 280:    */   public static long wallTimeDate(int year, int month, int day)
/* 281:    */   {
/* 282:311 */     Calendar calendar = Calendar.getInstance();
/* 283:    */     
/* 284:    */ 
/* 285:314 */     midnight(calendar);
/* 286:    */     
/* 287:    */ 
/* 288:317 */     return internalDate(year, month, day, calendar);
/* 289:    */   }
/* 290:    */   
/* 291:    */   public static long date(int year, int month, int day)
/* 292:    */   {
/* 293:322 */     return utcDate(year, month, day);
/* 294:    */   }
/* 295:    */   
/* 296:    */   public static long date(int year, int month, int day, int hour, int minute)
/* 297:    */   {
/* 298:327 */     return utcDate(year, month, day, hour, minute);
/* 299:    */   }
/* 300:    */   
/* 301:    */   public static long date(TimeZone tz, int year, int month, int day)
/* 302:    */   {
/* 303:333 */     Calendar calendar = Calendar.getInstance();
/* 304:    */     
/* 305:    */ 
/* 306:336 */     midnight(calendar);
/* 307:    */     
/* 308:338 */     calendar.setTimeZone(tz);
/* 309:    */     
/* 310:340 */     return internalDate(year, month, day, calendar);
/* 311:    */   }
/* 312:    */   
/* 313:    */   private static long internalDate(int year, int month, int day, Calendar calendar)
/* 314:    */   {
/* 315:344 */     calendar.set(1, year);
/* 316:345 */     calendar.set(2, month);
/* 317:346 */     calendar.set(5, day);
/* 318:    */     
/* 319:348 */     calendar.set(11, 0);
/* 320:    */     
/* 321:350 */     calendar.set(12, 0);
/* 322:    */     
/* 323:352 */     calendar.set(13, 0);
/* 324:    */     
/* 325:354 */     calendar.set(14, 0);
/* 326:    */     
/* 327:    */ 
/* 328:357 */     return calendar.getTime().getTime();
/* 329:    */   }
/* 330:    */   
/* 331:    */   public static long wallTimeDate(int year, int month, int day, int hour, int minute)
/* 332:    */   {
/* 333:362 */     Calendar calendar = Calendar.getInstance();
/* 334:363 */     midnight(calendar);
/* 335:    */     
/* 336:    */ 
/* 337:366 */     return internalDateLong(year, month, day, hour, minute, calendar);
/* 338:    */   }
/* 339:    */   
/* 340:    */   public static Date toDate(TimeZone tz, int year, int month, int day, int hour, int minute, int second)
/* 341:    */   {
/* 342:372 */     return internalDate(tz, year, month, day, hour, minute, second);
/* 343:    */   }
/* 344:    */   
/* 345:    */   public static Date toDate(TimeZone tz, int year, int month, int day, int hour, int minute, int second, int miliseconds)
/* 346:    */   {
/* 347:378 */     return internalDate(tz, year, month, day, hour, minute, second, miliseconds);
/* 348:    */   }
/* 349:    */   
/* 350:    */   public static Date toDate(int year, int month, int day, int hour, int minute, int second, int miliseconds)
/* 351:    */   {
/* 352:383 */     return internalDate(TimeZone.getDefault(), year, month, day, hour, minute, second, miliseconds);
/* 353:    */   }
/* 354:    */   
/* 355:    */   public static long date(TimeZone tz, int year, int month, int day, int hour, int minute)
/* 356:    */   {
/* 357:388 */     Calendar calendar = Calendar.getInstance();
/* 358:389 */     midnight(calendar);
/* 359:390 */     calendar.setTimeZone(tz);
/* 360:    */     
/* 361:392 */     return internalDateLong(year, month, day, hour, minute, calendar);
/* 362:    */   }
/* 363:    */   
/* 364:    */   private static void midnight(Calendar calendar)
/* 365:    */   {
/* 366:397 */     calendar.set(11, 0);
/* 367:398 */     calendar.set(12, 0);
/* 368:399 */     calendar.set(13, 0);
/* 369:400 */     calendar.set(14, 0);
/* 370:    */   }
/* 371:    */   
/* 372:    */   public static String euroUTCSystemDateNowString()
/* 373:    */   {
/* 374:409 */     long now = System.currentTimeMillis();
/* 375:410 */     return euroUTCSystemDateString(now);
/* 376:    */   }
/* 377:    */   
/* 378:    */   public static String euroUTCSystemDateString(long timestamp)
/* 379:    */   {
/* 380:421 */     Calendar calendar = Calendar.getInstance();
/* 381:422 */     calendar.setTimeInMillis(timestamp);
/* 382:423 */     calendar.setTimeZone(UTC_TIME_ZONE);
/* 383:424 */     int day = calendar.get(5);
/* 384:425 */     int month = calendar.get(2);
/* 385:426 */     int year = calendar.get(1);
/* 386:427 */     int hour = calendar.get(11);
/* 387:428 */     int minute = calendar.get(12);
/* 388:429 */     int second = calendar.get(13);
/* 389:    */     
/* 390:431 */     CharBuf buf = CharBuf.create(16);
/* 391:432 */     buf.add(Str.zfill(day, 2)).add('_');
/* 392:433 */     buf.add(Str.zfill(month, 2)).add('_');
/* 393:434 */     buf.add(year).add('_');
/* 394:435 */     buf.add(Str.zfill(hour, 2)).add('_');
/* 395:436 */     buf.add(Str.zfill(minute, 2)).add('_');
/* 396:437 */     buf.add(Str.zfill(second, 2)).add("_utc_euro");
/* 397:    */     
/* 398:439 */     return buf.toString();
/* 399:    */   }
/* 400:    */   
/* 401:    */   public static void main(String... args)
/* 402:    */   {
/* 403:445 */     Sys.println(euroUTCSystemDateNowString());
/* 404:    */   }
/* 405:    */   
/* 406:    */   public static Date year(int year)
/* 407:    */   {
/* 408:451 */     Calendar c = Calendar.getInstance();
/* 409:452 */     c.setTimeZone(GMT);
/* 410:453 */     c.set(1970, 0, 2, 0, 0, 0);
/* 411:454 */     c.set(1, year);
/* 412:455 */     c.set(14, 0);
/* 413:456 */     return c.getTime();
/* 414:    */   }
/* 415:    */   
/* 416:    */   public static Date getUSDate(int month, int day, int year)
/* 417:    */   {
/* 418:460 */     Calendar c = Calendar.getInstance();
/* 419:461 */     c.setTimeZone(GMT);
/* 420:462 */     c.set(year, month - 1, day + 1, 0, 0, 0);
/* 421:463 */     c.set(14, 0);
/* 422:464 */     return c.getTime();
/* 423:    */   }
/* 424:    */   
/* 425:    */   public static Date getUSDate(int month, int day, int year, int hour, int minute, int second)
/* 426:    */   {
/* 427:469 */     Calendar c = Calendar.getInstance();
/* 428:470 */     c.setTimeZone(GMT);
/* 429:471 */     c.set(year, month - 1, day + 1, hour, minute, second);
/* 430:472 */     c.set(14, 0);
/* 431:473 */     return c.getTime();
/* 432:    */   }
/* 433:    */   
/* 434:    */   public static Date getEuroDate(int day, int month, int year)
/* 435:    */   {
/* 436:477 */     Calendar c = Calendar.getInstance();
/* 437:478 */     c.setTimeZone(GMT);
/* 438:479 */     c.set(year, month - 1, day + 1, 0, 0, 0);
/* 439:480 */     c.set(14, 0);
/* 440:481 */     return c.getTime();
/* 441:    */   }
/* 442:    */   
/* 443:    */   public static Date getEuroDate(int day, int month, int year, int hour, int minute, int second)
/* 444:    */   {
/* 445:485 */     Calendar c = Calendar.getInstance();
/* 446:486 */     c.setTimeZone(GMT);
/* 447:487 */     c.set(year, month - 1, day + 1, hour, minute, second);
/* 448:488 */     c.set(14, 0);
/* 449:489 */     return c.getTime();
/* 450:    */   }
/* 451:    */   
/* 452:    */   public static Date fromISO8601_(String string)
/* 453:    */   {
/* 454:    */     try
/* 455:    */     {
/* 456:497 */       return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(string);
/* 457:    */     }
/* 458:    */     catch (ParseException e)
/* 459:    */     {
/* 460:499 */       return (Date)Exceptions.handle(Date.class, "Not a valid ISO8601", e);
/* 461:    */     }
/* 462:    */   }
/* 463:    */   
/* 464:    */   public static Date fromISO8601Jackson_(String string)
/* 465:    */   {
/* 466:508 */     if ((string.length() == 29) && (Str.idx(string, -3) == ':')) {
/* 467:    */       try
/* 468:    */       {
/* 469:512 */         return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(string);
/* 470:    */       }
/* 471:    */       catch (ParseException e)
/* 472:    */       {
/* 473:514 */         return (Date)Exceptions.handle(Date.class, "Not a valid ISO8601 \"Jackson\" date", e);
/* 474:    */       }
/* 475:    */     }
/* 476:    */     try
/* 477:    */     {
/* 478:522 */       return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(string);
/* 479:    */     }
/* 480:    */     catch (ParseException e)
/* 481:    */     {
/* 482:524 */       return (Date)Exceptions.handle(Date.class, "Not a valid ISO8601 \"Jackson\" date", e);
/* 483:    */     }
/* 484:    */   }
/* 485:    */   
/* 486:    */   public static Date fromJsonDate_(String string)
/* 487:    */   {
/* 488:    */     try
/* 489:    */     {
/* 490:536 */       return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").parse(string);
/* 491:    */     }
/* 492:    */     catch (ParseException e)
/* 493:    */     {
/* 494:538 */       return (Date)Exceptions.handle(Date.class, "Not a valid JSON date", e);
/* 495:    */     }
/* 496:    */   }
/* 497:    */   
/* 498:    */   public static Date fromJsonDate(String string)
/* 499:    */   {
/* 500:547 */     return fromJsonDate(FastStringUtils.toCharArray(string), 0, string.length());
/* 501:    */   }
/* 502:    */   
/* 503:    */   public static Date fromISO8601(String string)
/* 504:    */   {
/* 505:553 */     return fromISO8601(FastStringUtils.toCharArray(string), 0, string.length());
/* 506:    */   }
/* 507:    */   
/* 508:    */   public static Date fromISO8601Jackson(String string)
/* 509:    */   {
/* 510:559 */     return fromISO8601Jackson(FastStringUtils.toCharArray(string), 0, string.length());
/* 511:    */   }
/* 512:    */   
/* 513:    */   public static Date fromISO8601DateLoose(String string)
/* 514:    */   {
/* 515:564 */     return fromISO8601DateLoose(FastStringUtils.toCharArray(string), 0, string.length());
/* 516:    */   }
/* 517:    */   
/* 518:    */   public static Date fromISO8601DateLoose(char[] chars)
/* 519:    */   {
/* 520:569 */     return fromISO8601DateLoose(chars, 0, chars.length);
/* 521:    */   }
/* 522:    */   
/* 523:574 */   static final int SHORT_ISO_8601_TIME_LENGTH = "1994-11-05T08:15:30Z".length();
/* 524:575 */   static final int LONG_ISO_8601_TIME_LENGTH = "1994-11-05T08:15:30-05:00".length();
/* 525:576 */   static final int LONG_ISO_8601_JACKSON_TIME_LENGTH = "1994-11-05T08:11:22.123-0500".length();
/* 526:577 */   public static final int JSON_TIME_LENGTH = "2013-12-14T01:55:33.412Z".length();
/* 527:    */   private static final boolean isGMT;
/* 528:    */   
/* 529:    */   public static String jsonDate(Date date)
/* 530:    */   {
/* 531:586 */     CharBuf buf = CharBuf.create(JSON_TIME_LENGTH);
/* 532:587 */     jsonDateChars(date, buf);
/* 533:588 */     return buf.toString();
/* 534:    */   }
/* 535:    */   
/* 536:    */   static
/* 537:    */   {
/* 538:594 */     if (TimeZone.getDefault() == GMT) {
/* 539:595 */       isGMT = true;
/* 540:    */     } else {
/* 541:597 */       isGMT = false;
/* 542:    */     }
/* 543:    */   }
/* 544:    */   
/* 545:    */   public static void jsonDateChars(Date date, CharBuf buf)
/* 546:    */   {
/* 547:603 */     Calendar calendar = Calendar.getInstance();
/* 548:604 */     calendar.setTimeZone(GMT);
/* 549:605 */     jsonDateChars(calendar, date, buf);
/* 550:    */   }
/* 551:    */   
/* 552:    */   public static void jsonDateChars(long milis, CharBuf buf)
/* 553:    */   {
/* 554:610 */     Calendar calendar = Calendar.getInstance();
/* 555:611 */     calendar.setTimeZone(GMT);
/* 556:612 */     jsonDateChars(calendar, milis, buf);
/* 557:    */   }
/* 558:    */   
/* 559:    */   public static void jsonDateChars(Calendar calendar, Date date, CharBuf buf)
/* 560:    */   {
/* 561:616 */     jsonDateChars(calendar, date.getTime(), buf);
/* 562:    */   }
/* 563:    */   
/* 564:    */   public static void jsonDateChars(Calendar calendar, long milis, CharBuf buf)
/* 565:    */   {
/* 566:620 */     if (isGMT)
/* 567:    */     {
/* 568:622 */       fastJsonDateChars(new Date(milis), buf);
/* 569:623 */       return;
/* 570:    */     }
/* 571:626 */     calendar.setTimeInMillis(milis);
/* 572:    */     
/* 573:    */ 
/* 574:629 */     int day = calendar.get(5);
/* 575:630 */     int month = calendar.get(2) + 1;
/* 576:631 */     int year = calendar.get(1);
/* 577:632 */     int hour = calendar.get(11);
/* 578:633 */     int minute = calendar.get(12);
/* 579:634 */     int second = calendar.get(13);
/* 580:635 */     int mili = calendar.get(14);
/* 581:    */     
/* 582:637 */     buf.add('"');
/* 583:638 */     buf.add(year).add('-');
/* 584:639 */     buf.add(Str.zfill(month, 2)).add('-');
/* 585:640 */     buf.add(Str.zfill(day, 2)).add('T');
/* 586:    */     
/* 587:642 */     buf.add(Str.zfill(hour, 2)).add(':');
/* 588:643 */     buf.add(Str.zfill(minute, 2)).add(':');
/* 589:644 */     buf.add(Str.zfill(second, 2)).add(".");
/* 590:645 */     buf.add(Str.zfill(mili, 3)).add("Z");
/* 591:    */     
/* 592:647 */     buf.add('"');
/* 593:    */   }
/* 594:    */   
/* 595:    */   public static void fastJsonDateChars(Date date, CharBuf buf)
/* 596:    */   {
/* 597:653 */     int day = date.getDate();
/* 598:654 */     int month = date.getMonth() + 1;
/* 599:655 */     int year = date.getYear() + 1900;
/* 600:656 */     int hour = date.getHours();
/* 601:657 */     int minute = date.getMinutes();
/* 602:658 */     int second = date.getSeconds();
/* 603:659 */     int offset = date.getTimezoneOffset();
/* 604:660 */     int mili = 1;
/* 605:    */     
/* 606:662 */     buf.add('"');
/* 607:663 */     buf.add(year).add('-');
/* 608:664 */     buf.add(Str.zfill(month, 2)).add('-');
/* 609:665 */     buf.add(Str.zfill(day, 2)).add('T');
/* 610:    */     
/* 611:667 */     buf.add(Str.zfill(hour, 2)).add(':');
/* 612:668 */     buf.add(Str.zfill(minute, 2)).add(':');
/* 613:669 */     buf.add(Str.zfill(second, 2)).add(".");
/* 614:670 */     buf.add(Str.zfill(mili, 3)).add("Z");
/* 615:    */     
/* 616:672 */     buf.add('"');
/* 617:    */   }
/* 618:    */   
/* 619:    */   public static Date fromISO8601DateLoose(char[] buffer, int startIndex, int endIndex)
/* 620:    */   {
/* 621:678 */     if (isISO8601QuickCheck(buffer, startIndex, endIndex))
/* 622:    */     {
/* 623:680 */       if (isJsonDate(buffer, startIndex, endIndex)) {
/* 624:681 */         return fromJsonDate(buffer, startIndex, endIndex);
/* 625:    */       }
/* 626:683 */       if (isISO8601(buffer, startIndex, endIndex)) {
/* 627:684 */         return fromISO8601(buffer, startIndex, endIndex);
/* 628:    */       }
/* 629:    */       try
/* 630:    */       {
/* 631:687 */         return looseParse(buffer, startIndex, endIndex);
/* 632:    */       }
/* 633:    */       catch (Exception ex)
/* 634:    */       {
/* 635:689 */         throw new JsonException("unable to do a loose parse", ex);
/* 636:    */       }
/* 637:    */     }
/* 638:    */     try
/* 639:    */     {
/* 640:695 */       return looseParse(buffer, startIndex, endIndex);
/* 641:    */     }
/* 642:    */     catch (Exception ex)
/* 643:    */     {
/* 644:697 */       throw new JsonException("unable to do a loose parse", ex);
/* 645:    */     }
/* 646:    */   }
/* 647:    */   
/* 648:    */   private static Date looseParse(char[] buffer, int startIndex, int endIndex)
/* 649:    */   {
/* 650:705 */     char[][] parts = CharScanner.splitByCharsNoneEmpty(buffer, startIndex, endIndex, new char[] { '-', ':', 'T', '.' });
/* 651:706 */     int year = 0;
/* 652:707 */     int month = 0;
/* 653:708 */     int day = 0;
/* 654:    */     
/* 655:710 */     int hour = 0;
/* 656:711 */     int minutes = 0;
/* 657:712 */     int seconds = 0;
/* 658:    */     
/* 659:714 */     int mili = 0;
/* 660:716 */     if (parts.length >= 3)
/* 661:    */     {
/* 662:717 */       year = CharScanner.parseInt(parts[0]);
/* 663:718 */       month = CharScanner.parseInt(parts[1]);
/* 664:719 */       day = CharScanner.parseInt(parts[2]);
/* 665:    */     }
/* 666:722 */     if (parts.length >= 6)
/* 667:    */     {
/* 668:723 */       hour = CharScanner.parseInt(parts[3]);
/* 669:724 */       minutes = CharScanner.parseInt(parts[4]);
/* 670:725 */       seconds = CharScanner.parseInt(parts[5]);
/* 671:    */     }
/* 672:728 */     if (parts.length >= 7) {
/* 673:729 */       mili = CharScanner.parseInt(parts[6]);
/* 674:    */     }
/* 675:733 */     return toDate(year, month, day, hour, minutes, seconds, mili);
/* 676:    */   }
/* 677:    */   
/* 678:    */   public static Date fromISO8601Jackson(char[] charArray, int from, int to)
/* 679:    */   {
/* 680:    */     try
/* 681:    */     {
/* 682:739 */       if (isISO8601Jackson(charArray, from, to))
/* 683:    */       {
/* 684:740 */         int year = CharScanner.parseInt(charArray, from + 0, from + 4);
/* 685:741 */         int month = CharScanner.parseInt(charArray, from + 5, from + 7);
/* 686:742 */         int day = CharScanner.parseInt(charArray, from + 8, from + 10);
/* 687:743 */         int hour = CharScanner.parseInt(charArray, from + 11, from + 13);
/* 688:    */         
/* 689:745 */         int minute = CharScanner.parseInt(charArray, from + 14, from + 16);
/* 690:    */         
/* 691:747 */         int second = CharScanner.parseInt(charArray, from + 17, from + 19);
/* 692:748 */         int millisecond = CharScanner.parseInt(charArray, from + 20, from + 23);
/* 693:    */         
/* 694:750 */         TimeZone tz = null;
/* 695:752 */         if (charArray[(from + 19)] == 'Z')
/* 696:    */         {
/* 697:754 */           tz = GMT;
/* 698:    */         }
/* 699:    */         else
/* 700:    */         {
/* 701:759 */           StringBuilder builder = new StringBuilder(8);
/* 702:760 */           builder.append("GMT");
/* 703:762 */           for (int index = from + 23; index < to; index++) {
/* 704:763 */             if (charArray[index] != ':') {
/* 705:766 */               builder.append(charArray[index]);
/* 706:    */             }
/* 707:    */           }
/* 708:768 */           String tzStr = builder.toString();
/* 709:769 */           tz = TimeZone.getTimeZone(tzStr);
/* 710:    */         }
/* 711:771 */         return toDate(tz, year, month, day, hour, minute, second, millisecond);
/* 712:    */       }
/* 713:774 */       return null;
/* 714:    */     }
/* 715:    */     catch (Exception ex) {}
/* 716:777 */     return null;
/* 717:    */   }
/* 718:    */   
/* 719:    */   public static Date fromISO8601(char[] charArray, int from, int to)
/* 720:    */   {
/* 721:    */     try
/* 722:    */     {
/* 723:785 */       int length = to - from;
/* 724:786 */       if (isISO8601(charArray, from, to))
/* 725:    */       {
/* 726:787 */         int year = CharScanner.parseInt(charArray, from + 0, from + 4);
/* 727:788 */         int month = CharScanner.parseInt(charArray, from + 5, from + 7);
/* 728:789 */         int day = CharScanner.parseInt(charArray, from + 8, from + 10);
/* 729:790 */         int hour = CharScanner.parseInt(charArray, from + 11, from + 13);
/* 730:    */         
/* 731:792 */         int minute = CharScanner.parseInt(charArray, from + 14, from + 16);
/* 732:    */         
/* 733:794 */         int second = CharScanner.parseInt(charArray, from + 17, from + 19);
/* 734:795 */         TimeZone tz = null;
/* 735:797 */         if (charArray[(from + 19)] == 'Z')
/* 736:    */         {
/* 737:799 */           tz = GMT;
/* 738:    */         }
/* 739:    */         else
/* 740:    */         {
/* 741:803 */           StringBuilder builder = new StringBuilder(9);
/* 742:804 */           builder.append("GMT");
/* 743:805 */           builder.append(charArray, from + 19, 6);
/* 744:806 */           String tzStr = builder.toString();
/* 745:807 */           tz = TimeZone.getTimeZone(tzStr);
/* 746:    */         }
/* 747:810 */         return toDate(tz, year, month, day, hour, minute, second);
/* 748:    */       }
/* 749:813 */       return null;
/* 750:    */     }
/* 751:    */     catch (Exception ex) {}
/* 752:816 */     return null;
/* 753:    */   }
/* 754:    */   
/* 755:    */   public static Date fromJsonDate(char[] charArray, int from, int to)
/* 756:    */   {
/* 757:    */     try
/* 758:    */     {
/* 759:823 */       if (isJsonDate(charArray, from, to))
/* 760:    */       {
/* 761:824 */         int year = CharScanner.parseInt(charArray, from + 0, from + 4);
/* 762:825 */         int month = CharScanner.parseInt(charArray, from + 5, from + 7);
/* 763:826 */         int day = CharScanner.parseInt(charArray, from + 8, from + 10);
/* 764:827 */         int hour = CharScanner.parseInt(charArray, from + 11, from + 13);
/* 765:    */         
/* 766:829 */         int minute = CharScanner.parseInt(charArray, from + 14, from + 16);
/* 767:    */         
/* 768:831 */         int second = CharScanner.parseInt(charArray, from + 17, from + 19);
/* 769:    */         
/* 770:833 */         int milliseconds = CharScanner.parseInt(charArray, from + 20, from + 23);
/* 771:    */         
/* 772:835 */         TimeZone tz = GMT;
/* 773:    */         
/* 774:    */ 
/* 775:838 */         return toDate(tz, year, month, day, hour, minute, second, milliseconds);
/* 776:    */       }
/* 777:841 */       return null;
/* 778:    */     }
/* 779:    */     catch (Exception ex) {}
/* 780:844 */     return null;
/* 781:    */   }
/* 782:    */   
/* 783:    */   public static boolean isISO8601(String string)
/* 784:    */   {
/* 785:851 */     return isISO8601(FastStringUtils.toCharArray(string));
/* 786:    */   }
/* 787:    */   
/* 788:    */   public static boolean isISO8601(char[] charArray)
/* 789:    */   {
/* 790:856 */     return isISO8601(charArray, 0, charArray.length);
/* 791:    */   }
/* 792:    */   
/* 793:    */   public static boolean isISO8601(char[] charArray, int start, int to)
/* 794:    */   {
/* 795:860 */     boolean valid = true;
/* 796:861 */     int length = to - start;
/* 797:863 */     if (length == SHORT_ISO_8601_TIME_LENGTH)
/* 798:    */     {
/* 799:864 */       valid &= charArray[(start + 19)] == 'Z';
/* 800:    */     }
/* 801:866 */     else if (length == LONG_ISO_8601_TIME_LENGTH)
/* 802:    */     {
/* 803:867 */       valid &= ((charArray[(start + 19)] == '-') || (charArray[(start + 19)] == '+'));
/* 804:868 */       valid &= charArray[(start + 22)] == ':';
/* 805:    */     }
/* 806:    */     else
/* 807:    */     {
/* 808:871 */       return false;
/* 809:    */     }
/* 810:877 */     valid &= ((charArray[(start + 4)] == '-') && (charArray[(start + 7)] == '-') && (charArray[(start + 10)] == 'T') && (charArray[(start + 13)] == ':') && (charArray[(start + 16)] == ':'));
/* 811:    */     
/* 812:    */ 
/* 813:    */ 
/* 814:    */ 
/* 815:    */ 
/* 816:883 */     return valid;
/* 817:    */   }
/* 818:    */   
/* 819:    */   public static boolean isISO8601Jackson(char[] charArray, int start, int to)
/* 820:    */   {
/* 821:887 */     boolean valid = true;
/* 822:888 */     int length = to - start;
/* 823:890 */     if (length == SHORT_ISO_8601_TIME_LENGTH) {
/* 824:891 */       valid &= charArray[(start + 19)] == 'Z';
/* 825:893 */     } else if ((length == LONG_ISO_8601_JACKSON_TIME_LENGTH) || (length == 29)) {
/* 826:894 */       valid &= ((charArray[(start + 23)] == '-') || (charArray[(start + 23)] == '+'));
/* 827:    */     } else {
/* 828:896 */       return false;
/* 829:    */     }
/* 830:902 */     valid &= ((charArray[(start + 4)] == '-') && (charArray[(start + 7)] == '-') && (charArray[(start + 10)] == 'T') && (charArray[(start + 13)] == ':') && (charArray[(start + 16)] == ':'));
/* 831:    */     
/* 832:    */ 
/* 833:    */ 
/* 834:    */ 
/* 835:    */ 
/* 836:908 */     return valid;
/* 837:    */   }
/* 838:    */   
/* 839:    */   public static boolean isISO8601QuickCheck(char[] charArray, int start, int to)
/* 840:    */   {
/* 841:912 */     int length = to - start;
/* 842:    */     try
/* 843:    */     {
/* 844:916 */       if ((length == JSON_TIME_LENGTH) || (length == LONG_ISO_8601_TIME_LENGTH) || (length == SHORT_ISO_8601_TIME_LENGTH) || ((length >= 17) && (charArray[(start + 16)] == ':'))) {
/* 845:919 */         return true;
/* 846:    */       }
/* 847:922 */       return false;
/* 848:    */     }
/* 849:    */     catch (Exception ex)
/* 850:    */     {
/* 851:924 */       ex.printStackTrace();
/* 852:    */     }
/* 853:925 */     return false;
/* 854:    */   }
/* 855:    */   
/* 856:    */   public static boolean isISO8601QuickCheck(char[] charArray)
/* 857:    */   {
/* 858:931 */     int length = charArray.length;
/* 859:933 */     if ((length == JSON_TIME_LENGTH) || (length == LONG_ISO_8601_TIME_LENGTH) || (length == SHORT_ISO_8601_TIME_LENGTH) || ((length >= 16) && (charArray[16] == ':'))) {
/* 860:937 */       if ((length >= 16) && (charArray[16] == ':')) {
/* 861:938 */         return true;
/* 862:    */       }
/* 863:    */     }
/* 864:942 */     return false;
/* 865:    */   }
/* 866:    */   
/* 867:    */   public static boolean isJsonDate(String str)
/* 868:    */   {
/* 869:949 */     return isJsonDate(FastStringUtils.toCharArray(str), 0, str.length());
/* 870:    */   }
/* 871:    */   
/* 872:    */   public static boolean isJsonDate(char[] charArray, int start, int to)
/* 873:    */   {
/* 874:953 */     boolean valid = true;
/* 875:954 */     int length = to - start;
/* 876:956 */     if (length != JSON_TIME_LENGTH) {
/* 877:957 */       return false;
/* 878:    */     }
/* 879:960 */     valid &= charArray[(start + 19)] == '.';
/* 880:962 */     if (!valid) {
/* 881:963 */       return false;
/* 882:    */     }
/* 883:967 */     valid &= ((charArray[(start + 4)] == '-') && (charArray[(start + 7)] == '-') && (charArray[(start + 10)] == 'T') && (charArray[(start + 13)] == ':') && (charArray[(start + 16)] == ':'));
/* 884:    */     
/* 885:    */ 
/* 886:    */ 
/* 887:    */ 
/* 888:    */ 
/* 889:973 */     return valid;
/* 890:    */   }
/* 891:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.Dates
 * JD-Core Version:    0.7.0.1
 */