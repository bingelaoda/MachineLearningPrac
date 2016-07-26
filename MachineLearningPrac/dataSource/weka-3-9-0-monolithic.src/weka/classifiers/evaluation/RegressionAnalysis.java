/*   1:    */ package weka.classifiers.evaluation;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import weka.core.Attribute;
/*   5:    */ import weka.core.Instance;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.RevisionUtils;
/*   8:    */ import weka.core.matrix.Matrix;
/*   9:    */ 
/*  10:    */ public class RegressionAnalysis
/*  11:    */ {
/*  12:    */   public static double calculateSSR(Instances data, Attribute chosen, double slope, double intercept)
/*  13:    */     throws Exception
/*  14:    */   {
/*  15: 58 */     double ssr = 0.0D;
/*  16: 59 */     for (int i = 0; i < data.numInstances(); i++)
/*  17:    */     {
/*  18: 60 */       double yHat = slope * data.instance(i).value(chosen) + intercept;
/*  19: 61 */       double resid = data.instance(i).value(data.classIndex()) - yHat;
/*  20: 62 */       ssr += resid * resid;
/*  21:    */     }
/*  22: 64 */     return ssr;
/*  23:    */   }
/*  24:    */   
/*  25:    */   public static double calculateRSquared(Instances data, double ssr)
/*  26:    */     throws Exception
/*  27:    */   {
/*  28: 80 */     double yMean = data.meanOrMode(data.classIndex());
/*  29: 81 */     double tss = 0.0D;
/*  30: 82 */     for (int i = 0; i < data.numInstances(); i++) {
/*  31: 83 */       tss += (data.instance(i).value(data.classIndex()) - yMean) * (data.instance(i).value(data.classIndex()) - yMean);
/*  32:    */     }
/*  33: 88 */     double rsq = 1.0D - ssr / tss;
/*  34: 89 */     return rsq;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public static double calculateAdjRSquared(double rsq, int n, int k)
/*  38:    */   {
/*  39:102 */     if ((n < 1) || (k < 2) || (n == k))
/*  40:    */     {
/*  41:103 */       System.err.println("Cannot calculate Adjusted R^2.");
/*  42:104 */       return (0.0D / 0.0D);
/*  43:    */     }
/*  44:107 */     return 1.0D - (1.0D - rsq) * (n - 1) / (n - k);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public static double calculateFStat(double rsq, int n, int k)
/*  48:    */   {
/*  49:119 */     if ((n < 1) || (k < 2) || (n == k))
/*  50:    */     {
/*  51:120 */       System.err.println("Cannot calculate F-stat.");
/*  52:121 */       return (0.0D / 0.0D);
/*  53:    */     }
/*  54:124 */     double numerator = rsq / (k - 1);
/*  55:125 */     double denominator = (1.0D - rsq) / (n - k);
/*  56:126 */     return numerator / denominator;
/*  57:    */   }
/*  58:    */   
/*  59:    */   public static double[] calculateStdErrorOfCoef(Instances data, Attribute chosen, double slope, double intercept, int df)
/*  60:    */     throws Exception
/*  61:    */   {
/*  62:146 */     double ssr = calculateSSR(data, chosen, slope, intercept);
/*  63:147 */     double mse = ssr / df;
/*  64:    */     
/*  65:    */ 
/*  66:    */ 
/*  67:    */ 
/*  68:    */ 
/*  69:153 */     double[][] array = new double[data.numInstances()][2];
/*  70:154 */     for (int i = 0; i < data.numInstances(); i++)
/*  71:    */     {
/*  72:155 */       array[i][0] = data.instance(i).value(chosen);
/*  73:156 */       array[i][1] = 1.0D;
/*  74:    */     }
/*  75:163 */     Matrix X = new Matrix(array);
/*  76:164 */     Matrix Xt = X.transpose();
/*  77:165 */     Matrix XtX = Xt.times(X);
/*  78:166 */     Matrix inverse = XtX.inverse();
/*  79:167 */     Matrix cov = inverse.times(mse);
/*  80:    */     
/*  81:169 */     double[] result = new double[2];
/*  82:170 */     for (int i = 0; i < 2; i++) {
/*  83:171 */       result[i] = Math.sqrt(cov.get(i, i));
/*  84:    */     }
/*  85:174 */     return result;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public static double[] calculateStdErrorOfCoef(Instances data, boolean[] selected, double ssr, int n, int k)
/*  89:    */     throws Exception
/*  90:    */   {
/*  91:195 */     double[][] array = new double[n][k];
/*  92:    */     
/*  93:197 */     int column = 0;
/*  94:198 */     for (int j = 0; j < data.numAttributes(); j++) {
/*  95:199 */       if ((data.classIndex() != j) && (selected[j] != 0))
/*  96:    */       {
/*  97:200 */         for (int i = 0; i < n; i++) {
/*  98:201 */           array[i][column] = data.instance(i).value(j);
/*  99:    */         }
/* 100:203 */         column++;
/* 101:    */       }
/* 102:    */     }
/* 103:208 */     for (int i = 0; i < n; i++) {
/* 104:209 */       array[i][(k - 1)] = 1.0D;
/* 105:    */     }
/* 106:216 */     Matrix X = new Matrix(array);
/* 107:217 */     Matrix Xt = X.transpose();
/* 108:218 */     Matrix XtX = Xt.times(X);
/* 109:219 */     Matrix inverse = XtX.inverse();
/* 110:    */     
/* 111:221 */     double mse = ssr / (n - k);
/* 112:222 */     Matrix cov = inverse.times(mse);
/* 113:    */     
/* 114:224 */     double[] result = new double[k];
/* 115:225 */     for (int i = 0; i < k; i++) {
/* 116:226 */       result[i] = Math.sqrt(cov.get(i, i));
/* 117:    */     }
/* 118:228 */     return result;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public static double[] calculateTStats(double[] coef, double[] stderror, int k)
/* 122:    */   {
/* 123:241 */     double[] result = new double[k];
/* 124:242 */     for (int i = 0; i < k; i++) {
/* 125:243 */       coef[i] /= stderror[i];
/* 126:    */     }
/* 127:245 */     return result;
/* 128:    */   }
/* 129:    */   
/* 130:    */   public String getRevision()
/* 131:    */   {
/* 132:254 */     return RevisionUtils.extract("$Revision: ? $");
/* 133:    */   }
/* 134:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.RegressionAnalysis
 * JD-Core Version:    0.7.0.1
 */