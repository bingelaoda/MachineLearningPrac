/*  1:   */ package weka.classifiers.lazy.kstar;
/*  2:   */ 
/*  3:   */ import weka.core.RevisionHandler;
/*  4:   */ import weka.core.RevisionUtils;
/*  5:   */ 
/*  6:   */ public class KStarWrapper
/*  7:   */   implements RevisionHandler
/*  8:   */ {
/*  9:37 */   public double sphere = 0.0D;
/* 10:40 */   public double actEntropy = 0.0D;
/* 11:43 */   public double randEntropy = 0.0D;
/* 12:46 */   public double avgProb = 0.0D;
/* 13:49 */   public double minProb = 0.0D;
/* 14:   */   
/* 15:   */   public String getRevision()
/* 16:   */   {
/* 17:57 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 18:   */   }
/* 19:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.lazy.kstar.KStarWrapper
 * JD-Core Version:    0.7.0.1
 */