/*   1:    */ package weka.gui.visualize;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import javax.swing.JComponent;
/*   5:    */ 
/*   6:    */ public abstract class JComponentWriter
/*   7:    */ {
/*   8:    */   protected static final boolean DEBUG = false;
/*   9:    */   private JComponent component;
/*  10:    */   private File outputFile;
/*  11:    */   protected double m_xScale;
/*  12:    */   protected double m_yScale;
/*  13:    */   protected boolean m_ScalingEnabled;
/*  14:    */   protected boolean m_UseCustomDimensions;
/*  15:    */   protected int m_CustomWidth;
/*  16:    */   protected int m_CustomHeight;
/*  17:    */   
/*  18:    */   public JComponentWriter()
/*  19:    */   {
/*  20: 81 */     this(null);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public JComponentWriter(JComponent c)
/*  24:    */   {
/*  25: 90 */     this(c, null);
/*  26:    */   }
/*  27:    */   
/*  28:    */   public JComponentWriter(JComponent c, File f)
/*  29:    */   {
/*  30:100 */     this.component = c;
/*  31:101 */     this.outputFile = f;
/*  32:    */     
/*  33:103 */     initialize();
/*  34:    */   }
/*  35:    */   
/*  36:    */   protected void initialize()
/*  37:    */   {
/*  38:110 */     this.m_xScale = 1.0D;
/*  39:111 */     this.m_yScale = 1.0D;
/*  40:112 */     this.m_ScalingEnabled = true;
/*  41:113 */     this.m_UseCustomDimensions = false;
/*  42:114 */     this.m_CustomWidth = -1;
/*  43:115 */     this.m_CustomHeight = -1;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void setComponent(JComponent c)
/*  47:    */   {
/*  48:124 */     this.component = c;
/*  49:    */   }
/*  50:    */   
/*  51:    */   public JComponent getComponent()
/*  52:    */   {
/*  53:133 */     return this.component;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void setFile(File f)
/*  57:    */   {
/*  58:142 */     this.outputFile = f;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public File getFile()
/*  62:    */   {
/*  63:151 */     return this.outputFile;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public abstract String getDescription();
/*  67:    */   
/*  68:    */   public abstract String getExtension();
/*  69:    */   
/*  70:    */   public boolean getScalingEnabled()
/*  71:    */   {
/*  72:177 */     return this.m_ScalingEnabled;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public void setScalingEnabled(boolean enabled)
/*  76:    */   {
/*  77:186 */     this.m_ScalingEnabled = enabled;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public void setScale(double x, double y)
/*  81:    */   {
/*  82:195 */     if (getScalingEnabled())
/*  83:    */     {
/*  84:196 */       this.m_xScale = x;
/*  85:197 */       this.m_yScale = y;
/*  86:    */     }
/*  87:    */     else
/*  88:    */     {
/*  89:200 */       this.m_xScale = 1.0D;
/*  90:201 */       this.m_yScale = 1.0D;
/*  91:    */     }
/*  92:    */   }
/*  93:    */   
/*  94:    */   public double getXScale()
/*  95:    */   {
/*  96:214 */     return this.m_xScale;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public double getYScale()
/* 100:    */   {
/* 101:223 */     return this.m_xScale;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public boolean getUseCustomDimensions()
/* 105:    */   {
/* 106:232 */     return this.m_UseCustomDimensions;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public void setUseCustomDimensions(boolean value)
/* 110:    */   {
/* 111:241 */     this.m_UseCustomDimensions = value;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void setCustomWidth(int value)
/* 115:    */   {
/* 116:251 */     this.m_CustomWidth = value;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public int getCustomWidth()
/* 120:    */   {
/* 121:261 */     return this.m_CustomWidth;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public void setCustomHeight(int value)
/* 125:    */   {
/* 126:271 */     this.m_CustomHeight = value;
/* 127:    */   }
/* 128:    */   
/* 129:    */   public int getCustomHeight()
/* 130:    */   {
/* 131:281 */     return this.m_CustomHeight;
/* 132:    */   }
/* 133:    */   
/* 134:    */   protected abstract void generateOutput()
/* 135:    */     throws Exception;
/* 136:    */   
/* 137:    */   public void toOutput()
/* 138:    */     throws Exception
/* 139:    */   {
/* 140:301 */     if (getFile() == null) {
/* 141:302 */       throw new Exception("The file is not set!");
/* 142:    */     }
/* 143:303 */     if (getComponent() == null) {
/* 144:304 */       throw new Exception("The component is not set!");
/* 145:    */     }
/* 146:307 */     int oldWidth = getComponent().getWidth();
/* 147:308 */     int oldHeight = getComponent().getHeight();
/* 148:309 */     if (getUseCustomDimensions()) {
/* 149:310 */       getComponent().setSize(getCustomWidth(), getCustomHeight());
/* 150:    */     }
/* 151:312 */     generateOutput();
/* 152:315 */     if (getUseCustomDimensions()) {
/* 153:316 */       getComponent().setSize(oldWidth, oldHeight);
/* 154:    */     }
/* 155:    */   }
/* 156:    */   
/* 157:    */   public static void toOutput(JComponentWriter writer, JComponent comp, File file)
/* 158:    */     throws Exception
/* 159:    */   {
/* 160:328 */     toOutput(writer, comp, file, -1, -1);
/* 161:    */   }
/* 162:    */   
/* 163:    */   public static void toOutput(JComponentWriter writer, JComponent comp, File file, int width, int height)
/* 164:    */     throws Exception
/* 165:    */   {
/* 166:344 */     writer.setComponent(comp);
/* 167:345 */     writer.setFile(file);
/* 168:348 */     if ((width != -1) && (height != -1))
/* 169:    */     {
/* 170:349 */       writer.setUseCustomDimensions(true);
/* 171:350 */       writer.setCustomWidth(width);
/* 172:351 */       writer.setCustomHeight(height);
/* 173:    */     }
/* 174:354 */     writer.toOutput();
/* 175:    */   }
/* 176:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.visualize.JComponentWriter
 * JD-Core Version:    0.7.0.1
 */