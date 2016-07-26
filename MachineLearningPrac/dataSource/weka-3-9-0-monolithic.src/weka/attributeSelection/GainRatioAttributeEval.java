/*   1:    */ package weka.attributeSelection;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.ContingencyTables;
/*   9:    */ import weka.core.Instance;
/*  10:    */ import weka.core.Instances;
/*  11:    */ import weka.core.Option;
/*  12:    */ import weka.core.OptionHandler;
/*  13:    */ import weka.core.RevisionUtils;
/*  14:    */ import weka.core.Utils;
/*  15:    */ import weka.filters.Filter;
/*  16:    */ import weka.filters.supervised.attribute.Discretize;
/*  17:    */ 
/*  18:    */ public class GainRatioAttributeEval
/*  19:    */   extends ASEvaluation
/*  20:    */   implements AttributeEvaluator, OptionHandler
/*  21:    */ {
/*  22:    */   static final long serialVersionUID = -8504656625598579926L;
/*  23:    */   private Instances m_trainInstances;
/*  24:    */   private int m_classIndex;
/*  25:    */   private int m_numInstances;
/*  26:    */   private int m_numClasses;
/*  27:    */   private boolean m_missing_merge;
/*  28:    */   
/*  29:    */   public String globalInfo()
/*  30:    */   {
/*  31: 91 */     return "GainRatioAttributeEval :\n\nEvaluates the worth of an attribute by measuring the gain ratio with respect to the class.\n\nGainR(Class, Attribute) = (H(Class) - H(Class | Attribute)) / H(Attribute).\n";
/*  32:    */   }
/*  33:    */   
/*  34:    */   public GainRatioAttributeEval()
/*  35:    */   {
/*  36:101 */     resetOptions();
/*  37:    */   }
/*  38:    */   
/*  39:    */   public Enumeration<Option> listOptions()
/*  40:    */   {
/*  41:111 */     Vector<Option> newVector = new Vector(1);
/*  42:112 */     newVector.addElement(new Option("\ttreat missing values as a seperate value.", "M", 0, "-M"));
/*  43:    */     
/*  44:114 */     return newVector.elements();
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setOptions(String[] options)
/*  48:    */     throws Exception
/*  49:    */   {
/*  50:136 */     resetOptions();
/*  51:137 */     setMissingMerge(!Utils.getFlag('M', options));
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String missingMergeTipText()
/*  55:    */   {
/*  56:147 */     return "Distribute counts for missing values. Counts are distributed across other values in proportion to their frequency. Otherwise, missing is treated as a separate value.";
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setMissingMerge(boolean b)
/*  60:    */   {
/*  61:158 */     this.m_missing_merge = b;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public boolean getMissingMerge()
/*  65:    */   {
/*  66:167 */     return this.m_missing_merge;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public String[] getOptions()
/*  70:    */   {
/*  71:177 */     String[] options = new String[1];
/*  72:179 */     if (!getMissingMerge()) {
/*  73:180 */       options[0] = "-M";
/*  74:    */     } else {
/*  75:182 */       options[0] = "";
/*  76:    */     }
/*  77:185 */     return options;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public Capabilities getCapabilities()
/*  81:    */   {
/*  82:196 */     Capabilities result = super.getCapabilities();
/*  83:197 */     result.disableAll();
/*  84:    */     
/*  85:    */ 
/*  86:200 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  87:201 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  88:202 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  89:203 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  90:    */     
/*  91:    */ 
/*  92:206 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  93:207 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  94:    */     
/*  95:209 */     return result;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public void buildEvaluator(Instances data)
/*  99:    */     throws Exception
/* 100:    */   {
/* 101:223 */     getCapabilities().testWithFail(data);
/* 102:    */     
/* 103:225 */     this.m_trainInstances = data;
/* 104:226 */     this.m_classIndex = this.m_trainInstances.classIndex();
/* 105:227 */     this.m_numInstances = this.m_trainInstances.numInstances();
/* 106:228 */     Discretize disTransform = new Discretize();
/* 107:229 */     disTransform.setUseBetterEncoding(true);
/* 108:230 */     disTransform.setInputFormat(this.m_trainInstances);
/* 109:231 */     this.m_trainInstances = Filter.useFilter(this.m_trainInstances, disTransform);
/* 110:232 */     this.m_numClasses = this.m_trainInstances.attribute(this.m_classIndex).numValues();
/* 111:    */   }
/* 112:    */   
/* 113:    */   protected void resetOptions()
/* 114:    */   {
/* 115:239 */     this.m_trainInstances = null;
/* 116:240 */     this.m_missing_merge = true;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public double evaluateAttribute(int attribute)
/* 120:    */     throws Exception
/* 121:    */   {
/* 122:255 */     double sum = 0.0D;
/* 123:256 */     int ni = this.m_trainInstances.attribute(attribute).numValues() + 1;
/* 124:257 */     int nj = this.m_numClasses + 1;
/* 125:    */     
/* 126:    */ 
/* 127:260 */     double temp = 0.0D;
/* 128:261 */     double[] sumi = new double[ni];
/* 129:262 */     double[] sumj = new double[nj];
/* 130:263 */     double[][] counts = new double[ni][nj];
/* 131:264 */     sumi = new double[ni];
/* 132:265 */     sumj = new double[nj];
/* 133:267 */     for (int i = 0; i < ni; i++)
/* 134:    */     {
/* 135:268 */       sumi[i] = 0.0D;
/* 136:270 */       for (int j = 0; j < nj; j++)
/* 137:    */       {
/* 138:271 */         sumj[j] = 0.0D;
/* 139:272 */         counts[i][j] = 0.0D;
/* 140:    */       }
/* 141:    */     }
/* 142:277 */     for (i = 0; i < this.m_numInstances; i++)
/* 143:    */     {
/* 144:278 */       Instance inst = this.m_trainInstances.instance(i);
/* 145:    */       int ii;
/* 146:    */       int ii;
/* 147:280 */       if (inst.isMissing(attribute)) {
/* 148:281 */         ii = ni - 1;
/* 149:    */       } else {
/* 150:283 */         ii = (int)inst.value(attribute);
/* 151:    */       }
/* 152:    */       int jj;
/* 153:    */       int jj;
/* 154:286 */       if (inst.isMissing(this.m_classIndex)) {
/* 155:287 */         jj = nj - 1;
/* 156:    */       } else {
/* 157:289 */         jj = (int)inst.value(this.m_classIndex);
/* 158:    */       }
/* 159:292 */       counts[ii][jj] += inst.weight();
/* 160:    */     }
/* 161:296 */     for (i = 0; i < ni; i++)
/* 162:    */     {
/* 163:297 */       sumi[i] = 0.0D;
/* 164:299 */       for (int j = 0; j < nj; j++)
/* 165:    */       {
/* 166:300 */         sumi[i] += counts[i][j];
/* 167:301 */         sum += counts[i][j];
/* 168:    */       }
/* 169:    */     }
/* 170:306 */     for (int j = 0; j < nj; j++)
/* 171:    */     {
/* 172:307 */       sumj[j] = 0.0D;
/* 173:309 */       for (i = 0; i < ni; i++) {
/* 174:310 */         sumj[j] += counts[i][j];
/* 175:    */       }
/* 176:    */     }
/* 177:315 */     if ((this.m_missing_merge) && (sumi[(ni - 1)] < sum) && (sumj[(nj - 1)] < sum))
/* 178:    */     {
/* 179:316 */       double[] i_copy = new double[sumi.length];
/* 180:317 */       double[] j_copy = new double[sumj.length];
/* 181:318 */       double[][] counts_copy = new double[sumi.length][sumj.length];
/* 182:320 */       for (i = 0; i < ni; i++) {
/* 183:321 */         System.arraycopy(counts[i], 0, counts_copy[i], 0, sumj.length);
/* 184:    */       }
/* 185:324 */       System.arraycopy(sumi, 0, i_copy, 0, sumi.length);
/* 186:325 */       System.arraycopy(sumj, 0, j_copy, 0, sumj.length);
/* 187:326 */       double total_missing = sumi[(ni - 1)] + sumj[(nj - 1)] - counts[(ni - 1)][(nj - 1)];
/* 188:330 */       if (sumi[(ni - 1)] > 0.0D) {
/* 189:331 */         for (j = 0; j < nj - 1; j++) {
/* 190:332 */           if (counts[(ni - 1)][j] > 0.0D)
/* 191:    */           {
/* 192:333 */             for (i = 0; i < ni - 1; i++)
/* 193:    */             {
/* 194:334 */               temp = i_copy[i] / (sum - i_copy[(ni - 1)]) * counts[(ni - 1)][j];
/* 195:335 */               counts[i][j] += temp;
/* 196:336 */               sumi[i] += temp;
/* 197:    */             }
/* 198:339 */             counts[(ni - 1)][j] = 0.0D;
/* 199:    */           }
/* 200:    */         }
/* 201:    */       }
/* 202:344 */       sumi[(ni - 1)] = 0.0D;
/* 203:347 */       if (sumj[(nj - 1)] > 0.0D) {
/* 204:348 */         for (i = 0; i < ni - 1; i++) {
/* 205:349 */           if (counts[i][(nj - 1)] > 0.0D)
/* 206:    */           {
/* 207:350 */             for (j = 0; j < nj - 1; j++)
/* 208:    */             {
/* 209:351 */               temp = j_copy[j] / (sum - j_copy[(nj - 1)]) * counts[i][(nj - 1)];
/* 210:352 */               counts[i][j] += temp;
/* 211:353 */               sumj[j] += temp;
/* 212:    */             }
/* 213:356 */             counts[i][(nj - 1)] = 0.0D;
/* 214:    */           }
/* 215:    */         }
/* 216:    */       }
/* 217:361 */       sumj[(nj - 1)] = 0.0D;
/* 218:364 */       if ((counts[(ni - 1)][(nj - 1)] > 0.0D) && (total_missing < sum))
/* 219:    */       {
/* 220:365 */         for (i = 0; i < ni - 1; i++) {
/* 221:366 */           for (j = 0; j < nj - 1; j++)
/* 222:    */           {
/* 223:367 */             temp = counts_copy[i][j] / (sum - total_missing) * counts_copy[(ni - 1)][(nj - 1)];
/* 224:    */             
/* 225:369 */             counts[i][j] += temp;
/* 226:370 */             sumi[i] += temp;
/* 227:371 */             sumj[j] += temp;
/* 228:    */           }
/* 229:    */         }
/* 230:375 */         counts[(ni - 1)][(nj - 1)] = 0.0D;
/* 231:    */       }
/* 232:    */     }
/* 233:379 */     return ContingencyTables.gainRatio(counts);
/* 234:    */   }
/* 235:    */   
/* 236:    */   public String toString()
/* 237:    */   {
/* 238:389 */     StringBuffer text = new StringBuffer();
/* 239:391 */     if (this.m_trainInstances == null)
/* 240:    */     {
/* 241:392 */       text.append("\tGain Ratio evaluator has not been built");
/* 242:    */     }
/* 243:    */     else
/* 244:    */     {
/* 245:394 */       text.append("\tGain Ratio feature evaluator");
/* 246:396 */       if (!this.m_missing_merge) {
/* 247:397 */         text.append("\n\tMissing values treated as seperate");
/* 248:    */       }
/* 249:    */     }
/* 250:401 */     text.append("\n");
/* 251:402 */     return text.toString();
/* 252:    */   }
/* 253:    */   
/* 254:    */   public String getRevision()
/* 255:    */   {
/* 256:412 */     return RevisionUtils.extract("$Revision: 11215 $");
/* 257:    */   }
/* 258:    */   
/* 259:    */   public int[] postProcess(int[] attributeSet)
/* 260:    */   {
/* 261:419 */     this.m_trainInstances = new Instances(this.m_trainInstances, 0);
/* 262:    */     
/* 263:421 */     return attributeSet;
/* 264:    */   }
/* 265:    */   
/* 266:    */   public static void main(String[] args)
/* 267:    */   {
/* 268:430 */     runEvaluator(new GainRatioAttributeEval(), args);
/* 269:    */   }
/* 270:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.GainRatioAttributeEval
 * JD-Core Version:    0.7.0.1
 */