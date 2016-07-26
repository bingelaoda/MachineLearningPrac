/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import javax.swing.BorderFactory;
/*   5:    */ import javax.swing.JComboBox;
/*   6:    */ import javax.swing.JPanel;
/*   7:    */ import weka.core.Attribute;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.WekaException;
/*  10:    */ import weka.gui.knowledgeflow.GOEStepEditorDialog;
/*  11:    */ import weka.knowledgeflow.StepManager;
/*  12:    */ import weka.knowledgeflow.steps.ClassAssigner;
/*  13:    */ import weka.knowledgeflow.steps.Step;
/*  14:    */ 
/*  15:    */ public class ClassAssignerStepEditorDialog
/*  16:    */   extends GOEStepEditorDialog
/*  17:    */ {
/*  18:    */   private static final long serialVersionUID = 3105898651212196539L;
/*  19: 45 */   protected JComboBox<String> m_classCombo = new JComboBox();
/*  20:    */   
/*  21:    */   public void setStepToEdit(Step step)
/*  22:    */   {
/*  23: 54 */     copyOriginal(step);
/*  24:    */     
/*  25: 56 */     Instances incomingStructure = null;
/*  26:    */     try
/*  27:    */     {
/*  28: 58 */       incomingStructure = step.getStepManager().getIncomingStructureForConnectionType("dataSet");
/*  29: 61 */       if (incomingStructure == null) {
/*  30: 62 */         incomingStructure = step.getStepManager().getIncomingStructureForConnectionType("trainingSet");
/*  31:    */       }
/*  32: 66 */       if (incomingStructure == null) {
/*  33: 67 */         incomingStructure = step.getStepManager().getIncomingStructureForConnectionType("testSet");
/*  34:    */       }
/*  35: 71 */       if (incomingStructure == null) {
/*  36: 72 */         incomingStructure = step.getStepManager().getIncomingStructureForConnectionType("instance");
/*  37:    */       }
/*  38:    */     }
/*  39:    */     catch (WekaException ex)
/*  40:    */     {
/*  41: 77 */       showErrorDialog(ex);
/*  42:    */     }
/*  43: 80 */     if (incomingStructure != null)
/*  44:    */     {
/*  45: 81 */       this.m_classCombo.setEditable(true);
/*  46: 82 */       for (int i = 0; i < incomingStructure.numAttributes(); i++)
/*  47:    */       {
/*  48: 83 */         Attribute a = incomingStructure.attribute(i);
/*  49: 84 */         String attN = "(" + Attribute.typeToStringShort(a) + ") " + a.name();
/*  50: 85 */         this.m_classCombo.addItem(attN);
/*  51:    */       }
/*  52: 88 */       setComboToClass(incomingStructure);
/*  53:    */       
/*  54: 90 */       JPanel p = new JPanel(new BorderLayout());
/*  55: 91 */       p.setBorder(BorderFactory.createTitledBorder("Choose class attribute"));
/*  56: 92 */       p.add(this.m_classCombo, "North");
/*  57:    */       
/*  58: 94 */       createAboutPanel(step);
/*  59: 95 */       add(p, "Center");
/*  60:    */     }
/*  61:    */     else
/*  62:    */     {
/*  63: 97 */       super.setStepToEdit(step);
/*  64:    */     }
/*  65:    */   }
/*  66:    */   
/*  67:    */   protected void setComboToClass(Instances incomingStructure)
/*  68:    */   {
/*  69:107 */     String stepC = ((ClassAssigner)getStepToEdit()).getClassColumn();
/*  70:108 */     if ((stepC != null) && (stepC.length() > 0)) {
/*  71:109 */       if (stepC.equalsIgnoreCase("/first"))
/*  72:    */       {
/*  73:110 */         this.m_classCombo.setSelectedIndex(0);
/*  74:    */       }
/*  75:111 */       else if (stepC.equalsIgnoreCase("/last"))
/*  76:    */       {
/*  77:112 */         this.m_classCombo.setSelectedIndex(this.m_classCombo.getItemCount() - 1);
/*  78:    */       }
/*  79:    */       else
/*  80:    */       {
/*  81:114 */         Attribute a = incomingStructure.attribute(stepC);
/*  82:115 */         if (a != null)
/*  83:    */         {
/*  84:116 */           String attN = "(" + Attribute.typeToStringShort(a) + ") " + a.name();
/*  85:117 */           this.m_classCombo.setSelectedItem(attN);
/*  86:    */         }
/*  87:    */         else
/*  88:    */         {
/*  89:    */           try
/*  90:    */           {
/*  91:121 */             int num = Integer.parseInt(stepC);
/*  92:122 */             num--;
/*  93:123 */             if ((num >= 0) && (num < incomingStructure.numAttributes())) {
/*  94:124 */               this.m_classCombo.setSelectedIndex(num);
/*  95:    */             }
/*  96:    */           }
/*  97:    */           catch (NumberFormatException e)
/*  98:    */           {
/*  99:128 */             this.m_classCombo.setSelectedItem(stepC);
/* 100:    */           }
/* 101:    */         }
/* 102:    */       }
/* 103:    */     }
/* 104:    */   }
/* 105:    */   
/* 106:    */   public void okPressed()
/* 107:    */   {
/* 108:140 */     String selected = this.m_classCombo.getSelectedItem().toString();
/* 109:141 */     selected = selected.substring(selected.indexOf(')') + 1, selected.length()).trim();
/* 110:    */     
/* 111:143 */     ((ClassAssigner)getStepToEdit()).setClassColumn(selected);
/* 112:    */   }
/* 113:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.ClassAssignerStepEditorDialog
 * JD-Core Version:    0.7.0.1
 */