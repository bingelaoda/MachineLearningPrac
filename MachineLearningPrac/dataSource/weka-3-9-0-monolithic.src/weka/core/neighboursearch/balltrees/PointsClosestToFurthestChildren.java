/*   1:    */ package weka.core.neighboursearch.balltrees;
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
/*  12:    */ public class PointsClosestToFurthestChildren
/*  13:    */   extends BallSplitter
/*  14:    */   implements TechnicalInformationHandler
/*  15:    */ {
/*  16:    */   private static final long serialVersionUID = -2947177543565818260L;
/*  17:    */   
/*  18:    */   public String globalInfo()
/*  19:    */   {
/*  20: 91 */     return "Implements the Moore's method to split a node of a ball tree.\n\nFor more information please see section 2 of the 1st and 3.2.3 of the 2nd:\n\n" + getTechnicalInformation().toString();
/*  21:    */   }
/*  22:    */   
/*  23:    */   public TechnicalInformation getTechnicalInformation()
/*  24:    */   {
/*  25:108 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*  26:109 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Andrew W. Moore");
/*  27:110 */     result.setValue(TechnicalInformation.Field.TITLE, "The Anchors Hierarchy: Using the Triangle Inequality to Survive High Dimensional Data");
/*  28:    */     
/*  29:    */ 
/*  30:    */ 
/*  31:114 */     result.setValue(TechnicalInformation.Field.YEAR, "2000");
/*  32:115 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "UAI '00: Proceedings of the 16th Conference on Uncertainty in Artificial Intelligence");
/*  33:    */     
/*  34:    */ 
/*  35:    */ 
/*  36:119 */     result.setValue(TechnicalInformation.Field.PAGES, "397-405");
/*  37:120 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "Morgan Kaufmann Publishers Inc.");
/*  38:121 */     result.setValue(TechnicalInformation.Field.ADDRESS, "San Francisco, CA, USA");
/*  39:    */     
/*  40:123 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.MASTERSTHESIS);
/*  41:124 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Ashraf Masood Kibriya");
/*  42:125 */     additional.setValue(TechnicalInformation.Field.TITLE, "Fast Algorithms for Nearest Neighbour Search");
/*  43:    */     
/*  44:127 */     additional.setValue(TechnicalInformation.Field.YEAR, "2007");
/*  45:128 */     additional.setValue(TechnicalInformation.Field.SCHOOL, "Department of Computer Science, School of Computing and Mathematical Sciences, University of Waikato");
/*  46:    */     
/*  47:    */ 
/*  48:    */ 
/*  49:132 */     additional.setValue(TechnicalInformation.Field.ADDRESS, "Hamilton, New Zealand");
/*  50:    */     
/*  51:134 */     return result;
/*  52:    */   }
/*  53:    */   
/*  54:    */   public PointsClosestToFurthestChildren() {}
/*  55:    */   
/*  56:    */   public PointsClosestToFurthestChildren(int[] instList, Instances insts, EuclideanDistance e)
/*  57:    */   {
/*  58:150 */     super(instList, insts, e);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void splitNode(BallNode node, int numNodesCreated)
/*  62:    */     throws Exception
/*  63:    */   {
/*  64:164 */     correctlyInitialized();
/*  65:    */     
/*  66:166 */     double maxDist = (-1.0D / 0.0D);double dist = 0.0D;
/*  67:167 */     Instance furthest1 = null;Instance furthest2 = null;Instance pivot = node.getPivot();
/*  68:168 */     double[] distList = new double[node.m_NumInstances];
/*  69:169 */     for (int i = node.m_Start; i <= node.m_End; i++)
/*  70:    */     {
/*  71:170 */       Instance temp = this.m_Instances.instance(this.m_Instlist[i]);
/*  72:171 */       dist = this.m_DistanceFunction.distance(pivot, temp, (1.0D / 0.0D));
/*  73:172 */       if (dist > maxDist)
/*  74:    */       {
/*  75:173 */         maxDist = dist;
/*  76:174 */         furthest1 = temp;
/*  77:    */       }
/*  78:    */     }
/*  79:177 */     maxDist = (-1.0D / 0.0D);
/*  80:178 */     furthest1 = (Instance)furthest1.copy();
/*  81:179 */     for (int i = 0; i < node.m_NumInstances; i++)
/*  82:    */     {
/*  83:180 */       Instance temp = this.m_Instances.instance(this.m_Instlist[(i + node.m_Start)]);
/*  84:181 */       distList[i] = this.m_DistanceFunction.distance(furthest1, temp, (1.0D / 0.0D));
/*  85:183 */       if (distList[i] > maxDist)
/*  86:    */       {
/*  87:184 */         maxDist = distList[i];
/*  88:185 */         furthest2 = temp;
/*  89:    */       }
/*  90:    */     }
/*  91:188 */     furthest2 = (Instance)furthest2.copy();
/*  92:189 */     dist = 0.0D;
/*  93:190 */     int numRight = 0;
/*  94:192 */     for (int i = 0; i < node.m_NumInstances - numRight; i++)
/*  95:    */     {
/*  96:193 */       Instance temp = this.m_Instances.instance(this.m_Instlist[(i + node.m_Start)]);
/*  97:194 */       dist = this.m_DistanceFunction.distance(furthest2, temp, (1.0D / 0.0D));
/*  98:196 */       if (dist < distList[i])
/*  99:    */       {
/* 100:197 */         int t = this.m_Instlist[(node.m_End - numRight)];
/* 101:198 */         this.m_Instlist[(node.m_End - numRight)] = this.m_Instlist[(i + node.m_Start)];
/* 102:199 */         this.m_Instlist[(i + node.m_Start)] = t;
/* 103:200 */         double d = distList[(distList.length - 1 - numRight)];
/* 104:201 */         distList[(distList.length - 1 - numRight)] = distList[i];
/* 105:202 */         distList[i] = d;
/* 106:203 */         numRight++;
/* 107:204 */         i--;
/* 108:    */       }
/* 109:    */     }
/* 110:208 */     if ((numRight <= 0) || (numRight >= node.m_NumInstances)) {
/* 111:209 */       throw new Exception("Illegal value for numRight: " + numRight);
/* 112:    */     }
/* 113:212 */     node.m_Left = new BallNode(node.m_Start, node.m_End - numRight, numNodesCreated + 1, pivot = BallNode.calcCentroidPivot(node.m_Start, node.m_End - numRight, this.m_Instlist, this.m_Instances), BallNode.calcRadius(node.m_Start, node.m_End - numRight, this.m_Instlist, this.m_Instances, pivot, this.m_DistanceFunction));
/* 114:    */     
/* 115:    */ 
/* 116:    */ 
/* 117:    */ 
/* 118:    */ 
/* 119:218 */     node.m_Right = new BallNode(node.m_End - numRight + 1, node.m_End, numNodesCreated + 2, pivot = BallNode.calcCentroidPivot(node.m_End - numRight + 1, node.m_End, this.m_Instlist, this.m_Instances), BallNode.calcRadius(node.m_End - numRight + 1, node.m_End, this.m_Instlist, this.m_Instances, pivot, this.m_DistanceFunction));
/* 120:    */   }
/* 121:    */   
/* 122:    */   public String getRevision()
/* 123:    */   {
/* 124:232 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 125:    */   }
/* 126:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.balltrees.PointsClosestToFurthestChildren
 * JD-Core Version:    0.7.0.1
 */