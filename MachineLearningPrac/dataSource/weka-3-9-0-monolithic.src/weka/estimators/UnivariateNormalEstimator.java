/*   1:    */ package weka.estimators;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.io.Serializable;
/*   5:    */ import java.util.Random;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ import weka.core.Statistics;
/*   8:    */ 
/*   9:    */ public class UnivariateNormalEstimator
/*  10:    */   implements UnivariateDensityEstimator, UnivariateIntervalEstimator, UnivariateQuantileEstimator, Serializable
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = -1669009817825826548L;
/*  13: 45 */   protected double m_WeightedSum = 0.0D;
/*  14: 48 */   protected double m_WeightedSumSquared = 0.0D;
/*  15: 51 */   protected double m_SumOfWeights = 0.0D;
/*  16: 54 */   protected double m_Mean = 0.0D;
/*  17: 57 */   protected double m_Variance = 1.7976931348623157E+308D;
/*  18: 60 */   protected double m_MinVar = 1.0E-012D;
/*  19: 63 */   public static final double CONST = Math.log(6.283185307179586D);
/*  20:    */   
/*  21:    */   public String globalInfo()
/*  22:    */   {
/*  23: 69 */     return "Estimates a univariate normal density.";
/*  24:    */   }
/*  25:    */   
/*  26:    */   public void addValue(double value, double weight)
/*  27:    */   {
/*  28: 79 */     this.m_WeightedSum += value * weight;
/*  29: 80 */     this.m_WeightedSumSquared += value * value * weight;
/*  30: 81 */     this.m_SumOfWeights += weight;
/*  31:    */   }
/*  32:    */   
/*  33:    */   protected void updateMeanAndVariance()
/*  34:    */   {
/*  35: 93 */     this.m_Mean = 0.0D;
/*  36: 94 */     if (this.m_SumOfWeights > 0.0D) {
/*  37: 95 */       this.m_Mean = (this.m_WeightedSum / this.m_SumOfWeights);
/*  38:    */     }
/*  39: 99 */     this.m_Variance = 1.7976931348623157E+308D;
/*  40:100 */     if (this.m_SumOfWeights > 0.0D) {
/*  41:101 */       this.m_Variance = (this.m_WeightedSumSquared / this.m_SumOfWeights - this.m_Mean * this.m_Mean);
/*  42:    */     }
/*  43:105 */     if (this.m_Variance <= this.m_MinVar) {
/*  44:106 */       this.m_Variance = this.m_MinVar;
/*  45:    */     }
/*  46:    */   }
/*  47:    */   
/*  48:    */   public double[][] predictIntervals(double conf)
/*  49:    */   {
/*  50:118 */     updateMeanAndVariance();
/*  51:    */     
/*  52:120 */     double val = Statistics.normalInverse(1.0D - (1.0D - conf) / 2.0D);
/*  53:    */     
/*  54:122 */     double[][] arr = new double[1][2];
/*  55:123 */     arr[0][1] = (this.m_Mean + val * Math.sqrt(this.m_Variance));
/*  56:124 */     arr[0][0] = (this.m_Mean - val * Math.sqrt(this.m_Variance));
/*  57:    */     
/*  58:126 */     return arr;
/*  59:    */   }
/*  60:    */   
/*  61:    */   public double predictQuantile(double percentage)
/*  62:    */   {
/*  63:137 */     updateMeanAndVariance();
/*  64:    */     
/*  65:139 */     return this.m_Mean + Statistics.normalInverse(percentage) * Math.sqrt(this.m_Variance);
/*  66:    */   }
/*  67:    */   
/*  68:    */   public double logDensity(double value)
/*  69:    */   {
/*  70:152 */     updateMeanAndVariance();
/*  71:    */     
/*  72:    */ 
/*  73:155 */     double val = -0.5D * (CONST + Math.log(this.m_Variance) + (value - this.m_Mean) * (value - this.m_Mean) / this.m_Variance);
/*  74:    */     
/*  75:    */ 
/*  76:158 */     return val;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public String toString()
/*  80:    */   {
/*  81:166 */     updateMeanAndVariance();
/*  82:    */     
/*  83:168 */     return "Mean: " + this.m_Mean + "\t" + "Variance: " + this.m_Variance;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public String getRevision()
/*  87:    */   {
/*  88:178 */     return RevisionUtils.extract("$Revision: 11318 $");
/*  89:    */   }
/*  90:    */   
/*  91:    */   public static void main(String[] args)
/*  92:    */   {
/*  93:187 */     Random r = new Random();
/*  94:    */     
/*  95:    */ 
/*  96:190 */     UnivariateNormalEstimator e = new UnivariateNormalEstimator();
/*  97:    */     
/*  98:    */ 
/*  99:193 */     System.out.println(e);
/* 100:    */     
/* 101:    */ 
/* 102:196 */     double sum = 0.0D;
/* 103:197 */     for (int i = 0; i < 100000; i++) {
/* 104:198 */       sum += Math.exp(e.logDensity(r.nextDouble() * 10.0D - 5.0D));
/* 105:    */     }
/* 106:200 */     System.out.println("Approximate integral: " + 10.0D * sum / 100000.0D);
/* 107:203 */     for (int i = 0; i < 100000; i++)
/* 108:    */     {
/* 109:204 */       e.addValue(r.nextGaussian(), 1.0D);
/* 110:205 */       e.addValue(r.nextGaussian() * 2.0D, 3.0D);
/* 111:    */     }
/* 112:209 */     System.out.println(e);
/* 113:    */     
/* 114:    */ 
/* 115:212 */     sum = 0.0D;
/* 116:213 */     for (int i = 0; i < 100000; i++) {
/* 117:214 */       sum += Math.exp(e.logDensity(r.nextDouble() * 10.0D - 5.0D));
/* 118:    */     }
/* 119:216 */     System.out.println("Approximate integral: " + 10.0D * sum / 100000.0D);
/* 120:    */     
/* 121:    */ 
/* 122:219 */     e = new UnivariateNormalEstimator();
/* 123:222 */     for (int i = 0; i < 100000; i++)
/* 124:    */     {
/* 125:223 */       e.addValue(r.nextGaussian(), 1.0D);
/* 126:224 */       e.addValue(r.nextGaussian() * 2.0D, 1.0D);
/* 127:225 */       e.addValue(r.nextGaussian() * 2.0D, 1.0D);
/* 128:226 */       e.addValue(r.nextGaussian() * 2.0D, 1.0D);
/* 129:    */     }
/* 130:230 */     System.out.println(e);
/* 131:    */     
/* 132:    */ 
/* 133:233 */     sum = 0.0D;
/* 134:234 */     for (int i = 0; i < 100000; i++) {
/* 135:235 */       sum += Math.exp(e.logDensity(r.nextDouble() * 10.0D - 5.0D));
/* 136:    */     }
/* 137:237 */     System.out.println("Approximate integral: " + 10.0D * sum / 100000.0D);
/* 138:    */     
/* 139:    */ 
/* 140:240 */     e = new UnivariateNormalEstimator();
/* 141:243 */     for (int i = 0; i < 100000; i++) {
/* 142:244 */       e.addValue(r.nextGaussian() * 5.0D + 3.0D, 1.0D);
/* 143:    */     }
/* 144:248 */     System.out.println(e);
/* 145:    */     
/* 146:    */ 
/* 147:251 */     double[][] intervals = e.predictIntervals(0.95D);
/* 148:252 */     System.out.println("Lower: " + intervals[0][0] + " Upper: " + intervals[0][1]);
/* 149:253 */     double covered = 0.0D;
/* 150:254 */     for (int i = 0; i < 100000; i++)
/* 151:    */     {
/* 152:255 */       double val = r.nextGaussian() * 5.0D + 3.0D;
/* 153:256 */       if ((val >= intervals[0][0]) && (val <= intervals[0][1])) {
/* 154:257 */         covered += 1.0D;
/* 155:    */       }
/* 156:    */     }
/* 157:260 */     System.out.println("Coverage: " + covered / 100000.0D);
/* 158:    */     
/* 159:262 */     intervals = e.predictIntervals(0.8D);
/* 160:263 */     System.out.println("Lower: " + intervals[0][0] + " Upper: " + intervals[0][1]);
/* 161:264 */     covered = 0.0D;
/* 162:265 */     for (int i = 0; i < 100000; i++)
/* 163:    */     {
/* 164:266 */       double val = r.nextGaussian() * 5.0D + 3.0D;
/* 165:267 */       if ((val >= intervals[0][0]) && (val <= intervals[0][1])) {
/* 166:268 */         covered += 1.0D;
/* 167:    */       }
/* 168:    */     }
/* 169:271 */     System.out.println("Coverage: " + covered / 100000.0D);
/* 170:    */   }
/* 171:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.UnivariateNormalEstimator
 * JD-Core Version:    0.7.0.1
 */