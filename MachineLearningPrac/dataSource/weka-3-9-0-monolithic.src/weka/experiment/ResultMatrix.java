/*    1:     */ package weka.experiment;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.io.Serializable;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.Vector;
/*    7:     */ import weka.core.Option;
/*    8:     */ import weka.core.OptionHandler;
/*    9:     */ import weka.core.RevisionHandler;
/*   10:     */ import weka.core.Utils;
/*   11:     */ 
/*   12:     */ public abstract class ResultMatrix
/*   13:     */   implements Serializable, RevisionHandler, OptionHandler
/*   14:     */ {
/*   15:     */   private static final long serialVersionUID = 4487179306428209739L;
/*   16:     */   public static final int SIGNIFICANCE_TIE = 0;
/*   17:     */   public static final int SIGNIFICANCE_WIN = 1;
/*   18:     */   public static final int SIGNIFICANCE_LOSS = 2;
/*   19:  70 */   public String TIE_STRING = " ";
/*   20:  73 */   public String WIN_STRING = "v";
/*   21:  76 */   public String LOSS_STRING = "*";
/*   22:  79 */   public String LEFT_PARENTHESES = "(";
/*   23:  82 */   public String RIGHT_PARENTHESES = ")";
/*   24:  85 */   protected String[] m_ColNames = null;
/*   25:  88 */   protected String[] m_RowNames = null;
/*   26:  91 */   protected boolean[] m_ColHidden = null;
/*   27:  94 */   protected boolean[] m_RowHidden = null;
/*   28:  97 */   protected int[][] m_Significance = (int[][])null;
/*   29: 100 */   protected double[][] m_Mean = (double[][])null;
/*   30: 103 */   protected double[][] m_StdDev = (double[][])null;
/*   31: 106 */   protected double[] m_Counts = null;
/*   32:     */   protected int m_MeanPrec;
/*   33:     */   protected int m_StdDevPrec;
/*   34:     */   protected boolean m_ShowStdDev;
/*   35:     */   protected boolean m_ShowAverage;
/*   36:     */   protected boolean m_PrintColNames;
/*   37:     */   protected boolean m_PrintRowNames;
/*   38:     */   protected boolean m_EnumerateColNames;
/*   39:     */   protected boolean m_EnumerateRowNames;
/*   40:     */   protected int m_ColNameWidth;
/*   41:     */   protected int m_RowNameWidth;
/*   42:     */   protected int m_MeanWidth;
/*   43:     */   protected int m_StdDevWidth;
/*   44:     */   protected int m_SignificanceWidth;
/*   45:     */   protected int m_CountWidth;
/*   46: 153 */   protected Vector<String> m_HeaderKeys = null;
/*   47: 156 */   protected Vector<String> m_HeaderValues = null;
/*   48: 159 */   protected int[][] m_NonSigWins = (int[][])null;
/*   49: 162 */   protected int[][] m_Wins = (int[][])null;
/*   50: 165 */   protected int[] m_RankingWins = null;
/*   51: 168 */   protected int[] m_RankingLosses = null;
/*   52: 171 */   protected int[] m_RankingDiff = null;
/*   53: 174 */   protected int[] m_RowOrder = null;
/*   54: 177 */   protected int[] m_ColOrder = null;
/*   55: 180 */   protected boolean m_RemoveFilterName = false;
/*   56:     */   
/*   57:     */   public ResultMatrix()
/*   58:     */   {
/*   59: 186 */     this(1, 1);
/*   60:     */   }
/*   61:     */   
/*   62:     */   public ResultMatrix(int cols, int rows)
/*   63:     */   {
/*   64: 196 */     setSize(cols, rows);
/*   65: 197 */     clear();
/*   66:     */   }
/*   67:     */   
/*   68:     */   public ResultMatrix(ResultMatrix matrix)
/*   69:     */   {
/*   70: 206 */     assign(matrix);
/*   71:     */   }
/*   72:     */   
/*   73:     */   public abstract String globalInfo();
/*   74:     */   
/*   75:     */   public Enumeration<Option> listOptions()
/*   76:     */   {
/*   77: 225 */     Vector<Option> result = new Vector();
/*   78:     */     
/*   79: 227 */     result.addElement(new Option("\tThe number of decimals after the decimal point for the mean.\n\t(default: " + getDefaultMeanPrec() + ")", "mean-prec", 1, "-mean-prec <int>"));
/*   80:     */     
/*   81:     */ 
/*   82:     */ 
/*   83:     */ 
/*   84: 232 */     result.addElement(new Option("\tThe number of decimals after the decimal point for the mean.\n\t(default: " + getDefaultStdDevPrec() + ")", "stddev-prec", 1, "-stddev-prec <int>"));
/*   85:     */     
/*   86:     */ 
/*   87:     */ 
/*   88:     */ 
/*   89: 237 */     result.addElement(new Option("\tThe maximum width for the column names (0 = optimal).\n\t(default: " + getDefaultColNameWidth() + ")", "col-name-width", 1, "-col-name-width <int>"));
/*   90:     */     
/*   91:     */ 
/*   92:     */ 
/*   93:     */ 
/*   94: 242 */     result.addElement(new Option("\tThe maximum width for the row names (0 = optimal).\n\t(default: " + getDefaultRowNameWidth() + ")", "row-name-width", 1, "-row-name-width <int>"));
/*   95:     */     
/*   96:     */ 
/*   97:     */ 
/*   98:     */ 
/*   99: 247 */     result.addElement(new Option("\tThe width of the mean (0 = optimal).\n\t(default: " + getDefaultMeanWidth() + ")", "mean-width", 1, "-mean-width <int>"));
/*  100:     */     
/*  101:     */ 
/*  102:     */ 
/*  103: 251 */     result.addElement(new Option("\tThe width of the standard deviation (0 = optimal).\n\t(default: " + getDefaultStdDevWidth() + ")", "stddev-width", 1, "-stddev-width <int>"));
/*  104:     */     
/*  105:     */ 
/*  106:     */ 
/*  107:     */ 
/*  108: 256 */     result.addElement(new Option("\tThe width of the significance indicator (0 = optimal).\n\t(default: " + getDefaultSignificanceWidth() + ")", "sig-width", 1, "-sig-width <int>"));
/*  109:     */     
/*  110:     */ 
/*  111:     */ 
/*  112:     */ 
/*  113: 261 */     result.addElement(new Option("\tThe width of the counts (0 = optimal).\n\t(default: " + getDefaultCountWidth() + ")", "count-width", 1, "-count-width <int>"));
/*  114:     */     
/*  115:     */ 
/*  116:     */ 
/*  117: 265 */     result.addElement(new Option("\tWhether to display the standard deviation column.\n\t(default: no)", "show-stddev", 0, "-show-stddev"));
/*  118:     */     
/*  119:     */ 
/*  120:     */ 
/*  121: 269 */     result.addElement(new Option("\tWhether to show the row with averages.\n\t(default: no)", "show-avg", 0, "-show-avg"));
/*  122:     */     
/*  123:     */ 
/*  124: 272 */     result.addElement(new Option("\tWhether to remove the classname package prefixes from the\n\tfilter names in datasets.\n\t(default: no)", "remove-filter", 0, "-remove-filter"));
/*  125:     */     
/*  126:     */ 
/*  127:     */ 
/*  128:     */ 
/*  129: 277 */     result.addElement(new Option("\tWhether to output column names or just numbers representing them.\n\t(default: no)", "print-col-names", 0, "-print-col-names"));
/*  130:     */     
/*  131:     */ 
/*  132:     */ 
/*  133: 281 */     result.addElement(new Option("\tWhether to output row names or just numbers representing them.\n\t(default: no)", "print-row-names", 0, "-print-row-names"));
/*  134:     */     
/*  135:     */ 
/*  136:     */ 
/*  137: 285 */     result.addElement(new Option("\tWhether to enumerate the column names (prefixing them with \n\t'(x)', with 'x' being the index).\n\t(default: no)", "enum-col-names", 0, "-enum-col-names"));
/*  138:     */     
/*  139:     */ 
/*  140:     */ 
/*  141:     */ 
/*  142: 290 */     result.addElement(new Option("\tWhether to enumerate the row names (prefixing them with \n\t'(x)', with 'x' being the index).\n\t(default: no)", "enum-row-names", 0, "-enum-row-names"));
/*  143:     */     
/*  144:     */ 
/*  145:     */ 
/*  146:     */ 
/*  147: 295 */     return result.elements();
/*  148:     */   }
/*  149:     */   
/*  150:     */   public void setOptions(String[] options)
/*  151:     */     throws Exception
/*  152:     */   {
/*  153: 310 */     String tmpStr = Utils.getOption("mean-prec", options);
/*  154: 311 */     if (tmpStr.length() > 0) {
/*  155: 312 */       setMeanPrec(Integer.parseInt(tmpStr));
/*  156:     */     } else {
/*  157: 314 */       setMeanPrec(getDefaultMeanPrec());
/*  158:     */     }
/*  159: 317 */     tmpStr = Utils.getOption("stddev-prec", options);
/*  160: 318 */     if (tmpStr.length() > 0) {
/*  161: 319 */       setStdDevPrec(Integer.parseInt(tmpStr));
/*  162:     */     } else {
/*  163: 321 */       setStdDevPrec(getDefaultStdDevPrec());
/*  164:     */     }
/*  165: 324 */     tmpStr = Utils.getOption("col-name-width", options);
/*  166: 325 */     if (tmpStr.length() > 0) {
/*  167: 326 */       setColNameWidth(Integer.parseInt(tmpStr));
/*  168:     */     } else {
/*  169: 328 */       setColNameWidth(getDefaultColNameWidth());
/*  170:     */     }
/*  171: 331 */     tmpStr = Utils.getOption("row-name-width", options);
/*  172: 332 */     if (tmpStr.length() > 0) {
/*  173: 333 */       setRowNameWidth(Integer.parseInt(tmpStr));
/*  174:     */     } else {
/*  175: 335 */       setRowNameWidth(getDefaultRowNameWidth());
/*  176:     */     }
/*  177: 338 */     tmpStr = Utils.getOption("mean-width", options);
/*  178: 339 */     if (tmpStr.length() > 0) {
/*  179: 340 */       setMeanWidth(Integer.parseInt(tmpStr));
/*  180:     */     } else {
/*  181: 342 */       setMeanWidth(getDefaultMeanWidth());
/*  182:     */     }
/*  183: 345 */     tmpStr = Utils.getOption("stddev-width", options);
/*  184: 346 */     if (tmpStr.length() > 0) {
/*  185: 347 */       setStdDevWidth(Integer.parseInt(tmpStr));
/*  186:     */     } else {
/*  187: 349 */       setStdDevWidth(getDefaultStdDevWidth());
/*  188:     */     }
/*  189: 352 */     tmpStr = Utils.getOption("sig-width", options);
/*  190: 353 */     if (tmpStr.length() > 0) {
/*  191: 354 */       setSignificanceWidth(Integer.parseInt(tmpStr));
/*  192:     */     } else {
/*  193: 356 */       setSignificanceWidth(getDefaultSignificanceWidth());
/*  194:     */     }
/*  195: 359 */     tmpStr = Utils.getOption("count-width", options);
/*  196: 360 */     if (tmpStr.length() > 0) {
/*  197: 361 */       setStdDevPrec(Integer.parseInt(tmpStr));
/*  198:     */     } else {
/*  199: 363 */       setStdDevPrec(getDefaultCountWidth());
/*  200:     */     }
/*  201: 366 */     setShowStdDev(Utils.getFlag("show-stddev", options));
/*  202:     */     
/*  203: 368 */     setShowAverage(Utils.getFlag("show-avg", options));
/*  204:     */     
/*  205: 370 */     setRemoveFilterName(Utils.getFlag("remove-filter", options));
/*  206:     */     
/*  207: 372 */     setEnumerateColNames(Utils.getFlag("enum-col-names", options));
/*  208:     */     
/*  209: 374 */     setEnumerateRowNames(Utils.getFlag("enum-row-names", options));
/*  210:     */     
/*  211: 376 */     setPrintColNames(Utils.getFlag("print-col-names", options));
/*  212:     */     
/*  213: 378 */     setPrintRowNames(Utils.getFlag("print-row-names", options));
/*  214:     */   }
/*  215:     */   
/*  216:     */   public String[] getOptions()
/*  217:     */   {
/*  218: 390 */     Vector<String> result = new Vector();
/*  219:     */     
/*  220: 392 */     result.add("-mean-prec");
/*  221: 393 */     result.add("" + getMeanPrec());
/*  222:     */     
/*  223: 395 */     result.add("-stddev-prec");
/*  224: 396 */     result.add("" + getStdDevPrec());
/*  225:     */     
/*  226: 398 */     result.add("-col-name-width");
/*  227: 399 */     result.add("" + getColNameWidth());
/*  228:     */     
/*  229: 401 */     result.add("-row-name-width");
/*  230: 402 */     result.add("" + getRowNameWidth());
/*  231:     */     
/*  232: 404 */     result.add("-mean-width");
/*  233: 405 */     result.add("" + getMeanWidth());
/*  234:     */     
/*  235: 407 */     result.add("-stddev-width");
/*  236: 408 */     result.add("" + getStdDevWidth());
/*  237:     */     
/*  238: 410 */     result.add("-sig-width");
/*  239: 411 */     result.add("" + getSignificanceWidth());
/*  240:     */     
/*  241: 413 */     result.add("-count-width");
/*  242: 414 */     result.add("" + getCountWidth());
/*  243: 416 */     if (getShowStdDev()) {
/*  244: 417 */       result.add("-show-stddev");
/*  245:     */     }
/*  246: 420 */     if (getShowAverage()) {
/*  247: 421 */       result.add("-show-avg");
/*  248:     */     }
/*  249: 424 */     if (getRemoveFilterName()) {
/*  250: 425 */       result.add("-remove-filter");
/*  251:     */     }
/*  252: 428 */     if (getPrintColNames()) {
/*  253: 429 */       result.add("-print-col-names");
/*  254:     */     }
/*  255: 432 */     if (getPrintRowNames()) {
/*  256: 433 */       result.add("-print-row-names");
/*  257:     */     }
/*  258: 436 */     if (getEnumerateColNames()) {
/*  259: 437 */       result.add("-enum-col-names");
/*  260:     */     }
/*  261: 440 */     if (getEnumerateRowNames()) {
/*  262: 441 */       result.add("-enum-row-names");
/*  263:     */     }
/*  264: 444 */     return (String[])result.toArray(new String[result.size()]);
/*  265:     */   }
/*  266:     */   
/*  267:     */   public abstract String getDisplayName();
/*  268:     */   
/*  269:     */   public void assign(ResultMatrix matrix)
/*  270:     */   {
/*  271: 464 */     setSize(matrix.getColCount(), matrix.getRowCount());
/*  272:     */     
/*  273:     */ 
/*  274: 467 */     this.TIE_STRING = matrix.TIE_STRING;
/*  275: 468 */     this.WIN_STRING = matrix.WIN_STRING;
/*  276: 469 */     this.LOSS_STRING = matrix.LOSS_STRING;
/*  277: 470 */     this.LEFT_PARENTHESES = matrix.LEFT_PARENTHESES;
/*  278: 471 */     this.RIGHT_PARENTHESES = matrix.RIGHT_PARENTHESES;
/*  279: 472 */     this.m_MeanPrec = matrix.m_MeanPrec;
/*  280: 473 */     this.m_StdDevPrec = matrix.m_StdDevPrec;
/*  281: 474 */     this.m_ShowStdDev = matrix.m_ShowStdDev;
/*  282: 475 */     this.m_ShowAverage = matrix.m_ShowAverage;
/*  283: 476 */     this.m_PrintColNames = matrix.m_PrintColNames;
/*  284: 477 */     this.m_PrintRowNames = matrix.m_PrintRowNames;
/*  285: 478 */     this.m_EnumerateColNames = matrix.m_EnumerateColNames;
/*  286: 479 */     this.m_EnumerateRowNames = matrix.m_EnumerateRowNames;
/*  287: 480 */     this.m_RowNameWidth = matrix.m_RowNameWidth;
/*  288: 481 */     this.m_MeanWidth = matrix.m_MeanWidth;
/*  289: 482 */     this.m_StdDevWidth = matrix.m_StdDevWidth;
/*  290: 483 */     this.m_SignificanceWidth = matrix.m_SignificanceWidth;
/*  291: 484 */     this.m_CountWidth = matrix.m_CountWidth;
/*  292: 485 */     this.m_RemoveFilterName = matrix.m_RemoveFilterName;
/*  293:     */     
/*  294:     */ 
/*  295: 488 */     this.m_HeaderKeys = ((Vector)matrix.m_HeaderKeys.clone());
/*  296: 489 */     this.m_HeaderValues = ((Vector)matrix.m_HeaderValues.clone());
/*  297: 492 */     for (int i = 0; i < matrix.m_Mean.length; i++) {
/*  298: 493 */       for (int n = 0; n < matrix.m_Mean[i].length; n++)
/*  299:     */       {
/*  300: 494 */         this.m_Mean[i][n] = matrix.m_Mean[i][n];
/*  301: 495 */         this.m_StdDev[i][n] = matrix.m_StdDev[i][n];
/*  302: 496 */         this.m_Significance[i][n] = matrix.m_Significance[i][n];
/*  303:     */       }
/*  304:     */     }
/*  305: 500 */     for (i = 0; i < matrix.m_ColNames.length; i++)
/*  306:     */     {
/*  307: 501 */       this.m_ColNames[i] = matrix.m_ColNames[i];
/*  308: 502 */       this.m_ColHidden[i] = matrix.m_ColHidden[i];
/*  309:     */     }
/*  310: 505 */     for (i = 0; i < matrix.m_RowNames.length; i++)
/*  311:     */     {
/*  312: 506 */       this.m_RowNames[i] = matrix.m_RowNames[i];
/*  313: 507 */       this.m_RowHidden[i] = matrix.m_RowHidden[i];
/*  314:     */     }
/*  315: 510 */     for (i = 0; i < matrix.m_Counts.length; i++) {
/*  316: 511 */       this.m_Counts[i] = matrix.m_Counts[i];
/*  317:     */     }
/*  318: 515 */     if (matrix.m_NonSigWins != null)
/*  319:     */     {
/*  320: 516 */       this.m_NonSigWins = new int[matrix.m_NonSigWins.length][];
/*  321: 517 */       this.m_Wins = new int[matrix.m_NonSigWins.length][];
/*  322: 518 */       for (i = 0; i < matrix.m_NonSigWins.length; i++)
/*  323:     */       {
/*  324: 519 */         this.m_NonSigWins[i] = new int[matrix.m_NonSigWins[i].length];
/*  325: 520 */         this.m_Wins[i] = new int[matrix.m_NonSigWins[i].length];
/*  326: 522 */         for (int n = 0; n < matrix.m_NonSigWins[i].length; n++)
/*  327:     */         {
/*  328: 523 */           this.m_NonSigWins[i][n] = matrix.m_NonSigWins[i][n];
/*  329: 524 */           this.m_Wins[i][n] = matrix.m_Wins[i][n];
/*  330:     */         }
/*  331:     */       }
/*  332:     */     }
/*  333: 530 */     if (matrix.m_RankingWins != null)
/*  334:     */     {
/*  335: 531 */       this.m_RankingWins = new int[matrix.m_RankingWins.length];
/*  336: 532 */       this.m_RankingLosses = new int[matrix.m_RankingWins.length];
/*  337: 533 */       this.m_RankingDiff = new int[matrix.m_RankingWins.length];
/*  338: 534 */       for (i = 0; i < matrix.m_RankingWins.length; i++)
/*  339:     */       {
/*  340: 535 */         this.m_RankingWins[i] = matrix.m_RankingWins[i];
/*  341: 536 */         this.m_RankingLosses[i] = matrix.m_RankingLosses[i];
/*  342: 537 */         this.m_RankingDiff[i] = matrix.m_RankingDiff[i];
/*  343:     */       }
/*  344:     */     }
/*  345:     */   }
/*  346:     */   
/*  347:     */   public void clear()
/*  348:     */   {
/*  349: 547 */     this.m_MeanPrec = getDefaultMeanPrec();
/*  350: 548 */     this.m_StdDevPrec = getDefaultStdDevPrec();
/*  351: 549 */     this.m_ShowStdDev = getDefaultShowStdDev();
/*  352: 550 */     this.m_ShowAverage = getDefaultShowAverage();
/*  353: 551 */     this.m_RemoveFilterName = getDefaultRemoveFilterName();
/*  354: 552 */     this.m_PrintColNames = getDefaultPrintColNames();
/*  355: 553 */     this.m_PrintRowNames = getDefaultPrintRowNames();
/*  356: 554 */     this.m_EnumerateColNames = getDefaultEnumerateColNames();
/*  357: 555 */     this.m_EnumerateRowNames = getDefaultEnumerateRowNames();
/*  358: 556 */     this.m_RowNameWidth = getDefaultRowNameWidth();
/*  359: 557 */     this.m_ColNameWidth = getDefaultColNameWidth();
/*  360: 558 */     this.m_MeanWidth = getDefaultMeanWidth();
/*  361: 559 */     this.m_StdDevWidth = getDefaultStdDevWidth();
/*  362: 560 */     this.m_SignificanceWidth = getDefaultSignificanceWidth();
/*  363: 561 */     this.m_CountWidth = getDefaultCountWidth();
/*  364:     */     
/*  365: 563 */     setSize(getColCount(), getRowCount());
/*  366:     */   }
/*  367:     */   
/*  368:     */   public void setSize(int cols, int rows)
/*  369:     */   {
/*  370: 576 */     this.m_ColNames = new String[cols];
/*  371: 577 */     this.m_RowNames = new String[rows];
/*  372: 578 */     this.m_Counts = new double[rows];
/*  373: 579 */     this.m_ColHidden = new boolean[cols];
/*  374: 580 */     this.m_RowHidden = new boolean[rows];
/*  375: 581 */     this.m_Mean = new double[rows][cols];
/*  376: 582 */     this.m_Significance = new int[rows][cols];
/*  377: 583 */     this.m_StdDev = new double[rows][cols];
/*  378: 584 */     this.m_ColOrder = null;
/*  379: 585 */     this.m_RowOrder = null;
/*  380: 588 */     for (int i = 0; i < this.m_Mean.length; i++) {
/*  381: 589 */       for (int n = 0; n < this.m_Mean[i].length; n++) {
/*  382: 590 */         this.m_Mean[i][n] = (0.0D / 0.0D);
/*  383:     */       }
/*  384:     */     }
/*  385: 594 */     for (i = 0; i < this.m_ColNames.length; i++) {
/*  386: 595 */       this.m_ColNames[i] = ("col" + i);
/*  387:     */     }
/*  388: 597 */     for (i = 0; i < this.m_RowNames.length; i++) {
/*  389: 598 */       this.m_RowNames[i] = ("row" + i);
/*  390:     */     }
/*  391: 601 */     clearHeader();
/*  392: 602 */     clearSummary();
/*  393: 603 */     clearRanking();
/*  394:     */   }
/*  395:     */   
/*  396:     */   public void setMeanPrec(int prec)
/*  397:     */   {
/*  398: 612 */     if (prec >= 0) {
/*  399: 613 */       this.m_MeanPrec = prec;
/*  400:     */     }
/*  401:     */   }
/*  402:     */   
/*  403:     */   public int getMeanPrec()
/*  404:     */   {
/*  405: 623 */     return this.m_MeanPrec;
/*  406:     */   }
/*  407:     */   
/*  408:     */   public int getDefaultMeanPrec()
/*  409:     */   {
/*  410: 632 */     return 2;
/*  411:     */   }
/*  412:     */   
/*  413:     */   public String meanPrecTipText()
/*  414:     */   {
/*  415: 642 */     return "The number of decimals after the decimal point for the mean.";
/*  416:     */   }
/*  417:     */   
/*  418:     */   public void setStdDevPrec(int prec)
/*  419:     */   {
/*  420: 651 */     if (prec >= 0) {
/*  421: 652 */       this.m_StdDevPrec = prec;
/*  422:     */     }
/*  423:     */   }
/*  424:     */   
/*  425:     */   public int getStdDevPrec()
/*  426:     */   {
/*  427: 662 */     return this.m_StdDevPrec;
/*  428:     */   }
/*  429:     */   
/*  430:     */   public int getDefaultStdDevPrec()
/*  431:     */   {
/*  432: 671 */     return 2;
/*  433:     */   }
/*  434:     */   
/*  435:     */   public String stdDevPrecTipText()
/*  436:     */   {
/*  437: 681 */     return "The number of decimals after the decimal point for the standard deviation.";
/*  438:     */   }
/*  439:     */   
/*  440:     */   public void setColNameWidth(int width)
/*  441:     */   {
/*  442: 690 */     if (width >= 0) {
/*  443: 691 */       this.m_ColNameWidth = width;
/*  444:     */     }
/*  445:     */   }
/*  446:     */   
/*  447:     */   public int getColNameWidth()
/*  448:     */   {
/*  449: 701 */     return this.m_ColNameWidth;
/*  450:     */   }
/*  451:     */   
/*  452:     */   public int getDefaultColNameWidth()
/*  453:     */   {
/*  454: 710 */     return 0;
/*  455:     */   }
/*  456:     */   
/*  457:     */   public String colNameWidthTipText()
/*  458:     */   {
/*  459: 720 */     return "The maximum width of the column names (0 = optimal).";
/*  460:     */   }
/*  461:     */   
/*  462:     */   public void setRowNameWidth(int width)
/*  463:     */   {
/*  464: 729 */     if (width >= 0) {
/*  465: 730 */       this.m_RowNameWidth = width;
/*  466:     */     }
/*  467:     */   }
/*  468:     */   
/*  469:     */   public int getRowNameWidth()
/*  470:     */   {
/*  471: 740 */     return this.m_RowNameWidth;
/*  472:     */   }
/*  473:     */   
/*  474:     */   public int getDefaultRowNameWidth()
/*  475:     */   {
/*  476: 749 */     return 0;
/*  477:     */   }
/*  478:     */   
/*  479:     */   public String rowNameWidthTipText()
/*  480:     */   {
/*  481: 759 */     return "The maximum width for the row names (0 = optimal).";
/*  482:     */   }
/*  483:     */   
/*  484:     */   public void setMeanWidth(int width)
/*  485:     */   {
/*  486: 768 */     if (width >= 0) {
/*  487: 769 */       this.m_MeanWidth = width;
/*  488:     */     }
/*  489:     */   }
/*  490:     */   
/*  491:     */   public int getMeanWidth()
/*  492:     */   {
/*  493: 779 */     return this.m_MeanWidth;
/*  494:     */   }
/*  495:     */   
/*  496:     */   public int getDefaultMeanWidth()
/*  497:     */   {
/*  498: 788 */     return 0;
/*  499:     */   }
/*  500:     */   
/*  501:     */   public String meanWidthTipText()
/*  502:     */   {
/*  503: 798 */     return "The width of the mean (0 = optimal).";
/*  504:     */   }
/*  505:     */   
/*  506:     */   public void setStdDevWidth(int width)
/*  507:     */   {
/*  508: 807 */     if (width >= 0) {
/*  509: 808 */       this.m_StdDevWidth = width;
/*  510:     */     }
/*  511:     */   }
/*  512:     */   
/*  513:     */   public int getStdDevWidth()
/*  514:     */   {
/*  515: 818 */     return this.m_StdDevWidth;
/*  516:     */   }
/*  517:     */   
/*  518:     */   public int getDefaultStdDevWidth()
/*  519:     */   {
/*  520: 827 */     return 0;
/*  521:     */   }
/*  522:     */   
/*  523:     */   public String stdDevWidthTipText()
/*  524:     */   {
/*  525: 837 */     return "The width of the standard deviation (0 = optimal).";
/*  526:     */   }
/*  527:     */   
/*  528:     */   public void setSignificanceWidth(int width)
/*  529:     */   {
/*  530: 846 */     if (width >= 0) {
/*  531: 847 */       this.m_SignificanceWidth = width;
/*  532:     */     }
/*  533:     */   }
/*  534:     */   
/*  535:     */   public int getSignificanceWidth()
/*  536:     */   {
/*  537: 857 */     return this.m_SignificanceWidth;
/*  538:     */   }
/*  539:     */   
/*  540:     */   public int getDefaultSignificanceWidth()
/*  541:     */   {
/*  542: 866 */     return 0;
/*  543:     */   }
/*  544:     */   
/*  545:     */   public String significanceWidthTipText()
/*  546:     */   {
/*  547: 876 */     return "The width of the significance indicator (0 = optimal).";
/*  548:     */   }
/*  549:     */   
/*  550:     */   public void setCountWidth(int width)
/*  551:     */   {
/*  552: 885 */     if (width >= 0) {
/*  553: 886 */       this.m_CountWidth = width;
/*  554:     */     }
/*  555:     */   }
/*  556:     */   
/*  557:     */   public int getCountWidth()
/*  558:     */   {
/*  559: 896 */     return this.m_CountWidth;
/*  560:     */   }
/*  561:     */   
/*  562:     */   public int getDefaultCountWidth()
/*  563:     */   {
/*  564: 905 */     return 0;
/*  565:     */   }
/*  566:     */   
/*  567:     */   public String countWidthTipText()
/*  568:     */   {
/*  569: 915 */     return "The width of the counts (0 = optimal).";
/*  570:     */   }
/*  571:     */   
/*  572:     */   public void setShowStdDev(boolean show)
/*  573:     */   {
/*  574: 924 */     this.m_ShowStdDev = show;
/*  575:     */   }
/*  576:     */   
/*  577:     */   public boolean getShowStdDev()
/*  578:     */   {
/*  579: 933 */     return this.m_ShowStdDev;
/*  580:     */   }
/*  581:     */   
/*  582:     */   public boolean getDefaultShowStdDev()
/*  583:     */   {
/*  584: 942 */     return false;
/*  585:     */   }
/*  586:     */   
/*  587:     */   public String showStdDevTipText()
/*  588:     */   {
/*  589: 952 */     return "Whether to display the standard deviation column.";
/*  590:     */   }
/*  591:     */   
/*  592:     */   public void setShowAverage(boolean show)
/*  593:     */   {
/*  594: 961 */     this.m_ShowAverage = show;
/*  595:     */   }
/*  596:     */   
/*  597:     */   public boolean getShowAverage()
/*  598:     */   {
/*  599: 970 */     return this.m_ShowAverage;
/*  600:     */   }
/*  601:     */   
/*  602:     */   public boolean getDefaultShowAverage()
/*  603:     */   {
/*  604: 979 */     return false;
/*  605:     */   }
/*  606:     */   
/*  607:     */   public String showAverageTipText()
/*  608:     */   {
/*  609: 989 */     return "Whether to show the row with averages.";
/*  610:     */   }
/*  611:     */   
/*  612:     */   public void setRemoveFilterName(boolean remove)
/*  613:     */   {
/*  614: 998 */     this.m_RemoveFilterName = remove;
/*  615:     */   }
/*  616:     */   
/*  617:     */   public boolean getRemoveFilterName()
/*  618:     */   {
/*  619:1007 */     return this.m_RemoveFilterName;
/*  620:     */   }
/*  621:     */   
/*  622:     */   public boolean getDefaultRemoveFilterName()
/*  623:     */   {
/*  624:1017 */     return false;
/*  625:     */   }
/*  626:     */   
/*  627:     */   public String removeFilterNameTipText()
/*  628:     */   {
/*  629:1027 */     return "Whether to remove the classname package prefixes from the filter names in datasets.";
/*  630:     */   }
/*  631:     */   
/*  632:     */   public void setPrintColNames(boolean print)
/*  633:     */   {
/*  634:1038 */     this.m_PrintColNames = print;
/*  635:1039 */     if (!print) {
/*  636:1040 */       setEnumerateColNames(true);
/*  637:     */     }
/*  638:     */   }
/*  639:     */   
/*  640:     */   public boolean getPrintColNames()
/*  641:     */   {
/*  642:1050 */     return this.m_PrintColNames;
/*  643:     */   }
/*  644:     */   
/*  645:     */   public boolean getDefaultPrintColNames()
/*  646:     */   {
/*  647:1059 */     return true;
/*  648:     */   }
/*  649:     */   
/*  650:     */   public String printColNamesTipText()
/*  651:     */   {
/*  652:1069 */     return "Whether to output column names or just numbers representing them.";
/*  653:     */   }
/*  654:     */   
/*  655:     */   public void setPrintRowNames(boolean print)
/*  656:     */   {
/*  657:1080 */     this.m_PrintRowNames = print;
/*  658:1081 */     if (!print) {
/*  659:1082 */       setEnumerateRowNames(true);
/*  660:     */     }
/*  661:     */   }
/*  662:     */   
/*  663:     */   public boolean getPrintRowNames()
/*  664:     */   {
/*  665:1092 */     return this.m_PrintRowNames;
/*  666:     */   }
/*  667:     */   
/*  668:     */   public boolean getDefaultPrintRowNames()
/*  669:     */   {
/*  670:1101 */     return true;
/*  671:     */   }
/*  672:     */   
/*  673:     */   public String printRowNamesTipText()
/*  674:     */   {
/*  675:1111 */     return "Whether to output row names or just numbers representing them.";
/*  676:     */   }
/*  677:     */   
/*  678:     */   public void setEnumerateColNames(boolean enumerate)
/*  679:     */   {
/*  680:1121 */     this.m_EnumerateColNames = enumerate;
/*  681:     */   }
/*  682:     */   
/*  683:     */   public boolean getEnumerateColNames()
/*  684:     */   {
/*  685:1130 */     return this.m_EnumerateColNames;
/*  686:     */   }
/*  687:     */   
/*  688:     */   public boolean getDefaultEnumerateColNames()
/*  689:     */   {
/*  690:1139 */     return true;
/*  691:     */   }
/*  692:     */   
/*  693:     */   public String enumerateColNamesTipText()
/*  694:     */   {
/*  695:1149 */     return "Whether to enumerate the column names (prefixing them with '(x)', with 'x' being the index).";
/*  696:     */   }
/*  697:     */   
/*  698:     */   public void setEnumerateRowNames(boolean enumerate)
/*  699:     */   {
/*  700:1158 */     this.m_EnumerateRowNames = enumerate;
/*  701:     */   }
/*  702:     */   
/*  703:     */   public boolean getEnumerateRowNames()
/*  704:     */   {
/*  705:1167 */     return this.m_EnumerateRowNames;
/*  706:     */   }
/*  707:     */   
/*  708:     */   public boolean getDefaultEnumerateRowNames()
/*  709:     */   {
/*  710:1176 */     return false;
/*  711:     */   }
/*  712:     */   
/*  713:     */   public String enumerateRowNamesTipText()
/*  714:     */   {
/*  715:1186 */     return "Whether to enumerate the row names (prefixing them with '(x)', with 'x' being the index).";
/*  716:     */   }
/*  717:     */   
/*  718:     */   public int getColCount()
/*  719:     */   {
/*  720:1195 */     return this.m_ColNames.length;
/*  721:     */   }
/*  722:     */   
/*  723:     */   public int getVisibleColCount()
/*  724:     */   {
/*  725:1207 */     int cols = 0;
/*  726:1208 */     for (int i = 0; i < getColCount(); i++) {
/*  727:1209 */       if (!getColHidden(i)) {
/*  728:1210 */         cols++;
/*  729:     */       }
/*  730:     */     }
/*  731:1214 */     return cols;
/*  732:     */   }
/*  733:     */   
/*  734:     */   public int getRowCount()
/*  735:     */   {
/*  736:1223 */     return this.m_RowNames.length;
/*  737:     */   }
/*  738:     */   
/*  739:     */   public int getVisibleRowCount()
/*  740:     */   {
/*  741:1235 */     int rows = 0;
/*  742:1236 */     for (int i = 0; i < getRowCount(); i++) {
/*  743:1237 */       if (!getRowHidden(i)) {
/*  744:1238 */         rows++;
/*  745:     */       }
/*  746:     */     }
/*  747:1242 */     return rows;
/*  748:     */   }
/*  749:     */   
/*  750:     */   public void setColName(int index, String name)
/*  751:     */   {
/*  752:1252 */     if ((index >= 0) && (index < getColCount())) {
/*  753:1253 */       this.m_ColNames[index] = name;
/*  754:     */     }
/*  755:     */   }
/*  756:     */   
/*  757:     */   public String getColName(int index)
/*  758:     */   {
/*  759:1273 */     String result = null;
/*  760:1275 */     if ((index >= 0) && (index < getColCount()))
/*  761:     */     {
/*  762:1276 */       if (getPrintColNames()) {
/*  763:1277 */         result = this.m_ColNames[index];
/*  764:     */       } else {
/*  765:1279 */         result = "";
/*  766:     */       }
/*  767:1282 */       if (getEnumerateColNames())
/*  768:     */       {
/*  769:1283 */         result = this.LEFT_PARENTHESES + Integer.toString(index + 1) + this.RIGHT_PARENTHESES + " " + result;
/*  770:     */         
/*  771:1285 */         result = result.trim();
/*  772:     */       }
/*  773:     */     }
/*  774:1289 */     return result;
/*  775:     */   }
/*  776:     */   
/*  777:     */   public void setRowName(int index, String name)
/*  778:     */   {
/*  779:1299 */     if ((index >= 0) && (index < getRowCount())) {
/*  780:1300 */       this.m_RowNames[index] = name;
/*  781:     */     }
/*  782:     */   }
/*  783:     */   
/*  784:     */   public String getRowName(int index)
/*  785:     */   {
/*  786:1320 */     String result = null;
/*  787:1322 */     if ((index >= 0) && (index < getRowCount()))
/*  788:     */     {
/*  789:1323 */       if (getPrintRowNames()) {
/*  790:1324 */         result = this.m_RowNames[index];
/*  791:     */       } else {
/*  792:1326 */         result = "";
/*  793:     */       }
/*  794:1329 */       if (getEnumerateRowNames())
/*  795:     */       {
/*  796:1330 */         result = this.LEFT_PARENTHESES + Integer.toString(index + 1) + this.RIGHT_PARENTHESES + " " + result;
/*  797:     */         
/*  798:1332 */         result = result.trim();
/*  799:     */       }
/*  800:     */     }
/*  801:1336 */     return result;
/*  802:     */   }
/*  803:     */   
/*  804:     */   public void setColHidden(int index, boolean hidden)
/*  805:     */   {
/*  806:1346 */     if ((index >= 0) && (index < getColCount())) {
/*  807:1347 */       this.m_ColHidden[index] = hidden;
/*  808:     */     }
/*  809:     */   }
/*  810:     */   
/*  811:     */   public boolean getColHidden(int index)
/*  812:     */   {
/*  813:1359 */     if ((index >= 0) && (index < getColCount())) {
/*  814:1360 */       return this.m_ColHidden[index];
/*  815:     */     }
/*  816:1362 */     return false;
/*  817:     */   }
/*  818:     */   
/*  819:     */   public void setRowHidden(int index, boolean hidden)
/*  820:     */   {
/*  821:1373 */     if ((index >= 0) && (index < getRowCount())) {
/*  822:1374 */       this.m_RowHidden[index] = hidden;
/*  823:     */     }
/*  824:     */   }
/*  825:     */   
/*  826:     */   public boolean getRowHidden(int index)
/*  827:     */   {
/*  828:1386 */     if ((index >= 0) && (index < getRowCount())) {
/*  829:1387 */       return this.m_RowHidden[index];
/*  830:     */     }
/*  831:1389 */     return false;
/*  832:     */   }
/*  833:     */   
/*  834:     */   public void setCount(int index, double count)
/*  835:     */   {
/*  836:1400 */     if ((index >= 0) && (index < getRowCount())) {
/*  837:1401 */       this.m_Counts[index] = count;
/*  838:     */     }
/*  839:     */   }
/*  840:     */   
/*  841:     */   public double getCount(int index)
/*  842:     */   {
/*  843:1412 */     if ((index >= 0) && (index < getRowCount())) {
/*  844:1413 */       return this.m_Counts[index];
/*  845:     */     }
/*  846:1415 */     return 0.0D;
/*  847:     */   }
/*  848:     */   
/*  849:     */   public void setMean(int col, int row, double value)
/*  850:     */   {
/*  851:1427 */     if ((col >= 0) && (col < getColCount()) && (row >= 0) && (row < getRowCount())) {
/*  852:1429 */       this.m_Mean[row][col] = value;
/*  853:     */     }
/*  854:     */   }
/*  855:     */   
/*  856:     */   public double getMean(int col, int row)
/*  857:     */   {
/*  858:1442 */     if ((col >= 0) && (col < getColCount()) && (row >= 0) && (row < getRowCount())) {
/*  859:1444 */       return this.m_Mean[row][col];
/*  860:     */     }
/*  861:1446 */     return 0.0D;
/*  862:     */   }
/*  863:     */   
/*  864:     */   public double getAverage(int col)
/*  865:     */   {
/*  866:1462 */     if ((col >= 0) && (col < getColCount()))
/*  867:     */     {
/*  868:1463 */       double avg = 0.0D;
/*  869:1464 */       int count = 0;
/*  870:1466 */       for (int i = 0; i < getRowCount(); i++) {
/*  871:1467 */         if (!Double.isNaN(getMean(col, i)))
/*  872:     */         {
/*  873:1468 */           avg += getMean(col, i);
/*  874:1469 */           count++;
/*  875:     */         }
/*  876:     */       }
/*  877:1473 */       return avg / count;
/*  878:     */     }
/*  879:1475 */     return 0.0D;
/*  880:     */   }
/*  881:     */   
/*  882:     */   public void setStdDev(int col, int row, double value)
/*  883:     */   {
/*  884:1487 */     if ((col >= 0) && (col < getColCount()) && (row >= 0) && (row < getRowCount())) {
/*  885:1489 */       this.m_StdDev[row][col] = value;
/*  886:     */     }
/*  887:     */   }
/*  888:     */   
/*  889:     */   public double getStdDev(int col, int row)
/*  890:     */   {
/*  891:1502 */     if ((col >= 0) && (col < getColCount()) && (row >= 0) && (row < getRowCount())) {
/*  892:1504 */       return this.m_StdDev[row][col];
/*  893:     */     }
/*  894:1506 */     return 0.0D;
/*  895:     */   }
/*  896:     */   
/*  897:     */   public void setSignificance(int col, int row, int value)
/*  898:     */   {
/*  899:1518 */     if ((col >= 0) && (col < getColCount()) && (row >= 0) && (row < getRowCount())) {
/*  900:1520 */       this.m_Significance[row][col] = value;
/*  901:     */     }
/*  902:     */   }
/*  903:     */   
/*  904:     */   public int getSignificance(int col, int row)
/*  905:     */   {
/*  906:1533 */     if ((col >= 0) && (col < getColCount()) && (row >= 0) && (row < getRowCount())) {
/*  907:1535 */       return this.m_Significance[row][col];
/*  908:     */     }
/*  909:1537 */     return 0;
/*  910:     */   }
/*  911:     */   
/*  912:     */   public int getSignificanceCount(int col, int type)
/*  913:     */   {
/*  914:1552 */     int result = 0;
/*  915:1554 */     if ((col >= 0) && (col < getColCount())) {
/*  916:1555 */       for (int i = 0; i < getRowCount(); i++) {
/*  917:1556 */         if (!getRowHidden(i)) {
/*  918:1561 */           if (!Double.isNaN(getMean(col, i))) {
/*  919:1565 */             if (getSignificance(col, i) == type) {
/*  920:1566 */               result++;
/*  921:     */             }
/*  922:     */           }
/*  923:     */         }
/*  924:     */       }
/*  925:     */     }
/*  926:1571 */     return result;
/*  927:     */   }
/*  928:     */   
/*  929:     */   public void setRowOrder(int[] order)
/*  930:     */   {
/*  931:1583 */     if (order == null)
/*  932:     */     {
/*  933:1584 */       this.m_RowOrder = null;
/*  934:     */     }
/*  935:     */     else
/*  936:     */     {
/*  937:1586 */       if (order.length == getRowCount())
/*  938:     */       {
/*  939:1587 */         this.m_RowOrder = new int[order.length];
/*  940:1588 */         for (int i = 0; i < order.length; i++) {
/*  941:1589 */           this.m_RowOrder[i] = order[i];
/*  942:     */         }
/*  943:     */       }
/*  944:1592 */       System.err.println("setRowOrder: length does not match (" + order.length + " <> " + getRowCount() + ") - ignored!");
/*  945:     */     }
/*  946:     */   }
/*  947:     */   
/*  948:     */   public int[] getRowOrder()
/*  949:     */   {
/*  950:1604 */     return this.m_RowOrder;
/*  951:     */   }
/*  952:     */   
/*  953:     */   public int getDisplayRow(int index)
/*  954:     */   {
/*  955:1615 */     if ((index >= 0) && (index < getRowCount()))
/*  956:     */     {
/*  957:1616 */       if (getRowOrder() == null) {
/*  958:1617 */         return index;
/*  959:     */       }
/*  960:1619 */       return getRowOrder()[index];
/*  961:     */     }
/*  962:1622 */     return -1;
/*  963:     */   }
/*  964:     */   
/*  965:     */   public void setColOrder(int[] order)
/*  966:     */   {
/*  967:1635 */     if (order == null)
/*  968:     */     {
/*  969:1636 */       this.m_ColOrder = null;
/*  970:     */     }
/*  971:     */     else
/*  972:     */     {
/*  973:1638 */       if (order.length == getColCount())
/*  974:     */       {
/*  975:1639 */         this.m_ColOrder = new int[order.length];
/*  976:1640 */         for (int i = 0; i < order.length; i++) {
/*  977:1641 */           this.m_ColOrder[i] = order[i];
/*  978:     */         }
/*  979:     */       }
/*  980:1644 */       System.err.println("setColOrder: length does not match (" + order.length + " <> " + getColCount() + ") - ignored!");
/*  981:     */     }
/*  982:     */   }
/*  983:     */   
/*  984:     */   public int[] getColOrder()
/*  985:     */   {
/*  986:1656 */     return this.m_ColOrder;
/*  987:     */   }
/*  988:     */   
/*  989:     */   public int getDisplayCol(int index)
/*  990:     */   {
/*  991:1667 */     if ((index >= 0) && (index < getColCount()))
/*  992:     */     {
/*  993:1668 */       if (getColOrder() == null) {
/*  994:1669 */         return index;
/*  995:     */       }
/*  996:1671 */       return getColOrder()[index];
/*  997:     */     }
/*  998:1674 */     return -1;
/*  999:     */   }
/* 1000:     */   
/* 1001:     */   protected String doubleToString(double d, int prec)
/* 1002:     */   {
/* 1003:1691 */     String result = Utils.doubleToString(d, prec);
/* 1004:1694 */     if (result.indexOf(".") == -1) {
/* 1005:1695 */       result = result + ".";
/* 1006:     */     }
/* 1007:1699 */     int currentPrec = result.length() - result.indexOf(".") - 1;
/* 1008:1700 */     for (int i = currentPrec; i < prec; i++) {
/* 1009:1701 */       result = result + "0";
/* 1010:     */     }
/* 1011:1704 */     return result;
/* 1012:     */   }
/* 1013:     */   
/* 1014:     */   protected String trimString(String s, int length)
/* 1015:     */   {
/* 1016:1716 */     if ((length > 0) && (s.length() > length)) {
/* 1017:1717 */       return s.substring(0, length);
/* 1018:     */     }
/* 1019:1719 */     return s;
/* 1020:     */   }
/* 1021:     */   
/* 1022:     */   protected String padString(String s, int length)
/* 1023:     */   {
/* 1024:1732 */     return padString(s, length, false);
/* 1025:     */   }
/* 1026:     */   
/* 1027:     */   protected String padString(String s, int length, boolean left)
/* 1028:     */   {
/* 1029:1748 */     String result = s;
/* 1030:1751 */     for (int i = s.length(); i < length; i++) {
/* 1031:1752 */       if (left) {
/* 1032:1753 */         result = " " + result;
/* 1033:     */       } else {
/* 1034:1755 */         result = result + " ";
/* 1035:     */       }
/* 1036:     */     }
/* 1037:1760 */     if ((length > 0) && (result.length() > length)) {
/* 1038:1761 */       result = result.substring(0, length);
/* 1039:     */     }
/* 1040:1764 */     return result;
/* 1041:     */   }
/* 1042:     */   
/* 1043:     */   protected int getColSize(String[][] data, int col)
/* 1044:     */   {
/* 1045:1775 */     return getColSize(data, col, false, false);
/* 1046:     */   }
/* 1047:     */   
/* 1048:     */   protected int getColSize(String[][] data, int col, boolean skipFirst, boolean skipLast)
/* 1049:     */   {
/* 1050:1792 */     int result = 0;
/* 1051:1794 */     if ((col >= 0) && (col < data[0].length)) {
/* 1052:1795 */       for (int i = 0; i < data.length; i++) {
/* 1053:1797 */         if ((i != 0) || (!skipFirst)) {
/* 1054:1802 */           if ((i != data.length - 1) || (!skipLast)) {
/* 1055:1806 */             if (data[i][col].length() > result) {
/* 1056:1807 */               result = data[i][col].length();
/* 1057:     */             }
/* 1058:     */           }
/* 1059:     */         }
/* 1060:     */       }
/* 1061:     */     }
/* 1062:1812 */     return result;
/* 1063:     */   }
/* 1064:     */   
/* 1065:     */   protected String removeFilterName(String s)
/* 1066:     */   {
/* 1067:1824 */     if (getRemoveFilterName()) {
/* 1068:1825 */       return s.replaceAll("-weka\\.filters\\..*", "").replaceAll("-unsupervised\\..*", "").replaceAll("-supervised\\..*", "");
/* 1069:     */     }
/* 1070:1829 */     return s;
/* 1071:     */   }
/* 1072:     */   
/* 1073:     */   protected String[][] toArray()
/* 1074:     */   {
/* 1075:1856 */     int rows = getVisibleRowCount();
/* 1076:1857 */     if (getShowAverage()) {
/* 1077:1858 */       rows++;
/* 1078:     */     }
/* 1079:1860 */     int cols = getVisibleColCount();
/* 1080:1861 */     if (getShowStdDev()) {
/* 1081:1862 */       cols *= 3;
/* 1082:     */     } else {
/* 1083:1864 */       cols *= 2;
/* 1084:     */     }
/* 1085:1867 */     String[][] result = new String[rows + 2][cols + 1];
/* 1086:     */     
/* 1087:     */ 
/* 1088:1870 */     result[0][0] = trimString("Dataset", getRowNameWidth());
/* 1089:1871 */     int x = 1;
/* 1090:1872 */     for (int ii = 0; ii < getColCount(); ii++)
/* 1091:     */     {
/* 1092:1873 */       int i = getDisplayCol(ii);
/* 1093:1874 */       if (!getColHidden(i))
/* 1094:     */       {
/* 1095:1878 */         result[0][x] = trimString(removeFilterName(getColName(i)), getColNameWidth());
/* 1096:     */         
/* 1097:1880 */         x++;
/* 1098:1882 */         if (getShowStdDev())
/* 1099:     */         {
/* 1100:1883 */           result[0][x] = "";
/* 1101:1884 */           x++;
/* 1102:     */         }
/* 1103:1887 */         result[0][x] = "";
/* 1104:1888 */         x++;
/* 1105:     */       }
/* 1106:     */     }
/* 1107:1892 */     int y = 1;
/* 1108:1893 */     for (ii = 0; ii < getRowCount(); ii++)
/* 1109:     */     {
/* 1110:1894 */       int i = getDisplayRow(ii);
/* 1111:1895 */       if (!getRowHidden(i))
/* 1112:     */       {
/* 1113:1896 */         result[y][0] = trimString(removeFilterName(getRowName(i)), getRowNameWidth());
/* 1114:     */         
/* 1115:1898 */         y++;
/* 1116:     */       }
/* 1117:     */     }
/* 1118:1903 */     y = 1;
/* 1119:1904 */     for (ii = 0; ii < getRowCount(); ii++)
/* 1120:     */     {
/* 1121:1905 */       int i = getDisplayRow(ii);
/* 1122:1906 */       if (!getRowHidden(i))
/* 1123:     */       {
/* 1124:1910 */         x = 1;
/* 1125:1911 */         for (int nn = 0; nn < getColCount(); nn++)
/* 1126:     */         {
/* 1127:1912 */           int n = getDisplayCol(nn);
/* 1128:1913 */           if (!getColHidden(n))
/* 1129:     */           {
/* 1130:1918 */             boolean valueExists = !Double.isNaN(getMean(n, i));
/* 1131:1921 */             if (!valueExists) {
/* 1132:1922 */               result[y][x] = "";
/* 1133:     */             } else {
/* 1134:1924 */               result[y][x] = doubleToString(getMean(n, i), getMeanPrec());
/* 1135:     */             }
/* 1136:1926 */             x++;
/* 1137:1929 */             if (getShowStdDev())
/* 1138:     */             {
/* 1139:1930 */               if (!valueExists) {
/* 1140:1931 */                 result[y][x] = "";
/* 1141:1932 */               } else if (Double.isInfinite(getStdDev(n, i))) {
/* 1142:1933 */                 result[y][x] = "Inf";
/* 1143:     */               } else {
/* 1144:1935 */                 result[y][x] = doubleToString(getStdDev(n, i), getStdDevPrec());
/* 1145:     */               }
/* 1146:1937 */               x++;
/* 1147:     */             }
/* 1148:1941 */             if (!valueExists) {
/* 1149:1942 */               result[y][x] = "";
/* 1150:     */             } else {
/* 1151:1944 */               switch (getSignificance(n, i))
/* 1152:     */               {
/* 1153:     */               case 0: 
/* 1154:1946 */                 result[y][x] = this.TIE_STRING;
/* 1155:1947 */                 break;
/* 1156:     */               case 1: 
/* 1157:1949 */                 result[y][x] = this.WIN_STRING;
/* 1158:1950 */                 break;
/* 1159:     */               case 2: 
/* 1160:1952 */                 result[y][x] = this.LOSS_STRING;
/* 1161:     */               }
/* 1162:     */             }
/* 1163:1956 */             x++;
/* 1164:     */           }
/* 1165:     */         }
/* 1166:1959 */         y++;
/* 1167:     */       }
/* 1168:     */     }
/* 1169:1963 */     if (getShowAverage())
/* 1170:     */     {
/* 1171:1964 */       y = result.length - 2;
/* 1172:1965 */       x = 0;
/* 1173:1966 */       result[y][0] = "Average";
/* 1174:1967 */       x++;
/* 1175:1968 */       for (ii = 0; ii < getColCount(); ii++)
/* 1176:     */       {
/* 1177:1969 */         int i = getDisplayCol(ii);
/* 1178:1970 */         if (!getColHidden(i))
/* 1179:     */         {
/* 1180:1975 */           result[y][x] = doubleToString(getAverage(i), getMeanPrec());
/* 1181:1976 */           x++;
/* 1182:1979 */           if (getShowStdDev())
/* 1183:     */           {
/* 1184:1980 */             result[y][x] = "";
/* 1185:1981 */             x++;
/* 1186:     */           }
/* 1187:1985 */           result[y][x] = "";
/* 1188:1986 */           x++;
/* 1189:     */         }
/* 1190:     */       }
/* 1191:     */     }
/* 1192:1991 */     y = result.length - 1;
/* 1193:1992 */     x = 0;
/* 1194:1993 */     result[y][0] = (this.LEFT_PARENTHESES + this.WIN_STRING + "/" + this.TIE_STRING + "/" + this.LOSS_STRING + this.RIGHT_PARENTHESES);
/* 1195:     */     
/* 1196:1995 */     x++;
/* 1197:1996 */     for (ii = 0; ii < getColCount(); ii++)
/* 1198:     */     {
/* 1199:1997 */       int i = getDisplayCol(ii);
/* 1200:1998 */       if (!getColHidden(i))
/* 1201:     */       {
/* 1202:2003 */         result[y][x] = "";
/* 1203:2004 */         x++;
/* 1204:2007 */         if (getShowStdDev())
/* 1205:     */         {
/* 1206:2008 */           result[y][x] = "";
/* 1207:2009 */           x++;
/* 1208:     */         }
/* 1209:2013 */         result[y][x] = (this.LEFT_PARENTHESES + getSignificanceCount(i, 1) + "/" + getSignificanceCount(i, 0) + "/" + getSignificanceCount(i, 2) + this.RIGHT_PARENTHESES);
/* 1210:     */         
/* 1211:     */ 
/* 1212:     */ 
/* 1213:2017 */         x++;
/* 1214:     */       }
/* 1215:     */     }
/* 1216:2021 */     String[][] tmpResult = new String[result.length][result[0].length - 1];
/* 1217:     */     
/* 1218:2023 */     x = 0;
/* 1219:2024 */     for (int i = 0; i < result[0].length; i++) {
/* 1220:2026 */       if (((i != 3) || (!getShowStdDev())) && ((i != 2) || (getShowStdDev())))
/* 1221:     */       {
/* 1222:2030 */         for (int n = 0; n < result.length; n++) {
/* 1223:2031 */           tmpResult[n][x] = result[n][i];
/* 1224:     */         }
/* 1225:2034 */         x++;
/* 1226:     */       }
/* 1227:     */     }
/* 1228:2036 */     result = tmpResult;
/* 1229:     */     
/* 1230:2038 */     return result;
/* 1231:     */   }
/* 1232:     */   
/* 1233:     */   protected boolean isRowName(int index)
/* 1234:     */   {
/* 1235:2049 */     return index == 0;
/* 1236:     */   }
/* 1237:     */   
/* 1238:     */   protected boolean isMean(int index)
/* 1239:     */   {
/* 1240:     */     
/* 1241:2061 */     if (index == 0) {
/* 1242:2062 */       return true;
/* 1243:     */     }
/* 1244:2064 */     index--;
/* 1245:2066 */     if (index < 0) {
/* 1246:2067 */       return false;
/* 1247:     */     }
/* 1248:2070 */     if (getShowStdDev()) {
/* 1249:2071 */       return index % 3 == 1;
/* 1250:     */     }
/* 1251:2073 */     return index % 2 == 0;
/* 1252:     */   }
/* 1253:     */   
/* 1254:     */   protected boolean isAverage(int rowIndex)
/* 1255:     */   {
/* 1256:2086 */     if (getShowAverage()) {
/* 1257:2087 */       return getVisibleRowCount() + 1 == rowIndex;
/* 1258:     */     }
/* 1259:2089 */     return false;
/* 1260:     */   }
/* 1261:     */   
/* 1262:     */   protected boolean isStdDev(int index)
/* 1263:     */   {
/* 1264:2101 */     index--;
/* 1265:2102 */     index--;
/* 1266:2104 */     if (getShowStdDev())
/* 1267:     */     {
/* 1268:2105 */       if (index == 0) {
/* 1269:2106 */         return true;
/* 1270:     */       }
/* 1271:2108 */       index--;
/* 1272:2110 */       if (index < 0) {
/* 1273:2111 */         return false;
/* 1274:     */       }
/* 1275:2114 */       return index % 3 == 1;
/* 1276:     */     }
/* 1277:2117 */     return false;
/* 1278:     */   }
/* 1279:     */   
/* 1280:     */   protected boolean isSignificance(int index)
/* 1281:     */   {
/* 1282:2129 */     index--;
/* 1283:2130 */     index--;
/* 1284:2131 */     if (getShowStdDev())
/* 1285:     */     {
/* 1286:2132 */       index--;
/* 1287:2134 */       if (index < 0) {
/* 1288:2135 */         return false;
/* 1289:     */       }
/* 1290:2138 */       return index % 3 == 2;
/* 1291:     */     }
/* 1292:2140 */     if (index < 0) {
/* 1293:2141 */       return false;
/* 1294:     */     }
/* 1295:2144 */     return index % 2 == 1;
/* 1296:     */   }
/* 1297:     */   
/* 1298:     */   public abstract String toStringMatrix();
/* 1299:     */   
/* 1300:     */   public String toString()
/* 1301:     */   {
/* 1302:2163 */     return toStringMatrix();
/* 1303:     */   }
/* 1304:     */   
/* 1305:     */   public void clearHeader()
/* 1306:     */   {
/* 1307:2170 */     this.m_HeaderKeys = new Vector();
/* 1308:2171 */     this.m_HeaderValues = new Vector();
/* 1309:     */   }
/* 1310:     */   
/* 1311:     */   public void addHeader(String key, String value)
/* 1312:     */   {
/* 1313:2183 */     int pos = this.m_HeaderKeys.indexOf(key);
/* 1314:2184 */     if (pos > -1)
/* 1315:     */     {
/* 1316:2185 */       this.m_HeaderValues.set(pos, value);
/* 1317:     */     }
/* 1318:     */     else
/* 1319:     */     {
/* 1320:2187 */       this.m_HeaderKeys.add(key);
/* 1321:2188 */       this.m_HeaderValues.add(value);
/* 1322:     */     }
/* 1323:     */   }
/* 1324:     */   
/* 1325:     */   public String getHeader(String key)
/* 1326:     */   {
/* 1327:2202 */     int pos = this.m_HeaderKeys.indexOf(key);
/* 1328:2203 */     if (pos == 0) {
/* 1329:2204 */       return null;
/* 1330:     */     }
/* 1331:2206 */     return (String)this.m_HeaderKeys.get(pos);
/* 1332:     */   }
/* 1333:     */   
/* 1334:     */   public Enumeration<String> headerKeys()
/* 1335:     */   {
/* 1336:2216 */     return this.m_HeaderKeys.elements();
/* 1337:     */   }
/* 1338:     */   
/* 1339:     */   public abstract String toStringHeader();
/* 1340:     */   
/* 1341:     */   public abstract String toStringKey();
/* 1342:     */   
/* 1343:     */   public void clearSummary()
/* 1344:     */   {
/* 1345:2240 */     this.m_NonSigWins = ((int[][])null);
/* 1346:2241 */     this.m_Wins = ((int[][])null);
/* 1347:     */   }
/* 1348:     */   
/* 1349:     */   public void setSummary(int[][] nonSigWins, int[][] wins)
/* 1350:     */   {
/* 1351:2254 */     this.m_NonSigWins = new int[nonSigWins.length][nonSigWins[0].length];
/* 1352:2255 */     this.m_Wins = new int[wins.length][wins[0].length];
/* 1353:2257 */     for (int i = 0; i < this.m_NonSigWins.length; i++) {
/* 1354:2258 */       for (int n = 0; n < this.m_NonSigWins[i].length; n++)
/* 1355:     */       {
/* 1356:2259 */         this.m_NonSigWins[i][n] = nonSigWins[i][n];
/* 1357:2260 */         this.m_Wins[i][n] = wins[i][n];
/* 1358:     */       }
/* 1359:     */     }
/* 1360:     */   }
/* 1361:     */   
/* 1362:     */   protected String getSummaryTitle(int col)
/* 1363:     */   {
/* 1364:2272 */     return "" + (char)(97 + col % 26);
/* 1365:     */   }
/* 1366:     */   
/* 1367:     */   public abstract String toStringSummary();
/* 1368:     */   
/* 1369:     */   public void clearRanking()
/* 1370:     */   {
/* 1371:2286 */     this.m_RankingWins = null;
/* 1372:2287 */     this.m_RankingLosses = null;
/* 1373:2288 */     this.m_RankingDiff = null;
/* 1374:     */   }
/* 1375:     */   
/* 1376:     */   public void setRanking(int[][] wins)
/* 1377:     */   {
/* 1378:2300 */     this.m_RankingWins = new int[wins.length];
/* 1379:2301 */     this.m_RankingLosses = new int[wins.length];
/* 1380:2302 */     this.m_RankingDiff = new int[wins.length];
/* 1381:2304 */     for (int i = 0; i < wins.length; i++) {
/* 1382:2305 */       for (int j = 0; j < wins[i].length; j++)
/* 1383:     */       {
/* 1384:2306 */         this.m_RankingWins[j] += wins[i][j];
/* 1385:2307 */         this.m_RankingDiff[j] += wins[i][j];
/* 1386:2308 */         this.m_RankingLosses[i] += wins[i][j];
/* 1387:2309 */         this.m_RankingDiff[i] -= wins[i][j];
/* 1388:     */       }
/* 1389:     */     }
/* 1390:     */   }
/* 1391:     */   
/* 1392:     */   public abstract String toStringRanking();
/* 1393:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.ResultMatrix
 * JD-Core Version:    0.7.0.1
 */