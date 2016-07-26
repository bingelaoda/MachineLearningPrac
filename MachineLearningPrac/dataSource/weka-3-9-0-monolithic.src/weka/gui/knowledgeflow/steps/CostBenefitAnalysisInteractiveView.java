/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.Dimension;
/*   4:    */ import java.awt.event.ActionEvent;
/*   5:    */ import java.awt.event.ActionListener;
/*   6:    */ import java.awt.event.MouseEvent;
/*   7:    */ import java.util.List;
/*   8:    */ import javax.swing.BorderFactory;
/*   9:    */ import javax.swing.JButton;
/*  10:    */ import javax.swing.JList;
/*  11:    */ import javax.swing.JSplitPane;
/*  12:    */ import javax.swing.ListSelectionModel;
/*  13:    */ import javax.swing.event.ListSelectionEvent;
/*  14:    */ import javax.swing.event.ListSelectionListener;
/*  15:    */ import weka.core.Attribute;
/*  16:    */ import weka.core.WekaException;
/*  17:    */ import weka.gui.CostBenefitAnalysisPanel;
/*  18:    */ import weka.gui.ResultHistoryPanel;
/*  19:    */ import weka.gui.ResultHistoryPanel.RMouseAdapter;
/*  20:    */ import weka.gui.knowledgeflow.BaseInteractiveViewer;
/*  21:    */ import weka.gui.visualize.PlotData2D;
/*  22:    */ import weka.knowledgeflow.Data;
/*  23:    */ import weka.knowledgeflow.steps.CostBenefitAnalysis;
/*  24:    */ 
/*  25:    */ public class CostBenefitAnalysisInteractiveView
/*  26:    */   extends BaseInteractiveViewer
/*  27:    */ {
/*  28:    */   private static final long serialVersionUID = 4624182171551712860L;
/*  29:    */   protected ResultHistoryPanel m_history;
/*  30: 56 */   protected JButton m_clearButton = new JButton("Clear results");
/*  31: 59 */   protected CostBenefitAnalysisPanel m_cbPanel = new CostBenefitAnalysisPanel();
/*  32:    */   protected JSplitPane m_splitPane;
/*  33:    */   
/*  34:    */   public String getViewerName()
/*  35:    */   {
/*  36: 71 */     return "Cost-benefit Analysis";
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void init()
/*  40:    */     throws WekaException
/*  41:    */   {
/*  42: 81 */     addButton(this.m_clearButton);
/*  43:    */     
/*  44: 83 */     this.m_history = new ResultHistoryPanel(null);
/*  45: 84 */     this.m_history.setBorder(BorderFactory.createTitledBorder("Result list"));
/*  46: 85 */     this.m_history.setHandleRightClicks(false);
/*  47:    */     
/*  48: 87 */     this.m_history.getList().addMouseListener(new ResultHistoryPanel.RMouseAdapter()
/*  49:    */     {
/*  50:    */       private static final long serialVersionUID = -5174882230278923704L;
/*  51:    */       
/*  52:    */       public void mouseClicked(MouseEvent e)
/*  53:    */       {
/*  54: 93 */         int index = CostBenefitAnalysisInteractiveView.this.m_history.getList().locationToIndex(e.getPoint());
/*  55: 94 */         if (index != -1)
/*  56:    */         {
/*  57: 95 */           String name = CostBenefitAnalysisInteractiveView.this.m_history.getNameAtIndex(index);
/*  58:    */           
/*  59: 97 */           Object data = CostBenefitAnalysisInteractiveView.this.m_history.getNamedObject(name);
/*  60: 98 */           if ((data instanceof Data))
/*  61:    */           {
/*  62: 99 */             PlotData2D threshData = (PlotData2D)((Data)data).getPrimaryPayload();
/*  63:100 */             Attribute classAtt = (Attribute)((Data)data).getPayloadElement("class_attribute");
/*  64:    */             try
/*  65:    */             {
/*  66:104 */               CostBenefitAnalysisInteractiveView.this.m_cbPanel.setDataSet(threshData, classAtt);
/*  67:105 */               CostBenefitAnalysisInteractiveView.this.m_cbPanel.repaint();
/*  68:    */             }
/*  69:    */             catch (Exception ex)
/*  70:    */             {
/*  71:107 */               ex.printStackTrace();
/*  72:    */             }
/*  73:    */           }
/*  74:    */         }
/*  75:    */       }
/*  76:113 */     });
/*  77:114 */     this.m_history.getList().getSelectionModel().addListSelectionListener(new ListSelectionListener()
/*  78:    */     {
/*  79:    */       public void valueChanged(ListSelectionEvent e)
/*  80:    */       {
/*  81:118 */         if (!e.getValueIsAdjusting())
/*  82:    */         {
/*  83:119 */           ListSelectionModel lm = (ListSelectionModel)e.getSource();
/*  84:120 */           for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
/*  85:121 */             if (lm.isSelectedIndex(i))
/*  86:    */             {
/*  87:123 */               if (i == -1) {
/*  88:    */                 break;
/*  89:    */               }
/*  90:124 */               String name = CostBenefitAnalysisInteractiveView.this.m_history.getNameAtIndex(i);
/*  91:125 */               Object data = CostBenefitAnalysisInteractiveView.this.m_history.getNamedObject(name);
/*  92:126 */               if ((data != null) && ((data instanceof Data)))
/*  93:    */               {
/*  94:127 */                 PlotData2D threshData = (PlotData2D)((Data)data).getPrimaryPayload();
/*  95:128 */                 Attribute classAtt = (Attribute)((Data)data).getPayloadElement("class_attribute");
/*  96:    */                 try
/*  97:    */                 {
/*  98:132 */                   CostBenefitAnalysisInteractiveView.this.m_cbPanel.setDataSet(threshData, classAtt);
/*  99:133 */                   CostBenefitAnalysisInteractiveView.this.m_cbPanel.repaint();
/* 100:    */                 }
/* 101:    */                 catch (Exception ex)
/* 102:    */                 {
/* 103:135 */                   ex.printStackTrace();
/* 104:    */                 }
/* 105:    */               }
/* 106:138 */               break;
/* 107:    */             }
/* 108:    */           }
/* 109:    */         }
/* 110:    */       }
/* 111:145 */     });
/* 112:146 */     this.m_splitPane = new JSplitPane(1, this.m_history, this.m_cbPanel);
/* 113:    */     
/* 114:148 */     add(this.m_splitPane, "Center");
/* 115:149 */     this.m_cbPanel.setPreferredSize(new Dimension(1000, 600));
/* 116:    */     
/* 117:151 */     boolean first = true;
/* 118:152 */     for (Data d : ((CostBenefitAnalysis)getStep()).getDatasets())
/* 119:    */     {
/* 120:153 */       PlotData2D threshData = (PlotData2D)d.getPrimaryPayload();
/* 121:154 */       Attribute classAtt = (Attribute)d.getPayloadElement("class_attribute");
/* 122:    */       
/* 123:156 */       String title = threshData.getPlotName();
/* 124:157 */       this.m_history.addResult(title, new StringBuffer());
/* 125:158 */       this.m_history.addObject(title, d);
/* 126:159 */       if (first) {
/* 127:    */         try
/* 128:    */         {
/* 129:161 */           this.m_cbPanel.setDataSet(threshData, classAtt);
/* 130:162 */           this.m_cbPanel.repaint();
/* 131:163 */           first = false;
/* 132:    */         }
/* 133:    */         catch (Exception ex)
/* 134:    */         {
/* 135:165 */           ex.printStackTrace();
/* 136:    */         }
/* 137:    */       }
/* 138:    */     }
/* 139:170 */     this.m_clearButton.addActionListener(new ActionListener()
/* 140:    */     {
/* 141:    */       public void actionPerformed(ActionEvent e)
/* 142:    */       {
/* 143:173 */         CostBenefitAnalysisInteractiveView.this.m_history.clearResults();
/* 144:174 */         ((CostBenefitAnalysis)CostBenefitAnalysisInteractiveView.this.getStep()).getDatasets().clear();
/* 145:175 */         CostBenefitAnalysisInteractiveView.this.m_splitPane.remove(CostBenefitAnalysisInteractiveView.this.m_cbPanel);
/* 146:176 */         CostBenefitAnalysisInteractiveView.this.m_splitPane.revalidate();
/* 147:    */       }
/* 148:    */     });
/* 149:    */   }
/* 150:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.CostBenefitAnalysisInteractiveView
 * JD-Core Version:    0.7.0.1
 */