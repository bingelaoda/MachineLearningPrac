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
/*  15:    */ import weka.core.Instances;
/*  16:    */ import weka.core.WekaException;
/*  17:    */ import weka.gui.ResultHistoryPanel;
/*  18:    */ import weka.gui.ResultHistoryPanel.RMouseAdapter;
/*  19:    */ import weka.gui.knowledgeflow.BaseInteractiveViewer;
/*  20:    */ import weka.gui.visualize.PlotData2D;
/*  21:    */ import weka.gui.visualize.VisualizePanel;
/*  22:    */ import weka.knowledgeflow.steps.DataVisualizer;
/*  23:    */ 
/*  24:    */ public class DataVisualizerInteractiveView
/*  25:    */   extends BaseInteractiveViewer
/*  26:    */ {
/*  27:    */   private static final long serialVersionUID = 5345799787154266282L;
/*  28:    */   protected ResultHistoryPanel m_history;
/*  29: 53 */   protected VisualizePanel m_visPanel = new VisualizePanel();
/*  30: 56 */   protected JButton m_clearButton = new JButton("Clear results");
/*  31:    */   protected JSplitPane m_splitPane;
/*  32:    */   protected PlotData2D m_currentPlot;
/*  33:    */   
/*  34:    */   public String getViewerName()
/*  35:    */   {
/*  36: 71 */     return "Data Visualizer";
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
/*  47: 86 */     this.m_history.getList().addMouseListener(new ResultHistoryPanel.RMouseAdapter()
/*  48:    */     {
/*  49:    */       private static final long serialVersionUID = -5174882230278923704L;
/*  50:    */       
/*  51:    */       public void mouseClicked(MouseEvent e)
/*  52:    */       {
/*  53: 92 */         int index = DataVisualizerInteractiveView.this.m_history.getList().locationToIndex(e.getPoint());
/*  54: 93 */         if (index != -1)
/*  55:    */         {
/*  56: 94 */           String name = DataVisualizerInteractiveView.this.m_history.getNameAtIndex(index);
/*  57:    */           
/*  58: 96 */           Object plotD = DataVisualizerInteractiveView.this.m_history.getNamedObject(name);
/*  59: 97 */           if ((plotD instanceof PlotData2D)) {
/*  60:    */             try
/*  61:    */             {
/*  62: 99 */               if ((DataVisualizerInteractiveView.this.m_currentPlot != null) && (DataVisualizerInteractiveView.this.m_currentPlot != plotD))
/*  63:    */               {
/*  64:100 */                 DataVisualizerInteractiveView.this.m_currentPlot.setXindex(DataVisualizerInteractiveView.this.m_visPanel.getXIndex());
/*  65:101 */                 DataVisualizerInteractiveView.this.m_currentPlot.setYindex(DataVisualizerInteractiveView.this.m_visPanel.getYIndex());
/*  66:102 */                 DataVisualizerInteractiveView.this.m_currentPlot.setCindex(DataVisualizerInteractiveView.this.m_visPanel.getCIndex());
/*  67:    */               }
/*  68:105 */               DataVisualizerInteractiveView.this.m_currentPlot = ((PlotData2D)plotD);
/*  69:106 */               int x = DataVisualizerInteractiveView.this.m_currentPlot.getXindex();
/*  70:107 */               int y = DataVisualizerInteractiveView.this.m_currentPlot.getYindex();
/*  71:108 */               int c = DataVisualizerInteractiveView.this.m_currentPlot.getCindex();
/*  72:109 */               if ((x == y) && (x == 0) && (DataVisualizerInteractiveView.this.m_currentPlot.getPlotInstances().numAttributes() > 1)) {
/*  73:111 */                 y++;
/*  74:    */               }
/*  75:113 */               DataVisualizerInteractiveView.this.m_visPanel.setMasterPlot((PlotData2D)plotD);
/*  76:114 */               DataVisualizerInteractiveView.this.m_visPanel.setXIndex(x);
/*  77:115 */               DataVisualizerInteractiveView.this.m_visPanel.setYIndex(y);
/*  78:116 */               DataVisualizerInteractiveView.this.m_visPanel.setColourIndex(c, true);
/*  79:117 */               DataVisualizerInteractiveView.this.m_visPanel.repaint();
/*  80:    */             }
/*  81:    */             catch (Exception ex)
/*  82:    */             {
/*  83:119 */               ex.printStackTrace();
/*  84:    */             }
/*  85:    */           }
/*  86:    */         }
/*  87:    */       }
/*  88:125 */     });
/*  89:126 */     this.m_history.getList().getSelectionModel().addListSelectionListener(new ListSelectionListener()
/*  90:    */     {
/*  91:    */       public void valueChanged(ListSelectionEvent e)
/*  92:    */       {
/*  93:130 */         if (!e.getValueIsAdjusting())
/*  94:    */         {
/*  95:131 */           ListSelectionModel lm = (ListSelectionModel)e.getSource();
/*  96:132 */           for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
/*  97:133 */             if (lm.isSelectedIndex(i))
/*  98:    */             {
/*  99:135 */               if (i == -1) {
/* 100:    */                 break;
/* 101:    */               }
/* 102:136 */               String name = DataVisualizerInteractiveView.this.m_history.getNameAtIndex(i);
/* 103:137 */               Object plotD = DataVisualizerInteractiveView.this.m_history.getNamedObject(name);
/* 104:138 */               if ((plotD != null) && ((plotD instanceof PlotData2D))) {
/* 105:    */                 try
/* 106:    */                 {
/* 107:140 */                   if ((DataVisualizerInteractiveView.this.m_currentPlot != null) && (DataVisualizerInteractiveView.this.m_currentPlot != plotD))
/* 108:    */                   {
/* 109:141 */                     DataVisualizerInteractiveView.this.m_currentPlot.setXindex(DataVisualizerInteractiveView.this.m_visPanel.getXIndex());
/* 110:142 */                     DataVisualizerInteractiveView.this.m_currentPlot.setYindex(DataVisualizerInteractiveView.this.m_visPanel.getYIndex());
/* 111:143 */                     DataVisualizerInteractiveView.this.m_currentPlot.setCindex(DataVisualizerInteractiveView.this.m_visPanel.getCIndex());
/* 112:    */                   }
/* 113:146 */                   DataVisualizerInteractiveView.this.m_currentPlot = ((PlotData2D)plotD);
/* 114:147 */                   int x = DataVisualizerInteractiveView.this.m_currentPlot.getXindex();
/* 115:148 */                   int y = DataVisualizerInteractiveView.this.m_currentPlot.getYindex();
/* 116:149 */                   int c = DataVisualizerInteractiveView.this.m_currentPlot.getCindex();
/* 117:150 */                   if ((x == y) && (x == 0) && (DataVisualizerInteractiveView.this.m_currentPlot.getPlotInstances().numAttributes() > 1)) {
/* 118:152 */                     y++;
/* 119:    */                   }
/* 120:154 */                   DataVisualizerInteractiveView.this.m_visPanel.setMasterPlot((PlotData2D)plotD);
/* 121:155 */                   DataVisualizerInteractiveView.this.m_visPanel.setXIndex(x);
/* 122:156 */                   DataVisualizerInteractiveView.this.m_visPanel.setYIndex(y);
/* 123:157 */                   DataVisualizerInteractiveView.this.m_visPanel.setColourIndex(c, true);
/* 124:158 */                   DataVisualizerInteractiveView.this.m_visPanel.repaint();
/* 125:    */                 }
/* 126:    */                 catch (Exception ex)
/* 127:    */                 {
/* 128:160 */                   ex.printStackTrace();
/* 129:    */                 }
/* 130:    */               }
/* 131:163 */               break;
/* 132:    */             }
/* 133:    */           }
/* 134:    */         }
/* 135:    */       }
/* 136:170 */     });
/* 137:171 */     this.m_splitPane = new JSplitPane(1, this.m_history, this.m_visPanel);
/* 138:    */     
/* 139:    */ 
/* 140:174 */     add(this.m_splitPane, "Center");
/* 141:175 */     boolean first = true;
/* 142:176 */     for (PlotData2D pd : ((DataVisualizer)getStep()).getPlots())
/* 143:    */     {
/* 144:177 */       this.m_history.addResult(pd.getPlotName(), new StringBuffer());
/* 145:178 */       this.m_history.addObject(pd.getPlotName(), pd);
/* 146:179 */       if (first) {
/* 147:    */         try
/* 148:    */         {
/* 149:181 */           int x = pd.getXindex();
/* 150:182 */           int y = pd.getYindex();
/* 151:183 */           int c = pd.getCindex();
/* 152:184 */           if ((x == 0) && (y == 0) && (pd.getPlotInstances().numAttributes() > 1)) {
/* 153:185 */             y++;
/* 154:    */           }
/* 155:188 */           this.m_visPanel.setMasterPlot(pd);
/* 156:189 */           this.m_currentPlot = pd;
/* 157:190 */           this.m_visPanel.setXIndex(x);
/* 158:191 */           this.m_visPanel.setYIndex(y);
/* 159:192 */           if (pd.getPlotInstances().classIndex() >= 0) {
/* 160:193 */             this.m_visPanel.setColourIndex(pd.getPlotInstances().classIndex(), true);
/* 161:    */           } else {
/* 162:195 */             this.m_visPanel.setColourIndex(c, true);
/* 163:    */           }
/* 164:197 */           this.m_visPanel.repaint();
/* 165:198 */           first = false;
/* 166:    */         }
/* 167:    */         catch (Exception ex)
/* 168:    */         {
/* 169:200 */           ex.printStackTrace();
/* 170:    */         }
/* 171:    */       }
/* 172:204 */       this.m_visPanel.setPreferredSize(new Dimension(800, 600));
/* 173:    */     }
/* 174:207 */     this.m_clearButton.addActionListener(new ActionListener()
/* 175:    */     {
/* 176:    */       public void actionPerformed(ActionEvent e)
/* 177:    */       {
/* 178:210 */         DataVisualizerInteractiveView.this.m_history.clearResults();
/* 179:211 */         ((DataVisualizer)DataVisualizerInteractiveView.this.getStep()).getPlots().clear();
/* 180:212 */         DataVisualizerInteractiveView.this.m_splitPane.remove(DataVisualizerInteractiveView.this.m_visPanel);
/* 181:    */       }
/* 182:    */     });
/* 183:    */   }
/* 184:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.DataVisualizerInteractiveView
 * JD-Core Version:    0.7.0.1
 */