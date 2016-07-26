/*  1:   */ package jsyntaxpane.actions;
/*  2:   */ 
/*  3:   */ import java.awt.event.ActionEvent;
/*  4:   */ import javax.swing.text.JTextComponent;
/*  5:   */ import jsyntaxpane.SyntaxDocument;
/*  6:   */ import jsyntaxpane.actions.gui.HTMLPreviewFrame;
/*  7:   */ 
/*  8:   */ public class HTMLPreviewAction
/*  9:   */   extends DefaultSyntaxAction
/* 10:   */ {
/* 11:   */   public static final String HTML_PREVIEW_WINDOW = "html-preview-window";
/* 12:   */   
/* 13:   */   public HTMLPreviewAction()
/* 14:   */   {
/* 15:30 */     super("HTML_PREVIEW");
/* 16:   */   }
/* 17:   */   
/* 18:   */   public void actionPerformed(JTextComponent target, SyntaxDocument sDoc, int dot, ActionEvent e)
/* 19:   */   {
/* 20:37 */     Object obj = sDoc.getProperty("html-preview-window");
/* 21:38 */     if (obj == null)
/* 22:   */     {
/* 23:39 */       HTMLPreviewFrame dlg = new HTMLPreviewFrame(sDoc);
/* 24:40 */       sDoc.putProperty("html-preview-window", dlg);
/* 25:41 */       dlg.setVisible(true);
/* 26:   */     }
/* 27:   */     else
/* 28:   */     {
/* 29:43 */       HTMLPreviewFrame dlg = (HTMLPreviewFrame)obj;
/* 30:44 */       dlg.setVisible(this.enabled);
/* 31:   */     }
/* 32:   */   }
/* 33:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     jsyntaxpane.actions.HTMLPreviewAction
 * JD-Core Version:    0.7.0.1
 */