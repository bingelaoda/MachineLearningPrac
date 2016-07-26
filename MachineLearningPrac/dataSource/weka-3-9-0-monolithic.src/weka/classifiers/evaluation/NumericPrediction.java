/*   1:    */ package weka.classifiers.evaluation;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.RevisionHandler;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ 
/*   7:    */ public class NumericPrediction
/*   8:    */   implements Prediction, Serializable, RevisionHandler
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -4880216423674233887L;
/*  11: 44 */   private double m_Actual = MISSING_VALUE;
/*  12: 47 */   private double m_Predicted = MISSING_VALUE;
/*  13: 50 */   private double m_Weight = 1.0D;
/*  14:    */   private double[][] m_PredictionIntervals;
/*  15:    */   
/*  16:    */   public NumericPrediction(double actual, double predicted)
/*  17:    */   {
/*  18: 62 */     this(actual, predicted, 1.0D);
/*  19:    */   }
/*  20:    */   
/*  21:    */   public NumericPrediction(double actual, double predicted, double weight)
/*  22:    */   {
/*  23: 73 */     this(actual, predicted, weight, new double[0][]);
/*  24:    */   }
/*  25:    */   
/*  26:    */   public NumericPrediction(double actual, double predicted, double weight, double[][] predInt)
/*  27:    */   {
/*  28: 87 */     this.m_Actual = actual;
/*  29: 88 */     this.m_Predicted = predicted;
/*  30: 89 */     this.m_Weight = weight;
/*  31: 90 */     setPredictionIntervals(predInt);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public double actual()
/*  35:    */   {
/*  36:100 */     return this.m_Actual;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public double predicted()
/*  40:    */   {
/*  41:110 */     return this.m_Predicted;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public double weight()
/*  45:    */   {
/*  46:120 */     return this.m_Weight;
/*  47:    */   }
/*  48:    */   
/*  49:    */   public double error()
/*  50:    */   {
/*  51:132 */     if ((this.m_Actual == MISSING_VALUE) || (this.m_Predicted == MISSING_VALUE)) {
/*  52:134 */       return MISSING_VALUE;
/*  53:    */     }
/*  54:136 */     return this.m_Predicted - this.m_Actual;
/*  55:    */   }
/*  56:    */   
/*  57:    */   public void setPredictionIntervals(double[][] predInt)
/*  58:    */   {
/*  59:145 */     this.m_PredictionIntervals = ((double[][])predInt.clone());
/*  60:    */   }
/*  61:    */   
/*  62:    */   public double[][] predictionIntervals()
/*  63:    */   {
/*  64:156 */     return this.m_PredictionIntervals;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public String toString()
/*  68:    */   {
/*  69:165 */     StringBuffer sb = new StringBuffer();
/*  70:166 */     sb.append("NUM: ").append(actual()).append(' ').append(predicted());
/*  71:167 */     sb.append(' ').append(weight());
/*  72:168 */     return sb.toString();
/*  73:    */   }
/*  74:    */   
/*  75:    */   public String getRevision()
/*  76:    */   {
/*  77:177 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  78:    */   }
/*  79:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.NumericPrediction
 * JD-Core Version:    0.7.0.1
 */