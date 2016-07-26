/*   1:    */ package weka.classifiers.meta.generators;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ import java.util.Random;
/*   5:    */ import weka.core.Capabilities;
/*   6:    */ import weka.core.Capabilities.Capability;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ 
/*  10:    */ public class DiscreteGenerator
/*  11:    */   extends RandomizableGenerator
/*  12:    */   implements InstanceHandler, NumericAttributeGenerator
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = -2990312384506940726L;
/*  15:    */   protected double[][] m_Probabilities;
/*  16: 69 */   protected double m_Unseen = 4.9E-324D;
/*  17:    */   
/*  18:    */   public String globalInfo()
/*  19:    */   {
/*  20: 77 */     return "An artificial data generator that uses discrete buckets for values.\n\nIn this discrete generator, values are ranked according to how often they appear.  This is not to be confused with the discrete uniform generator which gives every bucket the  same probability.";
/*  21:    */   }
/*  22:    */   
/*  23:    */   public Capabilities getCapabilities()
/*  24:    */   {
/*  25: 94 */     Capabilities result = new Capabilities(this);
/*  26:    */     
/*  27: 96 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  28: 97 */     result.enableAllClasses();
/*  29: 98 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  30: 99 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  31:    */     
/*  32:101 */     return result;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void buildGenerator(Instances someinstances)
/*  36:    */     throws Exception
/*  37:    */   {
/*  38:113 */     getCapabilities().testWithFail(someinstances);
/*  39:    */     
/*  40:115 */     someinstances = new Instances(someinstances);
/*  41:116 */     someinstances.deleteWithMissing(0);
/*  42:    */     
/*  43:    */ 
/*  44:119 */     double[] values = new double[someinstances.numInstances()];
/*  45:121 */     for (int i = 0; i < someinstances.numInstances(); i++)
/*  46:    */     {
/*  47:122 */       Instance aninst = someinstances.instance(i);
/*  48:123 */       values[i] = aninst.value(0);
/*  49:    */     }
/*  50:126 */     Arrays.sort(values);
/*  51:    */     
/*  52:128 */     double count = 1.0D;
/*  53:129 */     for (int i = 1; i < values.length; i++) {
/*  54:130 */       if (values[i] != values[(i - 1)]) {
/*  55:131 */         count += 1.0D;
/*  56:    */       }
/*  57:    */     }
/*  58:135 */     double[][] allvals = new double[(int)count][2];
/*  59:136 */     int position = 0;
/*  60:137 */     allvals[0][0] = values[0];
/*  61:138 */     allvals[0][1] = 1.0D;
/*  62:140 */     for (int i = 1; i < values.length; i++) {
/*  63:141 */       if (values[i] != values[(i - 1)])
/*  64:    */       {
/*  65:142 */         position++;
/*  66:143 */         allvals[position][0] = values[i];
/*  67:144 */         allvals[position][1] = 1.0D;
/*  68:    */       }
/*  69:    */       else
/*  70:    */       {
/*  71:146 */         allvals[position][1] += 1.0D;
/*  72:    */       }
/*  73:    */     }
/*  74:150 */     for (int i = 0; i < count; i++) {
/*  75:151 */       allvals[i][1] /= (values.length + 1.0D);
/*  76:    */     }
/*  77:154 */     this.m_Probabilities = allvals;
/*  78:155 */     this.m_Unseen = (1.0D / (values.length + 1.0D));
/*  79:    */   }
/*  80:    */   
/*  81:    */   public double generate()
/*  82:    */   {
/*  83:164 */     double aprob = this.m_Random.nextDouble();
/*  84:165 */     double currentprob = 0.0D;
/*  85:166 */     for (int i = 0; i < this.m_Probabilities.length; i++)
/*  86:    */     {
/*  87:167 */       if (currentprob + this.m_Probabilities[i][1] >= aprob) {
/*  88:168 */         return this.m_Probabilities[i][0];
/*  89:    */       }
/*  90:170 */       currentprob += this.m_Probabilities[i][1];
/*  91:    */     }
/*  92:173 */     return 0.0D;
/*  93:    */   }
/*  94:    */   
/*  95:    */   public double getProbabilityOf(double valuex)
/*  96:    */   {
/*  97:185 */     for (int i = 0; i < this.m_Probabilities.length; i++) {
/*  98:186 */       if (valuex == this.m_Probabilities[i][0]) {
/*  99:187 */         return this.m_Probabilities[i][1];
/* 100:    */       }
/* 101:    */     }
/* 102:190 */     return this.m_Unseen;
/* 103:    */   }
/* 104:    */   
/* 105:    */   public double getLogProbabilityOf(double valuex)
/* 106:    */   {
/* 107:200 */     return Math.log(getProbabilityOf(valuex));
/* 108:    */   }
/* 109:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.generators.DiscreteGenerator
 * JD-Core Version:    0.7.0.1
 */