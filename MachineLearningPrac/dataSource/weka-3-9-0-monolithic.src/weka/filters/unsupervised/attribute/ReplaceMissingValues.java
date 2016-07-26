/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import weka.core.Attribute;
/*   4:    */ import weka.core.Capabilities;
/*   5:    */ import weka.core.Capabilities.Capability;
/*   6:    */ import weka.core.DenseInstance;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.RevisionUtils;
/*  10:    */ import weka.core.SparseInstance;
/*  11:    */ import weka.core.Utils;
/*  12:    */ import weka.filters.Sourcable;
/*  13:    */ import weka.filters.UnsupervisedFilter;
/*  14:    */ 
/*  15:    */ public class ReplaceMissingValues
/*  16:    */   extends PotentialClassIgnorer
/*  17:    */   implements UnsupervisedFilter, Sourcable
/*  18:    */ {
/*  19:    */   static final long serialVersionUID = 8349568310991609867L;
/*  20: 63 */   private double[] m_ModesAndMeans = null;
/*  21:    */   
/*  22:    */   public String globalInfo()
/*  23:    */   {
/*  24: 73 */     return "Replaces all missing values for nominal and numeric attributes in a dataset with the modes and means from the training data.";
/*  25:    */   }
/*  26:    */   
/*  27:    */   public Capabilities getCapabilities()
/*  28:    */   {
/*  29: 84 */     Capabilities result = super.getCapabilities();
/*  30: 85 */     result.disableAll();
/*  31:    */     
/*  32:    */ 
/*  33: 88 */     result.enableAllAttributes();
/*  34: 89 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  35:    */     
/*  36:    */ 
/*  37: 92 */     result.enableAllClasses();
/*  38: 93 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  39: 94 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  40:    */     
/*  41: 96 */     return result;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public boolean setInputFormat(Instances instanceInfo)
/*  45:    */     throws Exception
/*  46:    */   {
/*  47:112 */     super.setInputFormat(instanceInfo);
/*  48:113 */     setOutputFormat(instanceInfo);
/*  49:114 */     this.m_ModesAndMeans = null;
/*  50:115 */     return true;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public boolean input(Instance instance)
/*  54:    */   {
/*  55:129 */     if (getInputFormat() == null) {
/*  56:130 */       throw new IllegalStateException("No input instance format defined");
/*  57:    */     }
/*  58:132 */     if (this.m_NewBatch)
/*  59:    */     {
/*  60:133 */       resetQueue();
/*  61:134 */       this.m_NewBatch = false;
/*  62:    */     }
/*  63:136 */     if (this.m_ModesAndMeans == null)
/*  64:    */     {
/*  65:137 */       bufferInput(instance);
/*  66:138 */       return false;
/*  67:    */     }
/*  68:140 */     convertInstance(instance);
/*  69:141 */     return true;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public boolean batchFinished()
/*  73:    */   {
/*  74:155 */     if (getInputFormat() == null) {
/*  75:156 */       throw new IllegalStateException("No input instance format defined");
/*  76:    */     }
/*  77:159 */     if (this.m_ModesAndMeans == null)
/*  78:    */     {
/*  79:161 */       double sumOfWeights = getInputFormat().sumOfWeights();
/*  80:162 */       double[][] counts = new double[getInputFormat().numAttributes()][];
/*  81:163 */       for (int i = 0; i < getInputFormat().numAttributes(); i++) {
/*  82:164 */         if (getInputFormat().attribute(i).isNominal())
/*  83:    */         {
/*  84:165 */           counts[i] = new double[getInputFormat().attribute(i).numValues()];
/*  85:166 */           if (counts[i].length > 0) {
/*  86:167 */             counts[i][0] = sumOfWeights;
/*  87:    */           }
/*  88:    */         }
/*  89:    */       }
/*  90:170 */       double[] sums = new double[getInputFormat().numAttributes()];
/*  91:171 */       for (int i = 0; i < sums.length; i++) {
/*  92:172 */         sums[i] = sumOfWeights;
/*  93:    */       }
/*  94:174 */       double[] results = new double[getInputFormat().numAttributes()];
/*  95:175 */       for (int j = 0; j < getInputFormat().numInstances(); j++)
/*  96:    */       {
/*  97:176 */         Instance inst = getInputFormat().instance(j);
/*  98:177 */         for (int i = 0; i < inst.numValues(); i++) {
/*  99:178 */           if (!inst.isMissingSparse(i))
/* 100:    */           {
/* 101:179 */             double value = inst.valueSparse(i);
/* 102:180 */             if (inst.attributeSparse(i).isNominal())
/* 103:    */             {
/* 104:181 */               if (counts[inst.index(i)].length > 0)
/* 105:    */               {
/* 106:182 */                 counts[inst.index(i)][((int)value)] += inst.weight();
/* 107:183 */                 counts[inst.index(i)][0] -= inst.weight();
/* 108:    */               }
/* 109:    */             }
/* 110:185 */             else if (inst.attributeSparse(i).isNumeric()) {
/* 111:186 */               results[inst.index(i)] += inst.weight() * inst.valueSparse(i);
/* 112:    */             }
/* 113:    */           }
/* 114:189 */           else if (inst.attributeSparse(i).isNominal())
/* 115:    */           {
/* 116:190 */             if (counts[inst.index(i)].length > 0) {
/* 117:191 */               counts[inst.index(i)][0] -= inst.weight();
/* 118:    */             }
/* 119:    */           }
/* 120:193 */           else if (inst.attributeSparse(i).isNumeric())
/* 121:    */           {
/* 122:194 */             sums[inst.index(i)] -= inst.weight();
/* 123:    */           }
/* 124:    */         }
/* 125:    */       }
/* 126:199 */       this.m_ModesAndMeans = new double[getInputFormat().numAttributes()];
/* 127:200 */       for (int i = 0; i < getInputFormat().numAttributes(); i++) {
/* 128:201 */         if (getInputFormat().attribute(i).isNominal())
/* 129:    */         {
/* 130:202 */           if (counts[i].length == 0) {
/* 131:203 */             this.m_ModesAndMeans[i] = Utils.missingValue();
/* 132:    */           } else {
/* 133:205 */             this.m_ModesAndMeans[i] = Utils.maxIndex(counts[i]);
/* 134:    */           }
/* 135:    */         }
/* 136:206 */         else if ((getInputFormat().attribute(i).isNumeric()) && 
/* 137:207 */           (Utils.gr(sums[i], 0.0D))) {
/* 138:208 */           this.m_ModesAndMeans[i] = (results[i] / sums[i]);
/* 139:    */         }
/* 140:    */       }
/* 141:214 */       for (int i = 0; i < getInputFormat().numInstances(); i++) {
/* 142:215 */         convertInstance(getInputFormat().instance(i));
/* 143:    */       }
/* 144:    */     }
/* 145:219 */     flushInput();
/* 146:    */     
/* 147:221 */     this.m_NewBatch = true;
/* 148:222 */     return numPendingOutput() != 0;
/* 149:    */   }
/* 150:    */   
/* 151:    */   private void convertInstance(Instance instance)
/* 152:    */   {
/* 153:233 */     Instance inst = null;
/* 154:234 */     if ((instance instanceof SparseInstance))
/* 155:    */     {
/* 156:235 */       double[] vals = new double[instance.numValues()];
/* 157:236 */       int[] indices = new int[instance.numValues()];
/* 158:237 */       int num = 0;
/* 159:238 */       for (int j = 0; j < instance.numValues(); j++) {
/* 160:239 */         if ((instance.isMissingSparse(j)) && (getInputFormat().classIndex() != instance.index(j)) && ((instance.attributeSparse(j).isNominal()) || (instance.attributeSparse(j).isNumeric())))
/* 161:    */         {
/* 162:243 */           if (this.m_ModesAndMeans[instance.index(j)] != 0.0D)
/* 163:    */           {
/* 164:244 */             vals[num] = this.m_ModesAndMeans[instance.index(j)];
/* 165:245 */             indices[num] = instance.index(j);
/* 166:246 */             num++;
/* 167:    */           }
/* 168:    */         }
/* 169:    */         else
/* 170:    */         {
/* 171:249 */           vals[num] = instance.valueSparse(j);
/* 172:250 */           indices[num] = instance.index(j);
/* 173:251 */           num++;
/* 174:    */         }
/* 175:    */       }
/* 176:254 */       if (num == instance.numValues())
/* 177:    */       {
/* 178:255 */         inst = new SparseInstance(instance.weight(), vals, indices, instance.numAttributes());
/* 179:    */       }
/* 180:    */       else
/* 181:    */       {
/* 182:258 */         double[] tempVals = new double[num];
/* 183:259 */         int[] tempInd = new int[num];
/* 184:260 */         System.arraycopy(vals, 0, tempVals, 0, num);
/* 185:261 */         System.arraycopy(indices, 0, tempInd, 0, num);
/* 186:262 */         inst = new SparseInstance(instance.weight(), tempVals, tempInd, instance.numAttributes());
/* 187:    */       }
/* 188:    */     }
/* 189:    */     else
/* 190:    */     {
/* 191:266 */       double[] vals = new double[getInputFormat().numAttributes()];
/* 192:267 */       for (int j = 0; j < instance.numAttributes(); j++) {
/* 193:268 */         if ((instance.isMissing(j)) && (getInputFormat().classIndex() != j) && ((getInputFormat().attribute(j).isNominal()) || (getInputFormat().attribute(j).isNumeric()))) {
/* 194:272 */           vals[j] = this.m_ModesAndMeans[j];
/* 195:    */         } else {
/* 196:274 */           vals[j] = instance.value(j);
/* 197:    */         }
/* 198:    */       }
/* 199:277 */       inst = new DenseInstance(instance.weight(), vals);
/* 200:    */     }
/* 201:279 */     inst.setDataset(instance.dataset());
/* 202:280 */     push(inst, false);
/* 203:    */   }
/* 204:    */   
/* 205:    */   public String toSource(String className, Instances data)
/* 206:    */     throws Exception
/* 207:    */   {
/* 208:311 */     StringBuffer result = new StringBuffer();
/* 209:    */     
/* 210:    */ 
/* 211:314 */     boolean[] numeric = new boolean[data.numAttributes()];
/* 212:315 */     boolean[] nominal = new boolean[data.numAttributes()];
/* 213:316 */     String[] modes = new String[data.numAttributes()];
/* 214:317 */     double[] means = new double[data.numAttributes()];
/* 215:318 */     for (int i = 0; i < data.numAttributes(); i++)
/* 216:    */     {
/* 217:319 */       numeric[i] = ((data.attribute(i).isNumeric()) && (i != data.classIndex()) ? 1 : false);
/* 218:320 */       nominal[i] = ((data.attribute(i).isNominal()) && (i != data.classIndex()) ? 1 : false);
/* 219:322 */       if (numeric[i] != 0) {
/* 220:323 */         means[i] = this.m_ModesAndMeans[i];
/* 221:    */       } else {
/* 222:325 */         means[i] = (0.0D / 0.0D);
/* 223:    */       }
/* 224:327 */       if (nominal[i] != 0) {
/* 225:328 */         modes[i] = data.attribute(i).value((int)this.m_ModesAndMeans[i]);
/* 226:    */       } else {
/* 227:330 */         modes[i] = null;
/* 228:    */       }
/* 229:    */     }
/* 230:333 */     result.append("class " + className + " {\n");
/* 231:334 */     result.append("\n");
/* 232:335 */     result.append("  /** lists which numeric attributes will be processed */\n");
/* 233:336 */     result.append("  protected final static boolean[] NUMERIC = new boolean[]{" + Utils.arrayToString(numeric) + "};\n");
/* 234:337 */     result.append("\n");
/* 235:338 */     result.append("  /** lists which nominal attributes will be processed */\n");
/* 236:339 */     result.append("  protected final static boolean[] NOMINAL = new boolean[]{" + Utils.arrayToString(nominal) + "};\n");
/* 237:340 */     result.append("\n");
/* 238:341 */     result.append("  /** the means */\n");
/* 239:342 */     result.append("  protected final static double[] MEANS = new double[]{" + Utils.arrayToString(means).replaceAll("NaN", "Double.NaN") + "};\n");
/* 240:343 */     result.append("\n");
/* 241:344 */     result.append("  /** the modes */\n");
/* 242:345 */     result.append("  protected final static String[] MODES = new String[]{");
/* 243:346 */     for (i = 0; i < modes.length; i++)
/* 244:    */     {
/* 245:347 */       if (i > 0) {
/* 246:348 */         result.append(",");
/* 247:    */       }
/* 248:349 */       if (nominal[i] != 0) {
/* 249:350 */         result.append("\"" + Utils.quote(modes[i]) + "\"");
/* 250:    */       } else {
/* 251:352 */         result.append(modes[i]);
/* 252:    */       }
/* 253:    */     }
/* 254:354 */     result.append("};\n");
/* 255:355 */     result.append("\n");
/* 256:356 */     result.append("  /**\n");
/* 257:357 */     result.append("   * filters a single row\n");
/* 258:358 */     result.append("   * \n");
/* 259:359 */     result.append("   * @param i the row to process\n");
/* 260:360 */     result.append("   * @return the processed row\n");
/* 261:361 */     result.append("   */\n");
/* 262:362 */     result.append("  public static Object[] filter(Object[] i) {\n");
/* 263:363 */     result.append("    Object[] result;\n");
/* 264:364 */     result.append("\n");
/* 265:365 */     result.append("    result = new Object[i.length];\n");
/* 266:366 */     result.append("    for (int n = 0; n < i.length; n++) {\n");
/* 267:367 */     result.append("      if (i[n] == null) {\n");
/* 268:368 */     result.append("        if (NUMERIC[n])\n");
/* 269:369 */     result.append("          result[n] = MEANS[n];\n");
/* 270:370 */     result.append("        else if (NOMINAL[n])\n");
/* 271:371 */     result.append("          result[n] = MODES[n];\n");
/* 272:372 */     result.append("        else\n");
/* 273:373 */     result.append("          result[n] = i[n];\n");
/* 274:374 */     result.append("      }\n");
/* 275:375 */     result.append("      else {\n");
/* 276:376 */     result.append("        result[n] = i[n];\n");
/* 277:377 */     result.append("      }\n");
/* 278:378 */     result.append("    }\n");
/* 279:379 */     result.append("\n");
/* 280:380 */     result.append("    return result;\n");
/* 281:381 */     result.append("  }\n");
/* 282:382 */     result.append("\n");
/* 283:383 */     result.append("  /**\n");
/* 284:384 */     result.append("   * filters multiple rows\n");
/* 285:385 */     result.append("   * \n");
/* 286:386 */     result.append("   * @param i the rows to process\n");
/* 287:387 */     result.append("   * @return the processed rows\n");
/* 288:388 */     result.append("   */\n");
/* 289:389 */     result.append("  public static Object[][] filter(Object[][] i) {\n");
/* 290:390 */     result.append("    Object[][] result;\n");
/* 291:391 */     result.append("\n");
/* 292:392 */     result.append("    result = new Object[i.length][];\n");
/* 293:393 */     result.append("    for (int n = 0; n < i.length; n++) {\n");
/* 294:394 */     result.append("      result[n] = filter(i[n]);\n");
/* 295:395 */     result.append("    }\n");
/* 296:396 */     result.append("\n");
/* 297:397 */     result.append("    return result;\n");
/* 298:398 */     result.append("  }\n");
/* 299:399 */     result.append("}\n");
/* 300:    */     
/* 301:401 */     return result.toString();
/* 302:    */   }
/* 303:    */   
/* 304:    */   public String getRevision()
/* 305:    */   {
/* 306:410 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 307:    */   }
/* 308:    */   
/* 309:    */   public static void main(String[] argv)
/* 310:    */   {
/* 311:420 */     runFilter(new ReplaceMissingValues(), argv);
/* 312:    */   }
/* 313:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.ReplaceMissingValues
 * JD-Core Version:    0.7.0.1
 */