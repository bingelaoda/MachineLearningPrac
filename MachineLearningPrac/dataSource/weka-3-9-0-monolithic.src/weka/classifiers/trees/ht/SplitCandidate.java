/*  1:   */ package weka.classifiers.trees.ht;
/*  2:   */ 
/*  3:   */ import java.util.List;
/*  4:   */ import java.util.Map;
/*  5:   */ 
/*  6:   */ public class SplitCandidate
/*  7:   */   implements Comparable<SplitCandidate>
/*  8:   */ {
/*  9:   */   public Split m_splitTest;
/* 10:   */   public List<Map<String, WeightMass>> m_postSplitClassDistributions;
/* 11:   */   public double m_splitMerit;
/* 12:   */   
/* 13:   */   public SplitCandidate(Split splitTest, List<Map<String, WeightMass>> postSplitDists, double merit)
/* 14:   */   {
/* 15:56 */     this.m_splitTest = splitTest;
/* 16:57 */     this.m_postSplitClassDistributions = postSplitDists;
/* 17:58 */     this.m_splitMerit = merit;
/* 18:   */   }
/* 19:   */   
/* 20:   */   public int numSplits()
/* 21:   */   {
/* 22:67 */     return this.m_postSplitClassDistributions.size();
/* 23:   */   }
/* 24:   */   
/* 25:   */   public int compareTo(SplitCandidate comp)
/* 26:   */   {
/* 27:72 */     return Double.compare(this.m_splitMerit, comp.m_splitMerit);
/* 28:   */   }
/* 29:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.ht.SplitCandidate
 * JD-Core Version:    0.7.0.1
 */