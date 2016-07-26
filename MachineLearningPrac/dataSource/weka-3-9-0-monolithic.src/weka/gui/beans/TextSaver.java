/*   1:    */ package weka.gui.beans;
/*   2:    */ 
/*   3:    */ import java.awt.BorderLayout;
/*   4:    */ import java.beans.EventSetDescriptor;
/*   5:    */ import java.io.BufferedWriter;
/*   6:    */ import java.io.File;
/*   7:    */ import java.io.FileOutputStream;
/*   8:    */ import java.io.IOException;
/*   9:    */ import java.io.OutputStreamWriter;
/*  10:    */ import java.io.Serializable;
/*  11:    */ import javax.swing.JPanel;
/*  12:    */ import weka.core.Environment;
/*  13:    */ import weka.core.EnvironmentHandler;
/*  14:    */ import weka.gui.Logger;
/*  15:    */ 
/*  16:    */ @KFStep(category="DataSinks", toolTipText="Save text output to a file")
/*  17:    */ public class TextSaver
/*  18:    */   extends JPanel
/*  19:    */   implements TextListener, BeanCommon, Visible, Serializable, EnvironmentHandler
/*  20:    */ {
/*  21:    */   private static final long serialVersionUID = 6363577506969809332L;
/*  22: 57 */   protected BeanVisual m_visual = new BeanVisual("TextSaver", "weka/gui/beans/icons/DefaultText.gif", "weka/gui/beans/icons/DefaultText_animated.gif");
/*  23: 64 */   protected transient Logger m_logger = null;
/*  24:    */   protected transient Environment m_env;
/*  25:    */   protected String m_fileName;
/*  26: 75 */   protected boolean m_append = true;
/*  27:    */   
/*  28:    */   public String globalInfo()
/*  29:    */   {
/*  30: 83 */     return "Save/append static text to a file.";
/*  31:    */   }
/*  32:    */   
/*  33:    */   public TextSaver()
/*  34:    */   {
/*  35: 90 */     useDefaultVisual();
/*  36: 91 */     setLayout(new BorderLayout());
/*  37: 92 */     add(this.m_visual, "Center");
/*  38:    */     
/*  39: 94 */     this.m_env = Environment.getSystemWide();
/*  40:    */   }
/*  41:    */   
/*  42:    */   public void setFilename(String filename)
/*  43:    */   {
/*  44:103 */     this.m_fileName = filename;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public String getFilename()
/*  48:    */   {
/*  49:112 */     return this.m_fileName;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public void setAppend(boolean append)
/*  53:    */   {
/*  54:116 */     this.m_append = append;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public boolean getAppend()
/*  58:    */   {
/*  59:120 */     return this.m_append;
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void setEnvironment(Environment env)
/*  63:    */   {
/*  64:130 */     this.m_env = env;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void useDefaultVisual()
/*  68:    */   {
/*  69:135 */     this.m_visual.loadIcons("weka/gui/beans/icons/DefaultText.gif", "weka/gui/beans/icons/DefaultText_animated.gif");
/*  70:    */     
/*  71:137 */     this.m_visual.setText("TextSaver");
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void setVisual(BeanVisual newVisual)
/*  75:    */   {
/*  76:142 */     this.m_visual = newVisual;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public BeanVisual getVisual()
/*  80:    */   {
/*  81:147 */     return this.m_visual;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public void setCustomName(String name)
/*  85:    */   {
/*  86:152 */     this.m_visual.setText(name);
/*  87:    */   }
/*  88:    */   
/*  89:    */   public String getCustomName()
/*  90:    */   {
/*  91:157 */     return this.m_visual.getText();
/*  92:    */   }
/*  93:    */   
/*  94:    */   public void stop() {}
/*  95:    */   
/*  96:    */   public boolean isBusy()
/*  97:    */   {
/*  98:166 */     return false;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void setLog(Logger logger)
/* 102:    */   {
/* 103:171 */     this.m_logger = logger;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public boolean connectionAllowed(EventSetDescriptor esd)
/* 107:    */   {
/* 108:176 */     return connectionAllowed(esd.getName());
/* 109:    */   }
/* 110:    */   
/* 111:    */   public boolean connectionAllowed(String eventName)
/* 112:    */   {
/* 113:181 */     return true;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public void connectionNotification(String eventName, Object source) {}
/* 117:    */   
/* 118:    */   public void disconnectionNotification(String eventName, Object source) {}
/* 119:    */   
/* 120:    */   public synchronized void acceptText(TextEvent textEvent)
/* 121:    */   {
/* 122:199 */     String content = textEvent.getText();
/* 123:201 */     if ((this.m_fileName != null) && (this.m_fileName.length() > 0))
/* 124:    */     {
/* 125:202 */       if (this.m_env == null) {
/* 126:203 */         this.m_env = Environment.getSystemWide();
/* 127:    */       }
/* 128:205 */       String filename = this.m_fileName;
/* 129:    */       try
/* 130:    */       {
/* 131:207 */         filename = this.m_env.substitute(this.m_fileName);
/* 132:    */       }
/* 133:    */       catch (Exception ex) {}
/* 134:212 */       if (filename.toLowerCase().indexOf(".txt") < 0) {
/* 135:213 */         filename = filename + ".txt";
/* 136:    */       }
/* 137:216 */       File file = new File(filename);
/* 138:217 */       if (!file.isDirectory())
/* 139:    */       {
/* 140:218 */         BufferedWriter writer = null;
/* 141:    */         try
/* 142:    */         {
/* 143:221 */           writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, this.m_append), "utf-8"));
/* 144:    */           
/* 145:223 */           writer.write(content);
/* 146:224 */           writer.close();
/* 147:    */         }
/* 148:    */         catch (IOException e)
/* 149:    */         {
/* 150:226 */           if (this.m_logger != null)
/* 151:    */           {
/* 152:227 */             this.m_logger.statusMessage(statusMessagePrefix() + "WARNING: " + "an error occurred whilte trying to write text (see log)");
/* 153:    */             
/* 154:229 */             this.m_logger.logMessage("[" + getCustomName() + "] " + "an error occurred whilte trying to write text: " + e.getMessage());
/* 155:    */           }
/* 156:    */           else
/* 157:    */           {
/* 158:233 */             e.printStackTrace();
/* 159:    */           }
/* 160:    */         }
/* 161:    */       }
/* 162:    */       else
/* 163:    */       {
/* 164:238 */         String message = "Can't write text to file because supplied filename is a directory!";
/* 165:240 */         if (this.m_logger != null)
/* 166:    */         {
/* 167:241 */           this.m_logger.statusMessage(statusMessagePrefix() + "WARNING: " + message);
/* 168:242 */           this.m_logger.logMessage("[" + getCustomName() + "] " + message);
/* 169:    */         }
/* 170:    */       }
/* 171:    */     }
/* 172:    */     else
/* 173:    */     {
/* 174:247 */       String message = "Can't write text because no file has been supplied is a directory!";
/* 175:248 */       if (this.m_logger != null)
/* 176:    */       {
/* 177:249 */         this.m_logger.statusMessage(statusMessagePrefix() + "WARNING: " + message);
/* 178:250 */         this.m_logger.logMessage("[" + getCustomName() + "] " + message);
/* 179:    */       }
/* 180:    */     }
/* 181:    */   }
/* 182:    */   
/* 183:    */   private String statusMessagePrefix()
/* 184:    */   {
/* 185:256 */     return getCustomName() + "$" + hashCode() + "|";
/* 186:    */   }
/* 187:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.TextSaver
 * JD-Core Version:    0.7.0.1
 */