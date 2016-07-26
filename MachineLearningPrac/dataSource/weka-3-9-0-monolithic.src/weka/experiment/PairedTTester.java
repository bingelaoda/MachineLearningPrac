/*    1:     */ package weka.experiment;
/*    2:     */ 
/*    3:     */ import java.io.BufferedReader;
/*    4:     */ import java.io.FileReader;
/*    5:     */ import java.io.PrintStream;
/*    6:     */ import java.io.Serializable;
/*    7:     */ import java.text.SimpleDateFormat;
/*    8:     */ import java.util.ArrayList;
/*    9:     */ import java.util.Collections;
/*   10:     */ import java.util.Date;
/*   11:     */ import java.util.Enumeration;
/*   12:     */ import java.util.Vector;
/*   13:     */ import weka.core.Attribute;
/*   14:     */ import weka.core.Instance;
/*   15:     */ import weka.core.Instances;
/*   16:     */ import weka.core.Option;
/*   17:     */ import weka.core.OptionHandler;
/*   18:     */ import weka.core.Range;
/*   19:     */ import weka.core.RevisionHandler;
/*   20:     */ import weka.core.RevisionUtils;
/*   21:     */ import weka.core.Utils;
/*   22:     */ 
/*   23:     */ public class PairedTTester
/*   24:     */   implements OptionHandler, Tester, RevisionHandler
/*   25:     */ {
/*   26:     */   static final long serialVersionUID = 8370014624008728610L;
/*   27:     */   protected Instances m_Instances;
/*   28:     */   protected int m_RunColumn;
/*   29:     */   protected int m_RunColumnSet;
/*   30:     */   protected int m_FoldColumn;
/*   31:     */   protected int m_SortColumn;
/*   32:     */   protected int[] m_SortOrder;
/*   33:     */   protected int[] m_ColOrder;
/*   34:     */   protected double m_SignificanceLevel;
/*   35:     */   protected Range m_DatasetKeyColumnsRange;
/*   36:     */   protected int[] m_DatasetKeyColumns;
/*   37:     */   protected DatasetSpecifiers m_DatasetSpecifiers;
/*   38:     */   protected Range m_ResultsetKeyColumnsRange;
/*   39:     */   protected int[] m_ResultsetKeyColumns;
/*   40:     */   protected int[] m_DisplayedResultsets;
/*   41:     */   protected ArrayList<Resultset> m_Resultsets;
/*   42:     */   protected boolean m_ResultsetsValid;
/*   43:     */   protected boolean m_ShowStdDevs;
/*   44:     */   protected ResultMatrix m_ResultMatrix;
/*   45:     */   
/*   46:     */   public PairedTTester()
/*   47:     */   {
/*   48: 120 */     this.m_RunColumn = 0;
/*   49:     */     
/*   50:     */ 
/*   51: 123 */     this.m_RunColumnSet = -1;
/*   52:     */     
/*   53:     */ 
/*   54: 126 */     this.m_FoldColumn = -1;
/*   55:     */     
/*   56:     */ 
/*   57: 129 */     this.m_SortColumn = -1;
/*   58:     */     
/*   59:     */ 
/*   60: 132 */     this.m_SortOrder = null;
/*   61:     */     
/*   62:     */ 
/*   63: 135 */     this.m_ColOrder = null;
/*   64:     */     
/*   65:     */ 
/*   66: 138 */     this.m_SignificanceLevel = 0.05D;
/*   67:     */     
/*   68:     */ 
/*   69:     */ 
/*   70:     */ 
/*   71:     */ 
/*   72: 144 */     this.m_DatasetKeyColumnsRange = new Range();
/*   73:     */     
/*   74:     */ 
/*   75:     */ 
/*   76:     */ 
/*   77:     */ 
/*   78: 150 */     this.m_DatasetSpecifiers = new DatasetSpecifiers();
/*   79:     */     
/*   80:     */ 
/*   81:     */ 
/*   82:     */ 
/*   83:     */ 
/*   84: 156 */     this.m_ResultsetKeyColumnsRange = new Range();
/*   85:     */     
/*   86:     */ 
/*   87:     */ 
/*   88:     */ 
/*   89:     */ 
/*   90: 162 */     this.m_DisplayedResultsets = null;
/*   91:     */     
/*   92:     */ 
/*   93: 165 */     this.m_Resultsets = new ArrayList();
/*   94:     */     
/*   95:     */ 
/*   96:     */ 
/*   97:     */ 
/*   98:     */ 
/*   99: 171 */     this.m_ShowStdDevs = false;
/*  100:     */     
/*  101:     */ 
/*  102: 174 */     this.m_ResultMatrix = new ResultMatrixPlainText();
/*  103:     */   }
/*  104:     */   
/*  105:     */   protected class DatasetSpecifiers
/*  106:     */     implements RevisionHandler, Serializable
/*  107:     */   {
/*  108:     */     private static final long serialVersionUID = -9020938059902723401L;
/*  109: 183 */     ArrayList<Instance> m_Specifiers = new ArrayList();
/*  110:     */     
/*  111:     */     protected DatasetSpecifiers() {}
/*  112:     */     
/*  113:     */     protected void removeAllSpecifiers()
/*  114:     */     {
/*  115: 190 */       this.m_Specifiers.clear();
/*  116:     */     }
/*  117:     */     
/*  118:     */     protected void add(Instance inst)
/*  119:     */     {
/*  120: 200 */       for (int i = 0; i < this.m_Specifiers.size(); i++)
/*  121:     */       {
/*  122: 201 */         Instance specifier = (Instance)this.m_Specifiers.get(i);
/*  123: 202 */         boolean found = true;
/*  124: 203 */         for (int m_DatasetKeyColumn : PairedTTester.this.m_DatasetKeyColumns) {
/*  125: 204 */           if (inst.value(m_DatasetKeyColumn) != specifier.value(m_DatasetKeyColumn)) {
/*  126: 206 */             found = false;
/*  127:     */           }
/*  128:     */         }
/*  129: 209 */         if (found) {
/*  130: 210 */           return;
/*  131:     */         }
/*  132:     */       }
/*  133: 213 */       this.m_Specifiers.add(inst);
/*  134:     */     }
/*  135:     */     
/*  136:     */     protected Instance specifier(int i)
/*  137:     */     {
/*  138: 224 */       return (Instance)this.m_Specifiers.get(i);
/*  139:     */     }
/*  140:     */     
/*  141:     */     protected int numSpecifiers()
/*  142:     */     {
/*  143: 234 */       return this.m_Specifiers.size();
/*  144:     */     }
/*  145:     */     
/*  146:     */     public String getRevision()
/*  147:     */     {
/*  148: 244 */       return RevisionUtils.extract("$Revision: 11542 $");
/*  149:     */     }
/*  150:     */   }
/*  151:     */   
/*  152:     */   protected class Dataset
/*  153:     */     implements RevisionHandler, Serializable
/*  154:     */   {
/*  155:     */     private static final long serialVersionUID = -2801397601839433282L;
/*  156:     */     Instance m_Template;
/*  157:     */     ArrayList<Instance> m_Dataset;
/*  158:     */     
/*  159:     */     public Dataset(Instance template)
/*  160:     */     {
/*  161: 267 */       this.m_Template = template;
/*  162: 268 */       this.m_Dataset = new ArrayList();
/*  163: 269 */       add(template);
/*  164:     */     }
/*  165:     */     
/*  166:     */     protected boolean matchesTemplate(Instance first)
/*  167:     */     {
/*  168: 281 */       for (int m_DatasetKeyColumn : PairedTTester.this.m_DatasetKeyColumns) {
/*  169: 282 */         if (first.value(m_DatasetKeyColumn) != this.m_Template.value(m_DatasetKeyColumn)) {
/*  170: 284 */           return false;
/*  171:     */         }
/*  172:     */       }
/*  173: 287 */       return true;
/*  174:     */     }
/*  175:     */     
/*  176:     */     protected void add(Instance inst)
/*  177:     */     {
/*  178: 297 */       this.m_Dataset.add(inst);
/*  179:     */     }
/*  180:     */     
/*  181:     */     protected ArrayList<Instance> contents()
/*  182:     */     {
/*  183: 307 */       return this.m_Dataset;
/*  184:     */     }
/*  185:     */     
/*  186:     */     public void sort(int runColumn)
/*  187:     */     {
/*  188: 317 */       double[] runNums = new double[this.m_Dataset.size()];
/*  189: 318 */       for (int j = 0; j < runNums.length; j++) {
/*  190: 319 */         runNums[j] = ((Instance)this.m_Dataset.get(j)).value(runColumn);
/*  191:     */       }
/*  192: 321 */       int[] index = Utils.stableSort(runNums);
/*  193: 322 */       ArrayList<Instance> newDataset = new ArrayList(runNums.length);
/*  194: 323 */       for (int element : index) {
/*  195: 324 */         newDataset.add(this.m_Dataset.get(element));
/*  196:     */       }
/*  197: 326 */       this.m_Dataset = newDataset;
/*  198:     */     }
/*  199:     */     
/*  200:     */     public String getRevision()
/*  201:     */     {
/*  202: 336 */       return RevisionUtils.extract("$Revision: 11542 $");
/*  203:     */     }
/*  204:     */   }
/*  205:     */   
/*  206:     */   protected class Resultset
/*  207:     */     implements RevisionHandler, Serializable
/*  208:     */   {
/*  209:     */     private static final long serialVersionUID = 1543786683821339978L;
/*  210:     */     Instance m_Template;
/*  211:     */     ArrayList<PairedTTester.Dataset> m_Datasets;
/*  212:     */     
/*  213:     */     public Resultset(Instance template)
/*  214:     */     {
/*  215: 359 */       this.m_Template = template;
/*  216: 360 */       this.m_Datasets = new ArrayList();
/*  217: 361 */       add(template);
/*  218:     */     }
/*  219:     */     
/*  220:     */     protected boolean matchesTemplate(Instance first)
/*  221:     */     {
/*  222: 373 */       for (int m_ResultsetKeyColumn : PairedTTester.this.m_ResultsetKeyColumns) {
/*  223: 374 */         if (first.value(m_ResultsetKeyColumn) != this.m_Template.value(m_ResultsetKeyColumn)) {
/*  224: 376 */           return false;
/*  225:     */         }
/*  226:     */       }
/*  227: 379 */       return true;
/*  228:     */     }
/*  229:     */     
/*  230:     */     protected String templateString()
/*  231:     */     {
/*  232: 390 */       String result = "";
/*  233: 391 */       String tempResult = "";
/*  234: 392 */       for (int m_ResultsetKeyColumn : PairedTTester.this.m_ResultsetKeyColumns)
/*  235:     */       {
/*  236: 393 */         tempResult = this.m_Template.toString(m_ResultsetKeyColumn) + ' ';
/*  237:     */         
/*  238:     */ 
/*  239: 396 */         tempResult = Utils.removeSubstring(tempResult, "weka.classifiers.");
/*  240: 397 */         tempResult = Utils.removeSubstring(tempResult, "weka.filters.");
/*  241: 398 */         tempResult = Utils.removeSubstring(tempResult, "weka.attributeSelection.");
/*  242:     */         
/*  243: 400 */         result = result + tempResult;
/*  244:     */       }
/*  245: 402 */       return result.trim();
/*  246:     */     }
/*  247:     */     
/*  248:     */     public ArrayList<Instance> dataset(Instance inst)
/*  249:     */     {
/*  250: 413 */       for (int i = 0; i < this.m_Datasets.size(); i++) {
/*  251: 414 */         if (((PairedTTester.Dataset)this.m_Datasets.get(i)).matchesTemplate(inst)) {
/*  252: 415 */           return ((PairedTTester.Dataset)this.m_Datasets.get(i)).contents();
/*  253:     */         }
/*  254:     */       }
/*  255: 418 */       return null;
/*  256:     */     }
/*  257:     */     
/*  258:     */     public void add(Instance newInst)
/*  259:     */     {
/*  260: 428 */       for (int i = 0; i < this.m_Datasets.size(); i++) {
/*  261: 429 */         if (((PairedTTester.Dataset)this.m_Datasets.get(i)).matchesTemplate(newInst))
/*  262:     */         {
/*  263: 430 */           ((PairedTTester.Dataset)this.m_Datasets.get(i)).add(newInst);
/*  264: 431 */           return;
/*  265:     */         }
/*  266:     */       }
/*  267: 434 */       PairedTTester.Dataset newDataset = new PairedTTester.Dataset(PairedTTester.this, newInst);
/*  268: 435 */       this.m_Datasets.add(newDataset);
/*  269:     */     }
/*  270:     */     
/*  271:     */     public void sort(int runColumn)
/*  272:     */     {
/*  273: 445 */       for (int i = 0; i < this.m_Datasets.size(); i++) {
/*  274: 446 */         ((PairedTTester.Dataset)this.m_Datasets.get(i)).sort(runColumn);
/*  275:     */       }
/*  276:     */     }
/*  277:     */     
/*  278:     */     public String getRevision()
/*  279:     */     {
/*  280: 457 */       return RevisionUtils.extract("$Revision: 11542 $");
/*  281:     */     }
/*  282:     */   }
/*  283:     */   
/*  284:     */   protected String templateString(Instance template)
/*  285:     */   {
/*  286: 469 */     String result = "";
/*  287: 470 */     for (int m_DatasetKeyColumn : this.m_DatasetKeyColumns) {
/*  288: 471 */       result = result + template.toString(m_DatasetKeyColumn) + ' ';
/*  289:     */     }
/*  290: 473 */     if (result.startsWith("weka.classifiers.")) {
/*  291: 474 */       result = result.substring("weka.classifiers.".length());
/*  292:     */     }
/*  293: 476 */     return result.trim();
/*  294:     */   }
/*  295:     */   
/*  296:     */   public void setResultMatrix(ResultMatrix matrix)
/*  297:     */   {
/*  298: 487 */     this.m_ResultMatrix = matrix;
/*  299:     */   }
/*  300:     */   
/*  301:     */   public ResultMatrix getResultMatrix()
/*  302:     */   {
/*  303: 497 */     return this.m_ResultMatrix;
/*  304:     */   }
/*  305:     */   
/*  306:     */   public void setShowStdDevs(boolean s)
/*  307:     */   {
/*  308: 507 */     this.m_ShowStdDevs = s;
/*  309:     */   }
/*  310:     */   
/*  311:     */   public boolean getShowStdDevs()
/*  312:     */   {
/*  313: 517 */     return this.m_ShowStdDevs;
/*  314:     */   }
/*  315:     */   
/*  316:     */   protected void prepareData()
/*  317:     */     throws Exception
/*  318:     */   {
/*  319: 527 */     if (this.m_Instances == null) {
/*  320: 528 */       throw new Exception("No instances have been set");
/*  321:     */     }
/*  322: 530 */     if (this.m_RunColumnSet == -1) {
/*  323: 531 */       this.m_RunColumn = (this.m_Instances.numAttributes() - 1);
/*  324:     */     } else {
/*  325: 533 */       this.m_RunColumn = this.m_RunColumnSet;
/*  326:     */     }
/*  327: 536 */     if (this.m_ResultsetKeyColumnsRange == null) {
/*  328: 537 */       throw new Exception("No result specifier columns have been set");
/*  329:     */     }
/*  330: 539 */     this.m_ResultsetKeyColumnsRange.setUpper(this.m_Instances.numAttributes() - 1);
/*  331: 540 */     this.m_ResultsetKeyColumns = this.m_ResultsetKeyColumnsRange.getSelection();
/*  332: 542 */     if (this.m_DatasetKeyColumnsRange == null) {
/*  333: 543 */       throw new Exception("No dataset specifier columns have been set");
/*  334:     */     }
/*  335: 545 */     this.m_DatasetKeyColumnsRange.setUpper(this.m_Instances.numAttributes() - 1);
/*  336: 546 */     this.m_DatasetKeyColumns = this.m_DatasetKeyColumnsRange.getSelection();
/*  337:     */     
/*  338:     */ 
/*  339: 549 */     this.m_Resultsets.clear();
/*  340: 550 */     this.m_DatasetSpecifiers.removeAllSpecifiers();
/*  341: 551 */     for (int i = 0; i < this.m_Instances.numInstances(); i++)
/*  342:     */     {
/*  343: 552 */       Instance current = this.m_Instances.instance(i);
/*  344: 553 */       if (current.isMissing(this.m_RunColumn)) {
/*  345: 554 */         throw new Exception("Instance has missing value in run column!\n" + current);
/*  346:     */       }
/*  347: 557 */       for (int m_ResultsetKeyColumn : this.m_ResultsetKeyColumns) {
/*  348: 558 */         if (current.isMissing(m_ResultsetKeyColumn)) {
/*  349: 559 */           throw new Exception("Instance has missing value in resultset key column " + (m_ResultsetKeyColumn + 1) + "!\n" + current);
/*  350:     */         }
/*  351:     */       }
/*  352: 563 */       for (int m_DatasetKeyColumn : this.m_DatasetKeyColumns) {
/*  353: 564 */         if (current.isMissing(m_DatasetKeyColumn)) {
/*  354: 565 */           throw new Exception("Instance has missing value in dataset key column " + (m_DatasetKeyColumn + 1) + "!\n" + current);
/*  355:     */         }
/*  356:     */       }
/*  357: 569 */       boolean found = false;
/*  358: 570 */       for (int j = 0; j < this.m_Resultsets.size(); j++)
/*  359:     */       {
/*  360: 571 */         Resultset resultset = (Resultset)this.m_Resultsets.get(j);
/*  361: 572 */         if (resultset.matchesTemplate(current))
/*  362:     */         {
/*  363: 573 */           resultset.add(current);
/*  364: 574 */           found = true;
/*  365: 575 */           break;
/*  366:     */         }
/*  367:     */       }
/*  368: 578 */       if (!found)
/*  369:     */       {
/*  370: 579 */         Resultset resultset = new Resultset(current);
/*  371: 580 */         this.m_Resultsets.add(resultset);
/*  372:     */       }
/*  373: 583 */       this.m_DatasetSpecifiers.add(current);
/*  374:     */     }
/*  375: 587 */     for (int j = 0; j < this.m_Resultsets.size(); j++)
/*  376:     */     {
/*  377: 588 */       Resultset resultset = (Resultset)this.m_Resultsets.get(j);
/*  378: 589 */       if (this.m_FoldColumn >= 0) {
/*  379: 591 */         resultset.sort(this.m_FoldColumn);
/*  380:     */       }
/*  381: 593 */       resultset.sort(this.m_RunColumn);
/*  382:     */     }
/*  383: 596 */     this.m_ResultsetsValid = true;
/*  384:     */   }
/*  385:     */   
/*  386:     */   public int getNumDatasets()
/*  387:     */   {
/*  388: 607 */     if (!this.m_ResultsetsValid) {
/*  389:     */       try
/*  390:     */       {
/*  391: 609 */         prepareData();
/*  392:     */       }
/*  393:     */       catch (Exception ex)
/*  394:     */       {
/*  395: 611 */         ex.printStackTrace();
/*  396: 612 */         return 0;
/*  397:     */       }
/*  398:     */     }
/*  399: 615 */     return this.m_DatasetSpecifiers.numSpecifiers();
/*  400:     */   }
/*  401:     */   
/*  402:     */   public int getNumResultsets()
/*  403:     */   {
/*  404: 626 */     if (!this.m_ResultsetsValid) {
/*  405:     */       try
/*  406:     */       {
/*  407: 628 */         prepareData();
/*  408:     */       }
/*  409:     */       catch (Exception ex)
/*  410:     */       {
/*  411: 630 */         ex.printStackTrace();
/*  412: 631 */         return 0;
/*  413:     */       }
/*  414:     */     }
/*  415: 634 */     return this.m_Resultsets.size();
/*  416:     */   }
/*  417:     */   
/*  418:     */   public String getResultsetName(int index)
/*  419:     */   {
/*  420: 646 */     if (!this.m_ResultsetsValid) {
/*  421:     */       try
/*  422:     */       {
/*  423: 648 */         prepareData();
/*  424:     */       }
/*  425:     */       catch (Exception ex)
/*  426:     */       {
/*  427: 650 */         ex.printStackTrace();
/*  428: 651 */         return null;
/*  429:     */       }
/*  430:     */     }
/*  431: 654 */     return ((Resultset)this.m_Resultsets.get(index)).templateString();
/*  432:     */   }
/*  433:     */   
/*  434:     */   public boolean displayResultset(int index)
/*  435:     */   {
/*  436: 669 */     boolean result = true;
/*  437: 671 */     if (this.m_DisplayedResultsets != null)
/*  438:     */     {
/*  439: 672 */       result = false;
/*  440: 673 */       for (int i = 0; i < this.m_DisplayedResultsets.length; i++) {
/*  441: 674 */         if (this.m_DisplayedResultsets[i] == index)
/*  442:     */         {
/*  443: 675 */           result = true;
/*  444: 676 */           break;
/*  445:     */         }
/*  446:     */       }
/*  447:     */     }
/*  448: 681 */     return result;
/*  449:     */   }
/*  450:     */   
/*  451:     */   public PairedStats calculateStatistics(Instance datasetSpecifier, int resultset1Index, int resultset2Index, int comparisonColumn)
/*  452:     */     throws Exception
/*  453:     */   {
/*  454: 700 */     if (this.m_Instances.attribute(comparisonColumn).type() != 0) {
/*  455: 701 */       throw new Exception("Comparison column " + (comparisonColumn + 1) + " (" + this.m_Instances.attribute(comparisonColumn).name() + ") is not numeric");
/*  456:     */     }
/*  457: 704 */     if (!this.m_ResultsetsValid) {
/*  458: 705 */       prepareData();
/*  459:     */     }
/*  460: 708 */     Resultset resultset1 = (Resultset)this.m_Resultsets.get(resultset1Index);
/*  461: 709 */     Resultset resultset2 = (Resultset)this.m_Resultsets.get(resultset2Index);
/*  462: 710 */     ArrayList<Instance> dataset1 = resultset1.dataset(datasetSpecifier);
/*  463: 711 */     ArrayList<Instance> dataset2 = resultset2.dataset(datasetSpecifier);
/*  464: 712 */     String datasetName = templateString(datasetSpecifier);
/*  465: 713 */     if (dataset1 == null) {
/*  466: 714 */       throw new Exception("No results for dataset=" + datasetName + " for resultset=" + resultset1.templateString());
/*  467:     */     }
/*  468: 716 */     if (dataset2 == null) {
/*  469: 717 */       throw new Exception("No results for dataset=" + datasetName + " for resultset=" + resultset2.templateString());
/*  470:     */     }
/*  471: 719 */     if (dataset1.size() != dataset2.size()) {
/*  472: 720 */       throw new Exception("Results for dataset=" + datasetName + " differ in size for resultset=" + resultset1.templateString() + " and resultset=" + resultset2.templateString());
/*  473:     */     }
/*  474: 725 */     PairedStats pairedStats = new PairedStats(this.m_SignificanceLevel);
/*  475: 727 */     for (int k = 0; k < dataset1.size(); k++)
/*  476:     */     {
/*  477: 728 */       Instance current1 = (Instance)dataset1.get(k);
/*  478: 729 */       Instance current2 = (Instance)dataset2.get(k);
/*  479: 730 */       if (current1.isMissing(comparisonColumn))
/*  480:     */       {
/*  481: 731 */         System.err.println("Instance has missing value in comparison column!\n" + current1);
/*  482:     */       }
/*  483: 735 */       else if (current2.isMissing(comparisonColumn))
/*  484:     */       {
/*  485: 736 */         System.err.println("Instance has missing value in comparison column!\n" + current2);
/*  486:     */       }
/*  487:     */       else
/*  488:     */       {
/*  489: 740 */         if (current1.value(this.m_RunColumn) != current2.value(this.m_RunColumn)) {
/*  490: 741 */           System.err.println("Run numbers do not match!\n" + current1 + current2);
/*  491:     */         }
/*  492: 743 */         if ((this.m_FoldColumn != -1) && 
/*  493: 744 */           (current1.value(this.m_FoldColumn) != current2.value(this.m_FoldColumn))) {
/*  494: 745 */           System.err.println("Fold numbers do not match!\n" + current1 + current2);
/*  495:     */         }
/*  496: 749 */         double value1 = current1.value(comparisonColumn);
/*  497: 750 */         double value2 = current2.value(comparisonColumn);
/*  498: 751 */         pairedStats.add(value1, value2);
/*  499:     */       }
/*  500:     */     }
/*  501: 753 */     pairedStats.calculateDerived();
/*  502:     */     
/*  503:     */ 
/*  504: 756 */     return pairedStats;
/*  505:     */   }
/*  506:     */   
/*  507:     */   public String resultsetKey()
/*  508:     */   {
/*  509: 768 */     if (!this.m_ResultsetsValid) {
/*  510:     */       try
/*  511:     */       {
/*  512: 770 */         prepareData();
/*  513:     */       }
/*  514:     */       catch (Exception ex)
/*  515:     */       {
/*  516: 772 */         ex.printStackTrace();
/*  517: 773 */         return ex.getMessage();
/*  518:     */       }
/*  519:     */     }
/*  520: 776 */     String result = "";
/*  521: 777 */     for (int j = 0; j < getNumResultsets(); j++) {
/*  522: 778 */       result = result + "(" + (j + 1) + ") " + getResultsetName(j) + '\n';
/*  523:     */     }
/*  524: 780 */     return result + '\n';
/*  525:     */   }
/*  526:     */   
/*  527:     */   public String header(int comparisonColumn)
/*  528:     */   {
/*  529: 792 */     if (!this.m_ResultsetsValid) {
/*  530:     */       try
/*  531:     */       {
/*  532: 794 */         prepareData();
/*  533:     */       }
/*  534:     */       catch (Exception ex)
/*  535:     */       {
/*  536: 796 */         ex.printStackTrace();
/*  537: 797 */         return ex.getMessage();
/*  538:     */       }
/*  539:     */     }
/*  540: 801 */     initResultMatrix();
/*  541: 802 */     this.m_ResultMatrix.addHeader("Tester", getClass().getName() + " " + Utils.joinOptions(getOptions()));
/*  542: 803 */     this.m_ResultMatrix.addHeader("Analysing", this.m_Instances.attribute(comparisonColumn).name());
/*  543:     */     
/*  544: 805 */     this.m_ResultMatrix.addHeader("Datasets", Integer.toString(getNumDatasets()));
/*  545: 806 */     this.m_ResultMatrix.addHeader("Resultsets", Integer.toString(getNumResultsets()));
/*  546:     */     
/*  547: 808 */     this.m_ResultMatrix.addHeader("Confidence", getSignificanceLevel() + " (two tailed)");
/*  548:     */     
/*  549: 810 */     this.m_ResultMatrix.addHeader("Sorted by", getSortColumnName());
/*  550: 811 */     this.m_ResultMatrix.addHeader("Date", new SimpleDateFormat().format(new Date()));
/*  551:     */     
/*  552:     */ 
/*  553: 814 */     return this.m_ResultMatrix.toStringHeader() + "\n";
/*  554:     */   }
/*  555:     */   
/*  556:     */   public int[][] multiResultsetWins(int comparisonColumn, int[][] nonSigWin)
/*  557:     */     throws Exception
/*  558:     */   {
/*  559: 831 */     int numResultsets = getNumResultsets();
/*  560: 832 */     int[][] win = new int[numResultsets][numResultsets];
/*  561: 834 */     for (int i = 0; i < numResultsets; i++) {
/*  562: 835 */       for (int j = i + 1; j < numResultsets; j++)
/*  563:     */       {
/*  564: 836 */         System.err.print("Comparing (" + (i + 1) + ") with (" + (j + 1) + ")\r");
/*  565:     */         
/*  566: 838 */         System.err.flush();
/*  567: 839 */         for (int k = 0; k < getNumDatasets(); k++) {
/*  568:     */           try
/*  569:     */           {
/*  570: 841 */             PairedStats pairedStats = calculateStatistics(this.m_DatasetSpecifiers.specifier(k), i, j, comparisonColumn);
/*  571: 843 */             if (pairedStats.differencesSignificance < 0) {
/*  572: 844 */               win[i][j] += 1;
/*  573: 845 */             } else if (pairedStats.differencesSignificance > 0) {
/*  574: 846 */               win[j][i] += 1;
/*  575:     */             }
/*  576: 849 */             if (pairedStats.differencesStats.mean < 0.0D) {
/*  577: 850 */               nonSigWin[i][j] += 1;
/*  578: 851 */             } else if (pairedStats.differencesStats.mean > 0.0D) {
/*  579: 852 */               nonSigWin[j][i] += 1;
/*  580:     */             }
/*  581:     */           }
/*  582:     */           catch (Exception ex)
/*  583:     */           {
/*  584: 856 */             System.err.println(ex.getMessage());
/*  585:     */           }
/*  586:     */         }
/*  587:     */       }
/*  588:     */     }
/*  589: 861 */     return win;
/*  590:     */   }
/*  591:     */   
/*  592:     */   protected void initResultMatrix()
/*  593:     */   {
/*  594: 869 */     this.m_ResultMatrix.setSize(getNumResultsets(), getNumDatasets());
/*  595: 870 */     this.m_ResultMatrix.setShowStdDev(this.m_ShowStdDevs);
/*  596: 872 */     for (int i = 0; i < getNumDatasets(); i++) {
/*  597: 873 */       this.m_ResultMatrix.setRowName(i, templateString(this.m_DatasetSpecifiers.specifier(i)));
/*  598:     */     }
/*  599: 877 */     for (int j = 0; j < getNumResultsets(); j++)
/*  600:     */     {
/*  601: 878 */       this.m_ResultMatrix.setColName(j, getResultsetName(j));
/*  602: 879 */       this.m_ResultMatrix.setColHidden(j, !displayResultset(j));
/*  603:     */     }
/*  604:     */   }
/*  605:     */   
/*  606:     */   public String multiResultsetSummary(int comparisonColumn)
/*  607:     */     throws Exception
/*  608:     */   {
/*  609: 895 */     int[][] nonSigWin = new int[getNumResultsets()][getNumResultsets()];
/*  610: 896 */     int[][] win = multiResultsetWins(comparisonColumn, nonSigWin);
/*  611:     */     
/*  612: 898 */     initResultMatrix();
/*  613: 899 */     this.m_ResultMatrix.setSummary(nonSigWin, win);
/*  614:     */     
/*  615: 901 */     return this.m_ResultMatrix.toStringSummary();
/*  616:     */   }
/*  617:     */   
/*  618:     */   public String multiResultsetRanking(int comparisonColumn)
/*  619:     */     throws Exception
/*  620:     */   {
/*  621: 914 */     int[][] nonSigWin = new int[getNumResultsets()][getNumResultsets()];
/*  622: 915 */     int[][] win = multiResultsetWins(comparisonColumn, nonSigWin);
/*  623:     */     
/*  624: 917 */     initResultMatrix();
/*  625: 918 */     this.m_ResultMatrix.setRanking(win);
/*  626:     */     
/*  627: 920 */     return this.m_ResultMatrix.toStringRanking();
/*  628:     */   }
/*  629:     */   
/*  630:     */   public String multiResultsetFull(int baseResultset, int comparisonColumn)
/*  631:     */     throws Exception
/*  632:     */   {
/*  633: 936 */     int maxWidthMean = 2;
/*  634: 937 */     int maxWidthStdDev = 2;
/*  635:     */     
/*  636: 939 */     double[] sortValues = new double[getNumDatasets()];
/*  637: 942 */     for (int i = 0; i < getNumDatasets(); i++)
/*  638:     */     {
/*  639: 943 */       sortValues[i] = (1.0D / 0.0D);
/*  640: 945 */       for (int j = 0; j < getNumResultsets(); j++) {
/*  641: 946 */         if (displayResultset(j)) {
/*  642:     */           try
/*  643:     */           {
/*  644: 950 */             PairedStats pairedStats = calculateStatistics(this.m_DatasetSpecifiers.specifier(i), baseResultset, j, comparisonColumn);
/*  645: 953 */             if ((!Double.isInfinite(pairedStats.yStats.mean)) && (!Double.isNaN(pairedStats.yStats.mean)))
/*  646:     */             {
/*  647: 955 */               double width = Math.log(Math.abs(pairedStats.yStats.mean)) / Math.log(10.0D) + 1.0D;
/*  648: 957 */               if (width > maxWidthMean) {
/*  649: 958 */                 maxWidthMean = (int)width;
/*  650:     */               }
/*  651:     */             }
/*  652: 962 */             if (j == baseResultset) {
/*  653: 963 */               if (getSortColumn() != -1) {
/*  654: 964 */                 sortValues[i] = calculateStatistics(this.m_DatasetSpecifiers.specifier(i), baseResultset, j, getSortColumn()).xStats.mean;
/*  655:     */               } else {
/*  656: 968 */                 sortValues[i] = i;
/*  657:     */               }
/*  658:     */             }
/*  659: 972 */             if ((this.m_ShowStdDevs) && (!Double.isInfinite(pairedStats.yStats.stdDev)) && (!Double.isNaN(pairedStats.yStats.stdDev)))
/*  660:     */             {
/*  661: 974 */               double width = Math.log(Math.abs(pairedStats.yStats.stdDev)) / Math.log(10.0D) + 1.0D;
/*  662: 976 */               if (width > maxWidthStdDev) {
/*  663: 977 */                 maxWidthStdDev = (int)width;
/*  664:     */               }
/*  665:     */             }
/*  666:     */           }
/*  667:     */           catch (Exception ex)
/*  668:     */           {
/*  669: 982 */             System.err.println(ex);
/*  670:     */           }
/*  671:     */         }
/*  672:     */       }
/*  673:     */     }
/*  674: 988 */     this.m_SortOrder = Utils.sort(sortValues);
/*  675:     */     
/*  676:     */ 
/*  677: 991 */     this.m_ColOrder = new int[getNumResultsets()];
/*  678: 992 */     this.m_ColOrder[0] = baseResultset;
/*  679: 993 */     int index = 1;
/*  680: 994 */     for (int i = 0; i < getNumResultsets(); i++) {
/*  681: 995 */       if (i != baseResultset)
/*  682:     */       {
/*  683: 998 */         this.m_ColOrder[index] = i;
/*  684: 999 */         index++;
/*  685:     */       }
/*  686:     */     }
/*  687:1003 */     initResultMatrix();
/*  688:1004 */     this.m_ResultMatrix.setRowOrder(this.m_SortOrder);
/*  689:1005 */     this.m_ResultMatrix.setColOrder(this.m_ColOrder);
/*  690:1006 */     this.m_ResultMatrix.setMeanWidth(maxWidthMean);
/*  691:1007 */     this.m_ResultMatrix.setStdDevWidth(maxWidthStdDev);
/*  692:1008 */     this.m_ResultMatrix.setSignificanceWidth(1);
/*  693:1012 */     for (int i = 0; i < this.m_ResultMatrix.getColCount(); i++) {
/*  694:1013 */       if ((i == baseResultset) && (this.m_ResultMatrix.getColHidden(i)))
/*  695:     */       {
/*  696:1014 */         this.m_ResultMatrix.setColHidden(i, false);
/*  697:1015 */         System.err.println("Note: test base was hidden - set visible!");
/*  698:     */       }
/*  699:     */     }
/*  700:1020 */     for (int i = 0; i < getNumDatasets(); i++)
/*  701:     */     {
/*  702:1021 */       this.m_ResultMatrix.setRowName(i, templateString(this.m_DatasetSpecifiers.specifier(i)));
/*  703:1024 */       for (int j = 0; j < getNumResultsets(); j++) {
/*  704:     */         try
/*  705:     */         {
/*  706:1027 */           PairedStats pairedStats = calculateStatistics(this.m_DatasetSpecifiers.specifier(i), baseResultset, j, comparisonColumn);
/*  707:     */           
/*  708:     */ 
/*  709:     */ 
/*  710:     */ 
/*  711:1032 */           this.m_ResultMatrix.setCount(i, pairedStats.count);
/*  712:     */           
/*  713:     */ 
/*  714:1035 */           this.m_ResultMatrix.setMean(j, i, pairedStats.yStats.mean);
/*  715:     */           
/*  716:     */ 
/*  717:1038 */           this.m_ResultMatrix.setStdDev(j, i, pairedStats.yStats.stdDev);
/*  718:1041 */           if (pairedStats.differencesSignificance < 0) {
/*  719:1042 */             this.m_ResultMatrix.setSignificance(j, i, 1);
/*  720:1043 */           } else if (pairedStats.differencesSignificance > 0) {
/*  721:1044 */             this.m_ResultMatrix.setSignificance(j, i, 2);
/*  722:     */           } else {
/*  723:1047 */             this.m_ResultMatrix.setSignificance(j, i, 0);
/*  724:     */           }
/*  725:     */         }
/*  726:     */         catch (Exception e)
/*  727:     */         {
/*  728:1051 */           System.err.println(e);
/*  729:     */         }
/*  730:     */       }
/*  731:     */     }
/*  732:1057 */     StringBuffer result = new StringBuffer(1000);
/*  733:     */     try
/*  734:     */     {
/*  735:1059 */       result.append(this.m_ResultMatrix.toStringMatrix());
/*  736:     */     }
/*  737:     */     catch (Exception e)
/*  738:     */     {
/*  739:1061 */       e.printStackTrace();
/*  740:     */     }
/*  741:1066 */     if (this.m_ResultMatrix.getEnumerateColNames()) {
/*  742:1067 */       result.append("\n\n" + this.m_ResultMatrix.toStringKey());
/*  743:     */     }
/*  744:1070 */     return result.toString();
/*  745:     */   }
/*  746:     */   
/*  747:     */   public Enumeration<Option> listOptions()
/*  748:     */   {
/*  749:1081 */     Vector<Option> newVector = new Vector();
/*  750:     */     
/*  751:1083 */     newVector.addElement(new Option("\tSpecify list of columns that specify a unique\n\tdataset.\n\tFirst and last are valid indexes. (default none)", "D", 1, "-D <index,index2-index4,...>"));
/*  752:     */     
/*  753:     */ 
/*  754:     */ 
/*  755:1087 */     newVector.addElement(new Option("\tSet the index of the column containing the run number", "R", 1, "-R <index>"));
/*  756:     */     
/*  757:     */ 
/*  758:1090 */     newVector.addElement(new Option("\tSet the index of the column containing the fold number", "F", 1, "-F <index>"));
/*  759:     */     
/*  760:     */ 
/*  761:1093 */     newVector.addElement(new Option("\tSpecify list of columns that specify a unique\n\t'result generator' (eg: classifier name and options).\n\tFirst and last are valid indexes. (default none)", "G", 1, "-G <index1,index2-index4,...>"));
/*  762:     */     
/*  763:     */ 
/*  764:     */ 
/*  765:     */ 
/*  766:1098 */     newVector.addElement(new Option("\tSet the significance level for comparisons (default 0.05)", "S", 1, "-S <significance level>"));
/*  767:     */     
/*  768:     */ 
/*  769:1101 */     newVector.addElement(new Option("\tSet the result matrix (classname plus parameters).\n\t(default: weka.experiment.ResultMatrixPlainText)", "result-matrix", 1, "-result-matrix <result-matrix-class>"));
/*  770:     */     
/*  771:     */ 
/*  772:     */ 
/*  773:1105 */     newVector.addElement(new Option("\tShow standard deviations", "V", 0, "-V"));
/*  774:     */     
/*  775:1107 */     newVector.addElement(new Option("\tProduce table comparisons in Latex table format", "L", 0, "-L"));
/*  776:     */     
/*  777:1109 */     newVector.addElement(new Option("\tProduce table comparisons in CSV table format", "csv", 0, "-csv"));
/*  778:     */     
/*  779:1111 */     newVector.addElement(new Option("\tProduce table comparisons in HTML table format", "html", 0, "-html"));
/*  780:     */     
/*  781:1113 */     newVector.addElement(new Option("\tProduce table comparisons with only the significance values", "significance", 0, "-significance"));
/*  782:     */     
/*  783:     */ 
/*  784:1116 */     newVector.addElement(new Option("\tProduce table comparisons output suitable for GNUPlot", "gnuplot", 0, "-gnuplot"));
/*  785:     */     
/*  786:     */ 
/*  787:1119 */     newVector.addElement(new Option("", "", 0, "\nOptions specific to result matrix " + getResultMatrix().getClass().getName() + ":"));
/*  788:     */     
/*  789:     */ 
/*  790:     */ 
/*  791:     */ 
/*  792:1124 */     newVector.addAll(Collections.list(getResultMatrix().listOptions()));
/*  793:1125 */     return newVector.elements();
/*  794:     */   }
/*  795:     */   
/*  796:     */   public void setOptions(String[] options)
/*  797:     */     throws Exception
/*  798:     */   {
/*  799:1202 */     setShowStdDevs(Utils.getFlag('V', options));
/*  800:1203 */     String outputOption = Utils.getOption("result-matrix", options);
/*  801:1204 */     if (outputOption.length() != 0)
/*  802:     */     {
/*  803:1205 */       String[] resultMatrixSpec = Utils.splitOptions(outputOption);
/*  804:1206 */       if (resultMatrixSpec.length == 0) {
/*  805:1207 */         throw new Exception("Invalid ResultMatrix specification string");
/*  806:     */       }
/*  807:1209 */       String resultMatrixName = resultMatrixSpec[0];
/*  808:1210 */       resultMatrixSpec[0] = "";
/*  809:1211 */       ResultMatrix resultMatrix = (ResultMatrix)Utils.forName(Class.forName("weka.experiment.ResultMatrix"), resultMatrixName, resultMatrixSpec);
/*  810:     */       
/*  811:1213 */       setResultMatrix(resultMatrix);
/*  812:     */     }
/*  813:1214 */     else if (Utils.getFlag('L', options))
/*  814:     */     {
/*  815:1215 */       setResultMatrix(new ResultMatrixLatex());
/*  816:     */     }
/*  817:1216 */     else if (Utils.getFlag("csv", options))
/*  818:     */     {
/*  819:1217 */       setResultMatrix(new ResultMatrixCSV());
/*  820:     */     }
/*  821:1218 */     else if (Utils.getFlag("html", options))
/*  822:     */     {
/*  823:1219 */       setResultMatrix(new ResultMatrixHTML());
/*  824:     */     }
/*  825:1220 */     else if (Utils.getFlag("significance", options))
/*  826:     */     {
/*  827:1221 */       setResultMatrix(new ResultMatrixSignificance());
/*  828:     */     }
/*  829:1222 */     else if (Utils.getFlag("gnuplot", options))
/*  830:     */     {
/*  831:1223 */       setResultMatrix(new ResultMatrixGnuPlot());
/*  832:     */     }
/*  833:1226 */     String datasetList = Utils.getOption('D', options);
/*  834:1227 */     Range datasetRange = new Range();
/*  835:1228 */     if (datasetList.length() != 0) {
/*  836:1229 */       datasetRange.setRanges(datasetList);
/*  837:     */     }
/*  838:1231 */     setDatasetKeyColumns(datasetRange);
/*  839:     */     
/*  840:1233 */     String indexStr = Utils.getOption('R', options);
/*  841:1234 */     if (indexStr.length() != 0)
/*  842:     */     {
/*  843:1235 */       if (indexStr.equals("first")) {
/*  844:1236 */         setRunColumn(0);
/*  845:1237 */       } else if (indexStr.equals("last")) {
/*  846:1238 */         setRunColumn(-1);
/*  847:     */       } else {
/*  848:1240 */         setRunColumn(Integer.parseInt(indexStr) - 1);
/*  849:     */       }
/*  850:     */     }
/*  851:     */     else {
/*  852:1243 */       setRunColumn(-1);
/*  853:     */     }
/*  854:1246 */     String foldStr = Utils.getOption('F', options);
/*  855:1247 */     if (foldStr.length() != 0) {
/*  856:1248 */       setFoldColumn(Integer.parseInt(foldStr) - 1);
/*  857:     */     } else {
/*  858:1250 */       setFoldColumn(-1);
/*  859:     */     }
/*  860:1253 */     String sigStr = Utils.getOption('S', options);
/*  861:1254 */     if (sigStr.length() != 0) {
/*  862:1255 */       setSignificanceLevel(new Double(sigStr).doubleValue());
/*  863:     */     } else {
/*  864:1257 */       setSignificanceLevel(0.05D);
/*  865:     */     }
/*  866:1260 */     String resultsetList = Utils.getOption('G', options);
/*  867:1261 */     Range generatorRange = new Range();
/*  868:1262 */     if (resultsetList.length() != 0) {
/*  869:1263 */       generatorRange.setRanges(resultsetList);
/*  870:     */     }
/*  871:1265 */     setResultsetKeyColumns(generatorRange);
/*  872:     */   }
/*  873:     */   
/*  874:     */   public String[] getOptions()
/*  875:     */   {
/*  876:1276 */     Vector<String> options = new Vector();
/*  877:1278 */     if (!getResultsetKeyColumns().getRanges().equals(""))
/*  878:     */     {
/*  879:1279 */       options.add("-G");
/*  880:1280 */       options.add(getResultsetKeyColumns().getRanges());
/*  881:     */     }
/*  882:1282 */     if (!getDatasetKeyColumns().getRanges().equals(""))
/*  883:     */     {
/*  884:1283 */       options.add("-D");
/*  885:1284 */       options.add(getDatasetKeyColumns().getRanges());
/*  886:     */     }
/*  887:1286 */     options.add("-R");
/*  888:1287 */     options.add("" + (getRunColumn() + 1));
/*  889:1288 */     options.add("-S");
/*  890:1289 */     options.add("" + getSignificanceLevel());
/*  891:1291 */     if (getShowStdDevs()) {
/*  892:1292 */       options.add("-V");
/*  893:     */     }
/*  894:1295 */     options.add("-result-matrix");
/*  895:1296 */     String spec = getResultMatrix().getClass().getName();
/*  896:1297 */     if ((getResultMatrix() instanceof OptionHandler)) {
/*  897:1298 */       spec = spec + " " + Utils.joinOptions(getResultMatrix().getOptions());
/*  898:     */     }
/*  899:1300 */     options.add(spec.trim());
/*  900:     */     
/*  901:1302 */     return (String[])options.toArray(new String[options.size()]);
/*  902:     */   }
/*  903:     */   
/*  904:     */   public Range getResultsetKeyColumns()
/*  905:     */   {
/*  906:1313 */     return this.m_ResultsetKeyColumnsRange;
/*  907:     */   }
/*  908:     */   
/*  909:     */   public void setResultsetKeyColumns(Range newResultsetKeyColumns)
/*  910:     */   {
/*  911:1324 */     this.m_ResultsetKeyColumnsRange = newResultsetKeyColumns;
/*  912:1325 */     this.m_ResultsetsValid = false;
/*  913:     */   }
/*  914:     */   
/*  915:     */   public int[] getDisplayedResultsets()
/*  916:     */   {
/*  917:1336 */     return this.m_DisplayedResultsets;
/*  918:     */   }
/*  919:     */   
/*  920:     */   public void setDisplayedResultsets(int[] cols)
/*  921:     */   {
/*  922:1347 */     this.m_DisplayedResultsets = cols;
/*  923:     */   }
/*  924:     */   
/*  925:     */   public double getSignificanceLevel()
/*  926:     */   {
/*  927:1358 */     return this.m_SignificanceLevel;
/*  928:     */   }
/*  929:     */   
/*  930:     */   public void setSignificanceLevel(double newSignificanceLevel)
/*  931:     */   {
/*  932:1369 */     this.m_SignificanceLevel = newSignificanceLevel;
/*  933:     */   }
/*  934:     */   
/*  935:     */   public Range getDatasetKeyColumns()
/*  936:     */   {
/*  937:1380 */     return this.m_DatasetKeyColumnsRange;
/*  938:     */   }
/*  939:     */   
/*  940:     */   public void setDatasetKeyColumns(Range newDatasetKeyColumns)
/*  941:     */   {
/*  942:1391 */     this.m_DatasetKeyColumnsRange = newDatasetKeyColumns;
/*  943:1392 */     this.m_ResultsetsValid = false;
/*  944:     */   }
/*  945:     */   
/*  946:     */   public int getRunColumn()
/*  947:     */   {
/*  948:1403 */     return this.m_RunColumnSet;
/*  949:     */   }
/*  950:     */   
/*  951:     */   public void setRunColumn(int newRunColumn)
/*  952:     */   {
/*  953:1414 */     this.m_RunColumnSet = newRunColumn;
/*  954:1415 */     this.m_ResultsetsValid = false;
/*  955:     */   }
/*  956:     */   
/*  957:     */   public int getFoldColumn()
/*  958:     */   {
/*  959:1426 */     return this.m_FoldColumn;
/*  960:     */   }
/*  961:     */   
/*  962:     */   public void setFoldColumn(int newFoldColumn)
/*  963:     */   {
/*  964:1437 */     this.m_FoldColumn = newFoldColumn;
/*  965:1438 */     this.m_ResultsetsValid = false;
/*  966:     */   }
/*  967:     */   
/*  968:     */   public String getSortColumnName()
/*  969:     */   {
/*  970:1448 */     if (getSortColumn() == -1) {
/*  971:1449 */       return "-";
/*  972:     */     }
/*  973:1451 */     return this.m_Instances.attribute(getSortColumn()).name();
/*  974:     */   }
/*  975:     */   
/*  976:     */   public int getSortColumn()
/*  977:     */   {
/*  978:1462 */     return this.m_SortColumn;
/*  979:     */   }
/*  980:     */   
/*  981:     */   public void setSortColumn(int newSortColumn)
/*  982:     */   {
/*  983:1472 */     if (newSortColumn >= -1) {
/*  984:1473 */       this.m_SortColumn = newSortColumn;
/*  985:     */     }
/*  986:     */   }
/*  987:     */   
/*  988:     */   public Instances getInstances()
/*  989:     */   {
/*  990:1485 */     return this.m_Instances;
/*  991:     */   }
/*  992:     */   
/*  993:     */   public void setInstances(Instances newInstances)
/*  994:     */   {
/*  995:1496 */     this.m_Instances = newInstances;
/*  996:1497 */     this.m_ResultsetsValid = false;
/*  997:     */   }
/*  998:     */   
/*  999:     */   public void assign(Tester tester)
/* 1000:     */   {
/* 1001:1507 */     setInstances(tester.getInstances());
/* 1002:1508 */     setResultMatrix(tester.getResultMatrix());
/* 1003:1509 */     setShowStdDevs(tester.getShowStdDevs());
/* 1004:1510 */     setResultsetKeyColumns(tester.getResultsetKeyColumns());
/* 1005:1511 */     setDisplayedResultsets(tester.getDisplayedResultsets());
/* 1006:1512 */     setSignificanceLevel(tester.getSignificanceLevel());
/* 1007:1513 */     setDatasetKeyColumns(tester.getDatasetKeyColumns());
/* 1008:1514 */     setRunColumn(tester.getRunColumn());
/* 1009:1515 */     setFoldColumn(tester.getFoldColumn());
/* 1010:1516 */     setSortColumn(tester.getSortColumn());
/* 1011:     */   }
/* 1012:     */   
/* 1013:     */   public String getToolTipText()
/* 1014:     */   {
/* 1015:1527 */     return "Performs test using t-test statistic";
/* 1016:     */   }
/* 1017:     */   
/* 1018:     */   public String getDisplayName()
/* 1019:     */   {
/* 1020:1537 */     return "Paired T-Tester";
/* 1021:     */   }
/* 1022:     */   
/* 1023:     */   public String getRevision()
/* 1024:     */   {
/* 1025:1547 */     return RevisionUtils.extract("$Revision: 11542 $");
/* 1026:     */   }
/* 1027:     */   
/* 1028:     */   public static void main(String[] args)
/* 1029:     */   {
/* 1030:     */     try
/* 1031:     */     {
/* 1032:1558 */       PairedTTester tt = new PairedTTester();
/* 1033:1559 */       String datasetName = Utils.getOption('t', args);
/* 1034:1560 */       String compareColStr = Utils.getOption('c', args);
/* 1035:1561 */       String baseColStr = Utils.getOption('b', args);
/* 1036:1562 */       boolean summaryOnly = Utils.getFlag('s', args);
/* 1037:1563 */       boolean rankingOnly = Utils.getFlag('r', args);
/* 1038:1564 */       boolean noHeader = Utils.getFlag('n', args);
/* 1039:     */       try
/* 1040:     */       {
/* 1041:1566 */         if ((datasetName.length() == 0) || (compareColStr.length() == 0)) {
/* 1042:1567 */           throw new Exception("-t and -c options are required");
/* 1043:     */         }
/* 1044:1569 */         tt.setOptions(args);
/* 1045:1570 */         Utils.checkForRemainingOptions(args);
/* 1046:     */       }
/* 1047:     */       catch (Exception ex)
/* 1048:     */       {
/* 1049:1572 */         String result = "";
/* 1050:1573 */         Enumeration<Option> enu = tt.listOptions();
/* 1051:1574 */         while (enu.hasMoreElements())
/* 1052:     */         {
/* 1053:1575 */           Option option = (Option)enu.nextElement();
/* 1054:1576 */           result = result + option.synopsis() + '\n' + option.description() + '\n';
/* 1055:     */         }
/* 1056:1578 */         throw new Exception(ex.getMessage() + "\n\nUsage:\n\n" + "-t <file>\n" + "\tSet the dataset containing data to evaluate\n" + "-b <index>\n" + "\tSet the resultset to base comparisons against (optional)\n" + "-c <index>\n" + "\tSet the column to perform a comparison on\n" + "-s\n" + "\tSummarize wins over all resultset pairs\n" + "-r\n" + "\tGenerate a resultset ranking\n" + "-n\n" + "\tDo not output header info\n" + result);
/* 1057:     */       }
/* 1058:1585 */       Instances data = new Instances(new BufferedReader(new FileReader(datasetName)));
/* 1059:     */       
/* 1060:1587 */       tt.setInstances(data);
/* 1061:     */       
/* 1062:1589 */       int compareCol = Integer.parseInt(compareColStr) - 1;
/* 1063:1590 */       if (!noHeader) {
/* 1064:1591 */         System.out.println(tt.header(compareCol));
/* 1065:     */       }
/* 1066:1593 */       if (rankingOnly)
/* 1067:     */       {
/* 1068:1594 */         System.out.println(tt.multiResultsetRanking(compareCol));
/* 1069:     */       }
/* 1070:1595 */       else if (summaryOnly)
/* 1071:     */       {
/* 1072:1596 */         System.out.println(tt.multiResultsetSummary(compareCol));
/* 1073:     */       }
/* 1074:1599 */       else if (baseColStr.length() == 0)
/* 1075:     */       {
/* 1076:1600 */         for (int i = 0; i < tt.getNumResultsets(); i++) {
/* 1077:1601 */           if (tt.displayResultset(i)) {
/* 1078:1604 */             System.out.println(tt.multiResultsetFull(i, compareCol));
/* 1079:     */           }
/* 1080:     */         }
/* 1081:     */       }
/* 1082:     */       else
/* 1083:     */       {
/* 1084:1607 */         int baseCol = Integer.parseInt(baseColStr) - 1;
/* 1085:1608 */         System.out.println(tt.multiResultsetFull(baseCol, compareCol));
/* 1086:     */       }
/* 1087:     */     }
/* 1088:     */     catch (Exception e)
/* 1089:     */     {
/* 1090:1612 */       System.err.println(e.getMessage());
/* 1091:     */     }
/* 1092:     */   }
/* 1093:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.PairedTTester
 * JD-Core Version:    0.7.0.1
 */