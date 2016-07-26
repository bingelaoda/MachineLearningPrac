/*   1:    */ package weka.experiment;
/*   2:    */ 
/*   3:    */ import java.io.InputStreamReader;
/*   4:    */ import java.io.LineNumberReader;
/*   5:    */ import java.io.PrintStream;
/*   6:    */ import java.io.Serializable;
/*   7:    */ import java.util.StringTokenizer;
/*   8:    */ import weka.core.RevisionHandler;
/*   9:    */ import weka.core.RevisionUtils;
/*  10:    */ import weka.core.Utils;
/*  11:    */ 
/*  12:    */ public class Stats
/*  13:    */   implements Serializable, RevisionHandler
/*  14:    */ {
/*  15:    */   private static final long serialVersionUID = -8610544539090024102L;
/*  16:119 */   public double count = 0.0D;
/*  17:122 */   public double sum = 0.0D;
/*  18:125 */   public double sumSq = 0.0D;
/*  19:128 */   public double stdDev = (0.0D / 0.0D);
/*  20:131 */   public double mean = (0.0D / 0.0D);
/*  21:134 */   public double min = (0.0D / 0.0D);
/*  22:137 */   public double max = (0.0D / 0.0D);
/*  23:140 */   private double stdDevFactor = 0.0D;
/*  24:    */   
/*  25:    */   private void reset()
/*  26:    */   {
/*  27:143 */     this.count = 0.0D;
/*  28:144 */     this.sum = 0.0D;
/*  29:145 */     this.sumSq = 0.0D;
/*  30:146 */     this.stdDev = (0.0D / 0.0D);
/*  31:147 */     this.mean = (0.0D / 0.0D);
/*  32:148 */     this.min = (0.0D / 0.0D);
/*  33:149 */     this.max = (0.0D / 0.0D);
/*  34:150 */     this.stdDevFactor = 0.0D;
/*  35:    */   }
/*  36:    */   
/*  37:    */   private void negativeCount()
/*  38:    */   {
/*  39:154 */     this.sum = (0.0D / 0.0D);
/*  40:155 */     this.sumSq = (0.0D / 0.0D);
/*  41:156 */     this.stdDev = (0.0D / 0.0D);
/*  42:157 */     this.mean = (0.0D / 0.0D);
/*  43:158 */     this.min = (0.0D / 0.0D);
/*  44:159 */     this.max = (0.0D / 0.0D);
/*  45:    */   }
/*  46:    */   
/*  47:    */   private void goInvalid()
/*  48:    */   {
/*  49:163 */     this.count = (0.0D / 0.0D);
/*  50:164 */     negativeCount();
/*  51:    */   }
/*  52:    */   
/*  53:    */   private boolean isInvalid()
/*  54:    */   {
/*  55:168 */     return Double.isNaN(this.count);
/*  56:    */   }
/*  57:    */   
/*  58:    */   public void add(double value)
/*  59:    */   {
/*  60:180 */     add(value, 1.0D);
/*  61:    */   }
/*  62:    */   
/*  63:    */   public void add(double value, double weight)
/*  64:    */   {
/*  65:192 */     if (weight < 0.0D)
/*  66:    */     {
/*  67:193 */       subtract(value, -weight);
/*  68:194 */       return;
/*  69:    */     }
/*  70:198 */     if (isInvalid()) {
/*  71:199 */       return;
/*  72:    */     }
/*  73:202 */     if ((Double.isInfinite(weight)) || (Double.isNaN(weight)) || (Double.isInfinite(value)) || (Double.isNaN(value)))
/*  74:    */     {
/*  75:204 */       goInvalid();
/*  76:205 */       return;
/*  77:    */     }
/*  78:209 */     if (weight == 0.0D) {
/*  79:210 */       return;
/*  80:    */     }
/*  81:212 */     double newCount = this.count + weight;
/*  82:213 */     if ((this.count < 0.0D) && ((newCount > 0.0D) || (Utils.eq(newCount, 0.0D))))
/*  83:    */     {
/*  84:214 */       reset();
/*  85:215 */       return;
/*  86:    */     }
/*  87:218 */     this.count = newCount;
/*  88:220 */     if (this.count < 0.0D) {
/*  89:221 */       return;
/*  90:    */     }
/*  91:224 */     double weightedValue = value * weight;
/*  92:225 */     this.sum += weightedValue;
/*  93:226 */     this.sumSq += value * weightedValue;
/*  94:227 */     if (Double.isNaN(this.mean))
/*  95:    */     {
/*  96:230 */       this.mean = value;
/*  97:231 */       this.stdDevFactor = 0.0D;
/*  98:    */     }
/*  99:    */     else
/* 100:    */     {
/* 101:233 */       double delta = weight * (value - this.mean);
/* 102:234 */       this.mean += delta / this.count;
/* 103:235 */       this.stdDevFactor += delta * (value - this.mean);
/* 104:    */     }
/* 105:238 */     if (Double.isNaN(this.min)) {
/* 106:239 */       this.min = (this.max = value);
/* 107:240 */     } else if (value < this.min) {
/* 108:241 */       this.min = value;
/* 109:242 */     } else if (value > this.max) {
/* 110:243 */       this.max = value;
/* 111:    */     }
/* 112:    */   }
/* 113:    */   
/* 114:    */   public void subtract(double value)
/* 115:    */   {
/* 116:256 */     subtract(value, 1.0D);
/* 117:    */   }
/* 118:    */   
/* 119:    */   public void subtract(double value, double weight)
/* 120:    */   {
/* 121:268 */     if (weight < 0.0D)
/* 122:    */     {
/* 123:269 */       add(value, -weight);
/* 124:270 */       return;
/* 125:    */     }
/* 126:274 */     if (isInvalid()) {
/* 127:275 */       return;
/* 128:    */     }
/* 129:278 */     if ((Double.isInfinite(weight)) || (Double.isNaN(weight)) || (Double.isInfinite(value)) || (Double.isNaN(value)))
/* 130:    */     {
/* 131:280 */       goInvalid();
/* 132:281 */       return;
/* 133:    */     }
/* 134:285 */     if (weight == 0.0D) {
/* 135:286 */       return;
/* 136:    */     }
/* 137:288 */     this.count -= weight;
/* 138:290 */     if (Utils.eq(this.count, 0.0D))
/* 139:    */     {
/* 140:291 */       reset();
/* 141:292 */       return;
/* 142:    */     }
/* 143:293 */     if (this.count < 0.0D)
/* 144:    */     {
/* 145:294 */       negativeCount();
/* 146:295 */       return;
/* 147:    */     }
/* 148:298 */     double weightedValue = value * weight;
/* 149:299 */     this.sum -= weightedValue;
/* 150:300 */     this.sumSq -= value * weightedValue;
/* 151:301 */     double delta = weight * (value - this.mean);
/* 152:302 */     this.mean -= delta / this.count;
/* 153:303 */     this.stdDevFactor -= delta * (value - this.mean);
/* 154:    */   }
/* 155:    */   
/* 156:    */   public void calculateDerived()
/* 157:    */   {
/* 158:313 */     if (this.count <= 1.0D)
/* 159:    */     {
/* 160:314 */       this.stdDev = (0.0D / 0.0D);
/* 161:315 */       return;
/* 162:    */     }
/* 163:317 */     this.stdDev = (this.stdDevFactor / (this.count - 1.0D));
/* 164:318 */     if (this.stdDev < 0.0D)
/* 165:    */     {
/* 166:319 */       this.stdDev = 0.0D;
/* 167:320 */       return;
/* 168:    */     }
/* 169:322 */     this.stdDev = Math.sqrt(this.stdDev);
/* 170:    */   }
/* 171:    */   
/* 172:    */   public String toString()
/* 173:    */   {
/* 174:333 */     return "Count   " + Utils.doubleToString(this.count, 8) + '\n' + "Min     " + Utils.doubleToString(this.min, 8) + '\n' + "Max     " + Utils.doubleToString(this.max, 8) + '\n' + "Sum     " + Utils.doubleToString(this.sum, 8) + '\n' + "SumSq   " + Utils.doubleToString(this.sumSq, 8) + '\n' + "Mean    " + Utils.doubleToString(this.mean, 8) + '\n' + "StdDev  " + Utils.doubleToString(this.stdDev, 8) + '\n';
/* 175:    */   }
/* 176:    */   
/* 177:    */   public String getRevision()
/* 178:    */   {
/* 179:349 */     return RevisionUtils.extract("$Revision: 11424 $");
/* 180:    */   }
/* 181:    */   
/* 182:    */   public static void main(String[] args)
/* 183:    */   {
/* 184:    */     try
/* 185:    */     {
/* 186:361 */       Stats ps = new Stats();
/* 187:362 */       LineNumberReader r = new LineNumberReader(new InputStreamReader(System.in));
/* 188:    */       String line;
/* 189:365 */       while ((line = r.readLine()) != null)
/* 190:    */       {
/* 191:366 */         line = line.trim();
/* 192:367 */         if ((!line.equals("")) && (!line.startsWith("@")) && (!line.startsWith("%")))
/* 193:    */         {
/* 194:370 */           StringTokenizer s = new StringTokenizer(line, " ,\t\n\r\f");
/* 195:    */           
/* 196:372 */           int count = 0;
/* 197:373 */           double v1 = 0.0D;
/* 198:374 */           while (s.hasMoreTokens())
/* 199:    */           {
/* 200:375 */             double val = new Double(s.nextToken()).doubleValue();
/* 201:376 */             if (count == 0)
/* 202:    */             {
/* 203:377 */               v1 = val;
/* 204:    */             }
/* 205:    */             else
/* 206:    */             {
/* 207:379 */               System.err.println("MSG: Too many values in line \"" + line + "\", skipped.");
/* 208:    */               
/* 209:381 */               break;
/* 210:    */             }
/* 211:383 */             count++;
/* 212:    */           }
/* 213:385 */           if (count == 1) {
/* 214:386 */             ps.add(v1);
/* 215:    */           }
/* 216:    */         }
/* 217:    */       }
/* 218:389 */       ps.calculateDerived();
/* 219:390 */       System.err.println(ps);
/* 220:    */     }
/* 221:    */     catch (Exception ex)
/* 222:    */     {
/* 223:392 */       ex.printStackTrace();
/* 224:393 */       System.err.println(ex.getMessage());
/* 225:    */     }
/* 226:    */   }
/* 227:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.Stats
 * JD-Core Version:    0.7.0.1
 */