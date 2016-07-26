/*   1:    */ package weka.filters;
/*   2:    */ 
/*   3:    */ import weka.core.Instances;
/*   4:    */ 
/*   5:    */ public abstract class SimpleFilter
/*   6:    */   extends Filter
/*   7:    */ {
/*   8:    */   private static final long serialVersionUID = 5702974949137433141L;
/*   9:    */   
/*  10:    */   public abstract String globalInfo();
/*  11:    */   
/*  12:    */   protected void reset()
/*  13:    */   {
/*  14: 62 */     this.m_NewBatch = true;
/*  15: 63 */     this.m_FirstBatchDone = false;
/*  16:    */   }
/*  17:    */   
/*  18:    */   protected abstract boolean hasImmediateOutputFormat();
/*  19:    */   
/*  20:    */   protected abstract Instances determineOutputFormat(Instances paramInstances)
/*  21:    */     throws Exception;
/*  22:    */   
/*  23:    */   protected abstract Instances process(Instances paramInstances)
/*  24:    */     throws Exception;
/*  25:    */   
/*  26:    */   public boolean setInputFormat(Instances instanceInfo)
/*  27:    */     throws Exception
/*  28:    */   {
/*  29:115 */     super.setInputFormat(instanceInfo);
/*  30:    */     
/*  31:117 */     reset();
/*  32:119 */     if (hasImmediateOutputFormat()) {
/*  33:120 */       setOutputFormat(determineOutputFormat(instanceInfo));
/*  34:    */     }
/*  35:123 */     return hasImmediateOutputFormat();
/*  36:    */   }
/*  37:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.SimpleFilter
 * JD-Core Version:    0.7.0.1
 */