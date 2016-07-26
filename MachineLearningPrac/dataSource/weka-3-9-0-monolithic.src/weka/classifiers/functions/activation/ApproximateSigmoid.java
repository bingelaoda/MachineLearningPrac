/*  1:   */ package weka.classifiers.functions.activation;
/*  2:   */ 
/*  3:   */ public class ApproximateSigmoid
/*  4:   */   implements ActivationFunction
/*  5:   */ {
/*  6:   */   public String globalInfo()
/*  7:   */   {
/*  8:40 */     return "Computes approximate (fast) version of sigmoid activation function f(x) = 1 / (1 + e^(-x))";
/*  9:   */   }
/* 10:   */   
/* 11:   */   public double activation(double x, double[] d, int index)
/* 12:   */   {
/* 13:49 */     double y = 1.0D + -x / 4096.0D;
/* 14:50 */     x = y * y;
/* 15:51 */     x *= x;
/* 16:52 */     x *= x;
/* 17:53 */     x *= x;
/* 18:54 */     x *= x;
/* 19:55 */     x *= x;
/* 20:56 */     x *= x;
/* 21:57 */     x *= x;
/* 22:58 */     x *= x;
/* 23:59 */     x *= x;
/* 24:60 */     x *= x;
/* 25:61 */     x *= x;
/* 26:62 */     double output = 1.0D / (1.0D + x);
/* 27:65 */     if (d != null) {
/* 28:66 */       d[index] = (output * (1.0D - output) / y);
/* 29:   */     }
/* 30:69 */     return output;
/* 31:   */   }
/* 32:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.activation.ApproximateSigmoid
 * JD-Core Version:    0.7.0.1
 */