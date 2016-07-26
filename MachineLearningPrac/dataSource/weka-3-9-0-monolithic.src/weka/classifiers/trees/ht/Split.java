/*  1:   */ package weka.classifiers.trees.ht;
/*  2:   */ 
/*  3:   */ import java.io.Serializable;
/*  4:   */ import java.util.ArrayList;
/*  5:   */ import java.util.List;
/*  6:   */ import weka.core.Instance;
/*  7:   */ 
/*  8:   */ public abstract class Split
/*  9:   */   implements Serializable
/* 10:   */ {
/* 11:   */   private static final long serialVersionUID = 5390368487675958092L;
/* 12:47 */   protected List<String> m_splitAttNames = new ArrayList();
/* 13:   */   
/* 14:   */   public abstract String branchForInstance(Instance paramInstance);
/* 15:   */   
/* 16:   */   public abstract String conditionForBranch(String paramString);
/* 17:   */   
/* 18:   */   public List<String> splitAttributes()
/* 19:   */   {
/* 20:66 */     return this.m_splitAttNames;
/* 21:   */   }
/* 22:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.ht.Split
 * JD-Core Version:    0.7.0.1
 */