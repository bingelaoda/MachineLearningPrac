/*    1:     */ package weka.core.neighboursearch;
/*    2:     */ 
/*    3:     */ import java.io.PrintStream;
/*    4:     */ import java.util.Collections;
/*    5:     */ import java.util.Enumeration;
/*    6:     */ import java.util.Vector;
/*    7:     */ import weka.core.DistanceFunction;
/*    8:     */ import weka.core.EuclideanDistance;
/*    9:     */ import weka.core.Instance;
/*   10:     */ import weka.core.Instances;
/*   11:     */ import weka.core.Option;
/*   12:     */ import weka.core.RevisionUtils;
/*   13:     */ import weka.core.TechnicalInformation;
/*   14:     */ import weka.core.TechnicalInformation.Field;
/*   15:     */ import weka.core.TechnicalInformation.Type;
/*   16:     */ import weka.core.TechnicalInformationHandler;
/*   17:     */ import weka.core.Utils;
/*   18:     */ import weka.core.neighboursearch.kdtrees.KDTreeNode;
/*   19:     */ import weka.core.neighboursearch.kdtrees.KDTreeNodeSplitter;
/*   20:     */ import weka.core.neighboursearch.kdtrees.SlidingMidPointOfWidestSide;
/*   21:     */ 
/*   22:     */ public class KDTree
/*   23:     */   extends NearestNeighbourSearch
/*   24:     */   implements TechnicalInformationHandler
/*   25:     */ {
/*   26:     */   private static final long serialVersionUID = 1505717283763272533L;
/*   27:     */   protected double[] m_DistanceList;
/*   28:     */   protected int[] m_InstList;
/*   29:     */   protected KDTreeNode m_Root;
/*   30: 135 */   protected KDTreeNodeSplitter m_Splitter = new SlidingMidPointOfWidestSide();
/*   31:     */   protected int m_NumNodes;
/*   32:     */   protected int m_NumLeaves;
/*   33:     */   protected int m_MaxDepth;
/*   34: 141 */   protected TreePerformanceStats m_TreeStats = null;
/*   35:     */   public static final int MIN = 0;
/*   36:     */   public static final int MAX = 1;
/*   37:     */   public static final int WIDTH = 2;
/*   38:     */   boolean m_NormalizeNodeWidth;
/*   39:     */   protected EuclideanDistance m_EuclideanDistance;
/*   40:     */   protected double m_MinBoxRelWidth;
/*   41:     */   protected int m_MaxInstInLeaf;
/*   42:     */   
/*   43:     */   public TechnicalInformation getTechnicalInformation()
/*   44:     */   {
/*   45: 164 */     TechnicalInformation result = new TechnicalInformation(TechnicalInformation.Type.ARTICLE);
/*   46: 165 */     result.setValue(TechnicalInformation.Field.AUTHOR, "Jerome H. Friedman and Jon Luis Bentley and Raphael Ari Finkel");
/*   47: 166 */     result.setValue(TechnicalInformation.Field.YEAR, "1977");
/*   48: 167 */     result.setValue(TechnicalInformation.Field.TITLE, "An Algorithm for Finding Best Matches in Logarithmic Expected Time");
/*   49: 168 */     result.setValue(TechnicalInformation.Field.JOURNAL, "ACM Transactions on Mathematics Software");
/*   50: 169 */     result.setValue(TechnicalInformation.Field.PAGES, "209-226");
/*   51: 170 */     result.setValue(TechnicalInformation.Field.MONTH, "September");
/*   52: 171 */     result.setValue(TechnicalInformation.Field.VOLUME, "3");
/*   53: 172 */     result.setValue(TechnicalInformation.Field.NUMBER, "3");
/*   54:     */     
/*   55: 174 */     TechnicalInformation additional = result.add(TechnicalInformation.Type.TECHREPORT);
/*   56: 175 */     additional.setValue(TechnicalInformation.Field.AUTHOR, "Andrew Moore");
/*   57: 176 */     additional.setValue(TechnicalInformation.Field.YEAR, "1991");
/*   58: 177 */     additional.setValue(TechnicalInformation.Field.TITLE, "A tutorial on kd-trees");
/*   59: 178 */     additional.setValue(TechnicalInformation.Field.HOWPUBLISHED, "Extract from PhD Thesis");
/*   60: 179 */     additional.setValue(TechnicalInformation.Field.BOOKTITLE, "University of Cambridge Computer Laboratory Technical Report No. 209");
/*   61: 180 */     additional.setValue(TechnicalInformation.Field.HTTP, "Available from http://www.autonlab.org/autonweb/14665.html");
/*   62:     */     
/*   63: 182 */     return result;
/*   64:     */   }
/*   65:     */   
/*   66:     */   public KDTree()
/*   67:     */   {
/*   68:1011 */     this.m_NormalizeNodeWidth = true;
/*   69:1016 */     if ((this.m_DistanceFunction instanceof EuclideanDistance)) {
/*   70:1017 */       this.m_EuclideanDistance = ((EuclideanDistance)this.m_DistanceFunction);
/*   71:     */     } else {
/*   72:1019 */       this.m_DistanceFunction = (this.m_EuclideanDistance = new EuclideanDistance());
/*   73:     */     }
/*   74:1023 */     this.m_MinBoxRelWidth = 0.01D;
/*   75:     */     
/*   76:     */ 
/*   77:1026 */     this.m_MaxInstInLeaf = 40;
/*   78: 190 */     if (getMeasurePerformance()) {
/*   79: 191 */       this.m_Stats = (this.m_TreeStats = new TreePerformanceStats());
/*   80:     */     }
/*   81:     */   }
/*   82:     */   
/*   83:     */   public KDTree(Instances insts)
/*   84:     */   {
/*   85: 201 */     super(insts);
/*   86:     */     
/*   87:     */ 
/*   88:     */ 
/*   89:     */ 
/*   90:     */ 
/*   91:     */ 
/*   92:     */ 
/*   93:     */ 
/*   94:     */ 
/*   95:     */ 
/*   96:     */ 
/*   97:     */ 
/*   98:     */ 
/*   99:     */ 
/*  100:     */ 
/*  101:     */ 
/*  102:     */ 
/*  103:     */ 
/*  104:     */ 
/*  105:     */ 
/*  106:     */ 
/*  107:     */ 
/*  108:     */ 
/*  109:     */ 
/*  110:     */ 
/*  111:     */ 
/*  112:     */ 
/*  113:     */ 
/*  114:     */ 
/*  115:     */ 
/*  116:     */ 
/*  117:     */ 
/*  118:     */ 
/*  119:     */ 
/*  120:     */ 
/*  121:     */ 
/*  122:     */ 
/*  123:     */ 
/*  124:     */ 
/*  125:     */ 
/*  126:     */ 
/*  127:     */ 
/*  128:     */ 
/*  129:     */ 
/*  130:     */ 
/*  131:     */ 
/*  132:     */ 
/*  133:     */ 
/*  134:     */ 
/*  135:     */ 
/*  136:     */ 
/*  137:     */ 
/*  138:     */ 
/*  139:     */ 
/*  140:     */ 
/*  141:     */ 
/*  142:     */ 
/*  143:     */ 
/*  144:     */ 
/*  145:     */ 
/*  146:     */ 
/*  147:     */ 
/*  148:     */ 
/*  149:     */ 
/*  150:     */ 
/*  151:     */ 
/*  152:     */ 
/*  153:     */ 
/*  154:     */ 
/*  155:     */ 
/*  156:     */ 
/*  157:     */ 
/*  158:     */ 
/*  159:     */ 
/*  160:     */ 
/*  161:     */ 
/*  162:     */ 
/*  163:     */ 
/*  164:     */ 
/*  165:     */ 
/*  166:     */ 
/*  167:     */ 
/*  168:     */ 
/*  169:     */ 
/*  170:     */ 
/*  171:     */ 
/*  172:     */ 
/*  173:     */ 
/*  174:     */ 
/*  175:     */ 
/*  176:     */ 
/*  177:     */ 
/*  178:     */ 
/*  179:     */ 
/*  180:     */ 
/*  181:     */ 
/*  182:     */ 
/*  183:     */ 
/*  184:     */ 
/*  185:     */ 
/*  186:     */ 
/*  187:     */ 
/*  188:     */ 
/*  189:     */ 
/*  190:     */ 
/*  191:     */ 
/*  192:     */ 
/*  193:     */ 
/*  194:     */ 
/*  195:     */ 
/*  196:     */ 
/*  197:     */ 
/*  198:     */ 
/*  199:     */ 
/*  200:     */ 
/*  201:     */ 
/*  202:     */ 
/*  203:     */ 
/*  204:     */ 
/*  205:     */ 
/*  206:     */ 
/*  207:     */ 
/*  208:     */ 
/*  209:     */ 
/*  210:     */ 
/*  211:     */ 
/*  212:     */ 
/*  213:     */ 
/*  214:     */ 
/*  215:     */ 
/*  216:     */ 
/*  217:     */ 
/*  218:     */ 
/*  219:     */ 
/*  220:     */ 
/*  221:     */ 
/*  222:     */ 
/*  223:     */ 
/*  224:     */ 
/*  225:     */ 
/*  226:     */ 
/*  227:     */ 
/*  228:     */ 
/*  229:     */ 
/*  230:     */ 
/*  231:     */ 
/*  232:     */ 
/*  233:     */ 
/*  234:     */ 
/*  235:     */ 
/*  236:     */ 
/*  237:     */ 
/*  238:     */ 
/*  239:     */ 
/*  240:     */ 
/*  241:     */ 
/*  242:     */ 
/*  243:     */ 
/*  244:     */ 
/*  245:     */ 
/*  246:     */ 
/*  247:     */ 
/*  248:     */ 
/*  249:     */ 
/*  250:     */ 
/*  251:     */ 
/*  252:     */ 
/*  253:     */ 
/*  254:     */ 
/*  255:     */ 
/*  256:     */ 
/*  257:     */ 
/*  258:     */ 
/*  259:     */ 
/*  260:     */ 
/*  261:     */ 
/*  262:     */ 
/*  263:     */ 
/*  264:     */ 
/*  265:     */ 
/*  266:     */ 
/*  267:     */ 
/*  268:     */ 
/*  269:     */ 
/*  270:     */ 
/*  271:     */ 
/*  272:     */ 
/*  273:     */ 
/*  274:     */ 
/*  275:     */ 
/*  276:     */ 
/*  277:     */ 
/*  278:     */ 
/*  279:     */ 
/*  280:     */ 
/*  281:     */ 
/*  282:     */ 
/*  283:     */ 
/*  284:     */ 
/*  285:     */ 
/*  286:     */ 
/*  287:     */ 
/*  288:     */ 
/*  289:     */ 
/*  290:     */ 
/*  291:     */ 
/*  292:     */ 
/*  293:     */ 
/*  294:     */ 
/*  295:     */ 
/*  296:     */ 
/*  297:     */ 
/*  298:     */ 
/*  299:     */ 
/*  300:     */ 
/*  301:     */ 
/*  302:     */ 
/*  303:     */ 
/*  304:     */ 
/*  305:     */ 
/*  306:     */ 
/*  307:     */ 
/*  308:     */ 
/*  309:     */ 
/*  310:     */ 
/*  311:     */ 
/*  312:     */ 
/*  313:     */ 
/*  314:     */ 
/*  315:     */ 
/*  316:     */ 
/*  317:     */ 
/*  318:     */ 
/*  319:     */ 
/*  320:     */ 
/*  321:     */ 
/*  322:     */ 
/*  323:     */ 
/*  324:     */ 
/*  325:     */ 
/*  326:     */ 
/*  327:     */ 
/*  328:     */ 
/*  329:     */ 
/*  330:     */ 
/*  331:     */ 
/*  332:     */ 
/*  333:     */ 
/*  334:     */ 
/*  335:     */ 
/*  336:     */ 
/*  337:     */ 
/*  338:     */ 
/*  339:     */ 
/*  340:     */ 
/*  341:     */ 
/*  342:     */ 
/*  343:     */ 
/*  344:     */ 
/*  345:     */ 
/*  346:     */ 
/*  347:     */ 
/*  348:     */ 
/*  349:     */ 
/*  350:     */ 
/*  351:     */ 
/*  352:     */ 
/*  353:     */ 
/*  354:     */ 
/*  355:     */ 
/*  356:     */ 
/*  357:     */ 
/*  358:     */ 
/*  359:     */ 
/*  360:     */ 
/*  361:     */ 
/*  362:     */ 
/*  363:     */ 
/*  364:     */ 
/*  365:     */ 
/*  366:     */ 
/*  367:     */ 
/*  368:     */ 
/*  369:     */ 
/*  370:     */ 
/*  371:     */ 
/*  372:     */ 
/*  373:     */ 
/*  374:     */ 
/*  375:     */ 
/*  376:     */ 
/*  377:     */ 
/*  378:     */ 
/*  379:     */ 
/*  380:     */ 
/*  381:     */ 
/*  382:     */ 
/*  383:     */ 
/*  384:     */ 
/*  385:     */ 
/*  386:     */ 
/*  387:     */ 
/*  388:     */ 
/*  389:     */ 
/*  390:     */ 
/*  391:     */ 
/*  392:     */ 
/*  393:     */ 
/*  394:     */ 
/*  395:     */ 
/*  396:     */ 
/*  397:     */ 
/*  398:     */ 
/*  399:     */ 
/*  400:     */ 
/*  401:     */ 
/*  402:     */ 
/*  403:     */ 
/*  404:     */ 
/*  405:     */ 
/*  406:     */ 
/*  407:     */ 
/*  408:     */ 
/*  409:     */ 
/*  410:     */ 
/*  411:     */ 
/*  412:     */ 
/*  413:     */ 
/*  414:     */ 
/*  415:     */ 
/*  416:     */ 
/*  417:     */ 
/*  418:     */ 
/*  419:     */ 
/*  420:     */ 
/*  421:     */ 
/*  422:     */ 
/*  423:     */ 
/*  424:     */ 
/*  425:     */ 
/*  426:     */ 
/*  427:     */ 
/*  428:     */ 
/*  429:     */ 
/*  430:     */ 
/*  431:     */ 
/*  432:     */ 
/*  433:     */ 
/*  434:     */ 
/*  435:     */ 
/*  436:     */ 
/*  437:     */ 
/*  438:     */ 
/*  439:     */ 
/*  440:     */ 
/*  441:     */ 
/*  442:     */ 
/*  443:     */ 
/*  444:     */ 
/*  445:     */ 
/*  446:     */ 
/*  447:     */ 
/*  448:     */ 
/*  449:     */ 
/*  450:     */ 
/*  451:     */ 
/*  452:     */ 
/*  453:     */ 
/*  454:     */ 
/*  455:     */ 
/*  456:     */ 
/*  457:     */ 
/*  458:     */ 
/*  459:     */ 
/*  460:     */ 
/*  461:     */ 
/*  462:     */ 
/*  463:     */ 
/*  464:     */ 
/*  465:     */ 
/*  466:     */ 
/*  467:     */ 
/*  468:     */ 
/*  469:     */ 
/*  470:     */ 
/*  471:     */ 
/*  472:     */ 
/*  473:     */ 
/*  474:     */ 
/*  475:     */ 
/*  476:     */ 
/*  477:     */ 
/*  478:     */ 
/*  479:     */ 
/*  480:     */ 
/*  481:     */ 
/*  482:     */ 
/*  483:     */ 
/*  484:     */ 
/*  485:     */ 
/*  486:     */ 
/*  487:     */ 
/*  488:     */ 
/*  489:     */ 
/*  490:     */ 
/*  491:     */ 
/*  492:     */ 
/*  493:     */ 
/*  494:     */ 
/*  495:     */ 
/*  496:     */ 
/*  497:     */ 
/*  498:     */ 
/*  499:     */ 
/*  500:     */ 
/*  501:     */ 
/*  502:     */ 
/*  503:     */ 
/*  504:     */ 
/*  505:     */ 
/*  506:     */ 
/*  507:     */ 
/*  508:     */ 
/*  509:     */ 
/*  510:     */ 
/*  511:     */ 
/*  512:     */ 
/*  513:     */ 
/*  514:     */ 
/*  515:     */ 
/*  516:     */ 
/*  517:     */ 
/*  518:     */ 
/*  519:     */ 
/*  520:     */ 
/*  521:     */ 
/*  522:     */ 
/*  523:     */ 
/*  524:     */ 
/*  525:     */ 
/*  526:     */ 
/*  527:     */ 
/*  528:     */ 
/*  529:     */ 
/*  530:     */ 
/*  531:     */ 
/*  532:     */ 
/*  533:     */ 
/*  534:     */ 
/*  535:     */ 
/*  536:     */ 
/*  537:     */ 
/*  538:     */ 
/*  539:     */ 
/*  540:     */ 
/*  541:     */ 
/*  542:     */ 
/*  543:     */ 
/*  544:     */ 
/*  545:     */ 
/*  546:     */ 
/*  547:     */ 
/*  548:     */ 
/*  549:     */ 
/*  550:     */ 
/*  551:     */ 
/*  552:     */ 
/*  553:     */ 
/*  554:     */ 
/*  555:     */ 
/*  556:     */ 
/*  557:     */ 
/*  558:     */ 
/*  559:     */ 
/*  560:     */ 
/*  561:     */ 
/*  562:     */ 
/*  563:     */ 
/*  564:     */ 
/*  565:     */ 
/*  566:     */ 
/*  567:     */ 
/*  568:     */ 
/*  569:     */ 
/*  570:     */ 
/*  571:     */ 
/*  572:     */ 
/*  573:     */ 
/*  574:     */ 
/*  575:     */ 
/*  576:     */ 
/*  577:     */ 
/*  578:     */ 
/*  579:     */ 
/*  580:     */ 
/*  581:     */ 
/*  582:     */ 
/*  583:     */ 
/*  584:     */ 
/*  585:     */ 
/*  586:     */ 
/*  587:     */ 
/*  588:     */ 
/*  589:     */ 
/*  590:     */ 
/*  591:     */ 
/*  592:     */ 
/*  593:     */ 
/*  594:     */ 
/*  595:     */ 
/*  596:     */ 
/*  597:     */ 
/*  598:     */ 
/*  599:     */ 
/*  600:     */ 
/*  601:     */ 
/*  602:     */ 
/*  603:     */ 
/*  604:     */ 
/*  605:     */ 
/*  606:     */ 
/*  607:     */ 
/*  608:     */ 
/*  609:     */ 
/*  610:     */ 
/*  611:     */ 
/*  612:     */ 
/*  613:     */ 
/*  614:     */ 
/*  615:     */ 
/*  616:     */ 
/*  617:     */ 
/*  618:     */ 
/*  619:     */ 
/*  620:     */ 
/*  621:     */ 
/*  622:     */ 
/*  623:     */ 
/*  624:     */ 
/*  625:     */ 
/*  626:     */ 
/*  627:     */ 
/*  628:     */ 
/*  629:     */ 
/*  630:     */ 
/*  631:     */ 
/*  632:     */ 
/*  633:     */ 
/*  634:     */ 
/*  635:     */ 
/*  636:     */ 
/*  637:     */ 
/*  638:     */ 
/*  639:     */ 
/*  640:     */ 
/*  641:     */ 
/*  642:     */ 
/*  643:     */ 
/*  644:     */ 
/*  645:     */ 
/*  646:     */ 
/*  647:     */ 
/*  648:     */ 
/*  649:     */ 
/*  650:     */ 
/*  651:     */ 
/*  652:     */ 
/*  653:     */ 
/*  654:     */ 
/*  655:     */ 
/*  656:     */ 
/*  657:     */ 
/*  658:     */ 
/*  659:     */ 
/*  660:     */ 
/*  661:     */ 
/*  662:     */ 
/*  663:     */ 
/*  664:     */ 
/*  665:     */ 
/*  666:     */ 
/*  667:     */ 
/*  668:     */ 
/*  669:     */ 
/*  670:     */ 
/*  671:     */ 
/*  672:     */ 
/*  673:     */ 
/*  674:     */ 
/*  675:     */ 
/*  676:     */ 
/*  677:     */ 
/*  678:     */ 
/*  679:     */ 
/*  680:     */ 
/*  681:     */ 
/*  682:     */ 
/*  683:     */ 
/*  684:     */ 
/*  685:     */ 
/*  686:     */ 
/*  687:     */ 
/*  688:     */ 
/*  689:     */ 
/*  690:     */ 
/*  691:     */ 
/*  692:     */ 
/*  693:     */ 
/*  694:     */ 
/*  695:     */ 
/*  696:     */ 
/*  697:     */ 
/*  698:     */ 
/*  699:     */ 
/*  700:     */ 
/*  701:     */ 
/*  702:     */ 
/*  703:     */ 
/*  704:     */ 
/*  705:     */ 
/*  706:     */ 
/*  707:     */ 
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
/*  718:     */ 
/*  719:     */ 
/*  720:     */ 
/*  721:     */ 
/*  722:     */ 
/*  723:     */ 
/*  724:     */ 
/*  725:     */ 
/*  726:     */ 
/*  727:     */ 
/*  728:     */ 
/*  729:     */ 
/*  730:     */ 
/*  731:     */ 
/*  732:     */ 
/*  733:     */ 
/*  734:     */ 
/*  735:     */ 
/*  736:     */ 
/*  737:     */ 
/*  738:     */ 
/*  739:     */ 
/*  740:     */ 
/*  741:     */ 
/*  742:     */ 
/*  743:     */ 
/*  744:     */ 
/*  745:     */ 
/*  746:     */ 
/*  747:     */ 
/*  748:     */ 
/*  749:     */ 
/*  750:     */ 
/*  751:     */ 
/*  752:     */ 
/*  753:     */ 
/*  754:     */ 
/*  755:     */ 
/*  756:     */ 
/*  757:     */ 
/*  758:     */ 
/*  759:     */ 
/*  760:     */ 
/*  761:     */ 
/*  762:     */ 
/*  763:     */ 
/*  764:     */ 
/*  765:     */ 
/*  766:     */ 
/*  767:     */ 
/*  768:     */ 
/*  769:     */ 
/*  770:     */ 
/*  771:     */ 
/*  772:     */ 
/*  773:     */ 
/*  774:     */ 
/*  775:     */ 
/*  776:     */ 
/*  777:     */ 
/*  778:     */ 
/*  779:     */ 
/*  780:     */ 
/*  781:     */ 
/*  782:     */ 
/*  783:     */ 
/*  784:     */ 
/*  785:     */ 
/*  786:     */ 
/*  787:     */ 
/*  788:     */ 
/*  789:     */ 
/*  790:     */ 
/*  791:     */ 
/*  792:     */ 
/*  793:     */ 
/*  794:     */ 
/*  795:     */ 
/*  796:     */ 
/*  797:     */ 
/*  798:     */ 
/*  799:     */ 
/*  800:     */ 
/*  801:     */ 
/*  802:     */ 
/*  803:     */ 
/*  804:     */ 
/*  805:     */ 
/*  806:     */ 
/*  807:     */ 
/*  808:     */ 
/*  809:     */ 
/*  810:     */ 
/*  811:     */ 
/*  812:     */ 
/*  813:     */ 
/*  814:     */ 
/*  815:     */ 
/*  816:     */ 
/*  817:     */ 
/*  818:     */ 
/*  819:     */ 
/*  820:     */ 
/*  821:     */ 
/*  822:     */ 
/*  823:     */ 
/*  824:     */ 
/*  825:     */ 
/*  826:     */ 
/*  827:     */ 
/*  828:     */ 
/*  829:     */ 
/*  830:     */ 
/*  831:     */ 
/*  832:     */ 
/*  833:     */ 
/*  834:     */ 
/*  835:     */ 
/*  836:     */ 
/*  837:     */ 
/*  838:     */ 
/*  839:     */ 
/*  840:     */ 
/*  841:     */ 
/*  842:     */ 
/*  843:     */ 
/*  844:     */ 
/*  845:     */ 
/*  846:     */ 
/*  847:     */ 
/*  848:     */ 
/*  849:     */ 
/*  850:     */ 
/*  851:     */ 
/*  852:     */ 
/*  853:     */ 
/*  854:     */ 
/*  855:     */ 
/*  856:     */ 
/*  857:     */ 
/*  858:     */ 
/*  859:     */ 
/*  860:     */ 
/*  861:     */ 
/*  862:     */ 
/*  863:     */ 
/*  864:     */ 
/*  865:     */ 
/*  866:     */ 
/*  867:     */ 
/*  868:     */ 
/*  869:     */ 
/*  870:     */ 
/*  871:     */ 
/*  872:     */ 
/*  873:     */ 
/*  874:     */ 
/*  875:     */ 
/*  876:     */ 
/*  877:     */ 
/*  878:     */ 
/*  879:     */ 
/*  880:     */ 
/*  881:     */ 
/*  882:     */ 
/*  883:     */ 
/*  884:     */ 
/*  885:     */ 
/*  886:     */ 
/*  887:     */ 
/*  888:     */ 
/*  889:     */ 
/*  890:     */ 
/*  891:     */ 
/*  892:     */ 
/*  893:     */ 
/*  894:     */ 
/*  895:1011 */     this.m_NormalizeNodeWidth = true;
/*  896:1016 */     if ((this.m_DistanceFunction instanceof EuclideanDistance)) {
/*  897:1017 */       this.m_EuclideanDistance = ((EuclideanDistance)this.m_DistanceFunction);
/*  898:     */     } else {
/*  899:1019 */       this.m_DistanceFunction = (this.m_EuclideanDistance = new EuclideanDistance());
/*  900:     */     }
/*  901:1023 */     this.m_MinBoxRelWidth = 0.01D;
/*  902:     */     
/*  903:     */ 
/*  904:1026 */     this.m_MaxInstInLeaf = 40;
/*  905: 202 */     if (getMeasurePerformance()) {
/*  906: 203 */       this.m_Stats = (this.m_TreeStats = new TreePerformanceStats());
/*  907:     */     }
/*  908:     */   }
/*  909:     */   
/*  910:     */   protected void buildKDTree(Instances instances)
/*  911:     */     throws Exception
/*  912:     */   {
/*  913: 219 */     checkMissing(instances);
/*  914: 220 */     if (this.m_EuclideanDistance == null) {
/*  915: 221 */       this.m_DistanceFunction = (this.m_EuclideanDistance = new EuclideanDistance(instances));
/*  916:     */     } else {
/*  917: 224 */       this.m_EuclideanDistance.setInstances(instances);
/*  918:     */     }
/*  919: 226 */     this.m_Instances = instances;
/*  920: 227 */     int numInst = this.m_Instances.numInstances();
/*  921:     */     
/*  922:     */ 
/*  923: 230 */     this.m_InstList = new int[numInst];
/*  924: 232 */     for (int i = 0; i < numInst; i++) {
/*  925: 233 */       this.m_InstList[i] = i;
/*  926:     */     }
/*  927: 236 */     double[][] universe = this.m_EuclideanDistance.getRanges();
/*  928:     */     
/*  929:     */ 
/*  930: 239 */     this.m_Splitter.setInstances(this.m_Instances);
/*  931: 240 */     this.m_Splitter.setInstanceList(this.m_InstList);
/*  932: 241 */     this.m_Splitter.setEuclideanDistanceFunction(this.m_EuclideanDistance);
/*  933: 242 */     this.m_Splitter.setNodeWidthNormalization(this.m_NormalizeNodeWidth);
/*  934:     */     
/*  935:     */ 
/*  936: 245 */     this.m_NumNodes = (this.m_NumLeaves = 1);
/*  937: 246 */     this.m_MaxDepth = 0;
/*  938: 247 */     this.m_Root = new KDTreeNode(this.m_NumNodes, 0, this.m_Instances.numInstances() - 1, universe);
/*  939:     */     
/*  940:     */ 
/*  941: 250 */     splitNodes(this.m_Root, universe, this.m_MaxDepth + 1);
/*  942:     */   }
/*  943:     */   
/*  944:     */   protected void splitNodes(KDTreeNode node, double[][] universe, int depth)
/*  945:     */     throws Exception
/*  946:     */   {
/*  947: 269 */     double[][] nodeRanges = this.m_EuclideanDistance.initializeRanges(this.m_InstList, node.m_Start, node.m_End);
/*  948: 271 */     if ((node.numInstances() <= this.m_MaxInstInLeaf) || (getMaxRelativeNodeWidth(nodeRanges, universe) <= this.m_MinBoxRelWidth)) {
/*  949: 273 */       return;
/*  950:     */     }
/*  951: 276 */     this.m_NumLeaves -= 1;
/*  952: 278 */     if (depth > this.m_MaxDepth) {
/*  953: 279 */       this.m_MaxDepth = depth;
/*  954:     */     }
/*  955: 281 */     this.m_Splitter.splitNode(node, this.m_NumNodes, nodeRanges, universe);
/*  956: 282 */     this.m_NumNodes += 2;
/*  957: 283 */     this.m_NumLeaves += 2;
/*  958:     */     
/*  959: 285 */     splitNodes(node.m_Left, universe, depth + 1);
/*  960: 286 */     splitNodes(node.m_Right, universe, depth + 1);
/*  961:     */   }
/*  962:     */   
/*  963:     */   protected void findNearestNeighbours(Instance target, KDTreeNode node, int k, NearestNeighbourSearch.MyHeap heap, double distanceToParents)
/*  964:     */     throws Exception
/*  965:     */   {
/*  966: 308 */     if (node.isALeaf())
/*  967:     */     {
/*  968: 309 */       if (this.m_TreeStats != null)
/*  969:     */       {
/*  970: 310 */         this.m_TreeStats.updatePointCount(node.numInstances());
/*  971: 311 */         this.m_TreeStats.incrLeafCount();
/*  972:     */       }
/*  973: 315 */       for (int idx = node.m_Start; idx <= node.m_End; idx++) {
/*  974: 316 */         if (target != this.m_Instances.instance(this.m_InstList[idx])) {
/*  975: 320 */           if (heap.size() < k)
/*  976:     */           {
/*  977: 321 */             double distance = this.m_EuclideanDistance.distance(target, this.m_Instances.instance(this.m_InstList[idx]), (1.0D / 0.0D), this.m_Stats);
/*  978:     */             
/*  979: 323 */             heap.put(this.m_InstList[idx], distance);
/*  980:     */           }
/*  981:     */           else
/*  982:     */           {
/*  983: 325 */             NearestNeighbourSearch.MyHeapElement temp = heap.peek();
/*  984: 326 */             double distance = this.m_EuclideanDistance.distance(target, this.m_Instances.instance(this.m_InstList[idx]), temp.distance, this.m_Stats);
/*  985: 328 */             if (distance < temp.distance) {
/*  986: 329 */               heap.putBySubstitute(this.m_InstList[idx], distance);
/*  987: 330 */             } else if (distance == temp.distance) {
/*  988: 331 */               heap.putKthNearest(this.m_InstList[idx], distance);
/*  989:     */             }
/*  990:     */           }
/*  991:     */         }
/*  992:     */       }
/*  993:     */     }
/*  994:     */     else
/*  995:     */     {
/*  996: 337 */       if (this.m_TreeStats != null) {
/*  997: 338 */         this.m_TreeStats.incrIntNodeCount();
/*  998:     */       }
/*  999: 341 */       boolean targetInLeft = this.m_EuclideanDistance.valueIsSmallerEqual(target, node.m_SplitDim, node.m_SplitValue);
/* 1000:     */       KDTreeNode further;
/* 1001:     */       KDTreeNode nearer;
/* 1002:     */       KDTreeNode further;
/* 1003: 344 */       if (targetInLeft)
/* 1004:     */       {
/* 1005: 345 */         KDTreeNode nearer = node.m_Left;
/* 1006: 346 */         further = node.m_Right;
/* 1007:     */       }
/* 1008:     */       else
/* 1009:     */       {
/* 1010: 348 */         nearer = node.m_Right;
/* 1011: 349 */         further = node.m_Left;
/* 1012:     */       }
/* 1013: 351 */       findNearestNeighbours(target, nearer, k, heap, distanceToParents);
/* 1014: 354 */       if (heap.size() < k)
/* 1015:     */       {
/* 1016: 355 */         double distanceToSplitPlane = distanceToParents + this.m_EuclideanDistance.sqDifference(node.m_SplitDim, target.value(node.m_SplitDim), node.m_SplitValue);
/* 1017:     */         
/* 1018:     */ 
/* 1019: 358 */         findNearestNeighbours(target, further, k, heap, distanceToSplitPlane);
/* 1020: 359 */         return;
/* 1021:     */       }
/* 1022: 362 */       double distanceToSplitPlane = distanceToParents + this.m_EuclideanDistance.sqDifference(node.m_SplitDim, target.value(node.m_SplitDim), node.m_SplitValue);
/* 1023: 365 */       if (heap.peek().distance >= distanceToSplitPlane) {
/* 1024: 366 */         findNearestNeighbours(target, further, k, heap, distanceToSplitPlane);
/* 1025:     */       }
/* 1026:     */     }
/* 1027:     */   }
/* 1028:     */   
/* 1029:     */   public Instances kNearestNeighbours(Instance target, int k)
/* 1030:     */     throws Exception
/* 1031:     */   {
/* 1032: 384 */     checkMissing(target);
/* 1033: 386 */     if (this.m_Stats != null) {
/* 1034: 387 */       this.m_Stats.searchStart();
/* 1035:     */     }
/* 1036: 389 */     NearestNeighbourSearch.MyHeap heap = new NearestNeighbourSearch.MyHeap(this, k);
/* 1037: 390 */     findNearestNeighbours(target, this.m_Root, k, heap, 0.0D);
/* 1038: 392 */     if (this.m_Stats != null) {
/* 1039: 393 */       this.m_Stats.searchFinish();
/* 1040:     */     }
/* 1041: 395 */     Instances neighbours = new Instances(this.m_Instances, heap.size() + heap.noOfKthNearest());
/* 1042:     */     
/* 1043: 397 */     this.m_DistanceList = new double[heap.size() + heap.noOfKthNearest()];
/* 1044: 398 */     int[] indices = new int[heap.size() + heap.noOfKthNearest()];
/* 1045: 399 */     int i = indices.length - 1;
/* 1046: 401 */     while (heap.noOfKthNearest() > 0)
/* 1047:     */     {
/* 1048: 402 */       NearestNeighbourSearch.MyHeapElement h = heap.getKthNearest();
/* 1049: 403 */       indices[i] = h.index;
/* 1050: 404 */       this.m_DistanceList[i] = h.distance;
/* 1051: 405 */       i--;
/* 1052:     */     }
/* 1053: 407 */     while (heap.size() > 0)
/* 1054:     */     {
/* 1055: 408 */       NearestNeighbourSearch.MyHeapElement h = heap.get();
/* 1056: 409 */       indices[i] = h.index;
/* 1057: 410 */       this.m_DistanceList[i] = h.distance;
/* 1058: 411 */       i--;
/* 1059:     */     }
/* 1060: 413 */     this.m_DistanceFunction.postProcessDistances(this.m_DistanceList);
/* 1061: 415 */     for (int idx = 0; idx < indices.length; idx++) {
/* 1062: 416 */       neighbours.add(this.m_Instances.instance(indices[idx]));
/* 1063:     */     }
/* 1064: 419 */     return neighbours;
/* 1065:     */   }
/* 1066:     */   
/* 1067:     */   public Instance nearestNeighbour(Instance target)
/* 1068:     */     throws Exception
/* 1069:     */   {
/* 1070: 433 */     return kNearestNeighbours(target, 1).instance(0);
/* 1071:     */   }
/* 1072:     */   
/* 1073:     */   public double[] getDistances()
/* 1074:     */     throws Exception
/* 1075:     */   {
/* 1076: 448 */     if ((this.m_Instances == null) || (this.m_DistanceList == null)) {
/* 1077: 449 */       throw new Exception("The tree has not been supplied with a set of instances or getDistances() has been called before calling kNearestNeighbours().");
/* 1078:     */     }
/* 1079: 452 */     return this.m_DistanceList;
/* 1080:     */   }
/* 1081:     */   
/* 1082:     */   public void setInstances(Instances instances)
/* 1083:     */     throws Exception
/* 1084:     */   {
/* 1085: 464 */     super.setInstances(instances);
/* 1086: 465 */     buildKDTree(instances);
/* 1087:     */   }
/* 1088:     */   
/* 1089:     */   public void update(Instance instance)
/* 1090:     */     throws Exception
/* 1091:     */   {
/* 1092: 479 */     if (this.m_Instances == null) {
/* 1093: 480 */       throw new Exception("No instances supplied yet. Have to call setInstances(instances) with a set of Instances first.");
/* 1094:     */     }
/* 1095: 483 */     addInstanceInfo(instance);
/* 1096: 484 */     addInstanceToTree(instance, this.m_Root);
/* 1097:     */   }
/* 1098:     */   
/* 1099:     */   protected void addInstanceToTree(Instance inst, KDTreeNode node)
/* 1100:     */     throws Exception
/* 1101:     */   {
/* 1102: 503 */     if (node.isALeaf())
/* 1103:     */     {
/* 1104: 504 */       int[] instList = new int[this.m_Instances.numInstances()];
/* 1105:     */       try
/* 1106:     */       {
/* 1107: 506 */         System.arraycopy(this.m_InstList, 0, instList, 0, node.m_End + 1);
/* 1108: 508 */         if (node.m_End < this.m_InstList.length - 1) {
/* 1109: 509 */           System.arraycopy(this.m_InstList, node.m_End + 1, instList, node.m_End + 2, this.m_InstList.length - node.m_End - 1);
/* 1110:     */         }
/* 1111: 511 */         instList[(node.m_End + 1)] = (this.m_Instances.numInstances() - 1);
/* 1112:     */       }
/* 1113:     */       catch (ArrayIndexOutOfBoundsException ex)
/* 1114:     */       {
/* 1115: 513 */         System.err.println("m_InstList.length: " + this.m_InstList.length + " instList.length: " + instList.length + "node.m_End+1: " + (node.m_End + 1) + "m_InstList.length-node.m_End+1: " + (this.m_InstList.length - node.m_End - 1));
/* 1116:     */         
/* 1117:     */ 
/* 1118:     */ 
/* 1119: 517 */         throw ex;
/* 1120:     */       }
/* 1121: 519 */       this.m_InstList = instList;
/* 1122:     */       
/* 1123: 521 */       node.m_End += 1;
/* 1124: 522 */       node.m_NodeRanges = this.m_EuclideanDistance.updateRanges(inst, node.m_NodeRanges);
/* 1125:     */       
/* 1126:     */ 
/* 1127: 525 */       this.m_Splitter.setInstanceList(this.m_InstList);
/* 1128:     */       
/* 1129:     */ 
/* 1130: 528 */       double[][] universe = this.m_EuclideanDistance.getRanges();
/* 1131: 529 */       if ((node.numInstances() > this.m_MaxInstInLeaf) && (getMaxRelativeNodeWidth(node.m_NodeRanges, universe) > this.m_MinBoxRelWidth))
/* 1132:     */       {
/* 1133: 531 */         this.m_Splitter.splitNode(node, this.m_NumNodes, node.m_NodeRanges, universe);
/* 1134: 532 */         this.m_NumNodes += 2;
/* 1135:     */       }
/* 1136:     */     }
/* 1137:     */     else
/* 1138:     */     {
/* 1139: 536 */       if (this.m_EuclideanDistance.valueIsSmallerEqual(inst, node.m_SplitDim, node.m_SplitValue))
/* 1140:     */       {
/* 1141: 538 */         addInstanceToTree(inst, node.m_Left);
/* 1142: 539 */         afterAddInstance(node.m_Right);
/* 1143:     */       }
/* 1144:     */       else
/* 1145:     */       {
/* 1146: 541 */         addInstanceToTree(inst, node.m_Right);
/* 1147:     */       }
/* 1148: 543 */       node.m_End += 1;
/* 1149: 544 */       node.m_NodeRanges = this.m_EuclideanDistance.updateRanges(inst, node.m_NodeRanges);
/* 1150:     */     }
/* 1151:     */   }
/* 1152:     */   
/* 1153:     */   protected void afterAddInstance(KDTreeNode node)
/* 1154:     */   {
/* 1155: 565 */     node.m_Start += 1;
/* 1156: 566 */     node.m_End += 1;
/* 1157: 567 */     if (!node.isALeaf())
/* 1158:     */     {
/* 1159: 568 */       afterAddInstance(node.m_Left);
/* 1160: 569 */       afterAddInstance(node.m_Right);
/* 1161:     */     }
/* 1162:     */   }
/* 1163:     */   
/* 1164:     */   public void addInstanceInfo(Instance instance)
/* 1165:     */   {
/* 1166: 581 */     this.m_EuclideanDistance.updateRanges(instance);
/* 1167:     */   }
/* 1168:     */   
/* 1169:     */   protected void checkMissing(Instances instances)
/* 1170:     */     throws Exception
/* 1171:     */   {
/* 1172: 592 */     for (int i = 0; i < instances.numInstances(); i++)
/* 1173:     */     {
/* 1174: 593 */       Instance ins = instances.instance(i);
/* 1175: 594 */       for (int j = 0; j < ins.numValues(); j++) {
/* 1176: 595 */         if ((ins.index(j) != ins.classIndex()) && 
/* 1177: 596 */           (ins.isMissingSparse(j))) {
/* 1178: 597 */           throw new Exception("ERROR: KDTree can not deal with missing values. Please run ReplaceMissingValues filter on the dataset before passing it on to the KDTree.");
/* 1179:     */         }
/* 1180:     */       }
/* 1181:     */     }
/* 1182:     */   }
/* 1183:     */   
/* 1184:     */   protected void checkMissing(Instance ins)
/* 1185:     */     throws Exception
/* 1186:     */   {
/* 1187: 613 */     for (int j = 0; j < ins.numValues(); j++) {
/* 1188: 614 */       if ((ins.index(j) != ins.classIndex()) && 
/* 1189: 615 */         (ins.isMissingSparse(j))) {
/* 1190: 616 */         throw new Exception("ERROR: KDTree can not deal with missing values. Please run ReplaceMissingValues filter on the dataset before passing it on to the KDTree.");
/* 1191:     */       }
/* 1192:     */     }
/* 1193:     */   }
/* 1194:     */   
/* 1195:     */   protected double getMaxRelativeNodeWidth(double[][] nodeRanges, double[][] universe)
/* 1196:     */   {
/* 1197: 637 */     int widest = widestDim(nodeRanges, universe);
/* 1198: 638 */     if (widest < 0) {
/* 1199: 639 */       return 0.0D;
/* 1200:     */     }
/* 1201: 641 */     return nodeRanges[widest][2] / universe[widest][2];
/* 1202:     */   }
/* 1203:     */   
/* 1204:     */   protected int widestDim(double[][] nodeRanges, double[][] universe)
/* 1205:     */   {
/* 1206: 656 */     int classIdx = this.m_Instances.classIndex();
/* 1207: 657 */     double widest = 0.0D;
/* 1208: 658 */     int w = -1;
/* 1209: 659 */     if (this.m_NormalizeNodeWidth) {
/* 1210: 660 */       for (int i = 0; i < nodeRanges.length; i++)
/* 1211:     */       {
/* 1212: 661 */         double newWidest = nodeRanges[i][2] / universe[i][2];
/* 1213: 662 */         if ((newWidest > widest) && 
/* 1214: 663 */           (i != classIdx))
/* 1215:     */         {
/* 1216: 665 */           widest = newWidest;
/* 1217: 666 */           w = i;
/* 1218:     */         }
/* 1219:     */       }
/* 1220:     */     } else {
/* 1221: 670 */       for (int i = 0; i < nodeRanges.length; i++) {
/* 1222: 671 */         if ((nodeRanges[i][2] > widest) && 
/* 1223: 672 */           (i != classIdx))
/* 1224:     */         {
/* 1225: 674 */           widest = nodeRanges[i][2];
/* 1226: 675 */           w = i;
/* 1227:     */         }
/* 1228:     */       }
/* 1229:     */     }
/* 1230: 679 */     return w;
/* 1231:     */   }
/* 1232:     */   
/* 1233:     */   public double measureTreeSize()
/* 1234:     */   {
/* 1235: 688 */     return this.m_NumNodes;
/* 1236:     */   }
/* 1237:     */   
/* 1238:     */   public double measureNumLeaves()
/* 1239:     */   {
/* 1240: 697 */     return this.m_NumLeaves;
/* 1241:     */   }
/* 1242:     */   
/* 1243:     */   public double measureMaxDepth()
/* 1244:     */   {
/* 1245: 706 */     return this.m_MaxDepth;
/* 1246:     */   }
/* 1247:     */   
/* 1248:     */   public Enumeration<String> enumerateMeasures()
/* 1249:     */   {
/* 1250: 715 */     Vector<String> newVector = new Vector();
/* 1251: 716 */     newVector.addElement("measureTreeSize");
/* 1252: 717 */     newVector.addElement("measureNumLeaves");
/* 1253: 718 */     newVector.addElement("measureMaxDepth");
/* 1254: 719 */     if (this.m_Stats != null) {
/* 1255: 720 */       newVector.addAll(Collections.list(this.m_Stats.enumerateMeasures()));
/* 1256:     */     }
/* 1257: 722 */     return newVector.elements();
/* 1258:     */   }
/* 1259:     */   
/* 1260:     */   public double getMeasure(String additionalMeasureName)
/* 1261:     */   {
/* 1262: 735 */     if (additionalMeasureName.compareToIgnoreCase("measureMaxDepth") == 0) {
/* 1263: 736 */       return measureMaxDepth();
/* 1264:     */     }
/* 1265: 737 */     if (additionalMeasureName.compareToIgnoreCase("measureTreeSize") == 0) {
/* 1266: 738 */       return measureTreeSize();
/* 1267:     */     }
/* 1268: 739 */     if (additionalMeasureName.compareToIgnoreCase("measureNumLeaves") == 0) {
/* 1269: 740 */       return measureNumLeaves();
/* 1270:     */     }
/* 1271: 741 */     if (this.m_Stats != null) {
/* 1272: 742 */       return this.m_Stats.getMeasure(additionalMeasureName);
/* 1273:     */     }
/* 1274: 744 */     throw new IllegalArgumentException(additionalMeasureName + " not supported (KDTree)");
/* 1275:     */   }
/* 1276:     */   
/* 1277:     */   public void setMeasurePerformance(boolean measurePerformance)
/* 1278:     */   {
/* 1279: 755 */     this.m_MeasurePerformance = measurePerformance;
/* 1280: 756 */     if (this.m_MeasurePerformance)
/* 1281:     */     {
/* 1282: 757 */       if (this.m_Stats == null) {
/* 1283: 758 */         this.m_Stats = (this.m_TreeStats = new TreePerformanceStats());
/* 1284:     */       }
/* 1285:     */     }
/* 1286:     */     else {
/* 1287: 760 */       this.m_Stats = (this.m_TreeStats = null);
/* 1288:     */     }
/* 1289:     */   }
/* 1290:     */   
/* 1291:     */   public void centerInstances(Instances centers, int[] assignments, double pc)
/* 1292:     */     throws Exception
/* 1293:     */   {
/* 1294: 775 */     int[] centList = new int[centers.numInstances()];
/* 1295: 776 */     for (int i = 0; i < centers.numInstances(); i++) {
/* 1296: 777 */       centList[i] = i;
/* 1297:     */     }
/* 1298: 779 */     determineAssignments(this.m_Root, centers, centList, assignments, pc);
/* 1299:     */   }
/* 1300:     */   
/* 1301:     */   protected void determineAssignments(KDTreeNode node, Instances centers, int[] candidates, int[] assignments, double pc)
/* 1302:     */     throws Exception
/* 1303:     */   {
/* 1304: 797 */     int[] owners = refineOwners(node, centers, candidates);
/* 1305: 800 */     if (owners.length == 1)
/* 1306:     */     {
/* 1307: 802 */       for (int i = node.m_Start; i <= node.m_End; i++) {
/* 1308: 803 */         assignments[this.m_InstList[i]] = owners[0];
/* 1309:     */       }
/* 1310:     */     }
/* 1311: 806 */     else if (!node.isALeaf())
/* 1312:     */     {
/* 1313: 808 */       determineAssignments(node.m_Left, centers, owners, assignments, pc);
/* 1314: 809 */       determineAssignments(node.m_Right, centers, owners, assignments, pc);
/* 1315:     */     }
/* 1316:     */     else
/* 1317:     */     {
/* 1318: 813 */       assignSubToCenters(node, centers, owners, assignments);
/* 1319:     */     }
/* 1320:     */   }
/* 1321:     */   
/* 1322:     */   protected int[] refineOwners(KDTreeNode node, Instances centers, int[] candidates)
/* 1323:     */     throws Exception
/* 1324:     */   {
/* 1325: 829 */     int[] owners = new int[candidates.length];
/* 1326: 830 */     double minDistance = (1.0D / 0.0D);
/* 1327: 831 */     int ownerIndex = -1;
/* 1328:     */     
/* 1329: 833 */     int numCand = candidates.length;
/* 1330: 834 */     double[] distance = new double[numCand];
/* 1331: 835 */     boolean[] inside = new boolean[numCand];
/* 1332: 836 */     for (int i = 0; i < numCand; i++)
/* 1333:     */     {
/* 1334: 837 */       distance[i] = distanceToHrect(node, centers.instance(candidates[i]));
/* 1335: 838 */       inside[i] = (distance[i] == 0.0D ? 1 : false);
/* 1336: 839 */       if (distance[i] < minDistance)
/* 1337:     */       {
/* 1338: 840 */         minDistance = distance[i];
/* 1339: 841 */         ownerIndex = i;
/* 1340:     */       }
/* 1341:     */     }
/* 1342: 844 */     Instance owner = (Instance)centers.instance(candidates[ownerIndex]).copy();
/* 1343:     */     
/* 1344:     */ 
/* 1345:     */ 
/* 1346:     */ 
/* 1347: 849 */     int index = 0;
/* 1348: 850 */     for (int i = 0; i < numCand; i++) {
/* 1349: 852 */       if ((inside[i] != 0) || (distance[i] == distance[ownerIndex]))
/* 1350:     */       {
/* 1351: 858 */         owners[(index++)] = candidates[i];
/* 1352:     */       }
/* 1353:     */       else
/* 1354:     */       {
/* 1355: 861 */         Instance competitor = (Instance)centers.instance(candidates[i]).copy();
/* 1356: 862 */         if (!candidateIsFullOwner(node, owner, competitor)) {
/* 1357: 870 */           owners[(index++)] = candidates[i];
/* 1358:     */         }
/* 1359:     */       }
/* 1360:     */     }
/* 1361: 874 */     int[] result = new int[index];
/* 1362: 875 */     for (int i = 0; i < index; i++) {
/* 1363: 876 */       result[i] = owners[i];
/* 1364:     */     }
/* 1365: 877 */     return result;
/* 1366:     */   }
/* 1367:     */   
/* 1368:     */   protected double distanceToHrect(KDTreeNode node, Instance x)
/* 1369:     */     throws Exception
/* 1370:     */   {
/* 1371: 891 */     double distance = 0.0D;
/* 1372:     */     
/* 1373: 893 */     Instance closestPoint = (Instance)x.copy();
/* 1374:     */     
/* 1375: 895 */     boolean inside = clipToInsideHrect(node, closestPoint);
/* 1376: 896 */     if (!inside) {
/* 1377: 897 */       distance = this.m_EuclideanDistance.distance(closestPoint, x);
/* 1378:     */     }
/* 1379: 898 */     return distance;
/* 1380:     */   }
/* 1381:     */   
/* 1382:     */   protected boolean clipToInsideHrect(KDTreeNode node, Instance x)
/* 1383:     */   {
/* 1384: 914 */     boolean inside = true;
/* 1385: 915 */     for (int i = 0; i < this.m_Instances.numAttributes(); i++) {
/* 1386: 918 */       if (x.value(i) < node.m_NodeRanges[i][0])
/* 1387:     */       {
/* 1388: 919 */         x.setValue(i, node.m_NodeRanges[i][0]);
/* 1389: 920 */         inside = false;
/* 1390:     */       }
/* 1391: 921 */       else if (x.value(i) > node.m_NodeRanges[i][1])
/* 1392:     */       {
/* 1393: 922 */         x.setValue(i, node.m_NodeRanges[i][1]);
/* 1394: 923 */         inside = false;
/* 1395:     */       }
/* 1396:     */     }
/* 1397: 926 */     return inside;
/* 1398:     */   }
/* 1399:     */   
/* 1400:     */   protected boolean candidateIsFullOwner(KDTreeNode node, Instance candidate, Instance competitor)
/* 1401:     */     throws Exception
/* 1402:     */   {
/* 1403: 957 */     double[] extreme = new double[this.m_Instances.numAttributes()];
/* 1404: 958 */     for (int i = 0; i < this.m_Instances.numAttributes(); i++) {
/* 1405: 959 */       if (competitor.value(i) - candidate.value(i) > 0.0D) {
/* 1406: 960 */         extreme[i] = node.m_NodeRanges[i][1];
/* 1407:     */       } else {
/* 1408: 962 */         extreme[i] = node.m_NodeRanges[i][0];
/* 1409:     */       }
/* 1410:     */     }
/* 1411: 965 */     Instance extremeI = candidate.copy(extreme);
/* 1412: 966 */     boolean isFullOwner = this.m_EuclideanDistance.distance(extremeI, candidate) < this.m_EuclideanDistance.distance(extremeI, competitor);
/* 1413:     */     
/* 1414:     */ 
/* 1415: 969 */     return isFullOwner;
/* 1416:     */   }
/* 1417:     */   
/* 1418:     */   public void assignSubToCenters(KDTreeNode node, Instances centers, int[] centList, int[] assignments)
/* 1419:     */     throws Exception
/* 1420:     */   {
/* 1421: 988 */     if (assignments == null)
/* 1422:     */     {
/* 1423: 989 */       assignments = new int[this.m_Instances.numInstances()];
/* 1424: 990 */       for (int i = 0; i < assignments.length; i++) {
/* 1425: 991 */         assignments[i] = -1;
/* 1426:     */       }
/* 1427:     */     }
/* 1428: 996 */     for (int i = node.m_Start; i <= node.m_End; i++)
/* 1429:     */     {
/* 1430: 997 */       int instIndex = this.m_InstList[i];
/* 1431: 998 */       Instance inst = this.m_Instances.instance(instIndex);
/* 1432:     */       
/* 1433:1000 */       int newC = this.m_EuclideanDistance.closestPoint(inst, centers, centList);
/* 1434:     */       
/* 1435:1002 */       assignments[instIndex] = newC;
/* 1436:     */     }
/* 1437:     */   }
/* 1438:     */   
/* 1439:     */   public String minBoxRelWidthTipText()
/* 1440:     */   {
/* 1441:1038 */     return "The minimum relative width of the box. A node is only made a leaf if the width of the split dimension of the instances in a node normalized over the width of the split dimension of all the instances is less than or equal to this minimum relative width.";
/* 1442:     */   }
/* 1443:     */   
/* 1444:     */   public void setMinBoxRelWidth(double i)
/* 1445:     */   {
/* 1446:1050 */     this.m_MinBoxRelWidth = i;
/* 1447:     */   }
/* 1448:     */   
/* 1449:     */   public double getMinBoxRelWidth()
/* 1450:     */   {
/* 1451:1059 */     return this.m_MinBoxRelWidth;
/* 1452:     */   }
/* 1453:     */   
/* 1454:     */   public String maxInstInLeafTipText()
/* 1455:     */   {
/* 1456:1068 */     return "The max number of instances in a leaf.";
/* 1457:     */   }
/* 1458:     */   
/* 1459:     */   public void setMaxInstInLeaf(int i)
/* 1460:     */   {
/* 1461:1077 */     this.m_MaxInstInLeaf = i;
/* 1462:     */   }
/* 1463:     */   
/* 1464:     */   public int getMaxInstInLeaf()
/* 1465:     */   {
/* 1466:1086 */     return this.m_MaxInstInLeaf;
/* 1467:     */   }
/* 1468:     */   
/* 1469:     */   public String normalizeNodeWidthTipText()
/* 1470:     */   {
/* 1471:1095 */     return "Whether if the widths of the KDTree node should be normalized by the width of the universe or not. Where, width of the node is the range of the split attribute based on the instances in that node, and width of the universe is the range of the split attribute based on all the instances (default: false).";
/* 1472:     */   }
/* 1473:     */   
/* 1474:     */   public void setNormalizeNodeWidth(boolean n)
/* 1475:     */   {
/* 1476:1110 */     this.m_NormalizeNodeWidth = n;
/* 1477:     */   }
/* 1478:     */   
/* 1479:     */   public boolean getNormalizeNodeWidth()
/* 1480:     */   {
/* 1481:1119 */     return this.m_NormalizeNodeWidth;
/* 1482:     */   }
/* 1483:     */   
/* 1484:     */   public DistanceFunction getDistanceFunction()
/* 1485:     */   {
/* 1486:1128 */     return this.m_EuclideanDistance;
/* 1487:     */   }
/* 1488:     */   
/* 1489:     */   public void setDistanceFunction(DistanceFunction df)
/* 1490:     */     throws Exception
/* 1491:     */   {
/* 1492:1138 */     if (!(df instanceof EuclideanDistance)) {
/* 1493:1139 */       throw new Exception("KDTree currently only works with EuclideanDistanceFunction.");
/* 1494:     */     }
/* 1495:1141 */     this.m_DistanceFunction = (this.m_EuclideanDistance = (EuclideanDistance)df);
/* 1496:     */   }
/* 1497:     */   
/* 1498:     */   public String nodeSplitterTipText()
/* 1499:     */   {
/* 1500:1151 */     return "The the splitting method to split the nodes of the KDTree.";
/* 1501:     */   }
/* 1502:     */   
/* 1503:     */   public KDTreeNodeSplitter getNodeSplitter()
/* 1504:     */   {
/* 1505:1161 */     return this.m_Splitter;
/* 1506:     */   }
/* 1507:     */   
/* 1508:     */   public void setNodeSplitter(KDTreeNodeSplitter splitter)
/* 1509:     */   {
/* 1510:1170 */     this.m_Splitter = splitter;
/* 1511:     */   }
/* 1512:     */   
/* 1513:     */   public String globalInfo()
/* 1514:     */   {
/* 1515:1180 */     return "Class implementing the KDTree search algorithm for nearest neighbour search.\nThe connection to dataset is only a reference. For the tree structure the indexes are stored in an array. \nBuilding the tree:\nIf a node has <maximal-inst-number> (option -L) instances no further splitting is done. Also if the split would leave one side empty, the branch is not split any further even if the instances in the resulting node are more than <maximal-inst-number> instances.\n**PLEASE NOTE:** The algorithm can not handle missing values, so it is advisable to run ReplaceMissingValues filter if there are any missing values in the dataset.\n\nFor more information see:\n\n" + getTechnicalInformation().toString();
/* 1516:     */   }
/* 1517:     */   
/* 1518:     */   public Enumeration<Option> listOptions()
/* 1519:     */   {
/* 1520:1204 */     Vector<Option> newVector = new Vector();
/* 1521:     */     
/* 1522:1206 */     newVector.add(new Option("\tNode splitting method to use.\n\t(default: weka.core.neighboursearch.kdtrees.SlidingMidPointOfWidestSide)", "S", 1, "-S <classname and options>"));
/* 1523:     */     
/* 1524:     */ 
/* 1525:     */ 
/* 1526:     */ 
/* 1527:1211 */     newVector.addElement(new Option("\tSet minimal width of a box\n\t(default: 1.0E-2).", "W", 0, "-W <value>"));
/* 1528:     */     
/* 1529:     */ 
/* 1530:     */ 
/* 1531:     */ 
/* 1532:1216 */     newVector.addElement(new Option("\tMaximal number of instances in a leaf\n\t(default: 40).", "L", 0, "-L"));
/* 1533:     */     
/* 1534:     */ 
/* 1535:     */ 
/* 1536:     */ 
/* 1537:1221 */     newVector.addElement(new Option("\tNormalizing will be done\n\t(Select dimension for split, with normalising to universe).", "N", 0, "-N"));
/* 1538:     */     
/* 1539:     */ 
/* 1540:     */ 
/* 1541:     */ 
/* 1542:1226 */     newVector.addAll(Collections.list(super.listOptions()));
/* 1543:     */     
/* 1544:1228 */     return newVector.elements();
/* 1545:     */   }
/* 1546:     */   
/* 1547:     */   public void setOptions(String[] options)
/* 1548:     */     throws Exception
/* 1549:     */   {
/* 1550:1259 */     super.setOptions(options);
/* 1551:     */     
/* 1552:1261 */     String optionString = Utils.getOption('S', options);
/* 1553:1262 */     if (optionString.length() != 0)
/* 1554:     */     {
/* 1555:1263 */       String[] splitMethodSpec = Utils.splitOptions(optionString);
/* 1556:1264 */       if (splitMethodSpec.length == 0) {
/* 1557:1265 */         throw new Exception("Invalid DistanceFunction specification string.");
/* 1558:     */       }
/* 1559:1267 */       String className = splitMethodSpec[0];
/* 1560:1268 */       splitMethodSpec[0] = "";
/* 1561:     */       
/* 1562:1270 */       setNodeSplitter((KDTreeNodeSplitter)Utils.forName(KDTreeNodeSplitter.class, className, splitMethodSpec));
/* 1563:     */     }
/* 1564:     */     else
/* 1565:     */     {
/* 1566:1274 */       setNodeSplitter(new SlidingMidPointOfWidestSide());
/* 1567:     */     }
/* 1568:1277 */     optionString = Utils.getOption('W', options);
/* 1569:1278 */     if (optionString.length() != 0) {
/* 1570:1279 */       setMinBoxRelWidth(Double.parseDouble(optionString));
/* 1571:     */     } else {
/* 1572:1281 */       setMinBoxRelWidth(0.01D);
/* 1573:     */     }
/* 1574:1283 */     optionString = Utils.getOption('L', options);
/* 1575:1284 */     if (optionString.length() != 0) {
/* 1576:1285 */       setMaxInstInLeaf(Integer.parseInt(optionString));
/* 1577:     */     } else {
/* 1578:1287 */       setMaxInstInLeaf(40);
/* 1579:     */     }
/* 1580:1289 */     setNormalizeNodeWidth(Utils.getFlag('N', options));
/* 1581:     */     
/* 1582:1291 */     Utils.checkForRemainingOptions(options);
/* 1583:     */   }
/* 1584:     */   
/* 1585:     */   public String[] getOptions()
/* 1586:     */   {
/* 1587:1304 */     Vector<String> result = new Vector();
/* 1588:     */     
/* 1589:1306 */     String[] options = super.getOptions();
/* 1590:1307 */     for (int i = 0; i < options.length; i++) {
/* 1591:1308 */       result.add(options[i]);
/* 1592:     */     }
/* 1593:1310 */     result.add("-S");
/* 1594:1311 */     result.add((this.m_Splitter.getClass().getName() + " " + Utils.joinOptions(this.m_Splitter.getOptions())).trim());
/* 1595:     */     
/* 1596:     */ 
/* 1597:     */ 
/* 1598:1315 */     result.add("-W");
/* 1599:1316 */     result.add("" + getMinBoxRelWidth());
/* 1600:     */     
/* 1601:1318 */     result.add("-L");
/* 1602:1319 */     result.add("" + getMaxInstInLeaf());
/* 1603:1321 */     if (getNormalizeNodeWidth()) {
/* 1604:1322 */       result.add("-N");
/* 1605:     */     }
/* 1606:1324 */     return (String[])result.toArray(new String[result.size()]);
/* 1607:     */   }
/* 1608:     */   
/* 1609:     */   public String getRevision()
/* 1610:     */   {
/* 1611:1333 */     return RevisionUtils.extract("$Revision: 12479 $");
/* 1612:     */   }
/* 1613:     */ }


/* Location:           C:\Users\Administrator\Desktop\weka-3-9-0-monolithic.jar
 * Qualified Name:     weka.core.neighboursearch.KDTree
 * JD-Core Version:    0.7.0.1
 */