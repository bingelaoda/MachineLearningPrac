/*    1:     */ package weka.attributeSelection;
/*    2:     */ 
/*    3:     */ import java.util.ArrayList;
/*    4:     */ import java.util.Enumeration;
/*    5:     */ import java.util.Vector;
/*    6:     */ import no.uib.cipr.matrix.Matrices;
/*    7:     */ import no.uib.cipr.matrix.SymmDenseEVD;
/*    8:     */ import no.uib.cipr.matrix.UpperSymmDenseMatrix;
/*    9:     */ import weka.core.Attribute;
/*   10:     */ import weka.core.Capabilities;
/*   11:     */ import weka.core.Capabilities.Capability;
/*   12:     */ import weka.core.DenseInstance;
/*   13:     */ import weka.core.Instance;
/*   14:     */ import weka.core.Instances;
/*   15:     */ import weka.core.Option;
/*   16:     */ import weka.core.OptionHandler;
/*   17:     */ import weka.core.RevisionUtils;
/*   18:     */ import weka.core.SparseInstance;
/*   19:     */ import weka.core.Utils;
/*   20:     */ import weka.filters.Filter;
/*   21:     */ import weka.filters.unsupervised.attribute.Center;
/*   22:     */ import weka.filters.unsupervised.attribute.NominalToBinary;
/*   23:     */ import weka.filters.unsupervised.attribute.Remove;
/*   24:     */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*   25:     */ import weka.filters.unsupervised.attribute.Standardize;
/*   26:     */ 
/*   27:     */ public class PrincipalComponents
/*   28:     */   extends UnsupervisedAttributeEvaluator
/*   29:     */   implements AttributeTransformer, OptionHandler
/*   30:     */ {
/*   31:     */   private static final long serialVersionUID = -3675307197777734007L;
/*   32:     */   private Instances m_trainInstances;
/*   33:     */   private Instances m_trainHeader;
/*   34:     */   private Instances m_transformedFormat;
/*   35:     */   private Instances m_originalSpaceFormat;
/*   36:     */   private boolean m_hasClass;
/*   37:     */   private int m_classIndex;
/*   38:     */   private int m_numAttribs;
/*   39:     */   private int m_numInstances;
/*   40:     */   private UpperSymmDenseMatrix m_correlation;
/*   41:     */   private double[] m_means;
/*   42:     */   private double[] m_stdDevs;
/*   43: 121 */   private boolean m_center = false;
/*   44:     */   private double[][] m_eigenvectors;
/*   45: 130 */   private double[] m_eigenvalues = null;
/*   46:     */   private int[] m_sortedEigens;
/*   47: 136 */   private double m_sumOfEigenValues = 0.0D;
/*   48:     */   private ReplaceMissingValues m_replaceMissingFilter;
/*   49:     */   private NominalToBinary m_nominalToBinFilter;
/*   50:     */   private Remove m_attributeFilter;
/*   51:     */   private Center m_centerFilter;
/*   52:     */   private Standardize m_standardizeFilter;
/*   53: 146 */   private int m_outputNumAtts = -1;
/*   54: 152 */   private double m_coverVariance = 0.95D;
/*   55: 157 */   private boolean m_transBackToOriginal = false;
/*   56: 160 */   private int m_maxAttrsInName = 5;
/*   57:     */   private double[][] m_eTranspose;
/*   58:     */   
/*   59:     */   public String globalInfo()
/*   60:     */   {
/*   61: 174 */     return "Performs a principal components analysis and transformation of the data. Use in conjunction with a Ranker search. Dimensionality reduction is accomplished by choosing enough eigenvectors to account for some percentage of the variance in the original data---default 0.95 (95%). Attribute noise can be filtered by transforming to the PC space, eliminating some of the worst eigenvectors, and then transforming back to the original space.";
/*   62:     */   }
/*   63:     */   
/*   64:     */   public Enumeration<Option> listOptions()
/*   65:     */   {
/*   66: 191 */     Vector<Option> newVector = new Vector(4);
/*   67:     */     
/*   68: 193 */     newVector.addElement(new Option("\tCenter (rather than standardize) the\n\tdata and compute PCA using the covariance (rather\n\t than the correlation) matrix.", "C", 0, "-C"));
/*   69:     */     
/*   70:     */ 
/*   71:     */ 
/*   72: 197 */     newVector.addElement(new Option("\tRetain enough PC attributes to account \n\tfor this proportion of variance in the original data.\n\t(default = 0.95)", "R", 1, "-R"));
/*   73:     */     
/*   74:     */ 
/*   75:     */ 
/*   76: 201 */     newVector.addElement(new Option("\tTransform through the PC space and \n\tback to the original space.", "O", 0, "-O"));
/*   77:     */     
/*   78:     */ 
/*   79: 204 */     newVector.addElement(new Option("\tMaximum number of attributes to include in \n\ttransformed attribute names. (-1 = include all)", "A", 1, "-A"));
/*   80:     */     
/*   81:     */ 
/*   82: 207 */     return newVector.elements();
/*   83:     */   }
/*   84:     */   
/*   85:     */   public void setOptions(String[] options)
/*   86:     */     throws Exception
/*   87:     */   {
/*   88: 250 */     resetOptions();
/*   89:     */     
/*   90:     */ 
/*   91: 253 */     String optionString = Utils.getOption('R', options);
/*   92: 254 */     if (optionString.length() != 0)
/*   93:     */     {
/*   94: 256 */       Double temp = Double.valueOf(optionString);
/*   95: 257 */       setVarianceCovered(temp.doubleValue());
/*   96:     */     }
/*   97: 259 */     optionString = Utils.getOption('A', options);
/*   98: 260 */     if (optionString.length() != 0) {
/*   99: 261 */       setMaximumAttributeNames(Integer.parseInt(optionString));
/*  100:     */     }
/*  101: 264 */     setTransformBackToOriginal(Utils.getFlag('O', options));
/*  102: 265 */     setCenterData(Utils.getFlag('C', options));
/*  103:     */   }
/*  104:     */   
/*  105:     */   private void resetOptions()
/*  106:     */   {
/*  107: 272 */     this.m_coverVariance = 0.95D;
/*  108: 273 */     this.m_sumOfEigenValues = 0.0D;
/*  109: 274 */     this.m_transBackToOriginal = false;
/*  110:     */   }
/*  111:     */   
/*  112:     */   public String centerDataTipText()
/*  113:     */   {
/*  114: 284 */     return "Center (rather than standardize) the data. PCA will be computed from the covariance (rather than correlation) matrix";
/*  115:     */   }
/*  116:     */   
/*  117:     */   public void setCenterData(boolean center)
/*  118:     */   {
/*  119: 295 */     this.m_center = center;
/*  120:     */   }
/*  121:     */   
/*  122:     */   public boolean getCenterData()
/*  123:     */   {
/*  124: 305 */     return this.m_center;
/*  125:     */   }
/*  126:     */   
/*  127:     */   public String varianceCoveredTipText()
/*  128:     */   {
/*  129: 315 */     return "Retain enough PC attributes to account for this proportion of variance.";
/*  130:     */   }
/*  131:     */   
/*  132:     */   public void setVarianceCovered(double vc)
/*  133:     */   {
/*  134: 326 */     this.m_coverVariance = vc;
/*  135:     */   }
/*  136:     */   
/*  137:     */   public double getVarianceCovered()
/*  138:     */   {
/*  139: 336 */     return this.m_coverVariance;
/*  140:     */   }
/*  141:     */   
/*  142:     */   public String maximumAttributeNamesTipText()
/*  143:     */   {
/*  144: 346 */     return "The maximum number of attributes to include in transformed attribute names.";
/*  145:     */   }
/*  146:     */   
/*  147:     */   public void setMaximumAttributeNames(int m)
/*  148:     */   {
/*  149: 356 */     this.m_maxAttrsInName = m;
/*  150:     */   }
/*  151:     */   
/*  152:     */   public int getMaximumAttributeNames()
/*  153:     */   {
/*  154: 366 */     return this.m_maxAttrsInName;
/*  155:     */   }
/*  156:     */   
/*  157:     */   public String transformBackToOriginalTipText()
/*  158:     */   {
/*  159: 376 */     return "Transform through the PC space and back to the original space. If only the best n PCs are retained (by setting varianceCovered < 1) then this option will give a dataset in the original space but with less attribute noise.";
/*  160:     */   }
/*  161:     */   
/*  162:     */   public void setTransformBackToOriginal(boolean b)
/*  163:     */   {
/*  164: 388 */     this.m_transBackToOriginal = b;
/*  165:     */   }
/*  166:     */   
/*  167:     */   public boolean getTransformBackToOriginal()
/*  168:     */   {
/*  169: 397 */     return this.m_transBackToOriginal;
/*  170:     */   }
/*  171:     */   
/*  172:     */   public String[] getOptions()
/*  173:     */   {
/*  174: 408 */     Vector<String> options = new Vector();
/*  175: 410 */     if (getCenterData()) {
/*  176: 411 */       options.add("-C");
/*  177:     */     }
/*  178: 414 */     options.add("-R");
/*  179: 415 */     options.add("" + getVarianceCovered());
/*  180:     */     
/*  181: 417 */     options.add("-A");
/*  182: 418 */     options.add("" + getMaximumAttributeNames());
/*  183: 420 */     if (getTransformBackToOriginal()) {
/*  184: 421 */       options.add("-O");
/*  185:     */     }
/*  186: 424 */     return (String[])options.toArray(new String[0]);
/*  187:     */   }
/*  188:     */   
/*  189:     */   public Capabilities getCapabilities()
/*  190:     */   {
/*  191: 435 */     Capabilities result = super.getCapabilities();
/*  192: 436 */     result.disableAll();
/*  193:     */     
/*  194:     */ 
/*  195: 439 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*  196: 440 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*  197: 441 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*  198: 442 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*  199:     */     
/*  200:     */ 
/*  201: 445 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*  202: 446 */     result.enable(Capabilities.Capability.UNARY_CLASS);
/*  203: 447 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*  204: 448 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*  205: 449 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*  206: 450 */     result.enable(Capabilities.Capability.NO_CLASS);
/*  207:     */     
/*  208: 452 */     return result;
/*  209:     */   }
/*  210:     */   
/*  211:     */   public void buildEvaluator(Instances data)
/*  212:     */     throws Exception
/*  213:     */   {
/*  214: 464 */     getCapabilities().testWithFail(data);
/*  215:     */     
/*  216: 466 */     buildAttributeConstructor(data);
/*  217:     */   }
/*  218:     */   
/*  219:     */   private void buildAttributeConstructor(Instances data)
/*  220:     */     throws Exception
/*  221:     */   {
/*  222: 470 */     this.m_eigenvalues = null;
/*  223: 471 */     this.m_outputNumAtts = -1;
/*  224: 472 */     this.m_attributeFilter = null;
/*  225: 473 */     this.m_nominalToBinFilter = null;
/*  226: 474 */     this.m_sumOfEigenValues = 0.0D;
/*  227: 475 */     this.m_trainInstances = new Instances(data);
/*  228:     */     
/*  229:     */ 
/*  230:     */ 
/*  231: 479 */     this.m_trainHeader = new Instances(this.m_trainInstances, 0);
/*  232:     */     
/*  233: 481 */     this.m_replaceMissingFilter = new ReplaceMissingValues();
/*  234: 482 */     this.m_replaceMissingFilter.setInputFormat(this.m_trainInstances);
/*  235: 483 */     this.m_trainInstances = Filter.useFilter(this.m_trainInstances, this.m_replaceMissingFilter);
/*  236:     */     
/*  237:     */ 
/*  238:     */ 
/*  239:     */ 
/*  240:     */ 
/*  241:     */ 
/*  242:     */ 
/*  243:     */ 
/*  244: 492 */     this.m_nominalToBinFilter = new NominalToBinary();
/*  245: 493 */     this.m_nominalToBinFilter.setInputFormat(this.m_trainInstances);
/*  246: 494 */     this.m_trainInstances = Filter.useFilter(this.m_trainInstances, this.m_nominalToBinFilter);
/*  247:     */     
/*  248:     */ 
/*  249: 497 */     Vector<Integer> deleteCols = new Vector();
/*  250: 498 */     for (int i = 0; i < this.m_trainInstances.numAttributes(); i++) {
/*  251: 499 */       if (this.m_trainInstances.numDistinctValues(i) <= 1) {
/*  252: 500 */         deleteCols.addElement(new Integer(i));
/*  253:     */       }
/*  254:     */     }
/*  255: 504 */     if (this.m_trainInstances.classIndex() >= 0)
/*  256:     */     {
/*  257: 506 */       this.m_hasClass = true;
/*  258: 507 */       this.m_classIndex = this.m_trainInstances.classIndex();
/*  259: 508 */       deleteCols.addElement(new Integer(this.m_classIndex));
/*  260:     */     }
/*  261: 512 */     if (deleteCols.size() > 0)
/*  262:     */     {
/*  263: 513 */       this.m_attributeFilter = new Remove();
/*  264: 514 */       int[] todelete = new int[deleteCols.size()];
/*  265: 515 */       for (int i = 0; i < deleteCols.size(); i++) {
/*  266: 516 */         todelete[i] = ((Integer)deleteCols.elementAt(i)).intValue();
/*  267:     */       }
/*  268: 518 */       this.m_attributeFilter.setAttributeIndicesArray(todelete);
/*  269: 519 */       this.m_attributeFilter.setInvertSelection(false);
/*  270: 520 */       this.m_attributeFilter.setInputFormat(this.m_trainInstances);
/*  271: 521 */       this.m_trainInstances = Filter.useFilter(this.m_trainInstances, this.m_attributeFilter);
/*  272:     */     }
/*  273: 525 */     getCapabilities().testWithFail(this.m_trainInstances);
/*  274:     */     
/*  275: 527 */     this.m_numInstances = this.m_trainInstances.numInstances();
/*  276: 528 */     this.m_numAttribs = this.m_trainInstances.numAttributes();
/*  277:     */     
/*  278: 530 */     fillCovariance();
/*  279:     */     
/*  280: 532 */     SymmDenseEVD evd = SymmDenseEVD.factorize(this.m_correlation);
/*  281:     */     
/*  282: 534 */     this.m_eigenvectors = Matrices.getArray(evd.getEigenvectors());
/*  283: 535 */     this.m_eigenvalues = evd.getEigenvalues();
/*  284: 544 */     for (int i = 0; i < this.m_eigenvalues.length; i++) {
/*  285: 545 */       if (this.m_eigenvalues[i] < 0.0D) {
/*  286: 546 */         this.m_eigenvalues[i] = 0.0D;
/*  287:     */       }
/*  288:     */     }
/*  289: 549 */     this.m_sortedEigens = Utils.sort(this.m_eigenvalues);
/*  290: 550 */     this.m_sumOfEigenValues = Utils.sum(this.m_eigenvalues);
/*  291:     */     
/*  292: 552 */     this.m_transformedFormat = setOutputFormat();
/*  293: 553 */     if (this.m_transBackToOriginal)
/*  294:     */     {
/*  295: 554 */       this.m_originalSpaceFormat = setOutputFormatOriginal();
/*  296:     */       
/*  297:     */ 
/*  298: 557 */       int numVectors = this.m_transformedFormat.classIndex() < 0 ? this.m_transformedFormat.numAttributes() : this.m_transformedFormat.numAttributes() - 1;
/*  299:     */       
/*  300:     */ 
/*  301:     */ 
/*  302: 561 */       double[][] orderedVectors = new double[this.m_eigenvectors.length][numVectors + 1];
/*  303: 565 */       for (int i = this.m_numAttribs - 1; i > this.m_numAttribs - numVectors - 1; i--) {
/*  304: 566 */         for (int j = 0; j < this.m_numAttribs; j++) {
/*  305: 567 */           orderedVectors[j][(this.m_numAttribs - i)] = this.m_eigenvectors[j][this.m_sortedEigens[i]];
/*  306:     */         }
/*  307:     */       }
/*  308: 573 */       int nr = orderedVectors.length;
/*  309: 574 */       int nc = orderedVectors[0].length;
/*  310: 575 */       this.m_eTranspose = new double[nc][nr];
/*  311: 576 */       for (int i = 0; i < nc; i++) {
/*  312: 577 */         for (int j = 0; j < nr; j++) {
/*  313: 578 */           this.m_eTranspose[i][j] = orderedVectors[j][i];
/*  314:     */         }
/*  315:     */       }
/*  316:     */     }
/*  317:     */   }
/*  318:     */   
/*  319:     */   public Instances transformedHeader()
/*  320:     */     throws Exception
/*  321:     */   {
/*  322: 596 */     if (this.m_eigenvalues == null) {
/*  323: 597 */       throw new Exception("Principal components hasn't been built yet");
/*  324:     */     }
/*  325: 599 */     if (this.m_transBackToOriginal) {
/*  326: 600 */       return this.m_originalSpaceFormat;
/*  327:     */     }
/*  328: 602 */     return this.m_transformedFormat;
/*  329:     */   }
/*  330:     */   
/*  331:     */   public Instances getFilteredInputFormat()
/*  332:     */   {
/*  333: 613 */     return new Instances(this.m_trainInstances, 0);
/*  334:     */   }
/*  335:     */   
/*  336:     */   public double[][] getCorrelationMatrix()
/*  337:     */   {
/*  338: 622 */     return Matrices.getArray(this.m_correlation);
/*  339:     */   }
/*  340:     */   
/*  341:     */   public double[][] getUnsortedEigenVectors()
/*  342:     */   {
/*  343: 631 */     return this.m_eigenvectors;
/*  344:     */   }
/*  345:     */   
/*  346:     */   public double[] getEigenValues()
/*  347:     */   {
/*  348: 640 */     return this.m_eigenvalues;
/*  349:     */   }
/*  350:     */   
/*  351:     */   public Instances transformedData(Instances data)
/*  352:     */     throws Exception
/*  353:     */   {
/*  354: 651 */     if (this.m_eigenvalues == null) {
/*  355: 652 */       throw new Exception("Principal components hasn't been built yet");
/*  356:     */     }
/*  357: 655 */     Instances output = null;
/*  358: 657 */     if (this.m_transBackToOriginal) {
/*  359: 658 */       output = new Instances(this.m_originalSpaceFormat);
/*  360:     */     } else {
/*  361: 660 */       output = new Instances(this.m_transformedFormat);
/*  362:     */     }
/*  363: 662 */     for (int i = 0; i < data.numInstances(); i++)
/*  364:     */     {
/*  365: 663 */       Instance converted = convertInstance(data.instance(i));
/*  366: 664 */       output.add(converted);
/*  367:     */     }
/*  368: 667 */     return output;
/*  369:     */   }
/*  370:     */   
/*  371:     */   public double evaluateAttribute(int att)
/*  372:     */     throws Exception
/*  373:     */   {
/*  374: 681 */     if (this.m_eigenvalues == null) {
/*  375: 682 */       throw new Exception("Principal components hasn't been built yet!");
/*  376:     */     }
/*  377: 685 */     if (this.m_transBackToOriginal) {
/*  378: 686 */       return 1.0D;
/*  379:     */     }
/*  380: 690 */     double cumulative = 0.0D;
/*  381: 691 */     for (int i = this.m_numAttribs - 1; i >= this.m_numAttribs - att - 1; i--) {
/*  382: 692 */       cumulative += this.m_eigenvalues[this.m_sortedEigens[i]];
/*  383:     */     }
/*  384: 695 */     return 1.0D - cumulative / this.m_sumOfEigenValues;
/*  385:     */   }
/*  386:     */   
/*  387:     */   private void fillCovariance()
/*  388:     */     throws Exception
/*  389:     */   {
/*  390: 700 */     this.m_means = new double[this.m_trainInstances.numAttributes()];
/*  391: 701 */     this.m_stdDevs = new double[this.m_trainInstances.numAttributes()];
/*  392: 702 */     for (int i = 0; i < this.m_trainInstances.numAttributes(); i++)
/*  393:     */     {
/*  394: 703 */       this.m_means[i] = this.m_trainInstances.meanOrMode(i);
/*  395: 704 */       this.m_stdDevs[i] = Math.sqrt(Utils.variance(this.m_trainInstances.attributeToDoubleArray(i)));
/*  396:     */     }
/*  397: 709 */     if (this.m_center)
/*  398:     */     {
/*  399: 710 */       this.m_centerFilter = new Center();
/*  400: 711 */       this.m_centerFilter.setInputFormat(this.m_trainInstances);
/*  401: 712 */       this.m_trainInstances = Filter.useFilter(this.m_trainInstances, this.m_centerFilter);
/*  402:     */     }
/*  403:     */     else
/*  404:     */     {
/*  405: 714 */       this.m_standardizeFilter = new Standardize();
/*  406: 715 */       this.m_standardizeFilter.setInputFormat(this.m_trainInstances);
/*  407: 716 */       this.m_trainInstances = Filter.useFilter(this.m_trainInstances, this.m_standardizeFilter);
/*  408:     */     }
/*  409: 720 */     this.m_correlation = new UpperSymmDenseMatrix(this.m_numAttribs);
/*  410: 721 */     for (int i = 0; i < this.m_numAttribs; i++) {
/*  411: 722 */       for (int j = i; j < this.m_numAttribs; j++)
/*  412:     */       {
/*  413: 724 */         double cov = 0.0D;
/*  414: 725 */         for (Instance inst : this.m_trainInstances) {
/*  415: 726 */           cov += inst.value(i) * inst.value(j);
/*  416:     */         }
/*  417: 729 */         cov /= (this.m_trainInstances.numInstances() - 1);
/*  418: 730 */         this.m_correlation.set(i, j, cov);
/*  419:     */       }
/*  420:     */     }
/*  421:     */   }
/*  422:     */   
/*  423:     */   private String principalComponentsSummary()
/*  424:     */   {
/*  425: 741 */     StringBuffer result = new StringBuffer();
/*  426: 742 */     double cumulative = 0.0D;
/*  427: 743 */     Instances output = null;
/*  428: 744 */     int numVectors = 0;
/*  429:     */     try
/*  430:     */     {
/*  431: 747 */       output = setOutputFormat();
/*  432: 748 */       numVectors = output.classIndex() < 0 ? output.numAttributes() : output.numAttributes() - 1;
/*  433:     */     }
/*  434:     */     catch (Exception ex) {}
/*  435: 754 */     String corrCov = this.m_center ? "Covariance " : "Correlation ";
/*  436: 755 */     result.append(corrCov + "matrix\n" + matrixToString(Matrices.getArray(this.m_correlation)) + "\n\n");
/*  437:     */     
/*  438: 757 */     result.append("eigenvalue\tproportion\tcumulative\n");
/*  439: 758 */     for (int i = this.m_numAttribs - 1; i > this.m_numAttribs - numVectors - 1; i--)
/*  440:     */     {
/*  441: 759 */       cumulative += this.m_eigenvalues[this.m_sortedEigens[i]];
/*  442: 760 */       result.append(Utils.doubleToString(this.m_eigenvalues[this.m_sortedEigens[i]], 9, 5) + "\t" + Utils.doubleToString(this.m_eigenvalues[this.m_sortedEigens[i]] / this.m_sumOfEigenValues, 9, 5) + "\t" + Utils.doubleToString(cumulative / this.m_sumOfEigenValues, 9, 5) + "\t" + output.attribute(this.m_numAttribs - i - 1).name() + "\n");
/*  443:     */     }
/*  444: 771 */     result.append("\nEigenvectors\n");
/*  445: 772 */     for (int j = 1; j <= numVectors; j++) {
/*  446: 773 */       result.append(" V" + j + '\t');
/*  447:     */     }
/*  448: 775 */     result.append("\n");
/*  449: 776 */     for (int j = 0; j < this.m_numAttribs; j++)
/*  450:     */     {
/*  451: 778 */       for (int i = this.m_numAttribs - 1; i > this.m_numAttribs - numVectors - 1; i--) {
/*  452: 779 */         result.append(Utils.doubleToString(this.m_eigenvectors[j][this.m_sortedEigens[i]], 7, 4) + "\t");
/*  453:     */       }
/*  454: 782 */       result.append(this.m_trainInstances.attribute(j).name() + '\n');
/*  455:     */     }
/*  456: 785 */     if (this.m_transBackToOriginal) {
/*  457: 786 */       result.append("\nPC space transformed back to original space.\n(Note: can't evaluate attributes in the original space)\n");
/*  458:     */     }
/*  459: 789 */     return result.toString();
/*  460:     */   }
/*  461:     */   
/*  462:     */   public String toString()
/*  463:     */   {
/*  464: 799 */     if (this.m_eigenvalues == null) {
/*  465: 800 */       return "Principal components hasn't been built yet!";
/*  466:     */     }
/*  467: 802 */     return "\tPrincipal Components Attribute Transformer\n\n" + principalComponentsSummary();
/*  468:     */   }
/*  469:     */   
/*  470:     */   public static String matrixToString(double[][] matrix)
/*  471:     */   {
/*  472: 814 */     StringBuffer result = new StringBuffer();
/*  473: 815 */     int last = matrix.length - 1;
/*  474: 817 */     for (int i = 0; i <= last; i++) {
/*  475: 818 */       for (int j = 0; j <= last; j++)
/*  476:     */       {
/*  477: 819 */         result.append(Utils.doubleToString(matrix[i][j], 6, 2) + " ");
/*  478: 820 */         if (j == last) {
/*  479: 821 */           result.append('\n');
/*  480:     */         }
/*  481:     */       }
/*  482:     */     }
/*  483: 825 */     return result.toString();
/*  484:     */   }
/*  485:     */   
/*  486:     */   private Instance convertInstanceToOriginal(Instance inst)
/*  487:     */     throws Exception
/*  488:     */   {
/*  489: 836 */     double[] newVals = null;
/*  490: 838 */     if (this.m_hasClass) {
/*  491: 839 */       newVals = new double[this.m_numAttribs + 1];
/*  492:     */     } else {
/*  493: 841 */       newVals = new double[this.m_numAttribs];
/*  494:     */     }
/*  495: 844 */     if (this.m_hasClass) {
/*  496: 846 */       newVals[this.m_numAttribs] = inst.value(inst.numAttributes() - 1);
/*  497:     */     }
/*  498: 849 */     for (int i = 0; i < this.m_eTranspose[0].length; i++)
/*  499:     */     {
/*  500: 850 */       double tempval = 0.0D;
/*  501: 851 */       for (int j = 1; j < this.m_eTranspose.length; j++) {
/*  502: 852 */         tempval += this.m_eTranspose[j][i] * inst.value(j - 1);
/*  503:     */       }
/*  504: 854 */       newVals[i] = tempval;
/*  505: 855 */       if (!this.m_center) {
/*  506: 856 */         newVals[i] *= this.m_stdDevs[i];
/*  507:     */       }
/*  508: 858 */       newVals[i] += this.m_means[i];
/*  509:     */     }
/*  510: 861 */     if ((inst instanceof SparseInstance)) {
/*  511: 862 */       return new SparseInstance(inst.weight(), newVals);
/*  512:     */     }
/*  513: 864 */     return new DenseInstance(inst.weight(), newVals);
/*  514:     */   }
/*  515:     */   
/*  516:     */   public Instance convertInstance(Instance instance)
/*  517:     */     throws Exception
/*  518:     */   {
/*  519: 879 */     if (this.m_eigenvalues == null) {
/*  520: 880 */       throw new Exception("convertInstance: Principal components not built yet");
/*  521:     */     }
/*  522: 884 */     double[] newVals = new double[this.m_outputNumAtts];
/*  523: 885 */     Instance tempInst = (Instance)instance.copy();
/*  524: 886 */     if (!instance.dataset().equalHeaders(this.m_trainHeader)) {
/*  525: 887 */       throw new Exception("Can't convert instance: header's don't match: PrincipalComponents\n" + instance.dataset().equalHeadersMsg(this.m_trainHeader));
/*  526:     */     }
/*  527: 892 */     this.m_replaceMissingFilter.input(tempInst);
/*  528: 893 */     this.m_replaceMissingFilter.batchFinished();
/*  529: 894 */     tempInst = this.m_replaceMissingFilter.output();
/*  530:     */     
/*  531:     */ 
/*  532:     */ 
/*  533:     */ 
/*  534:     */ 
/*  535:     */ 
/*  536:     */ 
/*  537: 902 */     this.m_nominalToBinFilter.input(tempInst);
/*  538: 903 */     this.m_nominalToBinFilter.batchFinished();
/*  539: 904 */     tempInst = this.m_nominalToBinFilter.output();
/*  540: 906 */     if (this.m_attributeFilter != null)
/*  541:     */     {
/*  542: 907 */       this.m_attributeFilter.input(tempInst);
/*  543: 908 */       this.m_attributeFilter.batchFinished();
/*  544: 909 */       tempInst = this.m_attributeFilter.output();
/*  545:     */     }
/*  546: 912 */     if (!this.m_center)
/*  547:     */     {
/*  548: 913 */       this.m_standardizeFilter.input(tempInst);
/*  549: 914 */       this.m_standardizeFilter.batchFinished();
/*  550: 915 */       tempInst = this.m_standardizeFilter.output();
/*  551:     */     }
/*  552:     */     else
/*  553:     */     {
/*  554: 917 */       this.m_centerFilter.input(tempInst);
/*  555: 918 */       this.m_centerFilter.batchFinished();
/*  556: 919 */       tempInst = this.m_centerFilter.output();
/*  557:     */     }
/*  558: 922 */     if (this.m_hasClass) {
/*  559: 923 */       newVals[(this.m_outputNumAtts - 1)] = instance.value(instance.classIndex());
/*  560:     */     }
/*  561: 926 */     double cumulative = 0.0D;
/*  562: 927 */     for (int i = this.m_numAttribs - 1; i >= 0; i--)
/*  563:     */     {
/*  564: 928 */       double tempval = 0.0D;
/*  565: 929 */       for (int j = 0; j < this.m_numAttribs; j++) {
/*  566: 930 */         tempval += this.m_eigenvectors[j][this.m_sortedEigens[i]] * tempInst.value(j);
/*  567:     */       }
/*  568: 932 */       newVals[(this.m_numAttribs - i - 1)] = tempval;
/*  569: 933 */       cumulative += this.m_eigenvalues[this.m_sortedEigens[i]];
/*  570: 934 */       if (cumulative / this.m_sumOfEigenValues >= this.m_coverVariance) {
/*  571:     */         break;
/*  572:     */       }
/*  573:     */     }
/*  574: 939 */     if (!this.m_transBackToOriginal)
/*  575:     */     {
/*  576: 940 */       if ((instance instanceof SparseInstance)) {
/*  577: 941 */         return new SparseInstance(instance.weight(), newVals);
/*  578:     */       }
/*  579: 943 */       return new DenseInstance(instance.weight(), newVals);
/*  580:     */     }
/*  581: 946 */     if ((instance instanceof SparseInstance)) {
/*  582: 947 */       return convertInstanceToOriginal(new SparseInstance(instance.weight(), newVals));
/*  583:     */     }
/*  584: 950 */     return convertInstanceToOriginal(new DenseInstance(instance.weight(), newVals));
/*  585:     */   }
/*  586:     */   
/*  587:     */   private Instances setOutputFormatOriginal()
/*  588:     */     throws Exception
/*  589:     */   {
/*  590: 963 */     ArrayList<Attribute> attributes = new ArrayList();
/*  591: 965 */     for (int i = 0; i < this.m_numAttribs; i++)
/*  592:     */     {
/*  593: 966 */       String att = this.m_trainInstances.attribute(i).name();
/*  594: 967 */       attributes.add(new Attribute(att));
/*  595:     */     }
/*  596: 970 */     if (this.m_hasClass) {
/*  597: 971 */       attributes.add((Attribute)this.m_trainHeader.classAttribute().copy());
/*  598:     */     }
/*  599: 974 */     Instances outputFormat = new Instances(this.m_trainHeader.relationName() + "->PC->original space", attributes, 0);
/*  600: 979 */     if (this.m_hasClass) {
/*  601: 980 */       outputFormat.setClassIndex(outputFormat.numAttributes() - 1);
/*  602:     */     }
/*  603: 983 */     return outputFormat;
/*  604:     */   }
/*  605:     */   
/*  606:     */   private Instances setOutputFormat()
/*  607:     */     throws Exception
/*  608:     */   {
/*  609: 993 */     if (this.m_eigenvalues == null) {
/*  610: 994 */       return null;
/*  611:     */     }
/*  612: 997 */     double cumulative = 0.0D;
/*  613: 998 */     ArrayList<Attribute> attributes = new ArrayList();
/*  614: 999 */     for (int i = this.m_numAttribs - 1; i >= 0; i--)
/*  615:     */     {
/*  616:1000 */       StringBuffer attName = new StringBuffer();
/*  617:     */       
/*  618:1002 */       double[] coeff_mags = new double[this.m_numAttribs];
/*  619:1003 */       for (int j = 0; j < this.m_numAttribs; j++) {
/*  620:1004 */         coeff_mags[j] = (-Math.abs(this.m_eigenvectors[j][this.m_sortedEigens[i]]));
/*  621:     */       }
/*  622:1006 */       int num_attrs = this.m_maxAttrsInName > 0 ? Math.min(this.m_numAttribs, this.m_maxAttrsInName) : this.m_numAttribs;
/*  623:     */       int[] coeff_inds;
/*  624:     */       int[] coeff_inds;
/*  625:1011 */       if (this.m_numAttribs > 0)
/*  626:     */       {
/*  627:1013 */         coeff_inds = Utils.sort(coeff_mags);
/*  628:     */       }
/*  629:     */       else
/*  630:     */       {
/*  631:1016 */         coeff_inds = new int[this.m_numAttribs];
/*  632:1017 */         for (int j = 0; j < this.m_numAttribs; j++) {
/*  633:1018 */           coeff_inds[j] = j;
/*  634:     */         }
/*  635:     */       }
/*  636:1022 */       for (int j = 0; j < num_attrs; j++)
/*  637:     */       {
/*  638:1023 */         double coeff_value = this.m_eigenvectors[coeff_inds[j]][this.m_sortedEigens[i]];
/*  639:1024 */         if ((j > 0) && (coeff_value >= 0.0D)) {
/*  640:1025 */           attName.append("+");
/*  641:     */         }
/*  642:1027 */         attName.append(Utils.doubleToString(coeff_value, 5, 3) + this.m_trainInstances.attribute(coeff_inds[j]).name());
/*  643:     */       }
/*  644:1030 */       if (num_attrs < this.m_numAttribs) {
/*  645:1031 */         attName.append("...");
/*  646:     */       }
/*  647:1034 */       attributes.add(new Attribute(attName.toString()));
/*  648:1035 */       cumulative += this.m_eigenvalues[this.m_sortedEigens[i]];
/*  649:1037 */       if (cumulative / this.m_sumOfEigenValues >= this.m_coverVariance) {
/*  650:     */         break;
/*  651:     */       }
/*  652:     */     }
/*  653:1042 */     if (this.m_hasClass) {
/*  654:1043 */       attributes.add((Attribute)this.m_trainHeader.classAttribute().copy());
/*  655:     */     }
/*  656:1046 */     Instances outputFormat = new Instances(this.m_trainInstances.relationName() + "_principal components", attributes, 0);
/*  657:1051 */     if (this.m_hasClass) {
/*  658:1052 */       outputFormat.setClassIndex(outputFormat.numAttributes() - 1);
/*  659:     */     }
/*  660:1055 */     this.m_outputNumAtts = outputFormat.numAttributes();
/*  661:1056 */     return outputFormat;
/*  662:     */   }
/*  663:     */   
/*  664:     */   public String getRevision()
/*  665:     */   {
/*  666:1066 */     return RevisionUtils.extract("$Revision: 12659 $");
/*  667:     */   }
/*  668:     */   
/*  669:     */   public static void main(String[] argv)
/*  670:     */   {
/*  671:1076 */     runEvaluator(new PrincipalComponents(), argv);
/*  672:     */   }
/*  673:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.PrincipalComponents
 * JD-Core Version:    0.7.0.1
 */