/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Container;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.Font;
/*   7:    */ import java.awt.Point;
/*   8:    */ import java.awt.event.ActionEvent;
/*   9:    */ import java.awt.event.ActionListener;
/*  10:    */ import java.awt.event.MouseAdapter;
/*  11:    */ import java.awt.event.MouseEvent;
/*  12:    */ import java.awt.event.WindowAdapter;
/*  13:    */ import java.awt.event.WindowEvent;
/*  14:    */ import java.io.PrintStream;
/*  15:    */ import java.text.SimpleDateFormat;
/*  16:    */ import java.util.Date;
/*  17:    */ import javax.swing.BorderFactory;
/*  18:    */ import javax.swing.JButton;
/*  19:    */ import javax.swing.JFrame;
/*  20:    */ import javax.swing.JLabel;
/*  21:    */ import javax.swing.JMenuItem;
/*  22:    */ import javax.swing.JPanel;
/*  23:    */ import javax.swing.JPopupMenu;
/*  24:    */ import javax.swing.JScrollPane;
/*  25:    */ import javax.swing.JTextArea;
/*  26:    */ import javax.swing.JViewport;
/*  27:    */ import javax.swing.event.ChangeEvent;
/*  28:    */ import javax.swing.event.ChangeListener;
/*  29:    */ import weka.core.logging.Logger.Level;
/*  30:    */ 
/*  31:    */ public class LogPanel
/*  32:    */   extends JPanel
/*  33:    */   implements Logger, TaskLogger
/*  34:    */ {
/*  35:    */   private static final long serialVersionUID = -4072464549112439484L;
/*  36: 63 */   protected JLabel m_StatusLab = new JLabel("OK");
/*  37: 66 */   protected JTextArea m_LogText = new JTextArea(4, 20);
/*  38: 69 */   protected JButton m_logButton = new JButton("Log");
/*  39: 72 */   protected boolean m_First = true;
/*  40: 75 */   protected WekaTaskMonitor m_TaskMonitor = null;
/*  41:    */   
/*  42:    */   public LogPanel()
/*  43:    */   {
/*  44: 82 */     this(null, false, false, true);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public LogPanel(WekaTaskMonitor tm)
/*  48:    */   {
/*  49: 92 */     this(tm, true, false, true);
/*  50:    */   }
/*  51:    */   
/*  52:    */   public LogPanel(WekaTaskMonitor tm, boolean logHidden)
/*  53:    */   {
/*  54:104 */     this(tm, logHidden, false, true);
/*  55:    */   }
/*  56:    */   
/*  57:    */   public LogPanel(WekaTaskMonitor tm, boolean logHidden, boolean statusHidden, boolean titledBorder)
/*  58:    */   {
/*  59:122 */     this.m_TaskMonitor = tm;
/*  60:123 */     this.m_LogText.setEditable(false);
/*  61:124 */     this.m_LogText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
/*  62:125 */     this.m_StatusLab.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder("Status"), BorderFactory.createEmptyBorder(0, 5, 5, 5)));
/*  63:    */     
/*  64:    */ 
/*  65:    */ 
/*  66:    */ 
/*  67:130 */     JScrollPane js = new JScrollPane(this.m_LogText);
/*  68:131 */     js.getViewport().addChangeListener(new ChangeListener()
/*  69:    */     {
/*  70:    */       private int lastHeight;
/*  71:    */       
/*  72:    */       public void stateChanged(ChangeEvent e)
/*  73:    */       {
/*  74:135 */         JViewport vp = (JViewport)e.getSource();
/*  75:136 */         int h = vp.getViewSize().height;
/*  76:137 */         if (h != this.lastHeight)
/*  77:    */         {
/*  78:138 */           this.lastHeight = h;
/*  79:139 */           int x = h - vp.getExtentSize().height;
/*  80:140 */           vp.setViewPosition(new Point(0, x));
/*  81:    */         }
/*  82:    */       }
/*  83:    */     });
/*  84:145 */     if (logHidden)
/*  85:    */     {
/*  86:148 */       final JFrame jf = new JFrame("Log");
/*  87:149 */       jf.addWindowListener(new WindowAdapter()
/*  88:    */       {
/*  89:    */         public void windowClosing(WindowEvent e)
/*  90:    */         {
/*  91:151 */           jf.setVisible(false);
/*  92:    */         }
/*  93:153 */       });
/*  94:154 */       jf.getContentPane().setLayout(new BorderLayout());
/*  95:155 */       jf.getContentPane().add(js, "Center");
/*  96:156 */       jf.pack();
/*  97:157 */       jf.setSize(450, 350);
/*  98:    */       
/*  99:    */ 
/* 100:160 */       this.m_logButton.addActionListener(new ActionListener()
/* 101:    */       {
/* 102:    */         public void actionPerformed(ActionEvent e)
/* 103:    */         {
/* 104:162 */           jf.setVisible(true);
/* 105:    */         }
/* 106:166 */       });
/* 107:167 */       setLayout(new BorderLayout());
/* 108:168 */       JPanel logButPanel = new JPanel();
/* 109:169 */       logButPanel.setLayout(new BorderLayout());
/* 110:170 */       logButPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
/* 111:171 */       logButPanel.add(this.m_logButton, "Center");
/* 112:172 */       JPanel p1 = new JPanel();
/* 113:173 */       p1.setLayout(new BorderLayout());
/* 114:174 */       p1.add(this.m_StatusLab, "Center");
/* 115:175 */       p1.add(logButPanel, "East");
/* 116:177 */       if (tm == null)
/* 117:    */       {
/* 118:178 */         add(p1, "South");
/* 119:    */       }
/* 120:    */       else
/* 121:    */       {
/* 122:180 */         JPanel p2 = new JPanel();
/* 123:181 */         p2.setLayout(new BorderLayout());
/* 124:182 */         p2.add(p1, "Center");
/* 125:183 */         p2.add(this.m_TaskMonitor, "East");
/* 126:184 */         add(p2, "South");
/* 127:    */       }
/* 128:    */     }
/* 129:    */     else
/* 130:    */     {
/* 131:189 */       JPanel p1 = new JPanel();
/* 132:190 */       if (titledBorder) {
/* 133:191 */         p1.setBorder(BorderFactory.createTitledBorder("Log"));
/* 134:    */       }
/* 135:193 */       p1.setLayout(new BorderLayout());
/* 136:194 */       p1.add(js, "Center");
/* 137:195 */       setLayout(new BorderLayout());
/* 138:196 */       add(p1, "Center");
/* 139:198 */       if (tm == null)
/* 140:    */       {
/* 141:199 */         if (!statusHidden) {
/* 142:200 */           add(this.m_StatusLab, "South");
/* 143:    */         }
/* 144:    */       }
/* 145:203 */       else if (!statusHidden)
/* 146:    */       {
/* 147:204 */         JPanel p2 = new JPanel();
/* 148:205 */         p2.setLayout(new BorderLayout());
/* 149:206 */         p2.add(this.m_StatusLab, "Center");
/* 150:207 */         p2.add(this.m_TaskMonitor, "East");
/* 151:208 */         add(p2, "South");
/* 152:    */       }
/* 153:    */     }
/* 154:212 */     addPopup();
/* 155:    */   }
/* 156:    */   
/* 157:    */   public void setLoggingFontSize(int size)
/* 158:    */   {
/* 159:222 */     if (size > 0)
/* 160:    */     {
/* 161:223 */       this.m_LogText.setFont(new Font(null, 0, size));
/* 162:    */     }
/* 163:    */     else
/* 164:    */     {
/* 165:225 */       Font temp = new JTextArea().getFont();
/* 166:226 */       this.m_LogText.setFont(temp);
/* 167:    */     }
/* 168:    */   }
/* 169:    */   
/* 170:    */   private String printLong(long l)
/* 171:    */   {
/* 172:242 */     String str = Long.toString(l);
/* 173:243 */     String result = "";
/* 174:244 */     int count = 0;
/* 175:246 */     for (int i = str.length() - 1; i >= 0; i--)
/* 176:    */     {
/* 177:247 */       count++;
/* 178:248 */       result = str.charAt(i) + result;
/* 179:249 */       if ((count == 3) && (i > 0))
/* 180:    */       {
/* 181:250 */         result = "," + result;
/* 182:251 */         count = 0;
/* 183:    */       }
/* 184:    */     }
/* 185:255 */     return result;
/* 186:    */   }
/* 187:    */   
/* 188:    */   private void addPopup()
/* 189:    */   {
/* 190:263 */     addMouseListener(new MouseAdapter()
/* 191:    */     {
/* 192:    */       public void mouseClicked(MouseEvent e)
/* 193:    */       {
/* 194:265 */         if (((e.getModifiers() & 0x10) != 16) || (e.isAltDown()))
/* 195:    */         {
/* 196:267 */           JPopupMenu gcMenu = new JPopupMenu();
/* 197:268 */           JMenuItem availMem = new JMenuItem("Memory information");
/* 198:269 */           availMem.addActionListener(new ActionListener()
/* 199:    */           {
/* 200:    */             public void actionPerformed(ActionEvent ee)
/* 201:    */             {
/* 202:271 */               System.gc();
/* 203:272 */               Runtime currR = Runtime.getRuntime();
/* 204:273 */               long freeM = currR.freeMemory();
/* 205:274 */               long totalM = currR.totalMemory();
/* 206:275 */               long maxM = currR.maxMemory();
/* 207:276 */               LogPanel.this.logMessage("Memory (free/total/max.) in bytes: " + LogPanel.this.printLong(freeM) + " / " + LogPanel.this.printLong(totalM) + " / " + LogPanel.this.printLong(maxM));
/* 208:    */               
/* 209:    */ 
/* 210:279 */               LogPanel.this.statusMessage("Memory (free/total/max.) in bytes: " + LogPanel.this.printLong(freeM) + " / " + LogPanel.this.printLong(totalM) + " / " + LogPanel.this.printLong(maxM));
/* 211:    */             }
/* 212:283 */           });
/* 213:284 */           gcMenu.add(availMem);
/* 214:285 */           JMenuItem runGC = new JMenuItem("Run garbage collector");
/* 215:286 */           runGC.addActionListener(new ActionListener()
/* 216:    */           {
/* 217:    */             public void actionPerformed(ActionEvent ee)
/* 218:    */             {
/* 219:288 */               LogPanel.this.statusMessage("Running garbage collector");
/* 220:289 */               System.gc();
/* 221:290 */               LogPanel.this.statusMessage("OK");
/* 222:    */             }
/* 223:292 */           });
/* 224:293 */           gcMenu.add(runGC);
/* 225:294 */           gcMenu.show(LogPanel.this, e.getX(), e.getY());
/* 226:    */         }
/* 227:    */       }
/* 228:    */     });
/* 229:    */   }
/* 230:    */   
/* 231:    */   public void taskStarted()
/* 232:    */   {
/* 233:304 */     if (this.m_TaskMonitor != null) {
/* 234:305 */       this.m_TaskMonitor.taskStarted();
/* 235:    */     }
/* 236:    */   }
/* 237:    */   
/* 238:    */   public void taskFinished()
/* 239:    */   {
/* 240:313 */     if (this.m_TaskMonitor != null) {
/* 241:314 */       this.m_TaskMonitor.taskFinished();
/* 242:    */     }
/* 243:    */   }
/* 244:    */   
/* 245:    */   protected static String getTimestamp()
/* 246:    */   {
/* 247:325 */     return new SimpleDateFormat("HH:mm:ss:").format(new Date());
/* 248:    */   }
/* 249:    */   
/* 250:    */   public synchronized void logMessage(String message)
/* 251:    */   {
/* 252:336 */     if (this.m_First) {
/* 253:337 */       this.m_First = false;
/* 254:    */     } else {
/* 255:339 */       this.m_LogText.append("\n");
/* 256:    */     }
/* 257:341 */     this.m_LogText.append(getTimestamp() + ' ' + message);
/* 258:342 */     weka.core.logging.Logger.log(Logger.Level.INFO, message);
/* 259:    */   }
/* 260:    */   
/* 261:    */   public synchronized void statusMessage(String message)
/* 262:    */   {
/* 263:352 */     this.m_StatusLab.setText(message);
/* 264:    */   }
/* 265:    */   
/* 266:    */   public static void main(String[] args)
/* 267:    */   {
/* 268:    */     try
/* 269:    */     {
/* 270:363 */       JFrame jf = new JFrame("Log Panel");
/* 271:364 */       jf.getContentPane().setLayout(new BorderLayout());
/* 272:365 */       LogPanel lp = new LogPanel();
/* 273:366 */       jf.getContentPane().add(lp, "Center");
/* 274:367 */       jf.addWindowListener(new WindowAdapter()
/* 275:    */       {
/* 276:    */         public void windowClosing(WindowEvent e)
/* 277:    */         {
/* 278:369 */           this.val$jf.dispose();
/* 279:370 */           System.exit(0);
/* 280:    */         }
/* 281:372 */       });
/* 282:373 */       jf.pack();
/* 283:374 */       jf.setVisible(true);
/* 284:375 */       lp.logMessage("Welcome to the generic log panel!");
/* 285:376 */       lp.statusMessage("Hi there");
/* 286:377 */       lp.logMessage("Funky chickens");
/* 287:    */     }
/* 288:    */     catch (Exception ex)
/* 289:    */     {
/* 290:380 */       ex.printStackTrace();
/* 291:381 */       System.err.println(ex.getMessage());
/* 292:    */     }
/* 293:    */   }
/* 294:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.LogPanel
 * JD-Core Version:    0.7.0.1
 */