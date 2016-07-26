/*   1:    */ package weka.estimators;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Random;
/*   5:    */ import weka.core.Aggregateable;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ import weka.core.Statistics;
/*  10:    */ import weka.core.Utils;
/*  11:    */ 
/*  12:    */ public class KernelEstimator
/*  13:    */   extends Estimator
/*  14:    */   implements IncrementalEstimator, Aggregateable<KernelEstimator>
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = 3646923563367683925L;
/*  17:    */   private double[] m_Values;
/*  18:    */   private double[] m_Weights;
/*  19:    */   private int m_NumValues;
/*  20:    */   private double m_SumOfWeights;
/*  21:    */   private double m_StandardDev;
/*  22:    */   private double m_Precision;
/*  23:    */   private boolean m_AllWeightsOne;
/*  24: 66 */   private static double MAX_ERROR = 0.01D;
/*  25:    */   
/*  26:    */   private int findNearestValue(double key)
/*  27:    */   {
/*  28: 76 */     int low = 0;
/*  29: 77 */     int high = this.m_NumValues;
/*  30: 78 */     int middle = 0;
/*  31: 79 */     while (low < high)
/*  32:    */     {
/*  33: 80 */       middle = (low + high) / 2;
/*  34: 81 */       double current = this.m_Values[middle];
/*  35: 82 */       if (current == key) {
/*  36: 83 */         return middle;
/*  37:    */       }
/*  38: 85 */       if (current > key) {
/*  39: 86 */         high = middle;
/*  40: 87 */       } else if (current < key) {
/*  41: 88 */         low = middle + 1;
/*  42:    */       }
/*  43:    */     }
/*  44: 91 */     return low;
/*  45:    */   }
/*  46:    */   
/*  47:    */   private double round(double data)
/*  48:    */   {
/*  49:102 */     return Math.rint(data / this.m_Precision) * this.m_Precision;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public KernelEstimator(double precision)
/*  53:    */   {
/*  54:118 */     this.m_Values = new double[50];
/*  55:119 */     this.m_Weights = new double[50];
/*  56:120 */     this.m_NumValues = 0;
/*  57:121 */     this.m_SumOfWeights = 0.0D;
/*  58:122 */     this.m_AllWeightsOne = true;
/*  59:123 */     this.m_Precision = precision;
/*  60:125 */     if (this.m_Precision < Utils.SMALL) {
/*  61:126 */       this.m_Precision = Utils.SMALL;
/*  62:    */     }
/*  63:129 */     this.m_StandardDev = (this.m_Precision / 6.0D);
/*  64:    */   }
/*  65:    */   
/*  66:    */   public void addValue(double data, double weight)
/*  67:    */   {
/*  68:141 */     if (weight == 0.0D) {
/*  69:142 */       return;
/*  70:    */     }
/*  71:144 */     data = round(data);
/*  72:145 */     int insertIndex = findNearestValue(data);
/*  73:146 */     if ((this.m_NumValues <= insertIndex) || (this.m_Values[insertIndex] != data))
/*  74:    */     {
/*  75:147 */       if (this.m_NumValues < this.m_Values.length)
/*  76:    */       {
/*  77:148 */         int left = this.m_NumValues - insertIndex;
/*  78:149 */         System.arraycopy(this.m_Values, insertIndex, this.m_Values, insertIndex + 1, left);
/*  79:    */         
/*  80:151 */         System.arraycopy(this.m_Weights, insertIndex, this.m_Weights, insertIndex + 1, left);
/*  81:    */         
/*  82:    */ 
/*  83:154 */         this.m_Values[insertIndex] = data;
/*  84:155 */         this.m_Weights[insertIndex] = weight;
/*  85:156 */         this.m_NumValues += 1;
/*  86:    */       }
/*  87:    */       else
/*  88:    */       {
/*  89:158 */         double[] newValues = new double[this.m_Values.length * 2];
/*  90:159 */         double[] newWeights = new double[this.m_Values.length * 2];
/*  91:160 */         int left = this.m_NumValues - insertIndex;
/*  92:161 */         System.arraycopy(this.m_Values, 0, newValues, 0, insertIndex);
/*  93:162 */         System.arraycopy(this.m_Weights, 0, newWeights, 0, insertIndex);
/*  94:163 */         newValues[insertIndex] = data;
/*  95:164 */         newWeights[insertIndex] = weight;
/*  96:165 */         System.arraycopy(this.m_Values, insertIndex, newValues, insertIndex + 1, left);
/*  97:    */         
/*  98:167 */         System.arraycopy(this.m_Weights, insertIndex, newWeights, insertIndex + 1, left);
/*  99:    */         
/* 100:169 */         this.m_NumValues += 1;
/* 101:170 */         this.m_Values = newValues;
/* 102:171 */         this.m_Weights = newWeights;
/* 103:    */       }
/* 104:173 */       if (weight != 1.0D) {
/* 105:174 */         this.m_AllWeightsOne = false;
/* 106:    */       }
/* 107:    */     }
/* 108:    */     else
/* 109:    */     {
/* 110:177 */       this.m_Weights[insertIndex] += weight;
/* 111:178 */       this.m_AllWeightsOne = false;
/* 112:    */     }
/* 113:180 */     this.m_SumOfWeights += weight;
/* 114:181 */     double range = this.m_Values[(this.m_NumValues - 1)] - this.m_Values[0];
/* 115:182 */     if (range > 0.0D) {
/* 116:183 */       this.m_StandardDev = Math.max(range / Math.sqrt(this.m_SumOfWeights), this.m_Precision / 6.0D);
/* 117:    */     }
/* 118:    */   }
/* 119:    */   
/* 120:    */   public double getProbability(double data)
/* 121:    */   {
/* 122:198 */     double delta = 0.0D;double sum = 0.0D;double currentProb = 0.0D;
/* 123:199 */     double zLower = 0.0D;double zUpper = 0.0D;
/* 124:200 */     if (this.m_NumValues == 0)
/* 125:    */     {
/* 126:201 */       zLower = (data - this.m_Precision / 2.0D) / this.m_StandardDev;
/* 127:202 */       zUpper = (data + this.m_Precision / 2.0D) / this.m_StandardDev;
/* 128:203 */       return Statistics.normalProbability(zUpper) - Statistics.normalProbability(zLower);
/* 129:    */     }
/* 130:206 */     double weightSum = 0.0D;
/* 131:207 */     int start = findNearestValue(data);
/* 132:208 */     for (int i = start; i < this.m_NumValues; i++)
/* 133:    */     {
/* 134:209 */       delta = this.m_Values[i] - data;
/* 135:210 */       zLower = (delta - this.m_Precision / 2.0D) / this.m_StandardDev;
/* 136:211 */       zUpper = (delta + this.m_Precision / 2.0D) / this.m_StandardDev;
/* 137:212 */       currentProb = Statistics.normalProbability(zUpper) - Statistics.normalProbability(zLower);
/* 138:    */       
/* 139:214 */       sum += currentProb * this.m_Weights[i];
/* 140:    */       
/* 141:    */ 
/* 142:    */ 
/* 143:    */ 
/* 144:    */ 
/* 145:    */ 
/* 146:221 */       weightSum += this.m_Weights[i];
/* 147:222 */       if (currentProb * (this.m_SumOfWeights - weightSum) < sum * MAX_ERROR) {
/* 148:    */         break;
/* 149:    */       }
/* 150:    */     }
/* 151:226 */     for (int i = start - 1; i >= 0; i--)
/* 152:    */     {
/* 153:227 */       delta = this.m_Values[i] - data;
/* 154:228 */       zLower = (delta - this.m_Precision / 2.0D) / this.m_StandardDev;
/* 155:229 */       zUpper = (delta + this.m_Precision / 2.0D) / this.m_StandardDev;
/* 156:230 */       currentProb = Statistics.normalProbability(zUpper) - Statistics.normalProbability(zLower);
/* 157:    */       
/* 158:232 */       sum += currentProb * this.m_Weights[i];
/* 159:233 */       weightSum += this.m_Weights[i];
/* 160:234 */       if (currentProb * (this.m_SumOfWeights - weightSum) < sum * MAX_ERROR) {
/* 161:    */         break;
/* 162:    */       }
/* 163:    */     }
/* 164:238 */     return sum / this.m_SumOfWeights;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public String toString()
/* 168:    */   {
/* 169:245 */     String result = this.m_NumValues + " Normal Kernels. \nStandardDev = " + Utils.doubleToString(this.m_StandardDev, 6, 4) + " Precision = " + this.m_Precision;
/* 170:248 */     if (this.m_NumValues == 0)
/* 171:    */     {
/* 172:249 */       result = result + "  \nMean = 0";
/* 173:    */     }
/* 174:    */     else
/* 175:    */     {
/* 176:251 */       result = result + "  \nMeans =";
/* 177:252 */       for (int i = 0; i < this.m_NumValues; i++) {
/* 178:253 */         result = result + " " + this.m_Values[i];
/* 179:    */       }
/* 180:255 */       if (!this.m_AllWeightsOne)
/* 181:    */       {
/* 182:256 */         result = result + "\nWeights = ";
/* 183:257 */         for (int i = 0; i < this.m_NumValues; i++) {
/* 184:258 */           result = result + " " + this.m_Weights[i];
/* 185:    */         }
/* 186:    */       }
/* 187:    */     }
/* 188:262 */     return result + "\n";
/* 189:    */   }
/* 190:    */   
/* 191:    */   public int getNumKernels()
/* 192:    */   {
/* 193:271 */     return this.m_NumValues;
/* 194:    */   }
/* 195:    */   
/* 196:    */   public double[] getMeans()
/* 197:    */   {
/* 198:280 */     return this.m_Values;
/* 199:    */   }
/* 200:    */   
/* 201:    */   public double[] getWeights()
/* 202:    */   {
/* 203:289 */     return this.m_Weights;
/* 204:    */   }
/* 205:    */   
/* 206:    */   public double getPrecision()
/* 207:    */   {
/* 208:298 */     return this.m_Precision;
/* 209:    */   }
/* 210:    */   
/* 211:    */   public double getStdDev()
/* 212:    */   {
/* 213:307 */     return this.m_StandardDev;
/* 214:    */   }
/* 215:    */   
/* 216:    */   public Capabilities getCapabilities()
/* 217:    */   {
/* 218:317 */     Capabilities result = super.getCapabilities();
/* 219:318 */     result.disableAll();
/* 220:320 */     if (!this.m_noClass)
/* 221:    */     {
/* 222:321 */       result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 223:322 */       result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 224:    */     }
/* 225:    */     else
/* 226:    */     {
/* 227:324 */       result.enable(Capabilities.Capability.NO_CLASS);
/* 228:    */     }
/* 229:328 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 230:329 */     return result;
/* 231:    */   }
/* 232:    */   
/* 233:    */   public String getRevision()
/* 234:    */   {
/* 235:339 */     return RevisionUtils.extract("$Revision: 9785 $");
/* 236:    */   }
/* 237:    */   
/* 238:    */   public KernelEstimator aggregate(KernelEstimator toAggregate)
/* 239:    */     throws Exception
/* 240:    */   {
/* 241:346 */     for (int i = 0; i < toAggregate.m_NumValues; i++) {
/* 242:347 */       addValue(toAggregate.m_Values[i], toAggregate.m_Weights[i]);
/* 243:    */     }
/* 244:350 */     return this;
/* 245:    */   }
/* 246:    */   
/* 247:    */   public void finalizeAggregation()
/* 248:    */     throws Exception
/* 249:    */   {}
/* 250:    */   
/* 251:    */   public static void testAggregation()
/* 252:    */   {
/* 253:359 */     KernelEstimator ke = new KernelEstimator(0.01D);
/* 254:360 */     KernelEstimator one = new KernelEstimator(0.01D);
/* 255:361 */     KernelEstimator two = new KernelEstimator(0.01D);
/* 256:    */     
/* 257:363 */     Random r = new Random(1L);
/* 258:365 */     for (int i = 0; i < 100; i++)
/* 259:    */     {
/* 260:366 */       double z = r.nextDouble();
/* 261:    */       
/* 262:368 */       ke.addValue(z, 1.0D);
/* 263:369 */       if (i < 50) {
/* 264:370 */         one.addValue(z, 1.0D);
/* 265:    */       } else {
/* 266:372 */         two.addValue(z, 1.0D);
/* 267:    */       }
/* 268:    */     }
/* 269:    */     try
/* 270:    */     {
/* 271:378 */       System.out.println("\n\nFull\n");
/* 272:379 */       System.out.println(ke.toString());
/* 273:380 */       System.out.println("Prob (0): " + ke.getProbability(0.0D));
/* 274:    */       
/* 275:382 */       System.out.println("\nOne\n" + one.toString());
/* 276:383 */       System.out.println("Prob (0): " + one.getProbability(0.0D));
/* 277:    */       
/* 278:385 */       System.out.println("\nTwo\n" + two.toString());
/* 279:386 */       System.out.println("Prob (0): " + two.getProbability(0.0D));
/* 280:    */       
/* 281:388 */       one = one.aggregate(two);
/* 282:    */       
/* 283:390 */       System.out.println("Aggregated\n");
/* 284:391 */       System.out.println(one.toString());
/* 285:392 */       System.out.println("Prob (0): " + one.getProbability(0.0D));
/* 286:    */     }
/* 287:    */     catch (Exception ex)
/* 288:    */     {
/* 289:394 */       ex.printStackTrace();
/* 290:    */     }
/* 291:    */   }
/* 292:    */   
/* 293:    */   public static void main(String[] argv)
/* 294:    */   {
/* 295:    */     try
/* 296:    */     {
/* 297:406 */       if (argv.length < 2)
/* 298:    */       {
/* 299:407 */         System.out.println("Please specify a set of instances.");
/* 300:408 */         return;
/* 301:    */       }
/* 302:410 */       KernelEstimator newEst = new KernelEstimator(0.01D);
/* 303:411 */       for (int i = 0; i < argv.length - 3; i += 2) {
/* 304:412 */         newEst.addValue(Double.valueOf(argv[i]).doubleValue(), Double.valueOf(argv[(i + 1)]).doubleValue());
/* 305:    */       }
/* 306:415 */       System.out.println(newEst);
/* 307:    */       
/* 308:417 */       double start = Double.valueOf(argv[(argv.length - 2)]).doubleValue();
/* 309:418 */       double finish = Double.valueOf(argv[(argv.length - 1)]).doubleValue();
/* 310:419 */       for (double current = start; current < finish; current += (finish - start) / 50.0D) {
/* 311:420 */         System.out.println("Data: " + current + " " + newEst.getProbability(current));
/* 312:    */       }
/* 313:424 */       testAggregation();
/* 314:    */     }
/* 315:    */     catch (Exception e)
/* 316:    */     {
/* 317:426 */       System.out.println(e.getMessage());
/* 318:    */     }
/* 319:    */   }
/* 320:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.KernelEstimator
 * JD-Core Version:    0.7.0.1
 */