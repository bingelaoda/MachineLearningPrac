/*   1:    */ package weka.classifiers.meta.generators;
/*   2:    */ 
/*   3:    */ import java.util.Random;
/*   4:    */ import weka.clusterers.EM;
/*   5:    */ import weka.core.Capabilities;
/*   6:    */ import weka.core.Capabilities.Capability;
/*   7:    */ import weka.core.DenseInstance;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ 
/*  11:    */ public class EMGenerator
/*  12:    */   extends RandomizableGenerator
/*  13:    */   implements InstanceHandler, NumericAttributeGenerator
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = 2769416817955024550L;
/*  16:    */   protected EM m_EMModel;
/*  17:    */   
/*  18:    */   public String globalInfo()
/*  19:    */   {
/*  20: 69 */     return "A generator that uses EM as an underlying model.";
/*  21:    */   }
/*  22:    */   
/*  23:    */   public Capabilities getCapabilities()
/*  24:    */   {
/*  25: 79 */     Capabilities result = new Capabilities(this);
/*  26:    */     
/*  27:    */ 
/*  28:    */ 
/*  29: 83 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  30: 84 */     result.enableAllClasses();
/*  31: 85 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  32: 86 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  33:    */     
/*  34: 88 */     return result;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public void buildGenerator(Instances someinstances)
/*  38:    */     throws Exception
/*  39:    */   {
/*  40:100 */     getCapabilities().testWithFail(someinstances);
/*  41:    */     
/*  42:102 */     someinstances = new Instances(someinstances);
/*  43:103 */     someinstances.deleteWithMissing(0);
/*  44:    */     
/*  45:105 */     this.m_EMModel = new EM();
/*  46:106 */     this.m_EMModel.setMaxIterations(10);
/*  47:107 */     this.m_EMModel.buildClusterer(someinstances);
/*  48:    */   }
/*  49:    */   
/*  50:    */   public double generate()
/*  51:    */   {
/*  52:118 */     double[] clusterProbabilities = this.m_EMModel.getClusterPriors();
/*  53:119 */     double clusterPicked = this.m_Random.nextDouble();
/*  54:    */     
/*  55:    */ 
/*  56:122 */     double sum = 0.0D;
/*  57:123 */     int clusterID = 0;
/*  58:124 */     for (int i = 0; i < clusterProbabilities.length; i++)
/*  59:    */     {
/*  60:125 */       if ((clusterPicked > sum) && (clusterPicked <= sum + clusterProbabilities[i]))
/*  61:    */       {
/*  62:128 */         clusterID = i;
/*  63:129 */         break;
/*  64:    */       }
/*  65:131 */       sum += clusterProbabilities[i];
/*  66:132 */       clusterID = i;
/*  67:    */     }
/*  68:137 */     double[][][] normalDists = this.m_EMModel.getClusterModelsNumericAtts();
/*  69:138 */     double mean = normalDists[clusterID][0][0];
/*  70:139 */     double sd = normalDists[clusterID][0][1];
/*  71:    */     
/*  72:141 */     double gaussian = this.m_Random.nextGaussian();
/*  73:142 */     double value = mean + gaussian * sd;
/*  74:    */     
/*  75:144 */     return value;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public double getProbabilityOf(double valuex)
/*  79:    */   {
/*  80:157 */     Instance inst = new DenseInstance(1);
/*  81:158 */     inst.setValue(0, valuex);
/*  82:    */     try
/*  83:    */     {
/*  84:160 */       return Math.exp(this.m_EMModel.logDensityForInstance(inst));
/*  85:    */     }
/*  86:    */     catch (Exception e)
/*  87:    */     {
/*  88:162 */       e.printStackTrace();
/*  89:163 */       System.exit(-1);
/*  90:    */     }
/*  91:166 */     return 0.0D;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public double getLogProbabilityOf(double valuex)
/*  95:    */   {
/*  96:176 */     return Math.log(getProbabilityOf(valuex));
/*  97:    */   }
/*  98:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.meta.generators.EMGenerator
 * JD-Core Version:    0.7.0.1
 */