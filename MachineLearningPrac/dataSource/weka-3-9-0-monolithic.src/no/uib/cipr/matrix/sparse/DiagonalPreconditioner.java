/*  1:   */ package no.uib.cipr.matrix.sparse;
/*  2:   */ 
/*  3:   */ import no.uib.cipr.matrix.DenseVector;
/*  4:   */ import no.uib.cipr.matrix.Matrix;
/*  5:   */ import no.uib.cipr.matrix.Vector;
/*  6:   */ 
/*  7:   */ public class DiagonalPreconditioner
/*  8:   */   implements Preconditioner
/*  9:   */ {
/* 10:   */   private double[] invdiag;
/* 11:   */   
/* 12:   */   public DiagonalPreconditioner(int n)
/* 13:   */   {
/* 14:44 */     this.invdiag = new double[n];
/* 15:   */   }
/* 16:   */   
/* 17:   */   public Vector apply(Vector b, Vector x)
/* 18:   */   {
/* 19:48 */     if ((!(x instanceof DenseVector)) || (!(b instanceof DenseVector))) {
/* 20:49 */       throw new IllegalArgumentException("Vector must be DenseVectors");
/* 21:   */     }
/* 22:51 */     double[] xd = ((DenseVector)x).getData();
/* 23:52 */     double[] bd = ((DenseVector)b).getData();
/* 24:54 */     for (int i = 0; i < this.invdiag.length; i++) {
/* 25:55 */       bd[i] *= this.invdiag[i];
/* 26:   */     }
/* 27:57 */     return x;
/* 28:   */   }
/* 29:   */   
/* 30:   */   public Vector transApply(Vector b, Vector x)
/* 31:   */   {
/* 32:61 */     return apply(b, x);
/* 33:   */   }
/* 34:   */   
/* 35:   */   public void setMatrix(Matrix A)
/* 36:   */   {
/* 37:65 */     if (A.numRows() != this.invdiag.length) {
/* 38:66 */       throw new IllegalArgumentException("Matrix size differs from preconditioner size");
/* 39:   */     }
/* 40:69 */     for (int i = 0; i < this.invdiag.length; i++)
/* 41:   */     {
/* 42:70 */       this.invdiag[i] = A.get(i, i);
/* 43:71 */       if (this.invdiag[i] == 0.0D) {
/* 44:72 */         throw new RuntimeException("Zero diagonal on row " + (i + 1));
/* 45:   */       }
/* 46:74 */       this.invdiag[i] = (1.0D / this.invdiag[i]);
/* 47:   */     }
/* 48:   */   }
/* 49:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.DiagonalPreconditioner
 * JD-Core Version:    0.7.0.1
 */