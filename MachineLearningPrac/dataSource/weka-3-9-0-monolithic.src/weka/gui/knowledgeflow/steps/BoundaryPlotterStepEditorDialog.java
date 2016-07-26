/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.GridLayout;
/*   5:    */ import javax.swing.JComboBox;
/*   6:    */ import javax.swing.JPanel;
/*   7:    */ import javax.swing.border.TitledBorder;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.WekaException;
/*  11:    */ import weka.gui.EnvironmentField;
/*  12:    */ import weka.gui.knowledgeflow.GOEStepEditorDialog;
/*  13:    */ import weka.knowledgeflow.StepManager;
/*  14:    */ import weka.knowledgeflow.steps.BoundaryPlotter;
/*  15:    */ 
/*  16:    */ public class BoundaryPlotterStepEditorDialog
/*  17:    */   extends GOEStepEditorDialog
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = 4351742205211273840L;
/*  20: 49 */   protected JComboBox<String> m_xCombo = new JComboBox();
/*  21: 55 */   protected JComboBox<String> m_yCombo = new JComboBox();
/*  22: 61 */   protected EnvironmentField m_xEnviro = new EnvironmentField();
/*  23: 68 */   protected EnvironmentField m_yEnviro = new EnvironmentField();
/*  24:    */   
/*  25:    */   public void layoutEditor()
/*  26:    */   {
/*  27: 78 */     this.m_xCombo.setEditable(true);
/*  28: 79 */     this.m_yCombo.setEditable(true);
/*  29:    */     
/*  30: 81 */     BoundaryPlotter step = (BoundaryPlotter)getStepToEdit();
/*  31: 82 */     Instances incomingStructure = null;
/*  32:    */     try
/*  33:    */     {
/*  34: 84 */       incomingStructure = step.getStepManager().getIncomingStructureForConnectionType("dataSet");
/*  35: 87 */       if (incomingStructure == null) {
/*  36: 88 */         incomingStructure = step.getStepManager().getIncomingStructureForConnectionType("trainingSet");
/*  37:    */       }
/*  38: 92 */       if (incomingStructure == null) {
/*  39: 93 */         incomingStructure = step.getStepManager().getIncomingStructureForConnectionType("testSet");
/*  40:    */       }
/*  41: 97 */       if (incomingStructure == null) {
/*  42: 98 */         incomingStructure = step.getStepManager().getIncomingStructureForConnectionType("instance");
/*  43:    */       }
/*  44:    */     }
/*  45:    */     catch (WekaException ex)
/*  46:    */     {
/*  47:103 */       showErrorDialog(ex);
/*  48:    */     }
/*  49:106 */     JPanel attPan = new JPanel(new GridLayout(1, 2));
/*  50:107 */     JPanel xHolder = new JPanel(new BorderLayout());
/*  51:108 */     JPanel yHolder = new JPanel(new BorderLayout());
/*  52:109 */     xHolder.setBorder(new TitledBorder("X axis"));
/*  53:110 */     yHolder.setBorder(new TitledBorder("Y axis"));
/*  54:111 */     attPan.add(xHolder);
/*  55:112 */     attPan.add(yHolder);
/*  56:114 */     if (incomingStructure != null)
/*  57:    */     {
/*  58:115 */       this.m_xEnviro = null;
/*  59:116 */       this.m_yEnviro = null;
/*  60:117 */       xHolder.add(this.m_xCombo, "Center");
/*  61:118 */       yHolder.add(this.m_yCombo, "Center");
/*  62:119 */       String xAttN = step.getXAttName();
/*  63:120 */       String yAttN = step.getYAttName();
/*  64:    */       
/*  65:    */ 
/*  66:123 */       int numAdded = 0;
/*  67:124 */       for (int i = 0; i < incomingStructure.numAttributes(); i++)
/*  68:    */       {
/*  69:125 */         Attribute att = incomingStructure.attribute(i);
/*  70:126 */         if (att.isNumeric())
/*  71:    */         {
/*  72:127 */           this.m_xCombo.addItem(att.name());
/*  73:128 */           this.m_yCombo.addItem(att.name());
/*  74:129 */           numAdded++;
/*  75:    */         }
/*  76:    */       }
/*  77:132 */       attPan.add(xHolder);
/*  78:133 */       attPan.add(yHolder);
/*  79:135 */       if (numAdded < 2)
/*  80:    */       {
/*  81:136 */         showInfoDialog("There are not enough numeric attributes in the incoming data to visualize with", "Not enough attributes available", true);
/*  82:    */       }
/*  83:    */       else
/*  84:    */       {
/*  85:141 */         if ((xAttN != null) && (xAttN.length() > 0)) {
/*  86:142 */           this.m_xCombo.setSelectedItem(xAttN);
/*  87:    */         }
/*  88:144 */         if ((yAttN != null) && (yAttN.length() > 0)) {
/*  89:145 */           this.m_yCombo.setSelectedItem(yAttN);
/*  90:    */         }
/*  91:    */       }
/*  92:    */     }
/*  93:    */     else
/*  94:    */     {
/*  95:149 */       this.m_xCombo = null;
/*  96:150 */       this.m_yCombo = null;
/*  97:151 */       xHolder.add(this.m_xEnviro, "Center");
/*  98:152 */       yHolder.add(this.m_yEnviro, "Center");
/*  99:153 */       this.m_xEnviro.setText(step.getXAttName());
/* 100:154 */       this.m_yEnviro.setText(step.getYAttName());
/* 101:    */     }
/* 102:157 */     this.m_editorHolder.add(attPan, "South");
/* 103:    */   }
/* 104:    */   
/* 105:    */   public void okPressed()
/* 106:    */   {
/* 107:165 */     String xName = this.m_xCombo != null ? this.m_xCombo.getSelectedItem().toString() : this.m_xEnviro.getText();
/* 108:    */     
/* 109:    */ 
/* 110:168 */     String yName = this.m_yCombo != null ? this.m_yCombo.getSelectedItem().toString() : this.m_yEnviro.getText();
/* 111:    */     
/* 112:    */ 
/* 113:171 */     ((BoundaryPlotter)getStepToEdit()).setXAttName(xName);
/* 114:172 */     ((BoundaryPlotter)getStepToEdit()).setYAttName(yName);
/* 115:    */   }
/* 116:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.BoundaryPlotterStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */