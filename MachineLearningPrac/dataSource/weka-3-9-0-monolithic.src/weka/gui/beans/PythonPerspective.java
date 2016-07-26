/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import javax.swing.Icon;
/*   5:    */ import javax.swing.JPanel;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.gui.PythonPanel;
/*   8:    */ 
/*   9:    */ public class PythonPerspective
/*  10:    */   extends JPanel
/*  11:    */   implements KnowledgeFlowApp.KFPerspective
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = 8089719525675841189L;
/*  14:    */   protected PythonPanel m_pythonPanel;
/*  15:    */   
/*  16:    */   public PythonPerspective()
/*  17:    */   {
/*  18: 51 */     setLayout(new BorderLayout());
/*  19: 52 */     this.m_pythonPanel = new PythonPanel(true, null);
/*  20: 53 */     add(this.m_pythonPanel, "Center");
/*  21:    */   }
/*  22:    */   
/*  23:    */   public void setInstances(Instances insts)
/*  24:    */     throws Exception
/*  25:    */   {
/*  26: 65 */     this.m_pythonPanel.sendInstancesToPython(insts);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public boolean acceptsInstances()
/*  30:    */   {
/*  31: 75 */     return true;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public String getPerspectiveTitle()
/*  35:    */   {
/*  36: 85 */     return "CPython scripting";
/*  37:    */   }
/*  38:    */   
/*  39:    */   public String getPerspectiveTipText()
/*  40:    */   {
/*  41: 95 */     return "Write and execute Python scripts";
/*  42:    */   }
/*  43:    */   
/*  44:    */   public Icon getPerspectiveIcon()
/*  45:    */   {
/*  46:105 */     return PythonPanel.loadIcon("weka/gui/beans/icons/python-logo_small.png");
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setActive(boolean active) {}
/*  50:    */   
/*  51:    */   public void setLoaded(boolean loaded) {}
/*  52:    */   
/*  53:    */   public void setMainKFPerspective(KnowledgeFlowApp.MainKFPerspective main) {}
/*  54:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.PythonPerspective
 * JD-Core Version:    0.7.0.1
 */