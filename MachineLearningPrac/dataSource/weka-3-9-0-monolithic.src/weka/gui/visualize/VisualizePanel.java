/*    1:     */ package weka.gui.visualize;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Color;
/*    5:     */ import java.awt.Component;
/*    6:     */ import java.awt.Container;
/*    7:     */ import java.awt.Dimension;
/*    8:     */ import java.awt.Graphics;
/*    9:     */ import java.awt.GridBagConstraints;
/*   10:     */ import java.awt.GridBagLayout;
/*   11:     */ import java.awt.GridLayout;
/*   12:     */ import java.awt.Insets;
/*   13:     */ import java.awt.event.ActionEvent;
/*   14:     */ import java.awt.event.ActionListener;
/*   15:     */ import java.awt.event.MouseAdapter;
/*   16:     */ import java.awt.event.MouseEvent;
/*   17:     */ import java.awt.event.MouseMotionAdapter;
/*   18:     */ import java.awt.event.WindowAdapter;
/*   19:     */ import java.awt.event.WindowEvent;
/*   20:     */ import java.io.BufferedReader;
/*   21:     */ import java.io.BufferedWriter;
/*   22:     */ import java.io.File;
/*   23:     */ import java.io.FileReader;
/*   24:     */ import java.io.FileWriter;
/*   25:     */ import java.io.PrintStream;
/*   26:     */ import java.io.Reader;
/*   27:     */ import java.io.Writer;
/*   28:     */ import java.util.ArrayList;
/*   29:     */ import java.util.Properties;
/*   30:     */ import java.util.Random;
/*   31:     */ import javax.swing.BorderFactory;
/*   32:     */ import javax.swing.DefaultComboBoxModel;
/*   33:     */ import javax.swing.JButton;
/*   34:     */ import javax.swing.JComboBox;
/*   35:     */ import javax.swing.JFileChooser;
/*   36:     */ import javax.swing.JFrame;
/*   37:     */ import javax.swing.JLabel;
/*   38:     */ import javax.swing.JOptionPane;
/*   39:     */ import javax.swing.JPanel;
/*   40:     */ import javax.swing.JSlider;
/*   41:     */ import javax.swing.event.ChangeEvent;
/*   42:     */ import javax.swing.event.ChangeListener;
/*   43:     */ import javax.swing.filechooser.FileFilter;
/*   44:     */ import weka.core.Attribute;
/*   45:     */ import weka.core.Instance;
/*   46:     */ import weka.core.Instances;
/*   47:     */ import weka.core.Settings;
/*   48:     */ import weka.core.logging.Logger.Level;
/*   49:     */ import weka.gui.ExtensionFileFilter;
/*   50:     */ 
/*   51:     */ public class VisualizePanel
/*   52:     */   extends PrintablePanel
/*   53:     */ {
/*   54:     */   private static final long serialVersionUID = 240108358588153943L;
/*   55:     */   
/*   56:     */   protected class PlotPanel
/*   57:     */     extends PrintablePanel
/*   58:     */     implements Plot2DCompanion
/*   59:     */   {
/*   60:     */     private static final long serialVersionUID = -4823674171136494204L;
/*   61: 102 */     protected Plot2D m_plot2D = new Plot2D();
/*   62: 105 */     protected Instances m_plotInstances = null;
/*   63: 108 */     protected PlotData2D m_originalPlot = null;
/*   64: 114 */     protected int m_xIndex = 0;
/*   65: 115 */     protected int m_yIndex = 0;
/*   66: 116 */     protected int m_cIndex = 0;
/*   67: 117 */     protected int m_sIndex = 0;
/*   68:     */     private boolean m_createShape;
/*   69:     */     private ArrayList<ArrayList<Double>> m_shapes;
/*   70:     */     private ArrayList<Double> m_shapePoints;
/*   71:     */     private final Dimension m_newMousePos;
/*   72:     */     
/*   73:     */     public PlotPanel()
/*   74:     */     {
/*   75: 139 */       setBackground(this.m_plot2D.getBackground());
/*   76: 140 */       setLayout(new BorderLayout());
/*   77: 141 */       add(this.m_plot2D, "Center");
/*   78: 142 */       this.m_plot2D.setPlotCompanion(this);
/*   79:     */       
/*   80: 144 */       this.m_createShape = false;
/*   81: 145 */       this.m_shapes = null;
/*   82: 146 */       this.m_shapePoints = null;
/*   83: 147 */       this.m_newMousePos = new Dimension();
/*   84:     */       
/*   85: 149 */       addMouseListener(new MouseAdapter()
/*   86:     */       {
/*   87:     */         public void mousePressed(MouseEvent e)
/*   88:     */         {
/*   89: 153 */           if ((e.getModifiers() & 0x10) == 16) {
/*   90: 155 */             if (VisualizePanel.PlotPanel.this.m_sIndex != 0) {
/*   91: 157 */               if (VisualizePanel.PlotPanel.this.m_sIndex == 1)
/*   92:     */               {
/*   93: 158 */                 VisualizePanel.PlotPanel.this.m_createShape = true;
/*   94: 159 */                 VisualizePanel.PlotPanel.this.m_shapePoints = new ArrayList(5);
/*   95: 160 */                 VisualizePanel.PlotPanel.this.m_shapePoints.add(new Double(VisualizePanel.PlotPanel.this.m_sIndex));
/*   96: 161 */                 VisualizePanel.PlotPanel.this.m_shapePoints.add(new Double(e.getX()));
/*   97: 162 */                 VisualizePanel.PlotPanel.this.m_shapePoints.add(new Double(e.getY()));
/*   98: 163 */                 VisualizePanel.PlotPanel.this.m_shapePoints.add(new Double(e.getX()));
/*   99: 164 */                 VisualizePanel.PlotPanel.this.m_shapePoints.add(new Double(e.getY()));
/*  100:     */                 
/*  101: 166 */                 Graphics g = VisualizePanel.PlotPanel.this.m_plot2D.getGraphics();
/*  102: 167 */                 g.setColor(Color.black);
/*  103: 168 */                 g.setXORMode(Color.white);
/*  104: 169 */                 g.drawRect(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(1)).intValue(), ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(2)).intValue(), ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(3)).intValue() - ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(1)).intValue(), ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(4)).intValue() - ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(2)).intValue());
/*  105:     */                 
/*  106:     */ 
/*  107:     */ 
/*  108: 173 */                 g.dispose();
/*  109:     */               }
/*  110:     */             }
/*  111:     */           }
/*  112:     */         }
/*  113:     */         
/*  114:     */         public void mouseClicked(MouseEvent e)
/*  115:     */         {
/*  116: 184 */           if (((VisualizePanel.PlotPanel.this.m_sIndex == 2) || (VisualizePanel.PlotPanel.this.m_sIndex == 3)) && ((VisualizePanel.PlotPanel.this.m_createShape) || ((e.getModifiers() & 0x10) == 16)))
/*  117:     */           {
/*  118: 186 */             if (VisualizePanel.PlotPanel.this.m_createShape)
/*  119:     */             {
/*  120: 189 */               Graphics g = VisualizePanel.PlotPanel.this.m_plot2D.getGraphics();
/*  121: 190 */               g.setColor(Color.black);
/*  122: 191 */               g.setXORMode(Color.white);
/*  123: 192 */               if (((e.getModifiers() & 0x10) == 16) && (!e.isAltDown()))
/*  124:     */               {
/*  125: 194 */                 VisualizePanel.PlotPanel.this.m_shapePoints.add(new Double(VisualizePanel.PlotPanel.this.m_plot2D.convertToAttribX(e.getX())));
/*  126:     */                 
/*  127:     */ 
/*  128: 197 */                 VisualizePanel.PlotPanel.this.m_shapePoints.add(new Double(VisualizePanel.PlotPanel.this.m_plot2D.convertToAttribY(e.getY())));
/*  129:     */                 
/*  130:     */ 
/*  131: 200 */                 VisualizePanel.PlotPanel.this.m_newMousePos.width = e.getX();
/*  132: 201 */                 VisualizePanel.PlotPanel.this.m_newMousePos.height = e.getY();
/*  133: 202 */                 g.drawLine((int)Math.ceil(VisualizePanel.PlotPanel.this.m_plot2D.convertToPanelX(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 2)).doubleValue())), (int)Math.ceil(VisualizePanel.PlotPanel.this.m_plot2D.convertToPanelY(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 1)).doubleValue())), VisualizePanel.PlotPanel.this.m_newMousePos.width, VisualizePanel.PlotPanel.this.m_newMousePos.height);
/*  134:     */               }
/*  135: 210 */               else if (VisualizePanel.PlotPanel.this.m_sIndex == 3)
/*  136:     */               {
/*  137: 216 */                 VisualizePanel.PlotPanel.this.m_createShape = false;
/*  138: 217 */                 if (VisualizePanel.PlotPanel.this.m_shapePoints.size() >= 5)
/*  139:     */                 {
/*  140: 218 */                   double cx = Math.ceil(VisualizePanel.PlotPanel.this.m_plot2D.convertToPanelX(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 4)).doubleValue()));
/*  141:     */                   
/*  142:     */ 
/*  143:     */ 
/*  144: 222 */                   double cx2 = Math.ceil(VisualizePanel.PlotPanel.this.m_plot2D.convertToPanelX(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 2)).doubleValue())) - cx;
/*  145:     */                   
/*  146:     */ 
/*  147:     */ 
/*  148:     */ 
/*  149: 227 */                   cx2 *= 50000.0D;
/*  150:     */                   
/*  151: 229 */                   double cy = Math.ceil(VisualizePanel.PlotPanel.this.m_plot2D.convertToPanelY(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 3)).doubleValue()));
/*  152:     */                   
/*  153:     */ 
/*  154: 232 */                   double cy2 = Math.ceil(VisualizePanel.PlotPanel.this.m_plot2D.convertToPanelY(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 1)).doubleValue())) - cy;
/*  155:     */                   
/*  156:     */ 
/*  157:     */ 
/*  158: 236 */                   cy2 *= 50000.0D;
/*  159:     */                   
/*  160: 238 */                   double cxa = Math.ceil(VisualizePanel.PlotPanel.this.m_plot2D.convertToPanelX(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(3)).doubleValue()));
/*  161:     */                   
/*  162:     */ 
/*  163: 241 */                   double cxa2 = Math.ceil(VisualizePanel.PlotPanel.this.m_plot2D.convertToPanelX(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(1)).doubleValue())) - cxa;
/*  164:     */                   
/*  165:     */ 
/*  166: 244 */                   cxa2 *= 50000.0D;
/*  167:     */                   
/*  168: 246 */                   double cya = Math.ceil(VisualizePanel.PlotPanel.this.m_plot2D.convertToPanelY(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(4)).doubleValue()));
/*  169:     */                   
/*  170:     */ 
/*  171: 249 */                   double cya2 = Math.ceil(VisualizePanel.PlotPanel.this.m_plot2D.convertToPanelY(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(2)).doubleValue())) - cya;
/*  172:     */                   
/*  173:     */ 
/*  174:     */ 
/*  175: 253 */                   cya2 *= 50000.0D;
/*  176:     */                   
/*  177: 255 */                   VisualizePanel.PlotPanel.this.m_shapePoints.set(1, new Double(VisualizePanel.PlotPanel.this.m_plot2D.convertToAttribX(cxa2 + cxa)));
/*  178:     */                   
/*  179:     */ 
/*  180: 258 */                   VisualizePanel.PlotPanel.this.m_shapePoints.set(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 1, new Double(VisualizePanel.PlotPanel.this.m_plot2D.convertToAttribY(cy2 + cy)));
/*  181:     */                   
/*  182:     */ 
/*  183: 261 */                   VisualizePanel.PlotPanel.this.m_shapePoints.set(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 2, new Double(VisualizePanel.PlotPanel.this.m_plot2D.convertToAttribX(cx2 + cx)));
/*  184:     */                   
/*  185:     */ 
/*  186: 264 */                   VisualizePanel.PlotPanel.this.m_shapePoints.set(2, new Double(VisualizePanel.PlotPanel.this.m_plot2D.convertToAttribY(cya2 + cya)));
/*  187:     */                   
/*  188:     */ 
/*  189:     */ 
/*  190:     */ 
/*  191: 269 */                   cy = (1.0D / 0.0D);
/*  192: 270 */                   cy2 = (-1.0D / 0.0D);
/*  193: 271 */                   if (((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(1)).doubleValue() > ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(3)).doubleValue()) {
/*  194: 273 */                     if (((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(2)).doubleValue() == ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(4)).doubleValue()) {
/*  195: 275 */                       cy = ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(2)).doubleValue();
/*  196:     */                     }
/*  197:     */                   }
/*  198: 278 */                   if (((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 2)).doubleValue() > ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 4)).doubleValue()) {
/*  199: 280 */                     if (((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 3)).doubleValue() == ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 1)).doubleValue()) {
/*  200: 283 */                       cy2 = ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 1)).doubleValue();
/*  201:     */                     }
/*  202:     */                   }
/*  203: 288 */                   VisualizePanel.PlotPanel.this.m_shapePoints.add(new Double(cy));
/*  204: 289 */                   VisualizePanel.PlotPanel.this.m_shapePoints.add(new Double(cy2));
/*  205: 291 */                   if (!VisualizePanel.PlotPanel.this.inPolyline(VisualizePanel.PlotPanel.this.m_shapePoints, VisualizePanel.PlotPanel.this.m_plot2D.convertToAttribX(e.getX()), VisualizePanel.PlotPanel.this.m_plot2D.convertToAttribY(e.getY())))
/*  206:     */                   {
/*  207: 294 */                     Double tmp = (Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 2);
/*  208: 295 */                     VisualizePanel.PlotPanel.this.m_shapePoints.set(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 2, VisualizePanel.PlotPanel.this.m_shapePoints.get(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 1));
/*  209:     */                     
/*  210: 297 */                     VisualizePanel.PlotPanel.this.m_shapePoints.set(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 1, tmp);
/*  211:     */                   }
/*  212: 300 */                   if (VisualizePanel.PlotPanel.this.m_shapes == null) {
/*  213: 301 */                     VisualizePanel.PlotPanel.this.m_shapes = new ArrayList(4);
/*  214:     */                   }
/*  215: 303 */                   VisualizePanel.PlotPanel.this.m_shapes.add(VisualizePanel.PlotPanel.this.m_shapePoints);
/*  216:     */                   
/*  217: 305 */                   VisualizePanel.this.m_submit.setText("Submit");
/*  218: 306 */                   VisualizePanel.this.m_submit.setActionCommand("Submit");
/*  219:     */                   
/*  220: 308 */                   VisualizePanel.this.m_submit.setEnabled(true);
/*  221:     */                 }
/*  222: 311 */                 VisualizePanel.PlotPanel.this.m_shapePoints = null;
/*  223: 312 */                 VisualizePanel.PlotPanel.this.repaint();
/*  224:     */               }
/*  225:     */               else
/*  226:     */               {
/*  227: 316 */                 VisualizePanel.PlotPanel.this.m_createShape = false;
/*  228: 317 */                 if (VisualizePanel.PlotPanel.this.m_shapePoints.size() >= 7)
/*  229:     */                 {
/*  230: 318 */                   VisualizePanel.PlotPanel.this.m_shapePoints.add(VisualizePanel.PlotPanel.this.m_shapePoints.get(1));
/*  231: 319 */                   VisualizePanel.PlotPanel.this.m_shapePoints.add(VisualizePanel.PlotPanel.this.m_shapePoints.get(2));
/*  232: 320 */                   if (VisualizePanel.PlotPanel.this.m_shapes == null) {
/*  233: 321 */                     VisualizePanel.PlotPanel.this.m_shapes = new ArrayList(4);
/*  234:     */                   }
/*  235: 323 */                   VisualizePanel.PlotPanel.this.m_shapes.add(VisualizePanel.PlotPanel.this.m_shapePoints);
/*  236:     */                   
/*  237: 325 */                   VisualizePanel.this.m_submit.setText("Submit");
/*  238: 326 */                   VisualizePanel.this.m_submit.setActionCommand("Submit");
/*  239:     */                   
/*  240: 328 */                   VisualizePanel.this.m_submit.setEnabled(true);
/*  241:     */                 }
/*  242: 330 */                 VisualizePanel.PlotPanel.this.m_shapePoints = null;
/*  243: 331 */                 VisualizePanel.PlotPanel.this.repaint();
/*  244:     */               }
/*  245: 333 */               g.dispose();
/*  246:     */             }
/*  247: 335 */             else if ((e.getModifiers() & 0x10) == 16)
/*  248:     */             {
/*  249: 337 */               VisualizePanel.PlotPanel.this.m_createShape = true;
/*  250: 338 */               VisualizePanel.PlotPanel.this.m_shapePoints = new ArrayList(17);
/*  251: 339 */               VisualizePanel.PlotPanel.this.m_shapePoints.add(new Double(VisualizePanel.PlotPanel.this.m_sIndex));
/*  252: 340 */               VisualizePanel.PlotPanel.this.m_shapePoints.add(new Double(VisualizePanel.PlotPanel.this.m_plot2D.convertToAttribX(e.getX())));
/*  253:     */               
/*  254:     */ 
/*  255: 343 */               VisualizePanel.PlotPanel.this.m_shapePoints.add(new Double(VisualizePanel.PlotPanel.this.m_plot2D.convertToAttribY(e.getY())));
/*  256: 344 */               VisualizePanel.PlotPanel.this.m_newMousePos.width = e.getX();
/*  257: 345 */               VisualizePanel.PlotPanel.this.m_newMousePos.height = e.getY();
/*  258:     */               
/*  259: 347 */               Graphics g = VisualizePanel.PlotPanel.this.m_plot2D.getGraphics();
/*  260: 348 */               g.setColor(Color.black);
/*  261: 349 */               g.setXORMode(Color.white);
/*  262: 350 */               g.drawLine((int)Math.ceil(VisualizePanel.PlotPanel.this.m_plot2D.convertToPanelX(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(1)).doubleValue())), (int)Math.ceil(VisualizePanel.PlotPanel.this.m_plot2D.convertToPanelY(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(2)).doubleValue())), VisualizePanel.PlotPanel.this.m_newMousePos.width, VisualizePanel.PlotPanel.this.m_newMousePos.height);
/*  263:     */               
/*  264:     */ 
/*  265:     */ 
/*  266: 354 */               g.dispose();
/*  267:     */             }
/*  268:     */           }
/*  269: 357 */           else if ((e.getModifiers() & 0x10) == 16) {
/*  270: 359 */             VisualizePanel.PlotPanel.this.m_plot2D.searchPoints(e.getX(), e.getY(), false);
/*  271:     */           } else {
/*  272: 361 */             VisualizePanel.PlotPanel.this.m_plot2D.searchPoints(e.getX(), e.getY(), true);
/*  273:     */           }
/*  274:     */         }
/*  275:     */         
/*  276:     */         public void mouseReleased(MouseEvent e)
/*  277:     */         {
/*  278: 370 */           if ((VisualizePanel.PlotPanel.this.m_createShape) && 
/*  279: 371 */             (((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(0)).intValue() == 1))
/*  280:     */           {
/*  281: 372 */             VisualizePanel.PlotPanel.this.m_createShape = false;
/*  282: 373 */             Graphics g = VisualizePanel.PlotPanel.this.m_plot2D.getGraphics();
/*  283: 374 */             g.setColor(Color.black);
/*  284: 375 */             g.setXORMode(Color.white);
/*  285: 376 */             g.drawRect(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(1)).intValue(), ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(2)).intValue(), ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(3)).intValue() - ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(1)).intValue(), ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(4)).intValue() - ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(2)).intValue());
/*  286:     */             
/*  287:     */ 
/*  288:     */ 
/*  289:     */ 
/*  290: 381 */             g.dispose();
/*  291: 382 */             if ((VisualizePanel.PlotPanel.this.checkPoints(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(1)).doubleValue(), ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(2)).doubleValue())) && (VisualizePanel.PlotPanel.this.checkPoints(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(3)).doubleValue(), ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(4)).doubleValue()))) {
/*  292: 388 */               if ((((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(1)).doubleValue() < ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(3)).doubleValue()) && (((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(2)).doubleValue() < ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(4)).doubleValue()))
/*  293:     */               {
/*  294: 393 */                 if (VisualizePanel.PlotPanel.this.m_shapes == null) {
/*  295: 394 */                   VisualizePanel.PlotPanel.this.m_shapes = new ArrayList(2);
/*  296:     */                 }
/*  297: 396 */                 VisualizePanel.PlotPanel.this.m_shapePoints.set(1, new Double(VisualizePanel.PlotPanel.this.m_plot2D.convertToAttribX(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(1)).doubleValue())));
/*  298:     */                 
/*  299:     */ 
/*  300:     */ 
/*  301: 400 */                 VisualizePanel.PlotPanel.this.m_shapePoints.set(2, new Double(VisualizePanel.PlotPanel.this.m_plot2D.convertToAttribY(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(2)).doubleValue())));
/*  302:     */                 
/*  303:     */ 
/*  304:     */ 
/*  305: 404 */                 VisualizePanel.PlotPanel.this.m_shapePoints.set(3, new Double(VisualizePanel.PlotPanel.this.m_plot2D.convertToAttribX(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(3)).doubleValue())));
/*  306:     */                 
/*  307:     */ 
/*  308:     */ 
/*  309: 408 */                 VisualizePanel.PlotPanel.this.m_shapePoints.set(4, new Double(VisualizePanel.PlotPanel.this.m_plot2D.convertToAttribY(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(4)).doubleValue())));
/*  310:     */                 
/*  311:     */ 
/*  312:     */ 
/*  313:     */ 
/*  314: 413 */                 VisualizePanel.PlotPanel.this.m_shapes.add(VisualizePanel.PlotPanel.this.m_shapePoints);
/*  315:     */                 
/*  316: 415 */                 VisualizePanel.this.m_submit.setText("Submit");
/*  317: 416 */                 VisualizePanel.this.m_submit.setActionCommand("Submit");
/*  318:     */                 
/*  319: 418 */                 VisualizePanel.this.m_submit.setEnabled(true);
/*  320:     */                 
/*  321: 420 */                 VisualizePanel.PlotPanel.this.repaint();
/*  322:     */               }
/*  323:     */             }
/*  324: 423 */             VisualizePanel.PlotPanel.this.m_shapePoints = null;
/*  325:     */           }
/*  326:     */         }
/*  327: 428 */       });
/*  328: 429 */       addMouseMotionListener(new MouseMotionAdapter()
/*  329:     */       {
/*  330:     */         public void mouseDragged(MouseEvent e)
/*  331:     */         {
/*  332: 433 */           if ((VisualizePanel.PlotPanel.this.m_createShape) && 
/*  333: 434 */             (((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(0)).intValue() == 1))
/*  334:     */           {
/*  335: 435 */             Graphics g = VisualizePanel.PlotPanel.this.m_plot2D.getGraphics();
/*  336: 436 */             g.setColor(Color.black);
/*  337: 437 */             g.setXORMode(Color.white);
/*  338: 438 */             g.drawRect(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(1)).intValue(), ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(2)).intValue(), ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(3)).intValue() - ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(1)).intValue(), ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(4)).intValue() - ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(2)).intValue());
/*  339:     */             
/*  340:     */ 
/*  341:     */ 
/*  342:     */ 
/*  343: 443 */             VisualizePanel.PlotPanel.this.m_shapePoints.set(3, new Double(e.getX()));
/*  344: 444 */             VisualizePanel.PlotPanel.this.m_shapePoints.set(4, new Double(e.getY()));
/*  345:     */             
/*  346: 446 */             g.drawRect(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(1)).intValue(), ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(2)).intValue(), ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(3)).intValue() - ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(1)).intValue(), ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(4)).intValue() - ((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(2)).intValue());
/*  347:     */             
/*  348:     */ 
/*  349:     */ 
/*  350: 450 */             g.dispose();
/*  351:     */           }
/*  352:     */         }
/*  353:     */         
/*  354:     */         public void mouseMoved(MouseEvent e)
/*  355:     */         {
/*  356: 457 */           if ((VisualizePanel.PlotPanel.this.m_createShape) && (
/*  357: 458 */             (((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(0)).intValue() == 2) || (((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(0)).intValue() == 3)))
/*  358:     */           {
/*  359: 460 */             Graphics g = VisualizePanel.PlotPanel.this.m_plot2D.getGraphics();
/*  360: 461 */             g.setColor(Color.black);
/*  361: 462 */             g.setXORMode(Color.white);
/*  362: 463 */             g.drawLine((int)Math.ceil(VisualizePanel.PlotPanel.this.m_plot2D.convertToPanelX(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 2)).doubleValue())), (int)Math.ceil(VisualizePanel.PlotPanel.this.m_plot2D.convertToPanelY(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 1)).doubleValue())), VisualizePanel.PlotPanel.this.m_newMousePos.width, VisualizePanel.PlotPanel.this.m_newMousePos.height);
/*  363:     */             
/*  364:     */ 
/*  365:     */ 
/*  366:     */ 
/*  367:     */ 
/*  368:     */ 
/*  369: 470 */             VisualizePanel.PlotPanel.this.m_newMousePos.width = e.getX();
/*  370: 471 */             VisualizePanel.PlotPanel.this.m_newMousePos.height = e.getY();
/*  371:     */             
/*  372: 473 */             g.drawLine((int)Math.ceil(VisualizePanel.PlotPanel.this.m_plot2D.convertToPanelX(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 2)).doubleValue())), (int)Math.ceil(VisualizePanel.PlotPanel.this.m_plot2D.convertToPanelY(((Double)VisualizePanel.PlotPanel.this.m_shapePoints.get(VisualizePanel.PlotPanel.this.m_shapePoints.size() - 1)).doubleValue())), VisualizePanel.PlotPanel.this.m_newMousePos.width, VisualizePanel.PlotPanel.this.m_newMousePos.height);
/*  373:     */             
/*  374:     */ 
/*  375:     */ 
/*  376:     */ 
/*  377:     */ 
/*  378: 479 */             g.dispose();
/*  379:     */           }
/*  380:     */         }
/*  381: 484 */       });
/*  382: 485 */       VisualizePanel.this.m_submit.addActionListener(new ActionListener()
/*  383:     */       {
/*  384:     */         public void actionPerformed(ActionEvent e)
/*  385:     */         {
/*  386: 489 */           if (e.getActionCommand().equals("Submit"))
/*  387:     */           {
/*  388: 490 */             if ((VisualizePanel.this.m_splitListener != null) && (VisualizePanel.PlotPanel.this.m_shapes != null))
/*  389:     */             {
/*  390: 492 */               Instances sub_set1 = new Instances(VisualizePanel.PlotPanel.this.m_plot2D.getMasterPlot().m_plotInstances, 500);
/*  391:     */               
/*  392: 494 */               Instances sub_set2 = new Instances(VisualizePanel.PlotPanel.this.m_plot2D.getMasterPlot().m_plotInstances, 500);
/*  393: 497 */               if (VisualizePanel.PlotPanel.this.m_plot2D.getMasterPlot().m_plotInstances != null)
/*  394:     */               {
/*  395: 499 */                 for (int noa = 0; noa < VisualizePanel.PlotPanel.this.m_plot2D.getMasterPlot().m_plotInstances.numInstances(); noa++) {
/*  396: 501 */                   if ((!VisualizePanel.PlotPanel.this.m_plot2D.getMasterPlot().m_plotInstances.instance(noa).isMissing(VisualizePanel.PlotPanel.this.m_xIndex)) && (!VisualizePanel.PlotPanel.this.m_plot2D.getMasterPlot().m_plotInstances.instance(noa).isMissing(VisualizePanel.PlotPanel.this.m_yIndex))) {
/*  397: 506 */                     if (VisualizePanel.PlotPanel.this.inSplit(VisualizePanel.PlotPanel.this.m_plot2D.getMasterPlot().m_plotInstances.instance(noa))) {
/*  398: 508 */                       sub_set1.add(VisualizePanel.PlotPanel.this.m_plot2D.getMasterPlot().m_plotInstances.instance(noa));
/*  399:     */                     } else {
/*  400: 511 */                       sub_set2.add(VisualizePanel.PlotPanel.this.m_plot2D.getMasterPlot().m_plotInstances.instance(noa));
/*  401:     */                     }
/*  402:     */                   }
/*  403:     */                 }
/*  404: 516 */                 ArrayList<ArrayList<Double>> tmp = VisualizePanel.PlotPanel.this.m_shapes;
/*  405: 517 */                 VisualizePanel.PlotPanel.this.cancelShapes();
/*  406: 518 */                 VisualizePanel.this.m_splitListener.userDataEvent(new VisualizePanelEvent(tmp, sub_set1, sub_set2, VisualizePanel.PlotPanel.this.m_xIndex, VisualizePanel.PlotPanel.this.m_yIndex));
/*  407:     */               }
/*  408:     */             }
/*  409: 521 */             else if ((VisualizePanel.PlotPanel.this.m_shapes != null) && (VisualizePanel.PlotPanel.this.m_plot2D.getMasterPlot().m_plotInstances != null))
/*  410:     */             {
/*  411: 523 */               Instances sub_set1 = new Instances(VisualizePanel.PlotPanel.this.m_plot2D.getMasterPlot().m_plotInstances, 500);
/*  412:     */               
/*  413: 525 */               int count = 0;
/*  414: 526 */               for (int noa = 0; noa < VisualizePanel.PlotPanel.this.m_plot2D.getMasterPlot().m_plotInstances.numInstances(); noa++) {
/*  415: 528 */                 if (VisualizePanel.PlotPanel.this.inSplit(VisualizePanel.PlotPanel.this.m_plot2D.getMasterPlot().m_plotInstances.instance(noa)))
/*  416:     */                 {
/*  417: 530 */                   sub_set1.add(VisualizePanel.PlotPanel.this.m_plot2D.getMasterPlot().m_plotInstances.instance(noa));
/*  418:     */                   
/*  419: 532 */                   count++;
/*  420:     */                 }
/*  421:     */               }
/*  422: 537 */               int[] nSizes = null;
/*  423: 538 */               int[] nTypes = null;
/*  424: 539 */               int x = VisualizePanel.PlotPanel.this.m_xIndex;
/*  425: 540 */               int y = VisualizePanel.PlotPanel.this.m_yIndex;
/*  426: 542 */               if (VisualizePanel.PlotPanel.this.m_originalPlot == null) {
/*  427: 545 */                 VisualizePanel.PlotPanel.this.m_originalPlot = VisualizePanel.PlotPanel.this.m_plot2D.getMasterPlot();
/*  428:     */               }
/*  429: 548 */               if (count > 0)
/*  430:     */               {
/*  431: 549 */                 nTypes = new int[count];
/*  432: 550 */                 nSizes = new int[count];
/*  433: 551 */                 count = 0;
/*  434: 552 */                 for (int noa = 0; noa < VisualizePanel.PlotPanel.this.m_plot2D.getMasterPlot().m_plotInstances.numInstances(); noa++) {
/*  435: 554 */                   if (VisualizePanel.PlotPanel.this.inSplit(VisualizePanel.PlotPanel.this.m_plot2D.getMasterPlot().m_plotInstances.instance(noa)))
/*  436:     */                   {
/*  437: 557 */                     nTypes[count] = VisualizePanel.PlotPanel.this.m_plot2D.getMasterPlot().m_shapeType[noa];
/*  438: 558 */                     nSizes[count] = VisualizePanel.PlotPanel.this.m_plot2D.getMasterPlot().m_shapeSize[noa];
/*  439: 559 */                     count++;
/*  440:     */                   }
/*  441:     */                 }
/*  442:     */               }
/*  443: 563 */               VisualizePanel.PlotPanel.this.cancelShapes();
/*  444:     */               
/*  445: 565 */               PlotData2D newPlot = new PlotData2D(sub_set1);
/*  446:     */               try
/*  447:     */               {
/*  448: 568 */                 newPlot.setShapeSize(nSizes);
/*  449: 569 */                 newPlot.setShapeType(nTypes);
/*  450:     */                 
/*  451: 571 */                 VisualizePanel.PlotPanel.this.m_plot2D.removeAllPlots();
/*  452:     */                 
/*  453: 573 */                 VisualizePanel.this.addPlot(newPlot);
/*  454:     */               }
/*  455:     */               catch (Exception ex)
/*  456:     */               {
/*  457: 575 */                 System.err.println(ex);
/*  458: 576 */                 ex.printStackTrace();
/*  459:     */               }
/*  460:     */               try
/*  461:     */               {
/*  462: 580 */                 VisualizePanel.this.setXIndex(x);
/*  463: 581 */                 VisualizePanel.this.setYIndex(y);
/*  464:     */               }
/*  465:     */               catch (Exception er)
/*  466:     */               {
/*  467: 583 */                 System.out.println("Error : " + er);
/*  468:     */               }
/*  469:     */             }
/*  470:     */           }
/*  471: 588 */           else if (e.getActionCommand().equals("Reset"))
/*  472:     */           {
/*  473: 589 */             int x = VisualizePanel.PlotPanel.this.m_xIndex;
/*  474: 590 */             int y = VisualizePanel.PlotPanel.this.m_yIndex;
/*  475:     */             
/*  476: 592 */             VisualizePanel.PlotPanel.this.m_plot2D.removeAllPlots();
/*  477:     */             try
/*  478:     */             {
/*  479: 594 */               VisualizePanel.this.addPlot(VisualizePanel.PlotPanel.this.m_originalPlot);
/*  480:     */             }
/*  481:     */             catch (Exception ex)
/*  482:     */             {
/*  483: 596 */               System.err.println(ex);
/*  484: 597 */               ex.printStackTrace();
/*  485:     */             }
/*  486:     */             try
/*  487:     */             {
/*  488: 601 */               VisualizePanel.this.setXIndex(x);
/*  489: 602 */               VisualizePanel.this.setYIndex(y);
/*  490:     */             }
/*  491:     */             catch (Exception er)
/*  492:     */             {
/*  493: 604 */               System.out.println("Error : " + er);
/*  494:     */             }
/*  495:     */           }
/*  496:     */         }
/*  497: 609 */       });
/*  498: 610 */       VisualizePanel.this.m_cancel.addActionListener(new ActionListener()
/*  499:     */       {
/*  500:     */         public void actionPerformed(ActionEvent e)
/*  501:     */         {
/*  502: 613 */           VisualizePanel.PlotPanel.this.cancelShapes();
/*  503: 614 */           VisualizePanel.PlotPanel.this.repaint();
/*  504:     */         }
/*  505:     */       });
/*  506:     */     }
/*  507:     */     
/*  508:     */     protected void applySettings(Settings settings, String ownerID)
/*  509:     */     {
/*  510: 628 */       this.m_plot2D.applySettings(settings, ownerID);
/*  511: 629 */       setBackground(this.m_plot2D.getBackground());
/*  512: 630 */       repaint();
/*  513:     */     }
/*  514:     */     
/*  515:     */     public void removeAllPlots()
/*  516:     */     {
/*  517: 637 */       this.m_plot2D.removeAllPlots();
/*  518: 638 */       VisualizePanel.this.m_legendPanel.setPlotList(this.m_plot2D.getPlots());
/*  519:     */     }
/*  520:     */     
/*  521:     */     public ArrayList<ArrayList<Double>> getShapes()
/*  522:     */     {
/*  523: 646 */       return this.m_shapes;
/*  524:     */     }
/*  525:     */     
/*  526:     */     public void cancelShapes()
/*  527:     */     {
/*  528: 655 */       if (VisualizePanel.this.m_splitListener == null)
/*  529:     */       {
/*  530: 656 */         VisualizePanel.this.m_submit.setText("Reset");
/*  531: 657 */         VisualizePanel.this.m_submit.setActionCommand("Reset");
/*  532: 659 */         if ((this.m_originalPlot == null) || (this.m_originalPlot.m_plotInstances == this.m_plotInstances)) {
/*  533: 661 */           VisualizePanel.this.m_submit.setEnabled(false);
/*  534:     */         } else {
/*  535: 663 */           VisualizePanel.this.m_submit.setEnabled(true);
/*  536:     */         }
/*  537:     */       }
/*  538:     */       else
/*  539:     */       {
/*  540: 666 */         VisualizePanel.this.m_submit.setEnabled(false);
/*  541:     */       }
/*  542: 669 */       this.m_createShape = false;
/*  543: 670 */       this.m_shapePoints = null;
/*  544: 671 */       this.m_shapes = null;
/*  545: 672 */       repaint();
/*  546:     */     }
/*  547:     */     
/*  548:     */     public void setShapes(ArrayList<ArrayList<Double>> v)
/*  549:     */     {
/*  550: 684 */       if (v != null)
/*  551:     */       {
/*  552: 686 */         this.m_shapes = new ArrayList(v.size());
/*  553: 687 */         for (int noa = 0; noa < v.size(); noa++)
/*  554:     */         {
/*  555: 688 */           ArrayList<Double> temp = new ArrayList(((ArrayList)v.get(noa)).size());
/*  556: 689 */           this.m_shapes.add(temp);
/*  557: 690 */           for (int nob = 0; nob < ((ArrayList)v.get(noa)).size(); nob++) {
/*  558: 692 */             temp.add(((ArrayList)v.get(noa)).get(nob));
/*  559:     */           }
/*  560:     */         }
/*  561:     */       }
/*  562:     */       else
/*  563:     */       {
/*  564: 697 */         this.m_shapes = null;
/*  565:     */       }
/*  566: 699 */       repaint();
/*  567:     */     }
/*  568:     */     
/*  569:     */     private boolean checkPoints(double x1, double y1)
/*  570:     */     {
/*  571: 711 */       if ((x1 < 0.0D) || (x1 > getSize().width) || (y1 < 0.0D) || (y1 > getSize().height)) {
/*  572: 713 */         return false;
/*  573:     */       }
/*  574: 715 */       return true;
/*  575:     */     }
/*  576:     */     
/*  577:     */     public boolean inSplit(Instance i)
/*  578:     */     {
/*  579: 728 */       if (this.m_shapes != null) {
/*  580: 731 */         for (int noa = 0; noa < this.m_shapes.size(); noa++)
/*  581:     */         {
/*  582: 732 */           ArrayList<Double> stmp = (ArrayList)this.m_shapes.get(noa);
/*  583: 733 */           if (((Double)stmp.get(0)).intValue() == 1)
/*  584:     */           {
/*  585: 735 */             double x1 = ((Double)stmp.get(1)).doubleValue();
/*  586: 736 */             double y1 = ((Double)stmp.get(2)).doubleValue();
/*  587: 737 */             double x2 = ((Double)stmp.get(3)).doubleValue();
/*  588: 738 */             double y2 = ((Double)stmp.get(4)).doubleValue();
/*  589: 739 */             if ((i.value(this.m_xIndex) >= x1) && (i.value(this.m_xIndex) <= x2) && (i.value(this.m_yIndex) <= y1) && (i.value(this.m_yIndex) >= y2)) {
/*  590: 742 */               return true;
/*  591:     */             }
/*  592:     */           }
/*  593: 744 */           else if (((Double)stmp.get(0)).intValue() == 2)
/*  594:     */           {
/*  595: 746 */             if (inPoly(stmp, i.value(this.m_xIndex), i.value(this.m_yIndex))) {
/*  596: 747 */               return true;
/*  597:     */             }
/*  598:     */           }
/*  599: 749 */           else if (((Double)stmp.get(0)).intValue() == 3)
/*  600:     */           {
/*  601: 751 */             if (inPolyline(stmp, i.value(this.m_xIndex), i.value(this.m_yIndex))) {
/*  602: 752 */               return true;
/*  603:     */             }
/*  604:     */           }
/*  605:     */         }
/*  606:     */       }
/*  607: 757 */       return false;
/*  608:     */     }
/*  609:     */     
/*  610:     */     private boolean inPolyline(ArrayList<Double> ob, double x, double y)
/*  611:     */     {
/*  612: 778 */       int countx = 0;
/*  613: 783 */       for (int noa = 1; noa < ob.size() - 4; noa += 2)
/*  614:     */       {
/*  615: 784 */         double y1 = ((Double)ob.get(noa + 1)).doubleValue();
/*  616: 785 */         double y2 = ((Double)ob.get(noa + 3)).doubleValue();
/*  617: 786 */         double x1 = ((Double)ob.get(noa)).doubleValue();
/*  618: 787 */         double x2 = ((Double)ob.get(noa + 2)).doubleValue();
/*  619:     */         
/*  620:     */ 
/*  621: 790 */         double vecy = y2 - y1;
/*  622: 791 */         double vecx = x2 - x1;
/*  623: 792 */         if ((noa == 1) && (noa == ob.size() - 6))
/*  624:     */         {
/*  625: 794 */           if (vecy != 0.0D)
/*  626:     */           {
/*  627: 795 */             double change = (y - y1) / vecy;
/*  628: 796 */             if (vecx * change + x1 >= x) {
/*  629: 798 */               countx++;
/*  630:     */             }
/*  631:     */           }
/*  632:     */         }
/*  633: 801 */         else if (noa == 1)
/*  634:     */         {
/*  635: 802 */           if (((y < y2) && (vecy > 0.0D)) || ((y > y2) && (vecy < 0.0D)))
/*  636:     */           {
/*  637: 804 */             double change = (y - y1) / vecy;
/*  638: 805 */             if (vecx * change + x1 >= x) {
/*  639: 807 */               countx++;
/*  640:     */             }
/*  641:     */           }
/*  642:     */         }
/*  643: 810 */         else if (noa == ob.size() - 6)
/*  644:     */         {
/*  645: 812 */           if (((y <= y1) && (vecy < 0.0D)) || ((y >= y1) && (vecy > 0.0D)))
/*  646:     */           {
/*  647: 813 */             double change = (y - y1) / vecy;
/*  648: 814 */             if (vecx * change + x1 >= x) {
/*  649: 815 */               countx++;
/*  650:     */             }
/*  651:     */           }
/*  652:     */         }
/*  653: 818 */         else if (((y1 <= y) && (y < y2)) || ((y2 < y) && (y <= y1))) {
/*  654: 820 */           if (vecy != 0.0D)
/*  655:     */           {
/*  656: 824 */             double change = (y - y1) / vecy;
/*  657: 825 */             if (vecx * change + x1 >= x) {
/*  658: 827 */               countx++;
/*  659:     */             }
/*  660:     */           }
/*  661:     */         }
/*  662:     */       }
/*  663: 834 */       double y1 = ((Double)ob.get(ob.size() - 2)).doubleValue();
/*  664: 835 */       double y2 = ((Double)ob.get(ob.size() - 1)).doubleValue();
/*  665: 837 */       if (y1 > y2)
/*  666:     */       {
/*  667: 839 */         if ((y1 >= y) && (y > y2)) {
/*  668: 840 */           countx++;
/*  669:     */         }
/*  670:     */       }
/*  671: 844 */       else if ((y1 >= y) || (y > y2)) {
/*  672: 845 */         countx++;
/*  673:     */       }
/*  674: 849 */       if (countx % 2 == 1) {
/*  675: 850 */         return true;
/*  676:     */       }
/*  677: 852 */       return false;
/*  678:     */     }
/*  679:     */     
/*  680:     */     private boolean inPoly(ArrayList<Double> ob, double x, double y)
/*  681:     */     {
/*  682: 871 */       int count = 0;
/*  683: 875 */       for (int noa = 1; noa < ob.size() - 2; noa += 2)
/*  684:     */       {
/*  685: 876 */         double y1 = ((Double)ob.get(noa + 1)).doubleValue();
/*  686: 877 */         double y2 = ((Double)ob.get(noa + 3)).doubleValue();
/*  687: 878 */         if (((y1 <= y) && (y < y2)) || ((y2 < y) && (y <= y1)))
/*  688:     */         {
/*  689: 880 */           double vecy = y2 - y1;
/*  690: 881 */           if (vecy != 0.0D)
/*  691:     */           {
/*  692: 884 */             double x1 = ((Double)ob.get(noa)).doubleValue();
/*  693: 885 */             double x2 = ((Double)ob.get(noa + 2)).doubleValue();
/*  694: 886 */             double vecx = x2 - x1;
/*  695: 887 */             double change = (y - y1) / vecy;
/*  696: 888 */             if (vecx * change + x1 >= x) {
/*  697: 890 */               count++;
/*  698:     */             }
/*  699:     */           }
/*  700:     */         }
/*  701:     */       }
/*  702: 895 */       if (count % 2 == 1) {
/*  703: 898 */         return true;
/*  704:     */       }
/*  705: 901 */       return false;
/*  706:     */     }
/*  707:     */     
/*  708:     */     public void setJitter(int j)
/*  709:     */     {
/*  710: 913 */       this.m_plot2D.setJitter(j);
/*  711:     */     }
/*  712:     */     
/*  713:     */     public void setXindex(int x)
/*  714:     */     {
/*  715: 925 */       if (x != this.m_xIndex) {
/*  716: 926 */         cancelShapes();
/*  717:     */       }
/*  718: 928 */       this.m_xIndex = x;
/*  719: 929 */       this.m_plot2D.setXindex(x);
/*  720: 930 */       if (VisualizePanel.this.m_showAttBars) {
/*  721: 931 */         VisualizePanel.this.m_attrib.setX(x);
/*  722:     */       }
/*  723:     */     }
/*  724:     */     
/*  725:     */     public void setYindex(int y)
/*  726:     */     {
/*  727: 945 */       if (y != this.m_yIndex) {
/*  728: 946 */         cancelShapes();
/*  729:     */       }
/*  730: 948 */       this.m_yIndex = y;
/*  731: 949 */       this.m_plot2D.setYindex(y);
/*  732: 950 */       if (VisualizePanel.this.m_showAttBars) {
/*  733: 951 */         VisualizePanel.this.m_attrib.setY(y);
/*  734:     */       }
/*  735:     */     }
/*  736:     */     
/*  737:     */     public void setCindex(int c)
/*  738:     */     {
/*  739: 962 */       this.m_cIndex = c;
/*  740: 963 */       this.m_plot2D.setCindex(c);
/*  741: 964 */       if (VisualizePanel.this.m_showAttBars) {
/*  742: 965 */         VisualizePanel.this.m_attrib.setCindex(c, this.m_plot2D.getMaxC(), this.m_plot2D.getMinC());
/*  743:     */       }
/*  744: 967 */       VisualizePanel.this.m_classPanel.setCindex(c);
/*  745: 968 */       repaint();
/*  746:     */     }
/*  747:     */     
/*  748:     */     public void setSindex(int s)
/*  749:     */     {
/*  750: 977 */       if (s != this.m_sIndex)
/*  751:     */       {
/*  752: 978 */         this.m_shapePoints = null;
/*  753: 979 */         this.m_createShape = false;
/*  754:     */       }
/*  755: 981 */       this.m_sIndex = s;
/*  756: 982 */       repaint();
/*  757:     */     }
/*  758:     */     
/*  759:     */     public void setMasterPlot(PlotData2D newPlot)
/*  760:     */       throws Exception
/*  761:     */     {
/*  762: 992 */       this.m_plot2D.removeAllPlots();
/*  763: 993 */       addPlot(newPlot);
/*  764:     */     }
/*  765:     */     
/*  766:     */     public void addPlot(PlotData2D newPlot)
/*  767:     */       throws Exception
/*  768:     */     {
/*  769:1005 */       if (this.m_plot2D.getPlots().size() == 0)
/*  770:     */       {
/*  771:1006 */         this.m_plot2D.addPlot(newPlot);
/*  772:1007 */         if ((VisualizePanel.this.m_plotSurround.getComponentCount() > 1) && (VisualizePanel.this.m_plotSurround.getComponent(1) == VisualizePanel.this.m_attrib) && (VisualizePanel.this.m_showAttBars)) {
/*  773:     */           try
/*  774:     */           {
/*  775:1010 */             VisualizePanel.this.m_attrib.setInstances(newPlot.m_plotInstances);
/*  776:1011 */             VisualizePanel.this.m_attrib.setCindex(0);
/*  777:1012 */             VisualizePanel.this.m_attrib.setX(0);
/*  778:1013 */             VisualizePanel.this.m_attrib.setY(0);
/*  779:     */           }
/*  780:     */           catch (Exception ex)
/*  781:     */           {
/*  782:1017 */             VisualizePanel.this.m_plotSurround.remove(VisualizePanel.this.m_attrib);
/*  783:1018 */             System.err.println("Warning : data contains more attributes than can be displayed as attribute bars.");
/*  784:1020 */             if (VisualizePanel.this.m_Log != null) {
/*  785:1021 */               VisualizePanel.this.m_Log.logMessage("Warning : data contains more attributes than can be displayed as attribute bars.");
/*  786:     */             }
/*  787:     */           }
/*  788:1025 */         } else if (VisualizePanel.this.m_showAttBars) {
/*  789:     */           try
/*  790:     */           {
/*  791:1027 */             VisualizePanel.this.m_attrib.setInstances(newPlot.m_plotInstances);
/*  792:1028 */             VisualizePanel.this.m_attrib.setCindex(0);
/*  793:1029 */             VisualizePanel.this.m_attrib.setX(0);
/*  794:1030 */             VisualizePanel.this.m_attrib.setY(0);
/*  795:1031 */             GridBagConstraints constraints = new GridBagConstraints();
/*  796:1032 */             constraints.fill = 1;
/*  797:1033 */             constraints.insets = new Insets(0, 0, 0, 0);
/*  798:1034 */             constraints.gridx = 4;
/*  799:1035 */             constraints.gridy = 0;
/*  800:1036 */             constraints.weightx = 1.0D;
/*  801:1037 */             constraints.gridwidth = 1;
/*  802:1038 */             constraints.gridheight = 1;
/*  803:1039 */             constraints.weighty = 5.0D;
/*  804:1040 */             VisualizePanel.this.m_plotSurround.add(VisualizePanel.this.m_attrib, constraints);
/*  805:     */           }
/*  806:     */           catch (Exception ex)
/*  807:     */           {
/*  808:1042 */             System.err.println("Warning : data contains more attributes than can be displayed as attribute bars.");
/*  809:1044 */             if (VisualizePanel.this.m_Log != null) {
/*  810:1045 */               VisualizePanel.this.m_Log.logMessage("Warning : data contains more attributes than can be displayed as attribute bars.");
/*  811:     */             }
/*  812:     */           }
/*  813:     */         }
/*  814:1050 */         VisualizePanel.this.m_classPanel.setInstances(newPlot.m_plotInstances);
/*  815:     */         
/*  816:1052 */         plotReset(newPlot.m_plotInstances, newPlot.getCindex());
/*  817:1053 */         if ((newPlot.m_useCustomColour) && (VisualizePanel.this.m_showClassPanel))
/*  818:     */         {
/*  819:1054 */           VisualizePanel.this.remove(VisualizePanel.this.m_classSurround);
/*  820:1055 */           switchToLegend();
/*  821:1056 */           VisualizePanel.this.m_legendPanel.setPlotList(this.m_plot2D.getPlots());
/*  822:1057 */           VisualizePanel.this.m_ColourCombo.setEnabled(false);
/*  823:     */         }
/*  824:     */       }
/*  825:     */       else
/*  826:     */       {
/*  827:1060 */         if ((!newPlot.m_useCustomColour) && (VisualizePanel.this.m_showClassPanel))
/*  828:     */         {
/*  829:1061 */           VisualizePanel.this.add(VisualizePanel.this.m_classSurround, "South");
/*  830:1062 */           VisualizePanel.this.m_ColourCombo.setEnabled(true);
/*  831:     */         }
/*  832:1064 */         if (this.m_plot2D.getPlots().size() == 1) {
/*  833:1065 */           switchToLegend();
/*  834:     */         }
/*  835:1067 */         this.m_plot2D.addPlot(newPlot);
/*  836:1068 */         VisualizePanel.this.m_legendPanel.setPlotList(this.m_plot2D.getPlots());
/*  837:     */       }
/*  838:     */     }
/*  839:     */     
/*  840:     */     protected void switchToLegend()
/*  841:     */     {
/*  842:1077 */       if ((VisualizePanel.this.m_plotSurround.getComponentCount() > 1) && (VisualizePanel.this.m_plotSurround.getComponent(1) == VisualizePanel.this.m_attrib)) {
/*  843:1079 */         VisualizePanel.this.m_plotSurround.remove(VisualizePanel.this.m_attrib);
/*  844:     */       }
/*  845:1082 */       if ((VisualizePanel.this.m_plotSurround.getComponentCount() > 1) && (VisualizePanel.this.m_plotSurround.getComponent(1) == VisualizePanel.this.m_legendPanel)) {
/*  846:1084 */         return;
/*  847:     */       }
/*  848:1087 */       GridBagConstraints constraints = new GridBagConstraints();
/*  849:1088 */       constraints.fill = 1;
/*  850:1089 */       constraints.insets = new Insets(0, 0, 0, 0);
/*  851:1090 */       constraints.gridx = 4;
/*  852:1091 */       constraints.gridy = 0;
/*  853:1092 */       constraints.weightx = 1.0D;
/*  854:1093 */       constraints.gridwidth = 1;
/*  855:1094 */       constraints.gridheight = 1;
/*  856:1095 */       constraints.weighty = 5.0D;
/*  857:1096 */       VisualizePanel.this.m_plotSurround.add(VisualizePanel.this.m_legendPanel, constraints);
/*  858:1097 */       setSindex(0);
/*  859:1098 */       VisualizePanel.this.m_ShapeCombo.setEnabled(false);
/*  860:     */     }
/*  861:     */     
/*  862:     */     protected void switchToBars()
/*  863:     */     {
/*  864:1102 */       if ((VisualizePanel.this.m_plotSurround.getComponentCount() > 1) && (VisualizePanel.this.m_plotSurround.getComponent(1) == VisualizePanel.this.m_legendPanel)) {
/*  865:1104 */         VisualizePanel.this.m_plotSurround.remove(VisualizePanel.this.m_legendPanel);
/*  866:     */       }
/*  867:1107 */       if ((VisualizePanel.this.m_plotSurround.getComponentCount() > 1) && (VisualizePanel.this.m_plotSurround.getComponent(1) == VisualizePanel.this.m_attrib)) {
/*  868:1109 */         return;
/*  869:     */       }
/*  870:1112 */       if (VisualizePanel.this.m_showAttBars) {
/*  871:     */         try
/*  872:     */         {
/*  873:1114 */           VisualizePanel.this.m_attrib.setInstances(this.m_plot2D.getMasterPlot().m_plotInstances);
/*  874:1115 */           VisualizePanel.this.m_attrib.setCindex(0);
/*  875:1116 */           VisualizePanel.this.m_attrib.setX(0);
/*  876:1117 */           VisualizePanel.this.m_attrib.setY(0);
/*  877:1118 */           GridBagConstraints constraints = new GridBagConstraints();
/*  878:1119 */           constraints.fill = 1;
/*  879:1120 */           constraints.insets = new Insets(0, 0, 0, 0);
/*  880:1121 */           constraints.gridx = 4;
/*  881:1122 */           constraints.gridy = 0;
/*  882:1123 */           constraints.weightx = 1.0D;
/*  883:1124 */           constraints.gridwidth = 1;
/*  884:1125 */           constraints.gridheight = 1;
/*  885:1126 */           constraints.weighty = 5.0D;
/*  886:1127 */           VisualizePanel.this.m_plotSurround.add(VisualizePanel.this.m_attrib, constraints);
/*  887:     */         }
/*  888:     */         catch (Exception ex)
/*  889:     */         {
/*  890:1129 */           System.err.println("Warning : data contains more attributes than can be displayed as attribute bars.");
/*  891:1131 */           if (VisualizePanel.this.m_Log != null) {
/*  892:1132 */             VisualizePanel.this.m_Log.logMessage("Warning : data contains more attributes than can be displayed as attribute bars.");
/*  893:     */           }
/*  894:     */         }
/*  895:     */       }
/*  896:     */     }
/*  897:     */     
/*  898:     */     private void plotReset(Instances inst, int cIndex)
/*  899:     */     {
/*  900:1146 */       if (VisualizePanel.this.m_splitListener == null)
/*  901:     */       {
/*  902:1147 */         VisualizePanel.this.m_submit.setText("Reset");
/*  903:1148 */         VisualizePanel.this.m_submit.setActionCommand("Reset");
/*  904:1150 */         if ((this.m_originalPlot == null) || (this.m_originalPlot.m_plotInstances == inst)) {
/*  905:1151 */           VisualizePanel.this.m_submit.setEnabled(false);
/*  906:     */         } else {
/*  907:1153 */           VisualizePanel.this.m_submit.setEnabled(true);
/*  908:     */         }
/*  909:     */       }
/*  910:     */       else
/*  911:     */       {
/*  912:1156 */         VisualizePanel.this.m_submit.setEnabled(false);
/*  913:     */       }
/*  914:1159 */       this.m_plotInstances = inst;
/*  915:1160 */       if (VisualizePanel.this.m_splitListener != null) {
/*  916:1161 */         this.m_plotInstances.randomize(new Random());
/*  917:     */       }
/*  918:1163 */       this.m_xIndex = 0;
/*  919:1164 */       this.m_yIndex = 0;
/*  920:1165 */       this.m_cIndex = cIndex;
/*  921:1166 */       cancelShapes();
/*  922:     */     }
/*  923:     */     
/*  924:     */     public void setColours(ArrayList<Color> cols)
/*  925:     */     {
/*  926:1175 */       this.m_plot2D.setColours(cols);
/*  927:1176 */       VisualizePanel.this.m_colorList = cols;
/*  928:     */     }
/*  929:     */     
/*  930:     */     private void drawShapes(Graphics gx)
/*  931:     */     {
/*  932:1188 */       if (this.m_shapes != null) {
/*  933:1191 */         for (int noa = 0; noa < this.m_shapes.size(); noa++)
/*  934:     */         {
/*  935:1192 */           ArrayList<Double> stmp = (ArrayList)this.m_shapes.get(noa);
/*  936:1193 */           if (((Double)stmp.get(0)).intValue() == 1)
/*  937:     */           {
/*  938:1195 */             int x1 = (int)this.m_plot2D.convertToPanelX(((Double)stmp.get(1)).doubleValue());
/*  939:1196 */             int y1 = (int)this.m_plot2D.convertToPanelY(((Double)stmp.get(2)).doubleValue());
/*  940:1197 */             int x2 = (int)this.m_plot2D.convertToPanelX(((Double)stmp.get(3)).doubleValue());
/*  941:1198 */             int y2 = (int)this.m_plot2D.convertToPanelY(((Double)stmp.get(4)).doubleValue());
/*  942:     */             
/*  943:1200 */             gx.setColor(Color.gray);
/*  944:1201 */             gx.fillRect(x1, y1, x2 - x1, y2 - y1);
/*  945:1202 */             gx.setColor(Color.black);
/*  946:1203 */             gx.drawRect(x1, y1, x2 - x1, y2 - y1);
/*  947:     */           }
/*  948:1205 */           else if (((Double)stmp.get(0)).intValue() == 2)
/*  949:     */           {
/*  950:1208 */             int[] ar1 = getXCoords(stmp);
/*  951:1209 */             int[] ar2 = getYCoords(stmp);
/*  952:1210 */             gx.setColor(Color.gray);
/*  953:1211 */             gx.fillPolygon(ar1, ar2, (stmp.size() - 1) / 2);
/*  954:1212 */             gx.setColor(Color.black);
/*  955:1213 */             gx.drawPolyline(ar1, ar2, (stmp.size() - 1) / 2);
/*  956:     */           }
/*  957:1214 */           else if (((Double)stmp.get(0)).intValue() == 3)
/*  958:     */           {
/*  959:1217 */             ArrayList<Double> tmp = makePolygon(stmp);
/*  960:1218 */             int[] ar1 = getXCoords(tmp);
/*  961:1219 */             int[] ar2 = getYCoords(tmp);
/*  962:     */             
/*  963:1221 */             gx.setColor(Color.gray);
/*  964:1222 */             gx.fillPolygon(ar1, ar2, (tmp.size() - 1) / 2);
/*  965:1223 */             gx.setColor(Color.black);
/*  966:1224 */             gx.drawPolyline(ar1, ar2, (tmp.size() - 1) / 2);
/*  967:     */           }
/*  968:     */         }
/*  969:     */       }
/*  970:1229 */       if (this.m_shapePoints != null) {
/*  971:1231 */         if ((((Double)this.m_shapePoints.get(0)).intValue() == 2) || (((Double)this.m_shapePoints.get(0)).intValue() == 3))
/*  972:     */         {
/*  973:1233 */           gx.setColor(Color.black);
/*  974:1234 */           gx.setXORMode(Color.white);
/*  975:     */           
/*  976:1236 */           int[] ar1 = getXCoords(this.m_shapePoints);
/*  977:1237 */           int[] ar2 = getYCoords(this.m_shapePoints);
/*  978:1238 */           gx.drawPolyline(ar1, ar2, (this.m_shapePoints.size() - 1) / 2);
/*  979:1239 */           this.m_newMousePos.width = ((int)Math.ceil(this.m_plot2D.convertToPanelX(((Double)this.m_shapePoints.get(this.m_shapePoints.size() - 2)).doubleValue())));
/*  980:     */           
/*  981:     */ 
/*  982:     */ 
/*  983:1243 */           this.m_newMousePos.height = ((int)Math.ceil(this.m_plot2D.convertToPanelY(((Double)this.m_shapePoints.get(this.m_shapePoints.size() - 1)).doubleValue())));
/*  984:     */           
/*  985:     */ 
/*  986:     */ 
/*  987:1247 */           gx.drawLine((int)Math.ceil(this.m_plot2D.convertToPanelX(((Double)this.m_shapePoints.get(this.m_shapePoints.size() - 2)).doubleValue())), (int)Math.ceil(this.m_plot2D.convertToPanelY(((Double)this.m_shapePoints.get(this.m_shapePoints.size() - 1)).doubleValue())), this.m_newMousePos.width, this.m_newMousePos.height);
/*  988:     */           
/*  989:     */ 
/*  990:     */ 
/*  991:     */ 
/*  992:     */ 
/*  993:1253 */           gx.setPaintMode();
/*  994:     */         }
/*  995:     */       }
/*  996:     */     }
/*  997:     */     
/*  998:     */     private double[] lineIntersect(double x1, double y1, double x2, double y2, double x, double y, double offset)
/*  999:     */     {
/* 1000:1281 */       double xn = -100.0D;double yn = -100.0D;
/* 1001:1283 */       if (x == 0.0D)
/* 1002:     */       {
/* 1003:1284 */         if (((x1 <= offset) && (offset < x2)) || ((x1 >= offset) && (offset > x2)))
/* 1004:     */         {
/* 1005:1286 */           double xval = x1 - x2;
/* 1006:1287 */           double change = (offset - x2) / xval;
/* 1007:1288 */           yn = (y1 - y2) * change + y2;
/* 1008:1289 */           if ((0.0D <= yn) && (yn <= y)) {
/* 1009:1291 */             xn = offset;
/* 1010:     */           } else {
/* 1011:1294 */             xn = -100.0D;
/* 1012:     */           }
/* 1013:     */         }
/* 1014:     */       }
/* 1015:1297 */       else if ((y == 0.0D) && (
/* 1016:1298 */         ((y1 <= offset) && (offset < y2)) || ((y1 >= offset) && (offset > y2))))
/* 1017:     */       {
/* 1018:1300 */         double yval = y1 - y2;
/* 1019:1301 */         double change = (offset - y2) / yval;
/* 1020:1302 */         xn = (x1 - x2) * change + x2;
/* 1021:1303 */         if ((0.0D <= xn) && (xn <= x)) {
/* 1022:1305 */           yn = offset;
/* 1023:     */         } else {
/* 1024:1307 */           xn = -100.0D;
/* 1025:     */         }
/* 1026:     */       }
/* 1027:1311 */       double[] ret = new double[2];
/* 1028:1312 */       ret[0] = xn;
/* 1029:1313 */       ret[1] = yn;
/* 1030:1314 */       return ret;
/* 1031:     */     }
/* 1032:     */     
/* 1033:     */     private ArrayList<Double> makePolygon(ArrayList<Double> v)
/* 1034:     */     {
/* 1035:1325 */       ArrayList<Double> building = new ArrayList(v.size() + 10);
/* 1036:     */       
/* 1037:1327 */       int edge1 = 0;int edge2 = 0;
/* 1038:1328 */       for (int noa = 0; noa < v.size() - 2; noa++) {
/* 1039:1329 */         building.add(new Double(((Double)v.get(noa)).doubleValue()));
/* 1040:     */       }
/* 1041:1337 */       double x1 = this.m_plot2D.convertToPanelX(((Double)v.get(1)).doubleValue());
/* 1042:1338 */       double y1 = this.m_plot2D.convertToPanelY(((Double)v.get(2)).doubleValue());
/* 1043:1339 */       double x2 = this.m_plot2D.convertToPanelX(((Double)v.get(3)).doubleValue());
/* 1044:1340 */       double y2 = this.m_plot2D.convertToPanelY(((Double)v.get(4)).doubleValue());
/* 1045:     */       double[] new_coords;
/* 1046:1342 */       if (x1 < 0.0D)
/* 1047:     */       {
/* 1048:1344 */         double[] new_coords = lineIntersect(x1, y1, x2, y2, 0.0D, getHeight(), 0.0D);
/* 1049:1345 */         edge1 = 0;
/* 1050:1346 */         if (new_coords[0] < 0.0D) {
/* 1051:1348 */           if (y1 < 0.0D)
/* 1052:     */           {
/* 1053:1350 */             new_coords = lineIntersect(x1, y1, x2, y2, getWidth(), 0.0D, 0.0D);
/* 1054:1351 */             edge1 = 1;
/* 1055:     */           }
/* 1056:     */           else
/* 1057:     */           {
/* 1058:1354 */             new_coords = lineIntersect(x1, y1, x2, y2, getWidth(), 0.0D, getHeight());
/* 1059:     */             
/* 1060:     */ 
/* 1061:1357 */             edge1 = 3;
/* 1062:     */           }
/* 1063:     */         }
/* 1064:     */       }
/* 1065:1360 */       else if (x1 > getWidth())
/* 1066:     */       {
/* 1067:1362 */         double[] new_coords = lineIntersect(x1, y1, x2, y2, 0.0D, getHeight(), getWidth());
/* 1068:     */         
/* 1069:1364 */         edge1 = 2;
/* 1070:1365 */         if (new_coords[0] < 0.0D) {
/* 1071:1367 */           if (y1 < 0.0D)
/* 1072:     */           {
/* 1073:1369 */             new_coords = lineIntersect(x1, y1, x2, y2, getWidth(), 0.0D, 0.0D);
/* 1074:1370 */             edge1 = 1;
/* 1075:     */           }
/* 1076:     */           else
/* 1077:     */           {
/* 1078:1373 */             new_coords = lineIntersect(x1, y1, x2, y2, getWidth(), 0.0D, getHeight());
/* 1079:     */             
/* 1080:     */ 
/* 1081:1376 */             edge1 = 3;
/* 1082:     */           }
/* 1083:     */         }
/* 1084:     */       }
/* 1085:1379 */       else if (y1 < 0.0D)
/* 1086:     */       {
/* 1087:1381 */         double[] new_coords = lineIntersect(x1, y1, x2, y2, getWidth(), 0.0D, 0.0D);
/* 1088:1382 */         edge1 = 1;
/* 1089:     */       }
/* 1090:     */       else
/* 1091:     */       {
/* 1092:1385 */         new_coords = lineIntersect(x1, y1, x2, y2, getWidth(), 0.0D, getHeight());
/* 1093:     */         
/* 1094:1387 */         edge1 = 3;
/* 1095:     */       }
/* 1096:1390 */       building.set(1, new Double(this.m_plot2D.convertToAttribX(new_coords[0])));
/* 1097:1391 */       building.set(2, new Double(this.m_plot2D.convertToAttribY(new_coords[1])));
/* 1098:     */       
/* 1099:1393 */       x1 = this.m_plot2D.convertToPanelX(((Double)v.get(v.size() - 4)).doubleValue());
/* 1100:1394 */       y1 = this.m_plot2D.convertToPanelY(((Double)v.get(v.size() - 3)).doubleValue());
/* 1101:1395 */       x2 = this.m_plot2D.convertToPanelX(((Double)v.get(v.size() - 6)).doubleValue());
/* 1102:1396 */       y2 = this.m_plot2D.convertToPanelY(((Double)v.get(v.size() - 5)).doubleValue());
/* 1103:1398 */       if (x1 < 0.0D)
/* 1104:     */       {
/* 1105:1400 */         new_coords = lineIntersect(x1, y1, x2, y2, 0.0D, getHeight(), 0.0D);
/* 1106:1401 */         edge2 = 0;
/* 1107:1402 */         if (new_coords[0] < 0.0D) {
/* 1108:1404 */           if (y1 < 0.0D)
/* 1109:     */           {
/* 1110:1406 */             new_coords = lineIntersect(x1, y1, x2, y2, getWidth(), 0.0D, 0.0D);
/* 1111:1407 */             edge2 = 1;
/* 1112:     */           }
/* 1113:     */           else
/* 1114:     */           {
/* 1115:1410 */             new_coords = lineIntersect(x1, y1, x2, y2, getWidth(), 0.0D, getHeight());
/* 1116:     */             
/* 1117:     */ 
/* 1118:1413 */             edge2 = 3;
/* 1119:     */           }
/* 1120:     */         }
/* 1121:     */       }
/* 1122:1416 */       else if (x1 > getWidth())
/* 1123:     */       {
/* 1124:1418 */         new_coords = lineIntersect(x1, y1, x2, y2, 0.0D, getHeight(), getWidth());
/* 1125:     */         
/* 1126:1420 */         edge2 = 2;
/* 1127:1421 */         if (new_coords[0] < 0.0D) {
/* 1128:1423 */           if (y1 < 0.0D)
/* 1129:     */           {
/* 1130:1425 */             new_coords = lineIntersect(x1, y1, x2, y2, getWidth(), 0.0D, 0.0D);
/* 1131:1426 */             edge2 = 1;
/* 1132:     */           }
/* 1133:     */           else
/* 1134:     */           {
/* 1135:1429 */             new_coords = lineIntersect(x1, y1, x2, y2, getWidth(), 0.0D, getHeight());
/* 1136:     */             
/* 1137:     */ 
/* 1138:1432 */             edge2 = 3;
/* 1139:     */           }
/* 1140:     */         }
/* 1141:     */       }
/* 1142:1435 */       else if (y1 < 0.0D)
/* 1143:     */       {
/* 1144:1437 */         new_coords = lineIntersect(x1, y1, x2, y2, getWidth(), 0.0D, 0.0D);
/* 1145:1438 */         edge2 = 1;
/* 1146:     */       }
/* 1147:     */       else
/* 1148:     */       {
/* 1149:1441 */         new_coords = lineIntersect(x1, y1, x2, y2, getWidth(), 0.0D, getHeight());
/* 1150:     */         
/* 1151:1443 */         edge2 = 3;
/* 1152:     */       }
/* 1153:1446 */       building.set(building.size() - 2, new Double(this.m_plot2D.convertToAttribX(new_coords[0])));
/* 1154:     */       
/* 1155:1448 */       building.set(building.size() - 1, new Double(this.m_plot2D.convertToAttribY(new_coords[1])));
/* 1156:     */       
/* 1157:     */ 
/* 1158:     */ 
/* 1159:     */ 
/* 1160:     */ 
/* 1161:     */ 
/* 1162:1455 */       int xp = getWidth() * (edge2 & 0x1 ^ (edge2 & 0x2) / 2);
/* 1163:1456 */       int yp = getHeight() * ((edge2 & 0x2) / 2);
/* 1164:1459 */       if (inPolyline(v, this.m_plot2D.convertToAttribX(xp), this.m_plot2D.convertToAttribY(yp)))
/* 1165:     */       {
/* 1166:1462 */         building.add(new Double(this.m_plot2D.convertToAttribX(xp)));
/* 1167:1463 */         building.add(new Double(this.m_plot2D.convertToAttribY(yp)));
/* 1168:1464 */         for (int noa = (edge2 + 1) % 4; noa != edge1; noa = (noa + 1) % 4)
/* 1169:     */         {
/* 1170:1465 */           xp = getWidth() * (noa & 0x1 ^ (noa & 0x2) / 2);
/* 1171:1466 */           yp = getHeight() * ((noa & 0x2) / 2);
/* 1172:1467 */           building.add(new Double(this.m_plot2D.convertToAttribX(xp)));
/* 1173:1468 */           building.add(new Double(this.m_plot2D.convertToAttribY(yp)));
/* 1174:     */         }
/* 1175:     */       }
/* 1176:     */       else
/* 1177:     */       {
/* 1178:1471 */         xp = getWidth() * ((edge2 & 0x2) / 2);
/* 1179:1472 */         yp = getHeight() * (0x1 & (edge2 & 0x1 ^ (edge2 & 0x2) / 2 ^ 0xFFFFFFFF));
/* 1180:1473 */         if (inPolyline(v, this.m_plot2D.convertToAttribX(xp), this.m_plot2D.convertToAttribY(yp)))
/* 1181:     */         {
/* 1182:1476 */           building.add(new Double(this.m_plot2D.convertToAttribX(xp)));
/* 1183:1477 */           building.add(new Double(this.m_plot2D.convertToAttribY(yp)));
/* 1184:1478 */           for (int noa = (edge2 + 3) % 4; noa != edge1; noa = (noa + 3) % 4)
/* 1185:     */           {
/* 1186:1479 */             xp = getWidth() * ((noa & 0x2) / 2);
/* 1187:1480 */             yp = getHeight() * (0x1 & (noa & 0x1 ^ (noa & 0x2) / 2 ^ 0xFFFFFFFF));
/* 1188:1481 */             building.add(new Double(this.m_plot2D.convertToAttribX(xp)));
/* 1189:1482 */             building.add(new Double(this.m_plot2D.convertToAttribY(yp)));
/* 1190:     */           }
/* 1191:     */         }
/* 1192:     */       }
/* 1193:1486 */       return building;
/* 1194:     */     }
/* 1195:     */     
/* 1196:     */     private int[] getXCoords(ArrayList<Double> v)
/* 1197:     */     {
/* 1198:1497 */       int cach = (v.size() - 1) / 2;
/* 1199:1498 */       int[] ar = new int[cach];
/* 1200:1499 */       for (int noa = 0; noa < cach; noa++) {
/* 1201:1500 */         ar[noa] = ((int)this.m_plot2D.convertToPanelX(((Double)v.get(noa * 2 + 1)).doubleValue()));
/* 1202:     */       }
/* 1203:1503 */       return ar;
/* 1204:     */     }
/* 1205:     */     
/* 1206:     */     private int[] getYCoords(ArrayList<Double> v)
/* 1207:     */     {
/* 1208:1514 */       int cach = (v.size() - 1) / 2;
/* 1209:1515 */       int[] ar = new int[cach];
/* 1210:1516 */       for (int noa = 0; noa < cach; noa++) {
/* 1211:1517 */         ar[noa] = ((int)this.m_plot2D.convertToPanelY(((Double)v.get(noa * 2 + 2)).doubleValue()));
/* 1212:     */       }
/* 1213:1520 */       return ar;
/* 1214:     */     }
/* 1215:     */     
/* 1216:     */     public void prePlot(Graphics gx)
/* 1217:     */     {
/* 1218:1530 */       super.paintComponent(gx);
/* 1219:1531 */       if (this.m_plotInstances != null) {
/* 1220:1532 */         drawShapes(gx);
/* 1221:     */       }
/* 1222:     */     }
/* 1223:     */   }
/* 1224:     */   
/* 1225:1538 */   protected Color[] m_DefaultColors = { Color.blue, Color.red, Color.green, Color.cyan, Color.pink, new Color(255, 0, 255), Color.orange, new Color(255, 0, 0), new Color(0, 255, 0), Color.white };
/* 1226:1543 */   protected JComboBox m_XCombo = new JComboBox();
/* 1227:1546 */   protected JComboBox m_YCombo = new JComboBox();
/* 1228:1549 */   protected JComboBox m_ColourCombo = new JComboBox();
/* 1229:1554 */   protected JComboBox m_ShapeCombo = new JComboBox();
/* 1230:1557 */   protected JButton m_submit = new JButton("Submit");
/* 1231:1560 */   protected JButton m_cancel = new JButton("Clear");
/* 1232:1563 */   protected JButton m_openBut = new JButton("Open");
/* 1233:1566 */   protected JButton m_saveBut = new JButton("Save");
/* 1234:1569 */   private final Dimension COMBO_SIZE = new Dimension(250, this.m_saveBut.getPreferredSize().height);
/* 1235:1573 */   protected JFileChooser m_FileChooser = new JFileChooser(new File(System.getProperty("user.dir")));
/* 1236:1577 */   protected FileFilter m_ArffFilter = new ExtensionFileFilter(".arff", "Arff data files");
/* 1237:1581 */   protected JLabel m_JitterLab = new JLabel("Jitter", 4);
/* 1238:1584 */   protected JSlider m_Jitter = new JSlider(0, 50, 0);
/* 1239:1587 */   protected PlotPanel m_plot = new PlotPanel();
/* 1240:1593 */   protected AttributePanel m_attrib = new AttributePanel(this.m_plot.m_plot2D.getBackground());
/* 1241:1597 */   protected LegendPanel m_legendPanel = new LegendPanel();
/* 1242:1600 */   protected JPanel m_plotSurround = new JPanel();
/* 1243:1603 */   protected JPanel m_classSurround = new JPanel();
/* 1244:1608 */   protected ActionListener listener = null;
/* 1245:1614 */   protected VisualizePanelListener m_splitListener = null;
/* 1246:1620 */   protected String m_plotName = "";
/* 1247:1623 */   protected ClassPanel m_classPanel = new ClassPanel(this.m_plot.m_plot2D.getBackground());
/* 1248:     */   protected ArrayList<Color> m_colorList;
/* 1249:1633 */   protected String m_preferredXDimension = null;
/* 1250:1634 */   protected String m_preferredYDimension = null;
/* 1251:1635 */   protected String m_preferredColourDimension = null;
/* 1252:1638 */   protected boolean m_showAttBars = true;
/* 1253:1641 */   protected boolean m_showClassPanel = true;
/* 1254:     */   protected weka.gui.Logger m_Log;
/* 1255:     */   
/* 1256:     */   public void setLog(weka.gui.Logger newLog)
/* 1257:     */   {
/* 1258:1652 */     this.m_Log = newLog;
/* 1259:     */   }
/* 1260:     */   
/* 1261:     */   public void setShowAttBars(boolean sab)
/* 1262:     */   {
/* 1263:1662 */     if ((!sab) && (this.m_showAttBars))
/* 1264:     */     {
/* 1265:1663 */       this.m_plotSurround.remove(this.m_attrib);
/* 1266:     */     }
/* 1267:1664 */     else if ((sab) && (!this.m_showAttBars))
/* 1268:     */     {
/* 1269:1665 */       GridBagConstraints constraints = new GridBagConstraints();
/* 1270:1666 */       constraints.insets = new Insets(0, 0, 0, 0);
/* 1271:1667 */       constraints.gridx = 4;
/* 1272:1668 */       constraints.gridy = 0;
/* 1273:1669 */       constraints.weightx = 1.0D;
/* 1274:1670 */       constraints.gridwidth = 1;
/* 1275:1671 */       constraints.gridheight = 1;
/* 1276:1672 */       constraints.weighty = 5.0D;
/* 1277:1673 */       this.m_plotSurround.add(this.m_attrib, constraints);
/* 1278:     */     }
/* 1279:1675 */     this.m_showAttBars = sab;
/* 1280:1676 */     repaint();
/* 1281:     */   }
/* 1282:     */   
/* 1283:     */   public boolean getShowAttBars()
/* 1284:     */   {
/* 1285:1685 */     return this.m_showAttBars;
/* 1286:     */   }
/* 1287:     */   
/* 1288:     */   public void setShowClassPanel(boolean scp)
/* 1289:     */   {
/* 1290:1694 */     if ((!scp) && (this.m_showClassPanel)) {
/* 1291:1695 */       remove(this.m_classSurround);
/* 1292:1696 */     } else if ((scp) && (!this.m_showClassPanel)) {
/* 1293:1697 */       add(this.m_classSurround, "South");
/* 1294:     */     }
/* 1295:1699 */     this.m_showClassPanel = scp;
/* 1296:1700 */     repaint();
/* 1297:     */   }
/* 1298:     */   
/* 1299:     */   public boolean getShowClassPanel()
/* 1300:     */   {
/* 1301:1709 */     return this.m_showClassPanel;
/* 1302:     */   }
/* 1303:     */   
/* 1304:     */   public VisualizePanel(VisualizePanelListener ls)
/* 1305:     */   {
/* 1306:1718 */     this();
/* 1307:1719 */     this.m_splitListener = ls;
/* 1308:     */   }
/* 1309:     */   
/* 1310:     */   private void setProperties(String relationName)
/* 1311:     */   {
/* 1312:1728 */     if (VisualizeUtils.VISUALIZE_PROPERTIES != null)
/* 1313:     */     {
/* 1314:1729 */       String thisClass = getClass().getName();
/* 1315:1730 */       if (relationName == null)
/* 1316:     */       {
/* 1317:1732 */         String showAttBars = thisClass + ".displayAttributeBars";
/* 1318:     */         
/* 1319:1734 */         String val = VisualizeUtils.VISUALIZE_PROPERTIES.getProperty(showAttBars);
/* 1320:1736 */         if (val != null) {
/* 1321:1741 */           if (this.m_showAttBars) {
/* 1322:1742 */             if ((val.compareTo("true") == 0) || (val.compareTo("on") == 0)) {
/* 1323:1744 */               this.m_showAttBars = true;
/* 1324:     */             } else {
/* 1325:1746 */               this.m_showAttBars = false;
/* 1326:     */             }
/* 1327:     */           }
/* 1328:     */         }
/* 1329:     */       }
/* 1330:     */       else
/* 1331:     */       {
/* 1332:1755 */         String xcolKey = thisClass + "." + relationName + ".XDimension";
/* 1333:1756 */         String ycolKey = thisClass + "." + relationName + ".YDimension";
/* 1334:1757 */         String ccolKey = thisClass + "." + relationName + ".ColourDimension";
/* 1335:     */         
/* 1336:1759 */         this.m_preferredXDimension = VisualizeUtils.VISUALIZE_PROPERTIES.getProperty(xcolKey);
/* 1337:     */         
/* 1338:     */ 
/* 1339:     */ 
/* 1340:     */ 
/* 1341:     */ 
/* 1342:     */ 
/* 1343:     */ 
/* 1344:     */ 
/* 1345:1768 */         this.m_preferredYDimension = VisualizeUtils.VISUALIZE_PROPERTIES.getProperty(ycolKey);
/* 1346:     */         
/* 1347:     */ 
/* 1348:     */ 
/* 1349:     */ 
/* 1350:     */ 
/* 1351:     */ 
/* 1352:     */ 
/* 1353:     */ 
/* 1354:1777 */         this.m_preferredColourDimension = VisualizeUtils.VISUALIZE_PROPERTIES.getProperty(ccolKey);
/* 1355:     */       }
/* 1356:     */     }
/* 1357:     */   }
/* 1358:     */   
/* 1359:     */   public void applySettings(Settings settings, String ownerID)
/* 1360:     */   {
/* 1361:1798 */     this.m_plot.applySettings(settings, ownerID);
/* 1362:1799 */     this.m_attrib.applySettings(settings, ownerID);
/* 1363:1800 */     repaint();
/* 1364:     */   }
/* 1365:     */   
/* 1366:     */   public VisualizePanel()
/* 1367:     */   {
/* 1368:1808 */     setProperties(null);
/* 1369:1809 */     this.m_FileChooser.setFileFilter(this.m_ArffFilter);
/* 1370:1810 */     this.m_FileChooser.setFileSelectionMode(0);
/* 1371:     */     
/* 1372:1812 */     this.m_XCombo.setToolTipText("Select the attribute for the x axis");
/* 1373:1813 */     this.m_YCombo.setToolTipText("Select the attribute for the y axis");
/* 1374:1814 */     this.m_ColourCombo.setToolTipText("Select the attribute to colour on");
/* 1375:1815 */     this.m_ShapeCombo.setToolTipText("Select the shape to use for data selection");
/* 1376:     */     
/* 1377:1817 */     this.m_XCombo.setPreferredSize(this.COMBO_SIZE);
/* 1378:1818 */     this.m_YCombo.setPreferredSize(this.COMBO_SIZE);
/* 1379:1819 */     this.m_ColourCombo.setPreferredSize(this.COMBO_SIZE);
/* 1380:1820 */     this.m_ShapeCombo.setPreferredSize(this.COMBO_SIZE);
/* 1381:     */     
/* 1382:1822 */     this.m_XCombo.setMaximumSize(this.COMBO_SIZE);
/* 1383:1823 */     this.m_YCombo.setMaximumSize(this.COMBO_SIZE);
/* 1384:1824 */     this.m_ColourCombo.setMaximumSize(this.COMBO_SIZE);
/* 1385:1825 */     this.m_ShapeCombo.setMaximumSize(this.COMBO_SIZE);
/* 1386:     */     
/* 1387:1827 */     this.m_XCombo.setMinimumSize(this.COMBO_SIZE);
/* 1388:1828 */     this.m_YCombo.setMinimumSize(this.COMBO_SIZE);
/* 1389:1829 */     this.m_ColourCombo.setMinimumSize(this.COMBO_SIZE);
/* 1390:1830 */     this.m_ShapeCombo.setMinimumSize(this.COMBO_SIZE);
/* 1391:     */     
/* 1392:1832 */     this.m_XCombo.setEnabled(false);
/* 1393:1833 */     this.m_YCombo.setEnabled(false);
/* 1394:1834 */     this.m_ColourCombo.setEnabled(false);
/* 1395:1835 */     this.m_ShapeCombo.setEnabled(false);
/* 1396:     */     
/* 1397:     */ 
/* 1398:     */ 
/* 1399:1839 */     this.m_classPanel.addRepaintNotify(this);
/* 1400:1840 */     this.m_legendPanel.addRepaintNotify(this);
/* 1401:1845 */     for (int i = 0; i < this.m_DefaultColors.length; i++)
/* 1402:     */     {
/* 1403:1846 */       Color c = this.m_DefaultColors[i];
/* 1404:1847 */       if (c.equals(this.m_plot.m_plot2D.getBackground()))
/* 1405:     */       {
/* 1406:1848 */         int red = c.getRed();
/* 1407:1849 */         int blue = c.getBlue();
/* 1408:1850 */         int green = c.getGreen();
/* 1409:1851 */         red += (red < 128 ? (255 - red) / 2 : -(red / 2));
/* 1410:1852 */         blue += (blue < 128 ? (blue - red) / 2 : -(blue / 2));
/* 1411:1853 */         green += (green < 128 ? (255 - green) / 2 : -(green / 2));
/* 1412:1854 */         this.m_DefaultColors[i] = new Color(red, green, blue);
/* 1413:     */       }
/* 1414:     */     }
/* 1415:1857 */     this.m_classPanel.setDefaultColourList(this.m_DefaultColors);
/* 1416:1858 */     this.m_attrib.setDefaultColourList(this.m_DefaultColors);
/* 1417:     */     
/* 1418:1860 */     this.m_colorList = new ArrayList(10);
/* 1419:1861 */     for (int noa = this.m_colorList.size(); noa < 10; noa++)
/* 1420:     */     {
/* 1421:1862 */       Color pc = this.m_DefaultColors[(noa % 10)];
/* 1422:1863 */       int ija = noa / 10;
/* 1423:1864 */       ija *= 2;
/* 1424:1865 */       for (int j = 0; j < ija; j++) {
/* 1425:1866 */         pc = pc.darker();
/* 1426:     */       }
/* 1427:1869 */       this.m_colorList.add(pc);
/* 1428:     */     }
/* 1429:1871 */     this.m_plot.setColours(this.m_colorList);
/* 1430:1872 */     this.m_classPanel.setColours(this.m_colorList);
/* 1431:1873 */     this.m_attrib.setColours(this.m_colorList);
/* 1432:1874 */     this.m_attrib.addAttributePanelListener(new AttributePanelListener()
/* 1433:     */     {
/* 1434:     */       public void attributeSelectionChange(AttributePanelEvent e)
/* 1435:     */       {
/* 1436:1877 */         if (e.m_xChange) {
/* 1437:1878 */           VisualizePanel.this.m_XCombo.setSelectedIndex(e.m_indexVal);
/* 1438:     */         } else {
/* 1439:1880 */           VisualizePanel.this.m_YCombo.setSelectedIndex(e.m_indexVal);
/* 1440:     */         }
/* 1441:     */       }
/* 1442:1884 */     });
/* 1443:1885 */     this.m_XCombo.addActionListener(new ActionListener()
/* 1444:     */     {
/* 1445:     */       public void actionPerformed(ActionEvent e)
/* 1446:     */       {
/* 1447:1888 */         int selected = VisualizePanel.this.m_XCombo.getSelectedIndex();
/* 1448:1889 */         if (selected < 0) {
/* 1449:1890 */           selected = 0;
/* 1450:     */         }
/* 1451:1892 */         VisualizePanel.this.m_plot.setXindex(selected);
/* 1452:1895 */         if (VisualizePanel.this.listener != null) {
/* 1453:1896 */           VisualizePanel.this.listener.actionPerformed(e);
/* 1454:     */         }
/* 1455:     */       }
/* 1456:1900 */     });
/* 1457:1901 */     this.m_YCombo.addActionListener(new ActionListener()
/* 1458:     */     {
/* 1459:     */       public void actionPerformed(ActionEvent e)
/* 1460:     */       {
/* 1461:1904 */         int selected = VisualizePanel.this.m_YCombo.getSelectedIndex();
/* 1462:1905 */         if (selected < 0) {
/* 1463:1906 */           selected = 0;
/* 1464:     */         }
/* 1465:1908 */         VisualizePanel.this.m_plot.setYindex(selected);
/* 1466:1911 */         if (VisualizePanel.this.listener != null) {
/* 1467:1912 */           VisualizePanel.this.listener.actionPerformed(e);
/* 1468:     */         }
/* 1469:     */       }
/* 1470:1916 */     });
/* 1471:1917 */     this.m_ColourCombo.addActionListener(new ActionListener()
/* 1472:     */     {
/* 1473:     */       public void actionPerformed(ActionEvent e)
/* 1474:     */       {
/* 1475:1920 */         int selected = VisualizePanel.this.m_ColourCombo.getSelectedIndex();
/* 1476:1921 */         if (selected < 0) {
/* 1477:1922 */           selected = 0;
/* 1478:     */         }
/* 1479:1924 */         VisualizePanel.this.m_plot.setCindex(selected);
/* 1480:1926 */         if (VisualizePanel.this.listener != null) {
/* 1481:1927 */           VisualizePanel.this.listener.actionPerformed(e);
/* 1482:     */         }
/* 1483:     */       }
/* 1484:1932 */     });
/* 1485:1933 */     this.m_ShapeCombo.addActionListener(new ActionListener()
/* 1486:     */     {
/* 1487:     */       public void actionPerformed(ActionEvent e)
/* 1488:     */       {
/* 1489:1936 */         int selected = VisualizePanel.this.m_ShapeCombo.getSelectedIndex();
/* 1490:1937 */         if (selected < 0) {
/* 1491:1938 */           selected = 0;
/* 1492:     */         }
/* 1493:1940 */         VisualizePanel.this.m_plot.setSindex(selected);
/* 1494:1942 */         if (VisualizePanel.this.listener != null) {
/* 1495:1943 */           VisualizePanel.this.listener.actionPerformed(e);
/* 1496:     */         }
/* 1497:     */       }
/* 1498:1949 */     });
/* 1499:1950 */     this.m_Jitter.addChangeListener(new ChangeListener()
/* 1500:     */     {
/* 1501:     */       public void stateChanged(ChangeEvent e)
/* 1502:     */       {
/* 1503:1953 */         VisualizePanel.this.m_plot.setJitter(VisualizePanel.this.m_Jitter.getValue());
/* 1504:     */       }
/* 1505:1956 */     });
/* 1506:1957 */     this.m_openBut.setToolTipText("Loads previously saved instances from a file");
/* 1507:1958 */     this.m_openBut.addActionListener(new ActionListener()
/* 1508:     */     {
/* 1509:     */       public void actionPerformed(ActionEvent e)
/* 1510:     */       {
/* 1511:1961 */         VisualizePanel.this.openVisibleInstances();
/* 1512:     */       }
/* 1513:1964 */     });
/* 1514:1965 */     this.m_saveBut.setEnabled(false);
/* 1515:1966 */     this.m_saveBut.setToolTipText("Save the visible instances to a file");
/* 1516:1967 */     this.m_saveBut.addActionListener(new ActionListener()
/* 1517:     */     {
/* 1518:     */       public void actionPerformed(ActionEvent e)
/* 1519:     */       {
/* 1520:1970 */         VisualizePanel.this.saveVisibleInstances();
/* 1521:     */       }
/* 1522:1973 */     });
/* 1523:1974 */     JPanel combos = new JPanel();
/* 1524:1975 */     GridBagLayout gb = new GridBagLayout();
/* 1525:1976 */     GridBagConstraints constraints = new GridBagConstraints();
/* 1526:     */     
/* 1527:1978 */     this.m_XCombo.setLightWeightPopupEnabled(false);
/* 1528:1979 */     this.m_YCombo.setLightWeightPopupEnabled(false);
/* 1529:1980 */     this.m_ColourCombo.setLightWeightPopupEnabled(false);
/* 1530:1981 */     this.m_ShapeCombo.setLightWeightPopupEnabled(false);
/* 1531:1982 */     combos.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
/* 1532:     */     
/* 1533:1984 */     combos.setLayout(gb);
/* 1534:1985 */     constraints.gridx = 0;
/* 1535:1986 */     constraints.gridy = 0;
/* 1536:1987 */     constraints.weightx = 5.0D;
/* 1537:1988 */     constraints.fill = 2;
/* 1538:1989 */     constraints.gridwidth = 2;
/* 1539:1990 */     constraints.gridheight = 1;
/* 1540:1991 */     constraints.insets = new Insets(0, 2, 0, 2);
/* 1541:1992 */     combos.add(this.m_XCombo, constraints);
/* 1542:1993 */     constraints.gridx = 2;
/* 1543:1994 */     constraints.gridy = 0;
/* 1544:1995 */     constraints.weightx = 5.0D;
/* 1545:1996 */     constraints.gridwidth = 2;
/* 1546:1997 */     constraints.gridheight = 1;
/* 1547:1998 */     combos.add(this.m_YCombo, constraints);
/* 1548:1999 */     constraints.gridx = 0;
/* 1549:2000 */     constraints.gridy = 1;
/* 1550:2001 */     constraints.weightx = 5.0D;
/* 1551:2002 */     constraints.gridwidth = 2;
/* 1552:2003 */     constraints.gridheight = 1;
/* 1553:2004 */     combos.add(this.m_ColourCombo, constraints);
/* 1554:     */     
/* 1555:2006 */     constraints.gridx = 2;
/* 1556:2007 */     constraints.gridy = 1;
/* 1557:2008 */     constraints.weightx = 5.0D;
/* 1558:2009 */     constraints.gridwidth = 2;
/* 1559:2010 */     constraints.gridheight = 1;
/* 1560:2011 */     combos.add(this.m_ShapeCombo, constraints);
/* 1561:     */     
/* 1562:2013 */     JPanel mbts = new JPanel();
/* 1563:2014 */     mbts.setLayout(new GridLayout(1, 4));
/* 1564:2015 */     mbts.add(this.m_submit);
/* 1565:2016 */     mbts.add(this.m_cancel);
/* 1566:2017 */     mbts.add(this.m_openBut);
/* 1567:2018 */     mbts.add(this.m_saveBut);
/* 1568:     */     
/* 1569:2020 */     constraints.gridx = 0;
/* 1570:2021 */     constraints.gridy = 2;
/* 1571:2022 */     constraints.weightx = 5.0D;
/* 1572:2023 */     constraints.gridwidth = 2;
/* 1573:2024 */     constraints.gridheight = 1;
/* 1574:2025 */     combos.add(mbts, constraints);
/* 1575:     */     
/* 1576:     */ 
/* 1577:2028 */     constraints.gridx = 2;
/* 1578:2029 */     constraints.gridy = 2;
/* 1579:2030 */     constraints.weightx = 5.0D;
/* 1580:2031 */     constraints.gridwidth = 1;
/* 1581:2032 */     constraints.gridheight = 1;
/* 1582:2033 */     constraints.insets = new Insets(10, 0, 0, 5);
/* 1583:2034 */     combos.add(this.m_JitterLab, constraints);
/* 1584:2035 */     constraints.gridx = 3;
/* 1585:2036 */     constraints.gridy = 2;
/* 1586:2037 */     constraints.weightx = 5.0D;
/* 1587:2038 */     constraints.insets = new Insets(10, 0, 0, 0);
/* 1588:2039 */     combos.add(this.m_Jitter, constraints);
/* 1589:     */     
/* 1590:2041 */     this.m_classSurround = new JPanel();
/* 1591:2042 */     this.m_classSurround.setBorder(BorderFactory.createTitledBorder("Class colour"));
/* 1592:2043 */     this.m_classSurround.setLayout(new BorderLayout());
/* 1593:     */     
/* 1594:2045 */     this.m_classPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 10, 10));
/* 1595:2046 */     this.m_classSurround.add(this.m_classPanel, "Center");
/* 1596:     */     
/* 1597:2048 */     GridBagLayout gb2 = new GridBagLayout();
/* 1598:2049 */     this.m_plotSurround.setBorder(BorderFactory.createTitledBorder("Plot"));
/* 1599:2050 */     this.m_plotSurround.setLayout(gb2);
/* 1600:     */     
/* 1601:2052 */     constraints.fill = 1;
/* 1602:2053 */     constraints.insets = new Insets(0, 0, 0, 10);
/* 1603:2054 */     constraints.gridx = 0;
/* 1604:2055 */     constraints.gridy = 0;
/* 1605:2056 */     constraints.weightx = 3.0D;
/* 1606:2057 */     constraints.gridwidth = 4;
/* 1607:2058 */     constraints.gridheight = 1;
/* 1608:2059 */     constraints.weighty = 5.0D;
/* 1609:2060 */     this.m_plotSurround.add(this.m_plot, constraints);
/* 1610:2062 */     if (this.m_showAttBars)
/* 1611:     */     {
/* 1612:2063 */       constraints.insets = new Insets(0, 0, 0, 0);
/* 1613:2064 */       constraints.gridx = 4;
/* 1614:2065 */       constraints.gridy = 0;
/* 1615:2066 */       constraints.weightx = 1.0D;
/* 1616:2067 */       constraints.gridwidth = 1;
/* 1617:2068 */       constraints.gridheight = 1;
/* 1618:2069 */       constraints.weighty = 5.0D;
/* 1619:2070 */       this.m_plotSurround.add(this.m_attrib, constraints);
/* 1620:     */     }
/* 1621:2073 */     setLayout(new BorderLayout());
/* 1622:2074 */     add(combos, "North");
/* 1623:2075 */     add(this.m_plotSurround, "Center");
/* 1624:2076 */     add(this.m_classSurround, "South");
/* 1625:     */     
/* 1626:2078 */     String[] SNames = new String[4];
/* 1627:2079 */     SNames[0] = "Select Instance";
/* 1628:2080 */     SNames[1] = "Rectangle";
/* 1629:2081 */     SNames[2] = "Polygon";
/* 1630:2082 */     SNames[3] = "Polyline";
/* 1631:     */     
/* 1632:2084 */     this.m_ShapeCombo.setModel(new DefaultComboBoxModel(SNames));
/* 1633:2085 */     this.m_ShapeCombo.setEnabled(true);
/* 1634:     */   }
/* 1635:     */   
/* 1636:     */   protected void openVisibleInstances(Instances insts)
/* 1637:     */     throws Exception
/* 1638:     */   {
/* 1639:2095 */     PlotData2D tempd = new PlotData2D(insts);
/* 1640:2096 */     tempd.setPlotName(insts.relationName());
/* 1641:2097 */     tempd.addInstanceNumberAttribute();
/* 1642:2098 */     this.m_plot.m_plot2D.removeAllPlots();
/* 1643:2099 */     addPlot(tempd);
/* 1644:     */     
/* 1645:     */ 
/* 1646:2102 */     Component parent = getParent();
/* 1647:2103 */     while (parent != null)
/* 1648:     */     {
/* 1649:2104 */       if ((parent instanceof JFrame))
/* 1650:     */       {
/* 1651:2105 */         ((JFrame)parent).setTitle("Weka Classifier Visualize: " + insts.relationName() + " (display only)");
/* 1652:     */         
/* 1653:2107 */         break;
/* 1654:     */       }
/* 1655:2109 */       parent = parent.getParent();
/* 1656:     */     }
/* 1657:     */   }
/* 1658:     */   
/* 1659:     */   protected void openVisibleInstances()
/* 1660:     */   {
/* 1661:     */     try
/* 1662:     */     {
/* 1663:2119 */       int returnVal = this.m_FileChooser.showOpenDialog(this);
/* 1664:2120 */       if (returnVal == 0)
/* 1665:     */       {
/* 1666:2121 */         File sFile = this.m_FileChooser.getSelectedFile();
/* 1667:2122 */         if (!sFile.getName().toLowerCase().endsWith(".arff")) {
/* 1668:2123 */           sFile = new File(sFile.getParent(), sFile.getName() + ".arff");
/* 1669:     */         }
/* 1670:2127 */         File selected = sFile;
/* 1671:2128 */         Instances insts = new Instances(new BufferedReader(new FileReader(selected)));
/* 1672:     */         
/* 1673:2130 */         openVisibleInstances(insts);
/* 1674:     */       }
/* 1675:     */     }
/* 1676:     */     catch (Exception ex)
/* 1677:     */     {
/* 1678:2133 */       ex.printStackTrace();
/* 1679:2134 */       this.m_plot.m_plot2D.removeAllPlots();
/* 1680:2135 */       JOptionPane.showMessageDialog(this, ex.getMessage(), "Error loading file...", 0);
/* 1681:     */     }
/* 1682:     */   }
/* 1683:     */   
/* 1684:     */   private void saveVisibleInstances()
/* 1685:     */   {
/* 1686:2144 */     ArrayList<PlotData2D> plots = this.m_plot.m_plot2D.getPlots();
/* 1687:2145 */     if (plots != null)
/* 1688:     */     {
/* 1689:2146 */       PlotData2D master = (PlotData2D)plots.get(0);
/* 1690:2147 */       Instances saveInsts = new Instances(master.getPlotInstances());
/* 1691:2148 */       for (int i = 1; i < plots.size(); i++)
/* 1692:     */       {
/* 1693:2149 */         PlotData2D temp = (PlotData2D)plots.get(i);
/* 1694:2150 */         Instances addInsts = temp.getPlotInstances();
/* 1695:2151 */         for (int j = 0; j < addInsts.numInstances(); j++) {
/* 1696:2152 */           saveInsts.add(addInsts.instance(j));
/* 1697:     */         }
/* 1698:     */       }
/* 1699:     */       try
/* 1700:     */       {
/* 1701:2156 */         int returnVal = this.m_FileChooser.showSaveDialog(this);
/* 1702:2157 */         if (returnVal == 0)
/* 1703:     */         {
/* 1704:2158 */           File sFile = this.m_FileChooser.getSelectedFile();
/* 1705:2159 */           if (!sFile.getName().toLowerCase().endsWith(".arff")) {
/* 1706:2160 */             sFile = new File(sFile.getParent(), sFile.getName() + ".arff");
/* 1707:     */           }
/* 1708:2164 */           File selected = sFile;
/* 1709:2165 */           Writer w = new BufferedWriter(new FileWriter(selected));
/* 1710:2166 */           w.write(saveInsts.toString());
/* 1711:2167 */           w.close();
/* 1712:     */         }
/* 1713:     */       }
/* 1714:     */       catch (Exception ex)
/* 1715:     */       {
/* 1716:2170 */         ex.printStackTrace();
/* 1717:     */       }
/* 1718:     */     }
/* 1719:     */   }
/* 1720:     */   
/* 1721:     */   public void setColourIndex(int index, boolean enableCombo)
/* 1722:     */   {
/* 1723:2182 */     if (index >= 0) {
/* 1724:2183 */       this.m_ColourCombo.setSelectedIndex(index);
/* 1725:     */     } else {
/* 1726:2185 */       this.m_ColourCombo.setSelectedIndex(0);
/* 1727:     */     }
/* 1728:2187 */     this.m_ColourCombo.setEnabled(enableCombo);
/* 1729:     */   }
/* 1730:     */   
/* 1731:     */   public void setColourIndex(int index)
/* 1732:     */   {
/* 1733:2198 */     setColourIndex(index, false);
/* 1734:     */   }
/* 1735:     */   
/* 1736:     */   public void setXIndex(int index)
/* 1737:     */     throws Exception
/* 1738:     */   {
/* 1739:2208 */     if ((index >= 0) && (index < this.m_XCombo.getItemCount())) {
/* 1740:2209 */       this.m_XCombo.setSelectedIndex(index);
/* 1741:     */     } else {
/* 1742:2211 */       throw new Exception("x index is out of range!");
/* 1743:     */     }
/* 1744:     */   }
/* 1745:     */   
/* 1746:     */   public int getXIndex()
/* 1747:     */   {
/* 1748:2221 */     return this.m_XCombo.getSelectedIndex();
/* 1749:     */   }
/* 1750:     */   
/* 1751:     */   public void setYIndex(int index)
/* 1752:     */     throws Exception
/* 1753:     */   {
/* 1754:2231 */     if ((index >= 0) && (index < this.m_YCombo.getItemCount())) {
/* 1755:2232 */       this.m_YCombo.setSelectedIndex(index);
/* 1756:     */     } else {
/* 1757:2234 */       throw new Exception("y index is out of range!");
/* 1758:     */     }
/* 1759:     */   }
/* 1760:     */   
/* 1761:     */   public int getYIndex()
/* 1762:     */   {
/* 1763:2244 */     return this.m_YCombo.getSelectedIndex();
/* 1764:     */   }
/* 1765:     */   
/* 1766:     */   public int getCIndex()
/* 1767:     */   {
/* 1768:2253 */     return this.m_ColourCombo.getSelectedIndex();
/* 1769:     */   }
/* 1770:     */   
/* 1771:     */   public int getSIndex()
/* 1772:     */   {
/* 1773:2262 */     return this.m_ShapeCombo.getSelectedIndex();
/* 1774:     */   }
/* 1775:     */   
/* 1776:     */   public void setSIndex(int index)
/* 1777:     */     throws Exception
/* 1778:     */   {
/* 1779:2272 */     if ((index >= 0) && (index < this.m_ShapeCombo.getItemCount())) {
/* 1780:2273 */       this.m_ShapeCombo.setSelectedIndex(index);
/* 1781:     */     } else {
/* 1782:2275 */       throw new Exception("s index is out of range!");
/* 1783:     */     }
/* 1784:     */   }
/* 1785:     */   
/* 1786:     */   public void addActionListener(ActionListener act)
/* 1787:     */   {
/* 1788:2285 */     this.listener = act;
/* 1789:     */   }
/* 1790:     */   
/* 1791:     */   public void setName(String plotName)
/* 1792:     */   {
/* 1793:2295 */     this.m_plotName = plotName;
/* 1794:     */   }
/* 1795:     */   
/* 1796:     */   public String getName()
/* 1797:     */   {
/* 1798:2306 */     return this.m_plotName;
/* 1799:     */   }
/* 1800:     */   
/* 1801:     */   public Instances getInstances()
/* 1802:     */   {
/* 1803:2315 */     return this.m_plot.m_plotInstances;
/* 1804:     */   }
/* 1805:     */   
/* 1806:     */   protected void newColorAttribute(int a, Instances i)
/* 1807:     */   {
/* 1808:2328 */     if (i.attribute(a).isNominal())
/* 1809:     */     {
/* 1810:2329 */       for (int noa = this.m_colorList.size(); noa < i.attribute(a).numValues(); noa++)
/* 1811:     */       {
/* 1812:2330 */         Color pc = this.m_DefaultColors[(noa % 10)];
/* 1813:2331 */         int ija = noa / 10;
/* 1814:2332 */         ija *= 2;
/* 1815:2333 */         for (int j = 0; j < ija; j++) {
/* 1816:2334 */           pc = pc.brighter();
/* 1817:     */         }
/* 1818:2337 */         this.m_colorList.add(pc);
/* 1819:     */       }
/* 1820:2339 */       this.m_plot.setColours(this.m_colorList);
/* 1821:2340 */       this.m_attrib.setColours(this.m_colorList);
/* 1822:2341 */       this.m_classPanel.setColours(this.m_colorList);
/* 1823:     */     }
/* 1824:     */   }
/* 1825:     */   
/* 1826:     */   public void setShapes(ArrayList<ArrayList<Double>> l)
/* 1827:     */   {
/* 1828:2352 */     this.m_plot.setShapes(l);
/* 1829:     */   }
/* 1830:     */   
/* 1831:     */   public void setInstances(Instances inst)
/* 1832:     */   {
/* 1833:2361 */     if ((inst.numAttributes() > 0) && (inst.numInstances() > 0)) {
/* 1834:2362 */       newColorAttribute(inst.numAttributes() - 1, inst);
/* 1835:     */     }
/* 1836:2365 */     PlotData2D temp = new PlotData2D(inst);
/* 1837:2366 */     temp.setPlotName(inst.relationName());
/* 1838:     */     try
/* 1839:     */     {
/* 1840:2369 */       setMasterPlot(temp);
/* 1841:     */     }
/* 1842:     */     catch (Exception ex)
/* 1843:     */     {
/* 1844:2371 */       System.err.println(ex);
/* 1845:2372 */       ex.printStackTrace();
/* 1846:     */     }
/* 1847:     */   }
/* 1848:     */   
/* 1849:     */   public void setUpComboBoxes(Instances inst)
/* 1850:     */   {
/* 1851:2382 */     setProperties(inst.relationName());
/* 1852:2383 */     int prefX = -1;
/* 1853:2384 */     int prefY = -1;
/* 1854:2385 */     if (inst.numAttributes() > 1) {
/* 1855:2386 */       prefY = 1;
/* 1856:     */     }
/* 1857:2388 */     int prefC = -1;
/* 1858:2389 */     String[] XNames = new String[inst.numAttributes()];
/* 1859:2390 */     String[] YNames = new String[inst.numAttributes()];
/* 1860:2391 */     String[] CNames = new String[inst.numAttributes()];
/* 1861:2392 */     for (int i = 0; i < XNames.length; i++)
/* 1862:     */     {
/* 1863:2393 */       String type = " (" + Attribute.typeToStringShort(inst.attribute(i)) + ")";
/* 1864:2394 */       XNames[i] = ("X: " + inst.attribute(i).name() + type);
/* 1865:2395 */       YNames[i] = ("Y: " + inst.attribute(i).name() + type);
/* 1866:2396 */       CNames[i] = ("Colour: " + inst.attribute(i).name() + type);
/* 1867:2397 */       if ((this.m_preferredXDimension != null) && 
/* 1868:2398 */         (this.m_preferredXDimension.compareTo(inst.attribute(i).name()) == 0)) {
/* 1869:2399 */         prefX = i;
/* 1870:     */       }
/* 1871:2403 */       if ((this.m_preferredYDimension != null) && 
/* 1872:2404 */         (this.m_preferredYDimension.compareTo(inst.attribute(i).name()) == 0)) {
/* 1873:2405 */         prefY = i;
/* 1874:     */       }
/* 1875:2409 */       if ((this.m_preferredColourDimension != null) && 
/* 1876:2410 */         (this.m_preferredColourDimension.compareTo(inst.attribute(i).name()) == 0)) {
/* 1877:2411 */         prefC = i;
/* 1878:     */       }
/* 1879:     */     }
/* 1880:2416 */     this.m_XCombo.setModel(new DefaultComboBoxModel(XNames));
/* 1881:2417 */     this.m_YCombo.setModel(new DefaultComboBoxModel(YNames));
/* 1882:     */     
/* 1883:2419 */     this.m_ColourCombo.setModel(new DefaultComboBoxModel(CNames));
/* 1884:     */     
/* 1885:     */ 
/* 1886:2422 */     this.m_XCombo.setEnabled(true);
/* 1887:2423 */     this.m_YCombo.setEnabled(true);
/* 1888:2425 */     if (this.m_splitListener == null)
/* 1889:     */     {
/* 1890:2426 */       this.m_ColourCombo.setEnabled(true);
/* 1891:2427 */       this.m_ColourCombo.setSelectedIndex(inst.numAttributes() - 1);
/* 1892:     */     }
/* 1893:2429 */     this.m_plotSurround.setBorder(BorderFactory.createTitledBorder("Plot: " + inst.relationName()));
/* 1894:     */     try
/* 1895:     */     {
/* 1896:2432 */       if (prefX != -1) {
/* 1897:2433 */         setXIndex(prefX);
/* 1898:     */       }
/* 1899:2435 */       if (prefY != -1) {
/* 1900:2436 */         setYIndex(prefY);
/* 1901:     */       }
/* 1902:2438 */       if (prefC != -1) {
/* 1903:2439 */         this.m_ColourCombo.setSelectedIndex(prefC);
/* 1904:     */       }
/* 1905:     */     }
/* 1906:     */     catch (Exception ex)
/* 1907:     */     {
/* 1908:2442 */       System.err.println("Problem setting preferred Visualization dimensions");
/* 1909:     */     }
/* 1910:     */   }
/* 1911:     */   
/* 1912:     */   public void removeAllPlots()
/* 1913:     */   {
/* 1914:2450 */     this.m_plot.removeAllPlots();
/* 1915:     */   }
/* 1916:     */   
/* 1917:     */   public void setMasterPlot(PlotData2D newPlot)
/* 1918:     */     throws Exception
/* 1919:     */   {
/* 1920:2460 */     this.m_plot.setMasterPlot(newPlot);
/* 1921:2461 */     setUpComboBoxes(newPlot.m_plotInstances);
/* 1922:2462 */     this.m_saveBut.setEnabled(true);
/* 1923:2463 */     repaint();
/* 1924:     */   }
/* 1925:     */   
/* 1926:     */   public void addPlot(PlotData2D newPlot)
/* 1927:     */     throws Exception
/* 1928:     */   {
/* 1929:2473 */     this.m_plot.addPlot(newPlot);
/* 1930:2474 */     if (this.m_plot.m_plot2D.getMasterPlot() != null) {
/* 1931:2475 */       setUpComboBoxes(newPlot.m_plotInstances);
/* 1932:     */     }
/* 1933:2477 */     this.m_saveBut.setEnabled(true);
/* 1934:2478 */     repaint();
/* 1935:     */   }
/* 1936:     */   
/* 1937:     */   public PlotPanel getPlotPanel()
/* 1938:     */   {
/* 1939:2487 */     return this.m_plot;
/* 1940:     */   }
/* 1941:     */   
/* 1942:     */   public static void main(String[] args)
/* 1943:     */   {
/* 1944:     */     try
/* 1945:     */     {
/* 1946:2497 */       if (args.length < 1)
/* 1947:     */       {
/* 1948:2498 */         System.err.println("Usage : weka.gui.visualize.VisualizePanel <dataset> [<dataset> <dataset>...]");
/* 1949:     */         
/* 1950:2500 */         System.exit(1);
/* 1951:     */       }
/* 1952:2503 */       weka.core.logging.Logger.log(Logger.Level.INFO, "Logging started");
/* 1953:     */       
/* 1954:2505 */       JFrame jf = new JFrame("Weka Explorer: Visualize");
/* 1955:     */       
/* 1956:2507 */       jf.setSize(500, 400);
/* 1957:2508 */       jf.getContentPane().setLayout(new BorderLayout());
/* 1958:2509 */       VisualizePanel sp = new VisualizePanel();
/* 1959:     */       
/* 1960:2511 */       jf.getContentPane().add(sp, "Center");
/* 1961:2512 */       jf.addWindowListener(new WindowAdapter()
/* 1962:     */       {
/* 1963:     */         public void windowClosing(WindowEvent e)
/* 1964:     */         {
/* 1965:2515 */           this.val$jf.dispose();
/* 1966:2516 */           System.exit(0);
/* 1967:     */         }
/* 1968:2519 */       });
/* 1969:2520 */       jf.setVisible(true);
/* 1970:2521 */       if (args.length >= 1) {
/* 1971:2522 */         for (int j = 0; j < args.length; j++)
/* 1972:     */         {
/* 1973:2523 */           System.err.println("Loading instances from " + args[j]);
/* 1974:2524 */           Reader r = new BufferedReader(new FileReader(args[j]));
/* 1975:     */           
/* 1976:2526 */           Instances i = new Instances(r);
/* 1977:2527 */           i.setClassIndex(i.numAttributes() - 1);
/* 1978:2528 */           PlotData2D pd1 = new PlotData2D(i);
/* 1979:2530 */           if (j == 0)
/* 1980:     */           {
/* 1981:2531 */             pd1.setPlotName("Master plot");
/* 1982:2532 */             sp.setMasterPlot(pd1);
/* 1983:     */           }
/* 1984:     */           else
/* 1985:     */           {
/* 1986:2534 */             pd1.setPlotName("Plot " + (j + 1));
/* 1987:2535 */             pd1.m_useCustomColour = true;
/* 1988:2536 */             pd1.m_customColour = (j % 2 == 0 ? Color.red : Color.blue);
/* 1989:2537 */             sp.addPlot(pd1);
/* 1990:     */           }
/* 1991:     */         }
/* 1992:     */       }
/* 1993:     */     }
/* 1994:     */     catch (Exception ex)
/* 1995:     */     {
/* 1996:2542 */       ex.printStackTrace();
/* 1997:2543 */       System.err.println(ex.getMessage());
/* 1998:     */     }
/* 1999:     */   }
/* 2000:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.VisualizePanel
 * JD-Core Version:    0.7.0.1
 */