/*   1:    */ package weka.gui.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import weka.core.Defaults;
/*   5:    */ import weka.core.Instances;
/*   6:    */ import weka.gui.AbstractPerspective;
/*   7:    */ import weka.gui.GUIApplication;
/*   8:    */ import weka.gui.PerspectiveInfo;
/*   9:    */ import weka.gui.explorer.VisualizePanel.ScatterDefaults;
/*  10:    */ import weka.gui.visualize.MatrixPanel;
/*  11:    */ import weka.gui.visualize.VisualizeUtils.VisualizeDefaults;
/*  12:    */ 
/*  13:    */ @PerspectiveInfo(ID="weka.gui.knowledgeflow.scatterplotmatrixperspective", title="Scatter plot matrix", toolTipText="Scatter plots", iconPath="weka/gui/knowledgeflow/icons/application_view_tile.png")
/*  14:    */ public class ScatterPlotMatrixPerspective
/*  15:    */   extends AbstractPerspective
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = 5661598509822826837L;
/*  18:    */   protected MatrixPanel m_matrixPanel;
/*  19:    */   protected Instances m_visualizeDataSet;
/*  20:    */   
/*  21:    */   public ScatterPlotMatrixPerspective()
/*  22:    */   {
/*  23: 57 */     setLayout(new BorderLayout());
/*  24: 58 */     this.m_matrixPanel = new MatrixPanel();
/*  25: 59 */     add(this.m_matrixPanel, "Center");
/*  26:    */   }
/*  27:    */   
/*  28:    */   public Defaults getDefaultSettings()
/*  29:    */   {
/*  30: 71 */     Defaults d = new VisualizePanel.ScatterDefaults();
/*  31: 72 */     d.setID(getPerspectiveID());
/*  32: 73 */     d.add(new VisualizeUtils.VisualizeDefaults());
/*  33: 74 */     return d;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public boolean acceptsInstances()
/*  37:    */   {
/*  38: 84 */     return true;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setActive(boolean active)
/*  42:    */   {
/*  43: 94 */     super.setActive(active);
/*  44: 95 */     if ((this.m_isActive) && (this.m_visualizeDataSet != null))
/*  45:    */     {
/*  46: 96 */       this.m_matrixPanel.applySettings(getMainApplication().getApplicationSettings(), getPerspectiveID());
/*  47:    */       
/*  48: 98 */       this.m_matrixPanel.updatePanel();
/*  49:    */     }
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setInstances(Instances instances)
/*  53:    */   {
/*  54:109 */     this.m_visualizeDataSet = instances;
/*  55:110 */     this.m_matrixPanel.setInstances(this.m_visualizeDataSet);
/*  56:    */   }
/*  57:    */   
/*  58:    */   public boolean okToBeActive()
/*  59:    */   {
/*  60:121 */     return this.m_visualizeDataSet != null;
/*  61:    */   }
/*  62:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.ScatterPlotMatrixPerspective
 * JD-Core Version:    0.7.0.1
 */