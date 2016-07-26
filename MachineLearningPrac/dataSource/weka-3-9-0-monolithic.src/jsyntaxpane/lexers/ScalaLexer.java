/*    1:     */ package jsyntaxpane.lexers;
/*    2:     */ 
/*    3:     */ import java.io.IOException;
/*    4:     */ import java.io.InputStream;
/*    5:     */ import java.io.InputStreamReader;
/*    6:     */ import java.io.Reader;
/*    7:     */ import jsyntaxpane.Token;
/*    8:     */ import jsyntaxpane.TokenType;
/*    9:     */ 
/*   10:     */ public final class ScalaLexer
/*   11:     */   extends DefaultJFlexLexer
/*   12:     */ {
/*   13:     */   public static final int YYEOF = -1;
/*   14:     */   private static final int ZZ_BUFFERSIZE = 16384;
/*   15:     */   public static final int STRING = 2;
/*   16:     */   public static final int JDOC_TAG = 8;
/*   17:     */   public static final int JDOC = 6;
/*   18:     */   public static final int YYINITIAL = 0;
/*   19:     */   public static final int CHARLITERAL = 4;
/*   20:  50 */   private static final int[] ZZ_LEXSTATE = { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4 };
/*   21:     */   private static final String ZZ_CMAP_PACKED = "";
/*   22: 154 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*   23: 159 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*   24:     */   private static final String ZZ_ACTION_PACKED_0 = "";
/*   25:     */   
/*   26:     */   private static int[] zzUnpackAction()
/*   27:     */   {
/*   28: 176 */     int[] result = new int[307];
/*   29: 177 */     int offset = 0;
/*   30: 178 */     offset = zzUnpackAction("", offset, result);
/*   31: 179 */     return result;
/*   32:     */   }
/*   33:     */   
/*   34:     */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*   35:     */   {
/*   36: 183 */     int i = 0;
/*   37: 184 */     int j = offset;
/*   38: 185 */     int l = packed.length();
/*   39:     */     int count;
/*   40: 186 */     for (; i < l; count > 0)
/*   41:     */     {
/*   42: 187 */       count = packed.charAt(i++);
/*   43: 188 */       int value = packed.charAt(i++);
/*   44: 189 */       result[(j++)] = value;count--;
/*   45:     */     }
/*   46: 191 */     return j;
/*   47:     */   }
/*   48:     */   
/*   49: 198 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*   50:     */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*   51:     */   
/*   52:     */   private static int[] zzUnpackRowMap()
/*   53:     */   {
/*   54: 242 */     int[] result = new int[307];
/*   55: 243 */     int offset = 0;
/*   56: 244 */     offset = zzUnpackRowMap("", offset, result);
/*   57: 245 */     return result;
/*   58:     */   }
/*   59:     */   
/*   60:     */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*   61:     */   {
/*   62: 249 */     int i = 0;
/*   63: 250 */     int j = offset;
/*   64: 251 */     int l = packed.length();
/*   65: 252 */     while (i < l)
/*   66:     */     {
/*   67: 253 */       int high = packed.charAt(i++) << '\020';
/*   68: 254 */       result[(j++)] = (high | packed.charAt(i++));
/*   69:     */     }
/*   70: 256 */     return j;
/*   71:     */   }
/*   72:     */   
/*   73: 262 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*   74:     */   private static final String ZZ_TRANS_PACKED_0 = "";
/*   75:     */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*   76:     */   private static final int ZZ_NO_MATCH = 1;
/*   77:     */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*   78:     */   
/*   79:     */   private static int[] zzUnpackTrans()
/*   80:     */   {
/*   81: 648 */     int[] result = new int[22199];
/*   82: 649 */     int offset = 0;
/*   83: 650 */     offset = zzUnpackTrans("", offset, result);
/*   84: 651 */     return result;
/*   85:     */   }
/*   86:     */   
/*   87:     */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*   88:     */   {
/*   89: 655 */     int i = 0;
/*   90: 656 */     int j = offset;
/*   91: 657 */     int l = packed.length();
/*   92:     */     int count;
/*   93: 658 */     for (; i < l; count > 0)
/*   94:     */     {
/*   95: 659 */       count = packed.charAt(i++);
/*   96: 660 */       int value = packed.charAt(i++);
/*   97: 661 */       value--;
/*   98: 662 */       result[(j++)] = value;count--;
/*   99:     */     }
/*  100: 664 */     return j;
/*  101:     */   }
/*  102:     */   
/*  103: 674 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/*  104: 683 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/*  105:     */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/*  106:     */   private Reader zzReader;
/*  107:     */   private int zzState;
/*  108:     */   
/*  109:     */   private static int[] zzUnpackAttribute()
/*  110:     */   {
/*  111: 694 */     int[] result = new int[307];
/*  112: 695 */     int offset = 0;
/*  113: 696 */     offset = zzUnpackAttribute("", offset, result);
/*  114: 697 */     return result;
/*  115:     */   }
/*  116:     */   
/*  117:     */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/*  118:     */   {
/*  119: 701 */     int i = 0;
/*  120: 702 */     int j = offset;
/*  121: 703 */     int l = packed.length();
/*  122:     */     int count;
/*  123: 704 */     for (; i < l; count > 0)
/*  124:     */     {
/*  125: 705 */       count = packed.charAt(i++);
/*  126: 706 */       int value = packed.charAt(i++);
/*  127: 707 */       result[(j++)] = value;count--;
/*  128:     */     }
/*  129: 709 */     return j;
/*  130:     */   }
/*  131:     */   
/*  132: 719 */   private int zzLexicalState = 0;
/*  133: 723 */   private char[] zzBuffer = new char[16384];
/*  134:     */   private int zzMarkedPos;
/*  135:     */   private int zzCurrentPos;
/*  136:     */   private int zzStartRead;
/*  137:     */   private int zzEndRead;
/*  138:     */   private int yyline;
/*  139:     */   private int yychar;
/*  140:     */   private int yycolumn;
/*  141: 753 */   private boolean zzAtBOL = true;
/*  142:     */   private boolean zzAtEOF;
/*  143:     */   private boolean zzEOFDone;
/*  144:     */   private static final byte PARAN = 1;
/*  145:     */   private static final byte BRACKET = 2;
/*  146:     */   private static final byte CURLY = 3;
/*  147:     */   
/*  148:     */   public ScalaLexer() {}
/*  149:     */   
/*  150:     */   public int yychar()
/*  151:     */   {
/*  152: 772 */     return this.yychar;
/*  153:     */   }
/*  154:     */   
/*  155:     */   public ScalaLexer(Reader in)
/*  156:     */   {
/*  157: 788 */     this.zzReader = in;
/*  158:     */   }
/*  159:     */   
/*  160:     */   public ScalaLexer(InputStream in)
/*  161:     */   {
/*  162: 798 */     this(new InputStreamReader(in));
/*  163:     */   }
/*  164:     */   
/*  165:     */   private static char[] zzUnpackCMap(String packed)
/*  166:     */   {
/*  167: 808 */     char[] map = new char[65536];
/*  168: 809 */     int i = 0;
/*  169: 810 */     int j = 0;
/*  170:     */     int count;
/*  171: 811 */     for (; i < 1830; count > 0)
/*  172:     */     {
/*  173: 812 */       count = packed.charAt(i++);
/*  174: 813 */       char value = packed.charAt(i++);
/*  175: 814 */       map[(j++)] = value;count--;
/*  176:     */     }
/*  177: 816 */     return map;
/*  178:     */   }
/*  179:     */   
/*  180:     */   private boolean zzRefill()
/*  181:     */     throws IOException
/*  182:     */   {
/*  183: 830 */     if (this.zzStartRead > 0)
/*  184:     */     {
/*  185: 831 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/*  186:     */       
/*  187:     */ 
/*  188:     */ 
/*  189:     */ 
/*  190: 836 */       this.zzEndRead -= this.zzStartRead;
/*  191: 837 */       this.zzCurrentPos -= this.zzStartRead;
/*  192: 838 */       this.zzMarkedPos -= this.zzStartRead;
/*  193: 839 */       this.zzStartRead = 0;
/*  194:     */     }
/*  195: 843 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/*  196:     */     {
/*  197: 845 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/*  198: 846 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/*  199: 847 */       this.zzBuffer = newBuffer;
/*  200:     */     }
/*  201: 851 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/*  202: 854 */     if (numRead > 0)
/*  203:     */     {
/*  204: 855 */       this.zzEndRead += numRead;
/*  205: 856 */       return false;
/*  206:     */     }
/*  207: 859 */     if (numRead == 0)
/*  208:     */     {
/*  209: 860 */       int c = this.zzReader.read();
/*  210: 861 */       if (c == -1) {
/*  211: 862 */         return true;
/*  212:     */       }
/*  213: 864 */       this.zzBuffer[(this.zzEndRead++)] = ((char)c);
/*  214: 865 */       return false;
/*  215:     */     }
/*  216: 870 */     return true;
/*  217:     */   }
/*  218:     */   
/*  219:     */   public final void yyclose()
/*  220:     */     throws IOException
/*  221:     */   {
/*  222: 878 */     this.zzAtEOF = true;
/*  223: 879 */     this.zzEndRead = this.zzStartRead;
/*  224: 881 */     if (this.zzReader != null) {
/*  225: 882 */       this.zzReader.close();
/*  226:     */     }
/*  227:     */   }
/*  228:     */   
/*  229:     */   public final void yyreset(Reader reader)
/*  230:     */   {
/*  231: 897 */     this.zzReader = reader;
/*  232: 898 */     this.zzAtBOL = true;
/*  233: 899 */     this.zzAtEOF = false;
/*  234: 900 */     this.zzEOFDone = false;
/*  235: 901 */     this.zzEndRead = (this.zzStartRead = 0);
/*  236: 902 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/*  237: 903 */     this.yyline = (this.yychar = this.yycolumn = 0);
/*  238: 904 */     this.zzLexicalState = 0;
/*  239:     */   }
/*  240:     */   
/*  241:     */   public final int yystate()
/*  242:     */   {
/*  243: 912 */     return this.zzLexicalState;
/*  244:     */   }
/*  245:     */   
/*  246:     */   public final void yybegin(int newState)
/*  247:     */   {
/*  248: 922 */     this.zzLexicalState = newState;
/*  249:     */   }
/*  250:     */   
/*  251:     */   public final String yytext()
/*  252:     */   {
/*  253: 930 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*  254:     */   }
/*  255:     */   
/*  256:     */   public final char yycharat(int pos)
/*  257:     */   {
/*  258: 946 */     return this.zzBuffer[(this.zzStartRead + pos)];
/*  259:     */   }
/*  260:     */   
/*  261:     */   public final int yylength()
/*  262:     */   {
/*  263: 954 */     return this.zzMarkedPos - this.zzStartRead;
/*  264:     */   }
/*  265:     */   
/*  266:     */   private void zzScanError(int errorCode)
/*  267:     */   {
/*  268:     */     String message;
/*  269:     */     try
/*  270:     */     {
/*  271: 975 */       message = ZZ_ERROR_MSG[errorCode];
/*  272:     */     }
/*  273:     */     catch (ArrayIndexOutOfBoundsException e)
/*  274:     */     {
/*  275: 978 */       message = ZZ_ERROR_MSG[0];
/*  276:     */     }
/*  277: 981 */     throw new Error(message);
/*  278:     */   }
/*  279:     */   
/*  280:     */   public void yypushback(int number)
/*  281:     */   {
/*  282: 994 */     if (number > yylength()) {
/*  283: 995 */       zzScanError(2);
/*  284:     */     }
/*  285: 997 */     this.zzMarkedPos -= number;
/*  286:     */   }
/*  287:     */   
/*  288:     */   public Token yylex()
/*  289:     */     throws IOException
/*  290:     */   {
/*  291:1015 */     int zzEndReadL = this.zzEndRead;
/*  292:1016 */     char[] zzBufferL = this.zzBuffer;
/*  293:1017 */     char[] zzCMapL = ZZ_CMAP;
/*  294:     */     
/*  295:1019 */     int[] zzTransL = ZZ_TRANS;
/*  296:1020 */     int[] zzRowMapL = ZZ_ROWMAP;
/*  297:1021 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/*  298:     */     for (;;)
/*  299:     */     {
/*  300:1024 */       int zzMarkedPosL = this.zzMarkedPos;
/*  301:     */       
/*  302:1026 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/*  303:     */       
/*  304:1028 */       int zzAction = -1;
/*  305:     */       
/*  306:1030 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/*  307:     */       
/*  308:1032 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/*  309:     */       int zzInput;
/*  310:     */       for (;;)
/*  311:     */       {
/*  312:     */         int zzInput;
/*  313:1038 */         if (zzCurrentPosL < zzEndReadL)
/*  314:     */         {
/*  315:1039 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  316:     */         }
/*  317:     */         else
/*  318:     */         {
/*  319:1040 */           if (this.zzAtEOF)
/*  320:     */           {
/*  321:1041 */             int zzInput = -1;
/*  322:1042 */             break;
/*  323:     */           }
/*  324:1046 */           this.zzCurrentPos = zzCurrentPosL;
/*  325:1047 */           this.zzMarkedPos = zzMarkedPosL;
/*  326:1048 */           boolean eof = zzRefill();
/*  327:     */           
/*  328:1050 */           zzCurrentPosL = this.zzCurrentPos;
/*  329:1051 */           zzMarkedPosL = this.zzMarkedPos;
/*  330:1052 */           zzBufferL = this.zzBuffer;
/*  331:1053 */           zzEndReadL = this.zzEndRead;
/*  332:1054 */           if (eof)
/*  333:     */           {
/*  334:1055 */             int zzInput = -1;
/*  335:1056 */             break;
/*  336:     */           }
/*  337:1059 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  338:     */         }
/*  339:1062 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/*  340:1063 */         if (zzNext == -1) {
/*  341:     */           break;
/*  342:     */         }
/*  343:1064 */         this.zzState = zzNext;
/*  344:     */         
/*  345:1066 */         int zzAttributes = zzAttrL[this.zzState];
/*  346:1067 */         if ((zzAttributes & 0x1) == 1)
/*  347:     */         {
/*  348:1068 */           zzAction = this.zzState;
/*  349:1069 */           zzMarkedPosL = zzCurrentPosL;
/*  350:1070 */           if ((zzAttributes & 0x8) == 8) {
/*  351:     */             break;
/*  352:     */           }
/*  353:     */         }
/*  354:     */       }
/*  355:1077 */       this.zzMarkedPos = zzMarkedPosL;
/*  356:1079 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/*  357:     */       {
/*  358:     */       case 8: 
/*  359:1081 */         return token(TokenType.OPERATOR, -1);
/*  360:     */       case 28: 
/*  361:     */         break;
/*  362:     */       case 20: 
/*  363:1085 */         return token(TokenType.KEYWORD);
/*  364:     */       case 29: 
/*  365:     */         break;
/*  366:     */       case 4: 
/*  367:1089 */         return token(TokenType.NUMBER);
/*  368:     */       case 30: 
/*  369:     */         break;
/*  370:     */       case 18: 
/*  371:1093 */         yybegin(6);
/*  372:     */         
/*  373:1095 */         int start = this.tokenStart;
/*  374:1096 */         this.tokenStart = this.yychar;
/*  375:1097 */         int len = this.tokenLength;
/*  376:1098 */         this.tokenLength = 1;
/*  377:1099 */         return token(TokenType.COMMENT2, start, len);
/*  378:     */       case 31: 
/*  379:     */         break;
/*  380:     */       case 22: 
/*  381:1103 */         yybegin(0);
/*  382:1104 */         return token(TokenType.COMMENT, this.tokenStart, this.tokenLength + 2);
/*  383:     */       case 32: 
/*  384:     */         break;
/*  385:     */       case 2: 
/*  386:1108 */         return token(TokenType.OPERATOR);
/*  387:     */       case 33: 
/*  388:     */         break;
/*  389:     */       case 9: 
/*  390:1112 */         return token(TokenType.OPERATOR, 3);
/*  391:     */       case 34: 
/*  392:     */         break;
/*  393:     */       case 10: 
/*  394:1116 */         return token(TokenType.OPERATOR, -3);
/*  395:     */       case 35: 
/*  396:     */         break;
/*  397:     */       case 17: 
/*  398:1120 */         yybegin(8);
/*  399:1121 */         int start = this.tokenStart;
/*  400:1122 */         this.tokenStart = this.yychar;
/*  401:1123 */         int len = this.tokenLength;
/*  402:1124 */         this.tokenLength = 1;
/*  403:1125 */         return token(TokenType.COMMENT, start, len);
/*  404:     */       case 36: 
/*  405:     */         break;
/*  406:     */       case 13: 
/*  407:1129 */         this.tokenLength += yylength();
/*  408:     */       case 37: 
/*  409:     */         break;
/*  410:     */       case 14: 
/*  411:1133 */         yybegin(0);
/*  412:     */       case 38: 
/*  413:     */         break;
/*  414:     */       case 6: 
/*  415:1137 */         yybegin(4);
/*  416:1138 */         this.tokenStart = this.yychar;
/*  417:1139 */         this.tokenLength = 1;
/*  418:     */       case 39: 
/*  419:     */         break;
/*  420:     */       case 23: 
/*  421:1143 */         yybegin(6);
/*  422:1144 */         this.tokenStart = this.yychar;
/*  423:1145 */         this.tokenLength = 3;
/*  424:     */       case 40: 
/*  425:     */         break;
/*  426:     */       case 15: 
/*  427:1149 */         yybegin(0);
/*  428:     */         
/*  429:1151 */         return token(TokenType.STRING, this.tokenStart, this.tokenLength + 1);
/*  430:     */       case 41: 
/*  431:     */         break;
/*  432:     */       case 24: 
/*  433:1155 */         return token(TokenType.TYPE);
/*  434:     */       case 42: 
/*  435:     */         break;
/*  436:     */       case 27: 
/*  437:1159 */         return token(TokenType.WARNING);
/*  438:     */       case 43: 
/*  439:     */         break;
/*  440:     */       case 12: 
/*  441:1163 */         return token(TokenType.OPERATOR, -2);
/*  442:     */       case 44: 
/*  443:     */         break;
/*  444:     */       case 7: 
/*  445:1167 */         return token(TokenType.OPERATOR, 1);
/*  446:     */       case 45: 
/*  447:     */         break;
/*  448:     */       case 26: 
/*  449:1171 */         return token(TokenType.KEYWORD2);
/*  450:     */       case 46: 
/*  451:     */         break;
/*  452:     */       case 3: 
/*  453:1175 */         return token(TokenType.IDENTIFIER);
/*  454:     */       case 47: 
/*  455:     */         break;
/*  456:     */       case 21: 
/*  457:1179 */         this.tokenLength += 2;
/*  458:     */       case 48: 
/*  459:     */         break;
/*  460:     */       case 25: 
/*  461:1183 */         return token(TokenType.ERROR);
/*  462:     */       case 49: 
/*  463:     */         break;
/*  464:     */       case 16: 
/*  465:1187 */         this.tokenLength += 1;
/*  466:     */       case 50: 
/*  467:     */         break;
/*  468:     */       case 11: 
/*  469:1191 */         return token(TokenType.OPERATOR, 2);
/*  470:     */       case 51: 
/*  471:     */         break;
/*  472:     */       case 19: 
/*  473:1195 */         return token(TokenType.COMMENT);
/*  474:     */       case 52: 
/*  475:     */         break;
/*  476:     */       case 5: 
/*  477:1199 */         yybegin(2);
/*  478:1200 */         this.tokenStart = this.yychar;
/*  479:1201 */         this.tokenLength = 1;
/*  480:     */       case 53: 
/*  481:     */         break;
/*  482:     */       case 1: 
/*  483:     */       case 54: 
/*  484:     */         break;
/*  485:     */       default: 
/*  486:1209 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos))
/*  487:     */         {
/*  488:1210 */           this.zzAtEOF = true;
/*  489:     */           
/*  490:1212 */           return null;
/*  491:     */         }
/*  492:1216 */         zzScanError(1);
/*  493:     */       }
/*  494:     */     }
/*  495:     */   }
/*  496:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.ScalaLexer
 * JD-Core Version:    0.7.0.1
 */