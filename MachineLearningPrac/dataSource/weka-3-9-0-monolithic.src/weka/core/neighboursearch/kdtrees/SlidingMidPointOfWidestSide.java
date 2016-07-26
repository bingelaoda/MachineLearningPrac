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
/*  11:    */ public class SlidingMidPointOfWidestSide
/*  12:    */   extends KDTreeNodeSplitter
/*  13:    */   implements TechnicalInformationHandler
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = 852857628205680562L;
/*  16: 69 */   protected static double ERR = 0.001D;
/*  17:    */   
/*  18:    */   public String globalInfo()
/*  19:    */   {
/*  20: 78 */     return "The class that splits a node into two based on the midpoint value of the dimension in which the node's rectangle is widest. If after splitting one side is empty then it is slided towards the non-empty side until there is at least one point on the empty side.\n\nFor more information see also:\n\n" + getTechnicalInformation().toString();
/*  21:    */   }
/*  22:    */   
/*  23:    */   public TechnicalInformation getTechnicalInformation()
/*  24:    */   {
/*  25: 97 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.MANUAL);
/*  26: 98 */     result.setValue(TechnicalInformation.Field.AUTHOR, "David M. Mount");
/*  27: 99 */     result.setValue(TechnicalInformation.Field.YEAR, "2006");
/*  28:100 */     result.setValue(TechnicalInformation.Field.TITLE, "ANN Programming Manual");
/*  29:101 */     result.setValue(TechnicalInformation.Field.ORGANIZATION, "Department of Computer Science, University of Maryland");
/*  30:102 */     result.setValue(TechnicalInformation.Field.ADDRESS, "College Park, MD, USA");
/*  31:    */     
/*  32:104 */     result.setValue(TechnicalInformation.Field.HTTP, "Available from http://www.cs.umd.edu/~mount/ANN/");
/*  33:    */     
/*  34:    */ 
/*  35:107 */     return result;
/*  36:    */   }
/*  37:    */   
/*  38:    */   public void splitNode(KDTreeNode node, int numNodesCreated, double[][] nodeRanges, double[][] universe)
/*  39:    */     throws Exception
/*  40:    */   {
/*  41:131 */     correctlyInitialized();
/*  42:133 */     if (node.m_NodesRectBounds == null)
/*  43:    */     {
/*  44:134 */       node.m_NodesRectBounds = new double[2][node.m_NodeRanges.length];
/*  45:135 */       for (int i = 0; i < node.m_NodeRanges.length; i++)
/*  46:    */       {
/*  47:136 */         node.m_NodesRectBounds[0][i] = node.m_NodeRanges[i][0];
/*  48:137 */         node.m_NodesRectBounds[1][i] = node.m_NodeRanges[i][1];
/*  49:    */       }
/*  50:    */     }
/*  51:142 */     double maxRectWidth = (-1.0D / 0.0D);double maxPtWidth = (-1.0D / 0.0D);
/*  52:143 */     int splitDim = -1;int classIdx = this.m_Instances.classIndex();
/*  53:145 */     for (int i = 0; i < node.m_NodesRectBounds[0].length; i++) {
/*  54:146 */       if (i != classIdx)
/*  55:    */       {
/*  56:148 */         double tempval = node.m_NodesRectBounds[1][i] - node.m_NodesRectBounds[0][i];
/*  57:149 */         if (this.m_NormalizeNodeWidth) {
/*  58:150 */           tempval /= universe[i][2];
/*  59:    */         }
/*  60:152 */         if ((tempval > maxRectWidth) && (node.m_NodeRanges[i][2] > 0.0D)) {
/*  61:153 */           maxRectWidth = tempval;
/*  62:    */         }
/*  63:    */       }
/*  64:    */     }
/*  65:156 */     for (int i = 0; i < node.m_NodesRectBounds[0].length; i++) {
/*  66:157 */       if (i != classIdx)
/*  67:    */       {
/*  68:159 */         double tempval = node.m_NodesRectBounds[1][i] - node.m_NodesRectBounds[0][i];
/*  69:160 */         if (this.m_NormalizeNodeWidth) {
/*  70:161 */           tempval /= universe[i][2];
/*  71:    */         }
/*  72:163 */         if ((tempval >= maxRectWidth * (1.0D - ERR)) && (node.m_NodeRanges[i][2] > 0.0D)) {
/*  73:165 */           if (node.m_NodeRanges[i][2] > maxPtWidth)
/*  74:    */           {
/*  75:166 */             maxPtWidth = node.m_NodeRanges[i][2];
/*  76:167 */             if (this.m_NormalizeNodeWidth) {
/*  77:168 */               maxPtWidth /= universe[i][2];
/*  78:    */             }
/*  79:169 */             splitDim = i;
/*  80:    */           }
/*  81:    */         }
/*  82:    */       }
/*  83:    */     }
/*  84:174 */     double splitVal = node.m_NodesRectBounds[0][splitDim] + (node.m_NodesRectBounds[1][splitDim] - node.m_NodesRectBounds[0][splitDim]) * 0.5D;
/*  85:180 */     if (splitVal < node.m_NodeRanges[splitDim][0]) {
/*  86:181 */       splitVal = node.m_NodeRanges[splitDim][0];
/*  87:182 */     } else if (splitVal >= node.m_NodeRanges[splitDim][1]) {
/*  88:183 */       splitVal = node.m_NodeRanges[splitDim][1] - node.m_NodeRanges[splitDim][2] * 0.001D;
/*  89:    */     }
/*  90:186 */     int rightStart = rearrangePoints(this.m_InstList, node.m_Start, node.m_End, splitDim, splitVal);
/*  91:189 */     if ((rightStart == node.m_Start) || (rightStart > node.m_End))
/*  92:    */     {
/*  93:190 */       if (rightStart == node.m_Start) {
/*  94:191 */         throw new Exception("Left child is empty in node " + node.m_NodeNumber + ". Not possible with " + "SlidingMidPointofWidestSide splitting method. Please " + "check code.");
/*  95:    */       }
/*  96:196 */       throw new Exception("Right child is empty in node " + node.m_NodeNumber + ". Not possible with " + "SlidingMidPointofWidestSide splitting method. Please " + "check code.");
/*  97:    */     }
/*  98:202 */     node.m_SplitDim = splitDim;
/*  99:203 */     node.m_SplitValue = splitVal;
/* 100:    */     
/* 101:205 */     double[][] widths = new double[2][node.m_NodesRectBounds[0].length];
/* 102:    */     
/* 103:207 */     System.arraycopy(node.m_NodesRectBounds[0], 0, widths[0], 0, node.m_NodesRectBounds[0].length);
/* 104:    */     
/* 105:209 */     System.arraycopy(node.m_NodesRectBounds[1], 0, widths[1], 0, node.m_NodesRectBounds[1].length);
/* 106:    */     
/* 107:211 */     widths[1][splitDim] = splitVal;
/* 108:    */     
/* 109:213 */     node.m_Left = new KDTreeNode(numNodesCreated + 1, node.m_Start, rightStart - 1, this.m_EuclideanDistance.initializeRanges(this.m_InstList, node.m_Start, rightStart - 1), widths);
/* 110:    */     
/* 111:    */ 
/* 112:    */ 
/* 113:217 */     widths = new double[2][node.m_NodesRectBounds[0].length];
/* 114:218 */     System.arraycopy(node.m_NodesRectBounds[0], 0, widths[0], 0, node.m_NodesRectBounds[0].length);
/* 115:    */     
/* 116:220 */     System.arraycopy(node.m_NodesRectBounds[1], 0, widths[1], 0, node.m_NodesRectBounds[1].length);
/* 117:    */     
/* 118:222 */     widths[0][splitDim] = splitVal;
/* 119:    */     
/* 120:224 */     node.m_Right = new KDTreeNode(numNodesCreated + 2, rightStart, node.m_End, this.m_EuclideanDistance.initializeRanges(this.m_InstList, rightStart, node.m_End), widths);
/* 121:    */   }
/* 122:    */   
/* 123:    */   protected int rearrangePoints(int[] indices, int startidx, int endidx, int splitDim, double splitVal)
/* 124:    */   {
/* 125:245 */     int left = startidx - 1;
/* 126:246 */     for (int i = startidx; i <= endidx; i++) {
/* 127:247 */       if (this.m_EuclideanDistance.valueIsSmallerEqual(this.m_Instances.instance(indices[i]), splitDim, splitVal))
/* 128:    */       {
/* 129:249 */         left++;
/* 130:250 */         int tmp = indices[left];
/* 131:251 */         indices[left] = indices[i];
/* 132:252 */         indices[i] = tmp;
/* 133:    */       }
/* 134:    */     }
/* 135:255 */     return left + 1;
/* 136:    */   }
/* 137:    */   
/* 138:    */   public String getRevision()
/* 139:    */   {
/* 140:264 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 141:    */   }
/* 142:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.kdtrees.SlidingMidPointOfWidestSide
 * JD-Core Version:    0.7.0.1
 */