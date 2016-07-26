/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.Dimension;
/*   4:    */ import java.awt.Window;
/*   5:    */ import java.awt.event.ActionEvent;
/*   6:    */ import java.awt.event.ActionListener;
/*   7:    */ import java.awt.event.MouseEvent;
/*   8:    */ import java.util.List;
/*   9:    */ import javax.swing.BorderFactory;
/*  10:    */ import javax.swing.JButton;
/*  11:    */ import javax.swing.JList;
/*  12:    */ import javax.swing.JSplitPane;
/*  13:    */ import javax.swing.ListSelectionModel;
/*  14:    */ import javax.swing.event.ListSelectionEvent;
/*  15:    */ import javax.swing.event.ListSelectionListener;
/*  16:    */ import weka.core.Defaults;
/*  17:    */ import weka.core.Instances;
/*  18:    */ import weka.core.Settings;
/*  19:    */ import weka.core.WekaException;
/*  20:    */ import weka.gui.ResultHistoryPanel;
/*  21:    */ import weka.gui.ResultHistoryPanel.RMouseAdapter;
/*  22:    */ import weka.gui.knowledgeflow.AttributeSummaryPerspective;
/*  23:    */ import weka.gui.knowledgeflow.BaseInteractiveViewer;
/*  24:    */ import weka.knowledgeflow.Data;
/*  25:    */ import weka.knowledgeflow.steps.AttributeSummarizer;
/*  26:    */ 
/*  27:    */ public class AttributeSummarizerInteractiveView
/*  28:    */   extends BaseInteractiveViewer
/*  29:    */ {
/*  30:    */   private static final long serialVersionUID = -8080574605631027263L;
/*  31:    */   protected ResultHistoryPanel m_history;
/*  32: 57 */   protected AttributeSummaryPerspective m_summarizer = new AttributeSummaryPerspective();
/*  33: 61 */   protected JButton m_clearButton = new JButton("Clear results");
/*  34:    */   protected JSplitPane m_splitPane;
/*  35:    */   protected Instances m_currentInstances;
/*  36:    */   
/*  37:    */   public String getViewerName()
/*  38:    */   {
/*  39: 76 */     return "Attribute Summarizer";
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void init()
/*  43:    */     throws WekaException
/*  44:    */   {
/*  45: 86 */     addButton(this.m_clearButton);
/*  46:    */     
/*  47: 88 */     this.m_history = new ResultHistoryPanel(null);
/*  48: 89 */     this.m_history.setBorder(BorderFactory.createTitledBorder("Result list"));
/*  49: 90 */     this.m_history.setHandleRightClicks(false);
/*  50:    */     
/*  51: 92 */     this.m_history.getList().addMouseListener(new ResultHistoryPanel.RMouseAdapter()
/*  52:    */     {
/*  53:    */       private static final long serialVersionUID = -5174882230278923704L;
/*  54:    */       
/*  55:    */       public void mouseClicked(MouseEvent e)
/*  56:    */       {
/*  57: 98 */         int index = AttributeSummarizerInteractiveView.this.m_history.getList().locationToIndex(e.getPoint());
/*  58: 99 */         if (index != -1)
/*  59:    */         {
/*  60:100 */           String name = AttributeSummarizerInteractiveView.this.m_history.getNameAtIndex(index);
/*  61:    */           
/*  62:102 */           Object inst = AttributeSummarizerInteractiveView.this.m_history.getNamedObject(name);
/*  63:103 */           if ((inst instanceof Instances))
/*  64:    */           {
/*  65:104 */             AttributeSummarizerInteractiveView.this.m_currentInstances = ((Instances)inst);
/*  66:105 */             AttributeSummarizerInteractiveView.this.m_summarizer.setInstances((Instances)inst, AttributeSummarizerInteractiveView.this.getSettings());
/*  67:106 */             AttributeSummarizerInteractiveView.this.m_summarizer.repaint();
/*  68:107 */             AttributeSummarizerInteractiveView.this.m_parent.revalidate();
/*  69:    */           }
/*  70:    */         }
/*  71:    */       }
/*  72:112 */     });
/*  73:113 */     this.m_history.getList().getSelectionModel().addListSelectionListener(new ListSelectionListener()
/*  74:    */     {
/*  75:    */       public void valueChanged(ListSelectionEvent e)
/*  76:    */       {
/*  77:117 */         if (!e.getValueIsAdjusting())
/*  78:    */         {
/*  79:118 */           ListSelectionModel lm = (ListSelectionModel)e.getSource();
/*  80:119 */           for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
/*  81:120 */             if (lm.isSelectedIndex(i))
/*  82:    */             {
/*  83:122 */               if (i == -1) {
/*  84:    */                 break;
/*  85:    */               }
/*  86:123 */               String name = AttributeSummarizerInteractiveView.this.m_history.getNameAtIndex(i);
/*  87:124 */               Object inst = AttributeSummarizerInteractiveView.this.m_history.getNamedObject(name);
/*  88:125 */               if ((inst != null) && ((inst instanceof Instances)))
/*  89:    */               {
/*  90:126 */                 AttributeSummarizerInteractiveView.this.m_currentInstances = ((Instances)inst);
/*  91:127 */                 AttributeSummarizerInteractiveView.this.m_summarizer.setInstances((Instances)inst, AttributeSummarizerInteractiveView.this.getSettings());
/*  92:128 */                 AttributeSummarizerInteractiveView.this.m_summarizer.repaint();
/*  93:129 */                 AttributeSummarizerInteractiveView.this.m_parent.revalidate();
/*  94:    */               }
/*  95:131 */               break;
/*  96:    */             }
/*  97:    */           }
/*  98:    */         }
/*  99:    */       }
/* 100:138 */     });
/* 101:139 */     this.m_summarizer.setPreferredSize(new Dimension(800, 600));
/* 102:140 */     this.m_splitPane = new JSplitPane(1, this.m_history, this.m_summarizer);
/* 103:    */     
/* 104:142 */     add(this.m_splitPane, "Center");
/* 105:143 */     boolean first = true;
/* 106:144 */     for (Data d : ((AttributeSummarizer)getStep()).getDatasets())
/* 107:    */     {
/* 108:145 */       String title = d.getPayloadElement("aux_textTitle").toString();
/* 109:    */       
/* 110:147 */       this.m_history.addResult(title, new StringBuffer());
/* 111:148 */       Instances instances = (Instances)d.getPrimaryPayload();
/* 112:149 */       this.m_history.addObject(title, instances);
/* 113:150 */       if (first)
/* 114:    */       {
/* 115:151 */         this.m_summarizer.setInstances(instances, getSettings());
/* 116:152 */         this.m_summarizer.repaint();
/* 117:153 */         first = false;
/* 118:    */       }
/* 119:    */     }
/* 120:157 */     this.m_clearButton.addActionListener(new ActionListener()
/* 121:    */     {
/* 122:    */       public void actionPerformed(ActionEvent e)
/* 123:    */       {
/* 124:160 */         AttributeSummarizerInteractiveView.this.m_history.clearResults();
/* 125:161 */         ((AttributeSummarizer)AttributeSummarizerInteractiveView.this.getStep()).getDatasets().clear();
/* 126:162 */         AttributeSummarizerInteractiveView.this.m_splitPane.remove(AttributeSummarizerInteractiveView.this.m_summarizer);
/* 127:    */       }
/* 128:    */     });
/* 129:    */   }
/* 130:    */   
/* 131:    */   public Defaults getDefaultSettings()
/* 132:    */   {
/* 133:174 */     return new AttributeSummaryPerspective().getDefaultSettings();
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void applySettings(Settings settings)
/* 137:    */   {
/* 138:184 */     this.m_summarizer.setInstances(this.m_currentInstances, getSettings());
/* 139:185 */     this.m_summarizer.repaint();
/* 140:186 */     this.m_parent.revalidate();
/* 141:    */   }
/* 142:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.AttributeSummarizerInteractiveView
 * JD-Core Version:    0.7.0.1
 */