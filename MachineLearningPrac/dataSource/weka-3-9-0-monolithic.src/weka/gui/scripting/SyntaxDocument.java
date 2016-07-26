/*    1:     */ package weka.gui.scripting;
/*    2:     */ 
/*    3:     */ import java.awt.Color;
/*    4:     */ import java.awt.Font;
/*    5:     */ import java.awt.FontMetrics;
/*    6:     */ import java.awt.Toolkit;
/*    7:     */ import java.util.HashMap;
/*    8:     */ import java.util.Properties;
/*    9:     */ import javax.swing.text.AttributeSet;
/*   10:     */ import javax.swing.text.BadLocationException;
/*   11:     */ import javax.swing.text.DefaultStyledDocument;
/*   12:     */ import javax.swing.text.Element;
/*   13:     */ import javax.swing.text.MutableAttributeSet;
/*   14:     */ import javax.swing.text.SimpleAttributeSet;
/*   15:     */ import javax.swing.text.StyleConstants;
/*   16:     */ import javax.swing.text.TabSet;
/*   17:     */ import javax.swing.text.TabStop;
/*   18:     */ import weka.gui.visualize.VisualizeUtils;
/*   19:     */ 
/*   20:     */ public class SyntaxDocument
/*   21:     */   extends DefaultStyledDocument
/*   22:     */ {
/*   23:     */   protected static final long serialVersionUID = -3642426465631271381L;
/*   24:     */   public static final int MAX_TABS = 35;
/*   25:     */   public static final String DEFAULT_FONT_FAMILY = "monospaced";
/*   26:     */   public static final int DEFAULT_FONT_SIZE = 12;
/*   27:  69 */   public static final SimpleAttributeSet DEFAULT_NORMAL = new SimpleAttributeSet();
/*   28:     */   public static final SimpleAttributeSet DEFAULT_COMMENT;
/*   29:     */   public static final SimpleAttributeSet DEFAULT_STRING;
/*   30:     */   public static final SimpleAttributeSet DEFAULT_KEYWORD;
/*   31:     */   protected DefaultStyledDocument m_Self;
/*   32:     */   protected Element m_RootElement;
/*   33:     */   protected boolean m_InsideMultiLineComment;
/*   34:     */   protected HashMap<String, MutableAttributeSet> m_Keywords;
/*   35:     */   protected String m_Delimiters;
/*   36:     */   protected String m_QuoteDelimiters;
/*   37:     */   protected String m_QuoteEscape;
/*   38:     */   protected String m_MultiLineCommentStart;
/*   39:     */   protected String m_MultiLineCommentEnd;
/*   40:     */   protected String m_SingleLineCommentStart;
/*   41:     */   protected String m_BlockStart;
/*   42:     */   protected String m_BlockEnd;
/*   43:     */   protected int m_FontSize;
/*   44:     */   protected String m_FontName;
/*   45:     */   protected Color m_BackgroundColor;
/*   46:     */   protected String m_Indentation;
/*   47:     */   protected boolean m_AddMatchingEndBlocks;
/*   48:     */   protected boolean m_UseBlanks;
/*   49:     */   protected boolean m_MultiLineComment;
/*   50:     */   protected boolean m_CaseSensitive;
/*   51:     */   
/*   52:     */   static
/*   53:     */   {
/*   54:  70 */     StyleConstants.setForeground(DEFAULT_NORMAL, Color.BLACK);
/*   55:  71 */     StyleConstants.setFontFamily(DEFAULT_NORMAL, "monospaced");
/*   56:  72 */     StyleConstants.setFontSize(DEFAULT_NORMAL, 12);
/*   57:     */     
/*   58:  74 */     DEFAULT_COMMENT = new SimpleAttributeSet();
/*   59:  75 */     StyleConstants.setForeground(DEFAULT_COMMENT, Color.GRAY);
/*   60:  76 */     StyleConstants.setFontFamily(DEFAULT_COMMENT, "monospaced");
/*   61:  77 */     StyleConstants.setFontSize(DEFAULT_COMMENT, 12);
/*   62:     */     
/*   63:  79 */     DEFAULT_STRING = new SimpleAttributeSet();
/*   64:  80 */     StyleConstants.setForeground(DEFAULT_STRING, Color.RED);
/*   65:  81 */     StyleConstants.setFontFamily(DEFAULT_STRING, "monospaced");
/*   66:  82 */     StyleConstants.setFontSize(DEFAULT_STRING, 12);
/*   67:     */     
/*   68:     */ 
/*   69:  85 */     DEFAULT_KEYWORD = new SimpleAttributeSet();
/*   70:  86 */     StyleConstants.setForeground(DEFAULT_KEYWORD, Color.BLUE);
/*   71:  87 */     StyleConstants.setBold(DEFAULT_KEYWORD, false);
/*   72:  88 */     StyleConstants.setFontFamily(DEFAULT_KEYWORD, "monospaced");
/*   73:  89 */     StyleConstants.setFontSize(DEFAULT_KEYWORD, 12);
/*   74:     */   }
/*   75:     */   
/*   76:     */   public static enum ATTR_TYPE
/*   77:     */   {
/*   78:  97 */     Normal,  Comment,  Quote;
/*   79:     */     
/*   80:     */     private ATTR_TYPE() {}
/*   81:     */   }
/*   82:     */   
/*   83:     */   public SyntaxDocument(Properties props)
/*   84:     */   {
/*   85: 170 */     this.m_Self = this;
/*   86: 171 */     this.m_RootElement = this.m_Self.getDefaultRootElement();
/*   87: 172 */     this.m_Keywords = new HashMap();
/*   88: 173 */     this.m_FontSize = 12;
/*   89: 174 */     this.m_FontName = "monospaced";
/*   90: 175 */     putProperty("__EndOfLine__", "\n");
/*   91:     */     
/*   92: 177 */     setup(props);
/*   93:     */   }
/*   94:     */   
/*   95:     */   protected void setup(Properties props)
/*   96:     */   {
/*   97: 186 */     setDelimiters(props.getProperty("Delimiters", ";:{}()[]+-/%<=>!&|^~*"));
/*   98: 187 */     setQuoteDelimiters(props.getProperty("QuoteDelimiters", "\"'"));
/*   99: 188 */     setQuoteEscape(props.getProperty("QuoteEscape", "\\"));
/*  100: 189 */     setSingleLineCommentStart(props.getProperty("SingleLineCommentStart", "//"));
/*  101: 190 */     setMultiLineComment(props.getProperty("MultiLineComment", "false").equals("true"));
/*  102:     */     
/*  103: 192 */     setMultiLineCommentStart(props.getProperty("MultiLineCommentStart", "/*"));
/*  104: 193 */     setMultiLineCommentEnd(props.getProperty("MultiLineCommentEnd", "*/"));
/*  105: 194 */     setBlockStart(props.getProperty("BlockStart", "{"));
/*  106: 195 */     setBlockEnd(props.getProperty("BlockEnd", "}"));
/*  107: 196 */     setAddMatchingEndBlocks(props.getProperty("AddMatchingBlockEnd", "false").equals("true"));
/*  108:     */     
/*  109: 198 */     setUseBlanks(props.getProperty("UseBlanks", "false").equals("true"));
/*  110: 199 */     setCaseSensitive(props.getProperty("CaseSensitive", "true").equals("true"));
/*  111: 200 */     addKeywords(props.getProperty("Keywords", "").trim().replaceAll(" ", "").split(","), DEFAULT_KEYWORD);
/*  112:     */     
/*  113: 202 */     setTabs(Integer.parseInt(props.getProperty("Tabs", "2")));
/*  114: 203 */     setAttributeColor(DEFAULT_NORMAL, VisualizeUtils.processColour(props.getProperty("ForegroundColor", "black"), Color.BLACK));
/*  115:     */     
/*  116:     */ 
/*  117:     */ 
/*  118: 207 */     setAttributeColor(DEFAULT_COMMENT, VisualizeUtils.processColour(props.getProperty("CommentColor", "gray"), Color.GRAY));
/*  119:     */     
/*  120: 209 */     setAttributeColor(DEFAULT_STRING, VisualizeUtils.processColour(props.getProperty("StringColor", "red"), Color.RED));
/*  121:     */     
/*  122: 211 */     setAttributeColor(DEFAULT_KEYWORD, VisualizeUtils.processColour(props.getProperty("KeywordColor", "blue"), Color.BLUE));
/*  123:     */     
/*  124: 213 */     setBackgroundColor(VisualizeUtils.processColour(props.getProperty("BackgroundColor", "white"), Color.WHITE));
/*  125:     */     
/*  126: 215 */     setFontName(props.getProperty("FontName", "monospaced"));
/*  127: 216 */     setFontSize(Integer.parseInt(props.getProperty("FontSize", "12")));
/*  128: 217 */     setIndentationSize(Integer.parseInt(props.getProperty("Indentation", "2")));
/*  129:     */   }
/*  130:     */   
/*  131:     */   public void setAttributeFont(ATTR_TYPE attr, int style)
/*  132:     */   {
/*  133: 227 */     Font f = new Font(this.m_FontName, style, this.m_FontSize);
/*  134: 229 */     if (attr == ATTR_TYPE.Comment) {
/*  135: 230 */       setAttributeFont(DEFAULT_COMMENT, f);
/*  136: 231 */     } else if (attr == ATTR_TYPE.Quote) {
/*  137: 232 */       setAttributeFont(DEFAULT_STRING, f);
/*  138:     */     } else {
/*  139: 234 */       setAttributeFont(DEFAULT_NORMAL, f);
/*  140:     */     }
/*  141:     */   }
/*  142:     */   
/*  143:     */   public static void setAttributeFont(MutableAttributeSet attr, Font f)
/*  144:     */   {
/*  145: 245 */     StyleConstants.setBold(attr, f.isBold());
/*  146: 246 */     StyleConstants.setItalic(attr, f.isItalic());
/*  147: 247 */     StyleConstants.setFontFamily(attr, f.getFamily());
/*  148: 248 */     StyleConstants.setFontSize(attr, f.getSize());
/*  149:     */   }
/*  150:     */   
/*  151:     */   public void setAttributeColor(ATTR_TYPE attr, Color c)
/*  152:     */   {
/*  153: 258 */     if (attr == ATTR_TYPE.Comment) {
/*  154: 259 */       setAttributeColor(DEFAULT_COMMENT, c);
/*  155: 260 */     } else if (attr == ATTR_TYPE.Quote) {
/*  156: 261 */       setAttributeColor(DEFAULT_STRING, c);
/*  157:     */     } else {
/*  158: 263 */       setAttributeColor(DEFAULT_NORMAL, c);
/*  159:     */     }
/*  160:     */   }
/*  161:     */   
/*  162:     */   public static void setAttributeColor(MutableAttributeSet attr, Color c)
/*  163:     */   {
/*  164: 274 */     StyleConstants.setForeground(attr, c);
/*  165:     */   }
/*  166:     */   
/*  167:     */   public void addKeywords(String[] keywords, MutableAttributeSet attr)
/*  168:     */   {
/*  169: 286 */     for (int i = 0; i < keywords.length; i++) {
/*  170: 287 */       addKeyword(keywords[i], attr);
/*  171:     */     }
/*  172:     */   }
/*  173:     */   
/*  174:     */   public void addKeyword(String keyword, MutableAttributeSet attr)
/*  175:     */   {
/*  176: 298 */     if (this.m_CaseSensitive) {
/*  177: 299 */       this.m_Keywords.put(keyword, attr);
/*  178:     */     } else {
/*  179: 301 */       this.m_Keywords.put(keyword.toLowerCase(), attr);
/*  180:     */     }
/*  181:     */   }
/*  182:     */   
/*  183:     */   public MutableAttributeSet getKeywordFormatting(String keyword)
/*  184:     */   {
/*  185: 313 */     if (this.m_CaseSensitive) {
/*  186: 314 */       return (MutableAttributeSet)this.m_Keywords.get(keyword);
/*  187:     */     }
/*  188: 316 */     return (MutableAttributeSet)this.m_Keywords.get(keyword.toLowerCase());
/*  189:     */   }
/*  190:     */   
/*  191:     */   public void removeKeyword(String keyword)
/*  192:     */   {
/*  193: 327 */     if (this.m_CaseSensitive) {
/*  194: 328 */       this.m_Keywords.remove(keyword);
/*  195:     */     } else {
/*  196: 330 */       this.m_Keywords.remove(keyword.toLowerCase());
/*  197:     */     }
/*  198:     */   }
/*  199:     */   
/*  200:     */   public void setTabs(int charactersPerTab)
/*  201:     */   {
/*  202: 340 */     Font f = new Font(this.m_FontName, 0, this.m_FontSize);
/*  203:     */     
/*  204:     */ 
/*  205: 343 */     FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(f);
/*  206: 344 */     int charWidth = fm.charWidth('w');
/*  207: 345 */     int tabWidth = charWidth * charactersPerTab;
/*  208:     */     
/*  209: 347 */     TabStop[] tabs = new TabStop[35];
/*  210: 349 */     for (int j = 0; j < tabs.length; j++) {
/*  211: 350 */       tabs[j] = new TabStop((j + 1) * tabWidth);
/*  212:     */     }
/*  213: 353 */     TabSet tabSet = new TabSet(tabs);
/*  214: 354 */     SimpleAttributeSet attributes = new SimpleAttributeSet();
/*  215: 355 */     StyleConstants.setTabSet(attributes, tabSet);
/*  216: 356 */     int length = getLength();
/*  217: 357 */     setParagraphAttributes(0, length, attributes, false);
/*  218:     */   }
/*  219:     */   
/*  220:     */   public void insertString(int offset, String str, AttributeSet a)
/*  221:     */     throws BadLocationException
/*  222:     */   {
/*  223: 371 */     if ((this.m_AddMatchingEndBlocks) && (this.m_BlockStart.length() > 0) && (str.equals(this.m_BlockStart))) {
/*  224: 373 */       str = addMatchingBlockEnd(offset);
/*  225: 374 */     } else if ((this.m_UseBlanks) && (str.equals("\t"))) {
/*  226: 375 */       str = this.m_Indentation;
/*  227:     */     }
/*  228: 378 */     super.insertString(offset, str, a);
/*  229: 379 */     processChangedLines(offset, str.length());
/*  230:     */   }
/*  231:     */   
/*  232:     */   public void remove(int offset, int length)
/*  233:     */     throws BadLocationException
/*  234:     */   {
/*  235: 391 */     super.remove(offset, length);
/*  236: 392 */     processChangedLines(offset, 0);
/*  237:     */   }
/*  238:     */   
/*  239:     */   public void processChangedLines(int offset, int length)
/*  240:     */     throws BadLocationException
/*  241:     */   {
/*  242: 405 */     String content = this.m_Self.getText(0, this.m_Self.getLength());
/*  243:     */     
/*  244:     */ 
/*  245:     */ 
/*  246: 409 */     int startLine = this.m_RootElement.getElementIndex(offset);
/*  247: 410 */     int endLine = this.m_RootElement.getElementIndex(offset + length);
/*  248: 415 */     if (getMultiLineComment()) {
/*  249: 416 */       setInsideMultiLineComment(commentLinesBefore(content, startLine));
/*  250:     */     }
/*  251: 421 */     for (int i = startLine; i <= endLine; i++) {
/*  252: 422 */       applyHighlighting(content, i);
/*  253:     */     }
/*  254: 427 */     if (isMultiLineComment()) {
/*  255: 428 */       commentLinesAfter(content, endLine);
/*  256:     */     } else {
/*  257: 430 */       highlightLinesAfter(content, endLine);
/*  258:     */     }
/*  259:     */   }
/*  260:     */   
/*  261:     */   protected boolean commentLinesBefore(String content, int line)
/*  262:     */   {
/*  263: 443 */     int offset = this.m_RootElement.getElement(line).getStartOffset();
/*  264:     */     
/*  265:     */ 
/*  266:     */ 
/*  267: 447 */     int startDelimiter = -1;
/*  268: 448 */     if (getMultiLineComment()) {
/*  269: 449 */       startDelimiter = lastIndexOf(content, getMultiLineCommentStart(), offset - 2);
/*  270:     */     }
/*  271: 453 */     if (startDelimiter < 0) {
/*  272: 454 */       return false;
/*  273:     */     }
/*  274: 459 */     int endDelimiter = indexOf(content, getMultiLineCommentEnd(), startDelimiter);
/*  275: 462 */     if (((endDelimiter < offset ? 1 : 0) & (endDelimiter != -1 ? 1 : 0)) != 0) {
/*  276: 463 */       return false;
/*  277:     */     }
/*  278: 468 */     this.m_Self.setCharacterAttributes(startDelimiter, offset - startDelimiter + 1, DEFAULT_COMMENT, false);
/*  279:     */     
/*  280: 470 */     return true;
/*  281:     */   }
/*  282:     */   
/*  283:     */   protected void commentLinesAfter(String content, int line)
/*  284:     */   {
/*  285: 480 */     int offset = this.m_RootElement.getElement(line).getEndOffset();
/*  286:     */     
/*  287:     */ 
/*  288:     */ 
/*  289: 484 */     int endDelimiter = -1;
/*  290: 485 */     if (getMultiLineComment()) {
/*  291: 486 */       endDelimiter = indexOf(content, getMultiLineCommentEnd(), offset);
/*  292:     */     }
/*  293: 489 */     if (endDelimiter < 0) {
/*  294: 490 */       return;
/*  295:     */     }
/*  296: 495 */     int startDelimiter = lastIndexOf(content, getMultiLineCommentStart(), endDelimiter);
/*  297: 498 */     if ((startDelimiter < 0) || (startDelimiter <= offset)) {
/*  298: 499 */       this.m_Self.setCharacterAttributes(offset, endDelimiter - offset + 1, DEFAULT_COMMENT, false);
/*  299:     */     }
/*  300:     */   }
/*  301:     */   
/*  302:     */   protected void highlightLinesAfter(String content, int line)
/*  303:     */     throws BadLocationException
/*  304:     */   {
/*  305: 513 */     int offset = this.m_RootElement.getElement(line).getEndOffset();
/*  306:     */     
/*  307:     */ 
/*  308:     */ 
/*  309: 517 */     int startDelimiter = -1;
/*  310: 518 */     int endDelimiter = -1;
/*  311: 519 */     if (getMultiLineComment())
/*  312:     */     {
/*  313: 520 */       startDelimiter = indexOf(content, getMultiLineCommentStart(), offset);
/*  314: 521 */       endDelimiter = indexOf(content, getMultiLineCommentEnd(), offset);
/*  315:     */     }
/*  316: 524 */     if (startDelimiter < 0) {
/*  317: 525 */       startDelimiter = content.length();
/*  318:     */     }
/*  319: 528 */     if (endDelimiter < 0) {
/*  320: 529 */       endDelimiter = content.length();
/*  321:     */     }
/*  322: 532 */     int delimiter = Math.min(startDelimiter, endDelimiter);
/*  323: 534 */     if (delimiter < offset) {
/*  324: 535 */       return;
/*  325:     */     }
/*  326: 540 */     int endLine = this.m_RootElement.getElementIndex(delimiter);
/*  327: 542 */     for (int i = line + 1; i < endLine; i++)
/*  328:     */     {
/*  329: 543 */       Element branch = this.m_RootElement.getElement(i);
/*  330: 544 */       Element leaf = this.m_Self.getCharacterElement(branch.getStartOffset());
/*  331: 545 */       AttributeSet as = leaf.getAttributes();
/*  332: 547 */       if (as.isEqual(DEFAULT_COMMENT)) {
/*  333: 548 */         applyHighlighting(content, i);
/*  334:     */       }
/*  335:     */     }
/*  336:     */   }
/*  337:     */   
/*  338:     */   protected void applyHighlighting(String content, int line)
/*  339:     */     throws BadLocationException
/*  340:     */   {
/*  341: 562 */     int startOffset = this.m_RootElement.getElement(line).getStartOffset();
/*  342: 563 */     int endOffset = this.m_RootElement.getElement(line).getEndOffset() - 1;
/*  343:     */     
/*  344: 565 */     int lineLength = endOffset - startOffset;
/*  345: 566 */     int contentLength = content.length();
/*  346: 568 */     if (endOffset >= contentLength) {
/*  347: 569 */       endOffset = contentLength - 1;
/*  348:     */     }
/*  349: 575 */     if ((getMultiLineComment()) && (
/*  350: 576 */       (endingMultiLineComment(content, startOffset, endOffset)) || (isMultiLineComment()) || (startingMultiLineComment(content, startOffset, endOffset))))
/*  351:     */     {
/*  352: 579 */       this.m_Self.setCharacterAttributes(startOffset, endOffset - startOffset + 1, DEFAULT_COMMENT, false);
/*  353:     */       
/*  354: 581 */       return;
/*  355:     */     }
/*  356: 587 */     this.m_Self.setCharacterAttributes(startOffset, lineLength, DEFAULT_NORMAL, true);
/*  357:     */     
/*  358:     */ 
/*  359:     */ 
/*  360:     */ 
/*  361: 592 */     int index = content.indexOf(getSingleLineCommentStart(), startOffset);
/*  362: 594 */     if ((index > -1) && (index < endOffset))
/*  363:     */     {
/*  364: 595 */       this.m_Self.setCharacterAttributes(index, endOffset - index + 1, DEFAULT_COMMENT, false);
/*  365:     */       
/*  366: 597 */       endOffset = index - 1;
/*  367:     */     }
/*  368: 602 */     checkForTokens(content, startOffset, endOffset);
/*  369:     */   }
/*  370:     */   
/*  371:     */   protected boolean startingMultiLineComment(String content, int startOffset, int endOffset)
/*  372:     */     throws BadLocationException
/*  373:     */   {
/*  374: 616 */     if (!getMultiLineComment()) {
/*  375: 617 */       return false;
/*  376:     */     }
/*  377: 620 */     int index = indexOf(content, getMultiLineCommentStart(), startOffset);
/*  378: 622 */     if ((index < 0) || (index > endOffset)) {
/*  379: 623 */       return false;
/*  380:     */     }
/*  381: 625 */     setInsideMultiLineComment(true);
/*  382: 626 */     return true;
/*  383:     */   }
/*  384:     */   
/*  385:     */   protected boolean endingMultiLineComment(String content, int startOffset, int endOffset)
/*  386:     */     throws BadLocationException
/*  387:     */   {
/*  388: 641 */     if (!getMultiLineComment()) {
/*  389: 642 */       return false;
/*  390:     */     }
/*  391: 645 */     int index = indexOf(content, getMultiLineCommentEnd(), startOffset);
/*  392: 647 */     if ((index < 0) || (index > endOffset)) {
/*  393: 648 */       return false;
/*  394:     */     }
/*  395: 650 */     setInsideMultiLineComment(false);
/*  396: 651 */     return true;
/*  397:     */   }
/*  398:     */   
/*  399:     */   protected boolean isMultiLineComment()
/*  400:     */   {
/*  401: 662 */     return this.m_InsideMultiLineComment;
/*  402:     */   }
/*  403:     */   
/*  404:     */   protected void setInsideMultiLineComment(boolean value)
/*  405:     */   {
/*  406: 671 */     this.m_InsideMultiLineComment = value;
/*  407:     */   }
/*  408:     */   
/*  409:     */   protected void checkForTokens(String content, int startOffset, int endOffset)
/*  410:     */   {
/*  411: 682 */     while (startOffset <= endOffset)
/*  412:     */     {
/*  413: 685 */       while (isDelimiter(content.substring(startOffset, startOffset + 1))) {
/*  414: 686 */         if (startOffset < endOffset) {
/*  415: 687 */           startOffset++;
/*  416:     */         } else {
/*  417: 689 */           return;
/*  418:     */         }
/*  419:     */       }
/*  420: 695 */       if (isQuoteDelimiter(content.substring(startOffset, startOffset + 1))) {
/*  421: 696 */         startOffset = getQuoteToken(content, startOffset, endOffset);
/*  422:     */       } else {
/*  423: 698 */         startOffset = getOtherToken(content, startOffset, endOffset);
/*  424:     */       }
/*  425:     */     }
/*  426:     */   }
/*  427:     */   
/*  428:     */   protected int getQuoteToken(String content, int startOffset, int endOffset)
/*  429:     */   {
/*  430: 712 */     String quoteDelimiter = content.substring(startOffset, startOffset + 1);
/*  431: 713 */     String escapeString = escapeQuote(quoteDelimiter);
/*  432:     */     
/*  433:     */ 
/*  434: 716 */     int endOfQuote = startOffset;
/*  435:     */     
/*  436:     */ 
/*  437:     */ 
/*  438: 720 */     int index = content.indexOf(escapeString, endOfQuote + 1);
/*  439: 722 */     while ((index > -1) && (index < endOffset))
/*  440:     */     {
/*  441: 723 */       endOfQuote = index + 1;
/*  442: 724 */       index = content.indexOf(escapeString, endOfQuote);
/*  443:     */     }
/*  444: 729 */     index = content.indexOf(quoteDelimiter, endOfQuote + 1);
/*  445: 731 */     if ((index < 0) || (index > endOffset)) {
/*  446: 732 */       endOfQuote = endOffset;
/*  447:     */     } else {
/*  448: 734 */       endOfQuote = index;
/*  449:     */     }
/*  450: 737 */     this.m_Self.setCharacterAttributes(startOffset, endOfQuote - startOffset + 1, DEFAULT_STRING, false);
/*  451:     */     
/*  452:     */ 
/*  453: 740 */     return endOfQuote + 1;
/*  454:     */   }
/*  455:     */   
/*  456:     */   protected int getOtherToken(String content, int startOffset, int endOffset)
/*  457:     */   {
/*  458: 752 */     int endOfToken = startOffset + 1;
/*  459: 754 */     while ((endOfToken <= endOffset) && 
/*  460: 755 */       (!isDelimiter(content.substring(endOfToken, endOfToken + 1)))) {
/*  461: 758 */       endOfToken++;
/*  462:     */     }
/*  463: 761 */     String token = content.substring(startOffset, endOfToken);
/*  464:     */     
/*  465:     */ 
/*  466: 764 */     MutableAttributeSet attr = getKeywordFormatting(token);
/*  467: 765 */     if (attr != null) {
/*  468: 766 */       this.m_Self.setCharacterAttributes(startOffset, endOfToken - startOffset, attr, false);
/*  469:     */     }
/*  470: 770 */     return endOfToken + 1;
/*  471:     */   }
/*  472:     */   
/*  473:     */   protected int indexOf(String content, String needle, int offset)
/*  474:     */   {
/*  475:     */     int index;
/*  476: 784 */     while ((index = content.indexOf(needle, offset)) != -1)
/*  477:     */     {
/*  478: 785 */       String text = getLine(content, index).trim();
/*  479: 787 */       if ((text.startsWith(needle)) || (text.endsWith(needle))) {
/*  480:     */         break;
/*  481:     */       }
/*  482: 790 */       offset = index + 1;
/*  483:     */     }
/*  484: 794 */     return index;
/*  485:     */   }
/*  486:     */   
/*  487:     */   protected int lastIndexOf(String content, String needle, int offset)
/*  488:     */   {
/*  489:     */     int index;
/*  490: 808 */     while ((index = content.lastIndexOf(needle, offset)) != -1)
/*  491:     */     {
/*  492: 809 */       String text = getLine(content, index).trim();
/*  493: 811 */       if ((text.startsWith(needle)) || (text.endsWith(needle))) {
/*  494:     */         break;
/*  495:     */       }
/*  496: 814 */       offset = index - 1;
/*  497:     */     }
/*  498: 818 */     return index;
/*  499:     */   }
/*  500:     */   
/*  501:     */   protected String getLine(String content, int offset)
/*  502:     */   {
/*  503: 829 */     int line = this.m_RootElement.getElementIndex(offset);
/*  504: 830 */     Element lineElement = this.m_RootElement.getElement(line);
/*  505: 831 */     int start = lineElement.getStartOffset();
/*  506: 832 */     int end = lineElement.getEndOffset();
/*  507: 833 */     return content.substring(start, end - 1);
/*  508:     */   }
/*  509:     */   
/*  510:     */   public boolean isDelimiter(String character)
/*  511:     */   {
/*  512: 843 */     return (Character.isWhitespace(character.charAt(0))) || (this.m_Delimiters.indexOf(character.charAt(0)) > -1);
/*  513:     */   }
/*  514:     */   
/*  515:     */   public boolean isQuoteDelimiter(String character)
/*  516:     */   {
/*  517: 854 */     return this.m_QuoteDelimiters.indexOf(character.charAt(0)) > -1;
/*  518:     */   }
/*  519:     */   
/*  520:     */   public String escapeQuote(String quoteDelimiter)
/*  521:     */   {
/*  522: 864 */     return this.m_QuoteEscape + quoteDelimiter;
/*  523:     */   }
/*  524:     */   
/*  525:     */   protected String addMatchingBlockEnd(int offset)
/*  526:     */     throws BadLocationException
/*  527:     */   {
/*  528: 876 */     StringBuffer whiteSpace = new StringBuffer();
/*  529: 877 */     int line = this.m_RootElement.getElementIndex(offset);
/*  530: 878 */     int i = this.m_RootElement.getElement(line).getStartOffset();
/*  531:     */     for (;;)
/*  532:     */     {
/*  533: 881 */       String temp = this.m_Self.getText(i, 1);
/*  534: 883 */       if ((!temp.equals(" ")) && (!temp.equals("\t"))) {
/*  535:     */         break;
/*  536:     */       }
/*  537: 884 */       whiteSpace.append(temp);
/*  538: 885 */       i++;
/*  539:     */     }
/*  540: 892 */     StringBuffer result = new StringBuffer();
/*  541: 893 */     result.append(this.m_BlockStart);
/*  542: 894 */     result.append("\n");
/*  543: 895 */     result.append(whiteSpace.toString());
/*  544: 896 */     if (this.m_UseBlanks) {
/*  545: 897 */       result.append(this.m_Indentation);
/*  546:     */     } else {
/*  547: 899 */       result.append("\t");
/*  548:     */     }
/*  549: 901 */     result.append("\n");
/*  550: 902 */     result.append(whiteSpace.toString());
/*  551: 903 */     result.append(this.m_BlockEnd);
/*  552:     */     
/*  553: 905 */     return result.toString();
/*  554:     */   }
/*  555:     */   
/*  556:     */   public int getFontSize()
/*  557:     */   {
/*  558: 914 */     return this.m_FontSize;
/*  559:     */   }
/*  560:     */   
/*  561:     */   public void setFontSize(int fontSize)
/*  562:     */   {
/*  563: 923 */     this.m_FontSize = fontSize;
/*  564: 924 */     StyleConstants.setFontSize(DEFAULT_NORMAL, fontSize);
/*  565: 925 */     StyleConstants.setFontSize(DEFAULT_STRING, fontSize);
/*  566: 926 */     StyleConstants.setFontSize(DEFAULT_COMMENT, fontSize);
/*  567:     */   }
/*  568:     */   
/*  569:     */   public String getFontName()
/*  570:     */   {
/*  571: 935 */     return this.m_FontName;
/*  572:     */   }
/*  573:     */   
/*  574:     */   public void setFontName(String fontName)
/*  575:     */   {
/*  576: 944 */     this.m_FontName = fontName;
/*  577: 945 */     StyleConstants.setFontFamily(DEFAULT_NORMAL, fontName);
/*  578: 946 */     StyleConstants.setFontFamily(DEFAULT_STRING, fontName);
/*  579: 947 */     StyleConstants.setFontFamily(DEFAULT_COMMENT, fontName);
/*  580:     */   }
/*  581:     */   
/*  582:     */   public void setIndentationSize(int value)
/*  583:     */   {
/*  584: 958 */     this.m_Indentation = "";
/*  585: 959 */     for (int i = 0; i < value; i++) {
/*  586: 960 */       this.m_Indentation += " ";
/*  587:     */     }
/*  588:     */   }
/*  589:     */   
/*  590:     */   public int getIndentationSize()
/*  591:     */   {
/*  592: 970 */     return this.m_Indentation.length();
/*  593:     */   }
/*  594:     */   
/*  595:     */   public void setDelimiters(String value)
/*  596:     */   {
/*  597: 979 */     this.m_Delimiters = value;
/*  598:     */   }
/*  599:     */   
/*  600:     */   public String getDelimiters()
/*  601:     */   {
/*  602: 988 */     return this.m_Delimiters;
/*  603:     */   }
/*  604:     */   
/*  605:     */   public void setQuoteDelimiters(String value)
/*  606:     */   {
/*  607: 997 */     this.m_QuoteDelimiters = value;
/*  608:     */   }
/*  609:     */   
/*  610:     */   public String getQuoteDelimiters()
/*  611:     */   {
/*  612:1006 */     return this.m_QuoteDelimiters;
/*  613:     */   }
/*  614:     */   
/*  615:     */   public void setQuoteEscape(String value)
/*  616:     */   {
/*  617:1015 */     this.m_QuoteEscape = value;
/*  618:     */   }
/*  619:     */   
/*  620:     */   public String getQuoteEscape()
/*  621:     */   {
/*  622:1024 */     return this.m_QuoteEscape;
/*  623:     */   }
/*  624:     */   
/*  625:     */   public void setSingleLineCommentStart(String value)
/*  626:     */   {
/*  627:1033 */     this.m_SingleLineCommentStart = value;
/*  628:     */   }
/*  629:     */   
/*  630:     */   public String getSingleLineCommentStart()
/*  631:     */   {
/*  632:1042 */     return this.m_SingleLineCommentStart;
/*  633:     */   }
/*  634:     */   
/*  635:     */   public void setMultiLineCommentStart(String value)
/*  636:     */   {
/*  637:1051 */     this.m_MultiLineCommentStart = value;
/*  638:     */   }
/*  639:     */   
/*  640:     */   public String getMultiLineCommentStart()
/*  641:     */   {
/*  642:1060 */     return this.m_MultiLineCommentStart;
/*  643:     */   }
/*  644:     */   
/*  645:     */   public void setMultiLineCommentEnd(String value)
/*  646:     */   {
/*  647:1069 */     this.m_MultiLineCommentEnd = value;
/*  648:     */   }
/*  649:     */   
/*  650:     */   public String getMultiLineCommentEnd()
/*  651:     */   {
/*  652:1078 */     return this.m_MultiLineCommentEnd;
/*  653:     */   }
/*  654:     */   
/*  655:     */   public void setBlockStart(String value)
/*  656:     */   {
/*  657:1087 */     this.m_BlockStart = value;
/*  658:     */   }
/*  659:     */   
/*  660:     */   public String getBlockStart()
/*  661:     */   {
/*  662:1096 */     return this.m_BlockStart;
/*  663:     */   }
/*  664:     */   
/*  665:     */   public void setBlockEnd(String value)
/*  666:     */   {
/*  667:1105 */     this.m_BlockEnd = value;
/*  668:     */   }
/*  669:     */   
/*  670:     */   public String getBlockEnd()
/*  671:     */   {
/*  672:1114 */     return this.m_BlockEnd;
/*  673:     */   }
/*  674:     */   
/*  675:     */   public void setAddMatchingEndBlocks(boolean value)
/*  676:     */   {
/*  677:1123 */     this.m_AddMatchingEndBlocks = value;
/*  678:     */   }
/*  679:     */   
/*  680:     */   public boolean getAddMatchingEndBlocks()
/*  681:     */   {
/*  682:1132 */     return this.m_AddMatchingEndBlocks;
/*  683:     */   }
/*  684:     */   
/*  685:     */   public void setUseBlanks(boolean value)
/*  686:     */   {
/*  687:1141 */     this.m_UseBlanks = value;
/*  688:     */   }
/*  689:     */   
/*  690:     */   public boolean getUseBlanks()
/*  691:     */   {
/*  692:1150 */     return this.m_UseBlanks;
/*  693:     */   }
/*  694:     */   
/*  695:     */   public void setBackgroundColor(Color value)
/*  696:     */   {
/*  697:1159 */     this.m_BackgroundColor = value;
/*  698:     */   }
/*  699:     */   
/*  700:     */   public Color getBackgroundColor()
/*  701:     */   {
/*  702:1168 */     return this.m_BackgroundColor;
/*  703:     */   }
/*  704:     */   
/*  705:     */   public void setMultiLineComment(boolean value)
/*  706:     */   {
/*  707:1177 */     this.m_MultiLineComment = value;
/*  708:     */   }
/*  709:     */   
/*  710:     */   public boolean getMultiLineComment()
/*  711:     */   {
/*  712:1186 */     return this.m_MultiLineComment;
/*  713:     */   }
/*  714:     */   
/*  715:     */   public void setCaseSensitive(boolean value)
/*  716:     */   {
/*  717:1195 */     this.m_CaseSensitive = value;
/*  718:     */   }
/*  719:     */   
/*  720:     */   public boolean getCaseSensitive()
/*  721:     */   {
/*  722:1204 */     return this.m_CaseSensitive;
/*  723:     */   }
/*  724:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.scripting.SyntaxDocument
 * JD-Core Version:    0.7.0.1
 */