/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.Dimension;
/*   4:    */ import java.awt.event.ActionEvent;
/*   5:    */ import java.awt.event.ActionListener;
/*   6:    */ import java.util.List;
/*   7:    */ import javax.swing.JButton;
/*   8:    */ import weka.core.Defaults;
/*   9:    */ import weka.core.Settings;
/*  10:    */ import weka.core.WekaException;
/*  11:    */ import weka.gui.knowledgeflow.BaseInteractiveViewer;
/*  12:    */ import weka.gui.visualize.PlotData2D;
/*  13:    */ import weka.gui.visualize.VisualizePanel;
/*  14:    */ import weka.gui.visualize.VisualizeUtils.VisualizeDefaults;
/*  15:    */ import weka.knowledgeflow.steps.ModelPerformanceChart;
/*  16:    */ 
/*  17:    */ public class ModelPerformanceChartInteractiveView
/*  18:    */   extends BaseInteractiveViewer
/*  19:    */ {
/*  20:    */   private static final long serialVersionUID = 8818417648798221980L;
/*  21: 50 */   protected JButton m_clearButton = new JButton("Clear results");
/*  22: 53 */   protected VisualizePanel m_visPanel = new VisualizePanel();
/*  23:    */   protected static final String ID = "weka.gui.knowledgeflow.steps.ModelPerformanceChartInteractiveView";
/*  24:    */   
/*  25:    */   public String getViewerName()
/*  26:    */   {
/*  27: 66 */     return "Model Performance Chart";
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void init()
/*  31:    */     throws WekaException
/*  32:    */   {
/*  33: 76 */     addButton(this.m_clearButton);
/*  34: 77 */     add(this.m_visPanel, "Center");
/*  35:    */     
/*  36: 79 */     this.m_clearButton.addActionListener(new ActionListener()
/*  37:    */     {
/*  38:    */       public void actionPerformed(ActionEvent e)
/*  39:    */       {
/*  40: 82 */         ModelPerformanceChartInteractiveView.this.m_visPanel.removeAllPlots();
/*  41: 83 */         ModelPerformanceChartInteractiveView.this.m_visPanel.validate();
/*  42: 84 */         ModelPerformanceChartInteractiveView.this.m_visPanel.repaint();
/*  43:    */         
/*  44:    */ 
/*  45: 87 */         ((ModelPerformanceChart)ModelPerformanceChartInteractiveView.this.getStep()).clearPlotData();
/*  46:    */       }
/*  47: 90 */     });
/*  48: 91 */     List<PlotData2D> plotData = ((ModelPerformanceChart)getStep()).getPlots();
/*  49:    */     try
/*  50:    */     {
/*  51: 93 */       this.m_visPanel.setMasterPlot((PlotData2D)plotData.get(0));
/*  52: 95 */       for (int i = 1; i < plotData.size(); i++) {
/*  53: 96 */         this.m_visPanel.addPlot((PlotData2D)plotData.get(i));
/*  54:    */       }
/*  55: 99 */       if (((ModelPerformanceChart)getStep()).isDataIsThresholdData())
/*  56:    */       {
/*  57:100 */         this.m_visPanel.setXIndex(4);
/*  58:101 */         this.m_visPanel.setYIndex(5);
/*  59:    */       }
/*  60:    */     }
/*  61:    */     catch (Exception ex)
/*  62:    */     {
/*  63:104 */       throw new WekaException(ex);
/*  64:    */     }
/*  65:106 */     this.m_visPanel.setPreferredSize(new Dimension(800, 600));
/*  66:107 */     applySettings(getSettings());
/*  67:    */   }
/*  68:    */   
/*  69:    */   public Defaults getDefaultSettings()
/*  70:    */   {
/*  71:117 */     Defaults d = new VisualizeUtils.VisualizeDefaults();
/*  72:118 */     d.setID("weka.gui.knowledgeflow.steps.ModelPerformanceChartInteractiveView");
/*  73:    */     
/*  74:120 */     return d;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void applySettings(Settings settings)
/*  78:    */   {
/*  79:131 */     this.m_visPanel.applySettings(settings, "weka.gui.knowledgeflow.steps.ModelPerformanceChartInteractiveView");
/*  80:    */   }
/*  81:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.ModelPerformanceChartInteractiveView
 * JD-Core Version:    0.7.0.1
 */