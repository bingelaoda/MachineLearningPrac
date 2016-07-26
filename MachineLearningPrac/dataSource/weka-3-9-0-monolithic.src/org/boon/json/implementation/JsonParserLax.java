/*   1:    */ package org.boon.json.implementation;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.List;
/*   6:    */ import org.boon.core.TypeType;
/*   7:    */ import org.boon.core.Value;
/*   8:    */ import org.boon.core.reflection.fields.FieldAccessMode;
/*   9:    */ import org.boon.core.reflection.fields.FieldsAccessor;
/*  10:    */ import org.boon.core.value.CharSequenceValue;
/*  11:    */ import org.boon.core.value.LazyValueMap;
/*  12:    */ import org.boon.core.value.MapItemValue;
/*  13:    */ import org.boon.core.value.NumberValue;
/*  14:    */ import org.boon.core.value.ValueContainer;
/*  15:    */ import org.boon.core.value.ValueList;
/*  16:    */ import org.boon.core.value.ValueMap;
/*  17:    */ import org.boon.core.value.ValueMapImpl;
/*  18:    */ import org.boon.json.JsonException;
/*  19:    */ import org.boon.json.JsonParserEvents;
/*  20:    */ import org.boon.primitive.CharScanner;
/*  21:    */ 
/*  22:    */ public class JsonParserLax
/*  23:    */   extends JsonFastParser
/*  24:    */ {
/*  25: 51 */   private static ValueContainer EMPTY_LIST = new ValueContainer(Collections.emptyList());
/*  26:    */   private final JsonParserEvents events;
/*  27:    */   Object root;
/*  28:    */   
/*  29:    */   public JsonParserLax()
/*  30:    */   {
/*  31: 57 */     this(FieldAccessMode.FIELD);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public JsonParserLax(FieldAccessMode mode)
/*  35:    */   {
/*  36: 61 */     this(mode, true);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public JsonParserLax(FieldAccessMode mode, boolean useAnnotations)
/*  40:    */   {
/*  41: 65 */     this(FieldAccessMode.create(mode, useAnnotations));
/*  42:    */   }
/*  43:    */   
/*  44:    */   public JsonParserLax(FieldsAccessor fieldsAccessor)
/*  45:    */   {
/*  46: 69 */     this(false);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public JsonParserLax(boolean useValues)
/*  50:    */   {
/*  51: 73 */     this(useValues, false);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public JsonParserLax(boolean useValues, boolean chop)
/*  55:    */   {
/*  56: 77 */     this(useValues, chop, !chop);
/*  57:    */   }
/*  58:    */   
/*  59:    */   public JsonParserLax(boolean useValues, boolean chop, boolean lazyChop)
/*  60:    */   {
/*  61: 81 */     this(useValues, chop, lazyChop, true);
/*  62:    */   }
/*  63:    */   
/*  64:    */   public JsonParserLax(boolean useValues, boolean chop, boolean lazyChop, boolean defaultCheckDates)
/*  65:    */   {
/*  66: 85 */     this(useValues, chop, lazyChop, defaultCheckDates, null);
/*  67:    */   }
/*  68:    */   
/*  69:    */   public JsonParserLax(boolean useValues, boolean chop, boolean lazyChop, boolean defaultCheckDates, JsonParserEvents events)
/*  70:    */   {
/*  71: 89 */     super(useValues, chop, lazyChop, defaultCheckDates);
/*  72: 90 */     this.events = events;
/*  73:    */   }
/*  74:    */   
/*  75:    */   private Value decodeJsonObjectLax(boolean isRoot)
/*  76:    */   {
/*  77: 95 */     if (this.__currentChar == '{') {
/*  78: 96 */       nextChar();
/*  79:    */     }
/*  80: 99 */     ValueMap map = (ValueMap)(this.useValues ? new ValueMapImpl() : new LazyValueMap(this.lazyChop));
/*  81:101 */     if (this.events != null)
/*  82:    */     {
/*  83:102 */       if (isRoot) {
/*  84:102 */         this.root = map;
/*  85:    */       }
/*  86:103 */       if (!this.events.objectStart(this.__index)) {
/*  87:103 */         stop();
/*  88:    */       }
/*  89:    */     }
/*  90:106 */     Value value = new ValueContainer(map);
/*  91:    */     
/*  92:108 */     skipWhiteSpaceIfNeeded();
/*  93:109 */     int startIndexOfKey = this.__index;
/*  94:115 */     for (; this.__index < this.charArray.length; this.__index += 1)
/*  95:    */     {
/*  96:117 */       skipWhiteSpaceIfNeeded();
/*  97:    */       Value key;
/*  98:    */       Value item;
/*  99:    */       MapItemValue miv;
/* 100:119 */       switch (this.__currentChar)
/* 101:    */       {
/* 102:    */       case '/': 
/* 103:121 */         handleComment();
/* 104:122 */         startIndexOfKey = this.__index;
/* 105:123 */         break;
/* 106:    */       case '#': 
/* 107:126 */         handleBashComment();
/* 108:127 */         startIndexOfKey = this.__index;
/* 109:128 */         break;
/* 110:    */       case ':': 
/* 111:131 */         char startChar = this.charArray[startIndexOfKey];
/* 112:132 */         if (startChar == ',') {
/* 113:133 */           startIndexOfKey++;
/* 114:    */         }
/* 115:136 */         key = extractLaxString(startIndexOfKey, this.__index - 1, false, false);
/* 116:138 */         if ((this.events != null) && (!this.events.objectFieldName(this.__index, map, (CharSequenceValue)key))) {
/* 117:138 */           stop();
/* 118:    */         }
/* 119:139 */         this.__index += 1;
/* 120:    */         
/* 121:    */ 
/* 122:142 */         item = decodeValueInternal();
/* 123:143 */         if ((this.events != null) && (!this.events.objectField(this.__index, map, (CharSequenceValue)key, item))) {
/* 124:143 */           stop();
/* 125:    */         }
/* 126:144 */         skipWhiteSpaceIfNeeded();
/* 127:    */         
/* 128:146 */         miv = new MapItemValue(key, item);
/* 129:    */         
/* 130:148 */         map.add(miv);
/* 131:    */         
/* 132:150 */         startIndexOfKey = this.__index;
/* 133:151 */         if (this.__currentChar == '}') {
/* 134:152 */           this.__index += 1;
/* 135:    */         }
/* 136:153 */         break;
/* 137:    */       case '\'': 
/* 138:159 */         key = decodeStringSingle();
/* 139:162 */         if ((this.events != null) && (!this.events.objectFieldName(this.__index, map, (CharSequenceValue)key))) {
/* 140:162 */           stop();
/* 141:    */         }
/* 142:164 */         skipWhiteSpaceIfNeeded();
/* 143:166 */         if (this.__currentChar != ':') {
/* 144:168 */           complain("expecting current character to be ':' but got " + charDescription(this.__currentChar) + "\n");
/* 145:    */         }
/* 146:170 */         this.__index += 1;
/* 147:171 */         item = decodeValueInternal();
/* 148:173 */         if ((this.events != null) && (!this.events.objectField(this.__index, map, (CharSequenceValue)key, item))) {
/* 149:173 */           stop();
/* 150:    */         }
/* 151:176 */         skipWhiteSpaceIfNeeded();
/* 152:    */         
/* 153:178 */         miv = new MapItemValue(key, item);
/* 154:    */         
/* 155:180 */         map.add(miv);
/* 156:181 */         startIndexOfKey = this.__index;
/* 157:182 */         if (this.__currentChar == '}') {
/* 158:183 */           this.__index += 1;
/* 159:    */         }
/* 160:184 */         break;
/* 161:    */       case '"': 
/* 162:190 */         key = decodeStringDouble();
/* 163:192 */         if (this.events != null) {
/* 164:192 */           this.events.objectFieldName(this.__index, map, (CharSequenceValue)key);
/* 165:    */         }
/* 166:194 */         skipWhiteSpaceIfNeeded();
/* 167:196 */         if (this.__currentChar != ':') {
/* 168:198 */           complain("expecting current character to be ':' but got " + charDescription(this.__currentChar) + "\n");
/* 169:    */         }
/* 170:200 */         this.__index += 1;
/* 171:201 */         item = decodeValueInternal();
/* 172:203 */         if ((this.events != null) && (!this.events.objectField(this.__index, map, (CharSequenceValue)key, item))) {
/* 173:203 */           stop();
/* 174:    */         }
/* 175:205 */         skipWhiteSpaceIfNeeded();
/* 176:    */         
/* 177:207 */         miv = new MapItemValue(key, item);
/* 178:    */         
/* 179:209 */         map.add(miv);
/* 180:210 */         startIndexOfKey = this.__index;
/* 181:211 */         if (this.__currentChar == '}') {
/* 182:212 */           this.__index += 1;
/* 183:    */         }
/* 184:213 */         break;
/* 185:    */       case '}': 
/* 186:219 */         this.__index += 1;
/* 187:    */         break label780;
/* 188:    */       }
/* 189:    */     }
/* 190:    */     label780:
/* 191:225 */     if ((this.events != null) && (!this.events.objectEnd(this.__index, map))) {
/* 192:225 */       stop();
/* 193:    */     }
/* 194:226 */     return value;
/* 195:    */   }
/* 196:    */   
/* 197:    */   private Value extractLaxString(int startIndexOfKey, int end, boolean encoded, boolean checkDate)
/* 198:    */   {
/* 199:232 */     for (; (startIndexOfKey < this.__index) && (startIndexOfKey < this.charArray.length); startIndexOfKey++)
/* 200:    */     {
/* 201:233 */       char startChar = this.charArray[startIndexOfKey];
/* 202:234 */       switch (startChar)
/* 203:    */       {
/* 204:    */       case '\t': 
/* 205:    */       case '\n': 
/* 206:    */       case ' ': 
/* 207:    */         break;
/* 208:    */       default: 
/* 209:    */         break label72;
/* 210:    */       }
/* 211:    */     }
/* 212:    */     label72:
/* 213:246 */     for (int endIndex = end >= this.charArray.length ? this.charArray.length - 1 : end; (endIndex >= startIndexOfKey + 1) && (endIndex >= 0); endIndex--)
/* 214:    */     {
/* 215:249 */       char endChar = this.charArray[endIndex];
/* 216:250 */       switch (endChar)
/* 217:    */       {
/* 218:    */       case '\t': 
/* 219:    */       case '\n': 
/* 220:    */       case ' ': 
/* 221:    */       case '}': 
/* 222:    */         break;
/* 223:    */       case ',': 
/* 224:    */       case ';': 
/* 225:    */         break;
/* 226:    */       case ']': 
/* 227:    */         break;
/* 228:    */       default: 
/* 229:    */         break label202;
/* 230:    */       }
/* 231:    */     }
/* 232:    */     label202:
/* 233:266 */     CharSequenceValue value = new CharSequenceValue(this.chop, TypeType.STRING, startIndexOfKey, endIndex + 1, this.charArray, encoded, checkDate);
/* 234:267 */     if ((this.events != null) && (!this.events.string(startIndexOfKey, endIndex + 1, value))) {
/* 235:267 */       stop();
/* 236:    */     }
/* 237:268 */     return value;
/* 238:    */   }
/* 239:    */   
/* 240:    */   public Object parse(char[] chars)
/* 241:    */   {
/* 242:278 */     this.lastIndex = (chars.length - 1);
/* 243:    */     try
/* 244:    */     {
/* 245:283 */       this.__index = 0;
/* 246:284 */       this.charArray = chars;
/* 247:    */       
/* 248:286 */       Value value = decodeValueInternal(true);
/* 249:287 */       if (value.isContainer()) {
/* 250:288 */         return value.toValue();
/* 251:    */       }
/* 252:290 */       return value;
/* 253:    */     }
/* 254:    */     catch (StopException stop) {}
/* 255:294 */     return this.root;
/* 256:    */   }
/* 257:    */   
/* 258:    */   protected final Value decodeValueInternal()
/* 259:    */   {
/* 260:304 */     return decodeValueInternal(false);
/* 261:    */   }
/* 262:    */   
/* 263:    */   protected final Value decodeValueInternal(boolean isRoot)
/* 264:    */   {
/* 265:307 */     Value value = null;
/* 266:309 */     for (; this.__index < this.charArray.length; this.__index += 1)
/* 267:    */     {
/* 268:310 */       skipWhiteSpaceIfNeeded();
/* 269:312 */       switch (this.__currentChar)
/* 270:    */       {
/* 271:    */       case '\n': 
/* 272:    */         break;
/* 273:    */       case '\r': 
/* 274:    */         break;
/* 275:    */       case ' ': 
/* 276:    */         break;
/* 277:    */       case '\t': 
/* 278:    */         break;
/* 279:    */       case '\b': 
/* 280:    */         break;
/* 281:    */       case '\f': 
/* 282:    */         break;
/* 283:    */       case '/': 
/* 284:332 */         handleComment();
/* 285:333 */         break;
/* 286:    */       case '#': 
/* 287:336 */         handleBashComment();
/* 288:337 */         break;
/* 289:    */       case '"': 
/* 290:340 */         value = decodeStringDouble();
/* 291:341 */         break;
/* 292:    */       case '\'': 
/* 293:344 */         value = decodeStringSingle();
/* 294:345 */         break;
/* 295:    */       case 't': 
/* 296:349 */         if (isTrue()) {
/* 297:350 */           return decodeTrueWithEvents() == true ? ValueContainer.TRUE : ValueContainer.FALSE;
/* 298:    */         }
/* 299:352 */         value = decodeStringLax();
/* 300:    */         
/* 301:354 */         break;
/* 302:    */       case 'f': 
/* 303:357 */         if (isFalse()) {
/* 304:358 */           return !decodeFalseWithEvents() ? ValueContainer.FALSE : ValueContainer.TRUE;
/* 305:    */         }
/* 306:360 */         value = decodeStringLax();
/* 307:    */         
/* 308:362 */         break;
/* 309:    */       case 'n': 
/* 310:365 */         if (isNull()) {
/* 311:366 */           return decodeNullWithEvents() == null ? ValueContainer.NULL : ValueContainer.NULL;
/* 312:    */         }
/* 313:368 */         value = decodeStringLax();
/* 314:    */         
/* 315:    */ 
/* 316:371 */         break;
/* 317:    */       case '[': 
/* 318:374 */         value = decodeJsonArrayLax(isRoot);
/* 319:375 */         break;
/* 320:    */       case '{': 
/* 321:378 */         value = decodeJsonObjectLax(isRoot);
/* 322:379 */         break;
/* 323:    */       case '0': 
/* 324:    */       case '1': 
/* 325:    */       case '2': 
/* 326:    */       case '3': 
/* 327:    */       case '4': 
/* 328:    */       case '5': 
/* 329:    */       case '6': 
/* 330:    */       case '7': 
/* 331:    */       case '8': 
/* 332:    */       case '9': 
/* 333:391 */         return decodeNumberLax(false);
/* 334:    */       case '-': 
/* 335:394 */         return decodeNumberLax(true);
/* 336:    */       case '\013': 
/* 337:    */       case '\016': 
/* 338:    */       case '\017': 
/* 339:    */       case '\020': 
/* 340:    */       case '\021': 
/* 341:    */       case '\022': 
/* 342:    */       case '\023': 
/* 343:    */       case '\024': 
/* 344:    */       case '\025': 
/* 345:    */       case '\026': 
/* 346:    */       case '\027': 
/* 347:    */       case '\030': 
/* 348:    */       case '\031': 
/* 349:    */       case '\032': 
/* 350:    */       case '\033': 
/* 351:    */       case '\034': 
/* 352:    */       case '\035': 
/* 353:    */       case '\036': 
/* 354:    */       case '\037': 
/* 355:    */       case '!': 
/* 356:    */       case '$': 
/* 357:    */       case '%': 
/* 358:    */       case '&': 
/* 359:    */       case '(': 
/* 360:    */       case ')': 
/* 361:    */       case '*': 
/* 362:    */       case '+': 
/* 363:    */       case ',': 
/* 364:    */       case '.': 
/* 365:    */       case ':': 
/* 366:    */       case ';': 
/* 367:    */       case '<': 
/* 368:    */       case '=': 
/* 369:    */       case '>': 
/* 370:    */       case '?': 
/* 371:    */       case '@': 
/* 372:    */       case 'A': 
/* 373:    */       case 'B': 
/* 374:    */       case 'C': 
/* 375:    */       case 'D': 
/* 376:    */       case 'E': 
/* 377:    */       case 'F': 
/* 378:    */       case 'G': 
/* 379:    */       case 'H': 
/* 380:    */       case 'I': 
/* 381:    */       case 'J': 
/* 382:    */       case 'K': 
/* 383:    */       case 'L': 
/* 384:    */       case 'M': 
/* 385:    */       case 'N': 
/* 386:    */       case 'O': 
/* 387:    */       case 'P': 
/* 388:    */       case 'Q': 
/* 389:    */       case 'R': 
/* 390:    */       case 'S': 
/* 391:    */       case 'T': 
/* 392:    */       case 'U': 
/* 393:    */       case 'V': 
/* 394:    */       case 'W': 
/* 395:    */       case 'X': 
/* 396:    */       case 'Y': 
/* 397:    */       case 'Z': 
/* 398:    */       case '\\': 
/* 399:    */       case ']': 
/* 400:    */       case '^': 
/* 401:    */       case '_': 
/* 402:    */       case '`': 
/* 403:    */       case 'a': 
/* 404:    */       case 'b': 
/* 405:    */       case 'c': 
/* 406:    */       case 'd': 
/* 407:    */       case 'e': 
/* 408:    */       case 'g': 
/* 409:    */       case 'h': 
/* 410:    */       case 'i': 
/* 411:    */       case 'j': 
/* 412:    */       case 'k': 
/* 413:    */       case 'l': 
/* 414:    */       case 'm': 
/* 415:    */       case 'o': 
/* 416:    */       case 'p': 
/* 417:    */       case 'q': 
/* 418:    */       case 'r': 
/* 419:    */       case 's': 
/* 420:    */       case 'u': 
/* 421:    */       case 'v': 
/* 422:    */       case 'w': 
/* 423:    */       case 'x': 
/* 424:    */       case 'y': 
/* 425:    */       case 'z': 
/* 426:    */       default: 
/* 427:397 */         value = decodeStringLax();
/* 428:    */       }
/* 429:400 */       if (value != null) {
/* 430:401 */         return value;
/* 431:    */       }
/* 432:    */     }
/* 433:405 */     return null;
/* 434:    */   }
/* 435:    */   
/* 436:    */   private void handleBashComment()
/* 437:    */   {
/* 438:410 */     for (; this.__index < this.charArray.length; this.__index += 1)
/* 439:    */     {
/* 440:411 */       this.__currentChar = this.charArray[this.__index];
/* 441:413 */       if (this.__currentChar == '\n')
/* 442:    */       {
/* 443:414 */         this.__index += 1;
/* 444:415 */         return;
/* 445:    */       }
/* 446:    */     }
/* 447:    */   }
/* 448:    */   
/* 449:    */   private void handleComment()
/* 450:    */   {
/* 451:422 */     if (hasMore())
/* 452:    */     {
/* 453:423 */       this.__index += 1;
/* 454:424 */       this.__currentChar = this.charArray[this.__index];
/* 455:426 */       switch (this.__currentChar)
/* 456:    */       {
/* 457:    */       case '*': 
/* 458:428 */         for (; this.__index < this.charArray.length; this.__index += 1)
/* 459:    */         {
/* 460:429 */           this.__currentChar = this.charArray[this.__index];
/* 461:431 */           if (this.__currentChar == '*') {
/* 462:432 */             if (hasMore())
/* 463:    */             {
/* 464:433 */               this.__index += 1;
/* 465:434 */               this.__currentChar = this.charArray[this.__index];
/* 466:435 */               if ((this.__currentChar == '/') && 
/* 467:436 */                 (hasMore())) {
/* 468:437 */                 this.__index += 1;
/* 469:    */               }
/* 470:    */             }
/* 471:    */             else
/* 472:    */             {
/* 473:442 */               complain("missing close of comment");
/* 474:    */             }
/* 475:    */           }
/* 476:    */         }
/* 477:    */       case '/': 
/* 478:448 */         for (; this.__index < this.charArray.length; this.__index += 1)
/* 479:    */         {
/* 480:449 */           this.__currentChar = this.charArray[this.__index];
/* 481:451 */           if (this.__currentChar == '\n')
/* 482:    */           {
/* 483:452 */             if (hasMore())
/* 484:    */             {
/* 485:453 */               this.__index += 1;
/* 486:454 */               return;
/* 487:    */             }
/* 488:456 */             return;
/* 489:    */           }
/* 490:    */         }
/* 491:    */       }
/* 492:    */     }
/* 493:    */   }
/* 494:    */   
/* 495:    */   protected final Value decodeNumberLax(boolean minus)
/* 496:    */   {
/* 497:466 */     char[] array = this.charArray;
/* 498:    */     
/* 499:468 */     int startIndex = this.__index;
/* 500:469 */     int index = this.__index;
/* 501:    */     
/* 502:471 */     boolean doubleFloat = false;
/* 503:473 */     if ((minus) && (index + 1 < array.length)) {
/* 504:474 */       index++;
/* 505:    */     }
/* 506:    */     char currentChar;
/* 507:    */     for (;;)
/* 508:    */     {
/* 509:478 */       currentChar = array[index];
/* 510:479 */       if (!CharScanner.isNumberDigit(currentChar))
/* 511:    */       {
/* 512:481 */         if (currentChar <= ' ') {
/* 513:    */           break;
/* 514:    */         }
/* 515:483 */         if (CharScanner.isDelimiter(currentChar)) {
/* 516:    */           break;
/* 517:    */         }
/* 518:485 */         if (CharScanner.isDecimalChar(currentChar)) {
/* 519:486 */           doubleFloat = true;
/* 520:    */         }
/* 521:    */       }
/* 522:488 */       index++;
/* 523:489 */       if (index >= array.length) {
/* 524:    */         break;
/* 525:    */       }
/* 526:    */     }
/* 527:492 */     this.__index = index;
/* 528:493 */     this.__currentChar = currentChar;
/* 529:    */     
/* 530:495 */     TypeType type = doubleFloat ? TypeType.DOUBLE : TypeType.INT;
/* 531:    */     
/* 532:497 */     NumberValue value = new NumberValue(this.chop, type, startIndex, this.__index, this.charArray);
/* 533:498 */     if ((this.events != null) && (!this.events.number(startIndex, this.__index, value))) {
/* 534:498 */       stop();
/* 535:    */     }
/* 536:500 */     return value;
/* 537:    */   }
/* 538:    */   
/* 539:    */   private boolean isNull()
/* 540:    */   {
/* 541:504 */     if ((this.__index + NULL.length <= this.charArray.length) && 
/* 542:505 */       (this.charArray[this.__index] == 'n') && (this.charArray[(this.__index + 1)] == 'u') && (this.charArray[(this.__index + 2)] == 'l') && (this.charArray[(this.__index + 3)] == 'l')) {
/* 543:509 */       return true;
/* 544:    */     }
/* 545:512 */     return false;
/* 546:    */   }
/* 547:    */   
/* 548:    */   private boolean isTrue()
/* 549:    */   {
/* 550:516 */     if ((this.__index + TRUE.length <= this.charArray.length) && 
/* 551:517 */       (this.charArray[this.__index] == 't') && (this.charArray[(this.__index + 1)] == 'r') && (this.charArray[(this.__index + 2)] == 'u') && (this.charArray[(this.__index + 3)] == 'e')) {
/* 552:521 */       return true;
/* 553:    */     }
/* 554:525 */     return false;
/* 555:    */   }
/* 556:    */   
/* 557:    */   private boolean isFalse()
/* 558:    */   {
/* 559:529 */     if ((this.__index + FALSE.length <= this.charArray.length) && 
/* 560:530 */       (this.charArray[this.__index] == 'f') && (this.charArray[(this.__index + 1)] == 'a') && (this.charArray[(this.__index + 2)] == 'l') && (this.charArray[(this.__index + 3)] == 's') && (this.charArray[(this.__index + 4)] == 'e')) {
/* 561:535 */       return true;
/* 562:    */     }
/* 563:538 */     return false;
/* 564:    */   }
/* 565:    */   
/* 566:    */   private Value decodeStringLax()
/* 567:    */   {
/* 568:542 */     int index = this.__index;
/* 569:    */     
/* 570:544 */     int startIndex = this.__index;
/* 571:545 */     boolean encoded = false;
/* 572:546 */     char[] charArray = this.charArray;
/* 573:548 */     for (; index < charArray.length; index++)
/* 574:    */     {
/* 575:549 */       char currentChar = charArray[index];
/* 576:551 */       if ((CharScanner.isDelimiter(currentChar)) || 
/* 577:552 */         (currentChar == '\\')) {
/* 578:    */         break;
/* 579:    */       }
/* 580:    */     }
/* 581:555 */     Value value = extractLaxString(startIndex, index, encoded, this.checkDates);
/* 582:556 */     this.__index = index;
/* 583:557 */     return value;
/* 584:    */   }
/* 585:    */   
/* 586:    */   private Value decodeStringDouble()
/* 587:    */   {
/* 588:562 */     this.__currentChar = this.charArray[this.__index];
/* 589:564 */     if ((this.__index < this.charArray.length) && (this.__currentChar == '"')) {
/* 590:565 */       this.__index += 1;
/* 591:    */     }
/* 592:568 */     int startIndex = this.__index;
/* 593:    */     
/* 594:570 */     boolean escape = false;
/* 595:571 */     boolean encoded = false;
/* 596:574 */     for (; this.__index < this.charArray.length; this.__index += 1)
/* 597:    */     {
/* 598:575 */       this.__currentChar = this.charArray[this.__index];
/* 599:576 */       switch (this.__currentChar)
/* 600:    */       {
/* 601:    */       case '"': 
/* 602:579 */         if (!escape) {
/* 603:    */           break label151;
/* 604:    */         }
/* 605:582 */         escape = false;
/* 606:583 */         break;
/* 607:    */       case '\\': 
/* 608:587 */         if (!escape) {
/* 609:588 */           escape = true;
/* 610:    */         } else {
/* 611:590 */           escape = false;
/* 612:    */         }
/* 613:592 */         encoded = true;
/* 614:593 */         break;
/* 615:    */       default: 
/* 616:595 */         escape = false;
/* 617:    */       }
/* 618:    */     }
/* 619:    */     label151:
/* 620:598 */     CharSequenceValue value = new CharSequenceValue(this.chop, TypeType.STRING, startIndex, this.__index, this.charArray, encoded, this.checkDates);
/* 621:600 */     if (this.__index < this.charArray.length) {
/* 622:601 */       this.__index += 1;
/* 623:    */     }
/* 624:603 */     if ((this.events != null) && (!this.events.string(startIndex, this.__index, value))) {
/* 625:603 */       stop();
/* 626:    */     }
/* 627:605 */     return value;
/* 628:    */   }
/* 629:    */   
/* 630:    */   private Value decodeStringSingle()
/* 631:    */   {
/* 632:610 */     this.__currentChar = this.charArray[this.__index];
/* 633:612 */     if ((this.__index < this.charArray.length) && (this.__currentChar == '\'')) {
/* 634:613 */       this.__index += 1;
/* 635:    */     }
/* 636:616 */     int startIndex = this.__index;
/* 637:    */     
/* 638:618 */     boolean escape = false;
/* 639:619 */     boolean encoded = false;
/* 640:620 */     int minusCount = 0;
/* 641:621 */     int colonCount = 0;
/* 642:624 */     for (; this.__index < this.charArray.length; this.__index += 1)
/* 643:    */     {
/* 644:625 */       this.__currentChar = this.charArray[this.__index];
/* 645:626 */       switch (this.__currentChar)
/* 646:    */       {
/* 647:    */       case '\'': 
/* 648:629 */         if (!escape) {
/* 649:    */           break label175;
/* 650:    */         }
/* 651:632 */         escape = false;
/* 652:633 */         break;
/* 653:    */       case '\\': 
/* 654:637 */         encoded = true;
/* 655:638 */         escape = true;
/* 656:639 */         break;
/* 657:    */       case '-': 
/* 658:642 */         minusCount++;
/* 659:643 */         break;
/* 660:    */       case ':': 
/* 661:645 */         colonCount++;
/* 662:    */       }
/* 663:648 */       escape = false;
/* 664:    */     }
/* 665:    */     label175:
/* 666:651 */     boolean checkDates = (this.checkDates) && (!encoded) && (minusCount >= 2) && (colonCount >= 2);
/* 667:653 */     CharSequenceValue value = new CharSequenceValue(this.chop, TypeType.STRING, startIndex, this.__index, this.charArray, encoded, checkDates);
/* 668:655 */     if (this.__index < this.charArray.length) {
/* 669:656 */       this.__index += 1;
/* 670:    */     }
/* 671:660 */     if ((this.events != null) && (!this.events.string(startIndex, this.__index, value))) {
/* 672:660 */       stop();
/* 673:    */     }
/* 674:661 */     return value;
/* 675:    */   }
/* 676:    */   
/* 677:    */   private Value decodeJsonArrayLax(boolean isRoot)
/* 678:    */   {
/* 679:666 */     if ((this.events != null) && (!this.events.arrayStart(this.__index))) {
/* 680:666 */       stop();
/* 681:    */     }
/* 682:668 */     if (this.__currentChar == '[') {
/* 683:669 */       this.__index += 1;
/* 684:    */     }
/* 685:673 */     skipWhiteSpaceIfNeeded();
/* 686:675 */     if (this.__currentChar == ']')
/* 687:    */     {
/* 688:676 */       this.__index += 1;
/* 689:677 */       return EMPTY_LIST;
/* 690:    */     }
/* 691:    */     List<Object> list;
/* 692:    */     List<Object> list;
/* 693:682 */     if (this.useValues) {
/* 694:683 */       list = new ArrayList();
/* 695:    */     } else {
/* 696:685 */       list = new ValueList(this.lazyChop);
/* 697:    */     }
/* 698:688 */     if ((this.events != null) && (isRoot)) {
/* 699:689 */       this.root = list;
/* 700:    */     }
/* 701:692 */     Value value = new ValueContainer(list);
/* 702:    */     do
/* 703:    */     {
/* 704:696 */       skipWhiteSpaceIfNeeded();
/* 705:    */       
/* 706:698 */       Object arrayItem = decodeValueInternal();
/* 707:    */       
/* 708:    */ 
/* 709:701 */       list.add(arrayItem);
/* 710:703 */       if ((this.events != null) && (!this.events.arrayItem(this.__index, list, arrayItem))) {
/* 711:703 */         stop();
/* 712:    */       }
/* 713:705 */       skipWhiteSpaceIfNeeded();
/* 714:    */       
/* 715:707 */       char c = this.__currentChar;
/* 716:709 */       if (c == ',')
/* 717:    */       {
/* 718:710 */         this.__index += 1;
/* 719:    */       }
/* 720:    */       else
/* 721:    */       {
/* 722:712 */         if (c == ']')
/* 723:    */         {
/* 724:713 */           this.__index += 1;
/* 725:714 */           break;
/* 726:    */         }
/* 727:716 */         String charString = charDescription(c);
/* 728:    */         
/* 729:718 */         complain(String.format("expecting a ',' or a ']',  but got \nthe current character of  %s  on array index of %s \n", new Object[] { charString, Integer.valueOf(list.size()) }));
/* 730:    */       }
/* 731:724 */     } while (hasMore());
/* 732:727 */     if ((this.events != null) && (!this.events.arrayEnd(this.__index, list))) {
/* 733:727 */       stop();
/* 734:    */     }
/* 735:728 */     return value;
/* 736:    */   }
/* 737:    */   
/* 738:    */   public static class StopException
/* 739:    */     extends JsonException
/* 740:    */   {
/* 741:    */     public StopException(String message)
/* 742:    */     {
/* 743:734 */       super();
/* 744:    */     }
/* 745:    */   }
/* 746:    */   
/* 747:    */   private void stop()
/* 748:    */   {
/* 749:739 */     throw new StopException("STOPPED BY EVENT HANDLER at index " + this.__index);
/* 750:    */   }
/* 751:    */   
/* 752:    */   protected final boolean decodeTrueWithEvents()
/* 753:    */   {
/* 754:745 */     if ((this.__index + TRUE.length <= this.charArray.length) && 
/* 755:746 */       (this.charArray[this.__index] == 't') && (this.charArray[(++this.__index)] == 'r') && (this.charArray[(++this.__index)] == 'u') && (this.charArray[(++this.__index)] == 'e'))
/* 756:    */     {
/* 757:751 */       if ((this.events != null) && (!this.events.bool(this.__index, true))) {
/* 758:751 */         stop();
/* 759:    */       }
/* 760:753 */       this.__index += 1;
/* 761:754 */       return true;
/* 762:    */     }
/* 763:759 */     throw new JsonException(exceptionDetails("true not parsed properly"));
/* 764:    */   }
/* 765:    */   
/* 766:    */   protected final boolean decodeFalseWithEvents()
/* 767:    */   {
/* 768:766 */     if ((this.__index + FALSE.length <= this.charArray.length) && 
/* 769:767 */       (this.charArray[this.__index] == 'f') && (this.charArray[(++this.__index)] == 'a') && (this.charArray[(++this.__index)] == 'l') && (this.charArray[(++this.__index)] == 's') && (this.charArray[(++this.__index)] == 'e'))
/* 770:    */     {
/* 771:772 */       if ((this.events != null) && (!this.events.bool(this.__index, false))) {
/* 772:772 */         stop();
/* 773:    */       }
/* 774:774 */       this.__index += 1;
/* 775:    */       
/* 776:776 */       return false;
/* 777:    */     }
/* 778:779 */     throw new JsonException(exceptionDetails("false not parsed properly"));
/* 779:    */   }
/* 780:    */   
/* 781:    */   protected final Object decodeNullWithEvents()
/* 782:    */   {
/* 783:787 */     if ((this.__index + NULL.length <= this.charArray.length) && 
/* 784:788 */       (this.charArray[this.__index] == 'n') && (this.charArray[(++this.__index)] == 'u') && (this.charArray[(++this.__index)] == 'l') && (this.charArray[(++this.__index)] == 'l'))
/* 785:    */     {
/* 786:792 */       this.__index += 1;
/* 787:793 */       if ((this.events != null) && (!this.events.nullValue(this.__index))) {
/* 788:793 */         stop();
/* 789:    */       }
/* 790:795 */       return null;
/* 791:    */     }
/* 792:798 */     throw new JsonException(exceptionDetails("null not parse properly"));
/* 793:    */   }
/* 794:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.implementation.JsonParserLax
 * JD-Core Version:    0.7.0.1
 */