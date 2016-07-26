/*    1:     */ package weka.gui.beans;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Color;
/*    5:     */ import java.awt.Container;
/*    6:     */ import java.awt.Dimension;
/*    7:     */ import java.awt.FlowLayout;
/*    8:     */ import java.awt.Graphics;
/*    9:     */ import java.awt.GraphicsEnvironment;
/*   10:     */ import java.awt.GridLayout;
/*   11:     */ import java.awt.event.ActionEvent;
/*   12:     */ import java.awt.event.ActionListener;
/*   13:     */ import java.awt.event.FocusEvent;
/*   14:     */ import java.awt.event.FocusListener;
/*   15:     */ import java.awt.event.WindowAdapter;
/*   16:     */ import java.awt.event.WindowEvent;
/*   17:     */ import java.beans.EventSetDescriptor;
/*   18:     */ import java.beans.PropertyVetoException;
/*   19:     */ import java.beans.VetoableChangeListener;
/*   20:     */ import java.beans.beancontext.BeanContext;
/*   21:     */ import java.beans.beancontext.BeanContextChild;
/*   22:     */ import java.beans.beancontext.BeanContextChildSupport;
/*   23:     */ import java.io.BufferedReader;
/*   24:     */ import java.io.FileReader;
/*   25:     */ import java.io.PrintStream;
/*   26:     */ import java.io.Serializable;
/*   27:     */ import java.util.ArrayList;
/*   28:     */ import java.util.Enumeration;
/*   29:     */ import java.util.EventObject;
/*   30:     */ import java.util.List;
/*   31:     */ import java.util.Vector;
/*   32:     */ import javax.swing.BorderFactory;
/*   33:     */ import javax.swing.ButtonGroup;
/*   34:     */ import javax.swing.JButton;
/*   35:     */ import javax.swing.JFrame;
/*   36:     */ import javax.swing.JLabel;
/*   37:     */ import javax.swing.JPanel;
/*   38:     */ import javax.swing.JRadioButton;
/*   39:     */ import javax.swing.JSlider;
/*   40:     */ import javax.swing.JTextField;
/*   41:     */ import javax.swing.event.ChangeEvent;
/*   42:     */ import javax.swing.event.ChangeListener;
/*   43:     */ import weka.classifiers.Classifier;
/*   44:     */ import weka.classifiers.bayes.NaiveBayes;
/*   45:     */ import weka.classifiers.evaluation.EvaluationUtils;
/*   46:     */ import weka.classifiers.evaluation.Prediction;
/*   47:     */ import weka.classifiers.evaluation.ThresholdCurve;
/*   48:     */ import weka.core.Attribute;
/*   49:     */ import weka.core.DenseInstance;
/*   50:     */ import weka.core.Instance;
/*   51:     */ import weka.core.Instances;
/*   52:     */ import weka.core.Utils;
/*   53:     */ import weka.gui.Logger;
/*   54:     */ import weka.gui.visualize.PlotData2D;
/*   55:     */ import weka.gui.visualize.VisualizePanel;
/*   56:     */ 
/*   57:     */ @KFStep(category="Visualize", toolTipText="Interactive cost/benefit analysis")
/*   58:     */ public class CostBenefitAnalysis
/*   59:     */   extends JPanel
/*   60:     */   implements BeanCommon, ThresholdDataListener, Visible, UserRequestAcceptor, Serializable, BeanContextChild, HeadlessEventCollector
/*   61:     */ {
/*   62:     */   private static final long serialVersionUID = 8647471654613320469L;
/*   63:  86 */   protected BeanVisual m_visual = new BeanVisual("CostBenefitAnalysis", "weka/gui/beans/icons/ModelPerformanceChart.gif", "weka/gui/beans/icons/ModelPerformanceChart_animated.gif");
/*   64:     */   protected transient JFrame m_popupFrame;
/*   65:  92 */   protected boolean m_framePoppedUp = false;
/*   66:     */   private transient AnalysisPanel m_analysisPanel;
/*   67:     */   protected boolean m_design;
/*   68: 104 */   protected transient BeanContext m_beanContext = null;
/*   69: 109 */   protected BeanContextChildSupport m_bcSupport = new BeanContextChildSupport(this);
/*   70:     */   protected Object m_listenee;
/*   71:     */   protected List<EventObject> m_headlessEvents;
/*   72:     */   
/*   73:     */   protected static class AnalysisPanel
/*   74:     */     extends JPanel
/*   75:     */   {
/*   76:     */     private static final long serialVersionUID = 5364871945448769003L;
/*   77: 130 */     protected VisualizePanel m_performancePanel = new VisualizePanel();
/*   78: 133 */     protected VisualizePanel m_costBenefitPanel = new VisualizePanel();
/*   79:     */     protected Attribute m_classAttribute;
/*   80:     */     protected PlotData2D m_masterPlot;
/*   81:     */     protected PlotData2D m_costBenefit;
/*   82:     */     protected int[] m_shapeSizes;
/*   83: 151 */     protected int m_previousShapeIndex = -1;
/*   84: 154 */     protected JSlider m_thresholdSlider = new JSlider(0, 100, 0);
/*   85: 156 */     protected JRadioButton m_percPop = new JRadioButton("% of Population");
/*   86: 157 */     protected JRadioButton m_percOfTarget = new JRadioButton("% of Target (recall)");
/*   87: 159 */     protected JRadioButton m_threshold = new JRadioButton("Score Threshold");
/*   88: 161 */     protected JLabel m_percPopLab = new JLabel();
/*   89: 162 */     protected JLabel m_percOfTargetLab = new JLabel();
/*   90: 163 */     protected JLabel m_thresholdLab = new JLabel();
/*   91: 166 */     protected JLabel m_conf_predictedA = new JLabel("Predicted (a)", 4);
/*   92: 168 */     protected JLabel m_conf_predictedB = new JLabel("Predicted (b)", 4);
/*   93: 170 */     protected JLabel m_conf_actualA = new JLabel(" Actual (a):");
/*   94: 171 */     protected JLabel m_conf_actualB = new JLabel(" Actual (b):");
/*   95: 172 */     protected ConfusionCell m_conf_aa = new ConfusionCell();
/*   96: 173 */     protected ConfusionCell m_conf_ab = new ConfusionCell();
/*   97: 174 */     protected ConfusionCell m_conf_ba = new ConfusionCell();
/*   98: 175 */     protected ConfusionCell m_conf_bb = new ConfusionCell();
/*   99: 178 */     protected JLabel m_cost_predictedA = new JLabel("Predicted (a)", 4);
/*  100: 180 */     protected JLabel m_cost_predictedB = new JLabel("Predicted (b)", 4);
/*  101: 182 */     protected JLabel m_cost_actualA = new JLabel(" Actual (a)");
/*  102: 183 */     protected JLabel m_cost_actualB = new JLabel(" Actual (b)");
/*  103: 184 */     protected JTextField m_cost_aa = new JTextField("0.0", 5);
/*  104: 185 */     protected JTextField m_cost_ab = new JTextField("1.0", 5);
/*  105: 186 */     protected JTextField m_cost_ba = new JTextField("1.0", 5);
/*  106: 187 */     protected JTextField m_cost_bb = new JTextField("0.0", 5);
/*  107: 188 */     protected JButton m_maximizeCB = new JButton("Maximize Cost/Benefit");
/*  108: 189 */     protected JButton m_minimizeCB = new JButton("Minimize Cost/Benefit");
/*  109: 190 */     protected JRadioButton m_costR = new JRadioButton("Cost");
/*  110: 191 */     protected JRadioButton m_benefitR = new JRadioButton("Benefit");
/*  111: 192 */     protected JLabel m_costBenefitL = new JLabel("Cost: ", 4);
/*  112: 193 */     protected JLabel m_costBenefitV = new JLabel("0");
/*  113: 194 */     protected JLabel m_randomV = new JLabel("0");
/*  114: 195 */     protected JLabel m_gainV = new JLabel("0");
/*  115:     */     protected int m_originalPopSize;
/*  116: 200 */     protected JTextField m_totalPopField = new JTextField(6);
/*  117:     */     protected int m_totalPopPrevious;
/*  118: 204 */     protected JLabel m_classificationAccV = new JLabel("-");
/*  119:     */     protected double m_tpPrevious;
/*  120:     */     protected double m_fpPrevious;
/*  121:     */     protected double m_tnPrevious;
/*  122:     */     protected double m_fnPrevious;
/*  123:     */     
/*  124:     */     protected static class ConfusionCell
/*  125:     */       extends JPanel
/*  126:     */     {
/*  127:     */       private static final long serialVersionUID = 6148640235434494767L;
/*  128: 224 */       private final JLabel m_conf_cell = new JLabel("-", 4);
/*  129: 225 */       JLabel m_conf_perc = new JLabel("-", 4);
/*  130:     */       private final JPanel m_percentageP;
/*  131: 229 */       protected double m_percentage = 0.0D;
/*  132:     */       
/*  133:     */       public ConfusionCell()
/*  134:     */       {
/*  135: 233 */         setLayout(new BorderLayout());
/*  136: 234 */         setBorder(BorderFactory.createEtchedBorder());
/*  137:     */         
/*  138: 236 */         add(this.m_conf_cell, "North");
/*  139:     */         
/*  140: 238 */         this.m_percentageP = new JPanel()
/*  141:     */         {
/*  142:     */           public void paintComponent(Graphics gx)
/*  143:     */           {
/*  144: 241 */             super.paintComponent(gx);
/*  145: 243 */             if (CostBenefitAnalysis.AnalysisPanel.ConfusionCell.this.m_percentage > 0.0D)
/*  146:     */             {
/*  147: 244 */               gx.setColor(Color.BLUE);
/*  148: 245 */               int height = getHeight();
/*  149: 246 */               double width = getWidth();
/*  150: 247 */               int barWidth = (int)(CostBenefitAnalysis.AnalysisPanel.ConfusionCell.this.m_percentage * width);
/*  151: 248 */               gx.fillRect(0, 0, barWidth, height);
/*  152:     */             }
/*  153:     */           }
/*  154: 252 */         };
/*  155: 253 */         Dimension d = new Dimension(30, 5);
/*  156: 254 */         this.m_percentageP.setMinimumSize(d);
/*  157: 255 */         this.m_percentageP.setPreferredSize(d);
/*  158: 256 */         JPanel percHolder = new JPanel();
/*  159: 257 */         percHolder.setLayout(new BorderLayout());
/*  160: 258 */         percHolder.add(this.m_percentageP, "Center");
/*  161: 259 */         percHolder.add(this.m_conf_perc, "East");
/*  162:     */         
/*  163: 261 */         add(percHolder, "South");
/*  164:     */       }
/*  165:     */       
/*  166:     */       public void setCellValue(double cellValue, double max, double scaleFactor, int precision)
/*  167:     */       {
/*  168: 274 */         if (!Utils.isMissingValue(cellValue)) {
/*  169: 275 */           this.m_percentage = (cellValue / max);
/*  170:     */         } else {
/*  171: 277 */           this.m_percentage = 0.0D;
/*  172:     */         }
/*  173: 280 */         this.m_conf_cell.setText(Utils.doubleToString(cellValue * scaleFactor, 0));
/*  174: 281 */         this.m_conf_perc.setText(Utils.doubleToString(this.m_percentage * 100.0D, precision) + "%");
/*  175:     */         
/*  176:     */ 
/*  177:     */ 
/*  178: 285 */         this.m_percentageP.repaint();
/*  179:     */       }
/*  180:     */     }
/*  181:     */     
/*  182:     */     public AnalysisPanel()
/*  183:     */     {
/*  184: 290 */       setLayout(new BorderLayout());
/*  185: 291 */       this.m_performancePanel.setShowAttBars(false);
/*  186: 292 */       this.m_performancePanel.setShowClassPanel(false);
/*  187: 293 */       this.m_costBenefitPanel.setShowAttBars(false);
/*  188: 294 */       this.m_costBenefitPanel.setShowClassPanel(false);
/*  189:     */       
/*  190: 296 */       Dimension size = new Dimension(500, 400);
/*  191: 297 */       this.m_performancePanel.setPreferredSize(size);
/*  192: 298 */       this.m_performancePanel.setMinimumSize(size);
/*  193:     */       
/*  194: 300 */       size = new Dimension(500, 400);
/*  195: 301 */       this.m_costBenefitPanel.setMinimumSize(size);
/*  196: 302 */       this.m_costBenefitPanel.setPreferredSize(size);
/*  197:     */       
/*  198: 304 */       this.m_thresholdSlider.addChangeListener(new ChangeListener()
/*  199:     */       {
/*  200:     */         public void stateChanged(ChangeEvent e)
/*  201:     */         {
/*  202: 307 */           CostBenefitAnalysis.AnalysisPanel.this.updateInfoForSliderValue(CostBenefitAnalysis.AnalysisPanel.this.m_thresholdSlider.getValue() / 100.0D);
/*  203:     */         }
/*  204: 310 */       });
/*  205: 311 */       JPanel plotHolder = new JPanel();
/*  206: 312 */       plotHolder.setLayout(new GridLayout(1, 2));
/*  207: 313 */       plotHolder.add(this.m_performancePanel);
/*  208: 314 */       plotHolder.add(this.m_costBenefitPanel);
/*  209: 315 */       add(plotHolder, "Center");
/*  210:     */       
/*  211: 317 */       JPanel lowerPanel = new JPanel();
/*  212: 318 */       lowerPanel.setLayout(new BorderLayout());
/*  213:     */       
/*  214: 320 */       ButtonGroup bGroup = new ButtonGroup();
/*  215: 321 */       bGroup.add(this.m_percPop);
/*  216: 322 */       bGroup.add(this.m_percOfTarget);
/*  217: 323 */       bGroup.add(this.m_threshold);
/*  218:     */       
/*  219: 325 */       ButtonGroup bGroup2 = new ButtonGroup();
/*  220: 326 */       bGroup2.add(this.m_costR);
/*  221: 327 */       bGroup2.add(this.m_benefitR);
/*  222: 328 */       ActionListener rl = new ActionListener()
/*  223:     */       {
/*  224:     */         public void actionPerformed(ActionEvent e)
/*  225:     */         {
/*  226: 331 */           if (CostBenefitAnalysis.AnalysisPanel.this.m_costR.isSelected()) {
/*  227: 332 */             CostBenefitAnalysis.AnalysisPanel.this.m_costBenefitL.setText("Cost: ");
/*  228:     */           } else {
/*  229: 334 */             CostBenefitAnalysis.AnalysisPanel.this.m_costBenefitL.setText("Benefit: ");
/*  230:     */           }
/*  231: 337 */           double gain = Double.parseDouble(CostBenefitAnalysis.AnalysisPanel.this.m_gainV.getText());
/*  232: 338 */           gain = -gain;
/*  233: 339 */           CostBenefitAnalysis.AnalysisPanel.this.m_gainV.setText(Utils.doubleToString(gain, 2));
/*  234:     */         }
/*  235: 341 */       };
/*  236: 342 */       this.m_costR.addActionListener(rl);
/*  237: 343 */       this.m_benefitR.addActionListener(rl);
/*  238: 344 */       this.m_costR.setSelected(true);
/*  239:     */       
/*  240: 346 */       this.m_percPop.setSelected(true);
/*  241: 347 */       JPanel threshPanel = new JPanel();
/*  242: 348 */       threshPanel.setLayout(new BorderLayout());
/*  243: 349 */       JPanel radioHolder = new JPanel();
/*  244: 350 */       radioHolder.setLayout(new FlowLayout());
/*  245: 351 */       radioHolder.add(this.m_percPop);
/*  246: 352 */       radioHolder.add(this.m_percOfTarget);
/*  247: 353 */       radioHolder.add(this.m_threshold);
/*  248: 354 */       threshPanel.add(radioHolder, "North");
/*  249: 355 */       threshPanel.add(this.m_thresholdSlider, "South");
/*  250:     */       
/*  251: 357 */       JPanel threshInfoPanel = new JPanel();
/*  252: 358 */       threshInfoPanel.setLayout(new GridLayout(3, 2));
/*  253: 359 */       threshInfoPanel.add(new JLabel("% of Population: ", 4));
/*  254:     */       
/*  255: 361 */       threshInfoPanel.add(this.m_percPopLab);
/*  256: 362 */       threshInfoPanel.add(new JLabel("% of Target: ", 4));
/*  257: 363 */       threshInfoPanel.add(this.m_percOfTargetLab);
/*  258: 364 */       threshInfoPanel.add(new JLabel("Score Threshold: ", 4));
/*  259:     */       
/*  260: 366 */       threshInfoPanel.add(this.m_thresholdLab);
/*  261:     */       
/*  262: 368 */       JPanel threshHolder = new JPanel();
/*  263: 369 */       threshHolder.setBorder(BorderFactory.createTitledBorder("Threshold"));
/*  264: 370 */       threshHolder.setLayout(new BorderLayout());
/*  265: 371 */       threshHolder.add(threshPanel, "Center");
/*  266: 372 */       threshHolder.add(threshInfoPanel, "East");
/*  267:     */       
/*  268: 374 */       lowerPanel.add(threshHolder, "North");
/*  269:     */       
/*  270:     */ 
/*  271: 377 */       JPanel matrixHolder = new JPanel();
/*  272: 378 */       matrixHolder.setLayout(new GridLayout(1, 2));
/*  273:     */       
/*  274:     */ 
/*  275: 381 */       JPanel confusionPanel = new JPanel();
/*  276: 382 */       confusionPanel.setLayout(new GridLayout(3, 3));
/*  277: 383 */       confusionPanel.add(this.m_conf_predictedA);
/*  278: 384 */       confusionPanel.add(this.m_conf_predictedB);
/*  279: 385 */       confusionPanel.add(new JLabel());
/*  280: 386 */       confusionPanel.add(this.m_conf_aa);
/*  281: 387 */       confusionPanel.add(this.m_conf_ab);
/*  282: 388 */       confusionPanel.add(this.m_conf_actualA);
/*  283: 389 */       confusionPanel.add(this.m_conf_ba);
/*  284: 390 */       confusionPanel.add(this.m_conf_bb);
/*  285: 391 */       confusionPanel.add(this.m_conf_actualB);
/*  286: 392 */       JPanel tempHolderCA = new JPanel();
/*  287: 393 */       tempHolderCA.setLayout(new BorderLayout());
/*  288: 394 */       tempHolderCA.setBorder(BorderFactory.createTitledBorder("Confusion Matrix"));
/*  289:     */       
/*  290: 396 */       tempHolderCA.add(confusionPanel, "Center");
/*  291:     */       
/*  292: 398 */       JPanel accHolder = new JPanel();
/*  293: 399 */       accHolder.setLayout(new FlowLayout(0));
/*  294: 400 */       accHolder.add(new JLabel("Classification Accuracy: "));
/*  295: 401 */       accHolder.add(this.m_classificationAccV);
/*  296: 402 */       tempHolderCA.add(accHolder, "South");
/*  297:     */       
/*  298: 404 */       matrixHolder.add(tempHolderCA);
/*  299:     */       
/*  300:     */ 
/*  301: 407 */       JPanel costPanel = new JPanel();
/*  302: 408 */       costPanel.setBorder(BorderFactory.createTitledBorder("Cost Matrix"));
/*  303: 409 */       costPanel.setLayout(new BorderLayout());
/*  304:     */       
/*  305: 411 */       JPanel cmHolder = new JPanel();
/*  306: 412 */       cmHolder.setLayout(new GridLayout(3, 3));
/*  307: 413 */       cmHolder.add(this.m_cost_predictedA);
/*  308: 414 */       cmHolder.add(this.m_cost_predictedB);
/*  309: 415 */       cmHolder.add(new JLabel());
/*  310: 416 */       cmHolder.add(this.m_cost_aa);
/*  311: 417 */       cmHolder.add(this.m_cost_ab);
/*  312: 418 */       cmHolder.add(this.m_cost_actualA);
/*  313: 419 */       cmHolder.add(this.m_cost_ba);
/*  314: 420 */       cmHolder.add(this.m_cost_bb);
/*  315: 421 */       cmHolder.add(this.m_cost_actualB);
/*  316: 422 */       costPanel.add(cmHolder, "Center");
/*  317:     */       
/*  318: 424 */       FocusListener fl = new FocusListener()
/*  319:     */       {
/*  320:     */         public void focusGained(FocusEvent e) {}
/*  321:     */         
/*  322:     */         public void focusLost(FocusEvent e)
/*  323:     */         {
/*  324: 432 */           if (CostBenefitAnalysis.AnalysisPanel.this.constructCostBenefitData())
/*  325:     */           {
/*  326:     */             try
/*  327:     */             {
/*  328: 434 */               CostBenefitAnalysis.AnalysisPanel.this.m_costBenefitPanel.setMasterPlot(CostBenefitAnalysis.AnalysisPanel.this.m_costBenefit);
/*  329: 435 */               CostBenefitAnalysis.AnalysisPanel.this.m_costBenefitPanel.validate();
/*  330: 436 */               CostBenefitAnalysis.AnalysisPanel.this.m_costBenefitPanel.repaint();
/*  331:     */             }
/*  332:     */             catch (Exception ex)
/*  333:     */             {
/*  334: 438 */               ex.printStackTrace();
/*  335:     */             }
/*  336: 440 */             CostBenefitAnalysis.AnalysisPanel.this.updateCostBenefit();
/*  337:     */           }
/*  338:     */         }
/*  339: 444 */       };
/*  340: 445 */       ActionListener al = new ActionListener()
/*  341:     */       {
/*  342:     */         public void actionPerformed(ActionEvent e)
/*  343:     */         {
/*  344: 448 */           if (CostBenefitAnalysis.AnalysisPanel.this.constructCostBenefitData())
/*  345:     */           {
/*  346:     */             try
/*  347:     */             {
/*  348: 450 */               CostBenefitAnalysis.AnalysisPanel.this.m_costBenefitPanel.setMasterPlot(CostBenefitAnalysis.AnalysisPanel.this.m_costBenefit);
/*  349: 451 */               CostBenefitAnalysis.AnalysisPanel.this.m_costBenefitPanel.validate();
/*  350: 452 */               CostBenefitAnalysis.AnalysisPanel.this.m_costBenefitPanel.repaint();
/*  351:     */             }
/*  352:     */             catch (Exception ex)
/*  353:     */             {
/*  354: 454 */               ex.printStackTrace();
/*  355:     */             }
/*  356: 456 */             CostBenefitAnalysis.AnalysisPanel.this.updateCostBenefit();
/*  357:     */           }
/*  358:     */         }
/*  359: 460 */       };
/*  360: 461 */       this.m_cost_aa.addFocusListener(fl);
/*  361: 462 */       this.m_cost_aa.addActionListener(al);
/*  362: 463 */       this.m_cost_ab.addFocusListener(fl);
/*  363: 464 */       this.m_cost_ab.addActionListener(al);
/*  364: 465 */       this.m_cost_ba.addFocusListener(fl);
/*  365: 466 */       this.m_cost_ba.addActionListener(al);
/*  366: 467 */       this.m_cost_bb.addFocusListener(fl);
/*  367: 468 */       this.m_cost_bb.addActionListener(al);
/*  368:     */       
/*  369: 470 */       this.m_totalPopField.addFocusListener(fl);
/*  370: 471 */       this.m_totalPopField.addActionListener(al);
/*  371:     */       
/*  372: 473 */       JPanel cbHolder = new JPanel();
/*  373: 474 */       cbHolder.setLayout(new BorderLayout());
/*  374: 475 */       JPanel tempP = new JPanel();
/*  375: 476 */       tempP.setLayout(new GridLayout(3, 2));
/*  376: 477 */       tempP.add(this.m_costBenefitL);
/*  377: 478 */       tempP.add(this.m_costBenefitV);
/*  378: 479 */       tempP.add(new JLabel("Random: ", 4));
/*  379: 480 */       tempP.add(this.m_randomV);
/*  380: 481 */       tempP.add(new JLabel("Gain: ", 4));
/*  381: 482 */       tempP.add(this.m_gainV);
/*  382: 483 */       cbHolder.add(tempP, "North");
/*  383: 484 */       JPanel butHolder = new JPanel();
/*  384: 485 */       butHolder.setLayout(new GridLayout(2, 1));
/*  385: 486 */       butHolder.add(this.m_maximizeCB);
/*  386: 487 */       butHolder.add(this.m_minimizeCB);
/*  387: 488 */       this.m_maximizeCB.addActionListener(new ActionListener()
/*  388:     */       {
/*  389:     */         public void actionPerformed(ActionEvent e)
/*  390:     */         {
/*  391: 491 */           CostBenefitAnalysis.AnalysisPanel.this.findMaxMinCB(true);
/*  392:     */         }
/*  393: 494 */       });
/*  394: 495 */       this.m_minimizeCB.addActionListener(new ActionListener()
/*  395:     */       {
/*  396:     */         public void actionPerformed(ActionEvent e)
/*  397:     */         {
/*  398: 498 */           CostBenefitAnalysis.AnalysisPanel.this.findMaxMinCB(false);
/*  399:     */         }
/*  400: 501 */       });
/*  401: 502 */       cbHolder.add(butHolder, "South");
/*  402: 503 */       costPanel.add(cbHolder, "East");
/*  403:     */       
/*  404: 505 */       JPanel popCBR = new JPanel();
/*  405: 506 */       popCBR.setLayout(new GridLayout(1, 2));
/*  406: 507 */       JPanel popHolder = new JPanel();
/*  407: 508 */       popHolder.setLayout(new FlowLayout(0));
/*  408: 509 */       popHolder.add(new JLabel("Total Population: "));
/*  409: 510 */       popHolder.add(this.m_totalPopField);
/*  410:     */       
/*  411: 512 */       JPanel radioHolder2 = new JPanel();
/*  412: 513 */       radioHolder2.setLayout(new FlowLayout(2));
/*  413: 514 */       radioHolder2.add(this.m_costR);
/*  414: 515 */       radioHolder2.add(this.m_benefitR);
/*  415: 516 */       popCBR.add(popHolder);
/*  416: 517 */       popCBR.add(radioHolder2);
/*  417:     */       
/*  418: 519 */       costPanel.add(popCBR, "South");
/*  419:     */       
/*  420: 521 */       matrixHolder.add(costPanel);
/*  421:     */       
/*  422: 523 */       lowerPanel.add(matrixHolder, "South");
/*  423:     */       
/*  424:     */ 
/*  425:     */ 
/*  426:     */ 
/*  427:     */ 
/*  428:     */ 
/*  429:     */ 
/*  430:     */ 
/*  431:     */ 
/*  432:     */ 
/*  433:     */ 
/*  434: 535 */       add(lowerPanel, "South");
/*  435:     */     }
/*  436:     */     
/*  437:     */     private void findMaxMinCB(boolean max)
/*  438:     */     {
/*  439: 540 */       double maxMin = max ? (-1.0D / 0.0D) : (1.0D / 0.0D);
/*  440:     */       
/*  441:     */ 
/*  442: 543 */       Instances cBCurve = this.m_costBenefit.getPlotInstances();
/*  443: 544 */       int maxMinIndex = 0;
/*  444: 546 */       for (int i = 0; i < cBCurve.numInstances(); i++)
/*  445:     */       {
/*  446: 547 */         Instance current = cBCurve.instance(i);
/*  447: 548 */         if (max)
/*  448:     */         {
/*  449: 549 */           if (current.value(1) > maxMin)
/*  450:     */           {
/*  451: 550 */             maxMin = current.value(1);
/*  452: 551 */             maxMinIndex = i;
/*  453:     */           }
/*  454:     */         }
/*  455: 554 */         else if (current.value(1) < maxMin)
/*  456:     */         {
/*  457: 555 */           maxMin = current.value(1);
/*  458: 556 */           maxMinIndex = i;
/*  459:     */         }
/*  460:     */       }
/*  461: 562 */       int indexOfSampleSize = this.m_masterPlot.getPlotInstances().attribute("Sample Size").index();
/*  462:     */       
/*  463: 564 */       int indexOfPercOfTarget = this.m_masterPlot.getPlotInstances().attribute("Recall").index();
/*  464:     */       
/*  465: 566 */       int indexOfThreshold = this.m_masterPlot.getPlotInstances().attribute("Threshold").index();
/*  466:     */       int indexOfMetric;
/*  467:     */       int indexOfMetric;
/*  468: 570 */       if (this.m_percPop.isSelected())
/*  469:     */       {
/*  470: 571 */         indexOfMetric = indexOfSampleSize;
/*  471:     */       }
/*  472:     */       else
/*  473:     */       {
/*  474:     */         int indexOfMetric;
/*  475: 572 */         if (this.m_percOfTarget.isSelected()) {
/*  476: 573 */           indexOfMetric = indexOfPercOfTarget;
/*  477:     */         } else {
/*  478: 575 */           indexOfMetric = indexOfThreshold;
/*  479:     */         }
/*  480:     */       }
/*  481: 578 */       double valueOfMetric = this.m_masterPlot.getPlotInstances().instance(maxMinIndex).value(indexOfMetric);
/*  482:     */       
/*  483: 580 */       valueOfMetric *= 100.0D;
/*  484:     */       
/*  485:     */ 
/*  486: 583 */       this.m_thresholdSlider.setValue((int)valueOfMetric);
/*  487:     */       
/*  488:     */ 
/*  489:     */ 
/*  490: 587 */       updateInfoGivenIndex(maxMinIndex);
/*  491:     */     }
/*  492:     */     
/*  493:     */     private void updateCostBenefit()
/*  494:     */     {
/*  495: 591 */       double value = this.m_thresholdSlider.getValue() / 100.0D;
/*  496: 592 */       Instances plotInstances = this.m_masterPlot.getPlotInstances();
/*  497: 593 */       int indexOfSampleSize = this.m_masterPlot.getPlotInstances().attribute("Sample Size").index();
/*  498:     */       
/*  499: 595 */       int indexOfPercOfTarget = this.m_masterPlot.getPlotInstances().attribute("Recall").index();
/*  500:     */       
/*  501: 597 */       int indexOfThreshold = this.m_masterPlot.getPlotInstances().attribute("Threshold").index();
/*  502:     */       int indexOfMetric;
/*  503:     */       int indexOfMetric;
/*  504: 601 */       if (this.m_percPop.isSelected())
/*  505:     */       {
/*  506: 602 */         indexOfMetric = indexOfSampleSize;
/*  507:     */       }
/*  508:     */       else
/*  509:     */       {
/*  510:     */         int indexOfMetric;
/*  511: 603 */         if (this.m_percOfTarget.isSelected()) {
/*  512: 604 */           indexOfMetric = indexOfPercOfTarget;
/*  513:     */         } else {
/*  514: 606 */           indexOfMetric = indexOfThreshold;
/*  515:     */         }
/*  516:     */       }
/*  517: 609 */       int index = findIndexForValue(value, plotInstances, indexOfMetric);
/*  518: 610 */       updateCBRandomGainInfo(index);
/*  519:     */     }
/*  520:     */     
/*  521:     */     private void updateCBRandomGainInfo(int index)
/*  522:     */     {
/*  523: 614 */       double requestedPopSize = this.m_originalPopSize;
/*  524:     */       try
/*  525:     */       {
/*  526: 616 */         requestedPopSize = Double.parseDouble(this.m_totalPopField.getText());
/*  527:     */       }
/*  528:     */       catch (NumberFormatException e) {}
/*  529: 619 */       double scaleFactor = requestedPopSize / this.m_originalPopSize;
/*  530:     */       
/*  531: 621 */       double CB = this.m_costBenefit.getPlotInstances().instance(index).value(1);
/*  532: 622 */       this.m_costBenefitV.setText(Utils.doubleToString(CB, 2));
/*  533:     */       
/*  534: 624 */       double totalRandomCB = 0.0D;
/*  535: 625 */       Instance first = this.m_masterPlot.getPlotInstances().instance(0);
/*  536: 626 */       double totalPos = first.value(this.m_masterPlot.getPlotInstances().attribute("True Positives").index()) * scaleFactor;
/*  537:     */       
/*  538:     */ 
/*  539: 629 */       double totalNeg = first.value(this.m_masterPlot.getPlotInstances().attribute("False Positives")) * scaleFactor;
/*  540:     */       
/*  541:     */ 
/*  542:     */ 
/*  543: 633 */       double posInSample = totalPos * (Double.parseDouble(this.m_percPopLab.getText()) / 100.0D);
/*  544:     */       
/*  545: 635 */       double negInSample = totalNeg * (Double.parseDouble(this.m_percPopLab.getText()) / 100.0D);
/*  546:     */       
/*  547: 637 */       double posOutSample = totalPos - posInSample;
/*  548: 638 */       double negOutSample = totalNeg - negInSample;
/*  549:     */       
/*  550: 640 */       double tpCost = 0.0D;
/*  551:     */       try
/*  552:     */       {
/*  553: 642 */         tpCost = Double.parseDouble(this.m_cost_aa.getText());
/*  554:     */       }
/*  555:     */       catch (NumberFormatException n) {}
/*  556: 645 */       double fpCost = 0.0D;
/*  557:     */       try
/*  558:     */       {
/*  559: 647 */         fpCost = Double.parseDouble(this.m_cost_ba.getText());
/*  560:     */       }
/*  561:     */       catch (NumberFormatException n) {}
/*  562: 650 */       double tnCost = 0.0D;
/*  563:     */       try
/*  564:     */       {
/*  565: 652 */         tnCost = Double.parseDouble(this.m_cost_bb.getText());
/*  566:     */       }
/*  567:     */       catch (NumberFormatException n) {}
/*  568: 655 */       double fnCost = 0.0D;
/*  569:     */       try
/*  570:     */       {
/*  571: 657 */         fnCost = Double.parseDouble(this.m_cost_ab.getText());
/*  572:     */       }
/*  573:     */       catch (NumberFormatException n) {}
/*  574: 661 */       totalRandomCB += posInSample * tpCost;
/*  575: 662 */       totalRandomCB += negInSample * fpCost;
/*  576: 663 */       totalRandomCB += posOutSample * fnCost;
/*  577: 664 */       totalRandomCB += negOutSample * tnCost;
/*  578:     */       
/*  579: 666 */       this.m_randomV.setText(Utils.doubleToString(totalRandomCB, 2));
/*  580: 667 */       double gain = this.m_costR.isSelected() ? totalRandomCB - CB : CB - totalRandomCB;
/*  581:     */       
/*  582: 669 */       this.m_gainV.setText(Utils.doubleToString(gain, 2));
/*  583:     */       
/*  584:     */ 
/*  585: 672 */       Instance currentInst = this.m_masterPlot.getPlotInstances().instance(index);
/*  586: 673 */       double tp = currentInst.value(this.m_masterPlot.getPlotInstances().attribute("True Positives").index());
/*  587:     */       
/*  588: 675 */       double tn = currentInst.value(this.m_masterPlot.getPlotInstances().attribute("True Negatives").index());
/*  589:     */       
/*  590: 677 */       this.m_classificationAccV.setText(Utils.doubleToString((tp + tn) / (totalPos + totalNeg) * 100.0D, 4) + "%");
/*  591:     */     }
/*  592:     */     
/*  593:     */     private void updateInfoGivenIndex(int index)
/*  594:     */     {
/*  595: 683 */       Instances plotInstances = this.m_masterPlot.getPlotInstances();
/*  596: 684 */       int indexOfSampleSize = this.m_masterPlot.getPlotInstances().attribute("Sample Size").index();
/*  597:     */       
/*  598: 686 */       int indexOfPercOfTarget = this.m_masterPlot.getPlotInstances().attribute("Recall").index();
/*  599:     */       
/*  600: 688 */       int indexOfThreshold = this.m_masterPlot.getPlotInstances().attribute("Threshold").index();
/*  601:     */       
/*  602:     */ 
/*  603:     */ 
/*  604: 692 */       this.m_percPopLab.setText(Utils.doubleToString(100.0D * plotInstances.instance(index).value(indexOfSampleSize), 4));
/*  605:     */       
/*  606: 694 */       this.m_percOfTargetLab.setText(Utils.doubleToString(100.0D * plotInstances.instance(index).value(indexOfPercOfTarget), 4));
/*  607:     */       
/*  608: 696 */       this.m_thresholdLab.setText(Utils.doubleToString(plotInstances.instance(index).value(indexOfThreshold), 4));
/*  609: 707 */       if (this.m_previousShapeIndex >= 0) {
/*  610: 708 */         this.m_shapeSizes[this.m_previousShapeIndex] = 1;
/*  611:     */       }
/*  612: 711 */       this.m_shapeSizes[index] = 10;
/*  613: 712 */       this.m_previousShapeIndex = index;
/*  614:     */       
/*  615:     */ 
/*  616:     */ 
/*  617: 716 */       int tp = plotInstances.attribute("True Positives").index();
/*  618: 717 */       int fp = plotInstances.attribute("False Positives").index();
/*  619: 718 */       int tn = plotInstances.attribute("True Negatives").index();
/*  620: 719 */       int fn = plotInstances.attribute("False Negatives").index();
/*  621: 720 */       Instance temp = plotInstances.instance(index);
/*  622: 721 */       double totalInstances = temp.value(tp) + temp.value(fp) + temp.value(tn) + temp.value(fn);
/*  623:     */       
/*  624:     */ 
/*  625: 724 */       double requestedPopSize = totalInstances;
/*  626:     */       try
/*  627:     */       {
/*  628: 726 */         requestedPopSize = Double.parseDouble(this.m_totalPopField.getText());
/*  629:     */       }
/*  630:     */       catch (NumberFormatException e) {}
/*  631: 730 */       this.m_conf_aa.setCellValue(temp.value(tp), totalInstances, requestedPopSize / totalInstances, 2);
/*  632:     */       
/*  633: 732 */       this.m_conf_ab.setCellValue(temp.value(fn), totalInstances, requestedPopSize / totalInstances, 2);
/*  634:     */       
/*  635: 734 */       this.m_conf_ba.setCellValue(temp.value(fp), totalInstances, requestedPopSize / totalInstances, 2);
/*  636:     */       
/*  637: 736 */       this.m_conf_bb.setCellValue(temp.value(tn), totalInstances, requestedPopSize / totalInstances, 2);
/*  638:     */       
/*  639:     */ 
/*  640: 739 */       updateCBRandomGainInfo(index);
/*  641:     */       
/*  642: 741 */       repaint();
/*  643:     */     }
/*  644:     */     
/*  645:     */     private void updateInfoForSliderValue(double value)
/*  646:     */     {
/*  647: 745 */       int indexOfSampleSize = this.m_masterPlot.getPlotInstances().attribute("Sample Size").index();
/*  648:     */       
/*  649: 747 */       int indexOfPercOfTarget = this.m_masterPlot.getPlotInstances().attribute("Recall").index();
/*  650:     */       
/*  651: 749 */       int indexOfThreshold = this.m_masterPlot.getPlotInstances().attribute("Threshold").index();
/*  652:     */       int indexOfMetric;
/*  653:     */       int indexOfMetric;
/*  654: 753 */       if (this.m_percPop.isSelected())
/*  655:     */       {
/*  656: 754 */         indexOfMetric = indexOfSampleSize;
/*  657:     */       }
/*  658:     */       else
/*  659:     */       {
/*  660:     */         int indexOfMetric;
/*  661: 755 */         if (this.m_percOfTarget.isSelected()) {
/*  662: 756 */           indexOfMetric = indexOfPercOfTarget;
/*  663:     */         } else {
/*  664: 758 */           indexOfMetric = indexOfThreshold;
/*  665:     */         }
/*  666:     */       }
/*  667: 761 */       Instances plotInstances = this.m_masterPlot.getPlotInstances();
/*  668: 762 */       int index = findIndexForValue(value, plotInstances, indexOfMetric);
/*  669: 763 */       updateInfoGivenIndex(index);
/*  670:     */     }
/*  671:     */     
/*  672:     */     private int findIndexForValue(double value, Instances plotInstances, int indexOfMetric)
/*  673:     */     {
/*  674: 771 */       int index = -1;
/*  675: 772 */       int lower = 0;
/*  676: 773 */       int upper = plotInstances.numInstances() - 1;
/*  677: 774 */       int mid = (upper - lower) / 2;
/*  678: 775 */       boolean done = false;
/*  679: 776 */       while (!done)
/*  680:     */       {
/*  681: 777 */         if (upper - lower <= 1)
/*  682:     */         {
/*  683: 780 */           double comp1 = plotInstances.instance(upper).value(indexOfMetric);
/*  684: 781 */           double comp2 = plotInstances.instance(lower).value(indexOfMetric);
/*  685: 782 */           if (Math.abs(comp1 - value) < Math.abs(comp2 - value))
/*  686:     */           {
/*  687: 783 */             index = upper; break;
/*  688:     */           }
/*  689: 785 */           index = lower;
/*  690:     */           
/*  691:     */ 
/*  692: 788 */           break;
/*  693:     */         }
/*  694: 790 */         double comparisonVal = plotInstances.instance(mid).value(indexOfMetric);
/*  695: 791 */         if (value > comparisonVal)
/*  696:     */         {
/*  697: 792 */           if (this.m_threshold.isSelected())
/*  698:     */           {
/*  699: 793 */             lower = mid;
/*  700: 794 */             mid += (upper - lower) / 2;
/*  701:     */           }
/*  702:     */           else
/*  703:     */           {
/*  704: 796 */             upper = mid;
/*  705: 797 */             mid -= (upper - lower) / 2;
/*  706:     */           }
/*  707:     */         }
/*  708: 799 */         else if (value < comparisonVal)
/*  709:     */         {
/*  710: 800 */           if (this.m_threshold.isSelected())
/*  711:     */           {
/*  712: 801 */             upper = mid;
/*  713: 802 */             mid -= (upper - lower) / 2;
/*  714:     */           }
/*  715:     */           else
/*  716:     */           {
/*  717: 804 */             lower = mid;
/*  718: 805 */             mid += (upper - lower) / 2;
/*  719:     */           }
/*  720:     */         }
/*  721:     */         else
/*  722:     */         {
/*  723: 808 */           index = mid;
/*  724: 809 */           done = true;
/*  725:     */         }
/*  726:     */       }
/*  727: 814 */       if (!this.m_threshold.isSelected()) {
/*  728: 815 */         while ((index + 1 < plotInstances.numInstances()) && 
/*  729: 816 */           (plotInstances.instance(index + 1).value(indexOfMetric) == plotInstances.instance(index).value(indexOfMetric))) {
/*  730: 818 */           index++;
/*  731:     */         }
/*  732:     */       }
/*  733: 824 */       while ((index - 1 >= 0) && 
/*  734: 825 */         (plotInstances.instance(index - 1).value(indexOfMetric) == plotInstances.instance(index).value(indexOfMetric))) {
/*  735: 827 */         index--;
/*  736:     */       }
/*  737: 833 */       return index;
/*  738:     */     }
/*  739:     */     
/*  740:     */     public synchronized void setDataSet(PlotData2D data, Attribute classAtt)
/*  741:     */       throws Exception
/*  742:     */     {
/*  743: 847 */       this.m_masterPlot = new PlotData2D(data.getPlotInstances());
/*  744: 848 */       boolean[] connectPoints = new boolean[this.m_masterPlot.getPlotInstances().numInstances()];
/*  745: 850 */       for (int i = 1; i < connectPoints.length; i++) {
/*  746: 851 */         connectPoints[i] = true;
/*  747:     */       }
/*  748: 853 */       this.m_masterPlot.setConnectPoints(connectPoints);
/*  749:     */       
/*  750: 855 */       this.m_masterPlot.m_alwaysDisplayPointsOfThisSize = 10;
/*  751: 856 */       setClassForConfusionMatrix(classAtt);
/*  752: 857 */       this.m_performancePanel.setMasterPlot(this.m_masterPlot);
/*  753: 858 */       this.m_performancePanel.validate();
/*  754: 859 */       this.m_performancePanel.repaint();
/*  755:     */       
/*  756: 861 */       this.m_shapeSizes = new int[this.m_masterPlot.getPlotInstances().numInstances()];
/*  757: 862 */       for (int i = 0; i < this.m_shapeSizes.length; i++) {
/*  758: 863 */         this.m_shapeSizes[i] = 1;
/*  759:     */       }
/*  760: 865 */       this.m_masterPlot.setShapeSize(this.m_shapeSizes);
/*  761: 866 */       constructCostBenefitData();
/*  762: 867 */       this.m_costBenefitPanel.setMasterPlot(this.m_costBenefit);
/*  763: 868 */       this.m_costBenefitPanel.validate();
/*  764: 869 */       this.m_costBenefitPanel.repaint();
/*  765:     */       
/*  766: 871 */       this.m_totalPopPrevious = 0;
/*  767: 872 */       this.m_fpPrevious = 0.0D;
/*  768: 873 */       this.m_tpPrevious = 0.0D;
/*  769: 874 */       this.m_tnPrevious = 0.0D;
/*  770: 875 */       this.m_fnPrevious = 0.0D;
/*  771: 876 */       this.m_previousShapeIndex = -1;
/*  772:     */       
/*  773:     */ 
/*  774: 879 */       Instance first = this.m_masterPlot.getPlotInstances().instance(0);
/*  775: 880 */       double totalPos = first.value(this.m_masterPlot.getPlotInstances().attribute("True Positives").index());
/*  776:     */       
/*  777: 882 */       double totalNeg = first.value(this.m_masterPlot.getPlotInstances().attribute("False Positives"));
/*  778:     */       
/*  779: 884 */       this.m_originalPopSize = ((int)(totalPos + totalNeg));
/*  780: 885 */       this.m_totalPopField.setText("" + this.m_originalPopSize);
/*  781:     */       
/*  782: 887 */       this.m_performancePanel.setYIndex(5);
/*  783: 888 */       this.m_performancePanel.setXIndex(10);
/*  784: 889 */       this.m_costBenefitPanel.setXIndex(0);
/*  785: 890 */       this.m_costBenefitPanel.setYIndex(1);
/*  786:     */       
/*  787: 892 */       updateInfoForSliderValue(this.m_thresholdSlider.getValue() / 100.0D);
/*  788:     */     }
/*  789:     */     
/*  790:     */     private void setClassForConfusionMatrix(Attribute classAtt)
/*  791:     */     {
/*  792: 896 */       this.m_classAttribute = classAtt;
/*  793: 897 */       this.m_conf_actualA.setText(" Actual (a): " + classAtt.value(0));
/*  794: 898 */       this.m_conf_actualA.setToolTipText(classAtt.value(0));
/*  795: 899 */       String negClasses = "";
/*  796: 900 */       for (int i = 1; i < classAtt.numValues(); i++)
/*  797:     */       {
/*  798: 901 */         negClasses = negClasses + classAtt.value(i);
/*  799: 902 */         if (i < classAtt.numValues() - 1) {
/*  800: 903 */           negClasses = negClasses + ",";
/*  801:     */         }
/*  802:     */       }
/*  803: 906 */       this.m_conf_actualB.setText(" Actual (b): " + negClasses);
/*  804: 907 */       this.m_conf_actualB.setToolTipText(negClasses);
/*  805:     */     }
/*  806:     */     
/*  807:     */     private boolean constructCostBenefitData()
/*  808:     */     {
/*  809: 911 */       double tpCost = 0.0D;
/*  810:     */       try
/*  811:     */       {
/*  812: 913 */         tpCost = Double.parseDouble(this.m_cost_aa.getText());
/*  813:     */       }
/*  814:     */       catch (NumberFormatException n) {}
/*  815: 916 */       double fpCost = 0.0D;
/*  816:     */       try
/*  817:     */       {
/*  818: 918 */         fpCost = Double.parseDouble(this.m_cost_ba.getText());
/*  819:     */       }
/*  820:     */       catch (NumberFormatException n) {}
/*  821: 921 */       double tnCost = 0.0D;
/*  822:     */       try
/*  823:     */       {
/*  824: 923 */         tnCost = Double.parseDouble(this.m_cost_bb.getText());
/*  825:     */       }
/*  826:     */       catch (NumberFormatException n) {}
/*  827: 926 */       double fnCost = 0.0D;
/*  828:     */       try
/*  829:     */       {
/*  830: 928 */         fnCost = Double.parseDouble(this.m_cost_ab.getText());
/*  831:     */       }
/*  832:     */       catch (NumberFormatException n) {}
/*  833: 932 */       double requestedPopSize = this.m_originalPopSize;
/*  834:     */       try
/*  835:     */       {
/*  836: 934 */         requestedPopSize = Double.parseDouble(this.m_totalPopField.getText());
/*  837:     */       }
/*  838:     */       catch (NumberFormatException e) {}
/*  839: 938 */       double scaleFactor = 1.0D;
/*  840: 939 */       if (this.m_originalPopSize != 0) {
/*  841: 940 */         scaleFactor = requestedPopSize / this.m_originalPopSize;
/*  842:     */       }
/*  843: 943 */       if ((tpCost == this.m_tpPrevious) && (fpCost == this.m_fpPrevious) && (tnCost == this.m_tnPrevious) && (fnCost == this.m_fnPrevious) && (requestedPopSize == this.m_totalPopPrevious)) {
/*  844: 946 */         return false;
/*  845:     */       }
/*  846: 950 */       ArrayList<Attribute> fv = new ArrayList();
/*  847: 951 */       fv.add(new Attribute("Sample Size"));
/*  848: 952 */       fv.add(new Attribute("Cost/Benefit"));
/*  849: 953 */       fv.add(new Attribute("Threshold"));
/*  850: 954 */       Instances costBenefitI = new Instances("Cost/Benefit Curve", fv, 100);
/*  851:     */       
/*  852:     */ 
/*  853: 957 */       Instances performanceI = this.m_masterPlot.getPlotInstances();
/*  854: 959 */       for (int i = 0; i < performanceI.numInstances(); i++)
/*  855:     */       {
/*  856: 960 */         Instance current = performanceI.instance(i);
/*  857:     */         
/*  858: 962 */         double[] vals = new double[3];
/*  859: 963 */         vals[0] = current.value(10);
/*  860: 964 */         vals[1] = ((current.value(0) * tpCost + current.value(1) * fnCost + current.value(2) * fpCost + current.value(3) * tnCost) * scaleFactor);
/*  861:     */         
/*  862:     */ 
/*  863: 967 */         vals[2] = current.value(current.numAttributes() - 1);
/*  864: 968 */         Instance newInst = new DenseInstance(1.0D, vals);
/*  865: 969 */         costBenefitI.add(newInst);
/*  866:     */       }
/*  867: 972 */       costBenefitI.compactify();
/*  868:     */       
/*  869:     */ 
/*  870: 975 */       this.m_costBenefit = new PlotData2D(costBenefitI);
/*  871: 976 */       this.m_costBenefit.m_alwaysDisplayPointsOfThisSize = 10;
/*  872: 977 */       this.m_costBenefit.setPlotName("Cost/benefit curve");
/*  873: 978 */       boolean[] connectPoints = new boolean[costBenefitI.numInstances()];
/*  874: 980 */       for (int i = 0; i < connectPoints.length; i++) {
/*  875: 981 */         connectPoints[i] = true;
/*  876:     */       }
/*  877:     */       try
/*  878:     */       {
/*  879: 984 */         this.m_costBenefit.setConnectPoints(connectPoints);
/*  880: 985 */         this.m_costBenefit.setShapeSize(this.m_shapeSizes);
/*  881:     */       }
/*  882:     */       catch (Exception ex) {}
/*  883: 990 */       this.m_tpPrevious = tpCost;
/*  884: 991 */       this.m_fpPrevious = fpCost;
/*  885: 992 */       this.m_tnPrevious = tnCost;
/*  886: 993 */       this.m_fnPrevious = fnCost;
/*  887:     */       
/*  888: 995 */       return true;
/*  889:     */     }
/*  890:     */   }
/*  891:     */   
/*  892:     */   public CostBenefitAnalysis()
/*  893:     */   {
/*  894:1004 */     if (!GraphicsEnvironment.isHeadless()) {
/*  895:1005 */       appearanceFinal();
/*  896:     */     } else {
/*  897:1007 */       this.m_headlessEvents = new ArrayList();
/*  898:     */     }
/*  899:     */   }
/*  900:     */   
/*  901:     */   public String globalInfo()
/*  902:     */   {
/*  903:1017 */     return "Visualize performance charts (such as ROC).";
/*  904:     */   }
/*  905:     */   
/*  906:     */   public void acceptDataSet(ThresholdDataEvent e)
/*  907:     */   {
/*  908:1027 */     if (!GraphicsEnvironment.isHeadless())
/*  909:     */     {
/*  910:     */       try
/*  911:     */       {
/*  912:1029 */         setCurveData(e.getDataSet(), e.getClassAttribute());
/*  913:     */       }
/*  914:     */       catch (Exception ex)
/*  915:     */       {
/*  916:1031 */         System.err.println("[CostBenefitAnalysis] Problem setting up visualization.");
/*  917:     */         
/*  918:1033 */         ex.printStackTrace();
/*  919:     */       }
/*  920:     */     }
/*  921:     */     else
/*  922:     */     {
/*  923:1036 */       this.m_headlessEvents = new ArrayList();
/*  924:1037 */       this.m_headlessEvents.add(e);
/*  925:     */     }
/*  926:     */   }
/*  927:     */   
/*  928:     */   public void setCurveData(PlotData2D curveData, Attribute origClassAtt)
/*  929:     */     throws Exception
/*  930:     */   {
/*  931:1052 */     if (this.m_analysisPanel == null) {
/*  932:1053 */       this.m_analysisPanel = new AnalysisPanel();
/*  933:     */     }
/*  934:1055 */     this.m_analysisPanel.setDataSet(curveData, origClassAtt);
/*  935:     */   }
/*  936:     */   
/*  937:     */   public BeanVisual getVisual()
/*  938:     */   {
/*  939:1060 */     return this.m_visual;
/*  940:     */   }
/*  941:     */   
/*  942:     */   public void setVisual(BeanVisual newVisual)
/*  943:     */   {
/*  944:1065 */     this.m_visual = newVisual;
/*  945:     */   }
/*  946:     */   
/*  947:     */   public void useDefaultVisual()
/*  948:     */   {
/*  949:1070 */     this.m_visual.loadIcons("weka/gui/beans/icons/DefaultDataVisualizer.gif", "weka/gui/beans/icons/DefaultDataVisualizer_animated.gif");
/*  950:     */   }
/*  951:     */   
/*  952:     */   public Enumeration<String> enumerateRequests()
/*  953:     */   {
/*  954:1076 */     Vector<String> newVector = new Vector(0);
/*  955:1077 */     if ((this.m_analysisPanel != null) && 
/*  956:1078 */       (this.m_analysisPanel.m_masterPlot != null)) {
/*  957:1079 */       newVector.addElement("Show analysis");
/*  958:     */     }
/*  959:1082 */     return newVector.elements();
/*  960:     */   }
/*  961:     */   
/*  962:     */   public void performRequest(String request)
/*  963:     */   {
/*  964:1087 */     if (request.compareTo("Show analysis") == 0) {
/*  965:     */       try
/*  966:     */       {
/*  967:1090 */         if (!this.m_framePoppedUp)
/*  968:     */         {
/*  969:1091 */           this.m_framePoppedUp = true;
/*  970:     */           
/*  971:1093 */           final JFrame jf = new JFrame("Cost/Benefit Analysis");
/*  972:     */           
/*  973:1095 */           jf.setSize(1000, 600);
/*  974:1096 */           jf.getContentPane().setLayout(new BorderLayout());
/*  975:1097 */           jf.getContentPane().add(this.m_analysisPanel, "Center");
/*  976:1098 */           jf.addWindowListener(new WindowAdapter()
/*  977:     */           {
/*  978:     */             public void windowClosing(WindowEvent e)
/*  979:     */             {
/*  980:1101 */               jf.dispose();
/*  981:1102 */               CostBenefitAnalysis.this.m_framePoppedUp = false;
/*  982:     */             }
/*  983:1104 */           });
/*  984:1105 */           jf.setVisible(true);
/*  985:1106 */           this.m_popupFrame = jf;
/*  986:     */         }
/*  987:     */         else
/*  988:     */         {
/*  989:1108 */           this.m_popupFrame.toFront();
/*  990:     */         }
/*  991:     */       }
/*  992:     */       catch (Exception ex)
/*  993:     */       {
/*  994:1111 */         ex.printStackTrace();
/*  995:1112 */         this.m_framePoppedUp = false;
/*  996:     */       }
/*  997:     */     } else {
/*  998:1115 */       throw new IllegalArgumentException(request + " not supported (Cost/Benefit Analysis");
/*  999:     */     }
/* 1000:     */   }
/* 1001:     */   
/* 1002:     */   public void addVetoableChangeListener(String name, VetoableChangeListener vcl)
/* 1003:     */   {
/* 1004:1122 */     this.m_bcSupport.addVetoableChangeListener(name, vcl);
/* 1005:     */   }
/* 1006:     */   
/* 1007:     */   public BeanContext getBeanContext()
/* 1008:     */   {
/* 1009:1127 */     return this.m_beanContext;
/* 1010:     */   }
/* 1011:     */   
/* 1012:     */   public void removeVetoableChangeListener(String name, VetoableChangeListener vcl)
/* 1013:     */   {
/* 1014:1133 */     this.m_bcSupport.removeVetoableChangeListener(name, vcl);
/* 1015:     */   }
/* 1016:     */   
/* 1017:     */   protected void appearanceFinal()
/* 1018:     */   {
/* 1019:1137 */     removeAll();
/* 1020:1138 */     setLayout(new BorderLayout());
/* 1021:1139 */     setUpFinal();
/* 1022:     */   }
/* 1023:     */   
/* 1024:     */   protected void setUpFinal()
/* 1025:     */   {
/* 1026:1143 */     if (this.m_analysisPanel == null) {
/* 1027:1144 */       this.m_analysisPanel = new AnalysisPanel();
/* 1028:     */     }
/* 1029:1146 */     add(this.m_analysisPanel, "Center");
/* 1030:     */   }
/* 1031:     */   
/* 1032:     */   protected void appearanceDesign()
/* 1033:     */   {
/* 1034:1150 */     removeAll();
/* 1035:1151 */     useDefaultVisual();
/* 1036:1152 */     setLayout(new BorderLayout());
/* 1037:1153 */     add(this.m_visual, "Center");
/* 1038:     */   }
/* 1039:     */   
/* 1040:     */   public void setBeanContext(BeanContext bc)
/* 1041:     */     throws PropertyVetoException
/* 1042:     */   {
/* 1043:1158 */     this.m_beanContext = bc;
/* 1044:1159 */     this.m_design = this.m_beanContext.isDesignTime();
/* 1045:1160 */     if (this.m_design) {
/* 1046:1161 */       appearanceDesign();
/* 1047:1163 */     } else if (!GraphicsEnvironment.isHeadless()) {
/* 1048:1164 */       appearanceFinal();
/* 1049:     */     }
/* 1050:     */   }
/* 1051:     */   
/* 1052:     */   public boolean connectionAllowed(String eventName)
/* 1053:     */   {
/* 1054:1178 */     return this.m_listenee == null;
/* 1055:     */   }
/* 1056:     */   
/* 1057:     */   public void connectionNotification(String eventName, Object source)
/* 1058:     */   {
/* 1059:1192 */     if (connectionAllowed(eventName)) {
/* 1060:1193 */       this.m_listenee = source;
/* 1061:     */     }
/* 1062:     */   }
/* 1063:     */   
/* 1064:     */   public boolean connectionAllowed(EventSetDescriptor esd)
/* 1065:     */   {
/* 1066:1206 */     return connectionAllowed(esd.getName());
/* 1067:     */   }
/* 1068:     */   
/* 1069:     */   public void disconnectionNotification(String eventName, Object source)
/* 1070:     */   {
/* 1071:1219 */     if (this.m_listenee == source) {
/* 1072:1220 */       this.m_listenee = null;
/* 1073:     */     }
/* 1074:     */   }
/* 1075:     */   
/* 1076:     */   public String getCustomName()
/* 1077:     */   {
/* 1078:1232 */     return this.m_visual.getText();
/* 1079:     */   }
/* 1080:     */   
/* 1081:     */   public boolean isBusy()
/* 1082:     */   {
/* 1083:1243 */     return false;
/* 1084:     */   }
/* 1085:     */   
/* 1086:     */   public void setCustomName(String name)
/* 1087:     */   {
/* 1088:1253 */     this.m_visual.setText(name);
/* 1089:     */   }
/* 1090:     */   
/* 1091:     */   public void setLog(Logger logger) {}
/* 1092:     */   
/* 1093:     */   public void stop() {}
/* 1094:     */   
/* 1095:     */   public static void main(String[] args)
/* 1096:     */   {
/* 1097:     */     try
/* 1098:     */     {
/* 1099:1276 */       Instances train = new Instances(new BufferedReader(new FileReader(args[0])));
/* 1100:     */       
/* 1101:1278 */       train.setClassIndex(train.numAttributes() - 1);
/* 1102:1279 */       ThresholdCurve tc = new ThresholdCurve();
/* 1103:1280 */       EvaluationUtils eu = new EvaluationUtils();
/* 1104:     */       
/* 1105:     */ 
/* 1106:1283 */       Classifier classifier = new NaiveBayes();
/* 1107:1284 */       ArrayList<Prediction> predictions = new ArrayList();
/* 1108:1285 */       eu.setSeed(1);
/* 1109:1286 */       predictions.addAll(eu.getCVPredictions(classifier, train, 10));
/* 1110:1287 */       Instances result = tc.getCurve(predictions, 0);
/* 1111:1288 */       PlotData2D pd = new PlotData2D(result);
/* 1112:1289 */       pd.m_alwaysDisplayPointsOfThisSize = 10;
/* 1113:     */       
/* 1114:1291 */       boolean[] connectPoints = new boolean[result.numInstances()];
/* 1115:1292 */       for (int i = 1; i < connectPoints.length; i++) {
/* 1116:1293 */         connectPoints[i] = true;
/* 1117:     */       }
/* 1118:1295 */       pd.setConnectPoints(connectPoints);
/* 1119:1296 */       JFrame jf = new JFrame("CostBenefitTest");
/* 1120:1297 */       jf.setSize(1000, 600);
/* 1121:     */       
/* 1122:1299 */       jf.getContentPane().setLayout(new BorderLayout());
/* 1123:1300 */       AnalysisPanel analysisPanel = new AnalysisPanel();
/* 1124:     */       
/* 1125:1302 */       jf.getContentPane().add(analysisPanel, "Center");
/* 1126:1303 */       jf.addWindowListener(new WindowAdapter()
/* 1127:     */       {
/* 1128:     */         public void windowClosing(WindowEvent e)
/* 1129:     */         {
/* 1130:1306 */           this.val$jf.dispose();
/* 1131:1307 */           System.exit(0);
/* 1132:     */         }
/* 1133:1310 */       });
/* 1134:1311 */       jf.setVisible(true);
/* 1135:     */       
/* 1136:1313 */       analysisPanel.setDataSet(pd, train.classAttribute());
/* 1137:     */     }
/* 1138:     */     catch (Exception ex)
/* 1139:     */     {
/* 1140:1316 */       ex.printStackTrace();
/* 1141:     */     }
/* 1142:     */   }
/* 1143:     */   
/* 1144:     */   public List<EventObject> retrieveHeadlessEvents()
/* 1145:     */   {
/* 1146:1329 */     return this.m_headlessEvents;
/* 1147:     */   }
/* 1148:     */   
/* 1149:     */   public void processHeadlessEvents(List<EventObject> headless)
/* 1150:     */   {
/* 1151:1341 */     if (!GraphicsEnvironment.isHeadless()) {
/* 1152:1342 */       for (EventObject e : headless) {
/* 1153:1343 */         if ((e instanceof ThresholdDataEvent)) {
/* 1154:1344 */           acceptDataSet((ThresholdDataEvent)e);
/* 1155:     */         }
/* 1156:     */       }
/* 1157:     */     }
/* 1158:     */   }
/* 1159:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.CostBenefitAnalysis
 * JD-Core Version:    0.7.0.1
 */