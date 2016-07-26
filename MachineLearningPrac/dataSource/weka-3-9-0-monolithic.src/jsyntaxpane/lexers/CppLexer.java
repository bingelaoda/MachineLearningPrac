/*    1:     */ package jsyntaxpane.lexers;
/*    2:     */ 
/*    3:     */ import java.io.IOException;
/*    4:     */ import java.io.InputStream;
/*    5:     */ import java.io.InputStreamReader;
/*    6:     */ import java.io.Reader;
/*    7:     */ import jsyntaxpane.Token;
/*    8:     */ import jsyntaxpane.TokenType;
/*    9:     */ 
/*   10:     */ public final class CppLexer
/*   11:     */   extends DefaultJFlexLexer
/*   12:     */ {
/*   13:     */   public static final int YYEOF = -1;
/*   14:     */   private static final int ZZ_BUFFERSIZE = 16384;
/*   15:     */   public static final int YYINITIAL = 0;
/*   16:  54 */   private static final int[] ZZ_LEXSTATE = { 0, 0 };
/*   17:     */   private static final String ZZ_CMAP_PACKED = "";
/*   18: 155 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*   19: 160 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*   20:     */   private static final String ZZ_ACTION_PACKED_0 = "";
/*   21:     */   
/*   22:     */   private static int[] zzUnpackAction()
/*   23:     */   {
/*   24: 173 */     int[] result = new int[290];
/*   25: 174 */     int offset = 0;
/*   26: 175 */     offset = zzUnpackAction("", offset, result);
/*   27: 176 */     return result;
/*   28:     */   }
/*   29:     */   
/*   30:     */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*   31:     */   {
/*   32: 180 */     int i = 0;
/*   33: 181 */     int j = offset;
/*   34: 182 */     int l = packed.length();
/*   35:     */     int count;
/*   36: 183 */     for (; i < l; count > 0)
/*   37:     */     {
/*   38: 184 */       count = packed.charAt(i++);
/*   39: 185 */       int value = packed.charAt(i++);
/*   40: 186 */       result[(j++)] = value;count--;
/*   41:     */     }
/*   42: 188 */     return j;
/*   43:     */   }
/*   44:     */   
/*   45: 195 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*   46:     */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*   47:     */   
/*   48:     */   private static int[] zzUnpackRowMap()
/*   49:     */   {
/*   50: 237 */     int[] result = new int[290];
/*   51: 238 */     int offset = 0;
/*   52: 239 */     offset = zzUnpackRowMap("", offset, result);
/*   53: 240 */     return result;
/*   54:     */   }
/*   55:     */   
/*   56:     */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*   57:     */   {
/*   58: 244 */     int i = 0;
/*   59: 245 */     int j = offset;
/*   60: 246 */     int l = packed.length();
/*   61: 247 */     while (i < l)
/*   62:     */     {
/*   63: 248 */       int high = packed.charAt(i++) << '\020';
/*   64: 249 */       result[(j++)] = (high | packed.charAt(i++));
/*   65:     */     }
/*   66: 251 */     return j;
/*   67:     */   }
/*   68:     */   
/*   69: 257 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*   70:     */   private static final String ZZ_TRANS_PACKED_0 = "";
/*   71:     */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*   72:     */   private static final int ZZ_NO_MATCH = 1;
/*   73:     */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*   74:     */   
/*   75:     */   private static int[] zzUnpackTrans()
/*   76:     */   {
/*   77: 585 */     int[] result = new int[15718];
/*   78: 586 */     int offset = 0;
/*   79: 587 */     offset = zzUnpackTrans("", offset, result);
/*   80: 588 */     return result;
/*   81:     */   }
/*   82:     */   
/*   83:     */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*   84:     */   {
/*   85: 592 */     int i = 0;
/*   86: 593 */     int j = offset;
/*   87: 594 */     int l = packed.length();
/*   88:     */     int count;
/*   89: 595 */     for (; i < l; count > 0)
/*   90:     */     {
/*   91: 596 */       count = packed.charAt(i++);
/*   92: 597 */       int value = packed.charAt(i++);
/*   93: 598 */       value--;
/*   94: 599 */       result[(j++)] = value;count--;
/*   95:     */     }
/*   96: 601 */     return j;
/*   97:     */   }
/*   98:     */   
/*   99: 611 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/*  100: 620 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/*  101:     */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/*  102:     */   private Reader zzReader;
/*  103:     */   private int zzState;
/*  104:     */   
/*  105:     */   private static int[] zzUnpackAttribute()
/*  106:     */   {
/*  107: 630 */     int[] result = new int[290];
/*  108: 631 */     int offset = 0;
/*  109: 632 */     offset = zzUnpackAttribute("", offset, result);
/*  110: 633 */     return result;
/*  111:     */   }
/*  112:     */   
/*  113:     */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/*  114:     */   {
/*  115: 637 */     int i = 0;
/*  116: 638 */     int j = offset;
/*  117: 639 */     int l = packed.length();
/*  118:     */     int count;
/*  119: 640 */     for (; i < l; count > 0)
/*  120:     */     {
/*  121: 641 */       count = packed.charAt(i++);
/*  122: 642 */       int value = packed.charAt(i++);
/*  123: 643 */       result[(j++)] = value;count--;
/*  124:     */     }
/*  125: 645 */     return j;
/*  126:     */   }
/*  127:     */   
/*  128: 655 */   private int zzLexicalState = 0;
/*  129: 659 */   private char[] zzBuffer = new char[16384];
/*  130:     */   private int zzMarkedPos;
/*  131:     */   private int zzCurrentPos;
/*  132:     */   private int zzStartRead;
/*  133:     */   private int zzEndRead;
/*  134:     */   private int yyline;
/*  135:     */   private int yychar;
/*  136:     */   private int yycolumn;
/*  137: 689 */   private boolean zzAtBOL = true;
/*  138:     */   private boolean zzAtEOF;
/*  139:     */   private boolean zzEOFDone;
/*  140:     */   private static final byte PARAN = 1;
/*  141:     */   private static final byte BRACKET = 2;
/*  142:     */   private static final byte CURLY = 3;
/*  143:     */   
/*  144:     */   public CppLexer() {}
/*  145:     */   
/*  146:     */   public int yychar()
/*  147:     */   {
/*  148: 705 */     return this.yychar;
/*  149:     */   }
/*  150:     */   
/*  151:     */   public CppLexer(Reader in)
/*  152:     */   {
/*  153: 721 */     this.zzReader = in;
/*  154:     */   }
/*  155:     */   
/*  156:     */   public CppLexer(InputStream in)
/*  157:     */   {
/*  158: 731 */     this(new InputStreamReader(in));
/*  159:     */   }
/*  160:     */   
/*  161:     */   private static char[] zzUnpackCMap(String packed)
/*  162:     */   {
/*  163: 741 */     char[] map = new char[65536];
/*  164: 742 */     int i = 0;
/*  165: 743 */     int j = 0;
/*  166:     */     int count;
/*  167: 744 */     for (; i < 1778; count > 0)
/*  168:     */     {
/*  169: 745 */       count = packed.charAt(i++);
/*  170: 746 */       char value = packed.charAt(i++);
/*  171: 747 */       map[(j++)] = value;count--;
/*  172:     */     }
/*  173: 749 */     return map;
/*  174:     */   }
/*  175:     */   
/*  176:     */   private boolean zzRefill()
/*  177:     */     throws IOException
/*  178:     */   {
/*  179: 763 */     if (this.zzStartRead > 0)
/*  180:     */     {
/*  181: 764 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/*  182:     */       
/*  183:     */ 
/*  184:     */ 
/*  185:     */ 
/*  186: 769 */       this.zzEndRead -= this.zzStartRead;
/*  187: 770 */       this.zzCurrentPos -= this.zzStartRead;
/*  188: 771 */       this.zzMarkedPos -= this.zzStartRead;
/*  189: 772 */       this.zzStartRead = 0;
/*  190:     */     }
/*  191: 776 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/*  192:     */     {
/*  193: 778 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/*  194: 779 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/*  195: 780 */       this.zzBuffer = newBuffer;
/*  196:     */     }
/*  197: 784 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/*  198: 787 */     if (numRead > 0)
/*  199:     */     {
/*  200: 788 */       this.zzEndRead += numRead;
/*  201: 789 */       return false;
/*  202:     */     }
/*  203: 792 */     if (numRead == 0)
/*  204:     */     {
/*  205: 793 */       int c = this.zzReader.read();
/*  206: 794 */       if (c == -1) {
/*  207: 795 */         return true;
/*  208:     */       }
/*  209: 797 */       this.zzBuffer[(this.zzEndRead++)] = ((char)c);
/*  210: 798 */       return false;
/*  211:     */     }
/*  212: 803 */     return true;
/*  213:     */   }
/*  214:     */   
/*  215:     */   public final void yyclose()
/*  216:     */     throws IOException
/*  217:     */   {
/*  218: 811 */     this.zzAtEOF = true;
/*  219: 812 */     this.zzEndRead = this.zzStartRead;
/*  220: 814 */     if (this.zzReader != null) {
/*  221: 815 */       this.zzReader.close();
/*  222:     */     }
/*  223:     */   }
/*  224:     */   
/*  225:     */   public final void yyreset(Reader reader)
/*  226:     */   {
/*  227: 830 */     this.zzReader = reader;
/*  228: 831 */     this.zzAtBOL = true;
/*  229: 832 */     this.zzAtEOF = false;
/*  230: 833 */     this.zzEOFDone = false;
/*  231: 834 */     this.zzEndRead = (this.zzStartRead = 0);
/*  232: 835 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/*  233: 836 */     this.yyline = (this.yychar = this.yycolumn = 0);
/*  234: 837 */     this.zzLexicalState = 0;
/*  235:     */   }
/*  236:     */   
/*  237:     */   public final int yystate()
/*  238:     */   {
/*  239: 845 */     return this.zzLexicalState;
/*  240:     */   }
/*  241:     */   
/*  242:     */   public final void yybegin(int newState)
/*  243:     */   {
/*  244: 855 */     this.zzLexicalState = newState;
/*  245:     */   }
/*  246:     */   
/*  247:     */   public final String yytext()
/*  248:     */   {
/*  249: 863 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*  250:     */   }
/*  251:     */   
/*  252:     */   public final char yycharat(int pos)
/*  253:     */   {
/*  254: 879 */     return this.zzBuffer[(this.zzStartRead + pos)];
/*  255:     */   }
/*  256:     */   
/*  257:     */   public final int yylength()
/*  258:     */   {
/*  259: 887 */     return this.zzMarkedPos - this.zzStartRead;
/*  260:     */   }
/*  261:     */   
/*  262:     */   private void zzScanError(int errorCode)
/*  263:     */   {
/*  264:     */     String message;
/*  265:     */     try
/*  266:     */     {
/*  267: 908 */       message = ZZ_ERROR_MSG[errorCode];
/*  268:     */     }
/*  269:     */     catch (ArrayIndexOutOfBoundsException e)
/*  270:     */     {
/*  271: 911 */       message = ZZ_ERROR_MSG[0];
/*  272:     */     }
/*  273: 914 */     throw new Error(message);
/*  274:     */   }
/*  275:     */   
/*  276:     */   public void yypushback(int number)
/*  277:     */   {
/*  278: 927 */     if (number > yylength()) {
/*  279: 928 */       zzScanError(2);
/*  280:     */     }
/*  281: 930 */     this.zzMarkedPos -= number;
/*  282:     */   }
/*  283:     */   
/*  284:     */   public Token yylex()
/*  285:     */     throws IOException
/*  286:     */   {
/*  287: 948 */     int zzEndReadL = this.zzEndRead;
/*  288: 949 */     char[] zzBufferL = this.zzBuffer;
/*  289: 950 */     char[] zzCMapL = ZZ_CMAP;
/*  290:     */     
/*  291: 952 */     int[] zzTransL = ZZ_TRANS;
/*  292: 953 */     int[] zzRowMapL = ZZ_ROWMAP;
/*  293: 954 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/*  294:     */     for (;;)
/*  295:     */     {
/*  296: 957 */       int zzMarkedPosL = this.zzMarkedPos;
/*  297:     */       
/*  298: 959 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/*  299:     */       
/*  300: 961 */       int zzAction = -1;
/*  301:     */       
/*  302: 963 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/*  303:     */       
/*  304: 965 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/*  305:     */       int zzInput;
/*  306:     */       for (;;)
/*  307:     */       {
/*  308:     */         int zzInput;
/*  309: 971 */         if (zzCurrentPosL < zzEndReadL)
/*  310:     */         {
/*  311: 972 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  312:     */         }
/*  313:     */         else
/*  314:     */         {
/*  315: 973 */           if (this.zzAtEOF)
/*  316:     */           {
/*  317: 974 */             int zzInput = -1;
/*  318: 975 */             break;
/*  319:     */           }
/*  320: 979 */           this.zzCurrentPos = zzCurrentPosL;
/*  321: 980 */           this.zzMarkedPos = zzMarkedPosL;
/*  322: 981 */           boolean eof = zzRefill();
/*  323:     */           
/*  324: 983 */           zzCurrentPosL = this.zzCurrentPos;
/*  325: 984 */           zzMarkedPosL = this.zzMarkedPos;
/*  326: 985 */           zzBufferL = this.zzBuffer;
/*  327: 986 */           zzEndReadL = this.zzEndRead;
/*  328: 987 */           if (eof)
/*  329:     */           {
/*  330: 988 */             int zzInput = -1;
/*  331: 989 */             break;
/*  332:     */           }
/*  333: 992 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  334:     */         }
/*  335: 995 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/*  336: 996 */         if (zzNext == -1) {
/*  337:     */           break;
/*  338:     */         }
/*  339: 997 */         this.zzState = zzNext;
/*  340:     */         
/*  341: 999 */         int zzAttributes = zzAttrL[this.zzState];
/*  342:1000 */         if ((zzAttributes & 0x1) == 1)
/*  343:     */         {
/*  344:1001 */           zzAction = this.zzState;
/*  345:1002 */           zzMarkedPosL = zzCurrentPosL;
/*  346:1003 */           if ((zzAttributes & 0x8) == 8) {
/*  347:     */             break;
/*  348:     */           }
/*  349:     */         }
/*  350:     */       }
/*  351:1010 */       this.zzMarkedPos = zzMarkedPosL;
/*  352:1012 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/*  353:     */       {
/*  354:     */       case 16: 
/*  355:1014 */         return token(TokenType.TYPE2);
/*  356:     */       case 17: 
/*  357:     */         break;
/*  358:     */       case 13: 
/*  359:1018 */         return token(TokenType.KEYWORD);
/*  360:     */       case 18: 
/*  361:     */         break;
/*  362:     */       case 8: 
/*  363:1022 */         return token(TokenType.OPERATOR, 3);
/*  364:     */       case 19: 
/*  365:     */         break;
/*  366:     */       case 10: 
/*  367:1026 */         return token(TokenType.OPERATOR, 2);
/*  368:     */       case 20: 
/*  369:     */         break;
/*  370:     */       case 2: 
/*  371:1030 */         return token(TokenType.OPERATOR);
/*  372:     */       case 21: 
/*  373:     */         break;
/*  374:     */       case 7: 
/*  375:1034 */         return token(TokenType.OPERATOR, -1);
/*  376:     */       case 22: 
/*  377:     */         break;
/*  378:     */       case 15: 
/*  379:1038 */         return token(TokenType.KEYWORD2);
/*  380:     */       case 23: 
/*  381:     */         break;
/*  382:     */       case 4: 
/*  383:1042 */         return token(TokenType.IDENTIFIER);
/*  384:     */       case 24: 
/*  385:     */         break;
/*  386:     */       case 6: 
/*  387:1046 */         return token(TokenType.OPERATOR, 1);
/*  388:     */       case 25: 
/*  389:     */         break;
/*  390:     */       case 14: 
/*  391:1050 */         return token(TokenType.STRING);
/*  392:     */       case 26: 
/*  393:     */         break;
/*  394:     */       case 12: 
/*  395:1054 */         return token(TokenType.COMMENT);
/*  396:     */       case 27: 
/*  397:     */         break;
/*  398:     */       case 11: 
/*  399:1058 */         return token(TokenType.OPERATOR, -2);
/*  400:     */       case 28: 
/*  401:     */         break;
/*  402:     */       case 9: 
/*  403:1062 */         return token(TokenType.OPERATOR, -3);
/*  404:     */       case 29: 
/*  405:     */         break;
/*  406:     */       case 5: 
/*  407:1066 */         return token(TokenType.NUMBER);
/*  408:     */       case 30: 
/*  409:     */         break;
/*  410:     */       case 3: 
/*  411:1070 */         return token(TokenType.TYPE);
/*  412:     */       case 31: 
/*  413:     */         break;
/*  414:     */       case 1: 
/*  415:     */       case 32: 
/*  416:     */         break;
/*  417:     */       default: 
/*  418:1078 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos))
/*  419:     */         {
/*  420:1079 */           this.zzAtEOF = true;
/*  421:     */           
/*  422:1081 */           return null;
/*  423:     */         }
/*  424:1085 */         zzScanError(1);
/*  425:     */       }
/*  426:     */     }
/*  427:     */   }
/*  428:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.CppLexer
 * JD-Core Version:    0.7.0.1
 */