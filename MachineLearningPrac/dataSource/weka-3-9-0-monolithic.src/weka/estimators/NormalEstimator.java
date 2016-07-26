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
/*  12:    */ public class NormalEstimator
/*  13:    */   extends Estimator
/*  14:    */   implements IncrementalEstimator, Aggregateable<NormalEstimator>
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = 93584379632315841L;
/*  17:    */   private double m_SumOfWeights;
/*  18:    */   private double m_SumOfValues;
/*  19:    */   private double m_SumOfValuesSq;
/*  20:    */   private double m_Mean;
/*  21:    */   private double m_StandardDev;
/*  22:    */   private double m_Precision;
/*  23:    */   
/*  24:    */   private double round(double data)
/*  25:    */   {
/*  26: 70 */     return Math.rint(data / this.m_Precision) * this.m_Precision;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public NormalEstimator(double precision)
/*  30:    */   {
/*  31: 86 */     this.m_Precision = precision;
/*  32:    */     
/*  33:    */ 
/*  34: 89 */     this.m_StandardDev = (this.m_Precision / 6.0D);
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void addValue(double data, double weight)
/*  38:    */   {
/*  39:101 */     if (weight == 0.0D) {
/*  40:102 */       return;
/*  41:    */     }
/*  42:104 */     data = round(data);
/*  43:105 */     this.m_SumOfWeights += weight;
/*  44:106 */     this.m_SumOfValues += data * weight;
/*  45:107 */     this.m_SumOfValuesSq += data * data * weight;
/*  46:    */     
/*  47:109 */     computeParameters();
/*  48:    */   }
/*  49:    */   
/*  50:    */   protected void computeParameters()
/*  51:    */   {
/*  52:116 */     if (this.m_SumOfWeights > 0.0D)
/*  53:    */     {
/*  54:117 */       this.m_Mean = (this.m_SumOfValues / this.m_SumOfWeights);
/*  55:118 */       double stdDev = Math.sqrt(Math.abs(this.m_SumOfValuesSq - this.m_Mean * this.m_SumOfValues) / this.m_SumOfWeights);
/*  56:123 */       if (stdDev > 1.0E-010D) {
/*  57:124 */         this.m_StandardDev = Math.max(this.m_Precision / 6.0D, stdDev);
/*  58:    */       }
/*  59:    */     }
/*  60:    */   }
/*  61:    */   
/*  62:    */   public double getProbability(double data)
/*  63:    */   {
/*  64:140 */     data = round(data);
/*  65:141 */     double zLower = (data - this.m_Mean - this.m_Precision / 2.0D) / this.m_StandardDev;
/*  66:142 */     double zUpper = (data - this.m_Mean + this.m_Precision / 2.0D) / this.m_StandardDev;
/*  67:    */     
/*  68:144 */     double pLower = Statistics.normalProbability(zLower);
/*  69:145 */     double pUpper = Statistics.normalProbability(zUpper);
/*  70:146 */     return pUpper - pLower;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public String toString()
/*  74:    */   {
/*  75:155 */     return "Normal Distribution. Mean = " + Utils.doubleToString(this.m_Mean, 4) + " StandardDev = " + Utils.doubleToString(this.m_StandardDev, 4) + " WeightSum = " + Utils.doubleToString(this.m_SumOfWeights, 4) + " Precision = " + this.m_Precision + "\n";
/*  76:    */   }
/*  77:    */   
/*  78:    */   public Capabilities getCapabilities()
/*  79:    */   {
/*  80:168 */     Capabilities result = super.getCapabilities();
/*  81:169 */     result.disableAll();
/*  82:172 */     if (!this.m_noClass)
/*  83:    */     {
/*  84:173 */       result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  85:174 */       result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  86:    */     }
/*  87:    */     else
/*  88:    */     {
/*  89:176 */       result.enable(Capabilities.Capability.NO_CLASS);
/*  90:    */     }
/*  91:180 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  92:181 */     return result;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public double getMean()
/*  96:    */   {
/*  97:190 */     return this.m_Mean;
/*  98:    */   }
/*  99:    */   
/* 100:    */   public double getStdDev()
/* 101:    */   {
/* 102:199 */     return this.m_StandardDev;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public double getPrecision()
/* 106:    */   {
/* 107:208 */     return this.m_Precision;
/* 108:    */   }
/* 109:    */   
/* 110:    */   public double getSumOfWeights()
/* 111:    */   {
/* 112:217 */     return this.m_SumOfWeights;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public String getRevision()
/* 116:    */   {
/* 117:227 */     return RevisionUtils.extract("$Revision: 9785 $");
/* 118:    */   }
/* 119:    */   
/* 120:    */   public NormalEstimator aggregate(NormalEstimator toAggregate)
/* 121:    */     throws Exception
/* 122:    */   {
/* 123:234 */     this.m_SumOfWeights += toAggregate.m_SumOfWeights;
/* 124:235 */     this.m_SumOfValues += toAggregate.m_SumOfValues;
/* 125:236 */     this.m_SumOfValuesSq += toAggregate.m_SumOfValuesSq;
/* 126:238 */     if (toAggregate.m_Precision < this.m_Precision) {
/* 127:239 */       this.m_Precision = toAggregate.m_Precision;
/* 128:    */     }
/* 129:242 */     computeParameters();
/* 130:    */     
/* 131:244 */     return this;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public void finalizeAggregation()
/* 135:    */     throws Exception
/* 136:    */   {}
/* 137:    */   
/* 138:    */   public static void testAggregation()
/* 139:    */   {
/* 140:253 */     NormalEstimator ne = new NormalEstimator(0.01D);
/* 141:254 */     NormalEstimator one = new NormalEstimator(0.01D);
/* 142:255 */     NormalEstimator two = new NormalEstimator(0.01D);
/* 143:    */     
/* 144:257 */     Random r = new Random(1L);
/* 145:259 */     for (int i = 0; i < 100; i++)
/* 146:    */     {
/* 147:260 */       double z = r.nextDouble();
/* 148:    */       
/* 149:262 */       ne.addValue(z, 1.0D);
/* 150:263 */       if (i < 50) {
/* 151:264 */         one.addValue(z, 1.0D);
/* 152:    */       } else {
/* 153:266 */         two.addValue(z, 1.0D);
/* 154:    */       }
/* 155:    */     }
/* 156:    */     try
/* 157:    */     {
/* 158:271 */       System.out.println("\n\nFull\n");
/* 159:272 */       System.out.println(ne.toString());
/* 160:273 */       System.out.println("Prob (0): " + ne.getProbability(0.0D));
/* 161:    */       
/* 162:275 */       System.out.println("\nOne\n" + one.toString());
/* 163:276 */       System.out.println("Prob (0): " + one.getProbability(0.0D));
/* 164:    */       
/* 165:278 */       System.out.println("\nTwo\n" + two.toString());
/* 166:279 */       System.out.println("Prob (0): " + two.getProbability(0.0D));
/* 167:    */       
/* 168:281 */       one = one.aggregate(two);
/* 169:    */       
/* 170:283 */       System.out.println("\nAggregated\n");
/* 171:284 */       System.out.println(one.toString());
/* 172:285 */       System.out.println("Prob (0): " + one.getProbability(0.0D));
/* 173:    */     }
/* 174:    */     catch (Exception ex)
/* 175:    */     {
/* 176:287 */       ex.printStackTrace();
/* 177:    */     }
/* 178:    */   }
/* 179:    */   
/* 180:    */   public static void main(String[] argv)
/* 181:    */   {
/* 182:    */     try
/* 183:    */     {
/* 184:300 */       if (argv.length == 0)
/* 185:    */       {
/* 186:301 */         System.out.println("Please specify a set of instances.");
/* 187:302 */         return;
/* 188:    */       }
/* 189:304 */       NormalEstimator newEst = new NormalEstimator(0.01D);
/* 190:305 */       for (int i = 0; i < argv.length; i++)
/* 191:    */       {
/* 192:306 */         double current = Double.valueOf(argv[i]).doubleValue();
/* 193:307 */         System.out.println(newEst);
/* 194:308 */         System.out.println("Prediction for " + current + " = " + newEst.getProbability(current));
/* 195:    */         
/* 196:310 */         newEst.addValue(current, 1.0D);
/* 197:    */       }
/* 198:313 */       testAggregation();
/* 199:    */     }
/* 200:    */     catch (Exception e)
/* 201:    */     {
/* 202:315 */       System.out.println(e.getMessage());
/* 203:    */     }
/* 204:    */   }
/* 205:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.NormalEstimator
 * JD-Core Version:    0.7.0.1
 */