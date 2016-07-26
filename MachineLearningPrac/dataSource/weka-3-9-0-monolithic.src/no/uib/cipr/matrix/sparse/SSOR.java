/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import no.uib.cipr.matrix.DenseVector;
/*   4:    */ import no.uib.cipr.matrix.Matrix;
/*   5:    */ import no.uib.cipr.matrix.Vector;
/*   6:    */ 
/*   7:    */ public class SSOR
/*   8:    */   implements Preconditioner
/*   9:    */ {
/*  10:    */   private double omegaF;
/*  11:    */   private double omegaR;
/*  12:    */   private final CompRowMatrix F;
/*  13:    */   private final int[] diagind;
/*  14:    */   private final double[] xx;
/*  15:    */   private final boolean reverse;
/*  16:    */   
/*  17:    */   public SSOR(CompRowMatrix F, boolean reverse, double omegaF, double omegaR)
/*  18:    */   {
/*  19: 82 */     if (!F.isSquare()) {
/*  20: 83 */       throw new IllegalArgumentException("SSOR only applies to square matrices");
/*  21:    */     }
/*  22: 86 */     this.F = F;
/*  23: 87 */     this.reverse = reverse;
/*  24: 88 */     setOmega(omegaF, omegaR);
/*  25:    */     
/*  26: 90 */     int n = F.numRows();
/*  27: 91 */     this.diagind = new int[n];
/*  28: 92 */     this.xx = new double[n];
/*  29:    */   }
/*  30:    */   
/*  31:    */   public SSOR(CompRowMatrix F)
/*  32:    */   {
/*  33:103 */     this(F, true, 1.0D, 1.0D);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void setOmega(double omegaF, double omegaR)
/*  37:    */   {
/*  38:117 */     if ((omegaF < 0.0D) || (omegaF > 2.0D)) {
/*  39:118 */       throw new IllegalArgumentException("omegaF must be between 0 and 2");
/*  40:    */     }
/*  41:119 */     if ((omegaR < 0.0D) || (omegaR > 2.0D)) {
/*  42:120 */       throw new IllegalArgumentException("omegaR must be between 0 and 2");
/*  43:    */     }
/*  44:122 */     this.omegaF = omegaF;
/*  45:123 */     this.omegaR = omegaR;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void setMatrix(Matrix A)
/*  49:    */   {
/*  50:127 */     this.F.set(A);
/*  51:    */     
/*  52:129 */     int n = this.F.numRows();
/*  53:    */     
/*  54:131 */     int[] rowptr = this.F.getRowPointers();
/*  55:132 */     int[] colind = this.F.getColumnIndices();
/*  56:135 */     for (int k = 0; k < n; k++)
/*  57:    */     {
/*  58:136 */       this.diagind[k] = Arrays.binarySearch(colind, k, rowptr[k], rowptr[(k + 1)]);
/*  59:138 */       if (this.diagind[k] < 0) {
/*  60:139 */         throw new RuntimeException("Missing diagonal on row " + (k + 1));
/*  61:    */       }
/*  62:    */     }
/*  63:    */   }
/*  64:    */   
/*  65:    */   public Vector apply(Vector b, Vector x)
/*  66:    */   {
/*  67:144 */     if ((!(b instanceof DenseVector)) || (!(x instanceof DenseVector))) {
/*  68:145 */       throw new IllegalArgumentException("Vectors must be a DenseVectors");
/*  69:    */     }
/*  70:147 */     int[] rowptr = this.F.getRowPointers();
/*  71:148 */     int[] colind = this.F.getColumnIndices();
/*  72:149 */     double[] data = this.F.getData();
/*  73:    */     
/*  74:151 */     double[] bd = ((DenseVector)b).getData();
/*  75:152 */     double[] xd = ((DenseVector)x).getData();
/*  76:    */     
/*  77:154 */     int n = this.F.numRows();
/*  78:155 */     System.arraycopy(xd, 0, this.xx, 0, n);
/*  79:158 */     for (int i = 0; i < n; i++)
/*  80:    */     {
/*  81:160 */       double sigma = 0.0D;
/*  82:161 */       for (int j = rowptr[i]; j < this.diagind[i]; j++) {
/*  83:162 */         sigma += data[j] * this.xx[colind[j]];
/*  84:    */       }
/*  85:164 */       for (int j = this.diagind[i] + 1; j < rowptr[(i + 1)]; j++) {
/*  86:165 */         sigma += data[j] * xd[colind[j]];
/*  87:    */       }
/*  88:167 */       sigma = (bd[i] - sigma) / data[this.diagind[i]];
/*  89:    */       
/*  90:169 */       this.xx[i] = (xd[i] + this.omegaF * (sigma - xd[i]));
/*  91:    */     }
/*  92:173 */     if (!this.reverse)
/*  93:    */     {
/*  94:174 */       System.arraycopy(this.xx, 0, xd, 0, n);
/*  95:175 */       return x;
/*  96:    */     }
/*  97:179 */     for (int i = n - 1; i >= 0; i--)
/*  98:    */     {
/*  99:181 */       double sigma = 0.0D;
/* 100:182 */       for (int j = rowptr[i]; j < this.diagind[i]; j++) {
/* 101:183 */         sigma += data[j] * this.xx[colind[j]];
/* 102:    */       }
/* 103:185 */       for (int j = this.diagind[i] + 1; j < rowptr[(i + 1)]; j++) {
/* 104:186 */         sigma += data[j] * xd[colind[j]];
/* 105:    */       }
/* 106:188 */       sigma = (bd[i] - sigma) / data[this.diagind[i]];
/* 107:    */       
/* 108:190 */       xd[i] = (this.xx[i] + this.omegaR * (sigma - this.xx[i]));
/* 109:    */     }
/* 110:193 */     return x;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public Vector transApply(Vector b, Vector x)
/* 114:    */   {
/* 115:198 */     return apply(b, x);
/* 116:    */   }
/* 117:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.SSOR
 * JD-Core Version:    0.7.0.1
 */