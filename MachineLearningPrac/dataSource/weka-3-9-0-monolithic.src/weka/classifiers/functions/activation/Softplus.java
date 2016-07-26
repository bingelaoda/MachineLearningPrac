/*  1:   */ package weka.classifiers.functions.activation;
/*  2:   */ 
/*  3:   */ public class Softplus
/*  4:   */   implements ActivationFunction
/*  5:   */ {
/*  6:   */   public String globalInfo()
/*  7:   */   {
/*  8:40 */     return "Computes softplus activation function f(x) = ln(1 + e^(x))";
/*  9:   */   }
/* 10:   */   
/* 11:   */   public double activation(double x, double[] d, int index)
/* 12:   */   {
/* 13:48 */     double val = Math.exp(x);
/* 14:49 */     double output = Math.log(1.0D + val);
/* 15:52 */     if (d != null) {
/* 16:53 */       d[index] = (val / (1.0D + val));
/* 17:   */     }
/* 18:56 */     return output;
/* 19:   */   }
/* 20:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.activation.Softplus
 * JD-Core Version:    0.7.0.1
 */