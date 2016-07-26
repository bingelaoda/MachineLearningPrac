/*   1:    */ package weka.core.neighboursearch.kdtrees;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import weka.core.EuclideanDistance;
/*   5:    */ import weka.core.Instance;
/*   6:    */ import weka.core.Instances;
/*   7:    */ import weka.core.RevisionUtils;
/*   8:    */ import weka.core.TechnicalInformation;
/*   9:    */ import weka.core.TechnicalInformation.Field;
/*  10:    */ import weka.core.TechnicalInformation.Type;
/*  11:    */ import weka.core.TechnicalInformationHandler;
/*  12:    */ 
/*  13:    */ public class KMeansInpiredMethod
/*  14:    */   extends KDTreeNodeSplitter
/*  15:    */   implements TechnicalInformationHandler
/*  16:    */ {
/*  17:    */   private static final long serialVersionUID = -866783749124714304L;
/*  18:    */   
/*  19:    */   public String globalInfo()
/*  20:    */   {
/*  21: 75 */     return "The class that splits a node into two such that the overall sum of squared distances of points to their centres on both sides of the (axis-parallel) splitting plane is minimum.\n\nFor more information see also:\n\n" + getTechnicalInformation().toString();
/*  22:    */   }
/*  23:    */   
/*  24:    */   public TechnicalInformation getTechnicalInformation()
/*  25:    */   {
/*  26: 93 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.MASTERSTHESIS);
/*  27: 94 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Ashraf Masood Kibriya");
/*  28: 95 */     result.setValue(TechnicalInformation.Field.TITLE, "Fast Algorithms for Nearest Neighbour Search");
/*  29:    */     
/*  30: 97 */     result.setValue(TechnicalInformation.Field.YEAR, "2007");
/*  31: 98 */     result.setValue(TechnicalInformation.Field.SCHOOL, "Department of Computer Science, School of Computing and Mathematical Sciences, University of Waikato");
/*  32:    */     
/*  33:    */ 
/*  34:    */ 
/*  35:102 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Hamilton, New Zealand");
/*  36:    */     
/*  37:104 */     return result;
/*  38:    */   }
/*  39:    */   
/*  40:    */   public void splitNode(KDTreeNode node, int numNodesCreated, double[][] nodeRanges, double[][] universe)
/*  41:    */     throws Exception
/*  42:    */   {
/*  43:126 */     correctlyInitialized();
/*  44:    */     
/*  45:128 */     int splitDim = -1;
/*  46:129 */     double splitVal = (-1.0D / 0.0D);
/*  47:    */     
/*  48:131 */     double[] leftAttSum = new double[this.m_Instances.numAttributes()];double[] rightAttSum = new double[this.m_Instances.numAttributes()];
/*  49:132 */     double[] leftAttSqSum = new double[this.m_Instances.numAttributes()];
/*  50:133 */     double[] rightAttSqSum = new double[this.m_Instances.numAttributes()];
/*  51:134 */     double minSum = (1.0D / 0.0D);
/*  52:136 */     for (int dim = 0; dim < this.m_Instances.numAttributes(); dim++) {
/*  53:139 */       if ((node.m_NodeRanges[dim][2] != 0.0D) && (dim != this.m_Instances.classIndex()))
/*  54:    */       {
/*  55:144 */         quickSort(this.m_Instances, this.m_InstList, dim, node.m_Start, node.m_End);
/*  56:146 */         for (int i = node.m_Start; i <= node.m_End; tmp288_287++) {
/*  57:147 */           for (int j = 0; j < this.m_Instances.numAttributes(); j++) {
/*  58:148 */             if (j != this.m_Instances.classIndex())
/*  59:    */             {
/*  60:151 */               double val = this.m_Instances.instance(this.m_InstList[i]).value(j);
/*  61:152 */               if (this.m_NormalizeNodeWidth) {
/*  62:153 */                 if ((Double.isNaN(universe[j][0])) || (universe[j][0] == universe[j][1])) {
/*  63:155 */                   val = 0.0D;
/*  64:    */                 } else {
/*  65:157 */                   val = (val - universe[j][0]) / universe[j][2];
/*  66:    */                 }
/*  67:    */               }
/*  68:161 */               if (i == node.m_Start)
/*  69:    */               {
/*  70:162 */                 double tmp288_287 = (leftAttSqSum[j] = rightAttSqSum[j] = 0.0D);rightAttSum[j] = tmp288_287;leftAttSum[j] = tmp288_287;
/*  71:    */               }
/*  72:164 */               rightAttSum[j] += val;
/*  73:165 */               rightAttSqSum[j] += val * val;
/*  74:    */             }
/*  75:    */           }
/*  76:    */         }
/*  77:169 */         for (int i = node.m_Start; i <= node.m_End - 1; i++)
/*  78:    */         {
/*  79:170 */           Instance inst = this.m_Instances.instance(this.m_InstList[i]);
/*  80:    */           double rightSqSum;
/*  81:171 */           double leftSqSum = rightSqSum = 0.0D;
/*  82:172 */           for (int j = 0; j < this.m_Instances.numAttributes(); j++) {
/*  83:173 */             if (j != this.m_Instances.classIndex())
/*  84:    */             {
/*  85:176 */               double val = inst.value(j);
/*  86:178 */               if (this.m_NormalizeNodeWidth) {
/*  87:179 */                 if ((Double.isNaN(universe[j][0])) || (universe[j][0] == universe[j][1])) {
/*  88:181 */                   val = 0.0D;
/*  89:    */                 } else {
/*  90:183 */                   val = (val - universe[j][0]) / universe[j][2];
/*  91:    */                 }
/*  92:    */               }
/*  93:188 */               leftAttSum[j] += val;
/*  94:189 */               rightAttSum[j] -= val;
/*  95:190 */               leftAttSqSum[j] += val * val;
/*  96:191 */               rightAttSqSum[j] -= val * val;
/*  97:192 */               double leftSqMean = leftAttSum[j] / (i - node.m_Start + 1);
/*  98:193 */               leftSqMean *= leftSqMean;
/*  99:194 */               double rightSqMean = rightAttSum[j] / (node.m_End - i);
/* 100:195 */               rightSqMean *= rightSqMean;
/* 101:    */               
/* 102:197 */               leftSqSum += leftAttSqSum[j] - (i - node.m_Start + 1) * leftSqMean;
/* 103:198 */               rightSqSum += rightAttSqSum[j] - (node.m_End - i) * rightSqMean;
/* 104:    */             }
/* 105:    */           }
/* 106:201 */           if (minSum > leftSqSum + rightSqSum)
/* 107:    */           {
/* 108:202 */             minSum = leftSqSum + rightSqSum;
/* 109:204 */             if (i < node.m_End) {
/* 110:205 */               splitVal = (this.m_Instances.instance(this.m_InstList[i]).value(dim) + this.m_Instances.instance(this.m_InstList[(i + 1)]).value(dim)) / 2.0D;
/* 111:    */             } else {
/* 112:208 */               splitVal = this.m_Instances.instance(this.m_InstList[i]).value(dim);
/* 113:    */             }
/* 114:211 */             splitDim = dim;
/* 115:    */           }
/* 116:    */         }
/* 117:    */       }
/* 118:    */     }
/* 119:216 */     int rightStart = rearrangePoints(this.m_InstList, node.m_Start, node.m_End, splitDim, splitVal);
/* 120:219 */     if ((rightStart == node.m_Start) || (rightStart > node.m_End))
/* 121:    */     {
/* 122:220 */       System.out.println("node.m_Start: " + node.m_Start + " node.m_End: " + node.m_End + " splitDim: " + splitDim + " splitVal: " + splitVal + " node.min: " + node.m_NodeRanges[splitDim][0] + " node.max: " + node.m_NodeRanges[splitDim][1] + " node.numInstances: " + node.numInstances());
/* 123:226 */       if (rightStart == node.m_Start) {
/* 124:227 */         throw new Exception("Left child is empty in node " + node.m_NodeNumber + ". Not possible with " + "KMeanInspiredMethod splitting method. Please " + "check code.");
/* 125:    */       }
/* 126:231 */       throw new Exception("Right child is empty in node " + node.m_NodeNumber + ". Not possible with " + "KMeansInspiredMethod splitting method. Please " + "check code.");
/* 127:    */     }
/* 128:237 */     node.m_SplitDim = splitDim;
/* 129:238 */     node.m_SplitValue = splitVal;
/* 130:239 */     node.m_Left = new KDTreeNode(numNodesCreated + 1, node.m_Start, rightStart - 1, this.m_EuclideanDistance.initializeRanges(this.m_InstList, node.m_Start, rightStart - 1));
/* 131:    */     
/* 132:    */ 
/* 133:242 */     node.m_Right = new KDTreeNode(numNodesCreated + 2, rightStart, node.m_End, this.m_EuclideanDistance.initializeRanges(this.m_InstList, rightStart, node.m_End));
/* 134:    */   }
/* 135:    */   
/* 136:    */   protected static int partition(Instances insts, int[] index, int attidx, int l, int r)
/* 137:    */   {
/* 138:263 */     double pivot = insts.instance(index[((l + r) / 2)]).value(attidx);
/* 139:266 */     while (l < r)
/* 140:    */     {
/* 141:267 */       while ((insts.instance(index[l]).value(attidx) < pivot) && (l < r)) {
/* 142:268 */         l++;
/* 143:    */       }
/* 144:270 */       while ((insts.instance(index[r]).value(attidx) > pivot) && (l < r)) {
/* 145:271 */         r--;
/* 146:    */       }
/* 147:273 */       if (l < r)
/* 148:    */       {
/* 149:274 */         int help = index[l];
/* 150:275 */         index[l] = index[r];
/* 151:276 */         index[r] = help;
/* 152:277 */         l++;
/* 153:278 */         r--;
/* 154:    */       }
/* 155:    */     }
/* 156:281 */     if ((l == r) && (insts.instance(index[r]).value(attidx) > pivot)) {
/* 157:282 */       r--;
/* 158:    */     }
/* 159:285 */     return r;
/* 160:    */   }
/* 161:    */   
/* 162:    */   protected static void quickSort(Instances insts, int[] indices, int attidx, int left, int right)
/* 163:    */   {
/* 164:304 */     if (left < right)
/* 165:    */     {
/* 166:305 */       int middle = partition(insts, indices, attidx, left, right);
/* 167:306 */       quickSort(insts, indices, attidx, left, middle);
/* 168:307 */       quickSort(insts, indices, attidx, middle + 1, right);
/* 169:    */     }
/* 170:    */   }
/* 171:    */   
/* 172:    */   protected int rearrangePoints(int[] indices, int startidx, int endidx, int splitDim, double splitVal)
/* 173:    */   {
/* 174:328 */     int left = startidx - 1;
/* 175:329 */     for (int i = startidx; i <= endidx; i++) {
/* 176:330 */       if (this.m_EuclideanDistance.valueIsSmallerEqual(this.m_Instances.instance(indices[i]), splitDim, splitVal))
/* 177:    */       {
/* 178:332 */         left++;
/* 179:333 */         int tmp = indices[left];
/* 180:334 */         indices[left] = indices[i];
/* 181:335 */         indices[i] = tmp;
/* 182:    */       }
/* 183:    */     }
/* 184:338 */     return left + 1;
/* 185:    */   }
/* 186:    */   
/* 187:    */   public String getRevision()
/* 188:    */   {
/* 189:348 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 190:    */   }
/* 191:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.kdtrees.KMeansInpiredMethod
 * JD-Core Version:    0.7.0.1
 */