/*   1:    */ package weka.classifiers.functions.supportVector;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.CapabilitiesHandler;
/*   8:    */ import weka.core.CapabilitiesIgnorer;
/*   9:    */ import weka.core.Copyable;
/*  10:    */ import weka.core.Instance;
/*  11:    */ import weka.core.Instances;
/*  12:    */ import weka.core.Option;
/*  13:    */ import weka.core.OptionHandler;
/*  14:    */ import weka.core.RevisionHandler;
/*  15:    */ import weka.core.RevisionUtils;
/*  16:    */ import weka.core.SerializedObject;
/*  17:    */ import weka.core.Utils;
/*  18:    */ 
/*  19:    */ public abstract class Kernel
/*  20:    */   implements Serializable, OptionHandler, CapabilitiesHandler, CapabilitiesIgnorer, RevisionHandler
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = -6102771099905817064L;
/*  23:    */   protected Instances m_data;
/*  24: 60 */   protected boolean m_Debug = false;
/*  25: 63 */   protected boolean m_ChecksTurnedOff = false;
/*  26: 66 */   protected boolean m_DoNotCheckCapabilities = false;
/*  27:    */   
/*  28:    */   public String doNotCheckCapabilitiesTipText()
/*  29:    */   {
/*  30: 75 */     return "If set, associator capabilities are not checked before associator is built (Use with caution to reduce runtime).";
/*  31:    */   }
/*  32:    */   
/*  33:    */   public void setDoNotCheckCapabilities(boolean doNotCheckCapabilities)
/*  34:    */   {
/*  35: 87 */     this.m_DoNotCheckCapabilities = doNotCheckCapabilities;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public boolean getDoNotCheckCapabilities()
/*  39:    */   {
/*  40: 98 */     return this.m_DoNotCheckCapabilities;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public abstract String globalInfo();
/*  44:    */   
/*  45:    */   public abstract double eval(int paramInt1, int paramInt2, Instance paramInstance)
/*  46:    */     throws Exception;
/*  47:    */   
/*  48:    */   public abstract void clean();
/*  49:    */   
/*  50:    */   public abstract int numEvals();
/*  51:    */   
/*  52:    */   public abstract int numCacheHits();
/*  53:    */   
/*  54:    */   public Enumeration<Option> listOptions()
/*  55:    */   {
/*  56:151 */     Vector<Option> result = new Vector();
/*  57:    */     
/*  58:153 */     result.addElement(new Option("\tEnables debugging output (if available) to be printed.\n\t(default: off)", "output-debug-info", 0, "-output-debug-info"));
/*  59:    */     
/*  60:    */ 
/*  61:    */ 
/*  62:157 */     result.addElement(new Option("\tTurns off all checks - use with caution!\n\t(default: checks on)", "no-checks", 0, "-no-checks"));
/*  63:    */     
/*  64:    */ 
/*  65:160 */     return result.elements();
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void setOptions(String[] options)
/*  69:    */     throws Exception
/*  70:    */   {
/*  71:172 */     setDebug(Utils.getFlag("output-debug-info", options));
/*  72:    */     
/*  73:174 */     setChecksTurnedOff(Utils.getFlag("no-checks", options));
/*  74:    */     
/*  75:176 */     Utils.checkForRemainingOptions(options);
/*  76:    */   }
/*  77:    */   
/*  78:    */   public String[] getOptions()
/*  79:    */   {
/*  80:186 */     Vector<String> result = new Vector();
/*  81:188 */     if (getDebug()) {
/*  82:189 */       result.add("-output-debug-info");
/*  83:    */     }
/*  84:192 */     if (getChecksTurnedOff()) {
/*  85:193 */       result.add("-no-checks");
/*  86:    */     }
/*  87:196 */     return (String[])result.toArray(new String[result.size()]);
/*  88:    */   }
/*  89:    */   
/*  90:    */   public void setDebug(boolean value)
/*  91:    */   {
/*  92:206 */     this.m_Debug = value;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public boolean getDebug()
/*  96:    */   {
/*  97:215 */     return this.m_Debug;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public String debugTipText()
/* 101:    */   {
/* 102:225 */     return "Turns on the output of debugging information.";
/* 103:    */   }
/* 104:    */   
/* 105:    */   public void setChecksTurnedOff(boolean value)
/* 106:    */   {
/* 107:235 */     this.m_ChecksTurnedOff = value;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public boolean getChecksTurnedOff()
/* 111:    */   {
/* 112:244 */     return this.m_ChecksTurnedOff;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public String checksTurnedOffTipText()
/* 116:    */   {
/* 117:254 */     return "Turns time-consuming checks off - use with caution.";
/* 118:    */   }
/* 119:    */   
/* 120:    */   protected void initVars(Instances data)
/* 121:    */   {
/* 122:263 */     this.m_data = data;
/* 123:    */   }
/* 124:    */   
/* 125:    */   public Capabilities getCapabilities()
/* 126:    */   {
/* 127:275 */     Capabilities result = new Capabilities(this);
/* 128:276 */     result.enableAll();
/* 129:    */     
/* 130:278 */     return result;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public String getRevision()
/* 134:    */   {
/* 135:288 */     return RevisionUtils.extract("$Revision: 11013 $");
/* 136:    */   }
/* 137:    */   
/* 138:    */   public void buildKernel(Instances data)
/* 139:    */     throws Exception
/* 140:    */   {
/* 141:299 */     if (!getChecksTurnedOff()) {
/* 142:300 */       getCapabilities().testWithFail(data);
/* 143:    */     }
/* 144:303 */     initVars(data);
/* 145:    */   }
/* 146:    */   
/* 147:    */   public static Kernel makeCopy(Kernel kernel)
/* 148:    */     throws Exception
/* 149:    */   {
/* 150:315 */     if ((kernel instanceof Copyable)) {
/* 151:316 */       return (Kernel)((Copyable)kernel).copy();
/* 152:    */     }
/* 153:318 */     return (Kernel)new SerializedObject(kernel).getObject();
/* 154:    */   }
/* 155:    */   
/* 156:    */   public static Kernel[] makeCopies(Kernel model, int num)
/* 157:    */     throws Exception
/* 158:    */   {
/* 159:331 */     if (model == null) {
/* 160:332 */       throw new Exception("No model kernel set");
/* 161:    */     }
/* 162:335 */     Kernel[] kernels = new Kernel[num];
/* 163:336 */     if ((model instanceof Copyable))
/* 164:    */     {
/* 165:337 */       for (int i = 0; i < kernels.length; i++) {
/* 166:338 */         kernels[i] = ((Kernel)((Copyable)model).copy());
/* 167:    */       }
/* 168:    */     }
/* 169:    */     else
/* 170:    */     {
/* 171:341 */       SerializedObject so = new SerializedObject(model);
/* 172:342 */       for (int i = 0; i < kernels.length; i++) {
/* 173:343 */         kernels[i] = ((Kernel)so.getObject());
/* 174:    */       }
/* 175:    */     }
/* 176:347 */     return kernels;
/* 177:    */   }
/* 178:    */   
/* 179:    */   public static Kernel forName(String kernelName, String[] options)
/* 180:    */     throws Exception
/* 181:    */   {
/* 182:364 */     return (Kernel)Utils.forName(Kernel.class, kernelName, options);
/* 183:    */   }
/* 184:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.supportVector.Kernel
 * JD-Core Version:    0.7.0.1
 */