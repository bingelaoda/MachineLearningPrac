/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.Graphics;
/*   7:    */ import java.awt.Graphics2D;
/*   8:    */ import java.awt.Image;
/*   9:    */ import java.awt.Toolkit;
/*  10:    */ import java.awt.event.ActionEvent;
/*  11:    */ import java.awt.event.ActionListener;
/*  12:    */ import java.awt.event.MouseEvent;
/*  13:    */ import java.awt.event.WindowAdapter;
/*  14:    */ import java.awt.event.WindowEvent;
/*  15:    */ import java.awt.image.BufferedImage;
/*  16:    */ import java.beans.EventSetDescriptor;
/*  17:    */ import java.io.Serializable;
/*  18:    */ import java.net.URL;
/*  19:    */ import java.text.SimpleDateFormat;
/*  20:    */ import java.util.Date;
/*  21:    */ import java.util.Enumeration;
/*  22:    */ import java.util.Vector;
/*  23:    */ import javax.swing.BorderFactory;
/*  24:    */ import javax.swing.ImageIcon;
/*  25:    */ import javax.swing.JButton;
/*  26:    */ import javax.swing.JFrame;
/*  27:    */ import javax.swing.JList;
/*  28:    */ import javax.swing.JPanel;
/*  29:    */ import javax.swing.JScrollPane;
/*  30:    */ import javax.swing.JToolBar;
/*  31:    */ import javax.swing.ListSelectionModel;
/*  32:    */ import javax.swing.event.ListSelectionEvent;
/*  33:    */ import javax.swing.event.ListSelectionListener;
/*  34:    */ import weka.core.Environment;
/*  35:    */ import weka.gui.Logger;
/*  36:    */ import weka.gui.ResultHistoryPanel;
/*  37:    */ import weka.gui.ResultHistoryPanel.RMouseAdapter;
/*  38:    */ 
/*  39:    */ @KFStep(category="Visualization", toolTipText="Display static images")
/*  40:    */ public class ImageViewer
/*  41:    */   extends JPanel
/*  42:    */   implements ImageListener, BeanCommon, Visible, Serializable, UserRequestAcceptor
/*  43:    */ {
/*  44:    */   private static final long serialVersionUID = 7976930810628389750L;
/*  45:    */   protected ImageDisplayer m_plotter;
/*  46:    */   protected ResultHistoryPanel m_history;
/*  47: 79 */   private transient JFrame m_resultsFrame = null;
/*  48: 84 */   protected BeanVisual m_visual = new BeanVisual("ImageVisualizer", "weka/gui/beans/icons/StripChart.gif", "weka/gui/beans/icons/StripChart_animated.gif");
/*  49: 91 */   protected transient Logger m_logger = null;
/*  50:    */   protected transient Environment m_env;
/*  51:    */   
/*  52:    */   public ImageViewer()
/*  53:    */   {
/*  54:102 */     useDefaultVisual();
/*  55:103 */     setLayout(new BorderLayout());
/*  56:104 */     add(this.m_visual, "Center");
/*  57:    */     
/*  58:106 */     this.m_env = Environment.getSystemWide();
/*  59:107 */     this.m_plotter = new ImageDisplayer(null);
/*  60:    */     
/*  61:109 */     this.m_plotter.setMinimumSize(new Dimension(810, 610));
/*  62:110 */     this.m_plotter.setPreferredSize(new Dimension(810, 610));
/*  63:111 */     setUpResultHistory();
/*  64:    */   }
/*  65:    */   
/*  66:    */   public String globalInfo()
/*  67:    */   {
/*  68:120 */     return "Display static images";
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void useDefaultVisual()
/*  72:    */   {
/*  73:125 */     this.m_visual.loadIcons("weka/gui/beans/icons/StripChart.gif", "weka/gui/beans/icons/StripChart_animated.gif");
/*  74:    */     
/*  75:127 */     this.m_visual.setText("ImageViewer");
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void setVisual(BeanVisual newVisual)
/*  79:    */   {
/*  80:132 */     this.m_visual = newVisual;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public BeanVisual getVisual()
/*  84:    */   {
/*  85:137 */     return this.m_visual;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public void setCustomName(String name)
/*  89:    */   {
/*  90:142 */     this.m_visual.setText(name);
/*  91:    */   }
/*  92:    */   
/*  93:    */   public String getCustomName()
/*  94:    */   {
/*  95:147 */     return this.m_visual.getText();
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void stop() {}
/*  99:    */   
/* 100:    */   public boolean isBusy()
/* 101:    */   {
/* 102:156 */     return false;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public void setLog(Logger logger)
/* 106:    */   {
/* 107:161 */     this.m_logger = logger;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/* 111:    */   {
/* 112:167 */     return connectionAllowed(esd.getName());
/* 113:    */   }
/* 114:    */   
/* 115:    */   public boolean connectionAllowed(String eventName)
/* 116:    */   {
/* 117:172 */     return true;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public void connectionNotification(String eventName, Object source) {}
/* 121:    */   
/* 122:    */   public void disconnectionNotification(String eventName, Object source) {}
/* 123:    */   
/* 124:    */   public synchronized void acceptImage(ImageEvent imageE)
/* 125:    */   {
/* 126:186 */     BufferedImage image = imageE.getImage();
/* 127:187 */     String name = new SimpleDateFormat("HH:mm:ss:SS").format(new Date());
/* 128:188 */     name = ((imageE.getImageName() == null) || (imageE.getImageName().length() == 0) ? "Image at " : new StringBuilder().append(imageE.getImageName()).append(" ").toString()) + name;
/* 129:    */     
/* 130:    */ 
/* 131:    */ 
/* 132:    */ 
/* 133:193 */     this.m_history.addResult(name, new StringBuffer());
/* 134:194 */     this.m_history.addObject(name, image);
/* 135:195 */     this.m_plotter.setImage(image);
/* 136:196 */     this.m_plotter.repaint();
/* 137:    */   }
/* 138:    */   
/* 139:    */   protected void showResults()
/* 140:    */   {
/* 141:203 */     if (this.m_resultsFrame == null)
/* 142:    */     {
/* 143:204 */       if (this.m_history == null) {
/* 144:205 */         setUpResultHistory();
/* 145:    */       }
/* 146:207 */       this.m_resultsFrame = new JFrame("Image Viewer");
/* 147:208 */       this.m_resultsFrame.getContentPane().setLayout(new BorderLayout());
/* 148:209 */       this.m_resultsFrame.getContentPane().add(new MainPanel(this.m_history, this.m_plotter), "Center");
/* 149:    */       
/* 150:211 */       this.m_resultsFrame.addWindowListener(new WindowAdapter()
/* 151:    */       {
/* 152:    */         public void windowClosing(WindowEvent e)
/* 153:    */         {
/* 154:214 */           ImageViewer.this.m_resultsFrame.dispose();
/* 155:215 */           ImageViewer.this.m_resultsFrame = null;
/* 156:    */         }
/* 157:217 */       });
/* 158:218 */       this.m_resultsFrame.pack();
/* 159:219 */       this.m_resultsFrame.setVisible(true);
/* 160:    */     }
/* 161:    */     else
/* 162:    */     {
/* 163:221 */       this.m_resultsFrame.toFront();
/* 164:    */     }
/* 165:    */   }
/* 166:    */   
/* 167:    */   private void setUpResultHistory()
/* 168:    */   {
/* 169:226 */     if (this.m_history == null) {
/* 170:227 */       this.m_history = new ResultHistoryPanel(null);
/* 171:    */     }
/* 172:229 */     this.m_history.setBorder(BorderFactory.createTitledBorder("Image list"));
/* 173:230 */     this.m_history.setHandleRightClicks(false);
/* 174:231 */     this.m_history.getList().addMouseListener(new ResultHistoryPanel.RMouseAdapter()
/* 175:    */     {
/* 176:    */       private static final long serialVersionUID = -4984130887963944249L;
/* 177:    */       
/* 178:    */       public void mouseClicked(MouseEvent e)
/* 179:    */       {
/* 180:238 */         int index = ImageViewer.this.m_history.getList().locationToIndex(e.getPoint());
/* 181:239 */         if (index != -1)
/* 182:    */         {
/* 183:240 */           String name = ImageViewer.this.m_history.getNameAtIndex(index);
/* 184:    */           
/* 185:242 */           Object pic = ImageViewer.this.m_history.getNamedObject(name);
/* 186:243 */           if ((pic instanceof BufferedImage))
/* 187:    */           {
/* 188:244 */             ImageViewer.this.m_plotter.setImage((BufferedImage)pic);
/* 189:245 */             ImageViewer.this.m_plotter.repaint();
/* 190:    */           }
/* 191:    */         }
/* 192:    */       }
/* 193:250 */     });
/* 194:251 */     this.m_history.getList().getSelectionModel().addListSelectionListener(new ListSelectionListener()
/* 195:    */     {
/* 196:    */       public void valueChanged(ListSelectionEvent e)
/* 197:    */       {
/* 198:255 */         if (!e.getValueIsAdjusting())
/* 199:    */         {
/* 200:256 */           ListSelectionModel lm = (ListSelectionModel)e.getSource();
/* 201:257 */           for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
/* 202:258 */             if (lm.isSelectedIndex(i))
/* 203:    */             {
/* 204:260 */               if (i == -1) {
/* 205:    */                 break;
/* 206:    */               }
/* 207:261 */               String name = ImageViewer.this.m_history.getNameAtIndex(i);
/* 208:262 */               Object pic = ImageViewer.this.m_history.getNamedObject(name);
/* 209:263 */               if ((pic != null) && ((pic instanceof BufferedImage)))
/* 210:    */               {
/* 211:264 */                 ImageViewer.this.m_plotter.setImage((BufferedImage)pic);
/* 212:265 */                 ImageViewer.this.m_plotter.repaint();
/* 213:    */               }
/* 214:267 */               break;
/* 215:    */             }
/* 216:    */           }
/* 217:    */         }
/* 218:    */       }
/* 219:    */     });
/* 220:    */   }
/* 221:    */   
/* 222:    */   private static class MainPanel
/* 223:    */     extends JPanel
/* 224:    */   {
/* 225:    */     private static final long serialVersionUID = 5648976848887609072L;
/* 226:    */     
/* 227:    */     private static Image loadImage(String path)
/* 228:    */     {
/* 229:284 */       Image pic = null;
/* 230:285 */       URL imageURL = ImageViewer.class.getClassLoader().getResource(path);
/* 231:289 */       if (imageURL != null) {
/* 232:291 */         pic = Toolkit.getDefaultToolkit().getImage(imageURL);
/* 233:    */       }
/* 234:293 */       return pic;
/* 235:    */     }
/* 236:    */     
/* 237:    */     public MainPanel(ResultHistoryPanel p, final ImageViewer.ImageDisplayer id)
/* 238:    */     {
/* 239:303 */       setLayout(new BorderLayout());
/* 240:    */       
/* 241:305 */       JPanel topP = new JPanel();
/* 242:306 */       topP.setLayout(new BorderLayout());
/* 243:    */       
/* 244:308 */       JPanel holder = new JPanel();
/* 245:309 */       holder.setLayout(new BorderLayout());
/* 246:310 */       holder.setBorder(BorderFactory.createTitledBorder("Image"));
/* 247:311 */       JToolBar tools = new JToolBar();
/* 248:312 */       tools.setOrientation(0);
/* 249:313 */       JButton zoomInB = new JButton(new ImageIcon(loadImage("weka/gui/beans/icons/zoom_in.png")));
/* 250:    */       
/* 251:    */ 
/* 252:    */ 
/* 253:317 */       zoomInB.addActionListener(new ActionListener()
/* 254:    */       {
/* 255:    */         public void actionPerformed(ActionEvent e)
/* 256:    */         {
/* 257:320 */           int z = id.getZoom();
/* 258:321 */           z += 25;
/* 259:322 */           if (z >= 200) {
/* 260:323 */             z = 200;
/* 261:    */           }
/* 262:326 */           id.setZoom(z);
/* 263:327 */           id.repaint();
/* 264:    */         }
/* 265:330 */       });
/* 266:331 */       JButton zoomOutB = new JButton(new ImageIcon(loadImage("weka/gui/beans/icons/zoom_out.png")));
/* 267:    */       
/* 268:    */ 
/* 269:334 */       zoomOutB.addActionListener(new ActionListener()
/* 270:    */       {
/* 271:    */         public void actionPerformed(ActionEvent e)
/* 272:    */         {
/* 273:337 */           int z = id.getZoom();
/* 274:338 */           z -= 25;
/* 275:339 */           if (z <= 50) {
/* 276:340 */             z = 50;
/* 277:    */           }
/* 278:343 */           id.setZoom(z);
/* 279:344 */           id.repaint();
/* 280:    */         }
/* 281:347 */       });
/* 282:348 */       tools.add(zoomInB);
/* 283:349 */       tools.add(zoomOutB);
/* 284:350 */       holder.add(tools, "North");
/* 285:    */       
/* 286:352 */       JScrollPane js = new JScrollPane(id);
/* 287:353 */       holder.add(js, "Center");
/* 288:354 */       topP.add(holder, "Center");
/* 289:355 */       topP.add(p, "West");
/* 290:    */       
/* 291:357 */       add(topP, "Center");
/* 292:    */     }
/* 293:    */   }
/* 294:    */   
/* 295:    */   private static class ImageDisplayer
/* 296:    */     extends JPanel
/* 297:    */   {
/* 298:    */     private static final long serialVersionUID = 4161957589912537357L;
/* 299:    */     private BufferedImage m_image;
/* 300:375 */     private int m_imageZoom = 100;
/* 301:    */     
/* 302:    */     public void setImage(BufferedImage image)
/* 303:    */     {
/* 304:383 */       this.m_image = image;
/* 305:    */     }
/* 306:    */     
/* 307:    */     public void setZoom(int zoom)
/* 308:    */     {
/* 309:387 */       this.m_imageZoom = zoom;
/* 310:    */     }
/* 311:    */     
/* 312:    */     public int getZoom()
/* 313:    */     {
/* 314:391 */       return this.m_imageZoom;
/* 315:    */     }
/* 316:    */     
/* 317:    */     public void paintComponent(Graphics g)
/* 318:    */     {
/* 319:401 */       super.paintComponent(g);
/* 320:403 */       if (this.m_image != null)
/* 321:    */       {
/* 322:404 */         double lz = this.m_imageZoom / 100.0D;
/* 323:405 */         ((Graphics2D)g).scale(lz, lz);
/* 324:406 */         int plotWidth = this.m_image.getWidth();
/* 325:407 */         int plotHeight = this.m_image.getHeight();
/* 326:    */         
/* 327:409 */         int ourWidth = getWidth();
/* 328:410 */         int ourHeight = getHeight();
/* 329:    */         
/* 330:    */ 
/* 331:413 */         int x = 0;int y = 0;
/* 332:414 */         if (plotWidth < ourWidth) {
/* 333:415 */           x = (ourWidth - plotWidth) / 2;
/* 334:    */         }
/* 335:417 */         if (plotHeight < ourHeight) {
/* 336:418 */           y = (ourHeight - plotHeight) / 2;
/* 337:    */         }
/* 338:421 */         g.drawImage(this.m_image, x, y, this);
/* 339:422 */         setPreferredSize(new Dimension(plotWidth, plotHeight));
/* 340:423 */         revalidate();
/* 341:    */       }
/* 342:    */     }
/* 343:    */   }
/* 344:    */   
/* 345:    */   public Enumeration<String> enumerateRequests()
/* 346:    */   {
/* 347:430 */     Vector<String> newVector = new Vector(0);
/* 348:431 */     newVector.addElement("Show results");
/* 349:    */     
/* 350:433 */     return newVector.elements();
/* 351:    */   }
/* 352:    */   
/* 353:    */   public void performRequest(String request)
/* 354:    */   {
/* 355:438 */     if (request.compareTo("Show results") == 0) {
/* 356:439 */       showResults();
/* 357:    */     } else {
/* 358:441 */       throw new IllegalArgumentException(request + " not supported (ImageViewer)");
/* 359:    */     }
/* 360:    */   }
/* 361:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ImageViewer
 * JD-Core Version:    0.7.0.1
 */