/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ public class LowerSymmPackMatrix
/*   4:    */   extends AbstractSymmPackMatrix
/*   5:    */ {
/*   6:    */   public LowerSymmPackMatrix(int n)
/*   7:    */   {
/*   8: 38 */     super(n, UpLo.Lower);
/*   9:    */   }
/*  10:    */   
/*  11:    */   public LowerSymmPackMatrix(Matrix A)
/*  12:    */   {
/*  13: 49 */     this(A, true);
/*  14:    */   }
/*  15:    */   
/*  16:    */   public LowerSymmPackMatrix(Matrix A, boolean deep)
/*  17:    */   {
/*  18: 63 */     super(A, deep, UpLo.Lower);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public void add(int row, int column, double value)
/*  22:    */   {
/*  23: 68 */     if (column <= row) {
/*  24: 69 */       this.data[getIndex(row, column)] += value;
/*  25:    */     }
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void set(int row, int column, double value)
/*  29:    */   {
/*  30: 74 */     if (column <= row) {
/*  31: 75 */       this.data[getIndex(row, column)] = value;
/*  32:    */     }
/*  33:    */   }
/*  34:    */   
/*  35:    */   public double get(int row, int column)
/*  36:    */   {
/*  37: 80 */     if (column <= row) {
/*  38: 81 */       return this.data[getIndex(row, column)];
/*  39:    */     }
/*  40: 82 */     return this.data[getIndex(column, row)];
/*  41:    */   }
/*  42:    */   
/*  43:    */   int getIndex(int row, int column)
/*  44:    */   {
/*  45: 89 */     check(row, column);
/*  46: 90 */     return row + (2 * this.n - (column + 1)) * column / 2;
/*  47:    */   }
/*  48:    */   
/*  49:    */   void copy(Matrix A)
/*  50:    */   {
/*  51: 95 */     for (MatrixEntry e : A) {
/*  52: 96 */       if (e.row() >= e.column()) {
/*  53: 97 */         set(e.row(), e.column(), e.get());
/*  54:    */       }
/*  55:    */     }
/*  56:    */   }
/*  57:    */   
/*  58:    */   public LowerSymmPackMatrix copy()
/*  59:    */   {
/*  60:102 */     return new LowerSymmPackMatrix(this);
/*  61:    */   }
/*  62:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.LowerSymmPackMatrix
 * JD-Core Version:    0.7.0.1
 */