/*   1:    */ package weka.gui.arffviewer;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.GraphicsConfiguration;
/*   6:    */ import java.awt.Rectangle;
/*   7:    */ import java.awt.event.WindowEvent;
/*   8:    */ import java.awt.event.WindowListener;
/*   9:    */ import java.io.PrintStream;
/*  10:    */ import javax.swing.JFrame;
/*  11:    */ import javax.swing.JTabbedPane;
/*  12:    */ import weka.core.Memory;
/*  13:    */ import weka.core.converters.AbstractFileLoader;
/*  14:    */ import weka.core.logging.Logger;
/*  15:    */ import weka.core.logging.Logger.Level;
/*  16:    */ import weka.gui.ComponentHelper;
/*  17:    */ import weka.gui.LookAndFeel;
/*  18:    */ 
/*  19:    */ public class ArffViewer
/*  20:    */   extends JFrame
/*  21:    */   implements WindowListener
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = -7455845566922685175L;
/*  24:    */   private ArffViewerMainPanel m_MainPanel;
/*  25: 52 */   private static Memory m_Memory = new Memory(true);
/*  26:    */   private static ArffViewer m_Viewer;
/*  27:    */   private static boolean m_FilesLoaded;
/*  28:    */   private static String[] m_Args;
/*  29:    */   
/*  30:    */   public ArffViewer()
/*  31:    */   {
/*  32: 67 */     super("ARFF-Viewer");
/*  33: 68 */     createFrame();
/*  34:    */   }
/*  35:    */   
/*  36:    */   protected void createFrame()
/*  37:    */   {
/*  38: 76 */     setIconImage(ComponentHelper.getImage("weka_icon.gif"));
/*  39: 77 */     setSize(800, 600);
/*  40: 78 */     setCenteredLocation();
/*  41: 79 */     setDefaultCloseOperation(2);
/*  42:    */     
/*  43:    */ 
/*  44: 82 */     removeWindowListener(this);
/*  45:    */     
/*  46: 84 */     addWindowListener(this);
/*  47:    */     
/*  48: 86 */     getContentPane().setLayout(new BorderLayout());
/*  49:    */     
/*  50: 88 */     this.m_MainPanel = new ArffViewerMainPanel(this);
/*  51: 89 */     this.m_MainPanel.setConfirmExit(false);
/*  52: 90 */     getContentPane().add(this.m_MainPanel, "Center");
/*  53:    */     
/*  54: 92 */     setJMenuBar(this.m_MainPanel.getMenu());
/*  55:    */   }
/*  56:    */   
/*  57:    */   protected int getCenteredLeft()
/*  58:    */   {
/*  59:104 */     int width = getBounds().width;
/*  60:105 */     int x = (getGraphicsConfiguration().getBounds().width - width) / 2;
/*  61:107 */     if (x < 0) {
/*  62:108 */       x = 0;
/*  63:    */     }
/*  64:111 */     return x;
/*  65:    */   }
/*  66:    */   
/*  67:    */   protected int getCenteredTop()
/*  68:    */   {
/*  69:123 */     int height = getBounds().height;
/*  70:124 */     int y = (getGraphicsConfiguration().getBounds().height - height) / 2;
/*  71:126 */     if (y < 0) {
/*  72:127 */       y = 0;
/*  73:    */     }
/*  74:130 */     return y;
/*  75:    */   }
/*  76:    */   
/*  77:    */   public void setCenteredLocation()
/*  78:    */   {
/*  79:137 */     setLocation(getCenteredLeft(), getCenteredTop());
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void setConfirmExit(boolean confirm)
/*  83:    */   {
/*  84:146 */     this.m_MainPanel.setConfirmExit(confirm);
/*  85:    */   }
/*  86:    */   
/*  87:    */   public boolean getConfirmExit()
/*  88:    */   {
/*  89:156 */     return this.m_MainPanel.getConfirmExit();
/*  90:    */   }
/*  91:    */   
/*  92:    */   public void setExitOnClose(boolean value)
/*  93:    */   {
/*  94:165 */     this.m_MainPanel.setExitOnClose(value);
/*  95:    */   }
/*  96:    */   
/*  97:    */   public boolean getExitOnClose()
/*  98:    */   {
/*  99:174 */     return this.m_MainPanel.getExitOnClose();
/* 100:    */   }
/* 101:    */   
/* 102:    */   public ArffViewerMainPanel getMainPanel()
/* 103:    */   {
/* 104:183 */     return this.m_MainPanel;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public void refresh()
/* 108:    */   {
/* 109:190 */     validate();
/* 110:191 */     repaint();
/* 111:    */   }
/* 112:    */   
/* 113:    */   public void windowActivated(WindowEvent e) {}
/* 114:    */   
/* 115:    */   public void windowClosed(WindowEvent e) {}
/* 116:    */   
/* 117:    */   public void windowClosing(WindowEvent e)
/* 118:    */   {
/* 119:221 */     while (getMainPanel().getTabbedPane().getTabCount() > 0) {
/* 120:222 */       getMainPanel().closeFile(false);
/* 121:    */     }
/* 122:225 */     if (getConfirmExit())
/* 123:    */     {
/* 124:226 */       int button = ComponentHelper.showMessageBox(this, "Quit - " + getTitle(), "Do you really want to quit?", 0, 3);
/* 125:229 */       if (button == 0) {
/* 126:230 */         dispose();
/* 127:    */       }
/* 128:    */     }
/* 129:    */     else
/* 130:    */     {
/* 131:233 */       dispose();
/* 132:    */     }
/* 133:236 */     if (getExitOnClose()) {
/* 134:237 */       System.exit(0);
/* 135:    */     }
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void windowDeactivated(WindowEvent e) {}
/* 139:    */   
/* 140:    */   public void windowDeiconified(WindowEvent e) {}
/* 141:    */   
/* 142:    */   public void windowIconified(WindowEvent e) {}
/* 143:    */   
/* 144:    */   public void windowOpened(WindowEvent e) {}
/* 145:    */   
/* 146:    */   public String toString()
/* 147:    */   {
/* 148:284 */     return getClass().getName();
/* 149:    */   }
/* 150:    */   
/* 151:    */   public static void main(String[] args)
/* 152:    */     throws Exception
/* 153:    */   {
/* 154:295 */     Logger.log(Logger.Level.INFO, "Logging started");
/* 155:    */     
/* 156:297 */     LookAndFeel.setLookAndFeel();
/* 157:    */     try
/* 158:    */     {
/* 159:303 */       m_Viewer = new ArffViewer();
/* 160:304 */       m_Viewer.setExitOnClose(true);
/* 161:305 */       m_Viewer.setVisible(true);
/* 162:306 */       m_FilesLoaded = false;
/* 163:307 */       m_Args = args;
/* 164:    */       
/* 165:309 */       Thread memMonitor = new Thread()
/* 166:    */       {
/* 167:    */         public void run()
/* 168:    */         {
/* 169:    */           for (;;)
/* 170:    */           {
/* 171:314 */             if ((ArffViewer.m_Args.length > 0) && (!ArffViewer.m_FilesLoaded))
/* 172:    */             {
/* 173:315 */               for (int i = 0; i < ArffViewer.m_Args.length; i++)
/* 174:    */               {
/* 175:316 */                 System.out.println("Loading " + (i + 1) + "/" + ArffViewer.m_Args.length + ": '" + ArffViewer.m_Args[i] + "'...");
/* 176:    */                 
/* 177:318 */                 ArffViewer.m_Viewer.getMainPanel().loadFile(ArffViewer.m_Args[i], new AbstractFileLoader[0]);
/* 178:    */               }
/* 179:320 */               ArffViewer.m_Viewer.getMainPanel().getTabbedPane().setSelectedIndex(0);
/* 180:321 */               System.out.println("Finished!");
/* 181:322 */               ArffViewer.access$102(true);
/* 182:    */             }
/* 183:328 */             if (ArffViewer.m_Memory.isOutOfMemory())
/* 184:    */             {
/* 185:330 */               ArffViewer.m_Viewer.dispose();
/* 186:331 */               ArffViewer.access$202(null);
/* 187:332 */               System.gc();
/* 188:    */               
/* 189:    */ 
/* 190:335 */               System.err.println("\ndisplayed message:");
/* 191:336 */               ArffViewer.m_Memory.showOutOfMemory();
/* 192:337 */               System.err.println("\nrestarting...");
/* 193:    */               
/* 194:    */ 
/* 195:340 */               System.gc();
/* 196:341 */               ArffViewer.access$202(new ArffViewer());
/* 197:342 */               ArffViewer.m_Viewer.setExitOnClose(true);
/* 198:343 */               ArffViewer.m_Viewer.setVisible(true);
/* 199:    */             }
/* 200:    */           }
/* 201:    */         }
/* 202:353 */       };
/* 203:354 */       memMonitor.setPriority(5);
/* 204:355 */       memMonitor.start();
/* 205:    */     }
/* 206:    */     catch (Exception ex)
/* 207:    */     {
/* 208:357 */       ex.printStackTrace();
/* 209:358 */       System.err.println(ex.getMessage());
/* 210:    */     }
/* 211:    */   }
/* 212:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.arffviewer.ArffViewer
 * JD-Core Version:    0.7.0.1
 */