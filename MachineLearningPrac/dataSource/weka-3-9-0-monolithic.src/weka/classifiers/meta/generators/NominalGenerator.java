/*   1:    */ package weka.classifiers.meta.generators;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Random;
/*   5:    */ import weka.core.Attribute;
/*   6:    */ import weka.core.Instance;
/*   7:    */ import weka.core.Instances;
/*   8:    */ 
/*   9:    */ public class NominalGenerator
/*  10:    */   extends RandomizableGenerator
/*  11:    */   implements NominalAttributeGenerator
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = 5254947213887016283L;
/*  14:    */   protected double[] m_AttCounts;
/*  15:    */   
/*  16:    */   public String globalInfo()
/*  17:    */   {
/*  18: 69 */     return "A generator for nominal attributes.\n\nGenerates artificial data for nominal attributes.  Each attribute value is considered to be possible, i.e. the probability of any value is always non-zero.";
/*  19:    */   }
/*  20:    */   
/*  21:    */   public void buildGenerator(Instances someinstances, Attribute att)
/*  22:    */   {
/*  23: 84 */     this.m_AttCounts = new double[att.numValues()];
/*  24: 85 */     for (int i = 0; i < this.m_AttCounts.length; i++) {
/*  25: 86 */       this.m_AttCounts[i] = 1.0D;
/*  26:    */     }
/*  27: 90 */     Enumeration<Instance> instancesEnum = someinstances.enumerateInstances();
/*  28: 91 */     int totalCounts = this.m_AttCounts.length;
/*  29: 92 */     while (instancesEnum.hasMoreElements())
/*  30:    */     {
/*  31: 93 */       Instance aninst = (Instance)instancesEnum.nextElement();
/*  32: 94 */       if (!aninst.isMissing(att))
/*  33:    */       {
/*  34: 95 */         this.m_AttCounts[((int)aninst.value(att))] += 1.0D;
/*  35: 96 */         totalCounts++;
/*  36:    */       }
/*  37:    */     }
/*  38:101 */     for (int i = 0; i < this.m_AttCounts.length; i++) {
/*  39:102 */       this.m_AttCounts[i] /= totalCounts;
/*  40:    */     }
/*  41:    */   }
/*  42:    */   
/*  43:    */   public double generate()
/*  44:    */   {
/*  45:113 */     double prob = this.m_Random.nextDouble();
/*  46:    */     
/*  47:115 */     double probSoFar = 0.0D;
/*  48:116 */     for (int i = 0; i < this.m_AttCounts.length; i++)
/*  49:    */     {
/*  50:117 */       probSoFar += this.m_AttCounts[i];
/*  51:118 */       if (prob <= probSoFar) {
/*  52:119 */         return i;
/*  53:    */       }
/*  54:    */     }
/*  55:122 */     return 0.0D;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public double getProbabilityOf(double valuex)
/*  59:    */   {
/*  60:133 */     return this.m_AttCounts[((int)valuex)];
/*  61:    */   }
/*  62:    */   
/*  63:    */   public double getLogProbabilityOf(double valuex)
/*  64:    */   {
/*  65:144 */     return Math.log(getProbabilityOf(valuex));
/*  66:    */   }
/*  67:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.generators.NominalGenerator
 * JD-Core Version:    0.7.0.1
 */