/*  1:   */ package weka.classifiers.trees.j48;
/*  2:   */ 
/*  3:   */ import weka.core.ContingencyTables;
/*  4:   */ 
/*  5:   */ public abstract class EntropyBasedSplitCrit
/*  6:   */   extends SplitCriterion
/*  7:   */ {
/*  8:   */   private static final long serialVersionUID = -2618691439791653056L;
/*  9:   */   
/* 10:   */   public final double lnFunc(double num)
/* 11:   */   {
/* 12:45 */     if (num < 1.0E-006D) {
/* 13:46 */       return 0.0D;
/* 14:   */     }
/* 15:48 */     return ContingencyTables.lnFunc(num);
/* 16:   */   }
/* 17:   */   
/* 18:   */   public final double oldEnt(Distribution bags)
/* 19:   */   {
/* 20:56 */     double returnValue = 0.0D;
/* 21:59 */     for (int j = 0; j < bags.numClasses(); j++) {
/* 22:60 */       returnValue += lnFunc(bags.perClass(j));
/* 23:   */     }
/* 24:61 */     return (lnFunc(bags.total()) - returnValue) / ContingencyTables.log2;
/* 25:   */   }
/* 26:   */   
/* 27:   */   public final double newEnt(Distribution bags)
/* 28:   */   {
/* 29:69 */     double returnValue = 0.0D;
/* 30:72 */     for (int i = 0; i < bags.numBags(); i++)
/* 31:   */     {
/* 32:73 */       for (int j = 0; j < bags.numClasses(); j++) {
/* 33:74 */         returnValue += lnFunc(bags.perClassPerBag(i, j));
/* 34:   */       }
/* 35:75 */       returnValue -= lnFunc(bags.perBag(i));
/* 36:   */     }
/* 37:77 */     return -(returnValue / ContingencyTables.log2);
/* 38:   */   }
/* 39:   */   
/* 40:   */   public final double splitEnt(Distribution bags)
/* 41:   */   {
/* 42:86 */     double returnValue = 0.0D;
/* 43:89 */     for (int i = 0; i < bags.numBags(); i++) {
/* 44:90 */       returnValue += lnFunc(bags.perBag(i));
/* 45:   */     }
/* 46:91 */     return (lnFunc(bags.total()) - returnValue) / ContingencyTables.log2;
/* 47:   */   }
/* 48:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.EntropyBasedSplitCrit
 * JD-Core Version:    0.7.0.1
 */