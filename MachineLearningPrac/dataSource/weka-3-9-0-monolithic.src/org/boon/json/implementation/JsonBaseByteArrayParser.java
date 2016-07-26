/*   1:    */ package org.boon.json.implementation;
/*   2:    */ 
/*   3:    */ import java.io.InputStream;
/*   4:    */ import java.nio.charset.Charset;
/*   5:    */ import java.util.ArrayList;
/*   6:    */ import java.util.Collections;
/*   7:    */ import java.util.List;
/*   8:    */ import java.util.Map;
/*   9:    */ import java.util.concurrent.ConcurrentHashMap;
/*  10:    */ import org.boon.IO;
/*  11:    */ import org.boon.collections.LazyMap;
/*  12:    */ import org.boon.core.Typ;
/*  13:    */ import org.boon.core.reflection.MapObjectConversion;
/*  14:    */ import org.boon.json.JsonException;
/*  15:    */ import org.boon.primitive.Byt;
/*  16:    */ import org.boon.primitive.ByteScanner;
/*  17:    */ import org.boon.primitive.CharBuf;
/*  18:    */ import org.boon.primitive.InMemoryInputStream;
/*  19:    */ 
/*  20:    */ public abstract class JsonBaseByteArrayParser
/*  21:    */   extends BaseJsonParser
/*  22:    */ {
/*  23:    */   protected byte[] charArray;
/*  24:    */   protected int __index;
/*  25:    */   protected int __currentChar;
/*  26:    */   protected static final int NEW_LINE = 10;
/*  27:    */   protected static final int RETURN = 13;
/*  28:    */   protected static final int SPACE = 32;
/*  29:    */   protected static final int TAB = 9;
/*  30:    */   protected static final int BELL = 8;
/*  31:    */   protected static final int FORM_FEED = 12;
/*  32:    */   protected static final int COLON = 58;
/*  33:    */   protected static final int OPEN_CURLY = 123;
/*  34:    */   protected static final int OPEN_BRACKET = 91;
/*  35:    */   protected static final int LETTER_N = 110;
/*  36:    */   protected static final int LETTER_U = 117;
/*  37:    */   protected static final int LETTER_L = 108;
/*  38:    */   protected static final int LETTER_T = 116;
/*  39:    */   protected static final int LETTER_R = 114;
/*  40:    */   protected static final int LETTER_F = 102;
/*  41:    */   protected static final int LETTER_A = 97;
/*  42:    */   protected static final int LETTER_S = 115;
/*  43: 98 */   protected final CharBuf builder = CharBuf.create(20);
/*  44:    */   protected int lastIndex;
/*  45:    */   
/*  46:    */   protected final boolean hasMore()
/*  47:    */   {
/*  48:102 */     return this.__index < this.lastIndex;
/*  49:    */   }
/*  50:    */   
/*  51:    */   protected final boolean hasCurrent()
/*  52:    */   {
/*  53:107 */     return this.__index <= this.lastIndex;
/*  54:    */   }
/*  55:    */   
/*  56:    */   protected final void skipWhiteSpaceIfNeeded()
/*  57:    */   {
/*  58:112 */     int ix = this.__index;
/*  59:115 */     if (hasCurrent()) {
/*  60:116 */       this.__currentChar = this.charArray[ix];
/*  61:    */     }
/*  62:119 */     if (this.__currentChar <= 32)
/*  63:    */     {
/*  64:120 */       ix = ByteScanner.skipWhiteSpaceFast(this.charArray, ix);
/*  65:121 */       this.__currentChar = this.charArray[ix];
/*  66:122 */       this.__index = ix;
/*  67:    */     }
/*  68:    */   }
/*  69:    */   
/*  70:    */   protected final int nextChar()
/*  71:    */   {
/*  72:    */     try
/*  73:    */     {
/*  74:133 */       if (hasMore())
/*  75:    */       {
/*  76:134 */         this.__index += 1;
/*  77:135 */         return this.__currentChar = this.charArray[this.__index];
/*  78:    */       }
/*  79:137 */       return 0;
/*  80:    */     }
/*  81:    */     catch (Exception ex)
/*  82:    */     {
/*  83:140 */       throw new JsonException(exceptionDetails("unable to advance character"), ex);
/*  84:    */     }
/*  85:    */   }
/*  86:    */   
/*  87:    */   protected String exceptionDetails(String message)
/*  88:    */   {
/*  89:145 */     return ByteScanner.errorDetails(message, this.charArray, this.__index, this.__currentChar);
/*  90:    */   }
/*  91:    */   
/*  92:    */   private static int skipWhiteSpaceFastBytes(byte[] array, int index)
/*  93:    */   {
/*  94:152 */     for (; index < array.length; index++)
/*  95:    */     {
/*  96:153 */       int c = array[index];
/*  97:154 */       if (c > 32) {
/*  98:156 */         return index;
/*  99:    */       }
/* 100:    */     }
/* 101:159 */     return index - 1;
/* 102:    */   }
/* 103:    */   
/* 104:    */   protected final void skipWhiteSpace()
/* 105:    */   {
/* 106:164 */     this.__index = skipWhiteSpaceFastBytes(this.charArray, this.__index);
/* 107:165 */     this.__currentChar = this.charArray[this.__index];
/* 108:    */   }
/* 109:    */   
/* 110:    */   private Object decode(byte[] cs)
/* 111:    */   {
/* 112:173 */     this.lastIndex = (cs.length - 1);
/* 113:174 */     this.charArray = cs;
/* 114:175 */     this.__index = 0;
/* 115:176 */     return decodeValue();
/* 116:    */   }
/* 117:    */   
/* 118:    */   public <T> T parse(Class<T> type, String str)
/* 119:    */   {
/* 120:181 */     return parse(type, str.getBytes(this.charset));
/* 121:    */   }
/* 122:    */   
/* 123:    */   public <T> T parse(Class<T> type, byte[] bytes)
/* 124:    */   {
/* 125:186 */     if ((type == Map.class) || (type == List.class) || (Typ.isBasicType(type))) {
/* 126:187 */       return decode(bytes);
/* 127:    */     }
/* 128:189 */     Map<String, Object> objectMap = (Map)decode(bytes);
/* 129:190 */     return MapObjectConversion.fromMap(objectMap, type);
/* 130:    */   }
/* 131:    */   
/* 132:    */   public Object parse(byte[] bytes)
/* 133:    */   {
/* 134:197 */     return decode(bytes);
/* 135:    */   }
/* 136:    */   
/* 137:    */   public <T> T parse(Class<T> type, InputStream input)
/* 138:    */   {
/* 139:203 */     return parse(type, IO.input(input));
/* 140:    */   }
/* 141:    */   
/* 142:    */   public <T> T parse(Class<T> type, CharSequence charSequence)
/* 143:    */   {
/* 144:207 */     return parse(type, charSequence.toString());
/* 145:    */   }
/* 146:    */   
/* 147:    */   public <T> T parse(Class<T> type, char[] chars)
/* 148:    */   {
/* 149:212 */     return parse(type, new String(chars));
/* 150:    */   }
/* 151:    */   
/* 152:    */   protected void complain(String complaint)
/* 153:    */   {
/* 154:217 */     throw new JsonException(exceptionDetails(complaint));
/* 155:    */   }
/* 156:    */   
/* 157:    */   protected final Object decodeJsonObject()
/* 158:    */   {
/* 159:225 */     if (this.__currentChar == 123) {
/* 160:226 */       this.__index += 1;
/* 161:    */     }
/* 162:229 */     LazyMap map = new LazyMap();
/* 163:231 */     for (; this.__index < this.charArray.length; this.__index += 1)
/* 164:    */     {
/* 165:233 */       skipWhiteSpaceIfNeeded();
/* 166:236 */       if (this.__currentChar == 34)
/* 167:    */       {
/* 168:238 */         String key = decodeString();
/* 169:241 */         if (internKeys)
/* 170:    */         {
/* 171:242 */           String keyPrime = (String)internedKeysCache.get(key);
/* 172:243 */           if (keyPrime == null)
/* 173:    */           {
/* 174:244 */             key = key.intern();
/* 175:245 */             internedKeysCache.put(key, key);
/* 176:    */           }
/* 177:    */           else
/* 178:    */           {
/* 179:247 */             key = keyPrime;
/* 180:    */           }
/* 181:    */         }
/* 182:251 */         skipWhiteSpaceIfNeeded();
/* 183:253 */         if (this.__currentChar != 58) {
/* 184:255 */           complain("expecting current character to be " + charDescription(this.__currentChar) + "\n");
/* 185:    */         }
/* 186:257 */         this.__index += 1;
/* 187:    */         
/* 188:259 */         skipWhiteSpaceIfNeeded();
/* 189:    */         
/* 190:261 */         Object value = decodeValue();
/* 191:    */         
/* 192:263 */         skipWhiteSpaceIfNeeded();
/* 193:264 */         map.put(key, value);
/* 194:    */       }
/* 195:268 */       if (this.__currentChar == 125)
/* 196:    */       {
/* 197:269 */         this.__index += 1;
/* 198:270 */         break;
/* 199:    */       }
/* 200:271 */       if (this.__currentChar != 44) {
/* 201:274 */         complain("expecting '}' or ',' but got current char " + charDescription(this.__currentChar));
/* 202:    */       }
/* 203:    */     }
/* 204:281 */     return map;
/* 205:    */   }
/* 206:    */   
/* 207:    */   protected final Object decodeValue()
/* 208:    */   {
/* 209:287 */     Object value = null;
/* 210:290 */     for (; this.__index < this.charArray.length; this.__index += 1)
/* 211:    */     {
/* 212:291 */       this.__currentChar = this.charArray[this.__index];
/* 213:294 */       switch (this.__currentChar)
/* 214:    */       {
/* 215:    */       case 10: 
/* 216:    */         break;
/* 217:    */       case 8: 
/* 218:    */       case 9: 
/* 219:    */       case 12: 
/* 220:    */       case 13: 
/* 221:    */       case 32: 
/* 222:    */         break;
/* 223:    */       case 34: 
/* 224:306 */         value = decodeString();
/* 225:307 */         break;
/* 226:    */       case 116: 
/* 227:311 */         value = Boolean.valueOf(decodeTrue());
/* 228:312 */         break;
/* 229:    */       case 102: 
/* 230:315 */         value = Boolean.valueOf(decodeFalse());
/* 231:316 */         break;
/* 232:    */       case 110: 
/* 233:319 */         value = decodeNull();
/* 234:320 */         break;
/* 235:    */       case 91: 
/* 236:323 */         value = decodeJsonArray();
/* 237:324 */         break;
/* 238:    */       case 123: 
/* 239:327 */         value = decodeJsonObject();
/* 240:328 */         break;
/* 241:    */       case 48: 
/* 242:    */       case 49: 
/* 243:    */       case 50: 
/* 244:    */       case 51: 
/* 245:    */       case 52: 
/* 246:    */       case 53: 
/* 247:    */       case 54: 
/* 248:    */       case 55: 
/* 249:    */       case 56: 
/* 250:    */       case 57: 
/* 251:340 */         value = decodeNumber();
/* 252:341 */         break;
/* 253:    */       case 45: 
/* 254:344 */         value = decodeNumber();
/* 255:345 */         break;
/* 256:    */       default: 
/* 257:348 */         throw new JsonException(exceptionDetails("Unable to determine the current character, it is not a string, number, array, or object"));
/* 258:    */       }
/* 259:    */     }
/* 260:354 */     return value;
/* 261:    */   }
/* 262:    */   
/* 263:359 */   int[] endIndex = new int[1];
/* 264:    */   
/* 265:    */   private final Object decodeNumber()
/* 266:    */   {
/* 267:363 */     Number num = ByteScanner.parseJsonNumber(this.charArray, this.__index, this.charArray.length, this.endIndex);
/* 268:364 */     this.__index = this.endIndex[0];
/* 269:    */     
/* 270:366 */     return num;
/* 271:    */   }
/* 272:    */   
/* 273:371 */   protected static final byte[] NULL = Byt.bytes("null");
/* 274:    */   
/* 275:    */   protected final Object decodeNull()
/* 276:    */   {
/* 277:375 */     if ((this.__index + NULL.length <= this.charArray.length) && 
/* 278:376 */       (this.charArray[this.__index] == 110) && (this.charArray[(++this.__index)] == 117) && (this.charArray[(++this.__index)] == 108) && (this.charArray[(++this.__index)] == 108))
/* 279:    */     {
/* 280:380 */       nextChar();
/* 281:381 */       return null;
/* 282:    */     }
/* 283:384 */     throw new JsonException(exceptionDetails("null not parsed properly"));
/* 284:    */   }
/* 285:    */   
/* 286:387 */   protected static final byte[] TRUE = Byt.bytes("true");
/* 287:    */   
/* 288:    */   protected final boolean decodeTrue()
/* 289:    */   {
/* 290:391 */     if ((this.__index + TRUE.length <= this.charArray.length) && 
/* 291:392 */       (this.charArray[this.__index] == 116) && (this.charArray[(++this.__index)] == 114) && (this.charArray[(++this.__index)] == 117) && (this.charArray[(++this.__index)] == 101))
/* 292:    */     {
/* 293:397 */       nextChar();
/* 294:398 */       return true;
/* 295:    */     }
/* 296:403 */     throw new JsonException(exceptionDetails("true not parsed properly"));
/* 297:    */   }
/* 298:    */   
/* 299:407 */   protected static final byte[] FALSE = Byt.bytes("false");
/* 300:    */   
/* 301:    */   protected final boolean decodeFalse()
/* 302:    */   {
/* 303:411 */     if ((this.__index + FALSE.length <= this.charArray.length) && 
/* 304:412 */       (this.charArray[this.__index] == 102) && (this.charArray[(++this.__index)] == 97) && (this.charArray[(++this.__index)] == 108) && (this.charArray[(++this.__index)] == 115) && (this.charArray[(++this.__index)] == 101))
/* 305:    */     {
/* 306:417 */       nextChar();
/* 307:418 */       return false;
/* 308:    */     }
/* 309:421 */     throw new JsonException(exceptionDetails("false not parsed properly"));
/* 310:    */   }
/* 311:    */   
/* 312:    */   protected abstract String decodeString();
/* 313:    */   
/* 314:    */   protected final List decodeJsonArray()
/* 315:    */   {
/* 316:433 */     ArrayList<Object> list = null;
/* 317:    */     
/* 318:435 */     boolean foundEnd = false;
/* 319:436 */     byte[] charArray = this.charArray;
/* 320:    */     try
/* 321:    */     {
/* 322:439 */       if (this.__currentChar == 91) {
/* 323:440 */         this.__index += 1;
/* 324:    */       }
/* 325:444 */       skipWhiteSpaceIfNeeded();
/* 326:448 */       if (this.__currentChar == 93)
/* 327:    */       {
/* 328:449 */         this.__index += 1;
/* 329:450 */         return Collections.EMPTY_LIST;
/* 330:    */       }
/* 331:453 */       list = new ArrayList();
/* 332:459 */       while (hasMore())
/* 333:    */       {
/* 334:461 */         Object arrayItem = decodeValue();
/* 335:    */         
/* 336:463 */         list.add(arrayItem);
/* 337:    */         for (;;)
/* 338:    */         {
/* 339:467 */           c = charArray[this.__index];
/* 340:468 */           if (c == 44)
/* 341:    */           {
/* 342:469 */             this.__index += 1;
/* 343:470 */             break;
/* 344:    */           }
/* 345:471 */           if (c == 93)
/* 346:    */           {
/* 347:472 */             foundEnd = true;
/* 348:473 */             this.__index += 1;
/* 349:    */             break label242;
/* 350:    */           }
/* 351:475 */           if (c > 32) {
/* 352:    */             break label153;
/* 353:    */           }
/* 354:476 */           this.__index += 1;
/* 355:    */         }
/* 356:    */         label153:
/* 357:486 */         int c = charArray[this.__index];
/* 358:488 */         if (c == 44)
/* 359:    */         {
/* 360:489 */           this.__index += 1;
/* 361:    */         }
/* 362:    */         else
/* 363:    */         {
/* 364:491 */           if (c == 93)
/* 365:    */           {
/* 366:492 */             this.__index += 1;
/* 367:493 */             foundEnd = true;
/* 368:494 */             break;
/* 369:    */           }
/* 370:497 */           String charString = charDescription(c);
/* 371:    */           
/* 372:499 */           complain(String.format("expecting a ',' or a ']',  but got \nthe current character of  %s  on array index of %s \n", new Object[] { charString, Integer.valueOf(list.size()) }));
/* 373:    */         }
/* 374:    */       }
/* 375:    */     }
/* 376:    */     catch (Exception ex)
/* 377:    */     {
/* 378:    */       label242:
/* 379:509 */       if ((ex instanceof JsonException))
/* 380:    */       {
/* 381:510 */         JsonException jsonException = (JsonException)ex;
/* 382:511 */         throw jsonException;
/* 383:    */       }
/* 384:513 */       throw new JsonException(exceptionDetails("issue parsing JSON array"), ex);
/* 385:    */     }
/* 386:515 */     if (!foundEnd) {
/* 387:516 */       complain("Did not find end of Json Array");
/* 388:    */     }
/* 389:518 */     return list;
/* 390:    */   }
/* 391:    */   
/* 392:    */   protected final List decodeJsonArrayOLD()
/* 393:    */   {
/* 394:523 */     if (this.__currentChar == 91) {
/* 395:524 */       nextChar();
/* 396:    */     }
/* 397:527 */     skipWhiteSpace();
/* 398:530 */     if (this.__currentChar == 93)
/* 399:    */     {
/* 400:531 */       nextChar();
/* 401:532 */       return Collections.EMPTY_LIST;
/* 402:    */     }
/* 403:536 */     ArrayList<Object> list = new ArrayList();
/* 404:    */     
/* 405:538 */     boolean foundEnd = false;
/* 406:    */     
/* 407:    */ 
/* 408:    */ 
/* 409:542 */     int arrayIndex = 0;
/* 410:    */     try
/* 411:    */     {
/* 412:546 */       while (hasMore())
/* 413:    */       {
/* 414:547 */         skipWhiteSpace();
/* 415:    */         
/* 416:549 */         Object arrayItem = decodeValue();
/* 417:    */         
/* 418:551 */         list.add(arrayItem);
/* 419:    */         
/* 420:553 */         arrayIndex++;
/* 421:    */         
/* 422:555 */         this.__currentChar = this.charArray[this.__index];
/* 423:558 */         if (this.__currentChar == 44)
/* 424:    */         {
/* 425:559 */           nextChar();
/* 426:    */         }
/* 427:    */         else
/* 428:    */         {
/* 429:561 */           if (this.__currentChar == 93)
/* 430:    */           {
/* 431:562 */             nextChar();
/* 432:563 */             foundEnd = true;
/* 433:564 */             break;
/* 434:    */           }
/* 435:567 */           skipWhiteSpace();
/* 436:570 */           if (this.__currentChar == 44)
/* 437:    */           {
/* 438:571 */             nextChar();
/* 439:    */           }
/* 440:    */           else
/* 441:    */           {
/* 442:573 */             if (this.__currentChar == 93)
/* 443:    */             {
/* 444:574 */               nextChar();
/* 445:575 */               foundEnd = true;
/* 446:576 */               break;
/* 447:    */             }
/* 448:578 */             String charString = charDescription(this.__currentChar);
/* 449:    */             
/* 450:580 */             complain(String.format("expecting a ',' or a ']',  but got \nthe current character of  %s  on array index of %s \n", new Object[] { charString, Integer.valueOf(arrayIndex) }));
/* 451:    */           }
/* 452:    */         }
/* 453:    */       }
/* 454:    */     }
/* 455:    */     catch (Exception ex)
/* 456:    */     {
/* 457:589 */       throw new JsonException(exceptionDetails(ex.getMessage()), ex);
/* 458:    */     }
/* 459:592 */     if (!foundEnd) {
/* 460:593 */       complain("No end bracket found for JSON Array");
/* 461:    */     }
/* 462:595 */     return list;
/* 463:    */   }
/* 464:    */   
/* 465:    */   public <T> T parseDirect(Class<T> type, byte[] value)
/* 466:    */   {
/* 467:600 */     return parse(type, value);
/* 468:    */   }
/* 469:    */   
/* 470:    */   public <T> T parseAsStream(Class<T> type, byte[] value)
/* 471:    */   {
/* 472:604 */     return parse(type, new InMemoryInputStream(value));
/* 473:    */   }
/* 474:    */   
/* 475:    */   public <T> T parse(Class<T> type, byte[] bytes, Charset charset)
/* 476:    */   {
/* 477:610 */     return parse(type, bytes);
/* 478:    */   }
/* 479:    */   
/* 480:    */   public <T> T parseFile(Class<T> type, String fileName)
/* 481:    */   {
/* 482:615 */     return parse(type, IO.input(fileName));
/* 483:    */   }
/* 484:    */   
/* 485:    */   public Object parse(char[] chars)
/* 486:    */   {
/* 487:622 */     return parse(new String(chars));
/* 488:    */   }
/* 489:    */   
/* 490:    */   public Object parse(String string)
/* 491:    */   {
/* 492:627 */     return parse(string.getBytes(this.charset));
/* 493:    */   }
/* 494:    */   
/* 495:    */   public Object parse(byte[] bytes, Charset charset)
/* 496:    */   {
/* 497:634 */     return parse(bytes);
/* 498:    */   }
/* 499:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.json.implementation.JsonBaseByteArrayParser
 * JD-Core Version:    0.7.0.1
 */