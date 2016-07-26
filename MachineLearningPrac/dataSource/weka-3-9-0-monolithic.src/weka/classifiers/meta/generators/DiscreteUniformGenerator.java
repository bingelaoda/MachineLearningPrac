/*   1:    */ package weka.classifiers.meta.generators;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ import java.util.Random;
/*   5:    */ import weka.core.Capabilities;
/*   6:    */ import weka.core.Capabilities.Capability;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ 
/*  10:    */ public class DiscreteUniformGenerator
/*  11:    */   extends RandomizableGenerator
/*  12:    */   implements InstanceHandler, NumericAttributeGenerator
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = 3639933602298510366L;
/*  15:    */   protected double[] m_Probabilities;
/*  16:    */   
/*  17:    */   public String globalInfo()
/*  18:    */   {
/*  19: 72 */     return "An artificial data generator that uses discrete buckets for values.\n\nIn this discrete uniform generator, all buckets are given the same probability, regardless of how many values fall into each bucket.  This is not to be confused with the discrete generator which gives every bucket a probability based on how full the bucket is.";
/*  20:    */   }
/*  21:    */   
/*  22:    */   public Capabilities getCapabilities()
/*  23:    */   {
/*  24: 90 */     Capabilities result = new Capabilities(this);
/*  25:    */     
/*  26: 92 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  27: 93 */     result.enableAllClasses();
/*  28: 94 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  29: 95 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  30:    */     
/*  31: 97 */     return result;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public void buildGenerator(Instances someinstances)
/*  35:    */     throws Exception
/*  36:    */   {
/*  37:109 */     getCapabilities().testWithFail(someinstances);
/*  38:    */     
/*  39:111 */     someinstances = new Instances(someinstances);
/*  40:112 */     someinstances.deleteWithMissing(0);
/*  41:    */     
/*  42:    */ 
/*  43:115 */     double[] values = new double[someinstances.numInstances()];
/*  44:117 */     for (int i = 0; i < someinstances.numInstances(); i++)
/*  45:    */     {
/*  46:118 */       Instance aninst = someinstances.instance(i);
/*  47:119 */       values[i] = aninst.value(0);
/*  48:    */     }
/*  49:122 */     Arrays.sort(values);
/*  50:    */     
/*  51:124 */     double count = 1.0D;
/*  52:125 */     for (int i = 1; i < values.length; i++) {
/*  53:126 */       if (values[i] != values[(i - 1)]) {
/*  54:127 */         count += 1.0D;
/*  55:    */       }
/*  56:    */     }
/*  57:131 */     double[] allvals = new double[(int)count];
/*  58:132 */     int position = 0;
/*  59:133 */     allvals[0] = values[0];
/*  60:136 */     for (int i = 1; i < values.length; i++) {
/*  61:137 */       if (values[i] != values[(i - 1)])
/*  62:    */       {
/*  63:138 */         position++;
/*  64:139 */         allvals[position] = values[i];
/*  65:    */       }
/*  66:    */     }
/*  67:143 */     this.m_Probabilities = allvals;
/*  68:    */   }
/*  69:    */   
/*  70:    */   public double generate()
/*  71:    */   {
/*  72:152 */     double aprob = this.m_Random.nextDouble();
/*  73:153 */     double gap = 1.0D / this.m_Probabilities.length;
/*  74:154 */     int position = (int)(aprob / gap);
/*  75:155 */     if (position < 0) {
/*  76:156 */       position = 0;
/*  77:    */     }
/*  78:157 */     if (position > this.m_Probabilities.length - 1) {
/*  79:158 */       position = this.m_Probabilities.length - 1;
/*  80:    */     }
/*  81:160 */     return this.m_Probabilities[position];
/*  82:    */   }
/*  83:    */   
/*  84:    */   public double getProbabilityOf(double valuex)
/*  85:    */   {
/*  86:172 */     for (int i = 0; i < this.m_Probabilities.length; i++) {
/*  87:173 */       if (valuex == this.m_Probabilities[i]) {
/*  88:174 */         return 1.0D / (this.m_Probabilities.length + 1.0D);
/*  89:    */       }
/*  90:    */     }
/*  91:177 */     return 1.0D / (this.m_Probabilities.length + 1.0D);
/*  92:    */   }
/*  93:    */   
/*  94:    */   public double getLogProbabilityOf(double valuex)
/*  95:    */   {
/*  96:187 */     return Math.log(getProbabilityOf(valuex));
/*  97:    */   }
/*  98:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.generators.DiscreteUniformGenerator
 * JD-Core Version:    0.7.0.1
 */