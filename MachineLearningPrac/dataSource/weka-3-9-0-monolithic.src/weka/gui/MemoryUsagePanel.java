/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.Graphics;
/*   7:    */ import java.awt.Point;
/*   8:    */ import java.awt.event.ActionEvent;
/*   9:    */ import java.awt.event.ActionListener;
/*  10:    */ import java.io.PrintStream;
/*  11:    */ import java.util.Collections;
/*  12:    */ import java.util.Enumeration;
/*  13:    */ import java.util.Hashtable;
/*  14:    */ import java.util.Properties;
/*  15:    */ import java.util.Vector;
/*  16:    */ import javax.swing.JButton;
/*  17:    */ import javax.swing.JOptionPane;
/*  18:    */ import javax.swing.JPanel;
/*  19:    */ import javax.swing.SwingUtilities;
/*  20:    */ import weka.core.Memory;
/*  21:    */ import weka.core.Utils;
/*  22:    */ import weka.gui.visualize.VisualizeUtils;
/*  23:    */ 
/*  24:    */ public class MemoryUsagePanel
/*  25:    */   extends JPanel
/*  26:    */ {
/*  27:    */   private static final long serialVersionUID = -4812319791687471721L;
/*  28:    */   
/*  29:    */   protected class MemoryMonitor
/*  30:    */     extends Thread
/*  31:    */   {
/*  32:    */     protected int m_Interval;
/*  33:    */     protected boolean m_Monitoring;
/*  34:    */     
/*  35:    */     public MemoryMonitor()
/*  36:    */     {
/*  37: 76 */       setInterval(1000);
/*  38:    */     }
/*  39:    */     
/*  40:    */     public int getInterval()
/*  41:    */     {
/*  42: 85 */       return this.m_Interval;
/*  43:    */     }
/*  44:    */     
/*  45:    */     public void setInterval(int value)
/*  46:    */     {
/*  47: 94 */       this.m_Interval = value;
/*  48:    */     }
/*  49:    */     
/*  50:    */     public boolean isMonitoring()
/*  51:    */     {
/*  52:103 */       return this.m_Monitoring;
/*  53:    */     }
/*  54:    */     
/*  55:    */     public void stopMonitoring()
/*  56:    */     {
/*  57:110 */       this.m_Monitoring = false;
/*  58:    */     }
/*  59:    */     
/*  60:    */     public void run()
/*  61:    */     {
/*  62:118 */       this.m_Monitoring = true;
/*  63:120 */       while (this.m_Monitoring) {
/*  64:    */         try
/*  65:    */         {
/*  66:122 */           Thread.sleep(this.m_Interval);
/*  67:125 */           if (this.m_Monitoring)
/*  68:    */           {
/*  69:126 */             Runnable doUpdate = new Runnable()
/*  70:    */             {
/*  71:    */               public void run()
/*  72:    */               {
/*  73:129 */                 MemoryUsagePanel.MemoryMonitor.this.update();
/*  74:    */               }
/*  75:131 */             };
/*  76:132 */             SwingUtilities.invokeLater(doUpdate);
/*  77:    */           }
/*  78:    */         }
/*  79:    */         catch (InterruptedException ex)
/*  80:    */         {
/*  81:135 */           ex.printStackTrace();
/*  82:    */         }
/*  83:    */       }
/*  84:    */     }
/*  85:    */     
/*  86:    */     protected void update()
/*  87:    */     {
/*  88:148 */       double perc = MemoryUsagePanel.this.m_Memory.getCurrent() / MemoryUsagePanel.this.m_Memory.getMax();
/*  89:149 */       perc = Math.round(perc * 1000.0D) / 10L;
/*  90:    */       
/*  91:    */ 
/*  92:152 */       MemoryUsagePanel.this.setToolTipText("" + perc + "% used");
/*  93:    */       
/*  94:    */ 
/*  95:155 */       MemoryUsagePanel.this.m_History.insertElementAt(Double.valueOf(perc), 0);
/*  96:156 */       Dimension size = MemoryUsagePanel.this.getSize();
/*  97:157 */       while (MemoryUsagePanel.this.m_History.size() > size.getWidth()) {
/*  98:158 */         MemoryUsagePanel.this.m_History.remove(MemoryUsagePanel.this.m_History.size() - 1);
/*  99:    */       }
/* 100:162 */       MemoryUsagePanel.this.repaint();
/* 101:    */     }
/* 102:    */   }
/* 103:    */   
/* 104:167 */   protected static String PROPERTY_FILE = "weka/gui/MemoryUsage.props";
/* 105:    */   protected static Properties PROPERTIES;
/* 106:    */   protected Vector<Double> m_History;
/* 107:    */   protected Memory m_Memory;
/* 108:    */   protected MemoryMonitor m_Monitor;
/* 109:    */   protected JButton m_ButtonGC;
/* 110:    */   protected Vector<Double> m_Percentages;
/* 111:    */   protected Hashtable<Double, Color> m_Colors;
/* 112:    */   protected Color m_DefaultColor;
/* 113:    */   protected Color m_BackgroundColor;
/* 114:    */   protected Point m_FrameLocation;
/* 115:    */   
/* 116:    */   static
/* 117:    */   {
/* 118:    */     try
/* 119:    */     {
/* 120:209 */       PROPERTIES = Utils.readProperties(PROPERTY_FILE);
/* 121:210 */       Enumeration<?> keys = PROPERTIES.propertyNames();
/* 122:211 */       if (!keys.hasMoreElements()) {
/* 123:212 */         throw new Exception("Failed to read a property file for the memory usage panel");
/* 124:    */       }
/* 125:    */     }
/* 126:    */     catch (Exception ex)
/* 127:    */     {
/* 128:216 */       JOptionPane.showMessageDialog(null, "Could not read a configuration file for the memory usage\npanel. An example file is included with the Weka distribution.\nThis file should be named \"" + PROPERTY_FILE + "\" and\n" + "should be placed either in your user home (which is set\n" + "to \"" + System.getProperties().getProperty("user.home") + "\")\n" + "or the directory that java was started from\n", "MemoryUsagePanel", 0);
/* 129:    */     }
/* 130:    */   }
/* 131:    */   
/* 132:    */   public MemoryUsagePanel()
/* 133:    */   {
/* 134:234 */     this.m_Memory = new Memory();
/* 135:235 */     this.m_History = new Vector();
/* 136:236 */     this.m_Percentages = new Vector();
/* 137:237 */     this.m_Colors = new Hashtable();
/* 138:    */     
/* 139:    */ 
/* 140:240 */     this.m_BackgroundColor = parseColor("BackgroundColor", Color.WHITE);
/* 141:241 */     this.m_DefaultColor = parseColor("DefaultColor", Color.GREEN);
/* 142:242 */     String[] percs = PROPERTIES.getProperty("Percentages", "70,80,90").split(",");
/* 143:244 */     for (String perc2 : percs) {
/* 144:246 */       if (PROPERTIES.getProperty(perc2) != null)
/* 145:    */       {
/* 146:    */         double perc;
/* 147:    */         try
/* 148:    */         {
/* 149:252 */           perc = Double.parseDouble(perc2);
/* 150:    */         }
/* 151:    */         catch (Exception e)
/* 152:    */         {
/* 153:254 */           System.err.println("MemoryUsagePanel: cannot parse percentage '" + perc2 + "' - ignored!");
/* 154:    */           
/* 155:256 */           continue;
/* 156:    */         }
/* 157:260 */         Color color = parseColor(perc2, null);
/* 158:261 */         if (color != null)
/* 159:    */         {
/* 160:266 */           this.m_Percentages.add(Double.valueOf(perc));
/* 161:267 */           this.m_Colors.put(Double.valueOf(perc), color);
/* 162:    */         }
/* 163:    */       }
/* 164:    */       else
/* 165:    */       {
/* 166:269 */         System.err.println("MemoryUsagePanel: cannot find color for percentage '" + perc2 + "' - ignored!");
/* 167:    */       }
/* 168:    */     }
/* 169:274 */     Collections.sort(this.m_Percentages);
/* 170:    */     
/* 171:    */ 
/* 172:277 */     setLayout(new BorderLayout());
/* 173:    */     
/* 174:279 */     JPanel panel = new JPanel(new BorderLayout());
/* 175:280 */     add(panel, "East");
/* 176:    */     
/* 177:282 */     this.m_ButtonGC = new JButton("GC");
/* 178:283 */     this.m_ButtonGC.setToolTipText("Runs the garbage collector.");
/* 179:284 */     this.m_ButtonGC.addActionListener(new ActionListener()
/* 180:    */     {
/* 181:    */       public void actionPerformed(ActionEvent evt) {}
/* 182:289 */     });
/* 183:290 */     panel.add(this.m_ButtonGC, "North");
/* 184:    */     int height;
/* 185:    */     int width;
/* 186:    */     try
/* 187:    */     {
/* 188:296 */       height = Integer.parseInt(PROPERTIES.getProperty("Height", "" + (int)this.m_ButtonGC.getPreferredSize().getHeight()));
/* 189:    */       
/* 190:298 */       width = Integer.parseInt(PROPERTIES.getProperty("Width", "400"));
/* 191:    */     }
/* 192:    */     catch (Exception e)
/* 193:    */     {
/* 194:300 */       System.err.println("MemoryUsagePanel: Problem parsing the dimensions - " + e);
/* 195:    */       
/* 196:302 */       height = (int)this.m_ButtonGC.getPreferredSize().getHeight();
/* 197:303 */       width = 400;
/* 198:    */     }
/* 199:305 */     setPreferredSize(new Dimension(width, height));
/* 200:    */     int top;
/* 201:    */     int left;
/* 202:    */     try
/* 203:    */     {
/* 204:311 */       top = Integer.parseInt(PROPERTIES.getProperty("Top", "0"));
/* 205:312 */       left = Integer.parseInt(PROPERTIES.getProperty("Left", "0"));
/* 206:    */     }
/* 207:    */     catch (Exception e)
/* 208:    */     {
/* 209:314 */       System.err.println("MemoryUsagePanel: Problem parsing the position - " + e);
/* 210:    */       
/* 211:316 */       top = 0;
/* 212:317 */       left = 0;
/* 213:    */     }
/* 214:319 */     this.m_FrameLocation = new Point(left, top);
/* 215:    */     int interval;
/* 216:    */     try
/* 217:    */     {
/* 218:324 */       interval = Integer.parseInt(PROPERTIES.getProperty("Interval", "1000"));
/* 219:    */     }
/* 220:    */     catch (Exception e)
/* 221:    */     {
/* 222:326 */       System.err.println("MemoryUsagePanel: Problem parsing the refresh interval - " + e);
/* 223:    */       
/* 224:    */ 
/* 225:329 */       interval = 1000;
/* 226:    */     }
/* 227:331 */     this.m_Monitor = new MemoryMonitor();
/* 228:332 */     this.m_Monitor.setInterval(interval);
/* 229:333 */     this.m_Monitor.setPriority(10);
/* 230:334 */     this.m_Monitor.start();
/* 231:    */   }
/* 232:    */   
/* 233:    */   protected Color parseColor(String prop, Color defValue)
/* 234:    */   {
/* 235:349 */     Color result = defValue;
/* 236:    */     try
/* 237:    */     {
/* 238:352 */       String colorStr = PROPERTIES.getProperty(prop);
/* 239:353 */       Color color = VisualizeUtils.processColour(colorStr, result);
/* 240:354 */       if (color == null) {
/* 241:355 */         throw new Exception(colorStr);
/* 242:    */       }
/* 243:357 */       result = color;
/* 244:    */     }
/* 245:    */     catch (Exception e)
/* 246:    */     {
/* 247:359 */       System.err.println("MemoryUsagePanel: cannot parse color '" + e.getMessage() + "' - ignored!");
/* 248:    */     }
/* 249:363 */     return result;
/* 250:    */   }
/* 251:    */   
/* 252:    */   public boolean isMonitoring()
/* 253:    */   {
/* 254:372 */     return this.m_Monitor.isMonitoring();
/* 255:    */   }
/* 256:    */   
/* 257:    */   public void stopMonitoring()
/* 258:    */   {
/* 259:379 */     this.m_Monitor.stopMonitoring();
/* 260:    */   }
/* 261:    */   
/* 262:    */   public Point getFrameLocation()
/* 263:    */   {
/* 264:388 */     return this.m_FrameLocation;
/* 265:    */   }
/* 266:    */   
/* 267:    */   public void paintComponent(Graphics g)
/* 268:    */   {
/* 269:405 */     super.paintComponent(g);
/* 270:    */     
/* 271:407 */     g.setColor(this.m_BackgroundColor);
/* 272:408 */     g.fillRect(0, 0, getWidth(), getHeight());
/* 273:409 */     double scale = getHeight() / 100.0D;
/* 274:410 */     for (int i = 0; i < this.m_History.size(); i++)
/* 275:    */     {
/* 276:411 */       double perc = ((Double)this.m_History.get(i)).doubleValue();
/* 277:    */       
/* 278:    */ 
/* 279:414 */       Color color = this.m_DefaultColor;
/* 280:415 */       for (int n = this.m_Percentages.size() - 1; n >= 0; n--) {
/* 281:416 */         if (perc >= ((Double)this.m_Percentages.get(n)).doubleValue())
/* 282:    */         {
/* 283:417 */           color = (Color)this.m_Colors.get(this.m_Percentages.get(n));
/* 284:418 */           break;
/* 285:    */         }
/* 286:    */       }
/* 287:423 */       g.setColor(color);
/* 288:424 */       int len = (int)Math.round(perc * scale);
/* 289:425 */       g.drawLine(i, getHeight() - 1, i, getHeight() - len);
/* 290:    */     }
/* 291:    */   }
/* 292:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.MemoryUsagePanel
 * JD-Core Version:    0.7.0.1
 */