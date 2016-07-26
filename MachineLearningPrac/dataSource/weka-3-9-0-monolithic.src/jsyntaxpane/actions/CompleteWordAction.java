/*   1:    */ package jsyntaxpane.actions;
/*   2:    */ 
/*   3:    */ import java.awt.event.ActionEvent;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.regex.Matcher;
/*   7:    */ import java.util.regex.Pattern;
/*   8:    */ import javax.swing.text.JTextComponent;
/*   9:    */ import jsyntaxpane.SyntaxDocument;
/*  10:    */ import jsyntaxpane.Token;
/*  11:    */ import jsyntaxpane.actions.gui.ComboCompletionDialog;
/*  12:    */ import jsyntaxpane.util.StringUtils;
/*  13:    */ 
/*  14:    */ public class CompleteWordAction
/*  15:    */   extends DefaultSyntaxAction
/*  16:    */ {
/*  17:    */   private ComboCompletionDialog dlg;
/*  18:    */   
/*  19:    */   public CompleteWordAction()
/*  20:    */   {
/*  21: 44 */     super("COMPLETE_WORD");
/*  22:    */   }
/*  23:    */   
/*  24:    */   public void actionPerformed(JTextComponent target, SyntaxDocument sdoc, int dot, ActionEvent e)
/*  25:    */   {
/*  26: 50 */     Token current = sdoc.getWordAt(dot, this.wordsPattern);
/*  27: 51 */     if (current == null) {
/*  28: 52 */       return;
/*  29:    */     }
/*  30: 54 */     String cw = current.getString(sdoc);
/*  31: 55 */     target.select(current.start, current.end());
/*  32:    */     
/*  33: 57 */     sdoc.readLock();
/*  34: 58 */     List<String> matches = new ArrayList();
/*  35: 59 */     Matcher m = sdoc.getMatcher(this.wordsPattern, 0, current.start);
/*  36: 60 */     addWords(m, cw, matches);
/*  37: 61 */     m = sdoc.getMatcher(this.wordsPattern, current.end(), sdoc.getLength() - current.end());
/*  38: 62 */     addWords(m, cw, matches);
/*  39: 63 */     sdoc.readUnlock();
/*  40: 64 */     if (matches.size() == 0) {
/*  41: 65 */       return;
/*  42:    */     }
/*  43: 67 */     if (matches.size() == 1)
/*  44:    */     {
/*  45: 68 */       target.replaceSelection((String)matches.get(0));
/*  46: 69 */       return;
/*  47:    */     }
/*  48: 71 */     if (this.dlg == null) {
/*  49: 72 */       this.dlg = new ComboCompletionDialog(target);
/*  50:    */     }
/*  51: 74 */     this.dlg.displayFor(cw, matches);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void setWordsRegexp(String value)
/*  55:    */   {
/*  56: 78 */     this.wordsPattern = Pattern.compile(value);
/*  57:    */   }
/*  58:    */   
/*  59:    */   private void addWords(Matcher m, String abbr, List<String> matches)
/*  60:    */   {
/*  61: 90 */     while ((m != null) && (m.find()))
/*  62:    */     {
/*  63: 91 */       String word = m.group();
/*  64: 92 */       if ((StringUtils.camelCaseMatch(word, abbr)) && 
/*  65: 93 */         (!matches.contains(word))) {
/*  66: 94 */         matches.add(word);
/*  67:    */       }
/*  68:    */     }
/*  69:    */   }
/*  70:    */   
/*  71:100 */   private Pattern wordsPattern = DEFAULT_WORDS_REGEXP;
/*  72:101 */   private static final Pattern DEFAULT_WORDS_REGEXP = Pattern.compile("\\w+");
/*  73:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.CompleteWordAction
 * JD-Core Version:    0.7.0.1
 */