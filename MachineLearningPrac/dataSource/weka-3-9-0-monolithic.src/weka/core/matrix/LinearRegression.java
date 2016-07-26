/*   1:    */ package weka.core.matrix;
/*   2:    */ 
/*   3:    */ import weka.core.RevisionHandler;
/*   4:    */ import weka.core.RevisionUtils;
/*   5:    */ import weka.core.Utils;
/*   6:    */ 
/*   7:    */ public class LinearRegression
/*   8:    */   implements RevisionHandler
/*   9:    */ {
/*  10: 33 */   protected double[] m_Coefficients = null;
/*  11:    */   
/*  12:    */   public LinearRegression(Matrix a, Matrix y, double ridge)
/*  13:    */   {
/*  14: 44 */     calculate(a, y, ridge);
/*  15:    */   }
/*  16:    */   
/*  17:    */   public LinearRegression(Matrix a, Matrix y, double[] w, double ridge)
/*  18:    */   {
/*  19: 59 */     if (w.length != a.getRowDimension()) {
/*  20: 60 */       throw new IllegalArgumentException("Incorrect number of weights provided");
/*  21:    */     }
/*  22: 61 */     Matrix weightedThis = new Matrix(a.getRowDimension(), a.getColumnDimension());
/*  23:    */     
/*  24: 63 */     Matrix weightedDep = new Matrix(a.getRowDimension(), 1);
/*  25: 64 */     for (int i = 0; i < w.length; i++)
/*  26:    */     {
/*  27: 65 */       double sqrt_weight = Math.sqrt(w[i]);
/*  28: 66 */       for (int j = 0; j < a.getColumnDimension(); j++) {
/*  29: 67 */         weightedThis.set(i, j, a.get(i, j) * sqrt_weight);
/*  30:    */       }
/*  31: 68 */       weightedDep.set(i, 0, y.get(i, 0) * sqrt_weight);
/*  32:    */     }
/*  33: 71 */     calculate(weightedThis, weightedDep, ridge);
/*  34:    */   }
/*  35:    */   
/*  36:    */   protected void calculate(Matrix a, Matrix y, double ridge)
/*  37:    */   {
/*  38: 84 */     if (y.getColumnDimension() > 1) {
/*  39: 85 */       throw new IllegalArgumentException("Only one dependent variable allowed");
/*  40:    */     }
/*  41: 87 */     int nc = a.getColumnDimension();
/*  42: 88 */     this.m_Coefficients = new double[nc];
/*  43:    */     
/*  44:    */ 
/*  45: 91 */     Matrix ss = aTa(a);
/*  46: 92 */     Matrix bb = aTy(a, y);
/*  47:    */     
/*  48: 94 */     boolean success = true;
/*  49:    */     do
/*  50:    */     {
/*  51: 98 */       Matrix ssWithRidge = ss.copy();
/*  52: 99 */       for (int i = 0; i < nc; i++) {
/*  53:100 */         ssWithRidge.set(i, i, ssWithRidge.get(i, i) + ridge);
/*  54:    */       }
/*  55:    */       try
/*  56:    */       {
/*  57:104 */         Matrix solution = ssWithRidge.solve(bb);
/*  58:105 */         for (int i = 0; i < nc; i++) {
/*  59:106 */           this.m_Coefficients[i] = solution.get(i, 0);
/*  60:    */         }
/*  61:107 */         success = true;
/*  62:    */       }
/*  63:    */       catch (Exception ex)
/*  64:    */       {
/*  65:109 */         ridge *= 10.0D;
/*  66:110 */         success = false;
/*  67:    */       }
/*  68:112 */     } while (!success);
/*  69:    */   }
/*  70:    */   
/*  71:    */   private static Matrix aTa(Matrix a)
/*  72:    */   {
/*  73:119 */     int cols = a.getColumnDimension();
/*  74:120 */     double[][] A = a.getArray();
/*  75:121 */     Matrix x = new Matrix(cols, cols);
/*  76:122 */     double[][] X = x.getArray();
/*  77:123 */     double[] Acol = new double[a.getRowDimension()];
/*  78:124 */     for (int col1 = 0; col1 < cols; col1++)
/*  79:    */     {
/*  80:126 */       for (int row = 0; row < Acol.length; row++) {
/*  81:127 */         Acol[row] = A[row][col1];
/*  82:    */       }
/*  83:130 */       double[] Xrow = X[col1];
/*  84:131 */       for (int row = 0; row < Acol.length; row++)
/*  85:    */       {
/*  86:132 */         double[] Arow = A[row];
/*  87:133 */         for (int col2 = col1; col2 < Xrow.length; col2++) {
/*  88:134 */           Xrow[col2] += Acol[row] * Arow[col2];
/*  89:    */         }
/*  90:    */       }
/*  91:138 */       for (int col2 = col1 + 1; col2 < Xrow.length; col2++) {
/*  92:139 */         X[col2][col1] = Xrow[col2];
/*  93:    */       }
/*  94:    */     }
/*  95:142 */     return x;
/*  96:    */   }
/*  97:    */   
/*  98:    */   private static Matrix aTy(Matrix a, Matrix y)
/*  99:    */   {
/* 100:149 */     double[][] A = a.getArray();
/* 101:150 */     double[][] Y = y.getArray();
/* 102:151 */     Matrix x = new Matrix(a.getColumnDimension(), 1);
/* 103:152 */     double[][] X = x.getArray();
/* 104:153 */     for (int row = 0; row < A.length; row++)
/* 105:    */     {
/* 106:155 */       double[] Arow = A[row];
/* 107:156 */       double[] Yrow = Y[row];
/* 108:157 */       for (int col = 0; col < Arow.length; col++) {
/* 109:158 */         X[col][0] += Arow[col] * Yrow[0];
/* 110:    */       }
/* 111:    */     }
/* 112:161 */     return x;
/* 113:    */   }
/* 114:    */   
/* 115:    */   public final double[] getCoefficients()
/* 116:    */   {
/* 117:170 */     return this.m_Coefficients;
/* 118:    */   }
/* 119:    */   
/* 120:    */   public String toString()
/* 121:    */   {
/* 122:177 */     return Utils.arrayToString(getCoefficients());
/* 123:    */   }
/* 124:    */   
/* 125:    */   public String getRevision()
/* 126:    */   {
/* 127:186 */     return RevisionUtils.extract("$Revision: 9768 $");
/* 128:    */   }
/* 129:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.matrix.LinearRegression
 * JD-Core Version:    0.7.0.1
 */