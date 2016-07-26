/*   1:    */ package weka.gui.knowledgeflow;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.Color;
/*   5:    */ import java.awt.Dimension;
/*   6:    */ import java.awt.Graphics;
/*   7:    */ import java.awt.Graphics2D;
/*   8:    */ import java.awt.Image;
/*   9:    */ import java.awt.Point;
/*  10:    */ import java.awt.RenderingHints;
/*  11:    */ import java.awt.Toolkit;
/*  12:    */ import java.beans.Beans;
/*  13:    */ import java.net.URL;
/*  14:    */ import java.util.Map;
/*  15:    */ import java.util.Set;
/*  16:    */ import javax.swing.ImageIcon;
/*  17:    */ import javax.swing.JComponent;
/*  18:    */ import javax.swing.JLabel;
/*  19:    */ import javax.swing.JPanel;
/*  20:    */ import weka.core.WekaException;
/*  21:    */ import weka.knowledgeflow.StepManagerImpl;
/*  22:    */ import weka.knowledgeflow.steps.KFStep;
/*  23:    */ import weka.knowledgeflow.steps.Note;
/*  24:    */ import weka.knowledgeflow.steps.Step;
/*  25:    */ import weka.knowledgeflow.steps.WekaAlgorithmWrapper;
/*  26:    */ 
/*  27:    */ public class StepVisual
/*  28:    */   extends JPanel
/*  29:    */ {
/*  30:    */   public static final String BASE_ICON_PATH = "weka/gui/knowledgeflow/icons/";
/*  31:    */   private static final long serialVersionUID = 4156046438296843760L;
/*  32:    */   protected int m_x;
/*  33:    */   protected int m_y;
/*  34:    */   protected ImageIcon m_icon;
/*  35:    */   protected boolean m_displayConnectors;
/*  36: 78 */   protected Color m_connectorColor = Color.blue;
/*  37:    */   protected StepManagerImpl m_stepManager;
/*  38:    */   
/*  39:    */   private StepVisual(ImageIcon icon)
/*  40:    */   {
/*  41: 89 */     this.m_icon = icon;
/*  42: 91 */     if (icon != null)
/*  43:    */     {
/*  44: 92 */       setLayout(new BorderLayout());
/*  45: 93 */       setOpaque(false);
/*  46: 94 */       JLabel visual = new JLabel(this.m_icon);
/*  47: 95 */       add(visual, "Center");
/*  48: 96 */       Dimension d = visual.getPreferredSize();
/*  49: 97 */       Dimension d2 = new Dimension((int)d.getWidth() + 10, (int)d.getHeight() + 10);
/*  50:    */       
/*  51: 99 */       setMinimumSize(d2);
/*  52:100 */       setPreferredSize(d2);
/*  53:101 */       setMaximumSize(d2);
/*  54:    */     }
/*  55:    */   }
/*  56:    */   
/*  57:    */   protected StepVisual()
/*  58:    */   {
/*  59:109 */     this(null);
/*  60:    */   }
/*  61:    */   
/*  62:    */   public static StepVisual createVisual(StepManagerImpl stepManager)
/*  63:    */   {
/*  64:122 */     if ((stepManager.getManagedStep() instanceof Note))
/*  65:    */     {
/*  66:123 */       NoteVisual wrapper = new NoteVisual();
/*  67:124 */       wrapper.setStepManager(stepManager);
/*  68:125 */       return wrapper;
/*  69:    */     }
/*  70:127 */     ImageIcon icon = iconForStep(stepManager.getManagedStep());
/*  71:128 */     return createVisual(stepManager, icon);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public static StepVisual createVisual(StepManagerImpl stepManager, ImageIcon icon)
/*  75:    */   {
/*  76:144 */     StepVisual wrapper = new StepVisual(icon);
/*  77:145 */     wrapper.setStepManager(stepManager);
/*  78:    */     
/*  79:147 */     return wrapper;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public static ImageIcon iconForStep(Step step)
/*  83:    */   {
/*  84:157 */     KFStep stepAnnotation = (KFStep)step.getClass().getAnnotation(KFStep.class);
/*  85:158 */     if ((stepAnnotation != null) && (stepAnnotation.iconPath() != null) && (stepAnnotation.iconPath().length() > 0)) {
/*  86:160 */       return loadIcon(stepAnnotation.iconPath());
/*  87:    */     }
/*  88:163 */     if ((step instanceof WekaAlgorithmWrapper))
/*  89:    */     {
/*  90:164 */       ImageIcon icon = loadIcon(((WekaAlgorithmWrapper)step).getIconPath());
/*  91:165 */       if (icon == null) {
/*  92:167 */         icon = loadIcon(((WekaAlgorithmWrapper)step).getDefaultPackageLevelIconPath());
/*  93:    */       }
/*  94:172 */       if (icon == null) {
/*  95:174 */         icon = loadIcon(((WekaAlgorithmWrapper)step).getDefaultIconPath());
/*  96:    */       }
/*  97:177 */       return icon;
/*  98:    */     }
/*  99:181 */     return null;
/* 100:    */   }
/* 101:    */   
/* 102:    */   public static ImageIcon loadIcon(String iconPath)
/* 103:    */   {
/* 104:191 */     URL imageURL = StepVisual.class.getClassLoader().getResource(iconPath);
/* 105:194 */     if (imageURL != null)
/* 106:    */     {
/* 107:195 */       Image pic = Toolkit.getDefaultToolkit().getImage(imageURL);
/* 108:196 */       return new ImageIcon(pic);
/* 109:    */     }
/* 110:199 */     return null;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public static ImageIcon scaleIcon(ImageIcon icon, double factor)
/* 114:    */   {
/* 115:210 */     Image pic = icon.getImage();
/* 116:211 */     double width = icon.getIconWidth();
/* 117:212 */     double height = icon.getIconHeight();
/* 118:    */     
/* 119:214 */     width *= factor;
/* 120:215 */     height *= factor;
/* 121:    */     
/* 122:217 */     pic = pic.getScaledInstance((int)width, (int)height, 4);
/* 123:    */     
/* 124:219 */     return new ImageIcon(pic);
/* 125:    */   }
/* 126:    */   
/* 127:    */   public Image getIcon(double scale)
/* 128:    */   {
/* 129:229 */     if (scale == 1.0D) {
/* 130:230 */       return this.m_icon.getImage();
/* 131:    */     }
/* 132:233 */     Image pic = this.m_icon.getImage();
/* 133:234 */     double width = this.m_icon.getIconWidth();
/* 134:235 */     double height = this.m_icon.getIconHeight();
/* 135:    */     
/* 136:237 */     width *= scale;
/* 137:238 */     height *= scale;
/* 138:    */     
/* 139:240 */     pic = pic.getScaledInstance((int)width, (int)height, 4);
/* 140:    */     
/* 141:242 */     return pic;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public String getStepName()
/* 145:    */   {
/* 146:251 */     return this.m_stepManager.getManagedStep().getName();
/* 147:    */   }
/* 148:    */   
/* 149:    */   public void setStepName(String name)
/* 150:    */   {
/* 151:260 */     this.m_stepManager.getManagedStep().setName(name);
/* 152:    */   }
/* 153:    */   
/* 154:    */   public int getX()
/* 155:    */   {
/* 156:270 */     return this.m_x;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public void setX(int x)
/* 160:    */   {
/* 161:279 */     this.m_x = x;
/* 162:    */   }
/* 163:    */   
/* 164:    */   public int getY()
/* 165:    */   {
/* 166:289 */     return this.m_y;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void setY(int y)
/* 170:    */   {
/* 171:298 */     this.m_y = y;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public StepManagerImpl getStepManager()
/* 175:    */   {
/* 176:307 */     return this.m_stepManager;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public void setStepManager(StepManagerImpl manager)
/* 180:    */   {
/* 181:316 */     this.m_stepManager = manager;
/* 182:    */   }
/* 183:    */   
/* 184:    */   public String getCustomEditorForStep()
/* 185:    */   {
/* 186:326 */     return this.m_stepManager.getManagedStep().getCustomEditorForStep();
/* 187:    */   }
/* 188:    */   
/* 189:    */   public Set<String> getStepInteractiveViewActionNames()
/* 190:    */   {
/* 191:337 */     Map<String, String> viewComps = this.m_stepManager.getManagedStep().getInteractiveViewers();
/* 192:339 */     if (viewComps == null) {
/* 193:340 */       return null;
/* 194:    */     }
/* 195:343 */     return viewComps.keySet();
/* 196:    */   }
/* 197:    */   
/* 198:    */   public JComponent getStepInteractiveViewComponent(String viewActionName)
/* 199:    */     throws WekaException
/* 200:    */   {
/* 201:357 */     if (this.m_stepManager.getManagedStep().getInteractiveViewers() == null) {
/* 202:358 */       throw new WekaException("Step '" + this.m_stepManager.getManagedStep().getName() + "' " + "does not have any interactive view components");
/* 203:    */     }
/* 204:363 */     String clazz = (String)this.m_stepManager.getManagedStep().getInteractiveViewers().get(viewActionName);
/* 205:366 */     if (clazz == null) {
/* 206:367 */       throw new WekaException("Step '" + this.m_stepManager.getManagedStep().getName() + "' " + "does not have an interactive view component called '" + viewActionName + "'");
/* 207:    */     }
/* 208:373 */     Object comp = null;
/* 209:    */     try
/* 210:    */     {
/* 211:375 */       comp = Beans.instantiate(getClass().getClassLoader(), clazz);
/* 212:    */     }
/* 213:    */     catch (Exception ex)
/* 214:    */     {
/* 215:377 */       throw new WekaException(ex);
/* 216:    */     }
/* 217:380 */     if (!(comp instanceof JComponent)) {
/* 218:381 */       throw new WekaException("Interactive view component '" + clazz + "' does not " + "extend JComponent");
/* 219:    */     }
/* 220:385 */     return (JComponent)comp;
/* 221:    */   }
/* 222:    */   
/* 223:    */   public Point getClosestConnectorPoint(Point pt)
/* 224:    */   {
/* 225:396 */     int sourceX = getX();
/* 226:397 */     int sourceY = getY();
/* 227:398 */     int sourceWidth = getWidth();
/* 228:399 */     int sourceHeight = getHeight();
/* 229:400 */     int sourceMidX = sourceX + sourceWidth / 2;
/* 230:401 */     int sourceMidY = sourceY + sourceHeight / 2;
/* 231:402 */     int x = (int)pt.getX();
/* 232:403 */     int y = (int)pt.getY();
/* 233:    */     
/* 234:405 */     Point closest = new Point();
/* 235:406 */     int cx = x < sourceMidX ? sourceX : Math.abs(x - sourceMidX) < Math.abs(y - sourceMidY) ? sourceMidX : sourceX + sourceWidth;
/* 236:    */     
/* 237:    */ 
/* 238:409 */     int cy = y < sourceMidY ? sourceY : Math.abs(y - sourceMidY) < Math.abs(x - sourceMidX) ? sourceMidY : sourceY + sourceHeight;
/* 239:    */     
/* 240:    */ 
/* 241:412 */     closest.setLocation(cx, cy);
/* 242:413 */     return closest;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public void setDisplayConnectors(boolean dc)
/* 246:    */   {
/* 247:423 */     this.m_displayConnectors = dc;
/* 248:424 */     this.m_connectorColor = Color.blue;
/* 249:425 */     repaint();
/* 250:    */   }
/* 251:    */   
/* 252:    */   public void setDisplayConnectors(boolean dc, Color c)
/* 253:    */   {
/* 254:435 */     setDisplayConnectors(dc);
/* 255:436 */     this.m_connectorColor = c;
/* 256:    */   }
/* 257:    */   
/* 258:    */   public boolean getDisplayStepLabel()
/* 259:    */   {
/* 260:446 */     return true;
/* 261:    */   }
/* 262:    */   
/* 263:    */   public void paintComponent(Graphics gx)
/* 264:    */   {
/* 265:451 */     ((Graphics2D)gx).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 266:    */     
/* 267:    */ 
/* 268:454 */     super.paintComponent(gx);
/* 269:455 */     if (this.m_displayConnectors)
/* 270:    */     {
/* 271:456 */       gx.setColor(this.m_connectorColor);
/* 272:    */       
/* 273:458 */       int midx = (int)(getWidth() / 2.0D);
/* 274:459 */       int midy = (int)(getHeight() / 2.0D);
/* 275:460 */       gx.fillOval(midx - 2, 0, 5, 5);
/* 276:461 */       gx.fillOval(midx - 2, getHeight() - 5, 5, 5);
/* 277:462 */       gx.fillOval(0, midy - 2, 5, 5);
/* 278:463 */       gx.fillOval(getWidth() - 5, midy - 2, 5, 5);
/* 279:    */     }
/* 280:    */   }
/* 281:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.knowledgeflow.StepVisual
 * JD-Core Version:    0.7.0.1
 */