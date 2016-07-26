/*   1:    */ package weka.gui;
/*   2:    */ 
/*   3:    */ import java.awt.Color;
/*   4:    */ import java.awt.Dimension;
/*   5:    */ import java.awt.EventQueue;
/*   6:    */ import java.awt.Font;
/*   7:    */ import java.awt.FontMetrics;
/*   8:    */ import java.awt.Frame;
/*   9:    */ import java.awt.Graphics;
/*  10:    */ import java.awt.GraphicsDevice;
/*  11:    */ import java.awt.GraphicsDevice.WindowTranslucency;
/*  12:    */ import java.awt.GraphicsEnvironment;
/*  13:    */ import java.awt.Image;
/*  14:    */ import java.awt.MediaTracker;
/*  15:    */ import java.awt.Toolkit;
/*  16:    */ import java.awt.Window;
/*  17:    */ import java.awt.event.MouseAdapter;
/*  18:    */ import java.awt.event.MouseEvent;
/*  19:    */ import java.awt.geom.Ellipse2D.Double;
/*  20:    */ import java.lang.reflect.Method;
/*  21:    */ import java.net.URL;
/*  22:    */ import java.util.List;
/*  23:    */ 
/*  24:    */ public class SplashWindow
/*  25:    */   extends Window
/*  26:    */ {
/*  27:    */   private static final long serialVersionUID = -2685134277041307795L;
/*  28:    */   private static SplashWindow m_instance;
/*  29:    */   private final Image image;
/*  30:    */   private final List<String> message;
/*  31:101 */   private boolean paintCalled = false;
/*  32:    */   
/*  33:    */   private SplashWindow(Frame parent, Image image, List<String> message)
/*  34:    */   {
/*  35:110 */     super(parent);
/*  36:111 */     this.image = image;
/*  37:112 */     this.message = message;
/*  38:    */     
/*  39:    */ 
/*  40:115 */     MediaTracker mt = new MediaTracker(this);
/*  41:116 */     mt.addImage(image, 0);
/*  42:    */     try
/*  43:    */     {
/*  44:118 */       mt.waitForID(0);
/*  45:    */     }
/*  46:    */     catch (InterruptedException ie) {}
/*  47:123 */     int imgWidth = image.getWidth(this);
/*  48:124 */     int imgHeight = image.getHeight(this);
/*  49:125 */     setSize(imgWidth, imgHeight);
/*  50:126 */     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
/*  51:127 */     if (ge.getDefaultScreenDevice().isWindowTranslucencySupported(GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
/*  52:131 */       setShape(new Ellipse2D.Double(0.0D, 0.0D, getWidth(), getHeight()));
/*  53:    */     }
/*  54:133 */     Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
/*  55:134 */     setLocation((screenDim.width - imgWidth) / 2, (screenDim.height - imgHeight) / 2);
/*  56:    */     
/*  57:    */ 
/*  58:    */ 
/*  59:    */ 
/*  60:    */ 
/*  61:140 */     MouseAdapter disposeOnClick = new MouseAdapter()
/*  62:    */     {
/*  63:    */       public void mouseClicked(MouseEvent evt)
/*  64:    */       {
/*  65:148 */         synchronized (SplashWindow.this)
/*  66:    */         {
/*  67:149 */           SplashWindow.this.paintCalled = true;
/*  68:150 */           SplashWindow.this.notifyAll();
/*  69:    */         }
/*  70:152 */         SplashWindow.this.dispose();
/*  71:    */       }
/*  72:154 */     };
/*  73:155 */     addMouseListener(disposeOnClick);
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void update(Graphics g)
/*  77:    */   {
/*  78:168 */     paint(g);
/*  79:    */   }
/*  80:    */   
/*  81:    */   private void paintMessage(Graphics g)
/*  82:    */   {
/*  83:172 */     int imgWidth = this.image.getWidth(this);
/*  84:173 */     int imgHeight = this.image.getHeight(this);
/*  85:174 */     g.setFont(new Font(null, 1, 10));
/*  86:175 */     g.setColor(Color.WHITE);
/*  87:176 */     FontMetrics fm = g.getFontMetrics();
/*  88:177 */     int hf = fm.getAscent() + 1;
/*  89:    */     
/*  90:179 */     int heightStart = 4 * (imgHeight / 5) + 5;
/*  91:180 */     int count = 0;
/*  92:181 */     for (String s : this.message)
/*  93:    */     {
/*  94:182 */       int textWidth = fm.stringWidth(s);
/*  95:183 */       g.drawString(s, (imgWidth - textWidth) / 2, heightStart + count * hf);
/*  96:184 */       count++;
/*  97:    */     }
/*  98:    */   }
/*  99:    */   
/* 100:    */   public void paint(Graphics g)
/* 101:    */   {
/* 102:193 */     g.drawImage(this.image, 0, 0, this);
/* 103:194 */     if (this.message != null) {
/* 104:195 */       paintMessage(g);
/* 105:    */     }
/* 106:202 */     if (!this.paintCalled)
/* 107:    */     {
/* 108:203 */       this.paintCalled = true;
/* 109:204 */       synchronized (this)
/* 110:    */       {
/* 111:205 */         notifyAll();
/* 112:    */       }
/* 113:    */     }
/* 114:    */   }
/* 115:    */   
/* 116:    */   public static void splash(Image image, List<String> message)
/* 117:    */   {
/* 118:217 */     if ((m_instance == null) && (image != null))
/* 119:    */     {
/* 120:218 */       Frame f = new Frame();
/* 121:    */       
/* 122:    */ 
/* 123:221 */       m_instance = new SplashWindow(f, image, message);
/* 124:    */       
/* 125:    */ 
/* 126:224 */       m_instance.show();
/* 127:231 */       if ((!EventQueue.isDispatchThread()) && (Runtime.getRuntime().availableProcessors() == 1)) {
/* 128:233 */         synchronized (m_instance)
/* 129:    */         {
/* 130:234 */           while (!m_instance.paintCalled) {
/* 131:    */             try
/* 132:    */             {
/* 133:236 */               m_instance.wait();
/* 134:    */             }
/* 135:    */             catch (InterruptedException e) {}
/* 136:    */           }
/* 137:    */         }
/* 138:    */       }
/* 139:    */     }
/* 140:    */   }
/* 141:    */   
/* 142:    */   public static void splash(URL imageURL)
/* 143:    */   {
/* 144:251 */     splash(imageURL, null);
/* 145:    */   }
/* 146:    */   
/* 147:    */   public static void splash(URL imageURL, List<String> message)
/* 148:    */   {
/* 149:261 */     if (imageURL != null) {
/* 150:262 */       splash(Toolkit.getDefaultToolkit().createImage(imageURL), message);
/* 151:    */     }
/* 152:    */   }
/* 153:    */   
/* 154:    */   public static void disposeSplash()
/* 155:    */   {
/* 156:270 */     if (m_instance != null)
/* 157:    */     {
/* 158:271 */       m_instance.getOwner().dispose();
/* 159:272 */       m_instance = null;
/* 160:    */     }
/* 161:    */   }
/* 162:    */   
/* 163:    */   public static void invokeMethod(String className, String methodName, String[] args)
/* 164:    */   {
/* 165:    */     try
/* 166:    */     {
/* 167:286 */       Class.forName(className).getMethod(methodName, new Class[] { [Ljava.lang.String.class }).invoke(null, new Object[] { args });
/* 168:    */     }
/* 169:    */     catch (Exception e)
/* 170:    */     {
/* 171:290 */       InternalError error = new InternalError("Failed to invoke method: " + methodName);
/* 172:    */       
/* 173:292 */       error.initCause(e);
/* 174:293 */       throw error;
/* 175:    */     }
/* 176:    */   }
/* 177:    */   
/* 178:    */   public static void invokeMain(String className, String[] args)
/* 179:    */   {
/* 180:    */     try
/* 181:    */     {
/* 182:305 */       Class.forName(className).getMethod("main", new Class[] { [Ljava.lang.String.class }).invoke(null, new Object[] { args });
/* 183:    */     }
/* 184:    */     catch (Exception e)
/* 185:    */     {
/* 186:308 */       InternalError error = new InternalError("Failed to invoke main method");
/* 187:309 */       error.initCause(e);
/* 188:310 */       throw error;
/* 189:    */     }
/* 190:    */   }
/* 191:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.SplashWindow
 * JD-Core Version:    0.7.0.1
 */