/*    1:     */ package weka.classifiers.functions.supportVector;
/*    2:     */ 
/*    3:     */ import java.util.Collections;
/*    4:     */ import java.util.Enumeration;
/*    5:     */ import java.util.Vector;
/*    6:     */ import weka.core.Instance;
/*    7:     */ import weka.core.Instances;
/*    8:     */ import weka.core.Option;
/*    9:     */ import weka.core.RevisionUtils;
/*   10:     */ import weka.core.TechnicalInformation;
/*   11:     */ import weka.core.TechnicalInformation.Field;
/*   12:     */ import weka.core.TechnicalInformation.Type;
/*   13:     */ import weka.core.TechnicalInformationHandler;
/*   14:     */ import weka.core.Utils;
/*   15:     */ 
/*   16:     */ public class RegSMOImproved
/*   17:     */   extends RegSMO
/*   18:     */   implements TechnicalInformationHandler
/*   19:     */ {
/*   20:     */   private static final long serialVersionUID = 471692841446029784L;
/*   21:     */   public static final int I0 = 3;
/*   22:     */   public static final int I0a = 1;
/*   23:     */   public static final int I0b = 2;
/*   24:     */   public static final int I1 = 4;
/*   25:     */   public static final int I2 = 8;
/*   26:     */   public static final int I3 = 16;
/*   27:     */   protected SMOset m_I0;
/*   28:     */   protected int[] m_iSet;
/*   29:     */   protected double m_bUp;
/*   30:     */   protected double m_bLow;
/*   31:     */   protected int m_iUp;
/*   32:     */   protected int m_iLow;
/*   33: 144 */   double m_fTolerance = 0.001D;
/*   34: 147 */   boolean m_bUseVariant1 = true;
/*   35:     */   
/*   36:     */   public String globalInfo()
/*   37:     */   {
/*   38: 157 */     return "Learn SVM for regression using SMO with Shevade, Keerthi, et al. adaption of the stopping criterion.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*   39:     */   }
/*   40:     */   
/*   41:     */   public TechnicalInformation getTechnicalInformation()
/*   42:     */   {
/*   43: 174 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.INPROCEEDINGS);
/*   44: 175 */     result.setValue(TechnicalInformation.Field.AUTHOR, "S.K. Shevade and S.S. Keerthi and C. Bhattacharyya and K.R.K. Murthy");
/*   45:     */     
/*   46: 177 */     result.setValue(TechnicalInformation.Field.TITLE, "Improvements to the SMO Algorithm for SVM Regression");
/*   47:     */     
/*   48: 179 */     result.setValue(TechnicalInformation.Field.BOOKTITLE, "IEEE Transactions on Neural Networks");
/*   49: 180 */     result.setValue(TechnicalInformation.Field.YEAR, "1999");
/*   50: 181 */     result.setValue(TechnicalInformation.Field.PS, "http://guppy.mpe.nus.edu.sg/~mpessk/svm/ieee_smo_reg.ps.gz");
/*   51:     */     
/*   52:     */ 
/*   53: 184 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.TECHREPORT);
/*   54: 185 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "S.K. Shevade and S.S. Keerthi and C. Bhattacharyya and K.R.K. Murthy");
/*   55:     */     
/*   56: 187 */     additional.setValue(TechnicalInformation.Field.TITLE, "Improvements to the SMO Algorithm for SVM Regression");
/*   57:     */     
/*   58: 189 */     additional.setValue(TechnicalInformation.Field.INSTITUTION, "National University of Singapore");
/*   59: 190 */     additional.setValue(TechnicalInformation.Field.ADDRESS, "Control Division, Dept. of Mechanical Engineering");
/*   60:     */     
/*   61: 192 */     additional.setValue(TechnicalInformation.Field.NUMBER, "CD-99-16");
/*   62: 193 */     additional.setValue(TechnicalInformation.Field.YEAR, "1999");
/*   63: 194 */     additional.setValue(TechnicalInformation.Field.PS, "http://guppy.mpe.nus.edu.sg/~mpessk/svm/smoreg_mod.ps.gz");
/*   64:     */     
/*   65:     */ 
/*   66: 197 */     return result;
/*   67:     */   }
/*   68:     */   
/*   69:     */   public Enumeration<Option> listOptions()
/*   70:     */   {
/*   71: 207 */     Vector<Option> result = new Vector();
/*   72:     */     
/*   73: 209 */     result.addElement(new Option("\tThe tolerance parameter for checking the stopping criterion.\n\t(default 0.001)", "T", 1, "-T <double>"));
/*   74:     */     
/*   75:     */ 
/*   76:     */ 
/*   77: 213 */     result.addElement(new Option("\tUse variant 1 of the algorithm when true, otherwise use variant 2.\n\t(default true)", "V", 0, "-V"));
/*   78:     */     
/*   79:     */ 
/*   80:     */ 
/*   81: 217 */     result.addAll(Collections.list(super.listOptions()));
/*   82:     */     
/*   83: 219 */     return result.elements();
/*   84:     */   }
/*   85:     */   
/*   86:     */   public void setOptions(String[] options)
/*   87:     */     throws Exception
/*   88:     */   {
/*   89: 268 */     String tmpStr = Utils.getOption('T', options);
/*   90: 269 */     if (tmpStr.length() != 0) {
/*   91: 270 */       setTolerance(Double.parseDouble(tmpStr));
/*   92:     */     } else {
/*   93: 272 */       setTolerance(0.001D);
/*   94:     */     }
/*   95: 275 */     setUseVariant1(Utils.getFlag('V', options));
/*   96:     */     
/*   97: 277 */     super.setOptions(options);
/*   98:     */   }
/*   99:     */   
/*  100:     */   public String[] getOptions()
/*  101:     */   {
/*  102: 288 */     Vector<String> result = new Vector();
/*  103:     */     
/*  104: 290 */     result.add("-T");
/*  105: 291 */     result.add("" + getTolerance());
/*  106: 293 */     if (this.m_bUseVariant1) {
/*  107: 294 */       result.add("-V");
/*  108:     */     }
/*  109: 297 */     Collections.addAll(result, super.getOptions());
/*  110:     */     
/*  111: 299 */     return (String[])result.toArray(new String[result.size()]);
/*  112:     */   }
/*  113:     */   
/*  114:     */   public String toleranceTipText()
/*  115:     */   {
/*  116: 309 */     return "tolerance parameter used for checking stopping criterion b.up < b.low + 2 tol";
/*  117:     */   }
/*  118:     */   
/*  119:     */   public double getTolerance()
/*  120:     */   {
/*  121: 318 */     return this.m_fTolerance;
/*  122:     */   }
/*  123:     */   
/*  124:     */   public void setTolerance(double d)
/*  125:     */   {
/*  126: 327 */     this.m_fTolerance = d;
/*  127:     */   }
/*  128:     */   
/*  129:     */   public String useVariant1TipText()
/*  130:     */   {
/*  131: 337 */     return "set true to use variant 1 of the paper, otherwise use variant 2.";
/*  132:     */   }
/*  133:     */   
/*  134:     */   public boolean isUseVariant1()
/*  135:     */   {
/*  136: 346 */     return this.m_bUseVariant1;
/*  137:     */   }
/*  138:     */   
/*  139:     */   public void setUseVariant1(boolean b)
/*  140:     */   {
/*  141: 355 */     this.m_bUseVariant1 = b;
/*  142:     */   }
/*  143:     */   
/*  144:     */   protected int takeStep(int i1, int i2, double alpha2, double alpha2Star, double phi2)
/*  145:     */     throws Exception
/*  146:     */   {
/*  147: 377 */     if (i1 == i2) {
/*  148: 378 */       return 0;
/*  149:     */     }
/*  150: 380 */     double C1 = this.m_C * this.m_data.instance(i1).weight();
/*  151: 381 */     double C2 = this.m_C * this.m_data.instance(i2).weight();
/*  152:     */     
/*  153: 383 */     double alpha1 = this.m_alpha[i1];
/*  154: 384 */     double alpha1Star = this.m_alphaStar[i1];
/*  155:     */     
/*  156:     */ 
/*  157:     */ 
/*  158: 388 */     double phi1 = this.m_error[i1];
/*  159:     */     
/*  160:     */ 
/*  161:     */ 
/*  162:     */ 
/*  163:     */ 
/*  164:     */ 
/*  165:     */ 
/*  166:     */ 
/*  167:     */ 
/*  168:     */ 
/*  169: 399 */     double k11 = this.m_kernel.eval(i1, i1, this.m_data.instance(i1));
/*  170: 400 */     double k12 = this.m_kernel.eval(i1, i2, this.m_data.instance(i1));
/*  171: 401 */     double k22 = this.m_kernel.eval(i2, i2, this.m_data.instance(i2));
/*  172: 402 */     double eta = -2.0D * k12 + k11 + k22;
/*  173: 403 */     double gamma = alpha1 - alpha1Star + alpha2 - alpha2Star;
/*  174:     */     
/*  175:     */ 
/*  176:     */ 
/*  177:     */ 
/*  178:     */ 
/*  179:     */ 
/*  180:     */ 
/*  181:     */ 
/*  182:     */ 
/*  183:     */ 
/*  184:     */ 
/*  185:     */ 
/*  186:     */ 
/*  187:     */ 
/*  188:     */ 
/*  189:     */ 
/*  190:     */ 
/*  191:     */ 
/*  192:     */ 
/*  193:     */ 
/*  194:     */ 
/*  195:     */ 
/*  196:     */ 
/*  197:     */ 
/*  198:     */ 
/*  199:     */ 
/*  200:     */ 
/*  201:     */ 
/*  202:     */ 
/*  203:     */ 
/*  204:     */ 
/*  205:     */ 
/*  206:     */ 
/*  207:     */ 
/*  208:     */ 
/*  209:     */ 
/*  210:     */ 
/*  211:     */ 
/*  212:     */ 
/*  213:     */ 
/*  214:     */ 
/*  215:     */ 
/*  216:     */ 
/*  217:     */ 
/*  218:     */ 
/*  219:     */ 
/*  220:     */ 
/*  221:     */ 
/*  222:     */ 
/*  223:     */ 
/*  224:     */ 
/*  225:     */ 
/*  226:     */ 
/*  227:     */ 
/*  228:     */ 
/*  229:     */ 
/*  230:     */ 
/*  231:     */ 
/*  232:     */ 
/*  233:     */ 
/*  234:     */ 
/*  235:     */ 
/*  236:     */ 
/*  237:     */ 
/*  238:     */ 
/*  239:     */ 
/*  240:     */ 
/*  241:     */ 
/*  242:     */ 
/*  243:     */ 
/*  244:     */ 
/*  245:     */ 
/*  246:     */ 
/*  247:     */ 
/*  248:     */ 
/*  249: 479 */     double alpha1old = alpha1;
/*  250: 480 */     double alpha1Starold = alpha1Star;
/*  251: 481 */     double alpha2old = alpha2;
/*  252: 482 */     double alpha2Starold = alpha2Star;
/*  253: 483 */     double deltaPhi = phi1 - phi2;
/*  254: 485 */     if (findOptimalPointOnLine(i1, alpha1, alpha1Star, C1, i2, alpha2, alpha2Star, C2, gamma, eta, deltaPhi))
/*  255:     */     {
/*  256: 488 */       alpha1 = this.m_alpha[i1];
/*  257: 489 */       alpha1Star = this.m_alphaStar[i1];
/*  258: 490 */       alpha2 = this.m_alpha[i2];
/*  259: 491 */       alpha2Star = this.m_alphaStar[i2];
/*  260:     */       
/*  261:     */ 
/*  262:     */ 
/*  263:     */ 
/*  264:     */ 
/*  265:     */ 
/*  266:     */ 
/*  267:     */ 
/*  268:     */ 
/*  269:     */ 
/*  270:     */ 
/*  271:     */ 
/*  272:     */ 
/*  273: 505 */       double dAlpha1 = alpha1 - alpha1old - (alpha1Star - alpha1Starold);
/*  274: 506 */       double dAlpha2 = alpha2 - alpha2old - (alpha2Star - alpha2Starold);
/*  275: 507 */       for (int j = this.m_I0.getNext(-1); j != -1; j = this.m_I0.getNext(j)) {
/*  276: 508 */         if ((j != i1) && (j != i2)) {
/*  277: 509 */           this.m_error[j] -= dAlpha1 * this.m_kernel.eval(i1, j, this.m_data.instance(i1)) + dAlpha2 * this.m_kernel.eval(i2, j, this.m_data.instance(i2));
/*  278:     */         }
/*  279:     */       }
/*  280: 513 */       this.m_error[i1] -= dAlpha1 * k11 + dAlpha2 * k12;
/*  281: 514 */       this.m_error[i2] -= dAlpha1 * k12 + dAlpha2 * k22;
/*  282:     */       
/*  283: 516 */       updateIndexSetFor(i1, C1);
/*  284: 517 */       updateIndexSetFor(i2, C2);
/*  285:     */       
/*  286:     */ 
/*  287:     */ 
/*  288: 521 */       this.m_bUp = 1.7976931348623157E+308D;
/*  289: 522 */       this.m_bLow = -1.797693134862316E+308D;
/*  290: 523 */       for (int j = this.m_I0.getNext(-1); j != -1; j = this.m_I0.getNext(j)) {
/*  291: 524 */         updateBoundaries(j, this.m_error[j]);
/*  292:     */       }
/*  293: 526 */       if (!this.m_I0.contains(i1)) {
/*  294: 527 */         updateBoundaries(i1, this.m_error[i1]);
/*  295:     */       }
/*  296: 529 */       if (!this.m_I0.contains(i2)) {
/*  297: 530 */         updateBoundaries(i2, this.m_error[i2]);
/*  298:     */       }
/*  299: 533 */       return 1;
/*  300:     */     }
/*  301: 535 */     return 0;
/*  302:     */   }
/*  303:     */   
/*  304:     */   protected void updateIndexSetFor(int i, double C)
/*  305:     */     throws Exception
/*  306:     */   {
/*  307: 551 */     if ((this.m_alpha[i] == 0.0D) && (this.m_alphaStar[i] == 0.0D))
/*  308:     */     {
/*  309: 553 */       this.m_iSet[i] = 4;
/*  310: 554 */       this.m_I0.delete(i);
/*  311:     */     }
/*  312: 555 */     else if (this.m_alpha[i] > 0.0D)
/*  313:     */     {
/*  314: 556 */       if (this.m_alpha[i] < C)
/*  315:     */       {
/*  316: 557 */         if ((this.m_iSet[i] & 0x3) == 0) {
/*  317: 559 */           this.m_I0.insert(i);
/*  318:     */         }
/*  319: 562 */         this.m_iSet[i] = 1;
/*  320:     */       }
/*  321:     */       else
/*  322:     */       {
/*  323: 565 */         this.m_iSet[i] = 16;
/*  324: 566 */         this.m_I0.delete(i);
/*  325:     */       }
/*  326:     */     }
/*  327: 569 */     else if (this.m_alphaStar[i] < C)
/*  328:     */     {
/*  329: 570 */       if ((this.m_iSet[i] & 0x3) == 0) {
/*  330: 572 */         this.m_I0.insert(i);
/*  331:     */       }
/*  332: 575 */       this.m_iSet[i] = 2;
/*  333:     */     }
/*  334:     */     else
/*  335:     */     {
/*  336: 578 */       this.m_iSet[i] = 8;
/*  337: 579 */       this.m_I0.delete(i);
/*  338:     */     }
/*  339:     */   }
/*  340:     */   
/*  341:     */   protected void updateBoundaries(int i2, double F2)
/*  342:     */   {
/*  343: 591 */     int iSet = this.m_iSet[i2];
/*  344:     */     
/*  345: 593 */     double FLow = this.m_bLow;
/*  346: 594 */     if ((iSet & 0xA) > 0) {
/*  347: 595 */       FLow = F2 + this.m_epsilon;
/*  348: 596 */     } else if ((iSet & 0x5) > 0) {
/*  349: 597 */       FLow = F2 - this.m_epsilon;
/*  350:     */     }
/*  351: 599 */     if (this.m_bLow < FLow)
/*  352:     */     {
/*  353: 600 */       this.m_bLow = FLow;
/*  354: 601 */       this.m_iLow = i2;
/*  355:     */     }
/*  356: 603 */     double FUp = this.m_bUp;
/*  357: 604 */     if ((iSet & 0x11) > 0) {
/*  358: 605 */       FUp = F2 - this.m_epsilon;
/*  359: 606 */     } else if ((iSet & 0x6) > 0) {
/*  360: 607 */       FUp = F2 + this.m_epsilon;
/*  361:     */     }
/*  362: 609 */     if (this.m_bUp > FUp)
/*  363:     */     {
/*  364: 610 */       this.m_bUp = FUp;
/*  365: 611 */       this.m_iUp = i2;
/*  366:     */     }
/*  367:     */   }
/*  368:     */   
/*  369:     */   protected int examineExample(int i2)
/*  370:     */     throws Exception
/*  371:     */   {
/*  372: 647 */     int iSet = this.m_iSet[i2];
/*  373: 648 */     double F2 = this.m_error[i2];
/*  374: 649 */     if (!this.m_I0.contains(i2))
/*  375:     */     {
/*  376: 650 */       F2 = -SVMOutput(i2) - this.m_b + this.m_target[i2];
/*  377: 651 */       this.m_error[i2] = F2;
/*  378: 652 */       if (iSet == 4)
/*  379:     */       {
/*  380: 653 */         if (F2 + this.m_epsilon < this.m_bUp)
/*  381:     */         {
/*  382: 654 */           this.m_bUp = (F2 + this.m_epsilon);
/*  383: 655 */           this.m_iUp = i2;
/*  384:     */         }
/*  385: 656 */         else if (F2 - this.m_epsilon > this.m_bLow)
/*  386:     */         {
/*  387: 657 */           this.m_bLow = (F2 - this.m_epsilon);
/*  388: 658 */           this.m_iLow = i2;
/*  389:     */         }
/*  390:     */       }
/*  391: 660 */       else if ((iSet == 8) && (F2 + this.m_epsilon > this.m_bLow))
/*  392:     */       {
/*  393: 661 */         this.m_bLow = (F2 + this.m_epsilon);
/*  394: 662 */         this.m_iLow = i2;
/*  395:     */       }
/*  396: 663 */       else if ((iSet == 16) && (F2 - this.m_epsilon < this.m_bUp))
/*  397:     */       {
/*  398: 664 */         this.m_bUp = (F2 - this.m_epsilon);
/*  399: 665 */         this.m_iUp = i2;
/*  400:     */       }
/*  401:     */     }
/*  402: 730 */     int i1 = i2;
/*  403: 731 */     boolean bOptimality = true;
/*  404: 733 */     if (iSet == 1)
/*  405:     */     {
/*  406: 734 */       if (this.m_bLow - (F2 - this.m_epsilon) > 2.0D * this.m_fTolerance)
/*  407:     */       {
/*  408: 735 */         bOptimality = false;
/*  409: 736 */         i1 = this.m_iLow;
/*  410: 738 */         if (F2 - this.m_epsilon - this.m_bUp > this.m_bLow - (F2 - this.m_epsilon)) {
/*  411: 739 */           i1 = this.m_iUp;
/*  412:     */         }
/*  413:     */       }
/*  414: 741 */       else if (F2 - this.m_epsilon - this.m_bUp > 2.0D * this.m_fTolerance)
/*  415:     */       {
/*  416: 742 */         bOptimality = false;
/*  417: 743 */         i1 = this.m_iUp;
/*  418: 745 */         if (this.m_bLow - (F2 - this.m_epsilon) > F2 - this.m_epsilon - this.m_bUp) {
/*  419: 746 */           i1 = this.m_iLow;
/*  420:     */         }
/*  421:     */       }
/*  422:     */     }
/*  423: 750 */     else if (iSet == 2)
/*  424:     */     {
/*  425: 751 */       if (this.m_bLow - (F2 + this.m_epsilon) > 2.0D * this.m_fTolerance)
/*  426:     */       {
/*  427: 752 */         bOptimality = false;
/*  428: 753 */         i1 = this.m_iLow;
/*  429: 754 */         if (F2 + this.m_epsilon - this.m_bUp > this.m_bLow - (F2 + this.m_epsilon)) {
/*  430: 755 */           i1 = this.m_iUp;
/*  431:     */         }
/*  432:     */       }
/*  433: 757 */       else if (F2 + this.m_epsilon - this.m_bUp > 2.0D * this.m_fTolerance)
/*  434:     */       {
/*  435: 758 */         bOptimality = false;
/*  436: 759 */         i1 = this.m_iUp;
/*  437: 760 */         if (this.m_bLow - (F2 + this.m_epsilon) > F2 + this.m_epsilon - this.m_bUp) {
/*  438: 761 */           i1 = this.m_iLow;
/*  439:     */         }
/*  440:     */       }
/*  441:     */     }
/*  442: 765 */     else if (iSet == 4)
/*  443:     */     {
/*  444: 766 */       if (this.m_bLow - (F2 + this.m_epsilon) > 2.0D * this.m_fTolerance)
/*  445:     */       {
/*  446: 767 */         bOptimality = false;
/*  447: 768 */         i1 = this.m_iLow;
/*  448: 770 */         if (F2 + this.m_epsilon - this.m_bUp > this.m_bLow - (F2 + this.m_epsilon)) {
/*  449: 771 */           i1 = this.m_iUp;
/*  450:     */         }
/*  451:     */       }
/*  452: 773 */       else if (F2 - this.m_epsilon - this.m_bUp > 2.0D * this.m_fTolerance)
/*  453:     */       {
/*  454: 774 */         bOptimality = false;
/*  455: 775 */         i1 = this.m_iUp;
/*  456: 776 */         if (this.m_bLow - (F2 - this.m_epsilon) > F2 - this.m_epsilon - this.m_bUp) {
/*  457: 777 */           i1 = this.m_iLow;
/*  458:     */         }
/*  459:     */       }
/*  460:     */     }
/*  461: 781 */     else if (iSet == 8)
/*  462:     */     {
/*  463: 782 */       if (F2 + this.m_epsilon - this.m_bUp > 2.0D * this.m_fTolerance)
/*  464:     */       {
/*  465: 783 */         bOptimality = false;
/*  466: 784 */         i1 = this.m_iUp;
/*  467:     */       }
/*  468:     */     }
/*  469: 787 */     else if ((iSet == 16) && 
/*  470: 788 */       (this.m_bLow - (F2 - this.m_epsilon) > 2.0D * this.m_fTolerance))
/*  471:     */     {
/*  472: 789 */       bOptimality = false;
/*  473: 790 */       i1 = this.m_iLow;
/*  474:     */     }
/*  475: 801 */     if (bOptimality) {
/*  476: 802 */       return 0;
/*  477:     */     }
/*  478: 804 */     return takeStep(i1, i2, this.m_alpha[i2], this.m_alphaStar[i2], F2);
/*  479:     */   }
/*  480:     */   
/*  481:     */   protected void init(Instances data)
/*  482:     */     throws Exception
/*  483:     */   {
/*  484: 815 */     super.init(data);
/*  485:     */     
/*  486:     */ 
/*  487:     */ 
/*  488:     */ 
/*  489:     */ 
/*  490:     */ 
/*  491:     */ 
/*  492:     */ 
/*  493: 824 */     this.m_I0 = new SMOset(this.m_data.numInstances());
/*  494: 825 */     this.m_iSet = new int[this.m_data.numInstances()];
/*  495: 826 */     for (int i = 0; i < this.m_nInstances; i++) {
/*  496: 827 */       this.m_iSet[i] = 4;
/*  497:     */     }
/*  498: 830 */     this.m_iUp = 0;
/*  499: 831 */     this.m_bUp = (this.m_target[this.m_iUp] + this.m_epsilon);
/*  500: 832 */     this.m_iLow = this.m_iUp;
/*  501: 833 */     this.m_bLow = (this.m_target[this.m_iLow] - this.m_epsilon);
/*  502:     */     
/*  503: 835 */     this.m_error = new double[this.m_nInstances];
/*  504: 836 */     for (int i = 0; i < this.m_nInstances; i++) {
/*  505: 837 */       this.m_error[i] = this.m_target[i];
/*  506:     */     }
/*  507:     */   }
/*  508:     */   
/*  509:     */   protected void optimize1()
/*  510:     */     throws Exception
/*  511:     */   {
/*  512: 850 */     int nNumChanged = 0;
/*  513: 851 */     boolean bExamineAll = true;
/*  514: 854 */     while ((nNumChanged > 0) || (bExamineAll))
/*  515:     */     {
/*  516: 855 */       nNumChanged = 0;
/*  517: 866 */       if (bExamineAll) {
/*  518: 867 */         for (int i = 0; i < this.m_nInstances; i++) {
/*  519: 868 */           nNumChanged += examineExample(i);
/*  520:     */         }
/*  521:     */       } else {
/*  522: 871 */         for (int i = this.m_I0.getNext(-1); i != -1; i = this.m_I0.getNext(i))
/*  523:     */         {
/*  524: 873 */           nNumChanged += examineExample(i);
/*  525: 874 */           if (this.m_bLow - this.m_bUp < 2.0D * this.m_fTolerance)
/*  526:     */           {
/*  527: 875 */             nNumChanged = 0;
/*  528: 876 */             break;
/*  529:     */           }
/*  530:     */         }
/*  531:     */       }
/*  532: 886 */       if (bExamineAll) {
/*  533: 887 */         bExamineAll = false;
/*  534: 888 */       } else if (nNumChanged == 0) {
/*  535: 889 */         bExamineAll = true;
/*  536:     */       }
/*  537:     */     }
/*  538:     */   }
/*  539:     */   
/*  540:     */   protected void optimize2()
/*  541:     */     throws Exception
/*  542:     */   {
/*  543: 901 */     int nNumChanged = 0;
/*  544: 902 */     boolean bExamineAll = true;
/*  545: 905 */     while ((nNumChanged > 0) || (bExamineAll))
/*  546:     */     {
/*  547: 906 */       nNumChanged = 0;
/*  548: 928 */       if (bExamineAll)
/*  549:     */       {
/*  550: 929 */         for (int i = 0; i < this.m_nInstances; i++) {
/*  551: 930 */           nNumChanged += examineExample(i);
/*  552:     */         }
/*  553:     */       }
/*  554:     */       else
/*  555:     */       {
/*  556: 933 */         boolean bInnerLoopSuccess = true;
/*  557:     */         do
/*  558:     */         {
/*  559: 935 */           if (takeStep(this.m_iUp, this.m_iLow, this.m_alpha[this.m_iLow], this.m_alphaStar[this.m_iLow], this.m_error[this.m_iLow]) > 0)
/*  560:     */           {
/*  561: 937 */             bInnerLoopSuccess = true;
/*  562: 938 */             nNumChanged++;
/*  563:     */           }
/*  564:     */           else
/*  565:     */           {
/*  566: 940 */             bInnerLoopSuccess = false;
/*  567:     */           }
/*  568: 942 */         } while ((this.m_bUp <= this.m_bLow - 2.0D * this.m_fTolerance) && (bInnerLoopSuccess));
/*  569: 943 */         nNumChanged = 0;
/*  570:     */       }
/*  571: 953 */       if (bExamineAll) {
/*  572: 954 */         bExamineAll = false;
/*  573: 955 */       } else if (nNumChanged == 0) {
/*  574: 956 */         bExamineAll = true;
/*  575:     */       }
/*  576:     */     }
/*  577:     */   }
/*  578:     */   
/*  579:     */   protected void wrapUp()
/*  580:     */     throws Exception
/*  581:     */   {
/*  582: 969 */     this.m_b = (-(this.m_bLow + this.m_bUp) / 2.0D);
/*  583: 970 */     this.m_target = null;
/*  584: 971 */     this.m_error = null;
/*  585: 972 */     super.wrapUp();
/*  586:     */   }
/*  587:     */   
/*  588:     */   public void buildClassifier(Instances instances)
/*  589:     */     throws Exception
/*  590:     */   {
/*  591: 985 */     init(instances);
/*  592: 988 */     if (this.m_bUseVariant1) {
/*  593: 989 */       optimize1();
/*  594:     */     } else {
/*  595: 991 */       optimize2();
/*  596:     */     }
/*  597: 995 */     wrapUp();
/*  598:     */   }
/*  599:     */   
/*  600:     */   public String getRevision()
/*  601:     */   {
/*  602:1005 */     return RevisionUtils.extract("$Revision: 10169 $");
/*  603:     */   }
/*  604:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.supportVector.RegSMOImproved
 * JD-Core Version:    0.7.0.1
 */