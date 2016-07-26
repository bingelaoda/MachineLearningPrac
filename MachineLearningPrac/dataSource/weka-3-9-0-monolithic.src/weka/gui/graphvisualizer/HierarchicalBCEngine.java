/*    1:     */ package weka.gui.graphvisualizer;
/*    2:     */ 
/*    3:     */ import java.awt.GridBagConstraints;
/*    4:     */ import java.awt.GridBagLayout;
/*    5:     */ import java.awt.event.ActionEvent;
/*    6:     */ import java.awt.event.ActionListener;
/*    7:     */ import java.io.PrintStream;
/*    8:     */ import java.util.ArrayList;
/*    9:     */ import javax.swing.BorderFactory;
/*   10:     */ import javax.swing.ButtonGroup;
/*   11:     */ import javax.swing.JCheckBox;
/*   12:     */ import javax.swing.JPanel;
/*   13:     */ import javax.swing.JProgressBar;
/*   14:     */ import javax.swing.JRadioButton;
/*   15:     */ 
/*   16:     */ public class HierarchicalBCEngine
/*   17:     */   implements GraphConstants, LayoutEngine
/*   18:     */ {
/*   19:     */   protected ArrayList<GraphNode> m_nodes;
/*   20:     */   protected ArrayList<GraphEdge> m_edges;
/*   21:     */   protected ArrayList<LayoutCompleteEventListener> layoutCompleteListeners;
/*   22:     */   protected int[][] graphMatrix;
/*   23:     */   protected int[][] nodeLevels;
/*   24:     */   protected int m_nodeWidth;
/*   25:     */   protected int m_nodeHeight;
/*   26:     */   protected JRadioButton m_jRbNaiveLayout;
/*   27:     */   protected JRadioButton m_jRbPriorityLayout;
/*   28:     */   protected JRadioButton m_jRbTopdown;
/*   29:     */   protected JRadioButton m_jRbBottomup;
/*   30:     */   protected JCheckBox m_jCbEdgeConcentration;
/*   31:     */   protected JPanel m_controlsPanel;
/*   32:     */   protected JProgressBar m_progress;
/*   33: 106 */   protected boolean m_completeReLayout = false;
/*   34:     */   private int origNodesSize;
/*   35:     */   
/*   36:     */   public HierarchicalBCEngine(ArrayList<GraphNode> nodes, ArrayList<GraphEdge> edges, int nodeWidth, int nodeHeight)
/*   37:     */   {
/*   38: 120 */     this.m_nodes = nodes;
/*   39: 121 */     this.m_edges = edges;
/*   40: 122 */     this.m_nodeWidth = nodeWidth;
/*   41: 123 */     this.m_nodeHeight = nodeHeight;
/*   42: 124 */     makeGUIPanel(false);
/*   43:     */   }
/*   44:     */   
/*   45:     */   public HierarchicalBCEngine(ArrayList<GraphNode> nodes, ArrayList<GraphEdge> edges, int nodeWidth, int nodeHeight, boolean edgeConcentration)
/*   46:     */   {
/*   47: 142 */     this.m_nodes = nodes;
/*   48: 143 */     this.m_edges = edges;
/*   49: 144 */     this.m_nodeWidth = nodeWidth;
/*   50: 145 */     this.m_nodeHeight = nodeHeight;
/*   51: 146 */     makeGUIPanel(edgeConcentration);
/*   52:     */   }
/*   53:     */   
/*   54:     */   public HierarchicalBCEngine() {}
/*   55:     */   
/*   56:     */   protected void makeGUIPanel(boolean edgeConc)
/*   57:     */   {
/*   58: 162 */     this.m_jRbNaiveLayout = new JRadioButton("Naive Layout");
/*   59: 163 */     this.m_jRbPriorityLayout = new JRadioButton("Priority Layout");
/*   60: 164 */     ButtonGroup bg = new ButtonGroup();
/*   61: 165 */     bg.add(this.m_jRbNaiveLayout);
/*   62: 166 */     bg.add(this.m_jRbPriorityLayout);
/*   63: 167 */     this.m_jRbPriorityLayout.setSelected(true);
/*   64:     */     
/*   65: 169 */     ActionListener a = new ActionListener()
/*   66:     */     {
/*   67:     */       public void actionPerformed(ActionEvent ae)
/*   68:     */       {
/*   69: 172 */         HierarchicalBCEngine.this.m_completeReLayout = true;
/*   70:     */       }
/*   71: 175 */     };
/*   72: 176 */     this.m_jRbTopdown = new JRadioButton("Top Down");
/*   73: 177 */     this.m_jRbBottomup = new JRadioButton("Bottom Up");
/*   74: 178 */     this.m_jRbTopdown.addActionListener(a);
/*   75: 179 */     this.m_jRbBottomup.addActionListener(a);
/*   76: 180 */     bg = new ButtonGroup();
/*   77: 181 */     bg.add(this.m_jRbTopdown);
/*   78: 182 */     bg.add(this.m_jRbBottomup);
/*   79: 183 */     this.m_jRbBottomup.setSelected(true);
/*   80:     */     
/*   81: 185 */     this.m_jCbEdgeConcentration = new JCheckBox("With Edge Concentration", edgeConc);
/*   82: 186 */     this.m_jCbEdgeConcentration.setSelected(edgeConc);
/*   83: 187 */     this.m_jCbEdgeConcentration.addActionListener(a);
/*   84:     */     
/*   85: 189 */     JPanel jp1 = new JPanel(new GridBagLayout());
/*   86: 190 */     GridBagConstraints gbc = new GridBagConstraints();
/*   87: 191 */     gbc.gridwidth = 0;
/*   88: 192 */     gbc.anchor = 18;
/*   89: 193 */     gbc.weightx = 1.0D;
/*   90: 194 */     gbc.fill = 2;
/*   91: 195 */     jp1.add(this.m_jRbNaiveLayout, gbc);
/*   92: 196 */     jp1.add(this.m_jRbPriorityLayout, gbc);
/*   93: 197 */     jp1.setBorder(BorderFactory.createTitledBorder("Layout Type"));
/*   94:     */     
/*   95: 199 */     JPanel jp2 = new JPanel(new GridBagLayout());
/*   96: 200 */     jp2.add(this.m_jRbTopdown, gbc);
/*   97: 201 */     jp2.add(this.m_jRbBottomup, gbc);
/*   98: 202 */     jp2.setBorder(BorderFactory.createTitledBorder("Layout Method"));
/*   99:     */     
/*  100: 204 */     this.m_progress = new JProgressBar(0, 11);
/*  101: 205 */     this.m_progress.setBorderPainted(false);
/*  102: 206 */     this.m_progress.setStringPainted(true);
/*  103: 207 */     this.m_progress.setString("");
/*  104:     */     
/*  105: 209 */     this.m_progress.setValue(0);
/*  106:     */     
/*  107: 211 */     this.m_controlsPanel = new JPanel(new GridBagLayout());
/*  108: 212 */     this.m_controlsPanel.add(jp1, gbc);
/*  109: 213 */     this.m_controlsPanel.add(jp2, gbc);
/*  110: 214 */     this.m_controlsPanel.add(this.m_jCbEdgeConcentration, gbc);
/*  111:     */   }
/*  112:     */   
/*  113:     */   public ArrayList<GraphNode> getNodes()
/*  114:     */   {
/*  115: 220 */     return this.m_nodes;
/*  116:     */   }
/*  117:     */   
/*  118:     */   public JPanel getControlPanel()
/*  119:     */   {
/*  120: 229 */     return this.m_controlsPanel;
/*  121:     */   }
/*  122:     */   
/*  123:     */   public JProgressBar getProgressBar()
/*  124:     */   {
/*  125: 237 */     return this.m_progress;
/*  126:     */   }
/*  127:     */   
/*  128:     */   public void setNodesEdges(ArrayList<GraphNode> nodes, ArrayList<GraphEdge> edges)
/*  129:     */   {
/*  130: 250 */     this.m_nodes = nodes;
/*  131: 251 */     this.m_edges = edges;
/*  132:     */   }
/*  133:     */   
/*  134:     */   public void setNodeSize(int nodeWidth, int nodeHeight)
/*  135:     */   {
/*  136: 263 */     this.m_nodeWidth = nodeWidth;
/*  137: 264 */     this.m_nodeHeight = nodeHeight;
/*  138:     */   }
/*  139:     */   
/*  140:     */   public void addLayoutCompleteEventListener(LayoutCompleteEventListener l)
/*  141:     */   {
/*  142: 274 */     if (this.layoutCompleteListeners == null) {
/*  143: 275 */       this.layoutCompleteListeners = new ArrayList();
/*  144:     */     }
/*  145: 277 */     this.layoutCompleteListeners.add(l);
/*  146:     */   }
/*  147:     */   
/*  148:     */   public void removeLayoutCompleteEventListener(LayoutCompleteEventListener e)
/*  149:     */   {
/*  150: 287 */     if (this.layoutCompleteListeners != null)
/*  151:     */     {
/*  152: 290 */       for (int i = 0; i < this.layoutCompleteListeners.size(); i++)
/*  153:     */       {
/*  154: 291 */         LayoutCompleteEventListener l = (LayoutCompleteEventListener)this.layoutCompleteListeners.get(i);
/*  155: 292 */         if (l == e)
/*  156:     */         {
/*  157: 293 */           this.layoutCompleteListeners.remove(i);
/*  158: 294 */           return;
/*  159:     */         }
/*  160:     */       }
/*  161: 297 */       System.err.println("layoutCompleteListener to be remove not present");
/*  162:     */     }
/*  163:     */     else
/*  164:     */     {
/*  165: 299 */       System.err.println("layoutCompleteListener to be remove not present");
/*  166:     */     }
/*  167:     */   }
/*  168:     */   
/*  169:     */   public void fireLayoutCompleteEvent(LayoutCompleteEvent e)
/*  170:     */   {
/*  171: 310 */     if ((this.layoutCompleteListeners != null) && (this.layoutCompleteListeners.size() != 0)) {
/*  172: 313 */       for (int i = 0; i < this.layoutCompleteListeners.size(); i++)
/*  173:     */       {
/*  174: 314 */         LayoutCompleteEventListener l = (LayoutCompleteEventListener)this.layoutCompleteListeners.get(i);
/*  175: 315 */         l.layoutCompleted(e);
/*  176:     */       }
/*  177:     */     }
/*  178:     */   }
/*  179:     */   
/*  180:     */   public void layoutGraph()
/*  181:     */   {
/*  182: 336 */     if ((this.m_nodes == null) || (this.m_edges == null)) {
/*  183: 337 */       return;
/*  184:     */     }
/*  185: 340 */     Thread th = new Thread()
/*  186:     */     {
/*  187:     */       public void run()
/*  188:     */       {
/*  189: 343 */         HierarchicalBCEngine.this.m_progress.setBorderPainted(true);
/*  190: 344 */         if (HierarchicalBCEngine.this.nodeLevels == null)
/*  191:     */         {
/*  192: 345 */           HierarchicalBCEngine.this.makeProperHierarchy();
/*  193:     */         }
/*  194: 346 */         else if (HierarchicalBCEngine.this.m_completeReLayout == true)
/*  195:     */         {
/*  196: 347 */           HierarchicalBCEngine.this.clearTemps_and_EdgesFromNodes();
/*  197: 348 */           HierarchicalBCEngine.this.makeProperHierarchy();
/*  198: 349 */           HierarchicalBCEngine.this.m_completeReLayout = false;
/*  199:     */         }
/*  200: 353 */         if (HierarchicalBCEngine.this.m_jRbTopdown.isSelected())
/*  201:     */         {
/*  202: 354 */           int crossbefore = HierarchicalBCEngine.this.crossings(HierarchicalBCEngine.this.nodeLevels);int crossafter = 0;int i = 0;
/*  203:     */           do
/*  204:     */           {
/*  205: 356 */             HierarchicalBCEngine.this.m_progress.setValue(i + 4);
/*  206: 357 */             HierarchicalBCEngine.this.m_progress.setString("Minimizing Crossings: Pass" + (i + 1));
/*  207: 358 */             if (i != 0) {
/*  208: 359 */               crossbefore = crossafter;
/*  209:     */             }
/*  210: 361 */             HierarchicalBCEngine.this.nodeLevels = HierarchicalBCEngine.this.minimizeCrossings(false, HierarchicalBCEngine.this.nodeLevels);
/*  211: 362 */             crossafter = HierarchicalBCEngine.this.crossings(HierarchicalBCEngine.this.nodeLevels);
/*  212: 363 */             i++;
/*  213: 364 */           } while ((crossafter < crossbefore) && (i < 6));
/*  214:     */         }
/*  215:     */         else
/*  216:     */         {
/*  217: 366 */           int crossbefore = HierarchicalBCEngine.this.crossings(HierarchicalBCEngine.this.nodeLevels);int crossafter = 0;int i = 0;
/*  218:     */           do
/*  219:     */           {
/*  220: 368 */             HierarchicalBCEngine.this.m_progress.setValue(i + 4);
/*  221: 369 */             HierarchicalBCEngine.this.m_progress.setString("Minimizing Crossings: Pass" + (i + 1));
/*  222: 370 */             if (i != 0) {
/*  223: 371 */               crossbefore = crossafter;
/*  224:     */             }
/*  225: 373 */             HierarchicalBCEngine.this.nodeLevels = HierarchicalBCEngine.this.minimizeCrossings(true, HierarchicalBCEngine.this.nodeLevels);
/*  226: 374 */             crossafter = HierarchicalBCEngine.this.crossings(HierarchicalBCEngine.this.nodeLevels);
/*  227: 375 */             i++;
/*  228: 376 */           } while ((crossafter < crossbefore) && (i < 6));
/*  229:     */         }
/*  230: 382 */         HierarchicalBCEngine.this.m_progress.setValue(10);
/*  231: 383 */         HierarchicalBCEngine.this.m_progress.setString("Laying out vertices");
/*  232: 385 */         if (HierarchicalBCEngine.this.m_jRbNaiveLayout.isSelected()) {
/*  233: 386 */           HierarchicalBCEngine.this.naiveLayout();
/*  234:     */         } else {
/*  235: 388 */           HierarchicalBCEngine.this.priorityLayout1();
/*  236:     */         }
/*  237: 390 */         HierarchicalBCEngine.this.m_progress.setValue(11);
/*  238: 391 */         HierarchicalBCEngine.this.m_progress.setString("Layout Complete");
/*  239: 392 */         HierarchicalBCEngine.this.m_progress.repaint();
/*  240:     */         
/*  241: 394 */         HierarchicalBCEngine.this.fireLayoutCompleteEvent(new LayoutCompleteEvent(this));
/*  242: 395 */         HierarchicalBCEngine.this.m_progress.setValue(0);
/*  243: 396 */         HierarchicalBCEngine.this.m_progress.setString("");
/*  244: 397 */         HierarchicalBCEngine.this.m_progress.setBorderPainted(false);
/*  245:     */       }
/*  246: 399 */     };
/*  247: 400 */     th.start();
/*  248:     */   }
/*  249:     */   
/*  250:     */   protected void clearTemps_and_EdgesFromNodes()
/*  251:     */   {
/*  252: 415 */     int curSize = this.m_nodes.size();
/*  253: 416 */     for (int i = this.origNodesSize; i < curSize; i++) {
/*  254: 417 */       this.m_nodes.remove(this.origNodesSize);
/*  255:     */     }
/*  256: 419 */     for (int j = 0; j < this.m_nodes.size(); j++) {
/*  257: 420 */       ((GraphNode)this.m_nodes.get(j)).edges = ((int[][])null);
/*  258:     */     }
/*  259: 423 */     this.nodeLevels = ((int[][])null);
/*  260:     */   }
/*  261:     */   
/*  262:     */   protected void processGraph()
/*  263:     */   {
/*  264: 437 */     this.origNodesSize = this.m_nodes.size();
/*  265: 438 */     this.graphMatrix = new int[this.m_nodes.size()][this.m_nodes.size()];
/*  266: 445 */     for (int i = 0; i < this.m_edges.size(); i++) {
/*  267: 446 */       this.graphMatrix[((GraphEdge)this.m_edges.get(i)).src][((GraphEdge)this.m_edges.get(i)).dest] = ((GraphEdge)this.m_edges.get(i)).type;
/*  268:     */     }
/*  269:     */   }
/*  270:     */   
/*  271:     */   protected void makeProperHierarchy()
/*  272:     */   {
/*  273: 464 */     processGraph();
/*  274:     */     
/*  275: 466 */     this.m_progress.setValue(1);
/*  276: 467 */     this.m_progress.setString("Removing Cycles");
/*  277:     */     
/*  278: 469 */     removeCycles();
/*  279:     */     
/*  280: 471 */     this.m_progress.setValue(2);
/*  281: 472 */     this.m_progress.setString("Assigning levels to nodes");
/*  282:     */     
/*  283: 474 */     int[] nodesLevel = new int[this.m_nodes.size()];
/*  284: 475 */     int depth = 0;
/*  285: 476 */     for (int i = 0; i < this.graphMatrix.length; i++) {
/*  286: 477 */       assignLevels(nodesLevel, depth, i, 0);
/*  287:     */     }
/*  288: 480 */     for (int i = 0; i < nodesLevel.length; i++) {
/*  289: 481 */       if (nodesLevel[i] == 0)
/*  290:     */       {
/*  291: 482 */         int min = 65536;
/*  292: 483 */         for (int j = 0; j < this.graphMatrix[i].length; j++) {
/*  293: 484 */           if ((this.graphMatrix[i][j] == 1) && 
/*  294: 485 */             (min > nodesLevel[j])) {
/*  295: 486 */             min = nodesLevel[j];
/*  296:     */           }
/*  297:     */         }
/*  298: 492 */         if ((min != 65536) && (min > 1)) {
/*  299: 493 */           nodesLevel[i] = (min - 1);
/*  300:     */         }
/*  301:     */       }
/*  302:     */     }
/*  303: 499 */     int maxLevel = 0;
/*  304: 500 */     for (int element : nodesLevel) {
/*  305: 501 */       if (element > maxLevel) {
/*  306: 502 */         maxLevel = element;
/*  307:     */       }
/*  308:     */     }
/*  309: 507 */     int[] levelCounts = new int[maxLevel + 1];
/*  310: 509 */     for (int i = 0; i < nodesLevel.length; i++) {
/*  311: 510 */       levelCounts[nodesLevel[i]] += 1;
/*  312:     */     }
/*  313: 514 */     int[] levelsCounter = new int[maxLevel + 1];
/*  314: 515 */     this.nodeLevels = new int[maxLevel + 1][];
/*  315: 516 */     for (int i = 0; i < nodesLevel.length; i++)
/*  316:     */     {
/*  317: 517 */       if (this.nodeLevels[nodesLevel[i]] == null) {
/*  318: 518 */         this.nodeLevels[nodesLevel[i]] = new int[levelCounts[nodesLevel[i]]];
/*  319:     */       }
/*  320: 523 */       int tmp320_319 = nodesLevel[i]; int[] tmp320_314 = levelsCounter; int tmp322_321 = tmp320_314[tmp320_319];tmp320_314[tmp320_319] = (tmp322_321 + 1);this.nodeLevels[nodesLevel[i]][tmp322_321] = i;
/*  321:     */     }
/*  322: 526 */     this.m_progress.setValue(3);
/*  323: 527 */     this.m_progress.setString("Removing gaps by adding dummy vertices");
/*  324: 529 */     if (this.m_jCbEdgeConcentration.isSelected()) {
/*  325: 530 */       removeGapsWithEdgeConcentration(nodesLevel);
/*  326:     */     } else {
/*  327: 532 */       removeGaps(nodesLevel);
/*  328:     */     }
/*  329: 550 */     for (int i = 0; i < this.graphMatrix.length; i++)
/*  330:     */     {
/*  331: 551 */       GraphNode n = (GraphNode)this.m_nodes.get(i);
/*  332: 552 */       int sum = 0;
/*  333: 553 */       for (int j = 0; j < this.graphMatrix[i].length; j++) {
/*  334: 554 */         if (this.graphMatrix[i][j] != 0) {
/*  335: 555 */           sum++;
/*  336:     */         }
/*  337:     */       }
/*  338: 559 */       n.edges = new int[sum][2];
/*  339: 560 */       int j = 0;
/*  340: 560 */       for (int k = 0; j < this.graphMatrix[i].length; j++) {
/*  341: 561 */         if (this.graphMatrix[i][j] != 0)
/*  342:     */         {
/*  343: 562 */           n.edges[k][0] = j;
/*  344: 563 */           n.edges[k][1] = this.graphMatrix[i][j];
/*  345: 564 */           k++;
/*  346:     */         }
/*  347:     */       }
/*  348:     */     }
/*  349:     */   }
/*  350:     */   
/*  351:     */   private void removeGaps(int[] nodesLevel)
/*  352:     */   {
/*  353: 580 */     int temp = this.m_nodes.size();
/*  354: 581 */     int temp2 = this.graphMatrix[0].length;int tempCnt = 1;
/*  355: 583 */     for (int n = 0; n < temp; n++) {
/*  356: 584 */       for (int i = 0; i < temp2; i++)
/*  357:     */       {
/*  358: 585 */         int len = this.graphMatrix.length;
/*  359: 586 */         if (this.graphMatrix[n][i] > 0) {
/*  360: 587 */           if (nodesLevel[i] > nodesLevel[n] + 1)
/*  361:     */           {
/*  362: 588 */             int[][] tempMatrix = new int[this.graphMatrix.length + (nodesLevel[i] - nodesLevel[n] - 1)][this.graphMatrix.length + (nodesLevel[i] - nodesLevel[n] - 1)];
/*  363:     */             
/*  364:     */ 
/*  365: 591 */             int level = nodesLevel[n] + 1;
/*  366: 592 */             copyMatrix(this.graphMatrix, tempMatrix);
/*  367:     */             
/*  368: 594 */             String s1 = new String("S" + tempCnt++);
/*  369: 595 */             this.m_nodes.add(new GraphNode(s1, s1, 1));
/*  370: 596 */             int[] temp3 = new int[this.nodeLevels[level].length + 1];
/*  371:     */             
/*  372:     */ 
/*  373: 599 */             System.arraycopy(this.nodeLevels[level], 0, temp3, 0, this.nodeLevels[level].length);
/*  374:     */             
/*  375: 601 */             temp3[(temp3.length - 1)] = (this.m_nodes.size() - 1);
/*  376: 602 */             this.nodeLevels[level] = temp3;
/*  377: 603 */             level++;
/*  378: 609 */             for (int k = len; k < len + nodesLevel[i] - nodesLevel[n] - 1 - 1; k++)
/*  379:     */             {
/*  380: 610 */               String s2 = new String("S" + tempCnt);
/*  381: 611 */               this.m_nodes.add(new GraphNode(s2, s2, 1));
/*  382:     */               
/*  383: 613 */               temp3 = new int[this.nodeLevels[level].length + 1];
/*  384:     */               
/*  385:     */ 
/*  386: 616 */               System.arraycopy(this.nodeLevels[level], 0, temp3, 0, this.nodeLevels[level].length);
/*  387:     */               
/*  388: 618 */               temp3[(temp3.length - 1)] = (this.m_nodes.size() - 1);
/*  389: 619 */               this.nodeLevels[(level++)] = temp3;
/*  390:     */               
/*  391: 621 */               tempMatrix[k][(k + 1)] = tempMatrix[n][i];
/*  392: 622 */               tempCnt++;
/*  393: 623 */               if (k > len) {
/*  394: 624 */                 tempMatrix[k][(k - 1)] = (-1 * tempMatrix[n][i]);
/*  395:     */               }
/*  396:     */             }
/*  397: 629 */             tempMatrix[k][i] = tempMatrix[n][i];
/*  398:     */             
/*  399:     */ 
/*  400:     */ 
/*  401:     */ 
/*  402:     */ 
/*  403: 635 */             tempMatrix[n][len] = tempMatrix[n][i];
/*  404:     */             
/*  405: 637 */             tempMatrix[len][n] = (-1 * tempMatrix[n][i]);
/*  406:     */             
/*  407: 639 */             tempMatrix[i][k] = (-1 * tempMatrix[n][i]);
/*  408: 643 */             if (k > len) {
/*  409: 644 */               tempMatrix[k][(k - 1)] = (-1 * tempMatrix[n][i]);
/*  410:     */             }
/*  411: 649 */             tempMatrix[n][i] = 0;
/*  412: 650 */             tempMatrix[i][n] = 0;
/*  413:     */             
/*  414: 652 */             this.graphMatrix = tempMatrix;
/*  415:     */           }
/*  416:     */           else
/*  417:     */           {
/*  418: 659 */             this.graphMatrix[i][n] = (-1 * this.graphMatrix[n][i]);
/*  419:     */           }
/*  420:     */         }
/*  421:     */       }
/*  422:     */     }
/*  423:     */   }
/*  424:     */   
/*  425:     */   private void removeGapsWithEdgeConcentration(int[] nodesLevel)
/*  426:     */   {
/*  427: 676 */     int temp = this.m_nodes.size();int temp2 = this.graphMatrix[0].length;
/*  428: 677 */     int tempCnt = 1;
/*  429: 679 */     for (int n = 0; n < temp; n++) {
/*  430: 680 */       for (int i = 0; i < temp2; i++) {
/*  431: 681 */         if (this.graphMatrix[n][i] > 0) {
/*  432: 682 */           if (nodesLevel[i] > nodesLevel[n] + 1)
/*  433:     */           {
/*  434: 686 */             int tempLevel = nodesLevel[n];
/*  435: 687 */             boolean tempNodePresent = false;
/*  436: 688 */             int k = temp;
/*  437: 689 */             int tempnode = n;
/*  438: 691 */             while (tempLevel < nodesLevel[i] - 1)
/*  439:     */             {
/*  440: 692 */               tempNodePresent = false;
/*  441: 693 */               for (; k < this.graphMatrix.length; k++) {
/*  442: 694 */                 if (this.graphMatrix[tempnode][k] > 0)
/*  443:     */                 {
/*  444: 696 */                   tempNodePresent = true;
/*  445: 697 */                   break;
/*  446:     */                 }
/*  447:     */               }
/*  448: 700 */               if (tempNodePresent)
/*  449:     */               {
/*  450: 701 */                 tempnode = k;
/*  451: 702 */                 k += 1;
/*  452: 703 */                 tempLevel++;
/*  453:     */               }
/*  454: 705 */               else if (tempnode != n)
/*  455:     */               {
/*  456: 706 */                 tempnode = k - 1;
/*  457:     */               }
/*  458:     */             }
/*  459: 712 */             if (((GraphNode)this.m_nodes.get(tempnode)).nodeType == 1) {
/*  460: 713 */               ((GraphNode)this.m_nodes.get(tempnode)).nodeType = 2;
/*  461:     */             }
/*  462: 715 */             if (tempNodePresent)
/*  463:     */             {
/*  464: 717 */               this.graphMatrix[tempnode][i] = this.graphMatrix[n][i];
/*  465:     */               
/*  466:     */ 
/*  467:     */ 
/*  468:     */ 
/*  469:     */ 
/*  470:     */ 
/*  471:     */ 
/*  472:     */ 
/*  473:     */ 
/*  474:     */ 
/*  475:     */ 
/*  476:     */ 
/*  477: 730 */               this.graphMatrix[i][tempnode] = (-this.graphMatrix[n][i]);
/*  478:     */               
/*  479: 732 */               this.graphMatrix[n][i] = 0;
/*  480: 733 */               this.graphMatrix[i][n] = 0;
/*  481:     */             }
/*  482:     */             else
/*  483:     */             {
/*  484: 738 */               int len = this.graphMatrix.length;
/*  485: 739 */               int[][] tempMatrix = new int[this.graphMatrix.length + (nodesLevel[i] - nodesLevel[tempnode] - 1)][this.graphMatrix.length + (nodesLevel[i] - nodesLevel[tempnode] - 1)];
/*  486:     */               
/*  487:     */ 
/*  488: 742 */               int level = nodesLevel[tempnode] + 1;
/*  489: 743 */               copyMatrix(this.graphMatrix, tempMatrix);
/*  490:     */               
/*  491: 745 */               String s1 = new String("S" + tempCnt++);
/*  492:     */               
/*  493: 747 */               this.m_nodes.add(new GraphNode(s1, s1, 1));
/*  494:     */               
/*  495: 749 */               int[] temp3 = new int[this.nodeLevels[level].length + 1];
/*  496: 750 */               System.arraycopy(this.nodeLevels[level], 0, temp3, 0, this.nodeLevels[level].length);
/*  497:     */               
/*  498: 752 */               temp3[(temp3.length - 1)] = (this.m_nodes.size() - 1);
/*  499: 753 */               this.nodeLevels[level] = temp3;
/*  500: 754 */               temp3 = new int[this.m_nodes.size() + 1];
/*  501: 755 */               System.arraycopy(nodesLevel, 0, temp3, 0, nodesLevel.length);
/*  502: 756 */               temp3[(this.m_nodes.size() - 1)] = level;
/*  503: 757 */               nodesLevel = temp3;
/*  504: 758 */               level++;
/*  505: 765 */               for (int m = len; m < len + nodesLevel[i] - nodesLevel[tempnode] - 1 - 1; m++)
/*  506:     */               {
/*  507: 767 */                 String s2 = new String("S" + tempCnt++);
/*  508:     */                 
/*  509: 769 */                 this.m_nodes.add(new GraphNode(s2, s2, 1));
/*  510: 770 */                 temp3 = new int[this.nodeLevels[level].length + 1];
/*  511:     */                 
/*  512:     */ 
/*  513: 773 */                 System.arraycopy(this.nodeLevels[level], 0, temp3, 0, this.nodeLevels[level].length);
/*  514:     */                 
/*  515: 775 */                 temp3[(temp3.length - 1)] = (this.m_nodes.size() - 1);
/*  516: 776 */                 this.nodeLevels[level] = temp3;
/*  517: 777 */                 temp3 = new int[this.m_nodes.size() + 1];
/*  518: 778 */                 System.arraycopy(nodesLevel, 0, temp3, 0, nodesLevel.length);
/*  519: 779 */                 temp3[(this.m_nodes.size() - 1)] = level;
/*  520: 780 */                 nodesLevel = temp3;
/*  521: 781 */                 level++;
/*  522:     */                 
/*  523:     */ 
/*  524:     */ 
/*  525:     */ 
/*  526: 786 */                 tempMatrix[m][(m + 1)] = tempMatrix[n][i];
/*  527: 787 */                 if (m > len) {
/*  528: 791 */                   tempMatrix[m][(m - 1)] = (-1 * tempMatrix[n][i]);
/*  529:     */                 }
/*  530:     */               }
/*  531: 803 */               tempMatrix[m][i] = tempMatrix[n][i];
/*  532:     */               
/*  533:     */ 
/*  534:     */ 
/*  535:     */ 
/*  536:     */ 
/*  537: 809 */               tempMatrix[tempnode][len] = tempMatrix[n][i];
/*  538:     */               
/*  539:     */ 
/*  540:     */ 
/*  541:     */ 
/*  542:     */ 
/*  543: 815 */               tempMatrix[len][tempnode] = (-1 * tempMatrix[n][i]);
/*  544:     */               
/*  545:     */ 
/*  546:     */ 
/*  547:     */ 
/*  548:     */ 
/*  549: 821 */               tempMatrix[i][m] = (-1 * tempMatrix[n][i]);
/*  550: 822 */               if (m > len) {
/*  551: 829 */                 tempMatrix[m][(m - 1)] = (-1 * tempMatrix[n][i]);
/*  552:     */               }
/*  553: 833 */               tempMatrix[n][i] = 0;
/*  554:     */               
/*  555:     */ 
/*  556: 836 */               tempMatrix[i][n] = 0;
/*  557:     */               
/*  558: 838 */               this.graphMatrix = tempMatrix;
/*  559:     */             }
/*  560:     */           }
/*  561:     */           else
/*  562:     */           {
/*  563: 848 */             this.graphMatrix[i][n] = (-1 * this.graphMatrix[n][i]);
/*  564:     */           }
/*  565:     */         }
/*  566:     */       }
/*  567:     */     }
/*  568:     */   }
/*  569:     */   
/*  570:     */   private int indexOfElementInLevel(int element, int[] level)
/*  571:     */     throws Exception
/*  572:     */   {
/*  573: 864 */     for (int i = 0; i < level.length; i++) {
/*  574: 865 */       if (level[i] == element) {
/*  575: 866 */         return i;
/*  576:     */       }
/*  577:     */     }
/*  578: 869 */     throw new Exception("Error. Didn't find element " + ((GraphNode)this.m_nodes.get(element)).ID + " in level. Inspect code for " + "weka.gui.graphvisualizer.HierarchicalBCEngine");
/*  579:     */   }
/*  580:     */   
/*  581:     */   protected int crossings(int[][] levels)
/*  582:     */   {
/*  583: 880 */     int sum = 0;
/*  584: 882 */     for (int i = 0; i < levels.length - 1; i++)
/*  585:     */     {
/*  586: 885 */       MyList upper = new MyList(null);MyList lower = new MyList(null);
/*  587: 886 */       MyListNode[] lastOcrnce = new MyListNode[this.m_nodes.size()];
/*  588: 887 */       int[] edgeOcrnce = new int[this.m_nodes.size()];
/*  589:     */       
/*  590: 889 */       int j = 0;int uidx = 0;
/*  591: 889 */       for (int lidx = 0; j < levels[i].length + levels[(i + 1)].length; j++) {
/*  592: 890 */         if (((j % 2 == 0) && (uidx < levels[i].length)) || (lidx >= levels[(i + 1)].length))
/*  593:     */         {
/*  594: 892 */           int k1 = 0;int k2 = 0;int k3 = 0;
/*  595: 893 */           GraphNode n = (GraphNode)this.m_nodes.get(levels[i][uidx]);
/*  596: 896 */           if (lastOcrnce[levels[i][uidx]] != null)
/*  597:     */           {
/*  598: 897 */             MyListNode temp = new MyListNode(-1);
/*  599: 898 */             temp.next = upper.first;
/*  600:     */             try
/*  601:     */             {
/*  602:     */               do
/*  603:     */               {
/*  604: 901 */                 temp = temp.next;
/*  605: 902 */                 if (levels[i][uidx] == temp.n)
/*  606:     */                 {
/*  607: 903 */                   k1 += 1;
/*  608: 904 */                   k3 += k2;
/*  609:     */                   
/*  610: 906 */                   upper.remove(temp);
/*  611:     */                 }
/*  612:     */                 else
/*  613:     */                 {
/*  614: 908 */                   k2 += 1;
/*  615:     */                 }
/*  616: 910 */               } while (temp != lastOcrnce[levels[i][uidx]]);
/*  617:     */             }
/*  618:     */             catch (NullPointerException ex)
/*  619:     */             {
/*  620: 912 */               System.out.println("levels[i][uidx]: " + levels[i][uidx] + " which is: " + ((GraphNode)this.m_nodes.get(levels[i][uidx])).ID + " temp: " + temp + " upper.first: " + upper.first);
/*  621:     */               
/*  622:     */ 
/*  623: 915 */               ex.printStackTrace();
/*  624: 916 */               System.exit(-1);
/*  625:     */             }
/*  626: 919 */             lastOcrnce[levels[i][uidx]] = null;
/*  627: 920 */             sum = sum + k1 * lower.size() + k3;
/*  628:     */           }
/*  629: 924 */           for (int k = 0; k < n.edges.length; k++) {
/*  630: 925 */             if (n.edges[k][1] > 0) {
/*  631:     */               try
/*  632:     */               {
/*  633: 927 */                 if (indexOfElementInLevel(n.edges[k][0], levels[(i + 1)]) >= uidx) {
/*  634: 928 */                   edgeOcrnce[n.edges[k][0]] = 1;
/*  635:     */                 }
/*  636:     */               }
/*  637:     */               catch (Exception ex)
/*  638:     */               {
/*  639: 931 */                 ex.printStackTrace();
/*  640:     */               }
/*  641:     */             }
/*  642:     */           }
/*  643: 935 */           for (int k = 0; k < levels[(i + 1)].length; k++) {
/*  644: 936 */             if (edgeOcrnce[levels[(i + 1)][k]] == 1)
/*  645:     */             {
/*  646: 937 */               MyListNode temp = new MyListNode(levels[(i + 1)][k]);
/*  647:     */               
/*  648: 939 */               lower.add(temp);
/*  649: 940 */               lastOcrnce[levels[(i + 1)][k]] = temp;
/*  650: 941 */               edgeOcrnce[levels[(i + 1)][k]] = 0;
/*  651:     */             }
/*  652:     */           }
/*  653: 948 */           uidx++;
/*  654:     */         }
/*  655:     */         else
/*  656:     */         {
/*  657: 950 */           int k1 = 0;int k2 = 0;int k3 = 0;
/*  658: 951 */           GraphNode n = (GraphNode)this.m_nodes.get(levels[(i + 1)][lidx]);
/*  659: 954 */           if (lastOcrnce[levels[(i + 1)][lidx]] != null)
/*  660:     */           {
/*  661: 956 */             MyListNode temp = new MyListNode(-1);
/*  662: 957 */             temp.next = lower.first;
/*  663:     */             try
/*  664:     */             {
/*  665:     */               do
/*  666:     */               {
/*  667: 960 */                 temp = temp.next;
/*  668: 961 */                 if (levels[(i + 1)][lidx] == temp.n)
/*  669:     */                 {
/*  670: 962 */                   k1 += 1;
/*  671: 963 */                   k3 += k2;
/*  672: 964 */                   lower.remove(temp);
/*  673:     */                 }
/*  674:     */                 else
/*  675:     */                 {
/*  676: 967 */                   k2 += 1;
/*  677:     */                 }
/*  678: 973 */               } while (temp != lastOcrnce[levels[(i + 1)][lidx]]);
/*  679:     */             }
/*  680:     */             catch (NullPointerException ex)
/*  681:     */             {
/*  682: 975 */               System.out.print("levels[i+1][lidx]: " + levels[(i + 1)][lidx] + " which is: " + ((GraphNode)this.m_nodes.get(levels[(i + 1)][lidx])).ID + " temp: " + temp);
/*  683:     */               
/*  684:     */ 
/*  685: 978 */               System.out.println(" lower.first: " + lower.first);
/*  686: 979 */               ex.printStackTrace();
/*  687: 980 */               System.exit(-1);
/*  688:     */             }
/*  689: 983 */             lastOcrnce[levels[(i + 1)][lidx]] = null;
/*  690: 984 */             sum = sum + k1 * upper.size() + k3;
/*  691:     */           }
/*  692: 988 */           for (int k = 0; k < n.edges.length; k++) {
/*  693: 989 */             if (n.edges[k][1] < 0) {
/*  694:     */               try
/*  695:     */               {
/*  696: 991 */                 if (indexOfElementInLevel(n.edges[k][0], levels[i]) > lidx) {
/*  697: 992 */                   edgeOcrnce[n.edges[k][0]] = 1;
/*  698:     */                 }
/*  699:     */               }
/*  700:     */               catch (Exception ex)
/*  701:     */               {
/*  702: 995 */                 ex.printStackTrace();
/*  703:     */               }
/*  704:     */             }
/*  705:     */           }
/*  706: 999 */           for (int k = 0; k < levels[i].length; k++) {
/*  707:1000 */             if (edgeOcrnce[levels[i][k]] == 1)
/*  708:     */             {
/*  709:1001 */               MyListNode temp = new MyListNode(levels[i][k]);
/*  710:1002 */               upper.add(temp);
/*  711:1003 */               lastOcrnce[levels[i][k]] = temp;
/*  712:1004 */               edgeOcrnce[levels[i][k]] = 0;
/*  713:     */             }
/*  714:     */           }
/*  715:1012 */           lidx++;
/*  716:     */         }
/*  717:     */       }
/*  718:     */     }
/*  719:1018 */     return sum;
/*  720:     */   }
/*  721:     */   
/*  722:     */   protected void removeCycles()
/*  723:     */   {
/*  724:1027 */     int[] visited = new int[this.m_nodes.size()];
/*  725:1029 */     for (int i = 0; i < this.graphMatrix.length; i++) {
/*  726:1030 */       if (visited[i] == 0)
/*  727:     */       {
/*  728:1031 */         removeCycles2(i, visited);
/*  729:1032 */         visited[i] = 1;
/*  730:     */       }
/*  731:     */     }
/*  732:     */   }
/*  733:     */   
/*  734:     */   private void removeCycles2(int nindex, int[] visited)
/*  735:     */   {
/*  736:1042 */     visited[nindex] = 2;
/*  737:1043 */     for (int i = 0; i < this.graphMatrix[nindex].length; i++) {
/*  738:1044 */       if (this.graphMatrix[nindex][i] == 1) {
/*  739:1045 */         if (visited[i] == 0)
/*  740:     */         {
/*  741:1046 */           removeCycles2(i, visited);
/*  742:1047 */           visited[i] = 1;
/*  743:     */         }
/*  744:1048 */         else if (visited[i] == 2)
/*  745:     */         {
/*  746:1049 */           if (nindex == i)
/*  747:     */           {
/*  748:1050 */             this.graphMatrix[nindex][i] = 0;
/*  749:     */           }
/*  750:1051 */           else if (this.graphMatrix[i][nindex] == 1)
/*  751:     */           {
/*  752:1053 */             this.graphMatrix[i][nindex] = 3;
/*  753:1054 */             this.graphMatrix[nindex][i] = -3;
/*  754:     */           }
/*  755:     */           else
/*  756:     */           {
/*  757:1057 */             this.graphMatrix[i][nindex] = 2;
/*  758:1058 */             this.graphMatrix[nindex][i] = -2;
/*  759:     */           }
/*  760:     */         }
/*  761:     */       }
/*  762:     */     }
/*  763:     */   }
/*  764:     */   
/*  765:     */   protected void assignLevels(int[] levels, int depth, int i, int j)
/*  766:     */   {
/*  767:1071 */     if (i >= this.graphMatrix.length) {
/*  768:1072 */       return;
/*  769:     */     }
/*  770:1073 */     if (j >= this.graphMatrix[i].length) {
/*  771:1074 */       return;
/*  772:     */     }
/*  773:1076 */     if (this.graphMatrix[i][j] <= 0)
/*  774:     */     {
/*  775:1077 */       assignLevels(levels, depth, i, ++j);
/*  776:     */     }
/*  777:1078 */     else if ((this.graphMatrix[i][j] == 1) || (this.graphMatrix[i][j] == 3))
/*  778:     */     {
/*  779:1079 */       if (depth + 1 > levels[j])
/*  780:     */       {
/*  781:1080 */         levels[j] = (depth + 1);
/*  782:1081 */         assignLevels(levels, depth + 1, j, 0);
/*  783:     */       }
/*  784:1083 */       assignLevels(levels, depth, i, ++j);
/*  785:     */     }
/*  786:     */   }
/*  787:     */   
/*  788:     */   private int[][] minimizeCrossings(boolean reversed, int[][] nodeLevels)
/*  789:     */   {
/*  790:1095 */     if (!reversed)
/*  791:     */     {
/*  792:1096 */       for (int times = 0; times < 1; times++)
/*  793:     */       {
/*  794:1097 */         int[][] tempLevels = new int[nodeLevels.length][];
/*  795:     */         
/*  796:     */ 
/*  797:     */ 
/*  798:     */ 
/*  799:1102 */         copy2DArray(nodeLevels, tempLevels);
/*  800:1103 */         for (int i = 0; i < nodeLevels.length - 1; i++) {
/*  801:1104 */           phaseID(i, tempLevels);
/*  802:     */         }
/*  803:1106 */         if (crossings(tempLevels) < crossings(nodeLevels)) {
/*  804:1107 */           nodeLevels = tempLevels;
/*  805:     */         }
/*  806:1112 */         tempLevels = new int[nodeLevels.length][];
/*  807:1113 */         copy2DArray(nodeLevels, tempLevels);
/*  808:1114 */         for (int i = nodeLevels.length - 2; i >= 0; i--) {
/*  809:1115 */           phaseIU(i, tempLevels);
/*  810:     */         }
/*  811:1117 */         if (crossings(tempLevels) < crossings(nodeLevels)) {
/*  812:1118 */           nodeLevels = tempLevels;
/*  813:     */         }
/*  814:1123 */         tempLevels = new int[nodeLevels.length][];
/*  815:1124 */         copy2DArray(nodeLevels, tempLevels);
/*  816:1125 */         for (int i = 0; i < nodeLevels.length - 1; i++) {
/*  817:1126 */           phaseIID(i, tempLevels);
/*  818:     */         }
/*  819:1128 */         if (crossings(tempLevels) < crossings(nodeLevels)) {
/*  820:1129 */           nodeLevels = tempLevels;
/*  821:     */         }
/*  822:1137 */         tempLevels = new int[nodeLevels.length][];
/*  823:1138 */         copy2DArray(nodeLevels, tempLevels);
/*  824:1139 */         for (int i = nodeLevels.length - 2; i >= 0; i--) {
/*  825:1140 */           phaseIIU(i, tempLevels);
/*  826:     */         }
/*  827:1142 */         if (crossings(tempLevels) < crossings(nodeLevels)) {
/*  828:1143 */           nodeLevels = tempLevels;
/*  829:     */         }
/*  830:     */       }
/*  831:1151 */       return nodeLevels;
/*  832:     */     }
/*  833:1153 */     for (int times = 0; times < 1; times++)
/*  834:     */     {
/*  835:1154 */       int[][] tempLevels = new int[nodeLevels.length][];
/*  836:     */       
/*  837:     */ 
/*  838:     */ 
/*  839:     */ 
/*  840:1159 */       copy2DArray(nodeLevels, tempLevels);
/*  841:1160 */       for (int i = nodeLevels.length - 2; i >= 0; i--) {
/*  842:1161 */         phaseIU(i, tempLevels);
/*  843:     */       }
/*  844:1163 */       if (crossings(tempLevels) < crossings(nodeLevels)) {
/*  845:1164 */         nodeLevels = tempLevels;
/*  846:     */       }
/*  847:1170 */       tempLevels = new int[nodeLevels.length][];
/*  848:1171 */       copy2DArray(nodeLevels, tempLevels);
/*  849:1172 */       for (int i = 0; i < nodeLevels.length - 1; i++) {
/*  850:1173 */         phaseID(i, tempLevels);
/*  851:     */       }
/*  852:1175 */       if (crossings(tempLevels) < crossings(nodeLevels)) {
/*  853:1176 */         nodeLevels = tempLevels;
/*  854:     */       }
/*  855:1182 */       tempLevels = new int[nodeLevels.length][];
/*  856:1183 */       copy2DArray(nodeLevels, tempLevels);
/*  857:1184 */       for (int i = nodeLevels.length - 2; i >= 0; i--) {
/*  858:1185 */         phaseIIU(i, tempLevels);
/*  859:     */       }
/*  860:1187 */       if (crossings(tempLevels) < crossings(nodeLevels)) {
/*  861:1188 */         nodeLevels = tempLevels;
/*  862:     */       }
/*  863:1194 */       tempLevels = new int[nodeLevels.length][];
/*  864:1195 */       copy2DArray(nodeLevels, tempLevels);
/*  865:1196 */       for (int i = 0; i < nodeLevels.length - 1; i++) {
/*  866:1197 */         phaseIID(i, tempLevels);
/*  867:     */       }
/*  868:1199 */       if (crossings(tempLevels) < crossings(nodeLevels)) {
/*  869:1200 */         nodeLevels = tempLevels;
/*  870:     */       }
/*  871:     */     }
/*  872:1206 */     return nodeLevels;
/*  873:     */   }
/*  874:     */   
/*  875:     */   protected void phaseID(int lindex, int[][] levels)
/*  876:     */   {
/*  877:1218 */     float[] colBC = calcColBC(lindex, levels);
/*  878:     */     
/*  879:     */ 
/*  880:     */ 
/*  881:     */ 
/*  882:     */ 
/*  883:     */ 
/*  884:     */ 
/*  885:     */ 
/*  886:     */ 
/*  887:     */ 
/*  888:     */ 
/*  889:     */ 
/*  890:     */ 
/*  891:     */ 
/*  892:     */ 
/*  893:     */ 
/*  894:     */ 
/*  895:     */ 
/*  896:     */ 
/*  897:1238 */     isort(levels[(lindex + 1)], colBC);
/*  898:     */   }
/*  899:     */   
/*  900:     */   public void phaseIU(int lindex, int[][] levels)
/*  901:     */   {
/*  902:1259 */     float[] rowBC = calcRowBC(lindex, levels);
/*  903:     */     
/*  904:     */ 
/*  905:     */ 
/*  906:     */ 
/*  907:     */ 
/*  908:     */ 
/*  909:     */ 
/*  910:     */ 
/*  911:     */ 
/*  912:     */ 
/*  913:     */ 
/*  914:     */ 
/*  915:     */ 
/*  916:     */ 
/*  917:     */ 
/*  918:     */ 
/*  919:     */ 
/*  920:     */ 
/*  921:1278 */     isort(levels[lindex], rowBC);
/*  922:     */   }
/*  923:     */   
/*  924:     */   public void phaseIID(int lindex, int[][] levels)
/*  925:     */   {
/*  926:1295 */     float[] colBC = calcColBC(lindex, levels);
/*  927:1298 */     for (int i = 0; i < colBC.length - 1; i++) {
/*  928:1299 */       if (colBC[i] == colBC[(i + 1)])
/*  929:     */       {
/*  930:1302 */         int[][] tempLevels = new int[levels.length][];
/*  931:1303 */         copy2DArray(levels, tempLevels);
/*  932:     */         
/*  933:     */ 
/*  934:     */ 
/*  935:     */ 
/*  936:     */ 
/*  937:1309 */         int node1 = levels[(lindex + 1)][i];
/*  938:1310 */         int node2 = levels[(lindex + 1)][(i + 1)];
/*  939:1311 */         levels[(lindex + 1)][(i + 1)] = node1;
/*  940:1312 */         levels[(lindex + 1)][i] = node2;
/*  941:1314 */         for (int k = lindex + 1; k < levels.length - 1; k++) {
/*  942:1315 */           phaseID(k, levels);
/*  943:     */         }
/*  944:1319 */         if (crossings(levels) <= crossings(tempLevels))
/*  945:     */         {
/*  946:1322 */           copy2DArray(levels, tempLevels);
/*  947:     */         }
/*  948:     */         else
/*  949:     */         {
/*  950:1325 */           copy2DArray(tempLevels, levels);
/*  951:1326 */           levels[(lindex + 1)][(i + 1)] = node1;
/*  952:1327 */           levels[(lindex + 1)][i] = node2;
/*  953:     */         }
/*  954:1336 */         for (int k = levels.length - 2; k >= 0; k--) {
/*  955:1337 */           phaseIU(k, levels);
/*  956:     */         }
/*  957:1345 */         if (crossings(tempLevels) < crossings(levels)) {
/*  958:1346 */           copy2DArray(tempLevels, levels);
/*  959:     */         }
/*  960:     */       }
/*  961:     */     }
/*  962:     */   }
/*  963:     */   
/*  964:     */   public void phaseIIU(int lindex, int[][] levels)
/*  965:     */   {
/*  966:1363 */     float[] rowBC = calcRowBC(lindex, levels);
/*  967:1366 */     for (int i = 0; i < rowBC.length - 1; i++) {
/*  968:1367 */       if (rowBC[i] == rowBC[(i + 1)])
/*  969:     */       {
/*  970:1370 */         int[][] tempLevels = new int[levels.length][];
/*  971:1371 */         copy2DArray(levels, tempLevels);
/*  972:     */         
/*  973:     */ 
/*  974:     */ 
/*  975:     */ 
/*  976:1376 */         int node1 = levels[lindex][i];
/*  977:1377 */         int node2 = levels[lindex][(i + 1)];
/*  978:1378 */         levels[lindex][(i + 1)] = node1;
/*  979:1379 */         levels[lindex][i] = node2;
/*  980:1381 */         for (int k = lindex - 1; k >= 0; k--) {
/*  981:1382 */           phaseIU(k, levels);
/*  982:     */         }
/*  983:1384 */         if (crossings(levels) <= crossings(tempLevels))
/*  984:     */         {
/*  985:1387 */           copy2DArray(levels, tempLevels);
/*  986:     */         }
/*  987:     */         else
/*  988:     */         {
/*  989:1390 */           copy2DArray(tempLevels, levels);
/*  990:1391 */           levels[lindex][(i + 1)] = node1;
/*  991:1392 */           levels[lindex][i] = node2;
/*  992:     */         }
/*  993:1401 */         for (int k = 0; k < levels.length - 1; k++) {
/*  994:1402 */           phaseID(k, levels);
/*  995:     */         }
/*  996:1408 */         if (crossings(tempLevels) <= crossings(levels)) {
/*  997:1409 */           copy2DArray(tempLevels, levels);
/*  998:     */         }
/*  999:     */       }
/* 1000:     */     }
/* 1001:     */   }
/* 1002:     */   
/* 1003:     */   protected float[] calcRowBC(int lindex, int[][] levels)
/* 1004:     */   {
/* 1005:1424 */     float[] rowBC = new float[levels[lindex].length];
/* 1006:1427 */     for (int i = 0; i < levels[lindex].length; i++)
/* 1007:     */     {
/* 1008:1428 */       int sum = 0;
/* 1009:1429 */       GraphNode n = (GraphNode)this.m_nodes.get(levels[lindex][i]);
/* 1010:1431 */       for (int[] edge : n.edges) {
/* 1011:1432 */         if (edge[1] > 0)
/* 1012:     */         {
/* 1013:1433 */           sum++;
/* 1014:     */           try
/* 1015:     */           {
/* 1016:1435 */             rowBC[i] = (rowBC[i] + indexOfElementInLevel(edge[0], levels[(lindex + 1)]) + 1.0F);
/* 1017:     */           }
/* 1018:     */           catch (Exception ex)
/* 1019:     */           {
/* 1020:1438 */             return null;
/* 1021:     */           }
/* 1022:     */         }
/* 1023:     */       }
/* 1024:1442 */       if (rowBC[i] != 0.0F) {
/* 1025:1443 */         rowBC[i] /= sum;
/* 1026:     */       }
/* 1027:     */     }
/* 1028:1446 */     return rowBC;
/* 1029:     */   }
/* 1030:     */   
/* 1031:     */   protected float[] calcColBC(int lindex, int[][] levels)
/* 1032:     */   {
/* 1033:1453 */     float[] colBC = new float[levels[(lindex + 1)].length];
/* 1034:1456 */     for (int i = 0; i < levels[(lindex + 1)].length; i++)
/* 1035:     */     {
/* 1036:1457 */       int sum = 0;
/* 1037:1458 */       GraphNode n = (GraphNode)this.m_nodes.get(levels[(lindex + 1)][i]);
/* 1038:1460 */       for (int[] edge : n.edges) {
/* 1039:1461 */         if (edge[1] < 1)
/* 1040:     */         {
/* 1041:1462 */           sum++;
/* 1042:     */           try
/* 1043:     */           {
/* 1044:1464 */             colBC[i] = (colBC[i] + indexOfElementInLevel(edge[0], levels[lindex]) + 1.0F);
/* 1045:     */           }
/* 1046:     */           catch (Exception ex)
/* 1047:     */           {
/* 1048:1467 */             return null;
/* 1049:     */           }
/* 1050:     */         }
/* 1051:     */       }
/* 1052:1471 */       if (colBC[i] != 0.0F) {
/* 1053:1472 */         colBC[i] /= sum;
/* 1054:     */       }
/* 1055:     */     }
/* 1056:1475 */     return colBC;
/* 1057:     */   }
/* 1058:     */   
/* 1059:     */   protected void printMatrices(int[][] levels)
/* 1060:     */   {
/* 1061:1483 */     int i = 0;
/* 1062:1484 */     for (i = 0; i < levels.length - 1; i++)
/* 1063:     */     {
/* 1064:1485 */       float[] rowBC = null;
/* 1065:1486 */       float[] colBC = null;
/* 1066:     */       try
/* 1067:     */       {
/* 1068:1488 */         rowBC = calcRowBC(i, levels);
/* 1069:1489 */         colBC = calcColBC(i, levels);
/* 1070:     */       }
/* 1071:     */       catch (NullPointerException ne)
/* 1072:     */       {
/* 1073:1491 */         System.out.println("i: " + i + " levels.length: " + levels.length);
/* 1074:1492 */         ne.printStackTrace();
/* 1075:1493 */         return;
/* 1076:     */       }
/* 1077:1496 */       System.out.print("\nM" + (i + 1) + "\t");
/* 1078:1497 */       for (int j = 0; j < levels[(i + 1)].length; j++) {
/* 1079:1498 */         System.out.print(((GraphNode)this.m_nodes.get(levels[(i + 1)][j])).ID + " ");
/* 1080:     */       }
/* 1081:1501 */       System.out.println("");
/* 1082:1503 */       for (int j = 0; j < levels[i].length; j++)
/* 1083:     */       {
/* 1084:1504 */         System.out.print(((GraphNode)this.m_nodes.get(levels[i][j])).ID + "\t");
/* 1085:1506 */         for (int k = 0; k < levels[(i + 1)].length; k++) {
/* 1086:1508 */           System.out.print(this.graphMatrix[levels[i][j]][levels[(i + 1)][k]] + " ");
/* 1087:     */         }
/* 1088:1515 */         System.out.println(rowBC[j]);
/* 1089:     */       }
/* 1090:1517 */       System.out.print("\t");
/* 1091:1518 */       for (int k = 0; k < levels[(i + 1)].length; k++) {
/* 1092:1519 */         System.out.print(colBC[k] + " ");
/* 1093:     */       }
/* 1094:     */     }
/* 1095:1522 */     System.out.println("\nAt the end i: " + i + " levels.length: " + levels.length);
/* 1096:     */   }
/* 1097:     */   
/* 1098:     */   protected static void isort(int[] level, float[] BC)
/* 1099:     */   {
/* 1100:1551 */     for (int i = 0; i < BC.length - 1; i++)
/* 1101:     */     {
/* 1102:1553 */       int j = i;
/* 1103:1554 */       float temp = BC[(j + 1)];
/* 1104:1555 */       int temp2 = level[(j + 1)];
/* 1105:1556 */       if (temp != 0.0F)
/* 1106:     */       {
/* 1107:1559 */         int prej = j + 1;
/* 1108:1561 */         while ((j > -1) && ((temp < BC[j]) || (BC[j] == 0.0F))) {
/* 1109:1562 */           if (BC[j] == 0.0F)
/* 1110:     */           {
/* 1111:1563 */             j--;
/* 1112:     */           }
/* 1113:     */           else
/* 1114:     */           {
/* 1115:1566 */             BC[prej] = BC[j];
/* 1116:1567 */             level[prej] = level[j];
/* 1117:1568 */             prej = j;
/* 1118:1569 */             j--;
/* 1119:     */           }
/* 1120:     */         }
/* 1121:1573 */         BC[prej] = temp;
/* 1122:1574 */         level[prej] = temp2;
/* 1123:     */       }
/* 1124:     */     }
/* 1125:     */   }
/* 1126:     */   
/* 1127:     */   protected void copyMatrix(int[][] from, int[][] to)
/* 1128:     */   {
/* 1129:1585 */     for (int i = 0; i < from.length; i++) {
/* 1130:1586 */       for (int j = 0; j < from[i].length; j++) {
/* 1131:1587 */         to[i][j] = from[i][j];
/* 1132:     */       }
/* 1133:     */     }
/* 1134:     */   }
/* 1135:     */   
/* 1136:     */   protected void copy2DArray(int[][] from, int[][] to)
/* 1137:     */   {
/* 1138:1596 */     for (int i = 0; i < from.length; i++)
/* 1139:     */     {
/* 1140:1597 */       to[i] = new int[from[i].length];
/* 1141:1598 */       System.arraycopy(from[i], 0, to[i], 0, from[i].length);
/* 1142:     */     }
/* 1143:     */   }
/* 1144:     */   
/* 1145:     */   protected void naiveLayout()
/* 1146:     */   {
/* 1147:1617 */     if (this.nodeLevels == null) {
/* 1148:1618 */       makeProperHierarchy();
/* 1149:     */     }
/* 1150:1622 */     int i = 0;
/* 1151:1622 */     for (int temp = 0; i < this.nodeLevels.length; i++) {
/* 1152:1623 */       for (int j = 0; j < this.nodeLevels[i].length; j++)
/* 1153:     */       {
/* 1154:1624 */         temp = this.nodeLevels[i][j];
/* 1155:     */         
/* 1156:1626 */         GraphNode n = (GraphNode)this.m_nodes.get(temp);
/* 1157:1627 */         n.x = (j * this.m_nodeWidth);
/* 1158:1628 */         n.y = (i * 3 * this.m_nodeHeight);
/* 1159:     */       }
/* 1160:     */     }
/* 1161:     */   }
/* 1162:     */   
/* 1163:     */   protected int uConnectivity(int lindex, int eindex)
/* 1164:     */   {
/* 1165:1635 */     int n = 0;
/* 1166:1636 */     for (int i = 0; i < this.nodeLevels[(lindex - 1)].length; i++) {
/* 1167:1637 */       if (this.graphMatrix[this.nodeLevels[(lindex - 1)][i]][this.nodeLevels[lindex][eindex]] > 0) {
/* 1168:1638 */         n++;
/* 1169:     */       }
/* 1170:     */     }
/* 1171:1642 */     return n;
/* 1172:     */   }
/* 1173:     */   
/* 1174:     */   protected int lConnectivity(int lindex, int eindex)
/* 1175:     */   {
/* 1176:1646 */     int n = 0;
/* 1177:1647 */     for (int i = 0; i < this.nodeLevels[(lindex + 1)].length; i++) {
/* 1178:1648 */       if (this.graphMatrix[this.nodeLevels[lindex][eindex]][this.nodeLevels[(lindex + 1)][i]] > 0) {
/* 1179:1649 */         n++;
/* 1180:     */       }
/* 1181:     */     }
/* 1182:1653 */     return n;
/* 1183:     */   }
/* 1184:     */   
/* 1185:     */   protected int uBCenter(int lindex, int eindex, int[] horPositions)
/* 1186:     */   {
/* 1187:1657 */     int sum = 0;
/* 1188:1659 */     for (int i = 0; i < this.nodeLevels[(lindex - 1)].length; i++) {
/* 1189:1660 */       if (this.graphMatrix[this.nodeLevels[(lindex - 1)][i]][this.nodeLevels[lindex][eindex]] > 0) {
/* 1190:1661 */         sum += horPositions[this.nodeLevels[(lindex - 1)][i]];
/* 1191:     */       }
/* 1192:     */     }
/* 1193:1664 */     if (sum != 0) {
/* 1194:1668 */       sum /= uConnectivity(lindex, eindex);
/* 1195:     */     }
/* 1196:1670 */     return sum;
/* 1197:     */   }
/* 1198:     */   
/* 1199:     */   protected int lBCenter(int lindex, int eindex, int[] horPositions)
/* 1200:     */   {
/* 1201:1674 */     int sum = 0;
/* 1202:1676 */     for (int i = 0; i < this.nodeLevels[(lindex + 1)].length; i++) {
/* 1203:1677 */       if (this.graphMatrix[this.nodeLevels[lindex][eindex]][this.nodeLevels[(lindex + 1)][i]] > 0) {
/* 1204:1678 */         sum += horPositions[this.nodeLevels[(lindex + 1)][i]];
/* 1205:     */       }
/* 1206:     */     }
/* 1207:1681 */     if (sum != 0) {
/* 1208:1682 */       sum /= lConnectivity(lindex, eindex);
/* 1209:     */     }
/* 1210:1684 */     return sum;
/* 1211:     */   }
/* 1212:     */   
/* 1213:     */   protected void priorityLayout1()
/* 1214:     */   {
/* 1215:1693 */     int[] horPositions = new int[this.m_nodes.size()];
/* 1216:1694 */     int maxCount = 0;
/* 1217:1696 */     for (int i = 0; i < this.nodeLevels.length; i++)
/* 1218:     */     {
/* 1219:1697 */       int count = 0;
/* 1220:1698 */       for (int j = 0; j < this.nodeLevels[i].length; j++)
/* 1221:     */       {
/* 1222:1699 */         horPositions[this.nodeLevels[i][j]] = j;
/* 1223:1700 */         count++;
/* 1224:     */       }
/* 1225:1702 */       if (count > maxCount) {
/* 1226:1703 */         maxCount = count;
/* 1227:     */       }
/* 1228:     */     }
/* 1229:1709 */     for (int i = 1; i < this.nodeLevels.length; i++)
/* 1230:     */     {
/* 1231:1710 */       int[] priorities = new int[this.nodeLevels[i].length];
/* 1232:1711 */       int[] BC = new int[this.nodeLevels[i].length];
/* 1233:1712 */       for (int j = 0; j < this.nodeLevels[i].length; j++)
/* 1234:     */       {
/* 1235:1713 */         if (((GraphNode)this.m_nodes.get(this.nodeLevels[i][j])).ID.startsWith("S")) {
/* 1236:1714 */           priorities[j] = (maxCount + 1);
/* 1237:     */         } else {
/* 1238:1716 */           priorities[j] = uConnectivity(i, j);
/* 1239:     */         }
/* 1240:1718 */         BC[j] = uBCenter(i, j, horPositions);
/* 1241:     */       }
/* 1242:1725 */       priorityLayout2(this.nodeLevels[i], priorities, BC, horPositions);
/* 1243:     */     }
/* 1244:1741 */     for (int i = this.nodeLevels.length - 2; i >= 0; i--)
/* 1245:     */     {
/* 1246:1742 */       int[] priorities = new int[this.nodeLevels[i].length];
/* 1247:1743 */       int[] BC = new int[this.nodeLevels[i].length];
/* 1248:1744 */       for (int j = 0; j < this.nodeLevels[i].length; j++)
/* 1249:     */       {
/* 1250:1745 */         if (((GraphNode)this.m_nodes.get(this.nodeLevels[i][j])).ID.startsWith("S")) {
/* 1251:1746 */           priorities[j] = (maxCount + 1);
/* 1252:     */         } else {
/* 1253:1748 */           priorities[j] = lConnectivity(i, j);
/* 1254:     */         }
/* 1255:1750 */         BC[j] = lBCenter(i, j, horPositions);
/* 1256:     */       }
/* 1257:1752 */       priorityLayout2(this.nodeLevels[i], priorities, BC, horPositions);
/* 1258:     */     }
/* 1259:1768 */     for (int i = 2; i < this.nodeLevels.length; i++)
/* 1260:     */     {
/* 1261:1769 */       int[] priorities = new int[this.nodeLevels[i].length];
/* 1262:1770 */       int[] BC = new int[this.nodeLevels[i].length];
/* 1263:1771 */       for (int j = 0; j < this.nodeLevels[i].length; j++)
/* 1264:     */       {
/* 1265:1772 */         if (((GraphNode)this.m_nodes.get(this.nodeLevels[i][j])).ID.startsWith("S")) {
/* 1266:1773 */           priorities[j] = (maxCount + 1);
/* 1267:     */         } else {
/* 1268:1775 */           priorities[j] = uConnectivity(i, j);
/* 1269:     */         }
/* 1270:1777 */         BC[j] = uBCenter(i, j, horPositions);
/* 1271:     */       }
/* 1272:1784 */       priorityLayout2(this.nodeLevels[i], priorities, BC, horPositions);
/* 1273:     */     }
/* 1274:1798 */     int minPosition = horPositions[0];
/* 1275:1800 */     for (int horPosition : horPositions) {
/* 1276:1801 */       if (horPosition < minPosition) {
/* 1277:1802 */         minPosition = horPosition;
/* 1278:     */       }
/* 1279:     */     }
/* 1280:1805 */     if (minPosition < 0)
/* 1281:     */     {
/* 1282:1806 */       minPosition *= -1;
/* 1283:1807 */       for (int i = 0; i < horPositions.length; i++) {
/* 1284:1809 */         horPositions[i] += minPosition;
/* 1285:     */       }
/* 1286:     */     }
/* 1287:1815 */     int i = 0;
/* 1288:1815 */     for (int temp = 0; i < this.nodeLevels.length; i++) {
/* 1289:1816 */       for (int j = 0; j < this.nodeLevels[i].length; j++)
/* 1290:     */       {
/* 1291:1817 */         temp = this.nodeLevels[i][j];
/* 1292:     */         
/* 1293:1819 */         GraphNode n = (GraphNode)this.m_nodes.get(temp);
/* 1294:1820 */         n.x = (horPositions[temp] * this.m_nodeWidth);
/* 1295:1821 */         n.y = (i * 3 * this.m_nodeHeight);
/* 1296:     */       }
/* 1297:     */     }
/* 1298:     */   }
/* 1299:     */   
/* 1300:     */   private void priorityLayout2(int[] level, int[] priorities, int[] bCenters, int[] horPositions)
/* 1301:     */   {
/* 1302:1834 */     int[] descOrder = new int[priorities.length];
/* 1303:     */     
/* 1304:     */ 
/* 1305:1837 */     descOrder[0] = 0;
/* 1306:1838 */     for (int i = 0; i < priorities.length - 1; i++)
/* 1307:     */     {
/* 1308:1839 */       int j = i;
/* 1309:1840 */       int temp = i + 1;
/* 1310:1842 */       while ((j > -1) && (priorities[descOrder[j]] < priorities[temp]))
/* 1311:     */       {
/* 1312:1843 */         descOrder[(j + 1)] = descOrder[j];
/* 1313:1844 */         j--;
/* 1314:     */       }
/* 1315:1846 */       j++;
/* 1316:1847 */       descOrder[j] = temp;
/* 1317:     */     }
/* 1318:1857 */     for (int k = 0; k < descOrder.length; k++) {
/* 1319:1858 */       for (int i = 0; i < descOrder.length; i++)
/* 1320:     */       {
/* 1321:1860 */         int leftCount = 0;int rightCount = 0;
/* 1322:1861 */         for (int j = 0; j < priorities.length; j++) {
/* 1323:1862 */           if (horPositions[level[descOrder[i]]] > horPositions[level[j]]) {
/* 1324:1863 */             leftCount++;
/* 1325:1864 */           } else if (horPositions[level[descOrder[i]]] < horPositions[level[j]]) {
/* 1326:1865 */             rightCount++;
/* 1327:     */           }
/* 1328:     */         }
/* 1329:1868 */         int[] leftNodes = new int[leftCount];
/* 1330:1869 */         int[] rightNodes = new int[rightCount];
/* 1331:     */         
/* 1332:1871 */         int j = 0;int l = 0;
/* 1333:1871 */         for (int r = 0; j < priorities.length; j++) {
/* 1334:1872 */           if (horPositions[level[descOrder[i]]] > horPositions[level[j]]) {
/* 1335:1873 */             leftNodes[(l++)] = j;
/* 1336:1874 */           } else if (horPositions[level[descOrder[i]]] < horPositions[level[j]]) {
/* 1337:1875 */             rightNodes[(r++)] = j;
/* 1338:     */           }
/* 1339:     */         }
/* 1340:1881 */         while (Math.abs(horPositions[level[descOrder[i]]] - 1 - bCenters[descOrder[i]]) < Math.abs(horPositions[level[descOrder[i]]] - bCenters[descOrder[i]]))
/* 1341:     */         {
/* 1342:1885 */           int temp = horPositions[level[descOrder[i]]];
/* 1343:1886 */           boolean cantMove = false;
/* 1344:1888 */           for (int j = leftNodes.length - 1; j >= 0; j--)
/* 1345:     */           {
/* 1346:1889 */             if (temp - horPositions[level[leftNodes[j]]] > 1) {
/* 1347:     */               break;
/* 1348:     */             }
/* 1349:1891 */             if (priorities[descOrder[i]] <= priorities[leftNodes[j]])
/* 1350:     */             {
/* 1351:1892 */               cantMove = true;
/* 1352:1893 */               break;
/* 1353:     */             }
/* 1354:1895 */             temp = horPositions[level[leftNodes[j]]];
/* 1355:     */           }
/* 1356:1901 */           if (cantMove) {
/* 1357:     */             break;
/* 1358:     */           }
/* 1359:1905 */           temp = horPositions[level[descOrder[i]]] - 1;
/* 1360:1907 */           for (int j = leftNodes.length - 1; j >= 0; j--) {
/* 1361:1908 */             if (temp == horPositions[level[leftNodes[j]]])
/* 1362:     */             {
/* 1363:1913 */               int tmp482_481 = (horPositions[level[leftNodes[j]]] - 1);temp = tmp482_481;horPositions[level[leftNodes[j]]] = tmp482_481;
/* 1364:     */             }
/* 1365:     */           }
/* 1366:1921 */           horPositions[level[descOrder[i]]] -= 1;
/* 1367:     */         }
/* 1368:1926 */         while (Math.abs(horPositions[level[descOrder[i]]] + 1 - bCenters[descOrder[i]]) < Math.abs(horPositions[level[descOrder[i]]] - bCenters[descOrder[i]]))
/* 1369:     */         {
/* 1370:1929 */           int temp = horPositions[level[descOrder[i]]];
/* 1371:1930 */           boolean cantMove = false;
/* 1372:1932 */           for (int rightNode : rightNodes)
/* 1373:     */           {
/* 1374:1933 */             if (horPositions[level[rightNode]] - temp > 1) {
/* 1375:     */               break;
/* 1376:     */             }
/* 1377:1935 */             if (priorities[descOrder[i]] <= priorities[rightNode])
/* 1378:     */             {
/* 1379:1936 */               cantMove = true;
/* 1380:1937 */               break;
/* 1381:     */             }
/* 1382:1939 */             temp = horPositions[level[rightNode]];
/* 1383:     */           }
/* 1384:1945 */           if (cantMove) {
/* 1385:     */             break;
/* 1386:     */           }
/* 1387:1949 */           temp = horPositions[level[descOrder[i]]] + 1;
/* 1388:1951 */           for (int j = 0; j < rightNodes.length; j++) {
/* 1389:1952 */             if (temp == horPositions[level[rightNodes[j]]])
/* 1390:     */             {
/* 1391:1957 */               int tmp726_725 = (horPositions[level[rightNodes[j]]] + 1);temp = tmp726_725;horPositions[level[rightNodes[j]]] = tmp726_725;
/* 1392:     */             }
/* 1393:     */           }
/* 1394:1964 */           horPositions[level[descOrder[i]]] += 1;
/* 1395:     */         }
/* 1396:     */       }
/* 1397:     */     }
/* 1398:     */   }
/* 1399:     */   
/* 1400:     */   private class MyList
/* 1401:     */   {
/* 1402:     */     int size;
/* 1403:1976 */     HierarchicalBCEngine.MyListNode first = null;
/* 1404:1977 */     HierarchicalBCEngine.MyListNode last = null;
/* 1405:     */     
/* 1406:     */     private MyList() {}
/* 1407:     */     
/* 1408:     */     public void add(HierarchicalBCEngine.MyListNode n)
/* 1409:     */     {
/* 1410:1980 */       if (this.first == null)
/* 1411:     */       {
/* 1412:1981 */         this.first = (this.last = n);
/* 1413:     */       }
/* 1414:1982 */       else if (this.last.next == null)
/* 1415:     */       {
/* 1416:1983 */         this.last.next = n;
/* 1417:1984 */         this.last.next.previous = this.last;
/* 1418:1985 */         this.last = this.last.next;
/* 1419:     */       }
/* 1420:     */       else
/* 1421:     */       {
/* 1422:1987 */         System.err.println("Error shouldn't be in here. Check MyList code");
/* 1423:1988 */         this.size -= 1;
/* 1424:     */       }
/* 1425:1991 */       this.size += 1;
/* 1426:     */     }
/* 1427:     */     
/* 1428:     */     public void remove(HierarchicalBCEngine.MyListNode n)
/* 1429:     */     {
/* 1430:1995 */       if (n.previous != null) {
/* 1431:1996 */         n.previous.next = n.next;
/* 1432:     */       }
/* 1433:1998 */       if (n.next != null) {
/* 1434:1999 */         n.next.previous = n.previous;
/* 1435:     */       }
/* 1436:2001 */       if (this.last == n) {
/* 1437:2002 */         this.last = n.previous;
/* 1438:     */       }
/* 1439:2004 */       if (this.first == n) {
/* 1440:2005 */         this.first = n.next;
/* 1441:     */       }
/* 1442:2008 */       this.size -= 1;
/* 1443:     */     }
/* 1444:     */     
/* 1445:     */     public int size()
/* 1446:     */     {
/* 1447:2012 */       return this.size;
/* 1448:     */     }
/* 1449:     */   }
/* 1450:     */   
/* 1451:     */   private class MyListNode
/* 1452:     */   {
/* 1453:     */     int n;
/* 1454:     */     MyListNode next;
/* 1455:     */     MyListNode previous;
/* 1456:     */     
/* 1457:     */     public MyListNode(int i)
/* 1458:     */     {
/* 1459:2022 */       this.n = i;
/* 1460:2023 */       this.next = null;
/* 1461:2024 */       this.previous = null;
/* 1462:     */     }
/* 1463:     */   }
/* 1464:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.graphvisualizer.HierarchicalBCEngine
 * JD-Core Version:    0.7.0.1
 */