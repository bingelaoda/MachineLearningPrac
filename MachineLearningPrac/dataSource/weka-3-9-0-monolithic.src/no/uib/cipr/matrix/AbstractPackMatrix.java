/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ 
/*   5:    */ abstract class AbstractPackMatrix
/*   6:    */   extends AbstractMatrix
/*   7:    */ {
/*   8:    */   double[] data;
/*   9:    */   int n;
/*  10:    */   
/*  11:    */   public AbstractPackMatrix(int n)
/*  12:    */   {
/*  13: 48 */     super(n, n);
/*  14: 49 */     this.n = this.numRows;
/*  15: 50 */     this.data = new double[(n * n + n) / 2];
/*  16:    */   }
/*  17:    */   
/*  18:    */   public AbstractPackMatrix(Matrix A)
/*  19:    */   {
/*  20: 60 */     this(A, true);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public AbstractPackMatrix(Matrix A, boolean deep)
/*  24:    */   {
/*  25: 73 */     super(A);
/*  26: 75 */     if (!isSquare()) {
/*  27: 76 */       throw new IllegalArgumentException("Packed matrix must be square");
/*  28:    */     }
/*  29: 77 */     this.n = A.numRows();
/*  30: 78 */     if (deep)
/*  31:    */     {
/*  32: 79 */       this.data = new double[(this.n * this.n + this.n) / 2];
/*  33: 80 */       copy(A);
/*  34:    */     }
/*  35:    */     else
/*  36:    */     {
/*  37: 82 */       this.data = ((AbstractPackMatrix)A).getData();
/*  38:    */     }
/*  39:    */   }
/*  40:    */   
/*  41:    */   abstract void copy(Matrix paramMatrix);
/*  42:    */   
/*  43:    */   public double[] getData()
/*  44:    */   {
/*  45: 95 */     return this.data;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public Matrix set(Matrix B)
/*  49:    */   {
/*  50:100 */     if (!(B instanceof AbstractPackMatrix)) {
/*  51:101 */       return super.set(B);
/*  52:    */     }
/*  53:103 */     checkSize(B);
/*  54:    */     
/*  55:105 */     double[] Bd = ((AbstractPackMatrix)B).getData();
/*  56:107 */     if (Bd == this.data) {
/*  57:108 */       return this;
/*  58:    */     }
/*  59:110 */     System.arraycopy(Bd, 0, this.data, 0, this.data.length);
/*  60:    */     
/*  61:112 */     return this;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public Matrix zero()
/*  65:    */   {
/*  66:117 */     Arrays.fill(this.data, 0.0D);
/*  67:118 */     return this;
/*  68:    */   }
/*  69:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.AbstractPackMatrix
 * JD-Core Version:    0.7.0.1
 */