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
/*  19:    */ public class InfoGainAttributeEval
/*  20:    */   extends ASEvaluation
/*  21:    */   implements AttributeEvaluator, OptionHandler
/*  22:    */ {
/*  23:    */   static final long serialVersionUID = -1949849512589218930L;
/*  24:    */   private boolean m_missing_merge;
/*  25:    */   private boolean m_Binarize;
/*  26:    */   private double[] m_InfoGains;
/*  27:    */   
/*  28:    */   public String globalInfo()
/*  29:    */   {
/*  30: 93 */     return "InfoGainAttributeEval :\n\nEvaluates the worth of an attribute by measuring the information gain with respect to the class.\n\nInfoGain(Class,Attribute) = H(Class) - H(Class | Attribute).\n";
/*  31:    */   }
/*  32:    */   
/*  33:    */   public InfoGainAttributeEval()
/*  34:    */   {
/*  35:102 */     resetOptions();
/*  36:    */   }
/*  37:    */   
/*  38:    */   public Enumeration<Option> listOptions()
/*  39:    */   {
/*  40:112 */     Vector<Option> newVector = new Vector(2);
/*  41:113 */     newVector.addElement(new Option("\ttreat missing values as a seperate value.", "M", 0, "-M"));
/*  42:    */     
/*  43:115 */     newVector.addElement(new Option("\tjust binarize numeric attributes instead \n\tof properly discretizing them.", "B", 0, "-B"));
/*  44:    */     
/*  45:    */ 
/*  46:118 */     return newVector.elements();
/*  47:    */   }
/*  48:    */   
/*  49:    */   public void setOptions(String[] options)
/*  50:    */     throws Exception
/*  51:    */   {
/*  52:147 */     resetOptions();
/*  53:148 */     setMissingMerge(!Utils.getFlag('M', options));
/*  54:149 */     setBinarizeNumericAttributes(Utils.getFlag('B', options));
/*  55:    */   }
/*  56:    */   
/*  57:    */   public String[] getOptions()
/*  58:    */   {
/*  59:159 */     String[] options = new String[2];
/*  60:160 */     int current = 0;
/*  61:162 */     if (!getMissingMerge()) {
/*  62:163 */       options[(current++)] = "-M";
/*  63:    */     }
/*  64:165 */     if (getBinarizeNumericAttributes()) {
/*  65:166 */       options[(current++)] = "-B";
/*  66:    */     }
/*  67:169 */     while (current < options.length) {
/*  68:170 */       options[(current++)] = "";
/*  69:    */     }
/*  70:173 */     return options;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public String binarizeNumericAttributesTipText()
/*  74:    */   {
/*  75:183 */     return "Just binarize numeric attributes instead of properly discretizing them.";
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void setBinarizeNumericAttributes(boolean b)
/*  79:    */   {
/*  80:192 */     this.m_Binarize = b;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public boolean getBinarizeNumericAttributes()
/*  84:    */   {
/*  85:201 */     return this.m_Binarize;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public String missingMergeTipText()
/*  89:    */   {
/*  90:211 */     return "Distribute counts for missing values. Counts are distributed across other values in proportion to their frequency. Otherwise, missing is treated as a separate value.";
/*  91:    */   }
/*  92:    */   
/*  93:    */   public void setMissingMerge(boolean b)
/*  94:    */   {
/*  95:222 */     this.m_missing_merge = b;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public boolean getMissingMerge()
/*  99:    */   {
/* 100:231 */     return this.m_missing_merge;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public Capabilities getCapabilities()
/* 104:    */   {
/* 105:242 */     Capabilities result = super.getCapabilities();
/* 106:243 */     result.disableAll();
/* 107:    */     
/* 108:    */ 
/* 109:246 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/* 110:247 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 111:248 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/* 112:249 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/* 113:    */     
/* 114:    */ 
/* 115:252 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 116:253 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 117:    */     
/* 118:255 */     return result;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public void buildEvaluator(Instances data)
/* 122:    */     throws Exception
/* 123:    */   {
/* 124:269 */     getCapabilities().testWithFail(data);
/* 125:    */     
/* 126:271 */     int classIndex = data.classIndex();
/* 127:272 */     int numInstances = data.numInstances();
/* 128:274 */     if (!this.m_Binarize)
/* 129:    */     {
/* 130:275 */       Discretize disTransform = new Discretize();
/* 131:276 */       disTransform.setUseBetterEncoding(true);
/* 132:277 */       disTransform.setInputFormat(data);
/* 133:278 */       data = Filter.useFilter(data, disTransform);
/* 134:    */     }
/* 135:    */     else
/* 136:    */     {
/* 137:280 */       NumericToBinary binTransform = new NumericToBinary();
/* 138:281 */       binTransform.setInputFormat(data);
/* 139:282 */       data = Filter.useFilter(data, binTransform);
/* 140:    */     }
/* 141:284 */     int numClasses = data.attribute(classIndex).numValues();
/* 142:    */     
/* 143:    */ 
/* 144:287 */     double[][][] counts = new double[data.numAttributes()][][];
/* 145:288 */     for (int k = 0; k < data.numAttributes(); k++) {
/* 146:289 */       if (k != classIndex)
/* 147:    */       {
/* 148:290 */         int numValues = data.attribute(k).numValues();
/* 149:291 */         counts[k] = new double[numValues + 1][numClasses + 1];
/* 150:    */       }
/* 151:    */     }
/* 152:296 */     double[] temp = new double[numClasses + 1];
/* 153:297 */     for (int k = 0; k < numInstances; k++)
/* 154:    */     {
/* 155:298 */       Instance inst = data.instance(k);
/* 156:299 */       if (inst.classIsMissing()) {
/* 157:300 */         temp[numClasses] += inst.weight();
/* 158:    */       } else {
/* 159:302 */         temp[((int)inst.classValue())] += inst.weight();
/* 160:    */       }
/* 161:    */     }
/* 162:305 */     for (int k = 0; k < counts.length; k++) {
/* 163:306 */       if (k != classIndex) {
/* 164:307 */         for (int i = 0; i < temp.length; i++) {
/* 165:308 */           counts[k][0][i] = temp[i];
/* 166:    */         }
/* 167:    */       }
/* 168:    */     }
/* 169:314 */     for (int k = 0; k < numInstances; k++)
/* 170:    */     {
/* 171:315 */       Instance inst = data.instance(k);
/* 172:316 */       for (int i = 0; i < inst.numValues(); i++) {
/* 173:317 */         if (inst.index(i) != classIndex) {
/* 174:318 */           if ((inst.isMissingSparse(i)) || (inst.classIsMissing()))
/* 175:    */           {
/* 176:319 */             if (!inst.isMissingSparse(i))
/* 177:    */             {
/* 178:320 */               counts[inst.index(i)][((int)inst.valueSparse(i))][numClasses] += inst.weight();
/* 179:    */               
/* 180:322 */               counts[inst.index(i)][0][numClasses] -= inst.weight();
/* 181:    */             }
/* 182:323 */             else if (!inst.classIsMissing())
/* 183:    */             {
/* 184:324 */               counts[inst.index(i)][data.attribute(inst.index(i)).numValues()][((int)inst.classValue())] += inst.weight();
/* 185:    */               
/* 186:326 */               counts[inst.index(i)][0][((int)inst.classValue())] -= inst.weight();
/* 187:    */             }
/* 188:    */             else
/* 189:    */             {
/* 190:329 */               counts[inst.index(i)][data.attribute(inst.index(i)).numValues()][numClasses] += inst.weight();
/* 191:    */               
/* 192:331 */               counts[inst.index(i)][0][numClasses] -= inst.weight();
/* 193:    */             }
/* 194:    */           }
/* 195:    */           else
/* 196:    */           {
/* 197:334 */             counts[inst.index(i)][((int)inst.valueSparse(i))][((int)inst.classValue())] += inst.weight();
/* 198:    */             
/* 199:336 */             counts[inst.index(i)][0][((int)inst.classValue())] -= inst.weight();
/* 200:    */           }
/* 201:    */         }
/* 202:    */       }
/* 203:    */     }
/* 204:343 */     if (this.m_missing_merge) {
/* 205:345 */       for (int k = 0; k < data.numAttributes(); k++) {
/* 206:346 */         if (k != classIndex)
/* 207:    */         {
/* 208:347 */           int numValues = data.attribute(k).numValues();
/* 209:    */           
/* 210:    */ 
/* 211:350 */           double[] rowSums = new double[numValues];
/* 212:351 */           double[] columnSums = new double[numClasses];
/* 213:352 */           double sum = 0.0D;
/* 214:353 */           for (int i = 0; i < numValues; i++)
/* 215:    */           {
/* 216:354 */             for (int j = 0; j < numClasses; j++)
/* 217:    */             {
/* 218:355 */               rowSums[i] += counts[k][i][j];
/* 219:356 */               columnSums[j] += counts[k][i][j];
/* 220:    */             }
/* 221:358 */             sum += rowSums[i];
/* 222:    */           }
/* 223:361 */           if (Utils.gr(sum, 0.0D))
/* 224:    */           {
/* 225:362 */             double[][] additions = new double[numValues][numClasses];
/* 226:365 */             for (int i = 0; i < numValues; i++) {
/* 227:366 */               for (int j = 0; j < numClasses; j++) {
/* 228:367 */                 additions[i][j] = (rowSums[i] / sum * counts[k][numValues][j]);
/* 229:    */               }
/* 230:    */             }
/* 231:372 */             for (int i = 0; i < numClasses; i++) {
/* 232:373 */               for (int j = 0; j < numValues; j++) {
/* 233:374 */                 additions[j][i] += columnSums[i] / sum * counts[k][j][numClasses];
/* 234:    */               }
/* 235:    */             }
/* 236:380 */             for (int i = 0; i < numClasses; i++) {
/* 237:381 */               for (int j = 0; j < numValues; j++) {
/* 238:382 */                 additions[j][i] += counts[k][j][i] / sum * counts[k][numValues][numClasses];
/* 239:    */               }
/* 240:    */             }
/* 241:388 */             double[][] newTable = new double[numValues][numClasses];
/* 242:389 */             for (int i = 0; i < numValues; i++) {
/* 243:390 */               for (int j = 0; j < numClasses; j++) {
/* 244:391 */                 newTable[i][j] = (counts[k][i][j] + additions[i][j]);
/* 245:    */               }
/* 246:    */             }
/* 247:394 */             counts[k] = newTable;
/* 248:    */           }
/* 249:    */         }
/* 250:    */       }
/* 251:    */     }
/* 252:401 */     this.m_InfoGains = new double[data.numAttributes()];
/* 253:402 */     for (int i = 0; i < data.numAttributes(); i++) {
/* 254:403 */       if (i != classIndex) {
/* 255:404 */         this.m_InfoGains[i] = (ContingencyTables.entropyOverColumns(counts[i]) - ContingencyTables.entropyConditionedOnRows(counts[i]));
/* 256:    */       }
/* 257:    */     }
/* 258:    */   }
/* 259:    */   
/* 260:    */   protected void resetOptions()
/* 261:    */   {
/* 262:414 */     this.m_InfoGains = null;
/* 263:415 */     this.m_missing_merge = true;
/* 264:416 */     this.m_Binarize = false;
/* 265:    */   }
/* 266:    */   
/* 267:    */   public double evaluateAttribute(int attribute)
/* 268:    */     throws Exception
/* 269:    */   {
/* 270:430 */     return this.m_InfoGains[attribute];
/* 271:    */   }
/* 272:    */   
/* 273:    */   public String toString()
/* 274:    */   {
/* 275:440 */     StringBuffer text = new StringBuffer();
/* 276:442 */     if (this.m_InfoGains == null)
/* 277:    */     {
/* 278:443 */       text.append("Information Gain attribute evaluator has not been built");
/* 279:    */     }
/* 280:    */     else
/* 281:    */     {
/* 282:445 */       text.append("\tInformation Gain Ranking Filter");
/* 283:446 */       if (!this.m_missing_merge) {
/* 284:447 */         text.append("\n\tMissing values treated as seperate");
/* 285:    */       }
/* 286:449 */       if (this.m_Binarize) {
/* 287:450 */         text.append("\n\tNumeric attributes are just binarized");
/* 288:    */       }
/* 289:    */     }
/* 290:454 */     text.append("\n");
/* 291:455 */     return text.toString();
/* 292:    */   }
/* 293:    */   
/* 294:    */   public String getRevision()
/* 295:    */   {
/* 296:465 */     return RevisionUtils.extract("$Revision: 10172 $");
/* 297:    */   }
/* 298:    */   
/* 299:    */   public static void main(String[] args)
/* 300:    */   {
/* 301:477 */     runEvaluator(new InfoGainAttributeEval(), args);
/* 302:    */   }
/* 303:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.InfoGainAttributeEval
 * JD-Core Version:    0.7.0.1
 */