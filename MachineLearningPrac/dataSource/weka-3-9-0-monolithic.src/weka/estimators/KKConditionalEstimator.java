/*   1:    */ package weka.estimators;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Random;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ import weka.core.Statistics;
/*   7:    */ import weka.core.Utils;
/*   8:    */ 
/*   9:    */ public class KKConditionalEstimator
/*  10:    */   implements ConditionalEstimator
/*  11:    */ {
/*  12:    */   private double[] m_Values;
/*  13:    */   private double[] m_CondValues;
/*  14:    */   private double[] m_Weights;
/*  15:    */   private int m_NumValues;
/*  16:    */   private double m_SumOfWeights;
/*  17:    */   private double m_StandardDev;
/*  18:    */   private boolean m_AllWeightsOne;
/*  19:    */   private double m_Precision;
/*  20:    */   
/*  21:    */   private int findNearestPair(double key, double secondaryKey)
/*  22:    */   {
/*  23: 74 */     int low = 0;
/*  24: 75 */     int high = this.m_NumValues;
/*  25: 76 */     int middle = 0;
/*  26: 77 */     while (low < high)
/*  27:    */     {
/*  28: 78 */       middle = (low + high) / 2;
/*  29: 79 */       double current = this.m_CondValues[middle];
/*  30: 80 */       if (current == key)
/*  31:    */       {
/*  32: 81 */         double secondary = this.m_Values[middle];
/*  33: 82 */         if (secondary == secondaryKey) {
/*  34: 83 */           return middle;
/*  35:    */         }
/*  36: 85 */         if (secondary > secondaryKey) {
/*  37: 86 */           high = middle;
/*  38: 87 */         } else if (secondary < secondaryKey) {
/*  39: 88 */           low = middle + 1;
/*  40:    */         }
/*  41:    */       }
/*  42: 91 */       if (current > key) {
/*  43: 92 */         high = middle;
/*  44: 93 */       } else if (current < key) {
/*  45: 94 */         low = middle + 1;
/*  46:    */       }
/*  47:    */     }
/*  48: 97 */     return low;
/*  49:    */   }
/*  50:    */   
/*  51:    */   private double round(double data)
/*  52:    */   {
/*  53:108 */     return Math.rint(data / this.m_Precision) * this.m_Precision;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public KKConditionalEstimator(double precision)
/*  57:    */   {
/*  58:120 */     this.m_CondValues = new double[50];
/*  59:121 */     this.m_Values = new double[50];
/*  60:122 */     this.m_Weights = new double[50];
/*  61:123 */     this.m_NumValues = 0;
/*  62:124 */     this.m_SumOfWeights = 0.0D;
/*  63:125 */     this.m_StandardDev = 0.0D;
/*  64:126 */     this.m_AllWeightsOne = true;
/*  65:127 */     this.m_Precision = precision;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void addValue(double data, double given, double weight)
/*  69:    */   {
/*  70:139 */     data = round(data);
/*  71:140 */     given = round(given);
/*  72:141 */     int insertIndex = findNearestPair(given, data);
/*  73:142 */     if ((this.m_NumValues <= insertIndex) || (this.m_CondValues[insertIndex] != given) || (this.m_Values[insertIndex] != data))
/*  74:    */     {
/*  75:145 */       if (this.m_NumValues < this.m_Values.length)
/*  76:    */       {
/*  77:146 */         int left = this.m_NumValues - insertIndex;
/*  78:147 */         System.arraycopy(this.m_Values, insertIndex, this.m_Values, insertIndex + 1, left);
/*  79:    */         
/*  80:149 */         System.arraycopy(this.m_CondValues, insertIndex, this.m_CondValues, insertIndex + 1, left);
/*  81:    */         
/*  82:151 */         System.arraycopy(this.m_Weights, insertIndex, this.m_Weights, insertIndex + 1, left);
/*  83:    */         
/*  84:153 */         this.m_Values[insertIndex] = data;
/*  85:154 */         this.m_CondValues[insertIndex] = given;
/*  86:155 */         this.m_Weights[insertIndex] = weight;
/*  87:156 */         this.m_NumValues += 1;
/*  88:    */       }
/*  89:    */       else
/*  90:    */       {
/*  91:158 */         double[] newValues = new double[this.m_Values.length * 2];
/*  92:159 */         double[] newCondValues = new double[this.m_Values.length * 2];
/*  93:160 */         double[] newWeights = new double[this.m_Values.length * 2];
/*  94:161 */         int left = this.m_NumValues - insertIndex;
/*  95:162 */         System.arraycopy(this.m_Values, 0, newValues, 0, insertIndex);
/*  96:163 */         System.arraycopy(this.m_CondValues, 0, newCondValues, 0, insertIndex);
/*  97:164 */         System.arraycopy(this.m_Weights, 0, newWeights, 0, insertIndex);
/*  98:165 */         newValues[insertIndex] = data;
/*  99:166 */         newCondValues[insertIndex] = given;
/* 100:167 */         newWeights[insertIndex] = weight;
/* 101:168 */         System.arraycopy(this.m_Values, insertIndex, newValues, insertIndex + 1, left);
/* 102:    */         
/* 103:170 */         System.arraycopy(this.m_CondValues, insertIndex, newCondValues, insertIndex + 1, left);
/* 104:    */         
/* 105:172 */         System.arraycopy(this.m_Weights, insertIndex, newWeights, insertIndex + 1, left);
/* 106:    */         
/* 107:174 */         this.m_NumValues += 1;
/* 108:175 */         this.m_Values = newValues;
/* 109:176 */         this.m_CondValues = newCondValues;
/* 110:177 */         this.m_Weights = newWeights;
/* 111:    */       }
/* 112:179 */       if (weight != 1.0D) {
/* 113:180 */         this.m_AllWeightsOne = false;
/* 114:    */       }
/* 115:    */     }
/* 116:    */     else
/* 117:    */     {
/* 118:183 */       this.m_Weights[insertIndex] += weight;
/* 119:184 */       this.m_AllWeightsOne = false;
/* 120:    */     }
/* 121:186 */     this.m_SumOfWeights += weight;
/* 122:187 */     double range = this.m_CondValues[(this.m_NumValues - 1)] - this.m_CondValues[0];
/* 123:188 */     this.m_StandardDev = Math.max(range / Math.sqrt(this.m_SumOfWeights), this.m_Precision / 6.0D);
/* 124:    */   }
/* 125:    */   
/* 126:    */   public Estimator getEstimator(double given)
/* 127:    */   {
/* 128:201 */     Estimator result = new KernelEstimator(this.m_Precision);
/* 129:202 */     if (this.m_NumValues == 0) {
/* 130:203 */       return result;
/* 131:    */     }
/* 132:206 */     double delta = 0.0D;double currentProb = 0.0D;
/* 133:208 */     for (int i = 0; i < this.m_NumValues; i++)
/* 134:    */     {
/* 135:209 */       delta = this.m_CondValues[i] - given;
/* 136:210 */       double zLower = (delta - this.m_Precision / 2.0D) / this.m_StandardDev;
/* 137:211 */       double zUpper = (delta + this.m_Precision / 2.0D) / this.m_StandardDev;
/* 138:212 */       currentProb = Statistics.normalProbability(zUpper) - Statistics.normalProbability(zLower);
/* 139:    */       
/* 140:214 */       result.addValue(this.m_Values[i], currentProb * this.m_Weights[i]);
/* 141:    */     }
/* 142:216 */     return result;
/* 143:    */   }
/* 144:    */   
/* 145:    */   public double getProbability(double data, double given)
/* 146:    */   {
/* 147:228 */     return getEstimator(given).getProbability(data);
/* 148:    */   }
/* 149:    */   
/* 150:    */   public String toString()
/* 151:    */   {
/* 152:236 */     String result = "KK Conditional Estimator. " + this.m_NumValues + " Normal Kernels:\n" + "StandardDev = " + Utils.doubleToString(this.m_StandardDev, 4, 2) + "  \nMeans =";
/* 153:240 */     for (int i = 0; i < this.m_NumValues; i++)
/* 154:    */     {
/* 155:241 */       result = result + " (" + this.m_Values[i] + ", " + this.m_CondValues[i] + ")";
/* 156:242 */       if (!this.m_AllWeightsOne) {
/* 157:243 */         result = result + "w=" + this.m_Weights[i];
/* 158:    */       }
/* 159:    */     }
/* 160:246 */     return result;
/* 161:    */   }
/* 162:    */   
/* 163:    */   public String getRevision()
/* 164:    */   {
/* 165:255 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 166:    */   }
/* 167:    */   
/* 168:    */   public static void main(String[] argv)
/* 169:    */   {
/* 170:    */     try
/* 171:    */     {
/* 172:268 */       int seed = 42;
/* 173:269 */       if (argv.length > 0) {
/* 174:270 */         seed = Integer.parseInt(argv[0]);
/* 175:    */       }
/* 176:272 */       KKConditionalEstimator newEst = new KKConditionalEstimator(0.1D);
/* 177:    */       
/* 178:    */ 
/* 179:275 */       Random r = new Random(seed);
/* 180:    */       
/* 181:277 */       int numPoints = 50;
/* 182:278 */       if (argv.length > 2) {
/* 183:279 */         numPoints = Integer.parseInt(argv[2]);
/* 184:    */       }
/* 185:281 */       for (int i = 0; i < numPoints; i++)
/* 186:    */       {
/* 187:282 */         int x = Math.abs(r.nextInt() % 100);
/* 188:283 */         int y = Math.abs(r.nextInt() % 100);
/* 189:284 */         System.out.println("# " + x + "  " + y);
/* 190:285 */         newEst.addValue(x, y, 1.0D);
/* 191:    */       }
/* 192:    */       int cond;
/* 193:    */       int cond;
/* 194:289 */       if (argv.length > 1) {
/* 195:290 */         cond = Integer.parseInt(argv[1]);
/* 196:    */       } else {
/* 197:292 */         cond = Math.abs(r.nextInt() % 100);
/* 198:    */       }
/* 199:294 */       System.out.println("## Conditional = " + cond);
/* 200:295 */       Estimator result = newEst.getEstimator(cond);
/* 201:296 */       for (int i = 0; i <= 100; i += 5) {
/* 202:297 */         System.out.println(" " + i + "  " + result.getProbability(i));
/* 203:    */       }
/* 204:    */     }
/* 205:    */     catch (Exception e)
/* 206:    */     {
/* 207:300 */       System.out.println(e.getMessage());
/* 208:    */     }
/* 209:    */   }
/* 210:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.KKConditionalEstimator
 * JD-Core Version:    0.7.0.1
 */