/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import java.util.Map;
/*  5:   */ import java.util.regex.Pattern;
/*  6:   */ import javax.swing.JEditorPane;
/*  7:   */ import javax.swing.text.EditorKit;
/*  8:   */ import javax.swing.text.Element;
/*  9:   */ import javax.swing.text.JTextComponent;
/* 10:   */ import jsyntaxpane.DefaultSyntaxKit;
/* 11:   */ import jsyntaxpane.SyntaxDocument;
/* 12:   */ import jsyntaxpane.Token;
/* 13:   */ 
/* 14:   */ public class IndentAction
/* 15:   */   extends DefaultSyntaxAction
/* 16:   */ {
/* 17:   */   public IndentAction()
/* 18:   */   {
/* 19:41 */     super("insert-tab");
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sDoc, int dot, ActionEvent e)
/* 23:   */   {
/* 24:47 */     String selected = target.getSelectedText();
/* 25:48 */     EditorKit kit = ((JEditorPane)target).getEditorKit();
/* 26:49 */     Map<String, String> abbrvs = ((DefaultSyntaxKit)kit).getAbbreviations();
/* 27:50 */     if (selected == null)
/* 28:   */     {
/* 29:52 */       Token abbrToken = sDoc.getWordAt(dot, this.wordsPattern);
/* 30:53 */       Integer tabStop = Integer.valueOf(ActionUtils.getTabSize(target));
/* 31:54 */       int lineStart = sDoc.getParagraphElement(dot).getStartOffset();
/* 32:55 */       int column = dot - lineStart;
/* 33:56 */       int needed = tabStop.intValue() - column % tabStop.intValue();
/* 34:57 */       if ((abbrvs == null) || (abbrToken == null))
/* 35:   */       {
/* 36:58 */         target.replaceSelection("                ".substring(0, needed));
/* 37:   */       }
/* 38:   */       else
/* 39:   */       {
/* 40:60 */         String abbr = abbrToken.getString(sDoc);
/* 41:61 */         if (abbrvs.containsKey(abbr))
/* 42:   */         {
/* 43:62 */           target.select(abbrToken.start, abbrToken.end());
/* 44:63 */           abbr = (String)abbrvs.get(abbr);
/* 45:64 */           String[] abbrLines = abbr.split("\n");
/* 46:65 */           if (abbrLines.length > 1) {
/* 47:66 */             ActionUtils.insertLinesTemplate(target, abbrLines);
/* 48:   */           } else {
/* 49:68 */             ActionUtils.insertSimpleTemplate(target, abbr);
/* 50:   */           }
/* 51:   */         }
/* 52:   */         else
/* 53:   */         {
/* 54:71 */           target.replaceSelection("                ".substring(0, needed));
/* 55:   */         }
/* 56:   */       }
/* 57:   */     }
/* 58:   */     else
/* 59:   */     {
/* 60:75 */       String[] lines = ActionUtils.getSelectedLines(target);
/* 61:76 */       int start = target.getSelectionStart();
/* 62:77 */       StringBuilder sb = new StringBuilder();
/* 63:78 */       for (String line : lines)
/* 64:   */       {
/* 65:79 */         sb.append('\t');
/* 66:80 */         sb.append(line);
/* 67:81 */         sb.append('\n');
/* 68:   */       }
/* 69:83 */       target.replaceSelection(sb.toString());
/* 70:84 */       target.select(start, start + sb.length());
/* 71:   */     }
/* 72:   */   }
/* 73:   */   
/* 74:87 */   private Pattern wordsPattern = Pattern.compile("\\w+");
/* 75:   */   
/* 76:   */   public void setWordRegex(String regex)
/* 77:   */   {
/* 78:91 */     this.wordsPattern = Pattern.compile(regex);
/* 79:   */   }
/* 80:   */   
/* 81:   */   public Pattern getWordRegex()
/* 82:   */   {
/* 83:95 */     return this.wordsPattern;
/* 84:   */   }
/* 85:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.IndentAction
 * JD-Core Version:    0.7.0.1
 */