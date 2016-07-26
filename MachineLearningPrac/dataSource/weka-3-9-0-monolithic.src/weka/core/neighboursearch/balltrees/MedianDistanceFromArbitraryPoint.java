/*   1:    */ package weka.core.neighboursearch.balltrees;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Random;
/*   6:    */ import java.util.Vector;
/*   7:    */ import weka.core.EuclideanDistance;
/*   8:    */ import weka.core.Instance;
/*   9:    */ import weka.core.Instances;
/*  10:    */ import weka.core.Option;
/*  11:    */ import weka.core.RevisionUtils;
/*  12:    */ import weka.core.TechnicalInformation;
/*  13:    */ import weka.core.TechnicalInformation.Field;
/*  14:    */ import weka.core.TechnicalInformation.Type;
/*  15:    */ import weka.core.TechnicalInformationHandler;
/*  16:    */ import weka.core.Utils;
/*  17:    */ 
/*  18:    */ public class MedianDistanceFromArbitraryPoint
/*  19:    */   extends BallSplitter
/*  20:    */   implements TechnicalInformationHandler
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = 5617378551363700558L;
/*  23: 99 */   protected int m_RandSeed = 17;
/*  24:    */   protected Random m_Rand;
/*  25:    */   
/*  26:    */   public MedianDistanceFromArbitraryPoint() {}
/*  27:    */   
/*  28:    */   public MedianDistanceFromArbitraryPoint(int[] instList, Instances insts, EuclideanDistance e)
/*  29:    */   {
/*  30:119 */     super(instList, insts, e);
/*  31:    */   }
/*  32:    */   
/*  33:    */   public String globalInfo()
/*  34:    */   {
/*  35:129 */     return "Class that splits a BallNode of a ball tree using Uhlmann's described method.\n\nFor information see:\n\n" + getTechnicalInformation().toString();
/*  36:    */   }
/*  37:    */   
/*  38:    */   public TechnicalInformation getTechnicalInformation()
/*  39:    */   {
/*  40:146 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*  41:147 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Jeffrey K. Uhlmann");
/*  42:148 */     result.setValue(TechnicalInformation.Field.TITLE, "Satisfying general proximity/similarity queries with metric trees");
/*  43:    */     
/*  44:150 */     result.setValue(TechnicalInformation.Field.JOURNAL, "Information Processing Letters");
/*  45:151 */     result.setValue(TechnicalInformation.Field.MONTH, "November");
/*  46:152 */     result.setValue(TechnicalInformation.Field.YEAR, "1991");
/*  47:153 */     result.setValue(TechnicalInformation.Field.NUMBER, "4");
/*  48:154 */     result.setValue(TechnicalInformation.Field.VOLUME, "40");
/*  49:155 */     result.setValue(TechnicalInformation.Field.PAGES, "175-179");
/*  50:    */     
/*  51:157 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.MASTERSTHESIS);
/*  52:158 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Ashraf Masood Kibriya");
/*  53:159 */     additional.setValue(TechnicalInformation.Field.TITLE, "Fast Algorithms for Nearest Neighbour Search");
/*  54:    */     
/*  55:161 */     additional.setValue(TechnicalInformation.Field.YEAR, "2007");
/*  56:162 */     additional.setValue(TechnicalInformation.Field.SCHOOL, "Department of Computer Science, School of Computing and Mathematical Sciences, University of Waikato");
/*  57:    */     
/*  58:    */ 
/*  59:    */ 
/*  60:166 */     additional.setValue(TechnicalInformation.Field.ADDRESS, "Hamilton, New Zealand");
/*  61:    */     
/*  62:168 */     return result;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public Enumeration<Option> listOptions()
/*  66:    */   {
/*  67:178 */     Vector<Option> result = new Vector();
/*  68:    */     
/*  69:180 */     result.addElement(new Option("\tThe seed value for the random number generator.\n\t(default: 17)", "S", 1, "-S <num>"));
/*  70:    */     
/*  71:    */ 
/*  72:    */ 
/*  73:    */ 
/*  74:185 */     result.addAll(Collections.list(super.listOptions()));
/*  75:    */     
/*  76:187 */     return result.elements();
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void setOptions(String[] options)
/*  80:    */     throws Exception
/*  81:    */   {
/*  82:210 */     String tmpStr = Utils.getOption('S', options);
/*  83:212 */     if (tmpStr.length() > 0) {
/*  84:213 */       setRandomSeed(Integer.parseInt(tmpStr));
/*  85:    */     } else {
/*  86:215 */       setRandomSeed(17);
/*  87:    */     }
/*  88:218 */     super.setOptions(options);
/*  89:    */   }
/*  90:    */   
/*  91:    */   public String[] getOptions()
/*  92:    */   {
/*  93:229 */     Vector<String> result = new Vector();
/*  94:    */     
/*  95:231 */     result.add("-S");
/*  96:232 */     result.add("" + getRandomSeed());
/*  97:    */     
/*  98:234 */     Collections.addAll(result, super.getOptions());
/*  99:    */     
/* 100:236 */     return (String[])result.toArray(new String[result.size()]);
/* 101:    */   }
/* 102:    */   
/* 103:    */   public void setRandomSeed(int seed)
/* 104:    */   {
/* 105:245 */     this.m_RandSeed = seed;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public int getRandomSeed()
/* 109:    */   {
/* 110:254 */     return this.m_RandSeed;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public String randomSeedTipText()
/* 114:    */   {
/* 115:264 */     return "The seed value for the random number generator.";
/* 116:    */   }
/* 117:    */   
/* 118:    */   public void splitNode(BallNode node, int numNodesCreated)
/* 119:    */     throws Exception
/* 120:    */   {
/* 121:278 */     correctlyInitialized();
/* 122:    */     
/* 123:280 */     this.m_Rand = new Random(this.m_RandSeed);
/* 124:    */     
/* 125:282 */     int ridx = node.m_Start + this.m_Rand.nextInt(node.m_NumInstances);
/* 126:283 */     Instance randomInst = (Instance)this.m_Instances.instance(this.m_Instlist[ridx]).copy();
/* 127:    */     
/* 128:285 */     double[] distList = new double[node.m_NumInstances - 1];
/* 129:    */     
/* 130:287 */     int i = node.m_Start;
/* 131:287 */     for (int j = 0; i < node.m_End; j++)
/* 132:    */     {
/* 133:288 */       Instance temp = this.m_Instances.instance(this.m_Instlist[i]);
/* 134:289 */       distList[j] = this.m_DistanceFunction.distance(randomInst, temp, (1.0D / 0.0D));i++;
/* 135:    */     }
/* 136:293 */     int medianIdx = select(distList, this.m_Instlist, 0, distList.length - 1, node.m_Start, (node.m_End - node.m_Start) / 2 + 1) + node.m_Start;
/* 137:    */     Instance pivot;
/* 138:297 */     node.m_Left = new BallNode(node.m_Start, medianIdx, numNodesCreated + 1, pivot = BallNode.calcCentroidPivot(node.m_Start, medianIdx, this.m_Instlist, this.m_Instances), BallNode.calcRadius(node.m_Start, medianIdx, this.m_Instlist, this.m_Instances, pivot, this.m_DistanceFunction));
/* 139:    */     
/* 140:    */ 
/* 141:    */ 
/* 142:    */ 
/* 143:302 */     node.m_Right = new BallNode(medianIdx + 1, node.m_End, numNodesCreated + 2, pivot = BallNode.calcCentroidPivot(medianIdx + 1, node.m_End, this.m_Instlist, this.m_Instances), BallNode.calcRadius(medianIdx + 1, node.m_End, this.m_Instlist, this.m_Instances, pivot, this.m_DistanceFunction));
/* 144:    */   }
/* 145:    */   
/* 146:    */   protected int partition(double[] array, int[] index, int l, int r, int indexStart)
/* 147:    */   {
/* 148:327 */     double pivot = array[((l + r) / 2)];
/* 149:330 */     while (l < r)
/* 150:    */     {
/* 151:331 */       while ((array[l] < pivot) && (l < r)) {
/* 152:332 */         l++;
/* 153:    */       }
/* 154:334 */       while ((array[r] > pivot) && (l < r)) {
/* 155:335 */         r--;
/* 156:    */       }
/* 157:337 */       if (l < r)
/* 158:    */       {
/* 159:338 */         int help = index[(indexStart + l)];
/* 160:339 */         index[(indexStart + l)] = index[(indexStart + r)];
/* 161:340 */         index[(indexStart + r)] = help;
/* 162:341 */         l++;
/* 163:342 */         r--;
/* 164:    */       }
/* 165:    */     }
/* 166:345 */     if ((l == r) && (array[r] > pivot)) {
/* 167:346 */       r--;
/* 168:    */     }
/* 169:349 */     return r;
/* 170:    */   }
/* 171:    */   
/* 172:    */   protected int select(double[] array, int[] indices, int left, int right, int indexStart, int k)
/* 173:    */   {
/* 174:371 */     if (left == right) {
/* 175:372 */       return left;
/* 176:    */     }
/* 177:374 */     int middle = partition(array, indices, left, right, indexStart);
/* 178:375 */     if (middle - left + 1 >= k) {
/* 179:376 */       return select(array, indices, left, middle, indexStart, k);
/* 180:    */     }
/* 181:378 */     return select(array, indices, middle + 1, right, indexStart, k - (middle - left + 1));
/* 182:    */   }
/* 183:    */   
/* 184:    */   public String getRevision()
/* 185:    */   {
/* 186:391 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 187:    */   }
/* 188:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.balltrees.MedianDistanceFromArbitraryPoint
 * JD-Core Version:    0.7.0.1
 */