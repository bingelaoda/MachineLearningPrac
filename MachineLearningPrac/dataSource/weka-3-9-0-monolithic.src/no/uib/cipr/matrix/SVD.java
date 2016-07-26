/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.LAPACK;
/*   4:    */ import org.netlib.util.intW;
/*   5:    */ 
/*   6:    */ public class SVD
/*   7:    */ {
/*   8:    */   private final double[] work;
/*   9:    */   private final int[] iwork;
/*  10:    */   private final int m;
/*  11:    */   private final int n;
/*  12:    */   private final boolean vectors;
/*  13:    */   private final JobSVD job;
/*  14:    */   private final double[] S;
/*  15:    */   private final DenseMatrix U;
/*  16:    */   private final DenseMatrix Vt;
/*  17:    */   
/*  18:    */   public SVD(int m, int n)
/*  19:    */   {
/*  20: 75 */     this(m, n, true);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public SVD(int m, int n, boolean vectors)
/*  24:    */   {
/*  25: 90 */     this.m = m;
/*  26: 91 */     this.n = n;
/*  27: 92 */     this.vectors = vectors;
/*  28:    */     
/*  29:    */ 
/*  30: 95 */     this.S = new double[Math.min(m, n)];
/*  31: 96 */     if (vectors)
/*  32:    */     {
/*  33: 97 */       this.U = new DenseMatrix(m, m);
/*  34: 98 */       this.Vt = new DenseMatrix(n, n);
/*  35:    */     }
/*  36:    */     else
/*  37:    */     {
/*  38:100 */       this.U = (this.Vt = null);
/*  39:    */     }
/*  40:102 */     this.job = (vectors ? JobSVD.All : JobSVD.None);
/*  41:    */     
/*  42:    */ 
/*  43:105 */     this.iwork = new int[8 * Math.min(m, n)];
/*  44:    */     
/*  45:    */ 
/*  46:108 */     double[] worksize = new double[1];
/*  47:109 */     intW info = new intW(0);
/*  48:110 */     LAPACK.getInstance().dgesdd(this.job.netlib(), m, n, new double[0], 
/*  49:111 */       Matrices.ld(m), new double[0], new double[0], Matrices.ld(m), new double[0], 
/*  50:112 */       Matrices.ld(n), worksize, -1, this.iwork, info);
/*  51:    */     
/*  52:    */ 
/*  53:115 */     int lwork = -1;
/*  54:116 */     if (info.val != 0)
/*  55:    */     {
/*  56:117 */       if (vectors) {
/*  57:121 */         lwork = 3 * Math.min(m, n) * Math.min(m, n) + Math.max(
/*  58:122 */           Math.max(m, n), 4 * 
/*  59:123 */           Math.min(m, n) * Math.min(m, n) + 4 * 
/*  60:124 */           Math.min(m, n));
/*  61:    */       } else {
/*  62:129 */         lwork = 3 * Math.min(m, n) * Math.min(m, n) + Math.max(
/*  63:130 */           Math.max(m, n), 5 * 
/*  64:131 */           Math.min(m, n) * Math.min(m, n) + 4 * 
/*  65:132 */           Math.min(m, n));
/*  66:    */       }
/*  67:    */     }
/*  68:    */     else {
/*  69:134 */       lwork = (int)worksize[0];
/*  70:    */     }
/*  71:136 */     lwork = Math.max(lwork, 1);
/*  72:137 */     this.work = new double[lwork];
/*  73:    */   }
/*  74:    */   
/*  75:    */   public static SVD factorize(Matrix A)
/*  76:    */     throws NotConvergedException
/*  77:    */   {
/*  78:149 */     return new SVD(A.numRows(), A.numColumns()).factor(new DenseMatrix(A));
/*  79:    */   }
/*  80:    */   
/*  81:    */   public SVD factor(DenseMatrix A)
/*  82:    */     throws NotConvergedException
/*  83:    */   {
/*  84:162 */     if (A.numRows() != this.m) {
/*  85:163 */       throw new IllegalArgumentException("A.numRows() != m");
/*  86:    */     }
/*  87:164 */     if (A.numColumns() != this.n) {
/*  88:165 */       throw new IllegalArgumentException("A.numColumns() != n");
/*  89:    */     }
/*  90:167 */     intW info = new intW(0);
/*  91:168 */     LAPACK.getInstance().dgesdd(this.job.netlib(), this.m, this.n, A.getData(), 
/*  92:169 */       Matrices.ld(this.m), this.S, this.vectors ? this.U.getData() : new double[0], 
/*  93:170 */       Matrices.ld(this.m), this.vectors ? this.Vt.getData() : new double[0], 
/*  94:171 */       Matrices.ld(this.n), this.work, this.work.length, this.iwork, info);
/*  95:173 */     if (info.val > 0) {
/*  96:174 */       throw new NotConvergedException(NotConvergedException.Reason.Iterations);
/*  97:    */     }
/*  98:176 */     if (info.val < 0) {
/*  99:177 */       throw new IllegalArgumentException();
/* 100:    */     }
/* 101:179 */     return this;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public boolean hasSingularVectors()
/* 105:    */   {
/* 106:186 */     return this.U != null;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public DenseMatrix getU()
/* 110:    */   {
/* 111:196 */     return this.U;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public DenseMatrix getVt()
/* 115:    */   {
/* 116:206 */     return this.Vt;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public double[] getS()
/* 120:    */   {
/* 121:215 */     return this.S;
/* 122:    */   }
/* 123:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.SVD
 * JD-Core Version:    0.7.0.1
 */