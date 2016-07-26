/*   1:    */ package org.boon.json.implementation;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import org.boon.core.TypeType;
/*   6:    */ import org.boon.core.Value;
/*   7:    */ import org.boon.core.value.CharSequenceValue;
/*   8:    */ import org.boon.core.value.LazyValueMap;
/*   9:    */ import org.boon.core.value.MapItemValue;
/*  10:    */ import org.boon.core.value.NumberValue;
/*  11:    */ import org.boon.core.value.ValueContainer;
/*  12:    */ import org.boon.core.value.ValueList;
/*  13:    */ import org.boon.core.value.ValueMap;
/*  14:    */ import org.boon.core.value.ValueMapImpl;
/*  15:    */ import org.boon.primitive.CharScanner;
/*  16:    */ 
/*  17:    */ public class PlistParser
/*  18:    */   extends JsonFastParser
/*  19:    */ {
/*  20:    */   protected static final int CLOSED_PAREN = 41;
/*  21:    */   protected static final int SEMI_COLON = 59;
/*  22:    */   
/*  23:    */   public PlistParser() {}
/*  24:    */   
/*  25:    */   public PlistParser(boolean useValues)
/*  26:    */   {
/*  27: 54 */     super(useValues);
/*  28:    */   }
/*  29:    */   
/*  30:    */   public PlistParser(boolean useValues, boolean chop)
/*  31:    */   {
/*  32: 58 */     super(useValues, chop);
/*  33:    */   }
/*  34:    */   
/*  35:    */   public PlistParser(boolean useValues, boolean chop, boolean lazyChop)
/*  36:    */   {
/*  37: 62 */     super(useValues, chop, lazyChop);
/*  38:    */   }
/*  39:    */   
/*  40:    */   public PlistParser(boolean useValues, boolean chop, boolean lazyChop, boolean checkDates)
/*  41:    */   {
/*  42: 66 */     super(useValues, chop, lazyChop, checkDates);
/*  43:    */   }
/*  44:    */   
/*  45:    */   private Value decodeJsonObjectLax()
/*  46:    */   {
/*  47: 71 */     if (this.__currentChar == '{') {
/*  48: 72 */       nextChar();
/*  49:    */     }
/*  50: 74 */     ValueMap map = (ValueMap)(this.useValues ? new ValueMapImpl() : new LazyValueMap(this.lazyChop));
/*  51: 75 */     Value value = new ValueContainer(map);
/*  52:    */     
/*  53: 77 */     skipWhiteSpaceIfNeeded();
/*  54: 78 */     int startIndexOfKey = this.__index;
/*  55: 84 */     for (; this.__index < this.charArray.length; this.__index += 1)
/*  56:    */     {
/*  57: 86 */       skipWhiteSpaceIfNeeded();
/*  58:    */       Value key;
/*  59:    */       Value item;
/*  60:    */       MapItemValue miv;
/*  61: 88 */       switch (this.__currentChar)
/*  62:    */       {
/*  63:    */       case '/': 
/*  64: 90 */         handleComment();
/*  65: 91 */         startIndexOfKey = this.__index;
/*  66: 92 */         break;
/*  67:    */       case '#': 
/*  68: 95 */         handleBashComment();
/*  69: 96 */         startIndexOfKey = this.__index;
/*  70: 97 */         break;
/*  71:    */       case '=': 
/*  72:101 */         char startChar = this.charArray[startIndexOfKey];
/*  73:102 */         if (startChar == ';') {
/*  74:103 */           startIndexOfKey++;
/*  75:    */         }
/*  76:107 */         key = extractLaxString(startIndexOfKey, this.__index - 1, false, false);
/*  77:108 */         this.__index += 1;
/*  78:    */         
/*  79:    */ 
/*  80:111 */         item = decodeValuePlist();
/*  81:112 */         skipWhiteSpaceIfNeeded();
/*  82:    */         
/*  83:114 */         miv = new MapItemValue(key, item);
/*  84:    */         
/*  85:116 */         map.add(miv);
/*  86:    */         
/*  87:118 */         startIndexOfKey = this.__index;
/*  88:119 */         if (this.__currentChar == '}') {
/*  89:120 */           this.__index += 1;
/*  90:    */         }
/*  91:121 */         break;
/*  92:    */       case '"': 
/*  93:127 */         key = decodeStringPlist();
/*  94:    */         
/*  95:    */ 
/*  96:130 */         skipWhiteSpaceIfNeeded();
/*  97:132 */         if (this.__currentChar != '=') {
/*  98:134 */           complain("expecting current character to be '='  but got " + charDescription(this.__currentChar) + "\n");
/*  99:    */         }
/* 100:136 */         this.__index += 1;
/* 101:137 */         item = decodeValuePlist();
/* 102:    */         
/* 103:    */ 
/* 104:140 */         skipWhiteSpaceIfNeeded();
/* 105:141 */         miv = new MapItemValue(key, item);
/* 106:    */         
/* 107:143 */         map.add(miv);
/* 108:    */         
/* 109:145 */         startIndexOfKey = this.__index;
/* 110:146 */         if (this.__currentChar == '}')
/* 111:    */         {
/* 112:147 */           this.__index += 1;
/* 113:148 */           if ((!hasMore()) || 
/* 114:149 */             (this.charArray[this.__index] != ';')) {
/* 115:    */             return value;
/* 116:    */           }
/* 117:150 */           this.__index += 1;
/* 118:    */         }
/* 119:    */         break;
/* 120:    */       case '}': 
/* 121:160 */         this.__index += 1;
/* 122:161 */         if ((!hasMore()) || 
/* 123:162 */           (this.charArray[this.__index] != ';')) {
/* 124:    */           return value;
/* 125:    */         }
/* 126:163 */         this.__index += 1; return value;
/* 127:    */       }
/* 128:    */     }
/* 129:170 */     return value;
/* 130:    */   }
/* 131:    */   
/* 132:    */   private Value extractLaxString(int startIndexOfKey, int end, boolean encoded, boolean checkDate)
/* 133:    */   {
/* 134:176 */     for (; (startIndexOfKey < this.__index) && (startIndexOfKey < this.charArray.length); startIndexOfKey++)
/* 135:    */     {
/* 136:177 */       char startChar = this.charArray[startIndexOfKey];
/* 137:178 */       switch (startChar)
/* 138:    */       {
/* 139:    */       case '\t': 
/* 140:    */       case '\n': 
/* 141:    */       case ' ': 
/* 142:    */         break;
/* 143:    */       default: 
/* 144:    */         break label72;
/* 145:    */       }
/* 146:    */     }
/* 147:    */     label72:
/* 148:190 */     for (int endIndex = end >= this.charArray.length ? this.charArray.length - 1 : end; (endIndex >= startIndexOfKey + 1) && (endIndex >= 0); endIndex--)
/* 149:    */     {
/* 150:193 */       char endChar = this.charArray[endIndex];
/* 151:194 */       switch (endChar)
/* 152:    */       {
/* 153:    */       case '\t': 
/* 154:    */       case '\n': 
/* 155:    */       case ' ': 
/* 156:    */       case '}': 
/* 157:    */         break;
/* 158:    */       case ',': 
/* 159:    */       case ';': 
/* 160:    */         break;
/* 161:    */       case ')': 
/* 162:    */         break;
/* 163:    */       default: 
/* 164:    */         break label202;
/* 165:    */       }
/* 166:    */     }
/* 167:    */     label202:
/* 168:210 */     return new CharSequenceValue(this.chop, TypeType.STRING, startIndexOfKey, endIndex + 1, this.charArray, encoded, checkDate);
/* 169:    */   }
/* 170:    */   
/* 171:    */   private Value decodeValuePlist()
/* 172:    */   {
/* 173:214 */     Value value = null;
/* 174:216 */     for (; this.__index < this.charArray.length; this.__index += 1)
/* 175:    */     {
/* 176:217 */       skipWhiteSpaceIfNeeded();
/* 177:219 */       switch (this.__currentChar)
/* 178:    */       {
/* 179:    */       case '/': 
/* 180:222 */         handleComment();
/* 181:223 */         break;
/* 182:    */       case '#': 
/* 183:226 */         handleBashComment();
/* 184:227 */         break;
/* 185:    */       case '"': 
/* 186:230 */         value = decodeStringPlist();
/* 187:231 */         break;
/* 188:    */       case 't': 
/* 189:234 */         if (isTrue()) {
/* 190:235 */           return decodeTrue() == true ? ValueContainer.TRUE : ValueContainer.FALSE;
/* 191:    */         }
/* 192:237 */         value = decodeStringLax();
/* 193:    */         
/* 194:239 */         break;
/* 195:    */       case 'f': 
/* 196:242 */         if (isFalse()) {
/* 197:243 */           return !decodeFalse() ? ValueContainer.FALSE : ValueContainer.TRUE;
/* 198:    */         }
/* 199:245 */         value = decodeStringLax();
/* 200:    */         
/* 201:247 */         break;
/* 202:    */       case 'n': 
/* 203:250 */         if (isNull()) {
/* 204:251 */           return decodeNull() == null ? ValueContainer.NULL : ValueContainer.NULL;
/* 205:    */         }
/* 206:253 */         value = decodeStringLax();
/* 207:    */         
/* 208:    */ 
/* 209:256 */         break;
/* 210:    */       case '(': 
/* 211:259 */         value = decodeJsonArrayLax();
/* 212:260 */         break;
/* 213:    */       case '{': 
/* 214:263 */         value = decodeJsonObjectLax();
/* 215:264 */         break;
/* 216:    */       case '0': 
/* 217:    */       case '1': 
/* 218:    */       case '2': 
/* 219:    */       case '3': 
/* 220:    */       case '4': 
/* 221:    */       case '5': 
/* 222:    */       case '6': 
/* 223:    */       case '7': 
/* 224:    */       case '8': 
/* 225:    */       case '9': 
/* 226:277 */         value = decodeNumberPLIST(false);
/* 227:278 */         break;
/* 228:    */       case '-': 
/* 229:280 */         value = decodeNumberPLIST(true);
/* 230:281 */         break;
/* 231:    */       default: 
/* 232:284 */         value = decodeStringLax();
/* 233:    */       }
/* 234:288 */       if (value != null) {
/* 235:289 */         return value;
/* 236:    */       }
/* 237:    */     }
/* 238:293 */     return null;
/* 239:    */   }
/* 240:    */   
/* 241:    */   private void handleBashComment()
/* 242:    */   {
/* 243:297 */     for (; this.__index < this.charArray.length; this.__index += 1)
/* 244:    */     {
/* 245:298 */       this.__currentChar = this.charArray[this.__index];
/* 246:300 */       if (this.__currentChar == '\n')
/* 247:    */       {
/* 248:301 */         this.__index += 1;
/* 249:302 */         return;
/* 250:    */       }
/* 251:    */     }
/* 252:    */   }
/* 253:    */   
/* 254:    */   private void handleComment()
/* 255:    */   {
/* 256:310 */     if (hasMore())
/* 257:    */     {
/* 258:312 */       this.__index += 1;
/* 259:313 */       this.__currentChar = this.charArray[this.__index];
/* 260:315 */       switch (this.__currentChar)
/* 261:    */       {
/* 262:    */       case '*': 
/* 263:317 */         for (; this.__index < this.charArray.length; this.__index += 1)
/* 264:    */         {
/* 265:318 */           this.__currentChar = this.charArray[this.__index];
/* 266:320 */           if (this.__currentChar == '*') {
/* 267:321 */             if (hasMore())
/* 268:    */             {
/* 269:322 */               this.__index += 1;
/* 270:323 */               this.__currentChar = this.charArray[this.__index];
/* 271:324 */               if ((this.__currentChar == '/') && 
/* 272:325 */                 (hasMore())) {
/* 273:326 */                 this.__index += 1;
/* 274:    */               }
/* 275:    */             }
/* 276:    */             else
/* 277:    */             {
/* 278:331 */               complain("missing close of comment");
/* 279:    */             }
/* 280:    */           }
/* 281:    */         }
/* 282:    */       case '/': 
/* 283:339 */         for (; this.__index < this.charArray.length; this.__index += 1)
/* 284:    */         {
/* 285:340 */           this.__currentChar = this.charArray[this.__index];
/* 286:342 */           if (this.__currentChar == '\n')
/* 287:    */           {
/* 288:343 */             if (hasMore())
/* 289:    */             {
/* 290:344 */               this.__index += 1;
/* 291:345 */               return;
/* 292:    */             }
/* 293:347 */             return;
/* 294:    */           }
/* 295:    */         }
/* 296:    */       }
/* 297:    */     }
/* 298:    */   }
/* 299:    */   
/* 300:    */   protected static boolean isPLISTDelimiter(int c)
/* 301:    */   {
/* 302:363 */     return (c == 59) || (c == 125) || (c == 41) || (c == 44);
/* 303:    */   }
/* 304:    */   
/* 305:    */   protected Value decodeNumberPLIST(boolean minus)
/* 306:    */   {
/* 307:367 */     char[] array = this.charArray;
/* 308:    */     
/* 309:369 */     int startIndex = this.__index;
/* 310:370 */     int index = this.__index;
/* 311:    */     
/* 312:372 */     boolean doubleFloat = false;
/* 313:374 */     if ((minus) && (index + 1 < array.length)) {
/* 314:375 */       index++;
/* 315:    */     }
/* 316:    */     char currentChar;
/* 317:    */     for (;;)
/* 318:    */     {
/* 319:380 */       currentChar = array[index];
/* 320:381 */       if (!CharScanner.isNumberDigit(currentChar))
/* 321:    */       {
/* 322:383 */         if (currentChar <= ' ') {
/* 323:    */           break;
/* 324:    */         }
/* 325:385 */         if (isPLISTDelimiter(currentChar)) {
/* 326:    */           break;
/* 327:    */         }
/* 328:387 */         if (CharScanner.isDecimalChar(currentChar)) {
/* 329:388 */           doubleFloat = true;
/* 330:    */         }
/* 331:    */       }
/* 332:390 */       index++;
/* 333:391 */       if (index >= array.length) {
/* 334:    */         break;
/* 335:    */       }
/* 336:    */     }
/* 337:394 */     this.__index = index;
/* 338:395 */     this.__currentChar = currentChar;
/* 339:    */     
/* 340:397 */     TypeType type = doubleFloat ? TypeType.DOUBLE : TypeType.INT;
/* 341:    */     
/* 342:399 */     NumberValue value = new NumberValue(this.chop, type, startIndex, this.__index, this.charArray);
/* 343:    */     
/* 344:401 */     return value;
/* 345:    */   }
/* 346:    */   
/* 347:    */   private boolean isNull()
/* 348:    */   {
/* 349:406 */     if ((this.__index + NULL.length <= this.charArray.length) && 
/* 350:407 */       (this.charArray[this.__index] == 'n') && (this.charArray[(this.__index + 1)] == 'u') && (this.charArray[(this.__index + 2)] == 'l') && (this.charArray[(this.__index + 3)] == 'l')) {
/* 351:411 */       return true;
/* 352:    */     }
/* 353:414 */     return false;
/* 354:    */   }
/* 355:    */   
/* 356:    */   private boolean isTrue()
/* 357:    */   {
/* 358:419 */     if ((this.__index + TRUE.length <= this.charArray.length) && 
/* 359:420 */       (this.charArray[this.__index] == 't') && (this.charArray[(this.__index + 1)] == 'r') && (this.charArray[(this.__index + 2)] == 'u') && (this.charArray[(this.__index + 3)] == 'e')) {
/* 360:424 */       return true;
/* 361:    */     }
/* 362:428 */     return false;
/* 363:    */   }
/* 364:    */   
/* 365:    */   private boolean isFalse()
/* 366:    */   {
/* 367:433 */     if ((this.__index + FALSE.length <= this.charArray.length) && 
/* 368:434 */       (this.charArray[this.__index] == 'f') && (this.charArray[(this.__index + 1)] == 'a') && (this.charArray[(this.__index + 2)] == 'l') && (this.charArray[(this.__index + 3)] == 's') && (this.charArray[(this.__index + 4)] == 'e')) {
/* 369:439 */       return true;
/* 370:    */     }
/* 371:442 */     return false;
/* 372:    */   }
/* 373:    */   
/* 374:    */   private Value decodeStringLax()
/* 375:    */   {
/* 376:446 */     int index = this.__index;
/* 377:447 */     char currentChar = this.charArray[this.__index];
/* 378:448 */     int startIndex = this.__index;
/* 379:449 */     boolean encoded = false;
/* 380:450 */     char[] charArray = this.charArray;
/* 381:452 */     for (; index < charArray.length; index++)
/* 382:    */     {
/* 383:453 */       currentChar = charArray[index];
/* 384:455 */       if ((isPLISTDelimiter(currentChar)) || 
/* 385:456 */         (currentChar == '\\')) {
/* 386:    */         break;
/* 387:    */       }
/* 388:    */     }
/* 389:459 */     Value value = extractLaxString(startIndex, index, encoded, this.checkDates);
/* 390:    */     
/* 391:461 */     this.__index = index;
/* 392:462 */     return value;
/* 393:    */   }
/* 394:    */   
/* 395:    */   private Value decodeStringPlist()
/* 396:    */   {
/* 397:467 */     char[] array = this.charArray;
/* 398:468 */     int index = this.__index;
/* 399:469 */     char currentChar = this.charArray[index];
/* 400:471 */     if ((index < array.length) && (currentChar == '"')) {
/* 401:472 */       index++;
/* 402:    */     }
/* 403:475 */     int startIndex = index;
/* 404:    */     
/* 405:477 */     boolean encoded = CharScanner.hasEscapeChar(array, index, this.indexHolder);
/* 406:478 */     index = this.indexHolder[0];
/* 407:480 */     if (encoded) {
/* 408:481 */       index = CharScanner.findEndQuote(array, index);
/* 409:    */     }
/* 410:484 */     Value value = new CharSequenceValue(this.chop, TypeType.STRING, startIndex, index, array, encoded, this.checkDates);
/* 411:486 */     if (index < array.length) {
/* 412:487 */       index++;
/* 413:    */     }
/* 414:490 */     this.__index = index;
/* 415:491 */     return value;
/* 416:    */   }
/* 417:    */   
/* 418:    */   private Value decodeJsonArrayLax()
/* 419:    */   {
/* 420:496 */     if (this.__currentChar == '(') {
/* 421:497 */       this.__index += 1;
/* 422:    */     }
/* 423:500 */     skipWhiteSpaceIfNeeded();
/* 424:503 */     if (this.__currentChar == ')')
/* 425:    */     {
/* 426:504 */       this.__index += 1;
/* 427:505 */       return EMPTY_LIST;
/* 428:    */     }
/* 429:    */     List<Object> list;
/* 430:    */     List<Object> list;
/* 431:510 */     if (this.useValues) {
/* 432:511 */       list = new ArrayList();
/* 433:    */     } else {
/* 434:513 */       list = new ValueList(this.lazyChop);
/* 435:    */     }
/* 436:516 */     Value value = new ValueContainer(list);
/* 437:    */     
/* 438:518 */     skipWhiteSpaceIfNeeded();
/* 439:    */     do
/* 440:    */     {
/* 441:521 */       skipWhiteSpaceIfNeeded();
/* 442:    */       
/* 443:523 */       Object arrayItem = decodeValuePlist();
/* 444:    */       
/* 445:525 */       list.add(arrayItem);
/* 446:    */       
/* 447:527 */       skipWhiteSpaceIfNeeded();
/* 448:    */       
/* 449:529 */       char c = this.__currentChar;
/* 450:531 */       if (c == ',')
/* 451:    */       {
/* 452:532 */         this.__index += 1;
/* 453:    */       }
/* 454:    */       else
/* 455:    */       {
/* 456:534 */         if (c == ')')
/* 457:    */         {
/* 458:535 */           this.__index += 1;
/* 459:536 */           break;
/* 460:    */         }
/* 461:539 */         String charString = charDescription(c);
/* 462:    */         
/* 463:541 */         complain(String.format("expecting a ',' or a ')',  but got \nthe current character of  %s  on array index of %s \n", new Object[] { charString, Integer.valueOf(list.size()) }));
/* 464:    */       }
/* 465:548 */     } while (hasMore());
/* 466:550 */     return value;
/* 467:    */   }
/* 468:    */   
/* 469:    */   public Object parse(char[] chars)
/* 470:    */   {
/* 471:555 */     this.lastIndex = (chars.length - 1);
/* 472:556 */     this.__index = 0;
/* 473:557 */     this.charArray = chars;
/* 474:    */     
/* 475:559 */     Value value = decodeValuePlist();
/* 476:560 */     if (value.isContainer()) {
/* 477:561 */       return value.toValue();
/* 478:    */     }
/* 479:563 */     return value;
/* 480:    */   }
/* 481:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.implementation.PlistParser
 * JD-Core Version:    0.7.0.1
 */