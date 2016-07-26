/*   1:    */ package weka.gui.explorer;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Image;
/*   6:    */ import java.awt.Toolkit;
/*   7:    */ import java.awt.event.WindowAdapter;
/*   8:    */ import java.awt.event.WindowEvent;
/*   9:    */ import java.beans.PropertyChangeEvent;
/*  10:    */ import java.beans.PropertyChangeListener;
/*  11:    */ import java.io.File;
/*  12:    */ import java.io.PrintStream;
/*  13:    */ import java.text.SimpleDateFormat;
/*  14:    */ import java.util.Date;
/*  15:    */ import java.util.EventListener;
/*  16:    */ import java.util.HashSet;
/*  17:    */ import java.util.Hashtable;
/*  18:    */ import java.util.Vector;
/*  19:    */ import javax.swing.JFrame;
/*  20:    */ import javax.swing.JPanel;
/*  21:    */ import javax.swing.JTabbedPane;
/*  22:    */ import javax.swing.event.ChangeEvent;
/*  23:    */ import weka.core.Capabilities;
/*  24:    */ import weka.core.Copyright;
/*  25:    */ import weka.core.Instances;
/*  26:    */ import weka.core.Memory;
/*  27:    */ import weka.core.PluginManager;
/*  28:    */ import weka.core.converters.AbstractFileLoader;
/*  29:    */ import weka.core.converters.ConverterUtils;
/*  30:    */ import weka.core.logging.Logger.Level;
/*  31:    */ import weka.gui.GenericObjectEditor;
/*  32:    */ import weka.gui.LogPanel;
/*  33:    */ import weka.gui.LookAndFeel;
/*  34:    */ import weka.gui.WekaTaskMonitor;
/*  35:    */ 
/*  36:    */ public class Explorer
/*  37:    */   extends JPanel
/*  38:    */ {
/*  39:    */   private static final long serialVersionUID = -7674003708867909578L;
/*  40:    */   
/*  41:    */   public static abstract interface LogHandler
/*  42:    */   {
/*  43:    */     public abstract void setLog(weka.gui.Logger paramLogger);
/*  44:    */   }
/*  45:    */   
/*  46:    */   public static abstract interface ExplorerPanel
/*  47:    */   {
/*  48:    */     public abstract void setExplorer(Explorer paramExplorer);
/*  49:    */     
/*  50:    */     public abstract Explorer getExplorer();
/*  51:    */     
/*  52:    */     public abstract void setInstances(Instances paramInstances);
/*  53:    */     
/*  54:    */     public abstract String getTabTitle();
/*  55:    */     
/*  56:    */     public abstract String getTabTitleToolTip();
/*  57:    */   }
/*  58:    */   
/*  59:    */   public static class CapabilitiesFilterChangeEvent
/*  60:    */     extends ChangeEvent
/*  61:    */   {
/*  62:    */     private static final long serialVersionUID = 1194260517270385559L;
/*  63:    */     protected Capabilities m_Filter;
/*  64:    */     
/*  65:    */     public CapabilitiesFilterChangeEvent(Object source, Capabilities filter)
/*  66:    */     {
/*  67:109 */       super();
/*  68:110 */       this.m_Filter = filter;
/*  69:    */     }
/*  70:    */     
/*  71:    */     public Capabilities getFilter()
/*  72:    */     {
/*  73:119 */       return this.m_Filter;
/*  74:    */     }
/*  75:    */   }
/*  76:    */   
/*  77:185 */   protected PreprocessPanel m_PreprocessPanel = new PreprocessPanel();
/*  78:188 */   protected Vector<ExplorerPanel> m_Panels = new Vector();
/*  79:191 */   protected JTabbedPane m_TabbedPane = new JTabbedPane();
/*  80:194 */   protected LogPanel m_LogPanel = new LogPanel(new WekaTaskMonitor());
/*  81:197 */   protected HashSet<CapabilitiesFilterChangeListener> m_CapabilitiesFilterChangeListeners = new HashSet();
/*  82:    */   private static Explorer m_explorer;
/*  83:    */   
/*  84:    */   public Explorer()
/*  85:    */   {
/*  86:204 */     String date = new SimpleDateFormat("EEEE, d MMMM yyyy").format(new Date());
/*  87:    */     
/*  88:206 */     this.m_LogPanel.logMessage("Weka Explorer");
/*  89:207 */     this.m_LogPanel.logMessage("(c) " + Copyright.getFromYear() + "-" + Copyright.getToYear() + " " + Copyright.getOwner() + ", " + Copyright.getAddress());
/*  90:    */     
/*  91:    */ 
/*  92:210 */     this.m_LogPanel.logMessage("web: " + Copyright.getURL());
/*  93:211 */     this.m_LogPanel.logMessage("Started on " + date);
/*  94:212 */     this.m_LogPanel.statusMessage("Welcome to the Weka Explorer");
/*  95:    */     
/*  96:    */ 
/*  97:215 */     this.m_PreprocessPanel.setLog(this.m_LogPanel);
/*  98:216 */     this.m_TabbedPane.addTab(this.m_PreprocessPanel.getTabTitle(), null, this.m_PreprocessPanel, this.m_PreprocessPanel.getTabTitleToolTip());
/*  99:    */     
/* 100:    */ 
/* 101:    */ 
/* 102:220 */     String[] tabs = ExplorerDefaults.getTabs();
/* 103:221 */     Hashtable<String, HashSet<String>> tabOptions = new Hashtable();
/* 104:222 */     for (String tab : tabs) {
/* 105:    */       try
/* 106:    */       {
/* 107:225 */         String[] optionsStr = tab.split(":");
/* 108:226 */         String classname = optionsStr[0];
/* 109:227 */         if (!PluginManager.isInDisabledList(classname))
/* 110:    */         {
/* 111:230 */           HashSet<String> options = new HashSet();
/* 112:231 */           tabOptions.put(classname, options);
/* 113:232 */           for (int n = 1; n < optionsStr.length; n++) {
/* 114:233 */             options.add(optionsStr[n]);
/* 115:    */           }
/* 116:237 */           ExplorerPanel panel = (ExplorerPanel)Class.forName(classname).newInstance();
/* 117:    */           
/* 118:239 */           panel.setExplorer(this);
/* 119:240 */           this.m_Panels.add(panel);
/* 120:241 */           if ((panel instanceof LogHandler)) {
/* 121:242 */             ((LogHandler)panel).setLog(this.m_LogPanel);
/* 122:    */           }
/* 123:244 */           this.m_TabbedPane.addTab(panel.getTabTitle(), null, (JPanel)panel, panel.getTabTitleToolTip());
/* 124:    */         }
/* 125:    */       }
/* 126:    */       catch (Exception e)
/* 127:    */       {
/* 128:247 */         e.printStackTrace();
/* 129:    */       }
/* 130:    */     }
/* 131:252 */     this.m_TabbedPane.setSelectedIndex(0);
/* 132:253 */     for (int i = 0; i < this.m_Panels.size(); i++)
/* 133:    */     {
/* 134:254 */       HashSet<String> options = (HashSet)tabOptions.get(((ExplorerPanel)this.m_Panels.get(i)).getClass().getName());
/* 135:    */       
/* 136:256 */       this.m_TabbedPane.setEnabledAt(i + 1, options.contains("standalone"));
/* 137:    */     }
/* 138:260 */     this.m_PreprocessPanel.addPropertyChangeListener(new PropertyChangeListener()
/* 139:    */     {
/* 140:    */       public void propertyChange(PropertyChangeEvent e)
/* 141:    */       {
/* 142:263 */         for (int i = 0; i < Explorer.this.m_Panels.size(); i++)
/* 143:    */         {
/* 144:264 */           ((Explorer.ExplorerPanel)Explorer.this.m_Panels.get(i)).setInstances(Explorer.this.m_PreprocessPanel.getInstances());
/* 145:265 */           Explorer.this.m_TabbedPane.setEnabledAt(i + 1, true);
/* 146:    */         }
/* 147:    */       }
/* 148:270 */     });
/* 149:271 */     this.m_PreprocessPanel.setExplorer(this);
/* 150:272 */     addCapabilitiesFilterListener(this.m_PreprocessPanel);
/* 151:273 */     for (int i = 0; i < this.m_Panels.size(); i++) {
/* 152:274 */       if ((this.m_Panels.get(i) instanceof CapabilitiesFilterChangeListener)) {
/* 153:275 */         addCapabilitiesFilterListener((CapabilitiesFilterChangeListener)this.m_Panels.get(i));
/* 154:    */       }
/* 155:    */     }
/* 156:281 */     setLayout(new BorderLayout());
/* 157:282 */     add(this.m_TabbedPane, "Center");
/* 158:283 */     add(this.m_LogPanel, "South");
/* 159:    */   }
/* 160:    */   
/* 161:    */   public Vector<ExplorerPanel> getPanels()
/* 162:    */   {
/* 163:292 */     return this.m_Panels;
/* 164:    */   }
/* 165:    */   
/* 166:    */   public PreprocessPanel getPreprocessPanel()
/* 167:    */   {
/* 168:302 */     return this.m_PreprocessPanel;
/* 169:    */   }
/* 170:    */   
/* 171:    */   public JTabbedPane getTabbedPane()
/* 172:    */   {
/* 173:311 */     return this.m_TabbedPane;
/* 174:    */   }
/* 175:    */   
/* 176:    */   public void addCapabilitiesFilterListener(CapabilitiesFilterChangeListener l)
/* 177:    */   {
/* 178:322 */     this.m_CapabilitiesFilterChangeListeners.add(l);
/* 179:    */   }
/* 180:    */   
/* 181:    */   public boolean removeCapabilitiesFilterListener(CapabilitiesFilterChangeListener l)
/* 182:    */   {
/* 183:333 */     return this.m_CapabilitiesFilterChangeListeners.remove(l);
/* 184:    */   }
/* 185:    */   
/* 186:    */   public void notifyCapabilitiesFilterListener(Capabilities filter)
/* 187:    */   {
/* 188:342 */     for (CapabilitiesFilterChangeListener l : this.m_CapabilitiesFilterChangeListeners) {
/* 189:343 */       if (l != this) {
/* 190:346 */         l.capabilitiesFilterChanged(new CapabilitiesFilterChangeEvent(this, filter));
/* 191:    */       }
/* 192:    */     }
/* 193:    */   }
/* 194:    */   
/* 195:358 */   protected static Memory m_Memory = new Memory(true);
/* 196:    */   
/* 197:    */   public static void main(String[] args)
/* 198:    */   {
/* 199:367 */     weka.core.logging.Logger.log(Logger.Level.INFO, "Logging started");
/* 200:    */     
/* 201:    */ 
/* 202:370 */     LookAndFeel.setLookAndFeel();
/* 203:    */     
/* 204:    */ 
/* 205:373 */     GenericObjectEditor.determineClasses();
/* 206:    */     try
/* 207:    */     {
/* 208:379 */       m_explorer = new Explorer();
/* 209:380 */       JFrame jf = new JFrame("Weka Explorer");
/* 210:381 */       jf.getContentPane().setLayout(new BorderLayout());
/* 211:382 */       jf.getContentPane().add(m_explorer, "Center");
/* 212:383 */       jf.addWindowListener(new WindowAdapter()
/* 213:    */       {
/* 214:    */         public void windowClosing(WindowEvent e)
/* 215:    */         {
/* 216:386 */           this.val$jf.dispose();
/* 217:387 */           System.exit(0);
/* 218:    */         }
/* 219:389 */       });
/* 220:390 */       jf.pack();
/* 221:391 */       jf.setSize(800, 600);
/* 222:392 */       jf.setVisible(true);
/* 223:393 */       Image icon = Toolkit.getDefaultToolkit().getImage(m_explorer.getClass().getClassLoader().getResource("weka/gui/weka_icon_new_48.png"));
/* 224:    */       
/* 225:    */ 
/* 226:396 */       jf.setIconImage(icon);
/* 227:398 */       if (args.length == 1)
/* 228:    */       {
/* 229:399 */         System.err.println("Loading instances from " + args[0]);
/* 230:400 */         AbstractFileLoader loader = ConverterUtils.getLoaderForFile(args[0]);
/* 231:401 */         loader.setFile(new File(args[0]));
/* 232:402 */         m_explorer.m_PreprocessPanel.setInstancesFromFile(loader);
/* 233:    */       }
/* 234:405 */       Thread memMonitor = new Thread()
/* 235:    */       {
/* 236:    */         public void run()
/* 237:    */         {
/* 238:    */           for (;;)
/* 239:    */           {
/* 240:413 */             if (Explorer.m_Memory.isOutOfMemory())
/* 241:    */             {
/* 242:415 */               this.val$jf.dispose();
/* 243:416 */               Explorer.access$002(null);
/* 244:417 */               System.gc();
/* 245:    */               
/* 246:    */ 
/* 247:420 */               System.err.println("\ndisplayed message:");
/* 248:421 */               Explorer.m_Memory.showOutOfMemory();
/* 249:422 */               System.err.println("\nexiting");
/* 250:423 */               System.exit(-1);
/* 251:    */             }
/* 252:    */           }
/* 253:    */         }
/* 254:432 */       };
/* 255:433 */       memMonitor.setPriority(10);
/* 256:434 */       memMonitor.start();
/* 257:    */     }
/* 258:    */     catch (Exception ex)
/* 259:    */     {
/* 260:436 */       ex.printStackTrace();
/* 261:437 */       System.err.println(ex.getMessage());
/* 262:    */     }
/* 263:    */   }
/* 264:    */   
/* 265:    */   public static abstract interface CapabilitiesFilterChangeListener
/* 266:    */     extends EventListener
/* 267:    */   {
/* 268:    */     public abstract void capabilitiesFilterChanged(Explorer.CapabilitiesFilterChangeEvent paramCapabilitiesFilterChangeEvent);
/* 269:    */   }
/* 270:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.explorer.Explorer
 * JD-Core Version:    0.7.0.1
 */