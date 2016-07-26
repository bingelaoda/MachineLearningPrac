/*   1:    */ package org.boon.json.implementation;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.Reader;
/*   6:    */ import java.math.BigDecimal;
/*   7:    */ import java.math.BigInteger;
/*   8:    */ import java.nio.charset.Charset;
/*   9:    */ import java.nio.charset.StandardCharsets;
/*  10:    */ import java.nio.file.Files;
/*  11:    */ import java.nio.file.Path;
/*  12:    */ import java.util.Date;
/*  13:    */ import java.util.List;
/*  14:    */ import java.util.Map;
/*  15:    */ import org.boon.Exceptions;
/*  16:    */ import org.boon.IO;
/*  17:    */ import org.boon.core.Conversions;
/*  18:    */ import org.boon.core.Typ;
/*  19:    */ import org.boon.core.TypeType;
/*  20:    */ import org.boon.core.Value;
/*  21:    */ import org.boon.core.reflection.MapObjectConversion;
/*  22:    */ import org.boon.core.reflection.Mapper;
/*  23:    */ import org.boon.json.JsonParser;
/*  24:    */ import org.boon.json.JsonParserAndMapper;
/*  25:    */ import org.boon.primitive.CharBuf;
/*  26:    */ import org.boon.primitive.InMemoryInputStream;
/*  27:    */ 
/*  28:    */ public class BaseJsonParserAndMapper
/*  29:    */   implements JsonParserAndMapper
/*  30:    */ {
/*  31:    */   protected final JsonParser parser;
/*  32: 60 */   private final CharBuf builder = CharBuf.create(20);
/*  33:    */   private final Mapper mapper;
/*  34: 63 */   protected Charset charset = StandardCharsets.UTF_8;
/*  35: 66 */   protected int bufSize = 1024;
/*  36:    */   private char[] copyBuf;
/*  37:    */   private CharBuf fileInputBuf;
/*  38:    */   
/*  39:    */   public BaseJsonParserAndMapper(JsonParser parser, Mapper mapper)
/*  40:    */   {
/*  41: 71 */     this.parser = parser;
/*  42: 72 */     this.mapper = mapper;
/*  43:    */   }
/*  44:    */   
/*  45:    */   protected final <T> T convert(Class<T> clz, Object object)
/*  46:    */   {
/*  47: 79 */     if (object == null) {
/*  48: 80 */       return null;
/*  49:    */     }
/*  50: 83 */     TypeType coerceTo = TypeType.getType(clz);
/*  51: 85 */     switch (1.$SwitchMap$org$boon$core$TypeType[coerceTo.ordinal()])
/*  52:    */     {
/*  53:    */     case 1: 
/*  54:    */     case 2: 
/*  55:    */     case 3: 
/*  56: 89 */       return object;
/*  57:    */     }
/*  58: 93 */     TypeType coerceFrom = TypeType.getType(object.getClass());
/*  59: 95 */     switch (1.$SwitchMap$org$boon$core$TypeType[coerceFrom.ordinal()])
/*  60:    */     {
/*  61:    */     case 4: 
/*  62: 98 */       return this.mapper.fromValueMap((Map)object, clz);
/*  63:    */     case 1: 
/*  64:101 */       return this.mapper.fromMap((Map)object, clz);
/*  65:    */     case 5: 
/*  66:104 */       return ((Value)object).toValue();
/*  67:    */     case 2: 
/*  68:107 */       return this.mapper.convertListOfMapsToObjects((List)object, clz);
/*  69:    */     }
/*  70:110 */     if (Typ.isBasicTypeOrCollection(clz)) {
/*  71:111 */       return Conversions.coerce(coerceTo, clz, object);
/*  72:    */     }
/*  73:113 */     return object;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setCharset(Charset charset)
/*  77:    */   {
/*  78:121 */     this.charset = charset;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public Map<String, Object> parseMap(String jsonString)
/*  82:    */   {
/*  83:127 */     return (Map)parse(jsonString);
/*  84:    */   }
/*  85:    */   
/*  86:    */   public <T> List<T> parseList(Class<T> componentType, String jsonString)
/*  87:    */   {
/*  88:132 */     List<Object> list = (List)parse(List.class, jsonString);
/*  89:133 */     return convertList(componentType, list);
/*  90:    */   }
/*  91:    */   
/*  92:    */   private <T> List<T> convertList(Class<T> componentType, List<Object> list)
/*  93:    */   {
/*  94:138 */     List l = list;
/*  95:139 */     return MapObjectConversion.convertListOfMapsToObjects(componentType, l);
/*  96:    */   }
/*  97:    */   
/*  98:    */   public <T> List<T> parseList(Class<T> componentType, Reader reader)
/*  99:    */   {
/* 100:145 */     List<Object> list = (List)parse(List.class, reader);
/* 101:146 */     return convertList(componentType, list);
/* 102:    */   }
/* 103:    */   
/* 104:    */   public <T> List<T> parseList(Class<T> componentType, InputStream input)
/* 105:    */   {
/* 106:151 */     List<Object> list = (List)parse(List.class, input);
/* 107:152 */     return convertList(componentType, list);
/* 108:    */   }
/* 109:    */   
/* 110:    */   public <T> List<T> parseList(Class<T> componentType, InputStream input, Charset charset)
/* 111:    */   {
/* 112:157 */     List<Object> list = (List)parse(List.class, input, charset);
/* 113:158 */     return convertList(componentType, list);
/* 114:    */   }
/* 115:    */   
/* 116:    */   public <T> List<T> parseList(Class<T> componentType, byte[] jsonBytes)
/* 117:    */   {
/* 118:163 */     List<Object> list = (List)parse(List.class, jsonBytes);
/* 119:164 */     return convertList(componentType, list);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public <T> List<T> parseList(Class<T> componentType, byte[] jsonBytes, Charset charset)
/* 123:    */   {
/* 124:169 */     List<Object> list = (List)parse(List.class, jsonBytes, charset);
/* 125:170 */     return convertList(componentType, list);
/* 126:    */   }
/* 127:    */   
/* 128:    */   public <T> List<T> parseList(Class<T> componentType, char[] chars)
/* 129:    */   {
/* 130:175 */     List<Object> list = (List)parse(List.class, chars);
/* 131:176 */     return convertList(componentType, list);
/* 132:    */   }
/* 133:    */   
/* 134:    */   public <T> List<T> parseList(Class<T> componentType, CharSequence jsonSeq)
/* 135:    */   {
/* 136:181 */     List<Object> list = (List)parse(List.class, jsonSeq);
/* 137:182 */     return convertList(componentType, list);
/* 138:    */   }
/* 139:    */   
/* 140:    */   public <T> List<T> parseListFromFile(Class<T> componentType, String fileName)
/* 141:    */   {
/* 142:187 */     List<Object> list = (List)parseFile(List.class, fileName);
/* 143:188 */     return convertList(componentType, list);
/* 144:    */   }
/* 145:    */   
/* 146:    */   public <T> T parse(Class<T> type, String jsonString)
/* 147:    */   {
/* 148:194 */     return convert(type, parse(jsonString));
/* 149:    */   }
/* 150:    */   
/* 151:    */   public <T> T parse(Class<T> type, byte[] bytes)
/* 152:    */   {
/* 153:199 */     return convert(type, parse(bytes));
/* 154:    */   }
/* 155:    */   
/* 156:    */   public <T> T parse(Class<T> type, byte[] bytes, Charset charset)
/* 157:    */   {
/* 158:204 */     return convert(type, parse(bytes, charset));
/* 159:    */   }
/* 160:    */   
/* 161:    */   public Date parseDate(String jsonString)
/* 162:    */   {
/* 163:210 */     return Conversions.toDate(parse(jsonString));
/* 164:    */   }
/* 165:    */   
/* 166:    */   public Date parseDate(InputStream input)
/* 167:    */   {
/* 168:215 */     return Conversions.toDate(parse(input));
/* 169:    */   }
/* 170:    */   
/* 171:    */   public Date parseDate(InputStream input, Charset charset)
/* 172:    */   {
/* 173:220 */     return Conversions.toDate(parse(input));
/* 174:    */   }
/* 175:    */   
/* 176:    */   public Date parseDate(byte[] jsonBytes)
/* 177:    */   {
/* 178:225 */     return Conversions.toDate(parse(jsonBytes));
/* 179:    */   }
/* 180:    */   
/* 181:    */   public Date parseDate(byte[] jsonBytes, Charset charset)
/* 182:    */   {
/* 183:230 */     return Conversions.toDate(parse(jsonBytes, charset));
/* 184:    */   }
/* 185:    */   
/* 186:    */   public Date parseDate(char[] chars)
/* 187:    */   {
/* 188:235 */     return Conversions.toDate(parse(chars));
/* 189:    */   }
/* 190:    */   
/* 191:    */   public Date parseDate(CharSequence jsonSeq)
/* 192:    */   {
/* 193:240 */     return Conversions.toDate(parse(jsonSeq));
/* 194:    */   }
/* 195:    */   
/* 196:    */   public Date parseDateFromFile(String fileName)
/* 197:    */   {
/* 198:245 */     return Conversions.toDate(parseFile(fileName));
/* 199:    */   }
/* 200:    */   
/* 201:    */   public float[] parseFloatArray(String jsonString)
/* 202:    */   {
/* 203:250 */     List<Object> list = (List)parse(jsonString);
/* 204:251 */     return Conversions.farray(list);
/* 205:    */   }
/* 206:    */   
/* 207:    */   public double[] parseDoubleArray(String jsonString)
/* 208:    */   {
/* 209:256 */     List<Object> list = (List)parse(jsonString);
/* 210:257 */     return Conversions.darray(list);
/* 211:    */   }
/* 212:    */   
/* 213:    */   public long[] parseLongArray(String jsonString)
/* 214:    */   {
/* 215:262 */     List<Object> list = (List)parse(jsonString);
/* 216:263 */     return Conversions.larray(list);
/* 217:    */   }
/* 218:    */   
/* 219:    */   public int[] parseIntArray(String jsonString)
/* 220:    */   {
/* 221:268 */     List<Object> list = (List)parse(jsonString);
/* 222:269 */     return Conversions.iarray(list);
/* 223:    */   }
/* 224:    */   
/* 225:    */   public <T extends Enum> T parseEnum(Class<T> type, String jsonString)
/* 226:    */   {
/* 227:274 */     Object obj = parse(jsonString);
/* 228:275 */     return Conversions.toEnum(type, obj);
/* 229:    */   }
/* 230:    */   
/* 231:    */   public short parseShort(String jsonString)
/* 232:    */   {
/* 233:280 */     return Conversions.toShort(parse(jsonString));
/* 234:    */   }
/* 235:    */   
/* 236:    */   public byte parseByte(String jsonString)
/* 237:    */   {
/* 238:286 */     return Conversions.toByte(parse(jsonString));
/* 239:    */   }
/* 240:    */   
/* 241:    */   public char parseChar(String jsonString)
/* 242:    */   {
/* 243:291 */     return Conversions.toChar(parse(jsonString));
/* 244:    */   }
/* 245:    */   
/* 246:    */   public char[] parseCharArray(String jsonString)
/* 247:    */   {
/* 248:296 */     return Conversions.carray(parse(jsonString));
/* 249:    */   }
/* 250:    */   
/* 251:    */   public byte[] parseByteArray(String jsonString)
/* 252:    */   {
/* 253:301 */     return Conversions.barray(parse(jsonString));
/* 254:    */   }
/* 255:    */   
/* 256:    */   public short[] parseShortArray(String jsonString)
/* 257:    */   {
/* 258:306 */     return Conversions.sarray(parse(jsonString));
/* 259:    */   }
/* 260:    */   
/* 261:    */   public int parseInt(String jsonString)
/* 262:    */   {
/* 263:313 */     return Conversions.toInt(parse(jsonString));
/* 264:    */   }
/* 265:    */   
/* 266:    */   public int parseInt(InputStream input)
/* 267:    */   {
/* 268:318 */     return Conversions.toInt(parse(input));
/* 269:    */   }
/* 270:    */   
/* 271:    */   public int parseInt(InputStream input, Charset charset)
/* 272:    */   {
/* 273:323 */     return Conversions.toInt(parse(input, charset));
/* 274:    */   }
/* 275:    */   
/* 276:    */   public int parseInt(byte[] jsonBytes)
/* 277:    */   {
/* 278:328 */     return Conversions.toInt(parse(jsonBytes));
/* 279:    */   }
/* 280:    */   
/* 281:    */   public int parseInt(byte[] jsonBytes, Charset charset)
/* 282:    */   {
/* 283:333 */     return Conversions.toInt(parse(jsonBytes, charset));
/* 284:    */   }
/* 285:    */   
/* 286:    */   public int parseInt(char[] chars)
/* 287:    */   {
/* 288:338 */     return Conversions.toInt(parse(chars));
/* 289:    */   }
/* 290:    */   
/* 291:    */   public int parseInt(CharSequence jsonSeq)
/* 292:    */   {
/* 293:343 */     return Conversions.toInt(parse(jsonSeq));
/* 294:    */   }
/* 295:    */   
/* 296:    */   public int parseIntFromFile(String fileName)
/* 297:    */   {
/* 298:348 */     return Conversions.toInt(parseFile(fileName));
/* 299:    */   }
/* 300:    */   
/* 301:    */   public long parseLong(String jsonString)
/* 302:    */   {
/* 303:356 */     return Conversions.toLong(parse(jsonString));
/* 304:    */   }
/* 305:    */   
/* 306:    */   public long parseLong(InputStream input)
/* 307:    */   {
/* 308:361 */     return Conversions.toLong(parse(input));
/* 309:    */   }
/* 310:    */   
/* 311:    */   public long parseLong(InputStream input, Charset charset)
/* 312:    */   {
/* 313:366 */     return Conversions.toLong(parse(input, charset));
/* 314:    */   }
/* 315:    */   
/* 316:    */   public long parseLong(byte[] jsonBytes)
/* 317:    */   {
/* 318:371 */     return Conversions.toLong(parse(jsonBytes));
/* 319:    */   }
/* 320:    */   
/* 321:    */   public long parseLong(byte[] jsonBytes, Charset charset)
/* 322:    */   {
/* 323:376 */     return Conversions.toLong(parse(jsonBytes, charset));
/* 324:    */   }
/* 325:    */   
/* 326:    */   public long parseLong(char[] chars)
/* 327:    */   {
/* 328:381 */     return Conversions.toLong(parse(chars));
/* 329:    */   }
/* 330:    */   
/* 331:    */   public long parseLong(CharSequence jsonSeq)
/* 332:    */   {
/* 333:386 */     return Conversions.toLong(parse(jsonSeq));
/* 334:    */   }
/* 335:    */   
/* 336:    */   public long parseLongFromFile(String fileName)
/* 337:    */   {
/* 338:391 */     return Conversions.toLong(parseFile(fileName));
/* 339:    */   }
/* 340:    */   
/* 341:    */   public String parseString(String value)
/* 342:    */   {
/* 343:396 */     return Conversions.toString(parse(value));
/* 344:    */   }
/* 345:    */   
/* 346:    */   public String parseString(InputStream value)
/* 347:    */   {
/* 348:401 */     return Conversions.toString(parse(value));
/* 349:    */   }
/* 350:    */   
/* 351:    */   public String parseString(InputStream value, Charset charset)
/* 352:    */   {
/* 353:406 */     return Conversions.toString(parse(value, charset));
/* 354:    */   }
/* 355:    */   
/* 356:    */   public String parseString(byte[] value)
/* 357:    */   {
/* 358:411 */     return Conversions.toString(parse(value));
/* 359:    */   }
/* 360:    */   
/* 361:    */   public String parseString(byte[] value, Charset charset)
/* 362:    */   {
/* 363:416 */     return Conversions.toString(parse(value, charset));
/* 364:    */   }
/* 365:    */   
/* 366:    */   public String parseString(char[] value)
/* 367:    */   {
/* 368:421 */     return Conversions.toString(parse(value));
/* 369:    */   }
/* 370:    */   
/* 371:    */   public String parseString(CharSequence value)
/* 372:    */   {
/* 373:426 */     return Conversions.toString(parse(value));
/* 374:    */   }
/* 375:    */   
/* 376:    */   public String parseStringFromFile(String value)
/* 377:    */   {
/* 378:431 */     return Conversions.toString(parseFile(value));
/* 379:    */   }
/* 380:    */   
/* 381:    */   public double parseDouble(String value)
/* 382:    */   {
/* 383:437 */     return Conversions.toDouble(parse(value));
/* 384:    */   }
/* 385:    */   
/* 386:    */   public double parseDouble(InputStream value)
/* 387:    */   {
/* 388:442 */     return Conversions.toDouble(parse(value));
/* 389:    */   }
/* 390:    */   
/* 391:    */   public double parseDouble(byte[] value)
/* 392:    */   {
/* 393:447 */     return Conversions.toDouble(parse(value));
/* 394:    */   }
/* 395:    */   
/* 396:    */   public double parseDouble(char[] value)
/* 397:    */   {
/* 398:452 */     return Conversions.toDouble(parse(value));
/* 399:    */   }
/* 400:    */   
/* 401:    */   public double parseDouble(CharSequence value)
/* 402:    */   {
/* 403:457 */     return Conversions.toDouble(parse(value));
/* 404:    */   }
/* 405:    */   
/* 406:    */   public double parseDouble(byte[] value, Charset charset)
/* 407:    */   {
/* 408:462 */     return Conversions.toDouble(parse(value, charset));
/* 409:    */   }
/* 410:    */   
/* 411:    */   public double parseDouble(InputStream value, Charset charset)
/* 412:    */   {
/* 413:467 */     return Conversions.toDouble(parse(value, charset));
/* 414:    */   }
/* 415:    */   
/* 416:    */   public double parseDoubleFromFile(String fileName)
/* 417:    */   {
/* 418:472 */     return Conversions.toDouble(parseFile(fileName));
/* 419:    */   }
/* 420:    */   
/* 421:    */   public float parseFloat(String value)
/* 422:    */   {
/* 423:478 */     return Conversions.toFloat(parse(value));
/* 424:    */   }
/* 425:    */   
/* 426:    */   public float parseFloat(InputStream value)
/* 427:    */   {
/* 428:483 */     return Conversions.toFloat(parse(value));
/* 429:    */   }
/* 430:    */   
/* 431:    */   public float parseFloat(byte[] value)
/* 432:    */   {
/* 433:488 */     return Conversions.toFloat(parse(value));
/* 434:    */   }
/* 435:    */   
/* 436:    */   public float parseFloat(char[] value)
/* 437:    */   {
/* 438:493 */     return Conversions.toFloat(parse(value));
/* 439:    */   }
/* 440:    */   
/* 441:    */   public float parseFloat(CharSequence value)
/* 442:    */   {
/* 443:498 */     return Conversions.toFloat(parse(value));
/* 444:    */   }
/* 445:    */   
/* 446:    */   public float parseFloat(byte[] value, Charset charset)
/* 447:    */   {
/* 448:503 */     return Conversions.toFloat(parse(value, charset));
/* 449:    */   }
/* 450:    */   
/* 451:    */   public float parseFloat(InputStream value, Charset charset)
/* 452:    */   {
/* 453:508 */     return Conversions.toFloat(parse(value, charset));
/* 454:    */   }
/* 455:    */   
/* 456:    */   public float parseFloatFromFile(String fileName)
/* 457:    */   {
/* 458:513 */     return Conversions.toFloat(parseFile(fileName));
/* 459:    */   }
/* 460:    */   
/* 461:    */   public BigDecimal parseBigDecimal(String value)
/* 462:    */   {
/* 463:521 */     return Conversions.toBigDecimal(parse(value));
/* 464:    */   }
/* 465:    */   
/* 466:    */   public BigDecimal parseBigDecimal(InputStream value)
/* 467:    */   {
/* 468:526 */     return Conversions.toBigDecimal(parse(value));
/* 469:    */   }
/* 470:    */   
/* 471:    */   public BigDecimal parseBigDecimal(byte[] value)
/* 472:    */   {
/* 473:531 */     return Conversions.toBigDecimal(parse(value));
/* 474:    */   }
/* 475:    */   
/* 476:    */   public BigDecimal parseBigDecimal(char[] value)
/* 477:    */   {
/* 478:536 */     return Conversions.toBigDecimal(parse(value));
/* 479:    */   }
/* 480:    */   
/* 481:    */   public BigDecimal parseBigDecimal(CharSequence value)
/* 482:    */   {
/* 483:541 */     return Conversions.toBigDecimal(parse(value));
/* 484:    */   }
/* 485:    */   
/* 486:    */   public BigDecimal parseBigDecimal(byte[] value, Charset charset)
/* 487:    */   {
/* 488:546 */     return Conversions.toBigDecimal(parse(value, charset));
/* 489:    */   }
/* 490:    */   
/* 491:    */   public BigDecimal parseBigDecimal(InputStream value, Charset charset)
/* 492:    */   {
/* 493:551 */     return Conversions.toBigDecimal(parse(value, charset));
/* 494:    */   }
/* 495:    */   
/* 496:    */   public BigDecimal parseBigDecimalFromFile(String fileName)
/* 497:    */   {
/* 498:556 */     return Conversions.toBigDecimal(parseFile(fileName));
/* 499:    */   }
/* 500:    */   
/* 501:    */   public BigInteger parseBigInteger(String value)
/* 502:    */   {
/* 503:564 */     return Conversions.toBigInteger(parse(value));
/* 504:    */   }
/* 505:    */   
/* 506:    */   public BigInteger parseBigInteger(InputStream value)
/* 507:    */   {
/* 508:569 */     return Conversions.toBigInteger(parse(value));
/* 509:    */   }
/* 510:    */   
/* 511:    */   public BigInteger parseBigInteger(byte[] value)
/* 512:    */   {
/* 513:574 */     return Conversions.toBigInteger(parse(value));
/* 514:    */   }
/* 515:    */   
/* 516:    */   public BigInteger parseBigInteger(char[] value)
/* 517:    */   {
/* 518:579 */     return Conversions.toBigInteger(parse(value));
/* 519:    */   }
/* 520:    */   
/* 521:    */   public BigInteger parseBigInteger(CharSequence value)
/* 522:    */   {
/* 523:584 */     return Conversions.toBigInteger(parse(value));
/* 524:    */   }
/* 525:    */   
/* 526:    */   public BigInteger parseBigInteger(byte[] value, Charset charset)
/* 527:    */   {
/* 528:589 */     return Conversions.toBigInteger(parse(value, charset));
/* 529:    */   }
/* 530:    */   
/* 531:    */   public BigInteger parseBigInteger(InputStream value, Charset charset)
/* 532:    */   {
/* 533:594 */     return Conversions.toBigInteger(parse(value, charset));
/* 534:    */   }
/* 535:    */   
/* 536:    */   public BigInteger parseBigIntegerFile(String fileName)
/* 537:    */   {
/* 538:599 */     return Conversions.toBigInteger(parseFile(fileName));
/* 539:    */   }
/* 540:    */   
/* 541:    */   public Object parseDirect(byte[] value)
/* 542:    */   {
/* 543:608 */     this.builder.addAsUTF(value);
/* 544:609 */     return parse(this.builder.readForRecycle());
/* 545:    */   }
/* 546:    */   
/* 547:    */   public <T> T parseDirect(Class<T> type, byte[] value)
/* 548:    */   {
/* 549:615 */     this.builder.addAsUTF(value);
/* 550:616 */     return parse(type, this.builder.readForRecycle());
/* 551:    */   }
/* 552:    */   
/* 553:    */   public <T> T parseAsStream(Class<T> type, byte[] value)
/* 554:    */   {
/* 555:624 */     return parse(type, new InMemoryInputStream(value));
/* 556:    */   }
/* 557:    */   
/* 558:    */   public <T> T parse(Class<T> type, CharSequence charSequence)
/* 559:    */   {
/* 560:630 */     return parse(type, charSequence.toString());
/* 561:    */   }
/* 562:    */   
/* 563:    */   public <T> T parse(Class<T> type, char[] chars)
/* 564:    */   {
/* 565:635 */     return convert(type, parse(chars));
/* 566:    */   }
/* 567:    */   
/* 568:    */   public Object parseAsStream(byte[] value)
/* 569:    */   {
/* 570:640 */     return parse(new InMemoryInputStream(value));
/* 571:    */   }
/* 572:    */   
/* 573:    */   public Object parseFile(String fileName)
/* 574:    */   {
/* 575:    */     try
/* 576:    */     {
/* 577:646 */       Path filePath = IO.path(fileName);
/* 578:647 */       long size = Files.size(filePath);
/* 579:648 */       size = size > 2000000000L ? 1000000L : size;
/* 580:649 */       if (this.copyBuf == null) {
/* 581:650 */         this.copyBuf = new char[this.bufSize];
/* 582:    */       }
/* 583:653 */       Reader reader = Files.newBufferedReader(IO.path(fileName), this.charset);
/* 584:654 */       this.fileInputBuf = IO.read(reader, this.fileInputBuf, (int)size, this.copyBuf);
/* 585:655 */       return parse(this.fileInputBuf.readForRecycle());
/* 586:    */     }
/* 587:    */     catch (IOException ex)
/* 588:    */     {
/* 589:657 */       return Exceptions.handle(Object.class, fileName, ex);
/* 590:    */     }
/* 591:    */   }
/* 592:    */   
/* 593:    */   public void close() {}
/* 594:    */   
/* 595:    */   public <T> T parse(Class<T> type, Reader reader)
/* 596:    */   {
/* 597:673 */     if (this.copyBuf == null) {
/* 598:674 */       this.copyBuf = new char[this.bufSize];
/* 599:    */     }
/* 600:677 */     this.fileInputBuf = IO.read(reader, this.fileInputBuf, this.bufSize, this.copyBuf);
/* 601:678 */     return parse(type, this.fileInputBuf.readForRecycle());
/* 602:    */   }
/* 603:    */   
/* 604:    */   public <T> T parse(Class<T> type, InputStream input)
/* 605:    */   {
/* 606:685 */     if (this.copyBuf == null) {
/* 607:686 */       this.copyBuf = new char[this.bufSize];
/* 608:    */     }
/* 609:689 */     this.fileInputBuf = IO.read(input, this.fileInputBuf, this.charset, this.bufSize, this.copyBuf);
/* 610:690 */     return parse(type, this.fileInputBuf.readForRecycle());
/* 611:    */   }
/* 612:    */   
/* 613:    */   public <T> T parse(Class<T> type, InputStream input, Charset charset)
/* 614:    */   {
/* 615:696 */     this.fileInputBuf = IO.read(input, this.fileInputBuf, charset, this.bufSize, this.copyBuf);
/* 616:697 */     return parse(type, this.fileInputBuf.readForRecycle());
/* 617:    */   }
/* 618:    */   
/* 619:    */   public <T> T parseFile(Class<T> type, String fileName)
/* 620:    */   {
/* 621:    */     try
/* 622:    */     {
/* 623:705 */       Path filePath = IO.path(fileName);
/* 624:706 */       long size = Files.size(filePath);
/* 625:707 */       size = size > 2000000000L ? 1000000L : size;
/* 626:708 */       Reader reader = Files.newBufferedReader(IO.path(fileName), this.charset);
/* 627:709 */       this.fileInputBuf = IO.read(reader, this.fileInputBuf, (int)size, this.copyBuf);
/* 628:710 */       return parse(type, this.fileInputBuf.readForRecycle());
/* 629:    */     }
/* 630:    */     catch (IOException ex)
/* 631:    */     {
/* 632:712 */       return Exceptions.handle(type, fileName, ex);
/* 633:    */     }
/* 634:    */   }
/* 635:    */   
/* 636:    */   public Map<String, Object> parseMap(char[] value)
/* 637:    */   {
/* 638:722 */     return (Map)parse(value);
/* 639:    */   }
/* 640:    */   
/* 641:    */   public Map<String, Object> parseMap(byte[] value)
/* 642:    */   {
/* 643:727 */     return (Map)parse(value);
/* 644:    */   }
/* 645:    */   
/* 646:    */   public Map<String, Object> parseMap(byte[] value, Charset charset)
/* 647:    */   {
/* 648:732 */     return (Map)parse(value, charset);
/* 649:    */   }
/* 650:    */   
/* 651:    */   public Map<String, Object> parseMap(InputStream value, Charset charset)
/* 652:    */   {
/* 653:737 */     return (Map)parse(value, charset);
/* 654:    */   }
/* 655:    */   
/* 656:    */   public Map<String, Object> parseMap(CharSequence value)
/* 657:    */   {
/* 658:742 */     return (Map)parse(value);
/* 659:    */   }
/* 660:    */   
/* 661:    */   public Map<String, Object> parseMap(InputStream value)
/* 662:    */   {
/* 663:747 */     return (Map)parse(value);
/* 664:    */   }
/* 665:    */   
/* 666:    */   public Map<String, Object> parseMap(Reader value)
/* 667:    */   {
/* 668:752 */     return (Map)parse(value);
/* 669:    */   }
/* 670:    */   
/* 671:    */   public Map<String, Object> parseMapFromFile(String file)
/* 672:    */   {
/* 673:757 */     return (Map)parseFile(file);
/* 674:    */   }
/* 675:    */   
/* 676:    */   public Object parse(String jsonString)
/* 677:    */   {
/* 678:765 */     return this.parser.parse(jsonString);
/* 679:    */   }
/* 680:    */   
/* 681:    */   public Object parse(byte[] bytes)
/* 682:    */   {
/* 683:770 */     return this.parser.parse(bytes);
/* 684:    */   }
/* 685:    */   
/* 686:    */   public Object parse(byte[] bytes, Charset charset)
/* 687:    */   {
/* 688:775 */     return this.parser.parse(bytes, charset);
/* 689:    */   }
/* 690:    */   
/* 691:    */   public Object parse(CharSequence charSequence)
/* 692:    */   {
/* 693:780 */     return this.parser.parse(charSequence);
/* 694:    */   }
/* 695:    */   
/* 696:    */   public Object parse(char[] chars)
/* 697:    */   {
/* 698:786 */     return this.parser.parse(chars);
/* 699:    */   }
/* 700:    */   
/* 701:    */   public Object parse(Reader reader)
/* 702:    */   {
/* 703:791 */     return this.parser.parse(reader);
/* 704:    */   }
/* 705:    */   
/* 706:    */   public Object parse(InputStream input)
/* 707:    */   {
/* 708:796 */     return this.parser.parse(input);
/* 709:    */   }
/* 710:    */   
/* 711:    */   public Object parse(InputStream input, Charset charset)
/* 712:    */   {
/* 713:801 */     return this.parser.parse(input, charset);
/* 714:    */   }
/* 715:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.implementation.BaseJsonParserAndMapper
 * JD-Core Version:    0.7.0.1
 */