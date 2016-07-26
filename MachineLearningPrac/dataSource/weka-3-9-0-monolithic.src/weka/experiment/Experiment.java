/*    1:     */ package weka.experiment;
/*    2:     */ 
/*    3:     */ import java.beans.PropertyDescriptor;
/*    4:     */ import java.io.BufferedInputStream;
/*    5:     */ import java.io.BufferedOutputStream;
/*    6:     */ import java.io.File;
/*    7:     */ import java.io.FileInputStream;
/*    8:     */ import java.io.FileOutputStream;
/*    9:     */ import java.io.ObjectInputStream;
/*   10:     */ import java.io.ObjectOutputStream;
/*   11:     */ import java.io.PrintStream;
/*   12:     */ import java.io.Serializable;
/*   13:     */ import java.lang.reflect.Array;
/*   14:     */ import java.lang.reflect.Method;
/*   15:     */ import java.util.ArrayList;
/*   16:     */ import java.util.Collections;
/*   17:     */ import java.util.Enumeration;
/*   18:     */ import java.util.Vector;
/*   19:     */ import javax.swing.DefaultListModel;
/*   20:     */ import weka.core.AdditionalMeasureProducer;
/*   21:     */ import weka.core.Instances;
/*   22:     */ import weka.core.Option;
/*   23:     */ import weka.core.OptionHandler;
/*   24:     */ import weka.core.RevisionHandler;
/*   25:     */ import weka.core.RevisionUtils;
/*   26:     */ import weka.core.Utils;
/*   27:     */ import weka.core.WekaPackageManager;
/*   28:     */ import weka.core.converters.AbstractFileLoader;
/*   29:     */ import weka.core.converters.ConverterUtils;
/*   30:     */ import weka.core.xml.KOML;
/*   31:     */ import weka.core.xml.XMLOptions;
/*   32:     */ import weka.experiment.xml.XMLExperiment;
/*   33:     */ 
/*   34:     */ public class Experiment
/*   35:     */   implements Serializable, OptionHandler, RevisionHandler
/*   36:     */ {
/*   37:     */   static final long serialVersionUID = 44945596742646663L;
/*   38: 188 */   public static String FILE_EXTENSION = ".exp";
/*   39: 191 */   protected ResultListener m_ResultListener = new InstancesResultListener();
/*   40: 194 */   protected ResultProducer m_ResultProducer = new RandomSplitResultProducer();
/*   41: 197 */   protected int m_RunLower = 1;
/*   42: 200 */   protected int m_RunUpper = 10;
/*   43: 203 */   protected DefaultListModel m_Datasets = new DefaultListModel();
/*   44: 206 */   protected boolean m_UsePropertyIterator = false;
/*   45:     */   protected PropertyNode[] m_PropertyPath;
/*   46:     */   protected Object m_PropertyArray;
/*   47: 215 */   protected String m_Notes = "";
/*   48: 222 */   protected String[] m_AdditionalMeasures = null;
/*   49: 228 */   protected boolean m_ClassFirst = false;
/*   50: 234 */   protected boolean m_AdvanceDataSetFirst = true;
/*   51:     */   protected transient int m_RunNumber;
/*   52:     */   protected transient int m_DatasetNumber;
/*   53:     */   protected transient int m_PropertyNumber;
/*   54:     */   
/*   55:     */   public void classFirst(boolean flag)
/*   56:     */   {
/*   57: 245 */     this.m_ClassFirst = flag;
/*   58:     */   }
/*   59:     */   
/*   60:     */   public boolean getAdvanceDataSetFirst()
/*   61:     */   {
/*   62: 255 */     return this.m_AdvanceDataSetFirst;
/*   63:     */   }
/*   64:     */   
/*   65:     */   public void setAdvanceDataSetFirst(boolean newAdvanceDataSetFirst)
/*   66:     */   {
/*   67: 265 */     this.m_AdvanceDataSetFirst = newAdvanceDataSetFirst;
/*   68:     */   }
/*   69:     */   
/*   70:     */   public boolean getUsePropertyIterator()
/*   71:     */   {
/*   72: 275 */     return this.m_UsePropertyIterator;
/*   73:     */   }
/*   74:     */   
/*   75:     */   public void setUsePropertyIterator(boolean newUsePropertyIterator)
/*   76:     */   {
/*   77: 285 */     this.m_UsePropertyIterator = newUsePropertyIterator;
/*   78:     */   }
/*   79:     */   
/*   80:     */   public PropertyNode[] getPropertyPath()
/*   81:     */   {
/*   82: 296 */     return this.m_PropertyPath;
/*   83:     */   }
/*   84:     */   
/*   85:     */   public void setPropertyPath(PropertyNode[] newPropertyPath)
/*   86:     */   {
/*   87: 307 */     this.m_PropertyPath = newPropertyPath;
/*   88:     */   }
/*   89:     */   
/*   90:     */   public void setPropertyArray(Object newPropArray)
/*   91:     */   {
/*   92: 318 */     this.m_PropertyArray = newPropArray;
/*   93:     */   }
/*   94:     */   
/*   95:     */   public Object getPropertyArray()
/*   96:     */   {
/*   97: 329 */     return this.m_PropertyArray;
/*   98:     */   }
/*   99:     */   
/*  100:     */   public int getPropertyArrayLength()
/*  101:     */   {
/*  102: 340 */     return Array.getLength(this.m_PropertyArray);
/*  103:     */   }
/*  104:     */   
/*  105:     */   public Object getPropertyArrayValue(int index)
/*  106:     */   {
/*  107: 351 */     return Array.get(this.m_PropertyArray, index);
/*  108:     */   }
/*  109:     */   
/*  110: 365 */   protected transient boolean m_Finished = true;
/*  111:     */   protected transient Instances m_CurrentInstances;
/*  112:     */   protected transient int m_CurrentProperty;
/*  113:     */   
/*  114:     */   public int getCurrentRunNumber()
/*  115:     */   {
/*  116: 377 */     return this.m_RunNumber;
/*  117:     */   }
/*  118:     */   
/*  119:     */   public int getCurrentDatasetNumber()
/*  120:     */   {
/*  121: 386 */     return this.m_DatasetNumber;
/*  122:     */   }
/*  123:     */   
/*  124:     */   public int getCurrentPropertyNumber()
/*  125:     */   {
/*  126: 396 */     return this.m_PropertyNumber;
/*  127:     */   }
/*  128:     */   
/*  129:     */   public void initialize()
/*  130:     */     throws Exception
/*  131:     */   {
/*  132: 406 */     this.m_RunNumber = getRunLower();
/*  133: 407 */     this.m_DatasetNumber = 0;
/*  134: 408 */     this.m_PropertyNumber = 0;
/*  135: 409 */     this.m_CurrentProperty = -1;
/*  136: 410 */     this.m_CurrentInstances = null;
/*  137: 411 */     this.m_Finished = false;
/*  138: 412 */     if ((this.m_UsePropertyIterator) && (this.m_PropertyArray == null)) {
/*  139: 413 */       throw new Exception("Null array for property iterator");
/*  140:     */     }
/*  141: 415 */     if (getRunLower() > getRunUpper()) {
/*  142: 416 */       throw new Exception("Lower run number is greater than upper run number");
/*  143:     */     }
/*  144: 418 */     if (getDatasets().size() == 0) {
/*  145: 419 */       throw new Exception("No datasets have been specified");
/*  146:     */     }
/*  147: 421 */     if (this.m_ResultProducer == null) {
/*  148: 422 */       throw new Exception("No ResultProducer set");
/*  149:     */     }
/*  150: 424 */     if (this.m_ResultListener == null) {
/*  151: 425 */       throw new Exception("No ResultListener set");
/*  152:     */     }
/*  153: 429 */     determineAdditionalResultMeasures();
/*  154:     */     
/*  155:     */ 
/*  156: 432 */     this.m_ResultProducer.setResultListener(this.m_ResultListener);
/*  157: 433 */     this.m_ResultProducer.setAdditionalMeasures(this.m_AdditionalMeasures);
/*  158: 434 */     this.m_ResultProducer.preProcess();
/*  159:     */     
/*  160:     */ 
/*  161:     */ 
/*  162: 438 */     String[] columnConstraints = this.m_ResultListener.determineColumnConstraints(this.m_ResultProducer);
/*  163: 441 */     if (columnConstraints != null) {
/*  164: 442 */       this.m_ResultProducer.setAdditionalMeasures(columnConstraints);
/*  165:     */     }
/*  166:     */   }
/*  167:     */   
/*  168:     */   private void determineAdditionalResultMeasures()
/*  169:     */     throws Exception
/*  170:     */   {
/*  171: 454 */     this.m_AdditionalMeasures = null;
/*  172: 455 */     ArrayList<String> measureNames = new ArrayList();
/*  173: 458 */     if ((this.m_ResultProducer instanceof AdditionalMeasureProducer))
/*  174:     */     {
/*  175: 459 */       Enumeration<String> am = ((AdditionalMeasureProducer)this.m_ResultProducer).enumerateMeasures();
/*  176: 461 */       while (am.hasMoreElements())
/*  177:     */       {
/*  178: 462 */         String mname = (String)am.nextElement();
/*  179: 463 */         if (mname.startsWith("measure"))
/*  180:     */         {
/*  181: 464 */           if (measureNames.indexOf(mname) == -1) {
/*  182: 465 */             measureNames.add(mname);
/*  183:     */           }
/*  184:     */         }
/*  185:     */         else {
/*  186: 468 */           throw new Exception("Additional measures in " + this.m_ResultProducer.getClass().getName() + " must obey the naming convention" + " of starting with \"measure\"");
/*  187:     */         }
/*  188:     */       }
/*  189:     */     }
/*  190: 476 */     if ((this.m_UsePropertyIterator) && (this.m_PropertyArray != null)) {
/*  191: 477 */       for (int i = 0; i < Array.getLength(this.m_PropertyArray); i++)
/*  192:     */       {
/*  193: 478 */         Object current = Array.get(this.m_PropertyArray, i);
/*  194: 480 */         if ((current instanceof AdditionalMeasureProducer))
/*  195:     */         {
/*  196: 481 */           Enumeration<String> am = ((AdditionalMeasureProducer)current).enumerateMeasures();
/*  197: 483 */           while (am.hasMoreElements())
/*  198:     */           {
/*  199: 484 */             String mname = (String)am.nextElement();
/*  200: 485 */             if (mname.startsWith("measure"))
/*  201:     */             {
/*  202: 486 */               if (measureNames.indexOf(mname) == -1) {
/*  203: 487 */                 measureNames.add(mname);
/*  204:     */               }
/*  205:     */             }
/*  206:     */             else {
/*  207: 490 */               throw new Exception("Additional measures in " + current.getClass().getName() + " must obey the naming convention" + " of starting with \"measure\"");
/*  208:     */             }
/*  209:     */           }
/*  210:     */         }
/*  211:     */       }
/*  212:     */     }
/*  213: 499 */     if (measureNames.size() > 0)
/*  214:     */     {
/*  215: 500 */       this.m_AdditionalMeasures = new String[measureNames.size()];
/*  216: 501 */       for (int i = 0; i < measureNames.size(); i++) {
/*  217: 502 */         this.m_AdditionalMeasures[i] = ((String)measureNames.get(i));
/*  218:     */       }
/*  219:     */     }
/*  220:     */   }
/*  221:     */   
/*  222:     */   protected void setProperty(int propertyDepth, Object origValue)
/*  223:     */     throws Exception
/*  224:     */   {
/*  225: 518 */     PropertyDescriptor current = this.m_PropertyPath[propertyDepth].property;
/*  226: 519 */     Object subVal = null;
/*  227: 520 */     if (propertyDepth < this.m_PropertyPath.length - 1)
/*  228:     */     {
/*  229: 521 */       Method getter = current.getReadMethod();
/*  230: 522 */       Object[] getArgs = new Object[0];
/*  231: 523 */       subVal = getter.invoke(origValue, getArgs);
/*  232: 524 */       setProperty(propertyDepth + 1, subVal);
/*  233:     */     }
/*  234:     */     else
/*  235:     */     {
/*  236: 526 */       subVal = Array.get(this.m_PropertyArray, this.m_PropertyNumber);
/*  237:     */     }
/*  238: 528 */     Method setter = current.getWriteMethod();
/*  239: 529 */     Object[] args = { subVal };
/*  240: 530 */     setter.invoke(origValue, args);
/*  241:     */   }
/*  242:     */   
/*  243:     */   public boolean hasMoreIterations()
/*  244:     */   {
/*  245: 540 */     return !this.m_Finished;
/*  246:     */   }
/*  247:     */   
/*  248:     */   public void nextIteration()
/*  249:     */     throws Exception
/*  250:     */   {
/*  251: 550 */     if ((this.m_UsePropertyIterator) && 
/*  252: 551 */       (this.m_CurrentProperty != this.m_PropertyNumber))
/*  253:     */     {
/*  254: 552 */       setProperty(0, this.m_ResultProducer);
/*  255: 553 */       this.m_CurrentProperty = this.m_PropertyNumber;
/*  256:     */     }
/*  257: 557 */     if (this.m_CurrentInstances == null)
/*  258:     */     {
/*  259: 558 */       File currentFile = (File)getDatasets().elementAt(this.m_DatasetNumber);
/*  260: 559 */       AbstractFileLoader loader = ConverterUtils.getLoaderForFile(currentFile);
/*  261: 560 */       loader.setFile(currentFile);
/*  262: 561 */       Instances data = new Instances(loader.getDataSet());
/*  263: 563 */       if (data.classIndex() == -1) {
/*  264: 564 */         if (this.m_ClassFirst) {
/*  265: 565 */           data.setClassIndex(0);
/*  266:     */         } else {
/*  267: 567 */           data.setClassIndex(data.numAttributes() - 1);
/*  268:     */         }
/*  269:     */       }
/*  270: 570 */       this.m_CurrentInstances = data;
/*  271: 571 */       this.m_ResultProducer.setInstances(this.m_CurrentInstances);
/*  272:     */     }
/*  273: 574 */     this.m_ResultProducer.doRun(this.m_RunNumber);
/*  274:     */     
/*  275: 576 */     advanceCounters();
/*  276:     */   }
/*  277:     */   
/*  278:     */   public void advanceCounters()
/*  279:     */   {
/*  280: 584 */     if (this.m_AdvanceDataSetFirst)
/*  281:     */     {
/*  282: 585 */       this.m_RunNumber += 1;
/*  283: 586 */       if (this.m_RunNumber > getRunUpper())
/*  284:     */       {
/*  285: 587 */         this.m_RunNumber = getRunLower();
/*  286: 588 */         this.m_DatasetNumber += 1;
/*  287: 589 */         this.m_CurrentInstances = null;
/*  288: 590 */         if (this.m_DatasetNumber >= getDatasets().size())
/*  289:     */         {
/*  290: 591 */           this.m_DatasetNumber = 0;
/*  291: 592 */           if (this.m_UsePropertyIterator)
/*  292:     */           {
/*  293: 593 */             this.m_PropertyNumber += 1;
/*  294: 594 */             if (this.m_PropertyNumber >= Array.getLength(this.m_PropertyArray)) {
/*  295: 595 */               this.m_Finished = true;
/*  296:     */             }
/*  297:     */           }
/*  298:     */           else
/*  299:     */           {
/*  300: 598 */             this.m_Finished = true;
/*  301:     */           }
/*  302:     */         }
/*  303:     */       }
/*  304:     */     }
/*  305:     */     else
/*  306:     */     {
/*  307: 603 */       this.m_RunNumber += 1;
/*  308: 604 */       if (this.m_RunNumber > getRunUpper())
/*  309:     */       {
/*  310: 605 */         this.m_RunNumber = getRunLower();
/*  311: 606 */         if (this.m_UsePropertyIterator)
/*  312:     */         {
/*  313: 607 */           this.m_PropertyNumber += 1;
/*  314: 608 */           if (this.m_PropertyNumber >= Array.getLength(this.m_PropertyArray))
/*  315:     */           {
/*  316: 609 */             this.m_PropertyNumber = 0;
/*  317: 610 */             this.m_DatasetNumber += 1;
/*  318: 611 */             this.m_CurrentInstances = null;
/*  319: 612 */             if (this.m_DatasetNumber >= getDatasets().size()) {
/*  320: 613 */               this.m_Finished = true;
/*  321:     */             }
/*  322:     */           }
/*  323:     */         }
/*  324:     */         else
/*  325:     */         {
/*  326: 617 */           this.m_DatasetNumber += 1;
/*  327: 618 */           this.m_CurrentInstances = null;
/*  328: 619 */           if (this.m_DatasetNumber >= getDatasets().size()) {
/*  329: 620 */             this.m_Finished = true;
/*  330:     */           }
/*  331:     */         }
/*  332:     */       }
/*  333:     */     }
/*  334:     */   }
/*  335:     */   
/*  336:     */   public void runExperiment(boolean verbose)
/*  337:     */   {
/*  338: 629 */     while (hasMoreIterations()) {
/*  339:     */       try
/*  340:     */       {
/*  341: 631 */         if (verbose)
/*  342:     */         {
/*  343: 632 */           String current = "Iteration:";
/*  344: 633 */           if (getUsePropertyIterator())
/*  345:     */           {
/*  346: 634 */             int cnum = getCurrentPropertyNumber();
/*  347: 635 */             String ctype = getPropertyArray().getClass().getComponentType().getName();
/*  348:     */             
/*  349: 637 */             int lastDot = ctype.lastIndexOf('.');
/*  350: 638 */             if (lastDot != -1) {
/*  351: 639 */               ctype = ctype.substring(lastDot + 1);
/*  352:     */             }
/*  353: 641 */             String cname = " " + ctype + "=" + (cnum + 1) + ":" + getPropertyArrayValue(cnum).getClass().getName();
/*  354:     */             
/*  355: 643 */             current = current + cname;
/*  356:     */           }
/*  357: 645 */           String dname = ((File)getDatasets().elementAt(getCurrentDatasetNumber())).getName();
/*  358:     */           
/*  359: 647 */           current = current + " Dataset=" + dname + " Run=" + getCurrentRunNumber();
/*  360:     */           
/*  361: 649 */           System.out.println(current);
/*  362:     */         }
/*  363: 652 */         nextIteration();
/*  364:     */       }
/*  365:     */       catch (Exception ex)
/*  366:     */       {
/*  367: 654 */         ex.printStackTrace();
/*  368: 655 */         System.err.println(ex.getMessage());
/*  369: 656 */         advanceCounters();
/*  370:     */       }
/*  371:     */     }
/*  372:     */   }
/*  373:     */   
/*  374:     */   public void runExperiment()
/*  375:     */   {
/*  376: 665 */     runExperiment(false);
/*  377:     */   }
/*  378:     */   
/*  379:     */   public void postProcess()
/*  380:     */     throws Exception
/*  381:     */   {
/*  382: 676 */     this.m_ResultProducer.postProcess();
/*  383:     */   }
/*  384:     */   
/*  385:     */   public DefaultListModel getDatasets()
/*  386:     */   {
/*  387: 685 */     return this.m_Datasets;
/*  388:     */   }
/*  389:     */   
/*  390:     */   public void setDatasets(DefaultListModel ds)
/*  391:     */   {
/*  392: 694 */     this.m_Datasets = ds;
/*  393:     */   }
/*  394:     */   
/*  395:     */   public ResultListener getResultListener()
/*  396:     */   {
/*  397: 704 */     return this.m_ResultListener;
/*  398:     */   }
/*  399:     */   
/*  400:     */   public void setResultListener(ResultListener newResultListener)
/*  401:     */   {
/*  402: 714 */     this.m_ResultListener = newResultListener;
/*  403:     */   }
/*  404:     */   
/*  405:     */   public ResultProducer getResultProducer()
/*  406:     */   {
/*  407: 724 */     return this.m_ResultProducer;
/*  408:     */   }
/*  409:     */   
/*  410:     */   public void setResultProducer(ResultProducer newResultProducer)
/*  411:     */   {
/*  412: 734 */     this.m_ResultProducer = newResultProducer;
/*  413:     */   }
/*  414:     */   
/*  415:     */   public int getRunUpper()
/*  416:     */   {
/*  417: 744 */     return this.m_RunUpper;
/*  418:     */   }
/*  419:     */   
/*  420:     */   public void setRunUpper(int newRunUpper)
/*  421:     */   {
/*  422: 754 */     this.m_RunUpper = newRunUpper;
/*  423:     */   }
/*  424:     */   
/*  425:     */   public int getRunLower()
/*  426:     */   {
/*  427: 764 */     return this.m_RunLower;
/*  428:     */   }
/*  429:     */   
/*  430:     */   public void setRunLower(int newRunLower)
/*  431:     */   {
/*  432: 774 */     this.m_RunLower = newRunLower;
/*  433:     */   }
/*  434:     */   
/*  435:     */   public String getNotes()
/*  436:     */   {
/*  437: 784 */     return this.m_Notes;
/*  438:     */   }
/*  439:     */   
/*  440:     */   public void setNotes(String newNotes)
/*  441:     */   {
/*  442: 794 */     this.m_Notes = newNotes;
/*  443:     */   }
/*  444:     */   
/*  445:     */   public Enumeration<Option> listOptions()
/*  446:     */   {
/*  447: 805 */     Vector<Option> newVector = new Vector(6);
/*  448:     */     
/*  449: 807 */     newVector.addElement(new Option("\tThe lower run number to start the experiment from.\n\t(default 1)", "L", 1, "-L <num>"));
/*  450:     */     
/*  451:     */ 
/*  452: 810 */     newVector.addElement(new Option("\tThe upper run number to end the experiment at (inclusive).\n\t(default 10)", "U", 1, "-U <num>"));
/*  453:     */     
/*  454:     */ 
/*  455: 813 */     newVector.addElement(new Option("\tThe dataset to run the experiment on.\n\t(required, may be specified multiple times)", "T", 1, "-T <arff file>"));
/*  456:     */     
/*  457:     */ 
/*  458: 816 */     newVector.addElement(new Option("\tThe full class name of a ResultProducer (required).\n\teg: weka.experiment.RandomSplitResultProducer", "P", 1, "-P <class name>"));
/*  459:     */     
/*  460:     */ 
/*  461:     */ 
/*  462: 820 */     newVector.addElement(new Option("\tThe full class name of a ResultListener (required).\n\teg: weka.experiment.CSVResultListener", "D", 1, "-D <class name>"));
/*  463:     */     
/*  464:     */ 
/*  465:     */ 
/*  466:     */ 
/*  467: 825 */     newVector.addElement(new Option("\tA string containing any notes about the experiment.\n\t(default none)", "N", 1, "-N <string>"));
/*  468: 829 */     if ((this.m_ResultProducer != null) && ((this.m_ResultProducer instanceof OptionHandler)))
/*  469:     */     {
/*  470: 831 */       newVector.addElement(new Option("", "", 0, "\nOptions specific to result producer " + this.m_ResultProducer.getClass().getName() + ":"));
/*  471:     */       
/*  472:     */ 
/*  473: 834 */       newVector.addAll(Collections.list(((OptionHandler)this.m_ResultProducer).listOptions()));
/*  474:     */     }
/*  475: 837 */     return newVector.elements();
/*  476:     */   }
/*  477:     */   
/*  478:     */   public void setOptions(String[] options)
/*  479:     */     throws Exception
/*  480:     */   {
/*  481: 970 */     String lowerString = Utils.getOption('L', options);
/*  482: 971 */     if (lowerString.length() != 0) {
/*  483: 972 */       setRunLower(Integer.parseInt(lowerString));
/*  484:     */     } else {
/*  485: 974 */       setRunLower(1);
/*  486:     */     }
/*  487: 976 */     String upperString = Utils.getOption('U', options);
/*  488: 977 */     if (upperString.length() != 0) {
/*  489: 978 */       setRunUpper(Integer.parseInt(upperString));
/*  490:     */     } else {
/*  491: 980 */       setRunUpper(10);
/*  492:     */     }
/*  493: 982 */     if (getRunLower() > getRunUpper()) {
/*  494: 983 */       throw new Exception("Lower (" + getRunLower() + ") is greater than upper (" + getRunUpper() + ")");
/*  495:     */     }
/*  496: 987 */     setNotes(Utils.getOption('N', options));
/*  497:     */     
/*  498: 989 */     getDatasets().removeAllElements();
/*  499:     */     String dataName;
/*  500:     */     do
/*  501:     */     {
/*  502: 992 */       dataName = Utils.getOption('T', options);
/*  503: 993 */       if (dataName.length() != 0)
/*  504:     */       {
/*  505: 994 */         File dataset = new File(dataName);
/*  506: 995 */         getDatasets().addElement(dataset);
/*  507:     */       }
/*  508: 997 */     } while (dataName.length() != 0);
/*  509: 998 */     if (getDatasets().size() == 0) {
/*  510: 999 */       throw new Exception("Required: -T <arff file name>");
/*  511:     */     }
/*  512:1002 */     String rlName = Utils.getOption('D', options);
/*  513:1003 */     if (rlName.length() == 0) {
/*  514:1004 */       throw new Exception("Required: -D <ResultListener class name>");
/*  515:     */     }
/*  516:1006 */     rlName = rlName.trim();
/*  517:     */     
/*  518:1008 */     int breakLoc = rlName.indexOf(' ');
/*  519:1009 */     String clName = rlName;
/*  520:1010 */     String rlOptionsString = "";
/*  521:1011 */     String[] rlOptions = null;
/*  522:1012 */     if (breakLoc != -1)
/*  523:     */     {
/*  524:1013 */       clName = rlName.substring(0, breakLoc);
/*  525:1014 */       rlOptionsString = rlName.substring(breakLoc).trim();
/*  526:1015 */       rlOptions = Utils.splitOptions(rlOptionsString);
/*  527:     */     }
/*  528:1017 */     setResultListener((ResultListener)Utils.forName(ResultListener.class, clName, rlOptions));
/*  529:     */     
/*  530:     */ 
/*  531:1020 */     String rpName = Utils.getOption('P', options);
/*  532:1021 */     if (rpName.length() == 0) {
/*  533:1022 */       throw new Exception("Required: -P <ResultProducer class name>");
/*  534:     */     }
/*  535:1029 */     setResultProducer((ResultProducer)Utils.forName(ResultProducer.class, rpName, Utils.partitionOptions(options)));
/*  536:     */   }
/*  537:     */   
/*  538:     */   public String[] getOptions()
/*  539:     */   {
/*  540:1047 */     this.m_UsePropertyIterator = false;
/*  541:1048 */     this.m_PropertyPath = null;
/*  542:1049 */     this.m_PropertyArray = null;
/*  543:     */     
/*  544:1051 */     String[] rpOptions = new String[0];
/*  545:1052 */     if ((this.m_ResultProducer != null) && ((this.m_ResultProducer instanceof OptionHandler))) {
/*  546:1054 */       rpOptions = ((OptionHandler)this.m_ResultProducer).getOptions();
/*  547:     */     }
/*  548:1057 */     String[] options = new String[rpOptions.length + getDatasets().size() * 2 + 11];
/*  549:     */     
/*  550:1059 */     int current = 0;
/*  551:     */     
/*  552:1061 */     options[(current++)] = "-L";
/*  553:1062 */     options[(current++)] = ("" + getRunLower());
/*  554:1063 */     options[(current++)] = "-U";
/*  555:1064 */     options[(current++)] = ("" + getRunUpper());
/*  556:1065 */     if (getDatasets().size() != 0) {
/*  557:1066 */       for (int i = 0; i < getDatasets().size(); i++)
/*  558:     */       {
/*  559:1067 */         options[(current++)] = "-T";
/*  560:1068 */         options[(current++)] = getDatasets().elementAt(i).toString();
/*  561:     */       }
/*  562:     */     }
/*  563:1071 */     if (getResultListener() != null)
/*  564:     */     {
/*  565:1072 */       options[(current++)] = "-D";
/*  566:1073 */       options[(current++)] = getResultListener().getClass().getName();
/*  567:     */     }
/*  568:1075 */     if (getResultProducer() != null)
/*  569:     */     {
/*  570:1076 */       options[(current++)] = "-P";
/*  571:1077 */       options[(current++)] = getResultProducer().getClass().getName();
/*  572:     */     }
/*  573:1079 */     if (!getNotes().equals(""))
/*  574:     */     {
/*  575:1080 */       options[(current++)] = "-N";
/*  576:1081 */       options[(current++)] = getNotes();
/*  577:     */     }
/*  578:1083 */     options[(current++)] = "--";
/*  579:     */     
/*  580:1085 */     System.arraycopy(rpOptions, 0, options, current, rpOptions.length);
/*  581:1086 */     current += rpOptions.length;
/*  582:1087 */     while (current < options.length) {
/*  583:1088 */       options[(current++)] = "";
/*  584:     */     }
/*  585:1090 */     return options;
/*  586:     */   }
/*  587:     */   
/*  588:     */   public String toString()
/*  589:     */   {
/*  590:1101 */     String result = "Runs from: " + this.m_RunLower + " to: " + this.m_RunUpper + '\n';
/*  591:1102 */     result = result + "Datasets:";
/*  592:1103 */     for (int i = 0; i < this.m_Datasets.size(); i++) {
/*  593:1104 */       result = result + " " + this.m_Datasets.elementAt(i);
/*  594:     */     }
/*  595:1106 */     result = result + '\n';
/*  596:1107 */     result = result + "Custom property iterator: " + (this.m_UsePropertyIterator ? "on" : "off") + "\n";
/*  597:1109 */     if (this.m_UsePropertyIterator)
/*  598:     */     {
/*  599:1110 */       if (this.m_PropertyPath == null) {
/*  600:1111 */         throw new Error("*** null propertyPath ***");
/*  601:     */       }
/*  602:1113 */       if (this.m_PropertyArray == null) {
/*  603:1114 */         throw new Error("*** null propertyArray ***");
/*  604:     */       }
/*  605:1116 */       if (this.m_PropertyPath.length > 1)
/*  606:     */       {
/*  607:1117 */         result = result + "Custom property path:\n";
/*  608:1118 */         for (int i = 0; i < this.m_PropertyPath.length - 1; i++)
/*  609:     */         {
/*  610:1119 */           PropertyNode pn = this.m_PropertyPath[i];
/*  611:1120 */           result = result + "" + (i + 1) + "  " + pn.parentClass.getName() + "::" + pn.toString() + ' ' + pn.value.toString() + '\n';
/*  612:     */         }
/*  613:     */       }
/*  614:1124 */       result = result + "Custom property name:" + this.m_PropertyPath[(this.m_PropertyPath.length - 1)].toString() + '\n';
/*  615:     */       
/*  616:1126 */       result = result + "Custom property values:\n";
/*  617:1127 */       for (int i = 0; i < Array.getLength(this.m_PropertyArray); i++)
/*  618:     */       {
/*  619:1128 */         Object current = Array.get(this.m_PropertyArray, i);
/*  620:1129 */         result = result + " " + (i + 1) + " " + current.getClass().getName() + " " + current.toString() + '\n';
/*  621:     */       }
/*  622:     */     }
/*  623:1133 */     result = result + "ResultProducer: " + this.m_ResultProducer + '\n';
/*  624:1134 */     result = result + "ResultListener: " + this.m_ResultListener + '\n';
/*  625:1135 */     if (!getNotes().equals("")) {
/*  626:1136 */       result = result + "Notes: " + getNotes();
/*  627:     */     }
/*  628:1138 */     return result;
/*  629:     */   }
/*  630:     */   
/*  631:     */   public static Experiment read(String filename)
/*  632:     */     throws Exception
/*  633:     */   {
/*  634:     */     Experiment result;
/*  635:     */     Experiment result;
/*  636:1152 */     if ((KOML.isPresent()) && (filename.toLowerCase().endsWith(".koml")))
/*  637:     */     {
/*  638:1154 */       result = (Experiment)KOML.read(filename);
/*  639:     */     }
/*  640:     */     else
/*  641:     */     {
/*  642:     */       Experiment result;
/*  643:1157 */       if (filename.toLowerCase().endsWith(".xml"))
/*  644:     */       {
/*  645:1158 */         XMLExperiment xml = new XMLExperiment();
/*  646:1159 */         result = (Experiment)xml.read(filename);
/*  647:     */       }
/*  648:     */       else
/*  649:     */       {
/*  650:1163 */         FileInputStream fi = new FileInputStream(filename);
/*  651:1164 */         ObjectInputStream oi = new ObjectInputStream(new BufferedInputStream(fi));
/*  652:1165 */         result = (Experiment)oi.readObject();
/*  653:1166 */         oi.close();
/*  654:     */       }
/*  655:     */     }
/*  656:1169 */     return result;
/*  657:     */   }
/*  658:     */   
/*  659:     */   public static void write(String filename, Experiment exp)
/*  660:     */     throws Exception
/*  661:     */   {
/*  662:1181 */     if ((KOML.isPresent()) && (filename.toLowerCase().endsWith(".koml")))
/*  663:     */     {
/*  664:1183 */       KOML.write(filename, exp);
/*  665:     */     }
/*  666:1186 */     else if (filename.toLowerCase().endsWith(".xml"))
/*  667:     */     {
/*  668:1187 */       XMLExperiment xml = new XMLExperiment();
/*  669:1188 */       xml.write(filename, exp);
/*  670:     */     }
/*  671:     */     else
/*  672:     */     {
/*  673:1192 */       FileOutputStream fo = new FileOutputStream(filename);
/*  674:1193 */       ObjectOutputStream oo = new ObjectOutputStream(new BufferedOutputStream(fo));
/*  675:     */       
/*  676:1195 */       oo.writeObject(exp);
/*  677:1196 */       oo.close();
/*  678:     */     }
/*  679:     */   }
/*  680:     */   
/*  681:     */   public static void main(String[] args)
/*  682:     */   {
/*  683:     */     try
/*  684:     */     {
/*  685:1208 */       WekaPackageManager.loadPackages(false, true, false);
/*  686:1209 */       Experiment exp = null;
/*  687:     */       
/*  688:1211 */       String xmlOption = Utils.getOption("xml", args);
/*  689:1212 */       if (!xmlOption.equals("")) {
/*  690:1213 */         args = new XMLOptions(xmlOption).toArray();
/*  691:     */       }
/*  692:1216 */       String expFile = Utils.getOption('l', args);
/*  693:1217 */       String saveFile = Utils.getOption('s', args);
/*  694:1218 */       boolean runExp = Utils.getFlag('r', args);
/*  695:1219 */       boolean verbose = Utils.getFlag("verbose", args);
/*  696:1220 */       if (expFile.length() == 0)
/*  697:     */       {
/*  698:1221 */         exp = new Experiment();
/*  699:     */         try
/*  700:     */         {
/*  701:1223 */           exp.setOptions(args);
/*  702:1224 */           Utils.checkForRemainingOptions(args);
/*  703:     */         }
/*  704:     */         catch (Exception ex)
/*  705:     */         {
/*  706:1226 */           ex.printStackTrace();
/*  707:1227 */           String result = "Usage:\n\n-l <exp|xml file>\n\tLoad experiment from file (default use cli options).\n\tThe type is determined, based on the extension (" + FILE_EXTENSION + " or .xml)\n" + "-s <exp|xml file>\n" + "\tSave experiment to file after setting other options.\n" + "\tThe type is determined, based on the extension (" + FILE_EXTENSION + " or .xml)\n" + "\t(default don't save)\n" + "-r\n" + "\tRun experiment (default don't run)\n" + "-xml <filename | xml-string>\n" + "\tget options from XML-Data instead from parameters.\n" + "-verbose\n" + "\toutput progress information to std out." + "\n";
/*  708:     */           
/*  709:     */ 
/*  710:     */ 
/*  711:     */ 
/*  712:     */ 
/*  713:     */ 
/*  714:     */ 
/*  715:     */ 
/*  716:     */ 
/*  717:     */ 
/*  718:1238 */           Enumeration<Option> enm = exp.listOptions();
/*  719:1239 */           while (enm.hasMoreElements())
/*  720:     */           {
/*  721:1240 */             Option option = (Option)enm.nextElement();
/*  722:1241 */             result = result + option.synopsis() + "\n";
/*  723:1242 */             result = result + option.description() + "\n";
/*  724:     */           }
/*  725:1244 */           throw new Exception(result + "\n" + ex.getMessage());
/*  726:     */         }
/*  727:     */       }
/*  728:     */       else
/*  729:     */       {
/*  730:1247 */         exp = read(expFile);
/*  731:1249 */         if ((exp instanceof RemoteExperiment)) {
/*  732:1250 */           throw new Exception("Cannot run remote experiment using Experiment class. Use RemoteExperiment class instead!");
/*  733:     */         }
/*  734:     */         String dataName;
/*  735:     */         do
/*  736:     */         {
/*  737:1257 */           dataName = Utils.getOption('T', args);
/*  738:1258 */           if (dataName.length() != 0)
/*  739:     */           {
/*  740:1259 */             File dataset = new File(dataName);
/*  741:1260 */             exp.getDatasets().addElement(dataset);
/*  742:     */           }
/*  743:1262 */         } while (dataName.length() != 0);
/*  744:     */       }
/*  745:1265 */       System.err.println("Experiment:\n" + exp.toString());
/*  746:1267 */       if (saveFile.length() != 0) {
/*  747:1268 */         write(saveFile, exp);
/*  748:     */       }
/*  749:1271 */       if (runExp)
/*  750:     */       {
/*  751:1272 */         System.err.println("Initializing...");
/*  752:1273 */         exp.initialize();
/*  753:1274 */         System.err.println("Iterating...");
/*  754:1275 */         exp.runExperiment(verbose);
/*  755:1276 */         System.err.println("Postprocessing...");
/*  756:1277 */         exp.postProcess();
/*  757:     */       }
/*  758:     */     }
/*  759:     */     catch (Exception ex)
/*  760:     */     {
/*  761:1281 */       System.err.println(ex.getMessage());
/*  762:     */     }
/*  763:     */   }
/*  764:     */   
/*  765:     */   public String getRevision()
/*  766:     */   {
/*  767:1292 */     return RevisionUtils.extract("$Revision: 12564 $");
/*  768:     */   }
/*  769:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.experiment.Experiment
 * JD-Core Version:    0.7.0.1
 */