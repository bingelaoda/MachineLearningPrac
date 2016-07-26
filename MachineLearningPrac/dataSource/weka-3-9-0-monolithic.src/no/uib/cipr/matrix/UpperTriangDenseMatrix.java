/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ public class UpperTriangDenseMatrix
/*   4:    */   extends AbstractTriangDenseMatrix
/*   5:    */ {
/*   6:    */   public UpperTriangDenseMatrix(int n)
/*   7:    */   {
/*   8: 39 */     super(n, UpLo.Upper, Diag.NonUnit);
/*   9:    */   }
/*  10:    */   
/*  11:    */   UpperTriangDenseMatrix(int n, Diag diag)
/*  12:    */   {
/*  13: 50 */     super(n, UpLo.Upper, diag);
/*  14:    */   }
/*  15:    */   
/*  16:    */   public UpperTriangDenseMatrix(Matrix A)
/*  17:    */   {
/*  18: 60 */     this(A, Math.min(A.numRows(), A.numColumns()));
/*  19:    */   }
/*  20:    */   
/*  21:    */   public UpperTriangDenseMatrix(Matrix A, boolean deep)
/*  22:    */   {
/*  23: 73 */     this(A, Math.min(A.numRows(), A.numColumns()), deep);
/*  24:    */   }
/*  25:    */   
/*  26:    */   UpperTriangDenseMatrix(Matrix A, boolean deep, Diag diag)
/*  27:    */   {
/*  28: 86 */     this(A, Math.min(A.numRows(), A.numColumns()), deep, diag);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public UpperTriangDenseMatrix(Matrix A, int k)
/*  32:    */   {
/*  33: 99 */     this(A, k, true);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public UpperTriangDenseMatrix(Matrix A, int k, boolean deep)
/*  37:    */   {
/*  38:115 */     super(A, k, deep, UpLo.Upper, Diag.NonUnit);
/*  39:    */   }
/*  40:    */   
/*  41:    */   UpperTriangDenseMatrix(Matrix A, int k, boolean deep, Diag diag)
/*  42:    */   {
/*  43:131 */     super(A, k, deep, UpLo.Upper, diag);
/*  44:    */   }
/*  45:    */   
/*  46:    */   public void add(int row, int column, double value)
/*  47:    */   {
/*  48:136 */     if (row > column) {
/*  49:137 */       throw new IllegalArgumentException("row > column");
/*  50:    */     }
/*  51:138 */     super.add(row, column, value);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public double get(int row, int column)
/*  55:    */   {
/*  56:143 */     if (row > column) {
/*  57:144 */       return 0.0D;
/*  58:    */     }
/*  59:145 */     return super.get(row, column);
/*  60:    */   }
/*  61:    */   
/*  62:    */   public void set(int row, int column, double value)
/*  63:    */   {
/*  64:150 */     if (row > column) {
/*  65:151 */       throw new IllegalArgumentException("row > column");
/*  66:    */     }
/*  67:152 */     super.set(row, column, value);
/*  68:    */   }
/*  69:    */   
/*  70:    */   void copy(Matrix A)
/*  71:    */   {
/*  72:157 */     for (MatrixEntry e : A) {
/*  73:158 */       if (e.row() <= e.column()) {
/*  74:159 */         set(e.row(), e.column(), e.get());
/*  75:    */       }
/*  76:    */     }
/*  77:    */   }
/*  78:    */   
/*  79:    */   public Matrix set(Matrix A)
/*  80:    */   {
/*  81:164 */     zero();
/*  82:165 */     copy(A);
/*  83:166 */     return this;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public UpperTriangDenseMatrix copy()
/*  87:    */   {
/*  88:171 */     return new UpperTriangDenseMatrix(this);
/*  89:    */   }
/*  90:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.UpperTriangDenseMatrix
 * JD-Core Version:    0.7.0.1
 */