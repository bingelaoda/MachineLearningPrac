/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import javax.swing.text.JTextComponent;
/*  5:   */ import jsyntaxpane.SyntaxDocument;
/*  6:   */ import jsyntaxpane.Token;
/*  7:   */ import jsyntaxpane.actions.gui.ReflectCompletionDialog;
/*  8:   */ 
/*  9:   */ public class ReflectCompletionAction
/* 10:   */   extends DefaultSyntaxAction
/* 11:   */ {
/* 12:   */   ReflectCompletionDialog dlg;
/* 13:   */   
/* 14:   */   public ReflectCompletionAction()
/* 15:   */   {
/* 16:34 */     super("REFLECT_COMPLETION");
/* 17:   */   }
/* 18:   */   
/* 19:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sDoc, int dot, ActionEvent e)
/* 20:   */   {
/* 21:40 */     Token t = sDoc.getTokenAt(dot);
/* 22:41 */     if (t != null) {
/* 23:42 */       target.select(t.start, t.end());
/* 24:   */     }
/* 25:44 */     if (this.dlg == null) {
/* 26:45 */       this.dlg = new ReflectCompletionDialog(target);
/* 27:   */     }
/* 28:47 */     this.dlg.displayFor(target);
/* 29:   */   }
/* 30:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.ReflectCompletionAction
 * JD-Core Version:    0.7.0.1
 */