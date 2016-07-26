/*  1:   */ package weka.classifiers.functions.loss;
/*  2:   */ 
/*  3:   */ public class SquaredError
/*  4:   */   implements LossFunction
/*  5:   */ {
/*  6:   */   public String globalInfo()
/*  7:   */   {
/*  8:45 */     return "Squared error for MLPRegressor and MLPClassifier:\nloss(a, b) = (a-b)^2";
/*  9:   */   }
/* 10:   */   
/* 11:   */   public double loss(double pred, double actual)
/* 12:   */   {
/* 13:57 */     return (pred - actual) * (pred - actual);
/* 14:   */   }
/* 15:   */   
/* 16:   */   public double derivative(double pred, double actual)
/* 17:   */   {
/* 18:68 */     return pred - actual;
/* 19:   */   }
/* 20:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.loss.SquaredError
 * JD-Core Version:    0.7.0.1
 */