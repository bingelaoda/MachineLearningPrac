/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.GraphicsEnvironment;
/*   6:    */ import java.awt.event.MouseEvent;
/*   7:    */ import java.awt.event.WindowAdapter;
/*   8:    */ import java.awt.event.WindowEvent;
/*   9:    */ import java.beans.VetoableChangeListener;
/*  10:    */ import java.beans.beancontext.BeanContext;
/*  11:    */ import java.beans.beancontext.BeanContextChild;
/*  12:    */ import java.beans.beancontext.BeanContextChildSupport;
/*  13:    */ import java.io.PrintStream;
/*  14:    */ import java.io.Serializable;
/*  15:    */ import java.text.SimpleDateFormat;
/*  16:    */ import java.util.ArrayList;
/*  17:    */ import java.util.Date;
/*  18:    */ import java.util.Enumeration;
/*  19:    */ import java.util.Vector;
/*  20:    */ import javax.swing.BorderFactory;
/*  21:    */ import javax.swing.JFrame;
/*  22:    */ import javax.swing.JList;
/*  23:    */ import javax.swing.JPanel;
/*  24:    */ import weka.gui.ResultHistoryPanel;
/*  25:    */ import weka.gui.ResultHistoryPanel.RMouseAdapter;
/*  26:    */ import weka.gui.graphvisualizer.BIFFormatException;
/*  27:    */ import weka.gui.graphvisualizer.GraphVisualizer;
/*  28:    */ import weka.gui.treevisualizer.PlaceNode2;
/*  29:    */ import weka.gui.treevisualizer.TreeVisualizer;
/*  30:    */ 
/*  31:    */ public class GraphViewer
/*  32:    */   extends JPanel
/*  33:    */   implements Visible, GraphListener, UserRequestAcceptor, Serializable, BeanContextChild
/*  34:    */ {
/*  35:    */   private static final long serialVersionUID = -5183121972114900617L;
/*  36:    */   protected BeanVisual m_visual;
/*  37: 63 */   private transient JFrame m_resultsFrame = null;
/*  38:    */   protected transient ResultHistoryPanel m_history;
/*  39: 70 */   protected transient BeanContext m_beanContext = null;
/*  40: 75 */   protected BeanContextChildSupport m_bcSupport = new BeanContextChildSupport(this);
/*  41:    */   protected boolean m_design;
/*  42:    */   
/*  43:    */   public GraphViewer()
/*  44:    */   {
/*  45: 89 */     if (!GraphicsEnvironment.isHeadless()) {
/*  46: 90 */       appearanceFinal();
/*  47:    */     }
/*  48:    */   }
/*  49:    */   
/*  50:    */   protected void appearanceDesign()
/*  51:    */   {
/*  52: 95 */     setUpResultHistory();
/*  53: 96 */     removeAll();
/*  54: 97 */     this.m_visual = new BeanVisual("GraphViewer", "weka/gui/beans/icons/DefaultGraph.gif", "weka/gui/beans/icons/DefaultGraph_animated.gif");
/*  55:    */     
/*  56: 99 */     setLayout(new BorderLayout());
/*  57:100 */     add(this.m_visual, "Center");
/*  58:    */   }
/*  59:    */   
/*  60:    */   protected void appearanceFinal()
/*  61:    */   {
/*  62:104 */     removeAll();
/*  63:105 */     setLayout(new BorderLayout());
/*  64:106 */     setUpFinal();
/*  65:    */   }
/*  66:    */   
/*  67:    */   protected void setUpFinal()
/*  68:    */   {
/*  69:110 */     setUpResultHistory();
/*  70:111 */     add(this.m_history, "Center");
/*  71:    */   }
/*  72:    */   
/*  73:    */   public String globalInfo()
/*  74:    */   {
/*  75:120 */     return "Graphically visualize trees or graphs produced by classifiers/clusterers.";
/*  76:    */   }
/*  77:    */   
/*  78:    */   private void setUpResultHistory()
/*  79:    */   {
/*  80:124 */     if (this.m_history == null) {
/*  81:125 */       this.m_history = new ResultHistoryPanel(null);
/*  82:    */     }
/*  83:127 */     this.m_history.setBorder(BorderFactory.createTitledBorder("Graph list"));
/*  84:128 */     this.m_history.setHandleRightClicks(false);
/*  85:129 */     this.m_history.getList().addMouseListener(new ResultHistoryPanel.RMouseAdapter()
/*  86:    */     {
/*  87:    */       private static final long serialVersionUID = -4984130887963944249L;
/*  88:    */       
/*  89:    */       public void mouseClicked(MouseEvent e)
/*  90:    */       {
/*  91:136 */         int index = GraphViewer.this.m_history.getList().locationToIndex(e.getPoint());
/*  92:137 */         if (index != -1)
/*  93:    */         {
/*  94:138 */           String name = GraphViewer.this.m_history.getNameAtIndex(index);
/*  95:139 */           GraphViewer.this.doPopup(name);
/*  96:    */         }
/*  97:    */       }
/*  98:    */     });
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void setBeanContext(BeanContext bc)
/* 102:    */   {
/* 103:152 */     this.m_beanContext = bc;
/* 104:153 */     this.m_design = this.m_beanContext.isDesignTime();
/* 105:154 */     if (this.m_design) {
/* 106:155 */       appearanceDesign();
/* 107:157 */     } else if (!GraphicsEnvironment.isHeadless()) {
/* 108:158 */       appearanceFinal();
/* 109:    */     }
/* 110:    */   }
/* 111:    */   
/* 112:    */   public BeanContext getBeanContext()
/* 113:    */   {
/* 114:170 */     return this.m_beanContext;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public void addVetoableChangeListener(String name, VetoableChangeListener vcl)
/* 118:    */   {
/* 119:181 */     this.m_bcSupport.addVetoableChangeListener(name, vcl);
/* 120:    */   }
/* 121:    */   
/* 122:    */   public void removeVetoableChangeListener(String name, VetoableChangeListener vcl)
/* 123:    */   {
/* 124:193 */     this.m_bcSupport.removeVetoableChangeListener(name, vcl);
/* 125:    */   }
/* 126:    */   
/* 127:    */   public synchronized void acceptGraph(GraphEvent e)
/* 128:    */   {
/* 129:204 */     ArrayList<Object> graphInfo = new ArrayList();
/* 130:206 */     if (this.m_history == null) {
/* 131:207 */       setUpResultHistory();
/* 132:    */     }
/* 133:209 */     String name = new SimpleDateFormat("HH:mm:ss - ").format(new Date());
/* 134:    */     
/* 135:211 */     name = name + e.getGraphTitle();
/* 136:212 */     graphInfo.add(new Integer(e.getGraphType()));
/* 137:213 */     graphInfo.add(e.getGraphString());
/* 138:214 */     this.m_history.addResult(name, new StringBuffer());
/* 139:215 */     this.m_history.addObject(name, graphInfo);
/* 140:    */   }
/* 141:    */   
/* 142:    */   public void setVisual(BeanVisual newVisual)
/* 143:    */   {
/* 144:225 */     this.m_visual = newVisual;
/* 145:    */   }
/* 146:    */   
/* 147:    */   public BeanVisual getVisual()
/* 148:    */   {
/* 149:234 */     return this.m_visual;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public void useDefaultVisual()
/* 153:    */   {
/* 154:243 */     this.m_visual.loadIcons("weka/gui/beans/icons/DefaultGraph.gif", "weka/gui/beans/icons/DefaultGraph_animated.gif");
/* 155:    */   }
/* 156:    */   
/* 157:    */   public void showResults()
/* 158:    */   {
/* 159:252 */     if (this.m_resultsFrame == null)
/* 160:    */     {
/* 161:253 */       if (this.m_history == null) {
/* 162:254 */         setUpResultHistory();
/* 163:    */       }
/* 164:256 */       this.m_resultsFrame = new JFrame("Graph Viewer");
/* 165:257 */       this.m_resultsFrame.getContentPane().setLayout(new BorderLayout());
/* 166:258 */       this.m_resultsFrame.getContentPane().add(this.m_history, "Center");
/* 167:259 */       this.m_resultsFrame.addWindowListener(new WindowAdapter()
/* 168:    */       {
/* 169:    */         public void windowClosing(WindowEvent e)
/* 170:    */         {
/* 171:262 */           GraphViewer.this.m_resultsFrame.dispose();
/* 172:263 */           GraphViewer.this.m_resultsFrame = null;
/* 173:    */         }
/* 174:265 */       });
/* 175:266 */       this.m_resultsFrame.pack();
/* 176:267 */       this.m_resultsFrame.setVisible(true);
/* 177:    */     }
/* 178:    */     else
/* 179:    */     {
/* 180:269 */       this.m_resultsFrame.toFront();
/* 181:    */     }
/* 182:    */   }
/* 183:    */   
/* 184:    */   private void doPopup(String name)
/* 185:    */   {
/* 186:280 */     ArrayList<Object> graph = (ArrayList)this.m_history.getNamedObject(name);
/* 187:281 */     int grphType = ((Integer)graph.get(0)).intValue();
/* 188:282 */     String grphString = (String)graph.get(graph.size() - 1);
/* 189:284 */     if (grphType == 1)
/* 190:    */     {
/* 191:285 */       final JFrame jf = new JFrame("Weka Classifier Tree Visualizer: " + name);
/* 192:    */       
/* 193:287 */       jf.setSize(500, 400);
/* 194:288 */       jf.getContentPane().setLayout(new BorderLayout());
/* 195:289 */       TreeVisualizer tv = new TreeVisualizer(null, grphString, new PlaceNode2());
/* 196:290 */       jf.getContentPane().add(tv, "Center");
/* 197:291 */       jf.addWindowListener(new WindowAdapter()
/* 198:    */       {
/* 199:    */         public void windowClosing(WindowEvent e)
/* 200:    */         {
/* 201:294 */           jf.dispose();
/* 202:    */         }
/* 203:297 */       });
/* 204:298 */       jf.setVisible(true);
/* 205:    */     }
/* 206:300 */     if (grphType == 2)
/* 207:    */     {
/* 208:301 */       final JFrame jf = new JFrame("Weka Classifier Graph Visualizer: " + name);
/* 209:    */       
/* 210:303 */       jf.setSize(500, 400);
/* 211:304 */       jf.getContentPane().setLayout(new BorderLayout());
/* 212:305 */       GraphVisualizer gv = new GraphVisualizer();
/* 213:    */       try
/* 214:    */       {
/* 215:307 */         gv.readBIF(grphString);
/* 216:    */       }
/* 217:    */       catch (BIFFormatException be)
/* 218:    */       {
/* 219:309 */         System.err.println("unable to visualize BayesNet");
/* 220:310 */         be.printStackTrace();
/* 221:    */       }
/* 222:312 */       gv.layoutGraph();
/* 223:313 */       jf.getContentPane().add(gv, "Center");
/* 224:314 */       jf.addWindowListener(new WindowAdapter()
/* 225:    */       {
/* 226:    */         public void windowClosing(WindowEvent e)
/* 227:    */         {
/* 228:317 */           jf.dispose();
/* 229:    */         }
/* 230:320 */       });
/* 231:321 */       jf.setVisible(true);
/* 232:    */     }
/* 233:    */   }
/* 234:    */   
/* 235:    */   public Enumeration<String> enumerateRequests()
/* 236:    */   {
/* 237:332 */     Vector<String> newVector = new Vector(0);
/* 238:333 */     newVector.addElement("Show results");
/* 239:    */     
/* 240:335 */     return newVector.elements();
/* 241:    */   }
/* 242:    */   
/* 243:    */   public void performRequest(String request)
/* 244:    */   {
/* 245:346 */     if (request.compareTo("Show results") == 0) {
/* 246:347 */       showResults();
/* 247:    */     } else {
/* 248:349 */       throw new IllegalArgumentException(request + " not supported (GraphViewer)");
/* 249:    */     }
/* 250:    */   }
/* 251:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.GraphViewer
 * JD-Core Version:    0.7.0.1
 */