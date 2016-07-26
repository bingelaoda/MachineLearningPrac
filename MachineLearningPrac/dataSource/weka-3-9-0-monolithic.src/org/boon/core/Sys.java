/*   1:    */ package org.boon.core;
/*   2:    */ 
/*   3:    */ import com.sun.management.UnixOperatingSystemMXBean;
/*   4:    */ import java.io.File;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.lang.management.GarbageCollectorMXBean;
/*   7:    */ import java.lang.management.ManagementFactory;
/*   8:    */ import java.lang.management.MemoryMXBean;
/*   9:    */ import java.lang.management.MemoryUsage;
/*  10:    */ import java.lang.management.OperatingSystemMXBean;
/*  11:    */ import java.lang.management.RuntimeMXBean;
/*  12:    */ import java.lang.management.ThreadMXBean;
/*  13:    */ import java.math.BigDecimal;
/*  14:    */ import java.math.BigInteger;
/*  15:    */ import java.nio.file.Path;
/*  16:    */ import java.util.List;
/*  17:    */ import java.util.concurrent.ConcurrentHashMap;
/*  18:    */ import java.util.concurrent.atomic.AtomicReference;
/*  19:    */ import org.boon.Exceptions;
/*  20:    */ import org.boon.IO;
/*  21:    */ import org.boon.Lists;
/*  22:    */ import org.boon.Str;
/*  23:    */ import org.boon.core.reflection.Annotations;
/*  24:    */ import org.boon.core.reflection.Reflection;
/*  25:    */ import org.boon.core.timer.TimeKeeper;
/*  26:    */ import org.boon.core.timer.TimeKeeperBasic;
/*  27:    */ import org.boon.json.JsonParserAndMapper;
/*  28:    */ import org.boon.json.JsonParserFactory;
/*  29:    */ import org.boon.logging.Logging;
/*  30:    */ 
/*  31:    */ public class Sys
/*  32:    */ {
/*  33: 59 */   public static ConcurrentHashMap<Object, Object> systemProperties = new ConcurrentHashMap(System.getProperties());
/*  34: 61 */   public static ConcurrentHashMap<String, String> env = new ConcurrentHashMap(System.getenv());
/*  35: 64 */   private static final boolean isWindows = System.getProperty("os.name").contains("Windows");
/*  36:    */   private static final boolean inContainer;
/*  37:    */   private static final boolean is1_7OorLater;
/*  38:    */   private static final int buildNumber;
/*  39:    */   private static final BigDecimal version;
/*  40:    */   private static final boolean is1_7;
/*  41:    */   private static final boolean is1_8;
/*  42: 71 */   public static final Object DEFAULT_NULL_NOT_EMPTY = new Object();
/*  43:    */   static final AtomicReference<TimeKeeper> timer;
/*  44:    */   static boolean _oracleJVMAndUnix;
/*  45:    */   
/*  46:    */   public static void println(String message)
/*  47:    */   {
/*  48:143 */     System.out.println(message);
/*  49:    */   }
/*  50:    */   
/*  51:    */   public static void print(String message)
/*  52:    */   {
/*  53:147 */     System.out.print(message);
/*  54:    */   }
/*  55:    */   
/*  56:    */   public static boolean isWindows()
/*  57:    */   {
/*  58:152 */     return isWindows;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public static boolean is1_7OrLater()
/*  62:    */   {
/*  63:156 */     return is1_7OorLater;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public static boolean is1_7()
/*  67:    */   {
/*  68:160 */     return is1_7;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public static boolean is1_8()
/*  72:    */   {
/*  73:163 */     return is1_8;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public static int buildNumber()
/*  77:    */   {
/*  78:167 */     return buildNumber;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public static char windowsPathSeparator()
/*  82:    */   {
/*  83:171 */     return '\\';
/*  84:    */   }
/*  85:    */   
/*  86:    */   public static TimeKeeper timer()
/*  87:    */   {
/*  88:178 */     return (TimeKeeper)timer.get();
/*  89:    */   }
/*  90:    */   
/*  91:    */   public static long time()
/*  92:    */   {
/*  93:182 */     return ((TimeKeeper)timer.get()).time();
/*  94:    */   }
/*  95:    */   
/*  96:    */   private static boolean detectContainer()
/*  97:    */   {
/*  98:    */     boolean _inContainer;
/*  99:    */     try
/* 100:    */     {
/* 101:207 */       Class.forName("javax.servlet.http.HttpServlet");
/* 102:    */       
/* 103:209 */       _inContainer = true;
/* 104:    */     }
/* 105:    */     catch (ClassNotFoundException e)
/* 106:    */     {
/* 107:211 */       _inContainer = false;
/* 108:    */     }
/* 109:213 */     if (!_inContainer) {
/* 110:    */       try
/* 111:    */       {
/* 112:215 */         Class.forName("javax.ejb.EJBContext");
/* 113:    */         
/* 114:217 */         _inContainer = true;
/* 115:    */       }
/* 116:    */       catch (ClassNotFoundException e)
/* 117:    */       {
/* 118:219 */         _inContainer = false;
/* 119:    */       }
/* 120:    */     }
/* 121:224 */     return _inContainer;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public static boolean inContainer()
/* 125:    */   {
/* 126:230 */     return inContainer;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public static Object contextToHold()
/* 130:    */   {
/* 131:238 */     return Lists.list(new Object[] { Reflection.contextToHold(), Annotations.contextToHold(), Logging.contextToHold() });
/* 132:    */   }
/* 133:    */   
/* 134:    */   public static String sysPropMultipleKeys(String... keys)
/* 135:    */   {
/* 136:244 */     for (String key : keys)
/* 137:    */     {
/* 138:245 */       String value = _sysProp(key, DEFAULT_NULL_NOT_EMPTY);
/* 139:246 */       if (value != null) {
/* 140:247 */         return value;
/* 141:    */       }
/* 142:    */     }
/* 143:250 */     return null;
/* 144:    */   }
/* 145:    */   
/* 146:    */   public static String sysProp(String key)
/* 147:    */   {
/* 148:254 */     return _sysProp(key, null);
/* 149:    */   }
/* 150:    */   
/* 151:    */   public static String sysPropDefaultNull(String key)
/* 152:    */   {
/* 153:258 */     return _sysProp(key, DEFAULT_NULL_NOT_EMPTY);
/* 154:    */   }
/* 155:    */   
/* 156:    */   public static String sysProp(String key, Object defaultValue)
/* 157:    */   {
/* 158:272 */     return _sysProp(key, defaultValue);
/* 159:    */   }
/* 160:    */   
/* 161:    */   private static String _sysProp(String key, Object defaultValue)
/* 162:    */   {
/* 163:276 */     String property = (String)systemProperties.get(key);
/* 164:277 */     if (property == null)
/* 165:    */     {
/* 166:278 */       property = (String)env.get(key);
/* 167:280 */       if (property == null)
/* 168:    */       {
/* 169:281 */         String newKey = Str.underBarCase(key);
/* 170:282 */         property = (String)env.get(newKey);
/* 171:284 */         if ((property == null) && (defaultValue != DEFAULT_NULL_NOT_EMPTY)) {
/* 172:285 */           property = Conversions.toString(defaultValue);
/* 173:    */         }
/* 174:    */       }
/* 175:    */     }
/* 176:290 */     return property;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public static boolean sysPropBoolean(String key)
/* 180:    */   {
/* 181:295 */     return sysProp(key, false);
/* 182:    */   }
/* 183:    */   
/* 184:    */   public static boolean sysProp(String key, boolean defaultValue)
/* 185:    */   {
/* 186:301 */     String property = (String)systemProperties.get(key);
/* 187:302 */     if (property == null) {
/* 188:303 */       property = (String)env.get(key);
/* 189:    */     }
/* 190:306 */     if (property == null)
/* 191:    */     {
/* 192:307 */       String newKey = Str.underBarCase(key);
/* 193:308 */       property = (String)env.get(newKey);
/* 194:    */     }
/* 195:311 */     if (property == null) {
/* 196:312 */       return defaultValue;
/* 197:    */     }
/* 198:315 */     return Conversions.toBoolean(property);
/* 199:    */   }
/* 200:    */   
/* 201:    */   public static int sysPropInt(String key)
/* 202:    */   {
/* 203:321 */     return sysProp(key, -1);
/* 204:    */   }
/* 205:    */   
/* 206:    */   public static int sysProp(String key, int defaultValue)
/* 207:    */   {
/* 208:328 */     String property = (String)systemProperties.get(key);
/* 209:329 */     if (property == null) {
/* 210:330 */       property = (String)env.get(key);
/* 211:    */     }
/* 212:333 */     if (property == null)
/* 213:    */     {
/* 214:334 */       String newKey = Str.underBarCase(key);
/* 215:335 */       property = (String)env.get(newKey);
/* 216:    */     }
/* 217:338 */     if (property == null) {
/* 218:339 */       return defaultValue;
/* 219:    */     }
/* 220:342 */     return Conversions.toInt(property);
/* 221:    */   }
/* 222:    */   
/* 223:    */   public static File sysProp(String key, File defaultValue)
/* 224:    */   {
/* 225:348 */     String property = (String)systemProperties.get(key);
/* 226:349 */     if (property == null) {
/* 227:350 */       property = (String)env.get(key);
/* 228:    */     }
/* 229:353 */     if (property == null)
/* 230:    */     {
/* 231:354 */       String newKey = Str.underBarCase(key);
/* 232:355 */       property = (String)env.get(newKey);
/* 233:    */     }
/* 234:358 */     if (property == null) {
/* 235:359 */       return defaultValue;
/* 236:    */     }
/* 237:362 */     return new File(property);
/* 238:    */   }
/* 239:    */   
/* 240:    */   public static Path sysProp(String key, Path defaultValue)
/* 241:    */   {
/* 242:369 */     String property = (String)systemProperties.get(key);
/* 243:370 */     if (property == null) {
/* 244:371 */       property = (String)env.get(key);
/* 245:    */     }
/* 246:374 */     if (property == null)
/* 247:    */     {
/* 248:375 */       String newKey = Str.underBarCase(key);
/* 249:376 */       property = (String)env.get(newKey);
/* 250:    */     }
/* 251:379 */     if (property == null) {
/* 252:380 */       return defaultValue;
/* 253:    */     }
/* 254:383 */     return IO.path(property);
/* 255:    */   }
/* 256:    */   
/* 257:    */   public static int sysPropLong(String key)
/* 258:    */   {
/* 259:388 */     return sysProp(key, -1);
/* 260:    */   }
/* 261:    */   
/* 262:    */   public static long sysProp(String key, long defaultValue)
/* 263:    */   {
/* 264:394 */     String property = (String)systemProperties.get(key);
/* 265:395 */     if (property == null) {
/* 266:396 */       property = (String)env.get(key);
/* 267:    */     }
/* 268:399 */     if (property == null)
/* 269:    */     {
/* 270:400 */       String newKey = Str.underBarCase(key);
/* 271:401 */       property = (String)env.get(newKey);
/* 272:    */     }
/* 273:404 */     if (property == null) {
/* 274:405 */       return defaultValue;
/* 275:    */     }
/* 276:408 */     return Conversions.toLong(property);
/* 277:    */   }
/* 278:    */   
/* 279:    */   public static short sysPropShort(String key)
/* 280:    */   {
/* 281:414 */     return sysProp(key, (short)-1);
/* 282:    */   }
/* 283:    */   
/* 284:    */   public static short sysProp(String key, short defaultValue)
/* 285:    */   {
/* 286:420 */     String property = (String)systemProperties.get(key);
/* 287:421 */     if (property == null) {
/* 288:422 */       property = (String)env.get(key);
/* 289:    */     }
/* 290:425 */     if (property == null)
/* 291:    */     {
/* 292:426 */       String newKey = Str.underBarCase(key);
/* 293:427 */       property = (String)env.get(newKey);
/* 294:    */     }
/* 295:430 */     if (property == null) {
/* 296:431 */       return defaultValue;
/* 297:    */     }
/* 298:434 */     return Conversions.toShort(property);
/* 299:    */   }
/* 300:    */   
/* 301:    */   public static byte sysPropByte(String key)
/* 302:    */   {
/* 303:440 */     return sysProp(key, (byte)-1);
/* 304:    */   }
/* 305:    */   
/* 306:    */   public static byte sysProp(String key, byte defaultValue)
/* 307:    */   {
/* 308:446 */     String property = (String)systemProperties.get(key);
/* 309:447 */     if (property == null) {
/* 310:448 */       property = (String)env.get(key);
/* 311:    */     }
/* 312:451 */     if (property == null)
/* 313:    */     {
/* 314:452 */       String newKey = Str.underBarCase(key);
/* 315:453 */       property = (String)env.get(newKey);
/* 316:    */     }
/* 317:456 */     if (property == null) {
/* 318:457 */       return defaultValue;
/* 319:    */     }
/* 320:460 */     return Conversions.toByte(property);
/* 321:    */   }
/* 322:    */   
/* 323:    */   public static BigDecimal sysPropBigDecimal(String key)
/* 324:    */   {
/* 325:466 */     return sysPropBigDecima(key, (BigDecimal)null);
/* 326:    */   }
/* 327:    */   
/* 328:    */   public static BigDecimal sysPropBigDecima(String key, BigDecimal defaultValue)
/* 329:    */   {
/* 330:472 */     String property = (String)systemProperties.get(key);
/* 331:473 */     if (property == null) {
/* 332:474 */       property = (String)env.get(key);
/* 333:    */     }
/* 334:477 */     if (property == null)
/* 335:    */     {
/* 336:478 */       String newKey = Str.underBarCase(key);
/* 337:479 */       property = (String)env.get(newKey);
/* 338:    */     }
/* 339:482 */     if (property == null) {
/* 340:483 */       return defaultValue;
/* 341:    */     }
/* 342:486 */     return Conversions.toBigDecimal(property);
/* 343:    */   }
/* 344:    */   
/* 345:    */   public static BigInteger sysPropBigInteger(String key)
/* 346:    */   {
/* 347:492 */     return sysPropBigInteger(key, (BigInteger)null);
/* 348:    */   }
/* 349:    */   
/* 350:    */   public static BigInteger sysPropBigInteger(String key, BigInteger defaultValue)
/* 351:    */   {
/* 352:498 */     String property = (String)systemProperties.get(key);
/* 353:499 */     if (property == null) {
/* 354:500 */       property = (String)env.get(key);
/* 355:    */     }
/* 356:503 */     if (property == null)
/* 357:    */     {
/* 358:504 */       String newKey = Str.underBarCase(key);
/* 359:505 */       property = (String)env.get(newKey);
/* 360:    */     }
/* 361:508 */     if (property == null) {
/* 362:509 */       return defaultValue;
/* 363:    */     }
/* 364:512 */     return Conversions.toBigInteger(property);
/* 365:    */   }
/* 366:    */   
/* 367:    */   public static <T extends Enum> T sysPropEnum(Class<T> cls, String key)
/* 368:    */   {
/* 369:518 */     return sysProp(cls, key, null);
/* 370:    */   }
/* 371:    */   
/* 372:    */   public static <T extends Enum> T sysProp(Class<T> cls, String key, T defaultValue)
/* 373:    */   {
/* 374:523 */     String property = (String)systemProperties.get(key);
/* 375:524 */     if (property == null) {
/* 376:525 */       property = (String)env.get(key);
/* 377:    */     }
/* 378:528 */     if (property == null)
/* 379:    */     {
/* 380:529 */       String newKey = Str.underBarCase(key);
/* 381:530 */       property = (String)env.get(newKey);
/* 382:    */     }
/* 383:533 */     if (property == null) {
/* 384:534 */       return defaultValue;
/* 385:    */     }
/* 386:537 */     return Conversions.toEnum(cls, property);
/* 387:    */   }
/* 388:    */   
/* 389:    */   public static String putSysProp(String key, Object value)
/* 390:    */   {
/* 391:545 */     return (String)systemProperties.put(key, Conversions.toString(value));
/* 392:    */   }
/* 393:    */   
/* 394:    */   public static boolean hasSysProp(String propertyName)
/* 395:    */   {
/* 396:549 */     return systemProperties.containsKey(propertyName);
/* 397:    */   }
/* 398:    */   
/* 399:    */   public static void sleep(long duration)
/* 400:    */   {
/* 401:    */     try
/* 402:    */     {
/* 403:554 */       Thread.sleep(duration);
/* 404:    */     }
/* 405:    */     catch (InterruptedException e)
/* 406:    */     {
/* 407:556 */       Thread.interrupted();
/* 408:    */     }
/* 409:    */   }
/* 410:    */   
/* 411:    */   public static int availableProcessors()
/* 412:    */   {
/* 413:561 */     return Runtime.getRuntime().availableProcessors();
/* 414:    */   }
/* 415:    */   
/* 416:    */   public static long freeMemory()
/* 417:    */   {
/* 418:566 */     return Runtime.getRuntime().freeMemory();
/* 419:    */   }
/* 420:    */   
/* 421:    */   public static long totalMemory()
/* 422:    */   {
/* 423:571 */     return Runtime.getRuntime().totalMemory();
/* 424:    */   }
/* 425:    */   
/* 426:    */   public static long maxMemory()
/* 427:    */   {
/* 428:576 */     return Runtime.getRuntime().maxMemory();
/* 429:    */   }
/* 430:    */   
/* 431:    */   static
/* 432:    */   {
/* 433: 75 */     BigDecimal v = new BigDecimal("-1");
/* 434: 76 */     int b = -1;
/* 435: 77 */     String sversion = System.getProperty("java.version");
/* 436: 78 */     if (sversion.indexOf("_") != -1)
/* 437:    */     {
/* 438: 79 */       String[] split = sversion.split("_");
/* 439:    */       try
/* 440:    */       {
/* 441: 82 */         String ver = split[0];
/* 442: 83 */         if (ver.startsWith("1.8")) {
/* 443: 84 */           v = new BigDecimal("1.8");
/* 444:    */         }
/* 445: 86 */         if (ver.startsWith("1.7")) {
/* 446: 87 */           v = new BigDecimal("1.7");
/* 447:    */         }
/* 448: 90 */         if (ver.startsWith("1.6")) {
/* 449: 91 */           v = new BigDecimal("1.6");
/* 450:    */         }
/* 451: 95 */         if (ver.startsWith("1.5")) {
/* 452: 96 */           v = new BigDecimal("1.5");
/* 453:    */         }
/* 454:100 */         if (ver.startsWith("1.9")) {
/* 455:101 */           v = new BigDecimal("1.9");
/* 456:    */         }
/* 457:104 */         String build = split[1];
/* 458:105 */         if (build.endsWith("-ea")) {
/* 459:106 */           build = build.substring(0, build.length() - 3);
/* 460:    */         }
/* 461:108 */         b = Integer.parseInt(build);
/* 462:    */       }
/* 463:    */       catch (Exception ex)
/* 464:    */       {
/* 465:110 */         ex.printStackTrace();
/* 466:111 */         System.err.println("Unable to determine build number or version");
/* 467:    */       }
/* 468:    */     }
/* 469:113 */     else if (("1.8.0".equals(sversion)) || ("1.8.0-ea".equals(sversion)))
/* 470:    */     {
/* 471:114 */       b = -1;
/* 472:115 */       v = new BigDecimal("1.8");
/* 473:    */     }
/* 474:    */     else
/* 475:    */     {
/* 476:    */       try
/* 477:    */       {
/* 478:119 */         v = new BigDecimal(sversion);
/* 479:120 */         b = -1;
/* 480:    */       }
/* 481:    */       catch (Exception ex)
/* 482:    */       {
/* 483:123 */         if (sversion.startsWith("1.7")) {
/* 484:124 */           v = new BigDecimal("1.7");
/* 485:125 */         } else if (sversion.startsWith("1.8")) {
/* 486:126 */           v = new BigDecimal("1.8");
/* 487:    */         } else {
/* 488:128 */           v = new BigDecimal("-1.0");
/* 489:    */         }
/* 490:    */       }
/* 491:    */     }
/* 492:133 */     buildNumber = b;
/* 493:134 */     version = v;
/* 494:    */     
/* 495:136 */     is1_7OorLater = version.compareTo(new BigDecimal("1.7")) >= 0;
/* 496:137 */     is1_7 = version.compareTo(new BigDecimal("1.7")) == 0;
/* 497:138 */     is1_8 = version.compareTo(new BigDecimal("1.8")) == 0;
/* 498:    */     
/* 499:    */ 
/* 500:    */ 
/* 501:    */ 
/* 502:    */ 
/* 503:    */ 
/* 504:    */ 
/* 505:    */ 
/* 506:    */ 
/* 507:    */ 
/* 508:    */ 
/* 509:    */ 
/* 510:    */ 
/* 511:    */ 
/* 512:    */ 
/* 513:    */ 
/* 514:    */ 
/* 515:    */ 
/* 516:    */ 
/* 517:    */ 
/* 518:    */ 
/* 519:    */ 
/* 520:    */ 
/* 521:    */ 
/* 522:    */ 
/* 523:    */ 
/* 524:    */ 
/* 525:    */ 
/* 526:    */ 
/* 527:    */ 
/* 528:    */ 
/* 529:    */ 
/* 530:    */ 
/* 531:    */ 
/* 532:    */ 
/* 533:    */ 
/* 534:175 */     timer = new AtomicReference(new TimeKeeperBasic());
/* 535:    */     
/* 536:    */ 
/* 537:    */ 
/* 538:    */ 
/* 539:    */ 
/* 540:    */ 
/* 541:    */ 
/* 542:    */ 
/* 543:    */ 
/* 544:    */ 
/* 545:    */ 
/* 546:    */ 
/* 547:188 */     boolean forceInContainer = Boolean.parseBoolean(System.getProperty("org.boon.forceInContainer", "false"));
/* 548:189 */     boolean forceNoContainer = Boolean.parseBoolean(System.getProperty("org.boon.forceNoContainer", "false"));
/* 549:    */     boolean _inContainer;
/* 550:    */     boolean _inContainer;
/* 551:191 */     if (forceNoContainer)
/* 552:    */     {
/* 553:192 */       _inContainer = false;
/* 554:    */     }
/* 555:    */     else
/* 556:    */     {
/* 557:    */       boolean _inContainer;
/* 558:193 */       if (forceInContainer) {
/* 559:194 */         _inContainer = true;
/* 560:    */       } else {
/* 561:196 */         _inContainer = detectContainer();
/* 562:    */       }
/* 563:    */     }
/* 564:199 */     inContainer = _inContainer;
/* 565:    */     
/* 566:    */ 
/* 567:    */ 
/* 568:    */ 
/* 569:    */ 
/* 570:    */ 
/* 571:    */ 
/* 572:    */ 
/* 573:    */ 
/* 574:    */ 
/* 575:    */ 
/* 576:    */ 
/* 577:    */ 
/* 578:    */ 
/* 579:    */ 
/* 580:    */ 
/* 581:    */ 
/* 582:    */ 
/* 583:    */ 
/* 584:    */ 
/* 585:    */ 
/* 586:    */ 
/* 587:    */ 
/* 588:    */ 
/* 589:    */ 
/* 590:    */ 
/* 591:    */ 
/* 592:    */ 
/* 593:    */ 
/* 594:    */ 
/* 595:    */ 
/* 596:    */ 
/* 597:    */ 
/* 598:    */ 
/* 599:    */ 
/* 600:    */ 
/* 601:    */ 
/* 602:    */ 
/* 603:    */ 
/* 604:    */ 
/* 605:    */ 
/* 606:    */ 
/* 607:    */ 
/* 608:    */ 
/* 609:    */ 
/* 610:    */ 
/* 611:    */ 
/* 612:    */ 
/* 613:    */ 
/* 614:    */ 
/* 615:    */ 
/* 616:    */ 
/* 617:    */ 
/* 618:    */ 
/* 619:    */ 
/* 620:    */ 
/* 621:    */ 
/* 622:    */ 
/* 623:    */ 
/* 624:    */ 
/* 625:    */ 
/* 626:    */ 
/* 627:    */ 
/* 628:    */ 
/* 629:    */ 
/* 630:    */ 
/* 631:    */ 
/* 632:    */ 
/* 633:    */ 
/* 634:    */ 
/* 635:    */ 
/* 636:    */ 
/* 637:    */ 
/* 638:    */ 
/* 639:    */ 
/* 640:    */ 
/* 641:    */ 
/* 642:    */ 
/* 643:    */ 
/* 644:    */ 
/* 645:    */ 
/* 646:    */ 
/* 647:    */ 
/* 648:    */ 
/* 649:    */ 
/* 650:    */ 
/* 651:    */ 
/* 652:    */ 
/* 653:    */ 
/* 654:    */ 
/* 655:    */ 
/* 656:    */ 
/* 657:    */ 
/* 658:    */ 
/* 659:    */ 
/* 660:    */ 
/* 661:    */ 
/* 662:    */ 
/* 663:    */ 
/* 664:    */ 
/* 665:    */ 
/* 666:    */ 
/* 667:    */ 
/* 668:    */ 
/* 669:    */ 
/* 670:    */ 
/* 671:    */ 
/* 672:    */ 
/* 673:    */ 
/* 674:    */ 
/* 675:    */ 
/* 676:    */ 
/* 677:    */ 
/* 678:    */ 
/* 679:    */ 
/* 680:    */ 
/* 681:    */ 
/* 682:    */ 
/* 683:    */ 
/* 684:    */ 
/* 685:    */ 
/* 686:    */ 
/* 687:    */ 
/* 688:    */ 
/* 689:    */ 
/* 690:    */ 
/* 691:    */ 
/* 692:    */ 
/* 693:    */ 
/* 694:    */ 
/* 695:    */ 
/* 696:    */ 
/* 697:    */ 
/* 698:    */ 
/* 699:    */ 
/* 700:    */ 
/* 701:    */ 
/* 702:    */ 
/* 703:    */ 
/* 704:    */ 
/* 705:    */ 
/* 706:    */ 
/* 707:    */ 
/* 708:    */ 
/* 709:    */ 
/* 710:    */ 
/* 711:    */ 
/* 712:    */ 
/* 713:    */ 
/* 714:    */ 
/* 715:    */ 
/* 716:    */ 
/* 717:    */ 
/* 718:    */ 
/* 719:    */ 
/* 720:    */ 
/* 721:    */ 
/* 722:    */ 
/* 723:    */ 
/* 724:    */ 
/* 725:    */ 
/* 726:    */ 
/* 727:    */ 
/* 728:    */ 
/* 729:    */ 
/* 730:    */ 
/* 731:    */ 
/* 732:    */ 
/* 733:    */ 
/* 734:    */ 
/* 735:    */ 
/* 736:    */ 
/* 737:    */ 
/* 738:    */ 
/* 739:    */ 
/* 740:    */ 
/* 741:    */ 
/* 742:    */ 
/* 743:    */ 
/* 744:    */ 
/* 745:    */ 
/* 746:    */ 
/* 747:    */ 
/* 748:    */ 
/* 749:    */ 
/* 750:    */ 
/* 751:    */ 
/* 752:    */ 
/* 753:    */ 
/* 754:    */ 
/* 755:    */ 
/* 756:    */ 
/* 757:    */ 
/* 758:    */ 
/* 759:    */ 
/* 760:    */ 
/* 761:    */ 
/* 762:    */ 
/* 763:    */ 
/* 764:    */ 
/* 765:    */ 
/* 766:    */ 
/* 767:    */ 
/* 768:    */ 
/* 769:    */ 
/* 770:    */ 
/* 771:    */ 
/* 772:    */ 
/* 773:    */ 
/* 774:    */ 
/* 775:    */ 
/* 776:    */ 
/* 777:    */ 
/* 778:    */ 
/* 779:    */ 
/* 780:    */ 
/* 781:    */ 
/* 782:    */ 
/* 783:    */ 
/* 784:    */ 
/* 785:    */ 
/* 786:    */ 
/* 787:    */ 
/* 788:    */ 
/* 789:    */ 
/* 790:    */ 
/* 791:    */ 
/* 792:    */ 
/* 793:    */ 
/* 794:    */ 
/* 795:    */ 
/* 796:    */ 
/* 797:    */ 
/* 798:    */ 
/* 799:    */ 
/* 800:    */ 
/* 801:    */ 
/* 802:    */ 
/* 803:    */ 
/* 804:    */ 
/* 805:    */ 
/* 806:    */ 
/* 807:    */ 
/* 808:    */ 
/* 809:    */ 
/* 810:    */ 
/* 811:    */ 
/* 812:    */ 
/* 813:    */ 
/* 814:    */ 
/* 815:    */ 
/* 816:    */ 
/* 817:    */ 
/* 818:    */ 
/* 819:    */ 
/* 820:    */ 
/* 821:    */ 
/* 822:    */ 
/* 823:    */ 
/* 824:    */ 
/* 825:    */ 
/* 826:    */ 
/* 827:    */ 
/* 828:    */ 
/* 829:    */ 
/* 830:    */ 
/* 831:    */ 
/* 832:    */ 
/* 833:    */ 
/* 834:    */ 
/* 835:    */ 
/* 836:    */ 
/* 837:    */ 
/* 838:    */ 
/* 839:    */ 
/* 840:    */ 
/* 841:    */ 
/* 842:    */ 
/* 843:    */ 
/* 844:    */ 
/* 845:    */ 
/* 846:    */ 
/* 847:    */ 
/* 848:    */ 
/* 849:    */ 
/* 850:    */ 
/* 851:    */ 
/* 852:    */ 
/* 853:    */ 
/* 854:    */ 
/* 855:    */ 
/* 856:    */ 
/* 857:    */ 
/* 858:    */ 
/* 859:    */ 
/* 860:    */ 
/* 861:    */ 
/* 862:    */ 
/* 863:    */ 
/* 864:    */ 
/* 865:    */ 
/* 866:    */ 
/* 867:    */ 
/* 868:    */ 
/* 869:    */ 
/* 870:    */ 
/* 871:    */ 
/* 872:    */ 
/* 873:    */ 
/* 874:    */ 
/* 875:    */ 
/* 876:    */ 
/* 877:    */ 
/* 878:    */ 
/* 879:    */ 
/* 880:    */ 
/* 881:    */ 
/* 882:    */ 
/* 883:    */ 
/* 884:    */ 
/* 885:    */ 
/* 886:    */ 
/* 887:    */ 
/* 888:    */ 
/* 889:    */ 
/* 890:    */ 
/* 891:    */ 
/* 892:    */ 
/* 893:    */ 
/* 894:    */ 
/* 895:    */ 
/* 896:    */ 
/* 897:    */ 
/* 898:    */ 
/* 899:    */ 
/* 900:    */ 
/* 901:    */ 
/* 902:    */ 
/* 903:    */ 
/* 904:    */ 
/* 905:    */ 
/* 906:    */ 
/* 907:    */ 
/* 908:    */ 
/* 909:    */ 
/* 910:    */ 
/* 911:    */ 
/* 912:    */ 
/* 913:    */ 
/* 914:    */ 
/* 915:    */ 
/* 916:    */ 
/* 917:    */ 
/* 918:    */ 
/* 919:    */ 
/* 920:    */ 
/* 921:    */ 
/* 922:    */ 
/* 923:    */ 
/* 924:    */ 
/* 925:    */ 
/* 926:    */ 
/* 927:    */ 
/* 928:    */ 
/* 929:    */ 
/* 930:    */ 
/* 931:    */ 
/* 932:    */ 
/* 933:    */ 
/* 934:    */ 
/* 935:    */ 
/* 936:    */ 
/* 937:    */ 
/* 938:    */ 
/* 939:    */ 
/* 940:    */ 
/* 941:    */ 
/* 942:    */ 
/* 943:    */ 
/* 944:    */ 
/* 945:580 */     _oracleJVMAndUnix = false;
/* 946:    */     try
/* 947:    */     {
/* 948:583 */       Class.forName("com.sun.management.UnixOperatingSystemMXBean");
/* 949:584 */       _oracleJVMAndUnix = true;
/* 950:    */     }
/* 951:    */     catch (ClassNotFoundException e)
/* 952:    */     {
/* 953:586 */       _oracleJVMAndUnix = false;
/* 954:    */     }
/* 955:    */   }
/* 956:    */   
/* 957:590 */   private static final boolean oracleJVMAndUnix = _oracleJVMAndUnix;
/* 958:    */   
/* 959:    */   public static List<GarbageCollectorMXBean> gc()
/* 960:    */   {
/* 961:594 */     return ManagementFactory.getGarbageCollectorMXBeans();
/* 962:    */   }
/* 963:    */   
/* 964:    */   public static double loadAverage()
/* 965:    */   {
/* 966:598 */     return ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage();
/* 967:    */   }
/* 968:    */   
/* 969:    */   public static long maxFileDescriptorCount()
/* 970:    */   {
/* 971:605 */     if (oracleJVMAndUnix)
/* 972:    */     {
/* 973:607 */       UnixOperatingSystemMXBean unix = (UnixOperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
/* 974:608 */       return unix.getMaxFileDescriptorCount();
/* 975:    */     }
/* 976:610 */     return -1L;
/* 977:    */   }
/* 978:    */   
/* 979:    */   public static long openFileDescriptorCount()
/* 980:    */   {
/* 981:617 */     if (oracleJVMAndUnix)
/* 982:    */     {
/* 983:619 */       UnixOperatingSystemMXBean unix = (UnixOperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
/* 984:620 */       return unix.getOpenFileDescriptorCount();
/* 985:    */     }
/* 986:622 */     return -1L;
/* 987:    */   }
/* 988:    */   
/* 989:    */   public static long committedVirtualMemorySize()
/* 990:    */   {
/* 991:629 */     if (oracleJVMAndUnix)
/* 992:    */     {
/* 993:631 */       UnixOperatingSystemMXBean unix = (UnixOperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
/* 994:632 */       return unix.getCommittedVirtualMemorySize();
/* 995:    */     }
/* 996:634 */     return -1L;
/* 997:    */   }
/* 998:    */   
/* 999:    */   public static long totalSwapSpaceSize()
/* :00:    */   {
/* :01:641 */     if (oracleJVMAndUnix)
/* :02:    */     {
/* :03:643 */       UnixOperatingSystemMXBean unix = (UnixOperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
/* :04:644 */       return unix.getTotalSwapSpaceSize();
/* :05:    */     }
/* :06:646 */     return -1L;
/* :07:    */   }
/* :08:    */   
/* :09:    */   public static long freeSwapSpaceSize()
/* :10:    */   {
/* :11:653 */     if (oracleJVMAndUnix)
/* :12:    */     {
/* :13:655 */       UnixOperatingSystemMXBean unix = (UnixOperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
/* :14:656 */       return unix.getFreeSwapSpaceSize();
/* :15:    */     }
/* :16:658 */     return -1L;
/* :17:    */   }
/* :18:    */   
/* :19:    */   public static long processCpuTime()
/* :20:    */   {
/* :21:665 */     if (oracleJVMAndUnix)
/* :22:    */     {
/* :23:667 */       UnixOperatingSystemMXBean unix = (UnixOperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
/* :24:668 */       return unix.getProcessCpuTime();
/* :25:    */     }
/* :26:670 */     return -1L;
/* :27:    */   }
/* :28:    */   
/* :29:    */   public static long freePhysicalMemorySize()
/* :30:    */   {
/* :31:677 */     if (oracleJVMAndUnix)
/* :32:    */     {
/* :33:679 */       UnixOperatingSystemMXBean unix = (UnixOperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
/* :34:680 */       return unix.getFreePhysicalMemorySize();
/* :35:    */     }
/* :36:682 */     return -1L;
/* :37:    */   }
/* :38:    */   
/* :39:    */   public static long totalPhysicalMemorySize()
/* :40:    */   {
/* :41:689 */     if (oracleJVMAndUnix)
/* :42:    */     {
/* :43:691 */       UnixOperatingSystemMXBean unix = (UnixOperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
/* :44:692 */       return unix.getTotalPhysicalMemorySize();
/* :45:    */     }
/* :46:694 */     return -1L;
/* :47:    */   }
/* :48:    */   
/* :49:    */   public static double systemCpuLoad()
/* :50:    */   {
/* :51:702 */     if (oracleJVMAndUnix)
/* :52:    */     {
/* :53:704 */       UnixOperatingSystemMXBean unix = (UnixOperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
/* :54:705 */       return unix.getSystemCpuLoad();
/* :55:    */     }
/* :56:707 */     return -1.0D;
/* :57:    */   }
/* :58:    */   
/* :59:    */   public static double processCpuLoad()
/* :60:    */   {
/* :61:714 */     if (oracleJVMAndUnix)
/* :62:    */     {
/* :63:716 */       UnixOperatingSystemMXBean unix = (UnixOperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
/* :64:717 */       return unix.getProcessCpuLoad();
/* :65:    */     }
/* :66:719 */     return -1.0D;
/* :67:    */   }
/* :68:    */   
/* :69:    */   public static long uptime()
/* :70:    */   {
/* :71:726 */     return ManagementFactory.getRuntimeMXBean().getUptime();
/* :72:    */   }
/* :73:    */   
/* :74:    */   public static long startTime()
/* :75:    */   {
/* :76:730 */     return ManagementFactory.getRuntimeMXBean().getStartTime();
/* :77:    */   }
/* :78:    */   
/* :79:    */   public static int pendingFinalizationCount()
/* :80:    */   {
/* :81:734 */     return ManagementFactory.getMemoryMXBean().getObjectPendingFinalizationCount();
/* :82:    */   }
/* :83:    */   
/* :84:    */   public static MemoryUsage heapMemoryUsage()
/* :85:    */   {
/* :86:739 */     return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
/* :87:    */   }
/* :88:    */   
/* :89:    */   public static MemoryUsage nonHeapMemoryUsage()
/* :90:    */   {
/* :91:743 */     return ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage();
/* :92:    */   }
/* :93:    */   
/* :94:    */   public static int threadPeakCount()
/* :95:    */   {
/* :96:749 */     return ManagementFactory.getThreadMXBean().getPeakThreadCount();
/* :97:    */   }
/* :98:    */   
/* :99:    */   public static int threadCount()
/* ;00:    */   {
/* ;01:755 */     return ManagementFactory.getThreadMXBean().getThreadCount();
/* ;02:    */   }
/* ;03:    */   
/* ;04:    */   public static long threadsStarted()
/* ;05:    */   {
/* ;06:761 */     return ManagementFactory.getThreadMXBean().getTotalStartedThreadCount();
/* ;07:    */   }
/* ;08:    */   
/* ;09:    */   public static long threadCPUTime()
/* ;10:    */   {
/* ;11:766 */     return ManagementFactory.getThreadMXBean().getCurrentThreadCpuTime();
/* ;12:    */   }
/* ;13:    */   
/* ;14:    */   public static long threadUserTime()
/* ;15:    */   {
/* ;16:771 */     return ManagementFactory.getThreadMXBean().getCurrentThreadUserTime();
/* ;17:    */   }
/* ;18:    */   
/* ;19:    */   public static int threadDaemonCount()
/* ;20:    */   {
/* ;21:777 */     return ManagementFactory.getThreadMXBean().getDaemonThreadCount();
/* ;22:    */   }
/* ;23:    */   
/* ;24:    */   public static <T> T loadFromFileLocation(Class<T> clazz, String... fileLocations)
/* ;25:    */   {
/* ;26:781 */     for (String fileLocation : fileLocations) {
/* ;27:782 */       if ((fileLocation != null) && (IO.exists(fileLocation))) {
/* ;28:    */         try
/* ;29:    */         {
/* ;30:784 */           return new JsonParserFactory().create().parseFile(clazz, fileLocation);
/* ;31:    */         }
/* ;32:    */         catch (Exception ex)
/* ;33:    */         {
/* ;34:787 */           ex.printStackTrace();
/* ;35:788 */           Exceptions.handle(ex, new Object[] { "Unable to read file from ", fileLocation });
/* ;36:789 */           return null;
/* ;37:    */         }
/* ;38:    */       }
/* ;39:    */     }
/* ;40:    */     try
/* ;41:    */     {
/* ;42:795 */       return clazz.newInstance();
/* ;43:    */     }
/* ;44:    */     catch (InstantiationException|IllegalAccessException e)
/* ;45:    */     {
/* ;46:798 */       Exceptions.handle(e, new Object[] { "Unable to create instance of " + clazz.getName() });
/* ;47:    */     }
/* ;48:799 */     return null;
/* ;49:    */   }
/* ;50:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.core.Sys
 * JD-Core Version:    0.7.0.1
 */