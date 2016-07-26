/*   1:    */ package org.j_paine.formatter;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ 
/*   5:    */ public class CJFormat
/*   6:    */ {
/*   7:    */   private int width;
/*   8:    */   private int precision;
/*   9:    */   private String pre;
/*  10:    */   private String post;
/*  11:    */   private boolean leading_zeroes;
/*  12:    */   private boolean show_plus;
/*  13:    */   private boolean alternate;
/*  14:    */   private boolean show_space;
/*  15:    */   private boolean left_align;
/*  16:    */   private char fmt;
/*  17:    */   
/*  18:    */   public CJFormat(String paramString)
/*  19:    */   {
/*  20: 86 */     this.width = 0;
/*  21: 87 */     this.precision = -1;
/*  22: 88 */     this.pre = "";
/*  23: 89 */     this.post = "";
/*  24: 90 */     this.leading_zeroes = false;
/*  25: 91 */     this.show_plus = false;
/*  26: 92 */     this.alternate = false;
/*  27: 93 */     this.show_space = false;
/*  28: 94 */     this.left_align = false;
/*  29: 95 */     this.fmt = ' ';
/*  30:    */     
/*  31: 97 */     int i = 0;
/*  32: 98 */     int j = paramString.length();
/*  33: 99 */     int k = 0;
/*  34:    */     
/*  35:    */ 
/*  36:102 */     int m = 0;
/*  37:104 */     while (k == 0)
/*  38:    */     {
/*  39:105 */       if (m >= j) {
/*  40:105 */         k = 5;
/*  41:106 */       } else if (paramString.charAt(m) == '%')
/*  42:    */       {
/*  43:107 */         if (m < j - 1)
/*  44:    */         {
/*  45:108 */           if (paramString.charAt(m + 1) == '%')
/*  46:    */           {
/*  47:109 */             this.pre += '%';
/*  48:110 */             m++;
/*  49:    */           }
/*  50:    */           else
/*  51:    */           {
/*  52:113 */             k = 1;
/*  53:    */           }
/*  54:    */         }
/*  55:    */         else {
/*  56:115 */           throw new IllegalArgumentException();
/*  57:    */         }
/*  58:    */       }
/*  59:    */       else {
/*  60:118 */         this.pre += paramString.charAt(m);
/*  61:    */       }
/*  62:119 */       m++;
/*  63:    */     }
/*  64:121 */     while (k == 1)
/*  65:    */     {
/*  66:122 */       if (m >= j)
/*  67:    */       {
/*  68:122 */         k = 5;
/*  69:    */       }
/*  70:123 */       else if (paramString.charAt(m) == ' ')
/*  71:    */       {
/*  72:123 */         this.show_space = true;
/*  73:    */       }
/*  74:124 */       else if (paramString.charAt(m) == '-')
/*  75:    */       {
/*  76:124 */         this.left_align = true;
/*  77:    */       }
/*  78:125 */       else if (paramString.charAt(m) == '+')
/*  79:    */       {
/*  80:125 */         this.show_plus = true;
/*  81:    */       }
/*  82:126 */       else if (paramString.charAt(m) == '0')
/*  83:    */       {
/*  84:126 */         this.leading_zeroes = true;
/*  85:    */       }
/*  86:127 */       else if (paramString.charAt(m) == '#')
/*  87:    */       {
/*  88:127 */         this.alternate = true;
/*  89:    */       }
/*  90:    */       else
/*  91:    */       {
/*  92:128 */         k = 2;m--;
/*  93:    */       }
/*  94:129 */       m++;
/*  95:    */     }
/*  96:131 */     while (k == 2) {
/*  97:132 */       if (m >= j)
/*  98:    */       {
/*  99:132 */         k = 5;
/* 100:    */       }
/* 101:133 */       else if (('0' <= paramString.charAt(m)) && (paramString.charAt(m) <= '9'))
/* 102:    */       {
/* 103:134 */         this.width = (this.width * 10 + paramString.charAt(m) - 48);
/* 104:135 */         m++;
/* 105:    */       }
/* 106:137 */       else if (paramString.charAt(m) == '.')
/* 107:    */       {
/* 108:138 */         k = 3;
/* 109:139 */         this.precision = 0;
/* 110:140 */         m++;
/* 111:    */       }
/* 112:    */       else
/* 113:    */       {
/* 114:143 */         k = 4;
/* 115:    */       }
/* 116:    */     }
/* 117:145 */     while (k == 3) {
/* 118:146 */       if (m >= j)
/* 119:    */       {
/* 120:146 */         k = 5;
/* 121:    */       }
/* 122:147 */       else if (('0' <= paramString.charAt(m)) && (paramString.charAt(m) <= '9'))
/* 123:    */       {
/* 124:148 */         this.precision = (this.precision * 10 + paramString.charAt(m) - 48);
/* 125:149 */         m++;
/* 126:    */       }
/* 127:    */       else
/* 128:    */       {
/* 129:152 */         k = 4;
/* 130:    */       }
/* 131:    */     }
/* 132:154 */     if (k == 4)
/* 133:    */     {
/* 134:155 */       if (m >= j) {
/* 135:155 */         k = 5;
/* 136:    */       } else {
/* 137:156 */         this.fmt = paramString.charAt(m);
/* 138:    */       }
/* 139:157 */       m++;
/* 140:    */     }
/* 141:159 */     if (m < j) {
/* 142:160 */       this.post = paramString.substring(m, j);
/* 143:    */     }
/* 144:    */   }
/* 145:    */   
/* 146:    */   public CJFormat()
/* 147:    */   {
/* 148:165 */     this.width = 0;
/* 149:166 */     this.precision = -1;
/* 150:167 */     this.pre = "";
/* 151:168 */     this.post = "";
/* 152:169 */     this.leading_zeroes = false;
/* 153:170 */     this.show_plus = false;
/* 154:171 */     this.alternate = false;
/* 155:172 */     this.show_space = false;
/* 156:173 */     this.left_align = false;
/* 157:174 */     this.fmt = ' ';
/* 158:    */   }
/* 159:    */   
/* 160:    */   public static void print(PrintStream paramPrintStream, String paramString, double paramDouble)
/* 161:    */   {
/* 162:186 */     paramPrintStream.print(new CJFormat(paramString).form(paramDouble));
/* 163:    */   }
/* 164:    */   
/* 165:    */   public static void print(PrintStream paramPrintStream, String paramString, long paramLong)
/* 166:    */   {
/* 167:196 */     paramPrintStream.print(new CJFormat(paramString).form(paramLong));
/* 168:    */   }
/* 169:    */   
/* 170:    */   public static void print(PrintStream paramPrintStream, String paramString, char paramChar)
/* 171:    */   {
/* 172:207 */     paramPrintStream.print(new CJFormat(paramString).form(paramChar));
/* 173:    */   }
/* 174:    */   
/* 175:    */   public static void print(PrintStream paramPrintStream, String paramString1, String paramString2)
/* 176:    */   {
/* 177:217 */     paramPrintStream.print(new CJFormat(paramString1).form(paramString2));
/* 178:    */   }
/* 179:    */   
/* 180:    */   public static int atoi(String paramString)
/* 181:    */   {
/* 182:227 */     return (int)atol(paramString);
/* 183:    */   }
/* 184:    */   
/* 185:    */   public static long atol(String paramString)
/* 186:    */   {
/* 187:237 */     int i = 0;
/* 188:239 */     while ((i < paramString.length()) && (Character.isSpace(paramString.charAt(i)))) {
/* 189:239 */       i++;
/* 190:    */     }
/* 191:240 */     if ((i < paramString.length()) && (paramString.charAt(i) == '0'))
/* 192:    */     {
/* 193:241 */       if ((i + 1 < paramString.length()) && ((paramString.charAt(i + 1) == 'x') || (paramString.charAt(i + 1) == 'X'))) {
/* 194:242 */         return parseLong(paramString.substring(i + 2), 16);
/* 195:    */       }
/* 196:243 */       return parseLong(paramString, 8);
/* 197:    */     }
/* 198:245 */     return parseLong(paramString, 10);
/* 199:    */   }
/* 200:    */   
/* 201:    */   private static long parseLong(String paramString, int paramInt)
/* 202:    */   {
/* 203:249 */     int i = 0;
/* 204:250 */     int j = 1;
/* 205:251 */     long l = 0L;
/* 206:253 */     while ((i < paramString.length()) && (Character.isSpace(paramString.charAt(i)))) {
/* 207:253 */       i++;
/* 208:    */     }
/* 209:254 */     if ((i < paramString.length()) && (paramString.charAt(i) == '-'))
/* 210:    */     {
/* 211:254 */       j = -1;i++;
/* 212:    */     }
/* 213:255 */     else if ((i < paramString.length()) && (paramString.charAt(i) == '+'))
/* 214:    */     {
/* 215:255 */       i++;
/* 216:    */     }
/* 217:256 */     while (i < paramString.length())
/* 218:    */     {
/* 219:257 */       int k = paramString.charAt(i);
/* 220:258 */       if ((48 <= k) && (k < 48 + paramInt)) {
/* 221:259 */         l = l * paramInt + k - 48L;
/* 222:260 */       } else if ((65 <= k) && (k < 65 + paramInt - 10)) {
/* 223:261 */         l = l * paramInt + k - 65L + 10L;
/* 224:262 */       } else if ((97 <= k) && (k < 97 + paramInt - 10)) {
/* 225:263 */         l = l * paramInt + k - 97L + 10L;
/* 226:    */       } else {
/* 227:265 */         return l * j;
/* 228:    */       }
/* 229:266 */       i++;
/* 230:    */     }
/* 231:268 */     return l * j;
/* 232:    */   }
/* 233:    */   
/* 234:    */   public static double atof(String paramString)
/* 235:    */   {
/* 236:277 */     int i = 0;
/* 237:278 */     int j = 1;
/* 238:279 */     double d1 = 0.0D;
/* 239:280 */     double d2 = 0.0D;
/* 240:281 */     double d3 = 1.0D;
/* 241:282 */     int k = 0;
/* 242:284 */     while ((i < paramString.length()) && (Character.isSpace(paramString.charAt(i)))) {
/* 243:284 */       i++;
/* 244:    */     }
/* 245:285 */     if ((i < paramString.length()) && (paramString.charAt(i) == '-'))
/* 246:    */     {
/* 247:285 */       j = -1;i++;
/* 248:    */     }
/* 249:286 */     else if ((i < paramString.length()) && (paramString.charAt(i) == '+'))
/* 250:    */     {
/* 251:286 */       i++;
/* 252:    */     }
/* 253:287 */     while (i < paramString.length())
/* 254:    */     {
/* 255:288 */       int m = paramString.charAt(i);
/* 256:289 */       if ((48 <= m) && (m <= 57))
/* 257:    */       {
/* 258:290 */         if (k == 0)
/* 259:    */         {
/* 260:291 */           d1 = d1 * 10.0D + m - 48.0D;
/* 261:    */         }
/* 262:292 */         else if (k == 1)
/* 263:    */         {
/* 264:293 */           d3 /= 10.0D;
/* 265:294 */           d1 += d3 * (m - 48);
/* 266:    */         }
/* 267:    */       }
/* 268:297 */       else if (m == 46)
/* 269:    */       {
/* 270:298 */         if (k == 0) {
/* 271:298 */           k = 1;
/* 272:    */         } else {
/* 273:299 */           return j * d1;
/* 274:    */         }
/* 275:    */       }
/* 276:    */       else
/* 277:    */       {
/* 278:301 */         if ((m == 101) || (m == 69))
/* 279:    */         {
/* 280:302 */           long l = (int)parseLong(paramString.substring(i + 1), 10);
/* 281:303 */           return j * d1 * Math.pow(10.0D, l);
/* 282:    */         }
/* 283:305 */         return j * d1;
/* 284:    */       }
/* 285:306 */       i++;
/* 286:    */     }
/* 287:308 */     return j * d1;
/* 288:    */   }
/* 289:    */   
/* 290:    */   public String form(double paramDouble)
/* 291:    */   {
/* 292:320 */     if (this.precision < 0) {
/* 293:320 */       this.precision = 6;
/* 294:    */     }
/* 295:321 */     int i = 1;
/* 296:322 */     if (paramDouble < 0.0D)
/* 297:    */     {
/* 298:322 */       paramDouble = -paramDouble;i = -1;
/* 299:    */     }
/* 300:    */     String str;
/* 301:323 */     if (this.fmt == 'f') {
/* 302:324 */       str = fixed_format(paramDouble);
/* 303:325 */     } else if ((this.fmt == 'e') || (this.fmt == 'E') || (this.fmt == 'g') || (this.fmt == 'G')) {
/* 304:326 */       str = exp_format(paramDouble);
/* 305:    */     } else {
/* 306:327 */       throw new IllegalArgumentException();
/* 307:    */     }
/* 308:329 */     return pad(sign(i, str));
/* 309:    */   }
/* 310:    */   
/* 311:    */   public String form(long paramLong)
/* 312:    */   {
/* 313:340 */     int i = 0;
/* 314:    */     String str;
/* 315:341 */     if ((this.fmt == 'd') || (this.fmt == 'i'))
/* 316:    */     {
/* 317:342 */       i = 1;
/* 318:343 */       if (paramLong < 0L)
/* 319:    */       {
/* 320:343 */         paramLong = -paramLong;i = -1;
/* 321:    */       }
/* 322:344 */       str = "" + paramLong;
/* 323:    */     }
/* 324:346 */     else if (this.fmt == 'o')
/* 325:    */     {
/* 326:347 */       str = convert(paramLong, 3, 7, "01234567");
/* 327:    */     }
/* 328:348 */     else if (this.fmt == 'x')
/* 329:    */     {
/* 330:349 */       str = convert(paramLong, 4, 15, "0123456789abcdef");
/* 331:    */     }
/* 332:350 */     else if (this.fmt == 'X')
/* 333:    */     {
/* 334:351 */       str = convert(paramLong, 4, 15, "0123456789ABCDEF");
/* 335:    */     }
/* 336:    */     else
/* 337:    */     {
/* 338:352 */       throw new IllegalArgumentException();
/* 339:    */     }
/* 340:354 */     return pad(sign(i, str));
/* 341:    */   }
/* 342:    */   
/* 343:    */   public String form(char paramChar)
/* 344:    */   {
/* 345:364 */     if (this.fmt != 'c') {
/* 346:365 */       throw new IllegalArgumentException();
/* 347:    */     }
/* 348:367 */     String str = "" + paramChar;
/* 349:368 */     return pad(str);
/* 350:    */   }
/* 351:    */   
/* 352:    */   public String form(String paramString)
/* 353:    */   {
/* 354:378 */     if (this.fmt != 's') {
/* 355:379 */       throw new IllegalArgumentException();
/* 356:    */     }
/* 357:380 */     if (this.precision >= 0) {
/* 358:380 */       paramString = paramString.substring(0, this.precision);
/* 359:    */     }
/* 360:381 */     return pad(paramString);
/* 361:    */   }
/* 362:    */   
/* 363:    */   public static void main(String[] paramArrayOfString)
/* 364:    */   {
/* 365:390 */     double d1 = 1.23456789012D;
/* 366:391 */     double d2 = 123.0D;
/* 367:392 */     double d3 = 1.2345E+030D;
/* 368:393 */     double d4 = 1.02D;
/* 369:394 */     double d5 = 1.234E-005D;
/* 370:395 */     int i = 51966;
/* 371:396 */     print(System.out, "x = |%f|\n", d1);
/* 372:397 */     print(System.out, "u = |%20f|\n", d5);
/* 373:398 */     print(System.out, "x = |% .5f|\n", d1);
/* 374:399 */     print(System.out, "w = |%20.5f|\n", d4);
/* 375:400 */     print(System.out, "x = |%020.5f|\n", d1);
/* 376:401 */     print(System.out, "x = |%+20.5f|\n", d1);
/* 377:402 */     print(System.out, "x = |%+020.5f|\n", d1);
/* 378:403 */     print(System.out, "x = |% 020.5f|\n", d1);
/* 379:404 */     print(System.out, "y = |%#+20.5f|\n", d2);
/* 380:405 */     print(System.out, "y = |%-+20.5f|\n", d2);
/* 381:406 */     print(System.out, "z = |%20.5f|\n", d3);
/* 382:    */     
/* 383:408 */     print(System.out, "x = |%e|\n", d1);
/* 384:409 */     print(System.out, "u = |%20e|\n", d5);
/* 385:410 */     print(System.out, "x = |% .5e|\n", d1);
/* 386:411 */     print(System.out, "w = |%20.5e|\n", d4);
/* 387:412 */     print(System.out, "x = |%020.5e|\n", d1);
/* 388:413 */     print(System.out, "x = |%+20.5e|\n", d1);
/* 389:414 */     print(System.out, "x = |%+020.5e|\n", d1);
/* 390:415 */     print(System.out, "x = |% 020.5e|\n", d1);
/* 391:416 */     print(System.out, "y = |%#+20.5e|\n", d2);
/* 392:417 */     print(System.out, "y = |%-+20.5e|\n", d2);
/* 393:    */     
/* 394:419 */     print(System.out, "x = |%g|\n", d1);
/* 395:420 */     print(System.out, "z = |%g|\n", d3);
/* 396:421 */     print(System.out, "w = |%g|\n", d4);
/* 397:422 */     print(System.out, "u = |%g|\n", d5);
/* 398:423 */     print(System.out, "y = |%.2g|\n", d2);
/* 399:424 */     print(System.out, "y = |%#.2g|\n", d2);
/* 400:    */     
/* 401:426 */     print(System.out, "d = |%d|\n", i);
/* 402:427 */     print(System.out, "d = |%20d|\n", i);
/* 403:428 */     print(System.out, "d = |%020d|\n", i);
/* 404:429 */     print(System.out, "d = |%+20d|\n", i);
/* 405:430 */     print(System.out, "d = |% 020d|\n", i);
/* 406:431 */     print(System.out, "d = |%-20d|\n", i);
/* 407:432 */     print(System.out, "d = |%20.8d|\n", i);
/* 408:433 */     print(System.out, "d = |%x|\n", i);
/* 409:434 */     print(System.out, "d = |%20X|\n", i);
/* 410:435 */     print(System.out, "d = |%#20x|\n", i);
/* 411:436 */     print(System.out, "d = |%020X|\n", i);
/* 412:437 */     print(System.out, "d = |%20.8x|\n", i);
/* 413:438 */     print(System.out, "d = |%o|\n", i);
/* 414:439 */     print(System.out, "d = |%020o|\n", i);
/* 415:440 */     print(System.out, "d = |%#20o|\n", i);
/* 416:441 */     print(System.out, "d = |%#020o|\n", i);
/* 417:442 */     print(System.out, "d = |%20.12o|\n", i);
/* 418:    */     
/* 419:444 */     print(System.out, "s = |%-20s|\n", "Hello");
/* 420:445 */     print(System.out, "s = |%-20c|\n", '!');
/* 421:    */   }
/* 422:    */   
/* 423:    */   private static String repeat(char paramChar, int paramInt)
/* 424:    */   {
/* 425:450 */     if (paramInt <= 0) {
/* 426:450 */       return "";
/* 427:    */     }
/* 428:451 */     StringBuffer localStringBuffer = new StringBuffer(paramInt);
/* 429:452 */     for (int i = 0; i < paramInt; i++) {
/* 430:452 */       localStringBuffer.append(paramChar);
/* 431:    */     }
/* 432:453 */     return localStringBuffer.toString();
/* 433:    */   }
/* 434:    */   
/* 435:    */   private static String convert(long paramLong, int paramInt1, int paramInt2, String paramString)
/* 436:    */   {
/* 437:457 */     if (paramLong == 0L) {
/* 438:457 */       return "0";
/* 439:    */     }
/* 440:458 */     String str = "";
/* 441:459 */     while (paramLong != 0L)
/* 442:    */     {
/* 443:460 */       str = paramString.charAt((int)(paramLong & paramInt2)) + str;
/* 444:461 */       paramLong >>>= paramInt1;
/* 445:    */     }
/* 446:463 */     return str;
/* 447:    */   }
/* 448:    */   
/* 449:    */   private String pad(String paramString)
/* 450:    */   {
/* 451:467 */     String str = repeat(' ', this.width - paramString.length());
/* 452:468 */     if (this.left_align) {
/* 453:468 */       return this.pre + paramString + str + this.post;
/* 454:    */     }
/* 455:469 */     return this.pre + str + paramString + this.post;
/* 456:    */   }
/* 457:    */   
/* 458:    */   private String sign(int paramInt, String paramString)
/* 459:    */   {
/* 460:473 */     String str = "";
/* 461:474 */     if (paramInt < 0) {
/* 462:474 */       str = "-";
/* 463:475 */     } else if (paramInt > 0)
/* 464:    */     {
/* 465:476 */       if (this.show_plus) {
/* 466:476 */         str = "+";
/* 467:477 */       } else if (this.show_space) {
/* 468:477 */         str = " ";
/* 469:    */       }
/* 470:    */     }
/* 471:480 */     else if ((this.fmt == 'o') && (this.alternate) && (paramString.length() > 0) && (paramString.charAt(0) != '0')) {
/* 472:480 */       str = "0";
/* 473:481 */     } else if ((this.fmt == 'x') && (this.alternate)) {
/* 474:481 */       str = "0x";
/* 475:482 */     } else if ((this.fmt == 'X') && (this.alternate)) {
/* 476:482 */       str = "0X";
/* 477:    */     }
/* 478:484 */     int i = 0;
/* 479:485 */     if (this.leading_zeroes) {
/* 480:486 */       i = this.width;
/* 481:487 */     } else if (((this.fmt == 'd') || (this.fmt == 'i') || (this.fmt == 'x') || (this.fmt == 'X') || (this.fmt == 'o')) && (this.precision > 0)) {
/* 482:488 */       i = this.precision;
/* 483:    */     }
/* 484:490 */     return str + repeat('0', i - str.length() - paramString.length()) + paramString;
/* 485:    */   }
/* 486:    */   
/* 487:    */   private String fixed_format(double paramDouble)
/* 488:    */   {
/* 489:495 */     String str = "";
/* 490:497 */     if (paramDouble > 9.223372036854776E+018D) {
/* 491:497 */       return exp_format(paramDouble);
/* 492:    */     }
/* 493:499 */     long l = (this.precision == 0 ? paramDouble + 0.5D : paramDouble);
/* 494:500 */     str = str + l;
/* 495:    */     
/* 496:502 */     double d = paramDouble - l;
/* 497:503 */     if ((d >= 1.0D) || (d < 0.0D)) {
/* 498:503 */       return exp_format(paramDouble);
/* 499:    */     }
/* 500:505 */     return str + frac_part(d);
/* 501:    */   }
/* 502:    */   
/* 503:    */   private String frac_part(double paramDouble)
/* 504:    */   {
/* 505:510 */     String str1 = "";
/* 506:511 */     if (this.precision > 0)
/* 507:    */     {
/* 508:512 */       double d = 1.0D;
/* 509:513 */       String str2 = "";
/* 510:514 */       for (int j = 1; (j <= this.precision) && (d <= 9.223372036854776E+018D); j++)
/* 511:    */       {
/* 512:515 */         d *= 10.0D;
/* 513:516 */         str2 = str2 + "0";
/* 514:    */       }
/* 515:518 */       long l = (d * paramDouble + 0.5D);
/* 516:    */       
/* 517:520 */       str1 = str2 + l;
/* 518:521 */       str1 = str1.substring(str1.length() - this.precision, str1.length());
/* 519:    */     }
/* 520:525 */     if ((this.precision > 0) || (this.alternate)) {
/* 521:525 */       str1 = "." + str1;
/* 522:    */     }
/* 523:526 */     if (((this.fmt == 'G') || (this.fmt == 'g')) && (!this.alternate))
/* 524:    */     {
/* 525:528 */       int i = str1.length() - 1;
/* 526:529 */       while ((i >= 0) && (str1.charAt(i) == '0')) {
/* 527:529 */         i--;
/* 528:    */       }
/* 529:530 */       if ((i >= 0) && (str1.charAt(i) == '.')) {
/* 530:530 */         i--;
/* 531:    */       }
/* 532:531 */       str1 = str1.substring(0, i + 1);
/* 533:    */     }
/* 534:533 */     return str1;
/* 535:    */   }
/* 536:    */   
/* 537:    */   private String exp_format(double paramDouble)
/* 538:    */   {
/* 539:537 */     String str1 = "";
/* 540:538 */     int i = 0;
/* 541:539 */     double d1 = paramDouble;
/* 542:540 */     double d2 = 1.0D;
/* 543:542 */     if (paramDouble == (0.0D / 0.0D)) {
/* 544:543 */       return "NaN";
/* 545:    */     }
/* 546:544 */     if (paramDouble == (-1.0D / 0.0D)) {
/* 547:545 */       return "-Inf";
/* 548:    */     }
/* 549:546 */     if (paramDouble == (1.0D / 0.0D)) {
/* 550:547 */       return "Inf";
/* 551:    */     }
/* 552:548 */     if (paramDouble == 0.0D)
/* 553:    */     {
/* 554:549 */       if ((this.fmt == 'e') || (this.fmt == 'E') || (this.fmt == 'g') || (this.fmt == 'G')) {
/* 555:550 */         return "0.000E+00";
/* 556:    */       }
/* 557:552 */       return "0.000";
/* 558:    */     }
/* 559:555 */     while (d1 > 10.0D)
/* 560:    */     {
/* 561:556 */       i++;
/* 562:557 */       d2 /= 10.0D;
/* 563:558 */       d1 /= 10.0D;
/* 564:    */     }
/* 565:560 */     while (d1 < 1.0D)
/* 566:    */     {
/* 567:561 */       i--;
/* 568:562 */       d2 *= 10.0D;
/* 569:563 */       d1 *= 10.0D;
/* 570:    */     }
/* 571:565 */     if (((this.fmt == 'g') || (this.fmt == 'G')) && (i >= -4) && (i < this.precision)) {
/* 572:566 */       return fixed_format(paramDouble);
/* 573:    */     }
/* 574:568 */     paramDouble *= d2;
/* 575:569 */     str1 = str1 + fixed_format(paramDouble);
/* 576:571 */     if ((this.fmt == 'e') || (this.fmt == 'g')) {
/* 577:572 */       str1 = str1 + "e";
/* 578:    */     } else {
/* 579:574 */       str1 = str1 + "E";
/* 580:    */     }
/* 581:576 */     String str2 = "000";
/* 582:577 */     if (i >= 0)
/* 583:    */     {
/* 584:578 */       str1 = str1 + "+";
/* 585:579 */       str2 = str2 + i;
/* 586:    */     }
/* 587:    */     else
/* 588:    */     {
/* 589:582 */       str1 = str1 + "-";
/* 590:583 */       str2 = str2 + -i;
/* 591:    */     }
/* 592:586 */     return str1 + str2.substring(str2.length() - 3, str2.length());
/* 593:    */   }
/* 594:    */   
/* 595:    */   public void setWidth(int paramInt)
/* 596:    */   {
/* 597:603 */     this.width = paramInt;
/* 598:    */   }
/* 599:    */   
/* 600:    */   public void setPrecision(int paramInt)
/* 601:    */   {
/* 602:609 */     this.precision = paramInt;
/* 603:    */   }
/* 604:    */   
/* 605:    */   public void setPre(String paramString)
/* 606:    */   {
/* 607:615 */     this.pre = paramString;
/* 608:    */   }
/* 609:    */   
/* 610:    */   public void setPost(String paramString)
/* 611:    */   {
/* 612:621 */     this.post = paramString;
/* 613:    */   }
/* 614:    */   
/* 615:    */   public void setLeadingZeroes(boolean paramBoolean)
/* 616:    */   {
/* 617:627 */     this.leading_zeroes = paramBoolean;
/* 618:    */   }
/* 619:    */   
/* 620:    */   public void setShowPlus(boolean paramBoolean)
/* 621:    */   {
/* 622:633 */     this.show_plus = paramBoolean;
/* 623:    */   }
/* 624:    */   
/* 625:    */   public void setAlternate(boolean paramBoolean)
/* 626:    */   {
/* 627:639 */     this.alternate = paramBoolean;
/* 628:    */   }
/* 629:    */   
/* 630:    */   public void setShowSpace(boolean paramBoolean)
/* 631:    */   {
/* 632:645 */     this.show_space = paramBoolean;
/* 633:    */   }
/* 634:    */   
/* 635:    */   public void setLeftAlign(boolean paramBoolean)
/* 636:    */   {
/* 637:651 */     this.left_align = paramBoolean;
/* 638:    */   }
/* 639:    */   
/* 640:    */   public void setFmt(char paramChar)
/* 641:    */   {
/* 642:657 */     this.fmt = paramChar;
/* 643:    */   }
/* 644:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.j_paine.formatter.CJFormat
 * JD-Core Version:    0.7.0.1
 */