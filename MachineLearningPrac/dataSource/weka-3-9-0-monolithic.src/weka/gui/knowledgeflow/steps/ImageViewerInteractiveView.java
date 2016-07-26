/*   1:    */ package weka.gui.knowledgeflow.steps;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.Graphics;
/*   6:    */ import java.awt.Graphics2D;
/*   7:    */ import java.awt.Image;
/*   8:    */ import java.awt.Toolkit;
/*   9:    */ import java.awt.event.ActionEvent;
/*  10:    */ import java.awt.event.ActionListener;
/*  11:    */ import java.awt.event.MouseEvent;
/*  12:    */ import java.awt.image.BufferedImage;
/*  13:    */ import java.net.URL;
/*  14:    */ import java.util.Map;
/*  15:    */ import java.util.Map.Entry;
/*  16:    */ import javax.swing.BorderFactory;
/*  17:    */ import javax.swing.ImageIcon;
/*  18:    */ import javax.swing.JButton;
/*  19:    */ import javax.swing.JList;
/*  20:    */ import javax.swing.JPanel;
/*  21:    */ import javax.swing.JScrollPane;
/*  22:    */ import javax.swing.JSplitPane;
/*  23:    */ import javax.swing.JToolBar;
/*  24:    */ import javax.swing.ListModel;
/*  25:    */ import javax.swing.ListSelectionModel;
/*  26:    */ import javax.swing.event.ListSelectionEvent;
/*  27:    */ import javax.swing.event.ListSelectionListener;
/*  28:    */ import weka.core.WekaException;
/*  29:    */ import weka.gui.ResultHistoryPanel;
/*  30:    */ import weka.gui.ResultHistoryPanel.RMouseAdapter;
/*  31:    */ import weka.gui.knowledgeflow.BaseInteractiveViewer;
/*  32:    */ import weka.knowledgeflow.steps.ImageViewer;
/*  33:    */ 
/*  34:    */ public class ImageViewerInteractiveView
/*  35:    */   extends BaseInteractiveViewer
/*  36:    */ {
/*  37:    */   private static final long serialVersionUID = -6652203133445653870L;
/*  38:    */   protected JButton m_clearButton;
/*  39:    */   protected ResultHistoryPanel m_history;
/*  40:    */   protected ImageDisplayer m_plotter;
/*  41:    */   
/*  42:    */   public ImageViewerInteractiveView()
/*  43:    */   {
/*  44: 63 */     this.m_clearButton = new JButton("Clear results");
/*  45:    */   }
/*  46:    */   
/*  47:    */   public String getViewerName()
/*  48:    */   {
/*  49: 78 */     return "Image Viewer";
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void init()
/*  53:    */     throws WekaException
/*  54:    */   {
/*  55: 88 */     addButton(this.m_clearButton);
/*  56:    */     
/*  57: 90 */     this.m_plotter = new ImageDisplayer();
/*  58: 91 */     this.m_plotter.setMinimumSize(new Dimension(810, 610));
/*  59: 92 */     this.m_plotter.setPreferredSize(new Dimension(810, 610));
/*  60:    */     
/*  61: 94 */     this.m_history = new ResultHistoryPanel(null);
/*  62: 95 */     this.m_history.setBorder(BorderFactory.createTitledBorder("Image list"));
/*  63: 96 */     this.m_history.setHandleRightClicks(false);
/*  64: 97 */     this.m_history.getList().addMouseListener(new ResultHistoryPanel.RMouseAdapter()
/*  65:    */     {
/*  66:    */       private static final long serialVersionUID = -4984130887963944249L;
/*  67:    */       
/*  68:    */       public void mouseClicked(MouseEvent e)
/*  69:    */       {
/*  70:102 */         int index = ImageViewerInteractiveView.this.m_history.getList().locationToIndex(e.getPoint());
/*  71:103 */         if (index != -1)
/*  72:    */         {
/*  73:104 */           String name = ImageViewerInteractiveView.this.m_history.getNameAtIndex(index);
/*  74:    */           
/*  75:106 */           Object pic = ImageViewerInteractiveView.this.m_history.getNamedObject(name);
/*  76:107 */           if ((pic instanceof BufferedImage))
/*  77:    */           {
/*  78:108 */             ImageViewerInteractiveView.this.m_plotter.setImage((BufferedImage)pic);
/*  79:109 */             ImageViewerInteractiveView.this.m_plotter.repaint();
/*  80:    */           }
/*  81:    */         }
/*  82:    */       }
/*  83:114 */     });
/*  84:115 */     this.m_history.getList().getSelectionModel().addListSelectionListener(new ListSelectionListener()
/*  85:    */     {
/*  86:    */       public void valueChanged(ListSelectionEvent e)
/*  87:    */       {
/*  88:118 */         if (!e.getValueIsAdjusting())
/*  89:    */         {
/*  90:119 */           ListSelectionModel lm = (ListSelectionModel)e.getSource();
/*  91:120 */           for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
/*  92:121 */             if (lm.isSelectedIndex(i))
/*  93:    */             {
/*  94:123 */               if (i == -1) {
/*  95:    */                 break;
/*  96:    */               }
/*  97:124 */               String name = ImageViewerInteractiveView.this.m_history.getNameAtIndex(i);
/*  98:125 */               Object pic = ImageViewerInteractiveView.this.m_history.getNamedObject(name);
/*  99:126 */               if ((pic != null) && ((pic instanceof BufferedImage)))
/* 100:    */               {
/* 101:127 */                 ImageViewerInteractiveView.this.m_plotter.setImage((BufferedImage)pic);
/* 102:128 */                 ImageViewerInteractiveView.this.m_plotter.repaint();
/* 103:    */               }
/* 104:130 */               break;
/* 105:    */             }
/* 106:    */           }
/* 107:    */         }
/* 108:    */       }
/* 109:137 */     });
/* 110:138 */     MainPanel mainPanel = new MainPanel(this.m_history, this.m_plotter);
/* 111:139 */     add(mainPanel, "Center");
/* 112:    */     
/* 113:141 */     boolean first = true;
/* 114:142 */     for (Map.Entry<String, BufferedImage> e : ((ImageViewer)getStep()).getImages().entrySet())
/* 115:    */     {
/* 116:144 */       this.m_history.addResult((String)e.getKey(), new StringBuffer());
/* 117:145 */       this.m_history.addObject((String)e.getKey(), e.getValue());
/* 118:146 */       if (first)
/* 119:    */       {
/* 120:147 */         this.m_plotter.setImage((BufferedImage)e.getValue());
/* 121:148 */         this.m_plotter.repaint();
/* 122:149 */         first = false;
/* 123:    */       }
/* 124:    */     }
/* 125:153 */     if (this.m_history.getList().getModel().getSize() > 0) {
/* 126:154 */       this.m_history.getList().setSelectedIndex(0);
/* 127:    */     }
/* 128:157 */     this.m_clearButton.addActionListener(new ActionListener()
/* 129:    */     {
/* 130:    */       public void actionPerformed(ActionEvent e)
/* 131:    */       {
/* 132:160 */         ImageViewerInteractiveView.this.m_history.clearResults();
/* 133:161 */         ((ImageViewer)ImageViewerInteractiveView.this.getStep()).getImages().clear();
/* 134:162 */         ImageViewerInteractiveView.this.m_plotter.setImage(null);
/* 135:163 */         ImageViewerInteractiveView.this.m_plotter.repaint();
/* 136:    */       }
/* 137:    */     });
/* 138:    */   }
/* 139:    */   
/* 140:    */   protected static class MainPanel
/* 141:    */     extends JPanel
/* 142:    */   {
/* 143:    */     private static final long serialVersionUID = 5648976848887609072L;
/* 144:    */     
/* 145:    */     private static Image loadImage(String path)
/* 146:    */     {
/* 147:176 */       Image pic = null;
/* 148:177 */       URL imageURL = ImageViewer.class.getClassLoader().getResource(path);
/* 149:181 */       if (imageURL != null) {
/* 150:183 */         pic = Toolkit.getDefaultToolkit().getImage(imageURL);
/* 151:    */       }
/* 152:185 */       return pic;
/* 153:    */     }
/* 154:    */     
/* 155:    */     public MainPanel(ResultHistoryPanel p, final ImageViewerInteractiveView.ImageDisplayer id)
/* 156:    */     {
/* 157:201 */       setLayout(new BorderLayout());
/* 158:    */       
/* 159:203 */       JPanel topP = new JPanel();
/* 160:204 */       topP.setLayout(new BorderLayout());
/* 161:    */       
/* 162:206 */       JPanel holder = new JPanel();
/* 163:207 */       holder.setLayout(new BorderLayout());
/* 164:208 */       holder.setBorder(BorderFactory.createTitledBorder("Image"));
/* 165:209 */       JToolBar tools = new JToolBar();
/* 166:210 */       tools.setOrientation(0);
/* 167:211 */       JButton zoomInB = new JButton(new ImageIcon(loadImage("weka/gui/knowledgeflow/icons/zoom_in.png")));
/* 168:    */       
/* 169:    */ 
/* 170:    */ 
/* 171:215 */       zoomInB.addActionListener(new ActionListener()
/* 172:    */       {
/* 173:    */         public void actionPerformed(ActionEvent e)
/* 174:    */         {
/* 175:217 */           int z = id.getZoom();
/* 176:218 */           z += 25;
/* 177:219 */           if (z >= 200) {
/* 178:220 */             z = 200;
/* 179:    */           }
/* 180:223 */           id.setZoom(z);
/* 181:224 */           id.repaint();
/* 182:    */         }
/* 183:227 */       });
/* 184:228 */       JButton zoomOutB = new JButton(new ImageIcon(loadImage("weka/gui/knowledgeflow/icons/zoom_out.png")));
/* 185:    */       
/* 186:    */ 
/* 187:231 */       zoomOutB.addActionListener(new ActionListener()
/* 188:    */       {
/* 189:    */         public void actionPerformed(ActionEvent e)
/* 190:    */         {
/* 191:233 */           int z = id.getZoom();
/* 192:234 */           z -= 25;
/* 193:235 */           if (z <= 50) {
/* 194:236 */             z = 50;
/* 195:    */           }
/* 196:239 */           id.setZoom(z);
/* 197:240 */           id.repaint();
/* 198:    */         }
/* 199:243 */       });
/* 200:244 */       tools.add(zoomInB);
/* 201:245 */       tools.add(zoomOutB);
/* 202:246 */       holder.add(tools, "North");
/* 203:    */       
/* 204:248 */       JScrollPane js = new JScrollPane(id);
/* 205:249 */       holder.add(js, "Center");
/* 206:250 */       JSplitPane p2 = new JSplitPane(1, p, holder);
/* 207:    */       
/* 208:252 */       topP.add(p2, "Center");
/* 209:    */       
/* 210:    */ 
/* 211:255 */       add(topP, "Center");
/* 212:    */     }
/* 213:    */   }
/* 214:    */   
/* 215:    */   protected static class ImageDisplayer
/* 216:    */     extends JPanel
/* 217:    */   {
/* 218:    */     private static final long serialVersionUID = 4161957589912537357L;
/* 219:    */     private BufferedImage m_image;
/* 220:273 */     private int m_imageZoom = 100;
/* 221:    */     
/* 222:    */     public void setImage(BufferedImage image)
/* 223:    */     {
/* 224:281 */       this.m_image = image;
/* 225:    */     }
/* 226:    */     
/* 227:    */     public void setZoom(int zoom)
/* 228:    */     {
/* 229:285 */       this.m_imageZoom = zoom;
/* 230:    */     }
/* 231:    */     
/* 232:    */     public int getZoom()
/* 233:    */     {
/* 234:289 */       return this.m_imageZoom;
/* 235:    */     }
/* 236:    */     
/* 237:    */     public void paintComponent(Graphics g)
/* 238:    */     {
/* 239:299 */       super.paintComponent(g);
/* 240:301 */       if (this.m_image != null)
/* 241:    */       {
/* 242:302 */         double lz = this.m_imageZoom / 100.0D;
/* 243:303 */         ((Graphics2D)g).scale(lz, lz);
/* 244:304 */         int plotWidth = this.m_image.getWidth();
/* 245:305 */         int plotHeight = this.m_image.getHeight();
/* 246:    */         
/* 247:307 */         int ourWidth = getWidth();
/* 248:308 */         int ourHeight = getHeight();
/* 249:    */         
/* 250:    */ 
/* 251:311 */         int x = 0;int y = 0;
/* 252:312 */         if (plotWidth < ourWidth) {
/* 253:313 */           x = (ourWidth - plotWidth) / 2;
/* 254:    */         }
/* 255:315 */         if (plotHeight < ourHeight) {
/* 256:316 */           y = (ourHeight - plotHeight) / 2;
/* 257:    */         }
/* 258:319 */         g.drawImage(this.m_image, x, y, this);
/* 259:320 */         setPreferredSize(new Dimension(plotWidth, plotHeight));
/* 260:321 */         revalidate();
/* 261:    */       }
/* 262:    */     }
/* 263:    */   }
/* 264:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.steps.ImageViewerInteractiveView
 * JD-Core Version:    0.7.0.1
 */