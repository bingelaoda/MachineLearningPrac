/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ public class RelationalLocator
/*   4:    */   extends AttributeLocator
/*   5:    */ {
/*   6:    */   private static final long serialVersionUID = 4646872277151854732L;
/*   7:    */   
/*   8:    */   public RelationalLocator(Instances data)
/*   9:    */   {
/*  10: 42 */     super(data, 4);
/*  11:    */   }
/*  12:    */   
/*  13:    */   public RelationalLocator(Instances data, int fromIndex, int toIndex)
/*  14:    */   {
/*  15: 54 */     super(data, 4, fromIndex, toIndex);
/*  16:    */   }
/*  17:    */   
/*  18:    */   public RelationalLocator(Instances data, int[] indices)
/*  19:    */   {
/*  20: 65 */     super(data, 4, indices);
/*  21:    */   }
/*  22:    */   
/*  23:    */   public static void copyRelationalValues(Instance inst, Instances destDataset, AttributeLocator strAtts)
/*  24:    */   {
/*  25: 81 */     if (inst.dataset() == null) {
/*  26: 82 */       throw new IllegalArgumentException("Instance has no dataset assigned!!");
/*  27:    */     }
/*  28: 83 */     if (inst.dataset().numAttributes() != destDataset.numAttributes()) {
/*  29: 84 */       throw new IllegalArgumentException("Src and Dest differ in # of attributes: " + inst.dataset().numAttributes() + " != " + destDataset.numAttributes());
/*  30:    */     }
/*  31: 89 */     copyRelationalValues(inst, true, inst.dataset(), strAtts, destDataset, strAtts);
/*  32:    */   }
/*  33:    */   
/*  34:    */   public static void copyRelationalValues(Instance instance, boolean instSrcCompat, Instances srcDataset, AttributeLocator srcLoc, Instances destDataset, AttributeLocator destLoc)
/*  35:    */   {
/*  36:123 */     if (srcDataset == destDataset) {
/*  37:124 */       return;
/*  38:    */     }
/*  39:127 */     if (srcLoc.getAttributeIndices().length != destLoc.getAttributeIndices().length) {
/*  40:128 */       throw new IllegalArgumentException("Src and Dest relational indices differ in length: " + srcLoc.getAttributeIndices().length + " != " + destLoc.getAttributeIndices().length);
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
/*  55:150 */         int valIndex = dest.addRelation(src.relation((int)instance.value(instIndex)));
/*  56:    */         
/*  57:152 */         instance.setValue(instIndex, valIndex);
/*  58:    */       }
/*  59:    */     }
/*  60:157 */     int[] srcIndices = srcLoc.getLocatorIndices();
/*  61:158 */     int[] destIndices = destLoc.getLocatorIndices();
/*  62:159 */     for (int i = 0; i < srcIndices.length; i++)
/*  63:    */     {
/*  64:160 */       int index = instSrcCompat ? srcLoc.getActualIndex(srcIndices[i]) : destLoc.getActualIndex(destIndices[i]);
/*  65:162 */       if (!instance.isMissing(index))
/*  66:    */       {
/*  67:165 */         int valueIndex = (int)instance.value(index);
/*  68:166 */         Instances rel = instSrcCompat ? srcDataset.attribute(index).relation(valueIndex) : destDataset.attribute(index).relation(valueIndex);
/*  69:    */         
/*  70:168 */         AttributeLocator srcRelAttsNew = srcLoc.getLocator(srcIndices[i]);
/*  71:169 */         Instances srcDatasetNew = srcRelAttsNew.getData();
/*  72:170 */         AttributeLocator destRelAttsNew = destLoc.getLocator(destIndices[i]);
/*  73:171 */         Instances destDatasetNew = destRelAttsNew.getData();
/*  74:172 */         for (int n = 0; n < rel.numInstances(); n++) {
/*  75:173 */           copyRelationalValues(rel.instance(n), instSrcCompat, srcDatasetNew, srcRelAttsNew, destDatasetNew, destRelAttsNew);
/*  76:    */         }
/*  77:    */       }
/*  78:    */     }
/*  79:    */   }
/*  80:    */   
/*  81:    */   public String getRevision()
/*  82:    */   {
/*  83:186 */     return RevisionUtils.extract("$Revision: 12038 $");
/*  84:    */   }
/*  85:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.RelationalLocator
 * JD-Core Version:    0.7.0.1
 */