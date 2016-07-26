/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.event.ActionEvent;
/*   5:    */ import java.awt.event.ActionListener;
/*   6:    */ import java.util.ArrayList;
/*   7:    */ import java.util.List;
/*   8:    */ import javax.swing.JButton;
/*   9:    */ import javax.swing.JPanel;
/*  10:    */ import weka.gui.EvaluationMetricSelectionDialog;
/*  11:    */ import weka.gui.knowledgeflow.GOEStepEditorDialog;
/*  12:    */ import weka.knowledgeflow.steps.ClassifierPerformanceEvaluator;
/*  13:    */ import weka.knowledgeflow.steps.Step;
/*  14:    */ 
/*  15:    */ public class ClassifierPerformanceEvaluatorStepEditorDialog
/*  16:    */   extends GOEStepEditorDialog
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = -4459460141076392499L;
/*  19: 49 */   protected JButton m_showEvalDialog = new JButton("Evaluation metrics...");
/*  20: 52 */   protected List<String> m_evaluationMetrics = new ArrayList();
/*  21:    */   
/*  22:    */   public void setStepToEdit(Step step)
/*  23:    */   {
/*  24: 68 */     copyOriginal(step);
/*  25:    */     
/*  26: 70 */     addPrimaryEditorPanel("North");
/*  27: 71 */     JPanel p = new JPanel(new BorderLayout());
/*  28: 72 */     p.add(this.m_showEvalDialog, "North");
/*  29: 73 */     this.m_primaryEditorHolder.add(p, "Center");
/*  30: 74 */     add(this.m_editorHolder, "North");
/*  31:    */     
/*  32: 76 */     String evalM = ((ClassifierPerformanceEvaluator)step).getEvaluationMetricsToOutput();
/*  33: 78 */     if ((evalM != null) && (evalM.length() > 0))
/*  34:    */     {
/*  35: 79 */       String[] parts = evalM.split(",");
/*  36: 80 */       for (String s : parts) {
/*  37: 81 */         this.m_evaluationMetrics.add(s.trim());
/*  38:    */       }
/*  39:    */     }
/*  40: 85 */     this.m_showEvalDialog.addActionListener(new ActionListener()
/*  41:    */     {
/*  42:    */       public void actionPerformed(ActionEvent e)
/*  43:    */       {
/*  44: 88 */         EvaluationMetricSelectionDialog esd = new EvaluationMetricSelectionDialog(ClassifierPerformanceEvaluatorStepEditorDialog.this.m_parent, ClassifierPerformanceEvaluatorStepEditorDialog.this.m_evaluationMetrics);
/*  45:    */         
/*  46: 90 */         esd.setLocation(ClassifierPerformanceEvaluatorStepEditorDialog.this.m_showEvalDialog.getLocationOnScreen());
/*  47: 91 */         esd.pack();
/*  48: 92 */         esd.setVisible(true);
/*  49: 93 */         ClassifierPerformanceEvaluatorStepEditorDialog.this.m_evaluationMetrics = esd.getSelectedEvalMetrics();
/*  50:    */       }
/*  51:    */     });
/*  52:    */   }
/*  53:    */   
/*  54:    */   protected void okPressed()
/*  55:    */   {
/*  56:103 */     StringBuilder b = new StringBuilder();
/*  57:104 */     for (String s : this.m_evaluationMetrics) {
/*  58:105 */       b.append(s).append(",");
/*  59:    */     }
/*  60:107 */     String newList = b.substring(0, b.length() - 1);
/*  61:108 */     ((ClassifierPerformanceEvaluator)getStepToEdit()).setEvaluationMetricsToOutput(newList);
/*  62:    */   }
/*  63:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.ClassifierPerformanceEvaluatorStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */