/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.event.ActionEvent;
/*   6:    */ import java.awt.event.ActionListener;
/*   7:    */ import java.awt.event.MouseEvent;
/*   8:    */ import java.util.List;
/*   9:    */ import javax.swing.BorderFactory;
/*  10:    */ import javax.swing.JButton;
/*  11:    */ import javax.swing.JList;
/*  12:    */ import javax.swing.JPanel;
/*  13:    */ import javax.swing.JSplitPane;
/*  14:    */ import javax.swing.ListSelectionModel;
/*  15:    */ import javax.swing.event.ListSelectionEvent;
/*  16:    */ import javax.swing.event.ListSelectionListener;
/*  17:    */ import weka.core.WekaException;
/*  18:    */ import weka.gui.ResultHistoryPanel;
/*  19:    */ import weka.gui.ResultHistoryPanel.RMouseAdapter;
/*  20:    */ import weka.gui.graphvisualizer.BIFFormatException;
/*  21:    */ import weka.gui.graphvisualizer.GraphVisualizer;
/*  22:    */ import weka.gui.knowledgeflow.BaseInteractiveViewer;
/*  23:    */ import weka.gui.treevisualizer.PlaceNode2;
/*  24:    */ import weka.gui.treevisualizer.TreeVisualizer;
/*  25:    */ import weka.knowledgeflow.Data;
/*  26:    */ import weka.knowledgeflow.steps.GraphViewer;
/*  27:    */ 
/*  28:    */ public class GraphViewerInteractiveView
/*  29:    */   extends BaseInteractiveViewer
/*  30:    */ {
/*  31:    */   private static final long serialVersionUID = 2109423349272114409L;
/*  32:    */   protected ResultHistoryPanel m_history;
/*  33: 58 */   protected JButton m_clearButton = new JButton("Clear results");
/*  34:    */   protected JSplitPane m_splitPane;
/*  35:    */   protected TreeVisualizer m_treeVisualizer;
/*  36:    */   protected GraphVisualizer m_graphVisualizer;
/*  37: 70 */   JPanel m_holderPanel = new JPanel(new BorderLayout());
/*  38:    */   
/*  39:    */   public String getViewerName()
/*  40:    */   {
/*  41: 79 */     return "Graph Viewer";
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void init()
/*  45:    */     throws WekaException
/*  46:    */   {
/*  47: 89 */     addButton(this.m_clearButton);
/*  48:    */     
/*  49: 91 */     this.m_history = new ResultHistoryPanel(null);
/*  50: 92 */     this.m_history.setBorder(BorderFactory.createTitledBorder("Result list"));
/*  51: 93 */     this.m_history.setHandleRightClicks(false);
/*  52:    */     
/*  53: 95 */     this.m_history.getList().addMouseListener(new ResultHistoryPanel.RMouseAdapter()
/*  54:    */     {
/*  55:    */       private static final long serialVersionUID = -5174882230278923704L;
/*  56:    */       
/*  57:    */       public void mouseClicked(MouseEvent e)
/*  58:    */       {
/*  59: 99 */         int index = GraphViewerInteractiveView.this.m_history.getList().locationToIndex(e.getPoint());
/*  60:100 */         if (index != -1)
/*  61:    */         {
/*  62:101 */           String name = GraphViewerInteractiveView.this.m_history.getNameAtIndex(index);
/*  63:    */           
/*  64:103 */           Object data = GraphViewerInteractiveView.this.m_history.getNamedObject(name);
/*  65:104 */           if ((data instanceof Data))
/*  66:    */           {
/*  67:105 */             String grphString = (String)((Data)data).getPrimaryPayload();
/*  68:106 */             Integer grphType = (Integer)((Data)data).getPayloadElement("graph_type");
/*  69:107 */             if ((GraphViewerInteractiveView.this.m_treeVisualizer != null) || (GraphViewerInteractiveView.this.m_graphVisualizer != null)) {
/*  70:108 */               GraphViewerInteractiveView.this.m_holderPanel.remove(GraphViewerInteractiveView.this.m_treeVisualizer != null ? GraphViewerInteractiveView.this.m_treeVisualizer : GraphViewerInteractiveView.this.m_graphVisualizer);
/*  71:    */             }
/*  72:110 */             if (grphType.intValue() == 1)
/*  73:    */             {
/*  74:111 */               GraphViewerInteractiveView.this.m_treeVisualizer = new TreeVisualizer(null, grphString, new PlaceNode2());
/*  75:112 */               GraphViewerInteractiveView.this.m_holderPanel.add(GraphViewerInteractiveView.this.m_treeVisualizer, "Center");
/*  76:113 */               GraphViewerInteractiveView.this.m_splitPane.revalidate();
/*  77:    */             }
/*  78:114 */             else if (grphType.intValue() == 2)
/*  79:    */             {
/*  80:115 */               GraphViewerInteractiveView.this.m_graphVisualizer = new GraphVisualizer();
/*  81:    */               try
/*  82:    */               {
/*  83:117 */                 GraphViewerInteractiveView.this.m_graphVisualizer.readBIF(grphString);
/*  84:    */               }
/*  85:    */               catch (BIFFormatException ex)
/*  86:    */               {
/*  87:119 */                 ex.printStackTrace();
/*  88:    */               }
/*  89:121 */               GraphViewerInteractiveView.this.m_graphVisualizer.layoutGraph();
/*  90:122 */               GraphViewerInteractiveView.this.m_holderPanel.add(GraphViewerInteractiveView.this.m_graphVisualizer, "Center");
/*  91:123 */               GraphViewerInteractiveView.this.m_splitPane.revalidate();
/*  92:    */             }
/*  93:    */           }
/*  94:    */         }
/*  95:    */       }
/*  96:129 */     });
/*  97:130 */     this.m_history.getList().getSelectionModel().addListSelectionListener(new ListSelectionListener()
/*  98:    */     {
/*  99:    */       public void valueChanged(ListSelectionEvent e)
/* 100:    */       {
/* 101:134 */         if (!e.getValueIsAdjusting())
/* 102:    */         {
/* 103:135 */           ListSelectionModel lm = (ListSelectionModel)e.getSource();
/* 104:136 */           for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
/* 105:137 */             if (lm.isSelectedIndex(i))
/* 106:    */             {
/* 107:139 */               if (i == -1) {
/* 108:    */                 break;
/* 109:    */               }
/* 110:140 */               String name = GraphViewerInteractiveView.this.m_history.getNameAtIndex(i);
/* 111:141 */               Object data = GraphViewerInteractiveView.this.m_history.getNamedObject(name);
/* 112:142 */               if ((data != null) && ((data instanceof Data)))
/* 113:    */               {
/* 114:143 */                 String grphString = (String)((Data)data).getPrimaryPayload();
/* 115:144 */                 Integer grphType = (Integer)((Data)data).getPayloadElement("graph_type");
/* 116:147 */                 if ((GraphViewerInteractiveView.this.m_treeVisualizer != null) || (GraphViewerInteractiveView.this.m_graphVisualizer != null)) {
/* 117:148 */                   GraphViewerInteractiveView.this.m_holderPanel.remove(GraphViewerInteractiveView.this.m_treeVisualizer != null ? GraphViewerInteractiveView.this.m_treeVisualizer : GraphViewerInteractiveView.this.m_graphVisualizer);
/* 118:    */                 }
/* 119:152 */                 if (grphType.intValue() == 1)
/* 120:    */                 {
/* 121:153 */                   GraphViewerInteractiveView.this.m_treeVisualizer = new TreeVisualizer(null, grphString, new PlaceNode2());
/* 122:    */                   
/* 123:155 */                   GraphViewerInteractiveView.this.m_holderPanel.add(GraphViewerInteractiveView.this.m_treeVisualizer, "Center");
/* 124:156 */                   GraphViewerInteractiveView.this.m_splitPane.revalidate();
/* 125:    */                 }
/* 126:157 */                 else if (grphType.intValue() == 2)
/* 127:    */                 {
/* 128:158 */                   GraphViewerInteractiveView.this.m_graphVisualizer = new GraphVisualizer();
/* 129:    */                   try
/* 130:    */                   {
/* 131:160 */                     GraphViewerInteractiveView.this.m_graphVisualizer.readBIF(grphString);
/* 132:    */                   }
/* 133:    */                   catch (BIFFormatException ex)
/* 134:    */                   {
/* 135:162 */                     ex.printStackTrace();
/* 136:    */                   }
/* 137:164 */                   GraphViewerInteractiveView.this.m_graphVisualizer.layoutGraph();
/* 138:165 */                   GraphViewerInteractiveView.this.m_holderPanel.add(GraphViewerInteractiveView.this.m_graphVisualizer, "Center");
/* 139:166 */                   GraphViewerInteractiveView.this.m_splitPane.revalidate();
/* 140:    */                 }
/* 141:    */               }
/* 142:169 */               break;
/* 143:    */             }
/* 144:    */           }
/* 145:    */         }
/* 146:    */       }
/* 147:176 */     });
/* 148:177 */     this.m_splitPane = new JSplitPane(1, this.m_history, this.m_holderPanel);
/* 149:    */     
/* 150:    */ 
/* 151:180 */     add(this.m_splitPane, "Center");
/* 152:181 */     this.m_holderPanel.setPreferredSize(new Dimension(800, 600));
/* 153:    */     
/* 154:183 */     boolean first = true;
/* 155:184 */     for (Data d : ((GraphViewer)getStep()).getDatasets())
/* 156:    */     {
/* 157:185 */       String title = (String)d.getPayloadElement("graph_title");
/* 158:186 */       this.m_history.addResult(title, new StringBuffer());
/* 159:187 */       this.m_history.addObject(title, d);
/* 160:188 */       if (first)
/* 161:    */       {
/* 162:189 */         String grphString = (String)d.getPrimaryPayload();
/* 163:190 */         Integer grphType = (Integer)d.getPayloadElement("graph_type");
/* 164:192 */         if (grphType.intValue() == 1)
/* 165:    */         {
/* 166:193 */           this.m_treeVisualizer = new TreeVisualizer(null, grphString, new PlaceNode2());
/* 167:    */           
/* 168:    */ 
/* 169:196 */           this.m_holderPanel.add(this.m_treeVisualizer, "Center");
/* 170:    */         }
/* 171:197 */         else if (grphType.intValue() == 2)
/* 172:    */         {
/* 173:198 */           this.m_graphVisualizer = new GraphVisualizer();
/* 174:    */           try
/* 175:    */           {
/* 176:200 */             this.m_graphVisualizer.readBIF(grphString);
/* 177:    */           }
/* 178:    */           catch (BIFFormatException ex)
/* 179:    */           {
/* 180:202 */             ex.printStackTrace();
/* 181:    */           }
/* 182:204 */           this.m_graphVisualizer.layoutGraph();
/* 183:205 */           this.m_holderPanel.add(this.m_graphVisualizer, "Center");
/* 184:    */         }
/* 185:207 */         this.m_splitPane.revalidate();
/* 186:208 */         first = false;
/* 187:    */       }
/* 188:    */     }
/* 189:212 */     this.m_clearButton.addActionListener(new ActionListener()
/* 190:    */     {
/* 191:    */       public void actionPerformed(ActionEvent e)
/* 192:    */       {
/* 193:215 */         GraphViewerInteractiveView.this.m_history.clearResults();
/* 194:216 */         ((GraphViewer)GraphViewerInteractiveView.this.getStep()).getDatasets().clear();
/* 195:217 */         if ((GraphViewerInteractiveView.this.m_treeVisualizer != null) || (GraphViewerInteractiveView.this.m_graphVisualizer != null))
/* 196:    */         {
/* 197:218 */           GraphViewerInteractiveView.this.m_splitPane.remove(GraphViewerInteractiveView.this.m_holderPanel);
/* 198:    */           
/* 199:220 */           GraphViewerInteractiveView.this.revalidate();
/* 200:    */         }
/* 201:    */       }
/* 202:    */     });
/* 203:    */   }
/* 204:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.GraphViewerInteractiveView
 * JD-Core Version:    0.7.0.1
 */