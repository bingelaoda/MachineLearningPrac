/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.GridLayout;
/*   5:    */ import java.beans.PropertyChangeEvent;
/*   6:    */ import java.beans.PropertyChangeListener;
/*   7:    */ import javax.swing.JLabel;
/*   8:    */ import javax.swing.JPanel;
/*   9:    */ import weka.core.converters.FileSourcedConverter;
/*  10:    */ import weka.gui.EnvironmentField;
/*  11:    */ import weka.gui.FileEnvironmentField;
/*  12:    */ import weka.gui.PropertySheetPanel;
/*  13:    */ import weka.gui.knowledgeflow.GOEStepEditorDialog;
/*  14:    */ import weka.knowledgeflow.steps.Step;
/*  15:    */ 
/*  16:    */ public class SaverStepEditorDialog
/*  17:    */   extends GOEStepEditorDialog
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = -8353826767500440827L;
/*  20:    */   protected EnvironmentField m_prefixOrFile;
/*  21:    */   protected FileEnvironmentField m_directory;
/*  22: 57 */   protected JLabel m_dirLab = new JLabel("Directory ", 4);
/*  23: 60 */   protected JLabel m_prefLab = new JLabel("Prefix ", 4);
/*  24:    */   
/*  25:    */   public void setStepToEdit(Step step)
/*  26:    */   {
/*  27: 75 */     copyOriginal(step);
/*  28:    */     
/*  29: 77 */     weka.knowledgeflow.steps.Saver wrappedStep = (weka.knowledgeflow.steps.Saver)step;
/*  30: 78 */     if ((wrappedStep.getSaver() instanceof FileSourcedConverter)) {
/*  31: 79 */       setupFileSaver(wrappedStep);
/*  32:    */     } else {
/*  33: 81 */       super.setStepToEdit(step);
/*  34:    */     }
/*  35:    */   }
/*  36:    */   
/*  37:    */   protected void setupFileSaver(final weka.knowledgeflow.steps.Saver wrappedStep)
/*  38:    */   {
/*  39: 91 */     addPrimaryEditorPanel("North");
/*  40: 92 */     this.m_prefixOrFile = new EnvironmentField();
/*  41: 93 */     this.m_prefixOrFile.setEnvironment(this.m_env);
/*  42: 94 */     this.m_directory = new FileEnvironmentField("", 1, true);
/*  43: 95 */     this.m_directory.setEnvironment(this.m_env);
/*  44: 96 */     JPanel p = new JPanel();
/*  45: 97 */     p.setLayout(new BorderLayout());
/*  46: 98 */     this.m_secondaryEditor = new PropertySheetPanel(false);
/*  47: 99 */     this.m_secondaryEditor.setEnvironment(this.m_env);
/*  48:100 */     this.m_secondaryEditor.setTarget(this.m_stepToEdit);
/*  49:101 */     p.add(this.m_secondaryEditor, "North");
/*  50:102 */     JPanel tp = new JPanel();
/*  51:103 */     tp.setLayout(new BorderLayout());
/*  52:104 */     JPanel dp = new JPanel();
/*  53:105 */     dp.setLayout(new GridLayout(0, 1));
/*  54:106 */     dp.add(this.m_dirLab);
/*  55:107 */     dp.add(this.m_prefLab);
/*  56:108 */     JPanel dp2 = new JPanel();
/*  57:109 */     dp2.setLayout(new GridLayout(0, 1));
/*  58:110 */     dp2.add(this.m_directory);
/*  59:111 */     dp2.add(this.m_prefixOrFile);
/*  60:112 */     tp.add(dp, "West");
/*  61:113 */     tp.add(dp2, "Center");
/*  62:114 */     p.add(tp, "Center");
/*  63:    */     
/*  64:116 */     this.m_primaryEditorHolder.add(p, "Center");
/*  65:117 */     add(this.m_editorHolder, "Center");
/*  66:    */     try
/*  67:    */     {
/*  68:120 */       String dir = wrappedStep.getSaver().retrieveDir();
/*  69:121 */       String prefixOrFile = wrappedStep.getSaver().filePrefix();
/*  70:122 */       this.m_directory.setText(dir);
/*  71:123 */       this.m_prefixOrFile.setText(prefixOrFile);
/*  72:    */     }
/*  73:    */     catch (Exception e)
/*  74:    */     {
/*  75:125 */       e.printStackTrace();
/*  76:    */     }
/*  77:128 */     this.m_secondaryEditor.addPropertyChangeListener(new PropertyChangeListener()
/*  78:    */     {
/*  79:    */       public void propertyChange(PropertyChangeEvent evt)
/*  80:    */       {
/*  81:131 */         if (wrappedStep.getRelationNameForFilename())
/*  82:    */         {
/*  83:132 */           if (SaverStepEditorDialog.this.m_prefLab.getText().startsWith("File")) {
/*  84:133 */             SaverStepEditorDialog.this.m_prefLab.setText("Prefix ");
/*  85:    */           }
/*  86:    */         }
/*  87:136 */         else if (SaverStepEditorDialog.this.m_prefLab.getText().startsWith("Prefix")) {
/*  88:137 */           SaverStepEditorDialog.this.m_prefLab.setText("Filename ");
/*  89:    */         }
/*  90:    */       }
/*  91:    */     });
/*  92:    */   }
/*  93:    */   
/*  94:    */   protected void setupOther()
/*  95:    */   {
/*  96:148 */     addPrimaryEditorPanel("North");
/*  97:149 */     addSecondaryEditorPanel("Center");
/*  98:150 */     add(this.m_editorHolder, "Center");
/*  99:    */   }
/* 100:    */   
/* 101:    */   protected void okPressed()
/* 102:    */   {
/* 103:158 */     if ((((weka.knowledgeflow.steps.Saver)this.m_stepToEdit).getSaver() instanceof FileSourcedConverter)) {
/* 104:    */       try
/* 105:    */       {
/* 106:160 */         ((weka.knowledgeflow.steps.Saver)this.m_stepToEdit).getSaver().setDir(this.m_directory.getText());
/* 107:161 */         ((weka.knowledgeflow.steps.Saver)this.m_stepToEdit).getSaver().setFilePrefix(this.m_prefixOrFile.getText());
/* 108:    */       }
/* 109:    */       catch (Exception e)
/* 110:    */       {
/* 111:164 */         e.printStackTrace();
/* 112:    */       }
/* 113:    */     }
/* 114:167 */     super.okPressed();
/* 115:    */   }
/* 116:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.SaverStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */