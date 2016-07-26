/*    1:     */ package weka.classifiers.trees;
/*    2:     */ 
/*    3:     */ import java.awt.Container;
/*    4:     */ import java.awt.Frame;
/*    5:     */ import java.awt.Point;
/*    6:     */ import java.awt.event.ActionEvent;
/*    7:     */ import java.awt.event.ActionListener;
/*    8:     */ import java.awt.event.WindowAdapter;
/*    9:     */ import java.awt.event.WindowEvent;
/*   10:     */ import java.io.PrintStream;
/*   11:     */ import java.io.Serializable;
/*   12:     */ import java.util.ArrayList;
/*   13:     */ import javax.swing.JFrame;
/*   14:     */ import javax.swing.JOptionPane;
/*   15:     */ import javax.swing.JTabbedPane;
/*   16:     */ import weka.classifiers.AbstractClassifier;
/*   17:     */ import weka.classifiers.Classifier;
/*   18:     */ import weka.classifiers.functions.LinearRegression;
/*   19:     */ import weka.classifiers.rules.ZeroR;
/*   20:     */ import weka.core.Attribute;
/*   21:     */ import weka.core.Capabilities;
/*   22:     */ import weka.core.Capabilities.Capability;
/*   23:     */ import weka.core.Drawable;
/*   24:     */ import weka.core.Instance;
/*   25:     */ import weka.core.Instances;
/*   26:     */ import weka.core.RevisionHandler;
/*   27:     */ import weka.core.RevisionUtils;
/*   28:     */ import weka.core.TechnicalInformation;
/*   29:     */ import weka.core.TechnicalInformation.Field;
/*   30:     */ import weka.core.TechnicalInformation.Type;
/*   31:     */ import weka.core.TechnicalInformationHandler;
/*   32:     */ import weka.core.Utils;
/*   33:     */ import weka.filters.Filter;
/*   34:     */ import weka.filters.unsupervised.attribute.Remove;
/*   35:     */ import weka.gui.GenericObjectEditor;
/*   36:     */ import weka.gui.GenericObjectEditor.GOEPanel;
/*   37:     */ import weka.gui.PropertyDialog;
/*   38:     */ import weka.gui.treevisualizer.PlaceNode1;
/*   39:     */ import weka.gui.treevisualizer.PlaceNode2;
/*   40:     */ import weka.gui.treevisualizer.TreeDisplayEvent;
/*   41:     */ import weka.gui.treevisualizer.TreeDisplayListener;
/*   42:     */ import weka.gui.treevisualizer.TreeVisualizer;
/*   43:     */ import weka.gui.visualize.VisualizePanel;
/*   44:     */ import weka.gui.visualize.VisualizePanelEvent;
/*   45:     */ import weka.gui.visualize.VisualizePanelListener;
/*   46:     */ 
/*   47:     */ public class UserClassifier
/*   48:     */   extends AbstractClassifier
/*   49:     */   implements Drawable, TreeDisplayListener, VisualizePanelListener, TechnicalInformationHandler
/*   50:     */ {
/*   51:     */   static final long serialVersionUID = 6483901103562809843L;
/*   52:     */   private static final int LEAF = 0;
/*   53:     */   private static final int RECTANGLE = 1;
/*   54:     */   private static final int POLYGON = 2;
/*   55:     */   private static final int POLYLINE = 3;
/*   56:     */   private static final int VLINE = 5;
/*   57:     */   private static final int HLINE = 6;
/*   58: 130 */   private transient TreeVisualizer m_tView = null;
/*   59: 132 */   private transient VisualizePanel m_iView = null;
/*   60:     */   private TreeClass m_top;
/*   61:     */   private TreeClass m_focus;
/*   62:     */   private int m_nextId;
/*   63:     */   private transient JTabbedPane m_reps;
/*   64:     */   private transient JFrame m_mainWin;
/*   65: 142 */   private boolean m_built = false;
/*   66:     */   private GenericObjectEditor m_classifiers;
/*   67:     */   private PropertyDialog m_propertyDialog;
/*   68:     */   
/*   69:     */   public static void main(String[] argv)
/*   70:     */   {
/*   71: 159 */     runClassifier(new UserClassifier(), argv);
/*   72:     */   }
/*   73:     */   
/*   74:     */   public String toString()
/*   75:     */   {
/*   76: 167 */     if (!this.m_built) {
/*   77: 169 */       return "Tree Not Built";
/*   78:     */     }
/*   79: 171 */     StringBuffer text = new StringBuffer();
/*   80:     */     try
/*   81:     */     {
/*   82: 173 */       this.m_top.toString(0, text);
/*   83:     */       
/*   84: 175 */       this.m_top.objectStrings(text);
/*   85:     */     }
/*   86:     */     catch (Exception e)
/*   87:     */     {
/*   88: 178 */       System.out.println("error: " + e.getMessage());
/*   89:     */     }
/*   90: 181 */     return text.toString();
/*   91:     */   }
/*   92:     */   
/*   93:     */   public void userCommand(TreeDisplayEvent e)
/*   94:     */   {
/*   95: 193 */     if (this.m_propertyDialog != null)
/*   96:     */     {
/*   97: 194 */       this.m_propertyDialog.dispose();
/*   98: 195 */       this.m_propertyDialog = null;
/*   99:     */     }
/*  100:     */     try
/*  101:     */     {
/*  102: 198 */       if (((this.m_iView == null) || (this.m_tView != null)) || 
/*  103:     */       
/*  104:     */ 
/*  105: 201 */         (e.getCommand() != 0)) {
/*  106: 203 */         if (e.getCommand() == 1)
/*  107:     */         {
/*  108: 205 */           if (this.m_top == null)
/*  109:     */           {
/*  110: 208 */             System.out.println("Error : Received event from a TreeDisplayer that is unknown to the classifier.");
/*  111:     */           }
/*  112:     */           else
/*  113:     */           {
/*  114: 211 */             this.m_tView.setHighlight(e.getID());
/*  115:     */             
/*  116:     */ 
/*  117:     */ 
/*  118:     */ 
/*  119: 216 */             this.m_focus = this.m_top.getNode(e.getID());
/*  120: 217 */             this.m_iView.setInstances(this.m_focus.m_training);
/*  121: 218 */             if (this.m_focus.m_attrib1 >= 0) {
/*  122: 219 */               this.m_iView.setXIndex(this.m_focus.m_attrib1);
/*  123:     */             }
/*  124: 221 */             if (this.m_focus.m_attrib2 >= 0) {
/*  125: 222 */               this.m_iView.setYIndex(this.m_focus.m_attrib2);
/*  126:     */             }
/*  127: 224 */             this.m_iView.setColourIndex(this.m_focus.m_training.classIndex());
/*  128: 225 */             if (((Double)((ArrayList)this.m_focus.m_ranges.get(0)).get(0)).intValue() != 0) {
/*  129: 226 */               this.m_iView.setShapes(this.m_focus.m_ranges);
/*  130:     */             }
/*  131:     */           }
/*  132:     */         }
/*  133: 230 */         else if (e.getCommand() == 2)
/*  134:     */         {
/*  135: 235 */           this.m_focus = this.m_top.getNode(e.getID());
/*  136: 236 */           this.m_iView.setInstances(this.m_focus.m_training);
/*  137: 237 */           if (this.m_focus.m_attrib1 >= 0) {
/*  138: 238 */             this.m_iView.setXIndex(this.m_focus.m_attrib1);
/*  139:     */           }
/*  140: 240 */           if (this.m_focus.m_attrib2 >= 0) {
/*  141: 241 */             this.m_iView.setYIndex(this.m_focus.m_attrib2);
/*  142:     */           }
/*  143: 243 */           this.m_iView.setColourIndex(this.m_focus.m_training.classIndex());
/*  144: 244 */           if (((Double)((ArrayList)this.m_focus.m_ranges.get(0)).get(0)).intValue() != 0) {
/*  145: 245 */             this.m_iView.setShapes(this.m_focus.m_ranges);
/*  146:     */           }
/*  147: 249 */           this.m_focus.m_set1 = null;
/*  148: 250 */           this.m_focus.m_set2 = null;
/*  149: 251 */           this.m_focus.setInfo(this.m_focus.m_attrib1, this.m_focus.m_attrib2, null);
/*  150:     */           
/*  151: 253 */           this.m_tView = new TreeVisualizer(this, graph(), new PlaceNode2());
/*  152:     */           
/*  153: 255 */           this.m_reps.setComponentAt(0, this.m_tView);
/*  154:     */           
/*  155: 257 */           this.m_tView.setHighlight(this.m_focus.m_identity);
/*  156:     */         }
/*  157: 258 */         else if (e.getCommand() == 4)
/*  158:     */         {
/*  159: 263 */           this.m_focus = this.m_top.getNode(e.getID());
/*  160: 264 */           this.m_iView.setInstances(this.m_focus.m_training);
/*  161: 265 */           if (this.m_focus.m_attrib1 >= 0) {
/*  162: 266 */             this.m_iView.setXIndex(this.m_focus.m_attrib1);
/*  163:     */           }
/*  164: 268 */           if (this.m_focus.m_attrib2 >= 0) {
/*  165: 269 */             this.m_iView.setYIndex(this.m_focus.m_attrib2);
/*  166:     */           }
/*  167: 271 */           this.m_iView.setColourIndex(this.m_focus.m_training.classIndex());
/*  168: 272 */           if (((Double)((ArrayList)this.m_focus.m_ranges.get(0)).get(0)).intValue() != 0) {
/*  169: 273 */             this.m_iView.setShapes(this.m_focus.m_ranges);
/*  170:     */           }
/*  171: 276 */           Classifier classifierAtNode = this.m_focus.getClassifier();
/*  172: 277 */           if (classifierAtNode != null) {
/*  173: 278 */             this.m_classifiers.setValue(classifierAtNode);
/*  174:     */           }
/*  175: 280 */           this.m_propertyDialog = new PropertyDialog((Frame)null, this.m_classifiers, this.m_mainWin.getLocationOnScreen().x, this.m_mainWin.getLocationOnScreen().y);
/*  176:     */           
/*  177: 282 */           this.m_propertyDialog.setVisible(true);
/*  178:     */           
/*  179:     */ 
/*  180:     */ 
/*  181:     */ 
/*  182:     */ 
/*  183:     */ 
/*  184:     */ 
/*  185:     */ 
/*  186:     */ 
/*  187:     */ 
/*  188:     */ 
/*  189:     */ 
/*  190: 295 */           this.m_tView.setHighlight(this.m_focus.m_identity);
/*  191:     */         }
/*  192: 301 */         else if (e.getCommand() == 3)
/*  193:     */         {
/*  194: 303 */           int well = JOptionPane.showConfirmDialog(this.m_mainWin, "Are You Sure...\nClick Yes To Accept The Tree\n Click No To Return", "Accept Tree", 0);
/*  195: 307 */           if (well == 0)
/*  196:     */           {
/*  197: 308 */             this.m_mainWin.setDefaultCloseOperation(2);
/*  198: 309 */             this.m_mainWin.dispose();
/*  199: 310 */             blocker(false);
/*  200:     */           }
/*  201:     */         }
/*  202:     */       }
/*  203:     */     }
/*  204:     */     catch (Exception er)
/*  205:     */     {
/*  206: 316 */       System.out.println("Error : " + er);
/*  207: 317 */       System.out.println("Part of user input so had to catch here");
/*  208: 318 */       er.printStackTrace();
/*  209:     */     }
/*  210:     */   }
/*  211:     */   
/*  212:     */   public void userDataEvent(VisualizePanelEvent e)
/*  213:     */   {
/*  214: 331 */     if (this.m_propertyDialog != null)
/*  215:     */     {
/*  216: 332 */       this.m_propertyDialog.dispose();
/*  217: 333 */       this.m_propertyDialog = null;
/*  218:     */     }
/*  219:     */     try
/*  220:     */     {
/*  221: 337 */       if (this.m_focus != null)
/*  222:     */       {
/*  223: 339 */         double wdom = e.getInstances1().numInstances() + e.getInstances2().numInstances();
/*  224: 341 */         if (wdom == 0.0D) {
/*  225: 342 */           wdom = 1.0D;
/*  226:     */         }
/*  227: 345 */         TreeClass tmp = this.m_focus;
/*  228: 346 */         this.m_focus.m_set1 = new TreeClass(null, e.getAttribute1(), e.getAttribute2(), this.m_nextId, e.getInstances1().numInstances() / wdom, e.getInstances1(), this.m_focus);
/*  229:     */         
/*  230:     */ 
/*  231:     */ 
/*  232: 350 */         this.m_focus.m_set2 = new TreeClass(null, e.getAttribute1(), e.getAttribute2(), this.m_nextId, e.getInstances2().numInstances() / wdom, e.getInstances2(), this.m_focus);
/*  233:     */         
/*  234:     */ 
/*  235:     */ 
/*  236:     */ 
/*  237:     */ 
/*  238: 356 */         this.m_focus.setInfo(e.getAttribute1(), e.getAttribute2(), e.getValues());
/*  239:     */         
/*  240: 358 */         this.m_tView = new TreeVisualizer(this, graph(), new PlaceNode2());
/*  241:     */         
/*  242:     */ 
/*  243: 361 */         this.m_reps.setComponentAt(0, this.m_tView);
/*  244:     */         
/*  245: 363 */         this.m_focus = this.m_focus.m_set2;
/*  246: 364 */         this.m_tView.setHighlight(this.m_focus.m_identity);
/*  247: 365 */         this.m_iView.setInstances(this.m_focus.m_training);
/*  248: 366 */         if (tmp.m_attrib1 >= 0) {
/*  249: 367 */           this.m_iView.setXIndex(tmp.m_attrib1);
/*  250:     */         }
/*  251: 369 */         if (tmp.m_attrib2 >= 0) {
/*  252: 370 */           this.m_iView.setYIndex(tmp.m_attrib2);
/*  253:     */         }
/*  254: 372 */         this.m_iView.setColourIndex(this.m_focus.m_training.classIndex());
/*  255: 373 */         if (((Double)((ArrayList)this.m_focus.m_ranges.get(0)).get(0)).intValue() != 0) {
/*  256: 374 */           this.m_iView.setShapes(this.m_focus.m_ranges);
/*  257:     */         }
/*  258:     */       }
/*  259:     */       else
/*  260:     */       {
/*  261: 378 */         System.out.println("Somehow the focus is null");
/*  262:     */       }
/*  263:     */     }
/*  264:     */     catch (Exception er)
/*  265:     */     {
/*  266: 381 */       System.out.println("Error : " + er);
/*  267: 382 */       System.out.println("Part of user input so had to catch here");
/*  268:     */     }
/*  269:     */   }
/*  270:     */   
/*  271:     */   public UserClassifier()
/*  272:     */   {
/*  273: 393 */     this.m_top = null;
/*  274: 394 */     this.m_tView = null;
/*  275: 395 */     this.m_iView = null;
/*  276: 396 */     this.m_nextId = 0;
/*  277:     */   }
/*  278:     */   
/*  279:     */   public int graphType()
/*  280:     */   {
/*  281: 407 */     return 1;
/*  282:     */   }
/*  283:     */   
/*  284:     */   public String graph()
/*  285:     */     throws Exception
/*  286:     */   {
/*  287: 418 */     StringBuffer text = new StringBuffer();
/*  288: 419 */     text.append("digraph UserClassifierTree {\nnode [fontsize=10]\nedge [fontsize=10 style=bold]\n");
/*  289:     */     
/*  290:     */ 
/*  291: 422 */     this.m_top.toDotty(text);
/*  292: 423 */     return text.toString() + "}\n";
/*  293:     */   }
/*  294:     */   
/*  295:     */   private synchronized void blocker(boolean tf)
/*  296:     */   {
/*  297: 435 */     if (tf) {
/*  298:     */       try
/*  299:     */       {
/*  300: 437 */         wait();
/*  301:     */       }
/*  302:     */       catch (InterruptedException e) {}
/*  303:     */     } else {
/*  304: 441 */       notifyAll();
/*  305:     */     }
/*  306:     */   }
/*  307:     */   
/*  308:     */   public String globalInfo()
/*  309:     */   {
/*  310: 454 */     return "Interactively classify through visual means. You are Presented with a scatter graph of the data against two user selectable attributes, as well as a view of the decision tree. You can create binary splits by creating polygons around data plotted on the scatter graph, as well as by allowing another classifier to take over at points in the decision tree should you see fit.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*  311:     */   }
/*  312:     */   
/*  313:     */   public TechnicalInformation getTechnicalInformation()
/*  314:     */   {
/*  315: 475 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  316: 476 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Malcolm Ware and Eibe Frank and Geoffrey Holmes and Mark Hall and Ian H. Witten");
/*  317:     */     
/*  318:     */ 
/*  319:     */ 
/*  320: 480 */     result.setValue(TechnicalInformation.Field.YEAR, "2001");
/*  321: 481 */     result.setValue(TechnicalInformation.Field.TITLE, "Interactive machine learning: letting users build classifiers");
/*  322:     */     
/*  323: 483 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Int. J. Hum.-Comput. Stud.");
/*  324: 484 */     result.setValue(TechnicalInformation.Field.VOLUME, "55");
/*  325: 485 */     result.setValue(TechnicalInformation.Field.NUMBER, "3");
/*  326: 486 */     result.setValue(TechnicalInformation.Field.PAGES, "281-292");
/*  327: 487 */     result.setValue(TechnicalInformation.Field.PS, "http://www.cs.waikato.ac.nz/~ml/publications/2000/00MW-etal-Interactive-ML.ps");
/*  328:     */     
/*  329:     */ 
/*  330:     */ 
/*  331: 491 */     return result;
/*  332:     */   }
/*  333:     */   
/*  334:     */   public Capabilities getCapabilities()
/*  335:     */   {
/*  336: 501 */     Capabilities result = super.getCapabilities();
/*  337: 502 */     result.disableAll();
/*  338:     */     
/*  339:     */ 
/*  340: 505 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  341: 506 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  342: 507 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  343: 508 */     result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
/*  344: 509 */     result.enable(Capabilities.Capability.RELATIONAL_ATTRIBUTES);
/*  345: 510 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  346:     */     
/*  347:     */ 
/*  348: 513 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  349: 514 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  350: 515 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  351: 516 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  352:     */     
/*  353:     */ 
/*  354: 519 */     result.setMinimumNumberInstances(0);
/*  355:     */     
/*  356: 521 */     return result;
/*  357:     */   }
/*  358:     */   
/*  359:     */   public void buildClassifier(Instances i)
/*  360:     */     throws Exception
/*  361:     */   {
/*  362: 533 */     getCapabilities().testWithFail(i);
/*  363:     */     
/*  364:     */ 
/*  365: 536 */     i = new Instances(i);
/*  366: 537 */     i.deleteWithMissingClass();
/*  367:     */     
/*  368:     */ 
/*  369:     */ 
/*  370:     */ 
/*  371:     */ 
/*  372:     */ 
/*  373: 544 */     this.m_classifiers = new GenericObjectEditor(true);
/*  374: 545 */     this.m_classifiers.setClassType(Classifier.class);
/*  375: 546 */     this.m_classifiers.setValue(new ZeroR());
/*  376:     */     
/*  377: 548 */     ((GenericObjectEditor.GOEPanel)this.m_classifiers.getCustomEditor()).addOkListener(new ActionListener()
/*  378:     */     {
/*  379:     */       public void actionPerformed(ActionEvent e)
/*  380:     */       {
/*  381:     */         try
/*  382:     */         {
/*  383: 556 */           UserClassifier.this.m_focus.m_set1 = null;
/*  384: 557 */           UserClassifier.this.m_focus.m_set2 = null;
/*  385: 558 */           UserClassifier.this.m_focus.setInfo(UserClassifier.this.m_focus.m_attrib1, UserClassifier.this.m_focus.m_attrib2, null);
/*  386: 559 */           UserClassifier.this.m_focus.setClassifier((Classifier)UserClassifier.this.m_classifiers.getValue());
/*  387:     */           
/*  388:     */ 
/*  389:     */ 
/*  390:     */ 
/*  391:     */ 
/*  392:     */ 
/*  393:     */ 
/*  394: 567 */           UserClassifier.this.m_tView = new TreeVisualizer(UserClassifier.this, UserClassifier.this.graph(), new PlaceNode2());
/*  395:     */           
/*  396: 569 */           UserClassifier.this.m_tView.setHighlight(UserClassifier.this.m_focus.m_identity);
/*  397: 570 */           UserClassifier.this.m_reps.setComponentAt(0, UserClassifier.this.m_tView);
/*  398: 571 */           UserClassifier.this.m_iView.setShapes(null);
/*  399:     */         }
/*  400:     */         catch (Exception er)
/*  401:     */         {
/*  402: 573 */           System.out.println("Error : " + er);
/*  403: 574 */           System.out.println("Part of user input so had to catch here");
/*  404: 575 */           JOptionPane.showMessageDialog(null, "Unable to use " + UserClassifier.this.m_focus.getClassifier().getClass().getName() + " at this node.\n" + "This exception was produced:\n" + er.toString(), "UserClassifier", 0);
/*  405:     */         }
/*  406:     */       }
/*  407: 583 */     });
/*  408: 584 */     this.m_built = false;
/*  409: 585 */     this.m_mainWin = new JFrame();
/*  410:     */     
/*  411: 587 */     this.m_mainWin.addWindowListener(new WindowAdapter()
/*  412:     */     {
/*  413:     */       public void windowClosing(WindowEvent e)
/*  414:     */       {
/*  415: 590 */         int well = JOptionPane.showConfirmDialog(UserClassifier.this.m_mainWin, "Are You Sure...\nClick Yes To Accept The Tree\n Click No To Return", "Accept Tree", 0);
/*  416: 594 */         if (well == 0)
/*  417:     */         {
/*  418: 595 */           UserClassifier.this.m_mainWin.setDefaultCloseOperation(2);
/*  419: 596 */           UserClassifier.this.blocker(false);
/*  420:     */         }
/*  421:     */         else
/*  422:     */         {
/*  423: 599 */           UserClassifier.this.m_mainWin.setDefaultCloseOperation(0);
/*  424:     */         }
/*  425:     */       }
/*  426: 603 */     });
/*  427: 604 */     this.m_reps = new JTabbedPane();
/*  428: 605 */     this.m_mainWin.getContentPane().add(this.m_reps);
/*  429:     */     
/*  430:     */ 
/*  431: 608 */     Instances te = new Instances(i, i.numInstances());
/*  432: 609 */     for (int noa = 0; noa < i.numInstances(); noa++) {
/*  433: 610 */       te.add(i.instance(noa));
/*  434:     */     }
/*  435: 613 */     te.deleteWithMissingClass();
/*  436:     */     
/*  437:     */ 
/*  438: 616 */     this.m_top = new TreeClass(null, 0, 0, this.m_nextId, 1.0D, te, null);
/*  439: 617 */     this.m_focus = this.m_top;
/*  440:     */     
/*  441: 619 */     this.m_tView = new TreeVisualizer(this, graph(), new PlaceNode1());
/*  442:     */     
/*  443: 621 */     this.m_reps.add("Tree Visualizer", this.m_tView);
/*  444:     */     
/*  445:     */ 
/*  446:     */ 
/*  447:     */ 
/*  448:     */ 
/*  449: 627 */     this.m_tView.setHighlight(this.m_top.m_identity);
/*  450: 628 */     this.m_iView = new VisualizePanel(this);
/*  451:     */     
/*  452: 630 */     this.m_iView.setInstances(this.m_top.m_training);
/*  453: 631 */     this.m_iView.setColourIndex(te.classIndex());
/*  454:     */     
/*  455:     */ 
/*  456:     */ 
/*  457:     */ 
/*  458: 636 */     this.m_reps.add("Data Visualizer", this.m_iView);
/*  459: 637 */     this.m_mainWin.setSize(560, 420);
/*  460: 638 */     this.m_mainWin.setVisible(true);
/*  461: 639 */     blocker(true);
/*  462: 643 */     if (this.m_propertyDialog != null)
/*  463:     */     {
/*  464: 644 */       this.m_propertyDialog.dispose();
/*  465: 645 */       this.m_propertyDialog = null;
/*  466:     */     }
/*  467: 649 */     this.m_classifiers = null;
/*  468: 650 */     this.m_built = true;
/*  469:     */   }
/*  470:     */   
/*  471:     */   public double[] distributionForInstance(Instance i)
/*  472:     */     throws Exception
/*  473:     */   {
/*  474: 664 */     if (!this.m_built) {
/*  475: 665 */       return null;
/*  476:     */     }
/*  477: 668 */     double[] res = this.m_top.calcClassType(i);
/*  478: 669 */     if (this.m_top.m_training.classAttribute().isNumeric()) {
/*  479: 670 */       return res;
/*  480:     */     }
/*  481: 674 */     double highest = -1.0D;
/*  482: 675 */     double count = 0.0D;
/*  483: 676 */     for (int noa = 0; noa < this.m_top.m_training.numClasses(); noa++)
/*  484:     */     {
/*  485: 677 */       count += res[noa];
/*  486: 678 */       if (res[noa] > highest) {
/*  487: 680 */         highest = res[noa];
/*  488:     */       }
/*  489:     */     }
/*  490: 684 */     if (count <= 0.0D) {
/*  491: 686 */       return null;
/*  492:     */     }
/*  493: 689 */     for (int noa = 0; noa < this.m_top.m_training.numClasses(); noa++) {
/*  494: 690 */       res[noa] /= count;
/*  495:     */     }
/*  496: 694 */     return res;
/*  497:     */   }
/*  498:     */   
/*  499:     */   private class TreeClass
/*  500:     */     implements Serializable, RevisionHandler
/*  501:     */   {
/*  502:     */     static final long serialVersionUID = 595663560871347434L;
/*  503:     */     public ArrayList<ArrayList<Double>> m_ranges;
/*  504:     */     public int m_attrib1;
/*  505:     */     public int m_attrib2;
/*  506:     */     public TreeClass m_set1;
/*  507:     */     public TreeClass m_set2;
/*  508:     */     public TreeClass m_parent;
/*  509:     */     public String m_identity;
/*  510:     */     public double m_weight;
/*  511:     */     public Instances m_training;
/*  512:     */     public Classifier m_classObject;
/*  513:     */     public Filter m_filter;
/*  514:     */     
/*  515:     */     public TreeClass(int r, int a1, int a2, double id, Instances arg7, TreeClass i)
/*  516:     */       throws Exception
/*  517:     */     {
/*  518: 753 */       this.m_set1 = null;
/*  519: 754 */       this.m_set2 = null;
/*  520: 755 */       this.m_ranges = r;
/*  521: 756 */       this.m_classObject = null;
/*  522: 757 */       this.m_filter = null;
/*  523: 758 */       this.m_training = i;
/*  524: 759 */       this.m_attrib1 = a1;
/*  525: 760 */       this.m_attrib2 = a2;
/*  526: 761 */       this.m_identity = ("N" + String.valueOf(id));
/*  527: 762 */       this.m_weight = w;
/*  528: 763 */       this.m_parent = p;
/*  529: 764 */       UserClassifier.access$708(UserClassifier.this);
/*  530: 765 */       if (this.m_ranges == null) {
/*  531: 767 */         setLeaf();
/*  532:     */       }
/*  533:     */     }
/*  534:     */     
/*  535:     */     public void setClassifier(Classifier c)
/*  536:     */       throws Exception
/*  537:     */     {
/*  538: 791 */       this.m_classObject = c;
/*  539: 792 */       this.m_classObject.buildClassifier(this.m_training);
/*  540:     */     }
/*  541:     */     
/*  542:     */     public Classifier getClassifier()
/*  543:     */     {
/*  544: 802 */       return this.m_classObject;
/*  545:     */     }
/*  546:     */     
/*  547:     */     public void setInfo(int at1, int at2, ArrayList<ArrayList<Double>> ar)
/*  548:     */       throws Exception
/*  549:     */     {
/*  550: 817 */       this.m_classObject = null;
/*  551: 818 */       this.m_filter = null;
/*  552: 819 */       this.m_attrib1 = at1;
/*  553: 820 */       this.m_attrib2 = at2;
/*  554: 821 */       this.m_ranges = ar;
/*  555: 824 */       if (this.m_ranges == null) {
/*  556: 825 */         setLeaf();
/*  557:     */       }
/*  558:     */     }
/*  559:     */     
/*  560:     */     private void setLeaf()
/*  561:     */       throws Exception
/*  562:     */     {
/*  563: 854 */       if (this.m_training != null) {
/*  564: 856 */         if (this.m_training.classAttribute().isNominal())
/*  565:     */         {
/*  566: 860 */           this.m_ranges = new ArrayList(1);
/*  567: 861 */           this.m_ranges.add(new ArrayList(this.m_training.numClasses() + 1));
/*  568: 862 */           ArrayList<Double> tmp = (ArrayList)this.m_ranges.get(0);
/*  569: 863 */           tmp.add(new Double(0.0D));
/*  570: 864 */           for (int noa = 0; noa < this.m_training.numClasses(); noa++) {
/*  571: 865 */             tmp.add(new Double(0.0D));
/*  572:     */           }
/*  573: 867 */           for (int noa = 0; noa < this.m_training.numInstances(); noa++) {
/*  574: 868 */             tmp.set((int)this.m_training.instance(noa).classValue() + 1, new Double(((Double)tmp.get((int)this.m_training.instance(noa).classValue() + 1)).doubleValue() + this.m_training.instance(noa).weight()));
/*  575:     */           }
/*  576:     */         }
/*  577:     */         else
/*  578:     */         {
/*  579: 877 */           this.m_ranges = new ArrayList(1);
/*  580: 878 */           double t1 = 0.0D;
/*  581: 879 */           for (int noa = 0; noa < this.m_training.numInstances(); noa++) {
/*  582: 880 */             t1 += this.m_training.instance(noa).classValue();
/*  583:     */           }
/*  584: 883 */           if (this.m_training.numInstances() != 0) {
/*  585: 884 */             t1 /= this.m_training.numInstances();
/*  586:     */           }
/*  587: 886 */           double t2 = 0.0D;
/*  588: 887 */           for (int noa = 0; noa < this.m_training.numInstances(); noa++) {
/*  589: 888 */             t2 += Math.pow(this.m_training.instance(noa).classValue() - t1, 2.0D);
/*  590:     */           }
/*  591: 891 */           if (this.m_training.numInstances() != 0)
/*  592:     */           {
/*  593: 892 */             t1 = Math.sqrt(t2 / this.m_training.numInstances());
/*  594: 893 */             this.m_ranges.add(new ArrayList(2));
/*  595: 894 */             ArrayList<Double> tmp = (ArrayList)this.m_ranges.get(0);
/*  596: 895 */             tmp.add(new Double(0.0D));
/*  597: 896 */             tmp.add(new Double(t1));
/*  598:     */           }
/*  599:     */           else
/*  600:     */           {
/*  601: 898 */             this.m_ranges.add(new ArrayList(2));
/*  602: 899 */             ArrayList<Double> tmp = (ArrayList)this.m_ranges.get(0);
/*  603: 900 */             tmp.add(new Double(0.0D));
/*  604: 901 */             tmp.add(new Double((0.0D / 0.0D)));
/*  605:     */           }
/*  606:     */         }
/*  607:     */       }
/*  608:     */     }
/*  609:     */     
/*  610:     */     public double[] calcClassType(Instance i)
/*  611:     */       throws Exception
/*  612:     */     {
/*  613: 922 */       double x = 0.0D;double y = 0.0D;
/*  614: 923 */       if (this.m_attrib1 >= 0) {
/*  615: 924 */         x = i.value(this.m_attrib1);
/*  616:     */       }
/*  617: 926 */       if (this.m_attrib2 >= 0) {
/*  618: 927 */         y = i.value(this.m_attrib2);
/*  619:     */       }
/*  620:     */       double[] rt;
/*  621:     */       double[] rt;
/*  622: 930 */       if (this.m_training.classAttribute().isNominal()) {
/*  623: 931 */         rt = new double[this.m_training.numClasses()];
/*  624:     */       } else {
/*  625: 933 */         rt = new double[1];
/*  626:     */       }
/*  627: 937 */       if (this.m_classObject != null)
/*  628:     */       {
/*  629: 939 */         if (this.m_training.classAttribute().isNominal())
/*  630:     */         {
/*  631: 940 */           rt[((int)this.m_classObject.classifyInstance(i))] = 1.0D;
/*  632:     */         }
/*  633: 942 */         else if (this.m_filter != null)
/*  634:     */         {
/*  635: 943 */           this.m_filter.input(i);
/*  636: 944 */           rt[0] = this.m_classObject.classifyInstance(this.m_filter.output());
/*  637:     */         }
/*  638:     */         else
/*  639:     */         {
/*  640: 946 */           rt[0] = this.m_classObject.classifyInstance(i);
/*  641:     */         }
/*  642: 950 */         return rt;
/*  643:     */       }
/*  644: 951 */       if (((Double)((ArrayList)this.m_ranges.get(0)).get(0)).intValue() == 0)
/*  645:     */       {
/*  646: 956 */         if (this.m_training.classAttribute().isNumeric())
/*  647:     */         {
/*  648: 958 */           setLinear();
/*  649: 959 */           this.m_filter.input(i);
/*  650: 960 */           rt[0] = this.m_classObject.classifyInstance(this.m_filter.output());
/*  651: 961 */           return rt;
/*  652:     */         }
/*  653: 964 */         int totaler = 0;
/*  654: 965 */         ArrayList<Double> tmp = (ArrayList)this.m_ranges.get(0);
/*  655: 966 */         for (int noa = 0; noa < this.m_training.numClasses(); noa++)
/*  656:     */         {
/*  657: 967 */           rt[noa] = ((Double)tmp.get(noa + 1)).doubleValue();
/*  658: 968 */           totaler = (int)(totaler + rt[noa]);
/*  659:     */         }
/*  660: 970 */         for (int noa = 0; noa < this.m_training.numClasses(); noa++) {
/*  661: 971 */           rt[noa] /= totaler;
/*  662:     */         }
/*  663: 973 */         return rt;
/*  664:     */       }
/*  665: 976 */       for (int noa = 0; noa < this.m_ranges.size(); noa++)
/*  666:     */       {
/*  667: 978 */         ArrayList<Double> tmp = (ArrayList)this.m_ranges.get(noa);
/*  668: 980 */         if ((((Double)tmp.get(0)).intValue() != 5) || (Utils.isMissingValue(x))) {
/*  669: 982 */           if ((((Double)tmp.get(0)).intValue() != 6) || (Utils.isMissingValue(y)))
/*  670:     */           {
/*  671: 984 */             if ((Utils.isMissingValue(x)) || (Utils.isMissingValue(y)))
/*  672:     */             {
/*  673: 987 */               rt = this.m_set1.calcClassType(i);
/*  674: 988 */               double[] tem = this.m_set2.calcClassType(i);
/*  675: 989 */               if (this.m_training.classAttribute().isNominal())
/*  676:     */               {
/*  677: 990 */                 for (int nob = 0; nob < this.m_training.numClasses(); nob++)
/*  678:     */                 {
/*  679: 991 */                   rt[nob] *= this.m_set1.m_weight;
/*  680: 992 */                   rt[nob] += tem[nob] * this.m_set2.m_weight;
/*  681:     */                 }
/*  682:     */               }
/*  683:     */               else
/*  684:     */               {
/*  685: 995 */                 rt[0] *= this.m_set1.m_weight;
/*  686: 996 */                 rt[0] += tem[0] * this.m_set2.m_weight;
/*  687:     */               }
/*  688: 998 */               return rt;
/*  689:     */             }
/*  690: 999 */             if (((Double)tmp.get(0)).intValue() == 1)
/*  691:     */             {
/*  692:1001 */               if ((x >= ((Double)tmp.get(1)).doubleValue()) && (x <= ((Double)tmp.get(3)).doubleValue()) && (y <= ((Double)tmp.get(2)).doubleValue()) && (y >= ((Double)tmp.get(4)).doubleValue()))
/*  693:     */               {
/*  694:1007 */                 rt = this.m_set1.calcClassType(i);
/*  695:1008 */                 return rt;
/*  696:     */               }
/*  697:     */             }
/*  698:1011 */             else if (((Double)tmp.get(0)).intValue() == 2)
/*  699:     */             {
/*  700:1012 */               if (inPoly(tmp, x, y))
/*  701:     */               {
/*  702:1013 */                 rt = this.m_set1.calcClassType(i);
/*  703:1014 */                 return rt;
/*  704:     */               }
/*  705:     */             }
/*  706:1016 */             else if ((((Double)tmp.get(0)).intValue() == 3) && 
/*  707:1017 */               (inPolyline(tmp, x, y)))
/*  708:     */             {
/*  709:1018 */               rt = this.m_set1.calcClassType(i);
/*  710:1019 */               return rt;
/*  711:     */             }
/*  712:     */           }
/*  713:     */         }
/*  714:     */       }
/*  715:1024 */       if (this.m_set2 != null) {
/*  716:1025 */         rt = this.m_set2.calcClassType(i);
/*  717:     */       }
/*  718:1027 */       return rt;
/*  719:     */     }
/*  720:     */     
/*  721:     */     private void setLinear()
/*  722:     */       throws Exception
/*  723:     */     {
/*  724:1041 */       boolean[] attributeList = new boolean[this.m_training.numAttributes()];
/*  725:1042 */       for (int noa = 0; noa < this.m_training.numAttributes(); noa++) {
/*  726:1043 */         attributeList[noa] = false;
/*  727:     */       }
/*  728:1046 */       TreeClass temp = this;
/*  729:1047 */       attributeList[this.m_training.classIndex()] = true;
/*  730:1048 */       while (temp != null)
/*  731:     */       {
/*  732:1049 */         attributeList[temp.m_attrib1] = true;
/*  733:1050 */         attributeList[temp.m_attrib2] = true;
/*  734:1051 */         temp = temp.m_parent;
/*  735:     */       }
/*  736:1053 */       int classind = 0;
/*  737:1056 */       for (int noa = 0; noa < this.m_training.classIndex(); noa++) {
/*  738:1057 */         if (attributeList[noa] != 0) {
/*  739:1058 */           classind++;
/*  740:     */         }
/*  741:     */       }
/*  742:1062 */       int count = 0;
/*  743:1063 */       for (int noa = 0; noa < this.m_training.numAttributes(); noa++) {
/*  744:1064 */         if (attributeList[noa] != 0) {
/*  745:1065 */           count++;
/*  746:     */         }
/*  747:     */       }
/*  748:1070 */       int[] attributeList2 = new int[count];
/*  749:1071 */       count = 0;
/*  750:1072 */       for (int noa = 0; noa < this.m_training.numAttributes(); noa++) {
/*  751:1073 */         if (attributeList[noa] != 0)
/*  752:     */         {
/*  753:1074 */           attributeList2[count] = noa;
/*  754:1075 */           count++;
/*  755:     */         }
/*  756:     */       }
/*  757:1079 */       this.m_filter = new Remove();
/*  758:1080 */       ((Remove)this.m_filter).setInvertSelection(true);
/*  759:1081 */       ((Remove)this.m_filter).setAttributeIndicesArray(attributeList2);
/*  760:1082 */       this.m_filter.setInputFormat(this.m_training);
/*  761:     */       
/*  762:1084 */       Instances temp2 = Filter.useFilter(this.m_training, this.m_filter);
/*  763:1085 */       temp2.setClassIndex(classind);
/*  764:1086 */       this.m_classObject = new LinearRegression();
/*  765:1087 */       this.m_classObject.buildClassifier(temp2);
/*  766:     */     }
/*  767:     */     
/*  768:     */     private boolean inPolyline(ArrayList<Double> ob, double x, double y)
/*  769:     */     {
/*  770:1106 */       int countx = 0;
/*  771:1111 */       for (int noa = 1; noa < ob.size() - 4; noa += 2)
/*  772:     */       {
/*  773:1112 */         double y1 = ((Double)ob.get(noa + 1)).doubleValue();
/*  774:1113 */         double y2 = ((Double)ob.get(noa + 3)).doubleValue();
/*  775:1114 */         double x1 = ((Double)ob.get(noa)).doubleValue();
/*  776:1115 */         double x2 = ((Double)ob.get(noa + 2)).doubleValue();
/*  777:1116 */         double vecy = y2 - y1;
/*  778:1117 */         double vecx = x2 - x1;
/*  779:1118 */         if ((noa == 1) && (noa == ob.size() - 6))
/*  780:     */         {
/*  781:1120 */           if (vecy != 0.0D)
/*  782:     */           {
/*  783:1121 */             double change = (y - y1) / vecy;
/*  784:1122 */             if (vecx * change + x1 >= x) {
/*  785:1124 */               countx++;
/*  786:     */             }
/*  787:     */           }
/*  788:     */         }
/*  789:1127 */         else if (noa == 1)
/*  790:     */         {
/*  791:1128 */           if (((y < y2) && (vecy > 0.0D)) || ((y > y2) && (vecy < 0.0D)))
/*  792:     */           {
/*  793:1130 */             double change = (y - y1) / vecy;
/*  794:1131 */             if (vecx * change + x1 >= x) {
/*  795:1133 */               countx++;
/*  796:     */             }
/*  797:     */           }
/*  798:     */         }
/*  799:1136 */         else if (noa == ob.size() - 6)
/*  800:     */         {
/*  801:1138 */           if (((y <= y1) && (vecy < 0.0D)) || ((y >= y1) && (vecy > 0.0D)))
/*  802:     */           {
/*  803:1139 */             double change = (y - y1) / vecy;
/*  804:1140 */             if (vecx * change + x1 >= x) {
/*  805:1141 */               countx++;
/*  806:     */             }
/*  807:     */           }
/*  808:     */         }
/*  809:1145 */         else if (((y1 <= y) && (y < y2)) || ((y2 < y) && (y <= y1))) {
/*  810:1147 */           if (vecy != 0.0D)
/*  811:     */           {
/*  812:1151 */             double change = (y - y1) / vecy;
/*  813:1152 */             if (vecx * change + x1 >= x) {
/*  814:1154 */               countx++;
/*  815:     */             }
/*  816:     */           }
/*  817:     */         }
/*  818:     */       }
/*  819:1162 */       double y1 = ((Double)ob.get(ob.size() - 2)).doubleValue();
/*  820:1163 */       double y2 = ((Double)ob.get(ob.size() - 1)).doubleValue();
/*  821:1165 */       if (y1 > y2)
/*  822:     */       {
/*  823:1167 */         if ((y1 >= y) && (y > y2)) {
/*  824:1168 */           countx++;
/*  825:     */         }
/*  826:     */       }
/*  827:1172 */       else if ((y1 >= y) || (y > y2)) {
/*  828:1173 */         countx++;
/*  829:     */       }
/*  830:1177 */       if (countx % 2 == 1) {
/*  831:1178 */         return true;
/*  832:     */       }
/*  833:1180 */       return false;
/*  834:     */     }
/*  835:     */     
/*  836:     */     private boolean inPoly(ArrayList<Double> ob, double x, double y)
/*  837:     */     {
/*  838:1193 */       int count = 0;
/*  839:1197 */       for (int noa = 1; noa < ob.size() - 2; noa += 2)
/*  840:     */       {
/*  841:1198 */         double y1 = ((Double)ob.get(noa + 1)).doubleValue();
/*  842:1199 */         double y2 = ((Double)ob.get(noa + 3)).doubleValue();
/*  843:1200 */         if (((y1 <= y) && (y < y2)) || ((y2 < y) && (y <= y1)))
/*  844:     */         {
/*  845:1202 */           double vecy = y2 - y1;
/*  846:1203 */           if (vecy != 0.0D)
/*  847:     */           {
/*  848:1206 */             double x1 = ((Double)ob.get(noa)).doubleValue();
/*  849:1207 */             double x2 = ((Double)ob.get(noa + 2)).doubleValue();
/*  850:1208 */             double vecx = x2 - x1;
/*  851:1209 */             double change = (y - y1) / vecy;
/*  852:1210 */             if (vecx * change + x1 >= x) {
/*  853:1212 */               count++;
/*  854:     */             }
/*  855:     */           }
/*  856:     */         }
/*  857:     */       }
/*  858:1218 */       if (count % 2 == 1) {
/*  859:1221 */         return true;
/*  860:     */       }
/*  861:1224 */       return false;
/*  862:     */     }
/*  863:     */     
/*  864:     */     public TreeClass getNode(String id)
/*  865:     */     {
/*  866:1239 */       if (id.equals(this.m_identity)) {
/*  867:1240 */         return this;
/*  868:     */       }
/*  869:1243 */       if (this.m_set1 != null)
/*  870:     */       {
/*  871:1244 */         TreeClass tmp = this.m_set1.getNode(id);
/*  872:1245 */         if (tmp != null) {
/*  873:1246 */           return tmp;
/*  874:     */         }
/*  875:     */       }
/*  876:1249 */       if (this.m_set2 != null)
/*  877:     */       {
/*  878:1250 */         TreeClass tmp = this.m_set2.getNode(id);
/*  879:1251 */         if (tmp != null) {
/*  880:1252 */           return tmp;
/*  881:     */         }
/*  882:     */       }
/*  883:1255 */       return null;
/*  884:     */     }
/*  885:     */     
/*  886:     */     public void getAlternateLabel(StringBuffer s)
/*  887:     */       throws Exception
/*  888:     */     {
/*  889:1269 */       ArrayList<Double> tmp = (ArrayList)this.m_ranges.get(0);
/*  890:1271 */       if ((this.m_classObject != null) && (this.m_training.classAttribute().isNominal()))
/*  891:     */       {
/*  892:1272 */         s.append("Classified by " + this.m_classObject.getClass().getName());
/*  893:     */       }
/*  894:1273 */       else if (((Double)tmp.get(0)).intValue() == 0)
/*  895:     */       {
/*  896:1274 */         if (this.m_training.classAttribute().isNominal())
/*  897:     */         {
/*  898:1275 */           double high = -1000.0D;
/*  899:1276 */           int num = 0;
/*  900:1277 */           double count = 0.0D;
/*  901:1278 */           for (int noa = 0; noa < this.m_training.classAttribute().numValues(); noa++)
/*  902:     */           {
/*  903:1279 */             if (((Double)tmp.get(noa + 1)).doubleValue() > high)
/*  904:     */             {
/*  905:1280 */               high = ((Double)tmp.get(noa + 1)).doubleValue();
/*  906:1281 */               num = noa + 1;
/*  907:     */             }
/*  908:1283 */             count += ((Double)tmp.get(noa + 1)).doubleValue();
/*  909:     */           }
/*  910:1285 */           s.append(this.m_training.classAttribute().value(num - 1) + "(" + count);
/*  911:1286 */           if (count > high) {
/*  912:1287 */             s.append("/" + (count - high));
/*  913:     */           }
/*  914:1289 */           s.append(")");
/*  915:     */         }
/*  916:     */         else
/*  917:     */         {
/*  918:1291 */           if ((this.m_classObject == null) && (((Double)tmp.get(0)).intValue() == 0)) {
/*  919:1292 */             setLinear();
/*  920:     */           }
/*  921:1294 */           s.append("Standard Deviation = " + Utils.doubleToString(((Double)tmp.get(1)).doubleValue(), 6));
/*  922:     */         }
/*  923:     */       }
/*  924:     */       else
/*  925:     */       {
/*  926:1299 */         s.append("Split on ");
/*  927:1300 */         s.append(this.m_training.attribute(this.m_attrib1).name() + " AND ");
/*  928:1301 */         s.append(this.m_training.attribute(this.m_attrib2).name());
/*  929:     */       }
/*  930:     */     }
/*  931:     */     
/*  932:     */     public void getLabel(StringBuffer s)
/*  933:     */       throws Exception
/*  934:     */     {
/*  935:1318 */       ArrayList<Double> tmp = (ArrayList)this.m_ranges.get(0);
/*  936:1320 */       if ((this.m_classObject != null) && (this.m_training.classAttribute().isNominal()))
/*  937:     */       {
/*  938:1321 */         s.append("Classified by\\n" + this.m_classObject.getClass().getName());
/*  939:     */       }
/*  940:1322 */       else if (((Double)tmp.get(0)).intValue() == 0)
/*  941:     */       {
/*  942:1324 */         if (this.m_training.classAttribute().isNominal())
/*  943:     */         {
/*  944:1325 */           boolean first = true;
/*  945:1326 */           for (int noa = 0; noa < this.m_training.classAttribute().numValues(); noa++) {
/*  946:1327 */             if (((Double)tmp.get(noa + 1)).doubleValue() > 0.0D)
/*  947:     */             {
/*  948:1328 */               if (first)
/*  949:     */               {
/*  950:1329 */                 s.append("[" + this.m_training.classAttribute().value(noa));
/*  951:1330 */                 first = false;
/*  952:     */               }
/*  953:     */               else
/*  954:     */               {
/*  955:1332 */                 s.append("\\n[" + this.m_training.classAttribute().value(noa));
/*  956:     */               }
/*  957:1334 */               s.append(", " + ((Double)tmp.get(noa + 1)).doubleValue() + "]");
/*  958:     */             }
/*  959:     */           }
/*  960:     */         }
/*  961:     */         else
/*  962:     */         {
/*  963:1338 */           if ((this.m_classObject == null) && (((Double)tmp.get(0)).intValue() == 0)) {
/*  964:1339 */             setLinear();
/*  965:     */           }
/*  966:1341 */           s.append("Standard Deviation = " + Utils.doubleToString(((Double)tmp.get(1)).doubleValue(), 6));
/*  967:     */         }
/*  968:     */       }
/*  969:     */       else
/*  970:     */       {
/*  971:1345 */         s.append("Split on\\n");
/*  972:1346 */         s.append(this.m_training.attribute(this.m_attrib1).name() + " AND\\n");
/*  973:1347 */         s.append(this.m_training.attribute(this.m_attrib2).name());
/*  974:     */       }
/*  975:     */     }
/*  976:     */     
/*  977:     */     public void toDotty(StringBuffer t)
/*  978:     */       throws Exception
/*  979:     */     {
/*  980:1360 */       t.append(this.m_identity + " [label=\"");
/*  981:1361 */       getLabel(t);
/*  982:1362 */       t.append("\" ");
/*  983:1365 */       if (((Double)((ArrayList)this.m_ranges.get(0)).get(0)).intValue() == 0) {
/*  984:1366 */         t.append("shape=box ");
/*  985:     */       } else {
/*  986:1368 */         t.append("shape=ellipse ");
/*  987:     */       }
/*  988:1370 */       t.append("style=filled color=gray95]\n");
/*  989:1372 */       if (this.m_set1 != null)
/*  990:     */       {
/*  991:1373 */         t.append(this.m_identity + "->");
/*  992:1374 */         t.append(this.m_set1.m_identity + " [label=\"True\"]\n");
/*  993:     */         
/*  994:1376 */         this.m_set1.toDotty(t);
/*  995:     */       }
/*  996:1378 */       if (this.m_set2 != null)
/*  997:     */       {
/*  998:1379 */         t.append(this.m_identity + "->");
/*  999:1380 */         t.append(this.m_set2.m_identity + " [label=\"False\"]\n");
/* 1000:     */         
/* 1001:     */ 
/* 1002:1383 */         this.m_set2.toDotty(t);
/* 1003:     */       }
/* 1004:     */     }
/* 1005:     */     
/* 1006:     */     public void objectStrings(StringBuffer t)
/* 1007:     */     {
/* 1008:1395 */       if (this.m_classObject != null) {
/* 1009:1396 */         t.append("\n\n" + this.m_identity + " {\n" + this.m_classObject.toString() + "\n}");
/* 1010:     */       }
/* 1011:1399 */       if (this.m_set1 != null) {
/* 1012:1400 */         this.m_set1.objectStrings(t);
/* 1013:     */       }
/* 1014:1402 */       if (this.m_set2 != null) {
/* 1015:1403 */         this.m_set2.objectStrings(t);
/* 1016:     */       }
/* 1017:     */     }
/* 1018:     */     
/* 1019:     */     public void toString(int l, StringBuffer t)
/* 1020:     */       throws Exception
/* 1021:     */     {
/* 1022:1416 */       if (((Double)((ArrayList)this.m_ranges.get(0)).get(0)).intValue() == 0)
/* 1023:     */       {
/* 1024:1417 */         t.append(": " + this.m_identity + " ");
/* 1025:1418 */         getAlternateLabel(t);
/* 1026:     */       }
/* 1027:1420 */       if (this.m_set1 != null)
/* 1028:     */       {
/* 1029:1421 */         t.append("\n");
/* 1030:1422 */         for (int noa = 0; noa < l; noa++) {
/* 1031:1423 */           t.append("|   ");
/* 1032:     */         }
/* 1033:1426 */         getAlternateLabel(t);
/* 1034:1427 */         t.append(" (In Set)");
/* 1035:1428 */         this.m_set1.toString(l + 1, t);
/* 1036:     */       }
/* 1037:1430 */       if (this.m_set2 != null)
/* 1038:     */       {
/* 1039:1431 */         t.append("\n");
/* 1040:1432 */         for (int noa = 0; noa < l; noa++) {
/* 1041:1433 */           t.append("|   ");
/* 1042:     */         }
/* 1043:1435 */         getAlternateLabel(t);
/* 1044:1436 */         t.append(" (Not in Set)");
/* 1045:1437 */         this.m_set2.toString(l + 1, t);
/* 1046:     */       }
/* 1047:     */     }
/* 1048:     */     
/* 1049:     */     public String getRevision()
/* 1050:     */     {
/* 1051:1449 */       return RevisionUtils.extract("$Revision: 10390 $");
/* 1052:     */     }
/* 1053:     */   }
/* 1054:     */   
/* 1055:     */   public String getRevision()
/* 1056:     */   {
/* 1057:1460 */     return RevisionUtils.extract("$Revision: 10390 $");
/* 1058:     */   }
/* 1059:     */   
/* 1060:     */   static {}
/* 1061:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.UserClassifier
 * JD-Core Version:    0.7.0.1
 */