/*   1:    */ package weka.classifiers.functions.pace;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Random;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ import weka.core.matrix.DoubleVector;
/*   7:    */ import weka.core.matrix.Maths;
/*   8:    */ 
/*   9:    */ public class ChisqMixture
/*  10:    */   extends MixtureDistribution
/*  11:    */ {
/*  12: 71 */   protected double separatingThreshold = 0.05D;
/*  13: 74 */   protected double trimingThreshold = 0.5D;
/*  14: 76 */   protected double supportThreshold = 0.5D;
/*  15: 78 */   protected int maxNumSupportPoints = 200;
/*  16: 80 */   protected int fittingIntervalLength = 3;
/*  17: 82 */   protected double fittingIntervalThreshold = 0.5D;
/*  18:    */   
/*  19:    */   public double getSeparatingThreshold()
/*  20:    */   {
/*  21: 95 */     return this.separatingThreshold;
/*  22:    */   }
/*  23:    */   
/*  24:    */   public void setSeparatingThreshold(double t)
/*  25:    */   {
/*  26:104 */     this.separatingThreshold = t;
/*  27:    */   }
/*  28:    */   
/*  29:    */   public double getTrimingThreshold()
/*  30:    */   {
/*  31:113 */     return this.trimingThreshold;
/*  32:    */   }
/*  33:    */   
/*  34:    */   public void setTrimingThreshold(double t)
/*  35:    */   {
/*  36:122 */     this.trimingThreshold = t;
/*  37:    */   }
/*  38:    */   
/*  39:    */   public boolean separable(DoubleVector data, int i0, int i1, double x)
/*  40:    */   {
/*  41:137 */     DoubleVector dataSqrt = data.sqrt();
/*  42:138 */     double xh = Math.sqrt(x);
/*  43:    */     
/*  44:140 */     NormalMixture m = new NormalMixture();
/*  45:141 */     m.setSeparatingThreshold(this.separatingThreshold);
/*  46:142 */     return m.separable(dataSqrt, i0, i1, xh);
/*  47:    */   }
/*  48:    */   
/*  49:    */   public DoubleVector supportPoints(DoubleVector data, int ne)
/*  50:    */   {
/*  51:155 */     DoubleVector sp = new DoubleVector();
/*  52:156 */     sp.setCapacity(data.size() + 1);
/*  53:158 */     if ((data.get(0) < this.supportThreshold) || (ne != 0)) {
/*  54:159 */       sp.addElement(0.0D);
/*  55:    */     }
/*  56:160 */     for (int i = 0; i < data.size(); i++) {
/*  57:161 */       if (data.get(i) > this.supportThreshold) {
/*  58:162 */         sp.addElement(data.get(i));
/*  59:    */       }
/*  60:    */     }
/*  61:165 */     if (sp.size() > this.maxNumSupportPoints) {
/*  62:166 */       throw new IllegalArgumentException("Too many support points. ");
/*  63:    */     }
/*  64:168 */     return sp;
/*  65:    */   }
/*  66:    */   
/*  67:    */   public PaceMatrix fittingIntervals(DoubleVector data)
/*  68:    */   {
/*  69:179 */     PaceMatrix a = new PaceMatrix(data.size() * 2, 2);
/*  70:180 */     DoubleVector v = data.sqrt();
/*  71:181 */     int count = 0;
/*  72:183 */     for (int i = 0; i < data.size(); i++)
/*  73:    */     {
/*  74:184 */       double left = v.get(i) - this.fittingIntervalLength;
/*  75:185 */       if (left < this.fittingIntervalThreshold) {
/*  76:185 */         left = 0.0D;
/*  77:    */       }
/*  78:186 */       left *= left;
/*  79:187 */       double right = data.get(i);
/*  80:188 */       if (right < this.fittingIntervalThreshold) {
/*  81:189 */         right = this.fittingIntervalThreshold;
/*  82:    */       }
/*  83:190 */       a.set(count, 0, left);
/*  84:191 */       a.set(count, 1, right);
/*  85:192 */       count++;
/*  86:    */     }
/*  87:194 */     for (int i = 0; i < data.size(); i++)
/*  88:    */     {
/*  89:195 */       double left = data.get(i);
/*  90:196 */       if (left < this.fittingIntervalThreshold) {
/*  91:196 */         left = 0.0D;
/*  92:    */       }
/*  93:197 */       double right = v.get(i) + this.fittingIntervalThreshold;
/*  94:198 */       right *= right;
/*  95:199 */       a.set(count, 0, left);
/*  96:200 */       a.set(count, 1, right);
/*  97:201 */       count++;
/*  98:    */     }
/*  99:203 */     a.setRowDimension(count);
/* 100:    */     
/* 101:205 */     return a;
/* 102:    */   }
/* 103:    */   
/* 104:    */   public PaceMatrix probabilityMatrix(DoubleVector s, PaceMatrix intervals)
/* 105:    */   {
/* 106:218 */     int ns = s.size();
/* 107:219 */     int nr = intervals.getRowDimension();
/* 108:220 */     PaceMatrix p = new PaceMatrix(nr, ns);
/* 109:222 */     for (int i = 0; i < nr; i++) {
/* 110:223 */       for (int j = 0; j < ns; j++) {
/* 111:224 */         p.set(i, j, Maths.pchisq(intervals.get(i, 1), s.get(j)) - Maths.pchisq(intervals.get(i, 0), s.get(j)));
/* 112:    */       }
/* 113:    */     }
/* 114:230 */     return p;
/* 115:    */   }
/* 116:    */   
/* 117:    */   public double pace6(double x)
/* 118:    */   {
/* 119:242 */     if (x > 100.0D) {
/* 120:242 */       return x;
/* 121:    */     }
/* 122:243 */     DoubleVector points = this.mixingDistribution.getPointValues();
/* 123:244 */     DoubleVector values = this.mixingDistribution.getFunctionValues();
/* 124:245 */     DoubleVector mean = points.sqrt();
/* 125:    */     
/* 126:247 */     DoubleVector d = Maths.dchisqLog(x, points);
/* 127:248 */     d.minusEquals(d.max());
/* 128:249 */     d = d.map("java.lang.Math", "exp").timesEquals(values);
/* 129:250 */     double atilde = mean.innerProduct(d) / d.sum();
/* 130:251 */     return atilde * atilde;
/* 131:    */   }
/* 132:    */   
/* 133:    */   public DoubleVector pace6(DoubleVector x)
/* 134:    */   {
/* 135:262 */     DoubleVector pred = new DoubleVector(x.size());
/* 136:263 */     for (int i = 0; i < x.size(); i++) {
/* 137:264 */       pred.set(i, pace6(x.get(i)));
/* 138:    */     }
/* 139:265 */     trim(pred);
/* 140:266 */     return pred;
/* 141:    */   }
/* 142:    */   
/* 143:    */   public DoubleVector pace2(DoubleVector x)
/* 144:    */   {
/* 145:277 */     DoubleVector chf = new DoubleVector(x.size());
/* 146:278 */     for (int i = 0; i < x.size(); i++) {
/* 147:278 */       chf.set(i, hf(x.get(i)));
/* 148:    */     }
/* 149:280 */     chf.cumulateInPlace();
/* 150:    */     
/* 151:282 */     int index = chf.indexOfMax();
/* 152:    */     
/* 153:284 */     DoubleVector copy = x.copy();
/* 154:285 */     if (index < x.size() - 1) {
/* 155:285 */       copy.set(index + 1, x.size() - 1, 0.0D);
/* 156:    */     }
/* 157:286 */     trim(copy);
/* 158:287 */     return copy;
/* 159:    */   }
/* 160:    */   
/* 161:    */   public DoubleVector pace4(DoubleVector x)
/* 162:    */   {
/* 163:298 */     DoubleVector h = h(x);
/* 164:299 */     DoubleVector copy = x.copy();
/* 165:300 */     for (int i = 0; i < x.size(); i++) {
/* 166:301 */       if (h.get(i) <= 0.0D) {
/* 167:301 */         copy.set(i, 0.0D);
/* 168:    */       }
/* 169:    */     }
/* 170:302 */     trim(copy);
/* 171:303 */     return copy;
/* 172:    */   }
/* 173:    */   
/* 174:    */   public void trim(DoubleVector x)
/* 175:    */   {
/* 176:313 */     for (int i = 0; i < x.size(); i++) {
/* 177:314 */       if (x.get(i) <= this.trimingThreshold) {
/* 178:314 */         x.set(i, 0.0D);
/* 179:    */       }
/* 180:    */     }
/* 181:    */   }
/* 182:    */   
/* 183:    */   public double hf(double AHat)
/* 184:    */   {
/* 185:327 */     DoubleVector points = this.mixingDistribution.getPointValues();
/* 186:328 */     DoubleVector values = this.mixingDistribution.getFunctionValues();
/* 187:    */     
/* 188:330 */     double x = Math.sqrt(AHat);
/* 189:331 */     DoubleVector mean = points.sqrt();
/* 190:332 */     DoubleVector d1 = Maths.dnormLog(x, mean, 1.0D);
/* 191:333 */     double d1max = d1.max();
/* 192:334 */     d1.minusEquals(d1max);
/* 193:335 */     DoubleVector d2 = Maths.dnormLog(-x, mean, 1.0D);
/* 194:336 */     d2.minusEquals(d1max);
/* 195:    */     
/* 196:338 */     d1 = d1.map("java.lang.Math", "exp");
/* 197:339 */     d1.timesEquals(values);
/* 198:340 */     d2 = d2.map("java.lang.Math", "exp");
/* 199:341 */     d2.timesEquals(values);
/* 200:    */     
/* 201:343 */     return (points.minus(x / 2.0D).innerProduct(d1) - points.plus(x / 2.0D).innerProduct(d2)) / (d1.sum() + d2.sum());
/* 202:    */   }
/* 203:    */   
/* 204:    */   public double h(double AHat)
/* 205:    */   {
/* 206:356 */     if (AHat == 0.0D) {
/* 207:356 */       return 0.0D;
/* 208:    */     }
/* 209:357 */     DoubleVector points = this.mixingDistribution.getPointValues();
/* 210:358 */     DoubleVector values = this.mixingDistribution.getFunctionValues();
/* 211:    */     
/* 212:360 */     double aHat = Math.sqrt(AHat);
/* 213:361 */     DoubleVector aStar = points.sqrt();
/* 214:362 */     DoubleVector d1 = Maths.dnorm(aHat, aStar, 1.0D).timesEquals(values);
/* 215:363 */     DoubleVector d2 = Maths.dnorm(-aHat, aStar, 1.0D).timesEquals(values);
/* 216:    */     
/* 217:365 */     return points.minus(aHat / 2.0D).innerProduct(d1) - points.plus(aHat / 2.0D).innerProduct(d2);
/* 218:    */   }
/* 219:    */   
/* 220:    */   public DoubleVector h(DoubleVector AHat)
/* 221:    */   {
/* 222:377 */     DoubleVector h = new DoubleVector(AHat.size());
/* 223:378 */     for (int i = 0; i < AHat.size(); i++) {
/* 224:379 */       h.set(i, h(AHat.get(i)));
/* 225:    */     }
/* 226:380 */     return h;
/* 227:    */   }
/* 228:    */   
/* 229:    */   public double f(double x)
/* 230:    */   {
/* 231:391 */     DoubleVector points = this.mixingDistribution.getPointValues();
/* 232:392 */     DoubleVector values = this.mixingDistribution.getFunctionValues();
/* 233:    */     
/* 234:394 */     return Maths.dchisq(x, points).timesEquals(values).sum();
/* 235:    */   }
/* 236:    */   
/* 237:    */   public DoubleVector f(DoubleVector x)
/* 238:    */   {
/* 239:405 */     DoubleVector f = new DoubleVector(x.size());
/* 240:406 */     for (int i = 0; i < x.size(); i++) {
/* 241:407 */       f.set(i, h(f.get(i)));
/* 242:    */     }
/* 243:408 */     return f;
/* 244:    */   }
/* 245:    */   
/* 246:    */   public String toString()
/* 247:    */   {
/* 248:417 */     return this.mixingDistribution.toString();
/* 249:    */   }
/* 250:    */   
/* 251:    */   public String getRevision()
/* 252:    */   {
/* 253:426 */     return RevisionUtils.extract("$Revision: 8109 $");
/* 254:    */   }
/* 255:    */   
/* 256:    */   public static void main(String[] args)
/* 257:    */   {
/* 258:436 */     int n1 = 50;
/* 259:437 */     int n2 = 50;
/* 260:438 */     double ncp1 = 0.0D;
/* 261:439 */     double ncp2 = 10.0D;
/* 262:440 */     double mu1 = Math.sqrt(ncp1);
/* 263:441 */     double mu2 = Math.sqrt(ncp2);
/* 264:442 */     DoubleVector a = Maths.rnorm(n1, mu1, 1.0D, new Random());
/* 265:443 */     a = a.cat(Maths.rnorm(n2, mu2, 1.0D, new Random()));
/* 266:444 */     DoubleVector aNormal = a;
/* 267:445 */     a = a.square();
/* 268:446 */     a.sort();
/* 269:    */     
/* 270:448 */     DoubleVector means = new DoubleVector(n1, mu1).cat(new DoubleVector(n2, mu2));
/* 271:    */     
/* 272:450 */     System.out.println("==========================================================");
/* 273:451 */     System.out.println("This is to test the estimation of the mixing\ndistribution of the mixture of non-central Chi-square\ndistributions. The example mixture used is of the form: \n\n   0.5 * Chi^2_1(ncp1) + 0.5 * Chi^2_1(ncp2)\n");
/* 274:    */     
/* 275:    */ 
/* 276:    */ 
/* 277:    */ 
/* 278:456 */     System.out.println("It also tests the PACE estimators. Quadratic losses of the\nestimators are given, measuring their performance.");
/* 279:    */     
/* 280:458 */     System.out.println("==========================================================");
/* 281:459 */     System.out.println("ncp1 = " + ncp1 + " ncp2 = " + ncp2 + "\n");
/* 282:    */     
/* 283:461 */     System.out.println(a.size() + " observations are: \n\n" + a);
/* 284:    */     
/* 285:463 */     System.out.println("\nQuadratic loss of the raw data (i.e., the MLE) = " + aNormal.sum2(means));
/* 286:    */     
/* 287:465 */     System.out.println("==========================================================");
/* 288:    */     
/* 289:    */ 
/* 290:468 */     ChisqMixture d = new ChisqMixture();
/* 291:469 */     d.fit(a, 1);
/* 292:470 */     System.out.println("The estimated mixing distribution is\n" + d);
/* 293:    */     
/* 294:472 */     DoubleVector pred = d.pace2(a.rev()).rev();
/* 295:473 */     System.out.println("\nThe PACE2 Estimate = \n" + pred);
/* 296:474 */     System.out.println("Quadratic loss = " + pred.sqrt().times(aNormal.sign()).sum2(means));
/* 297:    */     
/* 298:    */ 
/* 299:477 */     pred = d.pace4(a);
/* 300:478 */     System.out.println("\nThe PACE4 Estimate = \n" + pred);
/* 301:479 */     System.out.println("Quadratic loss = " + pred.sqrt().times(aNormal.sign()).sum2(means));
/* 302:    */     
/* 303:    */ 
/* 304:482 */     pred = d.pace6(a);
/* 305:483 */     System.out.println("\nThe PACE6 Estimate = \n" + pred);
/* 306:484 */     System.out.println("Quadratic loss = " + pred.sqrt().times(aNormal.sign()).sum2(means));
/* 307:    */   }
/* 308:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.pace.ChisqMixture
 * JD-Core Version:    0.7.0.1
 */