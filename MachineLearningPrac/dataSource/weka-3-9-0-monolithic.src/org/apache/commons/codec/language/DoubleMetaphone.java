/*    1:     */ package org.apache.commons.codec.language;
/*    2:     */ 
/*    3:     */ import org.apache.commons.codec.EncoderException;
/*    4:     */ import org.apache.commons.codec.StringEncoder;
/*    5:     */ 
/*    6:     */ public class DoubleMetaphone
/*    7:     */   implements StringEncoder
/*    8:     */ {
/*    9:     */   private static final String VOWELS = "AEIOUY";
/*   10:  46 */   private static final String[] SILENT_START = { "GN", "KN", "PN", "WR", "PS" };
/*   11:  48 */   private static final String[] L_R_N_M_B_H_F_V_W_SPACE = { "L", "R", "N", "M", "B", "H", "F", "V", "W", " " };
/*   12:  50 */   private static final String[] ES_EP_EB_EL_EY_IB_IL_IN_IE_EI_ER = { "ES", "EP", "EB", "EL", "EY", "IB", "IL", "IN", "IE", "EI", "ER" };
/*   13:  52 */   private static final String[] L_T_K_S_N_M_B_Z = { "L", "T", "K", "S", "N", "M", "B", "Z" };
/*   14:  58 */   protected int maxCodeLen = 4;
/*   15:     */   
/*   16:     */   public String doubleMetaphone(String value)
/*   17:     */   {
/*   18:  74 */     return doubleMetaphone(value, false);
/*   19:     */   }
/*   20:     */   
/*   21:     */   public String doubleMetaphone(String value, boolean alternate)
/*   22:     */   {
/*   23:  86 */     value = cleanInput(value);
/*   24:  87 */     if (value == null) {
/*   25:  88 */       return null;
/*   26:     */     }
/*   27:  91 */     boolean slavoGermanic = isSlavoGermanic(value);
/*   28:  92 */     int index = isSilentStart(value) ? 1 : 0;
/*   29:     */     
/*   30:  94 */     DoubleMetaphoneResult result = new DoubleMetaphoneResult(getMaxCodeLen());
/*   31:  96 */     while ((!result.isComplete()) && (index <= value.length() - 1)) {
/*   32:  97 */       switch (value.charAt(index))
/*   33:     */       {
/*   34:     */       case 'A': 
/*   35:     */       case 'E': 
/*   36:     */       case 'I': 
/*   37:     */       case 'O': 
/*   38:     */       case 'U': 
/*   39:     */       case 'Y': 
/*   40: 104 */         index = handleAEIOUY(value, result, index);
/*   41: 105 */         break;
/*   42:     */       case 'B': 
/*   43: 107 */         result.append('P');
/*   44: 108 */         index = charAt(value, index + 1) == 'B' ? index + 2 : index + 1;
/*   45: 109 */         break;
/*   46:     */       case 'Ç': 
/*   47: 112 */         result.append('S');
/*   48: 113 */         index++;
/*   49: 114 */         break;
/*   50:     */       case 'C': 
/*   51: 116 */         index = handleC(value, result, index);
/*   52: 117 */         break;
/*   53:     */       case 'D': 
/*   54: 119 */         index = handleD(value, result, index);
/*   55: 120 */         break;
/*   56:     */       case 'F': 
/*   57: 122 */         result.append('F');
/*   58: 123 */         index = charAt(value, index + 1) == 'F' ? index + 2 : index + 1;
/*   59: 124 */         break;
/*   60:     */       case 'G': 
/*   61: 126 */         index = handleG(value, result, index, slavoGermanic);
/*   62: 127 */         break;
/*   63:     */       case 'H': 
/*   64: 129 */         index = handleH(value, result, index);
/*   65: 130 */         break;
/*   66:     */       case 'J': 
/*   67: 132 */         index = handleJ(value, result, index, slavoGermanic);
/*   68: 133 */         break;
/*   69:     */       case 'K': 
/*   70: 135 */         result.append('K');
/*   71: 136 */         index = charAt(value, index + 1) == 'K' ? index + 2 : index + 1;
/*   72: 137 */         break;
/*   73:     */       case 'L': 
/*   74: 139 */         index = handleL(value, result, index);
/*   75: 140 */         break;
/*   76:     */       case 'M': 
/*   77: 142 */         result.append('M');
/*   78: 143 */         index = conditionM0(value, index) ? index + 2 : index + 1;
/*   79: 144 */         break;
/*   80:     */       case 'N': 
/*   81: 146 */         result.append('N');
/*   82: 147 */         index = charAt(value, index + 1) == 'N' ? index + 2 : index + 1;
/*   83: 148 */         break;
/*   84:     */       case 'Ñ': 
/*   85: 151 */         result.append('N');
/*   86: 152 */         index++;
/*   87: 153 */         break;
/*   88:     */       case 'P': 
/*   89: 155 */         index = handleP(value, result, index);
/*   90: 156 */         break;
/*   91:     */       case 'Q': 
/*   92: 158 */         result.append('K');
/*   93: 159 */         index = charAt(value, index + 1) == 'Q' ? index + 2 : index + 1;
/*   94: 160 */         break;
/*   95:     */       case 'R': 
/*   96: 162 */         index = handleR(value, result, index, slavoGermanic);
/*   97: 163 */         break;
/*   98:     */       case 'S': 
/*   99: 165 */         index = handleS(value, result, index, slavoGermanic);
/*  100: 166 */         break;
/*  101:     */       case 'T': 
/*  102: 168 */         index = handleT(value, result, index);
/*  103: 169 */         break;
/*  104:     */       case 'V': 
/*  105: 171 */         result.append('F');
/*  106: 172 */         index = charAt(value, index + 1) == 'V' ? index + 2 : index + 1;
/*  107: 173 */         break;
/*  108:     */       case 'W': 
/*  109: 175 */         index = handleW(value, result, index);
/*  110: 176 */         break;
/*  111:     */       case 'X': 
/*  112: 178 */         index = handleX(value, result, index);
/*  113: 179 */         break;
/*  114:     */       case 'Z': 
/*  115: 181 */         index = handleZ(value, result, index, slavoGermanic);
/*  116: 182 */         break;
/*  117:     */       default: 
/*  118: 184 */         index++;
/*  119:     */       }
/*  120:     */     }
/*  121: 189 */     return alternate ? result.getAlternate() : result.getPrimary();
/*  122:     */   }
/*  123:     */   
/*  124:     */   public Object encode(Object obj)
/*  125:     */     throws EncoderException
/*  126:     */   {
/*  127: 201 */     if (!(obj instanceof String)) {
/*  128: 202 */       throw new EncoderException("DoubleMetaphone encode parameter is not of type String");
/*  129:     */     }
/*  130: 204 */     return doubleMetaphone((String)obj);
/*  131:     */   }
/*  132:     */   
/*  133:     */   public String encode(String value)
/*  134:     */   {
/*  135: 214 */     return doubleMetaphone(value);
/*  136:     */   }
/*  137:     */   
/*  138:     */   public boolean isDoubleMetaphoneEqual(String value1, String value2)
/*  139:     */   {
/*  140: 228 */     return isDoubleMetaphoneEqual(value1, value2, false);
/*  141:     */   }
/*  142:     */   
/*  143:     */   public boolean isDoubleMetaphoneEqual(String value1, String value2, boolean alternate)
/*  144:     */   {
/*  145: 244 */     return doubleMetaphone(value1, alternate).equals(doubleMetaphone(value2, alternate));
/*  146:     */   }
/*  147:     */   
/*  148:     */   public int getMaxCodeLen()
/*  149:     */   {
/*  150: 253 */     return this.maxCodeLen;
/*  151:     */   }
/*  152:     */   
/*  153:     */   public void setMaxCodeLen(int maxCodeLen)
/*  154:     */   {
/*  155: 261 */     this.maxCodeLen = maxCodeLen;
/*  156:     */   }
/*  157:     */   
/*  158:     */   private int handleAEIOUY(String value, DoubleMetaphoneResult result, int index)
/*  159:     */   {
/*  160: 271 */     if (index == 0) {
/*  161: 272 */       result.append('A');
/*  162:     */     }
/*  163: 274 */     return index + 1;
/*  164:     */   }
/*  165:     */   
/*  166:     */   private int handleC(String value, DoubleMetaphoneResult result, int index)
/*  167:     */   {
/*  168: 283 */     if (conditionC0(value, index))
/*  169:     */     {
/*  170: 284 */       result.append('K');
/*  171: 285 */       index += 2;
/*  172:     */     }
/*  173: 286 */     else if ((index == 0) && (contains(value, index, 6, "CAESAR")))
/*  174:     */     {
/*  175: 287 */       result.append('S');
/*  176: 288 */       index += 2;
/*  177:     */     }
/*  178: 289 */     else if (contains(value, index, 2, "CH"))
/*  179:     */     {
/*  180: 290 */       index = handleCH(value, result, index);
/*  181:     */     }
/*  182: 291 */     else if ((contains(value, index, 2, "CZ")) && (!contains(value, index - 2, 4, "WICZ")))
/*  183:     */     {
/*  184: 294 */       result.append('S', 'X');
/*  185: 295 */       index += 2;
/*  186:     */     }
/*  187: 296 */     else if (contains(value, index + 1, 3, "CIA"))
/*  188:     */     {
/*  189: 298 */       result.append('X');
/*  190: 299 */       index += 3;
/*  191:     */     }
/*  192:     */     else
/*  193:     */     {
/*  194: 300 */       if ((contains(value, index, 2, "CC")) && ((index != 1) || (charAt(value, 0) != 'M'))) {
/*  195: 303 */         return handleCC(value, result, index);
/*  196:     */       }
/*  197: 304 */       if (contains(value, index, 2, "CK", "CG", "CQ"))
/*  198:     */       {
/*  199: 305 */         result.append('K');
/*  200: 306 */         index += 2;
/*  201:     */       }
/*  202: 307 */       else if (contains(value, index, 2, "CI", "CE", "CY"))
/*  203:     */       {
/*  204: 309 */         if (contains(value, index, 3, "CIO", "CIE", "CIA")) {
/*  205: 310 */           result.append('S', 'X');
/*  206:     */         } else {
/*  207: 312 */           result.append('S');
/*  208:     */         }
/*  209: 314 */         index += 2;
/*  210:     */       }
/*  211:     */       else
/*  212:     */       {
/*  213: 316 */         result.append('K');
/*  214: 317 */         if (contains(value, index + 1, 2, " C", " Q", " G")) {
/*  215: 319 */           index += 3;
/*  216: 320 */         } else if ((contains(value, index + 1, 1, "C", "K", "Q")) && (!contains(value, index + 1, 2, "CE", "CI"))) {
/*  217: 322 */           index += 2;
/*  218:     */         } else {
/*  219: 324 */           index++;
/*  220:     */         }
/*  221:     */       }
/*  222:     */     }
/*  223: 328 */     return index;
/*  224:     */   }
/*  225:     */   
/*  226:     */   private int handleCC(String value, DoubleMetaphoneResult result, int index)
/*  227:     */   {
/*  228: 337 */     if ((contains(value, index + 2, 1, "I", "E", "H")) && (!contains(value, index + 2, 2, "HU")))
/*  229:     */     {
/*  230: 340 */       if (((index == 1) && (charAt(value, index - 1) == 'A')) || (contains(value, index - 1, 5, "UCCEE", "UCCES"))) {
/*  231: 343 */         result.append("KS");
/*  232:     */       } else {
/*  233: 346 */         result.append('X');
/*  234:     */       }
/*  235: 348 */       index += 3;
/*  236:     */     }
/*  237:     */     else
/*  238:     */     {
/*  239: 350 */       result.append('K');
/*  240: 351 */       index += 2;
/*  241:     */     }
/*  242: 354 */     return index;
/*  243:     */   }
/*  244:     */   
/*  245:     */   private int handleCH(String value, DoubleMetaphoneResult result, int index)
/*  246:     */   {
/*  247: 363 */     if ((index > 0) && (contains(value, index, 4, "CHAE")))
/*  248:     */     {
/*  249: 364 */       result.append('K', 'X');
/*  250: 365 */       return index + 2;
/*  251:     */     }
/*  252: 366 */     if (conditionCH0(value, index))
/*  253:     */     {
/*  254: 368 */       result.append('K');
/*  255: 369 */       return index + 2;
/*  256:     */     }
/*  257: 370 */     if (conditionCH1(value, index))
/*  258:     */     {
/*  259: 372 */       result.append('K');
/*  260: 373 */       return index + 2;
/*  261:     */     }
/*  262: 375 */     if (index > 0)
/*  263:     */     {
/*  264: 376 */       if (contains(value, 0, 2, "MC")) {
/*  265: 377 */         result.append('K');
/*  266:     */       } else {
/*  267: 379 */         result.append('X', 'K');
/*  268:     */       }
/*  269:     */     }
/*  270:     */     else {
/*  271: 382 */       result.append('X');
/*  272:     */     }
/*  273: 384 */     return index + 2;
/*  274:     */   }
/*  275:     */   
/*  276:     */   private int handleD(String value, DoubleMetaphoneResult result, int index)
/*  277:     */   {
/*  278: 394 */     if (contains(value, index, 2, "DG"))
/*  279:     */     {
/*  280: 396 */       if (contains(value, index + 2, 1, "I", "E", "Y"))
/*  281:     */       {
/*  282: 397 */         result.append('J');
/*  283: 398 */         index += 3;
/*  284:     */       }
/*  285:     */       else
/*  286:     */       {
/*  287: 401 */         result.append("TK");
/*  288: 402 */         index += 2;
/*  289:     */       }
/*  290:     */     }
/*  291: 404 */     else if (contains(value, index, 2, "DT", "DD"))
/*  292:     */     {
/*  293: 405 */       result.append('T');
/*  294: 406 */       index += 2;
/*  295:     */     }
/*  296:     */     else
/*  297:     */     {
/*  298: 408 */       result.append('T');
/*  299: 409 */       index++;
/*  300:     */     }
/*  301: 411 */     return index;
/*  302:     */   }
/*  303:     */   
/*  304:     */   private int handleG(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic)
/*  305:     */   {
/*  306: 421 */     if (charAt(value, index + 1) == 'H')
/*  307:     */     {
/*  308: 422 */       index = handleGH(value, result, index);
/*  309:     */     }
/*  310: 423 */     else if (charAt(value, index + 1) == 'N')
/*  311:     */     {
/*  312: 424 */       if ((index == 1) && (isVowel(charAt(value, 0))) && (!slavoGermanic)) {
/*  313: 425 */         result.append("KN", "N");
/*  314: 426 */       } else if ((!contains(value, index + 2, 2, "EY")) && (charAt(value, index + 1) != 'Y') && (!slavoGermanic)) {
/*  315: 428 */         result.append("N", "KN");
/*  316:     */       } else {
/*  317: 430 */         result.append("KN");
/*  318:     */       }
/*  319: 432 */       index += 2;
/*  320:     */     }
/*  321: 433 */     else if ((contains(value, index + 1, 2, "LI")) && (!slavoGermanic))
/*  322:     */     {
/*  323: 434 */       result.append("KL", "L");
/*  324: 435 */       index += 2;
/*  325:     */     }
/*  326: 436 */     else if ((index == 0) && ((charAt(value, index + 1) == 'Y') || (contains(value, index + 1, 2, ES_EP_EB_EL_EY_IB_IL_IN_IE_EI_ER))))
/*  327:     */     {
/*  328: 438 */       result.append('K', 'J');
/*  329: 439 */       index += 2;
/*  330:     */     }
/*  331: 440 */     else if (((contains(value, index + 1, 2, "ER")) || (charAt(value, index + 1) == 'Y')) && (!contains(value, 0, 6, "DANGER", "RANGER", "MANGER")) && (!contains(value, index - 1, 1, "E", "I")) && (!contains(value, index - 1, 3, "RGY", "OGY")))
/*  332:     */     {
/*  333: 446 */       result.append('K', 'J');
/*  334: 447 */       index += 2;
/*  335:     */     }
/*  336: 448 */     else if ((contains(value, index + 1, 1, "E", "I", "Y")) || (contains(value, index - 1, 4, "AGGI", "OGGI")))
/*  337:     */     {
/*  338: 451 */       if ((contains(value, 0, 4, "VAN ", "VON ")) || (contains(value, 0, 3, "SCH")) || (contains(value, index + 1, 2, "ET"))) {
/*  339: 453 */         result.append('K');
/*  340: 454 */       } else if (contains(value, index + 1, 4, "IER")) {
/*  341: 455 */         result.append('J');
/*  342:     */       } else {
/*  343: 457 */         result.append('J', 'K');
/*  344:     */       }
/*  345: 459 */       index += 2;
/*  346:     */     }
/*  347: 460 */     else if (charAt(value, index + 1) == 'G')
/*  348:     */     {
/*  349: 461 */       index += 2;
/*  350: 462 */       result.append('K');
/*  351:     */     }
/*  352:     */     else
/*  353:     */     {
/*  354: 464 */       index++;
/*  355: 465 */       result.append('K');
/*  356:     */     }
/*  357: 467 */     return index;
/*  358:     */   }
/*  359:     */   
/*  360:     */   private int handleGH(String value, DoubleMetaphoneResult result, int index)
/*  361:     */   {
/*  362: 476 */     if ((index > 0) && (!isVowel(charAt(value, index - 1))))
/*  363:     */     {
/*  364: 477 */       result.append('K');
/*  365: 478 */       index += 2;
/*  366:     */     }
/*  367: 479 */     else if (index == 0)
/*  368:     */     {
/*  369: 480 */       if (charAt(value, index + 2) == 'I') {
/*  370: 481 */         result.append('J');
/*  371:     */       } else {
/*  372: 483 */         result.append('K');
/*  373:     */       }
/*  374: 485 */       index += 2;
/*  375:     */     }
/*  376: 486 */     else if (((index > 1) && (contains(value, index - 2, 1, "B", "H", "D"))) || ((index > 2) && (contains(value, index - 3, 1, "B", "H", "D"))) || ((index > 3) && (contains(value, index - 4, 1, "B", "H"))))
/*  377:     */     {
/*  378: 490 */       index += 2;
/*  379:     */     }
/*  380:     */     else
/*  381:     */     {
/*  382: 492 */       if ((index > 2) && (charAt(value, index - 1) == 'U') && (contains(value, index - 3, 1, "C", "G", "L", "R", "T"))) {
/*  383: 495 */         result.append('F');
/*  384: 496 */       } else if ((index > 0) && (charAt(value, index - 1) != 'I')) {
/*  385: 497 */         result.append('K');
/*  386:     */       }
/*  387: 499 */       index += 2;
/*  388:     */     }
/*  389: 501 */     return index;
/*  390:     */   }
/*  391:     */   
/*  392:     */   private int handleH(String value, DoubleMetaphoneResult result, int index)
/*  393:     */   {
/*  394: 511 */     if (((index == 0) || (isVowel(charAt(value, index - 1)))) && (isVowel(charAt(value, index + 1))))
/*  395:     */     {
/*  396: 513 */       result.append('H');
/*  397: 514 */       index += 2;
/*  398:     */     }
/*  399:     */     else
/*  400:     */     {
/*  401: 517 */       index++;
/*  402:     */     }
/*  403: 519 */     return index;
/*  404:     */   }
/*  405:     */   
/*  406:     */   private int handleJ(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic)
/*  407:     */   {
/*  408: 527 */     if ((contains(value, index, 4, "JOSE")) || (contains(value, 0, 4, "SAN ")))
/*  409:     */     {
/*  410: 529 */       if (((index == 0) && (charAt(value, index + 4) == ' ')) || (value.length() == 4) || (contains(value, 0, 4, "SAN "))) {
/*  411: 531 */         result.append('H');
/*  412:     */       } else {
/*  413: 533 */         result.append('J', 'H');
/*  414:     */       }
/*  415: 535 */       index++;
/*  416:     */     }
/*  417:     */     else
/*  418:     */     {
/*  419: 537 */       if ((index == 0) && (!contains(value, index, 4, "JOSE"))) {
/*  420: 538 */         result.append('J', 'A');
/*  421: 539 */       } else if ((isVowel(charAt(value, index - 1))) && (!slavoGermanic) && ((charAt(value, index + 1) == 'A') || (charAt(value, index + 1) == 'O'))) {
/*  422: 541 */         result.append('J', 'H');
/*  423: 542 */       } else if (index == value.length() - 1) {
/*  424: 543 */         result.append('J', ' ');
/*  425: 544 */       } else if ((!contains(value, index + 1, 1, L_T_K_S_N_M_B_Z)) && (!contains(value, index - 1, 1, "S", "K", "L"))) {
/*  426: 545 */         result.append('J');
/*  427:     */       }
/*  428: 548 */       if (charAt(value, index + 1) == 'J') {
/*  429: 549 */         index += 2;
/*  430:     */       } else {
/*  431: 551 */         index++;
/*  432:     */       }
/*  433:     */     }
/*  434: 554 */     return index;
/*  435:     */   }
/*  436:     */   
/*  437:     */   private int handleL(String value, DoubleMetaphoneResult result, int index)
/*  438:     */   {
/*  439: 563 */     result.append('L');
/*  440: 564 */     if (charAt(value, index + 1) == 'L')
/*  441:     */     {
/*  442: 565 */       if (conditionL0(value, index)) {
/*  443: 566 */         result.appendAlternate(' ');
/*  444:     */       }
/*  445: 568 */       index += 2;
/*  446:     */     }
/*  447:     */     else
/*  448:     */     {
/*  449: 570 */       index++;
/*  450:     */     }
/*  451: 572 */     return index;
/*  452:     */   }
/*  453:     */   
/*  454:     */   private int handleP(String value, DoubleMetaphoneResult result, int index)
/*  455:     */   {
/*  456: 581 */     if (charAt(value, index + 1) == 'H')
/*  457:     */     {
/*  458: 582 */       result.append('F');
/*  459: 583 */       index += 2;
/*  460:     */     }
/*  461:     */     else
/*  462:     */     {
/*  463: 585 */       result.append('P');
/*  464: 586 */       index = contains(value, index + 1, 1, "P", "B") ? index + 2 : index + 1;
/*  465:     */     }
/*  466: 588 */     return index;
/*  467:     */   }
/*  468:     */   
/*  469:     */   private int handleR(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic)
/*  470:     */   {
/*  471: 598 */     if ((index == value.length() - 1) && (!slavoGermanic) && (contains(value, index - 2, 2, "IE")) && (!contains(value, index - 4, 2, "ME", "MA"))) {
/*  472: 601 */       result.appendAlternate('R');
/*  473:     */     } else {
/*  474: 603 */       result.append('R');
/*  475:     */     }
/*  476: 605 */     return charAt(value, index + 1) == 'R' ? index + 2 : index + 1;
/*  477:     */   }
/*  478:     */   
/*  479:     */   private int handleS(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic)
/*  480:     */   {
/*  481: 615 */     if (contains(value, index - 1, 3, "ISL", "YSL"))
/*  482:     */     {
/*  483: 617 */       index++;
/*  484:     */     }
/*  485: 618 */     else if ((index == 0) && (contains(value, index, 5, "SUGAR")))
/*  486:     */     {
/*  487: 620 */       result.append('X', 'S');
/*  488: 621 */       index++;
/*  489:     */     }
/*  490: 622 */     else if (contains(value, index, 2, "SH"))
/*  491:     */     {
/*  492: 623 */       if (contains(value, index + 1, 4, "HEIM", "HOEK", "HOLM", "HOLZ")) {
/*  493: 626 */         result.append('S');
/*  494:     */       } else {
/*  495: 628 */         result.append('X');
/*  496:     */       }
/*  497: 630 */       index += 2;
/*  498:     */     }
/*  499: 631 */     else if ((contains(value, index, 3, "SIO", "SIA")) || (contains(value, index, 4, "SIAN")))
/*  500:     */     {
/*  501: 633 */       if (slavoGermanic) {
/*  502: 634 */         result.append('S');
/*  503:     */       } else {
/*  504: 636 */         result.append('S', 'X');
/*  505:     */       }
/*  506: 638 */       index += 3;
/*  507:     */     }
/*  508: 639 */     else if (((index == 0) && (contains(value, index + 1, 1, "M", "N", "L", "W"))) || (contains(value, index + 1, 1, "Z")))
/*  509:     */     {
/*  510: 644 */       result.append('S', 'X');
/*  511: 645 */       index = contains(value, index + 1, 1, "Z") ? index + 2 : index + 1;
/*  512:     */     }
/*  513: 646 */     else if (contains(value, index, 2, "SC"))
/*  514:     */     {
/*  515: 647 */       index = handleSC(value, result, index);
/*  516:     */     }
/*  517:     */     else
/*  518:     */     {
/*  519: 649 */       if ((index == value.length() - 1) && (contains(value, index - 2, 2, "AI", "OI"))) {
/*  520: 652 */         result.appendAlternate('S');
/*  521:     */       } else {
/*  522: 654 */         result.append('S');
/*  523:     */       }
/*  524: 656 */       index = contains(value, index + 1, 1, "S", "Z") ? index + 2 : index + 1;
/*  525:     */     }
/*  526: 658 */     return index;
/*  527:     */   }
/*  528:     */   
/*  529:     */   private int handleSC(String value, DoubleMetaphoneResult result, int index)
/*  530:     */   {
/*  531: 667 */     if (charAt(value, index + 2) == 'H')
/*  532:     */     {
/*  533: 669 */       if (contains(value, index + 3, 2, "OO", "ER", "EN", "UY", "ED", "EM"))
/*  534:     */       {
/*  535: 672 */         if (contains(value, index + 3, 2, "ER", "EN")) {
/*  536: 674 */           result.append("X", "SK");
/*  537:     */         } else {
/*  538: 676 */           result.append("SK");
/*  539:     */         }
/*  540:     */       }
/*  541: 679 */       else if ((index == 0) && (!isVowel(charAt(value, 3))) && (charAt(value, 3) != 'W')) {
/*  542: 680 */         result.append('X', 'S');
/*  543:     */       } else {
/*  544: 682 */         result.append('X');
/*  545:     */       }
/*  546:     */     }
/*  547: 685 */     else if (contains(value, index + 2, 1, "I", "E", "Y")) {
/*  548: 686 */       result.append('S');
/*  549:     */     } else {
/*  550: 688 */       result.append("SK");
/*  551:     */     }
/*  552: 690 */     return index + 3;
/*  553:     */   }
/*  554:     */   
/*  555:     */   private int handleT(String value, DoubleMetaphoneResult result, int index)
/*  556:     */   {
/*  557: 699 */     if (contains(value, index, 4, "TION"))
/*  558:     */     {
/*  559: 700 */       result.append('X');
/*  560: 701 */       index += 3;
/*  561:     */     }
/*  562: 702 */     else if (contains(value, index, 3, "TIA", "TCH"))
/*  563:     */     {
/*  564: 703 */       result.append('X');
/*  565: 704 */       index += 3;
/*  566:     */     }
/*  567: 705 */     else if ((contains(value, index, 2, "TH")) || (contains(value, index, 3, "TTH")))
/*  568:     */     {
/*  569: 707 */       if ((contains(value, index + 2, 2, "OM", "AM")) || (contains(value, 0, 4, "VAN ", "VON ")) || (contains(value, 0, 3, "SCH"))) {
/*  570: 711 */         result.append('T');
/*  571:     */       } else {
/*  572: 713 */         result.append('0', 'T');
/*  573:     */       }
/*  574: 715 */       index += 2;
/*  575:     */     }
/*  576:     */     else
/*  577:     */     {
/*  578: 717 */       result.append('T');
/*  579: 718 */       index = contains(value, index + 1, 1, "T", "D") ? index + 2 : index + 1;
/*  580:     */     }
/*  581: 720 */     return index;
/*  582:     */   }
/*  583:     */   
/*  584:     */   private int handleW(String value, DoubleMetaphoneResult result, int index)
/*  585:     */   {
/*  586: 729 */     if (contains(value, index, 2, "WR"))
/*  587:     */     {
/*  588: 731 */       result.append('R');
/*  589: 732 */       index += 2;
/*  590:     */     }
/*  591: 734 */     else if ((index == 0) && ((isVowel(charAt(value, index + 1))) || (contains(value, index, 2, "WH"))))
/*  592:     */     {
/*  593: 736 */       if (isVowel(charAt(value, index + 1))) {
/*  594: 738 */         result.append('A', 'F');
/*  595:     */       } else {
/*  596: 741 */         result.append('A');
/*  597:     */       }
/*  598: 743 */       index++;
/*  599:     */     }
/*  600: 744 */     else if (((index == value.length() - 1) && (isVowel(charAt(value, index - 1)))) || (contains(value, index - 1, 5, "EWSKI", "EWSKY", "OWSKI", "OWSKY")) || (contains(value, 0, 3, "SCH")))
/*  601:     */     {
/*  602: 749 */       result.appendAlternate('F');
/*  603: 750 */       index++;
/*  604:     */     }
/*  605: 751 */     else if (contains(value, index, 4, "WICZ", "WITZ"))
/*  606:     */     {
/*  607: 753 */       result.append("TS", "FX");
/*  608: 754 */       index += 4;
/*  609:     */     }
/*  610:     */     else
/*  611:     */     {
/*  612: 756 */       index++;
/*  613:     */     }
/*  614: 759 */     return index;
/*  615:     */   }
/*  616:     */   
/*  617:     */   private int handleX(String value, DoubleMetaphoneResult result, int index)
/*  618:     */   {
/*  619: 768 */     if (index == 0)
/*  620:     */     {
/*  621: 769 */       result.append('S');
/*  622: 770 */       index++;
/*  623:     */     }
/*  624:     */     else
/*  625:     */     {
/*  626: 772 */       if ((index != value.length() - 1) || ((!contains(value, index - 3, 3, "IAU", "EAU")) && (!contains(value, index - 2, 2, "AU", "OU")))) {
/*  627: 776 */         result.append("KS");
/*  628:     */       }
/*  629: 778 */       index = contains(value, index + 1, 1, "C", "X") ? index + 2 : index + 1;
/*  630:     */     }
/*  631: 780 */     return index;
/*  632:     */   }
/*  633:     */   
/*  634:     */   private int handleZ(String value, DoubleMetaphoneResult result, int index, boolean slavoGermanic)
/*  635:     */   {
/*  636: 788 */     if (charAt(value, index + 1) == 'H')
/*  637:     */     {
/*  638: 790 */       result.append('J');
/*  639: 791 */       index += 2;
/*  640:     */     }
/*  641:     */     else
/*  642:     */     {
/*  643: 793 */       if ((contains(value, index + 1, 2, "ZO", "ZI", "ZA")) || ((slavoGermanic) && (index > 0) && (charAt(value, index - 1) != 'T'))) {
/*  644: 794 */         result.append("S", "TS");
/*  645:     */       } else {
/*  646: 796 */         result.append('S');
/*  647:     */       }
/*  648: 798 */       index = charAt(value, index + 1) == 'Z' ? index + 2 : index + 1;
/*  649:     */     }
/*  650: 800 */     return index;
/*  651:     */   }
/*  652:     */   
/*  653:     */   private boolean conditionC0(String value, int index)
/*  654:     */   {
/*  655: 809 */     if (contains(value, index, 4, "CHIA")) {
/*  656: 810 */       return true;
/*  657:     */     }
/*  658: 811 */     if (index <= 1) {
/*  659: 812 */       return false;
/*  660:     */     }
/*  661: 813 */     if (isVowel(charAt(value, index - 2))) {
/*  662: 814 */       return false;
/*  663:     */     }
/*  664: 815 */     if (!contains(value, index - 1, 3, "ACH")) {
/*  665: 816 */       return false;
/*  666:     */     }
/*  667: 818 */     char c = charAt(value, index + 2);
/*  668: 819 */     return ((c != 'I') && (c != 'E')) || (contains(value, index - 2, 6, "BACHER", "MACHER"));
/*  669:     */   }
/*  670:     */   
/*  671:     */   private boolean conditionCH0(String value, int index)
/*  672:     */   {
/*  673: 828 */     if (index != 0) {
/*  674: 829 */       return false;
/*  675:     */     }
/*  676: 830 */     if ((!contains(value, index + 1, 5, "HARAC", "HARIS")) && (!contains(value, index + 1, 3, "HOR", "HYM", "HIA", "HEM"))) {
/*  677: 832 */       return false;
/*  678:     */     }
/*  679: 833 */     if (contains(value, 0, 5, "CHORE")) {
/*  680: 834 */       return false;
/*  681:     */     }
/*  682: 836 */     return true;
/*  683:     */   }
/*  684:     */   
/*  685:     */   private boolean conditionCH1(String value, int index)
/*  686:     */   {
/*  687: 844 */     return (contains(value, 0, 4, "VAN ", "VON ")) || (contains(value, 0, 3, "SCH")) || (contains(value, index - 2, 6, "ORCHES", "ARCHIT", "ORCHID")) || (contains(value, index + 2, 1, "T", "S")) || (((contains(value, index - 1, 1, "A", "O", "U", "E")) || (index == 0)) && ((contains(value, index + 2, 1, L_R_N_M_B_H_F_V_W_SPACE)) || (index + 1 == value.length() - 1)));
/*  688:     */   }
/*  689:     */   
/*  690:     */   private boolean conditionL0(String value, int index)
/*  691:     */   {
/*  692: 856 */     if ((index == value.length() - 3) && (contains(value, index - 1, 4, "ILLO", "ILLA", "ALLE"))) {
/*  693: 858 */       return true;
/*  694:     */     }
/*  695: 859 */     if (((contains(value, index - 1, 2, "AS", "OS")) || (contains(value, value.length() - 1, 1, "A", "O"))) && (contains(value, index - 1, 4, "ALLE"))) {
/*  696: 862 */       return true;
/*  697:     */     }
/*  698: 864 */     return false;
/*  699:     */   }
/*  700:     */   
/*  701:     */   private boolean conditionM0(String value, int index)
/*  702:     */   {
/*  703: 872 */     if (charAt(value, index + 1) == 'M') {
/*  704: 873 */       return true;
/*  705:     */     }
/*  706: 875 */     return (contains(value, index - 1, 3, "UMB")) && ((index + 1 == value.length() - 1) || (contains(value, index + 2, 2, "ER")));
/*  707:     */   }
/*  708:     */   
/*  709:     */   private boolean isSlavoGermanic(String value)
/*  710:     */   {
/*  711: 887 */     return (value.indexOf('W') > -1) || (value.indexOf('K') > -1) || (value.indexOf("CZ") > -1) || (value.indexOf("WITZ") > -1);
/*  712:     */   }
/*  713:     */   
/*  714:     */   private boolean isVowel(char ch)
/*  715:     */   {
/*  716: 895 */     return "AEIOUY".indexOf(ch) != -1;
/*  717:     */   }
/*  718:     */   
/*  719:     */   private boolean isSilentStart(String value)
/*  720:     */   {
/*  721: 904 */     boolean result = false;
/*  722: 905 */     for (int i = 0; i < SILENT_START.length; i++) {
/*  723: 906 */       if (value.startsWith(SILENT_START[i]))
/*  724:     */       {
/*  725: 907 */         result = true;
/*  726: 908 */         break;
/*  727:     */       }
/*  728:     */     }
/*  729: 911 */     return result;
/*  730:     */   }
/*  731:     */   
/*  732:     */   private String cleanInput(String input)
/*  733:     */   {
/*  734: 918 */     if (input == null) {
/*  735: 919 */       return null;
/*  736:     */     }
/*  737: 921 */     input = input.trim();
/*  738: 922 */     if (input.length() == 0) {
/*  739: 923 */       return null;
/*  740:     */     }
/*  741: 925 */     return input.toUpperCase();
/*  742:     */   }
/*  743:     */   
/*  744:     */   protected char charAt(String value, int index)
/*  745:     */   {
/*  746: 934 */     if ((index < 0) || (index >= value.length())) {
/*  747: 935 */       return '\000';
/*  748:     */     }
/*  749: 937 */     return value.charAt(index);
/*  750:     */   }
/*  751:     */   
/*  752:     */   private static boolean contains(String value, int start, int length, String criteria)
/*  753:     */   {
/*  754: 945 */     return contains(value, start, length, new String[] { criteria });
/*  755:     */   }
/*  756:     */   
/*  757:     */   private static boolean contains(String value, int start, int length, String criteria1, String criteria2)
/*  758:     */   {
/*  759: 954 */     return contains(value, start, length, new String[] { criteria1, criteria2 });
/*  760:     */   }
/*  761:     */   
/*  762:     */   private static boolean contains(String value, int start, int length, String criteria1, String criteria2, String criteria3)
/*  763:     */   {
/*  764: 964 */     return contains(value, start, length, new String[] { criteria1, criteria2, criteria3 });
/*  765:     */   }
/*  766:     */   
/*  767:     */   private static boolean contains(String value, int start, int length, String criteria1, String criteria2, String criteria3, String criteria4)
/*  768:     */   {
/*  769: 974 */     return contains(value, start, length, new String[] { criteria1, criteria2, criteria3, criteria4 });
/*  770:     */   }
/*  771:     */   
/*  772:     */   private static boolean contains(String value, int start, int length, String criteria1, String criteria2, String criteria3, String criteria4, String criteria5)
/*  773:     */   {
/*  774: 986 */     return contains(value, start, length, new String[] { criteria1, criteria2, criteria3, criteria4, criteria5 });
/*  775:     */   }
/*  776:     */   
/*  777:     */   private static boolean contains(String value, int start, int length, String criteria1, String criteria2, String criteria3, String criteria4, String criteria5, String criteria6)
/*  778:     */   {
/*  779: 998 */     return contains(value, start, length, new String[] { criteria1, criteria2, criteria3, criteria4, criteria5, criteria6 });
/*  780:     */   }
/*  781:     */   
/*  782:     */   protected static boolean contains(String value, int start, int length, String[] criteria)
/*  783:     */   {
/*  784:1010 */     boolean result = false;
/*  785:1011 */     if ((start >= 0) && (start + length <= value.length()))
/*  786:     */     {
/*  787:1012 */       String target = value.substring(start, start + length);
/*  788:1014 */       for (int i = 0; i < criteria.length; i++) {
/*  789:1015 */         if (target.equals(criteria[i]))
/*  790:     */         {
/*  791:1016 */           result = true;
/*  792:1017 */           break;
/*  793:     */         }
/*  794:     */       }
/*  795:     */     }
/*  796:1021 */     return result;
/*  797:     */   }
/*  798:     */   
/*  799:     */   public class DoubleMetaphoneResult
/*  800:     */   {
/*  801:1032 */     private StringBuffer primary = new StringBuffer(DoubleMetaphone.this.getMaxCodeLen());
/*  802:1033 */     private StringBuffer alternate = new StringBuffer(DoubleMetaphone.this.getMaxCodeLen());
/*  803:     */     private int maxLength;
/*  804:     */     
/*  805:     */     public DoubleMetaphoneResult(int maxLength)
/*  806:     */     {
/*  807:1037 */       this.maxLength = maxLength;
/*  808:     */     }
/*  809:     */     
/*  810:     */     public void append(char value)
/*  811:     */     {
/*  812:1041 */       appendPrimary(value);
/*  813:1042 */       appendAlternate(value);
/*  814:     */     }
/*  815:     */     
/*  816:     */     public void append(char primary, char alternate)
/*  817:     */     {
/*  818:1046 */       appendPrimary(primary);
/*  819:1047 */       appendAlternate(alternate);
/*  820:     */     }
/*  821:     */     
/*  822:     */     public void appendPrimary(char value)
/*  823:     */     {
/*  824:1051 */       if (this.primary.length() < this.maxLength) {
/*  825:1052 */         this.primary.append(value);
/*  826:     */       }
/*  827:     */     }
/*  828:     */     
/*  829:     */     public void appendAlternate(char value)
/*  830:     */     {
/*  831:1057 */       if (this.alternate.length() < this.maxLength) {
/*  832:1058 */         this.alternate.append(value);
/*  833:     */       }
/*  834:     */     }
/*  835:     */     
/*  836:     */     public void append(String value)
/*  837:     */     {
/*  838:1063 */       appendPrimary(value);
/*  839:1064 */       appendAlternate(value);
/*  840:     */     }
/*  841:     */     
/*  842:     */     public void append(String primary, String alternate)
/*  843:     */     {
/*  844:1068 */       appendPrimary(primary);
/*  845:1069 */       appendAlternate(alternate);
/*  846:     */     }
/*  847:     */     
/*  848:     */     public void appendPrimary(String value)
/*  849:     */     {
/*  850:1073 */       int addChars = this.maxLength - this.primary.length();
/*  851:1074 */       if (value.length() <= addChars) {
/*  852:1075 */         this.primary.append(value);
/*  853:     */       } else {
/*  854:1077 */         this.primary.append(value.substring(0, addChars));
/*  855:     */       }
/*  856:     */     }
/*  857:     */     
/*  858:     */     public void appendAlternate(String value)
/*  859:     */     {
/*  860:1082 */       int addChars = this.maxLength - this.alternate.length();
/*  861:1083 */       if (value.length() <= addChars) {
/*  862:1084 */         this.alternate.append(value);
/*  863:     */       } else {
/*  864:1086 */         this.alternate.append(value.substring(0, addChars));
/*  865:     */       }
/*  866:     */     }
/*  867:     */     
/*  868:     */     public String getPrimary()
/*  869:     */     {
/*  870:1091 */       return this.primary.toString();
/*  871:     */     }
/*  872:     */     
/*  873:     */     public String getAlternate()
/*  874:     */     {
/*  875:1095 */       return this.alternate.toString();
/*  876:     */     }
/*  877:     */     
/*  878:     */     public boolean isComplete()
/*  879:     */     {
/*  880:1099 */       return (this.primary.length() >= this.maxLength) && (this.alternate.length() >= this.maxLength);
/*  881:     */     }
/*  882:     */   }
/*  883:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     org.apache.commons.codec.language.DoubleMetaphone
 * JD-Core Version:    0.7.0.1
 */