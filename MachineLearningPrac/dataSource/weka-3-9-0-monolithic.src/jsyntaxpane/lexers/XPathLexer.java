/*    1:     */ package jsyntaxpane.lexers;
/*    2:     */ 
/*    3:     */ import java.io.IOException;
/*    4:     */ import java.io.InputStream;
/*    5:     */ import java.io.InputStreamReader;
/*    6:     */ import java.io.Reader;
/*    7:     */ import jsyntaxpane.Token;
/*    8:     */ import jsyntaxpane.TokenType;
/*    9:     */ 
/*   10:     */ public final class XPathLexer
/*   11:     */   extends DefaultJFlexLexer
/*   12:     */ {
/*   13:     */   public static final int YYEOF = -1;
/*   14:     */   private static final int ZZ_BUFFERSIZE = 16384;
/*   15:     */   public static final int STRING_SINGLE = 4;
/*   16:     */   public static final int YYINITIAL = 0;
/*   17:     */   public static final int STRING_DOUBLE = 2;
/*   18:  51 */   private static final int[] ZZ_LEXSTATE = { 0, 0, 1, 1, 2, 2 };
/*   19:     */   private static final String ZZ_CMAP_PACKED = "";
/*   20: 113 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*   21: 118 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*   22:     */   private static final String ZZ_ACTION_PACKED_0 = "";
/*   23:     */   
/*   24:     */   private static int[] zzUnpackAction()
/*   25:     */   {
/*   26: 132 */     int[] result = new int[554];
/*   27: 133 */     int offset = 0;
/*   28: 134 */     offset = zzUnpackAction("", offset, result);
/*   29: 135 */     return result;
/*   30:     */   }
/*   31:     */   
/*   32:     */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*   33:     */   {
/*   34: 139 */     int i = 0;
/*   35: 140 */     int j = offset;
/*   36: 141 */     int l = packed.length();
/*   37:     */     int count;
/*   38: 142 */     for (; i < l; count > 0)
/*   39:     */     {
/*   40: 143 */       count = packed.charAt(i++);
/*   41: 144 */       int value = packed.charAt(i++);
/*   42: 145 */       result[(j++)] = value;count--;
/*   43:     */     }
/*   44: 147 */     return j;
/*   45:     */   }
/*   46:     */   
/*   47: 154 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*   48:     */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*   49:     */   
/*   50:     */   private static int[] zzUnpackRowMap()
/*   51:     */   {
/*   52: 229 */     int[] result = new int[554];
/*   53: 230 */     int offset = 0;
/*   54: 231 */     offset = zzUnpackRowMap("", offset, result);
/*   55: 232 */     return result;
/*   56:     */   }
/*   57:     */   
/*   58:     */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*   59:     */   {
/*   60: 236 */     int i = 0;
/*   61: 237 */     int j = offset;
/*   62: 238 */     int l = packed.length();
/*   63: 239 */     while (i < l)
/*   64:     */     {
/*   65: 240 */       int high = packed.charAt(i++) << '\020';
/*   66: 241 */       result[(j++)] = (high | packed.charAt(i++));
/*   67:     */     }
/*   68: 243 */     return j;
/*   69:     */   }
/*   70:     */   
/*   71: 249 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*   72:     */   private static final String ZZ_TRANS_PACKED_0 = "";
/*   73:     */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*   74:     */   private static final int ZZ_NO_MATCH = 1;
/*   75:     */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*   76:     */   
/*   77:     */   private static int[] zzUnpackTrans()
/*   78:     */   {
/*   79: 864 */     int[] result = new int[28836];
/*   80: 865 */     int offset = 0;
/*   81: 866 */     offset = zzUnpackTrans("", offset, result);
/*   82: 867 */     return result;
/*   83:     */   }
/*   84:     */   
/*   85:     */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*   86:     */   {
/*   87: 871 */     int i = 0;
/*   88: 872 */     int j = offset;
/*   89: 873 */     int l = packed.length();
/*   90:     */     int count;
/*   91: 874 */     for (; i < l; count > 0)
/*   92:     */     {
/*   93: 875 */       count = packed.charAt(i++);
/*   94: 876 */       int value = packed.charAt(i++);
/*   95: 877 */       value--;
/*   96: 878 */       result[(j++)] = value;count--;
/*   97:     */     }
/*   98: 880 */     return j;
/*   99:     */   }
/*  100:     */   
/*  101: 890 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/*  102: 899 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/*  103:     */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/*  104:     */   private Reader zzReader;
/*  105:     */   private int zzState;
/*  106:     */   
/*  107:     */   private static int[] zzUnpackAttribute()
/*  108:     */   {
/*  109: 906 */     int[] result = new int[554];
/*  110: 907 */     int offset = 0;
/*  111: 908 */     offset = zzUnpackAttribute("", offset, result);
/*  112: 909 */     return result;
/*  113:     */   }
/*  114:     */   
/*  115:     */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/*  116:     */   {
/*  117: 913 */     int i = 0;
/*  118: 914 */     int j = offset;
/*  119: 915 */     int l = packed.length();
/*  120:     */     int count;
/*  121: 916 */     for (; i < l; count > 0)
/*  122:     */     {
/*  123: 917 */       count = packed.charAt(i++);
/*  124: 918 */       int value = packed.charAt(i++);
/*  125: 919 */       result[(j++)] = value;count--;
/*  126:     */     }
/*  127: 921 */     return j;
/*  128:     */   }
/*  129:     */   
/*  130: 931 */   private int zzLexicalState = 0;
/*  131: 935 */   private char[] zzBuffer = new char[16384];
/*  132:     */   private int zzMarkedPos;
/*  133:     */   private int zzCurrentPos;
/*  134:     */   private int zzStartRead;
/*  135:     */   private int zzEndRead;
/*  136:     */   private int yyline;
/*  137:     */   private int yychar;
/*  138:     */   private int yycolumn;
/*  139: 965 */   private boolean zzAtBOL = true;
/*  140:     */   private boolean zzAtEOF;
/*  141:     */   private boolean zzEOFDone;
/*  142:     */   private static final byte PARAN = 1;
/*  143:     */   private static final byte BRACKET = 2;
/*  144:     */   private static final byte CURLY = 3;
/*  145:     */   
/*  146:     */   public XPathLexer() {}
/*  147:     */   
/*  148:     */   public int yychar()
/*  149:     */   {
/*  150: 984 */     return this.yychar;
/*  151:     */   }
/*  152:     */   
/*  153:     */   public XPathLexer(Reader in)
/*  154:     */   {
/*  155:1000 */     this.zzReader = in;
/*  156:     */   }
/*  157:     */   
/*  158:     */   public XPathLexer(InputStream in)
/*  159:     */   {
/*  160:1010 */     this(new InputStreamReader(in));
/*  161:     */   }
/*  162:     */   
/*  163:     */   private static char[] zzUnpackCMap(String packed)
/*  164:     */   {
/*  165:1020 */     char[] map = new char[65536];
/*  166:1021 */     int i = 0;
/*  167:1022 */     int j = 0;
/*  168:     */     int count;
/*  169:1023 */     for (; i < 996; count > 0)
/*  170:     */     {
/*  171:1024 */       count = packed.charAt(i++);
/*  172:1025 */       char value = packed.charAt(i++);
/*  173:1026 */       map[(j++)] = value;count--;
/*  174:     */     }
/*  175:1028 */     return map;
/*  176:     */   }
/*  177:     */   
/*  178:     */   private boolean zzRefill()
/*  179:     */     throws IOException
/*  180:     */   {
/*  181:1042 */     if (this.zzStartRead > 0)
/*  182:     */     {
/*  183:1043 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/*  184:     */       
/*  185:     */ 
/*  186:     */ 
/*  187:     */ 
/*  188:1048 */       this.zzEndRead -= this.zzStartRead;
/*  189:1049 */       this.zzCurrentPos -= this.zzStartRead;
/*  190:1050 */       this.zzMarkedPos -= this.zzStartRead;
/*  191:1051 */       this.zzStartRead = 0;
/*  192:     */     }
/*  193:1055 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/*  194:     */     {
/*  195:1057 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/*  196:1058 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/*  197:1059 */       this.zzBuffer = newBuffer;
/*  198:     */     }
/*  199:1063 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/*  200:1066 */     if (numRead > 0)
/*  201:     */     {
/*  202:1067 */       this.zzEndRead += numRead;
/*  203:1068 */       return false;
/*  204:     */     }
/*  205:1071 */     if (numRead == 0)
/*  206:     */     {
/*  207:1072 */       int c = this.zzReader.read();
/*  208:1073 */       if (c == -1) {
/*  209:1074 */         return true;
/*  210:     */       }
/*  211:1076 */       this.zzBuffer[(this.zzEndRead++)] = ((char)c);
/*  212:1077 */       return false;
/*  213:     */     }
/*  214:1082 */     return true;
/*  215:     */   }
/*  216:     */   
/*  217:     */   public final void yyclose()
/*  218:     */     throws IOException
/*  219:     */   {
/*  220:1090 */     this.zzAtEOF = true;
/*  221:1091 */     this.zzEndRead = this.zzStartRead;
/*  222:1093 */     if (this.zzReader != null) {
/*  223:1094 */       this.zzReader.close();
/*  224:     */     }
/*  225:     */   }
/*  226:     */   
/*  227:     */   public final void yyreset(Reader reader)
/*  228:     */   {
/*  229:1109 */     this.zzReader = reader;
/*  230:1110 */     this.zzAtBOL = true;
/*  231:1111 */     this.zzAtEOF = false;
/*  232:1112 */     this.zzEOFDone = false;
/*  233:1113 */     this.zzEndRead = (this.zzStartRead = 0);
/*  234:1114 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/*  235:1115 */     this.yyline = (this.yychar = this.yycolumn = 0);
/*  236:1116 */     this.zzLexicalState = 0;
/*  237:     */   }
/*  238:     */   
/*  239:     */   public final int yystate()
/*  240:     */   {
/*  241:1124 */     return this.zzLexicalState;
/*  242:     */   }
/*  243:     */   
/*  244:     */   public final void yybegin(int newState)
/*  245:     */   {
/*  246:1134 */     this.zzLexicalState = newState;
/*  247:     */   }
/*  248:     */   
/*  249:     */   public final String yytext()
/*  250:     */   {
/*  251:1142 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*  252:     */   }
/*  253:     */   
/*  254:     */   public final char yycharat(int pos)
/*  255:     */   {
/*  256:1158 */     return this.zzBuffer[(this.zzStartRead + pos)];
/*  257:     */   }
/*  258:     */   
/*  259:     */   public final int yylength()
/*  260:     */   {
/*  261:1166 */     return this.zzMarkedPos - this.zzStartRead;
/*  262:     */   }
/*  263:     */   
/*  264:     */   private void zzScanError(int errorCode)
/*  265:     */   {
/*  266:     */     String message;
/*  267:     */     try
/*  268:     */     {
/*  269:1187 */       message = ZZ_ERROR_MSG[errorCode];
/*  270:     */     }
/*  271:     */     catch (ArrayIndexOutOfBoundsException e)
/*  272:     */     {
/*  273:1190 */       message = ZZ_ERROR_MSG[0];
/*  274:     */     }
/*  275:1193 */     throw new Error(message);
/*  276:     */   }
/*  277:     */   
/*  278:     */   public void yypushback(int number)
/*  279:     */   {
/*  280:1206 */     if (number > yylength()) {
/*  281:1207 */       zzScanError(2);
/*  282:     */     }
/*  283:1209 */     this.zzMarkedPos -= number;
/*  284:     */   }
/*  285:     */   
/*  286:     */   public Token yylex()
/*  287:     */     throws IOException
/*  288:     */   {
/*  289:1227 */     int zzEndReadL = this.zzEndRead;
/*  290:1228 */     char[] zzBufferL = this.zzBuffer;
/*  291:1229 */     char[] zzCMapL = ZZ_CMAP;
/*  292:     */     
/*  293:1231 */     int[] zzTransL = ZZ_TRANS;
/*  294:1232 */     int[] zzRowMapL = ZZ_ROWMAP;
/*  295:1233 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/*  296:     */     for (;;)
/*  297:     */     {
/*  298:1236 */       int zzMarkedPosL = this.zzMarkedPos;
/*  299:     */       
/*  300:1238 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/*  301:     */       
/*  302:1240 */       int zzAction = -1;
/*  303:     */       
/*  304:1242 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/*  305:     */       
/*  306:1244 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/*  307:     */       int zzInput;
/*  308:     */       for (;;)
/*  309:     */       {
/*  310:     */         int zzInput;
/*  311:1250 */         if (zzCurrentPosL < zzEndReadL)
/*  312:     */         {
/*  313:1251 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  314:     */         }
/*  315:     */         else
/*  316:     */         {
/*  317:1252 */           if (this.zzAtEOF)
/*  318:     */           {
/*  319:1253 */             int zzInput = -1;
/*  320:1254 */             break;
/*  321:     */           }
/*  322:1258 */           this.zzCurrentPos = zzCurrentPosL;
/*  323:1259 */           this.zzMarkedPos = zzMarkedPosL;
/*  324:1260 */           boolean eof = zzRefill();
/*  325:     */           
/*  326:1262 */           zzCurrentPosL = this.zzCurrentPos;
/*  327:1263 */           zzMarkedPosL = this.zzMarkedPos;
/*  328:1264 */           zzBufferL = this.zzBuffer;
/*  329:1265 */           zzEndReadL = this.zzEndRead;
/*  330:1266 */           if (eof)
/*  331:     */           {
/*  332:1267 */             int zzInput = -1;
/*  333:1268 */             break;
/*  334:     */           }
/*  335:1271 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  336:     */         }
/*  337:1274 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/*  338:1275 */         if (zzNext == -1) {
/*  339:     */           break;
/*  340:     */         }
/*  341:1276 */         this.zzState = zzNext;
/*  342:     */         
/*  343:1278 */         int zzAttributes = zzAttrL[this.zzState];
/*  344:1279 */         if ((zzAttributes & 0x1) == 1)
/*  345:     */         {
/*  346:1280 */           zzAction = this.zzState;
/*  347:1281 */           zzMarkedPosL = zzCurrentPosL;
/*  348:1282 */           if ((zzAttributes & 0x8) == 8) {
/*  349:     */             break;
/*  350:     */           }
/*  351:     */         }
/*  352:     */       }
/*  353:1289 */       this.zzMarkedPos = zzMarkedPosL;
/*  354:1291 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/*  355:     */       {
/*  356:     */       case 7: 
/*  357:1293 */         return token(TokenType.OPERATOR, -1);
/*  358:     */       case 19: 
/*  359:     */         break;
/*  360:     */       case 17: 
/*  361:1297 */         return token(TokenType.KEYWORD);
/*  362:     */       case 20: 
/*  363:     */         break;
/*  364:     */       case 2: 
/*  365:1301 */         return token(TokenType.NUMBER);
/*  366:     */       case 21: 
/*  367:     */         break;
/*  368:     */       case 15: 
/*  369:1305 */         yybegin(0);
/*  370:     */         
/*  371:1307 */         return token(TokenType.STRING, this.tokenStart, this.tokenLength + 1);
/*  372:     */       case 22: 
/*  373:     */         break;
/*  374:     */       case 4: 
/*  375:1311 */         return token(TokenType.OPERATOR);
/*  376:     */       case 23: 
/*  377:     */         break;
/*  378:     */       case 8: 
/*  379:1315 */         return token(TokenType.OPERATOR, 3);
/*  380:     */       case 24: 
/*  381:     */         break;
/*  382:     */       case 9: 
/*  383:1319 */         return token(TokenType.OPERATOR, -3);
/*  384:     */       case 25: 
/*  385:     */         break;
/*  386:     */       case 14: 
/*  387:1323 */         this.tokenLength += yylength();
/*  388:     */       case 26: 
/*  389:     */         break;
/*  390:     */       case 13: 
/*  391:1327 */         yybegin(4);
/*  392:1328 */         this.tokenStart = this.yychar;
/*  393:1329 */         this.tokenLength = 1;
/*  394:     */       case 27: 
/*  395:     */         break;
/*  396:     */       case 18: 
/*  397:1333 */         return token(TokenType.TYPE);
/*  398:     */       case 28: 
/*  399:     */         break;
/*  400:     */       case 1: 
/*  401:     */       case 29: 
/*  402:     */         break;
/*  403:     */       case 11: 
/*  404:1341 */         return token(TokenType.OPERATOR, -2);
/*  405:     */       case 30: 
/*  406:     */         break;
/*  407:     */       case 12: 
/*  408:1345 */         yybegin(2);
/*  409:1346 */         this.tokenStart = this.yychar;
/*  410:1347 */         this.tokenLength = 1;
/*  411:     */       case 31: 
/*  412:     */         break;
/*  413:     */       case 6: 
/*  414:1351 */         return token(TokenType.OPERATOR, 1);
/*  415:     */       case 32: 
/*  416:     */         break;
/*  417:     */       case 3: 
/*  418:1355 */         return token(TokenType.IDENTIFIER);
/*  419:     */       case 33: 
/*  420:     */         break;
/*  421:     */       case 16: 
/*  422:1359 */         return token(TokenType.KEYWORD2);
/*  423:     */       case 34: 
/*  424:     */         break;
/*  425:     */       case 10: 
/*  426:1363 */         return token(TokenType.OPERATOR, 2);
/*  427:     */       case 35: 
/*  428:     */         break;
/*  429:     */       case 5: 
/*  430:     */       case 36: 
/*  431:     */         break;
/*  432:     */       default: 
/*  433:1371 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos))
/*  434:     */         {
/*  435:1372 */           this.zzAtEOF = true;
/*  436:1373 */           return null;
/*  437:     */         }
/*  438:1376 */         zzScanError(1);
/*  439:     */       }
/*  440:     */     }
/*  441:     */   }
/*  442:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.XPathLexer
 * JD-Core Version:    0.7.0.1
 */