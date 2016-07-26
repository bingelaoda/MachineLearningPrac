/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import javax.swing.text.JTextComponent;
/*  5:   */ import jsyntaxpane.SyntaxDocument;
/*  6:   */ 
/*  7:   */ public class TemplateAction
/*  8:   */   extends DefaultSyntaxAction
/*  9:   */ {
/* 10:   */   private String template;
/* 11:34 */   private String[] tlines = null;
/* 12:   */   private boolean wholeLines;
/* 13:   */   private boolean mustHaveSelection;
/* 14:   */   
/* 15:   */   public TemplateAction()
/* 16:   */   {
/* 17:39 */     super("template");
/* 18:   */   }
/* 19:   */   
/* 20:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sdoc, int dot, ActionEvent e)
/* 21:   */   {
/* 22:45 */     if ((this.mustHaveSelection) && 
/* 23:46 */       (target.getSelectionEnd() == target.getSelectionStart())) {
/* 24:47 */       return;
/* 25:   */     }
/* 26:50 */     if (this.wholeLines)
/* 27:   */     {
/* 28:51 */       if (this.tlines == null) {
/* 29:52 */         this.tlines = this.template.split("\n");
/* 30:   */       }
/* 31:54 */       ActionUtils.insertLinesTemplate(target, this.tlines);
/* 32:   */     }
/* 33:   */     else
/* 34:   */     {
/* 35:56 */       ActionUtils.insertSimpleTemplate(target, this.template);
/* 36:   */     }
/* 37:   */   }
/* 38:   */   
/* 39:   */   public void setWholeLines(String value)
/* 40:   */   {
/* 41:61 */     this.wholeLines = Boolean.parseBoolean(value);
/* 42:   */   }
/* 43:   */   
/* 44:   */   public void setTemplate(String t)
/* 45:   */   {
/* 46:65 */     this.template = t;
/* 47:   */   }
/* 48:   */   
/* 49:   */   public void setMustHaveSelection(String value)
/* 50:   */   {
/* 51:69 */     this.mustHaveSelection = Boolean.parseBoolean(value);
/* 52:   */   }
/* 53:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.TemplateAction
 * JD-Core Version:    0.7.0.1
 */