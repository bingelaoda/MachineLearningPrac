/*   1:    */ package weka.core.neighboursearch.kdtrees;
/*   2:    */ 
/*   3:    */ import weka.core.EuclideanDistance;
/*   4:    */ import weka.core.Instance;
/*   5:    */ import weka.core.Instances;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ import weka.core.TechnicalInformation;
/*   8:    */ import weka.core.TechnicalInformation.Field;
/*   9:    */ import weka.core.TechnicalInformation.Type;
/*  10:    */ import weka.core.TechnicalInformationHandler;
/*  11:    */ 
/*  12:    */ public class MedianOfWidestDimension
/*  13:    */   extends KDTreeNodeSplitter
/*  14:    */   implements TechnicalInformationHandler
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = 1383443320160540663L;
/*  17:    */   
/*  18:    */   public String globalInfo()
/*  19:    */   {
/*  20: 76 */     return "The class that splits a KDTree node based on the median value of a dimension in which the node's points have the widest spread.\n\nFor more information see also:\n\n" + getTechnicalInformation().toString();
/*  21:    */   }
/*  22:    */   
/*  23:    */   public TechnicalInformation getTechnicalInformation()
/*  24:    */   {
/*  25: 93 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  26: 94 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Jerome H. Friedman and Jon Luis Bentley and Raphael Ari Finkel");
/*  27: 95 */     result.setValue(TechnicalInformation.Field.YEAR, "1977");
/*  28: 96 */     result.setValue(TechnicalInformation.Field.TITLE, "An Algorithm for Finding Best Matches in Logarithmic Expected Time");
/*  29: 97 */     result.setValue(TechnicalInformation.Field.JOURNAL, "ACM Transactions on Mathematics Software");
/*  30: 98 */     result.setValue(TechnicalInformation.Field.PAGES, "209-226");
/*  31: 99 */     result.setValue(TechnicalInformation.Field.MONTH, "September");
/*  32:100 */     result.setValue(TechnicalInformation.Field.VOLUME, "3");
/*  33:101 */     result.setValue(TechnicalInformation.Field.NUMBER, "3");
/*  34:    */     
/*  35:103 */     return result;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void splitNode(KDTreeNode node, int numNodesCreated, double[][] nodeRanges, double[][] universe)
/*  39:    */     throws Exception
/*  40:    */   {
/*  41:126 */     correctlyInitialized();
/*  42:    */     
/*  43:128 */     int splitDim = widestDim(nodeRanges, universe);
/*  44:    */     
/*  45:    */ 
/*  46:    */ 
/*  47:    */ 
/*  48:133 */     int medianIdxIdx = node.m_Start + (node.m_End - node.m_Start) / 2;
/*  49:    */     
/*  50:    */ 
/*  51:136 */     int medianIdx = select(splitDim, this.m_InstList, node.m_Start, node.m_End, (node.m_End - node.m_Start) / 2 + 1);
/*  52:    */     
/*  53:    */ 
/*  54:139 */     node.m_SplitDim = splitDim;
/*  55:140 */     node.m_SplitValue = this.m_Instances.instance(this.m_InstList[medianIdx]).value(splitDim);
/*  56:    */     
/*  57:142 */     node.m_Left = new KDTreeNode(numNodesCreated + 1, node.m_Start, medianIdxIdx, this.m_EuclideanDistance.initializeRanges(this.m_InstList, node.m_Start, medianIdxIdx));
/*  58:    */     
/*  59:144 */     node.m_Right = new KDTreeNode(numNodesCreated + 2, medianIdxIdx + 1, node.m_End, this.m_EuclideanDistance.initializeRanges(this.m_InstList, medianIdxIdx + 1, node.m_End));
/*  60:    */   }
/*  61:    */   
/*  62:    */   protected int partition(int attIdx, int[] index, int l, int r)
/*  63:    */   {
/*  64:164 */     double pivot = this.m_Instances.instance(index[((l + r) / 2)]).value(attIdx);
/*  65:167 */     while (l < r)
/*  66:    */     {
/*  67:168 */       while ((this.m_Instances.instance(index[l]).value(attIdx) < pivot) && (l < r)) {
/*  68:169 */         l++;
/*  69:    */       }
/*  70:171 */       while ((this.m_Instances.instance(index[r]).value(attIdx) > pivot) && (l < r)) {
/*  71:172 */         r--;
/*  72:    */       }
/*  73:174 */       if (l < r)
/*  74:    */       {
/*  75:175 */         int help = index[l];
/*  76:176 */         index[l] = index[r];
/*  77:177 */         index[r] = help;
/*  78:178 */         l++;
/*  79:179 */         r--;
/*  80:    */       }
/*  81:    */     }
/*  82:182 */     if ((l == r) && (this.m_Instances.instance(index[r]).value(attIdx) > pivot)) {
/*  83:183 */       r--;
/*  84:    */     }
/*  85:186 */     return r;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public int select(int attIdx, int[] indices, int left, int right, int k)
/*  89:    */   {
/*  90:206 */     if (left == right) {
/*  91:207 */       return left;
/*  92:    */     }
/*  93:209 */     int middle = partition(attIdx, indices, left, right);
/*  94:210 */     if (middle - left + 1 >= k) {
/*  95:211 */       return select(attIdx, indices, left, middle, k);
/*  96:    */     }
/*  97:213 */     return select(attIdx, indices, middle + 1, right, k - (middle - left + 1));
/*  98:    */   }
/*  99:    */   
/* 100:    */   public String getRevision()
/* 101:    */   {
/* 102:224 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 103:    */   }
/* 104:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.kdtrees.MedianOfWidestDimension
 * JD-Core Version:    0.7.0.1
 */