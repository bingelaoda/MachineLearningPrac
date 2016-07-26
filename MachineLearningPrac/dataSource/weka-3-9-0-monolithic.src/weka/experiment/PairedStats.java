/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.InputStreamReader;
/*   4:    */ import java.io.LineNumberReader;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.util.StringTokenizer;
/*   7:    */ import weka.core.RevisionHandler;
/*   8:    */ import weka.core.RevisionUtils;
/*   9:    */ import weka.core.Statistics;
/*  10:    */ import weka.core.Utils;
/*  11:    */ 
/*  12:    */ public class PairedStats
/*  13:    */   implements RevisionHandler
/*  14:    */ {
/*  15:    */   public Stats xStats;
/*  16:    */   public Stats yStats;
/*  17:    */   public Stats differencesStats;
/*  18:    */   public double differencesProbability;
/*  19:    */   public double correlation;
/*  20:    */   public double xySum;
/*  21:    */   public double count;
/*  22:    */   public int differencesSignificance;
/*  23:    */   public double sigLevel;
/*  24: 72 */   protected int m_degreesOfFreedom = 0;
/*  25:    */   
/*  26:    */   public PairedStats(double sig)
/*  27:    */   {
/*  28: 81 */     this.xStats = new Stats();
/*  29: 82 */     this.yStats = new Stats();
/*  30: 83 */     this.differencesStats = new Stats();
/*  31: 84 */     this.sigLevel = sig;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public void setDegreesOfFreedom(int d)
/*  35:    */   {
/*  36: 92 */     if (d <= 0) {
/*  37: 93 */       throw new IllegalArgumentException("PairedStats: degrees of freedom must be >= 1");
/*  38:    */     }
/*  39: 95 */     this.m_degreesOfFreedom = d;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public int getDegreesOfFreedom()
/*  43:    */   {
/*  44:103 */     return this.m_degreesOfFreedom;
/*  45:    */   }
/*  46:    */   
/*  47:    */   public void add(double value1, double value2)
/*  48:    */   {
/*  49:114 */     this.xStats.add(value1);
/*  50:115 */     this.yStats.add(value2);
/*  51:116 */     this.differencesStats.add(value1 - value2);
/*  52:117 */     this.xySum += value1 * value2;
/*  53:118 */     this.count += 1.0D;
/*  54:    */   }
/*  55:    */   
/*  56:    */   public void subtract(double value1, double value2)
/*  57:    */   {
/*  58:129 */     this.xStats.subtract(value1);
/*  59:130 */     this.yStats.subtract(value2);
/*  60:131 */     this.differencesStats.subtract(value1 - value2);
/*  61:132 */     this.xySum -= value1 * value2;
/*  62:133 */     this.count -= 1.0D;
/*  63:    */   }
/*  64:    */   
/*  65:    */   public void add(double[] value1, double[] value2)
/*  66:    */   {
/*  67:144 */     if ((value1 == null) || (value2 == null)) {
/*  68:145 */       throw new NullPointerException();
/*  69:    */     }
/*  70:147 */     if (value1.length != value2.length) {
/*  71:148 */       throw new IllegalArgumentException("Arrays must be of the same length");
/*  72:    */     }
/*  73:150 */     for (int i = 0; i < value1.length; i++) {
/*  74:151 */       add(value1[i], value2[i]);
/*  75:    */     }
/*  76:    */   }
/*  77:    */   
/*  78:    */   public void subtract(double[] value1, double[] value2)
/*  79:    */   {
/*  80:163 */     if ((value1 == null) || (value2 == null)) {
/*  81:164 */       throw new NullPointerException();
/*  82:    */     }
/*  83:166 */     if (value1.length != value2.length) {
/*  84:167 */       throw new IllegalArgumentException("Arrays must be of the same length");
/*  85:    */     }
/*  86:169 */     for (int i = 0; i < value1.length; i++) {
/*  87:170 */       subtract(value1[i], value2[i]);
/*  88:    */     }
/*  89:    */   }
/*  90:    */   
/*  91:    */   public void calculateDerived()
/*  92:    */   {
/*  93:180 */     this.xStats.calculateDerived();
/*  94:181 */     this.yStats.calculateDerived();
/*  95:182 */     this.differencesStats.calculateDerived();
/*  96:    */     
/*  97:184 */     this.correlation = (0.0D / 0.0D);
/*  98:185 */     if ((!Double.isNaN(this.xStats.stdDev)) && (!Double.isNaN(this.yStats.stdDev)) && (!Utils.eq(this.xStats.stdDev, 0.0D)))
/*  99:    */     {
/* 100:187 */       double slope = (this.xySum - this.xStats.sum * this.yStats.sum / this.count) / (this.xStats.sumSq - this.xStats.sum * this.xStats.mean);
/* 101:189 */       if (!Utils.eq(this.yStats.stdDev, 0.0D)) {
/* 102:190 */         this.correlation = (slope * this.xStats.stdDev / this.yStats.stdDev);
/* 103:    */       } else {
/* 104:192 */         this.correlation = 1.0D;
/* 105:    */       }
/* 106:    */     }
/* 107:196 */     if (Utils.gr(this.differencesStats.stdDev, 0.0D))
/* 108:    */     {
/* 109:197 */       double tval = this.differencesStats.mean * Math.sqrt(this.count) / this.differencesStats.stdDev;
/* 110:201 */       if (this.m_degreesOfFreedom >= 1) {
/* 111:202 */         this.differencesProbability = Statistics.FProbability(tval * tval, 1, this.m_degreesOfFreedom);
/* 112:205 */       } else if (this.count > 1.0D) {
/* 113:206 */         this.differencesProbability = Statistics.FProbability(tval * tval, 1, (int)this.count - 1);
/* 114:    */       } else {
/* 115:209 */         this.differencesProbability = 1.0D;
/* 116:    */       }
/* 117:    */     }
/* 118:213 */     else if (this.differencesStats.sumSq == 0.0D)
/* 119:    */     {
/* 120:214 */       this.differencesProbability = 1.0D;
/* 121:    */     }
/* 122:    */     else
/* 123:    */     {
/* 124:216 */       this.differencesProbability = 0.0D;
/* 125:    */     }
/* 126:219 */     this.differencesSignificance = 0;
/* 127:220 */     if (this.differencesProbability <= this.sigLevel) {
/* 128:221 */       if (this.xStats.mean > this.yStats.mean) {
/* 129:222 */         this.differencesSignificance = 1;
/* 130:    */       } else {
/* 131:224 */         this.differencesSignificance = -1;
/* 132:    */       }
/* 133:    */     }
/* 134:    */   }
/* 135:    */   
/* 136:    */   public String toString()
/* 137:    */   {
/* 138:236 */     return "Analysis for " + Utils.doubleToString(this.count, 0) + " points:\n" + "                " + "         Column 1" + "         Column 2" + "       Difference\n" + "Minimums        " + Utils.doubleToString(this.xStats.min, 17, 4) + Utils.doubleToString(this.yStats.min, 17, 4) + Utils.doubleToString(this.differencesStats.min, 17, 4) + '\n' + "Maximums        " + Utils.doubleToString(this.xStats.max, 17, 4) + Utils.doubleToString(this.yStats.max, 17, 4) + Utils.doubleToString(this.differencesStats.max, 17, 4) + '\n' + "Sums            " + Utils.doubleToString(this.xStats.sum, 17, 4) + Utils.doubleToString(this.yStats.sum, 17, 4) + Utils.doubleToString(this.differencesStats.sum, 17, 4) + '\n' + "SumSquares      " + Utils.doubleToString(this.xStats.sumSq, 17, 4) + Utils.doubleToString(this.yStats.sumSq, 17, 4) + Utils.doubleToString(this.differencesStats.sumSq, 17, 4) + '\n' + "Means           " + Utils.doubleToString(this.xStats.mean, 17, 4) + Utils.doubleToString(this.yStats.mean, 17, 4) + Utils.doubleToString(this.differencesStats.mean, 17, 4) + '\n' + "SDs             " + Utils.doubleToString(this.xStats.stdDev, 17, 4) + Utils.doubleToString(this.yStats.stdDev, 17, 4) + Utils.doubleToString(this.differencesStats.stdDev, 17, 4) + '\n' + "Prob(differences) " + Utils.doubleToString(this.differencesProbability, 4) + " (sigflag " + this.differencesSignificance + ")\n" + "Correlation       " + Utils.doubleToString(this.correlation, 4) + "\n";
/* 139:    */   }
/* 140:    */   
/* 141:    */   public String getRevision()
/* 142:    */   {
/* 143:279 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 144:    */   }
/* 145:    */   
/* 146:    */   public static void main(String[] args)
/* 147:    */   {
/* 148:    */     try
/* 149:    */     {
/* 150:291 */       PairedStats ps = new PairedStats(0.05D);
/* 151:292 */       LineNumberReader r = new LineNumberReader(new InputStreamReader(System.in));
/* 152:    */       String line;
/* 153:295 */       while ((line = r.readLine()) != null)
/* 154:    */       {
/* 155:296 */         line = line.trim();
/* 156:297 */         if ((!line.equals("")) && (!line.startsWith("@")) && (!line.startsWith("%")))
/* 157:    */         {
/* 158:300 */           StringTokenizer s = new StringTokenizer(line, " ,\t\n\r\f");
/* 159:    */           
/* 160:302 */           int count = 0;
/* 161:303 */           double v1 = 0.0D;double v2 = 0.0D;
/* 162:304 */           while (s.hasMoreTokens())
/* 163:    */           {
/* 164:305 */             double val = new Double(s.nextToken()).doubleValue();
/* 165:306 */             if (count == 0)
/* 166:    */             {
/* 167:307 */               v1 = val;
/* 168:    */             }
/* 169:308 */             else if (count == 1)
/* 170:    */             {
/* 171:309 */               v2 = val;
/* 172:    */             }
/* 173:    */             else
/* 174:    */             {
/* 175:311 */               System.err.println("MSG: Too many values in line \"" + line + "\", skipped.");
/* 176:    */               
/* 177:313 */               break;
/* 178:    */             }
/* 179:315 */             count++;
/* 180:    */           }
/* 181:317 */           if (count == 2) {
/* 182:318 */             ps.add(v1, v2);
/* 183:    */           }
/* 184:    */         }
/* 185:    */       }
/* 186:321 */       ps.calculateDerived();
/* 187:322 */       System.err.println(ps);
/* 188:    */     }
/* 189:    */     catch (Exception ex)
/* 190:    */     {
/* 191:324 */       ex.printStackTrace();
/* 192:325 */       System.err.println(ex.getMessage());
/* 193:    */     }
/* 194:    */   }
/* 195:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.PairedStats
 * JD-Core Version:    0.7.0.1
 */