/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ public class UnitLowerTriangDenseMatrix
/*   4:    */   extends LowerTriangDenseMatrix
/*   5:    */ {
/*   6:    */   public UnitLowerTriangDenseMatrix(int n)
/*   7:    */   {
/*   8: 39 */     super(n, Diag.Unit);
/*   9:    */   }
/*  10:    */   
/*  11:    */   public UnitLowerTriangDenseMatrix(Matrix A)
/*  12:    */   {
/*  13: 50 */     this(A, true);
/*  14:    */   }
/*  15:    */   
/*  16:    */   public UnitLowerTriangDenseMatrix(Matrix A, boolean deep)
/*  17:    */   {
/*  18: 65 */     super(A, deep, Diag.Unit);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public void add(int row, int column, double value)
/*  22:    */   {
/*  23: 70 */     if (column == row) {
/*  24: 71 */       throw new IllegalArgumentException("column == row");
/*  25:    */     }
/*  26: 72 */     super.add(row, column, value);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public double get(int row, int column)
/*  30:    */   {
/*  31: 77 */     if (column == row) {
/*  32: 78 */       return 1.0D;
/*  33:    */     }
/*  34: 79 */     return super.get(row, column);
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void set(int row, int column, double value)
/*  38:    */   {
/*  39: 84 */     if (column == row) {
/*  40: 85 */       throw new IllegalArgumentException("column == row");
/*  41:    */     }
/*  42: 86 */     super.set(row, column, value);
/*  43:    */   }
/*  44:    */   
/*  45:    */   void copy(Matrix A)
/*  46:    */   {
/*  47: 91 */     for (MatrixEntry e : A) {
/*  48: 92 */       if (e.row() > e.column()) {
/*  49: 93 */         set(e.row(), e.column(), e.get());
/*  50:    */       }
/*  51:    */     }
/*  52:    */   }
/*  53:    */   
/*  54:    */   public UnitLowerTriangDenseMatrix copy()
/*  55:    */   {
/*  56: 98 */     return new UnitLowerTriangDenseMatrix(this);
/*  57:    */   }
/*  58:    */   
/*  59:    */   public Matrix zero()
/*  60:    */   {
/*  61:103 */     throw new UnsupportedOperationException();
/*  62:    */   }
/*  63:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.UnitLowerTriangDenseMatrix
 * JD-Core Version:    0.7.0.1
 */