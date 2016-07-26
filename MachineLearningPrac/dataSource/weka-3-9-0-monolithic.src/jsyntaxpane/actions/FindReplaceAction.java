/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import javax.swing.text.JTextComponent;
/*  5:   */ import jsyntaxpane.SyntaxDocument;
/*  6:   */ 
/*  7:   */ public class FindReplaceAction
/*  8:   */   extends DefaultSyntaxAction
/*  9:   */ {
/* 10:   */   public FindReplaceAction()
/* 11:   */   {
/* 12:33 */     super("FIND_REPLACE");
/* 13:   */   }
/* 14:   */   
/* 15:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sdoc, int dot, ActionEvent e)
/* 16:   */   {
/* 17:39 */     DocumentSearchData dsd = DocumentSearchData.getFromEditor(target);
/* 18:40 */     dsd.showReplaceDialog(target);
/* 19:   */   }
/* 20:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.FindReplaceAction
 * JD-Core Version:    0.7.0.1
 */