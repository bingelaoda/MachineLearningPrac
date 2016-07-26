/*   1:    */ package jsyntaxpane.actions;
/*   2:    */ 
/*   3:    */ import java.awt.Component;
/*   4:    */ import java.awt.FontMetrics;
/*   5:    */ import java.awt.Frame;
/*   6:    */ import java.awt.Point;
/*   7:    */ import java.awt.Rectangle;
/*   8:    */ import java.awt.Window;
/*   9:    */ import java.awt.event.KeyEvent;
/*  10:    */ import java.util.logging.Level;
/*  11:    */ import java.util.logging.Logger;
/*  12:    */ import java.util.regex.Matcher;
/*  13:    */ import java.util.regex.Pattern;
/*  14:    */ import javax.swing.Action;
/*  15:    */ import javax.swing.ActionMap;
/*  16:    */ import javax.swing.JComboBox;
/*  17:    */ import javax.swing.JEditorPane;
/*  18:    */ import javax.swing.MutableComboBoxModel;
/*  19:    */ import javax.swing.SwingUtilities;
/*  20:    */ import javax.swing.text.BadLocationException;
/*  21:    */ import javax.swing.text.Document;
/*  22:    */ import javax.swing.text.EditorKit;
/*  23:    */ import javax.swing.text.Element;
/*  24:    */ import javax.swing.text.JTextComponent;
/*  25:    */ import javax.swing.text.PlainDocument;
/*  26:    */ import jsyntaxpane.DefaultSyntaxKit;
/*  27:    */ import jsyntaxpane.SyntaxDocument;
/*  28:    */ import jsyntaxpane.Token;
/*  29:    */ 
/*  30:    */ public class ActionUtils
/*  31:    */ {
/*  32: 52 */   private static ActionUtils instance = null;
/*  33:    */   
/*  34:    */   public static synchronized ActionUtils getInstance()
/*  35:    */   {
/*  36: 59 */     if (instance == null) {
/*  37: 60 */       instance = new ActionUtils();
/*  38:    */     }
/*  39: 62 */     return instance;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public static String getIndent(String line)
/*  43:    */   {
/*  44: 72 */     if ((line == null) || (line.length() == 0)) {
/*  45: 73 */       return "";
/*  46:    */     }
/*  47: 75 */     int i = 0;
/*  48: 76 */     while ((i < line.length()) && (line.charAt(i) == ' ')) {
/*  49: 77 */       i++;
/*  50:    */     }
/*  51: 79 */     return line.substring(0, i);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public static String[] getSelectedLines(JTextComponent target)
/*  55:    */   {
/*  56: 95 */     String[] lines = null;
/*  57:    */     try
/*  58:    */     {
/*  59: 97 */       PlainDocument pDoc = (PlainDocument)target.getDocument();
/*  60: 98 */       int start = pDoc.getParagraphElement(target.getSelectionStart()).getStartOffset();
/*  61:    */       int end;
/*  62:    */       int end;
/*  63:100 */       if (target.getSelectionStart() == target.getSelectionEnd()) {
/*  64:101 */         end = pDoc.getParagraphElement(target.getSelectionEnd()).getEndOffset();
/*  65:    */       } else {
/*  66:105 */         end = pDoc.getParagraphElement(target.getSelectionEnd() - 1).getEndOffset();
/*  67:    */       }
/*  68:107 */       target.select(start, end);
/*  69:108 */       lines = pDoc.getText(start, end - start).split("\n");
/*  70:109 */       target.select(start, end);
/*  71:    */     }
/*  72:    */     catch (BadLocationException ex)
/*  73:    */     {
/*  74:111 */       Logger.getLogger(ActionUtils.class.getName()).log(Level.SEVERE, null, ex);
/*  75:112 */       lines = EMPTY_STRING_ARRAY;
/*  76:    */     }
/*  77:114 */     return lines;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public static String getLine(JTextComponent target)
/*  81:    */   {
/*  82:123 */     return getLineAt(target, target.getCaretPosition());
/*  83:    */   }
/*  84:    */   
/*  85:    */   public static String getLineAt(JTextComponent target, int pos)
/*  86:    */   {
/*  87:134 */     String line = null;
/*  88:135 */     Document doc = target.getDocument();
/*  89:136 */     if ((doc instanceof PlainDocument))
/*  90:    */     {
/*  91:137 */       PlainDocument pDoc = (PlainDocument)doc;
/*  92:138 */       int start = pDoc.getParagraphElement(pos).getStartOffset();
/*  93:139 */       int end = pDoc.getParagraphElement(pos).getEndOffset();
/*  94:    */       try
/*  95:    */       {
/*  96:141 */         line = doc.getText(start, end - start);
/*  97:142 */         if ((line != null) && (line.endsWith("\n"))) {
/*  98:143 */           line = line.substring(0, line.length() - 1);
/*  99:    */         }
/* 100:    */       }
/* 101:    */       catch (BadLocationException ex)
/* 102:    */       {
/* 103:146 */         Logger.getLogger(ActionUtils.class.getName()).log(Level.SEVERE, null, ex);
/* 104:    */       }
/* 105:    */     }
/* 106:149 */     return line;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public static Frame getFrameFor(Component comp)
/* 110:    */   {
/* 111:159 */     Window w = SwingUtilities.getWindowAncestor(comp);
/* 112:160 */     if ((w != null) && ((w instanceof Frame)))
/* 113:    */     {
/* 114:161 */       Frame frame = (Frame)w;
/* 115:162 */       return frame;
/* 116:    */     }
/* 117:164 */     return null;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public static String getTokenStringAt(SyntaxDocument doc, int pos)
/* 121:    */   {
/* 122:175 */     String word = "";
/* 123:176 */     Token t = doc.getTokenAt(pos);
/* 124:177 */     if (t != null) {
/* 125:    */       try
/* 126:    */       {
/* 127:179 */         word = doc.getText(t.start, t.length);
/* 128:    */       }
/* 129:    */       catch (BadLocationException ex)
/* 130:    */       {
/* 131:181 */         Logger.getLogger(ActionUtils.class.getName()).log(Level.SEVERE, null, ex);
/* 132:    */       }
/* 133:    */     }
/* 134:184 */     return word;
/* 135:    */   }
/* 136:    */   
/* 137:    */   public static SyntaxDocument getSyntaxDocument(JTextComponent component)
/* 138:    */   {
/* 139:195 */     if (component == null) {
/* 140:196 */       return null;
/* 141:    */     }
/* 142:198 */     Document doc = component.getDocument();
/* 143:199 */     if ((doc instanceof SyntaxDocument)) {
/* 144:200 */       return (SyntaxDocument)doc;
/* 145:    */     }
/* 146:202 */     return null;
/* 147:    */   }
/* 148:    */   
/* 149:    */   public static int getLineNumber(JTextComponent editor, int pos)
/* 150:    */     throws BadLocationException
/* 151:    */   {
/* 152:216 */     if (getSyntaxDocument(editor) != null)
/* 153:    */     {
/* 154:217 */       SyntaxDocument sdoc = getSyntaxDocument(editor);
/* 155:218 */       return sdoc.getLineNumberAt(pos);
/* 156:    */     }
/* 157:220 */     Document doc = editor.getDocument();
/* 158:221 */     return doc.getDefaultRootElement().getElementIndex(pos);
/* 159:    */   }
/* 160:    */   
/* 161:    */   public static int getColumnNumber(JTextComponent editor, int pos)
/* 162:    */     throws BadLocationException
/* 163:    */   {
/* 164:235 */     Rectangle r = editor.modelToView(pos);
/* 165:236 */     int start = editor.viewToModel(new Point(0, r.y));
/* 166:237 */     int column = pos - start;
/* 167:238 */     return column;
/* 168:    */   }
/* 169:    */   
/* 170:    */   public static int getDocumentPosition(JTextComponent editor, int line, int column)
/* 171:    */   {
/* 172:252 */     int lineHeight = editor.getFontMetrics(editor.getFont()).getHeight();
/* 173:253 */     int charWidth = editor.getFontMetrics(editor.getFont()).charWidth('m');
/* 174:254 */     int y = line * lineHeight;
/* 175:255 */     int x = column * charWidth;
/* 176:256 */     Point pt = new Point(x, y);
/* 177:257 */     int pos = editor.viewToModel(pt);
/* 178:258 */     return pos;
/* 179:    */   }
/* 180:    */   
/* 181:    */   public static int getLineCount(JTextComponent pane)
/* 182:    */   {
/* 183:262 */     SyntaxDocument sdoc = getSyntaxDocument(pane);
/* 184:263 */     if (sdoc != null) {
/* 185:264 */       return sdoc.getLineCount();
/* 186:    */     }
/* 187:266 */     int count = 0;
/* 188:    */     try
/* 189:    */     {
/* 190:268 */       int p = pane.getDocument().getLength() - 1;
/* 191:269 */       if (p > 0) {
/* 192:270 */         count = getLineNumber(pane, p);
/* 193:    */       }
/* 194:    */     }
/* 195:    */     catch (BadLocationException ex)
/* 196:    */     {
/* 197:273 */       Logger.getLogger(ActionUtils.class.getName()).log(Level.SEVERE, null, ex);
/* 198:    */     }
/* 199:275 */     return count;
/* 200:    */   }
/* 201:    */   
/* 202:    */   public static void insertIntoCombo(JComboBox combo, Object item)
/* 203:    */   {
/* 204:286 */     MutableComboBoxModel model = (MutableComboBoxModel)combo.getModel();
/* 205:287 */     if (model.getSize() == 0)
/* 206:    */     {
/* 207:288 */       model.insertElementAt(item, 0);
/* 208:289 */       return;
/* 209:    */     }
/* 210:292 */     Object o = model.getElementAt(0);
/* 211:293 */     if (o.equals(item)) {
/* 212:294 */       return;
/* 213:    */     }
/* 214:296 */     model.removeElement(item);
/* 215:297 */     model.insertElementAt(item, 0);
/* 216:298 */     combo.setSelectedIndex(0);
/* 217:    */   }
/* 218:    */   
/* 219:    */   public static void insertMagicString(JTextComponent target, String result)
/* 220:    */   {
/* 221:    */     try
/* 222:    */     {
/* 223:303 */       insertMagicString(target, target.getCaretPosition(), result);
/* 224:    */     }
/* 225:    */     catch (BadLocationException ex)
/* 226:    */     {
/* 227:305 */       Logger.getLogger(ActionUtils.class.getName()).log(Level.SEVERE, null, ex);
/* 228:    */     }
/* 229:    */   }
/* 230:    */   
/* 231:    */   public static String repeatString(String source, int repeat)
/* 232:    */   {
/* 233:318 */     if (repeat < 0) {
/* 234:319 */       throw new IllegalArgumentException("Cannot repeat " + repeat + " times.");
/* 235:    */     }
/* 236:321 */     if ((repeat == 0) || (source == null) || (source.length() == 0)) {
/* 237:322 */       return "";
/* 238:    */     }
/* 239:324 */     StringBuffer buffer = new StringBuffer();
/* 240:325 */     for (int i = 0; i < repeat; i++) {
/* 241:326 */       buffer.append(source);
/* 242:    */     }
/* 243:328 */     return buffer.toString();
/* 244:    */   }
/* 245:    */   
/* 246:    */   public static boolean isEmptyOrBlanks(String string)
/* 247:    */   {
/* 248:338 */     if ((string == null) || (string.length() == 0)) {
/* 249:339 */       return true;
/* 250:    */     }
/* 251:341 */     for (int i = 0; i < string.length(); i++)
/* 252:    */     {
/* 253:342 */       char c = string.charAt(i);
/* 254:343 */       if (!Character.isWhitespace(c)) {
/* 255:344 */         return false;
/* 256:    */       }
/* 257:    */     }
/* 258:347 */     return true;
/* 259:    */   }
/* 260:    */   
/* 261:    */   public static int getTabSize(JTextComponent text)
/* 262:    */   {
/* 263:357 */     Integer tabs = (Integer)text.getDocument().getProperty("tabSize");
/* 264:358 */     return null == tabs ? 0 : tabs.intValue();
/* 265:    */   }
/* 266:    */   
/* 267:    */   public static void insertMagicString(JTextComponent target, int dot, String toInsert)
/* 268:    */     throws BadLocationException
/* 269:    */   {
/* 270:376 */     Document doc = target.getDocument();
/* 271:377 */     String[] lines = toInsert.split("\n");
/* 272:378 */     if (lines.length > 1)
/* 273:    */     {
/* 274:380 */       String tabToSpaces = getTab(target);
/* 275:381 */       String currentLine = getLineAt(target, dot);
/* 276:382 */       String currentIndent = getIndent(currentLine);
/* 277:383 */       StringBuilder sb = new StringBuilder(toInsert.length());
/* 278:384 */       boolean firstLine = true;
/* 279:385 */       for (String l : lines)
/* 280:    */       {
/* 281:386 */         if (!firstLine) {
/* 282:387 */           sb.append(currentIndent);
/* 283:    */         }
/* 284:389 */         firstLine = false;
/* 285:    */         
/* 286:391 */         sb.append(l.replace("\t", tabToSpaces));
/* 287:392 */         sb.append("\n");
/* 288:    */       }
/* 289:394 */       toInsert = sb.toString();
/* 290:    */     }
/* 291:396 */     if (toInsert.indexOf('|') >= 0)
/* 292:    */     {
/* 293:397 */       int ofst = toInsert.indexOf('|');
/* 294:398 */       int ofst2 = toInsert.indexOf('|', ofst + 1);
/* 295:399 */       toInsert = toInsert.replace("|", "");
/* 296:400 */       doc.insertString(dot, toInsert, null);
/* 297:401 */       dot = target.getCaretPosition();
/* 298:402 */       int strLength = toInsert.length();
/* 299:403 */       if (ofst2 > 0) {
/* 300:406 */         target.select(dot + ofst - strLength, dot + ofst2 - strLength - 1);
/* 301:    */       } else {
/* 302:408 */         target.setCaretPosition(dot + ofst - strLength);
/* 303:    */       }
/* 304:    */     }
/* 305:    */     else
/* 306:    */     {
/* 307:411 */       doc.insertString(dot, toInsert, null);
/* 308:    */     }
/* 309:    */   }
/* 310:    */   
/* 311:    */   public static void insertLinesTemplate(JTextComponent target, String[] templateLines)
/* 312:    */   {
/* 313:437 */     String thisIndent = getIndent(getLineAt(target, target.getSelectionStart()));
/* 314:438 */     String[] selLines = getSelectedLines(target);
/* 315:439 */     int selStart = -1;int selEnd = -1;
/* 316:440 */     StringBuffer sb = new StringBuffer();
/* 317:441 */     for (String tLine : templateLines)
/* 318:    */     {
/* 319:442 */       int selNdx = tLine.indexOf("#{selection}");
/* 320:443 */       if (selNdx >= 0)
/* 321:    */       {
/* 322:445 */         for (String selLine : selLines)
/* 323:    */         {
/* 324:446 */           sb.append(tLine.subSequence(0, selNdx));
/* 325:447 */           sb.append(selLine);
/* 326:448 */           sb.append('\n');
/* 327:    */         }
/* 328:    */       }
/* 329:    */       else
/* 330:    */       {
/* 331:451 */         sb.append(thisIndent);
/* 332:    */         
/* 333:453 */         Matcher pm = PTAGS_PATTERN.matcher(tLine);
/* 334:454 */         int lineStart = sb.length();
/* 335:455 */         while (pm.find())
/* 336:    */         {
/* 337:456 */           selStart = pm.start() + lineStart;
/* 338:457 */           pm.appendReplacement(sb, pm.group(1));
/* 339:458 */           selEnd = sb.length();
/* 340:    */         }
/* 341:460 */         pm.appendTail(sb);
/* 342:461 */         sb.append('\n');
/* 343:    */       }
/* 344:    */     }
/* 345:464 */     int ofst = target.getSelectionStart();
/* 346:465 */     target.replaceSelection(sb.toString());
/* 347:466 */     if (selStart >= 0) {
/* 348:468 */       target.select(ofst + selStart, ofst + selEnd);
/* 349:    */     }
/* 350:    */   }
/* 351:    */   
/* 352:    */   public static void insertSimpleTemplate(JTextComponent target, String template)
/* 353:    */   {
/* 354:489 */     String selected = target.getSelectedText();
/* 355:490 */     selected = selected == null ? "" : selected;
/* 356:491 */     StringBuffer sb = new StringBuffer(template.length());
/* 357:492 */     Matcher pm = PTAGS_PATTERN.matcher(template.replace("#{selection}", selected));
/* 358:493 */     int selStart = -1;int selEnd = -1;
/* 359:494 */     int lineStart = 0;
/* 360:495 */     while (pm.find())
/* 361:    */     {
/* 362:496 */       selStart = pm.start() + lineStart;
/* 363:497 */       pm.appendReplacement(sb, pm.group(1));
/* 364:498 */       selEnd = sb.length();
/* 365:    */     }
/* 366:500 */     pm.appendTail(sb);
/* 367:503 */     if (selStart >= 0)
/* 368:    */     {
/* 369:504 */       selStart += target.getSelectionStart();
/* 370:505 */       selEnd += target.getSelectionStart();
/* 371:    */     }
/* 372:507 */     target.replaceSelection(sb.toString());
/* 373:508 */     if (selStart >= 0) {
/* 374:510 */       target.select(selStart, selEnd);
/* 375:    */     }
/* 376:    */   }
/* 377:    */   
/* 378:    */   public static boolean selectLines(JTextComponent target)
/* 379:    */   {
/* 380:521 */     if (target.getSelectionStart() == target.getSelectionEnd()) {
/* 381:522 */       return false;
/* 382:    */     }
/* 383:524 */     PlainDocument pDoc = (PlainDocument)target.getDocument();
/* 384:525 */     Element es = pDoc.getParagraphElement(target.getSelectionStart());
/* 385:    */     
/* 386:    */ 
/* 387:528 */     Element ee = pDoc.getParagraphElement(target.getSelectionEnd() - 1);
/* 388:529 */     if ((es.equals(ee)) && (ee.getEndOffset() != target.getSelectionEnd())) {
/* 389:530 */       return false;
/* 390:    */     }
/* 391:532 */     int start = es.getStartOffset();
/* 392:533 */     int end = ee.getEndOffset();
/* 393:534 */     target.select(start, end - 1);
/* 394:535 */     return true;
/* 395:    */   }
/* 396:    */   
/* 397:    */   public static void setCaretPosition(JTextComponent target, int line, int column)
/* 398:    */   {
/* 399:545 */     int p = getDocumentPosition(target, line, column);
/* 400:546 */     target.setCaretPosition(p);
/* 401:    */   }
/* 402:    */   
/* 403:    */   public static String getTab(JTextComponent target)
/* 404:    */   {
/* 405:555 */     return "                ".substring(0, getTabSize(target));
/* 406:    */   }
/* 407:    */   
/* 408:    */   public static <T extends Action> T getAction(JTextComponent target, Class<T> aClass)
/* 409:    */   {
/* 410:567 */     for (Object k : target.getActionMap().allKeys())
/* 411:    */     {
/* 412:568 */       Action a = target.getActionMap().get(k);
/* 413:569 */       if (aClass.isInstance(a))
/* 414:    */       {
/* 415:571 */         T t = a;
/* 416:572 */         return t;
/* 417:    */       }
/* 418:    */     }
/* 419:575 */     return null;
/* 420:    */   }
/* 421:    */   
/* 422:    */   public static DefaultSyntaxKit getSyntaxKit(JTextComponent target)
/* 423:    */   {
/* 424:585 */     DefaultSyntaxKit kit = null;
/* 425:586 */     if ((target instanceof JEditorPane))
/* 426:    */     {
/* 427:587 */       JEditorPane jEditorPane = (JEditorPane)target;
/* 428:588 */       EditorKit k = jEditorPane.getEditorKit();
/* 429:589 */       if ((k instanceof DefaultSyntaxKit)) {
/* 430:590 */         kit = (DefaultSyntaxKit)k;
/* 431:    */       }
/* 432:    */     }
/* 433:593 */     return kit;
/* 434:    */   }
/* 435:    */   
/* 436:    */   public static void sendKeyPress(JTextComponent target, int v_key, int modifiers)
/* 437:    */   {
/* 438:603 */     KeyEvent ke = new KeyEvent(target, 401, System.currentTimeMillis(), modifiers, v_key, 65535);
/* 439:    */     
/* 440:605 */     target.dispatchEvent(ke);
/* 441:    */   }
/* 442:    */   
/* 443:608 */   static final String[] EMPTY_STRING_ARRAY = new String[0];
/* 444:    */   static final String SPACES = "                ";
/* 445:614 */   public static final Pattern PTAGS_PATTERN = Pattern.compile("\\#\\{p:([^}]*)\\}");
/* 446:    */   public static final String TEMPLATE_SELECTION = "#{selection}";
/* 447:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.ActionUtils
 * JD-Core Version:    0.7.0.1
 */