/*  1:   */ package weka.classifiers.functions.activation;
/*  2:   */ 
/*  3:   */ public class Sigmoid
/*  4:   */   implements ActivationFunction
/*  5:   */ {
/*  6:   */   public String globalInfo()
/*  7:   */   {
/*  8:40 */     return "Computes sigmoid activation function f(x) = 1 / (1 + e^(-x))";
/*  9:   */   }
/* 10:   */   
/* 11:   */   public double activation(double x, double[] d, int index)
/* 12:   */   {
/* 13:48 */     double output = 1.0D / (1.0D + Math.exp(-x));
/* 14:51 */     if (d != null) {
/* 15:52 */       d[index] = (output * (1.0D - output));
/* 16:   */     }
/* 17:55 */     return output;
/* 18:   */   }
/* 19:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.activation.Sigmoid
 * JD-Core Version:    0.7.0.1
 */