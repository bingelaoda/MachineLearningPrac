/*    1:     */ package weka.core.pmml.jaxbbindings;
/*    2:     */ 
/*    3:     */ import java.util.List;
/*    4:     */ import javax.xml.bind.JAXBElement;
/*    5:     */ import javax.xml.bind.annotation.XmlElementDecl;
/*    6:     */ import javax.xml.bind.annotation.XmlRegistry;
/*    7:     */ import javax.xml.namespace.QName;
/*    8:     */ 
/*    9:     */ @XmlRegistry
/*   10:     */ public class ObjectFactory
/*   11:     */ {
/*   12:  35 */   private static final QName _SpectralAnalysis_QNAME = new QName("http://www.dmg.org/PMML-4_1", "SpectralAnalysis");
/*   13:  36 */   private static final QName _REALEntries_QNAME = new QName("http://www.dmg.org/PMML-4_1", "REAL-Entries");
/*   14:  37 */   private static final QName _INTEntries_QNAME = new QName("http://www.dmg.org/PMML-4_1", "INT-Entries");
/*   15:  38 */   private static final QName _CountTable_QNAME = new QName("http://www.dmg.org/PMML-4_1", "CountTable");
/*   16:  39 */   private static final QName _ARIMA_QNAME = new QName("http://www.dmg.org/PMML-4_1", "ARIMA");
/*   17:  40 */   private static final QName _Indices_QNAME = new QName("http://www.dmg.org/PMML-4_1", "Indices");
/*   18:  41 */   private static final QName _SeasonalTrendDecomposition_QNAME = new QName("http://www.dmg.org/PMML-4_1", "SeasonalTrendDecomposition");
/*   19:  42 */   private static final QName _NormalizedCountTable_QNAME = new QName("http://www.dmg.org/PMML-4_1", "NormalizedCountTable");
/*   20:  43 */   private static final QName _Array_QNAME = new QName("http://www.dmg.org/PMML-4_1", "Array");
/*   21:     */   
/*   22:     */   public PPCell createPPCell()
/*   23:     */   {
/*   24:  57 */     return new PPCell();
/*   25:     */   }
/*   26:     */   
/*   27:     */   public Time createTime()
/*   28:     */   {
/*   29:  65 */     return new Time();
/*   30:     */   }
/*   31:     */   
/*   32:     */   public Taxonomy createTaxonomy()
/*   33:     */   {
/*   34:  73 */     return new Taxonomy();
/*   35:     */   }
/*   36:     */   
/*   37:     */   public RuleSetModel createRuleSetModel()
/*   38:     */   {
/*   39:  81 */     return new RuleSetModel();
/*   40:     */   }
/*   41:     */   
/*   42:     */   public Node createNode()
/*   43:     */   {
/*   44:  89 */     return new Node();
/*   45:     */   }
/*   46:     */   
/*   47:     */   public DecisionTree createDecisionTree()
/*   48:     */   {
/*   49:  97 */     return new DecisionTree();
/*   50:     */   }
/*   51:     */   
/*   52:     */   public TrendExpoSmooth createTrendExpoSmooth()
/*   53:     */   {
/*   54: 105 */     return new TrendExpoSmooth();
/*   55:     */   }
/*   56:     */   
/*   57:     */   public GaussianDistribution createGaussianDistribution()
/*   58:     */   {
/*   59: 113 */     return new GaussianDistribution();
/*   60:     */   }
/*   61:     */   
/*   62:     */   public NeuralInputs createNeuralInputs()
/*   63:     */   {
/*   64: 121 */     return new NeuralInputs();
/*   65:     */   }
/*   66:     */   
/*   67:     */   public VectorInstance createVectorInstance()
/*   68:     */   {
/*   69: 129 */     return new VectorInstance();
/*   70:     */   }
/*   71:     */   
/*   72:     */   public BinarySimilarity createBinarySimilarity()
/*   73:     */   {
/*   74: 137 */     return new BinarySimilarity();
/*   75:     */   }
/*   76:     */   
/*   77:     */   public Baseline createBaseline()
/*   78:     */   {
/*   79: 145 */     return new Baseline();
/*   80:     */   }
/*   81:     */   
/*   82:     */   public Jaccard createJaccard()
/*   83:     */   {
/*   84: 153 */     return new Jaccard();
/*   85:     */   }
/*   86:     */   
/*   87:     */   public Interval createInterval()
/*   88:     */   {
/*   89: 161 */     return new Interval();
/*   90:     */   }
/*   91:     */   
/*   92:     */   public TargetValueCount createTargetValueCount()
/*   93:     */   {
/*   94: 169 */     return new TargetValueCount();
/*   95:     */   }
/*   96:     */   
/*   97:     */   public Partition createPartition()
/*   98:     */   {
/*   99: 177 */     return new Partition();
/*  100:     */   }
/*  101:     */   
/*  102:     */   public Aggregate createAggregate()
/*  103:     */   {
/*  104: 185 */     return new Aggregate();
/*  105:     */   }
/*  106:     */   
/*  107:     */   public RegressionModel createRegressionModel()
/*  108:     */   {
/*  109: 193 */     return new RegressionModel();
/*  110:     */   }
/*  111:     */   
/*  112:     */   public BoundaryValueMeans createBoundaryValueMeans()
/*  113:     */   {
/*  114: 201 */     return new BoundaryValueMeans();
/*  115:     */   }
/*  116:     */   
/*  117:     */   public ChildParent createChildParent()
/*  118:     */   {
/*  119: 209 */     return new ChildParent();
/*  120:     */   }
/*  121:     */   
/*  122:     */   public Timestamp createTimestamp()
/*  123:     */   {
/*  124: 217 */     return new Timestamp();
/*  125:     */   }
/*  126:     */   
/*  127:     */   public VectorFields createVectorFields()
/*  128:     */   {
/*  129: 225 */     return new VectorFields();
/*  130:     */   }
/*  131:     */   
/*  132:     */   public TransformationDictionary createTransformationDictionary()
/*  133:     */   {
/*  134: 233 */     return new TransformationDictionary();
/*  135:     */   }
/*  136:     */   
/*  137:     */   public CompoundPredicate createCompoundPredicate()
/*  138:     */   {
/*  139: 241 */     return new CompoundPredicate();
/*  140:     */   }
/*  141:     */   
/*  142:     */   public ROCGraph createROCGraph()
/*  143:     */   {
/*  144: 249 */     return new ROCGraph();
/*  145:     */   }
/*  146:     */   
/*  147:     */   public ModelExplanation createModelExplanation()
/*  148:     */   {
/*  149: 257 */     return new ModelExplanation();
/*  150:     */   }
/*  151:     */   
/*  152:     */   public PMML createPMML()
/*  153:     */   {
/*  154: 265 */     return new PMML();
/*  155:     */   }
/*  156:     */   
/*  157:     */   public COUNTTABLETYPE createCOUNTTABLETYPE()
/*  158:     */   {
/*  159: 273 */     return new COUNTTABLETYPE();
/*  160:     */   }
/*  161:     */   
/*  162:     */   public Output createOutput()
/*  163:     */   {
/*  164: 281 */     return new Output();
/*  165:     */   }
/*  166:     */   
/*  167:     */   public Annotation createAnnotation()
/*  168:     */   {
/*  169: 289 */     return new Annotation();
/*  170:     */   }
/*  171:     */   
/*  172:     */   public KohonenMap createKohonenMap()
/*  173:     */   {
/*  174: 297 */     return new KohonenMap();
/*  175:     */   }
/*  176:     */   
/*  177:     */   public SeasonalityExpoSmooth createSeasonalityExpoSmooth()
/*  178:     */   {
/*  179: 305 */     return new SeasonalityExpoSmooth();
/*  180:     */   }
/*  181:     */   
/*  182:     */   public NumericPredictor createNumericPredictor()
/*  183:     */   {
/*  184: 313 */     return new NumericPredictor();
/*  185:     */   }
/*  186:     */   
/*  187:     */   public TimeException createTimeException()
/*  188:     */   {
/*  189: 321 */     return new TimeException();
/*  190:     */   }
/*  191:     */   
/*  192:     */   public LiftData createLiftData()
/*  193:     */   {
/*  194: 329 */     return new LiftData();
/*  195:     */   }
/*  196:     */   
/*  197:     */   public Target createTarget()
/*  198:     */   {
/*  199: 337 */     return new Target();
/*  200:     */   }
/*  201:     */   
/*  202:     */   public Targets createTargets()
/*  203:     */   {
/*  204: 345 */     return new Targets();
/*  205:     */   }
/*  206:     */   
/*  207:     */   public OutputField createOutputField()
/*  208:     */   {
/*  209: 353 */     return new OutputField();
/*  210:     */   }
/*  211:     */   
/*  212:     */   public NeuralNetwork createNeuralNetwork()
/*  213:     */   {
/*  214: 361 */     return new NeuralNetwork();
/*  215:     */   }
/*  216:     */   
/*  217:     */   public RuleSelectionMethod createRuleSelectionMethod()
/*  218:     */   {
/*  219: 369 */     return new RuleSelectionMethod();
/*  220:     */   }
/*  221:     */   
/*  222:     */   public TextDictionary createTextDictionary()
/*  223:     */   {
/*  224: 377 */     return new TextDictionary();
/*  225:     */   }
/*  226:     */   
/*  227:     */   public Matrix createMatrix()
/*  228:     */   {
/*  229: 385 */     return new Matrix();
/*  230:     */   }
/*  231:     */   
/*  232:     */   public CompoundRule createCompoundRule()
/*  233:     */   {
/*  234: 393 */     return new CompoundRule();
/*  235:     */   }
/*  236:     */   
/*  237:     */   public MultivariateStats createMultivariateStats()
/*  238:     */   {
/*  239: 401 */     return new MultivariateStats();
/*  240:     */   }
/*  241:     */   
/*  242:     */   public NaiveBayesModel createNaiveBayesModel()
/*  243:     */   {
/*  244: 409 */     return new NaiveBayesModel();
/*  245:     */   }
/*  246:     */   
/*  247:     */   public Anova createAnova()
/*  248:     */   {
/*  249: 417 */     return new Anova();
/*  250:     */   }
/*  251:     */   
/*  252:     */   public Covariances createCovariances()
/*  253:     */   {
/*  254: 425 */     return new Covariances();
/*  255:     */   }
/*  256:     */   
/*  257:     */   public NearestNeighborModel createNearestNeighborModel()
/*  258:     */   {
/*  259: 433 */     return new NearestNeighborModel();
/*  260:     */   }
/*  261:     */   
/*  262:     */   public SupportVectors createSupportVectors()
/*  263:     */   {
/*  264: 441 */     return new SupportVectors();
/*  265:     */   }
/*  266:     */   
/*  267:     */   public SetPredicate createSetPredicate()
/*  268:     */   {
/*  269: 449 */     return new SetPredicate();
/*  270:     */   }
/*  271:     */   
/*  272:     */   public VerificationField createVerificationField()
/*  273:     */   {
/*  274: 457 */     return new VerificationField();
/*  275:     */   }
/*  276:     */   
/*  277:     */   public TextDocument createTextDocument()
/*  278:     */   {
/*  279: 465 */     return new TextDocument();
/*  280:     */   }
/*  281:     */   
/*  282:     */   public SupportVector createSupportVector()
/*  283:     */   {
/*  284: 473 */     return new SupportVector();
/*  285:     */   }
/*  286:     */   
/*  287:     */   public SequenceRule createSequenceRule()
/*  288:     */   {
/*  289: 481 */     return new SequenceRule();
/*  290:     */   }
/*  291:     */   
/*  292:     */   public FieldValue createFieldValue()
/*  293:     */   {
/*  294: 489 */     return new FieldValue();
/*  295:     */   }
/*  296:     */   
/*  297:     */   public True createTrue()
/*  298:     */   {
/*  299: 497 */     return new True();
/*  300:     */   }
/*  301:     */   
/*  302:     */   public DataDictionary createDataDictionary()
/*  303:     */   {
/*  304: 505 */     return new DataDictionary();
/*  305:     */   }
/*  306:     */   
/*  307:     */   public LiftGraph createLiftGraph()
/*  308:     */   {
/*  309: 513 */     return new LiftGraph();
/*  310:     */   }
/*  311:     */   
/*  312:     */   public SimpleRule createSimpleRule()
/*  313:     */   {
/*  314: 521 */     return new SimpleRule();
/*  315:     */   }
/*  316:     */   
/*  317:     */   public AntecedentSequence createAntecedentSequence()
/*  318:     */   {
/*  319: 529 */     return new AntecedentSequence();
/*  320:     */   }
/*  321:     */   
/*  322:     */   public LocalTransformations createLocalTransformations()
/*  323:     */   {
/*  324: 537 */     return new LocalTransformations();
/*  325:     */   }
/*  326:     */   
/*  327:     */   public SquaredEuclidean createSquaredEuclidean()
/*  328:     */   {
/*  329: 545 */     return new SquaredEuclidean();
/*  330:     */   }
/*  331:     */   
/*  332:     */   public Con1 createCon()
/*  333:     */   {
/*  334: 553 */     return new Con1();
/*  335:     */   }
/*  336:     */   
/*  337:     */   public NormContinuous createNormContinuous()
/*  338:     */   {
/*  339: 561 */     return new NormContinuous();
/*  340:     */   }
/*  341:     */   
/*  342:     */   public Segment createSegment()
/*  343:     */   {
/*  344: 569 */     return new Segment();
/*  345:     */   }
/*  346:     */   
/*  347:     */   public KNNInputs createKNNInputs()
/*  348:     */   {
/*  349: 577 */     return new KNNInputs();
/*  350:     */   }
/*  351:     */   
/*  352:     */   public FieldValueCount createFieldValueCount()
/*  353:     */   {
/*  354: 585 */     return new FieldValueCount();
/*  355:     */   }
/*  356:     */   
/*  357:     */   public BayesOutput createBayesOutput()
/*  358:     */   {
/*  359: 593 */     return new BayesOutput();
/*  360:     */   }
/*  361:     */   
/*  362:     */   public ClusteringModelQuality createClusteringModelQuality()
/*  363:     */   {
/*  364: 601 */     return new ClusteringModelQuality();
/*  365:     */   }
/*  366:     */   
/*  367:     */   public ClusteringModel createClusteringModel()
/*  368:     */   {
/*  369: 609 */     return new ClusteringModel();
/*  370:     */   }
/*  371:     */   
/*  372:     */   public Chebychev createChebychev()
/*  373:     */   {
/*  374: 617 */     return new Chebychev();
/*  375:     */   }
/*  376:     */   
/*  377:     */   public PairCounts createPairCounts()
/*  378:     */   {
/*  379: 625 */     return new PairCounts();
/*  380:     */   }
/*  381:     */   
/*  382:     */   public ConfusionMatrix createConfusionMatrix()
/*  383:     */   {
/*  384: 633 */     return new ConfusionMatrix();
/*  385:     */   }
/*  386:     */   
/*  387:     */   public Correlations createCorrelations()
/*  388:     */   {
/*  389: 641 */     return new Correlations();
/*  390:     */   }
/*  391:     */   
/*  392:     */   public SetReference createSetReference()
/*  393:     */   {
/*  394: 649 */     return new SetReference();
/*  395:     */   }
/*  396:     */   
/*  397:     */   public MiningBuildTask createMiningBuildTask()
/*  398:     */   {
/*  399: 657 */     return new MiningBuildTask();
/*  400:     */   }
/*  401:     */   
/*  402:     */   public TreeModel createTreeModel()
/*  403:     */   {
/*  404: 665 */     return new TreeModel();
/*  405:     */   }
/*  406:     */   
/*  407:     */   public CorrelationFields createCorrelationFields()
/*  408:     */   {
/*  409: 673 */     return new CorrelationFields();
/*  410:     */   }
/*  411:     */   
/*  412:     */   public Coefficients createCoefficients()
/*  413:     */   {
/*  414: 681 */     return new Coefficients();
/*  415:     */   }
/*  416:     */   
/*  417:     */   public PoissonDistribution createPoissonDistribution()
/*  418:     */   {
/*  419: 689 */     return new PoissonDistribution();
/*  420:     */   }
/*  421:     */   
/*  422:     */   public TimeCycle createTimeCycle()
/*  423:     */   {
/*  424: 697 */     return new TimeCycle();
/*  425:     */   }
/*  426:     */   
/*  427:     */   public Predictor createPredictor()
/*  428:     */   {
/*  429: 705 */     return new Predictor();
/*  430:     */   }
/*  431:     */   
/*  432:     */   public BaselineCell createBaselineCell()
/*  433:     */   {
/*  434: 713 */     return new BaselineCell();
/*  435:     */   }
/*  436:     */   
/*  437:     */   public TextModelSimiliarity createTextModelSimiliarity()
/*  438:     */   {
/*  439: 721 */     return new TextModelSimiliarity();
/*  440:     */   }
/*  441:     */   
/*  442:     */   public ExponentialSmoothing createExponentialSmoothing()
/*  443:     */   {
/*  444: 729 */     return new ExponentialSmoothing();
/*  445:     */   }
/*  446:     */   
/*  447:     */   public Sequence createSequence()
/*  448:     */   {
/*  449: 737 */     return new Sequence();
/*  450:     */   }
/*  451:     */   
/*  452:     */   public PCovMatrix createPCovMatrix()
/*  453:     */   {
/*  454: 745 */     return new PCovMatrix();
/*  455:     */   }
/*  456:     */   
/*  457:     */   public RuleSet createRuleSet()
/*  458:     */   {
/*  459: 753 */     return new RuleSet();
/*  460:     */   }
/*  461:     */   
/*  462:     */   public Application createApplication()
/*  463:     */   {
/*  464: 761 */     return new Application();
/*  465:     */   }
/*  466:     */   
/*  467:     */   public AnyDistribution createAnyDistribution()
/*  468:     */   {
/*  469: 769 */     return new AnyDistribution();
/*  470:     */   }
/*  471:     */   
/*  472:     */   public SupportVectorMachine createSupportVectorMachine()
/*  473:     */   {
/*  474: 777 */     return new SupportVectorMachine();
/*  475:     */   }
/*  476:     */   
/*  477:     */   public BayesInput createBayesInput()
/*  478:     */   {
/*  479: 785 */     return new BayesInput();
/*  480:     */   }
/*  481:     */   
/*  482:     */   public Minkowski createMinkowski()
/*  483:     */   {
/*  484: 793 */     return new Minkowski();
/*  485:     */   }
/*  486:     */   
/*  487:     */   public VerificationFields createVerificationFields()
/*  488:     */   {
/*  489: 801 */     return new VerificationFields();
/*  490:     */   }
/*  491:     */   
/*  492:     */   public RadialBasisKernelType createRadialBasisKernelType()
/*  493:     */   {
/*  494: 809 */     return new RadialBasisKernelType();
/*  495:     */   }
/*  496:     */   
/*  497:     */   public UniformDistribution createUniformDistribution()
/*  498:     */   {
/*  499: 817 */     return new UniformDistribution();
/*  500:     */   }
/*  501:     */   
/*  502:     */   public InlineTable createInlineTable()
/*  503:     */   {
/*  504: 825 */     return new InlineTable();
/*  505:     */   }
/*  506:     */   
/*  507:     */   public LinearNorm createLinearNorm()
/*  508:     */   {
/*  509: 833 */     return new LinearNorm();
/*  510:     */   }
/*  511:     */   
/*  512:     */   public ClassLabels createClassLabels()
/*  513:     */   {
/*  514: 841 */     return new ClassLabels();
/*  515:     */   }
/*  516:     */   
/*  517:     */   public LinearKernelType createLinearKernelType()
/*  518:     */   {
/*  519: 849 */     return new LinearKernelType();
/*  520:     */   }
/*  521:     */   
/*  522:     */   public DataField createDataField()
/*  523:     */   {
/*  524: 857 */     return new DataField();
/*  525:     */   }
/*  526:     */   
/*  527:     */   public AssociationRule createAssociationRule()
/*  528:     */   {
/*  529: 865 */     return new AssociationRule();
/*  530:     */   }
/*  531:     */   
/*  532:     */   public KNNInput createKNNInput()
/*  533:     */   {
/*  534: 873 */     return new KNNInput();
/*  535:     */   }
/*  536:     */   
/*  537:     */   public FieldRef createFieldRef()
/*  538:     */   {
/*  539: 881 */     return new FieldRef();
/*  540:     */   }
/*  541:     */   
/*  542:     */   public PolynomialKernelType createPolynomialKernelType()
/*  543:     */   {
/*  544: 889 */     return new PolynomialKernelType();
/*  545:     */   }
/*  546:     */   
/*  547:     */   public Counts createCounts()
/*  548:     */   {
/*  549: 897 */     return new Counts();
/*  550:     */   }
/*  551:     */   
/*  552:     */   public Cluster createCluster()
/*  553:     */   {
/*  554: 905 */     return new Cluster();
/*  555:     */   }
/*  556:     */   
/*  557:     */   public Characteristic createCharacteristic()
/*  558:     */   {
/*  559: 913 */     return new Characteristic();
/*  560:     */   }
/*  561:     */   
/*  562:     */   public MiningSchema createMiningSchema()
/*  563:     */   {
/*  564: 921 */     return new MiningSchema();
/*  565:     */   }
/*  566:     */   
/*  567:     */   public TextModel createTextModel()
/*  568:     */   {
/*  569: 929 */     return new TextModel();
/*  570:     */   }
/*  571:     */   
/*  572:     */   public Categories createCategories()
/*  573:     */   {
/*  574: 937 */     return new Categories();
/*  575:     */   }
/*  576:     */   
/*  577:     */   public Characteristics createCharacteristics()
/*  578:     */   {
/*  579: 945 */     return new Characteristics();
/*  580:     */   }
/*  581:     */   
/*  582:     */   public PPMatrix createPPMatrix()
/*  583:     */   {
/*  584: 953 */     return new PPMatrix();
/*  585:     */   }
/*  586:     */   
/*  587:     */   public Constraints createConstraints()
/*  588:     */   {
/*  589: 961 */     return new Constraints();
/*  590:     */   }
/*  591:     */   
/*  592:     */   public MultivariateStat createMultivariateStat()
/*  593:     */   {
/*  594: 969 */     return new MultivariateStat();
/*  595:     */   }
/*  596:     */   
/*  597:     */   public CorrelationMethods createCorrelationMethods()
/*  598:     */   {
/*  599: 977 */     return new CorrelationMethods();
/*  600:     */   }
/*  601:     */   
/*  602:     */   public NeuralOutputs createNeuralOutputs()
/*  603:     */   {
/*  604: 985 */     return new NeuralOutputs();
/*  605:     */   }
/*  606:     */   
/*  607:     */   public Extension createExtension()
/*  608:     */   {
/*  609: 993 */     return new Extension();
/*  610:     */   }
/*  611:     */   
/*  612:     */   public MiningModel createMiningModel()
/*  613:     */   {
/*  614:1001 */     return new MiningModel();
/*  615:     */   }
/*  616:     */   
/*  617:     */   public ArrayType createArrayType()
/*  618:     */   {
/*  619:1009 */     return new ArrayType();
/*  620:     */   }
/*  621:     */   
/*  622:     */   public DocumentTermMatrix createDocumentTermMatrix()
/*  623:     */   {
/*  624:1017 */     return new DocumentTermMatrix();
/*  625:     */   }
/*  626:     */   
/*  627:     */   public MiningField createMiningField()
/*  628:     */   {
/*  629:1025 */     return new MiningField();
/*  630:     */   }
/*  631:     */   
/*  632:     */   public Header createHeader()
/*  633:     */   {
/*  634:1033 */     return new Header();
/*  635:     */   }
/*  636:     */   
/*  637:     */   public Value createValue()
/*  638:     */   {
/*  639:1041 */     return new Value();
/*  640:     */   }
/*  641:     */   
/*  642:     */   public SimpleMatching createSimpleMatching()
/*  643:     */   {
/*  644:1049 */     return new SimpleMatching();
/*  645:     */   }
/*  646:     */   
/*  647:     */   public TextModelNormalization createTextModelNormalization()
/*  648:     */   {
/*  649:1057 */     return new TextModelNormalization();
/*  650:     */   }
/*  651:     */   
/*  652:     */   public EventValues createEventValues()
/*  653:     */   {
/*  654:1065 */     return new EventValues();
/*  655:     */   }
/*  656:     */   
/*  657:     */   public Coefficient createCoefficient()
/*  658:     */   {
/*  659:1073 */     return new Coefficient();
/*  660:     */   }
/*  661:     */   
/*  662:     */   public ComparisonMeasure createComparisonMeasure()
/*  663:     */   {
/*  664:1081 */     return new ComparisonMeasure();
/*  665:     */   }
/*  666:     */   
/*  667:     */   public Comparisons createComparisons()
/*  668:     */   {
/*  669:1089 */     return new Comparisons();
/*  670:     */   }
/*  671:     */   
/*  672:     */   public ScoreDistribution createScoreDistribution()
/*  673:     */   {
/*  674:1097 */     return new ScoreDistribution();
/*  675:     */   }
/*  676:     */   
/*  677:     */   public TestDistributions createTestDistributions()
/*  678:     */   {
/*  679:1105 */     return new TestDistributions();
/*  680:     */   }
/*  681:     */   
/*  682:     */   public NeuralInput createNeuralInput()
/*  683:     */   {
/*  684:1113 */     return new NeuralInput();
/*  685:     */   }
/*  686:     */   
/*  687:     */   public ModelVerification createModelVerification()
/*  688:     */   {
/*  689:1121 */     return new ModelVerification();
/*  690:     */   }
/*  691:     */   
/*  692:     */   public Attribute createAttribute()
/*  693:     */   {
/*  694:1129 */     return new Attribute();
/*  695:     */   }
/*  696:     */   
/*  697:     */   public PredictorTerm createPredictorTerm()
/*  698:     */   {
/*  699:1137 */     return new PredictorTerm();
/*  700:     */   }
/*  701:     */   
/*  702:     */   public Regression createRegression()
/*  703:     */   {
/*  704:1145 */     return new Regression();
/*  705:     */   }
/*  706:     */   
/*  707:     */   public PartitionFieldStats createPartitionFieldStats()
/*  708:     */   {
/*  709:1153 */     return new PartitionFieldStats();
/*  710:     */   }
/*  711:     */   
/*  712:     */   public YCoordinates createYCoordinates()
/*  713:     */   {
/*  714:1161 */     return new YCoordinates();
/*  715:     */   }
/*  716:     */   
/*  717:     */   public NeuralOutput createNeuralOutput()
/*  718:     */   {
/*  719:1169 */     return new NeuralOutput();
/*  720:     */   }
/*  721:     */   
/*  722:     */   public FactorList createFactorList()
/*  723:     */   {
/*  724:1177 */     return new FactorList();
/*  725:     */   }
/*  726:     */   
/*  727:     */   public TargetValueCounts createTargetValueCounts()
/*  728:     */   {
/*  729:1185 */     return new TargetValueCounts();
/*  730:     */   }
/*  731:     */   
/*  732:     */   public TrainingInstances createTrainingInstances()
/*  733:     */   {
/*  734:1193 */     return new TrainingInstances();
/*  735:     */   }
/*  736:     */   
/*  737:     */   public SigmoidKernelType createSigmoidKernelType()
/*  738:     */   {
/*  739:1201 */     return new SigmoidKernelType();
/*  740:     */   }
/*  741:     */   
/*  742:     */   public Segmentation createSegmentation()
/*  743:     */   {
/*  744:1209 */     return new Segmentation();
/*  745:     */   }
/*  746:     */   
/*  747:     */   public NormDiscrete createNormDiscrete()
/*  748:     */   {
/*  749:1217 */     return new NormDiscrete();
/*  750:     */   }
/*  751:     */   
/*  752:     */   public RandomLiftGraph createRandomLiftGraph()
/*  753:     */   {
/*  754:1225 */     return new RandomLiftGraph();
/*  755:     */   }
/*  756:     */   
/*  757:     */   public ParameterField createParameterField()
/*  758:     */   {
/*  759:1233 */     return new ParameterField();
/*  760:     */   }
/*  761:     */   
/*  762:     */   public SimplePredicate createSimplePredicate()
/*  763:     */   {
/*  764:1241 */     return new SimplePredicate();
/*  765:     */   }
/*  766:     */   
/*  767:     */   public PCell createPCell()
/*  768:     */   {
/*  769:1249 */     return new PCell();
/*  770:     */   }
/*  771:     */   
/*  772:     */   public Scorecard createScorecard()
/*  773:     */   {
/*  774:1257 */     return new Scorecard();
/*  775:     */   }
/*  776:     */   
/*  777:     */   public Decisions createDecisions()
/*  778:     */   {
/*  779:1265 */     return new Decisions();
/*  780:     */   }
/*  781:     */   
/*  782:     */   public Parameter createParameter()
/*  783:     */   {
/*  784:1273 */     return new Parameter();
/*  785:     */   }
/*  786:     */   
/*  787:     */   public CovariateList createCovariateList()
/*  788:     */   {
/*  789:1281 */     return new CovariateList();
/*  790:     */   }
/*  791:     */   
/*  792:     */   public ConsequentSequence createConsequentSequence()
/*  793:     */   {
/*  794:1289 */     return new ConsequentSequence();
/*  795:     */   }
/*  796:     */   
/*  797:     */   public ModelLiftGraph createModelLiftGraph()
/*  798:     */   {
/*  799:1297 */     return new ModelLiftGraph();
/*  800:     */   }
/*  801:     */   
/*  802:     */   public MatCell createMatCell()
/*  803:     */   {
/*  804:1305 */     return new MatCell();
/*  805:     */   }
/*  806:     */   
/*  807:     */   public GeneralRegressionModel createGeneralRegressionModel()
/*  808:     */   {
/*  809:1313 */     return new GeneralRegressionModel();
/*  810:     */   }
/*  811:     */   
/*  812:     */   public TimeSeries createTimeSeries()
/*  813:     */   {
/*  814:1321 */     return new TimeSeries();
/*  815:     */   }
/*  816:     */   
/*  817:     */   public ItemRef createItemRef()
/*  818:     */   {
/*  819:1329 */     return new ItemRef();
/*  820:     */   }
/*  821:     */   
/*  822:     */   public SequenceModel createSequenceModel()
/*  823:     */   {
/*  824:1337 */     return new SequenceModel();
/*  825:     */   }
/*  826:     */   
/*  827:     */   public ClusteringField createClusteringField()
/*  828:     */   {
/*  829:1345 */     return new ClusteringField();
/*  830:     */   }
/*  831:     */   
/*  832:     */   public Row createRow()
/*  833:     */   {
/*  834:1353 */     return new Row();
/*  835:     */   }
/*  836:     */   
/*  837:     */   public BaseCumHazardTables createBaseCumHazardTables()
/*  838:     */   {
/*  839:1361 */     return new BaseCumHazardTables();
/*  840:     */   }
/*  841:     */   
/*  842:     */   public BaselineModel createBaselineModel()
/*  843:     */   {
/*  844:1369 */     return new BaselineModel();
/*  845:     */   }
/*  846:     */   
/*  847:     */   public TableLocator createTableLocator()
/*  848:     */   {
/*  849:1377 */     return new TableLocator();
/*  850:     */   }
/*  851:     */   
/*  852:     */   public SimpleSetPredicate createSimpleSetPredicate()
/*  853:     */   {
/*  854:1385 */     return new SimpleSetPredicate();
/*  855:     */   }
/*  856:     */   
/*  857:     */   public Discretize createDiscretize()
/*  858:     */   {
/*  859:1393 */     return new Discretize();
/*  860:     */   }
/*  861:     */   
/*  862:     */   public Decision createDecision()
/*  863:     */   {
/*  864:1401 */     return new Decision();
/*  865:     */   }
/*  866:     */   
/*  867:     */   public Level createLevel()
/*  868:     */   {
/*  869:1409 */     return new Level();
/*  870:     */   }
/*  871:     */   
/*  872:     */   public FieldColumnPair createFieldColumnPair()
/*  873:     */   {
/*  874:1417 */     return new FieldColumnPair();
/*  875:     */   }
/*  876:     */   
/*  877:     */   public ModelStats createModelStats()
/*  878:     */   {
/*  879:1425 */     return new ModelStats();
/*  880:     */   }
/*  881:     */   
/*  882:     */   public Euclidean createEuclidean()
/*  883:     */   {
/*  884:1433 */     return new Euclidean();
/*  885:     */   }
/*  886:     */   
/*  887:     */   public SequenceReference createSequenceReference()
/*  888:     */   {
/*  889:1441 */     return new SequenceReference();
/*  890:     */   }
/*  891:     */   
/*  892:     */   public ContStats createContStats()
/*  893:     */   {
/*  894:1449 */     return new ContStats();
/*  895:     */   }
/*  896:     */   
/*  897:     */   public Quantile createQuantile()
/*  898:     */   {
/*  899:1457 */     return new Quantile();
/*  900:     */   }
/*  901:     */   
/*  902:     */   public Itemset createItemset()
/*  903:     */   {
/*  904:1465 */     return new Itemset();
/*  905:     */   }
/*  906:     */   
/*  907:     */   public PCovCell createPCovCell()
/*  908:     */   {
/*  909:1473 */     return new PCovCell();
/*  910:     */   }
/*  911:     */   
/*  912:     */   public Tanimoto createTanimoto()
/*  913:     */   {
/*  914:1481 */     return new Tanimoto();
/*  915:     */   }
/*  916:     */   
/*  917:     */   public TimeAnchor createTimeAnchor()
/*  918:     */   {
/*  919:1489 */     return new TimeAnchor();
/*  920:     */   }
/*  921:     */   
/*  922:     */   public OptimumLiftGraph createOptimumLiftGraph()
/*  923:     */   {
/*  924:1497 */     return new OptimumLiftGraph();
/*  925:     */   }
/*  926:     */   
/*  927:     */   public DerivedField createDerivedField()
/*  928:     */   {
/*  929:1505 */     return new DerivedField();
/*  930:     */   }
/*  931:     */   
/*  932:     */   public REALSparseArray createREALSparseArray()
/*  933:     */   {
/*  934:1513 */     return new REALSparseArray();
/*  935:     */   }
/*  936:     */   
/*  937:     */   public InstanceField createInstanceField()
/*  938:     */   {
/*  939:1521 */     return new InstanceField();
/*  940:     */   }
/*  941:     */   
/*  942:     */   public MissingValueWeights createMissingValueWeights()
/*  943:     */   {
/*  944:1529 */     return new MissingValueWeights();
/*  945:     */   }
/*  946:     */   
/*  947:     */   public BaselineStratum createBaselineStratum()
/*  948:     */   {
/*  949:1537 */     return new BaselineStratum();
/*  950:     */   }
/*  951:     */   
/*  952:     */   public ROC createROC()
/*  953:     */   {
/*  954:1545 */     return new ROC();
/*  955:     */   }
/*  956:     */   
/*  957:     */   public Delimiter createDelimiter()
/*  958:     */   {
/*  959:1553 */     return new Delimiter();
/*  960:     */   }
/*  961:     */   
/*  962:     */   public DiscrStats createDiscrStats()
/*  963:     */   {
/*  964:1561 */     return new DiscrStats();
/*  965:     */   }
/*  966:     */   
/*  967:     */   public False createFalse()
/*  968:     */   {
/*  969:1569 */     return new False();
/*  970:     */   }
/*  971:     */   
/*  972:     */   public CorrelationValues createCorrelationValues()
/*  973:     */   {
/*  974:1577 */     return new CorrelationValues();
/*  975:     */   }
/*  976:     */   
/*  977:     */   public ParameterList createParameterList()
/*  978:     */   {
/*  979:1585 */     return new ParameterList();
/*  980:     */   }
/*  981:     */   
/*  982:     */   public Category createCategory()
/*  983:     */   {
/*  984:1593 */     return new Category();
/*  985:     */   }
/*  986:     */   
/*  987:     */   public Item createItem()
/*  988:     */   {
/*  989:1601 */     return new Item();
/*  990:     */   }
/*  991:     */   
/*  992:     */   public AssociationModel createAssociationModel()
/*  993:     */   {
/*  994:1609 */     return new AssociationModel();
/*  995:     */   }
/*  996:     */   
/*  997:     */   public BayesInputs createBayesInputs()
/*  998:     */   {
/*  999:1617 */     return new BayesInputs();
/* 1000:     */   }
/* 1001:     */   
/* 1002:     */   public NeuralLayer createNeuralLayer()
/* 1003:     */   {
/* 1004:1625 */     return new NeuralLayer();
/* 1005:     */   }
/* 1006:     */   
/* 1007:     */   public INTSparseArray createINTSparseArray()
/* 1008:     */   {
/* 1009:1633 */     return new INTSparseArray();
/* 1010:     */   }
/* 1011:     */   
/* 1012:     */   public ResultField createResultField()
/* 1013:     */   {
/* 1014:1641 */     return new ResultField();
/* 1015:     */   }
/* 1016:     */   
/* 1017:     */   public XCoordinates createXCoordinates()
/* 1018:     */   {
/* 1019:1649 */     return new XCoordinates();
/* 1020:     */   }
/* 1021:     */   
/* 1022:     */   public Neuron createNeuron()
/* 1023:     */   {
/* 1024:1657 */     return new Neuron();
/* 1025:     */   }
/* 1026:     */   
/* 1027:     */   public InstanceFields createInstanceFields()
/* 1028:     */   {
/* 1029:1665 */     return new InstanceFields();
/* 1030:     */   }
/* 1031:     */   
/* 1032:     */   public PredictiveModelQuality createPredictiveModelQuality()
/* 1033:     */   {
/* 1034:1673 */     return new PredictiveModelQuality();
/* 1035:     */   }
/* 1036:     */   
/* 1037:     */   public TextCorpus createTextCorpus()
/* 1038:     */   {
/* 1039:1681 */     return new TextCorpus();
/* 1040:     */   }
/* 1041:     */   
/* 1042:     */   public TimeSeriesModel createTimeSeriesModel()
/* 1043:     */   {
/* 1044:1689 */     return new TimeSeriesModel();
/* 1045:     */   }
/* 1046:     */   
/* 1047:     */   public SupportVectorMachineModel createSupportVectorMachineModel()
/* 1048:     */   {
/* 1049:1697 */     return new SupportVectorMachineModel();
/* 1050:     */   }
/* 1051:     */   
/* 1052:     */   public Constant createConstant()
/* 1053:     */   {
/* 1054:1705 */     return new Constant();
/* 1055:     */   }
/* 1056:     */   
/* 1057:     */   public UnivariateStats createUnivariateStats()
/* 1058:     */   {
/* 1059:1713 */     return new UnivariateStats();
/* 1060:     */   }
/* 1061:     */   
/* 1062:     */   public DiscretizeBin createDiscretizeBin()
/* 1063:     */   {
/* 1064:1721 */     return new DiscretizeBin();
/* 1065:     */   }
/* 1066:     */   
/* 1067:     */   public NumericInfo createNumericInfo()
/* 1068:     */   {
/* 1069:1729 */     return new NumericInfo();
/* 1070:     */   }
/* 1071:     */   
/* 1072:     */   public MapValues createMapValues()
/* 1073:     */   {
/* 1074:1737 */     return new MapValues();
/* 1075:     */   }
/* 1076:     */   
/* 1077:     */   public RegressionTable createRegressionTable()
/* 1078:     */   {
/* 1079:1745 */     return new RegressionTable();
/* 1080:     */   }
/* 1081:     */   
/* 1082:     */   public TimeValue createTimeValue()
/* 1083:     */   {
/* 1084:1753 */     return new TimeValue();
/* 1085:     */   }
/* 1086:     */   
/* 1087:     */   public AnovaRow createAnovaRow()
/* 1088:     */   {
/* 1089:1761 */     return new AnovaRow();
/* 1090:     */   }
/* 1091:     */   
/* 1092:     */   public DefineFunction createDefineFunction()
/* 1093:     */   {
/* 1094:1769 */     return new DefineFunction();
/* 1095:     */   }
/* 1096:     */   
/* 1097:     */   public Apply createApply()
/* 1098:     */   {
/* 1099:1777 */     return new Apply();
/* 1100:     */   }
/* 1101:     */   
/* 1102:     */   public CityBlock createCityBlock()
/* 1103:     */   {
/* 1104:1785 */     return new CityBlock();
/* 1105:     */   }
/* 1106:     */   
/* 1107:     */   public TargetValue createTargetValue()
/* 1108:     */   {
/* 1109:1793 */     return new TargetValue();
/* 1110:     */   }
/* 1111:     */   
/* 1112:     */   public VectorDictionary createVectorDictionary()
/* 1113:     */   {
/* 1114:1801 */     return new VectorDictionary();
/* 1115:     */   }
/* 1116:     */   
/* 1117:     */   public Alternate createAlternate()
/* 1118:     */   {
/* 1119:1809 */     return new Alternate();
/* 1120:     */   }
/* 1121:     */   
/* 1122:     */   public BoundaryValues createBoundaryValues()
/* 1123:     */   {
/* 1124:1817 */     return new BoundaryValues();
/* 1125:     */   }
/* 1126:     */   
/* 1127:     */   public CategoricalPredictor createCategoricalPredictor()
/* 1128:     */   {
/* 1129:1825 */     return new CategoricalPredictor();
/* 1130:     */   }
/* 1131:     */   
/* 1132:     */   public ParamMatrix createParamMatrix()
/* 1133:     */   {
/* 1134:1833 */     return new ParamMatrix();
/* 1135:     */   }
/* 1136:     */   
/* 1137:     */   @XmlElementDecl(namespace="http://www.dmg.org/PMML-4_1", name="SpectralAnalysis")
/* 1138:     */   public JAXBElement<Object> createSpectralAnalysis(Object value)
/* 1139:     */   {
/* 1140:1842 */     return new JAXBElement(_SpectralAnalysis_QNAME, Object.class, null, value);
/* 1141:     */   }
/* 1142:     */   
/* 1143:     */   @XmlElementDecl(namespace="http://www.dmg.org/PMML-4_1", name="REAL-Entries")
/* 1144:     */   public JAXBElement<List<Double>> createREALEntries(List<Double> value)
/* 1145:     */   {
/* 1146:1851 */     return new JAXBElement(_REALEntries_QNAME, List.class, null, value);
/* 1147:     */   }
/* 1148:     */   
/* 1149:     */   @XmlElementDecl(namespace="http://www.dmg.org/PMML-4_1", name="INT-Entries")
/* 1150:     */   public JAXBElement<List<Integer>> createINTEntries(List<Integer> value)
/* 1151:     */   {
/* 1152:1860 */     return new JAXBElement(_INTEntries_QNAME, List.class, null, value);
/* 1153:     */   }
/* 1154:     */   
/* 1155:     */   @XmlElementDecl(namespace="http://www.dmg.org/PMML-4_1", name="CountTable")
/* 1156:     */   public JAXBElement<COUNTTABLETYPE> createCountTable(COUNTTABLETYPE value)
/* 1157:     */   {
/* 1158:1869 */     return new JAXBElement(_CountTable_QNAME, COUNTTABLETYPE.class, null, value);
/* 1159:     */   }
/* 1160:     */   
/* 1161:     */   @XmlElementDecl(namespace="http://www.dmg.org/PMML-4_1", name="ARIMA")
/* 1162:     */   public JAXBElement<Object> createARIMA(Object value)
/* 1163:     */   {
/* 1164:1878 */     return new JAXBElement(_ARIMA_QNAME, Object.class, null, value);
/* 1165:     */   }
/* 1166:     */   
/* 1167:     */   @XmlElementDecl(namespace="http://www.dmg.org/PMML-4_1", name="Indices")
/* 1168:     */   public JAXBElement<List<Integer>> createIndices(List<Integer> value)
/* 1169:     */   {
/* 1170:1887 */     return new JAXBElement(_Indices_QNAME, List.class, null, value);
/* 1171:     */   }
/* 1172:     */   
/* 1173:     */   @XmlElementDecl(namespace="http://www.dmg.org/PMML-4_1", name="SeasonalTrendDecomposition")
/* 1174:     */   public JAXBElement<Object> createSeasonalTrendDecomposition(Object value)
/* 1175:     */   {
/* 1176:1896 */     return new JAXBElement(_SeasonalTrendDecomposition_QNAME, Object.class, null, value);
/* 1177:     */   }
/* 1178:     */   
/* 1179:     */   @XmlElementDecl(namespace="http://www.dmg.org/PMML-4_1", name="NormalizedCountTable")
/* 1180:     */   public JAXBElement<COUNTTABLETYPE> createNormalizedCountTable(COUNTTABLETYPE value)
/* 1181:     */   {
/* 1182:1905 */     return new JAXBElement(_NormalizedCountTable_QNAME, COUNTTABLETYPE.class, null, value);
/* 1183:     */   }
/* 1184:     */   
/* 1185:     */   @XmlElementDecl(namespace="http://www.dmg.org/PMML-4_1", name="Array")
/* 1186:     */   public JAXBElement<ArrayType> createArray(ArrayType value)
/* 1187:     */   {
/* 1188:1914 */     return new JAXBElement(_Array_QNAME, ArrayType.class, null, value);
/* 1189:     */   }
/* 1190:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.pmml.jaxbbindings.ObjectFactory
 * JD-Core Version:    0.7.0.1
 */