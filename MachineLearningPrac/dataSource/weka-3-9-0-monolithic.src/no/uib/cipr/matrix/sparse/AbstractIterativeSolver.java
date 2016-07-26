/*  1:   */ package no.uib.cipr.matrix.sparse;
/*  2:   */ 
/*  3:   */ import no.uib.cipr.matrix.Matrix;
/*  4:   */ import no.uib.cipr.matrix.Vector;
/*  5:   */ 
/*  6:   */ public abstract class AbstractIterativeSolver
/*  7:   */   implements IterativeSolver
/*  8:   */ {
/*  9:   */   protected Preconditioner M;
/* 10:   */   protected IterationMonitor iter;
/* 11:   */   
/* 12:   */   public AbstractIterativeSolver()
/* 13:   */   {
/* 14:46 */     this.M = new IdentityPreconditioner(null);
/* 15:47 */     this.iter = new DefaultIterationMonitor();
/* 16:   */   }
/* 17:   */   
/* 18:   */   public void setPreconditioner(Preconditioner M)
/* 19:   */   {
/* 20:51 */     this.M = M;
/* 21:   */   }
/* 22:   */   
/* 23:   */   public Preconditioner getPreconditioner()
/* 24:   */   {
/* 25:55 */     return this.M;
/* 26:   */   }
/* 27:   */   
/* 28:   */   public IterationMonitor getIterationMonitor()
/* 29:   */   {
/* 30:59 */     return this.iter;
/* 31:   */   }
/* 32:   */   
/* 33:   */   public void setIterationMonitor(IterationMonitor iter)
/* 34:   */   {
/* 35:63 */     this.iter = iter;
/* 36:   */   }
/* 37:   */   
/* 38:   */   protected void checkSizes(Matrix A, Vector b, Vector x)
/* 39:   */   {
/* 40:71 */     if (!A.isSquare()) {
/* 41:72 */       throw new IllegalArgumentException("!A.isSquare()");
/* 42:   */     }
/* 43:73 */     if (b.size() != A.numRows()) {
/* 44:74 */       throw new IllegalArgumentException("b.size() != A.numRows()");
/* 45:   */     }
/* 46:75 */     if (b.size() != x.size()) {
/* 47:76 */       throw new IllegalArgumentException("b.size() != x.size()");
/* 48:   */     }
/* 49:   */   }
/* 50:   */   
/* 51:   */   private static class IdentityPreconditioner
/* 52:   */     implements Preconditioner
/* 53:   */   {
/* 54:   */     public Vector apply(Vector b, Vector x)
/* 55:   */     {
/* 56:85 */       return x.set(b);
/* 57:   */     }
/* 58:   */     
/* 59:   */     public Vector transApply(Vector b, Vector x)
/* 60:   */     {
/* 61:89 */       return x.set(b);
/* 62:   */     }
/* 63:   */     
/* 64:   */     public void setMatrix(Matrix A) {}
/* 65:   */   }
/* 66:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.sparse.AbstractIterativeSolver
 * JD-Core Version:    0.7.0.1
 */