/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import javax.swing.text.JTextComponent;
/*  5:   */ import jsyntaxpane.SyntaxDocument;
/*  6:   */ import jsyntaxpane.actions.gui.GotoLineDialog;
/*  7:   */ 
/*  8:   */ public class GotoLineAction
/*  9:   */   extends DefaultSyntaxAction
/* 10:   */ {
/* 11:   */   public GotoLineAction()
/* 12:   */   {
/* 13:27 */     super("GOTO_LINE");
/* 14:   */   }
/* 15:   */   
/* 16:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sdoc, int dot, ActionEvent e)
/* 17:   */   {
/* 18:33 */     GotoLineDialog.showForEditor(target);
/* 19:   */   }
/* 20:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.GotoLineAction
 * JD-Core Version:    0.7.0.1
 */