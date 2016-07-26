/*    1:     */ package jsyntaxpane.lexers;
/*    2:     */ 
/*    3:     */ import java.io.IOException;
/*    4:     */ import java.io.InputStream;
/*    5:     */ import java.io.InputStreamReader;
/*    6:     */ import java.io.Reader;
/*    7:     */ import jsyntaxpane.Token;
/*    8:     */ import jsyntaxpane.TokenType;
/*    9:     */ 
/*   10:     */ public final class XHTMLLexer
/*   11:     */   extends DefaultJFlexLexer
/*   12:     */ {
/*   13:     */   public static final int YYEOF = -1;
/*   14:     */   private static final int ZZ_BUFFERSIZE = 16384;
/*   15:     */   public static final int INSTR = 8;
/*   16:     */   public static final int YYINITIAL = 0;
/*   17:     */   public static final int COMMENT = 2;
/*   18:     */   public static final int CDATA = 4;
/*   19:     */   public static final int TAG = 6;
/*   20:     */   public static final int DOCTYPE = 10;
/*   21:  52 */   private static final int[] ZZ_LEXSTATE = { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5 };
/*   22:     */   private static final String ZZ_CMAP_PACKED = "";
/*   23:  77 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*   24:  82 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*   25:     */   private static final String ZZ_ACTION_PACKED_0 = "";
/*   26:     */   
/*   27:     */   private static int[] zzUnpackAction()
/*   28:     */   {
/*   29:  95 */     int[] result = new int[498];
/*   30:  96 */     int offset = 0;
/*   31:  97 */     offset = zzUnpackAction("", offset, result);
/*   32:  98 */     return result;
/*   33:     */   }
/*   34:     */   
/*   35:     */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*   36:     */   {
/*   37: 102 */     int i = 0;
/*   38: 103 */     int j = offset;
/*   39: 104 */     int l = packed.length();
/*   40:     */     int count;
/*   41: 105 */     for (; i < l; count > 0)
/*   42:     */     {
/*   43: 106 */       count = packed.charAt(i++);
/*   44: 107 */       int value = packed.charAt(i++);
/*   45: 108 */       result[(j++)] = value;count--;
/*   46:     */     }
/*   47: 110 */     return j;
/*   48:     */   }
/*   49:     */   
/*   50: 117 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*   51:     */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*   52:     */   
/*   53:     */   private static int[] zzUnpackRowMap()
/*   54:     */   {
/*   55: 185 */     int[] result = new int[498];
/*   56: 186 */     int offset = 0;
/*   57: 187 */     offset = zzUnpackRowMap("", offset, result);
/*   58: 188 */     return result;
/*   59:     */   }
/*   60:     */   
/*   61:     */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*   62:     */   {
/*   63: 192 */     int i = 0;
/*   64: 193 */     int j = offset;
/*   65: 194 */     int l = packed.length();
/*   66: 195 */     while (i < l)
/*   67:     */     {
/*   68: 196 */       int high = packed.charAt(i++) << '\020';
/*   69: 197 */       result[(j++)] = (high | packed.charAt(i++));
/*   70:     */     }
/*   71: 199 */     return j;
/*   72:     */   }
/*   73:     */   
/*   74: 205 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*   75:     */   private static final String ZZ_TRANS_PACKED_0 = "";
/*   76:     */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*   77:     */   private static final int ZZ_NO_MATCH = 1;
/*   78:     */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*   79:     */   
/*   80:     */   private static int[] zzUnpackTrans()
/*   81:     */   {
/*   82: 960 */     int[] result = new int[35372];
/*   83: 961 */     int offset = 0;
/*   84: 962 */     offset = zzUnpackTrans("", offset, result);
/*   85: 963 */     return result;
/*   86:     */   }
/*   87:     */   
/*   88:     */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*   89:     */   {
/*   90: 967 */     int i = 0;
/*   91: 968 */     int j = offset;
/*   92: 969 */     int l = packed.length();
/*   93:     */     int count;
/*   94: 970 */     for (; i < l; count > 0)
/*   95:     */     {
/*   96: 971 */       count = packed.charAt(i++);
/*   97: 972 */       int value = packed.charAt(i++);
/*   98: 973 */       value--;
/*   99: 974 */       result[(j++)] = value;count--;
/*  100:     */     }
/*  101: 976 */     return j;
/*  102:     */   }
/*  103:     */   
/*  104: 986 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/*  105: 995 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/*  106:     */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/*  107:     */   private Reader zzReader;
/*  108:     */   private int zzState;
/*  109:     */   
/*  110:     */   private static int[] zzUnpackAttribute()
/*  111:     */   {
/*  112:1007 */     int[] result = new int[498];
/*  113:1008 */     int offset = 0;
/*  114:1009 */     offset = zzUnpackAttribute("", offset, result);
/*  115:1010 */     return result;
/*  116:     */   }
/*  117:     */   
/*  118:     */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/*  119:     */   {
/*  120:1014 */     int i = 0;
/*  121:1015 */     int j = offset;
/*  122:1016 */     int l = packed.length();
/*  123:     */     int count;
/*  124:1017 */     for (; i < l; count > 0)
/*  125:     */     {
/*  126:1018 */       count = packed.charAt(i++);
/*  127:1019 */       int value = packed.charAt(i++);
/*  128:1020 */       result[(j++)] = value;count--;
/*  129:     */     }
/*  130:1022 */     return j;
/*  131:     */   }
/*  132:     */   
/*  133:1032 */   private int zzLexicalState = 0;
/*  134:1036 */   private char[] zzBuffer = new char[16384];
/*  135:     */   private int zzMarkedPos;
/*  136:     */   private int zzCurrentPos;
/*  137:     */   private int zzStartRead;
/*  138:     */   private int zzEndRead;
/*  139:     */   private int yyline;
/*  140:     */   private int yychar;
/*  141:     */   private int yycolumn;
/*  142:1066 */   private boolean zzAtBOL = true;
/*  143:     */   private boolean zzAtEOF;
/*  144:     */   private boolean zzEOFDone;
/*  145:     */   private static final byte TAG_OPEN = 1;
/*  146:     */   private static final byte TAG_CLOSE = -1;
/*  147:     */   private static final byte INSTR_OPEN = 2;
/*  148:     */   private static final byte INSTR_CLOSE = -2;
/*  149:     */   private static final byte CDATA_OPEN = 3;
/*  150:     */   private static final byte CDATA_CLOSE = -3;
/*  151:     */   private static final byte COMMENT_OPEN = 4;
/*  152:     */   private static final byte COMMENT_CLOSE = -4;
/*  153:     */   
/*  154:     */   public XHTMLLexer() {}
/*  155:     */   
/*  156:     */   public int yychar()
/*  157:     */   {
/*  158:1085 */     return this.yychar;
/*  159:     */   }
/*  160:     */   
/*  161:     */   public XHTMLLexer(Reader in)
/*  162:     */   {
/*  163:1108 */     this.zzReader = in;
/*  164:     */   }
/*  165:     */   
/*  166:     */   public XHTMLLexer(InputStream in)
/*  167:     */   {
/*  168:1118 */     this(new InputStreamReader(in));
/*  169:     */   }
/*  170:     */   
/*  171:     */   private static char[] zzUnpackCMap(String packed)
/*  172:     */   {
/*  173:1128 */     char[] map = new char[65536];
/*  174:1129 */     int i = 0;
/*  175:1130 */     int j = 0;
/*  176:     */     int count;
/*  177:1131 */     for (; i < 256; count > 0)
/*  178:     */     {
/*  179:1132 */       count = packed.charAt(i++);
/*  180:1133 */       char value = packed.charAt(i++);
/*  181:1134 */       map[(j++)] = value;count--;
/*  182:     */     }
/*  183:1136 */     return map;
/*  184:     */   }
/*  185:     */   
/*  186:     */   private boolean zzRefill()
/*  187:     */     throws IOException
/*  188:     */   {
/*  189:1150 */     if (this.zzStartRead > 0)
/*  190:     */     {
/*  191:1151 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/*  192:     */       
/*  193:     */ 
/*  194:     */ 
/*  195:     */ 
/*  196:1156 */       this.zzEndRead -= this.zzStartRead;
/*  197:1157 */       this.zzCurrentPos -= this.zzStartRead;
/*  198:1158 */       this.zzMarkedPos -= this.zzStartRead;
/*  199:1159 */       this.zzStartRead = 0;
/*  200:     */     }
/*  201:1163 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/*  202:     */     {
/*  203:1165 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/*  204:1166 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/*  205:1167 */       this.zzBuffer = newBuffer;
/*  206:     */     }
/*  207:1171 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/*  208:1174 */     if (numRead > 0)
/*  209:     */     {
/*  210:1175 */       this.zzEndRead += numRead;
/*  211:1176 */       return false;
/*  212:     */     }
/*  213:1179 */     if (numRead == 0)
/*  214:     */     {
/*  215:1180 */       int c = this.zzReader.read();
/*  216:1181 */       if (c == -1) {
/*  217:1182 */         return true;
/*  218:     */       }
/*  219:1184 */       this.zzBuffer[(this.zzEndRead++)] = ((char)c);
/*  220:1185 */       return false;
/*  221:     */     }
/*  222:1190 */     return true;
/*  223:     */   }
/*  224:     */   
/*  225:     */   public final void yyclose()
/*  226:     */     throws IOException
/*  227:     */   {
/*  228:1198 */     this.zzAtEOF = true;
/*  229:1199 */     this.zzEndRead = this.zzStartRead;
/*  230:1201 */     if (this.zzReader != null) {
/*  231:1202 */       this.zzReader.close();
/*  232:     */     }
/*  233:     */   }
/*  234:     */   
/*  235:     */   public final void yyreset(Reader reader)
/*  236:     */   {
/*  237:1217 */     this.zzReader = reader;
/*  238:1218 */     this.zzAtBOL = true;
/*  239:1219 */     this.zzAtEOF = false;
/*  240:1220 */     this.zzEOFDone = false;
/*  241:1221 */     this.zzEndRead = (this.zzStartRead = 0);
/*  242:1222 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/*  243:1223 */     this.yyline = (this.yychar = this.yycolumn = 0);
/*  244:1224 */     this.zzLexicalState = 0;
/*  245:     */   }
/*  246:     */   
/*  247:     */   public final int yystate()
/*  248:     */   {
/*  249:1232 */     return this.zzLexicalState;
/*  250:     */   }
/*  251:     */   
/*  252:     */   public final void yybegin(int newState)
/*  253:     */   {
/*  254:1242 */     this.zzLexicalState = newState;
/*  255:     */   }
/*  256:     */   
/*  257:     */   public final String yytext()
/*  258:     */   {
/*  259:1250 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*  260:     */   }
/*  261:     */   
/*  262:     */   public final char yycharat(int pos)
/*  263:     */   {
/*  264:1266 */     return this.zzBuffer[(this.zzStartRead + pos)];
/*  265:     */   }
/*  266:     */   
/*  267:     */   public final int yylength()
/*  268:     */   {
/*  269:1274 */     return this.zzMarkedPos - this.zzStartRead;
/*  270:     */   }
/*  271:     */   
/*  272:     */   private void zzScanError(int errorCode)
/*  273:     */   {
/*  274:     */     String message;
/*  275:     */     try
/*  276:     */     {
/*  277:1295 */       message = ZZ_ERROR_MSG[errorCode];
/*  278:     */     }
/*  279:     */     catch (ArrayIndexOutOfBoundsException e)
/*  280:     */     {
/*  281:1298 */       message = ZZ_ERROR_MSG[0];
/*  282:     */     }
/*  283:1301 */     throw new Error(message);
/*  284:     */   }
/*  285:     */   
/*  286:     */   public void yypushback(int number)
/*  287:     */   {
/*  288:1314 */     if (number > yylength()) {
/*  289:1315 */       zzScanError(2);
/*  290:     */     }
/*  291:1317 */     this.zzMarkedPos -= number;
/*  292:     */   }
/*  293:     */   
/*  294:     */   public Token yylex()
/*  295:     */     throws IOException
/*  296:     */   {
/*  297:1335 */     int zzEndReadL = this.zzEndRead;
/*  298:1336 */     char[] zzBufferL = this.zzBuffer;
/*  299:1337 */     char[] zzCMapL = ZZ_CMAP;
/*  300:     */     
/*  301:1339 */     int[] zzTransL = ZZ_TRANS;
/*  302:1340 */     int[] zzRowMapL = ZZ_ROWMAP;
/*  303:1341 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/*  304:     */     for (;;)
/*  305:     */     {
/*  306:1344 */       int zzMarkedPosL = this.zzMarkedPos;
/*  307:     */       
/*  308:1346 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/*  309:     */       
/*  310:1348 */       int zzAction = -1;
/*  311:     */       
/*  312:1350 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/*  313:     */       
/*  314:1352 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/*  315:     */       int zzInput;
/*  316:     */       for (;;)
/*  317:     */       {
/*  318:     */         int zzInput;
/*  319:1358 */         if (zzCurrentPosL < zzEndReadL)
/*  320:     */         {
/*  321:1359 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  322:     */         }
/*  323:     */         else
/*  324:     */         {
/*  325:1360 */           if (this.zzAtEOF)
/*  326:     */           {
/*  327:1361 */             int zzInput = -1;
/*  328:1362 */             break;
/*  329:     */           }
/*  330:1366 */           this.zzCurrentPos = zzCurrentPosL;
/*  331:1367 */           this.zzMarkedPos = zzMarkedPosL;
/*  332:1368 */           boolean eof = zzRefill();
/*  333:     */           
/*  334:1370 */           zzCurrentPosL = this.zzCurrentPos;
/*  335:1371 */           zzMarkedPosL = this.zzMarkedPos;
/*  336:1372 */           zzBufferL = this.zzBuffer;
/*  337:1373 */           zzEndReadL = this.zzEndRead;
/*  338:1374 */           if (eof)
/*  339:     */           {
/*  340:1375 */             int zzInput = -1;
/*  341:1376 */             break;
/*  342:     */           }
/*  343:1379 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  344:     */         }
/*  345:1382 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/*  346:1383 */         if (zzNext == -1) {
/*  347:     */           break;
/*  348:     */         }
/*  349:1384 */         this.zzState = zzNext;
/*  350:     */         
/*  351:1386 */         int zzAttributes = zzAttrL[this.zzState];
/*  352:1387 */         if ((zzAttributes & 0x1) == 1)
/*  353:     */         {
/*  354:1388 */           zzAction = this.zzState;
/*  355:1389 */           zzMarkedPosL = zzCurrentPosL;
/*  356:1390 */           if ((zzAttributes & 0x8) == 8) {
/*  357:     */             break;
/*  358:     */           }
/*  359:     */         }
/*  360:     */       }
/*  361:1397 */       this.zzMarkedPos = zzMarkedPosL;
/*  362:1399 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/*  363:     */       {
/*  364:     */       case 14: 
/*  365:1401 */         return token(TokenType.KEYWORD, -1);
/*  366:     */       case 19: 
/*  367:     */         break;
/*  368:     */       case 11: 
/*  369:1405 */         yybegin(0);
/*  370:1406 */         return token(TokenType.COMMENT2, -4);
/*  371:     */       case 20: 
/*  372:     */         break;
/*  373:     */       case 7: 
/*  374:1410 */         yybegin(0);
/*  375:1411 */         return token(TokenType.KEYWORD, -1);
/*  376:     */       case 21: 
/*  377:     */         break;
/*  378:     */       case 4: 
/*  379:1415 */         yybegin(6);
/*  380:1416 */         return token(TokenType.KEYWORD, 1);
/*  381:     */       case 22: 
/*  382:     */         break;
/*  383:     */       case 8: 
/*  384:1420 */         return token(TokenType.STRING);
/*  385:     */       case 23: 
/*  386:     */         break;
/*  387:     */       case 15: 
/*  388:1424 */         return token(TokenType.KEYWORD2, -1);
/*  389:     */       case 24: 
/*  390:     */         break;
/*  391:     */       case 2: 
/*  392:1428 */         yybegin(0);
/*  393:1429 */         return token(TokenType.KEYWORD);
/*  394:     */       case 25: 
/*  395:     */         break;
/*  396:     */       case 6: 
/*  397:1433 */         return token(TokenType.IDENTIFIER);
/*  398:     */       case 26: 
/*  399:     */         break;
/*  400:     */       case 10: 
/*  401:1437 */         return token(TokenType.KEYWORD2);
/*  402:     */       case 27: 
/*  403:     */         break;
/*  404:     */       case 9: 
/*  405:1441 */         yybegin(8);
/*  406:1442 */         return token(TokenType.TYPE2, 2);
/*  407:     */       case 28: 
/*  408:     */         break;
/*  409:     */       case 13: 
/*  410:1446 */         yybegin(2);
/*  411:1447 */         return token(TokenType.COMMENT2, 4);
/*  412:     */       case 29: 
/*  413:     */         break;
/*  414:     */       case 5: 
/*  415:1451 */         yybegin(6);
/*  416:1452 */         return token(TokenType.KEYWORD2, 1);
/*  417:     */       case 30: 
/*  418:     */         break;
/*  419:     */       case 18: 
/*  420:1456 */         yybegin(4);
/*  421:1457 */         return token(TokenType.COMMENT2, 3);
/*  422:     */       case 31: 
/*  423:     */         break;
/*  424:     */       case 16: 
/*  425:1461 */         yypushback(3);
/*  426:1462 */         return token(TokenType.COMMENT);
/*  427:     */       case 32: 
/*  428:     */         break;
/*  429:     */       case 17: 
/*  430:1466 */         yybegin(10);
/*  431:1467 */         return token(TokenType.TYPE2, 2);
/*  432:     */       case 33: 
/*  433:     */         break;
/*  434:     */       case 3: 
/*  435:1471 */         yybegin(0);
/*  436:1472 */         return token(TokenType.TYPE2, -2);
/*  437:     */       case 34: 
/*  438:     */         break;
/*  439:     */       case 1: 
/*  440:     */       case 35: 
/*  441:     */         break;
/*  442:     */       case 12: 
/*  443:1480 */         yybegin(0);
/*  444:1481 */         return token(TokenType.COMMENT2, -3);
/*  445:     */       case 36: 
/*  446:     */         break;
/*  447:     */       default: 
/*  448:1485 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos)) {
/*  449:1486 */           this.zzAtEOF = true;
/*  450:     */         }
/*  451:1487 */         switch (this.zzLexicalState)
/*  452:     */         {
/*  453:     */         case 8: 
/*  454:1489 */           return null;
/*  455:     */         case 499: 
/*  456:     */           break;
/*  457:     */         case 0: 
/*  458:1493 */           return null;
/*  459:     */         case 500: 
/*  460:     */           break;
/*  461:     */         case 2: 
/*  462:1497 */           return null;
/*  463:     */         case 501: 
/*  464:     */           break;
/*  465:     */         case 4: 
/*  466:1501 */           return null;
/*  467:     */         case 502: 
/*  468:     */           break;
/*  469:     */         case 6: 
/*  470:1505 */           return null;
/*  471:     */         case 503: 
/*  472:     */           break;
/*  473:     */         default: 
/*  474:1509 */           return null;
/*  475:     */           
/*  476:     */ 
/*  477:     */ 
/*  478:1513 */           zzScanError(1);
/*  479:     */         }
/*  480:     */         break;
/*  481:     */       }
/*  482:     */     }
/*  483:     */   }
/*  484:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.XHTMLLexer
 * JD-Core Version:    0.7.0.1
 */