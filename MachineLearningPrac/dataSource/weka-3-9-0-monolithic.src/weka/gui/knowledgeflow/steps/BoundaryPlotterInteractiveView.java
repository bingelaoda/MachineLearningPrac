/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.Dimension;
/*   4:    */ import java.awt.event.ActionEvent;
/*   5:    */ import java.awt.event.ActionListener;
/*   6:    */ import java.awt.event.MouseEvent;
/*   7:    */ import java.awt.image.BufferedImage;
/*   8:    */ import java.util.Map;
/*   9:    */ import java.util.Map.Entry;
/*  10:    */ import javax.swing.BorderFactory;
/*  11:    */ import javax.swing.JButton;
/*  12:    */ import javax.swing.JList;
/*  13:    */ import javax.swing.ListSelectionModel;
/*  14:    */ import javax.swing.event.ListSelectionEvent;
/*  15:    */ import javax.swing.event.ListSelectionListener;
/*  16:    */ import weka.core.WekaException;
/*  17:    */ import weka.gui.ResultHistoryPanel;
/*  18:    */ import weka.gui.ResultHistoryPanel.RMouseAdapter;
/*  19:    */ import weka.gui.knowledgeflow.BaseInteractiveViewer;
/*  20:    */ import weka.knowledgeflow.steps.BoundaryPlotter;
/*  21:    */ import weka.knowledgeflow.steps.BoundaryPlotter.RenderingUpdateListener;
/*  22:    */ 
/*  23:    */ public class BoundaryPlotterInteractiveView
/*  24:    */   extends BaseInteractiveViewer
/*  25:    */   implements BoundaryPlotter.RenderingUpdateListener
/*  26:    */ {
/*  27:    */   private static final long serialVersionUID = 5567187861739468636L;
/*  28: 49 */   protected JButton m_clearButton = new JButton("Clear results");
/*  29:    */   protected ResultHistoryPanel m_history;
/*  30:    */   protected ImageViewerInteractiveView.ImageDisplayer m_plotter;
/*  31:    */   
/*  32:    */   public String getViewerName()
/*  33:    */   {
/*  34: 64 */     return "Boundary Visualizer";
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void init()
/*  38:    */     throws WekaException
/*  39:    */   {
/*  40: 74 */     addButton(this.m_clearButton);
/*  41:    */     
/*  42: 76 */     this.m_plotter = new ImageViewerInteractiveView.ImageDisplayer();
/*  43: 77 */     this.m_plotter.setMinimumSize(new Dimension(810, 610));
/*  44: 78 */     this.m_plotter.setPreferredSize(new Dimension(810, 610));
/*  45:    */     
/*  46: 80 */     this.m_history = new ResultHistoryPanel(null);
/*  47: 81 */     this.m_history.setBorder(BorderFactory.createTitledBorder("Image list"));
/*  48: 82 */     this.m_history.setHandleRightClicks(false);
/*  49: 83 */     this.m_history.getList().addMouseListener(new ResultHistoryPanel.RMouseAdapter()
/*  50:    */     {
/*  51:    */       private static final long serialVersionUID = -4984130887963944249L;
/*  52:    */       
/*  53:    */       public void mouseClicked(MouseEvent e)
/*  54:    */       {
/*  55: 90 */         int index = BoundaryPlotterInteractiveView.this.m_history.getList().locationToIndex(e.getPoint());
/*  56: 91 */         if (index != -1)
/*  57:    */         {
/*  58: 92 */           String name = BoundaryPlotterInteractiveView.this.m_history.getNameAtIndex(index);
/*  59:    */           
/*  60: 94 */           Object pic = BoundaryPlotterInteractiveView.this.m_history.getNamedObject(name);
/*  61: 95 */           if ((pic instanceof BufferedImage))
/*  62:    */           {
/*  63: 96 */             BoundaryPlotterInteractiveView.this.m_plotter.setImage((BufferedImage)pic);
/*  64: 97 */             BoundaryPlotterInteractiveView.this.m_plotter.repaint();
/*  65:    */           }
/*  66:    */         }
/*  67:    */       }
/*  68:102 */     });
/*  69:103 */     this.m_history.getList().getSelectionModel().addListSelectionListener(new ListSelectionListener()
/*  70:    */     {
/*  71:    */       public void valueChanged(ListSelectionEvent e)
/*  72:    */       {
/*  73:107 */         if (!e.getValueIsAdjusting())
/*  74:    */         {
/*  75:108 */           ListSelectionModel lm = (ListSelectionModel)e.getSource();
/*  76:109 */           for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
/*  77:110 */             if (lm.isSelectedIndex(i))
/*  78:    */             {
/*  79:112 */               if (i == -1) {
/*  80:    */                 break;
/*  81:    */               }
/*  82:113 */               String name = BoundaryPlotterInteractiveView.this.m_history.getNameAtIndex(i);
/*  83:114 */               Object pic = BoundaryPlotterInteractiveView.this.m_history.getNamedObject(name);
/*  84:115 */               if ((pic != null) && ((pic instanceof BufferedImage)))
/*  85:    */               {
/*  86:116 */                 BoundaryPlotterInteractiveView.this.m_plotter.setImage((BufferedImage)pic);
/*  87:117 */                 BoundaryPlotterInteractiveView.this.m_plotter.repaint();
/*  88:    */               }
/*  89:119 */               break;
/*  90:    */             }
/*  91:    */           }
/*  92:    */         }
/*  93:    */       }
/*  94:126 */     });
/*  95:127 */     ImageViewerInteractiveView.MainPanel mainPanel = new ImageViewerInteractiveView.MainPanel(this.m_history, this.m_plotter);
/*  96:    */     
/*  97:129 */     add(mainPanel, "Center");
/*  98:    */     
/*  99:131 */     boolean first = true;
/* 100:    */     
/* 101:133 */     Map<String, BufferedImage> images = ((BoundaryPlotter)getStep()).getImages();
/* 102:135 */     if (images != null) {
/* 103:136 */       for (Map.Entry<String, BufferedImage> e : images.entrySet())
/* 104:    */       {
/* 105:137 */         this.m_history.addResult((String)e.getKey(), new StringBuffer());
/* 106:138 */         this.m_history.addObject((String)e.getKey(), e.getValue());
/* 107:139 */         if (first)
/* 108:    */         {
/* 109:140 */           this.m_plotter.setImage((BufferedImage)e.getValue());
/* 110:141 */           this.m_plotter.repaint();
/* 111:142 */           first = false;
/* 112:    */         }
/* 113:    */       }
/* 114:    */     }
/* 115:147 */     this.m_clearButton.addActionListener(new ActionListener()
/* 116:    */     {
/* 117:    */       public void actionPerformed(ActionEvent e)
/* 118:    */       {
/* 119:150 */         BoundaryPlotterInteractiveView.this.m_history.clearResults();
/* 120:    */         
/* 121:152 */         ((BoundaryPlotter)BoundaryPlotterInteractiveView.this.getStep()).getImages().clear();
/* 122:153 */         BoundaryPlotterInteractiveView.this.m_plotter.setImage(null);
/* 123:154 */         BoundaryPlotterInteractiveView.this.m_plotter.repaint();
/* 124:    */       }
/* 125:157 */     });
/* 126:158 */     ((BoundaryPlotter)getStep()).setRenderingListener(this);
/* 127:    */   }
/* 128:    */   
/* 129:    */   public void renderingImageUpdate()
/* 130:    */   {
/* 131:166 */     this.m_plotter.repaint();
/* 132:    */   }
/* 133:    */   
/* 134:    */   public void newPlotStarted(String description)
/* 135:    */   {
/* 136:171 */     BufferedImage currentImage = ((BoundaryPlotter)getStep()).getCurrentImage();
/* 137:173 */     if (currentImage != null)
/* 138:    */     {
/* 139:174 */       this.m_history.addResult(description, new StringBuffer());
/* 140:175 */       this.m_history.addObject(description, currentImage);
/* 141:176 */       this.m_history.setSelectedListValue(description);
/* 142:177 */       this.m_plotter.setImage(currentImage);
/* 143:178 */       this.m_plotter.repaint();
/* 144:    */     }
/* 145:    */   }
/* 146:    */   
/* 147:    */   public void currentPlotRowCompleted(int row)
/* 148:    */   {
/* 149:189 */     this.m_plotter.repaint();
/* 150:    */   }
/* 151:    */   
/* 152:    */   public void closePressed()
/* 153:    */   {
/* 154:197 */     ((BoundaryPlotter)getStep()).removeRenderingListener(this);
/* 155:    */   }
/* 156:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.BoundaryPlotterInteractiveView
 * JD-Core Version:    0.7.0.1
 */