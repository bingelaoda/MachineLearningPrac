/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.LAPACK;
/*   4:    */ import org.netlib.util.intW;
/*   5:    */ 
/*   6:    */ public class SymmTridiagEVD
/*   7:    */   extends SymmEVD
/*   8:    */ {
/*   9:    */   private final double[] work;
/*  10:    */   private final int[] iwork;
/*  11:    */   private final JobEigRange range;
/*  12:    */   private final int[] isuppz;
/*  13:    */   private final double abstol;
/*  14:    */   
/*  15:    */   public SymmTridiagEVD(int n)
/*  16:    */   {
/*  17: 65 */     this(n, true);
/*  18:    */   }
/*  19:    */   
/*  20:    */   public SymmTridiagEVD(int n, double abstol)
/*  21:    */   {
/*  22: 78 */     this(n, true, abstol);
/*  23:    */   }
/*  24:    */   
/*  25:    */   public SymmTridiagEVD(int n, boolean vectors)
/*  26:    */   {
/*  27: 92 */     this(n, vectors, LAPACK.getInstance().dlamch("Safe minimum"));
/*  28:    */   }
/*  29:    */   
/*  30:    */   public SymmTridiagEVD(int n, boolean vectors, double abstol)
/*  31:    */   {
/*  32:107 */     super(n, vectors);
/*  33:108 */     this.abstol = abstol;
/*  34:    */     
/*  35:110 */     this.range = JobEigRange.All;
/*  36:111 */     this.isuppz = new int[2 * Math.max(1, n)];
/*  37:    */     
/*  38:    */ 
/*  39:114 */     double[] worksize = new double[1];
/*  40:115 */     int[] iworksize = new int[1];
/*  41:116 */     intW info = new intW(0);
/*  42:117 */     LAPACK.getInstance().dstevr(this.job.netlib(), this.range.netlib(), n, new double[0], new double[0], 0.0D, 0.0D, 0, 0, abstol, new intW(1), new double[0], new double[0], 
/*  43:    */     
/*  44:119 */       Matrices.ld(n), this.isuppz, worksize, -1, iworksize, -1, info);
/*  45:    */     
/*  46:    */ 
/*  47:    */ 
/*  48:123 */     int lwork = 0;int liwork = 0;
/*  49:124 */     if (info.val != 0)
/*  50:    */     {
/*  51:125 */       lwork = 20 * n;
/*  52:126 */       liwork = 10 * n;
/*  53:    */     }
/*  54:    */     else
/*  55:    */     {
/*  56:128 */       lwork = (int)worksize[0];
/*  57:129 */       liwork = iworksize[0];
/*  58:    */     }
/*  59:132 */     lwork = Math.max(1, lwork);
/*  60:133 */     liwork = Math.max(1, liwork);
/*  61:134 */     this.work = new double[lwork];
/*  62:135 */     this.iwork = new int[liwork];
/*  63:    */   }
/*  64:    */   
/*  65:    */   public static SymmTridiagEVD factorize(Matrix A)
/*  66:    */     throws NotConvergedException
/*  67:    */   {
/*  68:150 */     return new SymmTridiagEVD(A.numRows()).factor(new SymmTridiagMatrix(A));
/*  69:    */   }
/*  70:    */   
/*  71:    */   public SymmTridiagEVD factor(SymmTridiagMatrix A)
/*  72:    */     throws NotConvergedException
/*  73:    */   {
/*  74:163 */     if (A.numRows() != this.n) {
/*  75:164 */       throw new IllegalArgumentException("A.numRows() != n");
/*  76:    */     }
/*  77:166 */     intW info = new intW(0);
/*  78:167 */     LAPACK.getInstance().dstevr(this.job.netlib(), this.range.netlib(), this.n, A
/*  79:168 */       .getDiagonal(), A.getOffDiagonal(), 0.0D, 0.0D, 0, 0, this.abstol, new intW(1), this.w, this.job == JobEig.All ? this.Z
/*  80:    */       
/*  81:170 */       .getData() : new double[0], 
/*  82:171 */       Matrices.ld(this.n), this.isuppz, this.work, this.work.length, this.iwork, this.iwork.length, info);
/*  83:174 */     if (info.val > 0) {
/*  84:175 */       throw new NotConvergedException(NotConvergedException.Reason.Iterations);
/*  85:    */     }
/*  86:177 */     if (info.val < 0) {
/*  87:178 */       throw new IllegalArgumentException();
/*  88:    */     }
/*  89:180 */     return this;
/*  90:    */   }
/*  91:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.SymmTridiagEVD
 * JD-Core Version:    0.7.0.1
 */