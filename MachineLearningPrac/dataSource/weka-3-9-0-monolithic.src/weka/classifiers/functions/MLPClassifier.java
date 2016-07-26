/*   1:    */ package weka.classifiers.functions;
/*   2:    */ 
/*   3:    */ import java.util.Arrays;
/*   4:    */ import java.util.Random;
/*   5:    */ import weka.classifiers.functions.activation.ActivationFunction;
/*   6:    */ import weka.classifiers.functions.activation.ApproximateSigmoid;
/*   7:    */ import weka.classifiers.functions.activation.Sigmoid;
/*   8:    */ import weka.classifiers.functions.loss.LossFunction;
/*   9:    */ import weka.core.Capabilities;
/*  10:    */ import weka.core.Capabilities.Capability;
/*  11:    */ import weka.core.Instance;
/*  12:    */ import weka.core.Instances;
/*  13:    */ import weka.core.Utils;
/*  14:    */ import weka.core.WeightedInstancesHandler;
/*  15:    */ import weka.filters.Filter;
/*  16:    */ import weka.filters.unsupervised.attribute.Standardize;
/*  17:    */ 
/*  18:    */ public class MLPClassifier
/*  19:    */   extends MLPModel
/*  20:    */   implements WeightedInstancesHandler
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = -3297474276438394644L;
/*  23:118 */   protected ActivationFunction m_OutputActivationFunction = null;
/*  24:    */   
/*  25:    */   public Capabilities getCapabilities()
/*  26:    */   {
/*  27:127 */     Capabilities result = super.getCapabilities();
/*  28:    */     
/*  29:    */ 
/*  30:130 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  31:131 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  32:    */     
/*  33:133 */     return result;
/*  34:    */   }
/*  35:    */   
/*  36:    */   protected Instances initializeClassifier(Instances data, Random random)
/*  37:    */     throws Exception
/*  38:    */   {
/*  39:142 */     data = super.initializeClassifier(data, random);
/*  40:144 */     if ((this.m_ActivationFunction instanceof ApproximateSigmoid)) {
/*  41:145 */       this.m_OutputActivationFunction = new ApproximateSigmoid();
/*  42:    */     } else {
/*  43:147 */       this.m_OutputActivationFunction = new Sigmoid();
/*  44:    */     }
/*  45:150 */     if (data != null)
/*  46:    */     {
/*  47:152 */       this.m_Filter = new Standardize();
/*  48:153 */       this.m_Filter.setInputFormat(data);
/*  49:154 */       data = Filter.useFilter(data, this.m_Filter);
/*  50:    */     }
/*  51:156 */     return data;
/*  52:    */   }
/*  53:    */   
/*  54:    */   protected double calculateErrorForOneInstance(double[] outputs, Instance inst)
/*  55:    */   {
/*  56:170 */     double sum = 0.0D;
/*  57:171 */     for (int i = 0; i < this.m_numClasses; i++) {
/*  58:172 */       sum += this.m_Loss.loss(this.m_OutputActivationFunction.activation(getOutput(i, outputs), null, 0), (int)inst.value(this.m_classIndex) == i ? 0.99D : 0.01D);
/*  59:    */     }
/*  60:175 */     return inst.weight() * sum;
/*  61:    */   }
/*  62:    */   
/*  63:    */   protected double[] computeDeltas(Instance inst, double[] outputs)
/*  64:    */   {
/*  65:184 */     double[] activationDerivativeOutput = new double[1];
/*  66:    */     
/*  67:    */ 
/*  68:187 */     double[] deltas = new double[inst.numClasses()];
/*  69:188 */     Arrays.fill(deltas, inst.weight());
/*  70:191 */     for (int i = 0; i < deltas.length; i++) {
/*  71:192 */       deltas[i] *= this.m_Loss.derivative(this.m_OutputActivationFunction.activation(getOutput(i, outputs), activationDerivativeOutput, 0), (int)inst.value(this.m_classIndex) == i ? 0.99D : 0.01D) * activationDerivativeOutput[0];
/*  72:    */     }
/*  73:196 */     return deltas;
/*  74:    */   }
/*  75:    */   
/*  76:    */   protected double[] postProcessDistribution(double[] dist)
/*  77:    */   {
/*  78:204 */     for (int i = 0; i < this.m_numClasses; i++)
/*  79:    */     {
/*  80:205 */       dist[i] = this.m_ActivationFunction.activation(dist[i], null, 0);
/*  81:206 */       if (dist[i] < 0.0D) {
/*  82:207 */         dist[i] = 0.0D;
/*  83:208 */       } else if (dist[i] > 1.0D) {
/*  84:209 */         dist[i] = 1.0D;
/*  85:    */       }
/*  86:    */     }
/*  87:212 */     double sum = 0.0D;
/*  88:213 */     for (double d : dist) {
/*  89:214 */       sum += d;
/*  90:    */     }
/*  91:216 */     if (sum > 0.0D)
/*  92:    */     {
/*  93:217 */       Utils.normalize(dist, sum);
/*  94:218 */       return dist;
/*  95:    */     }
/*  96:220 */     return null;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public String modelType()
/* 100:    */   {
/* 101:229 */     return "MLPClassifier";
/* 102:    */   }
/* 103:    */   
/* 104:    */   public static void main(String[] argv)
/* 105:    */   {
/* 106:238 */     runClassifier(new MLPClassifier(), argv);
/* 107:    */   }
/* 108:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.MLPClassifier
 * JD-Core Version:    0.7.0.1
 */