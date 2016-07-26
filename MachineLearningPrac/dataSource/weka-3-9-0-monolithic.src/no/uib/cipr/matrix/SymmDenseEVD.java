/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.LAPACK;
/*   4:    */ import org.netlib.util.intW;
/*   5:    */ 
/*   6:    */ public class SymmDenseEVD
/*   7:    */   extends SymmEVD
/*   8:    */ {
/*   9:    */   private final double[] work;
/*  10:    */   private final int[] iwork;
/*  11:    */   private final UpLo uplo;
/*  12:    */   private final JobEigRange range;
/*  13:    */   private final int[] isuppz;
/*  14:    */   private final double abstol;
/*  15:    */   
/*  16:    */   public SymmDenseEVD(int n, boolean upper)
/*  17:    */   {
/*  18: 73 */     this(n, upper, true, LAPACK.getInstance().dlamch("Safe minimum"));
/*  19:    */   }
/*  20:    */   
/*  21:    */   public SymmDenseEVD(int n, boolean upper, double abstol)
/*  22:    */   {
/*  23: 89 */     this(n, upper, true, abstol);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public SymmDenseEVD(int n, boolean upper, boolean vectors)
/*  27:    */   {
/*  28:106 */     this(n, upper, vectors, LAPACK.getInstance().dlamch("Safe minimum"));
/*  29:    */   }
/*  30:    */   
/*  31:    */   public SymmDenseEVD(int n, boolean upper, boolean vectors, double abstol)
/*  32:    */   {
/*  33:124 */     super(n, vectors);
/*  34:125 */     this.abstol = abstol;
/*  35:    */     
/*  36:127 */     this.uplo = (upper ? UpLo.Upper : UpLo.Lower);
/*  37:128 */     this.range = JobEigRange.All;
/*  38:129 */     this.isuppz = new int[2 * Math.max(1, n)];
/*  39:    */     
/*  40:    */ 
/*  41:132 */     double[] worksize = new double[1];
/*  42:133 */     int[] iworksize = new int[1];
/*  43:134 */     intW info = new intW(0);
/*  44:135 */     LAPACK.getInstance().dsyevr(this.job.netlib(), this.range.netlib(), this.uplo
/*  45:136 */       .netlib(), n, new double[0], Matrices.ld(n), 0.0D, 0.0D, 0, 0, abstol, new intW(1), new double[0], new double[0], 
/*  46:    */       
/*  47:138 */       Matrices.ld(n), this.isuppz, worksize, -1, iworksize, -1, info);
/*  48:    */     
/*  49:    */ 
/*  50:141 */     int lwork = 0;int liwork = 0;
/*  51:142 */     if (info.val != 0)
/*  52:    */     {
/*  53:143 */       lwork = 26 * n;
/*  54:144 */       liwork = 10 * n;
/*  55:    */     }
/*  56:    */     else
/*  57:    */     {
/*  58:146 */       lwork = (int)worksize[0];
/*  59:147 */       liwork = iworksize[0];
/*  60:    */     }
/*  61:150 */     lwork = Math.max(1, lwork);
/*  62:151 */     liwork = Math.max(1, liwork);
/*  63:152 */     this.work = new double[lwork];
/*  64:153 */     this.iwork = new int[liwork];
/*  65:    */   }
/*  66:    */   
/*  67:    */   public static SymmDenseEVD factorize(Matrix A)
/*  68:    */     throws NotConvergedException
/*  69:    */   {
/*  70:168 */     return new SymmDenseEVD(A.numRows(), true).factor(new UpperSymmDenseMatrix(A));
/*  71:    */   }
/*  72:    */   
/*  73:    */   public SymmDenseEVD factor(LowerSymmDenseMatrix A)
/*  74:    */     throws NotConvergedException
/*  75:    */   {
/*  76:181 */     if (this.uplo != UpLo.Lower) {
/*  77:182 */       throw new IllegalArgumentException("Eigenvalue computer configured for lower-symmetrical matrices");
/*  78:    */     }
/*  79:185 */     return factor(A, A.getData());
/*  80:    */   }
/*  81:    */   
/*  82:    */   public SymmDenseEVD factor(UpperSymmDenseMatrix A)
/*  83:    */     throws NotConvergedException
/*  84:    */   {
/*  85:198 */     if (this.uplo != UpLo.Upper) {
/*  86:199 */       throw new IllegalArgumentException("Eigenvalue computer configured for upper-symmetrical matrices");
/*  87:    */     }
/*  88:202 */     return factor(A, A.getData());
/*  89:    */   }
/*  90:    */   
/*  91:    */   private SymmDenseEVD factor(Matrix A, double[] data)
/*  92:    */     throws NotConvergedException
/*  93:    */   {
/*  94:207 */     if (A.numRows() != this.n) {
/*  95:208 */       throw new IllegalArgumentException("A.numRows() != n");
/*  96:    */     }
/*  97:210 */     intW info = new intW(0);
/*  98:211 */     LAPACK.getInstance().dsyevr(this.job.netlib(), this.range.netlib(), this.uplo
/*  99:212 */       .netlib(), this.n, data, Matrices.ld(this.n), 0.0D, 0.0D, 0, 0, this.abstol, new intW(1), this.w, this.job == JobEig.All ? this.Z
/* 100:    */       
/* 101:214 */       .getData() : new double[0], 
/* 102:215 */       Matrices.ld(this.n), this.isuppz, this.work, this.work.length, this.iwork, this.iwork.length, info);
/* 103:218 */     if (info.val > 0) {
/* 104:219 */       throw new NotConvergedException(NotConvergedException.Reason.Iterations);
/* 105:    */     }
/* 106:221 */     if (info.val < 0) {
/* 107:222 */       throw new IllegalArgumentException();
/* 108:    */     }
/* 109:224 */     return this;
/* 110:    */   }
/* 111:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.SymmDenseEVD
 * JD-Core Version:    0.7.0.1
 */