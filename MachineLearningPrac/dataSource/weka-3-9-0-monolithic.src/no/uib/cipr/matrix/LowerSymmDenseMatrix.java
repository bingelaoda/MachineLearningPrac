/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */ public class LowerSymmDenseMatrix
/*  4:   */   extends AbstractSymmDenseMatrix
/*  5:   */ {
/*  6:   */   public LowerSymmDenseMatrix(int n)
/*  7:   */   {
/*  8:39 */     super(n, UpLo.Lower);
/*  9:   */   }
/* 10:   */   
/* 11:   */   public LowerSymmDenseMatrix(Matrix A)
/* 12:   */   {
/* 13:50 */     this(A, true);
/* 14:   */   }
/* 15:   */   
/* 16:   */   public LowerSymmDenseMatrix(Matrix A, boolean deep)
/* 17:   */   {
/* 18:64 */     super(A, deep, UpLo.Lower);
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void add(int row, int column, double value)
/* 22:   */   {
/* 23:69 */     if (column <= row) {
/* 24:70 */       super.add(row, column, value);
/* 25:   */     }
/* 26:   */   }
/* 27:   */   
/* 28:   */   public double get(int row, int column)
/* 29:   */   {
/* 30:75 */     if (column > row) {
/* 31:76 */       return super.get(column, row);
/* 32:   */     }
/* 33:77 */     return super.get(row, column);
/* 34:   */   }
/* 35:   */   
/* 36:   */   public void set(int row, int column, double value)
/* 37:   */   {
/* 38:82 */     if (column <= row) {
/* 39:83 */       super.set(row, column, value);
/* 40:   */     }
/* 41:   */   }
/* 42:   */   
/* 43:   */   public LowerSymmDenseMatrix copy()
/* 44:   */   {
/* 45:88 */     return new LowerSymmDenseMatrix(this);
/* 46:   */   }
/* 47:   */   
/* 48:   */   void copy(Matrix A)
/* 49:   */   {
/* 50:93 */     for (MatrixEntry e : A) {
/* 51:94 */       if (e.row() >= e.column()) {
/* 52:95 */         set(e.row(), e.column(), e.get());
/* 53:   */       }
/* 54:   */     }
/* 55:   */   }
/* 56:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.LowerSymmDenseMatrix
 * JD-Core Version:    0.7.0.1
 */