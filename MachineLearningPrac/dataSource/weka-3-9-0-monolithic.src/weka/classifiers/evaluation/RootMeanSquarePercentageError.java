/*   1:    */ package weka.classifiers.evaluation;
/*   2:    */ 
/*   3:    */ import java.util.ArrayList;
/*   4:    */ import java.util.List;
/*   5:    */ import weka.core.Instance;
/*   6:    */ import weka.core.Utils;
/*   7:    */ 
/*   8:    */ public class RootMeanSquarePercentageError
/*   9:    */   extends AbstractEvaluationMetric
/*  10:    */   implements StandardEvaluationMetric
/*  11:    */ {
/*  12: 39 */   protected double m_SumSquarePercentageError = 0.0D;
/*  13:    */   
/*  14:    */   public boolean appliesToNominalClass()
/*  15:    */   {
/*  16: 46 */     return false;
/*  17:    */   }
/*  18:    */   
/*  19:    */   public boolean appliesToNumericClass()
/*  20:    */   {
/*  21: 54 */     return true;
/*  22:    */   }
/*  23:    */   
/*  24:    */   public String getMetricName()
/*  25:    */   {
/*  26: 62 */     return "RMSPE";
/*  27:    */   }
/*  28:    */   
/*  29:    */   public String getMetricDescription()
/*  30:    */   {
/*  31: 69 */     return "The root mean square percentage error.";
/*  32:    */   }
/*  33:    */   
/*  34:    */   public void updateStatsForClassifier(double[] predictedDistribution, Instance instance) {}
/*  35:    */   
/*  36:    */   public void updateStatsForPredictor(double predictedValue, Instance instance)
/*  37:    */   {
/*  38: 87 */     if ((!instance.classIsMissing()) && 
/*  39: 88 */       (!Utils.isMissingValue(predictedValue)))
/*  40:    */     {
/*  41: 89 */       double relativeError = (instance.classValue() - predictedValue) / instance.classValue();
/*  42: 90 */       this.m_SumSquarePercentageError += instance.weight() * relativeError * relativeError;
/*  43:    */     }
/*  44:    */   }
/*  45:    */   
/*  46:    */   public List<String> getStatisticNames()
/*  47:    */   {
/*  48:101 */     ArrayList<String> names = new ArrayList();
/*  49:102 */     names.add("rmspe");
/*  50:    */     
/*  51:104 */     return names;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String toSummaryString()
/*  55:    */   {
/*  56:113 */     return "Root mean square percentage error  " + Utils.doubleToString(getStatistic("rmspe"), 12, 4) + "\n";
/*  57:    */   }
/*  58:    */   
/*  59:    */   public double getStatistic(String name)
/*  60:    */   {
/*  61:123 */     if (!name.equals("rmspe")) {
/*  62:124 */       throw new AbstractEvaluationMetric.UnknownStatisticException(this, "Statistic " + name + " is unknown.");
/*  63:    */     }
/*  64:127 */     return Math.sqrt(this.m_SumSquarePercentageError / (this.m_baseEvaluation.m_WithClass - this.m_baseEvaluation.m_Unclassified));
/*  65:    */   }
/*  66:    */   
/*  67:    */   public boolean statisticIsMaximisable(String statName)
/*  68:    */   {
/*  69:137 */     return false;
/*  70:    */   }
/*  71:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.RootMeanSquarePercentageError
 * JD-Core Version:    0.7.0.1
 */