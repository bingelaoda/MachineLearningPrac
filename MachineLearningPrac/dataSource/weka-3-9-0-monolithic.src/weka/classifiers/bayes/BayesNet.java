/*    1:     */ package weka.classifiers.bayes;
/*    2:     */ 
/*    3:     */ import java.util.Collections;
/*    4:     */ import java.util.Enumeration;
/*    5:     */ import java.util.Vector;
/*    6:     */ import weka.classifiers.AbstractClassifier;
/*    7:     */ import weka.classifiers.bayes.net.ADNode;
/*    8:     */ import weka.classifiers.bayes.net.BIFReader;
/*    9:     */ import weka.classifiers.bayes.net.ParentSet;
/*   10:     */ import weka.classifiers.bayes.net.estimate.BayesNetEstimator;
/*   11:     */ import weka.classifiers.bayes.net.estimate.DiscreteEstimatorBayes;
/*   12:     */ import weka.classifiers.bayes.net.estimate.SimpleEstimator;
/*   13:     */ import weka.classifiers.bayes.net.search.SearchAlgorithm;
/*   14:     */ import weka.classifiers.bayes.net.search.local.K2;
/*   15:     */ import weka.classifiers.bayes.net.search.local.LocalScoreSearchAlgorithm;
/*   16:     */ import weka.core.AdditionalMeasureProducer;
/*   17:     */ import weka.core.Attribute;
/*   18:     */ import weka.core.Capabilities;
/*   19:     */ import weka.core.Capabilities.Capability;
/*   20:     */ import weka.core.Drawable;
/*   21:     */ import weka.core.Instance;
/*   22:     */ import weka.core.Instances;
/*   23:     */ import weka.core.Option;
/*   24:     */ import weka.core.OptionHandler;
/*   25:     */ import weka.core.RevisionUtils;
/*   26:     */ import weka.core.Utils;
/*   27:     */ import weka.core.WeightedInstancesHandler;
/*   28:     */ import weka.estimators.Estimator;
/*   29:     */ import weka.filters.Filter;
/*   30:     */ import weka.filters.supervised.attribute.Discretize;
/*   31:     */ import weka.filters.unsupervised.attribute.ReplaceMissingValues;
/*   32:     */ 
/*   33:     */ public class BayesNet
/*   34:     */   extends AbstractClassifier
/*   35:     */   implements OptionHandler, WeightedInstancesHandler, Drawable, AdditionalMeasureProducer
/*   36:     */ {
/*   37:     */   static final long serialVersionUID = 746037443258775954L;
/*   38:     */   protected ParentSet[] m_ParentSets;
/*   39:     */   public Estimator[][] m_Distributions;
/*   40: 114 */   protected Discretize m_DiscretizeFilter = null;
/*   41: 117 */   int m_nNonDiscreteAttribute = -1;
/*   42: 120 */   protected ReplaceMissingValues m_MissingValuesFilter = null;
/*   43:     */   protected int m_NumClasses;
/*   44:     */   public Instances m_Instances;
/*   45:     */   private int m_NumInstances;
/*   46:     */   ADNode m_ADTree;
/*   47: 147 */   protected BIFReader m_otherBayesNet = null;
/*   48: 153 */   boolean m_bUseADTree = false;
/*   49: 158 */   SearchAlgorithm m_SearchAlgorithm = new K2();
/*   50: 163 */   BayesNetEstimator m_BayesNetEstimator = new SimpleEstimator();
/*   51:     */   
/*   52:     */   public Capabilities getCapabilities()
/*   53:     */   {
/*   54: 172 */     Capabilities result = super.getCapabilities();
/*   55: 173 */     result.disableAll();
/*   56:     */     
/*   57:     */ 
/*   58: 176 */     result.enable(Capabilities.Capability.NOMINAL_ATTRIBUTES);
/*   59: 177 */     result.enable(Capabilities.Capability.NUMERIC_ATTRIBUTES);
/*   60: 178 */     result.enable(Capabilities.Capability.MISSING_VALUES);
/*   61:     */     
/*   62:     */ 
/*   63: 181 */     result.enable(Capabilities.Capability.NOMINAL_CLASS);
/*   64: 182 */     result.enable(Capabilities.Capability.MISSING_CLASS_VALUES);
/*   65:     */     
/*   66:     */ 
/*   67: 185 */     result.setMinimumNumberInstances(0);
/*   68:     */     
/*   69: 187 */     return result;
/*   70:     */   }
/*   71:     */   
/*   72:     */   public void buildClassifier(Instances instances)
/*   73:     */     throws Exception
/*   74:     */   {
/*   75: 200 */     getCapabilities().testWithFail(instances);
/*   76:     */     
/*   77:     */ 
/*   78: 203 */     instances = new Instances(instances);
/*   79: 204 */     instances.deleteWithMissingClass();
/*   80:     */     
/*   81:     */ 
/*   82:     */ 
/*   83: 208 */     instances = normalizeDataSet(instances);
/*   84:     */     
/*   85:     */ 
/*   86: 211 */     this.m_Instances = new Instances(instances);
/*   87: 212 */     this.m_NumInstances = this.m_Instances.numInstances();
/*   88:     */     
/*   89:     */ 
/*   90: 215 */     this.m_NumClasses = instances.numClasses();
/*   91: 218 */     if (this.m_bUseADTree) {
/*   92: 219 */       this.m_ADTree = ADNode.makeADTree(instances);
/*   93:     */     }
/*   94: 224 */     initStructure();
/*   95:     */     
/*   96:     */ 
/*   97: 227 */     buildStructure();
/*   98:     */     
/*   99:     */ 
/*  100: 230 */     estimateCPTs();
/*  101:     */     
/*  102:     */ 
/*  103: 233 */     this.m_Instances = new Instances(this.m_Instances, 0);
/*  104: 234 */     this.m_ADTree = null;
/*  105:     */   }
/*  106:     */   
/*  107:     */   public int getNumInstances()
/*  108:     */   {
/*  109: 242 */     return this.m_NumInstances;
/*  110:     */   }
/*  111:     */   
/*  112:     */   protected Instances normalizeDataSet(Instances instances)
/*  113:     */     throws Exception
/*  114:     */   {
/*  115: 255 */     this.m_nNonDiscreteAttribute = -1;
/*  116: 256 */     Enumeration<Attribute> enu = instances.enumerateAttributes();
/*  117: 257 */     while (enu.hasMoreElements())
/*  118:     */     {
/*  119: 258 */       Attribute attribute = (Attribute)enu.nextElement();
/*  120: 259 */       if (attribute.type() != 1) {
/*  121: 260 */         this.m_nNonDiscreteAttribute = attribute.index();
/*  122:     */       }
/*  123:     */     }
/*  124: 264 */     if ((this.m_nNonDiscreteAttribute > -1) && (instances.attribute(this.m_nNonDiscreteAttribute).type() != 1))
/*  125:     */     {
/*  126: 266 */       this.m_DiscretizeFilter = new Discretize();
/*  127: 267 */       this.m_DiscretizeFilter.setInputFormat(instances);
/*  128: 268 */       instances = Filter.useFilter(instances, this.m_DiscretizeFilter);
/*  129:     */     }
/*  130: 271 */     this.m_MissingValuesFilter = new ReplaceMissingValues();
/*  131: 272 */     this.m_MissingValuesFilter.setInputFormat(instances);
/*  132: 273 */     instances = Filter.useFilter(instances, this.m_MissingValuesFilter);
/*  133:     */     
/*  134: 275 */     return instances;
/*  135:     */   }
/*  136:     */   
/*  137:     */   protected Instance normalizeInstance(Instance instance)
/*  138:     */     throws Exception
/*  139:     */   {
/*  140: 287 */     if ((this.m_nNonDiscreteAttribute > -1) && (instance.attribute(this.m_nNonDiscreteAttribute).type() != 1))
/*  141:     */     {
/*  142: 289 */       this.m_DiscretizeFilter.input(instance);
/*  143: 290 */       instance = this.m_DiscretizeFilter.output();
/*  144:     */     }
/*  145: 293 */     this.m_MissingValuesFilter.input(instance);
/*  146: 294 */     instance = this.m_MissingValuesFilter.output();
/*  147:     */     
/*  148: 296 */     return instance;
/*  149:     */   }
/*  150:     */   
/*  151:     */   public void initStructure()
/*  152:     */     throws Exception
/*  153:     */   {
/*  154: 311 */     int nAttribute = 0;
/*  155: 313 */     for (int iOrder = 1; iOrder < this.m_Instances.numAttributes(); iOrder++) {
/*  156: 314 */       if (nAttribute == this.m_Instances.classIndex()) {
/*  157: 315 */         nAttribute++;
/*  158:     */       }
/*  159:     */     }
/*  160: 322 */     this.m_ParentSets = new ParentSet[this.m_Instances.numAttributes()];
/*  161: 324 */     for (int iAttribute = 0; iAttribute < this.m_Instances.numAttributes(); iAttribute++) {
/*  162: 325 */       this.m_ParentSets[iAttribute] = new ParentSet(this.m_Instances.numAttributes());
/*  163:     */     }
/*  164:     */   }
/*  165:     */   
/*  166:     */   public void buildStructure()
/*  167:     */     throws Exception
/*  168:     */   {
/*  169: 339 */     this.m_SearchAlgorithm.buildStructure(this, this.m_Instances);
/*  170:     */   }
/*  171:     */   
/*  172:     */   public void estimateCPTs()
/*  173:     */     throws Exception
/*  174:     */   {
/*  175: 349 */     this.m_BayesNetEstimator.estimateCPTs(this);
/*  176:     */   }
/*  177:     */   
/*  178:     */   public void initCPTs()
/*  179:     */     throws Exception
/*  180:     */   {
/*  181: 358 */     this.m_BayesNetEstimator.initCPTs(this);
/*  182:     */   }
/*  183:     */   
/*  184:     */   public void updateClassifier(Instance instance)
/*  185:     */     throws Exception
/*  186:     */   {
/*  187: 368 */     instance = normalizeInstance(instance);
/*  188: 369 */     this.m_BayesNetEstimator.updateClassifier(this, instance);
/*  189:     */   }
/*  190:     */   
/*  191:     */   public double[] distributionForInstance(Instance instance)
/*  192:     */     throws Exception
/*  193:     */   {
/*  194: 381 */     instance = normalizeInstance(instance);
/*  195: 382 */     return this.m_BayesNetEstimator.distributionForInstance(this, instance);
/*  196:     */   }
/*  197:     */   
/*  198:     */   public double[] countsForInstance(Instance instance)
/*  199:     */     throws Exception
/*  200:     */   {
/*  201: 394 */     double[] fCounts = new double[this.m_NumClasses];
/*  202: 396 */     for (int iClass = 0; iClass < this.m_NumClasses; iClass++) {
/*  203: 397 */       fCounts[iClass] = 0.0D;
/*  204:     */     }
/*  205: 400 */     for (int iClass = 0; iClass < this.m_NumClasses; iClass++)
/*  206:     */     {
/*  207: 401 */       double fCount = 0.0D;
/*  208: 403 */       for (int iAttribute = 0; iAttribute < this.m_Instances.numAttributes(); iAttribute++)
/*  209:     */       {
/*  210: 404 */         double iCPT = 0.0D;
/*  211: 406 */         for (int iParent = 0; iParent < this.m_ParentSets[iAttribute].getNrOfParents(); iParent++)
/*  212:     */         {
/*  213: 408 */           int nParent = this.m_ParentSets[iAttribute].getParent(iParent);
/*  214: 410 */           if (nParent == this.m_Instances.classIndex()) {
/*  215: 411 */             iCPT = iCPT * this.m_NumClasses + iClass;
/*  216:     */           } else {
/*  217: 413 */             iCPT = iCPT * this.m_Instances.attribute(nParent).numValues() + instance.value(nParent);
/*  218:     */           }
/*  219:     */         }
/*  220: 418 */         if (iAttribute == this.m_Instances.classIndex()) {
/*  221: 419 */           fCount += ((DiscreteEstimatorBayes)this.m_Distributions[iAttribute][((int)iCPT)]).getCount(iClass);
/*  222:     */         } else {
/*  223: 422 */           fCount += ((DiscreteEstimatorBayes)this.m_Distributions[iAttribute][((int)iCPT)]).getCount(instance.value(iAttribute));
/*  224:     */         }
/*  225:     */       }
/*  226: 427 */       fCounts[iClass] += fCount;
/*  227:     */     }
/*  228: 429 */     return fCounts;
/*  229:     */   }
/*  230:     */   
/*  231:     */   public Enumeration<Option> listOptions()
/*  232:     */   {
/*  233: 439 */     Vector<Option> newVector = new Vector(4);
/*  234:     */     
/*  235: 441 */     newVector.addElement(new Option("\tDo not use ADTree data structure\n", "D", 0, "-D"));
/*  236:     */     
/*  237: 443 */     newVector.addElement(new Option("\tBIF file to compare with\n", "B", 1, "-B <BIF file>"));
/*  238:     */     
/*  239: 445 */     newVector.addElement(new Option("\tSearch algorithm\n", "Q", 1, "-Q weka.classifiers.bayes.net.search.SearchAlgorithm"));
/*  240:     */     
/*  241: 447 */     newVector.addElement(new Option("\tEstimator algorithm\n", "E", 1, "-E weka.classifiers.bayes.net.estimate.SimpleEstimator"));
/*  242:     */     
/*  243: 449 */     newVector.addAll(Collections.list(super.listOptions()));
/*  244:     */     
/*  245: 451 */     newVector.addElement(new Option("", "", 0, "\nOptions specific to search method " + getSearchAlgorithm().getClass().getName() + ":"));
/*  246:     */     
/*  247:     */ 
/*  248: 454 */     newVector.addAll(Collections.list(getSearchAlgorithm().listOptions()));
/*  249:     */     
/*  250: 456 */     newVector.addElement(new Option("", "", 0, "\nOptions specific to estimator method " + getEstimator().getClass().getName() + ":"));
/*  251:     */     
/*  252:     */ 
/*  253: 459 */     newVector.addAll(Collections.list(getEstimator().listOptions()));
/*  254:     */     
/*  255: 461 */     return newVector.elements();
/*  256:     */   }
/*  257:     */   
/*  258:     */   public void setOptions(String[] options)
/*  259:     */     throws Exception
/*  260:     */   {
/*  261: 498 */     super.setOptions(options);
/*  262: 499 */     this.m_bUseADTree = (!Utils.getFlag('D', options));
/*  263:     */     
/*  264: 501 */     String sBIFFile = Utils.getOption('B', options);
/*  265: 502 */     if ((sBIFFile != null) && (!sBIFFile.equals(""))) {
/*  266: 503 */       setBIFFile(sBIFFile);
/*  267:     */     }
/*  268: 506 */     String searchAlgorithmName = Utils.getOption('Q', options);
/*  269: 507 */     if (searchAlgorithmName.length() != 0) {
/*  270: 508 */       setSearchAlgorithm((SearchAlgorithm)Utils.forName(SearchAlgorithm.class, searchAlgorithmName, partitionOptions(options)));
/*  271:     */     } else {
/*  272: 511 */       setSearchAlgorithm(new K2());
/*  273:     */     }
/*  274: 514 */     String estimatorName = Utils.getOption('E', options);
/*  275: 515 */     if (estimatorName.length() != 0) {
/*  276: 516 */       setEstimator((BayesNetEstimator)Utils.forName(BayesNetEstimator.class, estimatorName, Utils.partitionOptions(options)));
/*  277:     */     } else {
/*  278: 519 */       setEstimator(new SimpleEstimator());
/*  279:     */     }
/*  280: 522 */     Utils.checkForRemainingOptions(options);
/*  281:     */   }
/*  282:     */   
/*  283:     */   public static String[] partitionOptions(String[] options)
/*  284:     */   {
/*  285: 536 */     for (int i = 0; i < options.length; i++) {
/*  286: 537 */       if (options[i].equals("--"))
/*  287:     */       {
/*  288: 539 */         int j = i;
/*  289: 540 */         while ((j < options.length) && (!options[j].equals("-E"))) {
/*  290: 541 */           j++;
/*  291:     */         }
/*  292: 546 */         options[(i++)] = "";
/*  293: 547 */         String[] result = new String[options.length - i];
/*  294: 548 */         j = i;
/*  295: 549 */         while ((j < options.length) && (!options[j].equals("-E")))
/*  296:     */         {
/*  297: 550 */           result[(j - i)] = options[j];
/*  298: 551 */           options[j] = "";
/*  299: 552 */           j++;
/*  300:     */         }
/*  301: 554 */         while (j < options.length)
/*  302:     */         {
/*  303: 555 */           result[(j - i)] = "";
/*  304: 556 */           j++;
/*  305:     */         }
/*  306: 558 */         return result;
/*  307:     */       }
/*  308:     */     }
/*  309: 561 */     return new String[0];
/*  310:     */   }
/*  311:     */   
/*  312:     */   public String[] getOptions()
/*  313:     */   {
/*  314: 571 */     Vector<String> options = new Vector();
/*  315:     */     
/*  316: 573 */     Collections.addAll(options, super.getOptions());
/*  317: 575 */     if (!this.m_bUseADTree) {
/*  318: 576 */       options.add("-D");
/*  319:     */     }
/*  320: 579 */     if (this.m_otherBayesNet != null)
/*  321:     */     {
/*  322: 580 */       options.add("-B");
/*  323: 581 */       options.add(this.m_otherBayesNet.getFileName());
/*  324:     */     }
/*  325: 584 */     options.add("-Q");
/*  326: 585 */     options.add("" + getSearchAlgorithm().getClass().getName());
/*  327: 586 */     options.add("--");
/*  328: 587 */     Collections.addAll(options, getSearchAlgorithm().getOptions());
/*  329:     */     
/*  330: 589 */     options.add("-E");
/*  331: 590 */     options.add("" + getEstimator().getClass().getName());
/*  332: 591 */     options.add("--");
/*  333: 592 */     Collections.addAll(options, getEstimator().getOptions());
/*  334:     */     
/*  335: 594 */     return (String[])options.toArray(new String[0]);
/*  336:     */   }
/*  337:     */   
/*  338:     */   public void setSearchAlgorithm(SearchAlgorithm newSearchAlgorithm)
/*  339:     */   {
/*  340: 603 */     this.m_SearchAlgorithm = newSearchAlgorithm;
/*  341:     */   }
/*  342:     */   
/*  343:     */   public SearchAlgorithm getSearchAlgorithm()
/*  344:     */   {
/*  345: 612 */     return this.m_SearchAlgorithm;
/*  346:     */   }
/*  347:     */   
/*  348:     */   public void setEstimator(BayesNetEstimator newBayesNetEstimator)
/*  349:     */   {
/*  350: 621 */     this.m_BayesNetEstimator = newBayesNetEstimator;
/*  351:     */   }
/*  352:     */   
/*  353:     */   public BayesNetEstimator getEstimator()
/*  354:     */   {
/*  355: 630 */     return this.m_BayesNetEstimator;
/*  356:     */   }
/*  357:     */   
/*  358:     */   public void setUseADTree(boolean bUseADTree)
/*  359:     */   {
/*  360: 639 */     this.m_bUseADTree = bUseADTree;
/*  361:     */   }
/*  362:     */   
/*  363:     */   public boolean getUseADTree()
/*  364:     */   {
/*  365: 648 */     return this.m_bUseADTree;
/*  366:     */   }
/*  367:     */   
/*  368:     */   public void setBIFFile(String sBIFFile)
/*  369:     */   {
/*  370:     */     try
/*  371:     */     {
/*  372: 658 */       this.m_otherBayesNet = new BIFReader().processFile(sBIFFile);
/*  373:     */     }
/*  374:     */     catch (Throwable t)
/*  375:     */     {
/*  376: 660 */       this.m_otherBayesNet = null;
/*  377:     */     }
/*  378:     */   }
/*  379:     */   
/*  380:     */   public String getBIFFile()
/*  381:     */   {
/*  382: 670 */     if (this.m_otherBayesNet != null) {
/*  383: 671 */       return this.m_otherBayesNet.getFileName();
/*  384:     */     }
/*  385: 673 */     return "";
/*  386:     */   }
/*  387:     */   
/*  388:     */   public String toString()
/*  389:     */   {
/*  390: 683 */     StringBuffer text = new StringBuffer();
/*  391:     */     
/*  392: 685 */     text.append("Bayes Network Classifier");
/*  393: 686 */     text.append("\n" + (this.m_bUseADTree ? "Using " : "not using ") + "ADTree");
/*  394: 688 */     if (this.m_Instances == null)
/*  395:     */     {
/*  396: 689 */       text.append(": No model built yet.");
/*  397:     */     }
/*  398:     */     else
/*  399:     */     {
/*  400: 693 */       text.append("\n#attributes=");
/*  401: 694 */       text.append(this.m_Instances.numAttributes());
/*  402: 695 */       text.append(" #classindex=");
/*  403: 696 */       text.append(this.m_Instances.classIndex());
/*  404: 697 */       text.append("\nNetwork structure (nodes followed by parents)\n");
/*  405: 699 */       for (int iAttribute = 0; iAttribute < this.m_Instances.numAttributes(); iAttribute++)
/*  406:     */       {
/*  407: 700 */         text.append(this.m_Instances.attribute(iAttribute).name() + "(" + this.m_Instances.attribute(iAttribute).numValues() + "): ");
/*  408: 703 */         for (int iParent = 0; iParent < this.m_ParentSets[iAttribute].getNrOfParents(); iParent++) {
/*  409: 705 */           text.append(this.m_Instances.attribute(this.m_ParentSets[iAttribute].getParent(iParent)).name() + " ");
/*  410:     */         }
/*  411: 710 */         text.append("\n");
/*  412:     */       }
/*  413: 722 */       text.append("LogScore Bayes: " + measureBayesScore() + "\n");
/*  414: 723 */       text.append("LogScore BDeu: " + measureBDeuScore() + "\n");
/*  415: 724 */       text.append("LogScore MDL: " + measureMDLScore() + "\n");
/*  416: 725 */       text.append("LogScore ENTROPY: " + measureEntropyScore() + "\n");
/*  417: 726 */       text.append("LogScore AIC: " + measureAICScore() + "\n");
/*  418: 728 */       if (this.m_otherBayesNet != null)
/*  419:     */       {
/*  420: 729 */         text.append("Missing: " + this.m_otherBayesNet.missingArcs(this) + " Extra: " + this.m_otherBayesNet.extraArcs(this) + " Reversed: " + this.m_otherBayesNet.reversedArcs(this) + "\n");
/*  421:     */         
/*  422:     */ 
/*  423: 732 */         text.append("Divergence: " + this.m_otherBayesNet.divergence(this) + "\n");
/*  424:     */       }
/*  425:     */     }
/*  426: 736 */     return text.toString();
/*  427:     */   }
/*  428:     */   
/*  429:     */   public int graphType()
/*  430:     */   {
/*  431: 746 */     return 2;
/*  432:     */   }
/*  433:     */   
/*  434:     */   public String graph()
/*  435:     */     throws Exception
/*  436:     */   {
/*  437: 757 */     return toXMLBIF03();
/*  438:     */   }
/*  439:     */   
/*  440:     */   public String getBIFHeader()
/*  441:     */   {
/*  442: 761 */     StringBuffer text = new StringBuffer();
/*  443: 762 */     text.append("<?xml version=\"1.0\"?>\n");
/*  444: 763 */     text.append("<!-- DTD for the XMLBIF 0.3 format -->\n");
/*  445: 764 */     text.append("<!DOCTYPE BIF [\n");
/*  446: 765 */     text.append("\t<!ELEMENT BIF ( NETWORK )*>\n");
/*  447: 766 */     text.append("\t      <!ATTLIST BIF VERSION CDATA #REQUIRED>\n");
/*  448: 767 */     text.append("\t<!ELEMENT NETWORK ( NAME, ( PROPERTY | VARIABLE | DEFINITION )* )>\n");
/*  449:     */     
/*  450: 769 */     text.append("\t<!ELEMENT NAME (#PCDATA)>\n");
/*  451: 770 */     text.append("\t<!ELEMENT VARIABLE ( NAME, ( OUTCOME |  PROPERTY )* ) >\n");
/*  452: 771 */     text.append("\t      <!ATTLIST VARIABLE TYPE (nature|decision|utility) \"nature\">\n");
/*  453:     */     
/*  454: 773 */     text.append("\t<!ELEMENT OUTCOME (#PCDATA)>\n");
/*  455: 774 */     text.append("\t<!ELEMENT DEFINITION ( FOR | GIVEN | TABLE | PROPERTY )* >\n");
/*  456:     */     
/*  457: 776 */     text.append("\t<!ELEMENT FOR (#PCDATA)>\n");
/*  458: 777 */     text.append("\t<!ELEMENT GIVEN (#PCDATA)>\n");
/*  459: 778 */     text.append("\t<!ELEMENT TABLE (#PCDATA)>\n");
/*  460: 779 */     text.append("\t<!ELEMENT PROPERTY (#PCDATA)>\n");
/*  461: 780 */     text.append("]>\n");
/*  462: 781 */     return text.toString();
/*  463:     */   }
/*  464:     */   
/*  465:     */   public String toXMLBIF03()
/*  466:     */   {
/*  467: 792 */     if (this.m_Instances == null) {
/*  468: 793 */       return "<!--No model built yet-->";
/*  469:     */     }
/*  470: 796 */     StringBuffer text = new StringBuffer();
/*  471: 797 */     text.append(getBIFHeader());
/*  472: 798 */     text.append("\n");
/*  473: 799 */     text.append("\n");
/*  474: 800 */     text.append("<BIF VERSION=\"0.3\">\n");
/*  475: 801 */     text.append("<NETWORK>\n");
/*  476: 802 */     text.append("<NAME>" + XMLNormalize(this.m_Instances.relationName()) + "</NAME>\n");
/*  477: 804 */     for (int iAttribute = 0; iAttribute < this.m_Instances.numAttributes(); iAttribute++)
/*  478:     */     {
/*  479: 805 */       text.append("<VARIABLE TYPE=\"nature\">\n");
/*  480: 806 */       text.append("<NAME>" + XMLNormalize(this.m_Instances.attribute(iAttribute).name()) + "</NAME>\n");
/*  481: 808 */       for (int iValue = 0; iValue < this.m_Instances.attribute(iAttribute).numValues(); iValue++) {
/*  482: 810 */         text.append("<OUTCOME>" + XMLNormalize(this.m_Instances.attribute(iAttribute).value(iValue)) + "</OUTCOME>\n");
/*  483:     */       }
/*  484: 814 */       text.append("</VARIABLE>\n");
/*  485:     */     }
/*  486: 817 */     for (int iAttribute = 0; iAttribute < this.m_Instances.numAttributes(); iAttribute++)
/*  487:     */     {
/*  488: 818 */       text.append("<DEFINITION>\n");
/*  489: 819 */       text.append("<FOR>" + XMLNormalize(this.m_Instances.attribute(iAttribute).name()) + "</FOR>\n");
/*  490: 821 */       for (int iParent = 0; iParent < this.m_ParentSets[iAttribute].getNrOfParents(); iParent++) {
/*  491: 822 */         text.append("<GIVEN>" + XMLNormalize(this.m_Instances.attribute(this.m_ParentSets[iAttribute].getParent(iParent)).name()) + "</GIVEN>\n");
/*  492:     */       }
/*  493: 828 */       text.append("<TABLE>\n");
/*  494: 829 */       for (int iParent = 0; iParent < this.m_ParentSets[iAttribute].getCardinalityOfParents(); iParent++)
/*  495:     */       {
/*  496: 831 */         for (int iValue = 0; iValue < this.m_Instances.attribute(iAttribute).numValues(); iValue++)
/*  497:     */         {
/*  498: 833 */           text.append(this.m_Distributions[iAttribute][iParent].getProbability(iValue));
/*  499:     */           
/*  500: 835 */           text.append(' ');
/*  501:     */         }
/*  502: 837 */         text.append('\n');
/*  503:     */       }
/*  504: 839 */       text.append("</TABLE>\n");
/*  505: 840 */       text.append("</DEFINITION>\n");
/*  506:     */     }
/*  507: 842 */     text.append("</NETWORK>\n");
/*  508: 843 */     text.append("</BIF>\n");
/*  509: 844 */     return text.toString();
/*  510:     */   }
/*  511:     */   
/*  512:     */   protected String XMLNormalize(String sStr)
/*  513:     */   {
/*  514: 855 */     StringBuffer sStr2 = new StringBuffer();
/*  515: 856 */     for (int iStr = 0; iStr < sStr.length(); iStr++)
/*  516:     */     {
/*  517: 857 */       char c = sStr.charAt(iStr);
/*  518: 858 */       switch (c)
/*  519:     */       {
/*  520:     */       case '&': 
/*  521: 860 */         sStr2.append("&amp;");
/*  522: 861 */         break;
/*  523:     */       case '\'': 
/*  524: 863 */         sStr2.append("&apos;");
/*  525: 864 */         break;
/*  526:     */       case '"': 
/*  527: 866 */         sStr2.append("&quot;");
/*  528: 867 */         break;
/*  529:     */       case '<': 
/*  530: 869 */         sStr2.append("&lt;");
/*  531: 870 */         break;
/*  532:     */       case '>': 
/*  533: 872 */         sStr2.append("&gt;");
/*  534: 873 */         break;
/*  535:     */       default: 
/*  536: 875 */         sStr2.append(c);
/*  537:     */       }
/*  538:     */     }
/*  539: 878 */     return sStr2.toString();
/*  540:     */   }
/*  541:     */   
/*  542:     */   public String useADTreeTipText()
/*  543:     */   {
/*  544: 885 */     return "When ADTree (the data structure for increasing speed on counts, not to be confused with the classifier under the same name) is used learning time goes down typically. However, because ADTrees are memory intensive, memory problems may occur. Switching this option off makes the structure learning algorithms slower, and run with less memory. By default, ADTrees are used.";
/*  545:     */   }
/*  546:     */   
/*  547:     */   public String searchAlgorithmTipText()
/*  548:     */   {
/*  549: 897 */     return "Select method used for searching network structures.";
/*  550:     */   }
/*  551:     */   
/*  552:     */   public String estimatorTipText()
/*  553:     */   {
/*  554: 906 */     return "Select Estimator algorithm for finding the conditional probability tables of the Bayes Network.";
/*  555:     */   }
/*  556:     */   
/*  557:     */   public String BIFFileTipText()
/*  558:     */   {
/*  559: 914 */     return "Set the name of a file in BIF XML format. A Bayes network learned from data can be compared with the Bayes network represented by the BIF file. Statistics calculated are o.a. the number of missing and extra arcs.";
/*  560:     */   }
/*  561:     */   
/*  562:     */   public String globalInfo()
/*  563:     */   {
/*  564: 925 */     return "Bayes Network learning using various search algorithms and quality measures.\nBase class for a Bayes Network classifier. Provides datastructures (network structure, conditional probability distributions, etc.) and facilities common to Bayes Network learning algorithms like K2 and B.\n\nFor more information see:\n\nhttp://www.cs.waikato.ac.nz/~remco/weka.pdf";
/*  565:     */   }
/*  566:     */   
/*  567:     */   public static void main(String[] argv)
/*  568:     */   {
/*  569: 941 */     runClassifier(new BayesNet(), argv);
/*  570:     */   }
/*  571:     */   
/*  572:     */   public String getName()
/*  573:     */   {
/*  574: 950 */     return this.m_Instances.relationName();
/*  575:     */   }
/*  576:     */   
/*  577:     */   public int getNrOfNodes()
/*  578:     */   {
/*  579: 959 */     return this.m_Instances.numAttributes();
/*  580:     */   }
/*  581:     */   
/*  582:     */   public String getNodeName(int iNode)
/*  583:     */   {
/*  584: 969 */     return this.m_Instances.attribute(iNode).name();
/*  585:     */   }
/*  586:     */   
/*  587:     */   public int getCardinality(int iNode)
/*  588:     */   {
/*  589: 979 */     return this.m_Instances.attribute(iNode).numValues();
/*  590:     */   }
/*  591:     */   
/*  592:     */   public String getNodeValue(int iNode, int iValue)
/*  593:     */   {
/*  594: 990 */     return this.m_Instances.attribute(iNode).value(iValue);
/*  595:     */   }
/*  596:     */   
/*  597:     */   public int getNrOfParents(int iNode)
/*  598:     */   {
/*  599:1000 */     return this.m_ParentSets[iNode].getNrOfParents();
/*  600:     */   }
/*  601:     */   
/*  602:     */   public int getParent(int iNode, int iParent)
/*  603:     */   {
/*  604:1012 */     return this.m_ParentSets[iNode].getParent(iParent);
/*  605:     */   }
/*  606:     */   
/*  607:     */   public ParentSet[] getParentSets()
/*  608:     */   {
/*  609:1021 */     return this.m_ParentSets;
/*  610:     */   }
/*  611:     */   
/*  612:     */   public Estimator[][] getDistributions()
/*  613:     */   {
/*  614:1030 */     return this.m_Distributions;
/*  615:     */   }
/*  616:     */   
/*  617:     */   public int getParentCardinality(int iNode)
/*  618:     */   {
/*  619:1040 */     return this.m_ParentSets[iNode].getCardinalityOfParents();
/*  620:     */   }
/*  621:     */   
/*  622:     */   public double getProbability(int iNode, int iParent, int iValue)
/*  623:     */   {
/*  624:1054 */     return this.m_Distributions[iNode][iParent].getProbability(iValue);
/*  625:     */   }
/*  626:     */   
/*  627:     */   public ParentSet getParentSet(int iNode)
/*  628:     */   {
/*  629:1064 */     return this.m_ParentSets[iNode];
/*  630:     */   }
/*  631:     */   
/*  632:     */   public ADNode getADTree()
/*  633:     */   {
/*  634:1073 */     return this.m_ADTree;
/*  635:     */   }
/*  636:     */   
/*  637:     */   public Enumeration<String> enumerateMeasures()
/*  638:     */   {
/*  639:1086 */     Vector<String> newVector = new Vector(4);
/*  640:1087 */     newVector.addElement("measureExtraArcs");
/*  641:1088 */     newVector.addElement("measureMissingArcs");
/*  642:1089 */     newVector.addElement("measureReversedArcs");
/*  643:1090 */     newVector.addElement("measureDivergence");
/*  644:1091 */     newVector.addElement("measureBayesScore");
/*  645:1092 */     newVector.addElement("measureBDeuScore");
/*  646:1093 */     newVector.addElement("measureMDLScore");
/*  647:1094 */     newVector.addElement("measureAICScore");
/*  648:1095 */     newVector.addElement("measureEntropyScore");
/*  649:1096 */     return newVector.elements();
/*  650:     */   }
/*  651:     */   
/*  652:     */   public double measureExtraArcs()
/*  653:     */   {
/*  654:1100 */     if (this.m_otherBayesNet != null) {
/*  655:1101 */       return this.m_otherBayesNet.extraArcs(this);
/*  656:     */     }
/*  657:1103 */     return 0.0D;
/*  658:     */   }
/*  659:     */   
/*  660:     */   public double measureMissingArcs()
/*  661:     */   {
/*  662:1107 */     if (this.m_otherBayesNet != null) {
/*  663:1108 */       return this.m_otherBayesNet.missingArcs(this);
/*  664:     */     }
/*  665:1110 */     return 0.0D;
/*  666:     */   }
/*  667:     */   
/*  668:     */   public double measureReversedArcs()
/*  669:     */   {
/*  670:1114 */     if (this.m_otherBayesNet != null) {
/*  671:1115 */       return this.m_otherBayesNet.reversedArcs(this);
/*  672:     */     }
/*  673:1117 */     return 0.0D;
/*  674:     */   }
/*  675:     */   
/*  676:     */   public double measureDivergence()
/*  677:     */   {
/*  678:1121 */     if (this.m_otherBayesNet != null) {
/*  679:1122 */       return this.m_otherBayesNet.divergence(this);
/*  680:     */     }
/*  681:1124 */     return 0.0D;
/*  682:     */   }
/*  683:     */   
/*  684:     */   public double measureBayesScore()
/*  685:     */   {
/*  686:     */     try
/*  687:     */     {
/*  688:1129 */       LocalScoreSearchAlgorithm s = new LocalScoreSearchAlgorithm(this, this.m_Instances);
/*  689:     */       
/*  690:1131 */       return s.logScore(0);
/*  691:     */     }
/*  692:     */     catch (ArithmeticException ex) {}
/*  693:1133 */     return (0.0D / 0.0D);
/*  694:     */   }
/*  695:     */   
/*  696:     */   public double measureBDeuScore()
/*  697:     */   {
/*  698:     */     try
/*  699:     */     {
/*  700:1140 */       LocalScoreSearchAlgorithm s = new LocalScoreSearchAlgorithm(this, this.m_Instances);
/*  701:     */       
/*  702:1142 */       return s.logScore(1);
/*  703:     */     }
/*  704:     */     catch (ArithmeticException ex) {}
/*  705:1144 */     return (0.0D / 0.0D);
/*  706:     */   }
/*  707:     */   
/*  708:     */   public double measureMDLScore()
/*  709:     */   {
/*  710:     */     try
/*  711:     */     {
/*  712:1151 */       LocalScoreSearchAlgorithm s = new LocalScoreSearchAlgorithm(this, this.m_Instances);
/*  713:     */       
/*  714:1153 */       return s.logScore(2);
/*  715:     */     }
/*  716:     */     catch (ArithmeticException ex) {}
/*  717:1155 */     return (0.0D / 0.0D);
/*  718:     */   }
/*  719:     */   
/*  720:     */   public double measureAICScore()
/*  721:     */   {
/*  722:     */     try
/*  723:     */     {
/*  724:1162 */       LocalScoreSearchAlgorithm s = new LocalScoreSearchAlgorithm(this, this.m_Instances);
/*  725:     */       
/*  726:1164 */       return s.logScore(4);
/*  727:     */     }
/*  728:     */     catch (ArithmeticException ex) {}
/*  729:1166 */     return (0.0D / 0.0D);
/*  730:     */   }
/*  731:     */   
/*  732:     */   public double measureEntropyScore()
/*  733:     */   {
/*  734:     */     try
/*  735:     */     {
/*  736:1173 */       LocalScoreSearchAlgorithm s = new LocalScoreSearchAlgorithm(this, this.m_Instances);
/*  737:     */       
/*  738:1175 */       return s.logScore(3);
/*  739:     */     }
/*  740:     */     catch (ArithmeticException ex) {}
/*  741:1177 */     return (0.0D / 0.0D);
/*  742:     */   }
/*  743:     */   
/*  744:     */   public double getMeasure(String measureName)
/*  745:     */   {
/*  746:1190 */     if (measureName.equals("measureExtraArcs")) {
/*  747:1191 */       return measureExtraArcs();
/*  748:     */     }
/*  749:1193 */     if (measureName.equals("measureMissingArcs")) {
/*  750:1194 */       return measureMissingArcs();
/*  751:     */     }
/*  752:1196 */     if (measureName.equals("measureReversedArcs")) {
/*  753:1197 */       return measureReversedArcs();
/*  754:     */     }
/*  755:1199 */     if (measureName.equals("measureDivergence")) {
/*  756:1200 */       return measureDivergence();
/*  757:     */     }
/*  758:1202 */     if (measureName.equals("measureBayesScore")) {
/*  759:1203 */       return measureBayesScore();
/*  760:     */     }
/*  761:1205 */     if (measureName.equals("measureBDeuScore")) {
/*  762:1206 */       return measureBDeuScore();
/*  763:     */     }
/*  764:1208 */     if (measureName.equals("measureMDLScore")) {
/*  765:1209 */       return measureMDLScore();
/*  766:     */     }
/*  767:1211 */     if (measureName.equals("measureAICScore")) {
/*  768:1212 */       return measureAICScore();
/*  769:     */     }
/*  770:1214 */     if (measureName.equals("measureEntropyScore")) {
/*  771:1215 */       return measureEntropyScore();
/*  772:     */     }
/*  773:1217 */     return 0.0D;
/*  774:     */   }
/*  775:     */   
/*  776:     */   public String getRevision()
/*  777:     */   {
/*  778:1227 */     return RevisionUtils.extract("$Revision: 11325 $");
/*  779:     */   }
/*  780:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.classifiers.bayes.BayesNet
 * JD-Core Version:    0.7.0.1
 */