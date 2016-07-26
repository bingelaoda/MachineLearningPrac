/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import javax.swing.text.JTextComponent;
/*  5:   */ import jsyntaxpane.SyntaxDocument;
/*  6:   */ import jsyntaxpane.Token;
/*  7:   */ 
/*  8:   */ public class JumpToPairAction
/*  9:   */   extends DefaultSyntaxAction
/* 10:   */ {
/* 11:   */   public JumpToPairAction()
/* 12:   */   {
/* 13:28 */     super("JUMP_TO_PAIR");
/* 14:   */   }
/* 15:   */   
/* 16:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sdoc, int dot, ActionEvent e)
/* 17:   */   {
/* 18:34 */     Token current = sdoc.getTokenAt(dot);
/* 19:35 */     if (current == null) {
/* 20:36 */       return;
/* 21:   */     }
/* 22:39 */     Token pair = sdoc.getPairFor(current);
/* 23:40 */     if (pair != null) {
/* 24:41 */       target.setCaretPosition(pair.start);
/* 25:   */     }
/* 26:   */   }
/* 27:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.JumpToPairAction
 * JD-Core Version:    0.7.0.1
 */