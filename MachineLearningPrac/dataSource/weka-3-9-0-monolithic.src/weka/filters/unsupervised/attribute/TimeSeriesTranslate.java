/*   1:    */ package weka.filters.unsupervised.attribute;
/*   2:    */ 
/*   3:    */ import weka.core.Attribute;
/*   4:    */ import weka.core.Capabilities;
/*   5:    */ import weka.core.Capabilities.Capability;
/*   6:    */ import weka.core.DenseInstance;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.Range;
/*  10:    */ import weka.core.RevisionUtils;
/*  11:    */ import weka.core.SparseInstance;
/*  12:    */ import weka.core.UnsupportedAttributeTypeException;
/*  13:    */ import weka.core.Utils;
/*  14:    */ 
/*  15:    */ public class TimeSeriesTranslate
/*  16:    */   extends AbstractTimeSeries
/*  17:    */ {
/*  18:    */   static final long serialVersionUID = -8901621509691785705L;
/*  19:    */   
/*  20:    */   public String globalInfo()
/*  21:    */   {
/*  22: 78 */     return "An instance filter that assumes instances form time-series data and replaces attribute values in the current instance with the equivalent attribute values of some previous (or future) instance. For instances where the desired value is unknown either the instance may be dropped, or missing values used. Skips the class attribute if it is set.";
/*  23:    */   }
/*  24:    */   
/*  25:    */   public Capabilities getCapabilities()
/*  26:    */   {
/*  27: 93 */     Capabilities result = super.getCapabilities();
/*  28: 94 */     result.disableAll();
/*  29:    */     
/*  30:    */ 
/*  31: 97 */     result.enableAllAttributes();
/*  32: 98 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  33:    */     
/*  34:    */ 
/*  35:101 */     result.enableAllClasses();
/*  36:102 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  37:103 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  38:    */     
/*  39:105 */     return result;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public boolean setInputFormat(Instances instanceInfo)
/*  43:    */     throws Exception
/*  44:    */   {
/*  45:120 */     if ((instanceInfo.classIndex() > 0) && (!getFillWithMissing())) {
/*  46:121 */       throw new IllegalArgumentException("TimeSeriesTranslate: Need to fill in missing values using appropriate option when class index is set.");
/*  47:    */     }
/*  48:124 */     super.setInputFormat(instanceInfo);
/*  49:    */     
/*  50:126 */     Instances outputFormat = new Instances(instanceInfo, 0);
/*  51:127 */     for (int i = 0; i < instanceInfo.numAttributes(); i++) {
/*  52:128 */       if ((i != instanceInfo.classIndex()) && 
/*  53:129 */         (this.m_SelectedCols.isInRange(i))) {
/*  54:130 */         if ((outputFormat.attribute(i).isNominal()) || (outputFormat.attribute(i).isNumeric())) {
/*  55:132 */           outputFormat.renameAttribute(i, outputFormat.attribute(i).name() + (this.m_InstanceRange < 0 ? '-' : '+') + Math.abs(this.m_InstanceRange));
/*  56:    */         } else {
/*  57:136 */           throw new UnsupportedAttributeTypeException("Only numeric and nominal attributes may be  manipulated in time series.");
/*  58:    */         }
/*  59:    */       }
/*  60:    */     }
/*  61:142 */     outputFormat.setClassIndex(instanceInfo.classIndex());
/*  62:143 */     setOutputFormat(outputFormat);
/*  63:144 */     return true;
/*  64:    */   }
/*  65:    */   
/*  66:    */   protected Instance mergeInstances(Instance source, Instance dest)
/*  67:    */   {
/*  68:158 */     Instances outputFormat = outputFormatPeek();
/*  69:159 */     double[] vals = new double[outputFormat.numAttributes()];
/*  70:160 */     for (int i = 0; i < vals.length; i++) {
/*  71:161 */       if ((i != outputFormat.classIndex()) && (this.m_SelectedCols.isInRange(i)))
/*  72:    */       {
/*  73:162 */         if (source != null) {
/*  74:163 */           vals[i] = source.value(i);
/*  75:    */         } else {
/*  76:165 */           vals[i] = Utils.missingValue();
/*  77:    */         }
/*  78:    */       }
/*  79:    */       else {
/*  80:168 */         vals[i] = dest.value(i);
/*  81:    */       }
/*  82:    */     }
/*  83:171 */     Instance inst = null;
/*  84:172 */     if ((dest instanceof SparseInstance)) {
/*  85:173 */       inst = new SparseInstance(dest.weight(), vals);
/*  86:    */     } else {
/*  87:175 */       inst = new DenseInstance(dest.weight(), vals);
/*  88:    */     }
/*  89:177 */     inst.setDataset(dest.dataset());
/*  90:178 */     return inst;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public String getRevision()
/*  94:    */   {
/*  95:187 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  96:    */   }
/*  97:    */   
/*  98:    */   public static void main(String[] argv)
/*  99:    */   {
/* 100:196 */     runFilter(new TimeSeriesTranslate(), argv);
/* 101:    */   }
/* 102:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.TimeSeriesTranslate
 * JD-Core Version:    0.7.0.1
 */