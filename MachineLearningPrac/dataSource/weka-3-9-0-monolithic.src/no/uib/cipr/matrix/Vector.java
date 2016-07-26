/*   1:    */ package no.uib.cipr.matrix;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ 
/*   5:    */ public abstract interface Vector
/*   6:    */   extends Iterable<VectorEntry>, Serializable
/*   7:    */ {
/*   8:    */   public abstract int size();
/*   9:    */   
/*  10:    */   public abstract void set(int paramInt, double paramDouble);
/*  11:    */   
/*  12:    */   public abstract void add(int paramInt, double paramDouble);
/*  13:    */   
/*  14:    */   public abstract double get(int paramInt);
/*  15:    */   
/*  16:    */   public abstract Vector copy();
/*  17:    */   
/*  18:    */   public abstract Vector zero();
/*  19:    */   
/*  20:    */   public abstract Vector scale(double paramDouble);
/*  21:    */   
/*  22:    */   public abstract Vector set(Vector paramVector);
/*  23:    */   
/*  24:    */   public abstract Vector set(double paramDouble, Vector paramVector);
/*  25:    */   
/*  26:    */   public abstract Vector add(Vector paramVector);
/*  27:    */   
/*  28:    */   public abstract Vector add(double paramDouble, Vector paramVector);
/*  29:    */   
/*  30:    */   public abstract double dot(Vector paramVector);
/*  31:    */   
/*  32:    */   public abstract double norm(Norm paramNorm);
/*  33:    */   
/*  34:    */   public static enum Norm
/*  35:    */   {
/*  36:164 */     One,  Two,  TwoRobust,  Infinity;
/*  37:    */     
/*  38:    */     private Norm() {}
/*  39:    */   }
/*  40:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     no.uib.cipr.matrix.Vector
 * JD-Core Version:    0.7.0.1
 */