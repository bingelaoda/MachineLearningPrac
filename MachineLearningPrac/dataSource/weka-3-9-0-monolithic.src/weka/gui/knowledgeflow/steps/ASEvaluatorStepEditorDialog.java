/*  1:   */ package weka.gui.knowledgeflow.steps;
/*  2:   */ 
/*  3:   */ import java.awt.BorderLayout;
/*  4:   */ import javax.swing.JCheckBox;
/*  5:   */ import javax.swing.JPanel;
/*  6:   */ import weka.gui.knowledgeflow.GOEStepEditorDialog;
/*  7:   */ import weka.knowledgeflow.steps.ASEvaluator;
/*  8:   */ import weka.knowledgeflow.steps.Step;
/*  9:   */ 
/* 10:   */ public class ASEvaluatorStepEditorDialog
/* 11:   */   extends GOEStepEditorDialog
/* 12:   */ {
/* 13:   */   private static final long serialVersionUID = -166234654984288982L;
/* 14:42 */   protected JCheckBox m_treatXValFoldsSeparately = new JCheckBox("Treat x-val folds separately");
/* 15:   */   
/* 16:   */   public void setStepToEdit(Step step)
/* 17:   */   {
/* 18:52 */     copyOriginal(step);
/* 19:53 */     addPrimaryEditorPanel("North");
/* 20:   */     
/* 21:55 */     JPanel p = new JPanel(new BorderLayout());
/* 22:56 */     p.add(this.m_treatXValFoldsSeparately, "North");
/* 23:57 */     this.m_primaryEditorHolder.add(p, "Center");
/* 24:   */     
/* 25:59 */     add(this.m_editorHolder, "Center");
/* 26:60 */     this.m_treatXValFoldsSeparately.setSelected(((ASEvaluator)step).getTreatXValFoldsSeparately());
/* 27:   */   }
/* 28:   */   
/* 29:   */   protected void okPressed()
/* 30:   */   {
/* 31:69 */     ((ASEvaluator)this.m_stepToEdit).setTreatXValFoldsSeparately(this.m_treatXValFoldsSeparately.isSelected());
/* 32:   */   }
/* 33:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.ASEvaluatorStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */