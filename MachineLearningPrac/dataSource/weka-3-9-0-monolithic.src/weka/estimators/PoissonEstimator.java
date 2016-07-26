/*   1:    */ package weka.estimators;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import weka.core.Capabilities;
/*   5:    */ import weka.core.Capabilities.Capability;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ import weka.core.Utils;
/*   8:    */ 
/*   9:    */ public class PoissonEstimator
/*  10:    */   extends Estimator
/*  11:    */   implements IncrementalEstimator
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = 7669362595289236662L;
/*  14:    */   private double m_NumValues;
/*  15:    */   private double m_SumOfValues;
/*  16:    */   private double m_Lambda;
/*  17:    */   
/*  18:    */   private double logFac(double x)
/*  19:    */   {
/*  20: 64 */     double result = 0.0D;
/*  21: 65 */     for (double i = 2.0D; i <= x; i += 1.0D) {
/*  22: 66 */       result += Math.log(i);
/*  23:    */     }
/*  24: 68 */     return result;
/*  25:    */   }
/*  26:    */   
/*  27:    */   private double Poisson(double x)
/*  28:    */   {
/*  29: 79 */     return Math.exp(-this.m_Lambda + x * Math.log(this.m_Lambda) - logFac(x));
/*  30:    */   }
/*  31:    */   
/*  32:    */   public void addValue(double data, double weight)
/*  33:    */   {
/*  34: 90 */     this.m_NumValues += weight;
/*  35: 91 */     this.m_SumOfValues += data * weight;
/*  36: 92 */     if (this.m_NumValues != 0.0D) {
/*  37: 93 */       this.m_Lambda = (this.m_SumOfValues / this.m_NumValues);
/*  38:    */     }
/*  39:    */   }
/*  40:    */   
/*  41:    */   public double getProbability(double data)
/*  42:    */   {
/*  43:105 */     return Poisson(data);
/*  44:    */   }
/*  45:    */   
/*  46:    */   public String toString()
/*  47:    */   {
/*  48:111 */     return "Poisson Lambda = " + Utils.doubleToString(this.m_Lambda, 4, 2) + "\n";
/*  49:    */   }
/*  50:    */   
/*  51:    */   public Capabilities getCapabilities()
/*  52:    */   {
/*  53:120 */     Capabilities result = super.getCapabilities();
/*  54:121 */     result.disableAll();
/*  55:124 */     if (!this.m_noClass)
/*  56:    */     {
/*  57:125 */       result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  58:126 */       result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  59:    */     }
/*  60:    */     else
/*  61:    */     {
/*  62:128 */       result.enable(Capabilities.Capability.NO_CLASS);
/*  63:    */     }
/*  64:132 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  65:133 */     return result;
/*  66:    */   }
/*  67:    */   
/*  68:    */   public String getRevision()
/*  69:    */   {
/*  70:142 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  71:    */   }
/*  72:    */   
/*  73:    */   public static void main(String[] argv)
/*  74:    */   {
/*  75:    */     try
/*  76:    */     {
/*  77:153 */       if (argv.length == 0)
/*  78:    */       {
/*  79:154 */         System.out.println("Please specify a set of instances.");
/*  80:155 */         return;
/*  81:    */       }
/*  82:157 */       PoissonEstimator newEst = new PoissonEstimator();
/*  83:158 */       for (int i = 0; i < argv.length; i++)
/*  84:    */       {
/*  85:159 */         double current = Double.valueOf(argv[i]).doubleValue();
/*  86:160 */         System.out.println(newEst);
/*  87:161 */         System.out.println("Prediction for " + current + " = " + newEst.getProbability(current));
/*  88:    */         
/*  89:163 */         newEst.addValue(current, 1.0D);
/*  90:    */       }
/*  91:    */     }
/*  92:    */     catch (Exception e)
/*  93:    */     {
/*  94:167 */       System.out.println(e.getMessage());
/*  95:    */     }
/*  96:    */   }
/*  97:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.estimators.PoissonEstimator
 * JD-Core Version:    0.7.0.1
 */