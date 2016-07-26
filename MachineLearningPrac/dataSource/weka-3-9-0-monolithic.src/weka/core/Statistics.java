/*    1:     */ package weka.core;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ 
/*    5:     */ public class Statistics
/*    6:     */   implements RevisionHandler
/*    7:     */ {
/*    8:     */   protected static final double MACHEP = 1.110223024625157E-016D;
/*    9:     */   protected static final double MAXLOG = 709.78271289338397D;
/*   10:     */   protected static final double MINLOG = -745.13321910194122D;
/*   11:     */   protected static final double MAXGAM = 171.62437695630271D;
/*   12:     */   protected static final double SQTPI = 2.506628274631001D;
/*   13:     */   protected static final double SQRTH = 0.7071067811865476D;
/*   14:     */   protected static final double LOGPI = 1.1447298858494D;
/*   15:     */   protected static final double big = 4503599627370496.0D;
/*   16:     */   protected static final double biginv = 2.220446049250313E-016D;
/*   17:  40 */   protected static final double[] P0 = { -59.963350101410789D, 98.001075418599967D, -56.676285746907027D, 13.931260938727968D, -1.239165838673813D };
/*   18:  43 */   protected static final double[] Q0 = { 1.954488583381418D, 4.676279128988815D, 86.360242139089053D, -225.46268785411937D, 200.26021238006066D, -82.037225616833339D, 15.90562251262117D, -1.1833162112133D };
/*   19:  54 */   protected static final double[] P1 = { 4.055448923059625D, 31.525109459989388D, 57.162819224642128D, 44.080507389320083D, 14.684956192885803D, 2.186633068507903D, -0.1402560791713545D, -0.03504246268278482D, -0.0008574567851546855D };
/*   20:  59 */   protected static final double[] Q1 = { 15.779988325646675D, 45.390763512887922D, 41.317203825467203D, 15.04253856929075D, 2.504649462083094D, -0.1421829228547878D, -0.03808064076915783D, -0.0009332594808954574D };
/*   21:  70 */   protected static final double[] P2 = { 3.23774891776946D, 6.915228890689842D, 3.938810252924744D, 1.333034608158076D, 0.2014853895491791D, 0.012371663481782D, 0.0003015815535082354D, 2.658069746867376E-006D, 6.239745391849833E-009D };
/*   22:  75 */   protected static final double[] Q2 = { 6.02427039364742D, 3.679835638561609D, 1.377020994890813D, 0.2162369935944966D, 0.01342040060885432D, 0.0003280144646821277D, 2.892478647453807E-006D, 6.790194080099813E-009D };
/*   23:     */   
/*   24:     */   public static double binomialStandardError(double p, int n)
/*   25:     */   {
/*   26:  91 */     if (n == 0) {
/*   27:  92 */       return 0.0D;
/*   28:     */     }
/*   29:  94 */     return Math.sqrt(p * (1.0D - p) / n);
/*   30:     */   }
/*   31:     */   
/*   32:     */   public static double chiSquaredProbability(double x, double v)
/*   33:     */   {
/*   34: 108 */     if ((x < 0.0D) || (v < 1.0D)) {
/*   35: 109 */       return 0.0D;
/*   36:     */     }
/*   37: 111 */     return incompleteGammaComplement(v / 2.0D, x / 2.0D);
/*   38:     */   }
/*   39:     */   
/*   40:     */   public static double FProbability(double F, int df1, int df2)
/*   41:     */   {
/*   42: 124 */     return incompleteBeta(df2 / 2.0D, df1 / 2.0D, df2 / (df2 + df1 * F));
/*   43:     */   }
/*   44:     */   
/*   45:     */   public static double normalProbability(double a)
/*   46:     */   {
/*   47: 155 */     double x = a * 0.7071067811865476D;
/*   48: 156 */     double z = Math.abs(x);
/*   49:     */     double y;
/*   50:     */     double y;
/*   51: 158 */     if (z < 0.7071067811865476D)
/*   52:     */     {
/*   53: 159 */       y = 0.5D + 0.5D * errorFunction(x);
/*   54:     */     }
/*   55:     */     else
/*   56:     */     {
/*   57: 161 */       y = 0.5D * errorFunctionComplemented(z);
/*   58: 162 */       if (x > 0.0D) {
/*   59: 163 */         y = 1.0D - y;
/*   60:     */       }
/*   61:     */     }
/*   62: 166 */     return y;
/*   63:     */   }
/*   64:     */   
/*   65:     */   public static double normalInverse(double y0)
/*   66:     */   {
/*   67: 190 */     double s2pi = Math.sqrt(6.283185307179586D);
/*   68: 192 */     if (y0 <= 0.0D) {
/*   69: 193 */       throw new IllegalArgumentException();
/*   70:     */     }
/*   71: 195 */     if (y0 >= 1.0D) {
/*   72: 196 */       throw new IllegalArgumentException();
/*   73:     */     }
/*   74: 198 */     int code = 1;
/*   75: 199 */     double y = y0;
/*   76: 200 */     if (y > 0.864664716763387D)
/*   77:     */     {
/*   78: 201 */       y = 1.0D - y;
/*   79: 202 */       code = 0;
/*   80:     */     }
/*   81: 205 */     if (y > 0.135335283236613D)
/*   82:     */     {
/*   83: 206 */       y -= 0.5D;
/*   84: 207 */       double y2 = y * y;
/*   85: 208 */       double x = y + y * (y2 * polevl(y2, P0, 4) / p1evl(y2, Q0, 8));
/*   86: 209 */       x *= s2pi;
/*   87: 210 */       return x;
/*   88:     */     }
/*   89: 213 */     double x = Math.sqrt(-2.0D * Math.log(y));
/*   90: 214 */     double x0 = x - Math.log(x) / x;
/*   91:     */     
/*   92: 216 */     double z = 1.0D / x;
/*   93:     */     double x1;
/*   94:     */     double x1;
/*   95: 217 */     if (x < 8.0D) {
/*   96: 218 */       x1 = z * polevl(z, P1, 8) / p1evl(z, Q1, 8);
/*   97:     */     } else {
/*   98: 220 */       x1 = z * polevl(z, P2, 8) / p1evl(z, Q2, 8);
/*   99:     */     }
/*  100: 222 */     x = x0 - x1;
/*  101: 223 */     if (code != 0) {
/*  102: 224 */       x = -x;
/*  103:     */     }
/*  104: 226 */     return x;
/*  105:     */   }
/*  106:     */   
/*  107:     */   public static double lnGamma(double x)
/*  108:     */   {
/*  109: 239 */     double[] A = { 0.0008116141674705085D, -0.0005950619042843014D, 0.0007936503404577169D, -0.002777777777300997D, 0.0833333333333332D };
/*  110:     */     
/*  111:     */ 
/*  112: 242 */     double[] B = { -1378.2515256912086D, -38801.631513463784D, -331612.99273887119D, -1162370.9749276231D, -1721737.0082083966D, -853555.66424576542D };
/*  113:     */     
/*  114:     */ 
/*  115: 245 */     double[] C = { -351.81570143652345D, -17064.210665188115D, -220528.59055385445D, -1139334.4436798252D, -2532523.0717758294D, -2018891.4143353277D };
/*  116: 251 */     if (x < -34.0D)
/*  117:     */     {
/*  118: 252 */       double q = -x;
/*  119: 253 */       double w = lnGamma(q);
/*  120: 254 */       double p = Math.floor(q);
/*  121: 255 */       if (p == q) {
/*  122: 256 */         throw new ArithmeticException("lnGamma: Overflow");
/*  123:     */       }
/*  124: 258 */       double z = q - p;
/*  125: 259 */       if (z > 0.5D)
/*  126:     */       {
/*  127: 260 */         p += 1.0D;
/*  128: 261 */         z = p - q;
/*  129:     */       }
/*  130: 263 */       z = q * Math.sin(3.141592653589793D * z);
/*  131: 264 */       if (z == 0.0D) {
/*  132: 265 */         throw new ArithmeticException("lnGamma: Overflow");
/*  133:     */       }
/*  134: 267 */       z = 1.1447298858494D - Math.log(z) - w;
/*  135: 268 */       return z;
/*  136:     */     }
/*  137: 271 */     if (x < 13.0D)
/*  138:     */     {
/*  139: 272 */       double z = 1.0D;
/*  140: 273 */       while (x >= 3.0D)
/*  141:     */       {
/*  142: 274 */         x -= 1.0D;
/*  143: 275 */         z *= x;
/*  144:     */       }
/*  145: 277 */       while (x < 2.0D)
/*  146:     */       {
/*  147: 278 */         if (x == 0.0D) {
/*  148: 279 */           throw new ArithmeticException("lnGamma: Overflow");
/*  149:     */         }
/*  150: 281 */         z /= x;
/*  151: 282 */         x += 1.0D;
/*  152:     */       }
/*  153: 284 */       if (z < 0.0D) {
/*  154: 285 */         z = -z;
/*  155:     */       }
/*  156: 287 */       if (x == 2.0D) {
/*  157: 288 */         return Math.log(z);
/*  158:     */       }
/*  159: 290 */       x -= 2.0D;
/*  160: 291 */       double p = x * polevl(x, B, 5) / p1evl(x, C, 6);
/*  161: 292 */       return Math.log(z) + p;
/*  162:     */     }
/*  163: 295 */     if (x > 2.556348E+305D) {
/*  164: 296 */       throw new ArithmeticException("lnGamma: Overflow");
/*  165:     */     }
/*  166: 299 */     double q = (x - 0.5D) * Math.log(x) - x + 0.9189385332046728D;
/*  167: 301 */     if (x > 100000000.0D) {
/*  168: 302 */       return q;
/*  169:     */     }
/*  170: 305 */     double p = 1.0D / (x * x);
/*  171: 306 */     if (x >= 1000.0D) {
/*  172: 307 */       q += ((0.0007936507936507937D * p - 0.002777777777777778D) * p + 0.08333333333333333D) / x;
/*  173:     */     } else {
/*  174: 310 */       q += polevl(p, A, 4) / x;
/*  175:     */     }
/*  176: 312 */     return q;
/*  177:     */   }
/*  178:     */   
/*  179:     */   public static double errorFunction(double x)
/*  180:     */   {
/*  181: 342 */     double[] T = { 9.604973739870516D, 90.026019720384269D, 2232.0053459468431D, 7003.3251411280507D, 55592.301301039493D };
/*  182:     */     
/*  183:     */ 
/*  184: 345 */     double[] U = { 33.561714164750313D, 521.35794978015269D, 4594.3238297098014D, 22629.000061389095D, 49267.394260863592D };
/*  185: 351 */     if (Math.abs(x) > 1.0D) {
/*  186: 352 */       return 1.0D - errorFunctionComplemented(x);
/*  187:     */     }
/*  188: 354 */     double z = x * x;
/*  189: 355 */     double y = x * polevl(z, T, 4) / p1evl(z, U, 5);
/*  190: 356 */     return y;
/*  191:     */   }
/*  192:     */   
/*  193:     */   public static double errorFunctionComplemented(double a)
/*  194:     */   {
/*  195: 388 */     double[] P = { 2.461969814735305E-010D, 0.5641895648310689D, 7.463210564422699D, 48.637197098568137D, 196.5208329560771D, 526.44519499547732D, 934.52852717195765D, 1027.5518868951572D, 557.53533536939938D };
/*  196:     */     
/*  197:     */ 
/*  198:     */ 
/*  199:     */ 
/*  200: 393 */     double[] Q = { 13.228195115474499D, 86.707214088598974D, 354.93777888781989D, 975.70850174320549D, 1823.9091668790973D, 2246.3376081871097D, 1656.6630919416134D, 557.53534081772773D };
/*  201:     */     
/*  202:     */ 
/*  203:     */ 
/*  204:     */ 
/*  205:     */ 
/*  206:     */ 
/*  207: 400 */     double[] R = { 0.5641895835477551D, 1.275366707599781D, 5.019050422511805D, 6.160210979930536D, 7.40974269950449D, 2.978866653721002D };
/*  208:     */     
/*  209:     */ 
/*  210: 403 */     double[] S = { 2.260528632201173D, 9.396035249380015D, 12.048953980809666D, 17.081445074756591D, 9.608968090632859D, 3.369076451000815D };
/*  211:     */     double x;
/*  212:     */     double x;
/*  213: 409 */     if (a < 0.0D) {
/*  214: 410 */       x = -a;
/*  215:     */     } else {
/*  216: 412 */       x = a;
/*  217:     */     }
/*  218: 415 */     if (x < 1.0D) {
/*  219: 416 */       return 1.0D - errorFunction(a);
/*  220:     */     }
/*  221: 419 */     double z = -a * a;
/*  222: 421 */     if (z < -709.78271289338397D)
/*  223:     */     {
/*  224: 422 */       if (a < 0.0D) {
/*  225: 423 */         return 2.0D;
/*  226:     */       }
/*  227: 425 */       return 0.0D;
/*  228:     */     }
/*  229: 429 */     z = Math.exp(z);
/*  230:     */     double q;
/*  231:     */     double p;
/*  232:     */     double q;
/*  233: 431 */     if (x < 8.0D)
/*  234:     */     {
/*  235: 432 */       double p = polevl(x, P, 8);
/*  236: 433 */       q = p1evl(x, Q, 8);
/*  237:     */     }
/*  238:     */     else
/*  239:     */     {
/*  240: 435 */       p = polevl(x, R, 5);
/*  241: 436 */       q = p1evl(x, S, 6);
/*  242:     */     }
/*  243: 439 */     double y = z * p / q;
/*  244: 441 */     if (a < 0.0D) {
/*  245: 442 */       y = 2.0D - y;
/*  246:     */     }
/*  247: 445 */     if (y == 0.0D)
/*  248:     */     {
/*  249: 446 */       if (a < 0.0D) {
/*  250: 447 */         return 2.0D;
/*  251:     */       }
/*  252: 449 */       return 0.0D;
/*  253:     */     }
/*  254: 452 */     return y;
/*  255:     */   }
/*  256:     */   
/*  257:     */   public static double p1evl(double x, double[] coef, int N)
/*  258:     */   {
/*  259: 484 */     double ans = x + coef[0];
/*  260: 486 */     for (int i = 1; i < N; i++) {
/*  261: 487 */       ans = ans * x + coef[i];
/*  262:     */     }
/*  263: 490 */     return ans;
/*  264:     */   }
/*  265:     */   
/*  266:     */   public static double polevl(double x, double[] coef, int N)
/*  267:     */   {
/*  268: 516 */     double ans = coef[0];
/*  269: 518 */     for (int i = 1; i <= N; i++) {
/*  270: 519 */       ans = ans * x + coef[i];
/*  271:     */     }
/*  272: 522 */     return ans;
/*  273:     */   }
/*  274:     */   
/*  275:     */   public static double incompleteGamma(double a, double x)
/*  276:     */   {
/*  277: 535 */     if ((x <= 0.0D) || (a <= 0.0D)) {
/*  278: 536 */       return 0.0D;
/*  279:     */     }
/*  280: 539 */     if ((x > 1.0D) && (x > a)) {
/*  281: 540 */       return 1.0D - incompleteGammaComplement(a, x);
/*  282:     */     }
/*  283: 544 */     double ax = a * Math.log(x) - x - lnGamma(a);
/*  284: 545 */     if (ax < -709.78271289338397D) {
/*  285: 546 */       return 0.0D;
/*  286:     */     }
/*  287: 549 */     ax = Math.exp(ax);
/*  288:     */     
/*  289:     */ 
/*  290: 552 */     double r = a;
/*  291: 553 */     double c = 1.0D;
/*  292: 554 */     double ans = 1.0D;
/*  293:     */     do
/*  294:     */     {
/*  295: 557 */       r += 1.0D;
/*  296: 558 */       c *= x / r;
/*  297: 559 */       ans += c;
/*  298: 560 */     } while (c / ans > 1.110223024625157E-016D);
/*  299: 562 */     return ans * ax / a;
/*  300:     */   }
/*  301:     */   
/*  302:     */   public static double incompleteGammaComplement(double a, double x)
/*  303:     */   {
/*  304: 576 */     if ((x <= 0.0D) || (a <= 0.0D)) {
/*  305: 577 */       return 1.0D;
/*  306:     */     }
/*  307: 580 */     if ((x < 1.0D) || (x < a)) {
/*  308: 581 */       return 1.0D - incompleteGamma(a, x);
/*  309:     */     }
/*  310: 584 */     double ax = a * Math.log(x) - x - lnGamma(a);
/*  311: 585 */     if (ax < -709.78271289338397D) {
/*  312: 586 */       return 0.0D;
/*  313:     */     }
/*  314: 589 */     ax = Math.exp(ax);
/*  315:     */     
/*  316:     */ 
/*  317: 592 */     double y = 1.0D - a;
/*  318: 593 */     double z = x + y + 1.0D;
/*  319: 594 */     double c = 0.0D;
/*  320: 595 */     double pkm2 = 1.0D;
/*  321: 596 */     double qkm2 = x;
/*  322: 597 */     double pkm1 = x + 1.0D;
/*  323: 598 */     double qkm1 = z * x;
/*  324: 599 */     double ans = pkm1 / qkm1;
/*  325:     */     double t;
/*  326:     */     do
/*  327:     */     {
/*  328: 602 */       c += 1.0D;
/*  329: 603 */       y += 1.0D;
/*  330: 604 */       z += 2.0D;
/*  331: 605 */       double yc = y * c;
/*  332: 606 */       double pk = pkm1 * z - pkm2 * yc;
/*  333: 607 */       double qk = qkm1 * z - qkm2 * yc;
/*  334: 608 */       if (qk != 0.0D)
/*  335:     */       {
/*  336: 609 */         double r = pk / qk;
/*  337: 610 */         double t = Math.abs((ans - r) / r);
/*  338: 611 */         ans = r;
/*  339:     */       }
/*  340:     */       else
/*  341:     */       {
/*  342: 613 */         t = 1.0D;
/*  343:     */       }
/*  344: 616 */       pkm2 = pkm1;
/*  345: 617 */       pkm1 = pk;
/*  346: 618 */       qkm2 = qkm1;
/*  347: 619 */       qkm1 = qk;
/*  348: 620 */       if (Math.abs(pk) > 4503599627370496.0D)
/*  349:     */       {
/*  350: 621 */         pkm2 *= 2.220446049250313E-016D;
/*  351: 622 */         pkm1 *= 2.220446049250313E-016D;
/*  352: 623 */         qkm2 *= 2.220446049250313E-016D;
/*  353: 624 */         qkm1 *= 2.220446049250313E-016D;
/*  354:     */       }
/*  355: 626 */     } while (t > 1.110223024625157E-016D);
/*  356: 628 */     return ans * ax;
/*  357:     */   }
/*  358:     */   
/*  359:     */   public static double gamma(double x)
/*  360:     */   {
/*  361: 636 */     double[] P = { 0.0001601195224767519D, 0.001191351470065864D, 0.01042137975617616D, 0.04763678004571372D, 0.207448227648436D, 0.4942148268014971D, 1.0D };
/*  362:     */     
/*  363:     */ 
/*  364:     */ 
/*  365: 640 */     double[] Q = { -2.315818733241201E-005D, 0.0005396055804933034D, -0.004456419138517973D, 0.01181397852220604D, 0.03582363986054987D, -0.2345917957182434D, 0.0714304917030273D, 1.0D };
/*  366:     */     
/*  367:     */ 
/*  368:     */ 
/*  369:     */ 
/*  370:     */ 
/*  371: 646 */     double q = Math.abs(x);
/*  372: 648 */     if (q > 33.0D)
/*  373:     */     {
/*  374: 649 */       if (x < 0.0D)
/*  375:     */       {
/*  376: 650 */         double p = Math.floor(q);
/*  377: 651 */         if (p == q) {
/*  378: 652 */           throw new ArithmeticException("gamma: overflow");
/*  379:     */         }
/*  380: 654 */         double z = q - p;
/*  381: 655 */         if (z > 0.5D)
/*  382:     */         {
/*  383: 656 */           p += 1.0D;
/*  384: 657 */           z = q - p;
/*  385:     */         }
/*  386: 659 */         z = q * Math.sin(3.141592653589793D * z);
/*  387: 660 */         if (z == 0.0D) {
/*  388: 661 */           throw new ArithmeticException("gamma: overflow");
/*  389:     */         }
/*  390: 663 */         z = Math.abs(z);
/*  391: 664 */         z = 3.141592653589793D / (z * stirlingFormula(q));
/*  392:     */         
/*  393: 666 */         return -z;
/*  394:     */       }
/*  395: 668 */       return stirlingFormula(x);
/*  396:     */     }
/*  397: 672 */     double z = 1.0D;
/*  398: 673 */     while (x >= 3.0D)
/*  399:     */     {
/*  400: 674 */       x -= 1.0D;
/*  401: 675 */       z *= x;
/*  402:     */     }
/*  403: 678 */     while (x < 0.0D)
/*  404:     */     {
/*  405: 679 */       if (x == 0.0D) {
/*  406: 680 */         throw new ArithmeticException("gamma: singular");
/*  407:     */       }
/*  408: 681 */       if (x > -1.E-009D) {
/*  409: 682 */         return z / ((1.0D + 0.5772156649015329D * x) * x);
/*  410:     */       }
/*  411: 684 */       z /= x;
/*  412: 685 */       x += 1.0D;
/*  413:     */     }
/*  414: 688 */     while (x < 2.0D)
/*  415:     */     {
/*  416: 689 */       if (x == 0.0D) {
/*  417: 690 */         throw new ArithmeticException("gamma: singular");
/*  418:     */       }
/*  419: 691 */       if (x < 1.E-009D) {
/*  420: 692 */         return z / ((1.0D + 0.5772156649015329D * x) * x);
/*  421:     */       }
/*  422: 694 */       z /= x;
/*  423: 695 */       x += 1.0D;
/*  424:     */     }
/*  425: 698 */     if ((x == 2.0D) || (x == 3.0D)) {
/*  426: 699 */       return z;
/*  427:     */     }
/*  428: 702 */     x -= 2.0D;
/*  429: 703 */     double p = polevl(x, P, 6);
/*  430: 704 */     q = polevl(x, Q, 7);
/*  431: 705 */     return z * p / q;
/*  432:     */   }
/*  433:     */   
/*  434:     */   public static double stirlingFormula(double x)
/*  435:     */   {
/*  436: 714 */     double[] STIR = { 0.0007873113957930937D, -0.0002295499616133781D, -0.002681326178057812D, 0.003472222216054587D, 0.0833333333333482D };
/*  437:     */     
/*  438:     */ 
/*  439: 717 */     double MAXSTIR = 143.01607999999999D;
/*  440:     */     
/*  441: 719 */     double w = 1.0D / x;
/*  442: 720 */     double y = Math.exp(x);
/*  443:     */     
/*  444: 722 */     w = 1.0D + w * polevl(w, STIR, 4);
/*  445: 724 */     if (x > MAXSTIR)
/*  446:     */     {
/*  447: 726 */       double v = Math.pow(x, 0.5D * x - 0.25D);
/*  448: 727 */       y = v * (v / y);
/*  449:     */     }
/*  450:     */     else
/*  451:     */     {
/*  452: 729 */       y = Math.pow(x, x - 0.5D) / y;
/*  453:     */     }
/*  454: 731 */     y = 2.506628274631001D * y * w;
/*  455: 732 */     return y;
/*  456:     */   }
/*  457:     */   
/*  458:     */   public static double incompleteBeta(double aa, double bb, double xx)
/*  459:     */   {
/*  460: 747 */     if ((aa <= 0.0D) || (bb <= 0.0D)) {
/*  461: 748 */       throw new ArithmeticException("ibeta: Domain error!");
/*  462:     */     }
/*  463: 751 */     if ((xx <= 0.0D) || (xx >= 1.0D))
/*  464:     */     {
/*  465: 752 */       if (xx == 0.0D) {
/*  466: 753 */         return 0.0D;
/*  467:     */       }
/*  468: 755 */       if (xx == 1.0D) {
/*  469: 756 */         return 1.0D;
/*  470:     */       }
/*  471: 758 */       throw new ArithmeticException("ibeta: Domain error!");
/*  472:     */     }
/*  473: 761 */     boolean flag = false;
/*  474: 762 */     if ((bb * xx <= 1.0D) && (xx <= 0.95D))
/*  475:     */     {
/*  476: 763 */       double t = powerSeries(aa, bb, xx);
/*  477: 764 */       return t;
/*  478:     */     }
/*  479: 767 */     double w = 1.0D - xx;
/*  480:     */     double x;
/*  481:     */     double a;
/*  482:     */     double b;
/*  483:     */     double xc;
/*  484:     */     double x;
/*  485: 770 */     if (xx > aa / (aa + bb))
/*  486:     */     {
/*  487: 771 */       flag = true;
/*  488: 772 */       double a = bb;
/*  489: 773 */       double b = aa;
/*  490: 774 */       double xc = xx;
/*  491: 775 */       x = w;
/*  492:     */     }
/*  493:     */     else
/*  494:     */     {
/*  495: 777 */       a = aa;
/*  496: 778 */       b = bb;
/*  497: 779 */       xc = w;
/*  498: 780 */       x = xx;
/*  499:     */     }
/*  500: 783 */     if ((flag) && (b * x <= 1.0D) && (x <= 0.95D))
/*  501:     */     {
/*  502: 784 */       double t = powerSeries(a, b, x);
/*  503: 785 */       if (t <= 1.110223024625157E-016D) {
/*  504: 786 */         t = 0.9999999999999999D;
/*  505:     */       } else {
/*  506: 788 */         t = 1.0D - t;
/*  507:     */       }
/*  508: 790 */       return t;
/*  509:     */     }
/*  510: 794 */     double y = x * (a + b - 2.0D) - (a - 1.0D);
/*  511: 795 */     if (y < 0.0D) {
/*  512: 796 */       w = incompleteBetaFraction1(a, b, x);
/*  513:     */     } else {
/*  514: 798 */       w = incompleteBetaFraction2(a, b, x) / xc;
/*  515:     */     }
/*  516: 805 */     y = a * Math.log(x);
/*  517: 806 */     double t = b * Math.log(xc);
/*  518: 807 */     if ((a + b < 171.62437695630271D) && (Math.abs(y) < 709.78271289338397D) && (Math.abs(t) < 709.78271289338397D))
/*  519:     */     {
/*  520: 808 */       t = Math.pow(xc, b);
/*  521: 809 */       t *= Math.pow(x, a);
/*  522: 810 */       t /= a;
/*  523: 811 */       t *= w;
/*  524: 812 */       t *= gamma(a + b) / (gamma(a) * gamma(b));
/*  525: 813 */       if (flag) {
/*  526: 814 */         if (t <= 1.110223024625157E-016D) {
/*  527: 815 */           t = 0.9999999999999999D;
/*  528:     */         } else {
/*  529: 817 */           t = 1.0D - t;
/*  530:     */         }
/*  531:     */       }
/*  532: 820 */       return t;
/*  533:     */     }
/*  534: 823 */     y += t + lnGamma(a + b) - lnGamma(a) - lnGamma(b);
/*  535: 824 */     y += Math.log(w / a);
/*  536: 825 */     if (y < -745.13321910194122D) {
/*  537: 826 */       t = 0.0D;
/*  538:     */     } else {
/*  539: 828 */       t = Math.exp(y);
/*  540:     */     }
/*  541: 831 */     if (flag) {
/*  542: 832 */       if (t <= 1.110223024625157E-016D) {
/*  543: 833 */         t = 0.9999999999999999D;
/*  544:     */       } else {
/*  545: 835 */         t = 1.0D - t;
/*  546:     */       }
/*  547:     */     }
/*  548: 838 */     return t;
/*  549:     */   }
/*  550:     */   
/*  551:     */   public static double incompleteBetaFraction1(double a, double b, double x)
/*  552:     */   {
/*  553: 851 */     double k1 = a;
/*  554: 852 */     double k2 = a + b;
/*  555: 853 */     double k3 = a;
/*  556: 854 */     double k4 = a + 1.0D;
/*  557: 855 */     double k5 = 1.0D;
/*  558: 856 */     double k6 = b - 1.0D;
/*  559: 857 */     double k7 = k4;
/*  560: 858 */     double k8 = a + 2.0D;
/*  561:     */     
/*  562: 860 */     double pkm2 = 0.0D;
/*  563: 861 */     double qkm2 = 1.0D;
/*  564: 862 */     double pkm1 = 1.0D;
/*  565: 863 */     double qkm1 = 1.0D;
/*  566: 864 */     double ans = 1.0D;
/*  567: 865 */     double r = 1.0D;
/*  568: 866 */     int n = 0;
/*  569: 867 */     double thresh = 3.33066907387547E-016D;
/*  570:     */     do
/*  571:     */     {
/*  572: 869 */       double xk = -(x * k1 * k2) / (k3 * k4);
/*  573: 870 */       double pk = pkm1 + pkm2 * xk;
/*  574: 871 */       double qk = qkm1 + qkm2 * xk;
/*  575: 872 */       pkm2 = pkm1;
/*  576: 873 */       pkm1 = pk;
/*  577: 874 */       qkm2 = qkm1;
/*  578: 875 */       qkm1 = qk;
/*  579:     */       
/*  580: 877 */       xk = x * k5 * k6 / (k7 * k8);
/*  581: 878 */       pk = pkm1 + pkm2 * xk;
/*  582: 879 */       qk = qkm1 + qkm2 * xk;
/*  583: 880 */       pkm2 = pkm1;
/*  584: 881 */       pkm1 = pk;
/*  585: 882 */       qkm2 = qkm1;
/*  586: 883 */       qkm1 = qk;
/*  587: 885 */       if (qk != 0.0D) {
/*  588: 886 */         r = pk / qk;
/*  589:     */       }
/*  590:     */       double t;
/*  591: 888 */       if (r != 0.0D)
/*  592:     */       {
/*  593: 889 */         double t = Math.abs((ans - r) / r);
/*  594: 890 */         ans = r;
/*  595:     */       }
/*  596:     */       else
/*  597:     */       {
/*  598: 892 */         t = 1.0D;
/*  599:     */       }
/*  600: 895 */       if (t < thresh) {
/*  601: 896 */         return ans;
/*  602:     */       }
/*  603: 899 */       k1 += 1.0D;
/*  604: 900 */       k2 += 1.0D;
/*  605: 901 */       k3 += 2.0D;
/*  606: 902 */       k4 += 2.0D;
/*  607: 903 */       k5 += 1.0D;
/*  608: 904 */       k6 -= 1.0D;
/*  609: 905 */       k7 += 2.0D;
/*  610: 906 */       k8 += 2.0D;
/*  611: 908 */       if (Math.abs(qk) + Math.abs(pk) > 4503599627370496.0D)
/*  612:     */       {
/*  613: 909 */         pkm2 *= 2.220446049250313E-016D;
/*  614: 910 */         pkm1 *= 2.220446049250313E-016D;
/*  615: 911 */         qkm2 *= 2.220446049250313E-016D;
/*  616: 912 */         qkm1 *= 2.220446049250313E-016D;
/*  617:     */       }
/*  618: 914 */       if ((Math.abs(qk) < 2.220446049250313E-016D) || (Math.abs(pk) < 2.220446049250313E-016D))
/*  619:     */       {
/*  620: 915 */         pkm2 *= 4503599627370496.0D;
/*  621: 916 */         pkm1 *= 4503599627370496.0D;
/*  622: 917 */         qkm2 *= 4503599627370496.0D;
/*  623: 918 */         qkm1 *= 4503599627370496.0D;
/*  624:     */       }
/*  625: 920 */       n++;
/*  626: 920 */     } while (n < 300);
/*  627: 922 */     return ans;
/*  628:     */   }
/*  629:     */   
/*  630:     */   public static double incompleteBetaFraction2(double a, double b, double x)
/*  631:     */   {
/*  632: 935 */     double k1 = a;
/*  633: 936 */     double k2 = b - 1.0D;
/*  634: 937 */     double k3 = a;
/*  635: 938 */     double k4 = a + 1.0D;
/*  636: 939 */     double k5 = 1.0D;
/*  637: 940 */     double k6 = a + b;
/*  638: 941 */     double k7 = a + 1.0D;
/*  639:     */     
/*  640: 943 */     double k8 = a + 2.0D;
/*  641:     */     
/*  642: 945 */     double pkm2 = 0.0D;
/*  643: 946 */     double qkm2 = 1.0D;
/*  644: 947 */     double pkm1 = 1.0D;
/*  645: 948 */     double qkm1 = 1.0D;
/*  646: 949 */     double z = x / (1.0D - x);
/*  647: 950 */     double ans = 1.0D;
/*  648: 951 */     double r = 1.0D;
/*  649: 952 */     int n = 0;
/*  650: 953 */     double thresh = 3.33066907387547E-016D;
/*  651:     */     do
/*  652:     */     {
/*  653: 955 */       double xk = -(z * k1 * k2) / (k3 * k4);
/*  654: 956 */       double pk = pkm1 + pkm2 * xk;
/*  655: 957 */       double qk = qkm1 + qkm2 * xk;
/*  656: 958 */       pkm2 = pkm1;
/*  657: 959 */       pkm1 = pk;
/*  658: 960 */       qkm2 = qkm1;
/*  659: 961 */       qkm1 = qk;
/*  660:     */       
/*  661: 963 */       xk = z * k5 * k6 / (k7 * k8);
/*  662: 964 */       pk = pkm1 + pkm2 * xk;
/*  663: 965 */       qk = qkm1 + qkm2 * xk;
/*  664: 966 */       pkm2 = pkm1;
/*  665: 967 */       pkm1 = pk;
/*  666: 968 */       qkm2 = qkm1;
/*  667: 969 */       qkm1 = qk;
/*  668: 971 */       if (qk != 0.0D) {
/*  669: 972 */         r = pk / qk;
/*  670:     */       }
/*  671:     */       double t;
/*  672: 974 */       if (r != 0.0D)
/*  673:     */       {
/*  674: 975 */         double t = Math.abs((ans - r) / r);
/*  675: 976 */         ans = r;
/*  676:     */       }
/*  677:     */       else
/*  678:     */       {
/*  679: 978 */         t = 1.0D;
/*  680:     */       }
/*  681: 981 */       if (t < thresh) {
/*  682: 982 */         return ans;
/*  683:     */       }
/*  684: 985 */       k1 += 1.0D;
/*  685: 986 */       k2 -= 1.0D;
/*  686: 987 */       k3 += 2.0D;
/*  687: 988 */       k4 += 2.0D;
/*  688: 989 */       k5 += 1.0D;
/*  689: 990 */       k6 += 1.0D;
/*  690: 991 */       k7 += 2.0D;
/*  691: 992 */       k8 += 2.0D;
/*  692: 994 */       if (Math.abs(qk) + Math.abs(pk) > 4503599627370496.0D)
/*  693:     */       {
/*  694: 995 */         pkm2 *= 2.220446049250313E-016D;
/*  695: 996 */         pkm1 *= 2.220446049250313E-016D;
/*  696: 997 */         qkm2 *= 2.220446049250313E-016D;
/*  697: 998 */         qkm1 *= 2.220446049250313E-016D;
/*  698:     */       }
/*  699:1000 */       if ((Math.abs(qk) < 2.220446049250313E-016D) || (Math.abs(pk) < 2.220446049250313E-016D))
/*  700:     */       {
/*  701:1001 */         pkm2 *= 4503599627370496.0D;
/*  702:1002 */         pkm1 *= 4503599627370496.0D;
/*  703:1003 */         qkm2 *= 4503599627370496.0D;
/*  704:1004 */         qkm1 *= 4503599627370496.0D;
/*  705:     */       }
/*  706:1006 */       n++;
/*  707:1006 */     } while (n < 300);
/*  708:1008 */     return ans;
/*  709:     */   }
/*  710:     */   
/*  711:     */   public static double powerSeries(double a, double b, double x)
/*  712:     */   {
/*  713:1019 */     double ai = 1.0D / a;
/*  714:1020 */     double u = (1.0D - b) * x;
/*  715:1021 */     double v = u / (a + 1.0D);
/*  716:1022 */     double t1 = v;
/*  717:1023 */     double t = u;
/*  718:1024 */     double n = 2.0D;
/*  719:1025 */     double s = 0.0D;
/*  720:1026 */     double z = 1.110223024625157E-016D * ai;
/*  721:1027 */     while (Math.abs(v) > z)
/*  722:     */     {
/*  723:1028 */       u = (n - b) * x / n;
/*  724:1029 */       t *= u;
/*  725:1030 */       v = t / (a + n);
/*  726:1031 */       s += v;
/*  727:1032 */       n += 1.0D;
/*  728:     */     }
/*  729:1034 */     s += t1;
/*  730:1035 */     s += ai;
/*  731:     */     
/*  732:1037 */     u = a * Math.log(x);
/*  733:1038 */     if ((a + b < 171.62437695630271D) && (Math.abs(u) < 709.78271289338397D))
/*  734:     */     {
/*  735:1039 */       t = gamma(a + b) / (gamma(a) * gamma(b));
/*  736:1040 */       s = s * t * Math.pow(x, a);
/*  737:     */     }
/*  738:     */     else
/*  739:     */     {
/*  740:1042 */       t = lnGamma(a + b) - lnGamma(a) - lnGamma(b) + u + Math.log(s);
/*  741:1043 */       if (t < -745.13321910194122D) {
/*  742:1044 */         s = 0.0D;
/*  743:     */       } else {
/*  744:1046 */         s = Math.exp(t);
/*  745:     */       }
/*  746:     */     }
/*  747:1049 */     return s;
/*  748:     */   }
/*  749:     */   
/*  750:     */   public String getRevision()
/*  751:     */   {
/*  752:1059 */     return RevisionUtils.extract("$Revision: 10203 $");
/*  753:     */   }
/*  754:     */   
/*  755:     */   public static void main(String[] ops)
/*  756:     */   {
/*  757:1067 */     System.out.println("Binomial standard error (0.5, 100): " + binomialStandardError(0.5D, 100));
/*  758:     */     
/*  759:1069 */     System.out.println("Chi-squared probability (2.558, 10): " + chiSquaredProbability(2.558D, 10.0D));
/*  760:     */     
/*  761:1071 */     System.out.println("Normal probability (0.2): " + normalProbability(0.2D));
/*  762:     */     
/*  763:1073 */     System.out.println("F probability (5.1922, 4, 5): " + FProbability(5.1922D, 4, 5));
/*  764:     */     
/*  765:1075 */     System.out.println("lnGamma(6): " + lnGamma(6.0D));
/*  766:     */   }
/*  767:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Statistics
 * JD-Core Version:    0.7.0.1
 */