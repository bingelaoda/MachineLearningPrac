/*  1:   */ package weka.knowledgeflow.steps;
/*  2:   */ 
/*  3:   */ import java.util.Arrays;
/*  4:   */ import java.util.LinkedHashMap;
/*  5:   */ import java.util.List;
/*  6:   */ import java.util.Map;
/*  7:   */ import weka.gui.visualize.PlotData2D;
/*  8:   */ import weka.knowledgeflow.Data;
/*  9:   */ import weka.knowledgeflow.StepManager;
/* 10:   */ 
/* 11:   */ @KFStep(name="CostBenefitAnalysis", category="Visualization", toolTipText="View threshold data in an interactive cost-benefit visualization", iconPath="weka/gui/knowledgeflow/icons/ModelPerformanceChart.gif")
/* 12:   */ public class CostBenefitAnalysis
/* 13:   */   extends BaseSimpleDataVisualizer
/* 14:   */ {
/* 15:   */   private static final long serialVersionUID = 7756281775575854085L;
/* 16:   */   
/* 17:   */   public List<String> getIncomingConnectionTypes()
/* 18:   */   {
/* 19:49 */     return Arrays.asList(new String[] { "thresholdData" });
/* 20:   */   }
/* 21:   */   
/* 22:   */   public void processIncoming(Data data)
/* 23:   */   {
/* 24:59 */     getStepManager().processing();
/* 25:   */     
/* 26:61 */     PlotData2D pd = (PlotData2D)data.getPrimaryPayload();
/* 27:   */     
/* 28:63 */     getStepManager().logDetailed("Processing " + pd.getPlotName());
/* 29:64 */     this.m_data.add(data);
/* 30:65 */     getStepManager().finished();
/* 31:   */   }
/* 32:   */   
/* 33:   */   public Map<String, String> getInteractiveViewers()
/* 34:   */   {
/* 35:90 */     Map<String, String> views = new LinkedHashMap();
/* 36:92 */     if (this.m_data.size() > 0) {
/* 37:93 */       views.put("Show plots", "weka.gui.knowledgeflow.steps.CostBenefitAnalysisInteractiveView");
/* 38:   */     }
/* 39:97 */     return views;
/* 40:   */   }
/* 41:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.CostBenefitAnalysis
 * JD-Core Version:    0.7.0.1
 */