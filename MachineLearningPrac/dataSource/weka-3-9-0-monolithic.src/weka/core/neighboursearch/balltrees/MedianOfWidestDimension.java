/*   1:    */ package weka.core.neighboursearch.balltrees;
/*   2:    */ 
/*   3:    */ import java.util.Enumeration;
/*   4:    */ import java.util.Vector;
/*   5:    */ import weka.core.EuclideanDistance;
/*   6:    */ import weka.core.Instance;
/*   7:    */ import weka.core.Instances;
/*   8:    */ import weka.core.Option;
/*   9:    */ import weka.core.OptionHandler;
/*  10:    */ import weka.core.RevisionUtils;
/*  11:    */ import weka.core.TechnicalInformation;
/*  12:    */ import weka.core.TechnicalInformation.Field;
/*  13:    */ import weka.core.TechnicalInformation.Type;
/*  14:    */ import weka.core.TechnicalInformationHandler;
/*  15:    */ import weka.core.Utils;
/*  16:    */ 
/*  17:    */ public class MedianOfWidestDimension
/*  18:    */   extends BallSplitter
/*  19:    */   implements OptionHandler, TechnicalInformationHandler
/*  20:    */ {
/*  21:    */   private static final long serialVersionUID = 3054842574468790421L;
/*  22: 84 */   protected boolean m_NormalizeDimWidths = true;
/*  23:    */   
/*  24:    */   public MedianOfWidestDimension() {}
/*  25:    */   
/*  26:    */   public MedianOfWidestDimension(int[] instList, Instances insts, EuclideanDistance e)
/*  27:    */   {
/*  28:101 */     super(instList, insts, e);
/*  29:    */   }
/*  30:    */   
/*  31:    */   public String globalInfo()
/*  32:    */   {
/*  33:111 */     return "Class that splits a BallNode of a ball tree based on the median value of the widest dimension of the points in the ball. It essentially implements Omohundro's  KD construction algorithm.";
/*  34:    */   }
/*  35:    */   
/*  36:    */   public TechnicalInformation getTechnicalInformation()
/*  37:    */   {
/*  38:127 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.TECHREPORT);
/*  39:128 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Stephen M. Omohundro");
/*  40:129 */     result.setValue(TechnicalInformation.Field.YEAR, "1989");
/*  41:130 */     result.setValue(TechnicalInformation.Field.TITLE, "Five Balltree Construction Algorithms");
/*  42:131 */     result.setValue(TechnicalInformation.Field.MONTH, "December");
/*  43:132 */     result.setValue(TechnicalInformation.Field.NUMBER, "TR-89-063");
/*  44:133 */     result.setValue(TechnicalInformation.Field.INSTITUTION, "International Computer Science Institute");
/*  45:    */     
/*  46:    */ 
/*  47:136 */     return result;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void splitNode(BallNode node, int numNodesCreated)
/*  51:    */     throws Exception
/*  52:    */   {
/*  53:150 */     correctlyInitialized();
/*  54:    */     
/*  55:152 */     double[][] ranges = this.m_DistanceFunction.initializeRanges(this.m_Instlist, node.m_Start, node.m_End);
/*  56:    */     
/*  57:    */ 
/*  58:155 */     int splitAttrib = widestDim(ranges, this.m_DistanceFunction.getRanges());
/*  59:    */     
/*  60:    */ 
/*  61:    */ 
/*  62:    */ 
/*  63:160 */     int medianIdxIdx = node.m_Start + (node.m_End - node.m_Start) / 2;
/*  64:    */     
/*  65:    */ 
/*  66:163 */     int medianIdx = select(splitAttrib, this.m_Instlist, node.m_Start, node.m_End, (node.m_End - node.m_Start) / 2 + 1);
/*  67:    */     
/*  68:    */ 
/*  69:    */ 
/*  70:    */ 
/*  71:    */ 
/*  72:    */ 
/*  73:    */ 
/*  74:    */ 
/*  75:172 */     node.m_SplitAttrib = splitAttrib;
/*  76:173 */     node.m_SplitVal = this.m_Instances.instance(this.m_Instlist[medianIdx]).value(splitAttrib);
/*  77:    */     Instance pivot;
/*  78:176 */     node.m_Left = new BallNode(node.m_Start, medianIdxIdx, numNodesCreated + 1, pivot = BallNode.calcCentroidPivot(node.m_Start, medianIdxIdx, this.m_Instlist, this.m_Instances), BallNode.calcRadius(node.m_Start, medianIdxIdx, this.m_Instlist, this.m_Instances, pivot, this.m_DistanceFunction));
/*  79:    */     
/*  80:    */ 
/*  81:    */ 
/*  82:180 */     node.m_Right = new BallNode(medianIdxIdx + 1, node.m_End, numNodesCreated + 2, pivot = BallNode.calcCentroidPivot(medianIdxIdx + 1, node.m_End, this.m_Instlist, this.m_Instances), BallNode.calcRadius(medianIdxIdx + 1, node.m_End, this.m_Instlist, this.m_Instances, pivot, this.m_DistanceFunction));
/*  83:    */   }
/*  84:    */   
/*  85:    */   protected int partition(int attIdx, int[] index, int l, int r)
/*  86:    */   {
/*  87:203 */     double pivot = this.m_Instances.instance(index[((l + r) / 2)]).value(attIdx);
/*  88:206 */     while (l < r)
/*  89:    */     {
/*  90:207 */       while ((this.m_Instances.instance(index[l]).value(attIdx) < pivot) && (l < r)) {
/*  91:208 */         l++;
/*  92:    */       }
/*  93:210 */       while ((this.m_Instances.instance(index[r]).value(attIdx) > pivot) && (l < r)) {
/*  94:211 */         r--;
/*  95:    */       }
/*  96:213 */       if (l < r)
/*  97:    */       {
/*  98:214 */         int help = index[l];
/*  99:215 */         index[l] = index[r];
/* 100:216 */         index[r] = help;
/* 101:217 */         l++;
/* 102:218 */         r--;
/* 103:    */       }
/* 104:    */     }
/* 105:221 */     if ((l == r) && (this.m_Instances.instance(index[r]).value(attIdx) > pivot)) {
/* 106:222 */       r--;
/* 107:    */     }
/* 108:225 */     return r;
/* 109:    */   }
/* 110:    */   
/* 111:    */   public int select(int attIdx, int[] indices, int left, int right, int k)
/* 112:    */   {
/* 113:244 */     if (left == right) {
/* 114:245 */       return left;
/* 115:    */     }
/* 116:247 */     int middle = partition(attIdx, indices, left, right);
/* 117:248 */     if (middle - left + 1 >= k) {
/* 118:249 */       return select(attIdx, indices, left, middle, k);
/* 119:    */     }
/* 120:251 */     return select(attIdx, indices, middle + 1, right, k - (middle - left + 1));
/* 121:    */   }
/* 122:    */   
/* 123:    */   protected int widestDim(double[][] nodeRanges, double[][] universe)
/* 124:    */   {
/* 125:268 */     int classIdx = this.m_Instances.classIndex();
/* 126:269 */     double widest = 0.0D;
/* 127:270 */     int w = -1;
/* 128:271 */     if (this.m_NormalizeDimWidths) {
/* 129:272 */       for (int i = 0; i < nodeRanges.length; i++)
/* 130:    */       {
/* 131:273 */         double newWidest = nodeRanges[i][2] / universe[i][2];
/* 132:275 */         if ((newWidest > widest) && 
/* 133:276 */           (i != classIdx))
/* 134:    */         {
/* 135:279 */           widest = newWidest;
/* 136:280 */           w = i;
/* 137:    */         }
/* 138:    */       }
/* 139:    */     } else {
/* 140:284 */       for (int i = 0; i < nodeRanges.length; i++) {
/* 141:285 */         if ((nodeRanges[i][2] > widest) && 
/* 142:286 */           (i != classIdx))
/* 143:    */         {
/* 144:289 */           widest = nodeRanges[i][2];
/* 145:290 */           w = i;
/* 146:    */         }
/* 147:    */       }
/* 148:    */     }
/* 149:294 */     return w;
/* 150:    */   }
/* 151:    */   
/* 152:    */   public String normalizeDimWidthsTipText()
/* 153:    */   {
/* 154:304 */     return "Whether to normalize the widths(ranges) of the dimensions (attributes) before selecting the widest one.";
/* 155:    */   }
/* 156:    */   
/* 157:    */   public void setNormalizeDimWidths(boolean normalize)
/* 158:    */   {
/* 159:315 */     this.m_NormalizeDimWidths = normalize;
/* 160:    */   }
/* 161:    */   
/* 162:    */   public boolean getNormalizeDimWidths()
/* 163:    */   {
/* 164:325 */     return this.m_NormalizeDimWidths;
/* 165:    */   }
/* 166:    */   
/* 167:    */   public Enumeration<Option> listOptions()
/* 168:    */   {
/* 169:335 */     Vector<Option> newVector = new Vector();
/* 170:    */     
/* 171:337 */     newVector.addElement(new Option("\tNormalize dimensions' widths.", "N", 0, "-N"));
/* 172:    */     
/* 173:    */ 
/* 174:340 */     return newVector.elements();
/* 175:    */   }
/* 176:    */   
/* 177:    */   public void setOptions(String[] options)
/* 178:    */     throws Exception
/* 179:    */   {
/* 180:362 */     setNormalizeDimWidths(Utils.getFlag('N', options));
/* 181:    */   }
/* 182:    */   
/* 183:    */   public String[] getOptions()
/* 184:    */   {
/* 185:375 */     Vector<String> result = new Vector();
/* 186:377 */     if (getNormalizeDimWidths()) {
/* 187:378 */       result.add("-N");
/* 188:    */     }
/* 189:381 */     return (String[])result.toArray(new String[result.size()]);
/* 190:    */   }
/* 191:    */   
/* 192:    */   public String getRevision()
/* 193:    */   {
/* 194:391 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 195:    */   }
/* 196:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.balltrees.MedianOfWidestDimension
 * JD-Core Version:    0.7.0.1
 */