/*   1:    */ package weka.core.matrix;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.RevisionHandler;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ 
/*   7:    */ public class QRDecomposition
/*   8:    */   implements Serializable, RevisionHandler
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -5013090736132211418L;
/*  11:    */   private double[][] QR;
/*  12:    */   private int m;
/*  13:    */   private int n;
/*  14:    */   private double[] Rdiag;
/*  15:    */   
/*  16:    */   public QRDecomposition(Matrix A)
/*  17:    */   {
/*  18: 70 */     this.QR = A.getArrayCopy();
/*  19: 71 */     this.m = A.getRowDimension();
/*  20: 72 */     this.n = A.getColumnDimension();
/*  21: 73 */     this.Rdiag = new double[this.n];
/*  22: 76 */     for (int k = 0; k < this.n; k++)
/*  23:    */     {
/*  24: 78 */       double nrm = 0.0D;
/*  25: 79 */       for (int i = k; i < this.m; i++) {
/*  26: 80 */         nrm = Maths.hypot(nrm, this.QR[i][k]);
/*  27:    */       }
/*  28: 83 */       if (nrm != 0.0D)
/*  29:    */       {
/*  30: 85 */         if (this.QR[k][k] < 0.0D) {
/*  31: 86 */           nrm = -nrm;
/*  32:    */         }
/*  33: 88 */         for (int i = k; i < this.m; i++) {
/*  34: 89 */           this.QR[i][k] /= nrm;
/*  35:    */         }
/*  36: 91 */         this.QR[k][k] += 1.0D;
/*  37: 94 */         for (int j = k + 1; j < this.n; j++)
/*  38:    */         {
/*  39: 95 */           double s = 0.0D;
/*  40: 96 */           for (int i = k; i < this.m; i++) {
/*  41: 97 */             s += this.QR[i][k] * this.QR[i][j];
/*  42:    */           }
/*  43: 99 */           s = -s / this.QR[k][k];
/*  44:100 */           for (int i = k; i < this.m; i++) {
/*  45:101 */             this.QR[i][j] += s * this.QR[i][k];
/*  46:    */           }
/*  47:    */         }
/*  48:    */       }
/*  49:105 */       this.Rdiag[k] = (-nrm);
/*  50:    */     }
/*  51:    */   }
/*  52:    */   
/*  53:    */   public boolean isFullRank()
/*  54:    */   {
/*  55:114 */     for (int j = 0; j < this.n; j++) {
/*  56:115 */       if (this.Rdiag[j] == 0.0D) {
/*  57:116 */         return false;
/*  58:    */       }
/*  59:    */     }
/*  60:118 */     return true;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public Matrix getH()
/*  64:    */   {
/*  65:126 */     Matrix X = new Matrix(this.m, this.n);
/*  66:127 */     double[][] H = X.getArray();
/*  67:128 */     for (int i = 0; i < this.m; i++) {
/*  68:129 */       for (int j = 0; j < this.n; j++) {
/*  69:130 */         if (i >= j) {
/*  70:131 */           H[i][j] = this.QR[i][j];
/*  71:    */         } else {
/*  72:133 */           H[i][j] = 0.0D;
/*  73:    */         }
/*  74:    */       }
/*  75:    */     }
/*  76:137 */     return X;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public Matrix getR()
/*  80:    */   {
/*  81:145 */     Matrix X = new Matrix(this.n, this.n);
/*  82:146 */     double[][] R = X.getArray();
/*  83:147 */     for (int i = 0; i < this.n; i++) {
/*  84:148 */       for (int j = 0; j < this.n; j++) {
/*  85:149 */         if (i < j) {
/*  86:150 */           R[i][j] = this.QR[i][j];
/*  87:151 */         } else if (i == j) {
/*  88:152 */           R[i][j] = this.Rdiag[i];
/*  89:    */         } else {
/*  90:154 */           R[i][j] = 0.0D;
/*  91:    */         }
/*  92:    */       }
/*  93:    */     }
/*  94:158 */     return X;
/*  95:    */   }
/*  96:    */   
/*  97:    */   public Matrix getQ()
/*  98:    */   {
/*  99:166 */     Matrix X = new Matrix(this.m, this.n);
/* 100:167 */     double[][] Q = X.getArray();
/* 101:168 */     for (int k = this.n - 1; k >= 0; k--)
/* 102:    */     {
/* 103:169 */       for (int i = 0; i < this.m; i++) {
/* 104:170 */         Q[i][k] = 0.0D;
/* 105:    */       }
/* 106:172 */       Q[k][k] = 1.0D;
/* 107:173 */       for (int j = k; j < this.n; j++) {
/* 108:174 */         if (this.QR[k][k] != 0.0D)
/* 109:    */         {
/* 110:175 */           double s = 0.0D;
/* 111:176 */           for (int i = k; i < this.m; i++) {
/* 112:177 */             s += this.QR[i][k] * Q[i][j];
/* 113:    */           }
/* 114:179 */           s = -s / this.QR[k][k];
/* 115:180 */           for (int i = k; i < this.m; i++) {
/* 116:181 */             Q[i][j] += s * this.QR[i][k];
/* 117:    */           }
/* 118:    */         }
/* 119:    */       }
/* 120:    */     }
/* 121:186 */     return X;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public Matrix solve(Matrix B)
/* 125:    */   {
/* 126:197 */     if (B.getRowDimension() != this.m) {
/* 127:198 */       throw new IllegalArgumentException("Matrix row dimensions must agree.");
/* 128:    */     }
/* 129:200 */     if (!isFullRank()) {
/* 130:201 */       throw new RuntimeException("Matrix is rank deficient.");
/* 131:    */     }
/* 132:205 */     int nx = B.getColumnDimension();
/* 133:206 */     double[][] X = B.getArrayCopy();
/* 134:209 */     for (int k = 0; k < this.n; k++) {
/* 135:210 */       for (int j = 0; j < nx; j++)
/* 136:    */       {
/* 137:211 */         double s = 0.0D;
/* 138:212 */         for (int i = k; i < this.m; i++) {
/* 139:213 */           s += this.QR[i][k] * X[i][j];
/* 140:    */         }
/* 141:215 */         s = -s / this.QR[k][k];
/* 142:216 */         for (int i = k; i < this.m; i++) {
/* 143:217 */           X[i][j] += s * this.QR[i][k];
/* 144:    */         }
/* 145:    */       }
/* 146:    */     }
/* 147:222 */     for (int k = this.n - 1; k >= 0; k--)
/* 148:    */     {
/* 149:223 */       for (int j = 0; j < nx; j++) {
/* 150:224 */         X[k][j] /= this.Rdiag[k];
/* 151:    */       }
/* 152:226 */       for (int i = 0; i < k; i++) {
/* 153:227 */         for (int j = 0; j < nx; j++) {
/* 154:228 */           X[i][j] -= X[k][j] * this.QR[i][k];
/* 155:    */         }
/* 156:    */       }
/* 157:    */     }
/* 158:232 */     return new Matrix(X, this.n, nx).getMatrix(0, this.n - 1, 0, nx - 1);
/* 159:    */   }
/* 160:    */   
/* 161:    */   public String getRevision()
/* 162:    */   {
/* 163:241 */     return RevisionUtils.extract("$Revision: 5953 $");
/* 164:    */   }
/* 165:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.matrix.QRDecomposition
 * JD-Core Version:    0.7.0.1
 */