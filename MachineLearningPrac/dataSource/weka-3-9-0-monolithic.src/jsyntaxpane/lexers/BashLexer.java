/*    1:     */ package jsyntaxpane.lexers;
/*    2:     */ 
/*    3:     */ import java.io.IOException;
/*    4:     */ import java.io.InputStream;
/*    5:     */ import java.io.InputStreamReader;
/*    6:     */ import java.io.Reader;
/*    7:     */ import jsyntaxpane.Token;
/*    8:     */ import jsyntaxpane.TokenType;
/*    9:     */ 
/*   10:     */ public final class BashLexer
/*   11:     */   extends DefaultJFlexLexer
/*   12:     */ {
/*   13:     */   public static final int YYEOF = -1;
/*   14:     */   private static final int ZZ_BUFFERSIZE = 16384;
/*   15:     */   public static final int YYINITIAL = 0;
/*   16:  47 */   private static final int[] ZZ_LEXSTATE = { 0, 0 };
/*   17:     */   private static final String ZZ_CMAP_PACKED = "";
/*   18:  67 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*   19:  72 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*   20:     */   private static final String ZZ_ACTION_PACKED_0 = "";
/*   21:     */   
/*   22:     */   private static int[] zzUnpackAction()
/*   23:     */   {
/*   24:  88 */     int[] result = new int[373];
/*   25:  89 */     int offset = 0;
/*   26:  90 */     offset = zzUnpackAction("", offset, result);
/*   27:  91 */     return result;
/*   28:     */   }
/*   29:     */   
/*   30:     */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*   31:     */   {
/*   32:  95 */     int i = 0;
/*   33:  96 */     int j = offset;
/*   34:  97 */     int l = packed.length();
/*   35:     */     int count;
/*   36:  98 */     for (; i < l; count > 0)
/*   37:     */     {
/*   38:  99 */       count = packed.charAt(i++);
/*   39: 100 */       int value = packed.charAt(i++);
/*   40: 101 */       result[(j++)] = value;count--;
/*   41:     */     }
/*   42: 103 */     return j;
/*   43:     */   }
/*   44:     */   
/*   45: 110 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*   46:     */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*   47:     */   
/*   48:     */   private static int[] zzUnpackRowMap()
/*   49:     */   {
/*   50: 162 */     int[] result = new int[373];
/*   51: 163 */     int offset = 0;
/*   52: 164 */     offset = zzUnpackRowMap("", offset, result);
/*   53: 165 */     return result;
/*   54:     */   }
/*   55:     */   
/*   56:     */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*   57:     */   {
/*   58: 169 */     int i = 0;
/*   59: 170 */     int j = offset;
/*   60: 171 */     int l = packed.length();
/*   61: 172 */     while (i < l)
/*   62:     */     {
/*   63: 173 */       int high = packed.charAt(i++) << '\020';
/*   64: 174 */       result[(j++)] = (high | packed.charAt(i++));
/*   65:     */     }
/*   66: 176 */     return j;
/*   67:     */   }
/*   68:     */   
/*   69: 182 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*   70:     */   private static final String ZZ_TRANS_PACKED_0 = "";
/*   71:     */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*   72:     */   private static final int ZZ_NO_MATCH = 1;
/*   73:     */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*   74:     */   
/*   75:     */   private static int[] zzUnpackTrans()
/*   76:     */   {
/*   77: 798 */     int[] result = new int[19250];
/*   78: 799 */     int offset = 0;
/*   79: 800 */     offset = zzUnpackTrans("", offset, result);
/*   80: 801 */     return result;
/*   81:     */   }
/*   82:     */   
/*   83:     */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*   84:     */   {
/*   85: 805 */     int i = 0;
/*   86: 806 */     int j = offset;
/*   87: 807 */     int l = packed.length();
/*   88:     */     int count;
/*   89: 808 */     for (; i < l; count > 0)
/*   90:     */     {
/*   91: 809 */       count = packed.charAt(i++);
/*   92: 810 */       int value = packed.charAt(i++);
/*   93: 811 */       value--;
/*   94: 812 */       result[(j++)] = value;count--;
/*   95:     */     }
/*   96: 814 */     return j;
/*   97:     */   }
/*   98:     */   
/*   99: 824 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/*  100: 833 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/*  101:     */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/*  102:     */   private Reader zzReader;
/*  103:     */   private int zzState;
/*  104:     */   
/*  105:     */   private static int[] zzUnpackAttribute()
/*  106:     */   {
/*  107: 841 */     int[] result = new int[373];
/*  108: 842 */     int offset = 0;
/*  109: 843 */     offset = zzUnpackAttribute("", offset, result);
/*  110: 844 */     return result;
/*  111:     */   }
/*  112:     */   
/*  113:     */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/*  114:     */   {
/*  115: 848 */     int i = 0;
/*  116: 849 */     int j = offset;
/*  117: 850 */     int l = packed.length();
/*  118:     */     int count;
/*  119: 851 */     for (; i < l; count > 0)
/*  120:     */     {
/*  121: 852 */       count = packed.charAt(i++);
/*  122: 853 */       int value = packed.charAt(i++);
/*  123: 854 */       result[(j++)] = value;count--;
/*  124:     */     }
/*  125: 856 */     return j;
/*  126:     */   }
/*  127:     */   
/*  128: 866 */   private int zzLexicalState = 0;
/*  129: 870 */   private char[] zzBuffer = new char[16384];
/*  130:     */   private int zzMarkedPos;
/*  131:     */   private int zzCurrentPos;
/*  132:     */   private int zzStartRead;
/*  133:     */   private int zzEndRead;
/*  134:     */   private int yyline;
/*  135:     */   private int yychar;
/*  136:     */   private int yycolumn;
/*  137: 900 */   private boolean zzAtBOL = true;
/*  138:     */   private boolean zzAtEOF;
/*  139:     */   private boolean zzEOFDone;
/*  140:     */   private static final byte PARAN = 1;
/*  141:     */   private static final byte BRACKET = 2;
/*  142:     */   private static final byte CURLY = 3;
/*  143:     */   private static final byte DO = 4;
/*  144:     */   private static final byte CASE = 5;
/*  145:     */   private static final byte IF = 5;
/*  146:     */   private static final byte INT_EXPR = 6;
/*  147:     */   
/*  148:     */   public BashLexer() {}
/*  149:     */   
/*  150:     */   public int yychar()
/*  151:     */   {
/*  152: 927 */     return this.yychar;
/*  153:     */   }
/*  154:     */   
/*  155:     */   public BashLexer(Reader in)
/*  156:     */   {
/*  157: 939 */     this.zzReader = in;
/*  158:     */   }
/*  159:     */   
/*  160:     */   public BashLexer(InputStream in)
/*  161:     */   {
/*  162: 949 */     this(new InputStreamReader(in));
/*  163:     */   }
/*  164:     */   
/*  165:     */   private static char[] zzUnpackCMap(String packed)
/*  166:     */   {
/*  167: 959 */     char[] map = new char[65536];
/*  168: 960 */     int i = 0;
/*  169: 961 */     int j = 0;
/*  170:     */     int count;
/*  171: 962 */     for (; i < 144; count > 0)
/*  172:     */     {
/*  173: 963 */       count = packed.charAt(i++);
/*  174: 964 */       char value = packed.charAt(i++);
/*  175: 965 */       map[(j++)] = value;count--;
/*  176:     */     }
/*  177: 967 */     return map;
/*  178:     */   }
/*  179:     */   
/*  180:     */   private boolean zzRefill()
/*  181:     */     throws IOException
/*  182:     */   {
/*  183: 981 */     if (this.zzStartRead > 0)
/*  184:     */     {
/*  185: 982 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/*  186:     */       
/*  187:     */ 
/*  188:     */ 
/*  189:     */ 
/*  190: 987 */       this.zzEndRead -= this.zzStartRead;
/*  191: 988 */       this.zzCurrentPos -= this.zzStartRead;
/*  192: 989 */       this.zzMarkedPos -= this.zzStartRead;
/*  193: 990 */       this.zzStartRead = 0;
/*  194:     */     }
/*  195: 994 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/*  196:     */     {
/*  197: 996 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/*  198: 997 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/*  199: 998 */       this.zzBuffer = newBuffer;
/*  200:     */     }
/*  201:1002 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/*  202:1005 */     if (numRead > 0)
/*  203:     */     {
/*  204:1006 */       this.zzEndRead += numRead;
/*  205:1007 */       return false;
/*  206:     */     }
/*  207:1010 */     if (numRead == 0)
/*  208:     */     {
/*  209:1011 */       int c = this.zzReader.read();
/*  210:1012 */       if (c == -1) {
/*  211:1013 */         return true;
/*  212:     */       }
/*  213:1015 */       this.zzBuffer[(this.zzEndRead++)] = ((char)c);
/*  214:1016 */       return false;
/*  215:     */     }
/*  216:1021 */     return true;
/*  217:     */   }
/*  218:     */   
/*  219:     */   public final void yyclose()
/*  220:     */     throws IOException
/*  221:     */   {
/*  222:1029 */     this.zzAtEOF = true;
/*  223:1030 */     this.zzEndRead = this.zzStartRead;
/*  224:1032 */     if (this.zzReader != null) {
/*  225:1033 */       this.zzReader.close();
/*  226:     */     }
/*  227:     */   }
/*  228:     */   
/*  229:     */   public final void yyreset(Reader reader)
/*  230:     */   {
/*  231:1048 */     this.zzReader = reader;
/*  232:1049 */     this.zzAtBOL = true;
/*  233:1050 */     this.zzAtEOF = false;
/*  234:1051 */     this.zzEOFDone = false;
/*  235:1052 */     this.zzEndRead = (this.zzStartRead = 0);
/*  236:1053 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/*  237:1054 */     this.yyline = (this.yychar = this.yycolumn = 0);
/*  238:1055 */     this.zzLexicalState = 0;
/*  239:     */   }
/*  240:     */   
/*  241:     */   public final int yystate()
/*  242:     */   {
/*  243:1063 */     return this.zzLexicalState;
/*  244:     */   }
/*  245:     */   
/*  246:     */   public final void yybegin(int newState)
/*  247:     */   {
/*  248:1073 */     this.zzLexicalState = newState;
/*  249:     */   }
/*  250:     */   
/*  251:     */   public final String yytext()
/*  252:     */   {
/*  253:1081 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*  254:     */   }
/*  255:     */   
/*  256:     */   public final char yycharat(int pos)
/*  257:     */   {
/*  258:1097 */     return this.zzBuffer[(this.zzStartRead + pos)];
/*  259:     */   }
/*  260:     */   
/*  261:     */   public final int yylength()
/*  262:     */   {
/*  263:1105 */     return this.zzMarkedPos - this.zzStartRead;
/*  264:     */   }
/*  265:     */   
/*  266:     */   private void zzScanError(int errorCode)
/*  267:     */   {
/*  268:     */     String message;
/*  269:     */     try
/*  270:     */     {
/*  271:1126 */       message = ZZ_ERROR_MSG[errorCode];
/*  272:     */     }
/*  273:     */     catch (ArrayIndexOutOfBoundsException e)
/*  274:     */     {
/*  275:1129 */       message = ZZ_ERROR_MSG[0];
/*  276:     */     }
/*  277:1132 */     throw new Error(message);
/*  278:     */   }
/*  279:     */   
/*  280:     */   public void yypushback(int number)
/*  281:     */   {
/*  282:1145 */     if (number > yylength()) {
/*  283:1146 */       zzScanError(2);
/*  284:     */     }
/*  285:1148 */     this.zzMarkedPos -= number;
/*  286:     */   }
/*  287:     */   
/*  288:     */   public Token yylex()
/*  289:     */     throws IOException
/*  290:     */   {
/*  291:1166 */     int zzEndReadL = this.zzEndRead;
/*  292:1167 */     char[] zzBufferL = this.zzBuffer;
/*  293:1168 */     char[] zzCMapL = ZZ_CMAP;
/*  294:     */     
/*  295:1170 */     int[] zzTransL = ZZ_TRANS;
/*  296:1171 */     int[] zzRowMapL = ZZ_ROWMAP;
/*  297:1172 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/*  298:     */     for (;;)
/*  299:     */     {
/*  300:1175 */       int zzMarkedPosL = this.zzMarkedPos;
/*  301:     */       
/*  302:1177 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/*  303:     */       
/*  304:1179 */       int zzAction = -1;
/*  305:     */       
/*  306:1181 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/*  307:     */       
/*  308:1183 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/*  309:     */       int zzInput;
/*  310:     */       for (;;)
/*  311:     */       {
/*  312:     */         int zzInput;
/*  313:1189 */         if (zzCurrentPosL < zzEndReadL)
/*  314:     */         {
/*  315:1190 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  316:     */         }
/*  317:     */         else
/*  318:     */         {
/*  319:1191 */           if (this.zzAtEOF)
/*  320:     */           {
/*  321:1192 */             int zzInput = -1;
/*  322:1193 */             break;
/*  323:     */           }
/*  324:1197 */           this.zzCurrentPos = zzCurrentPosL;
/*  325:1198 */           this.zzMarkedPos = zzMarkedPosL;
/*  326:1199 */           boolean eof = zzRefill();
/*  327:     */           
/*  328:1201 */           zzCurrentPosL = this.zzCurrentPos;
/*  329:1202 */           zzMarkedPosL = this.zzMarkedPos;
/*  330:1203 */           zzBufferL = this.zzBuffer;
/*  331:1204 */           zzEndReadL = this.zzEndRead;
/*  332:1205 */           if (eof)
/*  333:     */           {
/*  334:1206 */             int zzInput = -1;
/*  335:1207 */             break;
/*  336:     */           }
/*  337:1210 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  338:     */         }
/*  339:1213 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/*  340:1214 */         if (zzNext == -1) {
/*  341:     */           break;
/*  342:     */         }
/*  343:1215 */         this.zzState = zzNext;
/*  344:     */         
/*  345:1217 */         int zzAttributes = zzAttrL[this.zzState];
/*  346:1218 */         if ((zzAttributes & 0x1) == 1)
/*  347:     */         {
/*  348:1219 */           zzAction = this.zzState;
/*  349:1220 */           zzMarkedPosL = zzCurrentPosL;
/*  350:1221 */           if ((zzAttributes & 0x8) == 8) {
/*  351:     */             break;
/*  352:     */           }
/*  353:     */         }
/*  354:     */       }
/*  355:1228 */       this.zzMarkedPos = zzMarkedPosL;
/*  356:1230 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/*  357:     */       {
/*  358:     */       case 5: 
/*  359:1232 */         return token(TokenType.OPERATOR, -1);
/*  360:     */       case 24: 
/*  361:     */         break;
/*  362:     */       case 10: 
/*  363:1236 */         return token(TokenType.KEYWORD);
/*  364:     */       case 25: 
/*  365:     */         break;
/*  366:     */       case 3: 
/*  367:1240 */         return token(TokenType.OPERATOR);
/*  368:     */       case 26: 
/*  369:     */         break;
/*  370:     */       case 6: 
/*  371:1244 */         return token(TokenType.OPERATOR, 3);
/*  372:     */       case 27: 
/*  373:     */         break;
/*  374:     */       case 7: 
/*  375:1248 */         return token(TokenType.OPERATOR, -3);
/*  376:     */       case 28: 
/*  377:     */         break;
/*  378:     */       case 20: 
/*  379:1252 */         return token(TokenType.KEYWORD, 6);
/*  380:     */       case 29: 
/*  381:     */         break;
/*  382:     */       case 18: 
/*  383:1256 */         return token(TokenType.STRING);
/*  384:     */       case 30: 
/*  385:     */         break;
/*  386:     */       case 17: 
/*  387:1260 */         return token(TokenType.TYPE);
/*  388:     */       case 31: 
/*  389:     */         break;
/*  390:     */       case 12: 
/*  391:1264 */         return token(TokenType.COMMENT2);
/*  392:     */       case 32: 
/*  393:     */         break;
/*  394:     */       case 1: 
/*  395:     */       case 33: 
/*  396:     */         break;
/*  397:     */       case 9: 
/*  398:1272 */         return token(TokenType.OPERATOR, -2);
/*  399:     */       case 34: 
/*  400:     */         break;
/*  401:     */       case 4: 
/*  402:1276 */         return token(TokenType.OPERATOR, 1);
/*  403:     */       case 35: 
/*  404:     */         break;
/*  405:     */       case 13: 
/*  406:1280 */         return token(TokenType.KEYWORD, 5);
/*  407:     */       case 36: 
/*  408:     */         break;
/*  409:     */       case 2: 
/*  410:1284 */         return token(TokenType.IDENTIFIER);
/*  411:     */       case 37: 
/*  412:     */         break;
/*  413:     */       case 14: 
/*  414:1288 */         return token(TokenType.KEYWORD, -5);
/*  415:     */       case 38: 
/*  416:     */         break;
/*  417:     */       case 16: 
/*  418:1292 */         return token(TokenType.KEYWORD, -6);
/*  419:     */       case 39: 
/*  420:     */         break;
/*  421:     */       case 23: 
/*  422:1296 */         return token(TokenType.KEYWORD, 5);
/*  423:     */       case 40: 
/*  424:     */         break;
/*  425:     */       case 8: 
/*  426:1300 */         return token(TokenType.OPERATOR, 2);
/*  427:     */       case 41: 
/*  428:     */         break;
/*  429:     */       case 22: 
/*  430:1304 */         return token(TokenType.KEYWORD, -5);
/*  431:     */       case 42: 
/*  432:     */         break;
/*  433:     */       case 11: 
/*  434:1308 */         return token(TokenType.COMMENT);
/*  435:     */       case 43: 
/*  436:     */         break;
/*  437:     */       case 15: 
/*  438:1312 */         return token(TokenType.KEYWORD, 4);
/*  439:     */       case 44: 
/*  440:     */         break;
/*  441:     */       case 21: 
/*  442:1316 */         return token(TokenType.KEYWORD, -4);
/*  443:     */       case 45: 
/*  444:     */         break;
/*  445:     */       case 19: 
/*  446:1320 */         return token(TokenType.STRING2);
/*  447:     */       case 46: 
/*  448:     */         break;
/*  449:     */       default: 
/*  450:1324 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos))
/*  451:     */         {
/*  452:1325 */           this.zzAtEOF = true;
/*  453:     */           
/*  454:1327 */           return null;
/*  455:     */         }
/*  456:1331 */         zzScanError(1);
/*  457:     */       }
/*  458:     */     }
/*  459:     */   }
/*  460:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.BashLexer
 * JD-Core Version:    0.7.0.1
 */