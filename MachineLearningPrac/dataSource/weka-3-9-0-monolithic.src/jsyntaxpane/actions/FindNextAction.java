/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import javax.swing.text.JTextComponent;
/*  5:   */ import jsyntaxpane.SyntaxDocument;
/*  6:   */ 
/*  7:   */ public class FindNextAction
/*  8:   */   extends DefaultSyntaxAction
/*  9:   */ {
/* 10:   */   public FindNextAction()
/* 11:   */   {
/* 12:13 */     super("find-next");
/* 13:   */   }
/* 14:   */   
/* 15:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sdoc, int dot, ActionEvent e)
/* 16:   */   {
/* 17:19 */     DocumentSearchData dsd = DocumentSearchData.getFromEditor(target);
/* 18:20 */     if ((dsd != null) && 
/* 19:21 */       (!dsd.doFindNext(target))) {
/* 20:22 */       dsd.msgNotFound(target);
/* 21:   */     }
/* 22:   */   }
/* 23:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.FindNextAction
 * JD-Core Version:    0.7.0.1
 */