/*    1:     */ package jsyntaxpane.lexers;
/*    2:     */ 
/*    3:     */ import java.io.IOException;
/*    4:     */ import java.io.InputStream;
/*    5:     */ import java.io.InputStreamReader;
/*    6:     */ import java.io.Reader;
/*    7:     */ import jsyntaxpane.Token;
/*    8:     */ import jsyntaxpane.TokenType;
/*    9:     */ 
/*   10:     */ public final class JFlexLexer
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
/*   28: 180 */     int[] result = new int[473];
/*   29: 181 */     int offset = 0;
/*   30: 182 */     offset = zzUnpackAction("", offset, result);
/*   31: 183 */     return result;
/*   32:     */   }
/*   33:     */   
/*   34:     */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*   35:     */   {
/*   36: 187 */     int i = 0;
/*   37: 188 */     int j = offset;
/*   38: 189 */     int l = packed.length();
/*   39:     */     int count;
/*   40: 190 */     for (; i < l; count > 0)
/*   41:     */     {
/*   42: 191 */       count = packed.charAt(i++);
/*   43: 192 */       int value = packed.charAt(i++);
/*   44: 193 */       result[(j++)] = value;count--;
/*   45:     */     }
/*   46: 195 */     return j;
/*   47:     */   }
/*   48:     */   
/*   49: 202 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*   50:     */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*   51:     */   
/*   52:     */   private static int[] zzUnpackRowMap()
/*   53:     */   {
/*   54: 267 */     int[] result = new int[473];
/*   55: 268 */     int offset = 0;
/*   56: 269 */     offset = zzUnpackRowMap("", offset, result);
/*   57: 270 */     return result;
/*   58:     */   }
/*   59:     */   
/*   60:     */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*   61:     */   {
/*   62: 274 */     int i = 0;
/*   63: 275 */     int j = offset;
/*   64: 276 */     int l = packed.length();
/*   65: 277 */     while (i < l)
/*   66:     */     {
/*   67: 278 */       int high = packed.charAt(i++) << '\020';
/*   68: 279 */       result[(j++)] = (high | packed.charAt(i++));
/*   69:     */     }
/*   70: 281 */     return j;
/*   71:     */   }
/*   72:     */   
/*   73: 287 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*   74:     */   private static final String ZZ_TRANS_PACKED_0 = "";
/*   75:     */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*   76:     */   private static final int ZZ_NO_MATCH = 1;
/*   77:     */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*   78:     */   
/*   79:     */   private static int[] zzUnpackTrans()
/*   80:     */   {
/*   81: 923 */     int[] result = new int[32996];
/*   82: 924 */     int offset = 0;
/*   83: 925 */     offset = zzUnpackTrans("", offset, result);
/*   84: 926 */     return result;
/*   85:     */   }
/*   86:     */   
/*   87:     */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*   88:     */   {
/*   89: 930 */     int i = 0;
/*   90: 931 */     int j = offset;
/*   91: 932 */     int l = packed.length();
/*   92:     */     int count;
/*   93: 933 */     for (; i < l; count > 0)
/*   94:     */     {
/*   95: 934 */       count = packed.charAt(i++);
/*   96: 935 */       int value = packed.charAt(i++);
/*   97: 936 */       value--;
/*   98: 937 */       result[(j++)] = value;count--;
/*   99:     */     }
/*  100: 939 */     return j;
/*  101:     */   }
/*  102:     */   
/*  103: 949 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/*  104: 958 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/*  105:     */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/*  106:     */   private Reader zzReader;
/*  107:     */   private int zzState;
/*  108:     */   
/*  109:     */   private static int[] zzUnpackAttribute()
/*  110:     */   {
/*  111: 974 */     int[] result = new int[473];
/*  112: 975 */     int offset = 0;
/*  113: 976 */     offset = zzUnpackAttribute("", offset, result);
/*  114: 977 */     return result;
/*  115:     */   }
/*  116:     */   
/*  117:     */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/*  118:     */   {
/*  119: 981 */     int i = 0;
/*  120: 982 */     int j = offset;
/*  121: 983 */     int l = packed.length();
/*  122:     */     int count;
/*  123: 984 */     for (; i < l; count > 0)
/*  124:     */     {
/*  125: 985 */       count = packed.charAt(i++);
/*  126: 986 */       int value = packed.charAt(i++);
/*  127: 987 */       result[(j++)] = value;count--;
/*  128:     */     }
/*  129: 989 */     return j;
/*  130:     */   }
/*  131:     */   
/*  132: 999 */   private int zzLexicalState = 0;
/*  133:1003 */   private char[] zzBuffer = new char[16384];
/*  134:     */   private int zzMarkedPos;
/*  135:     */   private int zzCurrentPos;
/*  136:     */   private int zzStartRead;
/*  137:     */   private int zzEndRead;
/*  138:     */   private int yyline;
/*  139:     */   private int yychar;
/*  140:     */   private int yycolumn;
/*  141:1033 */   private boolean zzAtBOL = true;
/*  142:     */   private boolean zzAtEOF;
/*  143:     */   private boolean zzEOFDone;
/*  144:     */   
/*  145:     */   public JFlexLexer() {}
/*  146:     */   
/*  147:     */   public int yychar()
/*  148:     */   {
/*  149:1052 */     return this.yychar;
/*  150:     */   }
/*  151:     */   
/*  152:     */   public JFlexLexer(Reader in)
/*  153:     */   {
/*  154:1063 */     this.zzReader = in;
/*  155:     */   }
/*  156:     */   
/*  157:     */   public JFlexLexer(InputStream in)
/*  158:     */   {
/*  159:1073 */     this(new InputStreamReader(in));
/*  160:     */   }
/*  161:     */   
/*  162:     */   private static char[] zzUnpackCMap(String packed)
/*  163:     */   {
/*  164:1083 */     char[] map = new char[65536];
/*  165:1084 */     int i = 0;
/*  166:1085 */     int j = 0;
/*  167:     */     int count;
/*  168:1086 */     for (; i < 1822; count > 0)
/*  169:     */     {
/*  170:1087 */       count = packed.charAt(i++);
/*  171:1088 */       char value = packed.charAt(i++);
/*  172:1089 */       map[(j++)] = value;count--;
/*  173:     */     }
/*  174:1091 */     return map;
/*  175:     */   }
/*  176:     */   
/*  177:     */   private boolean zzRefill()
/*  178:     */     throws IOException
/*  179:     */   {
/*  180:1105 */     if (this.zzStartRead > 0)
/*  181:     */     {
/*  182:1106 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/*  183:     */       
/*  184:     */ 
/*  185:     */ 
/*  186:     */ 
/*  187:1111 */       this.zzEndRead -= this.zzStartRead;
/*  188:1112 */       this.zzCurrentPos -= this.zzStartRead;
/*  189:1113 */       this.zzMarkedPos -= this.zzStartRead;
/*  190:1114 */       this.zzStartRead = 0;
/*  191:     */     }
/*  192:1118 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/*  193:     */     {
/*  194:1120 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/*  195:1121 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/*  196:1122 */       this.zzBuffer = newBuffer;
/*  197:     */     }
/*  198:1126 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/*  199:1129 */     if (numRead > 0)
/*  200:     */     {
/*  201:1130 */       this.zzEndRead += numRead;
/*  202:1131 */       return false;
/*  203:     */     }
/*  204:1134 */     if (numRead == 0)
/*  205:     */     {
/*  206:1135 */       int c = this.zzReader.read();
/*  207:1136 */       if (c == -1) {
/*  208:1137 */         return true;
/*  209:     */       }
/*  210:1139 */       this.zzBuffer[(this.zzEndRead++)] = ((char)c);
/*  211:1140 */       return false;
/*  212:     */     }
/*  213:1145 */     return true;
/*  214:     */   }
/*  215:     */   
/*  216:     */   public final void yyclose()
/*  217:     */     throws IOException
/*  218:     */   {
/*  219:1153 */     this.zzAtEOF = true;
/*  220:1154 */     this.zzEndRead = this.zzStartRead;
/*  221:1156 */     if (this.zzReader != null) {
/*  222:1157 */       this.zzReader.close();
/*  223:     */     }
/*  224:     */   }
/*  225:     */   
/*  226:     */   public final void yyreset(Reader reader)
/*  227:     */   {
/*  228:1172 */     this.zzReader = reader;
/*  229:1173 */     this.zzAtBOL = true;
/*  230:1174 */     this.zzAtEOF = false;
/*  231:1175 */     this.zzEOFDone = false;
/*  232:1176 */     this.zzEndRead = (this.zzStartRead = 0);
/*  233:1177 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/*  234:1178 */     this.yyline = (this.yychar = this.yycolumn = 0);
/*  235:1179 */     this.zzLexicalState = 0;
/*  236:     */   }
/*  237:     */   
/*  238:     */   public final int yystate()
/*  239:     */   {
/*  240:1187 */     return this.zzLexicalState;
/*  241:     */   }
/*  242:     */   
/*  243:     */   public final void yybegin(int newState)
/*  244:     */   {
/*  245:1197 */     this.zzLexicalState = newState;
/*  246:     */   }
/*  247:     */   
/*  248:     */   public final String yytext()
/*  249:     */   {
/*  250:1205 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*  251:     */   }
/*  252:     */   
/*  253:     */   public final char yycharat(int pos)
/*  254:     */   {
/*  255:1221 */     return this.zzBuffer[(this.zzStartRead + pos)];
/*  256:     */   }
/*  257:     */   
/*  258:     */   public final int yylength()
/*  259:     */   {
/*  260:1229 */     return this.zzMarkedPos - this.zzStartRead;
/*  261:     */   }
/*  262:     */   
/*  263:     */   private void zzScanError(int errorCode)
/*  264:     */   {
/*  265:     */     String message;
/*  266:     */     try
/*  267:     */     {
/*  268:1250 */       message = ZZ_ERROR_MSG[errorCode];
/*  269:     */     }
/*  270:     */     catch (ArrayIndexOutOfBoundsException e)
/*  271:     */     {
/*  272:1253 */       message = ZZ_ERROR_MSG[0];
/*  273:     */     }
/*  274:1256 */     throw new Error(message);
/*  275:     */   }
/*  276:     */   
/*  277:     */   public void yypushback(int number)
/*  278:     */   {
/*  279:1269 */     if (number > yylength()) {
/*  280:1270 */       zzScanError(2);
/*  281:     */     }
/*  282:1272 */     this.zzMarkedPos -= number;
/*  283:     */   }
/*  284:     */   
/*  285:     */   public Token yylex()
/*  286:     */     throws IOException
/*  287:     */   {
/*  288:1290 */     int zzEndReadL = this.zzEndRead;
/*  289:1291 */     char[] zzBufferL = this.zzBuffer;
/*  290:1292 */     char[] zzCMapL = ZZ_CMAP;
/*  291:     */     
/*  292:1294 */     int[] zzTransL = ZZ_TRANS;
/*  293:1295 */     int[] zzRowMapL = ZZ_ROWMAP;
/*  294:1296 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/*  295:     */     for (;;)
/*  296:     */     {
/*  297:1299 */       int zzMarkedPosL = this.zzMarkedPos;
/*  298:     */       
/*  299:1301 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/*  300:     */       
/*  301:1303 */       int zzAction = -1;
/*  302:     */       
/*  303:1305 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/*  304:     */       
/*  305:1307 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/*  306:     */       int zzInput;
/*  307:     */       for (;;)
/*  308:     */       {
/*  309:     */         int zzInput;
/*  310:1313 */         if (zzCurrentPosL < zzEndReadL)
/*  311:     */         {
/*  312:1314 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  313:     */         }
/*  314:     */         else
/*  315:     */         {
/*  316:1315 */           if (this.zzAtEOF)
/*  317:     */           {
/*  318:1316 */             int zzInput = -1;
/*  319:1317 */             break;
/*  320:     */           }
/*  321:1321 */           this.zzCurrentPos = zzCurrentPosL;
/*  322:1322 */           this.zzMarkedPos = zzMarkedPosL;
/*  323:1323 */           boolean eof = zzRefill();
/*  324:     */           
/*  325:1325 */           zzCurrentPosL = this.zzCurrentPos;
/*  326:1326 */           zzMarkedPosL = this.zzMarkedPos;
/*  327:1327 */           zzBufferL = this.zzBuffer;
/*  328:1328 */           zzEndReadL = this.zzEndRead;
/*  329:1329 */           if (eof)
/*  330:     */           {
/*  331:1330 */             int zzInput = -1;
/*  332:1331 */             break;
/*  333:     */           }
/*  334:1334 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  335:     */         }
/*  336:1337 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/*  337:1338 */         if (zzNext == -1) {
/*  338:     */           break;
/*  339:     */         }
/*  340:1339 */         this.zzState = zzNext;
/*  341:     */         
/*  342:1341 */         int zzAttributes = zzAttrL[this.zzState];
/*  343:1342 */         if ((zzAttributes & 0x1) == 1)
/*  344:     */         {
/*  345:1343 */           zzAction = this.zzState;
/*  346:1344 */           zzMarkedPosL = zzCurrentPosL;
/*  347:1345 */           if ((zzAttributes & 0x8) == 8) {
/*  348:     */             break;
/*  349:     */           }
/*  350:     */         }
/*  351:     */       }
/*  352:1352 */       this.zzMarkedPos = zzMarkedPosL;
/*  353:1354 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/*  354:     */       {
/*  355:     */       case 14: 
/*  356:1356 */         return token(TokenType.KEYWORD);
/*  357:     */       case 21: 
/*  358:     */         break;
/*  359:     */       case 4: 
/*  360:1360 */         return token(TokenType.NUMBER);
/*  361:     */       case 22: 
/*  362:     */         break;
/*  363:     */       case 12: 
/*  364:1364 */         yybegin(6);
/*  365:     */         
/*  366:1366 */         int start = this.tokenStart;
/*  367:1367 */         this.tokenStart = this.yychar;
/*  368:1368 */         int len = this.tokenLength;
/*  369:1369 */         this.tokenLength = 1;
/*  370:1370 */         return token(TokenType.COMMENT2, start, len);
/*  371:     */       case 23: 
/*  372:     */         break;
/*  373:     */       case 2: 
/*  374:1374 */         return token(TokenType.OPERATOR);
/*  375:     */       case 24: 
/*  376:     */         break;
/*  377:     */       case 17: 
/*  378:1378 */         yybegin(0);
/*  379:1379 */         return token(TokenType.COMMENT, this.tokenStart, this.tokenLength + 2);
/*  380:     */       case 25: 
/*  381:     */         break;
/*  382:     */       case 11: 
/*  383:1383 */         yybegin(8);
/*  384:1384 */         int start = this.tokenStart;
/*  385:1385 */         this.tokenStart = this.yychar;
/*  386:1386 */         int len = this.tokenLength;
/*  387:1387 */         this.tokenLength = 1;
/*  388:1388 */         return token(TokenType.COMMENT, start, len);
/*  389:     */       case 26: 
/*  390:     */         break;
/*  391:     */       case 7: 
/*  392:1392 */         this.tokenLength += yylength();
/*  393:     */       case 27: 
/*  394:     */         break;
/*  395:     */       case 8: 
/*  396:1396 */         yybegin(0);
/*  397:     */       case 28: 
/*  398:     */         break;
/*  399:     */       case 6: 
/*  400:1400 */         yybegin(4);
/*  401:1401 */         this.tokenStart = this.yychar;
/*  402:1402 */         this.tokenLength = 1;
/*  403:     */       case 29: 
/*  404:     */         break;
/*  405:     */       case 18: 
/*  406:1406 */         yybegin(6);
/*  407:1407 */         this.tokenStart = this.yychar;
/*  408:1408 */         this.tokenLength = 3;
/*  409:     */       case 30: 
/*  410:     */         break;
/*  411:     */       case 9: 
/*  412:1412 */         yybegin(0);
/*  413:     */         
/*  414:1414 */         return token(TokenType.STRING, this.tokenStart, this.tokenLength + 1);
/*  415:     */       case 31: 
/*  416:     */         break;
/*  417:     */       case 20: 
/*  418:1418 */         return token(TokenType.TYPE);
/*  419:     */       case 32: 
/*  420:     */         break;
/*  421:     */       case 3: 
/*  422:1422 */         return token(TokenType.IDENTIFIER);
/*  423:     */       case 33: 
/*  424:     */         break;
/*  425:     */       case 15: 
/*  426:1426 */         return token(TokenType.KEYWORD2);
/*  427:     */       case 34: 
/*  428:     */         break;
/*  429:     */       case 16: 
/*  430:1430 */         this.tokenLength += 2;
/*  431:     */       case 35: 
/*  432:     */         break;
/*  433:     */       case 19: 
/*  434:1434 */         return token(TokenType.TYPE2);
/*  435:     */       case 36: 
/*  436:     */         break;
/*  437:     */       case 10: 
/*  438:1438 */         this.tokenLength += 1;
/*  439:     */       case 37: 
/*  440:     */         break;
/*  441:     */       case 13: 
/*  442:1442 */         return token(TokenType.COMMENT);
/*  443:     */       case 38: 
/*  444:     */         break;
/*  445:     */       case 5: 
/*  446:1446 */         yybegin(2);
/*  447:1447 */         this.tokenStart = this.yychar;
/*  448:1448 */         this.tokenLength = 1;
/*  449:     */       case 39: 
/*  450:     */         break;
/*  451:     */       case 1: 
/*  452:     */       case 40: 
/*  453:     */         break;
/*  454:     */       default: 
/*  455:1456 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos))
/*  456:     */         {
/*  457:1457 */           this.zzAtEOF = true;
/*  458:     */           
/*  459:1459 */           return null;
/*  460:     */         }
/*  461:1463 */         zzScanError(1);
/*  462:     */       }
/*  463:     */     }
/*  464:     */   }
/*  465:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.JFlexLexer
 * JD-Core Version:    0.7.0.1
 */