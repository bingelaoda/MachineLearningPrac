/*    1:     */ package weka.core;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import weka.core.matrix.Matrix;
/*    5:     */ 
/*    6:     */ public abstract class Optimization
/*    7:     */   implements TechnicalInformationHandler, RevisionHandler
/*    8:     */ {
/*    9:     */   protected double m_ALF;
/*   10:     */   protected double m_BETA;
/*   11:     */   protected double m_TOLX;
/*   12:     */   protected double m_STPMX;
/*   13:     */   protected int m_MAXITS;
/*   14:     */   protected boolean m_Debug;
/*   15:     */   protected double m_f;
/*   16:     */   private double m_Slope;
/*   17:     */   protected boolean m_IsZeroStep;
/*   18:     */   protected double[] m_X;
/*   19:     */   
/*   20:     */   public Optimization()
/*   21:     */   {
/*   22: 164 */     this.m_ALF = 0.0001D;
/*   23:     */     
/*   24: 166 */     this.m_BETA = 0.9D;
/*   25:     */     
/*   26: 168 */     this.m_TOLX = 1.0E-006D;
/*   27:     */     
/*   28: 170 */     this.m_STPMX = 100.0D;
/*   29:     */     
/*   30: 172 */     this.m_MAXITS = 200;
/*   31:     */     
/*   32: 174 */     this.m_Debug = false;
/*   33:     */     
/*   34:     */ 
/*   35:     */ 
/*   36:     */ 
/*   37:     */ 
/*   38:     */ 
/*   39:     */ 
/*   40:     */ 
/*   41: 183 */     this.m_IsZeroStep = false;
/*   42:     */   }
/*   43:     */   
/*   44: 191 */   protected static double m_Epsilon = 1.0D;
/*   45:     */   
/*   46:     */   static
/*   47:     */   {
/*   48: 192 */     while (1.0D + m_Epsilon > 1.0D) {
/*   49: 193 */       m_Epsilon /= 2.0D;
/*   50:     */     }
/*   51: 195 */     m_Epsilon *= 2.0D;
/*   52:     */   }
/*   53:     */   
/*   54: 196 */   protected static double m_Zero = Math.sqrt(m_Epsilon);
/*   55:     */   
/*   56:     */   public TechnicalInformation getTechnicalInformation()
/*   57:     */   {
/*   58: 211 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.MASTERSTHESIS);
/*   59: 212 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Xin Xu");
/*   60: 213 */     result.setValue(TechnicalInformation.Field.YEAR, "2003");
/*   61: 214 */     result.setValue(TechnicalInformation.Field.TITLE, "Statistical learning in multiple instance problem");
/*   62:     */     
/*   63: 216 */     result.setValue(TechnicalInformation.Field.SCHOOL, "University of Waikato");
/*   64: 217 */     result.setValue(TechnicalInformation.Field.ADDRESS, "Hamilton, NZ");
/*   65: 218 */     result.setValue(TechnicalInformation.Field.NOTE, "0657.594");
/*   66:     */     
/*   67: 220 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.BOOK);
/*   68: 221 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "P. E. Gill and W. Murray and M. H. Wright");
/*   69:     */     
/*   70: 223 */     additional.setValue(TechnicalInformation.Field.YEAR, "1981");
/*   71: 224 */     additional.setValue(TechnicalInformation.Field.TITLE, "Practical Optimization");
/*   72: 225 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "Academic Press");
/*   73: 226 */     additional.setValue(TechnicalInformation.Field.ADDRESS, "London and New York");
/*   74:     */     
/*   75: 228 */     additional = result.add(TechnicalInformation.Type.TECHREPORT);
/*   76: 229 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "P. E. Gill and W. Murray");
/*   77: 230 */     additional.setValue(TechnicalInformation.Field.YEAR, "1976");
/*   78: 231 */     additional.setValue(TechnicalInformation.Field.TITLE, "Minimization subject to bounds on the variables");
/*   79:     */     
/*   80: 233 */     additional.setValue(TechnicalInformation.Field.INSTITUTION, "National Physical Laboratory");
/*   81: 234 */     additional.setValue(TechnicalInformation.Field.NUMBER, "NAC 72");
/*   82:     */     
/*   83: 236 */     additional = result.add(TechnicalInformation.Type.BOOK);
/*   84: 237 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "E. K. P. Chong and S. H. Zak");
/*   85: 238 */     additional.setValue(TechnicalInformation.Field.YEAR, "1996");
/*   86: 239 */     additional.setValue(TechnicalInformation.Field.TITLE, "An Introduction to Optimization");
/*   87: 240 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "John Wiley and Sons");
/*   88: 241 */     additional.setValue(TechnicalInformation.Field.ADDRESS, "New York");
/*   89:     */     
/*   90: 243 */     additional = result.add(TechnicalInformation.Type.BOOK);
/*   91: 244 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "J. E. Dennis and R. B. Schnabel");
/*   92: 245 */     additional.setValue(TechnicalInformation.Field.YEAR, "1983");
/*   93: 246 */     additional.setValue(TechnicalInformation.Field.TITLE, "Numerical Methods for Unconstrained Optimization and Nonlinear Equations");
/*   94:     */     
/*   95:     */ 
/*   96: 249 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "Prentice-Hall");
/*   97:     */     
/*   98: 251 */     additional = result.add(TechnicalInformation.Type.BOOK);
/*   99: 252 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "W. H. Press and B. P. Flannery and S. A. Teukolsky and W. T. Vetterling");
/*  100:     */     
/*  101:     */ 
/*  102: 255 */     additional.setValue(TechnicalInformation.Field.YEAR, "1992");
/*  103: 256 */     additional.setValue(TechnicalInformation.Field.TITLE, "Numerical Recipes in C");
/*  104: 257 */     additional.setValue(TechnicalInformation.Field.PUBLISHER, "Cambridge University Press");
/*  105: 258 */     additional.setValue(TechnicalInformation.Field.EDITION, "Second");
/*  106:     */     
/*  107: 260 */     additional = result.add(TechnicalInformation.Type.ARTICLE);
/*  108: 261 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "P. E. Gill and G. H. Golub and W. Murray and M. A. Saunders");
/*  109:     */     
/*  110: 263 */     additional.setValue(TechnicalInformation.Field.YEAR, "1974");
/*  111: 264 */     additional.setValue(TechnicalInformation.Field.TITLE, "Methods for modifying matrix factorizations");
/*  112:     */     
/*  113: 266 */     additional.setValue(TechnicalInformation.Field.JOURNAL, "Mathematics of Computation");
/*  114: 267 */     additional.setValue(TechnicalInformation.Field.VOLUME, "28");
/*  115: 268 */     additional.setValue(TechnicalInformation.Field.NUMBER, "126");
/*  116: 269 */     additional.setValue(TechnicalInformation.Field.PAGES, "505-535");
/*  117:     */     
/*  118: 271 */     return result;
/*  119:     */   }
/*  120:     */   
/*  121:     */   protected double[] evaluateHessian(double[] x, int index)
/*  122:     */     throws Exception
/*  123:     */   {
/*  124: 304 */     return null;
/*  125:     */   }
/*  126:     */   
/*  127:     */   public double getMinFunction()
/*  128:     */   {
/*  129: 313 */     return this.m_f;
/*  130:     */   }
/*  131:     */   
/*  132:     */   public void setMaxIteration(int it)
/*  133:     */   {
/*  134: 322 */     this.m_MAXITS = it;
/*  135:     */   }
/*  136:     */   
/*  137:     */   public void setDebug(boolean db)
/*  138:     */   {
/*  139: 331 */     this.m_Debug = db;
/*  140:     */   }
/*  141:     */   
/*  142:     */   public double[] getVarbValues()
/*  143:     */   {
/*  144: 341 */     return this.m_X;
/*  145:     */   }
/*  146:     */   
/*  147:     */   public double[] lnsrch(double[] xold, double[] gradient, double[] direct, double stpmax, boolean[] isFixed, double[][] nwsBounds, DynamicIntArray wsBdsIndx)
/*  148:     */     throws Exception
/*  149:     */   {
/*  150: 369 */     if (this.m_Debug) {
/*  151: 370 */       System.err.print("Machine precision is " + m_Epsilon + " and zero set to " + m_Zero);
/*  152:     */     }
/*  153: 374 */     int len = xold.length;int fixedOne = -1;
/*  154:     */     
/*  155:     */ 
/*  156:     */ 
/*  157: 378 */     double alpha = (1.0D / 0.0D);double fold = this.m_f;
/*  158:     */     
/*  159:     */ 
/*  160: 381 */     double alam2 = 0.0D;double disc = 0.0D;double maxalam = 1.0D;
/*  161:     */     
/*  162: 383 */     double[] x = new double[len];
/*  163:     */     
/*  164:     */ 
/*  165: 386 */     double sum = 0.0D;
/*  166: 386 */     for (int i = 0; i < len; i++) {
/*  167: 387 */       if (isFixed[i] == 0) {
/*  168: 388 */         sum += direct[i] * direct[i];
/*  169:     */       }
/*  170:     */     }
/*  171: 391 */     sum = Math.sqrt(sum);
/*  172: 393 */     if (this.m_Debug) {
/*  173: 394 */       System.err.println("fold:  " + Utils.doubleToString(fold, 10, 7) + "\n" + "sum:  " + Utils.doubleToString(sum, 10, 7) + "\n" + "stpmax:  " + Utils.doubleToString(stpmax, 10, 7));
/*  174:     */     }
/*  175: 398 */     if (sum > stpmax) {
/*  176: 399 */       for (i = 0; i < len; i++) {
/*  177: 400 */         if (isFixed[i] == 0) {
/*  178: 401 */           direct[i] *= stpmax / sum;
/*  179:     */         }
/*  180:     */       }
/*  181:     */     }
/*  182: 405 */     maxalam = stpmax / sum;
/*  183:     */     
/*  184:     */ 
/*  185:     */ 
/*  186: 409 */     this.m_Slope = 0.0D;
/*  187: 410 */     for (i = 0; i < len; i++)
/*  188:     */     {
/*  189: 411 */       x[i] = xold[i];
/*  190: 412 */       if (isFixed[i] == 0) {
/*  191: 413 */         this.m_Slope += gradient[i] * direct[i];
/*  192:     */       }
/*  193:     */     }
/*  194: 417 */     if (this.m_Debug) {
/*  195: 418 */       System.err.print("slope:  " + Utils.doubleToString(this.m_Slope, 10, 7) + "\n");
/*  196:     */     }
/*  197: 423 */     if (Math.abs(this.m_Slope) <= m_Zero)
/*  198:     */     {
/*  199: 424 */       if (this.m_Debug) {
/*  200: 425 */         System.err.println("Gradient and direction orthogonal -- Min. found with current fixed variables (or all variables fixed). Try to release some variables now.");
/*  201:     */       }
/*  202: 430 */       return x;
/*  203:     */     }
/*  204: 434 */     if (this.m_Slope > m_Zero)
/*  205:     */     {
/*  206: 435 */       if (this.m_Debug) {
/*  207: 436 */         for (int h = 0; h < x.length; h++) {
/*  208: 437 */           System.err.println(h + ": isFixed=" + isFixed[h] + ", x=" + x[h] + ", grad=" + gradient[h] + ", direct=" + direct[h]);
/*  209:     */         }
/*  210:     */       }
/*  211: 441 */       throw new Exception("g'*p positive! -- Try to debug from here: line 327.");
/*  212:     */     }
/*  213: 445 */     double test = 0.0D;
/*  214: 446 */     for (i = 0; i < len; i++) {
/*  215: 447 */       if (isFixed[i] == 0)
/*  216:     */       {
/*  217: 448 */         double temp = Math.abs(direct[i]) / Math.max(Math.abs(x[i]), 1.0D);
/*  218: 449 */         if (temp > test) {
/*  219: 450 */           test = temp;
/*  220:     */         }
/*  221:     */       }
/*  222:     */     }
/*  223:     */     double alamin;
/*  224: 455 */     if (test > m_Zero)
/*  225:     */     {
/*  226: 456 */       alamin = this.m_TOLX / test;
/*  227:     */     }
/*  228:     */     else
/*  229:     */     {
/*  230: 458 */       if (this.m_Debug) {
/*  231: 459 */         System.err.println("Zero directions for all free variables -- Min. found with current fixed variables (or all variables fixed). Try to release some variables now.");
/*  232:     */       }
/*  233: 464 */       return x;
/*  234:     */     }
/*  235:     */     double alamin;
/*  236: 468 */     for (i = 0; i < len; i++) {
/*  237: 469 */       if (isFixed[i] == 0) {
/*  238: 471 */         if ((direct[i] < -m_Epsilon) && (!Double.isNaN(nwsBounds[0][i])))
/*  239:     */         {
/*  240: 473 */           double alpi = (nwsBounds[0][i] - xold[i]) / direct[i];
/*  241: 474 */           if (alpi <= m_Zero)
/*  242:     */           {
/*  243: 475 */             if (this.m_Debug) {
/*  244: 476 */               System.err.println("Fix variable " + i + " to lower bound " + nwsBounds[0][i] + " from value " + xold[i]);
/*  245:     */             }
/*  246: 479 */             x[i] = nwsBounds[0][i];
/*  247: 480 */             isFixed[i] = true;
/*  248: 481 */             alpha = 0.0D;
/*  249: 482 */             nwsBounds[0][i] = (0.0D / 0.0D);
/*  250: 483 */             wsBdsIndx.addElement(i);
/*  251:     */           }
/*  252: 484 */           else if (alpha > alpi)
/*  253:     */           {
/*  254: 485 */             alpha = alpi;
/*  255: 486 */             fixedOne = i;
/*  256:     */           }
/*  257:     */         }
/*  258: 488 */         else if ((direct[i] > m_Epsilon) && (!Double.isNaN(nwsBounds[1][i])))
/*  259:     */         {
/*  260: 490 */           double alpi = (nwsBounds[1][i] - xold[i]) / direct[i];
/*  261: 491 */           if (alpi <= m_Zero)
/*  262:     */           {
/*  263: 492 */             if (this.m_Debug) {
/*  264: 493 */               System.err.println("Fix variable " + i + " to upper bound " + nwsBounds[1][i] + " from value " + xold[i]);
/*  265:     */             }
/*  266: 496 */             x[i] = nwsBounds[1][i];
/*  267: 497 */             isFixed[i] = true;
/*  268: 498 */             alpha = 0.0D;
/*  269: 499 */             nwsBounds[1][i] = (0.0D / 0.0D);
/*  270: 500 */             wsBdsIndx.addElement(i);
/*  271:     */           }
/*  272: 501 */           else if (alpha > alpi)
/*  273:     */           {
/*  274: 502 */             alpha = alpi;
/*  275: 503 */             fixedOne = i;
/*  276:     */           }
/*  277:     */         }
/*  278:     */       }
/*  279:     */     }
/*  280: 509 */     if (this.m_Debug)
/*  281:     */     {
/*  282: 510 */       System.err.println("alamin: " + Utils.doubleToString(alamin, 10, 7));
/*  283: 511 */       System.err.println("alpha: " + Utils.doubleToString(alpha, 10, 7));
/*  284:     */     }
/*  285: 514 */     if (alpha <= m_Zero)
/*  286:     */     {
/*  287: 515 */       this.m_IsZeroStep = true;
/*  288: 516 */       if (this.m_Debug) {
/*  289: 517 */         System.err.println("Alpha too small, try again");
/*  290:     */       }
/*  291: 519 */       return x;
/*  292:     */     }
/*  293: 522 */     double alam = alpha;
/*  294: 523 */     if (alam > 1.0D) {
/*  295: 524 */       alam = 1.0D;
/*  296:     */     }
/*  297: 528 */     double initF = fold;
/*  298: 529 */     double hi = alam;double lo = alam;double newSlope = 0.0D;double fhi = this.m_f;double flo = this.m_f;
/*  299: 534 */     for (int k = 0;; k++)
/*  300:     */     {
/*  301: 535 */       if (this.m_Debug) {
/*  302: 536 */         System.err.println("\nLine search iteration: " + k);
/*  303:     */       }
/*  304: 539 */       for (i = 0; i < len; i++) {
/*  305: 540 */         if (isFixed[i] == 0)
/*  306:     */         {
/*  307: 541 */           xold[i] += alam * direct[i];
/*  308: 542 */           if ((!Double.isNaN(nwsBounds[0][i])) && (x[i] < nwsBounds[0][i])) {
/*  309: 543 */             x[i] = nwsBounds[0][i];
/*  310: 544 */           } else if ((!Double.isNaN(nwsBounds[1][i])) && (x[i] > nwsBounds[1][i])) {
/*  311: 545 */             x[i] = nwsBounds[1][i];
/*  312:     */           }
/*  313:     */         }
/*  314:     */       }
/*  315: 550 */       this.m_f = objectiveFunction(x);
/*  316: 551 */       if (Double.isNaN(this.m_f)) {
/*  317: 552 */         throw new Exception("Objective function value is NaN!");
/*  318:     */       }
/*  319: 555 */       while (Double.isInfinite(this.m_f))
/*  320:     */       {
/*  321: 556 */         if (this.m_Debug) {
/*  322: 557 */           System.err.println("Too large m_f.  Shrink step by half.");
/*  323:     */         }
/*  324: 559 */         alam *= 0.5D;
/*  325: 560 */         if (alam <= m_Epsilon)
/*  326:     */         {
/*  327: 561 */           if (this.m_Debug) {
/*  328: 562 */             System.err.println("Wrong starting points, change them!");
/*  329:     */           }
/*  330: 564 */           return x;
/*  331:     */         }
/*  332: 567 */         for (i = 0; i < len; i++) {
/*  333: 568 */           if (isFixed[i] == 0) {
/*  334: 569 */             xold[i] += alam * direct[i];
/*  335:     */           }
/*  336:     */         }
/*  337: 573 */         this.m_f = objectiveFunction(x);
/*  338: 574 */         if (Double.isNaN(this.m_f)) {
/*  339: 575 */           throw new Exception("Objective function value is NaN!");
/*  340:     */         }
/*  341: 578 */         initF = (1.0D / 0.0D);
/*  342:     */       }
/*  343: 581 */       if (this.m_Debug)
/*  344:     */       {
/*  345: 582 */         System.err.println("obj. function: " + Utils.doubleToString(this.m_f, 10, 7));
/*  346:     */         
/*  347: 584 */         System.err.println("threshold: " + Utils.doubleToString(fold + this.m_ALF * alam * this.m_Slope, 10, 7));
/*  348:     */       }
/*  349: 588 */       if (this.m_f <= fold + this.m_ALF * alam * this.m_Slope)
/*  350:     */       {
/*  351: 590 */         if (this.m_Debug) {
/*  352: 591 */           System.err.println("Sufficient function decrease (alpha condition): ");
/*  353:     */         }
/*  354: 594 */         double[] newGrad = evaluateGradient(x);
/*  355: 595 */         newSlope = 0.0D;
/*  356: 595 */         for (i = 0; i < len; i++) {
/*  357: 596 */           if (isFixed[i] == 0) {
/*  358: 597 */             newSlope += newGrad[i] * direct[i];
/*  359:     */           }
/*  360:     */         }
/*  361: 601 */         if (this.m_Debug) {
/*  362: 602 */           System.err.println("newSlope: " + newSlope);
/*  363:     */         }
/*  364: 605 */         if (newSlope >= this.m_BETA * this.m_Slope)
/*  365:     */         {
/*  366: 607 */           if (this.m_Debug) {
/*  367: 608 */             System.err.println("Increasing derivatives (beta condition): ");
/*  368:     */           }
/*  369: 611 */           if ((fixedOne != -1) && (alam >= alpha))
/*  370:     */           {
/*  371: 612 */             if (direct[fixedOne] > 0.0D)
/*  372:     */             {
/*  373: 613 */               x[fixedOne] = nwsBounds[1][fixedOne];
/*  374: 614 */               nwsBounds[1][fixedOne] = (0.0D / 0.0D);
/*  375:     */             }
/*  376:     */             else
/*  377:     */             {
/*  378: 616 */               x[fixedOne] = nwsBounds[0][fixedOne];
/*  379: 617 */               nwsBounds[0][fixedOne] = (0.0D / 0.0D);
/*  380:     */             }
/*  381: 620 */             if (this.m_Debug) {
/*  382: 621 */               System.err.println("Fix variable " + fixedOne + " to bound " + x[fixedOne] + " from value " + xold[fixedOne]);
/*  383:     */             }
/*  384: 624 */             isFixed[fixedOne] = true;
/*  385: 625 */             wsBdsIndx.addElement(fixedOne);
/*  386:     */           }
/*  387: 627 */           return x;
/*  388:     */         }
/*  389: 628 */         if (k == 0)
/*  390:     */         {
/*  391: 630 */           double upper = Math.min(alpha, maxalam);
/*  392: 631 */           if (this.m_Debug) {
/*  393: 632 */             System.err.println("Alpha condition holds, increase alpha... ");
/*  394:     */           }
/*  395: 634 */           while ((alam < upper) && (this.m_f <= fold + this.m_ALF * alam * this.m_Slope))
/*  396:     */           {
/*  397: 635 */             lo = alam;
/*  398: 636 */             flo = this.m_f;
/*  399: 637 */             alam *= 2.0D;
/*  400: 638 */             if (alam >= upper) {
/*  401: 639 */               alam = upper;
/*  402:     */             }
/*  403: 642 */             for (i = 0; i < len; i++) {
/*  404: 643 */               if (isFixed[i] == 0) {
/*  405: 644 */                 xold[i] += alam * direct[i];
/*  406:     */               }
/*  407:     */             }
/*  408: 647 */             this.m_f = objectiveFunction(x);
/*  409: 648 */             if (Double.isNaN(this.m_f)) {
/*  410: 649 */               throw new Exception("Objective function value is NaN!");
/*  411:     */             }
/*  412: 652 */             newGrad = evaluateGradient(x);
/*  413: 653 */             newSlope = 0.0D;
/*  414: 653 */             for (i = 0; i < len; i++) {
/*  415: 654 */               if (isFixed[i] == 0) {
/*  416: 655 */                 newSlope += newGrad[i] * direct[i];
/*  417:     */               }
/*  418:     */             }
/*  419: 659 */             if (newSlope >= this.m_BETA * this.m_Slope)
/*  420:     */             {
/*  421: 660 */               if (this.m_Debug) {
/*  422: 661 */                 System.err.println("Increasing derivatives (beta condition): \nnewSlope = " + Utils.doubleToString(newSlope, 10, 7));
/*  423:     */               }
/*  424: 666 */               if ((fixedOne != -1) && (alam >= alpha))
/*  425:     */               {
/*  426: 667 */                 if (direct[fixedOne] > 0.0D)
/*  427:     */                 {
/*  428: 668 */                   x[fixedOne] = nwsBounds[1][fixedOne];
/*  429: 669 */                   nwsBounds[1][fixedOne] = (0.0D / 0.0D);
/*  430:     */                 }
/*  431:     */                 else
/*  432:     */                 {
/*  433: 672 */                   x[fixedOne] = nwsBounds[0][fixedOne];
/*  434: 673 */                   nwsBounds[0][fixedOne] = (0.0D / 0.0D);
/*  435:     */                 }
/*  436: 677 */                 if (this.m_Debug) {
/*  437: 678 */                   System.err.println("Fix variable " + fixedOne + " to bound " + x[fixedOne] + " from value " + xold[fixedOne]);
/*  438:     */                 }
/*  439: 681 */                 isFixed[fixedOne] = true;
/*  440: 682 */                 wsBdsIndx.addElement(fixedOne);
/*  441:     */               }
/*  442: 684 */               return x;
/*  443:     */             }
/*  444:     */           }
/*  445: 687 */           hi = alam;
/*  446: 688 */           fhi = this.m_f;
/*  447: 689 */           break;
/*  448:     */         }
/*  449: 691 */         if (this.m_Debug) {
/*  450: 692 */           System.err.println("Alpha condition holds.");
/*  451:     */         }
/*  452: 694 */         hi = alam2;
/*  453: 695 */         lo = alam;
/*  454: 696 */         flo = this.m_f;
/*  455: 697 */         break;
/*  456:     */       }
/*  457: 699 */       if (alam < alamin)
/*  458:     */       {
/*  459: 700 */         if (initF < fold)
/*  460:     */         {
/*  461: 701 */           alam = Math.min(1.0D, alpha);
/*  462: 702 */           for (i = 0; i < len; i++) {
/*  463: 703 */             if (isFixed[i] == 0) {
/*  464: 704 */               xold[i] += alam * direct[i];
/*  465:     */             }
/*  466:     */           }
/*  467: 708 */           if (this.m_Debug) {
/*  468: 709 */             System.err.println("No feasible lambda: still take alpha=" + alam);
/*  469:     */           }
/*  470: 713 */           if ((fixedOne != -1) && (alam >= alpha))
/*  471:     */           {
/*  472: 714 */             if (direct[fixedOne] > 0.0D)
/*  473:     */             {
/*  474: 715 */               x[fixedOne] = nwsBounds[1][fixedOne];
/*  475: 716 */               nwsBounds[1][fixedOne] = (0.0D / 0.0D);
/*  476:     */             }
/*  477:     */             else
/*  478:     */             {
/*  479: 718 */               x[fixedOne] = nwsBounds[0][fixedOne];
/*  480: 719 */               nwsBounds[0][fixedOne] = (0.0D / 0.0D);
/*  481:     */             }
/*  482: 722 */             if (this.m_Debug) {
/*  483: 723 */               System.err.println("Fix variable " + fixedOne + " to bound " + x[fixedOne] + " from value " + xold[fixedOne]);
/*  484:     */             }
/*  485: 726 */             isFixed[fixedOne] = true;
/*  486: 727 */             wsBdsIndx.addElement(fixedOne);
/*  487:     */           }
/*  488:     */         }
/*  489:     */         else
/*  490:     */         {
/*  491: 730 */           for (i = 0; i < len; i++) {
/*  492: 731 */             x[i] = xold[i];
/*  493:     */           }
/*  494: 733 */           this.m_f = fold;
/*  495: 734 */           if (this.m_Debug) {
/*  496: 735 */             System.err.println("Cannot find feasible lambda");
/*  497:     */           }
/*  498:     */         }
/*  499: 739 */         return x;
/*  500:     */       }
/*  501:     */       double tmplam;
/*  502:     */       double tmplam;
/*  503: 741 */       if (k == 0)
/*  504:     */       {
/*  505: 742 */         if (!Double.isInfinite(initF)) {
/*  506: 743 */           initF = this.m_f;
/*  507:     */         }
/*  508: 746 */         tmplam = -0.5D * alam * this.m_Slope / ((this.m_f - fold) / alam - this.m_Slope);
/*  509:     */       }
/*  510:     */       else
/*  511:     */       {
/*  512: 748 */         double rhs1 = this.m_f - fold - alam * this.m_Slope;
/*  513: 749 */         double rhs2 = fhi - fold - alam2 * this.m_Slope;
/*  514: 750 */         double a = (rhs1 / (alam * alam) - rhs2 / (alam2 * alam2)) / (alam - alam2);
/*  515: 751 */         double b = (-alam2 * rhs1 / (alam * alam) + alam * rhs2 / (alam2 * alam2)) / (alam - alam2);
/*  516:     */         double tmplam;
/*  517: 753 */         if (a == 0.0D)
/*  518:     */         {
/*  519: 754 */           tmplam = -this.m_Slope / (2.0D * b);
/*  520:     */         }
/*  521:     */         else
/*  522:     */         {
/*  523: 756 */           disc = b * b - 3.0D * a * this.m_Slope;
/*  524: 757 */           if (disc < 0.0D) {
/*  525: 758 */             disc = 0.0D;
/*  526:     */           }
/*  527: 760 */           double numerator = -b + Math.sqrt(disc);
/*  528: 761 */           if (numerator >= 1.7976931348623157E+308D)
/*  529:     */           {
/*  530: 762 */             numerator = 1.7976931348623157E+308D;
/*  531: 763 */             if (this.m_Debug) {
/*  532: 764 */               System.err.print("-b+sqrt(disc) too large! Set it to MAX_VALUE.");
/*  533:     */             }
/*  534:     */           }
/*  535: 768 */           tmplam = numerator / (3.0D * a);
/*  536:     */         }
/*  537: 770 */         if (this.m_Debug) {
/*  538: 771 */           System.err.print("Cubic interpolation: \na:   " + Utils.doubleToString(a, 10, 7) + "\n" + "b:   " + Utils.doubleToString(b, 10, 7) + "\n" + "disc:   " + Utils.doubleToString(disc, 10, 7) + "\n" + "tmplam:   " + tmplam + "\n" + "alam:   " + Utils.doubleToString(alam, 10, 7) + "\n");
/*  539:     */         }
/*  540: 778 */         if (tmplam > 0.5D * alam) {
/*  541: 779 */           tmplam = 0.5D * alam;
/*  542:     */         }
/*  543:     */       }
/*  544: 783 */       alam2 = alam;
/*  545: 784 */       fhi = this.m_f;
/*  546: 785 */       alam = Math.max(tmplam, 0.1D * alam);
/*  547: 787 */       if (alam > alpha) {
/*  548: 788 */         throw new Exception("Sth. wrong in lnsrch:Lambda infeasible!(lambda=" + alam + ", alpha=" + alpha + ", upper=" + tmplam + "|" + -alpha * this.m_Slope / (2.0D * ((this.m_f - fold) / alpha - this.m_Slope)) + ", m_f=" + this.m_f + ", fold=" + fold + ", slope=" + this.m_Slope);
/*  549:     */       }
/*  550:     */     }
/*  551:     */     double[] newGrad;
/*  552: 798 */     double ldiff = hi - lo;
/*  553: 799 */     if (this.m_Debug) {
/*  554: 800 */       System.err.println("Last stage of searching for beta condition (alam between " + Utils.doubleToString(lo, 10, 7) + " and " + Utils.doubleToString(hi, 10, 7) + ")...\n" + "Quadratic Interpolation(QI):\n" + "Last newSlope = " + Utils.doubleToString(newSlope, 10, 7));
/*  555:     */     }
/*  556: 808 */     while ((newSlope < this.m_BETA * this.m_Slope) && (ldiff >= alamin))
/*  557:     */     {
/*  558: 809 */       double lincr = -0.5D * newSlope * ldiff * ldiff / (fhi - flo - newSlope * ldiff);
/*  559: 811 */       if (this.m_Debug) {
/*  560: 812 */         System.err.println("fhi = " + fhi + "\n" + "flo = " + flo + "\n" + "ldiff = " + ldiff + "\n" + "lincr (using QI) = " + lincr + "\n");
/*  561:     */       }
/*  562: 816 */       if (lincr < 0.2D * ldiff) {
/*  563: 817 */         lincr = 0.2D * ldiff;
/*  564:     */       }
/*  565: 819 */       alam = lo + lincr;
/*  566: 820 */       if (alam >= hi)
/*  567:     */       {
/*  568: 822 */         alam = hi;
/*  569: 823 */         lincr = ldiff;
/*  570:     */       }
/*  571: 825 */       for (i = 0; i < len; i++) {
/*  572: 826 */         if (isFixed[i] == 0) {
/*  573: 827 */           xold[i] += alam * direct[i];
/*  574:     */         }
/*  575:     */       }
/*  576: 830 */       this.m_f = objectiveFunction(x);
/*  577: 831 */       if (Double.isNaN(this.m_f)) {
/*  578: 832 */         throw new Exception("Objective function value is NaN!");
/*  579:     */       }
/*  580: 835 */       if (this.m_f > fold + this.m_ALF * alam * this.m_Slope)
/*  581:     */       {
/*  582: 837 */         ldiff = lincr;
/*  583: 838 */         fhi = this.m_f;
/*  584:     */       }
/*  585:     */       else
/*  586:     */       {
/*  587: 840 */         newGrad = evaluateGradient(x);
/*  588: 841 */         newSlope = 0.0D;
/*  589: 841 */         for (i = 0; i < len; i++) {
/*  590: 842 */           if (isFixed[i] == 0) {
/*  591: 843 */             newSlope += newGrad[i] * direct[i];
/*  592:     */           }
/*  593:     */         }
/*  594: 847 */         if (newSlope < this.m_BETA * this.m_Slope)
/*  595:     */         {
/*  596: 849 */           lo = alam;
/*  597: 850 */           ldiff -= lincr;
/*  598: 851 */           flo = this.m_f;
/*  599:     */         }
/*  600:     */       }
/*  601:     */     }
/*  602: 856 */     if (newSlope < this.m_BETA * this.m_Slope)
/*  603:     */     {
/*  604: 857 */       if (this.m_Debug) {
/*  605: 858 */         System.err.println("Beta condition cannot be satisfied, take alpha condition");
/*  606:     */       }
/*  607: 861 */       alam = lo;
/*  608: 862 */       for (i = 0; i < len; i++) {
/*  609: 863 */         if (isFixed[i] == 0) {
/*  610: 864 */           xold[i] += alam * direct[i];
/*  611:     */         }
/*  612:     */       }
/*  613: 867 */       this.m_f = flo;
/*  614:     */     }
/*  615: 868 */     else if (this.m_Debug)
/*  616:     */     {
/*  617: 869 */       System.err.println("Both alpha and beta conditions are satisfied. alam=" + Utils.doubleToString(alam, 10, 7));
/*  618:     */     }
/*  619: 873 */     if ((fixedOne != -1) && (alam >= alpha))
/*  620:     */     {
/*  621: 874 */       if (direct[fixedOne] > 0.0D)
/*  622:     */       {
/*  623: 875 */         x[fixedOne] = nwsBounds[1][fixedOne];
/*  624: 876 */         nwsBounds[1][fixedOne] = (0.0D / 0.0D);
/*  625:     */       }
/*  626:     */       else
/*  627:     */       {
/*  628: 878 */         x[fixedOne] = nwsBounds[0][fixedOne];
/*  629: 879 */         nwsBounds[0][fixedOne] = (0.0D / 0.0D);
/*  630:     */       }
/*  631: 882 */       if (this.m_Debug) {
/*  632: 883 */         System.err.println("Fix variable " + fixedOne + " to bound " + x[fixedOne] + " from value " + xold[fixedOne]);
/*  633:     */       }
/*  634: 886 */       isFixed[fixedOne] = true;
/*  635: 887 */       wsBdsIndx.addElement(fixedOne);
/*  636:     */     }
/*  637: 890 */     return x;
/*  638:     */   }
/*  639:     */   
/*  640:     */   public double[] findArgmin(double[] initX, double[][] constraints)
/*  641:     */     throws Exception
/*  642:     */   {
/*  643: 904 */     int l = initX.length;
/*  644:     */     
/*  645:     */ 
/*  646:     */ 
/*  647: 908 */     boolean[] isFixed = new boolean[l];
/*  648: 909 */     double[][] nwsBounds = new double[2][l];
/*  649:     */     
/*  650: 911 */     DynamicIntArray wsBdsIndx = new DynamicIntArray(constraints.length);
/*  651:     */     
/*  652: 913 */     DynamicIntArray toFree = null;DynamicIntArray oldToFree = null;
/*  653:     */     
/*  654:     */ 
/*  655: 916 */     this.m_f = objectiveFunction(initX);
/*  656: 917 */     if (Double.isNaN(this.m_f)) {
/*  657: 918 */       throw new Exception("Objective function value is NaN!");
/*  658:     */     }
/*  659: 921 */     double sum = 0.0D;
/*  660: 922 */     double[] grad = evaluateGradient(initX);double[] deltaGrad = new double[l];double[] deltaX = new double[l];double[] direct = new double[l];double[] x = new double[l];
/*  661: 923 */     Matrix L = new Matrix(l, l);
/*  662: 924 */     double[] D = new double[l];
/*  663: 925 */     for (int i = 0; i < l; i++)
/*  664:     */     {
/*  665: 927 */       L.set(i, i, 1.0D);
/*  666: 928 */       D[i] = 1.0D;
/*  667: 929 */       direct[i] = (-grad[i]);
/*  668: 930 */       sum += grad[i] * grad[i];
/*  669: 931 */       x[i] = initX[i];
/*  670: 932 */       nwsBounds[0][i] = constraints[0][i];
/*  671: 933 */       nwsBounds[1][i] = constraints[1][i];
/*  672: 934 */       isFixed[i] = false;
/*  673:     */     }
/*  674: 936 */     double stpmax = this.m_STPMX * Math.max(Math.sqrt(sum), l);
/*  675: 938 */     for (int step = 0; step < this.m_MAXITS; step++)
/*  676:     */     {
/*  677: 939 */       if (this.m_Debug) {
/*  678: 940 */         System.err.println("\nIteration # " + step + ":");
/*  679:     */       }
/*  680: 944 */       double[] oldX = x;
/*  681: 945 */       double[] oldGrad = grad;
/*  682: 948 */       if (this.m_Debug) {
/*  683: 949 */         System.err.println("Line search ... ");
/*  684:     */       }
/*  685: 951 */       this.m_IsZeroStep = false;
/*  686: 952 */       x = lnsrch(x, grad, direct, stpmax, isFixed, nwsBounds, wsBdsIndx);
/*  687: 953 */       if (this.m_Debug) {
/*  688: 954 */         System.err.println("Line search finished.");
/*  689:     */       }
/*  690: 957 */       if (this.m_IsZeroStep)
/*  691:     */       {
/*  692: 958 */         for (int f = 0; f < wsBdsIndx.size(); f++)
/*  693:     */         {
/*  694: 959 */           int[] idx = new int[1];
/*  695:     */           
/*  696: 961 */           idx[0] = wsBdsIndx.elementAt(f);
/*  697: 962 */           L.setMatrix(idx, 0, l - 1, new Matrix(1, l));
/*  698:     */           
/*  699: 964 */           L.setMatrix(0, l - 1, idx, new Matrix(l, 1));
/*  700:     */           
/*  701: 966 */           D[idx[0]] = 0.0D;
/*  702:     */         }
/*  703: 969 */         grad = evaluateGradient(x);
/*  704: 970 */         step--;
/*  705:     */       }
/*  706:     */       else
/*  707:     */       {
/*  708: 973 */         boolean finish = false;
/*  709: 974 */         double test = 0.0D;
/*  710: 975 */         for (int h = 0; h < l; h++)
/*  711:     */         {
/*  712: 976 */           x[h] -= oldX[h];
/*  713: 977 */           double tmp = Math.abs(deltaX[h]) / Math.max(Math.abs(x[h]), 1.0D);
/*  714: 978 */           if (tmp > test) {
/*  715: 979 */             test = tmp;
/*  716:     */           }
/*  717:     */         }
/*  718: 982 */         if (test < m_Zero)
/*  719:     */         {
/*  720: 983 */           if (this.m_Debug) {
/*  721: 984 */             System.err.println("\nDeltaX converge: " + test);
/*  722:     */           }
/*  723: 986 */           finish = true;
/*  724:     */         }
/*  725: 990 */         grad = evaluateGradient(x);
/*  726: 991 */         test = 0.0D;
/*  727: 992 */         double denom = 0.0D;double dxSq = 0.0D;double dgSq = 0.0D;double newlyBounded = 0.0D;
/*  728: 993 */         for (int g = 0; g < l; g++)
/*  729:     */         {
/*  730: 994 */           if (isFixed[g] == 0)
/*  731:     */           {
/*  732: 995 */             grad[g] -= oldGrad[g];
/*  733:     */             
/*  734: 997 */             denom += deltaX[g] * deltaGrad[g];
/*  735: 998 */             dxSq += deltaX[g] * deltaX[g];
/*  736: 999 */             dgSq += deltaGrad[g] * deltaGrad[g];
/*  737:     */           }
/*  738:     */           else
/*  739:     */           {
/*  740:1001 */             newlyBounded += deltaX[g] * (grad[g] - oldGrad[g]);
/*  741:     */           }
/*  742:1006 */           double tmp = Math.abs(grad[g]) * Math.max(Math.abs(direct[g]), 1.0D) / Math.max(Math.abs(this.m_f), 1.0D);
/*  743:1008 */           if (tmp > test) {
/*  744:1009 */             test = tmp;
/*  745:     */           }
/*  746:     */         }
/*  747:1013 */         if (test < m_Zero)
/*  748:     */         {
/*  749:1014 */           if (this.m_Debug) {
/*  750:1015 */             System.err.println("Gradient converge: " + test);
/*  751:     */           }
/*  752:1017 */           finish = true;
/*  753:     */         }
/*  754:1021 */         if (this.m_Debug) {
/*  755:1022 */           System.err.println("dg'*dx=" + (denom + newlyBounded));
/*  756:     */         }
/*  757:1025 */         if (Math.abs(denom + newlyBounded) < m_Zero) {
/*  758:1026 */           finish = true;
/*  759:     */         }
/*  760:1029 */         int size = wsBdsIndx.size();
/*  761:1030 */         boolean isUpdate = true;
/*  762:1032 */         if (finish)
/*  763:     */         {
/*  764:1033 */           if (this.m_Debug) {
/*  765:1034 */             System.err.println("Test any release possible ...");
/*  766:     */           }
/*  767:1037 */           if (toFree != null) {
/*  768:1038 */             oldToFree = (DynamicIntArray)toFree.copy();
/*  769:     */           }
/*  770:1040 */           toFree = new DynamicIntArray(wsBdsIndx.size());
/*  771:1042 */           for (int m = size - 1; m >= 0; m--)
/*  772:     */           {
/*  773:1043 */             int index = wsBdsIndx.elementAt(m);
/*  774:1044 */             double[] hessian = evaluateHessian(x, index);
/*  775:1045 */             double deltaL = 0.0D;
/*  776:1046 */             if (hessian != null) {
/*  777:1047 */               for (int mm = 0; mm < hessian.length; mm++) {
/*  778:1048 */                 if (isFixed[mm] == 0) {
/*  779:1049 */                   deltaL += hessian[mm] * direct[mm];
/*  780:     */                 }
/*  781:     */               }
/*  782:     */             }
/*  783:     */             double L1;
/*  784:1057 */             if (x[index] >= constraints[1][index])
/*  785:     */             {
/*  786:1058 */               L1 = -grad[index];
/*  787:     */             }
/*  788:     */             else
/*  789:     */             {
/*  790:     */               double L1;
/*  791:1059 */               if (x[index] <= constraints[0][index]) {
/*  792:1060 */                 L1 = grad[index];
/*  793:     */               } else {
/*  794:1062 */                 throw new Exception("x[" + index + "] not fixed on the" + " bounds where it should have been!");
/*  795:     */               }
/*  796:     */             }
/*  797:     */             double L1;
/*  798:1067 */             double L2 = L1 + deltaL;
/*  799:1068 */             if (this.m_Debug) {
/*  800:1069 */               System.err.println("Variable " + index + ": Lagrangian=" + L1 + "|" + L2);
/*  801:     */             }
/*  802:1074 */             boolean isConverge = 2.0D * Math.abs(deltaL) < Math.min(Math.abs(L1), Math.abs(L2));
/*  803:1076 */             if ((L1 * L2 > 0.0D) && (isConverge)) {
/*  804:1078 */               if (L2 < 0.0D)
/*  805:     */               {
/*  806:1079 */                 toFree.addElement(index);
/*  807:1080 */                 wsBdsIndx.removeElementAt(m);
/*  808:1081 */                 finish = false;
/*  809:     */               }
/*  810:     */             }
/*  811:1088 */             if ((hessian == null) && (toFree != null) && (toFree.equal(oldToFree))) {
/*  812:1090 */               finish = true;
/*  813:     */             }
/*  814:     */           }
/*  815:1094 */           if (finish)
/*  816:     */           {
/*  817:1095 */             if (this.m_Debug) {
/*  818:1096 */               System.err.println("Minimum found.");
/*  819:     */             }
/*  820:1098 */             this.m_f = objectiveFunction(x);
/*  821:1099 */             if (Double.isNaN(this.m_f)) {
/*  822:1100 */               throw new Exception("Objective function value is NaN!");
/*  823:     */             }
/*  824:1102 */             return x;
/*  825:     */           }
/*  826:1106 */           for (int mmm = 0; mmm < toFree.size(); mmm++)
/*  827:     */           {
/*  828:1107 */             int freeIndx = toFree.elementAt(mmm);
/*  829:1108 */             isFixed[freeIndx] = false;
/*  830:1109 */             if (x[freeIndx] <= constraints[0][freeIndx])
/*  831:     */             {
/*  832:1110 */               nwsBounds[0][freeIndx] = constraints[0][freeIndx];
/*  833:1111 */               if (this.m_Debug) {
/*  834:1112 */                 System.err.println("Free variable " + freeIndx + " from bound " + nwsBounds[0][freeIndx]);
/*  835:     */               }
/*  836:     */             }
/*  837:     */             else
/*  838:     */             {
/*  839:1116 */               nwsBounds[1][freeIndx] = constraints[1][freeIndx];
/*  840:1117 */               if (this.m_Debug) {
/*  841:1118 */                 System.err.println("Free variable " + freeIndx + " from bound " + nwsBounds[1][freeIndx]);
/*  842:     */               }
/*  843:     */             }
/*  844:1122 */             L.set(freeIndx, freeIndx, 1.0D);
/*  845:1123 */             D[freeIndx] = 1.0D;
/*  846:1124 */             isUpdate = false;
/*  847:     */           }
/*  848:     */         }
/*  849:1128 */         if (denom < Math.max(m_Zero * Math.sqrt(dxSq) * Math.sqrt(dgSq), m_Zero))
/*  850:     */         {
/*  851:1130 */           if (this.m_Debug) {
/*  852:1131 */             System.err.println("dg'*dx negative!");
/*  853:     */           }
/*  854:1133 */           isUpdate = false;
/*  855:     */         }
/*  856:1136 */         if (isUpdate)
/*  857:     */         {
/*  858:1139 */           double coeff = 1.0D / denom;
/*  859:1140 */           updateCholeskyFactor(L, D, deltaGrad, coeff, isFixed);
/*  860:     */           
/*  861:     */ 
/*  862:1143 */           coeff = 1.0D / this.m_Slope;
/*  863:1144 */           updateCholeskyFactor(L, D, oldGrad, coeff, isFixed);
/*  864:     */         }
/*  865:     */       }
/*  866:1149 */       Matrix LD = new Matrix(l, l);
/*  867:1150 */       double[] b = new double[l];
/*  868:1152 */       for (int k = 0; k < l; k++)
/*  869:     */       {
/*  870:1153 */         if (isFixed[k] == 0) {
/*  871:1154 */           b[k] = (-grad[k]);
/*  872:     */         } else {
/*  873:1156 */           b[k] = 0.0D;
/*  874:     */         }
/*  875:1159 */         for (int j = k; j < l; j++) {
/*  876:1160 */           if ((isFixed[j] == 0) && (isFixed[k] == 0)) {
/*  877:1161 */             LD.set(j, k, L.get(j, k) * D[k]);
/*  878:     */           }
/*  879:     */         }
/*  880:     */       }
/*  881:1167 */       double[] LDIR = solveTriangle(LD, b, true, isFixed);
/*  882:1168 */       LD = null;
/*  883:1170 */       for (int m = 0; m < LDIR.length; m++) {
/*  884:1171 */         if (Double.isNaN(LDIR[m])) {
/*  885:1172 */           throw new Exception("L*direct[" + m + "] is NaN!" + "|-g=" + b[m] + "|" + isFixed[m] + "|diag=" + D[m]);
/*  886:     */         }
/*  887:     */       }
/*  888:1178 */       direct = solveTriangle(L, LDIR, false, isFixed);
/*  889:1179 */       for (double element : direct) {
/*  890:1180 */         if (Double.isNaN(element)) {
/*  891:1181 */           throw new Exception("direct is NaN!");
/*  892:     */         }
/*  893:     */       }
/*  894:     */     }
/*  895:1188 */     if (this.m_Debug) {
/*  896:1189 */       System.err.println("Cannot find minimum -- too many interations!");
/*  897:     */     }
/*  898:1191 */     this.m_X = x;
/*  899:1192 */     return null;
/*  900:     */   }
/*  901:     */   
/*  902:     */   public static double[] solveTriangle(Matrix t, double[] b, boolean isLower, boolean[] isZero)
/*  903:     */   {
/*  904:1208 */     int n = b.length;
/*  905:1209 */     double[] result = new double[n];
/*  906:1210 */     if (isZero == null) {
/*  907:1211 */       isZero = new boolean[n];
/*  908:     */     }
/*  909:1214 */     if (isLower)
/*  910:     */     {
/*  911:1215 */       int j = 0;
/*  912:1216 */       while ((j < n) && (isZero[j] != 0))
/*  913:     */       {
/*  914:1217 */         result[j] = 0.0D;
/*  915:1218 */         j++;
/*  916:     */       }
/*  917:1221 */       if (j < n)
/*  918:     */       {
/*  919:1222 */         b[j] /= t.get(j, j);
/*  920:1224 */         for (; j < n; j++) {
/*  921:1225 */           if (isZero[j] == 0)
/*  922:     */           {
/*  923:1226 */             double numerator = b[j];
/*  924:1227 */             for (int k = 0; k < j; k++) {
/*  925:1228 */               numerator -= t.get(j, k) * result[k];
/*  926:     */             }
/*  927:1230 */             result[j] = (numerator / t.get(j, j));
/*  928:     */           }
/*  929:     */           else
/*  930:     */           {
/*  931:1232 */             result[j] = 0.0D;
/*  932:     */           }
/*  933:     */         }
/*  934:     */       }
/*  935:     */     }
/*  936:     */     else
/*  937:     */     {
/*  938:1237 */       int j = n - 1;
/*  939:1238 */       while ((j >= 0) && (isZero[j] != 0))
/*  940:     */       {
/*  941:1239 */         result[j] = 0.0D;
/*  942:1240 */         j--;
/*  943:     */       }
/*  944:1243 */       if (j >= 0)
/*  945:     */       {
/*  946:1244 */         b[j] /= t.get(j, j);
/*  947:1246 */         for (; j >= 0; j--) {
/*  948:1247 */           if (isZero[j] == 0)
/*  949:     */           {
/*  950:1248 */             double numerator = b[j];
/*  951:1249 */             for (int k = j + 1; k < n; k++) {
/*  952:1250 */               numerator -= t.get(k, j) * result[k];
/*  953:     */             }
/*  954:1252 */             result[j] = (numerator / t.get(j, j));
/*  955:     */           }
/*  956:     */           else
/*  957:     */           {
/*  958:1254 */             result[j] = 0.0D;
/*  959:     */           }
/*  960:     */         }
/*  961:     */       }
/*  962:     */     }
/*  963:1260 */     return result;
/*  964:     */   }
/*  965:     */   
/*  966:     */   protected void updateCholeskyFactor(Matrix L, double[] D, double[] v, double coeff, boolean[] isFixed)
/*  967:     */     throws Exception
/*  968:     */   {
/*  969:1279 */     int n = v.length;
/*  970:1280 */     double[] vp = new double[n];
/*  971:1281 */     for (int i = 0; i < v.length; i++) {
/*  972:1282 */       if (isFixed[i] == 0) {
/*  973:1283 */         vp[i] = v[i];
/*  974:     */       } else {
/*  975:1285 */         vp[i] = 0.0D;
/*  976:     */       }
/*  977:     */     }
/*  978:1289 */     if (coeff > 0.0D)
/*  979:     */     {
/*  980:1290 */       double t = coeff;
/*  981:1291 */       for (int j = 0; j < n; j++) {
/*  982:1292 */         if (isFixed[j] == 0)
/*  983:     */         {
/*  984:1296 */           double p = vp[j];
/*  985:1297 */           double d = D[j];double dbarj = d + t * p * p;
/*  986:1298 */           D[j] = dbarj;
/*  987:     */           
/*  988:1300 */           double b = p * t / dbarj;
/*  989:1301 */           t *= d / dbarj;
/*  990:1302 */           for (int r = j + 1; r < n; r++) {
/*  991:1303 */             if (isFixed[r] == 0)
/*  992:     */             {
/*  993:1304 */               double l = L.get(r, j);
/*  994:1305 */               vp[r] -= p * l;
/*  995:1306 */               L.set(r, j, l + b * vp[r]);
/*  996:     */             }
/*  997:     */             else
/*  998:     */             {
/*  999:1308 */               L.set(r, j, 0.0D);
/* 1000:     */             }
/* 1001:     */           }
/* 1002:     */         }
/* 1003:     */       }
/* 1004:     */     }
/* 1005:     */     else
/* 1006:     */     {
/* 1007:1313 */       double[] P = solveTriangle(L, v, true, isFixed);
/* 1008:1314 */       double t = 0.0D;
/* 1009:1315 */       for (int i = 0; i < n; i++) {
/* 1010:1316 */         if (isFixed[i] == 0) {
/* 1011:1317 */           t += P[i] * P[i] / D[i];
/* 1012:     */         }
/* 1013:     */       }
/* 1014:1321 */       double sqrt = 1.0D + coeff * t;
/* 1015:1322 */       sqrt = sqrt < 0.0D ? 0.0D : Math.sqrt(sqrt);
/* 1016:     */       
/* 1017:1324 */       double alpha = coeff;double sigma = coeff / (1.0D + sqrt);
/* 1018:1326 */       for (int j = 0; j < n; j++) {
/* 1019:1327 */         if (isFixed[j] == 0)
/* 1020:     */         {
/* 1021:1331 */           double d = D[j];
/* 1022:1332 */           double p = P[j] * P[j] / d;
/* 1023:1333 */           double theta = 1.0D + sigma * p;
/* 1024:1334 */           t -= p;
/* 1025:1335 */           if (t < 0.0D) {
/* 1026:1336 */             t = 0.0D;
/* 1027:     */           }
/* 1028:1339 */           double plus = sigma * sigma * p * t;
/* 1029:1340 */           if ((j < n - 1) && (plus <= m_Zero)) {
/* 1030:1341 */             plus = m_Zero;
/* 1031:     */           }
/* 1032:1343 */           double rho = theta * theta + plus;
/* 1033:1344 */           D[j] = (rho * d);
/* 1034:1346 */           if (Double.isNaN(D[j])) {
/* 1035:1347 */             throw new Exception("d[" + j + "] NaN! P=" + P[j] + ",d=" + d + ",t=" + t + ",p=" + p + ",sigma=" + sigma + ",sclar=" + coeff);
/* 1036:     */           }
/* 1037:1351 */           double b = alpha * P[j] / (rho * d);
/* 1038:1352 */           alpha /= rho;
/* 1039:1353 */           rho = Math.sqrt(rho);
/* 1040:1354 */           double sigmaOld = sigma;
/* 1041:1355 */           sigma *= (1.0D + rho) / (rho * (theta + rho));
/* 1042:1356 */           if ((j < n - 1) && ((Double.isNaN(sigma)) || (Double.isInfinite(sigma)))) {
/* 1043:1357 */             throw new Exception("sigma NaN/Inf! rho=" + rho + ",theta=" + theta + ",P[" + j + "]=" + P[j] + ",p=" + p + ",d=" + d + ",t=" + t + ",oldsigma=" + sigmaOld);
/* 1044:     */           }
/* 1045:1362 */           for (int r = j + 1; r < n; r++) {
/* 1046:1363 */             if (isFixed[r] == 0)
/* 1047:     */             {
/* 1048:1364 */               double l = L.get(r, j);
/* 1049:1365 */               vp[r] -= P[j] * l;
/* 1050:1366 */               L.set(r, j, l + b * vp[r]);
/* 1051:     */             }
/* 1052:     */             else
/* 1053:     */             {
/* 1054:1368 */               L.set(r, j, 0.0D);
/* 1055:     */             }
/* 1056:     */           }
/* 1057:     */         }
/* 1058:     */       }
/* 1059:     */     }
/* 1060:     */   }
/* 1061:     */   
/* 1062:     */   protected abstract double objectiveFunction(double[] paramArrayOfDouble)
/* 1063:     */     throws Exception;
/* 1064:     */   
/* 1065:     */   protected abstract double[] evaluateGradient(double[] paramArrayOfDouble)
/* 1066:     */     throws Exception;
/* 1067:     */   
/* 1068:     */   protected class DynamicIntArray
/* 1069:     */     implements RevisionHandler
/* 1070:     */   {
/* 1071:     */     private int[] m_Objects;
/* 1072:1384 */     private int m_Size = 0;
/* 1073:1387 */     private int m_CapacityIncrement = 1;
/* 1074:1390 */     private int m_CapacityMultiplier = 2;
/* 1075:     */     
/* 1076:     */     public DynamicIntArray(int capacity)
/* 1077:     */     {
/* 1078:1399 */       this.m_Objects = new int[capacity];
/* 1079:     */     }
/* 1080:     */     
/* 1081:     */     public final void addElement(int element)
/* 1082:     */     {
/* 1083:1410 */       if (this.m_Size == this.m_Objects.length)
/* 1084:     */       {
/* 1085:1412 */         int[] newObjects = new int[this.m_CapacityMultiplier * (this.m_Objects.length + this.m_CapacityIncrement)];
/* 1086:     */         
/* 1087:1414 */         System.arraycopy(this.m_Objects, 0, newObjects, 0, this.m_Size);
/* 1088:1415 */         this.m_Objects = newObjects;
/* 1089:     */       }
/* 1090:1417 */       this.m_Objects[this.m_Size] = element;
/* 1091:1418 */       this.m_Size += 1;
/* 1092:     */     }
/* 1093:     */     
/* 1094:     */     public final Object copy()
/* 1095:     */     {
/* 1096:1428 */       DynamicIntArray copy = new DynamicIntArray(Optimization.this, this.m_Objects.length);
/* 1097:     */       
/* 1098:1430 */       copy.m_Size = this.m_Size;
/* 1099:1431 */       copy.m_CapacityIncrement = this.m_CapacityIncrement;
/* 1100:1432 */       copy.m_CapacityMultiplier = this.m_CapacityMultiplier;
/* 1101:1433 */       System.arraycopy(this.m_Objects, 0, copy.m_Objects, 0, this.m_Size);
/* 1102:1434 */       return copy;
/* 1103:     */     }
/* 1104:     */     
/* 1105:     */     public final int elementAt(int index)
/* 1106:     */     {
/* 1107:1445 */       return this.m_Objects[index];
/* 1108:     */     }
/* 1109:     */     
/* 1110:     */     private boolean equal(DynamicIntArray b)
/* 1111:     */     {
/* 1112:1457 */       if ((b == null) || (size() != b.size())) {
/* 1113:1458 */         return false;
/* 1114:     */       }
/* 1115:1461 */       int size = size();
/* 1116:     */       
/* 1117:     */ 
/* 1118:1464 */       int[] sorta = Utils.sort(this.m_Objects);int[] sortb = Utils.sort(b.m_Objects);
/* 1119:1465 */       for (int j = 0; j < size; j++) {
/* 1120:1466 */         if (this.m_Objects[sorta[j]] != b.m_Objects[sortb[j]]) {
/* 1121:1467 */           return false;
/* 1122:     */         }
/* 1123:     */       }
/* 1124:1471 */       return true;
/* 1125:     */     }
/* 1126:     */     
/* 1127:     */     public final void removeElementAt(int index)
/* 1128:     */     {
/* 1129:1481 */       System.arraycopy(this.m_Objects, index + 1, this.m_Objects, index, this.m_Size - index - 1);
/* 1130:     */       
/* 1131:1483 */       this.m_Size -= 1;
/* 1132:     */     }
/* 1133:     */     
/* 1134:     */     public final void removeAllElements()
/* 1135:     */     {
/* 1136:1491 */       this.m_Objects = new int[this.m_Objects.length];
/* 1137:1492 */       this.m_Size = 0;
/* 1138:     */     }
/* 1139:     */     
/* 1140:     */     public final int size()
/* 1141:     */     {
/* 1142:1502 */       return this.m_Size;
/* 1143:     */     }
/* 1144:     */     
/* 1145:     */     public String getRevision()
/* 1146:     */     {
/* 1147:1512 */       return RevisionUtils.extract("$Revision: 11271 $");
/* 1148:     */     }
/* 1149:     */   }
/* 1150:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.Optimization
 * JD-Core Version:    0.7.0.1
 */