/*   1:    */ package weka.classifiers.functions.pace;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Random;
/*   5:    */ import weka.core.RevisionUtils;
/*   6:    */ import weka.core.matrix.DoubleVector;
/*   7:    */ import weka.core.matrix.Maths;
/*   8:    */ 
/*   9:    */ public class NormalMixture
/*  10:    */   extends MixtureDistribution
/*  11:    */ {
/*  12: 71 */   protected double separatingThreshold = 0.05D;
/*  13: 74 */   protected double trimingThreshold = 0.7D;
/*  14: 76 */   protected double fittingIntervalLength = 3.0D;
/*  15:    */   
/*  16:    */   public double getSeparatingThreshold()
/*  17:    */   {
/*  18: 90 */     return this.separatingThreshold;
/*  19:    */   }
/*  20:    */   
/*  21:    */   public void setSeparatingThreshold(double t)
/*  22:    */   {
/*  23: 99 */     this.separatingThreshold = t;
/*  24:    */   }
/*  25:    */   
/*  26:    */   public double getTrimingThreshold()
/*  27:    */   {
/*  28:109 */     return this.trimingThreshold;
/*  29:    */   }
/*  30:    */   
/*  31:    */   public void setTrimingThreshold(double t)
/*  32:    */   {
/*  33:118 */     this.trimingThreshold = t;
/*  34:    */   }
/*  35:    */   
/*  36:    */   public boolean separable(DoubleVector data, int i0, int i1, double x)
/*  37:    */   {
/*  38:132 */     double p = 0.0D;
/*  39:133 */     for (int i = i0; i <= i1; i++) {
/*  40:134 */       p += Maths.pnorm(-Math.abs(x - data.get(i)));
/*  41:    */     }
/*  42:136 */     if (p < this.separatingThreshold) {
/*  43:136 */       return true;
/*  44:    */     }
/*  45:137 */     return false;
/*  46:    */   }
/*  47:    */   
/*  48:    */   public DoubleVector supportPoints(DoubleVector data, int ne)
/*  49:    */   {
/*  50:149 */     if (data.size() < 2) {
/*  51:150 */       throw new IllegalArgumentException("data size < 2");
/*  52:    */     }
/*  53:152 */     return data.copy();
/*  54:    */   }
/*  55:    */   
/*  56:    */   public PaceMatrix fittingIntervals(DoubleVector data)
/*  57:    */   {
/*  58:162 */     DoubleVector left = data.cat(data.minus(this.fittingIntervalLength));
/*  59:163 */     DoubleVector right = data.plus(this.fittingIntervalLength).cat(data);
/*  60:    */     
/*  61:165 */     PaceMatrix a = new PaceMatrix(left.size(), 2);
/*  62:    */     
/*  63:167 */     a.setMatrix(0, left.size() - 1, 0, left);
/*  64:168 */     a.setMatrix(0, right.size() - 1, 1, right);
/*  65:    */     
/*  66:170 */     return a;
/*  67:    */   }
/*  68:    */   
/*  69:    */   public PaceMatrix probabilityMatrix(DoubleVector s, PaceMatrix intervals)
/*  70:    */   {
/*  71:184 */     int ns = s.size();
/*  72:185 */     int nr = intervals.getRowDimension();
/*  73:186 */     PaceMatrix p = new PaceMatrix(nr, ns);
/*  74:188 */     for (int i = 0; i < nr; i++) {
/*  75:189 */       for (int j = 0; j < ns; j++) {
/*  76:190 */         p.set(i, j, Maths.pnorm(intervals.get(i, 1), s.get(j), 1.0D) - Maths.pnorm(intervals.get(i, 0), s.get(j), 1.0D));
/*  77:    */       }
/*  78:    */     }
/*  79:196 */     return p;
/*  80:    */   }
/*  81:    */   
/*  82:    */   public double empiricalBayesEstimate(double x)
/*  83:    */   {
/*  84:206 */     if (Math.abs(x) > 10.0D) {
/*  85:206 */       return x;
/*  86:    */     }
/*  87:207 */     DoubleVector d = Maths.dnormLog(x, this.mixingDistribution.getPointValues(), 1.0D);
/*  88:    */     
/*  89:    */ 
/*  90:210 */     d.minusEquals(d.max());
/*  91:211 */     d = d.map("java.lang.Math", "exp");
/*  92:212 */     d.timesEquals(this.mixingDistribution.getFunctionValues());
/*  93:213 */     return this.mixingDistribution.getPointValues().innerProduct(d) / d.sum();
/*  94:    */   }
/*  95:    */   
/*  96:    */   public DoubleVector empiricalBayesEstimate(DoubleVector x)
/*  97:    */   {
/*  98:223 */     DoubleVector pred = new DoubleVector(x.size());
/*  99:224 */     for (int i = 0; i < x.size(); i++) {
/* 100:225 */       pred.set(i, empiricalBayesEstimate(x.get(i)));
/* 101:    */     }
/* 102:226 */     trim(pred);
/* 103:227 */     return pred;
/* 104:    */   }
/* 105:    */   
/* 106:    */   public DoubleVector nestedEstimate(DoubleVector x)
/* 107:    */   {
/* 108:238 */     DoubleVector chf = new DoubleVector(x.size());
/* 109:239 */     for (int i = 0; i < x.size(); i++) {
/* 110:239 */       chf.set(i, hf(x.get(i)));
/* 111:    */     }
/* 112:240 */     chf.cumulateInPlace();
/* 113:241 */     int index = chf.indexOfMax();
/* 114:242 */     DoubleVector copy = x.copy();
/* 115:243 */     if (index < x.size() - 1) {
/* 116:243 */       copy.set(index + 1, x.size() - 1, 0.0D);
/* 117:    */     }
/* 118:244 */     trim(copy);
/* 119:245 */     return copy;
/* 120:    */   }
/* 121:    */   
/* 122:    */   public DoubleVector subsetEstimate(DoubleVector x)
/* 123:    */   {
/* 124:256 */     DoubleVector h = h(x);
/* 125:257 */     DoubleVector copy = x.copy();
/* 126:258 */     for (int i = 0; i < x.size(); i++) {
/* 127:259 */       if (h.get(i) <= 0.0D) {
/* 128:259 */         copy.set(i, 0.0D);
/* 129:    */       }
/* 130:    */     }
/* 131:260 */     trim(copy);
/* 132:261 */     return copy;
/* 133:    */   }
/* 134:    */   
/* 135:    */   public void trim(DoubleVector x)
/* 136:    */   {
/* 137:270 */     for (int i = 0; i < x.size(); i++) {
/* 138:271 */       if (Math.abs(x.get(i)) <= this.trimingThreshold) {
/* 139:271 */         x.set(i, 0.0D);
/* 140:    */       }
/* 141:    */     }
/* 142:    */   }
/* 143:    */   
/* 144:    */   public double hf(double x)
/* 145:    */   {
/* 146:283 */     DoubleVector points = this.mixingDistribution.getPointValues();
/* 147:284 */     DoubleVector values = this.mixingDistribution.getFunctionValues();
/* 148:    */     
/* 149:286 */     DoubleVector d = Maths.dnormLog(x, points, 1.0D);
/* 150:287 */     d.minusEquals(d.max());
/* 151:    */     
/* 152:289 */     d = d.map("java.lang.Math", "exp");
/* 153:290 */     d.timesEquals(values);
/* 154:    */     
/* 155:292 */     return points.times(2.0D * x).minusEquals(x * x).innerProduct(d) / d.sum();
/* 156:    */   }
/* 157:    */   
/* 158:    */   public double h(double x)
/* 159:    */   {
/* 160:303 */     DoubleVector points = this.mixingDistribution.getPointValues();
/* 161:304 */     DoubleVector values = this.mixingDistribution.getFunctionValues();
/* 162:305 */     DoubleVector d = Maths.dnorm(x, points, 1.0D).timesEquals(values);
/* 163:306 */     return points.times(2.0D * x).minusEquals(x * x).innerProduct(d);
/* 164:    */   }
/* 165:    */   
/* 166:    */   public DoubleVector h(DoubleVector x)
/* 167:    */   {
/* 168:317 */     DoubleVector h = new DoubleVector(x.size());
/* 169:318 */     for (int i = 0; i < x.size(); i++) {
/* 170:319 */       h.set(i, h(x.get(i)));
/* 171:    */     }
/* 172:320 */     return h;
/* 173:    */   }
/* 174:    */   
/* 175:    */   public double f(double x)
/* 176:    */   {
/* 177:330 */     DoubleVector points = this.mixingDistribution.getPointValues();
/* 178:331 */     DoubleVector values = this.mixingDistribution.getFunctionValues();
/* 179:332 */     return Maths.dchisq(x, points).timesEquals(values).sum();
/* 180:    */   }
/* 181:    */   
/* 182:    */   public DoubleVector f(DoubleVector x)
/* 183:    */   {
/* 184:342 */     DoubleVector f = new DoubleVector(x.size());
/* 185:343 */     for (int i = 0; i < x.size(); i++) {
/* 186:344 */       f.set(i, h(f.get(i)));
/* 187:    */     }
/* 188:345 */     return f;
/* 189:    */   }
/* 190:    */   
/* 191:    */   public String toString()
/* 192:    */   {
/* 193:354 */     return this.mixingDistribution.toString();
/* 194:    */   }
/* 195:    */   
/* 196:    */   public String getRevision()
/* 197:    */   {
/* 198:363 */     return RevisionUtils.extract("$Revision: 8109 $");
/* 199:    */   }
/* 200:    */   
/* 201:    */   public static void main(String[] args)
/* 202:    */   {
/* 203:372 */     int n1 = 50;
/* 204:373 */     int n2 = 50;
/* 205:374 */     double mu1 = 0.0D;
/* 206:375 */     double mu2 = 5.0D;
/* 207:376 */     DoubleVector a = Maths.rnorm(n1, mu1, 1.0D, new Random());
/* 208:377 */     a = a.cat(Maths.rnorm(n2, mu2, 1.0D, new Random()));
/* 209:378 */     DoubleVector means = new DoubleVector(n1, mu1).cat(new DoubleVector(n2, mu2));
/* 210:    */     
/* 211:380 */     System.out.println("==========================================================");
/* 212:381 */     System.out.println("This is to test the estimation of the mixing\ndistribution of the mixture of unit variance normal\ndistributions. The example mixture used is of the form: \n\n   0.5 * N(mu1, 1) + 0.5 * N(mu2, 1)\n");
/* 213:    */     
/* 214:    */ 
/* 215:    */ 
/* 216:    */ 
/* 217:386 */     System.out.println("It also tests three estimators: the subset\nselector, the nested model selector, and the empirical Bayes\nestimator. Quadratic losses of the estimators are given, \nand are taken as the measure of their performance.");
/* 218:    */     
/* 219:    */ 
/* 220:    */ 
/* 221:390 */     System.out.println("==========================================================");
/* 222:391 */     System.out.println("mu1 = " + mu1 + " mu2 = " + mu2 + "\n");
/* 223:    */     
/* 224:393 */     System.out.println(a.size() + " observations are: \n\n" + a);
/* 225:    */     
/* 226:395 */     System.out.println("\nQuadratic loss of the raw data (i.e., the MLE) = " + a.sum2(means));
/* 227:    */     
/* 228:397 */     System.out.println("==========================================================");
/* 229:    */     
/* 230:    */ 
/* 231:400 */     NormalMixture d = new NormalMixture();
/* 232:401 */     d.fit(a, 1);
/* 233:402 */     System.out.println("The estimated mixing distribution is:\n" + d);
/* 234:    */     
/* 235:404 */     DoubleVector pred = d.nestedEstimate(a.rev()).rev();
/* 236:405 */     System.out.println("\nThe Nested Estimate = \n" + pred);
/* 237:406 */     System.out.println("Quadratic loss = " + pred.sum2(means));
/* 238:    */     
/* 239:408 */     pred = d.subsetEstimate(a);
/* 240:409 */     System.out.println("\nThe Subset Estimate = \n" + pred);
/* 241:410 */     System.out.println("Quadratic loss = " + pred.sum2(means));
/* 242:    */     
/* 243:412 */     pred = d.empiricalBayesEstimate(a);
/* 244:413 */     System.out.println("\nThe Empirical Bayes Estimate = \n" + pred);
/* 245:414 */     System.out.println("Quadratic loss = " + pred.sum2(means));
/* 246:    */   }
/* 247:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.pace.NormalMixture
 * JD-Core Version:    0.7.0.1
 */