/*   1:    */ package weka.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ import java.util.LinkedHashMap;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.Map;
/*   7:    */ import weka.core.WekaException;
/*   8:    */ import weka.knowledgeflow.Data;
/*   9:    */ import weka.knowledgeflow.StepManager;
/*  10:    */ 
/*  11:    */ @KFStep(name="GraphViewer", category="Visualization", toolTipText="Visualize graph output from Drawable schemes", iconPath="weka/gui/knowledgeflow/icons/DefaultGraph.gif")
/*  12:    */ public class GraphViewer
/*  13:    */   extends BaseSimpleDataVisualizer
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = -3256888744740965144L;
/*  16:    */   
/*  17:    */   public List<String> getIncomingConnectionTypes()
/*  18:    */   {
/*  19: 58 */     return Arrays.asList(new String[] { "graph" });
/*  20:    */   }
/*  21:    */   
/*  22:    */   public List<String> getOutgoingConnectionTypes()
/*  23:    */   {
/*  24: 72 */     return getStepManager().numIncomingConnections() > 0 ? Arrays.asList(new String[] { "text" }) : null;
/*  25:    */   }
/*  26:    */   
/*  27:    */   public void processIncoming(Data data)
/*  28:    */     throws WekaException
/*  29:    */   {
/*  30: 84 */     getStepManager().processing();
/*  31: 85 */     String graphTitle = (String)data.getPayloadElement("graph_title");
/*  32:    */     
/*  33: 87 */     getStepManager().logDetailed(graphTitle);
/*  34: 88 */     this.m_data.add(data);
/*  35:    */     
/*  36: 90 */     Data textOut = new Data("text", data.getPrimaryPayload());
/*  37: 91 */     textOut.setPayloadElement("aux_textTitle", graphTitle);
/*  38: 92 */     getStepManager().outputData(new Data[] { textOut });
/*  39: 93 */     getStepManager().finished();
/*  40:    */   }
/*  41:    */   
/*  42:    */   public Map<String, String> getInteractiveViewers()
/*  43:    */   {
/*  44:118 */     Map<String, String> views = new LinkedHashMap();
/*  45:120 */     if (this.m_data.size() > 0) {
/*  46:121 */       views.put("Show plots", "weka.gui.knowledgeflow.steps.GraphViewerInteractiveView");
/*  47:    */     }
/*  48:125 */     return views;
/*  49:    */   }
/*  50:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.GraphViewer
 * JD-Core Version:    0.7.0.1
 */