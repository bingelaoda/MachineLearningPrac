/*    1:     */ package weka.knowledgeflow.steps;
/*    2:     */ 
/*    3:     */ import java.awt.Color;
/*    4:     */ import java.awt.Graphics;
/*    5:     */ import java.awt.Graphics2D;
/*    6:     */ import java.awt.RenderingHints;
/*    7:     */ import java.awt.image.BufferedImage;
/*    8:     */ import java.io.IOException;
/*    9:     */ import java.io.Serializable;
/*   10:     */ import java.util.ArrayList;
/*   11:     */ import java.util.Arrays;
/*   12:     */ import java.util.LinkedHashMap;
/*   13:     */ import java.util.List;
/*   14:     */ import java.util.Map;
/*   15:     */ import java.util.Random;
/*   16:     */ import java.util.concurrent.Future;
/*   17:     */ import weka.classifiers.AbstractClassifier;
/*   18:     */ import weka.clusterers.AbstractClusterer;
/*   19:     */ import weka.clusterers.DensityBasedClusterer;
/*   20:     */ import weka.core.Attribute;
/*   21:     */ import weka.core.DenseInstance;
/*   22:     */ import weka.core.Instance;
/*   23:     */ import weka.core.Instances;
/*   24:     */ import weka.core.OptionHandler;
/*   25:     */ import weka.core.OptionMetadata;
/*   26:     */ import weka.core.SerializedObject;
/*   27:     */ import weka.core.Utils;
/*   28:     */ import weka.core.WekaException;
/*   29:     */ import weka.gui.ProgrammaticProperty;
/*   30:     */ import weka.gui.boundaryvisualizer.DataGenerator;
/*   31:     */ import weka.gui.boundaryvisualizer.KDDataGenerator;
/*   32:     */ import weka.knowledgeflow.Data;
/*   33:     */ import weka.knowledgeflow.ExecutionEnvironment;
/*   34:     */ import weka.knowledgeflow.ExecutionResult;
/*   35:     */ import weka.knowledgeflow.LogManager;
/*   36:     */ import weka.knowledgeflow.StepManager;
/*   37:     */ import weka.knowledgeflow.StepTask;
/*   38:     */ 
/*   39:     */ @KFStep(name="BoundaryPlotter", category="Visualization", toolTipText="Visualize class/cluster decision boundaries in a 2D plot", iconPath="weka/gui/knowledgeflow/icons/DefaultDataVisualizer.gif")
/*   40:     */ public class BoundaryPlotter
/*   41:     */   extends BaseStep
/*   42:     */   implements DataCollector
/*   43:     */ {
/*   44:  71 */   public static final Color[] DEFAULT_COLORS = { Color.red, Color.green, Color.blue, new Color(0, 255, 255), new Color(255, 0, 255), new Color(255, 255, 0), new Color(255, 255, 255), new Color(0, 0, 0) };
/*   45:     */   private static final long serialVersionUID = 7864251468395026619L;
/*   46:  81 */   protected List<Color> m_Colors = new ArrayList();
/*   47:  88 */   protected int m_maxRowsInParallel = 10;
/*   48:  91 */   protected int m_imageWidth = 400;
/*   49:  94 */   protected int m_imageHeight = 400;
/*   50:  97 */   protected String m_xAttName = "/first";
/*   51: 100 */   protected String m_yAttName = "2";
/*   52: 103 */   protected boolean m_plotTrainingData = true;
/*   53:     */   protected int m_xAttribute;
/*   54:     */   protected int m_yAttribute;
/*   55:     */   protected double m_minX;
/*   56:     */   protected double m_minY;
/*   57:     */   protected double m_maxX;
/*   58:     */   protected double m_maxY;
/*   59:     */   protected double m_rangeX;
/*   60:     */   protected double m_rangeY;
/*   61:     */   protected double m_pixHeight;
/*   62:     */   protected double m_pixWidth;
/*   63:     */   protected transient BufferedImage m_osi;
/*   64:     */   protected String m_currentDescription;
/*   65:     */   protected transient Map<String, BufferedImage> m_completedImages;
/*   66:     */   protected List<weka.classifiers.Classifier> m_classifierTemplates;
/*   67:     */   protected List<DensityBasedClusterer> m_clustererTemplates;
/*   68:     */   protected weka.classifiers.Classifier[] m_threadClassifiers;
/*   69:     */   protected weka.clusterers.Clusterer[] m_threadClusterers;
/*   70:     */   protected DataGenerator[] m_threadGenerators;
/*   71:     */   protected KDDataGenerator m_dataGenerator;
/*   72: 149 */   protected String m_kBand = "3";
/*   73: 152 */   protected String m_nSamples = "2";
/*   74: 155 */   protected String m_sBase = "2";
/*   75: 158 */   protected int m_kernelBandwidth = 3;
/*   76: 161 */   protected int m_numSamplesPerRegion = 2;
/*   77: 164 */   protected int m_samplesBase = 2;
/*   78:     */   protected transient RenderingUpdateListener m_plotListener;
/*   79:     */   protected boolean m_isReset;
/*   80:     */   
/*   81:     */   public BoundaryPlotter()
/*   82:     */   {
/*   83: 176 */     for (Color element : DEFAULT_COLORS) {
/*   84: 177 */       this.m_Colors.add(new Color(element.getRed(), element.getGreen(), element.getBlue()));
/*   85:     */     }
/*   86:     */   }
/*   87:     */   
/*   88:     */   @ProgrammaticProperty
/*   89:     */   @OptionMetadata(displayName="X attribute", description="Attribute to visualize on the x-axis", displayOrder=1)
/*   90:     */   public void setXAttName(String xAttName)
/*   91:     */   {
/*   92: 193 */     this.m_xAttName = xAttName;
/*   93:     */   }
/*   94:     */   
/*   95:     */   public String getXAttName()
/*   96:     */   {
/*   97: 202 */     return this.m_xAttName;
/*   98:     */   }
/*   99:     */   
/*  100:     */   @ProgrammaticProperty
/*  101:     */   @OptionMetadata(displayName="Y attribute", description="Attribute to visualize on the y-axis", displayOrder=2)
/*  102:     */   public void setYAttName(String attName)
/*  103:     */   {
/*  104: 216 */     this.m_yAttName = attName;
/*  105:     */   }
/*  106:     */   
/*  107:     */   public String getYAttName()
/*  108:     */   {
/*  109: 225 */     return this.m_yAttName;
/*  110:     */   }
/*  111:     */   
/*  112:     */   @OptionMetadata(displayName="Base for sampling (r)", description="The base for sampling", displayOrder=3)
/*  113:     */   public void setBaseForSampling(String base)
/*  114:     */   {
/*  115: 236 */     this.m_sBase = base;
/*  116:     */   }
/*  117:     */   
/*  118:     */   public String getBaseForSampling()
/*  119:     */   {
/*  120: 245 */     return this.m_sBase;
/*  121:     */   }
/*  122:     */   
/*  123:     */   @OptionMetadata(displayName="Num. locations per pixel", description="Number of locations per pixel", displayOrder=4)
/*  124:     */   public void setNumLocationsPerPixel(String num)
/*  125:     */   {
/*  126: 256 */     this.m_nSamples = num;
/*  127:     */   }
/*  128:     */   
/*  129:     */   public String getNumLocationsPerPixel()
/*  130:     */   {
/*  131: 265 */     return this.m_nSamples;
/*  132:     */   }
/*  133:     */   
/*  134:     */   @OptionMetadata(displayName="Kernel bandwidth (k)", description="Kernel bandwidth", displayOrder=4)
/*  135:     */   public void setKernelBandwidth(String band)
/*  136:     */   {
/*  137: 276 */     this.m_kBand = band;
/*  138:     */   }
/*  139:     */   
/*  140:     */   public String getKernelBandwidth()
/*  141:     */   {
/*  142: 285 */     return this.m_kBand;
/*  143:     */   }
/*  144:     */   
/*  145:     */   @OptionMetadata(displayName="Image width (pixels)", description="Image width in pixels", displayOrder=5)
/*  146:     */   public void setImageWidth(int width)
/*  147:     */   {
/*  148: 296 */     this.m_imageWidth = width;
/*  149:     */   }
/*  150:     */   
/*  151:     */   public int getImageWidth()
/*  152:     */   {
/*  153: 305 */     return this.m_imageWidth;
/*  154:     */   }
/*  155:     */   
/*  156:     */   @OptionMetadata(displayName="Image height (pixels)", description="Image height in pixels", displayOrder=6)
/*  157:     */   public void setImageHeight(int height)
/*  158:     */   {
/*  159: 316 */     this.m_imageHeight = height;
/*  160:     */   }
/*  161:     */   
/*  162:     */   public int getImageHeight()
/*  163:     */   {
/*  164: 325 */     return this.m_imageHeight;
/*  165:     */   }
/*  166:     */   
/*  167:     */   @OptionMetadata(displayName="Max image rows to compute in parallel", description="Use this many tasks for computing rows of the image", displayOrder=7)
/*  168:     */   public void setComputeMaxRowsInParallel(int max)
/*  169:     */   {
/*  170: 337 */     if (max > 0) {
/*  171: 338 */       this.m_maxRowsInParallel = max;
/*  172:     */     }
/*  173:     */   }
/*  174:     */   
/*  175:     */   public int getComputeMaxRowsInParallel()
/*  176:     */   {
/*  177: 348 */     return this.m_maxRowsInParallel;
/*  178:     */   }
/*  179:     */   
/*  180:     */   @OptionMetadata(displayName="Plot training points", description="Superimpose the training data over the top of the plot", displayOrder=8)
/*  181:     */   public void setPlotTrainingData(boolean plot)
/*  182:     */   {
/*  183: 360 */     this.m_plotTrainingData = plot;
/*  184:     */   }
/*  185:     */   
/*  186:     */   public boolean getPlotTrainingData()
/*  187:     */   {
/*  188: 369 */     return this.m_plotTrainingData;
/*  189:     */   }
/*  190:     */   
/*  191:     */   public void stepInit()
/*  192:     */     throws WekaException
/*  193:     */   {
/*  194: 380 */     List<StepManager> infos = getStepManager().getIncomingConnectedStepsOfConnectionType("info");
/*  195: 383 */     if (infos.size() == 0) {
/*  196: 384 */       throw new WekaException("One or more classifiers/clusterers need to be supplied via an 'info' connection type");
/*  197:     */     }
/*  198: 389 */     this.m_classifierTemplates = new ArrayList();
/*  199: 390 */     this.m_clustererTemplates = new ArrayList();
/*  200: 391 */     for (StepManager m : infos)
/*  201:     */     {
/*  202: 392 */       Step info = m.getInfoStep();
/*  203: 394 */       if ((info instanceof Classifier))
/*  204:     */       {
/*  205: 395 */         this.m_classifierTemplates.add(((Classifier)info).getClassifier());
/*  206:     */       }
/*  207: 397 */       else if ((info instanceof Clusterer))
/*  208:     */       {
/*  209: 398 */         weka.clusterers.Clusterer c = ((Clusterer)info).getClusterer();
/*  210: 400 */         if (!(c instanceof DensityBasedClusterer)) {
/*  211: 401 */           throw new WekaException("Clusterer " + c.getClass().getCanonicalName() + " is not a DensityBasedClusterer");
/*  212:     */         }
/*  213: 405 */         this.m_clustererTemplates.add((DensityBasedClusterer)c);
/*  214:     */       }
/*  215:     */     }
/*  216: 409 */     this.m_completedImages = new LinkedHashMap();
/*  217: 411 */     if ((this.m_nSamples != null) && (this.m_nSamples.length() > 0))
/*  218:     */     {
/*  219: 412 */       String nSampes = environmentSubstitute(this.m_nSamples);
/*  220:     */       try
/*  221:     */       {
/*  222: 414 */         this.m_numSamplesPerRegion = Integer.parseInt(nSampes);
/*  223:     */       }
/*  224:     */       catch (NumberFormatException ex)
/*  225:     */       {
/*  226: 416 */         getStepManager().logWarning("Unable to parse '" + nSampes + "' for num " + "samples per region parameter, using default: " + this.m_numSamplesPerRegion);
/*  227:     */       }
/*  228:     */     }
/*  229: 423 */     if ((this.m_sBase != null) && (this.m_sBase.length() > 0))
/*  230:     */     {
/*  231: 424 */       String sBase = environmentSubstitute(this.m_sBase);
/*  232:     */       try
/*  233:     */       {
/*  234: 426 */         this.m_samplesBase = Integer.parseInt(sBase);
/*  235:     */       }
/*  236:     */       catch (NumberFormatException ex)
/*  237:     */       {
/*  238: 428 */         getStepManager().logWarning("Unable to parse '" + sBase + "' for " + "the base for sampling parameter, using default: " + this.m_samplesBase);
/*  239:     */       }
/*  240:     */     }
/*  241: 435 */     if ((this.m_kBand != null) && (this.m_kBand.length() > 0))
/*  242:     */     {
/*  243: 436 */       String kBand = environmentSubstitute(this.m_kBand);
/*  244:     */       try
/*  245:     */       {
/*  246: 438 */         this.m_kernelBandwidth = Integer.parseInt(kBand);
/*  247:     */       }
/*  248:     */       catch (NumberFormatException ex)
/*  249:     */       {
/*  250: 440 */         getStepManager().logWarning("Unable to parse '" + kBand + "' for kernel " + "bandwidth parameter, using default: " + this.m_kernelBandwidth);
/*  251:     */       }
/*  252:     */     }
/*  253: 450 */     this.m_isReset = true;
/*  254:     */   }
/*  255:     */   
/*  256:     */   protected void computeMinMaxAtts(Instances trainingData)
/*  257:     */   {
/*  258: 454 */     this.m_minX = 1.7976931348623157E+308D;
/*  259: 455 */     this.m_minY = 1.7976931348623157E+308D;
/*  260: 456 */     this.m_maxX = 4.9E-324D;
/*  261: 457 */     this.m_maxY = 4.9E-324D;
/*  262:     */     
/*  263: 459 */     boolean allPointsLessThanOne = true;
/*  264: 461 */     if (trainingData.numInstances() == 0)
/*  265:     */     {
/*  266: 462 */       this.m_minX = (this.m_minY = 0.0D);
/*  267: 463 */       this.m_maxX = (this.m_maxY = 1.0D);
/*  268:     */     }
/*  269:     */     else
/*  270:     */     {
/*  271: 465 */       for (int i = 0; i < trainingData.numInstances(); i++)
/*  272:     */       {
/*  273: 466 */         Instance inst = trainingData.instance(i);
/*  274: 467 */         double x = inst.value(this.m_xAttribute);
/*  275: 468 */         double y = inst.value(this.m_yAttribute);
/*  276: 469 */         if ((!Utils.isMissingValue(x)) && (!Utils.isMissingValue(y)))
/*  277:     */         {
/*  278: 470 */           if (x < this.m_minX) {
/*  279: 471 */             this.m_minX = x;
/*  280:     */           }
/*  281: 473 */           if (x > this.m_maxX) {
/*  282: 474 */             this.m_maxX = x;
/*  283:     */           }
/*  284: 477 */           if (y < this.m_minY) {
/*  285: 478 */             this.m_minY = y;
/*  286:     */           }
/*  287: 480 */           if (y > this.m_maxY) {
/*  288: 481 */             this.m_maxY = y;
/*  289:     */           }
/*  290: 483 */           if ((x > 1.0D) || (y > 1.0D)) {
/*  291: 484 */             allPointsLessThanOne = false;
/*  292:     */           }
/*  293:     */         }
/*  294:     */       }
/*  295:     */     }
/*  296: 490 */     if (this.m_minX == this.m_maxX) {
/*  297: 491 */       this.m_minX = 0.0D;
/*  298:     */     }
/*  299: 493 */     if (this.m_minY == this.m_maxY) {
/*  300: 494 */       this.m_minY = 0.0D;
/*  301:     */     }
/*  302: 496 */     if (this.m_minX == 1.7976931348623157E+308D) {
/*  303: 497 */       this.m_minX = 0.0D;
/*  304:     */     }
/*  305: 499 */     if (this.m_minY == 1.7976931348623157E+308D) {
/*  306: 500 */       this.m_minY = 0.0D;
/*  307:     */     }
/*  308: 502 */     if (this.m_maxX == 4.9E-324D) {
/*  309: 503 */       this.m_maxX = 1.0D;
/*  310:     */     }
/*  311: 505 */     if (this.m_maxY == 4.9E-324D) {
/*  312: 506 */       this.m_maxY = 1.0D;
/*  313:     */     }
/*  314: 508 */     if (allPointsLessThanOne) {
/*  315: 510 */       this.m_maxX = (this.m_maxY = 1.0D);
/*  316:     */     }
/*  317: 513 */     this.m_rangeX = (this.m_maxX - this.m_minX);
/*  318: 514 */     this.m_rangeY = (this.m_maxY - this.m_minY);
/*  319:     */     
/*  320: 516 */     this.m_pixWidth = (this.m_rangeX / this.m_imageWidth);
/*  321: 517 */     this.m_pixHeight = (this.m_rangeY / this.m_imageHeight);
/*  322:     */   }
/*  323:     */   
/*  324:     */   protected int getAttIndex(String attName, Instances data)
/*  325:     */     throws WekaException
/*  326:     */   {
/*  327: 522 */     attName = environmentSubstitute(attName);
/*  328: 523 */     int index = -1;
/*  329: 525 */     if ((attName.equalsIgnoreCase("first")) || (attName.equalsIgnoreCase("/first")))
/*  330:     */     {
/*  331: 526 */       index = 0;
/*  332:     */     }
/*  333: 527 */     else if ((attName.equalsIgnoreCase("last")) || (attName.equalsIgnoreCase("/last")))
/*  334:     */     {
/*  335: 529 */       index = data.numAttributes() - 1;
/*  336:     */     }
/*  337:     */     else
/*  338:     */     {
/*  339: 531 */       Attribute a = data.attribute(attName);
/*  340: 532 */       if (a != null) {
/*  341: 533 */         index = a.index();
/*  342:     */       } else {
/*  343:     */         try
/*  344:     */         {
/*  345: 537 */           index = Integer.parseInt(attName);
/*  346: 538 */           index--;
/*  347:     */         }
/*  348:     */         catch (NumberFormatException ex) {}
/*  349:     */       }
/*  350:     */     }
/*  351: 544 */     if (index == -1) {
/*  352: 545 */       throw new WekaException("Unable to find attribute '" + attName + "' in the data " + "or to parse it as an index");
/*  353:     */     }
/*  354: 549 */     return index;
/*  355:     */   }
/*  356:     */   
/*  357:     */   protected void initDataGenerator(Instances trainingData)
/*  358:     */     throws WekaException
/*  359:     */   {
/*  360: 555 */     boolean[] attsToWeightOn = new boolean[trainingData.numAttributes()];
/*  361: 556 */     attsToWeightOn[this.m_xAttribute] = true;
/*  362: 557 */     attsToWeightOn[this.m_yAttribute] = true;
/*  363:     */     
/*  364: 559 */     this.m_dataGenerator = new KDDataGenerator();
/*  365: 560 */     this.m_dataGenerator.setWeightingDimensions(attsToWeightOn);
/*  366: 561 */     this.m_dataGenerator.setKernelBandwidth(this.m_kernelBandwidth);
/*  367:     */     try
/*  368:     */     {
/*  369: 563 */       this.m_dataGenerator.buildGenerator(trainingData);
/*  370:     */     }
/*  371:     */     catch (Exception ex)
/*  372:     */     {
/*  373: 565 */       throw new WekaException(ex);
/*  374:     */     }
/*  375:     */   }
/*  376:     */   
/*  377:     */   public synchronized void processIncoming(Data data)
/*  378:     */     throws WekaException
/*  379:     */   {
/*  380: 572 */     getStepManager().processing();
/*  381: 573 */     Instances training = (Instances)data.getPrimaryPayload();
/*  382: 574 */     Integer setNum = (Integer)data.getPayloadElement("aux_set_num", Integer.valueOf(1));
/*  383:     */     
/*  384: 576 */     Integer maxSetNum = (Integer)data.getPayloadElement("aux_max_set_num", Integer.valueOf(1));
/*  385:     */     
/*  386:     */ 
/*  387: 579 */     this.m_xAttribute = getAttIndex(this.m_xAttName, training);
/*  388: 580 */     this.m_yAttribute = getAttIndex(this.m_yAttName, training);
/*  389:     */     
/*  390: 582 */     computeMinMaxAtts(training);
/*  391: 583 */     initDataGenerator(training);
/*  392: 585 */     for (weka.classifiers.Classifier c : this.m_classifierTemplates)
/*  393:     */     {
/*  394: 586 */       if (isStopRequested())
/*  395:     */       {
/*  396: 587 */         getStepManager().interrupted();
/*  397: 588 */         return;
/*  398:     */       }
/*  399: 591 */       doScheme(c, null, training, setNum.intValue(), maxSetNum.intValue());
/*  400:     */     }
/*  401: 594 */     for (DensityBasedClusterer c : this.m_clustererTemplates)
/*  402:     */     {
/*  403: 595 */       if (isStopRequested())
/*  404:     */       {
/*  405: 596 */         getStepManager().interrupted();
/*  406: 597 */         return;
/*  407:     */       }
/*  408: 599 */       doScheme(null, c, training, setNum.intValue(), maxSetNum.intValue());
/*  409:     */     }
/*  410: 602 */     if (isStopRequested()) {
/*  411: 603 */       getStepManager().interrupted();
/*  412:     */     } else {
/*  413: 605 */       getStepManager().finished();
/*  414:     */     }
/*  415:     */   }
/*  416:     */   
/*  417:     */   protected void doScheme(weka.classifiers.Classifier classifier, DensityBasedClusterer clust, Instances trainingData, int setNum, int maxSetNum)
/*  418:     */     throws WekaException
/*  419:     */   {
/*  420:     */     try
/*  421:     */     {
/*  422: 612 */       this.m_osi = new BufferedImage(this.m_imageWidth, this.m_imageHeight, 1);
/*  423:     */       
/*  424:     */ 
/*  425: 615 */       this.m_currentDescription = makeSchemeSpec(classifier != null ? classifier : clust, setNum, maxSetNum);
/*  426:     */       
/*  427:     */ 
/*  428:     */ 
/*  429: 619 */       getStepManager().logBasic("Starting new plot for " + this.m_currentDescription);
/*  430: 621 */       if (this.m_plotListener != null) {
/*  431: 622 */         this.m_plotListener.newPlotStarted(this.m_currentDescription);
/*  432:     */       }
/*  433: 625 */       Graphics m = this.m_osi.getGraphics();
/*  434: 626 */       m.fillRect(0, 0, this.m_imageWidth, this.m_imageHeight);
/*  435:     */       
/*  436: 628 */       weka.classifiers.Classifier toTrainClassifier = null;
/*  437: 629 */       DensityBasedClusterer toTrainClusterer = null;
/*  438: 630 */       if (classifier != null)
/*  439:     */       {
/*  440: 631 */         toTrainClassifier = AbstractClassifier.makeCopy(classifier);
/*  441:     */         
/*  442: 633 */         toTrainClassifier.buildClassifier(trainingData);
/*  443:     */       }
/*  444:     */       else
/*  445:     */       {
/*  446: 635 */         int tempClassIndex = trainingData.classIndex();
/*  447: 636 */         trainingData.setClassIndex(-1);
/*  448: 637 */         toTrainClusterer = (DensityBasedClusterer)AbstractClusterer.makeCopy(clust);
/*  449:     */         
/*  450:     */ 
/*  451: 640 */         toTrainClusterer.buildClusterer(trainingData);
/*  452: 641 */         trainingData.setClassIndex(tempClassIndex);
/*  453:     */       }
/*  454: 645 */       if (toTrainClassifier != null) {
/*  455: 646 */         this.m_threadClassifiers = AbstractClassifier.makeCopies(toTrainClassifier, this.m_maxRowsInParallel);
/*  456:     */       } else {
/*  457: 649 */         this.m_threadClusterers = AbstractClusterer.makeCopies(toTrainClusterer, this.m_maxRowsInParallel);
/*  458:     */       }
/*  459: 652 */       this.m_threadGenerators = new DataGenerator[this.m_maxRowsInParallel];
/*  460: 653 */       SerializedObject so = new SerializedObject(this.m_dataGenerator);
/*  461: 654 */       for (int i = 0; i < this.m_maxRowsInParallel; i++) {
/*  462: 655 */         this.m_threadGenerators[i] = ((DataGenerator)so.getObject());
/*  463:     */       }
/*  464: 658 */       int taskCount = 0;
/*  465: 659 */       List<Future<ExecutionResult<RowResult>>> results = new ArrayList();
/*  466: 661 */       for (int i = 0; i < this.m_imageHeight; i++) {
/*  467: 662 */         if (taskCount < this.m_maxRowsInParallel)
/*  468:     */         {
/*  469: 663 */           getStepManager().logDetailed("Launching task to compute image row " + i);
/*  470:     */           
/*  471: 665 */           SchemeRowTask t = new SchemeRowTask(this);
/*  472: 666 */           t.setResourceIntensive(isResourceIntensive());
/*  473: 667 */           t.m_classifier = null;
/*  474: 668 */           t.m_clusterer = null;
/*  475: 669 */           if (toTrainClassifier != null) {
/*  476: 670 */             t.m_classifier = this.m_threadClassifiers[taskCount];
/*  477:     */           } else {
/*  478: 672 */             t.m_clusterer = ((DensityBasedClusterer)this.m_threadClusterers[taskCount]);
/*  479:     */           }
/*  480: 675 */           t.m_rowNum = i;
/*  481: 676 */           t.m_xAtt = this.m_xAttribute;
/*  482: 677 */           t.m_yAtt = this.m_yAttribute;
/*  483: 678 */           t.m_imageWidth = this.m_imageWidth;
/*  484: 679 */           t.m_imageHeight = this.m_imageHeight;
/*  485: 680 */           t.m_pixWidth = this.m_pixWidth;
/*  486: 681 */           t.m_pixHeight = this.m_pixHeight;
/*  487: 682 */           t.m_dataGenerator = this.m_threadGenerators[taskCount];
/*  488: 683 */           t.m_trainingData = trainingData;
/*  489: 684 */           t.m_minX = this.m_minX;
/*  490: 685 */           t.m_maxX = this.m_maxX;
/*  491: 686 */           t.m_minY = this.m_minY;
/*  492: 687 */           t.m_maxY = this.m_maxY;
/*  493: 688 */           t.m_numOfSamplesPerRegion = this.m_numSamplesPerRegion;
/*  494: 689 */           t.m_samplesBase = this.m_samplesBase;
/*  495:     */           
/*  496: 691 */           results.add(getStepManager().getExecutionEnvironment().submitTask(t));
/*  497: 692 */           taskCount++;
/*  498:     */         }
/*  499:     */         else
/*  500:     */         {
/*  501: 695 */           for (Future<ExecutionResult<RowResult>> r : results)
/*  502:     */           {
/*  503: 696 */             double[][] rowProbs = ((RowResult)((ExecutionResult)r.get()).getResult()).m_rowProbs;
/*  504: 697 */             for (int j = 0; j < this.m_imageWidth; j++) {
/*  505: 698 */               plotPoint(this.m_osi, j, ((RowResult)((ExecutionResult)r.get()).getResult()).m_rowNumber, rowProbs[j], j == this.m_imageWidth - 1);
/*  506:     */             }
/*  507: 701 */             getStepManager().statusMessage("Completed row " + ((RowResult)((ExecutionResult)r.get()).getResult()).m_rowNumber);
/*  508:     */             
/*  509: 703 */             getStepManager().logDetailed("Completed image row " + ((RowResult)((ExecutionResult)r.get()).getResult()).m_rowNumber);
/*  510:     */           }
/*  511: 706 */           results.clear();
/*  512: 707 */           taskCount = 0;
/*  513: 708 */           if (i != this.m_imageHeight - 1) {
/*  514: 709 */             i--;
/*  515:     */           }
/*  516: 711 */           if (isStopRequested()) {
/*  517: 712 */             return;
/*  518:     */           }
/*  519:     */         }
/*  520:     */       }
/*  521: 716 */       if (results.size() > 0)
/*  522:     */       {
/*  523: 718 */         for (Future<ExecutionResult<RowResult>> r : results)
/*  524:     */         {
/*  525: 719 */           double[][] rowProbs = ((RowResult)((ExecutionResult)r.get()).getResult()).m_rowProbs;
/*  526: 720 */           for (int i = 0; i < this.m_imageWidth; i++) {
/*  527: 721 */             plotPoint(this.m_osi, i, ((RowResult)((ExecutionResult)r.get()).getResult()).m_rowNumber, rowProbs[i], i == this.m_imageWidth - 1);
/*  528:     */           }
/*  529: 724 */           getStepManager().statusMessage("Completed row " + ((RowResult)((ExecutionResult)r.get()).getResult()).m_rowNumber);
/*  530:     */           
/*  531: 726 */           getStepManager().logDetailed("Completed image row " + ((RowResult)((ExecutionResult)r.get()).getResult()).m_rowNumber);
/*  532:     */         }
/*  533: 729 */         if (isStopRequested()) {
/*  534: 730 */           return;
/*  535:     */         }
/*  536:     */       }
/*  537: 734 */       if (this.m_plotTrainingData) {
/*  538: 735 */         plotTrainingData(trainingData);
/*  539:     */       }
/*  540: 738 */       this.m_completedImages.put(this.m_currentDescription, this.m_osi);
/*  541: 739 */       Data imageOut = new Data("image", this.m_osi);
/*  542: 740 */       imageOut.setPayloadElement("aux_textTitle", this.m_currentDescription);
/*  543:     */       
/*  544: 742 */       getStepManager().outputData(new Data[] { imageOut });
/*  545:     */     }
/*  546:     */     catch (Exception ex)
/*  547:     */     {
/*  548: 744 */       throw new WekaException(ex);
/*  549:     */     }
/*  550:     */   }
/*  551:     */   
/*  552:     */   protected String makeSchemeSpec(Object scheme, int setNum, int maxSetNum)
/*  553:     */   {
/*  554: 749 */     String name = scheme.getClass().getCanonicalName();
/*  555: 750 */     name = name.substring(name.lastIndexOf('.') + 1, name.length());
/*  556: 751 */     if ((scheme instanceof OptionHandler)) {
/*  557: 752 */       name = name + " " + Utils.joinOptions(((OptionHandler)scheme).getOptions());
/*  558:     */     }
/*  559: 754 */     if (maxSetNum != 1) {
/*  560: 755 */       name = name + " (set " + setNum + " of " + maxSetNum + ")";
/*  561:     */     }
/*  562: 758 */     return name;
/*  563:     */   }
/*  564:     */   
/*  565:     */   protected void plotPoint(BufferedImage osi, int x, int y, double[] probs, boolean update)
/*  566:     */   {
/*  567: 763 */     Graphics osg = osi.getGraphics();
/*  568: 764 */     osg.setPaintMode();
/*  569:     */     
/*  570: 766 */     float[] colVal = new float[3];
/*  571:     */     
/*  572: 768 */     float[] tempCols = new float[3];
/*  573: 769 */     for (int k = 0; k < probs.length; k++)
/*  574:     */     {
/*  575: 770 */       Color curr = (Color)this.m_Colors.get(k % this.m_Colors.size());
/*  576:     */       
/*  577: 772 */       curr.getRGBColorComponents(tempCols);
/*  578: 773 */       for (int z = 0; z < 3; z++)
/*  579:     */       {
/*  580: 774 */         int tmp79_77 = z; float[] tmp79_75 = colVal;tmp79_75[tmp79_77] = ((float)(tmp79_75[tmp79_77] + probs[k] * tempCols[z]));
/*  581:     */       }
/*  582:     */     }
/*  583: 778 */     for (int z = 0; z < 3; z++) {
/*  584: 779 */       if (colVal[z] < 0.0F) {
/*  585: 780 */         colVal[z] = 0.0F;
/*  586: 781 */       } else if (colVal[z] > 1.0F) {
/*  587: 782 */         colVal[z] = 1.0F;
/*  588:     */       }
/*  589:     */     }
/*  590: 786 */     osg.setColor(new Color(colVal[0], colVal[1], colVal[2]));
/*  591: 787 */     osg.fillRect(x, y, 1, 1);
/*  592: 789 */     if (update) {
/*  593: 792 */       if (this.m_plotListener != null) {
/*  594: 793 */         this.m_plotListener.currentPlotRowCompleted(y);
/*  595:     */       }
/*  596:     */     }
/*  597:     */   }
/*  598:     */   
/*  599:     */   public void plotTrainingData(Instances trainingData)
/*  600:     */   {
/*  601: 799 */     Graphics2D osg = (Graphics2D)this.m_osi.getGraphics();
/*  602:     */     
/*  603: 801 */     osg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/*  604:     */     
/*  605: 803 */     double xval = 0.0D;
/*  606: 804 */     double yval = 0.0D;
/*  607: 806 */     for (int i = 0; i < trainingData.numInstances(); i++) {
/*  608: 807 */       if ((!trainingData.instance(i).isMissing(this.m_xAttribute)) && (!trainingData.instance(i).isMissing(this.m_yAttribute)))
/*  609:     */       {
/*  610: 810 */         xval = trainingData.instance(i).value(this.m_xAttribute);
/*  611: 811 */         yval = trainingData.instance(i).value(this.m_yAttribute);
/*  612:     */         
/*  613: 813 */         int panelX = convertToImageX(xval);
/*  614: 814 */         int panelY = convertToImageY(yval);
/*  615: 815 */         Color colorToPlotWith = Color.white;
/*  616: 816 */         if (trainingData.classIndex() > 0) {
/*  617: 817 */           colorToPlotWith = (Color)this.m_Colors.get((int)trainingData.instance(i).value(trainingData.classIndex()) % this.m_Colors.size());
/*  618:     */         }
/*  619: 822 */         if (colorToPlotWith.equals(Color.white)) {
/*  620: 823 */           osg.setColor(Color.black);
/*  621:     */         } else {
/*  622: 825 */           osg.setColor(Color.white);
/*  623:     */         }
/*  624: 827 */         osg.fillOval(panelX - 3, panelY - 3, 7, 7);
/*  625: 828 */         osg.setColor(colorToPlotWith);
/*  626: 829 */         osg.fillOval(panelX - 2, panelY - 2, 5, 5);
/*  627:     */       }
/*  628:     */     }
/*  629: 833 */     if (this.m_plotListener != null) {
/*  630: 834 */       this.m_plotListener.renderingImageUpdate();
/*  631:     */     }
/*  632:     */   }
/*  633:     */   
/*  634:     */   private int convertToImageX(double xval)
/*  635:     */   {
/*  636: 839 */     double temp = (xval - this.m_minX) / this.m_rangeX;
/*  637: 840 */     temp *= this.m_imageWidth;
/*  638:     */     
/*  639: 842 */     return (int)temp;
/*  640:     */   }
/*  641:     */   
/*  642:     */   private int convertToImageY(double yval)
/*  643:     */   {
/*  644: 846 */     double temp = (yval - this.m_minY) / this.m_rangeY;
/*  645: 847 */     temp *= this.m_imageHeight;
/*  646: 848 */     temp = this.m_imageHeight - temp;
/*  647:     */     
/*  648: 850 */     return (int)temp;
/*  649:     */   }
/*  650:     */   
/*  651:     */   public List<String> getIncomingConnectionTypes()
/*  652:     */   {
/*  653: 864 */     return Arrays.asList(new String[] { "dataSet", "trainingSet", "info" });
/*  654:     */   }
/*  655:     */   
/*  656:     */   public List<String> getOutgoingConnectionTypes()
/*  657:     */   {
/*  658: 879 */     return Arrays.asList(new String[] { "image" });
/*  659:     */   }
/*  660:     */   
/*  661:     */   public Map<String, BufferedImage> getImages()
/*  662:     */   {
/*  663: 888 */     return this.m_completedImages;
/*  664:     */   }
/*  665:     */   
/*  666:     */   public BufferedImage getCurrentImage()
/*  667:     */   {
/*  668: 897 */     return this.m_osi;
/*  669:     */   }
/*  670:     */   
/*  671:     */   public void setRenderingListener(RenderingUpdateListener l)
/*  672:     */   {
/*  673: 906 */     this.m_plotListener = l;
/*  674:     */   }
/*  675:     */   
/*  676:     */   public void removeRenderingListener(RenderingUpdateListener l)
/*  677:     */   {
/*  678: 915 */     if (l == this.m_plotListener) {
/*  679: 916 */       this.m_plotListener = null;
/*  680:     */     }
/*  681:     */   }
/*  682:     */   
/*  683:     */   public Map<String, String> getInteractiveViewers()
/*  684:     */   {
/*  685: 942 */     Map<String, String> views = new LinkedHashMap();
/*  686: 943 */     if (this.m_plotListener == null) {
/*  687: 944 */       views.put("Show plots", "weka.gui.knowledgeflow.steps.BoundaryPlotterInteractiveView");
/*  688:     */     }
/*  689: 947 */     return views;
/*  690:     */   }
/*  691:     */   
/*  692:     */   public String getCustomEditorForStep()
/*  693:     */   {
/*  694: 960 */     return "weka.gui.knowledgeflow.steps.BoundaryPlotterStepEditorDialog";
/*  695:     */   }
/*  696:     */   
/*  697:     */   public Object retrieveData()
/*  698:     */   {
/*  699: 970 */     return ImageViewer.bufferedImageMapToSerializableByteMap(this.m_completedImages);
/*  700:     */   }
/*  701:     */   
/*  702:     */   public void restoreData(Object data)
/*  703:     */     throws WekaException
/*  704:     */   {
/*  705: 982 */     if (!(data instanceof Map)) {
/*  706: 983 */       throw new IllegalArgumentException("Argument must be a Map");
/*  707:     */     }
/*  708:     */     try
/*  709:     */     {
/*  710: 987 */       this.m_completedImages = ImageViewer.byteArrayImageMapToBufferedImageMap((Map)data);
/*  711:     */     }
/*  712:     */     catch (IOException ex)
/*  713:     */     {
/*  714: 991 */       throw new WekaException(ex);
/*  715:     */     }
/*  716:     */   }
/*  717:     */   
/*  718:     */   protected static class SchemeRowTask
/*  719:     */     extends StepTask<BoundaryPlotter.RowResult>
/*  720:     */     implements Serializable
/*  721:     */   {
/*  722:     */     private static final long serialVersionUID = -4144732293602550066L;
/*  723:     */     protected int m_xAtt;
/*  724:     */     protected int m_yAtt;
/*  725:     */     protected int m_rowNum;
/*  726:     */     protected int m_imageWidth;
/*  727:     */     protected int m_imageHeight;
/*  728:     */     protected double m_pixWidth;
/*  729:     */     protected double m_pixHeight;
/*  730:     */     protected weka.classifiers.Classifier m_classifier;
/*  731:     */     protected DensityBasedClusterer m_clusterer;
/*  732:     */     protected DataGenerator m_dataGenerator;
/*  733:     */     protected Instances m_trainingData;
/*  734:     */     protected double m_minX;
/*  735:     */     protected double m_maxX;
/*  736:     */     protected double m_minY;
/*  737:     */     protected double m_maxY;
/*  738:     */     protected int m_numOfSamplesPerRegion;
/*  739:     */     protected double m_samplesBase;
/*  740:     */     private Random m_random;
/*  741:     */     private int m_numOfSamplesPerGenerator;
/*  742:     */     private boolean[] m_attsToWeightOn;
/*  743:     */     private double[] m_weightingAttsValues;
/*  744:     */     private double[] m_vals;
/*  745:     */     private double[] m_dist;
/*  746:     */     Instance m_predInst;
/*  747:     */     
/*  748:     */     public SchemeRowTask(Step source)
/*  749:     */     {
/*  750:1068 */       super();
/*  751:     */     }
/*  752:     */     
/*  753:     */     public void process()
/*  754:     */       throws Exception
/*  755:     */     {
/*  756:1073 */       BoundaryPlotter.RowResult result = new BoundaryPlotter.RowResult();
/*  757:1074 */       result.m_rowNumber = this.m_rowNum;
/*  758:1075 */       result.m_rowProbs = new double[this.m_imageWidth][0];
/*  759:     */       
/*  760:1077 */       this.m_random = new Random(this.m_rowNum * 11);
/*  761:1078 */       this.m_dataGenerator.setSeed(this.m_rowNum * 11);
/*  762:     */       
/*  763:1080 */       this.m_numOfSamplesPerGenerator = ((int)Math.pow(this.m_samplesBase, this.m_trainingData.numAttributes() - 3));
/*  764:1082 */       if (this.m_trainingData == null) {
/*  765:1083 */         throw new Exception("No training data set");
/*  766:     */       }
/*  767:1085 */       if ((this.m_classifier == null) && (this.m_clusterer == null)) {
/*  768:1086 */         throw new Exception("No scheme set");
/*  769:     */       }
/*  770:1088 */       if (this.m_dataGenerator == null) {
/*  771:1089 */         throw new Exception("No data generator set");
/*  772:     */       }
/*  773:1091 */       if ((this.m_trainingData.attribute(this.m_xAtt).isNominal()) || (this.m_trainingData.attribute(this.m_yAtt).isNominal())) {
/*  774:1093 */         throw new Exception("Visualization dimensions must be numeric");
/*  775:     */       }
/*  776:1096 */       this.m_attsToWeightOn = new boolean[this.m_trainingData.numAttributes()];
/*  777:1097 */       this.m_attsToWeightOn[this.m_xAtt] = true;
/*  778:1098 */       this.m_attsToWeightOn[this.m_yAtt] = true;
/*  779:     */       
/*  780:     */ 
/*  781:1101 */       this.m_weightingAttsValues = new double[this.m_attsToWeightOn.length];
/*  782:1102 */       this.m_vals = new double[this.m_trainingData.numAttributes()];
/*  783:1103 */       this.m_predInst = new DenseInstance(1.0D, this.m_vals);
/*  784:1104 */       this.m_predInst.setDataset(this.m_trainingData);
/*  785:1105 */       getLogHandler().logDetailed("Computing row number: " + this.m_rowNum);
/*  786:1106 */       for (int j = 0; j < this.m_imageWidth; j++)
/*  787:     */       {
/*  788:1107 */         double[] preds = calculateRegionProbs(j, this.m_rowNum);
/*  789:1108 */         result.m_rowProbs[j] = preds;
/*  790:     */       }
/*  791:1111 */       getExecutionResult().setResult(result);
/*  792:     */     }
/*  793:     */     
/*  794:     */     private double[] calculateRegionProbs(int j, int i)
/*  795:     */       throws Exception
/*  796:     */     {
/*  797:1115 */       double[] sumOfProbsForRegion = new double[this.m_classifier != null ? this.m_trainingData.classAttribute().numValues() : this.m_clusterer.numberOfClusters()];
/*  798:     */       
/*  799:     */ 
/*  800:     */ 
/*  801:     */ 
/*  802:1120 */       double sumOfSums = 0.0D;
/*  803:1121 */       for (int u = 0; u < this.m_numOfSamplesPerRegion; u++)
/*  804:     */       {
/*  805:1123 */         double[] sumOfProbsForLocation = new double[this.m_classifier != null ? this.m_trainingData.classAttribute().numValues() : this.m_clusterer.numberOfClusters()];
/*  806:     */         
/*  807:     */ 
/*  808:     */ 
/*  809:     */ 
/*  810:1128 */         this.m_weightingAttsValues[this.m_xAtt] = getRandomX(j);
/*  811:1129 */         this.m_weightingAttsValues[this.m_yAtt] = getRandomY(this.m_imageHeight - i - 1);
/*  812:     */         
/*  813:1131 */         this.m_dataGenerator.setWeightingValues(this.m_weightingAttsValues);
/*  814:     */         
/*  815:1133 */         double[] weights = this.m_dataGenerator.getWeights();
/*  816:1134 */         double sumOfWeights = Utils.sum(weights);
/*  817:1135 */         sumOfSums += sumOfWeights;
/*  818:1136 */         int[] indices = Utils.sort(weights);
/*  819:     */         
/*  820:     */ 
/*  821:1139 */         int[] newIndices = new int[indices.length];
/*  822:1140 */         double sumSoFar = 0.0D;
/*  823:1141 */         double criticalMass = 0.99D * sumOfWeights;
/*  824:1142 */         int index = weights.length - 1;
/*  825:1143 */         int counter = 0;
/*  826:1144 */         for (int z = weights.length - 1; z >= 0; z--)
/*  827:     */         {
/*  828:1145 */           newIndices[(index--)] = indices[z];
/*  829:1146 */           sumSoFar += weights[indices[z]];
/*  830:1147 */           counter++;
/*  831:1148 */           if (sumSoFar > criticalMass) {
/*  832:     */             break;
/*  833:     */           }
/*  834:     */         }
/*  835:1152 */         indices = new int[counter];
/*  836:1153 */         System.arraycopy(newIndices, index + 1, indices, 0, counter);
/*  837:1155 */         for (int z = 0; z < this.m_numOfSamplesPerGenerator; z++)
/*  838:     */         {
/*  839:1157 */           this.m_dataGenerator.setWeightingValues(this.m_weightingAttsValues);
/*  840:1158 */           double[][] values = this.m_dataGenerator.generateInstances(indices);
/*  841:1160 */           for (int q = 0; q < values.length; q++) {
/*  842:1161 */             if (values[q] != null)
/*  843:     */             {
/*  844:1162 */               System.arraycopy(values[q], 0, this.m_vals, 0, this.m_vals.length);
/*  845:1163 */               this.m_vals[this.m_xAtt] = this.m_weightingAttsValues[this.m_xAtt];
/*  846:1164 */               this.m_vals[this.m_yAtt] = this.m_weightingAttsValues[this.m_yAtt];
/*  847:     */               
/*  848:     */ 
/*  849:1167 */               this.m_dist = (this.m_classifier != null ? this.m_classifier.distributionForInstance(this.m_predInst) : this.m_clusterer.distributionForInstance(this.m_predInst));
/*  850:1172 */               for (int k = 0; k < sumOfProbsForLocation.length; k++) {
/*  851:1173 */                 sumOfProbsForLocation[k] += this.m_dist[k] * weights[q];
/*  852:     */               }
/*  853:     */             }
/*  854:     */           }
/*  855:     */         }
/*  856:1179 */         for (int k = 0; k < sumOfProbsForRegion.length; k++) {
/*  857:1180 */           sumOfProbsForRegion[k] += sumOfProbsForLocation[k] / this.m_numOfSamplesPerGenerator;
/*  858:     */         }
/*  859:     */       }
/*  860:1185 */       if (sumOfSums > 0.0D) {
/*  861:1187 */         Utils.normalize(sumOfProbsForRegion, sumOfSums);
/*  862:     */       } else {
/*  863:1189 */         throw new Exception("Arithmetic underflow. Please increase value of kernel bandwidth parameter (k).");
/*  864:     */       }
/*  865:1195 */       double[] tempDist = new double[sumOfProbsForRegion.length];
/*  866:1196 */       System.arraycopy(sumOfProbsForRegion, 0, tempDist, 0, sumOfProbsForRegion.length);
/*  867:     */       
/*  868:     */ 
/*  869:1199 */       return tempDist;
/*  870:     */     }
/*  871:     */     
/*  872:     */     private double getRandomX(int pix)
/*  873:     */     {
/*  874:1211 */       double minPix = this.m_minX + pix * this.m_pixWidth;
/*  875:     */       
/*  876:1213 */       return minPix + this.m_random.nextDouble() * this.m_pixWidth;
/*  877:     */     }
/*  878:     */     
/*  879:     */     private double getRandomY(int pix)
/*  880:     */     {
/*  881:1225 */       double minPix = this.m_minY + pix * this.m_pixHeight;
/*  882:     */       
/*  883:1227 */       return minPix + this.m_random.nextDouble() * this.m_pixHeight;
/*  884:     */     }
/*  885:     */   }
/*  886:     */   
/*  887:     */   protected static class RowResult
/*  888:     */   {
/*  889:     */     protected double[][] m_rowProbs;
/*  890:     */     protected int m_rowNumber;
/*  891:     */   }
/*  892:     */   
/*  893:     */   public static abstract interface RenderingUpdateListener
/*  894:     */   {
/*  895:     */     public abstract void newPlotStarted(String paramString);
/*  896:     */     
/*  897:     */     public abstract void currentPlotRowCompleted(int paramInt);
/*  898:     */     
/*  899:     */     public abstract void renderingImageUpdate();
/*  900:     */   }
/*  901:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.knowledgeflow.steps.BoundaryPlotter
 * JD-Core Version:    0.7.0.1
 */