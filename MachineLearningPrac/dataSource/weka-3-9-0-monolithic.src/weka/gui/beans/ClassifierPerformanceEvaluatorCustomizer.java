/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.GridLayout;
/*   5:    */ import java.awt.Window;
/*   6:    */ import java.awt.event.ActionEvent;
/*   7:    */ import java.awt.event.ActionListener;
/*   8:    */ import java.beans.PropertyChangeListener;
/*   9:    */ import java.beans.PropertyChangeSupport;
/*  10:    */ import java.util.ArrayList;
/*  11:    */ import java.util.List;
/*  12:    */ import javax.swing.BorderFactory;
/*  13:    */ import javax.swing.JButton;
/*  14:    */ import javax.swing.JPanel;
/*  15:    */ import weka.gui.EvaluationMetricSelectionDialog;
/*  16:    */ import weka.gui.PropertySheetPanel;
/*  17:    */ 
/*  18:    */ public class ClassifierPerformanceEvaluatorCustomizer
/*  19:    */   extends JPanel
/*  20:    */   implements BeanCustomizer, CustomizerCloseRequester, CustomizerClosingListener
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = -1055460295546483853L;
/*  23: 54 */   private final PropertyChangeSupport m_pcSupport = new PropertyChangeSupport(this);
/*  24: 57 */   private final PropertySheetPanel m_splitEditor = new PropertySheetPanel();
/*  25:    */   private ClassifierPerformanceEvaluator m_cpe;
/*  26:    */   private BeanCustomizer.ModifyListener m_modifyListener;
/*  27:    */   private int m_executionSlotsBackup;
/*  28:    */   private Window m_parent;
/*  29: 65 */   private final JButton m_evalMetricsBut = new JButton("Evaluation metrics...");
/*  30:    */   private List<String> m_evaluationMetrics;
/*  31:    */   
/*  32:    */   public ClassifierPerformanceEvaluatorCustomizer()
/*  33:    */   {
/*  34: 72 */     setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
/*  35:    */     
/*  36: 74 */     setLayout(new BorderLayout());
/*  37:    */     
/*  38: 76 */     JPanel holder = new JPanel();
/*  39: 77 */     holder.setLayout(new BorderLayout());
/*  40:    */     
/*  41: 79 */     holder.add(this.m_splitEditor, "North");
/*  42: 80 */     holder.add(this.m_evalMetricsBut, "South");
/*  43: 81 */     add(holder, "North");
/*  44: 82 */     this.m_evalMetricsBut.setToolTipText("Enable/disable output of specific evaluation metrics");
/*  45:    */     
/*  46:    */ 
/*  47: 85 */     addButtons();
/*  48:    */   }
/*  49:    */   
/*  50:    */   private void addButtons()
/*  51:    */   {
/*  52: 89 */     JButton okBut = new JButton("OK");
/*  53: 90 */     JButton cancelBut = new JButton("Cancel");
/*  54:    */     
/*  55: 92 */     JPanel butHolder = new JPanel();
/*  56: 93 */     butHolder.setLayout(new GridLayout(1, 2));
/*  57: 94 */     butHolder.add(okBut);
/*  58: 95 */     butHolder.add(cancelBut);
/*  59: 96 */     add(butHolder, "South");
/*  60:    */     
/*  61: 98 */     okBut.addActionListener(new ActionListener()
/*  62:    */     {
/*  63:    */       public void actionPerformed(ActionEvent e)
/*  64:    */       {
/*  65:101 */         if (ClassifierPerformanceEvaluatorCustomizer.this.m_modifyListener != null) {
/*  66:102 */           ClassifierPerformanceEvaluatorCustomizer.this.m_modifyListener.setModifiedStatus(ClassifierPerformanceEvaluatorCustomizer.this, true);
/*  67:    */         }
/*  68:106 */         if (ClassifierPerformanceEvaluatorCustomizer.this.m_evaluationMetrics.size() > 0)
/*  69:    */         {
/*  70:107 */           StringBuilder b = new StringBuilder();
/*  71:108 */           for (String s : ClassifierPerformanceEvaluatorCustomizer.this.m_evaluationMetrics) {
/*  72:109 */             b.append(s).append(",");
/*  73:    */           }
/*  74:111 */           String newList = b.substring(0, b.length() - 1);
/*  75:112 */           ClassifierPerformanceEvaluatorCustomizer.this.m_cpe.setEvaluationMetricsToOutput(newList);
/*  76:    */         }
/*  77:114 */         if (ClassifierPerformanceEvaluatorCustomizer.this.m_parent != null) {
/*  78:115 */           ClassifierPerformanceEvaluatorCustomizer.this.m_parent.dispose();
/*  79:    */         }
/*  80:    */       }
/*  81:119 */     });
/*  82:120 */     cancelBut.addActionListener(new ActionListener()
/*  83:    */     {
/*  84:    */       public void actionPerformed(ActionEvent e)
/*  85:    */       {
/*  86:124 */         ClassifierPerformanceEvaluatorCustomizer.this.customizerClosing();
/*  87:125 */         if (ClassifierPerformanceEvaluatorCustomizer.this.m_parent != null) {
/*  88:126 */           ClassifierPerformanceEvaluatorCustomizer.this.m_parent.dispose();
/*  89:    */         }
/*  90:    */       }
/*  91:    */     });
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void setObject(Object object)
/*  95:    */   {
/*  96:139 */     this.m_cpe = ((ClassifierPerformanceEvaluator)object);
/*  97:140 */     this.m_executionSlotsBackup = this.m_cpe.getExecutionSlots();
/*  98:141 */     this.m_splitEditor.setTarget(this.m_cpe);
/*  99:    */     
/* 100:143 */     String list = this.m_cpe.getEvaluationMetricsToOutput();
/* 101:144 */     this.m_evaluationMetrics = new ArrayList();
/* 102:145 */     if ((list != null) && (list.length() > 0))
/* 103:    */     {
/* 104:146 */       String[] parts = list.split(",");
/* 105:147 */       for (String s : parts) {
/* 106:148 */         this.m_evaluationMetrics.add(s.trim());
/* 107:    */       }
/* 108:    */     }
/* 109:    */   }
/* 110:    */   
/* 111:    */   public void addPropertyChangeListener(PropertyChangeListener pcl)
/* 112:    */   {
/* 113:160 */     this.m_pcSupport.addPropertyChangeListener(pcl);
/* 114:    */   }
/* 115:    */   
/* 116:    */   public void removePropertyChangeListener(PropertyChangeListener pcl)
/* 117:    */   {
/* 118:170 */     this.m_pcSupport.removePropertyChangeListener(pcl);
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void setModifiedListener(BeanCustomizer.ModifyListener l)
/* 122:    */   {
/* 123:175 */     this.m_modifyListener = l;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public void customizerClosing()
/* 127:    */   {
/* 128:181 */     this.m_cpe.setExecutionSlots(this.m_executionSlotsBackup);
/* 129:    */   }
/* 130:    */   
/* 131:    */   public void setParentWindow(Window parent)
/* 132:    */   {
/* 133:186 */     this.m_parent = parent;
/* 134:    */     
/* 135:188 */     this.m_evalMetricsBut.addActionListener(new ActionListener()
/* 136:    */     {
/* 137:    */       public void actionPerformed(ActionEvent e)
/* 138:    */       {
/* 139:191 */         EvaluationMetricSelectionDialog esd = new EvaluationMetricSelectionDialog(ClassifierPerformanceEvaluatorCustomizer.this.m_parent, ClassifierPerformanceEvaluatorCustomizer.this.m_evaluationMetrics);
/* 140:    */         
/* 141:193 */         esd.setLocation(ClassifierPerformanceEvaluatorCustomizer.this.m_evalMetricsBut.getLocationOnScreen());
/* 142:194 */         esd.pack();
/* 143:195 */         esd.setVisible(true);
/* 144:196 */         ClassifierPerformanceEvaluatorCustomizer.this.m_evaluationMetrics = esd.getSelectedEvalMetrics();
/* 145:    */       }
/* 146:    */     });
/* 147:    */   }
/* 148:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ClassifierPerformanceEvaluatorCustomizer
 * JD-Core Version:    0.7.0.1
 */