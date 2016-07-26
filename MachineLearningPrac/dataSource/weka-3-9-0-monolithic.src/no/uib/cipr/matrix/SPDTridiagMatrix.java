/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */ import com.github.fommil.netlib.LAPACK;
/*  4:   */ import org.netlib.util.intW;
/*  5:   */ 
/*  6:   */ public class SPDTridiagMatrix
/*  7:   */   extends SymmTridiagMatrix
/*  8:   */ {
/*  9:   */   public SPDTridiagMatrix(int n)
/* 10:   */   {
/* 11:41 */     super(n);
/* 12:   */   }
/* 13:   */   
/* 14:   */   public SPDTridiagMatrix(Matrix A)
/* 15:   */   {
/* 16:52 */     super(A);
/* 17:   */   }
/* 18:   */   
/* 19:   */   public SPDTridiagMatrix(Matrix A, boolean deep)
/* 20:   */   {
/* 21:66 */     super(A, deep);
/* 22:   */   }
/* 23:   */   
/* 24:   */   public SPDTridiagMatrix copy()
/* 25:   */   {
/* 26:71 */     return new SPDTridiagMatrix(this);
/* 27:   */   }
/* 28:   */   
/* 29:   */   public Matrix solve(Matrix B, Matrix X)
/* 30:   */   {
/* 31:76 */     if (!(X instanceof DenseMatrix)) {
/* 32:77 */       throw new UnsupportedOperationException("X must be a DenseMatrix");
/* 33:   */     }
/* 34:79 */     checkSolve(B, X);
/* 35:   */     
/* 36:81 */     double[] Xd = ((DenseMatrix)X).getData();
/* 37:   */     
/* 38:83 */     X.set(B);
/* 39:   */     
/* 40:85 */     intW info = new intW(0);
/* 41:86 */     LAPACK.getInstance().dptsv(this.numRows, X.numColumns(), (double[])this.diag.clone(), 
/* 42:87 */       (double[])this.offDiag.clone(), Xd, Matrices.ld(this.numRows), info);
/* 43:89 */     if (info.val > 0) {
/* 44:90 */       throw new MatrixNotSPDException();
/* 45:   */     }
/* 46:91 */     if (info.val < 0) {
/* 47:92 */       throw new IllegalArgumentException();
/* 48:   */     }
/* 49:94 */     return X;
/* 50:   */   }
/* 51:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.SPDTridiagMatrix
 * JD-Core Version:    0.7.0.1
 */