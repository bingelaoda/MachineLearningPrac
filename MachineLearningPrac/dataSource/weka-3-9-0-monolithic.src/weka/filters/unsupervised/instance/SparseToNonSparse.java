/*   1:    */ package weka.filters.unsupervised.instance;
/*   2:    */ 
/*   3:    */ import weka.core.Capabilities;
/*   4:    */ import weka.core.Capabilities.Capability;
/*   5:    */ import weka.core.DenseInstance;
/*   6:    */ import weka.core.Instance;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ import weka.core.SparseInstance;
/*  10:    */ import weka.filters.Filter;
/*  11:    */ import weka.filters.StreamableFilter;
/*  12:    */ import weka.filters.UnsupervisedFilter;
/*  13:    */ 
/*  14:    */ public class SparseToNonSparse
/*  15:    */   extends Filter
/*  16:    */   implements UnsupervisedFilter, StreamableFilter
/*  17:    */ {
/*  18:    */   static final long serialVersionUID = 2481634184210236074L;
/*  19:    */   
/*  20:    */   public String globalInfo()
/*  21:    */   {
/*  22: 59 */     return "An instance filter that converts all incoming sparse instances into non-sparse format.";
/*  23:    */   }
/*  24:    */   
/*  25:    */   public Capabilities getCapabilities()
/*  26:    */   {
/*  27: 70 */     Capabilities result = super.getCapabilities();
/*  28: 71 */     result.disableAll();
/*  29:    */     
/*  30:    */ 
/*  31: 74 */     result.enableAllAttributes();
/*  32: 75 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  33:    */     
/*  34:    */ 
/*  35: 78 */     result.enableAllClasses();
/*  36: 79 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  37: 80 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  38:    */     
/*  39: 82 */     return result;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public boolean setInputFormat(Instances instanceInfo)
/*  43:    */     throws Exception
/*  44:    */   {
/*  45: 96 */     super.setInputFormat(instanceInfo);
/*  46: 97 */     setOutputFormat(instanceInfo);
/*  47: 98 */     return true;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public boolean input(Instance instance)
/*  51:    */   {
/*  52:114 */     if (getInputFormat() == null) {
/*  53:115 */       throw new IllegalStateException("No input instance format defined");
/*  54:    */     }
/*  55:117 */     if (this.m_NewBatch)
/*  56:    */     {
/*  57:118 */       resetQueue();
/*  58:119 */       this.m_NewBatch = false;
/*  59:    */     }
/*  60:121 */     Instance inst = null;
/*  61:122 */     if ((instance instanceof SparseInstance))
/*  62:    */     {
/*  63:123 */       inst = new DenseInstance(instance.weight(), instance.toDoubleArray());
/*  64:124 */       inst.setDataset(instance.dataset());
/*  65:    */     }
/*  66:    */     else
/*  67:    */     {
/*  68:126 */       inst = (Instance)instance.copy();
/*  69:    */     }
/*  70:128 */     push(inst, false);
/*  71:129 */     return true;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public String getRevision()
/*  75:    */   {
/*  76:138 */     return RevisionUtils.extract("$Revision: 12037 $");
/*  77:    */   }
/*  78:    */   
/*  79:    */   public static void main(String[] argv)
/*  80:    */   {
/*  81:147 */     runFilter(new SparseToNonSparse(), argv);
/*  82:    */   }
/*  83:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.instance.SparseToNonSparse
 * JD-Core Version:    0.7.0.1
 */