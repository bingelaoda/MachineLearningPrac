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
/*  17:    */ import weka.filters.unsupervised.attribute.NumericToBinary;
/*  18:    */ 
/*  19:    */ public class ChiSquaredAttributeEval
/*  20:    */   extends ASEvaluation
/*  21:    */   implements AttributeEvaluator, OptionHandler
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = -8316857822521717692L;
/*  24:    */   private boolean m_missing_merge;
/*  25:    */   private boolean m_Binarize;
/*  26:    */   private double[] m_ChiSquareds;
/*  27:    */   
/*  28:    */   public String globalInfo()
/*  29:    */   {
/*  30: 91 */     return "ChiSquaredAttributeEval :\n\nEvaluates the worth of an attribute by computing the value of the chi-squared statistic with respect to the class.\n";
/*  31:    */   }
/*  32:    */   
/*  33:    */   public ChiSquaredAttributeEval()
/*  34:    */   {
/*  35: 99 */     resetOptions();
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Enumeration<Option> listOptions()
/*  39:    */   {
/*  40:109 */     Vector<Option> newVector = new Vector(2);
/*  41:110 */     newVector.addElement(new Option("\ttreat missing values as a seperate value.", "M", 0, "-M"));
/*  42:    */     
/*  43:112 */     newVector.addElement(new Option("\tjust binarize numeric attributes instead \n\tof properly discretizing them.", "B", 0, "-B"));
/*  44:    */     
/*  45:    */ 
/*  46:115 */     return newVector.elements();
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setOptions(String[] options)
/*  50:    */     throws Exception
/*  51:    */   {
/*  52:144 */     resetOptions();
/*  53:145 */     setMissingMerge(!Utils.getFlag('M', options));
/*  54:146 */     setBinarizeNumericAttributes(Utils.getFlag('B', options));
/*  55:    */     
/*  56:148 */     Utils.checkForRemainingOptions(options);
/*  57:    */   }
/*  58:    */   
/*  59:    */   public String[] getOptions()
/*  60:    */   {
/*  61:159 */     Vector<String> options = new Vector();
/*  62:161 */     if (!getMissingMerge()) {
/*  63:162 */       options.add("-M");
/*  64:    */     }
/*  65:164 */     if (getBinarizeNumericAttributes()) {
/*  66:165 */       options.add("-B");
/*  67:    */     }
/*  68:168 */     return (String[])options.toArray(new String[0]);
/*  69:    */   }
/*  70:    */   
/*  71:    */   public String binarizeNumericAttributesTipText()
/*  72:    */   {
/*  73:178 */     return "Just binarize numeric attributes instead of properly discretizing them.";
/*  74:    */   }
/*  75:    */   
/*  76:    */   public void setBinarizeNumericAttributes(boolean b)
/*  77:    */   {
/*  78:187 */     this.m_Binarize = b;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public boolean getBinarizeNumericAttributes()
/*  82:    */   {
/*  83:196 */     return this.m_Binarize;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public String missingMergeTipText()
/*  87:    */   {
/*  88:206 */     return "Distribute counts for missing values. Counts are distributed across other values in proportion to their frequency. Otherwise, missing is treated as a separate value.";
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void setMissingMerge(boolean b)
/*  92:    */   {
/*  93:217 */     this.m_missing_merge = b;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public boolean getMissingMerge()
/*  97:    */   {
/*  98:226 */     return this.m_missing_merge;
/*  99:    */   }
/* 100:    */   
/* 101:    */   public Capabilities getCapabilities()
/* 102:    */   {
/* 103:237 */     Capabilities result = super.getCapabilities();
/* 104:238 */     result.disableAll();
/* 105:    */     
/* 106:    */ 
/* 107:241 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 108:242 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 109:243 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 110:244 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 111:    */     
/* 112:    */ 
/* 113:247 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 114:248 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 115:    */     
/* 116:250 */     return result;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void buildEvaluator(Instances data)
/* 120:    */     throws Exception
/* 121:    */   {
/* 122:264 */     getCapabilities().testWithFail(data);
/* 123:    */     
/* 124:266 */     int classIndex = data.classIndex();
/* 125:267 */     int numInstances = data.numInstances();
/* 126:269 */     if (!this.m_Binarize)
/* 127:    */     {
/* 128:270 */       Discretize disTransform = new Discretize();
/* 129:271 */       disTransform.setUseBetterEncoding(true);
/* 130:272 */       disTransform.setInputFormat(data);
/* 131:273 */       data = Filter.useFilter(data, disTransform);
/* 132:    */     }
/* 133:    */     else
/* 134:    */     {
/* 135:275 */       NumericToBinary binTransform = new NumericToBinary();
/* 136:276 */       binTransform.setInputFormat(data);
/* 137:277 */       data = Filter.useFilter(data, binTransform);
/* 138:    */     }
/* 139:279 */     int numClasses = data.attribute(classIndex).numValues();
/* 140:    */     
/* 141:    */ 
/* 142:282 */     double[][][] counts = new double[data.numAttributes()][][];
/* 143:283 */     for (int k = 0; k < data.numAttributes(); k++) {
/* 144:284 */       if (k != classIndex)
/* 145:    */       {
/* 146:285 */         int numValues = data.attribute(k).numValues();
/* 147:286 */         counts[k] = new double[numValues + 1][numClasses + 1];
/* 148:    */       }
/* 149:    */     }
/* 150:291 */     double[] temp = new double[numClasses + 1];
/* 151:292 */     for (int k = 0; k < numInstances; k++)
/* 152:    */     {
/* 153:293 */       Instance inst = data.instance(k);
/* 154:294 */       if (inst.classIsMissing()) {
/* 155:295 */         temp[numClasses] += inst.weight();
/* 156:    */       } else {
/* 157:297 */         temp[((int)inst.classValue())] += inst.weight();
/* 158:    */       }
/* 159:    */     }
/* 160:300 */     for (int k = 0; k < counts.length; k++) {
/* 161:301 */       if (k != classIndex) {
/* 162:302 */         for (int i = 0; i < temp.length; i++) {
/* 163:303 */           counts[k][0][i] = temp[i];
/* 164:    */         }
/* 165:    */       }
/* 166:    */     }
/* 167:309 */     for (int k = 0; k < numInstances; k++)
/* 168:    */     {
/* 169:310 */       Instance inst = data.instance(k);
/* 170:311 */       for (int i = 0; i < inst.numValues(); i++) {
/* 171:312 */         if (inst.index(i) != classIndex) {
/* 172:313 */           if ((inst.isMissingSparse(i)) || (inst.classIsMissing()))
/* 173:    */           {
/* 174:314 */             if (!inst.isMissingSparse(i))
/* 175:    */             {
/* 176:315 */               counts[inst.index(i)][((int)inst.valueSparse(i))][numClasses] += inst.weight();
/* 177:    */               
/* 178:317 */               counts[inst.index(i)][0][numClasses] -= inst.weight();
/* 179:    */             }
/* 180:318 */             else if (!inst.classIsMissing())
/* 181:    */             {
/* 182:319 */               counts[inst.index(i)][data.attribute(inst.index(i)).numValues()][((int)inst.classValue())] += inst.weight();
/* 183:    */               
/* 184:321 */               counts[inst.index(i)][0][((int)inst.classValue())] -= inst.weight();
/* 185:    */             }
/* 186:    */             else
/* 187:    */             {
/* 188:324 */               counts[inst.index(i)][data.attribute(inst.index(i)).numValues()][numClasses] += inst.weight();
/* 189:    */               
/* 190:326 */               counts[inst.index(i)][0][numClasses] -= inst.weight();
/* 191:    */             }
/* 192:    */           }
/* 193:    */           else
/* 194:    */           {
/* 195:329 */             counts[inst.index(i)][((int)inst.valueSparse(i))][((int)inst.classValue())] += inst.weight();
/* 196:    */             
/* 197:331 */             counts[inst.index(i)][0][((int)inst.classValue())] -= inst.weight();
/* 198:    */           }
/* 199:    */         }
/* 200:    */       }
/* 201:    */     }
/* 202:338 */     if (this.m_missing_merge) {
/* 203:340 */       for (int k = 0; k < data.numAttributes(); k++) {
/* 204:341 */         if (k != classIndex)
/* 205:    */         {
/* 206:342 */           int numValues = data.attribute(k).numValues();
/* 207:    */           
/* 208:    */ 
/* 209:345 */           double[] rowSums = new double[numValues];
/* 210:346 */           double[] columnSums = new double[numClasses];
/* 211:347 */           double sum = 0.0D;
/* 212:348 */           for (int i = 0; i < numValues; i++)
/* 213:    */           {
/* 214:349 */             for (int j = 0; j < numClasses; j++)
/* 215:    */             {
/* 216:350 */               rowSums[i] += counts[k][i][j];
/* 217:351 */               columnSums[j] += counts[k][i][j];
/* 218:    */             }
/* 219:353 */             sum += rowSums[i];
/* 220:    */           }
/* 221:356 */           if (Utils.gr(sum, 0.0D))
/* 222:    */           {
/* 223:357 */             double[][] additions = new double[numValues][numClasses];
/* 224:360 */             for (int i = 0; i < numValues; i++) {
/* 225:361 */               for (int j = 0; j < numClasses; j++) {
/* 226:362 */                 additions[i][j] = (rowSums[i] / sum * counts[k][numValues][j]);
/* 227:    */               }
/* 228:    */             }
/* 229:367 */             for (int i = 0; i < numClasses; i++) {
/* 230:368 */               for (int j = 0; j < numValues; j++) {
/* 231:369 */                 additions[j][i] += columnSums[i] / sum * counts[k][j][numClasses];
/* 232:    */               }
/* 233:    */             }
/* 234:375 */             for (int i = 0; i < numClasses; i++) {
/* 235:376 */               for (int j = 0; j < numValues; j++) {
/* 236:377 */                 additions[j][i] += counts[k][j][i] / sum * counts[k][numValues][numClasses];
/* 237:    */               }
/* 238:    */             }
/* 239:383 */             double[][] newTable = new double[numValues][numClasses];
/* 240:384 */             for (int i = 0; i < numValues; i++) {
/* 241:385 */               for (int j = 0; j < numClasses; j++) {
/* 242:386 */                 newTable[i][j] = (counts[k][i][j] + additions[i][j]);
/* 243:    */               }
/* 244:    */             }
/* 245:389 */             counts[k] = newTable;
/* 246:    */           }
/* 247:    */         }
/* 248:    */       }
/* 249:    */     }
/* 250:396 */     this.m_ChiSquareds = new double[data.numAttributes()];
/* 251:397 */     for (int i = 0; i < data.numAttributes(); i++) {
/* 252:398 */       if (i != classIndex) {
/* 253:399 */         this.m_ChiSquareds[i] = ContingencyTables.chiVal(ContingencyTables.reduceMatrix(counts[i]), false);
/* 254:    */       }
/* 255:    */     }
/* 256:    */   }
/* 257:    */   
/* 258:    */   protected void resetOptions()
/* 259:    */   {
/* 260:409 */     this.m_ChiSquareds = null;
/* 261:410 */     this.m_missing_merge = true;
/* 262:411 */     this.m_Binarize = false;
/* 263:    */   }
/* 264:    */   
/* 265:    */   public double evaluateAttribute(int attribute)
/* 266:    */     throws Exception
/* 267:    */   {
/* 268:424 */     return this.m_ChiSquareds[attribute];
/* 269:    */   }
/* 270:    */   
/* 271:    */   public String toString()
/* 272:    */   {
/* 273:434 */     StringBuffer text = new StringBuffer();
/* 274:436 */     if (this.m_ChiSquareds == null)
/* 275:    */     {
/* 276:437 */       text.append("Chi-squared attribute evaluator has not been built");
/* 277:    */     }
/* 278:    */     else
/* 279:    */     {
/* 280:439 */       text.append("\tChi-squared Ranking Filter");
/* 281:440 */       if (!this.m_missing_merge) {
/* 282:441 */         text.append("\n\tMissing values treated as seperate");
/* 283:    */       }
/* 284:443 */       if (this.m_Binarize) {
/* 285:444 */         text.append("\n\tNumeric attributes are just binarized");
/* 286:    */       }
/* 287:    */     }
/* 288:448 */     text.append("\n");
/* 289:449 */     return text.toString();
/* 290:    */   }
/* 291:    */   
/* 292:    */   public String getRevision()
/* 293:    */   {
/* 294:459 */     return RevisionUtils.extract("$Revision: 10330 $");
/* 295:    */   }
/* 296:    */   
/* 297:    */   public static void main(String[] args)
/* 298:    */   {
/* 299:468 */     runEvaluator(new ChiSquaredAttributeEval(), args);
/* 300:    */   }
/* 301:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.ChiSquaredAttributeEval
 * JD-Core Version:    0.7.0.1
 */