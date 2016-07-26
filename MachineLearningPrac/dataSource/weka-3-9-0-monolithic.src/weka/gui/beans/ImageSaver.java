/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.awt.image.BufferedImage;
/*   5:    */ import java.beans.EventSetDescriptor;
/*   6:    */ import java.io.File;
/*   7:    */ import java.io.IOException;
/*   8:    */ import java.io.Serializable;
/*   9:    */ import javax.imageio.ImageIO;
/*  10:    */ import javax.swing.JPanel;
/*  11:    */ import weka.core.Environment;
/*  12:    */ import weka.core.EnvironmentHandler;
/*  13:    */ import weka.gui.Logger;
/*  14:    */ 
/*  15:    */ public class ImageSaver
/*  16:    */   extends JPanel
/*  17:    */   implements ImageListener, BeanCommon, Visible, Serializable, EnvironmentHandler
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = -641438159956934314L;
/*  20: 56 */   protected BeanVisual m_visual = new BeanVisual("AbstractDataSink", "weka/gui/beans/icons/SerializedModelSaver.gif", "weka/gui/beans/icons/SerializedModelSaver_animated.gif");
/*  21: 66 */   protected Object m_listenee = null;
/*  22: 71 */   protected transient Logger m_logger = null;
/*  23:    */   protected transient Environment m_env;
/*  24:    */   protected String m_fileName;
/*  25:    */   
/*  26:    */   public String globalInfo()
/*  27:    */   {
/*  28: 87 */     return "Save static images (such as those produced by ModelPerformanceChart) to a file.";
/*  29:    */   }
/*  30:    */   
/*  31:    */   public ImageSaver()
/*  32:    */   {
/*  33: 95 */     useDefaultVisual();
/*  34: 96 */     setLayout(new BorderLayout());
/*  35: 97 */     add(this.m_visual, "Center");
/*  36:    */     
/*  37: 99 */     this.m_env = Environment.getSystemWide();
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void setFilename(String filename)
/*  41:    */   {
/*  42:108 */     this.m_fileName = filename;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public String getFilename()
/*  46:    */   {
/*  47:117 */     return this.m_fileName;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setEnvironment(Environment env)
/*  51:    */   {
/*  52:126 */     this.m_env = env;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public void useDefaultVisual()
/*  56:    */   {
/*  57:131 */     this.m_visual.loadIcons("weka/gui/beans/icons/SerializedModelSaver.gif", "weka/gui/beans/icons/SerializedModelSaver_animated.gif");
/*  58:    */     
/*  59:133 */     this.m_visual.setText("ImageSaver");
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setVisual(BeanVisual newVisual)
/*  63:    */   {
/*  64:138 */     this.m_visual = newVisual;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public BeanVisual getVisual()
/*  68:    */   {
/*  69:143 */     return this.m_visual;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public void setCustomName(String name)
/*  73:    */   {
/*  74:148 */     this.m_visual.setText(name);
/*  75:    */   }
/*  76:    */   
/*  77:    */   public String getCustomName()
/*  78:    */   {
/*  79:153 */     return this.m_visual.getText();
/*  80:    */   }
/*  81:    */   
/*  82:    */   public void stop() {}
/*  83:    */   
/*  84:    */   public boolean isBusy()
/*  85:    */   {
/*  86:162 */     return false;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public void setLog(Logger logger)
/*  90:    */   {
/*  91:167 */     this.m_logger = logger;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/*  95:    */   {
/*  96:172 */     return connectionAllowed(esd.getName());
/*  97:    */   }
/*  98:    */   
/*  99:    */   public boolean connectionAllowed(String eventName)
/* 100:    */   {
/* 101:177 */     return this.m_listenee == null;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public void connectionNotification(String eventName, Object source)
/* 105:    */   {
/* 106:182 */     if (connectionAllowed(eventName)) {
/* 107:183 */       this.m_listenee = source;
/* 108:    */     }
/* 109:    */   }
/* 110:    */   
/* 111:    */   public void disconnectionNotification(String eventName, Object source)
/* 112:    */   {
/* 113:189 */     if (this.m_listenee == source) {
/* 114:190 */       this.m_listenee = null;
/* 115:    */     }
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void acceptImage(ImageEvent imageE)
/* 119:    */   {
/* 120:200 */     BufferedImage image = imageE.getImage();
/* 121:202 */     if ((this.m_fileName != null) && (this.m_fileName.length() > 0))
/* 122:    */     {
/* 123:203 */       if (this.m_env == null) {
/* 124:204 */         this.m_env = Environment.getSystemWide();
/* 125:    */       }
/* 126:206 */       String filename = this.m_fileName;
/* 127:    */       try
/* 128:    */       {
/* 129:208 */         filename = this.m_env.substitute(this.m_fileName);
/* 130:    */       }
/* 131:    */       catch (Exception ex) {}
/* 132:213 */       if (filename.toLowerCase().indexOf(".png") < 0) {
/* 133:214 */         filename = filename + ".png";
/* 134:    */       }
/* 135:217 */       File file = new File(filename);
/* 136:218 */       if (!file.isDirectory())
/* 137:    */       {
/* 138:    */         try
/* 139:    */         {
/* 140:220 */           ImageIO.write(image, "png", file);
/* 141:    */         }
/* 142:    */         catch (IOException e)
/* 143:    */         {
/* 144:222 */           if (this.m_logger != null)
/* 145:    */           {
/* 146:223 */             this.m_logger.statusMessage(statusMessagePrefix() + "WARNING: " + "an error occurred whilte trying to write image (see log)");
/* 147:    */             
/* 148:225 */             this.m_logger.logMessage("[" + getCustomName() + "] " + "an error occurred whilte trying to write image: " + e.getMessage());
/* 149:    */           }
/* 150:    */           else
/* 151:    */           {
/* 152:228 */             e.printStackTrace();
/* 153:    */           }
/* 154:    */         }
/* 155:    */       }
/* 156:    */       else
/* 157:    */       {
/* 158:232 */         String message = "Can't write image to file because supplied filename is a directory!";
/* 159:234 */         if (this.m_logger != null)
/* 160:    */         {
/* 161:235 */           this.m_logger.statusMessage(statusMessagePrefix() + "WARNING: " + message);
/* 162:236 */           this.m_logger.logMessage("[" + getCustomName() + "] " + message);
/* 163:    */         }
/* 164:    */       }
/* 165:    */     }
/* 166:    */     else
/* 167:    */     {
/* 168:240 */       String message = "Can't write image bacause no filename has been supplied! is a directory!";
/* 169:242 */       if (this.m_logger != null)
/* 170:    */       {
/* 171:243 */         this.m_logger.statusMessage(statusMessagePrefix() + "WARNING: " + message);
/* 172:244 */         this.m_logger.logMessage("[" + getCustomName() + "] " + message);
/* 173:    */       }
/* 174:    */     }
/* 175:    */   }
/* 176:    */   
/* 177:    */   private String statusMessagePrefix()
/* 178:    */   {
/* 179:250 */     return getCustomName() + "$" + hashCode() + "|";
/* 180:    */   }
/* 181:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.ImageSaver
 * JD-Core Version:    0.7.0.1
 */