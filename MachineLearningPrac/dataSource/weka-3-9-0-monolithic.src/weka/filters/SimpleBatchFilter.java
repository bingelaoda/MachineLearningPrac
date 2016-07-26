/*   1:    */ package weka.filters;
/*   2:    */ 
/*   3:    */ import weka.core.Instance;
/*   4:    */ import weka.core.Instances;
/*   5:    */ 
/*   6:    */ public abstract class SimpleBatchFilter
/*   7:    */   extends SimpleFilter
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = 8102908673378055114L;
/*  10:    */   
/*  11:    */   protected boolean hasImmediateOutputFormat()
/*  12:    */   {
/*  13:167 */     return false;
/*  14:    */   }
/*  15:    */   
/*  16:    */   public boolean allowAccessToFullInputFormat()
/*  17:    */   {
/*  18:179 */     return false;
/*  19:    */   }
/*  20:    */   
/*  21:    */   public boolean input(Instance instance)
/*  22:    */     throws Exception
/*  23:    */   {
/*  24:196 */     if (getInputFormat() == null) {
/*  25:197 */       throw new IllegalStateException("No input instance format defined");
/*  26:    */     }
/*  27:200 */     if (this.m_NewBatch)
/*  28:    */     {
/*  29:201 */       resetQueue();
/*  30:202 */       this.m_NewBatch = false;
/*  31:    */     }
/*  32:205 */     bufferInput(instance);
/*  33:207 */     if (isFirstBatchDone())
/*  34:    */     {
/*  35:208 */       Instances inst = new Instances(getInputFormat());
/*  36:209 */       inst = process(inst);
/*  37:210 */       for (int i = 0; i < inst.numInstances(); i++) {
/*  38:211 */         push(inst.instance(i), false);
/*  39:    */       }
/*  40:213 */       flushInput();
/*  41:    */     }
/*  42:216 */     return this.m_FirstBatchDone;
/*  43:    */   }
/*  44:    */   
/*  45:    */   public boolean batchFinished()
/*  46:    */     throws Exception
/*  47:    */   {
/*  48:238 */     if (getInputFormat() == null) {
/*  49:239 */       throw new IllegalStateException("No input instance format defined");
/*  50:    */     }
/*  51:243 */     Instances inst = new Instances(getInputFormat());
/*  52:246 */     if ((!hasImmediateOutputFormat()) && (!isFirstBatchDone())) {
/*  53:247 */       if (allowAccessToFullInputFormat()) {
/*  54:248 */         setOutputFormat(determineOutputFormat(inst));
/*  55:    */       } else {
/*  56:250 */         setOutputFormat(determineOutputFormat(new Instances(inst, 0)));
/*  57:    */       }
/*  58:    */     }
/*  59:257 */     if (inst.numInstances() > 0)
/*  60:    */     {
/*  61:259 */       inst = process(inst);
/*  62:    */       
/*  63:    */ 
/*  64:262 */       flushInput();
/*  65:265 */       for (int i = 0; i < inst.numInstances(); i++) {
/*  66:266 */         push(inst.instance(i), false);
/*  67:    */       }
/*  68:    */     }
/*  69:270 */     this.m_NewBatch = true;
/*  70:271 */     this.m_FirstBatchDone = true;
/*  71:    */     
/*  72:273 */     return numPendingOutput() != 0;
/*  73:    */   }
/*  74:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.SimpleBatchFilter
 * JD-Core Version:    0.7.0.1
 */