/*   1:    */ package weka.estimators;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Random;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ import weka.core.Utils;
/*   8:    */ import weka.core.matrix.Matrix;
/*   9:    */ 
/*  10:    */ public class NNConditionalEstimator
/*  11:    */   implements ConditionalEstimator
/*  12:    */ {
/*  13: 41 */   private final Vector<Double> m_Values = new Vector();
/*  14: 44 */   private final Vector<Double> m_CondValues = new Vector();
/*  15: 47 */   private final Vector<Double> m_Weights = new Vector();
/*  16:    */   private double m_SumOfWeights;
/*  17:    */   private double m_CondMean;
/*  18:    */   private double m_ValueMean;
/*  19:    */   private Matrix m_Covariance;
/*  20:    */   
/*  21:    */   private int findNearestPair(double key, double secondaryKey)
/*  22:    */   {
/*  23: 74 */     int low = 0;
/*  24: 75 */     int high = this.m_CondValues.size();
/*  25: 76 */     int middle = 0;
/*  26: 77 */     while (low < high)
/*  27:    */     {
/*  28: 78 */       middle = (low + high) / 2;
/*  29: 79 */       double current = ((Double)this.m_CondValues.elementAt(middle)).doubleValue();
/*  30: 80 */       if (current == key)
/*  31:    */       {
/*  32: 81 */         double secondary = ((Double)this.m_Values.elementAt(middle)).doubleValue();
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
/*  51:    */   private void calculateCovariance()
/*  52:    */   {
/*  53:103 */     double sumValues = 0.0D;double sumConds = 0.0D;
/*  54:104 */     for (int i = 0; i < this.m_Values.size(); i++)
/*  55:    */     {
/*  56:105 */       sumValues += ((Double)this.m_Values.elementAt(i)).doubleValue() * ((Double)this.m_Weights.elementAt(i)).doubleValue();
/*  57:    */       
/*  58:107 */       sumConds += ((Double)this.m_CondValues.elementAt(i)).doubleValue() * ((Double)this.m_Weights.elementAt(i)).doubleValue();
/*  59:    */     }
/*  60:110 */     this.m_ValueMean = (sumValues / this.m_SumOfWeights);
/*  61:111 */     this.m_CondMean = (sumConds / this.m_SumOfWeights);
/*  62:112 */     double c00 = 0.0D;double c01 = 0.0D;double c10 = 0.0D;double c11 = 0.0D;
/*  63:113 */     for (int i = 0; i < this.m_Values.size(); i++)
/*  64:    */     {
/*  65:114 */       double x = ((Double)this.m_Values.elementAt(i)).doubleValue();
/*  66:115 */       double y = ((Double)this.m_CondValues.elementAt(i)).doubleValue();
/*  67:116 */       double weight = ((Double)this.m_Weights.elementAt(i)).doubleValue();
/*  68:117 */       c00 += (x - this.m_ValueMean) * (x - this.m_ValueMean) * weight;
/*  69:118 */       c01 += (x - this.m_ValueMean) * (y - this.m_CondMean) * weight;
/*  70:119 */       c11 += (y - this.m_CondMean) * (y - this.m_CondMean) * weight;
/*  71:    */     }
/*  72:121 */     c00 /= (this.m_SumOfWeights - 1.0D);
/*  73:122 */     c01 /= (this.m_SumOfWeights - 1.0D);
/*  74:123 */     c10 = c01;
/*  75:124 */     c11 /= (this.m_SumOfWeights - 1.0D);
/*  76:125 */     this.m_Covariance = new Matrix(2, 2);
/*  77:126 */     this.m_Covariance.set(0, 0, c00);
/*  78:127 */     this.m_Covariance.set(0, 1, c01);
/*  79:128 */     this.m_Covariance.set(1, 0, c10);
/*  80:129 */     this.m_Covariance.set(1, 1, c11);
/*  81:    */   }
/*  82:    */   
/*  83:    */   public void addValue(double data, double given, double weight)
/*  84:    */   {
/*  85:142 */     int insertIndex = findNearestPair(given, data);
/*  86:143 */     if ((this.m_Values.size() <= insertIndex) || (((Double)this.m_CondValues.elementAt(insertIndex)).doubleValue() != given) || (((Double)this.m_Values.elementAt(insertIndex)).doubleValue() != data))
/*  87:    */     {
/*  88:146 */       this.m_CondValues.insertElementAt(new Double(given), insertIndex);
/*  89:147 */       this.m_Values.insertElementAt(new Double(data), insertIndex);
/*  90:148 */       this.m_Weights.insertElementAt(new Double(weight), insertIndex);
/*  91:149 */       if (weight == 1.0D) {}
/*  92:    */     }
/*  93:    */     else
/*  94:    */     {
/*  95:152 */       double newWeight = ((Double)this.m_Weights.elementAt(insertIndex)).doubleValue();
/*  96:153 */       newWeight += weight;
/*  97:154 */       this.m_Weights.setElementAt(new Double(newWeight), insertIndex);
/*  98:    */     }
/*  99:156 */     this.m_SumOfWeights += weight;
/* 100:    */     
/* 101:158 */     this.m_Covariance = null;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public Estimator getEstimator(double given)
/* 105:    */   {
/* 106:170 */     if (this.m_Covariance == null) {
/* 107:171 */       calculateCovariance();
/* 108:    */     }
/* 109:173 */     Estimator result = new MahalanobisEstimator(this.m_Covariance, given - this.m_CondMean, this.m_ValueMean);
/* 110:    */     
/* 111:175 */     return result;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public double getProbability(double data, double given)
/* 115:    */   {
/* 116:188 */     return getEstimator(given).getProbability(data);
/* 117:    */   }
/* 118:    */   
/* 119:    */   public String toString()
/* 120:    */   {
/* 121:195 */     if (this.m_Covariance == null) {
/* 122:196 */       calculateCovariance();
/* 123:    */     }
/* 124:198 */     String result = "NN Conditional Estimator. " + this.m_CondValues.size() + " data points.  Mean = " + Utils.doubleToString(this.m_ValueMean, 4, 2) + "  Conditional mean = " + Utils.doubleToString(this.m_CondMean, 4, 2);
/* 125:    */     
/* 126:    */ 
/* 127:201 */     result = result + "  Covariance Matrix: \n" + this.m_Covariance;
/* 128:202 */     return result;
/* 129:    */   }
/* 130:    */   
/* 131:    */   public String getRevision()
/* 132:    */   {
/* 133:212 */     return RevisionUtils.extract("$Revision: 10153 $");
/* 134:    */   }
/* 135:    */   
/* 136:    */   public static void main(String[] argv)
/* 137:    */   {
/* 138:    */     try
/* 139:    */     {
/* 140:223 */       int seed = 42;
/* 141:224 */       if (argv.length > 0) {
/* 142:225 */         seed = Integer.parseInt(argv[0]);
/* 143:    */       }
/* 144:227 */       NNConditionalEstimator newEst = new NNConditionalEstimator();
/* 145:    */       
/* 146:    */ 
/* 147:230 */       Random r = new Random(seed);
/* 148:    */       
/* 149:232 */       int numPoints = 50;
/* 150:233 */       if (argv.length > 2) {
/* 151:234 */         numPoints = Integer.parseInt(argv[2]);
/* 152:    */       }
/* 153:236 */       for (int i = 0; i < numPoints; i++)
/* 154:    */       {
/* 155:237 */         int x = Math.abs(r.nextInt() % 100);
/* 156:238 */         int y = Math.abs(r.nextInt() % 100);
/* 157:239 */         System.out.println("# " + x + "  " + y);
/* 158:240 */         newEst.addValue(x, y, 1.0D);
/* 159:    */       }
/* 160:    */       int cond;
/* 161:    */       int cond;
/* 162:244 */       if (argv.length > 1) {
/* 163:245 */         cond = Integer.parseInt(argv[1]);
/* 164:    */       } else {
/* 165:247 */         cond = Math.abs(r.nextInt() % 100);
/* 166:    */       }
/* 167:249 */       System.out.println("## Conditional = " + cond);
/* 168:250 */       Estimator result = newEst.getEstimator(cond);
/* 169:251 */       for (int i = 0; i <= 100; i += 5) {
/* 170:252 */         System.out.println(" " + i + "  " + result.getProbability(i));
/* 171:    */       }
/* 172:    */     }
/* 173:    */     catch (Exception e)
/* 174:    */     {
/* 175:255 */       System.out.println(e.getMessage());
/* 176:    */     }
/* 177:    */   }
/* 178:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.NNConditionalEstimator
 * JD-Core Version:    0.7.0.1
 */