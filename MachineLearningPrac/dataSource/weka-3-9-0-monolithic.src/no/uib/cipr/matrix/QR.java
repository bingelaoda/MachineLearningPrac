/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.LAPACK;
/*   4:    */ import org.netlib.util.intW;
/*   5:    */ 
/*   6:    */ public class QR
/*   7:    */   extends OrthogonalComputer
/*   8:    */ {
/*   9:    */   public QR(int m, int n)
/*  10:    */   {
/*  11: 41 */     super(m, n, true);
/*  12: 43 */     if (n > m) {
/*  13: 44 */       throw new IllegalArgumentException("n > m");
/*  14:    */     }
/*  15: 50 */     this.work = new double[1];
/*  16: 51 */     intW info = new intW(0);
/*  17: 52 */     LAPACK.getInstance().dgeqrf(m, n, new double[0], Matrices.ld(m), new double[0], this.work, -1, info);
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
/*  32: 67 */     LAPACK.getInstance().dorgqr(m, n, this.k, new double[0], Matrices.ld(m), new double[0], this.workGen, -1, info);
/*  33: 70 */     if (info.val != 0) {
/*  34: 71 */       lwork = n;
/*  35:    */     } else {
/*  36: 73 */       lwork = (int)this.workGen[0];
/*  37:    */     }
/*  38: 74 */     lwork = Math.max(1, lwork);
/*  39: 75 */     this.workGen = new double[lwork];
/*  40:    */   }
/*  41:    */   
/*  42:    */   public static QR factorize(Matrix A)
/*  43:    */   {
/*  44: 88 */     return new QR(A.numRows(), A.numColumns()).factor(new DenseMatrix(A));
/*  45:    */   }
/*  46:    */   
/*  47:    */   public QR factor(DenseMatrix A)
/*  48:    */   {
/*  49: 94 */     if (this.Q.numRows() != A.numRows()) {
/*  50: 95 */       throw new IllegalArgumentException("Q.numRows() != A.numRows()");
/*  51:    */     }
/*  52: 96 */     if (this.Q.numColumns() != A.numColumns()) {
/*  53: 97 */       throw new IllegalArgumentException("Q.numColumns() != A.numColumns()");
/*  54:    */     }
/*  55: 99 */     if (this.R == null) {
/*  56:100 */       throw new IllegalArgumentException("R == null");
/*  57:    */     }
/*  58:105 */     intW info = new intW(0);
/*  59:106 */     LAPACK.getInstance().dgeqrf(this.m, this.n, A.getData(), Matrices.ld(this.m), this.tau, this.work, this.work.length, info);
/*  60:109 */     if (info.val < 0) {
/*  61:110 */       throw new IllegalArgumentException();
/*  62:    */     }
/*  63:112 */     this.R.zero();
/*  64:113 */     for (MatrixEntry e : A) {
/*  65:114 */       if (e.row() <= e.column()) {
/*  66:115 */         this.R.set(e.row(), e.column(), e.get());
/*  67:    */       }
/*  68:    */     }
/*  69:120 */     info.val = 0;
/*  70:121 */     LAPACK.getInstance().dorgqr(this.m, this.n, this.k, A.getData(), Matrices.ld(this.m), this.tau, this.workGen, this.workGen.length, info);
/*  71:124 */     if (info.val < 0) {
/*  72:125 */       throw new IllegalArgumentException();
/*  73:    */     }
/*  74:127 */     this.Q.set(A);
/*  75:    */     
/*  76:129 */     return this;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public UpperTriangDenseMatrix getR()
/*  80:    */   {
/*  81:136 */     return this.R;
/*  82:    */   }
/*  83:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.QR
 * JD-Core Version:    0.7.0.1
 */