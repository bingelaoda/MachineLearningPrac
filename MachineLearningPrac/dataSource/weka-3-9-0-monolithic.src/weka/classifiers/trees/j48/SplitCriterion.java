/*  1:   */ package weka.classifiers.trees.j48;
/*  2:   */ 
/*  3:   */ import java.io.Serializable;
/*  4:   */ import weka.core.RevisionHandler;
/*  5:   */ 
/*  6:   */ public abstract class SplitCriterion
/*  7:   */   implements Serializable, RevisionHandler
/*  8:   */ {
/*  9:   */   private static final long serialVersionUID = 5490996638027101259L;
/* 10:   */   
/* 11:   */   public double splitCritValue(Distribution bags)
/* 12:   */   {
/* 13:48 */     return 0.0D;
/* 14:   */   }
/* 15:   */   
/* 16:   */   public double splitCritValue(Distribution train, Distribution test)
/* 17:   */   {
/* 18:59 */     return 0.0D;
/* 19:   */   }
/* 20:   */   
/* 21:   */   public double splitCritValue(Distribution train, Distribution test, int noClassesDefault)
/* 22:   */   {
/* 23:71 */     return 0.0D;
/* 24:   */   }
/* 25:   */   
/* 26:   */   public double splitCritValue(Distribution train, Distribution test, Distribution defC)
/* 27:   */   {
/* 28:83 */     return 0.0D;
/* 29:   */   }
/* 30:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.j48.SplitCriterion
 * JD-Core Version:    0.7.0.1
 */