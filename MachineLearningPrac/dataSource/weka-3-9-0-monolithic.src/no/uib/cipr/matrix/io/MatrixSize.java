/*   1:    */ package no.uib.cipr.matrix.io;
/*   2:    */ 
/*   3:    */ public class MatrixSize
/*   4:    */ {
/*   5:    */   private int numRows;
/*   6:    */   private int numColumns;
/*   7:    */   private int numEntries;
/*   8:    */   
/*   9:    */   public MatrixSize(int numRows, int numColumns, MatrixInfo info)
/*  10:    */   {
/*  11: 55 */     this.numRows = numRows;
/*  12: 56 */     this.numColumns = numColumns;
/*  13: 58 */     if (!info.isDense()) {
/*  14: 59 */       throw new IllegalArgumentException("Matrix must be dense");
/*  15:    */     }
/*  16: 61 */     if (info.isGeneral()) {
/*  17: 62 */       this.numEntries = (numRows * numColumns);
/*  18: 63 */     } else if ((info.isSymmetric()) || (info.isHermitian())) {
/*  19: 64 */       this.numEntries = ((numRows * numColumns - numRows) / 2 + numRows);
/*  20: 65 */     } else if (info.isSkewSymmetric()) {
/*  21: 66 */       this.numEntries = ((numRows * numColumns - numRows) / 2);
/*  22:    */     }
/*  23:    */   }
/*  24:    */   
/*  25:    */   public MatrixSize(int numRows, int numColumns, int numEntries)
/*  26:    */   {
/*  27: 80 */     this.numRows = numRows;
/*  28: 81 */     this.numColumns = numColumns;
/*  29: 82 */     this.numEntries = numEntries;
/*  30:    */     
/*  31:    */ 
/*  32: 85 */     long maxR = numRows;long maxC = numColumns;long max = maxR * maxC;
/*  33: 86 */     if (numEntries > max) {
/*  34: 87 */       throw new IllegalArgumentException("numEntries > numRows*numColumns");
/*  35:    */     }
/*  36:    */   }
/*  37:    */   
/*  38:    */   public int numRows()
/*  39:    */   {
/*  40: 95 */     return this.numRows;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public int numColumns()
/*  44:    */   {
/*  45:102 */     return this.numColumns;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public int numEntries()
/*  49:    */   {
/*  50:109 */     return this.numEntries;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public boolean isSquare()
/*  54:    */   {
/*  55:117 */     return this.numRows == this.numColumns;
/*  56:    */   }
/*  57:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.io.MatrixSize
 * JD-Core Version:    0.7.0.1
 */