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
/*  15:    */ public class Standardize
/*  16:    */   extends PotentialClassIgnorer
/*  17:    */   implements UnsupervisedFilter, Sourcable
/*  18:    */ {
/*  19:    */   static final long serialVersionUID = -6830769026855053281L;
/*  20:    */   private double[] m_Means;
/*  21:    */   private double[] m_StdDevs;
/*  22:    */   
/*  23:    */   public String globalInfo()
/*  24:    */   {
/*  25: 75 */     return "Standardizes all numeric attributes in the given dataset to have zero mean and unit variance (apart from the class attribute, if set).";
/*  26:    */   }
/*  27:    */   
/*  28:    */   public Capabilities getCapabilities()
/*  29:    */   {
/*  30: 86 */     Capabilities result = super.getCapabilities();
/*  31: 87 */     result.disableAll();
/*  32:    */     
/*  33:    */ 
/*  34: 90 */     result.enableAllAttributes();
/*  35: 91 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  36:    */     
/*  37:    */ 
/*  38: 94 */     result.enableAllClasses();
/*  39: 95 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  40: 96 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  41:    */     
/*  42: 98 */     return result;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public boolean setInputFormat(Instances instanceInfo)
/*  46:    */     throws Exception
/*  47:    */   {
/*  48:114 */     super.setInputFormat(instanceInfo);
/*  49:115 */     setOutputFormat(instanceInfo);
/*  50:116 */     this.m_Means = (this.m_StdDevs = null);
/*  51:117 */     return true;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public boolean input(Instance instance)
/*  55:    */     throws Exception
/*  56:    */   {
/*  57:131 */     if (getInputFormat() == null) {
/*  58:132 */       throw new IllegalStateException("No input instance format defined");
/*  59:    */     }
/*  60:134 */     if (this.m_NewBatch)
/*  61:    */     {
/*  62:135 */       resetQueue();
/*  63:136 */       this.m_NewBatch = false;
/*  64:    */     }
/*  65:138 */     if (this.m_Means == null)
/*  66:    */     {
/*  67:139 */       bufferInput(instance);
/*  68:140 */       return false;
/*  69:    */     }
/*  70:142 */     convertInstance(instance);
/*  71:143 */     return true;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public boolean batchFinished()
/*  75:    */     throws Exception
/*  76:    */   {
/*  77:158 */     if (getInputFormat() == null) {
/*  78:159 */       throw new IllegalStateException("No input instance format defined");
/*  79:    */     }
/*  80:161 */     if (this.m_Means == null)
/*  81:    */     {
/*  82:162 */       Instances input = getInputFormat();
/*  83:163 */       this.m_Means = new double[input.numAttributes()];
/*  84:164 */       this.m_StdDevs = new double[input.numAttributes()];
/*  85:165 */       for (int i = 0; i < input.numAttributes(); i++) {
/*  86:166 */         if ((input.attribute(i).isNumeric()) && (input.classIndex() != i))
/*  87:    */         {
/*  88:168 */           this.m_Means[i] = input.meanOrMode(i);
/*  89:169 */           this.m_StdDevs[i] = Math.sqrt(input.variance(i));
/*  90:    */         }
/*  91:    */       }
/*  92:174 */       for (int i = 0; i < input.numInstances(); i++) {
/*  93:175 */         convertInstance(input.instance(i));
/*  94:    */       }
/*  95:    */     }
/*  96:179 */     flushInput();
/*  97:    */     
/*  98:181 */     this.m_NewBatch = true;
/*  99:182 */     return numPendingOutput() != 0;
/* 100:    */   }
/* 101:    */   
/* 102:    */   private void convertInstance(Instance instance)
/* 103:    */     throws Exception
/* 104:    */   {
/* 105:194 */     Instance inst = null;
/* 106:195 */     if ((instance instanceof SparseInstance))
/* 107:    */     {
/* 108:196 */       double[] newVals = new double[instance.numAttributes()];
/* 109:197 */       int[] newIndices = new int[instance.numAttributes()];
/* 110:198 */       double[] vals = instance.toDoubleArray();
/* 111:199 */       int ind = 0;
/* 112:200 */       for (int j = 0; j < instance.numAttributes(); j++) {
/* 113:202 */         if ((instance.attribute(j).isNumeric()) && (!Utils.isMissingValue(vals[j])) && (getInputFormat().classIndex() != j))
/* 114:    */         {
/* 115:    */           double value;
/* 116:    */           double value;
/* 117:207 */           if (this.m_StdDevs[j] > 0.0D) {
/* 118:208 */             value = (vals[j] - this.m_Means[j]) / this.m_StdDevs[j];
/* 119:    */           } else {
/* 120:210 */             value = vals[j] - this.m_Means[j];
/* 121:    */           }
/* 122:212 */           if (Double.isNaN(value)) {
/* 123:213 */             throw new Exception("A NaN value was generated while standardizing attribute " + instance.attribute(j).name());
/* 124:    */           }
/* 125:217 */           if (value != 0.0D)
/* 126:    */           {
/* 127:218 */             newVals[ind] = value;
/* 128:219 */             newIndices[ind] = j;
/* 129:220 */             ind++;
/* 130:    */           }
/* 131:    */         }
/* 132:    */         else
/* 133:    */         {
/* 134:223 */           double value = vals[j];
/* 135:224 */           if (value != 0.0D)
/* 136:    */           {
/* 137:225 */             newVals[ind] = value;
/* 138:226 */             newIndices[ind] = j;
/* 139:227 */             ind++;
/* 140:    */           }
/* 141:    */         }
/* 142:    */       }
/* 143:231 */       double[] tempVals = new double[ind];
/* 144:232 */       int[] tempInd = new int[ind];
/* 145:233 */       System.arraycopy(newVals, 0, tempVals, 0, ind);
/* 146:234 */       System.arraycopy(newIndices, 0, tempInd, 0, ind);
/* 147:235 */       inst = new SparseInstance(instance.weight(), tempVals, tempInd, instance.numAttributes());
/* 148:    */     }
/* 149:    */     else
/* 150:    */     {
/* 151:238 */       double[] vals = instance.toDoubleArray();
/* 152:239 */       for (int j = 0; j < getInputFormat().numAttributes(); j++) {
/* 153:240 */         if ((instance.attribute(j).isNumeric()) && (!Utils.isMissingValue(vals[j])) && (getInputFormat().classIndex() != j))
/* 154:    */         {
/* 155:245 */           if (this.m_StdDevs[j] > 0.0D) {
/* 156:246 */             vals[j] = ((vals[j] - this.m_Means[j]) / this.m_StdDevs[j]);
/* 157:    */           } else {
/* 158:248 */             vals[j] -= this.m_Means[j];
/* 159:    */           }
/* 160:250 */           if (Double.isNaN(vals[j])) {
/* 161:251 */             throw new Exception("A NaN value was generated while standardizing attribute " + instance.attribute(j).name());
/* 162:    */           }
/* 163:    */         }
/* 164:    */       }
/* 165:257 */       inst = new DenseInstance(instance.weight(), vals);
/* 166:    */     }
/* 167:259 */     inst.setDataset(instance.dataset());
/* 168:260 */     push(inst, false);
/* 169:    */   }
/* 170:    */   
/* 171:    */   public String toSource(String className, Instances data)
/* 172:    */     throws Exception
/* 173:    */   {
/* 174:288 */     StringBuffer result = new StringBuffer();
/* 175:    */     
/* 176:    */ 
/* 177:291 */     boolean[] process = new boolean[data.numAttributes()];
/* 178:292 */     for (int i = 0; i < data.numAttributes(); i++) {
/* 179:293 */       process[i] = ((data.attribute(i).isNumeric()) && (i != data.classIndex()) ? 1 : false);
/* 180:    */     }
/* 181:296 */     result.append("class " + className + " {\n");
/* 182:297 */     result.append("\n");
/* 183:298 */     result.append("  /** lists which attributes will be processed */\n");
/* 184:299 */     result.append("  protected final static boolean[] PROCESS = new boolean[]{" + Utils.arrayToString(process) + "};\n");
/* 185:300 */     result.append("\n");
/* 186:301 */     result.append("  /** the computed means */\n");
/* 187:302 */     result.append("  protected final static double[] MEANS = new double[]{" + Utils.arrayToString(this.m_Means) + "};\n");
/* 188:303 */     result.append("\n");
/* 189:304 */     result.append("  /** the computed standard deviations */\n");
/* 190:305 */     result.append("  protected final static double[] STDEVS = new double[]{" + Utils.arrayToString(this.m_StdDevs) + "};\n");
/* 191:306 */     result.append("\n");
/* 192:307 */     result.append("  /**\n");
/* 193:308 */     result.append("   * filters a single row\n");
/* 194:309 */     result.append("   * \n");
/* 195:310 */     result.append("   * @param i the row to process\n");
/* 196:311 */     result.append("   * @return the processed row\n");
/* 197:312 */     result.append("   */\n");
/* 198:313 */     result.append("  public static Object[] filter(Object[] i) {\n");
/* 199:314 */     result.append("    Object[] result;\n");
/* 200:315 */     result.append("\n");
/* 201:316 */     result.append("    result = new Object[i.length];\n");
/* 202:317 */     result.append("    for (int n = 0; n < i.length; n++) {\n");
/* 203:318 */     result.append("      if (PROCESS[n] && (i[n] != null)) {\n");
/* 204:319 */     result.append("        if (STDEVS[n] > 0)\n");
/* 205:320 */     result.append("          result[n] = (((Double) i[n]) - MEANS[n]) / STDEVS[n];\n");
/* 206:321 */     result.append("        else\n");
/* 207:322 */     result.append("          result[n] = ((Double) i[n]) - MEANS[n];\n");
/* 208:323 */     result.append("      }\n");
/* 209:324 */     result.append("      else {\n");
/* 210:325 */     result.append("        result[n] = i[n];\n");
/* 211:326 */     result.append("      }\n");
/* 212:327 */     result.append("    }\n");
/* 213:328 */     result.append("\n");
/* 214:329 */     result.append("    return result;\n");
/* 215:330 */     result.append("  }\n");
/* 216:331 */     result.append("\n");
/* 217:332 */     result.append("  /**\n");
/* 218:333 */     result.append("   * filters multiple rows\n");
/* 219:334 */     result.append("   * \n");
/* 220:335 */     result.append("   * @param i the rows to process\n");
/* 221:336 */     result.append("   * @return the processed rows\n");
/* 222:337 */     result.append("   */\n");
/* 223:338 */     result.append("  public static Object[][] filter(Object[][] i) {\n");
/* 224:339 */     result.append("    Object[][] result;\n");
/* 225:340 */     result.append("\n");
/* 226:341 */     result.append("    result = new Object[i.length][];\n");
/* 227:342 */     result.append("    for (int n = 0; n < i.length; n++) {\n");
/* 228:343 */     result.append("      result[n] = filter(i[n]);\n");
/* 229:344 */     result.append("    }\n");
/* 230:345 */     result.append("\n");
/* 231:346 */     result.append("    return result;\n");
/* 232:347 */     result.append("  }\n");
/* 233:348 */     result.append("}\n");
/* 234:    */     
/* 235:350 */     return result.toString();
/* 236:    */   }
/* 237:    */   
/* 238:    */   public String getRevision()
/* 239:    */   {
/* 240:359 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 241:    */   }
/* 242:    */   
/* 243:    */   public static void main(String[] argv)
/* 244:    */   {
/* 245:369 */     runFilter(new Standardize(), argv);
/* 246:    */   }
/* 247:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.Standardize
 * JD-Core Version:    0.7.0.1
 */