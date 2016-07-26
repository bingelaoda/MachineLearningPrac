/*    1:     */ package jsyntaxpane.lexers;
/*    2:     */ 
/*    3:     */ import java.io.IOException;
/*    4:     */ import java.io.InputStream;
/*    5:     */ import java.io.InputStreamReader;
/*    6:     */ import java.io.Reader;
/*    7:     */ import jsyntaxpane.Token;
/*    8:     */ import jsyntaxpane.TokenType;
/*    9:     */ 
/*   10:     */ public final class SqlLexer
/*   11:     */   extends DefaultJFlexLexer
/*   12:     */ {
/*   13:     */   public static final int YYEOF = -1;
/*   14:     */   private static final int ZZ_BUFFERSIZE = 16384;
/*   15:     */   public static final int YYINITIAL = 0;
/*   16:     */   public static final int DQ_STRING = 2;
/*   17:     */   public static final int SQ_STRING = 4;
/*   18:  48 */   private static final int[] ZZ_LEXSTATE = { 0, 0, 1, 1, 2, 2 };
/*   19:     */   private static final String ZZ_CMAP_PACKED = "";
/*   20: 151 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*   21: 156 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*   22:     */   private static final String ZZ_ACTION_PACKED_0 = "";
/*   23:     */   
/*   24:     */   private static int[] zzUnpackAction()
/*   25:     */   {
/*   26: 174 */     int[] result = new int[484];
/*   27: 175 */     int offset = 0;
/*   28: 176 */     offset = zzUnpackAction("", offset, result);
/*   29: 177 */     return result;
/*   30:     */   }
/*   31:     */   
/*   32:     */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*   33:     */   {
/*   34: 181 */     int i = 0;
/*   35: 182 */     int j = offset;
/*   36: 183 */     int l = packed.length();
/*   37:     */     int count;
/*   38: 184 */     for (; i < l; count > 0)
/*   39:     */     {
/*   40: 185 */       count = packed.charAt(i++);
/*   41: 186 */       int value = packed.charAt(i++);
/*   42: 187 */       result[(j++)] = value;count--;
/*   43:     */     }
/*   44: 189 */     return j;
/*   45:     */   }
/*   46:     */   
/*   47: 196 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*   48:     */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*   49:     */   
/*   50:     */   private static int[] zzUnpackRowMap()
/*   51:     */   {
/*   52: 262 */     int[] result = new int[484];
/*   53: 263 */     int offset = 0;
/*   54: 264 */     offset = zzUnpackRowMap("", offset, result);
/*   55: 265 */     return result;
/*   56:     */   }
/*   57:     */   
/*   58:     */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*   59:     */   {
/*   60: 269 */     int i = 0;
/*   61: 270 */     int j = offset;
/*   62: 271 */     int l = packed.length();
/*   63: 272 */     while (i < l)
/*   64:     */     {
/*   65: 273 */       int high = packed.charAt(i++) << '\020';
/*   66: 274 */       result[(j++)] = (high | packed.charAt(i++));
/*   67:     */     }
/*   68: 276 */     return j;
/*   69:     */   }
/*   70:     */   
/*   71: 282 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*   72:     */   private static final String ZZ_TRANS_PACKED_0 = "";
/*   73:     */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*   74:     */   private static final int ZZ_NO_MATCH = 1;
/*   75:     */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*   76:     */   
/*   77:     */   private static int[] zzUnpackTrans()
/*   78:     */   {
/*   79: 836 */     int[] result = new int[22043];
/*   80: 837 */     int offset = 0;
/*   81: 838 */     offset = zzUnpackTrans("", offset, result);
/*   82: 839 */     return result;
/*   83:     */   }
/*   84:     */   
/*   85:     */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*   86:     */   {
/*   87: 843 */     int i = 0;
/*   88: 844 */     int j = offset;
/*   89: 845 */     int l = packed.length();
/*   90:     */     int count;
/*   91: 846 */     for (; i < l; count > 0)
/*   92:     */     {
/*   93: 847 */       count = packed.charAt(i++);
/*   94: 848 */       int value = packed.charAt(i++);
/*   95: 849 */       value--;
/*   96: 850 */       result[(j++)] = value;count--;
/*   97:     */     }
/*   98: 852 */     return j;
/*   99:     */   }
/*  100:     */   
/*  101: 862 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/*  102: 871 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/*  103:     */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/*  104:     */   private Reader zzReader;
/*  105:     */   private int zzState;
/*  106:     */   
/*  107:     */   private static int[] zzUnpackAttribute()
/*  108:     */   {
/*  109: 882 */     int[] result = new int[484];
/*  110: 883 */     int offset = 0;
/*  111: 884 */     offset = zzUnpackAttribute("", offset, result);
/*  112: 885 */     return result;
/*  113:     */   }
/*  114:     */   
/*  115:     */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/*  116:     */   {
/*  117: 889 */     int i = 0;
/*  118: 890 */     int j = offset;
/*  119: 891 */     int l = packed.length();
/*  120:     */     int count;
/*  121: 892 */     for (; i < l; count > 0)
/*  122:     */     {
/*  123: 893 */       count = packed.charAt(i++);
/*  124: 894 */       int value = packed.charAt(i++);
/*  125: 895 */       result[(j++)] = value;count--;
/*  126:     */     }
/*  127: 897 */     return j;
/*  128:     */   }
/*  129:     */   
/*  130: 907 */   private int zzLexicalState = 0;
/*  131: 911 */   private char[] zzBuffer = new char[16384];
/*  132:     */   private int zzMarkedPos;
/*  133:     */   private int zzCurrentPos;
/*  134:     */   private int zzStartRead;
/*  135:     */   private int zzEndRead;
/*  136:     */   private int yyline;
/*  137:     */   private int yychar;
/*  138:     */   private int yycolumn;
/*  139: 941 */   private boolean zzAtBOL = true;
/*  140:     */   private boolean zzAtEOF;
/*  141:     */   private boolean zzEOFDone;
/*  142:     */   
/*  143:     */   public SqlLexer() {}
/*  144:     */   
/*  145:     */   public int yychar()
/*  146:     */   {
/*  147: 959 */     return this.yychar;
/*  148:     */   }
/*  149:     */   
/*  150:     */   public SqlLexer(Reader in)
/*  151:     */   {
/*  152: 971 */     this.zzReader = in;
/*  153:     */   }
/*  154:     */   
/*  155:     */   public SqlLexer(InputStream in)
/*  156:     */   {
/*  157: 981 */     this(new InputStreamReader(in));
/*  158:     */   }
/*  159:     */   
/*  160:     */   private static char[] zzUnpackCMap(String packed)
/*  161:     */   {
/*  162: 991 */     char[] map = new char[65536];
/*  163: 992 */     int i = 0;
/*  164: 993 */     int j = 0;
/*  165:     */     int count;
/*  166: 994 */     for (; i < 1820; count > 0)
/*  167:     */     {
/*  168: 995 */       count = packed.charAt(i++);
/*  169: 996 */       char value = packed.charAt(i++);
/*  170: 997 */       map[(j++)] = value;count--;
/*  171:     */     }
/*  172: 999 */     return map;
/*  173:     */   }
/*  174:     */   
/*  175:     */   private boolean zzRefill()
/*  176:     */     throws IOException
/*  177:     */   {
/*  178:1013 */     if (this.zzStartRead > 0)
/*  179:     */     {
/*  180:1014 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/*  181:     */       
/*  182:     */ 
/*  183:     */ 
/*  184:     */ 
/*  185:1019 */       this.zzEndRead -= this.zzStartRead;
/*  186:1020 */       this.zzCurrentPos -= this.zzStartRead;
/*  187:1021 */       this.zzMarkedPos -= this.zzStartRead;
/*  188:1022 */       this.zzStartRead = 0;
/*  189:     */     }
/*  190:1026 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/*  191:     */     {
/*  192:1028 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/*  193:1029 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/*  194:1030 */       this.zzBuffer = newBuffer;
/*  195:     */     }
/*  196:1034 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/*  197:1037 */     if (numRead > 0)
/*  198:     */     {
/*  199:1038 */       this.zzEndRead += numRead;
/*  200:1039 */       return false;
/*  201:     */     }
/*  202:1042 */     if (numRead == 0)
/*  203:     */     {
/*  204:1043 */       int c = this.zzReader.read();
/*  205:1044 */       if (c == -1) {
/*  206:1045 */         return true;
/*  207:     */       }
/*  208:1047 */       this.zzBuffer[(this.zzEndRead++)] = ((char)c);
/*  209:1048 */       return false;
/*  210:     */     }
/*  211:1053 */     return true;
/*  212:     */   }
/*  213:     */   
/*  214:     */   public final void yyclose()
/*  215:     */     throws IOException
/*  216:     */   {
/*  217:1061 */     this.zzAtEOF = true;
/*  218:1062 */     this.zzEndRead = this.zzStartRead;
/*  219:1064 */     if (this.zzReader != null) {
/*  220:1065 */       this.zzReader.close();
/*  221:     */     }
/*  222:     */   }
/*  223:     */   
/*  224:     */   public final void yyreset(Reader reader)
/*  225:     */   {
/*  226:1080 */     this.zzReader = reader;
/*  227:1081 */     this.zzAtBOL = true;
/*  228:1082 */     this.zzAtEOF = false;
/*  229:1083 */     this.zzEOFDone = false;
/*  230:1084 */     this.zzEndRead = (this.zzStartRead = 0);
/*  231:1085 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/*  232:1086 */     this.yyline = (this.yychar = this.yycolumn = 0);
/*  233:1087 */     this.zzLexicalState = 0;
/*  234:     */   }
/*  235:     */   
/*  236:     */   public final int yystate()
/*  237:     */   {
/*  238:1095 */     return this.zzLexicalState;
/*  239:     */   }
/*  240:     */   
/*  241:     */   public final void yybegin(int newState)
/*  242:     */   {
/*  243:1105 */     this.zzLexicalState = newState;
/*  244:     */   }
/*  245:     */   
/*  246:     */   public final String yytext()
/*  247:     */   {
/*  248:1113 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*  249:     */   }
/*  250:     */   
/*  251:     */   public final char yycharat(int pos)
/*  252:     */   {
/*  253:1129 */     return this.zzBuffer[(this.zzStartRead + pos)];
/*  254:     */   }
/*  255:     */   
/*  256:     */   public final int yylength()
/*  257:     */   {
/*  258:1137 */     return this.zzMarkedPos - this.zzStartRead;
/*  259:     */   }
/*  260:     */   
/*  261:     */   private void zzScanError(int errorCode)
/*  262:     */   {
/*  263:     */     String message;
/*  264:     */     try
/*  265:     */     {
/*  266:1158 */       message = ZZ_ERROR_MSG[errorCode];
/*  267:     */     }
/*  268:     */     catch (ArrayIndexOutOfBoundsException e)
/*  269:     */     {
/*  270:1161 */       message = ZZ_ERROR_MSG[0];
/*  271:     */     }
/*  272:1164 */     throw new Error(message);
/*  273:     */   }
/*  274:     */   
/*  275:     */   public void yypushback(int number)
/*  276:     */   {
/*  277:1177 */     if (number > yylength()) {
/*  278:1178 */       zzScanError(2);
/*  279:     */     }
/*  280:1180 */     this.zzMarkedPos -= number;
/*  281:     */   }
/*  282:     */   
/*  283:     */   public Token yylex()
/*  284:     */     throws IOException
/*  285:     */   {
/*  286:1198 */     int zzEndReadL = this.zzEndRead;
/*  287:1199 */     char[] zzBufferL = this.zzBuffer;
/*  288:1200 */     char[] zzCMapL = ZZ_CMAP;
/*  289:     */     
/*  290:1202 */     int[] zzTransL = ZZ_TRANS;
/*  291:1203 */     int[] zzRowMapL = ZZ_ROWMAP;
/*  292:1204 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/*  293:     */     for (;;)
/*  294:     */     {
/*  295:1207 */       int zzMarkedPosL = this.zzMarkedPos;
/*  296:     */       
/*  297:1209 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/*  298:     */       
/*  299:1211 */       int zzAction = -1;
/*  300:     */       
/*  301:1213 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/*  302:     */       
/*  303:1215 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/*  304:     */       int zzInput;
/*  305:     */       for (;;)
/*  306:     */       {
/*  307:     */         int zzInput;
/*  308:1221 */         if (zzCurrentPosL < zzEndReadL)
/*  309:     */         {
/*  310:1222 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  311:     */         }
/*  312:     */         else
/*  313:     */         {
/*  314:1223 */           if (this.zzAtEOF)
/*  315:     */           {
/*  316:1224 */             int zzInput = -1;
/*  317:1225 */             break;
/*  318:     */           }
/*  319:1229 */           this.zzCurrentPos = zzCurrentPosL;
/*  320:1230 */           this.zzMarkedPos = zzMarkedPosL;
/*  321:1231 */           boolean eof = zzRefill();
/*  322:     */           
/*  323:1233 */           zzCurrentPosL = this.zzCurrentPos;
/*  324:1234 */           zzMarkedPosL = this.zzMarkedPos;
/*  325:1235 */           zzBufferL = this.zzBuffer;
/*  326:1236 */           zzEndReadL = this.zzEndRead;
/*  327:1237 */           if (eof)
/*  328:     */           {
/*  329:1238 */             int zzInput = -1;
/*  330:1239 */             break;
/*  331:     */           }
/*  332:1242 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  333:     */         }
/*  334:1245 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/*  335:1246 */         if (zzNext == -1) {
/*  336:     */           break;
/*  337:     */         }
/*  338:1247 */         this.zzState = zzNext;
/*  339:     */         
/*  340:1249 */         int zzAttributes = zzAttrL[this.zzState];
/*  341:1250 */         if ((zzAttributes & 0x1) == 1)
/*  342:     */         {
/*  343:1251 */           zzAction = this.zzState;
/*  344:1252 */           zzMarkedPosL = zzCurrentPosL;
/*  345:1253 */           if ((zzAttributes & 0x8) == 8) {
/*  346:     */             break;
/*  347:     */           }
/*  348:     */         }
/*  349:     */       }
/*  350:1260 */       this.zzMarkedPos = zzMarkedPosL;
/*  351:1262 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/*  352:     */       {
/*  353:     */       case 12: 
/*  354:1264 */         return token(TokenType.KEYWORD);
/*  355:     */       case 14: 
/*  356:     */         break;
/*  357:     */       case 7: 
/*  358:1268 */         yybegin(4);
/*  359:1269 */         this.tokenStart = this.yychar;
/*  360:1270 */         this.tokenLength = 1;
/*  361:     */       case 15: 
/*  362:     */         break;
/*  363:     */       case 5: 
/*  364:1274 */         return token(TokenType.OPERATOR);
/*  365:     */       case 16: 
/*  366:     */         break;
/*  367:     */       case 13: 
/*  368:1278 */         this.tokenLength += 2;
/*  369:     */       case 17: 
/*  370:     */         break;
/*  371:     */       case 3: 
/*  372:1282 */         return token(TokenType.IDENTIFIER);
/*  373:     */       case 18: 
/*  374:     */         break;
/*  375:     */       case 11: 
/*  376:1286 */         return token(TokenType.COMMENT);
/*  377:     */       case 19: 
/*  378:     */         break;
/*  379:     */       case 10: 
/*  380:1290 */         yybegin(0);
/*  381:     */         
/*  382:1292 */         return token(TokenType.STRING, this.tokenStart, this.tokenLength + 1);
/*  383:     */       case 20: 
/*  384:     */         break;
/*  385:     */       case 9: 
/*  386:1296 */         yybegin(0);
/*  387:     */       case 21: 
/*  388:     */         break;
/*  389:     */       case 4: 
/*  390:1300 */         return token(TokenType.NUMBER);
/*  391:     */       case 22: 
/*  392:     */         break;
/*  393:     */       case 6: 
/*  394:1304 */         yybegin(2);
/*  395:1305 */         this.tokenStart = this.yychar;
/*  396:1306 */         this.tokenLength = 1;
/*  397:     */       case 23: 
/*  398:     */         break;
/*  399:     */       case 2: 
/*  400:     */       case 24: 
/*  401:     */         break;
/*  402:     */       case 1: 
/*  403:     */       case 25: 
/*  404:     */         break;
/*  405:     */       case 8: 
/*  406:1318 */         this.tokenLength += yylength();
/*  407:     */       case 26: 
/*  408:     */         break;
/*  409:     */       default: 
/*  410:1322 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos))
/*  411:     */         {
/*  412:1323 */           this.zzAtEOF = true;
/*  413:     */           
/*  414:1325 */           return null;
/*  415:     */         }
/*  416:1329 */         zzScanError(1);
/*  417:     */       }
/*  418:     */     }
/*  419:     */   }
/*  420:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.SqlLexer
 * JD-Core Version:    0.7.0.1
 */