/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */ public class UnitLowerTriangPackMatrix
/*  4:   */   extends LowerTriangPackMatrix
/*  5:   */ {
/*  6:   */   public UnitLowerTriangPackMatrix(int n)
/*  7:   */   {
/*  8:38 */     super(n, Diag.Unit);
/*  9:   */   }
/* 10:   */   
/* 11:   */   public UnitLowerTriangPackMatrix(Matrix A)
/* 12:   */   {
/* 13:49 */     this(A, true);
/* 14:   */   }
/* 15:   */   
/* 16:   */   public UnitLowerTriangPackMatrix(Matrix A, boolean deep)
/* 17:   */   {
/* 18:63 */     super(A, deep, Diag.Unit);
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void add(int row, int column, double value)
/* 22:   */   {
/* 23:68 */     if (column == row) {
/* 24:69 */       throw new IllegalArgumentException("column == row");
/* 25:   */     }
/* 26:70 */     super.add(row, column, value);
/* 27:   */   }
/* 28:   */   
/* 29:   */   public double get(int row, int column)
/* 30:   */   {
/* 31:75 */     if (column == row) {
/* 32:76 */       return 1.0D;
/* 33:   */     }
/* 34:77 */     return super.get(row, column);
/* 35:   */   }
/* 36:   */   
/* 37:   */   public void set(int row, int column, double value)
/* 38:   */   {
/* 39:82 */     if (column == row) {
/* 40:83 */       throw new IllegalArgumentException("column == row");
/* 41:   */     }
/* 42:84 */     super.set(row, column, value);
/* 43:   */   }
/* 44:   */   
/* 45:   */   void copy(Matrix A)
/* 46:   */   {
/* 47:89 */     for (MatrixEntry e : A) {
/* 48:90 */       if (e.row() > e.column()) {
/* 49:91 */         set(e.row(), e.column(), e.get());
/* 50:   */       }
/* 51:   */     }
/* 52:   */   }
/* 53:   */   
/* 54:   */   public UnitLowerTriangPackMatrix copy()
/* 55:   */   {
/* 56:96 */     return new UnitLowerTriangPackMatrix(this);
/* 57:   */   }
/* 58:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.UnitLowerTriangPackMatrix
 * JD-Core Version:    0.7.0.1
 */