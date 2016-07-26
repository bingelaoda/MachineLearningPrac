/*   1:    */ package weka.classifiers.trees.lmt;
/*   2:    */ 
/*   3:    */ import weka.classifiers.trees.j48.ClassifierSplitModel;
/*   4:    */ import weka.classifiers.trees.j48.Distribution;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Instance;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ import weka.core.Utils;
/*  10:    */ 
/*  11:    */ public class ResidualSplit
/*  12:    */   extends ClassifierSplitModel
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = -5055883734183713525L;
/*  15:    */   protected Attribute m_attribute;
/*  16:    */   protected int m_attIndex;
/*  17:    */   protected int m_numInstances;
/*  18:    */   protected int m_numClasses;
/*  19:    */   protected Instances m_data;
/*  20:    */   protected double[][] m_dataZs;
/*  21:    */   protected double[][] m_dataWs;
/*  22:    */   protected double m_splitPoint;
/*  23:    */   
/*  24:    */   public ResidualSplit(int attIndex)
/*  25:    */   {
/*  26: 74 */     this.m_attIndex = attIndex;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public void buildClassifier(Instances data, double[][] dataZs, double[][] dataWs)
/*  30:    */     throws Exception
/*  31:    */   {
/*  32: 84 */     this.m_numClasses = data.numClasses();
/*  33: 85 */     this.m_numInstances = data.numInstances();
/*  34: 86 */     if (this.m_numInstances == 0) {
/*  35: 86 */       throw new Exception("Can't build split on 0 instances");
/*  36:    */     }
/*  37: 89 */     this.m_data = data;
/*  38: 90 */     this.m_dataZs = dataZs;
/*  39: 91 */     this.m_dataWs = dataWs;
/*  40: 92 */     this.m_attribute = data.attribute(this.m_attIndex);
/*  41: 95 */     if (this.m_attribute.isNominal())
/*  42:    */     {
/*  43: 96 */       this.m_splitPoint = 0.0D;
/*  44: 97 */       this.m_numSubsets = this.m_attribute.numValues();
/*  45:    */     }
/*  46:    */     else
/*  47:    */     {
/*  48: 99 */       getSplitPoint();
/*  49:100 */       this.m_numSubsets = 2;
/*  50:    */     }
/*  51:103 */     this.m_distribution = new Distribution(data, this);
/*  52:    */   }
/*  53:    */   
/*  54:    */   protected boolean getSplitPoint()
/*  55:    */     throws Exception
/*  56:    */   {
/*  57:113 */     double[] splitPoints = new double[this.m_numInstances];
/*  58:114 */     int numSplitPoints = 0;
/*  59:    */     
/*  60:116 */     Instances sortedData = new Instances(this.m_data);
/*  61:117 */     sortedData.sort(sortedData.attribute(this.m_attIndex));
/*  62:    */     
/*  63:    */ 
/*  64:    */ 
/*  65:121 */     double last = sortedData.instance(0).value(this.m_attIndex);
/*  66:123 */     for (int i = 0; i < this.m_numInstances - 1; i++)
/*  67:    */     {
/*  68:124 */       double current = sortedData.instance(i + 1).value(this.m_attIndex);
/*  69:125 */       if (!Utils.eq(current, last)) {
/*  70:126 */         splitPoints[(numSplitPoints++)] = ((last + current) / 2.0D);
/*  71:    */       }
/*  72:128 */       last = current;
/*  73:    */     }
/*  74:132 */     double[] entropyGain = new double[numSplitPoints];
/*  75:134 */     for (int i = 0; i < numSplitPoints; i++)
/*  76:    */     {
/*  77:135 */       this.m_splitPoint = splitPoints[i];
/*  78:136 */       entropyGain[i] = entropyGain();
/*  79:    */     }
/*  80:140 */     int bestSplit = -1;
/*  81:141 */     double bestGain = -1.797693134862316E+308D;
/*  82:143 */     for (int i = 0; i < numSplitPoints; i++) {
/*  83:144 */       if (entropyGain[i] > bestGain)
/*  84:    */       {
/*  85:145 */         bestGain = entropyGain[i];
/*  86:146 */         bestSplit = i;
/*  87:    */       }
/*  88:    */     }
/*  89:150 */     if (bestSplit < 0) {
/*  90:150 */       return false;
/*  91:    */     }
/*  92:152 */     this.m_splitPoint = splitPoints[bestSplit];
/*  93:153 */     return true;
/*  94:    */   }
/*  95:    */   
/*  96:    */   public double entropyGain()
/*  97:    */     throws Exception
/*  98:    */   {
/*  99:    */     int numSubsets;
/* 100:    */     int numSubsets;
/* 101:162 */     if (this.m_attribute.isNominal()) {
/* 102:163 */       numSubsets = this.m_attribute.numValues();
/* 103:    */     } else {
/* 104:165 */       numSubsets = 2;
/* 105:    */     }
/* 106:168 */     double[][][] splitDataZs = new double[numSubsets][][];
/* 107:169 */     double[][][] splitDataWs = new double[numSubsets][][];
/* 108:    */     
/* 109:    */ 
/* 110:172 */     int[] subsetSize = new int[numSubsets];
/* 111:173 */     for (int i = 0; i < this.m_numInstances; i++)
/* 112:    */     {
/* 113:174 */       int subset = whichSubset(this.m_data.instance(i));
/* 114:175 */       if (subset < 0) {
/* 115:175 */         throw new Exception("ResidualSplit: no support for splits on missing values");
/* 116:    */       }
/* 117:176 */       subsetSize[subset] += 1;
/* 118:    */     }
/* 119:179 */     for (int i = 0; i < numSubsets; i++)
/* 120:    */     {
/* 121:180 */       splitDataZs[i] = new double[subsetSize[i]][];
/* 122:181 */       splitDataWs[i] = new double[subsetSize[i]][];
/* 123:    */     }
/* 124:185 */     int[] subsetCount = new int[numSubsets];
/* 125:188 */     for (int i = 0; i < this.m_numInstances; i++)
/* 126:    */     {
/* 127:189 */       int subset = whichSubset(this.m_data.instance(i));
/* 128:190 */       splitDataZs[subset][subsetCount[subset]] = this.m_dataZs[i];
/* 129:191 */       splitDataWs[subset][subsetCount[subset]] = this.m_dataWs[i];
/* 130:192 */       subsetCount[subset] += 1;
/* 131:    */     }
/* 132:196 */     double entropyOrig = entropy(this.m_dataZs, this.m_dataWs);
/* 133:    */     
/* 134:198 */     double entropySplit = 0.0D;
/* 135:200 */     for (int i = 0; i < numSubsets; i++) {
/* 136:201 */       entropySplit += entropy(splitDataZs[i], splitDataWs[i]);
/* 137:    */     }
/* 138:204 */     return entropyOrig - entropySplit;
/* 139:    */   }
/* 140:    */   
/* 141:    */   protected double entropy(double[][] dataZs, double[][] dataWs)
/* 142:    */   {
/* 143:212 */     double entropy = 0.0D;
/* 144:213 */     int numInstances = dataZs.length;
/* 145:215 */     for (int j = 0; j < this.m_numClasses; j++)
/* 146:    */     {
/* 147:218 */       double m = 0.0D;
/* 148:219 */       double sum = 0.0D;
/* 149:220 */       for (int i = 0; i < numInstances; i++)
/* 150:    */       {
/* 151:221 */         m += dataZs[i][j] * dataWs[i][j];
/* 152:222 */         sum += dataWs[i][j];
/* 153:    */       }
/* 154:224 */       m /= sum;
/* 155:227 */       for (int i = 0; i < numInstances; i++) {
/* 156:228 */         entropy += dataWs[i][j] * Math.pow(dataZs[i][j] - m, 2.0D);
/* 157:    */       }
/* 158:    */     }
/* 159:233 */     return entropy;
/* 160:    */   }
/* 161:    */   
/* 162:    */   public boolean checkModel(int minNumInstances)
/* 163:    */   {
/* 164:241 */     int count = 0;
/* 165:242 */     for (int i = 0; i < this.m_distribution.numBags(); i++) {
/* 166:243 */       if (this.m_distribution.perBag(i) >= minNumInstances) {
/* 167:243 */         count++;
/* 168:    */       }
/* 169:    */     }
/* 170:245 */     return count >= 2;
/* 171:    */   }
/* 172:    */   
/* 173:    */   public final String leftSide(Instances data)
/* 174:    */   {
/* 175:253 */     return data.attribute(this.m_attIndex).name();
/* 176:    */   }
/* 177:    */   
/* 178:    */   public final String rightSide(int index, Instances data)
/* 179:    */   {
/* 180:263 */     StringBuffer text = new StringBuffer();
/* 181:264 */     if (data.attribute(this.m_attIndex).isNominal()) {
/* 182:265 */       text.append(" = " + data.attribute(this.m_attIndex).value(index));
/* 183:268 */     } else if (index == 0) {
/* 184:269 */       text.append(" <= " + Utils.doubleToString(this.m_splitPoint, 6));
/* 185:    */     } else {
/* 186:272 */       text.append(" > " + Utils.doubleToString(this.m_splitPoint, 6));
/* 187:    */     }
/* 188:274 */     return text.toString();
/* 189:    */   }
/* 190:    */   
/* 191:    */   public final int whichSubset(Instance instance)
/* 192:    */     throws Exception
/* 193:    */   {
/* 194:280 */     if (instance.isMissing(this.m_attIndex)) {
/* 195:281 */       return -1;
/* 196:    */     }
/* 197:283 */     if (instance.attribute(this.m_attIndex).isNominal()) {
/* 198:284 */       return (int)instance.value(this.m_attIndex);
/* 199:    */     }
/* 200:286 */     if (Utils.smOrEq(instance.value(this.m_attIndex), this.m_splitPoint)) {
/* 201:287 */       return 0;
/* 202:    */     }
/* 203:289 */     return 1;
/* 204:    */   }
/* 205:    */   
/* 206:    */   public void buildClassifier(Instances data) {}
/* 207:    */   
/* 208:    */   public final double[] weights(Instance instance)
/* 209:    */   {
/* 210:301 */     return null;
/* 211:    */   }
/* 212:    */   
/* 213:    */   public final String sourceExpression(int index, Instances data)
/* 214:    */   {
/* 215:307 */     return "";
/* 216:    */   }
/* 217:    */   
/* 218:    */   public String getRevision()
/* 219:    */   {
/* 220:316 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 221:    */   }
/* 222:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.lmt.ResidualSplit
 * JD-Core Version:    0.7.0.1
 */