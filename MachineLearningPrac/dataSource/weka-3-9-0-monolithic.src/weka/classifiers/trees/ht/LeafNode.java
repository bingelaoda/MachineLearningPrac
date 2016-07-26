/*  1:   */ package weka.classifiers.trees.ht;
/*  2:   */ 
/*  3:   */ import java.io.Serializable;
/*  4:   */ import weka.core.Instance;
/*  5:   */ 
/*  6:   */ public class LeafNode
/*  7:   */   extends HNode
/*  8:   */   implements Serializable
/*  9:   */ {
/* 10:   */   private static final long serialVersionUID = -3359429731894384404L;
/* 11:   */   public HNode m_theNode;
/* 12:   */   public SplitNode m_parentNode;
/* 13:   */   public String m_parentBranch;
/* 14:   */   
/* 15:   */   public LeafNode() {}
/* 16:   */   
/* 17:   */   public LeafNode(HNode node, SplitNode parentNode, String parentBranch)
/* 18:   */   {
/* 19:65 */     this.m_theNode = node;
/* 20:66 */     this.m_parentNode = parentNode;
/* 21:67 */     this.m_parentBranch = parentBranch;
/* 22:   */   }
/* 23:   */   
/* 24:   */   public void updateNode(Instance inst)
/* 25:   */     throws Exception
/* 26:   */   {
/* 27:72 */     if (this.m_theNode != null) {
/* 28:73 */       this.m_theNode.updateDistribution(inst);
/* 29:   */     } else {
/* 30:75 */       super.updateDistribution(inst);
/* 31:   */     }
/* 32:   */   }
/* 33:   */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.trees.ht.LeafNode
 * JD-Core Version:    0.7.0.1
 */