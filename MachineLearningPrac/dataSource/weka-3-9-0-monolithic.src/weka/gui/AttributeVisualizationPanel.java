/*    1:     */ package weka.gui;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Color;
/*    5:     */ import java.awt.Container;
/*    6:     */ import java.awt.FlowLayout;
/*    7:     */ import java.awt.Font;
/*    8:     */ import java.awt.FontMetrics;
/*    9:     */ import java.awt.Graphics;
/*   10:     */ import java.awt.Point;
/*   11:     */ import java.awt.event.ComponentAdapter;
/*   12:     */ import java.awt.event.ComponentEvent;
/*   13:     */ import java.awt.event.ItemEvent;
/*   14:     */ import java.awt.event.ItemListener;
/*   15:     */ import java.awt.event.MouseEvent;
/*   16:     */ import java.io.FileReader;
/*   17:     */ import java.io.PrintStream;
/*   18:     */ import java.util.ArrayList;
/*   19:     */ import javax.swing.JComboBox;
/*   20:     */ import javax.swing.JFrame;
/*   21:     */ import weka.core.Attribute;
/*   22:     */ import weka.core.AttributeStats;
/*   23:     */ import weka.core.Instance;
/*   24:     */ import weka.core.Instances;
/*   25:     */ import weka.core.SparseInstance;
/*   26:     */ import weka.core.Utils;
/*   27:     */ import weka.experiment.Stats;
/*   28:     */ import weka.gui.visualize.PrintableComponent;
/*   29:     */ import weka.gui.visualize.PrintablePanel;
/*   30:     */ 
/*   31:     */ public class AttributeVisualizationPanel
/*   32:     */   extends PrintablePanel
/*   33:     */ {
/*   34:     */   private static final long serialVersionUID = -8650490488825371193L;
/*   35:     */   protected Instances m_data;
/*   36:     */   protected AttributeStats m_as;
/*   37:     */   protected AttributeStats[] m_asCache;
/*   38:     */   protected int m_attribIndex;
/*   39:     */   protected double m_maxValue;
/*   40:     */   protected double[] m_histBarCounts;
/*   41:     */   SparseInstance[] m_histBarClassCounts;
/*   42:     */   protected double m_barRange;
/*   43:     */   protected int m_classIndex;
/*   44:     */   private Thread m_hc;
/*   45: 148 */   private boolean m_threadRun = false;
/*   46: 150 */   private boolean m_doneCurrentAttribute = false;
/*   47: 151 */   private boolean m_displayCurrentAttribute = false;
/*   48:     */   protected JComboBox m_colorAttrib;
/*   49:     */   private final FontMetrics m_fm;
/*   50: 176 */   private final Integer m_locker = new Integer(1);
/*   51: 184 */   private final ArrayList<Color> m_colorList = new ArrayList();
/*   52: 187 */   private static final Color[] m_defaultColors = { Color.blue, Color.red, Color.cyan, new Color(75, 123, 130), Color.pink, Color.green, Color.orange, new Color(255, 0, 255), new Color(255, 0, 0), new Color(0, 255, 0) };
/*   53:     */   
/*   54:     */   public AttributeVisualizationPanel()
/*   55:     */   {
/*   56: 196 */     this(false);
/*   57:     */   }
/*   58:     */   
/*   59:     */   public AttributeVisualizationPanel(boolean showColouringOption)
/*   60:     */   {
/*   61: 208 */     setFont(new Font("Default", 0, 9));
/*   62: 209 */     this.m_fm = getFontMetrics(getFont());
/*   63: 210 */     setToolTipText("");
/*   64: 211 */     FlowLayout fl = new FlowLayout(0);
/*   65: 212 */     setLayout(fl);
/*   66: 213 */     addComponentListener(new ComponentAdapter()
/*   67:     */     {
/*   68:     */       public void componentResized(ComponentEvent ce)
/*   69:     */       {
/*   70: 216 */         if (AttributeVisualizationPanel.this.m_data != null) {}
/*   71:     */       }
/*   72: 221 */     });
/*   73: 222 */     this.m_colorAttrib = new JComboBox();
/*   74: 223 */     this.m_colorAttrib.addItemListener(new ItemListener()
/*   75:     */     {
/*   76:     */       public void itemStateChanged(ItemEvent ie)
/*   77:     */       {
/*   78: 226 */         if (ie.getStateChange() == 1)
/*   79:     */         {
/*   80: 227 */           AttributeVisualizationPanel.this.m_classIndex = (AttributeVisualizationPanel.this.m_colorAttrib.getSelectedIndex() - 1);
/*   81: 228 */           if (AttributeVisualizationPanel.this.m_as != null) {
/*   82: 229 */             AttributeVisualizationPanel.this.setAttribute(AttributeVisualizationPanel.this.m_attribIndex);
/*   83:     */           }
/*   84:     */         }
/*   85:     */       }
/*   86:     */     });
/*   87: 235 */     if (showColouringOption)
/*   88:     */     {
/*   89: 237 */       add(this.m_colorAttrib);
/*   90: 238 */       validate();
/*   91:     */     }
/*   92:     */   }
/*   93:     */   
/*   94:     */   public void setInstances(Instances newins)
/*   95:     */   {
/*   96: 249 */     this.m_attribIndex = 0;
/*   97: 250 */     this.m_as = null;
/*   98: 251 */     this.m_data = new Instances(newins);
/*   99: 252 */     if (this.m_colorAttrib != null)
/*  100:     */     {
/*  101: 253 */       this.m_colorAttrib.removeAllItems();
/*  102: 254 */       this.m_colorAttrib.addItem("No class");
/*  103: 255 */       for (int i = 0; i < this.m_data.numAttributes(); i++)
/*  104:     */       {
/*  105: 256 */         String type = "(" + Attribute.typeToStringShort(this.m_data.attribute(i)) + ")";
/*  106:     */         
/*  107: 258 */         this.m_colorAttrib.addItem(new String("Class: " + this.m_data.attribute(i).name() + " " + type));
/*  108:     */       }
/*  109: 261 */       if (this.m_data.classIndex() >= 0) {
/*  110: 262 */         this.m_colorAttrib.setSelectedIndex(this.m_data.classIndex() + 1);
/*  111:     */       } else {
/*  112: 264 */         this.m_colorAttrib.setSelectedIndex(this.m_data.numAttributes());
/*  113:     */       }
/*  114:     */     }
/*  115: 270 */     if (this.m_data.classIndex() >= 0) {
/*  116: 271 */       this.m_classIndex = this.m_data.classIndex();
/*  117:     */     } else {
/*  118: 273 */       this.m_classIndex = (this.m_data.numAttributes() - 1);
/*  119:     */     }
/*  120: 276 */     this.m_asCache = new AttributeStats[this.m_data.numAttributes()];
/*  121:     */   }
/*  122:     */   
/*  123:     */   public JComboBox getColorBox()
/*  124:     */   {
/*  125: 284 */     return this.m_colorAttrib;
/*  126:     */   }
/*  127:     */   
/*  128:     */   public int getColoringIndex()
/*  129:     */   {
/*  130: 293 */     return this.m_classIndex;
/*  131:     */   }
/*  132:     */   
/*  133:     */   public void setColoringIndex(int ci)
/*  134:     */   {
/*  135: 302 */     this.m_classIndex = ci;
/*  136: 303 */     if (this.m_colorAttrib != null) {
/*  137: 304 */       this.m_colorAttrib.setSelectedIndex(ci + 1);
/*  138:     */     } else {
/*  139: 306 */       setAttribute(this.m_attribIndex);
/*  140:     */     }
/*  141:     */   }
/*  142:     */   
/*  143:     */   public void setAttribute(int index)
/*  144:     */   {
/*  145: 317 */     synchronized (this.m_locker)
/*  146:     */     {
/*  147: 319 */       this.m_threadRun = false;
/*  148: 320 */       this.m_doneCurrentAttribute = false;
/*  149: 321 */       this.m_displayCurrentAttribute = true;
/*  150:     */       
/*  151: 323 */       this.m_attribIndex = index;
/*  152: 324 */       if (this.m_asCache[index] != null)
/*  153:     */       {
/*  154: 325 */         this.m_as = this.m_asCache[index];
/*  155:     */       }
/*  156:     */       else
/*  157:     */       {
/*  158: 327 */         this.m_asCache[index] = this.m_data.attributeStats(index);
/*  159: 328 */         this.m_as = this.m_asCache[index];
/*  160:     */       }
/*  161:     */     }
/*  162: 334 */     repaint();
/*  163:     */   }
/*  164:     */   
/*  165:     */   public void calcGraph(int panelWidth, int panelHeight)
/*  166:     */   {
/*  167: 344 */     synchronized (this.m_locker)
/*  168:     */     {
/*  169: 345 */       this.m_threadRun = true;
/*  170: 346 */       if (this.m_as.nominalWeights != null)
/*  171:     */       {
/*  172: 347 */         this.m_hc = new BarCalc(panelWidth, panelHeight);
/*  173: 348 */         this.m_hc.setPriority(1);
/*  174: 349 */         this.m_hc.start();
/*  175:     */       }
/*  176: 350 */       else if (this.m_as.numericStats != null)
/*  177:     */       {
/*  178: 351 */         this.m_hc = new HistCalc(null);
/*  179: 352 */         this.m_hc.setPriority(1);
/*  180: 353 */         this.m_hc.start();
/*  181:     */       }
/*  182:     */       else
/*  183:     */       {
/*  184: 355 */         this.m_histBarCounts = null;
/*  185: 356 */         this.m_histBarClassCounts = null;
/*  186: 357 */         this.m_doneCurrentAttribute = true;
/*  187: 358 */         this.m_threadRun = false;
/*  188: 359 */         repaint();
/*  189:     */       }
/*  190:     */     }
/*  191:     */   }
/*  192:     */   
/*  193:     */   private class BarCalc
/*  194:     */     extends Thread
/*  195:     */   {
/*  196:     */     private final int m_panelWidth;
/*  197:     */     
/*  198:     */     public BarCalc(int panelWidth, int panelHeight)
/*  199:     */     {
/*  200: 375 */       this.m_panelWidth = panelWidth;
/*  201:     */     }
/*  202:     */     
/*  203:     */     public void run()
/*  204:     */     {
/*  205: 380 */       synchronized (AttributeVisualizationPanel.this.m_locker)
/*  206:     */       {
/*  207: 383 */         if (AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_attribIndex).numValues() > this.m_panelWidth)
/*  208:     */         {
/*  209: 384 */           AttributeVisualizationPanel.this.m_histBarClassCounts = null;
/*  210: 385 */           AttributeVisualizationPanel.this.m_threadRun = false;
/*  211: 386 */           AttributeVisualizationPanel.this.m_doneCurrentAttribute = true;
/*  212: 387 */           AttributeVisualizationPanel.this.m_displayCurrentAttribute = false;
/*  213: 388 */           AttributeVisualizationPanel.this.repaint();
/*  214: 389 */           return;
/*  215:     */         }
/*  216: 392 */         if ((AttributeVisualizationPanel.this.m_classIndex >= 0) && (AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_classIndex).isNominal()))
/*  217:     */         {
/*  218: 394 */           SparseInstance[] histClassCounts = new SparseInstance[AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_attribIndex).numValues()];
/*  219: 398 */           if (AttributeVisualizationPanel.this.m_as.nominalWeights.length > 0)
/*  220:     */           {
/*  221: 399 */             AttributeVisualizationPanel.this.m_maxValue = AttributeVisualizationPanel.this.m_as.nominalWeights[0];
/*  222: 400 */             for (int i = 0; i < AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_attribIndex).numValues(); i++) {
/*  223: 401 */               if (AttributeVisualizationPanel.this.m_as.nominalWeights[i] > AttributeVisualizationPanel.this.m_maxValue) {
/*  224: 402 */                 AttributeVisualizationPanel.this.m_maxValue = AttributeVisualizationPanel.this.m_as.nominalWeights[i];
/*  225:     */               }
/*  226:     */             }
/*  227:     */           }
/*  228:     */           else
/*  229:     */           {
/*  230: 406 */             AttributeVisualizationPanel.this.m_maxValue = 0.0D;
/*  231:     */           }
/*  232: 409 */           if (AttributeVisualizationPanel.this.m_colorList.size() == 0) {
/*  233: 410 */             AttributeVisualizationPanel.this.m_colorList.add(Color.black);
/*  234:     */           }
/*  235: 412 */           for (int i = AttributeVisualizationPanel.this.m_colorList.size(); i < AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_classIndex).numValues() + 1; i++)
/*  236:     */           {
/*  237: 414 */             Color pc = AttributeVisualizationPanel.m_defaultColors[((i - 1) % 10)];
/*  238: 415 */             int ija = (i - 1) / 10;
/*  239: 416 */             ija *= 2;
/*  240: 418 */             for (int j = 0; j < ija; j++) {
/*  241: 419 */               pc = pc.darker();
/*  242:     */             }
/*  243: 422 */             AttributeVisualizationPanel.this.m_colorList.add(pc);
/*  244:     */           }
/*  245: 426 */           AttributeVisualizationPanel.this.m_data.sort(AttributeVisualizationPanel.this.m_attribIndex);
/*  246: 427 */           double[] tempClassCounts = null;
/*  247: 428 */           int tempAttValueIndex = -1;
/*  248: 430 */           for (int k = 0; k < AttributeVisualizationPanel.this.m_data.numInstances(); k++) {
/*  249: 435 */             if (!AttributeVisualizationPanel.this.m_data.instance(k).isMissing(AttributeVisualizationPanel.this.m_attribIndex))
/*  250:     */             {
/*  251: 437 */               if (AttributeVisualizationPanel.this.m_data.instance(k).value(AttributeVisualizationPanel.this.m_attribIndex) != tempAttValueIndex)
/*  252:     */               {
/*  253: 438 */                 if (tempClassCounts != null)
/*  254:     */                 {
/*  255: 440 */                   int numNonZero = 0;
/*  256: 441 */                   for (double tempClassCount : tempClassCounts) {
/*  257: 442 */                     if (tempClassCount > 0.0D) {
/*  258: 443 */                       numNonZero++;
/*  259:     */                     }
/*  260:     */                   }
/*  261: 446 */                   double[] nonZeroVals = new double[numNonZero];
/*  262: 447 */                   int[] nonZeroIndices = new int[numNonZero];
/*  263: 448 */                   int count = 0;
/*  264: 449 */                   for (int z = 0; z < tempClassCounts.length; z++) {
/*  265: 450 */                     if (tempClassCounts[z] > 0.0D)
/*  266:     */                     {
/*  267: 451 */                       nonZeroVals[count] = tempClassCounts[z];
/*  268: 452 */                       nonZeroIndices[(count++)] = z;
/*  269:     */                     }
/*  270:     */                   }
/*  271: 455 */                   SparseInstance tempS = new SparseInstance(1.0D, nonZeroVals, nonZeroIndices, tempClassCounts.length);
/*  272:     */                   
/*  273: 457 */                   histClassCounts[tempAttValueIndex] = tempS;
/*  274:     */                 }
/*  275: 460 */                 tempClassCounts = new double[AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_classIndex).numValues() + 1];
/*  276:     */                 
/*  277: 462 */                 tempAttValueIndex = (int)AttributeVisualizationPanel.this.m_data.instance(k).value(AttributeVisualizationPanel.this.m_attribIndex);
/*  278:     */               }
/*  279: 470 */               if (AttributeVisualizationPanel.this.m_data.instance(k).isMissing(AttributeVisualizationPanel.this.m_classIndex)) {
/*  280: 475 */                 tempClassCounts[0] += AttributeVisualizationPanel.this.m_data.instance(k).weight();
/*  281:     */               } else {
/*  282: 477 */                 tempClassCounts[((int)AttributeVisualizationPanel.this.m_data.instance(k).value(AttributeVisualizationPanel.this.m_classIndex) + 1)] += AttributeVisualizationPanel.this.m_data.instance(k).weight();
/*  283:     */               }
/*  284:     */             }
/*  285:     */           }
/*  286: 490 */           if (tempClassCounts != null)
/*  287:     */           {
/*  288: 492 */             int numNonZero = 0;
/*  289: 493 */             for (double tempClassCount : tempClassCounts) {
/*  290: 494 */               if (tempClassCount > 0.0D) {
/*  291: 495 */                 numNonZero++;
/*  292:     */               }
/*  293:     */             }
/*  294: 498 */             double[] nonZeroVals = new double[numNonZero];
/*  295: 499 */             int[] nonZeroIndices = new int[numNonZero];
/*  296: 500 */             int count = 0;
/*  297: 501 */             for (int z = 0; z < tempClassCounts.length; z++) {
/*  298: 502 */               if (tempClassCounts[z] > 0.0D)
/*  299:     */               {
/*  300: 503 */                 nonZeroVals[count] = tempClassCounts[z];
/*  301: 504 */                 nonZeroIndices[(count++)] = z;
/*  302:     */               }
/*  303:     */             }
/*  304: 507 */             SparseInstance tempS = new SparseInstance(1.0D, nonZeroVals, nonZeroIndices, tempClassCounts.length);
/*  305:     */             
/*  306: 509 */             histClassCounts[tempAttValueIndex] = tempS;
/*  307:     */           }
/*  308: 521 */           AttributeVisualizationPanel.this.m_threadRun = false;
/*  309: 522 */           AttributeVisualizationPanel.this.m_doneCurrentAttribute = true;
/*  310: 523 */           AttributeVisualizationPanel.this.m_displayCurrentAttribute = true;
/*  311: 524 */           AttributeVisualizationPanel.this.m_histBarClassCounts = histClassCounts;
/*  312:     */           
/*  313:     */ 
/*  314:     */ 
/*  315:     */ 
/*  316: 529 */           AttributeVisualizationPanel.this.repaint();
/*  317:     */         }
/*  318:     */         else
/*  319:     */         {
/*  320: 532 */           double[] histCounts = new double[AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_attribIndex).numValues()];
/*  321: 534 */           if (AttributeVisualizationPanel.this.m_as.nominalWeights.length > 0)
/*  322:     */           {
/*  323: 535 */             AttributeVisualizationPanel.this.m_maxValue = AttributeVisualizationPanel.this.m_as.nominalWeights[0];
/*  324: 536 */             for (int i = 0; i < AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_attribIndex).numValues(); i++) {
/*  325: 537 */               if (AttributeVisualizationPanel.this.m_as.nominalWeights[i] > AttributeVisualizationPanel.this.m_maxValue) {
/*  326: 538 */                 AttributeVisualizationPanel.this.m_maxValue = AttributeVisualizationPanel.this.m_as.nominalWeights[i];
/*  327:     */               }
/*  328:     */             }
/*  329:     */           }
/*  330:     */           else
/*  331:     */           {
/*  332: 542 */             AttributeVisualizationPanel.this.m_maxValue = 0.0D;
/*  333:     */           }
/*  334: 545 */           for (int k = 0; k < AttributeVisualizationPanel.this.m_data.numInstances(); k++) {
/*  335: 546 */             if (!AttributeVisualizationPanel.this.m_data.instance(k).isMissing(AttributeVisualizationPanel.this.m_attribIndex)) {
/*  336: 547 */               histCounts[((int)AttributeVisualizationPanel.this.m_data.instance(k).value(AttributeVisualizationPanel.this.m_attribIndex))] += AttributeVisualizationPanel.this.m_data.instance(k).weight();
/*  337:     */             }
/*  338:     */           }
/*  339: 551 */           AttributeVisualizationPanel.this.m_threadRun = false;
/*  340: 552 */           AttributeVisualizationPanel.this.m_displayCurrentAttribute = true;
/*  341: 553 */           AttributeVisualizationPanel.this.m_doneCurrentAttribute = true;
/*  342: 554 */           AttributeVisualizationPanel.this.m_histBarCounts = histCounts;
/*  343:     */           
/*  344:     */ 
/*  345:     */ 
/*  346:     */ 
/*  347: 559 */           AttributeVisualizationPanel.this.repaint();
/*  348:     */         }
/*  349:     */       }
/*  350:     */     }
/*  351:     */   }
/*  352:     */   
/*  353:     */   private class HistCalc
/*  354:     */     extends Thread
/*  355:     */   {
/*  356:     */     private HistCalc() {}
/*  357:     */     
/*  358:     */     public void run()
/*  359:     */     {
/*  360: 575 */       synchronized (AttributeVisualizationPanel.this.m_locker)
/*  361:     */       {
/*  362: 576 */         if ((AttributeVisualizationPanel.this.m_classIndex >= 0) && (AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_classIndex).isNominal()))
/*  363:     */         {
/*  364: 579 */           double intervalWidth = 0.0D;
/*  365:     */           
/*  366:     */ 
/*  367:     */ 
/*  368:     */ 
/*  369:     */ 
/*  370:     */ 
/*  371:     */ 
/*  372:     */ 
/*  373:     */ 
/*  374:     */ 
/*  375:     */ 
/*  376: 591 */           intervalWidth = 3.49D * AttributeVisualizationPanel.this.m_as.numericStats.stdDev * Math.pow(AttributeVisualizationPanel.this.m_data.numInstances(), -0.3333333333333333D);
/*  377:     */           
/*  378:     */ 
/*  379:     */ 
/*  380:     */ 
/*  381:     */ 
/*  382: 597 */           int intervals = Math.max(1, (int)Math.round((AttributeVisualizationPanel.this.m_as.numericStats.max - AttributeVisualizationPanel.this.m_as.numericStats.min) / intervalWidth));
/*  383: 610 */           if (intervals > AttributeVisualizationPanel.this.getWidth())
/*  384:     */           {
/*  385: 611 */             intervals = AttributeVisualizationPanel.this.getWidth() - 6;
/*  386: 612 */             if (intervals < 1) {
/*  387: 613 */               intervals = 1;
/*  388:     */             }
/*  389:     */           }
/*  390: 616 */           double[][] histClassCounts = new double[intervals][AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_classIndex).numValues() + 1];
/*  391:     */           
/*  392:     */ 
/*  393: 619 */           double barRange = (AttributeVisualizationPanel.this.m_as.numericStats.max - AttributeVisualizationPanel.this.m_as.numericStats.min) / histClassCounts.length;
/*  394:     */           
/*  395:     */ 
/*  396: 622 */           AttributeVisualizationPanel.this.m_maxValue = 0.0D;
/*  397: 624 */           if (AttributeVisualizationPanel.this.m_colorList.size() == 0) {
/*  398: 625 */             AttributeVisualizationPanel.this.m_colorList.add(Color.black);
/*  399:     */           }
/*  400: 627 */           for (int i = AttributeVisualizationPanel.this.m_colorList.size(); i < AttributeVisualizationPanel.this.m_data.attribute(AttributeVisualizationPanel.this.m_classIndex).numValues() + 1; i++)
/*  401:     */           {
/*  402: 629 */             Color pc = AttributeVisualizationPanel.m_defaultColors[((i - 1) % 10)];
/*  403: 630 */             int ija = (i - 1) / 10;
/*  404: 631 */             ija *= 2;
/*  405: 632 */             for (int j = 0; j < ija; j++) {
/*  406: 633 */               pc = pc.darker();
/*  407:     */             }
/*  408: 635 */             AttributeVisualizationPanel.this.m_colorList.add(pc);
/*  409:     */           }
/*  410: 638 */           for (int k = 0; k < AttributeVisualizationPanel.this.m_data.numInstances(); k++)
/*  411:     */           {
/*  412: 639 */             int t = 0;
/*  413:     */             try
/*  414:     */             {
/*  415: 642 */               if (!AttributeVisualizationPanel.this.m_data.instance(k).isMissing(AttributeVisualizationPanel.this.m_attribIndex))
/*  416:     */               {
/*  417: 644 */                 t = (int)Math.ceil((float)((AttributeVisualizationPanel.this.m_data.instance(k).value(AttributeVisualizationPanel.this.m_attribIndex) - AttributeVisualizationPanel.this.m_as.numericStats.min) / barRange));
/*  418: 646 */                 if (t == 0)
/*  419:     */                 {
/*  420: 647 */                   if (AttributeVisualizationPanel.this.m_data.instance(k).isMissing(AttributeVisualizationPanel.this.m_classIndex)) {
/*  421: 648 */                     histClassCounts[t][0] += AttributeVisualizationPanel.this.m_data.instance(k).weight();
/*  422:     */                   } else {
/*  423: 650 */                     histClassCounts[t][((int)AttributeVisualizationPanel.this.m_data.instance(k).value(AttributeVisualizationPanel.this.m_classIndex) + 1)] += AttributeVisualizationPanel.this.m_data.instance(k).weight();
/*  424:     */                   }
/*  425:     */                 }
/*  426: 656 */                 else if (AttributeVisualizationPanel.this.m_data.instance(k).isMissing(AttributeVisualizationPanel.this.m_classIndex)) {
/*  427: 657 */                   histClassCounts[(t - 1)][0] += AttributeVisualizationPanel.this.m_data.instance(k).weight();
/*  428:     */                 } else {
/*  429: 659 */                   histClassCounts[(t - 1)][((int)AttributeVisualizationPanel.this.m_data.instance(k).value(AttributeVisualizationPanel.this.m_classIndex) + 1)] += AttributeVisualizationPanel.this.m_data.instance(k).weight();
/*  430:     */                 }
/*  431:     */               }
/*  432:     */             }
/*  433:     */             catch (ArrayIndexOutOfBoundsException ae)
/*  434:     */             {
/*  435: 667 */               System.out.println("t:" + t + " barRange:" + barRange + " histLength:" + histClassCounts.length + " value:" + AttributeVisualizationPanel.this.m_data.instance(k).value(AttributeVisualizationPanel.this.m_attribIndex) + " min:" + AttributeVisualizationPanel.this.m_as.numericStats.min + " sumResult:" + (AttributeVisualizationPanel.this.m_data.instance(k).value(AttributeVisualizationPanel.this.m_attribIndex) - AttributeVisualizationPanel.this.m_as.numericStats.min) + " divideResult:" + (float)((AttributeVisualizationPanel.this.m_data.instance(k).value(AttributeVisualizationPanel.this.m_attribIndex) - AttributeVisualizationPanel.this.m_as.numericStats.min) / barRange) + " finalResult:" + Math.ceil((float)((AttributeVisualizationPanel.this.m_data.instance(k).value(AttributeVisualizationPanel.this.m_attribIndex) - AttributeVisualizationPanel.this.m_as.numericStats.min) / barRange)));
/*  436:     */             }
/*  437:     */           }
/*  438: 687 */           for (double[] histClassCount : histClassCounts)
/*  439:     */           {
/*  440: 688 */             double sum = 0.0D;
/*  441: 689 */             for (double element : histClassCount) {
/*  442: 690 */               sum += element;
/*  443:     */             }
/*  444: 692 */             if (AttributeVisualizationPanel.this.m_maxValue < sum) {
/*  445: 693 */               AttributeVisualizationPanel.this.m_maxValue = sum;
/*  446:     */             }
/*  447:     */           }
/*  448: 698 */           SparseInstance[] histClassCountsSparse = new SparseInstance[histClassCounts.length];
/*  449: 700 */           for (int i = 0; i < histClassCounts.length; i++)
/*  450:     */           {
/*  451: 701 */             int numSparseValues = 0;
/*  452: 702 */             for (int j = 0; j < histClassCounts[i].length; j++) {
/*  453: 703 */               if (histClassCounts[i][j] > 0.0D) {
/*  454: 704 */                 numSparseValues++;
/*  455:     */               }
/*  456:     */             }
/*  457: 707 */             double[] sparseValues = new double[numSparseValues];
/*  458: 708 */             int[] sparseIndices = new int[numSparseValues];
/*  459: 709 */             int count = 0;
/*  460: 710 */             for (int j = 0; j < histClassCounts[i].length; j++) {
/*  461: 711 */               if (histClassCounts[i][j] > 0.0D)
/*  462:     */               {
/*  463: 712 */                 sparseValues[count] = histClassCounts[i][j];
/*  464: 713 */                 sparseIndices[(count++)] = j;
/*  465:     */               }
/*  466:     */             }
/*  467: 717 */             SparseInstance tempS = new SparseInstance(1.0D, sparseValues, sparseIndices, histClassCounts[i].length);
/*  468:     */             
/*  469: 719 */             histClassCountsSparse[i] = tempS;
/*  470:     */           }
/*  471: 723 */           AttributeVisualizationPanel.this.m_histBarClassCounts = histClassCountsSparse;
/*  472: 724 */           AttributeVisualizationPanel.this.m_barRange = barRange;
/*  473:     */         }
/*  474:     */         else
/*  475:     */         {
/*  476: 750 */           double intervalWidth = 3.49D * AttributeVisualizationPanel.this.m_as.numericStats.stdDev * Math.pow(AttributeVisualizationPanel.this.m_data.numInstances(), -0.3333333333333333D);
/*  477:     */           
/*  478:     */ 
/*  479:     */ 
/*  480:     */ 
/*  481:     */ 
/*  482: 756 */           int intervals = Math.max(1, (int)Math.round((AttributeVisualizationPanel.this.m_as.numericStats.max - AttributeVisualizationPanel.this.m_as.numericStats.min) / intervalWidth));
/*  483: 764 */           if (intervals > AttributeVisualizationPanel.this.getWidth())
/*  484:     */           {
/*  485: 765 */             intervals = AttributeVisualizationPanel.this.getWidth() - 6;
/*  486: 766 */             if (intervals < 1) {
/*  487: 767 */               intervals = 1;
/*  488:     */             }
/*  489:     */           }
/*  490: 771 */           double[] histCounts = new double[intervals];
/*  491: 772 */           double barRange = (AttributeVisualizationPanel.this.m_as.numericStats.max - AttributeVisualizationPanel.this.m_as.numericStats.min) / histCounts.length;
/*  492:     */           
/*  493:     */ 
/*  494: 775 */           AttributeVisualizationPanel.this.m_maxValue = 0.0D;
/*  495: 777 */           for (int k = 0; k < AttributeVisualizationPanel.this.m_data.numInstances(); k++)
/*  496:     */           {
/*  497: 778 */             int t = 0;
/*  498: 782 */             if (!AttributeVisualizationPanel.this.m_data.instance(k).isMissing(AttributeVisualizationPanel.this.m_attribIndex)) {
/*  499:     */               try
/*  500:     */               {
/*  501: 788 */                 t = (int)Math.ceil((float)((AttributeVisualizationPanel.this.m_data.instance(k).value(AttributeVisualizationPanel.this.m_attribIndex) - AttributeVisualizationPanel.this.m_as.numericStats.min) / barRange));
/*  502: 790 */                 if (t == 0)
/*  503:     */                 {
/*  504: 791 */                   histCounts[t] += AttributeVisualizationPanel.this.m_data.instance(k).weight();
/*  505: 792 */                   if (histCounts[t] > AttributeVisualizationPanel.this.m_maxValue) {
/*  506: 793 */                     AttributeVisualizationPanel.this.m_maxValue = histCounts[t];
/*  507:     */                   }
/*  508:     */                 }
/*  509:     */                 else
/*  510:     */                 {
/*  511: 796 */                   histCounts[(t - 1)] += AttributeVisualizationPanel.this.m_data.instance(k).weight();
/*  512: 797 */                   if (histCounts[(t - 1)] > AttributeVisualizationPanel.this.m_maxValue) {
/*  513: 798 */                     AttributeVisualizationPanel.this.m_maxValue = histCounts[(t - 1)];
/*  514:     */                   }
/*  515:     */                 }
/*  516:     */               }
/*  517:     */               catch (ArrayIndexOutOfBoundsException ae)
/*  518:     */               {
/*  519: 802 */                 ae.printStackTrace();
/*  520: 803 */                 System.out.println("t:" + t + " barRange:" + barRange + " histLength:" + histCounts.length + " value:" + AttributeVisualizationPanel.this.m_data.instance(k).value(AttributeVisualizationPanel.this.m_attribIndex) + " min:" + AttributeVisualizationPanel.this.m_as.numericStats.min + " sumResult:" + (AttributeVisualizationPanel.this.m_data.instance(k).value(AttributeVisualizationPanel.this.m_attribIndex) - AttributeVisualizationPanel.this.m_as.numericStats.min) + " divideResult:" + (float)((AttributeVisualizationPanel.this.m_data.instance(k).value(AttributeVisualizationPanel.this.m_attribIndex) - AttributeVisualizationPanel.this.m_as.numericStats.min) / barRange) + " finalResult:" + Math.ceil((float)((AttributeVisualizationPanel.this.m_data.instance(k).value(AttributeVisualizationPanel.this.m_attribIndex) - AttributeVisualizationPanel.this.m_as.numericStats.min) / barRange)));
/*  521:     */               }
/*  522:     */             }
/*  523:     */           }
/*  524: 823 */           AttributeVisualizationPanel.this.m_histBarCounts = histCounts;
/*  525: 824 */           AttributeVisualizationPanel.this.m_barRange = barRange;
/*  526:     */         }
/*  527: 827 */         AttributeVisualizationPanel.this.m_threadRun = false;
/*  528: 828 */         AttributeVisualizationPanel.this.m_displayCurrentAttribute = true;
/*  529: 829 */         AttributeVisualizationPanel.this.m_doneCurrentAttribute = true;
/*  530:     */         
/*  531:     */ 
/*  532:     */ 
/*  533:     */ 
/*  534: 834 */         AttributeVisualizationPanel.this.repaint();
/*  535:     */       }
/*  536:     */     }
/*  537:     */   }
/*  538:     */   
/*  539:     */   public String getToolTipText(MouseEvent ev)
/*  540:     */   {
/*  541: 896 */     if ((this.m_as != null) && (this.m_as.nominalWeights != null))
/*  542:     */     {
/*  543: 899 */       float intervalWidth = getWidth() / this.m_as.nominalWeights.length;
/*  544:     */       
/*  545:     */ 
/*  546: 902 */       int x = 0;
/*  547:     */       int barWidth;
/*  548:     */       int barWidth;
/*  549: 905 */       if (intervalWidth > 5.0F) {
/*  550: 906 */         barWidth = (int)Math.floor(intervalWidth * 0.8F);
/*  551:     */       } else {
/*  552: 908 */         barWidth = 1;
/*  553:     */       }
/*  554: 914 */       x += (int)(Math.floor(intervalWidth * 0.1F) < 1.0D ? 1.0D : Math.floor(intervalWidth * 0.1F));
/*  555: 921 */       if (getWidth() - (this.m_as.nominalWeights.length * barWidth + (int)(Math.floor(intervalWidth * 0.2F) < 1.0D ? 1.0D : Math.floor(intervalWidth * 0.2F)) * this.m_as.nominalWeights.length) > 2) {
/*  556: 932 */         x += (getWidth() - (this.m_as.nominalWeights.length * barWidth + (int)(Math.floor(intervalWidth * 0.2F) < 1.0D ? 1.0D : Math.floor(intervalWidth * 0.2F)) * this.m_as.nominalWeights.length)) / 2;
/*  557:     */       }
/*  558: 938 */       for (int i = 0; i < this.m_as.nominalWeights.length; i++)
/*  559:     */       {
/*  560: 939 */         double heightRatio = (getHeight() - this.m_fm.getHeight()) / this.m_maxValue;
/*  561: 944 */         if ((ev.getX() >= x) && (ev.getX() <= x + barWidth) && (ev.getY() >= getHeight() - Math.round(this.m_as.nominalWeights[i] * heightRatio))) {
/*  562: 948 */           return this.m_data.attribute(this.m_attribIndex).value(i) + " [" + Utils.doubleToString(this.m_as.nominalWeights[i], 3) + "]";
/*  563:     */         }
/*  564: 953 */         x = x + barWidth + (int)(Math.floor(intervalWidth * 0.2F) < 1.0D ? 1.0D : Math.floor(intervalWidth * 0.2F));
/*  565:     */       }
/*  566:     */     }
/*  567: 958 */     else if ((!this.m_threadRun) && ((this.m_histBarCounts != null) || (this.m_histBarClassCounts != null)))
/*  568:     */     {
/*  569: 961 */       int x = 0;
/*  570: 962 */       double bar = this.m_as.numericStats.min;
/*  571: 965 */       if ((this.m_classIndex >= 0) && (this.m_data.attribute(this.m_classIndex).isNominal()))
/*  572:     */       {
/*  573: 969 */         int barWidth = (getWidth() - 6) / this.m_histBarClassCounts.length < 1 ? 1 : (getWidth() - 6) / this.m_histBarClassCounts.length;
/*  574:     */         
/*  575:     */ 
/*  576:     */ 
/*  577:     */ 
/*  578: 974 */         x = 3;
/*  579: 975 */         if (getWidth() - (x + this.m_histBarClassCounts.length * barWidth) > 5) {
/*  580: 976 */           x += (getWidth() - (x + this.m_histBarClassCounts.length * barWidth)) / 2;
/*  581:     */         }
/*  582: 979 */         if (ev.getX() - x >= 0)
/*  583:     */         {
/*  584: 983 */           int temp = (int)((ev.getX() - x) / (barWidth + 1.0E-010D));
/*  585: 984 */           if (temp == 0)
/*  586:     */           {
/*  587: 985 */             double sum = 0.0D;
/*  588: 986 */             for (int k = 0; k < this.m_histBarClassCounts[0].numValues(); k++) {
/*  589: 987 */               sum += this.m_histBarClassCounts[0].valueSparse(k);
/*  590:     */             }
/*  591: 991 */             return "<html><center><font face=Dialog size=-1>" + Utils.doubleToString(sum, 3) + "<br>" + "[" + Utils.doubleToString(bar + this.m_barRange * temp, 3) + ", " + Utils.doubleToString(bar + this.m_barRange * (temp + 1), 3) + "]" + "</font></center></html>";
/*  592:     */           }
/*  593: 995 */           if (temp < this.m_histBarClassCounts.length)
/*  594:     */           {
/*  595: 997 */             double sum = 0.0D;
/*  596: 998 */             for (int k = 0; k < this.m_histBarClassCounts[temp].numValues(); k++) {
/*  597: 999 */               sum += this.m_histBarClassCounts[temp].valueSparse(k);
/*  598:     */             }
/*  599:1003 */             return "<html><center><font face=Dialog size=-1>" + Utils.doubleToString(sum, 3) + "<br>(" + Utils.doubleToString(bar + this.m_barRange * temp, 3) + ", " + Utils.doubleToString(bar + this.m_barRange * (temp + 1), 3) + "]</font></center></html>";
/*  600:     */           }
/*  601:     */         }
/*  602:     */       }
/*  603:     */       else
/*  604:     */       {
/*  605:1010 */         int barWidth = (getWidth() - 6) / this.m_histBarCounts.length < 1 ? 1 : (getWidth() - 6) / this.m_histBarCounts.length;
/*  606:     */         
/*  607:     */ 
/*  608:     */ 
/*  609:     */ 
/*  610:1015 */         x = 3;
/*  611:1016 */         if (getWidth() - (x + this.m_histBarCounts.length * barWidth) > 5) {
/*  612:1017 */           x += (getWidth() - (x + this.m_histBarCounts.length * barWidth)) / 2;
/*  613:     */         }
/*  614:1020 */         if (ev.getX() - x >= 0)
/*  615:     */         {
/*  616:1022 */           int temp = (int)((ev.getX() - x) / (barWidth + 1.0E-010D));
/*  617:1025 */           if (temp == 0) {
/*  618:1026 */             return "<html><center><font face=Dialog size=-1>" + this.m_histBarCounts[0] + "<br>" + "[" + Utils.doubleToString(bar + this.m_barRange * temp, 3) + ", " + Utils.doubleToString(bar + this.m_barRange * (temp + 1), 3) + "]" + "</font></center></html>";
/*  619:     */           }
/*  620:1030 */           if (temp < this.m_histBarCounts.length) {
/*  621:1031 */             return "<html><center><font face=Dialog size=-1>" + this.m_histBarCounts[temp] + "<br>" + "(" + Utils.doubleToString(bar + this.m_barRange * temp, 3) + ", " + Utils.doubleToString(bar + this.m_barRange * (temp + 1), 3) + "]" + "</font></center></html>";
/*  622:     */           }
/*  623:     */         }
/*  624:     */       }
/*  625:     */     }
/*  626:1039 */     return PrintableComponent.getToolTipText(this.m_Printer);
/*  627:     */   }
/*  628:     */   
/*  629:     */   public void paintComponent(Graphics g)
/*  630:     */   {
/*  631:1049 */     g.clearRect(0, 0, getWidth(), getHeight());
/*  632:1051 */     if (this.m_as != null)
/*  633:     */     {
/*  634:1052 */       if ((!this.m_doneCurrentAttribute) && (!this.m_threadRun)) {
/*  635:1053 */         calcGraph(getWidth(), getHeight());
/*  636:     */       }
/*  637:1055 */       if ((!this.m_threadRun) && (this.m_displayCurrentAttribute))
/*  638:     */       {
/*  639:1058 */         int buttonHeight = 0;
/*  640:1060 */         if (this.m_colorAttrib != null) {
/*  641:1061 */           buttonHeight = this.m_colorAttrib.getHeight() + this.m_colorAttrib.getLocation().y;
/*  642:     */         }
/*  643:1066 */         if ((this.m_as.nominalWeights != null) && ((this.m_histBarClassCounts != null) || (this.m_histBarCounts != null)))
/*  644:     */         {
/*  645:1069 */           int x = 0;int y = 0;
/*  646:1073 */           if ((this.m_classIndex >= 0) && (this.m_data.attribute(this.m_classIndex).isNominal()))
/*  647:     */           {
/*  648:1076 */             double intervalWidth = getWidth() / this.m_histBarClassCounts.length;
/*  649:     */             int barWidth;
/*  650:     */             int barWidth;
/*  651:1083 */             if (intervalWidth > 5.0D) {
/*  652:1084 */               barWidth = (int)Math.floor(intervalWidth * 0.800000011920929D);
/*  653:     */             } else {
/*  654:1086 */               barWidth = 1;
/*  655:     */             }
/*  656:1092 */             x += (int)(Math.floor(intervalWidth * 0.1000000014901161D) < 1.0D ? 1.0D : Math.floor(intervalWidth * 0.1000000014901161D));
/*  657:1098 */             if (getWidth() - (this.m_histBarClassCounts.length * barWidth + (int)(Math.floor(intervalWidth * 0.2000000029802322D) < 1.0D ? 1.0D : Math.floor(intervalWidth * 0.2000000029802322D)) * this.m_histBarClassCounts.length) > 2) {
/*  658:1109 */               x += (getWidth() - (this.m_histBarClassCounts.length * barWidth + (int)(Math.floor(intervalWidth * 0.2000000029802322D) < 1.0D ? 1.0D : Math.floor(intervalWidth * 0.2000000029802322D)) * this.m_histBarClassCounts.length)) / 2;
/*  659:     */             }
/*  660:1118 */             double sum = 0.0D;
/*  661:1119 */             for (SparseInstance m_histBarClassCount : this.m_histBarClassCounts)
/*  662:     */             {
/*  663:1126 */               double heightRatio = (getHeight() - this.m_fm.getHeight() - buttonHeight) / this.m_maxValue;
/*  664:     */               
/*  665:1128 */               y = getHeight();
/*  666:1129 */               if (m_histBarClassCount != null) {
/*  667:1130 */                 for (int j = 0; j < m_histBarClassCount.numAttributes(); j++)
/*  668:     */                 {
/*  669:1131 */                   sum += m_histBarClassCount.value(j);
/*  670:1132 */                   y = (int)(y - Math.round(m_histBarClassCount.value(j) * heightRatio));
/*  671:     */                   
/*  672:     */ 
/*  673:1135 */                   g.setColor((Color)this.m_colorList.get(j));
/*  674:1136 */                   g.fillRect(x, y, barWidth, (int)Math.round(m_histBarClassCount.value(j) * heightRatio));
/*  675:     */                   
/*  676:1138 */                   g.setColor(Color.black);
/*  677:     */                 }
/*  678:     */               }
/*  679:1143 */               if (this.m_fm.stringWidth(Utils.doubleToString(sum, 1)) < intervalWidth) {
/*  680:1144 */                 g.drawString(Utils.doubleToString(sum, 1), x, y - 1);
/*  681:     */               }
/*  682:1150 */               x = x + barWidth + (int)(Math.floor(intervalWidth * 0.2000000029802322D) < 1.0D ? 1.0D : Math.floor(intervalWidth * 0.2000000029802322D));
/*  683:     */               
/*  684:     */ 
/*  685:     */ 
/*  686:     */ 
/*  687:1155 */               sum = 0.0D;
/*  688:     */             }
/*  689:     */           }
/*  690:     */           else
/*  691:     */           {
/*  692:1161 */             double intervalWidth = getWidth() / this.m_histBarCounts.length;
/*  693:     */             int barWidth;
/*  694:     */             int barWidth;
/*  695:1165 */             if (intervalWidth > 5.0D) {
/*  696:1166 */               barWidth = (int)Math.floor(intervalWidth * 0.800000011920929D);
/*  697:     */             } else {
/*  698:1168 */               barWidth = 1;
/*  699:     */             }
/*  700:1173 */             x += (int)(Math.floor(intervalWidth * 0.1000000014901161D) < 1.0D ? 1.0D : Math.floor(intervalWidth * 0.1000000014901161D));
/*  701:1178 */             if (getWidth() - (this.m_histBarCounts.length * barWidth + (int)(Math.floor(intervalWidth * 0.2000000029802322D) < 1.0D ? 1.0D : Math.floor(intervalWidth * 0.2000000029802322D)) * this.m_histBarCounts.length) > 2) {
/*  702:1183 */               x += (getWidth() - (this.m_histBarCounts.length * barWidth + (int)(Math.floor(intervalWidth * 0.2000000029802322D) < 1.0D ? 1.0D : Math.floor(intervalWidth * 0.2000000029802322D)) * this.m_histBarCounts.length)) / 2;
/*  703:     */             }
/*  704:1189 */             for (double m_histBarCount : this.m_histBarCounts)
/*  705:     */             {
/*  706:1192 */               double heightRatio = (getHeight() - this.m_fm.getHeight() - buttonHeight) / this.m_maxValue;
/*  707:     */               
/*  708:1194 */               y = (int)(getHeight() - Math.round(m_histBarCount * heightRatio));
/*  709:     */               
/*  710:1196 */               g.fillRect(x, y, barWidth, (int)Math.round(m_histBarCount * heightRatio));
/*  711:1200 */               if (this.m_fm.stringWidth(Utils.doubleToString(m_histBarCount, 1)) < intervalWidth) {
/*  712:1201 */                 g.drawString(Utils.doubleToString(m_histBarCount, 1), x, y - 1);
/*  713:     */               }
/*  714:1206 */               x = x + barWidth + (int)(Math.floor(intervalWidth * 0.2000000029802322D) < 1.0D ? 1.0D : Math.floor(intervalWidth * 0.2000000029802322D));
/*  715:     */             }
/*  716:     */           }
/*  717:     */         }
/*  718:1215 */         else if ((this.m_as.numericStats != null) && ((this.m_histBarClassCounts != null) || (this.m_histBarCounts != null)))
/*  719:     */         {
/*  720:1219 */           int x = 0;int y = 0;
/*  721:1223 */           if ((this.m_classIndex >= 0) && (this.m_data.attribute(this.m_classIndex).isNominal()))
/*  722:     */           {
/*  723:1227 */             int barWidth = (getWidth() - 6) / this.m_histBarClassCounts.length < 1 ? 1 : (getWidth() - 6) / this.m_histBarClassCounts.length;
/*  724:     */             
/*  725:     */ 
/*  726:     */ 
/*  727:     */ 
/*  728:1232 */             x = 3;
/*  729:1235 */             if (getWidth() - (x + this.m_histBarClassCounts.length * barWidth) > 5) {
/*  730:1245 */               x += (getWidth() - (x + this.m_histBarClassCounts.length * barWidth)) / 2;
/*  731:     */             }
/*  732:1249 */             for (SparseInstance m_histBarClassCount : this.m_histBarClassCounts) {
/*  733:1250 */               if (m_histBarClassCount != null)
/*  734:     */               {
/*  735:1254 */                 double heightRatio = (getHeight() - this.m_fm.getHeight() - buttonHeight - 19.0F) / this.m_maxValue;
/*  736:     */                 
/*  737:     */ 
/*  738:1257 */                 y = getHeight() - 19;
/*  739:     */                 
/*  740:1259 */                 double sum = 0.0D;
/*  741:1260 */                 for (int j = 0; j < m_histBarClassCount.numValues(); j++)
/*  742:     */                 {
/*  743:1261 */                   y = (int)(y - Math.round(m_histBarClassCount.valueSparse(j) * heightRatio));
/*  744:     */                   
/*  745:     */ 
/*  746:     */ 
/*  747:     */ 
/*  748:     */ 
/*  749:1267 */                   g.setColor((Color)this.m_colorList.get(m_histBarClassCount.index(j)));
/*  750:1269 */                   if (barWidth > 1) {
/*  751:1270 */                     g.fillRect(x, y, barWidth, (int)Math.round(m_histBarClassCount.valueSparse(j) * heightRatio));
/*  752:1276 */                   } else if (m_histBarClassCount.valueSparse(j) * heightRatio > 0.0D) {
/*  753:1277 */                     g.drawLine(x, y, x, (int)(y + Math.round(m_histBarClassCount.valueSparse(j) * heightRatio)));
/*  754:     */                   }
/*  755:1284 */                   g.setColor(Color.black);
/*  756:1285 */                   sum += m_histBarClassCount.valueSparse(j);
/*  757:     */                 }
/*  758:1288 */                 if (this.m_fm.stringWidth(" " + Utils.doubleToString(sum, 1)) < barWidth) {
/*  759:1289 */                   g.drawString(" " + Utils.doubleToString(sum, 1), x, y - 1);
/*  760:     */                 }
/*  761:1292 */                 x += barWidth;
/*  762:     */               }
/*  763:     */             }
/*  764:1298 */             x = 3;
/*  765:1299 */             if (getWidth() - (x + this.m_histBarClassCounts.length * barWidth) > 5) {
/*  766:1300 */               x += (getWidth() - (x + this.m_histBarClassCounts.length * barWidth)) / 2;
/*  767:     */             }
/*  768:1304 */             g.drawLine(x, getHeight() - 17, barWidth == 1 ? x + barWidth * this.m_histBarClassCounts.length - 1 : x + barWidth * this.m_histBarClassCounts.length, getHeight() - 17);
/*  769:     */             
/*  770:     */ 
/*  771:     */ 
/*  772:     */ 
/*  773:     */ 
/*  774:     */ 
/*  775:1311 */             g.drawLine(x, getHeight() - 16, x, getHeight() - 12);
/*  776:     */             
/*  777:1313 */             g.drawString(Utils.doubleToString(this.m_as.numericStats.min, 2), x, getHeight() - 12 + this.m_fm.getHeight());
/*  778:     */             
/*  779:1315 */             g.drawLine(x + barWidth * this.m_histBarClassCounts.length / 2, getHeight() - 16, x + barWidth * this.m_histBarClassCounts.length / 2, getHeight() - 12);
/*  780:     */             
/*  781:     */ 
/*  782:     */ 
/*  783:     */ 
/*  784:     */ 
/*  785:     */ 
/*  786:1322 */             g.drawString(Utils.doubleToString(this.m_as.numericStats.max / 2.0D + this.m_as.numericStats.min / 2.0D, 2), x + barWidth * this.m_histBarClassCounts.length / 2 - this.m_fm.stringWidth(Utils.doubleToString(this.m_as.numericStats.max / 2.0D + this.m_as.numericStats.min / 2.0D, 2)) / 2, getHeight() - 12 + this.m_fm.getHeight());
/*  787:     */             
/*  788:     */ 
/*  789:     */ 
/*  790:     */ 
/*  791:     */ 
/*  792:     */ 
/*  793:     */ 
/*  794:     */ 
/*  795:1331 */             g.drawLine(barWidth == 1 ? x + barWidth * this.m_histBarClassCounts.length - 1 : x + barWidth * this.m_histBarClassCounts.length, getHeight() - 16, barWidth == 1 ? x + barWidth * this.m_histBarClassCounts.length - 1 : x + barWidth * this.m_histBarClassCounts.length, getHeight() - 12);
/*  796:     */             
/*  797:     */ 
/*  798:     */ 
/*  799:     */ 
/*  800:     */ 
/*  801:1337 */             g.drawString(Utils.doubleToString(this.m_as.numericStats.max, 2), barWidth == 1 ? x + barWidth * this.m_histBarClassCounts.length - this.m_fm.stringWidth(Utils.doubleToString(this.m_as.numericStats.max, 2)) - 1 : x + barWidth * this.m_histBarClassCounts.length - this.m_fm.stringWidth(Utils.doubleToString(this.m_as.numericStats.max, 2)), getHeight() - 12 + this.m_fm.getHeight());
/*  802:     */           }
/*  803:     */           else
/*  804:     */           {
/*  805:1352 */             int barWidth = (getWidth() - 6) / this.m_histBarCounts.length < 1 ? 1 : (getWidth() - 6) / this.m_histBarCounts.length;
/*  806:     */             
/*  807:     */ 
/*  808:     */ 
/*  809:1356 */             x = 3;
/*  810:1357 */             if (getWidth() - (x + this.m_histBarCounts.length * barWidth) > 5) {
/*  811:1358 */               x += (getWidth() - (x + this.m_histBarCounts.length * barWidth)) / 2;
/*  812:     */             }
/*  813:1362 */             for (double m_histBarCount : this.m_histBarCounts)
/*  814:     */             {
/*  815:1366 */               double heightRatio = (getHeight() - this.m_fm.getHeight() - buttonHeight - 19.0F) / this.m_maxValue;
/*  816:     */               
/*  817:     */ 
/*  818:1369 */               y = (int)(getHeight() - Math.round(m_histBarCount * heightRatio) - 19L);
/*  819:1374 */               if (barWidth > 1) {
/*  820:1375 */                 g.drawRect(x, y, barWidth, (int)Math.round(m_histBarCount * heightRatio));
/*  821:1377 */               } else if (m_histBarCount * heightRatio > 0.0D) {
/*  822:1378 */                 g.drawLine(x, y, x, (int)(y + Math.round(m_histBarCount * heightRatio)));
/*  823:     */               }
/*  824:1381 */               if (this.m_fm.stringWidth(" " + Utils.doubleToString(m_histBarCount, 1)) < barWidth) {
/*  825:1383 */                 g.drawString(" " + Utils.doubleToString(m_histBarCount, 1), x, y - 1);
/*  826:     */               }
/*  827:1387 */               x += barWidth;
/*  828:     */             }
/*  829:1391 */             x = 3;
/*  830:1392 */             if (getWidth() - (x + this.m_histBarCounts.length * barWidth) > 5) {
/*  831:1393 */               x += (getWidth() - (x + this.m_histBarCounts.length * barWidth)) / 2;
/*  832:     */             }
/*  833:1398 */             g.drawLine(x, getHeight() - 17, barWidth == 1 ? x + barWidth * this.m_histBarCounts.length - 1 : x + barWidth * this.m_histBarCounts.length, getHeight() - 17);
/*  834:     */             
/*  835:     */ 
/*  836:1401 */             g.drawLine(x, getHeight() - 16, x, getHeight() - 12);
/*  837:     */             
/*  838:1403 */             g.drawString(Utils.doubleToString(this.m_as.numericStats.min, 2), x, getHeight() - 12 + this.m_fm.getHeight());
/*  839:     */             
/*  840:1405 */             g.drawLine(x + barWidth * this.m_histBarCounts.length / 2, getHeight() - 16, x + barWidth * this.m_histBarCounts.length / 2, getHeight() - 12);
/*  841:     */             
/*  842:     */ 
/*  843:1408 */             g.drawString(Utils.doubleToString(this.m_as.numericStats.max / 2.0D + this.m_as.numericStats.min / 2.0D, 2), x + barWidth * this.m_histBarCounts.length / 2 - this.m_fm.stringWidth(Utils.doubleToString(this.m_as.numericStats.max / 2.0D + this.m_as.numericStats.min / 2.0D, 2)) / 2, getHeight() - 12 + this.m_fm.getHeight());
/*  844:     */             
/*  845:     */ 
/*  846:     */ 
/*  847:     */ 
/*  848:     */ 
/*  849:     */ 
/*  850:     */ 
/*  851:     */ 
/*  852:1417 */             g.drawLine(barWidth == 1 ? x + barWidth * this.m_histBarCounts.length - 1 : x + barWidth * this.m_histBarCounts.length, getHeight() - 16, barWidth == 1 ? x + barWidth * this.m_histBarCounts.length - 1 : x + barWidth * this.m_histBarCounts.length, getHeight() - 12);
/*  853:     */             
/*  854:     */ 
/*  855:     */ 
/*  856:     */ 
/*  857:     */ 
/*  858:1423 */             g.drawString(Utils.doubleToString(this.m_as.numericStats.max, 2), barWidth == 1 ? x + barWidth * this.m_histBarCounts.length - this.m_fm.stringWidth(Utils.doubleToString(this.m_as.numericStats.max, 2)) - 1 : x + barWidth * this.m_histBarCounts.length - this.m_fm.stringWidth(Utils.doubleToString(this.m_as.numericStats.max, 2)), getHeight() - 12 + this.m_fm.getHeight());
/*  859:     */           }
/*  860:     */         }
/*  861:     */         else
/*  862:     */         {
/*  863:1440 */           g.clearRect(0, 0, getWidth(), getHeight());
/*  864:1441 */           g.drawString("Attribute is neither numeric nor nominal.", getWidth() / 2 - this.m_fm.stringWidth("Attribute is neither numeric nor nominal.") / 2, getHeight() / 2 - this.m_fm.getHeight() / 2);
/*  865:     */         }
/*  866:     */       }
/*  867:1448 */       else if (this.m_displayCurrentAttribute)
/*  868:     */       {
/*  869:1450 */         g.clearRect(0, 0, getWidth(), getHeight());
/*  870:1451 */         g.drawString("Calculating. Please Wait...", getWidth() / 2 - this.m_fm.stringWidth("Calculating. Please Wait...") / 2, getHeight() / 2 - this.m_fm.getHeight() / 2);
/*  871:     */       }
/*  872:1454 */       else if (!this.m_displayCurrentAttribute)
/*  873:     */       {
/*  874:1455 */         g.clearRect(0, 0, getWidth(), getHeight());
/*  875:1456 */         g.drawString("Too many values to display.", getWidth() / 2 - this.m_fm.stringWidth("Too many values to display.") / 2, getHeight() / 2 - this.m_fm.getHeight() / 2);
/*  876:     */       }
/*  877:     */     }
/*  878:     */   }
/*  879:     */   
/*  880:     */   public static void main(String[] args)
/*  881:     */   {
/*  882:1469 */     if (args.length != 3)
/*  883:     */     {
/*  884:1470 */       JFrame jf = new JFrame("AttribVisualization");
/*  885:1471 */       AttributeVisualizationPanel ap = new AttributeVisualizationPanel();
/*  886:     */       try
/*  887:     */       {
/*  888:1473 */         Instances ins = new Instances(new FileReader(args[0]));
/*  889:1474 */         ap.setInstances(ins);
/*  890:1475 */         System.out.println("Loaded: " + args[0] + "\nRelation: " + ap.m_data.relationName() + "\nAttributes: " + ap.m_data.numAttributes());
/*  891:     */         
/*  892:     */ 
/*  893:1478 */         ap.setAttribute(Integer.parseInt(args[1]));
/*  894:     */       }
/*  895:     */       catch (Exception ex)
/*  896:     */       {
/*  897:1480 */         ex.printStackTrace();
/*  898:1481 */         System.exit(-1);
/*  899:     */       }
/*  900:1483 */       System.out.println("The attributes are: ");
/*  901:1484 */       for (int i = 0; i < ap.m_data.numAttributes(); i++) {
/*  902:1485 */         System.out.println(ap.m_data.attribute(i).name());
/*  903:     */       }
/*  904:1488 */       jf.setSize(500, 300);
/*  905:1489 */       jf.getContentPane().setLayout(new BorderLayout());
/*  906:1490 */       jf.getContentPane().add(ap, "Center");
/*  907:1491 */       jf.setDefaultCloseOperation(3);
/*  908:1492 */       jf.setVisible(true);
/*  909:     */     }
/*  910:     */     else
/*  911:     */     {
/*  912:1494 */       System.out.println("Usage: java AttributeVisualizationPanel [arff file] [index of attribute]");
/*  913:     */     }
/*  914:     */   }
/*  915:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.AttributeVisualizationPanel
 * JD-Core Version:    0.7.0.1
 */