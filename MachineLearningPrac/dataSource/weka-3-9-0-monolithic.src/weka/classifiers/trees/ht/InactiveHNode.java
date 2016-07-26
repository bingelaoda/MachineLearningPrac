/*  1:   */ package weka.classifiers.trees.ht;
/*  2:   */ 
/*  3:   */ import java.io.Serializable;
/*  4:   */ import java.util.Map;
/*  5:   */ import weka.core.Instance;
/*  6:   */ 
/*  7:   */ public class InactiveHNode
/*  8:   */   extends LeafNode
/*  9:   */   implements LearningNode, Serializable
/* 10:   */ {
/* 11:   */   private static final long serialVersionUID = -8747567733141700911L;
/* 12:   */   
/* 13:   */   public InactiveHNode(Map<String, WeightMass> classDistrib)
/* 14:   */   {
/* 15:50 */     this.m_classDistribution = classDistrib;
/* 16:   */   }
/* 17:   */   
/* 18:   */   public void updateNode(Instance inst)
/* 19:   */   {
/* 20:55 */     super.updateDistribution(inst);
/* 21:   */   }
/* 22:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.ht.InactiveHNode
 * JD-Core Version:    0.7.0.1
 */