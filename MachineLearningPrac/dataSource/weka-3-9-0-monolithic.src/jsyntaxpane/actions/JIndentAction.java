/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import java.util.logging.Level;
/*  5:   */ import java.util.logging.Logger;
/*  6:   */ import javax.swing.text.BadLocationException;
/*  7:   */ import javax.swing.text.Element;
/*  8:   */ import javax.swing.text.JTextComponent;
/*  9:   */ import jsyntaxpane.SyntaxDocument;
/* 10:   */ import jsyntaxpane.Token;
/* 11:   */ import jsyntaxpane.TokenType;
/* 12:   */ 
/* 13:   */ public class JIndentAction
/* 14:   */   extends DefaultSyntaxAction
/* 15:   */ {
/* 16:   */   public JIndentAction()
/* 17:   */   {
/* 18:35 */     super("JINDENT");
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sDoc, int dot, ActionEvent e)
/* 22:   */   {
/* 23:45 */     int pos = target.getCaretPosition();
/* 24:46 */     int start = sDoc.getParagraphElement(pos).getStartOffset();
/* 25:47 */     String line = ActionUtils.getLine(target);
/* 26:48 */     String lineToPos = line.substring(0, pos - start);
/* 27:49 */     String prefix = ActionUtils.getIndent(line);
/* 28:50 */     Token t = sDoc.getTokenAt(pos);
/* 29:51 */     if (TokenType.isComment(t))
/* 30:   */     {
/* 31:52 */       String trimmed = line.trim();
/* 32:53 */       if ((!trimmed.startsWith("/*")) || (!trimmed.endsWith("*/"))) {
/* 33:55 */         if (trimmed.endsWith("*/")) {
/* 34:   */           try
/* 35:   */           {
/* 36:58 */             String commentStartLine = sDoc.getLineAt(t.start);
/* 37:59 */             prefix = ActionUtils.getIndent(commentStartLine);
/* 38:   */           }
/* 39:   */           catch (BadLocationException ex)
/* 40:   */           {
/* 41:61 */             Logger.getLogger(JIndentAction.class.getName()).log(Level.SEVERE, null, ex);
/* 42:   */           }
/* 43:63 */         } else if (trimmed.startsWith("*")) {
/* 44:64 */           prefix = prefix + "* ";
/* 45:65 */         } else if (trimmed.startsWith("/**")) {
/* 46:66 */           prefix = prefix + " * ";
/* 47:67 */         } else if (trimmed.startsWith("/*")) {
/* 48:68 */           prefix = prefix + " ";
/* 49:   */         }
/* 50:   */       }
/* 51:   */     }
/* 52:70 */     else if (lineToPos.trim().endsWith("{"))
/* 53:   */     {
/* 54:71 */       prefix = prefix + ActionUtils.getTab(target);
/* 55:   */     }
/* 56:   */     else
/* 57:   */     {
/* 58:73 */       String noComment = sDoc.getUncommentedText(start, pos);
/* 59:75 */       if (noComment.trim().endsWith("{")) {
/* 60:76 */         prefix = prefix + ActionUtils.getTab(target);
/* 61:   */       }
/* 62:   */     }
/* 63:79 */     target.replaceSelection("\n" + prefix);
/* 64:   */   }
/* 65:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.JIndentAction
 * JD-Core Version:    0.7.0.1
 */