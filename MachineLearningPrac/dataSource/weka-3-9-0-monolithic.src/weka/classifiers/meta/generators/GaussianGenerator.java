/*   1:    */ package weka.classifiers.meta.generators;
/*   2:    */ 
/*   3:    */ import java.util.Random;
/*   4:    */ 
/*   5:    */ public class GaussianGenerator
/*   6:    */   extends RandomizableDistributionGenerator
/*   7:    */   implements NumericAttributeGenerator
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = 4860675869078046797L;
/*  10:    */   
/*  11:    */   public String globalInfo()
/*  12:    */   {
/*  13: 70 */     return "An artificial data generator that uses a single Gaussian distribution.\n\nIf a mixture of Gaussians is required, use the EM Generator.";
/*  14:    */   }
/*  15:    */   
/*  16:    */   public double generate()
/*  17:    */   {
/*  18: 81 */     double gaussian = this.m_Random.nextGaussian();
/*  19: 82 */     double value = this.m_Mean + gaussian * this.m_StandardDeviation;
/*  20: 83 */     return value;
/*  21:    */   }
/*  22:    */   
/*  23:    */   public double getProbabilityOf(double valuex)
/*  24:    */   {
/*  25: 94 */     double twopisqrt = Math.sqrt(6.283185307179586D);
/*  26: 95 */     double left = 1.0D / (this.m_StandardDeviation * twopisqrt);
/*  27: 96 */     double diffsquared = Math.pow(valuex - this.m_Mean, 2.0D);
/*  28: 97 */     double bottomright = 2.0D * Math.pow(this.m_StandardDeviation, 2.0D);
/*  29: 98 */     double brackets = -1.0D * (diffsquared / bottomright);
/*  30:    */     
/*  31:100 */     double probx = left * Math.exp(brackets);
/*  32:    */     
/*  33:102 */     return probx;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public double getLogProbabilityOf(double valuex)
/*  37:    */   {
/*  38:113 */     double twopisqrt = Math.log(Math.sqrt(6.283185307179586D));
/*  39:114 */     double left = -(Math.log(this.m_StandardDeviation) + twopisqrt);
/*  40:115 */     double diffsquared = Math.pow(valuex - this.m_Mean, 2.0D);
/*  41:116 */     double bottomright = 2.0D * Math.pow(this.m_StandardDeviation, 2.0D);
/*  42:117 */     double brackets = -1.0D * (diffsquared / bottomright);
/*  43:    */     
/*  44:119 */     double probx = left + brackets;
/*  45:    */     
/*  46:121 */     return probx;
/*  47:    */   }
/*  48:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.generators.GaussianGenerator
 * JD-Core Version:    0.7.0.1
 */