/*   1:    */ package weka.core.converters;
/*   2:    */ 
/*   3:    */ import java.io.File;
/*   4:    */ import java.io.IOException;
/*   5:    */ import java.io.OutputStream;
/*   6:    */ import java.io.PrintStream;
/*   7:    */ import weka.core.Capabilities;
/*   8:    */ import weka.core.CapabilitiesHandler;
/*   9:    */ import weka.core.CapabilitiesIgnorer;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ 
/*  13:    */ public abstract class AbstractSaver
/*  14:    */   implements Saver, CapabilitiesHandler, CapabilitiesIgnorer
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = -27467499727819258L;
/*  17:    */   protected static final int WRITE = 0;
/*  18:    */   protected static final int WAIT = 1;
/*  19:    */   protected static final int CANCEL = 2;
/*  20:    */   protected static final int STRUCTURE_READY = 3;
/*  21:    */   private Instances m_instances;
/*  22:    */   protected int m_retrieval;
/*  23:    */   private int m_writeMode;
/*  24: 62 */   protected boolean m_DoNotCheckCapabilities = false;
/*  25:    */   
/*  26:    */   public String doNotCheckCapabilitiesTipText()
/*  27:    */   {
/*  28: 71 */     return "If set, saver capabilities are not checked (Use with caution to reduce runtime).";
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void setDoNotCheckCapabilities(boolean doNotCheckCapabilities)
/*  32:    */   {
/*  33: 82 */     this.m_DoNotCheckCapabilities = doNotCheckCapabilities;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public boolean getDoNotCheckCapabilities()
/*  37:    */   {
/*  38: 92 */     return this.m_DoNotCheckCapabilities;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void resetOptions()
/*  42:    */   {
/*  43:101 */     this.m_instances = null;
/*  44:102 */     this.m_writeMode = 1;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void resetStructure()
/*  48:    */   {
/*  49:108 */     this.m_instances = null;
/*  50:109 */     this.m_writeMode = 1;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public void setRetrieval(int mode)
/*  54:    */   {
/*  55:120 */     this.m_retrieval = mode;
/*  56:    */   }
/*  57:    */   
/*  58:    */   protected int getRetrieval()
/*  59:    */   {
/*  60:130 */     return this.m_retrieval;
/*  61:    */   }
/*  62:    */   
/*  63:    */   protected void setWriteMode(int mode)
/*  64:    */   {
/*  65:140 */     this.m_writeMode = mode;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public int getWriteMode()
/*  69:    */   {
/*  70:151 */     return this.m_writeMode;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void setInstances(Instances instances)
/*  74:    */   {
/*  75:162 */     Capabilities cap = getCapabilities();
/*  76:163 */     if (!cap.test(instances)) {
/*  77:164 */       throw new IllegalArgumentException(cap.getFailReason());
/*  78:    */     }
/*  79:167 */     if (this.m_retrieval == 2)
/*  80:    */     {
/*  81:168 */       if (setStructure(instances) == 2) {
/*  82:169 */         cancel();
/*  83:    */       }
/*  84:    */     }
/*  85:    */     else {
/*  86:172 */       this.m_instances = instances;
/*  87:    */     }
/*  88:    */   }
/*  89:    */   
/*  90:    */   public Instances getInstances()
/*  91:    */   {
/*  92:183 */     return this.m_instances;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public void setDestination(File file)
/*  96:    */     throws IOException
/*  97:    */   {
/*  98:195 */     throw new IOException("Writing to a file not supported");
/*  99:    */   }
/* 100:    */   
/* 101:    */   public void setDestination(OutputStream output)
/* 102:    */     throws IOException
/* 103:    */   {
/* 104:207 */     throw new IOException("Writing to an outputstream not supported");
/* 105:    */   }
/* 106:    */   
/* 107:    */   public Capabilities getCapabilities()
/* 108:    */   {
/* 109:219 */     Capabilities result = new Capabilities(this);
/* 110:    */     
/* 111:221 */     result.setMinimumNumberInstances(0);
/* 112:    */     
/* 113:223 */     return result;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public int setStructure(Instances headerInfo)
/* 117:    */   {
/* 118:235 */     Capabilities cap = getCapabilities();
/* 119:236 */     if (!cap.test(headerInfo)) {
/* 120:237 */       throw new IllegalArgumentException(cap.getFailReason());
/* 121:    */     }
/* 122:240 */     if ((this.m_writeMode == 1) && (headerInfo != null))
/* 123:    */     {
/* 124:241 */       this.m_instances = headerInfo;
/* 125:242 */       this.m_writeMode = 3;
/* 126:    */     }
/* 127:244 */     else if ((headerInfo == null) || (this.m_writeMode != 3) || (!headerInfo.equalHeaders(this.m_instances)))
/* 128:    */     {
/* 129:246 */       this.m_instances = null;
/* 130:247 */       if (this.m_writeMode != 1) {
/* 131:248 */         System.err.println("A structure cannot be set up during an active incremental saving process.");
/* 132:    */       }
/* 133:251 */       this.m_writeMode = 2;
/* 134:    */     }
/* 135:254 */     return this.m_writeMode;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void cancel()
/* 139:    */   {
/* 140:260 */     if (this.m_writeMode == 2) {
/* 141:261 */       resetOptions();
/* 142:    */     }
/* 143:    */   }
/* 144:    */   
/* 145:    */   public void writeIncremental(Instance i)
/* 146:    */     throws IOException
/* 147:    */   {
/* 148:277 */     throw new IOException("No Incremental saving possible.");
/* 149:    */   }
/* 150:    */   
/* 151:    */   public abstract void writeBatch()
/* 152:    */     throws IOException;
/* 153:    */   
/* 154:    */   public String getFileExtension()
/* 155:    */     throws Exception
/* 156:    */   {
/* 157:296 */     throw new Exception("Saving in a file not supported.");
/* 158:    */   }
/* 159:    */   
/* 160:    */   public void setFile(File file)
/* 161:    */     throws IOException
/* 162:    */   {
/* 163:308 */     throw new IOException("Saving in a file not supported.");
/* 164:    */   }
/* 165:    */   
/* 166:    */   public void setFilePrefix(String prefix)
/* 167:    */     throws Exception
/* 168:    */   {
/* 169:320 */     throw new Exception("Saving in a file not supported.");
/* 170:    */   }
/* 171:    */   
/* 172:    */   public String filePrefix()
/* 173:    */     throws Exception
/* 174:    */   {
/* 175:331 */     throw new Exception("Saving in a file not supported.");
/* 176:    */   }
/* 177:    */   
/* 178:    */   public void setDir(String dir)
/* 179:    */     throws IOException
/* 180:    */   {
/* 181:343 */     throw new IOException("Saving in a file not supported.");
/* 182:    */   }
/* 183:    */   
/* 184:    */   public void setDirAndPrefix(String relationName, String add)
/* 185:    */     throws IOException
/* 186:    */   {
/* 187:357 */     throw new IOException("Saving in a file not supported.");
/* 188:    */   }
/* 189:    */   
/* 190:    */   public String retrieveDir()
/* 191:    */     throws IOException
/* 192:    */   {
/* 193:368 */     throw new IOException("Saving in a file not supported.");
/* 194:    */   }
/* 195:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.converters.AbstractSaver
 * JD-Core Version:    0.7.0.1
 */