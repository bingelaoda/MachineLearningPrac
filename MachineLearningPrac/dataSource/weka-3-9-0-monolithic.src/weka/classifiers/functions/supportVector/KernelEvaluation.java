/*   1:    */ package weka.classifiers.functions.supportVector;
/*   2:    */ 
/*   3:    */ import java.io.BufferedReader;
/*   4:    */ import java.io.FileReader;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.util.Enumeration;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.Option;
/*   9:    */ import weka.core.OptionHandler;
/*  10:    */ import weka.core.RevisionHandler;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.Utils;
/*  13:    */ 
/*  14:    */ public class KernelEvaluation
/*  15:    */   implements RevisionHandler
/*  16:    */ {
/*  17:    */   protected StringBuffer m_Result;
/*  18:    */   protected double[][] m_Evaluations;
/*  19:    */   protected int m_NumEvals;
/*  20:    */   protected int m_NumCacheHits;
/*  21:    */   protected String[] m_Options;
/*  22:    */   
/*  23:    */   public KernelEvaluation()
/*  24:    */   {
/*  25: 63 */     this.m_Result = new StringBuffer();
/*  26: 64 */     this.m_Evaluations = new double[0][0];
/*  27: 65 */     this.m_Options = new String[0];
/*  28: 66 */     this.m_NumEvals = 0;
/*  29: 67 */     this.m_NumCacheHits = 0;
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void setUserOptions(String[] options)
/*  33:    */   {
/*  34: 76 */     this.m_Options = ((String[])options.clone());
/*  35:    */   }
/*  36:    */   
/*  37:    */   public String[] getUserOptions()
/*  38:    */   {
/*  39: 85 */     return (String[])this.m_Options.clone();
/*  40:    */   }
/*  41:    */   
/*  42:    */   protected static String makeOptionString(Kernel Kernel)
/*  43:    */   {
/*  44: 97 */     StringBuffer text = new StringBuffer();
/*  45:    */     
/*  46:    */ 
/*  47:100 */     text.append("\nGeneral options:\n\n");
/*  48:101 */     text.append("-t <training file>\n");
/*  49:102 */     text.append("\tThe name of the training file.\n");
/*  50:103 */     text.append("-c <class index>\n");
/*  51:104 */     text.append("\tSets index of class attribute (default: last).\n");
/*  52:107 */     if ((Kernel instanceof OptionHandler))
/*  53:    */     {
/*  54:108 */       text.append("\nOptions specific to " + Kernel.getClass().getName().replaceAll(".*\\.", "") + ":\n\n");
/*  55:    */       
/*  56:    */ 
/*  57:111 */       Enumeration<Option> enm = Kernel.listOptions();
/*  58:112 */       while (enm.hasMoreElements())
/*  59:    */       {
/*  60:113 */         Option option = (Option)enm.nextElement();
/*  61:114 */         text.append(option.synopsis() + "\n");
/*  62:115 */         text.append(option.description() + "\n");
/*  63:    */       }
/*  64:    */     }
/*  65:119 */     return text.toString();
/*  66:    */   }
/*  67:    */   
/*  68:    */   public static String evaluate(Kernel Kernel, String[] options)
/*  69:    */     throws Exception
/*  70:    */   {
/*  71:134 */     String trainFileString = "";
/*  72:    */     
/*  73:    */ 
/*  74:    */ 
/*  75:138 */     int classIndex = -1;
/*  76:143 */     if (Utils.getFlag('h', options)) {
/*  77:144 */       throw new Exception("\nHelp requested.\n" + makeOptionString(Kernel));
/*  78:    */     }
/*  79:    */     BufferedReader reader;
/*  80:    */     String[] userOptions;
/*  81:    */     try
/*  82:    */     {
/*  83:149 */       trainFileString = Utils.getOption('t', options);
/*  84:150 */       if (trainFileString.length() == 0) {
/*  85:151 */         throw new Exception("No training file given!");
/*  86:    */       }
/*  87:153 */       reader = new BufferedReader(new FileReader(trainFileString));
/*  88:    */       
/*  89:155 */       String classIndexString = Utils.getOption('c', options);
/*  90:156 */       if (classIndexString.length() != 0) {
/*  91:157 */         if (classIndexString.equals("first")) {
/*  92:158 */           classIndex = 1;
/*  93:159 */         } else if (classIndexString.equals("last")) {
/*  94:160 */           classIndex = -1;
/*  95:    */         } else {
/*  96:162 */           classIndex = Integer.parseInt(classIndexString);
/*  97:    */         }
/*  98:    */       }
/*  99:167 */       userOptions = (String[])options.clone();
/* 100:168 */       if ((Kernel instanceof OptionHandler)) {
/* 101:169 */         Kernel.setOptions(options);
/* 102:    */       }
/* 103:173 */       Utils.checkForRemainingOptions(options);
/* 104:    */     }
/* 105:    */     catch (Exception e)
/* 106:    */     {
/* 107:175 */       throw new Exception("\nWeka exception: " + e.getMessage() + "\n" + makeOptionString(Kernel));
/* 108:    */     }
/* 109:180 */     KernelEvaluation eval = new KernelEvaluation();
/* 110:181 */     eval.setUserOptions(userOptions);
/* 111:182 */     Instances train = new Instances(reader);
/* 112:183 */     if (classIndex == -1) {
/* 113:184 */       train.setClassIndex(train.numAttributes() - 1);
/* 114:    */     } else {
/* 115:186 */       train.setClassIndex(classIndex);
/* 116:    */     }
/* 117:189 */     return eval.evaluate(Kernel, train);
/* 118:    */   }
/* 119:    */   
/* 120:    */   public static String evaluate(String kernelString, String[] options)
/* 121:    */     throws Exception
/* 122:    */   {
/* 123:    */     Kernel kernel;
/* 124:    */     try
/* 125:    */     {
/* 126:206 */       kernel = (Kernel)Class.forName(kernelString).newInstance();
/* 127:    */     }
/* 128:    */     catch (Exception e)
/* 129:    */     {
/* 130:208 */       throw new Exception("Can't find class with name " + kernelString + '.');
/* 131:    */     }
/* 132:211 */     return evaluate(kernel, options);
/* 133:    */   }
/* 134:    */   
/* 135:    */   public String evaluate(Kernel kernel, Instances data)
/* 136:    */     throws Exception
/* 137:    */   {
/* 138:230 */     this.m_Result = new StringBuffer();
/* 139:    */     
/* 140:    */ 
/* 141:233 */     long startTime = System.currentTimeMillis();
/* 142:234 */     kernel.buildKernel(data);
/* 143:235 */     long endTime = System.currentTimeMillis();
/* 144:236 */     this.m_Result.append("\n=== Model ===\n\n");
/* 145:237 */     if (Utils.joinOptions(getUserOptions()).trim().length() != 0) {
/* 146:238 */       this.m_Result.append("Options: " + Utils.joinOptions(getUserOptions()) + "\n\n");
/* 147:    */     }
/* 148:241 */     this.m_Result.append(kernel.toString() + "\n");
/* 149:    */     
/* 150:    */ 
/* 151:244 */     this.m_Evaluations = new double[data.numInstances()][data.numInstances()];
/* 152:245 */     for (int n = 0; n < data.numInstances(); n++) {
/* 153:246 */       for (int i = n; i < data.numInstances(); i++) {
/* 154:247 */         this.m_Evaluations[n][i] = kernel.eval(n, i, data.instance(n));
/* 155:    */       }
/* 156:    */     }
/* 157:252 */     if ((kernel instanceof CachedKernel)) {
/* 158:253 */       for (n = 0; n < data.numInstances(); n++) {
/* 159:254 */         for (int i = n; i < data.numInstances(); i++) {
/* 160:255 */           this.m_Evaluations[n][i] = kernel.eval(n, i, data.instance(n));
/* 161:    */         }
/* 162:    */       }
/* 163:    */     }
/* 164:260 */     this.m_NumEvals = kernel.numEvals();
/* 165:261 */     this.m_NumCacheHits = kernel.numCacheHits();
/* 166:    */     
/* 167:    */ 
/* 168:264 */     this.m_Result.append("\n=== Evaluation ===\n\n");
/* 169:265 */     if ((kernel instanceof CachedKernel)) {
/* 170:266 */       this.m_Result.append("Cache size   : " + ((CachedKernel)kernel).getCacheSize() + "\n");
/* 171:    */     }
/* 172:269 */     this.m_Result.append("# Evaluations: " + this.m_NumEvals + "\n");
/* 173:270 */     this.m_Result.append("# Cache hits : " + this.m_NumCacheHits + "\n");
/* 174:271 */     this.m_Result.append("Elapsed time : " + (endTime - startTime) / 1000.0D + "s\n");
/* 175:    */     
/* 176:    */ 
/* 177:274 */     return this.m_Result.toString();
/* 178:    */   }
/* 179:    */   
/* 180:    */   public boolean equals(Object obj)
/* 181:    */   {
/* 182:286 */     if ((obj == null) || (!obj.getClass().equals(getClass()))) {
/* 183:287 */       return false;
/* 184:    */     }
/* 185:290 */     KernelEvaluation cmp = (KernelEvaluation)obj;
/* 186:292 */     if (this.m_NumEvals != cmp.m_NumEvals) {
/* 187:293 */       return false;
/* 188:    */     }
/* 189:295 */     if (this.m_NumCacheHits != cmp.m_NumCacheHits) {
/* 190:296 */       return false;
/* 191:    */     }
/* 192:299 */     if (this.m_Evaluations.length != cmp.m_Evaluations.length) {
/* 193:300 */       return false;
/* 194:    */     }
/* 195:302 */     for (int n = 0; n < this.m_Evaluations.length; n++) {
/* 196:303 */       for (int i = 0; i < this.m_Evaluations[n].length; i++) {
/* 197:304 */         if ((!Double.isNaN(this.m_Evaluations[n][i])) || (!Double.isNaN(cmp.m_Evaluations[n][i]))) {
/* 198:308 */           if (this.m_Evaluations[n][i] != cmp.m_Evaluations[n][i]) {
/* 199:309 */             return false;
/* 200:    */           }
/* 201:    */         }
/* 202:    */       }
/* 203:    */     }
/* 204:314 */     return true;
/* 205:    */   }
/* 206:    */   
/* 207:    */   public String toSummaryString()
/* 208:    */   {
/* 209:323 */     return toSummaryString("");
/* 210:    */   }
/* 211:    */   
/* 212:    */   public String toSummaryString(String title)
/* 213:    */   {
/* 214:335 */     StringBuffer result = new StringBuffer(title);
/* 215:336 */     if (title.length() != 0) {
/* 216:337 */       result.append("\n");
/* 217:    */     }
/* 218:339 */     result.append(this.m_Result);
/* 219:    */     
/* 220:341 */     return result.toString();
/* 221:    */   }
/* 222:    */   
/* 223:    */   public String toString()
/* 224:    */   {
/* 225:352 */     return toSummaryString();
/* 226:    */   }
/* 227:    */   
/* 228:    */   public String getRevision()
/* 229:    */   {
/* 230:362 */     return RevisionUtils.extract("$Revision: 10169 $");
/* 231:    */   }
/* 232:    */   
/* 233:    */   public static void main(String[] args)
/* 234:    */   {
/* 235:    */     try
/* 236:    */     {
/* 237:374 */       if (args.length == 0) {
/* 238:375 */         throw new Exception("The first argument must be the class name of a kernel");
/* 239:    */       }
/* 240:378 */       String kernel = args[0];
/* 241:379 */       args[0] = "";
/* 242:380 */       System.out.println(evaluate(kernel, args));
/* 243:    */     }
/* 244:    */     catch (Exception ex)
/* 245:    */     {
/* 246:382 */       ex.printStackTrace();
/* 247:383 */       System.err.println(ex.getMessage());
/* 248:    */     }
/* 249:    */   }
/* 250:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.supportVector.KernelEvaluation
 * JD-Core Version:    0.7.0.1
 */