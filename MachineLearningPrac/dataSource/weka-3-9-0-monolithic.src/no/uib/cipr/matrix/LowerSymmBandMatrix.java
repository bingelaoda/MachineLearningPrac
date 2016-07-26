/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */ public class LowerSymmBandMatrix
/*  4:   */   extends AbstractSymmBandMatrix
/*  5:   */ {
/*  6:   */   public LowerSymmBandMatrix(int n, int kd)
/*  7:   */   {
/*  8:40 */     super(n, kd, 0, UpLo.Lower);
/*  9:   */   }
/* 10:   */   
/* 11:   */   public LowerSymmBandMatrix(Matrix A, int kd)
/* 12:   */   {
/* 13:54 */     this(A, kd, true);
/* 14:   */   }
/* 15:   */   
/* 16:   */   public LowerSymmBandMatrix(Matrix A, int kd, boolean deep)
/* 17:   */   {
/* 18:71 */     super(A, kd, 0, deep, UpLo.Lower);
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void add(int row, int column, double value)
/* 22:   */   {
/* 23:76 */     if (column <= row) {
/* 24:77 */       super.add(row, column, value);
/* 25:   */     }
/* 26:   */   }
/* 27:   */   
/* 28:   */   public double get(int row, int column)
/* 29:   */   {
/* 30:82 */     if (column > row) {
/* 31:83 */       return super.get(column, row);
/* 32:   */     }
/* 33:84 */     return super.get(row, column);
/* 34:   */   }
/* 35:   */   
/* 36:   */   public void set(int row, int column, double value)
/* 37:   */   {
/* 38:89 */     if (column <= row) {
/* 39:90 */       super.set(row, column, value);
/* 40:   */     }
/* 41:   */   }
/* 42:   */   
/* 43:   */   public LowerSymmBandMatrix copy()
/* 44:   */   {
/* 45:95 */     return new LowerSymmBandMatrix(this, this.kd);
/* 46:   */   }
/* 47:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.LowerSymmBandMatrix
 * JD-Core Version:    0.7.0.1
 */