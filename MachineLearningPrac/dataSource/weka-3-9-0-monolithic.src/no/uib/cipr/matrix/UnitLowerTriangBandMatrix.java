/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ public class UnitLowerTriangBandMatrix
/*   4:    */   extends LowerTriangBandMatrix
/*   5:    */ {
/*   6:    */   public UnitLowerTriangBandMatrix(int n, int kd)
/*   7:    */   {
/*   8: 40 */     super(n, kd, Diag.Unit);
/*   9:    */   }
/*  10:    */   
/*  11:    */   public UnitLowerTriangBandMatrix(Matrix A, int kd)
/*  12:    */   {
/*  13: 54 */     this(A, kd, true);
/*  14:    */   }
/*  15:    */   
/*  16:    */   public UnitLowerTriangBandMatrix(Matrix A, int kd, boolean deep)
/*  17:    */   {
/*  18: 71 */     super(A, kd, deep, Diag.Unit);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public void add(int row, int column, double value)
/*  22:    */   {
/*  23: 76 */     if (row == column) {
/*  24: 77 */       throw new IllegalArgumentException("row == column");
/*  25:    */     }
/*  26: 78 */     super.add(row, column, value);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public double get(int row, int column)
/*  30:    */   {
/*  31: 83 */     if (row == column) {
/*  32: 84 */       return 1.0D;
/*  33:    */     }
/*  34: 85 */     return super.get(row, column);
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void set(int row, int column, double value)
/*  38:    */   {
/*  39: 90 */     if (row == column) {
/*  40: 91 */       throw new IllegalArgumentException("row == column");
/*  41:    */     }
/*  42: 92 */     super.set(row, column, value);
/*  43:    */   }
/*  44:    */   
/*  45:    */   public UnitLowerTriangBandMatrix copy()
/*  46:    */   {
/*  47: 97 */     return new UnitLowerTriangBandMatrix(this, this.kl);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public Matrix zero()
/*  51:    */   {
/*  52:102 */     throw new UnsupportedOperationException();
/*  53:    */   }
/*  54:    */   
/*  55:    */   void copy(Matrix A)
/*  56:    */   {
/*  57:107 */     for (MatrixEntry e : A) {
/*  58:108 */       if ((inBand(e.row(), e.column())) && (e.row() != e.column())) {
/*  59:109 */         set(e.row(), e.column(), e.get());
/*  60:    */       }
/*  61:    */     }
/*  62:    */   }
/*  63:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.UnitLowerTriangBandMatrix
 * JD-Core Version:    0.7.0.1
 */