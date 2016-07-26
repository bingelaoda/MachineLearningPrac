/*  1:   */ package weka.classifiers.trees.j48;
/*  2:   */ 
/*  3:   */ import weka.core.ContingencyTables;
/*  4:   */ import weka.core.RevisionUtils;
/*  5:   */ import weka.core.Utils;
/*  6:   */ 
/*  7:   */ public final class EntropySplitCrit
/*  8:   */   extends EntropyBasedSplitCrit
/*  9:   */ {
/* 10:   */   private static final long serialVersionUID = 5986252682266803935L;
/* 11:   */   
/* 12:   */   public final double splitCritValue(Distribution bags)
/* 13:   */   {
/* 14:45 */     return newEnt(bags);
/* 15:   */   }
/* 16:   */   
/* 17:   */   public final double splitCritValue(Distribution train, Distribution test)
/* 18:   */   {
/* 19:53 */     double result = 0.0D;
/* 20:54 */     int numClasses = 0;
/* 21:58 */     for (int j = 0; j < test.numClasses(); j++) {
/* 22:59 */       if ((Utils.gr(train.perClass(j), 0.0D)) || (Utils.gr(test.perClass(j), 0.0D))) {
/* 23:60 */         numClasses++;
/* 24:   */       }
/* 25:   */     }
/* 26:63 */     for (int i = 0; i < test.numBags(); i++) {
/* 27:64 */       if (Utils.gr(test.perBag(i), 0.0D))
/* 28:   */       {
/* 29:65 */         for (j = 0; j < test.numClasses(); j++) {
/* 30:66 */           if (Utils.gr(test.perClassPerBag(i, j), 0.0D)) {
/* 31:67 */             result -= test.perClassPerBag(i, j) * Math.log(train.perClassPerBag(i, j) + 1.0D);
/* 32:   */           }
/* 33:   */         }
/* 34:69 */         result += test.perBag(i) * Math.log(train.perBag(i) + numClasses);
/* 35:   */       }
/* 36:   */     }
/* 37:72 */     return result / ContingencyTables.log2;
/* 38:   */   }
/* 39:   */   
/* 40:   */   public String getRevision()
/* 41:   */   {
/* 42:81 */     return RevisionUtils.extract("$Revision: 10055 $");
/* 43:   */   }
/* 44:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.EntropySplitCrit
 * JD-Core Version:    0.7.0.1
 */