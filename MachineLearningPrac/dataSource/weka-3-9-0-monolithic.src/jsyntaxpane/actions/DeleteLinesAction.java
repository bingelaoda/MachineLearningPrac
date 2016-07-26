/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import java.util.logging.Level;
/*  5:   */ import java.util.logging.Logger;
/*  6:   */ import javax.swing.text.BadLocationException;
/*  7:   */ import javax.swing.text.JTextComponent;
/*  8:   */ import jsyntaxpane.SyntaxDocument;
/*  9:   */ 
/* 10:   */ public class DeleteLinesAction
/* 11:   */   extends DefaultSyntaxAction
/* 12:   */ {
/* 13:   */   public DeleteLinesAction()
/* 14:   */   {
/* 15:30 */     super("DELETE_LINES");
/* 16:   */   }
/* 17:   */   
/* 18:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sdoc, int dot, ActionEvent e)
/* 19:   */   {
/* 20:   */     try
/* 21:   */     {
/* 22:37 */       int st = sdoc.getLineStartOffset(target.getSelectionStart());
/* 23:38 */       int en = sdoc.getLineEndOffset(target.getSelectionEnd());
/* 24:39 */       sdoc.remove(st, en - st);
/* 25:   */     }
/* 26:   */     catch (BadLocationException ex)
/* 27:   */     {
/* 28:41 */       Logger.getLogger(DeleteLinesAction.class.getName()).log(Level.SEVERE, null, ex);
/* 29:   */     }
/* 30:   */   }
/* 31:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.DeleteLinesAction
 * JD-Core Version:    0.7.0.1
 */