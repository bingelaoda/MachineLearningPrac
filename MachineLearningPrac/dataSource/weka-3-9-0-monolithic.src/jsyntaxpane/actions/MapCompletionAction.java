/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import java.util.Map;
/*  5:   */ import javax.swing.text.JTextComponent;
/*  6:   */ import jsyntaxpane.SyntaxDocument;
/*  7:   */ import jsyntaxpane.Token;
/*  8:   */ import jsyntaxpane.util.JarServiceProvider;
/*  9:   */ 
/* 10:   */ public class MapCompletionAction
/* 11:   */   extends DefaultSyntaxAction
/* 12:   */ {
/* 13:   */   Map<String, String> completions;
/* 14:   */   
/* 15:   */   public MapCompletionAction()
/* 16:   */   {
/* 17:32 */     super("MAP_COMPLETION");
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sDoc, int dot, ActionEvent e)
/* 21:   */   {
/* 22:38 */     Token token = sDoc.getTokenAt(dot);
/* 23:39 */     if (token != null)
/* 24:   */     {
/* 25:40 */       String abbriv = ActionUtils.getTokenStringAt(sDoc, dot);
/* 26:41 */       if (this.completions.containsKey(abbriv))
/* 27:   */       {
/* 28:42 */         String completed = (String)this.completions.get(abbriv);
/* 29:43 */         if (completed.indexOf('|') >= 0)
/* 30:   */         {
/* 31:44 */           int ofst = completed.length() - completed.indexOf('|') - 1;
/* 32:45 */           sDoc.replaceToken(token, completed.replace("|", ""));
/* 33:46 */           target.setCaretPosition(target.getCaretPosition() - ofst);
/* 34:   */         }
/* 35:   */         else
/* 36:   */         {
/* 37:48 */           sDoc.replaceToken(token, completed);
/* 38:   */         }
/* 39:   */       }
/* 40:   */     }
/* 41:   */   }
/* 42:   */   
/* 43:   */   public void setCompletionsFile(String value)
/* 44:   */   {
/* 45:55 */     this.completions = JarServiceProvider.readStringsMap(value);
/* 46:   */   }
/* 47:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.MapCompletionAction
 * JD-Core Version:    0.7.0.1
 */