/*  1:   */ package weka.gui.knowledgeflow.steps;
/*  2:   */ 
/*  3:   */ import java.awt.BorderLayout;
/*  4:   */ import java.util.List;
/*  5:   */ import javax.swing.BorderFactory;
/*  6:   */ import javax.swing.JComboBox;
/*  7:   */ import javax.swing.JPanel;
/*  8:   */ import weka.gui.knowledgeflow.MainKFPerspective;
/*  9:   */ import weka.gui.knowledgeflow.StepEditorDialog;
/* 10:   */ import weka.gui.knowledgeflow.VisibleLayout;
/* 11:   */ import weka.knowledgeflow.Flow;
/* 12:   */ import weka.knowledgeflow.StepManager;
/* 13:   */ import weka.knowledgeflow.StepManagerImpl;
/* 14:   */ import weka.knowledgeflow.steps.Block;
/* 15:   */ import weka.knowledgeflow.steps.Step;
/* 16:   */ 
/* 17:   */ public class BlockStepEditorDialog
/* 18:   */   extends StepEditorDialog
/* 19:   */ {
/* 20:   */   private static final long serialVersionUID = 1183316309880876170L;
/* 21:46 */   protected JComboBox<String> m_stepToBlockBox = new JComboBox();
/* 22:   */   
/* 23:   */   public void layoutEditor()
/* 24:   */   {
/* 25:53 */     this.m_stepToBlockBox.setEditable(true);
/* 26:   */     
/* 27:55 */     StepManager sm = getStepToEdit().getStepManager();
/* 28:56 */     List<StepManagerImpl> flowSteps = getMainPerspective().getCurrentLayout().getFlow().getSteps();
/* 29:58 */     for (StepManagerImpl smi : flowSteps) {
/* 30:59 */       this.m_stepToBlockBox.addItem(smi.getName());
/* 31:   */     }
/* 32:62 */     JPanel p = new JPanel(new BorderLayout());
/* 33:63 */     p.setBorder(BorderFactory.createTitledBorder("Choose step to wait for"));
/* 34:64 */     p.add(this.m_stepToBlockBox, "North");
/* 35:   */     
/* 36:66 */     add(p, "Center");
/* 37:   */     
/* 38:68 */     String userSelected = ((Block)getStepToEdit()).getStepToWaitFor();
/* 39:69 */     if (userSelected != null) {
/* 40:70 */       this.m_stepToBlockBox.setSelectedItem(userSelected);
/* 41:   */     }
/* 42:   */   }
/* 43:   */   
/* 44:   */   public void okPressed()
/* 45:   */   {
/* 46:80 */     String selected = (String)this.m_stepToBlockBox.getSelectedItem();
/* 47:81 */     ((Block)getStepToEdit()).setStepToWaitFor(selected);
/* 48:   */   }
/* 49:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.BlockStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */