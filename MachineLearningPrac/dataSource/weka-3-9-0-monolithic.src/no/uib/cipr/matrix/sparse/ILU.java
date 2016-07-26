/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import no.uib.cipr.matrix.DenseVector;
/*   4:    */ import no.uib.cipr.matrix.Matrix;
/*   5:    */ import no.uib.cipr.matrix.Vector;
/*   6:    */ 
/*   7:    */ public class ILU
/*   8:    */   implements Preconditioner
/*   9:    */ {
/*  10:    */   private final CompRowMatrix LU;
/*  11:    */   private Matrix L;
/*  12:    */   private Matrix U;
/*  13:    */   private final Vector y;
/*  14:    */   
/*  15:    */   public ILU(CompRowMatrix LU)
/*  16:    */   {
/*  17: 55 */     if (!LU.isSquare()) {
/*  18: 56 */       throw new IllegalArgumentException("ILU only applies to square matrices");
/*  19:    */     }
/*  20: 59 */     this.LU = LU;
/*  21: 60 */     int n = LU.numRows();
/*  22: 61 */     this.y = new DenseVector(n);
/*  23:    */   }
/*  24:    */   
/*  25:    */   public Vector apply(Vector b, Vector x)
/*  26:    */   {
/*  27: 66 */     this.L.solve(b, this.y);
/*  28:    */     
/*  29:    */ 
/*  30: 69 */     return this.U.solve(this.y, x);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public Vector transApply(Vector b, Vector x)
/*  34:    */   {
/*  35: 74 */     this.U.transSolve(b, this.y);
/*  36:    */     
/*  37:    */ 
/*  38: 77 */     return this.L.transSolve(this.y, x);
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void setMatrix(Matrix A)
/*  42:    */   {
/*  43: 81 */     this.LU.set(A);
/*  44:    */     
/*  45: 83 */     factor();
/*  46:    */   }
/*  47:    */   
/*  48:    */   private void factor()
/*  49:    */   {
/*  50: 87 */     int n = this.LU.numRows();
/*  51:    */     
/*  52:    */ 
/*  53: 90 */     int[] colind = this.LU.getColumnIndices();
/*  54: 91 */     int[] rowptr = this.LU.getRowPointers();
/*  55: 92 */     double[] data = this.LU.getData();
/*  56:    */     
/*  57:    */ 
/*  58: 95 */     int[] diagind = findDiagonalIndices(n, colind, rowptr);
/*  59: 98 */     for (int k = 1; k < n; k++) {
/*  60: 99 */       for (int i = rowptr[k]; i < diagind[k]; i++)
/*  61:    */       {
/*  62:102 */         int index = colind[i];
/*  63:103 */         double LUii = data[diagind[index]];
/*  64:105 */         if (LUii == 0.0D) {
/*  65:106 */           throw new RuntimeException("Zero pivot encountered on row " + (i + 1) + " during ILU process");
/*  66:    */         }
/*  67:110 */         double LUki = data[i] /= LUii;
/*  68:    */         
/*  69:    */ 
/*  70:113 */         int j = diagind[index] + 1;
/*  71:113 */         for (int l = rowptr[k] + 1; j < rowptr[(index + 1)]; j++)
/*  72:    */         {
/*  73:115 */           while ((l < rowptr[(k + 1)]) && (colind[l] < colind[j])) {
/*  74:116 */             l++;
/*  75:    */           }
/*  76:118 */           if (colind[l] == colind[j]) {
/*  77:119 */             data[l] -= LUki * data[j];
/*  78:    */           }
/*  79:    */         }
/*  80:    */       }
/*  81:    */     }
/*  82:123 */     this.L = new UnitLowerCompRowMatrix(this.LU, diagind);
/*  83:124 */     this.U = new UpperCompRowMatrix(this.LU, diagind);
/*  84:    */   }
/*  85:    */   
/*  86:    */   private int[] findDiagonalIndices(int m, int[] colind, int[] rowptr)
/*  87:    */   {
/*  88:128 */     int[] diagind = new int[m];
/*  89:130 */     for (int k = 0; k < m; k++)
/*  90:    */     {
/*  91:131 */       diagind[k] = Arrays.binarySearch(colind, k, rowptr[k], rowptr[(k + 1)]);
/*  92:134 */       if (diagind[k] < 0) {
/*  93:135 */         throw new RuntimeException("Missing diagonal entry on row " + (k + 1));
/*  94:    */       }
/*  95:    */     }
/*  96:139 */     return diagind;
/*  97:    */   }
/*  98:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.ILU
 * JD-Core Version:    0.7.0.1
 */