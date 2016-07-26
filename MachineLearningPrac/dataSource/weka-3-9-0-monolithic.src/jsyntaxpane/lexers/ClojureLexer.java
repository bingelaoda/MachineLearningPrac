/*    1:     */ package jsyntaxpane.lexers;
/*    2:     */ 
/*    3:     */ import java.io.IOException;
/*    4:     */ import java.io.InputStream;
/*    5:     */ import java.io.InputStreamReader;
/*    6:     */ import java.io.Reader;
/*    7:     */ import jsyntaxpane.Token;
/*    8:     */ import jsyntaxpane.TokenType;
/*    9:     */ 
/*   10:     */ public final class ClojureLexer
/*   11:     */   extends DefaultJFlexLexer
/*   12:     */ {
/*   13:     */   public static final int YYEOF = -1;
/*   14:     */   private static final int ZZ_BUFFERSIZE = 16384;
/*   15:     */   public static final int STRING = 2;
/*   16:     */   public static final int YYINITIAL = 0;
/*   17:     */   public static final int CHARLITERAL = 4;
/*   18:  49 */   private static final int[] ZZ_LEXSTATE = { 0, 0, 1, 1, 2, 2 };
/*   19:     */   private static final String ZZ_CMAP_PACKED = "";
/*   20: 151 */   private static final char[] ZZ_CMAP = zzUnpackCMap("");
/*   21: 156 */   private static final int[] ZZ_ACTION = zzUnpackAction();
/*   22:     */   private static final String ZZ_ACTION_PACKED_0 = "";
/*   23:     */   
/*   24:     */   private static int[] zzUnpackAction()
/*   25:     */   {
/*   26: 196 */     int[] result = new int[814];
/*   27: 197 */     int offset = 0;
/*   28: 198 */     offset = zzUnpackAction("", offset, result);
/*   29: 199 */     return result;
/*   30:     */   }
/*   31:     */   
/*   32:     */   private static int zzUnpackAction(String packed, int offset, int[] result)
/*   33:     */   {
/*   34: 203 */     int i = 0;
/*   35: 204 */     int j = offset;
/*   36: 205 */     int l = packed.length();
/*   37:     */     int count;
/*   38: 206 */     for (; i < l; count > 0)
/*   39:     */     {
/*   40: 207 */       count = packed.charAt(i++);
/*   41: 208 */       int value = packed.charAt(i++);
/*   42: 209 */       result[(j++)] = value;count--;
/*   43:     */     }
/*   44: 211 */     return j;
/*   45:     */   }
/*   46:     */   
/*   47: 218 */   private static final int[] ZZ_ROWMAP = zzUnpackRowMap();
/*   48:     */   private static final String ZZ_ROWMAP_PACKED_0 = "";
/*   49:     */   
/*   50:     */   private static int[] zzUnpackRowMap()
/*   51:     */   {
/*   52: 325 */     int[] result = new int[814];
/*   53: 326 */     int offset = 0;
/*   54: 327 */     offset = zzUnpackRowMap("", offset, result);
/*   55: 328 */     return result;
/*   56:     */   }
/*   57:     */   
/*   58:     */   private static int zzUnpackRowMap(String packed, int offset, int[] result)
/*   59:     */   {
/*   60: 332 */     int i = 0;
/*   61: 333 */     int j = offset;
/*   62: 334 */     int l = packed.length();
/*   63: 335 */     while (i < l)
/*   64:     */     {
/*   65: 336 */       int high = packed.charAt(i++) << '\020';
/*   66: 337 */       result[(j++)] = (high | packed.charAt(i++));
/*   67:     */     }
/*   68: 339 */     return j;
/*   69:     */   }
/*   70:     */   
/*   71: 345 */   private static final int[] ZZ_TRANS = zzUnpackTrans();
/*   72:     */   private static final String ZZ_TRANS_PACKED_0 = "";
/*   73:     */   private static final int ZZ_UNKNOWN_ERROR = 0;
/*   74:     */   private static final int ZZ_NO_MATCH = 1;
/*   75:     */   private static final int ZZ_PUSHBACK_2BIG = 2;
/*   76:     */   
/*   77:     */   private static int[] zzUnpackTrans()
/*   78:     */   {
/*   79:1480 */     int[] result = new int[49833];
/*   80:1481 */     int offset = 0;
/*   81:1482 */     offset = zzUnpackTrans("", offset, result);
/*   82:1483 */     return result;
/*   83:     */   }
/*   84:     */   
/*   85:     */   private static int zzUnpackTrans(String packed, int offset, int[] result)
/*   86:     */   {
/*   87:1487 */     int i = 0;
/*   88:1488 */     int j = offset;
/*   89:1489 */     int l = packed.length();
/*   90:     */     int count;
/*   91:1490 */     for (; i < l; count > 0)
/*   92:     */     {
/*   93:1491 */       count = packed.charAt(i++);
/*   94:1492 */       int value = packed.charAt(i++);
/*   95:1493 */       value--;
/*   96:1494 */       result[(j++)] = value;count--;
/*   97:     */     }
/*   98:1496 */     return j;
/*   99:     */   }
/*  100:     */   
/*  101:1506 */   private static final String[] ZZ_ERROR_MSG = { "Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large" };
/*  102:1515 */   private static final int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
/*  103:     */   private static final String ZZ_ATTRIBUTE_PACKED_0 = "";
/*  104:     */   private Reader zzReader;
/*  105:     */   private int zzState;
/*  106:     */   
/*  107:     */   private static int[] zzUnpackAttribute()
/*  108:     */   {
/*  109:1543 */     int[] result = new int[814];
/*  110:1544 */     int offset = 0;
/*  111:1545 */     offset = zzUnpackAttribute("", offset, result);
/*  112:1546 */     return result;
/*  113:     */   }
/*  114:     */   
/*  115:     */   private static int zzUnpackAttribute(String packed, int offset, int[] result)
/*  116:     */   {
/*  117:1550 */     int i = 0;
/*  118:1551 */     int j = offset;
/*  119:1552 */     int l = packed.length();
/*  120:     */     int count;
/*  121:1553 */     for (; i < l; count > 0)
/*  122:     */     {
/*  123:1554 */       count = packed.charAt(i++);
/*  124:1555 */       int value = packed.charAt(i++);
/*  125:1556 */       result[(j++)] = value;count--;
/*  126:     */     }
/*  127:1558 */     return j;
/*  128:     */   }
/*  129:     */   
/*  130:1568 */   private int zzLexicalState = 0;
/*  131:1572 */   private char[] zzBuffer = new char[16384];
/*  132:     */   private int zzMarkedPos;
/*  133:     */   private int zzCurrentPos;
/*  134:     */   private int zzStartRead;
/*  135:     */   private int zzEndRead;
/*  136:     */   private int yyline;
/*  137:     */   private int yychar;
/*  138:     */   private int yycolumn;
/*  139:1602 */   private boolean zzAtBOL = true;
/*  140:     */   private boolean zzAtEOF;
/*  141:     */   private boolean zzEOFDone;
/*  142:     */   private static final byte PARAN = 1;
/*  143:     */   private static final byte BRACKET = 2;
/*  144:     */   private static final byte CURLY = 3;
/*  145:     */   
/*  146:     */   public ClojureLexer() {}
/*  147:     */   
/*  148:     */   public int yychar()
/*  149:     */   {
/*  150:1621 */     return this.yychar;
/*  151:     */   }
/*  152:     */   
/*  153:     */   public ClojureLexer(Reader in)
/*  154:     */   {
/*  155:1637 */     this.zzReader = in;
/*  156:     */   }
/*  157:     */   
/*  158:     */   public ClojureLexer(InputStream in)
/*  159:     */   {
/*  160:1647 */     this(new InputStreamReader(in));
/*  161:     */   }
/*  162:     */   
/*  163:     */   private static char[] zzUnpackCMap(String packed)
/*  164:     */   {
/*  165:1657 */     char[] map = new char[65536];
/*  166:1658 */     int i = 0;
/*  167:1659 */     int j = 0;
/*  168:     */     int count;
/*  169:1660 */     for (; i < 1782; count > 0)
/*  170:     */     {
/*  171:1661 */       count = packed.charAt(i++);
/*  172:1662 */       char value = packed.charAt(i++);
/*  173:1663 */       map[(j++)] = value;count--;
/*  174:     */     }
/*  175:1665 */     return map;
/*  176:     */   }
/*  177:     */   
/*  178:     */   private boolean zzRefill()
/*  179:     */     throws IOException
/*  180:     */   {
/*  181:1679 */     if (this.zzStartRead > 0)
/*  182:     */     {
/*  183:1680 */       System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
/*  184:     */       
/*  185:     */ 
/*  186:     */ 
/*  187:     */ 
/*  188:1685 */       this.zzEndRead -= this.zzStartRead;
/*  189:1686 */       this.zzCurrentPos -= this.zzStartRead;
/*  190:1687 */       this.zzMarkedPos -= this.zzStartRead;
/*  191:1688 */       this.zzStartRead = 0;
/*  192:     */     }
/*  193:1692 */     if (this.zzCurrentPos >= this.zzBuffer.length)
/*  194:     */     {
/*  195:1694 */       char[] newBuffer = new char[this.zzCurrentPos * 2];
/*  196:1695 */       System.arraycopy(this.zzBuffer, 0, newBuffer, 0, this.zzBuffer.length);
/*  197:1696 */       this.zzBuffer = newBuffer;
/*  198:     */     }
/*  199:1700 */     int numRead = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
/*  200:1703 */     if (numRead > 0)
/*  201:     */     {
/*  202:1704 */       this.zzEndRead += numRead;
/*  203:1705 */       return false;
/*  204:     */     }
/*  205:1708 */     if (numRead == 0)
/*  206:     */     {
/*  207:1709 */       int c = this.zzReader.read();
/*  208:1710 */       if (c == -1) {
/*  209:1711 */         return true;
/*  210:     */       }
/*  211:1713 */       this.zzBuffer[(this.zzEndRead++)] = ((char)c);
/*  212:1714 */       return false;
/*  213:     */     }
/*  214:1719 */     return true;
/*  215:     */   }
/*  216:     */   
/*  217:     */   public final void yyclose()
/*  218:     */     throws IOException
/*  219:     */   {
/*  220:1727 */     this.zzAtEOF = true;
/*  221:1728 */     this.zzEndRead = this.zzStartRead;
/*  222:1730 */     if (this.zzReader != null) {
/*  223:1731 */       this.zzReader.close();
/*  224:     */     }
/*  225:     */   }
/*  226:     */   
/*  227:     */   public final void yyreset(Reader reader)
/*  228:     */   {
/*  229:1746 */     this.zzReader = reader;
/*  230:1747 */     this.zzAtBOL = true;
/*  231:1748 */     this.zzAtEOF = false;
/*  232:1749 */     this.zzEOFDone = false;
/*  233:1750 */     this.zzEndRead = (this.zzStartRead = 0);
/*  234:1751 */     this.zzCurrentPos = (this.zzMarkedPos = 0);
/*  235:1752 */     this.yyline = (this.yychar = this.yycolumn = 0);
/*  236:1753 */     this.zzLexicalState = 0;
/*  237:     */   }
/*  238:     */   
/*  239:     */   public final int yystate()
/*  240:     */   {
/*  241:1761 */     return this.zzLexicalState;
/*  242:     */   }
/*  243:     */   
/*  244:     */   public final void yybegin(int newState)
/*  245:     */   {
/*  246:1771 */     this.zzLexicalState = newState;
/*  247:     */   }
/*  248:     */   
/*  249:     */   public final String yytext()
/*  250:     */   {
/*  251:1779 */     return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
/*  252:     */   }
/*  253:     */   
/*  254:     */   public final char yycharat(int pos)
/*  255:     */   {
/*  256:1795 */     return this.zzBuffer[(this.zzStartRead + pos)];
/*  257:     */   }
/*  258:     */   
/*  259:     */   public final int yylength()
/*  260:     */   {
/*  261:1803 */     return this.zzMarkedPos - this.zzStartRead;
/*  262:     */   }
/*  263:     */   
/*  264:     */   private void zzScanError(int errorCode)
/*  265:     */   {
/*  266:     */     String message;
/*  267:     */     try
/*  268:     */     {
/*  269:1824 */       message = ZZ_ERROR_MSG[errorCode];
/*  270:     */     }
/*  271:     */     catch (ArrayIndexOutOfBoundsException e)
/*  272:     */     {
/*  273:1827 */       message = ZZ_ERROR_MSG[0];
/*  274:     */     }
/*  275:1830 */     throw new Error(message);
/*  276:     */   }
/*  277:     */   
/*  278:     */   public void yypushback(int number)
/*  279:     */   {
/*  280:1843 */     if (number > yylength()) {
/*  281:1844 */       zzScanError(2);
/*  282:     */     }
/*  283:1846 */     this.zzMarkedPos -= number;
/*  284:     */   }
/*  285:     */   
/*  286:     */   public Token yylex()
/*  287:     */     throws IOException
/*  288:     */   {
/*  289:1864 */     int zzEndReadL = this.zzEndRead;
/*  290:1865 */     char[] zzBufferL = this.zzBuffer;
/*  291:1866 */     char[] zzCMapL = ZZ_CMAP;
/*  292:     */     
/*  293:1868 */     int[] zzTransL = ZZ_TRANS;
/*  294:1869 */     int[] zzRowMapL = ZZ_ROWMAP;
/*  295:1870 */     int[] zzAttrL = ZZ_ATTRIBUTE;
/*  296:     */     for (;;)
/*  297:     */     {
/*  298:1873 */       int zzMarkedPosL = this.zzMarkedPos;
/*  299:     */       
/*  300:1875 */       this.yychar += zzMarkedPosL - this.zzStartRead;
/*  301:     */       
/*  302:1877 */       int zzAction = -1;
/*  303:     */       
/*  304:1879 */       int zzCurrentPosL = this.zzCurrentPos = this.zzStartRead = zzMarkedPosL;
/*  305:     */       
/*  306:1881 */       this.zzState = ZZ_LEXSTATE[this.zzLexicalState];
/*  307:     */       int zzInput;
/*  308:     */       for (;;)
/*  309:     */       {
/*  310:     */         int zzInput;
/*  311:1887 */         if (zzCurrentPosL < zzEndReadL)
/*  312:     */         {
/*  313:1888 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  314:     */         }
/*  315:     */         else
/*  316:     */         {
/*  317:1889 */           if (this.zzAtEOF)
/*  318:     */           {
/*  319:1890 */             int zzInput = -1;
/*  320:1891 */             break;
/*  321:     */           }
/*  322:1895 */           this.zzCurrentPos = zzCurrentPosL;
/*  323:1896 */           this.zzMarkedPos = zzMarkedPosL;
/*  324:1897 */           boolean eof = zzRefill();
/*  325:     */           
/*  326:1899 */           zzCurrentPosL = this.zzCurrentPos;
/*  327:1900 */           zzMarkedPosL = this.zzMarkedPos;
/*  328:1901 */           zzBufferL = this.zzBuffer;
/*  329:1902 */           zzEndReadL = this.zzEndRead;
/*  330:1903 */           if (eof)
/*  331:     */           {
/*  332:1904 */             int zzInput = -1;
/*  333:1905 */             break;
/*  334:     */           }
/*  335:1908 */           zzInput = zzBufferL[(zzCurrentPosL++)];
/*  336:     */         }
/*  337:1911 */         int zzNext = zzTransL[(zzRowMapL[this.zzState] + zzCMapL[zzInput])];
/*  338:1912 */         if (zzNext == -1) {
/*  339:     */           break;
/*  340:     */         }
/*  341:1913 */         this.zzState = zzNext;
/*  342:     */         
/*  343:1915 */         int zzAttributes = zzAttrL[this.zzState];
/*  344:1916 */         if ((zzAttributes & 0x1) == 1)
/*  345:     */         {
/*  346:1917 */           zzAction = this.zzState;
/*  347:1918 */           zzMarkedPosL = zzCurrentPosL;
/*  348:1919 */           if ((zzAttributes & 0x8) == 8) {
/*  349:     */             break;
/*  350:     */           }
/*  351:     */         }
/*  352:     */       }
/*  353:1926 */       this.zzMarkedPos = zzMarkedPosL;
/*  354:1928 */       switch (zzAction < 0 ? zzAction : ZZ_ACTION[zzAction])
/*  355:     */       {
/*  356:     */       case 9: 
/*  357:1930 */         return token(TokenType.OPERATOR, -1);
/*  358:     */       case 19: 
/*  359:     */         break;
/*  360:     */       case 17: 
/*  361:1934 */         return token(TokenType.KEYWORD);
/*  362:     */       case 20: 
/*  363:     */         break;
/*  364:     */       case 4: 
/*  365:1938 */         return token(TokenType.NUMBER);
/*  366:     */       case 21: 
/*  367:     */         break;
/*  368:     */       case 10: 
/*  369:1942 */         return token(TokenType.OPERATOR, 3);
/*  370:     */       case 22: 
/*  371:     */         break;
/*  372:     */       case 11: 
/*  373:1946 */         return token(TokenType.OPERATOR, -3);
/*  374:     */       case 23: 
/*  375:     */         break;
/*  376:     */       case 14: 
/*  377:1950 */         this.tokenLength += yylength();
/*  378:     */       case 24: 
/*  379:     */         break;
/*  380:     */       case 15: 
/*  381:1954 */         yybegin(0);
/*  382:     */       case 25: 
/*  383:     */         break;
/*  384:     */       case 7: 
/*  385:1958 */         yybegin(4);
/*  386:1959 */         this.tokenStart = this.yychar;
/*  387:1960 */         this.tokenLength = 1;
/*  388:     */       case 26: 
/*  389:     */         break;
/*  390:     */       case 16: 
/*  391:1964 */         yybegin(0);
/*  392:     */         
/*  393:1966 */         return token(TokenType.STRING, this.tokenStart, this.tokenLength + 1);
/*  394:     */       case 27: 
/*  395:     */         break;
/*  396:     */       case 13: 
/*  397:1970 */         return token(TokenType.OPERATOR, -2);
/*  398:     */       case 28: 
/*  399:     */         break;
/*  400:     */       case 8: 
/*  401:1974 */         return token(TokenType.OPERATOR, 1);
/*  402:     */       case 29: 
/*  403:     */         break;
/*  404:     */       case 3: 
/*  405:1978 */         return token(TokenType.IDENTIFIER);
/*  406:     */       case 30: 
/*  407:     */         break;
/*  408:     */       case 5: 
/*  409:1982 */         return token(TokenType.KEYWORD2);
/*  410:     */       case 31: 
/*  411:     */         break;
/*  412:     */       case 18: 
/*  413:1986 */         this.tokenLength += 2;
/*  414:     */       case 32: 
/*  415:     */         break;
/*  416:     */       case 12: 
/*  417:1990 */         return token(TokenType.OPERATOR, 2);
/*  418:     */       case 33: 
/*  419:     */         break;
/*  420:     */       case 2: 
/*  421:1994 */         return token(TokenType.COMMENT);
/*  422:     */       case 34: 
/*  423:     */         break;
/*  424:     */       case 6: 
/*  425:1998 */         yybegin(2);
/*  426:1999 */         this.tokenStart = this.yychar;
/*  427:2000 */         this.tokenLength = 1;
/*  428:     */       case 35: 
/*  429:     */         break;
/*  430:     */       case 1: 
/*  431:     */       case 36: 
/*  432:     */         break;
/*  433:     */       default: 
/*  434:2008 */         if ((zzInput == -1) && (this.zzStartRead == this.zzCurrentPos))
/*  435:     */         {
/*  436:2009 */           this.zzAtEOF = true;
/*  437:     */           
/*  438:2011 */           return null;
/*  439:     */         }
/*  440:2015 */         zzScanError(1);
/*  441:     */       }
/*  442:     */     }
/*  443:     */   }
/*  444:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.lexers.ClojureLexer
 * JD-Core Version:    0.7.0.1
 */