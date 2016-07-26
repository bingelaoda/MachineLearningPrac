/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import javax.swing.text.JTextComponent;
/*  5:   */ import jsyntaxpane.SyntaxDocument;
/*  6:   */ import jsyntaxpane.Token;
/*  7:   */ import jsyntaxpane.TokenType;
/*  8:   */ 
/*  9:   */ public class XmlTagCompleteAction
/* 10:   */   extends DefaultSyntaxAction
/* 11:   */ {
/* 12:   */   public XmlTagCompleteAction()
/* 13:   */   {
/* 14:29 */     super("XML_TAG_COMPLETE");
/* 15:   */   }
/* 16:   */   
/* 17:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sDoc, int dot, ActionEvent e)
/* 18:   */   {
/* 19:35 */     Token tok = sDoc.getTokenAt(dot);
/* 20:36 */     while ((tok != null) && (tok.type != TokenType.TYPE)) {
/* 21:37 */       tok = sDoc.getPrevToken(tok);
/* 22:   */     }
/* 23:39 */     if (tok == null)
/* 24:   */     {
/* 25:40 */       target.replaceSelection(">");
/* 26:   */     }
/* 27:   */     else
/* 28:   */     {
/* 29:42 */       CharSequence tag = tok.getText(sDoc);
/* 30:43 */       int savepos = target.getSelectionStart();
/* 31:44 */       target.replaceSelection("></" + tag.subSequence(1, tag.length()) + ">");
/* 32:45 */       target.setCaretPosition(savepos + 1);
/* 33:   */     }
/* 34:   */   }
/* 35:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.XmlTagCompleteAction
 * JD-Core Version:    0.7.0.1
 */