/*    1:     */ package weka.gui.knowledgeflow;
/*    2:     */ 
/*    3:     */ import java.awt.BasicStroke;
/*    4:     */ import java.awt.BorderLayout;
/*    5:     */ import java.awt.Color;
/*    6:     */ import java.awt.Container;
/*    7:     */ import java.awt.Cursor;
/*    8:     */ import java.awt.Dialog.ModalityType;
/*    9:     */ import java.awt.Dimension;
/*   10:     */ import java.awt.Font;
/*   11:     */ import java.awt.FontMetrics;
/*   12:     */ import java.awt.Frame;
/*   13:     */ import java.awt.Graphics;
/*   14:     */ import java.awt.Graphics2D;
/*   15:     */ import java.awt.Menu;
/*   16:     */ import java.awt.MenuItem;
/*   17:     */ import java.awt.Point;
/*   18:     */ import java.awt.PopupMenu;
/*   19:     */ import java.awt.Rectangle;
/*   20:     */ import java.awt.RenderingHints;
/*   21:     */ import java.awt.Stroke;
/*   22:     */ import java.awt.event.ActionEvent;
/*   23:     */ import java.awt.event.ActionListener;
/*   24:     */ import java.awt.event.MouseAdapter;
/*   25:     */ import java.awt.event.MouseEvent;
/*   26:     */ import java.awt.event.MouseMotionAdapter;
/*   27:     */ import java.awt.event.WindowAdapter;
/*   28:     */ import java.awt.event.WindowEvent;
/*   29:     */ import java.beans.Beans;
/*   30:     */ import java.io.File;
/*   31:     */ import java.io.IOException;
/*   32:     */ import java.util.ArrayList;
/*   33:     */ import java.util.Iterator;
/*   34:     */ import java.util.List;
/*   35:     */ import java.util.Map;
/*   36:     */ import java.util.Map.Entry;
/*   37:     */ import javax.swing.JComponent;
/*   38:     */ import javax.swing.JDialog;
/*   39:     */ import javax.swing.JFrame;
/*   40:     */ import javax.swing.JOptionPane;
/*   41:     */ import weka.core.EnvironmentHandler;
/*   42:     */ import weka.core.Instances;
/*   43:     */ import weka.core.WekaException;
/*   44:     */ import weka.core.converters.FileSourcedConverter;
/*   45:     */ import weka.gui.GUIApplication;
/*   46:     */ import weka.gui.Perspective;
/*   47:     */ import weka.gui.PerspectiveManager;
/*   48:     */ import weka.gui.visualize.PrintablePanel;
/*   49:     */ import weka.knowledgeflow.KFDefaults;
/*   50:     */ import weka.knowledgeflow.StepManager;
/*   51:     */ import weka.knowledgeflow.StepManagerImpl;
/*   52:     */ import weka.knowledgeflow.steps.Note;
/*   53:     */ import weka.knowledgeflow.steps.Step;
/*   54:     */ 
/*   55:     */ public class LayoutPanel
/*   56:     */   extends PrintablePanel
/*   57:     */ {
/*   58:     */   private static final long serialVersionUID = 4988098224376217099L;
/*   59:     */   protected int m_gridSpacing;
/*   60:     */   protected VisibleLayout m_visLayout;
/*   61:     */   protected int m_currentX;
/*   62:     */   protected int m_currentY;
/*   63:     */   protected int m_oldX;
/*   64:     */   protected int m_oldY;
/*   65:     */   protected Thread m_perspectiveDataLoadThread;
/*   66:     */   
/*   67:     */   public LayoutPanel(VisibleLayout vis)
/*   68:     */   {
/*   69:  87 */     this.m_visLayout = vis;
/*   70:  88 */     setLayout(null);
/*   71:     */     
/*   72:  90 */     setupMouseListener();
/*   73:  91 */     setupMouseMotionListener();
/*   74:     */     
/*   75:  93 */     this.m_gridSpacing = ((Integer)this.m_visLayout.getMainPerspective().getSetting(KFDefaults.GRID_SPACING_KEY, Integer.valueOf(40))).intValue();
/*   76:     */   }
/*   77:     */   
/*   78:     */   protected void setupMouseListener()
/*   79:     */   {
/*   80: 102 */     addMouseListener(new MouseAdapter()
/*   81:     */     {
/*   82:     */       public void mousePressed(MouseEvent me)
/*   83:     */       {
/*   84: 105 */         LayoutPanel.this.requestFocusInWindow();
/*   85: 106 */         double z = LayoutPanel.this.m_visLayout.getZoomSetting() / 100.0D;
/*   86: 107 */         double px = me.getX();
/*   87: 108 */         double py = me.getY();
/*   88: 109 */         py /= z;
/*   89: 110 */         px /= z;
/*   90: 112 */         if ((LayoutPanel.this.m_visLayout.getMainPerspective().getPalleteSelectedStep() == null) && 
/*   91: 113 */           ((me.getModifiers() & 0x10) == 16) && (LayoutPanel.this.m_visLayout.getFlowLayoutOperation() == VisibleLayout.LayoutOperation.NONE))
/*   92:     */         {
/*   93: 115 */           StepVisual step = LayoutPanel.this.m_visLayout.findStep(new Point((int)px, (int)py));
/*   94: 117 */           if (step != null)
/*   95:     */           {
/*   96: 118 */             LayoutPanel.this.m_visLayout.setEditStep(step);
/*   97: 119 */             LayoutPanel.this.m_oldX = ((int)px);
/*   98: 120 */             LayoutPanel.this.m_oldY = ((int)py);
/*   99: 121 */             LayoutPanel.this.m_visLayout.setFlowLayoutOperation(VisibleLayout.LayoutOperation.MOVING);
/*  100:     */           }
/*  101: 123 */           if (LayoutPanel.this.m_visLayout.getFlowLayoutOperation() != VisibleLayout.LayoutOperation.MOVING)
/*  102:     */           {
/*  103: 124 */             LayoutPanel.this.m_visLayout.setFlowLayoutOperation(VisibleLayout.LayoutOperation.SELECTING);
/*  104: 125 */             LayoutPanel.this.m_oldX = ((int)px);
/*  105: 126 */             LayoutPanel.this.m_oldY = ((int)py);
/*  106:     */             
/*  107: 128 */             LayoutPanel.this.m_currentX = LayoutPanel.this.m_oldX;
/*  108: 129 */             LayoutPanel.this.m_currentY = LayoutPanel.this.m_oldY;
/*  109: 130 */             Graphics2D gx = (Graphics2D)LayoutPanel.this.getGraphics();
/*  110: 131 */             gx.setXORMode(Color.white);
/*  111: 132 */             gx.dispose();
/*  112:     */           }
/*  113:     */         }
/*  114:     */       }
/*  115:     */       
/*  116:     */       public void mouseReleased(MouseEvent me)
/*  117:     */       {
/*  118: 140 */         LayoutPanel.this.requestFocusInWindow();
/*  119: 142 */         if ((LayoutPanel.this.m_visLayout.getEditStep() != null) && (LayoutPanel.this.m_visLayout.getFlowLayoutOperation() == VisibleLayout.LayoutOperation.MOVING))
/*  120:     */         {
/*  121: 144 */           if (LayoutPanel.this.m_visLayout.getMainPerspective().getSnapToGrid())
/*  122:     */           {
/*  123: 145 */             int x = LayoutPanel.this.snapToGrid(LayoutPanel.this.m_visLayout.getEditStep().getX());
/*  124: 146 */             int y = LayoutPanel.this.snapToGrid(LayoutPanel.this.m_visLayout.getEditStep().getY());
/*  125: 147 */             LayoutPanel.this.m_visLayout.getEditStep().setX(x);
/*  126: 148 */             LayoutPanel.this.m_visLayout.getEditStep().setY(y);
/*  127: 149 */             LayoutPanel.this.snapSelectedToGrid();
/*  128:     */           }
/*  129: 152 */           LayoutPanel.this.m_visLayout.setEditStep(null);
/*  130: 153 */           LayoutPanel.this.revalidate();
/*  131: 154 */           LayoutPanel.this.repaint();
/*  132: 155 */           LayoutPanel.this.m_visLayout.setFlowLayoutOperation(VisibleLayout.LayoutOperation.NONE);
/*  133:     */         }
/*  134: 158 */         if (LayoutPanel.this.m_visLayout.getFlowLayoutOperation() == VisibleLayout.LayoutOperation.SELECTING)
/*  135:     */         {
/*  136: 159 */           LayoutPanel.this.revalidate();
/*  137: 160 */           LayoutPanel.this.repaint();
/*  138: 161 */           LayoutPanel.this.m_visLayout.setFlowLayoutOperation(VisibleLayout.LayoutOperation.NONE);
/*  139: 162 */           double z = LayoutPanel.this.m_visLayout.getZoomSetting() / 100.0D;
/*  140: 163 */           double px = me.getX();
/*  141: 164 */           double py = me.getY();
/*  142: 165 */           py /= z;
/*  143: 166 */           px /= z;
/*  144:     */           
/*  145: 168 */           LayoutPanel.this.highlightSubFlow(LayoutPanel.this.m_currentX, LayoutPanel.this.m_currentY, (int)px, (int)py);
/*  146:     */         }
/*  147:     */       }
/*  148:     */       
/*  149:     */       public void mouseClicked(MouseEvent me)
/*  150:     */       {
/*  151: 174 */         LayoutPanel.this.requestFocusInWindow();
/*  152: 175 */         Point p = me.getPoint();
/*  153: 176 */         Point np = new Point();
/*  154: 177 */         double z = LayoutPanel.this.m_visLayout.getZoomSetting() / 100.0D;
/*  155: 178 */         double px = me.getX();
/*  156: 179 */         double py = me.getY();
/*  157: 180 */         px /= z;
/*  158: 181 */         py /= z;
/*  159:     */         
/*  160: 183 */         np.setLocation(p.getX() / z, p.getY() / z);
/*  161: 184 */         StepVisual step = LayoutPanel.this.m_visLayout.findStep(np);
/*  162: 185 */         if ((LayoutPanel.this.m_visLayout.getFlowLayoutOperation() == VisibleLayout.LayoutOperation.ADDING) || (LayoutPanel.this.m_visLayout.getFlowLayoutOperation() == VisibleLayout.LayoutOperation.NONE))
/*  163:     */         {
/*  164: 189 */           if (step != null)
/*  165:     */           {
/*  166: 190 */             if (me.getClickCount() == 2)
/*  167:     */             {
/*  168: 191 */               if ((!step.getStepManager().isStepBusy()) && (!LayoutPanel.this.m_visLayout.isExecuting())) {
/*  169: 193 */                 LayoutPanel.this.popupStepEditorDialog(step);
/*  170:     */               }
/*  171:     */             }
/*  172:     */             else
/*  173:     */             {
/*  174: 195 */               if (((me.getModifiers() & 0x10) != 16) || (me.isAltDown()))
/*  175:     */               {
/*  176: 197 */                 LayoutPanel.this.stepContextualMenu(step, (int)(p.getX() / z), (int)(p.getY() / z));
/*  177:     */                 
/*  178: 199 */                 return;
/*  179:     */               }
/*  180: 202 */               List<StepVisual> v = LayoutPanel.this.m_visLayout.getSelectedSteps();
/*  181: 203 */               if (!me.isShiftDown()) {
/*  182: 204 */                 v = new ArrayList();
/*  183:     */               }
/*  184: 206 */               v.add(step);
/*  185: 207 */               LayoutPanel.this.m_visLayout.setSelectedSteps(v);
/*  186:     */             }
/*  187:     */           }
/*  188:     */           else
/*  189:     */           {
/*  190: 211 */             if (((me.getModifiers() & 0x10) != 16) || (me.isAltDown()))
/*  191:     */             {
/*  192: 214 */               if (!LayoutPanel.this.m_visLayout.isExecuting())
/*  193:     */               {
/*  194: 215 */                 LayoutPanel.this.canvasContextualMenu((int)px, (int)py);
/*  195:     */                 
/*  196: 217 */                 LayoutPanel.this.revalidate();
/*  197: 218 */                 LayoutPanel.this.repaint();
/*  198: 219 */                 LayoutPanel.this.m_visLayout.getMainPerspective().notifyIsDirty();
/*  199:     */               }
/*  200: 221 */               return;
/*  201:     */             }
/*  202: 222 */             if (LayoutPanel.this.m_visLayout.getMainPerspective().getPalleteSelectedStep() != null)
/*  203:     */             {
/*  204: 228 */               double x = px;
/*  205: 229 */               double y = py;
/*  206: 231 */               if (LayoutPanel.this.m_visLayout.getMainPerspective().getSnapToGrid())
/*  207:     */               {
/*  208: 232 */                 x = LayoutPanel.this.snapToGrid((int)x);
/*  209: 233 */                 y = LayoutPanel.this.snapToGrid((int)y);
/*  210:     */               }
/*  211: 236 */               LayoutPanel.this.m_visLayout.addUndoPoint();
/*  212: 237 */               LayoutPanel.this.m_visLayout.addStep(LayoutPanel.this.m_visLayout.getMainPerspective().getPalleteSelectedStep(), (int)x, (int)y);
/*  213:     */               
/*  214:     */ 
/*  215: 240 */               LayoutPanel.this.m_visLayout.getMainPerspective().clearDesignPaletteSelection();
/*  216: 241 */               LayoutPanel.this.m_visLayout.getMainPerspective().setPalleteSelectedStep(null);
/*  217: 242 */               LayoutPanel.this.m_visLayout.setFlowLayoutOperation(VisibleLayout.LayoutOperation.NONE);
/*  218: 243 */               LayoutPanel.this.m_visLayout.setEdited(true);
/*  219:     */             }
/*  220:     */           }
/*  221: 246 */           LayoutPanel.this.revalidate();
/*  222: 247 */           LayoutPanel.this.repaint();
/*  223: 248 */           LayoutPanel.this.m_visLayout.getMainPerspective().notifyIsDirty();
/*  224:     */         }
/*  225: 251 */         if ((LayoutPanel.this.m_visLayout.getFlowLayoutOperation() == VisibleLayout.LayoutOperation.PASTING) && (LayoutPanel.this.m_visLayout.getMainPerspective().getPasteBuffer().length() > 0))
/*  226:     */         {
/*  227:     */           try
/*  228:     */           {
/*  229: 255 */             LayoutPanel.this.m_visLayout.pasteFromClipboard((int)px, (int)py);
/*  230:     */           }
/*  231:     */           catch (WekaException e)
/*  232:     */           {
/*  233: 257 */             LayoutPanel.this.m_visLayout.getMainPerspective().showErrorDialog(e);
/*  234:     */           }
/*  235: 260 */           LayoutPanel.this.m_visLayout.setFlowLayoutOperation(VisibleLayout.LayoutOperation.NONE);
/*  236: 261 */           LayoutPanel.this.m_visLayout.getMainPerspective().setCursor(Cursor.getPredefinedCursor(0));
/*  237:     */           
/*  238: 263 */           return;
/*  239:     */         }
/*  240: 266 */         if (LayoutPanel.this.m_visLayout.getFlowLayoutOperation() == VisibleLayout.LayoutOperation.CONNECTING)
/*  241:     */         {
/*  242: 268 */           LayoutPanel.this.repaint();
/*  243: 269 */           for (StepVisual v : LayoutPanel.this.m_visLayout.getRenderGraph()) {
/*  244: 270 */             v.setDisplayConnectors(false);
/*  245:     */           }
/*  246: 273 */           if (step != null)
/*  247:     */           {
/*  248: 277 */             LayoutPanel.this.m_visLayout.addUndoPoint();
/*  249:     */             
/*  250: 279 */             LayoutPanel.this.m_visLayout.connectSteps(LayoutPanel.this.m_visLayout.getEditStep().getStepManager(), step.getStepManager(), LayoutPanel.this.m_visLayout.getEditConnection());
/*  251:     */             
/*  252:     */ 
/*  253:     */ 
/*  254: 283 */             LayoutPanel.this.m_visLayout.setEdited(true);
/*  255: 284 */             LayoutPanel.this.repaint();
/*  256:     */           }
/*  257: 286 */           LayoutPanel.this.m_visLayout.setFlowLayoutOperation(VisibleLayout.LayoutOperation.NONE);
/*  258: 287 */           LayoutPanel.this.m_visLayout.setEditStep(null);
/*  259: 288 */           LayoutPanel.this.m_visLayout.setEditConnection(null);
/*  260:     */         }
/*  261: 291 */         if (LayoutPanel.this.m_visLayout.getSelectedSteps().size() > 0) {
/*  262: 292 */           LayoutPanel.this.m_visLayout.setSelectedSteps(new ArrayList());
/*  263:     */         }
/*  264:     */       }
/*  265:     */     });
/*  266:     */   }
/*  267:     */   
/*  268:     */   protected void setupMouseMotionListener()
/*  269:     */   {
/*  270: 302 */     addMouseMotionListener(new MouseMotionAdapter()
/*  271:     */     {
/*  272:     */       public void mouseDragged(MouseEvent me)
/*  273:     */       {
/*  274: 306 */         double z = LayoutPanel.this.m_visLayout.getZoomSetting() / 100.0D;
/*  275: 307 */         double px = me.getX();
/*  276: 308 */         double py = me.getY();
/*  277: 309 */         px /= z;
/*  278: 310 */         py /= z;
/*  279: 312 */         if ((LayoutPanel.this.m_visLayout.getEditStep() != null) && (LayoutPanel.this.m_visLayout.getFlowLayoutOperation() == VisibleLayout.LayoutOperation.MOVING))
/*  280:     */         {
/*  281: 314 */           int deltaX = (int)px - LayoutPanel.this.m_oldX;
/*  282: 315 */           int deltaY = (int)py - LayoutPanel.this.m_oldY;
/*  283:     */           
/*  284: 317 */           LayoutPanel.this.m_visLayout.getEditStep().setX(LayoutPanel.this.m_visLayout.getEditStep().getX() + deltaX);
/*  285:     */           
/*  286: 319 */           LayoutPanel.this.m_visLayout.getEditStep().setY(LayoutPanel.this.m_visLayout.getEditStep().getY() + deltaY);
/*  287: 321 */           for (StepVisual v : LayoutPanel.this.m_visLayout.getSelectedSteps()) {
/*  288: 322 */             if (v != LayoutPanel.this.m_visLayout.getEditStep())
/*  289:     */             {
/*  290: 323 */               v.setX(v.getX() + deltaX);
/*  291: 324 */               v.setY(v.getY() + deltaY);
/*  292:     */             }
/*  293:     */           }
/*  294: 327 */           LayoutPanel.this.repaint();
/*  295: 328 */           LayoutPanel.this.m_oldX = ((int)px);
/*  296: 329 */           LayoutPanel.this.m_oldY = ((int)py);
/*  297: 330 */           LayoutPanel.this.m_visLayout.setEdited(true);
/*  298:     */         }
/*  299: 333 */         if (LayoutPanel.this.m_visLayout.getFlowLayoutOperation() == VisibleLayout.LayoutOperation.SELECTING)
/*  300:     */         {
/*  301: 334 */           LayoutPanel.this.repaint();
/*  302: 335 */           LayoutPanel.this.m_oldX = ((int)px);
/*  303: 336 */           LayoutPanel.this.m_oldY = ((int)py);
/*  304:     */         }
/*  305:     */       }
/*  306:     */       
/*  307:     */       public void mouseMoved(MouseEvent me)
/*  308:     */       {
/*  309: 342 */         double z = LayoutPanel.this.m_visLayout.getZoomSetting() / 100.0D;
/*  310: 343 */         double px = me.getX();
/*  311: 344 */         double py = me.getY();
/*  312: 345 */         px /= z;
/*  313: 346 */         py /= z;
/*  314: 348 */         if (LayoutPanel.this.m_visLayout.getFlowLayoutOperation() == VisibleLayout.LayoutOperation.CONNECTING)
/*  315:     */         {
/*  316: 349 */           LayoutPanel.this.repaint();
/*  317: 350 */           LayoutPanel.this.m_oldX = ((int)px);
/*  318: 351 */           LayoutPanel.this.m_oldY = ((int)py);
/*  319:     */         }
/*  320:     */       }
/*  321:     */     });
/*  322:     */   }
/*  323:     */   
/*  324:     */   public void paintComponent(Graphics gx)
/*  325:     */   {
/*  326: 359 */     Color backG = (Color)this.m_visLayout.getMainPerspective().getSetting(KFDefaults.LAYOUT_COLOR_KEY, KFDefaults.LAYOUT_COLOR);
/*  327: 362 */     if (!backG.equals(getBackground())) {
/*  328: 363 */       setBackground(backG);
/*  329:     */     }
/*  330: 366 */     double lz = this.m_visLayout.getZoomSetting() / 100.0D;
/*  331: 367 */     ((Graphics2D)gx).scale(lz, lz);
/*  332: 368 */     if (this.m_visLayout.getZoomSetting() < 100) {
/*  333: 369 */       ((Graphics2D)gx).setStroke(new BasicStroke(2.0F));
/*  334:     */     }
/*  335: 371 */     super.paintComponent(gx);
/*  336:     */     
/*  337: 373 */     ((Graphics2D)gx).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  338:     */     
/*  339:     */ 
/*  340: 376 */     ((Graphics2D)gx).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
/*  341:     */     
/*  342:     */ 
/*  343: 379 */     paintStepLabels(gx);
/*  344: 380 */     paintConnections(gx);
/*  345: 382 */     if (this.m_visLayout.getFlowLayoutOperation() == VisibleLayout.LayoutOperation.CONNECTING) {
/*  346: 383 */       gx.drawLine(this.m_currentX, this.m_currentY, this.m_oldX, this.m_oldY);
/*  347: 384 */     } else if (this.m_visLayout.getFlowLayoutOperation() == VisibleLayout.LayoutOperation.SELECTING) {
/*  348: 385 */       gx.drawRect(this.m_currentX < this.m_oldX ? this.m_currentX : this.m_oldX, this.m_currentY < this.m_oldY ? this.m_currentY : this.m_oldY, Math.abs(this.m_oldX - this.m_currentX), Math.abs(this.m_oldY - this.m_currentY));
/*  349:     */     }
/*  350: 390 */     if (((Boolean)this.m_visLayout.getMainPerspective().getSetting(KFDefaults.SHOW_GRID_KEY, Boolean.valueOf(false))).booleanValue())
/*  351:     */     {
/*  352: 392 */       Color gridColor = (Color)this.m_visLayout.getMainPerspective().getSetting(KFDefaults.GRID_COLOR_KEY, KFDefaults.GRID_COLOR);
/*  353:     */       
/*  354:     */ 
/*  355: 395 */       gx.setColor(gridColor);
/*  356: 396 */       int gridSpacing = ((Integer)this.m_visLayout.getMainPerspective().getSetting(KFDefaults.GRID_SPACING_KEY, Integer.valueOf(40))).intValue();
/*  357:     */       
/*  358:     */ 
/*  359: 399 */       int layoutWidth = ((Integer)this.m_visLayout.getMainPerspective().getSetting(KFDefaults.LAYOUT_WIDTH_KEY, Integer.valueOf(2560))).intValue();
/*  360:     */       
/*  361:     */ 
/*  362: 402 */       int layoutHeight = ((Integer)this.m_visLayout.getMainPerspective().getSetting(KFDefaults.LAYOUT_HEIGHT_KEY, Integer.valueOf(1440))).intValue();
/*  363:     */       
/*  364:     */ 
/*  365: 405 */       Stroke original = ((Graphics2D)gx).getStroke();
/*  366: 406 */       Stroke dashed = new BasicStroke(1.0F, 0, 2, 0.0F, new float[] { 3.0F }, 0.0F);
/*  367:     */       
/*  368:     */ 
/*  369: 409 */       ((Graphics2D)gx).setStroke(dashed);
/*  370: 411 */       for (int i = gridSpacing; i < layoutWidth / lz; i += gridSpacing) {
/*  371: 412 */         gx.drawLine(i, 0, i, (int)(layoutHeight / lz));
/*  372:     */       }
/*  373: 414 */       for (int i = gridSpacing; i < layoutHeight / lz; i += gridSpacing) {
/*  374: 415 */         gx.drawLine(0, i, (int)(layoutWidth / lz), i);
/*  375:     */       }
/*  376: 417 */       ((Graphics2D)gx).setStroke(original);
/*  377:     */     }
/*  378:     */   }
/*  379:     */   
/*  380:     */   public void doLayout()
/*  381:     */   {
/*  382: 424 */     super.doLayout();
/*  383: 426 */     for (StepVisual v : this.m_visLayout.getRenderGraph())
/*  384:     */     {
/*  385: 427 */       Dimension d = v.getPreferredSize();
/*  386: 428 */       v.setBounds(v.getX(), v.getY(), d.width, d.height);
/*  387: 429 */       v.revalidate();
/*  388:     */     }
/*  389:     */   }
/*  390:     */   
/*  391:     */   protected void paintStepLabels(Graphics gx)
/*  392:     */   {
/*  393: 439 */     gx.setFont(new Font(null, 0, ((Integer)this.m_visLayout.getMainPerspective().getSetting(KFDefaults.STEP_LABEL_FONT_SIZE_KEY, Integer.valueOf(9))).intValue()));
/*  394:     */     
/*  395:     */ 
/*  396: 442 */     FontMetrics fm = gx.getFontMetrics();
/*  397: 443 */     int hf = fm.getAscent();
/*  398: 445 */     for (StepVisual v : this.m_visLayout.getRenderGraph()) {
/*  399: 446 */       if (v.getDisplayStepLabel())
/*  400:     */       {
/*  401: 450 */         int cx = v.getX();
/*  402: 451 */         int cy = v.getY();
/*  403: 452 */         int width = v.getWidth();
/*  404: 453 */         int height = v.getHeight();
/*  405: 454 */         String label = v.getStepName();
/*  406: 455 */         int labelwidth = fm.stringWidth(label);
/*  407: 456 */         if (labelwidth < width)
/*  408:     */         {
/*  409: 457 */           gx.drawString(label, cx + width / 2 - labelwidth / 2, cy + height + hf + 2);
/*  410:     */         }
/*  411:     */         else
/*  412:     */         {
/*  413: 463 */           int mid = label.length() / 2;
/*  414:     */           
/*  415: 465 */           int closest = label.length();
/*  416: 466 */           int closestI = -1;
/*  417: 467 */           for (int z = 0; z < label.length(); z++) {
/*  418: 468 */             if ((label.charAt(z) < 'a') && 
/*  419: 469 */               (Math.abs(mid - z) < closest))
/*  420:     */             {
/*  421: 470 */               closest = Math.abs(mid - z);
/*  422: 471 */               closestI = z;
/*  423:     */             }
/*  424:     */           }
/*  425: 475 */           if (closestI != -1)
/*  426:     */           {
/*  427: 476 */             String left = label.substring(0, closestI);
/*  428: 477 */             String right = label.substring(closestI, label.length());
/*  429: 478 */             if ((left.length() > 1) && (right.length() > 1))
/*  430:     */             {
/*  431: 479 */               gx.drawString(left, cx + width / 2 - fm.stringWidth(left) / 2, cy + height + hf * 1 + 2);
/*  432:     */               
/*  433:     */ 
/*  434: 482 */               gx.drawString(right, cx + width / 2 - fm.stringWidth(right) / 2, cy + height + hf * 2 + 2);
/*  435:     */             }
/*  436:     */             else
/*  437:     */             {
/*  438: 485 */               gx.drawString(label, cx + width / 2 - fm.stringWidth(label) / 2, cy + height + hf * 1 + 2);
/*  439:     */             }
/*  440:     */           }
/*  441:     */           else
/*  442:     */           {
/*  443: 489 */             gx.drawString(label, cx + width / 2 - fm.stringWidth(label) / 2, cy + height + hf * 1 + 2);
/*  444:     */           }
/*  445:     */         }
/*  446:     */       }
/*  447:     */     }
/*  448:     */   }
/*  449:     */   
/*  450:     */   protected void paintConnections(Graphics gx)
/*  451:     */   {
/*  452: 503 */     for (Iterator i$ = this.m_visLayout.getRenderGraph().iterator(); i$.hasNext();)
/*  453:     */     {
/*  454: 503 */       stepVis = (StepVisual)i$.next();
/*  455: 504 */       outConns = stepVis.getStepManager().getOutgoingConnections();
/*  456: 507 */       if (outConns.size() > 0)
/*  457:     */       {
/*  458: 508 */         generatableOutputConnections = stepVis.getStepManager().getStepOutgoingConnectionTypes();
/*  459:     */         
/*  460:     */ 
/*  461:     */ 
/*  462:     */ 
/*  463:     */ 
/*  464: 514 */         count = 0;
/*  465: 515 */         for (Map.Entry<String, List<StepManager>> e : outConns.entrySet())
/*  466:     */         {
/*  467: 516 */           String connName = (String)e.getKey();
/*  468: 517 */           List<StepManager> connectedSteps = (List)e.getValue();
/*  469:     */           int sX;
/*  470:     */           int sY;
/*  471:     */           int sWidth;
/*  472:     */           int sHeight;
/*  473: 518 */           if (connectedSteps.size() > 0)
/*  474:     */           {
/*  475: 519 */             sX = stepVis.getX();
/*  476: 520 */             sY = stepVis.getY();
/*  477: 521 */             sWidth = stepVis.getWidth();
/*  478: 522 */             sHeight = stepVis.getHeight();
/*  479: 524 */             for (StepManager target : connectedSteps)
/*  480:     */             {
/*  481: 525 */               StepManagerImpl targetI = (StepManagerImpl)target;
/*  482: 526 */               int tX = targetI.getStepVisual().getX();
/*  483: 527 */               int tY = targetI.getStepVisual().getY();
/*  484: 528 */               int tWidth = targetI.getStepVisual().getWidth();
/*  485: 529 */               int tHeight = targetI.getStepVisual().getHeight();
/*  486:     */               
/*  487: 531 */               Point bestSourcePoint = stepVis.getClosestConnectorPoint(new Point(tX + tWidth / 2, tY + tHeight / 2));
/*  488:     */               
/*  489:     */ 
/*  490: 534 */               Point bestTargetPoint = targetI.getStepVisual().getClosestConnectorPoint(new Point(sX + sWidth / 2, sY + sHeight / 2));
/*  491:     */               
/*  492:     */ 
/*  493:     */ 
/*  494: 538 */               gx.setColor(Color.red);
/*  495: 539 */               boolean active = (generatableOutputConnections != null) && (generatableOutputConnections.contains(connName));
/*  496: 543 */               if (!active) {
/*  497: 544 */                 gx.setColor(Color.gray);
/*  498:     */               }
/*  499: 547 */               gx.drawLine((int)bestSourcePoint.getX(), (int)bestSourcePoint.getY(), (int)bestTargetPoint.getX(), (int)bestTargetPoint.getY());
/*  500:     */               double angle;
/*  501:     */               try
/*  502:     */               {
/*  503: 554 */                 double a = (bestSourcePoint.getY() - bestTargetPoint.getY()) / (bestSourcePoint.getX() - bestTargetPoint.getX());
/*  504:     */                 
/*  505:     */ 
/*  506: 557 */                 angle = Math.atan(a);
/*  507:     */               }
/*  508:     */               catch (Exception ex)
/*  509:     */               {
/*  510: 559 */                 angle = 1.570796326794897D;
/*  511:     */               }
/*  512: 561 */               Point arrowstart = new Point(bestTargetPoint.x, bestTargetPoint.y);
/*  513:     */               
/*  514: 563 */               Point arrowoffset = new Point((int)(7.0D * Math.cos(angle)), (int)(7.0D * Math.sin(angle)));
/*  515:     */               Point arrowend;
/*  516:     */               Point arrowend;
/*  517: 567 */               if (bestSourcePoint.getX() >= bestTargetPoint.getX()) {
/*  518: 569 */                 arrowend = new Point(arrowstart.x + arrowoffset.x, arrowstart.y + arrowoffset.y);
/*  519:     */               } else {
/*  520: 573 */                 arrowend = new Point(arrowstart.x - arrowoffset.x, arrowstart.y - arrowoffset.y);
/*  521:     */               }
/*  522: 577 */               int[] xs = { arrowstart.x, arrowend.x + (int)(7.0D * Math.cos(angle + 1.570796326794897D)), arrowend.x + (int)(7.0D * Math.cos(angle - 1.570796326794897D)) };
/*  523:     */               
/*  524:     */ 
/*  525:     */ 
/*  526: 581 */               int[] ys = { arrowstart.y, arrowend.y + (int)(7.0D * Math.sin(angle + 1.570796326794897D)), arrowend.y + (int)(7.0D * Math.sin(angle - 1.570796326794897D)) };
/*  527:     */               
/*  528:     */ 
/*  529:     */ 
/*  530: 585 */               gx.fillPolygon(xs, ys, 3);
/*  531:     */               
/*  532:     */ 
/*  533: 588 */               int midx = (int)bestSourcePoint.getX();
/*  534: 589 */               midx += (int)((bestTargetPoint.getX() - bestSourcePoint.getX()) / 2.0D);
/*  535:     */               
/*  536: 591 */               int midy = (int)bestSourcePoint.getY();
/*  537: 592 */               midy += (int)((bestTargetPoint.getY() - bestSourcePoint.getY()) / 2.0D) - 2;
/*  538:     */               
/*  539: 594 */               gx.setColor(active ? Color.blue : Color.gray);
/*  540: 597 */               if (this.m_visLayout.previousConn(outConns, targetI, count)) {
/*  541: 598 */                 midy -= 15;
/*  542:     */               }
/*  543: 600 */               gx.drawString(connName, midx, midy);
/*  544:     */             }
/*  545:     */           }
/*  546: 604 */           count++;
/*  547:     */         }
/*  548:     */       }
/*  549:     */     }
/*  550:     */     StepVisual stepVis;
/*  551:     */     Map<String, List<StepManager>> outConns;
/*  552:     */     List<String> generatableOutputConnections;
/*  553:     */     int count;
/*  554:     */   }
/*  555:     */   
/*  556:     */   protected void canvasContextualMenu(final int x, final int y)
/*  557:     */   {
/*  558: 618 */     Map<String, List<StepManagerImpl[]>> closestConnections = this.m_visLayout.findClosestConnections(new Point(x, y), 10);
/*  559:     */     
/*  560:     */ 
/*  561: 621 */     PopupMenu contextualMenu = new PopupMenu();
/*  562: 622 */     int menuItemCount = 0;
/*  563: 623 */     if (this.m_visLayout.getSelectedSteps().size() > 0)
/*  564:     */     {
/*  565: 624 */       MenuItem snapItem = new MenuItem("Snap selected to grid");
/*  566: 625 */       snapItem.addActionListener(new ActionListener()
/*  567:     */       {
/*  568:     */         public void actionPerformed(ActionEvent e)
/*  569:     */         {
/*  570: 628 */           LayoutPanel.this.snapSelectedToGrid();
/*  571:     */         }
/*  572: 630 */       });
/*  573: 631 */       contextualMenu.add(snapItem);
/*  574: 632 */       menuItemCount++;
/*  575:     */       
/*  576: 634 */       MenuItem copyItem = new MenuItem("Copy selected");
/*  577: 635 */       copyItem.addActionListener(new ActionListener()
/*  578:     */       {
/*  579:     */         public void actionPerformed(ActionEvent e)
/*  580:     */         {
/*  581:     */           try
/*  582:     */           {
/*  583: 639 */             LayoutPanel.this.m_visLayout.copySelectedStepsToClipboard();
/*  584: 640 */             LayoutPanel.this.m_visLayout.setSelectedSteps(new ArrayList());
/*  585:     */           }
/*  586:     */           catch (WekaException ex)
/*  587:     */           {
/*  588: 642 */             LayoutPanel.this.m_visLayout.getMainPerspective().showErrorDialog(ex);
/*  589:     */           }
/*  590:     */         }
/*  591: 645 */       });
/*  592: 646 */       contextualMenu.add(copyItem);
/*  593: 647 */       menuItemCount++;
/*  594:     */       
/*  595: 649 */       MenuItem cutItem = new MenuItem("Cut selected");
/*  596: 650 */       cutItem.addActionListener(new ActionListener()
/*  597:     */       {
/*  598:     */         public void actionPerformed(ActionEvent e)
/*  599:     */         {
/*  600:     */           try
/*  601:     */           {
/*  602: 654 */             LayoutPanel.this.m_visLayout.copySelectedStepsToClipboard();
/*  603: 655 */             LayoutPanel.this.m_visLayout.removeSelectedSteps();
/*  604:     */           }
/*  605:     */           catch (WekaException ex)
/*  606:     */           {
/*  607: 657 */             LayoutPanel.this.m_visLayout.getMainPerspective().showErrorDialog(ex);
/*  608:     */           }
/*  609:     */         }
/*  610: 660 */       });
/*  611: 661 */       contextualMenu.add(cutItem);
/*  612: 662 */       menuItemCount++;
/*  613:     */       
/*  614: 664 */       MenuItem deleteSelected = new MenuItem("Delete selected");
/*  615: 665 */       deleteSelected.addActionListener(new ActionListener()
/*  616:     */       {
/*  617:     */         public void actionPerformed(ActionEvent e)
/*  618:     */         {
/*  619:     */           try
/*  620:     */           {
/*  621: 669 */             LayoutPanel.this.m_visLayout.removeSelectedSteps();
/*  622:     */           }
/*  623:     */           catch (WekaException ex)
/*  624:     */           {
/*  625: 671 */             LayoutPanel.this.m_visLayout.getMainPerspective().showErrorDialog(ex);
/*  626:     */           }
/*  627:     */         }
/*  628: 674 */       });
/*  629: 675 */       contextualMenu.add(deleteSelected);
/*  630: 676 */       menuItemCount++;
/*  631:     */     }
/*  632: 679 */     if ((this.m_visLayout.getMainPerspective().getPasteBuffer() != null) && (this.m_visLayout.getMainPerspective().getPasteBuffer().length() > 0))
/*  633:     */     {
/*  634: 681 */       contextualMenu.addSeparator();
/*  635: 682 */       menuItemCount++;
/*  636:     */       
/*  637: 684 */       MenuItem pasteItem = new MenuItem("Paste");
/*  638: 685 */       pasteItem.addActionListener(new ActionListener()
/*  639:     */       {
/*  640:     */         public void actionPerformed(ActionEvent e)
/*  641:     */         {
/*  642:     */           try
/*  643:     */           {
/*  644: 690 */             LayoutPanel.this.m_visLayout.pasteFromClipboard(x, y);
/*  645:     */           }
/*  646:     */           catch (WekaException ex)
/*  647:     */           {
/*  648: 692 */             LayoutPanel.this.m_visLayout.getMainPerspective().showErrorDialog(ex);
/*  649:     */           }
/*  650:     */         }
/*  651: 695 */       });
/*  652: 696 */       contextualMenu.add(pasteItem);
/*  653: 697 */       menuItemCount++;
/*  654:     */     }
/*  655: 700 */     if (closestConnections.size() > 0)
/*  656:     */     {
/*  657: 701 */       contextualMenu.addSeparator();
/*  658: 702 */       menuItemCount++;
/*  659:     */       
/*  660: 704 */       MenuItem deleteConnection = new MenuItem("Delete connection:");
/*  661: 705 */       deleteConnection.setEnabled(false);
/*  662: 706 */       contextualMenu.insert(deleteConnection, menuItemCount++);
/*  663: 708 */       for (Map.Entry<String, List<StepManagerImpl[]>> e : closestConnections.entrySet())
/*  664:     */       {
/*  665: 710 */         connName = (String)e.getKey();
/*  666: 711 */         for (StepManagerImpl[] cons : (List)e.getValue())
/*  667:     */         {
/*  668: 712 */           final StepManagerImpl source = cons[0];
/*  669: 713 */           final StepManagerImpl target = cons[1];
/*  670:     */           
/*  671: 715 */           MenuItem deleteItem = new MenuItem(connName + "-->" + target.getName());
/*  672:     */           
/*  673: 717 */           deleteItem.addActionListener(new ActionListener()
/*  674:     */           {
/*  675:     */             public void actionPerformed(ActionEvent e)
/*  676:     */             {
/*  677: 720 */               LayoutPanel.this.m_visLayout.addUndoPoint();
/*  678:     */               
/*  679: 722 */               source.disconnectStepWithConnection(target.getManagedStep(), connName);
/*  680:     */               
/*  681: 724 */               target.disconnectStepWithConnection(source.getManagedStep(), connName);
/*  682: 726 */               if (LayoutPanel.this.m_visLayout.getSelectedSteps().size() > 0) {
/*  683: 727 */                 LayoutPanel.this.m_visLayout.setSelectedSteps(new ArrayList());
/*  684:     */               }
/*  685: 729 */               LayoutPanel.this.m_visLayout.setEdited(true);
/*  686: 730 */               LayoutPanel.this.revalidate();
/*  687: 731 */               LayoutPanel.this.repaint();
/*  688: 732 */               LayoutPanel.this.m_visLayout.getMainPerspective().notifyIsDirty();
/*  689:     */             }
/*  690: 734 */           });
/*  691: 735 */           contextualMenu.add(deleteItem);
/*  692: 736 */           menuItemCount++;
/*  693:     */         }
/*  694:     */       }
/*  695:     */     }
/*  696:     */     final String connName;
/*  697: 741 */     if (menuItemCount > 0)
/*  698:     */     {
/*  699: 742 */       contextualMenu.addSeparator();
/*  700: 743 */       menuItemCount++;
/*  701:     */     }
/*  702: 746 */     MenuItem noteItem = new MenuItem("New note");
/*  703: 747 */     noteItem.addActionListener(new ActionListener()
/*  704:     */     {
/*  705:     */       public void actionPerformed(ActionEvent e)
/*  706:     */       {
/*  707: 750 */         LayoutPanel.this.initiateAddNote();
/*  708:     */       }
/*  709: 752 */     });
/*  710: 753 */     contextualMenu.add(noteItem);
/*  711:     */     
/*  712: 755 */     add(contextualMenu);
/*  713:     */     
/*  714:     */ 
/*  715: 758 */     double z = this.m_visLayout.getZoomSetting() / 100.0D;
/*  716: 759 */     double px = x * z;
/*  717: 760 */     double py = y * z;
/*  718: 761 */     contextualMenu.show(this, (int)px, (int)py);
/*  719:     */   }
/*  720:     */   
/*  721:     */   protected void initiateAddNote()
/*  722:     */   {
/*  723: 768 */     Note n = new Note();
/*  724: 769 */     StepManagerImpl noteManager = new StepManagerImpl(n);
/*  725: 770 */     StepVisual noteVisual = StepVisual.createVisual(noteManager);
/*  726: 771 */     this.m_visLayout.getMainPerspective().setPalleteSelectedStep(noteVisual.getStepManager());
/*  727:     */     
/*  728: 773 */     this.m_visLayout.setFlowLayoutOperation(VisibleLayout.LayoutOperation.ADDING);
/*  729: 774 */     this.m_visLayout.getMainPerspective().setCursor(Cursor.getPredefinedCursor(1));
/*  730:     */   }
/*  731:     */   
/*  732:     */   protected void stepContextualMenu(final StepVisual step, final int x, final int y)
/*  733:     */   {
/*  734: 787 */     PopupMenu stepContextMenu = new PopupMenu();
/*  735: 788 */     boolean executing = this.m_visLayout.isExecuting();
/*  736:     */     
/*  737: 790 */     int menuItemCount = 0;
/*  738: 791 */     MenuItem edit = new MenuItem("Edit:");
/*  739: 792 */     edit.setEnabled(false);
/*  740: 793 */     stepContextMenu.insert(edit, menuItemCount);
/*  741: 794 */     menuItemCount++;
/*  742: 796 */     if (this.m_visLayout.getSelectedSteps().size() > 0)
/*  743:     */     {
/*  744: 797 */       MenuItem copyItem = new MenuItem("Copy");
/*  745: 798 */       copyItem.addActionListener(new ActionListener()
/*  746:     */       {
/*  747:     */         public void actionPerformed(ActionEvent e)
/*  748:     */         {
/*  749:     */           try
/*  750:     */           {
/*  751: 802 */             LayoutPanel.this.m_visLayout.copySelectedStepsToClipboard();
/*  752:     */           }
/*  753:     */           catch (WekaException ex)
/*  754:     */           {
/*  755: 804 */             LayoutPanel.this.m_visLayout.getMainPerspective().showErrorDialog(ex);
/*  756:     */           }
/*  757: 806 */           LayoutPanel.this.m_visLayout.setSelectedSteps(new ArrayList());
/*  758:     */         }
/*  759: 808 */       });
/*  760: 809 */       stepContextMenu.add(copyItem);
/*  761: 810 */       copyItem.setEnabled(!executing);
/*  762: 811 */       menuItemCount++;
/*  763:     */     }
/*  764: 814 */     MenuItem deleteItem = new MenuItem("Delete");
/*  765: 815 */     deleteItem.addActionListener(new ActionListener()
/*  766:     */     {
/*  767:     */       public void actionPerformed(ActionEvent e)
/*  768:     */       {
/*  769: 819 */         LayoutPanel.this.m_visLayout.addUndoPoint();
/*  770:     */         try
/*  771:     */         {
/*  772: 822 */           LayoutPanel.this.m_visLayout.removeStep(step);
/*  773:     */         }
/*  774:     */         catch (WekaException ex)
/*  775:     */         {
/*  776: 824 */           LayoutPanel.this.m_visLayout.getMainPerspective().showErrorDialog(ex);
/*  777:     */         }
/*  778: 828 */         String key = step.getStepName() + "$" + step.getStepManager().getManagedStep().hashCode();
/*  779:     */         
/*  780:     */ 
/*  781: 831 */         LayoutPanel.this.m_visLayout.getLogPanel().statusMessage(key + "|remove");
/*  782:     */         
/*  783: 833 */         LayoutPanel.this.revalidate();
/*  784: 834 */         LayoutPanel.this.repaint();
/*  785:     */         
/*  786: 836 */         LayoutPanel.this.m_visLayout.setEdited(true);
/*  787: 837 */         LayoutPanel.this.m_visLayout.getMainPerspective().notifyIsDirty();
/*  788: 838 */         LayoutPanel.this.m_visLayout.getMainPerspective().getMainToolBar().enableWidget(MainKFPerspectiveToolBar.Widgets.SELECT_ALL_BUTTON.toString(), LayoutPanel.this.m_visLayout.getSelectedSteps().size() > 0);
/*  789:     */       }
/*  790: 845 */     });
/*  791: 846 */     deleteItem.setEnabled(!executing);
/*  792: 847 */     stepContextMenu.add(deleteItem);
/*  793: 848 */     menuItemCount++;
/*  794:     */     
/*  795: 850 */     MenuItem nameItem = new MenuItem("Set name...");
/*  796: 851 */     nameItem.addActionListener(new ActionListener()
/*  797:     */     {
/*  798:     */       public void actionPerformed(ActionEvent e)
/*  799:     */       {
/*  800: 854 */         String oldName = step.getStepName();
/*  801: 855 */         String name = JOptionPane.showInputDialog(LayoutPanel.this.m_visLayout.getMainPerspective(), "Enter a name for this step", oldName);
/*  802: 858 */         if (name != null)
/*  803:     */         {
/*  804: 859 */           LayoutPanel.this.m_visLayout.renameStep(oldName, name);
/*  805: 860 */           LayoutPanel.this.m_visLayout.setEdited(true);
/*  806: 861 */           LayoutPanel.this.revalidate();
/*  807: 862 */           LayoutPanel.this.repaint();
/*  808:     */         }
/*  809:     */       }
/*  810: 865 */     });
/*  811: 866 */     nameItem.setEnabled(!executing);
/*  812: 867 */     stepContextMenu.add(nameItem);
/*  813: 868 */     menuItemCount++;
/*  814:     */     
/*  815: 870 */     MenuItem configItem = new MenuItem("Configure...");
/*  816: 871 */     configItem.addActionListener(new ActionListener()
/*  817:     */     {
/*  818:     */       public void actionPerformed(ActionEvent e)
/*  819:     */       {
/*  820: 874 */         LayoutPanel.this.popupStepEditorDialog(step);
/*  821:     */         
/*  822: 876 */         LayoutPanel.this.m_visLayout.getMainPerspective().notifyIsDirty();
/*  823:     */       }
/*  824: 879 */     });
/*  825: 880 */     configItem.setEnabled(!executing);
/*  826: 881 */     stepContextMenu.add(configItem);
/*  827: 882 */     menuItemCount++;
/*  828:     */     
/*  829:     */ 
/*  830: 885 */     List<String> outgoingConnTypes = step.getStepManager().getManagedStep().getOutgoingConnectionTypes();
/*  831: 887 */     if ((outgoingConnTypes != null) && (outgoingConnTypes.size() > 0))
/*  832:     */     {
/*  833: 888 */       MenuItem connections = new MenuItem("Connections:");
/*  834: 889 */       connections.setEnabled(false);
/*  835: 890 */       stepContextMenu.insert(connections, menuItemCount);
/*  836: 891 */       menuItemCount++;
/*  837: 893 */       for (final String connType : outgoingConnTypes)
/*  838:     */       {
/*  839: 894 */         MenuItem conItem = new MenuItem(connType);
/*  840: 895 */         conItem.setEnabled(!executing);
/*  841: 896 */         conItem.addActionListener(new ActionListener()
/*  842:     */         {
/*  843:     */           public void actionPerformed(ActionEvent e)
/*  844:     */           {
/*  845: 899 */             LayoutPanel.this.initiateConnection(connType, step, x, y);
/*  846: 900 */             LayoutPanel.this.m_visLayout.getMainPerspective().notifyIsDirty();
/*  847:     */           }
/*  848: 902 */         });
/*  849: 903 */         stepContextMenu.add(conItem);
/*  850: 904 */         menuItemCount++;
/*  851:     */       }
/*  852:     */     }
/*  853: 909 */     Map<String, String> interactiveViews = step.getStepManager().getManagedStep().getInteractiveViewers();
/*  854:     */     
/*  855: 911 */     Map<String, StepInteractiveViewer> interactiveViewsImpls = step.getStepManager().getManagedStep().getInteractiveViewersImpls();
/*  856: 913 */     if ((interactiveViews != null) && (interactiveViews.size() > 0))
/*  857:     */     {
/*  858: 914 */       MenuItem actions = new MenuItem("Actions:");
/*  859: 915 */       actions.setEnabled(false);
/*  860: 916 */       stepContextMenu.insert(actions, menuItemCount++);
/*  861: 918 */       for (Map.Entry<String, String> e : interactiveViews.entrySet())
/*  862:     */       {
/*  863: 919 */         String command = (String)e.getKey();
/*  864: 920 */         String viewerClassName = (String)e.getValue();
/*  865: 921 */         addInteractiveViewMenuItem(step, (String)e.getKey(), !executing, (String)e.getValue(), null, stepContextMenu);
/*  866:     */         
/*  867: 923 */         menuItemCount++;
/*  868:     */       }
/*  869:     */     }
/*  870: 925 */     else if ((interactiveViewsImpls != null) && (interactiveViewsImpls.size() > 0))
/*  871:     */     {
/*  872: 927 */       MenuItem actions = new MenuItem("Actions:");
/*  873: 928 */       actions.setEnabled(false);
/*  874: 929 */       stepContextMenu.insert(actions, menuItemCount++);
/*  875: 931 */       for (Map.Entry<String, StepInteractiveViewer> e : interactiveViewsImpls.entrySet())
/*  876:     */       {
/*  877: 933 */         String command = (String)e.getKey();
/*  878: 934 */         StepInteractiveViewer impl = (StepInteractiveViewer)e.getValue();
/*  879: 935 */         addInteractiveViewMenuItem(step, (String)e.getKey(), !executing, null, impl, stepContextMenu);
/*  880:     */         
/*  881: 937 */         menuItemCount++;
/*  882:     */       }
/*  883:     */     }
/*  884: 942 */     final List<Perspective> perspectives = this.m_visLayout.getMainPerspective().getMainApplication().getPerspectiveManager().getVisiblePerspectives();
/*  885: 945 */     if ((perspectives.size() > 0) && ((step.getStepManager().getManagedStep() instanceof weka.knowledgeflow.steps.Loader)))
/*  886:     */     {
/*  887: 947 */       final weka.core.converters.Loader theLoader = ((weka.knowledgeflow.steps.Loader)step.getStepManager().getManagedStep()).getLoader();
/*  888:     */       
/*  889:     */ 
/*  890: 950 */       boolean ok = true;
/*  891: 951 */       if ((theLoader instanceof FileSourcedConverter))
/*  892:     */       {
/*  893: 952 */         String fileName = ((FileSourcedConverter)theLoader).retrieveFile().getPath();
/*  894:     */         
/*  895: 954 */         fileName = this.m_visLayout.environmentSubstitute(fileName);
/*  896: 955 */         File tempF = new File(fileName);
/*  897: 956 */         String fileNameFixedPathSep = fileName.replace(File.separatorChar, '/');
/*  898: 957 */         if ((!tempF.isFile()) && (getClass().getClassLoader().getResource(fileNameFixedPathSep) == null)) {
/*  899: 959 */           ok = false;
/*  900:     */         }
/*  901:     */       }
/*  902: 962 */       if (ok)
/*  903:     */       {
/*  904: 963 */         stepContextMenu.addSeparator();
/*  905: 964 */         menuItemCount++;
/*  906: 966 */         if (perspectives.size() > 1)
/*  907:     */         {
/*  908: 967 */           MenuItem sendToAllPerspectives = new MenuItem("Send to all perspectives");
/*  909:     */           
/*  910: 969 */           menuItemCount++;
/*  911: 970 */           sendToAllPerspectives.addActionListener(new ActionListener()
/*  912:     */           {
/*  913:     */             public void actionPerformed(ActionEvent e)
/*  914:     */             {
/*  915: 973 */               LayoutPanel.this.loadDataAndSendToPerspective(theLoader, perspectives, -1);
/*  916:     */             }
/*  917: 975 */           });
/*  918: 976 */           stepContextMenu.add(sendToAllPerspectives);
/*  919:     */         }
/*  920: 978 */         Menu sendToPerspective = new Menu("Send to perspective...");
/*  921: 979 */         stepContextMenu.add(sendToPerspective);
/*  922: 980 */         menuItemCount++;
/*  923: 981 */         for (int i = 0; i < perspectives.size(); i++)
/*  924:     */         {
/*  925: 982 */           final int pIndex = i;
/*  926: 984 */           if ((((Perspective)perspectives.get(i)).acceptsInstances()) && (!((Perspective)perspectives.get(i)).getPerspectiveID().equalsIgnoreCase("knowledgeflow.main")))
/*  927:     */           {
/*  928: 987 */             String pName = ((Perspective)perspectives.get(i)).getPerspectiveTitle();
/*  929: 988 */             MenuItem pI = new MenuItem(pName);
/*  930: 989 */             pI.addActionListener(new ActionListener()
/*  931:     */             {
/*  932:     */               public void actionPerformed(ActionEvent e)
/*  933:     */               {
/*  934: 992 */                 LayoutPanel.this.loadDataAndSendToPerspective(theLoader, perspectives, pIndex);
/*  935:     */               }
/*  936: 994 */             });
/*  937: 995 */             sendToPerspective.add(pI);
/*  938:     */           }
/*  939:     */         }
/*  940:     */       }
/*  941:     */     }
/*  942:1001 */     if (menuItemCount > 0)
/*  943:     */     {
/*  944:1003 */       double z = this.m_visLayout.getZoomSetting() / 100.0D;
/*  945:1004 */       double px = x * z;
/*  946:1005 */       double py = y * z;
/*  947:     */       
/*  948:1007 */       add(stepContextMenu);
/*  949:1008 */       stepContextMenu.show(this, (int)px, (int)py);
/*  950:     */     }
/*  951:     */   }
/*  952:     */   
/*  953:     */   private synchronized void loadDataAndSendToPerspective(final weka.core.converters.Loader loader, final List<Perspective> perspectives, final int perspectiveIndex)
/*  954:     */   {
/*  955:1015 */     if (this.m_perspectiveDataLoadThread == null)
/*  956:     */     {
/*  957:1016 */       this.m_perspectiveDataLoadThread = new Thread()
/*  958:     */       {
/*  959:     */         public void run()
/*  960:     */         {
/*  961:     */           try
/*  962:     */           {
/*  963:1020 */             if ((loader instanceof EnvironmentHandler))
/*  964:     */             {
/*  965:1021 */               ((EnvironmentHandler)loader).setEnvironment(LayoutPanel.this.m_visLayout.getEnvironment());
/*  966:     */               
/*  967:1023 */               loader.reset();
/*  968:     */             }
/*  969:1025 */             LayoutPanel.this.m_visLayout.getLogPanel().statusMessage("@!@[KnowledgeFlow]|Sending data to perspective(s)...");
/*  970:     */             
/*  971:1027 */             Instances data = loader.getDataSet();
/*  972:1028 */             if (data != null)
/*  973:     */             {
/*  974:1029 */               LayoutPanel.this.m_visLayout.getMainPerspective().getMainApplication().showPerspectivesToolBar();
/*  975:1036 */               if (perspectiveIndex < 0)
/*  976:     */               {
/*  977:1038 */                 for (Perspective p : perspectives) {
/*  978:1039 */                   if (p.acceptsInstances())
/*  979:     */                   {
/*  980:1040 */                     p.setInstances(data);
/*  981:1041 */                     LayoutPanel.this.m_visLayout.getMainPerspective().getMainApplication().getPerspectiveManager().setEnablePerspectiveTab(p.getPerspectiveID(), true);
/*  982:     */                   }
/*  983:     */                 }
/*  984:     */               }
/*  985:     */               else
/*  986:     */               {
/*  987:1047 */                 ((Perspective)perspectives.get(perspectiveIndex)).setInstances(data);
/*  988:1048 */                 LayoutPanel.this.m_visLayout.getMainPerspective().getMainApplication().getPerspectiveManager().setActivePerspective(((Perspective)perspectives.get(perspectiveIndex)).getPerspectiveID());
/*  989:     */                 
/*  990:     */ 
/*  991:     */ 
/*  992:     */ 
/*  993:     */ 
/*  994:1054 */                 LayoutPanel.this.m_visLayout.getMainPerspective().getMainApplication().getPerspectiveManager().setEnablePerspectiveTab(((Perspective)perspectives.get(perspectiveIndex)).getPerspectiveID(), true);
/*  995:     */               }
/*  996:     */             }
/*  997:     */           }
/*  998:     */           catch (Exception ex)
/*  999:     */           {
/* 1000:1063 */             LayoutPanel.this.m_visLayout.getMainPerspective().showErrorDialog(ex);
/* 1001:     */           }
/* 1002:     */           finally
/* 1003:     */           {
/* 1004:1070 */             LayoutPanel.this.m_perspectiveDataLoadThread = null;
/* 1005:1071 */             LayoutPanel.this.m_visLayout.getLogPanel().statusMessage("@!@[KnowledgeFlow]|OK");
/* 1006:     */           }
/* 1007:     */         }
/* 1008:1074 */       };
/* 1009:1075 */       this.m_perspectiveDataLoadThread.setPriority(1);
/* 1010:1076 */       this.m_perspectiveDataLoadThread.start();
/* 1011:     */     }
/* 1012:     */   }
/* 1013:     */   
/* 1014:     */   protected void initiateConnection(String connType, StepVisual source, int x, int y)
/* 1015:     */   {
/* 1016:1091 */     if (this.m_visLayout.getSelectedSteps().size() > 0) {
/* 1017:1092 */       this.m_visLayout.setSelectedSteps(new ArrayList());
/* 1018:     */     }
/* 1019:1095 */     List<StepManagerImpl> connectableForConnType = this.m_visLayout.findStepsThatCanAcceptConnection(connType);
/* 1020:1098 */     for (StepManagerImpl sm : connectableForConnType) {
/* 1021:1099 */       sm.getStepVisual().setDisplayConnectors(true);
/* 1022:     */     }
/* 1023:1102 */     if (connectableForConnType.size() > 0)
/* 1024:     */     {
/* 1025:1103 */       source.setDisplayConnectors(true);
/* 1026:1104 */       this.m_visLayout.setEditStep(source);
/* 1027:1105 */       this.m_visLayout.setEditConnection(connType);
/* 1028:1106 */       Point closest = source.getClosestConnectorPoint(new Point(x, y));
/* 1029:1107 */       this.m_currentX = ((int)closest.getX());
/* 1030:1108 */       this.m_currentY = ((int)closest.getY());
/* 1031:1109 */       this.m_oldX = this.m_currentX;
/* 1032:1110 */       this.m_oldY = this.m_currentY;
/* 1033:1111 */       Graphics2D gx = (Graphics2D)getGraphics();
/* 1034:1112 */       gx.setXORMode(Color.white);
/* 1035:1113 */       gx.drawLine(this.m_currentX, this.m_currentY, this.m_currentX, this.m_currentY);
/* 1036:1114 */       gx.dispose();
/* 1037:1115 */       this.m_visLayout.setFlowLayoutOperation(VisibleLayout.LayoutOperation.CONNECTING);
/* 1038:     */     }
/* 1039:1118 */     revalidate();
/* 1040:1119 */     repaint();
/* 1041:1120 */     this.m_visLayout.getMainPerspective().notifyIsDirty();
/* 1042:     */   }
/* 1043:     */   
/* 1044:     */   protected void addInteractiveViewMenuItem(final StepVisual step, String entryText, boolean enabled, final String viewerClassName, final StepInteractiveViewer viewerImpl, PopupMenu stepContextMenu)
/* 1045:     */   {
/* 1046:1141 */     MenuItem viewItem = new MenuItem(entryText);
/* 1047:1142 */     viewItem.setEnabled(enabled);
/* 1048:1143 */     viewItem.addActionListener(new ActionListener()
/* 1049:     */     {
/* 1050:     */       public void actionPerformed(ActionEvent e)
/* 1051:     */       {
/* 1052:1146 */         LayoutPanel.this.popupStepInteractiveViewer(step, viewerClassName, viewerImpl);
/* 1053:     */       }
/* 1054:1148 */     });
/* 1055:1149 */     stepContextMenu.add(viewItem);
/* 1056:     */   }
/* 1057:     */   
/* 1058:     */   protected void popupStepInteractiveViewer(StepVisual step, String viewerClassName, StepInteractiveViewer viewerImpl)
/* 1059:     */   {
/* 1060:     */     try
/* 1061:     */     {
/* 1062:1162 */       Object viewer = viewerClassName != null ? Beans.instantiate(getClass().getClassLoader(), viewerClassName) : viewerImpl;
/* 1063:1165 */       if (!(viewer instanceof StepInteractiveViewer)) {
/* 1064:1166 */         throw new WekaException("Interactive step viewer component " + viewerClassName + " must implement StepInteractiveViewer");
/* 1065:     */       }
/* 1066:1170 */       if (!(viewer instanceof JComponent)) {
/* 1067:1171 */         throw new WekaException("Interactive step viewer component " + viewerClassName + " must be a JComponent");
/* 1068:     */       }
/* 1069:1175 */       String viewerName = ((StepInteractiveViewer)viewer).getViewerName();
/* 1070:1176 */       ((StepInteractiveViewer)viewer).setStep(step.getStepManager().getManagedStep());
/* 1071:     */       
/* 1072:1178 */       ((StepInteractiveViewer)viewer).setMainKFPerspective(this.m_visLayout.getMainPerspective());
/* 1073:     */       
/* 1074:1180 */       JFrame jf = new JFrame(viewerName);
/* 1075:1181 */       ((StepInteractiveViewer)viewer).setParentWindow(jf);
/* 1076:1182 */       ((StepInteractiveViewer)viewer).init();
/* 1077:1183 */       jf.setLayout(new BorderLayout());
/* 1078:1184 */       jf.add((JComponent)viewer, "Center");
/* 1079:1185 */       jf.pack();
/* 1080:1186 */       jf.setVisible(true);
/* 1081:1187 */       ((StepInteractiveViewer)viewer).nowVisible();
/* 1082:     */     }
/* 1083:     */     catch (IOException e)
/* 1084:     */     {
/* 1085:1189 */       e.printStackTrace();
/* 1086:     */     }
/* 1087:     */     catch (Exception e)
/* 1088:     */     {
/* 1089:1191 */       this.m_visLayout.getMainPerspective().showErrorDialog(e);
/* 1090:     */     }
/* 1091:     */   }
/* 1092:     */   
/* 1093:     */   protected void popupStepEditorDialog(StepVisual step)
/* 1094:     */   {
/* 1095:1201 */     String custEditor = step.getStepManager().getManagedStep().getCustomEditorForStep();
/* 1096:     */     
/* 1097:     */ 
/* 1098:1204 */     StepEditorDialog toPopup = null;
/* 1099:1205 */     if ((custEditor != null) && (custEditor.length() > 0))
/* 1100:     */     {
/* 1101:     */       try
/* 1102:     */       {
/* 1103:1207 */         Object custPanel = Beans.instantiate(getClass().getClassLoader(), custEditor);
/* 1104:1210 */         if (!(custPanel instanceof StepEditorDialog)) {
/* 1105:1211 */           throw new WekaException("Custom editor must be a subclass of StepEditorDialog");
/* 1106:     */         }
/* 1107:1215 */         toPopup = (StepEditorDialog)custPanel;
/* 1108:     */       }
/* 1109:     */       catch (Exception ex)
/* 1110:     */       {
/* 1111:1217 */         this.m_visLayout.getMainPerspective().showErrorDialog(ex);
/* 1112:1218 */         ex.printStackTrace();
/* 1113:     */       }
/* 1114:     */     }
/* 1115:     */     else
/* 1116:     */     {
/* 1117:1222 */       toPopup = new GOEStepEditorDialog();
/* 1118:1223 */       toPopup.setMainPerspective(this.m_visLayout.getMainPerspective());
/* 1119:     */     }
/* 1120:1226 */     final JDialog d = new JDialog((Frame)getTopLevelAncestor(), Dialog.ModalityType.DOCUMENT_MODAL);
/* 1121:     */     
/* 1122:     */ 
/* 1123:1229 */     d.setLayout(new BorderLayout());
/* 1124:1230 */     d.getContentPane().add(toPopup, "Center");
/* 1125:1231 */     final StepEditorDialog toPopupC = toPopup;
/* 1126:1232 */     d.addWindowListener(new WindowAdapter()
/* 1127:     */     {
/* 1128:     */       public void windowClosing(WindowEvent e)
/* 1129:     */       {
/* 1130:1235 */         d.dispose();
/* 1131:     */       }
/* 1132:1237 */     });
/* 1133:1238 */     toPopup.setParentWindow(d);
/* 1134:1239 */     toPopup.setEnvironment(this.m_visLayout.getEnvironment());
/* 1135:1240 */     toPopup.setMainPerspective(this.m_visLayout.getMainPerspective());
/* 1136:1241 */     toPopup.setStepToEdit(step.getStepManager().getManagedStep());
/* 1137:     */     
/* 1138:1243 */     toPopup.setClosingListener(new StepEditorDialog.ClosingListener()
/* 1139:     */     {
/* 1140:     */       public void closing()
/* 1141:     */       {
/* 1142:1246 */         if (toPopupC.isEdited()) {
/* 1143:1247 */           LayoutPanel.this.m_visLayout.setEdited(true);
/* 1144:     */         }
/* 1145:     */       }
/* 1146:1250 */     });
/* 1147:1251 */     d.pack();
/* 1148:1252 */     d.setLocationRelativeTo(this.m_visLayout.getMainPerspective());
/* 1149:1253 */     d.setVisible(true);
/* 1150:     */   }
/* 1151:     */   
/* 1152:     */   private int snapToGrid(int val)
/* 1153:     */   {
/* 1154:1257 */     int r = val % this.m_gridSpacing;
/* 1155:1258 */     val /= this.m_gridSpacing;
/* 1156:1259 */     if (r > this.m_gridSpacing / 2) {
/* 1157:1260 */       val++;
/* 1158:     */     }
/* 1159:1262 */     val *= this.m_gridSpacing;
/* 1160:     */     
/* 1161:1264 */     return val;
/* 1162:     */   }
/* 1163:     */   
/* 1164:     */   protected void snapSelectedToGrid()
/* 1165:     */   {
/* 1166:1271 */     List<StepVisual> selected = this.m_visLayout.getSelectedSteps();
/* 1167:1272 */     for (StepVisual s : selected)
/* 1168:     */     {
/* 1169:1273 */       int x = s.getX();
/* 1170:1274 */       int y = s.getY();
/* 1171:1275 */       s.setX(snapToGrid(x));
/* 1172:1276 */       s.setY(snapToGrid(y));
/* 1173:     */     }
/* 1174:1278 */     revalidate();
/* 1175:1279 */     repaint();
/* 1176:1280 */     this.m_visLayout.setEdited(true);
/* 1177:1281 */     this.m_visLayout.getMainPerspective().notifyIsDirty();
/* 1178:     */   }
/* 1179:     */   
/* 1180:     */   private void highlightSubFlow(int startX, int startY, int endX, int endY)
/* 1181:     */   {
/* 1182:1285 */     Rectangle r = new Rectangle(startX < endX ? startX : endX, startY < endY ? startY : endY, Math.abs(startX - endX), Math.abs(startY - endY));
/* 1183:     */     
/* 1184:     */ 
/* 1185:     */ 
/* 1186:     */ 
/* 1187:1290 */     List<StepVisual> selected = this.m_visLayout.findSteps(r);
/* 1188:1291 */     this.m_visLayout.setSelectedSteps(selected);
/* 1189:     */   }
/* 1190:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.LayoutPanel
 * JD-Core Version:    0.7.0.1
 */