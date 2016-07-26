/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import no.uib.cipr.matrix.Matrix;
/*   4:    */ import no.uib.cipr.matrix.Vector;
/*   5:    */ 
/*   6:    */ public class Chebyshev
/*   7:    */   extends AbstractIterativeSolver
/*   8:    */ {
/*   9:    */   private double eigmin;
/*  10:    */   private double eigmax;
/*  11:    */   private Vector p;
/*  12:    */   private Vector z;
/*  13:    */   private Vector r;
/*  14:    */   private Vector q;
/*  15:    */   
/*  16:    */   public Chebyshev(Vector template, double eigmin, double eigmax)
/*  17:    */   {
/*  18: 63 */     this.p = template.copy();
/*  19: 64 */     this.z = template.copy();
/*  20: 65 */     this.r = template.copy();
/*  21: 66 */     this.q = template.copy();
/*  22: 67 */     setEigenvalues(eigmin, eigmax);
/*  23:    */   }
/*  24:    */   
/*  25:    */   public void setEigenvalues(double eigmin, double eigmax)
/*  26:    */   {
/*  27: 79 */     this.eigmin = eigmin;
/*  28: 80 */     this.eigmax = eigmax;
/*  29: 82 */     if (eigmin <= 0.0D) {
/*  30: 83 */       throw new IllegalArgumentException("eigmin <= 0");
/*  31:    */     }
/*  32: 84 */     if (eigmax <= 0.0D) {
/*  33: 85 */       throw new IllegalArgumentException("eigmax <= 0");
/*  34:    */     }
/*  35: 86 */     if (eigmin > eigmax) {
/*  36: 87 */       throw new IllegalArgumentException("eigmin > eigmax");
/*  37:    */     }
/*  38:    */   }
/*  39:    */   
/*  40:    */   public Vector solve(Matrix A, Vector b, Vector x)
/*  41:    */     throws IterativeSolverNotConvergedException
/*  42:    */   {
/*  43: 92 */     checkSizes(A, b, x);
/*  44:    */     
/*  45: 94 */     double alpha = 0.0D;double beta = 0.0D;double c = 0.0D;double d = 0.0D;
/*  46:    */     
/*  47: 96 */     A.multAdd(-1.0D, x, this.r.set(b));
/*  48:    */     
/*  49: 98 */     c = (this.eigmax - this.eigmin) / 2.0D;
/*  50: 99 */     d = (this.eigmax + this.eigmin) / 2.0D;
/*  51:101 */     for (this.iter.setFirst(); !this.iter.converged(this.r, x); this.iter.next())
/*  52:    */     {
/*  53:102 */       this.M.apply(this.r, this.z);
/*  54:104 */       if (this.iter.isFirst())
/*  55:    */       {
/*  56:105 */         this.p.set(this.z);
/*  57:106 */         alpha = 2.0D / d;
/*  58:    */       }
/*  59:    */       else
/*  60:    */       {
/*  61:108 */         beta = alpha * c / 2.0D;
/*  62:109 */         beta *= beta;
/*  63:110 */         alpha = 1.0D / (d - beta);
/*  64:111 */         this.p.scale(beta).add(this.z);
/*  65:    */       }
/*  66:114 */       A.mult(this.p, this.q);
/*  67:115 */       x.add(alpha, this.p);
/*  68:116 */       this.r.add(-alpha, this.q);
/*  69:    */     }
/*  70:119 */     return x;
/*  71:    */   }
/*  72:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.Chebyshev
 * JD-Core Version:    0.7.0.1
 */