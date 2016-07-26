/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.LAPACK;
/*   4:    */ import java.util.BitSet;
/*   5:    */ 
/*   6:    */ public class PermutationMatrix
/*   7:    */   extends AbstractMatrix
/*   8:    */ {
/*   9:    */   private int[] permutations;
/*  10:    */   private int[] pivots;
/*  11:    */   private boolean transposed;
/*  12:    */   
/*  13:    */   public static PermutationMatrix fromPartialPivots(int[] pivots)
/*  14:    */   {
/*  15: 28 */     int[] permutations = new int[pivots.length];
/*  16: 29 */     for (int i = 0; i < pivots.length; i++) {
/*  17: 30 */       permutations[i] = i;
/*  18:    */     }
/*  19: 33 */     for (int i = 0; i < pivots.length; i++)
/*  20:    */     {
/*  21: 34 */       int j = pivots[i] - 1;
/*  22: 35 */       if (j != i)
/*  23:    */       {
/*  24: 37 */         int tmp = permutations[i];
/*  25: 38 */         permutations[i] = permutations[j];
/*  26: 39 */         permutations[j] = tmp;
/*  27:    */       }
/*  28:    */     }
/*  29: 42 */     return new PermutationMatrix(permutations, pivots);
/*  30:    */   }
/*  31:    */   
/*  32:    */   public PermutationMatrix(int[] permutations)
/*  33:    */   {
/*  34: 52 */     this(permutations, null);
/*  35:    */   }
/*  36:    */   
/*  37:    */   private PermutationMatrix(int[] permutations, int[] pivots)
/*  38:    */   {
/*  39: 58 */     super(permutations.length, permutations.length);
/*  40: 59 */     this.permutations = permutations;
/*  41: 60 */     BitSet bitset = new BitSet();
/*  42: 61 */     for (int i : permutations)
/*  43:    */     {
/*  44: 62 */       if (bitset.get(i)) {
/*  45: 63 */         throw new IllegalArgumentException("non-unique permutations: " + i);
/*  46:    */       }
/*  47: 65 */       bitset.set(i);
/*  48:    */     }
/*  49: 67 */     this.pivots = pivots;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public double get(int row, int column)
/*  53:    */   {
/*  54: 72 */     if ((!this.transposed) && (this.permutations[row] == column)) {
/*  55: 73 */       return 1.0D;
/*  56:    */     }
/*  57: 74 */     if ((this.transposed) && (this.permutations[column] == row)) {
/*  58: 75 */       return 1.0D;
/*  59:    */     }
/*  60: 76 */     return 0.0D;
/*  61:    */   }
/*  62:    */   
/*  63:    */   public Matrix transpose()
/*  64:    */   {
/*  65: 81 */     this.transposed = (!this.transposed);
/*  66: 82 */     return this;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public Matrix mult(Matrix B, Matrix C)
/*  70:    */   {
/*  71: 87 */     if ((C instanceof DenseMatrix)) {
/*  72: 88 */       return mult(B, (DenseMatrix)C);
/*  73:    */     }
/*  74: 89 */     return super.mult(B, C);
/*  75:    */   }
/*  76:    */   
/*  77:    */   public Matrix mult(Matrix B, DenseMatrix C)
/*  78:    */   {
/*  79: 93 */     if (this.pivots == null) {
/*  80: 94 */       return super.mult(B, C);
/*  81:    */     }
/*  82: 95 */     checkMultAdd(B, C);
/*  83: 96 */     C.set(B);
/*  84:    */     
/*  85: 98 */     LAPACK.getInstance().dlaswp(C.numColumns(), C.getData(), 
/*  86: 99 */       Matrices.ld(C.numRows()), 1, this.pivots.length, this.pivots, this.transposed ? -1 : 1);
/*  87:    */     
/*  88:101 */     return C;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public Matrix transAmult(Matrix B, Matrix C)
/*  92:    */   {
/*  93:106 */     if ((C instanceof DenseMatrix)) {
/*  94:107 */       return transAmult(B, (DenseMatrix)C);
/*  95:    */     }
/*  96:108 */     return super.transAmult(B, C);
/*  97:    */   }
/*  98:    */   
/*  99:    */   public Matrix transAmult(Matrix B, DenseMatrix C)
/* 100:    */   {
/* 101:112 */     if (this.pivots == null) {
/* 102:113 */       return super.transAmult(B, C);
/* 103:    */     }
/* 104:114 */     checkTransAmultAdd(B, C);
/* 105:115 */     C.set(B);
/* 106:    */     
/* 107:117 */     LAPACK.getInstance().dlaswp(C.numColumns(), C.getData(), 
/* 108:118 */       Matrices.ld(C.numRows()), 1, this.pivots.length, this.pivots, this.transposed ? 1 : -1);
/* 109:    */     
/* 110:120 */     return C;
/* 111:    */   }
/* 112:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.PermutationMatrix
 * JD-Core Version:    0.7.0.1
 */