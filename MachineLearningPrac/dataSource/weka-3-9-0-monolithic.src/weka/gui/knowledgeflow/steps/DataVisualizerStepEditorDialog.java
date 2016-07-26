/*  1:   */ package weka.gui.knowledgeflow.steps;
/*  2:   */ 
/*  3:   */ import javax.swing.JComboBox;
/*  4:   */ import weka.gui.EnvironmentField;
/*  5:   */ import weka.knowledgeflow.steps.DataVisualizer;
/*  6:   */ 
/*  7:   */ public class DataVisualizerStepEditorDialog
/*  8:   */   extends ModelPerformanceChartStepEditorDialog
/*  9:   */ {
/* 10:   */   private static final long serialVersionUID = -6032558757326543902L;
/* 11:   */   
/* 12:   */   protected void getCurrentSettings()
/* 13:   */   {
/* 14:43 */     this.m_currentRendererName = ((DataVisualizer)getStepToEdit()).getOffscreenRendererName();
/* 15:   */     
/* 16:45 */     this.m_currentRendererOptions = ((DataVisualizer)getStepToEdit()).getOffscreenAdditionalOpts();
/* 17:   */   }
/* 18:   */   
/* 19:   */   public void okPressed()
/* 20:   */   {
/* 21:54 */     ((DataVisualizer)getStepToEdit()).setOffscreenRendererName(this.m_offscreenSelector.getSelectedItem().toString());
/* 22:   */     
/* 23:   */ 
/* 24:57 */     ((DataVisualizer)getStepToEdit()).setOffscreenAdditionalOpts(this.m_rendererOptions.getText());
/* 25:   */   }
/* 26:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.DataVisualizerStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */