/*   1:    */ package weka.gui.explorer;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import javax.swing.JComponent;
/*   5:    */ import javax.swing.JPanel;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.WekaException;
/*   8:    */ import weka.gui.Logger;
/*   9:    */ import weka.gui.PythonPanel;
/*  10:    */ 
/*  11:    */ public class PythonExplorerPanel
/*  12:    */   extends JPanel
/*  13:    */   implements Explorer.ExplorerPanel, Explorer.LogHandler
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = -340790747195368260L;
/*  16:    */   protected Explorer m_explorer;
/*  17:    */   PythonPanel m_pythonPanel;
/*  18:    */   
/*  19:    */   public PythonExplorerPanel()
/*  20:    */   {
/*  21: 56 */     setLayout(new BorderLayout());
/*  22:    */   }
/*  23:    */   
/*  24:    */   protected void setup()
/*  25:    */   {
/*  26: 63 */     this.m_pythonPanel = new PythonPanel(false, this.m_explorer);
/*  27: 64 */     add(this.m_pythonPanel, "Center");
/*  28:    */   }
/*  29:    */   
/*  30:    */   public void setExplorer(Explorer parent)
/*  31:    */   {
/*  32: 74 */     this.m_explorer = parent;
/*  33: 75 */     setup();
/*  34:    */   }
/*  35:    */   
/*  36:    */   public Explorer getExplorer()
/*  37:    */   {
/*  38: 85 */     return this.m_explorer;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setInstances(Instances inst)
/*  42:    */   {
/*  43: 95 */     if (this.m_pythonPanel != null) {
/*  44:    */       try
/*  45:    */       {
/*  46: 97 */         this.m_pythonPanel.sendInstancesToPython(inst);
/*  47:    */       }
/*  48:    */       catch (WekaException ex)
/*  49:    */       {
/*  50: 99 */         ex.printStackTrace();
/*  51:    */       }
/*  52:    */     }
/*  53:    */   }
/*  54:    */   
/*  55:    */   public String getTabTitle()
/*  56:    */   {
/*  57:111 */     return "CPython Scripting";
/*  58:    */   }
/*  59:    */   
/*  60:    */   public String getTabTitleToolTip()
/*  61:    */   {
/*  62:121 */     return "Write and execute Python scripts";
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void setLog(Logger newLog)
/*  66:    */   {
/*  67:131 */     if (((newLog instanceof JComponent)) && (this.m_pythonPanel != null)) {
/*  68:132 */       this.m_pythonPanel.setLogger(newLog);
/*  69:    */     }
/*  70:    */   }
/*  71:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.explorer.PythonExplorerPanel
 * JD-Core Version:    0.7.0.1
 */