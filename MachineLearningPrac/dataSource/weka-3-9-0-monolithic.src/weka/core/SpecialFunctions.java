/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ 
/*   5:    */ public final class SpecialFunctions
/*   6:    */   implements RevisionHandler
/*   7:    */ {
/*   8: 35 */   private static double log2 = Math.log(2.0D);
/*   9:    */   
/*  10:    */   public static double lnFactorial(double x)
/*  11:    */   {
/*  12: 45 */     return Statistics.lnGamma(x + 1.0D);
/*  13:    */   }
/*  14:    */   
/*  15:    */   public static double log2Binomial(double a, double b)
/*  16:    */   {
/*  17: 57 */     if (Utils.gr(b, a)) {
/*  18: 58 */       throw new ArithmeticException("Can't compute binomial coefficient.");
/*  19:    */     }
/*  20: 60 */     return (lnFactorial(a) - lnFactorial(b) - lnFactorial(a - b)) / log2;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public static double log2Multinomial(double a, double[] bs)
/*  24:    */   {
/*  25: 73 */     double sum = 0.0D;
/*  26: 76 */     for (int i = 0; i < bs.length; i++)
/*  27:    */     {
/*  28: 77 */       if (Utils.gr(bs[i], a)) {
/*  29: 78 */         throw new ArithmeticException("Can't compute multinomial coefficient.");
/*  30:    */       }
/*  31: 81 */       sum += lnFactorial(bs[i]);
/*  32:    */     }
/*  33: 84 */     return (lnFactorial(a) - sum) / log2;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public String getRevision()
/*  37:    */   {
/*  38: 93 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  39:    */   }
/*  40:    */   
/*  41:    */   public static void main(String[] ops)
/*  42:    */   {
/*  43:101 */     double[] doubles = { 1.0D, 2.0D, 3.0D };
/*  44:    */     
/*  45:103 */     System.out.println("6!: " + Math.exp(lnFactorial(6.0D)));
/*  46:104 */     System.out.println("Binomial 6 over 2: " + Math.pow(2.0D, log2Binomial(6.0D, 2.0D)));
/*  47:    */     
/*  48:106 */     System.out.println("Multinomial 6 over 1, 2, 3: " + Math.pow(2.0D, log2Multinomial(6.0D, doubles)));
/*  49:    */   }
/*  50:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.SpecialFunctions
 * JD-Core Version:    0.7.0.1
 */