/*   1:    */ package weka.classifiers.trees.j48;
/*   2:    */ 
/*   3:    */ import weka.core.RevisionUtils;
/*   4:    */ import weka.core.Utils;
/*   5:    */ 
/*   6:    */ public final class InfoGainSplitCrit
/*   7:    */   extends EntropyBasedSplitCrit
/*   8:    */ {
/*   9:    */   private static final long serialVersionUID = 4892105020180728499L;
/*  10:    */   
/*  11:    */   public final double splitCritValue(Distribution bags)
/*  12:    */   {
/*  13: 47 */     double numerator = oldEnt(bags) - newEnt(bags);
/*  14: 50 */     if (Utils.eq(numerator, 0.0D)) {
/*  15: 51 */       return 1.7976931348623157E+308D;
/*  16:    */     }
/*  17: 56 */     return bags.total() / numerator;
/*  18:    */   }
/*  19:    */   
/*  20:    */   public final double splitCritValue(Distribution bags, double totalNoInst)
/*  21:    */   {
/*  22: 71 */     double noUnknown = totalNoInst - bags.total();
/*  23: 72 */     double unknownRate = noUnknown / totalNoInst;
/*  24: 73 */     double numerator = oldEnt(bags) - newEnt(bags);
/*  25: 74 */     numerator = (1.0D - unknownRate) * numerator;
/*  26: 77 */     if (Utils.eq(numerator, 0.0D)) {
/*  27: 78 */       return 0.0D;
/*  28:    */     }
/*  29: 81 */     return numerator / bags.total();
/*  30:    */   }
/*  31:    */   
/*  32:    */   public final double splitCritValue(Distribution bags, double totalNoInst, double oldEnt)
/*  33:    */   {
/*  34: 97 */     double noUnknown = totalNoInst - bags.total();
/*  35: 98 */     double unknownRate = noUnknown / totalNoInst;
/*  36: 99 */     double numerator = oldEnt - newEnt(bags);
/*  37:100 */     numerator = (1.0D - unknownRate) * numerator;
/*  38:103 */     if (Utils.eq(numerator, 0.0D)) {
/*  39:104 */       return 0.0D;
/*  40:    */     }
/*  41:107 */     return numerator / bags.total();
/*  42:    */   }
/*  43:    */   
/*  44:    */   public String getRevision()
/*  45:    */   {
/*  46:117 */     return RevisionUtils.extract("$Revision: 10169 $");
/*  47:    */   }
/*  48:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.InfoGainSplitCrit
 * JD-Core Version:    0.7.0.1
 */