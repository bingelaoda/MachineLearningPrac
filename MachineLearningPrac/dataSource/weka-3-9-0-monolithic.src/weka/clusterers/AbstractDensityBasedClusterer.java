/*   1:    */ package weka.clusterers;
/*   2:    */ 
/*   3:    */ import weka.core.Instance;
/*   4:    */ import weka.core.SerializedObject;
/*   5:    */ import weka.core.Utils;
/*   6:    */ 
/*   7:    */ public abstract class AbstractDensityBasedClusterer
/*   8:    */   extends AbstractClusterer
/*   9:    */   implements DensityBasedClusterer
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = -5950728041704213845L;
/*  12:    */   
/*  13:    */   public abstract double[] clusterPriors()
/*  14:    */     throws Exception;
/*  15:    */   
/*  16:    */   public abstract double[] logDensityPerClusterForInstance(Instance paramInstance)
/*  17:    */     throws Exception;
/*  18:    */   
/*  19:    */   public double logDensityForInstance(Instance instance)
/*  20:    */     throws Exception
/*  21:    */   {
/*  22: 77 */     double[] a = logJointDensitiesForInstance(instance);
/*  23: 78 */     double max = a[Utils.maxIndex(a)];
/*  24: 79 */     double sum = 0.0D;
/*  25: 81 */     for (int i = 0; i < a.length; i++) {
/*  26: 82 */       sum += Math.exp(a[i] - max);
/*  27:    */     }
/*  28: 85 */     return max + Math.log(sum);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public double[] distributionForInstance(Instance instance)
/*  32:    */     throws Exception
/*  33:    */   {
/*  34: 97 */     return Utils.logs2probs(logJointDensitiesForInstance(instance));
/*  35:    */   }
/*  36:    */   
/*  37:    */   public double[] logJointDensitiesForInstance(Instance inst)
/*  38:    */     throws Exception
/*  39:    */   {
/*  40:110 */     double[] weights = logDensityPerClusterForInstance(inst);
/*  41:111 */     double[] priors = clusterPriors();
/*  42:113 */     for (int i = 0; i < weights.length; i++) {
/*  43:114 */       if (priors[i] > 0.0D) {
/*  44:115 */         weights[i] += Math.log(priors[i]);
/*  45:    */       } else {
/*  46:117 */         throw new IllegalArgumentException("Cluster empty!");
/*  47:    */       }
/*  48:    */     }
/*  49:120 */     return weights;
/*  50:    */   }
/*  51:    */   
/*  52:    */   public static DensityBasedClusterer[] makeCopies(DensityBasedClusterer model, int num)
/*  53:    */     throws Exception
/*  54:    */   {
/*  55:136 */     if (model == null) {
/*  56:137 */       throw new Exception("No model clusterer set");
/*  57:    */     }
/*  58:139 */     DensityBasedClusterer[] clusterers = new DensityBasedClusterer[num];
/*  59:140 */     SerializedObject so = new SerializedObject(model);
/*  60:141 */     for (int i = 0; i < clusterers.length; i++) {
/*  61:142 */       clusterers[i] = ((DensityBasedClusterer)so.getObject());
/*  62:    */     }
/*  63:144 */     return clusterers;
/*  64:    */   }
/*  65:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.clusterers.AbstractDensityBasedClusterer
 * JD-Core Version:    0.7.0.1
 */