/*   1:    */ package weka.core.neighboursearch.balltrees;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.DistanceFunction;
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
/*  18:    */ public class TopDownConstructor
/*  19:    */   extends BallTreeConstructor
/*  20:    */   implements TechnicalInformationHandler
/*  21:    */ {
/*  22:    */   private static final long serialVersionUID = -5150140645091889979L;
/*  23: 86 */   protected BallSplitter m_Splitter = new PointsClosestToFurthestChildren();
/*  24:    */   
/*  25:    */   public String globalInfo()
/*  26:    */   {
/*  27:101 */     return "The class implementing the TopDown construction method of ball trees. It further uses one of a number of different splitting methods to split a ball while constructing the tree top down.\n\nFor more information see also:\n\n" + getTechnicalInformation().toString();
/*  28:    */   }
/*  29:    */   
/*  30:    */   public TechnicalInformation getTechnicalInformation()
/*  31:    */   {
/*  32:119 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.TECHREPORT);
/*  33:120 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Stephen M. Omohundro");
/*  34:121 */     result.setValue(TechnicalInformation.Field.YEAR, "1989");
/*  35:122 */     result.setValue(TechnicalInformation.Field.TITLE, "Five Balltree Construction Algorithms");
/*  36:123 */     result.setValue(TechnicalInformation.Field.MONTH, "December");
/*  37:124 */     result.setValue(TechnicalInformation.Field.NUMBER, "TR-89-063");
/*  38:125 */     result.setValue(TechnicalInformation.Field.INSTITUTION, "International Computer Science Institute");
/*  39:    */     
/*  40:    */ 
/*  41:128 */     return result;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public BallNode buildTree()
/*  45:    */     throws Exception
/*  46:    */   {
/*  47:141 */     this.m_NumNodes = (this.m_MaxDepth = 0);
/*  48:142 */     this.m_NumLeaves = 1;
/*  49:    */     
/*  50:144 */     this.m_Splitter.setInstances(this.m_Instances);
/*  51:145 */     this.m_Splitter.setInstanceList(this.m_InstList);
/*  52:146 */     this.m_Splitter.setEuclideanDistanceFunction((EuclideanDistance)this.m_DistanceFunction);
/*  53:    */     
/*  54:    */ 
/*  55:149 */     BallNode root = new BallNode(0, this.m_InstList.length - 1, 0);
/*  56:150 */     root.setPivot(BallNode.calcCentroidPivot(this.m_InstList, this.m_Instances));
/*  57:151 */     root.setRadius(BallNode.calcRadius(this.m_InstList, this.m_Instances, root.getPivot(), this.m_DistanceFunction));
/*  58:    */     
/*  59:    */ 
/*  60:154 */     splitNodes(root, this.m_MaxDepth + 1, root.m_Radius);
/*  61:    */     
/*  62:156 */     return root;
/*  63:    */   }
/*  64:    */   
/*  65:    */   protected void splitNodes(BallNode node, int depth, double rootRadius)
/*  66:    */     throws Exception
/*  67:    */   {
/*  68:172 */     if ((node.m_NumInstances <= this.m_MaxInstancesInLeaf) || (rootRadius == 0.0D) || (node.m_Radius / rootRadius < this.m_MaxRelLeafRadius)) {
/*  69:175 */       return;
/*  70:    */     }
/*  71:178 */     this.m_NumLeaves -= 1;
/*  72:179 */     this.m_Splitter.splitNode(node, this.m_NumNodes);
/*  73:180 */     this.m_NumNodes += 2;
/*  74:181 */     this.m_NumLeaves += 2;
/*  75:183 */     if (this.m_MaxDepth < depth) {
/*  76:184 */       this.m_MaxDepth = depth;
/*  77:    */     }
/*  78:187 */     splitNodes(node.m_Left, depth + 1, rootRadius);
/*  79:188 */     splitNodes(node.m_Right, depth + 1, rootRadius);
/*  80:190 */     if (this.m_FullyContainChildBalls)
/*  81:    */     {
/*  82:191 */       double radius = BallNode.calcRadius(node.m_Left, node.m_Right, node.getPivot(), this.m_DistanceFunction);
/*  83:    */       
/*  84:    */ 
/*  85:    */ 
/*  86:    */ 
/*  87:    */ 
/*  88:    */ 
/*  89:    */ 
/*  90:    */ 
/*  91:    */ 
/*  92:    */ 
/*  93:    */ 
/*  94:    */ 
/*  95:    */ 
/*  96:    */ 
/*  97:206 */       node.setRadius(radius);
/*  98:    */     }
/*  99:    */   }
/* 100:    */   
/* 101:    */   public int[] addInstance(BallNode node, Instance inst)
/* 102:    */     throws Exception
/* 103:    */   {
/* 104:224 */     if ((node.m_Left != null) && (node.m_Right != null))
/* 105:    */     {
/* 106:227 */       double leftDist = this.m_DistanceFunction.distance(inst, node.m_Left.getPivot(), (1.0D / 0.0D));
/* 107:    */       
/* 108:229 */       double rightDist = this.m_DistanceFunction.distance(inst, node.m_Right.getPivot(), (1.0D / 0.0D));
/* 109:231 */       if (leftDist < rightDist)
/* 110:    */       {
/* 111:232 */         addInstance(node.m_Left, inst);
/* 112:    */         
/* 113:234 */         processNodesAfterAddInstance(node.m_Right);
/* 114:    */       }
/* 115:    */       else
/* 116:    */       {
/* 117:236 */         addInstance(node.m_Right, inst);
/* 118:    */       }
/* 119:239 */       node.m_End += 1;
/* 120:    */     }
/* 121:    */     else
/* 122:    */     {
/* 123:240 */       if ((node.m_Left != null) || (node.m_Right != null)) {
/* 124:241 */         throw new Exception("Error: Only one leaf of the built ball tree is assigned. Please check code.");
/* 125:    */       }
/* 126:245 */       int index = this.m_Instances.numInstances() - 1;
/* 127:    */       
/* 128:247 */       int[] instList = new int[this.m_Instances.numInstances()];
/* 129:248 */       System.arraycopy(this.m_InstList, 0, instList, 0, node.m_End + 1);
/* 130:249 */       if (node.m_End < this.m_InstList.length - 1) {
/* 131:250 */         System.arraycopy(this.m_InstList, node.m_End + 2, instList, node.m_End + 2, this.m_InstList.length - node.m_End - 1);
/* 132:    */       }
/* 133:253 */       instList[(node.m_End + 1)] = index;
/* 134:254 */       node.m_End += 1;
/* 135:255 */       node.m_NumInstances += 1;
/* 136:256 */       this.m_InstList = instList;
/* 137:    */       
/* 138:258 */       this.m_Splitter.setInstanceList(this.m_InstList);
/* 139:260 */       if (node.m_NumInstances > this.m_MaxInstancesInLeaf)
/* 140:    */       {
/* 141:261 */         this.m_Splitter.splitNode(node, this.m_NumNodes);
/* 142:262 */         this.m_NumNodes += 2;
/* 143:    */       }
/* 144:    */     }
/* 145:265 */     return this.m_InstList;
/* 146:    */   }
/* 147:    */   
/* 148:    */   protected void processNodesAfterAddInstance(BallNode node)
/* 149:    */   {
/* 150:276 */     node.m_Start += 1;
/* 151:277 */     node.m_End += 1;
/* 152:279 */     if ((node.m_Left != null) && (node.m_Right != null))
/* 153:    */     {
/* 154:280 */       processNodesAfterAddInstance(node.m_Left);
/* 155:281 */       processNodesAfterAddInstance(node.m_Right);
/* 156:    */     }
/* 157:    */   }
/* 158:    */   
/* 159:    */   public String ballSplitterTipText()
/* 160:    */   {
/* 161:292 */     return "The BallSplitter algorithm set that would be used by the TopDown BallTree constructor.";
/* 162:    */   }
/* 163:    */   
/* 164:    */   public BallSplitter getBallSplitter()
/* 165:    */   {
/* 166:303 */     return this.m_Splitter;
/* 167:    */   }
/* 168:    */   
/* 169:    */   public void setBallSplitter(BallSplitter splitter)
/* 170:    */   {
/* 171:312 */     this.m_Splitter = splitter;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public Enumeration<Option> listOptions()
/* 175:    */   {
/* 176:322 */     Vector<Option> newVector = new Vector();
/* 177:    */     
/* 178:324 */     newVector.addElement(new Option("\tBall splitting algorithm to use.", "S", 1, "-S <classname and options>"));
/* 179:    */     
/* 180:    */ 
/* 181:327 */     newVector.addAll(Collections.list(super.listOptions()));
/* 182:    */     
/* 183:329 */     return newVector.elements();
/* 184:    */   }
/* 185:    */   
/* 186:    */   public void setOptions(String[] options)
/* 187:    */     throws Exception
/* 188:    */   {
/* 189:351 */     String optionString = Utils.getOption('S', options);
/* 190:352 */     if (optionString.length() != 0)
/* 191:    */     {
/* 192:353 */       String[] nnSearchClassSpec = Utils.splitOptions(optionString);
/* 193:354 */       if (nnSearchClassSpec.length == 0) {
/* 194:355 */         throw new Exception("Invalid BallSplitter specification string.");
/* 195:    */       }
/* 196:357 */       String className = nnSearchClassSpec[0];
/* 197:358 */       nnSearchClassSpec[0] = "";
/* 198:    */       
/* 199:360 */       setBallSplitter((BallSplitter)Utils.forName(BallSplitter.class, className, nnSearchClassSpec));
/* 200:    */     }
/* 201:    */     else
/* 202:    */     {
/* 203:363 */       setBallSplitter(new PointsClosestToFurthestChildren());
/* 204:    */     }
/* 205:366 */     super.setOptions(options);
/* 206:    */   }
/* 207:    */   
/* 208:    */   public String[] getOptions()
/* 209:    */   {
/* 210:377 */     Vector<String> result = new Vector();
/* 211:    */     
/* 212:379 */     result.add("-S");
/* 213:380 */     result.add(this.m_Splitter.getClass().getName());
/* 214:    */     
/* 215:382 */     Collections.addAll(result, super.getOptions());
/* 216:    */     
/* 217:384 */     return (String[])result.toArray(new String[result.size()]);
/* 218:    */   }
/* 219:    */   
/* 220:    */   public String getRevision()
/* 221:    */   {
/* 222:394 */     return RevisionUtils.extract("$Revision: 10203 $");
/* 223:    */   }
/* 224:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.balltrees.TopDownConstructor
 * JD-Core Version:    0.7.0.1
 */