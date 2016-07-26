/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import javax.swing.text.JTextComponent;
/*  5:   */ import jsyntaxpane.SyntaxDocument;
/*  6:   */ 
/*  7:   */ public class UnindentAction
/*  8:   */   extends DefaultSyntaxAction
/*  9:   */ {
/* 10:   */   public UnindentAction()
/* 11:   */   {
/* 12:28 */     super("UNINDENT");
/* 13:   */   }
/* 14:   */   
/* 15:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sDoc, int dot, ActionEvent e)
/* 16:   */   {
/* 17:34 */     String indent = ActionUtils.getTab(target);
/* 18:35 */     String[] lines = ActionUtils.getSelectedLines(target);
/* 19:36 */     int start = target.getSelectionStart();
/* 20:37 */     StringBuilder sb = new StringBuilder();
/* 21:38 */     for (String line : lines)
/* 22:   */     {
/* 23:39 */       if (line.startsWith(indent)) {
/* 24:40 */         sb.append(line.substring(indent.length()));
/* 25:41 */       } else if (line.startsWith("\t")) {
/* 26:42 */         sb.append(line.substring(1));
/* 27:   */       } else {
/* 28:44 */         sb.append(line);
/* 29:   */       }
/* 30:46 */       sb.append('\n');
/* 31:   */     }
/* 32:48 */     target.replaceSelection(sb.toString());
/* 33:49 */     target.select(start, start + sb.length());
/* 34:   */   }
/* 35:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.UnindentAction
 * JD-Core Version:    0.7.0.1
 */