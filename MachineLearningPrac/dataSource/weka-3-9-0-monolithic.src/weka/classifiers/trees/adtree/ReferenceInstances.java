/*  1:   */ package weka.classifiers.trees.adtree;
/*  2:   */ 
/*  3:   */ import java.util.ArrayList;
/*  4:   */ import weka.core.Instance;
/*  5:   */ import weka.core.Instances;
/*  6:   */ import weka.core.RevisionUtils;
/*  7:   */ 
/*  8:   */ public class ReferenceInstances
/*  9:   */   extends Instances
/* 10:   */ {
/* 11:   */   private static final long serialVersionUID = -8022666381920252997L;
/* 12:   */   
/* 13:   */   public ReferenceInstances(Instances dataset, int capacity)
/* 14:   */   {
/* 15:50 */     super(dataset, capacity);
/* 16:   */   }
/* 17:   */   
/* 18:   */   public final void addReference(Instance instance)
/* 19:   */   {
/* 20:63 */     this.m_Instances.add(instance);
/* 21:   */   }
/* 22:   */   
/* 23:   */   public String getRevision()
/* 24:   */   {
/* 25:72 */     return RevisionUtils.extract("$Revision: 8109 $");
/* 26:   */   }
/* 27:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.adtree.ReferenceInstances
 * JD-Core Version:    0.7.0.1
 */