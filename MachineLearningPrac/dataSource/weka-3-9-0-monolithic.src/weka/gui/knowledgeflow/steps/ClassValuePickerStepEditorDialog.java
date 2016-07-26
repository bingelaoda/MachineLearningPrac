/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import javax.swing.BorderFactory;
/*   5:    */ import javax.swing.JComboBox;
/*   6:    */ import javax.swing.JLabel;
/*   7:    */ import javax.swing.JPanel;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.WekaException;
/*  11:    */ import weka.gui.EnvironmentField.WideComboBox;
/*  12:    */ import weka.gui.knowledgeflow.GOEStepEditorDialog;
/*  13:    */ import weka.knowledgeflow.StepManager;
/*  14:    */ import weka.knowledgeflow.steps.ClassValuePicker;
/*  15:    */ import weka.knowledgeflow.steps.Step;
/*  16:    */ 
/*  17:    */ public class ClassValuePickerStepEditorDialog
/*  18:    */   extends GOEStepEditorDialog
/*  19:    */ {
/*  20:    */   private static final long serialVersionUID = 7009335918650499183L;
/*  21: 49 */   protected JComboBox m_classValueCombo = new EnvironmentField.WideComboBox();
/*  22:    */   
/*  23:    */   public void setStepToEdit(Step step)
/*  24:    */   {
/*  25: 59 */     copyOriginal(step);
/*  26:    */     
/*  27: 61 */     Instances incomingStructure = null;
/*  28:    */     try
/*  29:    */     {
/*  30: 64 */       incomingStructure = step.getStepManager().getIncomingStructureForConnectionType("dataSet");
/*  31: 66 */       if (incomingStructure == null) {
/*  32: 67 */         incomingStructure = step.getStepManager().getIncomingStructureForConnectionType("trainingSet");
/*  33:    */       }
/*  34: 70 */       if (incomingStructure == null) {
/*  35: 71 */         incomingStructure = step.getStepManager().getIncomingStructureForConnectionType("testSet");
/*  36:    */       }
/*  37: 74 */       if (incomingStructure == null) {
/*  38: 75 */         incomingStructure = step.getStepManager().getIncomingStructureForConnectionType("instance");
/*  39:    */       }
/*  40:    */     }
/*  41:    */     catch (WekaException ex)
/*  42:    */     {
/*  43: 79 */       showErrorDialog(ex);
/*  44:    */     }
/*  45: 82 */     if (incomingStructure != null)
/*  46:    */     {
/*  47: 84 */       if (incomingStructure.classIndex() < 0)
/*  48:    */       {
/*  49: 85 */         showInfoDialog("No class attribute is set in the data", "ClassValuePicker", true);
/*  50:    */         
/*  51: 87 */         add(new JLabel("No class attribute set in data"), "Center");
/*  52:    */       }
/*  53: 88 */       else if (incomingStructure.classAttribute().isNumeric())
/*  54:    */       {
/*  55: 89 */         showInfoDialog("Cant set class value - class is numeric!", "ClassValuePicker", true);
/*  56:    */         
/*  57: 91 */         add(new JLabel("Can't set class value - class is numeric"), "Center");
/*  58:    */       }
/*  59:    */       else
/*  60:    */       {
/*  61: 94 */         this.m_classValueCombo.setEditable(true);
/*  62: 95 */         this.m_classValueCombo.setToolTipText("Class label. /first, /last and /<num> can be used to specify the first, last or specific index of the label to use respectively");
/*  63:100 */         for (int i = 0; i < incomingStructure.classAttribute().numValues(); i++) {
/*  64:102 */           this.m_classValueCombo.addItem(incomingStructure.classAttribute().value(i));
/*  65:    */         }
/*  66:105 */         String stepL = ((ClassValuePicker)getStepToEdit()).getClassValue();
/*  67:106 */         if ((stepL != null) && (stepL.length() > 0)) {
/*  68:107 */           this.m_classValueCombo.setSelectedItem(stepL);
/*  69:    */         }
/*  70:110 */         JPanel p = new JPanel(new BorderLayout());
/*  71:111 */         p.setBorder(BorderFactory.createTitledBorder("Choose class value"));
/*  72:112 */         p.add(this.m_classValueCombo, "North");
/*  73:    */         
/*  74:114 */         createAboutPanel(step);
/*  75:115 */         add(p, "Center");
/*  76:    */       }
/*  77:    */     }
/*  78:    */     else {
/*  79:118 */       super.setStepToEdit(step);
/*  80:    */     }
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void okPressed()
/*  84:    */   {
/*  85:127 */     Object selected = this.m_classValueCombo.getSelectedItem();
/*  86:129 */     if (selected != null) {
/*  87:130 */       ((ClassValuePicker)getStepToEdit()).setClassValue(selected.toString());
/*  88:    */     }
/*  89:    */   }
/*  90:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.ClassValuePickerStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */