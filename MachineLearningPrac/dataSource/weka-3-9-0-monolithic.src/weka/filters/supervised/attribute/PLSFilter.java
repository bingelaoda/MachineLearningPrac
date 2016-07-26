/*    1:     */ package weka.filters.supervised.attribute;
/*    2:     */ 
/*    3:     */ import java.util.ArrayList;
/*    4:     */ import java.util.Collections;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.Vector;
/*    7:     */ import weka.core.Attribute;
/*    8:     */ import weka.core.Capabilities;
/*    9:     */ import weka.core.Capabilities.Capability;
/*   10:     */ import weka.core.DenseInstance;
/*   11:     */ import weka.core.Instance;
/*   12:     */ import weka.core.Instances;
/*   13:     */ import weka.core.Option;
/*   14:     */ import weka.core.RevisionUtils;
/*   15:     */ import weka.core.SelectedTag;
/*   16:     */ import weka.core.Tag;
/*   17:     */ import weka.core.TechnicalInformation;
/*   18:     */ import weka.core.TechnicalInformation.Field;
/*   19:     */ import weka.core.TechnicalInformation.Type;
/*   20:     */ import weka.core.TechnicalInformationHandler;
/*   21:     */ import weka.core.Utils;
/*   22:     */ import weka.core.matrix.EigenvalueDecomposition;
/*   23:     */ import weka.core.matrix.Matrix;
/*   24:     */ import weka.filters.Filter;
/*   25:     */ import weka.filters.SimpleBatchFilter;
/*   26:     */ import weka.filters.SupervisedFilter;
/*   27:     */ import weka.filters.unsupervised.attribute.Center;
/*   28:     */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*   29:     */ import weka.filters.unsupervised.attribute.Standardize;
/*   30:     */ 
/*   31:     */ public class PLSFilter
/*   32:     */   extends SimpleBatchFilter
/*   33:     */   implements SupervisedFilter, TechnicalInformationHandler
/*   34:     */ {
/*   35:     */   static final long serialVersionUID = -3335106965521265631L;
/*   36:     */   public static final int ALGORITHM_SIMPLS = 1;
/*   37:     */   public static final int ALGORITHM_PLS1 = 2;
/*   38: 163 */   public static final Tag[] TAGS_ALGORITHM = { new Tag(1, "SIMPLS"), new Tag(2, "PLS1") };
/*   39:     */   public static final int PREPROCESSING_NONE = 0;
/*   40:     */   public static final int PREPROCESSING_CENTER = 1;
/*   41:     */   public static final int PREPROCESSING_STANDARDIZE = 2;
/*   42: 173 */   public static final Tag[] TAGS_PREPROCESSING = { new Tag(0, "none"), new Tag(1, "center"), new Tag(2, "standardize") };
/*   43: 179 */   protected int m_NumComponents = 20;
/*   44: 182 */   protected int m_Algorithm = 2;
/*   45: 185 */   protected Matrix m_PLS1_RegVector = null;
/*   46: 188 */   protected Matrix m_PLS1_P = null;
/*   47: 191 */   protected Matrix m_PLS1_W = null;
/*   48: 194 */   protected Matrix m_PLS1_b_hat = null;
/*   49: 197 */   protected Matrix m_SIMPLS_W = null;
/*   50: 200 */   protected Matrix m_SIMPLS_B = null;
/*   51: 203 */   protected boolean m_PerformPrediction = false;
/*   52: 206 */   protected Filter m_Missing = null;
/*   53: 209 */   protected boolean m_ReplaceMissing = true;
/*   54: 212 */   protected Filter m_Filter = null;
/*   55: 215 */   protected int m_Preprocessing = 1;
/*   56: 218 */   protected double m_ClassMean = 0.0D;
/*   57: 221 */   protected double m_ClassStdDev = 0.0D;
/*   58:     */   
/*   59:     */   public PLSFilter()
/*   60:     */   {
/*   61: 230 */     this.m_Missing = new ReplaceMissingValues();
/*   62: 231 */     this.m_Filter = new Center();
/*   63:     */   }
/*   64:     */   
/*   65:     */   public String globalInfo()
/*   66:     */   {
/*   67: 242 */     return "Runs Partial Least Square Regression over the given instances and computes the resulting beta matrix for prediction.\nBy default it replaces missing values and centers the data.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/*   68:     */   }
/*   69:     */   
/*   70:     */   public TechnicalInformation getTechnicalInformation()
/*   71:     */   {
/*   72: 260 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.BOOK);
/*   73: 261 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Tormod Naes and Tomas Isaksson and Tom Fearn and Tony Davies");
/*   74:     */     
/*   75: 263 */     result.setValue(TechnicalInformation.Field.YEAR, "2002");
/*   76: 264 */     result.setValue(TechnicalInformation.Field.TITLE, "A User Friendly Guide to Multivariate Calibration and Classification");
/*   77:     */     
/*   78: 266 */     result.setValue(TechnicalInformation.Field.PUBLISHER, "NIR Publications");
/*   79: 267 */     result.setValue(TechnicalInformation.Field.ISBN, "0-9528666-2-5");
/*   80:     */     
/*   81: 269 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.MISC);
/*   82: 270 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "StatSoft, Inc.");
/*   83: 271 */     additional.setValue(TechnicalInformation.Field.TITLE, "Partial Least Squares (PLS)");
/*   84: 272 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "Electronic Textbook StatSoft");
/*   85: 273 */     additional.setValue(TechnicalInformation.Field.HTTP, "http://www.statsoft.com/textbook/stpls.html");
/*   86:     */     
/*   87:     */ 
/*   88: 276 */     additional = result.add(TechnicalInformation.Type.MISC);
/*   89: 277 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Bent Jorgensen and Yuri Goegebeur");
/*   90: 278 */     additional.setValue(TechnicalInformation.Field.TITLE, "Module 7: Partial least squares regression I");
/*   91:     */     
/*   92: 280 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "ST02: Multivariate Data Analysis and Chemometrics");
/*   93:     */     
/*   94: 282 */     additional.setValue(TechnicalInformation.Field.HTTP, "http://statmaster.sdu.dk/courses/ST02/module07/");
/*   95:     */     
/*   96:     */ 
/*   97: 285 */     additional = result.add(TechnicalInformation.Type.ARTICLE);
/*   98: 286 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "S. de Jong");
/*   99: 287 */     additional.setValue(TechnicalInformation.Field.YEAR, "1993");
/*  100: 288 */     additional.setValue(TechnicalInformation.Field.TITLE, "SIMPLS: an alternative approach to partial least squares regression");
/*  101:     */     
/*  102: 290 */     additional.setValue(TechnicalInformation.Field.JOURNAL, "Chemometrics and Intelligent Laboratory Systems");
/*  103:     */     
/*  104: 292 */     additional.setValue(TechnicalInformation.Field.VOLUME, "18");
/*  105: 293 */     additional.setValue(TechnicalInformation.Field.PAGES, "251-263");
/*  106:     */     
/*  107: 295 */     return result;
/*  108:     */   }
/*  109:     */   
/*  110:     */   public Enumeration<Option> listOptions()
/*  111:     */   {
/*  112: 306 */     Vector<Option> result = new Vector();
/*  113:     */     
/*  114: 308 */     result.addElement(new Option("\tThe number of components to compute.\n\t(default: 20)", "C", 1, "-C <num>"));
/*  115:     */     
/*  116:     */ 
/*  117: 311 */     result.addElement(new Option("\tUpdates the class attribute as well.\n\t(default: off)", "U", 0, "-U"));
/*  118:     */     
/*  119:     */ 
/*  120: 314 */     result.addElement(new Option("\tTurns replacing of missing values on.\n\t(default: off)", "M", 0, "-M"));
/*  121:     */     
/*  122:     */ 
/*  123: 317 */     String param = "";
/*  124: 318 */     for (int i = 0; i < TAGS_ALGORITHM.length; i++)
/*  125:     */     {
/*  126: 319 */       if (i > 0) {
/*  127: 320 */         param = param + "|";
/*  128:     */       }
/*  129: 322 */       SelectedTag tag = new SelectedTag(TAGS_ALGORITHM[i].getID(), TAGS_ALGORITHM);
/*  130:     */       
/*  131: 324 */       param = param + tag.getSelectedTag().getReadable();
/*  132:     */     }
/*  133: 326 */     result.addElement(new Option("\tThe algorithm to use.\n\t(default: PLS1)", "A", 1, "-A <" + param + ">"));
/*  134:     */     
/*  135:     */ 
/*  136: 329 */     param = "";
/*  137: 330 */     for (int i = 0; i < TAGS_PREPROCESSING.length; i++)
/*  138:     */     {
/*  139: 331 */       if (i > 0) {
/*  140: 332 */         param = param + "|";
/*  141:     */       }
/*  142: 334 */       SelectedTag tag = new SelectedTag(TAGS_PREPROCESSING[i].getID(), TAGS_PREPROCESSING);
/*  143:     */       
/*  144: 336 */       param = param + tag.getSelectedTag().getReadable();
/*  145:     */     }
/*  146: 338 */     result.addElement(new Option("\tThe type of preprocessing that is applied to the data.\n\t(default: center)", "P", 1, "-P <" + param + ">"));
/*  147:     */     
/*  148:     */ 
/*  149:     */ 
/*  150: 342 */     result.addAll(Collections.list(super.listOptions()));
/*  151:     */     
/*  152: 344 */     return result.elements();
/*  153:     */   }
/*  154:     */   
/*  155:     */   public String[] getOptions()
/*  156:     */   {
/*  157: 355 */     Vector<String> result = new Vector();
/*  158:     */     
/*  159: 357 */     result.add("-C");
/*  160: 358 */     result.add("" + getNumComponents());
/*  161: 360 */     if (getPerformPrediction()) {
/*  162: 361 */       result.add("-U");
/*  163:     */     }
/*  164: 364 */     if (getReplaceMissing()) {
/*  165: 365 */       result.add("-M");
/*  166:     */     }
/*  167: 368 */     result.add("-A");
/*  168: 369 */     result.add("" + getAlgorithm().getSelectedTag().getReadable());
/*  169:     */     
/*  170: 371 */     result.add("-P");
/*  171: 372 */     result.add("" + getPreprocessing().getSelectedTag().getReadable());
/*  172:     */     
/*  173: 374 */     Collections.addAll(result, super.getOptions());
/*  174:     */     
/*  175: 376 */     return (String[])result.toArray(new String[result.size()]);
/*  176:     */   }
/*  177:     */   
/*  178:     */   public void setOptions(String[] options)
/*  179:     */     throws Exception
/*  180:     */   {
/*  181: 430 */     String tmpStr = Utils.getOption("C", options);
/*  182: 431 */     if (tmpStr.length() != 0) {
/*  183: 432 */       setNumComponents(Integer.parseInt(tmpStr));
/*  184:     */     } else {
/*  185: 434 */       setNumComponents(20);
/*  186:     */     }
/*  187: 437 */     setPerformPrediction(Utils.getFlag("U", options));
/*  188:     */     
/*  189: 439 */     setReplaceMissing(Utils.getFlag("M", options));
/*  190:     */     
/*  191: 441 */     tmpStr = Utils.getOption("A", options);
/*  192: 442 */     if (tmpStr.length() != 0) {
/*  193: 443 */       setAlgorithm(new SelectedTag(tmpStr, TAGS_ALGORITHM));
/*  194:     */     } else {
/*  195: 445 */       setAlgorithm(new SelectedTag(2, TAGS_ALGORITHM));
/*  196:     */     }
/*  197: 448 */     tmpStr = Utils.getOption("P", options);
/*  198: 449 */     if (tmpStr.length() != 0) {
/*  199: 450 */       setPreprocessing(new SelectedTag(tmpStr, TAGS_PREPROCESSING));
/*  200:     */     } else {
/*  201: 452 */       setPreprocessing(new SelectedTag(1, TAGS_PREPROCESSING));
/*  202:     */     }
/*  203: 455 */     super.setOptions(options);
/*  204:     */     
/*  205: 457 */     Utils.checkForRemainingOptions(options);
/*  206:     */   }
/*  207:     */   
/*  208:     */   public String numComponentsTipText()
/*  209:     */   {
/*  210: 467 */     return "The number of components to compute.";
/*  211:     */   }
/*  212:     */   
/*  213:     */   public void setNumComponents(int value)
/*  214:     */   {
/*  215: 476 */     this.m_NumComponents = value;
/*  216:     */   }
/*  217:     */   
/*  218:     */   public int getNumComponents()
/*  219:     */   {
/*  220: 485 */     return this.m_NumComponents;
/*  221:     */   }
/*  222:     */   
/*  223:     */   public String performPredictionTipText()
/*  224:     */   {
/*  225: 495 */     return "Whether to update the class attribute with the predicted value.";
/*  226:     */   }
/*  227:     */   
/*  228:     */   public void setPerformPrediction(boolean value)
/*  229:     */   {
/*  230: 505 */     this.m_PerformPrediction = value;
/*  231:     */   }
/*  232:     */   
/*  233:     */   public boolean getPerformPrediction()
/*  234:     */   {
/*  235: 514 */     return this.m_PerformPrediction;
/*  236:     */   }
/*  237:     */   
/*  238:     */   public String algorithmTipText()
/*  239:     */   {
/*  240: 524 */     return "Sets the type of algorithm to use.";
/*  241:     */   }
/*  242:     */   
/*  243:     */   public void setAlgorithm(SelectedTag value)
/*  244:     */   {
/*  245: 533 */     if (value.getTags() == TAGS_ALGORITHM) {
/*  246: 534 */       this.m_Algorithm = value.getSelectedTag().getID();
/*  247:     */     }
/*  248:     */   }
/*  249:     */   
/*  250:     */   public SelectedTag getAlgorithm()
/*  251:     */   {
/*  252: 544 */     return new SelectedTag(this.m_Algorithm, TAGS_ALGORITHM);
/*  253:     */   }
/*  254:     */   
/*  255:     */   public String replaceMissingTipText()
/*  256:     */   {
/*  257: 554 */     return "Whether to replace missing values.";
/*  258:     */   }
/*  259:     */   
/*  260:     */   public void setReplaceMissing(boolean value)
/*  261:     */   {
/*  262: 564 */     this.m_ReplaceMissing = value;
/*  263:     */   }
/*  264:     */   
/*  265:     */   public boolean getReplaceMissing()
/*  266:     */   {
/*  267: 574 */     return this.m_ReplaceMissing;
/*  268:     */   }
/*  269:     */   
/*  270:     */   public String preprocessingTipText()
/*  271:     */   {
/*  272: 584 */     return "Sets the type of preprocessing to use.";
/*  273:     */   }
/*  274:     */   
/*  275:     */   public void setPreprocessing(SelectedTag value)
/*  276:     */   {
/*  277: 593 */     if (value.getTags() == TAGS_PREPROCESSING) {
/*  278: 594 */       this.m_Preprocessing = value.getSelectedTag().getID();
/*  279:     */     }
/*  280:     */   }
/*  281:     */   
/*  282:     */   public SelectedTag getPreprocessing()
/*  283:     */   {
/*  284: 604 */     return new SelectedTag(this.m_Preprocessing, TAGS_PREPROCESSING);
/*  285:     */   }
/*  286:     */   
/*  287:     */   protected Instances determineOutputFormat(Instances inputFormat)
/*  288:     */     throws Exception
/*  289:     */   {
/*  290: 624 */     ArrayList<Attribute> atts = new ArrayList();
/*  291: 625 */     String prefix = getAlgorithm().getSelectedTag().getReadable();
/*  292: 626 */     for (int i = 0; i < getNumComponents(); i++) {
/*  293: 627 */       atts.add(new Attribute(prefix + "_" + (i + 1)));
/*  294:     */     }
/*  295: 629 */     atts.add(new Attribute("Class"));
/*  296: 630 */     Instances result = new Instances(prefix, atts, 0);
/*  297: 631 */     result.setClassIndex(result.numAttributes() - 1);
/*  298:     */     
/*  299: 633 */     return result;
/*  300:     */   }
/*  301:     */   
/*  302:     */   protected Matrix getX(Instances instances)
/*  303:     */   {
/*  304: 651 */     int clsIndex = instances.classIndex();
/*  305: 652 */     double[][] x = new double[instances.numInstances()][];
/*  306: 654 */     for (int i = 0; i < instances.numInstances(); i++)
/*  307:     */     {
/*  308: 655 */       double[] values = instances.instance(i).toDoubleArray();
/*  309: 656 */       x[i] = new double[values.length - 1];
/*  310:     */       
/*  311: 658 */       int j = 0;
/*  312: 659 */       for (int n = 0; n < values.length; n++) {
/*  313: 660 */         if (n != clsIndex)
/*  314:     */         {
/*  315: 661 */           x[i][j] = values[n];
/*  316: 662 */           j++;
/*  317:     */         }
/*  318:     */       }
/*  319:     */     }
/*  320: 667 */     Matrix result = new Matrix(x);
/*  321:     */     
/*  322: 669 */     return result;
/*  323:     */   }
/*  324:     */   
/*  325:     */   protected Matrix getX(Instance instance)
/*  326:     */   {
/*  327: 683 */     double[][] x = new double[1][];
/*  328: 684 */     double[] values = instance.toDoubleArray();
/*  329: 685 */     x[0] = new double[values.length - 1];
/*  330: 686 */     System.arraycopy(values, 0, x[0], 0, values.length - 1);
/*  331:     */     
/*  332: 688 */     Matrix result = new Matrix(x);
/*  333:     */     
/*  334: 690 */     return result;
/*  335:     */   }
/*  336:     */   
/*  337:     */   protected Matrix getY(Instances instances)
/*  338:     */   {
/*  339: 704 */     double[][] y = new double[instances.numInstances()][1];
/*  340: 705 */     for (int i = 0; i < instances.numInstances(); i++) {
/*  341: 706 */       y[i][0] = instances.instance(i).classValue();
/*  342:     */     }
/*  343: 709 */     Matrix result = new Matrix(y);
/*  344:     */     
/*  345: 711 */     return result;
/*  346:     */   }
/*  347:     */   
/*  348:     */   protected Matrix getY(Instance instance)
/*  349:     */   {
/*  350: 724 */     double[][] y = new double[1][1];
/*  351: 725 */     y[0][0] = instance.classValue();
/*  352:     */     
/*  353: 727 */     Matrix result = new Matrix(y);
/*  354:     */     
/*  355: 729 */     return result;
/*  356:     */   }
/*  357:     */   
/*  358:     */   protected Instances toInstances(Instances header, Matrix x, Matrix y)
/*  359:     */   {
/*  360: 751 */     Instances result = new Instances(header, 0);
/*  361:     */     
/*  362: 753 */     int rows = x.getRowDimension();
/*  363: 754 */     int cols = x.getColumnDimension();
/*  364: 755 */     int clsIdx = header.classIndex();
/*  365: 757 */     for (int i = 0; i < rows; i++)
/*  366:     */     {
/*  367: 758 */       double[] values = new double[cols + 1];
/*  368: 759 */       int offset = 0;
/*  369: 761 */       for (int n = 0; n < values.length; n++) {
/*  370: 762 */         if (n == clsIdx)
/*  371:     */         {
/*  372: 763 */           offset--;
/*  373: 764 */           values[n] = y.get(i, 0);
/*  374:     */         }
/*  375:     */         else
/*  376:     */         {
/*  377: 766 */           values[n] = x.get(i, n + offset);
/*  378:     */         }
/*  379:     */       }
/*  380: 770 */       result.add(new DenseInstance(1.0D, values));
/*  381:     */     }
/*  382: 773 */     return result;
/*  383:     */   }
/*  384:     */   
/*  385:     */   protected Matrix columnAsVector(Matrix m, int columnIndex)
/*  386:     */   {
/*  387: 787 */     Matrix result = new Matrix(m.getRowDimension(), 1);
/*  388: 789 */     for (int i = 0; i < m.getRowDimension(); i++) {
/*  389: 790 */       result.set(i, 0, m.get(i, columnIndex));
/*  390:     */     }
/*  391: 793 */     return result;
/*  392:     */   }
/*  393:     */   
/*  394:     */   protected void setVector(Matrix v, Matrix m, int columnIndex)
/*  395:     */   {
/*  396: 805 */     m.setMatrix(0, m.getRowDimension() - 1, columnIndex, columnIndex, v);
/*  397:     */   }
/*  398:     */   
/*  399:     */   protected Matrix getVector(Matrix m, int columnIndex)
/*  400:     */   {
/*  401: 816 */     return m.getMatrix(0, m.getRowDimension() - 1, columnIndex, columnIndex);
/*  402:     */   }
/*  403:     */   
/*  404:     */   protected Matrix getDominantEigenVector(Matrix m)
/*  405:     */   {
/*  406: 831 */     EigenvalueDecomposition eigendecomp = m.eig();
/*  407: 832 */     double[] eigenvalues = eigendecomp.getRealEigenvalues();
/*  408: 833 */     int index = Utils.maxIndex(eigenvalues);
/*  409: 834 */     Matrix result = columnAsVector(eigendecomp.getV(), index);
/*  410:     */     
/*  411: 836 */     return result;
/*  412:     */   }
/*  413:     */   
/*  414:     */   protected void normalizeVector(Matrix v)
/*  415:     */   {
/*  416: 849 */     double sum = 0.0D;
/*  417: 850 */     for (int i = 0; i < v.getRowDimension(); i++) {
/*  418: 851 */       sum += v.get(i, 0) * v.get(i, 0);
/*  419:     */     }
/*  420: 853 */     sum = StrictMath.sqrt(sum);
/*  421: 856 */     for (i = 0; i < v.getRowDimension(); i++) {
/*  422: 857 */       v.set(i, 0, v.get(i, 0) / sum);
/*  423:     */     }
/*  424:     */   }
/*  425:     */   
/*  426:     */   protected Instances processPLS1(Instances instances)
/*  427:     */     throws Exception
/*  428:     */   {
/*  429:     */     Instances result;
/*  430:     */     Instances result;
/*  431: 884 */     if (!isFirstBatchDone())
/*  432:     */     {
/*  433: 886 */       Matrix X = getX(instances);
/*  434: 887 */       Matrix y = getY(instances);
/*  435: 888 */       Matrix X_trans = X.transpose();
/*  436:     */       
/*  437:     */ 
/*  438: 891 */       Matrix W = new Matrix(instances.numAttributes() - 1, getNumComponents());
/*  439: 892 */       Matrix P = new Matrix(instances.numAttributes() - 1, getNumComponents());
/*  440: 893 */       Matrix T = new Matrix(instances.numInstances(), getNumComponents());
/*  441: 894 */       Matrix b_hat = new Matrix(getNumComponents(), 1);
/*  442: 896 */       for (int j = 0; j < getNumComponents(); j++)
/*  443:     */       {
/*  444: 898 */         Matrix w = X_trans.times(y);
/*  445: 899 */         normalizeVector(w);
/*  446: 900 */         setVector(w, W, j);
/*  447:     */         
/*  448:     */ 
/*  449: 903 */         Matrix t = X.times(w);
/*  450: 904 */         Matrix t_trans = t.transpose();
/*  451: 905 */         setVector(t, T, j);
/*  452:     */         
/*  453:     */ 
/*  454: 908 */         double b = t_trans.times(y).get(0, 0) / t_trans.times(t).get(0, 0);
/*  455: 909 */         b_hat.set(j, 0, b);
/*  456:     */         
/*  457:     */ 
/*  458: 912 */         Matrix p = X_trans.times(t).times(1.0D / t_trans.times(t).get(0, 0));
/*  459: 913 */         Matrix p_trans = p.transpose();
/*  460: 914 */         setVector(p, P, j);
/*  461:     */         
/*  462:     */ 
/*  463: 917 */         X = X.minus(t.times(p_trans));
/*  464: 918 */         y = y.minus(t.times(b));
/*  465:     */       }
/*  466: 922 */       Matrix tmp = W.times(P.transpose().times(W).inverse());
/*  467:     */       
/*  468:     */ 
/*  469: 925 */       Matrix X_new = getX(instances).times(tmp);
/*  470:     */       
/*  471:     */ 
/*  472: 928 */       this.m_PLS1_RegVector = tmp.times(b_hat);
/*  473:     */       
/*  474:     */ 
/*  475: 931 */       this.m_PLS1_P = P;
/*  476: 932 */       this.m_PLS1_W = W;
/*  477: 933 */       this.m_PLS1_b_hat = b_hat;
/*  478:     */       Instances result;
/*  479: 935 */       if (getPerformPrediction()) {
/*  480: 936 */         result = toInstances(getOutputFormat(), X_new, y);
/*  481:     */       } else {
/*  482: 938 */         result = toInstances(getOutputFormat(), X_new, getY(instances));
/*  483:     */       }
/*  484:     */     }
/*  485:     */     else
/*  486:     */     {
/*  487: 943 */       result = new Instances(getOutputFormat());
/*  488: 945 */       for (int i = 0; i < instances.numInstances(); i++)
/*  489:     */       {
/*  490: 947 */         Instances tmpInst = new Instances(instances, 0);
/*  491: 948 */         tmpInst.add((Instance)instances.instance(i).copy());
/*  492: 949 */         Matrix x = getX(tmpInst);
/*  493: 950 */         Matrix X = new Matrix(1, getNumComponents());
/*  494: 951 */         Matrix T = new Matrix(1, getNumComponents());
/*  495: 953 */         for (int j = 0; j < getNumComponents(); j++)
/*  496:     */         {
/*  497: 954 */           setVector(x, X, j);
/*  498:     */           
/*  499: 956 */           Matrix t = x.times(getVector(this.m_PLS1_W, j));
/*  500: 957 */           setVector(t, T, j);
/*  501:     */           
/*  502: 959 */           x = x.minus(getVector(this.m_PLS1_P, j).transpose().times(t.get(0, 0)));
/*  503:     */         }
/*  504: 962 */         if (getPerformPrediction()) {
/*  505: 963 */           tmpInst = toInstances(getOutputFormat(), T, T.times(this.m_PLS1_b_hat));
/*  506:     */         } else {
/*  507: 965 */           tmpInst = toInstances(getOutputFormat(), T, getY(tmpInst));
/*  508:     */         }
/*  509: 968 */         result.add(tmpInst.instance(0));
/*  510:     */       }
/*  511:     */     }
/*  512: 972 */     return result;
/*  513:     */   }
/*  514:     */   
/*  515:     */   protected Instances processSIMPLS(Instances instances)
/*  516:     */     throws Exception
/*  517:     */   {
/*  518:     */     Instances result;
/*  519:     */     Instances result;
/*  520: 997 */     if (!isFirstBatchDone())
/*  521:     */     {
/*  522: 999 */       Matrix X = getX(instances);
/*  523:1000 */       Matrix X_trans = X.transpose();
/*  524:1001 */       Matrix Y = getY(instances);
/*  525:1002 */       Matrix A = X_trans.times(Y);
/*  526:1003 */       Matrix M = X_trans.times(X);
/*  527:1004 */       Matrix C = Matrix.identity(instances.numAttributes() - 1, instances.numAttributes() - 1);
/*  528:     */       
/*  529:1006 */       Matrix W = new Matrix(instances.numAttributes() - 1, getNumComponents());
/*  530:1007 */       Matrix P = new Matrix(instances.numAttributes() - 1, getNumComponents());
/*  531:1008 */       Matrix Q = new Matrix(1, getNumComponents());
/*  532:1010 */       for (int h = 0; h < getNumComponents(); h++)
/*  533:     */       {
/*  534:1012 */         Matrix A_trans = A.transpose();
/*  535:1013 */         Matrix q = getDominantEigenVector(A_trans.times(A));
/*  536:     */         
/*  537:     */ 
/*  538:1016 */         Matrix w = A.times(q);
/*  539:1017 */         Matrix c = w.transpose().times(M).times(w);
/*  540:1018 */         w = w.times(1.0D / StrictMath.sqrt(c.get(0, 0)));
/*  541:1019 */         setVector(w, W, h);
/*  542:     */         
/*  543:     */ 
/*  544:1022 */         Matrix p = M.times(w);
/*  545:1023 */         Matrix p_trans = p.transpose();
/*  546:1024 */         setVector(p, P, h);
/*  547:     */         
/*  548:     */ 
/*  549:1027 */         q = A_trans.times(w);
/*  550:1028 */         setVector(q, Q, h);
/*  551:     */         
/*  552:     */ 
/*  553:1031 */         Matrix v = C.times(p);
/*  554:1032 */         normalizeVector(v);
/*  555:1033 */         Matrix v_trans = v.transpose();
/*  556:     */         
/*  557:     */ 
/*  558:1036 */         C = C.minus(v.times(v_trans));
/*  559:1037 */         M = M.minus(p.times(p_trans));
/*  560:     */         
/*  561:     */ 
/*  562:1040 */         A = C.times(A);
/*  563:     */       }
/*  564:1044 */       this.m_SIMPLS_W = W;
/*  565:1045 */       Matrix T = X.times(this.m_SIMPLS_W);
/*  566:1046 */       Matrix X_new = T;
/*  567:1047 */       this.m_SIMPLS_B = W.times(Q.transpose());
/*  568:     */       Matrix y;
/*  569:     */       Matrix y;
/*  570:1049 */       if (getPerformPrediction()) {
/*  571:1050 */         y = T.times(P.transpose()).times(this.m_SIMPLS_B);
/*  572:     */       } else {
/*  573:1052 */         y = getY(instances);
/*  574:     */       }
/*  575:1055 */       result = toInstances(getOutputFormat(), X_new, y);
/*  576:     */     }
/*  577:     */     else
/*  578:     */     {
/*  579:1057 */       result = new Instances(getOutputFormat());
/*  580:     */       
/*  581:1059 */       Matrix X = getX(instances);
/*  582:1060 */       Matrix X_new = X.times(this.m_SIMPLS_W);
/*  583:     */       Matrix y;
/*  584:     */       Matrix y;
/*  585:1062 */       if (getPerformPrediction()) {
/*  586:1063 */         y = X.times(this.m_SIMPLS_B);
/*  587:     */       } else {
/*  588:1065 */         y = getY(instances);
/*  589:     */       }
/*  590:1068 */       result = toInstances(getOutputFormat(), X_new, y);
/*  591:     */     }
/*  592:1071 */     return result;
/*  593:     */   }
/*  594:     */   
/*  595:     */   public Capabilities getCapabilities()
/*  596:     */   {
/*  597:1082 */     Capabilities result = super.getCapabilities();
/*  598:1083 */     result.disableAll();
/*  599:     */     
/*  600:     */ 
/*  601:1086 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  602:1087 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  603:1088 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  604:     */     
/*  605:     */ 
/*  606:1091 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  607:1092 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  608:     */     
/*  609:1094 */     return result;
/*  610:     */   }
/*  611:     */   
/*  612:     */   protected Instances process(Instances instances)
/*  613:     */     throws Exception
/*  614:     */   {
/*  615:1113 */     Instances result = null;
/*  616:     */     double[] clsValues;
/*  617:     */     double[] clsValues;
/*  618:1116 */     if (!getPerformPrediction()) {
/*  619:1117 */       clsValues = instances.attributeToDoubleArray(instances.classIndex());
/*  620:     */     } else {
/*  621:1119 */       clsValues = null;
/*  622:     */     }
/*  623:1122 */     if (!isFirstBatchDone())
/*  624:     */     {
/*  625:1124 */       if (this.m_ReplaceMissing) {
/*  626:1125 */         this.m_Missing.setInputFormat(instances);
/*  627:     */       }
/*  628:1128 */       switch (this.m_Preprocessing)
/*  629:     */       {
/*  630:     */       case 1: 
/*  631:1130 */         this.m_ClassMean = instances.meanOrMode(instances.classIndex());
/*  632:1131 */         this.m_ClassStdDev = 1.0D;
/*  633:1132 */         this.m_Filter = new Center();
/*  634:1133 */         ((Center)this.m_Filter).setIgnoreClass(true);
/*  635:1134 */         break;
/*  636:     */       case 2: 
/*  637:1136 */         this.m_ClassMean = instances.meanOrMode(instances.classIndex());
/*  638:1137 */         this.m_ClassStdDev = StrictMath.sqrt(instances.variance(instances.classIndex()));
/*  639:     */         
/*  640:1139 */         this.m_Filter = new Standardize();
/*  641:1140 */         ((Standardize)this.m_Filter).setIgnoreClass(true);
/*  642:1141 */         break;
/*  643:     */       default: 
/*  644:1143 */         this.m_ClassMean = 0.0D;
/*  645:1144 */         this.m_ClassStdDev = 1.0D;
/*  646:1145 */         this.m_Filter = null;
/*  647:     */       }
/*  648:1147 */       if (this.m_Filter != null) {
/*  649:1148 */         this.m_Filter.setInputFormat(instances);
/*  650:     */       }
/*  651:     */     }
/*  652:1153 */     if (this.m_ReplaceMissing) {
/*  653:1154 */       instances = Filter.useFilter(instances, this.m_Missing);
/*  654:     */     }
/*  655:1156 */     if (this.m_Filter != null) {
/*  656:1157 */       instances = Filter.useFilter(instances, this.m_Filter);
/*  657:     */     }
/*  658:1160 */     switch (this.m_Algorithm)
/*  659:     */     {
/*  660:     */     case 1: 
/*  661:1162 */       result = processSIMPLS(instances);
/*  662:1163 */       break;
/*  663:     */     case 2: 
/*  664:1165 */       result = processPLS1(instances);
/*  665:1166 */       break;
/*  666:     */     default: 
/*  667:1168 */       throw new IllegalStateException("Algorithm type '" + this.m_Algorithm + "' is not recognized!");
/*  668:     */     }
/*  669:1174 */     for (int i = 0; i < result.numInstances(); i++) {
/*  670:1175 */       if (!getPerformPrediction())
/*  671:     */       {
/*  672:1176 */         result.instance(i).setClassValue(clsValues[i]);
/*  673:     */       }
/*  674:     */       else
/*  675:     */       {
/*  676:1178 */         double clsValue = result.instance(i).classValue();
/*  677:1179 */         result.instance(i).setClassValue(clsValue * this.m_ClassStdDev + this.m_ClassMean);
/*  678:     */       }
/*  679:     */     }
/*  680:1184 */     return result;
/*  681:     */   }
/*  682:     */   
/*  683:     */   public String getRevision()
/*  684:     */   {
/*  685:1194 */     return RevisionUtils.extract("$Revision: 10364 $");
/*  686:     */   }
/*  687:     */   
/*  688:     */   public static void main(String[] args)
/*  689:     */   {
/*  690:1203 */     runFilter(new PLSFilter(), args);
/*  691:     */   }
/*  692:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.filters.supervised.attribute.PLSFilter
 * JD-Core Version:    0.7.0.1
 */