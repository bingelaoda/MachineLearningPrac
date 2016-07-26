/*   1:    */ package weka.gui.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import weka.core.Instances;
/*   5:    */ import weka.gui.AbstractPerspective;
/*   6:    */ import weka.gui.GUIApplication;
/*   7:    */ import weka.gui.PerspectiveInfo;
/*   8:    */ import weka.gui.PythonPanel;
/*   9:    */ 
/*  10:    */ @PerspectiveInfo(ID="python", title="CPython Scripting", toolTipText="Write and execute Python scripts", iconPath="weka/gui/knowledgeflow/icons/python-logo_small.png")
/*  11:    */ public class PythonPerspective
/*  12:    */   extends AbstractPerspective
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = 1309879745978244501L;
/*  15:    */   protected PythonPanel m_pythonPanel;
/*  16:    */   
/*  17:    */   public PythonPerspective()
/*  18:    */   {
/*  19: 53 */     setLayout(new BorderLayout());
/*  20: 54 */     this.m_pythonPanel = new PythonPanel(true, null);
/*  21: 55 */     add(this.m_pythonPanel, "Center");
/*  22:    */   }
/*  23:    */   
/*  24:    */   public void instantiationComplete()
/*  25:    */   {
/*  26: 64 */     this.m_pythonPanel = new PythonPanel(!getMainApplication().getApplicationID().equals("workbench"), null);
/*  27:    */     
/*  28:    */ 
/*  29: 67 */     add(this.m_pythonPanel, "Center");
/*  30:    */   }
/*  31:    */   
/*  32:    */   public boolean requiresLog()
/*  33:    */   {
/*  34: 77 */     return getMainApplication().getApplicationID().equals("workbench");
/*  35:    */   }
/*  36:    */   
/*  37:    */   public boolean acceptsInstances()
/*  38:    */   {
/*  39: 88 */     return true;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void setInstances(Instances instances)
/*  43:    */   {
/*  44:    */     try
/*  45:    */     {
/*  46:100 */       this.m_pythonPanel.sendInstancesToPython(instances);
/*  47:    */     }
/*  48:    */     catch (Exception ex)
/*  49:    */     {
/*  50:102 */       getMainApplication().showErrorDialog(ex);
/*  51:    */     }
/*  52:    */   }
/*  53:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.PythonPerspective
 * JD-Core Version:    0.7.0.1
 */