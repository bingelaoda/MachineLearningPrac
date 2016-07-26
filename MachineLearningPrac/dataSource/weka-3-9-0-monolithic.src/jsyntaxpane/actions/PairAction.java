/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import java.util.HashMap;
/*  5:   */ import java.util.Map;
/*  6:   */ import javax.swing.text.JTextComponent;
/*  7:   */ import jsyntaxpane.SyntaxDocument;
/*  8:   */ 
/*  9:   */ public class PairAction
/* 10:   */   extends DefaultSyntaxAction
/* 11:   */ {
/* 12:   */   public PairAction()
/* 13:   */   {
/* 14:31 */     super("PAIR_ACTION");
/* 15:   */   }
/* 16:   */   
/* 17:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sDoc, int dot, ActionEvent e)
/* 18:   */   {
/* 19:37 */     String left = e.getActionCommand();
/* 20:38 */     String right = (String)PAIRS.get(left);
/* 21:39 */     String selected = target.getSelectedText();
/* 22:40 */     if (selected != null)
/* 23:   */     {
/* 24:41 */       target.replaceSelection(left + selected + right);
/* 25:   */     }
/* 26:   */     else
/* 27:   */     {
/* 28:43 */       target.replaceSelection(left + right);
/* 29:44 */       target.setCaretPosition(target.getCaretPosition() - right.length());
/* 30:   */     }
/* 31:   */   }
/* 32:   */   
/* 33:47 */   private static Map<String, String> PAIRS = new HashMap(4);
/* 34:   */   
/* 35:   */   static
/* 36:   */   {
/* 37:51 */     PAIRS.put("(", ")");
/* 38:52 */     PAIRS.put("[", "]");
/* 39:53 */     PAIRS.put("\"", "\"");
/* 40:54 */     PAIRS.put("'", "'");
/* 41:   */   }
/* 42:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.PairAction
 * JD-Core Version:    0.7.0.1
 */