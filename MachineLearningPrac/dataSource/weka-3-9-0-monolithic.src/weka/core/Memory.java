/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.lang.management.ManagementFactory;
/*   5:    */ import java.lang.management.MemoryMXBean;
/*   6:    */ import java.lang.management.MemoryUsage;
/*   7:    */ import javax.swing.JCheckBox;
/*   8:    */ import javax.swing.JOptionPane;
/*   9:    */ 
/*  10:    */ public class Memory
/*  11:    */   implements RevisionHandler
/*  12:    */ {
/*  13:    */   public static final long OUT_OF_MEMORY_THRESHOLD = 52428800L;
/*  14:    */   public static final long LOW_MEMORY_MINIMUM = 104857600L;
/*  15:    */   public static final long MAX_SLEEP_TIME = 10L;
/*  16: 48 */   protected boolean m_Enabled = true;
/*  17: 51 */   protected boolean m_UseGUI = false;
/*  18: 54 */   protected static MemoryMXBean m_MemoryMXBean = ;
/*  19: 58 */   protected MemoryUsage m_MemoryUsage = null;
/*  20: 61 */   protected long m_SleepTime = 10L;
/*  21:    */   
/*  22:    */   public Memory()
/*  23:    */   {
/*  24: 67 */     this(false);
/*  25:    */   }
/*  26:    */   
/*  27:    */   public Memory(boolean useGUI)
/*  28:    */   {
/*  29: 76 */     this.m_UseGUI = useGUI;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public boolean isEnabled()
/*  33:    */   {
/*  34: 85 */     return this.m_Enabled;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void setEnabled(boolean value)
/*  38:    */   {
/*  39: 94 */     this.m_Enabled = value;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public boolean getUseGUI()
/*  43:    */   {
/*  44:104 */     return this.m_UseGUI;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public long getInitial()
/*  48:    */   {
/*  49:114 */     this.m_MemoryUsage = m_MemoryMXBean.getHeapMemoryUsage();
/*  50:115 */     return this.m_MemoryUsage.getInit();
/*  51:    */   }
/*  52:    */   
/*  53:    */   public long getCurrent()
/*  54:    */   {
/*  55:125 */     this.m_MemoryUsage = m_MemoryMXBean.getHeapMemoryUsage();
/*  56:126 */     return this.m_MemoryUsage.getUsed();
/*  57:    */   }
/*  58:    */   
/*  59:    */   public long getMax()
/*  60:    */   {
/*  61:136 */     this.m_MemoryUsage = m_MemoryMXBean.getHeapMemoryUsage();
/*  62:137 */     return this.m_MemoryUsage.getMax();
/*  63:    */   }
/*  64:    */   
/*  65:    */   public boolean isOutOfMemory()
/*  66:    */   {
/*  67:    */     try
/*  68:    */     {
/*  69:151 */       Thread.sleep(this.m_SleepTime);
/*  70:    */     }
/*  71:    */     catch (InterruptedException ex)
/*  72:    */     {
/*  73:153 */       ex.printStackTrace();
/*  74:    */     }
/*  75:156 */     this.m_MemoryUsage = m_MemoryMXBean.getHeapMemoryUsage();
/*  76:157 */     if (isEnabled())
/*  77:    */     {
/*  78:159 */       long avail = this.m_MemoryUsage.getMax() - this.m_MemoryUsage.getUsed();
/*  79:160 */       if (avail > 52428800L)
/*  80:    */       {
/*  81:161 */         long num = (avail - 52428800L) / 5242880L + 1L;
/*  82:    */         
/*  83:163 */         this.m_SleepTime = ((2.0D * (Math.log(num) + 2.5D)));
/*  84:164 */         if (this.m_SleepTime > 10L) {
/*  85:165 */           this.m_SleepTime = 10L;
/*  86:    */         }
/*  87:    */       }
/*  88:170 */       return avail < 52428800L;
/*  89:    */     }
/*  90:172 */     return false;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public boolean memoryIsLow()
/*  94:    */   {
/*  95:183 */     this.m_MemoryUsage = m_MemoryMXBean.getHeapMemoryUsage();
/*  96:185 */     if (isEnabled())
/*  97:    */     {
/*  98:186 */       long lowThreshold = (0.2D * this.m_MemoryUsage.getMax());
/*  99:189 */       if (lowThreshold < 104857600L) {
/* 100:190 */         lowThreshold = 104857600L;
/* 101:    */       }
/* 102:193 */       long avail = this.m_MemoryUsage.getMax() - this.m_MemoryUsage.getUsed();
/* 103:    */       
/* 104:195 */       return avail < lowThreshold;
/* 105:    */     }
/* 106:197 */     return false;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public static double toMegaByte(long bytes)
/* 110:    */   {
/* 111:207 */     return bytes / 1048576.0D;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void showOutOfMemory()
/* 115:    */   {
/* 116:219 */     if ((!isEnabled()) || (this.m_MemoryUsage == null)) {
/* 117:220 */       return;
/* 118:    */     }
/* 119:223 */     System.gc();
/* 120:    */     
/* 121:225 */     String msg = "Not enough memory (less than 50MB left on heap). Please load a smaller dataset or use a larger heap size.\n- initial heap size:   " + Utils.doubleToString(toMegaByte(this.m_MemoryUsage.getInit()), 1) + "MB\n" + "- current memory (heap) used:  " + Utils.doubleToString(toMegaByte(this.m_MemoryUsage.getUsed()), 1) + "MB\n" + "- max. memory (heap) available: " + Utils.doubleToString(toMegaByte(this.m_MemoryUsage.getMax()), 1) + "MB\n" + "\n" + "Note:\n" + "The Java heap size can be specified with the -Xmx option.\n" + "E.g., to use 128MB as heap size, the command line looks like this:\n" + "   java -Xmx128m -classpath ...\n" + "This does NOT work in the SimpleCLI, the above java command refers\n" + "to the one with which Weka is started. See the Weka FAQ on the web\n" + "for further info.";
/* 122:    */     
/* 123:    */ 
/* 124:    */ 
/* 125:    */ 
/* 126:    */ 
/* 127:    */ 
/* 128:    */ 
/* 129:    */ 
/* 130:    */ 
/* 131:    */ 
/* 132:    */ 
/* 133:    */ 
/* 134:    */ 
/* 135:    */ 
/* 136:    */ 
/* 137:    */ 
/* 138:    */ 
/* 139:    */ 
/* 140:    */ 
/* 141:245 */     System.err.println(msg);
/* 142:247 */     if (getUseGUI()) {
/* 143:248 */       JOptionPane.showMessageDialog(null, msg, "OutOfMemory", 2);
/* 144:    */     }
/* 145:    */   }
/* 146:    */   
/* 147:    */   public boolean showMemoryIsLow()
/* 148:    */   {
/* 149:259 */     if ((!isEnabled()) || (this.m_MemoryUsage == null)) {
/* 150:260 */       return true;
/* 151:    */     }
/* 152:263 */     String msg = "Warning: memory is running low - available heap space is less than 20% of maximum or 100MB (whichever is greater)\n\n- initial heap size:   " + Utils.doubleToString(toMegaByte(this.m_MemoryUsage.getInit()), 1) + "MB\n" + "- current memory (heap) used:  " + Utils.doubleToString(toMegaByte(this.m_MemoryUsage.getUsed()), 1) + "MB\n" + "- max. memory (heap) available: " + Utils.doubleToString(toMegaByte(this.m_MemoryUsage.getMax()), 1) + "MB\n\n" + "Consider deleting some results before continuing.\nCheck the Weka FAQ " + "on the web for suggestions on how to save memory.\n" + "Note that Weka will shut down when less than 50MB remain." + "\nDo you wish to continue regardless?\n\n";
/* 153:    */     
/* 154:    */ 
/* 155:    */ 
/* 156:    */ 
/* 157:    */ 
/* 158:    */ 
/* 159:    */ 
/* 160:    */ 
/* 161:    */ 
/* 162:    */ 
/* 163:    */ 
/* 164:    */ 
/* 165:    */ 
/* 166:    */ 
/* 167:    */ 
/* 168:279 */     System.err.println(msg);
/* 169:281 */     if ((getUseGUI()) && 
/* 170:282 */       (!Utils.getDontShowDialog("weka.core.Memory.LowMemoryWarning")))
/* 171:    */     {
/* 172:283 */       JCheckBox dontShow = new JCheckBox("Do not show this message again");
/* 173:284 */       Object[] stuff = new Object[2];
/* 174:285 */       stuff[0] = msg;
/* 175:286 */       stuff[1] = dontShow;
/* 176:    */       
/* 177:288 */       int result = JOptionPane.showConfirmDialog(null, stuff, "Memory", 0);
/* 178:291 */       if (dontShow.isSelected()) {
/* 179:    */         try
/* 180:    */         {
/* 181:293 */           Utils.setDontShowDialog("weka.core.Memory.LowMemoryWarning");
/* 182:    */         }
/* 183:    */         catch (Exception ex) {}
/* 184:    */       }
/* 185:299 */       return result == 0;
/* 186:    */     }
/* 187:303 */     return true;
/* 188:    */   }
/* 189:    */   
/* 190:    */   public void stopThreads()
/* 191:    */   {
/* 192:315 */     Thread[] thGroup = new Thread[Thread.activeCount()];
/* 193:316 */     Thread.enumerate(thGroup);
/* 194:318 */     for (int i = 0; i < thGroup.length; i++)
/* 195:    */     {
/* 196:319 */       Thread t = thGroup[i];
/* 197:320 */       if ((t != null) && 
/* 198:321 */         (t != Thread.currentThread())) {
/* 199:322 */         if (t.getName().startsWith("Thread")) {
/* 200:323 */           t.stop();
/* 201:324 */         } else if (t.getName().startsWith("AWT-EventQueue")) {
/* 202:325 */           t.stop();
/* 203:    */         }
/* 204:    */       }
/* 205:    */     }
/* 206:331 */     thGroup = null;
/* 207:    */     
/* 208:333 */     System.gc();
/* 209:    */   }
/* 210:    */   
/* 211:    */   public String getRevision()
/* 212:    */   {
/* 213:343 */     return RevisionUtils.extract("$Revision: 11271 $");
/* 214:    */   }
/* 215:    */   
/* 216:    */   public static void main(String[] args)
/* 217:    */   {
/* 218:352 */     Memory mem = new Memory();
/* 219:353 */     System.out.println("Initial memory: " + Utils.doubleToString(toMegaByte(mem.getInitial()), 1) + "MB" + " (" + mem.getInitial() + ")");
/* 220:    */     
/* 221:    */ 
/* 222:356 */     System.out.println("Max memory: " + Utils.doubleToString(toMegaByte(mem.getMax()), 1) + "MB" + " (" + mem.getMax() + ")");
/* 223:    */   }
/* 224:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Memory
 * JD-Core Version:    0.7.0.1
 */