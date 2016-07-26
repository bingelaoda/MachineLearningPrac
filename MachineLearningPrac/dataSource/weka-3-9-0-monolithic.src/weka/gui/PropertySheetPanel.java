/*    1:     */ package weka.gui;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.awt.Component;
/*    5:     */ import java.awt.Container;
/*    6:     */ import java.awt.Dialog;
/*    7:     */ import java.awt.Dimension;
/*    8:     */ import java.awt.Font;
/*    9:     */ import java.awt.Frame;
/*   10:     */ import java.awt.GridBagConstraints;
/*   11:     */ import java.awt.GridBagLayout;
/*   12:     */ import java.awt.Insets;
/*   13:     */ import java.awt.Point;
/*   14:     */ import java.awt.event.ActionEvent;
/*   15:     */ import java.awt.event.ActionListener;
/*   16:     */ import java.awt.event.WindowAdapter;
/*   17:     */ import java.awt.event.WindowEvent;
/*   18:     */ import java.beans.BeanDescriptor;
/*   19:     */ import java.beans.BeanInfo;
/*   20:     */ import java.beans.Beans;
/*   21:     */ import java.beans.IntrospectionException;
/*   22:     */ import java.beans.Introspector;
/*   23:     */ import java.beans.MethodDescriptor;
/*   24:     */ import java.beans.PropertyChangeEvent;
/*   25:     */ import java.beans.PropertyChangeListener;
/*   26:     */ import java.beans.PropertyChangeSupport;
/*   27:     */ import java.beans.PropertyDescriptor;
/*   28:     */ import java.beans.PropertyEditor;
/*   29:     */ import java.beans.PropertyEditorManager;
/*   30:     */ import java.beans.PropertyVetoException;
/*   31:     */ import java.io.File;
/*   32:     */ import java.io.PrintStream;
/*   33:     */ import java.lang.annotation.Annotation;
/*   34:     */ import java.lang.reflect.InvocationTargetException;
/*   35:     */ import java.lang.reflect.Method;
/*   36:     */ import java.util.ArrayList;
/*   37:     */ import java.util.Arrays;
/*   38:     */ import java.util.Iterator;
/*   39:     */ import java.util.List;
/*   40:     */ import javax.swing.BorderFactory;
/*   41:     */ import javax.swing.JButton;
/*   42:     */ import javax.swing.JComponent;
/*   43:     */ import javax.swing.JDialog;
/*   44:     */ import javax.swing.JFrame;
/*   45:     */ import javax.swing.JLabel;
/*   46:     */ import javax.swing.JOptionPane;
/*   47:     */ import javax.swing.JPanel;
/*   48:     */ import javax.swing.JScrollPane;
/*   49:     */ import javax.swing.JTextArea;
/*   50:     */ import weka.core.Capabilities;
/*   51:     */ import weka.core.Capabilities.Capability;
/*   52:     */ import weka.core.CapabilitiesHandler;
/*   53:     */ import weka.core.Environment;
/*   54:     */ import weka.core.EnvironmentHandler;
/*   55:     */ import weka.core.MultiInstanceCapabilitiesHandler;
/*   56:     */ import weka.core.OptionMetadata;
/*   57:     */ import weka.core.Utils;
/*   58:     */ import weka.gui.beans.GOECustomizer;
/*   59:     */ 
/*   60:     */ public class PropertySheetPanel
/*   61:     */   extends JPanel
/*   62:     */   implements PropertyChangeListener, EnvironmentHandler
/*   63:     */ {
/*   64:     */   private static final long serialVersionUID = -8939835593429918345L;
/*   65:     */   private Object m_Target;
/*   66:     */   
/*   67:     */   protected class CapabilitiesHelpDialog
/*   68:     */     extends JDialog
/*   69:     */     implements PropertyChangeListener
/*   70:     */   {
/*   71:     */     private static final long serialVersionUID = -1404770987103289858L;
/*   72:     */     private CapabilitiesHelpDialog m_Self;
/*   73:     */     
/*   74:     */     public CapabilitiesHelpDialog(Frame owner)
/*   75:     */     {
/*   76: 105 */       super();
/*   77:     */       
/*   78: 107 */       initialize();
/*   79:     */     }
/*   80:     */     
/*   81:     */     public CapabilitiesHelpDialog(Dialog owner)
/*   82:     */     {
/*   83: 116 */       super();
/*   84:     */       
/*   85: 118 */       initialize();
/*   86:     */     }
/*   87:     */     
/*   88:     */     protected void initialize()
/*   89:     */     {
/*   90: 125 */       setTitle("Information about Capabilities");
/*   91:     */       
/*   92: 127 */       this.m_Self = this;
/*   93:     */       
/*   94: 129 */       PropertySheetPanel.this.m_CapabilitiesText = new JTextArea();
/*   95: 130 */       PropertySheetPanel.this.m_CapabilitiesText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*   96: 131 */       PropertySheetPanel.this.m_CapabilitiesText.setLineWrap(true);
/*   97: 132 */       PropertySheetPanel.this.m_CapabilitiesText.setWrapStyleWord(true);
/*   98: 133 */       PropertySheetPanel.this.m_CapabilitiesText.setEditable(false);
/*   99: 134 */       updateText();
/*  100: 135 */       addWindowListener(new WindowAdapter()
/*  101:     */       {
/*  102:     */         public void windowClosing(WindowEvent e)
/*  103:     */         {
/*  104: 138 */           PropertySheetPanel.CapabilitiesHelpDialog.this.m_Self.dispose();
/*  105: 139 */           if (PropertySheetPanel.this.m_CapabilitiesDialog == PropertySheetPanel.CapabilitiesHelpDialog.this.m_Self) {
/*  106: 140 */             PropertySheetPanel.this.m_CapabilitiesBut.setEnabled(true);
/*  107:     */           }
/*  108:     */         }
/*  109: 143 */       });
/*  110: 144 */       getContentPane().setLayout(new BorderLayout());
/*  111: 145 */       getContentPane().add(new JScrollPane(PropertySheetPanel.this.m_CapabilitiesText), "Center");
/*  112:     */       
/*  113: 147 */       pack();
/*  114:     */     }
/*  115:     */     
/*  116:     */     protected void updateText()
/*  117:     */     {
/*  118: 154 */       StringBuffer helpText = new StringBuffer();
/*  119: 156 */       if ((PropertySheetPanel.this.m_Target instanceof CapabilitiesHandler)) {
/*  120: 157 */         helpText.append(PropertySheetPanel.addCapabilities("CAPABILITIES", ((CapabilitiesHandler)PropertySheetPanel.this.m_Target).getCapabilities()));
/*  121:     */       }
/*  122: 161 */       if ((PropertySheetPanel.this.m_Target instanceof MultiInstanceCapabilitiesHandler)) {
/*  123: 162 */         helpText.append(PropertySheetPanel.addCapabilities("MI CAPABILITIES", ((MultiInstanceCapabilitiesHandler)PropertySheetPanel.this.m_Target).getMultiInstanceCapabilities()));
/*  124:     */       }
/*  125: 167 */       PropertySheetPanel.this.m_CapabilitiesText.setText(helpText.toString());
/*  126: 168 */       PropertySheetPanel.this.m_CapabilitiesText.setCaretPosition(0);
/*  127:     */     }
/*  128:     */     
/*  129:     */     public void propertyChange(PropertyChangeEvent evt)
/*  130:     */     {
/*  131: 178 */       updateText();
/*  132:     */     }
/*  133:     */   }
/*  134:     */   
/*  135:     */   public static String listCapabilities(Capabilities c)
/*  136:     */   {
/*  137: 192 */     String result = "";
/*  138: 193 */     Iterator<Capabilities.Capability> iter = c.capabilities();
/*  139: 194 */     while (iter.hasNext())
/*  140:     */     {
/*  141: 195 */       if (result.length() != 0) {
/*  142: 196 */         result = result + ", ";
/*  143:     */       }
/*  144: 198 */       result = result + ((Capabilities.Capability)iter.next()).toString();
/*  145:     */     }
/*  146: 201 */     return result;
/*  147:     */   }
/*  148:     */   
/*  149:     */   public static String addCapabilities(String title, Capabilities c)
/*  150:     */   {
/*  151: 216 */     String result = title + "\n";
/*  152:     */     
/*  153:     */ 
/*  154: 219 */     String caps = listCapabilities(c.getClassCapabilities());
/*  155: 220 */     if (caps.length() != 0)
/*  156:     */     {
/*  157: 221 */       result = result + "Class -- ";
/*  158: 222 */       result = result + caps;
/*  159: 223 */       result = result + "\n\n";
/*  160:     */     }
/*  161: 227 */     caps = listCapabilities(c.getAttributeCapabilities());
/*  162: 228 */     if (caps.length() != 0)
/*  163:     */     {
/*  164: 229 */       result = result + "Attributes -- ";
/*  165: 230 */       result = result + caps;
/*  166: 231 */       result = result + "\n\n";
/*  167:     */     }
/*  168: 235 */     caps = listCapabilities(c.getOtherCapabilities());
/*  169: 236 */     if (caps.length() != 0)
/*  170:     */     {
/*  171: 237 */       result = result + "Other -- ";
/*  172: 238 */       result = result + caps;
/*  173: 239 */       result = result + "\n\n";
/*  174:     */     }
/*  175: 243 */     result = result + "Additional\n";
/*  176: 244 */     result = result + "min # of instances: " + c.getMinimumNumberInstances() + "\n";
/*  177: 245 */     result = result + "\n";
/*  178:     */     
/*  179: 247 */     return result;
/*  180:     */   }
/*  181:     */   
/*  182: 254 */   private boolean m_showAboutPanel = true;
/*  183:     */   private GOECustomizer m_Customizer;
/*  184:     */   private PropertyDescriptor[] m_Properties;
/*  185:     */   private MethodDescriptor[] m_Methods;
/*  186:     */   private PropertyEditor[] m_Editors;
/*  187:     */   private Object[] m_Values;
/*  188:     */   private JComponent[] m_Views;
/*  189:     */   private JLabel[] m_Labels;
/*  190:     */   private String[] m_TipTexts;
/*  191:     */   private StringBuffer m_HelpText;
/*  192:     */   private JDialog m_HelpDialog;
/*  193:     */   private CapabilitiesHelpDialog m_CapabilitiesDialog;
/*  194:     */   private JButton m_HelpBut;
/*  195:     */   private JButton m_CapabilitiesBut;
/*  196:     */   private JTextArea m_CapabilitiesText;
/*  197: 299 */   private int m_NumEditable = 0;
/*  198:     */   private JPanel m_aboutPanel;
/*  199:     */   private transient Environment m_env;
/*  200:     */   private boolean m_useEnvironmentPropertyEditors;
/*  201:     */   
/*  202:     */   public PropertySheetPanel()
/*  203:     */   {
/*  204: 321 */     setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
/*  205: 322 */     this.m_env = Environment.getSystemWide();
/*  206:     */   }
/*  207:     */   
/*  208:     */   public PropertySheetPanel(boolean showAboutPanel)
/*  209:     */   {
/*  210: 332 */     this.m_showAboutPanel = showAboutPanel;
/*  211:     */   }
/*  212:     */   
/*  213:     */   public void setUseEnvironmentPropertyEditors(boolean u)
/*  214:     */   {
/*  215: 342 */     this.m_useEnvironmentPropertyEditors = u;
/*  216:     */   }
/*  217:     */   
/*  218:     */   public boolean getUseEnvironmentPropertyEditors()
/*  219:     */   {
/*  220: 352 */     return this.m_useEnvironmentPropertyEditors;
/*  221:     */   }
/*  222:     */   
/*  223:     */   public JPanel getAboutPanel()
/*  224:     */   {
/*  225: 363 */     return this.m_aboutPanel;
/*  226:     */   }
/*  227:     */   
/*  228: 367 */   private final PropertyChangeSupport support = new PropertyChangeSupport(this);
/*  229:     */   
/*  230:     */   public void propertyChange(PropertyChangeEvent evt)
/*  231:     */   {
/*  232: 377 */     wasModified(evt);
/*  233: 378 */     this.support.firePropertyChange("", null, null);
/*  234:     */   }
/*  235:     */   
/*  236:     */   public void addPropertyChangeListener(PropertyChangeListener l)
/*  237:     */   {
/*  238: 389 */     if ((this.support != null) && (l != null)) {
/*  239: 390 */       this.support.addPropertyChangeListener(l);
/*  240:     */     }
/*  241:     */   }
/*  242:     */   
/*  243:     */   public void removePropertyChangeListener(PropertyChangeListener l)
/*  244:     */   {
/*  245: 402 */     if ((this.support != null) && (l != null)) {
/*  246: 403 */       this.support.removePropertyChangeListener(l);
/*  247:     */     }
/*  248:     */   }
/*  249:     */   
/*  250:     */   public synchronized void setTarget(Object targ)
/*  251:     */   {
/*  252: 414 */     if (this.m_env == null) {
/*  253: 415 */       this.m_env = Environment.getSystemWide();
/*  254:     */     }
/*  255: 420 */     int componentOffset = 0;
/*  256:     */     
/*  257:     */ 
/*  258: 423 */     removeAll();
/*  259:     */     
/*  260: 425 */     setLayout(new BorderLayout());
/*  261: 426 */     JPanel scrollablePanel = new JPanel();
/*  262: 427 */     JScrollPane scrollPane = new JScrollPane(scrollablePanel);
/*  263: 428 */     scrollPane.setBorder(BorderFactory.createEmptyBorder());
/*  264: 429 */     add(scrollPane, "Center");
/*  265:     */     
/*  266: 431 */     GridBagLayout gbLayout = new GridBagLayout();
/*  267:     */     
/*  268: 433 */     scrollablePanel.setLayout(gbLayout);
/*  269: 434 */     setVisible(false);
/*  270: 435 */     this.m_NumEditable = 0;
/*  271: 436 */     this.m_Target = targ;
/*  272: 437 */     Class<?> custClass = null;
/*  273:     */     try
/*  274:     */     {
/*  275: 439 */       BeanInfo bi = Introspector.getBeanInfo(this.m_Target.getClass());
/*  276: 440 */       this.m_Properties = bi.getPropertyDescriptors();
/*  277: 441 */       this.m_Methods = bi.getMethodDescriptors();
/*  278: 442 */       custClass = Introspector.getBeanInfo(this.m_Target.getClass()).getBeanDescriptor().getCustomizerClass();
/*  279:     */     }
/*  280:     */     catch (IntrospectionException ex)
/*  281:     */     {
/*  282: 445 */       System.err.println("PropertySheet: Couldn't introspect");
/*  283: 446 */       return;
/*  284:     */     }
/*  285: 449 */     JTextArea jt = new JTextArea();
/*  286: 450 */     this.m_HelpText = null;
/*  287:     */     
/*  288:     */ 
/*  289: 453 */     Object[] args = new Object[0];
/*  290: 454 */     boolean firstTip = true;
/*  291: 455 */     StringBuffer optionsBuff = new StringBuffer();
/*  292: 456 */     for (MethodDescriptor m_Method : this.m_Methods)
/*  293:     */     {
/*  294: 457 */       String name = m_Method.getDisplayName();
/*  295: 458 */       Method meth = m_Method.getMethod();
/*  296: 459 */       OptionMetadata o = (OptionMetadata)meth.getAnnotation(OptionMetadata.class);
/*  297: 461 */       if (((name.endsWith("TipText")) || (o != null)) && (
/*  298: 462 */         (meth.getReturnType().equals(String.class)) || (o != null))) {
/*  299:     */         try
/*  300:     */         {
/*  301: 464 */           String tempTip = o != null ? o.description() : (String)meth.invoke(this.m_Target, args);
/*  302:     */           
/*  303: 466 */           name = o != null ? o.displayName() : name;
/*  304: 468 */           if (firstTip)
/*  305:     */           {
/*  306: 469 */             optionsBuff.append("OPTIONS\n");
/*  307: 470 */             firstTip = false;
/*  308:     */           }
/*  309: 472 */           tempTip = tempTip.replace("<html>", "").replace("</html>", "").replace("<br>", "\n").replace("<p>", "\n\n");
/*  310:     */           
/*  311: 474 */           optionsBuff.append(name.replace("TipText", "")).append(" -- ");
/*  312: 475 */           optionsBuff.append(tempTip).append("\n\n");
/*  313:     */         }
/*  314:     */         catch (Exception ex) {}
/*  315:     */       }
/*  316: 485 */       if ((name.equals("globalInfo")) && 
/*  317: 486 */         (meth.getReturnType().equals(String.class))) {
/*  318:     */         try
/*  319:     */         {
/*  320: 489 */           String globalInfo = (String)meth.invoke(this.m_Target, args);
/*  321: 490 */           String summary = globalInfo;
/*  322: 491 */           int ci = globalInfo.indexOf('.');
/*  323: 492 */           if (ci != -1) {
/*  324: 493 */             summary = globalInfo.substring(0, ci + 1);
/*  325:     */           }
/*  326: 495 */           String className = targ.getClass().getName();
/*  327: 496 */           this.m_HelpText = new StringBuffer("NAME\n");
/*  328: 497 */           this.m_HelpText.append(className).append("\n\n");
/*  329: 498 */           this.m_HelpText.append("SYNOPSIS\n").append(globalInfo).append("\n\n");
/*  330: 499 */           this.m_HelpBut = new JButton("More");
/*  331: 500 */           this.m_HelpBut.setToolTipText("More information about " + className);
/*  332:     */           
/*  333: 502 */           this.m_HelpBut.addActionListener(new ActionListener()
/*  334:     */           {
/*  335:     */             public void actionPerformed(ActionEvent a)
/*  336:     */             {
/*  337: 505 */               PropertySheetPanel.this.openHelpFrame();
/*  338: 506 */               PropertySheetPanel.this.m_HelpBut.setEnabled(false);
/*  339:     */             }
/*  340:     */           });
/*  341: 510 */           if ((this.m_Target instanceof CapabilitiesHandler))
/*  342:     */           {
/*  343: 511 */             this.m_CapabilitiesBut = new JButton("Capabilities");
/*  344: 512 */             this.m_CapabilitiesBut.setToolTipText("The capabilities of " + className);
/*  345:     */             
/*  346:     */ 
/*  347: 515 */             this.m_CapabilitiesBut.addActionListener(new ActionListener()
/*  348:     */             {
/*  349:     */               public void actionPerformed(ActionEvent a)
/*  350:     */               {
/*  351: 518 */                 PropertySheetPanel.this.openCapabilitiesHelpDialog();
/*  352: 519 */                 PropertySheetPanel.this.m_CapabilitiesBut.setEnabled(false);
/*  353:     */               }
/*  354:     */             });
/*  355:     */           }
/*  356:     */           else
/*  357:     */           {
/*  358: 523 */             this.m_CapabilitiesBut = null;
/*  359:     */           }
/*  360: 526 */           jt.setColumns(30);
/*  361: 527 */           jt.setFont(new Font("SansSerif", 0, 12));
/*  362: 528 */           jt.setEditable(false);
/*  363: 529 */           jt.setLineWrap(true);
/*  364: 530 */           jt.setWrapStyleWord(true);
/*  365: 531 */           jt.setText(summary);
/*  366: 532 */           jt.setBackground(getBackground());
/*  367: 533 */           JPanel jp = new JPanel();
/*  368: 534 */           jp.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("About"), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
/*  369:     */           
/*  370:     */ 
/*  371: 537 */           jp.setLayout(new BorderLayout());
/*  372: 538 */           jp.add(jt, "Center");
/*  373: 539 */           JPanel p2 = new JPanel();
/*  374: 540 */           p2.setLayout(new BorderLayout());
/*  375: 541 */           p2.add(this.m_HelpBut, "North");
/*  376: 542 */           if (this.m_CapabilitiesBut != null)
/*  377:     */           {
/*  378: 543 */             JPanel p3 = new JPanel();
/*  379: 544 */             p3.setLayout(new BorderLayout());
/*  380: 545 */             p3.add(this.m_CapabilitiesBut, "North");
/*  381: 546 */             p2.add(p3, "Center");
/*  382:     */           }
/*  383: 548 */           jp.add(p2, "East");
/*  384: 549 */           GridBagConstraints gbConstraints = new GridBagConstraints();
/*  385:     */           
/*  386: 551 */           gbConstraints.fill = 1;
/*  387:     */           
/*  388: 553 */           gbConstraints.gridwidth = 2;
/*  389: 554 */           gbConstraints.insets = new Insets(0, 5, 0, 5);
/*  390: 555 */           gbLayout.setConstraints(jp, gbConstraints);
/*  391: 556 */           this.m_aboutPanel = jp;
/*  392: 557 */           if (this.m_showAboutPanel) {
/*  393: 558 */             scrollablePanel.add(this.m_aboutPanel);
/*  394:     */           }
/*  395: 560 */           componentOffset = 1;
/*  396:     */         }
/*  397:     */         catch (Exception ex) {}
/*  398:     */       }
/*  399:     */     }
/*  400: 570 */     if (this.m_HelpText != null) {
/*  401: 571 */       this.m_HelpText.append(optionsBuff.toString());
/*  402:     */     }
/*  403: 574 */     if (custClass != null) {
/*  404:     */       try
/*  405:     */       {
/*  406: 577 */         Object customizer = custClass.newInstance();
/*  407: 579 */         if (((customizer instanceof JComponent)) && ((customizer instanceof GOECustomizer)))
/*  408:     */         {
/*  409: 581 */           this.m_Customizer = ((GOECustomizer)customizer);
/*  410:     */           
/*  411: 583 */           this.m_Customizer.dontShowOKCancelButtons();
/*  412: 584 */           this.m_Customizer.setObject(this.m_Target);
/*  413:     */           
/*  414: 586 */           GridBagConstraints gbc = new GridBagConstraints();
/*  415: 587 */           gbc.fill = 1;
/*  416: 588 */           gbc.gridwidth = 2;
/*  417: 589 */           gbc.gridy = componentOffset;
/*  418: 590 */           gbc.gridx = 0;
/*  419: 591 */           gbc.insets = new Insets(0, 5, 0, 5);
/*  420: 592 */           gbLayout.setConstraints((JComponent)this.m_Customizer, gbc);
/*  421: 593 */           scrollablePanel.add((JComponent)this.m_Customizer);
/*  422:     */           
/*  423: 595 */           validate();
/*  424:     */           
/*  425:     */ 
/*  426:     */ 
/*  427:     */ 
/*  428: 600 */           Dimension dim = scrollablePanel.getPreferredSize();
/*  429: 601 */           dim.height += 20;
/*  430: 602 */           dim.width += 20;
/*  431: 603 */           scrollPane.setPreferredSize(dim);
/*  432: 604 */           validate();
/*  433:     */           
/*  434: 606 */           setVisible(true);
/*  435: 607 */           return;
/*  436:     */         }
/*  437:     */       }
/*  438:     */       catch (InstantiationException e)
/*  439:     */       {
/*  440: 611 */         e.printStackTrace();
/*  441:     */       }
/*  442:     */       catch (IllegalAccessException e)
/*  443:     */       {
/*  444: 614 */         e.printStackTrace();
/*  445:     */       }
/*  446:     */     }
/*  447: 618 */     int[] propOrdering = new int[this.m_Properties.length];
/*  448: 619 */     for (int i = 0; i < propOrdering.length; i++) {
/*  449: 620 */       propOrdering[i] = 2147483647;
/*  450:     */     }
/*  451: 622 */     for (int i = 0; i < this.m_Properties.length; i++)
/*  452:     */     {
/*  453: 623 */       Method getter = this.m_Properties[i].getReadMethod();
/*  454: 624 */       Method setter = this.m_Properties[i].getWriteMethod();
/*  455: 625 */       if ((getter != null) && (setter != null))
/*  456:     */       {
/*  457: 628 */         List<Annotation> annotations = new ArrayList();
/*  458: 629 */         if (setter.getDeclaredAnnotations().length > 0) {
/*  459: 630 */           annotations.addAll(Arrays.asList(setter.getDeclaredAnnotations()));
/*  460:     */         }
/*  461: 632 */         if (getter.getDeclaredAnnotations().length > 0) {
/*  462: 633 */           annotations.addAll(Arrays.asList(getter.getDeclaredAnnotations()));
/*  463:     */         }
/*  464: 635 */         for (Annotation a : annotations) {
/*  465: 636 */           if ((a instanceof OptionMetadata))
/*  466:     */           {
/*  467: 637 */             propOrdering[i] = ((OptionMetadata)a).displayOrder();
/*  468: 638 */             break;
/*  469:     */           }
/*  470:     */         }
/*  471:     */       }
/*  472:     */     }
/*  473: 642 */     int[] sortedPropOrderings = Utils.sort(propOrdering);
/*  474: 643 */     this.m_Editors = new PropertyEditor[this.m_Properties.length];
/*  475: 644 */     this.m_Values = new Object[this.m_Properties.length];
/*  476: 645 */     this.m_Views = new JComponent[this.m_Properties.length];
/*  477: 646 */     this.m_Labels = new JLabel[this.m_Properties.length];
/*  478: 647 */     this.m_TipTexts = new String[this.m_Properties.length];
/*  479: 649 */     for (int i = 0; i < this.m_Properties.length; i++) {
/*  480: 652 */       if ((!this.m_Properties[sortedPropOrderings[i]].isHidden()) && (!this.m_Properties[sortedPropOrderings[i]].isExpert()))
/*  481:     */       {
/*  482: 657 */         String name = this.m_Properties[sortedPropOrderings[i]].getDisplayName();
/*  483: 658 */         String origName = name;
/*  484: 659 */         Class<?> type = this.m_Properties[sortedPropOrderings[i]].getPropertyType();
/*  485: 660 */         Method getter = this.m_Properties[sortedPropOrderings[i]].getReadMethod();
/*  486: 661 */         Method setter = this.m_Properties[sortedPropOrderings[i]].getWriteMethod();
/*  487: 664 */         if ((getter != null) && (setter != null))
/*  488:     */         {
/*  489: 668 */           List<Annotation> annotations = new ArrayList();
/*  490: 669 */           if (setter.getDeclaredAnnotations().length > 0) {
/*  491: 670 */             annotations.addAll(Arrays.asList(setter.getDeclaredAnnotations()));
/*  492:     */           }
/*  493: 672 */           if (getter.getDeclaredAnnotations().length > 0) {
/*  494: 673 */             annotations.addAll(Arrays.asList(getter.getDeclaredAnnotations()));
/*  495:     */           }
/*  496: 676 */           boolean skip = false;
/*  497: 677 */           boolean password = false;
/*  498: 678 */           FilePropertyMetadata fileProp = null;
/*  499: 679 */           for (Annotation a : annotations)
/*  500:     */           {
/*  501: 680 */             if ((a instanceof ProgrammaticProperty))
/*  502:     */             {
/*  503: 681 */               skip = true;
/*  504: 682 */               break;
/*  505:     */             }
/*  506: 685 */             if ((a instanceof OptionMetadata))
/*  507:     */             {
/*  508: 686 */               name = ((OptionMetadata)a).displayName();
/*  509: 687 */               String tempTip = ((OptionMetadata)a).description();
/*  510: 688 */               int ci = tempTip.indexOf('.');
/*  511: 689 */               if (ci < 0) {
/*  512: 690 */                 this.m_TipTexts[sortedPropOrderings[i]] = tempTip;
/*  513:     */               } else {
/*  514: 692 */                 this.m_TipTexts[sortedPropOrderings[i]] = tempTip.substring(0, ci);
/*  515:     */               }
/*  516:     */             }
/*  517: 696 */             if ((a instanceof PasswordProperty)) {
/*  518: 697 */               password = true;
/*  519:     */             }
/*  520: 700 */             if ((a instanceof FilePropertyMetadata)) {
/*  521: 701 */               fileProp = (FilePropertyMetadata)a;
/*  522:     */             }
/*  523:     */           }
/*  524: 704 */           if (!skip)
/*  525:     */           {
/*  526: 709 */             JComponent view = null;
/*  527:     */             try
/*  528:     */             {
/*  529: 713 */               Object value = getter.invoke(this.m_Target, args);
/*  530: 714 */               this.m_Values[sortedPropOrderings[i]] = value;
/*  531:     */               
/*  532: 716 */               PropertyEditor editor = null;
/*  533: 717 */               Class<?> pec = this.m_Properties[sortedPropOrderings[i]].getPropertyEditorClass();
/*  534: 718 */               if (pec != null) {
/*  535:     */                 try
/*  536:     */                 {
/*  537: 720 */                   editor = (PropertyEditor)pec.newInstance();
/*  538:     */                 }
/*  539:     */                 catch (Exception ex) {}
/*  540:     */               }
/*  541: 725 */               if (editor == null) {
/*  542: 726 */                 if ((password) && (String.class.isAssignableFrom(type))) {
/*  543: 727 */                   editor = new PasswordField();
/*  544: 728 */                 } else if ((this.m_useEnvironmentPropertyEditors) && (String.class.isAssignableFrom(type))) {
/*  545: 730 */                   editor = new EnvironmentField();
/*  546: 731 */                 } else if (((this.m_useEnvironmentPropertyEditors) || (fileProp != null)) && (File.class.isAssignableFrom(type)))
/*  547:     */                 {
/*  548: 733 */                   if (fileProp != null) {
/*  549: 734 */                     editor = new FileEnvironmentField("", fileProp.fileChooserDialogType(), fileProp.directoriesOnly());
/*  550:     */                   } else {
/*  551: 736 */                     editor = new FileEnvironmentField();
/*  552:     */                   }
/*  553:     */                 }
/*  554:     */                 else {
/*  555: 739 */                   editor = PropertyEditorManager.findEditor(type);
/*  556:     */                 }
/*  557:     */               }
/*  558: 742 */               this.m_Editors[sortedPropOrderings[i]] = editor;
/*  559: 745 */               if (editor == null) {
/*  560:     */                 continue;
/*  561:     */               }
/*  562: 756 */               if ((editor instanceof GenericObjectEditor)) {
/*  563: 757 */                 ((GenericObjectEditor)editor).setClassType(type);
/*  564:     */               }
/*  565: 760 */               if ((editor instanceof EnvironmentHandler)) {
/*  566: 761 */                 ((EnvironmentHandler)editor).setEnvironment(this.m_env);
/*  567:     */               }
/*  568: 765 */               if (value == null) {
/*  569:     */                 continue;
/*  570:     */               }
/*  571: 777 */               editor.setValue(value);
/*  572: 779 */               if (this.m_TipTexts[sortedPropOrderings[i]] == null)
/*  573:     */               {
/*  574: 781 */                 String tipName = origName + "TipText";
/*  575: 782 */                 for (MethodDescriptor m_Method : this.m_Methods)
/*  576:     */                 {
/*  577: 783 */                   String mname = m_Method.getDisplayName();
/*  578: 784 */                   Method meth = m_Method.getMethod();
/*  579: 785 */                   if ((mname.equals(tipName)) && 
/*  580: 786 */                     (meth.getReturnType().equals(String.class))) {
/*  581:     */                     try
/*  582:     */                     {
/*  583: 788 */                       String tempTip = (String)meth.invoke(this.m_Target, args);
/*  584: 789 */                       int ci = tempTip.indexOf('.');
/*  585: 790 */                       if (ci < 0) {
/*  586: 791 */                         this.m_TipTexts[sortedPropOrderings[i]] = tempTip;
/*  587:     */                       } else {
/*  588: 793 */                         this.m_TipTexts[sortedPropOrderings[i]] = tempTip.substring(0, ci);
/*  589:     */                       }
/*  590:     */                     }
/*  591:     */                     catch (Exception ex) {}
/*  592:     */                   }
/*  593:     */                 }
/*  594:     */               }
/*  595: 812 */               if ((editor.isPaintable()) && (editor.supportsCustomEditor()))
/*  596:     */               {
/*  597: 813 */                 view = new PropertyPanel(editor);
/*  598:     */               }
/*  599: 814 */               else if ((editor.supportsCustomEditor()) && ((editor.getCustomEditor() instanceof JComponent)))
/*  600:     */               {
/*  601: 816 */                 view = (JComponent)editor.getCustomEditor();
/*  602:     */               }
/*  603: 817 */               else if (editor.getTags() != null)
/*  604:     */               {
/*  605: 818 */                 view = new PropertyValueSelector(editor);
/*  606:     */               }
/*  607: 819 */               else if (editor.getAsText() != null)
/*  608:     */               {
/*  609: 820 */                 view = new PropertyText(editor);
/*  610:     */               }
/*  611:     */               else
/*  612:     */               {
/*  613: 822 */                 System.err.println("Warning: Property \"" + name + "\" has non-displayabale editor.  Skipping.");
/*  614:     */                 
/*  615: 824 */                 continue;
/*  616:     */               }
/*  617: 827 */               editor.addPropertyChangeListener(this);
/*  618:     */             }
/*  619:     */             catch (InvocationTargetException ex)
/*  620:     */             {
/*  621: 830 */               System.err.println("Skipping property " + name + " ; exception on target: " + ex.getTargetException());
/*  622:     */               
/*  623: 832 */               ex.getTargetException().printStackTrace();
/*  624: 833 */               continue;
/*  625:     */             }
/*  626:     */             catch (Exception ex)
/*  627:     */             {
/*  628: 835 */               System.err.println("Skipping property " + name + " ; exception: " + ex);
/*  629: 836 */               ex.printStackTrace();
/*  630: 837 */               continue;
/*  631:     */             }
/*  632: 840 */             this.m_Labels[sortedPropOrderings[i]] = new JLabel(name, 4);
/*  633: 841 */             this.m_Labels[sortedPropOrderings[i]].setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 5));
/*  634: 842 */             this.m_Views[sortedPropOrderings[i]] = view;
/*  635: 843 */             GridBagConstraints gbConstraints = new GridBagConstraints();
/*  636: 844 */             gbConstraints.anchor = 13;
/*  637: 845 */             gbConstraints.fill = 2;
/*  638: 846 */             gbConstraints.gridy = (i + componentOffset);
/*  639: 847 */             gbConstraints.gridx = 0;
/*  640: 848 */             gbLayout.setConstraints(this.m_Labels[sortedPropOrderings[i]], gbConstraints);
/*  641: 849 */             scrollablePanel.add(this.m_Labels[sortedPropOrderings[i]]);
/*  642: 850 */             JPanel newPanel = new JPanel();
/*  643: 851 */             if (this.m_TipTexts[sortedPropOrderings[i]] != null)
/*  644:     */             {
/*  645: 852 */               this.m_Views[sortedPropOrderings[i]].setToolTipText(this.m_TipTexts[sortedPropOrderings[i]]);
/*  646: 853 */               this.m_Labels[sortedPropOrderings[i]].setToolTipText(this.m_TipTexts[sortedPropOrderings[i]]);
/*  647:     */             }
/*  648: 855 */             newPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 10));
/*  649: 856 */             newPanel.setLayout(new BorderLayout());
/*  650: 857 */             newPanel.add(this.m_Views[sortedPropOrderings[i]], "Center");
/*  651: 858 */             gbConstraints = new GridBagConstraints();
/*  652: 859 */             gbConstraints.anchor = 17;
/*  653: 860 */             gbConstraints.fill = 1;
/*  654: 861 */             gbConstraints.gridy = (i + componentOffset);
/*  655: 862 */             gbConstraints.gridx = 1;
/*  656: 863 */             gbConstraints.weightx = 100.0D;
/*  657: 864 */             gbLayout.setConstraints(newPanel, gbConstraints);
/*  658: 865 */             scrollablePanel.add(newPanel);
/*  659: 866 */             this.m_NumEditable += 1;
/*  660:     */           }
/*  661:     */         }
/*  662:     */       }
/*  663:     */     }
/*  664: 869 */     if (this.m_NumEditable == 0)
/*  665:     */     {
/*  666: 870 */       JLabel empty = new JLabel("No editable properties", 0);
/*  667: 871 */       Dimension d = empty.getPreferredSize();
/*  668: 872 */       empty.setPreferredSize(new Dimension(d.width * 2, d.height * 2));
/*  669: 873 */       empty.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 10));
/*  670: 874 */       GridBagConstraints gbConstraints = new GridBagConstraints();
/*  671: 875 */       gbConstraints.anchor = 10;
/*  672: 876 */       gbConstraints.fill = 2;
/*  673: 877 */       gbConstraints.gridy = componentOffset;
/*  674: 878 */       gbConstraints.gridx = 0;
/*  675: 879 */       gbLayout.setConstraints(empty, gbConstraints);
/*  676: 880 */       scrollablePanel.add(empty);
/*  677:     */     }
/*  678: 883 */     validate();
/*  679:     */     
/*  680:     */ 
/*  681:     */ 
/*  682:     */ 
/*  683: 888 */     Dimension dim = scrollablePanel.getPreferredSize();
/*  684: 889 */     dim.height += 20;
/*  685: 890 */     dim.width += 20;
/*  686: 891 */     scrollPane.setPreferredSize(dim);
/*  687: 892 */     validate();
/*  688:     */     
/*  689: 894 */     setVisible(true);
/*  690:     */   }
/*  691:     */   
/*  692:     */   protected void openHelpFrame()
/*  693:     */   {
/*  694: 902 */     JTextArea ta = new JTextArea();
/*  695: 903 */     ta.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  696: 904 */     ta.setLineWrap(true);
/*  697: 905 */     ta.setWrapStyleWord(true);
/*  698:     */     
/*  699: 907 */     ta.setEditable(false);
/*  700: 908 */     ta.setText(this.m_HelpText.toString());
/*  701: 909 */     ta.setCaretPosition(0);
/*  702:     */     JDialog jdtmp;
/*  703:     */     JDialog jdtmp;
/*  704: 911 */     if (PropertyDialog.getParentDialog(this) != null)
/*  705:     */     {
/*  706: 912 */       jdtmp = new JDialog(PropertyDialog.getParentDialog(this), "Information");
/*  707:     */     }
/*  708:     */     else
/*  709:     */     {
/*  710:     */       JDialog jdtmp;
/*  711: 913 */       if (PropertyDialog.getParentFrame(this) != null) {
/*  712: 914 */         jdtmp = new JDialog(PropertyDialog.getParentFrame(this), "Information");
/*  713:     */       } else {
/*  714: 916 */         jdtmp = new JDialog(PropertyDialog.getParentDialog(this.m_aboutPanel), "Information");
/*  715:     */       }
/*  716:     */     }
/*  717: 919 */     final JDialog jd = jdtmp;
/*  718: 920 */     jd.addWindowListener(new WindowAdapter()
/*  719:     */     {
/*  720:     */       public void windowClosing(WindowEvent e)
/*  721:     */       {
/*  722: 923 */         jd.dispose();
/*  723: 924 */         if (PropertySheetPanel.this.m_HelpDialog == jd) {
/*  724: 925 */           PropertySheetPanel.this.m_HelpBut.setEnabled(true);
/*  725:     */         }
/*  726:     */       }
/*  727: 928 */     });
/*  728: 929 */     jd.getContentPane().setLayout(new BorderLayout());
/*  729: 930 */     jd.getContentPane().add(new JScrollPane(ta), "Center");
/*  730: 931 */     jd.pack();
/*  731: 932 */     jd.setSize(400, 350);
/*  732: 933 */     jd.setLocation(this.m_aboutPanel.getTopLevelAncestor().getLocationOnScreen().x + this.m_aboutPanel.getTopLevelAncestor().getSize().width, this.m_aboutPanel.getTopLevelAncestor().getLocationOnScreen().y);
/*  733:     */     
/*  734:     */ 
/*  735: 936 */     jd.setVisible(true);
/*  736: 937 */     this.m_HelpDialog = jd;
/*  737:     */   }
/*  738:     */   
/*  739:     */   protected void openCapabilitiesHelpDialog()
/*  740:     */   {
/*  741: 944 */     if (PropertyDialog.getParentDialog(this) != null) {
/*  742: 945 */       this.m_CapabilitiesDialog = new CapabilitiesHelpDialog(PropertyDialog.getParentDialog(this));
/*  743:     */     } else {
/*  744: 948 */       this.m_CapabilitiesDialog = new CapabilitiesHelpDialog(PropertyDialog.getParentFrame(this));
/*  745:     */     }
/*  746: 951 */     this.m_CapabilitiesDialog.setSize(400, 350);
/*  747: 952 */     this.m_CapabilitiesDialog.setLocation(this.m_aboutPanel.getTopLevelAncestor().getLocationOnScreen().x + this.m_aboutPanel.getTopLevelAncestor().getSize().width, this.m_aboutPanel.getTopLevelAncestor().getLocationOnScreen().y);
/*  748:     */     
/*  749:     */ 
/*  750:     */ 
/*  751: 956 */     this.m_CapabilitiesDialog.setVisible(true);
/*  752: 957 */     addPropertyChangeListener(this.m_CapabilitiesDialog);
/*  753:     */   }
/*  754:     */   
/*  755:     */   public int editableProperties()
/*  756:     */   {
/*  757: 967 */     return this.m_NumEditable;
/*  758:     */   }
/*  759:     */   
/*  760:     */   public boolean hasCustomizer()
/*  761:     */   {
/*  762: 976 */     return this.m_Customizer != null;
/*  763:     */   }
/*  764:     */   
/*  765:     */   synchronized void wasModified(PropertyChangeEvent evt)
/*  766:     */   {
/*  767: 988 */     if ((evt.getSource() instanceof PropertyEditor))
/*  768:     */     {
/*  769: 989 */       PropertyEditor editor = (PropertyEditor)evt.getSource();
/*  770: 990 */       for (int i = 0; i < this.m_Editors.length; i++) {
/*  771: 991 */         if (this.m_Editors[i] == editor)
/*  772:     */         {
/*  773: 992 */           PropertyDescriptor property = this.m_Properties[i];
/*  774: 993 */           Object value = editor.getValue();
/*  775: 994 */           this.m_Values[i] = value;
/*  776: 995 */           Method setter = property.getWriteMethod();
/*  777:     */           try
/*  778:     */           {
/*  779: 997 */             Object[] args = { value };
/*  780: 998 */             args[0] = value;
/*  781: 999 */             setter.invoke(this.m_Target, args);
/*  782:     */           }
/*  783:     */           catch (InvocationTargetException ex)
/*  784:     */           {
/*  785:1001 */             if ((ex.getTargetException() instanceof PropertyVetoException))
/*  786:     */             {
/*  787:1002 */               String message = "WARNING: Vetoed; reason is: " + ex.getTargetException().getMessage();
/*  788:     */               
/*  789:1004 */               System.err.println(message);
/*  790:     */               Component jf;
/*  791:     */               Component jf;
/*  792:1007 */               if ((evt.getSource() instanceof JPanel)) {
/*  793:1008 */                 jf = ((JPanel)evt.getSource()).getParent();
/*  794:     */               } else {
/*  795:1010 */                 jf = new JFrame();
/*  796:     */               }
/*  797:1012 */               JOptionPane.showMessageDialog(jf, message, "error", 2);
/*  798:1014 */               if ((jf instanceof JFrame)) {
/*  799:1015 */                 ((JFrame)jf).dispose();
/*  800:     */               }
/*  801:     */             }
/*  802:     */             else
/*  803:     */             {
/*  804:1019 */               System.err.println(ex.getTargetException().getClass().getName() + " while updating " + property.getName() + ": " + ex.getTargetException().getMessage());
/*  805:     */               Component jf;
/*  806:     */               Component jf;
/*  807:1023 */               if ((evt.getSource() instanceof JPanel)) {
/*  808:1024 */                 jf = ((JPanel)evt.getSource()).getParent();
/*  809:     */               } else {
/*  810:1026 */                 jf = new JFrame();
/*  811:     */               }
/*  812:1028 */               JOptionPane.showMessageDialog(jf, ex.getTargetException().getClass().getName() + " while updating " + property.getName() + ":\n" + ex.getTargetException().getMessage(), "error", 2);
/*  813:1035 */               if ((jf instanceof JFrame)) {
/*  814:1036 */                 ((JFrame)jf).dispose();
/*  815:     */               }
/*  816:     */             }
/*  817:     */           }
/*  818:     */           catch (Exception ex)
/*  819:     */           {
/*  820:1041 */             System.err.println("Unexpected exception while updating " + property.getName());
/*  821:     */           }
/*  822:1044 */           if ((this.m_Views[i] == null) || (!(this.m_Views[i] instanceof PropertyPanel))) {
/*  823:     */             break;
/*  824:     */           }
/*  825:1046 */           this.m_Views[i].repaint();
/*  826:1047 */           revalidate(); break;
/*  827:     */         }
/*  828:     */       }
/*  829:     */     }
/*  830:1056 */     for (int i = 0; i < this.m_Properties.length; i++)
/*  831:     */     {
/*  832:     */       Object o;
/*  833:     */       try
/*  834:     */       {
/*  835:1059 */         Method getter = this.m_Properties[i].getReadMethod();
/*  836:1060 */         Method setter = this.m_Properties[i].getWriteMethod();
/*  837:1062 */         if ((getter != null) && (setter == null)) {
/*  838:     */           continue;
/*  839:     */         }
/*  840:1067 */         Object[] args = new Object[0];
/*  841:1068 */         o = getter.invoke(this.m_Target, args);
/*  842:     */       }
/*  843:     */       catch (Exception ex)
/*  844:     */       {
/*  845:1070 */         o = null;
/*  846:     */       }
/*  847:1072 */       if ((o != this.m_Values[i]) && ((o == null) || (!o.equals(this.m_Values[i]))))
/*  848:     */       {
/*  849:1076 */         this.m_Values[i] = o;
/*  850:1078 */         if (this.m_Editors[i] != null)
/*  851:     */         {
/*  852:1082 */           this.m_Editors[i].removePropertyChangeListener(this);
/*  853:1083 */           this.m_Editors[i].setValue(o);
/*  854:1084 */           this.m_Editors[i].addPropertyChangeListener(this);
/*  855:1085 */           if (this.m_Views[i] != null) {
/*  856:1087 */             this.m_Views[i].repaint();
/*  857:     */           }
/*  858:     */         }
/*  859:     */       }
/*  860:     */     }
/*  861:1092 */     if (Beans.isInstanceOf(this.m_Target, Component.class)) {
/*  862:1093 */       ((Component)Beans.getInstanceOf(this.m_Target, Component.class)).repaint();
/*  863:     */     }
/*  864:     */   }
/*  865:     */   
/*  866:     */   public void setEnvironment(Environment env)
/*  867:     */   {
/*  868:1104 */     this.m_env = env;
/*  869:     */   }
/*  870:     */   
/*  871:     */   public void closingOK()
/*  872:     */   {
/*  873:1111 */     if (this.m_Customizer != null) {
/*  874:1115 */       this.m_Customizer.closingOK();
/*  875:     */     }
/*  876:     */   }
/*  877:     */   
/*  878:     */   public void closingCancel()
/*  879:     */   {
/*  880:1127 */     if (this.m_Customizer != null) {
/*  881:1128 */       this.m_Customizer.closingCancel();
/*  882:     */     }
/*  883:     */   }
/*  884:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.PropertySheetPanel
 * JD-Core Version:    0.7.0.1
 */