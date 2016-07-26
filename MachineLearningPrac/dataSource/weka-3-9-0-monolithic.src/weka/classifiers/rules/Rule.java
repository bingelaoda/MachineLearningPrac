/*  1:   */ package weka.classifiers.rules;
/*  2:   */ 
/*  3:   */ import java.io.Serializable;
/*  4:   */ import weka.core.Copyable;
/*  5:   */ import weka.core.Instance;
/*  6:   */ import weka.core.Instances;
/*  7:   */ import weka.core.RevisionHandler;
/*  8:   */ import weka.core.WeightedInstancesHandler;
/*  9:   */ 
/* 10:   */ public abstract class Rule
/* 11:   */   implements WeightedInstancesHandler, Copyable, Serializable, RevisionHandler
/* 12:   */ {
/* 13:   */   private static final long serialVersionUID = 8815687740470471229L;
/* 14:   */   
/* 15:   */   public Object copy()
/* 16:   */   {
/* 17:48 */     return this;
/* 18:   */   }
/* 19:   */   
/* 20:   */   public abstract boolean covers(Instance paramInstance);
/* 21:   */   
/* 22:   */   public abstract void grow(Instances paramInstances)
/* 23:   */     throws Exception;
/* 24:   */   
/* 25:   */   public abstract boolean hasAntds();
/* 26:   */   
/* 27:   */   public abstract double getConsequent();
/* 28:   */   
/* 29:   */   public abstract double size();
/* 30:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.rules.Rule
 * JD-Core Version:    0.7.0.1
 */