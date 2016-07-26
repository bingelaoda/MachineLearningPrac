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
/*  15:    */ public class Center
/*  16:    */   extends PotentialClassIgnorer
/*  17:    */   implements UnsupervisedFilter, Sourcable
/*  18:    */ {
/*  19:    */   private static final long serialVersionUID = -9101338448900581023L;
/*  20:    */   private double[] m_Means;
/*  21:    */   
/*  22:    */   public String globalInfo()
/*  23:    */   {
/*  24: 73 */     return "Centers all numeric attributes in the given dataset to have zero mean (apart from the class attribute, if set).";
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
/*  47:110 */     super.setInputFormat(instanceInfo);
/*  48:111 */     setOutputFormat(instanceInfo);
/*  49:112 */     this.m_Means = null;
/*  50:113 */     return true;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public boolean input(Instance instance)
/*  54:    */   {
/*  55:127 */     if (getInputFormat() == null) {
/*  56:128 */       throw new IllegalStateException("No input instance format defined");
/*  57:    */     }
/*  58:130 */     if (this.m_NewBatch)
/*  59:    */     {
/*  60:131 */       resetQueue();
/*  61:132 */       this.m_NewBatch = false;
/*  62:    */     }
/*  63:135 */     if (this.m_Means == null)
/*  64:    */     {
/*  65:136 */       bufferInput(instance);
/*  66:137 */       return false;
/*  67:    */     }
/*  68:140 */     convertInstance(instance);
/*  69:141 */     return true;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public boolean batchFinished()
/*  73:    */   {
/*  74:154 */     if (getInputFormat() == null) {
/*  75:155 */       throw new IllegalStateException("No input instance format defined");
/*  76:    */     }
/*  77:157 */     if (this.m_Means == null)
/*  78:    */     {
/*  79:158 */       Instances input = getInputFormat();
/*  80:159 */       this.m_Means = new double[input.numAttributes()];
/*  81:160 */       for (int i = 0; i < input.numAttributes(); i++) {
/*  82:161 */         if ((input.attribute(i).isNumeric()) && (input.classIndex() != i)) {
/*  83:163 */           this.m_Means[i] = input.meanOrMode(i);
/*  84:    */         }
/*  85:    */       }
/*  86:168 */       for (int i = 0; i < input.numInstances(); i++) {
/*  87:169 */         convertInstance(input.instance(i));
/*  88:    */       }
/*  89:    */     }
/*  90:173 */     flushInput();
/*  91:    */     
/*  92:175 */     this.m_NewBatch = true;
/*  93:176 */     return numPendingOutput() != 0;
/*  94:    */   }
/*  95:    */   
/*  96:    */   private void convertInstance(Instance instance)
/*  97:    */   {
/*  98:186 */     Instance inst = null;
/*  99:188 */     if ((instance instanceof SparseInstance))
/* 100:    */     {
/* 101:189 */       double[] newVals = new double[instance.numAttributes()];
/* 102:190 */       int[] newIndices = new int[instance.numAttributes()];
/* 103:191 */       double[] vals = instance.toDoubleArray();
/* 104:192 */       int ind = 0;
/* 105:193 */       for (int j = 0; j < instance.numAttributes(); j++) {
/* 106:195 */         if ((instance.attribute(j).isNumeric()) && (!Utils.isMissingValue(vals[j])) && (getInputFormat().classIndex() != j))
/* 107:    */         {
/* 108:199 */           double value = vals[j] - this.m_Means[j];
/* 109:200 */           if (value != 0.0D)
/* 110:    */           {
/* 111:201 */             newVals[ind] = value;
/* 112:202 */             newIndices[ind] = j;
/* 113:203 */             ind++;
/* 114:    */           }
/* 115:    */         }
/* 116:    */         else
/* 117:    */         {
/* 118:206 */           double value = vals[j];
/* 119:207 */           if (value != 0.0D)
/* 120:    */           {
/* 121:208 */             newVals[ind] = value;
/* 122:209 */             newIndices[ind] = j;
/* 123:210 */             ind++;
/* 124:    */           }
/* 125:    */         }
/* 126:    */       }
/* 127:214 */       double[] tempVals = new double[ind];
/* 128:215 */       int[] tempInd = new int[ind];
/* 129:216 */       System.arraycopy(newVals, 0, tempVals, 0, ind);
/* 130:217 */       System.arraycopy(newIndices, 0, tempInd, 0, ind);
/* 131:218 */       inst = new SparseInstance(instance.weight(), tempVals, tempInd, instance.numAttributes());
/* 132:    */     }
/* 133:    */     else
/* 134:    */     {
/* 135:222 */       double[] vals = instance.toDoubleArray();
/* 136:223 */       for (int j = 0; j < getInputFormat().numAttributes(); j++) {
/* 137:224 */         if ((instance.attribute(j).isNumeric()) && (!Utils.isMissingValue(vals[j])) && (getInputFormat().classIndex() != j)) {
/* 138:227 */           vals[j] -= this.m_Means[j];
/* 139:    */         }
/* 140:    */       }
/* 141:230 */       inst = new DenseInstance(instance.weight(), vals);
/* 142:    */     }
/* 143:233 */     inst.setDataset(instance.dataset());
/* 144:    */     
/* 145:235 */     push(inst, false);
/* 146:    */   }
/* 147:    */   
/* 148:    */   public String toSource(String className, Instances data)
/* 149:    */     throws Exception
/* 150:    */   {
/* 151:263 */     StringBuffer result = new StringBuffer();
/* 152:    */     
/* 153:    */ 
/* 154:266 */     boolean[] process = new boolean[data.numAttributes()];
/* 155:267 */     for (int i = 0; i < data.numAttributes(); i++) {
/* 156:268 */       process[i] = ((data.attribute(i).isNumeric()) && (i != data.classIndex()) ? 1 : false);
/* 157:    */     }
/* 158:271 */     result.append("class " + className + " {\n");
/* 159:272 */     result.append("\n");
/* 160:273 */     result.append("  /** lists which attributes will be processed */\n");
/* 161:274 */     result.append("  protected final static boolean[] PROCESS = new boolean[]{" + Utils.arrayToString(process) + "};\n");
/* 162:275 */     result.append("\n");
/* 163:276 */     result.append("  /** the computed means */\n");
/* 164:277 */     result.append("  protected final static double[] MEANS = new double[]{" + Utils.arrayToString(this.m_Means) + "};\n");
/* 165:278 */     result.append("\n");
/* 166:279 */     result.append("  /**\n");
/* 167:280 */     result.append("   * filters a single row\n");
/* 168:281 */     result.append("   * \n");
/* 169:282 */     result.append("   * @param i the row to process\n");
/* 170:283 */     result.append("   * @return the processed row\n");
/* 171:284 */     result.append("   */\n");
/* 172:285 */     result.append("  public static Object[] filter(Object[] i) {\n");
/* 173:286 */     result.append("    Object[] result;\n");
/* 174:287 */     result.append("\n");
/* 175:288 */     result.append("    result = new Object[i.length];\n");
/* 176:289 */     result.append("    for (int n = 0; n < i.length; n++) {\n");
/* 177:290 */     result.append("      if (PROCESS[n] && (i[n] != null))\n");
/* 178:291 */     result.append("        result[n] = ((Double) i[n]) - MEANS[n];\n");
/* 179:292 */     result.append("      else\n");
/* 180:293 */     result.append("        result[n] = i[n];\n");
/* 181:294 */     result.append("    }\n");
/* 182:295 */     result.append("\n");
/* 183:296 */     result.append("    return result;\n");
/* 184:297 */     result.append("  }\n");
/* 185:298 */     result.append("\n");
/* 186:299 */     result.append("  /**\n");
/* 187:300 */     result.append("   * filters multiple rows\n");
/* 188:301 */     result.append("   * \n");
/* 189:302 */     result.append("   * @param i the rows to process\n");
/* 190:303 */     result.append("   * @return the processed rows\n");
/* 191:304 */     result.append("   */\n");
/* 192:305 */     result.append("  public static Object[][] filter(Object[][] i) {\n");
/* 193:306 */     result.append("    Object[][] result;\n");
/* 194:307 */     result.append("\n");
/* 195:308 */     result.append("    result = new Object[i.length][];\n");
/* 196:309 */     result.append("    for (int n = 0; n < i.length; n++) {\n");
/* 197:310 */     result.append("      result[n] = filter(i[n]);\n");
/* 198:311 */     result.append("    }\n");
/* 199:312 */     result.append("\n");
/* 200:313 */     result.append("    return result;\n");
/* 201:314 */     result.append("  }\n");
/* 202:315 */     result.append("}\n");
/* 203:    */     
/* 204:317 */     return result.toString();
/* 205:    */   }
/* 206:    */   
/* 207:    */   public String getRevision()
/* 208:    */   {
/* 209:326 */     return RevisionUtils.extract("$Revision: 12037 $");
/* 210:    */   }
/* 211:    */   
/* 212:    */   public static void main(String[] args)
/* 213:    */   {
/* 214:335 */     runFilter(new Center(), args);
/* 215:    */   }
/* 216:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.Center
 * JD-Core Version:    0.7.0.1
 */