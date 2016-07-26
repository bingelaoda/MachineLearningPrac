/*   1:    */ package weka.estimators;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import weka.core.Capabilities;
/*   5:    */ import weka.core.Capabilities.Capability;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ import weka.core.Utils;
/*   8:    */ import weka.core.matrix.Matrix;
/*   9:    */ 
/*  10:    */ public class MahalanobisEstimator
/*  11:    */   extends Estimator
/*  12:    */   implements IncrementalEstimator
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = 8950225468990043868L;
/*  15:    */   private Matrix m_CovarianceInverse;
/*  16:    */   private double m_Determinant;
/*  17:    */   private double m_ConstDelta;
/*  18:    */   private double m_ValueMean;
/*  19: 57 */   private static double TWO_PI = 6.283185307179586D;
/*  20:    */   
/*  21:    */   private double normalKernel(double x)
/*  22:    */   {
/*  23: 68 */     Matrix thisPoint = new Matrix(1, 2);
/*  24: 69 */     thisPoint.set(0, 0, x);
/*  25: 70 */     thisPoint.set(0, 1, this.m_ConstDelta);
/*  26: 71 */     return Math.exp(-thisPoint.times(this.m_CovarianceInverse).times(thisPoint.transpose()).get(0, 0) / 2.0D) / (Math.sqrt(TWO_PI) * this.m_Determinant);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public MahalanobisEstimator(Matrix covariance, double constDelta, double valueMean)
/*  30:    */   {
/*  31: 86 */     this.m_CovarianceInverse = null;
/*  32: 87 */     if ((covariance.getRowDimension() == 2) && (covariance.getColumnDimension() == 2))
/*  33:    */     {
/*  34: 88 */       double a = covariance.get(0, 0);
/*  35: 89 */       double b = covariance.get(0, 1);
/*  36: 90 */       double c = covariance.get(1, 0);
/*  37: 91 */       double d = covariance.get(1, 1);
/*  38: 92 */       if (a == 0.0D)
/*  39:    */       {
/*  40: 93 */         a = c;c = 0.0D;
/*  41: 94 */         double temp = b;
/*  42: 95 */         b = d;d = temp;
/*  43:    */       }
/*  44: 97 */       if (a == 0.0D) {
/*  45: 98 */         return;
/*  46:    */       }
/*  47:100 */       double denom = d - c * b / a;
/*  48:101 */       if (denom == 0.0D) {
/*  49:102 */         return;
/*  50:    */       }
/*  51:104 */       this.m_Determinant = (covariance.get(0, 0) * covariance.get(1, 1) - covariance.get(1, 0) * covariance.get(0, 1));
/*  52:    */       
/*  53:106 */       this.m_CovarianceInverse = new Matrix(2, 2);
/*  54:107 */       this.m_CovarianceInverse.set(0, 0, 1.0D / a + b * c / a / a / denom);
/*  55:108 */       this.m_CovarianceInverse.set(0, 1, -b / a / denom);
/*  56:109 */       this.m_CovarianceInverse.set(1, 0, -c / a / denom);
/*  57:110 */       this.m_CovarianceInverse.set(1, 1, 1.0D / denom);
/*  58:111 */       this.m_ConstDelta = constDelta;
/*  59:112 */       this.m_ValueMean = valueMean;
/*  60:    */     }
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void addValue(double data, double weight) {}
/*  64:    */   
/*  65:    */   public double getProbability(double data)
/*  66:    */   {
/*  67:135 */     double delta = data - this.m_ValueMean;
/*  68:136 */     if (this.m_CovarianceInverse == null) {
/*  69:137 */       return 0.0D;
/*  70:    */     }
/*  71:139 */     return normalKernel(delta);
/*  72:    */   }
/*  73:    */   
/*  74:    */   public String toString()
/*  75:    */   {
/*  76:145 */     if (this.m_CovarianceInverse == null) {
/*  77:146 */       return "No covariance inverse\n";
/*  78:    */     }
/*  79:148 */     return "Mahalanovis Distribution. Mean = " + Utils.doubleToString(this.m_ValueMean, 4, 2) + "  ConditionalOffset = " + Utils.doubleToString(this.m_ConstDelta, 4, 2) + "\n" + "Covariance Matrix: Determinant = " + this.m_Determinant + "  Inverse:\n" + this.m_CovarianceInverse;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public Capabilities getCapabilities()
/*  83:    */   {
/*  84:162 */     Capabilities result = super.getCapabilities();
/*  85:163 */     result.disableAll();
/*  86:166 */     if (!this.m_noClass)
/*  87:    */     {
/*  88:167 */       result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  89:168 */       result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  90:    */     }
/*  91:    */     else
/*  92:    */     {
/*  93:170 */       result.enable(Capabilities.Capability.NO_CLASS);
/*  94:    */     }
/*  95:174 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  96:175 */     return result;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public String getRevision()
/* 100:    */   {
/* 101:184 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 102:    */   }
/* 103:    */   
/* 104:    */   public static void main(String[] argv)
/* 105:    */   {
/* 106:    */     try
/* 107:    */     {
/* 108:195 */       double delta = 0.5D;
/* 109:196 */       double xmean = 0.0D;
/* 110:197 */       double lower = 0.0D;
/* 111:198 */       double upper = 10.0D;
/* 112:199 */       Matrix covariance = new Matrix(2, 2);
/* 113:200 */       covariance.set(0, 0, 2.0D);
/* 114:201 */       covariance.set(0, 1, -3.0D);
/* 115:202 */       covariance.set(1, 0, -4.0D);
/* 116:203 */       covariance.set(1, 1, 5.0D);
/* 117:204 */       if (argv.length > 0) {
/* 118:205 */         covariance.set(0, 0, Double.valueOf(argv[0]).doubleValue());
/* 119:    */       }
/* 120:207 */       if (argv.length > 1) {
/* 121:208 */         covariance.set(0, 1, Double.valueOf(argv[1]).doubleValue());
/* 122:    */       }
/* 123:210 */       if (argv.length > 2) {
/* 124:211 */         covariance.set(1, 0, Double.valueOf(argv[2]).doubleValue());
/* 125:    */       }
/* 126:213 */       if (argv.length > 3) {
/* 127:214 */         covariance.set(1, 1, Double.valueOf(argv[3]).doubleValue());
/* 128:    */       }
/* 129:216 */       if (argv.length > 4) {
/* 130:217 */         delta = Double.valueOf(argv[4]).doubleValue();
/* 131:    */       }
/* 132:219 */       if (argv.length > 5) {
/* 133:220 */         xmean = Double.valueOf(argv[5]).doubleValue();
/* 134:    */       }
/* 135:223 */       MahalanobisEstimator newEst = new MahalanobisEstimator(covariance, delta, xmean);
/* 136:225 */       if (argv.length > 6)
/* 137:    */       {
/* 138:226 */         lower = Double.valueOf(argv[6]).doubleValue();
/* 139:227 */         if (argv.length > 7) {
/* 140:228 */           upper = Double.valueOf(argv[7]).doubleValue();
/* 141:    */         }
/* 142:230 */         double increment = (upper - lower) / 50.0D;
/* 143:231 */         for (double current = lower; current <= upper; current += increment) {
/* 144:232 */           System.out.println(current + "  " + newEst.getProbability(current));
/* 145:    */         }
/* 146:    */       }
/* 147:    */       else
/* 148:    */       {
/* 149:234 */         System.out.println("Covariance Matrix\n" + covariance);
/* 150:235 */         System.out.println(newEst);
/* 151:    */       }
/* 152:    */     }
/* 153:    */     catch (Exception e)
/* 154:    */     {
/* 155:238 */       System.out.println(e.getMessage());
/* 156:    */     }
/* 157:    */   }
/* 158:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.MahalanobisEstimator
 * JD-Core Version:    0.7.0.1
 */