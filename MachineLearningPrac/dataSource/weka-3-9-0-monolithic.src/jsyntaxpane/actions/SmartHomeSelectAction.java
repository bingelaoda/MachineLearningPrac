/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import java.util.logging.Level;
/*  5:   */ import java.util.logging.Logger;
/*  6:   */ import javax.swing.text.BadLocationException;
/*  7:   */ import javax.swing.text.JTextComponent;
/*  8:   */ import jsyntaxpane.SyntaxDocument;
/*  9:   */ 
/* 10:   */ public class SmartHomeSelectAction
/* 11:   */   extends DefaultSyntaxAction
/* 12:   */ {
/* 13:   */   public SmartHomeSelectAction()
/* 14:   */   {
/* 15:30 */     super("smart-home-select");
/* 16:   */   }
/* 17:   */   
/* 18:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sDoc, int dot, ActionEvent e)
/* 19:   */   {
/* 20:   */     try
/* 21:   */     {
/* 22:37 */       target.moveCaretPosition(SmartHomeAction.getSmartHomeOffset(target, sDoc, dot));
/* 23:   */     }
/* 24:   */     catch (BadLocationException ex)
/* 25:   */     {
/* 26:39 */       Logger.getLogger(SmartHomeSelectAction.class.getName()).log(Level.SEVERE, null, ex);
/* 27:   */     }
/* 28:   */   }
/* 29:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.SmartHomeSelectAction
 * JD-Core Version:    0.7.0.1
 */