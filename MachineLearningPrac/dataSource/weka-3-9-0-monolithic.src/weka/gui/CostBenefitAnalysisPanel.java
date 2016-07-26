/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.FlowLayout;
/*   7:    */ import java.awt.Graphics;
/*   8:    */ import java.awt.GridLayout;
/*   9:    */ import java.awt.event.ActionEvent;
/*  10:    */ import java.awt.event.ActionListener;
/*  11:    */ import java.awt.event.FocusEvent;
/*  12:    */ import java.awt.event.FocusListener;
/*  13:    */ import java.util.ArrayList;
/*  14:    */ import javax.swing.BorderFactory;
/*  15:    */ import javax.swing.ButtonGroup;
/*  16:    */ import javax.swing.JButton;
/*  17:    */ import javax.swing.JLabel;
/*  18:    */ import javax.swing.JPanel;
/*  19:    */ import javax.swing.JRadioButton;
/*  20:    */ import javax.swing.JSlider;
/*  21:    */ import javax.swing.JTextField;
/*  22:    */ import javax.swing.event.ChangeEvent;
/*  23:    */ import javax.swing.event.ChangeListener;
/*  24:    */ import weka.core.Attribute;
/*  25:    */ import weka.core.DenseInstance;
/*  26:    */ import weka.core.Instance;
/*  27:    */ import weka.core.Instances;
/*  28:    */ import weka.core.Utils;
/*  29:    */ import weka.gui.visualize.PlotData2D;
/*  30:    */ import weka.gui.visualize.VisualizePanel;
/*  31:    */ 
/*  32:    */ public class CostBenefitAnalysisPanel
/*  33:    */   extends JPanel
/*  34:    */ {
/*  35:    */   private static final long serialVersionUID = 5364871945448769003L;
/*  36: 69 */   protected VisualizePanel m_performancePanel = new VisualizePanel();
/*  37: 72 */   protected VisualizePanel m_costBenefitPanel = new VisualizePanel();
/*  38:    */   protected Attribute m_classAttribute;
/*  39:    */   protected PlotData2D m_masterPlot;
/*  40:    */   protected PlotData2D m_costBenefit;
/*  41:    */   protected int[] m_shapeSizes;
/*  42: 90 */   protected int m_previousShapeIndex = -1;
/*  43: 93 */   protected JSlider m_thresholdSlider = new JSlider(0, 100, 0);
/*  44: 95 */   protected JRadioButton m_percPop = new JRadioButton("% of Population");
/*  45: 96 */   protected JRadioButton m_percOfTarget = new JRadioButton("% of Target (recall)");
/*  46: 98 */   protected JRadioButton m_threshold = new JRadioButton("Score Threshold");
/*  47:100 */   protected JLabel m_percPopLab = new JLabel();
/*  48:101 */   protected JLabel m_percOfTargetLab = new JLabel();
/*  49:102 */   protected JLabel m_thresholdLab = new JLabel();
/*  50:105 */   protected JLabel m_conf_predictedA = new JLabel("Predicted (a)", 4);
/*  51:107 */   protected JLabel m_conf_predictedB = new JLabel("Predicted (b)", 4);
/*  52:109 */   protected JLabel m_conf_actualA = new JLabel(" Actual (a):");
/*  53:110 */   protected JLabel m_conf_actualB = new JLabel(" Actual (b):");
/*  54:111 */   protected ConfusionCell m_conf_aa = new ConfusionCell();
/*  55:112 */   protected ConfusionCell m_conf_ab = new ConfusionCell();
/*  56:113 */   protected ConfusionCell m_conf_ba = new ConfusionCell();
/*  57:114 */   protected ConfusionCell m_conf_bb = new ConfusionCell();
/*  58:117 */   protected JLabel m_cost_predictedA = new JLabel("Predicted (a)", 4);
/*  59:119 */   protected JLabel m_cost_predictedB = new JLabel("Predicted (b)", 4);
/*  60:121 */   protected JLabel m_cost_actualA = new JLabel(" Actual (a)");
/*  61:122 */   protected JLabel m_cost_actualB = new JLabel(" Actual (b)");
/*  62:123 */   protected JTextField m_cost_aa = new JTextField("0.0", 5);
/*  63:124 */   protected JTextField m_cost_ab = new JTextField("1.0", 5);
/*  64:125 */   protected JTextField m_cost_ba = new JTextField("1.0", 5);
/*  65:126 */   protected JTextField m_cost_bb = new JTextField("0.0", 5);
/*  66:127 */   protected JButton m_maximizeCB = new JButton("Maximize Cost/Benefit");
/*  67:128 */   protected JButton m_minimizeCB = new JButton("Minimize Cost/Benefit");
/*  68:129 */   protected JRadioButton m_costR = new JRadioButton("Cost");
/*  69:130 */   protected JRadioButton m_benefitR = new JRadioButton("Benefit");
/*  70:131 */   protected JLabel m_costBenefitL = new JLabel("Cost: ", 4);
/*  71:132 */   protected JLabel m_costBenefitV = new JLabel("0");
/*  72:133 */   protected JLabel m_randomV = new JLabel("0");
/*  73:134 */   protected JLabel m_gainV = new JLabel("0");
/*  74:    */   protected int m_originalPopSize;
/*  75:139 */   protected JTextField m_totalPopField = new JTextField(6);
/*  76:    */   protected int m_totalPopPrevious;
/*  77:143 */   protected JLabel m_classificationAccV = new JLabel("-");
/*  78:    */   protected double m_tpPrevious;
/*  79:    */   protected double m_fpPrevious;
/*  80:    */   protected double m_tnPrevious;
/*  81:    */   protected double m_fnPrevious;
/*  82:    */   
/*  83:    */   protected static class ConfusionCell
/*  84:    */     extends JPanel
/*  85:    */   {
/*  86:    */     private static final long serialVersionUID = 6148640235434494767L;
/*  87:163 */     private final JLabel m_conf_cell = new JLabel("-", 4);
/*  88:164 */     JLabel m_conf_perc = new JLabel("-", 4);
/*  89:    */     private final JPanel m_percentageP;
/*  90:168 */     protected double m_percentage = 0.0D;
/*  91:    */     
/*  92:    */     public ConfusionCell()
/*  93:    */     {
/*  94:172 */       setLayout(new BorderLayout());
/*  95:173 */       setBorder(BorderFactory.createEtchedBorder());
/*  96:    */       
/*  97:175 */       add(this.m_conf_cell, "North");
/*  98:    */       
/*  99:177 */       this.m_percentageP = new JPanel()
/* 100:    */       {
/* 101:    */         public void paintComponent(Graphics gx)
/* 102:    */         {
/* 103:180 */           super.paintComponent(gx);
/* 104:182 */           if (CostBenefitAnalysisPanel.ConfusionCell.this.m_percentage > 0.0D)
/* 105:    */           {
/* 106:183 */             gx.setColor(Color.BLUE);
/* 107:184 */             int height = getHeight();
/* 108:185 */             double width = getWidth();
/* 109:186 */             int barWidth = (int)(CostBenefitAnalysisPanel.ConfusionCell.this.m_percentage * width);
/* 110:187 */             gx.fillRect(0, 0, barWidth, height);
/* 111:    */           }
/* 112:    */         }
/* 113:191 */       };
/* 114:192 */       Dimension d = new Dimension(30, 5);
/* 115:193 */       this.m_percentageP.setMinimumSize(d);
/* 116:194 */       this.m_percentageP.setPreferredSize(d);
/* 117:195 */       JPanel percHolder = new JPanel();
/* 118:196 */       percHolder.setLayout(new BorderLayout());
/* 119:197 */       percHolder.add(this.m_percentageP, "Center");
/* 120:198 */       percHolder.add(this.m_conf_perc, "East");
/* 121:    */       
/* 122:200 */       add(percHolder, "South");
/* 123:    */     }
/* 124:    */     
/* 125:    */     public void setCellValue(double cellValue, double max, double scaleFactor, int precision)
/* 126:    */     {
/* 127:213 */       if (!Utils.isMissingValue(cellValue)) {
/* 128:214 */         this.m_percentage = (cellValue / max);
/* 129:    */       } else {
/* 130:216 */         this.m_percentage = 0.0D;
/* 131:    */       }
/* 132:219 */       this.m_conf_cell.setText(Utils.doubleToString(cellValue * scaleFactor, 0));
/* 133:220 */       this.m_conf_perc.setText(Utils.doubleToString(this.m_percentage * 100.0D, precision) + "%");
/* 134:    */       
/* 135:    */ 
/* 136:    */ 
/* 137:224 */       this.m_percentageP.repaint();
/* 138:    */     }
/* 139:    */   }
/* 140:    */   
/* 141:    */   public CostBenefitAnalysisPanel()
/* 142:    */   {
/* 143:229 */     setLayout(new BorderLayout());
/* 144:230 */     this.m_performancePanel.setShowAttBars(false);
/* 145:231 */     this.m_performancePanel.setShowClassPanel(false);
/* 146:232 */     this.m_costBenefitPanel.setShowAttBars(false);
/* 147:233 */     this.m_costBenefitPanel.setShowClassPanel(false);
/* 148:    */     
/* 149:235 */     Dimension size = new Dimension(500, 400);
/* 150:236 */     this.m_performancePanel.setPreferredSize(size);
/* 151:237 */     this.m_performancePanel.setMinimumSize(size);
/* 152:    */     
/* 153:239 */     size = new Dimension(500, 400);
/* 154:240 */     this.m_costBenefitPanel.setMinimumSize(size);
/* 155:241 */     this.m_costBenefitPanel.setPreferredSize(size);
/* 156:    */     
/* 157:243 */     this.m_thresholdSlider.addChangeListener(new ChangeListener()
/* 158:    */     {
/* 159:    */       public void stateChanged(ChangeEvent e)
/* 160:    */       {
/* 161:246 */         CostBenefitAnalysisPanel.this.updateInfoForSliderValue(CostBenefitAnalysisPanel.this.m_thresholdSlider.getValue() / 100.0D);
/* 162:    */       }
/* 163:249 */     });
/* 164:250 */     JPanel plotHolder = new JPanel();
/* 165:251 */     plotHolder.setLayout(new GridLayout(1, 2));
/* 166:252 */     plotHolder.add(this.m_performancePanel);
/* 167:253 */     plotHolder.add(this.m_costBenefitPanel);
/* 168:254 */     add(plotHolder, "Center");
/* 169:    */     
/* 170:256 */     JPanel lowerPanel = new JPanel();
/* 171:257 */     lowerPanel.setLayout(new BorderLayout());
/* 172:    */     
/* 173:259 */     ButtonGroup bGroup = new ButtonGroup();
/* 174:260 */     bGroup.add(this.m_percPop);
/* 175:261 */     bGroup.add(this.m_percOfTarget);
/* 176:262 */     bGroup.add(this.m_threshold);
/* 177:    */     
/* 178:264 */     ButtonGroup bGroup2 = new ButtonGroup();
/* 179:265 */     bGroup2.add(this.m_costR);
/* 180:266 */     bGroup2.add(this.m_benefitR);
/* 181:267 */     ActionListener rl = new ActionListener()
/* 182:    */     {
/* 183:    */       public void actionPerformed(ActionEvent e)
/* 184:    */       {
/* 185:270 */         if (CostBenefitAnalysisPanel.this.m_costR.isSelected()) {
/* 186:271 */           CostBenefitAnalysisPanel.this.m_costBenefitL.setText("Cost: ");
/* 187:    */         } else {
/* 188:273 */           CostBenefitAnalysisPanel.this.m_costBenefitL.setText("Benefit: ");
/* 189:    */         }
/* 190:276 */         double gain = Double.parseDouble(CostBenefitAnalysisPanel.this.m_gainV.getText());
/* 191:277 */         gain = -gain;
/* 192:278 */         CostBenefitAnalysisPanel.this.m_gainV.setText(Utils.doubleToString(gain, 2));
/* 193:    */       }
/* 194:280 */     };
/* 195:281 */     this.m_costR.addActionListener(rl);
/* 196:282 */     this.m_benefitR.addActionListener(rl);
/* 197:283 */     this.m_costR.setSelected(true);
/* 198:    */     
/* 199:285 */     this.m_percPop.setSelected(true);
/* 200:286 */     JPanel threshPanel = new JPanel();
/* 201:287 */     threshPanel.setLayout(new BorderLayout());
/* 202:288 */     JPanel radioHolder = new JPanel();
/* 203:289 */     radioHolder.setLayout(new FlowLayout());
/* 204:290 */     radioHolder.add(this.m_percPop);
/* 205:291 */     radioHolder.add(this.m_percOfTarget);
/* 206:292 */     radioHolder.add(this.m_threshold);
/* 207:293 */     threshPanel.add(radioHolder, "North");
/* 208:294 */     threshPanel.add(this.m_thresholdSlider, "South");
/* 209:    */     
/* 210:296 */     JPanel threshInfoPanel = new JPanel();
/* 211:297 */     threshInfoPanel.setLayout(new GridLayout(3, 2));
/* 212:298 */     threshInfoPanel.add(new JLabel("% of Population: ", 4));
/* 213:    */     
/* 214:300 */     threshInfoPanel.add(this.m_percPopLab);
/* 215:301 */     threshInfoPanel.add(new JLabel("% of Target: ", 4));
/* 216:302 */     threshInfoPanel.add(this.m_percOfTargetLab);
/* 217:303 */     threshInfoPanel.add(new JLabel("Score Threshold: ", 4));
/* 218:    */     
/* 219:305 */     threshInfoPanel.add(this.m_thresholdLab);
/* 220:    */     
/* 221:307 */     JPanel threshHolder = new JPanel();
/* 222:308 */     threshHolder.setBorder(BorderFactory.createTitledBorder("Threshold"));
/* 223:309 */     threshHolder.setLayout(new BorderLayout());
/* 224:310 */     threshHolder.add(threshPanel, "Center");
/* 225:311 */     threshHolder.add(threshInfoPanel, "East");
/* 226:    */     
/* 227:313 */     lowerPanel.add(threshHolder, "North");
/* 228:    */     
/* 229:    */ 
/* 230:316 */     JPanel matrixHolder = new JPanel();
/* 231:317 */     matrixHolder.setLayout(new GridLayout(1, 2));
/* 232:    */     
/* 233:    */ 
/* 234:320 */     JPanel confusionPanel = new JPanel();
/* 235:321 */     confusionPanel.setLayout(new GridLayout(3, 3));
/* 236:322 */     confusionPanel.add(this.m_conf_predictedA);
/* 237:323 */     confusionPanel.add(this.m_conf_predictedB);
/* 238:324 */     confusionPanel.add(new JLabel());
/* 239:325 */     confusionPanel.add(this.m_conf_aa);
/* 240:326 */     confusionPanel.add(this.m_conf_ab);
/* 241:327 */     confusionPanel.add(this.m_conf_actualA);
/* 242:328 */     confusionPanel.add(this.m_conf_ba);
/* 243:329 */     confusionPanel.add(this.m_conf_bb);
/* 244:330 */     confusionPanel.add(this.m_conf_actualB);
/* 245:331 */     JPanel tempHolderCA = new JPanel();
/* 246:332 */     tempHolderCA.setLayout(new BorderLayout());
/* 247:333 */     tempHolderCA.setBorder(BorderFactory.createTitledBorder("Confusion Matrix"));
/* 248:    */     
/* 249:335 */     tempHolderCA.add(confusionPanel, "Center");
/* 250:    */     
/* 251:337 */     JPanel accHolder = new JPanel();
/* 252:338 */     accHolder.setLayout(new FlowLayout(0));
/* 253:339 */     accHolder.add(new JLabel("Classification Accuracy: "));
/* 254:340 */     accHolder.add(this.m_classificationAccV);
/* 255:341 */     tempHolderCA.add(accHolder, "South");
/* 256:    */     
/* 257:343 */     matrixHolder.add(tempHolderCA);
/* 258:    */     
/* 259:    */ 
/* 260:346 */     JPanel costPanel = new JPanel();
/* 261:347 */     costPanel.setBorder(BorderFactory.createTitledBorder("Cost Matrix"));
/* 262:348 */     costPanel.setLayout(new BorderLayout());
/* 263:    */     
/* 264:350 */     JPanel cmHolder = new JPanel();
/* 265:351 */     cmHolder.setLayout(new GridLayout(3, 3));
/* 266:352 */     cmHolder.add(this.m_cost_predictedA);
/* 267:353 */     cmHolder.add(this.m_cost_predictedB);
/* 268:354 */     cmHolder.add(new JLabel());
/* 269:355 */     cmHolder.add(this.m_cost_aa);
/* 270:356 */     cmHolder.add(this.m_cost_ab);
/* 271:357 */     cmHolder.add(this.m_cost_actualA);
/* 272:358 */     cmHolder.add(this.m_cost_ba);
/* 273:359 */     cmHolder.add(this.m_cost_bb);
/* 274:360 */     cmHolder.add(this.m_cost_actualB);
/* 275:361 */     costPanel.add(cmHolder, "Center");
/* 276:    */     
/* 277:363 */     FocusListener fl = new FocusListener()
/* 278:    */     {
/* 279:    */       public void focusGained(FocusEvent e) {}
/* 280:    */       
/* 281:    */       public void focusLost(FocusEvent e)
/* 282:    */       {
/* 283:371 */         if (CostBenefitAnalysisPanel.this.constructCostBenefitData())
/* 284:    */         {
/* 285:    */           try
/* 286:    */           {
/* 287:373 */             CostBenefitAnalysisPanel.this.m_costBenefitPanel.setMasterPlot(CostBenefitAnalysisPanel.this.m_costBenefit);
/* 288:374 */             CostBenefitAnalysisPanel.this.m_costBenefitPanel.validate();
/* 289:375 */             CostBenefitAnalysisPanel.this.m_costBenefitPanel.repaint();
/* 290:    */           }
/* 291:    */           catch (Exception ex)
/* 292:    */           {
/* 293:377 */             ex.printStackTrace();
/* 294:    */           }
/* 295:379 */           CostBenefitAnalysisPanel.this.updateCostBenefit();
/* 296:    */         }
/* 297:    */       }
/* 298:383 */     };
/* 299:384 */     ActionListener al = new ActionListener()
/* 300:    */     {
/* 301:    */       public void actionPerformed(ActionEvent e)
/* 302:    */       {
/* 303:387 */         if (CostBenefitAnalysisPanel.this.constructCostBenefitData())
/* 304:    */         {
/* 305:    */           try
/* 306:    */           {
/* 307:389 */             CostBenefitAnalysisPanel.this.m_costBenefitPanel.setMasterPlot(CostBenefitAnalysisPanel.this.m_costBenefit);
/* 308:390 */             CostBenefitAnalysisPanel.this.m_costBenefitPanel.validate();
/* 309:391 */             CostBenefitAnalysisPanel.this.m_costBenefitPanel.repaint();
/* 310:    */           }
/* 311:    */           catch (Exception ex)
/* 312:    */           {
/* 313:393 */             ex.printStackTrace();
/* 314:    */           }
/* 315:395 */           CostBenefitAnalysisPanel.this.updateCostBenefit();
/* 316:    */         }
/* 317:    */       }
/* 318:399 */     };
/* 319:400 */     this.m_cost_aa.addFocusListener(fl);
/* 320:401 */     this.m_cost_aa.addActionListener(al);
/* 321:402 */     this.m_cost_ab.addFocusListener(fl);
/* 322:403 */     this.m_cost_ab.addActionListener(al);
/* 323:404 */     this.m_cost_ba.addFocusListener(fl);
/* 324:405 */     this.m_cost_ba.addActionListener(al);
/* 325:406 */     this.m_cost_bb.addFocusListener(fl);
/* 326:407 */     this.m_cost_bb.addActionListener(al);
/* 327:    */     
/* 328:409 */     this.m_totalPopField.addFocusListener(fl);
/* 329:410 */     this.m_totalPopField.addActionListener(al);
/* 330:    */     
/* 331:412 */     JPanel cbHolder = new JPanel();
/* 332:413 */     cbHolder.setLayout(new BorderLayout());
/* 333:414 */     JPanel tempP = new JPanel();
/* 334:415 */     tempP.setLayout(new GridLayout(3, 2));
/* 335:416 */     tempP.add(this.m_costBenefitL);
/* 336:417 */     tempP.add(this.m_costBenefitV);
/* 337:418 */     tempP.add(new JLabel("Random: ", 4));
/* 338:419 */     tempP.add(this.m_randomV);
/* 339:420 */     tempP.add(new JLabel("Gain: ", 4));
/* 340:421 */     tempP.add(this.m_gainV);
/* 341:422 */     cbHolder.add(tempP, "North");
/* 342:423 */     JPanel butHolder = new JPanel();
/* 343:424 */     butHolder.setLayout(new GridLayout(2, 1));
/* 344:425 */     butHolder.add(this.m_maximizeCB);
/* 345:426 */     butHolder.add(this.m_minimizeCB);
/* 346:427 */     this.m_maximizeCB.addActionListener(new ActionListener()
/* 347:    */     {
/* 348:    */       public void actionPerformed(ActionEvent e)
/* 349:    */       {
/* 350:430 */         CostBenefitAnalysisPanel.this.findMaxMinCB(true);
/* 351:    */       }
/* 352:433 */     });
/* 353:434 */     this.m_minimizeCB.addActionListener(new ActionListener()
/* 354:    */     {
/* 355:    */       public void actionPerformed(ActionEvent e)
/* 356:    */       {
/* 357:437 */         CostBenefitAnalysisPanel.this.findMaxMinCB(false);
/* 358:    */       }
/* 359:440 */     });
/* 360:441 */     cbHolder.add(butHolder, "South");
/* 361:442 */     costPanel.add(cbHolder, "East");
/* 362:    */     
/* 363:444 */     JPanel popCBR = new JPanel();
/* 364:445 */     popCBR.setLayout(new GridLayout(1, 2));
/* 365:446 */     JPanel popHolder = new JPanel();
/* 366:447 */     popHolder.setLayout(new FlowLayout(0));
/* 367:448 */     popHolder.add(new JLabel("Total Population: "));
/* 368:449 */     popHolder.add(this.m_totalPopField);
/* 369:    */     
/* 370:451 */     JPanel radioHolder2 = new JPanel();
/* 371:452 */     radioHolder2.setLayout(new FlowLayout(2));
/* 372:453 */     radioHolder2.add(this.m_costR);
/* 373:454 */     radioHolder2.add(this.m_benefitR);
/* 374:455 */     popCBR.add(popHolder);
/* 375:456 */     popCBR.add(radioHolder2);
/* 376:    */     
/* 377:458 */     costPanel.add(popCBR, "South");
/* 378:    */     
/* 379:460 */     matrixHolder.add(costPanel);
/* 380:    */     
/* 381:462 */     lowerPanel.add(matrixHolder, "South");
/* 382:    */     
/* 383:    */ 
/* 384:    */ 
/* 385:    */ 
/* 386:    */ 
/* 387:    */ 
/* 388:    */ 
/* 389:    */ 
/* 390:    */ 
/* 391:    */ 
/* 392:    */ 
/* 393:474 */     add(lowerPanel, "South");
/* 394:    */   }
/* 395:    */   
/* 396:    */   public PlotData2D getMasterPlot()
/* 397:    */   {
/* 398:484 */     return this.m_masterPlot;
/* 399:    */   }
/* 400:    */   
/* 401:    */   private void findMaxMinCB(boolean max)
/* 402:    */   {
/* 403:488 */     double maxMin = max ? (-1.0D / 0.0D) : (1.0D / 0.0D);
/* 404:    */     
/* 405:    */ 
/* 406:491 */     Instances cBCurve = this.m_costBenefit.getPlotInstances();
/* 407:492 */     int maxMinIndex = 0;
/* 408:494 */     for (int i = 0; i < cBCurve.numInstances(); i++)
/* 409:    */     {
/* 410:495 */       Instance current = cBCurve.instance(i);
/* 411:496 */       if (max)
/* 412:    */       {
/* 413:497 */         if (current.value(1) > maxMin)
/* 414:    */         {
/* 415:498 */           maxMin = current.value(1);
/* 416:499 */           maxMinIndex = i;
/* 417:    */         }
/* 418:    */       }
/* 419:502 */       else if (current.value(1) < maxMin)
/* 420:    */       {
/* 421:503 */         maxMin = current.value(1);
/* 422:504 */         maxMinIndex = i;
/* 423:    */       }
/* 424:    */     }
/* 425:510 */     int indexOfSampleSize = this.m_masterPlot.getPlotInstances().attribute("Sample Size").index();
/* 426:    */     
/* 427:512 */     int indexOfPercOfTarget = this.m_masterPlot.getPlotInstances().attribute("Recall").index();
/* 428:    */     
/* 429:514 */     int indexOfThreshold = this.m_masterPlot.getPlotInstances().attribute("Threshold").index();
/* 430:    */     int indexOfMetric;
/* 431:    */     int indexOfMetric;
/* 432:518 */     if (this.m_percPop.isSelected())
/* 433:    */     {
/* 434:519 */       indexOfMetric = indexOfSampleSize;
/* 435:    */     }
/* 436:    */     else
/* 437:    */     {
/* 438:    */       int indexOfMetric;
/* 439:520 */       if (this.m_percOfTarget.isSelected()) {
/* 440:521 */         indexOfMetric = indexOfPercOfTarget;
/* 441:    */       } else {
/* 442:523 */         indexOfMetric = indexOfThreshold;
/* 443:    */       }
/* 444:    */     }
/* 445:526 */     double valueOfMetric = this.m_masterPlot.getPlotInstances().instance(maxMinIndex).value(indexOfMetric);
/* 446:    */     
/* 447:528 */     valueOfMetric *= 100.0D;
/* 448:    */     
/* 449:    */ 
/* 450:531 */     this.m_thresholdSlider.setValue((int)valueOfMetric);
/* 451:    */     
/* 452:    */ 
/* 453:    */ 
/* 454:535 */     updateInfoGivenIndex(maxMinIndex);
/* 455:    */   }
/* 456:    */   
/* 457:    */   private void updateCostBenefit()
/* 458:    */   {
/* 459:539 */     double value = this.m_thresholdSlider.getValue() / 100.0D;
/* 460:540 */     Instances plotInstances = this.m_masterPlot.getPlotInstances();
/* 461:541 */     int indexOfSampleSize = this.m_masterPlot.getPlotInstances().attribute("Sample Size").index();
/* 462:    */     
/* 463:543 */     int indexOfPercOfTarget = this.m_masterPlot.getPlotInstances().attribute("Recall").index();
/* 464:    */     
/* 465:545 */     int indexOfThreshold = this.m_masterPlot.getPlotInstances().attribute("Threshold").index();
/* 466:    */     int indexOfMetric;
/* 467:    */     int indexOfMetric;
/* 468:549 */     if (this.m_percPop.isSelected())
/* 469:    */     {
/* 470:550 */       indexOfMetric = indexOfSampleSize;
/* 471:    */     }
/* 472:    */     else
/* 473:    */     {
/* 474:    */       int indexOfMetric;
/* 475:551 */       if (this.m_percOfTarget.isSelected()) {
/* 476:552 */         indexOfMetric = indexOfPercOfTarget;
/* 477:    */       } else {
/* 478:554 */         indexOfMetric = indexOfThreshold;
/* 479:    */       }
/* 480:    */     }
/* 481:557 */     int index = findIndexForValue(value, plotInstances, indexOfMetric);
/* 482:558 */     updateCBRandomGainInfo(index);
/* 483:    */   }
/* 484:    */   
/* 485:    */   private void updateCBRandomGainInfo(int index)
/* 486:    */   {
/* 487:562 */     double requestedPopSize = this.m_originalPopSize;
/* 488:    */     try
/* 489:    */     {
/* 490:564 */       requestedPopSize = Double.parseDouble(this.m_totalPopField.getText());
/* 491:    */     }
/* 492:    */     catch (NumberFormatException e) {}
/* 493:567 */     double scaleFactor = requestedPopSize / this.m_originalPopSize;
/* 494:    */     
/* 495:569 */     double CB = this.m_costBenefit.getPlotInstances().instance(index).value(1);
/* 496:570 */     this.m_costBenefitV.setText(Utils.doubleToString(CB, 2));
/* 497:    */     
/* 498:572 */     double totalRandomCB = 0.0D;
/* 499:573 */     Instance first = this.m_masterPlot.getPlotInstances().instance(0);
/* 500:574 */     double totalPos = first.value(this.m_masterPlot.getPlotInstances().attribute("True Positives").index()) * scaleFactor;
/* 501:    */     
/* 502:    */ 
/* 503:577 */     double totalNeg = first.value(this.m_masterPlot.getPlotInstances().attribute("False Positives")) * scaleFactor;
/* 504:    */     
/* 505:    */ 
/* 506:    */ 
/* 507:581 */     double posInSample = totalPos * (Double.parseDouble(this.m_percPopLab.getText()) / 100.0D);
/* 508:    */     
/* 509:583 */     double negInSample = totalNeg * (Double.parseDouble(this.m_percPopLab.getText()) / 100.0D);
/* 510:    */     
/* 511:585 */     double posOutSample = totalPos - posInSample;
/* 512:586 */     double negOutSample = totalNeg - negInSample;
/* 513:    */     
/* 514:588 */     double tpCost = 0.0D;
/* 515:    */     try
/* 516:    */     {
/* 517:590 */       tpCost = Double.parseDouble(this.m_cost_aa.getText());
/* 518:    */     }
/* 519:    */     catch (NumberFormatException n) {}
/* 520:593 */     double fpCost = 0.0D;
/* 521:    */     try
/* 522:    */     {
/* 523:595 */       fpCost = Double.parseDouble(this.m_cost_ba.getText());
/* 524:    */     }
/* 525:    */     catch (NumberFormatException n) {}
/* 526:598 */     double tnCost = 0.0D;
/* 527:    */     try
/* 528:    */     {
/* 529:600 */       tnCost = Double.parseDouble(this.m_cost_bb.getText());
/* 530:    */     }
/* 531:    */     catch (NumberFormatException n) {}
/* 532:603 */     double fnCost = 0.0D;
/* 533:    */     try
/* 534:    */     {
/* 535:605 */       fnCost = Double.parseDouble(this.m_cost_ab.getText());
/* 536:    */     }
/* 537:    */     catch (NumberFormatException n) {}
/* 538:609 */     totalRandomCB += posInSample * tpCost;
/* 539:610 */     totalRandomCB += negInSample * fpCost;
/* 540:611 */     totalRandomCB += posOutSample * fnCost;
/* 541:612 */     totalRandomCB += negOutSample * tnCost;
/* 542:    */     
/* 543:614 */     this.m_randomV.setText(Utils.doubleToString(totalRandomCB, 2));
/* 544:615 */     double gain = this.m_costR.isSelected() ? totalRandomCB - CB : CB - totalRandomCB;
/* 545:    */     
/* 546:617 */     this.m_gainV.setText(Utils.doubleToString(gain, 2));
/* 547:    */     
/* 548:    */ 
/* 549:620 */     Instance currentInst = this.m_masterPlot.getPlotInstances().instance(index);
/* 550:621 */     double tp = currentInst.value(this.m_masterPlot.getPlotInstances().attribute("True Positives").index());
/* 551:    */     
/* 552:623 */     double tn = currentInst.value(this.m_masterPlot.getPlotInstances().attribute("True Negatives").index());
/* 553:    */     
/* 554:625 */     this.m_classificationAccV.setText(Utils.doubleToString((tp + tn) / (totalPos + totalNeg) * 100.0D, 4) + "%");
/* 555:    */   }
/* 556:    */   
/* 557:    */   private void updateInfoGivenIndex(int index)
/* 558:    */   {
/* 559:631 */     Instances plotInstances = this.m_masterPlot.getPlotInstances();
/* 560:632 */     int indexOfSampleSize = this.m_masterPlot.getPlotInstances().attribute("Sample Size").index();
/* 561:    */     
/* 562:634 */     int indexOfPercOfTarget = this.m_masterPlot.getPlotInstances().attribute("Recall").index();
/* 563:    */     
/* 564:636 */     int indexOfThreshold = this.m_masterPlot.getPlotInstances().attribute("Threshold").index();
/* 565:    */     
/* 566:    */ 
/* 567:    */ 
/* 568:640 */     this.m_percPopLab.setText(Utils.doubleToString(100.0D * plotInstances.instance(index).value(indexOfSampleSize), 4));
/* 569:    */     
/* 570:642 */     this.m_percOfTargetLab.setText(Utils.doubleToString(100.0D * plotInstances.instance(index).value(indexOfPercOfTarget), 4));
/* 571:    */     
/* 572:644 */     this.m_thresholdLab.setText(Utils.doubleToString(plotInstances.instance(index).value(indexOfThreshold), 4));
/* 573:655 */     if (this.m_previousShapeIndex >= 0) {
/* 574:656 */       this.m_shapeSizes[this.m_previousShapeIndex] = 1;
/* 575:    */     }
/* 576:659 */     this.m_shapeSizes[index] = 10;
/* 577:660 */     this.m_previousShapeIndex = index;
/* 578:    */     
/* 579:    */ 
/* 580:    */ 
/* 581:664 */     int tp = plotInstances.attribute("True Positives").index();
/* 582:665 */     int fp = plotInstances.attribute("False Positives").index();
/* 583:666 */     int tn = plotInstances.attribute("True Negatives").index();
/* 584:667 */     int fn = plotInstances.attribute("False Negatives").index();
/* 585:668 */     Instance temp = plotInstances.instance(index);
/* 586:669 */     double totalInstances = temp.value(tp) + temp.value(fp) + temp.value(tn) + temp.value(fn);
/* 587:    */     
/* 588:    */ 
/* 589:672 */     double requestedPopSize = totalInstances;
/* 590:    */     try
/* 591:    */     {
/* 592:674 */       requestedPopSize = Double.parseDouble(this.m_totalPopField.getText());
/* 593:    */     }
/* 594:    */     catch (NumberFormatException e) {}
/* 595:678 */     this.m_conf_aa.setCellValue(temp.value(tp), totalInstances, requestedPopSize / totalInstances, 2);
/* 596:    */     
/* 597:680 */     this.m_conf_ab.setCellValue(temp.value(fn), totalInstances, requestedPopSize / totalInstances, 2);
/* 598:    */     
/* 599:682 */     this.m_conf_ba.setCellValue(temp.value(fp), totalInstances, requestedPopSize / totalInstances, 2);
/* 600:    */     
/* 601:684 */     this.m_conf_bb.setCellValue(temp.value(tn), totalInstances, requestedPopSize / totalInstances, 2);
/* 602:    */     
/* 603:    */ 
/* 604:687 */     updateCBRandomGainInfo(index);
/* 605:    */     
/* 606:689 */     repaint();
/* 607:    */   }
/* 608:    */   
/* 609:    */   private void updateInfoForSliderValue(double value)
/* 610:    */   {
/* 611:693 */     int indexOfSampleSize = this.m_masterPlot.getPlotInstances().attribute("Sample Size").index();
/* 612:    */     
/* 613:695 */     int indexOfPercOfTarget = this.m_masterPlot.getPlotInstances().attribute("Recall").index();
/* 614:    */     
/* 615:697 */     int indexOfThreshold = this.m_masterPlot.getPlotInstances().attribute("Threshold").index();
/* 616:    */     int indexOfMetric;
/* 617:    */     int indexOfMetric;
/* 618:701 */     if (this.m_percPop.isSelected())
/* 619:    */     {
/* 620:702 */       indexOfMetric = indexOfSampleSize;
/* 621:    */     }
/* 622:    */     else
/* 623:    */     {
/* 624:    */       int indexOfMetric;
/* 625:703 */       if (this.m_percOfTarget.isSelected()) {
/* 626:704 */         indexOfMetric = indexOfPercOfTarget;
/* 627:    */       } else {
/* 628:706 */         indexOfMetric = indexOfThreshold;
/* 629:    */       }
/* 630:    */     }
/* 631:709 */     Instances plotInstances = this.m_masterPlot.getPlotInstances();
/* 632:710 */     int index = findIndexForValue(value, plotInstances, indexOfMetric);
/* 633:711 */     updateInfoGivenIndex(index);
/* 634:    */   }
/* 635:    */   
/* 636:    */   private int findIndexForValue(double value, Instances plotInstances, int indexOfMetric)
/* 637:    */   {
/* 638:719 */     int index = -1;
/* 639:720 */     int lower = 0;
/* 640:721 */     int upper = plotInstances.numInstances() - 1;
/* 641:722 */     int mid = (upper - lower) / 2;
/* 642:723 */     boolean done = false;
/* 643:724 */     while (!done)
/* 644:    */     {
/* 645:725 */       if (upper - lower <= 1)
/* 646:    */       {
/* 647:728 */         double comp1 = plotInstances.instance(upper).value(indexOfMetric);
/* 648:729 */         double comp2 = plotInstances.instance(lower).value(indexOfMetric);
/* 649:730 */         if (Math.abs(comp1 - value) < Math.abs(comp2 - value))
/* 650:    */         {
/* 651:731 */           index = upper; break;
/* 652:    */         }
/* 653:733 */         index = lower;
/* 654:    */         
/* 655:    */ 
/* 656:736 */         break;
/* 657:    */       }
/* 658:738 */       double comparisonVal = plotInstances.instance(mid).value(indexOfMetric);
/* 659:739 */       if (value > comparisonVal)
/* 660:    */       {
/* 661:740 */         if (this.m_threshold.isSelected())
/* 662:    */         {
/* 663:741 */           lower = mid;
/* 664:742 */           mid += (upper - lower) / 2;
/* 665:    */         }
/* 666:    */         else
/* 667:    */         {
/* 668:744 */           upper = mid;
/* 669:745 */           mid -= (upper - lower) / 2;
/* 670:    */         }
/* 671:    */       }
/* 672:747 */       else if (value < comparisonVal)
/* 673:    */       {
/* 674:748 */         if (this.m_threshold.isSelected())
/* 675:    */         {
/* 676:749 */           upper = mid;
/* 677:750 */           mid -= (upper - lower) / 2;
/* 678:    */         }
/* 679:    */         else
/* 680:    */         {
/* 681:752 */           lower = mid;
/* 682:753 */           mid += (upper - lower) / 2;
/* 683:    */         }
/* 684:    */       }
/* 685:    */       else
/* 686:    */       {
/* 687:756 */         index = mid;
/* 688:757 */         done = true;
/* 689:    */       }
/* 690:    */     }
/* 691:762 */     if (!this.m_threshold.isSelected()) {
/* 692:763 */       while ((index + 1 < plotInstances.numInstances()) && 
/* 693:764 */         (plotInstances.instance(index + 1).value(indexOfMetric) == plotInstances.instance(index).value(indexOfMetric))) {
/* 694:766 */         index++;
/* 695:    */       }
/* 696:    */     }
/* 697:772 */     while ((index - 1 >= 0) && 
/* 698:773 */       (plotInstances.instance(index - 1).value(indexOfMetric) == plotInstances.instance(index).value(indexOfMetric))) {
/* 699:775 */       index--;
/* 700:    */     }
/* 701:781 */     return index;
/* 702:    */   }
/* 703:    */   
/* 704:    */   public synchronized void setDataSet(PlotData2D data, Attribute classAtt)
/* 705:    */     throws Exception
/* 706:    */   {
/* 707:795 */     this.m_masterPlot = new PlotData2D(data.getPlotInstances());
/* 708:796 */     boolean[] connectPoints = new boolean[this.m_masterPlot.getPlotInstances().numInstances()];
/* 709:798 */     for (int i = 1; i < connectPoints.length; i++) {
/* 710:799 */       connectPoints[i] = true;
/* 711:    */     }
/* 712:801 */     this.m_masterPlot.setConnectPoints(connectPoints);
/* 713:    */     
/* 714:803 */     this.m_masterPlot.m_alwaysDisplayPointsOfThisSize = 10;
/* 715:804 */     setClassForConfusionMatrix(classAtt);
/* 716:805 */     this.m_performancePanel.setMasterPlot(this.m_masterPlot);
/* 717:806 */     this.m_performancePanel.validate();
/* 718:807 */     this.m_performancePanel.repaint();
/* 719:    */     
/* 720:809 */     this.m_shapeSizes = new int[this.m_masterPlot.getPlotInstances().numInstances()];
/* 721:810 */     for (int i = 0; i < this.m_shapeSizes.length; i++) {
/* 722:811 */       this.m_shapeSizes[i] = 1;
/* 723:    */     }
/* 724:813 */     this.m_masterPlot.setShapeSize(this.m_shapeSizes);
/* 725:814 */     constructCostBenefitData();
/* 726:815 */     this.m_costBenefitPanel.setMasterPlot(this.m_costBenefit);
/* 727:816 */     this.m_costBenefitPanel.validate();
/* 728:817 */     this.m_costBenefitPanel.repaint();
/* 729:    */     
/* 730:819 */     this.m_totalPopPrevious = 0;
/* 731:820 */     this.m_fpPrevious = 0.0D;
/* 732:821 */     this.m_tpPrevious = 0.0D;
/* 733:822 */     this.m_tnPrevious = 0.0D;
/* 734:823 */     this.m_fnPrevious = 0.0D;
/* 735:824 */     this.m_previousShapeIndex = -1;
/* 736:    */     
/* 737:    */ 
/* 738:827 */     Instance first = this.m_masterPlot.getPlotInstances().instance(0);
/* 739:828 */     double totalPos = first.value(this.m_masterPlot.getPlotInstances().attribute("True Positives").index());
/* 740:    */     
/* 741:830 */     double totalNeg = first.value(this.m_masterPlot.getPlotInstances().attribute("False Positives"));
/* 742:    */     
/* 743:832 */     this.m_originalPopSize = ((int)(totalPos + totalNeg));
/* 744:833 */     this.m_totalPopField.setText("" + this.m_originalPopSize);
/* 745:    */     
/* 746:835 */     this.m_performancePanel.setYIndex(5);
/* 747:836 */     this.m_performancePanel.setXIndex(10);
/* 748:837 */     this.m_costBenefitPanel.setXIndex(0);
/* 749:838 */     this.m_costBenefitPanel.setYIndex(1);
/* 750:    */     
/* 751:840 */     updateInfoForSliderValue(this.m_thresholdSlider.getValue() / 100.0D);
/* 752:    */   }
/* 753:    */   
/* 754:    */   private void setClassForConfusionMatrix(Attribute classAtt)
/* 755:    */   {
/* 756:844 */     this.m_classAttribute = classAtt;
/* 757:845 */     this.m_conf_actualA.setText(" Actual (a): " + classAtt.value(0));
/* 758:846 */     this.m_conf_actualA.setToolTipText(classAtt.value(0));
/* 759:847 */     String negClasses = "";
/* 760:848 */     for (int i = 1; i < classAtt.numValues(); i++)
/* 761:    */     {
/* 762:849 */       negClasses = negClasses + classAtt.value(i);
/* 763:850 */       if (i < classAtt.numValues() - 1) {
/* 764:851 */         negClasses = negClasses + ",";
/* 765:    */       }
/* 766:    */     }
/* 767:854 */     this.m_conf_actualB.setText(" Actual (b): " + negClasses);
/* 768:855 */     this.m_conf_actualB.setToolTipText(negClasses);
/* 769:    */   }
/* 770:    */   
/* 771:    */   private boolean constructCostBenefitData()
/* 772:    */   {
/* 773:859 */     double tpCost = 0.0D;
/* 774:    */     try
/* 775:    */     {
/* 776:861 */       tpCost = Double.parseDouble(this.m_cost_aa.getText());
/* 777:    */     }
/* 778:    */     catch (NumberFormatException n) {}
/* 779:864 */     double fpCost = 0.0D;
/* 780:    */     try
/* 781:    */     {
/* 782:866 */       fpCost = Double.parseDouble(this.m_cost_ba.getText());
/* 783:    */     }
/* 784:    */     catch (NumberFormatException n) {}
/* 785:869 */     double tnCost = 0.0D;
/* 786:    */     try
/* 787:    */     {
/* 788:871 */       tnCost = Double.parseDouble(this.m_cost_bb.getText());
/* 789:    */     }
/* 790:    */     catch (NumberFormatException n) {}
/* 791:874 */     double fnCost = 0.0D;
/* 792:    */     try
/* 793:    */     {
/* 794:876 */       fnCost = Double.parseDouble(this.m_cost_ab.getText());
/* 795:    */     }
/* 796:    */     catch (NumberFormatException n) {}
/* 797:880 */     double requestedPopSize = this.m_originalPopSize;
/* 798:    */     try
/* 799:    */     {
/* 800:882 */       requestedPopSize = Double.parseDouble(this.m_totalPopField.getText());
/* 801:    */     }
/* 802:    */     catch (NumberFormatException e) {}
/* 803:886 */     double scaleFactor = 1.0D;
/* 804:887 */     if (this.m_originalPopSize != 0) {
/* 805:888 */       scaleFactor = requestedPopSize / this.m_originalPopSize;
/* 806:    */     }
/* 807:891 */     if ((tpCost == this.m_tpPrevious) && (fpCost == this.m_fpPrevious) && (tnCost == this.m_tnPrevious) && (fnCost == this.m_fnPrevious) && (requestedPopSize == this.m_totalPopPrevious)) {
/* 808:894 */       return false;
/* 809:    */     }
/* 810:898 */     ArrayList<Attribute> fv = new ArrayList();
/* 811:899 */     fv.add(new Attribute("Sample Size"));
/* 812:900 */     fv.add(new Attribute("Cost/Benefit"));
/* 813:901 */     fv.add(new Attribute("Threshold"));
/* 814:902 */     Instances costBenefitI = new Instances("Cost/Benefit Curve", fv, 100);
/* 815:    */     
/* 816:    */ 
/* 817:905 */     Instances performanceI = this.m_masterPlot.getPlotInstances();
/* 818:907 */     for (int i = 0; i < performanceI.numInstances(); i++)
/* 819:    */     {
/* 820:908 */       Instance current = performanceI.instance(i);
/* 821:    */       
/* 822:910 */       double[] vals = new double[3];
/* 823:911 */       vals[0] = current.value(10);
/* 824:912 */       vals[1] = ((current.value(0) * tpCost + current.value(1) * fnCost + current.value(2) * fpCost + current.value(3) * tnCost) * scaleFactor);
/* 825:    */       
/* 826:    */ 
/* 827:915 */       vals[2] = current.value(current.numAttributes() - 1);
/* 828:916 */       Instance newInst = new DenseInstance(1.0D, vals);
/* 829:917 */       costBenefitI.add(newInst);
/* 830:    */     }
/* 831:920 */     costBenefitI.compactify();
/* 832:    */     
/* 833:    */ 
/* 834:923 */     this.m_costBenefit = new PlotData2D(costBenefitI);
/* 835:924 */     this.m_costBenefit.m_alwaysDisplayPointsOfThisSize = 10;
/* 836:925 */     this.m_costBenefit.setPlotName("Cost/benefit curve");
/* 837:926 */     boolean[] connectPoints = new boolean[costBenefitI.numInstances()];
/* 838:928 */     for (int i = 0; i < connectPoints.length; i++) {
/* 839:929 */       connectPoints[i] = true;
/* 840:    */     }
/* 841:    */     try
/* 842:    */     {
/* 843:932 */       this.m_costBenefit.setConnectPoints(connectPoints);
/* 844:933 */       this.m_costBenefit.setShapeSize(this.m_shapeSizes);
/* 845:    */     }
/* 846:    */     catch (Exception ex) {}
/* 847:938 */     this.m_tpPrevious = tpCost;
/* 848:939 */     this.m_fpPrevious = fpCost;
/* 849:940 */     this.m_tnPrevious = tnCost;
/* 850:941 */     this.m_fnPrevious = fnCost;
/* 851:    */     
/* 852:943 */     return true;
/* 853:    */   }
/* 854:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.CostBenefitAnalysisPanel
 * JD-Core Version:    0.7.0.1
 */