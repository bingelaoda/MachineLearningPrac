/*    1:     */ package weka.classifiers.functions;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Color;
/*    5:     */ import java.awt.Component;
/*    6:     */ import java.awt.Container;
/*    7:     */ import java.awt.Dimension;
/*    8:     */ import java.awt.FontMetrics;
/*    9:     */ import java.awt.Graphics;
/*   10:     */ import java.awt.event.ActionEvent;
/*   11:     */ import java.awt.event.ActionListener;
/*   12:     */ import java.awt.event.MouseAdapter;
/*   13:     */ import java.awt.event.MouseEvent;
/*   14:     */ import java.awt.event.WindowAdapter;
/*   15:     */ import java.awt.event.WindowEvent;
/*   16:     */ import java.io.PrintStream;
/*   17:     */ import java.util.ArrayList;
/*   18:     */ import java.util.Collections;
/*   19:     */ import java.util.Enumeration;
/*   20:     */ import java.util.Random;
/*   21:     */ import java.util.StringTokenizer;
/*   22:     */ import java.util.Vector;
/*   23:     */ import javax.swing.BorderFactory;
/*   24:     */ import javax.swing.Box;
/*   25:     */ import javax.swing.BoxLayout;
/*   26:     */ import javax.swing.JButton;
/*   27:     */ import javax.swing.JFrame;
/*   28:     */ import javax.swing.JLabel;
/*   29:     */ import javax.swing.JOptionPane;
/*   30:     */ import javax.swing.JPanel;
/*   31:     */ import javax.swing.JScrollPane;
/*   32:     */ import javax.swing.JTextField;
/*   33:     */ import weka.classifiers.AbstractClassifier;
/*   34:     */ import weka.classifiers.Classifier;
/*   35:     */ import weka.classifiers.functions.neural.LinearUnit;
/*   36:     */ import weka.classifiers.functions.neural.NeuralConnection;
/*   37:     */ import weka.classifiers.functions.neural.NeuralNode;
/*   38:     */ import weka.classifiers.functions.neural.SigmoidUnit;
/*   39:     */ import weka.classifiers.rules.ZeroR;
/*   40:     */ import weka.core.Attribute;
/*   41:     */ import weka.core.Capabilities;
/*   42:     */ import weka.core.Capabilities.Capability;
/*   43:     */ import weka.core.DenseInstance;
/*   44:     */ import weka.core.Instance;
/*   45:     */ import weka.core.Instances;
/*   46:     */ import weka.core.Option;
/*   47:     */ import weka.core.OptionHandler;
/*   48:     */ import weka.core.Randomizable;
/*   49:     */ import weka.core.RevisionHandler;
/*   50:     */ import weka.core.RevisionUtils;
/*   51:     */ import weka.core.Utils;
/*   52:     */ import weka.core.WeightedInstancesHandler;
/*   53:     */ import weka.filters.Filter;
/*   54:     */ import weka.filters.unsupervised.attribute.NominalToBinary;
/*   55:     */ 
/*   56:     */ public class MultilayerPerceptron
/*   57:     */   extends AbstractClassifier
/*   58:     */   implements OptionHandler, WeightedInstancesHandler, Randomizable
/*   59:     */ {
/*   60:     */   private static final long serialVersionUID = -5990607817048210779L;
/*   61:     */   private Classifier m_ZeroR;
/*   62:     */   
/*   63:     */   public static void main(String[] argv)
/*   64:     */   {
/*   65: 183 */     runClassifier(new MultilayerPerceptron(), argv);
/*   66:     */   }
/*   67:     */   
/*   68:     */   protected class NeuralEnd
/*   69:     */     extends NeuralConnection
/*   70:     */   {
/*   71:     */     static final long serialVersionUID = 7305185603191183338L;
/*   72:     */     private int m_link;
/*   73:     */     private boolean m_input;
/*   74:     */     
/*   75:     */     public NeuralEnd(String id)
/*   76:     */     {
/*   77: 211 */       super();
/*   78:     */       
/*   79: 213 */       this.m_link = 0;
/*   80: 214 */       this.m_input = true;
/*   81:     */     }
/*   82:     */     
/*   83:     */     public boolean onUnit(Graphics g, int x, int y, int w, int h)
/*   84:     */     {
/*   85: 231 */       FontMetrics fm = g.getFontMetrics();
/*   86: 232 */       int l = (int)(this.m_x * w) - fm.stringWidth(this.m_id) / 2;
/*   87: 233 */       int t = (int)(this.m_y * h) - fm.getHeight() / 2;
/*   88: 234 */       if ((x < l) || (x > l + fm.stringWidth(this.m_id) + 4) || (y < t) || (y > t + fm.getHeight() + fm.getDescent() + 4)) {
/*   89: 236 */         return false;
/*   90:     */       }
/*   91: 238 */       return true;
/*   92:     */     }
/*   93:     */     
/*   94:     */     public void drawNode(Graphics g, int w, int h)
/*   95:     */     {
/*   96: 252 */       if ((this.m_type & 0x1) == 1) {
/*   97: 253 */         g.setColor(Color.green);
/*   98:     */       } else {
/*   99: 255 */         g.setColor(Color.orange);
/*  100:     */       }
/*  101: 258 */       FontMetrics fm = g.getFontMetrics();
/*  102: 259 */       int l = (int)(this.m_x * w) - fm.stringWidth(this.m_id) / 2;
/*  103: 260 */       int t = (int)(this.m_y * h) - fm.getHeight() / 2;
/*  104: 261 */       g.fill3DRect(l, t, fm.stringWidth(this.m_id) + 4, fm.getHeight() + fm.getDescent() + 4, true);
/*  105:     */       
/*  106: 263 */       g.setColor(Color.black);
/*  107:     */       
/*  108: 265 */       g.drawString(this.m_id, l + 2, t + fm.getHeight() + 2);
/*  109:     */     }
/*  110:     */     
/*  111:     */     public void drawHighlight(Graphics g, int w, int h)
/*  112:     */     {
/*  113: 279 */       g.setColor(Color.black);
/*  114: 280 */       FontMetrics fm = g.getFontMetrics();
/*  115: 281 */       int l = (int)(this.m_x * w) - fm.stringWidth(this.m_id) / 2;
/*  116: 282 */       int t = (int)(this.m_y * h) - fm.getHeight() / 2;
/*  117: 283 */       g.fillRect(l - 2, t - 2, fm.stringWidth(this.m_id) + 8, fm.getHeight() + fm.getDescent() + 8);
/*  118:     */       
/*  119: 285 */       drawNode(g, w, h);
/*  120:     */     }
/*  121:     */     
/*  122:     */     public double outputValue(boolean calculate)
/*  123:     */     {
/*  124: 298 */       if ((Double.isNaN(this.m_unitValue)) && (calculate)) {
/*  125: 299 */         if (this.m_input)
/*  126:     */         {
/*  127: 300 */           if (MultilayerPerceptron.this.m_currentInstance.isMissing(this.m_link)) {
/*  128: 301 */             this.m_unitValue = 0.0D;
/*  129:     */           } else {
/*  130: 304 */             this.m_unitValue = MultilayerPerceptron.this.m_currentInstance.value(this.m_link);
/*  131:     */           }
/*  132:     */         }
/*  133:     */         else
/*  134:     */         {
/*  135: 308 */           this.m_unitValue = 0.0D;
/*  136: 309 */           for (int noa = 0; noa < this.m_numInputs; noa++) {
/*  137: 310 */             this.m_unitValue += this.m_inputList[noa].outputValue(true);
/*  138:     */           }
/*  139: 313 */           if ((MultilayerPerceptron.this.m_numeric) && (MultilayerPerceptron.this.m_normalizeClass)) {
/*  140: 316 */             this.m_unitValue = (this.m_unitValue * MultilayerPerceptron.this.m_attributeRanges[MultilayerPerceptron.this.m_instances.classIndex()] + MultilayerPerceptron.this.m_attributeBases[MultilayerPerceptron.this.m_instances.classIndex()]);
/*  141:     */           }
/*  142:     */         }
/*  143:     */       }
/*  144: 322 */       return this.m_unitValue;
/*  145:     */     }
/*  146:     */     
/*  147:     */     public double errorValue(boolean calculate)
/*  148:     */     {
/*  149: 337 */       if ((!Double.isNaN(this.m_unitValue)) && (Double.isNaN(this.m_unitError)) && (calculate)) {
/*  150: 339 */         if (this.m_input)
/*  151:     */         {
/*  152: 340 */           this.m_unitError = 0.0D;
/*  153: 341 */           for (int noa = 0; noa < this.m_numOutputs; noa++) {
/*  154: 342 */             this.m_unitError += this.m_outputList[noa].errorValue(true);
/*  155:     */           }
/*  156:     */         }
/*  157: 345 */         else if (MultilayerPerceptron.this.m_currentInstance.classIsMissing())
/*  158:     */         {
/*  159: 346 */           this.m_unitError = 0.1D;
/*  160:     */         }
/*  161: 347 */         else if (MultilayerPerceptron.this.m_instances.classAttribute().isNominal())
/*  162:     */         {
/*  163: 348 */           if (MultilayerPerceptron.this.m_currentInstance.classValue() == this.m_link) {
/*  164: 349 */             this.m_unitError = (1.0D - this.m_unitValue);
/*  165:     */           } else {
/*  166: 351 */             this.m_unitError = (0.0D - this.m_unitValue);
/*  167:     */           }
/*  168:     */         }
/*  169: 353 */         else if (MultilayerPerceptron.this.m_numeric)
/*  170:     */         {
/*  171: 355 */           if (MultilayerPerceptron.this.m_normalizeClass)
/*  172:     */           {
/*  173: 356 */             if (MultilayerPerceptron.this.m_attributeRanges[MultilayerPerceptron.this.m_instances.classIndex()] == 0.0D) {
/*  174: 357 */               this.m_unitError = 0.0D;
/*  175:     */             } else {
/*  176: 359 */               this.m_unitError = ((MultilayerPerceptron.this.m_currentInstance.classValue() - this.m_unitValue) / MultilayerPerceptron.this.m_attributeRanges[MultilayerPerceptron.this.m_instances.classIndex()]);
/*  177:     */             }
/*  178:     */           }
/*  179:     */           else {
/*  180: 365 */             this.m_unitError = (MultilayerPerceptron.this.m_currentInstance.classValue() - this.m_unitValue);
/*  181:     */           }
/*  182:     */         }
/*  183:     */       }
/*  184: 370 */       return this.m_unitError;
/*  185:     */     }
/*  186:     */     
/*  187:     */     public void reset()
/*  188:     */     {
/*  189: 382 */       if ((!Double.isNaN(this.m_unitValue)) || (!Double.isNaN(this.m_unitError)))
/*  190:     */       {
/*  191: 383 */         this.m_unitValue = (0.0D / 0.0D);
/*  192: 384 */         this.m_unitError = (0.0D / 0.0D);
/*  193: 385 */         this.m_weightsUpdated = false;
/*  194: 386 */         for (int noa = 0; noa < this.m_numInputs; noa++) {
/*  195: 387 */           this.m_inputList[noa].reset();
/*  196:     */         }
/*  197:     */       }
/*  198:     */     }
/*  199:     */     
/*  200:     */     public void saveWeights()
/*  201:     */     {
/*  202: 397 */       for (int i = 0; i < this.m_numInputs; i++) {
/*  203: 398 */         this.m_inputList[i].saveWeights();
/*  204:     */       }
/*  205:     */     }
/*  206:     */     
/*  207:     */     public void restoreWeights()
/*  208:     */     {
/*  209: 407 */       for (int i = 0; i < this.m_numInputs; i++) {
/*  210: 408 */         this.m_inputList[i].restoreWeights();
/*  211:     */       }
/*  212:     */     }
/*  213:     */     
/*  214:     */     public void setLink(boolean input, int val)
/*  215:     */       throws Exception
/*  216:     */     {
/*  217: 421 */       this.m_input = input;
/*  218: 423 */       if (input) {
/*  219: 424 */         this.m_type = 1;
/*  220:     */       } else {
/*  221: 426 */         this.m_type = 2;
/*  222:     */       }
/*  223: 428 */       if ((val < 0) || ((input) && (val > MultilayerPerceptron.this.m_instances.numAttributes())) || ((!input) && (MultilayerPerceptron.this.m_instances.classAttribute().isNominal()) && (val > MultilayerPerceptron.this.m_instances.classAttribute().numValues()))) {
/*  224: 432 */         this.m_link = 0;
/*  225:     */       } else {
/*  226: 434 */         this.m_link = val;
/*  227:     */       }
/*  228:     */     }
/*  229:     */     
/*  230:     */     public int getLink()
/*  231:     */     {
/*  232: 442 */       return this.m_link;
/*  233:     */     }
/*  234:     */     
/*  235:     */     public String getRevision()
/*  236:     */     {
/*  237: 452 */       return RevisionUtils.extract("$Revision: 12449 $");
/*  238:     */     }
/*  239:     */   }
/*  240:     */   
/*  241:     */   private class NodePanel
/*  242:     */     extends JPanel
/*  243:     */     implements RevisionHandler
/*  244:     */   {
/*  245:     */     static final long serialVersionUID = -3067621833388149984L;
/*  246:     */     
/*  247:     */     public NodePanel()
/*  248:     */     {
/*  249: 470 */       addMouseListener(new MouseAdapter()
/*  250:     */       {
/*  251:     */         public void mousePressed(MouseEvent e)
/*  252:     */         {
/*  253: 475 */           if (!MultilayerPerceptron.this.m_stopped) {
/*  254: 476 */             return;
/*  255:     */           }
/*  256: 478 */           if (((e.getModifiers() & 0x10) == 16) && (!e.isAltDown()))
/*  257:     */           {
/*  258: 480 */             Graphics g = MultilayerPerceptron.NodePanel.this.getGraphics();
/*  259: 481 */             int x = e.getX();
/*  260: 482 */             int y = e.getY();
/*  261: 483 */             int w = MultilayerPerceptron.NodePanel.this.getWidth();
/*  262: 484 */             int h = MultilayerPerceptron.NodePanel.this.getHeight();
/*  263: 485 */             ArrayList<NeuralConnection> tmp = new ArrayList(4);
/*  264: 486 */             for (int noa = 0; noa < MultilayerPerceptron.this.m_numAttributes; noa++) {
/*  265: 487 */               if (MultilayerPerceptron.this.m_inputs[noa].onUnit(g, x, y, w, h))
/*  266:     */               {
/*  267: 488 */                 tmp.add(MultilayerPerceptron.this.m_inputs[noa]);
/*  268: 489 */                 MultilayerPerceptron.NodePanel.this.selection(tmp, (e.getModifiers() & 0x2) == 2, true);
/*  269:     */                 
/*  270:     */ 
/*  271:     */ 
/*  272: 493 */                 return;
/*  273:     */               }
/*  274:     */             }
/*  275: 496 */             for (int noa = 0; noa < MultilayerPerceptron.this.m_numClasses; noa++) {
/*  276: 497 */               if (MultilayerPerceptron.this.m_outputs[noa].onUnit(g, x, y, w, h))
/*  277:     */               {
/*  278: 498 */                 tmp.add(MultilayerPerceptron.this.m_outputs[noa]);
/*  279: 499 */                 MultilayerPerceptron.NodePanel.this.selection(tmp, (e.getModifiers() & 0x2) == 2, true);
/*  280:     */                 
/*  281:     */ 
/*  282:     */ 
/*  283: 503 */                 return;
/*  284:     */               }
/*  285:     */             }
/*  286: 506 */             for (NeuralConnection m_neuralNode : MultilayerPerceptron.this.m_neuralNodes) {
/*  287: 507 */               if (m_neuralNode.onUnit(g, x, y, w, h))
/*  288:     */               {
/*  289: 508 */                 tmp.add(m_neuralNode);
/*  290: 509 */                 MultilayerPerceptron.NodePanel.this.selection(tmp, (e.getModifiers() & 0x2) == 2, true);
/*  291:     */                 
/*  292:     */ 
/*  293:     */ 
/*  294: 513 */                 return;
/*  295:     */               }
/*  296:     */             }
/*  297: 517 */             NeuralNode temp = new NeuralNode(String.valueOf(MultilayerPerceptron.this.m_nextId), MultilayerPerceptron.this.m_random, MultilayerPerceptron.this.m_sigmoidUnit);
/*  298:     */             
/*  299: 519 */             MultilayerPerceptron.access$1308(MultilayerPerceptron.this);
/*  300: 520 */             temp.setX(e.getX() / w);
/*  301: 521 */             temp.setY(e.getY() / h);
/*  302: 522 */             tmp.add(temp);
/*  303: 523 */             MultilayerPerceptron.this.addNode(temp);
/*  304: 524 */             MultilayerPerceptron.NodePanel.this.selection(tmp, (e.getModifiers() & 0x2) == 2, true);
/*  305:     */           }
/*  306:     */           else
/*  307:     */           {
/*  308: 530 */             Graphics g = MultilayerPerceptron.NodePanel.this.getGraphics();
/*  309: 531 */             int x = e.getX();
/*  310: 532 */             int y = e.getY();
/*  311: 533 */             int w = MultilayerPerceptron.NodePanel.this.getWidth();
/*  312: 534 */             int h = MultilayerPerceptron.NodePanel.this.getHeight();
/*  313: 535 */             ArrayList<NeuralConnection> tmp = new ArrayList(4);
/*  314: 536 */             for (int noa = 0; noa < MultilayerPerceptron.this.m_numAttributes; noa++) {
/*  315: 537 */               if (MultilayerPerceptron.this.m_inputs[noa].onUnit(g, x, y, w, h))
/*  316:     */               {
/*  317: 538 */                 tmp.add(MultilayerPerceptron.this.m_inputs[noa]);
/*  318: 539 */                 MultilayerPerceptron.NodePanel.this.selection(tmp, (e.getModifiers() & 0x2) == 2, false);
/*  319:     */                 
/*  320:     */ 
/*  321:     */ 
/*  322: 543 */                 return;
/*  323:     */               }
/*  324:     */             }
/*  325: 547 */             for (int noa = 0; noa < MultilayerPerceptron.this.m_numClasses; noa++) {
/*  326: 548 */               if (MultilayerPerceptron.this.m_outputs[noa].onUnit(g, x, y, w, h))
/*  327:     */               {
/*  328: 549 */                 tmp.add(MultilayerPerceptron.this.m_outputs[noa]);
/*  329: 550 */                 MultilayerPerceptron.NodePanel.this.selection(tmp, (e.getModifiers() & 0x2) == 2, false);
/*  330:     */                 
/*  331:     */ 
/*  332:     */ 
/*  333: 554 */                 return;
/*  334:     */               }
/*  335:     */             }
/*  336: 557 */             for (NeuralConnection m_neuralNode : MultilayerPerceptron.this.m_neuralNodes) {
/*  337: 558 */               if (m_neuralNode.onUnit(g, x, y, w, h))
/*  338:     */               {
/*  339: 559 */                 tmp.add(m_neuralNode);
/*  340: 560 */                 MultilayerPerceptron.NodePanel.this.selection(tmp, (e.getModifiers() & 0x2) == 2, false);
/*  341:     */                 
/*  342:     */ 
/*  343:     */ 
/*  344: 564 */                 return;
/*  345:     */               }
/*  346:     */             }
/*  347: 567 */             MultilayerPerceptron.NodePanel.this.selection(null, (e.getModifiers() & 0x2) == 2, false);
/*  348:     */           }
/*  349:     */         }
/*  350:     */       });
/*  351:     */     }
/*  352:     */     
/*  353:     */     private void selection(ArrayList<NeuralConnection> v, boolean ctrl, boolean left)
/*  354:     */     {
/*  355: 589 */       if (v == null)
/*  356:     */       {
/*  357: 591 */         MultilayerPerceptron.this.m_selected.clear();
/*  358: 592 */         repaint();
/*  359: 593 */         return;
/*  360:     */       }
/*  361: 597 */       if (((ctrl) || (MultilayerPerceptron.this.m_selected.size() == 0)) && (left))
/*  362:     */       {
/*  363: 598 */         boolean removed = false;
/*  364: 599 */         for (int noa = 0; noa < v.size(); noa++)
/*  365:     */         {
/*  366: 600 */           removed = false;
/*  367: 601 */           for (int nob = 0; nob < MultilayerPerceptron.this.m_selected.size(); nob++) {
/*  368: 602 */             if (v.get(noa) == MultilayerPerceptron.this.m_selected.get(nob))
/*  369:     */             {
/*  370: 604 */               MultilayerPerceptron.this.m_selected.remove(nob);
/*  371: 605 */               removed = true;
/*  372: 606 */               break;
/*  373:     */             }
/*  374:     */           }
/*  375: 609 */           if (!removed) {
/*  376: 610 */             MultilayerPerceptron.this.m_selected.add(v.get(noa));
/*  377:     */           }
/*  378:     */         }
/*  379: 613 */         repaint();
/*  380: 614 */         return;
/*  381:     */       }
/*  382: 617 */       if (left) {
/*  383: 619 */         for (int noa = 0; noa < MultilayerPerceptron.this.m_selected.size(); noa++) {
/*  384: 620 */           for (int nob = 0; nob < v.size(); nob++) {
/*  385: 621 */             NeuralConnection.connect((NeuralConnection)MultilayerPerceptron.this.m_selected.get(noa), (NeuralConnection)v.get(nob));
/*  386:     */           }
/*  387:     */         }
/*  388: 624 */       } else if (MultilayerPerceptron.this.m_selected.size() > 0) {
/*  389: 627 */         for (int noa = 0; noa < MultilayerPerceptron.this.m_selected.size(); noa++) {
/*  390: 628 */           for (int nob = 0; nob < v.size(); nob++)
/*  391:     */           {
/*  392: 629 */             NeuralConnection.disconnect((NeuralConnection)MultilayerPerceptron.this.m_selected.get(noa), (NeuralConnection)v.get(nob));
/*  393:     */             
/*  394: 631 */             NeuralConnection.disconnect((NeuralConnection)v.get(nob), (NeuralConnection)MultilayerPerceptron.this.m_selected.get(noa));
/*  395:     */           }
/*  396:     */         }
/*  397:     */       } else {
/*  398: 638 */         for (int noa = 0; noa < v.size(); noa++)
/*  399:     */         {
/*  400: 639 */           ((NeuralConnection)v.get(noa)).removeAllInputs();
/*  401: 640 */           ((NeuralConnection)v.get(noa)).removeAllOutputs();
/*  402: 641 */           MultilayerPerceptron.this.removeNode((NeuralConnection)v.get(noa));
/*  403:     */         }
/*  404:     */       }
/*  405: 644 */       repaint();
/*  406:     */     }
/*  407:     */     
/*  408:     */     public void paintComponent(Graphics g)
/*  409:     */     {
/*  410: 655 */       super.paintComponent(g);
/*  411: 656 */       int x = getWidth();
/*  412: 657 */       int y = getHeight();
/*  413: 658 */       if ((25 * MultilayerPerceptron.this.m_numAttributes > 25 * MultilayerPerceptron.this.m_numClasses) && (25 * MultilayerPerceptron.this.m_numAttributes > y)) {
/*  414: 659 */         setSize(x, 25 * MultilayerPerceptron.this.m_numAttributes);
/*  415: 660 */       } else if (25 * MultilayerPerceptron.this.m_numClasses > y) {
/*  416: 661 */         setSize(x, 25 * MultilayerPerceptron.this.m_numClasses);
/*  417:     */       } else {
/*  418: 663 */         setSize(x, y);
/*  419:     */       }
/*  420: 666 */       y = getHeight();
/*  421: 667 */       for (int noa = 0; noa < MultilayerPerceptron.this.m_numAttributes; noa++) {
/*  422: 668 */         MultilayerPerceptron.this.m_inputs[noa].drawInputLines(g, x, y);
/*  423:     */       }
/*  424: 670 */       for (int noa = 0; noa < MultilayerPerceptron.this.m_numClasses; noa++)
/*  425:     */       {
/*  426: 671 */         MultilayerPerceptron.this.m_outputs[noa].drawInputLines(g, x, y);
/*  427: 672 */         MultilayerPerceptron.this.m_outputs[noa].drawOutputLines(g, x, y);
/*  428:     */       }
/*  429: 674 */       for (NeuralConnection m_neuralNode : MultilayerPerceptron.this.m_neuralNodes) {
/*  430: 675 */         m_neuralNode.drawInputLines(g, x, y);
/*  431:     */       }
/*  432: 677 */       for (int noa = 0; noa < MultilayerPerceptron.this.m_numAttributes; noa++) {
/*  433: 678 */         MultilayerPerceptron.this.m_inputs[noa].drawNode(g, x, y);
/*  434:     */       }
/*  435: 680 */       for (int noa = 0; noa < MultilayerPerceptron.this.m_numClasses; noa++) {
/*  436: 681 */         MultilayerPerceptron.this.m_outputs[noa].drawNode(g, x, y);
/*  437:     */       }
/*  438: 683 */       for (NeuralConnection m_neuralNode : MultilayerPerceptron.this.m_neuralNodes) {
/*  439: 684 */         m_neuralNode.drawNode(g, x, y);
/*  440:     */       }
/*  441: 687 */       for (int noa = 0; noa < MultilayerPerceptron.this.m_selected.size(); noa++) {
/*  442: 688 */         ((NeuralConnection)MultilayerPerceptron.this.m_selected.get(noa)).drawHighlight(g, x, y);
/*  443:     */       }
/*  444:     */     }
/*  445:     */     
/*  446:     */     public String getRevision()
/*  447:     */     {
/*  448: 699 */       return RevisionUtils.extract("$Revision: 12449 $");
/*  449:     */     }
/*  450:     */   }
/*  451:     */   
/*  452:     */   class ControlPanel
/*  453:     */     extends JPanel
/*  454:     */     implements RevisionHandler
/*  455:     */   {
/*  456:     */     static final long serialVersionUID = 7393543302294142271L;
/*  457:     */     public JButton m_startStop;
/*  458:     */     public JButton m_acceptButton;
/*  459:     */     public JPanel m_epochsLabel;
/*  460:     */     public JLabel m_totalEpochsLabel;
/*  461:     */     public JTextField m_changeEpochs;
/*  462:     */     public JLabel m_learningLabel;
/*  463:     */     public JLabel m_momentumLabel;
/*  464:     */     public JTextField m_changeLearning;
/*  465:     */     public JTextField m_changeMomentum;
/*  466:     */     public JPanel m_errorLabel;
/*  467:     */     
/*  468:     */     public ControlPanel()
/*  469:     */     {
/*  470: 750 */       setBorder(BorderFactory.createTitledBorder("Controls"));
/*  471:     */       
/*  472: 752 */       this.m_totalEpochsLabel = new JLabel("Num Of Epochs  ");
/*  473: 753 */       this.m_epochsLabel = new JPanel()
/*  474:     */       {
/*  475:     */         private static final long serialVersionUID = 2562773937093221399L;
/*  476:     */         
/*  477:     */         public void paintComponent(Graphics g)
/*  478:     */         {
/*  479: 759 */           super.paintComponent(g);
/*  480: 760 */           g.setColor(MultilayerPerceptron.this.m_controlPanel.m_totalEpochsLabel.getForeground());
/*  481: 761 */           g.drawString("Epoch  " + MultilayerPerceptron.this.m_epoch, 0, 10);
/*  482:     */         }
/*  483: 763 */       };
/*  484: 764 */       this.m_epochsLabel.setFont(this.m_totalEpochsLabel.getFont());
/*  485:     */       
/*  486: 766 */       this.m_changeEpochs = new JTextField();
/*  487: 767 */       this.m_changeEpochs.setText("" + MultilayerPerceptron.this.m_numEpochs);
/*  488: 768 */       this.m_errorLabel = new JPanel()
/*  489:     */       {
/*  490:     */         private static final long serialVersionUID = 4390239056336679189L;
/*  491:     */         
/*  492:     */         public void paintComponent(Graphics g)
/*  493:     */         {
/*  494: 774 */           super.paintComponent(g);
/*  495: 775 */           g.setColor(MultilayerPerceptron.this.m_controlPanel.m_totalEpochsLabel.getForeground());
/*  496: 776 */           if (MultilayerPerceptron.this.m_valSize == 0) {
/*  497: 777 */             g.drawString("Error per Epoch = " + Utils.doubleToString(MultilayerPerceptron.this.m_error, 7), 0, 10);
/*  498:     */           } else {
/*  499: 780 */             g.drawString("Validation Error per Epoch = " + Utils.doubleToString(MultilayerPerceptron.this.m_error, 7), 0, 10);
/*  500:     */           }
/*  501:     */         }
/*  502: 785 */       };
/*  503: 786 */       this.m_errorLabel.setFont(this.m_epochsLabel.getFont());
/*  504:     */       
/*  505: 788 */       this.m_learningLabel = new JLabel("Learning Rate = ");
/*  506: 789 */       this.m_momentumLabel = new JLabel("Momentum = ");
/*  507: 790 */       this.m_changeLearning = new JTextField();
/*  508: 791 */       this.m_changeMomentum = new JTextField();
/*  509: 792 */       this.m_changeLearning.setText("" + MultilayerPerceptron.this.m_learningRate);
/*  510: 793 */       this.m_changeMomentum.setText("" + MultilayerPerceptron.this.m_momentum);
/*  511: 794 */       setLayout(new BorderLayout(15, 10));
/*  512:     */       
/*  513: 796 */       MultilayerPerceptron.this.m_stopIt = true;
/*  514: 797 */       MultilayerPerceptron.this.m_accepted = false;
/*  515: 798 */       this.m_startStop = new JButton("Start");
/*  516: 799 */       this.m_startStop.setActionCommand("Start");
/*  517:     */       
/*  518: 801 */       this.m_acceptButton = new JButton("Accept");
/*  519: 802 */       this.m_acceptButton.setActionCommand("Accept");
/*  520:     */       
/*  521: 804 */       JPanel buttons = new JPanel();
/*  522: 805 */       buttons.setLayout(new BoxLayout(buttons, 1));
/*  523: 806 */       buttons.add(this.m_startStop);
/*  524: 807 */       buttons.add(this.m_acceptButton);
/*  525: 808 */       add(buttons, "West");
/*  526: 809 */       JPanel data = new JPanel();
/*  527: 810 */       data.setLayout(new BoxLayout(data, 1));
/*  528:     */       
/*  529: 812 */       Box ab = new Box(0);
/*  530: 813 */       ab.add(this.m_epochsLabel);
/*  531: 814 */       data.add(ab);
/*  532:     */       
/*  533: 816 */       ab = new Box(0);
/*  534: 817 */       Component b = Box.createGlue();
/*  535: 818 */       ab.add(this.m_totalEpochsLabel);
/*  536: 819 */       ab.add(this.m_changeEpochs);
/*  537: 820 */       this.m_changeEpochs.setMaximumSize(new Dimension(200, 20));
/*  538: 821 */       ab.add(b);
/*  539: 822 */       data.add(ab);
/*  540:     */       
/*  541: 824 */       ab = new Box(0);
/*  542: 825 */       ab.add(this.m_errorLabel);
/*  543: 826 */       data.add(ab);
/*  544:     */       
/*  545: 828 */       add(data, "Center");
/*  546:     */       
/*  547: 830 */       data = new JPanel();
/*  548: 831 */       data.setLayout(new BoxLayout(data, 1));
/*  549: 832 */       ab = new Box(0);
/*  550: 833 */       b = Box.createGlue();
/*  551: 834 */       ab.add(this.m_learningLabel);
/*  552: 835 */       ab.add(this.m_changeLearning);
/*  553: 836 */       this.m_changeLearning.setMaximumSize(new Dimension(200, 20));
/*  554: 837 */       ab.add(b);
/*  555: 838 */       data.add(ab);
/*  556:     */       
/*  557: 840 */       ab = new Box(0);
/*  558: 841 */       b = Box.createGlue();
/*  559: 842 */       ab.add(this.m_momentumLabel);
/*  560: 843 */       ab.add(this.m_changeMomentum);
/*  561: 844 */       this.m_changeMomentum.setMaximumSize(new Dimension(200, 20));
/*  562: 845 */       ab.add(b);
/*  563: 846 */       data.add(ab);
/*  564:     */       
/*  565: 848 */       add(data, "East");
/*  566:     */       
/*  567: 850 */       this.m_startStop.addActionListener(new ActionListener()
/*  568:     */       {
/*  569:     */         public void actionPerformed(ActionEvent e)
/*  570:     */         {
/*  571: 853 */           if (e.getActionCommand().equals("Start"))
/*  572:     */           {
/*  573: 854 */             MultilayerPerceptron.this.m_stopIt = false;
/*  574: 855 */             MultilayerPerceptron.ControlPanel.this.m_startStop.setText("Stop");
/*  575: 856 */             MultilayerPerceptron.ControlPanel.this.m_startStop.setActionCommand("Stop");
/*  576: 857 */             int n = Integer.valueOf(MultilayerPerceptron.ControlPanel.this.m_changeEpochs.getText()).intValue();
/*  577:     */             
/*  578: 859 */             MultilayerPerceptron.this.m_numEpochs = n;
/*  579: 860 */             MultilayerPerceptron.ControlPanel.this.m_changeEpochs.setText("" + MultilayerPerceptron.this.m_numEpochs);
/*  580:     */             
/*  581: 862 */             double m = Double.valueOf(MultilayerPerceptron.ControlPanel.this.m_changeLearning.getText()).doubleValue();
/*  582: 863 */             MultilayerPerceptron.this.setLearningRate(m);
/*  583: 864 */             MultilayerPerceptron.ControlPanel.this.m_changeLearning.setText("" + MultilayerPerceptron.this.m_learningRate);
/*  584:     */             
/*  585: 866 */             m = Double.valueOf(MultilayerPerceptron.ControlPanel.this.m_changeMomentum.getText()).doubleValue();
/*  586: 867 */             MultilayerPerceptron.this.setMomentum(m);
/*  587: 868 */             MultilayerPerceptron.ControlPanel.this.m_changeMomentum.setText("" + MultilayerPerceptron.this.m_momentum);
/*  588:     */             
/*  589: 870 */             MultilayerPerceptron.this.blocker(false);
/*  590:     */           }
/*  591: 871 */           else if (e.getActionCommand().equals("Stop"))
/*  592:     */           {
/*  593: 872 */             MultilayerPerceptron.this.m_stopIt = true;
/*  594: 873 */             MultilayerPerceptron.ControlPanel.this.m_startStop.setText("Start");
/*  595: 874 */             MultilayerPerceptron.ControlPanel.this.m_startStop.setActionCommand("Start");
/*  596:     */           }
/*  597:     */         }
/*  598: 878 */       });
/*  599: 879 */       this.m_acceptButton.addActionListener(new ActionListener()
/*  600:     */       {
/*  601:     */         public void actionPerformed(ActionEvent e)
/*  602:     */         {
/*  603: 882 */           MultilayerPerceptron.this.m_accepted = true;
/*  604: 883 */           MultilayerPerceptron.this.blocker(false);
/*  605:     */         }
/*  606: 886 */       });
/*  607: 887 */       this.m_changeEpochs.addActionListener(new ActionListener()
/*  608:     */       {
/*  609:     */         public void actionPerformed(ActionEvent e)
/*  610:     */         {
/*  611: 890 */           int n = Integer.valueOf(MultilayerPerceptron.ControlPanel.this.m_changeEpochs.getText()).intValue();
/*  612: 891 */           if (n > 0)
/*  613:     */           {
/*  614: 892 */             MultilayerPerceptron.this.m_numEpochs = n;
/*  615: 893 */             MultilayerPerceptron.this.blocker(false);
/*  616:     */           }
/*  617:     */         }
/*  618:     */       });
/*  619:     */     }
/*  620:     */     
/*  621:     */     public String getRevision()
/*  622:     */     {
/*  623: 906 */       return RevisionUtils.extract("$Revision: 12449 $");
/*  624:     */     }
/*  625:     */   }
/*  626:     */   
/*  627: 917 */   private boolean m_useDefaultModel = false;
/*  628:     */   private Instances m_instances;
/*  629:     */   private Instance m_currentInstance;
/*  630:     */   private boolean m_numeric;
/*  631:     */   private double[] m_attributeRanges;
/*  632:     */   private double[] m_attributeBases;
/*  633:     */   private NeuralEnd[] m_outputs;
/*  634:     */   private NeuralEnd[] m_inputs;
/*  635:     */   private NeuralConnection[] m_neuralNodes;
/*  636: 944 */   private int m_numClasses = 0;
/*  637: 947 */   private int m_numAttributes = 0;
/*  638:     */   private NodePanel m_nodePanel;
/*  639:     */   private ControlPanel m_controlPanel;
/*  640:     */   private int m_nextId;
/*  641:     */   private ArrayList<NeuralConnection> m_selected;
/*  642:     */   private int m_numEpochs;
/*  643:     */   private boolean m_stopIt;
/*  644:     */   private boolean m_stopped;
/*  645:     */   private boolean m_accepted;
/*  646:     */   private JFrame m_win;
/*  647:     */   private boolean m_autoBuild;
/*  648:     */   private boolean m_gui;
/*  649:     */   private int m_valSize;
/*  650:     */   private int m_driftThreshold;
/*  651:     */   private int m_randomSeed;
/*  652:     */   private Random m_random;
/*  653:     */   private boolean m_useNomToBin;
/*  654:     */   private NominalToBinary m_nominalToBinaryFilter;
/*  655:     */   private String m_hiddenLayers;
/*  656:     */   private boolean m_normalizeAttributes;
/*  657:     */   private boolean m_decay;
/*  658:     */   private double m_learningRate;
/*  659:     */   private double m_momentum;
/*  660:     */   private int m_epoch;
/*  661:     */   private double m_error;
/*  662:     */   private boolean m_reset;
/*  663:     */   private boolean m_normalizeClass;
/*  664:     */   private final SigmoidUnit m_sigmoidUnit;
/*  665:     */   private final LinearUnit m_linearUnit;
/*  666:     */   
/*  667:     */   public MultilayerPerceptron()
/*  668:     */   {
/*  669:1058 */     this.m_instances = null;
/*  670:1059 */     this.m_currentInstance = null;
/*  671:1060 */     this.m_controlPanel = null;
/*  672:1061 */     this.m_nodePanel = null;
/*  673:1062 */     this.m_epoch = 0;
/*  674:1063 */     this.m_error = 0.0D;
/*  675:     */     
/*  676:1065 */     this.m_outputs = new NeuralEnd[0];
/*  677:1066 */     this.m_inputs = new NeuralEnd[0];
/*  678:1067 */     this.m_numAttributes = 0;
/*  679:1068 */     this.m_numClasses = 0;
/*  680:1069 */     this.m_neuralNodes = new NeuralConnection[0];
/*  681:1070 */     this.m_selected = new ArrayList(4);
/*  682:1071 */     this.m_nextId = 0;
/*  683:1072 */     this.m_stopIt = true;
/*  684:1073 */     this.m_stopped = true;
/*  685:1074 */     this.m_accepted = false;
/*  686:1075 */     this.m_numeric = false;
/*  687:1076 */     this.m_random = null;
/*  688:1077 */     this.m_nominalToBinaryFilter = new NominalToBinary();
/*  689:1078 */     this.m_sigmoidUnit = new SigmoidUnit();
/*  690:1079 */     this.m_linearUnit = new LinearUnit();
/*  691:     */     
/*  692:     */ 
/*  693:     */ 
/*  694:     */ 
/*  695:1084 */     this.m_normalizeClass = true;
/*  696:1085 */     this.m_normalizeAttributes = true;
/*  697:1086 */     this.m_autoBuild = true;
/*  698:1087 */     this.m_gui = false;
/*  699:1088 */     this.m_useNomToBin = true;
/*  700:1089 */     this.m_driftThreshold = 20;
/*  701:1090 */     this.m_numEpochs = 500;
/*  702:1091 */     this.m_valSize = 0;
/*  703:1092 */     this.m_randomSeed = 0;
/*  704:1093 */     this.m_hiddenLayers = "a";
/*  705:1094 */     this.m_learningRate = 0.3D;
/*  706:1095 */     this.m_momentum = 0.2D;
/*  707:1096 */     this.m_reset = true;
/*  708:1097 */     this.m_decay = false;
/*  709:     */   }
/*  710:     */   
/*  711:     */   public void setDecay(boolean d)
/*  712:     */   {
/*  713:1104 */     this.m_decay = d;
/*  714:     */   }
/*  715:     */   
/*  716:     */   public boolean getDecay()
/*  717:     */   {
/*  718:1111 */     return this.m_decay;
/*  719:     */   }
/*  720:     */   
/*  721:     */   public void setReset(boolean r)
/*  722:     */   {
/*  723:1126 */     if (this.m_gui) {
/*  724:1127 */       r = false;
/*  725:     */     }
/*  726:1129 */     this.m_reset = r;
/*  727:     */   }
/*  728:     */   
/*  729:     */   public boolean getReset()
/*  730:     */   {
/*  731:1137 */     return this.m_reset;
/*  732:     */   }
/*  733:     */   
/*  734:     */   public void setNormalizeNumericClass(boolean c)
/*  735:     */   {
/*  736:1146 */     this.m_normalizeClass = c;
/*  737:     */   }
/*  738:     */   
/*  739:     */   public boolean getNormalizeNumericClass()
/*  740:     */   {
/*  741:1153 */     return this.m_normalizeClass;
/*  742:     */   }
/*  743:     */   
/*  744:     */   public void setNormalizeAttributes(boolean a)
/*  745:     */   {
/*  746:1161 */     this.m_normalizeAttributes = a;
/*  747:     */   }
/*  748:     */   
/*  749:     */   public boolean getNormalizeAttributes()
/*  750:     */   {
/*  751:1168 */     return this.m_normalizeAttributes;
/*  752:     */   }
/*  753:     */   
/*  754:     */   public void setNominalToBinaryFilter(boolean f)
/*  755:     */   {
/*  756:1175 */     this.m_useNomToBin = f;
/*  757:     */   }
/*  758:     */   
/*  759:     */   public boolean getNominalToBinaryFilter()
/*  760:     */   {
/*  761:1182 */     return this.m_useNomToBin;
/*  762:     */   }
/*  763:     */   
/*  764:     */   public void setSeed(int l)
/*  765:     */   {
/*  766:1193 */     if (l >= 0) {
/*  767:1194 */       this.m_randomSeed = l;
/*  768:     */     }
/*  769:     */   }
/*  770:     */   
/*  771:     */   public int getSeed()
/*  772:     */   {
/*  773:1203 */     return this.m_randomSeed;
/*  774:     */   }
/*  775:     */   
/*  776:     */   public void setValidationThreshold(int t)
/*  777:     */   {
/*  778:1214 */     if (t > 0) {
/*  779:1215 */       this.m_driftThreshold = t;
/*  780:     */     }
/*  781:     */   }
/*  782:     */   
/*  783:     */   public int getValidationThreshold()
/*  784:     */   {
/*  785:1223 */     return this.m_driftThreshold;
/*  786:     */   }
/*  787:     */   
/*  788:     */   public void setLearningRate(double l)
/*  789:     */   {
/*  790:1234 */     if ((l > 0.0D) && (l <= 1.0D))
/*  791:     */     {
/*  792:1235 */       this.m_learningRate = l;
/*  793:1237 */       if (this.m_controlPanel != null) {
/*  794:1238 */         this.m_controlPanel.m_changeLearning.setText("" + l);
/*  795:     */       }
/*  796:     */     }
/*  797:     */   }
/*  798:     */   
/*  799:     */   public double getLearningRate()
/*  800:     */   {
/*  801:1247 */     return this.m_learningRate;
/*  802:     */   }
/*  803:     */   
/*  804:     */   public void setMomentum(double m)
/*  805:     */   {
/*  806:1257 */     if ((m >= 0.0D) && (m <= 1.0D))
/*  807:     */     {
/*  808:1258 */       this.m_momentum = m;
/*  809:1260 */       if (this.m_controlPanel != null) {
/*  810:1261 */         this.m_controlPanel.m_changeMomentum.setText("" + m);
/*  811:     */       }
/*  812:     */     }
/*  813:     */   }
/*  814:     */   
/*  815:     */   public double getMomentum()
/*  816:     */   {
/*  817:1270 */     return this.m_momentum;
/*  818:     */   }
/*  819:     */   
/*  820:     */   public void setAutoBuild(boolean a)
/*  821:     */   {
/*  822:1281 */     if (!this.m_gui) {
/*  823:1282 */       a = true;
/*  824:     */     }
/*  825:1284 */     this.m_autoBuild = a;
/*  826:     */   }
/*  827:     */   
/*  828:     */   public boolean getAutoBuild()
/*  829:     */   {
/*  830:1291 */     return this.m_autoBuild;
/*  831:     */   }
/*  832:     */   
/*  833:     */   public void setHiddenLayers(String h)
/*  834:     */   {
/*  835:1307 */     String tmp = "";
/*  836:1308 */     StringTokenizer tok = new StringTokenizer(h, ",");
/*  837:1309 */     if (tok.countTokens() == 0) {
/*  838:1310 */       return;
/*  839:     */     }
/*  840:1315 */     boolean first = true;
/*  841:1316 */     while (tok.hasMoreTokens())
/*  842:     */     {
/*  843:1317 */       String c = tok.nextToken().trim();
/*  844:1319 */       if ((c.equals("a")) || (c.equals("i")) || (c.equals("o")) || (c.equals("t")))
/*  845:     */       {
/*  846:1320 */         tmp = tmp + c;
/*  847:     */       }
/*  848:     */       else
/*  849:     */       {
/*  850:1322 */         double dval = Double.valueOf(c).doubleValue();
/*  851:1323 */         int val = (int)dval;
/*  852:1325 */         if ((val == dval) && ((val != 0) || ((tok.countTokens() == 0) && (first))) && (val >= 0)) {
/*  853:1326 */           tmp = tmp + val;
/*  854:     */         } else {
/*  855:1328 */           return;
/*  856:     */         }
/*  857:     */       }
/*  858:1332 */       first = false;
/*  859:1333 */       if (tok.hasMoreTokens()) {
/*  860:1334 */         tmp = tmp + ", ";
/*  861:     */       }
/*  862:     */     }
/*  863:1337 */     this.m_hiddenLayers = tmp;
/*  864:     */   }
/*  865:     */   
/*  866:     */   public String getHiddenLayers()
/*  867:     */   {
/*  868:1345 */     return this.m_hiddenLayers;
/*  869:     */   }
/*  870:     */   
/*  871:     */   public void setGUI(boolean a)
/*  872:     */   {
/*  873:1355 */     this.m_gui = a;
/*  874:1356 */     if (!a) {
/*  875:1357 */       setAutoBuild(true);
/*  876:     */     } else {
/*  877:1360 */       setReset(false);
/*  878:     */     }
/*  879:     */   }
/*  880:     */   
/*  881:     */   public boolean getGUI()
/*  882:     */   {
/*  883:1368 */     return this.m_gui;
/*  884:     */   }
/*  885:     */   
/*  886:     */   public void setValidationSetSize(int a)
/*  887:     */   {
/*  888:1377 */     if ((a < 0) || (a > 99)) {
/*  889:1378 */       return;
/*  890:     */     }
/*  891:1380 */     this.m_valSize = a;
/*  892:     */   }
/*  893:     */   
/*  894:     */   public int getValidationSetSize()
/*  895:     */   {
/*  896:1387 */     return this.m_valSize;
/*  897:     */   }
/*  898:     */   
/*  899:     */   public void setTrainingTime(int n)
/*  900:     */   {
/*  901:1396 */     if (n > 0) {
/*  902:1397 */       this.m_numEpochs = n;
/*  903:     */     }
/*  904:     */   }
/*  905:     */   
/*  906:     */   public int getTrainingTime()
/*  907:     */   {
/*  908:1405 */     return this.m_numEpochs;
/*  909:     */   }
/*  910:     */   
/*  911:     */   private void addNode(NeuralConnection n)
/*  912:     */   {
/*  913:1415 */     NeuralConnection[] temp1 = new NeuralConnection[this.m_neuralNodes.length + 1];
/*  914:1416 */     for (int noa = 0; noa < this.m_neuralNodes.length; noa++) {
/*  915:1417 */       temp1[noa] = this.m_neuralNodes[noa];
/*  916:     */     }
/*  917:1420 */     temp1[(temp1.length - 1)] = n;
/*  918:1421 */     this.m_neuralNodes = temp1;
/*  919:     */   }
/*  920:     */   
/*  921:     */   private boolean removeNode(NeuralConnection n)
/*  922:     */   {
/*  923:1432 */     NeuralConnection[] temp1 = new NeuralConnection[this.m_neuralNodes.length - 1];
/*  924:1433 */     int skip = 0;
/*  925:1434 */     for (int noa = 0; noa < this.m_neuralNodes.length; noa++) {
/*  926:1435 */       if (n == this.m_neuralNodes[noa]) {
/*  927:1436 */         skip++;
/*  928:1437 */       } else if (noa - skip < temp1.length) {
/*  929:1438 */         temp1[(noa - skip)] = this.m_neuralNodes[noa];
/*  930:     */       } else {
/*  931:1440 */         return false;
/*  932:     */       }
/*  933:     */     }
/*  934:1443 */     this.m_neuralNodes = temp1;
/*  935:1444 */     return true;
/*  936:     */   }
/*  937:     */   
/*  938:     */   private Instances setClassType(Instances inst)
/*  939:     */     throws Exception
/*  940:     */   {
/*  941:1460 */     if (inst != null)
/*  942:     */     {
/*  943:1462 */       this.m_attributeRanges = new double[inst.numAttributes()];
/*  944:1463 */       this.m_attributeBases = new double[inst.numAttributes()];
/*  945:1464 */       for (int noa = 0; noa < inst.numAttributes(); noa++)
/*  946:     */       {
/*  947:1465 */         double min = (1.0D / 0.0D);
/*  948:1466 */         double max = (-1.0D / 0.0D);
/*  949:1467 */         for (int i = 0; i < inst.numInstances(); i++) {
/*  950:1468 */           if (!inst.instance(i).isMissing(noa))
/*  951:     */           {
/*  952:1469 */             double value = inst.instance(i).value(noa);
/*  953:1470 */             if (value < min) {
/*  954:1471 */               min = value;
/*  955:     */             }
/*  956:1473 */             if (value > max) {
/*  957:1474 */               max = value;
/*  958:     */             }
/*  959:     */           }
/*  960:     */         }
/*  961:1478 */         this.m_attributeRanges[noa] = ((max - min) / 2.0D);
/*  962:1479 */         this.m_attributeBases[noa] = ((max + min) / 2.0D);
/*  963:     */       }
/*  964:1482 */       if (this.m_normalizeAttributes) {
/*  965:1483 */         for (int i = 0; i < inst.numInstances(); i++)
/*  966:     */         {
/*  967:1484 */           Instance currentInstance = inst.instance(i);
/*  968:1485 */           double[] instance = new double[inst.numAttributes()];
/*  969:1486 */           for (int noa = 0; noa < inst.numAttributes(); noa++) {
/*  970:1487 */             if (noa != inst.classIndex())
/*  971:     */             {
/*  972:1488 */               if (this.m_attributeRanges[noa] != 0.0D) {
/*  973:1489 */                 instance[noa] = ((currentInstance.value(noa) - this.m_attributeBases[noa]) / this.m_attributeRanges[noa]);
/*  974:     */               } else {
/*  975:1491 */                 instance[noa] = (currentInstance.value(noa) - this.m_attributeBases[noa]);
/*  976:     */               }
/*  977:     */             }
/*  978:     */             else {
/*  979:1494 */               instance[noa] = currentInstance.value(noa);
/*  980:     */             }
/*  981:     */           }
/*  982:1497 */           inst.set(i, new DenseInstance(currentInstance.weight(), instance));
/*  983:     */         }
/*  984:     */       }
/*  985:1501 */       if (inst.classAttribute().isNumeric()) {
/*  986:1502 */         this.m_numeric = true;
/*  987:     */       } else {
/*  988:1504 */         this.m_numeric = false;
/*  989:     */       }
/*  990:     */     }
/*  991:1507 */     return inst;
/*  992:     */   }
/*  993:     */   
/*  994:     */   public synchronized void blocker(boolean tf)
/*  995:     */   {
/*  996:1518 */     if (tf) {
/*  997:     */       try
/*  998:     */       {
/*  999:1520 */         wait();
/* 1000:     */       }
/* 1001:     */       catch (InterruptedException e) {}
/* 1002:     */     } else {
/* 1003:1524 */       notifyAll();
/* 1004:     */     }
/* 1005:     */   }
/* 1006:     */   
/* 1007:     */   private void updateDisplay()
/* 1008:     */   {
/* 1009:1533 */     if (this.m_gui)
/* 1010:     */     {
/* 1011:1534 */       this.m_controlPanel.m_errorLabel.repaint();
/* 1012:1535 */       this.m_controlPanel.m_epochsLabel.repaint();
/* 1013:     */     }
/* 1014:     */   }
/* 1015:     */   
/* 1016:     */   private void resetNetwork()
/* 1017:     */   {
/* 1018:1543 */     for (int noc = 0; noc < this.m_numClasses; noc++) {
/* 1019:1544 */       this.m_outputs[noc].reset();
/* 1020:     */     }
/* 1021:     */   }
/* 1022:     */   
/* 1023:     */   private void calculateOutputs()
/* 1024:     */   {
/* 1025:1553 */     for (int noc = 0; noc < this.m_numClasses; noc++) {
/* 1026:1555 */       this.m_outputs[noc].outputValue(true);
/* 1027:     */     }
/* 1028:     */   }
/* 1029:     */   
/* 1030:     */   private double calculateErrors()
/* 1031:     */     throws Exception
/* 1032:     */   {
/* 1033:1567 */     double ret = 0.0D;double temp = 0.0D;
/* 1034:1568 */     for (int noc = 0; noc < this.m_numAttributes; noc++) {
/* 1035:1570 */       this.m_inputs[noc].errorValue(true);
/* 1036:     */     }
/* 1037:1573 */     for (int noc = 0; noc < this.m_numClasses; noc++)
/* 1038:     */     {
/* 1039:1574 */       temp = this.m_outputs[noc].errorValue(false);
/* 1040:1575 */       ret += temp * temp;
/* 1041:     */     }
/* 1042:1577 */     return ret;
/* 1043:     */   }
/* 1044:     */   
/* 1045:     */   private void updateNetworkWeights(double l, double m)
/* 1046:     */   {
/* 1047:1589 */     for (int noc = 0; noc < this.m_numClasses; noc++) {
/* 1048:1591 */       this.m_outputs[noc].updateWeights(l, m);
/* 1049:     */     }
/* 1050:     */   }
/* 1051:     */   
/* 1052:     */   private void setupInputs()
/* 1053:     */     throws Exception
/* 1054:     */   {
/* 1055:1600 */     this.m_inputs = new NeuralEnd[this.m_numAttributes];
/* 1056:1601 */     int now = 0;
/* 1057:1602 */     for (int noa = 0; noa < this.m_numAttributes + 1; noa++) {
/* 1058:1603 */       if (this.m_instances.classIndex() != noa)
/* 1059:     */       {
/* 1060:1604 */         this.m_inputs[(noa - now)] = new NeuralEnd(this.m_instances.attribute(noa).name());
/* 1061:     */         
/* 1062:1606 */         this.m_inputs[(noa - now)].setX(0.1D);
/* 1063:1607 */         this.m_inputs[(noa - now)].setY((noa - now + 1.0D) / (this.m_numAttributes + 1));
/* 1064:1608 */         this.m_inputs[(noa - now)].setLink(true, noa);
/* 1065:     */       }
/* 1066:     */       else
/* 1067:     */       {
/* 1068:1610 */         now = 1;
/* 1069:     */       }
/* 1070:     */     }
/* 1071:     */   }
/* 1072:     */   
/* 1073:     */   private void setupOutputs()
/* 1074:     */     throws Exception
/* 1075:     */   {
/* 1076:1621 */     this.m_outputs = new NeuralEnd[this.m_numClasses];
/* 1077:1622 */     for (int noa = 0; noa < this.m_numClasses; noa++)
/* 1078:     */     {
/* 1079:1623 */       if (this.m_numeric) {
/* 1080:1624 */         this.m_outputs[noa] = new NeuralEnd(this.m_instances.classAttribute().name());
/* 1081:     */       } else {
/* 1082:1626 */         this.m_outputs[noa] = new NeuralEnd(this.m_instances.classAttribute().value(noa));
/* 1083:     */       }
/* 1084:1629 */       this.m_outputs[noa].setX(0.9D);
/* 1085:1630 */       this.m_outputs[noa].setY((noa + 1.0D) / (this.m_numClasses + 1));
/* 1086:1631 */       this.m_outputs[noa].setLink(false, noa);
/* 1087:1632 */       NeuralNode temp = new NeuralNode(String.valueOf(this.m_nextId), this.m_random, this.m_sigmoidUnit);
/* 1088:     */       
/* 1089:1634 */       this.m_nextId += 1;
/* 1090:1635 */       temp.setX(0.75D);
/* 1091:1636 */       temp.setY((noa + 1.0D) / (this.m_numClasses + 1));
/* 1092:1637 */       addNode(temp);
/* 1093:1638 */       NeuralConnection.connect(temp, this.m_outputs[noa]);
/* 1094:     */     }
/* 1095:     */   }
/* 1096:     */   
/* 1097:     */   private void setupHiddenLayer()
/* 1098:     */   {
/* 1099:1647 */     StringTokenizer tok = new StringTokenizer(this.m_hiddenLayers, ",");
/* 1100:1648 */     int val = 0;
/* 1101:1649 */     int prev = 0;
/* 1102:1650 */     int num = tok.countTokens();
/* 1103:1652 */     for (int noa = 0; noa < num; noa++)
/* 1104:     */     {
/* 1105:1656 */       String c = tok.nextToken().trim();
/* 1106:1657 */       if (c.equals("a")) {
/* 1107:1658 */         val = (this.m_numAttributes + this.m_numClasses) / 2;
/* 1108:1659 */       } else if (c.equals("i")) {
/* 1109:1660 */         val = this.m_numAttributes;
/* 1110:1661 */       } else if (c.equals("o")) {
/* 1111:1662 */         val = this.m_numClasses;
/* 1112:1663 */       } else if (c.equals("t")) {
/* 1113:1664 */         val = this.m_numAttributes + this.m_numClasses;
/* 1114:     */       } else {
/* 1115:1666 */         val = Double.valueOf(c).intValue();
/* 1116:     */       }
/* 1117:1668 */       for (int nob = 0; nob < val; nob++)
/* 1118:     */       {
/* 1119:1669 */         NeuralNode temp = new NeuralNode(String.valueOf(this.m_nextId), this.m_random, this.m_sigmoidUnit);
/* 1120:     */         
/* 1121:1671 */         this.m_nextId += 1;
/* 1122:1672 */         temp.setX(0.5D / num * noa + 0.25D);
/* 1123:1673 */         temp.setY((nob + 1.0D) / (val + 1));
/* 1124:1674 */         addNode(temp);
/* 1125:1675 */         if (noa > 0) {
/* 1126:1677 */           for (int noc = this.m_neuralNodes.length - nob - 1 - prev; noc < this.m_neuralNodes.length - nob - 1; noc++) {
/* 1127:1679 */             NeuralConnection.connect(this.m_neuralNodes[noc], temp);
/* 1128:     */           }
/* 1129:     */         }
/* 1130:     */       }
/* 1131:1683 */       prev = val;
/* 1132:     */     }
/* 1133:1685 */     tok = new StringTokenizer(this.m_hiddenLayers, ",");
/* 1134:1686 */     String c = tok.nextToken();
/* 1135:1687 */     if (c.equals("a")) {
/* 1136:1688 */       val = (this.m_numAttributes + this.m_numClasses) / 2;
/* 1137:1689 */     } else if (c.equals("i")) {
/* 1138:1690 */       val = this.m_numAttributes;
/* 1139:1691 */     } else if (c.equals("o")) {
/* 1140:1692 */       val = this.m_numClasses;
/* 1141:1693 */     } else if (c.equals("t")) {
/* 1142:1694 */       val = this.m_numAttributes + this.m_numClasses;
/* 1143:     */     } else {
/* 1144:1696 */       val = Double.valueOf(c).intValue();
/* 1145:     */     }
/* 1146:1699 */     if (val == 0)
/* 1147:     */     {
/* 1148:1700 */       for (int noa = 0; noa < this.m_numAttributes; noa++) {
/* 1149:1701 */         for (int nob = 0; nob < this.m_numClasses; nob++) {
/* 1150:1702 */           NeuralConnection.connect(this.m_inputs[noa], this.m_neuralNodes[nob]);
/* 1151:     */         }
/* 1152:     */       }
/* 1153:     */     }
/* 1154:     */     else
/* 1155:     */     {
/* 1156:1706 */       for (int noa = 0; noa < this.m_numAttributes; noa++) {
/* 1157:1707 */         for (int nob = this.m_numClasses; nob < this.m_numClasses + val; nob++) {
/* 1158:1708 */           NeuralConnection.connect(this.m_inputs[noa], this.m_neuralNodes[nob]);
/* 1159:     */         }
/* 1160:     */       }
/* 1161:1711 */       for (int noa = this.m_neuralNodes.length - prev; noa < this.m_neuralNodes.length; noa++) {
/* 1162:1712 */         for (int nob = 0; nob < this.m_numClasses; nob++) {
/* 1163:1713 */           NeuralConnection.connect(this.m_neuralNodes[noa], this.m_neuralNodes[nob]);
/* 1164:     */         }
/* 1165:     */       }
/* 1166:     */     }
/* 1167:     */   }
/* 1168:     */   
/* 1169:     */   private void setEndsToLinear()
/* 1170:     */   {
/* 1171:1726 */     for (NeuralConnection m_neuralNode : this.m_neuralNodes) {
/* 1172:1727 */       if ((m_neuralNode.getType() & 0x8) == 8) {
/* 1173:1728 */         ((NeuralNode)m_neuralNode).setMethod(this.m_linearUnit);
/* 1174:     */       } else {
/* 1175:1730 */         ((NeuralNode)m_neuralNode).setMethod(this.m_sigmoidUnit);
/* 1176:     */       }
/* 1177:     */     }
/* 1178:     */   }
/* 1179:     */   
/* 1180:     */   public Capabilities getCapabilities()
/* 1181:     */   {
/* 1182:1742 */     Capabilities result = super.getCapabilities();
/* 1183:1743 */     result.disableAll();
/* 1184:     */     
/* 1185:     */ 
/* 1186:1746 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 1187:1747 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 1188:1748 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 1189:1749 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 1190:     */     
/* 1191:     */ 
/* 1192:1752 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 1193:1753 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/* 1194:1754 */     result.enable(Capabilities.Capability.DATE_CLASS);
/* 1195:1755 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 1196:     */     
/* 1197:1757 */     return result;
/* 1198:     */   }
/* 1199:     */   
/* 1200:     */   public void buildClassifier(Instances i)
/* 1201:     */     throws Exception
/* 1202:     */   {
/* 1203:1771 */     getCapabilities().testWithFail(i);
/* 1204:     */     
/* 1205:     */ 
/* 1206:1774 */     i = new Instances(i);
/* 1207:1775 */     i.deleteWithMissingClass();
/* 1208:     */     
/* 1209:1777 */     this.m_ZeroR = new ZeroR();
/* 1210:1778 */     this.m_ZeroR.buildClassifier(i);
/* 1211:1780 */     if (i.numAttributes() == 1)
/* 1212:     */     {
/* 1213:1781 */       System.err.println("Cannot build model (only class attribute present in data!), using ZeroR model instead!");
/* 1214:     */       
/* 1215:     */ 
/* 1216:1784 */       this.m_useDefaultModel = true;
/* 1217:1785 */       return;
/* 1218:     */     }
/* 1219:1787 */     this.m_useDefaultModel = false;
/* 1220:     */     
/* 1221:     */ 
/* 1222:1790 */     this.m_epoch = 0;
/* 1223:1791 */     this.m_error = 0.0D;
/* 1224:1792 */     this.m_instances = null;
/* 1225:1793 */     this.m_currentInstance = null;
/* 1226:1794 */     this.m_controlPanel = null;
/* 1227:1795 */     this.m_nodePanel = null;
/* 1228:     */     
/* 1229:1797 */     this.m_outputs = new NeuralEnd[0];
/* 1230:1798 */     this.m_inputs = new NeuralEnd[0];
/* 1231:1799 */     this.m_numAttributes = 0;
/* 1232:1800 */     this.m_numClasses = 0;
/* 1233:1801 */     this.m_neuralNodes = new NeuralConnection[0];
/* 1234:     */     
/* 1235:1803 */     this.m_selected = new ArrayList(4);
/* 1236:1804 */     this.m_nextId = 0;
/* 1237:1805 */     this.m_stopIt = true;
/* 1238:1806 */     this.m_stopped = true;
/* 1239:1807 */     this.m_accepted = false;
/* 1240:1808 */     this.m_instances = new Instances(i);
/* 1241:1809 */     this.m_random = new Random(this.m_randomSeed);
/* 1242:1810 */     this.m_instances.randomize(this.m_random);
/* 1243:1812 */     if (this.m_useNomToBin)
/* 1244:     */     {
/* 1245:1813 */       this.m_nominalToBinaryFilter = new NominalToBinary();
/* 1246:1814 */       this.m_nominalToBinaryFilter.setInputFormat(this.m_instances);
/* 1247:1815 */       this.m_instances = Filter.useFilter(this.m_instances, this.m_nominalToBinaryFilter);
/* 1248:     */     }
/* 1249:1817 */     this.m_numAttributes = (this.m_instances.numAttributes() - 1);
/* 1250:1818 */     this.m_numClasses = this.m_instances.numClasses();
/* 1251:     */     
/* 1252:1820 */     setClassType(this.m_instances);
/* 1253:     */     
/* 1254:     */ 
/* 1255:1823 */     Instances valSet = null;
/* 1256:     */     
/* 1257:1825 */     int numInVal = (int)(this.m_valSize / 100.0D * this.m_instances.numInstances());
/* 1258:1826 */     if (this.m_valSize > 0)
/* 1259:     */     {
/* 1260:1827 */       if (numInVal == 0) {
/* 1261:1828 */         numInVal = 1;
/* 1262:     */       }
/* 1263:1830 */       valSet = new Instances(this.m_instances, 0, numInVal);
/* 1264:     */     }
/* 1265:1834 */     setupInputs();
/* 1266:     */     
/* 1267:1836 */     setupOutputs();
/* 1268:1837 */     if (this.m_autoBuild) {
/* 1269:1838 */       setupHiddenLayer();
/* 1270:     */     }
/* 1271:1843 */     if (this.m_gui)
/* 1272:     */     {
/* 1273:1844 */       this.m_win = new JFrame();
/* 1274:     */       
/* 1275:1846 */       this.m_win.addWindowListener(new WindowAdapter()
/* 1276:     */       {
/* 1277:     */         public void windowClosing(WindowEvent e)
/* 1278:     */         {
/* 1279:1849 */           boolean k = MultilayerPerceptron.this.m_stopIt;
/* 1280:1850 */           MultilayerPerceptron.this.m_stopIt = true;
/* 1281:1851 */           int well = JOptionPane.showConfirmDialog(MultilayerPerceptron.this.m_win, "Are You Sure...\nClick Yes To Accept The Neural Network\n Click No To Return", "Accept Neural Network", 0);
/* 1282:1856 */           if (well == 0)
/* 1283:     */           {
/* 1284:1857 */             MultilayerPerceptron.this.m_win.setDefaultCloseOperation(2);
/* 1285:1858 */             MultilayerPerceptron.this.m_accepted = true;
/* 1286:1859 */             MultilayerPerceptron.this.blocker(false);
/* 1287:     */           }
/* 1288:     */           else
/* 1289:     */           {
/* 1290:1861 */             MultilayerPerceptron.this.m_win.setDefaultCloseOperation(0);
/* 1291:     */           }
/* 1292:1863 */           MultilayerPerceptron.this.m_stopIt = k;
/* 1293:     */         }
/* 1294:1866 */       });
/* 1295:1867 */       this.m_win.getContentPane().setLayout(new BorderLayout());
/* 1296:1868 */       this.m_win.setTitle("Neural Network");
/* 1297:1869 */       this.m_nodePanel = new NodePanel();
/* 1298:     */       
/* 1299:     */ 
/* 1300:     */ 
/* 1301:     */ 
/* 1302:     */ 
/* 1303:     */ 
/* 1304:1876 */       this.m_nodePanel.setPreferredSize(new Dimension(640, 480));
/* 1305:1877 */       this.m_nodePanel.revalidate();
/* 1306:     */       
/* 1307:1879 */       JScrollPane sp = new JScrollPane(this.m_nodePanel, 22, 31);
/* 1308:     */       
/* 1309:     */ 
/* 1310:1882 */       this.m_controlPanel = new ControlPanel();
/* 1311:     */       
/* 1312:1884 */       this.m_win.getContentPane().add(sp, "Center");
/* 1313:1885 */       this.m_win.getContentPane().add(this.m_controlPanel, "South");
/* 1314:1886 */       this.m_win.setSize(640, 480);
/* 1315:1887 */       this.m_win.setVisible(true);
/* 1316:     */     }
/* 1317:1891 */     if (this.m_gui)
/* 1318:     */     {
/* 1319:1892 */       blocker(true);
/* 1320:1893 */       this.m_controlPanel.m_changeEpochs.setEnabled(false);
/* 1321:1894 */       this.m_controlPanel.m_changeLearning.setEnabled(false);
/* 1322:1895 */       this.m_controlPanel.m_changeMomentum.setEnabled(false);
/* 1323:     */     }
/* 1324:1900 */     if (this.m_numeric) {
/* 1325:1901 */       setEndsToLinear();
/* 1326:     */     }
/* 1327:1903 */     if (this.m_accepted)
/* 1328:     */     {
/* 1329:1904 */       this.m_win.dispose();
/* 1330:1905 */       this.m_controlPanel = null;
/* 1331:1906 */       this.m_nodePanel = null;
/* 1332:1907 */       this.m_instances = new Instances(this.m_instances, 0);
/* 1333:1908 */       this.m_currentInstance = null;
/* 1334:1909 */       return;
/* 1335:     */     }
/* 1336:1913 */     double right = 0.0D;
/* 1337:1914 */     double driftOff = 0.0D;
/* 1338:1915 */     double lastRight = (1.0D / 0.0D);
/* 1339:1916 */     double bestError = (1.0D / 0.0D);
/* 1340:     */     
/* 1341:1918 */     double totalWeight = 0.0D;
/* 1342:1919 */     double totalValWeight = 0.0D;
/* 1343:1920 */     double origRate = this.m_learningRate;
/* 1344:1923 */     if (numInVal == this.m_instances.numInstances()) {
/* 1345:1924 */       numInVal--;
/* 1346:     */     }
/* 1347:1926 */     if (numInVal < 0) {
/* 1348:1927 */       numInVal = 0;
/* 1349:     */     }
/* 1350:1929 */     for (int noa = numInVal; noa < this.m_instances.numInstances(); noa++) {
/* 1351:1930 */       if (!this.m_instances.instance(noa).classIsMissing()) {
/* 1352:1931 */         totalWeight += this.m_instances.instance(noa).weight();
/* 1353:     */       }
/* 1354:     */     }
/* 1355:1934 */     if (this.m_valSize != 0) {
/* 1356:1935 */       for (int noa = 0; noa < valSet.numInstances(); noa++) {
/* 1357:1936 */         if (!valSet.instance(noa).classIsMissing()) {
/* 1358:1937 */           totalValWeight += valSet.instance(noa).weight();
/* 1359:     */         }
/* 1360:     */       }
/* 1361:     */     }
/* 1362:1941 */     this.m_stopped = false;
/* 1363:1943 */     for (int noa = 1; noa < this.m_numEpochs + 1; noa++)
/* 1364:     */     {
/* 1365:1944 */       right = 0.0D;
/* 1366:1945 */       for (int nob = numInVal; nob < this.m_instances.numInstances(); nob++)
/* 1367:     */       {
/* 1368:1946 */         this.m_currentInstance = this.m_instances.instance(nob);
/* 1369:1948 */         if (!this.m_currentInstance.classIsMissing())
/* 1370:     */         {
/* 1371:1952 */           resetNetwork();
/* 1372:1953 */           calculateOutputs();
/* 1373:1954 */           double tempRate = this.m_learningRate * this.m_currentInstance.weight();
/* 1374:1955 */           if (this.m_decay) {
/* 1375:1956 */             tempRate /= noa;
/* 1376:     */           }
/* 1377:1959 */           right += calculateErrors() / this.m_instances.numClasses() * this.m_currentInstance.weight();
/* 1378:     */           
/* 1379:1961 */           updateNetworkWeights(tempRate, this.m_momentum);
/* 1380:     */         }
/* 1381:     */       }
/* 1382:1966 */       right /= totalWeight;
/* 1383:1967 */       if ((Double.isInfinite(right)) || (Double.isNaN(right)))
/* 1384:     */       {
/* 1385:1968 */         if (!this.m_reset)
/* 1386:     */         {
/* 1387:1969 */           this.m_instances = null;
/* 1388:1970 */           throw new Exception("Network cannot train. Try restarting with a smaller learning rate.");
/* 1389:     */         }
/* 1390:1974 */         if (this.m_learningRate <= Utils.SMALL) {
/* 1391:1975 */           throw new IllegalStateException("Learning rate got too small (" + this.m_learningRate + " <= " + Utils.SMALL + ")!");
/* 1392:     */         }
/* 1393:1978 */         this.m_learningRate /= 2.0D;
/* 1394:1979 */         buildClassifier(i);
/* 1395:1980 */         this.m_learningRate = origRate;
/* 1396:1981 */         this.m_instances = new Instances(this.m_instances, 0);
/* 1397:1982 */         this.m_currentInstance = null;
/* 1398:1983 */         return;
/* 1399:     */       }
/* 1400:1988 */       if (this.m_valSize != 0)
/* 1401:     */       {
/* 1402:1989 */         right = 0.0D;
/* 1403:1990 */         for (int nob = 0; nob < valSet.numInstances(); nob++)
/* 1404:     */         {
/* 1405:1991 */           this.m_currentInstance = valSet.instance(nob);
/* 1406:1992 */           if (!this.m_currentInstance.classIsMissing())
/* 1407:     */           {
/* 1408:1994 */             resetNetwork();
/* 1409:1995 */             calculateOutputs();
/* 1410:1996 */             right += calculateErrors() / valSet.numClasses() * this.m_currentInstance.weight();
/* 1411:     */           }
/* 1412:     */         }
/* 1413:2005 */         if (right < lastRight)
/* 1414:     */         {
/* 1415:2006 */           if (right < bestError)
/* 1416:     */           {
/* 1417:2007 */             bestError = right;
/* 1418:2009 */             for (int noc = 0; noc < this.m_numClasses; noc++) {
/* 1419:2010 */               this.m_outputs[noc].saveWeights();
/* 1420:     */             }
/* 1421:2012 */             driftOff = 0.0D;
/* 1422:     */           }
/* 1423:     */         }
/* 1424:     */         else {
/* 1425:2015 */           driftOff += 1.0D;
/* 1426:     */         }
/* 1427:2017 */         lastRight = right;
/* 1428:2018 */         if ((driftOff > this.m_driftThreshold) || (noa + 1 >= this.m_numEpochs))
/* 1429:     */         {
/* 1430:2019 */           for (int noc = 0; noc < this.m_numClasses; noc++) {
/* 1431:2020 */             this.m_outputs[noc].restoreWeights();
/* 1432:     */           }
/* 1433:2022 */           this.m_accepted = true;
/* 1434:     */         }
/* 1435:2024 */         right /= totalValWeight;
/* 1436:     */       }
/* 1437:2026 */       this.m_epoch = noa;
/* 1438:2027 */       this.m_error = right;
/* 1439:     */       
/* 1440:2029 */       updateDisplay();
/* 1441:2032 */       if (this.m_gui)
/* 1442:     */       {
/* 1443:2034 */         while (((this.m_stopIt) || ((this.m_epoch >= this.m_numEpochs) && (this.m_valSize == 0))) && (!this.m_accepted))
/* 1444:     */         {
/* 1445:2035 */           this.m_stopIt = true;
/* 1446:2036 */           this.m_stopped = true;
/* 1447:2037 */           if ((this.m_epoch >= this.m_numEpochs) && (this.m_valSize == 0)) {
/* 1448:2039 */             this.m_controlPanel.m_startStop.setEnabled(false);
/* 1449:     */           } else {
/* 1450:2041 */             this.m_controlPanel.m_startStop.setEnabled(true);
/* 1451:     */           }
/* 1452:2043 */           this.m_controlPanel.m_startStop.setText("Start");
/* 1453:2044 */           this.m_controlPanel.m_startStop.setActionCommand("Start");
/* 1454:2045 */           this.m_controlPanel.m_changeEpochs.setEnabled(true);
/* 1455:2046 */           this.m_controlPanel.m_changeLearning.setEnabled(true);
/* 1456:2047 */           this.m_controlPanel.m_changeMomentum.setEnabled(true);
/* 1457:     */           
/* 1458:2049 */           blocker(true);
/* 1459:2050 */           if (this.m_numeric) {
/* 1460:2051 */             setEndsToLinear();
/* 1461:     */           }
/* 1462:     */         }
/* 1463:2054 */         this.m_controlPanel.m_changeEpochs.setEnabled(false);
/* 1464:2055 */         this.m_controlPanel.m_changeLearning.setEnabled(false);
/* 1465:2056 */         this.m_controlPanel.m_changeMomentum.setEnabled(false);
/* 1466:     */         
/* 1467:2058 */         this.m_stopped = false;
/* 1468:2060 */         if (this.m_accepted)
/* 1469:     */         {
/* 1470:2061 */           this.m_win.dispose();
/* 1471:2062 */           this.m_controlPanel = null;
/* 1472:2063 */           this.m_nodePanel = null;
/* 1473:2064 */           this.m_instances = new Instances(this.m_instances, 0);
/* 1474:2065 */           this.m_currentInstance = null;
/* 1475:2066 */           return;
/* 1476:     */         }
/* 1477:     */       }
/* 1478:2069 */       if (this.m_accepted)
/* 1479:     */       {
/* 1480:2070 */         this.m_instances = new Instances(this.m_instances, 0);
/* 1481:2071 */         this.m_currentInstance = null;
/* 1482:2072 */         return;
/* 1483:     */       }
/* 1484:     */     }
/* 1485:2075 */     if (this.m_gui)
/* 1486:     */     {
/* 1487:2076 */       this.m_win.dispose();
/* 1488:2077 */       this.m_controlPanel = null;
/* 1489:2078 */       this.m_nodePanel = null;
/* 1490:     */     }
/* 1491:2080 */     this.m_instances = new Instances(this.m_instances, 0);
/* 1492:2081 */     this.m_currentInstance = null;
/* 1493:     */   }
/* 1494:     */   
/* 1495:     */   public double[] distributionForInstance(Instance i)
/* 1496:     */     throws Exception
/* 1497:     */   {
/* 1498:2096 */     if (this.m_useDefaultModel) {
/* 1499:2097 */       return this.m_ZeroR.distributionForInstance(i);
/* 1500:     */     }
/* 1501:2100 */     if (this.m_useNomToBin)
/* 1502:     */     {
/* 1503:2101 */       this.m_nominalToBinaryFilter.input(i);
/* 1504:2102 */       this.m_currentInstance = this.m_nominalToBinaryFilter.output();
/* 1505:     */     }
/* 1506:     */     else
/* 1507:     */     {
/* 1508:2104 */       this.m_currentInstance = i;
/* 1509:     */     }
/* 1510:2108 */     this.m_currentInstance = ((Instance)this.m_currentInstance.copy());
/* 1511:2110 */     if (this.m_normalizeAttributes)
/* 1512:     */     {
/* 1513:2111 */       double[] instance = new double[this.m_currentInstance.numAttributes()];
/* 1514:2112 */       for (int noa = 0; noa < this.m_instances.numAttributes(); noa++) {
/* 1515:2113 */         if (noa != this.m_instances.classIndex())
/* 1516:     */         {
/* 1517:2114 */           if (this.m_attributeRanges[noa] != 0.0D) {
/* 1518:2115 */             instance[noa] = ((this.m_currentInstance.value(noa) - this.m_attributeBases[noa]) / this.m_attributeRanges[noa]);
/* 1519:     */           } else {
/* 1520:2117 */             instance[noa] = (this.m_currentInstance.value(noa) - this.m_attributeBases[noa]);
/* 1521:     */           }
/* 1522:     */         }
/* 1523:     */         else {
/* 1524:2120 */           instance[noa] = this.m_currentInstance.value(noa);
/* 1525:     */         }
/* 1526:     */       }
/* 1527:2123 */       this.m_currentInstance = new DenseInstance(this.m_currentInstance.weight(), instance);
/* 1528:2124 */       this.m_currentInstance.setDataset(this.m_instances);
/* 1529:     */     }
/* 1530:2126 */     resetNetwork();
/* 1531:     */     
/* 1532:     */ 
/* 1533:     */ 
/* 1534:2130 */     double[] theArray = new double[this.m_numClasses];
/* 1535:2131 */     for (int noa = 0; noa < this.m_numClasses; noa++) {
/* 1536:2132 */       theArray[noa] = this.m_outputs[noa].outputValue(true);
/* 1537:     */     }
/* 1538:2134 */     if (this.m_instances.classAttribute().isNumeric()) {
/* 1539:2135 */       return theArray;
/* 1540:     */     }
/* 1541:2139 */     double count = 0.0D;
/* 1542:2140 */     for (int noa = 0; noa < this.m_numClasses; noa++) {
/* 1543:2141 */       count += theArray[noa];
/* 1544:     */     }
/* 1545:2143 */     if (count <= 0.0D) {
/* 1546:2144 */       return this.m_ZeroR.distributionForInstance(i);
/* 1547:     */     }
/* 1548:2146 */     for (int noa = 0; noa < this.m_numClasses; noa++) {
/* 1549:2147 */       theArray[noa] /= count;
/* 1550:     */     }
/* 1551:2149 */     return theArray;
/* 1552:     */   }
/* 1553:     */   
/* 1554:     */   public Enumeration<Option> listOptions()
/* 1555:     */   {
/* 1556:2160 */     Vector<Option> newVector = new Vector(14);
/* 1557:     */     
/* 1558:2162 */     newVector.addElement(new Option("\tLearning Rate for the backpropagation algorithm.\n\t(Value should be between 0 - 1, Default = 0.3).", "L", 1, "-L <learning rate>"));
/* 1559:     */     
/* 1560:     */ 
/* 1561:     */ 
/* 1562:2166 */     newVector.addElement(new Option("\tMomentum Rate for the backpropagation algorithm.\n\t(Value should be between 0 - 1, Default = 0.2).", "M", 1, "-M <momentum>"));
/* 1563:     */     
/* 1564:     */ 
/* 1565:     */ 
/* 1566:2170 */     newVector.addElement(new Option("\tNumber of epochs to train through.\n\t(Default = 500).", "N", 1, "-N <number of epochs>"));
/* 1567:     */     
/* 1568:2172 */     newVector.addElement(new Option("\tPercentage size of validation set to use to terminate\n\ttraining (if this is non zero it can pre-empt num of epochs.\n\t(Value should be between 0 - 100, Default = 0).", "V", 1, "-V <percentage size of validation set>"));
/* 1569:     */     
/* 1570:     */ 
/* 1571:     */ 
/* 1572:     */ 
/* 1573:2177 */     newVector.addElement(new Option("\tThe value used to seed the random number generator\n\t(Value should be >= 0 and and a long, Default = 0).", "S", 1, "-S <seed>"));
/* 1574:     */     
/* 1575:     */ 
/* 1576:     */ 
/* 1577:2181 */     newVector.addElement(new Option("\tThe consequetive number of errors allowed for validation\n\ttesting before the netwrok terminates.\n\t(Value should be > 0, Default = 20).", "E", 1, "-E <threshold for number of consequetive errors>"));
/* 1578:     */     
/* 1579:     */ 
/* 1580:     */ 
/* 1581:     */ 
/* 1582:2186 */     newVector.addElement(new Option("\tGUI will be opened.\n\t(Use this to bring up a GUI).", "G", 0, "-G"));
/* 1583:     */     
/* 1584:2188 */     newVector.addElement(new Option("\tAutocreation of the network connections will NOT be done.\n\t(This will be ignored if -G is NOT set)", "A", 0, "-A"));
/* 1585:     */     
/* 1586:     */ 
/* 1587:2191 */     newVector.addElement(new Option("\tA NominalToBinary filter will NOT automatically be used.\n\t(Set this to not use a NominalToBinary filter).", "B", 0, "-B"));
/* 1588:     */     
/* 1589:     */ 
/* 1590:2194 */     newVector.addElement(new Option("\tThe hidden layers to be created for the network.\n\t(Value should be a list of comma separated Natural \n\tnumbers or the letters 'a' = (attribs + classes) / 2, \n\t'i' = attribs, 'o' = classes, 't' = attribs .+ classes)\n\tfor wildcard values, Default = a).", "H", 1, "-H <comma seperated numbers for nodes on each layer>"));
/* 1591:     */     
/* 1592:     */ 
/* 1593:     */ 
/* 1594:     */ 
/* 1595:     */ 
/* 1596:     */ 
/* 1597:2201 */     newVector.addElement(new Option("\tNormalizing a numeric class will NOT be done.\n\t(Set this to not normalize the class if it's numeric).", "C", 0, "-C"));
/* 1598:     */     
/* 1599:     */ 
/* 1600:     */ 
/* 1601:2205 */     newVector.addElement(new Option("\tNormalizing the attributes will NOT be done.\n\t(Set this to not normalize the attributes).", "I", 0, "-I"));
/* 1602:     */     
/* 1603:     */ 
/* 1604:2208 */     newVector.addElement(new Option("\tReseting the network will NOT be allowed.\n\t(Set this to not allow the network to reset).", "R", 0, "-R"));
/* 1605:     */     
/* 1606:     */ 
/* 1607:2211 */     newVector.addElement(new Option("\tLearning rate decay will occur.\n\t(Set this to cause the learning rate to decay).", "D", 0, "-D"));
/* 1608:     */     
/* 1609:     */ 
/* 1610:2214 */     newVector.addAll(Collections.list(super.listOptions()));
/* 1611:     */     
/* 1612:2216 */     return newVector.elements();
/* 1613:     */   }
/* 1614:     */   
/* 1615:     */   public void setOptions(String[] options)
/* 1616:     */     throws Exception
/* 1617:     */   {
/* 1618:2323 */     String learningString = Utils.getOption('L', options);
/* 1619:2324 */     if (learningString.length() != 0) {
/* 1620:2325 */       setLearningRate(new Double(learningString).doubleValue());
/* 1621:     */     } else {
/* 1622:2327 */       setLearningRate(0.3D);
/* 1623:     */     }
/* 1624:2329 */     String momentumString = Utils.getOption('M', options);
/* 1625:2330 */     if (momentumString.length() != 0) {
/* 1626:2331 */       setMomentum(new Double(momentumString).doubleValue());
/* 1627:     */     } else {
/* 1628:2333 */       setMomentum(0.2D);
/* 1629:     */     }
/* 1630:2335 */     String epochsString = Utils.getOption('N', options);
/* 1631:2336 */     if (epochsString.length() != 0) {
/* 1632:2337 */       setTrainingTime(Integer.parseInt(epochsString));
/* 1633:     */     } else {
/* 1634:2339 */       setTrainingTime(500);
/* 1635:     */     }
/* 1636:2341 */     String valSizeString = Utils.getOption('V', options);
/* 1637:2342 */     if (valSizeString.length() != 0) {
/* 1638:2343 */       setValidationSetSize(Integer.parseInt(valSizeString));
/* 1639:     */     } else {
/* 1640:2345 */       setValidationSetSize(0);
/* 1641:     */     }
/* 1642:2347 */     String seedString = Utils.getOption('S', options);
/* 1643:2348 */     if (seedString.length() != 0) {
/* 1644:2349 */       setSeed(Integer.parseInt(seedString));
/* 1645:     */     } else {
/* 1646:2351 */       setSeed(0);
/* 1647:     */     }
/* 1648:2353 */     String thresholdString = Utils.getOption('E', options);
/* 1649:2354 */     if (thresholdString.length() != 0) {
/* 1650:2355 */       setValidationThreshold(Integer.parseInt(thresholdString));
/* 1651:     */     } else {
/* 1652:2357 */       setValidationThreshold(20);
/* 1653:     */     }
/* 1654:2359 */     String hiddenLayers = Utils.getOption('H', options);
/* 1655:2360 */     if (hiddenLayers.length() != 0) {
/* 1656:2361 */       setHiddenLayers(hiddenLayers);
/* 1657:     */     } else {
/* 1658:2363 */       setHiddenLayers("a");
/* 1659:     */     }
/* 1660:2365 */     if (Utils.getFlag('G', options)) {
/* 1661:2366 */       setGUI(true);
/* 1662:     */     } else {
/* 1663:2368 */       setGUI(false);
/* 1664:     */     }
/* 1665:2372 */     if (Utils.getFlag('A', options)) {
/* 1666:2373 */       setAutoBuild(false);
/* 1667:     */     } else {
/* 1668:2375 */       setAutoBuild(true);
/* 1669:     */     }
/* 1670:2377 */     if (Utils.getFlag('B', options)) {
/* 1671:2378 */       setNominalToBinaryFilter(false);
/* 1672:     */     } else {
/* 1673:2380 */       setNominalToBinaryFilter(true);
/* 1674:     */     }
/* 1675:2382 */     if (Utils.getFlag('C', options)) {
/* 1676:2383 */       setNormalizeNumericClass(false);
/* 1677:     */     } else {
/* 1678:2385 */       setNormalizeNumericClass(true);
/* 1679:     */     }
/* 1680:2387 */     if (Utils.getFlag('I', options)) {
/* 1681:2388 */       setNormalizeAttributes(false);
/* 1682:     */     } else {
/* 1683:2390 */       setNormalizeAttributes(true);
/* 1684:     */     }
/* 1685:2392 */     if (Utils.getFlag('R', options)) {
/* 1686:2393 */       setReset(false);
/* 1687:     */     } else {
/* 1688:2395 */       setReset(true);
/* 1689:     */     }
/* 1690:2397 */     if (Utils.getFlag('D', options)) {
/* 1691:2398 */       setDecay(true);
/* 1692:     */     } else {
/* 1693:2400 */       setDecay(false);
/* 1694:     */     }
/* 1695:2403 */     super.setOptions(options);
/* 1696:     */     
/* 1697:2405 */     Utils.checkForRemainingOptions(options);
/* 1698:     */   }
/* 1699:     */   
/* 1700:     */   public String[] getOptions()
/* 1701:     */   {
/* 1702:2416 */     Vector<String> options = new Vector();
/* 1703:     */     
/* 1704:2418 */     options.add("-L");
/* 1705:2419 */     options.add("" + getLearningRate());
/* 1706:2420 */     options.add("-M");
/* 1707:2421 */     options.add("" + getMomentum());
/* 1708:2422 */     options.add("-N");
/* 1709:2423 */     options.add("" + getTrainingTime());
/* 1710:2424 */     options.add("-V");
/* 1711:2425 */     options.add("" + getValidationSetSize());
/* 1712:2426 */     options.add("-S");
/* 1713:2427 */     options.add("" + getSeed());
/* 1714:2428 */     options.add("-E");
/* 1715:2429 */     options.add("" + getValidationThreshold());
/* 1716:2430 */     options.add("-H");
/* 1717:2431 */     options.add(getHiddenLayers());
/* 1718:2432 */     if (getGUI()) {
/* 1719:2433 */       options.add("-G");
/* 1720:     */     }
/* 1721:2435 */     if (!getAutoBuild()) {
/* 1722:2436 */       options.add("-A");
/* 1723:     */     }
/* 1724:2438 */     if (!getNominalToBinaryFilter()) {
/* 1725:2439 */       options.add("-B");
/* 1726:     */     }
/* 1727:2441 */     if (!getNormalizeNumericClass()) {
/* 1728:2442 */       options.add("-C");
/* 1729:     */     }
/* 1730:2444 */     if (!getNormalizeAttributes()) {
/* 1731:2445 */       options.add("-I");
/* 1732:     */     }
/* 1733:2447 */     if (!getReset()) {
/* 1734:2448 */       options.add("-R");
/* 1735:     */     }
/* 1736:2450 */     if (getDecay()) {
/* 1737:2451 */       options.add("-D");
/* 1738:     */     }
/* 1739:2454 */     Collections.addAll(options, super.getOptions());
/* 1740:     */     
/* 1741:2456 */     return (String[])options.toArray(new String[0]);
/* 1742:     */   }
/* 1743:     */   
/* 1744:     */   public String toString()
/* 1745:     */   {
/* 1746:2465 */     if (this.m_useDefaultModel)
/* 1747:     */     {
/* 1748:2466 */       StringBuffer buf = new StringBuffer();
/* 1749:2467 */       buf.append(getClass().getName().replaceAll(".*\\.", "") + "\n");
/* 1750:2468 */       buf.append(getClass().getName().replaceAll(".*\\.", "").replaceAll(".", "=") + "\n\n");
/* 1751:     */       
/* 1752:     */ 
/* 1753:2471 */       buf.append("Warning: No model could be built, hence ZeroR model is used:\n\n");
/* 1754:     */       
/* 1755:2473 */       buf.append(this.m_ZeroR.toString());
/* 1756:2474 */       return buf.toString();
/* 1757:     */     }
/* 1758:2477 */     StringBuffer model = new StringBuffer(this.m_neuralNodes.length * 100);
/* 1759:2482 */     for (NeuralConnection m_neuralNode : this.m_neuralNodes)
/* 1760:     */     {
/* 1761:2483 */       NeuralNode con = (NeuralNode)m_neuralNode;
/* 1762:     */       
/* 1763:2485 */       double[] weights = con.getWeights();
/* 1764:2486 */       NeuralConnection[] inputs = con.getInputs();
/* 1765:2487 */       if ((con.getMethod() instanceof SigmoidUnit)) {
/* 1766:2488 */         model.append("Sigmoid ");
/* 1767:2489 */       } else if ((con.getMethod() instanceof LinearUnit)) {
/* 1768:2490 */         model.append("Linear ");
/* 1769:     */       }
/* 1770:2492 */       model.append("Node " + con.getId() + "\n    Inputs    Weights\n");
/* 1771:2493 */       model.append("    Threshold    " + weights[0] + "\n");
/* 1772:2494 */       for (int nob = 1; nob < con.getNumInputs() + 1; nob++) {
/* 1773:2495 */         if ((inputs[(nob - 1)].getType() & 0x1) == 1) {
/* 1774:2496 */           model.append("    Attrib " + this.m_instances.attribute(((NeuralEnd)inputs[(nob - 1)]).getLink()).name() + "    " + weights[nob] + "\n");
/* 1775:     */         } else {
/* 1776:2500 */           model.append("    Node " + inputs[(nob - 1)].getId() + "    " + weights[nob] + "\n");
/* 1777:     */         }
/* 1778:     */       }
/* 1779:     */     }
/* 1780:2506 */     for (NeuralEnd m_output : this.m_outputs)
/* 1781:     */     {
/* 1782:2507 */       NeuralConnection[] inputs = m_output.getInputs();
/* 1783:2508 */       model.append("Class " + this.m_instances.classAttribute().value(m_output.getLink()) + "\n    Input\n");
/* 1784:2511 */       for (int nob = 0; nob < m_output.getNumInputs(); nob++) {
/* 1785:2512 */         if ((inputs[nob].getType() & 0x1) == 1) {
/* 1786:2513 */           model.append("    Attrib " + this.m_instances.attribute(((NeuralEnd)inputs[nob]).getLink()).name() + "\n");
/* 1787:     */         } else {
/* 1788:2517 */           model.append("    Node " + inputs[nob].getId() + "\n");
/* 1789:     */         }
/* 1790:     */       }
/* 1791:     */     }
/* 1792:2521 */     return model.toString();
/* 1793:     */   }
/* 1794:     */   
/* 1795:     */   public String globalInfo()
/* 1796:     */   {
/* 1797:2530 */     return "A Classifier that uses backpropagation to classify instances.\nThis network can be built by hand, created by an algorithm or both. The network can also be monitored and modified during training time. The nodes in this network are all sigmoid (except for when the class is numeric in which case the the output nodes become unthresholded linear units).";
/* 1798:     */   }
/* 1799:     */   
/* 1800:     */   public String learningRateTipText()
/* 1801:     */   {
/* 1802:2542 */     return "The amount the weights are updated.";
/* 1803:     */   }
/* 1804:     */   
/* 1805:     */   public String momentumTipText()
/* 1806:     */   {
/* 1807:2549 */     return "Momentum applied to the weights during updating.";
/* 1808:     */   }
/* 1809:     */   
/* 1810:     */   public String autoBuildTipText()
/* 1811:     */   {
/* 1812:2556 */     return "Adds and connects up hidden layers in the network.";
/* 1813:     */   }
/* 1814:     */   
/* 1815:     */   public String seedTipText()
/* 1816:     */   {
/* 1817:2563 */     return "Seed used to initialise the random number generator.Random numbers are used for setting the initial weights of the connections betweem nodes, and also for shuffling the training data.";
/* 1818:     */   }
/* 1819:     */   
/* 1820:     */   public String validationThresholdTipText()
/* 1821:     */   {
/* 1822:2572 */     return "Used to terminate validation testing.The value here dictates how many times in a row the validation set error can get worse before training is terminated.";
/* 1823:     */   }
/* 1824:     */   
/* 1825:     */   public String GUITipText()
/* 1826:     */   {
/* 1827:2581 */     return "Brings up a gui interface. This will allow the pausing and altering of the nueral network during training.\n\n* To add a node left click (this node will be automatically selected, ensure no other nodes were selected).\n* To select a node left click on it either while no other node is selected or while holding down the control key (this toggles that node as being selected and not selected.\n* To connect a node, first have the start node(s) selected, then click either the end node or on an empty space (this will create a new node that is connected with the selected nodes). The selection status of nodes will stay the same after the connection. (Note these are directed connections, also a connection between two nodes will not be established more than once and certain connections that are deemed to be invalid will not be made).\n* To remove a connection select one of the connected node(s) in the connection and then right click the other node (it does not matter whether the node is the start or end the connection will be removed).\n* To remove a node right click it while no other nodes (including it) are selected. (This will also remove all connections to it)\n.* To deselect a node either left click it while holding down control, or right click on empty space.\n* The raw inputs are provided from the labels on the left.\n* The red nodes are hidden layers.\n* The orange nodes are the output nodes.\n* The labels on the right show the class the output node represents. Note that with a numeric class the output node will automatically be made into an unthresholded linear unit.\n\nAlterations to the neural network can only be done while the network is not running, This also applies to the learning rate and other fields on the control panel.\n\n* You can accept the network as being finished at any time.\n* The network is automatically paused at the beginning.\n* There is a running indication of what epoch the network is up to and what the (rough) error for that epoch was (or for the validation if that is being used). Note that this error value is based on a network that changes as the value is computed. (also depending on whether the class is normalized will effect the error reported for numeric classes.\n* Once the network is done it will pause again and either wait to be accepted or trained more.\n\nNote that if the gui is not set the network will not require any interaction.\n";
/* 1828:     */   }
/* 1829:     */   
/* 1830:     */   public String validationSetSizeTipText()
/* 1831:     */   {
/* 1832:2632 */     return "The percentage size of the validation set.(The training will continue until it is observed that the error on the validation set has been consistently getting worse, or if the training time is reached).\nIf This is set to zero no validation set will be used and instead the network will train for the specified number of epochs.";
/* 1833:     */   }
/* 1834:     */   
/* 1835:     */   public String trainingTimeTipText()
/* 1836:     */   {
/* 1837:2644 */     return "The number of epochs to train through. If the validation set is non-zero then it can terminate the network early";
/* 1838:     */   }
/* 1839:     */   
/* 1840:     */   public String nominalToBinaryFilterTipText()
/* 1841:     */   {
/* 1842:2653 */     return "This will preprocess the instances with the filter. This could help improve performance if there are nominal attributes in the data.";
/* 1843:     */   }
/* 1844:     */   
/* 1845:     */   public String hiddenLayersTipText()
/* 1846:     */   {
/* 1847:2662 */     return "This defines the hidden layers of the neural network. This is a list of positive whole numbers. 1 for each hidden layer. Comma seperated. To have no hidden layers put a single 0 here. This will only be used if autobuild is set. There are also wildcard values 'a' = (attribs + classes) / 2, 'i' = attribs, 'o' = classes , 't' = attribs + classes.";
/* 1848:     */   }
/* 1849:     */   
/* 1850:     */   public String normalizeNumericClassTipText()
/* 1851:     */   {
/* 1852:2674 */     return "This will normalize the class if it's numeric. This could help improve performance of the network, It normalizes the class to be between -1 and 1. Note that this is only internally, the output will be scaled back to the original range.";
/* 1853:     */   }
/* 1854:     */   
/* 1855:     */   public String normalizeAttributesTipText()
/* 1856:     */   {
/* 1857:2684 */     return "This will normalize the attributes. This could help improve performance of the network. This is not reliant on the class being numeric. This will also normalize nominal attributes as well (after they have been run through the nominal to binary filter if that is in use) so that the nominal values are between -1 and 1";
/* 1858:     */   }
/* 1859:     */   
/* 1860:     */   public String resetTipText()
/* 1861:     */   {
/* 1862:2696 */     return "This will allow the network to reset with a lower learning rate. If the network diverges from the answer this will automatically reset the network with a lower learning rate and begin training again. This option is only available if the gui is not set. Note that if the network diverges but isn't allowed to reset it will fail the training process and return an error message.";
/* 1863:     */   }
/* 1864:     */   
/* 1865:     */   public String decayTipText()
/* 1866:     */   {
/* 1867:2708 */     return "This will cause the learning rate to decrease. This will divide the starting learning rate by the epoch number, to determine what the current learning rate should be. This may help to stop the network from diverging from the target output, as well as improve general performance. Note that the decaying learning rate will not be shown in the gui, only the original learning rate. If the learning rate is changed in the gui, this is treated as the starting learning rate.";
/* 1868:     */   }
/* 1869:     */   
/* 1870:     */   public String getRevision()
/* 1871:     */   {
/* 1872:2725 */     return RevisionUtils.extract("$Revision: 12449 $");
/* 1873:     */   }
/* 1874:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.MultilayerPerceptron
 * JD-Core Version:    0.7.0.1
 */