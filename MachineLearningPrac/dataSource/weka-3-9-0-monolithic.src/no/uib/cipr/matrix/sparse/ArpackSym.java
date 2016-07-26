/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import com.github.fommil.netlib.ARPACK;
/*   4:    */ import java.util.Arrays;
/*   5:    */ import java.util.Comparator;
/*   6:    */ import java.util.Map;
/*   7:    */ import java.util.TreeMap;
/*   8:    */ import java.util.logging.Logger;
/*   9:    */ import no.uib.cipr.matrix.DenseVector;
/*  10:    */ import no.uib.cipr.matrix.DenseVectorSub;
/*  11:    */ import no.uib.cipr.matrix.Matrix;
/*  12:    */ import no.uib.cipr.matrix.MatrixEntry;
/*  13:    */ import no.uib.cipr.matrix.Vector;
/*  14:    */ import org.netlib.util.doubleW;
/*  15:    */ import org.netlib.util.intW;
/*  16:    */ 
/*  17:    */ public class ArpackSym
/*  18:    */ {
/*  19: 19 */   private static final Logger log = Logger.getLogger(ArpackSym.class.getName());
/*  20:    */   
/*  21:    */   public static enum Ritz
/*  22:    */   {
/*  23: 26 */     LA,  SA,  LM,  SM,  BE;
/*  24:    */     
/*  25:    */     private Ritz() {}
/*  26:    */   }
/*  27:    */   
/*  28: 45 */   private final ARPACK arpack = ARPACK.getInstance();
/*  29:    */   private static final double TOL = 0.0001D;
/*  30:    */   private static final boolean EXPENSIVE_CHECKS = true;
/*  31:    */   private final Matrix matrix;
/*  32:    */   
/*  33:    */   public ArpackSym(Matrix matrix)
/*  34:    */   {
/*  35: 54 */     if (!matrix.isSquare()) {
/*  36: 55 */       throw new IllegalArgumentException("matrix must be square");
/*  37:    */     }
/*  38: 57 */     for (MatrixEntry entry : matrix) {
/*  39: 58 */       if (entry.get() != matrix.get(entry.column(), entry.row())) {
/*  40: 59 */         throw new IllegalArgumentException("matrix must be symmetric");
/*  41:    */       }
/*  42:    */     }
/*  43: 62 */     this.matrix = matrix;
/*  44:    */   }
/*  45:    */   
/*  46:    */   public Map<Double, DenseVectorSub> solve(int eigenvalues, Ritz ritz)
/*  47:    */   {
/*  48: 78 */     if (eigenvalues <= 0) {
/*  49: 79 */       throw new IllegalArgumentException(eigenvalues + " <= 0");
/*  50:    */     }
/*  51: 80 */     if (eigenvalues >= this.matrix.numColumns()) {
/*  52: 82 */       throw new IllegalArgumentException(eigenvalues + " >= " + this.matrix.numColumns());
/*  53:    */     }
/*  54: 84 */     int n = this.matrix.numRows();
/*  55: 85 */     intW nev = new intW(eigenvalues);
/*  56:    */     
/*  57: 87 */     int ncv = Math.min(2 * eigenvalues, n);
/*  58:    */     
/*  59: 89 */     String bmat = "I";
/*  60: 90 */     String which = ritz.name();
/*  61: 91 */     doubleW tol = new doubleW(0.0001D);
/*  62: 92 */     intW info = new intW(0);
/*  63: 93 */     int[] iparam = new int[11];
/*  64: 94 */     iparam[0] = 1;
/*  65: 95 */     iparam[2] = 300;
/*  66: 96 */     iparam[6] = 1;
/*  67: 97 */     intW ido = new intW(0);
/*  68:    */     
/*  69:    */ 
/*  70:    */ 
/*  71:101 */     double[] resid = new double[n];
/*  72:    */     
/*  73:103 */     double[] v = new double[n * ncv];
/*  74:    */     
/*  75:105 */     double[] workd = new double[3 * n];
/*  76:    */     
/*  77:107 */     double[] workl = new double[ncv * (ncv + 8)];
/*  78:108 */     int[] ipntr = new int[11];
/*  79:    */     
/*  80:110 */     int i = 0;
/*  81:    */     for (;;)
/*  82:    */     {
/*  83:112 */       i++;
/*  84:113 */       this.arpack.dsaupd(ido, bmat, n, which, nev.val, tol, resid, ncv, v, n, iparam, ipntr, workd, workl, workl.length, info);
/*  85:115 */       if (ido.val == 99) {
/*  86:    */         break;
/*  87:    */       }
/*  88:117 */       if ((ido.val != -1) && (ido.val != 1)) {
/*  89:118 */         throw new IllegalStateException("ido = " + ido.val);
/*  90:    */       }
/*  91:120 */       av(workd, ipntr[0] - 1, ipntr[1] - 1);
/*  92:    */     }
/*  93:123 */     log.fine(i + " iterations for " + n);
/*  94:125 */     if (info.val != 0) {
/*  95:126 */       throw new IllegalStateException("info = " + info.val);
/*  96:    */     }
/*  97:128 */     double[] d = new double[nev.val];
/*  98:129 */     boolean[] select = new boolean[ncv];
/*  99:130 */     double[] z = Arrays.copyOfRange(v, 0, nev.val * n);
/* 100:    */     
/* 101:132 */     this.arpack.dseupd(true, "A", select, d, z, n, 0.0D, bmat, n, which, nev, 0.0001D, resid, ncv, v, n, iparam, ipntr, workd, workl, workl.length, info);
/* 102:135 */     if (info.val != 0) {
/* 103:136 */       throw new IllegalStateException("info = " + info.val);
/* 104:    */     }
/* 105:138 */     int computed = iparam[4];
/* 106:139 */     log.fine("computed " + computed + " eigenvalues");
/* 107:    */     
/* 108:141 */     Map<Double, DenseVectorSub> solution = new TreeMap(new Comparator()
/* 109:    */     {
/* 110:    */       public int compare(Double o1, Double o2)
/* 111:    */       {
/* 112:146 */         return Double.compare(o2.doubleValue(), o1.doubleValue());
/* 113:    */       }
/* 114:148 */     });
/* 115:149 */     DenseVector eigenvectors = new DenseVector(z, false);
/* 116:150 */     for (i = 0; i < computed; i++)
/* 117:    */     {
/* 118:151 */       double eigenvalue = d[i];
/* 119:152 */       DenseVectorSub eigenvector = new DenseVectorSub(eigenvectors, i * n, n);
/* 120:    */       
/* 121:154 */       solution.put(Double.valueOf(eigenvalue), eigenvector);
/* 122:    */     }
/* 123:157 */     return solution;
/* 124:    */   }
/* 125:    */   
/* 126:    */   private void av(double[] work, int input_offset, int output_offset)
/* 127:    */   {
/* 128:161 */     DenseVector w = new DenseVector(work, false);
/* 129:162 */     Vector x = new DenseVectorSub(w, input_offset, this.matrix.numColumns());
/* 130:163 */     Vector y = new DenseVectorSub(w, output_offset, this.matrix.numColumns());
/* 131:164 */     this.matrix.mult(x, y);
/* 132:    */   }
/* 133:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.ArpackSym
 * JD-Core Version:    0.7.0.1
 */