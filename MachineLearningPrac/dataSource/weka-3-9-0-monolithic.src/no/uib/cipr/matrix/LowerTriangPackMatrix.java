/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ public class LowerTriangPackMatrix
/*   4:    */   extends AbstractTriangPackMatrix
/*   5:    */ {
/*   6:    */   public LowerTriangPackMatrix(int n)
/*   7:    */   {
/*   8: 86 */     super(n, UpLo.Lower, Diag.NonUnit);
/*   9:    */   }
/*  10:    */   
/*  11:    */   LowerTriangPackMatrix(int n, Diag diag)
/*  12:    */   {
/*  13: 97 */     super(n, UpLo.Lower, diag);
/*  14:    */   }
/*  15:    */   
/*  16:    */   public LowerTriangPackMatrix(Matrix A)
/*  17:    */   {
/*  18:108 */     this(A, true);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public LowerTriangPackMatrix(Matrix A, boolean deep)
/*  22:    */   {
/*  23:122 */     super(A, deep, UpLo.Lower, Diag.NonUnit);
/*  24:    */   }
/*  25:    */   
/*  26:    */   LowerTriangPackMatrix(Matrix A, boolean deep, Diag diag)
/*  27:    */   {
/*  28:136 */     super(A, deep, UpLo.Lower, diag);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void add(int row, int column, double value)
/*  32:    */   {
/*  33:141 */     if (column > row) {
/*  34:142 */       throw new IllegalArgumentException("column > row");
/*  35:    */     }
/*  36:143 */     this.data[getIndex(row, column)] += value;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public void set(int row, int column, double value)
/*  40:    */   {
/*  41:148 */     if (column > row) {
/*  42:149 */       throw new IllegalArgumentException("column > row");
/*  43:    */     }
/*  44:150 */     this.data[getIndex(row, column)] = value;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public double get(int row, int column)
/*  48:    */   {
/*  49:155 */     if (column > row) {
/*  50:156 */       return 0.0D;
/*  51:    */     }
/*  52:157 */     return this.data[getIndex(row, column)];
/*  53:    */   }
/*  54:    */   
/*  55:    */   int getIndex(int row, int column)
/*  56:    */   {
/*  57:164 */     check(row, column);
/*  58:165 */     return row + (2 * this.n - (column + 1)) * column / 2;
/*  59:    */   }
/*  60:    */   
/*  61:    */   void copy(Matrix A)
/*  62:    */   {
/*  63:170 */     for (MatrixEntry e : A) {
/*  64:171 */       if (e.row() >= e.column()) {
/*  65:172 */         set(e.row(), e.column(), e.get());
/*  66:    */       }
/*  67:    */     }
/*  68:    */   }
/*  69:    */   
/*  70:    */   public LowerTriangPackMatrix copy()
/*  71:    */   {
/*  72:177 */     return new LowerTriangPackMatrix(this);
/*  73:    */   }
/*  74:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.LowerTriangPackMatrix
 * JD-Core Version:    0.7.0.1
 */