/*   1:    */ package weka.core.neighboursearch;
/*   2:    */ 
/*   3:    */ import java.util.Collections;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.RevisionUtils;
/*   7:    */ 
/*   8:    */ public class TreePerformanceStats
/*   9:    */   extends PerformanceStats
/*  10:    */ {
/*  11:    */   private static final long serialVersionUID = -6637636693340810373L;
/*  12:    */   protected int m_MinLeaves;
/*  13:    */   protected int m_MaxLeaves;
/*  14:    */   protected int m_SumLeaves;
/*  15:    */   protected int m_SumSqLeaves;
/*  16:    */   protected int m_LeafCount;
/*  17:    */   protected int m_MinIntNodes;
/*  18:    */   protected int m_MaxIntNodes;
/*  19:    */   protected int m_SumIntNodes;
/*  20:    */   protected int m_SumSqIntNodes;
/*  21:    */   protected int m_IntNodeCount;
/*  22:    */   
/*  23:    */   public TreePerformanceStats()
/*  24:    */   {
/*  25: 81 */     reset();
/*  26:    */   }
/*  27:    */   
/*  28:    */   public void reset()
/*  29:    */   {
/*  30: 88 */     super.reset();
/*  31:    */     
/*  32: 90 */     this.m_SumLeaves = (this.m_SumSqLeaves = this.m_LeafCount = 0);
/*  33: 91 */     this.m_MinLeaves = 2147483647;
/*  34: 92 */     this.m_MaxLeaves = -2147483648;
/*  35:    */     
/*  36: 94 */     this.m_SumIntNodes = (this.m_SumSqIntNodes = this.m_IntNodeCount = 0);
/*  37: 95 */     this.m_MinIntNodes = 2147483647;
/*  38: 96 */     this.m_MaxIntNodes = -2147483648;
/*  39:    */   }
/*  40:    */   
/*  41:    */   public void searchStart()
/*  42:    */   {
/*  43:104 */     super.searchStart();
/*  44:105 */     this.m_LeafCount = 0;
/*  45:106 */     this.m_IntNodeCount = 0;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public void searchFinish()
/*  49:    */   {
/*  50:114 */     super.searchFinish();
/*  51:    */     
/*  52:116 */     this.m_SumLeaves += this.m_LeafCount;this.m_SumSqLeaves += this.m_LeafCount * this.m_LeafCount;
/*  53:117 */     if (this.m_LeafCount < this.m_MinLeaves) {
/*  54:117 */       this.m_MinLeaves = this.m_LeafCount;
/*  55:    */     }
/*  56:118 */     if (this.m_LeafCount > this.m_MaxLeaves) {
/*  57:118 */       this.m_MaxLeaves = this.m_LeafCount;
/*  58:    */     }
/*  59:120 */     this.m_SumIntNodes += this.m_IntNodeCount;this.m_SumSqIntNodes += this.m_IntNodeCount * this.m_IntNodeCount;
/*  60:121 */     if (this.m_IntNodeCount < this.m_MinIntNodes) {
/*  61:121 */       this.m_MinIntNodes = this.m_IntNodeCount;
/*  62:    */     }
/*  63:122 */     if (this.m_IntNodeCount > this.m_MaxIntNodes) {
/*  64:122 */       this.m_MaxIntNodes = this.m_IntNodeCount;
/*  65:    */     }
/*  66:    */   }
/*  67:    */   
/*  68:    */   public void incrLeafCount()
/*  69:    */   {
/*  70:129 */     this.m_LeafCount += 1;
/*  71:    */   }
/*  72:    */   
/*  73:    */   public void incrIntNodeCount()
/*  74:    */   {
/*  75:136 */     this.m_IntNodeCount += 1;
/*  76:    */   }
/*  77:    */   
/*  78:    */   public int getTotalLeavesVisited()
/*  79:    */   {
/*  80:147 */     return this.m_SumLeaves;
/*  81:    */   }
/*  82:    */   
/*  83:    */   public double getMeanLeavesVisited()
/*  84:    */   {
/*  85:156 */     return this.m_SumLeaves / this.m_NumQueries;
/*  86:    */   }
/*  87:    */   
/*  88:    */   public double getStdDevLeavesVisited()
/*  89:    */   {
/*  90:165 */     return Math.sqrt((this.m_SumSqLeaves - this.m_SumLeaves * this.m_SumLeaves / this.m_NumQueries) / (this.m_NumQueries - 1));
/*  91:    */   }
/*  92:    */   
/*  93:    */   public int getMinLeavesVisited()
/*  94:    */   {
/*  95:174 */     return this.m_MinLeaves;
/*  96:    */   }
/*  97:    */   
/*  98:    */   public int getMaxLeavesVisited()
/*  99:    */   {
/* 100:183 */     return this.m_MaxLeaves;
/* 101:    */   }
/* 102:    */   
/* 103:    */   public int getTotalIntNodesVisited()
/* 104:    */   {
/* 105:194 */     return this.m_SumIntNodes;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public double getMeanIntNodesVisited()
/* 109:    */   {
/* 110:204 */     return this.m_SumIntNodes / this.m_NumQueries;
/* 111:    */   }
/* 112:    */   
/* 113:    */   public double getStdDevIntNodesVisited()
/* 114:    */   {
/* 115:213 */     return Math.sqrt((this.m_SumSqIntNodes - this.m_SumIntNodes * this.m_SumIntNodes / this.m_NumQueries) / (this.m_NumQueries - 1));
/* 116:    */   }
/* 117:    */   
/* 118:    */   public int getMinIntNodesVisited()
/* 119:    */   {
/* 120:222 */     return this.m_MinIntNodes;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public int getMaxIntNodesVisited()
/* 124:    */   {
/* 125:231 */     return this.m_MaxIntNodes;
/* 126:    */   }
/* 127:    */   
/* 128:    */   public Enumeration<String> enumerateMeasures()
/* 129:    */   {
/* 130:240 */     Vector<String> newVector = new Vector();
/* 131:    */     
/* 132:242 */     newVector.addAll(Collections.list(super.enumerateMeasures()));
/* 133:    */     
/* 134:244 */     newVector.addElement("measureTotal_nodes_visited");
/* 135:245 */     newVector.addElement("measureMean_nodes_visited");
/* 136:246 */     newVector.addElement("measureStdDev_nodes_visited");
/* 137:247 */     newVector.addElement("measureMin_nodes_visited");
/* 138:248 */     newVector.addElement("measureMax_nodes_visited");
/* 139:    */     
/* 140:250 */     newVector.addElement("measureTotal_leaves_visited");
/* 141:251 */     newVector.addElement("measureMean_leaves_visited");
/* 142:252 */     newVector.addElement("measureStdDev_leaves_visited");
/* 143:253 */     newVector.addElement("measureMin_leaves_visited");
/* 144:254 */     newVector.addElement("measureMax_leaves_visited");
/* 145:    */     
/* 146:256 */     return newVector.elements();
/* 147:    */   }
/* 148:    */   
/* 149:    */   public double getMeasure(String additionalMeasureName)
/* 150:    */     throws IllegalArgumentException
/* 151:    */   {
/* 152:269 */     if (additionalMeasureName.compareToIgnoreCase("measureTotal_nodes_visited") == 0) {
/* 153:270 */       return getTotalIntNodesVisited();
/* 154:    */     }
/* 155:271 */     if (additionalMeasureName.compareToIgnoreCase("measureMean_nodes_visited") == 0) {
/* 156:272 */       return getMeanIntNodesVisited();
/* 157:    */     }
/* 158:273 */     if (additionalMeasureName.compareToIgnoreCase("measureStdDev_nodes_visited") == 0) {
/* 159:274 */       return getStdDevIntNodesVisited();
/* 160:    */     }
/* 161:275 */     if (additionalMeasureName.compareToIgnoreCase("measureMin_nodes_visited") == 0) {
/* 162:276 */       return getMinIntNodesVisited();
/* 163:    */     }
/* 164:277 */     if (additionalMeasureName.compareToIgnoreCase("measureMax_nodes_visited") == 0) {
/* 165:278 */       return getMaxIntNodesVisited();
/* 166:    */     }
/* 167:281 */     if (additionalMeasureName.compareToIgnoreCase("measureTotal_leaves_visited") == 0) {
/* 168:282 */       return getTotalLeavesVisited();
/* 169:    */     }
/* 170:283 */     if (additionalMeasureName.compareToIgnoreCase("measureMean_leaves_visited") == 0) {
/* 171:284 */       return getMeanLeavesVisited();
/* 172:    */     }
/* 173:285 */     if (additionalMeasureName.compareToIgnoreCase("measureStdDev_leaves_visited") == 0) {
/* 174:286 */       return getStdDevLeavesVisited();
/* 175:    */     }
/* 176:287 */     if (additionalMeasureName.compareToIgnoreCase("measureMin_leaves_visited") == 0) {
/* 177:288 */       return getMinLeavesVisited();
/* 178:    */     }
/* 179:289 */     if (additionalMeasureName.compareToIgnoreCase("measureMax_leaves_visited") == 0) {
/* 180:290 */       return getMaxLeavesVisited();
/* 181:    */     }
/* 182:292 */     return super.getMeasure(additionalMeasureName);
/* 183:    */   }
/* 184:    */   
/* 185:    */   public String getStats()
/* 186:    */   {
/* 187:302 */     StringBuffer buf = new StringBuffer(super.getStats());
/* 188:    */     
/* 189:304 */     buf.append("leaves:    " + getMinLeavesVisited() + ", " + getMaxLeavesVisited() + "," + getTotalLeavesVisited() + "," + getMeanLeavesVisited() + ", " + getStdDevLeavesVisited() + "\n");
/* 190:    */     
/* 191:306 */     buf.append("Int nodes: " + getMinIntNodesVisited() + ", " + getMaxIntNodesVisited() + "," + getTotalIntNodesVisited() + "," + getMeanIntNodesVisited() + ", " + getStdDevIntNodesVisited() + "\n");
/* 192:    */     
/* 193:    */ 
/* 194:309 */     return buf.toString();
/* 195:    */   }
/* 196:    */   
/* 197:    */   public String getRevision()
/* 198:    */   {
/* 199:318 */     return RevisionUtils.extract("$Revision: 10141 $");
/* 200:    */   }
/* 201:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.TreePerformanceStats
 * JD-Core Version:    0.7.0.1
 */