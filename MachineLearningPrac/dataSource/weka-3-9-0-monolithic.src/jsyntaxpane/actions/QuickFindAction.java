/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import javax.swing.text.JTextComponent;
/*  5:   */ import jsyntaxpane.SyntaxDocument;
/*  6:   */ 
/*  7:   */ public class QuickFindAction
/*  8:   */   extends DefaultSyntaxAction
/*  9:   */ {
/* 10:   */   public QuickFindAction()
/* 11:   */   {
/* 12:27 */     super("quick-find");
/* 13:   */   }
/* 14:   */   
/* 15:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sDoc, int dot, ActionEvent e)
/* 16:   */   {
/* 17:33 */     DocumentSearchData.getFromEditor(target).showQuickFindDialog(target);
/* 18:   */   }
/* 19:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.QuickFindAction
 * JD-Core Version:    0.7.0.1
 */