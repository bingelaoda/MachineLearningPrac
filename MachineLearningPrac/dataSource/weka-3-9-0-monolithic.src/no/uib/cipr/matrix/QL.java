/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.LAPACK;
/*   4:    */ import org.netlib.util.intW;
/*   5:    */ 
/*   6:    */ public class QL
/*   7:    */   extends OrthogonalComputer
/*   8:    */ {
/*   9:    */   public QL(int m, int n)
/*  10:    */   {
/*  11: 41 */     super(m, n, false);
/*  12: 43 */     if (n > m) {
/*  13: 44 */       throw new IllegalArgumentException("n > m");
/*  14:    */     }
/*  15: 50 */     this.work = new double[1];
/*  16: 51 */     intW info = new intW(0);
/*  17: 52 */     LAPACK.getInstance().dgeqlf(m, n, new double[0], Matrices.ld(m), new double[0], this.work, -1, info);
/*  18:    */     int lwork;
/*  19: 55 */     if (info.val != 0) {
/*  20: 56 */       lwork = n;
/*  21:    */     } else {
/*  22: 58 */       lwork = (int)this.work[0];
/*  23:    */     }
/*  24: 59 */     int lwork = Math.max(1, lwork);
/*  25: 60 */     this.work = new double[lwork];
/*  26:    */     
/*  27:    */ 
/*  28:    */ 
/*  29:    */ 
/*  30: 65 */     this.workGen = new double[1];
/*  31: 66 */     intW info = new intW(0);
/*  32: 67 */     LAPACK.getInstance().dorgql(m, n, this.k, new double[0], Matrices.ld(m), new double[0], this.workGen, -1, info);
/*  33: 70 */     if (info.val != 0) {
/*  34: 71 */       lwork = n;
/*  35:    */     } else {
/*  36: 73 */       lwork = (int)this.workGen[0];
/*  37:    */     }
/*  38: 74 */     lwork = Math.max(1, lwork);
/*  39: 75 */     this.workGen = new double[lwork];
/*  40:    */   }
/*  41:    */   
/*  42:    */   public static QL factorize(Matrix A)
/*  43:    */   {
/*  44: 88 */     return new QL(A.numRows(), A.numColumns()).factor(new DenseMatrix(A));
/*  45:    */   }
/*  46:    */   
/*  47:    */   public QL factor(DenseMatrix A)
/*  48:    */   {
/*  49: 94 */     if (this.Q.numRows() != A.numRows()) {
/*  50: 95 */       throw new IllegalArgumentException("Q.numRows() != A.numRows()");
/*  51:    */     }
/*  52: 96 */     if (this.Q.numColumns() != A.numColumns()) {
/*  53: 97 */       throw new IllegalArgumentException("Q.numColumns() != A.numColumns()");
/*  54:    */     }
/*  55: 99 */     if (this.L == null) {
/*  56:100 */       throw new IllegalArgumentException("L == null");
/*  57:    */     }
/*  58:106 */     intW info = new intW(0);
/*  59:107 */     LAPACK.getInstance().dgeqlf(this.m, this.n, A.getData(), Matrices.ld(this.m), this.tau, this.work, this.work.length, info);
/*  60:110 */     if (info.val < 0) {
/*  61:111 */       throw new IllegalArgumentException();
/*  62:    */     }
/*  63:113 */     this.L.zero();
/*  64:114 */     for (MatrixEntry e : A) {
/*  65:115 */       if (e.row() >= this.m - this.n + e.column()) {
/*  66:116 */         this.L.set(e.row() - (this.m - this.n), e.column(), e.get());
/*  67:    */       }
/*  68:    */     }
/*  69:121 */     info.val = 0;
/*  70:122 */     LAPACK.getInstance().dorgql(this.m, this.n, this.k, A.getData(), Matrices.ld(this.m), this.tau, this.workGen, this.workGen.length, info);
/*  71:125 */     if (info.val < 0) {
/*  72:126 */       throw new IllegalArgumentException();
/*  73:    */     }
/*  74:128 */     this.Q.set(A);
/*  75:    */     
/*  76:130 */     return this;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public LowerTriangDenseMatrix getL()
/*  80:    */   {
/*  81:137 */     return this.L;
/*  82:    */   }
/*  83:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.QL
 * JD-Core Version:    0.7.0.1
 */