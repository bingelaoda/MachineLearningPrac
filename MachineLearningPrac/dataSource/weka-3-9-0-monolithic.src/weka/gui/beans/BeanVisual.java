/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Container;
/*   6:    */ import java.awt.Dimension;
/*   7:    */ import java.awt.Graphics;
/*   8:    */ import java.awt.Graphics2D;
/*   9:    */ import java.awt.Image;
/*  10:    */ import java.awt.Point;
/*  11:    */ import java.awt.RenderingHints;
/*  12:    */ import java.awt.Toolkit;
/*  13:    */ import java.beans.PropertyChangeListener;
/*  14:    */ import java.beans.PropertyChangeSupport;
/*  15:    */ import java.io.IOException;
/*  16:    */ import java.io.ObjectInputStream;
/*  17:    */ import java.io.PrintStream;
/*  18:    */ import java.net.URL;
/*  19:    */ import javax.swing.ImageIcon;
/*  20:    */ import javax.swing.JLabel;
/*  21:    */ import javax.swing.JPanel;
/*  22:    */ 
/*  23:    */ public class BeanVisual
/*  24:    */   extends JPanel
/*  25:    */ {
/*  26:    */   private static final long serialVersionUID = -6677473561687129614L;
/*  27:    */   public static final String ICON_PATH = "weka/gui/beans/icons/";
/*  28:    */   public static final int NORTH_CONNECTOR = 0;
/*  29:    */   public static final int SOUTH_CONNECTOR = 1;
/*  30:    */   public static final int EAST_CONNECTOR = 2;
/*  31:    */   public static final int WEST_CONNECTOR = 3;
/*  32:    */   protected String m_iconPath;
/*  33:    */   protected String m_animatedIconPath;
/*  34:    */   protected transient ImageIcon m_icon;
/*  35:    */   protected transient ImageIcon m_animatedIcon;
/*  36:    */   protected String m_visualName;
/*  37:    */   protected JLabel m_visualLabel;
/*  38: 99 */   private final PropertyChangeSupport m_pcs = new PropertyChangeSupport(this);
/*  39:101 */   private boolean m_displayConnectors = false;
/*  40:102 */   private Color m_connectorColor = Color.blue;
/*  41:    */   
/*  42:    */   public BeanVisual(String visualName, String iconPath, String animatedIconPath)
/*  43:    */   {
/*  44:113 */     loadIcons(iconPath, animatedIconPath);
/*  45:114 */     this.m_visualName = visualName;
/*  46:    */     
/*  47:116 */     this.m_visualLabel = new JLabel(this.m_icon);
/*  48:    */     
/*  49:118 */     setLayout(new BorderLayout());
/*  50:    */     
/*  51:    */ 
/*  52:    */ 
/*  53:122 */     add(this.m_visualLabel, "Center");
/*  54:123 */     Dimension d = this.m_visualLabel.getPreferredSize();
/*  55:    */     
/*  56:125 */     Dimension d2 = new Dimension((int)d.getWidth() + 10, (int)d.getHeight() + 10);
/*  57:    */     
/*  58:127 */     setMinimumSize(d2);
/*  59:128 */     setPreferredSize(d2);
/*  60:129 */     setMaximumSize(d2);
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void scale(int factor)
/*  64:    */   {
/*  65:138 */     if (this.m_icon != null)
/*  66:    */     {
/*  67:139 */       removeAll();
/*  68:140 */       Image pic = this.m_icon.getImage();
/*  69:141 */       int width = this.m_icon.getIconWidth();
/*  70:142 */       int height = this.m_icon.getIconHeight();
/*  71:143 */       int reduction = width / factor;
/*  72:144 */       width -= reduction;
/*  73:145 */       height -= reduction;
/*  74:146 */       pic = pic.getScaledInstance(width, height, 4);
/*  75:147 */       this.m_icon = new ImageIcon(pic);
/*  76:148 */       this.m_visualLabel = new JLabel(this.m_icon);
/*  77:149 */       add(this.m_visualLabel, "Center");
/*  78:150 */       Dimension d = this.m_visualLabel.getPreferredSize();
/*  79:    */       
/*  80:152 */       Dimension d2 = new Dimension((int)d.getWidth() + 10, (int)d.getHeight() + 10);
/*  81:    */       
/*  82:154 */       setMinimumSize(d2);
/*  83:155 */       setPreferredSize(d2);
/*  84:156 */       setMaximumSize(d2);
/*  85:    */     }
/*  86:    */   }
/*  87:    */   
/*  88:    */   public Image scale(double percent)
/*  89:    */   {
/*  90:161 */     if (this.m_icon != null)
/*  91:    */     {
/*  92:162 */       Image pic = this.m_icon.getImage();
/*  93:163 */       double width = this.m_icon.getIconWidth();
/*  94:164 */       double height = this.m_icon.getIconHeight();
/*  95:    */       
/*  96:166 */       width *= percent;
/*  97:167 */       height *= percent;
/*  98:    */       
/*  99:169 */       pic = pic.getScaledInstance((int)width, (int)height, 4);
/* 100:    */       
/* 101:    */ 
/* 102:172 */       return pic;
/* 103:    */     }
/* 104:175 */     return null;
/* 105:    */   }
/* 106:    */   
/* 107:    */   public boolean loadIcons(String iconPath, String animatedIconPath)
/* 108:    */   {
/* 109:191 */     boolean success = true;
/* 110:    */     
/* 111:193 */     URL imageURL = getClass().getClassLoader().getResource(iconPath);
/* 112:195 */     if (imageURL != null)
/* 113:    */     {
/* 114:198 */       Image pic = Toolkit.getDefaultToolkit().getImage(imageURL);
/* 115:    */       
/* 116:200 */       this.m_icon = new ImageIcon(pic);
/* 117:201 */       if (this.m_visualLabel != null) {
/* 118:202 */         this.m_visualLabel.setIcon(this.m_icon);
/* 119:    */       }
/* 120:    */     }
/* 121:207 */     imageURL = getClass().getClassLoader().getResource(animatedIconPath);
/* 122:208 */     if (imageURL == null)
/* 123:    */     {
/* 124:210 */       success = false;
/* 125:    */     }
/* 126:    */     else
/* 127:    */     {
/* 128:212 */       Image pic2 = Toolkit.getDefaultToolkit().getImage(imageURL);
/* 129:213 */       this.m_animatedIcon = new ImageIcon(pic2);
/* 130:    */     }
/* 131:215 */     this.m_iconPath = iconPath;
/* 132:216 */     this.m_animatedIconPath = animatedIconPath;
/* 133:217 */     return success;
/* 134:    */   }
/* 135:    */   
/* 136:    */   public void setText(String text)
/* 137:    */   {
/* 138:226 */     this.m_visualName = text;
/* 139:    */     
/* 140:228 */     this.m_pcs.firePropertyChange("label", null, null);
/* 141:    */   }
/* 142:    */   
/* 143:    */   public String getText()
/* 144:    */   {
/* 145:237 */     return this.m_visualName;
/* 146:    */   }
/* 147:    */   
/* 148:    */   @Deprecated
/* 149:    */   public void setStatic() {}
/* 150:    */   
/* 151:    */   @Deprecated
/* 152:    */   public void setAnimated() {}
/* 153:    */   
/* 154:    */   public Point getClosestConnectorPoint(Point pt)
/* 155:    */   {
/* 156:274 */     int sourceX = getParent().getX();
/* 157:275 */     int sourceY = getParent().getY();
/* 158:276 */     int sourceWidth = getWidth();
/* 159:277 */     int sourceHeight = getHeight();
/* 160:278 */     int sourceMidX = sourceX + sourceWidth / 2;
/* 161:279 */     int sourceMidY = sourceY + sourceHeight / 2;
/* 162:280 */     int x = (int)pt.getX();
/* 163:281 */     int y = (int)pt.getY();
/* 164:    */     
/* 165:283 */     Point closest = new Point();
/* 166:284 */     int cx = x < sourceMidX ? sourceX : Math.abs(x - sourceMidX) < Math.abs(y - sourceMidY) ? sourceMidX : sourceX + sourceWidth;
/* 167:    */     
/* 168:286 */     int cy = y < sourceMidY ? sourceY : Math.abs(y - sourceMidY) < Math.abs(x - sourceMidX) ? sourceMidY : sourceY + sourceHeight;
/* 169:    */     
/* 170:288 */     closest.setLocation(cx, cy);
/* 171:289 */     return closest;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public Point getConnectorPoint(int compassPoint)
/* 175:    */   {
/* 176:299 */     int sourceX = getParent().getX();
/* 177:300 */     int sourceY = getParent().getY();
/* 178:301 */     int sourceWidth = getWidth();
/* 179:302 */     int sourceHeight = getHeight();
/* 180:303 */     int sourceMidX = sourceX + sourceWidth / 2;
/* 181:304 */     int sourceMidY = sourceY + sourceHeight / 2;
/* 182:306 */     switch (compassPoint)
/* 183:    */     {
/* 184:    */     case 0: 
/* 185:308 */       return new Point(sourceMidX, sourceY);
/* 186:    */     case 1: 
/* 187:310 */       return new Point(sourceMidX, sourceY + sourceHeight);
/* 188:    */     case 3: 
/* 189:312 */       return new Point(sourceX, sourceMidY);
/* 190:    */     case 2: 
/* 191:314 */       return new Point(sourceX + sourceWidth, sourceMidY);
/* 192:    */     }
/* 193:316 */     System.err.println("Unrecognised connectorPoint (BeanVisual)");
/* 194:    */     
/* 195:318 */     return new Point(sourceX, sourceY);
/* 196:    */   }
/* 197:    */   
/* 198:    */   public ImageIcon getStaticIcon()
/* 199:    */   {
/* 200:327 */     return this.m_icon;
/* 201:    */   }
/* 202:    */   
/* 203:    */   public ImageIcon getAnimatedIcon()
/* 204:    */   {
/* 205:336 */     return this.m_animatedIcon;
/* 206:    */   }
/* 207:    */   
/* 208:    */   public String getIconPath()
/* 209:    */   {
/* 210:345 */     return this.m_iconPath;
/* 211:    */   }
/* 212:    */   
/* 213:    */   public String getAnimatedIconPath()
/* 214:    */   {
/* 215:354 */     return this.m_animatedIconPath;
/* 216:    */   }
/* 217:    */   
/* 218:    */   public void setDisplayConnectors(boolean dc)
/* 219:    */   {
/* 220:364 */     this.m_displayConnectors = dc;
/* 221:365 */     this.m_connectorColor = Color.blue;
/* 222:366 */     repaint();
/* 223:    */   }
/* 224:    */   
/* 225:    */   public void setDisplayConnectors(boolean dc, Color c)
/* 226:    */   {
/* 227:376 */     setDisplayConnectors(dc);
/* 228:377 */     this.m_connectorColor = c;
/* 229:    */   }
/* 230:    */   
/* 231:    */   public void addPropertyChangeListener(PropertyChangeListener pcl)
/* 232:    */   {
/* 233:388 */     if ((this.m_pcs != null) && (pcl != null)) {
/* 234:389 */       this.m_pcs.addPropertyChangeListener(pcl);
/* 235:    */     }
/* 236:    */   }
/* 237:    */   
/* 238:    */   public void removePropertyChangeListener(PropertyChangeListener pcl)
/* 239:    */   {
/* 240:401 */     if ((this.m_pcs != null) && (pcl != null)) {
/* 241:402 */       this.m_pcs.removePropertyChangeListener(pcl);
/* 242:    */     }
/* 243:    */   }
/* 244:    */   
/* 245:    */   public void paintComponent(Graphics gx)
/* 246:    */   {
/* 247:408 */     ((Graphics2D)gx).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 248:    */     
/* 249:    */ 
/* 250:411 */     super.paintComponent(gx);
/* 251:412 */     if (this.m_displayConnectors)
/* 252:    */     {
/* 253:413 */       gx.setColor(this.m_connectorColor);
/* 254:    */       
/* 255:415 */       int midx = (int)(getWidth() / 2.0D);
/* 256:416 */       int midy = (int)(getHeight() / 2.0D);
/* 257:417 */       gx.fillOval(midx - 2, 0, 5, 5);
/* 258:418 */       gx.fillOval(midx - 2, getHeight() - 5, 5, 5);
/* 259:419 */       gx.fillOval(0, midy - 2, 5, 5);
/* 260:420 */       gx.fillOval(getWidth() - 5, midy - 2, 5, 5);
/* 261:    */     }
/* 262:    */   }
/* 263:    */   
/* 264:    */   private void readObject(ObjectInputStream ois)
/* 265:    */     throws IOException, ClassNotFoundException
/* 266:    */   {
/* 267:    */     try
/* 268:    */     {
/* 269:436 */       ois.defaultReadObject();
/* 270:437 */       remove(this.m_visualLabel);
/* 271:438 */       this.m_visualLabel = new JLabel(this.m_icon);
/* 272:439 */       loadIcons(this.m_iconPath, this.m_animatedIconPath);
/* 273:440 */       add(this.m_visualLabel, "Center");
/* 274:441 */       Dimension d = this.m_visualLabel.getPreferredSize();
/* 275:442 */       Dimension d2 = new Dimension((int)d.getWidth() + 10, (int)d.getHeight() + 10);
/* 276:    */       
/* 277:444 */       setMinimumSize(d2);
/* 278:445 */       setPreferredSize(d2);
/* 279:446 */       setMaximumSize(d2);
/* 280:    */     }
/* 281:    */     catch (Exception ex)
/* 282:    */     {
/* 283:448 */       ex.printStackTrace();
/* 284:    */     }
/* 285:    */   }
/* 286:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.BeanVisual
 * JD-Core Version:    0.7.0.1
 */