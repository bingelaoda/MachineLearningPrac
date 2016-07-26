/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ public class LowerTriangBandMatrix
/*   4:    */   extends AbstractTriangBandMatrix
/*   5:    */ {
/*   6:    */   public LowerTriangBandMatrix(int n, int kd)
/*   7:    */   {
/*   8: 39 */     super(n, kd, 0, UpLo.Lower, Diag.NonUnit);
/*   9:    */   }
/*  10:    */   
/*  11:    */   public LowerTriangBandMatrix(Matrix A, int kd)
/*  12:    */   {
/*  13: 53 */     this(A, kd, true);
/*  14:    */   }
/*  15:    */   
/*  16:    */   public LowerTriangBandMatrix(Matrix A, int kd, boolean deep)
/*  17:    */   {
/*  18: 70 */     super(A, kd, 0, deep, UpLo.Lower, Diag.NonUnit);
/*  19:    */   }
/*  20:    */   
/*  21:    */   LowerTriangBandMatrix(int n, int kd, Diag diag)
/*  22:    */   {
/*  23: 83 */     super(n, kd, 0, UpLo.Lower, diag);
/*  24:    */   }
/*  25:    */   
/*  26:    */   LowerTriangBandMatrix(Matrix A, int kd, boolean deep, Diag diag)
/*  27:    */   {
/*  28:100 */     super(A, kd, 0, deep, UpLo.Lower, diag);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public LowerTriangBandMatrix copy()
/*  32:    */   {
/*  33:105 */     return new LowerTriangBandMatrix(this, this.kl);
/*  34:    */   }
/*  35:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.LowerTriangBandMatrix
 * JD-Core Version:    0.7.0.1
 */