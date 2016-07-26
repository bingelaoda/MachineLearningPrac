/*   1:    */ package weka.classifiers.trees.j48;
/*   2:    */ 
/*   3:    */ import weka.core.ContingencyTables;
/*   4:    */ import weka.core.RevisionUtils;
/*   5:    */ import weka.core.Utils;
/*   6:    */ 
/*   7:    */ public final class GainRatioSplitCrit
/*   8:    */   extends EntropyBasedSplitCrit
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -433336694718670930L;
/*  11:    */   
/*  12:    */   public final double splitCritValue(Distribution bags)
/*  13:    */   {
/*  14: 49 */     double numerator = oldEnt(bags) - newEnt(bags);
/*  15: 52 */     if (Utils.eq(numerator, 0.0D)) {
/*  16: 53 */       return 1.7976931348623157E+308D;
/*  17:    */     }
/*  18: 55 */     double denumerator = splitEnt(bags);
/*  19: 58 */     if (Utils.eq(denumerator, 0.0D)) {
/*  20: 59 */       return 1.7976931348623157E+308D;
/*  21:    */     }
/*  22: 64 */     return denumerator / numerator;
/*  23:    */   }
/*  24:    */   
/*  25:    */   public final double splitCritValue(Distribution bags, double totalnoInst, double numerator)
/*  26:    */   {
/*  27: 79 */     double denumerator = splitEnt(bags, totalnoInst);
/*  28: 82 */     if (Utils.eq(denumerator, 0.0D)) {
/*  29: 83 */       return 0.0D;
/*  30:    */     }
/*  31: 85 */     denumerator /= totalnoInst;
/*  32:    */     
/*  33: 87 */     return numerator / denumerator;
/*  34:    */   }
/*  35:    */   
/*  36:    */   private final double splitEnt(Distribution bags, double totalnoInst)
/*  37:    */   {
/*  38: 95 */     double returnValue = 0.0D;
/*  39:    */     
/*  40:    */ 
/*  41:    */ 
/*  42: 99 */     double noUnknown = totalnoInst - bags.total();
/*  43:100 */     if (Utils.gr(bags.total(), 0.0D))
/*  44:    */     {
/*  45:101 */       for (int i = 0; i < bags.numBags(); i++) {
/*  46:102 */         returnValue -= lnFunc(bags.perBag(i));
/*  47:    */       }
/*  48:104 */       returnValue -= lnFunc(noUnknown);
/*  49:105 */       returnValue += lnFunc(totalnoInst);
/*  50:    */     }
/*  51:107 */     return returnValue / ContingencyTables.log2;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public String getRevision()
/*  55:    */   {
/*  56:117 */     return RevisionUtils.extract("$Revision: 10169 $");
/*  57:    */   }
/*  58:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.GainRatioSplitCrit
 * JD-Core Version:    0.7.0.1
 */