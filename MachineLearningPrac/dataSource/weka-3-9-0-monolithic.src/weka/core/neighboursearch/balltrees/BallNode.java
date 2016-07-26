/*   1:    */ package weka.core.neighboursearch.balltrees;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import weka.core.DenseInstance;
/*   5:    */ import weka.core.DistanceFunction;
/*   6:    */ import weka.core.Instance;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.RevisionHandler;
/*   9:    */ import weka.core.RevisionUtils;
/*  10:    */ 
/*  11:    */ public class BallNode
/*  12:    */   implements Serializable, RevisionHandler
/*  13:    */ {
/*  14:    */   private static final long serialVersionUID = -8289151861759883510L;
/*  15:    */   public int m_Start;
/*  16:    */   public int m_End;
/*  17:    */   public int m_NumInstances;
/*  18:    */   public int m_NodeNumber;
/*  19: 66 */   public int m_SplitAttrib = -1;
/*  20: 71 */   public double m_SplitVal = -1.0D;
/*  21: 74 */   public BallNode m_Left = null;
/*  22: 77 */   public BallNode m_Right = null;
/*  23:    */   protected Instance m_Pivot;
/*  24:    */   protected double m_Radius;
/*  25:    */   
/*  26:    */   public BallNode(int nodeNumber)
/*  27:    */   {
/*  28: 92 */     this.m_NodeNumber = nodeNumber;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public BallNode(int start, int end, int nodeNumber)
/*  32:    */   {
/*  33:104 */     this.m_Start = start;
/*  34:105 */     this.m_End = end;
/*  35:106 */     this.m_NodeNumber = nodeNumber;
/*  36:107 */     this.m_NumInstances = (end - start + 1);
/*  37:    */   }
/*  38:    */   
/*  39:    */   public BallNode(int start, int end, int nodeNumber, Instance pivot, double radius)
/*  40:    */   {
/*  41:121 */     this.m_Start = start;
/*  42:122 */     this.m_End = end;
/*  43:123 */     this.m_NodeNumber = nodeNumber;
/*  44:124 */     this.m_Pivot = pivot;
/*  45:125 */     this.m_Radius = radius;
/*  46:126 */     this.m_NumInstances = (end - start + 1);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public boolean isALeaf()
/*  50:    */   {
/*  51:135 */     return (this.m_Left == null) && (this.m_Right == null);
/*  52:    */   }
/*  53:    */   
/*  54:    */   public void setStartEndIndices(int start, int end)
/*  55:    */   {
/*  56:148 */     this.m_Start = start;
/*  57:149 */     this.m_End = end;
/*  58:150 */     this.m_NumInstances = (end - start + 1);
/*  59:    */   }
/*  60:    */   
/*  61:    */   public void setPivot(Instance pivot)
/*  62:    */   {
/*  63:159 */     this.m_Pivot = pivot;
/*  64:    */   }
/*  65:    */   
/*  66:    */   public Instance getPivot()
/*  67:    */   {
/*  68:168 */     return this.m_Pivot;
/*  69:    */   }
/*  70:    */   
/*  71:    */   public void setRadius(double radius)
/*  72:    */   {
/*  73:177 */     this.m_Radius = radius;
/*  74:    */   }
/*  75:    */   
/*  76:    */   public double getRadius()
/*  77:    */   {
/*  78:185 */     return this.m_Radius;
/*  79:    */   }
/*  80:    */   
/*  81:    */   public int numInstances()
/*  82:    */   {
/*  83:195 */     return this.m_End - this.m_Start + 1;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public static Instance calcCentroidPivot(int[] instList, Instances insts)
/*  87:    */   {
/*  88:209 */     double[] attrVals = new double[insts.numAttributes()];
/*  89:212 */     for (int i = 0; i < instList.length; i++)
/*  90:    */     {
/*  91:213 */       Instance temp = insts.instance(instList[i]);
/*  92:214 */       for (int j = 0; j < temp.numValues(); j++) {
/*  93:215 */         attrVals[j] += temp.valueSparse(j);
/*  94:    */       }
/*  95:    */     }
/*  96:218 */     int j = 0;
/*  97:218 */     for (int numInsts = instList.length; j < attrVals.length; j++) {
/*  98:219 */       attrVals[j] /= numInsts;
/*  99:    */     }
/* 100:221 */     Instance temp = new DenseInstance(1.0D, attrVals);
/* 101:222 */     return temp;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public static Instance calcCentroidPivot(int start, int end, int[] instList, Instances insts)
/* 105:    */   {
/* 106:241 */     double[] attrVals = new double[insts.numAttributes()];
/* 107:243 */     for (int i = start; i <= end; i++)
/* 108:    */     {
/* 109:244 */       Instance temp = insts.instance(instList[i]);
/* 110:245 */       for (int j = 0; j < temp.numValues(); j++) {
/* 111:246 */         attrVals[j] += temp.valueSparse(j);
/* 112:    */       }
/* 113:    */     }
/* 114:249 */     int j = 0;
/* 115:249 */     for (int numInsts = end - start + 1; j < attrVals.length; j++) {
/* 116:250 */       attrVals[j] /= numInsts;
/* 117:    */     }
/* 118:253 */     Instance temp = new DenseInstance(1.0D, attrVals);
/* 119:254 */     return temp;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public static double calcRadius(int[] instList, Instances insts, Instance pivot, DistanceFunction distanceFunction)
/* 123:    */     throws Exception
/* 124:    */   {
/* 125:274 */     return calcRadius(0, instList.length - 1, instList, insts, pivot, distanceFunction);
/* 126:    */   }
/* 127:    */   
/* 128:    */   public static double calcRadius(int start, int end, int[] instList, Instances insts, Instance pivot, DistanceFunction distanceFunction)
/* 129:    */     throws Exception
/* 130:    */   {
/* 131:300 */     double radius = (-1.0D / 0.0D);
/* 132:302 */     for (int i = start; i <= end; i++)
/* 133:    */     {
/* 134:303 */       double dist = distanceFunction.distance(pivot, insts.instance(instList[i]), (1.0D / 0.0D));
/* 135:306 */       if (dist > radius) {
/* 136:307 */         radius = dist;
/* 137:    */       }
/* 138:    */     }
/* 139:309 */     return Math.sqrt(radius);
/* 140:    */   }
/* 141:    */   
/* 142:    */   public static Instance calcPivot(BallNode child1, BallNode child2, Instances insts)
/* 143:    */     throws Exception
/* 144:    */   {
/* 145:325 */     Instance p1 = child1.getPivot();Instance p2 = child2.getPivot();
/* 146:326 */     double[] attrVals = new double[p1.numAttributes()];
/* 147:328 */     for (int j = 0; j < attrVals.length; j++)
/* 148:    */     {
/* 149:329 */       attrVals[j] += p1.value(j);
/* 150:330 */       attrVals[j] += p2.value(j);
/* 151:331 */       attrVals[j] /= 2.0D;
/* 152:    */     }
/* 153:334 */     p1 = new DenseInstance(1.0D, attrVals);
/* 154:335 */     return p1;
/* 155:    */   }
/* 156:    */   
/* 157:    */   public static double calcRadius(BallNode child1, BallNode child2, Instance pivot, DistanceFunction distanceFunction)
/* 158:    */     throws Exception
/* 159:    */   {
/* 160:354 */     Instance p1 = child1.getPivot();Instance p2 = child2.getPivot();
/* 161:    */     
/* 162:356 */     double radius = child1.getRadius() + distanceFunction.distance(p1, p2) + child2.getRadius();
/* 163:    */     
/* 164:    */ 
/* 165:359 */     return radius / 2.0D;
/* 166:    */   }
/* 167:    */   
/* 168:    */   public String getRevision()
/* 169:    */   {
/* 170:368 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 171:    */   }
/* 172:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.balltrees.BallNode
 * JD-Core Version:    0.7.0.1
 */