/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ public class LowerTriangDenseMatrix
/*   4:    */   extends AbstractTriangDenseMatrix
/*   5:    */ {
/*   6:    */   public LowerTriangDenseMatrix(int n)
/*   7:    */   {
/*   8: 39 */     super(n, UpLo.Lower, Diag.NonUnit);
/*   9:    */   }
/*  10:    */   
/*  11:    */   LowerTriangDenseMatrix(int n, Diag diag)
/*  12:    */   {
/*  13: 50 */     super(n, UpLo.Lower, diag);
/*  14:    */   }
/*  15:    */   
/*  16:    */   public LowerTriangDenseMatrix(Matrix A)
/*  17:    */   {
/*  18: 60 */     this(A, Math.min(A.numRows(), A.numColumns()));
/*  19:    */   }
/*  20:    */   
/*  21:    */   public LowerTriangDenseMatrix(Matrix A, boolean deep)
/*  22:    */   {
/*  23: 74 */     this(A, Math.min(A.numRows(), A.numColumns()), deep);
/*  24:    */   }
/*  25:    */   
/*  26:    */   LowerTriangDenseMatrix(Matrix A, boolean deep, Diag diag)
/*  27:    */   {
/*  28: 88 */     this(A, Math.min(A.numRows(), A.numColumns()), deep, diag);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public LowerTriangDenseMatrix(Matrix A, int k)
/*  32:    */   {
/*  33:101 */     this(A, k, true);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public LowerTriangDenseMatrix(Matrix A, int k, boolean deep)
/*  37:    */   {
/*  38:118 */     super(A, k, deep, UpLo.Lower, Diag.NonUnit);
/*  39:    */   }
/*  40:    */   
/*  41:    */   LowerTriangDenseMatrix(Matrix A, int k, boolean deep, Diag diag)
/*  42:    */   {
/*  43:135 */     super(A, k, deep, UpLo.Lower, diag);
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void add(int row, int column, double value)
/*  47:    */   {
/*  48:140 */     if (column > row) {
/*  49:141 */       throw new IllegalArgumentException("column > row");
/*  50:    */     }
/*  51:142 */     super.add(row, column, value);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public double get(int row, int column)
/*  55:    */   {
/*  56:147 */     if (column > row) {
/*  57:148 */       return 0.0D;
/*  58:    */     }
/*  59:149 */     return super.get(row, column);
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void set(int row, int column, double value)
/*  63:    */   {
/*  64:154 */     if (column > row) {
/*  65:155 */       throw new IllegalArgumentException("column > row");
/*  66:    */     }
/*  67:156 */     super.set(row, column, value);
/*  68:    */   }
/*  69:    */   
/*  70:    */   public LowerTriangDenseMatrix copy()
/*  71:    */   {
/*  72:161 */     return new LowerTriangDenseMatrix(this);
/*  73:    */   }
/*  74:    */   
/*  75:    */   void copy(Matrix A)
/*  76:    */   {
/*  77:166 */     for (MatrixEntry e : A) {
/*  78:167 */       if (e.row() >= e.column()) {
/*  79:168 */         set(e.row(), e.column(), e.get());
/*  80:    */       }
/*  81:    */     }
/*  82:    */   }
/*  83:    */   
/*  84:    */   public Matrix set(Matrix A)
/*  85:    */   {
/*  86:173 */     zero();
/*  87:174 */     copy(A);
/*  88:175 */     return this;
/*  89:    */   }
/*  90:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.LowerTriangDenseMatrix
 * JD-Core Version:    0.7.0.1
 */