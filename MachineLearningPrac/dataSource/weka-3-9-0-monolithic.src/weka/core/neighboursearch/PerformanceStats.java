/*   1:    */ package weka.core.neighboursearch;
/*   2:    */ 
/*   3:    */ import java.io.Serializable;
/*   4:    */ import java.util.Enumeration;
/*   5:    */ import java.util.Vector;
/*   6:    */ import weka.core.AdditionalMeasureProducer;
/*   7:    */ import weka.core.RevisionHandler;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ 
/*  10:    */ public class PerformanceStats
/*  11:    */   implements AdditionalMeasureProducer, Serializable, RevisionHandler
/*  12:    */ {
/*  13:    */   private static final long serialVersionUID = -7215110351388368092L;
/*  14:    */   protected int m_NumQueries;
/*  15:    */   public double m_MinP;
/*  16:    */   public double m_MaxP;
/*  17:    */   public double m_SumP;
/*  18:    */   public double m_SumSqP;
/*  19:    */   public double m_PointCount;
/*  20:    */   public double m_MinC;
/*  21:    */   public double m_MaxC;
/*  22:    */   public double m_SumC;
/*  23:    */   public double m_SumSqC;
/*  24:    */   public double m_CoordCount;
/*  25:    */   
/*  26:    */   public PerformanceStats()
/*  27:    */   {
/*  28: 88 */     reset();
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void reset()
/*  32:    */   {
/*  33: 95 */     this.m_NumQueries = 0;
/*  34:    */     
/*  35: 97 */     this.m_SumP = (this.m_SumSqP = this.m_PointCount = 0.0D);
/*  36: 98 */     this.m_MinP = 2147483647.0D;
/*  37: 99 */     this.m_MaxP = -2147483648.0D;
/*  38:    */     
/*  39:101 */     this.m_SumC = (this.m_SumSqC = this.m_CoordCount = 0.0D);
/*  40:102 */     this.m_MinC = 2147483647.0D;
/*  41:103 */     this.m_MaxC = -2147483648.0D;
/*  42:    */   }
/*  43:    */   
/*  44:    */   public void searchStart()
/*  45:    */   {
/*  46:111 */     this.m_PointCount = 0.0D;
/*  47:112 */     this.m_CoordCount = 0.0D;
/*  48:    */   }
/*  49:    */   
/*  50:    */   public void searchFinish()
/*  51:    */   {
/*  52:120 */     this.m_NumQueries += 1;this.m_SumP += this.m_PointCount;this.m_SumSqP += this.m_PointCount * this.m_PointCount;
/*  53:121 */     if (this.m_PointCount < this.m_MinP) {
/*  54:121 */       this.m_MinP = this.m_PointCount;
/*  55:    */     }
/*  56:122 */     if (this.m_PointCount > this.m_MaxP) {
/*  57:122 */       this.m_MaxP = this.m_PointCount;
/*  58:    */     }
/*  59:124 */     double coordsPerPt = this.m_CoordCount / this.m_PointCount;
/*  60:125 */     this.m_SumC += coordsPerPt;this.m_SumSqC += coordsPerPt * coordsPerPt;
/*  61:126 */     if (coordsPerPt < this.m_MinC) {
/*  62:126 */       this.m_MinC = coordsPerPt;
/*  63:    */     }
/*  64:127 */     if (coordsPerPt > this.m_MaxC) {
/*  65:127 */       this.m_MaxC = coordsPerPt;
/*  66:    */     }
/*  67:    */   }
/*  68:    */   
/*  69:    */   public void incrPointCount()
/*  70:    */   {
/*  71:135 */     this.m_PointCount += 1.0D;
/*  72:    */   }
/*  73:    */   
/*  74:    */   public void incrCoordCount()
/*  75:    */   {
/*  76:144 */     this.m_CoordCount += 1.0D;
/*  77:    */   }
/*  78:    */   
/*  79:    */   public void updatePointCount(int n)
/*  80:    */   {
/*  81:153 */     this.m_PointCount += n;
/*  82:    */   }
/*  83:    */   
/*  84:    */   public int getNumQueries()
/*  85:    */   {
/*  86:162 */     return this.m_NumQueries;
/*  87:    */   }
/*  88:    */   
/*  89:    */   public double getTotalPointsVisited()
/*  90:    */   {
/*  91:171 */     return this.m_SumP;
/*  92:    */   }
/*  93:    */   
/*  94:    */   public double getMeanPointsVisited()
/*  95:    */   {
/*  96:180 */     return this.m_SumP / this.m_NumQueries;
/*  97:    */   }
/*  98:    */   
/*  99:    */   public double getStdDevPointsVisited()
/* 100:    */   {
/* 101:189 */     return Math.sqrt((this.m_SumSqP - this.m_SumP * this.m_SumP / this.m_NumQueries) / (this.m_NumQueries - 1));
/* 102:    */   }
/* 103:    */   
/* 104:    */   public double getMinPointsVisited()
/* 105:    */   {
/* 106:198 */     return this.m_MinP;
/* 107:    */   }
/* 108:    */   
/* 109:    */   public double getMaxPointsVisited()
/* 110:    */   {
/* 111:207 */     return this.m_MaxP;
/* 112:    */   }
/* 113:    */   
/* 114:    */   public double getTotalCoordsPerPoint()
/* 115:    */   {
/* 116:218 */     return this.m_SumC;
/* 117:    */   }
/* 118:    */   
/* 119:    */   public double getMeanCoordsPerPoint()
/* 120:    */   {
/* 121:227 */     return this.m_SumC / this.m_NumQueries;
/* 122:    */   }
/* 123:    */   
/* 124:    */   public double getStdDevCoordsPerPoint()
/* 125:    */   {
/* 126:236 */     return Math.sqrt((this.m_SumSqC - this.m_SumC * this.m_SumC / this.m_NumQueries) / (this.m_NumQueries - 1));
/* 127:    */   }
/* 128:    */   
/* 129:    */   public double getMinCoordsPerPoint()
/* 130:    */   {
/* 131:245 */     return this.m_MinC;
/* 132:    */   }
/* 133:    */   
/* 134:    */   public double getMaxCoordsPerPoint()
/* 135:    */   {
/* 136:254 */     return this.m_MaxC;
/* 137:    */   }
/* 138:    */   
/* 139:    */   public Enumeration<String> enumerateMeasures()
/* 140:    */   {
/* 141:265 */     Vector<String> newVector = new Vector();
/* 142:    */     
/* 143:267 */     newVector.addElement("measureTotal_points_visited");
/* 144:268 */     newVector.addElement("measureMean_points_visited");
/* 145:269 */     newVector.addElement("measureStdDev_points_visited");
/* 146:270 */     newVector.addElement("measureMin_points_visited");
/* 147:271 */     newVector.addElement("measureMax_points_visited");
/* 148:    */     
/* 149:273 */     newVector.addElement("measureTotalCoordsPerPoint");
/* 150:274 */     newVector.addElement("measureMeanCoordsPerPoint");
/* 151:275 */     newVector.addElement("measureStdDevCoordsPerPoint");
/* 152:276 */     newVector.addElement("measureMinCoordsPerPoint");
/* 153:277 */     newVector.addElement("measureMaxCoordsPerPoint");
/* 154:    */     
/* 155:279 */     return newVector.elements();
/* 156:    */   }
/* 157:    */   
/* 158:    */   public double getMeasure(String additionalMeasureName)
/* 159:    */     throws IllegalArgumentException
/* 160:    */   {
/* 161:292 */     if (additionalMeasureName.compareToIgnoreCase("measureTotal_points_visited") == 0) {
/* 162:293 */       return getTotalPointsVisited();
/* 163:    */     }
/* 164:294 */     if (additionalMeasureName.compareToIgnoreCase("measureMean_points_visited") == 0) {
/* 165:295 */       return getMeanPointsVisited();
/* 166:    */     }
/* 167:296 */     if (additionalMeasureName.compareToIgnoreCase("measureStdDev_points_visited") == 0) {
/* 168:297 */       return getStdDevPointsVisited();
/* 169:    */     }
/* 170:298 */     if (additionalMeasureName.compareToIgnoreCase("measureMin_points_visited") == 0) {
/* 171:299 */       return getMinPointsVisited();
/* 172:    */     }
/* 173:300 */     if (additionalMeasureName.compareToIgnoreCase("measureMax_points_visited") == 0) {
/* 174:301 */       return getMaxPointsVisited();
/* 175:    */     }
/* 176:304 */     if (additionalMeasureName.compareToIgnoreCase("measureTotalCoordsPerPoint") == 0) {
/* 177:305 */       return getTotalCoordsPerPoint();
/* 178:    */     }
/* 179:306 */     if (additionalMeasureName.compareToIgnoreCase("measureMeanCoordsPerPoint") == 0) {
/* 180:307 */       return getMeanCoordsPerPoint();
/* 181:    */     }
/* 182:308 */     if (additionalMeasureName.compareToIgnoreCase("measureStdDevCoordsPerPoint") == 0) {
/* 183:309 */       return getStdDevCoordsPerPoint();
/* 184:    */     }
/* 185:310 */     if (additionalMeasureName.compareToIgnoreCase("measureMinCoordsPerPoint") == 0) {
/* 186:311 */       return getMinCoordsPerPoint();
/* 187:    */     }
/* 188:312 */     if (additionalMeasureName.compareToIgnoreCase("measureMaxCoordsPerPoint") == 0) {
/* 189:313 */       return getMaxCoordsPerPoint();
/* 190:    */     }
/* 191:315 */     throw new IllegalArgumentException(additionalMeasureName + " not supported by PerformanceStats.");
/* 192:    */   }
/* 193:    */   
/* 194:    */   public String getStats()
/* 195:    */   {
/* 196:326 */     StringBuffer buf = new StringBuffer();
/* 197:    */     
/* 198:328 */     buf.append("           min, max, total, mean, stddev\n");
/* 199:329 */     buf.append("Points:    " + getMinPointsVisited() + ", " + getMaxPointsVisited() + "," + getTotalPointsVisited() + "," + getMeanPointsVisited() + ", " + getStdDevPointsVisited() + "\n");
/* 200:    */     
/* 201:    */ 
/* 202:332 */     return buf.toString();
/* 203:    */   }
/* 204:    */   
/* 205:    */   public String getRevision()
/* 206:    */   {
/* 207:341 */     return RevisionUtils.extract("$Revision: 10141 $");
/* 208:    */   }
/* 209:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.PerformanceStats
 * JD-Core Version:    0.7.0.1
 */