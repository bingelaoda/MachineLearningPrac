/*   1:    */ package weka.filters.supervised.instance;
/*   2:    */ 
/*   3:    */ import weka.core.Capabilities;
/*   4:    */ import weka.core.Capabilities.Capability;
/*   5:    */ import weka.core.Instance;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.RevisionUtils;
/*   8:    */ import weka.core.Utils;
/*   9:    */ import weka.core.WeightedInstancesHandler;
/*  10:    */ import weka.filters.SimpleBatchFilter;
/*  11:    */ import weka.filters.SupervisedFilter;
/*  12:    */ 
/*  13:    */ public class ClassBalancer
/*  14:    */   extends SimpleBatchFilter
/*  15:    */   implements SupervisedFilter, WeightedInstancesHandler
/*  16:    */ {
/*  17:    */   static final long serialVersionUID = 6237337831221353842L;
/*  18:    */   
/*  19:    */   public String globalInfo()
/*  20:    */   {
/*  21: 80 */     return "Reweights the instances in the data so that each class has the same total weight. The total sum of weights accross all instances will be maintained. Only the weights in the first batch of data received by this filter are changed, so it can be used with the FilteredClassifier.";
/*  22:    */   }
/*  23:    */   
/*  24:    */   protected Instances determineOutputFormat(Instances inputFormat)
/*  25:    */   {
/*  26: 94 */     return new Instances(inputFormat, 0);
/*  27:    */   }
/*  28:    */   
/*  29:    */   public Capabilities getCapabilities()
/*  30:    */   {
/*  31:107 */     Capabilities result = super.getCapabilities();
/*  32:108 */     result.disableAll();
/*  33:    */     
/*  34:    */ 
/*  35:111 */     result.enableAllAttributes();
/*  36:112 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  37:    */     
/*  38:    */ 
/*  39:115 */     result.enableAllClasses();
/*  40:116 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  41:    */     
/*  42:118 */     return result;
/*  43:    */   }
/*  44:    */   
/*  45:    */   protected Instances process(Instances instances)
/*  46:    */     throws Exception
/*  47:    */   {
/*  48:132 */     if (isFirstBatchDone()) {
/*  49:133 */       return new Instances(instances);
/*  50:    */     }
/*  51:137 */     Instances result = new Instances(instances, instances.numInstances());
/*  52:138 */     double[] sumOfWeightsPerClass = new double[instances.numClasses()];
/*  53:139 */     for (int i = 0; i < instances.numInstances(); i++)
/*  54:    */     {
/*  55:140 */       Instance inst = instances.instance(i);
/*  56:141 */       sumOfWeightsPerClass[((int)inst.classValue())] += inst.weight();
/*  57:    */     }
/*  58:143 */     double sumOfWeights = Utils.sum(sumOfWeightsPerClass);
/*  59:    */     
/*  60:    */ 
/*  61:146 */     double factor = sumOfWeights / instances.numClasses();
/*  62:147 */     for (int i = 0; i < instances.numInstances(); i++)
/*  63:    */     {
/*  64:148 */       result.add(instances.instance(i));
/*  65:149 */       Instance newInst = result.instance(i);
/*  66:150 */       copyValues(newInst, false, instances, outputFormatPeek());
/*  67:151 */       newInst.setWeight(factor * newInst.weight() / sumOfWeightsPerClass[((int)newInst.classValue())]);
/*  68:    */     }
/*  69:154 */     return result;
/*  70:    */   }
/*  71:    */   
/*  72:    */   public String getRevision()
/*  73:    */   {
/*  74:164 */     return RevisionUtils.extract("$Revision: 10215 $");
/*  75:    */   }
/*  76:    */   
/*  77:    */   public static void main(String[] args)
/*  78:    */   {
/*  79:173 */     runFilter(new ClassBalancer(), args);
/*  80:    */   }
/*  81:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.supervised.instance.ClassBalancer
 * JD-Core Version:    0.7.0.1
 */