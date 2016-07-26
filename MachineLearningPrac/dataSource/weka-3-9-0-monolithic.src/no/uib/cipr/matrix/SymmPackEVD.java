/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.LAPACK;
/*   4:    */ import org.netlib.util.intW;
/*   5:    */ 
/*   6:    */ public class SymmPackEVD
/*   7:    */   extends SymmEVD
/*   8:    */ {
/*   9:    */   private final double[] work;
/*  10:    */   private final int[] iwork;
/*  11:    */   private final UpLo uplo;
/*  12:    */   
/*  13:    */   public SymmPackEVD(int n, boolean upper)
/*  14:    */   {
/*  15: 57 */     this(n, upper, true);
/*  16:    */   }
/*  17:    */   
/*  18:    */   public SymmPackEVD(int n, boolean upper, boolean vectors)
/*  19:    */   {
/*  20: 73 */     super(n, vectors);
/*  21:    */     
/*  22: 75 */     this.uplo = (upper ? UpLo.Upper : UpLo.Lower);
/*  23:    */     
/*  24:    */ 
/*  25: 78 */     double[] worksize = new double[1];
/*  26: 79 */     int[] iworksize = new int[1];
/*  27: 80 */     intW info = new intW(0);
/*  28: 81 */     LAPACK.getInstance().dspevd(this.job.netlib(), this.uplo.netlib(), n, new double[0], new double[0], new double[0], 
/*  29: 82 */       Matrices.ld(n), worksize, -1, iworksize, -1, info);
/*  30:    */     
/*  31:    */ 
/*  32:    */ 
/*  33: 86 */     int lwork = 0;int liwork = 0;
/*  34: 87 */     if (info.val != 0)
/*  35:    */     {
/*  36: 88 */       if (this.job == JobEig.All)
/*  37:    */       {
/*  38: 89 */         lwork = 1 + 6 * n + n * n;
/*  39: 90 */         liwork = 3 + 5 * n;
/*  40:    */       }
/*  41:    */       else
/*  42:    */       {
/*  43: 92 */         lwork = 2 * n;
/*  44: 93 */         liwork = 1;
/*  45:    */       }
/*  46:    */     }
/*  47:    */     else
/*  48:    */     {
/*  49: 96 */       lwork = (int)worksize[0];
/*  50: 97 */       liwork = iworksize[0];
/*  51:    */     }
/*  52:100 */     lwork = Math.max(1, lwork);
/*  53:101 */     liwork = Math.max(1, liwork);
/*  54:102 */     this.work = new double[lwork];
/*  55:103 */     this.iwork = new int[liwork];
/*  56:    */   }
/*  57:    */   
/*  58:    */   public static SymmPackEVD factorize(Matrix A)
/*  59:    */     throws NotConvergedException
/*  60:    */   {
/*  61:118 */     return new SymmPackEVD(A.numRows(), true).factor(new UpperSymmPackMatrix(A));
/*  62:    */   }
/*  63:    */   
/*  64:    */   public SymmPackEVD factor(LowerSymmPackMatrix A)
/*  65:    */     throws NotConvergedException
/*  66:    */   {
/*  67:131 */     if (this.uplo != UpLo.Lower) {
/*  68:132 */       throw new IllegalArgumentException("Eigenvalue computer configured for lower-symmetrical matrices");
/*  69:    */     }
/*  70:135 */     return factor(A, A.getData());
/*  71:    */   }
/*  72:    */   
/*  73:    */   public SymmPackEVD factor(UpperSymmPackMatrix A)
/*  74:    */     throws NotConvergedException
/*  75:    */   {
/*  76:148 */     if (this.uplo != UpLo.Upper) {
/*  77:149 */       throw new IllegalArgumentException("Eigenvalue computer configured for upper-symmetrical matrices");
/*  78:    */     }
/*  79:152 */     return factor(A, A.getData());
/*  80:    */   }
/*  81:    */   
/*  82:    */   private SymmPackEVD factor(Matrix A, double[] data)
/*  83:    */     throws NotConvergedException
/*  84:    */   {
/*  85:157 */     if (A.numRows() != this.n) {
/*  86:158 */       throw new IllegalArgumentException("A.numRows() != n");
/*  87:    */     }
/*  88:160 */     intW info = new intW(0);
/*  89:161 */     LAPACK.getInstance().dspevd(this.job.netlib(), this.uplo.netlib(), this.n, data, this.w, this.job == JobEig.All ? this.Z
/*  90:162 */       .getData() : new double[0], 
/*  91:163 */       Matrices.ld(this.n), this.work, this.work.length, this.iwork, this.iwork.length, info);
/*  92:165 */     if (info.val > 0) {
/*  93:166 */       throw new NotConvergedException(NotConvergedException.Reason.Iterations);
/*  94:    */     }
/*  95:168 */     if (info.val < 0) {
/*  96:169 */       throw new IllegalArgumentException();
/*  97:    */     }
/*  98:171 */     return this;
/*  99:    */   }
/* 100:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.SymmPackEVD
 * JD-Core Version:    0.7.0.1
 */