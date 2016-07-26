/*  1:   */ package weka.classifiers.trees.lmt;
/*  2:   */ 
/*  3:   */ import java.util.Comparator;
/*  4:   */ import weka.core.RevisionHandler;
/*  5:   */ import weka.core.RevisionUtils;
/*  6:   */ 
/*  7:   */ class CompareNode
/*  8:   */   implements Comparator<LMTNode>, RevisionHandler
/*  9:   */ {
/* 10:   */   public int compare(LMTNode o1, LMTNode o2)
/* 11:   */   {
/* 12:54 */     if (o1.m_alpha < o2.m_alpha) {
/* 13:55 */       return -1;
/* 14:   */     }
/* 15:57 */     if (o1.m_alpha > o2.m_alpha) {
/* 16:58 */       return 1;
/* 17:   */     }
/* 18:60 */     return 0;
/* 19:   */   }
/* 20:   */   
/* 21:   */   public String getRevision()
/* 22:   */   {
/* 23:70 */     return RevisionUtils.extract("$Revision: 11566 $");
/* 24:   */   }
/* 25:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.lmt.CompareNode
 * JD-Core Version:    0.7.0.1
 */