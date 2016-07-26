/*   1:    */ package jsyntaxpane;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.Collections;
/*   5:    */ import java.util.Iterator;
/*   6:    */ import java.util.List;
/*   7:    */ import java.util.ListIterator;
/*   8:    */ import java.util.logging.Level;
/*   9:    */ import java.util.logging.Logger;
/*  10:    */ import java.util.regex.Matcher;
/*  11:    */ import java.util.regex.Pattern;
/*  12:    */ import javax.swing.event.DocumentEvent;
/*  13:    */ import javax.swing.text.AttributeSet;
/*  14:    */ import javax.swing.text.BadLocationException;
/*  15:    */ import javax.swing.text.Element;
/*  16:    */ import javax.swing.text.PlainDocument;
/*  17:    */ import javax.swing.text.Segment;
/*  18:    */ 
/*  19:    */ public class SyntaxDocument
/*  20:    */   extends PlainDocument
/*  21:    */ {
/*  22:    */   Lexer lexer;
/*  23:    */   List<Token> tokens;
/*  24:    */   CompoundUndoMan undo;
/*  25:    */   
/*  26:    */   public SyntaxDocument(Lexer lexer)
/*  27:    */   {
/*  28: 47 */     putProperty("tabSize", Integer.valueOf(4));
/*  29: 48 */     this.lexer = lexer;
/*  30:    */     
/*  31: 50 */     this.undo = new CompoundUndoMan(this);
/*  32:    */   }
/*  33:    */   
/*  34:    */   private void parse()
/*  35:    */   {
/*  36: 61 */     if (this.lexer == null)
/*  37:    */     {
/*  38: 62 */       this.tokens = null;
/*  39: 63 */       return;
/*  40:    */     }
/*  41: 65 */     List<Token> toks = new ArrayList(getLength() / 10);
/*  42: 66 */     long ts = System.nanoTime();
/*  43: 67 */     int len = getLength();
/*  44:    */     try
/*  45:    */     {
/*  46: 69 */       Segment seg = new Segment();
/*  47: 70 */       getText(0, getLength(), seg);
/*  48: 71 */       this.lexer.parse(seg, 0, toks);
/*  49:    */     }
/*  50:    */     catch (BadLocationException ex)
/*  51:    */     {
/*  52: 73 */       log.log(Level.SEVERE, null, ex);
/*  53:    */     }
/*  54:    */     finally
/*  55:    */     {
/*  56: 75 */       if (log.isLoggable(Level.FINEST)) {
/*  57: 76 */         log.finest(String.format("Parsed %d in %d ms, giving %d tokens\n", new Object[] { Integer.valueOf(len), Long.valueOf((System.nanoTime() - ts) / 1000000L), Integer.valueOf(toks.size()) }));
/*  58:    */       }
/*  59: 79 */       this.tokens = toks;
/*  60:    */     }
/*  61:    */   }
/*  62:    */   
/*  63:    */   protected void fireChangedUpdate(DocumentEvent e)
/*  64:    */   {
/*  65: 85 */     parse();
/*  66: 86 */     super.fireChangedUpdate(e);
/*  67:    */   }
/*  68:    */   
/*  69:    */   protected void fireInsertUpdate(DocumentEvent e)
/*  70:    */   {
/*  71: 91 */     parse();
/*  72: 92 */     super.fireInsertUpdate(e);
/*  73:    */   }
/*  74:    */   
/*  75:    */   protected void fireRemoveUpdate(DocumentEvent e)
/*  76:    */   {
/*  77: 97 */     parse();
/*  78: 98 */     super.fireRemoveUpdate(e);
/*  79:    */   }
/*  80:    */   
/*  81:    */   public void replaceToken(Token token, String replacement)
/*  82:    */   {
/*  83:    */     try
/*  84:    */     {
/*  85:108 */       replace(token.start, token.length, replacement, null);
/*  86:    */     }
/*  87:    */     catch (BadLocationException ex)
/*  88:    */     {
/*  89:110 */       log.log(Level.WARNING, "unable to replace token: " + token, ex);
/*  90:    */     }
/*  91:    */   }
/*  92:    */   
/*  93:    */   class TokenIterator
/*  94:    */     implements ListIterator<Token>
/*  95:    */   {
/*  96:    */     int start;
/*  97:    */     int end;
/*  98:122 */     int ndx = 0;
/*  99:    */     
/* 100:    */     private TokenIterator(int start, int end)
/* 101:    */     {
/* 102:126 */       this.start = start;
/* 103:127 */       this.end = end;
/* 104:    */       Token token;
/* 105:128 */       if ((SyntaxDocument.this.tokens != null) && (!SyntaxDocument.this.tokens.isEmpty()))
/* 106:    */       {
/* 107:129 */         token = new Token(TokenType.COMMENT, start, end - start);
/* 108:130 */         this.ndx = Collections.binarySearch(SyntaxDocument.this.tokens, token);
/* 109:132 */         if (this.ndx < 0)
/* 110:    */         {
/* 111:135 */           this.ndx = (-this.ndx - 1 - 1 < 0 ? 0 : -this.ndx - 1 - 1);
/* 112:136 */           Token t = (Token)SyntaxDocument.this.tokens.get(this.ndx);
/* 113:138 */           if (t.end() <= start) {
/* 114:139 */             this.ndx += 1;
/* 115:    */           }
/* 116:    */         }
/* 117:    */       }
/* 118:    */     }
/* 119:    */     
/* 120:    */     public boolean hasNext()
/* 121:    */     {
/* 122:148 */       if (SyntaxDocument.this.tokens == null) {
/* 123:149 */         return false;
/* 124:    */       }
/* 125:151 */       if (this.ndx >= SyntaxDocument.this.tokens.size()) {
/* 126:152 */         return false;
/* 127:    */       }
/* 128:154 */       Token t = (Token)SyntaxDocument.this.tokens.get(this.ndx);
/* 129:155 */       if (t.start >= this.end) {
/* 130:156 */         return false;
/* 131:    */       }
/* 132:158 */       return true;
/* 133:    */     }
/* 134:    */     
/* 135:    */     public Token next()
/* 136:    */     {
/* 137:163 */       return (Token)SyntaxDocument.this.tokens.get(this.ndx++);
/* 138:    */     }
/* 139:    */     
/* 140:    */     public void remove()
/* 141:    */     {
/* 142:168 */       throw new UnsupportedOperationException();
/* 143:    */     }
/* 144:    */     
/* 145:    */     public boolean hasPrevious()
/* 146:    */     {
/* 147:173 */       if (SyntaxDocument.this.tokens == null) {
/* 148:174 */         return false;
/* 149:    */       }
/* 150:176 */       if (this.ndx <= 0) {
/* 151:177 */         return false;
/* 152:    */       }
/* 153:179 */       Token t = (Token)SyntaxDocument.this.tokens.get(this.ndx);
/* 154:180 */       if (t.end() <= this.start) {
/* 155:181 */         return false;
/* 156:    */       }
/* 157:183 */       return true;
/* 158:    */     }
/* 159:    */     
/* 160:    */     public Token previous()
/* 161:    */     {
/* 162:188 */       return (Token)SyntaxDocument.this.tokens.get(this.ndx--);
/* 163:    */     }
/* 164:    */     
/* 165:    */     public int nextIndex()
/* 166:    */     {
/* 167:193 */       return this.ndx + 1;
/* 168:    */     }
/* 169:    */     
/* 170:    */     public int previousIndex()
/* 171:    */     {
/* 172:198 */       return this.ndx - 1;
/* 173:    */     }
/* 174:    */     
/* 175:    */     public void set(Token e)
/* 176:    */     {
/* 177:203 */       throw new UnsupportedOperationException();
/* 178:    */     }
/* 179:    */     
/* 180:    */     public void add(Token e)
/* 181:    */     {
/* 182:208 */       throw new UnsupportedOperationException();
/* 183:    */     }
/* 184:    */   }
/* 185:    */   
/* 186:    */   public Iterator<Token> getTokens(int start, int end)
/* 187:    */   {
/* 188:219 */     return new TokenIterator(start, end, null);
/* 189:    */   }
/* 190:    */   
/* 191:    */   public Token getTokenAt(int pos)
/* 192:    */   {
/* 193:229 */     if ((this.tokens == null) || (this.tokens.isEmpty()) || (pos > getLength())) {
/* 194:230 */       return null;
/* 195:    */     }
/* 196:232 */     Token tok = null;
/* 197:233 */     Token tKey = new Token(TokenType.DEFAULT, pos, 1);
/* 198:    */     
/* 199:235 */     int ndx = Collections.binarySearch(this.tokens, tKey);
/* 200:236 */     if (ndx < 0)
/* 201:    */     {
/* 202:239 */       ndx = -ndx - 1 - 1 < 0 ? 0 : -ndx - 1 - 1;
/* 203:240 */       Token t = (Token)this.tokens.get(ndx);
/* 204:241 */       if ((t.start <= pos) && (pos <= t.end())) {
/* 205:242 */         tok = t;
/* 206:    */       }
/* 207:    */     }
/* 208:    */     else
/* 209:    */     {
/* 210:245 */       tok = (Token)this.tokens.get(ndx);
/* 211:    */     }
/* 212:247 */     return tok;
/* 213:    */   }
/* 214:    */   
/* 215:    */   public Token getWordAt(int offs, Pattern p)
/* 216:    */   {
/* 217:251 */     Token word = null;
/* 218:    */     try
/* 219:    */     {
/* 220:253 */       Element line = getParagraphElement(offs);
/* 221:254 */       if (line == null)
/* 222:    */       {
/* 223:255 */         Token localToken1 = word;
/* 224:    */         
/* 225:    */ 
/* 226:    */ 
/* 227:    */ 
/* 228:    */ 
/* 229:    */ 
/* 230:    */ 
/* 231:    */ 
/* 232:    */ 
/* 233:    */ 
/* 234:    */ 
/* 235:    */ 
/* 236:    */ 
/* 237:    */ 
/* 238:    */ 
/* 239:    */ 
/* 240:    */ 
/* 241:    */ 
/* 242:    */ 
/* 243:275 */         return word;
/* 244:    */       }
/* 245:257 */       int lineStart = line.getStartOffset();
/* 246:258 */       int lineEnd = Math.min(line.getEndOffset(), getLength());
/* 247:259 */       Segment seg = new Segment();
/* 248:260 */       getText(lineStart, lineEnd - lineStart, seg);
/* 249:261 */       if (seg.count > 0)
/* 250:    */       {
/* 251:263 */         Matcher m = p.matcher(seg);
/* 252:264 */         int o = offs - lineStart;
/* 253:265 */         while (m.find()) {
/* 254:266 */           if ((m.start() <= o) && (o <= m.end())) {
/* 255:267 */             word = new Token(TokenType.DEFAULT, m.start() + lineStart, m.end() - m.start());
/* 256:    */           }
/* 257:    */         }
/* 258:    */       }
/* 259:275 */       return word;
/* 260:    */     }
/* 261:    */     catch (BadLocationException ex)
/* 262:    */     {
/* 263:272 */       ex = 
/* 264:    */       
/* 265:    */ 
/* 266:275 */         ex;Logger.getLogger(SyntaxDocument.class.getName()).log(Level.SEVERE, null, ex);return word;
/* 267:    */     }
/* 268:    */     finally {}
/* 269:275 */     return word;
/* 270:    */   }
/* 271:    */   
/* 272:    */   public Token getNextToken(Token tok)
/* 273:    */   {
/* 274:286 */     int n = this.tokens.indexOf(tok);
/* 275:287 */     if ((n >= 0) && (n < this.tokens.size() - 1)) {
/* 276:288 */       return (Token)this.tokens.get(n + 1);
/* 277:    */     }
/* 278:290 */     return null;
/* 279:    */   }
/* 280:    */   
/* 281:    */   public Token getPrevToken(Token tok)
/* 282:    */   {
/* 283:301 */     int n = this.tokens.indexOf(tok);
/* 284:302 */     if ((n > 0) && (!this.tokens.isEmpty())) {
/* 285:303 */       return (Token)this.tokens.get(n - 1);
/* 286:    */     }
/* 287:305 */     return null;
/* 288:    */   }
/* 289:    */   
/* 290:    */   public Token getPairFor(Token t)
/* 291:    */   {
/* 292:320 */     if ((t == null) || (t.pairValue == 0)) {
/* 293:321 */       return null;
/* 294:    */     }
/* 295:323 */     Token p = null;
/* 296:324 */     int ndx = this.tokens.indexOf(t);
/* 297:    */     
/* 298:    */ 
/* 299:327 */     int w = t.pairValue;
/* 300:328 */     int direction = t.pairValue > 0 ? 1 : -1;
/* 301:329 */     boolean done = false;
/* 302:330 */     int v = Math.abs(t.pairValue);
/* 303:331 */     while (!done)
/* 304:    */     {
/* 305:332 */       ndx += direction;
/* 306:333 */       if ((ndx < 0) || (ndx >= this.tokens.size())) {
/* 307:    */         break;
/* 308:    */       }
/* 309:336 */       Token current = (Token)this.tokens.get(ndx);
/* 310:337 */       if (Math.abs(current.pairValue) == v)
/* 311:    */       {
/* 312:338 */         w += current.pairValue;
/* 313:339 */         if (w == 0)
/* 314:    */         {
/* 315:340 */           p = current;
/* 316:341 */           done = true;
/* 317:    */         }
/* 318:    */       }
/* 319:    */     }
/* 320:346 */     return p;
/* 321:    */   }
/* 322:    */   
/* 323:    */   public void doUndo()
/* 324:    */   {
/* 325:353 */     if (this.undo.canUndo())
/* 326:    */     {
/* 327:354 */       this.undo.undo();
/* 328:355 */       parse();
/* 329:    */     }
/* 330:    */   }
/* 331:    */   
/* 332:    */   public void doRedo()
/* 333:    */   {
/* 334:363 */     if (this.undo.canRedo())
/* 335:    */     {
/* 336:364 */       this.undo.redo();
/* 337:365 */       parse();
/* 338:    */     }
/* 339:    */   }
/* 340:    */   
/* 341:    */   public Matcher getMatcher(Pattern pattern)
/* 342:    */   {
/* 343:375 */     return getMatcher(pattern, 0, getLength());
/* 344:    */   }
/* 345:    */   
/* 346:    */   public Matcher getMatcher(Pattern pattern, int start)
/* 347:    */   {
/* 348:389 */     return getMatcher(pattern, start, getLength() - start);
/* 349:    */   }
/* 350:    */   
/* 351:    */   public Matcher getMatcher(Pattern pattern, int start, int length)
/* 352:    */   {
/* 353:405 */     Matcher matcher = null;
/* 354:406 */     if (getLength() == 0) {
/* 355:407 */       return null;
/* 356:    */     }
/* 357:409 */     if (start >= getLength()) {
/* 358:410 */       return null;
/* 359:    */     }
/* 360:    */     try
/* 361:    */     {
/* 362:413 */       if (start < 0) {
/* 363:414 */         start = 0;
/* 364:    */       }
/* 365:416 */       if (start + length > getLength()) {
/* 366:417 */         length = getLength() - start;
/* 367:    */       }
/* 368:419 */       Segment seg = new Segment();
/* 369:420 */       getText(start, length, seg);
/* 370:421 */       matcher = pattern.matcher(seg);
/* 371:    */     }
/* 372:    */     catch (BadLocationException ex)
/* 373:    */     {
/* 374:423 */       log.log(Level.SEVERE, "Requested offset: " + ex.offsetRequested(), ex);
/* 375:    */     }
/* 376:425 */     return matcher;
/* 377:    */   }
/* 378:    */   
/* 379:    */   public void clearUndos()
/* 380:    */   {
/* 381:432 */     this.undo.discardAllEdits();
/* 382:    */   }
/* 383:    */   
/* 384:    */   public String getLineAt(int pos)
/* 385:    */     throws BadLocationException
/* 386:    */   {
/* 387:443 */     Element e = getParagraphElement(pos);
/* 388:444 */     Segment seg = new Segment();
/* 389:445 */     getText(e.getStartOffset(), e.getEndOffset() - e.getStartOffset(), seg);
/* 390:446 */     char last = seg.last();
/* 391:447 */     if ((last == '\n') || (last == '\r')) {
/* 392:448 */       seg.count -= 1;
/* 393:    */     }
/* 394:450 */     return seg.toString();
/* 395:    */   }
/* 396:    */   
/* 397:    */   public void removeLineAt(int pos)
/* 398:    */     throws BadLocationException
/* 399:    */   {
/* 400:460 */     Element e = getParagraphElement(pos);
/* 401:461 */     remove(e.getStartOffset(), getElementLength(e));
/* 402:    */   }
/* 403:    */   
/* 404:    */   public void replaceLineAt(int pos, String newLines)
/* 405:    */     throws BadLocationException
/* 406:    */   {
/* 407:473 */     Element e = getParagraphElement(pos);
/* 408:474 */     replace(e.getStartOffset(), getElementLength(e), newLines, null);
/* 409:    */   }
/* 410:    */   
/* 411:    */   private int getElementLength(Element e)
/* 412:    */   {
/* 413:484 */     int end = e.getEndOffset();
/* 414:485 */     if (end >= getLength() - 1) {
/* 415:486 */       end--;
/* 416:    */     }
/* 417:488 */     return end - e.getStartOffset();
/* 418:    */   }
/* 419:    */   
/* 420:    */   public synchronized String getUncommentedText(int aStart, int anEnd)
/* 421:    */   {
/* 422:499 */     readLock();
/* 423:500 */     StringBuilder result = new StringBuilder();
/* 424:501 */     Iterator<Token> iter = getTokens(aStart, anEnd);
/* 425:502 */     while (iter.hasNext())
/* 426:    */     {
/* 427:503 */       Token t = (Token)iter.next();
/* 428:504 */       if (!TokenType.isComment(t)) {
/* 429:505 */         result.append(t.getText(this));
/* 430:    */       }
/* 431:    */     }
/* 432:508 */     readUnlock();
/* 433:509 */     return result.toString();
/* 434:    */   }
/* 435:    */   
/* 436:    */   public int getLineStartOffset(int pos)
/* 437:    */   {
/* 438:518 */     return getParagraphElement(pos).getStartOffset();
/* 439:    */   }
/* 440:    */   
/* 441:    */   public int getLineEndOffset(int pos)
/* 442:    */   {
/* 443:529 */     int end = 0;
/* 444:530 */     end = getParagraphElement(pos).getEndOffset();
/* 445:531 */     if (end >= getLength()) {
/* 446:532 */       end = getLength();
/* 447:    */     }
/* 448:534 */     return end;
/* 449:    */   }
/* 450:    */   
/* 451:    */   public int getLineCount()
/* 452:    */   {
/* 453:542 */     Element e = getDefaultRootElement();
/* 454:543 */     int cnt = e.getElementCount();
/* 455:544 */     return cnt;
/* 456:    */   }
/* 457:    */   
/* 458:    */   public int getLineNumberAt(int pos)
/* 459:    */   {
/* 460:553 */     int lineNr = getDefaultRootElement().getElementIndex(pos);
/* 461:554 */     return lineNr;
/* 462:    */   }
/* 463:    */   
/* 464:    */   public String toString()
/* 465:    */   {
/* 466:559 */     return "SyntaxDocument(" + this.lexer + ", " + (this.tokens == null ? 0 : this.tokens.size()) + " tokens)@" + hashCode();
/* 467:    */   }
/* 468:    */   
/* 469:    */   public void replace(int offset, int length, String text, AttributeSet attrs)
/* 470:    */     throws BadLocationException
/* 471:    */   {
/* 472:574 */     remove(offset, length);
/* 473:575 */     this.undo.startCombine();
/* 474:576 */     insertString(offset, text, attrs);
/* 475:    */   }
/* 476:    */   
/* 477:580 */   private static final Logger log = Logger.getLogger(SyntaxDocument.class.getName());
/* 478:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.SyntaxDocument
 * JD-Core Version:    0.7.0.1
 */