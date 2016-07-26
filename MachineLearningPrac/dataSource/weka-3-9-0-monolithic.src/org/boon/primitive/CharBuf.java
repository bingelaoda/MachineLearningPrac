/*    1:     */ package org.boon.primitive;
/*    2:     */ 
/*    3:     */ import java.io.IOException;
/*    4:     */ import java.io.PrintStream;
/*    5:     */ import java.io.PrintWriter;
/*    6:     */ import java.io.Writer;
/*    7:     */ import java.math.BigDecimal;
/*    8:     */ import java.math.BigInteger;
/*    9:     */ import java.nio.charset.StandardCharsets;
/*   10:     */ import java.util.Arrays;
/*   11:     */ import java.util.Collection;
/*   12:     */ import java.util.Currency;
/*   13:     */ import java.util.Date;
/*   14:     */ import java.util.Iterator;
/*   15:     */ import java.util.Locale;
/*   16:     */ import java.util.Map;
/*   17:     */ import java.util.Map.Entry;
/*   18:     */ import java.util.Set;
/*   19:     */ import org.boon.Exceptions;
/*   20:     */ import org.boon.Lists;
/*   21:     */ import org.boon.Maps;
/*   22:     */ import org.boon.Str;
/*   23:     */ import org.boon.cache.Cache;
/*   24:     */ import org.boon.cache.CacheType;
/*   25:     */ import org.boon.cache.SimpleCache;
/*   26:     */ import org.boon.core.Conversions;
/*   27:     */ import org.boon.core.Dates;
/*   28:     */ import org.boon.core.TypeType;
/*   29:     */ import org.boon.core.reflection.FastStringUtils;
/*   30:     */ import org.boon.core.reflection.Mapper;
/*   31:     */ 
/*   32:     */ public class CharBuf
/*   33:     */   extends PrintWriter
/*   34:     */   implements CharSequence
/*   35:     */ {
/*   36:  66 */   protected int capacity = 16;
/*   37:  67 */   protected int location = 0;
/*   38:     */   protected char[] buffer;
/*   39:     */   private Cache<Integer, char[]> icache;
/*   40:     */   
/*   41:     */   public CharBuf(char[] buffer)
/*   42:     */   {
/*   43:  73 */     super(writer());
/*   44:  74 */     this.buffer = buffer;
/*   45:  75 */     this.capacity = buffer.length;
/*   46:     */   }
/*   47:     */   
/*   48:     */   public CharBuf(byte[] bytes)
/*   49:     */   {
/*   50:  80 */     super(writer());
/*   51:     */     
/*   52:  82 */     String str = new String(bytes, StandardCharsets.UTF_8);
/*   53:  83 */     this.buffer = FastStringUtils.toCharArray(str);
/*   54:  84 */     this.location = this.buffer.length;
/*   55:  85 */     this.capacity = this.buffer.length;
/*   56:     */   }
/*   57:     */   
/*   58:     */   public static CharBuf createExact(int capacity)
/*   59:     */   {
/*   60:  90 */     new CharBuf(capacity)
/*   61:     */     {
/*   62:     */       public CharBuf add(char[] chars)
/*   63:     */       {
/*   64:  92 */         Chr._idx(this.buffer, this.location, chars);
/*   65:  93 */         this.location += chars.length;
/*   66:  94 */         return this;
/*   67:     */       }
/*   68:     */     };
/*   69:     */   }
/*   70:     */   
/*   71:     */   public static CharBuf create(int capacity)
/*   72:     */   {
/*   73: 100 */     return new CharBuf(capacity);
/*   74:     */   }
/*   75:     */   
/*   76:     */   public static CharBuf createCharBuf(int capacity)
/*   77:     */   {
/*   78: 104 */     return new CharBuf(capacity);
/*   79:     */   }
/*   80:     */   
/*   81:     */   public static CharBuf createCharBuf()
/*   82:     */   {
/*   83: 109 */     return new CharBuf(100);
/*   84:     */   }
/*   85:     */   
/*   86:     */   public static CharBuf create(char[] buffer)
/*   87:     */   {
/*   88: 113 */     return new CharBuf(buffer);
/*   89:     */   }
/*   90:     */   
/*   91:     */   public static CharBuf createFromUTF8Bytes(byte[] buffer)
/*   92:     */   {
/*   93: 117 */     return new CharBuf(buffer);
/*   94:     */   }
/*   95:     */   
/*   96:     */   protected CharBuf(int capacity)
/*   97:     */   {
/*   98: 122 */     super(writer());
/*   99: 123 */     this.capacity = capacity;
/*  100: 124 */     init();
/*  101:     */   }
/*  102:     */   
/*  103:     */   protected CharBuf()
/*  104:     */   {
/*  105: 129 */     super(writer());
/*  106: 130 */     init();
/*  107:     */   }
/*  108:     */   
/*  109:     */   public void write(char[] cbuf, int off, int len)
/*  110:     */   {
/*  111: 136 */     if ((off == 0) && (cbuf.length == len))
/*  112:     */     {
/*  113: 137 */       add(cbuf);
/*  114:     */     }
/*  115:     */     else
/*  116:     */     {
/*  117: 139 */       char[] buffer = Arrays.copyOfRange(cbuf, off, off + len);
/*  118: 140 */       add(buffer);
/*  119:     */     }
/*  120:     */   }
/*  121:     */   
/*  122:     */   public void init()
/*  123:     */   {
/*  124: 154 */     this.buffer = new char[this.capacity];
/*  125:     */   }
/*  126:     */   
/*  127:     */   public final CharBuf add(Object str)
/*  128:     */   {
/*  129: 159 */     add(FastStringUtils.toCharArray(Str.str(str)));
/*  130: 160 */     return this;
/*  131:     */   }
/*  132:     */   
/*  133:     */   public final CharBuf add(String str)
/*  134:     */   {
/*  135: 164 */     add(FastStringUtils.toCharArray(str));
/*  136: 165 */     return this;
/*  137:     */   }
/*  138:     */   
/*  139:     */   public final CharBuf add(CharSequence str)
/*  140:     */   {
/*  141: 170 */     add(FastStringUtils.toCharArray(str));
/*  142: 171 */     return this;
/*  143:     */   }
/*  144:     */   
/*  145:     */   public final CharBuf addString(String str)
/*  146:     */   {
/*  147: 175 */     add(FastStringUtils.toCharArray(str));
/*  148: 176 */     return this;
/*  149:     */   }
/*  150:     */   
/*  151:     */   public final CharBuf addObject(Object object)
/*  152:     */   {
/*  153: 180 */     String str = object.toString();
/*  154: 181 */     addString(str);
/*  155: 182 */     return this;
/*  156:     */   }
/*  157:     */   
/*  158:     */   public final CharBuf add(int i)
/*  159:     */   {
/*  160: 187 */     add(Integer.toString(i));
/*  161: 188 */     return this;
/*  162:     */   }
/*  163:     */   
/*  164:     */   public final CharBuf addInt(int i)
/*  165:     */   {
/*  166: 195 */     switch (i)
/*  167:     */     {
/*  168:     */     case 0: 
/*  169: 197 */       addChar('0');
/*  170: 198 */       return this;
/*  171:     */     case 1: 
/*  172: 200 */       addChar('1');
/*  173: 201 */       return this;
/*  174:     */     case -1: 
/*  175: 203 */       addChar('-');
/*  176: 204 */       addChar('1');
/*  177: 205 */       return this;
/*  178:     */     }
/*  179: 208 */     addInt(Integer.valueOf(i));
/*  180: 209 */     return this;
/*  181:     */   }
/*  182:     */   
/*  183:     */   public final CharBuf addInt(Integer key)
/*  184:     */   {
/*  185: 214 */     if (this.icache == null) {
/*  186: 215 */       this.icache = new SimpleCache(1000, CacheType.LRU);
/*  187:     */     }
/*  188: 217 */     char[] chars = (char[])this.icache.get(key);
/*  189: 219 */     if (chars == null)
/*  190:     */     {
/*  191: 220 */       String str = Integer.toString(key.intValue());
/*  192: 221 */       chars = FastStringUtils.toCharArray(str);
/*  193: 222 */       this.icache.put(key, chars);
/*  194:     */     }
/*  195: 225 */     addChars(chars);
/*  196: 226 */     return this;
/*  197:     */   }
/*  198:     */   
/*  199: 230 */   final char[] trueChars = "true".toCharArray();
/*  200: 231 */   final char[] falseChars = "false".toCharArray();
/*  201:     */   private Cache<Double, char[]> dcache;
/*  202:     */   private Cache<Float, char[]> fcache;
/*  203:     */   int jsonControlCount;
/*  204:     */   
/*  205:     */   public final CharBuf add(boolean b)
/*  206:     */   {
/*  207: 234 */     addChars(b ? this.trueChars : this.falseChars);
/*  208: 235 */     return this;
/*  209:     */   }
/*  210:     */   
/*  211:     */   public final CharBuf addBoolean(boolean b)
/*  212:     */   {
/*  213: 241 */     add(Boolean.toString(b));
/*  214: 242 */     return this;
/*  215:     */   }
/*  216:     */   
/*  217:     */   public final CharBuf add(byte i)
/*  218:     */   {
/*  219: 247 */     add(Byte.toString(i));
/*  220: 248 */     return this;
/*  221:     */   }
/*  222:     */   
/*  223:     */   public final CharBuf addByte(byte i)
/*  224:     */   {
/*  225: 253 */     addInt(i);
/*  226: 254 */     return this;
/*  227:     */   }
/*  228:     */   
/*  229:     */   public final CharBuf add(short i)
/*  230:     */   {
/*  231: 260 */     add(Short.toString(i));
/*  232: 261 */     return this;
/*  233:     */   }
/*  234:     */   
/*  235:     */   public final CharBuf addShort(short i)
/*  236:     */   {
/*  237: 267 */     addInt(i);
/*  238: 268 */     return this;
/*  239:     */   }
/*  240:     */   
/*  241:     */   public final CharBuf add(long l)
/*  242:     */   {
/*  243: 272 */     add(Long.toString(l));
/*  244: 273 */     return this;
/*  245:     */   }
/*  246:     */   
/*  247:     */   public final CharBuf add(double d)
/*  248:     */   {
/*  249: 279 */     add(Double.toString(d));
/*  250: 280 */     return this;
/*  251:     */   }
/*  252:     */   
/*  253:     */   public final CharBuf addDouble(double d)
/*  254:     */   {
/*  255: 289 */     addDouble(Double.valueOf(d));
/*  256: 290 */     return this;
/*  257:     */   }
/*  258:     */   
/*  259:     */   public final CharBuf addDouble(Double key)
/*  260:     */   {
/*  261: 295 */     if (this.dcache == null) {
/*  262: 296 */       this.dcache = new SimpleCache(100, CacheType.LRU);
/*  263:     */     }
/*  264: 298 */     char[] chars = (char[])this.dcache.get(key);
/*  265: 300 */     if (chars == null)
/*  266:     */     {
/*  267: 301 */       String str = Double.toString(key.doubleValue());
/*  268: 302 */       chars = FastStringUtils.toCharArray(str);
/*  269: 303 */       this.dcache.put(key, chars);
/*  270:     */     }
/*  271: 306 */     add(chars);
/*  272: 307 */     return this;
/*  273:     */   }
/*  274:     */   
/*  275:     */   public final CharBuf add(float d)
/*  276:     */   {
/*  277: 312 */     add(Float.toString(d));
/*  278: 313 */     return this;
/*  279:     */   }
/*  280:     */   
/*  281:     */   public final CharBuf addFloat(float d)
/*  282:     */   {
/*  283: 320 */     addFloat(Float.valueOf(d));
/*  284: 321 */     return this;
/*  285:     */   }
/*  286:     */   
/*  287:     */   public final CharBuf addFloat(Float key)
/*  288:     */   {
/*  289: 327 */     if (this.fcache == null) {
/*  290: 328 */       this.fcache = new SimpleCache(100, CacheType.LRU);
/*  291:     */     }
/*  292: 330 */     char[] chars = (char[])this.fcache.get(key);
/*  293: 332 */     if (chars == null)
/*  294:     */     {
/*  295: 333 */       String str = Float.toString(key.floatValue());
/*  296: 334 */       chars = FastStringUtils.toCharArray(str);
/*  297: 335 */       this.fcache.put(key, chars);
/*  298:     */     }
/*  299: 338 */     add(chars);
/*  300:     */     
/*  301: 340 */     return this;
/*  302:     */   }
/*  303:     */   
/*  304:     */   public final CharBuf addChar(byte i)
/*  305:     */   {
/*  306: 344 */     add((char)i);
/*  307: 345 */     return this;
/*  308:     */   }
/*  309:     */   
/*  310:     */   public final CharBuf addChar(int i)
/*  311:     */   {
/*  312: 350 */     add((char)i);
/*  313: 351 */     return this;
/*  314:     */   }
/*  315:     */   
/*  316:     */   public final CharBuf addChar(short i)
/*  317:     */   {
/*  318: 356 */     add((char)i);
/*  319: 357 */     return this;
/*  320:     */   }
/*  321:     */   
/*  322:     */   public final CharBuf addChar(char ch)
/*  323:     */   {
/*  324: 364 */     if (1 + this.location > this.capacity)
/*  325:     */     {
/*  326: 365 */       this.buffer = Chr.grow(this.buffer);
/*  327: 366 */       this.capacity = this.buffer.length;
/*  328:     */     }
/*  329: 370 */     this.buffer[this.location] = ch;
/*  330: 371 */     this.location += 1;
/*  331:     */     
/*  332: 373 */     return this;
/*  333:     */   }
/*  334:     */   
/*  335:     */   public final CharBuf add(char ch)
/*  336:     */   {
/*  337: 378 */     if (1 + this.location > this.capacity)
/*  338:     */     {
/*  339: 379 */       this.buffer = Chr.grow(this.buffer);
/*  340: 380 */       this.capacity = this.buffer.length;
/*  341:     */     }
/*  342: 384 */     this.buffer[this.location] = ch;
/*  343: 385 */     this.location += 1;
/*  344:     */     
/*  345: 387 */     return this;
/*  346:     */   }
/*  347:     */   
/*  348:     */   public CharBuf addLine(String str)
/*  349:     */   {
/*  350: 391 */     add(FastStringUtils.toCharArray(str));
/*  351: 392 */     add('\n');
/*  352: 393 */     return this;
/*  353:     */   }
/*  354:     */   
/*  355:     */   public CharBuf addLine(Object str)
/*  356:     */   {
/*  357: 397 */     add(FastStringUtils.toCharArray(Str.str(str)));
/*  358:     */     
/*  359: 399 */     add('\n');
/*  360: 400 */     return this;
/*  361:     */   }
/*  362:     */   
/*  363:     */   public CharBuf addLine()
/*  364:     */   {
/*  365: 406 */     add('\n');
/*  366: 407 */     return this;
/*  367:     */   }
/*  368:     */   
/*  369:     */   public CharBuf addLine(char[] chars)
/*  370:     */   {
/*  371: 412 */     add(chars);
/*  372: 413 */     add('\n');
/*  373: 414 */     return this;
/*  374:     */   }
/*  375:     */   
/*  376:     */   public CharBuf addLine(CharSequence str)
/*  377:     */   {
/*  378: 419 */     add(str.toString());
/*  379: 420 */     add('\n');
/*  380: 421 */     return this;
/*  381:     */   }
/*  382:     */   
/*  383:     */   public CharBuf add(char[] chars)
/*  384:     */   {
/*  385: 425 */     if (chars.length + this.location > this.capacity)
/*  386:     */     {
/*  387: 426 */       this.buffer = Chr.grow(this.buffer, this.buffer.length * 2 + chars.length);
/*  388: 427 */       this.capacity = this.buffer.length;
/*  389:     */     }
/*  390: 430 */     Chr._idx(this.buffer, this.location, chars);
/*  391: 431 */     this.location += chars.length;
/*  392: 432 */     return this;
/*  393:     */   }
/*  394:     */   
/*  395:     */   public final CharBuf addChars(char[] chars)
/*  396:     */   {
/*  397: 438 */     if (chars.length + this.location > this.capacity)
/*  398:     */     {
/*  399: 439 */       this.buffer = Chr.grow(this.buffer, this.buffer.length * 2 + chars.length);
/*  400: 440 */       this.capacity = this.buffer.length;
/*  401:     */     }
/*  402: 443 */     System.arraycopy(chars, 0, this.buffer, this.location, chars.length);
/*  403: 444 */     this.location += chars.length;
/*  404: 445 */     return this;
/*  405:     */   }
/*  406:     */   
/*  407:     */   public final CharBuf addQuoted(char[] chars)
/*  408:     */   {
/*  409: 452 */     int sizeNeeded = chars.length + 2 + this.location;
/*  410: 453 */     if (sizeNeeded > this.capacity)
/*  411:     */     {
/*  412: 454 */       this.buffer = Chr.grow(this.buffer, sizeNeeded * 2);
/*  413: 455 */       this.capacity = this.buffer.length;
/*  414:     */     }
/*  415: 457 */     this.buffer[this.location] = '"';
/*  416: 458 */     this.location += 1;
/*  417:     */     
/*  418: 460 */     System.arraycopy(chars, 0, this.buffer, this.location, chars.length);
/*  419: 461 */     this.location += chars.length;
/*  420: 462 */     this.buffer[this.location] = '"';
/*  421: 463 */     this.location += 1;
/*  422:     */     
/*  423: 465 */     return this;
/*  424:     */   }
/*  425:     */   
/*  426:     */   public final CharBuf asJsonString(String jsonString, boolean asciiOnly)
/*  427:     */   {
/*  428: 469 */     char[] charArray = FastStringUtils.toCharArray(jsonString);
/*  429: 470 */     return addJsonEscapedString(charArray, asciiOnly);
/*  430:     */   }
/*  431:     */   
/*  432:     */   public final CharBuf asJsonString(String jsonString)
/*  433:     */   {
/*  434: 477 */     return asJsonString(jsonString, false);
/*  435:     */   }
/*  436:     */   
/*  437:     */   private static boolean isJSONEscapeOrAsciiControlOrUnicode(int c)
/*  438:     */   {
/*  439: 483 */     return (c < 32) || (c == 34) || (c == 92) || (c > 127);
/*  440:     */   }
/*  441:     */   
/*  442:     */   private static boolean isJSONEscapeOrAsciiControl(int c)
/*  443:     */   {
/*  444: 489 */     return (c < 32) || (c == 34) || (c == 92);
/*  445:     */   }
/*  446:     */   
/*  447:     */   private boolean hasAnyAsciiControlOrUnicodeChars(char[] charArray)
/*  448:     */   {
/*  449: 496 */     int index = 0;
/*  450:     */     for (;;)
/*  451:     */     {
/*  452: 499 */       char c = charArray[index];
/*  453: 500 */       if (isJSONEscapeOrAsciiControlOrUnicode(c)) {
/*  454: 501 */         this.jsonControlCount += 1;
/*  455:     */       }
/*  456: 504 */       index++;
/*  457: 504 */       if (index >= charArray.length) {
/*  458:     */         break;
/*  459:     */       }
/*  460:     */     }
/*  461: 507 */     return this.jsonControlCount > 0;
/*  462:     */   }
/*  463:     */   
/*  464:     */   private final boolean hasAnyAsciiControl(char[] charArray)
/*  465:     */   {
/*  466: 513 */     int index = 0;
/*  467:     */     for (;;)
/*  468:     */     {
/*  469: 516 */       char c = charArray[index];
/*  470: 517 */       if (isJSONEscapeOrAsciiControl(c)) {
/*  471: 518 */         this.jsonControlCount += 1;
/*  472:     */       }
/*  473: 521 */       index++;
/*  474: 521 */       if (index >= charArray.length) {
/*  475:     */         break;
/*  476:     */       }
/*  477:     */     }
/*  478: 524 */     return this.jsonControlCount > 0;
/*  479:     */   }
/*  480:     */   
/*  481:     */   public final CharBuf addJsonEscapedString(char[] charArray)
/*  482:     */   {
/*  483: 530 */     return addJsonEscapedString(charArray, false);
/*  484:     */   }
/*  485:     */   
/*  486:     */   public final CharBuf addJsonEscapedString(char[] charArray, boolean asciiOnly)
/*  487:     */   {
/*  488: 535 */     this.jsonControlCount = 0;
/*  489: 538 */     if (!asciiOnly)
/*  490:     */     {
/*  491: 540 */       if ((charArray.length > 0) && (hasAnyAsciiControl(charArray))) {
/*  492: 541 */         return doAddJsonEscapedString(charArray);
/*  493:     */       }
/*  494: 543 */       return addQuoted(charArray);
/*  495:     */     }
/*  496: 547 */     if ((charArray.length > 0) && (hasAnyAsciiControlOrUnicodeChars(charArray))) {
/*  497: 548 */       return doAddJsonEscapedStringEscapeUnicode(charArray);
/*  498:     */     }
/*  499: 550 */     return addQuoted(charArray);
/*  500:     */   }
/*  501:     */   
/*  502: 557 */   final byte[] encoded = new byte[2];
/*  503: 559 */   final byte[] charTo = new byte[2];
/*  504:     */   
/*  505:     */   private final CharBuf doAddJsonEscapedStringEscapeUnicode(char[] charArray)
/*  506:     */   {
/*  507: 566 */     char[] _buffer = this.buffer;
/*  508: 567 */     int _location = this.location;
/*  509:     */     
/*  510: 569 */     byte[] _encoded = this.encoded;
/*  511:     */     
/*  512: 571 */     byte[] _charTo = this.charTo;
/*  513:     */     
/*  514: 573 */     int ensureThisMuch = charArray.length + (this.jsonControlCount + 2) * 5;
/*  515:     */     
/*  516: 575 */     int sizeNeeded = ensureThisMuch + _location;
/*  517: 576 */     if (sizeNeeded > this.capacity)
/*  518:     */     {
/*  519: 578 */       int growBy = _buffer.length * 2 < sizeNeeded ? sizeNeeded : _buffer.length * 2;
/*  520: 579 */       _buffer = Chr.grow(_buffer, growBy);
/*  521: 580 */       this.capacity = _buffer.length;
/*  522:     */     }
/*  523: 586 */     _buffer[_location] = '"';
/*  524: 587 */     _location++;
/*  525:     */     
/*  526: 589 */     int index = 0;
/*  527:     */     for (;;)
/*  528:     */     {
/*  529: 592 */       char c = charArray[index];
/*  530: 595 */       if (isJSONEscapeOrAsciiControlOrUnicode(c))
/*  531:     */       {
/*  532: 597 */         switch (c)
/*  533:     */         {
/*  534:     */         case '"': 
/*  535: 599 */           _buffer[_location] = '\\';
/*  536: 600 */           _location++;
/*  537: 601 */           _buffer[_location] = '"';
/*  538: 602 */           _location++;
/*  539: 603 */           break;
/*  540:     */         case '\\': 
/*  541: 605 */           _buffer[_location] = '\\';
/*  542: 606 */           _location++;
/*  543: 607 */           _buffer[_location] = '\\';
/*  544: 608 */           _location++;
/*  545: 609 */           break;
/*  546:     */         case '\b': 
/*  547: 611 */           _buffer[_location] = '\\';
/*  548: 612 */           _location++;
/*  549: 613 */           _buffer[_location] = 'b';
/*  550: 614 */           _location++;
/*  551: 615 */           break;
/*  552:     */         case '\f': 
/*  553: 617 */           _buffer[_location] = '\\';
/*  554: 618 */           _location++;
/*  555: 619 */           _buffer[_location] = 'f';
/*  556: 620 */           _location++;
/*  557: 621 */           break;
/*  558:     */         case '\n': 
/*  559: 623 */           _buffer[_location] = '\\';
/*  560: 624 */           _location++;
/*  561: 625 */           _buffer[_location] = 'n';
/*  562: 626 */           _location++;
/*  563: 627 */           break;
/*  564:     */         case '\r': 
/*  565: 629 */           _buffer[_location] = '\\';
/*  566: 630 */           _location++;
/*  567: 631 */           _buffer[_location] = 'r';
/*  568: 632 */           _location++;
/*  569: 633 */           break;
/*  570:     */         case '\t': 
/*  571: 636 */           _buffer[_location] = '\\';
/*  572: 637 */           _location++;
/*  573: 638 */           _buffer[_location] = 't';
/*  574: 639 */           _location++;
/*  575: 640 */           break;
/*  576:     */         default: 
/*  577: 644 */           _buffer[_location] = '\\';
/*  578: 645 */           _location++;
/*  579: 646 */           _buffer[_location] = 'u';
/*  580: 647 */           _location++;
/*  581: 648 */           if (c <= 'ÿ')
/*  582:     */           {
/*  583: 649 */             _buffer[_location] = '0';
/*  584: 650 */             _location++;
/*  585: 651 */             _buffer[_location] = '0';
/*  586: 652 */             _location++;
/*  587: 653 */             ByteScanner.encodeByteIntoTwoAsciiCharBytes(c, _encoded);
/*  588: 654 */             for (int b : _encoded)
/*  589:     */             {
/*  590: 655 */               _buffer[_location] = ((char)b);
/*  591: 656 */               _location++;
/*  592:     */             }
/*  593:     */             break;
/*  594:     */           }
/*  595: 660 */           Byt.charTo(_charTo, c);
/*  596: 662 */           for (int charByte : _charTo)
/*  597:     */           {
/*  598: 663 */             ByteScanner.encodeByteIntoTwoAsciiCharBytes(charByte, _encoded);
/*  599: 664 */             for (int b : _encoded)
/*  600:     */             {
/*  601: 665 */               _buffer[_location] = ((char)b);
/*  602: 666 */               _location++;
/*  603:     */             }
/*  604:     */           }
/*  605:     */           break;
/*  606:     */         }
/*  607:     */       }
/*  608:     */       else
/*  609:     */       {
/*  610: 675 */         _buffer[_location] = c;
/*  611: 676 */         _location++;
/*  612:     */       }
/*  613: 681 */       index++;
/*  614: 681 */       if (index >= charArray.length) {
/*  615:     */         break;
/*  616:     */       }
/*  617:     */     }
/*  618: 685 */     _buffer[_location] = '"';
/*  619: 686 */     _location++;
/*  620:     */     
/*  621:     */ 
/*  622: 689 */     this.buffer = _buffer;
/*  623: 690 */     this.location = _location;
/*  624:     */     
/*  625: 692 */     return this;
/*  626:     */   }
/*  627:     */   
/*  628:     */   private final CharBuf doAddJsonEscapedString(char[] charArray)
/*  629:     */   {
/*  630: 699 */     char[] _buffer = this.buffer;
/*  631: 700 */     int _location = this.location;
/*  632:     */     
/*  633: 702 */     byte[] _encoded = this.encoded;
/*  634:     */     
/*  635: 704 */     byte[] _charTo = this.charTo;
/*  636:     */     
/*  637: 706 */     int ensureThisMuch = charArray.length + (this.jsonControlCount + 2) * 5;
/*  638:     */     
/*  639: 708 */     int sizeNeeded = ensureThisMuch + _location;
/*  640: 709 */     if (sizeNeeded > this.capacity)
/*  641:     */     {
/*  642: 711 */       int growBy = _buffer.length * 2 < sizeNeeded ? sizeNeeded : _buffer.length * 2;
/*  643: 712 */       _buffer = Chr.grow(_buffer, growBy);
/*  644: 713 */       this.capacity = _buffer.length;
/*  645:     */     }
/*  646: 719 */     _buffer[_location] = '"';
/*  647: 720 */     _location++;
/*  648:     */     
/*  649: 722 */     int index = 0;
/*  650:     */     for (;;)
/*  651:     */     {
/*  652: 725 */       char c = charArray[index];
/*  653: 728 */       if (isJSONEscapeOrAsciiControl(c))
/*  654:     */       {
/*  655: 730 */         switch (c)
/*  656:     */         {
/*  657:     */         case '"': 
/*  658: 732 */           _buffer[_location] = '\\';
/*  659: 733 */           _location++;
/*  660: 734 */           _buffer[_location] = '"';
/*  661: 735 */           _location++;
/*  662: 736 */           break;
/*  663:     */         case '\\': 
/*  664: 738 */           _buffer[_location] = '\\';
/*  665: 739 */           _location++;
/*  666: 740 */           _buffer[_location] = '\\';
/*  667: 741 */           _location++;
/*  668: 742 */           break;
/*  669:     */         case '\b': 
/*  670: 744 */           _buffer[_location] = '\\';
/*  671: 745 */           _location++;
/*  672: 746 */           _buffer[_location] = 'b';
/*  673: 747 */           _location++;
/*  674: 748 */           break;
/*  675:     */         case '\f': 
/*  676: 750 */           _buffer[_location] = '\\';
/*  677: 751 */           _location++;
/*  678: 752 */           _buffer[_location] = 'f';
/*  679: 753 */           _location++;
/*  680: 754 */           break;
/*  681:     */         case '\n': 
/*  682: 756 */           _buffer[_location] = '\\';
/*  683: 757 */           _location++;
/*  684: 758 */           _buffer[_location] = 'n';
/*  685: 759 */           _location++;
/*  686: 760 */           break;
/*  687:     */         case '\r': 
/*  688: 762 */           _buffer[_location] = '\\';
/*  689: 763 */           _location++;
/*  690: 764 */           _buffer[_location] = 'r';
/*  691: 765 */           _location++;
/*  692: 766 */           break;
/*  693:     */         case '\t': 
/*  694: 769 */           _buffer[_location] = '\\';
/*  695: 770 */           _location++;
/*  696: 771 */           _buffer[_location] = 't';
/*  697: 772 */           _location++;
/*  698: 773 */           break;
/*  699:     */         default: 
/*  700: 777 */           _buffer[_location] = '\\';
/*  701: 778 */           _location++;
/*  702: 779 */           _buffer[_location] = 'u';
/*  703: 780 */           _location++;
/*  704: 781 */           if (c <= 'ÿ')
/*  705:     */           {
/*  706: 782 */             _buffer[_location] = '0';
/*  707: 783 */             _location++;
/*  708: 784 */             _buffer[_location] = '0';
/*  709: 785 */             _location++;
/*  710: 786 */             ByteScanner.encodeByteIntoTwoAsciiCharBytes(c, _encoded);
/*  711: 787 */             for (int b : _encoded)
/*  712:     */             {
/*  713: 788 */               _buffer[_location] = ((char)b);
/*  714: 789 */               _location++;
/*  715:     */             }
/*  716:     */             break;
/*  717:     */           }
/*  718: 793 */           Byt.charTo(_charTo, c);
/*  719: 795 */           for (int charByte : _charTo)
/*  720:     */           {
/*  721: 796 */             ByteScanner.encodeByteIntoTwoAsciiCharBytes(charByte, _encoded);
/*  722: 797 */             for (int b : _encoded)
/*  723:     */             {
/*  724: 798 */               _buffer[_location] = ((char)b);
/*  725: 799 */               _location++;
/*  726:     */             }
/*  727:     */           }
/*  728:     */           break;
/*  729:     */         }
/*  730:     */       }
/*  731:     */       else
/*  732:     */       {
/*  733: 808 */         _buffer[_location] = c;
/*  734: 809 */         _location++;
/*  735:     */       }
/*  736: 814 */       index++;
/*  737: 814 */       if (index >= charArray.length) {
/*  738:     */         break;
/*  739:     */       }
/*  740:     */     }
/*  741: 818 */     _buffer[_location] = '"';
/*  742: 819 */     _location++;
/*  743:     */     
/*  744:     */ 
/*  745: 822 */     this.buffer = _buffer;
/*  746: 823 */     this.location = _location;
/*  747:     */     
/*  748: 825 */     return this;
/*  749:     */   }
/*  750:     */   
/*  751:     */   public final CharBuf addJsonFieldName(String str)
/*  752:     */   {
/*  753: 832 */     return addJsonFieldName(FastStringUtils.toCharArray(str));
/*  754:     */   }
/*  755:     */   
/*  756:     */   public final CharBuf addJsonFieldName(char[] chars)
/*  757:     */   {
/*  758: 836 */     int sizeNeeded = chars.length + 4 + this.location;
/*  759: 837 */     if (sizeNeeded > this.capacity)
/*  760:     */     {
/*  761: 838 */       this.buffer = Chr.grow(this.buffer, sizeNeeded * 2);
/*  762: 839 */       this.capacity = this.buffer.length;
/*  763:     */     }
/*  764: 841 */     this.buffer[this.location] = '"';
/*  765: 842 */     this.location += 1;
/*  766:     */     
/*  767: 844 */     System.arraycopy(chars, 0, this.buffer, this.location, chars.length);
/*  768:     */     
/*  769: 846 */     this.location += chars.length;
/*  770: 847 */     this.buffer[this.location] = '"';
/*  771: 848 */     this.location += 1;
/*  772: 849 */     this.buffer[this.location] = ':';
/*  773: 850 */     this.location += 1;
/*  774:     */     
/*  775: 852 */     return this;
/*  776:     */   }
/*  777:     */   
/*  778:     */   public final CharBuf addQuoted(String str)
/*  779:     */   {
/*  780: 856 */     char[] chars = FastStringUtils.toCharArray(str);
/*  781: 857 */     addQuoted(chars);
/*  782: 858 */     return this;
/*  783:     */   }
/*  784:     */   
/*  785:     */   public CharBuf add(char[] chars, int length)
/*  786:     */   {
/*  787: 863 */     if (length + this.location < this.capacity)
/*  788:     */     {
/*  789: 864 */       Chr._idx(this.buffer, this.location, chars, length);
/*  790:     */     }
/*  791:     */     else
/*  792:     */     {
/*  793: 866 */       this.buffer = Chr.grow(this.buffer, this.buffer.length * 2 + length);
/*  794: 867 */       Chr._idx(this.buffer, this.location, chars);
/*  795: 868 */       this.capacity = this.buffer.length;
/*  796:     */     }
/*  797: 870 */     this.location += length;
/*  798: 871 */     return this;
/*  799:     */   }
/*  800:     */   
/*  801:     */   public CharBuf add(byte[] chars)
/*  802:     */   {
/*  803: 875 */     if (chars.length + this.location < this.capacity)
/*  804:     */     {
/*  805: 876 */       Chr._idx(this.buffer, this.location, chars);
/*  806:     */     }
/*  807:     */     else
/*  808:     */     {
/*  809: 878 */       this.buffer = Chr.grow(this.buffer, this.buffer.length * 2 + chars.length);
/*  810: 879 */       Chr._idx(this.buffer, this.location, chars);
/*  811: 880 */       this.capacity = this.buffer.length;
/*  812:     */     }
/*  813: 882 */     this.location += chars.length;
/*  814: 883 */     return this;
/*  815:     */   }
/*  816:     */   
/*  817:     */   public CharBuf add(byte[] bytes, int start, int end)
/*  818:     */   {
/*  819: 890 */     int charsLength = end - start;
/*  820: 891 */     if (charsLength + this.location > this.capacity) {
/*  821: 892 */       this.buffer = Chr.grow(this.buffer, this.buffer.length * 2 + charsLength);
/*  822:     */     }
/*  823: 894 */     Chr._idx(this.buffer, this.location, bytes, start, end);
/*  824: 895 */     this.capacity = this.buffer.length;
/*  825: 896 */     this.location += charsLength;
/*  826: 897 */     return this;
/*  827:     */   }
/*  828:     */   
/*  829:     */   public CharBuf add(String string, int start, int end)
/*  830:     */   {
/*  831: 902 */     int charsLength = end - start;
/*  832: 903 */     if (charsLength + this.location > this.capacity) {
/*  833: 904 */       this.buffer = Chr.grow(this.buffer, this.buffer.length * 2 + charsLength);
/*  834:     */     }
/*  835: 907 */     return add(string.substring(start, end));
/*  836:     */   }
/*  837:     */   
/*  838:     */   public int length()
/*  839:     */   {
/*  840: 914 */     return len();
/*  841:     */   }
/*  842:     */   
/*  843:     */   public char charAt(int index)
/*  844:     */   {
/*  845: 919 */     return this.buffer[index];
/*  846:     */   }
/*  847:     */   
/*  848:     */   public CharSequence subSequence(int start, int end)
/*  849:     */   {
/*  850: 924 */     return new String(this.buffer, start, end - start);
/*  851:     */   }
/*  852:     */   
/*  853:     */   public String toString()
/*  854:     */   {
/*  855: 928 */     return FastStringUtils.noCopyStringFromCharsNoCheck(this.buffer, this.location);
/*  856:     */   }
/*  857:     */   
/*  858:     */   public String toDebugString()
/*  859:     */   {
/*  860: 932 */     return "CharBuf{capacity=" + this.capacity + ", location=" + this.location + '}';
/*  861:     */   }
/*  862:     */   
/*  863:     */   public String toStringAndRecycle()
/*  864:     */   {
/*  865: 940 */     String str = toString();
/*  866: 941 */     this.location = 0;
/*  867: 942 */     return str;
/*  868:     */   }
/*  869:     */   
/*  870:     */   public int len()
/*  871:     */   {
/*  872: 946 */     return this.location;
/*  873:     */   }
/*  874:     */   
/*  875:     */   public char[] toCharArray()
/*  876:     */   {
/*  877: 950 */     return this.buffer;
/*  878:     */   }
/*  879:     */   
/*  880:     */   public void _len(int location)
/*  881:     */   {
/*  882: 954 */     this.location = location;
/*  883:     */   }
/*  884:     */   
/*  885:     */   public char[] readForRecycle()
/*  886:     */   {
/*  887: 959 */     this.location = 0;
/*  888: 960 */     return this.buffer;
/*  889:     */   }
/*  890:     */   
/*  891:     */   public void recycle()
/*  892:     */   {
/*  893: 965 */     this.location = 0;
/*  894:     */   }
/*  895:     */   
/*  896:     */   public double doubleValue()
/*  897:     */   {
/*  898: 973 */     return CharScanner.parseDouble(this.buffer, 0, this.location);
/*  899:     */   }
/*  900:     */   
/*  901:     */   public float floatValue()
/*  902:     */   {
/*  903: 978 */     return CharScanner.parseFloat(this.buffer, 0, this.location);
/*  904:     */   }
/*  905:     */   
/*  906:     */   public int intValue()
/*  907:     */   {
/*  908: 982 */     return CharScanner.parseInt(this.buffer, 0, this.location);
/*  909:     */   }
/*  910:     */   
/*  911:     */   public long longValue()
/*  912:     */   {
/*  913: 986 */     return CharScanner.parseLong(this.buffer, 0, this.location);
/*  914:     */   }
/*  915:     */   
/*  916:     */   public byte byteValue()
/*  917:     */   {
/*  918: 991 */     return (byte)intValue();
/*  919:     */   }
/*  920:     */   
/*  921:     */   public short shortValue()
/*  922:     */   {
/*  923: 995 */     return (short)intValue();
/*  924:     */   }
/*  925:     */   
/*  926:     */   public Number toIntegerWrapper()
/*  927:     */   {
/*  928:1001 */     if (CharScanner.isInteger(this.buffer, 0, this.location)) {
/*  929:1002 */       return Integer.valueOf(intValue());
/*  930:     */     }
/*  931:1004 */     return Long.valueOf(longValue());
/*  932:     */   }
/*  933:     */   
/*  934:     */   public void addAsUTF(byte[] value)
/*  935:     */   {
/*  936:1012 */     String str = new String(value, StandardCharsets.UTF_8);
/*  937:1013 */     char[] chars = FastStringUtils.toCharArray(str);
/*  938:1014 */     add(chars);
/*  939:     */   }
/*  940:     */   
/*  941:1022 */   static final char[] nullChars = "null".toCharArray();
/*  942:     */   private Cache<BigDecimal, char[]> bigDCache;
/*  943:     */   private Cache<BigInteger, char[]> bigICache;
/*  944:     */   private Cache<Long, char[]> lcache;
/*  945:     */   private Cache<Currency, char[]> currencyCache;
/*  946:     */   
/*  947:     */   public final void addNull()
/*  948:     */   {
/*  949:1024 */     add(nullChars);
/*  950:     */   }
/*  951:     */   
/*  952:     */   public char lastChar()
/*  953:     */   {
/*  954:1028 */     return this.buffer[(this.location - 1)];
/*  955:     */   }
/*  956:     */   
/*  957:     */   public void removeLastChar()
/*  958:     */   {
/*  959:1032 */     this.location -= 1;
/*  960:1033 */     if (this.location < 0) {
/*  961:1034 */       this.location = 0;
/*  962:     */     }
/*  963:     */   }
/*  964:     */   
/*  965:     */   public CharBuf addBigDecimal(BigDecimal key)
/*  966:     */   {
/*  967:1041 */     if (this.bigDCache == null) {
/*  968:1042 */       this.bigDCache = new SimpleCache(100, CacheType.LRU);
/*  969:     */     }
/*  970:1044 */     char[] chars = (char[])this.bigDCache.get(key);
/*  971:1046 */     if (chars == null)
/*  972:     */     {
/*  973:1047 */       String str = key.toString();
/*  974:1048 */       chars = FastStringUtils.toCharArray(str);
/*  975:1049 */       this.bigDCache.put(key, chars);
/*  976:     */     }
/*  977:1052 */     add(chars);
/*  978:     */     
/*  979:1054 */     return this;
/*  980:     */   }
/*  981:     */   
/*  982:     */   public CharBuf addBigInteger(BigInteger key)
/*  983:     */   {
/*  984:1062 */     if (this.bigICache == null) {
/*  985:1063 */       this.bigICache = new SimpleCache(100, CacheType.LRU);
/*  986:     */     }
/*  987:1065 */     char[] chars = (char[])this.bigICache.get(key);
/*  988:1067 */     if (chars == null)
/*  989:     */     {
/*  990:1068 */       String str = key.toString();
/*  991:1069 */       chars = FastStringUtils.toCharArray(str);
/*  992:1070 */       this.bigICache.put(key, chars);
/*  993:     */     }
/*  994:1073 */     add(chars);
/*  995:     */     
/*  996:1075 */     return this;
/*  997:     */   }
/*  998:     */   
/*  999:     */   public final CharBuf addLong(long l)
/* 1000:     */   {
/* 1001:1086 */     addLong(Long.valueOf(l));
/* 1002:1087 */     return this;
/* 1003:     */   }
/* 1004:     */   
/* 1005:     */   public final CharBuf addLong(Long key)
/* 1006:     */   {
/* 1007:1092 */     if (this.lcache == null) {
/* 1008:1093 */       this.lcache = new SimpleCache(100, CacheType.LRU);
/* 1009:     */     }
/* 1010:1095 */     char[] chars = (char[])this.lcache.get(key);
/* 1011:1097 */     if (chars == null)
/* 1012:     */     {
/* 1013:1098 */       String str = Long.toString(key.longValue());
/* 1014:1099 */       chars = FastStringUtils.toCharArray(str);
/* 1015:1100 */       this.lcache.put(key, chars);
/* 1016:     */     }
/* 1017:1103 */     add(chars);
/* 1018:     */     
/* 1019:1105 */     return this;
/* 1020:     */   }
/* 1021:     */   
/* 1022:     */   public CharBuf addCurrency(Currency key)
/* 1023:     */   {
/* 1024:1110 */     if (this.currencyCache == null) {
/* 1025:1111 */       this.currencyCache = new SimpleCache(100, CacheType.LRU);
/* 1026:     */     }
/* 1027:1113 */     char[] chars = (char[])this.currencyCache.get(key);
/* 1028:1115 */     if (chars == null)
/* 1029:     */     {
/* 1030:1116 */       String str = '"' + key.toString() + '"';
/* 1031:1117 */       chars = FastStringUtils.toCharArray(str);
/* 1032:1118 */       this.currencyCache.put(key, chars);
/* 1033:     */     }
/* 1034:1121 */     add(chars);
/* 1035:     */     
/* 1036:1123 */     return this;
/* 1037:     */   }
/* 1038:     */   
/* 1039:     */   public CharSequence addHex(int decoded)
/* 1040:     */   {
/* 1041:1138 */     int _location = this.location;
/* 1042:1139 */     char[] _buffer = this.buffer;
/* 1043:1140 */     int _capacity = this.capacity;
/* 1044:1142 */     if (2 + _location > _capacity)
/* 1045:     */     {
/* 1046:1143 */       _buffer = Chr.grow(_buffer);
/* 1047:1144 */       _capacity = _buffer.length;
/* 1048:     */     }
/* 1049:1147 */     _buffer[_location] = ((char)encodeNibbleToHexAsciiCharByte(decoded >> 4 & 0xF));
/* 1050:1148 */     _location++;
/* 1051:     */     
/* 1052:     */ 
/* 1053:1151 */     _buffer[_location] = ((char)encodeNibbleToHexAsciiCharByte(decoded & 0xF));
/* 1054:1152 */     _location++;
/* 1055:     */     
/* 1056:1154 */     this.location = _location;
/* 1057:1155 */     this.buffer = _buffer;
/* 1058:1156 */     this.capacity = _capacity;
/* 1059:1157 */     return this;
/* 1060:     */   }
/* 1061:     */   
/* 1062:     */   protected static int encodeNibbleToHexAsciiCharByte(int nibble)
/* 1063:     */   {
/* 1064:1168 */     switch (nibble)
/* 1065:     */     {
/* 1066:     */     case 0: 
/* 1067:     */     case 1: 
/* 1068:     */     case 2: 
/* 1069:     */     case 3: 
/* 1070:     */     case 4: 
/* 1071:     */     case 5: 
/* 1072:     */     case 6: 
/* 1073:     */     case 7: 
/* 1074:     */     case 8: 
/* 1075:     */     case 9: 
/* 1076:1179 */       return nibble + 48;
/* 1077:     */     case 10: 
/* 1078:     */     case 11: 
/* 1079:     */     case 12: 
/* 1080:     */     case 13: 
/* 1081:     */     case 14: 
/* 1082:     */     case 15: 
/* 1083:1186 */       return nibble + 87;
/* 1084:     */     }
/* 1085:1188 */     Exceptions.die("illegal nibble: " + nibble);
/* 1086:1189 */     return -1;
/* 1087:     */   }
/* 1088:     */   
/* 1089:     */   public final CharBuf decodeJsonString(char[] chars)
/* 1090:     */   {
/* 1091:1194 */     return decodeJsonString(chars, 0, chars.length);
/* 1092:     */   }
/* 1093:     */   
/* 1094:1198 */   static final char[] controlMap = new char['ÿ'];
/* 1095:     */   protected static final int DOUBLE_QUOTE = 34;
/* 1096:     */   protected static final int ESCAPE = 92;
/* 1097:     */   protected static final int LETTER_N = 110;
/* 1098:     */   protected static final int LETTER_U = 117;
/* 1099:     */   protected static final int LETTER_T = 116;
/* 1100:     */   protected static final int LETTER_R = 114;
/* 1101:     */   protected static final int LETTER_B = 98;
/* 1102:     */   protected static final int LETTER_F = 102;
/* 1103:     */   protected static final int FORWARD_SLASH = 47;
/* 1104:     */   
/* 1105:     */   static
/* 1106:     */   {
/* 1107:1201 */     controlMap[110] = '\n';
/* 1108:1202 */     controlMap[98] = '\b';
/* 1109:1203 */     controlMap[47] = '/';
/* 1110:1204 */     controlMap[102] = '\f';
/* 1111:1205 */     controlMap[114] = '\r';
/* 1112:1206 */     controlMap[116] = '\t';
/* 1113:1207 */     controlMap[92] = '\\';
/* 1114:     */     
/* 1115:1209 */     controlMap[34] = '"';
/* 1116:     */   }
/* 1117:     */   
/* 1118:     */   public final CharBuf decodeJsonString(char[] chars, int start, int to)
/* 1119:     */   {
/* 1120:1213 */     int len = to - start;
/* 1121:     */     
/* 1122:     */ 
/* 1123:1216 */     char[] buffer = this.buffer;
/* 1124:1217 */     int location = this.location;
/* 1125:1221 */     if (len > this.capacity)
/* 1126:     */     {
/* 1127:1222 */       buffer = Chr.grow(buffer, buffer.length * 2 + len);
/* 1128:1223 */       this.capacity = buffer.length;
/* 1129:1224 */       this.buffer = buffer;
/* 1130:     */     }
/* 1131:1228 */     int index = start;
/* 1132:     */     for (;;)
/* 1133:     */     {
/* 1134:1231 */       char c = chars[index];
/* 1135:1232 */       if ((c == '\\') && (index < to - 1))
/* 1136:     */       {
/* 1137:1233 */         index++;
/* 1138:1234 */         c = chars[index];
/* 1139:1235 */         if (c != 'u')
/* 1140:     */         {
/* 1141:1236 */           buffer[(location++)] = controlMap[c];
/* 1142:     */         }
/* 1143:1239 */         else if (index + 4 < to)
/* 1144:     */         {
/* 1145:1240 */           String hex = new String(chars, index + 1, 4);
/* 1146:1241 */           char unicode = (char)Integer.parseInt(hex, 16);
/* 1147:1242 */           buffer[(location++)] = unicode;
/* 1148:1243 */           index += 4;
/* 1149:     */         }
/* 1150:     */       }
/* 1151:     */       else
/* 1152:     */       {
/* 1153:1248 */         buffer[(location++)] = c;
/* 1154:     */       }
/* 1155:1250 */       if (index >= to - 1) {
/* 1156:     */         break;
/* 1157:     */       }
/* 1158:1253 */       index++;
/* 1159:     */     }
/* 1160:1257 */     this.buffer = buffer;
/* 1161:1258 */     this.location = location;
/* 1162:     */     
/* 1163:1260 */     return this;
/* 1164:     */   }
/* 1165:     */   
/* 1166:     */   public final CharBuf decodeJsonString(byte[] bytes, int start, int to)
/* 1167:     */   {
/* 1168:1292 */     String str = new String(bytes, start, to - start, StandardCharsets.UTF_8);
/* 1169:1293 */     char[] chars = FastStringUtils.toCharArray(str);
/* 1170:1294 */     decodeJsonString(chars);
/* 1171:1295 */     return this;
/* 1172:     */   }
/* 1173:     */   
/* 1174:     */   public void ensure(int i)
/* 1175:     */   {
/* 1176:1299 */     if (i + this.location > this.capacity)
/* 1177:     */     {
/* 1178:1300 */       this.buffer = Chr.grow(this.buffer, i * 2);
/* 1179:1301 */       this.capacity = this.buffer.length;
/* 1180:     */     }
/* 1181:     */   }
/* 1182:     */   
/* 1183:     */   public boolean equals(Object o)
/* 1184:     */   {
/* 1185:1308 */     if (o == null) {
/* 1186:1309 */       return false;
/* 1187:     */     }
/* 1188:1312 */     if ((o instanceof CharSequence)) {
/* 1189:1313 */       return toString().equals(o.toString());
/* 1190:     */     }
/* 1191:1315 */     return false;
/* 1192:     */   }
/* 1193:     */   
/* 1194:     */   public int hashCode()
/* 1195:     */   {
/* 1196:1322 */     return toString().hashCode();
/* 1197:     */   }
/* 1198:     */   
/* 1199:     */   public CharBuf multiply(char c, int len)
/* 1200:     */   {
/* 1201:1326 */     for (int index = 0; index < len; index++) {
/* 1202:1327 */       add(c);
/* 1203:     */     }
/* 1204:1329 */     return this;
/* 1205:     */   }
/* 1206:     */   
/* 1207:     */   public CharBuf multiply(CharSequence str, int len)
/* 1208:     */   {
/* 1209:1335 */     for (int index = 0; index < len; index++) {
/* 1210:1336 */       add(str.toString());
/* 1211:     */     }
/* 1212:1338 */     return this;
/* 1213:     */   }
/* 1214:     */   
/* 1215:     */   public void puts(Object... messages)
/* 1216:     */   {
/* 1217:1343 */     for (Object message : messages)
/* 1218:     */     {
/* 1219:1346 */       if (message == null) {
/* 1220:1347 */         add("<NULL>");
/* 1221:1348 */       } else if ((message instanceof char[])) {
/* 1222:1349 */         add((char[])message);
/* 1223:1350 */       } else if (message.getClass().isArray()) {
/* 1224:1351 */         add(Lists.toListOrSingletonList(message).toString());
/* 1225:     */       } else {
/* 1226:1353 */         add(message.toString());
/* 1227:     */       }
/* 1228:1355 */       add(' ');
/* 1229:     */     }
/* 1230:1357 */     addLine();
/* 1231:     */   }
/* 1232:     */   
/* 1233:     */   public void println(String message)
/* 1234:     */   {
/* 1235:1371 */     addLine(message);
/* 1236:     */   }
/* 1237:     */   
/* 1238:     */   public void println(Object object)
/* 1239:     */   {
/* 1240:1384 */     addLine(Str.toString(object));
/* 1241:     */   }
/* 1242:     */   
/* 1243:     */   public CharBuf indent(int i)
/* 1244:     */   {
/* 1245:1388 */     return multiply(' ', i);
/* 1246:     */   }
/* 1247:     */   
/* 1248:     */   public void jsonDate(long millis)
/* 1249:     */   {
/* 1250:1392 */     Dates.jsonDateChars(new Date(millis), this);
/* 1251:     */   }
/* 1252:     */   
/* 1253:     */   public PrintWriter append(CharSequence csq)
/* 1254:     */   {
/* 1255:1399 */     return super.append(csq);
/* 1256:     */   }
/* 1257:     */   
/* 1258:     */   private static Writer writer()
/* 1259:     */   {
/* 1260:1403 */     new Writer()
/* 1261:     */     {
/* 1262:     */       public void write(char[] cbuf, int off, int len)
/* 1263:     */         throws IOException
/* 1264:     */       {}
/* 1265:     */       
/* 1266:     */       public void flush()
/* 1267:     */         throws IOException
/* 1268:     */       {}
/* 1269:     */       
/* 1270:     */       public void close()
/* 1271:     */         throws IOException
/* 1272:     */       {}
/* 1273:     */     };
/* 1274:     */   }
/* 1275:     */   
/* 1276:     */   public void print(boolean b)
/* 1277:     */   {
/* 1278:1425 */     addBoolean(b);
/* 1279:     */   }
/* 1280:     */   
/* 1281:     */   public void print(char c)
/* 1282:     */   {
/* 1283:1429 */     add(c);
/* 1284:     */   }
/* 1285:     */   
/* 1286:     */   public void print(int i)
/* 1287:     */   {
/* 1288:1433 */     addInt(i);
/* 1289:     */   }
/* 1290:     */   
/* 1291:     */   public void print(long l)
/* 1292:     */   {
/* 1293:1437 */     addLong(l);
/* 1294:     */   }
/* 1295:     */   
/* 1296:     */   public void print(float f)
/* 1297:     */   {
/* 1298:1441 */     add(f);
/* 1299:     */   }
/* 1300:     */   
/* 1301:     */   public void print(double d)
/* 1302:     */   {
/* 1303:1446 */     add(d);
/* 1304:     */   }
/* 1305:     */   
/* 1306:     */   public void print(char[] s)
/* 1307:     */   {
/* 1308:1450 */     addChars(s);
/* 1309:     */   }
/* 1310:     */   
/* 1311:     */   public void print(String s)
/* 1312:     */   {
/* 1313:1454 */     if (s == null) {
/* 1314:1455 */       s = "null";
/* 1315:     */     }
/* 1316:1457 */     addString(s);
/* 1317:     */   }
/* 1318:     */   
/* 1319:     */   public void print(Object obj)
/* 1320:     */   {
/* 1321:1461 */     write(String.valueOf(obj));
/* 1322:     */   }
/* 1323:     */   
/* 1324:     */   public void println()
/* 1325:     */   {
/* 1326:1467 */     addLine();
/* 1327:     */   }
/* 1328:     */   
/* 1329:     */   public void println(boolean x)
/* 1330:     */   {
/* 1331:1471 */     addBoolean(x).addLine();
/* 1332:     */   }
/* 1333:     */   
/* 1334:     */   public void println(char x)
/* 1335:     */   {
/* 1336:1475 */     addChar(x).addLine();
/* 1337:     */   }
/* 1338:     */   
/* 1339:     */   public void println(int x)
/* 1340:     */   {
/* 1341:1479 */     addInt(x).addLine();
/* 1342:     */   }
/* 1343:     */   
/* 1344:     */   public void println(long x)
/* 1345:     */   {
/* 1346:1485 */     addLong(x).addLine();
/* 1347:     */   }
/* 1348:     */   
/* 1349:     */   public void println(float x)
/* 1350:     */   {
/* 1351:1489 */     add(x).addLine();
/* 1352:     */   }
/* 1353:     */   
/* 1354:     */   public void println(double x)
/* 1355:     */   {
/* 1356:1493 */     add(x).addLine();
/* 1357:     */   }
/* 1358:     */   
/* 1359:     */   public void println(char[] x)
/* 1360:     */   {
/* 1361:1498 */     add(x).addLine();
/* 1362:     */   }
/* 1363:     */   
/* 1364:     */   public PrintWriter printf(String format, Object... args)
/* 1365:     */   {
/* 1366:1503 */     addLine(String.format(format, args));
/* 1367:1504 */     return this;
/* 1368:     */   }
/* 1369:     */   
/* 1370:     */   public PrintWriter printf(Locale l, String format, Object... args)
/* 1371:     */   {
/* 1372:1509 */     addLine(String.format(l, format, args));
/* 1373:1510 */     return this;
/* 1374:     */   }
/* 1375:     */   
/* 1376:     */   public void display()
/* 1377:     */   {
/* 1378:1516 */     System.out.println(toString());
/* 1379:     */   }
/* 1380:     */   
/* 1381:     */   public CharBuf prettyPrintMap(Map map)
/* 1382:     */   {
/* 1383:1522 */     return prettyPrintMap(map, 0);
/* 1384:     */   }
/* 1385:     */   
/* 1386:     */   public CharBuf prettyPrintBean(Object object)
/* 1387:     */   {
/* 1388:1528 */     Map<String, Object> map = Maps.toPrettyMap(object);
/* 1389:1529 */     return prettyPrintMap(map, 0);
/* 1390:     */   }
/* 1391:     */   
/* 1392:     */   public CharBuf prettyPrintBeanWithTypes(Object object)
/* 1393:     */   {
/* 1394:1533 */     Map<String, Object> map = Maps.toMap(object);
/* 1395:1534 */     return prettyPrintMap(map, 0);
/* 1396:     */   }
/* 1397:     */   
/* 1398:     */   public CharBuf prettyPrintBean(Mapper mapper, Object object)
/* 1399:     */   {
/* 1400:1538 */     return prettyPrintMap(mapper.toMap(object), 0);
/* 1401:     */   }
/* 1402:     */   
/* 1403:     */   public CharBuf prettyPrintMap(Map map, int indent)
/* 1404:     */   {
/* 1405:1543 */     Set set = map.entrySet();
/* 1406:     */     
/* 1407:1545 */     Iterator iterator = set.iterator();
/* 1408:     */     
/* 1409:1547 */     indent(indent * 4).add("{\n");
/* 1410:1549 */     while (iterator.hasNext())
/* 1411:     */     {
/* 1412:1551 */       Map.Entry entry = (Map.Entry)iterator.next();
/* 1413:     */       
/* 1414:1553 */       indent((indent + 1) * 4);
/* 1415:     */       
/* 1416:1555 */       addJsonEscapedString(entry.getKey().toString().toCharArray());
/* 1417:     */       
/* 1418:1557 */       add(" : ");
/* 1419:     */       
/* 1420:1559 */       Object value = entry.getValue();
/* 1421:     */       
/* 1422:1561 */       prettyPrintObject(value, true, indent);
/* 1423:     */       
/* 1424:1563 */       add(",\n");
/* 1425:     */     }
/* 1426:1568 */     if (map.size() > 0)
/* 1427:     */     {
/* 1428:1569 */       removeLastChar();
/* 1429:1570 */       removeLastChar();
/* 1430:1571 */       add("\n");
/* 1431:     */     }
/* 1432:1574 */     indent(indent * 4).add('}');
/* 1433:     */     
/* 1434:1576 */     return this;
/* 1435:     */   }
/* 1436:     */   
/* 1437:     */   public CharBuf prettyPrintCollection(Collection values, boolean type, int indent)
/* 1438:     */   {
/* 1439:1585 */     add('[');
/* 1440:1586 */     for (Object value : values)
/* 1441:     */     {
/* 1442:1588 */       prettyPrintObject(value, type, indent);
/* 1443:     */       
/* 1444:     */ 
/* 1445:1591 */       add(',');
/* 1446:     */     }
/* 1447:1594 */     if (values.size() > 0) {
/* 1448:1595 */       removeLastChar();
/* 1449:     */     }
/* 1450:1598 */     add(']');
/* 1451:     */     
/* 1452:1600 */     return this;
/* 1453:     */   }
/* 1454:     */   
/* 1455:     */   public CharBuf prettyPrintObject(Object value, boolean type, int indent)
/* 1456:     */   {
/* 1457:1606 */     TypeType instanceType = TypeType.getInstanceType(value);
/* 1458:1607 */     switch (3.$SwitchMap$org$boon$core$TypeType[instanceType.ordinal()])
/* 1459:     */     {
/* 1460:     */     case 1: 
/* 1461:     */     case 2: 
/* 1462:     */     case 3: 
/* 1463:     */     case 4: 
/* 1464:     */     case 5: 
/* 1465:     */     case 6: 
/* 1466:     */     case 7: 
/* 1467:     */     case 8: 
/* 1468:     */     case 9: 
/* 1469:1619 */       add(value);
/* 1470:1620 */       break;
/* 1471:     */     case 10: 
/* 1472:1623 */       addNull();
/* 1473:1624 */       break;
/* 1474:     */     case 11: 
/* 1475:1627 */       prettyPrintMap((Map)value, indent + 2);
/* 1476:1628 */       break;
/* 1477:     */     case 12: 
/* 1478:1631 */       if (type) {
/* 1479:1632 */         prettyPrintMap(Maps.toMap(value), indent);
/* 1480:     */       } else {
/* 1481:1634 */         prettyPrintMap(Maps.toPrettyMap(value), indent);
/* 1482:     */       }
/* 1483:1636 */       break;
/* 1484:     */     case 13: 
/* 1485:     */     case 14: 
/* 1486:     */     case 15: 
/* 1487:1642 */       prettyPrintCollection((Collection)value, type, indent);
/* 1488:1643 */       break;
/* 1489:     */     case 16: 
/* 1490:     */     case 17: 
/* 1491:     */     case 18: 
/* 1492:     */     case 19: 
/* 1493:     */     case 20: 
/* 1494:     */     case 21: 
/* 1495:     */     case 22: 
/* 1496:     */     case 23: 
/* 1497:     */     case 24: 
/* 1498:1657 */       prettyPrintCollection(Lists.list(Conversions.iterator(value)), type, indent);
/* 1499:1658 */       break;
/* 1500:     */     case 25: 
/* 1501:1661 */       addJsonEscapedString(((Enum)value).name().toCharArray());
/* 1502:1662 */       break;
/* 1503:     */     default: 
/* 1504:1666 */       addJsonEscapedString(value.toString().toCharArray());
/* 1505:     */     }
/* 1506:1671 */     return this;
/* 1507:     */   }
/* 1508:     */   
/* 1509:     */   public void flush() {}
/* 1510:     */   
/* 1511:     */   public void close() {}
/* 1512:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.boon.primitive.CharBuf
 * JD-Core Version:    0.7.0.1
 */