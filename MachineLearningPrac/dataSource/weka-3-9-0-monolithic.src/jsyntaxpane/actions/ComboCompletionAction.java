/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import java.util.List;
/*  5:   */ import java.util.Map;
/*  6:   */ import javax.swing.text.JTextComponent;
/*  7:   */ import jsyntaxpane.SyntaxDocument;
/*  8:   */ import jsyntaxpane.Token;
/*  9:   */ import jsyntaxpane.actions.gui.ComboCompletionDialog;
/* 10:   */ import jsyntaxpane.util.JarServiceProvider;
/* 11:   */ 
/* 12:   */ public class ComboCompletionAction
/* 13:   */   extends DefaultSyntaxAction
/* 14:   */ {
/* 15:   */   Map<String, String> completions;
/* 16:   */   ComboCompletionDialog dlg;
/* 17:   */   private List<String> items;
/* 18:   */   
/* 19:   */   public ComboCompletionAction()
/* 20:   */   {
/* 21:40 */     super("COMBO_COMPLETION");
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sdoc, int dot, ActionEvent e)
/* 25:   */   {
/* 26:46 */     if (sdoc == null) {
/* 27:47 */       return;
/* 28:   */     }
/* 29:49 */     Token token = sdoc.getTokenAt(dot);
/* 30:50 */     String abbrev = "";
/* 31:51 */     if (token != null)
/* 32:   */     {
/* 33:52 */       abbrev = token.getString(sdoc);
/* 34:53 */       target.select(token.start, token.end());
/* 35:   */     }
/* 36:55 */     if (this.dlg == null) {
/* 37:56 */       this.dlg = new ComboCompletionDialog(target);
/* 38:   */     }
/* 39:58 */     this.dlg.displayFor(abbrev, this.items);
/* 40:   */   }
/* 41:   */   
/* 42:   */   public void setItemsURL(String value)
/* 43:   */   {
/* 44:62 */     this.items = JarServiceProvider.readLines(value);
/* 45:   */   }
/* 46:   */   
/* 47:   */   public List<String> getItems()
/* 48:   */   {
/* 49:70 */     return this.items;
/* 50:   */   }
/* 51:   */   
/* 52:   */   public void setItems(List<String> items)
/* 53:   */   {
/* 54:78 */     this.items = items;
/* 55:   */   }
/* 56:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.ComboCompletionAction
 * JD-Core Version:    0.7.0.1
 */