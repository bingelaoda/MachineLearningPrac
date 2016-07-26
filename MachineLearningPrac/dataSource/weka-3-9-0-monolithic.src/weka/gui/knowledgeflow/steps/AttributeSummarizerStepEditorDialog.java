/*  1:   */ package weka.gui.knowledgeflow.steps;
/*  2:   */ 
/*  3:   */ import javax.swing.JComboBox;
/*  4:   */ import weka.gui.EnvironmentField;
/*  5:   */ import weka.knowledgeflow.steps.AttributeSummarizer;
/*  6:   */ 
/*  7:   */ public class AttributeSummarizerStepEditorDialog
/*  8:   */   extends ModelPerformanceChartStepEditorDialog
/*  9:   */ {
/* 10:   */   private static final long serialVersionUID = -4504946065343184549L;
/* 11:   */   
/* 12:   */   protected void getCurrentSettings()
/* 13:   */   {
/* 14:42 */     this.m_currentRendererName = ((AttributeSummarizer)getStepToEdit()).getOffscreenRendererName();
/* 15:   */     
/* 16:44 */     this.m_currentRendererOptions = ((AttributeSummarizer)getStepToEdit()).getOffscreenAdditionalOpts();
/* 17:   */   }
/* 18:   */   
/* 19:   */   public void okPressed()
/* 20:   */   {
/* 21:53 */     ((AttributeSummarizer)getStepToEdit()).setOffscreenRendererName(this.m_offscreenSelector.getSelectedItem().toString());
/* 22:   */     
/* 23:   */ 
/* 24:56 */     ((AttributeSummarizer)getStepToEdit()).setOffscreenAdditionalOpts(this.m_rendererOptions.getText());
/* 25:   */   }
/* 26:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.AttributeSummarizerStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */