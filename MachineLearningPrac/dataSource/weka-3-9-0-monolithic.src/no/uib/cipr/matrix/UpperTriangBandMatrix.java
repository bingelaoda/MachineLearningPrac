/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ public class UpperTriangBandMatrix
/*   4:    */   extends AbstractTriangBandMatrix
/*   5:    */ {
/*   6:    */   public UpperTriangBandMatrix(int n, int kd)
/*   7:    */   {
/*   8: 39 */     super(n, 0, kd, UpLo.Upper, Diag.NonUnit);
/*   9:    */   }
/*  10:    */   
/*  11:    */   public UpperTriangBandMatrix(Matrix A, int kd)
/*  12:    */   {
/*  13: 53 */     this(A, kd, true);
/*  14:    */   }
/*  15:    */   
/*  16:    */   public UpperTriangBandMatrix(Matrix A, int kd, boolean deep)
/*  17:    */   {
/*  18: 70 */     super(A, 0, kd, deep, UpLo.Upper, Diag.NonUnit);
/*  19:    */   }
/*  20:    */   
/*  21:    */   UpperTriangBandMatrix(int n, int kd, Diag diag)
/*  22:    */   {
/*  23: 83 */     super(n, 0, kd, UpLo.Upper, diag);
/*  24:    */   }
/*  25:    */   
/*  26:    */   UpperTriangBandMatrix(Matrix A, int kd, boolean deep, Diag diag)
/*  27:    */   {
/*  28:100 */     super(A, 0, kd, deep, UpLo.Upper, diag);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public UpperTriangBandMatrix copy()
/*  32:    */   {
/*  33:105 */     return new UpperTriangBandMatrix(this, this.ku);
/*  34:    */   }
/*  35:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.UpperTriangBandMatrix
 * JD-Core Version:    0.7.0.1
 */