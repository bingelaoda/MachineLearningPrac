/*   1:    */ package org.boon.json;
/*   2:    */ 
/*   3:    */ import java.io.IOException;
/*   4:    */ import java.io.InputStream;
/*   5:    */ import java.io.InputStreamReader;
/*   6:    */ import java.io.Reader;
/*   7:    */ import java.math.BigDecimal;
/*   8:    */ import java.math.BigInteger;
/*   9:    */ import java.nio.charset.Charset;
/*  10:    */ import java.nio.charset.StandardCharsets;
/*  11:    */ import java.nio.file.Files;
/*  12:    */ import java.nio.file.OpenOption;
/*  13:    */ import java.nio.file.Path;
/*  14:    */ import java.util.Date;
/*  15:    */ import java.util.List;
/*  16:    */ import java.util.Map;
/*  17:    */ import org.boon.Exceptions;
/*  18:    */ import org.boon.IO;
/*  19:    */ import org.boon.core.Typ;
/*  20:    */ import org.boon.core.Value;
/*  21:    */ import org.boon.core.reflection.MapObjectConversion;
/*  22:    */ import org.boon.core.reflection.Mapper;
/*  23:    */ import org.boon.core.value.ValueContainer;
/*  24:    */ import org.boon.json.implementation.BaseJsonParserAndMapper;
/*  25:    */ import org.boon.json.implementation.JsonFastParser;
/*  26:    */ import org.boon.json.implementation.JsonParserLax;
/*  27:    */ import org.boon.primitive.CharBuf;
/*  28:    */ import org.boon.primitive.InMemoryInputStream;
/*  29:    */ 
/*  30:    */ public class JsonMappingParser
/*  31:    */   implements JsonParserAndMapper
/*  32:    */ {
/*  33:    */   private final JsonParserAndMapper objectParser;
/*  34:    */   private final JsonParserAndMapper basicParser;
/*  35:    */   private final JsonParserAndMapper largeFileParser;
/*  36:    */   private final Mapper mapper;
/*  37:    */   private final Charset charset;
/*  38:    */   private CharBuf charBuf;
/*  39:    */   private char[] copyBuf;
/*  40: 73 */   private int bufSize = 4096;
/*  41:    */   
/*  42:    */   public JsonMappingParser(Mapper mapper, Charset charset, boolean lax, boolean chop, boolean lazyChop)
/*  43:    */   {
/*  44: 81 */     this.charset = charset;
/*  45: 82 */     this.mapper = mapper;
/*  46: 84 */     if (lax)
/*  47:    */     {
/*  48: 85 */       this.basicParser = new BaseJsonParserAndMapper(new JsonParserLax(false, chop, lazyChop), mapper);
/*  49: 86 */       this.objectParser = new BaseJsonParserAndMapper(new JsonParserLax(true), mapper);
/*  50:    */     }
/*  51:    */     else
/*  52:    */     {
/*  53: 88 */       this.basicParser = new BaseJsonParserAndMapper(new JsonFastParser(false, chop, lazyChop), mapper);
/*  54: 89 */       this.objectParser = new BaseJsonParserAndMapper(new JsonFastParser(true), mapper);
/*  55:    */     }
/*  56: 92 */     ((BaseJsonParserAndMapper)this.basicParser).setCharset(charset);
/*  57: 93 */     ((BaseJsonParserAndMapper)this.objectParser).setCharset(charset);
/*  58:    */     
/*  59:    */ 
/*  60: 96 */     this.largeFileParser = new JsonParserFactory().createCharacterSourceParser();
/*  61:    */   }
/*  62:    */   
/*  63:    */   public Map<String, Object> parseMap(String value)
/*  64:    */   {
/*  65:103 */     return this.basicParser.parseMap(value);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public Map<String, Object> parseMap(char[] value)
/*  69:    */   {
/*  70:108 */     return this.basicParser.parseMap(value);
/*  71:    */   }
/*  72:    */   
/*  73:    */   public Map<String, Object> parseMap(byte[] value)
/*  74:    */   {
/*  75:113 */     return this.basicParser.parseMap(value);
/*  76:    */   }
/*  77:    */   
/*  78:    */   public Map<String, Object> parseMap(byte[] value, Charset charset)
/*  79:    */   {
/*  80:118 */     return this.basicParser.parseMap(value, charset);
/*  81:    */   }
/*  82:    */   
/*  83:    */   public Map<String, Object> parseMap(InputStream value, Charset charset)
/*  84:    */   {
/*  85:123 */     return this.basicParser.parseMap(value, charset);
/*  86:    */   }
/*  87:    */   
/*  88:    */   public Map<String, Object> parseMap(CharSequence value)
/*  89:    */   {
/*  90:128 */     return this.basicParser.parseMap(value);
/*  91:    */   }
/*  92:    */   
/*  93:    */   public Map<String, Object> parseMap(InputStream value)
/*  94:    */   {
/*  95:133 */     return this.basicParser.parseMap(value);
/*  96:    */   }
/*  97:    */   
/*  98:    */   public Map<String, Object> parseMap(Reader value)
/*  99:    */   {
/* 100:138 */     return this.basicParser.parseMap(value);
/* 101:    */   }
/* 102:    */   
/* 103:    */   public Map<String, Object> parseMapFromFile(String file)
/* 104:    */   {
/* 105:144 */     return (Map)parseFile(file);
/* 106:    */   }
/* 107:    */   
/* 108:    */   public <T> List<T> parseList(Class<T> componentType, String jsonString)
/* 109:    */   {
/* 110:149 */     return this.objectParser.parseList(componentType, jsonString);
/* 111:    */   }
/* 112:    */   
/* 113:    */   public <T> List<T> parseList(Class<T> componentType, InputStream input)
/* 114:    */   {
/* 115:154 */     return this.objectParser.parseList(componentType, input);
/* 116:    */   }
/* 117:    */   
/* 118:    */   public <T> List<T> parseList(Class<T> componentType, Reader reader)
/* 119:    */   {
/* 120:159 */     return this.objectParser.parseList(componentType, reader);
/* 121:    */   }
/* 122:    */   
/* 123:    */   public <T> List<T> parseList(Class<T> componentType, InputStream input, Charset charset)
/* 124:    */   {
/* 125:164 */     return this.objectParser.parseList(componentType, input, charset);
/* 126:    */   }
/* 127:    */   
/* 128:    */   public <T> List<T> parseList(Class<T> componentType, byte[] jsonBytes)
/* 129:    */   {
/* 130:169 */     return this.objectParser.parseList(componentType, jsonBytes);
/* 131:    */   }
/* 132:    */   
/* 133:    */   public <T> List<T> parseList(Class<T> componentType, byte[] jsonBytes, Charset charset)
/* 134:    */   {
/* 135:174 */     return this.objectParser.parseList(componentType, jsonBytes, charset);
/* 136:    */   }
/* 137:    */   
/* 138:    */   public <T> List<T> parseList(Class<T> componentType, char[] chars)
/* 139:    */   {
/* 140:179 */     return this.objectParser.parseList(componentType, chars);
/* 141:    */   }
/* 142:    */   
/* 143:    */   public <T> List<T> parseList(Class<T> componentType, CharSequence jsonSeq)
/* 144:    */   {
/* 145:184 */     return this.objectParser.parseList(componentType, jsonSeq);
/* 146:    */   }
/* 147:    */   
/* 148:    */   public <T> List<T> parseListFromFile(Class<T> componentType, String fileName)
/* 149:    */   {
/* 150:189 */     return this.objectParser.parseListFromFile(componentType, fileName);
/* 151:    */   }
/* 152:    */   
/* 153:    */   public final <T> T parse(Class<T> type, String value)
/* 154:    */   {
/* 155:195 */     if (Typ.isBasicTypeOrCollection(type))
/* 156:    */     {
/* 157:196 */       Object obj = this.basicParser.parse(type, value);
/* 158:197 */       return obj;
/* 159:    */     }
/* 160:199 */     Object object = this.objectParser.parse(Map.class, value);
/* 161:200 */     return finalExtract(type, object);
/* 162:    */   }
/* 163:    */   
/* 164:    */   private <T> T finalExtract(Class<T> type, Object object)
/* 165:    */   {
/* 166:206 */     if ((object instanceof Map))
/* 167:    */     {
/* 168:207 */       Map<String, Value> objectMap = (Map)object;
/* 169:208 */       return this.mapper.fromValueMap(objectMap, type);
/* 170:    */     }
/* 171:211 */     if ((object instanceof ValueContainer)) {
/* 172:212 */       object = ((ValueContainer)object).toValue();
/* 173:    */     }
/* 174:215 */     if ((object instanceof List))
/* 175:    */     {
/* 176:216 */       List<Object> list = (List)object;
/* 177:217 */       return MapObjectConversion.fromList(list, type);
/* 178:    */     }
/* 179:219 */     return object;
/* 180:    */   }
/* 181:    */   
/* 182:    */   public final <T> T parse(Class<T> type, byte[] value)
/* 183:    */   {
/* 184:227 */     if ((type == Object.class) || (type == Map.class) || (type == List.class) || (Typ.isBasicType(type)))
/* 185:    */     {
/* 186:228 */       if (value.length < 100000) {
/* 187:229 */         return this.basicParser.parse(type, value);
/* 188:    */       }
/* 189:231 */       return this.basicParser.parseAsStream(type, value);
/* 190:    */     }
/* 191:234 */     Object object = this.objectParser.parse(Map.class, value);
/* 192:235 */     return finalExtract(type, object);
/* 193:    */   }
/* 194:    */   
/* 195:    */   public final <T> T parse(Class<T> type, byte[] value, Charset charset)
/* 196:    */   {
/* 197:242 */     if ((type == Object.class) || (type == Map.class) || (type == List.class) || (Typ.isBasicType(type))) {
/* 198:243 */       return this.basicParser.parse(type, value, charset);
/* 199:    */     }
/* 200:245 */     Object object = this.objectParser.parse(Map.class, value);
/* 201:246 */     return finalExtract(type, object);
/* 202:    */   }
/* 203:    */   
/* 204:    */   public final <T> T parse(Class<T> type, CharSequence value)
/* 205:    */   {
/* 206:252 */     if ((type == Object.class) || (type == Map.class) || (type == List.class) || (Typ.isBasicType(type))) {
/* 207:253 */       return this.basicParser.parse(type, value);
/* 208:    */     }
/* 209:255 */     Object object = this.objectParser.parse(Map.class, value);
/* 210:256 */     return finalExtract(type, object);
/* 211:    */   }
/* 212:    */   
/* 213:    */   public final <T> T parse(Class<T> type, char[] value)
/* 214:    */   {
/* 215:262 */     if ((type == Object.class) || (type == Map.class) || (type == List.class) || (Typ.isBasicType(type))) {
/* 216:263 */       return this.basicParser.parse(type, value);
/* 217:    */     }
/* 218:265 */     Object object = this.objectParser.parse(Map.class, value);
/* 219:266 */     return finalExtract(type, object);
/* 220:    */   }
/* 221:    */   
/* 222:    */   public final <T> T parse(Class<T> type, Reader reader)
/* 223:    */   {
/* 224:276 */     if (this.copyBuf == null) {
/* 225:277 */       this.copyBuf = new char[this.bufSize];
/* 226:    */     }
/* 227:280 */     this.charBuf = IO.read(reader, this.charBuf, this.bufSize, this.copyBuf);
/* 228:281 */     return parse(type, this.charBuf.readForRecycle());
/* 229:    */   }
/* 230:    */   
/* 231:    */   public <T> T parseFile(Class<T> type, String fileName)
/* 232:    */   {
/* 233:287 */     int bufSize = this.bufSize;
/* 234:    */     try
/* 235:    */     {
/* 236:291 */       Path filePath = IO.path(fileName);
/* 237:292 */       size = Files.size(filePath);
/* 238:    */       Object localObject1;
/* 239:294 */       if (size > 10000000L) {
/* 240:295 */         return this.largeFileParser.parseFile(type, fileName);
/* 241:    */       }
/* 242:297 */       size = size > 2000000L ? bufSize : size;
/* 243:298 */       this.bufSize = ((int)size);
/* 244:301 */       if (size < 1000000L) {
/* 245:302 */         return parse(type, Files.newInputStream(filePath, new OpenOption[0]), this.charset);
/* 246:    */       }
/* 247:304 */       return parse(type, Files.newBufferedReader(filePath, this.charset));
/* 248:    */     }
/* 249:    */     catch (IOException ex)
/* 250:    */     {
/* 251:    */       long size;
/* 252:307 */       return Exceptions.handle(type, fileName, ex);
/* 253:    */     }
/* 254:    */     finally
/* 255:    */     {
/* 256:309 */       this.bufSize = bufSize;
/* 257:    */     }
/* 258:    */   }
/* 259:    */   
/* 260:    */   public int parseInt(String jsonString)
/* 261:    */   {
/* 262:315 */     return this.basicParser.parseInt(jsonString);
/* 263:    */   }
/* 264:    */   
/* 265:    */   public int parseInt(InputStream input)
/* 266:    */   {
/* 267:320 */     return this.basicParser.parseInt(input);
/* 268:    */   }
/* 269:    */   
/* 270:    */   public int parseInt(InputStream input, Charset charset)
/* 271:    */   {
/* 272:325 */     return this.basicParser.parseInt(input, charset);
/* 273:    */   }
/* 274:    */   
/* 275:    */   public int parseInt(byte[] jsonBytes)
/* 276:    */   {
/* 277:330 */     return this.basicParser.parseInt(jsonBytes);
/* 278:    */   }
/* 279:    */   
/* 280:    */   public int parseInt(byte[] jsonBytes, Charset charset)
/* 281:    */   {
/* 282:335 */     return this.basicParser.parseInt(jsonBytes, charset);
/* 283:    */   }
/* 284:    */   
/* 285:    */   public int parseInt(char[] chars)
/* 286:    */   {
/* 287:340 */     return this.basicParser.parseInt(chars);
/* 288:    */   }
/* 289:    */   
/* 290:    */   public int parseInt(CharSequence jsonSeq)
/* 291:    */   {
/* 292:345 */     return this.basicParser.parseInt(jsonSeq);
/* 293:    */   }
/* 294:    */   
/* 295:    */   public int parseIntFromFile(String fileName)
/* 296:    */   {
/* 297:350 */     return this.basicParser.parseIntFromFile(fileName);
/* 298:    */   }
/* 299:    */   
/* 300:    */   public long parseLong(String jsonString)
/* 301:    */   {
/* 302:355 */     return this.basicParser.parseLong(jsonString);
/* 303:    */   }
/* 304:    */   
/* 305:    */   public long parseLong(InputStream input)
/* 306:    */   {
/* 307:360 */     return this.basicParser.parseLong(input);
/* 308:    */   }
/* 309:    */   
/* 310:    */   public long parseLong(InputStream input, Charset charset)
/* 311:    */   {
/* 312:365 */     return this.basicParser.parseLong(input, charset);
/* 313:    */   }
/* 314:    */   
/* 315:    */   public long parseLong(byte[] jsonBytes)
/* 316:    */   {
/* 317:370 */     return this.basicParser.parseLong(jsonBytes);
/* 318:    */   }
/* 319:    */   
/* 320:    */   public long parseLong(byte[] jsonBytes, Charset charset)
/* 321:    */   {
/* 322:375 */     return this.basicParser.parseLong(jsonBytes, charset);
/* 323:    */   }
/* 324:    */   
/* 325:    */   public long parseLong(char[] chars)
/* 326:    */   {
/* 327:380 */     return this.basicParser.parseLong(chars);
/* 328:    */   }
/* 329:    */   
/* 330:    */   public long parseLong(CharSequence jsonSeq)
/* 331:    */   {
/* 332:385 */     return this.basicParser.parseLong(jsonSeq);
/* 333:    */   }
/* 334:    */   
/* 335:    */   public long parseLongFromFile(String fileName)
/* 336:    */   {
/* 337:390 */     return this.basicParser.parseLongFromFile(fileName);
/* 338:    */   }
/* 339:    */   
/* 340:    */   public String parseString(String value)
/* 341:    */   {
/* 342:395 */     return this.basicParser.parseString(value);
/* 343:    */   }
/* 344:    */   
/* 345:    */   public String parseString(InputStream value)
/* 346:    */   {
/* 347:401 */     return this.basicParser.parseString(value);
/* 348:    */   }
/* 349:    */   
/* 350:    */   public String parseString(InputStream value, Charset charset)
/* 351:    */   {
/* 352:407 */     return this.basicParser.parseString(value, charset);
/* 353:    */   }
/* 354:    */   
/* 355:    */   public String parseString(byte[] value)
/* 356:    */   {
/* 357:413 */     return this.basicParser.parseString(value);
/* 358:    */   }
/* 359:    */   
/* 360:    */   public String parseString(byte[] value, Charset charset)
/* 361:    */   {
/* 362:419 */     return this.basicParser.parseString(value, charset);
/* 363:    */   }
/* 364:    */   
/* 365:    */   public String parseString(char[] value)
/* 366:    */   {
/* 367:426 */     return this.basicParser.parseString(value);
/* 368:    */   }
/* 369:    */   
/* 370:    */   public String parseString(CharSequence value)
/* 371:    */   {
/* 372:433 */     return this.basicParser.parseString(value);
/* 373:    */   }
/* 374:    */   
/* 375:    */   public String parseStringFromFile(String value)
/* 376:    */   {
/* 377:439 */     return this.basicParser.parseStringFromFile(value);
/* 378:    */   }
/* 379:    */   
/* 380:    */   public double parseDouble(String value)
/* 381:    */   {
/* 382:444 */     return this.basicParser.parseDouble(value);
/* 383:    */   }
/* 384:    */   
/* 385:    */   public double parseDouble(InputStream value)
/* 386:    */   {
/* 387:449 */     return this.basicParser.parseDouble(value);
/* 388:    */   }
/* 389:    */   
/* 390:    */   public double parseDouble(byte[] value)
/* 391:    */   {
/* 392:454 */     return this.basicParser.parseDouble(value);
/* 393:    */   }
/* 394:    */   
/* 395:    */   public double parseDouble(char[] value)
/* 396:    */   {
/* 397:459 */     return this.basicParser.parseDouble(value);
/* 398:    */   }
/* 399:    */   
/* 400:    */   public double parseDouble(CharSequence value)
/* 401:    */   {
/* 402:464 */     return this.basicParser.parseDouble(value);
/* 403:    */   }
/* 404:    */   
/* 405:    */   public double parseDouble(byte[] value, Charset charset)
/* 406:    */   {
/* 407:469 */     return this.basicParser.parseDouble(value, charset);
/* 408:    */   }
/* 409:    */   
/* 410:    */   public double parseDouble(InputStream value, Charset charset)
/* 411:    */   {
/* 412:474 */     return this.basicParser.parseDouble(value, charset);
/* 413:    */   }
/* 414:    */   
/* 415:    */   public double parseDoubleFromFile(String fileName)
/* 416:    */   {
/* 417:479 */     return this.basicParser.parseDoubleFromFile(fileName);
/* 418:    */   }
/* 419:    */   
/* 420:    */   public float parseFloat(String value)
/* 421:    */   {
/* 422:484 */     return this.basicParser.parseFloat(value);
/* 423:    */   }
/* 424:    */   
/* 425:    */   public float parseFloat(InputStream value)
/* 426:    */   {
/* 427:489 */     return this.basicParser.parseFloat(value);
/* 428:    */   }
/* 429:    */   
/* 430:    */   public float parseFloat(byte[] value)
/* 431:    */   {
/* 432:494 */     return this.basicParser.parseFloat(value);
/* 433:    */   }
/* 434:    */   
/* 435:    */   public float parseFloat(char[] value)
/* 436:    */   {
/* 437:499 */     return this.basicParser.parseFloat(value);
/* 438:    */   }
/* 439:    */   
/* 440:    */   public float parseFloat(CharSequence value)
/* 441:    */   {
/* 442:504 */     return this.basicParser.parseFloat(value);
/* 443:    */   }
/* 444:    */   
/* 445:    */   public float parseFloat(byte[] value, Charset charset)
/* 446:    */   {
/* 447:509 */     return this.basicParser.parseFloat(value, charset);
/* 448:    */   }
/* 449:    */   
/* 450:    */   public float parseFloat(InputStream value, Charset charset)
/* 451:    */   {
/* 452:514 */     return this.basicParser.parseFloat(value, charset);
/* 453:    */   }
/* 454:    */   
/* 455:    */   public float parseFloatFromFile(String fileName)
/* 456:    */   {
/* 457:519 */     return this.basicParser.parseFloatFromFile(fileName);
/* 458:    */   }
/* 459:    */   
/* 460:    */   public BigDecimal parseBigDecimal(String value)
/* 461:    */   {
/* 462:524 */     return this.basicParser.parseBigDecimal(value);
/* 463:    */   }
/* 464:    */   
/* 465:    */   public BigDecimal parseBigDecimal(InputStream value)
/* 466:    */   {
/* 467:529 */     return this.basicParser.parseBigDecimal(value);
/* 468:    */   }
/* 469:    */   
/* 470:    */   public BigDecimal parseBigDecimal(byte[] value)
/* 471:    */   {
/* 472:534 */     return this.basicParser.parseBigDecimal(value);
/* 473:    */   }
/* 474:    */   
/* 475:    */   public BigDecimal parseBigDecimal(char[] value)
/* 476:    */   {
/* 477:539 */     return this.basicParser.parseBigDecimal(value);
/* 478:    */   }
/* 479:    */   
/* 480:    */   public BigDecimal parseBigDecimal(CharSequence value)
/* 481:    */   {
/* 482:544 */     return this.basicParser.parseBigDecimal(value);
/* 483:    */   }
/* 484:    */   
/* 485:    */   public BigDecimal parseBigDecimal(byte[] value, Charset charset)
/* 486:    */   {
/* 487:549 */     return this.basicParser.parseBigDecimal(value, charset);
/* 488:    */   }
/* 489:    */   
/* 490:    */   public BigDecimal parseBigDecimal(InputStream value, Charset charset)
/* 491:    */   {
/* 492:554 */     return this.basicParser.parseBigDecimal(value, charset);
/* 493:    */   }
/* 494:    */   
/* 495:    */   public BigDecimal parseBigDecimalFromFile(String fileName)
/* 496:    */   {
/* 497:559 */     return this.basicParser.parseBigDecimalFromFile(fileName);
/* 498:    */   }
/* 499:    */   
/* 500:    */   public BigInteger parseBigInteger(String value)
/* 501:    */   {
/* 502:564 */     return this.basicParser.parseBigInteger(value);
/* 503:    */   }
/* 504:    */   
/* 505:    */   public BigInteger parseBigInteger(InputStream value)
/* 506:    */   {
/* 507:569 */     return this.basicParser.parseBigInteger(value);
/* 508:    */   }
/* 509:    */   
/* 510:    */   public BigInteger parseBigInteger(byte[] value)
/* 511:    */   {
/* 512:574 */     return this.basicParser.parseBigInteger(value);
/* 513:    */   }
/* 514:    */   
/* 515:    */   public BigInteger parseBigInteger(char[] value)
/* 516:    */   {
/* 517:579 */     return this.basicParser.parseBigInteger(value);
/* 518:    */   }
/* 519:    */   
/* 520:    */   public BigInteger parseBigInteger(CharSequence value)
/* 521:    */   {
/* 522:584 */     return this.basicParser.parseBigInteger(value);
/* 523:    */   }
/* 524:    */   
/* 525:    */   public BigInteger parseBigInteger(byte[] value, Charset charset)
/* 526:    */   {
/* 527:589 */     return this.basicParser.parseBigInteger(value, charset);
/* 528:    */   }
/* 529:    */   
/* 530:    */   public BigInteger parseBigInteger(InputStream value, Charset charset)
/* 531:    */   {
/* 532:594 */     return this.basicParser.parseBigInteger(value, charset);
/* 533:    */   }
/* 534:    */   
/* 535:    */   public BigInteger parseBigIntegerFile(String fileName)
/* 536:    */   {
/* 537:599 */     return this.basicParser.parseBigIntegerFile(fileName);
/* 538:    */   }
/* 539:    */   
/* 540:    */   public Date parseDate(String jsonString)
/* 541:    */   {
/* 542:604 */     return this.basicParser.parseDate(jsonString);
/* 543:    */   }
/* 544:    */   
/* 545:    */   public Date parseDate(InputStream input)
/* 546:    */   {
/* 547:609 */     return this.basicParser.parseDate(input);
/* 548:    */   }
/* 549:    */   
/* 550:    */   public Date parseDate(InputStream input, Charset charset)
/* 551:    */   {
/* 552:614 */     return this.basicParser.parseDate(input, charset);
/* 553:    */   }
/* 554:    */   
/* 555:    */   public Date parseDate(byte[] jsonBytes)
/* 556:    */   {
/* 557:619 */     return this.basicParser.parseDate(jsonBytes);
/* 558:    */   }
/* 559:    */   
/* 560:    */   public Date parseDate(byte[] jsonBytes, Charset charset)
/* 561:    */   {
/* 562:624 */     return this.basicParser.parseDate(jsonBytes, charset);
/* 563:    */   }
/* 564:    */   
/* 565:    */   public Date parseDate(char[] chars)
/* 566:    */   {
/* 567:629 */     return this.basicParser.parseDate(chars);
/* 568:    */   }
/* 569:    */   
/* 570:    */   public Date parseDate(CharSequence jsonSeq)
/* 571:    */   {
/* 572:634 */     return this.basicParser.parseDate(jsonSeq);
/* 573:    */   }
/* 574:    */   
/* 575:    */   public Date parseDateFromFile(String fileName)
/* 576:    */   {
/* 577:639 */     return this.basicParser.parseDateFromFile(fileName);
/* 578:    */   }
/* 579:    */   
/* 580:    */   public short parseShort(String jsonString)
/* 581:    */   {
/* 582:644 */     return this.basicParser.parseShort(jsonString);
/* 583:    */   }
/* 584:    */   
/* 585:    */   public byte parseByte(String jsonString)
/* 586:    */   {
/* 587:649 */     return this.basicParser.parseByte(jsonString);
/* 588:    */   }
/* 589:    */   
/* 590:    */   public char parseChar(String jsonString)
/* 591:    */   {
/* 592:654 */     return this.basicParser.parseChar(jsonString);
/* 593:    */   }
/* 594:    */   
/* 595:    */   public <T extends Enum> T parseEnum(Class<T> type, String jsonString)
/* 596:    */   {
/* 597:659 */     return this.basicParser.parseEnum(type, jsonString);
/* 598:    */   }
/* 599:    */   
/* 600:    */   public char[] parseCharArray(String jsonString)
/* 601:    */   {
/* 602:664 */     return this.basicParser.parseCharArray(jsonString);
/* 603:    */   }
/* 604:    */   
/* 605:    */   public byte[] parseByteArray(String jsonString)
/* 606:    */   {
/* 607:669 */     return this.basicParser.parseByteArray(jsonString);
/* 608:    */   }
/* 609:    */   
/* 610:    */   public short[] parseShortArray(String jsonString)
/* 611:    */   {
/* 612:674 */     return this.basicParser.parseShortArray(jsonString);
/* 613:    */   }
/* 614:    */   
/* 615:    */   public int[] parseIntArray(String jsonString)
/* 616:    */   {
/* 617:679 */     return this.basicParser.parseIntArray(jsonString);
/* 618:    */   }
/* 619:    */   
/* 620:    */   public float[] parseFloatArray(String jsonString)
/* 621:    */   {
/* 622:684 */     return this.basicParser.parseFloatArray(jsonString);
/* 623:    */   }
/* 624:    */   
/* 625:    */   public double[] parseDoubleArray(String jsonString)
/* 626:    */   {
/* 627:689 */     return this.basicParser.parseDoubleArray(jsonString);
/* 628:    */   }
/* 629:    */   
/* 630:    */   public long[] parseLongArray(String jsonString)
/* 631:    */   {
/* 632:694 */     return this.basicParser.parseLongArray(jsonString);
/* 633:    */   }
/* 634:    */   
/* 635:    */   public Object parse(String jsonString)
/* 636:    */   {
/* 637:699 */     return this.basicParser.parse(jsonString);
/* 638:    */   }
/* 639:    */   
/* 640:    */   public Object parse(CharSequence charSequence)
/* 641:    */   {
/* 642:705 */     return this.basicParser.parse(charSequence);
/* 643:    */   }
/* 644:    */   
/* 645:    */   public Object parse(char[] chars)
/* 646:    */   {
/* 647:711 */     return this.basicParser.parse(chars);
/* 648:    */   }
/* 649:    */   
/* 650:    */   public Object parse(Reader reader)
/* 651:    */   {
/* 652:716 */     return this.basicParser.parse(reader);
/* 653:    */   }
/* 654:    */   
/* 655:    */   public Object parse(InputStream input)
/* 656:    */   {
/* 657:721 */     return this.basicParser.parse(input);
/* 658:    */   }
/* 659:    */   
/* 660:    */   public Object parse(InputStream input, Charset charset)
/* 661:    */   {
/* 662:726 */     return this.basicParser.parse(input, charset);
/* 663:    */   }
/* 664:    */   
/* 665:    */   public Object parse(byte[] bytes)
/* 666:    */   {
/* 667:733 */     return parse(bytes, this.charset);
/* 668:    */   }
/* 669:    */   
/* 670:    */   public Object parse(byte[] bytes, Charset charset)
/* 671:    */   {
/* 672:739 */     if (bytes.length > 100000) {
/* 673:740 */       return parse(new InMemoryInputStream(bytes), charset);
/* 674:    */     }
/* 675:742 */     return this.basicParser.parse(bytes, charset);
/* 676:    */   }
/* 677:    */   
/* 678:    */   public Object parseDirect(byte[] value)
/* 679:    */   {
/* 680:748 */     if ((value.length < 20000) && (this.charset == StandardCharsets.UTF_8))
/* 681:    */     {
/* 682:749 */       CharBuf builder = CharBuf.createFromUTF8Bytes(value);
/* 683:750 */       return parse(builder.toCharArray());
/* 684:    */     }
/* 685:752 */     return parse(new InMemoryInputStream(value));
/* 686:    */   }
/* 687:    */   
/* 688:    */   public Object parseAsStream(byte[] value)
/* 689:    */   {
/* 690:758 */     return this.basicParser.parseAsStream(value);
/* 691:    */   }
/* 692:    */   
/* 693:    */   public Object parseFile(String fileName)
/* 694:    */   {
/* 695:764 */     int bufSize = this.bufSize;
/* 696:    */     try
/* 697:    */     {
/* 698:768 */       Path filePath = IO.path(fileName);
/* 699:769 */       size = Files.size(filePath);
/* 700:    */       Object localObject1;
/* 701:771 */       if (size > 10000000L) {
/* 702:772 */         return this.largeFileParser.parseFile(fileName);
/* 703:    */       }
/* 704:774 */       size = size > 2000000L ? bufSize : size;
/* 705:775 */       this.bufSize = ((int)size);
/* 706:778 */       if (size < 1000000L) {
/* 707:779 */         return parse(Files.newInputStream(filePath, new OpenOption[0]), this.charset);
/* 708:    */       }
/* 709:781 */       return parse(Files.newBufferedReader(filePath, this.charset));
/* 710:    */     }
/* 711:    */     catch (IOException ex)
/* 712:    */     {
/* 713:    */       long size;
/* 714:784 */       return Exceptions.handle(Typ.object, fileName, ex);
/* 715:    */     }
/* 716:    */     finally
/* 717:    */     {
/* 718:786 */       this.bufSize = bufSize;
/* 719:    */     }
/* 720:    */   }
/* 721:    */   
/* 722:    */   public void close() {}
/* 723:    */   
/* 724:    */   public final <T> T parse(Class<T> type, InputStream input)
/* 725:    */   {
/* 726:799 */     if (this.copyBuf == null) {
/* 727:800 */       this.copyBuf = new char[this.bufSize];
/* 728:    */     }
/* 729:803 */     this.charBuf = IO.read(input, this.charBuf, this.charset, this.bufSize, this.copyBuf);
/* 730:804 */     return parse(type, this.charBuf.readForRecycle());
/* 731:    */   }
/* 732:    */   
/* 733:    */   public final <T> T parse(Class<T> type, InputStream input, Charset charset)
/* 734:    */   {
/* 735:809 */     if (this.copyBuf == null) {
/* 736:810 */       this.copyBuf = new char[this.bufSize];
/* 737:    */     }
/* 738:813 */     this.charBuf = IO.read(input, this.charBuf, charset, this.bufSize, this.copyBuf);
/* 739:814 */     return parse(type, this.charBuf.readForRecycle());
/* 740:    */   }
/* 741:    */   
/* 742:    */   public final <T> T parseDirect(Class<T> type, byte[] value)
/* 743:    */   {
/* 744:820 */     if ((value.length < 20000) && (this.charset == StandardCharsets.UTF_8))
/* 745:    */     {
/* 746:821 */       CharBuf builder = CharBuf.createFromUTF8Bytes(value);
/* 747:822 */       return parse(type, builder.toCharArray());
/* 748:    */     }
/* 749:824 */     return parse(type, new InMemoryInputStream(value));
/* 750:    */   }
/* 751:    */   
/* 752:    */   public final <T> T parseAsStream(Class<T> type, byte[] value)
/* 753:    */   {
/* 754:830 */     if (this.copyBuf == null) {
/* 755:831 */       this.copyBuf = new char[this.bufSize];
/* 756:    */     }
/* 757:834 */     this.charBuf = IO.read(new InputStreamReader(new InMemoryInputStream(value), this.charset), this.charBuf, value.length, this.copyBuf);
/* 758:835 */     return this.basicParser.parse(type, this.charBuf.readForRecycle());
/* 759:    */   }
/* 760:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.JsonMappingParser
 * JD-Core Version:    0.7.0.1
 */