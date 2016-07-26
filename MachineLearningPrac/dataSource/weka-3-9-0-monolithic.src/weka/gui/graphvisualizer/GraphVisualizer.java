/*    1:     */ package weka.gui.graphvisualizer;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Color;
/*    5:     */ import java.awt.Container;
/*    6:     */ import java.awt.Dialog.ModalityType;
/*    7:     */ import java.awt.Dimension;
/*    8:     */ import java.awt.Font;
/*    9:     */ import java.awt.FontMetrics;
/*   10:     */ import java.awt.Frame;
/*   11:     */ import java.awt.Graphics;
/*   12:     */ import java.awt.Graphics2D;
/*   13:     */ import java.awt.GridBagConstraints;
/*   14:     */ import java.awt.GridBagLayout;
/*   15:     */ import java.awt.Insets;
/*   16:     */ import java.awt.LayoutManager;
/*   17:     */ import java.awt.Point;
/*   18:     */ import java.awt.Rectangle;
/*   19:     */ import java.awt.RenderingHints;
/*   20:     */ import java.awt.event.ActionEvent;
/*   21:     */ import java.awt.event.ActionListener;
/*   22:     */ import java.awt.event.MouseAdapter;
/*   23:     */ import java.awt.event.MouseEvent;
/*   24:     */ import java.awt.event.MouseMotionAdapter;
/*   25:     */ import java.io.File;
/*   26:     */ import java.io.FileInputStream;
/*   27:     */ import java.io.FileReader;
/*   28:     */ import java.io.IOException;
/*   29:     */ import java.io.InputStream;
/*   30:     */ import java.io.PrintStream;
/*   31:     */ import java.io.Reader;
/*   32:     */ import java.net.URL;
/*   33:     */ import java.util.ArrayList;
/*   34:     */ import java.util.Vector;
/*   35:     */ import javax.swing.BorderFactory;
/*   36:     */ import javax.swing.ImageIcon;
/*   37:     */ import javax.swing.JButton;
/*   38:     */ import javax.swing.JCheckBox;
/*   39:     */ import javax.swing.JDialog;
/*   40:     */ import javax.swing.JFileChooser;
/*   41:     */ import javax.swing.JFrame;
/*   42:     */ import javax.swing.JLabel;
/*   43:     */ import javax.swing.JOptionPane;
/*   44:     */ import javax.swing.JPanel;
/*   45:     */ import javax.swing.JScrollPane;
/*   46:     */ import javax.swing.JTable;
/*   47:     */ import javax.swing.JTextField;
/*   48:     */ import javax.swing.JToolBar;
/*   49:     */ import javax.swing.table.AbstractTableModel;
/*   50:     */ import weka.core.logging.Logger;
/*   51:     */ import weka.core.logging.Logger.Level;
/*   52:     */ import weka.gui.ExtensionFileFilter;
/*   53:     */ import weka.gui.visualize.PrintablePanel;
/*   54:     */ 
/*   55:     */ public class GraphVisualizer
/*   56:     */   extends JPanel
/*   57:     */   implements GraphConstants, LayoutCompleteEventListener
/*   58:     */ {
/*   59:     */   private static final long serialVersionUID = -2038911085935515624L;
/*   60: 105 */   protected ArrayList<GraphNode> m_nodes = new ArrayList();
/*   61: 107 */   protected ArrayList<GraphEdge> m_edges = new ArrayList();
/*   62:     */   protected LayoutEngine m_le;
/*   63:     */   protected GraphPanel m_gp;
/*   64:     */   protected String graphID;
/*   65:     */   protected JButton m_jBtSave;
/*   66: 124 */   private final String ICONPATH = "weka/gui/graphvisualizer/icons/";
/*   67: 126 */   private final FontMetrics fm = getFontMetrics(getFont());
/*   68: 127 */   private double scale = 1.0D;
/*   69: 128 */   private int nodeHeight = 2 * this.fm.getHeight();
/*   70: 128 */   private int nodeWidth = 24;
/*   71: 129 */   private int paddedNodeWidth = 32;
/*   72: 131 */   private final JTextField jTfNodeWidth = new JTextField(3);
/*   73: 133 */   private final JTextField jTfNodeHeight = new JTextField(3);
/*   74:     */   private final JButton jBtLayout;
/*   75: 140 */   private int maxStringWidth = 0;
/*   76: 142 */   private final int[] zoomPercents = { 10, 25, 50, 75, 100, 125, 150, 175, 200, 225, 250, 275, 300, 350, 400, 450, 500, 550, 600, 650, 700, 800, 900, 999 };
/*   77:     */   JScrollPane m_js;
/*   78:     */   
/*   79:     */   public GraphVisualizer()
/*   80:     */   {
/*   81: 153 */     this.m_gp = new GraphPanel();
/*   82: 154 */     this.m_js = new JScrollPane(this.m_gp);
/*   83:     */     
/*   84:     */ 
/*   85:     */ 
/*   86: 158 */     this.m_le = new HierarchicalBCEngine(this.m_nodes, this.m_edges, this.paddedNodeWidth, this.nodeHeight);
/*   87:     */     
/*   88: 160 */     this.m_le.addLayoutCompleteEventListener(this);
/*   89:     */     
/*   90: 162 */     this.m_jBtSave = new JButton();
/*   91: 163 */     URL tempURL = ClassLoader.getSystemResource("weka/gui/graphvisualizer/icons/save.gif");
/*   92: 164 */     if (tempURL != null) {
/*   93: 165 */       this.m_jBtSave.setIcon(new ImageIcon(tempURL));
/*   94:     */     } else {
/*   95: 167 */       System.err.println("weka/gui/graphvisualizer/icons/save.gif not found for weka.gui.graphvisualizer.Graph");
/*   96:     */     }
/*   97: 170 */     this.m_jBtSave.setToolTipText("Save Graph");
/*   98: 171 */     this.m_jBtSave.addActionListener(new ActionListener()
/*   99:     */     {
/*  100:     */       public void actionPerformed(ActionEvent ae)
/*  101:     */       {
/*  102: 174 */         JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
/*  103: 175 */         ExtensionFileFilter ef1 = new ExtensionFileFilter(".dot", "DOT files");
/*  104: 176 */         ExtensionFileFilter ef2 = new ExtensionFileFilter(".xml", "XML BIF files");
/*  105:     */         
/*  106: 178 */         fc.addChoosableFileFilter(ef1);
/*  107: 179 */         fc.addChoosableFileFilter(ef2);
/*  108: 180 */         fc.setDialogTitle("Save Graph As");
/*  109: 181 */         int rval = fc.showSaveDialog(GraphVisualizer.this);
/*  110: 183 */         if (rval == 0) {
/*  111: 186 */           if (fc.getFileFilter() == ef2)
/*  112:     */           {
/*  113: 187 */             String filename = fc.getSelectedFile().toString();
/*  114: 188 */             if (!filename.endsWith(".xml")) {
/*  115: 189 */               filename = filename.concat(".xml");
/*  116:     */             }
/*  117: 191 */             BIFParser.writeXMLBIF03(filename, GraphVisualizer.this.graphID, GraphVisualizer.this.m_nodes, GraphVisualizer.this.m_edges);
/*  118:     */           }
/*  119:     */           else
/*  120:     */           {
/*  121: 193 */             String filename = fc.getSelectedFile().toString();
/*  122: 194 */             if (!filename.endsWith(".dot")) {
/*  123: 195 */               filename = filename.concat(".dot");
/*  124:     */             }
/*  125: 197 */             DotParser.writeDOT(filename, GraphVisualizer.this.graphID, GraphVisualizer.this.m_nodes, GraphVisualizer.this.m_edges);
/*  126:     */           }
/*  127:     */         }
/*  128:     */       }
/*  129: 202 */     });
/*  130: 203 */     final JButton jBtZoomIn = new JButton();
/*  131: 204 */     tempURL = ClassLoader.getSystemResource("weka/gui/graphvisualizer/icons/zoomin.gif");
/*  132: 205 */     if (tempURL != null) {
/*  133: 206 */       jBtZoomIn.setIcon(new ImageIcon(tempURL));
/*  134:     */     } else {
/*  135: 208 */       System.err.println("weka/gui/graphvisualizer/icons/zoomin.gif not found for weka.gui.graphvisualizer.Graph");
/*  136:     */     }
/*  137: 211 */     jBtZoomIn.setToolTipText("Zoom In");
/*  138:     */     
/*  139: 213 */     final JButton jBtZoomOut = new JButton();
/*  140: 214 */     tempURL = ClassLoader.getSystemResource("weka/gui/graphvisualizer/icons/zoomout.gif");
/*  141: 215 */     if (tempURL != null) {
/*  142: 216 */       jBtZoomOut.setIcon(new ImageIcon(tempURL));
/*  143:     */     } else {
/*  144: 218 */       System.err.println("weka/gui/graphvisualizer/icons/zoomout.gif not found for weka.gui.graphvisualizer.Graph");
/*  145:     */     }
/*  146: 221 */     jBtZoomOut.setToolTipText("Zoom Out");
/*  147:     */     
/*  148: 223 */     final JTextField jTfZoom = new JTextField("100%");
/*  149: 224 */     jTfZoom.setMinimumSize(jTfZoom.getPreferredSize());
/*  150: 225 */     jTfZoom.setHorizontalAlignment(0);
/*  151: 226 */     jTfZoom.setToolTipText("Zoom");
/*  152:     */     
/*  153: 228 */     jTfZoom.addActionListener(new ActionListener()
/*  154:     */     {
/*  155:     */       public void actionPerformed(ActionEvent ae)
/*  156:     */       {
/*  157: 231 */         JTextField jt = (JTextField)ae.getSource();
/*  158:     */         try
/*  159:     */         {
/*  160: 233 */           int i = -1;
/*  161: 234 */           i = jt.getText().indexOf('%');
/*  162: 235 */           if (i == -1) {
/*  163: 236 */             i = Integer.parseInt(jt.getText());
/*  164:     */           } else {
/*  165: 238 */             i = Integer.parseInt(jt.getText().substring(0, i));
/*  166:     */           }
/*  167: 241 */           if (i <= 999) {
/*  168: 242 */             GraphVisualizer.this.scale = (i / 100.0D);
/*  169:     */           }
/*  170: 245 */           jt.setText((int)(GraphVisualizer.this.scale * 100.0D) + "%");
/*  171: 247 */           if (GraphVisualizer.this.scale > 0.1D)
/*  172:     */           {
/*  173: 248 */             if (!jBtZoomOut.isEnabled()) {
/*  174: 249 */               jBtZoomOut.setEnabled(true);
/*  175:     */             }
/*  176:     */           }
/*  177:     */           else {
/*  178: 252 */             jBtZoomOut.setEnabled(false);
/*  179:     */           }
/*  180: 254 */           if (GraphVisualizer.this.scale < 9.99D)
/*  181:     */           {
/*  182: 255 */             if (!jBtZoomIn.isEnabled()) {
/*  183: 256 */               jBtZoomIn.setEnabled(true);
/*  184:     */             }
/*  185:     */           }
/*  186:     */           else {
/*  187: 259 */             jBtZoomIn.setEnabled(false);
/*  188:     */           }
/*  189: 262 */           GraphVisualizer.this.setAppropriateSize();
/*  190:     */           
/*  191: 264 */           GraphVisualizer.this.m_gp.repaint();
/*  192: 265 */           GraphVisualizer.this.m_gp.invalidate();
/*  193: 266 */           GraphVisualizer.this.m_js.revalidate();
/*  194:     */         }
/*  195:     */         catch (NumberFormatException ne)
/*  196:     */         {
/*  197: 268 */           JOptionPane.showMessageDialog(GraphVisualizer.this.getParent(), "Invalid integer entered for zoom.", "Error", 0);
/*  198:     */           
/*  199:     */ 
/*  200: 271 */           jt.setText(GraphVisualizer.this.scale * 100.0D + "%");
/*  201:     */         }
/*  202:     */       }
/*  203: 275 */     });
/*  204: 276 */     jBtZoomIn.addActionListener(new ActionListener()
/*  205:     */     {
/*  206:     */       public void actionPerformed(ActionEvent ae)
/*  207:     */       {
/*  208: 279 */         int i = 0;int s = (int)(GraphVisualizer.this.scale * 100.0D);
/*  209: 280 */         if (s < 300) {
/*  210: 281 */           i = s / 25;
/*  211: 282 */         } else if (s < 700) {
/*  212: 283 */           i = 6 + s / 50;
/*  213:     */         } else {
/*  214: 285 */           i = 13 + s / 100;
/*  215:     */         }
/*  216: 288 */         if (s >= 999)
/*  217:     */         {
/*  218: 289 */           JButton b = (JButton)ae.getSource();
/*  219: 290 */           b.setEnabled(false);
/*  220: 291 */           return;
/*  221:     */         }
/*  222: 292 */         if (s >= 10)
/*  223:     */         {
/*  224: 293 */           if (i >= 22)
/*  225:     */           {
/*  226: 294 */             JButton b = (JButton)ae.getSource();
/*  227: 295 */             b.setEnabled(false);
/*  228:     */           }
/*  229: 297 */           if ((s == 10) && (!jBtZoomOut.isEnabled())) {
/*  230: 298 */             jBtZoomOut.setEnabled(true);
/*  231:     */           }
/*  232: 301 */           jTfZoom.setText(GraphVisualizer.this.zoomPercents[(i + 1)] + "%");
/*  233: 302 */           GraphVisualizer.this.scale = (GraphVisualizer.this.zoomPercents[(i + 1)] / 100.0D);
/*  234:     */         }
/*  235:     */         else
/*  236:     */         {
/*  237: 304 */           if (!jBtZoomOut.isEnabled()) {
/*  238: 305 */             jBtZoomOut.setEnabled(true);
/*  239:     */           }
/*  240: 308 */           jTfZoom.setText(GraphVisualizer.this.zoomPercents[0] + "%");
/*  241: 309 */           GraphVisualizer.this.scale = (GraphVisualizer.this.zoomPercents[0] / 100.0D);
/*  242:     */         }
/*  243: 311 */         GraphVisualizer.this.setAppropriateSize();
/*  244: 312 */         GraphVisualizer.this.m_gp.repaint();
/*  245: 313 */         GraphVisualizer.this.m_gp.invalidate();
/*  246: 314 */         GraphVisualizer.this.m_js.revalidate();
/*  247:     */       }
/*  248: 317 */     });
/*  249: 318 */     jBtZoomOut.addActionListener(new ActionListener()
/*  250:     */     {
/*  251:     */       public void actionPerformed(ActionEvent ae)
/*  252:     */       {
/*  253: 321 */         int i = 0;int s = (int)(GraphVisualizer.this.scale * 100.0D);
/*  254: 322 */         if (s < 300) {
/*  255: 323 */           i = (int)Math.ceil(s / 25.0D);
/*  256: 324 */         } else if (s < 700) {
/*  257: 325 */           i = 6 + (int)Math.ceil(s / 50.0D);
/*  258:     */         } else {
/*  259: 327 */           i = 13 + (int)Math.ceil(s / 100.0D);
/*  260:     */         }
/*  261: 330 */         if (s <= 10)
/*  262:     */         {
/*  263: 331 */           JButton b = (JButton)ae.getSource();
/*  264: 332 */           b.setEnabled(false);
/*  265:     */         }
/*  266: 333 */         else if (s < 999)
/*  267:     */         {
/*  268: 334 */           if (i <= 1)
/*  269:     */           {
/*  270: 335 */             JButton b = (JButton)ae.getSource();
/*  271: 336 */             b.setEnabled(false);
/*  272:     */           }
/*  273: 339 */           jTfZoom.setText(GraphVisualizer.this.zoomPercents[(i - 1)] + "%");
/*  274: 340 */           GraphVisualizer.this.scale = (GraphVisualizer.this.zoomPercents[(i - 1)] / 100.0D);
/*  275:     */         }
/*  276:     */         else
/*  277:     */         {
/*  278: 342 */           if (!jBtZoomIn.isEnabled()) {
/*  279: 343 */             jBtZoomIn.setEnabled(true);
/*  280:     */           }
/*  281: 346 */           jTfZoom.setText(GraphVisualizer.this.zoomPercents[22] + "%");
/*  282: 347 */           GraphVisualizer.this.scale = (GraphVisualizer.this.zoomPercents[22] / 100.0D);
/*  283:     */         }
/*  284: 349 */         GraphVisualizer.this.setAppropriateSize();
/*  285: 350 */         GraphVisualizer.this.m_gp.repaint();
/*  286: 351 */         GraphVisualizer.this.m_gp.invalidate();
/*  287: 352 */         GraphVisualizer.this.m_js.revalidate();
/*  288:     */       }
/*  289: 356 */     });
/*  290: 357 */     JButton jBtExtraControls = new JButton();
/*  291: 358 */     tempURL = ClassLoader.getSystemResource("weka/gui/graphvisualizer/icons/extra.gif");
/*  292: 359 */     if (tempURL != null) {
/*  293: 360 */       jBtExtraControls.setIcon(new ImageIcon(tempURL));
/*  294:     */     } else {
/*  295: 362 */       System.err.println("weka/gui/graphvisualizer/icons/extra.gif not found for weka.gui.graphvisualizer.Graph");
/*  296:     */     }
/*  297: 365 */     jBtExtraControls.setToolTipText("Show/Hide extra controls");
/*  298:     */     
/*  299: 367 */     final JCheckBox jCbCustomNodeSize = new JCheckBox("Custom Node Size");
/*  300: 368 */     final JLabel jLbNodeWidth = new JLabel("Width");
/*  301: 369 */     final JLabel jLbNodeHeight = new JLabel("Height");
/*  302:     */     
/*  303: 371 */     this.jTfNodeWidth.setHorizontalAlignment(0);
/*  304: 372 */     this.jTfNodeWidth.setText("" + this.nodeWidth);
/*  305: 373 */     this.jTfNodeHeight.setHorizontalAlignment(0);
/*  306: 374 */     this.jTfNodeHeight.setText("" + this.nodeHeight);
/*  307: 375 */     jLbNodeWidth.setEnabled(false);
/*  308: 376 */     this.jTfNodeWidth.setEnabled(false);
/*  309: 377 */     jLbNodeHeight.setEnabled(false);
/*  310: 378 */     this.jTfNodeHeight.setEnabled(false);
/*  311:     */     
/*  312: 380 */     jCbCustomNodeSize.addActionListener(new ActionListener()
/*  313:     */     {
/*  314:     */       public void actionPerformed(ActionEvent ae)
/*  315:     */       {
/*  316: 383 */         if (((JCheckBox)ae.getSource()).isSelected())
/*  317:     */         {
/*  318: 384 */           jLbNodeWidth.setEnabled(true);
/*  319: 385 */           GraphVisualizer.this.jTfNodeWidth.setEnabled(true);
/*  320: 386 */           jLbNodeHeight.setEnabled(true);
/*  321: 387 */           GraphVisualizer.this.jTfNodeHeight.setEnabled(true);
/*  322:     */         }
/*  323:     */         else
/*  324:     */         {
/*  325: 389 */           jLbNodeWidth.setEnabled(false);
/*  326: 390 */           GraphVisualizer.this.jTfNodeWidth.setEnabled(false);
/*  327: 391 */           jLbNodeHeight.setEnabled(false);
/*  328: 392 */           GraphVisualizer.this.jTfNodeHeight.setEnabled(false);
/*  329: 393 */           GraphVisualizer.this.setAppropriateNodeSize();
/*  330:     */         }
/*  331:     */       }
/*  332: 397 */     });
/*  333: 398 */     this.jBtLayout = new JButton("Layout Graph");
/*  334: 399 */     this.jBtLayout.addActionListener(new ActionListener()
/*  335:     */     {
/*  336:     */       public void actionPerformed(ActionEvent ae)
/*  337:     */       {
/*  338: 404 */         if (jCbCustomNodeSize.isSelected())
/*  339:     */         {
/*  340:     */           int tmpW;
/*  341:     */           try
/*  342:     */           {
/*  343: 406 */             tmpW = Integer.parseInt(GraphVisualizer.this.jTfNodeWidth.getText());
/*  344:     */           }
/*  345:     */           catch (NumberFormatException ne)
/*  346:     */           {
/*  347: 408 */             JOptionPane.showMessageDialog(GraphVisualizer.this.getParent(), "Invalid integer entered for node width.", "Error", 0);
/*  348:     */             
/*  349:     */ 
/*  350: 411 */             tmpW = GraphVisualizer.this.nodeWidth;
/*  351: 412 */             GraphVisualizer.this.jTfNodeWidth.setText("" + GraphVisualizer.this.nodeWidth);
/*  352:     */           }
/*  353:     */           int tmpH;
/*  354:     */           try
/*  355:     */           {
/*  356: 416 */             tmpH = Integer.parseInt(GraphVisualizer.this.jTfNodeHeight.getText());
/*  357:     */           }
/*  358:     */           catch (NumberFormatException ne)
/*  359:     */           {
/*  360: 418 */             JOptionPane.showMessageDialog(GraphVisualizer.this.getParent(), "Invalid integer entered for node height.", "Error", 0);
/*  361:     */             
/*  362:     */ 
/*  363: 421 */             tmpH = GraphVisualizer.this.nodeHeight;
/*  364: 422 */             GraphVisualizer.this.jTfNodeWidth.setText("" + GraphVisualizer.this.nodeHeight);
/*  365:     */           }
/*  366: 425 */           if ((tmpW != GraphVisualizer.this.nodeWidth) || (tmpH != GraphVisualizer.this.nodeHeight))
/*  367:     */           {
/*  368: 426 */             GraphVisualizer.this.nodeWidth = tmpW;
/*  369: 427 */             GraphVisualizer.this.paddedNodeWidth = (GraphVisualizer.this.nodeWidth + 8);
/*  370: 428 */             GraphVisualizer.this.nodeHeight = tmpH;
/*  371:     */           }
/*  372:     */         }
/*  373: 431 */         JButton bt = (JButton)ae.getSource();
/*  374: 432 */         bt.setEnabled(false);
/*  375: 433 */         GraphVisualizer.this.m_le.setNodeSize(GraphVisualizer.this.paddedNodeWidth, GraphVisualizer.this.nodeHeight);
/*  376: 434 */         GraphVisualizer.this.m_le.layoutGraph();
/*  377:     */       }
/*  378: 437 */     });
/*  379: 438 */     GridBagConstraints gbc = new GridBagConstraints();
/*  380:     */     
/*  381: 440 */     final JPanel p = new JPanel(new GridBagLayout());
/*  382: 441 */     gbc.gridwidth = 0;
/*  383: 442 */     gbc.anchor = 18;
/*  384: 443 */     gbc.fill = 0;
/*  385: 444 */     p.add(this.m_le.getControlPanel(), gbc);
/*  386: 445 */     gbc.gridwidth = 1;
/*  387: 446 */     gbc.insets = new Insets(8, 0, 0, 0);
/*  388: 447 */     gbc.anchor = 18;
/*  389: 448 */     gbc.gridwidth = 0;
/*  390:     */     
/*  391: 450 */     p.add(jCbCustomNodeSize, gbc);
/*  392: 451 */     gbc.insets = new Insets(0, 0, 0, 0);
/*  393: 452 */     gbc.gridwidth = 0;
/*  394: 453 */     Container c = new Container();
/*  395: 454 */     c.setLayout(new GridBagLayout());
/*  396: 455 */     gbc.gridwidth = -1;
/*  397: 456 */     c.add(jLbNodeWidth, gbc);
/*  398: 457 */     gbc.gridwidth = 0;
/*  399: 458 */     c.add(this.jTfNodeWidth, gbc);
/*  400: 459 */     gbc.gridwidth = -1;
/*  401: 460 */     c.add(jLbNodeHeight, gbc);
/*  402: 461 */     gbc.gridwidth = 0;
/*  403: 462 */     c.add(this.jTfNodeHeight, gbc);
/*  404: 463 */     gbc.fill = 2;
/*  405: 464 */     p.add(c, gbc);
/*  406:     */     
/*  407: 466 */     gbc.anchor = 18;
/*  408: 467 */     gbc.insets = new Insets(8, 0, 0, 0);
/*  409: 468 */     gbc.fill = 2;
/*  410: 469 */     p.add(this.jBtLayout, gbc);
/*  411: 470 */     gbc.fill = 0;
/*  412: 471 */     p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("ExtraControls"), BorderFactory.createEmptyBorder(4, 4, 4, 4)));
/*  413:     */     
/*  414:     */ 
/*  415: 474 */     p.setPreferredSize(new Dimension(0, 0));
/*  416:     */     
/*  417: 476 */     final JToolBar jTbTools = new JToolBar();
/*  418: 477 */     jTbTools.setFloatable(false);
/*  419: 478 */     jTbTools.setLayout(new GridBagLayout());
/*  420: 479 */     gbc.anchor = 18;
/*  421: 480 */     gbc.gridwidth = 0;
/*  422: 481 */     gbc.insets = new Insets(0, 0, 0, 0);
/*  423: 482 */     jTbTools.add(p, gbc);
/*  424: 483 */     gbc.gridwidth = 1;
/*  425: 484 */     jTbTools.add(this.m_jBtSave, gbc);
/*  426: 485 */     jTbTools.addSeparator(new Dimension(2, 2));
/*  427: 486 */     jTbTools.add(jBtZoomIn, gbc);
/*  428:     */     
/*  429: 488 */     gbc.fill = 3;
/*  430: 489 */     gbc.weighty = 1.0D;
/*  431: 490 */     JPanel p2 = new JPanel(new BorderLayout());
/*  432: 491 */     p2.setPreferredSize(jTfZoom.getPreferredSize());
/*  433: 492 */     p2.setMinimumSize(jTfZoom.getPreferredSize());
/*  434: 493 */     p2.add(jTfZoom, "Center");
/*  435: 494 */     jTbTools.add(p2, gbc);
/*  436: 495 */     gbc.weighty = 0.0D;
/*  437: 496 */     gbc.fill = 0;
/*  438:     */     
/*  439: 498 */     jTbTools.add(jBtZoomOut, gbc);
/*  440: 499 */     jTbTools.addSeparator(new Dimension(2, 2));
/*  441: 500 */     jTbTools.add(jBtExtraControls, gbc);
/*  442: 501 */     jTbTools.addSeparator(new Dimension(4, 2));
/*  443: 502 */     gbc.weightx = 1.0D;
/*  444: 503 */     gbc.fill = 1;
/*  445: 504 */     jTbTools.add(this.m_le.getProgressBar(), gbc);
/*  446:     */     
/*  447: 506 */     jBtExtraControls.addActionListener(new ActionListener()
/*  448:     */     {
/*  449:     */       public void actionPerformed(ActionEvent ae)
/*  450:     */       {
/*  451: 509 */         Dimension d = p.getPreferredSize();
/*  452: 510 */         if ((d.width == 0) || (d.height == 0))
/*  453:     */         {
/*  454: 511 */           LayoutManager lm = p.getLayout();
/*  455: 512 */           Dimension d2 = lm.preferredLayoutSize(p);
/*  456: 513 */           p.setPreferredSize(d2);
/*  457: 514 */           jTbTools.revalidate();
/*  458:     */         }
/*  459:     */         else
/*  460:     */         {
/*  461: 533 */           p.setPreferredSize(new Dimension(0, 0));
/*  462: 534 */           jTbTools.revalidate();
/*  463:     */         }
/*  464:     */       }
/*  465: 551 */     });
/*  466: 552 */     setLayout(new BorderLayout());
/*  467: 553 */     add(jTbTools, "North");
/*  468: 554 */     add(this.m_js, "Center");
/*  469:     */   }
/*  470:     */   
/*  471:     */   protected void setAppropriateNodeSize()
/*  472:     */   {
/*  473: 564 */     if (this.maxStringWidth == 0) {
/*  474: 565 */       for (int i = 0; i < this.m_nodes.size(); i++)
/*  475:     */       {
/*  476: 566 */         int strWidth = this.fm.stringWidth(((GraphNode)this.m_nodes.get(i)).lbl);
/*  477: 567 */         if (strWidth > this.maxStringWidth) {
/*  478: 568 */           this.maxStringWidth = strWidth;
/*  479:     */         }
/*  480:     */       }
/*  481:     */     }
/*  482: 572 */     this.nodeWidth = (this.maxStringWidth + 4);
/*  483: 573 */     this.paddedNodeWidth = (this.nodeWidth + 8);
/*  484: 574 */     this.jTfNodeWidth.setText("" + this.nodeWidth);
/*  485:     */     
/*  486: 576 */     this.nodeHeight = (2 * this.fm.getHeight());
/*  487: 577 */     this.jTfNodeHeight.setText("" + this.nodeHeight);
/*  488:     */   }
/*  489:     */   
/*  490:     */   protected void setAppropriateSize()
/*  491:     */   {
/*  492: 585 */     int maxX = 0;int maxY = 0;
/*  493:     */     
/*  494: 587 */     this.m_gp.setScale(this.scale, this.scale);
/*  495: 589 */     for (int i = 0; i < this.m_nodes.size(); i++)
/*  496:     */     {
/*  497: 590 */       GraphNode n = (GraphNode)this.m_nodes.get(i);
/*  498: 591 */       if (maxX < n.x) {
/*  499: 592 */         maxX = n.x;
/*  500:     */       }
/*  501: 594 */       if (maxY < n.y) {
/*  502: 595 */         maxY = n.y;
/*  503:     */       }
/*  504:     */     }
/*  505: 602 */     this.m_gp.setPreferredSize(new Dimension((int)((maxX + this.paddedNodeWidth + 2) * this.scale), (int)((maxY + this.nodeHeight + 2) * this.scale)));
/*  506:     */   }
/*  507:     */   
/*  508:     */   public void layoutCompleted(LayoutCompleteEvent le)
/*  509:     */   {
/*  510: 616 */     setAppropriateSize();
/*  511:     */     
/*  512: 618 */     this.m_gp.invalidate();
/*  513: 619 */     this.m_js.revalidate();
/*  514: 620 */     this.m_gp.repaint();
/*  515: 621 */     this.jBtLayout.setEnabled(true);
/*  516:     */   }
/*  517:     */   
/*  518:     */   public void layoutGraph()
/*  519:     */   {
/*  520: 631 */     if (this.m_le != null) {
/*  521: 632 */       this.m_le.layoutGraph();
/*  522:     */     }
/*  523:     */   }
/*  524:     */   
/*  525:     */   public void readBIF(String instring)
/*  526:     */     throws BIFFormatException
/*  527:     */   {
/*  528: 645 */     BIFParser bp = new BIFParser(instring, this.m_nodes, this.m_edges);
/*  529:     */     try
/*  530:     */     {
/*  531: 647 */       this.graphID = bp.parse();
/*  532:     */     }
/*  533:     */     catch (BIFFormatException bf)
/*  534:     */     {
/*  535: 649 */       System.out.println("BIF format error");
/*  536: 650 */       bf.printStackTrace();
/*  537:     */     }
/*  538:     */     catch (Exception ex)
/*  539:     */     {
/*  540: 652 */       ex.printStackTrace();
/*  541: 653 */       return;
/*  542:     */     }
/*  543: 656 */     setAppropriateNodeSize();
/*  544: 657 */     if (this.m_le != null) {
/*  545: 658 */       this.m_le.setNodeSize(this.paddedNodeWidth, this.nodeHeight);
/*  546:     */     }
/*  547:     */   }
/*  548:     */   
/*  549:     */   public void readBIF(InputStream instream)
/*  550:     */     throws BIFFormatException
/*  551:     */   {
/*  552: 670 */     BIFParser bp = new BIFParser(instream, this.m_nodes, this.m_edges);
/*  553:     */     try
/*  554:     */     {
/*  555: 672 */       this.graphID = bp.parse();
/*  556:     */     }
/*  557:     */     catch (BIFFormatException bf)
/*  558:     */     {
/*  559: 674 */       System.out.println("BIF format error");
/*  560: 675 */       bf.printStackTrace();
/*  561:     */     }
/*  562:     */     catch (Exception ex)
/*  563:     */     {
/*  564: 677 */       ex.printStackTrace();
/*  565: 678 */       return;
/*  566:     */     }
/*  567: 681 */     setAppropriateNodeSize();
/*  568: 682 */     if (this.m_le != null) {
/*  569: 683 */       this.m_le.setNodeSize(this.paddedNodeWidth, this.nodeHeight);
/*  570:     */     }
/*  571: 685 */     setAppropriateSize();
/*  572:     */   }
/*  573:     */   
/*  574:     */   public void readDOT(Reader input)
/*  575:     */   {
/*  576: 696 */     DotParser dp = new DotParser(input, this.m_nodes, this.m_edges);
/*  577: 697 */     this.graphID = dp.parse();
/*  578:     */     
/*  579: 699 */     setAppropriateNodeSize();
/*  580: 700 */     if (this.m_le != null)
/*  581:     */     {
/*  582: 701 */       this.m_le.setNodeSize(this.paddedNodeWidth, this.nodeHeight);
/*  583: 702 */       this.jBtLayout.setEnabled(false);
/*  584: 703 */       layoutGraph();
/*  585:     */     }
/*  586:     */   }
/*  587:     */   
/*  588:     */   private class GraphPanel
/*  589:     */     extends PrintablePanel
/*  590:     */   {
/*  591:     */     private static final long serialVersionUID = -3562813603236753173L;
/*  592:     */     
/*  593:     */     public GraphPanel()
/*  594:     */     {
/*  595: 717 */       addMouseListener(new GraphVisualizer.GraphVisualizerMouseListener(GraphVisualizer.this, null));
/*  596: 718 */       addMouseMotionListener(new GraphVisualizer.GraphVisualizerMouseMotionListener(GraphVisualizer.this, null));
/*  597: 719 */       setToolTipText("");
/*  598:     */     }
/*  599:     */     
/*  600:     */     public String getToolTipText(MouseEvent me)
/*  601:     */     {
/*  602: 727 */       Dimension d = GraphVisualizer.this.m_gp.getPreferredSize();
/*  603:     */       int ny;
/*  604:     */       int nx;
/*  605:     */       int y;
/*  606: 730 */       int x = y = nx = ny = 0;
/*  607: 732 */       if (d.width < GraphVisualizer.this.m_gp.getWidth()) {
/*  608: 733 */         nx = (int)((nx + GraphVisualizer.this.m_gp.getWidth() / 2 - d.width / 2) / GraphVisualizer.this.scale);
/*  609:     */       }
/*  610: 735 */       if (d.height < GraphVisualizer.this.m_gp.getHeight()) {
/*  611: 736 */         ny = (int)((ny + GraphVisualizer.this.m_gp.getHeight() / 2 - d.height / 2) / GraphVisualizer.this.scale);
/*  612:     */       }
/*  613: 739 */       Rectangle r = new Rectangle(0, 0, (int)(GraphVisualizer.this.paddedNodeWidth * GraphVisualizer.this.scale), (int)(GraphVisualizer.this.nodeHeight * GraphVisualizer.this.scale));
/*  614:     */       
/*  615: 741 */       x += me.getX();
/*  616: 742 */       y += me.getY();
/*  617: 745 */       for (int i = 0; i < GraphVisualizer.this.m_nodes.size(); i++)
/*  618:     */       {
/*  619: 746 */         GraphNode n = (GraphNode)GraphVisualizer.this.m_nodes.get(i);
/*  620: 747 */         if (n.nodeType != 3) {
/*  621: 748 */           return null;
/*  622:     */         }
/*  623: 750 */         r.x = ((int)((nx + n.x) * GraphVisualizer.this.scale));
/*  624: 751 */         r.y = ((int)((ny + n.y) * GraphVisualizer.this.scale));
/*  625: 752 */         if (r.contains(x, y))
/*  626:     */         {
/*  627: 753 */           if (n.probs == null) {
/*  628: 754 */             return n.lbl;
/*  629:     */           }
/*  630: 756 */           return n.lbl + " (click to view the probability dist. table)";
/*  631:     */         }
/*  632:     */       }
/*  633: 760 */       return null;
/*  634:     */     }
/*  635:     */     
/*  636:     */     public void paintComponent(Graphics gr)
/*  637:     */     {
/*  638: 765 */       Graphics2D g = (Graphics2D)gr;
/*  639: 766 */       RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  640:     */       
/*  641: 768 */       rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
/*  642: 769 */       g.setRenderingHints(rh);
/*  643: 770 */       g.scale(GraphVisualizer.this.scale, GraphVisualizer.this.scale);
/*  644: 771 */       Rectangle r = g.getClipBounds();
/*  645: 772 */       g.clearRect(r.x, r.y, r.width, r.height);
/*  646:     */       
/*  647:     */ 
/*  648: 775 */       int x = 0;int y = 0;
/*  649: 776 */       Dimension d = getPreferredSize();
/*  650: 782 */       if (d.width < getWidth()) {
/*  651: 783 */         x = (int)((x + getWidth() / 2 - d.width / 2) / GraphVisualizer.this.scale);
/*  652:     */       }
/*  653: 785 */       if (d.height < getHeight()) {
/*  654: 786 */         y = (int)((y + getHeight() / 2 - d.height / 2) / GraphVisualizer.this.scale);
/*  655:     */       }
/*  656: 789 */       for (int index = 0; index < GraphVisualizer.this.m_nodes.size(); index++)
/*  657:     */       {
/*  658: 790 */         GraphNode n = (GraphNode)GraphVisualizer.this.m_nodes.get(index);
/*  659: 791 */         if (n.nodeType == 3)
/*  660:     */         {
/*  661: 792 */           g.setColor(getBackground().darker().darker());
/*  662: 793 */           g.fillOval(x + n.x + GraphVisualizer.this.paddedNodeWidth - GraphVisualizer.this.nodeWidth - (GraphVisualizer.this.paddedNodeWidth - GraphVisualizer.this.nodeWidth) / 2, y + n.y, GraphVisualizer.this.nodeWidth, GraphVisualizer.this.nodeHeight);
/*  663:     */           
/*  664:     */ 
/*  665: 796 */           g.setColor(Color.white);
/*  666: 808 */           if (GraphVisualizer.this.fm.stringWidth(n.lbl) <= GraphVisualizer.this.nodeWidth) {
/*  667: 809 */             g.drawString(n.lbl, x + n.x + GraphVisualizer.this.paddedNodeWidth / 2 - GraphVisualizer.this.fm.stringWidth(n.lbl) / 2, y + n.y + GraphVisualizer.this.nodeHeight / 2 + GraphVisualizer.this.fm.getHeight() / 2 - 2);
/*  668: 812 */           } else if (GraphVisualizer.this.fm.stringWidth(n.ID) <= GraphVisualizer.this.nodeWidth) {
/*  669: 813 */             g.drawString(n.ID, x + n.x + GraphVisualizer.this.paddedNodeWidth / 2 - GraphVisualizer.this.fm.stringWidth(n.ID) / 2, y + n.y + GraphVisualizer.this.nodeHeight / 2 + GraphVisualizer.this.fm.getHeight() / 2 - 2);
/*  670: 816 */           } else if (GraphVisualizer.this.fm.stringWidth(Integer.toString(index)) <= GraphVisualizer.this.nodeWidth) {
/*  671: 817 */             g.drawString(Integer.toString(index), x + n.x + GraphVisualizer.this.paddedNodeWidth / 2 - GraphVisualizer.this.fm.stringWidth(Integer.toString(index)) / 2, y + n.y + GraphVisualizer.this.nodeHeight / 2 + GraphVisualizer.this.fm.getHeight() / 2 - 2);
/*  672:     */           }
/*  673: 822 */           g.setColor(Color.black);
/*  674:     */         }
/*  675:     */         else
/*  676:     */         {
/*  677: 830 */           g.drawLine(x + n.x + GraphVisualizer.this.paddedNodeWidth / 2, y + n.y, x + n.x + GraphVisualizer.this.paddedNodeWidth / 2, y + n.y + GraphVisualizer.this.nodeHeight);
/*  678:     */         }
/*  679: 841 */         if (n.edges != null) {
/*  680: 842 */           for (int[] edge : n.edges) {
/*  681: 843 */             if (edge[1] > 0)
/*  682:     */             {
/*  683: 844 */               GraphNode n2 = (GraphNode)GraphVisualizer.this.m_nodes.get(edge[0]);
/*  684:     */               
/*  685: 846 */               int x1 = n.x + GraphVisualizer.this.paddedNodeWidth / 2;
/*  686: 847 */               int y1 = n.y + GraphVisualizer.this.nodeHeight;
/*  687: 848 */               int x2 = n2.x + GraphVisualizer.this.paddedNodeWidth / 2;
/*  688: 849 */               int y2 = n2.y;
/*  689: 850 */               g.drawLine(x + x1, y + y1, x + x2, y + y2);
/*  690: 851 */               if (edge[1] == 1)
/*  691:     */               {
/*  692: 852 */                 if (n2.nodeType == 3) {
/*  693: 853 */                   drawArrow(g, x + x1, y + y1, x + x2, y + y2);
/*  694:     */                 }
/*  695:     */               }
/*  696: 855 */               else if (edge[1] == 2)
/*  697:     */               {
/*  698: 856 */                 if (n.nodeType == 3) {
/*  699: 857 */                   drawArrow(g, x + x2, y + y2, x + x1, y + y1);
/*  700:     */                 }
/*  701:     */               }
/*  702: 859 */               else if (edge[1] == 3)
/*  703:     */               {
/*  704: 860 */                 if (n.nodeType == 3) {
/*  705: 861 */                   drawArrow(g, x + x2, y + y2, x + x1, y + y1);
/*  706:     */                 }
/*  707: 863 */                 if (n2.nodeType == 3) {
/*  708: 864 */                   drawArrow(g, x + x1, y + y1, x + x2, y + y2);
/*  709:     */                 }
/*  710:     */               }
/*  711:     */             }
/*  712:     */           }
/*  713:     */         }
/*  714:     */       }
/*  715:     */     }
/*  716:     */     
/*  717:     */     protected void drawArrow(Graphics g, int x1, int y1, int x2, int y2)
/*  718:     */     {
/*  719: 881 */       if (x1 == x2)
/*  720:     */       {
/*  721: 882 */         if (y1 < y2)
/*  722:     */         {
/*  723: 883 */           g.drawLine(x2, y2, x2 + 4, y2 - 8);
/*  724: 884 */           g.drawLine(x2, y2, x2 - 4, y2 - 8);
/*  725:     */         }
/*  726:     */         else
/*  727:     */         {
/*  728: 886 */           g.drawLine(x2, y2, x2 + 4, y2 + 8);
/*  729: 887 */           g.drawLine(x2, y2, x2 - 4, y2 + 8);
/*  730:     */         }
/*  731:     */       }
/*  732:     */       else
/*  733:     */       {
/*  734: 891 */         double hyp = 0.0D;double base = 0.0D;double perp = 0.0D;
/*  735: 892 */         int x3 = 0;int y3 = 0;
/*  736:     */         double theta;
/*  737:     */         double theta;
/*  738: 894 */         if (x2 < x1)
/*  739:     */         {
/*  740: 895 */           base = x1 - x2;
/*  741: 896 */           hyp = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
/*  742: 897 */           theta = Math.acos(base / hyp);
/*  743:     */         }
/*  744:     */         else
/*  745:     */         {
/*  746: 899 */           base = x1 - x2;
/*  747: 900 */           hyp = Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
/*  748: 901 */           theta = Math.acos(base / hyp);
/*  749:     */         }
/*  750: 903 */         double beta = 0.5235987755982988D;
/*  751:     */         
/*  752:     */ 
/*  753:     */ 
/*  754: 907 */         hyp = 8.0D;
/*  755: 908 */         base = Math.cos(theta - beta) * hyp;
/*  756: 909 */         perp = Math.sin(theta - beta) * hyp;
/*  757:     */         
/*  758: 911 */         x3 = (int)(x2 + base);
/*  759: 912 */         if (y1 < y2) {
/*  760: 913 */           y3 = (int)(y2 - perp);
/*  761:     */         } else {
/*  762: 915 */           y3 = (int)(y2 + perp);
/*  763:     */         }
/*  764: 922 */         g.drawLine(x2, y2, x3, y3);
/*  765:     */         
/*  766: 924 */         base = Math.cos(theta + beta) * hyp;
/*  767: 925 */         perp = Math.sin(theta + beta) * hyp;
/*  768:     */         
/*  769: 927 */         x3 = (int)(x2 + base);
/*  770: 928 */         if (y1 < y2) {
/*  771: 929 */           y3 = (int)(y2 - perp);
/*  772:     */         } else {
/*  773: 931 */           y3 = (int)(y2 + perp);
/*  774:     */         }
/*  775: 936 */         g.drawLine(x2, y2, x3, y3);
/*  776:     */       }
/*  777:     */     }
/*  778:     */     
/*  779:     */     public void highLight(GraphNode n)
/*  780:     */     {
/*  781: 945 */       Graphics2D g = (Graphics2D)getGraphics();
/*  782: 946 */       RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  783:     */       
/*  784: 948 */       rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
/*  785: 949 */       g.setRenderingHints(rh);
/*  786: 950 */       g.setPaintMode();
/*  787: 951 */       g.scale(GraphVisualizer.this.scale, GraphVisualizer.this.scale);
/*  788: 952 */       int x = 0;int y = 0;
/*  789: 953 */       Dimension d = getPreferredSize();
/*  790: 959 */       if (d.width < getWidth()) {
/*  791: 960 */         x = (int)((x + getWidth() / 2 - d.width / 2) / GraphVisualizer.this.scale);
/*  792:     */       }
/*  793: 962 */       if (d.height < getHeight()) {
/*  794: 963 */         y = (int)((y + getHeight() / 2 - d.height / 2) / GraphVisualizer.this.scale);
/*  795:     */       }
/*  796: 967 */       if (n.nodeType == 3)
/*  797:     */       {
/*  798: 969 */         g.setXORMode(Color.green);
/*  799:     */         
/*  800: 971 */         g.fillOval(x + n.x + GraphVisualizer.this.paddedNodeWidth - GraphVisualizer.this.nodeWidth - (GraphVisualizer.this.paddedNodeWidth - GraphVisualizer.this.nodeWidth) / 2, y + n.y, GraphVisualizer.this.nodeWidth, GraphVisualizer.this.nodeHeight);
/*  801:     */         
/*  802: 973 */         g.setXORMode(Color.red);
/*  803: 979 */         if (GraphVisualizer.this.fm.stringWidth(n.lbl) <= GraphVisualizer.this.nodeWidth) {
/*  804: 980 */           g.drawString(n.lbl, x + n.x + GraphVisualizer.this.paddedNodeWidth / 2 - GraphVisualizer.this.fm.stringWidth(n.lbl) / 2, y + n.y + GraphVisualizer.this.nodeHeight / 2 + GraphVisualizer.this.fm.getHeight() / 2 - 2);
/*  805: 983 */         } else if (GraphVisualizer.this.fm.stringWidth(n.ID) <= GraphVisualizer.this.nodeWidth) {
/*  806: 984 */           g.drawString(n.ID, x + n.x + GraphVisualizer.this.paddedNodeWidth / 2 - GraphVisualizer.this.fm.stringWidth(n.ID) / 2, y + n.y + GraphVisualizer.this.nodeHeight / 2 + GraphVisualizer.this.fm.getHeight() / 2 - 2);
/*  807: 987 */         } else if (GraphVisualizer.this.fm.stringWidth(Integer.toString(GraphVisualizer.this.m_nodes.indexOf(n))) <= GraphVisualizer.this.nodeWidth) {
/*  808: 988 */           g.drawString(Integer.toString(GraphVisualizer.this.m_nodes.indexOf(n)), x + n.x + GraphVisualizer.this.paddedNodeWidth / 2 - GraphVisualizer.this.fm.stringWidth(Integer.toString(GraphVisualizer.this.m_nodes.indexOf(n))) / 2, y + n.y + GraphVisualizer.this.nodeHeight / 2 + GraphVisualizer.this.fm.getHeight() / 2 - 2);
/*  809:     */         }
/*  810: 995 */         g.setXORMode(Color.green);
/*  811:1000 */         if (n.edges != null) {
/*  812:1002 */           for (int[] edge2 : n.edges) {
/*  813:1003 */             if ((edge2[1] == 1) || (edge2[1] == 3))
/*  814:     */             {
/*  815:1004 */               GraphNode n2 = (GraphNode)GraphVisualizer.this.m_nodes.get(edge2[0]);
/*  816:     */               
/*  817:1006 */               int x1 = n.x + GraphVisualizer.this.paddedNodeWidth / 2;
/*  818:1007 */               int y1 = n.y + GraphVisualizer.this.nodeHeight;
/*  819:1008 */               int x2 = n2.x + GraphVisualizer.this.paddedNodeWidth / 2;
/*  820:1009 */               int y2 = n2.y;
/*  821:1010 */               g.drawLine(x + x1, y + y1, x + x2, y + y2);
/*  822:1011 */               if (edge2[1] == 1)
/*  823:     */               {
/*  824:1012 */                 if (n2.nodeType == 3) {
/*  825:1013 */                   drawArrow(g, x + x1, y + y1, x + x2, y + y2);
/*  826:     */                 }
/*  827:     */               }
/*  828:1015 */               else if (edge2[1] == 3)
/*  829:     */               {
/*  830:1016 */                 if (n.nodeType == 3) {
/*  831:1017 */                   drawArrow(g, x + x2, y + y2, x + x1, y + y1);
/*  832:     */                 }
/*  833:1019 */                 if (n2.nodeType == 3) {
/*  834:1020 */                   drawArrow(g, x + x1, y + y1, x + x2, y + y2);
/*  835:     */                 }
/*  836:     */               }
/*  837:1023 */               if (n2.nodeType == 3) {
/*  838:1024 */                 g.fillOval(x + n2.x + GraphVisualizer.this.paddedNodeWidth - GraphVisualizer.this.nodeWidth - (GraphVisualizer.this.paddedNodeWidth - GraphVisualizer.this.nodeWidth) / 2, y + n2.y, GraphVisualizer.this.nodeWidth, GraphVisualizer.this.nodeHeight);
/*  839:     */               }
/*  840:1034 */               Vector<GraphNode> t = new Vector();
/*  841:1035 */               while ((n2.nodeType != 3) || (t.size() > 0))
/*  842:     */               {
/*  843:1038 */                 if (t.size() > 0)
/*  844:     */                 {
/*  845:1039 */                   n2 = (GraphNode)t.get(0);
/*  846:1040 */                   t.removeElementAt(0);
/*  847:     */                 }
/*  848:1042 */                 if (n2.nodeType != 3)
/*  849:     */                 {
/*  850:1043 */                   g.drawLine(x + n2.x + GraphVisualizer.this.paddedNodeWidth / 2, y + n2.y, x + n2.x + GraphVisualizer.this.paddedNodeWidth / 2, y + n2.y + GraphVisualizer.this.nodeHeight);
/*  851:     */                   
/*  852:1045 */                   x1 = n2.x + GraphVisualizer.this.paddedNodeWidth / 2;
/*  853:1046 */                   y1 = n2.y + GraphVisualizer.this.nodeHeight;
/*  854:1048 */                   for (int[] edge : n2.edges) {
/*  855:1051 */                     if (edge[1] > 0)
/*  856:     */                     {
/*  857:1052 */                       GraphNode n3 = (GraphNode)GraphVisualizer.this.m_nodes.get(edge[0]);
/*  858:1053 */                       g.drawLine(x + x1, y + y1, x + n3.x + GraphVisualizer.this.paddedNodeWidth / 2, y + n3.y);
/*  859:1056 */                       if (n3.nodeType == 3)
/*  860:     */                       {
/*  861:1057 */                         g.fillOval(x + n3.x + GraphVisualizer.this.paddedNodeWidth - GraphVisualizer.this.nodeWidth - (GraphVisualizer.this.paddedNodeWidth - GraphVisualizer.this.nodeWidth) / 2, y + n3.y, GraphVisualizer.this.nodeWidth, GraphVisualizer.this.nodeHeight);
/*  862:     */                         
/*  863:     */ 
/*  864:1060 */                         drawArrow(g, x + x1, y + y1, x + n3.x + GraphVisualizer.this.paddedNodeWidth / 2, y + n3.y);
/*  865:     */                       }
/*  866:1064 */                       t.addElement(n3);
/*  867:     */                     }
/*  868:     */                   }
/*  869:     */                 }
/*  870:     */               }
/*  871:     */             }
/*  872:1070 */             else if ((edge2[1] == -2) || (edge2[1] == -3))
/*  873:     */             {
/*  874:1073 */               GraphNode n2 = (GraphNode)GraphVisualizer.this.m_nodes.get(edge2[0]);
/*  875:     */               
/*  876:1075 */               int x1 = n.x + GraphVisualizer.this.paddedNodeWidth / 2;
/*  877:1076 */               int y1 = n.y;
/*  878:1077 */               int x2 = n2.x + GraphVisualizer.this.paddedNodeWidth / 2;
/*  879:1078 */               int y2 = n2.y + GraphVisualizer.this.nodeHeight;
/*  880:1079 */               g.drawLine(x + x1, y + y1, x + x2, y + y2);
/*  881:1081 */               if (edge2[1] == -3)
/*  882:     */               {
/*  883:1082 */                 drawArrow(g, x + x2, y + y2, x + x1, y + y1);
/*  884:1083 */                 if (n2.nodeType != 1) {
/*  885:1084 */                   drawArrow(g, x + x1, y + y1, x + x2, y + y2);
/*  886:     */                 }
/*  887:     */               }
/*  888:1088 */               while (n2.nodeType != 3)
/*  889:     */               {
/*  890:1089 */                 g.drawLine(x + n2.x + GraphVisualizer.this.paddedNodeWidth / 2, y + n2.y + GraphVisualizer.this.nodeHeight, x + n2.x + GraphVisualizer.this.paddedNodeWidth / 2, y + n2.y);
/*  891:     */                 
/*  892:1091 */                 x1 = n2.x + GraphVisualizer.this.paddedNodeWidth / 2;
/*  893:1092 */                 y1 = n2.y;
/*  894:1093 */                 for (int[] edge : n2.edges) {
/*  895:1094 */                   if (edge[1] < 0)
/*  896:     */                   {
/*  897:1095 */                     n2 = (GraphNode)GraphVisualizer.this.m_nodes.get(edge[0]);
/*  898:1096 */                     g.drawLine(x + x1, y + y1, x + n2.x + GraphVisualizer.this.paddedNodeWidth / 2, y + n2.y + GraphVisualizer.this.nodeHeight);
/*  899:1098 */                     if (n2.nodeType == 1) {
/*  900:     */                       break;
/*  901:     */                     }
/*  902:1099 */                     drawArrow(g, x + x1, y + y1, x + n2.x + GraphVisualizer.this.paddedNodeWidth / 2, y + n2.y + GraphVisualizer.this.nodeHeight); break;
/*  903:     */                   }
/*  904:     */                 }
/*  905:     */               }
/*  906:     */             }
/*  907:     */           }
/*  908:     */         }
/*  909:     */       }
/*  910:     */     }
/*  911:     */   }
/*  912:     */   
/*  913:     */   private class GraphVisualizerTableModel
/*  914:     */     extends AbstractTableModel
/*  915:     */   {
/*  916:     */     private static final long serialVersionUID = -4789813491347366596L;
/*  917:     */     final String[] columnNames;
/*  918:     */     final double[][] data;
/*  919:     */     
/*  920:     */     public GraphVisualizerTableModel(double[][] d, String[] c)
/*  921:     */     {
/*  922:1126 */       this.data = d;
/*  923:1127 */       this.columnNames = c;
/*  924:     */     }
/*  925:     */     
/*  926:     */     public int getColumnCount()
/*  927:     */     {
/*  928:1132 */       return this.columnNames.length;
/*  929:     */     }
/*  930:     */     
/*  931:     */     public int getRowCount()
/*  932:     */     {
/*  933:1137 */       return this.data.length;
/*  934:     */     }
/*  935:     */     
/*  936:     */     public String getColumnName(int col)
/*  937:     */     {
/*  938:1142 */       return this.columnNames[col];
/*  939:     */     }
/*  940:     */     
/*  941:     */     public Object getValueAt(int row, int col)
/*  942:     */     {
/*  943:1147 */       return new Double(this.data[row][col]);
/*  944:     */     }
/*  945:     */     
/*  946:     */     public Class<?> getColumnClass(int c)
/*  947:     */     {
/*  948:1156 */       return getValueAt(0, c).getClass();
/*  949:     */     }
/*  950:     */     
/*  951:     */     public boolean isCellEditable(int row, int col)
/*  952:     */     {
/*  953:1164 */       return false;
/*  954:     */     }
/*  955:     */   }
/*  956:     */   
/*  957:     */   private class GraphVisualizerMouseListener
/*  958:     */     extends MouseAdapter
/*  959:     */   {
/*  960:     */     int x;
/*  961:     */     int y;
/*  962:     */     int nx;
/*  963:     */     int ny;
/*  964:     */     Rectangle r;
/*  965:     */     
/*  966:     */     private GraphVisualizerMouseListener() {}
/*  967:     */     
/*  968:     */     public void mouseClicked(MouseEvent me)
/*  969:     */     {
/*  970:1182 */       Dimension d = GraphVisualizer.this.m_gp.getPreferredSize();
/*  971:     */       
/*  972:     */ 
/*  973:1185 */       this.x = (this.y = this.nx = this.ny = 0);
/*  974:1187 */       if (d.width < GraphVisualizer.this.m_gp.getWidth()) {
/*  975:1188 */         this.nx = ((int)((this.nx + GraphVisualizer.this.m_gp.getWidth() / 2 - d.width / 2) / GraphVisualizer.this.scale));
/*  976:     */       }
/*  977:1190 */       if (d.height < GraphVisualizer.this.m_gp.getHeight()) {
/*  978:1191 */         this.ny = ((int)((this.ny + GraphVisualizer.this.m_gp.getHeight() / 2 - d.height / 2) / GraphVisualizer.this.scale));
/*  979:     */       }
/*  980:1194 */       this.r = new Rectangle(0, 0, (int)(GraphVisualizer.this.paddedNodeWidth * GraphVisualizer.this.scale), (int)(GraphVisualizer.this.nodeHeight * GraphVisualizer.this.scale));
/*  981:     */       
/*  982:1196 */       this.x += me.getX();
/*  983:1197 */       this.y += me.getY();
/*  984:1200 */       for (int i = 0; i < GraphVisualizer.this.m_nodes.size(); i++)
/*  985:     */       {
/*  986:1201 */         GraphNode n = (GraphNode)GraphVisualizer.this.m_nodes.get(i);
/*  987:1202 */         this.r.x = ((int)((this.nx + n.x) * GraphVisualizer.this.scale));
/*  988:1203 */         this.r.y = ((int)((this.ny + n.y) * GraphVisualizer.this.scale));
/*  989:1204 */         if (this.r.contains(this.x, this.y))
/*  990:     */         {
/*  991:1205 */           if (n.probs == null) {
/*  992:1206 */             return;
/*  993:     */           }
/*  994:1209 */           int noOfPrntsOutcomes = 1;
/*  995:1210 */           if (n.prnts != null)
/*  996:     */           {
/*  997:1211 */             for (int prnt : n.prnts)
/*  998:     */             {
/*  999:1212 */               GraphNode n2 = (GraphNode)GraphVisualizer.this.m_nodes.get(prnt);
/* 1000:1213 */               noOfPrntsOutcomes *= n2.outcomes.length;
/* 1001:     */             }
/* 1002:1215 */             if (noOfPrntsOutcomes > 511)
/* 1003:     */             {
/* 1004:1216 */               System.err.println("Too many outcomes of parents (" + noOfPrntsOutcomes + ") can't display probabilities");
/* 1005:     */               
/* 1006:1218 */               return;
/* 1007:     */             }
/* 1008:     */           }
/* 1009:1222 */           GraphVisualizer.GraphVisualizerTableModel tm = new GraphVisualizer.GraphVisualizerTableModel(GraphVisualizer.this, n.probs, n.outcomes);
/* 1010:     */           
/* 1011:     */ 
/* 1012:1225 */           JTable jTblProbs = new JTable(tm);
/* 1013:     */           
/* 1014:     */ 
/* 1015:1228 */           JScrollPane js = new JScrollPane(jTblProbs);
/* 1016:1230 */           if (n.prnts != null)
/* 1017:     */           {
/* 1018:1231 */             GridBagConstraints gbc = new GridBagConstraints();
/* 1019:1232 */             JPanel jPlRowHeader = new JPanel(new GridBagLayout());
/* 1020:     */             
/* 1021:     */ 
/* 1022:1235 */             int[] idx = new int[n.prnts.length];
/* 1023:     */             
/* 1024:1237 */             int[] lengths = new int[n.prnts.length];
/* 1025:     */             
/* 1026:     */ 
/* 1027:     */ 
/* 1028:     */ 
/* 1029:     */ 
/* 1030:     */ 
/* 1031:     */ 
/* 1032:     */ 
/* 1033:     */ 
/* 1034:     */ 
/* 1035:     */ 
/* 1036:     */ 
/* 1037:     */ 
/* 1038:1251 */             gbc.anchor = 18;
/* 1039:1252 */             gbc.fill = 2;
/* 1040:1253 */             gbc.insets = new Insets(0, 1, 0, 0);
/* 1041:1254 */             int addNum = 0;int temp = 0;
/* 1042:1255 */             boolean dark = false;
/* 1043:     */             for (;;)
/* 1044:     */             {
/* 1045:1258 */               gbc.gridwidth = 1;
/* 1046:1259 */               for (int k = 0; k < n.prnts.length; k++)
/* 1047:     */               {
/* 1048:1260 */                 GraphNode n2 = (GraphNode)GraphVisualizer.this.m_nodes.get(n.prnts[k]);
/* 1049:1261 */                 JLabel lb = new JLabel(n2.outcomes[idx[k]]);
/* 1050:1262 */                 lb.setFont(new Font("Dialog", 0, 12));
/* 1051:1263 */                 lb.setOpaque(true);
/* 1052:1264 */                 lb.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 1));
/* 1053:1265 */                 lb.setHorizontalAlignment(0);
/* 1054:1266 */                 if (dark)
/* 1055:     */                 {
/* 1056:1267 */                   lb.setBackground(lb.getBackground().darker());
/* 1057:1268 */                   lb.setForeground(Color.white);
/* 1058:     */                 }
/* 1059:     */                 else
/* 1060:     */                 {
/* 1061:1270 */                   lb.setForeground(Color.black);
/* 1062:     */                 }
/* 1063:1273 */                 temp = lb.getPreferredSize().width;
/* 1064:     */                 
/* 1065:     */ 
/* 1066:1276 */                 lb.setPreferredSize(new Dimension(temp, jTblProbs.getRowHeight()));
/* 1067:1278 */                 if (lengths[k] < temp) {
/* 1068:1279 */                   lengths[k] = temp;
/* 1069:     */                 }
/* 1070:1281 */                 temp = 0;
/* 1071:1283 */                 if (k == n.prnts.length - 1)
/* 1072:     */                 {
/* 1073:1284 */                   gbc.gridwidth = 0;
/* 1074:1285 */                   dark = dark != true;
/* 1075:     */                 }
/* 1076:1287 */                 jPlRowHeader.add(lb, gbc);
/* 1077:1288 */                 addNum++;
/* 1078:     */               }
/* 1079:1291 */               for (int k = n.prnts.length - 1; k >= 0; k--)
/* 1080:     */               {
/* 1081:1292 */                 GraphNode n2 = (GraphNode)GraphVisualizer.this.m_nodes.get(n.prnts[k]);
/* 1082:1293 */                 if ((idx[k] == n2.outcomes.length - 1) && (k != 0))
/* 1083:     */                 {
/* 1084:1294 */                   idx[k] = 0;
/* 1085:     */                 }
/* 1086:     */                 else
/* 1087:     */                 {
/* 1088:1297 */                   idx[k] += 1;
/* 1089:1298 */                   break;
/* 1090:     */                 }
/* 1091:     */               }
/* 1092:1302 */               GraphNode n2 = (GraphNode)GraphVisualizer.this.m_nodes.get(n.prnts[0]);
/* 1093:1303 */               if (idx[0] == n2.outcomes.length)
/* 1094:     */               {
/* 1095:1304 */                 JLabel lb = (JLabel)jPlRowHeader.getComponent(addNum - 1);
/* 1096:1305 */                 jPlRowHeader.remove(addNum - 1);
/* 1097:1306 */                 lb.setPreferredSize(new Dimension(lb.getPreferredSize().width, jTblProbs.getRowHeight()));
/* 1098:     */                 
/* 1099:1308 */                 gbc.gridwidth = 0;
/* 1100:1309 */                 gbc.weighty = 1.0D;
/* 1101:1310 */                 jPlRowHeader.add(lb, gbc);
/* 1102:1311 */                 gbc.weighty = 0.0D;
/* 1103:1312 */                 break;
/* 1104:     */               }
/* 1105:     */             }
/* 1106:1316 */             gbc.gridwidth = 1;
/* 1107:     */             
/* 1108:     */ 
/* 1109:     */ 
/* 1110:1320 */             JPanel jPlRowNames = new JPanel(new GridBagLayout());
/* 1111:1321 */             for (int j = 0; j < n.prnts.length; j++)
/* 1112:     */             {
/* 1113:1323 */               JLabel lb1 = new JLabel(((GraphNode)GraphVisualizer.this.m_nodes.get(n.prnts[j])).lbl);
/* 1114:1324 */               lb1.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 1));
/* 1115:1325 */               Dimension tempd = lb1.getPreferredSize();
/* 1116:1328 */               if (tempd.width < lengths[j])
/* 1117:     */               {
/* 1118:1329 */                 lb1.setPreferredSize(new Dimension(lengths[j], tempd.height));
/* 1119:1330 */                 lb1.setHorizontalAlignment(0);
/* 1120:1331 */                 lb1.setMinimumSize(new Dimension(lengths[j], tempd.height));
/* 1121:     */               }
/* 1122:1332 */               else if (tempd.width > lengths[j])
/* 1123:     */               {
/* 1124:1333 */                 JLabel lb2 = (JLabel)jPlRowHeader.getComponent(j);
/* 1125:1334 */                 lb2.setPreferredSize(new Dimension(tempd.width, lb2.getPreferredSize().height));
/* 1126:     */               }
/* 1127:1337 */               jPlRowNames.add(lb1, gbc);
/* 1128:     */             }
/* 1129:1340 */             js.setRowHeaderView(jPlRowHeader);
/* 1130:1341 */             js.setCorner("UPPER_LEFT_CORNER", jPlRowNames);
/* 1131:     */           }
/* 1132:1344 */           JDialog jd = new JDialog((Frame)GraphVisualizer.this.getTopLevelAncestor(), "Probability Distribution Table For " + n.lbl, Dialog.ModalityType.DOCUMENT_MODAL);
/* 1133:     */           
/* 1134:     */ 
/* 1135:     */ 
/* 1136:1348 */           jd.setSize(500, 400);
/* 1137:1349 */           jd.setLocation(GraphVisualizer.this.getLocation().x + GraphVisualizer.this.getWidth() / 2 - 250, GraphVisualizer.this.getLocation().y + GraphVisualizer.this.getHeight() / 2 - 200);
/* 1138:     */           
/* 1139:     */ 
/* 1140:     */ 
/* 1141:     */ 
/* 1142:     */ 
/* 1143:1355 */           jd.getContentPane().setLayout(new BorderLayout());
/* 1144:1356 */           jd.getContentPane().add(js, "Center");
/* 1145:1357 */           jd.setVisible(true);
/* 1146:     */           
/* 1147:1359 */           return;
/* 1148:     */         }
/* 1149:     */       }
/* 1150:     */     }
/* 1151:     */   }
/* 1152:     */   
/* 1153:     */   private class GraphVisualizerMouseMotionListener
/* 1154:     */     extends MouseMotionAdapter
/* 1155:     */   {
/* 1156:     */     int x;
/* 1157:     */     int y;
/* 1158:     */     int nx;
/* 1159:     */     int ny;
/* 1160:     */     Rectangle r;
/* 1161:     */     GraphNode lastNode;
/* 1162:     */     
/* 1163:     */     private GraphVisualizerMouseMotionListener() {}
/* 1164:     */     
/* 1165:     */     public void mouseMoved(MouseEvent me)
/* 1166:     */     {
/* 1167:1378 */       Dimension d = GraphVisualizer.this.m_gp.getPreferredSize();
/* 1168:     */       
/* 1169:     */ 
/* 1170:1381 */       this.x = (this.y = this.nx = this.ny = 0);
/* 1171:1383 */       if (d.width < GraphVisualizer.this.m_gp.getWidth()) {
/* 1172:1384 */         this.nx = ((int)((this.nx + GraphVisualizer.this.m_gp.getWidth() / 2 - d.width / 2) / GraphVisualizer.this.scale));
/* 1173:     */       }
/* 1174:1386 */       if (d.height < GraphVisualizer.this.m_gp.getHeight()) {
/* 1175:1387 */         this.ny = ((int)((this.ny + GraphVisualizer.this.m_gp.getHeight() / 2 - d.height / 2) / GraphVisualizer.this.scale));
/* 1176:     */       }
/* 1177:1390 */       this.r = new Rectangle(0, 0, (int)(GraphVisualizer.this.paddedNodeWidth * GraphVisualizer.this.scale), (int)(GraphVisualizer.this.nodeHeight * GraphVisualizer.this.scale));
/* 1178:     */       
/* 1179:1392 */       this.x += me.getX();
/* 1180:1393 */       this.y += me.getY();
/* 1181:1396 */       for (int i = 0; i < GraphVisualizer.this.m_nodes.size(); i++)
/* 1182:     */       {
/* 1183:1397 */         GraphNode n = (GraphNode)GraphVisualizer.this.m_nodes.get(i);
/* 1184:1398 */         this.r.x = ((int)((this.nx + n.x) * GraphVisualizer.this.scale));
/* 1185:1399 */         this.r.y = ((int)((this.ny + n.y) * GraphVisualizer.this.scale));
/* 1186:1400 */         if (this.r.contains(this.x, this.y))
/* 1187:     */         {
/* 1188:1401 */           if (n == this.lastNode) {
/* 1189:     */             break;
/* 1190:     */           }
/* 1191:1402 */           GraphVisualizer.this.m_gp.highLight(n);
/* 1192:1403 */           if (this.lastNode != null) {
/* 1193:1404 */             GraphVisualizer.this.m_gp.highLight(this.lastNode);
/* 1194:     */           }
/* 1195:1406 */           this.lastNode = n; break;
/* 1196:     */         }
/* 1197:     */       }
/* 1198:1411 */       if ((i == GraphVisualizer.this.m_nodes.size()) && (this.lastNode != null))
/* 1199:     */       {
/* 1200:1412 */         GraphVisualizer.this.m_gp.repaint();
/* 1201:     */         
/* 1202:1414 */         this.lastNode = null;
/* 1203:     */       }
/* 1204:     */     }
/* 1205:     */   }
/* 1206:     */   
/* 1207:     */   public static void main(String[] args)
/* 1208:     */   {
/* 1209:1424 */     Logger.log(Logger.Level.INFO, "Logging started");
/* 1210:     */     
/* 1211:1426 */     JFrame jf = new JFrame("Graph Visualizer");
/* 1212:1427 */     GraphVisualizer g = new GraphVisualizer();
/* 1213:     */     try
/* 1214:     */     {
/* 1215:1430 */       if (args[0].endsWith(".xml")) {
/* 1216:1439 */         g.readBIF(new FileInputStream(args[0]));
/* 1217:     */       } else {
/* 1218:1442 */         g.readDOT(new FileReader(args[0]));
/* 1219:     */       }
/* 1220:     */     }
/* 1221:     */     catch (IOException ex)
/* 1222:     */     {
/* 1223:1445 */       ex.printStackTrace();
/* 1224:     */     }
/* 1225:     */     catch (BIFFormatException bf)
/* 1226:     */     {
/* 1227:1447 */       bf.printStackTrace();
/* 1228:1448 */       System.exit(-1);
/* 1229:     */     }
/* 1230:1451 */     jf.getContentPane().add(g);
/* 1231:     */     
/* 1232:1453 */     jf.setDefaultCloseOperation(3);
/* 1233:1454 */     jf.setSize(800, 600);
/* 1234:     */     
/* 1235:1456 */     jf.setVisible(true);
/* 1236:     */   }
/* 1237:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.graphvisualizer.GraphVisualizer
 * JD-Core Version:    0.7.0.1
 */