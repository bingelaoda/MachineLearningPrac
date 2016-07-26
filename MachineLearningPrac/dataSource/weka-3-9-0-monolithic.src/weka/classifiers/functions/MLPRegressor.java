/*   1:    */ package weka.classifiers.functions;
/*   2:    */ 
/*   3:    */ import java.util.Random;
/*   4:    */ import weka.classifiers.functions.loss.LossFunction;
/*   5:    */ import weka.core.Capabilities;
/*   6:    */ import weka.core.Capabilities.Capability;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.WeightedInstancesHandler;
/*  10:    */ import weka.filters.Filter;
/*  11:    */ import weka.filters.unsupervised.attribute.Standardize;
/*  12:    */ 
/*  13:    */ public class MLPRegressor
/*  14:    */   extends MLPModel
/*  15:    */   implements WeightedInstancesHandler
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = -4477474276438394655L;
/*  18:    */   
/*  19:    */   public Capabilities getCapabilities()
/*  20:    */   {
/*  21:101 */     Capabilities result = super.getCapabilities();
/*  22:    */     
/*  23:    */ 
/*  24:104 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  25:105 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  26:    */     
/*  27:107 */     return result;
/*  28:    */   }
/*  29:    */   
/*  30:111 */   protected double m_x1 = 1.0D;
/*  31:112 */   protected double m_x0 = 0.0D;
/*  32:    */   
/*  33:    */   protected Instances initializeClassifier(Instances data, Random random)
/*  34:    */     throws Exception
/*  35:    */   {
/*  36:120 */     data = super.initializeClassifier(data, random);
/*  37:122 */     if (data != null)
/*  38:    */     {
/*  39:124 */       double y0 = data.instance(0).classValue();
/*  40:125 */       int index = 1;
/*  41:127 */       while ((index < data.numInstances()) && (data.instance(index).classValue() == y0)) {
/*  42:128 */         index++;
/*  43:    */       }
/*  44:130 */       if (index == data.numInstances()) {
/*  45:133 */         throw new Exception("All class values are the same. At least two class values should be different");
/*  46:    */       }
/*  47:136 */       double y1 = data.instance(index).classValue();
/*  48:    */       
/*  49:    */ 
/*  50:139 */       this.m_Filter = new Standardize();
/*  51:140 */       ((Standardize)this.m_Filter).setIgnoreClass(true);
/*  52:141 */       this.m_Filter.setInputFormat(data);
/*  53:142 */       data = Filter.useFilter(data, this.m_Filter);
/*  54:143 */       double z0 = data.instance(0).classValue();
/*  55:144 */       double z1 = data.instance(index).classValue();
/*  56:145 */       this.m_x1 = ((y0 - y1) / (z0 - z1));
/*  57:    */       
/*  58:147 */       this.m_x0 = (y0 - this.m_x1 * z0);
/*  59:    */     }
/*  60:149 */     return data;
/*  61:    */   }
/*  62:    */   
/*  63:    */   protected double calculateErrorForOneInstance(double[] outputs, Instance inst)
/*  64:    */   {
/*  65:162 */     return inst.weight() * this.m_Loss.loss(getOutput(0, outputs), inst.value(this.m_classIndex));
/*  66:    */   }
/*  67:    */   
/*  68:    */   protected double[] computeDeltas(Instance inst, double[] outputs)
/*  69:    */   {
/*  70:170 */     double[] deltas = new double[1];
/*  71:171 */     deltas[0] = (inst.weight() * this.m_Loss.derivative(getOutput(0, outputs), inst.value(this.m_classIndex)));
/*  72:172 */     return deltas;
/*  73:    */   }
/*  74:    */   
/*  75:    */   protected double[] postProcessDistribution(double[] dist)
/*  76:    */   {
/*  77:180 */     dist[0] = (dist[0] * this.m_x1 + this.m_x0);
/*  78:    */     
/*  79:182 */     return dist;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public String modelType()
/*  83:    */   {
/*  84:190 */     return "MPRegressor";
/*  85:    */   }
/*  86:    */   
/*  87:    */   public static void main(String[] argv)
/*  88:    */   {
/*  89:199 */     runClassifier(new MLPRegressor(), argv);
/*  90:    */   }
/*  91:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.MLPRegressor
 * JD-Core Version:    0.7.0.1
 */