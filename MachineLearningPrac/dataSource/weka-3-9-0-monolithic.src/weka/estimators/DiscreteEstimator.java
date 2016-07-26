/*   1:    */ package weka.estimators;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Random;
/*   5:    */ import weka.core.Aggregateable;
/*   6:    */ import weka.core.Capabilities;
/*   7:    */ import weka.core.Capabilities.Capability;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ import weka.core.Utils;
/*  10:    */ 
/*  11:    */ public class DiscreteEstimator
/*  12:    */   extends Estimator
/*  13:    */   implements IncrementalEstimator, Aggregateable<DiscreteEstimator>
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = -5526486742612434779L;
/*  16:    */   private final double[] m_Counts;
/*  17:    */   private double m_SumOfCounts;
/*  18:    */   private double m_FPrior;
/*  19:    */   
/*  20:    */   public DiscreteEstimator(int numSymbols, boolean laplace)
/*  21:    */   {
/*  22: 59 */     this.m_Counts = new double[numSymbols];
/*  23: 60 */     this.m_SumOfCounts = 0.0D;
/*  24: 61 */     if (laplace)
/*  25:    */     {
/*  26: 62 */       this.m_FPrior = 1.0D;
/*  27: 63 */       for (int i = 0; i < numSymbols; i++) {
/*  28: 64 */         this.m_Counts[i] = 1.0D;
/*  29:    */       }
/*  30: 66 */       this.m_SumOfCounts = numSymbols;
/*  31:    */     }
/*  32:    */   }
/*  33:    */   
/*  34:    */   public DiscreteEstimator(int nSymbols, double fPrior)
/*  35:    */   {
/*  36: 78 */     this.m_Counts = new double[nSymbols];
/*  37: 79 */     this.m_FPrior = fPrior;
/*  38: 80 */     for (int iSymbol = 0; iSymbol < nSymbols; iSymbol++) {
/*  39: 81 */       this.m_Counts[iSymbol] = fPrior;
/*  40:    */     }
/*  41: 83 */     this.m_SumOfCounts = (fPrior * nSymbols);
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void addValue(double data, double weight)
/*  45:    */   {
/*  46: 95 */     this.m_Counts[((int)data)] += weight;
/*  47: 96 */     this.m_SumOfCounts += weight;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public double getProbability(double data)
/*  51:    */   {
/*  52:108 */     if (this.m_SumOfCounts == 0.0D) {
/*  53:109 */       return 0.0D;
/*  54:    */     }
/*  55:111 */     return this.m_Counts[((int)data)] / this.m_SumOfCounts;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public int getNumSymbols()
/*  59:    */   {
/*  60:121 */     return this.m_Counts == null ? 0 : this.m_Counts.length;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public double getCount(double data)
/*  64:    */   {
/*  65:132 */     if (this.m_SumOfCounts == 0.0D) {
/*  66:133 */       return 0.0D;
/*  67:    */     }
/*  68:135 */     return this.m_Counts[((int)data)];
/*  69:    */   }
/*  70:    */   
/*  71:    */   public double getSumOfCounts()
/*  72:    */   {
/*  73:145 */     return this.m_SumOfCounts;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public String toString()
/*  77:    */   {
/*  78:154 */     StringBuffer result = new StringBuffer("Discrete Estimator. Counts = ");
/*  79:155 */     if (this.m_SumOfCounts > 1.0D)
/*  80:    */     {
/*  81:156 */       for (int i = 0; i < this.m_Counts.length; i++) {
/*  82:157 */         result.append(" ").append(Utils.doubleToString(this.m_Counts[i], 2));
/*  83:    */       }
/*  84:159 */       result.append("  (Total = ").append(Utils.doubleToString(this.m_SumOfCounts, 2));
/*  85:    */       
/*  86:161 */       result.append(")\n");
/*  87:    */     }
/*  88:    */     else
/*  89:    */     {
/*  90:163 */       for (int i = 0; i < this.m_Counts.length; i++) {
/*  91:164 */         result.append(" ").append(this.m_Counts[i]);
/*  92:    */       }
/*  93:166 */       result.append("  (Total = ").append(this.m_SumOfCounts).append(")\n");
/*  94:    */     }
/*  95:168 */     return result.toString();
/*  96:    */   }
/*  97:    */   
/*  98:    */   public Capabilities getCapabilities()
/*  99:    */   {
/* 100:178 */     Capabilities result = super.getCapabilities();
/* 101:179 */     result.disableAll();
/* 102:182 */     if (!this.m_noClass)
/* 103:    */     {
/* 104:183 */       result.enable(Capabilities.Capability.NOMINAL_CLASS);
/* 105:184 */       result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/* 106:    */     }
/* 107:    */     else
/* 108:    */     {
/* 109:186 */       result.enable(Capabilities.Capability.NO_CLASS);
/* 110:    */     }
/* 111:190 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/* 112:191 */     return result;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public String getRevision()
/* 116:    */   {
/* 117:201 */     return RevisionUtils.extract("$Revision: 11247 $");
/* 118:    */   }
/* 119:    */   
/* 120:    */   public DiscreteEstimator aggregate(DiscreteEstimator toAggregate)
/* 121:    */     throws Exception
/* 122:    */   {
/* 123:208 */     if (toAggregate.m_Counts.length != this.m_Counts.length) {
/* 124:209 */       throw new Exception("DiscreteEstimator to aggregate has a different number of symbols");
/* 125:    */     }
/* 126:213 */     this.m_SumOfCounts += toAggregate.m_SumOfCounts;
/* 127:214 */     for (int i = 0; i < this.m_Counts.length; i++) {
/* 128:215 */       this.m_Counts[i] += toAggregate.m_Counts[i] - toAggregate.m_FPrior;
/* 129:    */     }
/* 130:218 */     this.m_SumOfCounts -= toAggregate.m_FPrior * this.m_Counts.length;
/* 131:    */     
/* 132:220 */     return this;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void finalizeAggregation()
/* 136:    */     throws Exception
/* 137:    */   {}
/* 138:    */   
/* 139:    */   protected static void testAggregation()
/* 140:    */   {
/* 141:229 */     DiscreteEstimator df = new DiscreteEstimator(5, true);
/* 142:230 */     DiscreteEstimator one = new DiscreteEstimator(5, true);
/* 143:231 */     DiscreteEstimator two = new DiscreteEstimator(5, true);
/* 144:    */     
/* 145:233 */     Random r = new Random(1L);
/* 146:235 */     for (int i = 0; i < 100; i++)
/* 147:    */     {
/* 148:236 */       int z = r.nextInt(5);
/* 149:237 */       df.addValue(z, 1.0D);
/* 150:239 */       if (i < 50) {
/* 151:240 */         one.addValue(z, 1.0D);
/* 152:    */       } else {
/* 153:242 */         two.addValue(z, 1.0D);
/* 154:    */       }
/* 155:    */     }
/* 156:    */     try
/* 157:    */     {
/* 158:247 */       System.out.println("\n\nFull\n");
/* 159:248 */       System.out.println(df.toString());
/* 160:249 */       System.out.println("Prob (0): " + df.getProbability(0.0D));
/* 161:    */       
/* 162:251 */       System.out.println("\nOne\n" + one.toString());
/* 163:252 */       System.out.println("Prob (0): " + one.getProbability(0.0D));
/* 164:    */       
/* 165:254 */       System.out.println("\nTwo\n" + two.toString());
/* 166:255 */       System.out.println("Prob (0): " + two.getProbability(0.0D));
/* 167:    */       
/* 168:257 */       one = one.aggregate(two);
/* 169:    */       
/* 170:259 */       System.out.println("\nAggregated\n");
/* 171:260 */       System.out.println(one.toString());
/* 172:261 */       System.out.println("Prob (0): " + one.getProbability(0.0D));
/* 173:    */     }
/* 174:    */     catch (Exception ex)
/* 175:    */     {
/* 176:263 */       ex.printStackTrace();
/* 177:    */     }
/* 178:    */   }
/* 179:    */   
/* 180:    */   public static void main(String[] argv)
/* 181:    */   {
/* 182:    */     try
/* 183:    */     {
/* 184:276 */       if (argv.length == 0)
/* 185:    */       {
/* 186:277 */         System.out.println("Please specify a set of instances.");
/* 187:278 */         return;
/* 188:    */       }
/* 189:280 */       int current = Integer.parseInt(argv[0]);
/* 190:281 */       int max = current;
/* 191:282 */       for (int i = 1; i < argv.length; i++)
/* 192:    */       {
/* 193:283 */         current = Integer.parseInt(argv[i]);
/* 194:284 */         if (current > max) {
/* 195:285 */           max = current;
/* 196:    */         }
/* 197:    */       }
/* 198:288 */       DiscreteEstimator newEst = new DiscreteEstimator(max + 1, true);
/* 199:289 */       for (int i = 0; i < argv.length; i++)
/* 200:    */       {
/* 201:290 */         current = Integer.parseInt(argv[i]);
/* 202:291 */         System.out.println(newEst);
/* 203:292 */         System.out.println("Prediction for " + current + " = " + newEst.getProbability(current));
/* 204:    */         
/* 205:294 */         newEst.addValue(current, 1.0D);
/* 206:    */       }
/* 207:297 */       testAggregation();
/* 208:    */     }
/* 209:    */     catch (Exception e)
/* 210:    */     {
/* 211:299 */       System.out.println(e.getMessage());
/* 212:    */     }
/* 213:    */   }
/* 214:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.DiscreteEstimator
 * JD-Core Version:    0.7.0.1
 */