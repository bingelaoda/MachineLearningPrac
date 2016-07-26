/*   1:    */ package weka.classifiers.evaluation;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import java.util.List;
/*   6:    */ import java.util.Set;
/*   7:    */ import weka.core.PluginManager;
/*   8:    */ 
/*   9:    */ public abstract class AbstractEvaluationMetric
/*  10:    */   implements Serializable
/*  11:    */ {
/*  12:    */   private static final long serialVersionUID = -924507718482386887L;
/*  13:    */   protected Evaluation m_baseEvaluation;
/*  14:    */   
/*  15:    */   public static ArrayList<AbstractEvaluationMetric> getPluginMetrics()
/*  16:    */   {
/*  17: 50 */     ArrayList<AbstractEvaluationMetric> pluginMetricsList = null;
/*  18: 51 */     Set<String> pluginMetrics = PluginManager.getPluginNamesOfType(AbstractEvaluationMetric.class.getName());
/*  19: 54 */     if (pluginMetrics != null)
/*  20:    */     {
/*  21: 55 */       pluginMetricsList = new ArrayList();
/*  22: 57 */       for (String metric : pluginMetrics) {
/*  23:    */         try
/*  24:    */         {
/*  25: 59 */           Object impl = PluginManager.getPluginInstance(AbstractEvaluationMetric.class.getName(), metric);
/*  26: 62 */           if ((impl instanceof AbstractEvaluationMetric)) {
/*  27: 63 */             pluginMetricsList.add((AbstractEvaluationMetric)impl);
/*  28:    */           }
/*  29:    */         }
/*  30:    */         catch (Exception ex)
/*  31:    */         {
/*  32: 66 */           ex.printStackTrace();
/*  33:    */         }
/*  34:    */       }
/*  35:    */     }
/*  36: 70 */     return pluginMetricsList;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public class UnknownStatisticException
/*  40:    */     extends IllegalArgumentException
/*  41:    */   {
/*  42:    */     private static final long serialVersionUID = -8787045492227999839L;
/*  43:    */     
/*  44:    */     public UnknownStatisticException(String message)
/*  45:    */     {
/*  46: 91 */       super();
/*  47:    */     }
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void setBaseEvaluation(Evaluation eval)
/*  51:    */   {
/*  52:108 */     this.m_baseEvaluation = eval;
/*  53:    */   }
/*  54:    */   
/*  55:    */   public abstract boolean appliesToNominalClass();
/*  56:    */   
/*  57:    */   public abstract boolean appliesToNumericClass();
/*  58:    */   
/*  59:    */   public abstract String getMetricName();
/*  60:    */   
/*  61:    */   public abstract String getMetricDescription();
/*  62:    */   
/*  63:    */   public abstract List<String> getStatisticNames();
/*  64:    */   
/*  65:    */   public abstract double getStatistic(String paramString);
/*  66:    */   
/*  67:    */   public boolean statisticIsMaximisable(String statName)
/*  68:    */   {
/*  69:169 */     return true;
/*  70:    */   }
/*  71:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.evaluation.AbstractEvaluationMetric
 * JD-Core Version:    0.7.0.1
 */