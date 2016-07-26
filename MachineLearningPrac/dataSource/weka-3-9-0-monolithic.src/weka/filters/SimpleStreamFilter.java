/*   1:    */ package weka.filters;
/*   2:    */ 
/*   3:    */ import weka.core.Instance;
/*   4:    */ import weka.core.Instances;
/*   5:    */ 
/*   6:    */ public abstract class SimpleStreamFilter
/*   7:    */   extends SimpleFilter
/*   8:    */   implements StreamableFilter
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = 2754882676192747091L;
/*  11:    */   
/*  12:    */   protected boolean hasImmediateOutputFormat()
/*  13:    */   {
/*  14:169 */     return true;
/*  15:    */   }
/*  16:    */   
/*  17:    */   protected abstract Instances determineOutputFormat(Instances paramInstances)
/*  18:    */     throws Exception;
/*  19:    */   
/*  20:    */   protected abstract Instance process(Instance paramInstance)
/*  21:    */     throws Exception;
/*  22:    */   
/*  23:    */   protected Instances process(Instances instances)
/*  24:    */     throws Exception
/*  25:    */   {
/*  26:217 */     Instances result = new Instances(getOutputFormat(), 0);
/*  27:219 */     for (int i = 0; i < instances.numInstances(); i++) {
/*  28:220 */       result.add(process(instances.instance(i)));
/*  29:    */     }
/*  30:223 */     return result;
/*  31:    */   }
/*  32:    */   
/*  33:    */   protected void preprocess(Instances instances) {}
/*  34:    */   
/*  35:    */   public boolean input(Instance instance)
/*  36:    */     throws Exception
/*  37:    */   {
/*  38:249 */     if (getInputFormat() == null) {
/*  39:250 */       throw new IllegalStateException("No input instance format defined");
/*  40:    */     }
/*  41:253 */     if (this.m_NewBatch)
/*  42:    */     {
/*  43:254 */       resetQueue();
/*  44:255 */       this.m_NewBatch = false;
/*  45:    */     }
/*  46:    */     try
/*  47:    */     {
/*  48:259 */       if ((hasImmediateOutputFormat()) || (isFirstBatchDone()))
/*  49:    */       {
/*  50:260 */         Instance processed = process((Instance)instance.copy());
/*  51:261 */         if (processed != null)
/*  52:    */         {
/*  53:262 */           push(processed, false);
/*  54:263 */           return true;
/*  55:    */         }
/*  56:265 */         return false;
/*  57:    */       }
/*  58:267 */       bufferInput(instance);
/*  59:268 */       return false;
/*  60:    */     }
/*  61:    */     catch (Exception e) {}
/*  62:271 */     return false;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public boolean batchFinished()
/*  66:    */     throws Exception
/*  67:    */   {
/*  68:290 */     if (getInputFormat() == null) {
/*  69:291 */       throw new IllegalStateException("No input instance format defined");
/*  70:    */     }
/*  71:294 */     Instances inst = new Instances(getInputFormat());
/*  72:295 */     flushInput();
/*  73:297 */     if (!hasImmediateOutputFormat()) {
/*  74:298 */       preprocess(inst);
/*  75:    */     }
/*  76:302 */     inst = process(inst);
/*  77:305 */     if ((!hasImmediateOutputFormat()) && (!isFirstBatchDone())) {
/*  78:306 */       setOutputFormat(inst);
/*  79:    */     }
/*  80:310 */     for (int i = 0; i < inst.numInstances(); i++) {
/*  81:311 */       push(inst.instance(i), false);
/*  82:    */     }
/*  83:314 */     this.m_NewBatch = true;
/*  84:315 */     this.m_FirstBatchDone = true;
/*  85:    */     
/*  86:317 */     return numPendingOutput() != 0;
/*  87:    */   }
/*  88:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.SimpleStreamFilter
 * JD-Core Version:    0.7.0.1
 */