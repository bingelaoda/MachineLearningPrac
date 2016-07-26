/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import java.util.Map;
/*  5:   */ import javax.swing.JEditorPane;
/*  6:   */ import javax.swing.JOptionPane;
/*  7:   */ import javax.swing.text.JTextComponent;
/*  8:   */ import jsyntaxpane.DefaultSyntaxKit;
/*  9:   */ import jsyntaxpane.SyntaxDocument;
/* 10:   */ import jsyntaxpane.actions.gui.ShowAbbsDialog;
/* 11:   */ 
/* 12:   */ public class ShowAbbsAction
/* 13:   */   extends DefaultSyntaxAction
/* 14:   */ {
/* 15:   */   public ShowAbbsAction()
/* 16:   */   {
/* 17:34 */     super("show-abbreviations");
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sDoc, int dot, ActionEvent e)
/* 21:   */   {
/* 22:41 */     DefaultSyntaxKit kit = ActionUtils.getSyntaxKit(target);
/* 23:42 */     if (kit != null)
/* 24:   */     {
/* 25:43 */       Map<String, String> abbs = kit.getAbbreviations();
/* 26:44 */       if ((abbs == null) || (abbs.isEmpty())) {
/* 27:45 */         JOptionPane.showMessageDialog(target, "No Abbreviations exist for this content type");
/* 28:   */       } else {
/* 29:48 */         new ShowAbbsDialog((JEditorPane)target, abbs);
/* 30:   */       }
/* 31:   */     }
/* 32:   */   }
/* 33:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.ShowAbbsAction
 * JD-Core Version:    0.7.0.1
 */