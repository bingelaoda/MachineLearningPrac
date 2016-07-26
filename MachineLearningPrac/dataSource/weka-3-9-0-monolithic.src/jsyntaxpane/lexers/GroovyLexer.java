/*    1:     */ package jsyntaxpane.lexers;
/*    2:     */ 
/*    3:     */ import java.io.IOException;
/*    4:     */ import java.io.InputStream;
/*    5:     */ import java.io.InputStreamReader;
/*    6:     */ import java.io.Reader;
/*    7:     */ import jsyntaxpane.Token;
/*    8:     */ import jsyntaxpane.TokenType;
/*    9:     */ 
/*   10:     */ public final class GroovyLexer
/*   11:     */   extends DefaultJFlexLexer
/*   12:     */ {
/*   13:     */   public static final int YYEOF = -1;
/*   14:     */   private static final int ZZ_BUFFERSIZE = 16384;
/*   15:     */   public static final int JDOC = 10;
/*   16:     */   public static final int REGEX = 6;
/*   17:     */   public static final int STRING = 2;
/*   18:     */   public static final int GSTRING_EXPR = 8;
/*   19:     */   public static final int CHARLITERAL = 4;
/*   20:     */   public static final int ML_STRING = 14;
/*   21:     */   public static final int YYINITIAL = 0;
/*   22:     */   public static final int JDOC_TAG = 12;
/*   23:     */   public static final int ML_STRING_EXPR = 16;
/*   24:  55 */   private static final int[] ZZ_LEXSTATE = { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8 };
/*   25:     */   private static final String ZZ_CMAP_PACKED = "";
/*   26: 160 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*   27: 165 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*   28:     */   private static final String ZZ_ACTION_PACKED_0 = "";
/*   29:     */   
/*   30:     */   private static int[] zzUnpackAction()
/*   31:     */   {
/*   32: 186 */     int[] result = new int[540];
/*   33: 187 */     int offset = 0;
/*   34: 188 */     offset = zzUnpackAction("", offset, result);
/*   35: 189 */     return result;
/*   36:     */   }
/*   37:     */   
/*   38:     */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*   39:     */   {
/*   40: 193 */     int i = 0;
/*   41: 194 */     int j = offset;
/*   42: 195 */     int l = packed.length();
/*   43:     */     int count;
/*   44: 196 */     for (; i < l; count > 0)
/*   45:     */     {
/*   46: 197 */       count = packed.charAt(i++);
/*   47: 198 */       int value = packed.charAt(i++);
/*   48: 199 */       result[(j++)] = value;count--;
/*   49:     */     }
/*   50: 201 */     return j;
/*   51:     */   }
/*   52:     */   
/*   53: 208 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*   54:     */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*   55:     */   
/*   56:     */   private static int[] zzUnpackRowMap()
/*   57:     */   {
/*   58: 281 */     int[] result = new int[540];
/*   59: 282 */     int offset = 0;
/*   60: 283 */     offset = zzUnpackRowMap("", offset, result);
/*   61: 284 */     return result;
/*   62:     */   }
/*   63:     */   
/*   64:     */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*   65:     */   {
/*   66: 288 */     int i = 0;
/*   67: 289 */     int j = offset;
/*   68: 290 */     int l = packed.length();
/*   69: 291 */     while (i < l)
/*   70:     */     {
/*   71: 292 */       int high = packed.charAt(i++) << '\020';
/*   72: 293 */       result[(j++)] = (high | packed.charAt(i++));
/*   73:     */     }
/*   74: 295 */     return j;
/*   75:     */   }
/*   76:     */   
/*   77: 301 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*   78:     */   private static final String ZZ_TRANS_PACKED_0 = "";
/*   79:     */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*   80:     */   private static final int ZZ_NO_MATCH = 1;
/*   81:     */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*   82:     */   
/*   83:     */   private static int[] zzUnpackTrans()
/*   84:     */   {
/*   85:1069 */     int[] result = new int[40905];
/*   86:1070 */     int offset = 0;
/*   87:1071 */     offset = zzUnpackTrans("", offset, result);
/*   88:1072 */     return result;
/*   89:     */   }
/*   90:     */   
/*   91:     */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*   92:     */   {
/*   93:1076 */     int i = 0;
/*   94:1077 */     int j = offset;
/*   95:1078 */     int l = packed.length();
/*   96:     */     int count;
/*   97:1079 */     for (; i < l; count > 0)
/*   98:     */     {
/*   99:1080 */       count = packed.charAt(i++);
/*  100:1081 */       int value = packed.charAt(i++);
/*  101:1082 */       value--;
/*  102:1083 */       result[(j++)] = value;count--;
/*  103:     */     }
/*  104:1085 */     return j;
/*  105:     */   }
/*  106:     */   
/*  107:1095 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/*  108:1104 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/*  109:     */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/*  110:     */   private Reader zzReader;
/*  111:     */   private int zzState;
/*  112:     */   
/*  113:     */   private static int[] zzUnpackAttribute()
/*  114:     */   {
/*  115:1117 */     int[] result = new int[540];
/*  116:1118 */     int offset = 0;
/*  117:1119 */     offset = zzUnpackAttribute("", offset, result);
/*  118:1120 */     return result;
/*  119:     */   }
/*  120:     */   
/*  121:     */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/*  122:     */   {
/*  123:1124 */     int i = 0;
/*  124:1125 */     int j = offset;
/*  125:1126 */     int l = packed.length();
/*  126:     */     int count;
/*  127:1127 */     for (; i < l; count > 0)
/*  128:     */     {
/*  129:1128 */       count = packed.charAt(i++);
/*  130:1129 */       int value = packed.charAt(i++);
/*  131:1130 */       result[(j++)] = value;count--;
/*  132:     */     }
/*  133:1132 */     return j;
/*  134:     */   }
/*  135:     */   
/*  136:1142 */   private int zzLexicalState = 0;
/*  137:1146 */   private char[] zzBuffer = new char[16384];
/*  138:     */   private int zzMarkedPos;
/*  139:     */   private int zzCurrentPos;
/*  140:     */   private int zzStartRead;
/*  141:     */   private int zzEndRead;
/*  142:     */   private int yyline;
/*  143:     */   private int yychar;
/*  144:     */   private int yycolumn;
/*  145:1176 */   private boolean zzAtBOL = true;
/*  146:     */   private boolean zzAtEOF;
/*  147:     */   private boolean zzEOFDone;
/*  148:     */   private static final byte PARAN = 1;
/*  149:     */   private static final byte BRACKET = 2;
/*  150:     */   private static final byte CURLY = 3;
/*  151:     */   
/*  152:     */   public GroovyLexer() {}
/*  153:     */   
/*  154:     */   public int yychar()
/*  155:     */   {
/*  156:1194 */     return this.yychar;
/*  157:     */   }
/*  158:     */   
/*  159:     */   public GroovyLexer(Reader in)
/*  160:     */   {
/*  161:1209 */     this.zzReader = in;
/*  162:     */   }
/*  163:     */   
/*  164:     */   public GroovyLexer(InputStream in)
/*  165:     */   {
/*  166:1219 */     this(new InputStreamReader(in));
/*  167:     */   }
/*  168:     */   
/*  169:     */   private static char[] zzUnpackCMap(String packed)
/*  170:     */   {
/*  171:1229 */     char[] map = new char[65536];
/*  172:1230 */     int i = 0;
/*  173:1231 */     int j = 0;
/*  174:     */     int count;
/*  175:1232 */     for (; i < 1828; count > 0)
/*  176:     */     {
/*  177:1233 */       count = packed.charAt(i++);
/*  178:1234 */       char value = packed.charAt(i++);
/*  179:1235 */       map[(j++)] = value;count--;
/*  180:     */     }
/*  181:1237 */     return map;
/*  182:     */   }
/*  183:     */   
/*  184:     */   private boolean zzRefill()
/*  185:     */     throws IOException
/*  186:     */   {
/*  187:1251 */     if (this.zzStartRead > 0)
/*  188:     */     {
/*  189:1252 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/*  190:     */       
/*  191:     */ 
/*  192:     */ 
/*  193:     */ 
/*  194:1257 */       this.zzEndRead -= this.zzStartRead;
/*  195:1258 */       this.zzCurrentPos -= this.zzStartRead;
/*  196:1259 */       this.zzMarkedPos -= this.zzStartRead;
/*  197:1260 */       this.zzStartRead = 0;
/*  198:     */     }
/*  199:1264 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/*  200:     */     {
/*  201:1266 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/*  202:1267 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/*  203:1268 */       this.zzBuffer = newBuffer;
/*  204:     */     }
/*  205:1272 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/*  206:1275 */     if (numRead > 0)
/*  207:     */     {
/*  208:1276 */       this.zzEndRead += numRead;
/*  209:1277 */       return false;
/*  210:     */     }
/*  211:1280 */     if (numRead == 0)
/*  212:     */     {
/*  213:1281 */       int c = this.zzReader.read();
/*  214:1282 */       if (c == -1) {
/*  215:1283 */         return true;
/*  216:     */       }
/*  217:1285 */       this.zzBuffer[(this.zzEndRead++)] = ((char)c);
/*  218:1286 */       return false;
/*  219:     */     }
/*  220:1291 */     return true;
/*  221:     */   }
/*  222:     */   
/*  223:     */   public final void yyclose()
/*  224:     */     throws IOException
/*  225:     */   {
/*  226:1299 */     this.zzAtEOF = true;
/*  227:1300 */     this.zzEndRead = this.zzStartRead;
/*  228:1302 */     if (this.zzReader != null) {
/*  229:1303 */       this.zzReader.close();
/*  230:     */     }
/*  231:     */   }
/*  232:     */   
/*  233:     */   public final void yyreset(Reader reader)
/*  234:     */   {
/*  235:1318 */     this.zzReader = reader;
/*  236:1319 */     this.zzAtBOL = true;
/*  237:1320 */     this.zzAtEOF = false;
/*  238:1321 */     this.zzEOFDone = false;
/*  239:1322 */     this.zzEndRead = (this.zzStartRead = 0);
/*  240:1323 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/*  241:1324 */     this.yyline = (this.yychar = this.yycolumn = 0);
/*  242:1325 */     this.zzLexicalState = 0;
/*  243:     */   }
/*  244:     */   
/*  245:     */   public final int yystate()
/*  246:     */   {
/*  247:1333 */     return this.zzLexicalState;
/*  248:     */   }
/*  249:     */   
/*  250:     */   public final void yybegin(int newState)
/*  251:     */   {
/*  252:1343 */     this.zzLexicalState = newState;
/*  253:     */   }
/*  254:     */   
/*  255:     */   public final String yytext()
/*  256:     */   {
/*  257:1351 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*  258:     */   }
/*  259:     */   
/*  260:     */   public final char yycharat(int pos)
/*  261:     */   {
/*  262:1367 */     return this.zzBuffer[(this.zzStartRead + pos)];
/*  263:     */   }
/*  264:     */   
/*  265:     */   public final int yylength()
/*  266:     */   {
/*  267:1375 */     return this.zzMarkedPos - this.zzStartRead;
/*  268:     */   }
/*  269:     */   
/*  270:     */   private void zzScanError(int errorCode)
/*  271:     */   {
/*  272:     */     String message;
/*  273:     */     try
/*  274:     */     {
/*  275:1396 */       message = ZZ_ERROR_MSG[errorCode];
/*  276:     */     }
/*  277:     */     catch (ArrayIndexOutOfBoundsException e)
/*  278:     */     {
/*  279:1399 */       message = ZZ_ERROR_MSG[0];
/*  280:     */     }
/*  281:1402 */     throw new Error(message);
/*  282:     */   }
/*  283:     */   
/*  284:     */   public void yypushback(int number)
/*  285:     */   {
/*  286:1415 */     if (number > yylength()) {
/*  287:1416 */       zzScanError(2);
/*  288:     */     }
/*  289:1418 */     this.zzMarkedPos -= number;
/*  290:     */   }
/*  291:     */   
/*  292:     */   public Token yylex()
/*  293:     */     throws IOException
/*  294:     */   {
/*  295:1436 */     int zzEndReadL = this.zzEndRead;
/*  296:1437 */     char[] zzBufferL = this.zzBuffer;
/*  297:1438 */     char[] zzCMapL = ZZ_CMAP;
/*  298:     */     
/*  299:1440 */     int[] zzTransL = ZZ_TRANS;
/*  300:1441 */     int[] zzRowMapL = ZZ_ROWMAP;
/*  301:1442 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/*  302:     */     for (;;)
/*  303:     */     {
/*  304:1445 */       int zzMarkedPosL = this.zzMarkedPos;
/*  305:     */       
/*  306:1447 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/*  307:     */       
/*  308:1449 */       int zzAction = -1;
/*  309:     */       
/*  310:1451 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/*  311:     */       
/*  312:1453 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/*  313:     */       int zzInput;
/*  314:     */       for (;;)
/*  315:     */       {
/*  316:     */         int zzInput;
/*  317:1459 */         if (zzCurrentPosL < zzEndReadL)
/*  318:     */         {
/*  319:1460 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  320:     */         }
/*  321:     */         else
/*  322:     */         {
/*  323:1461 */           if (this.zzAtEOF)
/*  324:     */           {
/*  325:1462 */             int zzInput = -1;
/*  326:1463 */             break;
/*  327:     */           }
/*  328:1467 */           this.zzCurrentPos = zzCurrentPosL;
/*  329:1468 */           this.zzMarkedPos = zzMarkedPosL;
/*  330:1469 */           boolean eof = zzRefill();
/*  331:     */           
/*  332:1471 */           zzCurrentPosL = this.zzCurrentPos;
/*  333:1472 */           zzMarkedPosL = this.zzMarkedPos;
/*  334:1473 */           zzBufferL = this.zzBuffer;
/*  335:1474 */           zzEndReadL = this.zzEndRead;
/*  336:1475 */           if (eof)
/*  337:     */           {
/*  338:1476 */             int zzInput = -1;
/*  339:1477 */             break;
/*  340:     */           }
/*  341:1480 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  342:     */         }
/*  343:1483 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/*  344:1484 */         if (zzNext == -1) {
/*  345:     */           break;
/*  346:     */         }
/*  347:1485 */         this.zzState = zzNext;
/*  348:     */         
/*  349:1487 */         int zzAttributes = zzAttrL[this.zzState];
/*  350:1488 */         if ((zzAttributes & 0x1) == 1)
/*  351:     */         {
/*  352:1489 */           zzAction = this.zzState;
/*  353:1490 */           zzMarkedPosL = zzCurrentPosL;
/*  354:1491 */           if ((zzAttributes & 0x8) == 8) {
/*  355:     */             break;
/*  356:     */           }
/*  357:     */         }
/*  358:     */       }
/*  359:1498 */       this.zzMarkedPos = zzMarkedPosL;
/*  360:1500 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/*  361:     */       {
/*  362:     */       case 26: 
/*  363:1502 */         yybegin(8);
/*  364:     */         
/*  365:1504 */         int s = this.tokenStart;
/*  366:1505 */         int l = this.tokenLength;
/*  367:1506 */         this.tokenStart = this.yychar;
/*  368:1507 */         this.tokenLength = 2;
/*  369:1508 */         return token(TokenType.STRING, s, l);
/*  370:     */       case 36: 
/*  371:     */         break;
/*  372:     */       case 18: 
/*  373:1512 */         yybegin(0);
/*  374:     */         
/*  375:1514 */         return token(TokenType.REGEX, this.tokenStart, this.tokenLength + 1);
/*  376:     */       case 37: 
/*  377:     */         break;
/*  378:     */       case 9: 
/*  379:1518 */         return token(TokenType.OPERATOR, -1);
/*  380:     */       case 38: 
/*  381:     */         break;
/*  382:     */       case 25: 
/*  383:1522 */         return token(TokenType.KEYWORD);
/*  384:     */       case 39: 
/*  385:     */         break;
/*  386:     */       case 28: 
/*  387:1526 */         yybegin(0);
/*  388:1527 */         return token(TokenType.COMMENT, this.tokenStart, this.tokenLength + 2);
/*  389:     */       case 40: 
/*  390:     */         break;
/*  391:     */       case 5: 
/*  392:1531 */         return token(TokenType.NUMBER);
/*  393:     */       case 41: 
/*  394:     */         break;
/*  395:     */       case 3: 
/*  396:1535 */         return token(TokenType.OPERATOR);
/*  397:     */       case 42: 
/*  398:     */         break;
/*  399:     */       case 10: 
/*  400:1539 */         return token(TokenType.OPERATOR, 3);
/*  401:     */       case 43: 
/*  402:     */         break;
/*  403:     */       case 11: 
/*  404:1543 */         return token(TokenType.OPERATOR, -3);
/*  405:     */       case 44: 
/*  406:     */         break;
/*  407:     */       case 33: 
/*  408:1547 */         return token(TokenType.REGEX);
/*  409:     */       case 45: 
/*  410:     */         break;
/*  411:     */       case 21: 
/*  412:1551 */         yybegin(12);
/*  413:1552 */         int start = this.tokenStart;
/*  414:1553 */         this.tokenStart = this.yychar;
/*  415:1554 */         int len = this.tokenLength;
/*  416:1555 */         this.tokenLength = 1;
/*  417:1556 */         return token(TokenType.COMMENT, start, len);
/*  418:     */       case 46: 
/*  419:     */         break;
/*  420:     */       case 16: 
/*  421:1560 */         yybegin(0);
/*  422:     */         
/*  423:1562 */         return token(TokenType.STRING, this.tokenStart, this.tokenLength + 1);
/*  424:     */       case 47: 
/*  425:     */         break;
/*  426:     */       case 31: 
/*  427:1566 */         yybegin(14);
/*  428:1567 */         this.tokenStart = this.yychar;
/*  429:1568 */         this.tokenLength = 3;
/*  430:     */       case 48: 
/*  431:     */         break;
/*  432:     */       case 23: 
/*  433:1572 */         yybegin(14);
/*  434:     */         
/*  435:1574 */         int s = this.tokenStart;
/*  436:1575 */         int l = this.tokenLength + 1;
/*  437:1576 */         this.tokenStart = (this.yychar + 1);
/*  438:1577 */         this.tokenLength = 0;
/*  439:1578 */         return token(TokenType.STRING2, s, l);
/*  440:     */       case 49: 
/*  441:     */         break;
/*  442:     */       case 14: 
/*  443:1582 */         this.tokenLength += yylength();
/*  444:     */       case 50: 
/*  445:     */         break;
/*  446:     */       case 15: 
/*  447:1586 */         yybegin(0);
/*  448:     */       case 51: 
/*  449:     */         break;
/*  450:     */       case 7: 
/*  451:1590 */         yybegin(4);
/*  452:1591 */         this.tokenStart = this.yychar;
/*  453:1592 */         this.tokenLength = 1;
/*  454:     */       case 52: 
/*  455:     */         break;
/*  456:     */       case 17: 
/*  457:1596 */         yybegin(0);
/*  458:     */         
/*  459:1598 */         return token(TokenType.STRING, this.tokenStart, this.tokenLength + 1);
/*  460:     */       case 53: 
/*  461:     */         break;
/*  462:     */       case 34: 
/*  463:1602 */         return token(TokenType.TYPE);
/*  464:     */       case 54: 
/*  465:     */         break;
/*  466:     */       case 32: 
/*  467:1606 */         yybegin(0);
/*  468:     */         
/*  469:1608 */         return token(TokenType.STRING, this.tokenStart, this.tokenLength + 3);
/*  470:     */       case 55: 
/*  471:     */         break;
/*  472:     */       case 2: 
/*  473:     */       case 56: 
/*  474:     */         break;
/*  475:     */       case 13: 
/*  476:1616 */         return token(TokenType.OPERATOR, -2);
/*  477:     */       case 57: 
/*  478:     */         break;
/*  479:     */       case 8: 
/*  480:1620 */         return token(TokenType.OPERATOR, 1);
/*  481:     */       case 58: 
/*  482:     */         break;
/*  483:     */       case 4: 
/*  484:1624 */         return token(TokenType.IDENTIFIER);
/*  485:     */       case 59: 
/*  486:     */         break;
/*  487:     */       case 27: 
/*  488:1628 */         this.tokenLength += 2;
/*  489:     */       case 60: 
/*  490:     */         break;
/*  491:     */       case 35: 
/*  492:1632 */         return token(TokenType.TYPE2);
/*  493:     */       case 61: 
/*  494:     */         break;
/*  495:     */       case 30: 
/*  496:1636 */         yybegin(10);
/*  497:1637 */         this.tokenStart = this.yychar;
/*  498:1638 */         this.tokenLength = 3;
/*  499:     */       case 62: 
/*  500:     */         break;
/*  501:     */       case 19: 
/*  502:1642 */         this.tokenLength += 1;
/*  503:     */       case 63: 
/*  504:     */         break;
/*  505:     */       case 12: 
/*  506:1646 */         return token(TokenType.OPERATOR, 2);
/*  507:     */       case 64: 
/*  508:     */         break;
/*  509:     */       case 24: 
/*  510:1650 */         return token(TokenType.COMMENT);
/*  511:     */       case 65: 
/*  512:     */         break;
/*  513:     */       case 6: 
/*  514:1654 */         yybegin(2);
/*  515:1655 */         this.tokenStart = this.yychar;
/*  516:1656 */         this.tokenLength = 1;
/*  517:     */       case 66: 
/*  518:     */         break;
/*  519:     */       case 29: 
/*  520:1660 */         yybegin(16);
/*  521:     */         
/*  522:1662 */         int s = this.tokenStart;
/*  523:1663 */         int l = this.tokenLength;
/*  524:1664 */         this.tokenStart = this.yychar;
/*  525:1665 */         this.tokenLength = 2;
/*  526:1666 */         return token(TokenType.STRING, s, l);
/*  527:     */       case 67: 
/*  528:     */         break;
/*  529:     */       case 22: 
/*  530:1670 */         yybegin(10);
/*  531:     */         
/*  532:1672 */         int start = this.tokenStart;
/*  533:1673 */         this.tokenStart = this.yychar;
/*  534:1674 */         int len = this.tokenLength;
/*  535:1675 */         this.tokenLength = 1;
/*  536:1676 */         return token(TokenType.COMMENT2, start, len);
/*  537:     */       case 68: 
/*  538:     */         break;
/*  539:     */       case 20: 
/*  540:1680 */         yybegin(2);
/*  541:     */         
/*  542:1682 */         int s = this.tokenStart;
/*  543:1683 */         int l = this.tokenLength + 1;
/*  544:1684 */         this.tokenStart = (this.yychar + 1);
/*  545:1685 */         this.tokenLength = 0;
/*  546:1686 */         return token(TokenType.STRING2, s, l);
/*  547:     */       case 69: 
/*  548:     */         break;
/*  549:     */       case 1: 
/*  550:     */       case 70: 
/*  551:     */         break;
/*  552:     */       default: 
/*  553:1694 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos))
/*  554:     */         {
/*  555:1695 */           this.zzAtEOF = true;
/*  556:     */           
/*  557:1697 */           return null;
/*  558:     */         }
/*  559:1701 */         zzScanError(1);
/*  560:     */       }
/*  561:     */     }
/*  562:     */   }
/*  563:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.GroovyLexer
 * JD-Core Version:    0.7.0.1
 */