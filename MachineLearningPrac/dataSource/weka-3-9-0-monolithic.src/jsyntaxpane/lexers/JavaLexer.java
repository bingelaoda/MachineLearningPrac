/*    1:     */ package jsyntaxpane.lexers;
/*    2:     */ 
/*    3:     */ import java.io.IOException;
/*    4:     */ import java.io.InputStream;
/*    5:     */ import java.io.InputStreamReader;
/*    6:     */ import java.io.Reader;
/*    7:     */ import jsyntaxpane.Token;
/*    8:     */ import jsyntaxpane.TokenType;
/*    9:     */ 
/*   10:     */ public final class JavaLexer
/*   11:     */   extends DefaultJFlexLexer
/*   12:     */ {
/*   13:     */   public static final int YYEOF = -1;
/*   14:     */   private static final int ZZ_BUFFERSIZE = 16384;
/*   15:     */   public static final int STRING = 2;
/*   16:     */   public static final int JDOC_TAG = 8;
/*   17:     */   public static final int JDOC = 6;
/*   18:     */   public static final int YYINITIAL = 0;
/*   19:     */   public static final int CHARLITERAL = 4;
/*   20:  51 */   private static final int[] ZZ_LEXSTATE = { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4 };
/*   21:     */   private static final String ZZ_CMAP_PACKED = "";
/*   22: 155 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*   23: 160 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*   24:     */   private static final String ZZ_ACTION_PACKED_0 = "";
/*   25:     */   
/*   26:     */   private static int[] zzUnpackAction()
/*   27:     */   {
/*   28: 179 */     int[] result = new int[514];
/*   29: 180 */     int offset = 0;
/*   30: 181 */     offset = zzUnpackAction("", offset, result);
/*   31: 182 */     return result;
/*   32:     */   }
/*   33:     */   
/*   34:     */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*   35:     */   {
/*   36: 186 */     int i = 0;
/*   37: 187 */     int j = offset;
/*   38: 188 */     int l = packed.length();
/*   39:     */     int count;
/*   40: 189 */     for (; i < l; count > 0)
/*   41:     */     {
/*   42: 190 */       count = packed.charAt(i++);
/*   43: 191 */       int value = packed.charAt(i++);
/*   44: 192 */       result[(j++)] = value;count--;
/*   45:     */     }
/*   46: 194 */     return j;
/*   47:     */   }
/*   48:     */   
/*   49: 201 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*   50:     */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*   51:     */   
/*   52:     */   private static int[] zzUnpackRowMap()
/*   53:     */   {
/*   54: 271 */     int[] result = new int[514];
/*   55: 272 */     int offset = 0;
/*   56: 273 */     offset = zzUnpackRowMap("", offset, result);
/*   57: 274 */     return result;
/*   58:     */   }
/*   59:     */   
/*   60:     */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*   61:     */   {
/*   62: 278 */     int i = 0;
/*   63: 279 */     int j = offset;
/*   64: 280 */     int l = packed.length();
/*   65: 281 */     while (i < l)
/*   66:     */     {
/*   67: 282 */       int high = packed.charAt(i++) << '\020';
/*   68: 283 */       result[(j++)] = (high | packed.charAt(i++));
/*   69:     */     }
/*   70: 285 */     return j;
/*   71:     */   }
/*   72:     */   
/*   73: 291 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*   74:     */   private static final String ZZ_TRANS_PACKED_0 = "";
/*   75:     */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*   76:     */   private static final int ZZ_NO_MATCH = 1;
/*   77:     */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*   78:     */   
/*   79:     */   private static int[] zzUnpackTrans()
/*   80:     */   {
/*   81: 945 */     int[] result = new int[39447];
/*   82: 946 */     int offset = 0;
/*   83: 947 */     offset = zzUnpackTrans("", offset, result);
/*   84: 948 */     return result;
/*   85:     */   }
/*   86:     */   
/*   87:     */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*   88:     */   {
/*   89: 952 */     int i = 0;
/*   90: 953 */     int j = offset;
/*   91: 954 */     int l = packed.length();
/*   92:     */     int count;
/*   93: 955 */     for (; i < l; count > 0)
/*   94:     */     {
/*   95: 956 */       count = packed.charAt(i++);
/*   96: 957 */       int value = packed.charAt(i++);
/*   97: 958 */       value--;
/*   98: 959 */       result[(j++)] = value;count--;
/*   99:     */     }
/*  100: 961 */     return j;
/*  101:     */   }
/*  102:     */   
/*  103: 971 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/*  104: 980 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/*  105:     */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/*  106:     */   private Reader zzReader;
/*  107:     */   private int zzState;
/*  108:     */   
/*  109:     */   private static int[] zzUnpackAttribute()
/*  110:     */   {
/*  111: 991 */     int[] result = new int[514];
/*  112: 992 */     int offset = 0;
/*  113: 993 */     offset = zzUnpackAttribute("", offset, result);
/*  114: 994 */     return result;
/*  115:     */   }
/*  116:     */   
/*  117:     */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/*  118:     */   {
/*  119: 998 */     int i = 0;
/*  120: 999 */     int j = offset;
/*  121:1000 */     int l = packed.length();
/*  122:     */     int count;
/*  123:1001 */     for (; i < l; count > 0)
/*  124:     */     {
/*  125:1002 */       count = packed.charAt(i++);
/*  126:1003 */       int value = packed.charAt(i++);
/*  127:1004 */       result[(j++)] = value;count--;
/*  128:     */     }
/*  129:1006 */     return j;
/*  130:     */   }
/*  131:     */   
/*  132:1016 */   private int zzLexicalState = 0;
/*  133:1020 */   private char[] zzBuffer = new char[16384];
/*  134:     */   private int zzMarkedPos;
/*  135:     */   private int zzCurrentPos;
/*  136:     */   private int zzStartRead;
/*  137:     */   private int zzEndRead;
/*  138:     */   private int yyline;
/*  139:     */   private int yychar;
/*  140:     */   private int yycolumn;
/*  141:1050 */   private boolean zzAtBOL = true;
/*  142:     */   private boolean zzAtEOF;
/*  143:     */   private boolean zzEOFDone;
/*  144:     */   private static final byte PARAN = 1;
/*  145:     */   private static final byte BRACKET = 2;
/*  146:     */   private static final byte CURLY = 3;
/*  147:     */   
/*  148:     */   public JavaLexer() {}
/*  149:     */   
/*  150:     */   public int yychar()
/*  151:     */   {
/*  152:1069 */     return this.yychar;
/*  153:     */   }
/*  154:     */   
/*  155:     */   public JavaLexer(Reader in)
/*  156:     */   {
/*  157:1085 */     this.zzReader = in;
/*  158:     */   }
/*  159:     */   
/*  160:     */   public JavaLexer(InputStream in)
/*  161:     */   {
/*  162:1095 */     this(new InputStreamReader(in));
/*  163:     */   }
/*  164:     */   
/*  165:     */   private static char[] zzUnpackCMap(String packed)
/*  166:     */   {
/*  167:1105 */     char[] map = new char[65536];
/*  168:1106 */     int i = 0;
/*  169:1107 */     int j = 0;
/*  170:     */     int count;
/*  171:1108 */     for (; i < 1830; count > 0)
/*  172:     */     {
/*  173:1109 */       count = packed.charAt(i++);
/*  174:1110 */       char value = packed.charAt(i++);
/*  175:1111 */       map[(j++)] = value;count--;
/*  176:     */     }
/*  177:1113 */     return map;
/*  178:     */   }
/*  179:     */   
/*  180:     */   private boolean zzRefill()
/*  181:     */     throws IOException
/*  182:     */   {
/*  183:1127 */     if (this.zzStartRead > 0)
/*  184:     */     {
/*  185:1128 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/*  186:     */       
/*  187:     */ 
/*  188:     */ 
/*  189:     */ 
/*  190:1133 */       this.zzEndRead -= this.zzStartRead;
/*  191:1134 */       this.zzCurrentPos -= this.zzStartRead;
/*  192:1135 */       this.zzMarkedPos -= this.zzStartRead;
/*  193:1136 */       this.zzStartRead = 0;
/*  194:     */     }
/*  195:1140 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/*  196:     */     {
/*  197:1142 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/*  198:1143 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/*  199:1144 */       this.zzBuffer = newBuffer;
/*  200:     */     }
/*  201:1148 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/*  202:1151 */     if (numRead > 0)
/*  203:     */     {
/*  204:1152 */       this.zzEndRead += numRead;
/*  205:1153 */       return false;
/*  206:     */     }
/*  207:1156 */     if (numRead == 0)
/*  208:     */     {
/*  209:1157 */       int c = this.zzReader.read();
/*  210:1158 */       if (c == -1) {
/*  211:1159 */         return true;
/*  212:     */       }
/*  213:1161 */       this.zzBuffer[(this.zzEndRead++)] = ((char)c);
/*  214:1162 */       return false;
/*  215:     */     }
/*  216:1167 */     return true;
/*  217:     */   }
/*  218:     */   
/*  219:     */   public final void yyclose()
/*  220:     */     throws IOException
/*  221:     */   {
/*  222:1175 */     this.zzAtEOF = true;
/*  223:1176 */     this.zzEndRead = this.zzStartRead;
/*  224:1178 */     if (this.zzReader != null) {
/*  225:1179 */       this.zzReader.close();
/*  226:     */     }
/*  227:     */   }
/*  228:     */   
/*  229:     */   public final void yyreset(Reader reader)
/*  230:     */   {
/*  231:1194 */     this.zzReader = reader;
/*  232:1195 */     this.zzAtBOL = true;
/*  233:1196 */     this.zzAtEOF = false;
/*  234:1197 */     this.zzEOFDone = false;
/*  235:1198 */     this.zzEndRead = (this.zzStartRead = 0);
/*  236:1199 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/*  237:1200 */     this.yyline = (this.yychar = this.yycolumn = 0);
/*  238:1201 */     this.zzLexicalState = 0;
/*  239:     */   }
/*  240:     */   
/*  241:     */   public final int yystate()
/*  242:     */   {
/*  243:1209 */     return this.zzLexicalState;
/*  244:     */   }
/*  245:     */   
/*  246:     */   public final void yybegin(int newState)
/*  247:     */   {
/*  248:1219 */     this.zzLexicalState = newState;
/*  249:     */   }
/*  250:     */   
/*  251:     */   public final String yytext()
/*  252:     */   {
/*  253:1227 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*  254:     */   }
/*  255:     */   
/*  256:     */   public final char yycharat(int pos)
/*  257:     */   {
/*  258:1243 */     return this.zzBuffer[(this.zzStartRead + pos)];
/*  259:     */   }
/*  260:     */   
/*  261:     */   public final int yylength()
/*  262:     */   {
/*  263:1251 */     return this.zzMarkedPos - this.zzStartRead;
/*  264:     */   }
/*  265:     */   
/*  266:     */   private void zzScanError(int errorCode)
/*  267:     */   {
/*  268:     */     String message;
/*  269:     */     try
/*  270:     */     {
/*  271:1272 */       message = ZZ_ERROR_MSG[errorCode];
/*  272:     */     }
/*  273:     */     catch (ArrayIndexOutOfBoundsException e)
/*  274:     */     {
/*  275:1275 */       message = ZZ_ERROR_MSG[0];
/*  276:     */     }
/*  277:1278 */     throw new Error(message);
/*  278:     */   }
/*  279:     */   
/*  280:     */   public void yypushback(int number)
/*  281:     */   {
/*  282:1291 */     if (number > yylength()) {
/*  283:1292 */       zzScanError(2);
/*  284:     */     }
/*  285:1294 */     this.zzMarkedPos -= number;
/*  286:     */   }
/*  287:     */   
/*  288:     */   public Token yylex()
/*  289:     */     throws IOException
/*  290:     */   {
/*  291:1312 */     int zzEndReadL = this.zzEndRead;
/*  292:1313 */     char[] zzBufferL = this.zzBuffer;
/*  293:1314 */     char[] zzCMapL = ZZ_CMAP;
/*  294:     */     
/*  295:1316 */     int[] zzTransL = ZZ_TRANS;
/*  296:1317 */     int[] zzRowMapL = ZZ_ROWMAP;
/*  297:1318 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/*  298:     */     for (;;)
/*  299:     */     {
/*  300:1321 */       int zzMarkedPosL = this.zzMarkedPos;
/*  301:     */       
/*  302:1323 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/*  303:     */       
/*  304:1325 */       int zzAction = -1;
/*  305:     */       
/*  306:1327 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/*  307:     */       
/*  308:1329 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/*  309:     */       int zzInput;
/*  310:     */       for (;;)
/*  311:     */       {
/*  312:     */         int zzInput;
/*  313:1335 */         if (zzCurrentPosL < zzEndReadL)
/*  314:     */         {
/*  315:1336 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  316:     */         }
/*  317:     */         else
/*  318:     */         {
/*  319:1337 */           if (this.zzAtEOF)
/*  320:     */           {
/*  321:1338 */             int zzInput = -1;
/*  322:1339 */             break;
/*  323:     */           }
/*  324:1343 */           this.zzCurrentPos = zzCurrentPosL;
/*  325:1344 */           this.zzMarkedPos = zzMarkedPosL;
/*  326:1345 */           boolean eof = zzRefill();
/*  327:     */           
/*  328:1347 */           zzCurrentPosL = this.zzCurrentPos;
/*  329:1348 */           zzMarkedPosL = this.zzMarkedPos;
/*  330:1349 */           zzBufferL = this.zzBuffer;
/*  331:1350 */           zzEndReadL = this.zzEndRead;
/*  332:1351 */           if (eof)
/*  333:     */           {
/*  334:1352 */             int zzInput = -1;
/*  335:1353 */             break;
/*  336:     */           }
/*  337:1356 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  338:     */         }
/*  339:1359 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/*  340:1360 */         if (zzNext == -1) {
/*  341:     */           break;
/*  342:     */         }
/*  343:1361 */         this.zzState = zzNext;
/*  344:     */         
/*  345:1363 */         int zzAttributes = zzAttrL[this.zzState];
/*  346:1364 */         if ((zzAttributes & 0x1) == 1)
/*  347:     */         {
/*  348:1365 */           zzAction = this.zzState;
/*  349:1366 */           zzMarkedPosL = zzCurrentPosL;
/*  350:1367 */           if ((zzAttributes & 0x8) == 8) {
/*  351:     */             break;
/*  352:     */           }
/*  353:     */         }
/*  354:     */       }
/*  355:1374 */       this.zzMarkedPos = zzMarkedPosL;
/*  356:1376 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/*  357:     */       {
/*  358:     */       case 8: 
/*  359:1378 */         return token(TokenType.OPERATOR, -1);
/*  360:     */       case 28: 
/*  361:     */         break;
/*  362:     */       case 20: 
/*  363:1382 */         return token(TokenType.KEYWORD);
/*  364:     */       case 29: 
/*  365:     */         break;
/*  366:     */       case 4: 
/*  367:1386 */         return token(TokenType.NUMBER);
/*  368:     */       case 30: 
/*  369:     */         break;
/*  370:     */       case 18: 
/*  371:1390 */         yybegin(6);
/*  372:     */         
/*  373:1392 */         int start = this.tokenStart;
/*  374:1393 */         this.tokenStart = this.yychar;
/*  375:1394 */         int len = this.tokenLength;
/*  376:1395 */         this.tokenLength = 1;
/*  377:1396 */         return token(TokenType.COMMENT2, start, len);
/*  378:     */       case 31: 
/*  379:     */         break;
/*  380:     */       case 22: 
/*  381:1400 */         yybegin(0);
/*  382:1401 */         return token(TokenType.COMMENT, this.tokenStart, this.tokenLength + 2);
/*  383:     */       case 32: 
/*  384:     */         break;
/*  385:     */       case 2: 
/*  386:1405 */         return token(TokenType.OPERATOR);
/*  387:     */       case 33: 
/*  388:     */         break;
/*  389:     */       case 9: 
/*  390:1409 */         return token(TokenType.OPERATOR, 3);
/*  391:     */       case 34: 
/*  392:     */         break;
/*  393:     */       case 10: 
/*  394:1413 */         return token(TokenType.OPERATOR, -3);
/*  395:     */       case 35: 
/*  396:     */         break;
/*  397:     */       case 17: 
/*  398:1417 */         yybegin(8);
/*  399:1418 */         int start = this.tokenStart;
/*  400:1419 */         this.tokenStart = this.yychar;
/*  401:1420 */         int len = this.tokenLength;
/*  402:1421 */         this.tokenLength = 1;
/*  403:1422 */         return token(TokenType.COMMENT, start, len);
/*  404:     */       case 36: 
/*  405:     */         break;
/*  406:     */       case 13: 
/*  407:1426 */         this.tokenLength += yylength();
/*  408:     */       case 37: 
/*  409:     */         break;
/*  410:     */       case 14: 
/*  411:1430 */         yybegin(0);
/*  412:     */       case 38: 
/*  413:     */         break;
/*  414:     */       case 6: 
/*  415:1434 */         yybegin(4);
/*  416:1435 */         this.tokenStart = this.yychar;
/*  417:1436 */         this.tokenLength = 1;
/*  418:     */       case 39: 
/*  419:     */         break;
/*  420:     */       case 23: 
/*  421:1440 */         yybegin(6);
/*  422:1441 */         this.tokenStart = this.yychar;
/*  423:1442 */         this.tokenLength = 3;
/*  424:     */       case 40: 
/*  425:     */         break;
/*  426:     */       case 15: 
/*  427:1446 */         yybegin(0);
/*  428:     */         
/*  429:1448 */         return token(TokenType.STRING, this.tokenStart, this.tokenLength + 1);
/*  430:     */       case 41: 
/*  431:     */         break;
/*  432:     */       case 24: 
/*  433:1452 */         return token(TokenType.TYPE);
/*  434:     */       case 42: 
/*  435:     */         break;
/*  436:     */       case 26: 
/*  437:1456 */         return token(TokenType.WARNING);
/*  438:     */       case 43: 
/*  439:     */         break;
/*  440:     */       case 12: 
/*  441:1460 */         return token(TokenType.OPERATOR, -2);
/*  442:     */       case 44: 
/*  443:     */         break;
/*  444:     */       case 7: 
/*  445:1464 */         return token(TokenType.OPERATOR, 1);
/*  446:     */       case 45: 
/*  447:     */         break;
/*  448:     */       case 3: 
/*  449:1468 */         return token(TokenType.IDENTIFIER);
/*  450:     */       case 46: 
/*  451:     */         break;
/*  452:     */       case 21: 
/*  453:1472 */         this.tokenLength += 2;
/*  454:     */       case 47: 
/*  455:     */         break;
/*  456:     */       case 27: 
/*  457:1476 */         return token(TokenType.TYPE2);
/*  458:     */       case 48: 
/*  459:     */         break;
/*  460:     */       case 25: 
/*  461:1480 */         return token(TokenType.ERROR);
/*  462:     */       case 49: 
/*  463:     */         break;
/*  464:     */       case 16: 
/*  465:1484 */         this.tokenLength += 1;
/*  466:     */       case 50: 
/*  467:     */         break;
/*  468:     */       case 11: 
/*  469:1488 */         return token(TokenType.OPERATOR, 2);
/*  470:     */       case 51: 
/*  471:     */         break;
/*  472:     */       case 19: 
/*  473:1492 */         return token(TokenType.COMMENT);
/*  474:     */       case 52: 
/*  475:     */         break;
/*  476:     */       case 5: 
/*  477:1496 */         yybegin(2);
/*  478:1497 */         this.tokenStart = this.yychar;
/*  479:1498 */         this.tokenLength = 1;
/*  480:     */       case 53: 
/*  481:     */         break;
/*  482:     */       case 1: 
/*  483:     */       case 54: 
/*  484:     */         break;
/*  485:     */       default: 
/*  486:1506 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos))
/*  487:     */         {
/*  488:1507 */           this.zzAtEOF = true;
/*  489:     */           
/*  490:1509 */           return null;
/*  491:     */         }
/*  492:1513 */         zzScanError(1);
/*  493:     */       }
/*  494:     */     }
/*  495:     */   }
/*  496:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.JavaLexer
 * JD-Core Version:    0.7.0.1
 */