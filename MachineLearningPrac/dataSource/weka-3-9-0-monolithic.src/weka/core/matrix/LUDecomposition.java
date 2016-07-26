/*   1:    */ package weka.core.matrix;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.RevisionHandler;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ 
/*   7:    */ public class LUDecomposition
/*   8:    */   implements Serializable, RevisionHandler
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -2731022568037808629L;
/*  11:    */   private double[][] LU;
/*  12:    */   private int m;
/*  13:    */   private int n;
/*  14:    */   private int pivsign;
/*  15:    */   private int[] piv;
/*  16:    */   
/*  17:    */   public LUDecomposition(Matrix A)
/*  18:    */   {
/*  19: 75 */     this.LU = A.getArrayCopy();
/*  20: 76 */     this.m = A.getRowDimension();
/*  21: 77 */     this.n = A.getColumnDimension();
/*  22: 78 */     this.piv = new int[this.m];
/*  23: 79 */     for (int i = 0; i < this.m; i++) {
/*  24: 80 */       this.piv[i] = i;
/*  25:    */     }
/*  26: 82 */     this.pivsign = 1;
/*  27:    */     
/*  28: 84 */     double[] LUcolj = new double[this.m];
/*  29: 88 */     for (int j = 0; j < this.n; j++)
/*  30:    */     {
/*  31: 92 */       for (int i = 0; i < this.m; i++) {
/*  32: 93 */         LUcolj[i] = this.LU[i][j];
/*  33:    */       }
/*  34: 98 */       for (int i = 0; i < this.m; i++)
/*  35:    */       {
/*  36: 99 */         double[] LUrowi = this.LU[i];
/*  37:    */         
/*  38:    */ 
/*  39:    */ 
/*  40:103 */         int kmax = Math.min(i, j);
/*  41:104 */         double s = 0.0D;
/*  42:105 */         for (int k = 0; k < kmax; k++) {
/*  43:106 */           s += LUrowi[k] * LUcolj[k];
/*  44:    */         }
/*  45:109 */         int tmp185_183 = i; double[] tmp185_182 = LUcolj; long tmp190_189 = (tmp185_182[tmp185_183] - s);tmp185_182[tmp185_183] = tmp190_189;LUrowi[j] = tmp190_189;
/*  46:    */       }
/*  47:114 */       int p = j;
/*  48:115 */       for (int i = j + 1; i < this.m; i++) {
/*  49:116 */         if (Math.abs(LUcolj[i]) > Math.abs(LUcolj[p])) {
/*  50:117 */           p = i;
/*  51:    */         }
/*  52:    */       }
/*  53:120 */       if (p != j)
/*  54:    */       {
/*  55:121 */         for (int k = 0; k < this.n; k++)
/*  56:    */         {
/*  57:122 */           double t = this.LU[p][k];this.LU[p][k] = this.LU[j][k];this.LU[j][k] = t;
/*  58:    */         }
/*  59:124 */         int k = this.piv[p];this.piv[p] = this.piv[j];this.piv[j] = k;
/*  60:125 */         this.pivsign = (-this.pivsign);
/*  61:    */       }
/*  62:130 */       if (((j < this.m ? 1 : 0) & (this.LU[j][j] != 0.0D ? 1 : 0)) != 0) {
/*  63:131 */         for (int i = j + 1; i < this.m; i++) {
/*  64:132 */           this.LU[i][j] /= this.LU[j][j];
/*  65:    */         }
/*  66:    */       }
/*  67:    */     }
/*  68:    */   }
/*  69:    */   
/*  70:    */   public boolean isNonsingular()
/*  71:    */   {
/*  72:143 */     for (int j = 0; j < this.n; j++) {
/*  73:144 */       if (this.LU[j][j] == 0.0D) {
/*  74:145 */         return false;
/*  75:    */       }
/*  76:    */     }
/*  77:147 */     return true;
/*  78:    */   }
/*  79:    */   
/*  80:    */   public Matrix getL()
/*  81:    */   {
/*  82:155 */     Matrix X = new Matrix(this.m, this.n);
/*  83:156 */     double[][] L = X.getArray();
/*  84:157 */     for (int i = 0; i < this.m; i++) {
/*  85:158 */       for (int j = 0; j < this.n; j++) {
/*  86:159 */         if (i > j) {
/*  87:160 */           L[i][j] = this.LU[i][j];
/*  88:161 */         } else if (i == j) {
/*  89:162 */           L[i][j] = 1.0D;
/*  90:    */         } else {
/*  91:164 */           L[i][j] = 0.0D;
/*  92:    */         }
/*  93:    */       }
/*  94:    */     }
/*  95:168 */     return X;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public Matrix getU()
/*  99:    */   {
/* 100:176 */     Matrix X = new Matrix(this.n, this.n);
/* 101:177 */     double[][] U = X.getArray();
/* 102:178 */     for (int i = 0; i < this.n; i++) {
/* 103:179 */       for (int j = 0; j < this.n; j++) {
/* 104:180 */         if (i <= j) {
/* 105:181 */           U[i][j] = this.LU[i][j];
/* 106:    */         } else {
/* 107:183 */           U[i][j] = 0.0D;
/* 108:    */         }
/* 109:    */       }
/* 110:    */     }
/* 111:187 */     return X;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public int[] getPivot()
/* 115:    */   {
/* 116:195 */     int[] p = new int[this.m];
/* 117:196 */     for (int i = 0; i < this.m; i++) {
/* 118:197 */       p[i] = this.piv[i];
/* 119:    */     }
/* 120:199 */     return p;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public double[] getDoublePivot()
/* 124:    */   {
/* 125:207 */     double[] vals = new double[this.m];
/* 126:208 */     for (int i = 0; i < this.m; i++) {
/* 127:209 */       vals[i] = this.piv[i];
/* 128:    */     }
/* 129:211 */     return vals;
/* 130:    */   }
/* 131:    */   
/* 132:    */   public double det()
/* 133:    */   {
/* 134:220 */     if (this.m != this.n) {
/* 135:221 */       throw new IllegalArgumentException("Matrix must be square.");
/* 136:    */     }
/* 137:223 */     double d = this.pivsign;
/* 138:224 */     for (int j = 0; j < this.n; j++) {
/* 139:225 */       d *= this.LU[j][j];
/* 140:    */     }
/* 141:227 */     return d;
/* 142:    */   }
/* 143:    */   
/* 144:    */   public Matrix solve(Matrix B)
/* 145:    */   {
/* 146:238 */     if (B.getRowDimension() != this.m) {
/* 147:239 */       throw new IllegalArgumentException("Matrix row dimensions must agree.");
/* 148:    */     }
/* 149:241 */     if (!isNonsingular()) {
/* 150:242 */       throw new RuntimeException("Matrix is singular.");
/* 151:    */     }
/* 152:246 */     int nx = B.getColumnDimension();
/* 153:247 */     Matrix Xmat = B.getMatrix(this.piv, 0, nx - 1);
/* 154:248 */     double[][] X = Xmat.getArray();
/* 155:251 */     for (int k = 0; k < this.n; k++) {
/* 156:252 */       for (int i = k + 1; i < this.n; i++) {
/* 157:253 */         for (int j = 0; j < nx; j++) {
/* 158:254 */           X[i][j] -= X[k][j] * this.LU[i][k];
/* 159:    */         }
/* 160:    */       }
/* 161:    */     }
/* 162:259 */     for (int k = this.n - 1; k >= 0; k--)
/* 163:    */     {
/* 164:260 */       for (int j = 0; j < nx; j++) {
/* 165:261 */         X[k][j] /= this.LU[k][k];
/* 166:    */       }
/* 167:263 */       for (int i = 0; i < k; i++) {
/* 168:264 */         for (int j = 0; j < nx; j++) {
/* 169:265 */           X[i][j] -= X[k][j] * this.LU[i][k];
/* 170:    */         }
/* 171:    */       }
/* 172:    */     }
/* 173:269 */     return Xmat;
/* 174:    */   }
/* 175:    */   
/* 176:    */   public String getRevision()
/* 177:    */   {
/* 178:278 */     return RevisionUtils.extract("$Revision: 5953 $");
/* 179:    */   }
/* 180:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.matrix.LUDecomposition
 * JD-Core Version:    0.7.0.1
 */