/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import java.util.logging.Level;
/*  5:   */ import java.util.logging.Logger;
/*  6:   */ import javax.swing.text.BadLocationException;
/*  7:   */ import javax.swing.text.Element;
/*  8:   */ import javax.swing.text.JTextComponent;
/*  9:   */ import javax.swing.text.Segment;
/* 10:   */ import jsyntaxpane.SyntaxDocument;
/* 11:   */ 
/* 12:   */ public class SmartHomeAction
/* 13:   */   extends DefaultSyntaxAction
/* 14:   */ {
/* 15:   */   public SmartHomeAction()
/* 16:   */   {
/* 17:33 */     super("smart-home");
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sDoc, int dot, ActionEvent e)
/* 21:   */   {
/* 22:   */     try
/* 23:   */     {
/* 24:40 */       target.setCaretPosition(getSmartHomeOffset(target, sDoc, dot));
/* 25:   */     }
/* 26:   */     catch (BadLocationException ex)
/* 27:   */     {
/* 28:42 */       Logger.getLogger(SmartHomeAction.class.getName()).log(Level.SEVERE, null, ex);
/* 29:   */     }
/* 30:   */   }
/* 31:   */   
/* 32:   */   static int getSmartHomeOffset(JTextComponent target, SyntaxDocument sDoc, int dot)
/* 33:   */     throws BadLocationException
/* 34:   */   {
/* 35:48 */     Element el = sDoc.getParagraphElement(dot);
/* 36:49 */     Segment seg = new Segment();
/* 37:50 */     sDoc.getText(el.getStartOffset(), el.getEndOffset() - el.getStartOffset() - 1, seg);
/* 38:   */     
/* 39:52 */     int homeOffset = 0;
/* 40:53 */     int dotLineOffset = dot - el.getStartOffset();
/* 41:54 */     boolean inText = false;
/* 42:56 */     for (int i = 0; i < dotLineOffset; i++) {
/* 43:57 */       if (!Character.isWhitespace(seg.charAt(i)))
/* 44:   */       {
/* 45:58 */         inText = true;
/* 46:59 */         break;
/* 47:   */       }
/* 48:   */     }
/* 49:65 */     if ((dotLineOffset == 0) || (inText)) {
/* 50:66 */       for (char ch = seg.first(); (ch != 65535) && (Character.isWhitespace(ch)); ch = seg.next()) {
/* 51:69 */         homeOffset++;
/* 52:   */       }
/* 53:   */     }
/* 54:72 */     return el.getStartOffset() + homeOffset;
/* 55:   */   }
/* 56:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.SmartHomeAction
 * JD-Core Version:    0.7.0.1
 */