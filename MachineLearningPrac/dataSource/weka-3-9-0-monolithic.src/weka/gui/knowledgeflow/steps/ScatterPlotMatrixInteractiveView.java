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
/*  15:    */ import weka.core.Defaults;
/*  16:    */ import weka.core.Environment;
/*  17:    */ import weka.core.Instances;
/*  18:    */ import weka.core.Settings;
/*  19:    */ import weka.core.WekaException;
/*  20:    */ import weka.gui.ResultHistoryPanel;
/*  21:    */ import weka.gui.ResultHistoryPanel.RMouseAdapter;
/*  22:    */ import weka.gui.explorer.VisualizePanel.ScatterDefaults;
/*  23:    */ import weka.gui.knowledgeflow.BaseInteractiveViewer;
/*  24:    */ import weka.gui.knowledgeflow.ScatterPlotMatrixPerspective;
/*  25:    */ import weka.gui.visualize.MatrixPanel;
/*  26:    */ import weka.knowledgeflow.Data;
/*  27:    */ import weka.knowledgeflow.steps.ScatterPlotMatrix;
/*  28:    */ 
/*  29:    */ public class ScatterPlotMatrixInteractiveView
/*  30:    */   extends BaseInteractiveViewer
/*  31:    */ {
/*  32:    */   private static final long serialVersionUID = 275603100387301133L;
/*  33:    */   protected ResultHistoryPanel m_history;
/*  34: 64 */   protected MatrixPanel m_matrixPanel = new MatrixPanel();
/*  35: 67 */   protected JButton m_clearButton = new JButton("Clear results");
/*  36:    */   protected JSplitPane m_splitPane;
/*  37:    */   
/*  38:    */   public String getViewerName()
/*  39:    */   {
/*  40: 79 */     return "Scatter Plot Matrix";
/*  41:    */   }
/*  42:    */   
/*  43:    */   public void init()
/*  44:    */     throws WekaException
/*  45:    */   {
/*  46: 89 */     addButton(this.m_clearButton);
/*  47:    */     
/*  48: 91 */     this.m_history = new ResultHistoryPanel(null);
/*  49: 92 */     this.m_history.setBorder(BorderFactory.createTitledBorder("Result list"));
/*  50: 93 */     this.m_history.setHandleRightClicks(false);
/*  51:    */     
/*  52: 95 */     this.m_history.getList().addMouseListener(new ResultHistoryPanel.RMouseAdapter()
/*  53:    */     {
/*  54:    */       private static final long serialVersionUID = -5174882230278923704L;
/*  55:    */       
/*  56:    */       public void mouseClicked(MouseEvent e)
/*  57:    */       {
/*  58:101 */         int index = ScatterPlotMatrixInteractiveView.this.m_history.getList().locationToIndex(e.getPoint());
/*  59:102 */         if (index != -1)
/*  60:    */         {
/*  61:103 */           String name = ScatterPlotMatrixInteractiveView.this.m_history.getNameAtIndex(index);
/*  62:    */           
/*  63:105 */           Object insts = ScatterPlotMatrixInteractiveView.this.m_history.getNamedObject(name);
/*  64:106 */           if ((insts instanceof Instances)) {
/*  65:    */             try
/*  66:    */             {
/*  67:109 */               ScatterPlotMatrixInteractiveView.this.m_matrixPanel.setInstances((Instances)insts);
/*  68:110 */               ScatterPlotMatrixInteractiveView.this.m_matrixPanel.repaint();
/*  69:    */             }
/*  70:    */             catch (Exception ex)
/*  71:    */             {
/*  72:112 */               ex.printStackTrace();
/*  73:    */             }
/*  74:    */           }
/*  75:    */         }
/*  76:    */       }
/*  77:118 */     });
/*  78:119 */     this.m_history.getList().getSelectionModel().addListSelectionListener(new ListSelectionListener()
/*  79:    */     {
/*  80:    */       public void valueChanged(ListSelectionEvent e)
/*  81:    */       {
/*  82:123 */         if (!e.getValueIsAdjusting())
/*  83:    */         {
/*  84:124 */           ListSelectionModel lm = (ListSelectionModel)e.getSource();
/*  85:125 */           for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
/*  86:126 */             if (lm.isSelectedIndex(i))
/*  87:    */             {
/*  88:128 */               if (i == -1) {
/*  89:    */                 break;
/*  90:    */               }
/*  91:129 */               String name = ScatterPlotMatrixInteractiveView.this.m_history.getNameAtIndex(i);
/*  92:130 */               Object insts = ScatterPlotMatrixInteractiveView.this.m_history.getNamedObject(name);
/*  93:131 */               if ((insts != null) && ((insts instanceof Instances))) {
/*  94:    */                 try
/*  95:    */                 {
/*  96:133 */                   ScatterPlotMatrixInteractiveView.this.m_matrixPanel.setInstances((Instances)insts);
/*  97:134 */                   ScatterPlotMatrixInteractiveView.this.m_matrixPanel.repaint();
/*  98:    */                 }
/*  99:    */                 catch (Exception ex)
/* 100:    */                 {
/* 101:136 */                   ex.printStackTrace();
/* 102:    */                 }
/* 103:    */               }
/* 104:139 */               break;
/* 105:    */             }
/* 106:    */           }
/* 107:    */         }
/* 108:    */       }
/* 109:147 */     });
/* 110:148 */     this.m_matrixPanel.setPreferredSize(new Dimension(800, 600));
/* 111:149 */     this.m_history.setMinimumSize(new Dimension(150, 600));
/* 112:150 */     this.m_splitPane = new JSplitPane(1, this.m_history, this.m_matrixPanel);
/* 113:    */     
/* 114:    */ 
/* 115:153 */     add(this.m_splitPane, "Center");
/* 116:154 */     boolean first = true;
/* 117:155 */     for (Data d : ((ScatterPlotMatrix)getStep()).getDatasets())
/* 118:    */     {
/* 119:156 */       String title = d.getPayloadElement("aux_textTitle").toString();
/* 120:    */       
/* 121:158 */       this.m_history.addResult(title, new StringBuffer());
/* 122:159 */       Instances instances = (Instances)d.getPrimaryPayload();
/* 123:160 */       this.m_history.addObject(title, instances);
/* 124:161 */       if (first)
/* 125:    */       {
/* 126:162 */         this.m_matrixPanel.setInstances(instances);
/* 127:163 */         this.m_matrixPanel.repaint();
/* 128:164 */         first = false;
/* 129:    */       }
/* 130:    */     }
/* 131:168 */     this.m_clearButton.addActionListener(new ActionListener()
/* 132:    */     {
/* 133:    */       public void actionPerformed(ActionEvent e)
/* 134:    */       {
/* 135:171 */         ScatterPlotMatrixInteractiveView.this.m_history.clearResults();
/* 136:172 */         ((ScatterPlotMatrix)ScatterPlotMatrixInteractiveView.this.getStep()).getDatasets().clear();
/* 137:173 */         ScatterPlotMatrixInteractiveView.this.m_splitPane.remove(ScatterPlotMatrixInteractiveView.this.m_matrixPanel);
/* 138:    */       }
/* 139:175 */     });
/* 140:176 */     applySettings(getSettings());
/* 141:    */   }
/* 142:    */   
/* 143:    */   public void applySettings(Settings settings)
/* 144:    */   {
/* 145:186 */     int pointSize = ((Integer)settings.getSetting("weka.gui.workbench.visualizepanel", VisualizePanel.ScatterDefaults.POINT_SIZE_KEY, Integer.valueOf(1), Environment.getSystemWide())).intValue();
/* 146:    */     
/* 147:    */ 
/* 148:    */ 
/* 149:    */ 
/* 150:191 */     int plotSize = ((Integer)settings.getSetting("weka.gui.workbench.visualizepanel", VisualizePanel.ScatterDefaults.PLOT_SIZE_KEY, Integer.valueOf(100), Environment.getSystemWide())).intValue();
/* 151:    */     
/* 152:    */ 
/* 153:    */ 
/* 154:    */ 
/* 155:196 */     this.m_matrixPanel.setPointSize(pointSize);
/* 156:197 */     this.m_matrixPanel.setPlotSize(plotSize);
/* 157:198 */     this.m_matrixPanel.updatePanel();
/* 158:    */   }
/* 159:    */   
/* 160:    */   public Defaults getDefaultSettings()
/* 161:    */   {
/* 162:208 */     return new ScatterPlotMatrixPerspective().getDefaultSettings();
/* 163:    */   }
/* 164:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.ScatterPlotMatrixInteractiveView
 * JD-Core Version:    0.7.0.1
 */