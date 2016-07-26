/*    1:     */ package weka.classifiers.functions;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.Collections;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import no.uib.cipr.matrix.DenseMatrix;
/*    7:     */ import no.uib.cipr.matrix.DenseVector;
/*    8:     */ import no.uib.cipr.matrix.Matrix;
/*    9:     */ import no.uib.cipr.matrix.UpperSymmDenseMatrix;
/*   10:     */ import weka.classifiers.AbstractClassifier;
/*   11:     */ import weka.classifiers.evaluation.RegressionAnalysis;
/*   12:     */ import weka.core.Attribute;
/*   13:     */ import weka.core.Capabilities;
/*   14:     */ import weka.core.Capabilities.Capability;
/*   15:     */ import weka.core.Instance;
/*   16:     */ import weka.core.Instances;
/*   17:     */ import weka.core.Option;
/*   18:     */ import weka.core.OptionHandler;
/*   19:     */ import weka.core.RevisionUtils;
/*   20:     */ import weka.core.SelectedTag;
/*   21:     */ import weka.core.Tag;
/*   22:     */ import weka.core.Utils;
/*   23:     */ import weka.core.WeightedInstancesHandler;
/*   24:     */ import weka.filters.Filter;
/*   25:     */ import weka.filters.supervised.attribute.NominalToBinary;
/*   26:     */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*   27:     */ 
/*   28:     */ public class LinearRegression
/*   29:     */   extends AbstractClassifier
/*   30:     */   implements OptionHandler, WeightedInstancesHandler
/*   31:     */ {
/*   32:     */   public static final int SELECTION_M5 = 0;
/*   33:     */   public static final int SELECTION_NONE = 1;
/*   34:     */   public static final int SELECTION_GREEDY = 2;
/*   35: 105 */   public static final Tag[] TAGS_SELECTION = { new Tag(1, "No attribute selection"), new Tag(0, "M5 method"), new Tag(2, "Greedy method") };
/*   36:     */   static final long serialVersionUID = -3364580862046573747L;
/*   37:     */   protected double[] m_Coefficients;
/*   38:     */   protected boolean[] m_SelectedAttributes;
/*   39:     */   protected Instances m_TransformedData;
/*   40:     */   protected ReplaceMissingValues m_MissingFilter;
/*   41:     */   protected NominalToBinary m_TransformFilter;
/*   42:     */   protected double m_ClassStdDev;
/*   43:     */   protected double m_ClassMean;
/*   44:     */   protected int m_ClassIndex;
/*   45:     */   protected double[] m_Means;
/*   46:     */   protected double[] m_StdDevs;
/*   47:     */   protected boolean m_outputAdditionalStats;
/*   48:     */   protected int m_AttributeSelection;
/*   49: 141 */   protected boolean m_EliminateColinearAttributes = true;
/*   50: 143 */   protected boolean m_checksTurnedOff = false;
/*   51: 145 */   protected double m_Ridge = 1.0E-008D;
/*   52: 147 */   protected boolean m_Minimal = false;
/*   53: 149 */   protected boolean m_ModelBuilt = false;
/*   54:     */   protected boolean m_isZeroR;
/*   55:     */   private int m_df;
/*   56:     */   private double m_RSquared;
/*   57:     */   private double m_RSquaredAdj;
/*   58:     */   private double m_FStat;
/*   59:     */   private double[] m_StdErrorOfCoef;
/*   60:     */   private double[] m_TStats;
/*   61:     */   
/*   62:     */   public LinearRegression()
/*   63:     */   {
/*   64: 166 */     this.m_numDecimalPlaces = 4;
/*   65:     */   }
/*   66:     */   
/*   67:     */   public static void main(String[] argv)
/*   68:     */   {
/*   69: 175 */     runClassifier(new LinearRegression(), argv);
/*   70:     */   }
/*   71:     */   
/*   72:     */   public String globalInfo()
/*   73:     */   {
/*   74: 185 */     return "Class for using linear regression for prediction. Uses the Akaike criterion for model selection, and is able to deal with weighted instances.";
/*   75:     */   }
/*   76:     */   
/*   77:     */   public Capabilities getCapabilities()
/*   78:     */   {
/*   79: 197 */     Capabilities result = super.getCapabilities();
/*   80: 198 */     result.disableAll();
/*   81:     */     
/*   82:     */ 
/*   83: 201 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*   84: 202 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*   85: 203 */     result.enable(Capabilities.Capability.DATE_ATTRIBUTES);
/*   86: 204 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*   87:     */     
/*   88:     */ 
/*   89: 207 */     result.enable(Capabilities.Capability.NUMERIC_CLASS);
/*   90: 208 */     result.enable(Capabilities.Capability.DATE_CLASS);
/*   91: 209 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*   92:     */     
/*   93: 211 */     return result;
/*   94:     */   }
/*   95:     */   
/*   96:     */   public void buildClassifier(Instances data)
/*   97:     */     throws Exception
/*   98:     */   {
/*   99: 223 */     this.m_ModelBuilt = false;
/*  100: 224 */     this.m_isZeroR = false;
/*  101: 226 */     if (data.numInstances() == 1)
/*  102:     */     {
/*  103: 227 */       this.m_Coefficients = new double[1];
/*  104: 228 */       this.m_Coefficients[0] = data.instance(0).classValue();
/*  105: 229 */       this.m_SelectedAttributes = new boolean[data.numAttributes()];
/*  106: 230 */       this.m_isZeroR = true;
/*  107: 231 */       return;
/*  108:     */     }
/*  109: 234 */     if (!this.m_checksTurnedOff)
/*  110:     */     {
/*  111: 236 */       getCapabilities().testWithFail(data);
/*  112: 238 */       if (this.m_outputAdditionalStats)
/*  113:     */       {
/*  114: 242 */         boolean ok = true;
/*  115: 243 */         for (int i = 0; i < data.numInstances(); i++) {
/*  116: 244 */           if (data.instance(i).weight() != 1.0D)
/*  117:     */           {
/*  118: 245 */             ok = false;
/*  119: 246 */             break;
/*  120:     */           }
/*  121:     */         }
/*  122: 249 */         if (!ok) {
/*  123: 250 */           throw new Exception("Can only compute additional statistics on unweighted data");
/*  124:     */         }
/*  125:     */       }
/*  126: 256 */       data = new Instances(data);
/*  127: 257 */       data.deleteWithMissingClass();
/*  128:     */       
/*  129: 259 */       this.m_TransformFilter = new NominalToBinary();
/*  130: 260 */       this.m_TransformFilter.setInputFormat(data);
/*  131: 261 */       data = Filter.useFilter(data, this.m_TransformFilter);
/*  132: 262 */       this.m_MissingFilter = new ReplaceMissingValues();
/*  133: 263 */       this.m_MissingFilter.setInputFormat(data);
/*  134: 264 */       data = Filter.useFilter(data, this.m_MissingFilter);
/*  135: 265 */       data.deleteWithMissingClass();
/*  136:     */     }
/*  137:     */     else
/*  138:     */     {
/*  139: 267 */       this.m_TransformFilter = null;
/*  140: 268 */       this.m_MissingFilter = null;
/*  141:     */     }
/*  142: 271 */     this.m_ClassIndex = data.classIndex();
/*  143: 272 */     this.m_TransformedData = data;
/*  144:     */     
/*  145:     */ 
/*  146: 275 */     this.m_Coefficients = null;
/*  147:     */     
/*  148:     */ 
/*  149: 278 */     this.m_SelectedAttributes = new boolean[data.numAttributes()];
/*  150: 279 */     this.m_Means = new double[data.numAttributes()];
/*  151: 280 */     this.m_StdDevs = new double[data.numAttributes()];
/*  152: 281 */     for (int j = 0; j < data.numAttributes(); j++) {
/*  153: 282 */       if (j != this.m_ClassIndex)
/*  154:     */       {
/*  155: 283 */         this.m_SelectedAttributes[j] = true;
/*  156: 284 */         this.m_Means[j] = data.meanOrMode(j);
/*  157: 285 */         this.m_StdDevs[j] = Math.sqrt(data.variance(j));
/*  158: 286 */         if (this.m_StdDevs[j] == 0.0D) {
/*  159: 287 */           this.m_SelectedAttributes[j] = false;
/*  160:     */         }
/*  161:     */       }
/*  162:     */     }
/*  163: 292 */     this.m_ClassStdDev = Math.sqrt(data.variance(this.m_TransformedData.classIndex()));
/*  164: 293 */     this.m_ClassMean = data.meanOrMode(this.m_TransformedData.classIndex());
/*  165:     */     
/*  166:     */ 
/*  167: 296 */     findBestModel();
/*  168: 298 */     if (this.m_outputAdditionalStats)
/*  169:     */     {
/*  170: 300 */       int k = 1;
/*  171: 301 */       for (int i = 0; i < data.numAttributes(); i++) {
/*  172: 302 */         if ((i != data.classIndex()) && 
/*  173: 303 */           (this.m_SelectedAttributes[i] != 0)) {
/*  174: 304 */           k++;
/*  175:     */         }
/*  176:     */       }
/*  177: 308 */       this.m_df = (this.m_TransformedData.numInstances() - k);
/*  178:     */       
/*  179:     */ 
/*  180: 311 */       double se = calculateSE(this.m_SelectedAttributes, this.m_Coefficients);
/*  181: 312 */       this.m_RSquared = RegressionAnalysis.calculateRSquared(this.m_TransformedData, se);
/*  182: 313 */       this.m_RSquaredAdj = RegressionAnalysis.calculateAdjRSquared(this.m_RSquared, this.m_TransformedData.numInstances(), k);
/*  183:     */       
/*  184:     */ 
/*  185: 316 */       this.m_FStat = RegressionAnalysis.calculateFStat(this.m_RSquared, this.m_TransformedData.numInstances(), k);
/*  186:     */       
/*  187:     */ 
/*  188:     */ 
/*  189: 320 */       this.m_StdErrorOfCoef = RegressionAnalysis.calculateStdErrorOfCoef(this.m_TransformedData, this.m_SelectedAttributes, se, this.m_TransformedData.numInstances(), k);
/*  190:     */       
/*  191:     */ 
/*  192: 323 */       this.m_TStats = RegressionAnalysis.calculateTStats(this.m_Coefficients, this.m_StdErrorOfCoef, k);
/*  193:     */     }
/*  194: 328 */     if (this.m_Minimal)
/*  195:     */     {
/*  196: 329 */       this.m_TransformedData = null;
/*  197: 330 */       this.m_Means = null;
/*  198: 331 */       this.m_StdDevs = null;
/*  199:     */     }
/*  200:     */     else
/*  201:     */     {
/*  202: 333 */       this.m_TransformedData = new Instances(data, 0);
/*  203:     */     }
/*  204: 336 */     this.m_ModelBuilt = true;
/*  205:     */   }
/*  206:     */   
/*  207:     */   public double classifyInstance(Instance instance)
/*  208:     */     throws Exception
/*  209:     */   {
/*  210: 350 */     Instance transformedInstance = instance;
/*  211: 351 */     if ((!this.m_checksTurnedOff) && (!this.m_isZeroR))
/*  212:     */     {
/*  213: 352 */       this.m_TransformFilter.input(transformedInstance);
/*  214: 353 */       this.m_TransformFilter.batchFinished();
/*  215: 354 */       transformedInstance = this.m_TransformFilter.output();
/*  216: 355 */       this.m_MissingFilter.input(transformedInstance);
/*  217: 356 */       this.m_MissingFilter.batchFinished();
/*  218: 357 */       transformedInstance = this.m_MissingFilter.output();
/*  219:     */     }
/*  220: 361 */     return regressionPrediction(transformedInstance, this.m_SelectedAttributes, this.m_Coefficients);
/*  221:     */   }
/*  222:     */   
/*  223:     */   public String toString()
/*  224:     */   {
/*  225: 372 */     if (!this.m_ModelBuilt) {
/*  226: 373 */       return "Linear Regression: No model built yet.";
/*  227:     */     }
/*  228: 376 */     if (this.m_Minimal) {
/*  229: 377 */       return "Linear Regression: Model built.";
/*  230:     */     }
/*  231:     */     try
/*  232:     */     {
/*  233: 381 */       StringBuilder text = new StringBuilder();
/*  234: 382 */       int column = 0;
/*  235: 383 */       boolean first = true;
/*  236:     */       
/*  237: 385 */       text.append("\nLinear Regression Model\n\n");
/*  238:     */       
/*  239: 387 */       text.append(this.m_TransformedData.classAttribute().name() + " =\n\n");
/*  240: 388 */       for (int i = 0; i < this.m_TransformedData.numAttributes(); i++) {
/*  241: 389 */         if ((i != this.m_ClassIndex) && (this.m_SelectedAttributes[i] != 0))
/*  242:     */         {
/*  243: 390 */           if (!first) {
/*  244: 391 */             text.append(" +\n");
/*  245:     */           } else {
/*  246: 393 */             first = false;
/*  247:     */           }
/*  248: 395 */           text.append(Utils.doubleToString(this.m_Coefficients[column], 12, this.m_numDecimalPlaces) + " * ");
/*  249:     */           
/*  250: 397 */           text.append(this.m_TransformedData.attribute(i).name());
/*  251: 398 */           column++;
/*  252:     */         }
/*  253:     */       }
/*  254: 401 */       text.append(" +\n" + Utils.doubleToString(this.m_Coefficients[column], 12, this.m_numDecimalPlaces));
/*  255: 404 */       if (this.m_outputAdditionalStats)
/*  256:     */       {
/*  257: 405 */         int maxAttLength = 0;
/*  258: 406 */         for (int i = 0; i < this.m_TransformedData.numAttributes(); i++) {
/*  259: 407 */           if ((i != this.m_ClassIndex) && (this.m_SelectedAttributes[i] != 0) && 
/*  260: 408 */             (this.m_TransformedData.attribute(i).name().length() > maxAttLength)) {
/*  261: 409 */             maxAttLength = this.m_TransformedData.attribute(i).name().length();
/*  262:     */           }
/*  263:     */         }
/*  264: 413 */         maxAttLength += 3;
/*  265: 414 */         if (maxAttLength < "Variable".length() + 3) {
/*  266: 415 */           maxAttLength = "Variable".length() + 3;
/*  267:     */         }
/*  268: 418 */         text.append("\n\nRegression Analysis:\n\n" + Utils.padRight("Variable", maxAttLength) + "  Coefficient     SE of Coef        t-Stat");
/*  269:     */         
/*  270:     */ 
/*  271: 421 */         column = 0;
/*  272: 422 */         for (int i = 0; i < this.m_TransformedData.numAttributes(); i++) {
/*  273: 423 */           if ((i != this.m_ClassIndex) && (this.m_SelectedAttributes[i] != 0))
/*  274:     */           {
/*  275: 424 */             text.append("\n" + Utils.padRight(this.m_TransformedData.attribute(i).name(), maxAttLength));
/*  276:     */             
/*  277:     */ 
/*  278: 427 */             text.append(Utils.doubleToString(this.m_Coefficients[column], 12, this.m_numDecimalPlaces));
/*  279:     */             
/*  280: 429 */             text.append("   " + Utils.doubleToString(this.m_StdErrorOfCoef[column], 12, this.m_numDecimalPlaces));
/*  281:     */             
/*  282:     */ 
/*  283: 432 */             text.append("   " + Utils.doubleToString(this.m_TStats[column], 12, this.m_numDecimalPlaces));
/*  284:     */             
/*  285: 434 */             column++;
/*  286:     */           }
/*  287:     */         }
/*  288: 437 */         text.append(Utils.padRight("\nconst", maxAttLength + 1) + Utils.doubleToString(this.m_Coefficients[column], 12, this.m_numDecimalPlaces));
/*  289:     */         
/*  290:     */ 
/*  291: 440 */         text.append("   " + Utils.doubleToString(this.m_StdErrorOfCoef[column], 12, this.m_numDecimalPlaces));
/*  292:     */         
/*  293:     */ 
/*  294: 443 */         text.append("   " + Utils.doubleToString(this.m_TStats[column], 12, this.m_numDecimalPlaces));
/*  295:     */         
/*  296:     */ 
/*  297: 446 */         text.append("\n\nDegrees of freedom = " + Integer.toString(this.m_df));
/*  298: 447 */         text.append("\nR^2 value = " + Utils.doubleToString(this.m_RSquared, this.m_numDecimalPlaces));
/*  299:     */         
/*  300: 449 */         text.append("\nAdjusted R^2 = " + Utils.doubleToString(this.m_RSquaredAdj, 5));
/*  301:     */         
/*  302: 451 */         text.append("\nF-statistic = " + Utils.doubleToString(this.m_FStat, this.m_numDecimalPlaces));
/*  303:     */       }
/*  304: 455 */       return text.toString();
/*  305:     */     }
/*  306:     */     catch (Exception e) {}
/*  307: 457 */     return "Can't print Linear Regression!";
/*  308:     */   }
/*  309:     */   
/*  310:     */   public Enumeration<Option> listOptions()
/*  311:     */   {
/*  312: 468 */     java.util.Vector<Option> newVector = new java.util.Vector();
/*  313:     */     
/*  314: 470 */     newVector.addElement(new Option("\tSet the attribute selection method to use. 1 = None, 2 = Greedy.\n\t(default 0 = M5' method)", "S", 1, "-S <number of selection method>"));
/*  315:     */     
/*  316:     */ 
/*  317:     */ 
/*  318: 474 */     newVector.addElement(new Option("\tDo not try to eliminate colinear attributes.\n", "C", 0, "-C"));
/*  319:     */     
/*  320:     */ 
/*  321: 477 */     newVector.addElement(new Option("\tSet the attribute selection method to use. 1 = None, 2 = Greedy.\n\t(default 0 = M5' method)", "S", 1, "-S <number of selection method>"));
/*  322:     */     
/*  323:     */ 
/*  324:     */ 
/*  325: 481 */     newVector.addElement(new Option("\tSet ridge parameter (default 1.0e-8).\n", "R", 1, "-R <double>"));
/*  326:     */     
/*  327:     */ 
/*  328: 484 */     newVector.addElement(new Option("\tConserve memory, don't keep dataset header and means/stdevs.\n\tModel cannot be printed out if this option is enabled.\t(default: keep data)", "minimal", 0, "-minimal"));
/*  329:     */     
/*  330:     */ 
/*  331:     */ 
/*  332:     */ 
/*  333: 489 */     newVector.addElement(new Option("\tOutput additional statistics.", "additional-stats", 0, "-additional-stats"));
/*  334:     */     
/*  335:     */ 
/*  336: 492 */     newVector.addAll(Collections.list(super.listOptions()));
/*  337:     */     
/*  338: 494 */     return newVector.elements();
/*  339:     */   }
/*  340:     */   
/*  341:     */   public double[] coefficients()
/*  342:     */   {
/*  343: 504 */     double[] coefficients = new double[this.m_SelectedAttributes.length + 1];
/*  344: 505 */     int counter = 0;
/*  345: 506 */     for (int i = 0; i < this.m_SelectedAttributes.length; i++) {
/*  346: 507 */       if ((this.m_SelectedAttributes[i] != 0) && (i != this.m_ClassIndex)) {
/*  347: 508 */         coefficients[i] = this.m_Coefficients[(counter++)];
/*  348:     */       }
/*  349:     */     }
/*  350: 511 */     coefficients[this.m_SelectedAttributes.length] = this.m_Coefficients[counter];
/*  351: 512 */     return coefficients;
/*  352:     */   }
/*  353:     */   
/*  354:     */   public String[] getOptions()
/*  355:     */   {
/*  356: 522 */     java.util.Vector<String> result = new java.util.Vector();
/*  357:     */     
/*  358: 524 */     result.add("-S");
/*  359: 525 */     result.add("" + getAttributeSelectionMethod().getSelectedTag().getID());
/*  360: 527 */     if (!getEliminateColinearAttributes()) {
/*  361: 528 */       result.add("-C");
/*  362:     */     }
/*  363: 531 */     result.add("-R");
/*  364: 532 */     result.add("" + getRidge());
/*  365: 534 */     if (getMinimal()) {
/*  366: 535 */       result.add("-minimal");
/*  367:     */     }
/*  368: 538 */     if (getOutputAdditionalStats()) {
/*  369: 539 */       result.add("-additional-stats");
/*  370:     */     }
/*  371: 542 */     Collections.addAll(result, super.getOptions());
/*  372:     */     
/*  373: 544 */     return (String[])result.toArray(new String[result.size()]);
/*  374:     */   }
/*  375:     */   
/*  376:     */   public void setOptions(String[] options)
/*  377:     */     throws Exception
/*  378:     */   {
/*  379: 602 */     String selectionString = Utils.getOption('S', options);
/*  380: 603 */     if (selectionString.length() != 0) {
/*  381: 604 */       setAttributeSelectionMethod(new SelectedTag(Integer.parseInt(selectionString), TAGS_SELECTION));
/*  382:     */     } else {
/*  383: 607 */       setAttributeSelectionMethod(new SelectedTag(0, TAGS_SELECTION));
/*  384:     */     }
/*  385: 609 */     String ridgeString = Utils.getOption('R', options);
/*  386: 610 */     if (ridgeString.length() != 0) {
/*  387: 611 */       setRidge(new Double(ridgeString).doubleValue());
/*  388:     */     } else {
/*  389: 613 */       setRidge(1.0E-008D);
/*  390:     */     }
/*  391: 615 */     setEliminateColinearAttributes(!Utils.getFlag('C', options));
/*  392: 616 */     setMinimal(Utils.getFlag("minimal", options));
/*  393:     */     
/*  394: 618 */     setOutputAdditionalStats(Utils.getFlag("additional-stats", options));
/*  395:     */     
/*  396: 620 */     super.setOptions(options);
/*  397: 621 */     Utils.checkForRemainingOptions(options);
/*  398:     */   }
/*  399:     */   
/*  400:     */   public String ridgeTipText()
/*  401:     */   {
/*  402: 631 */     return "The value of the Ridge parameter.";
/*  403:     */   }
/*  404:     */   
/*  405:     */   public double getRidge()
/*  406:     */   {
/*  407: 641 */     return this.m_Ridge;
/*  408:     */   }
/*  409:     */   
/*  410:     */   public void setRidge(double newRidge)
/*  411:     */   {
/*  412: 651 */     this.m_Ridge = newRidge;
/*  413:     */   }
/*  414:     */   
/*  415:     */   public String eliminateColinearAttributesTipText()
/*  416:     */   {
/*  417: 661 */     return "Eliminate colinear attributes.";
/*  418:     */   }
/*  419:     */   
/*  420:     */   public boolean getEliminateColinearAttributes()
/*  421:     */   {
/*  422: 671 */     return this.m_EliminateColinearAttributes;
/*  423:     */   }
/*  424:     */   
/*  425:     */   public void setEliminateColinearAttributes(boolean newEliminateColinearAttributes)
/*  426:     */   {
/*  427: 683 */     this.m_EliminateColinearAttributes = newEliminateColinearAttributes;
/*  428:     */   }
/*  429:     */   
/*  430:     */   public int numParameters()
/*  431:     */   {
/*  432: 692 */     return this.m_Coefficients.length - 1;
/*  433:     */   }
/*  434:     */   
/*  435:     */   public String attributeSelectionMethodTipText()
/*  436:     */   {
/*  437: 702 */     return "Set the method used to select attributes for use in the linear regression. Available methods are: no attribute selection, attribute selection using M5's method (step through the attributes removing the one with the smallest standardised coefficient until no improvement is observed in the estimate of the error given by the Akaike information criterion), and a greedy selection using the Akaike information metric.";
/*  438:     */   }
/*  439:     */   
/*  440:     */   public SelectedTag getAttributeSelectionMethod()
/*  441:     */   {
/*  442: 718 */     return new SelectedTag(this.m_AttributeSelection, TAGS_SELECTION);
/*  443:     */   }
/*  444:     */   
/*  445:     */   public void setAttributeSelectionMethod(SelectedTag method)
/*  446:     */   {
/*  447: 728 */     if (method.getTags() == TAGS_SELECTION) {
/*  448: 729 */       this.m_AttributeSelection = method.getSelectedTag().getID();
/*  449:     */     }
/*  450:     */   }
/*  451:     */   
/*  452:     */   public String minimalTipText()
/*  453:     */   {
/*  454: 740 */     return "If enabled, dataset header, means and stdevs get discarded to conserve memory; also, the model cannot be printed out.";
/*  455:     */   }
/*  456:     */   
/*  457:     */   public boolean getMinimal()
/*  458:     */   {
/*  459: 751 */     return this.m_Minimal;
/*  460:     */   }
/*  461:     */   
/*  462:     */   public void setMinimal(boolean value)
/*  463:     */   {
/*  464: 761 */     this.m_Minimal = value;
/*  465:     */   }
/*  466:     */   
/*  467:     */   public String outputAdditionalStatsTipText()
/*  468:     */   {
/*  469: 771 */     return "Output additional statistics (such as std deviation of coefficients and t-statistics)";
/*  470:     */   }
/*  471:     */   
/*  472:     */   public boolean getOutputAdditionalStats()
/*  473:     */   {
/*  474: 782 */     return this.m_outputAdditionalStats;
/*  475:     */   }
/*  476:     */   
/*  477:     */   public void setOutputAdditionalStats(boolean additional)
/*  478:     */   {
/*  479: 792 */     this.m_outputAdditionalStats = additional;
/*  480:     */   }
/*  481:     */   
/*  482:     */   public void turnChecksOff()
/*  483:     */   {
/*  484: 800 */     this.m_checksTurnedOff = true;
/*  485:     */   }
/*  486:     */   
/*  487:     */   public void turnChecksOn()
/*  488:     */   {
/*  489: 807 */     this.m_checksTurnedOff = false;
/*  490:     */   }
/*  491:     */   
/*  492:     */   protected boolean deselectColinearAttributes(boolean[] selectedAttributes, double[] coefficients)
/*  493:     */   {
/*  494: 822 */     double maxSC = 1.5D;
/*  495: 823 */     int maxAttr = -1;int coeff = 0;
/*  496: 824 */     for (int i = 0; i < selectedAttributes.length; i++) {
/*  497: 825 */       if (selectedAttributes[i] != 0)
/*  498:     */       {
/*  499: 826 */         double SC = Math.abs(coefficients[coeff] * this.m_StdDevs[i] / this.m_ClassStdDev);
/*  500: 828 */         if (SC > maxSC)
/*  501:     */         {
/*  502: 829 */           maxSC = SC;
/*  503: 830 */           maxAttr = i;
/*  504:     */         }
/*  505: 832 */         coeff++;
/*  506:     */       }
/*  507:     */     }
/*  508: 835 */     if (maxAttr >= 0)
/*  509:     */     {
/*  510: 836 */       selectedAttributes[maxAttr] = false;
/*  511: 837 */       if (this.m_Debug) {
/*  512: 838 */         System.out.println("Deselected colinear attribute:" + (maxAttr + 1) + " with standardised coefficient: " + maxSC);
/*  513:     */       }
/*  514: 841 */       return true;
/*  515:     */     }
/*  516: 843 */     return false;
/*  517:     */   }
/*  518:     */   
/*  519:     */   protected void findBestModel()
/*  520:     */     throws Exception
/*  521:     */   {
/*  522: 856 */     int numInstances = this.m_TransformedData.numInstances();
/*  523: 858 */     if (this.m_Debug) {
/*  524: 859 */       System.out.println(new Instances(this.m_TransformedData, 0).toString());
/*  525:     */     }
/*  526:     */     do
/*  527:     */     {
/*  528: 864 */       this.m_Coefficients = doRegression(this.m_SelectedAttributes);
/*  529: 866 */     } while ((this.m_EliminateColinearAttributes) && (deselectColinearAttributes(this.m_SelectedAttributes, this.m_Coefficients)));
/*  530: 870 */     int numAttributes = 1;
/*  531: 871 */     for (boolean m_SelectedAttribute : this.m_SelectedAttributes) {
/*  532: 872 */       if (m_SelectedAttribute) {
/*  533: 873 */         numAttributes++;
/*  534:     */       }
/*  535:     */     }
/*  536: 877 */     double fullMSE = calculateSE(this.m_SelectedAttributes, this.m_Coefficients);
/*  537: 878 */     double akaike = numInstances - numAttributes + 2 * numAttributes;
/*  538: 879 */     if (this.m_Debug) {
/*  539: 880 */       System.out.println("Initial Akaike value: " + akaike);
/*  540:     */     }
/*  541: 884 */     int currentNumAttributes = numAttributes;
/*  542:     */     boolean improved;
/*  543: 885 */     switch (this.m_AttributeSelection)
/*  544:     */     {
/*  545:     */     case 2: 
/*  546:     */       do
/*  547:     */       {
/*  548: 891 */         boolean[] currentSelected = (boolean[])this.m_SelectedAttributes.clone();
/*  549: 892 */         improved = false;
/*  550: 893 */         currentNumAttributes--;
/*  551: 895 */         for (int i = 0; i < this.m_SelectedAttributes.length; i++) {
/*  552: 896 */           if (currentSelected[i] != 0)
/*  553:     */           {
/*  554: 899 */             currentSelected[i] = false;
/*  555: 900 */             double[] currentCoeffs = doRegression(currentSelected);
/*  556: 901 */             double currentMSE = calculateSE(currentSelected, currentCoeffs);
/*  557: 902 */             double currentAkaike = currentMSE / fullMSE * (numInstances - numAttributes) + 2 * currentNumAttributes;
/*  558: 905 */             if (this.m_Debug) {
/*  559: 906 */               System.out.println("(akaike: " + currentAkaike);
/*  560:     */             }
/*  561: 910 */             if (currentAkaike < akaike)
/*  562:     */             {
/*  563: 911 */               if (this.m_Debug) {
/*  564: 912 */                 System.err.println("Removing attribute " + (i + 1) + " improved Akaike: " + currentAkaike);
/*  565:     */               }
/*  566: 915 */               improved = true;
/*  567: 916 */               akaike = currentAkaike;
/*  568: 917 */               System.arraycopy(currentSelected, 0, this.m_SelectedAttributes, 0, this.m_SelectedAttributes.length);
/*  569:     */               
/*  570: 919 */               this.m_Coefficients = currentCoeffs;
/*  571:     */             }
/*  572: 921 */             currentSelected[i] = true;
/*  573:     */           }
/*  574:     */         }
/*  575: 924 */       } while (improved);
/*  576: 925 */       break;
/*  577:     */     case 0: 
/*  578:     */       for (;;)
/*  579:     */       {
/*  580: 932 */         improved = false;
/*  581: 933 */         currentNumAttributes--;
/*  582:     */         
/*  583:     */ 
/*  584: 936 */         double minSC = 0.0D;
/*  585: 937 */         int minAttr = -1;int coeff = 0;
/*  586: 938 */         for (int i = 0; i < this.m_SelectedAttributes.length; i++) {
/*  587: 939 */           if (this.m_SelectedAttributes[i] != 0)
/*  588:     */           {
/*  589: 940 */             double SC = Math.abs(this.m_Coefficients[coeff] * this.m_StdDevs[i] / this.m_ClassStdDev);
/*  590: 942 */             if ((coeff == 0) || (SC < minSC))
/*  591:     */             {
/*  592: 943 */               minSC = SC;
/*  593: 944 */               minAttr = i;
/*  594:     */             }
/*  595: 946 */             coeff++;
/*  596:     */           }
/*  597:     */         }
/*  598: 951 */         if (minAttr >= 0)
/*  599:     */         {
/*  600: 952 */           this.m_SelectedAttributes[minAttr] = false;
/*  601: 953 */           double[] currentCoeffs = doRegression(this.m_SelectedAttributes);
/*  602: 954 */           double currentMSE = calculateSE(this.m_SelectedAttributes, currentCoeffs);
/*  603: 955 */           double currentAkaike = currentMSE / fullMSE * (numInstances - numAttributes) + 2 * currentNumAttributes;
/*  604: 958 */           if (this.m_Debug) {
/*  605: 959 */             System.out.println("(akaike: " + currentAkaike);
/*  606:     */           }
/*  607: 963 */           if (currentAkaike < akaike)
/*  608:     */           {
/*  609: 964 */             if (this.m_Debug) {
/*  610: 965 */               System.err.println("Removing attribute " + (minAttr + 1) + " improved Akaike: " + currentAkaike);
/*  611:     */             }
/*  612: 968 */             improved = true;
/*  613: 969 */             akaike = currentAkaike;
/*  614: 970 */             this.m_Coefficients = currentCoeffs;
/*  615:     */           }
/*  616:     */           else
/*  617:     */           {
/*  618: 972 */             this.m_SelectedAttributes[minAttr] = true;
/*  619:     */           }
/*  620:     */         }
/*  621: 975 */         if (!improved) {
/*  622:     */           break;
/*  623:     */         }
/*  624:     */       }
/*  625:     */     }
/*  626:     */   }
/*  627:     */   
/*  628:     */   protected double calculateSE(boolean[] selectedAttributes, double[] coefficients)
/*  629:     */     throws Exception
/*  630:     */   {
/*  631: 995 */     double mse = 0.0D;
/*  632: 996 */     for (int i = 0; i < this.m_TransformedData.numInstances(); i++)
/*  633:     */     {
/*  634: 997 */       double prediction = regressionPrediction(this.m_TransformedData.instance(i), selectedAttributes, coefficients);
/*  635:     */       
/*  636:     */ 
/*  637:1000 */       double error = prediction - this.m_TransformedData.instance(i).classValue();
/*  638:1001 */       mse += error * error;
/*  639:     */     }
/*  640:1003 */     return mse;
/*  641:     */   }
/*  642:     */   
/*  643:     */   protected double regressionPrediction(Instance transformedInstance, boolean[] selectedAttributes, double[] coefficients)
/*  644:     */     throws Exception
/*  645:     */   {
/*  646:1021 */     double result = 0.0D;
/*  647:1022 */     int column = 0;
/*  648:1023 */     for (int j = 0; j < transformedInstance.numAttributes(); j++) {
/*  649:1024 */       if ((this.m_ClassIndex != j) && (selectedAttributes[j] != 0))
/*  650:     */       {
/*  651:1025 */         result += coefficients[column] * transformedInstance.value(j);
/*  652:1026 */         column++;
/*  653:     */       }
/*  654:     */     }
/*  655:1029 */     result += coefficients[column];
/*  656:     */     
/*  657:1031 */     return result;
/*  658:     */   }
/*  659:     */   
/*  660:     */   protected double[] doRegression(boolean[] selectedAttributes)
/*  661:     */     throws Exception
/*  662:     */   {
/*  663:1046 */     if (this.m_Debug)
/*  664:     */     {
/*  665:1047 */       System.out.print("doRegression(");
/*  666:1048 */       for (boolean selectedAttribute : selectedAttributes) {
/*  667:1049 */         System.out.print(" " + selectedAttribute);
/*  668:     */       }
/*  669:1051 */       System.out.println(" )");
/*  670:     */     }
/*  671:1053 */     int numAttributes = 0;
/*  672:1054 */     for (boolean selectedAttribute : selectedAttributes) {
/*  673:1055 */       if (selectedAttribute) {
/*  674:1056 */         numAttributes++;
/*  675:     */       }
/*  676:     */     }
/*  677:1061 */     Matrix independentTransposed = null;
/*  678:1062 */     no.uib.cipr.matrix.Vector dependent = null;
/*  679:1063 */     if (numAttributes > 0)
/*  680:     */     {
/*  681:1064 */       independentTransposed = new DenseMatrix(numAttributes, this.m_TransformedData.numInstances());
/*  682:1065 */       dependent = new DenseVector(this.m_TransformedData.numInstances());
/*  683:1066 */       for (int i = 0; i < this.m_TransformedData.numInstances(); i++)
/*  684:     */       {
/*  685:1067 */         Instance inst = this.m_TransformedData.instance(i);
/*  686:1068 */         double sqrt_weight = Math.sqrt(inst.weight());
/*  687:1069 */         int row = 0;
/*  688:1070 */         for (int j = 0; j < this.m_TransformedData.numAttributes(); j++) {
/*  689:1071 */           if (j == this.m_ClassIndex)
/*  690:     */           {
/*  691:1072 */             dependent.set(i, inst.classValue() * sqrt_weight);
/*  692:     */           }
/*  693:1074 */           else if (selectedAttributes[j] != 0)
/*  694:     */           {
/*  695:1075 */             double value = inst.value(j) - this.m_Means[j];
/*  696:1079 */             if (!this.m_checksTurnedOff) {
/*  697:1080 */               value /= this.m_StdDevs[j];
/*  698:     */             }
/*  699:1082 */             independentTransposed.set(row, i, value * sqrt_weight);
/*  700:1083 */             row++;
/*  701:     */           }
/*  702:     */         }
/*  703:     */       }
/*  704:     */     }
/*  705:1093 */     double[] coefficients = new double[numAttributes + 1];
/*  706:1094 */     if (numAttributes > 0)
/*  707:     */     {
/*  708:1096 */       no.uib.cipr.matrix.Vector aTy = independentTransposed.mult(dependent, new DenseVector(numAttributes));
/*  709:1097 */       Matrix aTa = new UpperSymmDenseMatrix(numAttributes).rank1(independentTransposed);
/*  710:1098 */       independentTransposed = null;
/*  711:1099 */       dependent = null;
/*  712:     */       
/*  713:1101 */       boolean success = true;
/*  714:1102 */       no.uib.cipr.matrix.Vector coeffsWithoutIntercept = null;
/*  715:1103 */       double ridge = getRidge();
/*  716:     */       do
/*  717:     */       {
/*  718:1105 */         for (int i = 0; i < numAttributes; i++) {
/*  719:1106 */           aTa.add(i, i, ridge);
/*  720:     */         }
/*  721:     */         try
/*  722:     */         {
/*  723:1109 */           coeffsWithoutIntercept = aTa.solve(aTy, new DenseVector(numAttributes));
/*  724:1110 */           success = true;
/*  725:     */         }
/*  726:     */         catch (Exception ex)
/*  727:     */         {
/*  728:1112 */           for (int i = 0; i < numAttributes; i++) {
/*  729:1113 */             aTa.add(i, i, -ridge);
/*  730:     */           }
/*  731:1115 */           ridge *= 10.0D;
/*  732:1116 */           success = false;
/*  733:     */         }
/*  734:1118 */       } while (!success);
/*  735:1120 */       System.arraycopy(((DenseVector)coeffsWithoutIntercept).getData(), 0, coefficients, 0, numAttributes);
/*  736:     */     }
/*  737:1122 */     coefficients[numAttributes] = this.m_ClassMean;
/*  738:     */     
/*  739:     */ 
/*  740:1125 */     int column = 0;
/*  741:1126 */     for (int i = 0; i < this.m_TransformedData.numAttributes(); i++) {
/*  742:1127 */       if ((i != this.m_TransformedData.classIndex()) && (selectedAttributes[i] != 0))
/*  743:     */       {
/*  744:1131 */         if (!this.m_checksTurnedOff) {
/*  745:1132 */           coefficients[column] /= this.m_StdDevs[i];
/*  746:     */         }
/*  747:1136 */         coefficients[(coefficients.length - 1)] -= coefficients[column] * this.m_Means[i];
/*  748:     */         
/*  749:1138 */         column++;
/*  750:     */       }
/*  751:     */     }
/*  752:1142 */     return coefficients;
/*  753:     */   }
/*  754:     */   
/*  755:     */   public String getRevision()
/*  756:     */   {
/*  757:1152 */     return RevisionUtils.extract("$Revision: 12643 $");
/*  758:     */   }
/*  759:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.functions.LinearRegression
 * JD-Core Version:    0.7.0.1
 */