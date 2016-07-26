/*  1:   */ package weka.classifiers.trees.j48;
/*  2:   */ 
/*  3:   */ import java.io.PrintStream;
/*  4:   */ import weka.core.RevisionHandler;
/*  5:   */ import weka.core.RevisionUtils;
/*  6:   */ import weka.core.Statistics;
/*  7:   */ 
/*  8:   */ public class Stats
/*  9:   */   implements RevisionHandler
/* 10:   */ {
/* 11:   */   public static double addErrs(double N, double e, float CF)
/* 12:   */   {
/* 13:50 */     if (CF > 0.5D)
/* 14:   */     {
/* 15:51 */       System.err.println("WARNING: confidence value for pruning  too high. Error estimate not modified.");
/* 16:   */       
/* 17:53 */       return 0.0D;
/* 18:   */     }
/* 19:58 */     if (e < 1.0D)
/* 20:   */     {
/* 21:62 */       double base = N * (1.0D - Math.pow(CF, 1.0D / N));
/* 22:63 */       if (e == 0.0D) {
/* 23:64 */         return base;
/* 24:   */       }
/* 25:68 */       return base + e * (addErrs(N, 1.0D, CF) - base);
/* 26:   */     }
/* 27:73 */     if (e + 0.5D >= N) {
/* 28:76 */       return Math.max(N - e, 0.0D);
/* 29:   */     }
/* 30:80 */     double z = Statistics.normalInverse(1.0F - CF);
/* 31:   */     
/* 32:   */ 
/* 33:83 */     double f = (e + 0.5D) / N;
/* 34:84 */     double r = (f + z * z / (2.0D * N) + z * Math.sqrt(f / N - f * f / N + z * z / (4.0D * N * N))) / (1.0D + z * z / N);
/* 35:   */     
/* 36:   */ 
/* 37:   */ 
/* 38:   */ 
/* 39:   */ 
/* 40:90 */     return r * N - e;
/* 41:   */   }
/* 42:   */   
/* 43:   */   public String getRevision()
/* 44:   */   {
/* 45:99 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 46:   */   }
/* 47:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.Stats
 * JD-Core Version:    0.7.0.1
 */