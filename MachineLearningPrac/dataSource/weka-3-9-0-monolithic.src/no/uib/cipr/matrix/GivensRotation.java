/*  1:   */ package no.uib.cipr.matrix;
/*  2:   */ 
/*  3:   */ public class GivensRotation
/*  4:   */ {
/*  5:   */   private final double c;
/*  6:   */   private final double s;
/*  7:   */   
/*  8:   */   public GivensRotation(double x, double y)
/*  9:   */   {
/* 10:43 */     double roe = Math.abs(x) > Math.abs(y) ? x : y;
/* 11:   */     
/* 12:45 */     double scale = Math.abs(x) + Math.abs(y);
/* 13:46 */     if (scale != 0.0D)
/* 14:   */     {
/* 15:47 */       double xs = x / scale;
/* 16:48 */       double ys = y / scale;
/* 17:49 */       double r = scale * Math.sqrt(xs * xs + ys * ys);
/* 18:50 */       if (roe < 0.0D) {
/* 19:51 */         r *= -1.0D;
/* 20:   */       }
/* 21:52 */       this.c = (x / r);
/* 22:53 */       this.s = (y / r);
/* 23:   */     }
/* 24:   */     else
/* 25:   */     {
/* 26:55 */       this.c = 1.0D;
/* 27:56 */       this.s = 0.0D;
/* 28:   */     }
/* 29:   */   }
/* 30:   */   
/* 31:   */   public void apply(Matrix H, int column, int i1, int i2)
/* 32:   */   {
/* 33:73 */     double temp = this.c * H.get(i1, column) + this.s * H.get(i2, column);
/* 34:74 */     H.set(i2, column, -this.s * H.get(i1, column) + this.c * H.get(i2, column));
/* 35:75 */     H.set(i1, column, temp);
/* 36:   */   }
/* 37:   */   
/* 38:   */   public void apply(Vector x, int i1, int i2)
/* 39:   */   {
/* 40:89 */     double temp = this.c * x.get(i1) + this.s * x.get(i2);
/* 41:90 */     x.set(i2, -this.s * x.get(i1) + this.c * x.get(i2));
/* 42:91 */     x.set(i1, temp);
/* 43:   */   }
/* 44:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.GivensRotation
 * JD-Core Version:    0.7.0.1
 */