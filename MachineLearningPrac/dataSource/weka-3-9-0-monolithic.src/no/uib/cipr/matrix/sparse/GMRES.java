/*   1:    */ package no.uib.cipr.matrix.sparse;
/*   2:    */ 
/*   3:    */ import no.uib.cipr.matrix.DenseMatrix;
/*   4:    */ import no.uib.cipr.matrix.DenseVector;
/*   5:    */ import no.uib.cipr.matrix.GivensRotation;
/*   6:    */ import no.uib.cipr.matrix.Matrix;
/*   7:    */ import no.uib.cipr.matrix.UpperTriangDenseMatrix;
/*   8:    */ import no.uib.cipr.matrix.Vector;
/*   9:    */ import no.uib.cipr.matrix.Vector.Norm;
/*  10:    */ 
/*  11:    */ public class GMRES
/*  12:    */   extends AbstractIterativeSolver
/*  13:    */ {
/*  14:    */   private int restart;
/*  15:    */   private Vector w;
/*  16:    */   private Vector u;
/*  17:    */   private Vector r;
/*  18:    */   private Vector[] v;
/*  19:    */   private DenseVector s;
/*  20:    */   private DenseMatrix H;
/*  21:    */   private GivensRotation[] rotation;
/*  22:    */   
/*  23:    */   public GMRES(Vector template)
/*  24:    */   {
/*  25: 85 */     this(template, 30);
/*  26:    */   }
/*  27:    */   
/*  28:    */   public GMRES(Vector template, int restart)
/*  29:    */   {
/*  30:100 */     this.w = template.copy();
/*  31:101 */     this.u = template.copy();
/*  32:102 */     this.r = template.copy();
/*  33:103 */     setRestart(restart);
/*  34:    */   }
/*  35:    */   
/*  36:    */   public void setRestart(int restart)
/*  37:    */   {
/*  38:113 */     this.restart = restart;
/*  39:114 */     if (restart <= 0) {
/*  40:115 */       throw new IllegalArgumentException("restart must be a positive integer");
/*  41:    */     }
/*  42:118 */     this.s = new DenseVector(restart + 1);
/*  43:119 */     this.H = new DenseMatrix(restart + 1, restart);
/*  44:120 */     this.rotation = new GivensRotation[restart + 1];
/*  45:    */     
/*  46:122 */     this.v = new Vector[restart + 1];
/*  47:123 */     for (int i = 0; i < this.v.length; i++) {
/*  48:124 */       this.v[i] = this.r.copy().zero();
/*  49:    */     }
/*  50:    */   }
/*  51:    */   
/*  52:    */   public Vector solve(Matrix A, Vector b, Vector x)
/*  53:    */     throws IterativeSolverNotConvergedException
/*  54:    */   {
/*  55:129 */     checkSizes(A, b, x);
/*  56:    */     
/*  57:131 */     A.multAdd(-1.0D, x, this.u.set(b));
/*  58:132 */     this.M.apply(this.u, this.r);
/*  59:133 */     double normr = this.r.norm(Vector.Norm.Two);
/*  60:134 */     this.M.apply(b, this.u);
/*  61:137 */     for (this.iter.setFirst(); !this.iter.converged(this.r, x); this.iter.next())
/*  62:    */     {
/*  63:139 */       this.v[0].set(1.0D / normr, this.r);
/*  64:140 */       this.s.zero().set(0, normr);
/*  65:141 */       int i = 0;
/*  66:144 */       while ((i < this.restart) && (!this.iter.converged(Math.abs(this.s.get(i)))))
/*  67:    */       {
/*  68:146 */         A.mult(this.v[i], this.u);
/*  69:147 */         this.M.apply(this.u, this.w);
/*  70:149 */         for (int k = 0; k <= i; k++)
/*  71:    */         {
/*  72:150 */           this.H.set(k, i, this.w.dot(this.v[k]));
/*  73:151 */           this.w.add(-this.H.get(k, i), this.v[k]);
/*  74:    */         }
/*  75:153 */         this.H.set(i + 1, i, this.w.norm(Vector.Norm.Two));
/*  76:154 */         this.v[(i + 1)].set(1.0D / this.H.get(i + 1, i), this.w);
/*  77:157 */         for (int k = 0; k < i; k++) {
/*  78:158 */           this.rotation[k].apply(this.H, i, k, k + 1);
/*  79:    */         }
/*  80:160 */         this.rotation[i] = new GivensRotation(this.H.get(i, i), this.H.get(i + 1, i));
/*  81:161 */         this.rotation[i].apply(this.H, i, i, i + 1);
/*  82:162 */         this.rotation[i].apply(this.s, i, i + 1);i++;this.iter.next();
/*  83:    */       }
/*  84:166 */       new UpperTriangDenseMatrix(this.H, i, false).solve(this.s, this.s);
/*  85:167 */       for (int j = 0; j < i; j++) {
/*  86:168 */         x.add(this.s.get(j), this.v[j]);
/*  87:    */       }
/*  88:170 */       A.multAdd(-1.0D, x, this.u.set(b));
/*  89:171 */       this.M.apply(this.u, this.r);
/*  90:172 */       normr = this.r.norm(Vector.Norm.Two);
/*  91:    */     }
/*  92:175 */     return x;
/*  93:    */   }
/*  94:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.GMRES
 * JD-Core Version:    0.7.0.1
 */