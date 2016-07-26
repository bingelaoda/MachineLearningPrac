/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ public class UnitUpperTriangDenseMatrix
/*   4:    */   extends UpperTriangDenseMatrix
/*   5:    */ {
/*   6:    */   public UnitUpperTriangDenseMatrix(int n)
/*   7:    */   {
/*   8: 39 */     super(n, Diag.Unit);
/*   9:    */   }
/*  10:    */   
/*  11:    */   public UnitUpperTriangDenseMatrix(Matrix A)
/*  12:    */   {
/*  13: 51 */     this(A, true);
/*  14:    */   }
/*  15:    */   
/*  16:    */   public UnitUpperTriangDenseMatrix(Matrix A, boolean deep)
/*  17:    */   {
/*  18: 67 */     super(A, deep, Diag.Unit);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public void add(int row, int column, double value)
/*  22:    */   {
/*  23: 72 */     if (column == row) {
/*  24: 73 */       throw new IllegalArgumentException("column == row");
/*  25:    */     }
/*  26: 74 */     super.add(row, column, value);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public double get(int row, int column)
/*  30:    */   {
/*  31: 79 */     if (row == column) {
/*  32: 80 */       return 1.0D;
/*  33:    */     }
/*  34: 81 */     return super.get(row, column);
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void set(int row, int column, double value)
/*  38:    */   {
/*  39: 86 */     if (column == row) {
/*  40: 87 */       throw new IllegalArgumentException("column == row");
/*  41:    */     }
/*  42: 88 */     super.set(row, column, value);
/*  43:    */   }
/*  44:    */   
/*  45:    */   void copy(Matrix A)
/*  46:    */   {
/*  47: 93 */     for (MatrixEntry e : A) {
/*  48: 94 */       if (e.row() < e.column()) {
/*  49: 95 */         set(e.row(), e.column(), e.get());
/*  50:    */       }
/*  51:    */     }
/*  52:    */   }
/*  53:    */   
/*  54:    */   public UnitUpperTriangDenseMatrix copy()
/*  55:    */   {
/*  56:100 */     return new UnitUpperTriangDenseMatrix(this);
/*  57:    */   }
/*  58:    */   
/*  59:    */   public Matrix zero()
/*  60:    */   {
/*  61:105 */     throw new UnsupportedOperationException();
/*  62:    */   }
/*  63:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.UnitUpperTriangDenseMatrix
 * JD-Core Version:    0.7.0.1
 */