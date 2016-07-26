/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ public class StringLocator
/*   4:    */   extends AttributeLocator
/*   5:    */ {
/*   6:    */   private static final long serialVersionUID = 7805522230268783972L;
/*   7:    */   
/*   8:    */   public StringLocator(Instances data)
/*   9:    */   {
/*  10: 44 */     super(data, 2);
/*  11:    */   }
/*  12:    */   
/*  13:    */   public StringLocator(Instances data, int fromIndex, int toIndex)
/*  14:    */   {
/*  15: 56 */     super(data, 2, fromIndex, toIndex);
/*  16:    */   }
/*  17:    */   
/*  18:    */   public StringLocator(Instances data, int[] indices)
/*  19:    */   {
/*  20: 67 */     super(data, 2, indices);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public static void copyStringValues(Instance inst, Instances destDataset, AttributeLocator strAtts)
/*  24:    */   {
/*  25: 83 */     if (inst.dataset() == null) {
/*  26: 84 */       throw new IllegalArgumentException("Instance has no dataset assigned!!");
/*  27:    */     }
/*  28: 85 */     if (inst.dataset().numAttributes() != destDataset.numAttributes()) {
/*  29: 86 */       throw new IllegalArgumentException("Src and Dest differ in # of attributes: " + inst.dataset().numAttributes() + " != " + destDataset.numAttributes());
/*  30:    */     }
/*  31: 91 */     copyStringValues(inst, true, inst.dataset(), strAtts, destDataset, strAtts);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public static void copyStringValues(Instance instance, boolean instSrcCompat, Instances srcDataset, AttributeLocator srcLoc, Instances destDataset, AttributeLocator destLoc)
/*  35:    */   {
/*  36:123 */     if (srcDataset == destDataset) {
/*  37:124 */       return;
/*  38:    */     }
/*  39:127 */     if (srcLoc.getAttributeIndices().length != destLoc.getAttributeIndices().length) {
/*  40:128 */       throw new IllegalArgumentException("Src and Dest string indices differ in length: " + srcLoc.getAttributeIndices().length + " != " + destLoc.getAttributeIndices().length);
/*  41:    */     }
/*  42:134 */     if (srcLoc.getLocatorIndices().length != destLoc.getLocatorIndices().length) {
/*  43:135 */       throw new IllegalArgumentException("Src and Dest locator indices differ in length: " + srcLoc.getLocatorIndices().length + " != " + destLoc.getLocatorIndices().length);
/*  44:    */     }
/*  45:141 */     for (int i = 0; i < srcLoc.getAttributeIndices().length; i++)
/*  46:    */     {
/*  47:142 */       int instIndex = instSrcCompat ? srcLoc.getActualIndex(srcLoc.getAttributeIndices()[i]) : destLoc.getActualIndex(destLoc.getAttributeIndices()[i]);
/*  48:    */       
/*  49:    */ 
/*  50:145 */       Attribute src = srcDataset.attribute(srcLoc.getActualIndex(srcLoc.getAttributeIndices()[i]));
/*  51:    */       
/*  52:147 */       Attribute dest = destDataset.attribute(destLoc.getActualIndex(destLoc.getAttributeIndices()[i]));
/*  53:149 */       if (!instance.isMissing(instIndex))
/*  54:    */       {
/*  55:150 */         int valIndex = dest.addStringValue(src, (int)instance.value(instIndex));
/*  56:151 */         instance.setValue(instIndex, valIndex);
/*  57:    */       }
/*  58:    */     }
/*  59:156 */     int[] srcIndices = srcLoc.getLocatorIndices();
/*  60:157 */     int[] destIndices = destLoc.getLocatorIndices();
/*  61:158 */     for (int i = 0; i < srcIndices.length; i++)
/*  62:    */     {
/*  63:159 */       int index = instSrcCompat ? srcLoc.getActualIndex(srcIndices[i]) : destLoc.getActualIndex(destIndices[i]);
/*  64:161 */       if (!instance.isMissing(index))
/*  65:    */       {
/*  66:164 */         int valueIndex = (int)instance.value(index);
/*  67:165 */         Instances rel = instSrcCompat ? srcDataset.attribute(index).relation(valueIndex) : destDataset.attribute(index).relation(valueIndex);
/*  68:    */         
/*  69:167 */         AttributeLocator srcStrAttsNew = srcLoc.getLocator(srcIndices[i]);
/*  70:168 */         Instances srcDatasetNew = srcStrAttsNew.getData();
/*  71:169 */         AttributeLocator destStrAttsNew = destLoc.getLocator(destIndices[i]);
/*  72:170 */         Instances destDatasetNew = destStrAttsNew.getData();
/*  73:171 */         for (int n = 0; n < rel.numInstances(); n++) {
/*  74:172 */           copyStringValues(rel.instance(n), instSrcCompat, srcDatasetNew, srcStrAttsNew, destDatasetNew, destStrAttsNew);
/*  75:    */         }
/*  76:    */       }
/*  77:    */     }
/*  78:    */   }
/*  79:    */   
/*  80:    */   public String getRevision()
/*  81:    */   {
/*  82:185 */     return RevisionUtils.extract("$Revision: 12038 $");
/*  83:    */   }
/*  84:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.StringLocator
 * JD-Core Version:    0.7.0.1
 */