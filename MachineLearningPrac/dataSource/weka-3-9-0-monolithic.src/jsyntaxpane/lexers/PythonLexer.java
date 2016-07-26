/*    1:     */ package jsyntaxpane.lexers;
/*    2:     */ 
/*    3:     */ import java.io.IOException;
/*    4:     */ import java.io.InputStream;
/*    5:     */ import java.io.InputStreamReader;
/*    6:     */ import java.io.Reader;
/*    7:     */ import jsyntaxpane.Token;
/*    8:     */ import jsyntaxpane.TokenType;
/*    9:     */ 
/*   10:     */ public final class PythonLexer
/*   11:     */   extends DefaultJFlexLexer
/*   12:     */ {
/*   13:     */   public static final int YYEOF = -1;
/*   14:     */   private static final int ZZ_BUFFERSIZE = 16384;
/*   15:     */   public static final int SQSTRING = 6;
/*   16:     */   public static final int ML_STRING = 4;
/*   17:     */   public static final int STRING = 2;
/*   18:     */   public static final int SQML_STRING = 8;
/*   19:     */   public static final int YYINITIAL = 0;
/*   20:  51 */   private static final int[] ZZ_LEXSTATE = { 0, 0, 1, 1, 2, 2, 3, 3, 4, 4 };
/*   21:     */   private static final String ZZ_CMAP_PACKED = "";
/*   22:  72 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*   23:  77 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*   24:     */   private static final String ZZ_ACTION_PACKED_0 = "";
/*   25:     */   
/*   26:     */   private static int[] zzUnpackAction()
/*   27:     */   {
/*   28:  96 */     int[] result = new int[342];
/*   29:  97 */     int offset = 0;
/*   30:  98 */     offset = zzUnpackAction("", offset, result);
/*   31:  99 */     return result;
/*   32:     */   }
/*   33:     */   
/*   34:     */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*   35:     */   {
/*   36: 103 */     int i = 0;
/*   37: 104 */     int j = offset;
/*   38: 105 */     int l = packed.length();
/*   39:     */     int count;
/*   40: 106 */     for (; i < l; count > 0)
/*   41:     */     {
/*   42: 107 */       count = packed.charAt(i++);
/*   43: 108 */       int value = packed.charAt(i++);
/*   44: 109 */       result[(j++)] = value;count--;
/*   45:     */     }
/*   46: 111 */     return j;
/*   47:     */   }
/*   48:     */   
/*   49: 118 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*   50:     */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*   51:     */   
/*   52:     */   private static int[] zzUnpackRowMap()
/*   53:     */   {
/*   54: 166 */     int[] result = new int[342];
/*   55: 167 */     int offset = 0;
/*   56: 168 */     offset = zzUnpackRowMap("", offset, result);
/*   57: 169 */     return result;
/*   58:     */   }
/*   59:     */   
/*   60:     */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*   61:     */   {
/*   62: 173 */     int i = 0;
/*   63: 174 */     int j = offset;
/*   64: 175 */     int l = packed.length();
/*   65: 176 */     while (i < l)
/*   66:     */     {
/*   67: 177 */       int high = packed.charAt(i++) << '\020';
/*   68: 178 */       result[(j++)] = (high | packed.charAt(i++));
/*   69:     */     }
/*   70: 180 */     return j;
/*   71:     */   }
/*   72:     */   
/*   73: 186 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*   74:     */   private static final String ZZ_TRANS_PACKED_0 = "";
/*   75:     */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*   76:     */   private static final int ZZ_NO_MATCH = 1;
/*   77:     */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*   78:     */   
/*   79:     */   private static int[] zzUnpackTrans()
/*   80:     */   {
/*   81: 603 */     int[] result = new int[20724];
/*   82: 604 */     int offset = 0;
/*   83: 605 */     offset = zzUnpackTrans("", offset, result);
/*   84: 606 */     return result;
/*   85:     */   }
/*   86:     */   
/*   87:     */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*   88:     */   {
/*   89: 610 */     int i = 0;
/*   90: 611 */     int j = offset;
/*   91: 612 */     int l = packed.length();
/*   92:     */     int count;
/*   93: 613 */     for (; i < l; count > 0)
/*   94:     */     {
/*   95: 614 */       count = packed.charAt(i++);
/*   96: 615 */       int value = packed.charAt(i++);
/*   97: 616 */       value--;
/*   98: 617 */       result[(j++)] = value;count--;
/*   99:     */     }
/*  100: 619 */     return j;
/*  101:     */   }
/*  102:     */   
/*  103: 629 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/*  104: 638 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/*  105:     */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/*  106:     */   private Reader zzReader;
/*  107:     */   private int zzState;
/*  108:     */   
/*  109:     */   private static int[] zzUnpackAttribute()
/*  110:     */   {
/*  111: 651 */     int[] result = new int[342];
/*  112: 652 */     int offset = 0;
/*  113: 653 */     offset = zzUnpackAttribute("", offset, result);
/*  114: 654 */     return result;
/*  115:     */   }
/*  116:     */   
/*  117:     */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/*  118:     */   {
/*  119: 658 */     int i = 0;
/*  120: 659 */     int j = offset;
/*  121: 660 */     int l = packed.length();
/*  122:     */     int count;
/*  123: 661 */     for (; i < l; count > 0)
/*  124:     */     {
/*  125: 662 */       count = packed.charAt(i++);
/*  126: 663 */       int value = packed.charAt(i++);
/*  127: 664 */       result[(j++)] = value;count--;
/*  128:     */     }
/*  129: 666 */     return j;
/*  130:     */   }
/*  131:     */   
/*  132: 676 */   private int zzLexicalState = 0;
/*  133: 680 */   private char[] zzBuffer = new char[16384];
/*  134:     */   private int zzMarkedPos;
/*  135:     */   private int zzCurrentPos;
/*  136:     */   private int zzStartRead;
/*  137:     */   private int zzEndRead;
/*  138:     */   private int yyline;
/*  139:     */   private int yychar;
/*  140:     */   private int yycolumn;
/*  141: 710 */   private boolean zzAtBOL = true;
/*  142:     */   private boolean zzAtEOF;
/*  143:     */   private boolean zzEOFDone;
/*  144:     */   private static final byte PARAN = 1;
/*  145:     */   private static final byte BRACKET = 2;
/*  146:     */   private static final byte CURLY = 3;
/*  147:     */   
/*  148:     */   public PythonLexer() {}
/*  149:     */   
/*  150:     */   public int yychar()
/*  151:     */   {
/*  152: 729 */     return this.yychar;
/*  153:     */   }
/*  154:     */   
/*  155:     */   public PythonLexer(Reader in)
/*  156:     */   {
/*  157: 745 */     this.zzReader = in;
/*  158:     */   }
/*  159:     */   
/*  160:     */   public PythonLexer(InputStream in)
/*  161:     */   {
/*  162: 755 */     this(new InputStreamReader(in));
/*  163:     */   }
/*  164:     */   
/*  165:     */   private static char[] zzUnpackCMap(String packed)
/*  166:     */   {
/*  167: 765 */     char[] map = new char[65536];
/*  168: 766 */     int i = 0;
/*  169: 767 */     int j = 0;
/*  170:     */     int count;
/*  171: 768 */     for (; i < 174; count > 0)
/*  172:     */     {
/*  173: 769 */       count = packed.charAt(i++);
/*  174: 770 */       char value = packed.charAt(i++);
/*  175: 771 */       map[(j++)] = value;count--;
/*  176:     */     }
/*  177: 773 */     return map;
/*  178:     */   }
/*  179:     */   
/*  180:     */   private boolean zzRefill()
/*  181:     */     throws IOException
/*  182:     */   {
/*  183: 787 */     if (this.zzStartRead > 0)
/*  184:     */     {
/*  185: 788 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/*  186:     */       
/*  187:     */ 
/*  188:     */ 
/*  189:     */ 
/*  190: 793 */       this.zzEndRead -= this.zzStartRead;
/*  191: 794 */       this.zzCurrentPos -= this.zzStartRead;
/*  192: 795 */       this.zzMarkedPos -= this.zzStartRead;
/*  193: 796 */       this.zzStartRead = 0;
/*  194:     */     }
/*  195: 800 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/*  196:     */     {
/*  197: 802 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/*  198: 803 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/*  199: 804 */       this.zzBuffer = newBuffer;
/*  200:     */     }
/*  201: 808 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/*  202: 811 */     if (numRead > 0)
/*  203:     */     {
/*  204: 812 */       this.zzEndRead += numRead;
/*  205: 813 */       return false;
/*  206:     */     }
/*  207: 816 */     if (numRead == 0)
/*  208:     */     {
/*  209: 817 */       int c = this.zzReader.read();
/*  210: 818 */       if (c == -1) {
/*  211: 819 */         return true;
/*  212:     */       }
/*  213: 821 */       this.zzBuffer[(this.zzEndRead++)] = ((char)c);
/*  214: 822 */       return false;
/*  215:     */     }
/*  216: 827 */     return true;
/*  217:     */   }
/*  218:     */   
/*  219:     */   public final void yyclose()
/*  220:     */     throws IOException
/*  221:     */   {
/*  222: 835 */     this.zzAtEOF = true;
/*  223: 836 */     this.zzEndRead = this.zzStartRead;
/*  224: 838 */     if (this.zzReader != null) {
/*  225: 839 */       this.zzReader.close();
/*  226:     */     }
/*  227:     */   }
/*  228:     */   
/*  229:     */   public final void yyreset(Reader reader)
/*  230:     */   {
/*  231: 854 */     this.zzReader = reader;
/*  232: 855 */     this.zzAtBOL = true;
/*  233: 856 */     this.zzAtEOF = false;
/*  234: 857 */     this.zzEOFDone = false;
/*  235: 858 */     this.zzEndRead = (this.zzStartRead = 0);
/*  236: 859 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/*  237: 860 */     this.yyline = (this.yychar = this.yycolumn = 0);
/*  238: 861 */     this.zzLexicalState = 0;
/*  239:     */   }
/*  240:     */   
/*  241:     */   public final int yystate()
/*  242:     */   {
/*  243: 869 */     return this.zzLexicalState;
/*  244:     */   }
/*  245:     */   
/*  246:     */   public final void yybegin(int newState)
/*  247:     */   {
/*  248: 879 */     this.zzLexicalState = newState;
/*  249:     */   }
/*  250:     */   
/*  251:     */   public final String yytext()
/*  252:     */   {
/*  253: 887 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*  254:     */   }
/*  255:     */   
/*  256:     */   public final char yycharat(int pos)
/*  257:     */   {
/*  258: 903 */     return this.zzBuffer[(this.zzStartRead + pos)];
/*  259:     */   }
/*  260:     */   
/*  261:     */   public final int yylength()
/*  262:     */   {
/*  263: 911 */     return this.zzMarkedPos - this.zzStartRead;
/*  264:     */   }
/*  265:     */   
/*  266:     */   private void zzScanError(int errorCode)
/*  267:     */   {
/*  268:     */     String message;
/*  269:     */     try
/*  270:     */     {
/*  271: 932 */       message = ZZ_ERROR_MSG[errorCode];
/*  272:     */     }
/*  273:     */     catch (ArrayIndexOutOfBoundsException e)
/*  274:     */     {
/*  275: 935 */       message = ZZ_ERROR_MSG[0];
/*  276:     */     }
/*  277: 938 */     throw new Error(message);
/*  278:     */   }
/*  279:     */   
/*  280:     */   public void yypushback(int number)
/*  281:     */   {
/*  282: 951 */     if (number > yylength()) {
/*  283: 952 */       zzScanError(2);
/*  284:     */     }
/*  285: 954 */     this.zzMarkedPos -= number;
/*  286:     */   }
/*  287:     */   
/*  288:     */   public Token yylex()
/*  289:     */     throws IOException
/*  290:     */   {
/*  291: 972 */     int zzEndReadL = this.zzEndRead;
/*  292: 973 */     char[] zzBufferL = this.zzBuffer;
/*  293: 974 */     char[] zzCMapL = ZZ_CMAP;
/*  294:     */     
/*  295: 976 */     int[] zzTransL = ZZ_TRANS;
/*  296: 977 */     int[] zzRowMapL = ZZ_ROWMAP;
/*  297: 978 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/*  298:     */     for (;;)
/*  299:     */     {
/*  300: 981 */       int zzMarkedPosL = this.zzMarkedPos;
/*  301:     */       
/*  302: 983 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/*  303:     */       
/*  304: 985 */       int zzAction = -1;
/*  305:     */       
/*  306: 987 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/*  307:     */       
/*  308: 989 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/*  309:     */       int zzInput;
/*  310:     */       for (;;)
/*  311:     */       {
/*  312:     */         int zzInput;
/*  313: 995 */         if (zzCurrentPosL < zzEndReadL)
/*  314:     */         {
/*  315: 996 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  316:     */         }
/*  317:     */         else
/*  318:     */         {
/*  319: 997 */           if (this.zzAtEOF)
/*  320:     */           {
/*  321: 998 */             int zzInput = -1;
/*  322: 999 */             break;
/*  323:     */           }
/*  324:1003 */           this.zzCurrentPos = zzCurrentPosL;
/*  325:1004 */           this.zzMarkedPos = zzMarkedPosL;
/*  326:1005 */           boolean eof = zzRefill();
/*  327:     */           
/*  328:1007 */           zzCurrentPosL = this.zzCurrentPos;
/*  329:1008 */           zzMarkedPosL = this.zzMarkedPos;
/*  330:1009 */           zzBufferL = this.zzBuffer;
/*  331:1010 */           zzEndReadL = this.zzEndRead;
/*  332:1011 */           if (eof)
/*  333:     */           {
/*  334:1012 */             int zzInput = -1;
/*  335:1013 */             break;
/*  336:     */           }
/*  337:1016 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  338:     */         }
/*  339:1019 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/*  340:1020 */         if (zzNext == -1) {
/*  341:     */           break;
/*  342:     */         }
/*  343:1021 */         this.zzState = zzNext;
/*  344:     */         
/*  345:1023 */         int zzAttributes = zzAttrL[this.zzState];
/*  346:1024 */         if ((zzAttributes & 0x1) == 1)
/*  347:     */         {
/*  348:1025 */           zzAction = this.zzState;
/*  349:1026 */           zzMarkedPosL = zzCurrentPosL;
/*  350:1027 */           if ((zzAttributes & 0x8) == 8) {
/*  351:     */             break;
/*  352:     */           }
/*  353:     */         }
/*  354:     */       }
/*  355:1034 */       this.zzMarkedPos = zzMarkedPosL;
/*  356:1036 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/*  357:     */       {
/*  358:     */       case 9: 
/*  359:1038 */         return token(TokenType.OPERATOR, -1);
/*  360:     */       case 26: 
/*  361:     */         break;
/*  362:     */       case 20: 
/*  363:1042 */         return token(TokenType.KEYWORD);
/*  364:     */       case 27: 
/*  365:     */         break;
/*  366:     */       case 19: 
/*  367:1046 */         yybegin(0);
/*  368:     */         
/*  369:1048 */         return token(TokenType.STRING, this.tokenStart, this.tokenLength + 1);
/*  370:     */       case 28: 
/*  371:     */         break;
/*  372:     */       case 4: 
/*  373:1052 */         return token(TokenType.NUMBER);
/*  374:     */       case 29: 
/*  375:     */         break;
/*  376:     */       case 5: 
/*  377:1056 */         return token(TokenType.OPERATOR);
/*  378:     */       case 30: 
/*  379:     */         break;
/*  380:     */       case 10: 
/*  381:1060 */         return token(TokenType.OPERATOR, 3);
/*  382:     */       case 31: 
/*  383:     */         break;
/*  384:     */       case 11: 
/*  385:1064 */         return token(TokenType.OPERATOR, -3);
/*  386:     */       case 32: 
/*  387:     */         break;
/*  388:     */       case 25: 
/*  389:1068 */         yybegin(0);
/*  390:     */         
/*  391:1070 */         return token(TokenType.STRING, this.tokenStart, this.tokenLength + 3);
/*  392:     */       case 33: 
/*  393:     */         break;
/*  394:     */       case 23: 
/*  395:1074 */         yybegin(4);
/*  396:1075 */         this.tokenStart = this.yychar;
/*  397:1076 */         this.tokenLength = 3;
/*  398:     */       case 34: 
/*  399:     */         break;
/*  400:     */       case 15: 
/*  401:1080 */         this.tokenLength += yylength();
/*  402:     */       case 35: 
/*  403:     */         break;
/*  404:     */       case 16: 
/*  405:1084 */         yybegin(0);
/*  406:     */       case 36: 
/*  407:     */         break;
/*  408:     */       case 6: 
/*  409:1088 */         yybegin(2);
/*  410:1089 */         this.tokenStart = this.yychar;
/*  411:1090 */         this.tokenLength = 1;
/*  412:     */       case 37: 
/*  413:     */         break;
/*  414:     */       case 17: 
/*  415:1094 */         yybegin(0);
/*  416:     */         
/*  417:1096 */         return token(TokenType.STRING, this.tokenStart, this.tokenLength + 1);
/*  418:     */       case 38: 
/*  419:     */         break;
/*  420:     */       case 21: 
/*  421:1100 */         return token(TokenType.TYPE);
/*  422:     */       case 39: 
/*  423:     */         break;
/*  424:     */       case 13: 
/*  425:1104 */         return token(TokenType.OPERATOR, -2);
/*  426:     */       case 40: 
/*  427:     */         break;
/*  428:     */       case 8: 
/*  429:1108 */         return token(TokenType.OPERATOR, 1);
/*  430:     */       case 41: 
/*  431:     */         break;
/*  432:     */       case 3: 
/*  433:1112 */         return token(TokenType.IDENTIFIER);
/*  434:     */       case 42: 
/*  435:     */         break;
/*  436:     */       case 22: 
/*  437:1116 */         this.tokenLength += 2;
/*  438:     */       case 43: 
/*  439:     */         break;
/*  440:     */       case 14: 
/*  441:1120 */         return token(TokenType.ERROR);
/*  442:     */       case 44: 
/*  443:     */         break;
/*  444:     */       case 24: 
/*  445:1124 */         yybegin(8);
/*  446:1125 */         this.tokenStart = this.yychar;
/*  447:1126 */         this.tokenLength = 3;
/*  448:     */       case 45: 
/*  449:     */         break;
/*  450:     */       case 18: 
/*  451:1130 */         this.tokenLength += 1;
/*  452:     */       case 46: 
/*  453:     */         break;
/*  454:     */       case 12: 
/*  455:1134 */         return token(TokenType.OPERATOR, 2);
/*  456:     */       case 47: 
/*  457:     */         break;
/*  458:     */       case 7: 
/*  459:1138 */         yybegin(6);
/*  460:1139 */         this.tokenStart = this.yychar;
/*  461:1140 */         this.tokenLength = 1;
/*  462:     */       case 48: 
/*  463:     */         break;
/*  464:     */       case 2: 
/*  465:1144 */         return token(TokenType.COMMENT);
/*  466:     */       case 49: 
/*  467:     */         break;
/*  468:     */       case 1: 
/*  469:     */       case 50: 
/*  470:     */         break;
/*  471:     */       default: 
/*  472:1152 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos))
/*  473:     */         {
/*  474:1153 */           this.zzAtEOF = true;
/*  475:     */           
/*  476:1155 */           return null;
/*  477:     */         }
/*  478:1159 */         zzScanError(1);
/*  479:     */       }
/*  480:     */     }
/*  481:     */   }
/*  482:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.PythonLexer
 * JD-Core Version:    0.7.0.1
 */