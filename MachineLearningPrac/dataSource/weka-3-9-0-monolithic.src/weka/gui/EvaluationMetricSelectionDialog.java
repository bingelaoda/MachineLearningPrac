/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dialog;
/*   6:    */ import java.awt.Dialog.ModalityType;
/*   7:    */ import java.awt.Frame;
/*   8:    */ import java.awt.GridLayout;
/*   9:    */ import java.awt.Window;
/*  10:    */ import java.awt.event.ActionEvent;
/*  11:    */ import java.awt.event.ActionListener;
/*  12:    */ import java.util.ArrayList;
/*  13:    */ import java.util.List;
/*  14:    */ import javax.swing.JButton;
/*  15:    */ import javax.swing.JDialog;
/*  16:    */ import javax.swing.JPanel;
/*  17:    */ import weka.classifiers.evaluation.Evaluation;
/*  18:    */ import weka.core.Attribute;
/*  19:    */ import weka.core.Instances;
/*  20:    */ 
/*  21:    */ public class EvaluationMetricSelectionDialog
/*  22:    */   extends JDialog
/*  23:    */ {
/*  24:    */   private static final long serialVersionUID = 4451184027143094270L;
/*  25:    */   protected List<String> m_selectedEvalMetrics;
/*  26:    */   
/*  27:    */   public EvaluationMetricSelectionDialog(Dialog parent, List<String> evalMetrics)
/*  28:    */   {
/*  29: 64 */     super(parent, "Manage evaluation metrics", Dialog.ModalityType.DOCUMENT_MODAL);
/*  30: 65 */     this.m_selectedEvalMetrics = evalMetrics;
/*  31: 66 */     init();
/*  32:    */   }
/*  33:    */   
/*  34:    */   public EvaluationMetricSelectionDialog(Window parent, List<String> evalMetrics)
/*  35:    */   {
/*  36: 76 */     super(parent, "Manage evaluation metrics", Dialog.ModalityType.DOCUMENT_MODAL);
/*  37: 77 */     this.m_selectedEvalMetrics = evalMetrics;
/*  38: 78 */     init();
/*  39:    */   }
/*  40:    */   
/*  41:    */   public EvaluationMetricSelectionDialog(Frame parent, List<String> evalMetrics)
/*  42:    */   {
/*  43: 88 */     super(parent, "Manage evaluation metrics", Dialog.ModalityType.DOCUMENT_MODAL);
/*  44: 89 */     this.m_selectedEvalMetrics = evalMetrics;
/*  45: 90 */     init();
/*  46:    */   }
/*  47:    */   
/*  48:    */   public List<String> getSelectedEvalMetrics()
/*  49:    */   {
/*  50: 99 */     return this.m_selectedEvalMetrics;
/*  51:    */   }
/*  52:    */   
/*  53:    */   private void init()
/*  54:    */   {
/*  55:103 */     final AttributeSelectionPanel evalConfigurer = new AttributeSelectionPanel(true, true, true, true);
/*  56:    */     
/*  57:    */ 
/*  58:106 */     ArrayList<Attribute> atts = new ArrayList();
/*  59:107 */     List<String> allEvalMetrics = Evaluation.getAllEvaluationMetricNames();
/*  60:108 */     for (String s : allEvalMetrics) {
/*  61:109 */       atts.add(new Attribute(s));
/*  62:    */     }
/*  63:112 */     final Instances metricInstances = new Instances("Metrics", atts, 1);
/*  64:113 */     boolean[] selectedMetrics = new boolean[metricInstances.numAttributes()];
/*  65:114 */     if (this.m_selectedEvalMetrics == null)
/*  66:    */     {
/*  67:116 */       this.m_selectedEvalMetrics = new ArrayList();
/*  68:117 */       for (int i = 0; i < metricInstances.numAttributes(); i++) {
/*  69:118 */         this.m_selectedEvalMetrics.add(metricInstances.attribute(i).name());
/*  70:    */       }
/*  71:    */     }
/*  72:122 */     for (int i = 0; i < selectedMetrics.length; i++) {
/*  73:123 */       if (this.m_selectedEvalMetrics.contains(metricInstances.attribute(i).name())) {
/*  74:124 */         selectedMetrics[i] = true;
/*  75:    */       }
/*  76:    */     }
/*  77:    */     try
/*  78:    */     {
/*  79:129 */       evalConfigurer.setInstances(metricInstances);
/*  80:130 */       evalConfigurer.setSelectedAttributes(selectedMetrics);
/*  81:    */     }
/*  82:    */     catch (Exception ex)
/*  83:    */     {
/*  84:132 */       ex.printStackTrace();
/*  85:133 */       return;
/*  86:    */     }
/*  87:136 */     setLayout(new BorderLayout());
/*  88:137 */     JPanel holder = new JPanel();
/*  89:138 */     holder.setLayout(new BorderLayout());
/*  90:139 */     holder.add(evalConfigurer, "Center");
/*  91:140 */     JButton okBut = new JButton("OK");
/*  92:141 */     JButton cancelBut = new JButton("Cancel");
/*  93:142 */     JPanel butHolder = new JPanel();
/*  94:143 */     butHolder.setLayout(new GridLayout(1, 2));
/*  95:144 */     butHolder.add(okBut);
/*  96:145 */     butHolder.add(cancelBut);
/*  97:146 */     holder.add(butHolder, "South");
/*  98:147 */     okBut.addActionListener(new ActionListener()
/*  99:    */     {
/* 100:    */       public void actionPerformed(ActionEvent e)
/* 101:    */       {
/* 102:150 */         int[] selected = evalConfigurer.getSelectedAttributes();
/* 103:151 */         EvaluationMetricSelectionDialog.this.m_selectedEvalMetrics.clear();
/* 104:152 */         for (int i = 0; i < selected.length; i++) {
/* 105:153 */           EvaluationMetricSelectionDialog.this.m_selectedEvalMetrics.add(metricInstances.attribute(selected[i]).name());
/* 106:    */         }
/* 107:156 */         EvaluationMetricSelectionDialog.this.dispose();
/* 108:    */       }
/* 109:159 */     });
/* 110:160 */     cancelBut.addActionListener(new ActionListener()
/* 111:    */     {
/* 112:    */       public void actionPerformed(ActionEvent e)
/* 113:    */       {
/* 114:163 */         EvaluationMetricSelectionDialog.this.dispose();
/* 115:    */       }
/* 116:166 */     });
/* 117:167 */     getContentPane().add(holder, "Center");
/* 118:168 */     pack();
/* 119:    */   }
/* 120:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.EvaluationMetricSelectionDialog
 * JD-Core Version:    0.7.0.1
 */