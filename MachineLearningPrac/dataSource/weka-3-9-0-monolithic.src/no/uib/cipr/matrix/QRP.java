/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.LAPACK;
/*   4:    */ import org.netlib.util.intW;
/*   5:    */ 
/*   6:    */ public class QRP
/*   7:    */ {
/*   8:    */   int[] jpvt;
/*   9:    */   final double[] tau;
/*  10:    */   final int m;
/*  11:    */   final int n;
/*  12:    */   final int k;
/*  13:    */   int rank;
/*  14:    */   double[] work;
/*  15:    */   final DenseMatrix Afact;
/*  16:    */   final DenseMatrix Q;
/*  17:    */   final DenseMatrix R;
/*  18:    */   
/*  19:    */   public QRP(int m, int n)
/*  20:    */   {
/*  21: 76 */     this.m = m;
/*  22: 77 */     this.n = n;
/*  23: 78 */     this.k = Math.min(m, n);
/*  24: 79 */     this.rank = 0;
/*  25: 80 */     this.jpvt = new int[n];
/*  26: 81 */     this.tau = new double[this.k];
/*  27:    */     
/*  28: 83 */     this.Q = new DenseMatrix(m, m);
/*  29: 84 */     this.R = new DenseMatrix(m, n);
/*  30: 85 */     this.Afact = new DenseMatrix(m, Math.max(m, n));
/*  31:    */     
/*  32:    */ 
/*  33: 88 */     intW info = new intW(0);
/*  34: 89 */     double[] dummy = new double[1];
/*  35: 90 */     double[] ret = new double[1];
/*  36:    */     
/*  37: 92 */     LAPACK lapack = LAPACK.getInstance();
/*  38:    */     
/*  39:    */ 
/*  40: 95 */     lapack.dgeqrf(m, n, dummy, Matrices.ld(m), dummy, ret, -1, info);
/*  41: 96 */     int lwork1 = info.val != 0 ? n : (int)ret[0];
/*  42:    */     
/*  43:    */ 
/*  44: 99 */     lapack.dorgqr(m, m, this.k, dummy, Matrices.ld(m), dummy, ret, -1, info);
/*  45:100 */     int lwork2 = info.val != 0 ? n : (int)ret[0];
/*  46:    */     
/*  47:102 */     this.work = new double[Math.max(lwork1, lwork2)];
/*  48:    */   }
/*  49:    */   
/*  50:    */   public static QRP factorize(Matrix A)
/*  51:    */   {
/*  52:113 */     return new QRP(A.numRows(), A.numColumns()).factor(A);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public QRP factor(Matrix A)
/*  56:    */   {
/*  57:124 */     if (this.Q.numRows() != A.numRows()) {
/*  58:125 */       throw new IllegalArgumentException("Q.numRows() != A.numRows()");
/*  59:    */     }
/*  60:126 */     if (this.R.numColumns() != A.numColumns()) {
/*  61:127 */       throw new IllegalArgumentException("R.numColumns() != A.numColumns()");
/*  62:    */     }
/*  63:131 */     this.Afact.zero();
/*  64:132 */     for (MatrixEntry e : A) {
/*  65:133 */       this.Afact.set(e.row(), e.column(), e.get());
/*  66:    */     }
/*  67:136 */     intW info = new intW(0);
/*  68:137 */     LAPACK lapack = LAPACK.getInstance();
/*  69:    */     
/*  70:    */ 
/*  71:    */ 
/*  72:    */ 
/*  73:142 */     double[] factorWorkOptimalSize = { 0.0D };
/*  74:    */     
/*  75:    */ 
/*  76:145 */     lapack.dgeqp3(this.m, this.n, this.Afact.getData(), Matrices.ld(this.m), this.jpvt, this.tau, factorWorkOptimalSize, -1, info);
/*  77:    */     
/*  78:147 */     double[] factorWork = new double[(int)factorWorkOptimalSize[0]];
/*  79:    */     
/*  80:    */ 
/*  81:    */ 
/*  82:    */ 
/*  83:152 */     lapack.dgeqp3(this.m, this.n, this.Afact.getData(), Matrices.ld(this.m), this.jpvt, this.tau, factorWork, factorWork.length, info);
/*  84:155 */     if (info.val < 0) {
/*  85:156 */       throw new IllegalArgumentException("DGEQP3 was " + info.val);
/*  86:    */     }
/*  87:162 */     this.R.zero();
/*  88:163 */     for (MatrixEntry e : this.Afact) {
/*  89:164 */       if ((e.row() <= e.column()) && (e.column() < this.R.numColumns())) {
/*  90:165 */         this.R.set(e.row(), e.column(), e.get());
/*  91:    */       }
/*  92:    */     }
/*  93:172 */     double EPS = 1.0E-012D;
/*  94:173 */     for (this.rank = 0; (this.rank < this.k) && 
/*  95:174 */           (Math.abs(this.R.get(this.rank, this.rank)) >= 1.0E-012D); this.rank += 1) {}
/*  96:181 */     lapack.dorgqr(this.m, this.m, this.k, this.Afact.getData(), Matrices.ld(this.m), this.tau, this.work, this.work.length, info);
/*  97:183 */     for (MatrixEntry e : this.Afact) {
/*  98:184 */       if (e.column() < this.Q.numColumns()) {
/*  99:185 */         this.Q.set(e.row(), e.column(), e.get());
/* 100:    */       }
/* 101:    */     }
/* 102:188 */     if (info.val < 0) {
/* 103:189 */       throw new IllegalArgumentException();
/* 104:    */     }
/* 105:192 */     for (int i = 0; i < this.jpvt.length; i++) {
/* 106:193 */       this.jpvt[i] -= 1;
/* 107:    */     }
/* 108:196 */     return this;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public DenseMatrix getR()
/* 112:    */   {
/* 113:203 */     return this.R;
/* 114:    */   }
/* 115:    */   
/* 116:    */   public DenseMatrix getQ()
/* 117:    */   {
/* 118:210 */     return this.Q;
/* 119:    */   }
/* 120:    */   
/* 121:    */   public int[] getPVector()
/* 122:    */   {
/* 123:218 */     return this.jpvt;
/* 124:    */   }
/* 125:    */   
/* 126:    */   public Matrix getP()
/* 127:    */   {
/* 128:226 */     Matrix P = new DenseMatrix(this.jpvt.length, this.jpvt.length);
/* 129:227 */     for (int i = 0; i < this.jpvt.length; i++) {
/* 130:228 */       P.set(this.jpvt[i], i, 1.0D);
/* 131:    */     }
/* 132:230 */     return P;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public int getRank()
/* 136:    */   {
/* 137:237 */     return this.rank;
/* 138:    */   }
/* 139:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.QRP
 * JD-Core Version:    0.7.0.1
 */