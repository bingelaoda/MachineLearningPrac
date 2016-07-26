/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.util.Map;
/*   5:    */ import java.util.Map.Entry;
/*   6:    */ import javax.swing.BorderFactory;
/*   7:    */ import javax.swing.JPanel;
/*   8:    */ import javax.swing.JTable;
/*   9:    */ import javax.swing.table.TableModel;
/*  10:    */ import weka.gui.InteractiveTableModel;
/*  11:    */ import weka.gui.InteractiveTablePanel;
/*  12:    */ import weka.gui.knowledgeflow.StepEditorDialog;
/*  13:    */ import weka.knowledgeflow.steps.SetVariables;
/*  14:    */ 
/*  15:    */ public class SetVariablesStepEditorDialog
/*  16:    */   extends StepEditorDialog
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = 5747505623497131129L;
/*  19:    */   protected VariablesPanel m_vp;
/*  20:    */   
/*  21:    */   protected void layoutEditor()
/*  22:    */   {
/*  23: 50 */     String internalRep = ((SetVariables)getStepToEdit()).getVarsInternalRep();
/*  24: 51 */     Map<String, String> vars = SetVariables.internalToMap(internalRep);
/*  25:    */     
/*  26: 53 */     this.m_vp = new VariablesPanel(vars);
/*  27: 54 */     add(this.m_vp, "Center");
/*  28:    */   }
/*  29:    */   
/*  30:    */   protected void okPressed()
/*  31:    */   {
/*  32: 59 */     String vi = this.m_vp.getVariablesInternal();
/*  33: 60 */     ((SetVariables)getStepToEdit()).setVarsInternalRep(this.m_vp.getVariablesInternal());
/*  34:    */   }
/*  35:    */   
/*  36:    */   protected static class VariablesPanel
/*  37:    */     extends JPanel
/*  38:    */   {
/*  39:    */     private static final long serialVersionUID = 5188290550108775006L;
/*  40: 70 */     protected InteractiveTablePanel m_table = new InteractiveTablePanel(new String[] { "Variable", "Value", "" });
/*  41:    */     
/*  42:    */     public VariablesPanel(Map<String, String> vars)
/*  43:    */     {
/*  44: 74 */       setLayout(new BorderLayout());
/*  45: 75 */       setBorder(BorderFactory.createTitledBorder("Variables to set"));
/*  46: 76 */       add(this.m_table, "Center");
/*  47:    */       
/*  48:    */ 
/*  49: 79 */       int row = 0;
/*  50: 80 */       JTable table = this.m_table.getTable();
/*  51: 81 */       for (Map.Entry<String, String> e : vars.entrySet())
/*  52:    */       {
/*  53: 82 */         String varName = (String)e.getKey();
/*  54: 83 */         String varVal = (String)e.getValue();
/*  55: 85 */         if ((varVal != null) && (varVal.length() > 0))
/*  56:    */         {
/*  57: 86 */           table.getModel().setValueAt(varName, row, 0);
/*  58: 87 */           table.getModel().setValueAt(varVal, row, 1);
/*  59: 88 */           ((InteractiveTableModel)table.getModel()).addEmptyRow();
/*  60: 89 */           row++;
/*  61:    */         }
/*  62:    */       }
/*  63:    */     }
/*  64:    */     
/*  65:    */     public String getVariablesInternal()
/*  66:    */     {
/*  67:100 */       StringBuilder b = new StringBuilder();
/*  68:101 */       JTable table = this.m_table.getTable();
/*  69:102 */       int numRows = table.getModel().getRowCount();
/*  70:104 */       for (int i = 0; i < numRows; i++)
/*  71:    */       {
/*  72:105 */         String paramName = table.getValueAt(i, 0).toString();
/*  73:106 */         String paramValue = table.getValueAt(i, 1).toString();
/*  74:107 */         if ((paramName.length() > 0) && (paramValue.length() > 0)) {
/*  75:108 */           b.append(paramName).append("@v@v").append(paramValue);
/*  76:    */         }
/*  77:110 */         if (i < numRows - 1) {
/*  78:111 */           b.append("@@vv@@");
/*  79:    */         }
/*  80:    */       }
/*  81:115 */       return b.toString();
/*  82:    */     }
/*  83:    */   }
/*  84:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.SetVariablesStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */