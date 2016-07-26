/*   1:    */ package weka.core.neighboursearch.kdtrees;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.RevisionHandler;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ 
/*   7:    */ public class KDTreeNode
/*   8:    */   implements Serializable, RevisionHandler
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -3660396067582792648L;
/*  11:    */   public int m_NodeNumber;
/*  12: 55 */   public KDTreeNode m_Left = null;
/*  13: 58 */   public KDTreeNode m_Right = null;
/*  14:    */   public double m_SplitValue;
/*  15:    */   public int m_SplitDim;
/*  16:    */   public double[][] m_NodeRanges;
/*  17:    */   public double[][] m_NodesRectBounds;
/*  18: 83 */   public int m_Start = 0;
/*  19: 90 */   public int m_End = 0;
/*  20:    */   
/*  21:    */   public KDTreeNode() {}
/*  22:    */   
/*  23:    */   public KDTreeNode(int nodeNum, int startidx, int endidx, double[][] nodeRanges)
/*  24:    */   {
/*  25:109 */     this.m_NodeNumber = nodeNum;
/*  26:110 */     this.m_Start = startidx;this.m_End = endidx;
/*  27:111 */     this.m_NodeRanges = nodeRanges;
/*  28:    */   }
/*  29:    */   
/*  30:    */   public KDTreeNode(int nodeNum, int startidx, int endidx, double[][] nodeRanges, double[][] rectBounds)
/*  31:    */   {
/*  32:129 */     this.m_NodeNumber = nodeNum;
/*  33:130 */     this.m_Start = startidx;this.m_End = endidx;
/*  34:131 */     this.m_NodeRanges = nodeRanges;
/*  35:132 */     this.m_NodesRectBounds = rectBounds;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public int getSplitDim()
/*  39:    */   {
/*  40:141 */     return this.m_SplitDim;
/*  41:    */   }
/*  42:    */   
/*  43:    */   public double getSplitValue()
/*  44:    */   {
/*  45:150 */     return this.m_SplitValue;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public boolean isALeaf()
/*  49:    */   {
/*  50:159 */     return this.m_Left == null;
/*  51:    */   }
/*  52:    */   
/*  53:    */   public int numInstances()
/*  54:    */   {
/*  55:170 */     return this.m_End - this.m_Start + 1;
/*  56:    */   }
/*  57:    */   
/*  58:    */   public String getRevision()
/*  59:    */   {
/*  60:179 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  61:    */   }
/*  62:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.kdtrees.KDTreeNode
 * JD-Core Version:    0.7.0.1
 */