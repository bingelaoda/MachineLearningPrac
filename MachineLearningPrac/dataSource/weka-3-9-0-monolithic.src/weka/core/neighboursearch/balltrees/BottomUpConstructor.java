/*   1:    */ package weka.core.neighboursearch.balltrees;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.ArrayList;
/*   5:    */ import weka.core.DenseInstance;
/*   6:    */ import weka.core.DistanceFunction;
/*   7:    */ import weka.core.Instance;
/*   8:    */ import weka.core.Instances;
/*   9:    */ import weka.core.RevisionHandler;
/*  10:    */ import weka.core.RevisionUtils;
/*  11:    */ import weka.core.TechnicalInformation;
/*  12:    */ import weka.core.TechnicalInformation.Field;
/*  13:    */ import weka.core.TechnicalInformation.Type;
/*  14:    */ import weka.core.TechnicalInformationHandler;
/*  15:    */ 
/*  16:    */ public class BottomUpConstructor
/*  17:    */   extends BallTreeConstructor
/*  18:    */   implements TechnicalInformationHandler
/*  19:    */ {
/*  20:    */   private static final long serialVersionUID = 5864250777657707687L;
/*  21:    */   
/*  22:    */   public String globalInfo()
/*  23:    */   {
/*  24: 87 */     return "The class that constructs a ball tree bottom up.";
/*  25:    */   }
/*  26:    */   
/*  27:    */   public TechnicalInformation getTechnicalInformation()
/*  28:    */   {
/*  29:100 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.TECHREPORT);
/*  30:101 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Stephen M. Omohundro");
/*  31:102 */     result.setValue(TechnicalInformation.Field.YEAR, "1989");
/*  32:103 */     result.setValue(TechnicalInformation.Field.TITLE, "Five Balltree Construction Algorithms");
/*  33:104 */     result.setValue(TechnicalInformation.Field.MONTH, "December");
/*  34:105 */     result.setValue(TechnicalInformation.Field.NUMBER, "TR-89-063");
/*  35:106 */     result.setValue(TechnicalInformation.Field.INSTITUTION, "International Computer Science Institute");
/*  36:    */     
/*  37:108 */     return result;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public BallNode buildTree()
/*  41:    */     throws Exception
/*  42:    */   {
/*  43:124 */     ArrayList<TempNode> list = new ArrayList();
/*  44:126 */     for (int i = 0; i < this.m_InstList.length; i++)
/*  45:    */     {
/*  46:127 */       TempNode n = new TempNode();
/*  47:128 */       n.points = new int[1];n.points[0] = this.m_InstList[i];
/*  48:129 */       n.anchor = this.m_Instances.instance(this.m_InstList[i]);
/*  49:130 */       n.radius = 0.0D;
/*  50:131 */       list.add(n);
/*  51:    */     }
/*  52:134 */     return mergeNodes(list, 0, this.m_InstList.length - 1, this.m_InstList);
/*  53:    */   }
/*  54:    */   
/*  55:    */   protected BallNode mergeNodes(ArrayList<TempNode> list, int startIdx, int endIdx, int[] instList)
/*  56:    */     throws Exception
/*  57:    */   {
/*  58:156 */     double minRadius = (1.0D / 0.0D);
/*  59:157 */     Instance minPivot = null;int min1 = -1;int min2 = -1;
/*  60:158 */     int[] minInstList = null;int merge = 1;
/*  61:161 */     while (list.size() > 1)
/*  62:    */     {
/*  63:162 */       System.err.print("merge step: " + merge++ + "               \r");
/*  64:163 */       minRadius = (1.0D / 0.0D);
/*  65:164 */       min1 = -1;min2 = -1;
/*  66:166 */       for (int i = 0; i < list.size(); i++)
/*  67:    */       {
/*  68:167 */         TempNode first = (TempNode)list.get(i);
/*  69:168 */         for (int j = i + 1; j < list.size(); j++)
/*  70:    */         {
/*  71:169 */           TempNode second = (TempNode)list.get(j);
/*  72:170 */           Instance pivot = calcPivot(first, second, this.m_Instances);
/*  73:171 */           double tmpRadius = calcRadius(first, second);
/*  74:172 */           if (tmpRadius < minRadius)
/*  75:    */           {
/*  76:173 */             minRadius = tmpRadius;
/*  77:174 */             min1 = i;min2 = j;
/*  78:175 */             minPivot = pivot;
/*  79:    */           }
/*  80:    */         }
/*  81:    */       }
/*  82:179 */       TempNode parent = new TempNode();
/*  83:180 */       parent.left = ((TempNode)list.get(min1));
/*  84:181 */       parent.right = ((TempNode)list.get(min2));
/*  85:182 */       minInstList = new int[parent.left.points.length + parent.right.points.length];
/*  86:183 */       System.arraycopy(parent.left.points, 0, minInstList, 0, parent.left.points.length);
/*  87:184 */       System.arraycopy(parent.right.points, 0, minInstList, parent.left.points.length, parent.right.points.length);
/*  88:    */       
/*  89:186 */       parent.points = minInstList;
/*  90:187 */       parent.anchor = minPivot;
/*  91:188 */       parent.radius = BallNode.calcRadius(parent.points, this.m_Instances, minPivot, this.m_DistanceFunction);
/*  92:189 */       list.remove(min1);list.remove(min2 - 1);
/*  93:190 */       list.add(parent);
/*  94:    */     }
/*  95:192 */     System.err.println("");
/*  96:193 */     TempNode tmpRoot = (TempNode)list.get(0);
/*  97:195 */     if (this.m_InstList.length != tmpRoot.points.length) {
/*  98:196 */       throw new Exception("Root nodes instance list is of irregular length. Please check code.");
/*  99:    */     }
/* 100:198 */     System.arraycopy(tmpRoot.points, 0, this.m_InstList, 0, tmpRoot.points.length);
/* 101:    */     
/* 102:200 */     this.m_NumNodes = (this.m_MaxDepth = this.m_NumLeaves = 0);
/* 103:201 */     double tmpRadius = BallNode.calcRadius(instList, this.m_Instances, tmpRoot.anchor, this.m_DistanceFunction);
/* 104:202 */     BallNode node = makeBallTree(tmpRoot, startIdx, endIdx, instList, 0, tmpRadius);
/* 105:    */     
/* 106:204 */     return node;
/* 107:    */   }
/* 108:    */   
/* 109:    */   protected BallNode makeBallTree(TempNode node, int startidx, int endidx, int[] instList, int depth, double rootRadius)
/* 110:    */     throws Exception
/* 111:    */   {
/* 112:226 */     BallNode ball = null;
/* 113:229 */     if (this.m_MaxDepth < depth) {
/* 114:230 */       this.m_MaxDepth = depth;
/* 115:    */     }
/* 116:232 */     if ((node.points.length > this.m_MaxInstancesInLeaf) && (rootRadius != 0.0D) && (node.radius / rootRadius >= this.m_MaxRelLeafRadius) && (node.left != null) && (node.right != null))
/* 117:    */     {
/* 118:    */       Instance pivot;
/* 119:235 */       ball = new BallNode(startidx, endidx, this.m_NumNodes, pivot = BallNode.calcCentroidPivot(startidx, endidx, instList, this.m_Instances), BallNode.calcRadius(startidx, endidx, instList, this.m_Instances, pivot, this.m_DistanceFunction));
/* 120:    */       
/* 121:    */ 
/* 122:    */ 
/* 123:    */ 
/* 124:    */ 
/* 125:241 */       this.m_NumNodes += 1;
/* 126:242 */       ball.m_Left = makeBallTree(node.left, startidx, startidx + node.left.points.length - 1, instList, depth + 1, rootRadius);
/* 127:243 */       ball.m_Right = makeBallTree(node.right, startidx + node.left.points.length, endidx, instList, depth + 1, rootRadius);
/* 128:    */     }
/* 129:    */     else
/* 130:    */     {
/* 131:    */       Instance pivot;
/* 132:246 */       ball = new BallNode(startidx, endidx, this.m_NumNodes, pivot = BallNode.calcCentroidPivot(startidx, endidx, instList, this.m_Instances), BallNode.calcRadius(startidx, endidx, instList, this.m_Instances, pivot, this.m_DistanceFunction));
/* 133:    */       
/* 134:    */ 
/* 135:    */ 
/* 136:    */ 
/* 137:251 */       this.m_NumNodes += 1;
/* 138:252 */       this.m_NumLeaves += 1;
/* 139:    */     }
/* 140:254 */     return ball;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public int[] addInstance(BallNode node, Instance inst)
/* 144:    */     throws Exception
/* 145:    */   {
/* 146:269 */     throw new Exception("BottomUpConstruction method does not allow addition of new Instances.");
/* 147:    */   }
/* 148:    */   
/* 149:    */   public Instance calcPivot(TempNode node1, TempNode node2, Instances insts)
/* 150:    */     throws Exception
/* 151:    */   {
/* 152:286 */     int classIdx = this.m_Instances.classIndex();
/* 153:287 */     double[] attrVals = new double[insts.numAttributes()];
/* 154:    */     
/* 155:289 */     double anchr1Ratio = node1.points.length / (node1.points.length + node2.points.length);
/* 156:    */     
/* 157:291 */     double anchr2Ratio = node2.points.length / (node1.points.length + node2.points.length);
/* 158:293 */     for (int k = 0; k < node1.anchor.numValues(); k++) {
/* 159:294 */       if (node1.anchor.index(k) != classIdx) {
/* 160:296 */         attrVals[k] += node1.anchor.valueSparse(k) * anchr1Ratio;
/* 161:    */       }
/* 162:    */     }
/* 163:298 */     for (int k = 0; k < node2.anchor.numValues(); k++) {
/* 164:299 */       if (node2.anchor.index(k) != classIdx) {
/* 165:301 */         attrVals[k] += node2.anchor.valueSparse(k) * anchr2Ratio;
/* 166:    */       }
/* 167:    */     }
/* 168:303 */     Instance temp = new DenseInstance(1.0D, attrVals);
/* 169:304 */     return temp;
/* 170:    */   }
/* 171:    */   
/* 172:    */   public double calcRadius(TempNode n1, TempNode n2)
/* 173:    */     throws Exception
/* 174:    */   {
/* 175:317 */     Instance a1 = n1.anchor;Instance a2 = n2.anchor;
/* 176:318 */     double radius = n1.radius + this.m_DistanceFunction.distance(a1, a2) + n2.radius;
/* 177:319 */     return radius / 2.0D;
/* 178:    */   }
/* 179:    */   
/* 180:    */   protected class TempNode
/* 181:    */     implements RevisionHandler
/* 182:    */   {
/* 183:    */     Instance anchor;
/* 184:    */     double radius;
/* 185:    */     int[] points;
/* 186:340 */     TempNode left = null;
/* 187:342 */     TempNode right = null;
/* 188:    */     
/* 189:    */     protected TempNode() {}
/* 190:    */     
/* 191:    */     public String toString()
/* 192:    */     {
/* 193:349 */       StringBuffer bf = new StringBuffer();
/* 194:350 */       bf.append("p: ");
/* 195:351 */       for (int i = 0; i < this.points.length; i++) {
/* 196:352 */         if (i != 0) {
/* 197:353 */           bf.append(", " + this.points[i]);
/* 198:    */         } else {
/* 199:355 */           bf.append("" + this.points[i]);
/* 200:    */         }
/* 201:    */       }
/* 202:356 */       return bf.toString();
/* 203:    */     }
/* 204:    */     
/* 205:    */     public String getRevision()
/* 206:    */     {
/* 207:365 */       return RevisionUtils.extract("$Revision: 8034 $");
/* 208:    */     }
/* 209:    */   }
/* 210:    */   
/* 211:    */   public String getRevision()
/* 212:    */   {
/* 213:375 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 214:    */   }
/* 215:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.balltrees.BottomUpConstructor
 * JD-Core Version:    0.7.0.1
 */