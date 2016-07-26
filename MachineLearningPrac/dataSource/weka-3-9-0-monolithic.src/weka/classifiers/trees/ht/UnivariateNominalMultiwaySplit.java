/*  1:   */ package weka.classifiers.trees.ht;
/*  2:   */ 
/*  3:   */ import java.io.Serializable;
/*  4:   */ import java.util.List;
/*  5:   */ import weka.core.Attribute;
/*  6:   */ import weka.core.Instance;
/*  7:   */ import weka.core.Instances;
/*  8:   */ 
/*  9:   */ public class UnivariateNominalMultiwaySplit
/* 10:   */   extends Split
/* 11:   */   implements Serializable
/* 12:   */ {
/* 13:   */   private static final long serialVersionUID = -9094590488097956665L;
/* 14:   */   
/* 15:   */   public UnivariateNominalMultiwaySplit(String attName)
/* 16:   */   {
/* 17:50 */     this.m_splitAttNames.add(attName);
/* 18:   */   }
/* 19:   */   
/* 20:   */   public String branchForInstance(Instance inst)
/* 21:   */   {
/* 22:55 */     Attribute att = inst.dataset().attribute((String)this.m_splitAttNames.get(0));
/* 23:56 */     if ((att == null) || (inst.isMissing(att))) {
/* 24:57 */       return null;
/* 25:   */     }
/* 26:59 */     return att.value((int)inst.value(att));
/* 27:   */   }
/* 28:   */   
/* 29:   */   public String conditionForBranch(String branch)
/* 30:   */   {
/* 31:64 */     return (String)this.m_splitAttNames.get(0) + " = " + branch;
/* 32:   */   }
/* 33:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.ht.UnivariateNominalMultiwaySplit
 * JD-Core Version:    0.7.0.1
 */