/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.Font;
/*   7:    */ import java.awt.GraphicsEnvironment;
/*   8:    */ import java.awt.GridLayout;
/*   9:    */ import java.awt.Image;
/*  10:    */ import java.awt.Toolkit;
/*  11:    */ import java.awt.event.ActionEvent;
/*  12:    */ import java.awt.event.ActionListener;
/*  13:    */ import java.awt.event.WindowAdapter;
/*  14:    */ import java.awt.event.WindowEvent;
/*  15:    */ import java.awt.image.BufferedImage;
/*  16:    */ import java.beans.beancontext.BeanContextChildSupport;
/*  17:    */ import java.io.BufferedReader;
/*  18:    */ import java.io.FileReader;
/*  19:    */ import java.io.PrintStream;
/*  20:    */ import java.io.Reader;
/*  21:    */ import java.net.URL;
/*  22:    */ import java.util.ArrayList;
/*  23:    */ import java.util.Enumeration;
/*  24:    */ import java.util.List;
/*  25:    */ import java.util.Vector;
/*  26:    */ import javax.swing.BorderFactory;
/*  27:    */ import javax.swing.DefaultComboBoxModel;
/*  28:    */ import javax.swing.Icon;
/*  29:    */ import javax.swing.ImageIcon;
/*  30:    */ import javax.swing.JComboBox;
/*  31:    */ import javax.swing.JFrame;
/*  32:    */ import javax.swing.JLabel;
/*  33:    */ import javax.swing.JPanel;
/*  34:    */ import javax.swing.JScrollPane;
/*  35:    */ import weka.core.Attribute;
/*  36:    */ import weka.core.Environment;
/*  37:    */ import weka.core.Instance;
/*  38:    */ import weka.core.Instances;
/*  39:    */ import weka.gui.AttributeVisualizationPanel;
/*  40:    */ 
/*  41:    */ public class AttributeSummarizer
/*  42:    */   extends DataVisualizer
/*  43:    */   implements KnowledgeFlowApp.KFPerspective
/*  44:    */ {
/*  45:    */   private static final long serialVersionUID = -294354961169372758L;
/*  46: 67 */   protected int m_gridWidth = 4;
/*  47: 72 */   protected int m_maxPlots = 100;
/*  48: 77 */   protected int m_coloringIndex = -1;
/*  49: 79 */   protected boolean m_showClassCombo = false;
/*  50: 80 */   protected boolean m_runningAsPerspective = false;
/*  51: 81 */   protected boolean m_activePerspective = false;
/*  52:    */   protected transient List<AttributeVisualizationPanel> m_plots;
/*  53:    */   
/*  54:    */   public AttributeSummarizer()
/*  55:    */   {
/*  56: 89 */     useDefaultVisual();
/*  57: 90 */     this.m_visual.setText("AttributeSummarizer");
/*  58: 94 */     if (!GraphicsEnvironment.isHeadless()) {
/*  59: 95 */       appearanceFinal();
/*  60:    */     }
/*  61:    */   }
/*  62:    */   
/*  63:    */   public String globalInfo()
/*  64:    */   {
/*  65:106 */     return "Plot summary bar charts for incoming data/training/test sets.";
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void setColoringIndex(int ci)
/*  69:    */   {
/*  70:115 */     this.m_coloringIndex = ci;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public int getColoringIndex()
/*  74:    */   {
/*  75:124 */     return this.m_coloringIndex;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void setGridWidth(int gw)
/*  79:    */   {
/*  80:133 */     if (gw > 0)
/*  81:    */     {
/*  82:134 */       this.m_bcSupport.firePropertyChange("gridWidth", new Integer(this.m_gridWidth), new Integer(gw));
/*  83:    */       
/*  84:136 */       this.m_gridWidth = gw;
/*  85:    */     }
/*  86:    */   }
/*  87:    */   
/*  88:    */   public int getGridWidth()
/*  89:    */   {
/*  90:146 */     return this.m_gridWidth;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public void setMaxPlots(int mp)
/*  94:    */   {
/*  95:155 */     if (mp > 0)
/*  96:    */     {
/*  97:156 */       this.m_bcSupport.firePropertyChange("maxPlots", new Integer(this.m_maxPlots), new Integer(mp));
/*  98:    */       
/*  99:158 */       this.m_maxPlots = mp;
/* 100:    */     }
/* 101:    */   }
/* 102:    */   
/* 103:    */   public int getMaxPlots()
/* 104:    */   {
/* 105:168 */     return this.m_maxPlots;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public void setDesign(boolean design)
/* 109:    */   {
/* 110:177 */     this.m_design = true;
/* 111:178 */     appearanceDesign();
/* 112:    */   }
/* 113:    */   
/* 114:    */   protected void appearanceDesign()
/* 115:    */   {
/* 116:183 */     removeAll();
/* 117:184 */     setLayout(new BorderLayout());
/* 118:185 */     add(this.m_visual, "Center");
/* 119:    */   }
/* 120:    */   
/* 121:    */   protected void appearanceFinal()
/* 122:    */   {
/* 123:190 */     removeAll();
/* 124:191 */     setLayout(new BorderLayout());
/* 125:    */   }
/* 126:    */   
/* 127:    */   protected void setUpFinal()
/* 128:    */   {
/* 129:196 */     removeAll();
/* 130:198 */     if (this.m_visualizeDataSet == null) {
/* 131:199 */       return;
/* 132:    */     }
/* 133:202 */     if ((!this.m_runningAsPerspective) || (this.m_activePerspective))
/* 134:    */     {
/* 135:203 */       JScrollPane hp = makePanel();
/* 136:204 */       add(hp, "Center");
/* 137:206 */       if (this.m_showClassCombo)
/* 138:    */       {
/* 139:207 */         Vector<String> atts = new Vector();
/* 140:208 */         for (int i = 0; i < this.m_visualizeDataSet.numAttributes(); i++) {
/* 141:209 */           atts.add("(" + Attribute.typeToStringShort(this.m_visualizeDataSet.attribute(i)) + ") " + this.m_visualizeDataSet.attribute(i).name());
/* 142:    */         }
/* 143:214 */         final JComboBox classCombo = new JComboBox();
/* 144:215 */         classCombo.setModel(new DefaultComboBoxModel(atts));
/* 145:217 */         if (atts.size() > 0)
/* 146:    */         {
/* 147:218 */           if (this.m_visualizeDataSet.classIndex() < 0) {
/* 148:219 */             classCombo.setSelectedIndex(atts.size() - 1);
/* 149:    */           } else {
/* 150:221 */             classCombo.setSelectedIndex(this.m_visualizeDataSet.classIndex());
/* 151:    */           }
/* 152:223 */           classCombo.setEnabled(true);
/* 153:224 */           for (int i = 0; i < this.m_plots.size(); i++) {
/* 154:225 */             ((AttributeVisualizationPanel)this.m_plots.get(i)).setColoringIndex(classCombo.getSelectedIndex());
/* 155:    */           }
/* 156:    */         }
/* 157:229 */         JPanel comboHolder = new JPanel();
/* 158:230 */         comboHolder.setLayout(new BorderLayout());
/* 159:231 */         JPanel tempHolder = new JPanel();
/* 160:232 */         tempHolder.setLayout(new BorderLayout());
/* 161:233 */         tempHolder.add(new JLabel("Class: "), "West");
/* 162:234 */         tempHolder.add(classCombo, "East");
/* 163:235 */         comboHolder.add(tempHolder, "West");
/* 164:236 */         add(comboHolder, "North");
/* 165:    */         
/* 166:238 */         classCombo.addActionListener(new ActionListener()
/* 167:    */         {
/* 168:    */           public void actionPerformed(ActionEvent e)
/* 169:    */           {
/* 170:241 */             int selected = classCombo.getSelectedIndex();
/* 171:242 */             if (selected >= 0) {
/* 172:243 */               for (int i = 0; i < AttributeSummarizer.this.m_plots.size(); i++) {
/* 173:244 */                 ((AttributeVisualizationPanel)AttributeSummarizer.this.m_plots.get(i)).setColoringIndex(selected);
/* 174:    */               }
/* 175:    */             }
/* 176:    */           }
/* 177:    */         });
/* 178:    */       }
/* 179:    */     }
/* 180:    */   }
/* 181:    */   
/* 182:    */   public void useDefaultVisual()
/* 183:    */   {
/* 184:259 */     this.m_visual.loadIcons("weka/gui/beans/icons/AttributeSummarizer.gif", "weka/gui/beans/icons/AttributeSummarizer_animated.gif");
/* 185:    */   }
/* 186:    */   
/* 187:    */   public Enumeration<String> enumerateRequests()
/* 188:    */   {
/* 189:270 */     Vector<String> newVector = new Vector(0);
/* 190:271 */     if (this.m_visualizeDataSet != null) {
/* 191:272 */       newVector.addElement("Show summaries");
/* 192:    */     }
/* 193:274 */     return newVector.elements();
/* 194:    */   }
/* 195:    */   
/* 196:    */   private JScrollPane makePanel()
/* 197:    */   {
/* 198:278 */     String fontFamily = getFont().getFamily();
/* 199:279 */     Font newFont = new Font(fontFamily, 0, 10);
/* 200:280 */     JPanel hp = new JPanel();
/* 201:281 */     hp.setFont(newFont);
/* 202:282 */     int numPlots = Math.min(this.m_visualizeDataSet.numAttributes(), this.m_maxPlots);
/* 203:283 */     int gridHeight = numPlots / this.m_gridWidth;
/* 204:285 */     if (numPlots % this.m_gridWidth != 0) {
/* 205:286 */       gridHeight++;
/* 206:    */     }
/* 207:288 */     hp.setLayout(new GridLayout(gridHeight, 4));
/* 208:    */     
/* 209:290 */     this.m_plots = new ArrayList();
/* 210:292 */     for (int i = 0; i < numPlots; i++)
/* 211:    */     {
/* 212:293 */       JPanel temp = new JPanel();
/* 213:294 */       temp.setLayout(new BorderLayout());
/* 214:295 */       temp.setBorder(BorderFactory.createTitledBorder(this.m_visualizeDataSet.attribute(i).name()));
/* 215:    */       
/* 216:    */ 
/* 217:298 */       AttributeVisualizationPanel ap = new AttributeVisualizationPanel();
/* 218:299 */       this.m_plots.add(ap);
/* 219:300 */       ap.setInstances(this.m_visualizeDataSet);
/* 220:301 */       if ((this.m_coloringIndex < 0) && (this.m_visualizeDataSet.classIndex() >= 0)) {
/* 221:302 */         ap.setColoringIndex(this.m_visualizeDataSet.classIndex());
/* 222:    */       } else {
/* 223:304 */         ap.setColoringIndex(this.m_coloringIndex);
/* 224:    */       }
/* 225:306 */       temp.add(ap, "Center");
/* 226:307 */       ap.setAttribute(i);
/* 227:308 */       hp.add(temp);
/* 228:    */     }
/* 229:311 */     Dimension d = new Dimension(830, gridHeight * 100);
/* 230:312 */     hp.setMinimumSize(d);
/* 231:313 */     hp.setMaximumSize(d);
/* 232:314 */     hp.setPreferredSize(d);
/* 233:    */     
/* 234:316 */     JScrollPane scroller = new JScrollPane(hp);
/* 235:    */     
/* 236:318 */     return scroller;
/* 237:    */   }
/* 238:    */   
/* 239:    */   public void setInstances(Instances inst)
/* 240:    */     throws Exception
/* 241:    */   {
/* 242:340 */     if (this.m_design) {
/* 243:341 */       throw new Exception("This method is not to be used during design time. It is meant to be used if this bean is being used programatically as as stand alone component.");
/* 244:    */     }
/* 245:346 */     this.m_visualizeDataSet = inst;
/* 246:347 */     setUpFinal();
/* 247:    */   }
/* 248:    */   
/* 249:    */   public boolean acceptsInstances()
/* 250:    */   {
/* 251:357 */     return true;
/* 252:    */   }
/* 253:    */   
/* 254:    */   public String getPerspectiveTitle()
/* 255:    */   {
/* 256:367 */     return "Attribute summary";
/* 257:    */   }
/* 258:    */   
/* 259:    */   public String getPerspectiveTipText()
/* 260:    */   {
/* 261:377 */     return "Matrix of attribute summary histograms";
/* 262:    */   }
/* 263:    */   
/* 264:    */   public Icon getPerspectiveIcon()
/* 265:    */   {
/* 266:388 */     Image pic = null;
/* 267:389 */     URL imageURL = getClass().getClassLoader().getResource("weka/gui/beans/icons/chart_bar.png");
/* 268:392 */     if (imageURL != null) {
/* 269:394 */       pic = Toolkit.getDefaultToolkit().getImage(imageURL);
/* 270:    */     }
/* 271:396 */     return new ImageIcon(pic);
/* 272:    */   }
/* 273:    */   
/* 274:    */   public void setActive(boolean active)
/* 275:    */   {
/* 276:407 */     this.m_activePerspective = active;
/* 277:408 */     this.m_plots = null;
/* 278:409 */     setUpFinal();
/* 279:    */   }
/* 280:    */   
/* 281:    */   public void setLoaded(boolean loaded) {}
/* 282:    */   
/* 283:    */   public void setMainKFPerspective(KnowledgeFlowApp.MainKFPerspective main)
/* 284:    */   {
/* 285:434 */     this.m_showClassCombo = true;
/* 286:435 */     this.m_runningAsPerspective = true;
/* 287:    */   }
/* 288:    */   
/* 289:    */   public void performRequest(String request)
/* 290:    */   {
/* 291:446 */     if (!this.m_design)
/* 292:    */     {
/* 293:447 */       setUpFinal();
/* 294:448 */       return;
/* 295:    */     }
/* 296:450 */     if (request.compareTo("Show summaries") == 0) {
/* 297:    */       try
/* 298:    */       {
/* 299:453 */         if (!this.m_framePoppedUp)
/* 300:    */         {
/* 301:454 */           this.m_framePoppedUp = true;
/* 302:455 */           JScrollPane holderP = makePanel();
/* 303:    */           
/* 304:457 */           final JFrame jf = new JFrame("Visualize");
/* 305:458 */           jf.setSize(800, 600);
/* 306:459 */           jf.getContentPane().setLayout(new BorderLayout());
/* 307:460 */           jf.getContentPane().add(holderP, "Center");
/* 308:461 */           jf.addWindowListener(new WindowAdapter()
/* 309:    */           {
/* 310:    */             public void windowClosing(WindowEvent e)
/* 311:    */             {
/* 312:464 */               jf.dispose();
/* 313:465 */               AttributeSummarizer.this.m_framePoppedUp = false;
/* 314:    */             }
/* 315:467 */           });
/* 316:468 */           jf.setVisible(true);
/* 317:469 */           this.m_popupFrame = jf;
/* 318:    */         }
/* 319:    */         else
/* 320:    */         {
/* 321:471 */           this.m_popupFrame.toFront();
/* 322:    */         }
/* 323:    */       }
/* 324:    */       catch (Exception ex)
/* 325:    */       {
/* 326:474 */         ex.printStackTrace();
/* 327:475 */         this.m_framePoppedUp = false;
/* 328:    */       }
/* 329:    */     } else {
/* 330:478 */       throw new IllegalArgumentException(request + " not supported (AttributeSummarizer)");
/* 331:    */     }
/* 332:    */   }
/* 333:    */   
/* 334:    */   protected void renderOffscreenImage(DataSetEvent e)
/* 335:    */   {
/* 336:485 */     if (this.m_env == null) {
/* 337:486 */       this.m_env = Environment.getSystemWide();
/* 338:    */     }
/* 339:489 */     if ((this.m_imageListeners.size() > 0) && (!this.m_processingHeadlessEvents))
/* 340:    */     {
/* 341:491 */       setupOffscreenRenderer();
/* 342:    */       
/* 343:493 */       this.m_offscreenPlotData = new ArrayList();
/* 344:494 */       Instances predictedI = e.getDataSet();
/* 345:495 */       if ((predictedI.classIndex() >= 0) && (predictedI.classAttribute().isNominal()))
/* 346:    */       {
/* 347:498 */         Instances[] classes = new Instances[predictedI.numClasses()];
/* 348:499 */         for (int i = 0; i < predictedI.numClasses(); i++)
/* 349:    */         {
/* 350:500 */           classes[i] = new Instances(predictedI, 0);
/* 351:501 */           classes[i].setRelationName(predictedI.classAttribute().value(i));
/* 352:    */         }
/* 353:503 */         for (int i = 0; i < predictedI.numInstances(); i++)
/* 354:    */         {
/* 355:504 */           Instance current = predictedI.instance(i);
/* 356:505 */           classes[((int)current.classValue())].add((Instance)current.copy());
/* 357:    */         }
/* 358:507 */         for (Instances classe : classes) {
/* 359:508 */           this.m_offscreenPlotData.add(classe);
/* 360:    */         }
/* 361:    */       }
/* 362:    */       else
/* 363:    */       {
/* 364:511 */         this.m_offscreenPlotData.add(new Instances(predictedI));
/* 365:    */       }
/* 366:514 */       List<String> options = new ArrayList();
/* 367:515 */       String additional = this.m_additionalOptions;
/* 368:516 */       if ((this.m_additionalOptions != null) && (this.m_additionalOptions.length() > 0)) {
/* 369:    */         try
/* 370:    */         {
/* 371:518 */           additional = this.m_env.substitute(additional);
/* 372:    */         }
/* 373:    */         catch (Exception ex) {}
/* 374:    */       }
/* 375:523 */       if ((additional != null) && (additional.indexOf("-color") < 0))
/* 376:    */       {
/* 377:525 */         if (additional.length() > 0) {
/* 378:526 */           additional = additional + ",";
/* 379:    */         }
/* 380:528 */         if (predictedI.classIndex() >= 0) {
/* 381:529 */           additional = additional + "-color=" + predictedI.classAttribute().name();
/* 382:    */         } else {
/* 383:531 */           additional = additional + "-color=/last";
/* 384:    */         }
/* 385:    */       }
/* 386:535 */       String[] optionsParts = additional.split(",");
/* 387:536 */       for (String p : optionsParts) {
/* 388:537 */         options.add(p.trim());
/* 389:    */       }
/* 390:541 */       String xAxis = this.m_xAxis;
/* 391:    */       try
/* 392:    */       {
/* 393:543 */         xAxis = this.m_env.substitute(xAxis);
/* 394:    */       }
/* 395:    */       catch (Exception ex) {}
/* 396:547 */       String width = this.m_width;
/* 397:548 */       String height = this.m_height;
/* 398:549 */       int defWidth = 500;
/* 399:550 */       int defHeight = 400;
/* 400:    */       try
/* 401:    */       {
/* 402:552 */         width = this.m_env.substitute(width);
/* 403:553 */         height = this.m_env.substitute(height);
/* 404:    */         
/* 405:555 */         defWidth = Integer.parseInt(width);
/* 406:556 */         defHeight = Integer.parseInt(height);
/* 407:    */       }
/* 408:    */       catch (Exception ex) {}
/* 409:    */       try
/* 410:    */       {
/* 411:561 */         BufferedImage osi = this.m_offscreenRenderer.renderHistogram(defWidth, defHeight, this.m_offscreenPlotData, xAxis, options);
/* 412:    */         
/* 413:    */ 
/* 414:564 */         ImageEvent ie = new ImageEvent(this, osi);
/* 415:565 */         notifyImageListeners(ie);
/* 416:    */       }
/* 417:    */       catch (Exception e1)
/* 418:    */       {
/* 419:567 */         e1.printStackTrace();
/* 420:    */       }
/* 421:    */     }
/* 422:    */   }
/* 423:    */   
/* 424:    */   public static void main(String[] args)
/* 425:    */   {
/* 426:    */     try
/* 427:    */     {
/* 428:575 */       if (args.length != 1)
/* 429:    */       {
/* 430:576 */         System.err.println("Usage: AttributeSummarizer <dataset>");
/* 431:577 */         System.exit(1);
/* 432:    */       }
/* 433:579 */       Reader r = new BufferedReader(new FileReader(args[0]));
/* 434:    */       
/* 435:581 */       Instances inst = new Instances(r);
/* 436:582 */       JFrame jf = new JFrame();
/* 437:583 */       jf.getContentPane().setLayout(new BorderLayout());
/* 438:584 */       AttributeSummarizer as = new AttributeSummarizer();
/* 439:585 */       as.setInstances(inst);
/* 440:    */       
/* 441:587 */       jf.getContentPane().add(as, "Center");
/* 442:588 */       jf.addWindowListener(new WindowAdapter()
/* 443:    */       {
/* 444:    */         public void windowClosing(WindowEvent e)
/* 445:    */         {
/* 446:591 */           this.val$jf.dispose();
/* 447:592 */           System.exit(0);
/* 448:    */         }
/* 449:594 */       });
/* 450:595 */       jf.setSize(830, 600);
/* 451:596 */       jf.setVisible(true);
/* 452:    */     }
/* 453:    */     catch (Exception ex)
/* 454:    */     {
/* 455:598 */       ex.printStackTrace();
/* 456:599 */       System.err.println(ex.getMessage());
/* 457:    */     }
/* 458:    */   }
/* 459:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.AttributeSummarizer
 * JD-Core Version:    0.7.0.1
 */