/*   1:    */ package weka.core;
/*   2:    */ 
/*   3:    */ import java.io.PrintStream;
/*   4:    */ import java.util.Random;
/*   5:    */ 
/*   6:    */ public final class RandomVariates
/*   7:    */   extends Random
/*   8:    */   implements RevisionHandler
/*   9:    */ {
/*  10:    */   private static final long serialVersionUID = -4763742718209460354L;
/*  11:    */   
/*  12:    */   public RandomVariates() {}
/*  13:    */   
/*  14:    */   public RandomVariates(long seed)
/*  15:    */   {
/*  16: 47 */     super(seed);
/*  17:    */   }
/*  18:    */   
/*  19:    */   protected int next(int bits)
/*  20:    */   {
/*  21: 56 */     return super.next(bits);
/*  22:    */   }
/*  23:    */   
/*  24:    */   public double nextExponential()
/*  25:    */   {
/*  26: 67 */     return -Math.log(1.0D - super.nextDouble());
/*  27:    */   }
/*  28:    */   
/*  29:    */   public double nextErlang(int a)
/*  30:    */     throws Exception
/*  31:    */   {
/*  32: 82 */     if (a < 1) {
/*  33: 83 */       throw new Exception("Shape parameter of Erlang distribution must be greater than 1!");
/*  34:    */     }
/*  35: 85 */     double product = 1.0D;
/*  36: 86 */     for (int i = 1; i <= a; i++) {
/*  37: 87 */       product *= super.nextDouble();
/*  38:    */     }
/*  39: 89 */     return -Math.log(product);
/*  40:    */   }
/*  41:    */   
/*  42:    */   public double nextGamma(double a)
/*  43:    */     throws Exception
/*  44:    */   {
/*  45:113 */     if (a <= 0.0D) {
/*  46:114 */       throw new Exception("Shape parameter of Gamma distributionmust be greater than 0!");
/*  47:    */     }
/*  48:116 */     if (a == 1.0D) {
/*  49:117 */       return nextExponential();
/*  50:    */     }
/*  51:118 */     if (a < 1.0D)
/*  52:    */     {
/*  53:119 */       double b = 1.0D + Math.exp(-1.0D) * a;
/*  54:    */       double x;
/*  55:    */       double condition;
/*  56:    */       do
/*  57:    */       {
/*  58:121 */         double p = b * super.nextDouble();
/*  59:    */         double condition;
/*  60:122 */         if (p < 1.0D)
/*  61:    */         {
/*  62:123 */           double x = Math.exp(Math.log(p) / a);
/*  63:124 */           condition = x;
/*  64:    */         }
/*  65:    */         else
/*  66:    */         {
/*  67:127 */           x = -Math.log((b - p) / a);
/*  68:128 */           condition = (1.0D - a) * Math.log(x);
/*  69:    */         }
/*  70:131 */       } while (nextExponential() < condition);
/*  71:132 */       return x;
/*  72:    */     }
/*  73:135 */     double b = a - 1.0D;double D = Math.sqrt(b);
/*  74:    */     double f1;
/*  75:    */     double D1;
/*  76:    */     double x2;
/*  77:    */     double x1;
/*  78:    */     double xl;
/*  79:    */     double f1;
/*  80:139 */     if (a <= 2.0D)
/*  81:    */     {
/*  82:140 */       double D1 = b / 2.0D;
/*  83:141 */       double x1 = 0.0D;
/*  84:142 */       double x2 = D1;
/*  85:143 */       double xl = -1.0D;
/*  86:144 */       f1 = 0.0D;
/*  87:    */     }
/*  88:    */     else
/*  89:    */     {
/*  90:147 */       D1 = D - 0.5D;
/*  91:148 */       x2 = b - D1;
/*  92:149 */       x1 = x2 - D1;
/*  93:150 */       xl = 1.0D - b / x1;
/*  94:151 */       f1 = Math.exp(b * Math.log(x1 / b) + 2.0D * D1);
/*  95:    */     }
/*  96:154 */     double f2 = Math.exp(b * Math.log(x2 / b) + D1);
/*  97:155 */     double x4 = b + D;
/*  98:156 */     double x5 = x4 + D;
/*  99:157 */     double xr = 1.0D - b / x5;
/* 100:158 */     double f4 = Math.exp(b * Math.log(x4 / b) - D);
/* 101:159 */     double f5 = Math.exp(b * Math.log(x5 / b) - 2.0D * D);
/* 102:160 */     double p1 = 2.0D * f4 * D;
/* 103:161 */     double p2 = 2.0D * f2 * D1 + p1;
/* 104:162 */     double p3 = f5 / xr + p2;
/* 105:163 */     double p4 = -f1 / xl + p3;
/* 106:    */     
/* 107:    */ 
/* 108:166 */     double w = 1.7976931348623157E+308D;double x = b;
/* 109:167 */     while ((Math.log(w) > b * Math.log(x / b) + b - x) || (x < 0.0D))
/* 110:    */     {
/* 111:168 */       double u = super.nextDouble() * p4;
/* 112:169 */       if (u <= p1)
/* 113:    */       {
/* 114:170 */         w = u / D - f4;
/* 115:171 */         if (w <= 0.0D) {
/* 116:171 */           return b + u / f4;
/* 117:    */         }
/* 118:172 */         if (w <= f5) {
/* 119:172 */           return x4 + w * D / f5;
/* 120:    */         }
/* 121:174 */         double v = super.nextDouble();
/* 122:175 */         x = x4 + v * D;
/* 123:176 */         double xp = 2.0D * x4 - x;
/* 124:178 */         if (w >= f4 + (f4 - 1.0D) * (x - x4) / (x4 - b)) {
/* 125:179 */           return xp;
/* 126:    */         }
/* 127:180 */         if (w <= f4 + (b / x4 - 1.0D) * f4 * (x - x4)) {
/* 128:181 */           return x;
/* 129:    */         }
/* 130:182 */         if ((w >= 2.0D * f4 - 1.0D) && (w >= 2.0D * f4 - Math.exp(b * Math.log(xp / b) + b - xp))) {
/* 131:185 */           return xp;
/* 132:    */         }
/* 133:    */       }
/* 134:187 */       else if (u <= p2)
/* 135:    */       {
/* 136:188 */         w = (u - p1) / D1 - f2;
/* 137:189 */         if (w <= 0.0D) {
/* 138:189 */           return b - (u - p1) / f2;
/* 139:    */         }
/* 140:190 */         if (w <= f1) {
/* 141:190 */           return x1 + w * D1 / f1;
/* 142:    */         }
/* 143:192 */         double v = super.nextDouble();
/* 144:193 */         x = x1 + v * D1;
/* 145:194 */         double xp = 2.0D * x2 - x;
/* 146:196 */         if (w >= f2 + (f2 - 1.0D) * (x - x2) / (x2 - b)) {
/* 147:197 */           return xp;
/* 148:    */         }
/* 149:198 */         if (w <= f2 * (x - x1) / D1) {
/* 150:199 */           return x;
/* 151:    */         }
/* 152:200 */         if ((w >= 2.0D * f2 - 1.0D) && (w >= 2.0D * f2 - Math.exp(b * Math.log(xp / b) + b - xp))) {
/* 153:203 */           return xp;
/* 154:    */         }
/* 155:    */       }
/* 156:205 */       else if (u < p3)
/* 157:    */       {
/* 158:206 */         w = super.nextDouble();
/* 159:207 */         u = (p3 - u) / (p3 - p2);
/* 160:208 */         x = x5 - Math.log(u) / xr;
/* 161:209 */         if (w <= (xr * (x5 - x) + 1.0D) / u) {
/* 162:209 */           return x;
/* 163:    */         }
/* 164:210 */         w = w * f5 * u;
/* 165:    */       }
/* 166:    */       else
/* 167:    */       {
/* 168:213 */         w = super.nextDouble();
/* 169:214 */         u = (p4 - u) / (p4 - p3);
/* 170:215 */         x = x1 - Math.log(u) / xl;
/* 171:216 */         if (x >= 0.0D)
/* 172:    */         {
/* 173:217 */           if (w <= (xl * (x1 - x) + 1.0D) / u) {
/* 174:217 */             return x;
/* 175:    */           }
/* 176:218 */           w = w * f1 * u;
/* 177:    */         }
/* 178:    */       }
/* 179:    */     }
/* 180:222 */     return x;
/* 181:    */   }
/* 182:    */   
/* 183:    */   public String getRevision()
/* 184:    */   {
/* 185:232 */     return RevisionUtils.extract("$Revision: 8034 $");
/* 186:    */   }
/* 187:    */   
/* 188:    */   public static void main(String[] ops)
/* 189:    */   {
/* 190:243 */     int n = Integer.parseInt(ops[0]);
/* 191:244 */     if (n <= 0) {
/* 192:245 */       n = 10;
/* 193:    */     }
/* 194:246 */     long seed = Long.parseLong(ops[1]);
/* 195:247 */     if (seed <= 0L) {
/* 196:248 */       seed = 45L;
/* 197:    */     }
/* 198:249 */     RandomVariates var = new RandomVariates(seed);
/* 199:250 */     double[] varb = new double[n];
/* 200:    */     try
/* 201:    */     {
/* 202:253 */       System.out.println("Generate " + n + " values with std. exp dist:");
/* 203:254 */       for (int i = 0; i < n; i++)
/* 204:    */       {
/* 205:255 */         varb[i] = var.nextExponential();
/* 206:256 */         System.out.print("[" + i + "] " + varb[i] + ", ");
/* 207:    */       }
/* 208:259 */       System.out.println("\nMean is " + Utils.mean(varb) + ", Variance is " + Utils.variance(varb) + "\n\nGenerate " + n + " values with" + " std. Erlang-5 dist:");
/* 209:264 */       for (int i = 0; i < n; i++)
/* 210:    */       {
/* 211:265 */         varb[i] = var.nextErlang(5);
/* 212:266 */         System.out.print("[" + i + "] " + varb[i] + ", ");
/* 213:    */       }
/* 214:269 */       System.out.println("\nMean is " + Utils.mean(varb) + ", Variance is " + Utils.variance(varb) + "\n\nGenerate " + n + " values with" + " std. Gamma(4.5) dist:");
/* 215:274 */       for (int i = 0; i < n; i++)
/* 216:    */       {
/* 217:275 */         varb[i] = var.nextGamma(4.5D);
/* 218:276 */         System.out.print("[" + i + "] " + varb[i] + ", ");
/* 219:    */       }
/* 220:279 */       System.out.println("\nMean is " + Utils.mean(varb) + ", Variance is " + Utils.variance(varb) + "\n\nGenerate " + n + " values with" + " std. Gamma(0.5) dist:");
/* 221:284 */       for (int i = 0; i < n; i++)
/* 222:    */       {
/* 223:285 */         varb[i] = var.nextGamma(0.5D);
/* 224:286 */         System.out.print("[" + i + "] " + varb[i] + ", ");
/* 225:    */       }
/* 226:289 */       System.out.println("\nMean is " + Utils.mean(varb) + ", Variance is " + Utils.variance(varb) + "\n\nGenerate " + n + " values with" + " std. Gaussian(5, 2) dist:");
/* 227:294 */       for (int i = 0; i < n; i++)
/* 228:    */       {
/* 229:295 */         varb[i] = (var.nextGaussian() * 2.0D + 5.0D);
/* 230:296 */         System.out.print("[" + i + "] " + varb[i] + ", ");
/* 231:    */       }
/* 232:298 */       System.out.println("\nMean is " + Utils.mean(varb) + ", Variance is " + Utils.variance(varb) + "\n");
/* 233:    */     }
/* 234:    */     catch (Exception e)
/* 235:    */     {
/* 236:302 */       e.printStackTrace();
/* 237:    */     }
/* 238:    */   }
/* 239:    */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.RandomVariates
 * JD-Core Version:    0.7.0.1
 */