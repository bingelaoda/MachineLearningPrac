/*    1:     */ package weka.gui.beans;
/*    2:     */ 
/*    3:     */ import java.awt.BorderLayout;
/*    4:     */ import java.beans.EventSetDescriptor;
/*    5:     */ import java.io.PrintStream;
/*    6:     */ import java.io.Serializable;
/*    7:     */ import java.util.ArrayList;
/*    8:     */ import java.util.Enumeration;
/*    9:     */ import java.util.List;
/*   10:     */ import java.util.Vector;
/*   11:     */ import javax.swing.JPanel;
/*   12:     */ import weka.classifiers.Classifier;
/*   13:     */ import weka.classifiers.misc.InputMappedClassifier;
/*   14:     */ import weka.clusterers.Clusterer;
/*   15:     */ import weka.clusterers.DensityBasedClusterer;
/*   16:     */ import weka.core.Attribute;
/*   17:     */ import weka.core.DenseInstance;
/*   18:     */ import weka.core.Instance;
/*   19:     */ import weka.core.Instances;
/*   20:     */ import weka.filters.Filter;
/*   21:     */ import weka.filters.unsupervised.attribute.Add;
/*   22:     */ import weka.gui.Logger;
/*   23:     */ 
/*   24:     */ public class PredictionAppender
/*   25:     */   extends JPanel
/*   26:     */   implements DataSource, TrainingSetProducer, TestSetProducer, Visible, BeanCommon, EventConstraints, BatchClassifierListener, IncrementalClassifierListener, BatchClustererListener, Serializable
/*   27:     */ {
/*   28:     */   private static final long serialVersionUID = -2987740065058976673L;
/*   29:  57 */   protected Vector<DataSourceListener> m_dataSourceListeners = new Vector();
/*   30:  63 */   protected Vector<InstanceListener> m_instanceListeners = new Vector();
/*   31:  69 */   protected Vector<TrainingSetListener> m_trainingSetListeners = new Vector();
/*   32:  75 */   protected Vector<TestSetListener> m_testSetListeners = new Vector();
/*   33:  81 */   protected Object m_listenee = null;
/*   34:     */   protected Instances m_format;
/*   35:  88 */   protected BeanVisual m_visual = new BeanVisual("PredictionAppender", "weka/gui/beans/icons/PredictionAppender.gif", "weka/gui/beans/icons/PredictionAppender_animated.gif");
/*   36:     */   protected boolean m_appendProbabilities;
/*   37:     */   protected transient Logger m_logger;
/*   38:     */   protected transient List<Integer> m_stringAttIndexes;
/*   39:     */   protected InstanceEvent m_instanceEvent;
/*   40:     */   protected transient StreamThroughput m_throughput;
/*   41:     */   
/*   42:     */   public String globalInfo()
/*   43:     */   {
/*   44: 108 */     return "Accepts batch or incremental classifier events and produces a new data set with classifier predictions appended.";
/*   45:     */   }
/*   46:     */   
/*   47:     */   public PredictionAppender()
/*   48:     */   {
/*   49: 116 */     setLayout(new BorderLayout());
/*   50: 117 */     add(this.m_visual, "Center");
/*   51:     */   }
/*   52:     */   
/*   53:     */   public void setCustomName(String name)
/*   54:     */   {
/*   55: 127 */     this.m_visual.setText(name);
/*   56:     */   }
/*   57:     */   
/*   58:     */   public String getCustomName()
/*   59:     */   {
/*   60: 137 */     return this.m_visual.getText();
/*   61:     */   }
/*   62:     */   
/*   63:     */   public String appendPredictedProbabilitiesTipText()
/*   64:     */   {
/*   65: 146 */     return "append probabilities rather than labels for discrete class predictions";
/*   66:     */   }
/*   67:     */   
/*   68:     */   public boolean getAppendPredictedProbabilities()
/*   69:     */   {
/*   70: 157 */     return this.m_appendProbabilities;
/*   71:     */   }
/*   72:     */   
/*   73:     */   public void setAppendPredictedProbabilities(boolean ap)
/*   74:     */   {
/*   75: 167 */     this.m_appendProbabilities = ap;
/*   76:     */   }
/*   77:     */   
/*   78:     */   public void addTrainingSetListener(TrainingSetListener tsl)
/*   79:     */   {
/*   80: 178 */     this.m_trainingSetListeners.addElement(tsl);
/*   81: 180 */     if (this.m_format != null)
/*   82:     */     {
/*   83: 181 */       TrainingSetEvent e = new TrainingSetEvent(this, this.m_format);
/*   84: 182 */       tsl.acceptTrainingSet(e);
/*   85:     */     }
/*   86:     */   }
/*   87:     */   
/*   88:     */   public void removeTrainingSetListener(TrainingSetListener tsl)
/*   89:     */   {
/*   90: 193 */     this.m_trainingSetListeners.removeElement(tsl);
/*   91:     */   }
/*   92:     */   
/*   93:     */   public void addTestSetListener(TestSetListener tsl)
/*   94:     */   {
/*   95: 203 */     this.m_testSetListeners.addElement(tsl);
/*   96: 205 */     if (this.m_format != null)
/*   97:     */     {
/*   98: 206 */       TestSetEvent e = new TestSetEvent(this, this.m_format);
/*   99: 207 */       tsl.acceptTestSet(e);
/*  100:     */     }
/*  101:     */   }
/*  102:     */   
/*  103:     */   public void removeTestSetListener(TestSetListener tsl)
/*  104:     */   {
/*  105: 218 */     this.m_testSetListeners.removeElement(tsl);
/*  106:     */   }
/*  107:     */   
/*  108:     */   public synchronized void addDataSourceListener(DataSourceListener dsl)
/*  109:     */   {
/*  110: 228 */     this.m_dataSourceListeners.addElement(dsl);
/*  111: 230 */     if (this.m_format != null)
/*  112:     */     {
/*  113: 231 */       DataSetEvent e = new DataSetEvent(this, this.m_format);
/*  114: 232 */       dsl.acceptDataSet(e);
/*  115:     */     }
/*  116:     */   }
/*  117:     */   
/*  118:     */   public synchronized void removeDataSourceListener(DataSourceListener dsl)
/*  119:     */   {
/*  120: 243 */     this.m_dataSourceListeners.remove(dsl);
/*  121:     */   }
/*  122:     */   
/*  123:     */   public synchronized void addInstanceListener(InstanceListener dsl)
/*  124:     */   {
/*  125: 253 */     this.m_instanceListeners.addElement(dsl);
/*  126: 255 */     if (this.m_format != null)
/*  127:     */     {
/*  128: 256 */       InstanceEvent e = new InstanceEvent(this, this.m_format);
/*  129: 257 */       dsl.acceptInstance(e);
/*  130:     */     }
/*  131:     */   }
/*  132:     */   
/*  133:     */   public synchronized void removeInstanceListener(InstanceListener dsl)
/*  134:     */   {
/*  135: 268 */     this.m_instanceListeners.remove(dsl);
/*  136:     */   }
/*  137:     */   
/*  138:     */   public void setVisual(BeanVisual newVisual)
/*  139:     */   {
/*  140: 278 */     this.m_visual = newVisual;
/*  141:     */   }
/*  142:     */   
/*  143:     */   public BeanVisual getVisual()
/*  144:     */   {
/*  145: 287 */     return this.m_visual;
/*  146:     */   }
/*  147:     */   
/*  148:     */   public void useDefaultVisual()
/*  149:     */   {
/*  150: 296 */     this.m_visual.loadIcons("weka/gui/beans/icons/PredictionAppender.gif", "weka/gui/beans/icons/PredictionAppender_animated.gif");
/*  151:     */   }
/*  152:     */   
/*  153:     */   public void acceptClassifier(IncrementalClassifierEvent e)
/*  154:     */   {
/*  155: 310 */     Classifier classifier = e.getClassifier();
/*  156: 311 */     Instance currentI = e.getCurrentInstance();
/*  157: 312 */     int status = e.getStatus();
/*  158: 313 */     int oldNumAtts = 0;
/*  159: 314 */     if (status == 0)
/*  160:     */     {
/*  161: 315 */       oldNumAtts = e.getStructure().numAttributes();
/*  162: 316 */       this.m_throughput = new StreamThroughput(statusMessagePrefix());
/*  163:     */     }
/*  164: 318 */     else if (currentI != null)
/*  165:     */     {
/*  166: 319 */       oldNumAtts = currentI.dataset().numAttributes();
/*  167:     */     }
/*  168: 322 */     if (status == 0)
/*  169:     */     {
/*  170: 323 */       this.m_instanceEvent = new InstanceEvent(this, null, 0);
/*  171:     */       
/*  172: 325 */       Instances oldStructure = new Instances(e.getStructure(), 0);
/*  173:     */       
/*  174:     */ 
/*  175:     */ 
/*  176:     */ 
/*  177: 330 */       this.m_stringAttIndexes = new ArrayList();
/*  178: 331 */       for (int i = 0; i < e.getStructure().numAttributes(); i++) {
/*  179: 332 */         if (e.getStructure().attribute(i).isString()) {
/*  180: 333 */           this.m_stringAttIndexes.add(new Integer(i));
/*  181:     */         }
/*  182:     */       }
/*  183: 337 */       String relationNameModifier = "_with predictions";
/*  184: 339 */       if ((!this.m_appendProbabilities) || (oldStructure.classAttribute().isNumeric())) {
/*  185:     */         try
/*  186:     */         {
/*  187: 341 */           this.m_format = makeDataSetClass(oldStructure, oldStructure, classifier, relationNameModifier);
/*  188:     */         }
/*  189:     */         catch (Exception ex)
/*  190:     */         {
/*  191: 345 */           ex.printStackTrace();
/*  192: 346 */           return;
/*  193:     */         }
/*  194: 348 */       } else if (this.m_appendProbabilities) {
/*  195:     */         try
/*  196:     */         {
/*  197: 350 */           this.m_format = makeDataSetProbabilities(oldStructure, oldStructure, classifier, relationNameModifier);
/*  198:     */         }
/*  199:     */         catch (Exception ex)
/*  200:     */         {
/*  201: 355 */           ex.printStackTrace();
/*  202: 356 */           return;
/*  203:     */         }
/*  204:     */       }
/*  205: 360 */       this.m_instanceEvent.setStructure(this.m_format);
/*  206: 361 */       notifyInstanceAvailable(this.m_instanceEvent);
/*  207: 362 */       return;
/*  208:     */     }
/*  209: 365 */     if (currentI != null)
/*  210:     */     {
/*  211: 366 */       this.m_throughput.updateStart();
/*  212: 367 */       double[] instanceVals = new double[this.m_format.numAttributes()];
/*  213: 368 */       Instance newInst = null;
/*  214:     */       try
/*  215:     */       {
/*  216: 371 */         for (int i = 0; i < oldNumAtts; i++) {
/*  217: 372 */           instanceVals[i] = currentI.value(i);
/*  218:     */         }
/*  219: 374 */         if ((!this.m_appendProbabilities) || (currentI.dataset().classAttribute().isNumeric()))
/*  220:     */         {
/*  221: 376 */           double predClass = classifier.classifyInstance(currentI);
/*  222: 377 */           instanceVals[(instanceVals.length - 1)] = predClass;
/*  223:     */         }
/*  224: 378 */         else if (this.m_appendProbabilities)
/*  225:     */         {
/*  226: 379 */           double[] preds = classifier.distributionForInstance(currentI);
/*  227: 380 */           for (int i = oldNumAtts; i < instanceVals.length; i++) {
/*  228: 381 */             instanceVals[i] = preds[(i - oldNumAtts)];
/*  229:     */           }
/*  230:     */         }
/*  231:     */       }
/*  232:     */       catch (Exception ex)
/*  233:     */       {
/*  234:     */         int i;
/*  235:     */         int index;
/*  236: 385 */         ex.printStackTrace();
/*  237:     */         int i;
/*  238:     */         int index;
/*  239:     */         return;
/*  240:     */       }
/*  241:     */       finally
/*  242:     */       {
/*  243: 388 */         newInst = new DenseInstance(currentI.weight(), instanceVals);
/*  244: 389 */         newInst.setDataset(this.m_format);
/*  245: 391 */         if (this.m_stringAttIndexes != null) {
/*  246: 392 */           for (int i = 0; i < this.m_stringAttIndexes.size(); i++)
/*  247:     */           {
/*  248: 393 */             int index = ((Integer)this.m_stringAttIndexes.get(i)).intValue();
/*  249: 394 */             this.m_format.attribute(((Integer)this.m_stringAttIndexes.get(i)).intValue()).setStringValue(currentI.stringValue(index));
/*  250:     */           }
/*  251:     */         }
/*  252: 399 */         this.m_instanceEvent.setInstance(newInst);
/*  253: 400 */         this.m_instanceEvent.setStatus(status);
/*  254: 401 */         this.m_throughput.updateEnd(this.m_logger);
/*  255:     */         
/*  256: 403 */         notifyInstanceAvailable(this.m_instanceEvent);
/*  257:     */       }
/*  258:     */     }
/*  259:     */     else
/*  260:     */     {
/*  261: 406 */       this.m_instanceEvent.setInstance(null);
/*  262:     */       
/*  263: 408 */       notifyInstanceAvailable(this.m_instanceEvent);
/*  264:     */     }
/*  265: 411 */     if ((status == 2) || (currentI == null))
/*  266:     */     {
/*  267: 414 */       this.m_instanceEvent = null;
/*  268: 415 */       this.m_throughput.finished(this.m_logger);
/*  269:     */     }
/*  270:     */   }
/*  271:     */   
/*  272:     */   public void acceptClassifier(BatchClassifierEvent e)
/*  273:     */   {
/*  274: 426 */     if ((this.m_dataSourceListeners.size() > 0) || (this.m_trainingSetListeners.size() > 0) || (this.m_testSetListeners.size() > 0))
/*  275:     */     {
/*  276: 429 */       if (e.getTestSet() == null) {
/*  277: 431 */         return;
/*  278:     */       }
/*  279: 434 */       if (((e.getTestSet().isStructureOnly()) || (e.getTestSet().getDataSet().numInstances() == 0)) && (e.getTestSet().getDataSet().classIndex() < 0)) {
/*  280: 437 */         return;
/*  281:     */       }
/*  282: 441 */       if (e.getTestSet().getDataSet().classIndex() < 0)
/*  283:     */       {
/*  284: 442 */         if (this.m_logger != null)
/*  285:     */         {
/*  286: 443 */           this.m_logger.logMessage("[PredictionAppender] " + statusMessagePrefix() + "No class attribute set in the data!");
/*  287:     */           
/*  288: 445 */           this.m_logger.statusMessage(statusMessagePrefix() + "ERROR: Can't append probablities - see log.");
/*  289:     */         }
/*  290: 448 */         stop();
/*  291: 449 */         return;
/*  292:     */       }
/*  293: 452 */       Instances testSet = e.getTestSet().getDataSet();
/*  294: 453 */       Instances trainSet = e.getTrainSet().getDataSet();
/*  295: 454 */       int setNum = e.getSetNumber();
/*  296: 455 */       int maxNum = e.getMaxSetNumber();
/*  297:     */       
/*  298: 457 */       Classifier classifier = e.getClassifier();
/*  299: 458 */       String relationNameModifier = "_set_" + e.getSetNumber() + "_of_" + e.getMaxSetNumber();
/*  300: 460 */       if ((!this.m_appendProbabilities) || (testSet.classAttribute().isNumeric())) {
/*  301:     */         try
/*  302:     */         {
/*  303: 462 */           Instances newTestSetInstances = makeDataSetClass(testSet, trainSet, classifier, relationNameModifier);
/*  304:     */           
/*  305:     */ 
/*  306: 465 */           Instances newTrainingSetInstances = makeDataSetClass(trainSet, trainSet, classifier, relationNameModifier);
/*  307: 469 */           if (this.m_trainingSetListeners.size() > 0)
/*  308:     */           {
/*  309: 470 */             TrainingSetEvent tse = new TrainingSetEvent(this, new Instances(newTrainingSetInstances, 0));
/*  310:     */             
/*  311:     */ 
/*  312: 473 */             tse.m_setNumber = setNum;
/*  313: 474 */             tse.m_maxSetNumber = maxNum;
/*  314: 475 */             notifyTrainingSetAvailable(tse);
/*  315: 477 */             for (int i = 0; i < trainSet.numInstances(); i++)
/*  316:     */             {
/*  317: 478 */               double predClass = classifier.classifyInstance(trainSet.instance(i));
/*  318:     */               
/*  319: 480 */               newTrainingSetInstances.instance(i).setValue(newTrainingSetInstances.numAttributes() - 1, predClass);
/*  320:     */             }
/*  321: 483 */             tse = new TrainingSetEvent(this, newTrainingSetInstances);
/*  322: 484 */             tse.m_setNumber = setNum;
/*  323: 485 */             tse.m_maxSetNumber = maxNum;
/*  324: 486 */             notifyTrainingSetAvailable(tse);
/*  325:     */           }
/*  326: 489 */           if (this.m_testSetListeners.size() > 0)
/*  327:     */           {
/*  328: 490 */             TestSetEvent tse = new TestSetEvent(this, new Instances(newTestSetInstances, 0));
/*  329:     */             
/*  330: 492 */             tse.m_setNumber = setNum;
/*  331: 493 */             tse.m_maxSetNumber = maxNum;
/*  332: 494 */             notifyTestSetAvailable(tse);
/*  333:     */           }
/*  334: 496 */           if (this.m_dataSourceListeners.size() > 0) {
/*  335: 497 */             notifyDataSetAvailable(new DataSetEvent(this, new Instances(newTestSetInstances, 0)));
/*  336:     */           }
/*  337: 500 */           if (e.getTestSet().isStructureOnly()) {
/*  338: 501 */             this.m_format = newTestSetInstances;
/*  339:     */           }
/*  340: 503 */           if ((this.m_dataSourceListeners.size() > 0) || (this.m_testSetListeners.size() > 0)) {
/*  341: 505 */             for (int i = 0; i < testSet.numInstances(); i++)
/*  342:     */             {
/*  343: 506 */               Instance tempInst = testSet.instance(i);
/*  344: 514 */               if ((tempInst.isMissing(tempInst.classIndex())) && (!(classifier instanceof InputMappedClassifier)))
/*  345:     */               {
/*  346: 516 */                 tempInst = (Instance)testSet.instance(i).copy();
/*  347: 517 */                 tempInst.setDataset(trainSet);
/*  348:     */               }
/*  349: 519 */               double predClass = classifier.classifyInstance(tempInst);
/*  350: 520 */               newTestSetInstances.instance(i).setValue(newTestSetInstances.numAttributes() - 1, predClass);
/*  351:     */             }
/*  352:     */           }
/*  353: 525 */           if (this.m_testSetListeners.size() > 0)
/*  354:     */           {
/*  355: 526 */             TestSetEvent tse = new TestSetEvent(this, newTestSetInstances);
/*  356: 527 */             tse.m_setNumber = setNum;
/*  357: 528 */             tse.m_maxSetNumber = maxNum;
/*  358: 529 */             notifyTestSetAvailable(tse);
/*  359:     */           }
/*  360: 531 */           if (this.m_dataSourceListeners.size() > 0) {
/*  361: 532 */             notifyDataSetAvailable(new DataSetEvent(this, newTestSetInstances));
/*  362:     */           }
/*  363: 534 */           return;
/*  364:     */         }
/*  365:     */         catch (Exception ex)
/*  366:     */         {
/*  367: 536 */           ex.printStackTrace();
/*  368:     */         }
/*  369:     */       }
/*  370: 539 */       if (this.m_appendProbabilities) {
/*  371:     */         try
/*  372:     */         {
/*  373: 541 */           Instances newTestSetInstances = makeDataSetProbabilities(testSet, trainSet, classifier, relationNameModifier);
/*  374:     */           
/*  375:     */ 
/*  376: 544 */           Instances newTrainingSetInstances = makeDataSetProbabilities(trainSet, trainSet, classifier, relationNameModifier);
/*  377: 547 */           if (this.m_trainingSetListeners.size() > 0)
/*  378:     */           {
/*  379: 548 */             TrainingSetEvent tse = new TrainingSetEvent(this, new Instances(newTrainingSetInstances, 0));
/*  380:     */             
/*  381:     */ 
/*  382: 551 */             tse.m_setNumber = setNum;
/*  383: 552 */             tse.m_maxSetNumber = maxNum;
/*  384: 553 */             notifyTrainingSetAvailable(tse);
/*  385: 555 */             for (int i = 0; i < trainSet.numInstances(); i++)
/*  386:     */             {
/*  387: 556 */               double[] preds = classifier.distributionForInstance(trainSet.instance(i));
/*  388: 558 */               for (int j = 0; j < trainSet.classAttribute().numValues(); j++) {
/*  389: 559 */                 newTrainingSetInstances.instance(i).setValue(trainSet.numAttributes() + j, preds[j]);
/*  390:     */               }
/*  391:     */             }
/*  392: 563 */             tse = new TrainingSetEvent(this, newTrainingSetInstances);
/*  393: 564 */             tse.m_setNumber = setNum;
/*  394: 565 */             tse.m_maxSetNumber = maxNum;
/*  395: 566 */             notifyTrainingSetAvailable(tse);
/*  396:     */           }
/*  397: 568 */           if (this.m_testSetListeners.size() > 0)
/*  398:     */           {
/*  399: 569 */             TestSetEvent tse = new TestSetEvent(this, new Instances(newTestSetInstances, 0));
/*  400:     */             
/*  401: 571 */             tse.m_setNumber = setNum;
/*  402: 572 */             tse.m_maxSetNumber = maxNum;
/*  403: 573 */             notifyTestSetAvailable(tse);
/*  404:     */           }
/*  405: 575 */           if (this.m_dataSourceListeners.size() > 0) {
/*  406: 576 */             notifyDataSetAvailable(new DataSetEvent(this, new Instances(newTestSetInstances, 0)));
/*  407:     */           }
/*  408: 579 */           if (e.getTestSet().isStructureOnly()) {
/*  409: 580 */             this.m_format = newTestSetInstances;
/*  410:     */           }
/*  411: 582 */           if ((this.m_dataSourceListeners.size() > 0) || (this.m_testSetListeners.size() > 0)) {
/*  412: 584 */             for (int i = 0; i < testSet.numInstances(); i++)
/*  413:     */             {
/*  414: 585 */               Instance tempInst = testSet.instance(i);
/*  415: 593 */               if ((tempInst.isMissing(tempInst.classIndex())) && (!(classifier instanceof InputMappedClassifier)))
/*  416:     */               {
/*  417: 595 */                 tempInst = (Instance)testSet.instance(i).copy();
/*  418: 596 */                 tempInst.setDataset(trainSet);
/*  419:     */               }
/*  420: 599 */               double[] preds = classifier.distributionForInstance(tempInst);
/*  421: 600 */               for (int j = 0; j < tempInst.classAttribute().numValues(); j++) {
/*  422: 601 */                 newTestSetInstances.instance(i).setValue(testSet.numAttributes() + j, preds[j]);
/*  423:     */               }
/*  424:     */             }
/*  425:     */           }
/*  426: 608 */           if (this.m_testSetListeners.size() > 0)
/*  427:     */           {
/*  428: 609 */             TestSetEvent tse = new TestSetEvent(this, newTestSetInstances);
/*  429: 610 */             tse.m_setNumber = setNum;
/*  430: 611 */             tse.m_maxSetNumber = maxNum;
/*  431: 612 */             notifyTestSetAvailable(tse);
/*  432:     */           }
/*  433: 614 */           if (this.m_dataSourceListeners.size() > 0) {
/*  434: 615 */             notifyDataSetAvailable(new DataSetEvent(this, newTestSetInstances));
/*  435:     */           }
/*  436:     */         }
/*  437:     */         catch (Exception ex)
/*  438:     */         {
/*  439: 618 */           ex.printStackTrace();
/*  440:     */         }
/*  441:     */       }
/*  442:     */     }
/*  443:     */   }
/*  444:     */   
/*  445:     */   public void acceptClusterer(BatchClustererEvent e)
/*  446:     */   {
/*  447: 631 */     if ((this.m_dataSourceListeners.size() > 0) || (this.m_trainingSetListeners.size() > 0) || (this.m_testSetListeners.size() > 0))
/*  448:     */     {
/*  449: 634 */       if (e.getTestSet().isStructureOnly()) {
/*  450: 635 */         return;
/*  451:     */       }
/*  452: 637 */       Instances testSet = e.getTestSet().getDataSet();
/*  453:     */       
/*  454: 639 */       Clusterer clusterer = e.getClusterer();
/*  455:     */       String test;
/*  456:     */       String test;
/*  457: 641 */       if (e.getTestOrTrain() == 0) {
/*  458: 642 */         test = "test";
/*  459:     */       } else {
/*  460: 644 */         test = "training";
/*  461:     */       }
/*  462: 646 */       String relationNameModifier = "_" + test + "_" + e.getSetNumber() + "_of_" + e.getMaxSetNumber();
/*  463: 648 */       if ((!this.m_appendProbabilities) || (!(clusterer instanceof DensityBasedClusterer)))
/*  464:     */       {
/*  465: 650 */         if ((this.m_appendProbabilities) && (!(clusterer instanceof DensityBasedClusterer)))
/*  466:     */         {
/*  467: 652 */           System.err.println("Only density based clusterers can append probabilities. Instead cluster will be assigned for each instance.");
/*  468: 654 */           if (this.m_logger != null)
/*  469:     */           {
/*  470: 655 */             this.m_logger.logMessage("[PredictionAppender] " + statusMessagePrefix() + " Only density based clusterers can " + "append probabilities. Instead cluster will be assigned for each " + "instance.");
/*  471:     */             
/*  472:     */ 
/*  473:     */ 
/*  474:     */ 
/*  475:     */ 
/*  476: 661 */             this.m_logger.statusMessage(statusMessagePrefix() + "WARNING: Only density based clusterers can append probabilities. " + "Instead cluster will be assigned for each instance.");
/*  477:     */           }
/*  478:     */         }
/*  479:     */         try
/*  480:     */         {
/*  481: 668 */           Instances newInstances = makeClusterDataSetClass(testSet, clusterer, relationNameModifier);
/*  482: 672 */           if (this.m_dataSourceListeners.size() > 0) {
/*  483: 673 */             notifyDataSetAvailable(new DataSetEvent(this, new Instances(newInstances, 0)));
/*  484:     */           }
/*  485: 677 */           if ((this.m_trainingSetListeners.size() > 0) && (e.getTestOrTrain() > 0))
/*  486:     */           {
/*  487: 678 */             TrainingSetEvent tse = new TrainingSetEvent(this, new Instances(newInstances, 0));
/*  488:     */             
/*  489: 680 */             tse.m_setNumber = e.getSetNumber();
/*  490: 681 */             tse.m_maxSetNumber = e.getMaxSetNumber();
/*  491: 682 */             notifyTrainingSetAvailable(tse);
/*  492:     */           }
/*  493: 685 */           if ((this.m_testSetListeners.size() > 0) && (e.getTestOrTrain() == 0))
/*  494:     */           {
/*  495: 686 */             TestSetEvent tse = new TestSetEvent(this, new Instances(newInstances, 0));
/*  496:     */             
/*  497: 688 */             tse.m_setNumber = e.getSetNumber();
/*  498: 689 */             tse.m_maxSetNumber = e.getMaxSetNumber();
/*  499: 690 */             notifyTestSetAvailable(tse);
/*  500:     */           }
/*  501: 694 */           for (int i = 0; i < testSet.numInstances(); i++)
/*  502:     */           {
/*  503: 695 */             double predCluster = clusterer.clusterInstance(testSet.instance(i));
/*  504: 696 */             newInstances.instance(i).setValue(newInstances.numAttributes() - 1, predCluster);
/*  505:     */           }
/*  506: 700 */           if (this.m_dataSourceListeners.size() > 0) {
/*  507: 701 */             notifyDataSetAvailable(new DataSetEvent(this, newInstances));
/*  508:     */           }
/*  509: 703 */           if ((this.m_trainingSetListeners.size() > 0) && (e.getTestOrTrain() > 0))
/*  510:     */           {
/*  511: 704 */             TrainingSetEvent tse = new TrainingSetEvent(this, newInstances);
/*  512: 705 */             tse.m_setNumber = e.getSetNumber();
/*  513: 706 */             tse.m_maxSetNumber = e.getMaxSetNumber();
/*  514: 707 */             notifyTrainingSetAvailable(tse);
/*  515:     */           }
/*  516: 709 */           if ((this.m_testSetListeners.size() > 0) && (e.getTestOrTrain() == 0))
/*  517:     */           {
/*  518: 710 */             TestSetEvent tse = new TestSetEvent(this, newInstances);
/*  519: 711 */             tse.m_setNumber = e.getSetNumber();
/*  520: 712 */             tse.m_maxSetNumber = e.getMaxSetNumber();
/*  521: 713 */             notifyTestSetAvailable(tse);
/*  522:     */           }
/*  523: 716 */           return;
/*  524:     */         }
/*  525:     */         catch (Exception ex)
/*  526:     */         {
/*  527: 718 */           ex.printStackTrace();
/*  528:     */         }
/*  529:     */       }
/*  530:     */       else
/*  531:     */       {
/*  532:     */         try
/*  533:     */         {
/*  534: 722 */           Instances newInstances = makeClusterDataSetProbabilities(testSet, clusterer, relationNameModifier);
/*  535:     */           
/*  536:     */ 
/*  537: 725 */           notifyDataSetAvailable(new DataSetEvent(this, new Instances(newInstances, 0)));
/*  538: 729 */           for (int i = 0; i < testSet.numInstances(); i++)
/*  539:     */           {
/*  540: 730 */             double[] probs = clusterer.distributionForInstance(testSet.instance(i));
/*  541: 732 */             for (int j = 0; j < clusterer.numberOfClusters(); j++) {
/*  542: 733 */               newInstances.instance(i).setValue(testSet.numAttributes() + j, probs[j]);
/*  543:     */             }
/*  544:     */           }
/*  545: 738 */           notifyDataSetAvailable(new DataSetEvent(this, newInstances));
/*  546:     */         }
/*  547:     */         catch (Exception ex)
/*  548:     */         {
/*  549: 740 */           ex.printStackTrace();
/*  550:     */         }
/*  551:     */       }
/*  552:     */     }
/*  553:     */   }
/*  554:     */   
/*  555:     */   private Instances makeDataSetProbabilities(Instances insts, Instances format, Classifier classifier, String relationNameModifier)
/*  556:     */     throws Exception
/*  557:     */   {
/*  558: 751 */     if ((classifier instanceof InputMappedClassifier)) {
/*  559: 752 */       format = ((InputMappedClassifier)classifier).getModelHeader(new Instances(format, 0));
/*  560:     */     }
/*  561: 757 */     String classifierName = classifier.getClass().getName();
/*  562: 758 */     classifierName = classifierName.substring(classifierName.lastIndexOf('.') + 1, classifierName.length());
/*  563:     */     
/*  564:     */ 
/*  565: 761 */     Instances newInstances = new Instances(insts);
/*  566: 762 */     for (int i = 0; i < format.classAttribute().numValues(); i++)
/*  567:     */     {
/*  568: 763 */       Add addF = new Add();
/*  569:     */       
/*  570: 765 */       addF.setAttributeIndex("last");
/*  571: 766 */       addF.setAttributeName(classifierName + "_prob_" + format.classAttribute().value(i));
/*  572:     */       
/*  573: 768 */       addF.setInputFormat(newInstances);
/*  574: 769 */       newInstances = Filter.useFilter(newInstances, addF);
/*  575:     */     }
/*  576: 771 */     newInstances.setRelationName(insts.relationName() + relationNameModifier);
/*  577: 772 */     return newInstances;
/*  578:     */   }
/*  579:     */   
/*  580:     */   private Instances makeDataSetClass(Instances insts, Instances structure, Classifier classifier, String relationNameModifier)
/*  581:     */     throws Exception
/*  582:     */   {
/*  583: 780 */     if ((classifier instanceof InputMappedClassifier)) {
/*  584: 781 */       structure = ((InputMappedClassifier)classifier).getModelHeader(new Instances(structure, 0));
/*  585:     */     }
/*  586: 786 */     Add addF = new Add();
/*  587:     */     
/*  588: 788 */     addF.setAttributeIndex("last");
/*  589: 789 */     String classifierName = classifier.getClass().getName();
/*  590: 790 */     classifierName = classifierName.substring(classifierName.lastIndexOf('.') + 1, classifierName.length());
/*  591:     */     
/*  592:     */ 
/*  593: 793 */     addF.setAttributeName("class_predicted_by: " + classifierName);
/*  594: 794 */     if (structure.classAttribute().isNominal())
/*  595:     */     {
/*  596: 795 */       String classLabels = "";
/*  597: 796 */       Enumeration<Object> enu = structure.classAttribute().enumerateValues();
/*  598: 797 */       classLabels = classLabels + (String)enu.nextElement();
/*  599: 798 */       while (enu.hasMoreElements()) {
/*  600: 799 */         classLabels = classLabels + "," + (String)enu.nextElement();
/*  601:     */       }
/*  602: 801 */       addF.setNominalLabels(classLabels);
/*  603:     */     }
/*  604: 803 */     addF.setInputFormat(insts);
/*  605:     */     
/*  606: 805 */     Instances newInstances = Filter.useFilter(insts, addF);
/*  607: 806 */     newInstances.setRelationName(insts.relationName() + relationNameModifier);
/*  608: 807 */     return newInstances;
/*  609:     */   }
/*  610:     */   
/*  611:     */   private Instances makeClusterDataSetProbabilities(Instances format, Clusterer clusterer, String relationNameModifier)
/*  612:     */     throws Exception
/*  613:     */   {
/*  614: 813 */     Instances newInstances = new Instances(format);
/*  615: 814 */     for (int i = 0; i < clusterer.numberOfClusters(); i++)
/*  616:     */     {
/*  617: 815 */       Add addF = new Add();
/*  618:     */       
/*  619: 817 */       addF.setAttributeIndex("last");
/*  620: 818 */       addF.setAttributeName("prob_cluster" + i);
/*  621: 819 */       addF.setInputFormat(newInstances);
/*  622: 820 */       newInstances = Filter.useFilter(newInstances, addF);
/*  623:     */     }
/*  624: 822 */     newInstances.setRelationName(format.relationName() + relationNameModifier);
/*  625: 823 */     return newInstances;
/*  626:     */   }
/*  627:     */   
/*  628:     */   private Instances makeClusterDataSetClass(Instances format, Clusterer clusterer, String relationNameModifier)
/*  629:     */     throws Exception
/*  630:     */   {
/*  631: 830 */     Add addF = new Add();
/*  632:     */     
/*  633: 832 */     addF.setAttributeIndex("last");
/*  634: 833 */     String clustererName = clusterer.getClass().getName();
/*  635: 834 */     clustererName = clustererName.substring(clustererName.lastIndexOf('.') + 1, clustererName.length());
/*  636:     */     
/*  637:     */ 
/*  638: 837 */     addF.setAttributeName("assigned_cluster: " + clustererName);
/*  639:     */     
/*  640: 839 */     String clusterLabels = "0";
/*  641: 845 */     for (int i = 1; i <= clusterer.numberOfClusters() - 1; i++) {
/*  642: 846 */       clusterLabels = clusterLabels + "," + i;
/*  643:     */     }
/*  644: 848 */     addF.setNominalLabels(clusterLabels);
/*  645:     */     
/*  646: 850 */     addF.setInputFormat(format);
/*  647:     */     
/*  648: 852 */     Instances newInstances = Filter.useFilter(format, addF);
/*  649: 853 */     newInstances.setRelationName(format.relationName() + relationNameModifier);
/*  650: 854 */     return newInstances;
/*  651:     */   }
/*  652:     */   
/*  653:     */   protected void notifyInstanceAvailable(InstanceEvent e)
/*  654:     */   {
/*  655:     */     Vector<InstanceListener> l;
/*  656: 865 */     synchronized (this)
/*  657:     */     {
/*  658: 866 */       l = (Vector)this.m_instanceListeners.clone();
/*  659:     */     }
/*  660: 869 */     if (l.size() > 0) {
/*  661: 870 */       for (int i = 0; i < l.size(); i++) {
/*  662: 871 */         ((InstanceListener)l.elementAt(i)).acceptInstance(e);
/*  663:     */       }
/*  664:     */     }
/*  665:     */   }
/*  666:     */   
/*  667:     */   protected void notifyDataSetAvailable(DataSetEvent e)
/*  668:     */   {
/*  669:     */     Vector<DataSourceListener> l;
/*  670: 884 */     synchronized (this)
/*  671:     */     {
/*  672: 885 */       l = (Vector)this.m_dataSourceListeners.clone();
/*  673:     */     }
/*  674: 888 */     if (l.size() > 0) {
/*  675: 889 */       for (int i = 0; i < l.size(); i++) {
/*  676: 890 */         ((DataSourceListener)l.elementAt(i)).acceptDataSet(e);
/*  677:     */       }
/*  678:     */     }
/*  679:     */   }
/*  680:     */   
/*  681:     */   protected void notifyTestSetAvailable(TestSetEvent e)
/*  682:     */   {
/*  683:     */     Vector<TestSetListener> l;
/*  684: 903 */     synchronized (this)
/*  685:     */     {
/*  686: 904 */       l = (Vector)this.m_testSetListeners.clone();
/*  687:     */     }
/*  688: 907 */     if (l.size() > 0) {
/*  689: 908 */       for (int i = 0; i < l.size(); i++) {
/*  690: 909 */         ((TestSetListener)l.elementAt(i)).acceptTestSet(e);
/*  691:     */       }
/*  692:     */     }
/*  693:     */   }
/*  694:     */   
/*  695:     */   protected void notifyTrainingSetAvailable(TrainingSetEvent e)
/*  696:     */   {
/*  697:     */     Vector<TrainingSetListener> l;
/*  698: 922 */     synchronized (this)
/*  699:     */     {
/*  700: 923 */       l = (Vector)this.m_trainingSetListeners.clone();
/*  701:     */     }
/*  702: 926 */     if (l.size() > 0) {
/*  703: 927 */       for (int i = 0; i < l.size(); i++) {
/*  704: 928 */         ((TrainingSetListener)l.elementAt(i)).acceptTrainingSet(e);
/*  705:     */       }
/*  706:     */     }
/*  707:     */   }
/*  708:     */   
/*  709:     */   public void setLog(Logger logger)
/*  710:     */   {
/*  711: 940 */     this.m_logger = logger;
/*  712:     */   }
/*  713:     */   
/*  714:     */   public void stop()
/*  715:     */   {
/*  716: 946 */     if ((this.m_listenee instanceof BeanCommon)) {
/*  717: 947 */       ((BeanCommon)this.m_listenee).stop();
/*  718:     */     }
/*  719:     */   }
/*  720:     */   
/*  721:     */   public boolean isBusy()
/*  722:     */   {
/*  723: 959 */     return false;
/*  724:     */   }
/*  725:     */   
/*  726:     */   public boolean connectionAllowed(String eventName)
/*  727:     */   {
/*  728: 971 */     return this.m_listenee == null;
/*  729:     */   }
/*  730:     */   
/*  731:     */   public boolean connectionAllowed(EventSetDescriptor esd)
/*  732:     */   {
/*  733: 983 */     return connectionAllowed(esd.getName());
/*  734:     */   }
/*  735:     */   
/*  736:     */   public synchronized void connectionNotification(String eventName, Object source)
/*  737:     */   {
/*  738: 997 */     if (connectionAllowed(eventName)) {
/*  739: 998 */       this.m_listenee = source;
/*  740:     */     }
/*  741:     */   }
/*  742:     */   
/*  743:     */   public synchronized void disconnectionNotification(String eventName, Object source)
/*  744:     */   {
/*  745:1013 */     if (this.m_listenee == source)
/*  746:     */     {
/*  747:1014 */       this.m_listenee = null;
/*  748:1015 */       this.m_format = null;
/*  749:     */     }
/*  750:     */   }
/*  751:     */   
/*  752:     */   public boolean eventGeneratable(String eventName)
/*  753:     */   {
/*  754:1029 */     if (this.m_listenee == null) {
/*  755:1030 */       return false;
/*  756:     */     }
/*  757:1033 */     if ((this.m_listenee instanceof EventConstraints))
/*  758:     */     {
/*  759:1034 */       if ((eventName.equals("instance")) && 
/*  760:1035 */         (!((EventConstraints)this.m_listenee).eventGeneratable("incrementalClassifier"))) {
/*  761:1037 */         return false;
/*  762:     */       }
/*  763:1040 */       if ((eventName.equals("dataSet")) || (eventName.equals("trainingSet")) || (eventName.equals("testSet")))
/*  764:     */       {
/*  765:1042 */         if (((EventConstraints)this.m_listenee).eventGeneratable("batchClassifier")) {
/*  766:1043 */           return true;
/*  767:     */         }
/*  768:1045 */         if (((EventConstraints)this.m_listenee).eventGeneratable("batchClusterer")) {
/*  769:1046 */           return true;
/*  770:     */         }
/*  771:1048 */         return false;
/*  772:     */       }
/*  773:     */     }
/*  774:1051 */     return true;
/*  775:     */   }
/*  776:     */   
/*  777:     */   private String statusMessagePrefix()
/*  778:     */   {
/*  779:1055 */     return getCustomName() + "$" + hashCode() + "|";
/*  780:     */   }
/*  781:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.gui.beans.PredictionAppender
 * JD-Core Version:    0.7.0.1
 */