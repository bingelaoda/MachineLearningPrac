/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import javax.swing.JEditorPane;
/*  5:   */ import javax.swing.text.JTextComponent;
/*  6:   */ import jsyntaxpane.DefaultSyntaxKit;
/*  7:   */ 
/*  8:   */ public class ToggleComponentAction
/*  9:   */   extends DefaultSyntaxAction
/* 10:   */ {
/* 11:   */   private String componentName;
/* 12:   */   
/* 13:   */   public ToggleComponentAction()
/* 14:   */   {
/* 15:34 */     super("toggle-component");
/* 16:35 */     putValue("SwingSelectedKey", Boolean.TRUE);
/* 17:   */   }
/* 18:   */   
/* 19:   */   public void setComponent(String name)
/* 20:   */   {
/* 21:39 */     this.componentName = name;
/* 22:   */   }
/* 23:   */   
/* 24:   */   public String toString()
/* 25:   */   {
/* 26:44 */     return super.toString() + "(" + this.componentName + ")";
/* 27:   */   }
/* 28:   */   
/* 29:   */   public void actionPerformed(ActionEvent e)
/* 30:   */   {
/* 31:49 */     JTextComponent target = getTextComponent(e);
/* 32:50 */     if ((target instanceof JEditorPane))
/* 33:   */     {
/* 34:51 */       JEditorPane jEditorPane = (JEditorPane)target;
/* 35:52 */       DefaultSyntaxKit kit = (DefaultSyntaxKit)jEditorPane.getEditorKit();
/* 36:53 */       boolean status = kit.toggleComponent(jEditorPane, this.componentName);
/* 37:54 */       putValue("SwingSelectedKey", Boolean.valueOf(status));
/* 38:   */     }
/* 39:   */   }
/* 40:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.ToggleComponentAction
 * JD-Core Version:    0.7.0.1
 */