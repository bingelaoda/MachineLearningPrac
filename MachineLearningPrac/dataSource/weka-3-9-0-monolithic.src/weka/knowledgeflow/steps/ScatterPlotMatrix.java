/*  1:   */ package weka.knowledgeflow.steps;
/*  2:   */ 
/*  3:   */ import java.util.LinkedHashMap;
/*  4:   */ import java.util.List;
/*  5:   */ import java.util.Map;
/*  6:   */ 
/*  7:   */ @KFStep(name="ScatterPlotMatrix", category="Visualization", toolTipText="Visualize datasets in a scatter plot matrix", iconPath="weka/gui/knowledgeflow/icons/ScatterPlotMatrix.gif")
/*  8:   */ public class ScatterPlotMatrix
/*  9:   */   extends BaseSimpleDataVisualizer
/* 10:   */ {
/* 11:   */   private static final long serialVersionUID = -2033576643553187310L;
/* 12:   */   
/* 13:   */   public Map<String, String> getInteractiveViewers()
/* 14:   */   {
/* 15:64 */     Map<String, String> views = new LinkedHashMap();
/* 16:66 */     if (this.m_data.size() > 0) {
/* 17:67 */       views.put("Show plots", "weka.gui.knowledgeflow.steps.ScatterPlotMatrixInteractiveView");
/* 18:   */     }
/* 19:71 */     return views;
/* 20:   */   }
/* 21:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.ScatterPlotMatrix
 * JD-Core Version:    0.7.0.1
 */