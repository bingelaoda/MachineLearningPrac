/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */ public class UpperSymmBandMatrix
/*  4:   */   extends AbstractSymmBandMatrix
/*  5:   */ {
/*  6:   */   public UpperSymmBandMatrix(int n, int kd)
/*  7:   */   {
/*  8:40 */     super(n, 0, kd, UpLo.Upper);
/*  9:   */   }
/* 10:   */   
/* 11:   */   public UpperSymmBandMatrix(Matrix A, int kd)
/* 12:   */   {
/* 13:54 */     this(A, kd, true);
/* 14:   */   }
/* 15:   */   
/* 16:   */   public UpperSymmBandMatrix(Matrix A, int kd, boolean deep)
/* 17:   */   {
/* 18:71 */     super(A, 0, kd, deep, UpLo.Upper);
/* 19:   */   }
/* 20:   */   
/* 21:   */   public void add(int row, int column, double value)
/* 22:   */   {
/* 23:76 */     if (row <= column) {
/* 24:77 */       super.add(row, column, value);
/* 25:   */     }
/* 26:   */   }
/* 27:   */   
/* 28:   */   public double get(int row, int column)
/* 29:   */   {
/* 30:82 */     if (row > column) {
/* 31:83 */       return super.get(column, row);
/* 32:   */     }
/* 33:84 */     return super.get(row, column);
/* 34:   */   }
/* 35:   */   
/* 36:   */   public void set(int row, int column, double value)
/* 37:   */   {
/* 38:89 */     if (row <= column) {
/* 39:90 */       super.set(row, column, value);
/* 40:   */     }
/* 41:   */   }
/* 42:   */   
/* 43:   */   public UpperSymmBandMatrix copy()
/* 44:   */   {
/* 45:95 */     return new UpperSymmBandMatrix(this, this.kd);
/* 46:   */   }
/* 47:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.UpperSymmBandMatrix
 * JD-Core Version:    0.7.0.1
 */