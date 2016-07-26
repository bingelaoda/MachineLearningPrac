/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import javax.swing.text.BadLocationException;
/*  5:   */ import javax.swing.text.Element;
/*  6:   */ import javax.swing.text.JTextComponent;
/*  7:   */ import jsyntaxpane.SyntaxDocument;
/*  8:   */ import jsyntaxpane.Token;
/*  9:   */ 
/* 10:   */ public class JUnindentAction
/* 11:   */   extends DefaultSyntaxAction
/* 12:   */ {
/* 13:   */   public JUnindentAction()
/* 14:   */   {
/* 15:31 */     super("JUNINDENT");
/* 16:   */   }
/* 17:   */   
/* 18:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sDoc, int dot, ActionEvent e)
/* 19:   */   {
/* 20:40 */     int pos = target.getCaretPosition();
/* 21:41 */     int start = sDoc.getParagraphElement(pos).getStartOffset();
/* 22:42 */     String line = ActionUtils.getLine(target);
/* 23:43 */     if (ActionUtils.isEmptyOrBlanks(line)) {
/* 24:   */       try
/* 25:   */       {
/* 26:45 */         sDoc.insertString(pos, "}", null);
/* 27:46 */         Token t = sDoc.getPairFor(sDoc.getTokenAt(pos));
/* 28:47 */         if (null != t)
/* 29:   */         {
/* 30:48 */           String pairLine = ActionUtils.getLineAt(target, t.start);
/* 31:49 */           String indent = ActionUtils.getIndent(pairLine);
/* 32:50 */           sDoc.replace(start, line.length() + 1, indent + "}", null);
/* 33:   */         }
/* 34:   */       }
/* 35:   */       catch (BadLocationException ble)
/* 36:   */       {
/* 37:53 */         target.replaceSelection("}");
/* 38:   */       }
/* 39:   */     } else {
/* 40:56 */       target.replaceSelection("}");
/* 41:   */     }
/* 42:   */   }
/* 43:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.JUnindentAction
 * JD-Core Version:    0.7.0.1
 */