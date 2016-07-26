/*   1:    */ package weka.gui.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import javax.swing.BorderFactory;
/*   5:    */ import javax.swing.JPanel;
/*   6:    */ import javax.swing.JScrollPane;
/*   7:    */ import weka.gui.GenericObjectEditor;
/*   8:    */ import weka.gui.PropertySheetPanel;
/*   9:    */ import weka.knowledgeflow.StepManagerImpl;
/*  10:    */ import weka.knowledgeflow.steps.Step;
/*  11:    */ import weka.knowledgeflow.steps.WekaAlgorithmWrapper;
/*  12:    */ 
/*  13:    */ public class GOEStepEditorDialog
/*  14:    */   extends StepEditorDialog
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = -2500973437145276268L;
/*  17:    */   protected StepManagerImpl m_manager;
/*  18:    */   protected Step m_stepOriginal;
/*  19: 56 */   protected PropertySheetPanel m_editor = new PropertySheetPanel();
/*  20:    */   protected PropertySheetPanel m_secondaryEditor;
/*  21: 65 */   protected JPanel m_editorHolder = new JPanel();
/*  22: 68 */   protected JPanel m_primaryEditorHolder = new JPanel();
/*  23:    */   
/*  24:    */   protected void setStepToEdit(Step step)
/*  25:    */   {
/*  26: 87 */     copyOriginal(step);
/*  27:    */     
/*  28: 89 */     addPrimaryEditorPanel("North");
/*  29: 90 */     addSecondaryEditorPanel("South");
/*  30:    */     
/*  31: 92 */     JScrollPane scrollPane = new JScrollPane(this.m_editorHolder);
/*  32: 93 */     add(scrollPane, "Center");
/*  33: 95 */     if (step.getDefaultSettings() != null) {
/*  34: 96 */       addSettingsButton();
/*  35:    */     }
/*  36: 99 */     layoutEditor();
/*  37:    */   }
/*  38:    */   
/*  39:    */   protected void copyOriginal(Step step)
/*  40:    */   {
/*  41:108 */     this.m_manager = ((StepManagerImpl)step.getStepManager());
/*  42:109 */     this.m_stepToEdit = step;
/*  43:    */     try
/*  44:    */     {
/*  45:112 */       this.m_stepOriginal = ((Step)GenericObjectEditor.makeCopy(step));
/*  46:    */     }
/*  47:    */     catch (Exception ex)
/*  48:    */     {
/*  49:114 */       showErrorDialog(ex);
/*  50:    */     }
/*  51:    */   }
/*  52:    */   
/*  53:    */   protected void addPrimaryEditorPanel(String borderLayoutPos)
/*  54:    */   {
/*  55:125 */     String className = (this.m_stepToEdit instanceof WekaAlgorithmWrapper) ? ((WekaAlgorithmWrapper)this.m_stepToEdit).getWrappedAlgorithm().getClass().getName() : this.m_stepToEdit.getClass().getName();
/*  56:    */     
/*  57:    */ 
/*  58:    */ 
/*  59:    */ 
/*  60:130 */     className = className.substring(className.lastIndexOf('.') + 1, className.length());
/*  61:    */     
/*  62:    */ 
/*  63:133 */     this.m_primaryEditorHolder.setLayout(new BorderLayout());
/*  64:    */     
/*  65:135 */     this.m_primaryEditorHolder.setBorder(BorderFactory.createTitledBorder(className + " options"));
/*  66:    */     
/*  67:137 */     this.m_editor.setUseEnvironmentPropertyEditors(true);
/*  68:138 */     this.m_editor.setEnvironment(this.m_env);
/*  69:139 */     this.m_editor.setTarget((this.m_stepToEdit instanceof WekaAlgorithmWrapper) ? ((WekaAlgorithmWrapper)this.m_stepToEdit).getWrappedAlgorithm() : this.m_stepToEdit);
/*  70:    */     
/*  71:    */ 
/*  72:142 */     this.m_editorHolder.setLayout(new BorderLayout());
/*  73:143 */     if ((this.m_editor.editableProperties() > 0) || (this.m_editor.hasCustomizer()))
/*  74:    */     {
/*  75:144 */       this.m_primaryEditorHolder.add(this.m_editor, "North");
/*  76:145 */       this.m_editorHolder.add(this.m_primaryEditorHolder, borderLayoutPos);
/*  77:    */     }
/*  78:    */     else
/*  79:    */     {
/*  80:147 */       JPanel about = this.m_editor.getAboutPanel();
/*  81:148 */       this.m_editorHolder.add(about, borderLayoutPos);
/*  82:    */     }
/*  83:    */   }
/*  84:    */   
/*  85:    */   protected void addSecondaryEditorPanel(String borderLayoutPos)
/*  86:    */   {
/*  87:159 */     if ((this.m_stepToEdit instanceof WekaAlgorithmWrapper))
/*  88:    */     {
/*  89:160 */       this.m_secondaryEditor = new PropertySheetPanel(false);
/*  90:161 */       this.m_secondaryEditor.setUseEnvironmentPropertyEditors(true);
/*  91:162 */       this.m_secondaryEditor.setBorder(BorderFactory.createTitledBorder("Additional options"));
/*  92:    */       
/*  93:164 */       this.m_secondaryEditor.setEnvironment(this.m_env);
/*  94:165 */       this.m_secondaryEditor.setTarget(this.m_stepToEdit);
/*  95:166 */       if ((this.m_secondaryEditor.editableProperties() > 0) || (this.m_secondaryEditor.hasCustomizer()))
/*  96:    */       {
/*  97:168 */         JPanel p = new JPanel();
/*  98:169 */         p.setLayout(new BorderLayout());
/*  99:170 */         p.add(this.m_secondaryEditor, "North");
/* 100:171 */         this.m_editorHolder.add(p, borderLayoutPos);
/* 101:    */       }
/* 102:    */     }
/* 103:    */   }
/* 104:    */   
/* 105:    */   protected void cancelPressed()
/* 106:    */   {
/* 107:182 */     if ((this.m_stepOriginal != null) && (this.m_manager != null)) {
/* 108:183 */       this.m_manager.setManagedStep(this.m_stepOriginal);
/* 109:    */     }
/* 110:    */   }
/* 111:    */   
/* 112:    */   protected void okPressed()
/* 113:    */   {
/* 114:192 */     if (this.m_editor.hasCustomizer()) {
/* 115:193 */       this.m_editor.closingOK();
/* 116:    */     }
/* 117:    */   }
/* 118:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.GOEStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */