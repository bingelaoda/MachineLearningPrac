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
/*  18:    */ public class SymmetricalUncertAttributeEval
/*  19:    */   extends ASEvaluation
/*  20:    */   implements AttributeEvaluator, OptionHandler
/*  21:    */ {
/*  22:    */   static final long serialVersionUID = -8096505776132296416L;
/*  23:    */   private Instances m_trainInstances;
/*  24:    */   private int m_classIndex;
/*  25:    */   private int m_numInstances;
/*  26:    */   private int m_numClasses;
/*  27:    */   private boolean m_missing_merge;
/*  28:    */   
/*  29:    */   public String globalInfo()
/*  30:    */   {
/*  31: 92 */     return "SymmetricalUncertAttributeEval :\n\nEvaluates the worth of an attribute by measuring the symmetrical uncertainty with respect to the class. \n\n SymmU(Class, Attribute) = 2 * (H(Class) - H(Class | Attribute)) / H(Class) + H(Attribute).\n";
/*  32:    */   }
/*  33:    */   
/*  34:    */   public SymmetricalUncertAttributeEval()
/*  35:    */   {
/*  36:102 */     resetOptions();
/*  37:    */   }
/*  38:    */   
/*  39:    */   public Enumeration<Option> listOptions()
/*  40:    */   {
/*  41:112 */     Vector<Option> newVector = new Vector(1);
/*  42:113 */     newVector.addElement(new Option("\ttreat missing values as a seperate value.", "M", 0, "-M"));
/*  43:    */     
/*  44:115 */     return newVector.elements();
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void setOptions(String[] options)
/*  48:    */     throws Exception
/*  49:    */   {
/*  50:137 */     resetOptions();
/*  51:138 */     setMissingMerge(!Utils.getFlag('M', options));
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String missingMergeTipText()
/*  55:    */   {
/*  56:148 */     return "Distribute counts for missing values. Counts are distributed across other values in proportion to their frequency. Otherwise, missing is treated as a separate value.";
/*  57:    */   }
/*  58:    */   
/*  59:    */   public void setMissingMerge(boolean b)
/*  60:    */   {
/*  61:159 */     this.m_missing_merge = b;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public boolean getMissingMerge()
/*  65:    */   {
/*  66:168 */     return this.m_missing_merge;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public String[] getOptions()
/*  70:    */   {
/*  71:178 */     String[] options = new String[1];
/*  72:179 */     int current = 0;
/*  73:181 */     if (!getMissingMerge()) {
/*  74:182 */       options[(current++)] = "-M";
/*  75:    */     }
/*  76:185 */     while (current < options.length) {
/*  77:186 */       options[(current++)] = "";
/*  78:    */     }
/*  79:189 */     return options;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public Capabilities getCapabilities()
/*  83:    */   {
/*  84:200 */     Capabilities result = super.getCapabilities();
/*  85:201 */     result.disableAll();
/*  86:    */     
/*  87:    */ 
/*  88:204 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  89:205 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  90:206 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  91:207 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  92:    */     
/*  93:    */ 
/*  94:210 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  95:211 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  96:    */     
/*  97:213 */     return result;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public void buildEvaluator(Instances data)
/* 101:    */     throws Exception
/* 102:    */   {
/* 103:227 */     getCapabilities().testWithFail(data);
/* 104:    */     
/* 105:229 */     this.m_trainInstances = data;
/* 106:230 */     this.m_classIndex = this.m_trainInstances.classIndex();
/* 107:231 */     this.m_numInstances = this.m_trainInstances.numInstances();
/* 108:232 */     Discretize disTransform = new Discretize();
/* 109:233 */     disTransform.setUseBetterEncoding(true);
/* 110:234 */     disTransform.setInputFormat(this.m_trainInstances);
/* 111:235 */     this.m_trainInstances = Filter.useFilter(this.m_trainInstances, disTransform);
/* 112:236 */     this.m_numClasses = this.m_trainInstances.attribute(this.m_classIndex).numValues();
/* 113:    */   }
/* 114:    */   
/* 115:    */   protected void resetOptions()
/* 116:    */   {
/* 117:243 */     this.m_trainInstances = null;
/* 118:244 */     this.m_missing_merge = true;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public double evaluateAttribute(int attribute)
/* 122:    */     throws Exception
/* 123:    */   {
/* 124:259 */     double sum = 0.0D;
/* 125:260 */     int ni = this.m_trainInstances.attribute(attribute).numValues() + 1;
/* 126:261 */     int nj = this.m_numClasses + 1;
/* 127:    */     
/* 128:    */ 
/* 129:264 */     double temp = 0.0D;
/* 130:265 */     double[] sumi = new double[ni];
/* 131:266 */     double[] sumj = new double[nj];
/* 132:267 */     double[][] counts = new double[ni][nj];
/* 133:268 */     sumi = new double[ni];
/* 134:269 */     sumj = new double[nj];
/* 135:271 */     for (int i = 0; i < ni; i++)
/* 136:    */     {
/* 137:272 */       sumi[i] = 0.0D;
/* 138:274 */       for (int j = 0; j < nj; j++)
/* 139:    */       {
/* 140:275 */         sumj[j] = 0.0D;
/* 141:276 */         counts[i][j] = 0.0D;
/* 142:    */       }
/* 143:    */     }
/* 144:281 */     for (i = 0; i < this.m_numInstances; i++)
/* 145:    */     {
/* 146:282 */       Instance inst = this.m_trainInstances.instance(i);
/* 147:    */       int ii;
/* 148:    */       int ii;
/* 149:284 */       if (inst.isMissing(attribute)) {
/* 150:285 */         ii = ni - 1;
/* 151:    */       } else {
/* 152:287 */         ii = (int)inst.value(attribute);
/* 153:    */       }
/* 154:    */       int jj;
/* 155:    */       int jj;
/* 156:290 */       if (inst.isMissing(this.m_classIndex)) {
/* 157:291 */         jj = nj - 1;
/* 158:    */       } else {
/* 159:293 */         jj = (int)inst.value(this.m_classIndex);
/* 160:    */       }
/* 161:296 */       counts[ii][jj] += 1.0D;
/* 162:    */     }
/* 163:300 */     for (i = 0; i < ni; i++)
/* 164:    */     {
/* 165:301 */       sumi[i] = 0.0D;
/* 166:303 */       for (int j = 0; j < nj; j++)
/* 167:    */       {
/* 168:304 */         sumi[i] += counts[i][j];
/* 169:305 */         sum += counts[i][j];
/* 170:    */       }
/* 171:    */     }
/* 172:310 */     for (int j = 0; j < nj; j++)
/* 173:    */     {
/* 174:311 */       sumj[j] = 0.0D;
/* 175:313 */       for (i = 0; i < ni; i++) {
/* 176:314 */         sumj[j] += counts[i][j];
/* 177:    */       }
/* 178:    */     }
/* 179:319 */     if ((this.m_missing_merge) && (sumi[(ni - 1)] < this.m_numInstances) && (sumj[(nj - 1)] < this.m_numInstances))
/* 180:    */     {
/* 181:321 */       double[] i_copy = new double[sumi.length];
/* 182:322 */       double[] j_copy = new double[sumj.length];
/* 183:323 */       double[][] counts_copy = new double[sumi.length][sumj.length];
/* 184:325 */       for (i = 0; i < ni; i++) {
/* 185:326 */         System.arraycopy(counts[i], 0, counts_copy[i], 0, sumj.length);
/* 186:    */       }
/* 187:329 */       System.arraycopy(sumi, 0, i_copy, 0, sumi.length);
/* 188:330 */       System.arraycopy(sumj, 0, j_copy, 0, sumj.length);
/* 189:331 */       double total_missing = sumi[(ni - 1)] + sumj[(nj - 1)] - counts[(ni - 1)][(nj - 1)];
/* 190:334 */       if (sumi[(ni - 1)] > 0.0D) {
/* 191:335 */         for (j = 0; j < nj - 1; j++) {
/* 192:336 */           if (counts[(ni - 1)][j] > 0.0D)
/* 193:    */           {
/* 194:337 */             for (i = 0; i < ni - 1; i++)
/* 195:    */             {
/* 196:338 */               temp = i_copy[i] / (sum - i_copy[(ni - 1)]) * counts[(ni - 1)][j];
/* 197:339 */               counts[i][j] += temp;
/* 198:340 */               sumi[i] += temp;
/* 199:    */             }
/* 200:343 */             counts[(ni - 1)][j] = 0.0D;
/* 201:    */           }
/* 202:    */         }
/* 203:    */       }
/* 204:348 */       sumi[(ni - 1)] = 0.0D;
/* 205:351 */       if (sumj[(nj - 1)] > 0.0D) {
/* 206:352 */         for (i = 0; i < ni - 1; i++) {
/* 207:353 */           if (counts[i][(nj - 1)] > 0.0D)
/* 208:    */           {
/* 209:354 */             for (j = 0; j < nj - 1; j++)
/* 210:    */             {
/* 211:355 */               temp = j_copy[j] / (sum - j_copy[(nj - 1)]) * counts[i][(nj - 1)];
/* 212:356 */               counts[i][j] += temp;
/* 213:357 */               sumj[j] += temp;
/* 214:    */             }
/* 215:360 */             counts[i][(nj - 1)] = 0.0D;
/* 216:    */           }
/* 217:    */         }
/* 218:    */       }
/* 219:365 */       sumj[(nj - 1)] = 0.0D;
/* 220:368 */       if ((counts[(ni - 1)][(nj - 1)] > 0.0D) && (total_missing != sum))
/* 221:    */       {
/* 222:369 */         for (i = 0; i < ni - 1; i++) {
/* 223:370 */           for (j = 0; j < nj - 1; j++)
/* 224:    */           {
/* 225:371 */             temp = counts_copy[i][j] / (sum - total_missing) * counts_copy[(ni - 1)][(nj - 1)];
/* 226:    */             
/* 227:373 */             counts[i][j] += temp;
/* 228:374 */             sumi[i] += temp;
/* 229:375 */             sumj[j] += temp;
/* 230:    */           }
/* 231:    */         }
/* 232:379 */         counts[(ni - 1)][(nj - 1)] = 0.0D;
/* 233:    */       }
/* 234:    */     }
/* 235:383 */     return ContingencyTables.symmetricalUncertainty(counts);
/* 236:    */   }
/* 237:    */   
/* 238:    */   public String toString()
/* 239:    */   {
/* 240:393 */     StringBuffer text = new StringBuffer();
/* 241:395 */     if (this.m_trainInstances == null)
/* 242:    */     {
/* 243:396 */       text.append("\tSymmetrical Uncertainty evaluator has not been built");
/* 244:    */     }
/* 245:    */     else
/* 246:    */     {
/* 247:398 */       text.append("\tSymmetrical Uncertainty Ranking Filter");
/* 248:399 */       if (!this.m_missing_merge) {
/* 249:400 */         text.append("\n\tMissing values treated as seperate");
/* 250:    */       }
/* 251:    */     }
/* 252:404 */     text.append("\n");
/* 253:405 */     return text.toString();
/* 254:    */   }
/* 255:    */   
/* 256:    */   public String getRevision()
/* 257:    */   {
/* 258:415 */     return RevisionUtils.extract("$Revision: 10172 $");
/* 259:    */   }
/* 260:    */   
/* 261:    */   public static void main(String[] argv)
/* 262:    */   {
/* 263:427 */     runEvaluator(new SymmetricalUncertAttributeEval(), argv);
/* 264:    */   }
/* 265:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.SymmetricalUncertAttributeEval
 * JD-Core Version:    0.7.0.1
 */