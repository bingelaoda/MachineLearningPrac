/*   1:    */ package weka.classifiers.functions.supportVector;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.classifiers.functions.SMOreg;
/*   8:    */ import weka.core.Attribute;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.OptionHandler;
/*  13:    */ import weka.core.RevisionHandler;
/*  14:    */ import weka.core.RevisionUtils;
/*  15:    */ import weka.core.SelectedTag;
/*  16:    */ import weka.core.Tag;
/*  17:    */ import weka.core.Utils;
/*  18:    */ 
/*  19:    */ public class RegOptimizer
/*  20:    */   implements OptionHandler, Serializable, RevisionHandler
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = -2198266997254461814L;
/*  23:    */   public double[] m_alpha;
/*  24:    */   public double[] m_alphaStar;
/*  25:    */   protected double m_b;
/*  26: 93 */   protected double m_epsilon = 0.001D;
/*  27: 96 */   protected double m_C = 1.0D;
/*  28:    */   protected double[] m_target;
/*  29:    */   protected Instances m_data;
/*  30:    */   protected Kernel m_kernel;
/*  31:108 */   protected int m_classIndex = -1;
/*  32:111 */   protected int m_nInstances = -1;
/*  33:    */   protected Random m_random;
/*  34:117 */   protected int m_nSeed = 1;
/*  35:    */   protected SMOset m_supportVectors;
/*  36:123 */   protected long m_nEvals = 0L;
/*  37:126 */   protected int m_nCacheHits = -1;
/*  38:    */   protected double[] m_weights;
/*  39:    */   protected double[] m_sparseWeights;
/*  40:    */   protected int[] m_sparseIndices;
/*  41:139 */   protected boolean m_bModelBuilt = false;
/*  42:142 */   protected SMOreg m_SVM = null;
/*  43:    */   
/*  44:    */   public RegOptimizer()
/*  45:    */   {
/*  46:149 */     this.m_random = new Random(this.m_nSeed);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public Enumeration<Option> listOptions()
/*  50:    */   {
/*  51:159 */     Vector<Option> result = new Vector();
/*  52:    */     
/*  53:161 */     result.addElement(new Option("\tThe epsilon parameter in epsilon-insensitive loss function.\n\t(default 1.0e-3)", "L", 1, "-L <double>"));
/*  54:    */     
/*  55:    */ 
/*  56:    */ 
/*  57:    */ 
/*  58:    */ 
/*  59:    */ 
/*  60:    */ 
/*  61:169 */     result.addElement(new Option("\tThe random number seed.\n\t(default 1)", "W", 1, "-W <double>"));
/*  62:    */     
/*  63:    */ 
/*  64:172 */     return result.elements();
/*  65:    */   }
/*  66:    */   
/*  67:    */   public void setOptions(String[] options)
/*  68:    */     throws Exception
/*  69:    */   {
/*  70:203 */     String tmpStr = Utils.getOption('L', options);
/*  71:204 */     if (tmpStr.length() != 0) {
/*  72:205 */       setEpsilonParameter(Double.parseDouble(tmpStr));
/*  73:    */     } else {
/*  74:207 */       setEpsilonParameter(0.001D);
/*  75:    */     }
/*  76:216 */     tmpStr = Utils.getOption('W', options);
/*  77:217 */     if (tmpStr.length() != 0) {
/*  78:218 */       setSeed(Integer.parseInt(tmpStr));
/*  79:    */     } else {
/*  80:220 */       setSeed(1);
/*  81:    */     }
/*  82:    */   }
/*  83:    */   
/*  84:    */   public String[] getOptions()
/*  85:    */   {
/*  86:231 */     Vector<String> result = new Vector();
/*  87:    */     
/*  88:233 */     result.add("-L");
/*  89:234 */     result.add("" + getEpsilonParameter());
/*  90:    */     
/*  91:236 */     result.add("-W");
/*  92:237 */     result.add("" + getSeed());
/*  93:    */     
/*  94:    */ 
/*  95:    */ 
/*  96:    */ 
/*  97:    */ 
/*  98:243 */     return (String[])result.toArray(new String[result.size()]);
/*  99:    */   }
/* 100:    */   
/* 101:    */   public boolean modelBuilt()
/* 102:    */   {
/* 103:252 */     return this.m_bModelBuilt;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public void setSMOReg(SMOreg value)
/* 107:    */   {
/* 108:261 */     this.m_SVM = value;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public long getKernelEvaluations()
/* 112:    */   {
/* 113:270 */     return this.m_nEvals;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public int getCacheHits()
/* 117:    */   {
/* 118:279 */     return this.m_nCacheHits;
/* 119:    */   }
/* 120:    */   
/* 121:    */   protected void init(Instances data)
/* 122:    */     throws Exception
/* 123:    */   {
/* 124:289 */     if (this.m_SVM == null) {
/* 125:290 */       throw new Exception("SVM not initialized in optimizer. Use RegOptimizer.setSVMReg()");
/* 126:    */     }
/* 127:293 */     this.m_C = this.m_SVM.getC();
/* 128:294 */     this.m_data = data;
/* 129:295 */     this.m_classIndex = data.classIndex();
/* 130:296 */     this.m_nInstances = data.numInstances();
/* 131:    */     
/* 132:    */ 
/* 133:299 */     this.m_kernel = Kernel.makeCopy(this.m_SVM.getKernel());
/* 134:300 */     this.m_kernel.buildKernel(data);
/* 135:    */     
/* 136:    */ 
/* 137:303 */     this.m_target = new double[this.m_nInstances];
/* 138:304 */     for (int i = 0; i < this.m_nInstances; i++) {
/* 139:305 */       this.m_target[i] = data.instance(i).classValue();
/* 140:    */     }
/* 141:308 */     this.m_random = new Random(this.m_nSeed);
/* 142:    */     
/* 143:    */ 
/* 144:311 */     this.m_alpha = new double[this.m_target.length];
/* 145:312 */     this.m_alphaStar = new double[this.m_target.length];
/* 146:    */     
/* 147:314 */     this.m_supportVectors = new SMOset(this.m_nInstances);
/* 148:    */     
/* 149:316 */     this.m_b = 0.0D;
/* 150:317 */     this.m_nEvals = 0L;
/* 151:318 */     this.m_nCacheHits = -1;
/* 152:    */   }
/* 153:    */   
/* 154:    */   protected void wrapUp()
/* 155:    */     throws Exception
/* 156:    */   {
/* 157:328 */     this.m_target = null;
/* 158:    */     
/* 159:330 */     this.m_nEvals = this.m_kernel.numEvals();
/* 160:331 */     this.m_nCacheHits = this.m_kernel.numCacheHits();
/* 161:333 */     if (((this.m_SVM.getKernel() instanceof PolyKernel)) && (((PolyKernel)this.m_SVM.getKernel()).getExponent() == 1.0D))
/* 162:    */     {
/* 163:336 */       double[] weights = new double[this.m_data.numAttributes()];
/* 164:337 */       for (int k = this.m_supportVectors.getNext(-1); k != -1; k = this.m_supportVectors.getNext(k)) {
/* 165:339 */         for (int j = 0; j < weights.length; j++) {
/* 166:340 */           if (j != this.m_classIndex) {
/* 167:341 */             weights[j] += (this.m_alpha[k] - this.m_alphaStar[k]) * this.m_data.instance(k).value(j);
/* 168:    */           }
/* 169:    */         }
/* 170:    */       }
/* 171:346 */       this.m_weights = weights;
/* 172:    */       
/* 173:    */ 
/* 174:349 */       this.m_alpha = null;
/* 175:350 */       this.m_alphaStar = null;
/* 176:351 */       this.m_kernel = null;
/* 177:    */     }
/* 178:    */     else
/* 179:    */     {
/* 180:354 */       this.m_kernel.clean();
/* 181:    */     }
/* 182:356 */     this.m_bModelBuilt = true;
/* 183:    */   }
/* 184:    */   
/* 185:    */   protected double getScore()
/* 186:    */     throws Exception
/* 187:    */   {
/* 188:366 */     double res = 0.0D;
/* 189:367 */     double t = 0.0D;double t2 = 0.0D;
/* 190:368 */     for (int i = 0; i < this.m_nInstances; i++)
/* 191:    */     {
/* 192:369 */       for (int j = 0; j < this.m_nInstances; j++) {
/* 193:370 */         t += (this.m_alpha[i] - this.m_alphaStar[i]) * (this.m_alpha[j] - this.m_alphaStar[j]) * this.m_kernel.eval(i, j, this.m_data.instance(i));
/* 194:    */       }
/* 195:389 */       t2 += this.m_target[i] * (this.m_alpha[i] - this.m_alphaStar[i]) - this.m_epsilon * (this.m_alpha[i] + this.m_alphaStar[i]);
/* 196:    */     }
/* 197:394 */     res += -0.5D * t + t2;
/* 198:395 */     return res;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public void buildClassifier(Instances data)
/* 202:    */     throws Exception
/* 203:    */   {
/* 204:406 */     throw new Exception("Don't call this directly, use subclass instead");
/* 205:    */   }
/* 206:    */   
/* 207:    */   protected double SVMOutput(int index)
/* 208:    */     throws Exception
/* 209:    */   {
/* 210:438 */     double result = -this.m_b;
/* 211:439 */     for (int i = this.m_supportVectors.getNext(-1); i != -1; i = this.m_supportVectors.getNext(i)) {
/* 212:441 */       result += (this.m_alpha[i] - this.m_alphaStar[i]) * this.m_kernel.eval(index, i, this.m_data.instance(index));
/* 213:    */     }
/* 214:444 */     return result;
/* 215:    */   }
/* 216:    */   
/* 217:    */   public double SVMOutput(Instance inst)
/* 218:    */     throws Exception
/* 219:    */   {
/* 220:455 */     double result = -this.m_b;
/* 221:457 */     if (this.m_weights != null) {
/* 222:459 */       for (int i = 0; i < inst.numValues(); i++) {
/* 223:460 */         if (inst.index(i) != this.m_classIndex) {
/* 224:461 */           result += this.m_weights[inst.index(i)] * inst.valueSparse(i);
/* 225:    */         }
/* 226:    */       }
/* 227:    */     } else {
/* 228:465 */       for (int i = this.m_supportVectors.getNext(-1); i != -1; i = this.m_supportVectors.getNext(i)) {
/* 229:467 */         result += (this.m_alpha[i] - this.m_alphaStar[i]) * this.m_kernel.eval(-1, i, inst);
/* 230:    */       }
/* 231:    */     }
/* 232:470 */     return result;
/* 233:    */   }
/* 234:    */   
/* 235:    */   public String seedTipText()
/* 236:    */   {
/* 237:480 */     return "Seed for random number generator.";
/* 238:    */   }
/* 239:    */   
/* 240:    */   public int getSeed()
/* 241:    */   {
/* 242:489 */     return this.m_nSeed;
/* 243:    */   }
/* 244:    */   
/* 245:    */   public void setSeed(int value)
/* 246:    */   {
/* 247:498 */     this.m_nSeed = value;
/* 248:    */   }
/* 249:    */   
/* 250:    */   public String epsilonParameterTipText()
/* 251:    */   {
/* 252:508 */     return "The epsilon parameter of the epsilon insensitive loss function.(default 0.001).";
/* 253:    */   }
/* 254:    */   
/* 255:    */   public double getEpsilonParameter()
/* 256:    */   {
/* 257:518 */     return this.m_epsilon;
/* 258:    */   }
/* 259:    */   
/* 260:    */   public void setEpsilonParameter(double v)
/* 261:    */   {
/* 262:528 */     this.m_epsilon = v;
/* 263:    */   }
/* 264:    */   
/* 265:    */   public String toString()
/* 266:    */   {
/* 267:538 */     StringBuffer text = new StringBuffer();
/* 268:539 */     text.append("SMOreg\n\n");
/* 269:540 */     if (this.m_weights != null)
/* 270:    */     {
/* 271:541 */       text.append("weights (not support vectors):\n");
/* 272:543 */       for (int i = 0; i < this.m_data.numAttributes(); i++) {
/* 273:544 */         if (i != this.m_classIndex)
/* 274:    */         {
/* 275:545 */           text.append((this.m_weights[i] >= 0.0D ? " + " : " - ") + Utils.doubleToString(Math.abs(this.m_weights[i]), 12, 4) + " * ");
/* 276:547 */           if (this.m_SVM.getFilterType().getSelectedTag().getID() == 1) {
/* 277:548 */             text.append("(standardized) ");
/* 278:549 */           } else if (this.m_SVM.getFilterType().getSelectedTag().getID() == 0) {
/* 279:550 */             text.append("(normalized) ");
/* 280:    */           }
/* 281:552 */           text.append(this.m_data.attribute(i).name() + "\n");
/* 282:    */         }
/* 283:    */       }
/* 284:    */     }
/* 285:    */     else
/* 286:    */     {
/* 287:557 */       text.append("Support vectors:\n");
/* 288:558 */       for (int i = 0; i < this.m_nInstances; i++)
/* 289:    */       {
/* 290:559 */         if (this.m_alpha[i] > 0.0D) {
/* 291:560 */           text.append("+" + this.m_alpha[i] + " * k[" + i + "]\n");
/* 292:    */         }
/* 293:562 */         if (this.m_alphaStar[i] > 0.0D) {
/* 294:563 */           text.append("-" + this.m_alphaStar[i] + " * k[" + i + "]\n");
/* 295:    */         }
/* 296:    */       }
/* 297:    */     }
/* 298:568 */     text.append((this.m_b <= 0.0D ? " + " : " - ") + Utils.doubleToString(Math.abs(this.m_b), 12, 4) + "\n\n");
/* 299:    */     
/* 300:    */ 
/* 301:571 */     text.append("\n\nNumber of kernel evaluations: " + this.m_nEvals);
/* 302:572 */     if ((this.m_nCacheHits >= 0) && (this.m_nEvals > 0L))
/* 303:    */     {
/* 304:573 */       double hitRatio = 1.0D - this.m_nEvals * 1.0D / (this.m_nCacheHits + this.m_nEvals);
/* 305:574 */       text.append(" (" + Utils.doubleToString(hitRatio * 100.0D, 7, 3).trim() + "% cached)");
/* 306:    */     }
/* 307:578 */     return text.toString();
/* 308:    */   }
/* 309:    */   
/* 310:    */   public String getRevision()
/* 311:    */   {
/* 312:588 */     return RevisionUtils.extract("$Revision: 12533 $");
/* 313:    */   }
/* 314:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.supportVector.RegOptimizer
 * JD-Core Version:    0.7.0.1
 */