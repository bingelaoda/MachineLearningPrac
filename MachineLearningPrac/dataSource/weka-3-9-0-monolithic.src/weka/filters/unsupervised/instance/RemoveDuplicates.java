/*   1:    */ package weka.filters.unsupervised.instance;
/*   2:    */ 
/*   3:    */ import java.util.HashSet;
/*   4:    */ import weka.classifiers.rules.DecisionTableHashKey;
/*   5:    */ import weka.core.Capabilities;
/*   6:    */ import weka.core.Capabilities.Capability;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.RevisionUtils;
/*  10:    */ import weka.filters.SimpleBatchFilter;
/*  11:    */ 
/*  12:    */ public class RemoveDuplicates
/*  13:    */   extends SimpleBatchFilter
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = 4518686110979589602L;
/*  16:    */   
/*  17:    */   public String globalInfo()
/*  18:    */   {
/*  19: 63 */     return "Removes all duplicate instances from the first batch of data it receives.";
/*  20:    */   }
/*  21:    */   
/*  22:    */   public boolean input(Instance instance)
/*  23:    */     throws Exception
/*  24:    */   {
/*  25: 81 */     if (getInputFormat() == null) {
/*  26: 82 */       throw new IllegalStateException("No input instance format defined");
/*  27:    */     }
/*  28: 84 */     if (this.m_NewBatch)
/*  29:    */     {
/*  30: 85 */       resetQueue();
/*  31: 86 */       this.m_NewBatch = false;
/*  32:    */     }
/*  33: 89 */     if (isFirstBatchDone())
/*  34:    */     {
/*  35: 90 */       push(instance);
/*  36: 91 */       return true;
/*  37:    */     }
/*  38: 93 */     bufferInput(instance);
/*  39: 94 */     return false;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public Capabilities getCapabilities()
/*  43:    */   {
/*  44:106 */     Capabilities result = super.getCapabilities();
/*  45:107 */     result.disableAll();
/*  46:    */     
/*  47:    */ 
/*  48:110 */     result.enable(Capabilities.Capability.STRING_ATTRIBUTES);
/*  49:111 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  50:112 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  51:113 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  52:114 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  53:    */     
/*  54:    */ 
/*  55:117 */     result.enable(Capabilities.Capability.STRING_CLASS);
/*  56:118 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  57:119 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  58:120 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  59:121 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  60:122 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  61:    */     
/*  62:124 */     return result;
/*  63:    */   }
/*  64:    */   
/*  65:    */   protected Instances determineOutputFormat(Instances inputFormat)
/*  66:    */     throws Exception
/*  67:    */   {
/*  68:139 */     return new Instances(inputFormat, 0);
/*  69:    */   }
/*  70:    */   
/*  71:    */   protected boolean hasImmediateOutputFormat()
/*  72:    */   {
/*  73:153 */     return true;
/*  74:    */   }
/*  75:    */   
/*  76:    */   protected Instances process(Instances instances)
/*  77:    */     throws Exception
/*  78:    */   {
/*  79:168 */     if (!isFirstBatchDone())
/*  80:    */     {
/*  81:169 */       HashSet<DecisionTableHashKey> hs = new HashSet();
/*  82:170 */       Instances newInstances = new Instances(instances, instances.numInstances());
/*  83:171 */       for (Instance inst : instances)
/*  84:    */       {
/*  85:172 */         DecisionTableHashKey key = new DecisionTableHashKey(inst, instances.numAttributes(), true);
/*  86:174 */         if (hs.add(key)) {
/*  87:175 */           newInstances.add(inst);
/*  88:    */         }
/*  89:    */       }
/*  90:178 */       newInstances.compactify();
/*  91:179 */       return newInstances;
/*  92:    */     }
/*  93:181 */     throw new Exception("The process method should never be called for subsequent batches.");
/*  94:    */   }
/*  95:    */   
/*  96:    */   public String getRevision()
/*  97:    */   {
/*  98:191 */     return RevisionUtils.extract("$Revision: 9804 $");
/*  99:    */   }
/* 100:    */   
/* 101:    */   public static void main(String[] args)
/* 102:    */   {
/* 103:200 */     runFilter(new RemoveDuplicates(), args);
/* 104:    */   }
/* 105:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.instance.RemoveDuplicates
 * JD-Core Version:    0.7.0.1
 */