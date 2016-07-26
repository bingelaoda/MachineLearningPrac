/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import java.util.regex.Matcher;
/*  5:   */ import java.util.regex.Pattern;
/*  6:   */ import javax.swing.text.JTextComponent;
/*  7:   */ import jsyntaxpane.SyntaxDocument;
/*  8:   */ 
/*  9:   */ public class ToggleCommentsAction
/* 10:   */   extends DefaultSyntaxAction
/* 11:   */ {
/* 12:29 */   protected String lineCommentStart = "// ";
/* 13:30 */   protected Pattern lineCommentPattern = null;
/* 14:   */   
/* 15:   */   public ToggleCommentsAction()
/* 16:   */   {
/* 17:37 */     super("toggle-comment");
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sDoc, int dot, ActionEvent e)
/* 21:   */   {
/* 22:47 */     if (this.lineCommentPattern == null) {
/* 23:48 */       this.lineCommentPattern = Pattern.compile("(^" + this.lineCommentStart + ")(.*)");
/* 24:   */     }
/* 25:50 */     String[] lines = ActionUtils.getSelectedLines(target);
/* 26:51 */     int start = target.getSelectionStart();
/* 27:52 */     StringBuffer toggled = new StringBuffer();
/* 28:53 */     for (int i = 0; i < lines.length; i++)
/* 29:   */     {
/* 30:54 */       Matcher m = this.lineCommentPattern.matcher(lines[i]);
/* 31:55 */       if (m.find())
/* 32:   */       {
/* 33:56 */         toggled.append(m.replaceFirst("$2"));
/* 34:   */       }
/* 35:   */       else
/* 36:   */       {
/* 37:58 */         toggled.append(this.lineCommentStart);
/* 38:59 */         toggled.append(lines[i]);
/* 39:   */       }
/* 40:61 */       toggled.append('\n');
/* 41:   */     }
/* 42:63 */     target.replaceSelection(toggled.toString());
/* 43:64 */     target.select(start, start + toggled.length());
/* 44:   */   }
/* 45:   */   
/* 46:   */   public void setLineComments(String value)
/* 47:   */   {
/* 48:68 */     this.lineCommentStart = value.replace("\"", "");
/* 49:   */   }
/* 50:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.ToggleCommentsAction
 * JD-Core Version:    0.7.0.1
 */