/*   1:    */ package weka.core.matrix;
/*   2:    */ 
/*   3:    */ import java.util.Random;
/*   4:    */ import weka.core.RevisionHandler;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ import weka.core.Statistics;
/*   7:    */ 
/*   8:    */ public class Maths
/*   9:    */   implements RevisionHandler
/*  10:    */ {
/*  11:    */   public static final double PSI = 0.398942280401433D;
/*  12:    */   public static final double logPSI = -0.9189385332046727D;
/*  13:    */   public static final int undefinedDistribution = 0;
/*  14:    */   public static final int normalDistribution = 1;
/*  15:    */   public static final int chisqDistribution = 2;
/*  16:    */   
/*  17:    */   public static double hypot(double a, double b)
/*  18:    */   {
/*  19:    */     double r;
/*  20: 55 */     if (Math.abs(a) > Math.abs(b))
/*  21:    */     {
/*  22: 56 */       double r = b / a;
/*  23: 57 */       r = Math.abs(a) * Math.sqrt(1.0D + r * r);
/*  24:    */     }
/*  25: 58 */     else if (b != 0.0D)
/*  26:    */     {
/*  27: 59 */       double r = a / b;
/*  28: 60 */       r = Math.abs(b) * Math.sqrt(1.0D + r * r);
/*  29:    */     }
/*  30:    */     else
/*  31:    */     {
/*  32: 62 */       r = 0.0D;
/*  33:    */     }
/*  34: 64 */     return r;
/*  35:    */   }
/*  36:    */   
/*  37:    */   public static double square(double x)
/*  38:    */   {
/*  39: 74 */     return x * x;
/*  40:    */   }
/*  41:    */   
/*  42:    */   public static double pnorm(double x)
/*  43:    */   {
/*  44: 85 */     return Statistics.normalProbability(x);
/*  45:    */   }
/*  46:    */   
/*  47:    */   public static double pnorm(double x, double mean, double sd)
/*  48:    */   {
/*  49: 96 */     if (sd <= 0.0D) {
/*  50: 97 */       throw new IllegalArgumentException("standard deviation <= 0.0");
/*  51:    */     }
/*  52: 98 */     return pnorm((x - mean) / sd);
/*  53:    */   }
/*  54:    */   
/*  55:    */   public static DoubleVector pnorm(double x, DoubleVector mean, double sd)
/*  56:    */   {
/*  57:111 */     DoubleVector p = new DoubleVector(mean.size());
/*  58:113 */     for (int i = 0; i < mean.size(); i++) {
/*  59:114 */       p.set(i, pnorm(x, mean.get(i), sd));
/*  60:    */     }
/*  61:116 */     return p;
/*  62:    */   }
/*  63:    */   
/*  64:    */   public static double dnorm(double x)
/*  65:    */   {
/*  66:125 */     return Math.exp(-x * x / 2.0D) * 0.398942280401433D;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public static double dnorm(double x, double mean, double sd)
/*  70:    */   {
/*  71:135 */     if (sd <= 0.0D) {
/*  72:136 */       throw new IllegalArgumentException("standard deviation <= 0.0");
/*  73:    */     }
/*  74:137 */     return dnorm((x - mean) / sd);
/*  75:    */   }
/*  76:    */   
/*  77:    */   public static DoubleVector dnorm(double x, DoubleVector mean, double sd)
/*  78:    */   {
/*  79:149 */     DoubleVector den = new DoubleVector(mean.size());
/*  80:151 */     for (int i = 0; i < mean.size(); i++) {
/*  81:152 */       den.set(i, dnorm(x, mean.get(i), sd));
/*  82:    */     }
/*  83:154 */     return den;
/*  84:    */   }
/*  85:    */   
/*  86:    */   public static double dnormLog(double x)
/*  87:    */   {
/*  88:163 */     return -0.9189385332046727D - x * x / 2.0D;
/*  89:    */   }
/*  90:    */   
/*  91:    */   public static double dnormLog(double x, double mean, double sd)
/*  92:    */   {
/*  93:172 */     if (sd <= 0.0D) {
/*  94:173 */       throw new IllegalArgumentException("standard deviation <= 0.0");
/*  95:    */     }
/*  96:174 */     return -Math.log(sd) + dnormLog((x - mean) / sd);
/*  97:    */   }
/*  98:    */   
/*  99:    */   public static DoubleVector dnormLog(double x, DoubleVector mean, double sd)
/* 100:    */   {
/* 101:186 */     DoubleVector denLog = new DoubleVector(mean.size());
/* 102:188 */     for (int i = 0; i < mean.size(); i++) {
/* 103:189 */       denLog.set(i, dnormLog(x, mean.get(i), sd));
/* 104:    */     }
/* 105:191 */     return denLog;
/* 106:    */   }
/* 107:    */   
/* 108:    */   public static DoubleVector rnorm(int n, double mean, double sd, Random random)
/* 109:    */   {
/* 110:205 */     if (sd < 0.0D) {
/* 111:206 */       throw new IllegalArgumentException("standard deviation < 0.0");
/* 112:    */     }
/* 113:208 */     if (sd == 0.0D) {
/* 114:208 */       return new DoubleVector(n, mean);
/* 115:    */     }
/* 116:209 */     DoubleVector v = new DoubleVector(n);
/* 117:210 */     for (int i = 0; i < n; i++) {
/* 118:211 */       v.set(i, (random.nextGaussian() + mean) / sd);
/* 119:    */     }
/* 120:212 */     return v;
/* 121:    */   }
/* 122:    */   
/* 123:    */   public static double pchisq(double x)
/* 124:    */   {
/* 125:222 */     double xh = Math.sqrt(x);
/* 126:223 */     return pnorm(xh) - pnorm(-xh);
/* 127:    */   }
/* 128:    */   
/* 129:    */   public static double pchisq(double x, double ncp)
/* 130:    */   {
/* 131:232 */     double mean = Math.sqrt(ncp);
/* 132:233 */     double xh = Math.sqrt(x);
/* 133:234 */     return pnorm(xh - mean) - pnorm(-xh - mean);
/* 134:    */   }
/* 135:    */   
/* 136:    */   public static DoubleVector pchisq(double x, DoubleVector ncp)
/* 137:    */   {
/* 138:243 */     int n = ncp.size();
/* 139:244 */     DoubleVector p = new DoubleVector(n);
/* 140:    */     
/* 141:246 */     double xh = Math.sqrt(x);
/* 142:248 */     for (int i = 0; i < n; i++)
/* 143:    */     {
/* 144:249 */       double mean = Math.sqrt(ncp.get(i));
/* 145:250 */       p.set(i, pnorm(xh - mean) - pnorm(-xh - mean));
/* 146:    */     }
/* 147:252 */     return p;
/* 148:    */   }
/* 149:    */   
/* 150:    */   public static double dchisq(double x)
/* 151:    */   {
/* 152:261 */     if (x == 0.0D) {
/* 153:261 */       return (1.0D / 0.0D);
/* 154:    */     }
/* 155:262 */     double xh = Math.sqrt(x);
/* 156:263 */     return dnorm(xh) / xh;
/* 157:    */   }
/* 158:    */   
/* 159:    */   public static double dchisq(double x, double ncp)
/* 160:    */   {
/* 161:272 */     if (ncp == 0.0D) {
/* 162:272 */       return dchisq(x);
/* 163:    */     }
/* 164:273 */     double xh = Math.sqrt(x);
/* 165:274 */     double mean = Math.sqrt(ncp);
/* 166:275 */     return (dnorm(xh - mean) + dnorm(-xh - mean)) / (2.0D * xh);
/* 167:    */   }
/* 168:    */   
/* 169:    */   public static DoubleVector dchisq(double x, DoubleVector ncp)
/* 170:    */   {
/* 171:284 */     int n = ncp.size();
/* 172:285 */     DoubleVector d = new DoubleVector(n);
/* 173:286 */     double xh = Math.sqrt(x);
/* 174:288 */     for (int i = 0; i < n; i++)
/* 175:    */     {
/* 176:289 */       double mean = Math.sqrt(ncp.get(i));
/* 177:290 */       if (ncp.get(i) == 0.0D) {
/* 178:290 */         d.set(i, dchisq(x));
/* 179:    */       } else {
/* 180:291 */         d.set(i, (dnorm(xh - mean) + dnorm(-xh - mean)) / (2.0D * xh));
/* 181:    */       }
/* 182:    */     }
/* 183:294 */     return d;
/* 184:    */   }
/* 185:    */   
/* 186:    */   public static double dchisqLog(double x)
/* 187:    */   {
/* 188:303 */     if (x == 0.0D) {
/* 189:303 */       return (1.0D / 0.0D);
/* 190:    */     }
/* 191:304 */     double xh = Math.sqrt(x);
/* 192:305 */     return dnormLog(xh) - Math.log(xh);
/* 193:    */   }
/* 194:    */   
/* 195:    */   public static double dchisqLog(double x, double ncp)
/* 196:    */   {
/* 197:313 */     if (ncp == 0.0D) {
/* 198:313 */       return dchisqLog(x);
/* 199:    */     }
/* 200:314 */     double xh = Math.sqrt(x);
/* 201:315 */     double mean = Math.sqrt(ncp);
/* 202:316 */     return Math.log(dnorm(xh - mean) + dnorm(-xh - mean)) - Math.log(2.0D * xh);
/* 203:    */   }
/* 204:    */   
/* 205:    */   public static DoubleVector dchisqLog(double x, DoubleVector ncp)
/* 206:    */   {
/* 207:326 */     DoubleVector dLog = new DoubleVector(ncp.size());
/* 208:327 */     double xh = Math.sqrt(x);
/* 209:330 */     for (int i = 0; i < ncp.size(); i++)
/* 210:    */     {
/* 211:331 */       double mean = Math.sqrt(ncp.get(i));
/* 212:332 */       if (ncp.get(i) == 0.0D) {
/* 213:332 */         dLog.set(i, dchisqLog(x));
/* 214:    */       } else {
/* 215:333 */         dLog.set(i, Math.log(dnorm(xh - mean) + dnorm(-xh - mean)) - Math.log(2.0D * xh));
/* 216:    */       }
/* 217:    */     }
/* 218:336 */     return dLog;
/* 219:    */   }
/* 220:    */   
/* 221:    */   public static DoubleVector rchisq(int n, double ncp, Random random)
/* 222:    */   {
/* 223:348 */     DoubleVector v = new DoubleVector(n);
/* 224:349 */     double mean = Math.sqrt(ncp);
/* 225:351 */     for (int i = 0; i < n; i++)
/* 226:    */     {
/* 227:352 */       double x = random.nextGaussian() + mean;
/* 228:353 */       v.set(i, x * x);
/* 229:    */     }
/* 230:355 */     return v;
/* 231:    */   }
/* 232:    */   
/* 233:    */   public String getRevision()
/* 234:    */   {
/* 235:364 */     return RevisionUtils.extract("$Revision: 5953 $");
/* 236:    */   }
/* 237:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.matrix.Maths
 * JD-Core Version:    0.7.0.1
 */