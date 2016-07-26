/*   1:    */ package weka.core.neighboursearch.kdtrees;
/*   2:    */ 
/*   3:    */ import weka.core.EuclideanDistance;
/*   4:    */ import weka.core.Instances;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ import weka.core.TechnicalInformation;
/*   7:    */ import weka.core.TechnicalInformation.Field;
/*   8:    */ import weka.core.TechnicalInformation.Type;
/*   9:    */ import weka.core.TechnicalInformationHandler;
/*  10:    */ 
/*  11:    */ public class MidPointOfWidestDimension
/*  12:    */   extends KDTreeNodeSplitter
/*  13:    */   implements TechnicalInformationHandler
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = -7617277960046591906L;
/*  16:    */   
/*  17:    */   public String globalInfo()
/*  18:    */   {
/*  19: 74 */     return "The class that splits a KDTree node based on the midpoint value of a dimension in which the node's points have the widest spread.\n\nFor more information see also:\n\n" + getTechnicalInformation().toString();
/*  20:    */   }
/*  21:    */   
/*  22:    */   public TechnicalInformation getTechnicalInformation()
/*  23:    */   {
/*  24: 91 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.TECHREPORT);
/*  25: 92 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Andrew Moore");
/*  26: 93 */     result.setValue(TechnicalInformation.Field.YEAR, "1991");
/*  27: 94 */     result.setValue(TechnicalInformation.Field.TITLE, "A tutorial on kd-trees");
/*  28: 95 */     result.setValue(TechnicalInformation.Field.HOWPUBLISHED, "Extract from PhD Thesis");
/*  29: 96 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "University of Cambridge Computer Laboratory Technical Report No. 209");
/*  30: 97 */     result.setValue(TechnicalInformation.Field.HTTP, "http://www.autonlab.org/autonweb/14665.html");
/*  31:    */     
/*  32: 99 */     return result;
/*  33:    */   }
/*  34:    */   
/*  35:    */   public void splitNode(KDTreeNode node, int numNodesCreated, double[][] nodeRanges, double[][] universe)
/*  36:    */     throws Exception
/*  37:    */   {
/*  38:121 */     correctlyInitialized();
/*  39:    */     
/*  40:123 */     int splitDim = widestDim(nodeRanges, universe);
/*  41:    */     
/*  42:125 */     double splitVal = this.m_EuclideanDistance.getMiddle(nodeRanges[splitDim]);
/*  43:    */     
/*  44:127 */     int rightStart = rearrangePoints(this.m_InstList, node.m_Start, node.m_End, splitDim, splitVal);
/*  45:130 */     if ((rightStart == node.m_Start) || (rightStart > node.m_End))
/*  46:    */     {
/*  47:131 */       if (rightStart == node.m_Start) {
/*  48:132 */         throw new Exception("Left child is empty in node " + node.m_NodeNumber + ". Not possible with " + "MidPointofWidestDim splitting method. Please " + "check code.");
/*  49:    */       }
/*  50:138 */       throw new Exception("Right child is empty in node " + node.m_NodeNumber + ". Not possible with " + "MidPointofWidestDim splitting method. Please " + "check code.");
/*  51:    */     }
/*  52:144 */     node.m_SplitDim = splitDim;
/*  53:145 */     node.m_SplitValue = splitVal;
/*  54:146 */     node.m_Left = new KDTreeNode(numNodesCreated + 1, node.m_Start, rightStart - 1, this.m_EuclideanDistance.initializeRanges(this.m_InstList, node.m_Start, rightStart - 1));
/*  55:    */     
/*  56:    */ 
/*  57:149 */     node.m_Right = new KDTreeNode(numNodesCreated + 2, rightStart, node.m_End, this.m_EuclideanDistance.initializeRanges(this.m_InstList, rightStart, node.m_End));
/*  58:    */   }
/*  59:    */   
/*  60:    */   protected int rearrangePoints(int[] indices, int startidx, int endidx, int splitDim, double splitVal)
/*  61:    */   {
/*  62:171 */     int left = startidx - 1;
/*  63:172 */     for (int i = startidx; i <= endidx; i++) {
/*  64:173 */       if (this.m_EuclideanDistance.valueIsSmallerEqual(this.m_Instances.instance(indices[i]), splitDim, splitVal))
/*  65:    */       {
/*  66:175 */         left++;
/*  67:176 */         int tmp = indices[left];
/*  68:177 */         indices[left] = indices[i];
/*  69:178 */         indices[i] = tmp;
/*  70:    */       }
/*  71:    */     }
/*  72:181 */     return left + 1;
/*  73:    */   }
/*  74:    */   
/*  75:    */   public String getRevision()
/*  76:    */   {
/*  77:190 */     return RevisionUtils.extract("$Revision: 8034 $");
/*  78:    */   }
/*  79:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.kdtrees.MidPointOfWidestDimension
 * JD-Core Version:    0.7.0.1
 */