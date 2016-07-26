/*   1:    */ package weka.classifiers.meta.generators;
/*   2:    */ 
/*   3:    */ import java.util.Random;
/*   4:    */ 
/*   5:    */ public class UniformDataGenerator
/*   6:    */   extends RandomizableRangedGenerator
/*   7:    */   implements NumericAttributeGenerator
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = -6390354660638644832L;
/*  10:    */   
/*  11:    */   public String globalInfo()
/*  12:    */   {
/*  13: 70 */     return "A uniform artificial data generator.\n\nThis generator uses a uniform data model - all values have the same probability, and generated values must fall within the range given to the generator.";
/*  14:    */   }
/*  15:    */   
/*  16:    */   public double generate()
/*  17:    */   {
/*  18: 83 */     double range = this.m_UpperRange - this.m_LowerRange;
/*  19: 84 */     return this.m_Random.nextDouble() * range + this.m_LowerRange;
/*  20:    */   }
/*  21:    */   
/*  22:    */   public double getProbabilityOf(double somedata)
/*  23:    */   {
/*  24: 96 */     double range = this.m_UpperRange - this.m_LowerRange;
/*  25: 97 */     if ((range <= 0.0D) || (somedata > this.m_UpperRange) || (somedata < this.m_LowerRange)) {
/*  26: 98 */       return 4.9E-324D;
/*  27:    */     }
/*  28:101 */     return 1.0D / range;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public double getLogProbabilityOf(double somedata)
/*  32:    */   {
/*  33:112 */     double range = this.m_UpperRange - this.m_LowerRange;
/*  34:113 */     if ((range <= 0.0D) || (somedata < this.m_LowerRange) || (somedata > this.m_UpperRange)) {
/*  35:115 */       return Math.log(4.9E-324D);
/*  36:    */     }
/*  37:117 */     return -Math.log(range);
/*  38:    */   }
/*  39:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.generators.UniformDataGenerator
 * JD-Core Version:    0.7.0.1
 */