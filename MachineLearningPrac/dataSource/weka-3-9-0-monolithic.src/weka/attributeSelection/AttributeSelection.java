/*    1:     */ package weka.attributeSelection;
/*    2:     */ 
/*    3:     */ import java.beans.BeanInfo;
/*    4:     */ import java.beans.IntrospectionException;
/*    5:     */ import java.beans.Introspector;
/*    6:     */ import java.beans.PropertyDescriptor;
/*    7:     */ import java.io.PrintStream;
/*    8:     */ import java.io.Serializable;
/*    9:     */ import java.lang.reflect.Method;
/*   10:     */ import java.util.Enumeration;
/*   11:     */ import java.util.Random;
/*   12:     */ import weka.core.Attribute;
/*   13:     */ import weka.core.Instance;
/*   14:     */ import weka.core.Instances;
/*   15:     */ import weka.core.Option;
/*   16:     */ import weka.core.OptionHandler;
/*   17:     */ import weka.core.RevisionHandler;
/*   18:     */ import weka.core.RevisionUtils;
/*   19:     */ import weka.core.Utils;
/*   20:     */ import weka.core.converters.ConverterUtils.DataSource;
/*   21:     */ import weka.filters.Filter;
/*   22:     */ import weka.filters.unsupervised.attribute.Remove;
/*   23:     */ 
/*   24:     */ public class AttributeSelection
/*   25:     */   implements Serializable, RevisionHandler
/*   26:     */ {
/*   27:     */   static final long serialVersionUID = 4170171824147584330L;
/*   28:     */   private Instances m_trainInstances;
/*   29:     */   private ASEvaluation m_ASEvaluator;
/*   30:     */   private ASSearch m_searchMethod;
/*   31:     */   private int m_numFolds;
/*   32:     */   private final StringBuffer m_selectionResults;
/*   33:     */   private boolean m_doRank;
/*   34:     */   private boolean m_doXval;
/*   35:     */   private int m_seed;
/*   36:     */   private int m_numToSelect;
/*   37:     */   private int[] m_selectedAttributeSet;
/*   38:     */   private double[][] m_attributeRanking;
/*   39: 135 */   private AttributeTransformer m_transformer = null;
/*   40: 141 */   private Remove m_attributeFilter = null;
/*   41: 147 */   private double[][] m_rankResults = (double[][])null;
/*   42: 148 */   private double[] m_subsetResults = null;
/*   43:     */   
/*   44:     */   public int numberAttributesSelected()
/*   45:     */     throws Exception
/*   46:     */   {
/*   47: 157 */     int[] att = selectedAttributes();
/*   48: 158 */     return att.length - 1;
/*   49:     */   }
/*   50:     */   
/*   51:     */   public int[] selectedAttributes()
/*   52:     */     throws Exception
/*   53:     */   {
/*   54: 168 */     if (this.m_selectedAttributeSet == null) {
/*   55: 169 */       throw new Exception("Attribute selection has not been performed yet!");
/*   56:     */     }
/*   57: 171 */     return this.m_selectedAttributeSet;
/*   58:     */   }
/*   59:     */   
/*   60:     */   public double[][] rankedAttributes()
/*   61:     */     throws Exception
/*   62:     */   {
/*   63: 182 */     if (this.m_attributeRanking == null) {
/*   64: 183 */       throw new Exception("Ranking has not been performed");
/*   65:     */     }
/*   66: 185 */     return this.m_attributeRanking;
/*   67:     */   }
/*   68:     */   
/*   69:     */   public void setEvaluator(ASEvaluation evaluator)
/*   70:     */   {
/*   71: 194 */     this.m_ASEvaluator = evaluator;
/*   72:     */   }
/*   73:     */   
/*   74:     */   public void setSearch(ASSearch search)
/*   75:     */   {
/*   76: 203 */     this.m_searchMethod = search;
/*   77: 205 */     if ((this.m_searchMethod instanceof RankedOutputSearch)) {
/*   78: 206 */       setRanking(((RankedOutputSearch)this.m_searchMethod).getGenerateRanking());
/*   79:     */     }
/*   80:     */   }
/*   81:     */   
/*   82:     */   public void setFolds(int folds)
/*   83:     */   {
/*   84: 216 */     this.m_numFolds = folds;
/*   85:     */   }
/*   86:     */   
/*   87:     */   public void setRanking(boolean r)
/*   88:     */   {
/*   89: 225 */     this.m_doRank = r;
/*   90:     */   }
/*   91:     */   
/*   92:     */   public void setXval(boolean x)
/*   93:     */   {
/*   94: 234 */     this.m_doXval = x;
/*   95:     */   }
/*   96:     */   
/*   97:     */   public void setSeed(int s)
/*   98:     */   {
/*   99: 243 */     this.m_seed = s;
/*  100:     */   }
/*  101:     */   
/*  102:     */   public String toResultsString()
/*  103:     */   {
/*  104: 252 */     return this.m_selectionResults.toString();
/*  105:     */   }
/*  106:     */   
/*  107:     */   public Instances reduceDimensionality(Instances in)
/*  108:     */     throws Exception
/*  109:     */   {
/*  110: 264 */     if (this.m_attributeFilter == null) {
/*  111: 265 */       throw new Exception("No feature selection has been performed yet!");
/*  112:     */     }
/*  113: 268 */     if (this.m_transformer != null)
/*  114:     */     {
/*  115: 269 */       Instances transformed = new Instances(this.m_transformer.transformedHeader(), in.numInstances());
/*  116: 271 */       for (int i = 0; i < in.numInstances(); i++) {
/*  117: 272 */         transformed.add(this.m_transformer.convertInstance(in.instance(i)));
/*  118:     */       }
/*  119: 274 */       return Filter.useFilter(transformed, this.m_attributeFilter);
/*  120:     */     }
/*  121: 277 */     return Filter.useFilter(in, this.m_attributeFilter);
/*  122:     */   }
/*  123:     */   
/*  124:     */   public Instance reduceDimensionality(Instance in)
/*  125:     */     throws Exception
/*  126:     */   {
/*  127: 289 */     if (this.m_attributeFilter == null) {
/*  128: 290 */       throw new Exception("No feature selection has been performed yet!");
/*  129:     */     }
/*  130: 292 */     if (this.m_transformer != null) {
/*  131: 293 */       in = this.m_transformer.convertInstance(in);
/*  132:     */     }
/*  133: 295 */     this.m_attributeFilter.input(in);
/*  134: 296 */     this.m_attributeFilter.batchFinished();
/*  135: 297 */     Instance result = this.m_attributeFilter.output();
/*  136: 298 */     return result;
/*  137:     */   }
/*  138:     */   
/*  139:     */   public AttributeSelection()
/*  140:     */   {
/*  141: 306 */     setFolds(10);
/*  142: 307 */     setRanking(false);
/*  143: 308 */     setXval(false);
/*  144: 309 */     setSeed(1);
/*  145: 310 */     setEvaluator(new CfsSubsetEval());
/*  146: 311 */     setSearch(new GreedyStepwise());
/*  147: 312 */     this.m_selectionResults = new StringBuffer();
/*  148: 313 */     this.m_selectedAttributeSet = null;
/*  149: 314 */     this.m_attributeRanking = ((double[][])null);
/*  150:     */   }
/*  151:     */   
/*  152:     */   public static String SelectAttributes(ASEvaluation ASEvaluator, String[] options)
/*  153:     */     throws Exception
/*  154:     */   {
/*  155: 330 */     Instances train = null;
/*  156: 331 */     ASSearch searchMethod = null;
/*  157: 332 */     String[] optionsTmp = (String[])options.clone();
/*  158: 333 */     boolean helpRequested = false;
/*  159:     */     String trainFileName;
/*  160:     */     try
/*  161:     */     {
/*  162: 337 */       trainFileName = Utils.getOption('i', options);
/*  163: 338 */       helpRequested = Utils.getFlag('h', optionsTmp);
/*  164: 340 */       if ((helpRequested) || (trainFileName.length() == 0))
/*  165:     */       {
/*  166: 341 */         String searchName = Utils.getOption('s', optionsTmp);
/*  167: 342 */         if (searchName.length() != 0)
/*  168:     */         {
/*  169: 343 */           String[] searchOptions = Utils.splitOptions(searchName);
/*  170: 344 */           searchMethod = (ASSearch)Class.forName(searchOptions[0]).newInstance();
/*  171:     */         }
/*  172: 348 */         if (helpRequested) {
/*  173: 349 */           throw new Exception("Help requested.");
/*  174:     */         }
/*  175: 351 */         throw new Exception("No training file given.");
/*  176:     */       }
/*  177:     */     }
/*  178:     */     catch (Exception e)
/*  179:     */     {
/*  180: 355 */       throw new Exception('\n' + e.getMessage() + makeOptionString(ASEvaluator, searchMethod));
/*  181:     */     }
/*  182: 359 */     ConverterUtils.DataSource source = new ConverterUtils.DataSource(trainFileName);
/*  183: 360 */     train = source.getDataSet();
/*  184: 361 */     return SelectAttributes(ASEvaluator, options, train);
/*  185:     */   }
/*  186:     */   
/*  187:     */   public String CVResultsString()
/*  188:     */     throws Exception
/*  189:     */   {
/*  190: 372 */     StringBuffer CvString = new StringBuffer();
/*  191: 374 */     if (((this.m_subsetResults == null) && (this.m_rankResults == null)) || (this.m_trainInstances == null)) {
/*  192: 376 */       throw new Exception("Attribute selection has not been performed yet!");
/*  193:     */     }
/*  194: 379 */     int fieldWidth = (int)(Math.log(this.m_trainInstances.numAttributes()) + 1.0D);
/*  195:     */     
/*  196: 381 */     CvString.append("\n\n=== Attribute selection " + this.m_numFolds + " fold cross-validation ");
/*  197: 384 */     if ((!(this.m_ASEvaluator instanceof UnsupervisedSubsetEvaluator)) && (!(this.m_ASEvaluator instanceof UnsupervisedAttributeEvaluator)) && (this.m_trainInstances.classAttribute().isNominal()))
/*  198:     */     {
/*  199: 387 */       CvString.append("(stratified), seed: ");
/*  200: 388 */       CvString.append(this.m_seed + " ===\n\n");
/*  201:     */     }
/*  202:     */     else
/*  203:     */     {
/*  204: 390 */       CvString.append("seed: " + this.m_seed + " ===\n\n");
/*  205:     */     }
/*  206: 393 */     if (((this.m_searchMethod instanceof RankedOutputSearch)) && (this.m_doRank == true))
/*  207:     */     {
/*  208: 394 */       CvString.append("average merit      average rank  attribute\n");
/*  209: 397 */       for (int i = 0; i < this.m_rankResults[0].length; i++)
/*  210:     */       {
/*  211: 398 */         this.m_rankResults[0][i] /= this.m_numFolds;
/*  212: 399 */         double var = this.m_rankResults[0][i] * this.m_rankResults[0][i] * this.m_numFolds;
/*  213: 400 */         var = this.m_rankResults[2][i] - var;
/*  214: 401 */         var /= this.m_numFolds;
/*  215: 403 */         if (var <= 0.0D)
/*  216:     */         {
/*  217: 404 */           var = 0.0D;
/*  218: 405 */           this.m_rankResults[2][i] = 0.0D;
/*  219:     */         }
/*  220:     */         else
/*  221:     */         {
/*  222: 407 */           this.m_rankResults[2][i] = Math.sqrt(var);
/*  223:     */         }
/*  224: 410 */         this.m_rankResults[1][i] /= this.m_numFolds;
/*  225: 411 */         var = this.m_rankResults[1][i] * this.m_rankResults[1][i] * this.m_numFolds;
/*  226: 412 */         var = this.m_rankResults[3][i] - var;
/*  227: 413 */         var /= this.m_numFolds;
/*  228: 415 */         if (var <= 0.0D)
/*  229:     */         {
/*  230: 416 */           var = 0.0D;
/*  231: 417 */           this.m_rankResults[3][i] = 0.0D;
/*  232:     */         }
/*  233:     */         else
/*  234:     */         {
/*  235: 419 */           this.m_rankResults[3][i] = Math.sqrt(var);
/*  236:     */         }
/*  237:     */       }
/*  238: 424 */       int[] s = Utils.sort(this.m_rankResults[1]);
/*  239: 425 */       for (int element : s) {
/*  240: 426 */         if (this.m_rankResults[1][element] > 0.0D) {
/*  241: 427 */           CvString.append(Utils.doubleToString(this.m_rankResults[0][element], 6, 3) + " +-" + Utils.doubleToString(this.m_rankResults[2][element], 6, 3) + "   " + Utils.doubleToString(this.m_rankResults[1][element], fieldWidth + 2, 1) + " +-" + Utils.doubleToString(this.m_rankResults[3][element], 5, 2) + "  " + Utils.doubleToString(element + 1, fieldWidth, 0) + " " + this.m_trainInstances.attribute(element).name() + "\n");
/*  242:     */         }
/*  243:     */       }
/*  244:     */     }
/*  245:     */     else
/*  246:     */     {
/*  247: 442 */       CvString.append("number of folds (%)  attribute\n");
/*  248: 444 */       for (int i = 0; i < this.m_subsetResults.length; i++) {
/*  249: 445 */         if (((this.m_ASEvaluator instanceof UnsupervisedSubsetEvaluator)) || (i != this.m_trainInstances.classIndex())) {
/*  250: 447 */           CvString.append(Utils.doubleToString(this.m_subsetResults[i], 12, 0) + "(" + Utils.doubleToString(this.m_subsetResults[i] / this.m_numFolds * 100.0D, 3, 0) + " %)  " + Utils.doubleToString(i + 1, fieldWidth, 0) + " " + this.m_trainInstances.attribute(i).name() + "\n");
/*  251:     */         }
/*  252:     */       }
/*  253:     */     }
/*  254: 456 */     return CvString.toString();
/*  255:     */   }
/*  256:     */   
/*  257:     */   public void selectAttributesCVSplit(Instances split)
/*  258:     */     throws Exception
/*  259:     */   {
/*  260: 471 */     this.m_ASEvaluator.buildEvaluator(split);
/*  261:     */     
/*  262: 473 */     int[] attributeSet = this.m_searchMethod.search(this.m_ASEvaluator, split);
/*  263:     */     
/*  264:     */ 
/*  265: 476 */     attributeSet = this.m_ASEvaluator.postProcess(attributeSet);
/*  266: 477 */     updateStatsForModelCVSplit(split, this.m_ASEvaluator, this.m_searchMethod, attributeSet, this.m_doRank);
/*  267:     */   }
/*  268:     */   
/*  269:     */   public void updateStatsForModelCVSplit(Instances split, ASEvaluation evaluator, ASSearch search, int[] attributeSet, boolean doRank)
/*  270:     */     throws Exception
/*  271:     */   {
/*  272: 495 */     double[][] attributeRanking = (double[][])null;
/*  273: 502 */     if (this.m_trainInstances == null) {
/*  274: 503 */       this.m_trainInstances = split;
/*  275:     */     }
/*  276: 507 */     if ((this.m_rankResults == null) && (this.m_subsetResults == null))
/*  277:     */     {
/*  278: 508 */       this.m_subsetResults = new double[split.numAttributes()];
/*  279: 509 */       this.m_rankResults = new double[4][split.numAttributes()];
/*  280:     */     }
/*  281: 512 */     if (((search instanceof RankedOutputSearch)) && (doRank))
/*  282:     */     {
/*  283: 513 */       attributeRanking = ((RankedOutputSearch)search).rankedAttributes();
/*  284: 515 */       for (int j = 0; j < attributeRanking.length; j++)
/*  285:     */       {
/*  286: 517 */         this.m_rankResults[0][((int)attributeRanking[j][0])] += attributeRanking[j][1];
/*  287:     */         
/*  288:     */ 
/*  289: 520 */         this.m_rankResults[2][((int)attributeRanking[j][0])] += attributeRanking[j][1] * attributeRanking[j][1];
/*  290:     */         
/*  291:     */ 
/*  292: 523 */         this.m_rankResults[1][((int)attributeRanking[j][0])] += j + 1;
/*  293:     */         
/*  294: 525 */         this.m_rankResults[3][((int)attributeRanking[j][0])] += (j + 1) * (j + 1);
/*  295:     */       }
/*  296:     */     }
/*  297:     */     else
/*  298:     */     {
/*  299: 529 */       for (int j = 0; j < attributeSet.length; j++) {
/*  300: 530 */         this.m_subsetResults[attributeSet[j]] += 1.0D;
/*  301:     */       }
/*  302:     */     }
/*  303:     */   }
/*  304:     */   
/*  305:     */   public String CrossValidateAttributes()
/*  306:     */     throws Exception
/*  307:     */   {
/*  308: 545 */     Instances cvData = new Instances(this.m_trainInstances);
/*  309:     */     
/*  310:     */ 
/*  311: 548 */     Random random = new Random(this.m_seed);
/*  312: 549 */     cvData.randomize(random);
/*  313: 551 */     if ((!(this.m_ASEvaluator instanceof UnsupervisedSubsetEvaluator)) && (!(this.m_ASEvaluator instanceof UnsupervisedAttributeEvaluator))) {
/*  314: 553 */       if (cvData.classAttribute().isNominal()) {
/*  315: 554 */         cvData.stratify(this.m_numFolds);
/*  316:     */       }
/*  317:     */     }
/*  318: 559 */     for (int i = 0; i < this.m_numFolds; i++)
/*  319:     */     {
/*  320: 561 */       Instances train = cvData.trainCV(this.m_numFolds, i, random);
/*  321: 562 */       selectAttributesCVSplit(train);
/*  322:     */     }
/*  323: 565 */     return CVResultsString();
/*  324:     */   }
/*  325:     */   
/*  326:     */   public void SelectAttributes(Instances data)
/*  327:     */     throws Exception
/*  328:     */   {
/*  329: 577 */     this.m_transformer = null;
/*  330: 578 */     this.m_attributeFilter = null;
/*  331: 579 */     this.m_trainInstances = data;
/*  332: 581 */     if ((this.m_doXval == true) && ((this.m_ASEvaluator instanceof AttributeTransformer))) {
/*  333: 582 */       throw new Exception("Can't cross validate an attribute transformer.");
/*  334:     */     }
/*  335: 585 */     if (((this.m_ASEvaluator instanceof SubsetEvaluator)) && ((this.m_searchMethod instanceof Ranker))) {
/*  336: 587 */       throw new Exception(this.m_ASEvaluator.getClass().getName() + " must use a search method other than Ranker");
/*  337:     */     }
/*  338: 591 */     if (((this.m_ASEvaluator instanceof AttributeEvaluator)) && (!(this.m_searchMethod instanceof Ranker))) {
/*  339: 596 */       throw new Exception("AttributeEvaluators must use the Ranker search method");
/*  340:     */     }
/*  341: 600 */     if ((this.m_searchMethod instanceof RankedOutputSearch)) {
/*  342: 601 */       this.m_doRank = ((RankedOutputSearch)this.m_searchMethod).getGenerateRanking();
/*  343:     */     }
/*  344: 604 */     if ((!(this.m_ASEvaluator instanceof UnsupervisedAttributeEvaluator)) && (!(this.m_ASEvaluator instanceof UnsupervisedSubsetEvaluator))) {
/*  345: 610 */       if (this.m_trainInstances.classIndex() < 0) {
/*  346: 611 */         this.m_trainInstances.setClassIndex(this.m_trainInstances.numAttributes() - 1);
/*  347:     */       }
/*  348:     */     }
/*  349: 616 */     this.m_ASEvaluator.buildEvaluator(this.m_trainInstances);
/*  350: 617 */     if ((this.m_ASEvaluator instanceof AttributeTransformer))
/*  351:     */     {
/*  352: 618 */       this.m_trainInstances = ((AttributeTransformer)this.m_ASEvaluator).transformedHeader();
/*  353:     */       
/*  354: 620 */       this.m_transformer = ((AttributeTransformer)this.m_ASEvaluator);
/*  355:     */     }
/*  356: 622 */     int fieldWidth = (int)(Math.log(this.m_trainInstances.numAttributes()) + 1.0D);
/*  357:     */     
/*  358:     */ 
/*  359: 625 */     int[] attributeSet = this.m_searchMethod.search(this.m_ASEvaluator, this.m_trainInstances);
/*  360:     */     try
/*  361:     */     {
/*  362: 631 */       BeanInfo bi = Introspector.getBeanInfo(this.m_searchMethod.getClass());
/*  363:     */       
/*  364:     */ 
/*  365: 634 */       PropertyDescriptor[] properties = bi.getPropertyDescriptors();
/*  366: 635 */       for (PropertyDescriptor propertie : properties)
/*  367:     */       {
/*  368: 636 */         propertie.getDisplayName();
/*  369: 637 */         Method meth = propertie.getReadMethod();
/*  370: 638 */         Object retType = meth.getReturnType();
/*  371: 639 */         if (retType.equals(ASEvaluation.class))
/*  372:     */         {
/*  373: 640 */           Class<?>[] args = new Class[0];
/*  374: 641 */           ASEvaluation tempEval = (ASEvaluation)meth.invoke(this.m_searchMethod, (Object[])args);
/*  375: 643 */           if ((tempEval instanceof AttributeTransformer))
/*  376:     */           {
/*  377: 645 */             this.m_trainInstances = ((AttributeTransformer)tempEval).transformedHeader();
/*  378:     */             
/*  379: 647 */             this.m_transformer = ((AttributeTransformer)tempEval);
/*  380:     */           }
/*  381:     */         }
/*  382:     */       }
/*  383:     */     }
/*  384:     */     catch (IntrospectionException ex)
/*  385:     */     {
/*  386: 652 */       System.err.println("AttributeSelection: Couldn't introspect");
/*  387:     */     }
/*  388: 656 */     attributeSet = this.m_ASEvaluator.postProcess(attributeSet);
/*  389: 657 */     if (!this.m_doRank) {
/*  390: 658 */       this.m_selectionResults.append(printSelectionResults());
/*  391:     */     }
/*  392: 661 */     if (((this.m_searchMethod instanceof RankedOutputSearch)) && (this.m_doRank == true))
/*  393:     */     {
/*  394:     */       try
/*  395:     */       {
/*  396: 663 */         this.m_attributeRanking = ((RankedOutputSearch)this.m_searchMethod).rankedAttributes();
/*  397:     */       }
/*  398:     */       catch (Exception ex)
/*  399:     */       {
/*  400: 666 */         ex.printStackTrace();
/*  401: 667 */         throw ex;
/*  402:     */       }
/*  403: 669 */       this.m_selectionResults.append(printSelectionResults());
/*  404: 670 */       this.m_selectionResults.append("Ranked attributes:\n");
/*  405:     */       
/*  406:     */ 
/*  407: 673 */       this.m_numToSelect = ((RankedOutputSearch)this.m_searchMethod).getCalculatedNumToSelect();
/*  408:     */       
/*  409:     */ 
/*  410:     */ 
/*  411: 677 */       int f_p = 0;
/*  412: 678 */       int w_p = 0;
/*  413: 680 */       for (int i = 0; i < this.m_numToSelect; i++)
/*  414:     */       {
/*  415: 681 */         double precision = Math.abs(this.m_attributeRanking[i][1]) - (int)Math.abs(this.m_attributeRanking[i][1]);
/*  416:     */         
/*  417:     */ 
/*  418: 684 */         double intPart = (int)Math.abs(this.m_attributeRanking[i][1]);
/*  419: 686 */         if (precision > 0.0D) {
/*  420: 687 */           precision = Math.abs(Math.log(Math.abs(precision)) / Math.log(10.0D)) + 3.0D;
/*  421:     */         }
/*  422: 690 */         if (precision > f_p) {
/*  423: 691 */           f_p = (int)precision;
/*  424:     */         }
/*  425: 694 */         if (intPart == 0.0D)
/*  426:     */         {
/*  427: 695 */           if (w_p < 2) {
/*  428: 696 */             w_p = 2;
/*  429:     */           }
/*  430:     */         }
/*  431: 698 */         else if (Math.abs(Math.log(Math.abs(this.m_attributeRanking[i][1])) / Math.log(10.0D)) + 1.0D > w_p) {
/*  432: 700 */           if (this.m_attributeRanking[i][1] > 0.0D) {
/*  433: 701 */             w_p = (int)Math.abs(Math.log(Math.abs(this.m_attributeRanking[i][1])) / Math.log(10.0D)) + 1;
/*  434:     */           }
/*  435:     */         }
/*  436:     */       }
/*  437: 709 */       for (int i = 0; i < this.m_numToSelect; i++) {
/*  438: 710 */         this.m_selectionResults.append(Utils.doubleToString(this.m_attributeRanking[i][1], f_p + w_p + 1, f_p) + Utils.doubleToString(this.m_attributeRanking[i][0] + 1.0D, fieldWidth + 1, 0) + " " + this.m_trainInstances.attribute((int)this.m_attributeRanking[i][0]).name() + "\n");
/*  439:     */       }
/*  440: 721 */       if (this.m_trainInstances.classIndex() >= 0)
/*  441:     */       {
/*  442: 722 */         if (((!(this.m_ASEvaluator instanceof UnsupervisedSubsetEvaluator)) && (!(this.m_ASEvaluator instanceof UnsupervisedAttributeEvaluator))) || ((this.m_ASEvaluator instanceof AttributeTransformer)))
/*  443:     */         {
/*  444: 725 */           this.m_selectedAttributeSet = new int[this.m_numToSelect + 1];
/*  445: 726 */           this.m_selectedAttributeSet[this.m_numToSelect] = this.m_trainInstances.classIndex();
/*  446:     */         }
/*  447:     */         else
/*  448:     */         {
/*  449: 728 */           this.m_selectedAttributeSet = new int[this.m_numToSelect];
/*  450:     */         }
/*  451:     */       }
/*  452:     */       else {
/*  453: 731 */         this.m_selectedAttributeSet = new int[this.m_numToSelect];
/*  454:     */       }
/*  455: 734 */       this.m_selectionResults.append("\nSelected attributes: ");
/*  456: 736 */       for (int i = 0; i < this.m_numToSelect; i++)
/*  457:     */       {
/*  458: 737 */         this.m_selectedAttributeSet[i] = ((int)this.m_attributeRanking[i][0]);
/*  459: 739 */         if (i == this.m_numToSelect - 1)
/*  460:     */         {
/*  461: 740 */           this.m_selectionResults.append((int)this.m_attributeRanking[i][0] + 1 + " : " + (i + 1) + "\n");
/*  462:     */         }
/*  463:     */         else
/*  464:     */         {
/*  465: 743 */           this.m_selectionResults.append((int)this.m_attributeRanking[i][0] + 1);
/*  466: 744 */           this.m_selectionResults.append(",");
/*  467:     */         }
/*  468:     */       }
/*  469:     */     }
/*  470:     */     else
/*  471:     */     {
/*  472: 750 */       if (((!(this.m_ASEvaluator instanceof UnsupervisedSubsetEvaluator)) && (!(this.m_ASEvaluator instanceof UnsupervisedAttributeEvaluator))) || (this.m_trainInstances.classIndex() >= 0))
/*  473:     */       {
/*  474: 754 */         this.m_selectedAttributeSet = new int[attributeSet.length + 1];
/*  475: 755 */         this.m_selectedAttributeSet[attributeSet.length] = this.m_trainInstances.classIndex();
/*  476:     */       }
/*  477:     */       else
/*  478:     */       {
/*  479: 758 */         this.m_selectedAttributeSet = new int[attributeSet.length];
/*  480:     */       }
/*  481: 761 */       for (int i = 0; i < attributeSet.length; i++) {
/*  482: 762 */         this.m_selectedAttributeSet[i] = attributeSet[i];
/*  483:     */       }
/*  484: 765 */       this.m_selectionResults.append("Selected attributes: ");
/*  485: 767 */       for (int i = 0; i < attributeSet.length; i++) {
/*  486: 768 */         if (i == attributeSet.length - 1) {
/*  487: 769 */           this.m_selectionResults.append(attributeSet[i] + 1 + " : " + attributeSet.length + "\n");
/*  488:     */         } else {
/*  489: 772 */           this.m_selectionResults.append(attributeSet[i] + 1 + ",");
/*  490:     */         }
/*  491:     */       }
/*  492: 776 */       for (int element : attributeSet) {
/*  493: 777 */         this.m_selectionResults.append("                     " + this.m_trainInstances.attribute(element).name() + "\n");
/*  494:     */       }
/*  495:     */     }
/*  496: 783 */     if (this.m_doXval == true) {
/*  497: 784 */       this.m_selectionResults.append(CrossValidateAttributes());
/*  498:     */     }
/*  499: 788 */     if ((this.m_selectedAttributeSet != null) && (!this.m_doXval))
/*  500:     */     {
/*  501: 789 */       this.m_attributeFilter = new Remove();
/*  502: 790 */       this.m_attributeFilter.setAttributeIndicesArray(this.m_selectedAttributeSet);
/*  503: 791 */       this.m_attributeFilter.setInvertSelection(true);
/*  504: 792 */       this.m_attributeFilter.setInputFormat(this.m_trainInstances);
/*  505:     */     }
/*  506: 796 */     this.m_trainInstances = new Instances(this.m_trainInstances, 0);
/*  507: 797 */     this.m_ASEvaluator.clean();
/*  508:     */   }
/*  509:     */   
/*  510:     */   public static String SelectAttributes(ASEvaluation ASEvaluator, String[] options, Instances train)
/*  511:     */     throws Exception
/*  512:     */   {
/*  513: 814 */     int seed = 1;int folds = 10;
/*  514:     */     
/*  515:     */ 
/*  516:     */ 
/*  517: 818 */     String[] searchOptions = null;
/*  518: 819 */     ASSearch searchMethod = null;
/*  519: 820 */     boolean doCrossVal = false;
/*  520: 821 */     int classIndex = -1;
/*  521: 822 */     boolean helpRequested = false;
/*  522: 823 */     AttributeSelection trainSelector = new AttributeSelection();
/*  523:     */     try
/*  524:     */     {
/*  525: 826 */       if (Utils.getFlag('h', options)) {
/*  526: 827 */         helpRequested = true;
/*  527:     */       }
/*  528: 831 */       if (train.classIndex() != -1) {
/*  529: 832 */         classIndex = train.classIndex() + 1;
/*  530:     */       }
/*  531: 836 */       String classString = Utils.getOption('c', options);
/*  532: 838 */       if (classString.length() != 0) {
/*  533: 839 */         if (classString.equals("first")) {
/*  534: 840 */           classIndex = 1;
/*  535: 841 */         } else if (classString.equals("last")) {
/*  536: 842 */           classIndex = train.numAttributes();
/*  537:     */         } else {
/*  538: 844 */           classIndex = Integer.parseInt(classString);
/*  539:     */         }
/*  540:     */       }
/*  541: 848 */       if ((classIndex != -1) && ((classIndex == 0) || (classIndex > train.numAttributes()))) {
/*  542: 850 */         throw new Exception("Class index out of range.");
/*  543:     */       }
/*  544: 853 */       if (classIndex != -1) {
/*  545: 854 */         train.setClassIndex(classIndex - 1);
/*  546:     */       }
/*  547: 860 */       String foldsString = Utils.getOption('x', options);
/*  548: 862 */       if (foldsString.length() != 0)
/*  549:     */       {
/*  550: 863 */         folds = Integer.parseInt(foldsString);
/*  551: 864 */         doCrossVal = true;
/*  552:     */       }
/*  553: 867 */       trainSelector.setFolds(folds);
/*  554: 868 */       trainSelector.setXval(doCrossVal);
/*  555:     */       
/*  556: 870 */       String seedString = Utils.getOption('n', options);
/*  557: 872 */       if (seedString.length() != 0) {
/*  558: 873 */         seed = Integer.parseInt(seedString);
/*  559:     */       }
/*  560: 876 */       trainSelector.setSeed(seed);
/*  561:     */       
/*  562: 878 */       String searchName = Utils.getOption('s', options);
/*  563: 880 */       if ((searchName.length() == 0) && (!(ASEvaluator instanceof AttributeEvaluator))) {
/*  564: 882 */         throw new Exception("No search method given.");
/*  565:     */       }
/*  566:     */       String searchClassName;
/*  567: 885 */       if (searchName.length() != 0)
/*  568:     */       {
/*  569: 886 */         searchName = searchName.trim();
/*  570:     */         
/*  571: 888 */         int breakLoc = searchName.indexOf(' ');
/*  572: 889 */         String searchClassName = searchName;
/*  573: 890 */         String searchOptionsString = "";
/*  574: 892 */         if (breakLoc != -1)
/*  575:     */         {
/*  576: 893 */           searchClassName = searchName.substring(0, breakLoc);
/*  577: 894 */           searchOptionsString = searchName.substring(breakLoc).trim();
/*  578: 895 */           searchOptions = Utils.splitOptions(searchOptionsString);
/*  579:     */         }
/*  580:     */       }
/*  581:     */       else
/*  582:     */       {
/*  583:     */         try
/*  584:     */         {
/*  585: 899 */           searchClassName = new String("weka.attributeSelection.Ranker");
/*  586: 900 */           searchMethod = (ASSearch)Class.forName(searchClassName).newInstance();
/*  587:     */         }
/*  588:     */         catch (Exception e)
/*  589:     */         {
/*  590: 903 */           throw new Exception("Can't create Ranker object");
/*  591:     */         }
/*  592:     */       }
/*  593: 909 */       if (searchMethod == null) {
/*  594: 910 */         searchMethod = ASSearch.forName(searchClassName, searchOptions);
/*  595:     */       }
/*  596: 914 */       trainSelector.setSearch(searchMethod);
/*  597:     */     }
/*  598:     */     catch (Exception e)
/*  599:     */     {
/*  600: 916 */       throw new Exception('\n' + e.getMessage() + makeOptionString(ASEvaluator, searchMethod));
/*  601:     */     }
/*  602:     */     try
/*  603:     */     {
/*  604: 922 */       if ((ASEvaluator instanceof OptionHandler)) {
/*  605: 923 */         ((OptionHandler)ASEvaluator).setOptions(options);
/*  606:     */       }
/*  607:     */     }
/*  608:     */     catch (Exception e)
/*  609:     */     {
/*  610: 933 */       throw new Exception("\n" + e.getMessage() + makeOptionString(ASEvaluator, searchMethod));
/*  611:     */     }
/*  612:     */     try
/*  613:     */     {
/*  614: 938 */       Utils.checkForRemainingOptions(options);
/*  615:     */     }
/*  616:     */     catch (Exception e)
/*  617:     */     {
/*  618: 940 */       throw new Exception('\n' + e.getMessage() + makeOptionString(ASEvaluator, searchMethod));
/*  619:     */     }
/*  620: 944 */     if (helpRequested)
/*  621:     */     {
/*  622: 945 */       System.out.println(makeOptionString(ASEvaluator, searchMethod));
/*  623: 946 */       System.exit(0);
/*  624:     */     }
/*  625: 950 */     trainSelector.setEvaluator(ASEvaluator);
/*  626:     */     
/*  627:     */ 
/*  628: 953 */     trainSelector.SelectAttributes(train);
/*  629:     */     
/*  630:     */ 
/*  631: 956 */     return trainSelector.toResultsString();
/*  632:     */   }
/*  633:     */   
/*  634:     */   private String printSelectionResults()
/*  635:     */   {
/*  636: 965 */     StringBuffer text = new StringBuffer();
/*  637: 966 */     text.append("\n\n=== Attribute Selection on all input data ===\n\nSearch Method:\n");
/*  638:     */     
/*  639: 968 */     text.append(this.m_searchMethod.toString());
/*  640: 969 */     text.append("\nAttribute ");
/*  641: 971 */     if ((this.m_ASEvaluator instanceof SubsetEvaluator)) {
/*  642: 972 */       text.append("Subset Evaluator (");
/*  643:     */     } else {
/*  644: 974 */       text.append("Evaluator (");
/*  645:     */     }
/*  646: 977 */     if ((!(this.m_ASEvaluator instanceof UnsupervisedSubsetEvaluator)) && (!(this.m_ASEvaluator instanceof UnsupervisedAttributeEvaluator)))
/*  647:     */     {
/*  648: 979 */       text.append("supervised, ");
/*  649: 980 */       text.append("Class (");
/*  650: 982 */       if (this.m_trainInstances.attribute(this.m_trainInstances.classIndex()).isNumeric()) {
/*  651: 983 */         text.append("numeric): ");
/*  652:     */       } else {
/*  653: 985 */         text.append("nominal): ");
/*  654:     */       }
/*  655: 988 */       text.append(this.m_trainInstances.classIndex() + 1 + " " + this.m_trainInstances.attribute(this.m_trainInstances.classIndex()).name() + "):\n");
/*  656:     */     }
/*  657:     */     else
/*  658:     */     {
/*  659: 992 */       text.append("unsupervised):\n");
/*  660:     */     }
/*  661: 995 */     text.append(this.m_ASEvaluator.toString() + "\n");
/*  662: 996 */     return text.toString();
/*  663:     */   }
/*  664:     */   
/*  665:     */   private static String makeOptionString(ASEvaluation ASEvaluator, ASSearch searchMethod)
/*  666:     */     throws Exception
/*  667:     */   {
/*  668:1010 */     StringBuffer optionsText = new StringBuffer("");
/*  669:     */     
/*  670:1012 */     optionsText.append("\n\nGeneral options:\n\n");
/*  671:1013 */     optionsText.append("-h\n\tdisplay this help\n");
/*  672:1014 */     optionsText.append("-i <name of input file>\n");
/*  673:1015 */     optionsText.append("\tSets training file.\n");
/*  674:1016 */     optionsText.append("-c <class index>\n");
/*  675:1017 */     optionsText.append("\tSets the class index for supervised attribute\n");
/*  676:1018 */     optionsText.append("\tselection. Default=last column.\n");
/*  677:1019 */     optionsText.append("-s <class name>\n");
/*  678:1020 */     optionsText.append("\tSets search method for subset evaluators.\n");
/*  679:1021 */     optionsText.append("-x <number of folds>\n");
/*  680:1022 */     optionsText.append("\tPerform a cross validation.\n");
/*  681:1023 */     optionsText.append("-n <random number seed>\n");
/*  682:1024 */     optionsText.append("\tUse in conjunction with -x.\n");
/*  683:1027 */     if ((ASEvaluator instanceof OptionHandler))
/*  684:     */     {
/*  685:1028 */       optionsText.append("\nOptions specific to " + ASEvaluator.getClass().getName() + ":\n\n");
/*  686:     */       
/*  687:1030 */       Enumeration<Option> enu = ((OptionHandler)ASEvaluator).listOptions();
/*  688:1032 */       while (enu.hasMoreElements())
/*  689:     */       {
/*  690:1033 */         Option option = (Option)enu.nextElement();
/*  691:1034 */         optionsText.append(option.synopsis() + '\n');
/*  692:1035 */         optionsText.append(option.description() + "\n");
/*  693:     */       }
/*  694:     */     }
/*  695:1039 */     if (searchMethod != null)
/*  696:     */     {
/*  697:1040 */       if ((searchMethod instanceof OptionHandler))
/*  698:     */       {
/*  699:1041 */         optionsText.append("\nOptions specific to " + searchMethod.getClass().getName() + ":\n\n");
/*  700:     */         
/*  701:1043 */         Enumeration<Option> enu = ((OptionHandler)searchMethod).listOptions();
/*  702:1045 */         while (enu.hasMoreElements())
/*  703:     */         {
/*  704:1046 */           Option option = (Option)enu.nextElement();
/*  705:1047 */           optionsText.append(option.synopsis() + '\n');
/*  706:1048 */           optionsText.append(option.description() + "\n");
/*  707:     */         }
/*  708:     */       }
/*  709:     */     }
/*  710:1052 */     else if ((ASEvaluator instanceof SubsetEvaluator)) {
/*  711:1053 */       System.out.println("No search method given.");
/*  712:     */     }
/*  713:1057 */     return optionsText.toString();
/*  714:     */   }
/*  715:     */   
/*  716:     */   public static void main(String[] args)
/*  717:     */   {
/*  718:     */     try
/*  719:     */     {
/*  720:1067 */       if (args.length == 0) {
/*  721:1068 */         throw new Exception("The first argument must be the name of an attribute/subset evaluator");
/*  722:     */       }
/*  723:1072 */       String EvaluatorName = args[0];
/*  724:1073 */       args[0] = "";
/*  725:1074 */       ASEvaluation newEval = ASEvaluation.forName(EvaluatorName, null);
/*  726:1075 */       System.out.println(SelectAttributes(newEval, args));
/*  727:     */     }
/*  728:     */     catch (Exception e)
/*  729:     */     {
/*  730:1077 */       System.out.println(e.getMessage());
/*  731:     */     }
/*  732:     */   }
/*  733:     */   
/*  734:     */   public String getRevision()
/*  735:     */   {
/*  736:1088 */     return RevisionUtils.extract("$Revision: 11942 $");
/*  737:     */   }
/*  738:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.attributeSelection.AttributeSelection
 * JD-Core Version:    0.7.0.1
 */