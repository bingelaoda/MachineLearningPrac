/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import javax.swing.text.JTextComponent;
/*  5:   */ import jsyntaxpane.SyntaxDocument;
/*  6:   */ 
/*  7:   */ public class RedoAction
/*  8:   */   extends DefaultSyntaxAction
/*  9:   */ {
/* 10:   */   public RedoAction()
/* 11:   */   {
/* 12:28 */     super("REDO");
/* 13:   */   }
/* 14:   */   
/* 15:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sDoc, int dot, ActionEvent e)
/* 16:   */   {
/* 17:34 */     if (sDoc != null) {
/* 18:35 */       sDoc.doRedo();
/* 19:   */     }
/* 20:   */   }
/* 21:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.RedoAction
 * JD-Core Version:    0.7.0.1
 */