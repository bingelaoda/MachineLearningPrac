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
/*  15:    */ public class TimeSeriesDelta
/*  16:    */   extends TimeSeriesTranslate
/*  17:    */ {
/*  18:    */   static final long serialVersionUID = 3101490081896634942L;
/*  19:    */   
/*  20:    */   public String globalInfo()
/*  21:    */   {
/*  22: 79 */     return "An instance filter that assumes instances form time-series data and replaces attribute values in the current instance with the difference between the current value and the equivalent attribute attribute value of some previous (or future) instance. For instances where the time-shifted value is unknown either the instance may be dropped, or missing values used. Skips the class attribute if it is set.";
/*  23:    */   }
/*  24:    */   
/*  25:    */   public Capabilities getCapabilities()
/*  26:    */   {
/*  27: 94 */     Capabilities result = super.getCapabilities();
/*  28: 95 */     result.disableAll();
/*  29:    */     
/*  30:    */ 
/*  31: 98 */     result.enableAllAttributes();
/*  32: 99 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  33:    */     
/*  34:    */ 
/*  35:102 */     result.enableAllClasses();
/*  36:103 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  37:104 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  38:    */     
/*  39:106 */     return result;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public boolean setInputFormat(Instances instanceInfo)
/*  43:    */     throws Exception
/*  44:    */   {
/*  45:121 */     if ((instanceInfo.classIndex() > 0) && (!getFillWithMissing())) {
/*  46:122 */       throw new IllegalArgumentException("TimeSeriesDelta: Need to fill in missing values using appropriate option when class index is set.");
/*  47:    */     }
/*  48:125 */     super.setInputFormat(instanceInfo);
/*  49:    */     
/*  50:127 */     Instances outputFormat = new Instances(instanceInfo, 0);
/*  51:128 */     for (int i = 0; i < instanceInfo.numAttributes(); i++) {
/*  52:129 */       if ((i != instanceInfo.classIndex()) && 
/*  53:130 */         (this.m_SelectedCols.isInRange(i))) {
/*  54:131 */         if (outputFormat.attribute(i).isNumeric()) {
/*  55:132 */           outputFormat.renameAttribute(i, outputFormat.attribute(i).name() + " d" + (this.m_InstanceRange < 0 ? '-' : '+') + Math.abs(this.m_InstanceRange));
/*  56:    */         } else {
/*  57:137 */           throw new UnsupportedAttributeTypeException("Time delta attributes must be numeric!");
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
/*  68:159 */     Instances outputFormat = outputFormatPeek();
/*  69:160 */     double[] vals = new double[outputFormat.numAttributes()];
/*  70:161 */     for (int i = 0; i < vals.length; i++) {
/*  71:162 */       if ((i != outputFormat.classIndex()) && (this.m_SelectedCols.isInRange(i)))
/*  72:    */       {
/*  73:163 */         if ((source != null) && (!source.isMissing(i)) && (!dest.isMissing(i))) {
/*  74:164 */           vals[i] = (dest.value(i) - source.value(i));
/*  75:    */         } else {
/*  76:166 */           vals[i] = Utils.missingValue();
/*  77:    */         }
/*  78:    */       }
/*  79:    */       else {
/*  80:169 */         vals[i] = dest.value(i);
/*  81:    */       }
/*  82:    */     }
/*  83:172 */     Instance inst = null;
/*  84:173 */     if ((dest instanceof SparseInstance)) {
/*  85:174 */       inst = new SparseInstance(dest.weight(), vals);
/*  86:    */     } else {
/*  87:176 */       inst = new DenseInstance(dest.weight(), vals);
/*  88:    */     }
/*  89:178 */     inst.setDataset(dest.dataset());
/*  90:179 */     return inst;
/*  91:    */   }
/*  92:    */   
/*  93:    */   public String getRevision()
/*  94:    */   {
/*  95:188 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  96:    */   }
/*  97:    */   
/*  98:    */   public static void main(String[] argv)
/*  99:    */   {
/* 100:197 */     runFilter(new TimeSeriesDelta(), argv);
/* 101:    */   }
/* 102:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.unsupervised.attribute.TimeSeriesDelta
 * JD-Core Version:    0.7.0.1
 */